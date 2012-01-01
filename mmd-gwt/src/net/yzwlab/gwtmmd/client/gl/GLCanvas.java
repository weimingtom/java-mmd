package net.yzwlab.gwtmmd.client.gl;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.IDataMutex;
import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.model.IMotionSegment;
import net.yzwlab.javammd.model.MMDModel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * WebGLキャンバスの実装です。
 */
public class GLCanvas extends Widget {

	/**
	 * 性能表示ラベルを保持します。
	 */
	private Label performanceLabel;

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
	private List<BufferGroup> buffers;

	/**
	 * モデルを保持します。
	 */
	private List<DynamicModel> models;

	/**
	 * 回転を保持します。
	 */
	private int currentRx;

	/**
	 * 回転を保持します。
	 */
	private int currentRy;

	/**
	 * 回転を保持します。
	 */
	private int currentRz;

	/**
	 * テクスチャを保持します。
	 */
	private List<JavaScriptObject> textures;

	/**
	 * 構築します。
	 * 
	 * @param performanceLabel
	 *            性能表示ラベル。nullは不可。
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 */
	public GLCanvas(Label performanceLabel, int width, int height) {
		if (performanceLabel == null) {
			throw new IllegalArgumentException();
		}
		this.performanceLabel = performanceLabel;
		this.canvasElement = Document.get().createElement("canvas");
		this.timer = null;
		this.gl = null;
		this.program = null;
		this.buffers = new ArrayList<BufferGroup>();
		this.models = new ArrayList<DynamicModel>();
		this.currentRx = 0;
		this.currentRy = 0;
		this.currentRz = 0;
		this.textures = new ArrayList<JavaScriptObject>();

		canvasElement.setAttribute("width", String.valueOf(width));
		canvasElement.setAttribute("height", String.valueOf(height));

		setElement(canvasElement);
	}

	public int getCurrentRx() {
		return currentRx;
	}

	public void setCurrentRx(int currentRx) {
		this.currentRx = currentRx;
	}

	public int getCurrentRy() {
		return currentRy;
	}

	public void setCurrentRy(int currentRy) {
		this.currentRy = currentRy;
	}

	public int getCurrentRz() {
		return currentRz;
	}

	public void setCurrentRz(int currentRz) {
		this.currentRz = currentRz;
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
		models.add(new DynamicModel(model));
	}

