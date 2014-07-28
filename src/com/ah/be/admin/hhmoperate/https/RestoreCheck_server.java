package com.ah.be.admin.hhmoperate.https;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.admin.hhmoperate.https.packet.HHMupdatePacket;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class RestoreCheck_server extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException
	{
		doRequest(request,response);		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	            throws ServletException, IOException
	{
		doRequest(request,response);    	
	}
	
	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	{		
		//BeLogTools.commonLog(BeLogTools.INFO, "HHMmove: restore check start!");
		
		InputStream is = request.getInputStream();			
		
		byte[] bBuffer = new byte[HHMConstant.Buffer_size];
		Arrays.fill(bBuffer, (byte)0);
		byte[] bRecvBuffer = new byte[HHMConstant.Upload_Back_Data_Size];
	    Arrays.fill(bRecvBuffer, (byte)0);
	    
	    int iRecvDataLength = 0;
	    int iLength = 0;
	    
	    while((iRecvDataLength = is.read(bRecvBuffer,0,HHMConstant.Upload_Back_Data_Size)) != -1 )
	    {	
	    	System.arraycopy(bRecvBuffer, 0, bBuffer, iLength, iRecvDataLength);
	    	iLength += iRecvDataLength;
	    }
	    
		//if(is.read(bBuffer,0,HHMConstant.Buffer_size) != -1)
		//{
			HHMupdatePacketData oRecvData = new HHMupdatePacketData();
			if(0 == HHMupdatePacket.parseupdatePacket(bBuffer, 0, oRecvData))
	 	    {
	 	    	BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove: [RestoreCheck_server] parse packet header error!");
	 	    	CommTool.sendRestoreCheckResponse(response, CommConst.RESTORE_ERROR); 	    	
	 	    	return;
	 	    }
			
			File file = new File(oRecvData.getFilePath() + File.separator + oRecvData.getHHMDomain().getDomainName());
			File errorFile = new File(oRecvData.getFilePath() + File.separator + oRecvData.getHHMDomain().getDomainName() + "_error");
			if(file.exists() && !errorFile.exists()){
				CommTool.sendRestoreCheckResponse(response, CommConst.RESTORE_RUNNING);
			}
			if(errorFile.exists()){
				CommTool.sendRestoreCheckResponse(response, CommConst.RESTORE_ERROR);
			}
			if(!file.exists() && !errorFile.exists()){
				CommTool.sendRestoreCheckResponse(response, CommConst.RESTORE_FINISH);
			}
			
		//}
		is.close();
	}
}
