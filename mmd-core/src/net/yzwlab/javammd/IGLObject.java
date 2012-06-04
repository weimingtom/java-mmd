package net.yzwlab.javammd;

/**
 * �`��\�ȃI�u�W�F�N�g�̒��ۂł��B
 */
public interface IGLObject {

	/**
	 * �e�N�X�`���̏������s���܂��B
	 * 
	 * @param pTextureProvider
	 *            �e�N�X�`���v���o�C�_�Bnull�͕s�B
	 * @param handler
	 *            �n���h���Bnull�͕s�B
	 * @throws ReadException
	 *             �ǂݍ��ݏ������̃G���[�B
	 */
	public void prepare(IGLTextureProvider pTextureProvider,
			IGLTextureProvider.Handler handler) throws ReadException;

	/**
	 * �`�悵�܂��B
	 * 
	 * @param gl
	 *            �`��ΏہBnull�͕s�B
	 */
	public void draw(IGL gl);

}
