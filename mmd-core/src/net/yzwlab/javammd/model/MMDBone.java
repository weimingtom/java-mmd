package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.MMD_MATRIX;
import net.yzwlab.javammd.format.MMD_MOTION_PAD;
import net.yzwlab.javammd.format.MMD_VECTOR2;
import net.yzwlab.javammd.format.MMD_VECTOR3;
import net.yzwlab.javammd.format.MMD_VECTOR4;
import net.yzwlab.javammd.format.MMD_VERTEX_TEXUSE;
import net.yzwlab.javammd.format.PMD_BONE_RECORD;
import net.yzwlab.javammd.format.VMD_MOTION_RECORD;

public class MMDBone {
	protected PMD_BONE_RECORD m_bone;

	private MMDBone m_parent;

	protected MMDBone m_pChild;

	protected MMD_VECTOR3 m_offset;

	protected MMD_MATRIX m_invTransform;

	protected MMD_VECTOR3 m_effectPosition;

	protected MMD_VECTOR4 m_effectRotation;

	protected MMD_MATRIX m_effectLocal;

	protected MMD_MATRIX m_effectSkinning;

	protected boolean m_bIKLimitAngle;

	protected List<Motion> m_motions;

	protected boolean m_bVisible;

	private MMD_MATRIX effectLocalTemp;

	public static int less(Motion pLeft, Motion pRight) {
		int f1 = 0;
		int f2 = 0;
		if (pLeft == null || pRight == null) {
			throw new IllegalArgumentException();
		}
		f1 = 0;
		f2 = 0;
		f1 = pLeft.getFrameNo();
		f2 = pRight.getFrameNo();
		if (f1 < f2) {
			return -1;
		}
		if (f1 > f2) {
			return 1;
		}
		return 0;
	}

	/**
	 * 構築します。
	 * 
	 * @param pBone
	 *            ボーンデータ。nullは不可。
	 */
	public MMDBone(PMD_BONE_RECORD pBone) {
		if (pBone == null) {
			throw new IllegalArgumentException();
		}
		this.m_bVisible = true;
		this.m_motions = new ArrayList<Motion>();
		this.m_bIKLimitAngle = false;
		this.m_effectSkinning = new MMD_MATRIX();
		this.m_effectLocal = new MMD_MATRIX();
		this.m_effectRotation = new MMD_VECTOR4();
		this.m_effectPosition = new MMD_VECTOR3();
		this.m_invTransform = new MMD_MATRIX();
		this.m_offset = new MMD_VECTOR3();
		this.m_pChild = null;
		this.m_parent = null;
		this.m_bone = pBone;
		this.effectLocalTemp = new MMD_MATRIX();
	}

	public void dispose() {
		clearMotion();
	}

