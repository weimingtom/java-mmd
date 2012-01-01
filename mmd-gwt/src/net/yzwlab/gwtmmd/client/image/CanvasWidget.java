package net.yzwlab.gwtmmd.client.image;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
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
	 * コンテキストを取得します。
	 * 
	 * @param elem
	 *            要素。nullは不可。
	 * @return コンテキスト。
	 */
	private native JavaScriptObject getContext2D(Element elem) /*-{
		return elem.getContext("2d");
	}-*/;

}