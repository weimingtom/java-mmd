package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * 行列を表現するクラスです。
 */
public class MMD_MATRIX implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public MMD_MATRIX read(IReadBuffer buffer) throws ReadException {
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
		float x2 = pQuat.x * pQuat.x * 2.0f;
		float y2 = pQuat.y * pQuat.y * 2.0f;
		float z2 = pQuat.z * pQuat.z * 2.0f;
		float xy = pQuat.x * pQuat.y * 2.0f;
		float yz = pQuat.y * pQuat.z * 2.0f;
		float zx = pQuat.z * pQuat.x * 2.0f;
		float xw = pQuat.x * pQuat.w * 2.0f;
		float yw = pQuat.y * pQuat.w * 2.0f;
		float zw = pQuat.z * pQuat.w * 2.0f;

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

	/**
	 * 乗算を行い、その結果を格納します。
	 * 
	 * @param pValue1
	 *            値1。nullは不可。
	 * @param pValue2
	 *            値2。nullは不可。
	 * @return 自分自身。
	 */
	public MMD_MATRIX multiply(MMD_MATRIX pValue1, MMD_MATRIX pValue2) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException();
		}
		float[][] values1 = pValue1.values;
		float[][] values2 = pValue2.values;
		for (int i = 0; i < 4; i++) {
			values[i][0] = values1[i][0] * values2[0][0] + values1[i][1]
					* values2[1][0] + values1[i][2] * values2[2][0]
					+ values1[i][3] * values2[3][0];
			values[i][1] = values1[i][0] * values2[0][1] + values1[i][1]
					* values2[1][1] + values1[i][2] * values2[2][1]
					+ values1[i][3] * values2[3][1];
			values[i][2] = values1[i][0] * values2[0][2] + values1[i][1]
					* values2[1][2] + values1[i][2] * values2[2][2]
					+ values1[i][3] * values2[3][2];
			values[i][3] = values1[i][0] * values2[0][3] + values1[i][1]
					* values2[1][3] + values1[i][2] * values2[2][3]
					+ values1[i][3] * values2[3][3];
		}
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
	public MMD_MATRIX lerp(MMD_MATRIX pValue1, MMD_MATRIX pValue2, float weight) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException();
		}
		float[][] fSrc1 = (pValue1.values);
		float[][] fSrc2 = (pValue2.values);
		float rev = 1.0f - weight;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				values[x][y] = fSrc1[x][y] * weight + fSrc2[x][y] * rev;
			}
		}
		return this;
	}

}