	public void init(List<MMDBone> bones) {
		if ((m_bone.getParent() & 0x8000) == 0) {
			if (m_bone.getParent() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			m_parent = bones.get(m_bone.getParent());
			m_offset.setX(m_bone.getPos()[0] - m_parent.m_bone.getPos()[0]);
			m_offset.setY(m_bone.getPos()[1] - m_parent.m_bone.getPos()[1]);
			m_offset.setZ(m_bone.getPos()[2] - m_parent.m_bone.getPos()[2]);
		} else {
			m_offset.setX(m_bone.getPos()[0]);
			m_offset.setY(m_bone.getPos()[1]);
			m_offset.setZ(m_bone.getPos()[2]);
		}
		if ((m_bone.getTo() & 0x8000) == 0) {
			if (m_bone.getTo() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			m_pChild = bones.get(m_bone.getTo());
		}
		m_invTransform.generateIdentity();
		m_invTransform.getValues()[3][0] = -m_bone.getPos()[0];
		m_invTransform.getValues()[3][1] = -m_bone.getPos()[1];
		m_invTransform.getValues()[3][2] = -m_bone.getPos()[2];
		reset();
	}

	public void setVisible(boolean visible) {
		m_bVisible = visible;
		return;
	}

	public boolean isVisible() {
		return m_bVisible;
	}

	public void reset() {
		m_effectPosition.toZero();
		m_effectRotation.toZero();
		m_effectRotation.setW(1.0f);
		m_effectLocal.generateIdentity();
		float[][] localValues = m_effectLocal.getValues();
		localValues[3][0] = m_bone.getPos()[0];
		localValues[3][1] = m_bone.getPos()[1];
		localValues[3][2] = m_bone.getPos()[2];
		return;
	}

	public void clearMotion() {
		m_motions.clear();
	}

	public byte[] getName() {
		return DataUtils.getStringData(m_bone.getName());
	}

	public boolean isTarget(VMD_MOTION_RECORD pMotion) {
		if (pMotion == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		if (DataUtils.compare(m_bone.getName(), pMotion.getName(), 15) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * モーションを追加します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @param offset
	 *            オフセット。
	 * @param pMotion
	 *            モーション。nullは不可。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public void addMotion(IReadBuffer buffer, int offset,
			VMD_MOTION_RECORD pMotion) throws ReadException {
		if (buffer == null || pMotion == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		Motion pMot = new Motion(buffer, offset, pMotion);
		m_motions.add(pMot);
		return;
	}

	/**
	 * スキニング行列を更新します。
	 */
	public void updateSkinning() {
		m_effectSkinning.multiply(m_invTransform, m_effectLocal);
	}

	/**
	 * スキニング行列を適用します。
	 * 
	 * @param pOriginal
	 *            オリジナルの行列。nullは不可。
	 * @param pDest
	 *            出力先の行列。nullは不可。
	 * @return 出力先の行列。
	 */
	public MMD_VERTEX_TEXUSE applySkinning(MMD_VERTEX_TEXUSE pOriginal,
			MMD_VERTEX_TEXUSE pDest) {
		if (pOriginal == null || pDest == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		MMD_VECTOR3 point = pDest.getPoint();
		point.copyFrom(pOriginal.getPoint());
		point.transform(m_effectSkinning);
		MMD_VECTOR3 normal = pDest.getNormal();
		normal.copyFrom(pOriginal.getNormal());
		normal.rotate(m_effectSkinning);
		pDest.getUv().copyFrom(pOriginal.getUv());
		return pDest;
	}

	/**
	 * スキニング行列を適用します。
	 * 
	 * @param pOriginal
	 *            オリジナル行列。nullは不可。
	 * @param pBone
	 *            ボーン。nullは不可。
	 * @param bweight
	 *            重み。
	 * @param pDest
	 *            適用結果の格納先。nullは不可。
	 * @param skinning
	 *            スキニング用バッファ。nullは不可。
	 * @return
	 */
	public MMD_VERTEX_TEXUSE applySkinning(MMD_VERTEX_TEXUSE pOriginal,
			MMDBone pBone, float bweight, MMD_VERTEX_TEXUSE pDest,
			MMD_MATRIX skinning) {
		if (pOriginal == null || pBone == null || pDest == null
				|| skinning == null) {
			throw new IllegalArgumentException();
		}
		skinning.lerp(m_effectSkinning, pBone.m_effectSkinning, bweight);
		MMD_VECTOR3 point = pDest.getPoint();
		point.copyFrom(pOriginal.getPoint());
		point.transform(skinning);
		MMD_VECTOR3 normal = pDest.getNormal();
		normal.copyFrom(pOriginal.getNormal());
		normal.rotate(skinning);
		pDest.getUv().copyFrom(pOriginal.getUv());
		return pDest;
	}

	public void prepareMotion() {
		Collections.sort(m_motions, new Comparator<Motion>() {
			@Override
			public int compare(Motion m1, Motion m2) {
				return less(m1, m2);
			}
		});
	}

	public void updateMotion(float elapsedFrame) {
		float offset = 0.0f;
		Motion pBegin = null;
		Motion pEnd = null;
		pBegin = null;
		pEnd = null;
		offset = 0.0f;
		MotionSet motionSet = findMotion(elapsedFrame);
		if (motionSet == null) {
			return;
		}
		pBegin = motionSet.motion1;
		pEnd = motionSet.motion2;
		offset = motionSet.offset;
		if (pEnd == null) {
			pBegin.getVectors(m_effectPosition, m_effectRotation);
		} else {
			MMD_VECTOR3 beginPos = new MMD_VECTOR3(), endPos = new MMD_VECTOR3();
			MMD_VECTOR4 beginQt = new MMD_VECTOR4(), endQt = new MMD_VECTOR4();
			pBegin.getVectors(beginPos, beginQt);
			pEnd.getVectors(endPos, endQt);
			m_effectPosition = pEnd.lerp(beginPos, endPos, offset);
			pEnd.lerp(m_effectRotation, beginQt, endQt, offset);
		}
	}

	/**
	 * 行列を更新します。
	 */
	public void updateMatrix() {
		m_effectLocal.fromQuaternion(m_effectRotation);
		float[][] localValues = m_effectLocal.getValues();
		localValues[3][0] = m_effectPosition.getX() + m_offset.getX();
		localValues[3][1] = m_effectPosition.getY() + m_offset.getY();
		localValues[3][2] = m_effectPosition.getZ() + m_offset.getZ();
		// String parentName = "NONE";
		// if (m_parent != null) {
		// parentName = new String(m_parent.getName());
		// }
		// System.err.println("MMDBone(" + new String(getName())
		// + ")updateMatrix: m_effectPosition=" + m_effectPosition
		// + ", m_offset=" + m_offset + "  (parent=" + parentName + ")");
		if (m_parent != null) {
			effectLocalTemp.copyFrom(m_effectLocal);
			m_effectLocal.multiply(effectLocalTemp, m_parent.m_effectLocal);
		}
	}

	/**
	 * 最大フレーム番号を取得します。
	 * 
	 * @return 最大フレーム番号。未定義の場合はnull。
	 */
	public Integer getMaxFrame() {
		if (m_motions.size() == 0) {
			return null;
		}
		return m_motions.get(m_motions.size() - 1).getFrameNo();
	}

	public void updateIKLimitAngle() {
		// TODO 暫定措置
		if (Arrays.equals(DataUtils.getStringData(m_bone.getName(), 20),
				new byte[] { (byte) -115, (byte) -74, (byte) -126, (byte) -48,
						(byte) -126, (byte) -76 }/* 左ひざ */)
				|| Arrays.equals(DataUtils.getStringData(m_bone.getName(), 20),
						new byte[] { (byte) -119, (byte) 69, (byte) -126,
								(byte) -48, (byte) -126, (byte) -76 } /* 右ひざ */)) {
			m_bIKLimitAngle = true;
		}
		return;
	}

	public boolean isIKLimitAngle() {
		return m_bIKLimitAngle;
	}

	/**
	 * local行列から位置を取得します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return local行列のうち位置。
	 */
	public MMD_VECTOR3 getPositionFromLocal(MMD_VECTOR3 buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		float[][] localValues = m_effectLocal.getValues();
		buffer.setX(localValues[3][0]);
		buffer.setY(localValues[3][1]);
		buffer.setZ(localValues[3][2]);
		return buffer;
	}

	/**
	 * local行列を取得します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return local行列。
	 */
	public MMD_MATRIX getLocal(MMD_MATRIX buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		buffer.copyFrom(m_effectLocal);
		return buffer;
	}

	public PositionAndQT getVectors() {
		return new PositionAndQT(m_effectPosition, m_effectRotation);
	}

	public void setVectors(MMD_VECTOR3 pPos, MMD_VECTOR4 pQt) {
		if (pPos == null || pQt == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_effectPosition.copyFrom(pPos);
		m_effectRotation.copyFrom(pQt);
	}

	public MotionSet findMotion(float elapsedTime) {
		int fr = 0;
		Motion pEMotion = null;
		Motion pSMotion = null;
		if (m_motions.size() == 0) {
			return null;
		}
		fr = m_motions.get(0).getFrameNo();
		if (elapsedTime <= fr) {
			return new MotionSet(m_motions.get(0), null, 0.0f);
		}
		fr = m_motions.get(m_motions.size() - 1).getFrameNo();
		if (elapsedTime >= fr) {
			return new MotionSet(m_motions.get(m_motions.size() - 1), null,
					0.0f);
		}
		for (int i = 0; i < m_motions.size() - 1; i++) {
			pSMotion = m_motions.get(i);
			pEMotion = m_motions.get(i + 1);
			int fr1 = pSMotion.getFrameNo();
			int fr2 = pEMotion.getFrameNo();
			if (fr1 <= elapsedTime && elapsedTime < fr2) {
				return new MotionSet(pSMotion, pEMotion, (elapsedTime - fr1)
						/ (fr2 - fr1));
			}
		}
		return null;
	}

	/**
	 * 親の名前を取得します。
	 * 
	 * @return 親の名前。
	 */
	public byte[] getParentName() {
		if (m_parent == null) {
			return null;
		}
		return m_parent.getName();
	}

	/**
	 * モーションを定義します。
	 */
	public class Motion {

		/**
		 * オフセットを保持します。
		 */
		private int offset;

		protected VMD_MOTION_RECORD m_motion;

		protected MMD_VECTOR3 m_pos;

		protected MMD_VECTOR4 m_qt;

		protected BezierCurve m_pXBez;

		protected BezierCurve m_pYBez;

		protected BezierCurve m_pZBez;

		protected BezierCurve m_pRotBez;

		/**
		 * 構築します。
		 * 
		 * @param buffer
		 *            バッファ。nullは不可。
		 * @param offset
		 *            オフセット。　
		 * @param pMotion
		 *            　モーション情報。nullは不可。
		 * @throws ReadException
		 *             読み込み時のエラー。
		 */
		public Motion(IReadBuffer buffer, int offset, VMD_MOTION_RECORD pMotion)
				throws ReadException {
			if (buffer == null || pMotion == null) {
				throw new IllegalArgumentException();
			}
			this.offset = offset;
			this.m_pRotBez = null;
			this.m_pZBez = null;
			this.m_pYBez = null;
			this.m_pXBez = null;
			this.m_qt = new MMD_VECTOR4();
			this.m_pos = new MMD_VECTOR3();
			this.m_motion = null;
			m_pXBez = null;
			m_pYBez = null;
			m_pZBez = null;
			m_pRotBez = null;
			m_motion = pMotion;
			m_pos.setX(m_motion.getPos()[0]);
			m_pos.setY(m_motion.getPos()[1]);
			m_pos.setZ(m_motion.getPos()[2]);
			m_qt = new MMD_VECTOR4();
			m_qt.setX(m_motion.getQt()[0]);
			m_qt.setY(m_motion.getQt()[1]);
			m_qt.setZ(m_motion.getQt()[2]);
			m_qt.setW(m_motion.getQt()[3]);
			m_qt.normalize();
			MMD_MOTION_PAD pad = (new MMD_MOTION_PAD()).Read(buffer
					.createFromByteArray(m_motion.getPad()));
			MMD_VECTOR2 p1 = new MMD_VECTOR2(), p2 = new MMD_VECTOR2();
			p1.setX(pad.getCInterpolationX()[0]);
			p1.setY(pad.getCInterpolationX()[4]);
			p2.setX(pad.getCInterpolationX()[8]);
			p2.setY(pad.getCInterpolationX()[12]);
			m_pXBez = new BezierCurve(p1, p2);
			p1.setX(pad.getCInterpolationY()[0]);
			p1.setY(pad.getCInterpolationY()[4]);
			p2.setX(pad.getCInterpolationY()[8]);
			p2.setY(pad.getCInterpolationY()[12]);
			m_pYBez = new BezierCurve(p1, p2);
			p1.setX(pad.getCInterpolationZ()[0]);
			p1.setY(pad.getCInterpolationZ()[4]);
			p2.setX(pad.getCInterpolationZ()[8]);
			p2.setY(pad.getCInterpolationZ()[12]);
			m_pZBez = new BezierCurve(p1, p2);
			p1.setX(pad.getCInterpolationRot()[0]);
			p1.setY(pad.getCInterpolationRot()[4]);
			p2.setX(pad.getCInterpolationRot()[8]);
			p2.setY(pad.getCInterpolationRot()[12]);
			m_pRotBez = new BezierCurve(p1, p2);
		}

		/**
		 * このモーションのフレーム番号を取得します。
		 * 
		 * @return このモーションのフレーム番号。
		 */
		public int getFrameNo() {
			return m_motion.getFrameNo() + offset;
		}

		public void getVectors(MMD_VECTOR3 pos, MMD_VECTOR4 qt) {
			if (pos == null || qt == null) {
				throw new IllegalArgumentException();
			}
			pos.copyFrom(m_pos);
			qt.copyFrom(m_qt);
		}

		public MMD_VECTOR3 lerp(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2,
				float weight) {
			MMD_VECTOR3 pDest = new MMD_VECTOR3();
			float posLerp = 0.0f;
			if (pDest == null || pValue1 == null || pValue2 == null) {
				throw new IllegalArgumentException("E_POINTER");
			}
			posLerp = 0.0f;
			posLerp = m_pXBez.getValue(weight);
			pDest.setX(pValue1.getX() * (1.0f - posLerp) + pValue2.getX()
					* posLerp);
			posLerp = m_pYBez.getValue(weight);
			pDest.setY(pValue1.getY() * (1.0f - posLerp) + pValue2.getY()
					* posLerp);
			posLerp = m_pZBez.getValue(weight);
			pDest.setZ(pValue1.getZ() * (1.0f - posLerp) + pValue2.getZ()
					* posLerp);
			return pDest;
		}

		/**
		 * 線形補間を行います。
		 * 
		 * @param target
		 *            格納対象のベクトル。nullは不可。
		 * @param pValue1
		 *            値1。nullは不可。
		 * @param pValue2
		 *            値2。nullは不可。
		 * @param weight
		 *            重み。
		 */
		public void lerp(MMD_VECTOR4 target, MMD_VECTOR4 pValue1,
				MMD_VECTOR4 pValue2, float weight) {
			if (target == null || pValue1 == null || pValue2 == null) {
				throw new IllegalArgumentException();
			}
			float rotLerp = m_pRotBez.getValue(weight);
			target.lerp(pValue1, pValue2, rotLerp);
		}
	}

	protected class PositionAndQT {

		private MMD_VECTOR3 position;

		private MMD_VECTOR4 qt;

		public PositionAndQT(MMD_VECTOR3 position, MMD_VECTOR4 qt) {
			if (position == null || qt == null) {
				throw new IllegalArgumentException();
			}
			this.position = new MMD_VECTOR3(position);
			this.qt = new MMD_VECTOR4(qt);
		}

		public MMD_VECTOR3 getPosition() {
			return position;
		}

		public MMD_VECTOR4 getQt() {
			return qt;
		}

	}

	private class MotionSet {

		private Motion motion1;

		private Motion motion2;

		private float offset;

		public MotionSet(Motion motion1, Motion motion2, float offset) {
			if (motion1 == null && motion2 == null && offset >= 1.0f) {
				throw new IllegalArgumentException();
			}
			this.motion1 = motion1;
			this.motion2 = motion2;
			this.offset = offset;
		}

	}
}
