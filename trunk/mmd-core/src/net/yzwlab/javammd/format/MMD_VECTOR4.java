package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VECTOR4 {
	protected float x;

	protected float y;

	protected float z;

	protected float w;

	public MMD_VECTOR4() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.w = 0.0f;
	}

	public MMD_VECTOR4(MMD_VECTOR4 source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
		this.w = source.w;
	}

	/**
	 * ベクトル情報のコピーを行います。
	 * 
	 * @param source
	 *            ソース。nullは不可。
	 */
	public void copyFrom(MMD_VECTOR4 source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
		this.w = source.w;
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

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	/**
	 * ベクトルを正規化します。
	 */
	public void Normalize() {
		double fSqr = 0.0f;
		fSqr = 1.0f / Math.sqrt(x * x + y * y + z * z + w * w);
		x = ((float) (x * fSqr));
		y = ((float) (y * fSqr));
		z = ((float) (z * fSqr));
		w = ((float) (w * fSqr));
	}

	public MMD_VECTOR4 Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.w = buffer.readFloat();
		return this;
	}
}
