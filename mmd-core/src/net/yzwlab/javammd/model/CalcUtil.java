package net.yzwlab.javammd.model;

import net.yzwlab.javammd.format.MMD_MATRIX;
import net.yzwlab.javammd.format.MMD_VECTOR3;
import net.yzwlab.javammd.format.MMD_VECTOR4;

public class CalcUtil {

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
		pDest.setValues(matTemp);
		return pDest;
	}

	public static MMD_MATRIX Lerp(MMD_MATRIX pValue1, MMD_MATRIX pValue2,
			float weight) {
		if (pValue1 == null || pValue2 == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		MMD_MATRIX pDest = new MMD_MATRIX();
		float[][] fOut = null;
		float[][] fSrc1 = null;
		float[][] fSrc2 = null;
		fOut = (pDest.getValues());
		fSrc1 = (pValue1.getValues());
		fSrc2 = (pValue2.getValues());
		float rev = 1.0f - weight;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				fOut[x][y] = fSrc1[x][y] * weight + fSrc2[x][y] * rev;
			}
		}
		return pDest;
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
		if (pSource == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		MMD_VECTOR3 angle = ToEuler(pSource);
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
