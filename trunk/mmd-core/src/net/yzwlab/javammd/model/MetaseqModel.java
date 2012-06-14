package net.yzwlab.javammd.model;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.GLTexture;
import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IGL.FrontFace;
import net.yzwlab.javammd.IGLObject;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.IGLTextureProvider.Handler;
import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * GLMetaseq��Java�ł̎����ł��B
 * �I���W�i����GLMetaseq.c�̃��C�Z���X��mmd-core/GLMetaseq-LICENSE.txt�ɋL�ڂ��܂��B
 */
public class MetaseqModel implements IGLObject {

	private static int indexOf(int offset, byte[] data, byte[] subset) {
		if (data == null || subset == null) {
			throw new IllegalArgumentException();
		}
		for (int i = offset; i < data.length - subset.length + 1; i++) {
			if (startsWith(i, data, subset)) {
				return i;
			}
		}
		return -1;
	}

	private static int indexOf(byte[] data, byte[] subset) {
		if (data == null || subset == null) {
			throw new IllegalArgumentException();
		}
		return indexOf(0, data, subset);
	}

	private static boolean startsWith(int offset, byte[] data, byte[] prefix) {
		if (data == null || prefix == null) {
			throw new IllegalArgumentException();
		}
		if (data.length < offset + prefix.length) {
			return false;
		}
		for (int i = 0; i < prefix.length; i++) {
			if (data[offset + i] != prefix[i]) {
				return false;
			}
		}
		return true;
	}

