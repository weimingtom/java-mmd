package net.yzwlab.androidmmd.cube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.yzwlab.androidmmd.gl.AbstractModel3D;

/**
 * �P���Ȏl�p�`�ł��B
 */
public class SimpleCube extends AbstractModel3D {

	private final FloatBuffer mVertexBuffer;

	public SimpleCube() {

		float vertices[] = {
				// �O
				-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
				0.5f,
				0.5f,
				0.5f,
				0.5f,
				0.5f,

				// ��
				-0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
				-0.5f,
				0.5f,
				0.5f,
				-0.5f,

				// ��
				-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
				-0.5f,
				0.5f,
				-0.5f,

				// �E
				0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
				0.5f,
				-0.5f,

				// ��
				-0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f,
				0.5f, -0.5f,

				// ��
				-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f };

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

	}

	public void draw(GL10 gl) {

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

		// Front
		gl.glNormal3f(0, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		// Back
		gl.glNormal3f(0, 0, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

		// Left
		gl.glNormal3f(-1.0f, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);

		// Right
		gl.glNormal3f(1.0f, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

		// Top
		gl.glNormal3f(0, 1.0f, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);

		// Right
		gl.glNormal3f(0, -1.0f, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

	}
}
