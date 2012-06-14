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
 * 画像リソースのローダです。
 */
public class ImageResourceLoader {

	/**
	 * ハンドラを定義します。
	 */
	public interface Handler {

		/**
		 * 読み込み完了時の処理です。
		 * 
		 * @param filename
		 *            ファイル名。nullは不可。
		 * @param image
		 *            画像。nullは不可。
		 */
		public void onLoad(String filename, GLTexture image);

		/**
		 * エラー発生時の処理です。
		 * 
		 * @param filename
		 *            ファイル名。nullは不可。
		 */
		public void onError(String filename);

	}

	/**
	 * コンテナを保持します。
	 */
	private VerticalPanel container;

	/**
	 * エントリのリストを保持します。
	 */
	private List<Entry> entries;

	/**
	 * WebGLキャンバスを保持します。
	 */
	private GLCanvas glCanvas;

	/**
	 * 構築します。
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
	 * WebGLキャンバスを設定します。
	 * 
	 * @param glCanvas
	 *            WebGLキャンバス。nullは不可。
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
	 * エントリを定義します。
	 */
	private class Entry implements LoadHandler, ErrorHandler {

		/**
		 * ファイル名を保持します。
		 */
		private String filename;

		/**
		 * 画像を保持します。
		 */
		private Image image;

		/**
		 * キャンバスを保持します。
		 */
		private CanvasWidget canvas;

		/**
		 * ハンドラを保持します。
		 */
		private Handler handler;

		/**
		 * 構築します。
		 * 
		 * @param filename
		 *            ファイル名。nullは不可。
		 * @param image
		 *            画像。nullは不可。
		 * @param canvas
		 *            キャンバス。nullは不可。
		 * @param handler
		 *            ハンドラ。nullは不可。
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
