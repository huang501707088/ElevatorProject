package com.hdos.elevatorproject.common;



import java.security.MessageDigest;


/**
 * 描述：MD5算法基础类
 * 
 * @author 创建时间：2009-4-2
 */

public class MD5 {
	public static void main(String[] args) {
		String passwordDb = MD5.encode("pengyejian");
		boolean veryFlag = MD5.verify("pengyejian", passwordDb);
		System.out.println(veryFlag);
	}
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 计算MD5
	 * 
	 * @param origin
	 *            原始字符串
	 * @param algorithm
	 *            MD5算法名称: "MD5,SHA-1"
	 * @return md5字符串；null：出错
	 */
	public static String encode(String origin, String algorithm) {
		return encode(origin, algorithm, "UTF-8");
	}

	/**
	 * 计算MD5
	 * 
	 * @param origin
	 *            原始字符串
	 * @param algorithm
	 *            MD5算法名称: "MD5,SHA-1"
	 * @param charset
	 *            字符集: "UTF-8","GBK"
	 * @return md5字符串；null：出错
	 */
	public static String encode(String origin, String algorithm, String charset) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance(algorithm);
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes(charset)));
		} catch (Exception ex) {
			System.out.println("计算" + algorithm + "出错"+ex);
		}
		return resultString;
	}

	/**
	 * 计算MD5
	 * 
	 * @param origin
	 *            原始字符串
	 * @return md5字符串；null：出错
	 */
	public static String encode(String origin) {
		return encode(origin, "MD5");
	}

	/**
	 * 验证MD5串
	 * 
	 * @param origin
	 *            原始字符串
	 * @param md5
	 *            md5字符串
	 * @param algorithm
	 *            MD5算法名称: "MD5,SHA-1"
	 * @param charset
	 *            字符集: "UTF-8","GBK"
	 * @return true：正确；false：不正确
	 */
	public static boolean verify(String origin, String md5, String algorithm,
			String charset) {
		String tmp = encode(origin, algorithm, charset);
		try {
			if (tmp.equalsIgnoreCase(md5)) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 验证MD5串
	 * 
	 * @param origin
	 *            原始字符串
	 * @param md5
	 *            md5字符串
	 * @param algorithm
	 *            MD5算法名称: "MD5,SHA-1"
	 * @return true：正确；false：不正确
	 */
	public static boolean verify(String origin, String md5, String algorithm) {
		return verify(origin, md5, algorithm, "UTF-8");
	}

	/**
	 * 验证MD5串
	 * 
	 * @param origin
	 *            原始字符串
	 * @param md5
	 *            md5字符串
	 * @return true：正确；false：不正确
	 */
	public static boolean verify(String origin, String md5) {
		return verify(origin, md5, "MD5");
	}

}
