/**
 *@filename		BeOperateHMCentOSImpl.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-9-13 11:33:20
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */

package com.ah.be.admin.adminOperateImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.BeAdminShellTool;
import com.ah.be.admin.ShellRslt_st;
import com.ah.be.admin.QueueOperation.HMUpdateStatus;
import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.admin.restoredb.AhRestoreGetXML;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.OEMCustomer;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.license.HM_License;
import com.ah.be.os.FileManager;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.be.admin.util.HAadminTool;
import com.ah.ha.HAStatus;

/**
 * @author Xiaolanbao
 * @version V2.2.0.0
 */
public class BeOperateHMCentOSImpl {

	private static final String AH_UNZIP_ROOT = "./tmp/dbxmlfile";

	//private static String AH_TMP_DOWMLOAD = "./tmp/HiveManager/downloads";

	//private static String AH_TMP_MAP = "./tmp/webapps/ROOT/domains";

	private static final String AH_SIGN_IMAGE_PUBLIC_KEY = "/etc/image_signing/rsapublickey.pem";

	//private static String AH_LOCAL_INNER_VERSION="./webapps/ROOT/WEB-INF/hmconf/.inner.ver";

	private static final String AH_LOCAL_INNER_VERSION = System.getenv("HM_ROOT")+"/WEB-INF/hmconf/.inner.ver";

	private static final String AH_UPDATE_INNER_VERSION ="./hm_soft_upgrade/hm/WEB-INF/hmconf/.inner.ver";

	//private static String AH_HM_VERSION="./hm_soft_upgrade/hm/WEB-INF/hmconf/hivemanager.ver";
	private static String kernelModel = null;

	private static String serialNumber = null;

	private static boolean bBackupFlag = false;

	private static boolean bGetlogFlag = false;

	private static boolean bUpdateFlag = false;

	private static boolean bRestoreFlag = false;

	private static Boolean isTftpEnable;

	private static void setRestoreFlag(boolean bFlag) {
		bRestoreFlag = bFlag;
	}

	private static void setGetlogFlag(boolean bFlag) {
		bGetlogFlag = bFlag;
	}

	private static void setBackupFlag(boolean bFlag) {
		bBackupFlag = bFlag;
	}

	private static void setUpdateFlag(boolean bFlag) {
		bUpdateFlag = bFlag;
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute backup
	 *
	 * @param: the backup content
	 *
	 * @return:the backup file name
	 *
	 * @throws: BeOperateException
	 */
	public static String execBackupScript(int iContent)
			throws BeOperateException {
		try {
			if (bBackupFlag) {
				String strBackupOnly = "adminBackupOnly";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupOnly));
			}

			setBackupFlag(true);

			String strErrMsg = "backup_in_progress";

			String strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackup.sh";

			// String strReturn = "";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot + "/ahBackup.sh "
					+ String.valueOf(iContent);

			// strReturn = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);
			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "/bin/ls ./backupdir";

			String strFileName = BeAdminCentOSTools.getOutStreamExecCmd(strCmd);

			if (strFileName.equals("")) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.execBackupScript(): backup data base, get file's name is null");

				setBackupFlag(false);

				String strBackupErr = "adminBackupErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupErr));
			}

			strCmd = "/bin/rm -rf ./backupdir";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			setBackupFlag(false);

