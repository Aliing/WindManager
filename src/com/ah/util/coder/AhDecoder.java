/**
 *@filename		AhDecoder.java
 *@version
 *@author		Francis
 *@createtime	2007-8-4 11:54:15 AM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.util.coder;

// java import
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhDecoder {

	public static int bits2int(int a_num, int a_right_shift_num, int a_bit_num) {
		if (a_bit_num > 32) {
			throw new NumberFormatException("The number of bits[" + a_bit_num
					+ "] should be no more than 32");
		} else {
			if (a_bit_num == 32) {
				return a_num >>> a_right_shift_num;
			} else {
				return (a_num >>> a_right_shift_num) & ((1 << a_bit_num) - 1);
			}
		}
	}

	/**
	 * <p>
	 * Convert bytes into int.
	 * </p>
	 * 
	 * @param a_bytes
	 *            byte array to be converted.
	 * @param a_offset
	 *            Start postion relative to the given byte array.
	 * @param a_offset_len
	 *            Number of bytes need to be converted.
	 * @return int value converted from bytes.
	 */
	public static int bytes2int(byte[] a_bytes, int a_offset, int a_offset_len) {
		if (a_offset_len > 4) {
			throw new NumberFormatException("Offset length[" + a_offset_len
					+ "] should be no more than 4.");
		}

		if (a_offset + a_offset_len > a_bytes.length) {
			throw new NumberFormatException("The sum of offset[" + a_offset
					+ "] and offset length[" + a_offset_len
					+ "] should be no more than the length of given bytes.");
		}

		int result = 0;

		for (int i = 0; i < a_offset_len; i++) {
			result += (a_bytes[a_offset + a_offset_len - 1 - i] & 0xff) << (i * 8);
		}

		return result;
	}

	/**
	 * <p>
	 * Convert bytes into int.
	 * </p>
	 * 
	 * @param a_bytes
	 *            byte array to be converted.
	 * @return int value converted from bytes.
	 */
	public static int bytes2int(byte[] a_bytes) {
		return bytes2int(a_bytes, 0, a_bytes.length);
	}

	/**
	 * <p>
	 * Convert bytes into int.
	 * </p>
	 * 
	 * @param a_buf
	 *            ByteBuffer in which byte array reside.
	 * @param a_len
	 *            Number of byte array to be converted into int.
	 * @return int value converted from bytes.
	 */
	public static int bytes2int(ByteBuffer a_buf, int a_len) {
		byte[] b_array = new byte[a_len];
		a_buf.get(b_array);

		return bytes2int(b_array);
	}

	/**
	 * <p>
	 * Convert bytes into hex.
	 * </p>
	 * 
	 * @param bytes
	 *            byte array to be converted into hex.
	 * @param enableHexPrefix
	 *            enable '0X' prefix
	 * @return hex string.
	 */
	public static String bytes2hex(byte[] bytes, boolean enableHexPrefix) {
		StringBuffer hexBuf = new StringBuffer();

		if (enableHexPrefix) {
			hexBuf.append("0X");
			for (byte b : bytes) {
				if (((int) b & 0xff) < 0x10) {
					hexBuf.append("0");
				}

				hexBuf.append(Long.toString((int) b & 0xff, 16).toUpperCase());
			}
		} else {
			for (byte b : bytes) {
				if (((int) b & 0xff) < 0x10) {
					hexBuf.append("0");
				}

				hexBuf.append(Long.toString((int) b & 0xff, 16).toUpperCase());
			}
		}

		return hexBuf.toString();
	}

	/**
	 * <p>
	 * Convert bytes into hex.
	 * </p>
	 * 
	 * @param bytes
	 *            byte array to be converted into hex.
	 * @return hex string.
	 */
	public static String bytes2hex(byte[] bytes) {
		return bytes2hex(bytes, false);
	}

	/**
	 * <p>
	 * Convert bytes into hex.
	 * </p>
	 * 
	 * @param a_buf
	 *            ByteBuffer in which byte array reside.
	 * @param a_len
	 *            Number of byte array to be converted into string.
	 * @return hex string.
	 */
	public static String bytes2hex(ByteBuffer a_buf, int a_len) {
		byte[] b_array = new byte[a_len];
		a_buf.get(b_array);

		return bytes2hex(b_array).toUpperCase();
	}

	/**
	 * <p>
	 * Convert bytes into string using iso-8859-1 encoding.
	 * </p>
	 * 
	 * @param a_buf
	 *            ByteBuffer in which byte array reside.
	 * @param a_len
	 *            Number of byte array to be converted into string.
	 * @return String value converted from byte array.
	 */
	public static String bytes2String(ByteBuffer a_buf, int a_len) {
		byte[] b_array = new byte[a_len];
		a_buf.get(b_array);

		try {
			return new String(b_array, "iso-8859-1");
		} catch (UnsupportedEncodingException uee) {
			return new String(b_array);
		}
	}
	
	/**
	 * <p>
	 * Convert bytes into string using utf-8 encoding.
	 * </p>
	 * 
	 * @param a_buf
	 *            ByteBuffer in which byte array reside.
	 * @param a_len
	 *            Number of byte array to be converted into string.
	 * @return String value converted from byte array.
	 */
	public static String bytes2StringForUtf8(ByteBuffer a_buf, int a_len) {
		byte[] b_array = new byte[a_len];
		a_buf.get(b_array);

		try {
			return new String(b_array, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			return new String(b_array);
		}
	}
	
	/**
	 * <p>
	 * Convert bytes into string using utf-8 encoding.
	 * </p>
	 * 
	 * @param a_buf
	 *            ByteBuffer in which byte array reside.
	 * @param a_len
	 *            Number of byte array to be converted into string.
	 * @return String value converted from byte array.
	 */
	public static String bytes2String(byte[] a_buf) {
		int len = 0;
		for(; len < a_buf.length; len++) {
			if(a_buf[len] == 0)
				break;
		}
		try {
			return new String(a_buf,0,len,"utf-8");
		} catch (UnsupportedEncodingException uee) {
			return new String(a_buf,0,len);
		}
	}
	
	/**
	 * <p>
	 * Convert long into ip.
	 * </p>
	 * 
	 * @param a_value
	 *            long value of ip.
	 * @return String of ip address relative to the long value specifed as argument.
	 */
	public static String long2Ip(long a_value) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((a_value >>> 24) & 0x000000ff));
		sb.append(".");
		sb.append(String.valueOf((a_value & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((a_value & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(a_value & 0x000000FF));

		return sb.toString();
	}

	/**
	 * <p>
	 * Convert a int value which is greater than 0 and less than 32 into a string representation of
	 * the netmask.
	 * </p>
	 * 
	 * @param int_netmask
	 *            which should be greater than 0 and less than 32.
	 * @return A string representation of the netmask.
	 */
	public static String int2Netmask(int int_netmask) {
		if (int_netmask < 0) {
			throw new NumberFormatException("The argument " + int_netmask
					+ " should not less than 0.");
		}

		if (int_netmask > 32) {
			throw new NumberFormatException("The argument " + int_netmask
					+ " should not greater than 32.");
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < int_netmask; i++) {
			sb.append("1");
		}

		for (int i = 0; i < 32 - int_netmask; i++) {
			sb.append("0");
		}

		long l = Long.parseLong(sb.toString(), 2);

		return long2Ip(l);
	}

	/**
	 * Convert int into ip.
	 * 
	 * @param int_value
	 *            - int value of ip.
	 * @return String of ip address relative to the long value specifed as argument.
	 */
	public static String int2IP(int int_value) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((int_value >>> 24) & 0x000000ff));
		sb.append(".");
		sb.append(String.valueOf((int_value & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((int_value & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(int_value & 0x000000FF));

		return sb.toString();
	}

	/**
	 * convert int to byte[]
	 * 
	 * @param number
	 *            int value
	 * @param byteLen
	 *            return value "byte[]" 's length
	 * @return
	 */
	public static byte[] toByteArray(int number, int byteLen) {
		// //check bytelen valid
		// double maxNum = Math.pow(2, byteLen * 8);
		// if (number > maxNum)
		// {
		// return null;
		// }

		int temp = number;
		byte[] b = new byte[byteLen];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	/**
	 * eight bytes number process
	 * 
	 * @param
	 * @return
	 */
	public static double long2double(long l) {
		if (l < 0) {
			double d = ((double) Math.abs(l)) + ((double) Long.MAX_VALUE);

			return d;
		}

		return l;
	}

	/**
	 * bcz there are no unsign type in java, so if 32 bit number biger than 2<<31-1, it will be a
	 * negative int, we must convert it to long(64bit)
	 * 
	 * @param
	 * @return
	 * @author juyizhou
	 */
	public static long int2long(int i) {
		if (i < 0) {
			long l = (long) i;
			l = l << 32;
			l = l >>> 32;
			return l;
		}

		return i;
	}
	public static long int2long(int high, int low) {
        long result = AhDecoder.int2long(high);
        result = result << 32;
        long low_long = AhDecoder.int2long(low);
        result = result | low_long;
        return result;
	}

	/**
	 * same as int2long(), for unsign number from c side
	 * 
	 * @param
	 * @return
	 */
	public static int short2int(short s) {
		if (s < 0) {
			int i = (int) s;
			i = i << 16;
			i = i >>> 16;

			return i;
		}

		return s;
	}

	/**
	 * same as int2long(), for unsign number from c side
	 * 
	 * @param
	 * @return
	 */
	public static int byte2int(byte b) {
		if (b < 0) {
			int i = (int) b;
			i = i << 24;
			i = i >>> 24;

			return i;
		}

		return b;
	}

	/**
	 * for unsign number from c side
	 * 
	 * @param
	 * @return
	 */
	public static short byte2short(byte b) {
		return (short) byte2int(b);
	}

	/**
	 * api for parse time zone byte
	 * 
	 * 00 000000 <br>
	 * daylight saving time | time zone
	 * 
	 * 
	 *@param
	 * 
	 *@return
	 */
	public static byte parseTimeZone(byte b) {
		byte plusTime = (byte) (b >>> 6);
		byte timezone = (byte) (((byte) (b << 2)) >>> 2);
		return (byte) (timezone + plusTime);
	}

	public static String getString(ByteBuffer buf) {
		short len = -1;
		len = buf.getShort();
		return AhDecoder.bytes2String(buf, len);
	}

	public static String getString(InputStream in) throws IOException {
		short len = -1;
		byte[] tmp = new byte[2];
		in.read(tmp);
		len = (short) bytes2int(tmp);

		if (len > 0) {
			tmp = new byte[len];
			in.read(tmp);
			return AhDecoder.bytes2String(ByteBuffer.wrap(tmp), len);
		} else {
			return "";
		}
	}

}