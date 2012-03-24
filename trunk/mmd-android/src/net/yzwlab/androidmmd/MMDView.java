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
 * MMD�̃r���[�ł��B
 */
public class MMDView extends GLSurfaceView implements Handler {

	/**
	 * �^�C�v�̒�`�ł��B
	 */
	public enum Type {
		NORMAL, OPENSENSE_3D
	}

	/**
	 * Logcat
	 */
	public static final String TAG = "MMDView";

	/**
	 * ���f����ێ����܂��B
	 */
	private Model3D model;

	/**
	 * �J�����p�����[�^��ێ����܂��B
	 */
	private MMDCamera camera;

	/**
	 * �Ō�̃^�C�v��ێ����܂��B
	 */
	private Type lastType;

	/**
	 * �����_����ێ����܂��B
	 */
	private Model3DRenderer renderer;

	/**
	 * �\�z���܂��B
	 * 
	 * @param context
	 *            �R���e�L�X�g�Bnull�͕s�B
	 * @param attrs
	 *            �����Z�b�g�Bnull�͕s�B
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
	 * �\�z���܂��B
	 * 
	 * @param context
	 *            �R���e�L�X�g�Bnull�͕s�B
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
	 * �J���������擾���܂��B
	 * 
	 * @return �J�����B
	 */
	public MMDCamera getCamera() {
		return camera;
	}

	/**
	 * ���f����ݒ肵�܂��B
	 * 
	 * @param model
	 *            ���f���Bnull�͕s�B
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
	 * ���������܂��B
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
	 * �^�C�v�̌��o���̏����ł��B
	 * 
	 * @param type
	 *            �^�C�v�Bnull�͕s�B
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
	 * �R�A�����_�����擾���܂��B
	 * 
	 * @return �R�A�����_���B
	 */
	private synchronized Renderer getCore() {
		return renderer;
	}

	/**
	 * �����_�����X�V���܂��B
	 * 
	 * @param renderer
	 *            �����_���Bnull�͕s�B
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
	 * OpenSense�̍\�z�����ł��B
	 */
	private class OpenSenseDetector implements SurfaceHolder.Callback {

		/**
		 * �\�z���܂��B
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
	 * �����_���̃��b�p�[�ł��B
	 */
	private class RendererWrapper implements Renderer {

		/**
		 * �\�z���܂��B
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
