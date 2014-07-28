package com.ah.be.admin.hhmoperate;

import java.util.List;

import com.ah.be.admin.QueueOperation.HHMUpdateStatusItem;
import com.ah.be.admin.hhmoperate.https.Msg_client;
import com.ah.be.admin.hhmoperate.https.RestoreCheck_client;
import com.ah.be.admin.hhmoperate.https.Upload_client;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommConst;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class HHMupdate {
	
	public static UpdateInfo updateHHM(HmDomain oDomain, int iContent,HhmUpgradeVersionInfo oUpdateInfo,HHMUpdateStatusItem oStatus)
	{
		UpdateInfo oReturnInfo = new UpdateInfo(); 
		
		//HHMoperate.setStatus(HHMoperate.Update_Status_Backup_Data);
		oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_Backup_Data);
		
		//backup data
		BackupInfo oBackupInfo = HHMbackup.backupHHM(oDomain, iContent, true);
		
		if(!oBackupInfo.getResult())
		{
			oReturnInfo.setResult(oBackupInfo.getResult());
			oReturnInfo.setErrorMsg(oBackupInfo.getErrorMsg());
			return oReturnInfo;
		}
		
		//HHMoperate.setStatus(HHMoperate.Update_Status_Move_Data);
		oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_Move_Data);
		
		//tranmit backup data
		if(!Upload_client.uploadFile(oUpdateInfo.getIpAddress(), HHMoperate.Https_port, HHMoperate.Https_Upload_Query, oBackupInfo.getFilePath(), oBackupInfo.getFileName()))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when upload backup file!");
			return oReturnInfo;
		}
		
		HHMupdatePacketData oSendData = new HHMupdatePacketData();
		oSendData.setFilePath(oBackupInfo.getFilePath());
		oSendData.setFileName(oBackupInfo.getFileName());
		oSendData.setHHMDomain(oDomain);
		oSendData.setHHMFlag(HHMConstant.HHM_Fix_Flag);
		oSendData.setHHMVersion(NmsUtil.getVersionInfo().getMainVersion());
		oSendData.setPackType(HHMConstant.Packet_Type_HHM_Update);
		oSendData.setProtocolVersion(HHMConstant.Packet_protocol_version);
		
		//HHMoperate.setStatus(HHMoperate.Update_Status_Restore_Data);
		oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_Restore_Data);
		
		//send hhm update
		if(!Msg_client.sendHHMupdate(oUpdateInfo.getIpAddress(), HHMoperate.Https_port, HHMoperate.Https_Message_Query, oSendData))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
			return oReturnInfo;
		}
		
		byte restoreSt = RestoreCheck_client.checkRestore(oUpdateInfo.getIpAddress(), HHMoperate.Https_port, HHMoperate.Https_Restore_Query, oSendData);
		while(CommConst.RESTORE_RUNNING == restoreSt){
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove: check restore thead return 1, restore is running!");
				oReturnInfo.setResult(false);
				oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
				return oReturnInfo;
			}
			
			restoreSt = RestoreCheck_client.checkRestore(oUpdateInfo.getIpAddress(), HHMoperate.Https_port, HHMoperate.Https_Restore_Query, oSendData);
		}
		
		if(CommConst.RESTORE_ERROR == restoreSt){
			BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove: check restore thead return 3, some error happened!");
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
			return oReturnInfo;
		}
		
		//add the record or update record to HM version info 
		//change the status to need confirm
		HMUpdateSoftwareInfo oInfo = new HMUpdateSoftwareInfo();
		oInfo.setDomainName(oDomain.getDomainName());
		oInfo.setHmVersion(oUpdateInfo.getHmVersion());
		oInfo.setIpAddress(oUpdateInfo.getIpAddress());
		oInfo.setStatus(HMUpdateSoftwareInfo.STATUS_NEEDCONFIRM);
		oInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);
		
		oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_Change);
		
		if(!recordHHMInfo(oInfo))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when changing version status!");
			oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_No_operation);
			return oReturnInfo;
		}
		
		oStatus.setUpdateStatus(HHMUpdateStatusItem.Update_Status_No_operation);
		
		oReturnInfo.setResult(true);
		
		return oReturnInfo;		
	}
	
	public static boolean recordHHMInfo(HMUpdateSoftwareInfo oInfo)
	{
		String where = "domainName = :s1 AND status != :s2";
		Object[] values = new Object[2]; 
		values[0] = oInfo.getDomainName();
		values[1] = HMUpdateSoftwareInfo.STATUS_ACTIVE;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(infoList.isEmpty())
		{
			//add a data		
			try
			{
				QueryUtil.createBo(oInfo);
			}
			catch(Exception ex)
			{
				//add log
				BeLogTools.commonLog(BeLogTools.ERROR, "add version info failed to HMUpdateSoftwareInfo");
				return false;
			}
		}
		else
		{
			//update the first data
			HMUpdateSoftwareInfo oTmp = infoList.get(0);
			oTmp.setDomainName(oInfo.getDomainName());
			oTmp.setHmVersion(oInfo.getHmVersion());
			oTmp.setIpAddress(oInfo.getIpAddress());
			oTmp.setStatus(oInfo.getStatus());
			oTmp.setApSwithStatus(oInfo.isApSwithStatus());
			
			try
			{
				QueryUtil.updateBo(oTmp);
			}
			catch(Exception ex)
			{
				//add log
				BeLogTools.commonLog(BeLogTools.ERROR, "update version info failed to HMUpdateSoftwareInfo. the id is:"+oTmp.getId());
				return false;
			}			
		}
		
		return true;		
	}
	
	public static boolean changeNeedApSwithStatus(String strDomainName, String strIP)
	{
		String where = "domainName = :s1 AND ipAddress = :s2";
		Object[] values = new Object[2]; 
		values[0] = strDomainName;
		values[1] = strIP;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(infoList.isEmpty())
		{
			return false;
		}
		
		HMUpdateSoftwareInfo oTmp = infoList.get(0);
		oTmp.setApSwithStatus(HMUpdateSoftwareInfo.NEED_AP_SWITH);
		
		try
		{
			QueryUtil.updateBo(oTmp);
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, "update version info failed to HMUpdateSoftwareInfo. the id is:"+oTmp.getId());
			return false;
		}	
		
		return true;
	}
	
	public static boolean changeNotNeedApSwitchStatus(String strDomainName)
	{
		String where = "domainName = :s1 AND apSwithStatus = :s2";
		Object[] values = new Object[2]; 
		values[0] = strDomainName;
		values[1] = HMUpdateSoftwareInfo.NEED_AP_SWITH;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(infoList.isEmpty())
		{
			return true;
		}

		for (HMUpdateSoftwareInfo oTmp : infoList) {
			oTmp.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);

			try {
				QueryUtil.updateBo(oTmp);
			}
			catch (Exception ex) {
				//add log
				BeLogTools.commonLog(BeLogTools.ERROR, "update version info failed to HMUpdateSoftwareInfo. the id is:" + oTmp.getId());
			}
		}
		
		return true;
	}

}