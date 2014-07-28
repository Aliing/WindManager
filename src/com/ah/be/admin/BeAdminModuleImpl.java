/**
 *@filename		BeAdminModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-4 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.admin;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.admin.QueueOperation.BackupQueueOperation;
import com.ah.be.admin.QueueOperation.BackupStatusItem;
import com.ah.be.admin.QueueOperation.HHMUpdateQueueOperation;
import com.ah.be.admin.QueueOperation.HHMUpdateStatusItem;
import com.ah.be.admin.QueueOperation.RestoreQueueOperation;
import com.ah.be.admin.QueueOperation.RestoreStatusItem;
import com.ah.be.admin.adminOperateImpl.BeCAFileInfo;
import com.ah.be.admin.adminOperateImpl.BeFileInfo;
import com.ah.be.admin.adminOperateImpl.BeLogServerInfo;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.adminOperateImpl.BeRebootInfoDTO;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.admin.adminOperateImpl.BeScpServerInfo;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgInfo;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgTools;
import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.auth.AhAuthFactory;
import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.admin.hhmoperate.BackupInfo;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.admin.hhmoperate.UpdateInfo;
import com.ah.be.admin.restoredb.AhRestoreDBData;
//import com.ah.be.admin.schedulebackup.AhBackupSchedule;
import com.ah.be.admin.schedulebackup.AhBackupSchedulePool;
import com.ah.be.admin.util.AhSshKeyMgmt;
import com.ah.be.admin.util.AhSshKeyMgmtImpl;
import com.ah.be.admin.util.EmailElement;
import com.ah.be.admin.util.KeyManager;
import com.ah.be.admin.util.SendMailThread;
import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.communication.event.BeCapwapServerParamConfigEvent;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.sa3party.SaProcess;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeAdminModuleImpl extends BaseModule implements BeAdminModule
{

	public static short	OS_TYPE						= 1;

	private AhSshKeyMgmt sshKeyMgmt;

	private AhBackupSchedulePool scheduleBackup;

	private BackupQueueOperation backupOperation;

	private RestoreQueueOperation restoreOperation;

	private HHMUpdateQueueOperation hhmOperation;

	private SendMailThread mailSender;

	private SaProcess saProcess;

	private APSwitchCenter deviceSwitchCenter;

	/*
	 * Constructor
	 */
	public BeAdminModuleImpl(short sType)
	{
		setModuleId(3);
		setModuleName("BeAdminModule");
		OS_TYPE = sType;
	}

	@Override
	public boolean init() {
		if (OS_TYPE == BeOsLayerModule.OS_WINDOWS) {
			sshKeyMgmt = new AhSshKeyMgmtImpl();

			// Regenerate a pair of SSH authentication keys with DSA algorithm by default when launching HM each time.
			sshKeyMgmt.generateKeys("DSA");
		} else {
			sshKeyMgmt = new KeyManager();
		}

		scheduleBackup = new AhBackupSchedulePool();
		backupOperation = new BackupQueueOperation();
		restoreOperation = new RestoreQueueOperation();
		hhmOperation = new HHMUpdateQueueOperation();
		mailSender = new SendMailThread();
		saProcess = new SaProcess();
		deviceSwitchCenter = new APSwitchCenter();
		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run()
	{
		scheduleBackup.initSchedulePool();

		//deal with backup
		backupOperation.dealwithBackup();

		//deal with restore
		restoreOperation.dealrestore();

		//deal with update
		hhmOperation.dealwithUpdate();

		mailSender.startTask();

		//fnr add for SA third party test.
		saProcess.start();

		return true;
	}

	@Override
	public boolean shutdown() {
		if (scheduleBackup != null) {
			scheduleBackup.stop();
		}

		//shutdown backup
		if (backupOperation != null) {
			backupOperation.shutdown();
		}

		//shutdown restore
		if (restoreOperation != null) {
			restoreOperation.shutdown();
		}

		//shutdown update
		if (hhmOperation != null) {
			hhmOperation.shutdown();
		}

		if (mailSender != null) {
			mailSender.shutdown();
		}

		//fnr add for SA third party test.
		if (saProcess != null) {
			saProcess.stopProcess();
		}

		return true;
	}

	@Override
	public SendMailThread getSendMailThread() {
		return mailSender;
	}

	@Override
	public SaProcess getSaProcess() {
		return saProcess;
	}

	@Override
	public APSwitchCenter getDeviceSwitchCenter() {
		return deviceSwitchCenter;
	}

	/**
	 * send email API
	 *
	 * @param email element -
	 */
	public void sendEmail(EmailElement email)
	{
		mailSender.sendEmail(email);
	}

	/**
	 * get cached mail setting bo
	 *
	 * @param domainName -
	 * @return -
	 */
	public MailNotification getCacheMailNotification(String domainName)
	{
		return mailSender.getCacheMailNotification(domainName);
	}

	/**
	 * Update email settings
	 *
	 * @param mailNotification -
	 */
	public void updateMailNotification(MailNotification mailNotification)
	{
		mailSender.updateMailNotification(mailNotification);
	}
	
	/**
	 * remove email settings
	 *
	 * @param mailNotification -
	 */
	public void removeMailNotification(String domainName)
	{
		mailSender.removeMailNotification(domainName);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: iContent: the backup content
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public String execBackup(int iContent) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execBackupScript(iContent);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description : execute get hivemanager log
	 * @param: null
	 * @return:the log file name
	 * @throws: BeOperateException
	 */
	public String getlogExecCmd() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.getlogExecCmd();
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description : get the boot information
	 * @param: null
	 * @return:List<BeRebootInfoDTO>, two partition information
	 * @throws: null
	 */
	public List<BeRebootInfoDTO> getRebootInfo()
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.getRebootInfo();
		}

		return new ArrayList<BeRebootInfoDTO>();
	}

	/*
	 * @author xiaolanbao
	 * @description : modify the boot label
	 * @param: strLable: name of label
	 * @return:boolean
	 * @throws: null
	 */
	public void rebootSystemByLabel(String strLabel) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.rebootSystemByLabel(strLabel);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	
	public void fourNodeRebootSystemByLabel(String strLabel) throws BeOperateException, InterruptedException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.fourNodeRebootSystemByLabel(strLabel);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}
	
	/*
	 * @author xiaolanbao
	 * @description :execute the shutdown system
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean shdownSystem() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.shdownSystem();
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :create hivemanager root CA
	 * @param: BeRootCADTO, information of creating root ca
	 * @return:boolean
	 * @throws: null
	 */
	public boolean createRootCA(BeRootCADTO oData) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.createRootCA(oData);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :create server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public boolean createServerCSR(BeRootCADTO oData) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.createServerCSR(oData);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :sign server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public boolean signServerCsr(BeRootCADTO oData, boolean bMergeFlag) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.signServerCsr(oData, bMergeFlag);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file list of ca
	 * @param: null
	 * @return:List<String>:the list of file name
	 * @throws: null
	 */
	public List<String> getCAFileList(String strDomainName)
	{
		return BeOperateHMCentOSImpl.getCAFileList(strDomainName);
	}

	/*
	 * @author xiaolanbao
	 * @description :remove the ca file
	 * @param: string: name of file
	 * @return:boolean
	 * @throws: null
	 */
	public boolean removeCAFile(String strFileName, String strDomainName) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.removeCAFile(strFileName, strDomainName);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
