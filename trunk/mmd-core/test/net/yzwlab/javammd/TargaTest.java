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
 * TGA���[�_�̃e�X�g�ł��B
 */
public class TargaTest {

	/**
	 * ���s���܂��B
	 * 
	 * @param args
	 *            �����Bnull�͕s�B
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
	 * �t�@�C���o�b�t�@�̎����ł��B
	 */
	private static class FileBuffer extends StreamBuffer {

		/**
		 * �\�z���܂��B
		 * 
		 * @param f
		 *            �t�@�C���Bnull�͕s�B
		 * @param in
		 *            ���̓X�g���[���Bnull�͕s�B
		 * @throws IOException
		 *             ���o�͊֌W�̃G���[�B
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