	private static byte[] subbytes(byte[] data, int startIndex, int endIndex) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		int len = endIndex - startIndex;
		if (len < 0) {
			throw new IllegalArgumentException();
		}
		byte[] r = new byte[len];
		for (int i = startIndex; i < endIndex; i++) {
			r[i - startIndex] = data[i];
		}
		return r;
	}

	private static byte[] addTerminater(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		byte[] r = new byte[data.length + 1];
		for (int i = 0; i < data.length; i++) {
			r[i] = data[i];
		}
		r[data.length] = 0;
		return r;
	}

	private List<MQO_OBJECT> objects;

	public MetaseqModel() {
		this.objects = new ArrayList<MQO_OBJECT>();
	}

	@Override
	public void update(float frameNo) {
		;
	}

	@Override
	public void prepare(IGLTextureProvider pTextureProvider, Handler handler)
			throws ReadException {
		if (pTextureProvider == null || handler == null) {
			throw new IllegalArgumentException();
		}
		for (MQO_OBJECT o : objects) {
			o.prepare(pTextureProvider, handler);
		}
	}

	@Override
	public void draw(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		if (objects.size() == 0) {
			return;
		}

		gl.glPushMatrix();
		// ���^�Z�R�͒��_�̕��т��\�ʂ���݂ĉE���
		IGL.FrontFace intFrontFace = gl.glGetFrontFace();
		gl.glFrontFace(FrontFace.GL_CW);

		for (MQO_OBJECT o : objects) {
			o.draw(gl);
		}
		// ���^�Z�R�͒��_�̕��т��\�ʂ���݂ĉE���i���̐ݒ�ɂ��ǂ��j
		gl.glFrontFace(intFrontFace);
		gl.glPopMatrix();
	}

	public void load(IReadBuffer buffer, double scale, int alpha)
			throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		ArrayList<MQO_OBJDATA> obj = new ArrayList<MQO_OBJDATA>();
		List<MQO_MATDATA> M = new ArrayList<MQO_MATDATA>();

		while (!buffer.isEOF()) {
			byte[] buf = buffer.readLine();

			// Material
			if (indexOf(buf, new byte[] { 'M', 'a', 't', 'e', 'r', 'i', 'a',
					'l' }) >= 0) {
				int endPos = indexOf(buf, new byte[] { '{' });
				mqoReadMaterial(buffer, M, Integer.parseInt(new String(
						subbytes(buf, 9, endPos)).trim()));
			}

			// Object
			if (indexOf(buf, new byte[] { 'O', 'b', 'j', 'e', 'c', 't', ' ' }) >= 0) {
				MQO_OBJDATA o = new MQO_OBJDATA(subbytes(buf, 7, buf.length));
				mqoReadObject(buffer, o);
				obj.add(o);
			}
		}

		for (MQO_OBJDATA data : obj) {
			MQO_OBJECT o = new MQO_OBJECT(data, M, scale, alpha);
			o.mqoMakePolygon(data, data.mqoVertexNormal(), M, scale, alpha);
			objects.add(o);
		}
	}

	private void mqoReadObject(IReadBuffer buffer, MQO_OBJDATA obj)
			throws ReadException {
		byte buf[] = null;

		while ((buf = buffer.readLine()) != null) {
			if (indexOf(buf, new byte[] { '}' }) >= 0)
				break;

			// visible
			int pos = -1;
			if ((pos = indexOf(buf, new byte[] { 'v', 'i', 's', 'i', 'b', 'l',
					'e', ' ' })) >= 0) {
				obj.visible = Integer.parseInt(new String(subbytes(buf,
						pos + 8, buf.length)).trim());
			}

			// shading
			if ((pos = indexOf(buf, new byte[] { 's', 'h', 'a', 'd', 'i', 'n',
					'g', ' ' })) >= 0) {
				// TODO
				// obj.shading = Integer.parseInt(new String(subbytes(buf,
				// pos + 8, buf.length)).trim());
			}

			// facet
			if ((pos = indexOf(buf, new byte[] { 'f', 'a', 'c', 'e', 't', ' ' })) >= 0) {
				obj.facet = Float.parseFloat(new String(subbytes(buf, pos + 6,
						buf.length)).trim());
			}

			// vertex
			if ((pos = indexOf(buf, new byte[] { 'v', 'e', 'r', 't', 'e', 'x',
					' ' })) >= 0) {
				int endPos = indexOf(pos, buf, new byte[] { '{' });
				int num = Integer.parseInt(new String(subbytes(buf, pos + 7,
						endPos)).trim());
				obj.mqoReadVertex(buffer, num);
			}
			// BVertex
			if ((pos = indexOf(buf, new byte[] { 'B', 'V', 'e', 'r', 't', 'e',
					'x', ' ' })) >= 0) {
				int endPos = indexOf(pos, buf, new byte[] { '{' });
				int num = Integer.parseInt(new String(subbytes(buf, pos + 8,
						endPos)).trim());
				obj.mqoReadBVertex(buffer, num);
			}

			// face
			if ((pos = indexOf(buf, new byte[] { 'f', 'a', 'c', 'e', ' ' })) >= 0) {
				int endPos = indexOf(pos, buf, new byte[] { '{' });
				int num = Integer.parseInt(new String(subbytes(buf, pos + 5,
						endPos)).trim());
				obj.mqoReadFace(buffer, num);
			}

		}

	}

	private void mqoReadMaterial(IReadBuffer buffer, List<MQO_MATDATA> MList,
			int num) throws ReadException {
		byte[] buf;
		while ((buf = buffer.readLine()) != null) {
			if (indexOf(buf, new byte[] { '}' }) >= 0 && MList.size() >= num) {
				break;
			}
			MQO_MATDATA m = new MQO_MATDATA();
			m.parse(buf);
			MList.add(m);
		}

	}

	private glPOINT3f mqoSnormal(glPOINT3f A, glPOINT3f B, glPOINT3f C) {
		double norm;
		glPOINT3f vec0 = new glPOINT3f(0.0f, 0.0f, 0.0f), vec1 = new glPOINT3f(
				0.0f, 0.0f, 0.0f);

		// �x�N�g��BA
		vec0.x = A.x - B.x;
		vec0.y = A.y - B.y;
		vec0.z = A.z - B.z;

		// �x�N�g��BC
		vec1.x = C.x - B.x;
		vec1.y = C.y - B.y;
		vec1.z = C.z - B.z;

		// �@���x�N�g��
		float x = vec0.y * vec1.z - vec0.z * vec1.y;
		float y = vec0.z * vec1.x - vec0.x * vec1.z;
		float z = vec0.x * vec1.y - vec0.y * vec1.x;
		glPOINT3f normal = new glPOINT3f(x, y, z);

		// ���K��
		norm = normal.x * normal.x + normal.y * normal.y + normal.z * normal.z;
		norm = Math.sqrt(norm);

		normal.x /= norm;
		normal.y /= norm;
		normal.z /= norm;
		return normal;
	}

	private class glCOLOR4f {
		float r;
		float g;
		float b;
		float a;

		public glCOLOR4f(float r, float g, float b, float a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
	}

	private class glPOINT3f {
		float x;

		float y;

		float z;

		public glPOINT3f(float x, float y, float z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}

	}

	private class glPOINT2f {
		final float x;

		final float y;

		public glPOINT2f(float x, float y) {
			this.x = x;
			this.y = y;
		}

	}

	private class MQO_FACE {
		int n; // 1�̖ʂ��\�����钸�_�̐��i3�`4�j
		int m; // �ʂ̍ގ��ԍ�
		int[] v; // ���_�ԍ����i�[�����z��
		glPOINT2f[] uv; // UV�}�b�v

		private void parse(String buf) throws ReadException {
			if (buf == null) {
				throw new IllegalArgumentException();
			}
			// �ʂ��\�����钸�_��
			buf = buf.trim();
			int pos = buf.indexOf(" ");
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			n = Integer.parseInt(buf.substring(0, pos).trim());
			buf = buf.substring(pos + 1);

			// ���_(V)�̓ǂݍ���
			if ((pos = buf.indexOf("V(")) >= 0) {
				int endPos = buf.indexOf(")", pos);
				String[] values = buf.substring(pos + 2, endPos).trim()
						.split(" ");
				v = new int[n];
				for (int i = 0; i < n; i++) {
					v[i] = Integer.parseInt(values[i]);
				}
			}

			// �}�e���A��(M)�̓ǂݍ���
			m = 0;
			if ((pos = buf.indexOf("M(")) >= 0) {
				int endPos = buf.indexOf(")", pos);
				m = Integer.parseInt(buf.substring(pos + 2, endPos).trim());
			} else { // �}�e���A�����ݒ肳��Ă��Ȃ���
				m = -1;
			}

			// UV�}�b�v(UV)�̓ǂݍ���
			if ((pos = buf.indexOf("UV(")) >= 0) {
				int endPos = buf.indexOf(")", pos);
				String[] values = buf.substring(pos + 3, endPos).trim()
						.split(" ");
				uv = new glPOINT2f[n];
				for (int i = 0; i < n; i++) {
					uv[i] = new glPOINT2f(Float.parseFloat(values[i * 2 + 0]),
							Float.parseFloat(values[i * 2 + 1]));
				}
			}
		}
	}

	private class VERTEX_TEXUSE {
		float point[/* 3 */]; // ���_�z�� (x, y, z)
		float normal[/* 3 */]; // �@���z�� (x, y, z)
		float uv[/* 2 */]; // UV�z�� (u, v)

		public VERTEX_TEXUSE() {
			this.point = new float[3];
			this.normal = new float[3];
			this.uv = new float[2];
		}
	};

	private class VERTEX_NOTEX {
		float point[/* 3 */]; // ���_�z�� (x, y, z)
		float normal[/* 3 */]; // �@���z�� (x, y, z)

		public VERTEX_NOTEX() {
			this.point = new float[3];
			this.normal = new float[3];
		}
	};

	/*
	 * =========================================================================
	 * �y�^��`�z �}�e���A�����i�}�e���A���ʂɒ��_�z������j
	 * =========================================================================
	 */
	private class MQO_MATERIAL {
		boolean isValidMaterialInfo;// �}�e���A�����̗L��/����
		private boolean useTexture; // �e�N�X�`���̗L���FUSE_TEXTURE / NOUSE_TEXTURE
		private Long textureId; // �e�N�X�`���̖��O(OpenGL)
		float color[/* 4 */]; // �F�z�� (r, g, b, a)
		float dif[/* 4 */]; // �g�U��
		float amb[/* 4 */]; // ���͌�
		float emi[/* 4 */]; // ���ȏƖ�
		float spc[/* 4 */]; // ���ˌ�
		float power; // ���ˌ��̋���
		VERTEX_NOTEX[] vertex_p; // �|���S���݂̂̎��̒��_�z��
		VERTEX_TEXUSE[] vertex_t; // �e�N�X�`���g�p���̒��_�z��

		private byte[] texFile; // �e�N�X�`���t�@�C��

		// TODO
		// private byte[] alpFile; // �A���t�@�e�N�X�`���t�@�C��

		public MQO_MATERIAL() {
			this.isValidMaterialInfo = false;
			this.useTexture = false;
			this.textureId = null;
			this.vertex_p = null;
			this.vertex_t = null;
			this.texFile = null;
			// TODO
			// this.alpFile = null;
		}

		public void prepare(IGLTextureProvider pTextureProvider,
				final Handler handler) throws ReadException {
			if (pTextureProvider == null || handler == null) {
				throw new IllegalArgumentException();
			}
			if (useTexture == false) {
				return;
			}
			pTextureProvider.load(texFile, new Handler() {

				@Override
				public void onSuccess(byte[] filename, GLTexture desc) {
					textureId = desc.getTextureId();
					handler.onSuccess(filename, desc);
				}

				@Override
				public void onError(byte[] filename, Throwable error) {
					handler.onError(filename, error);
				}
			});
		}

		public void copyFrom(MQO_MATDATA data) {
			if (data == null) {
				throw new IllegalArgumentException();
			}
			dif = data.dif;
			amb = data.amb;
			spc = data.spc;
			emi = data.emi;
			power = data.power;
			useTexture = data.useTex;
			texFile = data.texFile;
			// TODO
			// alpFile = data.alpFile;
		}

		public boolean isEmpty() {
			if (vertex_p != null) {
				if (vertex_p.length > 0) {
					return false;
				}
			}
			if (vertex_t != null) {
				if (vertex_t.length > 0) {
					return false;
				}
			}
			return true;
		}

		public boolean isUseTexture() {
			if (useTexture == false) {
				return false;
			}
			if (textureId == null) {
				return false;
			}
			return true;
		}

		public void drawVertexT(IGL gl) {
			if (vertex_t == null) {
				return;
			}
			gl.glBegin(IGL.C.GL_TRIANGLES, vertex_t.length);
			for (int i = 0; i < vertex_t.length; i++) {
				// �e�N�X�`�����W�z���ݒ�
				float[] uv = vertex_t[i].uv;
				gl.glTexCoord2f(uv[0], uv[1]);

				// ���_�z���ݒ�
				float[] v = vertex_t[i].point;
				gl.glVertex3f(v[0], v[1], v[2]);

				// �@���z���ݒ�
				v = vertex_t[i].normal;
				gl.glNormal3f(v[0], v[1], v[2]);

				// �F�ݒ�
				gl.glColor4f(color[0], color[1], color[2], color[3]);
			}
			gl.glEnd();
		}

		public void drawVertexP(IGL gl) {
			if (vertex_p == null) {
				return;
			}
			gl.glBegin(IGL.C.GL_TRIANGLES, vertex_p.length);
			for (int i = 0; i < vertex_p.length; i++) {
				// ���_�z���ݒ�
				float[] v = vertex_p[i].point;
				gl.glVertex3f(v[0], v[1], v[2]);

				// �@���z���ݒ�
				v = vertex_p[i].normal;
				gl.glNormal3f(v[0], v[1], v[2]);

				// �F�ݒ�
				gl.glColor4f(color[0], color[1], color[2], color[3]);
			}
			gl.glEnd();
		}

		private void mqoMakeArray(int matpos, MQO_FACE F[], int fnum,
				glPOINT3f V[], glPOINT3f N[], double facet, glCOLOR4f mcol,
				double scale, int alpha) {
			int f;
			int i;
			int dpos;
			double s;
			glPOINT3f normal; // �@���x�N�g��

			dpos = 0;
			color = new float[] { mcol.r, mcol.g, mcol.b, mcol.a };
			if (useTexture) {
				for (f = 0; f < fnum; f++) {
					if (F[f].m != matpos)
						continue;
					if (F[f].n == 3) {
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
								V[F[f].v[2]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 3; i++) {
							vertex_t[dpos] = new VERTEX_TEXUSE();
							vertex_t[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_t[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_t[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_t[dpos].uv[0] = F[f].uv[i].x;
							vertex_t[dpos].uv[1] = F[f].uv[i].y;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								// �X���[�W���O�p�@���i���_�@���Ɩʖ@���̊p�x�j�̂Ƃ��͖ʖ@���𒸓_�@���Ƃ���
								vertex_t[dpos].normal[0] = normal.x;
								vertex_t[dpos].normal[1] = normal.y;
								vertex_t[dpos].normal[2] = normal.z;
							} else {
								vertex_t[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_t[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_t[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
					}
					// �S���_�i�l�p�j�͂R���_�i�O�p�j���Q�ɕ���
					if (F[f].n == 4) {
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
								V[F[f].v[2]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 4; i++) {
							if (i == 3)
								continue;
							vertex_t[dpos] = new VERTEX_TEXUSE();
							vertex_t[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_t[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_t[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_t[dpos].uv[0] = F[f].uv[i].x;
							vertex_t[dpos].uv[1] = F[f].uv[i].y;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								vertex_t[dpos].normal[0] = normal.x;
								vertex_t[dpos].normal[1] = normal.y;
								vertex_t[dpos].normal[2] = normal.z;
							} else {
								vertex_t[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_t[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_t[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[2]],
								V[F[f].v[3]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 4; i++) {
							if (i == 1)
								continue;
							vertex_t[dpos] = new VERTEX_TEXUSE();
							vertex_t[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_t[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_t[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_t[dpos].uv[0] = F[f].uv[i].x;
							vertex_t[dpos].uv[1] = F[f].uv[i].y;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								vertex_t[dpos].normal[0] = normal.x;
								vertex_t[dpos].normal[1] = normal.y;
								vertex_t[dpos].normal[2] = normal.z;
							} else {
								vertex_t[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_t[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_t[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
					}
				}
			} else {
				if (alpha != 255) {
					color[3] = (float) ((double) alpha / (double) 255);
				}
				for (f = 0; f < fnum; f++) {
					if (F[f].m != matpos)
						continue;
					if (F[f].n == 3) {
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
								V[F[f].v[2]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 3; i++) {
							vertex_p[dpos] = new VERTEX_NOTEX();
							vertex_p[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_p[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_p[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_p[dpos].normal[0] = normal.x;
							vertex_p[dpos].normal[1] = normal.y;
							vertex_p[dpos].normal[2] = normal.z;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								vertex_p[dpos].normal[0] = normal.x;
								vertex_p[dpos].normal[1] = normal.y;
								vertex_p[dpos].normal[2] = normal.z;
							} else {
								vertex_p[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_p[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_p[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
					}
					// �S���_�i�l�p�j�͂R���_�i�O�p�j���Q�ɕ���
					if (F[f].n == 4) {
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
								V[F[f].v[2]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 4; i++) {
							if (i == 3)
								continue;
							vertex_p[dpos] = new VERTEX_NOTEX();
							vertex_p[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_p[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_p[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_p[dpos].normal[0] = normal.x;
							vertex_p[dpos].normal[1] = normal.y;
							vertex_p[dpos].normal[2] = normal.z;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								vertex_p[dpos].normal[0] = normal.x;
								vertex_p[dpos].normal[1] = normal.y;
								vertex_p[dpos].normal[2] = normal.z;
							} else {
								vertex_p[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_p[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_p[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
						normal = mqoSnormal(V[F[f].v[0]], V[F[f].v[2]],
								V[F[f].v[3]]); // �@���x�N�g�����v�Z
						for (i = 0; i < 4; i++) {
							if (i == 1)
								continue;
							vertex_p[dpos] = new VERTEX_NOTEX();
							vertex_p[dpos].point[0] = (float) (V[F[f].v[i]].x * scale);
							vertex_p[dpos].point[1] = (float) (V[F[f].v[i]].y * scale);
							vertex_p[dpos].point[2] = (float) (V[F[f].v[i]].z * scale);
							vertex_p[dpos].normal[0] = normal.x;
							vertex_p[dpos].normal[1] = normal.y;
							vertex_p[dpos].normal[2] = normal.z;
							s = Math.acos(normal.x * N[F[f].v[i]].x + normal.y
									* N[F[f].v[i]].y + normal.z
									* N[F[f].v[i]].z);
							if (facet < s) {
								vertex_p[dpos].normal[0] = normal.x;
								vertex_p[dpos].normal[1] = normal.y;
								vertex_p[dpos].normal[2] = normal.z;
							} else {
								vertex_p[dpos].normal[0] = N[F[f].v[i]].x;
								vertex_p[dpos].normal[1] = N[F[f].v[i]].y;
								vertex_p[dpos].normal[2] = N[F[f].v[i]].z;
							}
							dpos++;
						}
					}
				}
			}
		}
	};

	private class MQO_OBJECT {
		int alpha; // ���_�z��쐬���Ɏw�肳�ꂽ�A���t�@�l�i�Q�Ɨp�j
		List<MQO_INNER_OBJECT> obj; // �����I�u�W�F�N�g�z��

		public MQO_OBJECT(MQO_OBJDATA data, List<MQO_MATDATA> materials,
				double scale, int alpha) {
			this.alpha = alpha;
			this.obj = new ArrayList<MQO_INNER_OBJECT>();
		}

		public void prepare(IGLTextureProvider pTextureProvider, Handler handler)
				throws ReadException {
			if (pTextureProvider == null || handler == null) {
				throw new IllegalArgumentException();
			}
			for (MQO_INNER_OBJECT o : obj) {
				for (int m = 0; m < o.matnum; m++) { // �}�e���A�����[�v
					MQO_MATERIAL mat = o.mat[m];
					mat.prepare(pTextureProvider, handler);
				}
			}
		}

		public void draw(IGL gl) {
			MQO_MATERIAL mat;
			long bindGL_TEXTURE_2D = 0;
			boolean isGL_TEXTURE_2D = false;
			boolean isGL_BLEND = false;

			double dalpha = (double) alpha / (double) 255;
			for (MQO_INNER_OBJECT obj : this.obj) { // �����I�u�W�F�N�g���[�v
				if (!obj.isVisible)
					continue;
				// TODO
				// glShadeModel(((obj.isShadingFlat))?GL_FLAT:GL_SMOOTH);

				for (int m = 0; m < obj.matnum; m++) { // �}�e���A�����[�v
					mat = obj.mat[m];
					if (mat.isEmpty()) {
						continue;
					}

					if (mat.isValidMaterialInfo) { // �}�e���A���̏��ݒ�
						float[] src = mat.dif;
						float[] matenv = new float[] { src[0], src[1], src[2],
								src[3] };
						matenv[3] *= dalpha;
						gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK,
								IGL.C.GL_DIFFUSE, matenv);
						src = mat.amb;
						matenv = new float[] { src[0], src[1], src[2], src[3] };
						matenv[3] *= dalpha;
						gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK,
								IGL.C.GL_AMBIENT, matenv);
						src = mat.spc;
						matenv = new float[] { src[0], src[1], src[2], src[3] };
						matenv[3] *= dalpha;
						gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK,
								IGL.C.GL_SPECULAR, matenv);
						src = mat.emi;
						matenv = new float[] { src[0], src[1], src[2], src[3] };
						matenv[3] *= dalpha;
						gl.glMaterialfv(IGL.C.GL_FRONT_AND_BACK,
								IGL.C.GL_EMISSION, matenv);
						gl.glMaterialf(IGL.C.GL_FRONT_AND_BACK,
								IGL.C.GL_SHININESS, mat.power);
					}

					if (mat.isUseTexture()) { // �e�N�X�`��������ꍇ
						gl.glEnableClientState(IGL.C.GL_VERTEX_ARRAY);
						gl.glEnableClientState(IGL.C.GL_NORMAL_ARRAY);
						gl.glEnableClientState(IGL.C.GL_TEXTURE_COORD_ARRAY);

						isGL_TEXTURE_2D = gl.glIsEnabled(IGL.C.GL_TEXTURE_2D);
						isGL_BLEND = gl.glIsEnabled(IGL.C.GL_BLEND);
						bindGL_TEXTURE_2D = gl
								.glGetBindTexture(IGL.C.GL_TEXTURE_2D);
						// glGetIntegerv(GL_BLEND_SRC_ALPHA,&blendGL_SRC_ALPHA);

						gl.glEnable(IGL.C.GL_TEXTURE_2D);
						gl.glEnable(IGL.C.GL_BLEND);
						gl.glBlendFunc(IGL.C.GL_SRC_ALPHA,
								IGL.C.GL_ONE_MINUS_SRC_ALPHA);

						gl.glBindTexture(IGL.C.GL_TEXTURE_2D, mat.textureId);

						mat.drawVertexT(gl);

						gl.glBindTexture(IGL.C.GL_TEXTURE_2D, bindGL_TEXTURE_2D);
						if (isGL_BLEND == false)
							gl.glDisable(IGL.C.GL_BLEND);
						if (isGL_TEXTURE_2D == false)
							gl.glDisable(IGL.C.GL_TEXTURE_2D);

						gl.glDisableClientState(IGL.C.GL_VERTEX_ARRAY);
						gl.glDisableClientState(IGL.C.GL_NORMAL_ARRAY);
						gl.glDisableClientState(IGL.C.GL_TEXTURE_COORD_ARRAY);
					} else { // �e�N�X�`�����Ȃ��ꍇ

						gl.glEnableClientState(IGL.C.GL_VERTEX_ARRAY);
						gl.glEnableClientState(IGL.C.GL_NORMAL_ARRAY);
						// glEnableClientState( GL_COLOR_ARRAY );

						isGL_BLEND = gl.glIsEnabled(IGL.C.GL_BLEND);
						gl.glEnable(IGL.C.GL_BLEND);
						gl.glBlendFunc(IGL.C.GL_SRC_ALPHA,
								IGL.C.GL_ONE_MINUS_SRC_ALPHA);

						mat.drawVertexP(gl);

						if (isGL_BLEND == false)
							gl.glDisable(IGL.C.GL_BLEND);

						// glDisableClientState( GL_COLOR_ARRAY );
						gl.glDisableClientState(IGL.C.GL_VERTEX_ARRAY);
						gl.glDisableClientState(IGL.C.GL_NORMAL_ARRAY);

					}
				}
			}
		}

		private void mqoMakePolygon(MQO_OBJDATA readObj, glPOINT3f N[],
				List<MQO_MATDATA> M, double scale, int alpha) {
			MQO_INNER_OBJECT setObj = new MQO_INNER_OBJECT(readObj.objname);
			MQO_MATERIAL material;
			glCOLOR4f defcol = new glCOLOR4f(0.0f, 0.0f, 0.0f, 0.0f);
			glCOLOR4f pcol;
			int f, m;
			int[] mat_vnum;
			MQO_FACE[] F;
			glPOINT3f[] V;
			double facet;

			obj.add(setObj);
			setObj.isVisible = (readObj.visible > 0);
			// TODO
			// setObj.isShadingFlat = (readObj.shading == 0);
			F = readObj.F;
			V = readObj.V;
			facet = readObj.facet;

			// face�̒��ł̃}�e���A�����̒��_�̐�
			// M=NULL�̂Ƃ��AF[].m = 0 �������Ă���
			int n_mat = 0;
			if (M == null) {
				n_mat = 1;
			} else {
				n_mat = M.size();
			}

			mat_vnum = new int[n_mat];

			for (f = 0; f < F.length; f++) {
				if (F[f].m < 0)
					continue; // �}�e���A�����ݒ肳��Ă��Ȃ���
				if (F[f].n == 3) {
					mat_vnum[F[f].m] += 3;
				}
				if (F[f].n == 4) {
					// �S���_�i�l�p�j�͂R���_�i�O�p�j���Q�ɕ���
					// 0 3 0 0 3
					// �� ���@���@�@��
					// 1 2 1 2 2
					// �S���_�̕��ʃf�[�^��
					// �R���_�̕��ʃf�[�^���Q��
					mat_vnum[F[f].m] += 3 * 2;
				}
				if (setObj.matnum < F[f].m + 1)
					setObj.matnum = F[f].m + 1;
			}

			// �}�e���A���ʂɒ��_�z����쐬����
			setObj.mat = new MQO_MATERIAL[setObj.matnum];

			for (m = 0; m < setObj.matnum; m++) {
				material = new MQO_MATERIAL();
				setObj.mat[m] = material;
				int datanum = mat_vnum[m];
				material.isValidMaterialInfo = (M != null);

				if (mat_vnum[m] <= 0) {
					continue;
				}
				if (material.isValidMaterialInfo) {
					MQO_MATDATA data = M.get(m);
					material.copyFrom(data);
					pcol = data.col;
				} else {
					defcol.r = 1.0f;
					defcol.g = 1.0f;
					defcol.b = 1.0f;
					defcol.a = 1.0f;
					material.useTexture = false;
					pcol = defcol;
				}
				if (material.useTexture) {
					material.vertex_t = new VERTEX_TEXUSE[datanum];
					// TODO
					// material.texture_id = M[m].texName;
				} else {
					material.vertex_p = new VERTEX_NOTEX[datanum];
				}
				material.mqoMakeArray(m, F, F.length, V, N, facet, pcol, scale,
						alpha);
			}
		}

		private class MQO_INNER_OBJECT {
			boolean isVisible; // 0�F��\���@���̑��F�\��
			// TODO
			// boolean isShadingFlat; // �V�F�[�f�B���O���[�h
			int matnum; // �g�p�}�e���A����
			MQO_MATERIAL[] mat; // �}�e���A���z��

			public MQO_INNER_OBJECT(byte[] objname) {
				if (objname == null) {
					throw new IllegalArgumentException();
				}
			}
		};

	}

	private class MQO_OBJDATA {
		byte[] objname; // �p�[�c��
		int visible; // �����
		// TODO
		// int shading; // �V�F�[�f�B���O�i0:�t���b�g�^1:�O���[�j
		float facet; // �X���[�W���O�p
		MQO_FACE[] F; // ��
		glPOINT3f[] V; // ���_

		public MQO_OBJDATA(byte[] objname) {
			if (objname == null) {
				throw new IllegalArgumentException();
			}
			this.objname = objname;
			this.visible = 0;
			// TODO
			// this.shading = 0;
			this.facet = 0.0f;
			this.F = new MQO_FACE[0];
			this.V = new glPOINT3f[0];
		}

		private void mqoReadVertex(IReadBuffer buffer, int num)
				throws ReadException {
			byte buf[] = null;
			V = new glPOINT3f[num];
			int i = 0;

			while ((buf = buffer.readLine()) != null) {
				if (indexOf(buf, new byte[] { '}' }) >= 0)
					break;
				String[] values = new String(buf).trim().split(" ");
				V[i] = new glPOINT3f(Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]));
				i++;
			}
		}

		private void mqoReadBVertex(IReadBuffer buffer, int num)
				throws ReadException {
			V = new glPOINT3f[num];
			int n_vertex, i;
			byte[] cw;
			int pos = 0;

			cw = buffer.readLine();
			if ((pos = indexOf(cw, new byte[] { 'V', 'e', 'c', 't', 'o', 'r' })) >= 0) {
				int bracketBegin = indexOf(pos, cw, new byte[] { '[' });
				int bracketEnd = indexOf(pos, cw, new byte[] { ']' });
				if (bracketBegin < 0 || bracketEnd < 0) {
					throw new ReadException("Unexpected Line");
				}
				n_vertex = Integer.parseInt(new String(subbytes(cw, pos + 7,
						bracketBegin)).trim());
				Integer.parseInt(new String(subbytes(cw, bracketBegin + 1,
						bracketEnd - 1)).trim());
			} else {
				throw new ReadException("Unexpected Line");
			}
			// MQO�t�@�C���̃o�C�i�����_�f�[�^��intel�`���i���g���G�f�B�A���j
			for (i = 0; i < n_vertex; i++) {
				V[i] = new glPOINT3f(buffer.readFloat(), buffer.readFloat(),
						buffer.readFloat());
			}

			// "}"�܂œǂݔ�΂�
			byte[] buf = null;
			while ((buf = buffer.readLine()) != null) {
				if (indexOf(buf, new byte[] { '}' }) >= 0)
					break;
			}
		}

		private void mqoReadFace(IReadBuffer buffer, int num)
				throws ReadException {
			F = new MQO_FACE[num];
			byte buf[] = null;
			int i = 0;

			while ((buf = buffer.readLine()) != null) {
				if (indexOf(buf, new byte[] { '}' }) >= 0)
					break;

				F[i] = new MQO_FACE();
				F[i].parse(new String(buf));
				i++;
			}

		}

		private glPOINT3f[] mqoVertexNormal() {
			int f;
			int v;
			double len;
			glPOINT3f fnormal; // �ʖ@���x�N�g��
			glPOINT3f[] ret = new glPOINT3f[V.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = new glPOINT3f(0.0f, 0.0f, 0.0f);
			}

			// �ʂ̖@���𒸓_�ɑ�������
			for (f = 0; f < F.length; f++) {
				if (F[f].n == 3) {
					fnormal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
							V[F[f].v[2]]);
					for (int i = 0; i < 3; i++) {
						ret[F[f].v[i]].x += fnormal.x;
						ret[F[f].v[i]].y += fnormal.y;
						ret[F[f].v[i]].z += fnormal.z;
					}
				}
				if (F[f].n == 4) {
					fnormal = mqoSnormal(V[F[f].v[0]], V[F[f].v[1]],
							V[F[f].v[2]]);
					for (int i = 0; i < 4; i++) {
						if (i == 3)
							continue;
						ret[F[f].v[i]].x += fnormal.x;
						ret[F[f].v[i]].y += fnormal.y;
						ret[F[f].v[i]].z += fnormal.z;
					}
					fnormal = mqoSnormal(V[F[f].v[0]], V[F[f].v[2]],
							V[F[f].v[3]]);
					for (int i = 0; i < 4; i++) {
						if (i == 1)
							continue;
						ret[F[f].v[i]].x += fnormal.x;
						ret[F[f].v[i]].y += fnormal.y;
						ret[F[f].v[i]].z += fnormal.z;
					}
				}
			}
			// ���K��
			for (v = 0; v < V.length; v++) {
				if (ret[v].x == 0 && ret[v].y == 0 && ret[v].z == 0) {
					// �ʂɎg���ĂȂ��_
					continue;
				}
				len = Math.sqrt(ret[v].x * ret[v].x + ret[v].y * ret[v].y
						+ ret[v].z * ret[v].z);
				if (len != 0) {
					ret[v].x = (float) (ret[v].x / len);
					ret[v].y = (float) (ret[v].y / len);
					ret[v].z = (float) (ret[v].z / len);
				}
			}

			return ret;
		}
	}

	private class MQO_MATDATA {
		glCOLOR4f col; // �F
		float[] dif; // �g�U��
		float[] amb; // ���͌�
		float[] emi; // ���ȏƖ�
		float[] spc; // ���ˌ�
		float power; // ���ˌ��̋���
		boolean useTex; // �e�N�X�`���̗L��
		byte[] texFile; // �e�N�X�`���t�@�C��
		byte[] alpFile; // �A���t�@�e�N�X�`���t�@�C��

		private void parse(byte[] line) throws ReadException {
			if (line == null) {
				throw new IllegalArgumentException();
			}

			int pos = indexOf(line, new byte[] { 'c', 'o', 'l', '(' }); // �ގ����ǂݔ�΂�
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			int endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			byte[] values = subbytes(line, pos + 4, endPos);
			String[] svalues = new String(values).split(" ");
			// ���_�J���[
			col = new glCOLOR4f(Float.parseFloat(svalues[0]),
					Float.parseFloat(svalues[1]), Float.parseFloat(svalues[2]),
					Float.parseFloat(svalues[3]));

			// dif
			pos = indexOf(endPos, line, new byte[] { '(' });
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			float difVal = Float.parseFloat(new String(subbytes(line, pos + 1,
					endPos)));

			// amb
			pos = indexOf(endPos, line, new byte[] { '(' });
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			float ambVal = Float.parseFloat(new String(subbytes(line, pos + 1,
					endPos)));

			// emi
			pos = indexOf(endPos, line, new byte[] { '(' });
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			float emiVal = Float.parseFloat(new String(subbytes(line, pos + 1,
					endPos)));

			// spc
			pos = indexOf(endPos, line, new byte[] { '(' });
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			float spcVal = Float.parseFloat(new String(subbytes(line, pos + 1,
					endPos)));

			// power
			pos = indexOf(endPos, line, new byte[] { '(' });
			if (pos < 0) {
				throw new ReadException("Unexpected Line");
			}
			endPos = indexOf(pos, line, new byte[] { ')' });
			if (endPos < 0) {
				throw new ReadException("Unexpected Line");
			}
			power = Float
					.parseFloat(new String(subbytes(line, pos + 1, endPos)));

			// �g�U��
			dif = new float[4];
			dif[0] = difVal * col.r;
			dif[1] = difVal * col.g;
			dif[2] = difVal * col.b;
			dif[3] = col.a;

			// ���͌�
			amb = new float[4];
			amb[0] = ambVal * col.r;
			amb[1] = ambVal * col.g;
			amb[2] = ambVal * col.b;
			amb[3] = col.a;

			// ���ȏƖ�
			emi = new float[4];
			emi[0] = emiVal * col.r;
			emi[1] = emiVal * col.g;
			emi[2] = emiVal * col.b;
			emi[3] = col.a;

			// ���ˌ�
			spc = new float[4];
			spc[0] = spcVal * col.r;
			spc[1] = spcVal * col.g;
			spc[2] = spcVal * col.b;
			spc[3] = col.a;

			// tex�F�͗l�}�b�s���O��
			if ((pos = indexOf(line, new byte[] { 't', 'e', 'x', '(' })) >= 0) {
				useTex = true;

				endPos = indexOf(pos, line, new byte[] { ')' });
				texFile = subbytes(line, pos + 5, endPos - 1);
				texFile = addTerminater(texFile);
				if ((pos = indexOf(line, new byte[] { 'a', 'p', 'l', 'a', 'n',
						'e', '(' })) >= 0) {
					endPos = indexOf(pos, line, new byte[] { ')' });
					alpFile = subbytes(line, pos + 8, endPos - 1);
					alpFile = addTerminater(alpFile);
				} else {
					alpFile = null;
				}

			} else {
				useTex = false;
				texFile = null;
				alpFile = null;
			}
		}
	};

}
