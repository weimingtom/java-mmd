package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_COLOR3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected float r;

	protected float g;

	protected float b;

	public MMD_COLOR3() {
		this.r = 0.0f;
		this.g = 0.0f;
		this.b = 0.0f;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public MMD_COLOR3 Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.r = buffer.readFloat();
		this.g = buffer.readFloat();
		this.b = buffer.readFloat();
		return this;
	}
}