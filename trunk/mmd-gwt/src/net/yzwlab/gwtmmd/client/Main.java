package net.yzwlab.gwtmmd.client;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.gwtmmd.client.io.FileReadBuffer;
import net.yzwlab.javammd.ReadException;
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
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

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

	protected FileReader reader;
	protected List<File> readQueue;

	public Main() {
		this.reader = new FileReader();
		this.readQueue = new ArrayList<File>();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
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
				processFiles(event.getDataTransfer().<DataTransferExt> cast()
						.getFiles());
				event.stopPropagation();
				event.preventDefault();
			}
		});

		reader.addLoadEndHandler(new LoadEndHandler() {
			@Override
			public void onLoadEnd(LoadEndEvent event) {
				if (reader.getError() == null) {
					if (readQueue.size() > 0) {
						File file = readQueue.get(0);
						try {
							Window.alert("Loaded: " + file.getName());
							MMDModel model = new MMDModel();
							ArrayBuffer buf = reader.getArrayBufferResult();
							model.OpenPMD(new FileReadBuffer(buf));
							Window.alert("MMD: Bones=" + model.GetBoneCount());
						} catch (ReadException e) {
							handleError(
									file,
									e.getClass().getName() + ": "
											+ e.getMessage());
						} catch (Throwable e) {
							handleError(
									file,
									e.getClass().getName() + ": "
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

		RootPanel.get("canvas3d").add(new GLCanvas(640, 480));
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
			String type = file.getType();
			try {
				if (file.getName().toLowerCase().endsWith(".pmd")) {
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
}
