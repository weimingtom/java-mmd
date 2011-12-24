package net.yzwlab.gwtmmd.client.gl;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.model.MMDModel;

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
	private List<JavaScriptObject> buffers;

	/**
	 * モデルを保持します。
	 */
	private List<MMDModel> models;

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
		this.buffers = new ArrayList<JavaScriptObject>();
		this.models = new ArrayList<MMDModel>();

		canvasElement.setAttribute("width", String.valueOf(width));
		canvasElement.setAttribute("height", String.valueOf(height));

		setElement(canvasElement);
	}

	/**
	 * モデルを追加します。
	 * 
	 * @param model
	 *            モデル。nullは不可。
	 */
	public void addModel(MMDModel model) {
		if (model == null) {
			throw new IllegalArgumentException();
		}
		models.add(model);
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
	 * Perspective行列を生成します。
	 * 
	 * @return Perspective行列。
	 */
	private native JavaScriptObject createPMatrix() /*-{
		var persp = $wnd.mat4.create();
		$wnd.mat4.perspective(45, 4 / 3, 1, 100, persp);
		return persp;
	}-*/;

	/**
	 * ModelView行列を生成します。
	 * 
	 * @return ModelView行列。
	 */
	private native JavaScriptObject createMVMatrix() /*-{
		var modelView = $wnd.mat4.create();

		$wnd.mat4.identity(modelView); // Set to identity
		$wnd.mat4.translate(modelView, [ 0, -5, -15 ]); // Translate back 10 units
		$wnd.mat4.rotate(modelView, Math.PI / 4, [ 0, 0, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.scale(modelView, [ 0.5, 0.5, 0.5 ]); // Scale by 200%
		return modelView;
	}-*/;

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
		program.mvMatrixUniform = gl.getUniformLocation(program, "uMVMatrix");
		program.pMatrixUniform = gl.getUniformLocation(program, "uPMatrix");

		return program;
	}-*/;

	/**
	 * シーンを消去します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 */
	private native void clearScene(JavaScriptObject gl) /*-{
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
	 * MVMatrixを設定します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param program
	 *            シェーダ。nullは不可。
	 * @param mat
	 *            行列。nullは不可。
	 */
	private native void setMVMatrix(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject mat) /*-{
		gl.uniformMatrix4fv(program.mvMatrixUniform, false, mat);
	}-*/;

	/**
	 * PMatrixを設定します。
	 * 
	 * @param gl
	 *            WebGLオブジェクト。nullは不可。
	 * @param program
	 *            シェーダ。nullは不可。
	 * @param mat
	 *            行列。nullは不可。
	 */
	private native void setPMatrix(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject mat) /*-{
		gl.uniformMatrix4fv(program.pMatrixUniform, false, mat);
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

	/**
	 * シーン描画用のタイマーです。
	 */
	private class DrawSceneTimer extends Timer implements IDataMutex {

		/**
		 * Perspective行列を保持します。
		 */
		private JavaScriptObject pMatrix;

		/**
		 * 構築します。
		 */
		public DrawSceneTimer() {
			this.pMatrix = createPMatrix();
		}

		@Override
		public void run() {
			clearScene(gl);
			setPMatrix(gl, program, pMatrix);

			for (MMDModel model : models) {
				GL glctx = new GL(model);
				model.DrawAsync(this, glctx);
				glctx.flush();
			}
		}

		@Override
		public void Begin() {
			;
		}

		@Override
		public void End() {
			;
		}

		/**
		 * GLオブジェクトの抽象です。
		 */
		private class GL implements IGL {

			/**
			 * バッファインデックスを保持します。
			 */
			private int bufferIndex;

			/**
			 * 頂点リストを保持します。
			 */
			private JavaScriptObject vertexes;

			/**
			 * 変換行列を保持します。
			 */
			private JavaScriptObject mvMatrix;

			/**
			 * 構築します。
			 * 
			 * @param model
			 *            モデル。nullは不可。
			 */
			public GL(MMDModel model) {
				if (model == null) {
					throw new IllegalArgumentException();
				}
				this.bufferIndex = 0;
				this.vertexes = createVertexes();
				this.mvMatrix = createMVMatrix();
			}

			/**
			 * 出力します。
			 */
			public void flush() {
				setMVMatrix(gl, program, mvMatrix);
				for (int i = 0; i < bufferIndex; i++) {
					drawArrays(gl, program, buffers.get(i));
				}
			}

			@Override
			public C getGlFontFaceCode(int target) {
				return C.GL_FRONT_AND_BACK;
			}

			@Override
			public void glFrontFace(C mode) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glBegin(C mode) {
				if (mode == null) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public void glEnd() {
				JavaScriptObject buffer = null;
				if (bufferIndex >= buffers.size()) {
					buffer = createBuffer(gl);
					buffers.add(buffer);
				} else {
					buffer = buffers.get(bufferIndex);
				}
				initBuffer(gl, buffer, vertexes);

				bufferIndex++;

				vertexes = createVertexes();
			}

			@Override
			public void glVertex3f(float x, float y, float z) {
				pushVertexes(vertexes, x, y, z);
			}

			@Override
			public void glTexCoord2f(float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glNormal3f(float x, float y, float z) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glBindTexture(C target, long texture) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glBlendFunc(C c1, C c2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glPushMatrix() {
				// TODO Auto-generated method stub

			}

			@Override
			public void glPopMatrix() {
				// TODO Auto-generated method stub

			}

			@Override
			public void glScalef(float a1, float a2, float a3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glColor4f(float a1, float a2, float a3, float a4) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glDrawArrays(C mode, int offset, int length) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glEnable(C target) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glDisable(C target) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean glIsEnabled(C target) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void glMaterialfv(C c1, C c2, float[] fv) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glMaterialf(C c1, C c2, float f) {
				// TODO Auto-generated method stub

			}

			@Override
			public int glGetIntegerv(C target) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void glEnableClientState(C target) {
				// TODO Auto-generated method stub

			}

			@Override
			public void glDisableClientState(C target) {
				// TODO Auto-generated method stub

			}

			/**
			 * 頂点の最大数を取得します。
			 * 
			 * @param model
			 *            モデル。nullは不可。
			 * @return 頂点の最大数。
			 */
			private int getMaxVertexesSize(MMDModel model) {
				if (model == null) {
					throw new IllegalArgumentException();
				}
				int size = 0;
				for (int i = 0; i < model.getMaterialCount(); i++) {
					size = Math
							.max(size, model.getMaterial(i).getVertexCount());
				}
				return size;
			}

			/**
			 * 頂点バッファをリセットします。
			 * 
			 * @return 頂点バッファ。
			 */
			private native JavaScriptObject createVertexes() /*-{
				return new Array();
			}-*/;

			/**
			 * 頂点を追加します。
			 * 
			 * @param vertexes
			 *            頂点リスト。nullは不可。
			 * @param x
			 *            座標。
			 * @param y
			 *            座標。
			 * @param z
			 *            座標。
			 */
			private native void pushVertexes(JavaScriptObject vertexes,
					float x, float y, float z) /*-{
				vertexes.push(x);
				vertexes.push(y);
				vertexes.push(z);
			}-*/;

		}

	}

}
