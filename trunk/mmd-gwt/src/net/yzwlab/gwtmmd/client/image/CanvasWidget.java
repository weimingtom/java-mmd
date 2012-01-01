package net.yzwlab.gwtmmd.client.image;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;
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
	 * �摜��`�悵�܂��B
	 * 
	 * @param image
	 *            �摜�Bnull�͕s�B
	 * @param x
	 *            �ʒu�B
	 * @param y
	 *            �ʒu�B
	 */
	public void drawImage(Image image, int x, int y) {
		if (image == null) {
			throw new IllegalArgumentException();
		}
		JavaScriptObject context = getContext2D();
		drawImage(context, image.getElement(), x, y);
	}

	/**
	 * �̈��h��Ԃ��܂��B
	 * 
	 * @param color
	 *            �F������Bnull�͕s�B
	 * @param x
	 *            �̈�B
	 * @param y
	 *            �̈�B
	 * @param w
	 *            �̈�B
	 * @param h
	 *            �̈�B
	 */
	public void fillRect(String color, int x, int y, int w, int h) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		JavaScriptObject context = getContext2D();
		fillRect(context, color, x, y, w, h);
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

	/**
	 * �摜��`�悵�܂��B
	 * 
	 * @param context
	 *            �R���e�L�X�g�Bnull�͕s�B
	 * @param imageObj
	 *            �摜�Bnull�͕s�B
	 * @param x
	 *            ���W�B
	 * @param y
	 *            ���W�B
	 */
	private native void drawImage(JavaScriptObject context,
			JavaScriptObject imageObj, int x, int y) /*-{
		context.drawImage(imageObj, x, y);
	}-*/;

	/**
	 * �̈��h��Ԃ��܂��B
	 * 
	 * @param context
	 *            �R���e�L�X�g�Bnull�͕s�B
	 * @param color
	 *            �F������Bnull�͕s�B
	 * @param x
	 *            �̈�B
	 * @param y
	 *            �̈�B
	 * @param w
	 *            �̈�B
	 * @param h
	 *            �̈�B
	 */
	private native void fillRect(JavaScriptObject context, String color, int x,
			int y, int w, int h) /*-{
		context.fillStyle = color;
		context.fillRect(x, y, w, h);
	}-*/;

}