	/**
	 * モーションを設定します。
	 * 
	 * @param model
	 *            モデル。nullは不可。
	 * @param frameRate
	 *            フレームレート。
	 * @param motionSeg
	 *            モーション区分。null可。
	 */
	public void setMotion(MMDModel model, float frameRate,
			IMotionSegment motionSeg) {
		if (model == null) {
			throw new IllegalArgumentException();
		}
		for (DynamicModel targetModel : models) {
			if (targetModel.model == model) {
				targetModel.setMotion(frameRate, motionSeg);
				return;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * モデルをすべて削除します。
	 */
	public void removeAllModels() {
		models.clear();
	}

	/**
	 * テクスチャを生成します。
	 * 
	 * @param imageElement
	 *            画像要素。nullは不可。
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 * @return テクスチャのインデックス。
	 */
	public int createTexture(Element imageElement, int width, int height) {
		JavaScriptObject t = createTexture(gl, imageElement, width, height);
		int index = textures.size();
		textures.add(t);
		return index + 1;
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		gl = attach(canvasElement);
		if (gl == null) {
			throw new IllegalStateException();
		}
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
	 * @param rx
	 *            回転。
	 * @param ry
	 *            回転。
	 * @param rz
	 *            回転。
	 * @return ModelView行列。
	 */
	private native JavaScriptObject createMVMatrix(int rx, int ry, int rz) /*-{
		var modelView = $wnd.mat4.create();

		$wnd.mat4.identity(modelView); // Set to identity
		$wnd.mat4.translate(modelView, [ 0, -5, -15 ]); // Translate back 10 units
		$wnd.mat4.rotate(modelView, Math.PI / 8 * rx, [ 1, 0, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(modelView, Math.PI / 8 * ry, [ 0, 1, 0 ]); // Rotate 90 degrees around the Y axis
		$wnd.mat4.rotate(modelView, Math.PI / 8 * rz, [ 0, 0, 1 ]); // Rotate 90 degrees around the Y axis
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
			//throw "Failed to initialize WebGL";
			return null;
		}
		gl.viewportWidth = elem.width;
		gl.viewportHeight = elem.height;

		gl.clearColor(0.0, 0.0, 0.0, 1.0);
		gl.clearDepth(1.0);
		gl.enable(gl.DEPTH_TEST);
		gl.depthFunc(gl.LEQUAL);

		gl.enable(gl.TEXTURING);
		gl.enable(gl.TEXTURE_2D);
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
			throw "Failed to compile: " + gl.getShaderInfoLog(shader);
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
			throw "Failed to compile: " + gl.getShaderInfoLog(shader);
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
		program.vertexPositionAttribute = attr;

		attr = gl.getAttribLocation(program, "norm");
		gl.enableVertexAttribArray(attr);
		program.vertexNormalAttribute = attr;

		attr = gl.getAttribLocation(program, "coord");
		gl.enableVertexAttribArray(attr);
		program.vertexCoordAttribute = attr;

		program.mvMatrixUniform = gl.getUniformLocation(program, "uMVMatrix");
		program.pMatrixUniform = gl.getUniformLocation(program, "uPMatrix");
		program.normalMatrixUniform = gl.getUniformLocation(program,
				"uNormalMatrix");
		program.texture0 = gl.getUniformLocation(program,
				"texture0");

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
	private native void initBuffer3(JavaScriptObject gl,
			JavaScriptObject buffer, JavaScriptObject vertices) /*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices),
				gl.STATIC_DRAW);
		buffer.vertexNum = vertices.length / 3;
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
	private native void initBuffer2(JavaScriptObject gl,
			JavaScriptObject buffer, JavaScriptObject vertices) /*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices),
				gl.STATIC_DRAW);
		buffer.vertexNum = vertices.length / 2;
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

		var nmat = $wnd.mat4.create();
		;
		$wnd.mat4.inverse(mat, nmat);
		$wnd.mat4.transpose(nmat);
		gl.uniformMatrix4fv(program.normalMatrixUniform, false, nmat);
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
	 * @param vbuffer
	 *            バッファ。nullは不可。
	 * @param nbuffer
	 *            バッファ。nullは不可。
	 * @param cbuffer
	 *            バッファ。nullは不可。
	 * @param texture
	 *            テクスチャ。nullは不可。
	 */
	private native void drawArrays(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject vbuffer,
			JavaScriptObject nbuffer, JavaScriptObject cbuffer,
			JavaScriptObject texture)/*-{
		gl.bindBuffer(gl.ARRAY_BUFFER, vbuffer);
		gl.vertexAttribPointer(program.vertexPositionAttribute, 3, gl.FLOAT,
				false, 0, 0);
		gl.bindBuffer(gl.ARRAY_BUFFER, nbuffer);
		gl.vertexAttribPointer(program.vertexNormalAttribute, 3, gl.FLOAT,
				false, 0, 0);
		gl.bindBuffer(gl.ARRAY_BUFFER, cbuffer);
		gl.vertexAttribPointer(program.vertexCoordAttribute, 2, gl.FLOAT,
				false, 0, 0);

		gl.activeTexture(gl.TEXTURE0);
		gl.bindTexture(gl.TEXTURE_2D, texture);
		gl.uniform1i(program.texture0, 0);
		gl.drawArrays(gl.TRIANGLES, 0, vbuffer.vertexNum);
	}-*/;

	/**
	 * テクスチャを生成します。
	 * 
	 * @param gl
	 *            GL。nullは不可。
	 * @param imageElement
	 *            画像要素。nullは不可。
	 * @param width
	 *            幅。
	 * @param height
	 *            高さ。
	 * @return テクスチャ。
	 */
	private native JavaScriptObject createTexture(JavaScriptObject gl,
			Element imageElement, int width, int height) /*-{
		var texture = gl.createTexture();
		gl.activeTexture(gl.TEXTURE0);
		gl.bindTexture(gl.TEXTURE_2D, texture);
		gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA,
				gl.UNSIGNED_SHORT_4_4_4_4, imageElement);
		gl.generateMipmap(gl.TEXTURE_2D);
		gl.bindTexture(gl.TEXTURE_2D, null);
		return texture;
	}-*/;

	/**
	 * 動的モデルを実装します。
	 */
	private class DynamicModel {

		/**
		 * モデルを保持します。
		 */
		private MMDModel model;

		/**
		 * モーションセグメントを保持します。
		 */
		private IMotionSegment motionSeg;

		/**
		 * 時間のオフセットを保持します。
		 */
		private Long offsetTime;

		/**
		 * フレームレートを保持します。
		 */
		private float frameRate;

		/**
		 * 構築します。
		 * 
		 * @param model
		 *            モデル。nullは不可。
		 */
		public DynamicModel(MMDModel model) {
			if (model == null) {
				throw new IllegalArgumentException();
			}
			this.model = model;
			this.motionSeg = null;
			this.offsetTime = null;
			this.frameRate = 0.0f;
		}

		/**
		 * モーションを設定します。
		 * 
		 * @param frameRate
		 *            フレームレート。
		 * @param motionSeg
		 *            モーション。nullを指定可能。
		 */
		public void setMotion(float frameRate, IMotionSegment motionSeg) {
			this.frameRate = frameRate;
			this.motionSeg = motionSeg;
			this.offsetTime = null;
		}

		/**
		 * 現在のフレームを取得します。
		 * 
		 * @param currentTime
		 *            現在時刻。
		 * @return 現在のフレーム。
		 */
		public float getCurrentFrame(long currentTime) {
			if (motionSeg == null) {
				return 0.0f;
			}
			if (offsetTime == null) {
				offsetTime = currentTime;
			}
			return motionSeg.getFrame(frameRate, currentTime - offsetTime);
		}

	}

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
			long beginTime = System.currentTimeMillis();
			long updateEndTime = 0L;
			try {
				clearScene(gl);
				setPMatrix(gl, program, pMatrix);

				long currentTime = System.currentTimeMillis();
				for (DynamicModel model : models) {
					model.model.UpdateAsync(this,
							model.getCurrentFrame(currentTime));
				}

				updateEndTime = System.currentTimeMillis();

				for (DynamicModel model : models) {
					GL glctx = new GL(model.model, currentRx, currentRy,
							currentRz);
					model.model.draw(glctx);
					glctx.flush();
				}
			} finally {
				long dur = System.currentTimeMillis() - updateEndTime;
				long updateDur = updateEndTime - beginTime;
				performanceLabel.setText("モデル更新時間: " + updateDur + "msec."
						+ ", レンダリング時間: " + dur + "msec.");
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
			 * 法線ベクトルリストを保持します。
			 */
			private JavaScriptObject normals;

			/**
			 * テクスチャ座標リストを保持します。
			 */
			private JavaScriptObject coords;

			/**
			 * 変換行列を保持します。
			 */
			private JavaScriptObject mvMatrix;

			/**
			 * バッファのインデックスを保持します。
			 */
			private int vertCount;

			/**
			 * バッファのインデックスを保持します。
			 */
			private int normCount;

			/**
			 * バッファのインデックスを保持します。
			 */
			private int coordCount;

			/**
			 * 現在のテクスチャを保持します。
			 */
			private JavaScriptObject currentTexture;

			/**
			 * 構築します。
			 * 
			 * @param model
			 *            モデル。nullは不可。
			 * @param rx
			 *            回転。
			 * @param ry
			 *            回転。
			 * @param rz
			 *            回転。
			 */
			public GL(MMDModel model, int rx, int ry, int rz) {
				if (model == null) {
					throw new IllegalArgumentException();
				}
				this.bufferIndex = 0;
				this.vertexes = null;
				this.normals = null;
				this.coords = null;
				this.mvMatrix = createMVMatrix(rx, ry, rz);
				this.vertCount = 0;
				this.normCount = 0;
				this.coordCount = 0;
			}

			/**
			 * 出力します。
			 */
			public void flush() {
				setMVMatrix(gl, program, mvMatrix);

				for (int i = 0; i < bufferIndex; i++) {
					BufferGroup g = buffers.get(i);
					drawArrays(gl, program, g.vbuffer, g.nbuffer, g.cbuffer,
							g.texture);
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
			public void glBegin(C mode, int count) {
				if (mode == null) {
					throw new IllegalArgumentException();
				}

				vertCount = 0;
				normCount = 0;
				coordCount = 0;
				vertexes = createVertexes3(count);
				normals = createVertexes3(count);
				coords = createVertexes2(count);
			}

			@Override
			public void glEnd() {
				BufferGroup buffer = null;
				if (bufferIndex >= buffers.size()) {
					buffer = new BufferGroup(createBuffer(gl),
							createBuffer(gl), createBuffer(gl));
					buffers.add(buffer);
				} else {
					buffer = buffers.get(bufferIndex);
				}
				initBuffer3(gl, buffer.vbuffer, vertexes);
				initBuffer3(gl, buffer.nbuffer, normals);
				initBuffer2(gl, buffer.cbuffer, coords);
				buffer.texture = currentTexture;

				bufferIndex++;
			}

			@Override
			public void glVertex3f(float x, float y, float z) {
				pushVertexes(vertexes, vertCount, x, y, z);
				vertCount++;
			}

			@Override
			public void glTexCoord2f(float x, float y) {
				pushVertexes(coords, coordCount, x, y);
				coordCount++;
			}

			@Override
			public void glNormal3f(float x, float y, float z) {
				pushVertexes(normals, normCount, x, y, z);
				normCount++;
			}

			@Override
			public void glBindTexture(C target, long texture) {
				if (texture > 0) {
					int index = ((int) (texture - 1));
					currentTexture = textures.get(index);
				} else {
					currentTexture = null;
				}
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
			 * @param length
			 *            長さ。
			 * @return 頂点バッファ。
			 */
			private native JavaScriptObject createVertexes3(int length) /*-{
				return new Array(length * 3);
			}-*/;

			/**
			 * 頂点バッファをリセットします。
			 * 
			 * @param length
			 *            長さ。
			 * @return 頂点バッファ。
			 */
			private native JavaScriptObject createVertexes2(int length) /*-{
				return new Array(length * 2);
			}-*/;

			/**
			 * 頂点を追加します。
			 * 
			 * @param vertexes
			 *            頂点リスト。nullは不可。
			 * @param offset
			 *            オフセット。
			 * @param x
			 *            座標。
			 * @param y
			 *            座標。
			 * @param z
			 *            座標。
			 */
			private native void pushVertexes(JavaScriptObject vertexes,
					int offset, float x, float y, float z) /*-{
				vertexes[offset * 3] = x;
				vertexes[offset * 3 + 1] = y;
				vertexes[offset * 3 + 2] = -z;
			}-*/;

			/**
			 * 頂点を追加します。
			 * 
			 * @param vertexes
			 *            頂点リスト。nullは不可。
			 * @param offset
			 *            オフセット。
			 * @param x
			 *            座標。
			 * @param y
			 *            座標。
			 */
			private native void pushVertexes(JavaScriptObject vertexes,
					int offset, float x, float y) /*-{
				vertexes[offset * 2] = x;
				vertexes[offset * 2 + 1] = y;
			}-*/;

		}

	}

	private class BufferGroup {

		private JavaScriptObject vbuffer;

		private JavaScriptObject nbuffer;

		private JavaScriptObject cbuffer;

		private JavaScriptObject texture;

		public BufferGroup(JavaScriptObject vbuffer, JavaScriptObject nbuffer,
				JavaScriptObject cbuffer) {
			super();
			this.vbuffer = vbuffer;
			this.nbuffer = nbuffer;
			this.cbuffer = cbuffer;
			this.texture = null;
		}

	}

}