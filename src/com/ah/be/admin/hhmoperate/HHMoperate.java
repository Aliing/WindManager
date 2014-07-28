package com.ah.be.admin.hhmoperate;

import java.util.Collection;
import java.util.List;

import com.ah.be.admin.hhmoperate.https.Msg_client;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.be.log.BeLogTools;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class HHMoperate {
	
	//public static final int Update_Status_No_operation     = 0;
	//public static final int Update_Status_Backup_Data      = 1;
	//public static final int Update_Status_Move_Data        = 2;
	//public static final int Update_Status_Restore_Data     = 3;
	//public static final int Update_Status_Change           = 4;
	
	public static final int Https_port                     = 443;
	public static final String Https_Upload_Query          = "/hm/hhmuploadserver";
	public static final String Https_Message_Query         = "/hm/hhmmessageserver";
	public static final String Https_Restore_Query         = "/hm/hhmrestorecheck";
	
	public static final int HHM_UPDATE = 1;
	public static final int HHM_MV     = 2;
	
	//the status of hhm update
	//private static int update_status = Update_Status_No_operation;
	
//	public static void setStatus(int iStatus)
//	{
//		update_status = iStatus;
//	}
	
//	public static int getStatus()
//	{
//		return update_status;
//	}
	
	public static HmDomain updateDomainStatus(long lDomainId, int iStatus)
	{
		HmDomain oBo = QueryUtil.findBoById(HmDomain.class, lDomainId);
		
		if(null == oBo)
		{
			DebugUtil.adminDebugWarn("HHMoperate::updateDomainStatus, " +
					"Get hmdomain by id is null.");
			return null;
		}
		
		oBo.setRunStatus(iStatus);
		
		try
		{
//			oBo = (HmDomain) QueryUtil.updateBo(oBo);
//			
//			CacheMgmt.getInstance().updateHmDomainCache(oBo);
			BoMgmt.getDomainMgmt().updateDomain(oBo);
			
			return oBo;
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugWarn("HHMoperate::updateDomainStatus, " + ex.getMessage());
			
	        return null;
		}
	}
	
	public static int getDomainStatus(Long lDomainId)
	{
		HmDomain oBo = CacheMgmt.getInstance().getCacheDomainById(lDomainId);
		
		if(null == oBo)
		{
			return HmDomain.DOMAIN_UNKNOWN_STATUS;
		}
		
		return oBo.getRunStatus();
	}
	
	public static void changeAfterRestoreDomain(Long lDomainId, String strDomainName)
	{
		try {
			BoMgmt.getHiveApMgmt().resetConnectStatusViaCAPWAP(lDomainId);
		} catch (RuntimeException e1) {
			DebugUtil.adminDebugError(
					"reset CAPWAP Status of HiveAP on domain:"
							+ strDomainName + " error.", e1);
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugError(
					"reset CAPWAP Status of HiveAP on domain:"
							+ strDomainName + " error.", ex);
		}

		try {
			BoMgmt.getTrapMgmt().setCapwapAlarm2LinkDownByDomain(
					lDomainId);
		} catch (RuntimeException e) {
			DebugUtil.adminDebugError(
					"reset Alarm link up status of HiveAP on domain:"
							+ strDomainName + " error.", e);
		}
		
		// re-initialize the Cache values;
		CacheMgmt.getInstance().initCacheValues(lDomainId);
		// initialize home domain caches
		if (!HmDomain.HOME_DOMAIN.equals(strDomainName)) {
			CacheMgmt.getInstance().initCacheValues(
					BoMgmt.getDomainMgmt().getHomeDomain().getId());
		}
		// Initialize map hierarchy cache
		BoMgmt.getMapHierarchyCache().init();
		// Send HiveAp DTLS Parameters configuration
		BeTopoModuleUtil.sendHiveApDTLSParamConfig(lDomainId);
	}
	
    
	public static BackupInfo backupOperation(HmDomain oDomain, int iContent, boolean isNeedLicense)
	{
		BackupInfo oReturnInfo = new BackupInfo(); 
		
		//get status & charge status
		int iStatus = getDomainStatus(oDomain.getId());
		
		if(HmDomain.DOMAIN_BACKUP_STATUS == iStatus 
				|| HmDomain.DOMAIN_RESTORE_STATUS == iStatus
				|| HmDomain.DOMAIN_UPDATE_STATUS == iStatus)
		{
			oReturnInfo.setResult(false);
			String strErrMsg;
			
			switch(iStatus)
			{			
			    case HmDomain.DOMAIN_BACKUP_STATUS:
			    	strErrMsg = "Another administrator is currently backing up the HiveManager software. " +
			    			"Please try again later if necessary.";
			    	oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_RESTORE_STATUS:
			    	strErrMsg = "Another administrator is currently restoring the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_UPDATE_STATUS:
			    	strErrMsg = "Another administrator is currently updating the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			    	break;
			}
			
			return oReturnInfo;
		}		
		
		//set backup status
		if(null == updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_BACKUP_STATUS))
		{
			oReturnInfo.setResult(false);
			String strErrmsg = "An error occurred while changing the status of VHM";
			oReturnInfo.setErrorMsg(strErrmsg);
			return oReturnInfo;
		}
		
		//add the count
		CurrentLoadCache.getInstance().increaseNumberOfBackupRequest();
		CurrentLoadCache.getInstance().increaseNumberOfBackupRunning();
		
		
		//do backup
		oReturnInfo = HHMbackup.backupHHM(oDomain, iContent, isNeedLicense);		
		
		//set default status
		updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
		
		//decrease
		CurrentLoadCache.getInstance().decreaseNumberOfBackupRequestWithRunning();
		
		return oReturnInfo;
	}
	
	public static RestoreInfo restoreOperation(HmDomain oDomain, String strPath, String strFileName)
	{
		RestoreInfo oReturnInfo = new RestoreInfo();
		
	    //get status and charge	
       int iStatus = getDomainStatus(oDomain.getId());
		
		if(HmDomain.DOMAIN_BACKUP_STATUS == iStatus 
				|| HmDomain.DOMAIN_RESTORE_STATUS == iStatus
				|| HmDomain.DOMAIN_UPDATE_STATUS == iStatus)
		{
			oReturnInfo.setResult(false);
			String strErrMsg;
			
			switch(iStatus)
			{			
			    case HmDomain.DOMAIN_BACKUP_STATUS:
			    	strErrMsg = "Another administrator is currently backing up the HiveManager software. " +
			    			"Please try again later if necessary.";
			    	oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_RESTORE_STATUS:
			    	strErrMsg = "Another administrator is currently restoring the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_UPDATE_STATUS:
			    	strErrMsg = "Another administrator is currently updating the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			    	break;
			}
			
			return oReturnInfo;
		}
		
		//set restore status
		if(null == updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_RESTORE_STATUS))
		{
			oReturnInfo.setResult(false);
			String strErrmsg = "An error occurred while chaning the status of VHM";
			oReturnInfo.setErrorMsg(strErrmsg);
			updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
			return oReturnInfo;
		}
		
		//add the restore num
		//decrease
		CurrentLoadCache.getInstance().increaseNumberOfRestoreRequest();
		CurrentLoadCache.getInstance().increaseNumberOfRestoreRunning();
		
		//do restore
		String retMessage = HHMrestore.restoreData(strPath, strFileName, oDomain);
		if(!retMessage.equals(""))
		{
			oReturnInfo.setResult(false);
//			String strErrmsg = "An error occurred while restore db data";
			oReturnInfo.setErrorMsg(retMessage);
			
			updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
			
			CurrentLoadCache.getInstance().decreaseNumberOfRestoreRequestWithRunning();
			return oReturnInfo;
		}
		
		//do other changes 
		changeAfterRestoreDomain(oDomain.getId(),oDomain.getDomainName());

		oReturnInfo.setResult(true);
		
		//set default  status
		updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
		
		CurrentLoadCache.getInstance().decreaseNumberOfRestoreRequestWithRunning();
		return oReturnInfo;		
	}
	
	
	public static boolean revertOperation(HMUpdateSoftwareInfo oUpdateInfo)
	{
		//send to the peer side not switch ap
//		if(!Msg_client.sendHHMrevert(oUpdateInfo.getIpAddress(), HHMoperate.Https_port, HHMoperate.Https_Message_Query, oUpdateInfo.getDomainName()))
//		{
//			//add some log
//			DebugUtil.adminDebugError("Send to "+oUpdateInfo.getIpAddress()+" have some error. when do revert operation");
//			return false;
//		}
//		
//		String where = "domainName = :s1 AND ipAddress = :s2";
//		Object[] values = new Object[2]; 
//		values[0] = oUpdateInfo.getDomainName();
//		values[1] = oUpdateInfo.getIpAddress();
//		
//		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
//				null, new FilterParams(where,values));
//		
//		if(infoList.isEmpty())
//		{
//			return false;
//		}		
//		//change flag to need dns	
//		HMUpdateSoftwareInfo oInfo = infoList.get(0);
//		oInfo.setStatus(HMUpdateSoftwareInfo.STATUS_UPDATEDNS);
//		oInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);
//		
//		if(!HHMupdate.recordHHMInfo(oInfo))
//		{
//			//add some log
//			DebugUtil.adminDebugError("there has some error, when update change DNS");
//			return false;
//		}		
//		
//		return true;
		
		return revertOperation_2(oUpdateInfo.getDomainName(),oUpdateInfo.getHmVersion(),oUpdateInfo.getIpAddress());
	}
	
	/*
	public static UpdateInfo updatePlanner2HM(HmDomain domain) {
		Map<String, HhmUpgradeVersionInfo>	hmVersions = ConfigUtil.getUpgradeInfoForVHM(domain.getDomainName());
		if (hmVersions == null || hmVersions.size() == 0) {
			UpdateInfo info = new UpdateInfo();
			info.setResult(false);
			info.setErrorMsg("There is no target server.");
			return info;
		}
		
		HhmUpgradeVersionInfo target = null;
		for (String key : hmVersions.keySet()) {
			target = hmVersions.get(key);
			break;
		}

		// update planner default user to config user group at first.
		try {
			HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
					domain.getId());
			HmUserGroup configGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.CONFIG,
					domain.getId());
			defaultUser.setUserGroup(configGroup);
			
			QueryUtil.updateBo(defaultUser);
		} catch (Exception e) {
			DebugUtil.commonDebugError("updatePlanner2HM", e);
			UpdateInfo info = new UpdateInfo();
			info.setResult(false);
			info.setErrorMsg("Cannot upgrade planner account to normal HiveManager account.");
			return info;
		}
		
		
		UpdateInfo response = HHMoperate.updateOperation(domain,
				AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP, target);
		
		// update default user back to planner user group
		try {
			HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
					domain.getId());
			HmUserGroup plannerGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.PLANNING,
					domain.getId());
			defaultUser.setUserGroup(plannerGroup);
			
			QueryUtil.updateBo(defaultUser);
		} catch (Exception e) {
			DebugUtil.commonDebugError("updatePlanner2HM", e);
			UpdateInfo info = new UpdateInfo();
			info.setResult(false);
			info.setErrorMsg("Cannot rollback the planner account.");
			return info;
		}
		
		updateConfirm(domain);
		
		return response;
	}
	*/
	
	/*
	public static UpdateInfo updateOperation(HmDomain oDomain, int iContent, HhmUpgradeVersionInfo oUpdateInfo)
	{
        UpdateInfo oReturnInfo = new UpdateInfo();
		
	    //get status and charge	
        int iStatus = getDomainStatus(oDomain.getId());
		
		if(HmDomain.DOMAIN_BACKUP_STATUS == iStatus 
				|| HmDomain.DOMAIN_RESTORE_STATUS == iStatus
				|| HmDomain.DOMAIN_UPDATE_STATUS == iStatus)
		{
			oReturnInfo.setResult(false);
			String strErrMsg;
			
			switch(iStatus)
			{			
			    case HmDomain.DOMAIN_BACKUP_STATUS:
			    	strErrMsg = "Another administrator is currently backing up the HiveManager software. " +
			    			"Please try again later if necessary.";
			    	oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_RESTORE_STATUS:
			    	strErrMsg = "Another administrator is currently restoring the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			        break;
			    case HmDomain.DOMAIN_UPDATE_STATUS:
			    	strErrMsg = "Another administrator is currently updating the HiveManager software. " +
	    			"Please try again later if necessary.";
	    	        oReturnInfo.setErrorMsg(strErrMsg);
			    	break;
			}
			
			return oReturnInfo;
		}
		
		if(null == updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_UPDATE_STATUS))
		{
			oReturnInfo.setResult(false);
			String strErrmsg = "An error occurred while changing the status of VHM";
			oReturnInfo.setErrorMsg(strErrmsg);
			updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
			return oReturnInfo;
		}
		oReturnInfo = HHMupdate.updateHHM(oDomain, iContent, oUpdateInfo);
		
		updateDomainStatus(oDomain.getId(),HmDomain.DOMAIN_DEFAULT_STATUS);	
		
		return oReturnInfo;
	}
	*/
	
