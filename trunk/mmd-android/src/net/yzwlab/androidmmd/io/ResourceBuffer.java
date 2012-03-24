package net.yzwlab.androidmmd.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

/**
 * ファイルバッファの実装です。
 */
public class ResourceBuffer extends StreamBuffer implements IReadBuffer {

	private static long getLength(ClassLoader loader, String path)
			throws IOException {
		if (loader == null || path == null) {
			throw new IllegalArgumentException();
		}
		InputStream in = null;
		long count = 0L;
		try {
			in = loader.getResourceAsStream(path);
			if (in == null) {
				throw new FileNotFoundException(path);
			}
			BufferedInputStream bin = new BufferedInputStream(in);
			while (bin.read() != -1) {
				count++;
			}
			return count;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					;
				}
				in = null;
			}
		}
	}

	public ResourceBuffer(ClassLoader loader, String path) throws IOException {
		super(new DataInputStream(new BufferedInputStream(
				loader.getResourceAsStream(path))), getLength(loader, path));
	}

	public IReadBuffer createFromByteArray(byte[] data) throws ReadException {
		return new ByteBuffer(data);
	}

}
