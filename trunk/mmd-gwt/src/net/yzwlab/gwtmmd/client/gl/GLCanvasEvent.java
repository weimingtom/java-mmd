package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.GwtEvent;

/**
 * キャンバス関連のイベントです。
 */
public class GLCanvasEvent extends GwtEvent<GLCanvasHandler> {

	/**
	 * アクションを定義します。
	 */
	public enum Action {
		DRAW
	}

	/**
	 * タイプを定義します。
	 */
	public static Type<GLCanvasHandler> TYPE = new Type<GLCanvasHandler>();

	/**
	 * アクションを保持します。
	 */
	private Action action;

	/**
	 * 構築します。
	 * 
	 * @param action
	 *            アクション。nullは不可。
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
