package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * カメラの抽象です。
 */
public interface Camera3D {

	/**
	 * モードを定義します。
	 */
	public enum Mode {
		CENTER, LEFT, RIGHT
	}

	/**
	 * 変換を適用します。
	 * 
	 * @param mode
	 *            モード。nullは不可。
	 * @param gl
	 *            GL。nullは不可。
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 */
	public void transformProjection(Mode mode, GL10 gl, int width, int height);

	/**
	 * 変換を適用します。
	 * 
	 * @param mode
	 *            モード。nullは不可。
	 * @param gl
	 *            GL。nullは不可。
	 */
	public void transformModelView(Mode mode, GL10 gl);

}
