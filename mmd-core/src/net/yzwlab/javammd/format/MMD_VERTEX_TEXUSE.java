package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_VERTEX_TEXUSE implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		this.point.copyFrom((new MMD_VECTOR3()).read(buffer));
		this.normal.copyFrom((new MMD_VECTOR3()).read(buffer));
		this.uv.copyFrom((new MMD_VECTOR2()).Read(buffer));
		return this;
	}

	/**
	 * GLに対して頂点などを転送します。
	 * 
	 * @param gl
	 *            転送対象。nullは不可。
	 */
	public void toGL(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		gl.glTexCoord2f(uv.x, uv.y);
		gl.glVertex3f(point.x, point.y, point.z);
		gl.glNormal3f(normal.x, normal.y, normal.z);
	}
}