			return strFileName;

		} catch (Exception ex) {

			setBackupFlag(false);

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.execBackupScript(): catch exception",
							ex);

			throw new BeOperateException(ex.getMessage());
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute get hivemanage log
	 *
	 * @param: null
	 *
	 * @return:the log file name
	 *
	 * @throws: BeOperateException
	 */
	public static String getlogExecCmd() throws BeOperateException {
		if (bGetlogFlag) {

			String strGetlogOnly = "adminGetlogOnly";

			throw new BeOperateException(HmBeResUtil.getString(strGetlogOnly));
		}

		setGetlogFlag(true);

		String strErrMsg = "getlog_in_progress";

		String strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
				+ "/ahPackLogs.sh";

		BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

		strCmd = "sh " + BeAdminCentOSTools.ahShellRoot + "/ahPackLogs.sh";

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

		if (strReturn.equals(strErrMsg)) {

			// add debug log
			DebugUtil
					.adminDebugInfo("BeOperateHMCentOSImpl.getlogExecCmd(): Getting the logs have a error");

			setGetlogFlag(false);

			String strBackupErr = "adminGetlogErr";

			throw new BeOperateException(HmBeResUtil.getString(strBackupErr));
		}

		String strFileName = "support_logs.tar.gz";

		setGetlogFlag(false);

		return strFileName;
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : get the boot information
	 *
	 * @param: null
	 *
	 * @return:List<BeRebootInfoDTO>, two partation information
	 *
	 * @throws: null
	 */
	public static List<BeRebootInfoDTO> getRebootInfo() {
		List<BeRebootInfoDTO> oInfoList = new ArrayList<BeRebootInfoDTO>();

		String strMountLabel = BeAdminCentOSTools
				.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);

		String strBootLabel;

		// get version file;
		String strVersion = "";
		float mainVersion = 0f;
		float subVersion = 0f;

		BeVersionInfo oVerInfo = NmsUtil
				.getVersionInfo(BeAdminCentOSTools.AH_NMS_VERSION_FILE);

		if (!"".equals(oVerInfo.getMainVersion())
				&& !"".equals(oVerInfo.getSubVersion())) {
			strVersion = oVerInfo.getMainVersion() + "r"
					+ oVerInfo.getSubVersion();
			mainVersion = Float.parseFloat(oVerInfo.getMainVersion());
			subVersion = Float.parseFloat(oVerInfo.getSubVersion());
		}

		String strMapVersion = "";
		
		File oFile = new File(BeAdminCentOSTools.AH_NMS_MAP_VERSION_FILE);

		if(oFile.exists() && oFile.isFile())
		{
			oVerInfo = NmsUtil
			.getVersionInfo(BeAdminCentOSTools.AH_NMS_MAP_VERSION_FILE);
		}
		else
		{
			oVerInfo = NmsUtil
			.getVersionInfo(BeAdminCentOSTools.AH_NMS_MAP_VERSION_FILE_NEW);
		}

		float mapMainVersion = 0f;
		float mapSubVersion = 0f;
		if (!"".equals(oVerInfo.getMainVersion())
				&& !"".equals(oVerInfo.getSubVersion())) {
			strMapVersion = oVerInfo.getMainVersion() + "r"
					+ oVerInfo.getSubVersion();
			mapMainVersion = Float.parseFloat(oVerInfo.getMainVersion());
			mapSubVersion = Float.parseFloat(oVerInfo.getSubVersion());
		}

		BeRebootInfoDTO oDataInfo = new BeRebootInfoDTO();

		BeRebootInfoDTO oMapDataInfo = new BeRebootInfoDTO();

		if(mapMainVersion < mainVersion || (mapMainVersion == mainVersion && mapSubVersion <= subVersion)){
			if (BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL.equals(strMountLabel))
				oMapDataInfo.setCanShow(true);
			else oDataInfo.setCanShow(true);
		}
		
		if (BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL.equals(strMountLabel)) {
			strBootLabel = BeAdminCentOSTools
					.getBootLabel(BeAdminCentOSTools.SYSTEM_BOOT_CONF);

			oDataInfo.setLabel(BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL);

			oMapDataInfo.setLabel(BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL);

			oDataInfo.setIsRunningSoft(true);

			oMapDataInfo.setIsRunningSoft(false);

			oDataInfo.setVersion(strVersion);

			oMapDataInfo.setVersion(strMapVersion);

			if (strBootLabel.equals(strMountLabel)) {
				oDataInfo.setIsBootLabel(true);

				oMapDataInfo.setIsBootLabel(false);
			} else {
				oDataInfo.setIsBootLabel(false);

				oMapDataInfo.setIsBootLabel(true);
			}

			oInfoList.add(oDataInfo);

			oInfoList.add(oMapDataInfo);

		} else {
			strBootLabel = BeAdminCentOSTools
					.getBootLabel(BeAdminCentOSTools.SYSTEM_MAP_BOOT_CONF);

			oDataInfo.setLabel(BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL);

			oMapDataInfo.setLabel(BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL);

			oDataInfo.setIsRunningSoft(false);

			oMapDataInfo.setIsRunningSoft(true);

			oDataInfo.setVersion(strMapVersion);

			oMapDataInfo.setVersion(strVersion);

			if (strBootLabel.equals(strMountLabel)) {
				oDataInfo.setIsBootLabel(false);

				oMapDataInfo.setIsBootLabel(true);
			} else {
				oDataInfo.setIsBootLabel(true);

				oMapDataInfo.setIsBootLabel(false);
			}

			oInfoList.add(oDataInfo);

			oInfoList.add(oMapDataInfo);
		}

		return oInfoList;
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : reboot system after modify label
	 *
	 * @param: strLable: name of label
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static void rebootSystemByLabel(String strLabel)
			throws BeOperateException {
		if(!HAadminTool.isHaModel())
		{
			if (!modifyBootLabel(strLabel)) {
				String strModifyLabelErr = "adminModifyLabelErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strModifyLabelErr));
			}
			
			String activeDBIp = "";
			if(isDBPartition()){
				activeDBIp = getDBServerIP();
				switchRemoteMachine(activeDBIp);
			}
			
			rebootSystem();
			if(activeDBIp.length() > 0)
				rebootDB(activeDBIp);
		}
		else
		{
			if(!HAadminTool.isValidMaster())
			{
//				String strExecErr = "adminHaErr";
//
//				throw new BeOperateException(HmBeResUtil.getString(strExecErr));

				String strMountLabel = BeAdminCentOSTools
				.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);


				if(strLabel.equalsIgnoreCase(strMountLabel))
				{
					rebootSystem();
				}
				else
				{
					String strExecErr = "adminHaErr";

					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
			}

			String strMountLabel = BeAdminCentOSTools
			.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);

			if(strLabel.equalsIgnoreCase(strMountLabel))
			{
				//same
				if(!execHaReboot())
				{
					String strExecErr = "adminHaRebootErr";

					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
			}
			else
			{
				//different
				if(!execHaRevert())
				{
                    String strExecErr = "adminHaRebootErr";

					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
			}
		}
	}

	public static void rebootDB(String desIp){
		String strCmd = "ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no " +
				desIp + " reboot ";
		BeAdminCentOSTools.exeSysCmd(strCmd);
	}
	
	public static void switchRemoteMachine(String desIp) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/ahSwitchRemoteMachine.sh",desIp};

		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || strRsltList.isEmpty())
    	{

			String strExecErr = "adminModifyLabelErr";

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
    	}

        String result = strRsltList.get(strRsltList.size()-1);

        if(strErrMsg.equals(result)){

			String strExecErr = "adminModifyLabelErr";

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

	}
	
	public static void fourNodeRebootSystemByLabel(String strLabel)	throws BeOperateException, InterruptedException {
		
			String strMountLabel = BeAdminCentOSTools
			.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);

			String dbactive = getTransIP("dbactive");
			String dbpassive = getTransIP("dbpassive");
			String apppassive = getTransIP("apppassive");
			String appactive = getTransIP("appactive");
			if(strLabel.equalsIgnoreCase(strMountLabel))
			{
				//same
				rebootAppPassive(dbactive);
				rebootAppPassive(dbpassive);
				Thread.currentThread().sleep(10000);
				rebootAppPassive(apppassive);
				rebootSystem();
				/*if(!execHaReboot())
				{
					String strExecErr = "adminHaRebootErr";

					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}*/
			}
			else
			{
				//different
				proRemotShell(dbpassive,"/HiveManager/script/shell/switchPartition.sh");
				proRemotShell(dbactive,"/HiveManager/script/shell/switchPartition.sh");
				Thread.currentThread().sleep(10000);
				proRemotShell(apppassive,"/HiveManager/script/shell/switchPartition.sh");
				proRemotShell(appactive,"/HiveManager/script/shell/switchPartition.sh");
				
				/*if(!execHaRevert())--
				{
                    String strExecErr = "adminHaRebootErr";

					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}*/
			}
		
	}
	
	/*
	 * @author xiaolanbao
	 *
	 * @description : modify the boot label
	 *
	 * @param: strLable: name of label
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	private static boolean modifyBootLabel(String strLabel) {
		String strMountLabel = BeAdminCentOSTools
				.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);

		String strBootFile;

		if (BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL.equals(strMountLabel)) {
			strBootFile = BeAdminCentOSTools.SYSTEM_BOOT_CONF;
		} else {
			strBootFile = BeAdminCentOSTools.SYSTEM_MAP_BOOT_CONF;
		}

		return BeAdminCentOSTools.modifyBootLabel(strLabel, strBootFile);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : modify the boot label for update
	 *
	 * @param: null
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	private static boolean setBootMapLabel() {
		String strMountLabel = BeAdminCentOSTools
				.getMountLabel(BeAdminCentOSTools.SYSTEM_FSTAB_FILE);

		String strBootFile;

		String strMapLabel;

		if (BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL.equals(strMountLabel)) {
			strBootFile = BeAdminCentOSTools.SYSTEM_BOOT_CONF;

			strMapLabel = BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL;
		} else {
			strBootFile = BeAdminCentOSTools.SYSTEM_MAP_BOOT_CONF;

			strMapLabel = BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL;
		}

		return BeAdminCentOSTools.modifyBootLabel(strMapLabel, strBootFile);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :execute the reboot system
	 *
	 * @param: null
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	private static boolean rebootSystem() {
		//String strCmd = "sleep 5";
		//BeAdminCentOSTools.exeSysCmd(strCmd);

		String strCmd = "reboot";

		return BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :execute the shutdown system
	 *
	 * @param: null
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static boolean shdownSystem() {
		String strCmd = "poweroff";

		return BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :create hivemanager root CA
	 *
	 * @param: BeRootCADTO, information of creating root ca
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static boolean createRootCA(BeRootCADTO oData) {
		String strFileName = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
		        +oData.getDomainName()
		        +BeAdminCentOSTools.AH_CERTIFICATE_HOME
				+ File.separator + BeAdminCentOSTools.AH_NMS_HM_CA_CONF;

		if (!BeAdminCentOSTools.createCAConf(oData, strFileName)) {
			return false;
		}

		if (!BeAdminCentOSTools.createCAKeyPsd(oData.getPassword(),oData.getDomainName()))
		{
			return false;
		}

		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot +"/ahCreateDomainRootCA.sh",
				         oData.getPassword(), oData.getKeySize(),
				         oData.getValidity(), oData.getDomainName()};

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		return !strReturn.equals(strErrMsg);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :create server csr
	 *
	 * @param: BeRootCADTO, information of creating csr
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static boolean createServerCSR(BeRootCADTO oData) {
		String strFileName = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                             +oData.getDomainName()
                             +BeAdminCentOSTools.AH_CERTIFICATE_HOME
				             + File.separator + BeAdminCentOSTools.AH_NMS_HM_CA_SERVER_CONF;

		if (!BeAdminCentOSTools.createCAConf(oData, strFileName)) {
			return false;
		}

		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;


		if ("".equalsIgnoreCase(oData.getPassword()))
		{
			String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/ahCreateDomainServerCsrNoPsd.sh",
					          oData.getKeySize(),oData.getFileName(),oData.getDomainName()};

			String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

			return !strReturn.equals(strErrMsg);
		}

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/ahCreateDomainServerCsr.sh",
				             oData.getPassword(),oData.getKeySize(),oData.getFileName(),oData.getDomainName()};

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		return !strReturn.equals(strErrMsg);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :sign server csr
	 *
	 * @param: BeRootCADTO, information of creating csr
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static boolean signServerCsr(BeRootCADTO oData, boolean bMeregeFlag)
			throws BeOperateException {
		String strPsd;

		strPsd = BeAdminCentOSTools.getCAKeyPsd(oData.getDomainName());

		if (null == strPsd) {

			String strSignErr = "adminSignCertErr";

			throw new BeOperateException(HmBeResUtil.getString(strSignErr));
		}

		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;


		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/ahCADomainServerCsr.sh",
				         strPsd,oData.getValidity(),oData.getFileName(),oData.getDomainName()};

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		if (strReturn.equals(strErrMsg))
		{
			return false;
		}

		if(bMeregeFlag)
		{
		//	strReturn="";

			if("".equalsIgnoreCase(oData.getPassword()))
			{
				String[] strCmdsm={"sh",BeAdminCentOSTools.ahShellRoot + "/ahMergeDomainKeyAndCert.sh",
						oData.getFileName()+"_cert.pem",oData.getFileName()+"_key.pem",
						oData.getFileName()+"_key_cert.pem",oData.getDomainName()};

				strReturn = BeAdminCentOSTools.execCmdWithErr(strCmdsm, strErrMsg);

				return !strReturn.equalsIgnoreCase(strErrMsg);
			}
			else
			{
                String[] strCmdsm={"sh",BeAdminCentOSTools.ahShellRoot + "/ahMergeDomainKeyAndCert.sh",
                		oData.getFileName()+"_cert.pem",oData.getFileName()+"_key.pem",
                		oData.getFileName()+"_key_cert.pem",oData.getPassword(),oData.getDomainName()};

				strReturn = BeAdminCentOSTools.execCmdWithErr(strCmdsm, strErrMsg);

				return !strReturn.equalsIgnoreCase(strErrMsg);
			}
		}

		return true;
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :get the file list of ca
	 *
	 * @param: null
	 *
	 * @return:List<String>:the list of file name
	 *
	 * @throws: null
	 */
	public static List<String> getCAFileList(String strDomainName) {
		try {
			List<String> oFileList = HmBeOsUtil
			.getFileNamesOfDirecotry(AhDirTools.getCertificateDir(strDomainName));

			List<String> oListTmp = new ArrayList<String>();

			if (null == oFileList) {
				return null;
			}

			for (String file : oFileList) {
				if (!file.endsWith(BeAdminCentOSTools.AH_NMS_HM_CSR_TAIL)
						&& !file
								.endsWith(BeAdminCentOSTools.AH_NMS_HM_PSD_TAIL)
						&& !file
								.endsWith(BeAdminCentOSTools.AH_NMS_HM_SRL_TAIL)
						&& !file.endsWith("conf")) {
					oListTmp.add(file);
				}
			}

			return oListTmp;
		} catch (Exception ex) {
			// add debug log
			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.getCAFileList() catch exception",
							ex);

			return null;
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :get the file info list of ca
	 *
	 * @param: null
	 *
	 * @return:list<BeCAFileInfo>
	 *
	 * @throws: null
	 */
	public static List<BeCAFileInfo> getCAFileInfoList(String strDomainName) {
		try {
			File oFileTmp = new File(BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                    +strDomainName+BeAdminCentOSTools.AH_CERTIFICATE_HOME);

			List<File> oFileInfoList = HmBeOsUtil.getFilesFromFolder(oFileTmp,
					false);

			List<BeCAFileInfo> oFileList = new ArrayList<BeCAFileInfo>();

			if (null == oFileInfoList) {
				return null;
			}

			for (File oTmp : oFileInfoList) {
				String strFileName = oTmp.getName();

				if (strFileName.endsWith(BeAdminCentOSTools.AH_NMS_HM_CSR_TAIL)
						|| strFileName
								.endsWith(BeAdminCentOSTools.AH_NMS_HM_PSD_TAIL)
						|| strFileName
								.endsWith(BeAdminCentOSTools.AH_NMS_HM_SRL_TAIL)
						|| strFileName.endsWith("conf")) {
					continue;
				}

				SimpleDateFormat stmp = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				String strFileTime = stmp.format(new Date(oTmp.lastModified()));

				double dFileSize = oTmp.length();

				NumberFormat nbf = NumberFormat.getInstance();

				nbf.setMaximumFractionDigits(2);

				nbf.setMaximumFractionDigits(2);

				String strFileSize = nbf.format(dFileSize / 1024);

				BeCAFileInfo oFileInfo = new BeCAFileInfo();

				oFileInfo.setFileName(strFileName);

				oFileInfo.setFileSize(strFileSize);

				oFileInfo.setCreateTime(strFileTime);

				oFileInfo.setDomainName(strDomainName);

				oFileList.add(oFileInfo);
			}

			return oFileList;
		} catch (Exception ex) {
			// add debug log
			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.getCAFileInfoList() catch exception",
							ex);

			return null;
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :remove the ca file
	 *
	 * @param: string: name of file
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
	public static boolean removeCAFile(String strFileName,String strDomainName) {
		String strFile = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
        +strDomainName+BeAdminCentOSTools.AH_CERTIFICATE_HOME
				+ File.separator + strFileName;

		try {
			return HmBeOsUtil.deletefile(strFile);
		} catch (Exception ex) {
			// add debuglog
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.removeCAFile() catch exception", ex);

			return false;
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :execute update script
	 *
	 * @param:null
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */

	private static boolean execUpdateScript(String strFile, int iContent)
			throws BeOperateException {
		try {
			String strErrMsg = "update_in_progress";

			String[] strCmdss={"sh",BeAdminCentOSTools.ahUpdateRoot+"/updateSoftware.sh",strFile,String.valueOf(iContent)};

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmdss, strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				setUpdateFlag(false);

				return false;
			}

			setUpdateFlag(false);

			return true;
		} catch (Exception ex) {

			BeUploadCfgTools.finishRunningFlag();

			setUpdateFlag(false);

			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.execUpdateScript() catch exception",
					ex);

			throw new BeOperateException(ex.getMessage());
		}
	}


	private static boolean execHaUpdate(int iContent)
	{
		 String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/haUpdate.sh",String.valueOf(iContent)};

	        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

	        if(null == strRsltList || 0 == strRsltList.size())
	    	{
	    		DebugUtil.adminDebugWarn(
				"BeOperateHMCentOSImpl.execHaUpdate() no return could not charge");

	    		return false;
	    	}

	        String strRslt = strRsltList.get(0);

	        int iRslt;

	    	try
	    	{
	    	    iRslt = Integer.parseInt(strRslt);
	    	}
	    	catch(Exception ex)
	    	{
	    		DebugUtil.adminDebugWarn(
				"BeOperateHMCentOSImpl.execHaUpdate() not get the integer result");

	    		return false;
	    	}

		return 0 == iRslt;
	}

	private static boolean execHaReboot()
	{
        String strShFile = "haReboot.sh";

    	return BeOperateHMCentOSImpl.isRslt_0(strShFile);
	}

	private static boolean execHaRevert()
	{
        String strShFile = "haRevert.sh";

    	return BeOperateHMCentOSImpl.isRslt_0(strShFile);
	}

	/*@author xiaolanbao
	 *
	 *@description:update GM
	 *
	 */
//	private static void execGmUpdate() throws BeOperateException
//	{
//		String strErrMsg = "error_gm_update";
//
//		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot+"/updateGM.sh"};
//
//		String strReturnMsg = BeAdminCentOSTools.execCmdIncludeErr(strCmds, strErrMsg);
//
//		if ("".equalsIgnoreCase(strReturnMsg)) {
//
//			return;
//		}
//
//		if(strErrMsg.length() < strReturnMsg.length())
//		{
//			setUpdateFlag(false);
//
//			throw new BeOperateException(strReturnMsg.substring(strReturnMsg.indexOf(strErrMsg)+strErrMsg.length()+1));
//		}
//	}


	/*
	 * @author xiaolanbao
	 *
	 * @description :execute update
	 *
	 * @param:null
	 *
	 * @return:null
	 *
	 * @throws: BeOperateException
	 */
	public static void execUpdate(String strFile, int iContent)
			throws BeOperateException {
		verifieyUpdatePacket(strFile);
		unzipUpdateImage(strFile,false);
		checkUpgradEnv();
		//for hm db sperate
		if((!NmsUtil.isHostedHMApplication()) && isDBPartition()){
			
			//transfer files to db server
			String serIP = getDBServerIP();
			transUpdateFiles(serIP);
			touchDBServerFile(serIP,"/HiveManager/tomcat/hm_soft_upgrade/db_master_ip");
			proRemotShell(serIP, "/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmDBExect.sh" + " " + iContent);
			
			boolean boo = BeAppProcessStatusUtil.waitProcess(serIP,"dbactive");
			if(boo){
	        }else{
	        	setUpdateFlag(false);
				BeUploadCfgTools.finishRunningFlag();
				throw new BeOperateException(HmBeResUtil.getString("adminUpgradeDBPassiveErr"));
	        }
			
			String[] strCmd={"sh",BeAdminCentOSTools.ahUpdateRoot+"/HiveManager/script/shell/haHmAppExect.sh"};
			
			String strErrMsgs = "update_in_progress";
			String strReturnMsgs = BeAdminCentOSTools.execCmdWithErr(strCmd,strErrMsgs);

			if (strReturnMsgs.equals(strErrMsgs)) {
				setUpdateFlag(false);

				String strExecErr = "adminExecUpdateErr";

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException(HmBeResUtil.getString(strExecErr));
			}
			
			
			//create tag for upgrade local schema
			File file = new File("/HiveManager/tomcat/hm_soft_upgrade/appserver");
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new BeOperateException(HmBeResUtil.getString("adminExecUpdateErr"));
				}
			}
			
		}else{
			String strUpdateSh = BeAdminCentOSTools.ahUpdateRoot+"/updateSoftware.sh";
			File fUpdate = new File(strUpdateSh);
			if(!fUpdate.exists())
			{
				setUpdateFlag(false);
				BeUploadCfgTools.finishRunningFlag();
				String strExecErr = "adminExecUpdateErr";
				throw new BeOperateException(HmBeResUtil.getString(strExecErr));
			}
	
			//judge run model
			if(!HAadminTool.isHaModel())
			{
				//normal
				if (!execUpdateScript(strFile, iContent)) {
					String strExecErr = "adminExecUpdateErr";
	
					setUpdateFlag(false);
	
					BeUploadCfgTools.finishRunningFlag();
	
					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
	
				File oFile = new File("/hivemap");
	
				if (oFile.exists() && oFile.isDirectory())
				{
					if (!setBootMapLabel()) {
						String strModifyLabelErr = "adminModifyLabelErr";
	
						setUpdateFlag(false);
	
						BeUploadCfgTools.finishRunningFlag();
	
						throw new BeOperateException(HmBeResUtil
								.getString(strModifyLabelErr));
					}
				}
	
	//			rebootSystem();
			}
			else
			{
			    //HA model
				if(!HAadminTool.isValidMaster())
				{
					String strExecErr = "adminHaErr";
	
					setUpdateFlag(false);
	
					BeUploadCfgTools.finishRunningFlag();
	
					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
	
				//do ha update
				if(!execHaUpdate(iContent))
				{
	                String strExecErr = "adminHaUpdateErr";
	
					setUpdateFlag(false);
	
					BeUploadCfgTools.finishRunningFlag();
	
					throw new BeOperateException(HmBeResUtil.getString(strExecErr));
				}
				
				File hivemap = new File("/hivemap");
				if (hivemap.exists() && hivemap.isDirectory()){
					File tFile = new File("/hivemap/HiveManager/tomcat/ha2nodes");
					try {
						tFile.createNewFile();
					} catch (IOException e) {
						throw new BeOperateException(HmBeResUtil.getString("adminExecUpdateErr"));
					}
				}
			}
		}
		//check update status
		HMUpdateStatus.setStatus(HMUpdateStatus.BACKUP_DATA);
	}

	public static boolean isDBPartition() throws BeOperateException{
		String strErrMsg = "update_in_progress";
		String filePath = BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/isDBPartition.sh";
		
		File file = new File(filePath);
		if(!file.exists()){
			filePath = BeAdminCentOSTools.ahShellRoot + "/isDBPartition.sh";
		}
		
		String[] strCmds={"sh",filePath};
		
		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || strRsltList.isEmpty())
    	{
        	setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
    	}

        String result = strRsltList.get(strRsltList.size()-1);

        if(strErrMsg.equals(result)){
			setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

        return "true".equals(result);
	}
	
	/**
	 * for hm-ha, 4 nodes
	 * @param strFile
	 * @param iContent
	 * @param haStatus
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date May 9, 2012
	 */
	public static void haHmExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException{
		//check updata status
		HMUpdateStatus.setStatus(HMUpdateStatus.CHECK_FILE);
		setUpdateFlag(true);
		unzipUpdateImage(strFile,true);
		checkImageBit(strFile);
		HMUpdateStatus.setStatus(HMUpdateStatus.CHECK_ENV);
		createIPFile();
		//db passive
		transAndExecDBPassive(iContent);

		//db active
        boolean boo = BeAppProcessStatusUtil.waitProcess(getDesIP("dbpassive"),"dbpassive");
        if(boo){
        	transAndExecDBActive(iContent);
        }else{
        	setUpdateFlag(false);
			BeUploadCfgTools.finishRunningFlag();
			throw new BeOperateException(HmBeResUtil.getString("adminUpgradeDBPassiveErr"));
        }

		
        //app passive
        boo = BeAppProcessStatusUtil.waitProcess(getDesIP("dbactive"),"dbactive");
        if(boo){
			transAndExecAppPassive(haStatus);
        }else{
        	setUpdateFlag(false);
			BeUploadCfgTools.finishRunningFlag();
			throw new BeOperateException(HmBeResUtil.getString("adminUpgradeDBActiveErr"));
        }

        //app active
        String appPassiveIP = getDesIP("apppassive");
        boo = BeAppProcessStatusUtil.waitProcess(appPassiveIP,"apppassive");
        if(boo){
			execAppActive();
        }else{
        	setUpdateFlag(false);
			BeUploadCfgTools.finishRunningFlag();
			throw new BeOperateException(HmBeResUtil.getString("adminUpgradeAppPassiveErr"));
        }

        rebootAppPassive(appPassiveIP);

		setUpdateFlag(false);
		HMUpdateStatus.setStatus(HMUpdateStatus.BACKUP_DATA);
	}

	private static void rebootAppPassive(String appPassiveIp){
		String strCmd = "ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o " +
				"IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no " + appPassiveIp +
				" reboot";
		BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	private static String getDesIP(String desType) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haHmGetRemoteIP.sh",desType};

		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || strRsltList.isEmpty())
    	{
        	setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
    	}

        String result = strRsltList.get(strRsltList.size()-1);

        if(strErrMsg.equals(result)){
			setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

        return result;
	}

	/**
	 * db separate, get db server ip
	 * @param desType
	 * @return
	 * @throws BeOperateException
	 */
	private static String getDBServerIP() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/ahSepDBSerIP.sh"};

		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || strRsltList.isEmpty())
    	{
        	setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
    	}

        String result = strRsltList.get(strRsltList.size()-1);

        if(strErrMsg.equals(result)){
			setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

        return result;
	}
	
	private static void execAppActive() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haHmAppExect.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	/**
	 * hm-ha 4 nodes
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date May 16, 2012
	 */
	private static void transAndExecAppPassive(int haStatus) throws BeOperateException{
		String passiveip = getDesIP("apppassive");
		transUpdateFiles(passiveip);

		proRemotShell(passiveip, "/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmAppExect.sh " + haStatus);
	}

	/**
	 * hm ha 4 nodes
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date May 14, 2012
	 */
	private static void transAndExecDBPassive(int haStatus) throws BeOperateException{
		String passiveip = getDesIP("dbpassive");
		transUpdateFiles(passiveip);
		//touchDBServerFile(passiveip);

		proRemotShell(passiveip, "/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmDBExect.sh" + " " + haStatus);

	}

	/**
	 * hm-ha 4 nodes
	 * @param tarFile
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date May 9, 2012
	 */
	private static void transAndExecDBActive(int haStatus) throws BeOperateException{
		String activeip = getDesIP("dbactive");
		transUpdateFiles(activeip);
		touchDBServerFile(activeip,"/HiveManager/tomcat/hm_soft_upgrade/db_master_ip");

		proRemotShell(activeip, "/HiveManager/tomcat/hm_soft_upgrade/HiveManager/script/shell/haHmDBExect.sh" + " " + haStatus);
	}

	private static void proRemotShell(String desIP,String filePath){
		String strCmd = "ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o " +
				"IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no " + desIP + " " + filePath;

		BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	private static void createIPFile() throws BeOperateException{
		haHmCreateIPFile("appmaster",getTransIP("appactive"),"/HiveManager/tomcat/hm_soft_upgrade/dbserver");
		haHmCreateIPFile("dbpassive",getTransIP("dbpassive"),"/HiveManager/tomcat/hm_soft_upgrade/dbserver");
		haHmCreateIPFile("dbactive",getTransIP("dbactive"),"/HiveManager/tomcat/hm_soft_upgrade/dbserver");
		haHmCreateIPFile("apppassive",getTransIP("apppassive"),"/HiveManager/tomcat/hm_soft_upgrade/dbserver");

		haHmCreateIPFile("appmaster",getTransIP("appactive"),"/HiveManager/tomcat/hm_soft_upgrade/appserver");

	}

	/**
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date May 15, 2012
	 */
	private static void haHmCreateIPFile(String serverName,String serverIP,String filePath) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haHmCreateIPFile.sh",serverName,serverIP,filePath};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	/**
	 * touch file at db server
	 * @param desIP
	 * @return void
	 * @author fhu
	 * @date May 15, 2012
	 */
	private static void touchDBServerFile(String desIP,String file){
		String strCmd = "ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o " +
				"IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no " + desIP +
				" touch " + file;
		BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	/**
	 * transfer update files to target machine
	 * @param desIP  target IP
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date May 15, 2012
	 */
	private static void transUpdateFiles(String desIP) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haHmSshTransfer.sh",desIP};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}



	private static String getTransIP(String param) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haHmGetTransIP.sh",param};

		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || strRsltList.isEmpty())
    	{
        	setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
    	}

        String result = strRsltList.get(strRsltList.size()-1);

        if(strErrMsg.equals(result)){
			setUpdateFlag(false);

			String strExecErr = "adminGetIPErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

        return result;
	}

	public static void haExecUpdate(String strFile,int iContent,int haStatus) throws BeOperateException{
		verifieyUpdatePacket(strFile);
		unzipUpdateImage(strFile,true);
		checkImageBit(strFile);
		checkUpgradEnv();
				
		if(haStatus == HAStatus.STATUS_HA_MASTER){
			haCreateNewDB();
			haBackUp(iContent);
			//haRestoreDB();
		}else{
			haResetLocalSchema();
		}

		resetCapwapDBConn();

		haExecUpgradeFile(strFile,iContent);

		resetHADBConn();

		setUpdateFlag(false);
		//check updata status
		HMUpdateStatus.setStatus(HMUpdateStatus.BACKUP_DATA);
	}

	private static void haResetLocalSchema() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haResetLocalSchema.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	private static void resetHADBConn() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haResetHADBConn.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	private static void resetCapwapDBConn() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haResetCapwapDBConn.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	/**
	 * copy file and install rpm
	 * @return void
	 * @author fhu
	 * @date Feb 21, 2012
	 */
	private static void haExecUpgradeFile(String strFile,int iContent) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haExecUpdate.sh",strFile,String.valueOf(iContent)};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}

	}

	/**
	 * delete image and check LONG_BIT
	 * @param strFile image path
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 21, 2012
	 */
	private static void checkImageBit(String strFile) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haCheckImageBit.sh",strFile};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}
	
	private static void checkUpgradEnv() throws BeOperateException{
		//public check
		String[] checkCmd={"sh",BeAdminCentOSTools.ahUpdateRoot+"/HiveManager/script/shell/ahUpgradeCheck.sh"};
		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(checkCmd);
		String result = null;
		if(strRsltList.size() > 0){
			result = strRsltList.get(strRsltList.size()-1);
		}
		if (null != result && result.length() > 20) {
			setUpdateFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(result);
		}
		HMUpdateStatus.setStatus(HMUpdateStatus.CHECK_ENV);
	}
	
	private static void checkDiskFreeSpace() throws BeOperateException{
		//public check
		String[] checkCmd={"sh",BeAdminCentOSTools.ahShellRoot+"/ahCheckDiskSize.sh"};
		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(checkCmd);
		String result = null;
		if(strRsltList.size() > 0){
			result = strRsltList.get(strRsltList.size()-1);
		}
		if (null != result && result.length() > 20) {
			setBackupFlag(false);
			BeUploadCfgTools.finishRunningFlag();
			throw new BeOperateException(getFormatErrorMsg(result));
		}
	}
	
	
	private static String getFormatErrorMsg(String result){
		if(StringUtils.isBlank(result)){
			return "";
		}
		String[] msgs=result.split("&&");
		return msgs[0];
	}

	public static void haRestoreDB() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haRestoreDB.sh",
				"/HiveManager/tomcat/hm_soft_upgrade/hm/WEB-INF/classes"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminRestoreErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}


	/**
	 *
	 * @param iContent backup type
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 21, 2012
	 */
	public static void haBackUp(int iContent) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haBackup.sh",String.valueOf(iContent)};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	/**
	 * create new db from image
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 17, 2012
	 */
	private static void haCreateNewDB() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haCreateNewDB.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	/**
	 * unzip, HA model
	 * @param strFile
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 16, 2012
	 */
	private static void unzipUpdateImage(String strFile,boolean isHaApp) throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/tarUpdatePack.sh",strFile};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setUpdateFlag(false);

			String strExecErr = "adminExecUpdateUnzipErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
		HMUpdateStatus.setStatus(HMUpdateStatus.UNTAR_FILE);
		if(!isHaApp){
			File oFile = new File(AH_SIGN_IMAGE_PUBLIC_KEY);
			if(oFile.exists() && oFile.isFile())
			{
				if(!isExactVersion())
				{
					setUpdateFlag(false);
		
					BeUploadCfgTools.finishRunningFlag();
		
					String strbadVer = "adminUpdateBadVersion";
		
					throw new BeOperateException(HmBeResUtil
							.getString(strbadVer));
				}
			}
		}
	}

	/**
	 * verify image and updateflag
	 * @param strFile
	 * @throws BeOperateException
	 * @return void
	 * @author fhu
	 * @date Feb 16, 2012
	 */
	public static void verifieyUpdatePacket(String strFile) throws BeOperateException{
		File oFile = new File(AH_SIGN_IMAGE_PUBLIC_KEY);
		//sign check
		if(oFile.exists() && oFile.isFile())
		{
		  	if(!verifyUpdatePacket(strFile))
		  	{
		  		String strExecErr = "adminExecUpdateErr";

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		  	}
		}
		//check updata status
		HMUpdateStatus.setStatus(HMUpdateStatus.CHECK_FILE);
		if (bUpdateFlag) {
			String strAdminOnly = "adminUpdateOnly";

			throw new BeOperateException(HmBeResUtil
					.getString(strAdminOnly));
		}
		setUpdateFlag(true);
	}
	/*
	 * @author xiaolanbao
	 *
	 * @description :execute restore script
	 *
	 * @param:String strFileName
	 *
	 * @return:null
	 *
	 * @throws: null
	 */
	public static void execRestoreScript(String strFile)
			throws BeOperateException {
		try {
			if (bRestoreFlag) {
				String strAdminOnly = "adminRestoreOnly";

				throw new BeOperateException(HmBeResUtil
						.getString(strAdminOnly));
			}

			setRestoreFlag(true);

			String strErrMsg = "restore_in_progress";

//			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//					+ "/ahRestoreBeforeReboot.sh " + "\""+strFile+"\"";

			String[] strCmds = {"sh",BeAdminCentOSTools.ahShellRoot+ "/ahRestoreBeforeReboot.sh", strFile};

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				setRestoreFlag(false);

				String strRestoreErr = "adminRestoreErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			setRestoreFlag(false);

			rebootSystem();
		} catch (BeOperateException ex) {
			setRestoreFlag(false);

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.execRestoreScript() catch BeOperateException",
							ex);

			throw new BeOperateException(ex.getMessage());
		} catch (Exception ex) {
			setRestoreFlag(false);

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.execRestoreScript() catch exception",
							ex);

			throw new BeOperateException(ex.getMessage());
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :execute restort soft
	 *
	 * @param:null
	 *
	 * @return:null
	 *
	 * @throws: null
	 */
	public static boolean execRestartSoft() {
		try {
			String strErrMsg = "restart_in_progress";

			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahRestartSoft.sh";

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.execRestartSoft() restart operation return message: "
								+ strReturnMsg);

				return false;
			}

			return true;
		} catch (Exception ex) {
			// add log
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.execRestartSoft() catch exception",
					ex);

			return false;
		}
	}

	public static boolean execShutdownSoft() {
		try {
			String strErrMsg = "shutdown_in_progress";

			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/stopHiveManage.sh";

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.execShutdownSoft() shutdown operation return message: "
								+ strReturnMsg);

				return false;
			}

			return true;
		} catch (Exception ex) {
			// add log
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.execShutdownSoft() catch exception",
					ex);

			return false;
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description :execute clean db
	 *
	 * @param:null
	 *
	 * @return:null
	 *
	 * @throws: null
	 */
	public static boolean execCleanDB() {
		try {
			String strErrMsg = "clean_in_progress";

			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahCleanDB.sh";

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.execRestartSoft() cleandb operation return message: "
								+ strReturnMsg);

				return false;
			}

			return true;
		} catch (Exception ex) {
			// add log
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.execCleanDB() catch exception", ex);

			return false;
		}
	}

	/*
	 * @author:xiaolanbao
	 *
	 * @description :is there root ca files
	 *
	 * @param:null
	 *
	 * @return:bolean
	 *
	 * @throws: null
	 */
	public static boolean IsRootCAExist(String strDomainName) {
		String strFileName = BeAdminCentOSTools.AH_NMS_ROOT_CA_NAME;

		return IsCAFileExist(strFileName,strDomainName);
	}

	/*
	 * @author:xiaolanbao
	 *
	 * @description :is there ca files
	 *
	 * @param:the name of file
	 *
	 * @return:bolean
	 *
	 * @throws: null
	 */
	public static boolean IsCAFileExist(String strFileName,String strDomainName) {
		return IsFileExist(strFileName, AhDirTools.getCertificateDir(strDomainName));
	}

	private static boolean IsFileExist(String strFileName, String strPath) {
		String strFile = strPath.endsWith(File.separator) ? strPath + strFileName : strPath + File.separator + strFileName;

		File oFile = new File(strFile);

		try {
			return oFile.exists();
		} catch (Exception ex) {
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.IsFileExist() catch exception", ex);

			return false;
		}
	}

	/*
	 * @author:xiaolanbao
	 *
	 * @description :cp file from scpserver
	 *
	 * @param:struc for scpserverinfo
	 *
	 * @return:bolean
	 *
	 * @throws: null
	 */
	public static boolean getFileFromScpServer(BeScpServerInfo oVerInfo)
			throws BeOperateException {
		// long lSize = AhScpUtil.getFileSizeFromSftp(oVerInfo.getScpIp(),
		// Integer.parseInt(oVerInfo.getScpPort()),
		// AhScpUtil.DEFAULT_CONNECTION_TIMEOUT, oVerInfo.getScpUsr(),
		// oVerInfo.getScpPsd(), oVerInfo.getFilePath());

		long lSize = 0;

		File oFile = new File(oVerInfo.getFilePath());

		String strFileName = oFile.getName();

		BeUploadCfgInfo cfgInfo = new BeUploadCfgInfo();
		cfgInfo.setType(BeUploadCfgInfo.AH_UPLOAD_TYPE_UPDATE);
		cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNING_TRUE);
		cfgInfo.setLocation(BeUploadCfgInfo.AH_UPLOAD_LOCATION_REMOTE);
		cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_FALSE);
		cfgInfo.setName(strFileName);
		cfgInfo.setSize(String.valueOf(lSize));

		Connection conn = null;
		try {

			// AhScpUtil.scpGet(oVerInfo.getScpIp(), Integer.parseInt(oVerInfo
			// .getScpPort()), AhScpUtil.DEFAULT_CONNECTION_TIMEOUT,
			// oVerInfo.getScpUsr(), oVerInfo.getScpPsd(), oVerInfo
			// .getFilePath(),
			// BeAdminCentOSTools.Ah_NMS_DOWNLOADS_ROOT);
			//

			conn = new Connection(oVerInfo.getScpIp(), Integer
					.parseInt(oVerInfo.getScpPort()));
			conn.connect(null, 10000, 0);

			boolean isAuthenticated = conn.authenticateWithPassword(oVerInfo
					.getScpUsr(), oVerInfo.getScpPsd());
			if (!isAuthenticated) {
				throw new IOException("Authentication failed.");
			}

			HmBeAdminUtil.initUploadCfg(cfgInfo);

			SCPClient client = new SCPClient(conn);
			client.get(oVerInfo.getFilePath(),
					BeAdminCentOSTools.Ah_NMS_DOWNLOADS_ROOT);

			cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
			HmBeAdminUtil.initUploadCfg(cfgInfo);

			return true;
		} catch (Exception ex) {
			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.getFileFromScpServer() catch exception",
							ex);

			cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
			cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNINF_FALSE);
			HmBeAdminUtil.initUploadCfg(cfgInfo);

			String strPermission = "adminGetScpFileErr";

			throw (new BeOperateException(HmBeResUtil.getString(strPermission)
					+ " " + ex.getMessage()));
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/*
	 * @author:xiaolanbao
	 *
	 * @description :execute instal certificate for ssl in tomcat
	 *
	 * @param:strServerCertName-the name of server certificate,
	 * strServerKeyName-the name of server private key , strKeyPsd- the password
	 * of the key file.
	 *
	 * @return:bolean
	 *
	 * @throws: BeOperateException
	 */
	public static boolean execInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException {
		String strErrMsg = "install_ssl_err";

//		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//				+ "/ahInstallKeystore.sh " + "\""+strServerCertName+"\"" + " "
//				+ "\""+strServerKeyName+"\"" + " " + strKeyPsd;

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahInstallKeystore.sh",strServerCertName,strServerKeyName,strKeyPsd};

		try {
			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {

				String strAdminInstallCertErr = "adminInstallCertErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strAdminInstallCertErr));
			}

			return true;
		} catch (BeOperateException ex) {
			// add system log

			throw new BeOperateException(ex.getMessage());
		} catch (Exception ex) {
			// add debug log
			throw new BeOperateException(ex.getMessage());
		}
	}

	public static boolean execOemInstallCert(String strServerCertName,
			String strServerKeyName, String strKeyPsd)
			throws BeOperateException {
		String strErrMsg = "install_ssl_err";

//		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//				+ "/ahInstallKeystore.sh " + "\""+strServerCertName+"\"" + " "
//				+ "\""+strServerKeyName+"\"" + " " + strKeyPsd;

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahOemInstallKeystore.sh",strServerCertName,strServerKeyName,strKeyPsd};

		try {
			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {

				String strAdminInstallCertErr = "adminInstallCertErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strAdminInstallCertErr));
			}

			return true;
		} catch (BeOperateException ex) {
			// add system log

			throw new BeOperateException(ex.getMessage());
		} catch (Exception ex) {
			// add debug log
			throw new BeOperateException(ex.getMessage());
		}
	}
	
	/*
	 * @author:xiaolanbao
	 *
	 * @description :execute auto install keystore file
	 *
	 * @param:-null
	 *
	 * @return:bolean
	 *
	 * @throws: BeOperateException
	 */
	public static boolean execInstallCertAuto() throws BeOperateException {
		String strErrMsg = "install_ssl_err";

		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
				+ "/ahCreateKeystore.sh ";

		try {
			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {

				String strAdminInstallCertErr = "adminInstallCertErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strAdminInstallCertErr));
			}

			return true;
		} catch (BeOperateException ex) {
			// add system log

			throw new BeOperateException(ex.getMessage());
		} catch (Exception ex) {
			// add debug log
			throw new BeOperateException(ex.getMessage());
		}
	}

	/*
	 * @author:xiaolanbao
	 *
	 * @description :get the install keystore file information
	 *
	 * @param:-null
	 *
	 * @return:List<string>
	 *
	 * @throws: BeOperateException
	 */
	public static List<String> getKeystoreInfo() {
		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
				+ "/ahGetKeystoreInfo.sh";

		return BeAdminCentOSTools.getOutStreamsExecCmd(strCmd);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : set the syslog server info
	 *
	 * @param: BeLogServerInfo:the information of syslog server
	 *
	 * @return: boolean
	 *
	 * @throws: null
	 */
	public static boolean setLogServer(BeLogServerInfo oData) {
		if (null == oData) {
			return false;
		}

		String strErrMsg = "set_longng-err";

		String strReturnMsg;

		if (!oData.getIsLogServer()) {
			// set disable logserver

			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahSetDisableLogServer.sh ";

			strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			return !strErrMsg.equals(strReturnMsg);
		}

		if (oData.getIsFullNet() || 0 == oData.getSubNet().size()) {
			// set full net log server

			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahSetFullNetLogServer.sh ";

			strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			return !strErrMsg.equals(strReturnMsg);
		}

		String strLogFilter = "filter f_net { ";

		strLogFilter = strLogFilter + "netmask(" + oData.getSubNet().get(0)
				+ ")";

		for (int i = 1; i < oData.getSubNet().size(); ++i) {
			strLogFilter = strLogFilter + " or netmask("
					+ oData.getSubNet().get(i) + ")";
		}

		strLogFilter = strLogFilter + "; };";

		// set the subnet to log server
		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
				+ "/ahSetSubNetLogServer.sh " + strLogFilter;

		strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

		return !strErrMsg.equals(strReturnMsg);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : get the syslog server info
	 *
	 * @param: null
	 *
	 * @return: BeLogServerInfo: the information of syslog server
	 *
	 * @throws: null
	 */
	public static BeLogServerInfo getLogServerInfo() {
		BeLogServerInfo oData = new BeLogServerInfo();

		File fConf = new File(BeAdminCentOSTools.AH_NMS_LOGSERVER_CONF_FILE);

		if (!fConf.exists()) {
			return oData;
		}

		try {
			FileReader frConf = new FileReader(fConf);

			BufferedReader brConf = new BufferedReader(frConf);

			String strTmp;

			while ((strTmp = brConf.readLine()) != null) {
				strTmp = strTmp.trim();

				if ("".equals(strTmp)) {
					continue;
				}

				int iTmp = strTmp
						.indexOf(BeAdminCentOSTools.AH_NMS_LOGCONF_DEFAULT_FLAG);

				if (-1 != iTmp) {
					oData.setIsLogServer(false);

					brConf.close();

					frConf.close();

					return oData;
				}

				iTmp = strTmp
						.indexOf(BeAdminCentOSTools.AH_NMS_LOGCONF_NET_FLAG);

				if (-1 != iTmp) {
					brConf.close();

					frConf.close();

					return BeAdminCentOSTools.analyseNetSting(strTmp);
				}
			}

			brConf.close();

			frConf.close();

			return oData;
		} catch (Exception ex) {
			// add log
			DebugUtil
					.adminDebugWarn(
							"BeAdminCentOSTools.getLogServerInfo() catch exception is: ",
							ex);

			return oData;
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute domain backup
	 *
	 * @param: iContent:the flag about backup content; oDomain: the domain of
	 * backup
	 *
	 * @return:the backup file name
	 *
	 * @throws: BeOperateException
	 */
	public static String backupDomainData(HmDomain oDomain, int iContent)
			throws BeOperateException {
		try {
			if (null == oDomain.getId() || null == oDomain.getDomainName()
					|| "".equalsIgnoreCase(oDomain.getDomainName())) {
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.backupDomainData(): Domain id or name is error!");

				String strBackupErr = "adminBackupErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupErr));
			}

			if (bBackupFlag) {
				String strBackupOnly = "adminBackupOnly";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupOnly));
			}

			setBackupFlag(true);

			String strErrMsg = "backup_in_progress";

			String strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupDomainData.sh";

			// String strReturn = "";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

//			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//					+ "/ahBackupDomainData.sh "
//					+ String.valueOf(oDomain.getId()) + " "
//					+ "\""+oDomain.getDomainName()+"\"" + " " + String.valueOf(iContent);

			String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/ahBackupDomainData.sh",String.valueOf(oDomain.getId()),oDomain.getDomainName(),String.valueOf(iContent)};

			// strReturn = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);
			BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

			strCmd = "/bin/ls ./backupdir";

			String strFileName = BeAdminCentOSTools.getOutStreamExecCmd(strCmd);

			if (strFileName.equals("")) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.backupDomainData(): backup data base, get file's name is null");

				setBackupFlag(false);

				String strBackupErr = "adminBackupErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupErr));
			}

			strCmd = "/bin/rm -rf ./backupdir/" + strFileName;

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			setBackupFlag(false);

			return strFileName;

		} catch (Exception ex) {

			setBackupFlag(false);

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.backupDomainData(): catch exception",
							ex);

			throw new BeOperateException(ex.getMessage());
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute backup
	 *
	 * @param: the backup content
	 *
	 * @return:the backup file name
	 *
	 * @throws: BeOperateException
	 */
	public static String backupFullData(int iContent) throws BeOperateException {
		try {
			if (bBackupFlag) {
				String strBackupOnly = "adminBackupOnly";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupOnly));
			}

			setBackupFlag(true);
			
			checkDiskFreeSpace();

			String strErrMsg = "backup_in_progress";

			String strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupFullData.sh";

			// String strReturn = "";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupFullData.sh " + String.valueOf(iContent);

			// strReturn = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);
			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "/bin/ls ./backupdir";

			String strFileName = BeAdminCentOSTools.getOutStreamExecCmd(strCmd);

			if (strFileName.equals("")) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.backupFullData(): backup data base, get file's name is null");

				setBackupFlag(false);

				String strBackupErr = "adminBackupErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupErr));
			}

			strCmd = "/bin/rm -rf ./backupdir/" + strFileName;

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			setBackupFlag(false);

			return strFileName;

		} catch (Exception ex) {

			setBackupFlag(false);

			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.backupFullData(): catch exception",
					ex);

			throw new BeOperateException(ex.getMessage());
		}
	}

	/**
	 * backup use pg_dump
	 * @param iContent
	 * @return String
	 * @author fhu
	 * @date Feb 24, 2012
	 */
	public static String haBackupFullDataDump(int iContent) throws BeOperateException{
		try {
			if (bBackupFlag) {
				String strBackupOnly = "adminBackupOnly";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupOnly));
			}

			setBackupFlag(true);
			
			checkDiskFreeSpace();
			
			String strErrMsg = "backup_in_progress";

			String strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/ahBackupInit.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "chmod u+x " + BeAdminCentOSTools.ahShellRoot
					+ "/haBackupFullDataDump.sh";

			// String strReturn = "";

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
					+ "/haBackupFullDataDump.sh ";

			// strReturn = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);
			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			strCmd = "/bin/ls ./backupdir";

			String strFileName = BeAdminCentOSTools.getOutStreamExecCmd(strCmd);

			if (strFileName.equals("")) {
				// add log
				DebugUtil
						.adminDebugInfo("BeOperateHMCentOSImpl.backupFullData(): backup data base, get file's name is null");

				setBackupFlag(false);

				String strBackupErr = "adminBackupErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strBackupErr));
			}

			strCmd = "/bin/rm -rf ./backupdir/" + strFileName;

			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

			setBackupFlag(false);

			return strFileName;
		}catch (Exception ex) {

			setBackupFlag(false);

			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.backupFullData(): catch exception",
					ex);

			throw new BeOperateException(ex.getMessage());
		}

	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute restore domain data
	 *
	 * @param: domain and store content
	 *
	 * @return:null
	 *
	 * @throws: BeOperateException
	 */
	public static void restoreDomainData(String strFile, HmDomain oDomain)
			throws BeOperateException {
		try {
			if (null == oDomain || null == oDomain.getId()) {
				DebugUtil
						.adminDebugWarn("BeOperateHMCentOSImpl.restoreDomainData(): the parameter is error.");

				BeUploadCfgTools.finishRunningFlag();

				return;
			}

			if (bRestoreFlag) {
				String strAdminOnly = "adminRestoreOnly";

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException(HmBeResUtil
						.getString(strAdminOnly));
			}

			setRestoreFlag(true);

			HmDomain oBo = QueryUtil.findBoById(HmDomain.class,
					oDomain.getId());

			if (null == oBo) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException("The VHM is not exist!");
			}

			String strErrMsg = "restore_in_progress";

//			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//					+ "/ahUnzipRestoreFile.sh " + "\""+strFile+"\"";

			String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahUnzipRestoreFile.sh",strFile};

			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDataErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			String strVersionFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/"
					+ "hivemanager.ver";

			File oVersionFile = new File(strVersionFile);

			if (!oVersionFile.exists()) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDataErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			String strDomainFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/"
					+ "hm_domain.xml".toLowerCase();

			File oDomainFile = new File(strDomainFile);

			if (!oDomainFile.exists()) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDataErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			String strRestoreDomainName=getResoteDomainName();

			if ((null == strRestoreDomainName) || "".equalsIgnoreCase(strRestoreDomainName)  ) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDomainErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			// set domain status (reset domain run status before remove domain operation!)
			oBo = QueryUtil.findBoById(HmDomain.class, oDomain
					.getId());

			oBo.setRunStatus(HmDomain.DOMAIN_RESTORE_STATUS);

//			oBo = (HmDomain) QueryUtil.updateBo(oBo);
			BoMgmt.getDomainMgmt().updateDomain(oBo);

			// delete the data belong domain except domain content
			BoMgmt.getDomainMgmt().removeDomain(oDomain.getId(), false);

			setRestoreFlag(false);

			CacheMgmt.getInstance().updateHmDomainCache(oBo);

			String[] strCmdss={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahRestoreDomainData.sh",oBo.getDomainName(),String.valueOf(oBo.getId()), strRestoreDomainName};

			strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmdss, strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDataErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			oBo = QueryUtil.findBoById(HmDomain.class, oDomain
					.getId());

			oBo.setRunStatus(HmDomain.DOMAIN_DEFAULT_STATUS);

			oBo = QueryUtil.updateBo(oBo);

			// reset the CAPWAP status on HiveAP;
			// Do not reset the status, since it's already down when restore.
			// try {
			// BoMgmt.getHiveApMgmt().resetCapwapStatus(oBo.getId());
			// } catch (Exception e) {
			// DebugUtil.adminDebugError(
			// "reset CAPWAP status of HiveAP on domain:"
			// + oBo.getDomainName() + " error.", e);
			// }
			// reset the CAPWAP link up event to link down;
			try {
				BoMgmt.getHiveApMgmt().resetConnectStatusViaCAPWAP(oBo.getId());
			} catch (RuntimeException e1) {
				DebugUtil.adminDebugError(
						"reset CAPWAP Status of HiveAP on domain:"
								+ oBo.getDomainName() + " error.", e1);
			}

			try {
				BoMgmt.getTrapMgmt().setCapwapAlarm2LinkDownByDomain(
						oBo.getId());
			} catch (RuntimeException e) {
				DebugUtil.adminDebugError(
						"reset Alarm link up status of HiveAP on domain:"
								+ oBo.getDomainName() + " error.", e);
			}

			CacheMgmt.getInstance().updateHmDomainCache(oBo);
			// re-initialize the Cache values;
			CacheMgmt.getInstance().initCacheValues(oBo.getId());
			// initialize home domain caches
			if (!HmDomain.HOME_DOMAIN.equals(oBo.getDomainName())) {
				CacheMgmt.getInstance().initCacheValues(
						BoMgmt.getDomainMgmt().getHomeDomain().getId());
			}
			// Initialize map hierarchy cache
			BoMgmt.getMapHierarchyCache().init();
			// Send HiveAp DTLS Parameters configuration
			BeTopoModuleUtil.sendHiveApDTLSParamConfig(oBo.getId());
			// Send HiveAp Simulate Parameters configuration
			BeTopoModuleUtil.sendHiveApSimulateConfig(oBo.getId());
		} catch (BeOperateException ex) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			HmDomain oBo = QueryUtil.findBoById(HmDomain.class,
					oDomain.getId());

			if (null != oBo) {
				try {
					oBo.setRunStatus(HmDomain.DOMAIN_DEFAULT_STATUS);

					oBo = QueryUtil.updateBo(oBo);

					CacheMgmt.getInstance().updateHmDomainCache(oBo);
				} catch (Exception dbex) {
					DebugUtil.adminDebugWarn(dbex.getMessage());
				}
			}

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.execRestoreScript() catch BeOperateException",
							ex);

			throw new BeOperateException(ex.getMessage());
		} catch (Exception ex) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			HmDomain oBo = QueryUtil.findBoById(HmDomain.class,
					oDomain.getId());

			if (null != oBo) {
				try {
					oBo.setRunStatus(HmDomain.DOMAIN_DEFAULT_STATUS);

					oBo = QueryUtil.updateBo(oBo);

					CacheMgmt.getInstance().updateHmDomainCache(oBo);
				} catch (Exception dbex) {
					DebugUtil.adminDebugWarn(dbex.getMessage());
				}
			}

			DebugUtil
					.adminDebugWarn(
							"BeOperateHMCentOSImpl.execRestoreScript() catch exception",
							ex);

			throw new BeOperateException(ex.getMessage());
		}
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute restore all data
	 *
	 * @param: store content
	 *
	 * @return:null
	 *
	 * @throws: BeOperateException
	 */
	public static void restoreFullData(String strFile,String restoreType)
			throws BeOperateException {
		if (bRestoreFlag) {

			BeUploadCfgTools.finishRunningFlag();

			String strAdminOnly = "adminRestoreOnly";

			throw new BeOperateException(HmBeResUtil.getString(strAdminOnly));
		}

		setRestoreFlag(true);

		String strErrMsg = "restore_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahUnzipRestoreFile.sh",strFile};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
				strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			String strRestoreErr = "adminRestoreDataErr";

			throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
		}

