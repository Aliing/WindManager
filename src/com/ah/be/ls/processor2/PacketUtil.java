package com.ah.be.ls.processor2;

import java.nio.ByteBuffer;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import com.ah.be.ls.data2.Header;
import com.ah.be.ls.util.CommConst;
import com.ah.util.Tracer;

public class PacketUtil {
//	private static final Log log = LogFactory.getLog("commonlog.Packet");
                  private static final Tracer log = new Tracer(PacketUtil.class.getSimpleName());
	public static final byte[] secret_key = { (byte) 0x01,
			(byte) 0x03,
			(byte) 0x05,
			(byte) 0x07,
			(byte) 0x09,
			(byte) 0x0b,
			(byte) 0x0d,
			(byte) 0x0f,
			(byte) 0x80,
			(byte) 0x82,
			(byte) 0x84,
			(byte) 0x86,
			(byte) 0x88,
			(byte) 0x8a,
			(byte) 0x8c,
			(byte) 0x8e,
			(byte) 0xf1,
			(byte) 0xf2,
			(byte) 0xf3,
			(byte) 0xf4,
			(byte) 0xf5,
			(byte) 0xf6,
			(byte) 0xf7,
			(byte) 0xf8 };

	private static byte[] encryptData(byte[] content) {
		byte[] outBytes = null;

		try {
			Key key = new SecretKeySpec(secret_key, "DESede");

			Cipher cipher = Cipher.getInstance("DESede", "SunJCE");

			cipher.init(Cipher.ENCRYPT_MODE, key, cipher.getParameters());

			outBytes = cipher.doFinal(content);

		} catch (Exception ex) {
			log.error("PacketUtil",ex.getMessage(), ex);
			return outBytes;
		}

		return outBytes;
	}

	private static byte[] decryptData(byte[] bInput) {
		byte[] outBytes = null;
		try {
			Key key = new SecretKeySpec(secret_key, "DESede");

			Cipher cipher = Cipher.getInstance("DESede", "SunJCE");

			cipher.init(Cipher.DECRYPT_MODE, key, cipher.getParameters());

			outBytes = cipher.doFinal(bInput);

		} catch (Exception ex) {
			log.error("PacketUtil",ex.getMessage(), ex);
			return outBytes;
		}

		return outBytes;
	}

	public static byte[] join(Header header, byte[] content) {
		ByteBuffer buf = ByteBuffer.allocate(8192);

		byte[] outBytes;
		if (content.length == 0) {
			outBytes = new byte[0];
		} else {
			if (header.isSecretFlag()) {
				// encrypt data
				outBytes = encryptData(content);
			} else {
				outBytes = new byte[content.length];
				System.arraycopy(content, 0, outBytes, 0, content.length);
			}
		}
		buf.put(header.getType());
		buf.putInt(outBytes.length);
		buf.put(header.getProtocolVersion());
		buf.put(header.isSecretFlag() ? CommConst.Secret_Flag_Yes : CommConst.Secret_Flag_No);
		buf.put(outBytes);

		buf.flip();

		byte[] dst = new byte[buf.limit()];
		buf.get(dst);
		return dst;
	}

	public static byte[] split(ByteBuffer buf, Header header) {
		header.setPacketType(buf.get());
		header.setLength(buf.getInt());
		header.setProtocolVersion(buf.get());
		header.setSecretFlag(buf.get() == CommConst.Secret_Flag_Yes ? true : false);

		byte[] content = new byte[buf.remaining()];
		buf.get(content);

		if (header.isSecretFlag()) {
			byte[] outb = decryptData(content);
			return outb;
		} else {
			return content;
		}
	}

}
