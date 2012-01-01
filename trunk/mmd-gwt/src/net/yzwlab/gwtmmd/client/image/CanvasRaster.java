package net.yzwlab.gwtmmd.client.image;

import net.yzwlab.javammd.image.IImage;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Canvas�̃��X�^���C�Y���ꂽ���ł��B
 */
public interface CanvasRaster extends IImage {

	/**
	 * �Ώۂɑ΂��Ă��̃f�[�^��`�悵�܂��B
	 * 
	 * @param canvasContext
	 *            �L�����o�X�R���e�L�X�g�Bnull�͕s�B
	 * @param x
	 *            �]����X�B
	 * @param y
	 *            �]����Y�B
	 */
	public void renderTo(JavaScriptObject canvasContext, int x, int y);

}
