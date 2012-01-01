package net.yzwlab.gwtmmd.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * 入力ストリームから読み込むためのバッファです。
 */
public class InputStreamReader extends StreamBuffer {

	/**
	 * 構築します。
	 * 
	 * @param in
	 *            入力ストリーム。nullは不可。
	 * @param length
	 *            長さ。
	 */
	public InputStreamReader(InputStream in, int length) {
		super(new DataInputStream(in), length);
	}

	@Override
	public IReadBuffer createFromByteArray(byte[] data) throws ReadException {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		return new InputStreamReader(new ByteArrayInputStream(data),
				data.length);
	}

}
