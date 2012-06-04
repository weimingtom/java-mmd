package net.yzwlab.javammd;


/**
 * テクスチャの実体を提供するインタフェースです。
 */
public interface IGLTextureProvider {

	/**
	 * 読み込み処理のハンドラを示すインタフェースです。
	 */
	public interface Handler {

		/**
		 * 成功した場合に呼び出されるハンドラです。
		 * 
		 * @param filename
		 *            ファイル名。nullは不可。
		 * @param desc
		 *            テクスチャの定義。nullは不可。
		 */
		public void onSuccess(byte[] filename, GLTexture desc);

		/**
		 * エラーが発生した場合に呼び出されるハンドラです。
		 * 
		 * @param filename
		 *            ファイル名。nullは不可。
		 * @param error
		 *            エラーの内容。nullは不可。
		 */
		public void onError(byte[] filename, Throwable error);

	}

	/**
	 * 読み込みを指示します。
	 * 
	 * @param filename
	 *            ファイル名。nullは不可。
	 * @param handler
	 *            ハンドラ。nullは不可。
	 * @throws ReadException
	 */
	public void load(byte[] filename, Handler handler) throws ReadException;

}
