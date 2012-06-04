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
	 * �L�����o�X��ێ����܂��B
	 */
	private GLCanvas glCanvas;

	/**
	 * �摜���\�[�X���[�_��ێ����܂��B
	 */
	private ImageResourceLoader imageResourceLoader;

	/**
	 * ���[�_��ێ����܂��B
	 */
	private List<TextureLoader> textureLoaders;

	/**
	 * �e�N�X�`�����̉摜��ێ����܂��B
	 */
	private Map<String, CanvasRaster> textureImages;

	/**
	 * �e�N�X�`�����̉摜��ێ����܂��B
	 */
	private Map<String, GLTexture> textureDescs;

	/**
	 * ���O�����ς݂̃e�N�X�`�����[�_��ێ����܂��B
	 */
	private Map<String, List<TextureLoader>> namedTextureLoaders;

	/**
	 * �c��t�@�C������ێ����܂��B
	 */
	private List<String> remainFilenames;

	/**
	 * ���\�[�X�p�l����ێ����܂��B
	 */
	private VerticalPanel resourcePanel;

	/**
	 * ��{�f�B���N�g����ێ����܂��B
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
	 * �L�����o�X���擾���܂��B
	 * 
	 * @return �L�����o�X�B
	 */
	public GLCanvas getGlCanvas() {
		return glCanvas;
	}

	/**
	 * �t�@�C���̓ǂݍ��݂��w�����܂��B
	 * 
	 * @param baseDir
	 *            ��{�f�B���N�g���Bnull�͕s�B
	 * @param filename
	 *            �t�@�C�����Bnull�͕s�B
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
	 * �L���[�ւ̒ǉ����w�����܂��B
	 * 
	 * @param baseDir
	 *            ��{�f�B���N�g���Bnull�͕s�B
	 * @param filename
	 *            �t�@�C�����Bnull�͕s�B
	 */
	public void addQueue(String baseDir, String filename) {
		if (baseDir == null || filename == null) {
			throw new IllegalArgumentException();
		}
		lastBaseDir = baseDir;
		remainFilenames.add(filename);
	}

	/**
	 * ����������[�h���܂��B
	 * 
	 * @param greetingService
	 *            �T�[�o�T�[�r�X�Bnull�͕s�B
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
	 * �ǂݍ��񂾉摜��ǉ����܂��B
	 * 
	 * @param name
	 *            �t�@�C�����Bnull�͕s�B
	 * @param data
	 *            �f�[�^�Bnull�͕s�B
	 */
	public void addImage(String name, String data) {
		if (name == null || data == null) {
			throw new IllegalArgumentException();
		}
		imageResourceLoader.load(name, data, this);
	}

	/**
	 * ���X�^��ǉ����܂��B
	 * 
	 * @param name
	 *            ���O�Bnull�͕s�B
	 * @param raster
	 *            ���X�^�Bnull�͕s�B
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
		// ��������_�~�[�L�����o�X�����Ă���
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
	 * ���O�����ς݂̃��[�_��ݒ肵�܂��B
	 * 
	 * @param name
	 *            ���O�Bnull�͕s�B
	 * @param loader
	 *            �e�N�X�`�����[�_�Bnull�͕s�B
	 */
	private void setNamingResolvedLoader(String name, TextureLoader loader) {
		if (name == null || loader == null) {
			throw new IllegalArgumentException();
		}
		GLTexture desc = textureDescs.get(name);
		if (desc != null) {
			// ���[�h�ς�
			loader.set(desc);
			return;
		}
		CanvasRaster raster = textureImages.get(name);
		if (raster != null) {
			// ���[�h�ς�
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
