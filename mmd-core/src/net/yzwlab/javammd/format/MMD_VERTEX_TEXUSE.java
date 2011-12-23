package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VERTEX_TEXUSE {
	protected MMD_VECTOR3 point;

	protected MMD_VECTOR3 normal;

	protected MMD_VECTOR2 uv;

	public MMD_VERTEX_TEXUSE() {
		this.point = new MMD_VECTOR3();
		this.normal = new MMD_VECTOR3();
		this.uv = new MMD_VECTOR2();
	}

	public void copyFrom(MMD_VERTEX_TEXUSE source) {
		this.point = new MMD_VECTOR3(source.point);
		this.normal = new MMD_VECTOR3(source.normal);
		this.uv = new MMD_VECTOR2(source.uv);
	}

	public MMD_VECTOR3 getPoint() {
		return point;
	}

	public void setPoint(MMD_VECTOR3 point) {
		this.point = point;
	}

	public MMD_VECTOR3 getNormal() {
		return normal;
	}

	public void setNormal(MMD_VECTOR3 normal) {
		this.normal = normal;
	}

	public MMD_VECTOR2 getUv() {
		return uv;
	}

	public void setUv(MMD_VECTOR2 uv) {
		this.uv = uv;
	}

	public MMD_VERTEX_TEXUSE Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.point = (new MMD_VECTOR3()).Read(buffer);
		this.normal = (new MMD_VECTOR3()).Read(buffer);
		this.uv = (new MMD_VECTOR2()).Read(buffer);
		return this;
	}
}
