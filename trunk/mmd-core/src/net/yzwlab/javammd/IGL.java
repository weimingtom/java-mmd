package net.yzwlab.javammd;

public interface IGL {

	public enum C {
		GL_VERTEX_ARRAY, GL_NORMAL_ARRAY, GL_TEXTURE_COORD_ARRAY, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_TRIANGLES, GL_TEXTURE_2D, GL_BLEND, GL_NORMALIZE, GL_FRONT_AND_BACK, GL_DIFFUSE, GL_AMBIENT, GL_SPECULAR, GL_EMISSION, GL_SHININESS
	}

	public enum FrontFace {
		GL_CW;
	}

	/**
	 * 現在描画中のリソースコンテキストを取得します。 1つの描画領域しかない場合は0のみ。複数の描画領域がある場合は0ベースで取得できます。
	 * 
	 * @return リソースコンテキスト。
	 */
	public int getResourceContext();

	public FrontFace glGetFrontFace();

	public void glFrontFace(FrontFace mode);

	/**
	 * レンダリングを開始します。
	 * 
	 * @param mode
	 *            レンダリングモード。nullは不可。
	 * @param vertices
	 *            頂点数。
	 */
	public void glBegin(C mode, int vertices);

	public void glEnd();

	public void glVertex3f(float x, float y, float z);

	public void glTexCoord2f(float x, float y);

	public void glNormal3f(float x, float y, float z);

	public void glBindTexture(C target, long texture);

	public long glGetBindTexture(C target);

	public void glBlendFunc(C c1, C c2);

	public void glPushMatrix();

	public void glPopMatrix();

	public void glScalef(float a1, float a2, float a3);

	public void glColor4f(float a1, float a2, float a3, float a4);

	public void glEnable(C target);

	public void glDisable(C target);

	public boolean glIsEnabled(C target);

	public void glMaterialfv(C c1, C c2, float[] fv);

	public void glMaterialf(C c1, C c2, float f);

	public void glEnableClientState(C target);

	public void glDisableClientState(C target);

}
