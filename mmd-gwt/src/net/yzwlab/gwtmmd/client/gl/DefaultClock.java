package net.yzwlab.gwtmmd.client.gl;

/**
 * �N���b�N�̃f�t�H���g�����ł��B
 */
public class DefaultClock implements IModelClock {

	/**
	 * �\�z���܂��B
	 */
	public DefaultClock() {
		;
	}

	@Override
	public Long getCurrentTime() {
		return System.currentTimeMillis();
	}

}
