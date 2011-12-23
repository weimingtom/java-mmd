package net.yzwlab.javammd.format;

import net.yzwlab.javammd.ReadBuffer;
import net.yzwlab.javammd.ReadException;


public class PMD_MATERIAL_RECORD {
	protected S5 diffuse;
	
	protected float shininess;
	
	protected S6 specular;
	
	protected S6 ambient;
	
	protected short p12;
	
	protected int nEdges;
	
	protected byte[] textureFileName;
	
	public PMD_MATERIAL_RECORD() {
		this.diffuse = new S5();
		this.shininess = 0.0f;
		this.specular = new S6();
		this.ambient = new S6();
		this.p12 = 0;
		this.nEdges = 0;
		this.textureFileName = new byte[20];
	}
	
	public S5 getDiffuse() {
		return diffuse;
	}
	
	public void setDiffuse(S5 diffuse) {
		this.diffuse = diffuse;
	}
	
	public float getShininess() {
		return shininess;
	}
	
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	
	public S6 getSpecular() {
		return specular;
	}
	
	public void setSpecular(S6 specular) {
		this.specular = specular;
	}
	
	public S6 getAmbient() {
		return ambient;
	}
	
	public void setAmbient(S6 ambient) {
		this.ambient = ambient;
	}
	
	public short getP12() {
		return p12;
	}
	
	public void setP12(short p12) {
		this.p12 = p12;
	}
	
	public int getNEdges() {
		return nEdges;
	}
	
	public void setNEdges(int nEdges) {
		this.nEdges = nEdges;
	}
	
	public byte[] getTextureFileName() {
		return textureFileName;
	}
	
	public void setTextureFileName(byte[] textureFileName) {
		this.textureFileName = textureFileName;
	}
	
	public PMD_MATERIAL_RECORD Read(ReadBuffer buffer) throws ReadException {
		if(buffer == null) {
			throw new IllegalArgumentException();
		}
		this.diffuse = (new S5()).Read(buffer);
		this.shininess = buffer.readFloat();
		this.specular = (new S6()).Read(buffer);
		this.ambient = (new S6()).Read(buffer);
		this.p12 = buffer.readShort();
		this.nEdges = buffer.readInteger();
		this.textureFileName = buffer.readByteArray(20);
		return this;
	}
	public class S5 {
		protected float r;
		
		protected float g;
		
		protected float b;
		
		protected float a;
		
		public S5() {
			this.r = 0.0f;
			this.g = 0.0f;
			this.b = 0.0f;
			this.a = 0.0f;
		}
		
		public float getR() {
			return r;
		}
		
		public void setR(float r) {
			this.r = r;
		}
		
		public float getG() {
			return g;
		}
		
		public void setG(float g) {
			this.g = g;
		}
		
		public float getB() {
			return b;
		}
		
		public void setB(float b) {
			this.b = b;
		}
		
		public float getA() {
			return a;
		}
		
		public void setA(float a) {
			this.a = a;
		}
		
		public S5 Read(ReadBuffer buffer) throws ReadException {
			if(buffer == null) {
				throw new IllegalArgumentException();
			}
			this.r = buffer.readFloat();
			this.g = buffer.readFloat();
			this.b = buffer.readFloat();
			this.a = buffer.readFloat();
			return this;
		}
	}
	public class S6 {
		protected float r;
		
		protected float g;
		
		protected float b;
		
		public S6() {
			this.r = 0.0f;
			this.g = 0.0f;
			this.b = 0.0f;
		}
		
		public float getR() {
			return r;
		}
		
		public void setR(float r) {
			this.r = r;
		}
		
		public float getG() {
			return g;
		}
		
		public void setG(float g) {
			this.g = g;
		}
		
		public float getB() {
			return b;
		}
		
		public void setB(float b) {
			this.b = b;
		}
		
		public S6 Read(ReadBuffer buffer) throws ReadException {
			if(buffer == null) {
				throw new IllegalArgumentException();
			}
			this.r = buffer.readFloat();
			this.g = buffer.readFloat();
			this.b = buffer.readFloat();
			return this;
		}
	}
}
