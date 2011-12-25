package net.yzwlab.gwtmmd.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.yzwlab.gwtmmd.client.GreetingService;
import net.yzwlab.gwtmmd.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	@Override
	public byte[] getDefaultModel() throws IllegalArgumentException {
		InputStream in = null;
		try {
			in = GreetingServiceImpl.class
					.getResourceAsStream("mmd/azunyan.pmd");
			if (in == null) {
				throw new FileNotFoundException();
			}
			BufferedInputStream bufIn = new BufferedInputStream(in);

			int ch = -1;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((ch = bufIn.read()) != -1) {
				bout.write(ch);
			}
			return bout.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getClass().getName() + ": "
					+ e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					;
				}
				in = null;
			}
		}
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
