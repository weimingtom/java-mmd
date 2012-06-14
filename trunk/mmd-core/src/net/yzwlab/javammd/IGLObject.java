package net.yzwlab.javammd;

/**
 * 描画可能なオブジェクトの抽象です。
 */
public interface IGLObject {

	/**
	 * 更新処理を非同期的に行います。
	 * 
	 * @param frameNo
	 *            フレーム番号。
	 */
	public void update(float frameNo);

	/**
	 * テクスチャの準備を行います。
	 * 
	 * @param pTextureProvider
	 *            テクスチャプロバイダ。nullは不可。
	 * @param handler
	 *            ハンドラ。nullは不可。
	 * @throws ReadException
	 *             読み込み処理時のエラー。
	 */
	public void prepare(IGLTextureProvider pTextureProvider,
			IGLTextureProvider.Handler handler) throws ReadException;

	/**
	 * 描画します。
	 * 
	 * @param gl
	 *            描画対象。nullは不可。
	 */
	public void draw(IGL gl);

}
