package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_RIGID_BODY_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] szName;

	protected short unBoneIndex;

	protected byte cbColGroupIndex;

	protected short unColGroupMask;

	protected byte cbShapeType;

	protected float fWidth;

	protected float fHeight;

	protected float fDepth;

	protected float[] pos;

	protected float[] rotation;

	protected float fMass;

	protected float fLinearDamping;

	protected float fAngularDamping;

	protected float fRestitution;

	protected float fFriction;

	protected byte cbRigidBodyType;

	public PMD_RIGID_BODY_RECORD() {
		this.szName = new byte[20];
		this.unBoneIndex = 0;
		this.cbColGroupIndex = 0;
		this.unColGroupMask = 0;
		this.cbShapeType = 0;
		this.fWidth = 0.0f;
		this.fHeight = 0.0f;
		this.fDepth = 0.0f;
		this.pos = new float[3];
		this.rotation = new float[3];
		this.fMass = 0.0f;
		this.fLinearDamping = 0.0f;
		this.fAngularDamping = 0.0f;
		this.fRestitution = 0.0f;
		this.fFriction = 0.0f;
		this.cbRigidBodyType = 0;
	}

	public byte[] getSzname() {
		return szName;
	}

	public void setSzname(byte[] szName) {
		this.szName = szName;
	}

	public short getUnboneindex() {
		return unBoneIndex;
	}

	public void setUnboneindex(short unBoneIndex) {
		this.unBoneIndex = unBoneIndex;
	}

	public byte getCbcolgroupindex() {
		return cbColGroupIndex;
	}

	public void setCbcolgroupindex(byte cbColGroupIndex) {
		this.cbColGroupIndex = cbColGroupIndex;
	}

	public short getUncolgroupmask() {
		return unColGroupMask;
	}

	public void setUncolgroupmask(short unColGroupMask) {
		this.unColGroupMask = unColGroupMask;
	}

	public byte getCbshapetype() {
		return cbShapeType;
	}

	public void setCbshapetype(byte cbShapeType) {
		this.cbShapeType = cbShapeType;
	}

	public float getFwidth() {
		return fWidth;
	}

	public void setFwidth(float fWidth) {
		this.fWidth = fWidth;
	}

	public float getFheight() {
		return fHeight;
	}

	public void setFheight(float fHeight) {
		this.fHeight = fHeight;
	}

	public float getFdepth() {
		return fDepth;
	}

	public void setFdepth(float fDepth) {
		this.fDepth = fDepth;
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

	public float getFmass() {
		return fMass;
	}

	public void setFmass(float fMass) {
		this.fMass = fMass;
	}

	public float getFlineardamping() {
		return fLinearDamping;
	}

	public void setFlineardamping(float fLinearDamping) {
		this.fLinearDamping = fLinearDamping;
	}

	public float getFangulardamping() {
		return fAngularDamping;
	}

	public void setFangulardamping(float fAngularDamping) {
		this.fAngularDamping = fAngularDamping;
	}

	public float getFrestitution() {
		return fRestitution;
	}

	public void setFrestitution(float fRestitution) {
		this.fRestitution = fRestitution;
	}

	public float getFfriction() {
		return fFriction;
	}

	public void setFfriction(float fFriction) {
		this.fFriction = fFriction;
	}

	public byte getCbrigidbodytype() {
		return cbRigidBodyType;
	}

	public void setCbrigidbodytype(byte cbRigidBodyType) {
		this.cbRigidBodyType = cbRigidBodyType;
	}

	public PMD_RIGID_BODY_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.szName = buffer.readByteArray(20);
		this.unBoneIndex = buffer.readShort();
		this.cbColGroupIndex = buffer.readByte();
		this.unColGroupMask = buffer.readShort();
		this.cbShapeType = buffer.readByte();
		this.fWidth = buffer.readFloat();
		this.fHeight = buffer.readFloat();
		this.fDepth = buffer.readFloat();
		this.pos = buffer.readFloatArray(3);
		this.rotation = buffer.readFloatArray(3);
		this.fMass = buffer.readFloat();
		this.fLinearDamping = buffer.readFloat();
		this.fAngularDamping = buffer.readFloat();
		this.fRestitution = buffer.readFloat();
		this.fFriction = buffer.readFloat();
		this.cbRigidBodyType = buffer.readByte();
		return this;
	}
}
