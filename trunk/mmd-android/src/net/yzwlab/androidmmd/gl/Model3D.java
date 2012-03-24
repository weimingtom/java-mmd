package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * 3Dモデルの抽象です。
 */
public interface Model3D {

	/**
	 * ハンドラを定義します。
	 */
	public interface Handler {

		/**
		 * 変更を通知します。
		 * 
		 * @param source
		 *            変更元。nullは不可。
		 */
		public void updated(Model3D source);

	}

	/**
	 * ハンドラを追加します。
	 * 
	 * @param handler
	 *            ハンドラ。nullは不可。
	 */
	public void addHandler(Handler handler);

	/**
	 * ハンドラを削除します。
	 * 
	 * @param handler
	 *            ハンドラ。nullは不可。
	 */
	public void removeHandler(Handler handler);

	/**
	 * 準備します。
	 * 
	 * @param gl
	 *            GL。nullは不可。
	 */
	public void prepare(GL10 gl);

	/**
	 * 描画します。
	 * 
	 * @param gl
	 *            GL。nullは不可。
	 */
	public void draw(GL10 gl);

}
