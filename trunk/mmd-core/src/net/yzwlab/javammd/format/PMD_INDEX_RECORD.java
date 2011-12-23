package net.yzwlab.javammd.format;

import java.util.List;
import java.util.ArrayList;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_INDEX_RECORD {
	protected short id;
	
	public PMD_INDEX_RECORD() {
		this.id = 0;
	}
	
	public short getId() {
		return id;
	}
	
	public void setId(short id) {
		this.id = id;
	}
	
	public PMD_INDEX_RECORD Read(ReadBuffer buffer) throws ReadException {
		if(buffer == null) {
			throw new IllegalArgumentException();
		}
		this.id = buffer.readShort();
		return this;
	}
}
