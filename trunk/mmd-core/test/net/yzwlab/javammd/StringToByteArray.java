package net.yzwlab.javammd;

/**
 * 文字列からバイト配列に変換します。
 */
public class StringToByteArray {

	/**
	 * 実行します。
	 * 
	 * @param args
	 *            引数。nullは不可。
	 * @throws Exception
	 *             例外。
	 */
	public static void main(String[] args) throws Exception {
		for (String arg : args) {
			StringBuffer buf = new StringBuffer();
			for (byte dt : arg.getBytes("Shift_JIS")) {
				if (buf.length() > 0) {
					buf.append(", ");
				}
				buf.append("(byte) ");
				buf.append(dt);
			}
			System.out.println("  " + arg + " = new byte[]{ " + buf + " }");
		}
	}

}
