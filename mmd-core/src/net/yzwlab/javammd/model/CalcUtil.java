package net.yzwlab.javammd.model;

import net.yzwlab.javammd.format.MMD_MATRIX;
import net.yzwlab.javammd.format.MMD_VECTOR3;
import net.yzwlab.javammd.format.MMD_VECTOR4;

public class CalcUtil {
	public static MMD_MATRIX ToZeroM() {
		MMD_MATRIX pMatrix = new MMD_MATRIX();
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				pMatrix.getValues()[j][i] = 0.0f;
			}
		}
		return pMatrix;
	}

	public static MMD_VECTOR3 ToZeroV3() {
		MMD_VECTOR3 pVector = new MMD_VECTOR3();
		pVector.setX(0.0f);
		pVector.setY(0.0f);
		pVector.setZ(0.0f);
		return pVector;
	}

	public static MMD_VECTOR4 ToZeroV4() {
		MMD_VECTOR4 pVector = new MMD_VECTOR4();
		pVector.setX(0.0f);
		pVector.setY(0.0f);
		pVector.setZ(0.0f);
		pVector.setW(0.0f);
		return pVector;
	}

	public static MMD_MATRIX GenerateIdentity() {
		MMD_MATRIX pMatrix = ToZeroM();
		pMatrix.getValues()[0][0] = 1.0f;
		pMatrix.getValues()[1][1] = 1.0f;
		pMatrix.getValues()[2][2] = 1.0f;
		pMatrix.getValues()[3][3] = 1.0f;
		return pMatrix;
	}

	public static MMD_VECTOR3 Subtract(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		MMD_VECTOR3 temp = new MMD_VECTOR3();
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		temp.setX(pValue1.getX() - pValue2.getX());
		temp.setY(pValue1.getY() - pValue2.getY());
		temp.setZ(pValue1.getZ() - pValue2.getZ());
		pDest.setX(temp.getX());
		pDest.setY(temp.getY());
		pDest.setZ(temp.getZ());
		return pDest;
	}

	public static MMD_VECTOR3 Add(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		MMD_VECTOR3 temp = new MMD_VECTOR3();
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		temp.setX(pValue1.getX() + pValue2.getX());
		temp.setY(pValue1.getY() + pValue2.getY());
		temp.setZ(pValue1.getZ() + pValue2.getZ());
		pDest.setX(temp.getX());
		pDest.setY(temp.getY());
		pDest.setZ(temp.getZ());
		return pDest;
	}

	public static MMD_VECTOR4 Multiply(MMD_VECTOR4 pValue1, MMD_VECTOR4 pValue2) {
		MMD_VECTOR4 pDest = new MMD_VECTOR4();
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		float px, py, pz, pw;
		float qx, qy, qz, qw;
		px = pValue1.getX();
		py = pValue1.getY();
		pz = pValue1.getZ();
		pw = pValue1.getW();
		qx = pValue2.getX();
		qy = pValue2.getY();
		qz = pValue2.getZ();
		qw = pValue2.getW();
		pDest.setX(pw * qx + px * qw + py * qz - pz * qy);
		pDest.setY(pw * qy - px * qz + py * qw + pz * qx);
		pDest.setZ(pw * qz + px * qy - py * qx + pz * qw);
		pDest.setW(pw * qw - px * qx - py * qy - pz * qz);
		return pDest;
	}

	public static MMD_MATRIX Multiply(MMD_MATRIX pValue1, MMD_MATRIX pValue2) {
		MMD_MATRIX pDest = new MMD_MATRIX();
		float[][] matTemp = new float[4][4];
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		for (int i = 0; i < 4; i++) {
			float[][] values1 = pValue1.getValues();
			float[][] values2 = pValue2.getValues();
			matTemp[i][0] = values1[i][0] * values2[0][0] + values1[i][1]
					* values2[1][0] + values1[i][2] * values2[2][0]
					+ values1[i][3] * values2[3][0];
			matTemp[i][1] = values1[i][0] * values2[0][1] + values1[i][1]
					* values2[1][1] + values1[i][2] * values2[2][1]
					+ values1[i][3] * values2[3][1];
			matTemp[i][2] = values1[i][0] * values2[0][2] + values1[i][1]
					* values2[1][2] + values1[i][2] * values2[2][2]
					+ values1[i][3] * values2[3][2];
			matTemp[i][3] = values1[i][0] * values2[0][3] + values1[i][1]
					* values2[1][3] + values1[i][2] * values2[2][3]
					+ values1[i][3] * values2[3][3];
		}
		for (int i = 0; i < 4; i++) {
			pDest.getValues()[i][0] = matTemp[i][0];
			pDest.getValues()[i][1] = matTemp[i][1];
			pDest.getValues()[i][2] = matTemp[i][2];
			pDest.getValues()[i][3] = matTemp[i][3];
		}
		return pDest;
	}

	public static MMD_VECTOR3 Normalize(MMD_VECTOR3 pSource) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		if (pDest == null || pSource == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		double fSqr = 0.0f;
		fSqr = 1.0f / Math.sqrt(pSource.getX() * pSource.getX()
				+ pSource.getY() * pSource.getY() + pSource.getZ()
				* pSource.getZ());
		pDest.setX((float) (pSource.getX() * fSqr));
		pDest.setY((float) (pSource.getY() * fSqr));
		pDest.setZ((float) (pSource.getZ() * fSqr));
		return pDest;
	}

	public static MMD_VECTOR4 Normalize(MMD_VECTOR4 pSource) {
		MMD_VECTOR4 pDest = new MMD_VECTOR4();
		double fSqr = 0.0f;
		if (pDest == null || pSource == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		fSqr = 1.0f / Math.sqrt(pSource.getX() * pSource.getX()
				+ pSource.getY() * pSource.getY() + pSource.getZ()
				* pSource.getZ() + pSource.getW() * pSource.getW());
		pDest.setX((float) (pSource.getX() * fSqr));
		pDest.setY((float) (pSource.getY() * fSqr));
		pDest.setZ((float) (pSource.getZ() * fSqr));
		pDest.setW((float) (pSource.getW() * fSqr));
		return pDest;
	}

	public static MMD_VECTOR3 Lerp(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2,
			float weight) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		float t0 = 0.0f;
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		t0 = 1.0f - weight;
		pDest.setX(pValue1.getX() * t0 + pValue2.getX() * weight);
		pDest.setY(pValue1.getY() * t0 + pValue2.getY() * weight);
		pDest.setZ(pValue1.getZ() * t0 + pValue2.getZ() * weight);
		return pDest;
	}

	public static MMD_VECTOR4 Lerp(MMD_VECTOR4 pValue1, MMD_VECTOR4 pValue2,
			float weight) {
		MMD_VECTOR4 pDest = new MMD_VECTOR4();
		float qr = 0.0f;
		float t0 = 0.0f;
		MMD_VECTOR4 temp = new MMD_VECTOR4();
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		qr = pValue1.getX() * pValue2.getX() + pValue1.getY() * pValue2.getY()
				+ pValue1.getZ() * pValue2.getZ() + pValue1.getW()
				* pValue2.getW();
		t0 = 1.0f - weight;
		if (qr < 0) {
			temp.setX(pValue1.getX() * t0 - pValue2.getX() * weight);
			temp.setY(pValue1.getY() * t0 - pValue2.getY() * weight);
			temp.setZ(pValue1.getZ() * t0 - pValue2.getZ() * weight);
			temp.setW(pValue1.getW() * t0 - pValue2.getW() * weight);
		} else {
			temp.setX(pValue1.getX() * t0 + pValue2.getX() * weight);
			temp.setY(pValue1.getY() * t0 + pValue2.getY() * weight);
			temp.setZ(pValue1.getZ() * t0 + pValue2.getZ() * weight);
			temp.setW(pValue1.getW() * t0 + pValue2.getW() * weight);
		}
		return Normalize(temp);
	}

	public static MMD_MATRIX Lerp(MMD_MATRIX pValue1, MMD_MATRIX pValue2,
			float weight) {
		MMD_MATRIX pDest = new MMD_MATRIX();
		float[][] fOut = null;
		float[][] fSrc1 = null;
		float[][] fSrc2 = null;
		float rev = 0.0f;
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		fOut = (pDest.getValues());
		fSrc1 = (pValue1.getValues());
		fSrc2 = (pValue2.getValues());
		rev = 1.0f - weight;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				fOut[x][y] = fSrc1[x][y] * weight + fSrc2[x][y] * rev;
			}
		}
		return pDest;
	}

	public static MMD_MATRIX Inverse(MMD_MATRIX pSource) {
		MMD_MATRIX pDest = new MMD_MATRIX();
		float buf = 0.0f;
		MMD_MATRIX matTemp = new MMD_MATRIX();
		if (pDest == null || pSource == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		pDest = GenerateIdentity();
		for (int i = 0; i < 4; i++) {
			buf = 1 / matTemp.getValues()[i][i];
			for (int j = 0; j < 4; j++) {
				matTemp.getValues()[i][j] *= buf;
				pDest.getValues()[i][j] *= buf;
			}
			for (int j = 0; j < 4; j++) {
				if (i != j) {
					buf = matTemp.getValues()[j][i];
					for (int k = 0; k < 4; k++) {
						matTemp.getValues()[j][k] -= matTemp.getValues()[i][k]
								* buf;
						pDest.getValues()[j][k] -= pDest.getValues()[i][k]
								* buf;
					}
				}
			}
		}
		return pDest;
	}

	public static float DotProduct(MMD_VECTOR3 pValue1, MMD_VECTOR3 pValue2) {
		Float dp = 0.0f;
		if (dp == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		dp = (pValue1.getX() * pValue2.getX() + pValue1.getY() * pValue2.getY() + pValue1
				.getZ() * pValue2.getZ());
		return dp;
	}

	public static MMD_VECTOR3 CrossProduct(MMD_VECTOR3 pValue1,
			MMD_VECTOR3 pValue2) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		if (pDest == null || pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		pDest.setX(pValue1.getY() * pValue2.getZ() - pValue1.getZ()
				* pValue2.getY());
		pDest.setY(pValue1.getZ() * pValue2.getX() - pValue1.getX()
				* pValue2.getZ());
		pDest.setZ(pValue1.getX() * pValue2.getY() - pValue1.getY()
				* pValue2.getX());
		return pDest;
	}

	public static MMD_MATRIX QuaternionToMatrix(MMD_VECTOR4 pQuat) {
		MMD_MATRIX pDest = new MMD_MATRIX();
		float x2 = 0.0f;
		float xw = 0.0f;
		float xy = 0.0f;
		float y2 = 0.0f;
		float yw = 0.0f;
		float yz = 0.0f;
		float z2 = 0.0f;
		float zw = 0.0f;
		float zx = 0.0f;
		if (pDest == null || pQuat == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		x2 = pQuat.getX() * pQuat.getX() * 2.0f;
		y2 = pQuat.getY() * pQuat.getY() * 2.0f;
		z2 = pQuat.getZ() * pQuat.getZ() * 2.0f;
		xy = pQuat.getX() * pQuat.getY() * 2.0f;
		yz = pQuat.getY() * pQuat.getZ() * 2.0f;
		zx = pQuat.getZ() * pQuat.getX() * 2.0f;
		xw = pQuat.getX() * pQuat.getW() * 2.0f;
		yw = pQuat.getY() * pQuat.getW() * 2.0f;
		zw = pQuat.getZ() * pQuat.getW() * 2.0f;
		pDest.getValues()[0][0] = 1.0f - y2 - z2;
		pDest.getValues()[0][1] = xy + zw;
		pDest.getValues()[0][2] = zx - yw;
		pDest.getValues()[1][0] = xy - zw;
		pDest.getValues()[1][1] = 1.0f - z2 - x2;
		pDest.getValues()[1][2] = yz + xw;
		pDest.getValues()[2][0] = zx + yw;
		pDest.getValues()[2][1] = yz - xw;
		pDest.getValues()[2][2] = 1.0f - x2 - y2;
		pDest.getValues()[0][3] = pDest.getValues()[1][3] = pDest.getValues()[2][3] = pDest
				.getValues()[3][0] = pDest.getValues()[3][1] = pDest
				.getValues()[3][2] = 0.0f;
		pDest.getValues()[3][3] = 1.0f;
		return pDest;
	}

	public static MMD_VECTOR3 Transform(MMD_VECTOR3 pSource, MMD_MATRIX pMatrix) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		MMD_VECTOR3 temp = new MMD_VECTOR3();
		if (pDest == null || pSource == null || pMatrix == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		temp = Rotate(pSource, pMatrix);
		pDest.setX(temp.getX() + pMatrix.getValues()[3][0]);
		pDest.setY(temp.getY() + pMatrix.getValues()[3][1]);
		pDest.setZ(temp.getZ() + pMatrix.getValues()[3][2]);
		return pDest;
	}

	public static MMD_VECTOR3 Rotate(MMD_VECTOR3 pSource, MMD_MATRIX pMatrix) {
		MMD_VECTOR3 pDest = new MMD_VECTOR3();
		MMD_VECTOR3 temp = new MMD_VECTOR3();
		if (pDest == null || pSource == null || pMatrix == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		temp.setX(pSource.getX() * pMatrix.getValues()[0][0] + pSource.getY()
				* pMatrix.getValues()[1][0] + pSource.getZ()
				* pMatrix.getValues()[2][0]);
		temp.setY(pSource.getX() * pMatrix.getValues()[0][1] + pSource.getY()
				* pMatrix.getValues()[1][1] + pSource.getZ()
				* pMatrix.getValues()[2][1]);
		temp.setZ(pSource.getX() * pMatrix.getValues()[0][2] + pSource.getY()
				* pMatrix.getValues()[1][2] + pSource.getZ()
				* pMatrix.getValues()[2][2]);
		pDest.setX(temp.getX());
		pDest.setY(temp.getY());
		pDest.setZ(temp.getZ());
		return pDest;
	}

	public static MMD_VECTOR4 CreateAxis(MMD_VECTOR3 pAxis, float rotAngle) {
		MMD_VECTOR4 pDest = new MMD_VECTOR4();
		double fTemp = 0.0f;
		if (pDest == null || pAxis == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		if (Math.abs(rotAngle) < 0.0001f) {
			pDest.setX(0.0f);
			pDest.setY(0.0f);
			pDest.setZ(0.0f);
			pDest.setW(1.0f);
		} else {
			rotAngle *= 0.5f;
			fTemp = Math.sin(rotAngle);
			pDest.setX((float) (pAxis.getX() * fTemp));
			pDest.setY((float) (pAxis.getY() * fTemp));
			pDest.setZ((float) (pAxis.getZ() * fTemp));
			pDest.setW((float) (Math.cos(rotAngle)));
		}
		return pDest;
	}

	public static MMD_VECTOR3 ToEuler(MMD_VECTOR4 pQuat) {
		MMD_VECTOR3 pAngle = new MMD_VECTOR3();
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
		if (pAngle == null || pQuat == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		x2 = pQuat.getX() + pQuat.getX();
		y2 = pQuat.getY() + pQuat.getY();
		z2 = pQuat.getZ() + pQuat.getZ();
		xz2 = pQuat.getX() * z2;
		wy2 = pQuat.getW() * y2;
		temp = -(xz2 - wy2);
		if (temp >= 1.f) {
			temp = 1.f;
		} else if (temp <= -1.f) {
			temp = -1.f;
		}
		yRadian = (float) Math.asin(temp);
		xx2 = pQuat.getX() * x2;
		xy2 = pQuat.getX() * y2;
		zz2 = pQuat.getZ() * z2;
		wz2 = pQuat.getW() * z2;
		if (yRadian < 3.1415926f * 0.5f) {
			if (yRadian > -3.1415926f * 0.5f) {
				yz2 = pQuat.getY() * z2;
				wx2 = pQuat.getW() * x2;
				yy2 = pQuat.getY() * y2;
				pAngle.setX((float) Math
						.atan2((yz2 + wx2), (1.f - (xx2 + yy2))));
				pAngle.setY(yRadian);
				pAngle.setZ((float) Math
						.atan2((xy2 + wz2), (1.f - (yy2 + zz2))));
			} else {
				pAngle.setX(-(float) Math.atan2((xy2 - wz2),
						(1.f - (xx2 + zz2))));
				pAngle.setY(yRadian);
				pAngle.setZ(0.f);
			}
		} else {
			pAngle.setX((float) Math.atan2((xy2 - wz2), (1.f - (xx2 + zz2))));
			pAngle.setY(yRadian);
			pAngle.setZ(0.f);
		}
		return pAngle;
	}

	public static MMD_VECTOR4 CreateEuler(MMD_VECTOR3 pAngle) {
		MMD_VECTOR4 pQuat = new MMD_VECTOR4();
		float cosX = 0.0f;
		float cosY = 0.0f;
		float cosZ = 0.0f;
		float sinX = 0.0f;
		float sinY = 0.0f;
		float sinZ = 0.0f;
		float xRadian = 0.0f;
		float yRadian = 0.0f;
		float zRadian = 0.0f;
		if (pAngle == null || pQuat == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		xRadian = pAngle.getX() * 0.5f;
		yRadian = pAngle.getY() * 0.5f;
		zRadian = pAngle.getZ() * 0.5f;
		sinX = (float) Math.sin(xRadian);
		cosX = (float) Math.cos(xRadian);
		sinY = (float) Math.sin(yRadian);
		cosY = (float) Math.cos(yRadian);
		sinZ = (float) Math.sin(zRadian);
		cosZ = (float) Math.cos(zRadian);
		pQuat.setX(sinX * cosY * cosZ - cosX * sinY * sinZ);
		pQuat.setY(cosX * sinY * cosZ + sinX * cosY * sinZ);
		pQuat.setZ(cosX * cosY * sinZ - sinX * sinY * cosZ);
		pQuat.setW(cosX * cosY * cosZ + sinX * sinY * sinZ);
		return pQuat;
	}

	public static MMD_VECTOR4 LimitAngle(MMD_VECTOR4 pSource) {
		MMD_VECTOR4 pDest = new MMD_VECTOR4();
		MMD_VECTOR3 angle = new MMD_VECTOR3();
		if (pDest == null || pSource == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		angle = ToEuler(pSource);
		if (angle.getX() < -3.14159f) {
			angle.setX(-3.14159f);
		}
		if (-0.002f < angle.getX()) {
			angle.setX(-0.002f);
		}
		angle.setY(0.0f);
		angle.setZ(0.0f);
		return CreateEuler(angle);
	}

	public CalcUtil() {
	}

	public void dispose() {
	}

}
