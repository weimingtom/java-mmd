package net.yzwlab.androidmmd.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.MMDView;
import net.yzwlab.androidmmd.gl.AbstractModel3D;
import net.yzwlab.androidmmd.io.TextureLoader;
import net.yzwlab.androidmmd.util.NullHandler;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.TEXTURE_DESC;
import net.yzwlab.javammd.model.MMDModel;
import android.graphics.Bitmap;
import android.util.Log;

public class MMDModel3D extends AbstractModel3D implements TextureCollection {

	private static ByteBuffer extract(Bitmap bmp, int texWidth, int texHeight) {
		ByteBuffer bb = ByteBuffer.allocateDirect(texWidth * texHeight * 4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();
		// Convert ARGB -> RGBA
		int y = texHeight - 1;
		for (y = 0; y < bmp.getHeight(); y++) {
			int x = 0;
			for (x = 0; x < bmp.getWidth(); x++) {
				int pix = bmp.getPixel(x, y);
				int alpha = ((pix >> 24) & 0xFF);
				int red = ((pix >> 16) & 0xFF);
				int green = ((pix >> 8) & 0xFF);
				int blue = ((pix) & 0xFF); //
				ib.put(red << 24 | green << 16 | blue << 8 | alpha);
			}
			for (; x < texWidth; x++) {
				ib.put(0x00000000);
			}
		}

		// for (int y = 0; y < texHeight; y++) {
		// int coly = (int) (((double) y) / ((double) texHeight) * 255);
		// if (coly > 128) {
		// coly = 255;
		// } else {
		// coly = 0;
		// }
		// for (int x = 0; x < texWidth; x++) {
		// int colx = (int) (((double) x) / ((double) texWidth) * 255);
		// if (colx > 128) {
		// colx = 255;
		// } else {
		// colx = 0;
		// }
		// ib.put(0xff << 24 | colx << 16 | coly << 8 | 0xff);
		// }
		// }
		bb.position(0);
		return bb;
	}

	private MMDModel model;

	private GL10 lastGL;

	private ESGL wrappedGL;

	private TextureLoader loader;

	private List<BitmapEntry> loadedBitmaps;

	/**
	 * 構築します。
	 * 
	 * @param model
	 *            モデル。nullは不可。
	 */
	public MMDModel3D(MMDModel model) {
		if (model == null) {
			throw new IllegalArgumentException();
		}
		this.model = model;
		this.lastGL = null;
		this.wrappedGL = null;
		this.loader = null;
		this.loadedBitmaps = new ArrayList<BitmapEntry>();

		model.setScale(0.2f);
	}

	@Override
	public void prepare(GL10 gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		if (loader != null) {
			return;
		}
		loader = new TextureLoader(this);
		try {
			model.prepare(loader, new NullHandler());
		} catch (ReadException e) {
			Log.e(MMDView.TAG, "Failed to load", e);
		}
		loader.start();
	}

	public void draw(GL10 gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		BitmapEntry entry = null;
		while ((entry = popEntry()) != null) {
			entry.process(gl);
			if (loader != null) {
				loader.start();
			}
		}

		model.draw(wrap(gl));
	}

	public void onBitmap(byte[] filename, Bitmap bitmap,
			net.yzwlab.javammd.IMMDTextureProvider.Handler handler) {
		if (filename == null || bitmap == null || handler == null) {
			throw new IllegalArgumentException();
		}
		addEntry(new BitmapEntry(filename, bitmap, handler));
	}

	private synchronized ESGL wrap(GL10 gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		if (gl == lastGL) {
			return wrappedGL;
		}
		lastGL = gl;
		wrappedGL = new ESGL(gl);
		return wrappedGL;
	}

	/**
	 * エントリを追加します。
	 * 
	 * @param entry
	 *            エントリ。nullは不可。
	 */
	private synchronized void addEntry(BitmapEntry entry) {
		if (entry == null) {
			throw new IllegalArgumentException();
		}
		loadedBitmaps.add(entry);
	}

	/**
	 * エントリを取得します。
	 * 
	 * @return エントリ。見つからない場合はnull。
	 */
	private synchronized BitmapEntry popEntry() {
		if (loadedBitmaps.size() == 0) {
			return null;
		}
		return loadedBitmaps.remove(0);
	}

	private class BitmapEntry {

		private byte[] filename;

		private Bitmap bitmap;

		private net.yzwlab.javammd.IMMDTextureProvider.Handler handler;

		public BitmapEntry(byte[] filename, Bitmap bitmap,
				net.yzwlab.javammd.IMMDTextureProvider.Handler handler) {
			if (filename == null || bitmap == null || handler == null) {
				throw new IllegalArgumentException();
			}
			this.filename = filename;
			this.bitmap = bitmap;
			this.handler = handler;
		}

		public void process(GL10 gl) {
			if (gl == null) {
				throw new IllegalArgumentException();
			}
			handler.onSuccess(filename, createTexture(gl, bitmap));
		}

		/**
		 * テクスチャを生成します。
		 * 
		 * @param gl
		 * @param bitmap
		 * @return
		 */
		private TEXTURE_DESC createTexture(GL10 gl, Bitmap bitmap) {
			TEXTURE_DESC desc = new TEXTURE_DESC();
			desc.setTexWidth(bitmap.getWidth());
			desc.setTexHeight(bitmap.getHeight());

			int w = 1;
			while (w < desc.getTexWidth()) {
				w *= 2;
			}
			int h = 1;
			while (h < desc.getTexHeight()) {
				h *= 2;
			}

			desc.setTexMemWidth(w);
			desc.setTexMemHeight(h);
			Log.i(MMDView.TAG, "Texture: " + w + "x" + h);

			// gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
			// gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA,width,
			// height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
			// gl.glTexParameterx(GL10.GL_TEXTURE_2D,
			// GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			// gl.glTexParameterx(GL10.GL_TEXTURE_2D,
			// GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			// gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 4);
			// gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 4);
			IntBuffer intBuf = IntBuffer.allocate(32);
			gl.glGenTextures(1, intBuf);
			int textureId = intBuf.get(0);
			Log.i(MMDView.TAG, "Texture:" + textureId);

			gl.glActiveTexture(GL10.GL_TEXTURE0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

			// GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			ByteBuffer imageBuffer = extract(bitmap, w, h);
			imageBuffer.position(0);
			gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, w, h, 0,
					GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, imageBuffer);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0); // デフォルトテクスチャの割り当て
			desc.setTextureId(textureId);
			return desc;
		}

	}

}
