/**
 *@filename		AerohiveEncryptTool.java
 *@version		v1.7
 *@author		Fiona
 *@createtime	Mar 8, 2007 3:00:22 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.common;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

import com.ah.be.app.DebugUtil;

/**
 * @author Fiona
 * @version v1.7
 */
public class AerohiveEncryptTool
{
	private Cipher				m_Cipher_Ecipher;
	private Cipher				m_Cipher_Dcipher;

	// 8-byte Salt
	private static byte[]		m_Byte_Salt				=
														{ (byte) 0xA9,
		(byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35,
		(byte) 0xE3, (byte) 0x03						};

	// Iteration count
	private static final int	m_Int_IterationCount	= 13;
	private String				m_Str_Key				= "aerohive";

	public AerohiveEncryptTool(String arg_Key)
	{
		if (arg_Key != null && !arg_Key.trim().equals(""))
			m_Str_Key = arg_Key;

		try {
			// Create the key
			KeySpec keySpec = new PBEKeySpec(m_Str_Key.toCharArray(),
				m_Byte_Salt, m_Int_IterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
				.generateSecret(keySpec);
			m_Cipher_Ecipher = Cipher.getInstance(key.getAlgorithm());
			m_Cipher_Dcipher = Cipher.getInstance(key.getAlgorithm());

			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
				m_Byte_Salt, m_Int_IterationCount);

			// Create the ciphers
			m_Cipher_Ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			m_Cipher_Dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (Exception e) {
			DebugUtil.commonDebugWarn(e.getMessage());
		}
	}

	/**
	 * @param arg_OriginalStr
	 * @param arg_Type
	 * @return
	 */
	public String encrypt(String arg_OriginalStr)
	{
		try {
			// Encode the string into bytes using utf-8
			byte[] byte_Utf8 = arg_OriginalStr.getBytes("UTF8");

			// Encrypt
			byte[] byte_Enc = m_Cipher_Ecipher.doFinal(byte_Utf8);

			// Encode bytes to base64 to get a string
			return new String(Base64.encodeBase64(byte_Enc));
		} catch (Exception e) {
			DebugUtil.commonDebugWarn(e.getMessage());
		}
		return null;
	}

	public String decrypt(String arg_OriginalStr)
	{
		try {
			// Decode base64 to get bytes
			byte[] byte_Dec = Base64.decodeBase64(arg_OriginalStr.getBytes());

			// Decrypt
			byte[] byte_Utf8 = m_Cipher_Dcipher.doFinal(byte_Dec);
			
			// Decode using utf-8
			return new String(byte_Utf8, "UTF8");		
		} catch (Exception e) {
			DebugUtil.commonDebugWarn(e.getMessage());
		}
		return null;
	}
}
