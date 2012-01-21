package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.format.MMD_MATRIX;
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

	/**
	 * 検証します。
	 */
	public void verify() {
		int index = 0;
		for (MMD_VERTEX_DESC desc : m_pVertexes) {
			System.out.println("Verifying: " + index);
			desc.verify();
			index ++;
		}
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
		MMD_VERTEX_TEXUSE buffer = new MMD_VERTEX_TEXUSE();
		MMD_VERTEX_TEXUSE destBuffer = new MMD_VERTEX_TEXUSE();
		MMD_MATRIX skinningBuffer = new MMD_MATRIX();
		for (MMD_VERTEX_DESC pVert : m_pVertexes) {
			MMDBone[] bones = pVert.getBones();
			if(bones == null) {
				continue;
			}
			if (pVert.getBweight() == 0.0f) {
				MMDBone pBone = bones[1];
				if (pBone == null) {
					throw new IllegalStateException();
				}
				pVert.setCurrent(pBone.applySkinning(pVert.getFaced(buffer),
						destBuffer));
			} else if (pVert.getBweight() >= 0.9999f) {
				MMDBone pBone = bones[0];
				if (pBone == null) {
					throw new IllegalStateException();
				}
				pVert.setCurrent(pBone.applySkinning(pVert.getFaced(buffer),
						destBuffer));
			} else {
				MMDBone pBone0 = bones[0];
				if (pBone0 == null) {
					throw new IllegalStateException();
				}
				MMDBone pBone1 = bones[1];
				if (pBone1 == null) {
					throw new IllegalStateException();
				}
				pVert.setCurrent(pBone0.applySkinning(pVert.getFaced(buffer),
						pBone1, pVert.getBweight(), destBuffer, skinningBuffer));
			}
		}
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
