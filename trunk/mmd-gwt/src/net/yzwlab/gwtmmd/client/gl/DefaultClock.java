package net.yzwlab.gwtmmd.client.gl;

/**
 * クロックのデフォルト実装です。
 */
public class DefaultClock implements IModelClock {

	/**
	 * 構築します。
	 */
	public DefaultClock() {
		;
	}

	@Override
	public Long getCurrentTime() {
		return System.currentTimeMillis();
	}

}
