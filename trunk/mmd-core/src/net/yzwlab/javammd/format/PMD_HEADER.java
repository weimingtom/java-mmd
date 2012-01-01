package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_HEADER implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] header1;

	protected byte[] header2;

	public PMD_HEADER() {
		this.header1 = new byte[0x1b];
		this.header2 = new byte[0x100];
	}

	public byte[] getHeader1() {
		return header1;
	}

	public void setHeader1(byte[] header1) {
		this.header1 = header1;
	}

	public byte[] getHeader2() {
		return header2;
	}

	public void setHeader2(byte[] header2) {
		this.header2 = header2;
	}

	public PMD_HEADER Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.header1 = buffer.readByteArray(0x1b);
		this.header2 = buffer.readByteArray(0x100);
		return this;
	}
}
