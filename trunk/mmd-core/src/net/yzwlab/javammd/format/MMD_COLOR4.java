package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_COLOR4 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected float r;

	protected float g;

	protected float b;

	protected float a;

	public MMD_COLOR4() {
		this.r = 0.0f;
		this.g = 0.0f;
		this.b = 0.0f;
		this.a = 0.0f;
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

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public MMD_COLOR4 Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.r = buffer.readFloat();
		this.g = buffer.readFloat();
		this.b = buffer.readFloat();
		this.a = buffer.readFloat();
		return this;
	}
}