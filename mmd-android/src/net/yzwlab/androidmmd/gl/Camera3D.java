package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * �J�����̒��ۂł��B
 */
public interface Camera3D {

	/**
	 * ���[�h���`���܂��B
	 */
	public enum Mode {
		CENTER, LEFT, RIGHT
	}

	/**
	 * �ϊ���K�p���܂��B
	 * 
	 * @param mode
	 *            ���[�h�Bnull�͕s�B
	 * @param gl
	 *            GL�Bnull�͕s�B
	 * @param width
	 *            ���B
	 * @param height
	 *            �����B
	 */
	public void transformProjection(Mode mode, GL10 gl, int width, int height);

	/**
	 * �ϊ���K�p���܂��B
	 * 
	 * @param mode
	 *            ���[�h�Bnull�͕s�B
	 * @param gl
	 *            GL�Bnull�͕s�B
	 */
	public void transformModelView(Mode mode, GL10 gl);

}
