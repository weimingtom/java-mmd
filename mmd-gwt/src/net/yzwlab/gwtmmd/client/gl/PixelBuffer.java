package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.dom.client.Element;

/**
 * ピクセルバッファの抽象です。
 */
public interface PixelBuffer {

	/**
	 * 幅を取得します。
	 * 
	 * @return 幅。
	 */
	public int getWidth();

	/**
	 * 高さを取得します。
	 * 
	 * @return 高さ。
	 */
	public int getHeight();

	/**
	 * ピクセル値を取得します。
	 * 
	 * @param x
	 *            X。
	 * @param y
	 *            Y。
	 * @return ピクセル値。
	 */
	public int getPixel(int x, int y);

	/**
	 * キャンバスに対して描画します。
	 * 
	 * @param canvasElement
	 *            キャンバス要素。nullは不可。
	 */
	public void drawTo(Element canvasElement);

}
