package net.yzwlab.androidmmd;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.gl.Camera3D;
import android.opengl.GLU;

/**
 * カメラのパラメータです。
 */
public class MMDCamera implements Camera3D {

	/**
	 * ハンドラを定義します。
	 */
	public interface Handler {

		/**
		 * 変更を通知します。
		 * 
		 * @param source
		 *            変更元。nullは不可。
		 */
		public void paramChanged(MMDCamera source);

	}

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
	 * 角度を保持します。
	 */
	private float angleX;

	/**
	 * 角度を保持します。
	 */
	private float angleY;

	// private float[] rotationPoint;

	private float[] viewPos;

	private float[] viewDirection;

	private float[] viewUp;

	private float aperture;

	private float focallength; // along view direction

	private float eyeSeparation; // = 0.325f;

	private float far = 1000;

	/**
	 * ハンドラを保持します。
	 */
	private List<Handler> handlers;

	private float wd2;
	private float ndfl;
	private float near = 0.01f;
	float[] r = new float[3];

	/**
	 * 構築します。
	 */
	public MMDCamera() {
		this.angleX = 0.0f;
		this.angleY = 0.0f;
		focallength = 4;
		eyeSeparation = (float) (focallength / 30.0);
		aperture = 60;
		// rotationPoint = new float[] { 0, 0, 0 };
		viewDirection = new float[] { 0, -0.5f, -1 };
		viewUp = new float[] { 0, 1, 0 };
		viewPos = new float[] { 0, 4.0f, 1.5f };
		near = 1f;
		far = 50f;

		this.handlers = new ArrayList<Handler>();

		refresh();
	}

	/**
	 * ハンドラを追加します。
	 * 
	 * @param handler
	 *            ハンドラ。nullは不可。
	 */
	public void addHandler(Handler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		handlers.add(handler);
	}

	/**
	 * ハンドラを削除します。
	 * 
	 * @param handler
	 *            ハンドラ。nullは不可。
	 */
	public void removeHandler(Handler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		handlers.remove(handler);
	}

	public void transformProjection(Mode mode, GL10 gl, int width, int height) {
		if (mode == null) {
			throw new IllegalArgumentException();
		}
		float ratio = width / (float) height;
		if (mode == Mode.CENTER) {
			GLU.gluPerspective(gl, 45f, (float) width / height, 1f, 50f);
		} else if (mode == Mode.LEFT) {
			float left = (float) (-ratio * wd2 + 0.5 * eyeSeparation * ndfl);
			float right = (float) (ratio * wd2 + 0.5 * eyeSeparation * ndfl);
			float top = wd2;
			float bottom = -wd2;
			gl.glFrustumf(left, right, bottom, top, near, far);

		} else if (mode == Mode.RIGHT) {
			float left = (float) (-ratio * wd2 - 0.5 * eyeSeparation * ndfl);
			float right = (float) (ratio * wd2 - 0.5 * eyeSeparation * ndfl);
			float top = wd2;
			float bottom = -wd2;
			gl.glFrustumf(left, right, bottom, top, near, far);

		} else {
			throw new IllegalArgumentException();
		}
	}

	public void transformModelView(Mode mode, GL10 gl) {
		if (mode == null) {
			throw new IllegalArgumentException();
		}
		if (mode == Mode.CENTER) {
			gl.glTranslatef(0, -3.5f, -3f);
			//gl.glScalef(0.1f, 0.1f, 0.1f);
			gl.glRotatef(30f, 0, 1, 0);
		} else if (mode == Mode.LEFT) {
			//gl.glTranslatef(0, -2f, 0f);
			MMDCamera camera = this;
			GLU.gluLookAt(gl, camera.viewPos[0] - r[0], camera.viewPos[1]
					- r[1], camera.viewPos[2] - r[2], camera.viewPos[0] - r[0]
					+ camera.viewDirection[0], camera.viewPos[1] - r[1]
					+ camera.viewDirection[1], camera.viewPos[2] - r[2]
					+ camera.viewDirection[2], camera.viewUp[0],
					camera.viewUp[1], camera.viewUp[2]);
			//gl.glScalef(0.1f, 0.1f, 0.1f);
			//gl.glTranslatef(0, -1f, -0f);
		} else if (mode == Mode.RIGHT) {
			//gl.glTranslatef(0, -2f, 0f);
			MMDCamera camera = this;
			GLU.gluLookAt(gl, camera.viewPos[0] + r[0], camera.viewPos[1]
					+ r[1], camera.viewPos[2] + r[2], camera.viewPos[0] + r[0]
					+ camera.viewDirection[0], camera.viewPos[1] + r[1]
					+ camera.viewDirection[1], camera.viewPos[2] + r[2]
					+ camera.viewDirection[2], camera.viewUp[0],
					camera.viewUp[1], camera.viewUp[2]);
			//gl.glScalef(0.1f, 0.1f, 0.1f);
			//gl.glTranslatef(0, -1f, 0f);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public float getAngleX() {
		return angleX;
	}

	public void setAngleX(float angleX) {
		if (this.angleX == angleX) {
			return;
		}
		this.angleX = angleX;
		notifyChanged();
	}

	public float getAngleY() {
		return angleY;
	}

	public void setAngleY(float angleY) {
		if (this.angleY == angleY) {
			return;
		}
		this.angleY = angleY;
		notifyChanged();
	}

	/**
	 * ハンドラに通知します。
	 */
	private void notifyChanged() {
		for (Handler h : handlers) {
			h.paramChanged(this);
		}
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

}
