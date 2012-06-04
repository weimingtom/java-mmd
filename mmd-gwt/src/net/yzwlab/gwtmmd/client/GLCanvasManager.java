package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.image.CanvasRaster;
import net.yzwlab.gwtmmd.client.image.ImageResourceLoader;
import net.yzwlab.javammd.GLTexture;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.ReadException;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GLCanvasManager implements ImageResourceLoader.Handler,
		IGLTextureProvider {

	/**
	 * キャンバスを保持します。
	 */
	private GLCanvas glCanvas;

	/**
	 * 画像リソースローダを保持します。
	 */
	private ImageResourceLoader imageResourceLoader;

	/**
	 * ローダを保持します。
	 */
	private List<TextureLoader> textureLoaders;

	/**
	 * テクスチャ候補の画像を保持します。
	 */
	private Map<String, CanvasRaster> textureImages;

	/**
	 * テクスチャ候補の画像を保持します。
	 */
	private Map<String, GLTexture> textureDescs;

	/**
	 * 名前解決済みのテクスチャローダを保持します。
	 */
	private Map<String, List<TextureLoader>> namedTextureLoaders;

	/**
	 * 残りファイル名を保持します。
	 */
	private List<String> remainFilenames;

	/**
	 * リソースパネルを保持します。
	 */
	private VerticalPanel resourcePanel;

	/**
	 * 基本ディレクトリを保持します。
	 */
	private String lastBaseDir;

	public GLCanvasManager(GLCanvas glCanvas, VerticalPanel resourcePanel) {
		if (glCanvas == null || resourcePanel == null) {
			throw new IllegalArgumentException();
		}
		this.glCanvas = glCanvas;
		this.imageResourceLoader = new ImageResourceLoader(resourcePanel);
		this.textureLoaders = new ArrayList<TextureLoader>();
		this.textureImages = new HashMap<String, CanvasRaster>();
		this.textureDescs = new HashMap<String, GLTexture>();
		this.namedTextureLoaders = new HashMap<String, List<TextureLoader>>();
		this.remainFilenames = new ArrayList<String>();
		this.resourcePanel = resourcePanel;
		this.lastBaseDir = null;

		imageResourceLoader.setGLCanvas(glCanvas);
	}

	/**
	 * キャンバスを取得します。
	 * 
	 * @return キャンバス。
	 */
	public GLCanvas getGlCanvas() {
		return glCanvas;
	}

	/**
	 * ファイルの読み込みを指示します。
	 * 
	 * @param baseDir
	 *            基本ディレクトリ。nullは不可。
	 * @param filename
	 *            ファイル名。nullは不可。
	 */
	public void load(String baseDir, String filename) {
		if (baseDir == null || filename == null) {
			throw new IllegalArgumentException();
		}
		imageResourceLoader.load(filename, "/image?p=" + baseDir + "/"
				+ filename, this);
		this.lastBaseDir = baseDir;
	}

	/**
	 * キューへの追加を指示します。
	 * 
	 * @param baseDir
	 *            基本ディレクトリ。nullは不可。
	 * @param filename
	 *            ファイル名。nullは不可。
	 */
	public void addQueue(String baseDir, String filename) {
		if (baseDir == null || filename == null) {
			throw new IllegalArgumentException();
		}
		lastBaseDir = baseDir;
		remainFilenames.add(filename);
	}

	/**
	 * 文字列をロードします。
	 * 
	 * @param greetingService
	 *            サーバサービス。nullは不可。
	 */
	public void loadTexts(GreetingServiceAsync greetingService) {
		if (greetingService == null) {
			throw new IllegalArgumentException();
		}
		ArrayList<byte[]> dt = new ArrayList<byte[]>();
		for (TextureLoader textureLoader : textureLoaders) {
			dt.add(textureLoader.getFilename());
		}
		greetingService.getStrings(dt, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getClass().getName() + ": "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> result) {
				for (int i = result.size() - 1; i >= 0; i--) {
					TextureLoader loader = textureLoaders.get(i);
					loader.set(result.get(i));
					textureLoaders.remove(i);
					setNamingResolvedLoader(loader.getResolvedFilename(),
							loader);
				}
			}

		});
	}

	/**
	 * 読み込んだ画像を追加します。
	 * 
	 * @param name
	 *            ファイル名。nullは不可。
	 * @param data
	 *            データ。nullは不可。
	 */
	public void addImage(String name, String data) {
		if (name == null || data == null) {
			throw new IllegalArgumentException();
		}
		imageResourceLoader.load(name, data, this);
	}

	/**
	 * ラスタを追加します。
	 * 
	 * @param name
	 *            名前。nullは不可。
	 * @param raster
	 *            ラスタ。nullは不可。
	 */
	public void addRaster(String name, CanvasRaster raster) {
		if (name == null || raster == null) {
			throw new IllegalArgumentException();
		}
		textureImages.put(name, raster);

		List<TextureLoader> loader = namedTextureLoaders.get(name);
		if (loader == null) {
			return;
		}
		for (TextureLoader l : loader) {
			l.set(raster);
		}
		namedTextureLoaders.remove(name);
	}

	@Override
	public void load(byte[] filename, IGLTextureProvider.Handler handler)
			throws ReadException {
		// いったんダミーキャンバスを入れておく
		handler.onSuccess(filename, imageResourceLoader.getDummyTexture());

		VerticalPanel vpanel = new VerticalPanel();
		resourcePanel.add(vpanel);
		TextureLoader loader = new TextureLoader(glCanvas, filename, vpanel,
				handler);
		textureLoaders.add(loader);
	}

	@Override
	public void onError(String efilename) {
		Window.alert("Error: " + efilename);
		if (remainFilenames.size() == 0) {
			return;
		}
		String filename = remainFilenames.remove(0);
		imageResourceLoader.load(filename, "/image?p=" + lastBaseDir + "/"
				+ filename, this);
	}

	@Override
	public void onLoad(String name, GLTexture raster) {
		if (name == null || raster == null) {
			throw new IllegalArgumentException();
		}
		try {
			textureDescs.put(name, raster);
			List<TextureLoader> loader = namedTextureLoaders.get(name);
			if (loader == null) {
				return;
			}
			for (TextureLoader l : loader) {
				l.set(raster);
			}
			namedTextureLoaders.remove(name);
		} finally {
			if (remainFilenames.size() == 0) {
				return;
			}
			String filename = remainFilenames.remove(0);
			imageResourceLoader.load(filename, "/image?p=" + lastBaseDir + "/"
					+ filename, this);
		}
	}

	/**
	 * 名前解決済みのローダを設定します。
	 * 
	 * @param name
	 *            名前。nullは不可。
	 * @param loader
	 *            テクスチャローダ。nullは不可。
	 */
	private void setNamingResolvedLoader(String name, TextureLoader loader) {
		if (name == null || loader == null) {
			throw new IllegalArgumentException();
		}
		GLTexture desc = textureDescs.get(name);
		if (desc != null) {
			// ロード済み
			loader.set(desc);
			return;
		}
		CanvasRaster raster = textureImages.get(name);
		if (raster != null) {
			// ロード済み
			loader.set(raster);
			return;
		}
		List<TextureLoader> loaders = namedTextureLoaders.get(name);
		if (loaders == null) {
			loaders = new ArrayList<TextureLoader>();
			namedTextureLoaders.put(name, loaders);
		}
		loaders.add(loader);
	}

}
