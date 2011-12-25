package net.yzwlab.javammd.model;

/**
 * ���[�V�����̋敪�������C���^�t�F�[�X�ł��B
 */
public interface IMotionSegment {

	/**
	 * �J�n���Ԃ��擾���܂��B
	 * 
	 * @return �J�n���ԁB
	 */
	public int getStart();

	/**
	 * ��~���Ԃ��擾���܂��B
	 * 
	 * @return ��~���ԁB
	 */
	public int getStop();

	/**
	 * �t���[���ԍ����擾���܂��B
	 * 
	 * @param frameRate
	 *            �t���[�����[�g�B
	 * @param currentTime
	 *            ���ݎ����B�~���b�ŕ\������B
	 * @return �t���[���ԍ��B
	 */
	public float getFrame(float frameRate, long currentTime);

}
