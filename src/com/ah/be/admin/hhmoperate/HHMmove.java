package com.ah.be.admin.hhmoperate;

import java.util.ArrayList;
import java.util.List;

import com.ah.apiengine.element.MvInfo;
import com.ah.apiengine.element.MvResponseInfo;
import com.ah.be.admin.hhmoperate.https.Msg_client;
import com.ah.be.admin.hhmoperate.https.RestoreCheck_client;
import com.ah.be.admin.hhmoperate.https.Upload_client;
import com.ah.be.admin.hhmoperate.https.data.HHMupdatePacketData;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HmDomain;
//import com.ah.bo.admin.HmUser;
//import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.QueryUtil;

public class HHMmove {

	public static List<MvResponseInfo> lStatus = new ArrayList<MvResponseInfo>();

	public static String m_strDestVersion;

	private static UpdateInfo mvHHM(HmDomain oDomain, int iContent, String strDestIp, String strVersion,MvResponseInfo oInfo, int sHHMType)
	{
		UpdateInfo oReturnInfo = new UpdateInfo();

//		if(sHHMType == MvInfo.HHM_TYPE_PLAN_EVAL)
//		{
//			try
//			{
//				HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
//						oDomain.getId());
//				HmUserGroup configGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.CONFIG,
//						oDomain.getId());
//				defaultUser.setUserGroup(configGroup);
//
//				QueryUtil.updateBo(defaultUser);
//			}
//			catch(Exception ex)
//			{
//				BeLogTools.commonLog(BeLogTools.ERROR,ex);
//				oReturnInfo.setResult(false);
//				oReturnInfo.setErrorMsg("configure before planner update failed!");
//				return oReturnInfo;
//			}
//		}
//
		//backup data
		oInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_BACKUP_DATA);
		BackupInfo oBackupInfo = HHMoperate.backupOperation(oDomain, iContent, true);

		if(!oBackupInfo.getResult())
		{
			oReturnInfo.setResult(oBackupInfo.getResult());
			oReturnInfo.setErrorMsg(oBackupInfo.getErrorMsg());
			return oReturnInfo;
		}

		//transmit the backup data
		oInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_TRANSFER_DATA);
		if(!Upload_client.uploadFile(strDestIp, HHMoperate.Https_port, HHMoperate.Https_Upload_Query, oBackupInfo.getFilePath(), oBackupInfo.getFileName()))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when upload backup file!");
			return oReturnInfo;
		}

		//build packet
		HHMupdatePacketData oSendData = new HHMupdatePacketData();
		oSendData.setFilePath(oBackupInfo.getFilePath());
		oSendData.setFileName(oBackupInfo.getFileName());
		oSendData.setHHMDomain(oDomain);
		oSendData.setHHMFlag(HHMConstant.HHM_Fix_Flag);
		oSendData.setHHMVersion(NmsUtil.getVersionInfo().getMainVersion());
		oSendData.setPackType(HHMConstant.Packet_Type_HHM_Update);
		oSendData.setProtocolVersion(HHMConstant.Packet_protocol_version);

		//send data
		oInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_RESTORE_DATA);
		if(!Msg_client.sendHHMupdate(strDestIp, HHMoperate.Https_port, HHMoperate.Https_Message_Query, oSendData))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
			return oReturnInfo;
		}

		byte restoreSt = RestoreCheck_client.checkRestore(strDestIp, HHMoperate.Https_port, HHMoperate.Https_Restore_Query, oSendData);
		while(CommConst.RESTORE_RUNNING == restoreSt){
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove: check restore thead return 1, restore is running!");
				oReturnInfo.setResult(false);
				oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
				return oReturnInfo;
			}
			
			restoreSt = RestoreCheck_client.checkRestore(strDestIp, HHMoperate.Https_port, HHMoperate.Https_Restore_Query, oSendData);
		}
		
		if(CommConst.RESTORE_ERROR == restoreSt){
			BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove: check restore thead return 3, some error happened!");
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when do restore data at peer!");
			return oReturnInfo;
		}
		
		//chang data
		HMUpdateSoftwareInfo oSoftwareInfo = new HMUpdateSoftwareInfo();
		oSoftwareInfo.setDomainName(oDomain.getDomainName());
		oSoftwareInfo.setHmVersion(strVersion);
		oSoftwareInfo.setIpAddress(strDestIp);
		oSoftwareInfo.setStatus(HMUpdateSoftwareInfo.STATUS_NEEDCONFIRM);
		oSoftwareInfo.setApSwithStatus(HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH);

		//config
		oInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_TRANSFER_DATA);
		if(!HHMupdate.recordHHMInfo(oSoftwareInfo))
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg("There is an error, when changing version status!");
			return oReturnInfo;
		}

		//HHMoperate.updateConfirm(oDomain);
		HHMoperate.UpdateConfirm_2(oDomain, HHMoperate.HHM_MV);

		oInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_NO_OPERATION);

