package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.GwtEvent;

/**
 * �L�����o�X�֘A�̃C�x���g�ł��B
 */
public class GLCanvasEvent extends GwtEvent<GLCanvasHandler> {

	/**
	 * �A�N�V�������`���܂��B
	 */
	public enum Action {
		DRAW
	}

	/**
	 * �^�C�v���`���܂��B
	 */
	public static Type<GLCanvasHandler> TYPE = new Type<GLCanvasHandler>();

	/**
	 * �A�N�V������ێ����܂��B
	 */
	private Action action;

	/**
	 * �\�z���܂��B
	 * 
	 * @param action
	 *            �A�N�V�����Bnull�͕s�B
	 */
	public GLCanvasEvent(Action action) {
		if (action == null) {
			throw new IllegalArgumentException();
		}
		this.action = action;
	}

	@Override
	public Type<GLCanvasHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GLCanvasHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		if (action == Action.DRAW) {
			handler.onDraw(this);
		}
	}

}
