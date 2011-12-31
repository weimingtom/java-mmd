package net.yzwlab.javammd.image;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * 画像サービスのインタフェースを定義します。
 */
public interface IImageService {

	/**
	 * 画像タイプを定義します。
	 */
	public enum Type {
		BGR, ABGR
	}

	/**
	 * インタリーブされた画像を生成します。
	 * 
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @param type
	 *            タイプ。nullは不可。
	 * @param w
	 *            幅。
	 * @param h
	 *            高さ。
	 * @param scanlineStride
	 *            スキャンライン幅。
	 * @param pixelStride
	 *            ピクセル幅。
	 * @param bandOffsets
	 *            オフセット配列。nullは不可。
	 * @return 画像。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public IImage createInterleavedImage(IReadBuffer buffer, Type type, int w,
			int h, int scanlineStride, int pixelStride, int[] bandOffsets)
			throws ReadException;

}
