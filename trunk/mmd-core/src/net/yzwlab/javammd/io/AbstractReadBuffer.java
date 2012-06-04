package net.yzwlab.javammd.io;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public abstract class AbstractReadBuffer implements IReadBuffer {

	public AbstractReadBuffer() {
		;
	}

	@Override
	public byte[] readLine() throws ReadException {
		final int MAX_LINE = 512;
		byte[] data = new byte[MAX_LINE];
		int i = 0;
		int phase = 0;
		for (i = 0; i < data.length && isEOF() == false; i++) {
			data[i] = readByte();
			if (data[i] == '\r') {
				phase++;
			} else if (data[i] == '\n') {
				i -= phase;
				break;
			} else if (phase > 0) {
				throw new ReadException("Unexpected return code: "
						+ String.valueOf((int) '\r'));
			}
		}
		byte[] r = new byte[i];
		for (int j = 0; j < r.length; j++) {
			r[j] = data[j];
		}
		return r;
	}

	@Override
	public byte[] readByteArray(int length) throws ReadException {
		byte[] data = new byte[length];
		for (int i = 0; i < length; i++) {
			data[i] = readByte();
		}
		return data;
	}

	@Override
	public byte[][] readByteArray(int len1, int len2) throws ReadException {
		byte[][] data = new byte[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readByteArray(len2);
		}
		return data;
	}

	@Override
	public short[] readUByteArray(int length) throws ReadException {
		short[] data = new short[length];
		for (int i = 0; i < length; i++) {
			data[i] = readUByte();
		}
		return data;
	}

	@Override
	public short[][] readUByteArray(int len1, int len2) throws ReadException {
		short[][] data = new short[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readUByteArray(len2);
		}
		return data;
	}

	@Override
	public short[] readShortArray(int length) throws ReadException {
		short[] data = new short[length];
		for (int i = 0; i < length; i++) {
			data[i] = readShort();
		}
		return data;
	}

	@Override
	public short[][] readShortArray(int len1, int len2) throws ReadException {
		short[][] data = new short[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readShortArray(len2);
		}
		return data;
	}

	@Override
	public int[] readUShortArray(int length) throws ReadException {
		int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = readUShort();
		}
		return data;
	}

	@Override
	public int[][] readUShortArray(int len1, int len2) throws ReadException {
		int[][] data = new int[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readUShortArray(len2);
		}
		return data;
	}

	@Override
	public int[] readIntegerArray(int length) throws ReadException {
		int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = readInteger();
		}
		return data;
	}

	@Override
	public int[][] readIntegerArray(int len1, int len2) throws ReadException {
		int[][] data = new int[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readIntegerArray(len2);
		}
		return data;
	}

	@Override
	public long[] readUIntArray(int length) throws ReadException {
		long[] data = new long[length];
		for (int i = 0; i < length; i++) {
			data[i] = readUInt();
		}
		return data;
	}

	@Override
	public long[][] readUIntArray(int len1, int len2) throws ReadException {
		long[][] data = new long[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readUIntArray(len2);
		}
		return data;
	}

	@Override
	public long[] readLongArray(int length) throws ReadException {
		long[] data = new long[length];
		for (int i = 0; i < length; i++) {
			data[i] = readLong();
		}
		return data;
	}

	@Override
	public long[][] readLongArray(int len1, int len2) throws ReadException {
		long[][] data = new long[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readLongArray(len2);
		}
		return data;
	}

	@Override
	public float[] readFloatArray(int length) throws ReadException {
		float[] data = new float[length];
		for (int i = 0; i < length; i++) {
			data[i] = readFloat();
		}
		return data;
	}

	@Override
	public float[][] readFloatArray(int len1, int len2) throws ReadException {
		float[][] data = new float[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readFloatArray(len2);
		}
		return data;
	}

	@Override
	public double[] readDoubleArray(int length) throws ReadException {
		double[] data = new double[length];
		for (int i = 0; i < length; i++) {
			data[i] = readDouble();
		}
		return data;
	}

	@Override
	public double[][] readDoubleArray(int len1, int len2) throws ReadException {
		double[][] data = new double[len1][len2];
		for (int i = 0; i < len1; i++) {
			data[i] = readDoubleArray(len2);
		}
		return data;
	}

}
