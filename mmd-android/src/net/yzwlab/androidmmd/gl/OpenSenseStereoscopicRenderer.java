package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.gl.Camera3D.Mode;

/**
 * EVO 3D裸眼立体視用のレンダラです。
 */
public class OpenSenseStereoscopicRenderer implements Model3DRenderer {

	private int width;

	private int height;

	/**
	 * モデルを保持します。
	 */
	private Model3D model;

	/**
	 * カメラを保持します。
	 */
	private Camera3D userCamera;

	public OpenSenseStereoscopicRenderer() {
		this.model = null;
		this.userCamera = null;
	}

	public synchronized void setModel(Model3D model) {
		this.model = model;
	}

	public synchronized void setCamera(Camera3D camera) {
		this.userCamera = camera;
	}

	public void onDrawFrame(GL10 gl) {
		if (model != null) {
			model.prepare(gl);
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// gl.glColorMask(true, true, true, true);
		// LEFT
		gl.glViewport(0, 0, (int) width / 2, (int) height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		if (userCamera != null) {
			userCamera.transformProjection(Mode.LEFT, gl, width, height);
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (userCamera != null) {
			userCamera.transformModelView(Mode.LEFT, gl);
		}
		draw(gl);

		// RIGHT
		gl.glViewport((int) width / 2, 0, (int) width / 2, (int) height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		if (userCamera != null) {
			userCamera.transformProjection(Mode.RIGHT, gl, width, height);
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (userCamera != null) {
			userCamera.transformModelView(Mode.RIGHT, gl);
		}
		draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (h == 0) {
			h = 1;
		}
		width = w;
		height = h;

		float ratio = (float) width / (float) height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width, height);

		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glEnable(GL10.GL_TEXTURE_2D);// テクスチャを有効にする
		if (model != null) {
			model.prepare(gl);
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
	}

	private void draw(GL10 gl) {
		if (model != null) {
			model.draw(gl);
		}
	}

}
