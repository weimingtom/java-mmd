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

/**
 * MMDのマテリアルを管理するクラスです。
 */
public class MMDMaterial {
	protected PMD_MATERIAL_RECORD m_material;

	protected TEXTURE_DESC m_texture;

	// protected MMD_VERTEX_TEXUSE[] m_pCurrentVertexes;

	protected MMD_VERTEX_UNIT[] m_pVertexes;

	protected boolean m_bVisible;

	public MMDMaterial(PMD_MATERIAL_RECORD pMaterial) {
		this.m_bVisible = false;
		this.m_pVertexes = null;
		this.m_texture = null;
		this.m_material = new PMD_MATERIAL_RECORD();
		m_pVertexes = null;
		m_bVisible = true;
		if (pMaterial == null) {
			throw new IllegalArgumentException();
		}
		m_material = pMaterial;
	}

	/**
	 * 初期化します。
	 * 
	 * @param pVertexList
	 *            頂点リスト。nullは不可。
	 * @param bones
	 *            ボーンリスト。nullは不可。
	 * @param offset
	 *            オフセット。
	 * @return 次のオフセット。
	 */
	public int init(MMDVertexList pVertexList, List<MMDBone> bones, int offset) {
		if (pVertexList == null || bones == null) {
			throw new IllegalArgumentException();
		}
		Integer pNextOffset = 0;
		MMD_VERTEX_DESC pOriginalVert = null;
		PMD_VERTEX_RECORD vert = new PMD_VERTEX_RECORD();
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
			MMDBone targetBone = bones.get(vert.getB1());
			if (targetBone == null) {
				throw new IllegalArgumentException("Bone not found: "
						+ vert.getB1());
			}
			MMDBone[] tbones = new MMDBone[2];
			tbones[0] = targetBone;
			if (vert.getB2() >= bones.size()) {
				throw new IllegalArgumentException("E_UNEXPECTED");
			}
			targetBone = bones.get(vert.getB2());
			if (targetBone == null) {
				throw new IllegalArgumentException("Bone not found: "
						+ vert.getB2());
			}
			tbones[1] = targetBone;
			m_pVertexes[i].pOriginalVert.setBones(tbones);
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

	/**
	 * テクスチャの準備を行います。
	 * 
	 * @param pTextureProvider
	 *            テクスチャを提供するインタフェース。nullは不可。
	 * @param handler
	 *            ハンドラ。nullは不可。
	 * @throws ReadException
	 *             読み込み失敗時のエラー。
	 */
	public void prepare(IMMDTextureProvider pTextureProvider,
			final IMMDTextureProvider.Handler handler) throws ReadException {
		if (pTextureProvider == null || handler == null) {
			throw new IllegalArgumentException();
		}
		if (m_material.getTextureFileName()[0] == '\0') {
			return;
		}
		pTextureProvider.load(m_material.getTextureFileName(),
				new IMMDTextureProvider.Handler() {

					@Override
					public void onSuccess(byte[] filename, TEXTURE_DESC desc) {
						if (filename == null || desc == null) {
							throw new IllegalArgumentException();
						}
						setTexture(desc);
						handler.onSuccess(filename, desc);
					}

					@Override
					public void onError(byte[] filename, Throwable error) {
						if (filename == null || error == null) {
							throw new IllegalArgumentException();
						}
						handler.onError(filename, error);
					}

				});
	}

	/**
	 * 頂点バッファを更新します。
	 */
	public void updateVertexBuffer() {
		if (m_bVisible == false) {
			return;
		}
		if (m_pVertexes == null) {
			throw new IllegalStateException();
		}
		for (MMD_VERTEX_UNIT vert : m_pVertexes) {
			vert.pOriginalVert.copyCurrentTo(vert.pCurrentVert);
		}
	}

	/**
	 * マテリアルの内容を描画します。
	 * 
	 * @param gl
	 *            描画対象プラットフォーム。nullは不可。
	 */
	public synchronized void draw(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		float dalpha = 0.0f;
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

		float[] matenv = new float[4];
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
		if (m_texture != null) {
			gl.glEnableClientState(IGL.C.GL_TEXTURE_COORD_ARRAY);
		}
		isGL_TEXTURE_2D = gl.glIsEnabled(IGL.C.GL_TEXTURE_2D);
		isGL_BLEND = gl.glIsEnabled(IGL.C.GL_BLEND);
		bindGL_TEXTURE_2D = gl.glGetIntegerv(IGL.C.GL_TEXTURE_BINDING_2D);
		gl.glEnable(IGL.C.GL_TEXTURE_2D);
		gl.glEnable(IGL.C.GL_BLEND);
		gl.glBlendFunc(IGL.C.GL_SRC_ALPHA, IGL.C.GL_ONE_MINUS_SRC_ALPHA);
		if (m_texture != null) {
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
		gl.glBegin(IGL.C.GL_TRIANGLES, m_pVertexes.length);
		for (MMD_VERTEX_UNIT unit : m_pVertexes) {
			unit.pCurrentVert.toGL(gl);
		}
		gl.glEnd();
		gl.glBindTexture(IGL.C.GL_TEXTURE_2D, bindGL_TEXTURE_2D);
		if (isGL_BLEND == false)
			gl.glDisable(IGL.C.GL_BLEND);
		if (isGL_TEXTURE_2D == false)
			gl.glDisable(IGL.C.GL_TEXTURE_2D);
		gl.glDisableClientState(IGL.C.GL_VERTEX_ARRAY);
		gl.glDisableClientState(IGL.C.GL_NORMAL_ARRAY);
		if (m_texture != null) {
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
			pBone = m_pVertexes[i].pOriginalVert.getBones()[0];
			if (pBone != null) {
				vis = pBone.IsVisible();
				if (vis == false) {
					break;
				}
			}
			pBone = m_pVertexes[i].pOriginalVert.getBones()[1];
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

	/**
	 * テクスチャを設定します。
	 * 
	 * @param texture
	 *            テクスチャ。nullは不可。
	 */
	private synchronized void setTexture(TEXTURE_DESC texture) {
		if (texture == null) {
			throw new IllegalArgumentException();
		}
		m_texture = texture;
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
