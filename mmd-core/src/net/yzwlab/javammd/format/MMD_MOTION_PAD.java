package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class MMD_MOTION_PAD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] cInterpolationX;

	protected byte[] cInterpolationY;

	protected byte[] cInterpolationZ;

	protected byte[] cInterpolationRot;

	public MMD_MOTION_PAD() {
		this.cInterpolationX = new byte[16];
		this.cInterpolationY = new byte[16];
		this.cInterpolationZ = new byte[16];
		this.cInterpolationRot = new byte[16];
	}

	public byte[] getCInterpolationX() {
		return cInterpolationX;
	}

	public void setCInterpolationX(byte[] cInterpolationX) {
		this.cInterpolationX = cInterpolationX;
	}

	public byte[] getCInterpolationY() {
		return cInterpolationY;
	}

	public void setCInterpolationY(byte[] cInterpolationY) {
		this.cInterpolationY = cInterpolationY;
	}

	public byte[] getCInterpolationZ() {
		return cInterpolationZ;
	}

	public void setCInterpolationZ(byte[] cInterpolationZ) {
		this.cInterpolationZ = cInterpolationZ;
	}

	public byte[] getCInterpolationRot() {
		return cInterpolationRot;
	}

	public void setCInterpolationRot(byte[] cInterpolationRot) {
		this.cInterpolationRot = cInterpolationRot;
	}

	public MMD_MOTION_PAD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.cInterpolationX = buffer.readByteArray(16);
		this.cInterpolationY = buffer.readByteArray(16);
		this.cInterpolationZ = buffer.readByteArray(16);
		this.cInterpolationRot = buffer.readByteArray(16);
		return this;
	}
}
