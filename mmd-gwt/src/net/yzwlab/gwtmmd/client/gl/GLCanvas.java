package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * WebGLキャンバスの実装です。
 */
public class GLCanvas extends Widget {

	/**
	 * canvas要素を保持します。
	 */
	private Element canvasElement;

	/**
	 * タイマーを保持します。
	 */
	private Timer timer;

	/**
	 * WebGLオブジェクトを保持します。
	 */
	private JavaScriptObject gl;

	/**
	 * シェーダプログラムを保持します。
	 */
	private JavaScriptObject program;

	/**
	 * バッファを保持します。
	 */
	private JavaScriptObject buffer;

	/**
	 * 構築します。
	 * 
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 */
	public GLCanvas(int width, int height) {
		this.canvasElement = Document.get().createElement("canvas");
		this.timer = null;
		this.gl = null;
		this.program = null;
		this.buffer = null;

		canvasElement.setAttribute("width", String.valueOf(width));
		canvasElement.setAttribute("height", String.valueOf(height));

		setElement(canvasElement);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		gl = attach(canvasElement);
		Document doc = Document.get();
		JavaScriptObject vs = getVertexShader(gl,
				doc.getElementById("shader-vs").getInnerText());
		JavaScriptObject fs = getFragmentShader(gl,
				doc.getElementById("shader-fs").getInnerText());
		program = initShaders(gl, vs, fs);

		buffer = createBuffer(gl);
		initBuffer(gl, buffer, createTestVertexArray());

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new DrawSceneTimer();
		timer.scheduleRepeating(1000 / 30);
	}

	@Override
	protected void onUnload() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		gl = null;
		// detach(canvasElement);

		super.onUnload();
	}

	/**
	 * 初期化します。
	 * 
	 * @param elem
	 *            要素。nullは不可。
	 * @return WebGLオブジェクト。
	 */
	private native JavaScriptObject attach(Element elem) /*-{
		var gl = elem.getContext("experimental-webgl");
		if (!gl) {
			throw "Failed to initialize WebGL";
		}
		gl.viewportWidth = elem.width;
		gl.viewportHeight = elem.height;

		gl.clearColor(0.0, 0.0, 0.0, 1.0);
		gl.clearDepth(1.0);
		gl.enable(gl.DEPTH_TEST);
		gl.depthFunc(gl.LEQUAL);

		return gl;
	}-*/;

	/**
	 * シェーダを取得します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param source
	 *            ソース。nullは不可。
	 * @return シェーダ。
	 */
	private native JavaScriptObject getFragmentShader(JavaScriptObject gl,
			String source) /*-{
		var shader = gl.createShader(gl.FRAGMENT_SHADER);
		gl.shaderSource(shader, source);
		gl.compileShader(shader);

		if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
			throw "Failed to compile";
		}
		return shader;
	}-*/;

	/**
	 * シェーダを取得します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param source
	 *            ソース。nullは不可。
	 * @return シェーダ。
	 */
	private native JavaScriptObject getVertexShader(JavaScriptObject gl,
			String source) /*-{
		var shader = gl.createShader(gl.VERTEX_SHADER);
		gl.shaderSource(shader, source);
		gl.compileShader(shader);

		if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
			throw "Failed to compile";
		}
		return shader;
	}-*/;

	/**
	 * シェーダを初期化します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param vertexShader
	 *            Vertexシェーダ。nullは不可。
	 * @param fragmentShader
	 *            Fragmentシェーダ。nullは不可。
	 * @return
	 */
	private native JavaScriptObject initShaders(JavaScriptObject gl,
			JavaScriptObject vertexShader, JavaScriptObject fragmentShader) /*-{
		var program = gl.createProgram();

		gl.attachShader(program, vertexShader);
		gl.attachShader(program, fragmentShader);

		gl.linkProgram(program);

		if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
			throw "Failed to create program";
		}

		gl.useProgram(program);

		var attr = gl.getAttribLocation(program, "pos");
		gl.enableVertexAttribArray(attr);
		return program;
	}-*/;

	/**
	 * シーンを描画します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 */
	private native void drawScene(JavaScriptObject gl) /*-{
		gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
		gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	}-*/;

	/**
	 * バッファを生成します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @return バッファ。
	 */
	private native JavaScriptObject createBuffer(JavaScriptObject gl) /*-{
		return gl.createBuffer();
	}-*/;

	/**
	 * バッファを初期化します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param buffer
	 *            バッファ。nullは不可。
	 * @param vertices
	 *            頂点。nullは不可。
	 */
	private native void initBuffer(JavaScriptObject gl,
			JavaScriptObject buffer, JavaScriptObject vertices) /*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices),
				gl.STATIC_DRAW);
		buffer.vertexNum = vertices.length / 3;
	}-*/;

	/**
	 * バッファの内容を描画します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param program
	 *            シェーダ。nullは不可。
	 * @param buffer
	 *            バッファ。nullは不可。
	 */
	private native void drawArrays(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject buffer)/*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
		gl.vertexAttribPointer(program.vertexPositionAttribute, 3, gl.FLOAT,
				false, 0, 0);
		gl.drawArrays(gl.TRIANGLES, 0, buffer.vertexNum);
	}-*/;

	private native JavaScriptObject createTestVertexArray() /*-{
		return [ 0.0, 0.25, 0.0, -0.25, -0.25, 0.0, 0.25, -0.25, 0.0, -0.5,
				0.25, 0.0, -0.75, -0.25, 0.0, -0.25, -0.25, 0.0, 0.5, 0.25,
				0.0, 0.25, -0.25, 0.0, 0.75, -0.25, 0.0 ];
	}-*/;

	/**
	 * シーン描画用のタイマーです。
	 */
	private class DrawSceneTimer extends Timer {

		/**
		 * 構築します。
		 */
		public DrawSceneTimer() {
			;
		}

		@Override
		public void run() {
			drawScene(gl);

			drawArrays(gl, program, buffer);
		}

	}

}
