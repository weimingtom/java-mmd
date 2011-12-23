package net.yzwlab.javammd.format;

import java.util.List;
import java.util.ArrayList;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_GRP_NAME_RECORD {
	protected byte[] name;
	
	public PMD_GRP_NAME_RECORD() {
		this.name = new byte[50];
	}
	
	public byte[] getName() {
		return name;
	}
	
	public void setName(byte[] name) {
		this.name = name;
	}
	
	public PMD_GRP_NAME_RECORD Read(ReadBuffer buffer) throws ReadException {
		if(buffer == null) {
			throw new IllegalArgumentException();
		}
		this.name = buffer.readByteArray(50);
		return this;
	}
}
