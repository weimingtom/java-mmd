package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yzwlab.gwtmmd.client.gl.ITimerRunner;

import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Timer;

/**
 * �^�C�}�[�̎����ł��B
 */
public class DefaultTimerRunner implements ITimerRunner {

	/**
	 * �^�C�}�[��ێ����܂��B
	 */
	private Map<Integer, TimerImpl> timerMap;

	/**
	 * �\�z���܂��B
	 */
	public DefaultTimerRunner() {
		this.timerMap = new HashMap<Integer, TimerImpl>();
	}

	@Override
	public Registration scheduleRepeating(int periodMillis, Runnable task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}
		TimerImpl timer = timerMap.get(periodMillis);
		if (timer == null) {
			timer = new TimerImpl(periodMillis);
			timer.scheduleRepeating(periodMillis);
		}
		timerMap.put(periodMillis, timer);
		return timer.add(task);
	}

	/**
	 * ���O���o�͂��܂��B
	 * 
	 * @param message
	 *            ���b�Z�[�W�Bnull�B
	 */
	private native void log(String message) /*-{
		console.log(message);
	}-*/;

	/**
	 * �o�^���̎����ł��B
	 */
	private class TimerImpl extends Timer {

		/**
		 * �Ԋu��ێ����܂��B
		 */
		private int period;

		/**
		 * �^�X�N��ێ����܂��B
		 */
		private List<Runnable> tasks;

		/**
		 * �\�z���܂��B
		 * 
		 * @param period
		 *            �Ԋu�B
		 */
		public TimerImpl(int period) {
			this.period = period;
			this.tasks = new ArrayList<Runnable>();
		}

		/**
		 * �ǉ����܂��B
		 * 
		 * @param task
		 *            �^�X�N�Bnull�͕s�B
		 */
		public ITimerRunner.Registration add(ITimerRunner.Runnable task) {
			if (task == null) {
				throw new IllegalArgumentException();
			}
			tasks.add(task);
			return new RegistrationImpl(task);
		}

		@Override
		public void run() {
			for (Runnable task : tasks) {
				try {
					task.run();
				} catch (UmbrellaException ut) {
					log("begin UmbrellaException");
					for (Throwable t : ut.getCauses()) {
						t.printStackTrace();
						log("  + " + t.getClass().getName() + ": "
								+ t.getMessage());
					}
					log("end UmbrellaException");
				} catch (Throwable t) {
					t.printStackTrace();
					log(t.getClass().getName() + ": " + t.getMessage());
				}
			}
			if (tasks.size() == 0) {
				timerMap.remove(period);
				cancel();
			}
		}

		/**
		 * �o�^�����̎����ł��B
		 */
		private class RegistrationImpl implements ITimerRunner.Registration {

			/**
			 * �^�X�N��ێ����܂��B
			 */
			private ITimerRunner.Runnable task;

			/**
			 * �^�X�N��ێ����܂��B
			 * 
			 * @param task
			 *            �^�X�N�Bnull�͕s�B
			 */
			public RegistrationImpl(ITimerRunner.Runnable task) {
				if (task == null) {
					throw new IllegalArgumentException();
				}
				this.task = task;
			}

			@Override
			public void cancel() {
				tasks.remove(task);
			}

		}

	}

}
