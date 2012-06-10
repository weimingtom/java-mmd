package net.yzwlab.gwtmmd.client.gl;

/**
 * タイマーの実行エンジンを定義します。
 */
public interface ITimerRunner {

	/**
	 * ランナーを定義します。
	 */
	public interface Runnable {

		/**
		 * 実行します。
		 */
		public void run();

	}

	/**
	 * タイマーの登録情報を定義します。
	 */
	public interface Registration {

		/**
		 * キャンセルします。
		 */
		public void cancel();

	}

	/**
	 * 繰り返し実行を指示します。
	 * 
	 * @param periodMillis
	 *            実行間隔。nullは不可。
	 * @param task
	 *            タスク。
	 * @return 登録情報。
	 */
	public Registration scheduleRepeating(int periodMillis, Runnable task);

}
