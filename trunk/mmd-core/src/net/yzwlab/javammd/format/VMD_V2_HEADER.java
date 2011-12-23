package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_V2_HEADER {
	protected byte[] actor;

	public VMD_V2_HEADER() {
		this.actor = new byte[10];
		System.arraycopy(VMDFile.c_actor_v2, 0, actor, 0, actor.length);
	}

	public VMD_V2_HEADER Read(IReadBuffer buffer) throws ReadException {
		actor = buffer.readByteArray(10);
		return this;
	}

}
