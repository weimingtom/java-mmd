package net.yzwlab.gwtmmd.client;

import net.yzwlab.gwtmmd.client.gl.ITimerRunner;

import com.google.gwt.user.client.Timer;

/**
 * �^�C�}�[�̎����ł��B
 */
public class DefaultTimerRunner implements ITimerRunner {

	/**
	 * �\�z���܂��B
	 */
	public DefaultTimerRunner() {
		;
	}

	@Override
	public Registration scheduleRepeating(int periodMillis, Runnable task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}
		RegistrationImpl reg = new RegistrationImpl(task);
		reg.scheduleRepeating(periodMillis);
		return reg;
	}

	/**
	 * �o�^���̎����ł��B
	 */
	private class RegistrationImpl extends Timer implements
			ITimerRunner.Registration {

		/**
		 * �^�X�N��ێ����܂��B
		 */
		private Runnable task;

		/**
		 * �\�z���܂��B
		 * 
		 * @param task
		 *            �^�X�N�Bnull�͕s�B
		 */
		public RegistrationImpl(Runnable task) {
			if (task == null) {
				throw new IllegalArgumentException();
			}
			this.task = task;
		}

		@Override
		public void run() {
			task.run();
		}

	}

}
