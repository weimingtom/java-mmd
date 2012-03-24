package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * 3D���f���̒��ۂł��B
 */
public interface Model3D {

	/**
	 * �n���h�����`���܂��B
	 */
	public interface Handler {

		/**
		 * �ύX��ʒm���܂��B
		 * 
		 * @param source
		 *            �ύX���Bnull�͕s�B
		 */
		public void updated(Model3D source);

	}

	/**
	 * �n���h����ǉ����܂��B
	 * 
	 * @param handler
	 *            �n���h���Bnull�͕s�B
	 */
	public void addHandler(Handler handler);

	/**
	 * �n���h�����폜���܂��B
	 * 
	 * @param handler
	 *            �n���h���Bnull�͕s�B
	 */
	public void removeHandler(Handler handler);

	/**
	 * �������܂��B
	 * 
	 * @param gl
	 *            GL�Bnull�͕s�B
	 */
	public void prepare(GL10 gl);

	/**
	 * �`�悵�܂��B
	 * 
	 * @param gl
	 *            GL�Bnull�͕s�B
	 */
	public void draw(GL10 gl);

}
