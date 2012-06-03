package net.yzwlab.gwtmmd.client.gl;

/**
 * モデルのクロックを表現するインタフェースです。
 */
public interface IModelClock {

	/**
	 * 現在時刻を取得します。
	 * 
	 * @return 現在時刻。描画が不要な場合はnull。
	 */
	public Long getCurrentTime();

}
