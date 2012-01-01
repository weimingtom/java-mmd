package net.yzwlab.gwtmmd.client;

import java.util.List;

import net.yzwlab.gwtmmd.client.model.AnalyzedPMDFile;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;

	public AnalyzedPMDFile getDefaultPMD() throws IllegalArgumentException;

	/**
	 * 文字列を取得します。
	 * 
	 * @param data
	 *            データ。nullは不可。
	 * @return 文字列。
	 * @throws IllegalArgumentException
	 *             不正なパラメータが与えられた場合のエラー。
	 */
	public List<String> getStrings(List<byte[]> data)
			throws IllegalArgumentException;

}
