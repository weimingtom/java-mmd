package net.yzwlab.javammd;

import net.yzwlab.javammd.format.TEXTURE_DESC;

public interface IMMDTextureProvider {

	public abstract TEXTURE_DESC Load(byte[] filename) throws ReadException;

}
