package net.yzwlab.androidmmd.io;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.io.AbstractReadBuffer;

public abstract class StreamBuffer extends AbstractReadBuffer {

	private DataInput in;

	private long position;

	private long size;

	public StreamBuffer(DataInput in, long size) {
		if (in == null) {
			throw new IllegalArgumentException();
		}
		this.in = in;
		this.position = 0;
		this.size = size;
	}

	public boolean isEOF() {
		if (position >= size) {
			return true;
		}
		return false;
	}

	public byte readByte() throws ReadException {
		try {
			return in.readByte();
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position++;
		}
	}

	public short readUByte() throws ReadException {
		try {
			return (short) in.readUnsignedByte();
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position++;
		}
	}

	public short readShort() throws ReadException {
		try {
			return Short.reverseBytes(in.readShort());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 2;
		}
	}

	public int readUShort() throws ReadException {
		try {
			return Integer.reverseBytes(in.readUnsignedShort());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 2;
		}
	}

	public int readInteger() throws ReadException {
		try {
			return Integer.reverseBytes(in.readInt());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	public long readUInt() throws ReadException {
		throw new UnsupportedOperationException();
	}

	public long readLong() throws ReadException {
		try {
			return Integer.reverseBytes(in.readInt());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	public float readFloat() throws ReadException {
		try {
			ByteBuffer bb = ByteBuffer.allocate(64);
			byte[] buf = new byte[4];
			in.readFully(buf);
			bb.put(buf);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			float f = bb.getFloat(0);
			if (f == 0.0f /* && sf != 0.0f */) {
				// Log.i("GLTest", "Test: " + f + " / " + sf);
			}
			return f;
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	public double readDouble() throws ReadException {
		try {
			ByteBuffer bb = ByteBuffer.allocate(64);
			bb.putDouble(in.readDouble());
			bb.order(ByteOrder.LITTLE_ENDIAN);
			return bb.getDouble(0);
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 8;
		}
	}

}
