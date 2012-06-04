package net.yzwlab.javammd;

import java.io.Serializable;

public class GLTexture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected long textureId;

	protected int texWidth;

	protected int texHeight;

	public GLTexture() {
		this.textureId = 0L;
		this.texWidth = 0;
		this.texHeight = 0;
	}

	public long getTextureId() {
		return textureId;
	}

	public void setTextureId(long TextureId) {
		this.textureId = TextureId;
	}

	public int getTexWidth() {
		return texWidth;
	}

	public void setTexWidth(int TexWidth) {
		this.texWidth = TexWidth;
	}

	public int getTexHeight() {
		return texHeight;
	}

	public void setTexHeight(int TexHeight) {
		this.texHeight = TexHeight;
	}

}
