package net.yzwlab.androidmmd.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.gl.Camera3D.Mode;

public class DefaultRenderer implements Model3DRenderer {

	/**
	 * モデルを保持します。
	 */
	private Model3D model;

	/**
	 * カメラを保持します。
	 */
	private Camera3D camera;

	public DefaultRenderer() {
		this.model = null;
		this.camera = null;
	}

	public synchronized void setModel(Model3D model) {
		this.model = model;
	}

	public synchronized void setCamera(Camera3D camera) {
		this.camera = camera;
	}

	public synchronized void onDrawFrame(GL10 gl) {
		if (model != null) {
			model.prepare(gl);
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (camera != null) {
			camera.transformModelView(Mode.CENTER, gl);
		}
		if (model != null) {
			model.draw(gl);
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		if (camera != null) {
			camera.transformProjection(Mode.CENTER, gl, width, height);
		}
		if (model != null) {
			model.prepare(gl);
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
	}

}
