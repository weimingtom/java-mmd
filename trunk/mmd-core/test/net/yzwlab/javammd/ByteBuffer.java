package net.yzwlab.javammd;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteBuffer extends StreamBuffer {

	public ByteBuffer(byte[] data) {
		super(new DataInputStream(
				new ByteArrayInputStream(data)), data.length);
	}

	@Override
	public IReadBuffer createFromByteArray(byte[] data) throws ReadException {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		return new ByteBuffer(data);
	}

}
