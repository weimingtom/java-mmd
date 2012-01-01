package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_LIGHT_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int frame_no;

	protected float[] pos;

	protected float[] direction;

	public VMD_LIGHT_RECORD() {
		this.direction = new float[3];
		this.pos = new float[3];
		this.frame_no = 0;
	}

	public VMD_LIGHT_RECORD Read(IReadBuffer buffer) throws ReadException {
		frame_no = buffer.readInteger();
		pos = buffer.readFloatArray(3);
		direction = buffer.readFloatArray(3);
		return this;
	}

}
