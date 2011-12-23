package net.yzwlab.javammd.format;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_MORP_RECORD {
	protected byte[] name;

	protected int frame_no;

	protected float factor;

	public VMD_MORP_RECORD() {
		this.factor = 0.0f;
		this.frame_no = 0;
		this.name = new byte[15];
	}

	public VMD_MORP_RECORD Read(ReadBuffer buffer) throws ReadException {
		name = buffer.readByteArray(15);
		frame_no = buffer.readInteger();
		factor = buffer.readFloat();
		return this;
	}

	public byte[] getName() {
		return name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	public int getFrameNo() {
		return frame_no;
	}

	public void setFrameNo(int frame_no) {
		this.frame_no = frame_no;
	}

	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
		this.factor = factor;
	}

}
