package net.yzwlab.gwtmmd.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	public void getDefaultModel(AsyncCallback<byte[]> callback)
			throws IllegalArgumentException;
}
