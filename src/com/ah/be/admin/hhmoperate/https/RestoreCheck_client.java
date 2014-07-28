package com.ah.be.admin.hhmoperate.https;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServlet;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.admin.hhmoperate.https.packet.HHMupdatePacket;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class RestoreCheck_client extends HttpServlet {
	public static byte checkRestore(String Host, int port, String Query, 
            HHMupdatePacketData oSenddata){
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
				
				return CommConst.RESTORE_ERROR;
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
			
			is.close();	
		    conn.disconnect();
		    
			return bBuffer[0];
			/*if(HHMConstant.Success_Response_FLag != bBuffer[0])
			{
			    //error ersponse add log
			    BeLogTools.commonLog(BeLogTools.ERROR, "receive error response");
			    return false;
			}*/
		    
		}
		catch(Exception ex)
		{
			BeLogTools.commonLog(BeLogTools.ERROR, ex);		

			return CommConst.RESTORE_ERROR;
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
