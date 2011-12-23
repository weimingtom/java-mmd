package net.yzwlab.gwtmmd.client.gl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * WebGL�L�����o�X�̎����ł��B
 */
public class GLCanvas extends Widget {

	/**
	 * canvas�v�f��ێ����܂��B
	 */
	private Element canvasElement;

	/**
	 * �^�C�}�[��ێ����܂��B
	 */
	private Timer timer;

	/**
	 * WebGL�I�u�W�F�N�g��ێ����܂��B
	 */
	private JavaScriptObject gl;

	/**
	 * �V�F�[�_�v���O������ێ����܂��B
	 */
	private JavaScriptObject program;

	/**
	 * �o�b�t�@��ێ����܂��B
	 */
	private JavaScriptObject buffer;

	/**
	 * �\�z���܂��B
	 * 
	 * @param width
	 *            ���B
	 * @param height
	 *            �����B
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
	 * ���������܂��B
	 * 
	 * @param elem
	 *            �v�f�Bnull�͕s�B
	 * @return WebGL�I�u�W�F�N�g�B
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
	 * �V�F�[�_���擾���܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param source
	 *            �\�[�X�Bnull�͕s�B
	 * @return �V�F�[�_�B
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
	 * �V�F�[�_���擾���܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param source
	 *            �\�[�X�Bnull�͕s�B
	 * @return �V�F�[�_�B
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
	 * �V�F�[�_�����������܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param vertexShader
	 *            Vertex�V�F�[�_�Bnull�͕s�B
	 * @param fragmentShader
	 *            Fragment�V�F�[�_�Bnull�͕s�B
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
	 * �V�[����`�悵�܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 */
	private native void drawScene(JavaScriptObject gl) /*-{
		gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
		gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	}-*/;

	/**
	 * �o�b�t�@�𐶐����܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @return �o�b�t�@�B
	 */
	private native JavaScriptObject createBuffer(JavaScriptObject gl) /*-{
		return gl.createBuffer();
	}-*/;

	/**
	 * �o�b�t�@�����������܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param buffer
	 *            �o�b�t�@�Bnull�͕s�B
	 * @param vertices
	 *            ���_�Bnull�͕s�B
	 */
	private native void initBuffer(JavaScriptObject gl,
			JavaScriptObject buffer, JavaScriptObject vertices) /*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices),
				gl.STATIC_DRAW);
		buffer.vertexNum = vertices.length / 3;
	}-*/;

	/**
	 * �o�b�t�@�̓��e��`�悵�܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param program
	 *            �V�F�[�_�Bnull�͕s�B
	 * @param buffer
	 *            �o�b�t�@�Bnull�͕s�B
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
	 * �V�[���`��p�̃^�C�}�[�ł��B
	 */
	private class DrawSceneTimer extends Timer {

		/**
		 * �\�z���܂��B
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
