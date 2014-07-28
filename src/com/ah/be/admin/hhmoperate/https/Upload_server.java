package com.ah.be.admin.hhmoperate.https;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadHeadData;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadResponseData;
import com.ah.be.admin.hhmoperate.https.packet.HHMuploadHead;
import com.ah.be.admin.hhmoperate.https.packet.HHMuploadResponse;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommTool;
import com.ah.be.os.FileManager;
import com.ah.util.fileupload.FileUploadUtil;



public class Upload_server extends HttpServlet  {

	private static final long serialVersionUID = 1L;

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
		InputStream is = request.getInputStream();	
        OutputStream os = response.getOutputStream();
	
	    byte[] bBuffer = new byte[HHMConstant.Upload_Back_Data_Size];
	    byte[] bRecvBuffer = new byte[HHMConstant.Upload_Back_Data_Size];
	    Arrays.fill(bBuffer, (byte)0);
	    Arrays.fill(bRecvBuffer, (byte)0);
	    
	    int iRecvDataLength = 0;
	    int iLength = 0;
	    
	    while((iRecvDataLength = is.read(bRecvBuffer,0,HHMConstant.Upload_Back_Data_Size)) != -1 )
	    {	
	    	System.arraycopy(bRecvBuffer, 0, bBuffer, iLength, iRecvDataLength);
	    	
	    	iLength += iRecvDataLength;
	    }
	    //recv data
	    //int iRecvDataLength = is.read(bBuffer,0,HHMConstant.Upload_Back_Data_Size);
	    
	    //parse packet
	    HHMuploadHeadData oRecvData = new HHMuploadHeadData();
	    int iHeadLength = HHMuploadHead.parseHHMuploadHead(bBuffer, 0, oRecvData);
	    
	    if(0 == iHeadLength)
	    {
	    	CommTool.sendErrorResponse(response);	    	
	    	BeLogTools.commonLog(BeLogTools.ERROR, "Parse upload head has some error!");	    	
	    	return;
	    	
	    }	   
	    
	    if(iLength != (oRecvData.getDataLength()+iHeadLength))
	    {
	    	//add log
	    	CommTool.sendErrorResponse(response);
	    	BeLogTools.commonLog(BeLogTools.ERROR, "The recv Data not right");
 			return;
	    }	    
	    
	    /*
	     * fix PSIRT Security Issue CFD-384
	     * 
	     * Servlet with URL /hm/hhmuploadserver is for VHM backup file uploading which triggered by VHM move/upgrade operation
	     * 
	     * to fix this issue, two validations were added since Guadalupe release (6.1r6):
	     * 1. file path cannot contain string '../' (avoid path traversal attack)
	     * 2. add file path validation. normally the VHM backup file is under path '/tmp/backup'(HHMConstant.BACKUP_DOWNLOAD_HOME),
	     * 3. add file type validation. normally the backup file is end with '.tar.gz'
	     */
	    String filePath = oRecvData.getFilePath()+"/"+oRecvData.getFileName();
	    if (StringUtils.isEmpty(filePath) || FileUploadUtil.isPathTraversal(filePath)) {
	    	CommTool.sendErrorResponse(response);
	    	BeLogTools.commonLog(BeLogTools.ERROR, "Upload path cannot contain string '../'");
 			return;
	    }
	    if (!StringUtils.startsWithIgnoreCase(filePath, HHMConstant.BACKUP_DOWNLOAD_HOME)) {
	    	CommTool.sendErrorResponse(response);
	    	BeLogTools.commonLog(BeLogTools.ERROR, "Upload path must start with /tmp/backup");
 			return;
	    }
	    if (!StringUtils.isEmpty(filePath) && FileUploadUtil.containsNotAcceptableExtension(filePath)) {
	    	CommTool.sendErrorResponse(response);
	    	BeLogTools.commonLog(BeLogTools.ERROR, "Upload file extension cannot be .jsp/.cgi/.pl/.py/.rb/.asp");
 			return;
	    }
	    
	    File  oUploadFile = new File(oRecvData.getFilePath()+"/"+oRecvData.getFileName());
	    
	    File oStatusFile = new File(oRecvData.getFilePath()+"/"+HHMConstant.Upload_Static_Status_Name);
	    
	    RandomAccessFile oReadeStatus = null;	
	    int iStatus;
	    long lOffset;
	    
