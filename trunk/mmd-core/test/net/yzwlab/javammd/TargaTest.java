package net.yzwlab.javammd;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.TargaReader;

/**
 * TGAローダのテストです。
 */
public class TargaTest {

	/**
	 * 実行します。
	 * 
	 * @param args
	 *            引数。nullは不可。
	 */
	public static void main(String[] args) {
		InputStream in = null;
		try {
			File f = new File(args[0]);
			in = new FileInputStream(f);
			FileBuffer buf = new FileBuffer(f, new BufferedInputStream(in));

			TargaReader reader = new TargaReader();
			IImage image = reader.read(new AWTImageService(), buf);

			System.out.println("Image: " + image.getWidth() + "x"
					+ image.getHeight());
			BufferedImage rawImage = ((AWTImageService.Image) image).getImage();
			File workDir = new File("work");
			workDir.mkdirs();
			ImageIO.write(rawImage, "png", new File(workDir, "test.png"));
		} catch (Throwable th) {
			th.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					;
				}
				in = null;
			}
		}
	}

	/**
	 * ファイルバッファの実装です。
	 */
	private static class FileBuffer extends StreamBuffer {

		/**
		 * 構築します。
		 * 
		 * @param f
		 *            ファイル。nullは不可。
		 * @param in
		 *            入力ストリーム。nullは不可。
		 * @throws IOException
		 *             入出力関係のエラー。
		 */
		public FileBuffer(File f, InputStream in) throws IOException {
			super(new DataInputStream(in), f.length());
		}

		@Override
		public IReadBuffer createFromByteArray(byte[] data)
				throws ReadException {
			return new ByteBuffer(data);
		}

	}

}
