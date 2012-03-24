package net.yzwlab.androidmmd.gl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * 3Dモデルの抽象です。
 */
public abstract class AbstractModel3D implements Model3D {

	/**
	 * ハンドラを保持します。
	 */
	private List<Handler> handlers;

	/**
	 * 構築します。
	 */
	public AbstractModel3D() {
		this.handlers = new ArrayList<Handler>();
	}

	public void addHandler(Handler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		handlers.add(handler);
	}

	public void removeHandler(Handler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		handlers.remove(handler);
	}

	/**
	 * 準備処理のデフォルト実装。特に何もしない。
	 */
	public void prepare(GL10 gl) {
		if(gl == null) {
			throw new IllegalArgumentException();
		}
		;
	}

	/**
	 * ハンドラに通知します。
	 */
	protected void notifyChanged() {
		for (Handler h : handlers) {
			h.updated(this);
		}
	}

}
