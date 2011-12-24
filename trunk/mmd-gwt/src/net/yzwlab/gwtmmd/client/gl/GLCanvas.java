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
	private List<JavaScriptObject> buffers;

	/**
	 * ���f����ێ����܂��B
	 */
	private List<MMDModel> models;

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
		this.buffers = new ArrayList<JavaScriptObject>();
		this.models = new ArrayList<MMDModel>();

		canvasElement.setAttribute("width", String.valueOf(width));
		canvasElement.setAttribute("height", String.valueOf(height));

		setElement(canvasElement);
	}

	/**
	 * ���f����ǉ����܂��B
	 * 
	 * @param model
	 *            ���f���Bnull�͕s�B
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
	 * Perspective�s��𐶐����܂��B
	 * 
	 * @return Perspective�s��B
	 */
	private native JavaScriptObject createPMatrix() /*-{
		var persp = $wnd.mat4.create();
		$wnd.mat4.perspective(45, 4 / 3, 1, 100, persp);
		return persp;
	}-*/;

	/**
	 * ModelView�s��𐶐����܂��B
	 * 
	 * @return ModelView�s��B
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
		program.mvMatrixUniform = gl.getUniformLocation(program, "uMVMatrix");
		program.pMatrixUniform = gl.getUniformLocation(program, "uPMatrix");

		return program;
	}-*/;

	/**
	 * �V�[�����������܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 */
	private native void clearScene(JavaScriptObject gl) /*-{
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
	 * MVMatrix��ݒ肵�܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param program
	 *            �V�F�[�_�Bnull�͕s�B
	 * @param mat
	 *            �s��Bnull�͕s�B
	 */
	private native void setMVMatrix(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject mat) /*-{
		gl.uniformMatrix4fv(program.mvMatrixUniform, false, mat);
	}-*/;

	/**
	 * PMatrix��ݒ肵�܂��B
	 * 
	 * @param gl
	 *            WebGL�I�u�W�F�N�g�Bnull�͕s�B
	 * @param program
	 *            �V�F�[�_�Bnull�͕s�B
	 * @param mat
	 *            �s��Bnull�͕s�B
	 */
	private native void setPMatrix(JavaScriptObject gl,
			JavaScriptObject program, JavaScriptObject mat) /*-{
		gl.uniformMatrix4fv(program.pMatrixUniform, false, mat);
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

	/**
	 * �V�[���`��p�̃^�C�}�[�ł��B
	 */
	private class DrawSceneTimer extends Timer implements IDataMutex {

		/**
		 * Perspective�s���ێ����܂��B
		 */
		private JavaScriptObject pMatrix;

		/**
		 * �\�z���܂��B
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
		 * GL�I�u�W�F�N�g�̒��ۂł��B
		 */
		private class GL implements IGL {

			/**
			 * �o�b�t�@�C���f�b�N�X��ێ����܂��B
			 */
			private int bufferIndex;

			/**
			 * ���_���X�g��ێ����܂��B
			 */
			private JavaScriptObject vertexes;

			/**
			 * �ϊ��s���ێ����܂��B
			 */
			private JavaScriptObject mvMatrix;

			/**
			 * �\�z���܂��B
			 * 
			 * @param model
			 *            ���f���Bnull�͕s�B
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
			 * �o�͂��܂��B
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
			 * ���_�̍ő吔���擾���܂��B
			 * 
			 * @param model
			 *            ���f���Bnull�͕s�B
			 * @return ���_�̍ő吔�B
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
			 * ���_�o�b�t�@�����Z�b�g���܂��B
			 * 
			 * @return ���_�o�b�t�@�B
			 */
			private native JavaScriptObject createVertexes() /*-{
				return new Array();
			}-*/;

			/**
			 * ���_��ǉ����܂��B
			 * 
			 * @param vertexes
			 *            ���_���X�g�Bnull�͕s�B
			 * @param x
			 *            ���W�B
			 * @param y
			 *            ���W�B
			 * @param z
			 *            ���W�B
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
