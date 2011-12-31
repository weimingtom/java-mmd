package net.yzwlab.javammd.image;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.image.IImage.Direction;

/**
 * TGAファイルのローダです。
 */
public class TargaReader {

	/**
	 * 値を取得します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return 値。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	private static int get(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		return buffer.readUByte();
	}

	/**
	 * 値を取得します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return 値。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	private static int getInt16(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		return get(buffer) + (get(buffer) << 8);
	}

	/**
	 * 値を取得します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @return 値。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	private static int getInt8(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		return get(buffer);
	}

	int imageType;
	int x;
	int y;
	int width;
	int height;
	int depth;
	int flags;

	/**
	 * 構築します。
	 */
	public TargaReader() {
		this.imageType = 0;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.depth = 0;
		this.flags = 0;
	}

	/**
	 * 読み出します。
	 * 
	 * @param imageService
	 *            画像サービス。nullは不可。
	 * @param buf
	 *            バッファ。nullは不可。
	 * @return 画像。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public IImage read(IImageService imageService, IReadBuffer buf)
			throws ReadException {
		if (imageService == null || buf == null) {
			throw new IllegalArgumentException();
		}

		// 00: ID field length
		getInt8(buf);
		// 01: has color map
		getInt8(buf);
		// 02: type
		imageType = getInt8(buf);
		// 03: colormap entry
		getInt16(buf);
		// 05: colormap entry size
		getInt16(buf);
		// 07: colormap depth
		getInt8(buf);
		// 08: image position
		x = getInt16(buf);
		y = getInt16(buf);
		// 12: image size
		width = getInt16(buf);
		height = getInt16(buf);
		// 16: color depth
		depth = getInt8(buf);
		// 17: flags
		flags = getInt8(buf);

		switch (imageType) {
		case 0:
			throw new ReadException("No image data included.");
		case 1:
			throw new ReadException("RAW, " + info() + ". Not implemented.");
		case 2:
			return loadRGBImage(imageService, buf);
		case 3:
			throw new ReadException("RAW, " + info() + ". Not implemented.");
		case 9:
			throw new ReadException("RLE, " + info() + ". Not implemented.");
		case 10:
			return loadRLERGBImage(imageService, buf);
		case 11:
			throw new ReadException("Compressed, " + info()
					+ ". Not implemented.");
		case 32:
			throw new ReadException("Compressed, " + info()
					+ ". Not implemented.");
		case 33:
			throw new ReadException("Compressed, " + info()
					+ ". 4-pass quadtree-type process. Not implemented.");
		default:
			throw new ReadException("Unknown image type: " + imageType);
		}
	}

	private IImage loadRGBImage(IImageService imageService, IReadBuffer buf)
			throws ReadException {
		if (imageService == null || buf == null) {
			throw new IllegalArgumentException();
		}
		System.out.println("RAW, " + info());
		IImageService.Type bufferType = null;
		assert depth % 8 == 0;
		int bands = depth / 8;
		int[] bandOffsets3 = { 2, 1, 0 };
		int[] bandOffsets4 = { 2, 1, 0, 3 };
		int[] bandOffsets = null;
		switch (bands) {
		case 3:
			bufferType = IImageService.Type.BGR;
			bandOffsets = bandOffsets3;
			break;
		case 4:
			bufferType = IImageService.Type.ABGR;
			bandOffsets = bandOffsets4;
			break;
		default:
			throw new ReadException("Unknown depth: " + depth);
		}
		IImage image = imageService.createInterleavedImage(buf, bufferType,
				width, height, width * bands, bands, bandOffsets);

		if ((flags & (1 << 5)) == 0) {
			// left lower origin.
			return image.flip(IImage.Direction.VERTICAL);
		}

		return image;
	}

	private IImage loadRLERGBImage(IImageService imageService, IReadBuffer buf)
			throws ReadException {
		if (imageService == null || buf == null) {
			throw new IllegalArgumentException();
		}
		System.out.println("RLE, " + info());

		IImageService.Type bufferType = null;
		assert depth % 8 == 0;
		int bands = depth / 8;
		int[] bandOffsets3 = { 2, 1, 0 };
		int[] bandOffsets4 = { 2, 1, 0, 3 };
		int[] bandOffsets = null;
		switch (bands) {
		case 3:
			bufferType = IImageService.Type.BGR;
			bandOffsets = bandOffsets3;
			break;
		case 4:
			bufferType = IImageService.Type.ABGR;
			bandOffsets = bandOffsets4;
			break;
		default:
			throw new ReadException("Unknown depth: " + depth);
		}

		// decode RLE encoding
		byte decoded[] = new byte[width * height * bands];
		int index = 0;
		int pixel[] = { 0, 0, 0, 0 };
		while (index < decoded.length) {
			// packet header
			int header = getInt8(buf);
			int packetLength = (header & 0x7F) + 1;
			boolean isRLE = ((header >> 7) != 0);
			// packet body
			if (isRLE) {
				// run-length packet
				for (int j = 0; j < bands; ++j) {
					pixel[j] = getInt8(buf);
				}
				for (int i = 0; i < packetLength; ++i) {
					for (int j = 0; j < bands; ++j) {
						decoded[index++] = (byte) pixel[j];
					}
				}
			} else {
				// raw packet
				for (int i = 0; i < packetLength; ++i) {
					for (int j = 0; j < bands; ++j) {
						decoded[index++] = (byte) getInt8(buf);
					}
				}
			}
		}
		assert buf.isEOF();
		assert index == decoded.length;

		IImage image = imageService.createInterleavedImage(
				buf.createFromByteArray(decoded), bufferType, width, height,
				width * bands, bands, bandOffsets);

		if ((flags & (1 << 5)) == 0) {
			// left lower origin.
			return image.flip(Direction.VERTICAL);
		}

		return image;
	}

	/**
	 * 情報を取得します。
	 * 
	 * @return 情報。
	 */
	private String info() {
		return width + "x" + height + "(" + depth + "bit)";
	}

}
