package net.yzwlab.javammd.jogl.io;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TargaReader {
	public static void main(String[] args) {
		TargaReader reader = new TargaReader();
		for (String arg : args) {
			System.out.println(arg);
			try {
				File file = new File(arg);
				BufferedImage image = reader.read(file);
				ImageIO.write(image, "png", new File("out/", file.getName()
						+ ".png"));
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	int offset;
	int end;
	byte[] buf;

	int imageType;
	int x;
	int y;
	int width;
	int height;
	int depth;
	int flags;

	int asUnsigned(byte b) {
		int a = b;
		return (a < 0 ? 256 + a : a);
	}

	int get() {
		return asUnsigned(buf[offset++]);
	}

	int getInt8() {
		return get();
	}

	int getInt16() {
		return get() + (get() << 8);
	}

	String info() {
		return width + "x" + height + "(" + depth + "bit)";
	}

	public BufferedImage read(String fileName) throws IOException {
		return read(new File(fileName));
	}

	public BufferedImage read(File f) throws IOException {
		offset = 0;
		int length = (int) f.length();
		buf = new byte[length];
		end = length;

		BufferedInputStream io = new BufferedInputStream(new FileInputStream(f));
		io.read(buf);
		io.close();

		// 00: ID field length
		getInt8();
		// 01: has color map
		getInt8();
		// 02: type
		imageType = getInt8();
		// 03: colormap entry
		getInt16();
		// 05: colormap entry size
		getInt16();
		// 07: colormap depth
		getInt8();
		// 08: image position
		x = getInt16();
		y = getInt16();
		// 12: image size
		width = getInt16();
		height = getInt16();
		// 16: color depth
		depth = getInt8();
		// 17: flags
		flags = getInt8();

		switch (imageType) {
		case 0:
			throw new IOException("No image data included.");
		case 1:
			throw new IOException("RAW, " + info() + ". Not implemented.");
		case 2:
			return loadRGBImage();
		case 3:
			throw new IOException("RAW, " + info() + ". Not implemented.");
		case 9:
			throw new IOException("RLE, " + info() + ". Not implemented.");
		case 10:
			return loadRLERGBImage();
		case 11:
			throw new IOException("Compressed, " + info()
					+ ". Not implemented.");
		case 32:
			throw new IOException("Compressed, " + info()
					+ ". Not implemented.");
		case 33:
			throw new IOException("Compressed, " + info()
					+ ". 4-pass quadtree-type process. Not implemented.");
		default:
			throw new IOException("Unknown image type: " + imageType);
		}
	}

	BufferedImage loadRGBImage() throws IOException {
		System.out.println("RAW, " + info());
		int bufferType = 0;
		assert depth % 8 == 0;
		int bands = depth / 8;
		int[] bandOffsets3 = { 2, 1, 0 };
		int[] bandOffsets4 = { 2, 1, 0, 3 };
		int[] bandOffsets = null;
		switch (bands) {
		case 3:
			bufferType = BufferedImage.TYPE_3BYTE_BGR;
			bandOffsets = bandOffsets3;
			break;
		case 4:
			bufferType = BufferedImage.TYPE_4BYTE_ABGR;
			bandOffsets = bandOffsets4;
			break;
		default:
			throw new IOException("Unknown depth: " + depth);
		}
		DataBufferByte raw = new DataBufferByte(buf, width * height * bands,
				offset);

		Raster raster = Raster.createInterleavedRaster(raw, width, height,
				width * bands, bands, bandOffsets, null);

		BufferedImage image = new BufferedImage(width, height, bufferType);
		image.setData(raster);

		if ((flags & (1 << 5)) == 0) {
			// left lower origin.
			BufferedImage source = image;
			image = new BufferedImage(source.getWidth(), source.getHeight(),
					source.getType());
			int w = image.getWidth();
			int h = image.getHeight();
			int[] line = new int[w];
			for (int i = 0; i < source.getHeight(); i++) {
				source.getRGB(0, i, w, 1, line, 0, w);
				image.setRGB(0, h - i - 1, w, 1, line, 0, w);
			}
		}

		return image;
	}

	BufferedImage loadRLERGBImage() throws IOException {
		System.out.println("RLE, " + info());

		int bufferType = 0;
		assert depth % 8 == 0;
		int bands = depth / 8;
		int[] bandOffsets3 = { 2, 1, 0 };
		int[] bandOffsets4 = { 2, 1, 0, 3 };
		int[] bandOffsets = null;
		switch (bands) {
		case 3:
			bufferType = BufferedImage.TYPE_3BYTE_BGR;
			bandOffsets = bandOffsets3;
			break;
		case 4:
			bufferType = BufferedImage.TYPE_4BYTE_ABGR;
			bandOffsets = bandOffsets4;
			break;
		default:
			throw new IOException("Unknown depth: " + depth);
		}

		// decode RLE encoding
		byte decoded[] = new byte[width * height * bands];
		int index = 0;
		int pixel[] = { 0, 0, 0, 0 };
		while (index < decoded.length) {
			// packet header
			int header = getInt8();
			int packetLength = (header & 0x7F) + 1;
			boolean isRLE = ((header >> 7) != 0);
			// packet body
			if (isRLE) {
				// run-length packet
				for (int j = 0; j < bands; ++j) {
					pixel[j] = getInt8();
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
						decoded[index++] = (byte) getInt8();
					}
				}
			}
		}
		assert offset == buf.length;
		assert index == decoded.length;

		DataBufferByte raw = new DataBufferByte(decoded, width * height * bands);

		Raster raster = Raster.createInterleavedRaster(raw, width, height,
				width * bands, bands, bandOffsets, null);

		BufferedImage image = new BufferedImage(width, height, bufferType);
		image.setData(raster);

		if ((flags & (1 << 5)) == 0) {
			// left lower origin.
			// ToDo: flip vertical.
			System.out.println("left lower origin.");
		}

		return image;
	}

}
