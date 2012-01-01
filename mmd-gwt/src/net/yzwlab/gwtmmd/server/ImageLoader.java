package net.yzwlab.gwtmmd.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.IImageService;
import net.yzwlab.javammd.image.TargaReader;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

/**
 * 画像ローダです。
 */
public class ImageLoader implements IImageService {

	/**
	 * 画像タイプを保持します。
	 */
	public enum Type {
		TGA, BMP, PNG, JPEG
	}

	/**
	 * タイプを取得します。
	 * 
	 * @param filename
	 *            ファイル名。nullは不可。
	 * @return タイプ。
	 */
	public static Type getType(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException();
		}
		String lext = filename.toLowerCase();
		if (lext.endsWith(".tga")) {
			return Type.TGA;
		}
		if (lext.endsWith(".bmp")) {
			return Type.BMP;
		}
		if (lext.endsWith(".png")) {
			return Type.PNG;
		}
		if (lext.endsWith(".jpg") || lext.endsWith(".jpeg")) {
			return Type.JPEG;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 画像ローダを生成します。
	 */
	public ImageLoader() {
		;
	}

	/**
	 * 読み出します。
	 * 
	 * @param type
	 *            タイプ。nullは不可。
	 * @param in
	 *            入力ストリーム。nullは不可。
	 * @return 画像。
	 * @throws IOException
	 *             入出力関係のエラー。
	 */
	public Image read(Type type, InputStream in) throws IOException {
		if (type == null || in == null) {
			throw new IllegalArgumentException();
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BufferedInputStream bin = new BufferedInputStream(in);
		int ch = -1;
		while ((ch = bin.read()) != -1) {
			bout.write(ch);
		}
		byte[] dt = bout.toByteArray();
		if (type == Type.TGA) {
			try {
				TargaReader reader = new TargaReader();
				IImage image = reader.read(this, new InputStreamReader(
						new ByteArrayInputStream(dt), dt.length));
				dt = ((ILImage) image).asBMP();
				return ImagesServiceFactory.makeImage(dt);
			} catch (ReadException e) {
				throw new IOException(e);
			}
		}
		return ImagesServiceFactory.makeImage(dt);
	}

	@Override
	public IImage createInterleavedImage(IReadBuffer buffer,
			IImageService.Type type, int w, int h, int scanlineStride,
			int pixelStride, int[] bandOffsets) throws ReadException {
		if (buffer == null || type == null || bandOffsets == null) {
			throw new IllegalArgumentException();
		}
		byte[] imageData = new byte[w * 4 * h];
		for (int y = 0; y < h; y++) {
			byte[] scanline = buffer.readByteArray(scanlineStride);

			int[] pixelBuf = new int[4];
			pixelBuf[3] = 0xff;
			for (int x = 0; x < scanlineStride; x += pixelStride) {
				for (int i = 0; i < bandOffsets.length; i++) {
					pixelBuf[i] = ((int) scanline[x + bandOffsets[i]]) & 0xff;
				}

				int offset = (x / pixelStride) * 4 + y * w * 4;
				imageData[offset + 0] = (byte) pixelBuf[0];
				imageData[offset + 1] = (byte) pixelBuf[1];
				imageData[offset + 2] = (byte) pixelBuf[2];
				imageData[offset + 3] = (byte) pixelBuf[3];
			}
		}
		return new ILImage(w, h, imageData);
	}

	/**
	 * 画像データを定義します。
	 */
	private class ILImage implements IImage {

		/**
		 * ヘッダサイズを定義します。
		 */
		private static final int BITMAPFILEHEADER_SIZE = 14;

		/**
		 * ヘッダサイズを定義します。
		 */
		private static final int BITMAPINFOHEADER_SIZE = 40;

		/**
		 * 幅を保持します。
		 */
		private int width;

		/**
		 * 高さを保持します。
		 */
		private int height;

		/**
		 * データを保持します。
		 */
		private byte[] data;

		/**
		 * 構築します。
		 * 
		 * @param width
		 *            幅。
		 * @param height
		 *            高さ。
		 * @param data
		 *            データ。nullは不可。
		 */
		public ILImage(int width, int height, byte[] data) {
			if (data == null) {
				throw new IllegalArgumentException();
			}
			this.width = width;
			this.height = height;
			this.data = data;
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
				byte[] dest = new byte[data.length];
				for (int y = 0; y < height; y++) {
					int sourceOffset = y * width * 4;
					int destOffset = (height - y - 1) * width * 4;
					System.arraycopy(data, sourceOffset, dest, destOffset,
							width * 4);
				}
				return new ILImage(width, height, dest);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * BMPとして取得します。
		 * 
		 * @return BMPとして解釈した場合のデータ。
		 */
		public byte[] asBMP() {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				// BITMAPFILEHEADER
				bout.write('B');
				bout.write('M');
				writeDWORD(bout, BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE
						+ data.length);
				writeWORD(bout, 0);
				writeWORD(bout, 0);
				writeDWORD(bout, BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE);

				// BITMAPINFOHEADER
				writeDWORD(bout, BITMAPINFOHEADER_SIZE);
				writeDWORD(bout, width);
				writeDWORD(bout, height);
				writeWORD(bout, 1);
				writeWORD(bout, 32);
				writeDWORD(bout, 0);
				writeDWORD(bout, data.length);
				writeDWORD(bout, 1);
				writeDWORD(bout, 1);
				writeDWORD(bout, 0);
				writeDWORD(bout, 0);

				// RGB
				byte[] pixel = new byte[4];
				for (int y = 0; y < height; y++) {
					int destOffset = (height - y - 1) * width * 4;
					for(int x = 0;  x < width; x ++) {
						int destOffsetX = destOffset + x * 4;
						pixel[0] = data[destOffsetX + 2];
						pixel[1] = data[destOffsetX + 1];
						pixel[2] = data[destOffsetX + 0];
						pixel[3] = data[destOffsetX + 3];
						bout.write(pixel);
					}
				}
				return bout.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * DWORD値を書き出します。
		 * 
		 * @param out
		 *            書き出し先。nullは不可。
		 * @param value
		 *            値。
		 * @throws IOException
		 *             入出力関係のエラー。
		 */
		private void writeDWORD(OutputStream out, int value) throws IOException {
			if (out == null) {
				throw new IllegalArgumentException();
			}
			out.write((value >> 0) & 0xff);
			out.write((value >> 8) & 0xff);
			out.write((value >> 16) & 0xff);
			out.write((value >> 24) & 0xff);
		}

		/**
		 * WORD値を書き出します。
		 * 
		 * @param out
		 *            書き出し先。nullは不可。
		 * @param value
		 *            値。
		 * @throws IOException
		 *             入出力関係のエラー。
		 */
		private void writeWORD(OutputStream out, int value) throws IOException {
			if (out == null) {
				throw new IllegalArgumentException();
			}
			out.write((value >> 0) & 0xff);
			out.write((value >> 8) & 0xff);
		}

	}
}
