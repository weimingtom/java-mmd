package net.yzwlab.androidmmd.gl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * 3D���f���̒��ۂł��B
 */
public abstract class AbstractModel3D implements Model3D {

	/**
	 * �n���h����ێ����܂��B
	 */
	private List<Handler> handlers;

	/**
	 * �\�z���܂��B
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
	 * ���������̃f�t�H���g�����B���ɉ������Ȃ��B
	 */
	public void prepare(GL10 gl) {
		if(gl == null) {
			throw new IllegalArgumentException();
		}
		;
	}

	/**
	 * �n���h���ɒʒm���܂��B
	 */
	protected void notifyChanged() {
		for (Handler h : handlers) {
			h.updated(this);
		}
	}

}
