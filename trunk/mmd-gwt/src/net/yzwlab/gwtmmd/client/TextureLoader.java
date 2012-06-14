package net.yzwlab.gwtmmd.client;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.image.CanvasRaster;
import net.yzwlab.gwtmmd.client.image.CanvasWidget;
import net.yzwlab.javammd.GLTexture;
import net.yzwlab.javammd.IGLTextureProvider;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextureLoader {

	/**
	 * WebGLキャンバスを保持します。
	 */
	private GLCanvas glCanvas;

	private byte[] filename;

	private String filenameString;

	private VerticalPanel panel;

	private IGLTextureProvider.Handler handler;

	private CanvasWidget canvas;

	public TextureLoader(GLCanvas glCanvas, byte[] filename,
			VerticalPanel panel, IGLTextureProvider.Handler handler) {
		if (glCanvas == null || filename == null || panel == null
				|| handler == null) {
			throw new IllegalArgumentException();
		}
		this.glCanvas = glCanvas;
		this.filename = filename;
		this.filenameString = null;
		this.panel = panel;
		this.handler = handler;
		this.canvas = new CanvasWidget(Document.get().createElement("canvas"));
	}

	public byte[] getFilename() {
		return filename;
	}

	public void set(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		int pos = str.indexOf("*");
		if (pos >= 0) {
			this.filenameString = str.substring(0, pos);
		} else {
			this.filenameString = str;
		}
	}

	public String getResolvedFilename() {
		return filenameString;
	}

	/**
	 * ラスタ化済みの画像を設定します。
	 * 
	 * @param raster
	 *            ラスタ化済みの画像。nullは不可。
	 */
	public void set(CanvasRaster raster) {
		if (raster == null) {
			throw new IllegalArgumentException();
		}
		canvas.setWidth(raster.getWidth());
		canvas.setHeight(raster.getHeight());
		raster.renderTo(canvas.getContext2D(), 0, 0);

		int width = raster.getWidth();
		int height = raster.getHeight();
		int index = glCanvas.createTexture(canvas.getElement(), width, height);

		GLTexture desc = new GLTexture();
		desc.setTexWidth(width);
		desc.setTexHeight(height);
		desc.setTextureIds(new long[] { index });
		set(desc);
	}

	/**
	 * ラスタ化済みの画像を設定します。
	 * 
	 * @param desc
	 *            ラスタ化済みの画像。nullは不可。
	 */
	public void set(GLTexture desc) {
		if (desc == null) {
			throw new IllegalArgumentException();
		}
		handler.onSuccess(filename, desc);
	}
}
