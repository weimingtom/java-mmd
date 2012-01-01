package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.image.CanvasImageService;
import net.yzwlab.gwtmmd.client.image.CanvasRaster;
import net.yzwlab.gwtmmd.client.io.FileReadBuffer;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.TEXTURE_DESC;
import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.TargaReader;
import net.yzwlab.javammd.model.IMotionSegment;
import net.yzwlab.javammd.model.MMDModel;

import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.arrays.DataView;
import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint, IMMDTextureProvider {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * WebGLキャンバスを保持します。
	 */
	private GLCanvas glCanvas;

	/**
	 * 結果ラベルを保持します。
	 */
	private Label resultLabel;

	/**
	 * モーションパネルを保持します。
	 */
	private VerticalPanel motionPanel;

	/**
	 * 現在のモデルを保持します。
	 */
	private MMDModel currentModel;

	protected FileReader reader;
	protected List<File> readQueue;

	/**
	 * ローダを保持します。
	 */
	private List<TextureLoader> textureLoaders;

	/**
	 * リソースパネルを保持します。
	 */
	private VerticalPanel resourcePanel;

	/**
	 * 画像サービスを保持します。
	 */
	private CanvasImageService imageService;

	/**
	 * テクスチャ候補の画像を保持します。
	 */
	private Map<String, CanvasRaster> textureImages;

	/**
	 * 名前解決済みのテクスチャローダを保持します。
	 */
	private Map<String, List<TextureLoader>> namedTextureLoaders;

	/**
	 * 構築します。
	 */
	public Main() {
		this.glCanvas = null;
		this.resultLabel = null;
		this.currentModel = null;
		this.motionPanel = null;
		this.reader = new FileReader();
		this.readQueue = new ArrayList<File>();
		this.textureLoaders = new ArrayList<TextureLoader>();
		this.resourcePanel = new VerticalPanel();
		this.imageService = new CanvasImageService(resourcePanel);
		this.textureImages = new HashMap<String, CanvasRaster>();
		this.namedTextureLoaders = new HashMap<String, List<TextureLoader>>();
	}

	@Override
	public void load(byte[] filename, IMMDTextureProvider.Handler handler)
			throws ReadException {
		TEXTURE_DESC desc = new TEXTURE_DESC();
		desc.setTexWidth(100);
		desc.setTexHeight(100);
		desc.setTexMemWidth(100);
		desc.setTexMemHeight(100);
		desc.setTextureId(0L);
		// TODO
		// return desc;

		VerticalPanel vpanel = new VerticalPanel();
		resourcePanel.add(vpanel);
		TextureLoader loader = new TextureLoader(glCanvas, filename, vpanel,
				handler);
		textureLoaders.add(loader);
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		try {
			resultLabel = new Label("(Status)");
			motionPanel = new VerticalPanel();

			Label perfLabel = new Label("(Performance)");

			final DropPanel dropPanel = new DropPanel();
			VerticalPanel dropPanelContainer = new VerticalPanel();
			dropPanel.addStyleName("gwtmmd-drop");

			dropPanelContainer
					.add(new HTML(
							"下のエリアにPMDファイル(モデル)かVMDファイル(モーション)を<br/>ドラッグ＆ドロップするとその内容が反映されます:"));
			dropPanelContainer
					.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
			dropPanelContainer.add(dropPanel);
			dropPanel.setSize("100px", "100px");
			dropPanel.addDragEnterHandler(new DragEnterHandler() {
				@Override
				public void onDragEnter(DragEnterEvent event) {
					dropPanel.addStyleName("gwtmmd-drop-active");
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropPanel.addDragOverHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					// Mandatory handler, otherwise the default
					// behavior will kick in and onDrop will never
					// be called
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropPanel.addDragLeaveHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					dropPanel.removeStyleName("gwtmmd-drop-active");
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropPanel.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					dropPanel.removeStyleName("gwtmmd-drop-active");
					processFiles(event.getDataTransfer()
							.<DataTransferExt> cast().getFiles());
					event.stopPropagation();
					event.preventDefault();
				}
			});

			glCanvas = new GLCanvas(perfLabel, 640, 480);
			glCanvas.setCurrentRy(-1);
			glCanvas.setCurrentRx(1);

			reader.addLoadEndHandler(new LoadEndHandler() {
				@Override
				public void onLoadEnd(LoadEndEvent event) {
					if (reader.getError() == null) {
						if (readQueue.size() > 0) {
							File file = readQueue.get(0);
							try {
								ArrayBuffer buf = reader.getArrayBufferResult();
								if (isModel(file)) {
									loadPMD(glCanvas, buf, null);
								} else if (isMotion(file)) {
									if (currentModel == null) {
										Window.alert("モデルがロードされていません");
										return;
									}
									loadVMD(glCanvas, currentModel,
											file.getName(), buf, null);
								} else if (isTgaImage(file)) {
									loadTGA(file.getName(), buf, null);
								}
							} catch (Throwable e) {
								handleError(file, e.getClass().getName() + ": "
										+ e.getMessage());
							} finally {
								readQueue.remove(0);
								readNext();
							}
						}
					}
				}
			});

			reader.addErrorHandler(new ErrorHandler() {
				@Override
				public void onError(ErrorEvent event) {
					if (readQueue.size() > 0) {
						File file = readQueue.get(0);
						handleError(file, "");
						readQueue.remove(0);
						readNext();
					}
				}
			});

			RootPanel.get("canvas3d").add(glCanvas);

			VerticalPanel vpanel = new VerticalPanel();
			vpanel.add(resultLabel);
			vpanel.add(perfLabel);
			HorizontalPanel buttons = new HorizontalPanel();
			Button button = new Button("Rotate X++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRx(glCanvas.getCurrentRx() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate X--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRx(glCanvas.getCurrentRx() - 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Y++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRy(glCanvas.getCurrentRy() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Y--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRy(glCanvas.getCurrentRy() - 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Z++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRz(glCanvas.getCurrentRz() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Z--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					glCanvas.setCurrentRz(glCanvas.getCurrentRz() - 1);
				}
			});
			buttons.add(button);
			vpanel.add(motionPanel);
			vpanel.add(buttons);

			RootPanel.get("canvas3d_ctrl").add(vpanel);
			RootPanel.get("dropFieldContainer").add(dropPanelContainer);
			RootPanel.get("resources_3d").add(resourcePanel);

			final DialogBox dlg = new LoadingDialogBox("PMD");
			dlg.center();

			greetingService.getDefaultModel(new AsyncCallback<byte[]>() {
				@Override
				public void onFailure(Throwable caught) {
					dlg.hide();
					Window.alert("Error: " + caught.getClass().getName() + ": "
							+ caught.getMessage());
				}

				@Override
				public void onSuccess(byte[] result) {
					loadPMD(glCanvas, result, dlg);
				}
			});
		} catch (IllegalStateException e) {
			Window.alert("初期化に失敗。WebGLをサポートしていない環境かも・・・？");
		}
	}

	private void processFiles(FileList files) {
		for (File file : files) {
			readQueue.add(file);
		}
		readNext();
	}

	private void readNext() {
		if (readQueue.size() > 0) {
			File file = readQueue.get(0);
			try {
				if (isModel(file) || isMotion(file) || isTgaImage(file)) {
					reader.readAsArrayBuffer(file);
				} else {
					readQueue.remove(0);
					readNext();
				}
			} catch (Throwable t) {
				// Necessary for FF (see bug
				// https://bugzilla.mozilla.org/show_bug.cgi?id=701154)
				// Standard-complying browsers will to go in this branch
				handleError(file,
						t.getClass().getName() + ": " + t.getMessage());
				readQueue.remove(0);
				readNext();
			}
		}

	}

	private void handleError(File f, String message) {
		Window.alert("Error: " + f.getName() + ": " + message);
	}

	private void loadPMD(final GLCanvas glCanvas, final byte[] buf,
			final DialogBox dlg) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ArrayBuffer arr = ArrayBuffer.create(buf.length);
				DataView dataView = DataView.createDataView(arr);
				int pos = 0;
				for (byte dt : buf) {
					dataView.setUint8(pos, dt);
					pos++;
				}
				loadPMD(glCanvas, arr, dlg);
			}
		});
	}

	private void loadPMD(final GLCanvas glCanvas, final ArrayBuffer buf,
			DialogBox dlg) {
		if (dlg == null) {
			dlg = new LoadingDialogBox("PMD");
			dlg.center();
		}
		final DialogBox tdlg = dlg;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					MMDModel model = new MMDModel();
					model.openPMD(new FileReadBuffer(buf));

					model.prepare(Main.this, new IMMDTextureProvider.Handler() {
						@Override
						public void onSuccess(byte[] filename, TEXTURE_DESC desc) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onError(byte[] filename, Throwable error) {
							error.printStackTrace();
						}
					});
					glCanvas.removeAllModels();
					glCanvas.addModel(model);

					String msg = "モデル読み込み完了: Bones=" + model.getBoneCount()
							+ ", IKs=" + model.GetIKCount();
					resultLabel.setText(msg);
					currentModel = model;
					motionPanel.clear();

					ArrayList<byte[]> dt = new ArrayList<byte[]>();
					for (TextureLoader textureLoader : textureLoaders) {
						dt.add(textureLoader.getFilename());
					}
					greetingService.getStrings(dt,
							new AsyncCallback<List<String>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Error: "
											+ caught.getClass().getName()
											+ ": " + caught.getMessage());
								}

								@Override
								public void onSuccess(List<String> result) {
									for (int i = result.size() - 1; i >= 0; i--) {
										TextureLoader loader = textureLoaders
												.get(i);
										loader.set(result.get(i));
										textureLoaders.remove(i);
										setNamingResolvedLoader(result.get(i),
												loader);
									}
								}

							});
				} catch (ReadException e) {
					Window.alert(e.getClass().getName() + ": " + e.getMessage());
				} finally {
					tdlg.hide();
				}
			}
		});
	}

	/**
	 * VMDファイルを読み込みます。
	 * 
	 * @param canvas
	 *            キャンバス。nullは不可。
	 * @param model
	 *            モデル。nullは不可。
	 * @param name
	 *            名前。nullは不可。
	 * @param buf
	 *            バッファ。nullは不可。
	 * @param dlg
	 *            ダイアログ。nullを指定可能。
	 */
	private void loadVMD(final GLCanvas canvas, final MMDModel model,
			final String name, final ArrayBuffer buf, DialogBox dlg) {
		if (canvas == null || model == null || name == null || buf == null) {
			throw new IllegalArgumentException();
		}
		if (dlg == null) {
			dlg = new LoadingDialogBox("VMD");
			dlg.center();
		}
		final DialogBox tdlg = dlg;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					IMotionSegment segment = model.openVMD(new FileReadBuffer(
							buf));
					motionPanel.add(new MotionStartButton(canvas, resultLabel,
							24.0f, currentModel, name, segment));

					resultLabel.setText("モーション読み込み完了");
				} catch (Throwable e) {
					Window.alert(e.getClass().getName() + ": " + e.getMessage());
				} finally {
					tdlg.hide();
				}
			}
		});
	}

	/**
	 * TGAファイルを読み込みます。
	 * 
	 * @param name
	 *            名前。nullは不可。
	 * @param buf
	 *            バッファ。nullは不可。
	 * @param dlg
	 *            ダイアログ。nullを指定可能。
	 */
	private void loadTGA(final String name, final ArrayBuffer buf, DialogBox dlg) {
		if (name == null || buf == null) {
			throw new IllegalArgumentException();
		}
		if (dlg == null) {
			dlg = new LoadingDialogBox("TGA");
			dlg.center();
		}
		final DialogBox tdlg = dlg;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					TargaReader reader = new TargaReader();
					IImage image = reader.read(imageService,
							new FileReadBuffer(buf));

					addRaster(name, (CanvasRaster) image);
					resultLabel.setText("TGA読み込み完了: " + image.getWidth() + "x"
							+ image.getHeight());
				} catch (Throwable e) {
					Window.alert(e.getClass().getName() + ": " + e.getMessage());
				} finally {
					tdlg.hide();
				}
			}
		});
	}

	/**
	 * モデルファイルかどうかを判定します。
	 * 
	 * @param file
	 *            ファイル。nullは不可。
	 * @return モデルファイルであればtrue。
	 */
	private boolean isModel(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		return file.getName().toLowerCase().endsWith(".pmd");
	}

	/**
	 * モーションファイルかどうかを判定します。
	 * 
	 * @param file
	 *            ファイル。nullは不可。
	 * @return モーションファイルであればtrue。
	 */
	private boolean isMotion(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		return file.getName().toLowerCase().endsWith(".vmd");
	}

	/**
	 * TGA画像ファイルかどうかを判定します。
	 * 
	 * @param file
	 *            ファイル。nullは不可。
	 * @return TGA画像ファイルであればtrue。
	 */
	private boolean isTgaImage(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		return file.getName().toLowerCase().endsWith(".tga");
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

	/**
	 * ラスタを追加します。
	 * 
	 * @param name
	 *            名前。nullは不可。
	 * @param raster
	 *            ラスタ。nullは不可。
	 */
	private void addRaster(String name, CanvasRaster raster) {
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

	private class LoadingDialogBox extends DialogBox {

		public LoadingDialogBox(String file) {
			setText("Loading...");

			VerticalPanel vpanel = new VerticalPanel();
			vpanel.add(new Label(file + "ファイルを読み込んでいます。ちょっと待ってね・・・"));
			setWidget(vpanel);
		}

	}

}
