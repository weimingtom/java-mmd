package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.EventHandler;

/**
 * キャンバス関連のイベントハンドラです。
 */
public interface GLCanvasHandler extends EventHandler {

	/**
	 * 描画完了を通知します。
	 * 
	 * @param event
	 *            イベント。nullは不可。
	 */
	public void onDraw(GLCanvasEvent event);

}
