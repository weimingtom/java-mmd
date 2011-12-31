package net.yzwlab.javammd.image;

/**
 * �摜��\������C���^�t�F�[�X�ł��B
 */
public interface IImage {

	/**
	 * �������`���܂��B
	 */
	public enum Direction {
		HORIZONTAL, VERTICAL
	}

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
	 * ���]�������s���܂��B
	 * 
	 * @param dir
	 *            �����Bnull�͕s�B
	 * @return �摜�B
	 */
	public IImage flip(Direction dir);

}
