package net.yzwlab.gwtmmd.client;

import java.util.List;

import net.yzwlab.gwtmmd.client.model.AnalyzedPMDFile;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	public void getDefaultPMD(AsyncCallback<AnalyzedPMDFile> callback)
			throws IllegalArgumentException;

	/**
	 * ��������擾���܂��B
	 * 
	 * @param data
	 *            �f�[�^�Bnull�͕s�B
	 * @param callback
	 *            �R�[���o�b�N�Bnull�͕s�B
	 * @throws IllegalArgumentException
	 *             �s���ȃp�����[�^���^����ꂽ�ꍇ�̃G���[�B
	 */
	public void getStrings(List<byte[]> data,
			AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;

}
