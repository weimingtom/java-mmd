package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.EventHandler;

/**
 * �L�����o�X�֘A�̃C�x���g�n���h���ł��B
 */
public interface GLCanvasHandler extends EventHandler {

	/**
	 * �`�抮����ʒm���܂��B
	 * 
	 * @param event
	 *            �C�x���g�Bnull�͕s�B
	 */
	public void onDraw(GLCanvasEvent event);

}
