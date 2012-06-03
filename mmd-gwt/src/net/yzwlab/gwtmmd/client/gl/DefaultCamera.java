package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * カメラの定義です。
 */
public class DefaultCamera implements Camera3D {

	/**
	 * 回転を保持します。
	 */
	private int currentRx;

	/**
	 * 回転を保持します。
	 */
	private int currentRy;

	/**
	 * 回転を保持します。
	 */
	private int currentRz;

	/**
	 * 構築します。
	 */
	public DefaultCamera() {
		this.currentRx = 0;
		this.currentRy = 0;
		this.currentRz = 0;
	}

	public int getCurrentRx() {
		return currentRx;
	}

	public void setCurrentRx(int currentRx) {
		this.currentRx = currentRx;
	}

	public int getCurrentRy() {
		return currentRy;
	}

	public void setCurrentRy(int currentRy) {
		this.currentRy = currentRy;
	}

	public int getCurrentRz() {
		return currentRz;
	}

	public void setCurrentRz(int currentRz) {
		this.currentRz = currentRz;
	}

	@Override
	public JavaScriptObject getProjectionMatrix(Mode mode, int width, int height) {
		return createPMatrix(width, height);
	}

	@Override
	public JavaScriptObject getModelViewMatrix(Mode mode) {
		return createMVMatrix(currentRx, currentRy, currentRz);
	}

	/**
	 * Perspective行列を生成します。
	 * 
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 * @return Perspective行列。
	 */
	private native JavaScriptObject createPMatrix(int width, int height) /*-{
		var persp = $wnd.mat4.create();
		$wnd.mat4.perspective(45, width / height, 1, 100, persp);
		return persp;
	}-*/;

	/**
	 * ModelView行列を生成します。
	 * 
	 * @param rx
	 *            回転。
	 * @param ry
	 *            回転。
	 * @param rz
	 *            回転。
	 * @return ModelView行列。
	 */
	private native JavaScriptObject createMVMatrix(int rx, int ry, int rz) /*-{
		var modelView = $wnd.mat4.create();

		$wnd.mat4.identity(modelView); // Set to identity
		$wnd.mat4.translate(modelView, [ 0, -5, -15 ]); // Translate back 10 units
		$wnd.mat4.rotate(modelView, Math.PI / 8 * rx, [ 1, 0, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(modelView, Math.PI / 8 * ry, [ 0, 1, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(modelView, Math.PI / 8 * rz, [ 0, 0, 1 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.scale(modelView, [ 0.5, 0.5, 0.5 ]); // Scale by 200%
		return modelView;
	}-*/;

}
