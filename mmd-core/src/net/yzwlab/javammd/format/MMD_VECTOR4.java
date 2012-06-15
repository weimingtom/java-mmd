package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VECTOR4 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected float x;

	protected float y;

	protected float z;

	protected float w;

	/**
	 * 構築します。
	 */
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
		float qr = pValue1.x * pValue2.x + pValue1.y * pValue2.y + pValue1.z
				* pValue2.z + pValue1.w * pValue2.w;
		float t0 = 1.0f - weight;
		if (qr < 0) {
			x = (pValue1.x * t0 - pValue2.x * weight);
			y = (pValue1.y * t0 - pValue2.y * weight);
			z = (pValue1.z * t0 - pValue2.z * weight);
			w = (pValue1.w * t0 - pValue2.w * weight);
		} else {
			x = (pValue1.x * t0 + pValue2.x * weight);
			y = (pValue1.y * t0 + pValue2.y * weight);
			z = (pValue1.z * t0 + pValue2.z * weight);
			w = (pValue1.w * t0 + pValue2.w * weight);
		}
		return normalize();
	}

	/**
	 * 乗算を行い、その結果を格納します。
	 * 
	 * @param pValue1
	 *            値1。nullは不可。
	 * @param pValue2
	 *            値2。nullは不可。
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 multiply(MMD_VECTOR4 pValue1, MMD_VECTOR4 pValue2) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException();
		}
		float px, py, pz, pw;
		float qx, qy, qz, qw;
		px = pValue1.x;
		py = pValue1.y;
		pz = pValue1.z;
		pw = pValue1.w;
		qx = pValue2.x;
		qy = pValue2.y;
		qz = pValue2.z;
		qw = pValue2.w;
		x = (pw * qx + px * qw + py * qz - pz * qy);
		y = (pw * qy - px * qz + py * qw + pz * qx);
		z = (pw * qz + px * qy - py * qx + pz * qw);
		w = (pw * qw - px * qx - py * qy - pz * qz);
		return this;
	}

	/**
	 * 座標軸を生成します。
	 * 
	 * @param pAxis
	 *            座標軸。nullは不可。
	 * @param rotAngle
	 *            回転角。
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 createAxis(MMD_VECTOR3 pAxis, float rotAngle) {
		if (pAxis == null) {
			throw new IllegalArgumentException();
		}
		if (Math.abs(rotAngle) < 0.0001f) {
			x = (0.0f);
			y = (0.0f);
			z = (0.0f);
			w = (1.0f);
		} else {
			rotAngle *= 0.5f;
			double fTemp = Math.sin(rotAngle);
			x = ((float) (pAxis.x * fTemp));
			y = ((float) (pAxis.y * fTemp));
			z = ((float) (pAxis.z * fTemp));
			w = ((float) (Math.cos(rotAngle)));
		}
		return this;
	}

	/**
	 * オイラー表現に変換します。
	 * 
	 * @param buf
	 *            バッファ。nullは不可。
	 * @return バッファ。
	 */
	public MMD_VECTOR3 toEuler(MMD_VECTOR3 buf) {
		if (buf == null) {
			throw new IllegalArgumentException();
		}
		float temp = 0.0f;
		float wx2 = 0.0f;
		float wy2 = 0.0f;
		float wz2 = 0.0f;
		float x2 = 0.0f;
		float xx2 = 0.0f;
		float xy2 = 0.0f;
		float xz2 = 0.0f;
		float y2 = 0.0f;
		float yRadian = 0.0f;
		float yy2 = 0.0f;
		float yz2 = 0.0f;
		float z2 = 0.0f;
		float zz2 = 0.0f;
		x2 = x + x;
		y2 = y + y;
		z2 = z + z;
		xz2 = x * z2;
		wy2 = w * y2;
		temp = -(xz2 - wy2);
		if (temp >= 1.f) {
			temp = 1.f;
		} else if (temp <= -1.f) {
			temp = -1.f;
		}
		yRadian = (float) Math.asin(temp);
		xx2 = x * x2;
		xy2 = x * y2;
		zz2 = z * z2;
		wz2 = w * z2;
		if (yRadian < 3.1415926f * 0.5f) {
			if (yRadian > -3.1415926f * 0.5f) {
				yz2 = y * z2;
				wx2 = w * x2;
				yy2 = y * y2;
				buf.x = ((float) Math.atan2((yz2 + wx2), (1.f - (xx2 + yy2))));
				buf.y = (yRadian);
				buf.z = ((float) Math.atan2((xy2 + wz2), (1.f - (yy2 + zz2))));
			} else {
				buf.x = (-(float) Math.atan2((xy2 - wz2), (1.f - (xx2 + zz2))));
				buf.y = (yRadian);
				buf.z = (0.f);
			}
		} else {
			buf.x = ((float) Math.atan2((xy2 - wz2), (1.f - (xx2 + zz2))));
			buf.y = (yRadian);
			buf.z = (0.f);
		}
		return buf;
	}

	/**
	 * 角度制限を行います。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return 自分自身。
	 */
	public MMD_VECTOR4 limitAngle(MMD_VECTOR3 buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		MMD_VECTOR3 angle = toEuler(buffer);
		if (angle.x < -3.14159f) {
			angle.x = (-3.14159f);
		}
		if (-0.002f < angle.x) {
			angle.x = (-0.002f);
		}
		angle.y = (0.0f);
		angle.z = (0.0f);
		return angle.createEuler(this);
	}

	public MMD_VECTOR4 read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.w = buffer.readFloat();
		return this;
	}

	@Override
	public String toString() {
		return "MMD_VECTOR4 [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w
				+ "]";
	}
}
