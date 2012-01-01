package net.yzwlab.gwtmmd.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

/**
 * 画像の変換機能を提供するサーブレットです。
 */
public class ImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 構築します。
	 */
	public ImageServlet() {
		;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getParameter("p");
		if (path == null) {
			throw new ServletException();
		}
		path = path.trim();
		if (path.length() == 0) {
			throw new ServletException();
		}

		int pos = path.indexOf("/");
		if (pos < 0) {
			throw new ServletException("Invalid: " + path);
		}
		String base = path.substring(0, pos);
		String name = path.substring(pos + 1);
		if (name.contains("/")) {
			throw new ServletException("Invalid: " + path);
		}

		InputStream in = null;
		try {
			in = ImageServlet.class.getClassLoader().getResourceAsStream(
					"net/yzwlab/gwtmmd/server/mmd/" + base + "/" + name);
			ImageLoader loader = new ImageLoader();
			ImageLoader.Type type = ImageLoader.getType(name);
			ImagesService service = ImagesServiceFactory.getImagesService();

			Image img = loader.read(type, in);
			Image png = service.applyTransform(
					ImagesServiceFactory.makeResize(img.getWidth(),
							img.getHeight()), img,
					ImagesService.OutputEncoding.PNG);

			resp.setContentType("image/png");
			byte[] dt = png.getImageData();
			resp.setContentLength(dt.length);
			ServletOutputStream out = resp.getOutputStream();
			out.write(dt);
			out.flush();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					;
				}
			}
			in = null;
		}
	}

}