/*		if("dump".equals(restoreType)){
			String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
			File file = new File(checkFile);
			if(!file.exists()){
				setRestoreFlag(false);
				throw new BeOperateException(HmBeResUtil.getString("adminRestoreDumpErr"));
			}
		}else{*/
		
		String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
		File file = new File(checkFile);
		if(!file.exists()){
			/*String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
			File file = new File(checkFile);
			if(file.exists()){
/*				setRestoreFlag(false);
				throw new BeOperateException(HmBeResUtil.getString("adminRestoreDumpErr"));
			}*/
			// judge the packet is right
			String strVersionFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/"
					+ "hivemanager.ver";

		File oVersionFile = new File(strVersionFile);

		if (oVersionFile.exists()) {

			String strRestoreDomainName=getResoteDomainName();

			if((null==strRestoreDomainName)||"".equalsIgnoreCase(strRestoreDomainName))
			{
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				String strRestoreErr = "adminRestoreDataErr";

				throw new BeOperateException(HmBeResUtil
						.getString(strRestoreErr));
			}

			if(!"home".equalsIgnoreCase(strRestoreDomainName))
			{
				String[] strCmds1={"sh",BeAdminCentOSTools.ahShellRoot+ "/mvDomainToHome.sh", strRestoreDomainName};

				String strReturnMsg1 = BeAdminCentOSTools.execCmdWithErr(strCmds1,
						strErrMsg);

				if (strReturnMsg1.equals(strErrMsg)) {
					setRestoreFlag(false);

					BeUploadCfgTools.finishRunningFlag();

					String strRestoreErr = "adminRestoreDataErr";

						throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
					}
				}
			}
		}

		if(file.exists()){
			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot + "/ahCompareRestoreVersion.sh";
			String strReturnMsg1 = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg1.equals(strErrMsg)) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException(HmBeResUtil.getString("adminRestoreVersionErr"));
			}
		}
		
		// call script to restore the data
		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
				+ "/ahRestoreFullData.sh";

		strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			String strRestoreErr = "adminRestoreDataErr";

			throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
		}

		setRestoreFlag(false);

		rebootSystem();
	}

	public static void haRestoreFullData(String strFile,String restoreType)
			throws BeOperateException {
		if (bRestoreFlag) {

			BeUploadCfgTools.finishRunningFlag();

			String strAdminOnly = "adminRestoreOnly";

			throw new BeOperateException(HmBeResUtil.getString(strAdminOnly));
		}

		setRestoreFlag(true);

		String strErrMsg = "restore_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahUnzipRestoreFile.sh",strFile};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
				strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			String strRestoreErr = "adminRestoreDataErr";

			throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
		}

		/*if("dump".equals(restoreType)){
			String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
			File file = new File(checkFile);
			if(!file.exists()){
				setRestoreFlag(false);
				throw new BeOperateException(HmBeResUtil.getString("adminRestoreDumpErr"));
			}
		}else{*/
		String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
		File file = new File(checkFile);
		if(!file.exists()){
			/*String checkFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/.backupdump";
			File file = new File(checkFile);
			if(file.exists()){
				setRestoreFlag(false);
				throw new BeOperateException(HmBeResUtil.getString("adminRestoreDumpErr"));
			}*/

			// judge the packet is right
			String strVersionFile = BeOperateHMCentOSImpl.AH_UNZIP_ROOT + "/"
					+ "hivemanager.ver";

			//check hm_domain.xml is needless when restore pg_dump file
			File oVersionFile = new File(strVersionFile);


			if (oVersionFile.exists()) {

				String strRestoreDomainName=getResoteDomainName();

				if((null==strRestoreDomainName)||"".equalsIgnoreCase(strRestoreDomainName))
				{
					setRestoreFlag(false);

					BeUploadCfgTools.finishRunningFlag();

					String strRestoreErr = "adminRestoreDataErr";

					throw new BeOperateException(HmBeResUtil
							.getString(strRestoreErr));
				}

				if(!"home".equalsIgnoreCase(strRestoreDomainName))
				{
					String[] strCmds1={"sh",BeAdminCentOSTools.ahShellRoot+ "/mvDomainToHome.sh", strRestoreDomainName};

					String strReturnMsg1 = BeAdminCentOSTools.execCmdWithErr(strCmds1,
							strErrMsg);

					if (strReturnMsg1.equals(strErrMsg)) {
						setRestoreFlag(false);

						BeUploadCfgTools.finishRunningFlag();

						String strRestoreErr = "adminRestoreDataErr";

						throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
					}
				}
			}
		}

		if(file.exists()){
			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot + "/ahCompareRestoreVersion.sh";
			String strReturnMsg1 = BeAdminCentOSTools.execCmdWithErr(strCmd,
					strErrMsg);

			if (strReturnMsg1.equals(strErrMsg)) {
				setRestoreFlag(false);

				BeUploadCfgTools.finishRunningFlag();

				throw new BeOperateException(HmBeResUtil.getString("adminRestoreVersionErr"));
			}
		}
		
		// call script to restore the data
		String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
				+ "/ahRestoreFullData.sh";

		strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmd, strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {
			setRestoreFlag(false);

			BeUploadCfgTools.finishRunningFlag();

			String strRestoreErr = "adminRestoreDataErr";

			throw new BeOperateException(HmBeResUtil.getString(strRestoreErr));
		}

		setRestoreFlag(false);
	}

	/*
	 * @author xiaolanbao
	 *
	 * @description : execute clean domain data
	 *
	 * @param: oDomain
	 *
	 * @return:null
	 *
	 * @throws: BeOperateException
	 */
	public static void execCleanDomainDB(HmDomain oDomain) throws Exception {
		// delete the data belong domain except domain content
		BoMgmt.getDomainMgmt().removeDomain(oDomain.getId(), false);
	}

	/*
	 * @author xiaolanbao
	 * @description :get the file info list
	 * @param: int typeFlag - 1:mib;2:radius dic;3:mac oui dic;4:capture;5:cid client
	 * @return:list<BeFileInfo>
	 * @throws: null
	 */
	public static List<BeFileInfo> getFileInfoList(int typeFlag) {
		// get the file path
	//	String filePath = BeAdminCentOSTools.AH_NMS_MIB_ROOT;
		String filePath = AhDirTools.getMibDir();
		switch (typeFlag) {
			case HmBeAdminUtil.AH_HM_RADIUS_DICTIONARY_TYPE :
			//	filePath = BeAdminCentOSTools.AH_NMS_RADIUS_DICT_ROOT;
				filePath = AhDirTools.getRadiusDictionaryDir();
				break;
			case HmBeAdminUtil.AH_HM_MACOUI_DICTIONARY_TYPE :
			//	filePath = BeAdminCentOSTools.AH_NMS_MACOUI_DICT_ROOT;
				filePath = AhDirTools.getMacOuiDictionaryDir();
				break;
			case HmBeAdminUtil.AH_HM_CAPTURE_RESULT_FILETYPE:
			//	filePath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR;
				filePath = AhDirTools.getDumpDir();
				break;
			case HmBeAdminUtil.AH_HM_CID_CLIENT_FILE_TYPE:
				filePath = AhDirTools.getCidClientsDir();
				break;
			default :
				break;
		}
		File oFileTmp = new File(filePath);

		try {
			List<File> oFileList = HmBeOsUtil.getFilesFromFolder(oFileTmp,
					false);

			if (null == oFileList) {
				return null;
			}

			List<BeFileInfo> oFileInfoList = new ArrayList<BeFileInfo>();

			for (File fTmp : oFileList) {
				oFileInfoList.add(getFileInfo(fTmp));
			}

			return oFileInfoList;
		} catch (Exception ex) {
			DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.getFileInfo() catch exception", ex);

			return null;
		}
	}

	/*
	 * @author xiaolanbao
	 * @description :verify the CA certificate and privatekey
	 * @param: CA Name, certificate name, private key name, passwd for private key
	 * @return:boolean: true and false
	 * @throws: BeOperateException, the error exception
	 */
    public static boolean verifyCertificate(String strCAName, String strCertName, String strKeyName,String strPsd,String strDomainName, boolean ischeckCA) throws BeOperateException
    {
    	if( null == strCAName || null == strCertName || null == strKeyName)
    	{
    		DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.verifyCertificate() the input parameter is error, soome one is null");

    		return false;
    	}

    	List<String> strRsltList;
    	if(ischeckCA)
    	{
    		String[] strcmd1 = {"sh",BeAdminCentOSTools.ahShellRoot+ "/verifyCA.sh",strCAName,strCertName,strDomainName};
    		strRsltList = BeAdminCentOSTools.getOutStreamsExecCmd(strcmd1);

    		int iRslt;
        	try
        	{
        		String strRslt = strRsltList.get(0);
        	    iRslt = Integer.parseInt(strRslt);
        	}
        	catch(Exception ex)
        	{
        		DebugUtil.adminDebugWarn(
    			"BeOperateHMCentOSImpl.verifyCertificate() not get the integer result");

        		return false;
        	}

    	    String strErrMsg;
    	    switch (iRslt)
        	{
        	case 0:
        		break;
        	case 1:
        		strErrMsg = "verify_CA_Cert";

        		throw new BeOperateException(HmBeResUtil
        				.getString(strErrMsg));
        	default:
        		break;
        	}

        }

    	if(null == strPsd)
    	{
    		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/verifyCertificate.sh",strCAName,strCertName,strKeyName,strDomainName};

    		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
    	}
    	else
    	{
    		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/verifyCertificate.sh",strCAName,strCertName,strKeyName,strPsd,strDomainName};

    		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
    	}

    	if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifyCertificate() no return could not charge");

    		return true;
    	}

    	String strRslt = strRsltList.get(0);

    	String strError = "cert_verify_error";

    	if(strError.equalsIgnoreCase(strRslt))
    	{
    		return false;
    	}

    	int iRslt;

    	try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifyCertificate() not get the integer result");

    		return false;
    	}

    	String strErrMsg;

    	switch (iRslt)
    	{
    	case 0:
    		return true;
    	case 1:
    		strErrMsg = "verify_CA_Cert";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	case 2:
    		return false;
    	case 3:
    		strErrMsg = "verify_Privatekey";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	case 4:
    		strErrMsg = "verify_Certificate";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	case 5:
    		strErrMsg = "verify_Cert_Key";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	default:
    		return true;
    	}
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
    	if( null == strKeyFile)
    	{
    		DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.verifycpskey() the input parameter is error, some one is null");

    		return false;
    	}

    	List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/verifycpskey.sh",strKeyFile + ".pem"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifycpskey() no return could not charge");

    		return true;
    	}

		String strRslt = strRsltList.get(0);

		int iRslt;

		try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifycpskey() not get the integer result");

    		return false;
    	}

    	switch (iRslt)
    	{
    	case 0:
    		return true;
    	case 1:
    		return false;
    	case 2:
    		return false;
    	default:
    		return true;
    	}
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
    	if (null != isTftpEnable)
    	{
    		return isTftpEnable;
    	}

        List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/isTftpOn.sh"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isTftpEnable() no return could not charge");

    		isTftpEnable = false;

    		return isTftpEnable;
    	}

		String strRslt = strRsltList.get(0);

		int iRslt;

		try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isTftpEnable() not get the integer result");

    		isTftpEnable = false;

    		return isTftpEnable;
    	}

    	switch (iRslt)
    	{
    	case 0:
    		isTftpEnable = true;
    		return isTftpEnable;
    	case 1:
    		isTftpEnable = false;
    		return isTftpEnable;
    	default:
    		isTftpEnable = true;
    		return isTftpEnable;
    	}
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
        List<String> strRsltList;

        String[] strCmds = new String[3];

        strCmds[0]="sh";
    	strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/setTftpService.sh";

        if(bFlag)
        {
        	strCmds[2]="on";
        }
        else
        {
        	strCmds[2]="off";
        }

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setTftpEnable() no return could not charge");

    		return false;
    	}

		String strRslt = strRsltList.get(0);

		int iRslt;

		try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setTftpEnable() not get the integer result");

    		return false;
    	}

    	switch (iRslt)
    	{
    	case 0:
    		isTftpEnable = bFlag;
    		return true;
    	case 1:
    		return false;
    	default:
    		return false;
    	}
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
    	int iRslt = 22;

        List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetSshdPort.sh"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getSshdServicePort() no return could not charge");

    		return iRslt;
    	}

		String strRslt = strRsltList.get(0);

		try
    	{

    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getSshdServicePort() not get the integer result");

    		return 22;
    	}

    	return iRslt;
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
    	boolean bRslt = false;

        List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahSetSshdPort.sh",Integer.toString(iPort)};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setSshdPort() no return could not charge");

    		return bRslt;
    	}

		String strRslt = strRsltList.get(0);

		int iRslt;

		try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setSshdPort() not get the integer result");

    		return bRslt;
    	}

    	switch(iRslt)
    	{
    	case 0:
    		bRslt = true;
    		return bRslt;
    	case 1:
    		bRslt = false;
    		return bRslt;
    	default:
    		return bRslt;
    	}
    }

    /*
	 * @author xiaolanbao
	 * @description :get pc model
	 * @param: null
	 * @return:String, then model for the pc 1U or 2U
	 * @throws: null
	 */
    public static String getHmKernelModel() {
    	if (kernelModel == null) {
    		kernelModel = getKernelModel();
    	}
    	return kernelModel;
    }

    public static String getHmSerialNumber() {
    	if (serialNumber == null) {
    		serialNumber = getSerialNumber();
    	}
    	return serialNumber;
    }

    private static String getSerialNumber() {
    	List<String> strRsltList;
    	String strRslt = "";

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetHmSN.sh"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getSerialNumber() no return could not charge");
    	}
		else
		{
			strRslt = strRsltList.get(0);
		}
		return strRslt;
	}

    private static String getKernelModel()
    {
    	boolean vm_flag = HM_License.getInstance().isVirtualMachineSystem();
    	boolean HM_1U_flag;

        List<String> strRsltList;

        String strRslt = "1U";

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetPcModel.sh"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getHmKernelModel() no return could not charge");

    		HM_1U_flag = true;
    	}
		else
		{
			strRslt = strRsltList.get(0);

	    	if("1U".equalsIgnoreCase(strRslt))
	    	{
	    		HM_1U_flag = true;
	    	}
	    	else
	    	{
	    		HM_1U_flag = false;
	    	}
		}

    	if(NmsUtil.isHMForOEM())
    	{
    		Map <String, String> mapNumber =  NmsUtil.getOEMCustomer().getHmModelNumber();
    		String strReturn;
    		if(vm_flag)
    		{
    			if(HM_1U_flag)
    			{
    				strReturn = mapNumber.get(OEMCustomer.HM_MODEL_VM_1U_KEY);
    				if(null == strReturn)
    				{
    					return "VM 1U";
    				}
    			}
    			else
    			{
    				strReturn = mapNumber.get(OEMCustomer.HM_MODEL_VM_2U_KEY);
    				if(null == strReturn)
    				{
    					return "VM 2U";
    				}
    			}
    		}
    		else
    		{
    			if(HM_1U_flag)
    			{
    				strReturn = mapNumber.get(OEMCustomer.HM_MODEL_APP_1U_KEY);
    				if(null == strReturn)
    				{
    					return "1U";
    				}
    			}
    			else
    			{
    				strReturn = mapNumber.get(OEMCustomer.HM_MODEL_APP_2U_KEY);
    				if(null == strReturn)
    				{
    					return "2U";
    				}
    			}
    		}

    		return strReturn;
    	}
    	else
    	{
    		String strPre = "AH-HM-NR-";

    		if(vm_flag)
        	{
        		strPre = "VM ";
        		if ("1U".equals(strRslt))
        			return strPre + "32BIT";
        		if("2U".equals(strRslt))
        			return strPre + "64BIT";
        	}
    		if ("AH-HM-1U".equals(strRslt)) {
    			return strRslt + " (64 BIT)";
    		}else	if ("1U".equals(strRslt)){
    			return strPre+strRslt + " (32 BIT)";
    		}else{
    			return  strPre+strRslt + " (64 BIT)";
    		}
    	}
    }

    /*
	 * @author xiaolanbao
	 * @description :get pc model
	 * @param: null
	 * @return:String, then model for the pc 1U or 2U
	 * @throws: null
	 */
    public static String getHmModel()
    {
    	String strRslt = "1U";

        List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetPcModel.sh"};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getHmKernelModel() no return could not charge");

    		return strRslt;
    	}

		strRslt = strRsltList.get(0);

    	return strRslt;
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
    	String strRslt = "off";

    	List<String> strInfoList = new ArrayList<String>();

    	if(!"eth0".equalsIgnoreCase(strEth) && !"eth1".equalsIgnoreCase(strEth))
    	{
    		strInfoList.add(strRslt);
    		strInfoList.add("");
    		strInfoList.add("");

    	    return strInfoList;
    	}

    	if("eth1".equalsIgnoreCase(strEth))
    	{
    		if(!HmBeOsUtil.getEnable_Eth1())
    		{
    			strInfoList.add(strRslt);
    			strInfoList.add("");
        		strInfoList.add("");

        	    return strInfoList;
    		}
    	}

    	List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetEthPro.sh",strEth};

        strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size()|| 2 > strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getEthInfo() no return could not charge");

    		strInfoList.add(strRslt);
    		strInfoList.add("");
    		strInfoList.add("");

    	    return strInfoList;
    	}

		strRslt = "on";

		strInfoList.add(strRslt);

		strInfoList.add(strRsltList.get(0).trim());

		strInfoList.add(strRsltList.get(1).trim());

    	return strInfoList;
    }

    /*
	 * @author xiaolanbao
	 * @description :set eth inform
	 * @param: string:eth0/eth1, int:speed, string:duplex
	 * @return:boolean,true/false
	 * @throws: null
	 */
    public static boolean setEthPro(String strEth, int iSpeed, String strDuplex,boolean isAuto)
    {
        boolean bRslt = false;

        String strFlag = "on";

        if(!isAuto)
        {
	        if(10 != iSpeed && 100 != iSpeed && 1000 != iSpeed)
	        {
	        	return bRslt;
	        }

	        strDuplex = strDuplex.toLowerCase();

	        if(!"half".endsWith(strDuplex) && "full".endsWith(strDuplex))
	        {
	        	return bRslt;
	        }

	        strFlag = "off";
        }

        List<String> strRsltList;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahSetEthPro.sh",strEth,Integer.toString(iSpeed),strDuplex,strFlag};

		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setEthPro() no return could not charge");

    		return bRslt;
    	}

		String strRslt = strRsltList.get(0);

		int iRslt;

		try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.setEthPro() not get the integer result");

    		return bRslt;
    	}

    	switch(iRslt)
    	{
    	case 0:
    		bRslt = true;
    		return bRslt;
    	case 1:
    		bRslt = false;
    		return bRslt;
    	default:
    		return bRslt;
    	}
    }

    /*
	 * @author xiaolanbao
	 * @description :verify the update image
	 * @param: String : the file name for verify
	 * @return:boolean: true and false
	 * @throws: BeOperateException, the error exception
	 */
    private static boolean verifyUpdatePacket(String strFileName) throws BeOperateException
    {
    	if (bUpdateFlag)
    	{
			String strAdminOnly = "adminUpdateOnly";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil
					.getString(strAdminOnly));
    	}

    	if( null == strFileName)
    	{
    		DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.verifyUpdatePacket() the input parameter is null");

    		return false;
    	}

    	setUpdateFlag(true);

