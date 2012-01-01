package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VECTOR2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	/**
	 * ベクトル情報のコピーを行います。
	 * 
	 * @param source
	 *            ソース。nullは不可。
	 */
	public void copyFrom(MMD_VECTOR2 source) {
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
