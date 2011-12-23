package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadBuffer;
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

public class MMDModel {
	protected float m_scale;

	protected MMDVertexList m_pVertexList;

	protected List<MMDBone> m_bones;

	protected List<MMDMaterial> m_materials;

	protected List<MMDMorp> m_morps;

	protected List<MMDIK> m_iks;

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

	public void OpenPMD(ReadBuffer buffer) throws ReadException {
		PMD_MORP_RECORD baseMorp = new PMD_MORP_RECORD();
		PMD_BONE_RECORD bone = new PMD_BONE_RECORD();
		List<PMD_BONE_RECORD> bones = new ArrayList<PMD_BONE_RECORD>();
		boolean br = false;
		PMD_IK_RECORD ik = new PMD_IK_RECORD();
		List<PMD_IK_RECORD> iks = new ArrayList<PMD_IK_RECORD>();
		List<Short> indices = new ArrayList<Short>();
		PMD_MATERIAL_RECORD material = new PMD_MATERIAL_RECORD();
		List<PMD_MATERIAL_RECORD> materials = new ArrayList<PMD_MATERIAL_RECORD>();
		PMD_MORP_RECORD morp = new PMD_MORP_RECORD();
		List<PMD_MORP_RECORD> morps = new ArrayList<PMD_MORP_RECORD>();
		int offset = 0;
		PMDFile pmdFile = new PMDFile();
		List<PMD_VERTEX_RECORD> vertexes = new ArrayList<PMD_VERTEX_RECORD>();
		if (buffer == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		if (m_pVertexList != null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		br = pmdFile.Open(buffer);
		if (br == false) {
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
			offset = m_materials.get(i).Init(m_pVertexList, m_bones, offset);
		}
		return;
	}

	public void OpenVMD(ReadBuffer buffer) throws ReadException {
		boolean added = false;
		boolean br = false;
		VMD_MORP_RECORD morp = null;
		List<VMD_MORP_RECORD> morps = new ArrayList<VMD_MORP_RECORD>();
		VMD_MOTION_RECORD motion = null;
		String motionName = null;
		List<String> motionNames = new ArrayList<String>();
		List<VMD_MOTION_RECORD> motions = new ArrayList<VMD_MOTION_RECORD>();
		MMDBone pBone = null;
		MMDMorp pMorp = null;
		VMDFile vmdFile = new VMDFile();
		if (buffer == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		br = vmdFile.Open(buffer);
		if (br == false) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		motions = vmdFile.GetMotionChunk();
		for (int j = 0; j < motions.size(); j++) {
			motion = motions.get(j);
			motionName = DataUtils.getString(motion.getName());
			motionNames.add(motionName);
			added = false;
			for (int i = 0; i < m_bones.size(); i++) {
				pBone = m_bones.get(i);
				br = pBone.IsTarget(motion);
				if (br) {
					pBone.AddMotion(buffer, motion);
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
					pMorp.AddMotion(morp);
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
		return;
	}

	public void Prepare(IMMDTextureProvider pTextureProvider)
			throws ReadException {
		if (pTextureProvider == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).Prepare(pTextureProvider);
		}
	}

	public void SetScale(float scale) {
		m_scale = scale;
		return;
	}

	public float GetScale() {
		return m_scale;
	}

	public void ClearMotion() {
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).ClearMotion();
		}
		for (int i = 0; i < m_morps.size(); i++) {
			m_morps.get(i).ClearMotion();
		}
		return;
	}

	public void UpdateAsync(IDataMutex pMutex, float frameNo) {
		if (m_pVertexList == null || pMutex == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		UpdateMotion(frameNo);
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).UpdateSkinning();
		}
		pMutex.Begin();
		m_pVertexList.UpdateSkinning();
		pMutex.End();
	}

	public IDataMutex DrawAsync(IDataMutex pMutex, IGL gl) {
		if (pMutex == null || gl == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		pMutex.Begin();
		UpdateVertexBuffer();
		pMutex.End();
		gl.glPushMatrix();
		gl.glScalef(m_scale, m_scale, m_scale * -1.0f);
		boolean normalizeEnabled = gl.glIsEnabled(IGL.C.GL_NORMALIZE);
		gl.glEnable(IGL.C.GL_NORMALIZE);
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).Draw(gl);
		}
		if (normalizeEnabled == false) {
			gl.glDisable(IGL.C.GL_NORMALIZE);
		}
		gl.glPopMatrix();
		return pMutex;
	}

	public void SetFace(String faceName) {
		String elemName = null;
		String name = null;
		MMDMorp pElem = null;
		MMDMorp pSelectedElem = null;
		MMD_VERTEX_DESC[] ppOriginalDescs = null;
		if (faceName == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		name = faceName;
		pSelectedElem = null;
		for (int i = 0; i < m_morps.size(); i++) {
			pElem = m_morps.get(i);
			elemName = pElem.GetName();
			if (elemName.equals(name)) {
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

	public int GetFaceCount() {
		return m_morps.size();
	}

	public String GetFaceName(int index) {
		if (index >= m_morps.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_morps.get(index).GetName();
	}

	public int GetBoneCount() {
		Integer pCount = 0;
		pCount = m_bones.size();
		return pCount;
	}

	public String GetBoneName(int index) {
		if (index >= m_bones.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_bones.get(index).GetName();
	}

	public int GetIKCount() {
		return m_iks.size();
	}

	public String GetIKTargetName(int index) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_iks.get(index).GetTargetName();
	}

	public boolean IsIKEnabled(int index) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_iks.get(index).IsEnabled();
	}

	public void SetIKEnabled(int index, boolean value) {
		if (index >= m_iks.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		m_iks.get(index).SetEnabled(value);
	}

	public void SetBoneVisible(int index, boolean visible) {
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
		return;
	}

	public boolean IsBoneVisible(int index) {
		if (index >= m_bones.size()) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		return m_bones.get(index).IsVisible();
	}

	public Integer GetMaxFrame() {
		Integer pFrameNo = 0;
		int f = 0;
		int ret = 0;
		int validCount = 0;
		ret = 0;
		validCount = 0;
		for (int i = 0; i < m_morps.size(); i++) {
			pFrameNo = m_morps.get(i).GetMaxFrame();
			if (pFrameNo == null) {
				continue;
			}
			validCount++;
			if (f > ret) {
				ret = f;
			}
		}
		for (int i = 0; i < m_bones.size(); i++) {
			pFrameNo = m_bones.get(i).GetMaxFrame();
			if (pFrameNo == null) {
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
		pFrameNo = ret;
		return ret;
	}

	public void ResetVertexes() {
		if (m_pVertexList == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_pVertexList.ResetVertexes();
	}

	public void UpdateSkinning() {
		if (m_pVertexList == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		for (int i = 0; i < m_bones.size(); i++) {
			m_bones.get(i).UpdateSkinning();
		}
		m_pVertexList.UpdateSkinning();
	}

	public void UpdateVertexBuffer() {
		for (int i = 0; i < m_materials.size(); i++) {
			m_materials.get(i).UpdateVertexBuffer();
		}
	}

	public boolean UpdateMotion(float elapsedFrame) {
		MMD_VERTEX_DESC[] ppOriginalDescs = null;
		if (m_pVertexList == null) {
			return false;
		}
		ppOriginalDescs = null;
		ppOriginalDescs = m_pVertexList.GetVertexDescs();
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
			m_bones.get(i).UpdateMatrix();
		}
		for (int i = 0; i < m_iks.size(); i++) {
			m_iks.get(i).Update();
		}
		return true;
	}
}
