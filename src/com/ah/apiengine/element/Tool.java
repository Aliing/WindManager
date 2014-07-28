package com.ah.apiengine.element;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;

public class Tool {
	public static void putString(ByteBuffer bb, String str) throws UnsupportedEncodingException {
		int len = -1;
		byte[] toBytes;

		if (str == null) {
			str = "";
		}
		toBytes = str.getBytes("iso-8859-1");
		len = toBytes.length;
		bb.putShort((short) len);
		bb.put(toBytes);
	}

	public static String getString(ByteBuffer bb) {
		short len = -1;
		len = bb.getShort();
		return AhDecoder.bytes2String(bb, len);
	}

}
