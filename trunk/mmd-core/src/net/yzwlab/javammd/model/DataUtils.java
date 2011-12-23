package net.yzwlab.javammd.model;

/**
 * データ関係のユーティリティクラスです。
 */
public class DataUtils {

	/**
	 * バイトデータのオフセット部分を取得します。
	 * 
	 * @param data
	 *            データ。nullは不可。
	 * @param offset
	 *            オフセットの長さ。
	 * @return バイトデータ。
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
	 * 文字列を取得します。
	 * 
	 * @param data
	 *            バイト列。nullは不可。
	 * @return 文字列。
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
	 * 文字列を取得します。
	 * 
	 * @param data
	 *            バイト列。nullは不可。
	 * @param maxLen
	 *            最大長。
	 * @return 文字列。
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
	 * 文字列を比較します。
	 * 
	 * @param data1
	 *            バイト列。nullは不可。
	 * @param data2
	 *            バイト列。nullは不可。
	 * @param maxLen
	 *            最大長。
	 * @return 文字列。
	 */
	public static int compare(byte[] data1, byte[] data2, int maxLen) {
		if (data1 == null || data2 == null) {
			throw new IllegalArgumentException();
		}
		return getString(data1, maxLen).compareTo(getString(data2, maxLen));
	}

	/**
	 * インスタンス化を許しません。
	 */
	private DataUtils() {
		;
	}

}
