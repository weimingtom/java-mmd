package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yzwlab.gwtmmd.client.gl.ITimerRunner;

import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Timer;

/**
 * タイマーの実装です。
 */
public class DefaultTimerRunner implements ITimerRunner {

	/**
	 * タイマーを保持します。
	 */
	private Map<Integer, TimerImpl> timerMap;

	/**
	 * 構築します。
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
	 * ログを出力します。
	 * 
	 * @param message
	 *            メッセージ。null可。
	 */
	private native void log(String message) /*-{
		console.log(message);
	}-*/;

	/**
	 * 登録情報の実装です。
	 */
	private class TimerImpl extends Timer {

		/**
		 * 間隔を保持します。
		 */
		private int period;

		/**
		 * タスクを保持します。
		 */
		private List<Runnable> tasks;

		/**
		 * 構築します。
		 * 
		 * @param period
		 *            間隔。
		 */
		public TimerImpl(int period) {
			this.period = period;
			this.tasks = new ArrayList<Runnable>();
		}

		/**
		 * 追加します。
		 * 
		 * @param task
		 *            タスク。nullは不可。
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
		 * 登録処理の実装です。
		 */
		private class RegistrationImpl implements ITimerRunner.Registration {

			/**
			 * タスクを保持します。
			 */
			private ITimerRunner.Runnable task;

			/**
			 * タスクを保持します。
			 * 
			 * @param task
			 *            タスク。nullは不可。
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
