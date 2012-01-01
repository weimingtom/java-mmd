package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_CONSTRAINT_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] szName;

	protected long ulRigidA;

	protected long ulRigidB;

	protected float[] pos;

	protected float[] rotation;

	protected float[] posLimitL;

	protected float[] posLimitU;

	protected float[] rotLimitL;

	protected float[] rotLimitU;

	protected float[] springPos;

	protected float[] springRot;

	public PMD_CONSTRAINT_RECORD() {
		this.szName = new byte[20];
		this.ulRigidA = 0L;
		this.ulRigidB = 0L;
		this.pos = new float[3];
		this.rotation = new float[3];
		this.posLimitL = new float[3];
		this.posLimitU = new float[3];
		this.rotLimitL = new float[3];
		this.rotLimitU = new float[3];
		this.springPos = new float[3];
		this.springRot = new float[3];
	}

	public byte[] getSzname() {
		return szName;
	}

	public void setSzname(byte[] szName) {
		this.szName = szName;
	}

	public long getUlrigida() {
		return ulRigidA;
	}

	public void setUlrigida(long ulRigidA) {
		this.ulRigidA = ulRigidA;
	}

	public long getUlrigidb() {
		return ulRigidB;
	}

	public void setUlrigidb(long ulRigidB) {
		this.ulRigidB = ulRigidB;
	}

	public float[] getPos() {
		return pos;
	}

	public void setPos(float[] pos) {
		this.pos = pos;
	}

	public float[] getRotation() {
		return rotation;
	}

	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	public float[] getPoslimitl() {
		return posLimitL;
	}

	public void setPoslimitl(float[] posLimitL) {
		this.posLimitL = posLimitL;
	}

	public float[] getPoslimitu() {
		return posLimitU;
	}

	public void setPoslimitu(float[] posLimitU) {
		this.posLimitU = posLimitU;
	}

	public float[] getRotlimitl() {
		return rotLimitL;
	}

	public void setRotlimitl(float[] rotLimitL) {
		this.rotLimitL = rotLimitL;
	}

	public float[] getRotlimitu() {
		return rotLimitU;
	}

	public void setRotlimitu(float[] rotLimitU) {
		this.rotLimitU = rotLimitU;
	}

	public float[] getSpringpos() {
		return springPos;
	}

	public void setSpringpos(float[] springPos) {
		this.springPos = springPos;
	}

	public float[] getSpringrot() {
		return springRot;
	}

	public void setSpringrot(float[] springRot) {
		this.springRot = springRot;
	}

	public PMD_CONSTRAINT_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.szName = buffer.readByteArray(20);
		this.ulRigidA = buffer.readLong();
		this.ulRigidB = buffer.readLong();
		this.pos = buffer.readFloatArray(3);
		this.rotation = buffer.readFloatArray(3);
		this.posLimitL = buffer.readFloatArray(3);
		this.posLimitU = buffer.readFloatArray(3);
		this.rotLimitL = buffer.readFloatArray(3);
		this.rotLimitU = buffer.readFloatArray(3);
		this.springPos = buffer.readFloatArray(3);
		this.springRot = buffer.readFloatArray(3);
		return this;
	}
}
