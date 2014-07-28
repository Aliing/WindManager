package com.ah.mdm.core.profile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

public class CertificateParser
{
	private static Logger		logger			= LoggerFactory.getLogger(CertificateParser.class);

	private final static String	certPEMStart	= "-----BEGIN CERTIFICATE-----\n";
	private final static String	certPEMEnd		= "-----END CERTIFICATE-----";

	/**
	 * this method is used to analysis the input stream from .p12 file
	 * 
	 * @param in
	 * @param keyStorePassword
	 * @return X509Certificate
	 * 
	 * */
	public static X509Certificate ananysisP12(InputStream in, char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException
	{
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(in, keyStorePassword);
		in.close();
		Enumeration<String> enums = keyStore.aliases();
		if (enums.hasMoreElements())
		{
			String keyAlis = enums.nextElement();
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlis);
			return certificate;
		}
		return null;
	}

	/**
	 * read p12file's key
	 * 
	 * @param in
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 */
	public static PrivateKey ananysisP12Key(InputStream in, char[] keyStorePassword, char[] keyPassword) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException
	{
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(in, keyStorePassword);
		in.close();
		Enumeration<String> enums = keyStore.aliases();
		if (enums.hasMoreElements())
		{
			String keyAlis = enums.nextElement();
			Key key = keyStore.getKey(keyAlis, keyPassword);
			return (PrivateKey) key;
		}
		return null;
	}

	/**
	 * this method is used to analysis the input stream from .cert file, be
	 * careful the normal .cert file starts with "-----BEGIN CERTIFICATE----- "
	 * and ends with "-----END CERTIFICATE-----", if this method was used to
	 * analysis certificate data ,that exception will be throw
	 * 
	 * @param in
	 * @return X509Certificate
	 * 
	 * */
	public static X509Certificate analysisCert(InputStream in) throws CertificateException
	{
		X509Certificate cert = null;
		CertificateFactory cetFactory = CertificateFactory.getInstance("X.509");
		cert = (X509Certificate) cetFactory.generateCertificate(in);
		return cert;
	}

	/**
	 * get the issuer of the special X509Certificate
	 * 
	 * @param X509Certificate
	 * @return String
	 * 
	 * */
	public static String getIssuer(X509Certificate x509)
	{
		if (x509 == null)
		{
			throw new NullPointerException();
		}
		// 1.2.840.113549.1.9.1=#1610726865406165726f686976652e636f6d,CN=ruihe,OU=nms,O=aerohive,L=hangzhou,ST=zhejiang,C=CN
		String name = x509.getIssuerX500Principal().getName();
		String issuedValue = "";
		try
		{
			String[] arr = name.split(",");
			for (String s : arr)
			{
				if (s.startsWith("CN="))
				{
					issuedValue = s;
				}
			}
		} catch (Exception e)
		{
			logger.error("Exception occured: ", e);
		}
		return issuedValue;
	}

	/**
	 * get the date not after of the special X509Certificate
	 * 
	 * @param X509Certificate
	 * @return String
	 * 
	 * */
	public static Date getNotAfter(X509Certificate x509)
	{
		return x509.getNotAfter();
	}

	/**
	 * get the date not before of the special X509Certificate
	 * 
	 * @param X509Certificate
	 * @return String
	 * 
	 * */
	public static Date getNotBefore(X509Certificate x509)
	{
		return x509.getNotBefore();
	}

	/**
	 * get the data of the special X509Certificate
	 * 
	 * @param X509Certificate
	 * @return String
	 * 
	 * */
	public static byte[] getData(X509Certificate x509) throws CertificateEncodingException
	{
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(x509.getEncoded()).getBytes();
	}

	/**
	 * Check whether this certificate is the CA
	 * 
	 * @param x509
	 *            X509Certificate
	 * @return
	 */
	public static boolean isCA(X509Certificate x509)
	{
		return x509.getBasicConstraints() != -1;
	}

	// Converts to the PEM format
	public static String toPEM(byte[] certBytes) throws Exception
	{
		String s = "\n";
		byte[] lineSep = s.getBytes();

		// PEM format requires each line to have 64 characters
		Base64 b64 = new Base64(64, lineSep);

		String pemCert = new String(b64.encode(certBytes), "UTF-8");
		StringBuffer ret = new StringBuffer("");
		ret.append(certPEMStart).append(pemCert).append(certPEMEnd);

		return ret.toString();
	}

	public static String removeCertStartAndEndStr(String certContent)
	{
		String s = String.valueOf(certContent);
		s = s.replaceAll("-----BEGIN CERTIFICATE-----", "");
		s = s.replaceAll("-----END CERTIFICATE-----", "");
		return s;
	}
}
