package net.yzwlab.javammd.image;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * �摜�T�[�r�X�̃C���^�t�F�[�X���`���܂��B
 */
public interface IImageService {

	/**
	 * �摜�^�C�v���`���܂��B
	 */
	public enum Type {
		BGR, ABGR
	}

	/**
	 * �C���^���[�u���ꂽ�摜�𐶐����܂��B
	 * 
	 * @param buffer
	 *            �o�b�t�@�Bnull�͕s�B
	 * @param type
	 *            �^�C�v�Bnull�͕s�B
	 * @param w
	 *            ���B
	 * @param h
	 *            �����B
	 * @param scanlineStride
	 *            �X�L�������C�����B
	 * @param pixelStride
	 *            �s�N�Z�����B
	 * @param bandOffsets
	 *            �I�t�Z�b�g�z��Bnull�͕s�B
	 * @return �摜�B
	 * @throws ReadException
	 *             �ǂݍ��݊֌W�̃G���[�B
	 */
	public IImage createInterleavedImage(IReadBuffer buffer, Type type, int w,
			int h, int scanlineStride, int pixelStride, int[] bandOffsets)
			throws ReadException;

}
