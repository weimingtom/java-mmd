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

	protected MMDBone m_pParent;

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

	public static int Less(Motion pLeft, Motion pRight) {
		int f1 = 0;
		int f2 = 0;
		if (pLeft == null || pRight == null) {
			throw new IllegalArgumentException();
		}
		f1 = 0;
		f2 = 0;
		f1 = pLeft.GetFrameNo();
		f2 = pRight.GetFrameNo();
		if (f1 < f2) {
			return -1;
		}
		if (f1 > f2) {
			return 1;
		}
		return 0;
	}

	public MMDBone(PMD_BONE_RECORD pBone) {
		this.m_bVisible = false;
		this.m_motions = new ArrayList<Motion>();
		this.m_bIKLimitAngle = false;
		this.m_effectSkinning = new MMD_MATRIX();
		this.m_effectLocal = new MMD_MATRIX();
		this.m_effectRotation = new MMD_VECTOR4();
		this.m_effectPosition = new MMD_VECTOR3();
		this.m_invTransform = new MMD_MATRIX();
		this.m_offset = new MMD_VECTOR3();
		this.m_pChild = null;
		this.m_pParent = null;
		this.m_bone = new PMD_BONE_RECORD();
		m_pParent = null;
		m_pChild = null;
		m_bIKLimitAngle = false;
		m_bVisible = true;
		if (pBone == null) {
			throw new IllegalArgumentException();
		}
		m_bone = pBone;
		m_offset = CalcUtil.ToZeroV3();
		m_invTransform = CalcUtil.ToZeroM();
		m_effectPosition = CalcUtil.ToZeroV3();
		m_effectLocal = CalcUtil.ToZeroM();
		m_effectPosition = CalcUtil.ToZeroV3();
		m_effectSkinning = CalcUtil.ToZeroM();
	}

	public void dispose() {
		ClearMotion();
	}

	public void Init(List<MMDBone> bones) {
		if ((m_bone.getParent() & 0x8000) == 0) {
			if (m_bone.getParent() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			m_pParent = bones.get(m_bone.getParent());
			m_offset.setX(m_bone.getPos()[0] - m_pParent.m_bone.getPos()[0]);
			m_offset.setY(m_bone.getPos()[1] - m_pParent.m_bone.getPos()[1]);
			m_offset.setZ(m_bone.getPos()[2] - m_pParent.m_bone.getPos()[2]);
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
		m_invTransform = CalcUtil.GenerateIdentity();
		m_invTransform.getValues()[3][0] = -m_bone.getPos()[0];
		m_invTransform.getValues()[3][1] = -m_bone.getPos()[1];
		m_invTransform.getValues()[3][2] = -m_bone.getPos()[2];
		Reset();
	}

	public void SetVisible(boolean visible) {
		m_bVisible = visible;
		return;
	}

	public boolean IsVisible() {
		return m_bVisible;
	}

	public void Reset() {
		m_effectPosition = CalcUtil.ToZeroV3();
		m_effectRotation = CalcUtil.ToZeroV4();
		m_effectRotation.setW(1.0f);
		m_effectLocal = CalcUtil.GenerateIdentity();
		m_effectLocal.getValues()[3][0] = m_bone.getPos()[0];
		m_effectLocal.getValues()[3][1] = m_bone.getPos()[1];
		m_effectLocal.getValues()[3][2] = m_bone.getPos()[2];
		return;
	}

	public void ClearMotion() {
		m_motions.clear();
	}

	public byte[] GetName() {
		return DataUtils.getStringData(m_bone.getName());
	}

	public boolean IsTarget(VMD_MOTION_RECORD pMotion) {
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
	 * @param pMotion
	 *            モーション。nullは不可。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public void AddMotion(IReadBuffer buffer, VMD_MOTION_RECORD pMotion)
			throws ReadException {
		if (buffer == null || pMotion == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		Motion pMot = new Motion(buffer, pMotion);
		m_motions.add(pMot);
		return;
	}

	public void UpdateSkinning() {
		m_effectSkinning = CalcUtil.Multiply(m_invTransform, m_effectLocal);
	}

	public MMD_VERTEX_TEXUSE ApplySkinning(MMD_VERTEX_TEXUSE pOriginal) {
		MMD_VERTEX_TEXUSE pDest = new MMD_VERTEX_TEXUSE();
		if (pDest == null || pOriginal == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		pDest.setPoint(CalcUtil.Transform(pOriginal.getPoint(),
				m_effectSkinning));
		pDest.setNormal(CalcUtil.Rotate(pOriginal.getNormal(), m_effectSkinning));
		pDest.setUv(pOriginal.getUv());
		return pDest;
	}

	public MMD_VERTEX_TEXUSE ApplySkinning(MMD_VERTEX_TEXUSE pOriginal,
			MMDBone pBone, float bweight) {
		MMD_VERTEX_TEXUSE pDest = new MMD_VERTEX_TEXUSE();
		MMD_MATRIX skinning = new MMD_MATRIX();
		if (pDest == null || pOriginal == null || pBone == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		skinning = CalcUtil.ToZeroM();
		skinning = CalcUtil.Lerp(m_effectSkinning, pBone.m_effectSkinning,
				bweight);
		pDest.setPoint(CalcUtil.Transform(pOriginal.getPoint(), skinning));
		pDest.setNormal(CalcUtil.Rotate(pOriginal.getNormal(), skinning));
		pDest.setUv(pOriginal.getUv());
		return pDest;
	}

	public void PrepareMotion() {
		Collections.sort(m_motions, new Comparator<Motion>() {
			@Override
			public int compare(Motion m1, Motion m2) {
				return Less(m1, m2);
			}
		});
	}

	public void UpdateMotion(float elapsedFrame) {
		float offset = 0.0f;
		Motion pBegin = null;
		Motion pEnd = null;
		pBegin = null;
		pEnd = null;
		offset = 0.0f;
		MotionSet motionSet = FindMotion(elapsedFrame);
		if (motionSet == null) {
			return;
		}
		pBegin = motionSet.motion1;
		pEnd = motionSet.motion2;
		offset = motionSet.offset;
		if (pEnd == null) {
			PositionAndQT pqt = pBegin.GetVectors();
			m_effectPosition = pqt.position;
			m_effectRotation = pqt.qt;
		} else {
			MMD_VECTOR3 beginPos = null, endPos = null;
			MMD_VECTOR4 beginQt = null, endQt = null;
			PositionAndQT beginpqt = pBegin.GetVectors();
			if (beginpqt != null) {
				beginPos = beginpqt.position;
				beginQt = beginpqt.qt;
			}
			PositionAndQT endpqt = pEnd.GetVectors();
			if (endpqt != null) {
				endPos = endpqt.position;
				endQt = endpqt.qt;
			}
			m_effectPosition = pEnd.Lerp(beginPos, endPos, offset);
			m_effectRotation = pEnd.Lerp(beginQt, endQt, offset);
		}
	}

	public void UpdateMatrix() {
		m_effectLocal = CalcUtil.QuaternionToMatrix(m_effectRotation);
		m_effectLocal.getValues()[3][0] = m_effectPosition.getX()
				+ m_offset.getX();
		m_effectLocal.getValues()[3][1] = m_effectPosition.getY()
				+ m_offset.getY();
		m_effectLocal.getValues()[3][2] = m_effectPosition.getZ()
				+ m_offset.getZ();
		if (m_pParent != null) {
			m_effectLocal = CalcUtil.Multiply(m_effectLocal,
					m_pParent.m_effectLocal);
		}
	}

	public Integer GetMaxFrame() {
		if (m_motions.size() == 0) {
			return null;
		}
		return m_motions.get(m_motions.size() - 1).GetFrameNo();
	}

	public void UpdateIKLimitAngle() {
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

	public boolean IsIKLimitAngle() {
		return m_bIKLimitAngle;
	}

	public MMD_VECTOR3 GetPositionFromLocal() {
		MMD_VECTOR3 pVector = new MMD_VECTOR3();
		pVector.setX(m_effectLocal.getValues()[3][0]);
		pVector.setY(m_effectLocal.getValues()[3][1]);
		pVector.setZ(m_effectLocal.getValues()[3][2]);
		return pVector;
	}

	public MMD_MATRIX GetLocal() {
		MMD_MATRIX pMatrix = new MMD_MATRIX(m_effectLocal);
		return pMatrix;
	}

	public PositionAndQT GetVectors() {
		return new PositionAndQT(m_effectPosition, m_effectRotation);
	}

	public void SetVectors(MMD_VECTOR3 pPos, MMD_VECTOR4 pQt) {
		if (pPos == null || pQt == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_effectPosition = new MMD_VECTOR3(pPos);
		m_effectRotation = new MMD_VECTOR4(pQt);
		return;
	}

	public MotionSet FindMotion(float elapsedTime) {
		int fr = 0;
		Motion pEMotion = null;
		Motion pSMotion = null;
		if (m_motions.size() == 0) {
			return null;
		}
		fr = m_motions.get(0).GetFrameNo();
		if (elapsedTime <= fr) {
			return new MotionSet(m_motions.get(0), null, 0.0f);
		}
		fr = m_motions.get(m_motions.size() - 1).GetFrameNo();
		if (elapsedTime >= fr) {
			return new MotionSet(m_motions.get(m_motions.size() - 1), null,
					0.0f);
		}
		for (int i = 0; i < m_motions.size() - 1; i++) {
			pSMotion = m_motions.get(i);
			pEMotion = m_motions.get(i + 1);
			int fr1 = pSMotion.GetFrameNo();
			int fr2 = pEMotion.GetFrameNo();
			if (fr1 <= elapsedTime && elapsedTime < fr2) {
				return new MotionSet(pSMotion, pEMotion, (elapsedTime - fr1)
						/ (fr2 - fr1));
			}
		}
		return null;
	}

	public class Motion {
		protected VMD_MOTION_RECORD m_motion;

		protected MMD_VECTOR3 m_pos;

		protected MMD_VECTOR4 m_qt;

		protected BezierCurve m_pXBez;

		protected BezierCurve m_pYBez;

		protected BezierCurve m_pZBez;

		protected BezierCurve m_pRotBez;

		public Motion(IReadBuffer buffer, VMD_MOTION_RECORD pMotion)
				throws ReadException {
			this.m_pRotBez = null;
			this.m_pZBez = null;
			this.m_pYBez = null;
			this.m_pXBez = null;
			this.m_qt = new MMD_VECTOR4();
			this.m_pos = new MMD_VECTOR3();
			this.m_motion = null;
			MMD_VECTOR4 qt = new MMD_VECTOR4();
			m_pXBez = null;
			m_pYBez = null;
			m_pZBez = null;
			m_pRotBez = null;
			if (pMotion == null) {
				throw new IllegalArgumentException();
			}
			m_motion = pMotion;
			m_pos.setX(m_motion.getPos()[0]);
			m_pos.setY(m_motion.getPos()[1]);
			m_pos.setZ(m_motion.getPos()[2]);
			qt.setX(m_motion.getQt()[0]);
			qt.setY(m_motion.getQt()[1]);
			qt.setZ(m_motion.getQt()[2]);
			qt.setW(m_motion.getQt()[3]);
			m_qt = CalcUtil.Normalize(qt);
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

		public int GetFrameNo() {
			return m_motion.getFrameNo();
		}

		public PositionAndQT GetVectors() {
			return new PositionAndQT(m_pos, m_qt);
		}

		public MMD_VECTOR3 Lerp(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2,
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

		public MMD_VECTOR4 Lerp(MMD_VECTOR4 pValue1, MMD_VECTOR4 pValue2,
				float weight) {
			MMD_VECTOR4 pDest = new MMD_VECTOR4();
			float rotLerp = 0.0f;
			if (pDest == null || pValue1 == null || pValue2 == null) {
				throw new IllegalArgumentException("E_POINTER");
			}
			rotLerp = 0.0f;
			rotLerp = m_pRotBez.getValue(weight);
			return CalcUtil.Lerp(pValue1, pValue2, rotLerp);
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
