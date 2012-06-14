package net.yzwlab.javammd.model;

import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IGLObject;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.IGLTextureProvider.Handler;
import net.yzwlab.javammd.ReadException;

/**
 * �����̂�`�悵�܂��B
 */
public class Cube implements IGLObject {

	/**
	 * �T�C�Y��ێ����܂��B
	 */
	private float extent;

	/**
	 * �F��ێ����܂��B
	 */
	private float r;

	/**
	 * �F��ێ����܂��B
	 */
	private float g;

	/**
	 * �F��ێ����܂��B
	 */
	private float b;

	/**
	 * �F��ێ����܂��B
	 */
	private float a;

	/**
	 * �ړ��ʒu��ێ����܂��B
	 */
	private float tx;

	/**
	 * �ړ��ʒu��ێ����܂��B
	 */
	private float ty;

	/**
	 * �ړ��ʒu��ێ����܂��B
	 */
	private float tz;

	/**
	 * �\�z���܂��B
	 */
	public Cube() {
		this.extent = 1.0f;
		this.r = 1.0f;
		this.g = 1.0f;
		this.b = 1.0f;
		this.a = 1.0f;
		this.tx = 0.0f;
		this.ty = 0.0f;
		this.tz = 0.0f;
	}

	public float getScale() {
		return extent;
	}

	public void setScale(float scale) {
		this.extent = scale;
	}

	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setTranslate(float tx, float ty, float tz) {
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
	}

	@Override
	public void prepare(IGLTextureProvider pTextureProvider, Handler handler)
			throws ReadException {
		if (pTextureProvider == null) {
			throw new IllegalArgumentException();
		}
		;
	}

	@Override
	public void draw(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		gl.glPushMatrix();
		gl.glColor4f(r, g, b, a);
		gl.glBegin(IGL.C.GL_QUADS, 24);
		gl.glNormal3f(1f, 0f, 0f);
		glVertex3f(gl, +extent, -extent, +extent);
		glVertex3f(gl, +extent, -extent, -extent);
		glVertex3f(gl, +extent, +extent, -extent);
		glVertex3f(gl, +extent, +extent, +extent);
		gl.glNormal3f(0f, 1f, 0f);
		glVertex3f(gl, +extent, +extent, +extent);
		glVertex3f(gl, +extent, +extent, -extent);
		glVertex3f(gl, -extent, +extent, -extent);
		glVertex3f(gl, -extent, +extent, +extent);
		gl.glNormal3f(0f, 0f, 1f);
		glVertex3f(gl, +extent, +extent, +extent);
		glVertex3f(gl, -extent, +extent, +extent);
		glVertex3f(gl, -extent, -extent, +extent);
		glVertex3f(gl, +extent, -extent, +extent);
		gl.glNormal3f(-1f, 0f, 0f);
		glVertex3f(gl, -extent, -extent, +extent);
		glVertex3f(gl, -extent, +extent, +extent);
		glVertex3f(gl, -extent, +extent, -extent);
		glVertex3f(gl, -extent, -extent, -extent);
		gl.glNormal3f(0f, -1f, 0f);
		glVertex3f(gl, -extent, -extent, +extent);
		glVertex3f(gl, -extent, -extent, -extent);
		glVertex3f(gl, +extent, -extent, -extent);
		glVertex3f(gl, +extent, -extent, +extent);
		gl.glNormal3f(0f, 0f, -1f);
		glVertex3f(gl, -extent, -extent, -extent);
		glVertex3f(gl, -extent, +extent, -extent);
		glVertex3f(gl, +extent, +extent, -extent);
		glVertex3f(gl, +extent, -extent, -extent);
		gl.glEnd();
		gl.glPopMatrix();
	}

	private void glVertex3f(IGL gl, float x, float y, float z) {
		gl.glVertex3f(x + tx, y + ty, z + tz);
	}
}
