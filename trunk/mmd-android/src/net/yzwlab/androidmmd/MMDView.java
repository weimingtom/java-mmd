package net.yzwlab.androidmmd;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.gl.DefaultRenderer;
import net.yzwlab.androidmmd.gl.Model3D;
import net.yzwlab.androidmmd.gl.Model3DRenderer;
import net.yzwlab.androidmmd.gl.OpenSenseStereoscopicRenderer;
import net.yzwlab.androidmmd.gl.Model3D.Handler;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.htc.view.DisplaySetting;

/**
 * MMDのビューです。
 */
public class MMDView extends GLSurfaceView implements Handler {

	/**
	 * タイプの定義です。
	 */
	public enum Type {
		NORMAL, OPENSENSE_3D
	}

	/**
	 * Logcat
	 */
	public static final String TAG = "MMDView";

	/**
	 * モデルを保持します。
	 */
	private Model3D model;

	/**
	 * カメラパラメータを保持します。
	 */
	private MMDCamera camera;

	/**
	 * 最後のタイプを保持します。
	 */
	private Type lastType;

	/**
	 * レンダラを保持します。
	 */
	private Model3DRenderer renderer;

	/**
	 * 構築します。
	 * 
	 * @param context
	 *            コンテキスト。nullは不可。
	 * @param attrs
	 *            属性セット。nullは不可。
	 */
	public MMDView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.model = null;
		this.camera = null;
		this.lastType = null;
		this.renderer = null;
		init();
	}

	/**
	 * 構築します。
	 * 
	 * @param context
	 *            コンテキスト。nullは不可。
	 */
	public MMDView(Context context) {
		super(context);

		this.model = null;
		this.camera = null;
		this.lastType = null;
		this.renderer = null;
		init();
	}

	/**
	 * カメラ情報を取得します。
	 * 
	 * @return カメラ。
	 */
	public MMDCamera getCamera() {
		return camera;
	}

	/**
	 * モデルを設定します。
	 * 
	 * @param model
	 *            モデル。nullは不可。
	 */
	public synchronized void setModel(Model3D model) {
		if (this.model != null) {
			this.model.removeHandler(this);
		}
		this.model = model;
		if (model != null) {
			model.addHandler(this);
		}
		if (renderer == null) {
			return;
		}
		this.renderer.setModel(model);
		requestRender();
	}

	public void updated(Model3D source) {
		requestRender();
	}

	/**
	 * 初期化します。
	 */
	private void init() {
		camera = new MMDCamera();
		// TODO
		SurfaceHolder holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		holder.addCallback(new OpenSenseDetector());

		setRenderer(new RendererWrapper());

		typeDetected(Type.NORMAL);
	}

	/**
	 * タイプの検出時の処理です。
	 * 
	 * @param type
	 *            タイプ。nullは不可。
	 */
	private void typeDetected(Type type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		if (lastType == type) {
			return;
		}
		lastType = type;
		if (type == Type.NORMAL) {
			refreshRenderer(new DefaultRenderer());
		} else {
			refreshRenderer(new OpenSenseStereoscopicRenderer());
		}
	}

	/**
	 * コアレンダラを取得します。
	 * 
	 * @return コアレンダラ。
	 */
	private synchronized Renderer getCore() {
		return renderer;
	}

	/**
	 * レンダラを更新します。
	 * 
	 * @param renderer
	 *            レンダラ。nullは不可。
	 */
	private synchronized void refreshRenderer(Model3DRenderer renderer) {
		if (this.renderer == renderer) {
			return;
		}
		this.renderer = renderer;
		invalidate();
		if (this.renderer == null) {
			return;
		}
		this.renderer.setCamera(camera);
		this.renderer.setModel(model);
	}

	/**
	 * OpenSenseの構築処理です。
	 */
	private class OpenSenseDetector implements SurfaceHolder.Callback {

		/**
		 * 構築します。
		 */
		public OpenSenseDetector() {
			;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			boolean formatResult = false;
			Display display = ((WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay();
			int rotation = display.getRotation();
			if (rotation == Surface.ROTATION_90
					|| rotation == Surface.ROTATION_270) {
				try {
					formatResult = DisplaySetting.setStereoscopic3DFormat(
							holder.getSurface(),
							DisplaySetting.STEREOSCOPIC_3D_FORMAT_SIDE_BY_SIDE);
				} catch (NoClassDefFoundError e) {
					android.util.Log.i(TAG,
							"class not found - S3D display not available");
				}
			}

			Type t = Type.NORMAL;
			if (formatResult) {
				t = Type.OPENSENSE_3D;
			}
			android.util.Log.i(TAG, "Type: " + t);
			typeDetected(t);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			;
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			;
		}

	}

	/**
	 * レンダラのラッパーです。
	 */
	private class RendererWrapper implements Renderer {

		/**
		 * 構築します。
		 */
		private RendererWrapper() {
			;
		}

		public void onDrawFrame(GL10 gl) {
			Renderer renderer = getCore();
			if (renderer == null) {
				return;
			}
			renderer.onDrawFrame(gl);
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Renderer renderer = getCore();
			if (renderer == null) {
				return;
			}
			renderer.onSurfaceChanged(gl, width, height);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Renderer renderer = getCore();
			if (renderer == null) {
				return;
			}
			renderer.onSurfaceCreated(gl, config);
		}

	}

}
