/**
 *@filename		BeAdminTools.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-9-13 11:33:48
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */

package com.ah.be.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeLogServerInfo;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.AhDirTools;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author Xiaolanbao
 * @version V2.2.0.0
 */
public class BeAdminCentOSTools
{
	public static final String	ahBackupdir					= System.getenv("CATALINA_HOME") + "/dbxmlfile";

	//public static final String	ahShellRoot					= "./webapps/ROOT/WEB-INF/shell";
	public static final String	ahShellRoot					= "/HiveManager/script/shell";
	//public static final String  ahImageRoot					= "./webapps/ROOT/images/maps";
	public static final String  ahImageRoot					= System.getenv("HM_ROOT")+"/images/maps";

	public static final String  ahUpdateRoot                = System.getenv("CATALINA_HOME") + "/hm_soft_upgrade";

	public static final char	SYSTEM_FILE_NOTE_CHAR		= '#';

	public static final String	FSTAB_LABEL_LINE			= "LABEL=";

	public static final String	GRUBCONF_LABEL_LINE			= "root=LABEL=";

	public static final String	SYSTEM_FIRST_BOOT_LABEL		= "/1";

	public static final String	SYSTEM_SECOND_BOOT_LABEL	= "/hivemap";

	public static final String	SYSTEM_FSTAB_FILE			= "/etc/fstab";

	public static final String	SYSTEM_BOOT_CONF			= "/boot/grub/grub.conf";

	public static final String	SYSTEM_MAP_BOOT_CONF		= "/hivemap/boot/grub/grub.conf";

	//public static final String	AH_NMS_CERTIFICATE_ROOT		= "./webapps/ROOT/WEB-INF/downloads/aerohiveca";
	//public static final String	AH_NMS_CERTIFICATE_ROOT     = "/HiveManager/downloads/home/aerohiveca";
	public static final String  AH_CERTIFICAT_PFEFIX = AhDirTools.getDownloadsDir();

	public static final String  AH_CERTIFICATE_HOME  = "/aerohiveca";

//	public static final String  AH_NMS_MIB_ROOT             = "/HiveManager/open_file/mibs";

//	public static final String  AH_NMS_RADIUS_DICT_ROOT     = "/HiveManager/open_file/dict/radius";

//	public static final String  AH_NMS_MACOUI_DICT_ROOT     = "/HiveManager/open_file/dict/macoui";

//	public static final String  AH_NMS_MACOUI_DICT_FILE     = "/HiveManager/open_file/dict/macoui/ouiDictionary.txt";
	public static final String  AH_NMS_MACOUI_DICT_FILE     = AhDirTools.getMacOuiDictionaryDir() + "ouiDictionary.txt";

	public static final String  AH_NMS_CID_CLIENTS_DICT_FILE     = AhDirTools.getCidClientsDir() + "cidClients.txt";

	
//	public static final String	AH_NMS_CAPTURERESULT_DIR	= "/HiveManager/downloads/home/image/dump";

	public static final String	AH_NMS_HM_CA_KEY_PSD		= "hmkey.psd";

	public static final String	AH_NMS_HM_CA_CONF_ROOT_OLD		= "./conf";

	public static final String	AH_NMS_HM_CA_CONF			= "hmcsr.conf";

	public static final String	AH_NMS_HM_CA_ERROR_MSG		= "error_message";

	public static final String	AH_NMS_HM_CA_SERVER_CONF	= "servercsr.conf";

	public static final String	AH_NMS_HM_CSR_TAIL			= "csr";

	public static final String	AH_NMS_HM_PSD_TAIL			= "psd";

	public static final String	AH_NMS_HM_SRL_TAIL			= "srl";

	public static final String  AH_NMS_GET_LOG_FILE         = "support_logs.tar.gz";

	public static final String  AH_NMS_ROOT_CA_NAME         = "Default_CA.pem";

	public static final String  Ah_NMS_DOWNLOADS_ROOT       = System.getenv("HM_ROOT")+"/WEB-INF/downloads";

	public static final String  AH_NMS_VERSION_FILE         = System.getenv("HM_ROOT")+"/WEB-INF/hmconf/hivemanager.ver";

	public static final String  AH_NMS_MAP_VERSION_FILE     = "/hivemap"+System.getenv("CATALINA_HOME")+"/webapps/ROOT/WEB-INF/hmconf/hivemanager.ver";
	public static final String  AH_NMS_MAP_VERSION_FILE_NEW = "/hivemap"+System.getenv("HM_ROOT")+"/WEB-INF/hmconf/hivemanager.ver";

