/**
 *@filename		BeAdminModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-4 09:36:21 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin;

import java.util.List;

import com.ah.be.admin.QueueOperation.BackupStatusItem;
import com.ah.be.admin.QueueOperation.HHMUpdateStatusItem;
import com.ah.be.admin.QueueOperation.RestoreStatusItem;
import com.ah.be.admin.adminOperateImpl.BeCAFileInfo;
import com.ah.be.admin.adminOperateImpl.BeFileInfo;
import com.ah.be.admin.adminOperateImpl.BeLogServerInfo;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeRebootInfoDTO;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.admin.adminOperateImpl.BeScpServerInfo;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgInfo;
import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.admin.hhmoperate.BackupInfo;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.admin.hhmoperate.UpdateInfo;
import com.ah.be.admin.util.AhSshKeyMgmt;
import com.ah.be.admin.util.EmailElement;
import com.ah.be.admin.util.SendMailThread;
import com.ah.be.sa3party.SaProcess;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeAdminModule
{

	/**
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: the backup content
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public String execBackup(int iContent) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : execute get hivemanager log
	 * @param: null
	 * @return:the log file name
	 * @throws: BeOperateException
	 */
	public String getlogExecCmd() throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : get the boot information
	 * @param: null
	 * @return:List<BeRebootInfoDTO>, two partition information
	 * @throws: null
	 */
	public List<BeRebootInfoDTO> getRebootInfo();

	/**
	 * @author xiaolanbao
	 * @description : modify the boot label
	 * @param: strLable: name of label
	 * @return:boolean
	 * @throws: null
	 */
	public void rebootSystemByLabel(String strLabel) throws BeOperateException;

	public void fourNodeRebootSystemByLabel(String strLabel) throws BeOperateException, InterruptedException;
	
	/**
	 * @author xiaolanbao
	 * @description :execute the shutdown system
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean shdownSystem()throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :create hivemanager root CA
	 * @param: BeRootCADTO, information of creating root ca
	 * @return:boolean
	 * @throws: null
	 */
	public boolean createRootCA(BeRootCADTO oData) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :create server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public boolean createServerCSR(BeRootCADTO oData) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :sign server csr
	 * @param: BeRootCADTO, information of creating csr
	 * @return:boolean
	 * @throws: null
	 */
	public boolean signServerCsr(BeRootCADTO oData, boolean bMergeFlag) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :get the file list of ca
	 * @param: null
	 * @return:List<String>:the list of file name
	 * @throws: null
	 */
	public List<String> getCAFileList(String strDomainName);

	/**
	 * @author xiaolanbao
	 * @description :remove the ca file
	 * @param: string: name of file
	 * @return:boolean
	 * @throws: null
	 */
	public boolean removeCAFile(String strFileName,String strDomainName) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
	//public AhScheduleBackupData getScheduleData();

	/**
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
	//public boolean setBackupSchedule(AhScheduleBackupData oAhScheduleBackupDTO);

	/**
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
//	public boolean cancelBackupTask();

	/**
	 * @author xiaolanbao
	 * @description :execute update script
	 * @param:null
	 * @return:boolean
	 * @throws: null
	 */

	 public void execUpdate(String strFile, int iContent) throws BeOperateException;

	/**
	 * for online ha model
	 * @param strFile
	 * @param iContent
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 16, 2012
	 */
	public void HaExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException;

	public void HaHmExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException;
	
	 /**
	 * @author xiaolanbao
	 * @description :execute restore script
	 * @param:String strFileName
	 * @return:null
	 * @throws: null
	 */
	public void execRestoreScript(String strFile) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :execute restore soft
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public boolean execRestartSoft() throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description :execute clean db
	 * @param:null
	 * @return:null
	 * @throws: null
	 */
	public boolean execCleanDB() throws BeOperateException;

	/**
	 * @author:xiaolanbao
	 * @description :is there ca files
	 * @param:the name of file
	 * @return:boolean
	 * @throws: null
	 */
	public  boolean IsCAFileExist(String strFileName,String strDomainName);


	/**
	 * @author:xiaolanbao
	 * @description :is there  root ca files
	 * @param:null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean IsRootCAExist(String strDomainName);

	/**
	 * @author xiaolanbao
	 * @description :get the file info list of ca
	 * @param: null
	 * @return:list<BeCAFileInfo>
	 * @throws: null
	 */
	public List<BeCAFileInfo> getCAFileInfoList(String strDomainName);

	/**
	 * @author:xiaolanbao
	 * @description :cp file from scpserver
	 * @param:struc for scpserverinfo
	 * @return:boolean
	 * @throws: null
	 */
	public boolean getFileFromScpServer(BeScpServerInfo oVerInfo) throws BeOperateException;


	/**
	 * update capwap server settings
	 *
	 *@param bo, if null, function will query from db
	 *
	 *@return
	 */
	public boolean updateCapwapSettings(CapwapSettings bo);

	/**
	 * @author:xiaolanbao
	 * @description :execute install certificate for ssl in tomcat
	 * @param:strServerCertName-the name of server certificate,
	 *        strServerKeyName-the name of server private key ,
	 *        strKeyPsd- the password of the key file.
	 * @return:boolean
	 * @throws: BeOperateException
	 */
	public boolean execInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException ;
	
	public boolean execOemInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException ;

	/**
	 * @author:xiaolanbao
	 * @description :execute auto install keystore file
	 * @param:-null
	 * @return:boolean
	 * @throws: BeOperateException
	 */
	public boolean installCertAuto() throws BeOperateException;

	/**
	 * @author:xiaolanbao
	 * @description :get the install keystore file information
	 * @param:-null
	 * @return:List<string>
	 * @throws: BeOperateException
	 */
	public List<String> getKeystoreInfo() throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : set the syslog server info
	 * @param: BeLogServerInfo:the information of syslog server
	 * @return:  boolean
	 * @throws: null
	 */
	public boolean setLogServer(BeLogServerInfo oData) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : get the syslog server info
	 * @param: null
	 * @return: BeLogServerInfo: the information of syslog server
	 * @throws: null
	 */
	public  BeLogServerInfo getLogServerInfo();

	/**
	 * authenticate and authorize
	 * @param userName
	 * @param userPassword
	 * @return HM-Admin object
	 * @throws AhAuthException
	 * 1.User-name or password do not match
	 * 2.No any RADIUS server
	 * 3.RADIUS exception
	 * 4.The RADIUS server(s) have no response
	 * 5.The RADIUS server(s) reject
	 */
	public HmUser authenticate(String userName, String userPassword) throws AhAuthException;

	/**
	 * @author xiaolanbao
	 * @description : execute domain backup
	 * @param: iContent:the flag about backup content; oDomain: the domain of backup
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public String backupDomainData(HmDomain oDomain, int iContent)
			throws BeOperateException ;

	/**
	 * @author xiaolanbao
	 * @description : execute backup
	 * @param: the backup content
	 * @return:the backup file name
	 * @throws: BeOperateException
	 */
	public  String backupFullData(int iContent)
			throws BeOperateException ;

	public String haBackupFullDataDump(int iContent) throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : execute restore domain data
	 * @param: domain and store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public void restoreDomainData(String strFile, HmDomain oDomain)
    throws BeOperateException;

	/**
	 * @author xiaolanbao
	 * @description : execute restore all data
	 * @param: store content
	 * @return:null
	 * @throws: BeOperateException
	 */
	public void restoreFullData(String strFile,String restoreType)
                         throws BeOperateException;

	public void haRestoreFullData(String strFile,String restoreType)
			throws BeOperateException ;

	/**
	 * @author xiaolanbao
	 * @description :get schedule info
	 * @param: null
	 * @return:AhScheduleBackupData
	 * @throws: null
	 */
	public AhScheduleBackupData getDomainScheduleData(HmDomain oDomain);

	/**
	 * @author xiaolanbao
	 * @description :set schedule backup
	 * @param: AhScheduleBackupData
	 * @return:boolean
	 * @throws: null
	 */
	public boolean setDoaminBackupSchedule(AhScheduleBackupData oData);

	/**
	 * @author xiaolanbao
	 * @description :cancel schedule backup
	 * @param: null
	 * @return:boolean
	 * @throws: null
	 */
	public boolean cancelBackupTask(HmDomain oDomain);

	/**
	 * @author xiaolanbao
	 * @description : execute clean domain data
	 * @param: oDomain
	 * @return:null
	 * @throws: BeOperateException
	 */
	public  void execCleanDomainDB(HmDomain oDomain) throws Exception;

	/**
	 * @author xiaolanbao
	 * @description : init upload config
	 * @param: BeUploadCfgInfo: the info of cfg file
	 * @return:null
	 * @throws: null
	 */
	public void initUploadCfg(BeUploadCfgInfo oCfgInfo );

	/**
	 * @author xiaolanbao
	 * @description :get the file info list
	 * @param: int typeFlag - 1:mib;2:radius dic;3:mac oui dic
	 * @return:list<BeFileInfo>
	 * @throws: null
	 */
	public List<BeFileInfo> getFileInfoList(int typeFlag);

	/**
	 * @author xiaolanbao
	 * @description :verify the CA certificate and privatekey
	 * @param: CA Name, certificate name, private key name, passwd for private key
	 * @return:boolean: true and false
	 * @throws: BeOperateException, the error exception
	 */
    public boolean verifyCertificate(String strCAName, String strCertName, String strKeyName,String strPsd,String strDomainName, boolean ischeckCA) throws BeOperateException;

	AhSshKeyMgmt getSshKeyMgmt();

	/**
	 * @author xiaolanbao
	 * @description :generate the authebtucate keys
	 * @param: the type of the key
	 * @return:boolean: true and false
	 * @throws: null
	 */
    public boolean generateAuthkeys(String strType);

    /**
	 * @author xiaolanbao
	 * @description :get ssh port
	 * @param: null
	 * @return:int, then number of the port
	 * @throws: null
	 */
    public int getSshdPort();

    /**
	 * @author xiaolanbao
	 * @description :set sshd port
	 * @param: int ssh port
	 * @return:boolean, true or false
	 * @throws: null
	 */
    public  boolean setSshdPort(int iPort);

    /**
	 * @author xiaolanbao
	 * @description :get pc model
	 * @param: null
	 * @return:String, then model for the pc 1U or 2U
	 * @throws: null
	 */
    public String getHmKernelModel();

    public String getHmSerialNumber();

    /**
	 * @author xiaolanbao
	 * @description :get eth inform
	 * @param: string eth0 or eth1
	 * @return:1.off/on 2.speed 10/100/1000 3.Duplex half/Full
	 * @throws: null
	 */
    public List<String> getEthInfo(String strEth);

    /**
	 * @author xiaolanbao
	 * @description :set eth inform
	 * @param: string:eth0/eth1, int:speed, string:duplex
	 * @return:boolean,true/false
	 * @throws: null
	 */
    public boolean setEthPro(String strEth, int iSpeed, String strDuplex,boolean isAuto);

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
    public boolean createDomainCWP(String strDomainName, BeRootCADTO oData) throws BeOperateException;

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
    public boolean createDefaultDomainCwp(String strDomainName) throws BeOperateException;

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
    public boolean checkForCwp(String strDomainName,String strCertName, String strKeyName,String strPsd) throws BeOperateException;

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
    public boolean mergeForCwp(String strDomainName,String strOutCwp,String strInCert, String strInKey,String strPsd);


    /**
	 * @author xiaolanbao
	 * @description :swith pfx-pem
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public boolean switchPfxToPem(String strSrcName, String strDestName, String strKey,String strDomainName);

    /**
	 * @author xiaolanbao
	 * @description :swith der-pem cert
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public boolean switchDERToPemCert(String strSrcName, String strDestName,String strDomainName);

    /**
	 * @author xiaolanbao
	 * @description :swith der-pem server key
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public boolean switchDerToPemKey(String strSrcName, String strDestName, String strKey,String strDomainName);

    /**
	 * send email API
	 *
	 * @param email element -
	 */
	public void sendEmail(EmailElement email);

	/**
	 * Update email settings
	 *
	 *@param
	 *
	 *@return
	 */
	public void updateMailNotification(MailNotification mailNotification);
	
	/**
	 * remove email settings
	 *
	 *@param
	 *
	 *@return
	 */
	public void removeMailNotification(String domainName);

	/**
	 * get cached mail setting bo
	 *
	 *@param
	 *
	 *@return
	 */
	public MailNotification getCacheMailNotification(String domainName);

	/**
	 * put the backup to queue
	 *
	 */
	public BackupInfo backupDomainDataInQueue(HmDomain oDomain, int iContent);

	/**
	 * get the status for backup in queue
	 */
	public BackupStatusItem getBackupStatus(long lId);

	/**
	 * put the restore to queue
	 */
	public RestoreInfo restoreDomainDataInQueue(HmDomain oDomain, String strPath, String strFileName);

	/**
	 * get the status for restore in queue
	 */
	public RestoreStatusItem getRestoreStatus(long lId);

	/**
	 * cancel the backup before begin done
	 */
	public boolean cancelBackupInQueue(HmDomain oDomain);

	/**
	 * cancel the restore before begin done
	 */
	public boolean cancelRestoreInQueue(HmDomain oDomain);

	/**
	 * put the update to queue
	 */
	public UpdateInfo HHMUpdateInQueue(HmDomain oDomain, int iContent,HhmUpgradeVersionInfo oInfo,int iUpdateType);

	/**
	 * get the status for update in queue
	 */
	public HHMUpdateStatusItem getUpdateStatus(long lId);

	/**
	 * cancel the update before begin done
	 */
	public boolean cancelHHMupdateInQueue(HmDomain oDomain);

	SendMailThread getSendMailThread();

	SaProcess getSaProcess();

	APSwitchCenter getDeviceSwitchCenter();

}