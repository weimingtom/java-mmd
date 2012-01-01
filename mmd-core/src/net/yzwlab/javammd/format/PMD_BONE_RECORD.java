package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_BONE_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] name;

	protected short parent;

	protected short to;

	protected byte kind;

	protected short knum;

	protected float[] pos;

	public PMD_BONE_RECORD() {
		this.name = new byte[20];
		this.parent = 0;
		this.to = 0;
		this.kind = 0;
		this.knum = 0;
		this.pos = new float[3];
	}

	public byte[] getName() {
		return name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	public short getParent() {
		return parent;
	}

	public void setParent(short parent) {
		this.parent = parent;
	}

	public short getTo() {
		return to;
	}

	public void setTo(short to) {
		this.to = to;
	}

	public byte getKind() {
		return kind;
	}

	public void setKind(byte kind) {
		this.kind = kind;
	}

	public short getKnum() {
		return knum;
	}

	public void setKnum(short knum) {
		this.knum = knum;
	}

	public float[] getPos() {
		return pos;
	}

	public void setPos(float[] pos) {
		this.pos = pos;
	}

	public PMD_BONE_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.name = buffer.readByteArray(20);
		this.parent = buffer.readShort();
		this.to = buffer.readShort();
		this.kind = buffer.readByte();
		this.knum = buffer.readShort();
		this.pos = buffer.readFloatArray(3);
		return this;
	}
}