	public static final String AH_NMS_LOGSERVER_CONF_FILE   = "/etc/syslog-ng/syslog-ng.conf";

	public static final String AH_NMS_LOGCONF_DEFAULT_FLAG  = "#default";
	
	public static final String AH_NMS_LOGCONF_NET_FLAG      = "filter f_net";
	
	public static final String AH_NMS_LOGCONF_NET_SUBNET    = "netmask";
	
	public static final String AH_NMS_DEFAULT_CA            = "Default_CA.pem";
	
	public static final String AH_NMS_DEFAULT_SERVER_CERT   = "Default-Server_cert.pem";
	
	public static final String AH_NMS_DEFAULT_SERVER_KEY    = "Default-Server_key.pem";

	/*
	 * @author xiaolanbao
	 * @description : execute os cmd, if execute donot execute then return error
	 *              message
	 * @param: strCmd, strErrMsg
	 * @return: string
	 * @throws: null
	 */
	public static String execCmdWithErr(String strCmd, String strErrMsg)
	{
		if (null == strCmd)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdWithErr(): "+strMsg);

			return "";
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmd);

			oInputStream = proEcecCmd.getErrorStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				4096);

			String strtext;

			while ((strtext = oBfReader.readLine()) != null)
			{
				// add error log
				DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdWithErr() execute cmd("+strCmd+") error, message: "+strtext);

				if (strtext.equals(strErrMsg))
				{
					return strErrMsg;
				}
			}

			return "";
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ex);

			return "";
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ioe);
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ioe);
				}
			}
		}
	}
	
	public static String execCmdIncludeErr(String[] strCmds, String strErrMsg)
	{
		if (null == strCmds)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdIncludeErr(): "+strMsg);

			return "";
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmds);

			oInputStream = proEcecCmd.getErrorStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				4096);

			String strtext;

			while ((strtext = oBfReader.readLine()) != null)
			{
				// add error log
				DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdIncludeErr() execute cmd error, message: "+strtext);

				if (strtext.contains(strErrMsg))
				{
					return strtext;
				}
			}

			return "";
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdIncludeErr() catch exception",ex);

			return "";
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdIncludeErr() catch exception",ioe);
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdIncludeErr() catch exception",ioe);
				}
			}
		}
	}
	
	public static String execCmdWithErr(String[] strCmds, String strErrMsg)
	{
		if (null == strCmds)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdWithErr(): "+strMsg);

			return strErrMsg;
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmds);

			oInputStream = proEcecCmd.getErrorStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				4096);

			String strtext;

			while ((strtext = oBfReader.readLine()) != null)
			{
				// add error log
				DebugUtil.adminDebugError("BeAdminCentOSTools.execCmdWithErr() execute cmd error, message: "+strtext);

				if (strtext.equals(strErrMsg))
				{
					return strErrMsg;
				}
			}

			return "";
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ex);

			return strErrMsg;
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ioe);
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.execCmdWithErr() catch exception",ioe);
				}
			}
		}
	}

	/*
	 * @author xiaolanbao
	 * @description : execute os cmd, get the stdout Info(first line)
	 * @param: strCmd
	 * @return: string
	 * @throws: null
	 */
	public static String getOutStreamExecCmd(String strCmd)
	{
		if (null == strCmd)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamExecCmd(): "+strMsg);

			return "";
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmd);

			oInputStream = proEcecCmd.getInputStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				2048);

			String strtext;

			if ((strtext = oBfReader.readLine()) == null)
			{
				// add error log
				DebugUtil.adminDebugError("BeAdminCentOSTools.getOutStreamExecCmd() execute cmd("+strCmd+") error, message: "+strtext);

				return "";
			}

			return strtext;
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamExecCmd() catch exception",ex);

			return "";
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamExecCmd() catch exception",ioe);
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamExecCmd() catch exception",ioe);
				}
			}
		}
	}
	
	/*
	 * @author xiaolanbao
	 * @description : execute os cmd, get the stdout Info(all line)
	 * @param: strCmd
	 * @return: string
	 * @throws: null
	 */
	public static List<String> getOutStreamsExecCmd(String strCmd)
	{
		List<String> oTmplist = new ArrayList<String>();
		
		if (null == strCmd)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd(): "+strMsg);

			return oTmplist;
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmd);

			oInputStream = proEcecCmd.getInputStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				2048);

			String strtext;

			while ((strtext = oBfReader.readLine()) != null)
			{
				// add error log
				//DebugUtil.adminDebugError("BeAdminCentOSTools.getOutStreamsExecCmd() execute cmd("+strCmd+") error, message: "+strtext);
				
				oTmplist.add(strtext);
			}

			return oTmplist;
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ex);

			return oTmplist;
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ioe);
					
					return oTmplist;
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ioe);
					
					return oTmplist;
				}
			}
		}
	}
	
	public static List<String> getOutStreamsExecCmd(String[] strCmds)
	{
		List<String> oTmplist = new ArrayList<String>();
		
		if (null == strCmds)
		{
			String strMsg = "the command is null";
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd(): "+strMsg);

			return oTmplist;
		}

		InputStream oInputStream = null;

		BufferedReader oBfReader = null;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmds);

			oInputStream = proEcecCmd.getInputStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				2048);

			String strtext;

			while ((strtext = oBfReader.readLine()) != null)
			{
				// add error log
				DebugUtil.adminDebugInfo("BeAdminCentOSTools.getOutStreamsExecCmd() execute cmd error, message: "+strtext);
				
				oTmplist.add(strtext);
			}

			return oTmplist;
		}
		catch (Exception ex)
		{
			// add error log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ex);

			return oTmplist;
		}
		finally
		{
			if (oInputStream != null)
			{
				try
				{
					oInputStream.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ioe);
					
					return oTmplist;
				}
			}

			if (oBfReader != null)
			{
				try
				{
					oBfReader.close();
				}
				catch (IOException ioe)
				{
					// add error log
					DebugUtil.adminDebugWarn("BeAdminCentOSTools.getOutStreamsExecCmd() catch exception",ioe);
					
					return oTmplist;
				}
			}
		}
	}


	/*
	 * @author xiaolanbao
	 * @description : execute os cmd, donot get info
	 * @param: strCmd
	 * @return: null
	 * @throws: null
	 */
	public static boolean exeSysCmd(String strCmd)
	{
		if (null == strCmd)
		{
			// add debu log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.exeSysCmd(): the execute command is unll");

			return false;
		}

		InputStream oInputStream;
		BufferedReader oBfReader;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmd);

			oInputStream = proEcecCmd.getErrorStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				2048);

			String strText;

			if ((strText = oBfReader.readLine()) != null)
			{
				// add debug
				DebugUtil.adminDebugError("BeAdminCentOSTools.exeSysCmd() execute cmd("+strCmd+") error, message: "+strText);

				oInputStream.close();
				oBfReader.close();

				return false;
			}

			oInputStream.close();
			oBfReader.close();

			return true;
		}
		catch (Exception ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.exeSysCmd() catch exception",ex);

			return false;
		}
	}

	public static boolean exeSysCmd(String[] strCmd)
	{
		if (null == strCmd)
		{
			// add debu log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.exeSysCmd(): the execute command is unll");

			return false;
		}

		InputStream oInputStream;
		BufferedReader oBfReader;

		try
		{
			Process proEcecCmd = Runtime.getRuntime().exec(strCmd);

			oInputStream = proEcecCmd.getErrorStream();

			oBfReader = new BufferedReader(new InputStreamReader(oInputStream),
				2048);

			String strText;

			if ((strText = oBfReader.readLine()) != null)
			{
				// add debug
				DebugUtil.adminDebugError("BeAdminCentOSTools.exeSysCmd() execute cmd("+strCmd+") error, message: "+strText);

				oInputStream.close();
				oBfReader.close();

				return false;
			}

			oInputStream.close();
			oBfReader.close();

			return true;
		}
		catch (Exception ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.exeSysCmd() catch exception",ex);

			return false;
		}
	}
	
	/*
	 * @author xiaolanbao
	 * @description : get the system label of mount
	 * @param: String:the file which store the label(/etc/fstab)
	 * @return: string
	 * @throws: null
	 */
	public static String getMountLabel(String strFileName)
	{
		String strTmp;

		try
		{
			File fFstab = new File(strFileName);

			FileReader frFstab = new FileReader(fFstab);

			BufferedReader brFstab = new BufferedReader(frFstab);

			while ((strTmp = brFstab.readLine()) != null)
			{
				strTmp = strTmp.trim();

				if (strTmp.equals(""))
				{
					continue;
				}

				char cTmp = strTmp.charAt(0);

				if (BeAdminCentOSTools.SYSTEM_FILE_NOTE_CHAR == cTmp)
				{
					continue;
				}

				int iTmp = strTmp.indexOf(BeAdminCentOSTools.FSTAB_LABEL_LINE);

				if (-1 == iTmp)
				{
					continue;
				}

				int iLength = strTmp.length();

				if (iLength < 2)
				{
					continue;
				}

				int iflag = Integer.parseInt(strTmp.substring(iLength - 1));

				if (1 != iflag)
				{
					continue;
				}

				strTmp = strTmp.substring(0, iLength - 2).trim();

				iLength = strTmp.length();

				iflag = Integer.parseInt(strTmp.substring(iLength - 1));

				if (1 != iflag)
				{
					continue;
				}

				iTmp = strTmp.indexOf(" ");

				strTmp = strTmp.substring(
						BeAdminCentOSTools.FSTAB_LABEL_LINE.length(), iTmp);

				brFstab.close();

				frFstab.close();

				return strTmp;
			}

			brFstab.close();

			frFstab.close();

			return "";
		}
		catch (Exception ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getMountLabel() catch exception",ex);

			return "";
		}
	}

	/*
	 * @author xiaolanbao
	 * @description : get the system label of boot
	 * @param: string :the file store boot label (/boot/grub/grub.conf)
	 * @return: string
	 * @throws: null
	 */
	public static String getBootLabel(String strFileName)
	{
		try
		{
			File fGrubConf = new File(strFileName);

			FileReader frGrubconf = new FileReader(fGrubConf);

			BufferedReader brGrubconf = new BufferedReader(frGrubconf);

			String strTmp;

			while ((strTmp = brGrubconf.readLine()) != null)
			{
				if (strTmp.trim().equals(""))
				{
					continue;
				}

				char cTmp = strTmp.trim().charAt(0);

				if (BeAdminCentOSTools.SYSTEM_FILE_NOTE_CHAR == cTmp)
				{
					continue;
				}

				int iTmp = strTmp.trim().indexOf(
					BeAdminCentOSTools.GRUBCONF_LABEL_LINE);

				if (-1 == iTmp)
				{
					continue;
				}

				strTmp = strTmp.trim().substring(iTmp);

				strTmp = strTmp.substring(
						BeAdminCentOSTools.GRUBCONF_LABEL_LINE.length(), strTmp
						.indexOf(" "));

				brGrubconf.close();

				frGrubconf.close();

				return strTmp;
			}

			brGrubconf.close();

			frGrubconf.close();

			return "";

		}
		catch (IOException ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getBootLabel() catch exception",ex);

			return "";
		}
	}

	/*
	 * @author xiaolanbao
	 * @description : modify the boot label
	 * @param: string :strLbl: the name of label. strFileName:boot file
	 * @return: string
	 * @throws: null
	 */
	public static boolean modifyBootLabel(String strLbl, String strFileName)
	{
		String strTmp;

		String strContent = "";

		try
		{
			File fiGrubConf = new File(strFileName);

			FileReader frGrubFile = new FileReader(fiGrubConf);

			BufferedReader brGrubFile = new BufferedReader(frGrubFile);

			while ((strTmp = brGrubFile.readLine()) != null)
			{
				if (strTmp.trim().equals(""))
				{
					strContent = strContent.concat(strTmp);
					strContent = strContent.concat("\r\n");
					continue;
				}

				char cTmp = strTmp.trim().charAt(0);

				if (BeAdminCentOSTools.SYSTEM_FILE_NOTE_CHAR == cTmp)
				{
					strContent = strContent.concat(strTmp);
					strContent = strContent.concat("\r\n");
					continue;
				}

				int iTmp = strTmp.trim().indexOf(
					BeAdminCentOSTools.GRUBCONF_LABEL_LINE);

				if (-1 == iTmp)
				{
					strContent = strContent.concat(strTmp);
					strContent = strContent.concat("\r\n");
					continue;
				}

				if (!strLbl.equals(BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL))
				{
					if (strTmp.trim().contains(BeAdminCentOSTools.GRUBCONF_LABEL_LINE
							+ BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL))
					{
						strContent = strContent.concat(strTmp);
						strContent = strContent.concat("\r\n");
						continue;
					}

					strTmp = strTmp.replace(
						BeAdminCentOSTools.GRUBCONF_LABEL_LINE
							+ BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL,
						BeAdminCentOSTools.GRUBCONF_LABEL_LINE
							+ BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL);

					// m_lbl_boot =
					// ConstParameters.SOFTWARE_UPDATE_SECOND_BOOT_LABEL;
				}
				else
				{
					strTmp = strTmp.replace(
						BeAdminCentOSTools.GRUBCONF_LABEL_LINE
							+ BeAdminCentOSTools.SYSTEM_SECOND_BOOT_LABEL,
						BeAdminCentOSTools.GRUBCONF_LABEL_LINE
							+ BeAdminCentOSTools.SYSTEM_FIRST_BOOT_LABEL);

					// m_lbl_boot =
					// ConstParameters.SOFTWARE_UPDATE_FIRST_BOOT_LABEL;
				}

				strContent = strContent.concat(strTmp);
				strContent = strContent.concat("\r\n");
			}

			brGrubFile.close();
			frGrubFile.close();

			FileWriter fwGrubFile = new FileWriter(fiGrubConf);

			BufferedWriter bwGrubFile = new BufferedWriter(fwGrubFile);

			bwGrubFile.write(strContent, 0, strContent.length());

			bwGrubFile.close();

			fwGrubFile.close();

			return true;
		}
		catch (IOException ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.modifyBootLabel() catch exception",ex);

			return false;
		}
	}

	/*
	 * @author xiaolanbao
	 * @description : create the certificate config file
	 * @param: AhRootCADTO strFilename
	 * @return: boolean
	 * @throws: null
	 */
	public static boolean createCAConf(BeRootCADTO oData, String strFileName)
	{
		try
		{
			File fiRooConf = new File(strFileName);

			FileWriter fwGrubFile = new FileWriter(fiRooConf);

			BufferedWriter bwGrubFile = new BufferedWriter(fwGrubFile);

			String strContent = "########";
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("[ req ]");
			strContent = strContent.concat("\r\n");

			strContent = strContent
				.concat("distinguished_name= req_distinguished_name");
			strContent = strContent.concat("\r\n");

			// strContent = strContent
			// .concat("x509_extensions= v3_ca");
			// strContent = strContent.concat("\r\n");

			strContent = strContent.concat("prompt= no");
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("[ req_distinguished_name ]");
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("C= " + oData.getCountryCode());
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("ST= " + oData.getStateName());
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("L= " + oData.getLocalityName());
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("O= " + oData.getOrgName());
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("OU= " + oData.getOrgUnit());
			strContent = strContent.concat("\r\n");

			strContent = strContent.concat("CN= " + oData.getCommName());
			strContent = strContent.concat("\r\n");

			if(null !=  oData.getEmailAddress() && !"".equals( oData.getEmailAddress().trim()))
			{
				strContent = strContent.concat("emailAddress= "
					+ oData.getEmailAddress());
				strContent = strContent.concat("\r\n");
			}
			
			strContent = strContent.concat("[ usr_cert ]");
			strContent = strContent.concat("\r\n");
			strContent = strContent.concat("extendedKeyUsage=1.3.6.1.5.5.7.3.1");
			strContent = strContent.concat("\r\n");
			
			if(null != oData.getAltName() && !"".equals(oData.getAltName().trim()))
			{
				strContent = strContent.concat("subjectAltName= "+oData.getAltName());
				strContent = strContent.concat("\r\n");
				
			}
			// strContent = strContent.concat("[ v3_ca ]");
			// strContent = strContent.concat("\r\n");
			//
			// strContent = strContent.concat("[ v3_ca ]");
			// strContent = strContent.concat("\r\n");

			bwGrubFile.write(strContent, 0, strContent.length());

			bwGrubFile.close();

			fwGrubFile.close();
		}
		catch (Exception ex)
		{
			// add debug log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.createCAConf() catch exception",ex);

			return false;
		}

		return true;
	}

	/*
	 * @author xiaolanbao
	 * @description : create the key file
	 * @param: strPsd
	 * @return: boolean
	 * @throws: null
	 */
	public static boolean createCAKeyPsd(String strPsd, String strDomainName)
	{
		try
		{
			String strFileName = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
					        +strDomainName
					        +BeAdminCentOSTools.AH_CERTIFICATE_HOME
							+ File.separator + AH_NMS_HM_CA_KEY_PSD;

			File fiRooConf = new File(strFileName);

			FileWriter fwGrubFile = new FileWriter(fiRooConf);

			BufferedWriter bwGrubFile = new BufferedWriter(fwGrubFile);

			bwGrubFile.write(strPsd, 0, strPsd.length());

			bwGrubFile.close();

			fwGrubFile.close();

		}
		catch (Exception ex)
		{
			// add debug log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.createCAKeyPsd() catch exception",ex);

			return false;
		}

		return true;
	}

	/*
	 * @author xiaolanbao
	 * @description : get the root ca key password
	 * @param: null
	 * @return: string, the password
	 * @throws: null
	 */
	public static String getCAKeyPsd(String strDomainName)
	{
		String strFileName = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                            +strDomainName
					        +BeAdminCentOSTools.AH_CERTIFICATE_HOME 
					        + File.separator+ AH_NMS_HM_CA_KEY_PSD;

		try
		{
			File oFileHmKeyPsd = new File(strFileName);

			if (!oFileHmKeyPsd.exists())
			{
				// add debug log
				DebugUtil.adminDebugError("BeAdminCentOSTools.getCAKeyPsd(): the Default_key password file is not exist.");

				return null;
			}

			FileReader oFrPsd = new FileReader(oFileHmKeyPsd);

			BufferedReader brGrubFile = new BufferedReader(oFrPsd);

			String strTmp;

			while ((strTmp = brGrubFile.readLine()) != null)
			{
				return strTmp;
			}

			return null;
		}
		catch (Exception ex)
		{
			// add debug log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getCAKeyPsd() catch exception",ex);

			return null;
		}
	}
	
	/*
	 * @author xiaolanbao
	 * @description :analyse the f_net string for subnet
	 * @param: strNet: the string include the f_net
	 * @return: BeLogServerInfo: the information of syslog server
	 * @throws: null
	 */
	public static BeLogServerInfo analyseNetSting(String strNet)
	{
		BeLogServerInfo oData = new BeLogServerInfo();
		
		if (null == strNet)
		{
			return oData;
		}
		
		List<String> oNet = new ArrayList<String>();
		
		oData.setIsLogServer(true);
		
		int iTmp = strNet.indexOf(BeAdminCentOSTools.AH_NMS_LOGCONF_NET_SUBNET);
		
		if( -1 == iTmp )
		{			
			oData.setIsFullNet(true);
			
			return oData;
		}
		
		oData.setIsFullNet(false);
		try
		{
		    while(-1 != iTmp )
		    {
			    strNet = strNet.substring(iTmp);
			
			    oNet.add(strNet.substring(strNet.indexOf("(")+1, strNet.indexOf(")")));
			    
			    strNet = strNet.substring(strNet.indexOf(")")+1);
			    
			    iTmp = strNet.indexOf(BeAdminCentOSTools.AH_NMS_LOGCONF_NET_SUBNET);
		    }
		    
		    oData.setSubNet(oNet);
		    
		    return oData;
		}
		catch(Exception ex)
		{
			// add log
			DebugUtil.adminDebugWarn("BeAdminCentOSTools.getLogServerInfo() catch exception is: ",ex);

			return oData;	
		}	
	}
 
	/*
	 * @author xiaolanbao
	 * @description :init the domain status
	 * @param: iStatus: the status of domain status
	 * @return: null
	 * @throws: null
	 */
	public static void initDomainStatus(int iStatus)
	{
		List<HmDomain> oDomainList = QueryUtil.executeQuery(HmDomain.class, null , null);
		
		if(oDomainList.isEmpty())
		{
			return;
		}

		for (HmDomain hmDomain : oDomainList) {
			try {
				if (HmDomain.DOMAIN_BACKUP_STATUS == hmDomain.getRunStatus()
						|| HmDomain.DOMAIN_RESTORE_STATUS == hmDomain.getRunStatus()
						|| HmDomain.DOMAIN_UPDATE_STATUS == hmDomain.getRunStatus()) {
					hmDomain.setRunStatus(iStatus);

					BoMgmt.getDomainMgmt().updateDomain(hmDomain);
				}
			}
			catch (Exception ex) {
				DebugUtil.adminDebugWarn("BeAdminCentOSTools.initDomainStatus() catch exception is: ", ex);
			}
		}
	}
	
	public static String getCertificateCenter(String strDomainName)
	{
		return BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX+strDomainName+BeAdminCentOSTools.AH_CERTIFICATE_HOME;
	}
	
}