//		if(sHHMType == MvInfo.HHM_TYPE_PLAN_EVAL)
//		{
//			try
//			{
//				HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
//						oDomain.getId());
//				HmUserGroup plannerGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", HmUserGroup.PLANNING,
//						oDomain.getId());
//				defaultUser.setUserGroup(plannerGroup);
//			}
//			catch(Exception ex)
//			{
//				BeLogTools.commonLog(BeLogTools.ERROR,ex);
//				oReturnInfo.setResult(false);
//				oReturnInfo.setErrorMsg("configure after planner update failed!");
//				return oReturnInfo;
//			}
//		}

		oReturnInfo.setResult(true);

		return oReturnInfo;
	}

	public static void moveHHM()
	{
		int iType = getHMType();

		for (MvResponseInfo status : lStatus) {
			//find domain
			HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName",
					status.getDomainName());

			if (null == bo) {
				status.setMVStatus(MvResponseInfo.MV_STATUS_FINISHED);
				status.setMsg("Could not find the domain with name " + status.getDomainName());
				continue;
			}

			status.setMVStatus(MvResponseInfo.MV_STATUS_RUNNING);

			//move data
			UpdateInfo oUpdateInfo = mvHHM(bo, AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP,
					status.getDestIp(), m_strDestVersion, status, iType);

			status.setMVStatus(MvResponseInfo.MV_STATUS_FINISHED);
			status.setResult(oUpdateInfo.getResult());
			if (oUpdateInfo.getErrorMsg() != null) {
				status.setMsg(oUpdateInfo.getErrorMsg());
			}
		}
	}

	public static void initList(MvInfo oInfo)
	{
		lStatus.clear();

		List<String> lDomainNameList = oInfo.getDomainNameList();

		if(null == lDomainNameList)
		{
			BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove::initList, domain list is null");

			return ;
		}

		for (String domainName : lDomainNameList) {
			MvResponseInfo oMvInfo = new MvResponseInfo();
			oMvInfo.setDomainName(domainName);
			oMvInfo.setDestIp(oInfo.getDestIpaddress());
			oMvInfo.setSrcIp(oInfo.getSrcIpaddress());
			oMvInfo.setMVStatus(MvResponseInfo.MV_STATUS_WAITTING);
			oMvInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_NO_OPERATION);
			
			if(checkDomianName(lStatus, domainName)){
				lStatus.add(oMvInfo);
			}
		}
	}


	public static void initList_2(String destIPAddr, List<String> vhmNameList, String strDestVersion)
	{
        lStatus.clear();
        m_strDestVersion = strDestVersion;

		String strSrcIp = HmBeOsUtil.getHiveManagerIPAddr();

		if(null == vhmNameList)
		{
			BeLogTools.commonLog(BeLogTools.ERROR, "HHMmove::initList, domain list is null");

			return ;
		}

		for (String domainName : vhmNameList) {
			MvResponseInfo oMvInfo = new MvResponseInfo();
			oMvInfo.setDomainName(domainName);
			oMvInfo.setDestIp(destIPAddr);
			oMvInfo.setSrcIp(strSrcIp);
			oMvInfo.setMVStatus(MvResponseInfo.MV_STATUS_WAITTING);
			oMvInfo.setProcessStatus(MvResponseInfo.MV_PROCESS_STATUS_NO_OPERATION);

			if(checkDomianName(lStatus, domainName)){
				lStatus.add(oMvInfo);
			}
			//lStatus.add(oMvInfo);
		}
	}

	/**
	 * if lstatus include this domainName then return false,else return true
	 * @param lstatus List<MvResponseInfo>
	 * @param domainName
	 * @return boolean
	 * @author fhu
	 * @date Apr 16, 2012
	 */
	private static boolean checkDomianName(List<MvResponseInfo> lstatus,String domainName){
		for(MvResponseInfo mr : lstatus){
			if(mr.getDomainName().equals(domainName)){
				return false;
			}
		}
		return true;
	}

	public static void cleanStatusInfo()
	{
		lStatus.clear();
	}

	private static int getHMType()
	{
		if(NmsUtil.isPlanner())
		{
			return MvInfo.HHM_TYPE_PLAN_EVAL;
		}

		if(NmsUtil.isDemoHHM())
		{
			return MvInfo.HHM_TYPE_HM_EVAL;
		}

		return MvInfo.HHM_TYPE_PRODUCT;
	}

}