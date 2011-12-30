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

	/**
	 * 構築します。
	 * 
	 * @param pIK
	 *            IK。nullは不可。
	 */
	public MMDIK(PMD_IK_RECORD pIK) {
		if (pIK == null) {
			throw new IllegalArgumentException();
		}
		if (pIK.getLink().size() == 0) {
			throw new IllegalArgumentException();
		}
		this.m_bones = new ArrayList<MMDBone>();
		this.m_fact = 0.0f;
		this.m_pEffect = null;
		this.m_pTarget = null;
		this.m_bEnabled = false;
		this.m_ik = pIK;
		m_pTarget = null;
		m_pEffect = null;
		m_fact = 0.0f;
		m_bEnabled = true;
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
		float dp = 0.0f;
		int index = 0;
		boolean limitAngle = false;
		MMDBone pBone = null;
		float rotAngle = 0.0f;
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

		// オブジェクトはあらかじめ準備しておく(パフォーマンス対策)
		MMD_VECTOR3 diff = new MMD_VECTOR3();
		MMD_MATRIX matBoneBuf = new MMD_MATRIX();
		MMD_MATRIX matInvBoneBuf = new MMD_MATRIX();

		MMD_VECTOR3 targetOriginalPosition = m_pTarget.GetPositionFromLocal();
		for (int it = 0; it < m_ik.getCount(); it++) {
			index = 0;
			for (Iterator<MMDBone> bit = m_bones.iterator(); bit.hasNext(); index++) {
				pBone = bit.next();
				MMD_VECTOR3 effectPosition = m_pEffect.GetPositionFromLocal();
				MMD_MATRIX matBone = pBone.getLocal(matBoneBuf);
				MMD_MATRIX matInvBone = matBone.inverse(matInvBoneBuf);
				effectPosition = CalcUtil.Transform(effectPosition, matInvBone);
				MMD_VECTOR3 targetPosition = CalcUtil.Transform(
						targetOriginalPosition, matInvBone);
				diff.subtract(effectPosition, targetPosition);
				dp = 0.0f;
				dp = diff.dotProduct(diff);
				if (dp < 0.0000001f) {
					return;
				}
				effectPosition.normalize();
				targetPosition.normalize();
				dp = effectPosition.dotProduct(targetPosition);
				rotAngle = (float) Math.acos(dp);
				if (0.00000001f < Math.abs(rotAngle)) {
					if (rotAngle < -m_fact) {
						rotAngle = -m_fact;
					} else if (m_fact < rotAngle) {
						rotAngle = m_fact;
					}
					MMD_VECTOR3 rotAxis = CalcUtil.CrossProduct(effectPosition,
							targetPosition);
					dp = rotAxis.dotProduct(rotAxis);
					if (dp < 0.0000001f) {
						continue;
					}
					rotAxis.normalize();
					MMD_VECTOR4 rotQuat = CalcUtil
							.CreateAxis(rotAxis, rotAngle);
					limitAngle = false;
					limitAngle = pBone.IsIKLimitAngle();
					if (limitAngle) {
						rotQuat = CalcUtil.LimitAngle(rotQuat);
					}
					rotQuat.normalize();
					MMDBone.PositionAndQT pqt = pBone.GetVectors();
					MMD_VECTOR3 destPosition = pqt.getPosition();
					MMD_VECTOR4 destRotation = pqt.getQt();
					destRotation = CalcUtil.Multiply(destRotation, rotQuat);
					destRotation.normalize();
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
