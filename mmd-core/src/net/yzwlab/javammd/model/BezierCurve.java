package net.yzwlab.javammd.model;

import net.yzwlab.javammd.format.MMD_VECTOR2;

public class BezierCurve {
	protected float[] m_values;

	protected boolean m_bLinear;

	public static float CalcYValue(float x, MMD_VECTOR2 p1, MMD_VECTOR2 p2) {
		Float pValue = 0.0f;
		float invt = 0.0f;
		float t = 0.0f;
		float tempx = 0.0f;
		if (p1 == null || p2 == null || pValue == null) {
			throw new IllegalArgumentException("E_POINTER");
		}
		t = x;
		invt = 1.0f - t;
		for (int i = 0; i < 32; i++) {
			tempx = invt * invt * t * p1.getX() + invt * t * t * p2.getX() + t
					* t * t;
			tempx -= x;
			if (Math.abs(tempx) < 0.0001f) {
				break;
			} else {
				t -= tempx * 0.5f;
				invt = 1.0f - t;
			}
		}
		pValue = invt * invt * t * p1.getY() + invt * t * t * p2.getY() * t * t
				* t;
		return pValue;
	}

	public BezierCurve(MMD_VECTOR2 p1, MMD_VECTOR2 p2) {
		this.m_bLinear = false;
		this.m_values = new float[16 + 1];
		float addX = 0.0f;
		MMD_VECTOR2 np1 = new MMD_VECTOR2();
		MMD_VECTOR2 np2 = new MMD_VECTOR2();
		float v = 0.0f;
		m_bLinear = false;
		for (int i = 0; i < m_values.length; i++) {
			m_values[i] = 0;
		}
		if (p1.getX() == p1.getY() && p2.getX() == p2.getY()) {
			m_bLinear = true;
		} else {
			np1.setX((p1.getX() / 127.0f) * 3.0f);
			np1.setY((p1.getY() / 127.0f) * 3.0f);
			np2.setX((p2.getX() / 127.0f) * 3.0f);
			np2.setY((p2.getX() / 127.0f) * 3.0f); // TODO p2.y?
			m_values[0] = 0.0f;
			m_values[16] = 1.0f;
			addX = 1.0f / 16;
			for (int i = 1; i < 16; i++) {
				v = BezierCurve.CalcYValue(addX * i, np1, np2);
				m_values[i] = v;
			}
		}
	}

	public void dispose() {
		;
	}

	public float GetValue(float x) {
		Float pY = 0.0f;
		float dx = 0.0f;
		int index = 0;
		float offset = 0.0f;
		if (m_bLinear) {
			pY = x;
			return pY;
		}
		dx = x * 16;
		index = (int) dx;
		offset = dx - index;
		pY = m_values[index] * (1.0f - offset) + m_values[index + 1] * offset;
		return pY;
	}

}
