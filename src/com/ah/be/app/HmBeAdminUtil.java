package com.ah.be.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.QueueOperation.BackupStatusItem;
import com.ah.be.admin.QueueOperation.HHMUpdateStatusItem;
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
import com.ah.be.admin.adminOperateImpl.SubjectAltname_st;
import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.hhmoperate.BackupInfo;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.admin.hhmoperate.UpdateInfo;
import com.ah.be.admin.util.EmailElement;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;

/**
 *@filename		HmBeAdminUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:13:09
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public class HmBeAdminUtil
{

	public static final int AH_HM_MIB_FILE_TYPE = 1;

	public static final int AH_HM_RADIUS_DICTIONARY_TYPE = 2;

	public static final int AH_HM_MACOUI_DICTIONARY_TYPE = 3;

	public static final int AH_HM_CAPTURE_RESULT_FILETYPE = 4;
	
	public static final int AH_HM_CID_CLIENT_FILE_TYPE = 5;
	
	// restore vhm running status for fix bug 29327
	public static Map<Long, Boolean> VHM_RESTORE_STATUS_INFO = new HashMap<Long, Boolean>();

	/*
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: null
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public static String execBackup(int iContent) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().execBackup(iContent);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute get hivemanager log
	 * @param: null
	 * @return:the log file name
	 * @throws: BeOperateException
	 */
	public static String getlogExecCmd() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().getlogExecCmd();
	}

	/*
	 * @author xiaolanbao
	 * @description : get the boot information
	 * @param: null
	 * @return:List<BeRebootInfoDTO>, two partition information
	 * @throws: null
	 */
	public static List<BeRebootInfoDTO> getRebootInfo()
	{
		return AhAppContainer.HmBe.getAdminModule().getRebootInfo();
	}

	/*
	 * @author xiaolanbao
	 * @description : modify the boot label
	 * @param: strLable: name of label
	 * @return:boolean
	 * @throws: null
	 */
	public static void rebootByLabel(String strLabel) throws BeOperateException
	{
		AhAppContainer.HmBe.getAdminModule().rebootSystemByLabel(strLabel);
	}

	public static void fourNodeRebootByLabel(String strLabel) throws BeOperateException, InterruptedException
	{
		AhAppContainer.HmBe.getAdminModule().fourNodeRebootSystemByLabel(strLabel);
	}
	/*
	 * @author xiaolanbao
	 * @description :execute the shutdown system
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean shdownSystem() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().shdownSystem();
	}

	/*
	 * @author xiaolanbao
	 * @description :create hivemanager root CA
	 * @param: BeRootCADTO, information of creating root ca
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean createRootCA(BeRootCADTO oData) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().createRootCA(oData);
	}

	/*
	 * @author xiaolanbao
	 * @description :create server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean createServerCSR(BeRootCADTO oData) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().createServerCSR(oData);
	}

	/*
	 * @author xiaolanbao
	 * @description :sign server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean signServerCsr(BeRootCADTO oData, boolean bMergeFlag) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().signServerCsr(oData, bMergeFlag);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file list of ca
	 * @param: null
	 * @return:List<String>:the list of file name
	 * @throws: null
	 */
	public static List<String> getCAFileList(String strDomainName)
	{
		return AhAppContainer.HmBe.getAdminModule().getCAFileList(strDomainName);
	}

	/*
	 * @author xiaolanbao
	 * @description :remove the ca file
	 * @param: string: name of file
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean removeCAFile(String strFileName, String strDomainName) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().removeCAFile(strFileName, strDomainName);
	}

	/*
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
//	public static AhScheduleBackupData getScheduleData()
//	{
//		return AhAppContainer.HmBe.getAdminModule().getScheduleData();
//	}

	/*
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
//	public static boolean setBackupSchedule(AhScheduleBackupData oAhScheduleBackupDTO)
//	{
//		return AhAppContainer.HmBe.getAdminModule().setBackupSchedule(oAhScheduleBackupDTO);
//	}

	/*
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
//	public static boolean cancelBackupTask()
//	{
//		return AhAppContainer.HmBe.getAdminModule().cancelBackupTask();
//	}

	/*
	 * @author xiaolanbao
	 * @description :execute update
	 * @param:null
	 * @return:null
	 * @throws: BeOperateException
	 */

	public static void execUpdateSoft(String strFile, int iContent) throws BeOperateException
	{
		AhAppContainer.HmBe.getAdminModule().execUpdate(strFile, iContent);
	}

	/**
	 * @param strFile
	 * @param iContent
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 16, 2012
	 */
	public static void haExecUpdateSoft(String strFile,int iContent,int haStatus) throws BeOperateException{
		AhAppContainer.HmBe.getAdminModule().HaExecUpdate(strFile, iContent,haStatus);
	}

	/*
	 * @author xiaolanbao
	 * @description :execute restore script
	 * @param:String strFileName
	 * @return:null
	 * @throws: null
	 */
	public static void execRestore(String strFile) throws BeOperateException
	{
		AhAppContainer.HmBe.getAdminModule().execRestoreScript(strFile);
	}

	public static void haHmExecUpdateSoft(String strFile,int iContent,int haStatus) throws BeOperateException{
		AhAppContainer.HmBe.getAdminModule().HaHmExecUpdate(strFile, iContent,haStatus);
	}
	
	/*
	 * @author xiaolanbao
	 * @description :execute restart soft
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public static boolean restartSoft() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().execRestartSoft();
	}

	/*
	 * @author xiaolanbao
	 * @description :execute clean db
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public static boolean cleanDB() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().execCleanDB();
	}

	/*
	 * @author:xiaolanbao
	 * @description :is there ca files
	 * @param:the name of file
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean IsCAFileExist(String strFileName, String strDomainName)
	{
		return AhAppContainer.HmBe.getAdminModule().IsCAFileExist(strFileName, strDomainName);
	}

	/*
	 * @author:xiaolanbao
	 * @description :is there  root ca files
	 * @param:null
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean IsRootCAExist(String strDomainName)
	{
		return AhAppContainer.HmBe.getAdminModule().IsRootCAExist(strDomainName);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file info list of ca
	 * @param: null
	 * @return:list<BeCAFileInfo>
	 * @throws: null
	 */
	public static List<BeCAFileInfo> getCAFileInfoList(String strDomainName)
	{
		return AhAppContainer.HmBe.getAdminModule().getCAFileInfoList(strDomainName);
	}

	/*
	 * @author:xiaolanbao
	 * @description :cp file from scpserver
	 * @param:BeScpServerInfo the struct for scp server
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean getFileFromScpServer(BeScpServerInfo oVerInfo) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().getFileFromScpServer(oVerInfo);
	}

	/*
	 * update capwap server settings
	 *
	 *@param bo, if null, function will query from db
	 *
	 *@return
	 */
	public static boolean updateCapwapSettings(CapwapSettings bo)
	{
		return AhAppContainer.HmBe.getAdminModule().updateCapwapSettings(bo);
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
	public static boolean installCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().execInstallCert(strServerCertName, strServerKeyName, strKeyPsd);
	}

	public static boolean oemInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().execOemInstallCert(strServerCertName, strServerKeyName, strKeyPsd);
	}
	
	/*
	 * @author:xiaolanbao
	 * @description :execute auto install keystore file
	 * @param:-null
	 * @return:boolean
	 * @throws: BeOperateException
	 */
	public static boolean installCertAuto() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().installCertAuto();
	}

	/*
	 * @author:xiaolanbao
	 * @description :get the install keystore file information
	 * @param:-null
	 * @return:List<string>
	 * @throws: BeOperateException
	 */
	public static List<String> getKeystoreInfo() throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().getKeystoreInfo();
	}

	/*
	 * @author xiaolanbao
	 * @description : set the syslog server info
	 * @param: BeLogServerInfo:the information of syslog server
	 * @return:  boolean
	 * @throws: null
	 */
	public static boolean setLogServer(BeLogServerInfo oData) throws BeOperateException
	{
		return AhAppContainer.HmBe.getAdminModule().setLogServer(oData);
	}

	/*
	 * @author xiaolanbao
	 * @description : get the syslog server info
	 * @param: null
	 * @return: BeLogServerInfo: the information of syslog server
	 * @throws: null
	 */
	public static BeLogServerInfo getLogServerInfo()
	{
		return AhAppContainer.HmBe.getAdminModule().getLogServerInfo();
	}

	/*
	 * @author Jonathan
	 * @description authenticate and authorize
	 * @param userName
	 * @param userPassword
	 * @return HM admin-user object
	 */
	public static HmUser authenticate(String userName, String userPassword) throws AhAuthException
	{
		return AhAppContainer.HmBe.getAdminModule().authenticate(userName, userPassword);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute domain backup
	 * @param: iContent:the flag about backup content; oDomain: the domain of backup
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public static String backupDomainData(HmDomain oDomain, int iContent)
			throws BeOperateException
    {
		return AhAppContainer.HmBe.getAdminModule().backupDomainData(oDomain, iContent);
    }

	/*
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: the backup content
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public static String backupFullData(int iContent)
			throws BeOperateException
    {
		return AhAppContainer.HmBe.getAdminModule().backupFullData(iContent);
    }

	/**
	 * backup use pg_dump
	 * @param iContent
	 * @throws BeOperateException
	 * @return String
	 * @author fhu
	 * @date Feb 24, 2012
	 */
	public static String haBackupFullDataDump(int iContent) throws BeOperateException
    {
		return AhAppContainer.HmBe.getAdminModule().haBackupFullDataDump(iContent);
    }
	/*
	 * @author xiaolanbao
	 * @description : execute restore domain data
	 * @param: domain and store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public static void restoreDomainData(String strFile, HmDomain oDomain)
    		throws BeOperateException
    {
		AhAppContainer.HmBe.getAdminModule().restoreDomainData(strFile, oDomain);
    }

	/*
	 * @author xiaolanbao
	 * @description : execute restore all data
	 * @param: store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public static void restoreFullData(String strFile,String restoreType)
			throws BeOperateException
    {
		AhAppContainer.HmBe.getAdminModule().restoreFullData(strFile, restoreType);
    }

	public static void haRestoreFullData(String strFile,String restoreType)
			throws BeOperateException
    {
		AhAppContainer.HmBe.getAdminModule().haRestoreFullData(strFile,restoreType);
    }

	/*
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
	public static AhScheduleBackupData getDomainScheduleData(HmDomain oDomain)
	{
		return AhAppContainer.HmBe.getAdminModule().getDomainScheduleData(oDomain);
	}

	/*
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean setDoaminBackupSchedule(AhScheduleBackupData oData)
	{
		return AhAppContainer.HmBe.getAdminModule().setDoaminBackupSchedule(oData);
	}

	/*
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public static boolean cancelBackupTask(HmDomain oDomain)
	{
		return AhAppContainer.HmBe.getAdminModule().cancelBackupTask(oDomain);
	}

	/*
	 * @author xiaolanbao
	 * @description : execute clean domain data
	 * @param: oDomain
	 * @return:null
	 * @throws: BeOperateException
	 */
	public static void execCleanDomainDB(HmDomain oDomain) throws Exception
	{
		AhAppContainer.HmBe.getAdminModule().execCleanDomainDB(oDomain);
	}

	/*
	 * @author xiaolanbao
	 * @description : init upload config
	 * @param: BeUploadCfgInfo: the info of cfg file
	 * @return:null
	 * @throws: null
	 */
	public static void initUploadCfg(BeUploadCfgInfo oCfgInfo )
	{
		AhAppContainer.HmBe.getAdminModule().initUploadCfg(oCfgInfo);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file info list
	 * @param: int typeFlag - 1:mib;2:radius dic;3:mac oui dic
	 * @return:list<BeFileInfo>
	 * @throws: null
	 */
	public static List<BeFileInfo> getFileInfoList(int typeFlag)
	{
		return AhAppContainer.HmBe.getAdminModule().getFileInfoList(typeFlag);
	}

	/*
	 * @author xiaolanbao
	 * @description :verify the CA certificate and private key
	 * @param: CA Name, certificate name, private key name, passwd for private key
	 * @return:boolean: true and false
	 * @throws: BeOperateException, the error exception
	 */
    public static boolean verifyCertificate(String strCAName, String strCertName, String strKeyName, String strPsd, String strDomainName, boolean ischeckCA) throws BeOperateException
    {
    	return AhAppContainer.HmBe.getAdminModule().verifyCertificate(strCAName, strCertName, strKeyName, strPsd, strDomainName, ischeckCA);
    }

    /*
	 * @author xiaolanbao
	 * @description :generate the authenticate keys
	 * @param: the type of the key, rsa and dsa
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public static boolean generateAuthkeys(String strType)
    {
    	return AhAppContainer.HmBe.getAdminModule().generateAuthkeys(strType);
    }

    /*
	 * @author xiaolanbao
	 * @description :verify the captive portal server key
	 * @param: strKeyFile: the location of the file
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public static boolean verifycpskey(String strKeyFile)
    {
    	return BeOperateHMCentOSImpl.verifycpskey(strKeyFile);
    }

    /*
	 * @author xiaolanbao
	 * @description :charge the tftp service is on or off
	 * @param: null
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public static boolean isTftpEnable()
    {
    	return BeOperateHMCentOSImpl.isTftpEnable();
    }

    /*
	 * @author xiaolanbao
	 * @description :set the tftp service to on or off
	 * @param: null
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public static boolean setTftpEnable(boolean bFlag)
    {
    	return BeOperateHMCentOSImpl.setTftpEnable(bFlag);
    }

    /*
	 * @author xiaolanbao
	 * @description :get ssh port
	 * @param: null
	 * @return:int, then number of the port
	 * @throws: null
	 */
    public static int getSshdPort()
    {
    	return BeOperateHMCentOSImpl.getSshdPort();
    }

    /*
	 * @author xiaolanbao
	 * @description :set sshd port
	 * @param: int ssh port
	 * @return:boolean, true or false
	 * @throws: null
	 */
    public static boolean setSshdPort(int iPort)
    {
    	return BeOperateHMCentOSImpl.setSshdPort(iPort);
    }

    public static SubjectAltname_st getSubjetAltName(String strFile, String strDomainName){
    	return BeOperateHMCentOSImpl.getSubjetAltName(strFile, strDomainName);
    }

    /*
	 * @author xiaolanbao
	 * @description :get pc model
	 * @param: null
	 * @return:String, then model for the pc 1U or 2U
	 * @throws: null
	 */
    public static String getHmKernelModel()
    {
    	return AhAppContainer.HmBe.getAdminModule().getHmKernelModel();
    }

    public static String getHmSerialNumber()
    {
    	return AhAppContainer.HmBe.getAdminModule().getHmSerialNumber();
    }

    /*
	 * @author xiaolanbao
	 * @description :get eth inform
	 * @param: string eth0 or eth1
	 * @return:1.off/on 2.speed 10/100/1000 3.Duplex half/Full
	 * @throws: null
	 */
    public static List<String> getEthInfo(String strEth)
    {
    	return AhAppContainer.HmBe.getAdminModule().getEthInfo(strEth);
    }

    /*
	 * @author xiaolanbao
	 * @description :set eth inform
	 * @param: string:eth0/eth1, int:speed, string:duplex
	 * @return:boolean,true/false
	 * @throws: null
	 */
    public static boolean setEthPro(String strEth, int iSpeed, String strDuplex, boolean isAuto)
    {
    	return AhAppContainer.HmBe.getAdminModule().setEthPro(strEth, iSpeed, strDuplex, isAuto);
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
    public static boolean createDomainCWP(String strDomainName, BeRootCADTO oData) throws BeOperateException
    {
    	return AhAppContainer.HmBe.getAdminModule().createDomainCWP(strDomainName, oData);
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
    public static boolean createDefaultDomainCwp(String strDomainName) throws BeOperateException
    {
    	return AhAppContainer.HmBe.getAdminModule().createDefaultDomainCwp(strDomainName);
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
    public static boolean checkForCwp(String strDomainName, String strCertName, String strKeyName, String strPsd) throws BeOperateException
    {
    	return AhAppContainer.HmBe.getAdminModule().checkForCwp(strDomainName, strCertName, strKeyName, strPsd);
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
    public static boolean mergeForCwp(String strDomainName, String strOutCwp, String strInCert, String strInKey, String strPsd)
    {
    	return AhAppContainer.HmBe.getAdminModule().mergeForCwp(strDomainName, strOutCwp, strInCert, strInKey, strPsd);
    }

    /*
	 * @author xiaolanbao
	 * @description :swith pfx-pem
	 * @param:1:source name 2:dest name 3:is passwd, if no passwd,use null
	 * @return:boolean: true and false
	 */
    public static boolean switchPfxToPem(String strSrcName, String strDestName, String psd,String strDomainName)
    {
    	return AhAppContainer.HmBe.getAdminModule().switchPfxToPem(strSrcName, strDestName, psd, strDomainName);
    }

    /*
	 * @author xiaolanbao
	 * @description :swith der-pem cert
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public static boolean switchDERToPemCert(String strSrcName, String strDestName,String strDomainName)
    {
    	return AhAppContainer.HmBe.getAdminModule().switchDERToPemCert(strSrcName, strDestName, strDomainName);
    }

    /*
	 * @author xiaolanbao
	 * @description :swith der-pem server key
	 * @param:1:source name 2:dest name 3:is passwd, if no passwd,use null
	 * @return:boolean: true and false
	 */
    public static boolean switchDerToPemKey(String strSrcName, String strDestName, String psd,String strDomainName)
    {
    	return AhAppContainer.HmBe.getAdminModule().switchDerToPemKey(strSrcName, strDestName, psd, strDomainName);
    }

    /**
	 * send email API
	 *
	 * @param email element -
	 */
	public static void sendEmail(EmailElement email)
	{
		AhAppContainer.HmBe.getAdminModule().sendEmail(email);
	}

	/**
	 * Update email settings
	 *
	 * @param mailNotification -
	 */
	public static void updateMailNotification(MailNotification mailNotification)
	{
		AhAppContainer.HmBe.getAdminModule().updateMailNotification(mailNotification);
	}
	
	/**
	 * remove email settings
	 *
	 * @param mailNotification -
	 */
	public static void removeMailNotification(String domainName)
	{
		AhAppContainer.HmBe.getAdminModule().removeMailNotification(domainName);
	}

	/**
	 * get cached mail setting bo
	 *
	 * @param domainName -
	 * @return -
	 */
	public static MailNotification getCacheMailNotification(String domainName)
	{
		return AhAppContainer.HmBe.getAdminModule().getCacheMailNotification(domainName);
	}

	/**
	 * put the backup to queue
	 *
	 * @param oDomain -
	 * @param iContent -
	 * @return -
	 */
	public static BackupInfo backupDomainDataInQueue(HmDomain oDomain, int iContent)
	{
		return AhAppContainer.HmBe.getAdminModule().backupDomainDataInQueue(oDomain, iContent);
	}

	/**
	 * get the status for backup in queue
	 *
	 * @param lId -
	 * @return -
	 */
	public static BackupStatusItem getBackupStatus(long lId)
	{
		return AhAppContainer.HmBe.getAdminModule().getBackupStatus(lId);
	}

	/**
	 * put the restore to queue
	 *
	 * @param oDomain -
	 * @param strPath -
	 * @param strFileName -
	 * @return -
	 */
	public static RestoreInfo restoreDomainDataInQueue(HmDomain oDomain, String strPath, String strFileName)
	{
		return AhAppContainer.HmBe.getAdminModule().restoreDomainDataInQueue(oDomain, strPath, strFileName);
	}

	/**
	 * get the status for restore in queue
	 *
	 * @param lId -
	 * @return -
	 */
	public static RestoreStatusItem getRestoreStatus(long lId)
	{
		return AhAppContainer.HmBe.getAdminModule().getRestoreStatus(lId);
	}

	/**
	 * cancel the backup before it was done
	 *
	 * @param oDomain -
	 * @return -
	 */
	public static boolean cancelBackupInQueue(HmDomain oDomain)
	{
		return AhAppContainer.HmBe.getAdminModule().cancelBackupInQueue(oDomain);
	}

	/**
	 * cancel the restore before begin done
	 *
	 * @param oDomain -
	 * @return -
	 */
	public static boolean cancelRestoreInQueue(HmDomain oDomain)
	{
	    return AhAppContainer.HmBe.getAdminModule().cancelRestoreInQueue(oDomain);
	}

	/**
	 * put the update to queue
	 *
	 * @param oDomain -
	 * @param iContent -
	 * @param oInfo -
	 * @param iUpdateType -
	 * @return -
	 */
	public static UpdateInfo HHMUpdateInQueue(HmDomain oDomain, int iContent, HhmUpgradeVersionInfo oInfo, int iUpdateType)
	{
		return AhAppContainer.HmBe.getAdminModule().HHMUpdateInQueue(oDomain, iContent, oInfo, iUpdateType);
	}

	/**
	 * get the status for update in queue
	 *
	 * @param lId -
	 * @return -
	 */
	public static HHMUpdateStatusItem getUpdateStatus(long lId)
	{
		return AhAppContainer.HmBe.getAdminModule().getUpdateStatus(lId);
	}

	/**
	 * cancel the update before begin done
	 *
	 * @param oDomain -
	 * @return -
	 */
	public static boolean cancelHHMupdateInQueue(HmDomain oDomain)
	{
		return AhAppContainer.HmBe.getAdminModule().cancelHHMupdateInQueue(oDomain);
	}

}