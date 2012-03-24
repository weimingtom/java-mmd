package net.yzwlab.androidmmd.gl;

import android.opengl.GLSurfaceView.Renderer;

/**
 * レンダラです。
 */
public interface Model3DRenderer extends Renderer {

	/**
	 * モデルを設定します。
	 * 
	 * @param model
	 *            モデル。nullは不可。
	 */
	public void setModel(Model3D model);

	/**
	 * カメラを設定します。
	 * 
	 * @param camera
	 *            カメラ。nullは不可。
	 */
	public void setCamera(Camera3D camera);

}
