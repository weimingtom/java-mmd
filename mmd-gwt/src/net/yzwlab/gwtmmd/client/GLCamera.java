package net.yzwlab.gwtmmd.client;

import net.yzwlab.gwtmmd.client.gl.Camera3D;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * カメラのパラメータです。
 */
public class GLCamera implements Camera3D {

	public static void cross(float[] v1, float[] v2, float[] r) {
		r[0] = v1[1] * v2[2] - v2[1] * v1[2];
		r[1] = v1[2] * v2[0] - v2[2] * v1[0];
		r[2] = v1[0] * v2[1] - v2[0] * v1[1];
	}

	public static void scalarMultiply(float[] v, float s) {
		for (int i = 0; i < v.length; i++) {
			v[i] *= s;
		}
	}

	public static float magnitude(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	public static void normalize(float[] v) {
		scalarMultiply(v, 1 / magnitude(v));
	}

	/**
	 * 回転を保持します。
	 */
	private int currentRx;

	/**
	 * 回転を保持します。
	 */
	private int currentRy;

	/**
	 * 回転を保持します。
	 */
	private int currentRz;

	// private float[] rotationPoint;

	private float[] viewPos;

	private float[] viewDirection;

	private float[] viewUp;

	private float aperture;

	private float focallength; // along view direction

	private float eyeSeparation; // = 0.325f;

	private float far = 1000;

	private float wd2;
	private float ndfl;
	private float near = 0.01f;
	float[] r = new float[3];

	/**
	 * 構築します。
	 */
	public GLCamera() {
		this.currentRx = 0;
		this.currentRy = 0;
		this.currentRz = 0;
		focallength = 4;
		eyeSeparation = (float) (focallength / 30.0 / 2.0);
		aperture = 60;
		// rotationPoint = new float[] { 0, 0, 0 };
		viewDirection = new float[] { 0, -0.5f, -1 };
		viewUp = new float[] { 0, 1, 0 };
		viewPos = new float[] { 0, 4.0f, 1.5f };
		near = 1f;
		far = 50f;
		refresh();
	}

	@Override
	public JavaScriptObject getProjectionMatrix(Mode mode, int width, int height) {
		if (mode == null) {
			throw new IllegalArgumentException();
		}
		float ratio = width / (float) height;
		if (mode == Mode.LEFT) {
			float left = (float) (-ratio * wd2 + 0.5 * eyeSeparation * ndfl);
			float right = (float) (ratio * wd2 + 0.5 * eyeSeparation * ndfl);
			float top = wd2;
			float bottom = -wd2;
			return frustum(left, right, bottom, top, near, far);

		} else if (mode == Mode.RIGHT) {
			float left = (float) (-ratio * wd2 - 0.5 * eyeSeparation * ndfl);
			float right = (float) (ratio * wd2 - 0.5 * eyeSeparation * ndfl);
			float top = wd2;
			float bottom = -wd2;
			return frustum(left, right, bottom, top, near, far);

		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public JavaScriptObject getModelViewMatrix(Mode mode) {
		if (mode == null) {
			throw new IllegalArgumentException();
		}
		if (mode == Mode.LEFT) {
			// gl.glTranslatef(0, -2f, 0f);
			GLCamera camera = this;
			return lookAt(camera.viewPos[0] - r[0], camera.viewPos[1] - r[1],
					camera.viewPos[2] - r[2], camera.viewPos[0] - r[0]
							+ camera.viewDirection[0], camera.viewPos[1] - r[1]
							+ camera.viewDirection[1], camera.viewPos[2] - r[2]
							+ camera.viewDirection[2], camera.viewUp[0],
					camera.viewUp[1], camera.viewUp[2], currentRx, currentRy,
					currentRz);
			// gl.glScalef(0.1f, 0.1f, 0.1f);
			// gl.glTranslatef(0, -1f, -0f);
		} else if (mode == Mode.RIGHT) {
			// gl.glTranslatef(0, -2f, 0f);
			GLCamera camera = this;
			return lookAt(camera.viewPos[0] + r[0], camera.viewPos[1] + r[1],
					camera.viewPos[2] + r[2], camera.viewPos[0] + r[0]
							+ camera.viewDirection[0], camera.viewPos[1] + r[1]
							+ camera.viewDirection[1], camera.viewPos[2] + r[2]
							+ camera.viewDirection[2], camera.viewUp[0],
					camera.viewUp[1], camera.viewUp[2], currentRx, currentRy,
					currentRz);
			// gl.glScalef(0.1f, 0.1f, 0.1f);
			// gl.glTranslatef(0, -1f, 0f);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getCurrentRx() {
		return currentRx;
	}

	public void setCurrentRx(int currentRx) {
		this.currentRx = currentRx;
	}

	public int getCurrentRy() {
		return currentRy;
	}

	public void setCurrentRy(int currentRy) {
		this.currentRy = currentRy;
	}

	public int getCurrentRz() {
		return currentRz;
	}

	public void setCurrentRz(int currentRz) {
		this.currentRz = currentRz;
	}

	private void refresh() {
		float radians = (float) (0.0174532925 * aperture / 2);
		wd2 = (float) (near * Math.tan(radians));
		ndfl = near / focallength;
		near = focallength / 5;
		cross(viewDirection, viewUp, r);
		normalize(r);
		r[0] *= eyeSeparation / 2.0;
		r[1] *= eyeSeparation / 2.0;
		r[2] *= eyeSeparation / 2.0;
	}

	/**
	 * Frustum行列を生成します。
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 * @return Perspective行列。
	 */
	private native JavaScriptObject frustum(float left, float right,
			float bottom, float top, float near, float far) /*-{
		var dest = $wnd.mat4.create();
		$wnd.mat4.frustum(left, right, bottom, top, near, far, dest);
		return dest;
	}-*/;

	private native JavaScriptObject lookAt(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ, int rx, int ry, int rz) /*-{
		var eye = $wnd.vec3.create();
		eye[0] = eyeX;
		eye[1] = eyeY;
		eye[2] = eyeZ;
		var center = $wnd.vec3.create();
		center[0] = centerX;
		center[1] = centerY;
		center[2] = centerZ;
		var up = $wnd.vec3.create();
		up[0] = upX;
		up[1] = upY;
		up[2] = upZ;

		var dest = $wnd.mat4.create();
		$wnd.mat4.lookAt(eye, center, up, dest);
		$wnd.mat4.rotate(dest, Math.PI / 8 * rx, [ 1, 0, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(dest, Math.PI / 8 * ry, [ 0, 1, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(dest, Math.PI / 8 * rz, [ 0, 0, 1 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.scale(dest, [ 0.2, 0.2, 0.2 ]); // Scale by 20%
		return dest;
	}-*/;

}
