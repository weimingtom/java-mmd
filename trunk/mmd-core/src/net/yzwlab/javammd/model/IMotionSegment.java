package net.yzwlab.javammd.model;

/**
 * モーションの区分を示すインタフェースです。
 */
public interface IMotionSegment {

	/**
	 * 開始時間を取得します。
	 * 
	 * @return 開始時間。
	 */
	public int getStart();

	/**
	 * 停止時間を取得します。
	 * 
	 * @return 停止時間。
	 */
	public int getStop();

	/**
	 * フレーム番号を取得します。
	 * 
	 * @param frameRate
	 *            フレームレート。
	 * @param currentTime
	 *            現在時刻。ミリ秒で表現する。
	 * @return フレーム番号。
	 */
	public float getFrame(float frameRate, long currentTime);

}
