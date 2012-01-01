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
 * �摜���[�_�ł��B
 */
public class ImageLoader implements IImageService {

	/**
	 * �摜�^�C�v��ێ����܂��B
	 */
	public enum Type {
		TGA, BMP, PNG, JPEG
	}

	/**
	 * �^�C�v���擾���܂��B
	 * 
	 * @param filename
	 *            �t�@�C�����Bnull�͕s�B
	 * @return �^�C�v�B
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
	 * �摜���[�_�𐶐����܂��B
	 */
	public ImageLoader() {
		;
	}

	/**
	 * �ǂݏo���܂��B
	 * 
	 * @param type
	 *            �^�C�v�Bnull�͕s�B
	 * @param in
	 *            ���̓X�g���[���Bnull�͕s�B
	 * @return �摜�B
	 * @throws IOException
	 *             ���o�͊֌W�̃G���[�B
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
	 * �摜�f�[�^���`���܂��B
	 */
	private class ILImage implements IImage {

		/**
		 * �w�b�_�T�C�Y���`���܂��B
		 */
		private static final int BITMAPFILEHEADER_SIZE = 14;

		/**
		 * �w�b�_�T�C�Y���`���܂��B
		 */
		private static final int BITMAPINFOHEADER_SIZE = 40;

		/**
		 * ����ێ����܂��B
		 */
		private int width;

		/**
		 * ������ێ����܂��B
		 */
		private int height;

		/**
		 * �f�[�^��ێ����܂��B
		 */
		private byte[] data;

		/**
		 * �\�z���܂��B
		 * 
		 * @param width
		 *            ���B
		 * @param height
		 *            �����B
		 * @param data
		 *            �f�[�^�Bnull�͕s�B
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
		 * BMP�Ƃ��Ď擾���܂��B
		 * 
		 * @return BMP�Ƃ��ĉ��߂����ꍇ�̃f�[�^�B
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
		 * DWORD�l�������o���܂��B
		 * 
		 * @param out
		 *            �����o����Bnull�͕s�B
		 * @param value
		 *            �l�B
		 * @throws IOException
		 *             ���o�͊֌W�̃G���[�B
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
		 * WORD�l�������o���܂��B
		 * 
		 * @param out
		 *            �����o����Bnull�͕s�B
		 * @param value
		 *            �l�B
		 * @throws IOException
		 *             ���o�͊֌W�̃G���[�B
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
