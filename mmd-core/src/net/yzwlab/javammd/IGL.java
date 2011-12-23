package net.yzwlab.javammd;

public interface IGL {

	public enum C {
		GL_VERTEX_ARRAY, GL_NORMAL_ARRAY, GL_TEXTURE_COORD_ARRAY, GL_FRONT_FACE, GL_CW, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_TRIANGLES, GL_TEXTURE_2D, GL_BLEND, GL_NORMALIZE, GL_FRONT_AND_BACK, GL_DIFFUSE, GL_AMBIENT, GL_SPECULAR, GL_EMISSION, GL_SHININESS, GL_TEXTURE_BINDING_2D
	}

	public C getGlFontFaceCode(int target);

	public void glFrontFace(C mode);

	public void glBegin(C mode);

	public void glEnd();

	public void glVertex3f(float x, float y, float z);

	public void glTexCoord2f(float x, float y);

	public void glNormal3f(float x, float y, float z);

	public void glBindTexture(C target, long texture);

	public void glBlendFunc(C c1, C c2);

	public void glPushMatrix();

	public void glPopMatrix();

	public void glScalef(float a1, float a2, float a3);

	public void glColor4f(float a1, float a2, float a3, float a4);

	public void glDrawArrays(C mode, int offset, int length);

	public void glEnable(C target);

	public void glDisable(C target);

	public boolean glIsEnabled(C target);

	public void glMaterialfv(C c1, C c2, float[] fv);

	public void glMaterialf(C c1, C c2, float f);

	public int glGetIntegerv(C target);

	public void glEnableClientState(C target);

	public void glDisableClientState(C target);

}
