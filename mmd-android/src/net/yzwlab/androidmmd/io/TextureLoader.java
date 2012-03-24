package net.yzwlab.androidmmd.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.yzwlab.androidmmd.MMDActivity;
import net.yzwlab.androidmmd.model.TextureCollection;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureLoader implements IMMDTextureProvider {

	private static final String TAG = "GLTest";

	private TextureCollection collection;

	private List<Entry> queue;

	public TextureLoader(TextureCollection collection) {
		if (collection == null) {
			throw new IllegalArgumentException();
		}
		this.collection = collection;
		this.queue = new ArrayList<Entry>();
	}

	public synchronized void load(byte[] filename, Handler handler) {
		if (filename == null || handler == null) {
			throw new IllegalArgumentException();
		}
		queue.add(new Entry(filename, handler));
	}

	public void start() {
		synchronized (this) {
			if (queue.size() == 0) {
				return;
			}
		}
		new Thread() {
			@Override
			public void run() {
				Entry entry = null;
				if ((entry = popEntry()) != null) {
					byte[] filename = entry.filename;
					int len = filename.length;
					for (int i = 0; i < filename.length; i++) {
						byte ch = filename[i];
						if (ch == '*' || ch == 0) {
							len = i;
							break;
						}
					}

					String sfilename = new String(filename, 0, len);
					Log.i(TAG, "Texture: " + sfilename);

					InputStream in = null;
					try {
						String path = MMDActivity.MODEL_PATH;
						int pos = path.lastIndexOf("/");
						if (pos >= 0) {
							path = path.substring(0, pos + 1);
						}
						in = getClass().getClassLoader().getResourceAsStream(
								path + sfilename);
						if (in == null) {
							throw new ReadException("Not found: " + sfilename);
						}
						Bitmap bmp = BitmapFactory.decodeStream(in);
						if (bmp == null) {
							throw new ReadException("Can't decode: "
									+ sfilename);
						}

						collection.onBitmap(filename, bmp, entry.handler);
						Log.i(TAG, "Texture Loaded: " + sfilename);
					} catch (Throwable t) {
						entry.handler.onError(entry.filename, t);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								;
							}
							in = null;
						}
					}

				}
			}
		}.start();
	}

	private synchronized Entry popEntry() {
		if (queue.size() == 0) {
			return null;
		}
		return queue.remove(0);
	}

	private class Entry {

		private byte[] filename;

		private Handler handler;

		public Entry(byte[] filename, Handler handler) {
			if (filename == null || handler == null) {
				throw new IllegalArgumentException();
			}
			this.filename = filename;
			this.handler = handler;
		}

	}
}
