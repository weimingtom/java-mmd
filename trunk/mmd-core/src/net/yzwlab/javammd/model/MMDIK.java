package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.yzwlab.javammd.format.MMD_MATRIX;
import net.yzwlab.javammd.format.MMD_VECTOR3;
import net.yzwlab.javammd.format.MMD_VECTOR4;
import net.yzwlab.javammd.format.PMD_IK_RECORD;

public class MMDIK {
	protected PMD_IK_RECORD m_ik;

	protected boolean m_bEnabled;

	protected MMDBone m_pTarget;

	protected MMDBone m_pEffect;

	protected float m_fact;

	protected List<MMDBone> m_bones;

	public static int Compare(MMDIK pLeft, MMDIK pRight) {
		int sortVal1 = 0;
		int sortVal2 = 0;
		if (pLeft == null || pRight == null) {
			throw new IllegalArgumentException();
		}
		sortVal1 = pLeft.GetSortValue();
		sortVal2 = pRight.GetSortValue();
		if (sortVal1 < sortVal2) {
			return -1;
		}
		if (sortVal1 > sortVal2) {
			return 1;
		}
		return 0;
	}

	public MMDIK(PMD_IK_RECORD pIK) {
		this.m_bones = new ArrayList<MMDBone>();
		this.m_fact = 0.0f;
		this.m_pEffect = null;
		this.m_pTarget = null;
		this.m_bEnabled = false;
		this.m_ik = new PMD_IK_RECORD();
		m_pTarget = null;
		m_pEffect = null;
		m_fact = 0.0f;
		m_bEnabled = true;
		if (pIK == null) {
			throw new IllegalArgumentException();
		}
		if (pIK.getLink().size() == 0) {
			throw new IllegalArgumentException();
		}
		m_ik = pIK;
	}

	public void dispose() {
		m_pTarget = null;
		m_pEffect = null;
	}

	public void Init(List<MMDBone> bones) {
		MMDBone pBone = null;
		m_pTarget = bones.get(m_ik.getParent());
		m_pEffect = bones.get(m_ik.getTo());
		m_fact = m_ik.getFact() * 3.1415926f;
		for (int i = 0; i < m_ik.getLink().size(); i++) {
			pBone = bones.get(m_ik.getLink().get(i));
			m_bones.add(pBone);
			pBone.UpdateIKLimitAngle();
		}
	}

	public byte[] GetTargetName() {
		if (m_pTarget == null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		return m_pTarget.GetName();
	}

	public void SetEnabled(boolean enabled) {
		m_bEnabled = enabled;
		return;
	}

	public boolean IsEnabled() {
		Boolean pEnabled = false;
		pEnabled = m_bEnabled;
		return pEnabled;
	}

	public void Update() {
		MMD_VECTOR3 destPosition = new MMD_VECTOR3();
		MMD_VECTOR4 destRotation = new MMD_VECTOR4();
		MMD_VECTOR3 diff = new MMD_VECTOR3();
		float dp = 0.0f;
		MMD_VECTOR3 effectPosition = new MMD_VECTOR3();
		int index = 0;
		boolean limitAngle = false;
		MMD_MATRIX matBone = new MMD_MATRIX();
		MMD_MATRIX matInvBone = new MMD_MATRIX();
		MMDBone pBone = null;
		float rotAngle = 0.0f;
		MMD_VECTOR3 rotAxis = new MMD_VECTOR3();
		MMD_VECTOR4 rotQuat = new MMD_VECTOR4();
		MMD_VECTOR3 targetOriginalPosition = new MMD_VECTOR3();
		MMD_VECTOR3 targetPosition = new MMD_VECTOR3();
		if (m_pTarget == null || m_pEffect == null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		if (m_bEnabled == false) {
			return;
		}
		for (int i = m_bones.size() - 1; i >= 0; i--) {
			m_bones.get(i).UpdateMatrix();
		}
		m_pEffect.UpdateMatrix();
		targetOriginalPosition = m_pTarget.GetPositionFromLocal();
		for (int it = 0; it < m_ik.getCount(); it++) {
			index = 0;
			for (Iterator<MMDBone> bit = m_bones.iterator(); bit.hasNext(); index++) {
				pBone = bit.next();
				effectPosition = m_pEffect.GetPositionFromLocal();
				matBone = pBone.GetLocal();
				matInvBone = CalcUtil.Inverse(matBone);
				effectPosition = CalcUtil.Transform(effectPosition, matInvBone);
				targetPosition = CalcUtil.Transform(targetOriginalPosition,
						matInvBone);
				diff = CalcUtil.Subtract(effectPosition, targetPosition);
				dp = 0.0f;
				dp = CalcUtil.DotProduct(diff, diff);
				if (dp < 0.0000001f) {
					return;
				}
				effectPosition = CalcUtil.Normalize(effectPosition);
				targetPosition = CalcUtil.Normalize(targetPosition);
				dp = CalcUtil.DotProduct(effectPosition, targetPosition);
				rotAngle = (float) Math.acos(dp);
				if (0.00000001f < Math.abs(rotAngle)) {
					if (rotAngle < -m_fact) {
						rotAngle = -m_fact;
					} else if (m_fact < rotAngle) {
						rotAngle = m_fact;
					}
					rotAxis = CalcUtil.CrossProduct(effectPosition,
							targetPosition);
					dp = CalcUtil.DotProduct(rotAxis, rotAxis);
					if (dp < 0.0000001f) {
						continue;
					}
					rotAxis = CalcUtil.Normalize(rotAxis);
					rotQuat = CalcUtil.CreateAxis(rotAxis, rotAngle);
					limitAngle = false;
					limitAngle = pBone.IsIKLimitAngle();
					if (limitAngle) {
						rotQuat = CalcUtil.LimitAngle(rotQuat);
					}
					rotQuat = CalcUtil.Normalize(rotQuat);
					MMDBone.PositionAndQT pqt = pBone.GetVectors();
					destPosition = pqt.getPosition();
					destRotation = pqt.getQt();
					destRotation = CalcUtil.Multiply(destRotation, rotQuat);
					destRotation = CalcUtil.Normalize(destRotation);
					pBone.SetVectors(destPosition, destRotation);
					for (int j = index; j >= 0; j--) {
						m_bones.get(j).UpdateMatrix();
					}
					m_pEffect.UpdateMatrix();
				}
			}
		}
		return;
	}

	public int GetSortValue() {
		Integer pNum = 0;
		if (m_ik.getLink().size() == 0) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		pNum = new Integer((Short) (m_ik.getLink().get(0)).shortValue());
		return pNum;
	}

}
