package net.yzwlab.gwtmmd.client.image;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Canvas��\������E�B�W�b�g�ł��B
 */
public class CanvasWidget extends Widget {

	/**
	 * Canvas�v�f��ێ����܂��B
	 */
	private Element canvasElement;

	/**
	 * �\�z���܂��B
	 * 
	 * @param canvasElement
	 *            Canvas�v�f�Bnull�͕s�B
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
	 * �R���e�L�X�g���擾���܂��B
	 * 
	 * @return �R���e�L�X�g�B
	 */
	public JavaScriptObject getContext2D() {
		return getContext2D(canvasElement);
	}

	/**
	 * �R���e�L�X�g���擾���܂��B
	 * 
	 * @param elem
	 *            �v�f�Bnull�͕s�B
	 * @return �R���e�L�X�g�B
	 */
	private native JavaScriptObject getContext2D(Element elem) /*-{
		return elem.getContext("2d");
	}-*/;

}