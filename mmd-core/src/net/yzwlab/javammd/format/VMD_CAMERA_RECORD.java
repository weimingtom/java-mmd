package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_CAMERA_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int frame_no;

	protected float[] pos;

	protected float[] angle;

	protected byte[] pad;

	public VMD_CAMERA_RECORD() {
		this.pad = new byte[29];
		this.angle = new float[3];
		this.pos = new float[4];
		this.frame_no = 0;
		System.arraycopy(VMDFile.c_hokan_data2, 0, pad, 0, pad.length);
	}

	public VMD_CAMERA_RECORD Read(IReadBuffer buffer) throws ReadException {
		frame_no = buffer.readInteger();
		pos = buffer.readFloatArray(4);
		angle = buffer.readFloatArray(3);
		pad = buffer.readByteArray(29);
		return this;
	}

}
