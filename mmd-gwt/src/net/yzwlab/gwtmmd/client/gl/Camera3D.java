package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.core.client.JavaScriptObject;

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
	 * プロジェクション行列を取得します。
	 * 
	 * @param mode
	 *            モード。nullは不可。
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 * @return プロジェクション行列。
	 */
	public JavaScriptObject getProjectionMatrix(Mode mode, int width, int height);

	/**
	 * モデルビュー行列を取得します。
	 * 
	 * @param mode
	 *            モード。nullは不可。
	 * @return モデルビュー行列。
	 */
	public JavaScriptObject getModelViewMatrix(Mode mode);

}
