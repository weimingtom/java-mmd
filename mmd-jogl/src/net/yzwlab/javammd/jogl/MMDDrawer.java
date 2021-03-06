package net.yzwlab.javammd.jogl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import net.yzwlab.javammd.GLTexture;
import net.yzwlab.javammd.IGLObject;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.model.MMDModel;

/**
 * OpenGLの描画処理です。
 */
public class MMDDrawer implements GLEventListener, IGLTextureProvider.Handler {

	private File baseDir;

	private GLCanvas canvas;

	private List<IGLObject> models;

	private boolean loaded;

	private Long baseTime;

	public MMDDrawer(File baseDir, GLCanvas canvas, long baseTime) {
		this.baseDir = baseDir;
		this.canvas = canvas;
		this.models = new ArrayList<IGLObject>();
		this.loaded = false;
		this.baseTime = null;
	}

	public void add(IGLObject model) {
		if (model == null) {
			throw new IllegalArgumentException();
		}
		models.add(model);
	}

	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width,
			int height) {
		GL2 gl2 = glautodrawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(30.0, (double) width / (double) height, 1.0, 100.0);
		gl2.glTranslated(0.0, -5.0, -30.0);
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
				for (IGLObject model : models) {
					model.prepare(jogl, this);
				}
			} catch (ReadException e) {
				e.printStackTrace();
			}
			loaded = true;
		}
		// model.SetFace("にこ");
		// model.SetBoneVisible(30, false);

		long beginTime = System.currentTimeMillis();
		long updateEndTime = 0L;
		try {
			if (baseTime == null) {
				baseTime = System.currentTimeMillis();
			}
			for (IGLObject rawModel : models) {
				if (!(rawModel instanceof MMDModel)) {
					continue;
				}
				MMDModel model = (MMDModel) rawModel;
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
				model.update(nextFrame);
			}
			updateEndTime = System.currentTimeMillis();

			gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();

			gl2.glEnable(GL.GL_DEPTH_TEST);

			gl2.glColor3d(1.0, 1.0, 1.0);
			gl2.glTranslatef(0.0f, +10.0f, 0.0f);

			for (IGLObject model : models) {
				model.draw(jogl);
			}

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
	public void onSuccess(byte[] filename, GLTexture desc) {
		// TODO Auto-generated method stub
		;
	}

	@Override
	public void onError(byte[] filename, Throwable error) {
		error.printStackTrace();
	}

}
