package net.yzwlab.javammd.format;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class VMD_V3_HEADER {
	protected byte[] actor;

	public VMD_V3_HEADER() {
		this.actor = new byte[20];
		System.arraycopy(VMDFile.c_actor_v3, 0, actor, 0,
				Math.min(actor.length, VMDFile.c_actor_v3.length));
	}

	public VMD_V3_HEADER Read(IReadBuffer buffer) throws ReadException {
		actor = buffer.readByteArray(20);
		return this;
	}

}
