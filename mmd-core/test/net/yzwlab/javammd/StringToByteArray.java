package net.yzwlab.javammd;

/**
 * �����񂩂�o�C�g�z��ɕϊ����܂��B
 */
public class StringToByteArray {

	/**
	 * ���s���܂��B
	 * 
	 * @param args
	 *            �����Bnull�͕s�B
	 * @throws Exception
	 *             ��O�B
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
