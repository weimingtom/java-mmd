package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_MOTION_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] name;

	protected int frame_no;

	protected float[] pos;

	protected float[] qt;

	protected byte[] pad;

	public VMD_MOTION_RECORD() {
		this.pad = new byte[0x40];
		this.qt = new float[4];
		this.pos = new float[3];
		this.frame_no = 0;
		this.name = new byte[15];
		System.arraycopy(VMDFile.c_hokan_data, 0, pad, 0, pad.length);
	}

	public VMD_MOTION_RECORD Read(IReadBuffer buffer) throws ReadException {
		name = buffer.readByteArray(15);
		frame_no = buffer.readInteger();
		pos = buffer.readFloatArray(3);
		qt = buffer.readFloatArray(4);
		pad = buffer.readByteArray(0x40);
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

	public float[] getPos() {
		return pos;
	}

	public void setPos(float[] pos) {
		this.pos = pos;
	}

	public float[] getQt() {
		return qt;
	}

	public void setQt(float[] qt) {
		this.qt = qt;
	}

	public byte[] getPad() {
		return pad;
	}

	public void setPad(byte[] pad) {
		this.pad = pad;
	}

}
