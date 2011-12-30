package net.yzwlab.javammd.jogl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IMMDTextureProvider;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.format.TEXTURE_DESC;
import net.yzwlab.javammd.jogl.io.TargaReader;
import net.yzwlab.javammd.model.DataUtils;

public class JOGL implements IGL, IMMDTextureProvider {

	private File baseDir;

	private GL2 gl;

	public JOGL(File baseDir, GL2 gl) {
		if (baseDir == null || gl == null) {
			throw new IllegalArgumentException();
		}
		this.baseDir = baseDir;
		this.gl = gl;
	}

	@Override
	public void load(byte[] filename, IMMDTextureProvider.Handler handler)
			throws ReadException {
		if (filename == null || handler == null) {
			throw new IllegalArgumentException();
		}
		String sfilename = new String(DataUtils.getStringData(filename));
		int pos = sfilename.indexOf("*");
		if (pos > 0) {
			sfilename = sfilename.substring(0, pos);
		}
		File f = new File(baseDir, sfilename);
		try {
			TargaReader reader = new TargaReader();
			BufferedImage image = null;
			if (f.getName().endsWith(".tga")) {
				image = reader.read(f);
			} else {
				System.out.println(f.getPath());
				image = ImageIO.read(f);
				if (image == null) {
					throw new FileNotFoundException();
				}
			}
			int sizeWidth = 1;
			while (sizeWidth < image.getWidth()) {
				sizeWidth *= 2;
			}
			int sizeHeight = 1;
			while (sizeHeight < image.getHeight()) {
				sizeHeight *= 2;
			}

			BufferedImage textureImage = new BufferedImage(sizeWidth,
					sizeHeight, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = textureImage.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			textureImage.flush();

			TEXTURE_DESC ret = new TEXTURE_DESC();
			ret.setTexMemWidth(textureImage.getWidth());
			ret.setTexMemHeight(textureImage.getHeight());
			ret.setTexWidth(image.getWidth());
			ret.setTexHeight(image.getHeight());

			ByteBuffer imageBuffer = ByteBuffer.allocate(textureImage
					.getWidth() * textureImage.getHeight() * 4);
			int[] lineBuffer = new int[textureImage.getWidth()];
			for (int y = 0; y < textureImage.getHeight(); y++) {
				textureImage.getRGB(0, y, textureImage.getWidth(), 1,
						lineBuffer, 0, textureImage.getWidth());
				for (int i = 0; i < lineBuffer.length; i++) {
					int rgba = lineBuffer[i];
					imageBuffer.put(new byte[] {
							(byte) ((rgba & 0x00ff0000) >> 16),
							(byte) ((rgba & 0x0000ff00) >> 8),
							(byte) ((rgba & 0x000000ff) >> 0),
							(byte) ((rgba & 0xff000000) >> 24) });
				}
			}

			// GLubyte *image;
			gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 4);
			gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 4);
			IntBuffer intBuf = IntBuffer.allocate(32);
			gl.glGenTextures(1, intBuf);
			int textureId = intBuf.get(0);

			gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);

			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR);
			imageBuffer.position(0);
			gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8,
					ret.getTexMemWidth(), ret.getTexMemHeight(), 0,
					GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, imageBuffer);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0); // デフォルトテクスチャの割り当て
			ret.setTextureId(textureId);

			handler.onSuccess(filename, ret);
		} catch (IOException e) {
			throw new ReadException(e);
		}
	}

	@Override
	public C getGlFontFaceCode(int target) {
		// TODO Auto-generated method stub
		return C.GL_CW;
	}

	@Override
	public void glFrontFace(C mode) {
		int imode = 0;
		if (mode == C.GL_CW) {
			imode = GL2.GL_CW;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glFrontFace(imode);
	}

	@Override
	public void glBegin(C mode) {
		int imode = 0;
		if (mode == C.GL_TRIANGLES) {
			imode = GL.GL_TRIANGLES;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glBegin(imode);
	}

	@Override
	public void glEnd() {
		gl.glEnd();
	}

	@Override
	public void glVertex3f(float x, float y, float z) {
		gl.glVertex3f(x, y, z);
	}

	@Override
	public void glTexCoord2f(float x, float y) {
		gl.glTexCoord2f(x, y);
	}

	@Override
	public void glNormal3f(float x, float y, float z) {
		gl.glNormal3f(x, y, z);
	}

	@Override
	public void glBindTexture(C target, long texture) {
		int itarget = 0;
		if (target == C.GL_TEXTURE_2D) {
			itarget = GL2.GL_TEXTURE_2D;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glBindTexture(itarget, (int) texture);
	}

	@Override
	public void glBlendFunc(C c1, C c2) {
		int ic1 = 0;
		if (c1 == C.GL_SRC_ALPHA) {
			ic1 = GL2.GL_SRC_ALPHA;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_ONE_MINUS_SRC_ALPHA) {
			ic2 = GL2.GL_ONE_MINUS_SRC_ALPHA;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glBlendFunc(ic1, ic2);
	}

	@Override
	public void glPushMatrix() {
		gl.glPushMatrix();
	}

	@Override
	public void glPopMatrix() {
		gl.glPopMatrix();
	}

	@Override
	public void glScalef(float a1, float a2, float a3) {
		gl.glScalef(a1, a2, a3);
	}

	@Override
	public void glColor4f(float a1, float a2, float a3, float a4) {
		gl.glColor4f(a1, a2, a3, a4);
	}

	@Override
	public void glDrawArrays(C mode, int offset, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glEnable(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL2.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glEnable(itarget);
	}

	@Override
	public void glDisable(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL2.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glDisable(itarget);
	}

	@Override
	public boolean glIsEnabled(C target) {
		int itarget = 0;
		if (target == C.GL_NORMALIZE) {
			itarget = GL2.GL_NORMALIZE;
		} else if (target == C.GL_TEXTURE_2D) {
			itarget = GL.GL_TEXTURE_2D;
		} else if (target == C.GL_BLEND) {
			itarget = GL.GL_BLEND;
		} else {
			throw new IllegalArgumentException();
		}
		return gl.glIsEnabled(itarget);
	}

	@Override
	public void glMaterialfv(C c1, C c2, float[] fv) {
		int ic1 = 0;
		if (c1 == C.GL_FRONT_AND_BACK) {
			ic1 = GL2.GL_FRONT_AND_BACK;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_AMBIENT) {
			ic2 = GL2.GL_AMBIENT;
		} else if (c2 == C.GL_DIFFUSE) {
			ic2 = GL2.GL_DIFFUSE;
		} else if (c2 == C.GL_EMISSION) {
			ic2 = GL2.GL_EMISSION;
		} else if (c2 == C.GL_SPECULAR) {
			ic2 = GL2.GL_SPECULAR;
		} else if (c2 == C.GL_SHININESS) {
			ic2 = GL2.GL_SHININESS;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glMaterialfv(ic1, ic2, fv, 0);
	}

	@Override
	public void glMaterialf(C c1, C c2, float f) {
		int ic1 = 0;
		if (c1 == C.GL_FRONT_AND_BACK) {
			ic1 = GL2.GL_FRONT_AND_BACK;
		} else {
			throw new IllegalArgumentException();
		}
		int ic2 = 0;
		if (c2 == C.GL_AMBIENT) {
			ic2 = GL2.GL_AMBIENT;
		} else if (c2 == C.GL_DIFFUSE) {
			ic2 = GL2.GL_DIFFUSE;
		} else if (c2 == C.GL_EMISSION) {
			ic2 = GL2.GL_EMISSION;
		} else if (c2 == C.GL_SPECULAR) {
			ic2 = GL2.GL_SPECULAR;
		} else if (c2 == C.GL_SHININESS) {
			ic2 = GL2.GL_SHININESS;
		} else {
			throw new IllegalArgumentException();
		}
		gl.glMaterialf(ic1, ic2, f);
	}

	@Override
	public int glGetIntegerv(C target) {
		int itarget = 0;
		if (target == C.GL_FRONT_FACE) {
			itarget = GL2.GL_FRONT_FACE;
		} else if (target == C.GL_TEXTURE_BINDING_2D) {
			itarget = GL2.GL_TEXTURE_BINDING_2D;
		} else {
			throw new IllegalArgumentException();
		}
		// return gl.glGetIntegerv(itarget);
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

}
