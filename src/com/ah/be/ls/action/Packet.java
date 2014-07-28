/**
 *@filename		Packet.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.ls.action;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.ah.be.log.BeLogTools;
import com.ah.be.ls.data.PacketHeadData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;
import com.ah.util.Tracer;

public class Packet {
	private static final Tracer	log			= new Tracer(Packet.class.getSimpleName());

	public static final byte[]	secret_key	= { (byte) 0x01, (byte) 0x03, (byte) 0x05, (byte) 0x07,
			(byte) 0x09, (byte) 0x0b, (byte) 0x0d, (byte) 0x0f, (byte) 0x80, (byte) 0x82,
			(byte) 0x84, (byte) 0x86, (byte) 0x88, (byte) 0x8a, (byte) 0x8c, (byte) 0x8e,
			(byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6,
			(byte) 0xf7, (byte) 0xf8		};

	public static int buildPacket(byte[] bOut, int iOffset, PacketHeadData oData, byte[] bData) {
		int iLength = 0;

		// type
		bOut[iOffset + iLength] = oData.getType();
		iLength += CommConst.Type_Util_Length;

		// data-length
		iLength += CommConst.Packet_Length_Util_Length;

		// protocol
		bOut[iOffset + iLength] = oData.getProtocolVersion();
		iLength += CommConst.Protocol_Version_Util_Length;

		// secret flag
		bOut[iOffset + iLength] = oData.getSecretFlag();
		iLength += CommConst.Packet_Secret_Flag_Length;

		if (0 == oData.getLength()) {
			return 0;
		}

		// data
		int iDataSize;

		if (CommConst.Secret_Flag_Yes == oData.getSecretFlag()) {
			// encrypt data
			iDataSize = encrypt_data(bOut, iOffset + iLength, bData, oData.getLength());
		} else {
			iDataSize = oData.getLength();
			System.arraycopy(bData, 0, bOut, iOffset + iLength, oData.getLength());
		}

		if (0 == iDataSize) {
			return 0;
		}

		CommTool.int2bytes(bOut, iDataSize, iOffset + CommConst.Type_Util_Length);

		iLength += iDataSize;

		return iLength;
	}

	public static int parsePacket(byte[] bOut, PacketHeadData oData, byte[] bInput, int iOffset) {
		int iLength = 0;

		// type
		oData.setType(bInput[iOffset + iLength]);
		iLength += CommConst.Type_Util_Length;

		// data-length
		int iDataSize = CommTool.bytes2int(bInput, CommConst.Packet_Length_Util_Length, iOffset
				+ iLength);
		iLength += CommConst.Packet_Length_Util_Length;

		if (iDataSize <= 0) {
			return 0;
		}

		// protocol
		oData.setProtocolVersion(bInput[iOffset + iLength]);
		iLength += CommConst.Protocol_Version_Util_Length;

		// secret
		oData.setSecretFlag(bInput[iOffset + iLength]);
		iLength += CommConst.Packet_Secret_Flag_Length;

		int iData_length;

		if (CommConst.Secret_Flag_Yes == oData.getSecretFlag()) {
			// decrypt data
			iData_length = decrypt_data(bOut, bInput, iOffset + iLength, iDataSize);
		} else {
			iData_length = iDataSize;
			System.arraycopy(bInput, iOffset + iLength, bOut, 0, iDataSize);
		}

		if (0 == iData_length) {
			return 0;
		}

		oData.setLength(iData_length);

		iLength += iData_length;

		return iLength;
	}

	private static int encrypt_data(byte[] bOut, int iOffset, byte[] bInput, int iSize) {
		int iLength = 0;

		try {
			Key desk = new SecretKeySpec(secret_key, "DESede");

			Cipher encrypt_cipher = Cipher.getInstance("DESede", "SunJCE");

			encrypt_cipher.init(Cipher.ENCRYPT_MODE, desk, encrypt_cipher.getParameters());

			iLength = encrypt_cipher.doFinal(bInput, 0, iSize, bOut, iOffset);

		} catch (Exception ex) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(), ex);
			
			return iLength;
		}

		return iLength;
	}

	private static int decrypt_data(byte[] bOut, byte[] bInput, int iOffset, int iSize) {
		int iLength = 0;

		try {
			Key desk = new SecretKeySpec(secret_key, "DESede");

			Cipher encrypt_cipher = Cipher.getInstance("DESede", "SunJCE");

			encrypt_cipher.init(Cipher.DECRYPT_MODE, desk, encrypt_cipher.getParameters());

			iLength = encrypt_cipher.doFinal(bInput, iOffset, iSize, bOut);

		} catch (Exception ex) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(), ex);
			return iLength;
		}

		return iLength;
	}

	/**
	 * parse packet head, below 2 method used for lower communication interface
	 * 
	 * @author Jun
	 * @param headBuffer
	 * @return packet head object
	 */
	public static PacketHeadData parsePacketHead(final byte[] headBuffer) {
		PacketHeadData headData = new PacketHeadData();

		int offset = 0;

		// packet type
		headData.setType(headBuffer[offset]);
		offset += 1;

		// data length
		headData.setLength(CommTool.bytes2int(headBuffer, 4, offset));
		offset += 4;

		// packet protocol version
		headData.setProtocolVersion(headBuffer[offset]);
		offset += 1;

		// secret flag
		headData.setSecretFlag(headBuffer[offset]);
		offset += 1;

		return headData;
	}

	/**
	 * decrypt packet data
	 * 
	 * @author Jun
	 * @param dataBuffer
	 * @return decrypted packet data
	 */
	public static byte[] decryptData(final byte[] dataBuffer, int dataLen) {
		byte[] tmpBuffer = new byte[CommConst.BUFFER_SIZE];
		int length = 0;

		try {
			Key desk = new SecretKeySpec(secret_key, "DESede");

			Cipher encrypt_cipher = Cipher.getInstance("DESede", "SunJCE");

			encrypt_cipher.init(Cipher.DECRYPT_MODE, desk, encrypt_cipher.getParameters());

			length = encrypt_cipher.doFinal(dataBuffer, 0, dataLen, tmpBuffer, 0);

		} catch (Exception ex) {
			log.error("decryptData", "decryptData exception:", ex);
			return null;
		}

		byte[] outBuffer = new byte[length];
		System.arraycopy(tmpBuffer, 0, outBuffer, 0, length);

		return outBuffer;
	}

	public static void main(String[] args) {
		byte[] s = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		byte[] bTmp = new byte[126];

		Arrays.fill(bTmp, (byte) 0);

		System.out.println(encrypt_data(bTmp, 0, s, s.length));
		System.out.println(Arrays.toString(bTmp));

		byte[] bClear = new byte[126];

		System.out.println(decrypt_data(bClear, bTmp, 0, 16));
		System.out.println(Arrays.toString(bClear));
	}

}
