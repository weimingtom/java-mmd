package net.yzwlab.androidmmd;

import net.yzwlab.androidmmd.cube.SimpleCube;
import net.yzwlab.androidmmd.io.ResourceBuffer;
import net.yzwlab.androidmmd.model.MMDModel3D;
import net.yzwlab.javammd.model.MMDModel;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * MMDのアクティビティです。
 */
public class MMDActivity extends Activity {

	/**
	 * モデルのパスを定義します。
	 */
	public static final String MODEL_PATH = "net/yzwlab/androidmmd/diva_miku/main.pmd";

	/**
	 * Logcat
	 */
	private static final String TAG = "MMDActivity";

	/**
	 * メインビューを保持します。
	 */
	private MMDView mainView;

	/**
	 * 構築します。
	 */
	public MMDActivity() {
		this.mainView = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mainView = (MMDView) findViewById(R.id.mmdView);
		Log.i(MMDView.TAG, "Created");

		mainView.setModel(new SimpleCube());
		Thread worker = new Thread() {
			@Override
			public void run() {
				MMDModel model = new MMDModel();
				try {
					Log.i(TAG, "Loading...");
					model.openPMD(new ResourceBuffer(getClass()
							.getClassLoader(), MODEL_PATH));
					Log.i(TAG, "Loaded: bones=" + model.getBoneCount());
					mainView.setModel(new MMDModel3D(model));
				} catch (Throwable e) {
					Log.e(TAG, "Error: " + e.getMessage(), e);
				}
			}
		};
		worker.start();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mainView != null) {
			mainView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mainView != null) {
			mainView.onResume();
		}
	}

}