package net.yzwlab.gwtmmd.client;

import net.yzwlab.gwtmmd.client.gl.ITimerRunner;

import com.google.gwt.user.client.Timer;

/**
 * タイマーの実装です。
 */
public class DefaultTimerRunner implements ITimerRunner {

	/**
	 * 構築します。
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
	 * 登録情報の実装です。
	 */
	private class RegistrationImpl extends Timer implements
			ITimerRunner.Registration {

		/**
		 * タスクを保持します。
		 */
		private Runnable task;

		/**
		 * 構築します。
		 * 
		 * @param task
		 *            タスク。nullは不可。
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