//    	String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
//		+ "/checkImage.sh"+" "+"\""+strFileName+"\"";

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/checkImage.sh",strFileName};

        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

    	if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifyUpdatePacket() no return could not charge");

    		setUpdateFlag(false);

    		return false;
    	}

    	String strRslt = strRsltList.get(0);

        int iRslt;

    	try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.verifyUpdatePacket() not get the integer result");

    		setUpdateFlag(false);

    		BeUploadCfgTools.finishRunningFlag();

    		return false;
    	}

    	setUpdateFlag(false);

    	String strErrMsg;

    	switch (iRslt)
    	{
    	case 0:
    		return true;
    	case 1:
    		strErrMsg="error_verify_Update_Image";

    		BeUploadCfgTools.finishRunningFlag();

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	default:
    		return true;
    	}
    }


    /*
	 * @author xiaolanbao
	 * @description :swith pfx-pem
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public static boolean switchPfxToPem(String strSrcName, String strDestName, String strKey, String strDomainName)
    {
    	String[] strCmds;

    	if(null == strKey)
    	{
    		strCmds = new String[5];
    		strCmds[0]="sh";
    		strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/"+"switchPFXToPEM.sh";
    		strCmds[2]=strSrcName;
    		strCmds[3]=strDestName;
    		strCmds[4]=strDomainName;
    	}
    	else
    	{
    		strCmds = new String[6];
    		strCmds[0]="sh";
    		strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/"+"switchPFXToPEM.sh";
    		strCmds[2]=strSrcName;
    		strCmds[3]=strDestName;
    		strCmds[4]=strKey;
    		strCmds[5]=strDomainName;
    	}

    	return isRslt_0(strCmds);
    }

    /*
	 * @author xiaolanbao
	 * @description :swith der-pem cert
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public static boolean switchDERToPemCert(String strSrcName, String strDestName,String strDomainName)
    {
    	String[] strCmds = new String[5];

    	strCmds[0]="sh";
		strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/"+"switchDERToPEMCert.sh";
		strCmds[2]=strSrcName;
		strCmds[3]=strDestName;
		strCmds[4]=strDomainName;

    	return isRslt_0(strCmds);
    }

    /*
	 * @author xiaolanbao
	 * @description :swith der-pem server key
	 * @param:1:source name 2:dest name : password
	 * @return:boolean: true and false
	 */
    public static boolean switchDerToPemKey(String strSrcName, String strDestName, String strKey,String strDomainName)
    {
    	String[] strCmds;

    	if(null == strKey)
    	{
    		strCmds = new String[5];
    		strCmds[0]="sh";
    		strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/"+"switchDERToPEMKey.sh";
    		strCmds[2]=strSrcName;
    		strCmds[3]=strDestName;
    		strCmds[4]=strDomainName;
    	}
    	else
    	{
    		strCmds = new String[6];
    		strCmds[0]="sh";
    		strCmds[1]=BeAdminCentOSTools.ahShellRoot+ "/"+"switchDERToPEMKey.sh";
    		strCmds[2]=strSrcName;
    		strCmds[3]=strDestName;
    		strCmds[4]=strKey;
    		strCmds[5]=strDomainName;
    	}

    	return isRslt_0(strCmds);
    }

    public static boolean isRslt_0(String strShFile)
    {
        String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/"+strShFile};

        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isRslt_0() no return could not charge");

    		return false;
    	}

        String strRslt = strRsltList.get(0);

        int iRslt;

    	try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isRslt_0() not get the integer result");

    		return false;
    	}

		return 0 == iRslt;
    }

    public static boolean isRslt_0(String[] strCmds)
    {
        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);

        if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isRslt_0() no return could not charge");

    		return false;
    	}

        String strRslt = strRsltList.get(0);

        int iRslt;

    	try
    	{
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.isRslt_0() not get the integer result");

    		return false;
    	}

		return 0 == iRslt;
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
    public static boolean createDomainCWP(String strDomainName, BeRootCADTO oData)
    {
    	String strFileName = BeAdminCentOSTools.AH_NMS_HM_CA_CONF_ROOT_OLD
		+ File.separator + BeAdminCentOSTools.AH_NMS_HM_CA_SERVER_CONF;

		if (!BeAdminCentOSTools.createCAConf(oData, strFileName)) {
			return false;
        }

		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/ahCreateDomainCWP.sh", strDomainName, oData.getKeySize(),oData.getValidity(),oData.getFileName()};

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		return !strReturn.equals(strErrMsg);
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
    public static boolean createDefaultDomainCwp(String strDomainName)
    {
    	String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;

    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+"/ahCreateDefaultCWP.sh", strDomainName};

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		return !strReturn.equals(strErrMsg);
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
    public static boolean checkForCwp(String strDomainName,String strCertName, String strKeyName,String strPsd) throws BeOperateException
    {
    	if(null == strCertName || null == strKeyName)
    	{
    		DebugUtil.adminDebugWarn(
					"BeOperateHMCentOSImpl.checkForCwp() the input parameter is error, soome one is null");

    		return false;
    	}

    	List<String> strRsltList;

    	if(null == strPsd || "".equalsIgnoreCase(strPsd))
    	{
    		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahCheckForCWP.sh",strDomainName,strCertName,strKeyName};


    		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
    	}
    	else
    	{
    		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahCheckForCWP.sh",strDomainName,strCertName,strKeyName,strPsd};

    		strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
    	}

    	if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.checkForCwp() no return could not charge");

    		return true;
    	}

    	String strRslt = strRsltList.get(0);

    	int iRslt;

    	try
    	{

    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.checkForCwp() not get the integer result");

    		return false;
    	}

    	String strErrMsg;

    	switch (iRslt)
    	{
    	case 0:
    		return true;
    	case 1:
    		return false;
    	case 2:
    		return false;
    	case 3:
    		strErrMsg = "verify_Privatekey";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	case 4:
    		strErrMsg = "verify_Certificate";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	case 5:
    		strErrMsg = "verify_Cert_Key";

    		throw new BeOperateException(HmBeResUtil
    				.getString(strErrMsg));
    	default:
    		return true;
    	}
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
    public static boolean mergeForCwp(String strDomainName,String strOutCwp,String strInCert, String strInKey,String strPsd)
    {
    	String strOutPut = strOutCwp+".pem";

    	if(null == strPsd || "".equalsIgnoreCase(strPsd))
    	{
    		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;

        	String[] strCmdsm={"sh",BeAdminCentOSTools.ahShellRoot + "/ahMergeCwpFile.sh",strDomainName,strInCert,strInKey,strOutPut};

        	String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmdsm, strErrMsg);

    		return !strReturn.equalsIgnoreCase(strErrMsg);
    	}
    	else
    	{
    		String strErrMsg = BeAdminCentOSTools.AH_NMS_HM_CA_ERROR_MSG;

        	String[] strCmdsm={"sh",BeAdminCentOSTools.ahShellRoot + "/ahMergeCwpFile.sh",strDomainName,strInCert,strInKey,strOutPut,strPsd};

        	String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmdsm, strErrMsg);

    		return !strReturn.equalsIgnoreCase(strErrMsg);
    	}
    }


    /*
	 * @author xiaolanbao
	 *
	 * @description :create default domain CERT
	 *
	 * @param: String strDomain name
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public static boolean createDefaultDomainCERT(String strDomainName)
    {
    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/ahCreateDomainDefautCert.sh",strDomainName};

    	return isRslt_0(strCmds);
    }


    /*
	 * @author xiaolanbao
	 *
	 * @description :check if can use the update link by only GM
	 *
	 * @param: null
	 *
	 * @return:boolean
	 *
	 * @throws: null
	 */
    public static boolean IsValidUpdateLink()
    {
    	boolean bReturn = false;

    	String strGMLicense = "/opt/amigopod/www/_site/AerohiveLicense2.dat";

    	File oFile = new File(strGMLicense);

    	if(oFile.exists() && HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID != HmBeLicenseUtil.LICENSE_VALID)
    	{
    	    bReturn = true;
    	}

    	return bReturn;
    }

