package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * �n���h���̃R���N�V�����ł��B
 */
public interface HasGLCanvasHandlers extends HasHandlers {

	/**
	 * �n���h����ǉ����܂��B
	 * 
	 * @param handler
	 *            �n���h���Bnull�͕s�B
	 * @return �n���h���̓o�^���B
	 */
	public HandlerRegistration addGLCanvasHandler(GLCanvasHandler handler);

}
