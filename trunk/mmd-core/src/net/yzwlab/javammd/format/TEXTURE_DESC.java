package net.yzwlab.javammd.format;

import java.io.Serializable;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class TEXTURE_DESC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected long TextureId;

	protected int TexWidth;

	protected int TexHeight;

	protected int TexMemWidth;

	protected int TexMemHeight;

	public TEXTURE_DESC() {
		this.TextureId = 0L;
		this.TexWidth = 0;
		this.TexHeight = 0;
		this.TexMemWidth = 0;
		this.TexMemHeight = 0;
	}

	public long getTextureId() {
		return TextureId;
	}

	public void setTextureId(long TextureId) {
		this.TextureId = TextureId;
	}

	public int getTexWidth() {
		return TexWidth;
	}

	public void setTexWidth(int TexWidth) {
		this.TexWidth = TexWidth;
	}

	public int getTexHeight() {
		return TexHeight;
	}

	public void setTexHeight(int TexHeight) {
		this.TexHeight = TexHeight;
	}

	public int getTexMemWidth() {
		return TexMemWidth;
	}

	public void setTexMemWidth(int TexMemWidth) {
		this.TexMemWidth = TexMemWidth;
	}

	public int getTexMemHeight() {
		return TexMemHeight;
	}

	public void setTexMemHeight(int TexMemHeight) {
		this.TexMemHeight = TexMemHeight;
	}

	public TEXTURE_DESC Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		this.TextureId = buffer.readLong();
		this.TexWidth = buffer.readInteger();
		this.TexHeight = buffer.readInteger();
		this.TexMemWidth = buffer.readInteger();
		this.TexMemHeight = buffer.readInteger();
		return this;
	}
}