//    private static boolean isUpdateGM()
//    {
//        String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/isUpdateGm.sh"};
//
//        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
//
//    	if(null == strRsltList || 0 == strRsltList.size())
//    	{
//    		DebugUtil.adminDebugWarn(
//			"BeOperateHMCentOSImpl.isUpdateGM() no return could not charge");
//
//    		return false;
//    	}
//
//    	String strRslt = strRsltList.get(0);
//
//        int iRslt;
//
//    	try
//    	{
//    	    iRslt = Integer.parseInt(strRslt);
//    	}
//    	catch(Exception ex)
//    	{
//    		DebugUtil.adminDebugWarn(
//			"BeOperateHMCentOSImpl.isUpdateGM() not get the integer result");
//
//    		return false;
//    	}
//
//    	switch (iRslt)
//    	{
//    	case 0:
//    		return true;
//    	case 1:
//    		return false;
//    	default:
//    		return false;
//    	}
//    }

	private static String getResoteDomainName() {
		AhRestoreDBTools.HM_XML_TABLE_PATH = BeOperateHMCentOSImpl.AH_UNZIP_ROOT
				+ File.separatorChar;

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean bRslt = xmlParser.readXMLFile("hm_domain");

		if (!bRslt) {
			DebugUtil.adminDebugError("could not read the file hm_domain.xml");

			return "";
		}

		String colName = "domainname";

		try {
			bRslt = xmlParser.checkColExist(colName);

			if (!bRslt) {
				DebugUtil
						.adminDebugError("could not find the field domainname in the file hm_domain.xml");

				return "";
			}

			return xmlParser.getColVal(0, colName);
		} catch (Exception ex) {
			DebugUtil.adminDebugError(
					"BeOperateHMCentOSImpl.isSameDomain is error", ex);

			return "";
		}
	}

