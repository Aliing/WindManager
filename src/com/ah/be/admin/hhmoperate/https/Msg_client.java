package com.ah.be.admin.hhmoperate.https;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.admin.hhmoperate.https.packet.HHMrevertPacket;
import com.ah.be.admin.hhmoperate.https.packet.HHMupdatePacket;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommTool;


public class Msg_client {
	
//send hhm revert
public static boolean sendHHMrevert(String Host, int port, String Query,String strDomainName)
{
    HttpsURLConnection conn = null;
    InputStream is = null;
    OutputStream os = null;
    
    try
    {
    	//construct data
		byte[] bBuffer = new byte[HHMConstant.Buffer_size];
		Arrays.fill(bBuffer, (byte)0);			
		
		//ssl connect
		URL surl = new URL("https",Host,port,Query);		
		
		//init https
		CommTool.initHttps();
		
		//build revert packet
		BeLogTools.commonLog(BeLogTools.INFO, "HHMmove: buildRevertPacket start...");
        int iPacketLength = HHMrevertPacket.buildRevertPacket(bBuffer, 0, strDomainName);
        BeLogTools.commonLog(BeLogTools.INFO, "HHMmove: buildRevertPacket end. Packet length: " + iPacketLength);
		
		if(0 == iPacketLength)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, "build hhm revert packet has error");
			
			return false;
		}
		
		conn = (HttpsURLConnection)surl.openConnection();			
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setConnectTimeout(HHMConstant.TIME_OUT);
		conn.setReadTimeout(HHMConstant.TIME_OUT);			
		
		os = conn.getOutputStream();			
		os.write(bBuffer,0,iPacketLength);
		os.flush();
		os.close();			
		conn.getResponseCode();
		
		//receive data			
		Arrays.fill(bBuffer, (byte)0);		
		is = conn.getInputStream();
		
		BufferedInputStream in  = new BufferedInputStream(is);
		
		//receive data
		in.read(bBuffer,0,HHMConstant.Buffer_size) ;			
		
		if(HHMConstant.Success_Response_FLag != bBuffer[0])
		{
		    //error ersponse add log
		    BeLogTools.commonLog(BeLogTools.ERROR, "receive error response");
		    return false;
		}
		
	    is.close();	
	    conn.disconnect();
			
	    return true;
    }
    catch(Exception ex)
    {
    	BeLogTools.commonLog(BeLogTools.ERROR, ex);		

		return false;
    }
    finally
	{
		try
		{
			if(null != os)
			{
				os.close();
			}
		
			if(null != is)
			{
				is.close();
			}
			
			if(null != conn)
			{
				conn.disconnect();
			}				
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(),ex);
		}			
	}
}
	
//send hhm update
public static boolean sendHHMupdate(String Host, int port, String Query, 
            HHMupdatePacketData oSenddata)
{
    HttpsURLConnection conn = null;
    InputStream is = null;
    OutputStream os = null;

    try
    {	
		//construct data
		byte[] bBuffer = new byte[HHMConstant.Buffer_size];
		Arrays.fill(bBuffer, (byte)0);			
		
		//ssl connect
		URL surl = new URL("https",Host,port,Query);		
		
		//init https
		CommTool.initHttps();
		
		int iPacketLength = HHMupdatePacket.buildupdatePacket(bBuffer, 0, oSenddata);
		
		if(0 == iPacketLength)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, "build hhm update packet has error");
			
			return false;
		}

		conn = (HttpsURLConnection)surl.openConnection();			
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(HHMConstant.TIME_OUT);
		conn.setReadTimeout(HHMConstant.HHM_update_Time_Out);			
		
		os = conn.getOutputStream();			
		os.write(bBuffer,0,iPacketLength);
		os.flush();
		os.close();			
		conn.getResponseCode();

		
		//receive data			
		Arrays.fill(bBuffer, (byte)0);		
		is = conn.getInputStream();
		
		BufferedInputStream in  = new BufferedInputStream(is);
		
		//receive data
		in.read(bBuffer,0,HHMConstant.Buffer_size) ;			
		
		if(HHMConstant.Success_Response_FLag != bBuffer[0])
		{
		    //error ersponse add log
		    BeLogTools.commonLog(BeLogTools.ERROR, "receive error response");
		    return false;
		}
		
	    is.close();	
	    conn.disconnect();
			
	    return true;
	}
	catch(Exception ex)
	{
		BeLogTools.commonLog(BeLogTools.ERROR, ex);		

		return false;
	}
	finally
	{
		try
		{
			if(null != os)
			{
				os.close();
			}
		
			if(null != is)
			{
				is.close();
			}
			
			if(null != conn)
			{
				conn.disconnect();
			}				
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage(),ex);
		}			
	}
}

}
