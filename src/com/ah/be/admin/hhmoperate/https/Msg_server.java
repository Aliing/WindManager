package com.ah.be.admin.hhmoperate.https;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;

import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.hhmoperate.HHMupdate;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.admin.hhmoperate.https.packet.HHMrevertPacket;
import com.ah.be.admin.hhmoperate.https.packet.HHMupdatePacket;
import com.ah.be.app.AhAppContainer;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.ls.util.CommTool;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;

public class Msg_server extends HttpServlet {

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
	    	switch(bBuffer[0])
	    	{
	    	    case HHMConstant.Packet_Type_HHM_Update:
	    	    	dealUpatepacket(bBuffer,response,CommTool.getClientIp(request),request);
	    	    	break;
	    	    case HHMConstant.Packet_Type_HHM_Revert:
	    	    	dealRevertpacket(bBuffer,response,CommTool.getClientIp(request));
	    	    	break;
	    	    default:
	    	    	CommTool.sendErrorResponse(response); 	
	    	    	break;
	    	}	 	  			
	    //}  	    
	    
	    is.close(); 	
	}
	
	//deal hhm revert request
	private void dealRevertpacket(byte[] bBuffer, HttpServletResponse response, String strClientIp)
	{
		//parse the packet
		String strDomainName = HHMrevertPacket.parseRevertPacket(bBuffer, 0);
		
		if(null == strDomainName || "".equals(strDomainName))
		{
			BeLogTools.error(HmLogConst.M_COMMON,"parse revert packet error!");
 	    	CommTool.sendErrorResponse(response); 	    	
 	    	return;
		}
		
		//search domain
		HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName",
				strDomainName);
		
		if(null == bo)
		{
			BeLogTools.error(HmLogConst.M_COMMON,"could not find revert domain, the name is:"+strDomainName);
			CommTool.sendErrorResponse(response); 	    	
 	    	return;
		}
		
		//enable hm
		BoMgmt.getDomainMgmt().enableDomain(strDomainName);
		
		//change standby switchAP status
		//change db and memory
		if(HHMupdate.changeNotNeedApSwitchStatus(strDomainName))
		{
			APSwitchCenter deviceSwitchCenter = AhAppContainer.getBeAdminModule().getDeviceSwitchCenter();
			deviceSwitchCenter.removeSwitchInfo(strDomainName);
			
			HHMConstant.sendSuessResponse(response);
			
			return;
		}
		
		CommTool.sendErrorResponse(response);
	}
	
	//deal hhm update request
	private void dealUpatepacket(byte[] bBuffer, HttpServletResponse response, String strClientIp, HttpServletRequest request)
	{
        HHMupdatePacketData oRecvData = new HHMupdatePacketData();
 	    if(0 == HHMupdatePacket.parseupdatePacket(bBuffer, 0, oRecvData))
 	    {
 	    	BeLogTools.error(HmLogConst.M_COMMON,"parse packet header error!");
 	    	CommTool.sendErrorResponse(response); 	    	
 	    	return;
 	    }	 	    
 	    
 	    File folder = new File(oRecvData.getFilePath() );
 	    if(!folder.isDirectory()){
 	    	BeLogTools.error(HmLogConst.M_COMMON,"HHMmove: restore failed, cannot found backup folder!");
			CommTool.sendErrorResponse(response);
			return;
 	    }
 	    
 	    //create tag file for restore
 	    File file = new File(oRecvData.getFilePath() + File.separator + oRecvData.getHHMDomain().getDomainName());
		File errorFile = new File(oRecvData.getFilePath() + File.separator + oRecvData.getHHMDomain().getDomainName() + "_error");
		if(file.exists()){
			BeLogTools.warn(HmLogConst.M_COMMON,"HHMrestore: another restore theard is running for this vhm(" + oRecvData.getHHMDomain().getDomainName() + 
					"), this request will been given up!");
			while(file.exists()){
				try {
					BeLogTools.debug(HmLogConst.M_COMMON,"HHMmove: 2cd thread waiting...");
					Thread.currentThread().sleep(5000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
			if(!errorFile.exists()){
				BeLogTools.debug(HmLogConst.M_COMMON,"HHMmove: 2cd thread return success!");
				HHMConstant.sendSuessResponse(response);
				return ;
			}else{
				BeLogTools.debug(HmLogConst.M_COMMON,"HHMmove: 2cd thread return error!");
				CommTool.sendErrorResponse(response);
				return;
			}
		}
		
		// create tag file
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				BeLogTools.error(HmLogConst.M_COMMON,"HHMmove: create tag file fail, file path: " + file.getPath());
				HHMConstant.sendSuessResponse(response);
			}
		}
		
		BeLogTools.debug(HmLogConst.M_COMMON,"HHMmove: restore begin...");
		BeLogTools.debug(HmLogConst.M_COMMON,"clientIP: " + strClientIp + ", URL: " + request.getRequestURL());
		HHMConstant.sendSuessResponse(response);
 		//do the operate
 	    if(!doHHMupdate(oRecvData,strClientIp))
 	    {
 	    	BeLogTools.error(HmLogConst.M_COMMON,"HHMmove: HHM update failed on server!");
 	    	return;
 	    }
 	    BeLogTools.debug(HmLogConst.M_COMMON, "HHMmove: restore end.");  
	}
	
	private boolean doHHMupdate(HHMupdatePacketData oData, String strIP)
	{
		File file = new File(oData.getFilePath() + File.separator + oData.getHHMDomain().getDomainName());
		File errorFile = new File(oData.getFilePath() + File.separator + oData.getHHMDomain().getDomainName() + "_error");

		//create domain
		BeLogTools.debug(HmLogConst.M_COMMON, "HHMmove: delete and create vhm.");
		long lid = dealDomain(oData.getHHMDomain());
		
		if(0 == lid)
		{
			BeLogTools.error(HmLogConst.M_COMMON,"HHMmove: dealDomain failed, return error!");
			file.delete();
			if(!errorFile.exists()){
				try {
					errorFile.createNewFile();
				} catch (IOException e) {
					return false;
				}
				
			}
			return false;
		}
		
		//do restore
		HmDomain bo = QueryUtil.findBoById(HmDomain.class, lid);
		
		if(null == bo)
		{
			//add log
			BeLogTools.error(HmLogConst.M_COMMON,"HHMmove: could not find HmDomain by id= "+lid);
			file.delete();
			if(!errorFile.exists()){
				try {
					errorFile.createNewFile();
				} catch (IOException e) {
					return false;
				}
				
			}
			return false;
		}
		
	
		BeLogTools.debug(HmLogConst.M_COMMON, "HHMrestore: restore data of " + bo.getDomainName());
		RestoreInfo oRestoreInfo =  HHMoperate.restoreOperation(bo, oData.getFilePath(), oData.getFileName());
		
		if(!oRestoreInfo.getResult())
		{
			file.delete();
			if(!errorFile.exists()){
				try {
					errorFile.createNewFile();
				} catch (IOException e) {
					return false;
				}
				
			}
		    //add log
			BeLogTools.error(HmLogConst.M_COMMON,  oRestoreInfo.getErrorMsg());
			return false;
		}
		
		//remember the version
		HMUpdateSoftwareInfo oInfo = new HMUpdateSoftwareInfo();
		oInfo.setDomainName(oData.getHHMDomain().getDomainName());
		oInfo.setHmVersion(oData.getHHMVersion());
		oInfo.setIpAddress(strIP);
		oInfo.setStatus(HMUpdateSoftwareInfo.STATUS_STANDBY);
		oInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);
		
		HHMupdate.recordHHMInfo(oInfo);
		
		BeLogTools.debug(HmLogConst.M_COMMON,  "HHMmove: restore complete and return success!");
		//delete restore tag file
		file.delete();
		
		//enable vhm
		//because all vhm default is enable, so 
		
		return true;
	}
	
	private long dealDomain(HmDomain oDomain)
	{
		HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", oDomain.getDomainName());
		
		if(null != bo)
		{
			try
			{
				BeLogTools.debug(HmLogConst.M_COMMON,  "HHMrestore: (Msg_server-dealDomain) remove vhm '" + bo.getDomainName() + "'!");
				BoMgmt.getDomainMgmt().removeDomain(bo.getId(), true);
			}catch(Exception ex)
		    {
		    	//add log
				BeLogTools.error(HmLogConst.M_COMMON,  ex);
		    	return 0;
		    }
		}		
		
		try
		{
			// handle owner user field
//			if (oDomain.getOwnerUser() != null) {
//				HmUser ownerUser = QueryUtil.findBoByAttribute(HmUser.class, "userName", oDomain
//						.getOwnerUser().getUserName(), BoMgmt.getDomainMgmt().getHomeDomain()
//						.getId());
//				if (ownerUser == null) {
//					BeLogTools.commonLog(BeLogTools.WARN,
//							"Msg_server.dealDomain() Cannot find owner user of domain, user name is "
//									+ oDomain.getOwnerUser().getUserName() + ", domain is "
//									+ oDomain.getDomainName());
//				}
//				oDomain.setOwnerUser(ownerUser);
//			}
			
			//create bo	
			BeLogTools.debug(HmLogConst.M_COMMON,  "HHMrestore: (Msg_server-dealDomain) create new vhm '" + oDomain.getDomainName() + "'!");
			return BoMgmt.getDomainMgmt().createDomain(oDomain);
		}
	    catch(Exception ex)
	    {
	    	//add log
	    	BeLogTools.error(HmLogConst.M_COMMON, ex);
	    	return 0;
	    }		
	}

}