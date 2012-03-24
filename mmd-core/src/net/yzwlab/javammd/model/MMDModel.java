package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.MMD_VERTEX_DESC;
import net.yzwlab.javammd.format.PMDFile;
import net.yzwlab.javammd.format.PMD_BONE_RECORD;
import net.yzwlab.javammd.format.PMD_IK_RECORD;
import net.yzwlab.javammd.format.PMD_MATERIAL_RECORD;
import net.yzwlab.javammd.format.PMD_MORP_RECORD;
import net.yzwlab.javammd.format.PMD_VERTEX_RECORD;
import net.yzwlab.javammd.format.VMDFile;
import net.yzwlab.javammd.format.VMD_MORP_RECORD;
import net.yzwlab.javammd.format.VMD_MOTION_RECORD;

/**
 * MMDのモデルを表現します。
 */
public class MMDModel {

	protected float m_scale;

	protected MMDVertexList m_pVertexList;

	protected List<MMDBone> m_bones;

	protected List<MMDMaterial> m_materials;

	protected List<MMDMorp> m_morps;

	protected List<MMDIK> m_iks;

	/**
	 * 構築します。
	 */
	public MMDModel() {
		this.m_iks = new ArrayList<MMDIK>();
		this.m_morps = new ArrayList<MMDMorp>();
		this.m_materials = new ArrayList<MMDMaterial>();
		this.m_bones = new ArrayList<MMDBone>();
		this.m_pVertexList = null;
		this.m_scale = 0.0f;
		m_pVertexList = null;
		m_scale = 1.0f;
	}