//	public AhScheduleBackupData getScheduleData()
//	{
//		return AhBackupSchedule.getInstance().getScheduleData();
//	}

	/*
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
//	public boolean setBackupSchedule(AhScheduleBackupData oAhScheduleBackupDTO)
//	{
//		return AhBackupSchedule.getInstance().setBackupSchedule(oAhScheduleBackupDTO);
//	}

	/*
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
//	public boolean cancelBackupTask()
//	{
//		return AhBackupSchedule.getInstance().cancelBackupTask();
//	}

	/*
	 * @author xiaolanbao
	 * @description :execute update
	 * @param:null
	 * @return:
	 * @throws: BeOperateException
	 */

	public void execUpdate(String strFile, int iContent) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.execUpdate(strFile, iContent);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * for online HA model
	 */
	public void HaExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.haExecUpdate(strFile, iContent,haStatus);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/**
	 * for hm ha, 4 nodes
	 */
	public void HaHmExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.haHmExecUpdate(strFile, iContent,haStatus);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :execute restore script
	 * @param:String strFileName
	 * @return:null
	 * @throws: null
	 */
	public void execRestoreScript(String strFile) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.execRestoreScript(strFile);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :execute restore soft
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public boolean execRestartSoft() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execRestartSoft();
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :execute clean db
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public boolean execCleanDB() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execCleanDB();
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author:xiaolanbao
	 * @description :is there ca files
	 * @param:the name of file
	 * @return:boolean
	 * @throws: null
	 */
	public boolean IsCAFileExist(String strFileName, String strDomainName)
	{
		return BeOperateHMCentOSImpl.IsCAFileExist(strFileName, strDomainName);
	}

	/*
	 * @author:xiaolanbao
	 * @description :is there  root ca files
	 * @param:null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean IsRootCAExist(String strDomainName)
	{
		return BeOperateHMCentOSImpl.IsRootCAExist(strDomainName);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file info list of ca
	 * @param: null
	 * @return:list<BeCAFileInfo>
	 * @throws: null
	 */
	public List<BeCAFileInfo> getCAFileInfoList(String strDomainName)
	{
		return BeOperateHMCentOSImpl.getCAFileInfoList(strDomainName);
	}

	/*
	 * @author:xiaolanbao
	 * @description :cp file from scpserver
	 * @param:struc for scpserverinfo
	 * @return:boolean
	 * @throws: null
	 */
	public boolean getFileFromScpServer(BeScpServerInfo oVerInfo) throws BeOperateException
	{
		return BeOperateHMCentOSImpl.getFileFromScpServer(oVerInfo);
	}

	/**
	 * @see com.ah.be.admin.BeAdminModule#updateCapwapSettings(CapwapSettings bo)
	 */
	public boolean updateCapwapSettings(CapwapSettings bo)
	{
		if (bo == null)
		{
			List<CapwapSettings> capwapSettings = QueryUtil.executeQuery(CapwapSettings.class, null, null);
			if (capwapSettings.isEmpty())
			{
				return false;
			}

			bo = capwapSettings.get(0);
		}

		BeCapwapServerParamConfigEvent capwapEvent = new BeCapwapServerParamConfigEvent();
		capwapEvent.setUdpPort((short)bo.getUdpPort());
		capwapEvent.setEchoTimeout(bo.getTimeOut());
		capwapEvent.setNeighborDeadInterval(bo.getNeighborDeadInterval());
		capwapEvent.setDtlsCapability(bo.getDtlsCapability());
		capwapEvent.setPassPhrase(bo.getBootStrap());
		String simulatorFlag = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_APPLICATION_SUPPORTSIMULATOR, "1");
		capwapEvent.setSupportSimulator(Integer.valueOf(simulatorFlag) == 1);
		try
		{
			capwapEvent.buildPacket();
		}
		catch (Exception e)
		{
			return false;
		}

		HmBeCommunicationUtil.sendRequest(capwapEvent);

		return true;
	}

	/*
	 * @author:xiaolanbao
	 * @description :execute install certificate for ssl in tomcat
	 * @param:strServerCertName-the name of server certificate,
	 *        strServerKeyName-the name of server private key,
	 *        strKeyPsd- the password of the key file.
	 * @return:boolean
	 * @throws: BeOperateException
	 */
	public boolean execInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execInstallCert(strServerCertName, strServerKeyName, strKeyPsd);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	public boolean execOemInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execOemInstallCert(strServerCertName, strServerKeyName, strKeyPsd);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}
	/*
	 * @author:xiaolanbao
	 * @description :execute auto install keystore file
	 * @param:-null
	 * @return:boolean
	 * @throws: BeOperateException
	 */
	public boolean installCertAuto() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.execInstallCertAuto();
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author:xiaolanbao
	 * @description :get the install keystore file information
	 * @param:-null
	 * @return:List<string>
	 * @throws: BeOperateException
	 */
	public List<String> getKeystoreInfo() throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.getKeystoreInfo();
		}

		String strPermission = "adminKeystoreErr";

		List<String> oTmp = new ArrayList<String>();

		oTmp.add(HmBeResUtil.getString(strPermission));

		return oTmp;
	}

	/*
	 * @author xiaolanbao
	 * @description : set the syslog server info
	 * @param: BeLogServerInfo:the information of syslog server
	 * @return:  boolean
	 * @throws: null
	 */
	public boolean setLogServer(BeLogServerInfo oData) throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.setLogServer(oData);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description : get the syslog server info
	 * @param: null
	 * @return: BeLogServerInfo: the information of syslog server
	 * @throws: null
	 */
	public BeLogServerInfo getLogServerInfo()
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.getLogServerInfo();
		}

		return new BeLogServerInfo();
	}


	/**
	 * @see com.ah.be.admin.BeAdminModule#authenticate(java.lang.String, java.lang.String)
	 */
	public HmUser authenticate(String userName, String userPassword) throws AhAuthException {
		AhAuthAgent authAgent = AhAuthFactory.getInstance().getAuthAgent();
		return authAgent.execute(userName, userPassword);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute domain backup
	 * @param: iContent:the flag about backup content; oDomain: the domain of backup
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public String backupDomainData(HmDomain oDomain, int iContent)
			throws BeOperateException {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.backupDomainData(oDomain, iContent);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: the backup content
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public String backupFullData(int iContent)
			throws BeOperateException {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.backupFullData(iContent);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	public String haBackupFullDataDump(int iContent) throws BeOperateException {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			return BeOperateHMCentOSImpl.haBackupFullDataDump(iContent);
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description : execute restore domain data
	 * @param: domain and store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public void restoreDomainData(String strFile, HmDomain oDomain)
    throws BeOperateException
    {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.restoreDomainData(strFile, oDomain);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
    }

	/*
	 * @author xiaolanbao
	 * @description : execute restore all data
	 * @param: store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public void restoreFullData(String strFile,String restoreType)
                         throws BeOperateException
    {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.restoreFullData(strFile,restoreType);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
    }

	public void haRestoreFullData(String strFile,String restoreType)
			throws BeOperateException
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.haRestoreFullData(strFile,restoreType);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}

	/*
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
	public AhScheduleBackupData getDomainScheduleData(HmDomain oDomain)
	{
		return scheduleBackup.getScheduleData(oDomain);
	}

	/*
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
	public boolean setDoaminBackupSchedule(AhScheduleBackupData oData)
	{
		return scheduleBackup.setBackupSchedule(oData);
	}

	/*
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean cancelBackupTask(HmDomain oDomain)
	{
		return scheduleBackup.cancelBackupTask(oDomain);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute clean domain data
	 * @param: oDomain
	 * @return:null
	 * @throws: BeOperateException
	 */
	public void execCleanDomainDB(HmDomain oDomain) throws Exception
	{
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
		{
			BeOperateHMCentOSImpl.execCleanDomainDB(oDomain);

			return;
		}

		String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
	}


	/*
	 * @author xiaolanbao
	 * @description : init upload config
	 * @param: BeUploadCfgInfo: the info of cfg file
	 * @return:null
	 * @throws: null
	 */
	public void initUploadCfg(BeUploadCfgInfo oCfgInfo )
	{
		AhRestoreDBData.cleanLogFile();

		BeUploadCfgTools.initUploadConfFile(oCfgInfo);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file info list
	 * @param: int typeFlag - 1:mib;2:radius dic;3:mac oui dic
	 * @return:list<BeFileInfo>
	 * @throws: null
	 */
	public List<BeFileInfo> getFileInfoList(int typeFlag)
	{
		return BeOperateHMCentOSImpl.getFileInfoList(typeFlag);
	}

	/*
	 * @author xiaolanbao
	 * @description :verify the CA certificate and privatekey
	 * @param: CA Name, certificate name, private key name, passwd for private key
	 * @return:boolean: true and false
	 * @throws: BeOperateException, the error exception
	 */
    public boolean verifyCertificate(String strCAName, String strCertName, String strKeyName, String strPsd, String strDomainName, boolean ischeckCA) throws BeOperateException
    {
		return OS_TYPE != BeOsLayerModule.OS_LINUX || BeOperateHMCentOSImpl.verifyCertificate(strCAName, strCertName, strKeyName, strPsd, strDomainName,ischeckCA);
	}

	public AhSshKeyMgmt getSshKeyMgmt() {
		return sshKeyMgmt;
	}

    /*
	 * @author xiaolanbao
	 * @description :generate the authebtucate keys
	 * @param: the type of the key
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public boolean generateAuthkeys(String strType)
    {
		if(sshKeyMgmt.generateKeys(strType))
		{
			BeTopoModuleUtil.sendIdentification();

			return true;
		}

		return false;
    }

    /*
	 * @author xiaolanbao
	 * @description :get ssh port
	 * @param: null
	 * @return:int, then number of the port
	 * @throws: null
	 */
    public int getSshdPort()
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.getSshdPort();
    	}

    	return 22;
    }

    /*
	 * @author xiaolanbao
	 * @description :set sshd port
	 * @param: int ssh port
	 * @return:boolean, true or false
	 * @throws: null
	 */
    public boolean setSshdPort(int iPort)
    {
		return OS_TYPE == BeOsLayerModule.OS_LINUX && BeOperateHMCentOSImpl.setSshdPort(iPort);
	}

    /*
	 * @author xiaolanbao
	 * @description :get pc model
	 * @param: null
	 * @return:String, then model for the pc 1U or 2U
	 * @throws: null
	 */
    public String getHmKernelModel()
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.getHmKernelModel();
    	}

    	return "AH-HM-NR-1U";
    }

    /*
	 * @author xiaolanbao
	 * @description :get eth inform
	 * @param: string eth0 or eth1
	 * @return:1.off/on 2.speed 10/100/1000 3.Duplex half/Full
	 * @throws: null
	 */
    public List<String> getEthInfo(String strEth)
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.getEthInfo(strEth);
    	}

    	List<String> ethInfo = new ArrayList<String>();

    	ethInfo.add("off");
    	ethInfo.add("");
    	ethInfo.add("");

    	return ethInfo;
    }

    /*
	 * @author xiaolanbao
	 * @description :set eth inform
	 * @param: string:eth0/eth1, int:speed, string:duplex
	 * @return:boolean,true/false
	 * @throws: null
	 */
    public boolean setEthPro(String strEth, int iSpeed, String strDuplex, boolean isAuto)
    {
		return OS_TYPE == BeOsLayerModule.OS_LINUX && BeOperateHMCentOSImpl.setEthPro(strEth, iSpeed, strDuplex, isAuto);
	}

    /*
	 * @author xiaolanbao
	 *
	 * @description :create domain cwp key file
	 *
	 * @param: BeRootCADTO, information of creating cwp
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public boolean createDomainCWP(String strDomainName, BeRootCADTO oData) throws BeOperateException
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.createDomainCWP(strDomainName, oData);
    	}

    	String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
    }

    /*
	 * @author xiaolanbao
	 *
	 * @description :create default domain cwp key file
	 *
	 * @param: strDomain Name
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public boolean createDefaultDomainCwp(String strDomainName) throws BeOperateException
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.createDefaultDomainCwp(strDomainName);
    	}

//    	String strPermission = "adminPermitErr";
//
//		throw new BeOperateException(HmBeResUtil
//				.getString(strPermission));

    	return false;
    }

    /*
	 * @author xiaolanbao
	 *
	 * @description :check the cert and the key before merge
	 *
	 * @param: strCertName,strKeyName,strPsd:passwd for keyfile
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public boolean checkForCwp(String strDomainName, String strCertName, String strKeyName, String strPsd) throws BeOperateException
    {
    	if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.checkForCwp(strDomainName, strCertName, strKeyName, strPsd);
    	}

    	String strPermission = "adminPermitErr";

		throw new BeOperateException(HmBeResUtil
				.getString(strPermission));
    }

    /*
	 * @author xiaolanbao
	 *
	 * @description :merge the file for cwp key
	 *
	 * @param: strOutCwp:only the name of cwp file, do not include postfix
	 *          strIncert and strInKey: need full file name, include postfix
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public boolean mergeForCwp(String strDomainName, String strOutCwp, String strInCert, String strInKey, String strPsd)
    {
		return OS_TYPE == BeOsLayerModule.OS_LINUX && BeOperateHMCentOSImpl.mergeForCwp(strDomainName, strOutCwp, strInCert, strInKey, strPsd);
	}

	@Override
	public boolean switchDERToPemCert(String strSrcName, String strDestName, String strDomainName) {
		return BeOperateHMCentOSImpl.switchDERToPemCert(strSrcName, strDestName, strDomainName);
	}

	@Override
	public boolean switchDerToPemKey(String strSrcName, String strDestName,
			String strKey, String strDomainName) {

		return BeOperateHMCentOSImpl.switchDerToPemKey(strSrcName, strDestName, strKey, strDomainName);
	}

	@Override
	public boolean switchPfxToPem(String strSrcName, String strDestName,
			String strKey, String strDomainName) {

		return BeOperateHMCentOSImpl.switchPfxToPem(strSrcName, strDestName, strKey, strDomainName);
	}

	/**
	 * put the backup to queue
	 *
	 */
	public BackupInfo backupDomainDataInQueue(HmDomain oDomain, int iContent)
	{
		return backupOperation.backupDomainDataInQueue(oDomain, iContent);
	}

	/**
	 * get the status for backup in queue
	 */
	public BackupStatusItem getBackupStatus(long lId)
	{
		return backupOperation.getBackupStatus(lId);
	}

	/**
	 * put the restore to queue
	 */
	public RestoreInfo restoreDomainDataInQueue(HmDomain oDomain, String strPath, String strFileName)
	{
		return restoreOperation.restoreDomainDataInQueue(oDomain, strPath, strFileName);
	}

	/**
	 * get the status for restore in queue
	 */
	public RestoreStatusItem getRestoreStatus(long lId)
	{
		return restoreOperation.getRestoreStatus(lId);
	}

	/**
	 * cancel the backup before it was done
	 */
	public boolean cancelBackupInQueue(HmDomain oDomain)
	{
		return backupOperation.cancelBackupInQueue(oDomain);
	}

	/**
	 * cancel the restore before begin done
	 */
	public boolean cancelRestoreInQueue(HmDomain oDomain)
	{
		return restoreOperation.cancelRestoreInQueue(oDomain);
	}

	/**
	 * put the update to queue
	 */
	public UpdateInfo HHMUpdateInQueue(HmDomain oDomain, int iContent, HhmUpgradeVersionInfo oInfo, int iUpdateType)
	{
		return hhmOperation.HHMUpdateInQueue(oDomain, iContent, oInfo, iUpdateType);
	}

	/**
	 * get the status for update in queue
	 */
	public HHMUpdateStatusItem getUpdateStatus(long lId)
	{
		return hhmOperation.getUpdateStatus(lId);
	}

	/**
	 * cancel the update before begin done
	 */
	public boolean cancelHHMupdateInQueue(HmDomain oDomain)
	{
		return hhmOperation.cancelHHMupdateInQueue(oDomain);
	}

	@Override
	public String getHmSerialNumber() {
		if(OS_TYPE == BeOsLayerModule.OS_LINUX)
    	{
    		return BeOperateHMCentOSImpl.getHmSerialNumber();
    	}

    	return "";
	}
}