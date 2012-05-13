package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * ハンドラのコレクションです。
 */
public interface HasGLCanvasHandlers extends HasHandlers {

	/**
	 * ハンドラを追加します。
	 * 
	 * @param handler
	 *            ハンドラ。nullは不可。
	 * @return ハンドラの登録情報。
	 */
	public HandlerRegistration addGLCanvasHandler(GLCanvasHandler handler);

}
