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
	 * 
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 normalize() {
		double fSqr = 0.0f;
		fSqr = 1.0f / Math.sqrt(x * x + y * y + z * z + w * w);
		x = ((float) (x * fSqr));
		y = ((float) (y * fSqr));
		z = ((float) (z * fSqr));
		w = ((float) (w * fSqr));
		return this;
	}

	/**
	 * リセットします。
	 * 
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 toZero() {
		x = (0.0f);
		y = (0.0f);
		z = (0.0f);
		w = (0.0f);
		return this;
	}

	/**
	 * 線形補間を行います。
	 * 
	 * @param pValue1
	 *            値1。nullは不可。
	 * @param pValue2
	 *            値2。nullは不可。
	 * @param weight
	 *            重み。
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 lerp(MMD_VECTOR4 pValue1, MMD_VECTOR4 pValue2,
			float weight) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException();
		}
		float qr = pValue1.getX() * pValue2.getX() + pValue1.getY()
				* pValue2.getY() + pValue1.getZ() * pValue2.getZ()
				+ pValue1.getW() * pValue2.getW();
		float t0 = 1.0f - weight;
		if (qr < 0) {
			x = (pValue1.getX() * t0 - pValue2.getX() * weight);
			y = (pValue1.getY() * t0 - pValue2.getY() * weight);
			z = (pValue1.getZ() * t0 - pValue2.getZ() * weight);
			w = (pValue1.getW() * t0 - pValue2.getW() * weight);
		} else {
			x = (pValue1.getX() * t0 + pValue2.getX() * weight);
			y = (pValue1.getY() * t0 + pValue2.getY() * weight);
			z = (pValue1.getZ() * t0 + pValue2.getZ() * weight);
			w = (pValue1.getW() * t0 + pValue2.getW() * weight);
		}
		return normalize();
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
