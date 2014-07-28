package com.ah.be.config.hiveap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.DebugUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.config.create.cli.HmCliSpecialHandling;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class UpdateObjectFactory {

	public static UpdateObject getScriptUpdateObject(HiveAp hiveAp,
			short scriptType, String rebootTime, boolean isRelativeTime,
			boolean saveServerFiles, boolean continued, boolean isAuto, boolean enableDS) {
		ScriptConfigObject obj = new ScriptConfigObject();
		if (scriptType == UpdateParameters.COMPLETE_SCRIPT) {
			String host = getCapwapServerName(hiveAp);
			String userName = NmsUtil.getHMScpUser();
			String password = NmsUtil.getHMScpPsd();
			String scriptName = AhConfigUtil.getFullNewConfigName(hiveAp
					.getMacAddress());
			List<String> clis = new ArrayList<String>();
			
			//get HiveAp running version
			String query = "select softVer from " + HiveAp.class.getSimpleName() + " where id="+hiveAp.getId();
			List<?> softVerList = QueryUtil.executeQuery(query, 1);
			String softver = (softVerList.get(0) != null)? (String)softVerList.get(0) : "";
			if (HmCliSpecialHandling.isCapwapRollbackSettingEnable(hiveAp) && 
					NmsUtil.compareSoftwareVersion(softver, "3.5.0.0") >= 0) {
				clis.add(AhCliFactory.getConfigRollbackEnableCli());
				clis.add(AhCliFactory.getConfigRollbackNextRebootCli());
			}

			String downloadCli;
			
			if(enableDS || 
					hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_TCP){
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				downloadCli = AhCliFactory.downloadConfigViaHttp(hiveAp, host, scriptName,
						rebootTime, isRelativeTime, userName, password, proxy,
						proxyPort, proxyLoginUser, proxyLoginPwd, enableDS);
			}else{
				downloadCli = AhCliFactory.downloadConfig(hiveAp.getOwner()
						.getDomainName(), host, scriptName, rebootTime,
						isRelativeTime, userName, password);
			}

			// hide user name and password
			String cli = downloadCli.replace(userName, "******").replace(
					password, "******");
			DebugUtil.configDebugInfo("Upload HiveAp " + hiveAp.getHostName()
					+ " complete Script CLI:[" + cli + "].");
			clis.add(downloadCli);
			if (saveServerFiles) {
				String saveCli = AhCliFactory.getSaveServerFilesCli();
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate save files cli:" + saveCli);
				clis.add(saveCli);
			}
			obj.setClis(clis.toArray(new String[clis.size()]));
			// set active flag
			obj.setActived(false);
		}
		obj.setContinued(continued);
		obj.setScriptType(scriptType);
		obj.setSaveServerFiles(saveServerFiles);
		obj.setTimeCount(0);
		if(enableDS){
			obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_DS_CONFIG);
		}else{
			obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_SCRIPT);
		}
		obj.setMaxTimeout(UpdateParameters.SCRIPT_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

	public static UpdateObject getScriptUpdateObject(HiveAp hiveAp,
			List<String> clis, short scriptType, boolean isActived) {
		ScriptConfigObject obj = new ScriptConfigObject();
		if (scriptType == UpdateParameters.COMPLETE_SCRIPT) {
			obj.setClis(clis.toArray(new String[clis.size()]));
		}
		obj.setContinued(false);
		obj.setScriptType(scriptType);
		obj.setSaveServerFiles(false);
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_SCRIPT);
		obj.setMaxTimeout(UpdateParameters.SCRIPT_TIMEOUT_MAX);
		obj.setAuto(false);
		// set active flag
		obj.setActived(isActived);
		return obj;
	}

	public static UpdateObject getImageUpdateObject(HiveAp hiveAp,
			String rebootTime, boolean isRelativeTime, String imageName,
			int size, boolean continued, boolean isAuto, boolean tftp,
			int limit, int maxTimeout, boolean byVer, String version, short level) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String softVer = hiveAp.getSoftVer();
		String macAddress = hiveAp.getMacAddress();
		boolean enableSignature = true;
		if (null != softVer && "2.1.3.0".equals(softVer.trim())) {
			enableSignature = false;
			DebugUtil.configDebugInfo("HiveAp[" + macAddress
					+ "] software version:" + softVer
					+ ", disable its digital singature check.");
		}
		limit = NmsUtil.compareSoftwareVersion(softVer, "3.4.0.0") >= 0 ? limit
				: 0;
		String cli;
		
		if(MgrUtil.isEnableDownloadServer()){
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			if(byVer){
				cli = AhCliFactory.downloadImageViaDS(hiveAp.getHiveApModel(), version,
						rebootTime, isRelativeTime, userName, password, proxy,
						proxyPort, proxyLoginUser, proxyLoginPwd);
			}else{
				cli = AhCliFactory.downloadImageViaDS(imageName,
						rebootTime, isRelativeTime, userName, password, proxy,
						proxyPort, proxyLoginUser, proxyLoginPwd);
			}
		}else if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			cli = AhCliFactory.downloadImage(host, imageName, rebootTime,
					isRelativeTime, userName, password, enableSignature, tftp,
					limit);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			cli = AhCliFactory.downloadImageViaHttp(host, imageName,
					rebootTime, isRelativeTime, userName, password, proxy,
					proxyPort, proxyLoginUser, proxyLoginPwd);
		}

		// hide user name and password
		String hideCli = cli.replace(userName, "******").replace(password,
				"******");
		DebugUtil.configDebugInfo("HiveAp[" + macAddress
				+ "] generate image cli:" + hideCli + ", image size:" + size);
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(new String[] { cli });
		obj.setTimeCount(0);
		obj.setFileSize(size);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_IMAGE);
		obj.setMaxTimeout(maxTimeout);
		obj.setAuto(isAuto);
		obj.setLevel(level);
		//for fix bug 16604, BR100 upload image auto reboot
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			obj.setActived(true);
		}else{
			obj.setActived(false);
		}
		return obj;
	}

	public static UpdateObject getSignatureUpdateObject(HiveAp hiveAp,
			String signatureName, int size, int limit, int maxTimeout) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String macAddress = hiveAp.getMacAddress();
		String cli;
		if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			cli = AhCliFactory.downloadSignature(host, signatureName, userName,
					password, limit);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			cli = AhCliFactory.downloadSignatureViaHttp(host, signatureName,
					userName, password, proxy, proxyPort, proxyLoginUser,
					proxyLoginPwd);
		}

		// hide user name and password
		String hideCli = cli.replace(userName, "******").replace(password,
				"******");
		DebugUtil.configDebugInfo("HiveAp[" + macAddress
				+ "] generate signature cli:" + hideCli + ", signature size:"
				+ size);
		UpdateObject obj = new UpdateObject();
		obj.setClis(new String[] { cli });
		obj.setTimeCount(0);
		obj.setFileSize(size);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE);
		obj.setMaxTimeout(maxTimeout);
		return obj;
	}
	
	public static UpdateObject getOsDetectionUpdateObject(HiveAp hiveAp,String fileName,
			boolean continued,boolean isAuto) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String macAddress = hiveAp.getMacAddress();
		String domainName = hiveAp.getOwner().getDomainName();
		int size = fileName.length();
		String cli;	
		
		if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			cli = AhCliFactory.downloadOsDetectionFile(domainName, host, fileName, userName, password);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			cli = AhCliFactory.downloadOsDetectionFileViaHttp(domainName, host, fileName,
					userName, password, proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
		}

		// hide user name and password
		String hideCli = cli.replace(userName, "******").replace(password,
				"******");
		DebugUtil.configDebugInfo("HiveAp[" + macAddress
				+ "] generate OsDetection cli:" + hideCli + ", OsDetection file size:" + size);
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(new String[] { cli });
		obj.setTimeCount(0);
		obj.setFileSize(size);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_OS_DETECTION);
		obj.setMaxTimeout(UpdateParameters.OS_DETECTION_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		
		return obj;
	}
	
	public static UpdateObject getOsDetectionUpdateObject(HiveAp hiveAp,
			List<String> clis, int fileSize, boolean isActived) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setFileSize(fileSize);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_OS_DETECTION);
		obj.setMaxTimeout(UpdateParameters.OS_DETECTION_TIMEOUT_MAX);
		obj.setAuto(false);
		obj.setActived(isActived);
		return obj;
	}

	public static UpdateObject getImageUpdateObject(HiveAp hiveAp,
			List<String> clis, int imageSize, boolean isActived) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setFileSize(imageSize);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_IMAGE);
		obj.setMaxTimeout(UpdateParameters.IMAGE_TIMEOUT_MAX);
		obj.setAuto(false);
		obj.setActived(isActived);
		return obj;
	}
	
	public static UpdateObject getSignatureUpdateObject(HiveAp hiveAp,
			List<String> clis, int size) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setFileSize(size);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE);
		obj.setMaxTimeout(UpdateParameters.L7_SIGNATURE_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}

	public static UpdateObject getCwpUpdateObject(HiveAp hiveAp,
			Map<String, Set<String>> dir_files,
			Map<String, Set<String>> ppsk_dir_files,
			Map<Integer, String> index_keyFiles, boolean saveServerFiles,
			boolean continued, boolean isAuto) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String domainName = hiveAp.getOwner().getDomainName();
		List<String> clis = new ArrayList<String>();
		String cli;
		String hideCli;
		Set<String> dirs = dir_files.keySet();
		Set<String> ppsk_dirs = null;
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "4.0.0.0") >= 0){
			ppsk_dirs = ppsk_dir_files.keySet();
		}else{
			ppsk_dirs = new HashSet<String>();
		}
		
		String proxy = hiveAp.getProxyName();
		int proxyPort = hiveAp.getProxyPort();
		String proxyLoginUser = hiveAp.getProxyUsername();
		String proxyLoginPwd = hiveAp.getProxyPassword();
		
		for (String dir_name : dirs) {
			Set<String> files = dir_files.get(dir_name);
			cli = AhCliFactory.getRemoveWebPageDirCli(dir_name);
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate remove captive web page cli:" + cli);
			clis.add(cli);
			cli = AhCliFactory.getCreateWebPageDirCli(dir_name);
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate create captive web page cli:" + cli);
			clis.add(cli);
			
			String tarPackagePath = AhDirTools.getCwpWebDir(domainName) + dir_name + ".tar.gz";
			File tarFile = new File(tarPackagePath);
			if(tarFile.isFile() && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
				if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
					cli = AhCliFactory.downloadCwpWebPageTar(domainName, host,
							dir_name, tarFile.getName(), userName, password);
				} else {
					cli = AhCliFactory.downloadCwpWebPageViaHttpTar(domainName,
							host, dir_name, tarFile.getName(), userName, password,
							proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
				}

				// hide user name and password
				hideCli = cli.replace(userName, "******").replace(password,
						"******");
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate captive web page cli:" + hideCli);
				clis.add(cli);
			}else{
				for (String fileName : files) {
					//br100 don't need dynamic language resource and any static language js file except english
					if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 && BR100UpdateObjectFilter.isNeedFilter(fileName)){
						continue;
					}
					if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
						cli = AhCliFactory.downloadCwpWebPage(domainName, host,
								dir_name, fileName, userName, password);
					} else {
						cli = AhCliFactory.downloadCwpWebPageViaHttp(domainName,
								host, dir_name, fileName, userName, password,
								proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
					}

					// hide user name and password
					hideCli = cli.replace(userName, "******").replace(password,
							"******");
					DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
							+ "] generate captive web page cli:" + hideCli);
					clis.add(cli);
				}
			}
		}
		
		for (String ppsk_dir_name : ppsk_dirs) {
			Set<String> ppskFiles = ppsk_dir_files.get(ppsk_dir_name);
			cli = AhCliFactory.getRemovePpskWebPageDirCli(ppsk_dir_name);
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate remove ppsk web page cli:" + cli);
			clis.add(cli);
			cli = AhCliFactory.getCreatePpskWebPageDirCli(ppsk_dir_name);
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate create ppsk web page cli:" + cli);
			clis.add(cli);
			
			String tarPackagePath = AhDirTools.getCwpWebDir(domainName) + ppsk_dir_name + ".tar.gz";
			File tarFile = new File(tarPackagePath);
			if(tarFile.isFile() && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
				if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
					cli = AhCliFactory.downloadPpskWebPageTar(domainName, host,
							ppsk_dir_name, tarFile.getName(), userName, password);
				} else {
					cli = AhCliFactory.downloadPpskWebPageViaHttpTar(domainName,
							host, ppsk_dir_name, tarFile.getName(), userName, password,
							proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
				}

				// hide user name and password
				hideCli = cli.replace(userName, "******").replace(password,
						"******");
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate ppsk web page cli:" + hideCli);
				clis.add(cli);
			}else{
				for (String fileName : ppskFiles) {
					if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
						cli = AhCliFactory.downloadPpskWebPage(domainName, host,
								ppsk_dir_name, fileName, userName, password);
					} else {
						cli = AhCliFactory.downloadPpskWebPageViaHttp(domainName,
								host, ppsk_dir_name, fileName, userName, password,
								proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
					}

					// hide user name and password
					hideCli = cli.replace(userName, "******").replace(password,
							"******");
					DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
							+ "] generate ppsk web page cli:" + hideCli);
					clis.add(cli);
				}
			}
			
		}
		
		
		Set<Integer> indexes = index_keyFiles.keySet();
		for (Integer index : indexes) {
			String keyFileName = index_keyFiles.get(index);

			if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
				cli = AhCliFactory.downloadCwpServerKey(domainName, host,
						index, keyFileName, userName, password);
			} else {
				cli = AhCliFactory.downloadCwpServerKeyViaHttp(domainName,
						host, index, keyFileName, userName, password, proxy,
						proxyPort, proxyLoginUser, proxyLoginPwd);
			}

			// hide user name and password
			hideCli = cli.replace(userName, "******").replace(password,
					"******");
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate captive server key cli:" + hideCli);
			clis.add(cli);
		}
		if (saveServerFiles) {
			cli = AhCliFactory.getSaveServerFilesCli();
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate save files cli:" + cli);
			clis.add(cli);
		}
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_CWP);
		obj.setMaxTimeout(UpdateParameters.CWP_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

	public static UpdateObject getCwpUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_CWP);
		obj.setMaxTimeout(UpdateParameters.CWP_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}

	public static UpdateObject getCertUpdateObject(HiveAp hiveAp,
			Set<String> serverFiles, Set<String> clientFiles,
			boolean saveServerFiles, boolean continued, boolean isAuto) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		List<String> clis = new ArrayList<String>();
		String domainName = hiveAp.getOwner().getDomainName();
		String hideCli;
		String clearServerCli = AhCliFactory.getRadiusServerClearCli();
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Clear previous RADIUS Server file cli:"
				+ clearServerCli);
		clis.add(clearServerCli);
		if (null != serverFiles) {
			for (String serverFile : serverFiles) {
				String cli;

				if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
					cli = AhCliFactory.downloadRadiusServerKey(domainName,
							host, serverFile, userName, password);
				} else {
					String proxy = hiveAp.getProxyName();
					int proxyPort = hiveAp.getProxyPort();
					String proxyLoginUser = hiveAp.getProxyUsername();
					String proxyLoginPwd = hiveAp.getProxyPassword();
					cli = AhCliFactory.downloadRadiusServerKeyViaHttp(
							domainName, host, serverFile, userName, password,
							proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
				}

				// hide user name and password
				hideCli = cli.replace(userName, "******").replace(password,
						"******");
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate RADIUS Server file cli:" + hideCli);
				clis.add(cli);
			}
		}
		String clearClientCli = AhCliFactory.getLdapClientClearCli();
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Clear previous LDAP Client file cli:"
				+ clearClientCli);
		clis.add(clearClientCli);
		if (null != clientFiles) {
			for (String clientFile : clientFiles) {
				String cli;

				if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
					cli = AhCliFactory.downloadLdapClientKey(domainName, host,
							clientFile, userName, password);
				} else {
					String proxy = hiveAp.getProxyName();
					int proxyPort = hiveAp.getProxyPort();
					String proxyLoginUser = hiveAp.getProxyUsername();
					String proxyLoginPwd = hiveAp.getProxyPassword();
					cli = AhCliFactory.downloadLdapClientKeyViaHttp(domainName,
							host, clientFile, userName, password, proxy,
							proxyPort, proxyLoginUser, proxyLoginPwd);
				}

				// hide user name and password
				hideCli = cli.replace(userName, "******").replace(password,
						"******");
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate LDAP Client file cli:" + hideCli);
				clis.add(cli);
			}
		}
		if (saveServerFiles) {
			String cli = AhCliFactory.getSaveServerFilesCli();
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate save files cli:" + cli);
			clis.add(cli);
		}
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_RADIUS_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

	public static UpdateObject getCertUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_RADIUS_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}
	
	public static UpdateObject getCloudAuthUpdateObject(HiveAp hiveAp, 
			boolean continued, boolean isAuto){
		String hiveApMac = hiveAp.getMacAddress();
		String domainName = hiveAp.getOwner().getDomainName();
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String fileName = HmCloudAuthCertMgmtImpl.CERTIFICATE_NAME;
		String certCli = "";
		List<String> clis = new ArrayList<String>();
		
		if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			certCli = AhCliFactory.downloadCloudAuthCaCert(domainName, hiveApMac, host, fileName,
					userName, password);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			certCli = AhCliFactory.downloadCloudAuthCaCertViaHttp(domainName, hiveApMac, host,
					fileName, userName, password, proxy, proxyPort,
					proxyLoginUser, proxyLoginPwd);
		}
		
		// hide user name and password
		String hideCli = certCli.replace(userName, "******").replace(password,
				"******");
		DebugUtil.configDebugInfo("Device[" + hiveAp.getMacAddress()
				+ "] generate ID Manager cert file cli:" + hideCli);
		clis.add(certCli);
		
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}
	
	public static UpdateObject getCloudAuthUpdateObject(HiveAp hiveAp,
			List<String> clis){
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}
	
	public static UpdateObject getDsConfigUpdateObject(HiveAp hiveAp,
			List<String> clis, short updateType){
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(updateType);
		obj.setMaxTimeout(UpdateParameters.SCRIPT_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}
	
	public static UpdateObject getRebootUpdateObject(HiveAp hiveAp,
			String offset_str, String date, String time){
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		
		String rebootCli;
		if(date != null && time != null){
			rebootCli = AhCliFactory.getRebootCli(date, time);
		}else{
			rebootCli = AhCliFactory.getRebootCli(offset_str);
		}
		obj.setClis(new String[]{rebootCli});
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_REBOOT);
		obj.setMaxTimeout(UpdateParameters.SCRIPT_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}

	public static UpdateObject getVpnUpdateObject(HiveAp hiveAp, String rootCa,
			String serverCert, String privateKey, boolean saveServerFiles,
			boolean continued, boolean isAuto) {
		String domainName = hiveAp.getOwner().getDomainName();
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		List<String> clis = new ArrayList<String>();
		String hideCli;
		if (null != rootCa) {
			String cli;

			if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
				cli = AhCliFactory.downloadVpnCaCert(domainName, host, rootCa,
						userName, password);
			} else {
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				cli = AhCliFactory.downloadVpnCaCertViaHttp(domainName, host,
						rootCa, userName, password, proxy, proxyPort,
						proxyLoginUser, proxyLoginPwd);
			}

			// hide user name and password
			hideCli = cli.replace(userName, "******").replace(password,
					"******");
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate Certificate Authority file cli:" + hideCli);
			clis.add(cli);
		}
		if (null != serverCert) {
			String cli;

			if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
				cli = AhCliFactory.downloadVpnEeCert(domainName, host,
						serverCert, userName, password);
			} else {
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				cli = AhCliFactory.downloadVpnEeCertViaHttp(domainName, host,
						serverCert, userName, password, proxy, proxyPort,
						proxyLoginUser, proxyLoginPwd);
			}

			// hide user name and password
			hideCli = cli.replace(userName, "******").replace(password,
					"******");
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate Server Certificate file cli:" + hideCli);
			clis.add(cli);
		}
		if (null != privateKey) {
			String cli;

			if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
				cli = AhCliFactory.downloadVpnPrivateKey(domainName, host,
						privateKey, userName, password);
			} else {
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				cli = AhCliFactory.downloadVpnPrivateKeyViaHttp(domainName,
						host, privateKey, userName, password, proxy, proxyPort,
						proxyLoginUser, proxyLoginPwd);
			}

			// hide user name and password
			hideCli = cli.replace(userName, "******").replace(password,
					"******");
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate Server Cert Private Key file cli:" + hideCli);
			clis.add(cli);
		}
		if (saveServerFiles) {
			String cli = AhCliFactory.getSaveServerFilesCli();
			DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
					+ "] generate save files cli:" + cli);
			clis.add(cli);
		}
		UpdateObject obj = new UpdateObject();
		obj.setContinued(continued);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_VPN_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

	public static UpdateObject getVpnUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_VPN_CERTIFICATE);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}
	
	public static UpdateObject getRebootUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setContinued(false);
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_REBOOT);
		obj.setMaxTimeout(UpdateParameters.CERTIFICATE_TIMEOUT_MAX);
		obj.setAuto(false);
		return obj;
	}

	public static UpdateObject getBootstrapUpdateObject(HiveAp hiveAp,
			BootstrapConfigObject bcObject) {
		String host = getCapwapServerName(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String fileName = AhConfigUtil.getBootstrapConfigName(hiveAp
				.getMacAddress());
		String downloadCli;

		if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			downloadCli = AhCliFactory.downloadBootstrap(hiveAp.getOwner()
					.getDomainName(), host, fileName, userName, password);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			downloadCli = AhCliFactory.downloadBootstrapViaHttp(hiveAp
					.getOwner().getDomainName(), host, fileName, userName,
					password, proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
		}

		// hide user name and password
		String cli = downloadCli.replace(userName, "******").replace(password,
				"******");
		DebugUtil.configDebugInfo("Upload HiveAp " + hiveAp.getHostName()
				+ " Bootstrap Script CLI:[" + cli + "].");
		bcObject.setTimeCount(0);
		bcObject.setClis(new String[] { downloadCli });
		bcObject.setUpdateType(UpdateParameters.AH_DOWNLOAD_BOOTSTRAP);
		bcObject.setMaxTimeout(UpdateParameters.BOOTSTRAP_TIMEOUT_MAX);
		return bcObject;
	}

	public static UpdateObject getCountryCodeUpdateObject(HiveAp hiveAp,
			boolean continued, int countryCode, String offsetStr) {
		if(countryCode == 392 && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.6.0") >= 0 && 
				(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141)
			){
			//from 6.1.6.0 Japan start use country code 4014, this logic is for old version
			countryCode = 4014;
		}else if(countryCode == 392 && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.2.1.0") >= 0 && 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			countryCode = 4014;
		}
		String countryCodeCli = AhCliFactory.getCountryCodeCli(countryCode);
		String rebootCli = null;
		String[] clis;
		if (null != offsetStr && !"".equals(offsetStr)) {
			rebootCli = AhCliFactory.getRebootCli(offsetStr);
		}
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate country code cli:" + countryCodeCli + rebootCli);
		if (null != rebootCli) {
			clis = new String[] { countryCodeCli, rebootCli };
		} else {
			clis = new String[] { countryCodeCli };
		}
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis);
		obj.setTimeCount(0);
		obj.setContinued(continued);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_COUNTRY_CODE);
		obj.setMaxTimeout(UpdateParameters.COUNTRYCODE_TIMEOUT_MAX);
		return obj;
	}

	public static UpdateObject getCountryCodeUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_COUNTRY_CODE);
		obj.setMaxTimeout(UpdateParameters.COUNTRYCODE_TIMEOUT_MAX);
		return obj;
	}
	
	public static UpdateObject getOutdoorSettingsUpdateObject(HiveAp hiveAp,
			boolean continued, boolean isOutdoor, String offsetStr) {
		String outdoorcli = AhCliFactory.getOutdoorSettingsCli(isOutdoor);
		String rebootCli = null;
		String[] clis;
		if (null != offsetStr && !"".equals(offsetStr)) {
			rebootCli = AhCliFactory.getRebootCli(offsetStr);
		}
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate outdoor setings cli:" + outdoorcli + rebootCli);
		if (null != rebootCli) {
			clis = new String[] { outdoorcli, rebootCli };
		} else {
			clis = new String[] { outdoorcli };
		}
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis);
		obj.setTimeCount(0);
		obj.setContinued(continued);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_OUTDOORSTTINGS);
		obj.setMaxTimeout(UpdateParameters.COUNTRYCODE_TIMEOUT_MAX);
		return obj;
	}
	
	public static UpdateObject getOutdoorSettingsUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_OUTDOORSTTINGS);
		obj.setMaxTimeout(UpdateParameters.COUNTRYCODE_TIMEOUT_MAX);
		return obj;
	}

	public static UpdateObject getPskUpdateObject(HiveAp hiveAp,
			short scriptType, String rebootTime, boolean isRelativeTime,
			boolean saveServerFiles, boolean continued, boolean isAuto, boolean enableDS) {
		ScriptConfigObject obj = new ScriptConfigObject();
		if (scriptType == UpdateParameters.COMPLETE_SCRIPT) {
			String host = getCapwapServerName(hiveAp);
			String userName = NmsUtil.getHMScpUser();
			String password = NmsUtil.getHMScpPsd();
			String scriptName = AhConfigUtil.getUserNewConfigName(hiveAp
					.getMacAddress());
			List<String> clis = new ArrayList<String>();
			String downloadCli;

			if(enableDS || 
					hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_TCP){
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				downloadCli = AhCliFactory.downloadPskViaHttp(hiveAp, host, scriptName, userName, password,
						proxy, proxyPort, proxyLoginUser, proxyLoginPwd, enableDS);
			}else{
				downloadCli = AhCliFactory.downloadPsk(hiveAp.getOwner().getDomainName(), 
						host, scriptName, userName, password);
			}

			// hide user name and password
			String cli = downloadCli.replace(userName, "******").replace(
					password, "******");
			DebugUtil.configDebugInfo("Upload HiveAp " + hiveAp.getHostName()
					+ " complete PSK CLI:[" + cli + "].");
			clis.add(downloadCli);
			if (saveServerFiles) {
				String saveCli = AhCliFactory.getSaveServerFilesCli();
				DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
						+ "] generate save files cli:" + saveCli);
				clis.add(saveCli);
			}
			if (null != rebootTime && !"".equals(rebootTime.trim())) {
				// get reboot CLI
				String rebootCli;
				if (isRelativeTime) {
					rebootCli = AhCliFactory.getRebootCli(rebootTime);
				} else {
					String[] time_date = rebootTime.split(" ");
					rebootCli = AhCliFactory.getRebootCli(time_date[1],
							time_date[0]);
				}
				clis.add(rebootCli);
			}
			obj.setActived(false);
			obj.setClis(clis.toArray(new String[clis.size()]));
		}
		obj.setContinued(continued);
		obj.setScriptType(scriptType);
		obj.setSaveServerFiles(saveServerFiles);
		obj.setTimeCount(0);
		if(enableDS){
			obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG);
		}else{
			obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_PSK);
		}
		obj.setMaxTimeout(UpdateParameters.PSK_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

	public static UpdateObject getPskUpdateObject(HiveAp hiveAp,
			List<String> clis, short scriptType, boolean isActived) {
		ScriptConfigObject obj = new ScriptConfigObject();
		if (scriptType == UpdateParameters.COMPLETE_SCRIPT) {
			obj.setClis(clis.toArray(new String[clis.size()]));
		}
		obj.setContinued(false);
		obj.setScriptType(scriptType);
		obj.setSaveServerFiles(false);
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_PSK);
		obj.setMaxTimeout(UpdateParameters.PSK_TIMEOUT_MAX);
		obj.setAuto(false);
		// set active flag
		obj.setActived(isActived);
		return obj;
	}

	public static UpdateObject getPoeUpdateObject(HiveAp hiveAp, int maxPower) {
		String poeCli = AhCliFactory.getPoeMaxPowerCli(maxPower);
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate PoE Max Power cli:" + poeCli);
		String[] clis = new String[] { poeCli };
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis);
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_POE);
		obj.setMaxTimeout(UpdateParameters.POE_TIMEOUT_MAX);
		return obj;
	}
	
	public static UpdateObject getNetdumpUpdateObject(HiveAp hiveAp, boolean enableNetdump, String netdumpServer,String vlanId,
			String nVlanId,String device,String gateway,String ipMode,String managerPortStr) {
		List<String> clisList=new ArrayList<String>();
		String enableNetdumpCli = AhCliFactory.getEnableNetdumpCli(enableNetdump);
		clisList.add(enableNetdumpCli);
		String netdumpServerCli = AhCliFactory.getNetdumpServerCli(netdumpServer);
		clisList.add(netdumpServerCli);
		String netdumpFileCli = AhCliFactory.getNetdumpFileCli();
		clisList.add(netdumpFileCli);
		String netdumpVlanCli = AhCliFactory.getNetdumpVlanCli(vlanId,enableNetdump,"0");
		clisList.add(netdumpVlanCli);
		String netdumpNVlanCli = AhCliFactory.getNetdumpNVlanCli(nVlanId,enableNetdump,"0");
		clisList.add(netdumpNVlanCli);
		String netdumpDeviceCli = AhCliFactory.getNetdumpDeviceCli(device,ipMode);
		clisList.add(netdumpDeviceCli);
		String netdumpGatewayCli = AhCliFactory.getNetdumpGatewayCli(gateway,ipMode);
		clisList.add(netdumpGatewayCli);
		String netdumpManagerPortCli=AhCliFactory.getNetdumpManagerPortCli(managerPortStr);
		if(!"".equals(netdumpManagerPortCli)){
			clisList.add(netdumpManagerPortCli);
		}
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump enable cli:" + enableNetdumpCli);
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump server cli:" + netdumpServerCli);
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump file cli:" + netdumpFileCli);
		int clisLength=clisList.size();
		String[] clis =new String[clisLength];
        for(int i=0;i<clisLength;i++){
        	clis[i]=clisList.get(i);
        }
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis);
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_NET_DUMP);
		obj.setMaxTimeout(UpdateParameters.NETDUMP_TIMEOUT_MAX);
		return obj;
	}

	public static UpdateObject getPoeUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_POE);
		obj.setMaxTimeout(UpdateParameters.POE_TIMEOUT_MAX);
		return obj;
	}
	
	public static UpdateObject getNetdumpUpdateObject(HiveAp hiveAp,
			List<String> clis) {
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_NET_DUMP);
		obj.setMaxTimeout(UpdateParameters.NETDUMP_TIMEOUT_MAX);
		return obj;
	}

	/**
	 * get HiveAP's CAPWAP Server IP.
	 * 
	 * @param hiveAp
	 *            -
	 * @return -
	 */
	private static String getCapwapServerName(HiveAp hiveAp) {
		return NmsUtil.getRunningCapwapServer(hiveAp);
	}
	
	public static UpdateObject getApIpDnsUpdateObject(HiveAp hiveAp,
			String ipAddress, String netMask, String gateWay, String dnsIp) {
		
/*		String ipAndNetmaskCli = AhCliFactory.getConfigIpAndNetmaskCli(ipAddress, netMask);
		String gatewayCli = AhCliFactory.getConfigGatewayCli(gateWay);*/
		String dnsCli = AhCliFactory.getConfigDnsCli(dnsIp);
		String saveConfigCli = AhCliFactory.getSaveConfigCli();
		
/*		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump enable cli:" + ipAndNetmaskCli);
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump server cli:" + gatewayCli);*/
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump file cli:" + dnsCli);
		DebugUtil.configDebugInfo("HiveAp[" + hiveAp.getMacAddress()
				+ "] generate Netdump file cli:" + saveConfigCli);
		String[] clis = new String[] { /*ipAndNetmaskCli, gatewayCli,*/ dnsCli, saveConfigCli};
		UpdateObject obj = new UpdateObject();
		obj.setClis(clis);
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS);
		obj.setMaxTimeout(UpdateParameters.IP_DNS_TIMEOUT_MAX);
		return obj;
	}
	
	public static UpdateObject getDsCfgUpdateObject(HiveAp hiveAp,
			short uploadType, boolean isAuto) {
		UpdateObject obj = new UpdateObject();
		
		List<String> clis = new ArrayList<String>();
		String downloadCli = AhCliFactory.downloadConfigFromDS(hiveAp, uploadType, obj.getSequenceNum());
		clis.add(downloadCli);
		
		obj.setClis(clis.toArray(new String[clis.size()]));
		obj.setTimeCount(0);
		obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_DS_AUDIT_CONFIG);
		obj.setMaxTimeout(UpdateParameters.SCRIPT_TIMEOUT_MAX);
		obj.setAuto(isAuto);
		return obj;
	}

}