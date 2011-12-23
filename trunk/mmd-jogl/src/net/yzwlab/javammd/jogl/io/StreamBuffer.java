package net.yzwlab.javammd.jogl.io;

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

	@Override
	public boolean isEOF() {
		if (position >= size) {
			return true;
		}
		return false;
	}

	@Override
	public byte readByte() throws ReadException {
		try {
			return in.readByte();
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position++;
		}
	}

	@Override
	public short readUByte() throws ReadException {
		try {
			return (short) in.readUnsignedByte();
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position++;
		}
	}

	@Override
	public short readShort() throws ReadException {
		try {
			return Short.reverseBytes(in.readShort());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 2;
		}
	}

	@Override
	public int readUShort() throws ReadException {
		try {
			return Integer.reverseBytes(in.readUnsignedShort());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 2;
		}
	}

	@Override
	public int readInteger() throws ReadException {
		try {
			return Integer.reverseBytes(in.readInt());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	@Override
	public long readUInt() throws ReadException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long readLong() throws ReadException {
		try {
			return Integer.reverseBytes(in.readInt());
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	@Override
	public float readFloat() throws ReadException {
		try {
			ByteBuffer bb = ByteBuffer.allocate(64);
			bb.putFloat(in.readFloat());
			bb.order(ByteOrder.LITTLE_ENDIAN);
			return bb.getFloat(0);
		} catch (IOException e) {
			throw new ReadException(e);
		} finally {
			position += 4;
		}
	}

	@Override
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
