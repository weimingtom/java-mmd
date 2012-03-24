package net.yzwlab.androidmmd.gl;

import android.opengl.GLSurfaceView.Renderer;

/**
 * �����_���ł��B
 */
public interface Model3DRenderer extends Renderer {

	/**
	 * ���f����ݒ肵�܂��B
	 * 
	 * @param model
	 *            ���f���Bnull�͕s�B
	 */
	public void setModel(Model3D model);

	/**
	 * �J������ݒ肵�܂��B
	 * 
	 * @param camera
	 *            �J�����Bnull�͕s�B
	 */
	public void setCamera(Camera3D camera);

}
