package net.yzwlab.gwtmmd.server;

import java.io.UnsupportedEncodingException;

/**
 * 文字列化のためのユーティリティクラスです。
 */
public final class StringUtils {

	/**
	 * 文字列化します。
	 * 
	 * @param data
	 *            データ。nullは不可。
	 * @return 文字列。
	 * @throws UnsupportedEncodingException
	 *             解釈できないエンコードに関するエラー。
	 */
	public static String getString(byte[] data)
			throws UnsupportedEncodingException {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		int len = 0;
		for (byte elem : data) {
			if (elem == 0) {
				break;
			}
			len++;
		}
		return (new String(data, 0, len, "Shift_JIS"));
	}

	/**
	 * インスタンスかを許しません。
	 */
	private StringUtils() {
		;
	}

}
