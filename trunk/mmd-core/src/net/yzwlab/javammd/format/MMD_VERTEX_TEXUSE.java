package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VERTEX_TEXUSE {
	protected final MMD_VECTOR3 point;

	protected final MMD_VECTOR3 normal;

	protected final MMD_VECTOR2 uv;

	public MMD_VERTEX_TEXUSE() {
		this.point = new MMD_VECTOR3();
		this.normal = new MMD_VECTOR3();
		this.uv = new MMD_VECTOR2();
	}

	public void copyFrom(MMD_VERTEX_TEXUSE source) {
		this.point.copyFrom(source.point);
		this.normal.copyFrom(source.normal);
		this.uv.copyFrom(source.uv);
	}

	public MMD_VECTOR3 getPoint() {
		return point;
	}

	public void setPoint(MMD_VECTOR3 point) {
		this.point.copyFrom(point);
	}

	public MMD_VECTOR3 getNormal() {
		return normal;
	}

	public void setNormal(MMD_VECTOR3 normal) {
		this.normal.copyFrom(normal);
	}

	public MMD_VECTOR2 getUv() {
		return uv;
	}

	public MMD_VERTEX_TEXUSE Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.point.copyFrom((new MMD_VECTOR3()).Read(buffer));
		this.normal.copyFrom((new MMD_VECTOR3()).Read(buffer));
		this.uv.copyFrom((new MMD_VECTOR2()).Read(buffer));
		return this;
	}
}
