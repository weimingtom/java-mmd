package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_MATRIX {
	protected float[][] values;

	/**
	 * �\�z���܂��B
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
	 * �P�ʍs��𐶐����܂��B
	 * 
	 * @return �������g�B
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
	 * ���Z�b�g���܂��B
	 * 
	 * @return �������g�B
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
	 * �s����̃R�s�[���s���܂��B
	 * 
	 * @param source
	 *            �\�[�X�Bnull�͕s�B
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
	 * �t�s�񉻂��܂��B
	 * 
	 * @param matTemp
	 *            �v�Z�p�̃o�b�t�@�Bnull�͕s�B
	 * @return �������g�B
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

}
