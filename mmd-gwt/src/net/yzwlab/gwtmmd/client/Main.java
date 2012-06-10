package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yzwlab.gwtmmd.client.gl.Camera3D;
import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.gl.GLCanvasEvent;
import net.yzwlab.gwtmmd.client.gl.GLCanvasHandler;
import net.yzwlab.gwtmmd.client.gl.IModelClock;
import net.yzwlab.gwtmmd.client.gl.PixelBuffer;
import net.yzwlab.gwtmmd.client.image.CanvasImageService;
import net.yzwlab.gwtmmd.client.image.CanvasRaster;
import net.yzwlab.gwtmmd.client.io.FileReadBuffer;
import net.yzwlab.gwtmmd.client.model.AnalyzedPMDFile;
import net.yzwlab.javammd.GLTexture;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.PMDFile;
import net.yzwlab.javammd.image.IImage;
import net.yzwlab.javammd.image.TargaReader;
import net.yzwlab.javammd.model.IMotionSegment;
import net.yzwlab.javammd.model.MMDModel;

import org.vectomatic.arrays.ArrayBuffer;
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
import com.google.gwt.core.client.JavaScriptObject;
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
public class Main implements EntryPoint {
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
	private List<GLCanvasManager> canvasManagers;

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
	 * リソースパネルを保持します。
	 */
	private VerticalPanel resourcePanel;

	/**
	 * 画像サービスを保持します。
	 */
	private CanvasImageService imageService;

	/**
	 * レンダリング済みバッファを保持します。
	 */
	private Map<Integer, PixelBuffer> renderedBuffers;

	/**
	 * モデルの現在時刻を保持します。
	 */
	private Long modelCurrentTime;

	/**
	 * 構築します。
	 */
	public Main() {
		this.canvasManagers = new ArrayList<GLCanvasManager>();
		this.resultLabel = null;
		this.currentModel = null;
		this.motionPanel = null;
		this.reader = new FileReader();
		this.readQueue = new ArrayList<File>();
		this.resourcePanel = new VerticalPanel();
		this.imageService = new CanvasImageService(resourcePanel);
		this.renderedBuffers = new HashMap<Integer, PixelBuffer>();
		this.modelCurrentTime = null;
	}

