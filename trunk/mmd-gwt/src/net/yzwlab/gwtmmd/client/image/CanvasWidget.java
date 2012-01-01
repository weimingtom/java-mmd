package net.yzwlab.gwtmmd.client.image;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Canvasを表現するウィジットです。
 */
public class CanvasWidget extends Widget {

	/**
	 * Canvas要素を保持します。
	 */
	private Element canvasElement;

	/**
	 * 構築します。
	 * 
	 * @param canvasElement
	 *            Canvas要素。nullは不可。
	 */
	public CanvasWidget(Element canvasElement) {
		if (canvasElement == null) {
			throw new IllegalArgumentException();
		}
		this.canvasElement = canvasElement;
		setElement(canvasElement);
	}

	public void setWidth(int w) {
		canvasElement.setAttribute("width", String.valueOf(w));
	}

	public void setHeight(int h) {
		canvasElement.setAttribute("height", String.valueOf(h));
	}

	/**
	 * コンテキストを取得します。
	 * 
	 * @return コンテキスト。
	 */
	public JavaScriptObject getContext2D() {
		return getContext2D(canvasElement);
	}

	/**
	 * 画像を描画します。
	 * 
	 * @param image
	 *            画像。nullは不可。
	 * @param x
	 *            位置。
	 * @param y
	 *            位置。
	 */
	public void drawImage(Image image, int x, int y) {
		if (image == null) {
			throw new IllegalArgumentException();
		}
		JavaScriptObject context = getContext2D();
		drawImage(context, image.getElement(), x, y);
	}

	/**
	 * 領域を塗りつぶします。
	 * 
	 * @param color
	 *            色文字列。nullは不可。
	 * @param x
	 *            領域。
	 * @param y
	 *            領域。
	 * @param w
	 *            領域。
	 * @param h
	 *            領域。
	 */
	public void fillRect(String color, int x, int y, int w, int h) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		JavaScriptObject context = getContext2D();
		fillRect(context, color, x, y, w, h);
	}

	/**
	 * コンテキストを取得します。
	 * 
	 * @param elem
	 *            要素。nullは不可。
	 * @return コンテキスト。
	 */
	private native JavaScriptObject getContext2D(Element elem) /*-{
		return elem.getContext("2d");
	}-*/;

	/**
	 * 画像を描画します。
	 * 
	 * @param context
	 *            コンテキスト。nullは不可。
	 * @param imageObj
	 *            画像。nullは不可。
	 * @param x
	 *            座標。
	 * @param y
	 *            座標。
	 */
	private native void drawImage(JavaScriptObject context,
			JavaScriptObject imageObj, int x, int y) /*-{
		context.drawImage(imageObj, x, y);
	}-*/;

	/**
	 * 領域を塗りつぶします。
	 * 
	 * @param context
	 *            コンテキスト。nullは不可。
	 * @param color
	 *            色文字列。nullは不可。
	 * @param x
	 *            領域。
	 * @param y
	 *            領域。
	 * @param w
	 *            領域。
	 * @param h
	 *            領域。
	 */
	private native void fillRect(JavaScriptObject context, String color, int x,
			int y, int w, int h) /*-{
		context.fillStyle = color;
		context.fillRect(x, y, w, h);
	}-*/;

}