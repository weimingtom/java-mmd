package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VECTOR3 {
	protected float x;

	protected float y;

	protected float z;

	/**
	 * �\�z���܂��B
	 */
	public MMD_VECTOR3() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	public MMD_VECTOR3(MMD_VECTOR3 source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
	}

	/**
	 * �x�N�g�����̃R�s�[���s���܂��B
	 * 
	 * @param source
	 *            �\�[�X�Bnull�͕s�B
	 */
	public void copyFrom(MMD_VECTOR3 source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.x = source.x;
		this.y = source.y;
		this.z = source.z;
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

	/**
	 * ���K�����܂��B
	 * 
	 * @return �������g�B
	 */
	public MMD_VECTOR3 normalize() {
		double fSqr = 0.0f;
		fSqr = 1.0f / Math.sqrt(x * x + y * y + z * z);
		x = ((float) (x * fSqr));
		y = ((float) (y * fSqr));
		z = ((float) (z * fSqr));
		return this;
	}

	/**
	 * ���Z�b�g���܂��B
	 * 
	 * @return �������g�B
	 */
	public MMD_VECTOR3 toZero() {
		x = (0.0f);
		y = (0.0f);
		z = (0.0f);
		return this;
	}

	/**
	 * �������v�Z���A�������g�Ɋi�[���܂��B
	 * 
	 * @param pValue1
	 *            �l1�Bnull�͕s�B
	 * @param pValue2
	 *            �l2�Bnull�͕s�B
	 * @return �������g�B
	 */
	public MMD_VECTOR3 subtract(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		x = (pValue1.getX() - pValue2.getX());
		y = (pValue1.getY() - pValue2.getY());
		z = (pValue1.getZ() - pValue2.getZ());
		return this;
	}

	/**
	 * ���`��Ԃ��s���܂��B
	 * 
	 * @param pValue1
	 *            �l1�Bnull�͕s�B
	 * @param pValue2
	 *            �l2�Bnull�͕s�B
	 * @param weight
	 *            �d�݁B
	 * @return �������g�B
	 */
	public MMD_VECTOR3 lerp(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2,
			float weight) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		float t0 = 0.0f;
		t0 = 1.0f - weight;
		x = (pValue1.getX() * t0 + pValue2.getX() * weight);
		y = (pValue1.getY() * t0 + pValue2.getY() * weight);
		z = (pValue1.getZ() * t0 + pValue2.getZ() * weight);
		return this;
	}

	/**
	 * ���ς��v�Z���܂��B
	 * 
	 * @param pValue2
	 *            �l�Bnull�͕s�B
	 * @return ���ρB
	 */
	public float dotProduct(MMD_VECTOR3 pValue2) {
		if (pValue2 == null) {
			throw new IllegalArgumentException();
		}
		return (x * pValue2.getX() + y * pValue2.getY() + z * pValue2.getZ());
	}

	/**
	 * ��]���܂��B
	 * 
	 * @param pMatrix
	 *            ��]�s��Bnull�͕s�B
	 * @return �������g�B
	 */
	public MMD_VECTOR3 rotate(MMD_MATRIX pMatrix) {
		if (pMatrix == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		float sourceX = x;
		float sourceY = y;
		float sourceZ = z;
		x = (sourceX * pMatrix.values[0][0] + sourceY * pMatrix.values[1][0] + sourceZ
				* pMatrix.values[2][0]);
		y = (sourceX * pMatrix.values[0][1] + sourceY * pMatrix.values[1][1] + sourceZ
				* pMatrix.values[2][1]);
		z = (sourceX * pMatrix.values[0][2] + sourceY * pMatrix.values[1][2] + sourceZ
				* pMatrix.values[2][2]);
		return this;
	}

	/**
	 * �ϊ��s���K�p���܂��B
	 * 
	 * @param pMatrix
	 *            �ϊ��s��Bnull�͕s�B
	 * @return �������g�B
	 */
	public MMD_VECTOR3 transform(MMD_MATRIX pMatrix) {
		if (pMatrix == null) {
			throw new IllegalArgumentException();
		}
		rotate(pMatrix);
		x += (pMatrix.values[3][0]);
		y += (pMatrix.values[3][1]);
		z += (pMatrix.values[3][2]);
		return this;
	}

	public MMD_VECTOR3 Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		return this;
	}
}