	/**
	 * サイズを変更します。
	 * 
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 */
	public void changeSize(int width, int height) {
		for (GLCanvasManager canvasManager : canvasManagers) {
			canvasManager.getGlCanvas().setSize(width, height);
		}
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
							"下のエリアにPMDファイル(モデル)とか<br/>テクスチャ画像とかVMDファイル(モーション)とかを<br/>ドラッグ＆ドロップするとその内容が反映されます:"));
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

			RootPanel canvasLeft = RootPanel.get("canvas3d_left");
			RootPanel canvasRight = RootPanel.get("canvas3d_right");
			RootPanel canvasMain = RootPanel.get("canvas3d");
			int canvasCount = 0;
			if (canvasLeft != null && canvasRight != null) {
				canvasCount = 2;
			} else if (canvasMain != null) {
				canvasCount = 1;
			} else {
				throw new IllegalStateException();
			}

			final GLCamera camera = new GLCamera();
			// camera.setCurrentRy(-1);
			// camera.setCurrentRx(1);
			for (int i = 0; i < canvasCount; i++) {
				Camera3D.Mode mode = Camera3D.Mode.CENTER;
				if (canvasCount == 2) {
					if (i == 0) {
						mode = Camera3D.Mode.LEFT;
					} else {
						mode = Camera3D.Mode.RIGHT;
					}
				}
				GLCanvas glCanvas = new GLCanvas(new DefaultTimerRunner(),
						new ClockImpl(i), camera, mode, perfLabel, 640, 384);
				canvasManagers
						.add(new GLCanvasManager(glCanvas, resourcePanel));
			}

			reader.addLoadEndHandler(new LoadEndHandler() {
				@Override
				public void onLoadEnd(LoadEndEvent event) {
					if (reader.getError() == null) {
						if (readQueue.size() > 0) {
							File file = readQueue.get(0);
							try {
								if (isModel(file)) {
									ArrayBuffer buf = reader
											.getArrayBufferResult();
									loadPMD(buf, null);
								} else if (isMotion(file)) {
									ArrayBuffer buf = reader
											.getArrayBufferResult();
									if (currentModel == null) {
										Window.alert("モデルがロードされていません");
										return;
									}
									loadVMD(currentModel, file.getName(), buf,
											null);
								} else if (isTgaImage(file)) {
									ArrayBuffer buf = reader
											.getArrayBufferResult();
									loadTGA(file.getName(), buf, null);
								} else if (file.getType().startsWith("image/")) {
									for (GLCanvasManager canvasManager : canvasManagers) {
										canvasManager.addImage(file.getName(),
												reader.getStringResult());
									}
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

			if (canvasLeft != null && canvasRight != null) {
				canvasLeft.add(canvasManagers.get(0).getGlCanvas());
				canvasRight.add(canvasManagers.get(1).getGlCanvas());

				for (int i = 0; i < 2; i++) {
					final int index = i;
					renderedBuffers.put(index, null);
					final GLCanvas canvas = canvasManagers.get(i).getGlCanvas();
					canvas.addGLCanvasHandler(new GLCanvasHandler() {
						@Override
						public void onDraw(GLCanvasEvent event) {
							if (event == null) {
								throw new IllegalArgumentException();
							}
							int width = canvas.getWidth();
							int height = canvas.getHeight();
							PixelBuffer buffer = canvas.readPixels(0, 0, width,
									height);
							setRenderedBuffer(index, buffer);
						}
					});
				}
			} else {
				canvasMain.add(canvasManagers.get(0).getGlCanvas());
			}

			VerticalPanel vpanel = new VerticalPanel();
			vpanel.add(resultLabel);
			vpanel.add(perfLabel);
			HorizontalPanel buttons = new HorizontalPanel();
			Button button = new Button("Rotate X++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRx(camera.getCurrentRx() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate X--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRx(camera.getCurrentRx() - 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Y++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRy(camera.getCurrentRy() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Y--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRy(camera.getCurrentRy() - 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Z++");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRz(camera.getCurrentRz() + 1);
				}
			});
			buttons.add(button);
			button = new Button("Rotate Z--");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					camera.setCurrentRz(camera.getCurrentRz() - 1);
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

			greetingService.getDefaultPMD(new AsyncCallback<AnalyzedPMDFile>() {
				@Override
				public void onFailure(Throwable caught) {
					dlg.hide();
					Window.alert("Error: " + caught.getClass().getName() + ": "
							+ caught.getMessage());
				}

				@Override
				public void onSuccess(AnalyzedPMDFile result) {
					loadPMD(result.getFile(), dlg);
					boolean first = true;
					for (String filename : result.getImageFilenames()) {
						int pos = filename.indexOf("*");
						if (pos > 0) {
							filename = filename.substring(0, pos);
						}
						if (first) {
							for (GLCanvasManager canvasManager : canvasManagers) {
								canvasManager.load(result.getBaseDir(),
										filename);
							}
							first = false;
							continue;
						}
						for (GLCanvasManager canvasManager : canvasManagers) {
							canvasManager.addQueue(result.getBaseDir(),
									filename);
						}
					}
				}
			});

			initHandlers(this);
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
				} else if (file.getType().startsWith("image/")) {
					reader.readAsDataURL(file);
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

	private void loadPMD(final ArrayBuffer buf, DialogBox dlg) {
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

					for (GLCanvasManager canvasManager : canvasManagers) {
						GLCanvas glCanvas = canvasManager.getGlCanvas();
						model.prepare(canvasManager,
								new IGLTextureProvider.Handler() {
									@Override
									public void onSuccess(byte[] filename,
											GLTexture desc) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onError(byte[] filename,
											Throwable error) {
										error.printStackTrace();
									}
								});
						glCanvas.removeAllModels();
						glCanvas.addModel(model);
					}

					String msg = "モデル読み込み完了: Bones=" + model.getBoneCount()
							+ ", IKs=" + model.getIKCount();
					resultLabel.setText(msg);
					currentModel = model;
					motionPanel.clear();

					for (GLCanvasManager canvasManager : canvasManagers) {
						canvasManager.loadTexts(greetingService);
					}
				} catch (ReadException e) {
					Window.alert(e.getClass().getName() + ": " + e.getMessage());
				} finally {
					tdlg.hide();
				}
			}
		});
	}

	private void loadPMD(PMDFile file, DialogBox dlg) {
		if (dlg == null) {
			dlg = new LoadingDialogBox("PMD");
			dlg.center();
		}
		final DialogBox tdlg = dlg;
		try {
			MMDModel model = new MMDModel();
			model.setPMD(new FileReadBuffer(ArrayBuffer.create(1)), file);

			for (GLCanvasManager canvasManager : canvasManagers) {
				GLCanvas glCanvas = canvasManager.getGlCanvas();
				model.prepare(canvasManager, new IGLTextureProvider.Handler() {
					@Override
					public void onSuccess(byte[] filename, GLTexture desc) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(byte[] filename, Throwable error) {
						error.printStackTrace();
					}
				});
				glCanvas.removeAllModels();
				glCanvas.addModel(model);
			}

			String msg = "モデル読み込み完了: Bones=" + model.getBoneCount() + ", IKs="
					+ model.getIKCount();
			resultLabel.setText(msg);
			currentModel = model;
			motionPanel.clear();

			for (GLCanvasManager canvasManager : canvasManagers) {
				canvasManager.loadTexts(greetingService);
			}
		} catch (ReadException e) {
			Window.alert(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			tdlg.hide();
		}
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
	private void loadVMD(final MMDModel model, final String name,
			final ArrayBuffer buf, DialogBox dlg) {
		if (model == null || name == null || buf == null) {
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
					motionPanel.add(new MotionStartButton(getCanvases(),
							resultLabel, 24.0f, currentModel, name, segment));

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

					for (GLCanvasManager canvasManager : canvasManagers) {
						canvasManager.addRaster(name, (CanvasRaster) image);
					}
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
	 * キャンバスを取得します。
	 * 
	 * @return キャンバス。
	 */
	private GLCanvas[] getCanvases() {
		GLCanvas[] r = new GLCanvas[canvasManagers.size()];
		for (int i = 0; i < r.length; i++) {
			r[i] = canvasManagers.get(i).getGlCanvas();
		}
		return r;
	}

	private void setRenderedBuffer(int index, PixelBuffer buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		renderedBuffers.put(index, buffer);

		for (PixelBuffer buf : renderedBuffers.values()) {
			if (buf == null) {
				return;
			}
		}

		setBuffers(renderedBuffers.get(0).getPixelBuffer(), renderedBuffers
				.get(1).getPixelBuffer());

		for (Integer key : renderedBuffers.keySet()) {
			renderedBuffers.put(key, null);
		}
		modelCurrentTime = null;
	}

	private native void initHandlers(Main self) /*-{
		$wnd.set3DCanvasSize = function(size) {
			self.@net.yzwlab.gwtmmd.client.Main::changeSize(II)(size.width, size.height);
		};
	}-*/;

	private native void setBuffers(JavaScriptObject left, JavaScriptObject right) /*-{
		$wnd.canvas.setLeft(new $wnd.RV3DImageData(left));
		$wnd.canvas.setRight(new $wnd.RV3DImageData(right));
		$wnd.canvas.draw();
	}-*/;

	private class ClockImpl implements IModelClock {

		private int index;

		public ClockImpl(int index) {
			this.index = index;
		}

		@Override
		public Long getCurrentTime() {
			if (renderedBuffers.get(index) != null) {
				// バッファに設定済み
				return null;
			}
			if (modelCurrentTime != null) {
				return modelCurrentTime;
			}
			modelCurrentTime = System.currentTimeMillis();
			return modelCurrentTime;
		}
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
