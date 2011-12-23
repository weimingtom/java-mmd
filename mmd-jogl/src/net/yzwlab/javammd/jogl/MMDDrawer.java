package net.yzwlab.javammd.jogl;

import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.model.MMDModel;

/**
 * OpenGLÇÃï`âÊèàóùÇ≈Ç∑ÅB
 */
public class MMDDrawer implements GLEventListener, IDataMutex {

	private File baseDir;

	private float[] offset;

	private int index;

	private MMDModel model;

	private boolean loaded;

	public MMDDrawer(File baseDir, GLCanvas canvas, float[] offset, int index,
			MMDModel model, long baseTime) {
		this.baseDir = baseDir;
		this.offset = offset;
		this.index = index;
		this.model = model;
		this.loaded = false;
	}

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		System.out.println("reshape: " + index);
		GL2 gl2 = glautodrawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(30.0, (double) width / (double) height, 1.0, 100.0);
		gl2.glTranslated(0.0, 0.0, -5.0);
		glu.gluLookAt(3.0 + offset[index], 2.0 + 14.0, 5.0 + 10.0, 0.0,
				0.0 + 14.0, 0.0, 0.0, 1.0, 0.0);

		// coordinate system origin at lower left with width and height same as
		// the window
		// glu.gluOrtho2D(0.0f, width, 0.0f, height);

		gl2.glViewport(0, 0, width, height);
	}

	@Override
	public void init(GLAutoDrawable glautodrawable) {
	}

	@Override
	public void dispose(GLAutoDrawable glautodrawable) {
	}

	@Override
	public void display(GLAutoDrawable glautodrawable) {
		// OneTriangle.render(glautodrawable.getGL().getGL2(),
		// glautodrawable.getWidth(), glautodrawable.getHeight());
		/*
		 * glautodrawable.getWidth(), glautodrawable.getHeight(),
		 * glautodrawable.getHeight()
		 */
		GL2 gl2 = glautodrawable.getGL().getGL2();
		JOGL jogl = new JOGL(baseDir, gl2);
		if (loaded == false) {
			try {
				model.Prepare(jogl);
			} catch (ReadException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
		model.SetScale(1.0f);
		// model.SetFace("Ç…Ç±");
		// model.SetBoneVisible(30, false);
		model.UpdateAsync(this, 32.0f * 20);

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl2.glEnable(GL.GL_DEPTH_TEST);

		gl2.glColor3d(1.0, 1.0, 1.0);
		gl2.glTranslatef(0.0f, -0.0f, 0.0f);

		model.DrawAsync(this, jogl);

		gl2.glDisable(GL.GL_DEPTH_TEST);
		gl2.glFlush();

		// gl2.glReadBuffer(GL2.GL_BACK);
		//
		// int width = glautodrawable.getWidth();
		// int height = glautodrawable.getHeight();
		// ByteBuffer pixels = ByteBuffer.allocate(width * height * 3);
		// gl2.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
		// gl2.glReadPixels(0, 0, width, height, GL2.GL_RGB,
		// GL2.GL_UNSIGNED_BYTE,
		// pixels);
		//
		// BufferedImage img = new BufferedImage(width, height,
		// BufferedImage.TYPE_3BYTE_BGR);
		// byte[] data = new byte[3];
		// for (int y = 0; y < height; y++) {
		// for (int x = 0; x < width; x++) {
		// pixels.position(x * 3 + (height - y - 1) * width * 3);
		// pixels.get(data, 0, 3);
		// img.setRGB(x, y, ((data[0] & 0xff) << 16)
		// | ((data[1] & 0xff) << 8) | (data[2] & 0xff)
		// | (0xff << 24));
		// }
		// }
		// try {
		// ImageIO.write(img, "png", new File("test" + index + ".png"));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// if (index + 1 < offset.length) {
		// index++;
		// System.out.println("reshape: " + index);
		// OneTriangle.setup(glautodrawable.getGL().getGL2(), width, height,
		// offset[index]);
		// canvas.repaint();
		// }

		// TODO
		// long curTime = System.currentTimeMillis() - baseTime;
		// float frame = (float) (((double) curTime) / 100.0);
		// int frameCount = model.;
		// while (frame > frameCount) {
		// frame -= frameCount;
		// }
		// final float nextFrame = frame;
		// (new Thread(new Runnable() {
		// @Override
		// public void run() {
		// model.update(nextFrame);
		// canvas.repaint();
		// }
		// })).start();
	}

	@Override
	public void Begin() {
		;
	}

	@Override
	public void End() {
		;
	}

}
