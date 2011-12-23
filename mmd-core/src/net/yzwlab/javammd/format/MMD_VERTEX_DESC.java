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

	public MMD_VERTEX_TEXUSE getFaced() {
		MMD_VERTEX_TEXUSE r = new MMD_VERTEX_TEXUSE();
		r.copyFrom(faced);
		return r;
	}

	public void setFaced(MMD_VERTEX_TEXUSE current) {
		if (current == null) {
			throw new IllegalArgumentException();
		}
		this.faced.copyFrom(current);
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