	/**
	 * PMDファイルを読み出します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @param pmdFile
	 *            PMDファイル。nullは不可。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public void setPMD(IReadBuffer buffer, PMDFile pmdFile)
			throws ReadException {
		if (buffer == null || pmdFile == null) {
			throw new IllegalArgumentException();
		}
		PMD_MORP_RECORD baseMorp = new PMD_MORP_RECORD();
		PMD_BONE_RECORD bone = new PMD_BONE_RECORD();
		List<PMD_BONE_RECORD> bones = new ArrayList<PMD_BONE_RECORD>();
		PMD_IK_RECORD ik = new PMD_IK_RECORD();
		List<PMD_IK_RECORD> iks = new ArrayList<PMD_IK_RECORD>();
		List<Short> indices = new ArrayList<Short>();
		PMD_MATERIAL_RECORD material = new PMD_MATERIAL_RECORD();
		List<PMD_MATERIAL_RECORD> materials = new ArrayList<PMD_MATERIAL_RECORD>();
		PMD_MORP_RECORD morp = new PMD_MORP_RECORD();
		List<PMD_MORP_RECORD> morps = new ArrayList<PMD_MORP_RECORD>();
		int offset = 0;
		List<PMD_VERTEX_RECORD> vertexes = new ArrayList<PMD_VERTEX_RECORD>();
		if (m_pVertexList != null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		vertexes = pmdFile.GetVertexChunk();
		indices = pmdFile.GetIndexChunk();
		m_pVertexList = new MMDVertexList(vertexes, indices);
		bones = pmdFile.GetBoneChunk();
		for (int i = 0; i < bones.size(); i++) {
			bone = bones.get(i);
			m_bones.add(new MMDBone(bone));
		}
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).Init(m_bones);
		}
		morps = pmdFile.GetMorpChunk();
		for (int i = 0; i < morps.size(); i++) {
			morp = morps.get(i);
			if (i == 0) {
				m_morps.add(new MMDMorp(morp, null));
			} else {
				baseMorp = morps.get(0);
				m_morps.add(new MMDMorp(morp, baseMorp));
			}
		}
		iks = pmdFile.GetIKChunk();
		for (int i = 0; i < iks.size(); i++) {
			ik = iks.get(i);
			m_iks.add(new MMDIK(ik));
		}
		Collections.sort(m_iks, new Comparator<MMDIK>() {
			@Override
			public int compare(MMDIK o1, MMDIK o2) {
				return MMDIK.Compare(o1, o2);
			}
		});
		for (int i = 0; i < m_iks.size(); i++) {
			m_iks.get(i).Init(m_bones);
		}
		materials = pmdFile.GetMaterialChunk();
		for (int i = 0; i < materials.size(); i++) {
			material = materials.get(i);
			m_materials.add(new MMDMaterial(material));
		}
		offset = 0;
		for (int i = 0; i < m_materials.size(); i++) {
			offset = m_materials.get(i).init(m_pVertexList, m_bones, offset);
		}
		m_pVertexList.verify();
	}

	/**
	 * PMDファイルを読み出します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public void openPMD(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		PMDFile pmdFile = new PMDFile();
		boolean br = pmdFile.open(buffer);
		if (br == false) {
			throw new IllegalArgumentException();
		}
		setPMD(buffer, pmdFile);
	}

	/**
	 * VMDファイルを読み出します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return モーションが登録された時間区分。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public IMotionSegment setVMD(IReadBuffer buffer, VMDFile vmdFile)
			throws ReadException {
		if (buffer == null || vmdFile == null) {
			throw new IllegalArgumentException();
		}
		boolean added = false;
		boolean br = false;
		VMD_MORP_RECORD morp = null;
		List<VMD_MORP_RECORD> morps = new ArrayList<VMD_MORP_RECORD>();
		VMD_MOTION_RECORD motion = null;
		List<VMD_MOTION_RECORD> motions = new ArrayList<VMD_MOTION_RECORD>();
		MMDBone pBone = null;
		MMDMorp pMorp = null;

		// モーションを追加するオフセット値
		int offset = 0;
		Integer maxFrameNum = getMaxFrame();
		if (maxFrameNum != null) {
			offset = maxFrameNum.intValue() + 1;
		}

		motions = vmdFile.GetMotionChunk();
		for (int j = 0; j < motions.size(); j++) {
			motion = motions.get(j);
			added = false;
			for (int i = 0; i < m_bones.size(); i++) {
				pBone = m_bones.get(i);
				br = pBone.IsTarget(motion);
				if (br) {
					pBone.addMotion(buffer, offset, motion);
					added = true;
					break;
				}
			}
			if (added == false) {
			}
		}
		for (int i = 0; i < m_bones.size(); i++) {
			pBone = m_bones.get(i);
			pBone.PrepareMotion();
		}
		morps = vmdFile.GetMorpChunk();
		for (int j = 0; j < morps.size(); j++) {
			morp = morps.get(j);
			added = false;
			for (int i = 0; i < m_morps.size(); i++) {
				pMorp = m_morps.get(i);
				br = pMorp.IsTarget(morp);
				if (br) {
					pMorp.addMotion(offset, morp);
					added = true;
					break;
				}
			}
			if (added == false) {
			}
		}
		for (int i = 0; i < m_morps.size(); i++) {
			pMorp = m_morps.get(i);
			pMorp.PrepareMotion();
		}
		return new MotionSegment(offset);
	}

	/**
	 * VMDファイルを読み出します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return モーションが登録された時間区分。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public IMotionSegment openVMD(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		VMDFile vmdFile = new VMDFile();
		boolean br = vmdFile.open(buffer);
		if (br == false) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		return setVMD(buffer, vmdFile);
	}

	/**
	 * テクスチャの準備を行います。
	 * 
	 * @param pTextureProvider
	 *            テクスチャプロバイダ。nullは不可。
	 * @param handler
	 *            ハンドラ。nullは不可。
	 * @throws ReadException
	 *             読み込み処理時のエラー。
	 */
	public void prepare(IMMDTextureProvider pTextureProvider,
			IMMDTextureProvider.Handler handler) throws ReadException {
		if (pTextureProvider == null || handler == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).prepare(pTextureProvider, handler);
		}
	}

	/**
	 * 拡大率を設定します。
	 * 
	 * @param scale
	 *            拡大率。
	 */
	public void setScale(float scale) {
		m_scale = scale;
		return;
	}

	/**
	 * 拡大率を取得します。
	 * 
	 * @return 拡大率。
	 */
	public float getScale() {
		return m_scale;
	}

	/**
	 * モーション情報を消去します。
	 */
	public void clearMotion() {
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).ClearMotion();
		}
		for (int i = 0; i < m_morps.size(); i++) {
			m_morps.get(i).ClearMotion();
		}
		return;
	}

	/**
	 * 更新処理を非同期的に行います。
	 * 
	 * @param pMutex
	 *            同期用オブジェクト。近いうちに廃止予定。
	 * @param frameNo
	 *            フレーム番号。
	 */
	public void updateAsync(IDataMutex pMutex, float frameNo) {
		if (m_pVertexList == null || pMutex == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		updateMotion(frameNo);
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).updateSkinning();
		}
		pMutex.Begin();
		m_pVertexList.updateSkinning();
		pMutex.End();
	}

	/**
	 * 描画処理を実行します。
	 * 
	 * @param gl
	 *            描画対象のプラットフォーム。nullは不可。
	 */
	public void draw(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		updateVertexBuffer();
		gl.glPushMatrix();
		gl.glScalef(m_scale, m_scale, m_scale * -1.0f);
		boolean normalizeEnabled = gl.glIsEnabled(IGL.C.GL_NORMALIZE);
		gl.glEnable(IGL.C.GL_NORMALIZE);
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).draw(gl);
		}
		if (normalizeEnabled == false) {
			gl.glDisable(IGL.C.GL_NORMALIZE);
		}
		gl.glPopMatrix();
	}

	/**
	 * Faceを設定します。
	 * 
	 * @param faceName
	 *            Face名。nullは不可。
	 */
	public void setFace(byte[] faceName) {
		if (faceName == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		byte[] elemName = null;
		byte[] name = null;
		MMDMorp pElem = null;
		MMDMorp pSelectedElem = null;
		MMD_VERTEX_DESC[] ppOriginalDescs = null;
		name = faceName;
		pSelectedElem = null;
		for (int i = 0; i < m_morps.size(); i++) {
			pElem = m_morps.get(i);
			elemName = pElem.GetName();
			if (Arrays.equals(elemName, name)) {
				pSelectedElem = pElem;
				break;
			}
		}
		if (pSelectedElem == null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		ppOriginalDescs = m_pVertexList.GetVertexDescs();
		pSelectedElem.Set(ppOriginalDescs);
	}

	/**
	 * Face数を取得します。
	 * 
	 * @return Face数。
	 */
	public int getFaceCount() {
		return m_morps.size();
	}

	/**
	 * Face名を取得します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return Face名。
	 */
	public byte[] getFaceName(int index) {
		if (index >= m_morps.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_morps.get(index).GetName();
	}

	/**
	 * ボーン数を取得します。
	 * 
	 * @return ボーン。
	 */
	public int getBoneCount() {
		Integer pCount = 0;
		pCount = m_bones.size();
		return pCount;
	}

	/**
	 * ボーンを取得します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return ボーン。
	 */
	public IMMDBone getBone(int index) {
		if (index >= m_bones.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return new BoneAccessor(m_bones.get(index));
	}

	/**
	 * マテリアル数を取得します。
	 * 
	 * @return マテリアル数。
	 */
	public int getMaterialCount() {
		return m_materials.size();
	}

	/**
	 * マテリアルを取得します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return マテリアル。
	 */
	public IMMDMaterial getMaterial(int index) {
		if (index >= m_materials.size()) {
			throw new IllegalArgumentException();
		}
		return new MaterialAccessor(m_materials.get(index));
	}

	/**
	 * IK数を取得します。
	 * 
	 * @return IK数。
	 */
	public int getIKCount() {
		return m_iks.size();
	}

	/**
	 * IK名を取得します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return IK名。
	 */
	public byte[] getIKTargetName(int index) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_iks.get(index).GetTargetName();
	}

	/**
	 * IKが有効かどうかを判定します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return IKが有効であればtrue。
	 */
	public boolean isIKEnabled(int index) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_iks.get(index).IsEnabled();
	}

	/**
	 * IKが有効かどうかを設定します。
	 * 
	 * @param index
	 *            インデックス。
	 * @param value
	 *            IKが有効であればtrue。
	 */
	public void setIKEnabled(int index, boolean value) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		m_iks.get(index).SetEnabled(value);
	}

	/**
	 * ボーンを表示するかどうかを設定します。
	 * 
	 * @param index
	 *            インデックス。
	 * @param visible
	 *            ボーンを表示する場合はtrue。
	 */
	public void setBoneVisible(int index, boolean visible) {
		boolean curVisible = false;
		MMDBone pBone = null;
		MMDMaterial pElem = null;
		if (index >= m_bones.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		pBone = m_bones.get(index);
		curVisible = pBone.IsVisible();
		if (curVisible == visible) {
			return;
		}
		pBone.SetVisible(visible);
		for (int i = 0; i < m_materials.size(); i++) {
			pElem = m_materials.get(i);
			pElem.UpdateVisibility();
		}
	}

	/**
	 * ボーンを表示するかどうかを判定します。
	 * 
	 * @param index
	 *            インデックス。
	 * @return ボーンを表示する場合はtrue。
	 */
	public boolean isBoneVisible(int index) {
		if (index >= m_bones.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_bones.get(index).IsVisible();
	}

	/**
	 * 最大フレーム数を取得します。
	 * 
	 * @return 最大フレーム数。
	 */
	public Integer getMaxFrame() {
		int ret = 0;
		int validCount = 0;
		ret = 0;
		validCount = 0;
		for (int i = 0; i < m_morps.size(); i++) {
			Integer f = m_morps.get(i).GetMaxFrame();
			if (f == null) {
				continue;
			}
			validCount++;
			if (f > ret) {
				ret = f;
			}
		}
		for (int i = 0; i < m_bones.size(); i++) {
			Integer f = m_bones.get(i).getMaxFrame();
			if (f == null) {
				continue;
			}
			validCount++;
			if (f > ret) {
				ret = f;
			}
		}
		if (validCount == 0) {
			return null;
		}
		return ret;
	}

	/**
	 * 頂点情報をリセットします。
	 */
	public void resetVertexes() {
		if (m_pVertexList == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_pVertexList.ResetVertexes();
	}

	/**
	 * スキニング情報を更新します。
	 */
	public void updateSkinning() {
		if (m_pVertexList == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).updateSkinning();
		}
		m_pVertexList.updateSkinning();
	}

	/**
	 * 頂点バッファを更新します。
	 */
	public void updateVertexBuffer() {
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).updateVertexBuffer();
		}
	}

	/**
	 * モーションを更新します。
	 * 
	 * @param elapsedFrame
	 *            経過フレーム数。
	 * @return 更新に成功した場合はtrue。
	 */
	public boolean updateMotion(float elapsedFrame) {
		if (m_pVertexList == null) {
			return false;
		}
		MMD_VERTEX_DESC[] ppOriginalDescs = m_pVertexList.GetVertexDescs();
		if (m_morps.size() > 0) {
			m_morps.get(0).Set(ppOriginalDescs);
		}
		for (int i = 0; i < m_morps.size(); i++) {
			m_morps.get(i).ApplyMotion(elapsedFrame, ppOriginalDescs);
		}
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).UpdateMotion(elapsedFrame);
		}
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).updateMatrix();
		}
		for (int i = 0; i < m_iks.size(); i++) {
			m_iks.get(i).update();
		}
		return true;
	}

	/**
	 * ボーンのアクセサを定義します。
	 */
	private class BoneAccessor implements IMMDBone {

		/**
		 * ボーンを保持します。
		 */
		private MMDBone bone;

		/**
		 * 構築します。
		 * 
		 * @param bone
		 *            ボーン。nullは不可。
		 */
		public BoneAccessor(MMDBone bone) {
			if (bone == null) {
				throw new IllegalArgumentException();
			}
			this.bone = bone;
		}

		@Override
		public byte[] getName() {
			return bone.GetName();
		}

	}

	/**
	 * マテリアルを実装します。
	 */
	private class MaterialAccessor implements IMMDMaterial {

		/**
		 * マテリアルを保持します。
		 */
		private MMDMaterial material;

		/**
		 * 構築します。
		 * 
		 * @param material
		 *            マテリアル。nullは不可。
		 */
		public MaterialAccessor(MMDMaterial material) {
			if (material == null) {
				throw new IllegalArgumentException();
			}
			this.material = material;
		}

		@Override
		public int getVertexCount() {
			return material.m_pVertexes.length;
		}

	}

	/**
	 * モーションの区分情報を定義します。
	 */
	private class MotionSegment implements IMotionSegment {

		/**
		 * オフセットを保持します。
		 */
		private int offset;

		/**
		 * 終了点を保持します。
		 */
		private int end;

		/**
		 * 構築します。
		 * 
		 * @param offset
		 *            オフセット。
		 */
		public MotionSegment(int offset) {
			this.offset = offset;
			this.end = 0;

			Integer maxFrame = getMaxFrame();
			if (maxFrame != null) {
				this.end = maxFrame.intValue();
			}
		}

		@Override
		public int getStart() {
			return offset;
		}

		@Override
		public int getStop() {
			return end;
		}

		@Override
		public float getFrame(float frameRate, long currentTime) {
			float fcurrentTime = ((float) currentTime) / 1000.0f;

			int len = end - offset + 1;
			int currentFrame = ((int) (fcurrentTime * frameRate));
			int relativeFrame = currentFrame % len;
			return relativeFrame + offset;
		}

	}

}
