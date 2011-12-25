package net.yzwlab.gwtmmd.client.io;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.io.AbstractReadBuffer;

import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.arrays.DataView;
import org.vectomatic.arrays.Uint8Array;

public class FileReadBuffer extends AbstractReadBuffer {

	private DataView view;

	private int position;

	private boolean le;

	public FileReadBuffer(ArrayBuffer remain) {
		if (remain == null) {
			throw new IllegalArgumentException();
		}
		this.view = DataView.createDataView(remain);
		this.position = 0;
		this.le = true;
	}

	@Override
	public IReadBuffer createFromByteArray(byte[] data) throws ReadException {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		ArrayBuffer arr = ArrayBuffer.create(data.length);
		DataView dataView = DataView.createDataView(arr);
		int pos = 0;
		for (byte dt : data) {
			dataView.setUint8(pos, dt);
			pos++;
		}
		return new FileReadBuffer(arr);
	}

	@Override
	public boolean isEOF() {
		if (position >= view.getByteLength()) {
			return true;
		}
		return false;
	}

	@Override
	public byte readByte() throws ReadException {
		try {
			return view.getInt8(position);
		} finally {
			position++;
		}
	}

	@Override
	public short readUByte() throws ReadException {
		try {
			return view.getUint8(position);
		} finally {
			position++;
		}
	}

	@Override
	public short readShort() throws ReadException {
		try {
			return view.getInt16(position, le);
		} finally {
			position += 2;
		}
	}

	@Override
	public int readUShort() throws ReadException {
		try {
			return view.getUint16(position, le);
		} finally {
			position += 2;
		}
	}

	@Override
	public int readInteger() throws ReadException {
		try {
			return view.getInt32(position, le);
		} finally {
			position += 4;
		}
	}

	@Override
	public long readUInt() throws ReadException {
		try {
			return view.getUint32(position, le);
		} finally {
			position += 4;
		}
	}

	@Override
	public long readLong() throws ReadException {
		try {
			return view.getInt32(position, le);
		} finally {
			position += 4;
		}
	}

	@Override
	public float readFloat() throws ReadException {
		try {
			return view.getFloat32(position, le);
		} finally {
			position += 4;
		}
	}

	@Override
	public double readDouble() throws ReadException {
		try {
			return view.getFloat64(position, le);
		} finally {
			position += 8;
		}
	}

}
