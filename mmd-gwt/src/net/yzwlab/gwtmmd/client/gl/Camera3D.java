package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * �J�����̒��ۂł��B
 */
public interface Camera3D {

	/**
	 * ���[�h���`���܂��B
	 */
	public enum Mode {
		CENTER, LEFT, RIGHT
	}

	/**
	 * �v���W�F�N�V�����s����擾���܂��B
	 * 
	 * @param mode
	 *            ���[�h�Bnull�͕s�B
	 * @param width
	 *            ���B
	 * @param height
	 *            �����B
	 * @return �v���W�F�N�V�����s��B
	 */
	public JavaScriptObject getProjectionMatrix(Mode mode, int width, int height);

	/**
	 * ���f���r���[�s����擾���܂��B
	 * 
	 * @param mode
	 *            ���[�h�Bnull�͕s�B
	 * @return ���f���r���[�s��B
	 */
	public JavaScriptObject getModelViewMatrix(Mode mode);

	public int getCurrentRx();

	public void setCurrentRx(int currentRx);

	public int getCurrentRy();

	public void setCurrentRy(int currentRy);

	public int getCurrentRz();

	public void setCurrentRz(int currentRz);

}
