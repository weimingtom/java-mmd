package net.yzwlab.gwtmmd.client.image;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.javammd.GLTexture;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * �摜���\�[�X�̃��[�_�ł��B
 */
public class ImageResourceLoader {

	/**
	 * �n���h�����`���܂��B
	 */
	public interface Handler {

		/**
		 * �ǂݍ��݊������̏����ł��B
		 * 
		 * @param filename
		 *            �t�@�C�����Bnull�͕s�B
		 * @param image
		 *            �摜�Bnull�͕s�B
		 */
		public void onLoad(String filename, GLTexture image);

		/**
		 * �G���[�������̏����ł��B
		 * 
		 * @param filename
		 *            �t�@�C�����Bnull�͕s�B
		 */
		public void onError(String filename);

	}

	/**
	 * �R���e�i��ێ����܂��B
	 */
	private VerticalPanel container;

	/**
	 * �G���g���̃��X�g��ێ����܂��B
	 */
	private List<Entry> entries;

	/**
	 * WebGL�L�����o�X��ێ����܂��B
	 */
	private GLCanvas glCanvas;

	/**
	 * �\�z���܂��B
	 */
	public ImageResourceLoader(VerticalPanel container) {
		if (container == null) {
			throw new IllegalArgumentException();
		}
		this.container = container;
		this.entries = new ArrayList<Entry>();
		this.glCanvas = null;
	}

	/**
	 * WebGL�L�����o�X��ݒ肵�܂��B
	 * 
	 * @param glCanvas
	 *            WebGL�L�����o�X�Bnull�͕s�B
	 */
	public void setGLCanvas(GLCanvas glCanvas) {
		if (glCanvas == null) {
			throw new IllegalArgumentException();
		}
		this.glCanvas = glCanvas;
	}

	public void load(String filename, String url, Handler handler) {
		if (filename == null || url == null || handler == null) {
			throw new IllegalArgumentException();
		}
		Image img = new Image();
		img.setUrl(url);

		CanvasWidget canvas = new CanvasWidget(Document.get().createElement(
				"canvas"));

		container.add(new Label(filename));
		container.add(img);
		container.add(canvas);

		entries.add(new Entry(filename, img, canvas, handler));
	}

	/**
	 * �G���g�����`���܂��B
	 */
	private class Entry implements LoadHandler, ErrorHandler {

		/**
		 * �t�@�C������ێ����܂��B
		 */
		private String filename;

		/**
		 * �摜��ێ����܂��B
		 */
		private Image image;

		/**
		 * �L�����o�X��ێ����܂��B
		 */
		private CanvasWidget canvas;

		/**
		 * �n���h����ێ����܂��B
		 */
		private Handler handler;

		/**
		 * �\�z���܂��B
		 * 
		 * @param filename
		 *            �t�@�C�����Bnull�͕s�B
		 * @param image
		 *            �摜�Bnull�͕s�B
		 * @param canvas
		 *            �L�����o�X�Bnull�͕s�B
		 * @param handler
		 *            �n���h���Bnull�͕s�B
		 */
		public Entry(String filename, Image image, CanvasWidget canvas,
				Handler handler) {
			if (filename == null || image == null || canvas == null
					|| handler == null) {
				throw new IllegalArgumentException();
			}
			this.filename = filename;
			this.image = image;
			this.canvas = canvas;
			this.handler = handler;

			image.addLoadHandler(this);
			image.addErrorHandler(this);
		}

		@Override
		public void onLoad(LoadEvent event) {
			if (event == null) {
				throw new IllegalArgumentException();
			}
			int width = image.getWidth();
			int height = image.getHeight();

			int memWidth = 1;
			int memHeight = 1;
			while (memWidth < width) {
				memWidth *= 2;
			}
			while (memHeight < height) {
				memHeight *= 2;
			}
			int id = 0;
			if (memWidth == width && memHeight == height) {
				id = glCanvas.createTexture(image.getElement(), width, height);
			} else {
				canvas.setWidth(memWidth);
				canvas.setHeight(memHeight);
				canvas.drawImage(image, 0, 0);
				id = glCanvas.createTexture(canvas.getElement(), memWidth,
						memHeight);
			}

			GLTexture desc = new GLTexture();
			desc.setTexHeight(height);
			desc.setTexWidth(width);
			desc.setTextureIds(new long[] { id });
			handler.onLoad(filename, desc);
		}

		@Override
		public void onError(ErrorEvent event) {
			if (event == null) {
				throw new IllegalArgumentException();
			}
			handler.onError(filename);
		}

	}

}
