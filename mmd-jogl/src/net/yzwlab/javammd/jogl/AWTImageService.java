package net.yzwlab.javammd.jogl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.IImageService;

/**
 * 画像サービスの実装です。
 */
public class AWTImageService implements IImageService {

	/**
	 * 構築します。
	 */
	public AWTImageService() {
		;
	}

	@Override
	public IImage createInterleavedImage(IReadBuffer buffer, Type type,
			int width, int height, int scanlineStride, int pixelStride,
			int[] bandOffsets) throws ReadException {
		if (buffer == null || type == null || bandOffsets == null) {
			throw new IllegalArgumentException();
		}

		DataBufferByte raw = new DataBufferByte(
				buffer.readByteArray(scanlineStride * height), scanlineStride
						* height, 0);

		Raster raster = Raster.createInterleavedRaster(raw, width, height,
				scanlineStride, pixelStride, bandOffsets, null);

		int bufferType = BufferedImage.TYPE_3BYTE_BGR;
		if (type == Type.ABGR) {
			bufferType = BufferedImage.TYPE_4BYTE_ABGR;
		}
		BufferedImage image = new BufferedImage(width, height, bufferType);
		image.setData(raster);
		return new Image(image);
	}

	/**
	 * 画像を実装します。
	 */
	public class Image implements IImage {

		/**
		 * 画像を保持します。
		 */
		private BufferedImage image;

		/**
		 * 構築します。
		 * 
		 * @param image
		 *            画像。nullは不可。
		 */
		public Image(BufferedImage image) {
			if (image == null) {
				throw new IllegalArgumentException();
			}
			this.image = image;
		}

		@Override
		public int getWidth() {
			return image.getWidth();
		}

		@Override
		public int getHeight() {
			return image.getHeight();
		}

		@Override
		public IImage flip(Direction dir) {
			if (dir == Direction.VERTICAL) {
				BufferedImage source = image;
				BufferedImage image = new BufferedImage(source.getWidth(),
						source.getHeight(), source.getType());
				int w = image.getWidth();
				int h = image.getHeight();
				int[] line = new int[w];
				for (int i = 0; i < source.getHeight(); i++) {
					source.getRGB(0, i, w, 1, line, 0, w);
					image.setRGB(0, h - i - 1, w, 1, line, 0, w);
				}
				return new Image(image);
			}
			throw new UnsupportedOperationException();
		}

		/**
		 * 画像を取得します。
		 * 
		 * @return 画像。
		 */
		public BufferedImage getImage() {
			return image;
		}

	}

}
