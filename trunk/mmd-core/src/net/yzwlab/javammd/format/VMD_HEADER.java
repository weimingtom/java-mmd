package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_HEADER implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] hdr_string;

	protected byte[] unknown;

	public VMD_HEADER() {
		this.unknown = new byte[0x04];
		this.hdr_string = new byte[0x1A];
		System.arraycopy(VMDFile.c_hdr_string, 0, hdr_string, 0,
				Math.min(hdr_string.length, VMDFile.c_hdr_string.length));
	}

	public VMD_HEADER Read(IReadBuffer buffer) throws ReadException {
		hdr_string = buffer.readByteArray(0x1A);
		unknown = buffer.readByteArray(0x04);
		return this;
	}

}
