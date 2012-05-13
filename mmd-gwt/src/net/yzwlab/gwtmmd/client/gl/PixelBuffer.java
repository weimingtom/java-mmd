package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.dom.client.Element;

/**
 * �s�N�Z���o�b�t�@�̒��ۂł��B
 */
public interface PixelBuffer {

	/**
	 * �����擾���܂��B
	 * 
	 * @return ���B
	 */
	public int getWidth();

	/**
	 * �������擾���܂��B
	 * 
	 * @return �����B
	 */
	public int getHeight();

	/**
	 * �s�N�Z���l���擾���܂��B
	 * 
	 * @param x
	 *            X�B
	 * @param y
	 *            Y�B
	 * @return �s�N�Z���l�B
	 */
	public int getPixel(int x, int y);

	/**
	 * �L�����o�X�ɑ΂��ĕ`�悵�܂��B
	 * 
	 * @param canvasElement
	 *            �L�����o�X�v�f�Bnull�͕s�B
	 */
	public void drawTo(Element canvasElement);

}
