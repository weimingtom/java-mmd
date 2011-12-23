package net.yzwlab.javammd.format;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_HEADER {
	protected byte[] hdr_string;

	protected byte[] unknown;

	public VMD_HEADER() {
		this.unknown = new byte[0x04];
		this.hdr_string = new byte[0x1A];
		System.arraycopy(VMDFile.c_hdr_string, 0, hdr_string, 0,
				Math.min(hdr_string.length, VMDFile.c_hdr_string.length));
	}

	public VMD_HEADER Read(ReadBuffer buffer) throws ReadException {
		hdr_string = buffer.readByteArray(0x1A);
		unknown = buffer.readByteArray(0x04);
		return this;
	}

}
