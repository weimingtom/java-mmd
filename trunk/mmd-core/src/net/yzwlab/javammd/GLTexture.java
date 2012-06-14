package net.yzwlab.javammd;

import java.io.Serializable;

public class GLTexture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected long[] textureIds;

	protected int texWidth;

	protected int texHeight;

	public GLTexture() {
		this.textureIds = null;
		this.texWidth = 0;
		this.texHeight = 0;
	}

	public long getTextureId(int resourceContext) {
		if (textureIds == null) {
			return 0L;
		}
		if (textureIds.length <= resourceContext) {
			return 0L;
		}
		return textureIds[resourceContext];
	}

	public void setTextureIds(long[] textureIds) {
		this.textureIds = textureIds;
	}

	public int getTexWidth() {
		return texWidth;
	}

	public void setTexWidth(int texWidth) {
		this.texWidth = texWidth;
	}

	public int getTexHeight() {
		return texHeight;
	}

	public void setTexHeight(int texHeight) {
		this.texHeight = texHeight;
	}

}
