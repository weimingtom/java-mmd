package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.yzwlab.javammd.format.MMD_VECTOR3;
import net.yzwlab.javammd.format.MMD_VERTEX_DESC;
import net.yzwlab.javammd.format.MMD_VERTEX_TEXUSE;
import net.yzwlab.javammd.format.PMD_MORP_RECORD;
import net.yzwlab.javammd.format.PMD_MORP_VERTEX_RECORD;
import net.yzwlab.javammd.format.VMD_MORP_RECORD;

public class MMDMorp {
	protected PMD_MORP_RECORD m_morp;

	protected List<Motion> m_motions;

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
			return +1;
		}
		return 0;
	}

	public MMDMorp(PMD_MORP_RECORD pMorp, PMD_MORP_RECORD pMorpBase) {
		this.m_motions = new ArrayList<Motion>();
		this.m_morp = new PMD_MORP_RECORD();
		PMD_MORP_VERTEX_RECORD source = new PMD_MORP_VERTEX_RECORD();
		PMD_MORP_VERTEX_RECORD source1 = new PMD_MORP_VERTEX_RECORD();
		PMD_MORP_VERTEX_RECORD source2 = new PMD_MORP_VERTEX_RECORD();
		if (pMorp == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_morp = new PMD_MORP_RECORD(pMorp);
		m_morp.getMv().clear();
		if (pMorpBase == null) {
			for (int i = 0; i < pMorp.getMv().size(); i++) {
				source = pMorp.getMv().get(i);
				PMD_MORP_VERTEX_RECORD vert = new PMD_MORP_VERTEX_RECORD();
				vert.getVec()[0] = source.getVec()[0];
				vert.getVec()[1] = source.getVec()[1];
				vert.getVec()[2] = source.getVec()[2];
				vert.setNo(source.getNo());
				m_morp.getMv().add(vert);
			}
		} else {
			for (int i = 0; i < pMorp.getMv().size(); i++) {
				source1 = pMorp.getMv().get(i);
				source2 = pMorpBase.getMv().get(source1.getNo());
				PMD_MORP_VERTEX_RECORD vert = new PMD_MORP_VERTEX_RECORD();
				vert.getVec()[0] = (source1.getVec()[0] + source2.getVec()[0]);
				vert.getVec()[1] = (source1.getVec()[1] + source2.getVec()[1]);
				vert.getVec()[2] = (source1.getVec()[2] + source2.getVec()[2]);
				vert.setNo(source2.getNo());
				m_morp.getMv().add(vert);
			}
		}
	}

	public void dispose() {
		ClearMotion();
	}

	public void ClearMotion() {
		Motion pMotion = null;
		for (int i = 0; i < m_motions.size(); i++) {
			pMotion = m_motions.get(i);
			pMotion.dispose();
		}
		m_motions.clear();
		return;
	}

	public String GetName() {
		return DataUtils.getString(m_morp.getName(), 20);
	}

	public Integer GetMaxFrame() {
		if (m_motions.size() == 0) {
			return null;
		}
		return m_motions.get(m_motions.size() - 1).GetFrameNo();
	}

	public boolean IsTarget(VMD_MORP_RECORD pMotion) {
		if (pMotion == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		if (DataUtils.compare(m_morp.getName(), pMotion.getName(), 15) == 0) {
			return true;
		}
		return false;
	}

	public void AddMotion(VMD_MORP_RECORD pMotion) {
		Motion pMot = null;
		if (pMotion == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		try {
			pMot = new Motion(pMotion);
		} catch (Throwable t) {
			pMot.dispose();
			pMot = null;
			return;
		}
		m_motions.add(pMot);
		return;
	}

	public void PrepareMotion() {
		Collections.sort(m_motions, new Comparator<Motion>() {
			@Override
			public int compare(Motion o1, Motion o2) {
				return Less(o1, o2);
			}
		});
		return;
	}

	public MMD_VERTEX_DESC[] ApplyMotion(float elapsedFrame,
			MMD_VERTEX_DESC[] pOriginalVertexes) {
		float offset = 0.0f;
		Motion pBegin = null;
		Motion pEnd = null;
		float weight = 0.0f;
		float weight1 = 0.0f;
		float weight2 = 0.0f;
		pBegin = null;
		pEnd = null;
		offset = 0.0f;
		MotionSet ms = FindMotion(elapsedFrame);
		if (ms == null) {
			return pOriginalVertexes;
		}
		pBegin = ms.motion1;
		pEnd = ms.motion2;
		offset = ms.offset;
		if (pEnd == null) {
			weight = pBegin.GetRate();
			return Blend(pOriginalVertexes, weight);
		} else {
			weight1 = pBegin.GetRate();
			weight2 = pEnd.GetRate();
			weight = weight1 * (1.0f - offset) + weight2 * offset;
			return Blend(pOriginalVertexes, weight);
		}
	}

	public MMD_VERTEX_DESC[] Set(MMD_VERTEX_DESC[] pOriginalVertexes) {
		int jno = 0;
		PMD_MORP_VERTEX_RECORD v = new PMD_MORP_VERTEX_RECORD();
		for (int i = 0; i < m_morp.getMv().size(); i++) {
			v = m_morp.getMv().get(i);
			jno = v.getNo();
			MMD_VERTEX_TEXUSE faced = pOriginalVertexes[jno].getFaced();
			faced.getPoint().setX(v.getVec()[0]);
			faced.getPoint().setY(v.getVec()[1]);
			faced.getPoint().setZ(v.getVec()[2]);
			pOriginalVertexes[jno].setFaced(faced);
		}
		return pOriginalVertexes;
	}

	public MMD_VERTEX_DESC[] Blend(MMD_VERTEX_DESC[] pOriginalVertexes,
			float weight) {
		int jno = 0;
		PMD_MORP_VERTEX_RECORD v = new PMD_MORP_VERTEX_RECORD();
		MMD_VECTOR3 vec = new MMD_VECTOR3();
		if (weight == 1.0f) {
			return Set(pOriginalVertexes);
		}
		for (int i = 0; i < m_morp.getMv().size(); i++) {
			v = m_morp.getMv().get(i);
			vec.setX(v.getVec()[0]);
			vec.setY(v.getVec()[1]);
			vec.setZ(v.getVec()[2]);
			jno = v.getNo();
			MMD_VERTEX_TEXUSE faced = pOriginalVertexes[jno].getFaced();
			faced.setPoint(CalcUtil.Lerp(faced.getPoint(), vec, weight));
			pOriginalVertexes[jno].setFaced(faced);
		}
		return pOriginalVertexes;
	}

	public MotionSet FindMotion(float elapsedTime) {
		Float pOffset = 0.0f;
		int fr = 0;
		int fr1 = 0;
		int fr2 = 0;
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
			fr1 = 0;
			fr2 = 0;
			fr1 = pSMotion.GetFrameNo();
			fr2 = pEMotion.GetFrameNo();
			if (fr1 <= elapsedTime && elapsedTime <= fr2) {
				pOffset = (elapsedTime - fr1) / (fr2 - fr1);
				return new MotionSet(pSMotion, pEMotion, pOffset);
			}
		}
		return null;
	}

	public class Motion {
		protected VMD_MORP_RECORD m_motion;

		public Motion(VMD_MORP_RECORD pMotion) {
			this.m_motion = null;
			if (pMotion == null) {
				throw new IllegalArgumentException();
			}
			m_motion = pMotion;
		}

		public void dispose() {
			;
		}

		public int GetFrameNo() {
			Integer pFrameNo = 0;
			pFrameNo = m_motion.getFrameNo();
			return pFrameNo;
		}

		public float GetRate() {
			Float pRate = 0.0f;
			pRate = m_motion.getFactor();
			return pRate;
		}

	}

	private class MotionSet {

		private Motion motion1;

		private Motion motion2;

		private float offset;

		public MotionSet(Motion motion1, Motion motion2, float offset) {
			if (motion1 == null && motion2 == null) {
				throw new IllegalArgumentException();
			}
			this.motion1 = motion1;
			this.motion2 = motion2;
			this.offset = offset;
		}

	}
}
