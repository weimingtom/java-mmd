package net.yzwlab.androidmmd.util;

import net.yzwlab.androidmmd.MMDView;
import net.yzwlab.javammd.IMMDTextureProvider.Handler;
import net.yzwlab.javammd.format.TEXTURE_DESC;
import android.util.Log;

public class NullHandler implements Handler {

	public void onSuccess(byte[] filename, TEXTURE_DESC desc) {
		;
	}

	public void onError(byte[] filename, Throwable error) {
		Log.e(MMDView.TAG, "Failed to load", error);
	}

}
