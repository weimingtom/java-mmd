package net.yzwlab.gwtmmd.client.image;

import net.yzwlab.javammd.image.IImage;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Canvasのラスタライズされた情報です。
 */
public interface CanvasRaster extends IImage {

	/**
	 * 対象に対してこのデータを描画します。
	 * 
	 * @param canvasContext
	 *            キャンバスコンテキスト。nullは不可。
	 * @param x
	 *            転送先X。
	 * @param y
	 *            転送先Y。
	 */
	public void renderTo(JavaScriptObject canvasContext, int x, int y);

}
