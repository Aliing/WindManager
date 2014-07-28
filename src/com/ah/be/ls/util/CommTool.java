/**
 *@filename		CommTool.java
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

package com.ah.be.ls.util;

import java.io.OutputStream;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ah.be.log.BeLogTools;
import com.ah.be.ls.ClientTrustManager;
import com.ah.be.ls.HostnameVerify;

public class CommTool {

	public static void long2bytes(byte[] bOut, long lValue, int iOffset) {
		for (int i = 0; i < 8; ++i) {
			bOut[iOffset + i] = (byte) ((lValue >> (7 - i) * 8) & 0xFF);
		}
	}

	public static void int2bytes(byte[] bOut, int iValue, int iOffset) {
		for (int i = 0; i < 4; ++i) {
			bOut[iOffset + i] = (byte) ((iValue >> (3 - i) * 8) & 0xFF);
		}
	}

	public static void short2bytes(byte[] bOut, short sValue, int iOffset) {
		for (int i = 0; i < 2; ++i) {
			bOut[iOffset + i] = (byte) (sValue >> ((1 - i) * 8) & 0xFF);
		}

	}

	public static int bytes2int(byte[] bInput, int bSize, int iOffset) {
		if (bSize > 4) {
			bSize = 4;
		}

		int iReturn = 0;
		int iTmp = 0;

		for (int i = 0; i < bSize; ++i) {
			// iReturn = iReturn + (bInput[i] << (3-i)*8);
			iReturn <<= 8;
			iTmp = bInput[i + iOffset] & 0xFF;

			iReturn |= iTmp;
		}

		return iReturn;
	}

	public static long bytes2long(byte[] bInput, int bSize, int iOffset) {
		if (bSize > 8) {
			bSize = 8;
		}

		long lReturn = 0;
		long lTmp = 0;

		for (int i = 0; i < bSize; ++i) {
			// iReturn = iReturn + (bInput[i] << (3-i)*8);
			lReturn <<= 8;
			lTmp = bInput[i + iOffset] & 0xFF;

			lReturn |= lTmp;
		}

		return lReturn;
	}

	public static short bytes2short(byte[] bInput, int bSize, int iOffset) {
		if (bSize > 2) {
			bSize = 2;
		}

		short sReturn = 0;
		short sTmp = 0;

		for (int i = 0; i < bSize; ++i) {
			sReturn <<= 8;
			sTmp = (short) (bInput[i + iOffset] & 0xFF);

			sReturn |= sTmp;
		}

		return sReturn;
	}

	public static short byte2short(byte bValue) {
		short sReturn = 0;

		short sTmp = (short) (bValue & 0xFF);

		sReturn |= sTmp;

		return sReturn;
	}

	public static int short2int(short sValue) {
		int iReturn = 0;

		int iTmp = sValue & 0xFFFF;

		iReturn |= iTmp;

		return iReturn;
	}

	public static int string2bytes(byte[] bOut, String strInput, int iOffset) {
		byte[] bTmp;
		if (strInput == null) {
			strInput = "";
		}

		try {
			bTmp = strInput.getBytes(CommConst.Charset_Name);
		} catch (Exception ex) {
			bTmp = strInput.getBytes();
		}

		System.arraycopy(bTmp, 0, bOut, iOffset, bTmp.length);

		return bTmp.length;
	}

	public static String byte2string(byte[] bInput, int ioffset, int iLength) {
		int actualLen = -1;
		for (int i = ioffset; i < ioffset + iLength; i++) {
			if (bInput[i] == 0) {
				actualLen = i - ioffset;
				break;
			}
		}
		if (actualLen == -1) {
			actualLen = iLength;
		}

		try {
			return new String(bInput, ioffset, actualLen, CommConst.Charset_Name);
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(), ex);
			return new String(bInput, ioffset, actualLen);
		}
	}

	public static void ip2bytes(byte[] bOutput, int ioffset, String strIp) {
		String[] strTmp = strIp.split("\\.", 4);

		for (int i = 0; i < 4; ++i) {
			bOutput[i + ioffset] = (byte) (Integer.parseInt(strTmp[i]) & 0xFF);
		}
	}

	public static String bytes2ip(byte[] bInput, int ioffset) {
		return String.valueOf(byte2short(bInput[ioffset])) + "."
				+ String.valueOf(byte2short(bInput[ioffset + 1])) + "."
				+ String.valueOf(byte2short(bInput[ioffset + 2])) + "."
				+ String.valueOf(byte2short(bInput[ioffset + 3]));

	}

	public static void innerVersion2bytes(byte[] bOut, int ioffset, String strInnerVer) {
		String[] strTmp = strInnerVer.split("\\.", 3);

		for (int i = 0; i < 3; ++i) {
			bOut[ioffset + i] = (byte) ((Integer.parseInt(strTmp[i]) & 0xFF));
		}
	}

	public static String bytes2innerVersion(byte[] bInput, int iOffset) {
		return String.valueOf(byte2short(bInput[iOffset])) + "."
				+ String.valueOf(byte2short(bInput[iOffset + 1])) + "."
				+ String.valueOf(byte2short(bInput[iOffset + 2]));
	}

	public static int getRandInt() {
		int iReturn = 0;

		Random rTmp = new Random();

		int iTmp = rTmp.nextInt(126) + 1;

		iReturn |= (iTmp & 0xFF);

		for (int i = 1; i < 4; i++) {
			iReturn = iReturn << 8;
			iTmp = rTmp.nextInt(255);
			iReturn |= (iTmp & 0xFF);
		}

		return iReturn;
	}

	public static String getClientIp(HttpServletRequest req) {
		String ip = req.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getRemoteAddr();
		}

		return ip;
	}

	public static void initHttps() throws Exception {
		trustAllCeritificates();
		verifyHostName();
	}

	private static void trustAllCeritificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];

		TrustManager tm = new ClientTrustManager();

		trustAllCerts[0] = tm;

		SSLContext ssl_context = SSLContext.getInstance("SSL");

		ssl_context.init(null, trustAllCerts, null);

		HttpsURLConnection.setDefaultSSLSocketFactory(ssl_context.getSocketFactory());
	}

	private static void verifyHostName() {
		HostnameVerify hv = new HostnameVerify();

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	public static void sendErrorResponse(HttpServletResponse response) {
		byte[] bBuffer = new byte[1];
		bBuffer[0] = CommConst.Error_Response_Flag;

		try {
			OutputStream os = response.getOutputStream();
			os.write(bBuffer, 0, 1);
			os.flush();
			os.close();
		} catch (Exception ex) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(), ex);
		}
	}
	
	public static void sendRestoreCheckResponse(HttpServletResponse response, byte returnCode){
		byte[] bBuffer = new byte[1];
		bBuffer[0] = returnCode;

		try {
			OutputStream os = response.getOutputStream();
			os.write(bBuffer, 0, 1);
			os.flush();
			os.close();
		} catch (Exception ex) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(), ex);
		}
	}
	
	public static String subString(String strSrc, String strFlag)
	{
		if(null == strSrc)
		{
			return null;
		}
		
		int i = 0;
		
		if((i=strSrc.indexOf(strFlag)) == -1)
		{
			return null;
		}
		
		return strSrc.substring(i+1);
	}

	public static void main(String[] args) {	

	}
}
