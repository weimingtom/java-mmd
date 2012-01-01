package net.yzwlab.gwtmmd.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	public void getDefaultModel(AsyncCallback<byte[]> callback)
			throws IllegalArgumentException;

	/**
	 * 文字列を取得します。
	 * 
	 * @param data
	 *            データ。nullは不可。
	 * @param callback
	 *            コールバック。nullは不可。
	 * @throws IllegalArgumentException
	 *             不正なパラメータが与えられた場合のエラー。
	 */
	public void getStrings(List<byte[]> data,
			AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;

}
