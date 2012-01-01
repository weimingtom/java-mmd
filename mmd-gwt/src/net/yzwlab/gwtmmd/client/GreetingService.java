package net.yzwlab.gwtmmd.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;

	public byte[] getDefaultModel() throws IllegalArgumentException;

	/**
	 * ��������擾���܂��B
	 * 
	 * @param data
	 *            �f�[�^�Bnull�͕s�B
	 * @return ������B
	 * @throws IllegalArgumentException
	 *             �s���ȃp�����[�^���^����ꂽ�ꍇ�̃G���[�B
	 */
	public List<String> getStrings(List<byte[]> data)
			throws IllegalArgumentException;

}
