package net.yzwlab.androidmmd.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.javammd.IGL;

public class ESGL implements IGL {

	private GL10 gl;

	private DrawContext context;

	private ByteBuffer vbb;

	private ByteBuffer nbb;

	private ByteBuffer tbb;

	public ESGL(GL10 gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		this.gl = gl;
		this.context = null;
		this.vbb = null;
		this.nbb = null;
		this.tbb = null;
	}

	public FrontFace glGetFrontFace() {
		return FrontFace.GL_CW;
	}

	public void glFrontFace(FrontFace mode) {
		int imode = 0;
		if (mode == FrontFace.GL_CW) {
			imode = GL10.GL_CW;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glFrontFace(imode);
	}

	public void glBegin(C mode, int vertices) {
		context = new DrawContext(mode, vertices);
	}

	public void glEnd() {
		context.commit();
		context = null;
	}

	public void glVertex3f(float x, float y, float z) {
		context.addVertex(x, y, z);
	}

	public void glTexCoord2f(float x, float y) {
		context.addTexCoord(x, y);
	}

	public void glNormal3f(float x, float y, float z) {
		context.addNormal(x, y, z);
	}

	public long glGetBindTexture(C target) {
		int itarget = 0;
		if (target == C.GL_TEXTURE_2D) {
			itarget = GL10.GL_TEXTURE_2D;
		} else {
			throw new IllegalArgumentException();
		}
		// TODO
		return 0;
	}

	public void glBindTexture(C target, long texture) {
		int itarget = 0;
		if (target == C.GL_TEXTURE_2D) {
			itarget = GL10.GL_TEXTURE_2D;
		} else {
			throw new IllegalArgumentException();
		}
		// gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(itarget, (int) texture);
		// Log.i("GLTest", "Texture:" + texture);
	}

	public void glBlendFunc(C c1, C c2) {
		int ic1 = 0;
		if (c1 == C.GL_SRC_ALPHA) {
			ic1 = GL10.GL_SRC_ALPHA;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_ONE_MINUS_SRC_ALPHA) {
			ic2 = GL10.GL_ONE_MINUS_SRC_ALPHA;
		} else {
			throw new IllegalArgumentException();
		}
		// gl.glBlendFunc(ic1, ic2);
	}

	public void glPushMatrix() {
		gl.glPushMatrix();
	}

	public void glPopMatrix() {
		gl.glPopMatrix();
	}

	public void glScalef(float a1, float a2, float a3) {
		gl.glScalef(a1, a2, a3);
	}

	public void glColor4f(float a1, float a2, float a3, float a4) {
		gl.glColor4f(a1, a2, a3, a4);
	}

	public void glDrawArrays(C mode, int offset, int length) {
		// TODO Auto-generated method stub

	}

	public void glEnable(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL10.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL10.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL10.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glEnable(itarget);
	}

	public void glDisable(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL10.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL10.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL10.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glDisable(itarget);
	}

	public boolean glIsEnabled(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL10.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL10.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL10.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		// return glIsEnabled(itarget);
		return false;
	}

	public void glMaterialfv(C c1, C c2, float[] fv) {
		int ic1 = 0;
		if (c1 == C.GL_FRONT_AND_BACK) {
			ic1 = GL10.GL_FRONT_AND_BACK;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_AMBIENT) {
			ic2 = GL10.GL_AMBIENT;
		} else if (c2 == C.GL_DIFFUSE) {
			ic2 = GL10.GL_DIFFUSE;
		} else if (c2 == C.GL_EMISSION) {
			ic2 = GL10.GL_EMISSION;
		} else if (c2 == C.GL_SPECULAR) {
			ic2 = GL10.GL_SPECULAR;
		} else if (c2 == C.GL_SHININESS) {
			ic2 = GL10.GL_SHININESS;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glMaterialfv(ic1, ic2, fv, 0);
	}

	public void glMaterialf(C c1, C c2, float f) {
		int ic1 = 0;
		if (c1 == C.GL_FRONT_AND_BACK) {
			ic1 = GL10.GL_FRONT_AND_BACK;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_AMBIENT) {
			ic2 = GL10.GL_AMBIENT;
		} else if (c2 == C.GL_DIFFUSE) {
			ic2 = GL10.GL_DIFFUSE;
		} else if (c2 == C.GL_EMISSION) {
			ic2 = GL10.GL_EMISSION;
		} else if (c2 == C.GL_SPECULAR) {
			ic2 = GL10.GL_SPECULAR;
		} else if (c2 == C.GL_SHININESS) {
			ic2 = GL10.GL_SHININESS;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glMaterialf(ic1, ic2, f);
	}

	public void glEnableClientState(C target) {
		// TODO Auto-generated method stub

	}

	public void glDisableClientState(C target) {
		// TODO Auto-generated method stub

	}

	private class DrawContext {

		private C mode;

		private int count;

		private FloatBuffer mvertexBuffer;

		private FloatBuffer mnormBuffer;

		private FloatBuffer mtexCoordBuffer;

		public DrawContext(C mode, int count) {
			this.mode = mode;
			this.count = count;

			if (vbb == null) {
				vbb = ByteBuffer.allocateDirect(count * 3 * 4);
				vbb.order(ByteOrder.nativeOrder());
			} else if (vbb.capacity() < count * 3 * 4) {
				vbb = ByteBuffer.allocateDirect(count * 3 * 4);
				vbb.order(ByteOrder.nativeOrder());
			}
			mvertexBuffer = vbb.asFloatBuffer();
			if (nbb == null) {
				nbb = ByteBuffer.allocateDirect(count * 3 * 4);
				nbb.order(ByteOrder.nativeOrder());
			} else if (nbb.capacity() < count * 3 * 4) {
				nbb = ByteBuffer.allocateDirect(count * 3 * 4);
				nbb.order(ByteOrder.nativeOrder());
			}
			mnormBuffer = nbb.asFloatBuffer();
			if (tbb == null) {
				tbb = ByteBuffer.allocateDirect(count * 2 * 4);
				tbb.order(ByteOrder.nativeOrder());
			} else if (tbb.capacity() < count * 2 * 4) {
				tbb = ByteBuffer.allocateDirect(count * 2 * 4);
				tbb.order(ByteOrder.nativeOrder());
			}
			mtexCoordBuffer = tbb.asFloatBuffer();

			mvertexBuffer.clear();
			mnormBuffer.clear();
			mtexCoordBuffer.clear();
		}

		public void addVertex(float x, float y, float z) {
			mvertexBuffer.put(x);
			mvertexBuffer.put(y);
			mvertexBuffer.put(z);
		}

		public void addNormal(float x, float y, float z) {
			mnormBuffer.put(x);
			mnormBuffer.put(y);
			mnormBuffer.put(z);
		}

		public void addTexCoord(float x, float y) {
			mtexCoordBuffer.put(x);
			mtexCoordBuffer.put(y);
		}

		public void commit() {
			int vmode = 0;
			if (mode == C.GL_TRIANGLES) {
				vmode = GL10.GL_TRIANGLES;
			} else {
				throw new IllegalStateException();
			}
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			// gl.glColor4f(0.0f,0.0f,0.0f,0.0f);

			mvertexBuffer.position(0);
			mnormBuffer.position(0);
			mtexCoordBuffer.position(0);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mvertexBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mnormBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mtexCoordBuffer);

			// Front
			gl.glDrawArrays(vmode, 0, count);
		}
	}

}
