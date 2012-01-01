package net.yzwlab.gwtmmd.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.format.PMDFile;

/**
 * ��͍ς݂�PMD�t�@�C���ł��B
 */
public class AnalyzedPMDFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * �t�@�C����ێ����܂��B
	 */
	private PMDFile file;

	/**
	 * �x�[�X�p�X��ێ����܂��B
	 */
	private String baseDir;

	/**
	 * �摜�t�@�C�������X�g��ێ����܂��B
	 */
	private List<String> imageFilenames;

	/**
	 * �\�z���܂��B
	 */
	public AnalyzedPMDFile() {
		this.file = null;
		this.baseDir = null;
		this.imageFilenames = new ArrayList<String>();
	}

	public PMDFile getFile() {
		return file;
	}

	public void setFile(PMDFile file) {
		this.file = file;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public List<String> getImageFilenames() {
		return imageFilenames;
	}

	public void setImageFilenames(List<String> imageFilenames) {
		this.imageFilenames = imageFilenames;
	}

}
