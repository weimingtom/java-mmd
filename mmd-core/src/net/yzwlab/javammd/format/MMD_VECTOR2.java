package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VECTOR2 {
	protected float x;

	protected float y;

	public MMD_VECTOR2() {
		this.x = 0.0f;
		this.y = 0.0f;
	}

	public MMD_VECTOR2(MMD_VECTOR2 source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.x = source.x;
		this.y = source.y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public MMD_VECTOR2 Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		return this;
	}
}
