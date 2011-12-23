package net.yzwlab.javammd.format;

import java.util.List;
import java.util.ArrayList;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_GRP_RECORD {
	protected short BoneNo;
	
	protected byte grp;
	
	public PMD_GRP_RECORD() {
		this.BoneNo = 0;
		this.grp = 0;
	}
	
	public short getBoneNo() {
		return BoneNo;
	}
	
	public void setBoneNo(short BoneNo) {
		this.BoneNo = BoneNo;
	}
	
	public byte getGrp() {
		return grp;
	}
	
	public void setGrp(byte grp) {
		this.grp = grp;
	}
	
	public PMD_GRP_RECORD Read(ReadBuffer buffer) throws ReadException {
		if(buffer == null) {
			throw new IllegalArgumentException();
		}
		this.BoneNo = buffer.readShort();
		this.grp = buffer.readByte();
		return this;
	}
}
