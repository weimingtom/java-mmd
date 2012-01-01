package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_VERTEX_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected float x;

	protected float y;

	protected float z;

	protected float nx;

	protected float ny;

	protected float nz;

	protected float tx;

	protected float ty;

	protected short b1;

	protected short b2;

	protected byte bw;

	protected byte unknown;

	public PMD_VERTEX_RECORD() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.nx = 0.0f;
		this.ny = 0.0f;
		this.nz = 0.0f;
		this.tx = 0.0f;
		this.ty = 0.0f;
		this.b1 = 0;
		this.b2 = 0;
		this.bw = 0;
		this.unknown = 0;
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

	public float getNx() {
		return nx;
	}

	public void setNx(float nx) {
		this.nx = nx;
	}

	public float getNy() {
		return ny;
	}

	public void setNy(float ny) {
		this.ny = ny;
	}

	public float getNz() {
		return nz;
	}

	public void setNz(float nz) {
		this.nz = nz;
	}

	public float getTx() {
		return tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	public float getTy() {
		return ty;
	}

	public void setTy(float ty) {
		this.ty = ty;
	}

	public short getB1() {
		return b1;
	}

	public void setB1(short b1) {
		this.b1 = b1;
	}

	public short getB2() {
		return b2;
	}

	public void setB2(short b2) {
		this.b2 = b2;
	}

	public byte getBw() {
		return bw;
	}

	public void setBw(byte bw) {
		this.bw = bw;
	}

	public byte getUnknown() {
		return unknown;
	}

	public void setUnknown(byte unknown) {
		this.unknown = unknown;
	}

	public PMD_VERTEX_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.x = buffer.readFloat();
		this.y = buffer.readFloat();
		this.z = buffer.readFloat();
		this.nx = buffer.readFloat();
		this.ny = buffer.readFloat();
		this.nz = buffer.readFloat();
		this.tx = buffer.readFloat();
		this.ty = buffer.readFloat();
		this.b1 = buffer.readShort();
		this.b2 = buffer.readShort();
		this.bw = buffer.readByte();
		this.unknown = buffer.readByte();
		return this;
	}
}
