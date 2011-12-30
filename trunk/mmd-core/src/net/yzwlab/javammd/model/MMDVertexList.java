package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.format.MMD_VERTEX_DESC;
import net.yzwlab.javammd.format.MMD_VERTEX_TEXUSE;
import net.yzwlab.javammd.format.PMD_VERTEX_RECORD;

public class MMDVertexList {
	protected List<PMD_VERTEX_RECORD> m_vertexes;

	protected List<Short> m_indices;

	protected MMD_VERTEX_DESC[] m_pVertexes;

	public MMDVertexList(List<PMD_VERTEX_RECORD> pVertexes, List<Short> pIndices) {
		this.m_pVertexes = null;
		this.m_indices = new ArrayList<Short>();
		this.m_vertexes = new ArrayList<PMD_VERTEX_RECORD>();
		m_pVertexes = null;
		if (pVertexes == null || pIndices == null) {
			throw new IllegalArgumentException();
		}
		m_vertexes = pVertexes;
		m_indices = pIndices;
		CreateVertexDesc();
	}

	public void dispose() {
		if (m_pVertexes != null) {
			// for (int i = 0; i < m_vertexes.size(); i++) {
			// pDesc = m_pVertexes[i];
			// m_pVertexes[i] = null;
			// }
			m_pVertexes = null;
		}
	}

	public MMD_VERTEX_DESC[] GetVertexDescs() {
		return m_pVertexes;
	}

	public MMD_VERTEX_DESC GetVertexDesc(int index) {
		int vindex = 0;
		if (m_indices.size() <= index) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		vindex = m_indices.get(index);
		return m_pVertexes[vindex];
	}

	public PMD_VERTEX_RECORD GetVertex(int index) {
		int vindex = 0;
		if (m_indices.size() <= index) {
			throw new IllegalArgumentException("E_INVALIDARG");
		}
		vindex = m_indices.get(index);
		return m_vertexes.get(vindex);
	}

	/**
	 * スキニング行列を適用します。<br/>
	 * 頂点分だけ処理を行うので、パフォーマンスに効いてくる部分。(と、思われる)
	 */
	public void updateSkinning() {
		MMDBone pBone = null;
		MMDBone pBone0 = null;
		MMDBone pBone1 = null;
		MMD_VERTEX_DESC pVert = null;
		MMD_VERTEX_TEXUSE buffer = new MMD_VERTEX_TEXUSE();
		MMD_VERTEX_TEXUSE destBuffer = new MMD_VERTEX_TEXUSE();
		for (int i = 0; i < m_vertexes.size(); i++) {
			pVert = m_pVertexes[i];
			if (pVert.getBweight() == 0.0f) {
				pBone = pVert.getPBones()[1];
				if (pBone == null) {
					throw new IllegalArgumentException("E_POINTER");
				}
				pVert.setCurrent(pBone.applySkinning(pVert.getFaced(buffer),
						destBuffer));
			} else if (pVert.getBweight() >= 0.9999f) {
				pBone = pVert.getPBones()[0];
				if (pBone == null) {
					throw new IllegalArgumentException("E_POINTER");
				}
				pVert.setCurrent(pBone.applySkinning(pVert.getFaced(buffer),
						destBuffer));
			} else {
				pBone0 = pVert.getPBones()[0];
				if (pBone0 == null) {
					throw new IllegalArgumentException("E_POINTER");
				}
				pBone1 = pVert.getPBones()[1];
				if (pBone1 == null) {
					throw new IllegalArgumentException("E_POINTER");
				}
				pVert.setCurrent(pBone0.ApplySkinning(pVert.getFaced(buffer),
						pBone1, pVert.getBweight(), destBuffer));
			}
		}
		return;
	}

	public void ResetVertexes() {
		MMD_VERTEX_DESC pDesc = null;
		MMD_VERTEX_TEXUSE buffer = new MMD_VERTEX_TEXUSE();
		for (int i = 0; i < m_vertexes.size(); i++) {
			pDesc = m_pVertexes[i];
			pDesc.setFaced(pDesc.getOriginal());
			pDesc.setCurrent(pDesc.getFaced(buffer));
		}
		return;
	}

	private void CreateVertexDesc() {
		m_pVertexes = new MMD_VERTEX_DESC[m_vertexes.size()];
		MMD_VERTEX_TEXUSE buffer = new MMD_VERTEX_TEXUSE();
		for (int i = 0; i < m_vertexes.size(); i++) {
			PMD_VERTEX_RECORD vert = m_vertexes.get(i);
			MMD_VERTEX_DESC pTargetVert = new MMD_VERTEX_DESC(vert);
			pTargetVert.setFaced(pTargetVert.getOriginal());
			pTargetVert.setCurrent(pTargetVert.getFaced(buffer));
			m_pVertexes[i] = pTargetVert;
		}
	}
}
