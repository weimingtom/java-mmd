package net.yzwlab.gwtmmd.client.gl;

/**
 * �^�C�}�[�̎��s�G���W�����`���܂��B
 */
public interface ITimerRunner {

	/**
	 * �����i�[���`���܂��B
	 */
	public interface Runnable {

		/**
		 * ���s���܂��B
		 */
		public void run();

	}

	/**
	 * �^�C�}�[�̓o�^�����`���܂��B
	 */
	public interface Registration {

		/**
		 * �L�����Z�����܂��B
		 */
		public void cancel();

	}

	/**
	 * �J��Ԃ����s���w�����܂��B
	 * 
	 * @param periodMillis
	 *            ���s�Ԋu�Bnull�͕s�B
	 * @param task
	 *            �^�X�N�B
	 * @return �o�^���B
	 */
	public Registration scheduleRepeating(int periodMillis, Runnable task);

}
