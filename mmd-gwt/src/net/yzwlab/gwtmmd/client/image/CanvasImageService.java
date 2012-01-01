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
 * Canvas�𗘗p�����摜�T�[�r�X���`���܂��B
 */
public class CanvasImageService implements IImageService {

	/**
	 * �_�~�[�̃L�����o�X��ێ����܂��B
	 */
	Element dummyCanvas;

	/**
	 * �\�z���܂��B
	 * 
	 * @param container
	 *            �R���e�i�Bnull�͕s�B
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
	 * �摜�R���e�L�X�g���擾���܂��B
	 * 
	 * @param canvas
	 *            Canvas�v�f�Bnull�͕s�B
	 * @return �摜�R���e�L�X�g�B
	 */
	private native JavaScriptObject get2DContext(Element canvasElement) /*-{
		return canvasElement.getContext("2d");
	}-*/;

	/**
	 * �摜�f�[�^�𐶐����܂��B
	 * 
	 * @param context
	 *            Canvas�̃R���e�L�X�g�Bnull�͕s�B
	 * @param w
	 *            ���B
	 * @param h
	 *            �����B
	 * @return �摜�f�[�^�B
	 */
	private native JavaScriptObject createImageData(JavaScriptObject context,
			int w, int h) /*-{
		return context.createImageData(w, h);
	}-*/;

	/**
	 * RGBA��ݒ肵�܂��B
	 * 
	 * @param imageData
	 *            �摜�f�[�^�Bnull�͕s�B
	 * @param byteOffset
	 *            �o�C�g�P�ʂł̃I�t�Z�b�g�B
	 * @param r
	 *            �F�����B
	 * @param g
	 *            �F�����B
	 * @param b
	 *            �F�����B
	 * @param a
	 *            �F�����B
	 */
	private native void setRGBA(JavaScriptObject imageData, int byteOffset,
			int r, int g, int b, int a) /*-{
		imageData.data[byteOffset + 0] = r;
		imageData.data[byteOffset + 1] = g;
		imageData.data[byteOffset + 2] = b;
		imageData.data[byteOffset + 3] = a;
	}-*/;

	/**
	 * �摜�f�[�^��ݒ肵�܂��B
	 * 
	 * @param context
	 *            Canvas�̃R���e�L�X�g�Bnull�͕s�B
	 * @param imageData
	 *            �摜�f�[�^�Bnull�͕s�B
	 * @param x
	 *            �]����B
	 * @param y
	 *            �]����B
	 */
	private native void putImageData(JavaScriptObject context,
			JavaScriptObject imageData, int x, int y) /*-{
		context.putImageData(imageData, x, y);
	}-*/;

	/**
	 * ���������ɔ��]���܂��B
	 * 
	 * @param source
	 *            �]�����摜�Bnull�͕s�B
	 * @param dest
	 *            �]����摜�Bnull�͕s�B
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
	 * �摜���`���܂��B
	 */
	public class Image implements IImage, CanvasRaster {

		/**
		 * ����ێ����܂��B
		 */
		private int width;

		/**
		 * ������ێ����܂��B
		 */
		private int height;

		/**
		 * �摜�f�[�^��ێ����܂��B
		 */
		private JavaScriptObject imageData;

		/**
		 * �\�z���܂��B
		 * 
		 * @param width
		 *            ���B
		 * @param height
		 *            �����B
		 * @param imageData
		 *            �摜�f�[�^�Bnull�͕s�B
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
