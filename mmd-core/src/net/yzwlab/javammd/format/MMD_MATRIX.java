package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_MATRIX {
	protected float[][] values;

	/**
	 * 構築します。
	 */
	public MMD_MATRIX() {
		this.values = new float[4][4];
	}

	public MMD_MATRIX(MMD_MATRIX source) {
		this();
		if (source == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < source.values.length; i++) {
			float[] values = source.values[i];
			float[] destValues = this.values[i];
			for (int j = 0; j < values.length; j++) {
				destValues[j] = values[j];
			}
		}
	}

	public float[][] getValues() {
		return values;
	}

	public void setValues(float[][] values) {
		this.values = values;
	}

	public MMD_MATRIX Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.values = buffer.readFloatArray(4, 4);
		return this;
	}

	/**
	 * 単位行列を生成します。
	 * 
	 * @return 自分自身。
	 */
	public MMD_MATRIX generateIdentity() {
		toZero();
		values[0][0] = 1.0f;
		values[1][1] = 1.0f;
		values[2][2] = 1.0f;
		values[3][3] = 1.0f;
		return this;
	}

	/**
	 * リセットします。
	 * 
	 * @return 自分自身。
	 */
	public MMD_MATRIX toZero() {
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				values[j][i] = 0.0f;
			}
		}
		return this;
	}

	/**
	 * 行列情報のコピーを行います。
	 * 
	 * @param source
	 *            ソース。nullは不可。
	 */
	public void copyFrom(MMD_MATRIX source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				values[j][i] = source.values[j][i];
			}
		}
	}

	/**
	 * 逆行列化します。
	 * 
	 * @param matTemp
	 *            計算用のバッファ。nullは不可。
	 * @return 自分自身。
	 */
	public MMD_MATRIX inverse(MMD_MATRIX matTemp) {
		if (matTemp == null) {
			throw new IllegalArgumentException();
		}
		matTemp.copyFrom(this);
		generateIdentity();
		for (int i = 0; i < 4; i++) {
			float buf = 1 / matTemp.values[i][i];
			for (int j = 0; j < 4; j++) {
				matTemp.values[i][j] *= buf;
				values[i][j] *= buf;
			}
			for (int j = 0; j < 4; j++) {
				if (i != j) {
					buf = matTemp.values[j][i];
					for (int k = 0; k < 4; k++) {
						matTemp.values[j][k] -= matTemp.values[i][k] * buf;
						values[j][k] -= values[i][k] * buf;
					}
				}
			}
		}
		return this;
	}

	/**
	 * クォータニオンから行列へと変換します。
	 * 
	 * @param pQuat
	 *            クォータニオン。nullは不可。
	 * @return 自分自身。
	 */
	public MMD_MATRIX fromQuaternion(MMD_VECTOR4 pQuat) {
		if (pQuat == null) {
			throw new IllegalArgumentException();
		}
		float x2 = pQuat.getX() * pQuat.getX() * 2.0f;
		float y2 = pQuat.getY() * pQuat.getY() * 2.0f;
		float z2 = pQuat.getZ() * pQuat.getZ() * 2.0f;
		float xy = pQuat.getX() * pQuat.getY() * 2.0f;
		float yz = pQuat.getY() * pQuat.getZ() * 2.0f;
		float zx = pQuat.getZ() * pQuat.getX() * 2.0f;
		float xw = pQuat.getX() * pQuat.getW() * 2.0f;
		float yw = pQuat.getY() * pQuat.getW() * 2.0f;
		float zw = pQuat.getZ() * pQuat.getW() * 2.0f;

		values[0][0] = 1.0f - y2 - z2;
		values[0][1] = xy + zw;
		values[0][2] = zx - yw;
		values[1][0] = xy - zw;
		values[1][1] = 1.0f - z2 - x2;
		values[1][2] = yz + xw;
		values[2][0] = zx + yw;
		values[2][1] = yz - xw;
		values[2][2] = 1.0f - x2 - y2;
		values[0][3] = values[1][3] = values[2][3] = values[3][0] = values[3][1] = values[3][2] = 0.0f;
		values[3][3] = 1.0f;
		return this;
	}

}
