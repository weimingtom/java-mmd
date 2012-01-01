package net.yzwlab.gwtmmd.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.format.PMDFile;

/**
 * 解析済みのPMDファイルです。
 */
public class AnalyzedPMDFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ファイルを保持します。
	 */
	private PMDFile file;

	/**
	 * ベースパスを保持します。
	 */
	private String baseDir;

	/**
	 * 画像ファイル名リストを保持します。
	 */
	private List<String> imageFilenames;

	/**
	 * 構築します。
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
