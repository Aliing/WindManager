package com.ah.be.sa3party;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;

public class SaComm {
	
	public static final int T_LOGIN1_Q         = 1;
	public static final int T_LOGIN1_R         = 2;
	public static final int T_LOGIN2_Q         = 3;
	public static final int T_LOGIN2_R         = 4;
    public static final int T_RETRIVEAP_Q      = 11;
    public static final int T_RETRIVEAP_R      = 12;
    public static final int T_STARTSA_Q        = 13;
    public static final int T_STARTSA_R        = 14;
    public static final int T_STOPSA_Q         = 15;
    public static final int T_STOPSA_R         = 16;
    public static final int T_APINFO_N         = 101;
    public static final int T_SADATA_N         = 102;
    
    public static final int E_T_LOGINFO_1      = 1;
    public static final int E_T_LOGINFO_2      = 2;
    public static final int E_T_APINFO         = 3;
    public static final int E_T_STARTSA_INFO   = 4;
    public static final int E_T_STOPSA_INFO    = 5;
    public static final int E_T_SPECDATA_INFO  = 6;
    public static final int E_T_RESULT_DESC    = 7;
    public static final int E_T_AP_IDENT       = 8;
    
    public static final String WIFI_NAME_0     = "wifi0";
    public static final String WIFI_NAME_1     = "wifi1";
    
    public static final String MD5_POSTFIX     = "HiveMaNager";
    
    private static String SA_UsrName           = "SA";
    private static String SA_PASSWD            = "aerohive";
    
    public static int		MAX_DATA_SIZE	   = 0X40000;
	public static int		BUFFER_SIZE		   = 4096;
	public static int       SA_PORT            = 12229;
    
    public static void setSAUsr(String strUsr)
    {
    	SA_UsrName = strUsr;
    }
    
    public static String getSAUsr()
    {
    	return SA_UsrName;
    }
    
    public static void setSAPasswd(String strPsd)
    {
    	SA_PASSWD = strPsd;
    }
    
    public static String getSAPasswd()
    {
    	return SA_PASSWD;
    }
    
    public static byte[] getMd5(byte[] bsrc)
    {
    	try
    	{
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		md.update(bsrc);
    		return md.digest();
    	}
    	catch(Exception ex)
    	{
    		return null;
    	}    	
    }
    
    public static void long2bytes(byte[] bOut, long lValue, int iOffset) {
		for (int i = 0; i < 8; ++i) {
			bOut[iOffset + i] = (byte) ((lValue >> (7 - i) * 8) & 0xFF);
		}
	}
    
    public static int get16randm(byte[] buffer,int iOffset)
    {
    	long ltmp = System.currentTimeMillis();    	
    	SaComm.long2bytes(buffer, ltmp, iOffset);    	
    	ltmp = System.currentTimeMillis();
    	SaComm.long2bytes(buffer, ltmp, iOffset+8);
    	
    	return 16;
    }
    
	/**
	 * send data
	 * 
	 * @param arg_Data data bytes
	 * @return -
	 * @throws IOException -
	 */
	public static int send(SocketChannel cChannel, ByteBuffer arg_Data) throws IOException
	{
		if ((cChannel == null)||(!cChannel.isConnected()))
			throw new IOException("Have not connected");
		int length = arg_Data.limit();
		while(length > 0) {
			int int_Ret = cChannel.write(arg_Data);
			if (int_Ret < 0)
				throw new IOException("Fail to send data");
			length -= int_Ret;
		}
		return 0;
	}
	
	public static void long2unsignedbytes(byte[] bOut, long lValue, int iOffset) {
		for (int i = 0; i < 4; ++i) {
			bOut[iOffset + i] = (byte) ((lValue >> (3- i) * 8) & 0xFF);
		}
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
	
	public static short byte2short(byte b)
	{
		short s = 0;
		s |= (b&0xff);
		
		return s;
	}

    
    public static void main(String[] args)
    {
    	long ltmp = System.currentTimeMillis()/1000;
    	
    	System.out.println(ltmp);
    	byte[] btmp = new byte[4];
    	long2unsignedbytes(btmp,ltmp,0);
    	System.out.println(bytes2long(btmp,4,0));
    	
    }
}
