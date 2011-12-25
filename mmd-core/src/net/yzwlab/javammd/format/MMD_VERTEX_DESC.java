package net.yzwlab.javammd.format;

import net.yzwlab.javammd.model.MMDBone;

public class MMD_VERTEX_DESC {
	private final MMD_VERTEX_TEXUSE original;

	private final MMD_VERTEX_TEXUSE faced;

	private final MMD_VERTEX_TEXUSE current;

	protected MMDBone[] pBones;

	protected float bweight;

	public MMD_VERTEX_DESC(PMD_VERTEX_RECORD vert) {
		if (vert == null) {
			throw new IllegalArgumentException();
		}
		this.original = new MMD_VERTEX_TEXUSE();
		this.faced = new MMD_VERTEX_TEXUSE();
		this.current = new MMD_VERTEX_TEXUSE();
		this.pBones = new MMDBone[2];
		this.bweight = 0.0f;

		this.original.point.x = vert.x;
		this.original.point.y = vert.y;
		this.original.point.z = vert.z;
		this.original.normal.x = vert.nx;
		this.original.normal.y = vert.ny;
		this.original.normal.z = vert.nz;
		this.original.uv.x = vert.tx;
		this.original.uv.y = vert.ty;
	}

	public void setCurrent(MMD_VERTEX_TEXUSE current) {
		if (current == null) {
			throw new IllegalArgumentException();
		}
		this.current.copyFrom(current);
	}

	public void copyCurrentTo(MMD_VERTEX_TEXUSE target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		target.copyFrom(current);
	}

	public MMD_VERTEX_TEXUSE getOriginal() {
		MMD_VERTEX_TEXUSE r = new MMD_VERTEX_TEXUSE();
		r.copyFrom(original);
		return r;
	}

	public MMD_VERTEX_TEXUSE getFaced(MMD_VERTEX_TEXUSE buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		buffer.copyFrom(faced);
		return buffer;
	}

	public void setFaced(MMD_VERTEX_TEXUSE current) {
		if (current == null) {
			throw new IllegalArgumentException();
		}
		this.faced.copyFrom(current);
	}

	public void setFaced(PMD_MORP_VERTEX_RECORD v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		float[] values = v.getVec();
		MMD_VECTOR3 point = faced.getPoint();
		point.x = (values[0]);
		point.y = (values[1]);
		point.z = (values[2]);
	}

	public void setFaced(MMD_VECTOR3 v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		faced.getPoint().copyFrom(v);
	}

	public MMDBone[] getPBones() {
		return pBones;
	}

	public void setPBones(MMDBone[] pBones) {
		this.pBones = pBones;
	}

	public float getBweight() {
		return bweight;
	}

	public void setBweight(float bweight) {
		this.bweight = bweight;
	}

}
