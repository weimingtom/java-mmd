package net.yzwlab.javammd.model;

import java.util.List;

import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.MMD_VERTEX_DESC;
import net.yzwlab.javammd.format.MMD_VERTEX_TEXUSE;
import net.yzwlab.javammd.format.PMD_MATERIAL_RECORD;
import net.yzwlab.javammd.format.PMD_VERTEX_RECORD;
import net.yzwlab.javammd.format.TEXTURE_DESC;

public class MMDMaterial {
	protected PMD_MATERIAL_RECORD m_material;

	protected TEXTURE_DESC m_texture;

	// protected MMD_VERTEX_TEXUSE[] m_pCurrentVertexes;

	protected MMD_VERTEX_UNIT[] m_pVertexes;

	protected boolean m_bVisible;

	public MMDMaterial(PMD_MATERIAL_RECORD pMaterial) {
		this.m_bVisible = false;
		this.m_pVertexes = null;
		this.m_texture = new TEXTURE_DESC();
		this.m_material = new PMD_MATERIAL_RECORD();
		m_pVertexes = null;
		m_bVisible = true;
		if (pMaterial == null) {
			throw new IllegalArgumentException();
		}
		m_material = pMaterial;
	}

	public int Init(MMDVertexList pVertexList, List<MMDBone> bones, int offset) {
		Integer pNextOffset = 0;
		MMD_VERTEX_DESC pOriginalVert = null;
		PMD_VERTEX_RECORD vert = new PMD_VERTEX_RECORD();
		if (pVertexList == null || pNextOffset == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		m_pVertexes = new MMD_VERTEX_UNIT[m_material.getNEdges()];
		for (int i = 0; i < m_material.getNEdges(); i++) {
			pOriginalVert = pVertexList.GetVertexDesc(i + offset);
			m_pVertexes[i] = new MMD_VERTEX_UNIT(pOriginalVert,
					new MMD_VERTEX_TEXUSE());
			m_pVertexes[i].pOriginalVert
					.copyCurrentTo(m_pVertexes[i].pCurrentVert);
			vert = pVertexList.GetVertex(i + offset);
			if (vert.getB1() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			m_pVertexes[i].pOriginalVert.getPBones()[0] = bones.get(vert
					.getB1());
			if (vert.getB2() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			m_pVertexes[i].pOriginalVert.getPBones()[1] = bones.get(vert
					.getB2());
			m_pVertexes[i].pOriginalVert.setBweight((vert.getBw() / 100.0f));
		}
		pNextOffset = offset + m_material.getNEdges();
		return pNextOffset;
	}

	public void SetVisible(boolean visible) {
		m_bVisible = visible;
		return;
	}

	public boolean IsVisible() {
		Boolean pVisible = false;
		pVisible = m_bVisible;
		return pVisible;
	}

	public void Prepare(IMMDTextureProvider pTextureProvider)
			throws ReadException {
		if (pTextureProvider == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		if (m_material.getTextureFileName()[0] == '\0') {
			return;
		}
		m_texture = pTextureProvider.Load(m_material.getTextureFileName());
	}

	public void UpdateVertexBuffer() {
		if (m_pVertexes == null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		if (m_bVisible == false) {
			return;
		}
		for (int i = 0; i < m_material.getNEdges(); i++) {
			m_pVertexes[i].pOriginalVert
					.copyCurrentTo(m_pVertexes[i].pCurrentVert);
		}
		return;
	}

	public void Draw(IGL gl) {
		float dalpha = 0.0f;
		float[] matenv = new float[4];
		MMD_VERTEX_TEXUSE pVert = null;
		if (m_bVisible == false) {
			return;
		}
		int intFrontFace;
		gl.glPushMatrix();
		intFrontFace = gl.glGetIntegerv(IGL.C.GL_FRONT_FACE);
		gl.glFrontFace(IGL.C.GL_CW);
		// dalpha = 1.0f;
		int bindGL_TEXTURE_2D = 0;
		boolean isGL_TEXTURE_2D = false;
		boolean isGL_BLEND = false;
		matenv[0] = m_material.getDiffuse().getR();
		matenv[1] = m_material.getDiffuse().getG();
		matenv[2] = m_material.getDiffuse().getB();
		matenv[3] = m_material.getDiffuse().getA() * dalpha;
		gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK, IGL.C.GL_DIFFUSE, matenv);
		matenv[0] = m_material.getAmbient().getR();
		matenv[1] = m_material.getAmbient().getG();
		matenv[2] = m_material.getAmbient().getB();
		matenv[3] = dalpha;
		gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK, IGL.C.GL_AMBIENT, matenv);
		matenv[0] = m_material.getSpecular().getR();
		matenv[1] = m_material.getSpecular().getG();
		matenv[2] = m_material.getSpecular().getB();
		matenv[3] = dalpha;
		gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK, IGL.C.GL_SPECULAR, matenv);
		matenv[0] = m_material.getAmbient().getR();
		matenv[1] = m_material.getAmbient().getG();
		matenv[2] = m_material.getAmbient().getB();
		matenv[3] = dalpha;
		gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK, IGL.C.GL_EMISSION, matenv);
		gl.glMaterialf(IGL.C.GL_FRONT_AND_BACK, IGL.C.GL_SHININESS,
				m_material.getShininess());
		gl.glEnableClientState(IGL.C.GL_VERTEX_ARRAY);
		gl.glEnableClientState(IGL.C.GL_NORMAL_ARRAY);
		if (m_texture.getTexMemHeight() > 0 && m_texture.getTexMemWidth() > 0) {
			gl.glEnableClientState(IGL.C.GL_TEXTURE_COORD_ARRAY);
		}
		isGL_TEXTURE_2D = gl.glIsEnabled(IGL.C.GL_TEXTURE_2D);
		isGL_BLEND = gl.glIsEnabled(IGL.C.GL_BLEND);
		bindGL_TEXTURE_2D = gl.glGetIntegerv(IGL.C.GL_TEXTURE_BINDING_2D);
		gl.glEnable(IGL.C.GL_TEXTURE_2D);
		gl.glEnable(IGL.C.GL_BLEND);
		gl.glBlendFunc(IGL.C.GL_SRC_ALPHA, IGL.C.GL_ONE_MINUS_SRC_ALPHA);
		if (m_texture.getTexMemHeight() > 0 && m_texture.getTexMemWidth() > 0) {
			gl.glBindTexture(IGL.C.GL_TEXTURE_2D, m_texture.getTextureId());
		}
		// pVert = m_pVertexes[0].pCurrentVert;
		// base = (pVert.point);
		// offset = ((pVert.point) - (pVert.point));
		// gl.glVertexPointer(3, IGL.C.GL_FLOAT, sizeof, base + offset);
		// if (m_texture.tex_mem_height > 0 && m_texture.tex_mem_width > 0) {
		// offset = ((pVert.uv) - (pVert.point));
		// gl.glTexCoordPointer(2, IGL.C.GL_FLOAT, sizeof, base + offset);
		// }
		// offset = ((pVert.normal) - (pVert.point));
		// gl.glNormalPointer(GL_FLOAT, sizeof, base + offset);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// gl.glDrawArrays(IGL.C.GL_TRIANGLES, 0, m_material.nEdges);
		gl.glBegin(IGL.C.GL_TRIANGLES);
		for (int i = 0; i < m_material.getNEdges(); i++) {
			pVert = m_pVertexes[i].pCurrentVert;
			gl.glTexCoord2f(pVert.getUv().getX(), pVert.getUv().getY());
			gl.glVertex3f(pVert.getPoint().getX(), pVert.getPoint().getY(),
					pVert.getPoint().getZ());
			gl.glNormal3f(pVert.getNormal().getX(), pVert.getNormal().getY(),
					pVert.getNormal().getZ());
		}
		gl.glEnd();
		gl.glBindTexture(IGL.C.GL_TEXTURE_2D, bindGL_TEXTURE_2D);
		if (isGL_BLEND == false)
			gl.glDisable(IGL.C.GL_BLEND);
		if (isGL_TEXTURE_2D == false)
			gl.glDisable(IGL.C.GL_TEXTURE_2D);
		gl.glDisableClientState(IGL.C.GL_VERTEX_ARRAY);
		gl.glDisableClientState(IGL.C.GL_NORMAL_ARRAY);
		if (m_texture.getTexMemHeight() > 0 && m_texture.getTexMemWidth() > 0) {
			gl.glDisableClientState(IGL.C.GL_TEXTURE_COORD_ARRAY);
		}
		gl.glFrontFace(gl.getGlFontFaceCode(intFrontFace));
		gl.glPopMatrix();
		return;
	}

	public void UpdateVisibility() {
		MMDBone pBone = null;
		boolean vis = false;
		if (m_pVertexes == null) {
			throw new IllegalArgumentException("E_UNEXPECTED");
		}
		vis = true;
		for (int i = 0; i < m_material.getNEdges(); i++) {
			pBone = m_pVertexes[i].pOriginalVert.getPBones()[0];
			if (pBone != null) {
				vis = pBone.IsVisible();
				if (vis == false) {
					break;
				}
			}
			pBone = m_pVertexes[i].pOriginalVert.getPBones()[1];
			if (pBone != null) {
				vis = pBone.IsVisible();
				if (vis == false) {
					break;
				}
			}
		}
		m_bVisible = vis;
		return;
	}

	public class MMD_VERTEX_UNIT {
		protected final MMD_VERTEX_DESC pOriginalVert;

		protected final MMD_VERTEX_TEXUSE pCurrentVert;

		public MMD_VERTEX_UNIT(MMD_VERTEX_DESC pOriginalVert,
				MMD_VERTEX_TEXUSE pCurrentVert) {
			this.pOriginalVert = pOriginalVert;
			this.pCurrentVert = pCurrentVert;
		}

	}

}
