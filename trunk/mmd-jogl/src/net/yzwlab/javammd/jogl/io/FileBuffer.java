package net.yzwlab.javammd.jogl.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * ファイルバッファの実装です。
 */
public class FileBuffer extends StreamBuffer implements IReadBuffer {

	public FileBuffer(File f) throws IOException {
		super(new DataInputStream(new BufferedInputStream(
				new FileInputStream(f))), f.length());
	}

	public FileBuffer(String path) throws IOException {
		this(new File(path));
	}

	@Override
	public IReadBuffer createFromByteArray(byte[] data) throws ReadException {
		return new ByteBuffer(data);
	}

}
