package net.yzwlab.androidmmd.model;

import net.yzwlab.javammd.IMMDTextureProvider.Handler;
import android.graphics.Bitmap;

public interface TextureCollection {

	public void onBitmap(byte[] filename, Bitmap bitmap, Handler handler);

}
