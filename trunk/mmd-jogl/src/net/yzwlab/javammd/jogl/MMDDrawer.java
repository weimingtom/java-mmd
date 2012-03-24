package net.yzwlab.javammd.jogl;

import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.TEXTURE_DESC;
import net.yzwlab.javammd.model.MMDModel;

/**
 * OpenGL‚Ì•`‰æˆ—‚Å‚·B
 */
public class MMDDrawer implements GLEventListener, IDataMutex,
		IMMDTextureProvider.Handler {

	private File baseDir;

	private GLCanvas canvas;

	private MMDModel model;

	private boolean loaded;

	private Long baseTime;

	public MMDDrawer(File baseDir, GLCanvas canvas, MMDModel model,
			long baseTime) {
		this.baseDir = baseDir;
		this.canvas = canvas;
		this.model = model;
		this.loaded = false;
		this.baseTime = null;
	}

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		GL2 gl2 = glautodrawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(30.0, (double) width / (double) height, 1.0, 100.0);
		gl2.glTranslated(0.0, 0.0, -20.0);
		glu.gluLookAt(3.0, 2.0 + 14.0, 5.0 + 10.0, 0.0, 0.0 + 14.0, 0.0, 0.0,
				1.0, 0.0);

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
				model.prepare(jogl, this);
			} catch (ReadException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
		model.setScale(1.0f);
		// model.SetFace("‚É‚±");
		// model.SetBoneVisible(30, false);

		long beginTime = System.currentTimeMillis();
		long updateEndTime = 0L;
		try {
			if (baseTime == null) {
				baseTime = System.currentTimeMillis();
			}
			double curTime = (System.currentTimeMillis() - baseTime) / 1000.0;
			float frame = (float) (curTime * 30.0);
			Integer frameCount = model.getMaxFrame();
			if (frameCount != null) {
				if (frameCount > 0) {
					while (frame > frameCount) {
						frame -= frameCount;
					}
				}
			}
			float nextFrame = frame;
			model.updateAsync(MMDDrawer.this, nextFrame);
			updateEndTime = System.currentTimeMillis();

			gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();

			gl2.glEnable(GL.GL_DEPTH_TEST);

			gl2.glColor3d(1.0, 1.0, 1.0);
			gl2.glTranslatef(0.0f, -0.0f, 0.0f);

			model.draw(jogl);

			gl2.glDisable(GL.GL_DEPTH_TEST);
			gl2.glFlush();
		} finally {
			long updateDur = updateEndTime - beginTime;
			long dur = System.currentTimeMillis() - updateEndTime;
			System.out.println("Frame: update=" + updateDur + "msec., draw="
					+ dur + "msec.");
		}

		(new Thread(new Runnable() {
			@Override
			public void run() {
				canvas.repaint();
			}
		})).start();
	}

	@Override
	public void Begin() {
		;
	}

	@Override
	public void End() {
		;
	}

	@Override
	public void onSuccess(byte[] filename, TEXTURE_DESC desc) {
		// TODO Auto-generated method stub
		;
	}

	@Override
	public void onError(byte[] filename, Throwable error) {
		error.printStackTrace();
	}

}
