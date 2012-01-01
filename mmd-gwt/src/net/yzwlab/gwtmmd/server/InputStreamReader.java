package net.yzwlab.gwtmmd.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * ���̓X�g���[������ǂݍ��ނ��߂̃o�b�t�@�ł��B
 */
public class InputStreamReader extends StreamBuffer {

	/**
	 * �\�z���܂��B
	 * 
	 * @param in
	 *            ���̓X�g���[���Bnull�͕s�B
	 * @param length
	 *            �����B
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
