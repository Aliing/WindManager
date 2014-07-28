/**
 *@filename		AhEncoder.java
 *@version
 *@author		Francis
 *@createtime	2007-8-4 11:56:47 AM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.util.coder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhEncoder {

	/**
	 * Convert hex into bytes.
	 * 
	 * @param a_hex
	 *            -
	 * @return Byte array of hex.
	 */
	public static byte[] hex2bytes(String a_hex) {
		if (a_hex == null || a_hex.length() % 2 != 0) {
			return null;
		}

		byte[] bytes = new byte[a_hex.length() / 2];

		for (int i = 0; i < a_hex.length(); i += 2) {
			bytes[i / 2] = (byte) (Integer.parseInt(a_hex.substring(i, i + 2), 16) & 0xff);
		}

		return bytes;
	}

	public static byte[] int2bytes(int a_num) {
		byte[] barray = new byte[4];

		for (int i = 0; i < 4; i++) {
			barray[i] = (byte) ((a_num >>> (24 - 8 * i)) & 0xff);
		}

		return barray;
	}

	/**
	 * Convert ip to long.
	 * 
	 * @param a_ip
	 *            - String of ip address.
	 * @return long value relative to the ip specified as argument.
	 */
	public static long ip2Long(String a_ip) {
		long[] ip = new long[4];
		int position1 = a_ip.indexOf(".");
		int position2 = a_ip.indexOf(".", position1 + 1);
		int position3 = a_ip.indexOf(".", position2 + 1);

		ip[0] = Long.parseLong(a_ip.substring(0, position1));
		ip[1] = Long.parseLong(a_ip.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(a_ip.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(a_ip.substring(position3 + 1));

		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3]; // ip1*256*256*256+ip2*256*256+ip3*256+ip4
	}

	/**
	 * convert ip to int
	 * 
	 *@param
	 * 
	 *@return
	 */
	public static int ip2Int(String a_ip) {
		return (int) ip2Long(a_ip);
	}

	/**
	 * Get valid bit number of netmask that is another way to configure netmask in UNIX/LINUX
	 * system.
	 * 
	 * @param a_netmask
	 *            - String of netmask.
	 * @return Valid bit number of the netmask.
	 */
	public static int netmask2int(String a_netmask) {
		long l = ip2Long(a_netmask);
		String binaryString = Long.toBinaryString(l);
		int len = 32 - binaryString.length();

		for (int i = 0; i < len; i++) {
			binaryString = "0" + binaryString;
		}

		int invalidBitNum = 0;
		char[] c_array = binaryString.toCharArray();

		for (char c : c_array) {
			if (c == '1') {
				invalidBitNum++;// Calculate the number of high bit.
			} else {
				break;
			}
		}

		return invalidBitNum;
	}

	public static void putString(ByteBuffer buf, String str) {
		if (str == null) {
			str = "";
		}
		byte[] b_array = str.getBytes();
		buf.putShort((short) b_array.length);
		if (b_array.length > 0) {
			buf.put(b_array);
		}
	}

	public static void putString(OutputStream out, String str) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		if (str == null) {
			str = "";
		}
		byte[] b_array = str.getBytes();
		buf.putShort((short) b_array.length);
		if (b_array.length > 0) {
			buf.put(b_array);
		}
		buf.flip();
		byte[] tmp = new byte[buf.limit()];
		out.write(tmp);
	}

	public static void putInt(OutputStream out, int size) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(size);
		out.write(buf.array());
	}

}