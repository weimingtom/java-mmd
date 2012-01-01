package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_MORP_VERTEX_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int no;

	protected float[] vec;

	public PMD_MORP_VERTEX_RECORD() {
		this.no = 0;
		this.vec = new float[3];
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public float[] getVec() {
		return vec;
	}

	public void setVec(float[] vec) {
		this.vec = vec;
	}

	public PMD_MORP_VERTEX_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.no = buffer.readInteger();
		this.vec = buffer.readFloatArray(3);
		return this;
	}
}