//	public static boolean updateConfirm(HmDomain oDomain)
//	{		
//		String where = "domainName = :s1 AND status = :s2";
//		Object[] values = new Object[2]; 
//		values[0] = oDomain.getDomainName();
//		values[1] = HMUpdateSoftwareInfo.STATUS_NEEDCONFIRM;
//		
//		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
//				null, new FilterParams(where,values));
//		
//		if(infoList.isEmpty())
//		{
//			return false;
//		}		
//		//change flag to need dns	
//		HMUpdateSoftwareInfo oInfo = infoList.get(0);
//		oInfo.setStatus(HMUpdateSoftwareInfo.STATUS_UPDATEDNS);
//		oInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);
//		
//		if(!HHMupdate.recordHHMInfo(oInfo))
//		{
//			//add some log
//			DebugUtil.adminDebugError("there has some error, when update change DNS");
//			return false;
//		}		
//		
//		return true;
//	}
	
	public static List<HMUpdateSoftwareInfo> getChangeDNSInfo()
	{
		String where = "status = :s1";
		Object[] values = new Object[1]; 		
		values[0] = HMUpdateSoftwareInfo.STATUS_UPDATEDNS;
		
		return QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
	}
	
	public static void doAfterChangeDNS(Collection<HMUpdateSoftwareInfo> oInfoList)
	{
		for (HMUpdateSoftwareInfo updateSoftwareInfo : oInfoList) {
			setVersionStatusToStandby(updateSoftwareInfo);
		}
	}
	
	public static boolean setVersionStatusToStandby(HMUpdateSoftwareInfo oInfo)
	{
		HMUpdateSoftwareInfo oTmp = QueryUtil.findBoById(HMUpdateSoftwareInfo.class, oInfo.getId());
		
		oTmp.setStatus(HMUpdateSoftwareInfo.STATUS_STANDBY);
		oTmp.setApSwithStatus(HMUpdateSoftwareInfo.NEED_AP_SWITH);
		
		try
		{
			QueryUtil.updateBo(oTmp);
			APSwitchCenter deviceSwitchCenter = AhAppContainer.getBeAdminModule().getDeviceSwitchCenter();
			deviceSwitchCenter.addSwitchInfo(oInfo.getDomainName(), oInfo.getIpAddress());
			
			//change AP setting	
			HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName",
					oInfo.getDomainName());
			
			if(null == bo)
			{
				return false;
			}
			
			BeTopoModuleUtil.transferHiveAPs(bo.getId(), oInfo.getIpAddress());
			
			//disable vhm
			BoMgmt.getDomainMgmt().disableDomain(oInfo.getDomainName());
			
			return true;
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.commonLog(BeLogTools.ERROR, 
					"update version info failed to HMUpdateSoftwareInfo. the name is: "+
					oInfo.getDomainName());
			
			BeLogTools.commonLog(BeLogTools.ERROR, ex);
			
			return false;
		}			
	}
	
	public static boolean UpdateConfirm_2(HmDomain oDomain, int iType)
	{
		String where = "domainName = :s1 AND status = :s2";
		Object[] values = new Object[2]; 
		values[0] = oDomain.getDomainName();
		values[1] = HMUpdateSoftwareInfo.STATUS_NEEDCONFIRM;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(infoList.isEmpty())
		{
			return false;
		}		
		//change flag to need dns	
		HMUpdateSoftwareInfo oInfo = infoList.get(0);
		oInfo.setStatus(HMUpdateSoftwareInfo.STATUS_UPDATEDNS);
		oInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);
		
		if(!HHMupdate.recordHHMInfo(oInfo))
		{
			//add some log
			DebugUtil.adminDebugError("there has some error, when update change DNS");
			return false;
		}		
		
		VhmRumStatus oUpdateInfo = new VhmRumStatus();
		oUpdateInfo.setVhmName(oInfo.getDomainName());
		oUpdateInfo.setSrcHmolAddress(HmBeOsUtil.getHiveManagerIPAddr());
		oUpdateInfo.setDestHmolAddress(oInfo.getIpAddress());
		oUpdateInfo.setProcessStatus(VhmRumStatus.STATUS_FINISHED);
		oUpdateInfo.setSuccess(true);
		
		//report update resl
		try
		{
			if(iType == HHM_UPDATE)
			{
				RemotePortalOperationRequest.reportVhmUpgradeStatus(oUpdateInfo);
			}
			else
			{
				RemotePortalOperationRequest.reportVhmMovingStatus(oUpdateInfo);
			}
			
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugError("Update vHM information to portal failed, "+ex.getMessage());
			return false;
		}
		
		//standby this domain
		return setVersionStatusToStandby(oInfo);
	}
	
	public static boolean revertOperation_2(String strDomainName, String strVersion, String strDestIp)
	{
		//send to the peer side not switch ap
		if(!Msg_client.sendHHMrevert(strDestIp, HHMoperate.Https_port, HHMoperate.Https_Message_Query, strDomainName))
		{
			//add some log
			DebugUtil.adminDebugError("Send to "+strDestIp+" have some error. when do revert operation");
			return false;
		}
		
		//report this 
		VhmRumStatus oInfoStatus = new VhmRumStatus();
		oInfoStatus.setVhmName(strDomainName);
		oInfoStatus.setDestHmolAddress(strDestIp);
		oInfoStatus.setSrcHmolAddress(HmBeOsUtil.getHiveManagerIPAddr());
		oInfoStatus.setStatus(VhmRumStatus.STATUS_FINISHED);
		oInfoStatus.setSuccess(true);
		try
		{
			RemotePortalOperationRequest.reportVhmRevertStatus(oInfoStatus);	
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugError(ex.getMessage());
			return false;
		}		
		
		String where = "domainName = :s1 AND ipAddress = :s2";
		Object[] values = new Object[2]; 
		values[0] = strDomainName;
		values[1] = strDestIp;
		
		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
				null, new FilterParams(where,values));
		
		if(infoList.isEmpty())
		{
			//add this bo
			HMUpdateSoftwareInfo bo = new HMUpdateSoftwareInfo();
			bo.setDomainName(strDomainName);
			bo.setIpAddress(strDestIp);
			bo.setHmVersion(strVersion);
            
			try
			{
				QueryUtil.createBo(bo);	
				
				infoList = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class,
						null, new FilterParams(where,values));
			}
			catch(Exception ex)
			{
				DebugUtil.adminDebugError(ex.getMessage());
				return false;
			}			
		}
		
		HMUpdateSoftwareInfo oInfo = infoList.get(0);	
		
		//standby this domain
		return setVersionStatusToStandby(oInfo);
	}

}