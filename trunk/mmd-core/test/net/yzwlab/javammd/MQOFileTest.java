package net.yzwlab.javammd;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.yzwlab.javammd.model.MetaseqModel;

public class MQOFileTest implements IGLTextureProvider, IGL,
		IGLTextureProvider.Handler {

	public static void main(String[] args) {
		try {
			File f = new File(args[0]);
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				MetaseqModel model = new MetaseqModel();
				model.load(new FileBuffer(new DataInputStream(
						new BufferedInputStream(in)), f.length()), 1.0, 255);
				MQOFileTest main = new MQOFileTest();
				model.prepare(main, main);
				model.draw(main);
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
		} catch (ReadException t) {
			t.printStackTrace();
		} catch (IOException t) {
			t.printStackTrace();
		}
	}

	private static class FileBuffer extends StreamBuffer {

		public FileBuffer(DataInput in, long size) {
			super(in, size);
		}

		@Override
		public IReadBuffer createFromByteArray(byte[] data)
				throws ReadException {
			if (data == null) {
				throw new IllegalArgumentException();
			}
			return new ByteBuffer(data);
		}

	}

	@Override
	public void load(byte[] filename, IGLTextureProvider.Handler handler) {
		if (filename == null || handler == null) {
			throw new IllegalArgumentException();
		}
		GLTexture ret = new GLTexture();
		ret.setTextureId(1L);
		ret.setTexWidth(100);
		ret.setTexHeight(100);
		handler.onSuccess(filename, ret);
	}

	@Override
	public void glPushMatrix() {
		System.out.println("glPushMatrix");
	}

	@Override
	public void glPopMatrix() {
		System.out.println("glPopMatrix");
	}

	@Override
	public void glScalef(float a1, float a2, float a3) {
		System.out.println("glScalef");
	}

	@Override
	public void glColor4f(float a1, float a2, float a3, float a4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glEnable(C target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glDisable(C target) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean glIsEnabled(C target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void glMaterialfv(C c1, C c2, float[] fv) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glMaterialf(C c1, C c2, float f) {
		// TODO Auto-generated method stub

	}

	@Override
	public FrontFace glGetFrontFace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void glFrontFace(FrontFace mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glBegin(C mode, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void glVertex3f(float x, float y, float z) {
		// System.out.println("V: " + x + ", " + y + ", " + z);
	}

	@Override
	public void glTexCoord2f(float x, float y) {
		// System.out.println("T: " + x + ", " + y);
	}

	@Override
	public void glNormal3f(float x, float y, float z) {
		// System.out.println("N: " + x + ", " + y + ", " + z);
	}

	@Override
	public void glBindTexture(C target, long texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glBlendFunc(C c1, C c2) {
		// TODO Auto-generated method stub

	}

	@Override
	public long glGetBindTexture(C target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void glEnableClientState(C target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glDisableClientState(C target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(byte[] filename, GLTexture desc) {
		;
	}

	@Override
	public void onError(byte[] filename, Throwable error) {
		error.printStackTrace();
	}

}
