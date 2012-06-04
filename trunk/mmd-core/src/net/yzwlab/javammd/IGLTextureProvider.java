package net.yzwlab.javammd;

import net.yzwlab.javammd.format.TEXTURE_DESC;

/**
 * �e�N�X�`���̎��̂�񋟂���C���^�t�F�[�X�ł��B
 */
public interface IGLTextureProvider {

	/**
	 * �ǂݍ��ݏ����̃n���h���������C���^�t�F�[�X�ł��B
	 */
	public interface Handler {

		/**
		 * ���������ꍇ�ɌĂяo�����n���h���ł��B
		 * 
		 * @param filename
		 *            �t�@�C�����Bnull�͕s�B
		 * @param desc
		 *            �e�N�X�`���̒�`�Bnull�͕s�B
		 */
		public void onSuccess(byte[] filename, TEXTURE_DESC desc);

		/**
		 * �G���[�����������ꍇ�ɌĂяo�����n���h���ł��B
		 * 
		 * @param filename
		 *            �t�@�C�����Bnull�͕s�B
		 * @param error
		 *            �G���[�̓��e�Bnull�͕s�B
		 */
		public void onError(byte[] filename, Throwable error);

	}

	/**
	 * �ǂݍ��݂��w�����܂��B
	 * 
	 * @param filename
	 *            �t�@�C�����Bnull�͕s�B
	 * @param handler
	 *            �n���h���Bnull�͕s�B
	 * @throws ReadException
	 */
	public void load(byte[] filename, Handler handler) throws ReadException;

}