//	private static boolean isExactPacket(HmDomain oDomain) throws Exception {
//		String strDomainDownDir = BeOperateHMCentOSImpl.AH_TMP_DOWMLOAD
//				+ File.separatorChar + oDomain.getDomainName();
//
//		String strDomainMaoDir = BeOperateHMCentOSImpl.AH_TMP_MAP
//				+ File.separatorChar + oDomain.getDomainName();
//
//		File oDownDir = new File(strDomainDownDir);
//
//		File oMapDir = new File(strDomainMaoDir);
//
//		return oDownDir.exists() && oMapDir.exists() && oDownDir.isDirectory()
//				&& oMapDir.isDirectory();
//	}

	private static BeFileInfo getFileInfo(File fInput) {
		if (null == fInput) {
			return null;
		}

		SimpleDateFormat stmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String strFileTime = stmp.format(new Date(fInput.lastModified()));

		double dFileSize = fInput.length();

		NumberFormat nbf = NumberFormat.getInstance();

		nbf.setMaximumFractionDigits(2);

		nbf.setMaximumFractionDigits(2);

		String strFileSize = nbf.format(dFileSize / 1024);

		BeFileInfo oFileInfo = new BeFileInfo();

		oFileInfo.setFileName(fInput.getName());

		oFileInfo.setFileSize(strFileSize);

		oFileInfo.setCreateTime(strFileTime);

		return oFileInfo;
	}

	private static boolean isExactVersion()
	{
		String strLocal;
		String strUpdate;

		try
		{
			String[] strLocals;
			String[] strUpdates;

		    strLocals=FileManager.getInstance().readFile(AH_LOCAL_INNER_VERSION);
		    strUpdates=FileManager.getInstance().readFile(AH_UPDATE_INNER_VERSION);

		    if(null == strLocals || 0 == strLocals.length || null == strUpdates || 0 == strUpdates.length)
		    {
		    	DebugUtil.adminDebugError("the content of local or update .inner.ver is empty");

		    	return false;
		    }

		    strLocal = strLocals[0];

		    strUpdate = strUpdates[0];

		    strLocal = strLocal.substring(0, strLocal.indexOf("."));

		    strUpdate = strUpdate.substring(0,strUpdate.indexOf("."));

		    int iLocalNo;
		    int iUpdateNo;
		    iLocalNo=Integer.parseInt(strLocal);
		    iUpdateNo=Integer.parseInt(strUpdate);

			return (iUpdateNo - iLocalNo) <= 2 && (iUpdateNo - iLocalNo) >= 0;
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugError(ex.getMessage());

			return false;
		}
	}

	public static boolean isExistHomeDomain()
	{
		String where = "domainName = :s1";
		Object[] values = new Object[1];
		values[0] = HmDomain.HOME_DOMAIN;

		List<HmDomain> infoList = QueryUtil.executeQuery(HmDomain.class,
				null, new FilterParams(where,values));

		return !infoList.isEmpty();
	}

	public static SubjectAltname_st getSubjetAltName(String strFile, String strDomainName)
	{
		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/subjectAltname.sh", strFile,strDomainName};

		ShellRslt_st oShellRslt =  BeAdminShellTool.exe_shell_with_rslt(strCmds);

		SubjectAltname_st oResult = new SubjectAltname_st();

		oResult.setResult(oShellRslt.getResult());
		oResult.setContent(oShellRslt.getContent());
		oResult.parseResult();

		return oResult;
	}

	public static void main(String[] args) {

	}

}