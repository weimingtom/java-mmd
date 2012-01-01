package net.yzwlab.gwtmmd.client.image;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.IImageService;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Canvasを利用した画像サービスを定義します。
 */
public class CanvasImageService implements IImageService {

	/**
	 * ダミーのキャンバスを保持します。
	 */
	Element dummyCanvas;

	/**
	 * 構築します。
	 * 
	 * @param container
	 *            コンテナ。nullは不可。
	 */
	public CanvasImageService(VerticalPanel container) {
		if (container == null) {
			throw new IllegalArgumentException();
		}
		this.dummyCanvas = Document.get().createElement("canvas");

		CanvasWidget w = new CanvasWidget(dummyCanvas);
		container.add(w);
		w.setWidth(512);
		w.setHeight(512);
	}

	@Override
	public IImage createInterleavedImage(IReadBuffer buffer, Type type, int w,
			int h, int scanlineStride, int pixelStride, int[] bandOffsets)
			throws ReadException {
		if (buffer == null || type == null || bandOffsets == null) {
			throw new IllegalArgumentException();
		}
		byte[] scanline = null;
		JavaScriptObject canvasContext = get2DContext(dummyCanvas);
		JavaScriptObject imageData = createImageData(canvasContext, w, h);
		for (int y = 0; y < h; y++) {
			scanline = buffer.readByteArray(scanlineStride);

			int[] pixelBuf = new int[4];
			pixelBuf[3] = 0xff;
			for (int x = 0; x < scanlineStride; x += pixelStride) {
				for (int i = 0; i < bandOffsets.length; i++) {
					pixelBuf[i] = ((int) scanline[x + bandOffsets[i]]) & 0xff;
				}
				setRGBA(imageData, (x / pixelStride) * 4 + y * w * 4,
						pixelBuf[0], pixelBuf[1], pixelBuf[2], pixelBuf[3]);
			}
		}
		// TODO
		putImageData(canvasContext, imageData, 0, 0);
		return new Image(w, h, imageData);
	}

	/**
	 * 画像コンテキストを取得します。
	 * 
	 * @param canvas
	 *            Canvas要素。nullは不可。
	 * @return 画像コンテキスト。
	 */
	private native JavaScriptObject get2DContext(Element canvasElement) /*-{
		return canvasElement.getContext("2d");
	}-*/;

	/**
	 * 画像データを生成します。
	 * 
	 * @param context
	 *            Canvasのコンテキスト。nullは不可。
	 * @param w
	 *            幅。
	 * @param h
	 *            高さ。
	 * @return 画像データ。
	 */
	private native JavaScriptObject createImageData(JavaScriptObject context,
			int w, int h) /*-{
		return context.createImageData(w, h);
	}-*/;

	/**
	 * RGBAを設定します。
	 * 
	 * @param imageData
	 *            画像データ。nullは不可。
	 * @param byteOffset
	 *            バイト単位でのオフセット。
	 * @param r
	 *            色成分。
	 * @param g
	 *            色成分。
	 * @param b
	 *            色成分。
	 * @param a
	 *            色成分。
	 */
	private native void setRGBA(JavaScriptObject imageData, int byteOffset,
			int r, int g, int b, int a) /*-{
		imageData.data[byteOffset + 0] = r;
		imageData.data[byteOffset + 1] = g;
		imageData.data[byteOffset + 2] = b;
		imageData.data[byteOffset + 3] = a;
	}-*/;

	/**
	 * 画像データを設定します。
	 * 
	 * @param context
	 *            Canvasのコンテキスト。nullは不可。
	 * @param imageData
	 *            画像データ。nullは不可。
	 * @param x
	 *            転送先。
	 * @param y
	 *            転送先。
	 */
	private native void putImageData(JavaScriptObject context,
			JavaScriptObject imageData, int x, int y) /*-{
		context.putImageData(imageData, x, y);
	}-*/;

	/**
	 * 垂直方向に反転します。
	 * 
	 * @param source
	 *            転送元画像。nullは不可。
	 * @param dest
	 *            転送先画像。nullは不可。
	 */
	private native void flipVertical(JavaScriptObject source,
			JavaScriptObject dest) /*-{
		if (source.width != dest.width || source.height != dest.height) {
			throw "Invalid images";
		}
		var y = 0;
		for (y = 0; y < source.height; y++) {
			var sourceOffset = y * source.width * 4;
			var destOffset = (dest.height - y - 1) * dest.width * 4;
			var x = 0;
			for (x = 0; x < source.width * 4; x++) {
				dest.data[destOffset + x] = source.data[sourceOffset + x];
			}
		}
	}-*/;

	/**
	 * 画像を定義します。
	 */
	public class Image implements IImage, CanvasRaster {

		/**
		 * 幅を保持します。
		 */
		private int width;

		/**
		 * 高さを保持します。
		 */
		private int height;

		/**
		 * 画像データを保持します。
		 */
		private JavaScriptObject imageData;

		/**
		 * 構築します。
		 * 
		 * @param width
		 *            幅。
		 * @param height
		 *            高さ。
		 * @param imageData
		 *            画像データ。nullは不可。
		 */
		public Image(int width, int height, JavaScriptObject imageData) {
			if (imageData == null) {
				throw new IllegalArgumentException();
			}
			this.width = width;
			this.height = height;
			this.imageData = imageData;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public IImage flip(Direction dir) {
			if (dir == Direction.VERTICAL) {
				JavaScriptObject canvasContext = get2DContext(dummyCanvas);
				JavaScriptObject destImageData = createImageData(canvasContext,
						width, height);
				flipVertical(imageData, destImageData);
				// TODO
				putImageData(canvasContext, destImageData, 0, 0);
				return new Image(width, height, destImageData);
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public void renderTo(JavaScriptObject canvasContext, int x, int y) {
			if (canvasContext == null) {
				throw new IllegalArgumentException();
			}
			putImageData(canvasContext, imageData, x, y);
		}

	}

}
