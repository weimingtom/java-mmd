package net.yzwlab.gwtmmd.client;

import net.yzwlab.gwtmmd.client.gl.GLCanvas;
import net.yzwlab.javammd.model.IMotionSegment;
import net.yzwlab.javammd.model.MMDModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * モーション開始ボタンです。
 */
public class MotionStartButton extends Composite implements ClickHandler {

	/**
	 * キャンバスを保持します。
	 */
	private GLCanvas[] canvases;

	/**
	 * 結果ラベルを保持します。
	 */
	private Label resultLabel;

	/**
	 * フレームレートを保持します。
	 */
	private float frameRate;

	/**
	 * モデルを保持します。
	 */
	private MMDModel model;

	/**
	 * 名前を保持します。
	 */
	private String name;

	/**
	 * モーション区分を保持します。
	 */
	private IMotionSegment motionSeg;

	/**
	 * 構築します。
	 * 
	 * @param canvases
	 *            キャンバス。nullは不可。
	 * @param resultLabel
	 *            結果ラベル。nullは不可。
	 * @param frameRate
	 *            フレームレート。
	 * @param model
	 *            モデル。nullは不可。
	 * @param name
	 *            名前。nullは不可。
	 * @param motionSeg
	 *            モーション区分。nullは不可。
	 */
	public MotionStartButton(GLCanvas[] canvases, Label resultLabel,
			float frameRate, MMDModel model, String name,
			IMotionSegment motionSeg) {
		if (canvases == null || resultLabel == null || model == null
				|| name == null || motionSeg == null) {
			throw new IllegalArgumentException();
		}
		this.canvases = canvases;
		this.resultLabel = resultLabel;
		this.frameRate = frameRate;
		this.model = model;
		this.name = name;
		this.motionSeg = motionSeg;

		Button button = new Button("再生:" + name);
		button.addClickHandler(this);
		initWidget(button);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event == null) {
			throw new IllegalArgumentException();
		}
		for (GLCanvas canvas : canvases) {
			canvas.setMotion(model, frameRate, motionSeg);
		}
		resultLabel.setText("モーション実行開始: " + name);
	}

}
