package net.yzwlab.javammd;

/**
 * データ読み込み用バッファの抽象です。
 */
public interface IReadBuffer {

	/**
	 * バイト配列からバッファを生成します。
	 * 
	 * @param data
	 *            バイト配列。nullにはならない。
	 * @return バッファ。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public IReadBuffer createFromByteArray(byte[] data) throws ReadException;

	public boolean isEOF();

	public byte readByte() throws ReadException;

	public byte[] readByteArray(int length) throws ReadException;

	public byte[][] readByteArray(int len1, int len2) throws ReadException;

	public short readUByte() throws ReadException;

	public short[] readUByteArray(int length) throws ReadException;

	public short[][] readUByteArray(int len1, int len2) throws ReadException;

	public short readShort() throws ReadException;

	public short[] readShortArray(int length) throws ReadException;

	public short[][] readShortArray(int len1, int len2) throws ReadException;

	public int readUShort() throws ReadException;

	public int[] readUShortArray(int length) throws ReadException;

	public int[][] readUShortArray(int len1, int len2) throws ReadException;

	public int readInteger() throws ReadException;

	public int[] readIntegerArray(int length) throws ReadException;

	public int[][] readIntegerArray(int len1, int len2) throws ReadException;

	public long readUInt() throws ReadException;

	public long[] readUIntArray(int length) throws ReadException;

	public long[][] readUIntArray(int len1, int len2) throws ReadException;

	public long readLong() throws ReadException;

	public long[] readLongArray(int length) throws ReadException;

	public long[][] readLongArray(int len1, int len2) throws ReadException;

	public float readFloat() throws ReadException;

	public float[] readFloatArray(int length) throws ReadException;

	public float[][] readFloatArray(int len1, int len2) throws ReadException;

	public double readDouble() throws ReadException;

	public double[] readDoubleArray(int length) throws ReadException;

	public double[][] readDoubleArray(int len1, int len2) throws ReadException;

	/**
	 * バッファを文字列として解釈し、1行分のバイト配列を取得します。
	 * 
	 * @return 1行分のバイト配列。ストリームの末尾の場合はnull。
	 * @throws ReadException
	 *             読み込み失敗時のエラー。
	 */
	public byte[] readLine() throws ReadException;

}
