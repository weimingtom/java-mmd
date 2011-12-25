package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.io.FileReadBuffer;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.TEXTURE_DESC;
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
	 * 構築します。
	 */
	public Main() {
		this.resultLabel = null;
		this.currentModel = null;
		this.motionPanel = null;
		this.reader = new FileReader();
		this.readQueue = new ArrayList<File>();
	}

	@Override
	public TEXTURE_DESC Load(byte[] filename) throws ReadException {
		TEXTURE_DESC desc = new TEXTURE_DESC();
		desc.setTexWidth(100);
		desc.setTexHeight(100);
		desc.setTexMemWidth(100);
		desc.setTexMemHeight(100);
		desc.setTextureId(0L);
		return desc;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		try {
			resultLabel = new Label("(Status)");
			motionPanel = new VerticalPanel();

			final DropPanel dropPanel = new DropPanel();
			dropPanel.addStyleName("gwtmmd-drop");

			RootPanel.get("dropFieldContainer").add(dropPanel);
			dropPanel.setSize("200px", "200px");
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

			final GLCanvas glCanvas = new GLCanvas(640, 480);
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

			final DialogBox dlg = new LoadingDialogBox();
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
				if (isModel(file) || isMotion(file)) {
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
			dlg = new LoadingDialogBox();
			dlg.center();
		}
		final DialogBox tdlg = dlg;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					MMDModel model = new MMDModel();
					model.openPMD(new FileReadBuffer(buf));

					model.Prepare(Main.this);
					glCanvas.removeAllModels();
					glCanvas.addModel(model);

					String msg = "モデル読み込み完了: Bones=" + model.getBoneCount()
							+ ", IKs=" + model.GetIKCount();
					resultLabel.setText(msg);
					currentModel = model;
					motionPanel.clear();
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
			dlg = new LoadingDialogBox();
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

	private class LoadingDialogBox extends DialogBox {

		public LoadingDialogBox() {
			setText("Loading...");

			VerticalPanel vpanel = new VerticalPanel();
			vpanel.add(new Label("Loading PMD file. Please wait..."));
			setWidget(vpanel);
		}

	}

}