	    try
	    {
	    switch(oRecvData.getSendStatus())
	    {
	        case HHMConstant.Upload_Send_Status_Begin:	        	
	        	//clean backup tmp dir		
	    		File oFile = new File(oRecvData.getFilePath());
	    		String strBackupHome = oFile.getParent();
	    		
	    		if(null != strBackupHome)
	    		{
	    			oFile = new File(strBackupHome);
	    			
	    			try
	    			{
	    				if(oFile.exists())
	    				{
	    					if(oFile.isDirectory())
	    					{
	    						FileManager.getInstance().deleteDirectory(strBackupHome);
	    					}
	    					else
	    					{
	    						FileManager.getInstance().deletefile(strBackupHome);
	    					}
	    				}
	    			}
	    			catch(Exception ex)
	    			{
	    				BeLogTools.commonLog(BeLogTools.ERROR, ex);
	    			}
	    		}		
	        	
	        	//write the status file
                File oUploadDir = new File(oRecvData.getFilePath());
		    	
//		    	if(oUploadDir.exists())
//		    	{
//		    	    if(oUploadDir.isDirectory())
//		    	    {
//		    	    	//deleteDir(oUploadDir.getAbsolutePath());
//		    	    	FileManager.getInstance().deleteDirectory(oUploadDir.getAbsolutePath());
//		    	    }
//		    	    else
//		    	    {
//		    	    	oUploadDir.delete();
//		    	    }
//		    	}
		    	
		    	oUploadDir.mkdirs();
		    	oReadeStatus = new RandomAccessFile(oStatusFile, "rw");
		    	
		    	oReadeStatus.setLength(0);
	     		oReadeStatus.writeBytes("status="+oRecvData.getSendStatus());
	     		oReadeStatus.writeBytes("\r\n");
	     		oReadeStatus.writeBytes("offset=" + 0);
	     		oReadeStatus.writeBytes("\r\n");
	     		oReadeStatus.close();
		    	
	     		iStatus = oRecvData.getSendStatus();
	     		lOffset = 0;
	     		
	        	break;
	        case HHMConstant.Upload_Send_Status_Continue:
	        case HHMConstant.Upload_Send_Status_End:
	        	
	        	//only do some check
	        	if(!oUploadFile.exists() || !oStatusFile.exists())
		    	{
		    		CommTool.sendErrorResponse(response);
		    		BeLogTools.commonLog(BeLogTools.ERROR, "the recv file not exist.");
		    		return ;
		    	}
	            //read the status file
                oReadeStatus = new RandomAccessFile(oStatusFile, "rw");
		    	
		    	String strContent = oReadeStatus.readLine();
				
				if(null == strContent)
				{
					//add log
		 			CommTool.sendErrorResponse(response);
		 			BeLogTools.commonLog(BeLogTools.ERROR, "could not get upload status");
		 			return;
				}
				
				iStatus = Integer.parseInt(CommTool.subString(strContent, "="));
				
				strContent = oReadeStatus.readLine();
				
				if(null == strContent)
				{
					//add log
		 			CommTool.sendErrorResponse(response);
		 			BeLogTools.commonLog(BeLogTools.ERROR, "could not get upload offset");
		 			return;
				}
				
			    lOffset = Long.parseLong(CommTool.subString(strContent, "=")); 
	        	
	        	//modify the status file.
			    oReadeStatus.setLength(0);
	     		oReadeStatus.writeBytes("status="+oRecvData.getSendStatus());
	     		oReadeStatus.writeBytes("\r\n");
	     		oReadeStatus.writeBytes("offset=" + lOffset);
	     		oReadeStatus.writeBytes("\r\n");
	     		oReadeStatus.close();
	     		
	     		iStatus = oRecvData.getSendStatus();
			    
	        	break;
	        default :
	        	CommTool.sendErrorResponse(response);
 			    return;	        	
	    }
	    
	    //build response
	    HHMuploadResponseData oSendData = new HHMuploadResponseData();	   
	    oSendData.setPacketType(HHMConstant.Packet_Type_Upload_backup_Response);
	    oSendData.setProtocolVersion(HHMConstant.Packet_protocol_version);
	    
	      
	    //if upload status end. send the response.
        //local offset is not equal with the remote offset	    
	    //send response
	    if(HHMConstant.Upload_Send_Status_End == iStatus || lOffset != oRecvData.getOffset())
	    {
	    	oSendData.setSendStatus((byte)(iStatus & 0xFF));
	    	oSendData.setOffset(lOffset);
	    	Arrays.fill(bBuffer, (byte)0);
	    	int iPacklength = HHMuploadResponse.buildHHMuploadResponse(bBuffer, 0, oSendData);
	    	os.write(bBuffer,0,iPacklength);
			os.flush();
			os.close();				
			//do the ye wu response
			return;			
	    }
	    
	    //if both offset equal then write file
	    RandomAccessFile oUploadAccess = new RandomAccessFile(oUploadFile, "rw");
    
	    oUploadAccess.seek(lOffset);	    
	    oUploadAccess.write(bBuffer, iHeadLength, oRecvData.getDataLength());
	    oUploadAccess.close();
	    
	    oSendData.setOffset(lOffset+oRecvData.getDataLength());
	    
	    oSendData.setSendStatus(HHMConstant.Upload_Send_Status_Continue);   
	    
	  
	    //rewrite the status file
	    oReadeStatus = new RandomAccessFile(oStatusFile, "rw");
	    
	    oReadeStatus.setLength(0);
 		oReadeStatus.writeBytes("status="+oSendData.getSendStatus());
 		oReadeStatus.writeBytes("\r\n");
 		oReadeStatus.writeBytes("offset=" + oSendData.getOffset());
 		oReadeStatus.writeBytes("\r\n");
 		oReadeStatus.close();    
	    
	    //send response
	    
 		Arrays.fill(bBuffer, (byte)0);
 		int iPacklength = HHMuploadResponse.buildHHMuploadResponse(bBuffer, 0, oSendData);
    	os.write(bBuffer,0,iPacklength);
		os.flush();
		os.close();	
		
		return;
	    }
	    catch(Exception ex)
	    {
	    	//add log
	    	CommTool.sendErrorResponse(response);
 			return;
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
				
				if(null != oReadeStatus)
				{
					oReadeStatus.close();
				}			
				
			}
			catch(Exception ex)
			{
				//add log
				CommTool.sendErrorResponse(response);
	 			return;
			}		
	    }
	    
	}
}
