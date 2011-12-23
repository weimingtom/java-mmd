package net.yzwlab.javammd.model;

/**
 * �f�[�^�֌W�̃��[�e�B���e�B�N���X�ł��B
 */
public class DataUtils {

	/**
	 * �o�C�g�f�[�^�̃I�t�Z�b�g�������擾���܂��B
	 * 
	 * @param data
	 *            �f�[�^�Bnull�͕s�B
	 * @param offset
	 *            �I�t�Z�b�g�̒����B
	 * @return �o�C�g�f�[�^�B
	 */
	public static byte[] offsetBytes(byte[] data, int offset) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		byte[] ret = new byte[data.length - offset];
		for (int i = offset; i < data.length; i++) {
			ret[i - offset] = data[i];
		}
		return ret;
	}

	/**
	 * ��������擾���܂��B
	 * 
	 * @param data
	 *            �o�C�g��Bnull�͕s�B
	 * @return ������B
	 */
	public static String getString(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		int len = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0) {
				len = i;
				break;
			}
		}
		return new String(data, 0, len);
	}

	/**
	 * ��������擾���܂��B
	 * 
	 * @param data
	 *            �o�C�g��Bnull�͕s�B
	 * @param maxLen
	 *            �ő咷�B
	 * @return ������B
	 */
	public static String getString(byte[] data, int maxLen) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		int len = maxLen;
		for (int i = 0; i < data.length && i < maxLen; i++) {
			if (data[i] == 0) {
				len = i;
				break;
			}
		}
		return new String(data, 0, len);
	}

	/**
	 * ��������r���܂��B
	 * 
	 * @param data1
	 *            �o�C�g��Bnull�͕s�B
	 * @param data2
	 *            �o�C�g��Bnull�͕s�B
	 * @param maxLen
	 *            �ő咷�B
	 * @return ������B
	 */
	public static int compare(byte[] data1, byte[] data2, int maxLen) {
		if (data1 == null || data2 == null) {
			throw new IllegalArgumentException();
		}
		return getString(data1, maxLen).compareTo(getString(data2, maxLen));
	}

	/**
	 * �C���X�^���X���������܂���B
	 */
	private DataUtils() {
		;
	}

}
