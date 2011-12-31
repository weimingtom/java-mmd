package net.yzwlab.javammd.image;

/**
 * 画像を表現するインタフェースです。
 */
public interface IImage {

	/**
	 * 方向を定義します。
	 */
	public enum Direction {
		HORIZONTAL, VERTICAL
	}

	/**
	 * 幅を取得します。
	 * 
	 * @return 幅。
	 */
	public int getWidth();

	/**
	 * 高さを取得します。
	 * 
	 * @return 高さ。
	 */
	public int getHeight();

	/**
	 * 反転処理を行います。
	 * 
	 * @param dir
	 *            方向。nullは不可。
	 * @return 画像。
	 */
	public IImage flip(Direction dir);

}
