package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.LocalAddresses;
import com.ah.be.admin.QueueOperation.HHMUpdateStatusItem;
import com.ah.be.admin.QueueOperation.HHMUpdateWaittingItem;
import com.ah.be.admin.QueueOperation.HMUpdateStatus;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeScpServerInfo;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgInfo;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.hhmoperate.UpdateInfo;
import com.ah.be.admin.util.HAadminTool;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.hhm.HmolConnTestResult;
import com.ah.bo.hhm.HmolUpgradeServerInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HmContextListener;
import com.ah.util.HmolConnectivityTestTool;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class UpdateSoftwareAction extends BaseAction {

	private static final long					serialVersionUID			= 1L;

	private static final Tracer					log							= new Tracer(
																					UpdateSoftwareAction.class
																							.getSimpleName());

	private final String						UPDATESCOPE_NOALARM			= "noAlarmEvent";

	private final String						UPDATESCOPE_FULL			= "full";

	// enum value : {"partBackup","fullBackup"}
	private String								updateScope					= UPDATESCOPE_FULL;

	private String								serverIP;

	private String								scpPort						= "22";

	private String								filePath;

	private String								userName;

	private String								password;

	//
	private final String						UPDATESOURCE_LOCALHOST		= "localhost";

	private final String						UPDATESOURCE_REMOTESERVER	= "remoteServer";

	private final String						UPDATESOURCE_LICENSESERVER	= "licenseServer";

	// enum value {"localhost","remoteServer"}
	private String								updateSource				= UPDATESOURCE_LOCALHOST;

	private File								localFile;

	private String								localFileContentType;

	private String								localFileFileName;
	
	private String                              upgradeFileName;
	
	private int                                 scopeValue;
	
	private int                                 haStatus;
	
	private boolean                             restartApp;
	
	private boolean                             rebootSys;

	// path of up load destination
	private final String						savePath					= HmContextListener.context
																					.getRealPath("WEB-INF"
																							+ File.separator
																							+ "downloads");
//	private Map<String, HhmUpgradeVersionInfo>	hmVersions;

	/**
	 * version - ip
	 */
	private Map<String, String> versionMap;

	// set visibility of cancel button
	// visible:"" invisible:"none"
	//private String hideCommunicationTest = "none";

	private String hideVersionList = "";

	//private String hideCommunicationTestResult = "none";

	//private String hideRetryTestBtn = "none";

	HmolConnTestResult hmolConnTestResult;

	private int testFailType;

	@Override
	public String execute() throws Exception {
		if (!isExternalUpdate) {
			String forward = globalForward();
			if (forward != null) {
				return forward;
			}
		}
		if (!isExternalUpdate) {
			initHMVersions();
		}
		try {
			initScopeValue(updateSource);   //scope Value
			initHaStatus();                 // Hm Status
			if ("update".equals(operation)) {
				HMUpdateStatus.clearStatus();
				if (updateSource.equalsIgnoreCase(UPDATESOURCE_LOCALHOST) && localFile == null) {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					return SUCCESS;
				}

				getDescriptionForUpdate();
				if (updateSource.equals(UPDATESOURCE_LICENSESERVER) && "".equals(downloadFileName)) {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					return SUCCESS;
				}
				try {
					 dismissUpgradeData();
					 initImageFileName();            // upload file
					if (isHaHHMApp()) {
						if (HmBeOsUtil.getMaintenanceModeFromDb()) {
							// HA HHM updateSoftware
							HmBeAdminUtil.haExecUpdateSoft(upgradeFileName,
									scopeValue, haStatus);
							restartApp = true;
						} else {
							addActionMessage("please make sure app working in maintenance mode!");
							return SUCCESS;
						}
					} else if (!NmsUtil.isHostedHMApplication()
							&& HAadminTool.isHaModel()
							&& !is2node(getPrimaryDbUrl())
							&& HAadminTool.isValidMaster()) {
						// HA Hm updateSoftware
						HmBeAdminUtil.haHmExecUpdateSoft(upgradeFileName,
								scopeValue, haStatus);
						rebootSys = true;
					} else {
						updateSoftware();
						if (!HAadminTool.isHaModel()) {
							rebootSys = true;
						}
					}
				} catch (BeOperateException e) {
					String errorMsg=e.getMessage();
					createUpgradeData(errorMsg);
					addActionError(getFormatErrorMsg(errorMsg.split("&&")[0]));
					log.error("execute", "Update software failed!", e);
					if (!isExternalUpdate) {
						generateAuditLog(
								HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.update.software"));
					}
					endProgressConfigFile();
				}
				if(restartApp){
				  Thread thread = new Thread(){
						public void run(){
							try {
								sleep(15000);
								restartApp();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				}else if(rebootSys){
                    Thread thread = new Thread(){
						public void run(){
							try {
								sleep(15000);
								rebootSystem();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				}
				return SUCCESS;
			} else if ("trackHmUpdateStatus".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("status", HMUpdateStatus.updateStatus);
				return "json";
			}else if("removeErrorMsg".equals(operation)){
				dismissUpgradeData();
				return "json";
			} else if ("initProgessConfig".equals(operation)) {
				// clean HM Update Status
				HMUpdateStatus.clearStatus();
				initProgressConfigFile();

				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				return "json";
			} else if ("endProgessConfig".equals(operation)) {
				endProgressConfigFile();

				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				return "json";
			} else if ("uploadExceedLimit".equals(operation)) {
				addActionError(HmBeResUtil.getString("upload.exceedLimit"));

				endProgressConfigFile();

				return SUCCESS;
			} else if ("getNewDes".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("v", getDescriptionForUpdate());
				return "json";
			} else if ("updateHHM".equals(operation)) {
				// vhm of hhm upgrade

				//remove this vhm test result
				HmolConnectivityTestTool.removeVhmTestResult(getVhmId());

				// the selected version object
				HhmUpgradeVersionInfo newVersion = new HhmUpgradeVersionInfo();
				newVersion.setHmVersion(upVersion);
				//newVersion.setIpAddress(versionMap.get(upVersion));

				String vhmId = getVhmId();
				HmolUpgradeServerInfo serverInfo = getHmolUpgradeServerInfo(vhmId,upVersion);
				if (serverInfo == null) {
					newVersion.setIpAddress(versionMap.get(upVersion));
				} else {
					String serverDomainName = serverInfo.getServerDomainName();
					newVersion.setIpAddress(serverDomainName);
				}

				UpdateInfo response = HmBeAdminUtil.HHMUpdateInQueue(getDomain(),
						AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP, newVersion,
						HHMUpdateWaittingItem.FLAG_UPDATE_HHM2HHM);

				jsonObject = new JSONObject();
				jsonObject.put("success", response.getResult());
				if (!response.getResult()) {
					jsonObject.put("message", response.getErrorMsg());
					log.error("updateHHM", "update failed!");
				}

				//Remove stored target server.
				QueryUtil.bulkRemoveBos(HmolUpgradeServerInfo.class, new FilterParams("vhmId = :s1 and versionName = :s2",  new String[]{vhmId,upVersion}));

				return "json";
			} else if ("pollUpdateStatus".equals(operation)) {
				HHMUpdateStatusItem item = HmBeAdminUtil.getUpdateStatus(getDomainId());

				jsonObject = new JSONObject();
				if (item == null) {
					jsonObject.put("status", 0);
					return "json";
				}

				log.info("Poll update status, status=" + item.getStatus() + ", runStatus="
						+ item.getUpdateStatus());
				jsonObject.put("status", item.getStatus());

				String message = "";
				if (item.getStatus() == HHMUpdateStatusItem.UPDATE_FINISHED) {
					UpdateInfo result = item.getReturnInfo();
					jsonObject.put("success", result.getResult());
					if (result.getResult()) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, HmBeResUtil.getString("updateSoftware.hhmUpdate"));
						message = MgrUtil.getUserMessage("update.software.success.confirm.message");
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, HmBeResUtil.getString("updateSoftware.hhmUpdate"));
						message = result.getErrorMsg();
					}
				} else if (item.getStatus() == HHMUpdateStatusItem.UPDATE_RUNNING) {
					jsonObject.put("runStatus", item.getUpdateStatus());
				} else if (item.getStatus() == HHMUpdateStatusItem.UPDATE_WAITTING) {
					message = MgrUtil.getUserMessage("wait.for.other.update.task.completion.message",NmsUtil.transformTime((int) item.getWaittingTime()));
				} else {
					message = MgrUtil.getUserMessage("invalid.status.value.message") + item.getStatus();
				}
				jsonObject.put("message", message);

				return "json";
			} else if ("confirmUpdate".equals(operation)) {
				boolean isSucc = HHMoperate.UpdateConfirm_2(getDomain(), HHMoperate.HHM_UPDATE);
				if (isSucc) {
					addActionMessage(MgrUtil.getUserMessage("message.switch.new.version.success",NmsUtil.getOEMCustomer().getNmsName()));

					// log out this session
					// invalidateCurrentSession();

					request.getSession().invalidate();

					if (null != request.getRemoteUser()) {
						redirectUrl = getSingleSignOutURL();
						return "redirect";
					}
				} else {
					addActionError(MgrUtil.getUserMessage("action.error.switch.new.version") + NmsUtil.getOEMCustomer().getNmsName() + ".");
				}

				initHMVersions();

				return SUCCESS;
			} else if ("cancelUpdate".equals(operation)) {
				boolean success = HmBeAdminUtil.cancelHHMupdateInQueue(getDomain());

				generateAuditLog(success ? HmAuditLog.STATUS_SUCCESS : HmAuditLog.STATUS_FAILURE,
						MgrUtil.getUserMessage("hm.audit.log.cancel.update.operation"));

				jsonObject = new JSONObject();
				jsonObject.put("success", success);

				return "json";
			} else if ("upgradeToHM".equals(operation)) {
				log.info("execute", "operation:" + operation);
				upgradeToHM();
				return "json";
			} else if ("connectivityTest".equals(operation) || "retryTest".equals(operation)) {
				log.info("execute", "operation:" + operation);
				connectivityTest();
				return "json";
			} else if("connectivityTestResult".equals(operation)){
				log.info("execute", "operation:" + operation);
				hmolConnTestResult = HmolConnectivityTestTool.getTestResult(getVhmId());
				return "connectivityTestResult";
			} else if("cancelConnTest".equals(operation)){
				log.info("execute", "operation:" + operation);
				//remove this vhm test result
				HmolConnectivityTestTool.removeVhmTestResult(getVhmId());
				return "json";
			} else {
				/**
				 * show error msg if Login user is belongs to Home domain 
				 * and has HM upgrade feature permission and HM upgrade failed
				 */
				String errorMsg = getUpgradeErrorMsg();
				if (null != errorMsg) {
				  addActionError(errorMsg);
				}
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private Object[] getPrimaryDbUrl() throws IllegalAccessException, InvocationTargetException{
		String hql = " select primaryDbUrl,secondaryDbUrl from " + HASettings.class.getSimpleName();
		List<?> rsList = QueryUtil.executeQuery(hql, 1);
		Object[] obj = new Object[2];
		if(rsList.isEmpty()){
			return obj;
		}
		obj = (Object[]) rsList.get(0);
		//str[0] = (obj[0]==null?"":(String)obj[0]);
		//str[1] = (obj[1]==null?"":(String)obj[1]);
		/* for (Object objs : rsList) {
			    Object[] obj = (Object[]) objs;
			    AsUserSync.syncForRemoveAsUser((String) obj[1]);
			   }*/
		//HASettings set = new HASettings();


		//rsList.get(0);
		//org.apache.commons.beanutils.BeanUtils.copyProperties(set, rsList.get(0));
		return obj;
		//return rsList.get(0);//(rsList.get(0)==null?"":rsList.get(0)).toString();
	}


	private boolean is2node(Object[] param) throws SocketException
	{
		final Object primaryDbUrl = param[0];
		final Object secondaryDbUrl = param[1];
		  //select primaryDbUrl, secondaryDbUrl from ha_settings
		  return null == primaryDbUrl
		      || null == secondaryDbUrl
		      || "".equals(primaryDbUrl)
		      || "".equals(secondaryDbUrl)
		      || null != LocalAddresses.iterate
		    (
		      new LocalAddresses.Iteration()
		      {
		        public  boolean stop  (String address,String network)
		        {
		          return address.equals(primaryDbUrl)
		              || address.equals(secondaryDbUrl);
		        }
		      }
		    );
		}

	public static void restartApp() throws IOException {
		String[] strCmds = { "sh",
				BeAdminCentOSTools.ahShellRoot + "/ahRestartSoft.sh" };
		Runtime.getRuntime().exec(strCmds);
	}

	private static boolean rebootSystem() {
		String strCmd = "reboot";
		return BeAdminCentOSTools.exeSysCmd(strCmd);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_UPDATE_SOFTWARE);
	}

	private void initHMVersions() {
		if (!getIsInHomeDomain()) {
			try {
				versionMap = RemotePortalOperationRequest.requestUpgradeVhm(getDomain()
						.getDomainName());
			} catch (Exception e) {
				log.error("initHMVersions", "catch exception", e);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.update.software.exception")
								+ e.getMessage());
			}
		}
	}

	private final String	PROGRESS_CONFIGFILE	= "update_progress_config";

	private void initProgressConfigFile() {
		BeUploadCfgInfo cfgInfo = new BeUploadCfgInfo();
		cfgInfo.setType(BeUploadCfgInfo.AH_UPLOAD_TYPE_UPDATE);
		cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNING_TRUE);
		cfgInfo.setLocation(BeUploadCfgInfo.AH_UPLOAD_LOCATION_LOCAL);
		cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_FALSE);
		cfgInfo.setName(getLocalFileFileName());
		cfgInfo.setSize("0"); // can't get file size from javascript
		HmBeAdminUtil.initUploadCfg(cfgInfo);

		MgrUtil.setSessionAttribute(PROGRESS_CONFIGFILE, cfgInfo);
	}

	private void endProgressConfigFile() {
		try {
			BeUploadCfgInfo cfgInfo = (BeUploadCfgInfo) MgrUtil
					.getSessionAttribute(PROGRESS_CONFIGFILE);
			if (cfgInfo == null) {
				return;
			}

			cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
			cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNINF_FALSE);
			HmBeAdminUtil.initUploadCfg(cfgInfo);
		} catch (Exception e) {
			log.error("endProgressConfigFile", "catch exception", e);
		}
	}

	/**
	 * update image in ha model
	 * @throws IOException -
	 * @throws BeOperateException -
	 * @return -
	 * @author fhu
	 * @date Feb 15, 2012
	 */
	private void initImageFileName() throws IOException, BeOperateException {
		if (updateSource.equals(UPDATESOURCE_LOCALHOST)) {
			// 1: upload file to server side
			FileOutputStream fos;
			FileInputStream fis;
			fos = new FileOutputStream(savePath + File.separator + getLocalFileFileName());
			fis = new FileInputStream(getLocalFile());
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();
			fis.close();

			BeUploadCfgInfo cfgInfo = (BeUploadCfgInfo) MgrUtil
					.getSessionAttribute(PROGRESS_CONFIGFILE);
			cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
			HmBeAdminUtil.initUploadCfg(cfgInfo);
			upgradeFileName = localFileFileName;
		} else if (updateSource.equals(UPDATESOURCE_REMOTESERVER)) {
			// 1. get file from remote server
			BeScpServerInfo serverInfo = new BeScpServerInfo();
			serverInfo.setFilePath(filePath);
			serverInfo.setScpIp(serverIP);
			serverInfo.setScpPort(scpPort);
			serverInfo.setScpPsd(password);
			serverInfo.setScpUsr(userName);
			boolean isSucc = HmBeAdminUtil.getFileFromScpServer(serverInfo);
			if (isSucc) {
				upgradeFileName = getFileNameFromFilePath();
			}
		} else if (updateSource.equals(UPDATESOURCE_LICENSESERVER)) {
			// get file from download
			if (!"".equals(downloadFileName)) {

				// copy file to server side
				FileOutputStream fos;
				FileInputStream fis;
				fos = new FileOutputStream(savePath + File.separator
						+ downloadFileName);
				fis = new FileInputStream(new File(
						AhDirTools.getHiveManagerImageDir() + downloadFileName));
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fis.close();
				upgradeFileName = downloadFileName;
			}
		}
		if (null == upgradeFileName) {
			throw new BeOperateException(MgrUtil.getUserMessage("hollywood_06.action.uploadFile.failed"));
		}
		//check updata status
		HMUpdateStatus.setStatus(HMUpdateStatus.UPLOAD_FILE);
	}

	private void initScopeValue(String updateScope) {
		if (updateScope.equals(UPDATESCOPE_FULL)) {
			scopeValue = AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP;
		} else if (updateScope.equals(UPDATESCOPE_NOALARM)) {
			scopeValue = AhScheduleBackupData.BACKUPCONTENT_PARTLYBACKUP;
		}
	}

	private void initHaStatus() {
		HAMonitor haMonitor = HAUtil.getHAMonitor();
		HAStatus currentHAStatus = haMonitor.getCurrentStatus();
		haStatus = currentHAStatus.getStatus();
	}

	private void updateSoftware() throws IOException, BeOperateException {
		HmBeAdminUtil.execUpdateSoft(upgradeFileName, scopeValue);
	}

	private void upgradeToHM() throws JSONException {
		jsonObject = new JSONObject();
		try {
			Map<String, String> hmVersions = RemotePortalOperationRequest.requestUpgradeVhm(getDomain()
				.getDomainName());
			if (hmVersions == null || hmVersions.size() == 0) {
				jsonObject.put("success", false);
				jsonObject.put("message", MgrUtil.getUserMessage("no.online.demo.available.message",NmsUtil.getOEMCustomer().getNmsName()));
				return;
			}

			HhmUpgradeVersionInfo target = new HhmUpgradeVersionInfo();
			for (String key : hmVersions.keySet()) {
				target.setHmVersion(key);
				target.setIpAddress(hmVersions.get(key));
				break;
			}

			UpdateInfo info = HmBeAdminUtil.HHMUpdateInQueue(getDomain(),
					AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP, target,
					HHMUpdateWaittingItem.FLAG_UPDATE_PLAN2DEMO);

			jsonObject.put("success", info.getResult());
			jsonObject.put("message", info.getErrorMsg());
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("message", e.getMessage());
		}
	}

	private void connectivityTest() throws JSONException {
		jsonObject = new JSONObject();
		try {
			String vhmId = getVhmId();

			if (vhmId == null) {
				jsonObject.put("success", false);
				jsonObject.put("message",MgrUtil.getUserMessage("vhm.not.find.message"));
				return;
			}

			//Only one connectivity test in the same time for the same VHM.
			if (HmolConnectivityTestTool.isVhmDoingTest(vhmId)) {
				jsonObject.put("success", false);
				jsonObject.put("message",MgrUtil.getUserMessage("exist.same.connect.test.message"));
				return;
			}

			HmolConnectivityTestTool.setVhmDoingTestFlag(vhmId,true);

			String serverDomainName  = versionMap.get(upVersion);
			String serverIpAddress;
			HmolUpgradeServerInfo serverInfo = getHmolUpgradeServerInfo(vhmId,upVersion);
			if (serverInfo == null) {
				// get server ipAddress by server Domain name.
				InetAddress addr = InetAddress.getByName(serverDomainName);
				serverIpAddress = addr.getHostAddress();
				//store this server in DB
				HmolUpgradeServerInfo hmolUpgradeServerInfo = new HmolUpgradeServerInfo();
				hmolUpgradeServerInfo.setVhmId(vhmId);
				hmolUpgradeServerInfo.setServerAddress(serverIpAddress);
				hmolUpgradeServerInfo.setServerDomainName(serverDomainName);
				hmolUpgradeServerInfo.setVersionName(upVersion);
				createBo(hmolUpgradeServerInfo);
			} else {
				serverIpAddress = serverInfo.getServerAddress();
				serverDomainName = serverInfo.getServerDomainName();
			}

			// do connectivity test
			HmolConnectivityTestTool.doTest(vhmId, getDomainId(), serverIpAddress,serverDomainName);

			//get test result
			hmolConnTestResult = HmolConnectivityTestTool.getTestResult(vhmId);

			jsonObject.put("success", true);

			if (hmolConnTestResult.isConnTestSuccess()) {
				jsonObject.put("testResult",true);
			} else {
				jsonObject.put("testResult",false);
			}
			int deviceCount = hmolConnTestResult.getDevicesCount();
			int noConOld = hmolConnTestResult.getNoConnectDevicesListLength();
			int conOld = deviceCount - noConOld;
			int failConNew = hmolConnTestResult.getFailConnectDeviceListLength();
			int sucConNew = conOld - failConNew;
			jsonObject.put("deviceCount", deviceCount);
			jsonObject.put("noConnectListLength", noConOld);
			jsonObject.put("connectListLength", conOld);
			jsonObject.put("failConnectListLength", failConNew);
			jsonObject.put("sucConnectListLength", sucConNew);
			
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("message", e.getMessage());

			//remove this vhm test result
			HmolConnectivityTestTool.removeVhmTestResult(getVhmId());
		}
	}

	private String getVhmId(){
		HmDomain hmDomain = QueryUtil.findBoById(HmDomain.class, getDomainId());
		String vhmId = null;
		if (hmDomain != null) {
			vhmId = hmDomain.getVhmID();
		}
		return vhmId;
	}

	private HmolUpgradeServerInfo getHmolUpgradeServerInfo(String vhmId, String upVersion) throws Exception{
		// find server from DB
		List<HmolUpgradeServerInfo> hmolUpgradeServerInfos =QueryUtil.executeQuery(HmolUpgradeServerInfo.class, null, new FilterParams("vhmId = :s1 and versionName = :s2", new String[]{vhmId,upVersion}));
		HmolUpgradeServerInfo hmolUpgradeServerInfo = null;
		if (!hmolUpgradeServerInfos.isEmpty()) {
			hmolUpgradeServerInfo = hmolUpgradeServerInfos.get(0);
		}
		return hmolUpgradeServerInfo;
	}

	/**
	 * parse filePath , get file name from it
	 *
	 * @return -
	 */
	private String getFileNameFromFilePath() {
		File file = new File(filePath);
		return file.getName();
	}

	public boolean getNeedConfirm() {
		if (getIsInHomeDomain()) {
			return false;
		}

		String where = "domainName = :s1 AND status = :s2";
		Object[] values = new Object[2];
		values[0] = getDomain().getDomainName();
		values[1] = HMUpdateSoftwareInfo.STATUS_NEEDCONFIRM;

		List<HMUpdateSoftwareInfo> infoList = QueryUtil.executeQuery(
				HMUpdateSoftwareInfo.class, null, new FilterParams(where, values));

		return !infoList.isEmpty();
	}

	private void createUpgradeData(String errorMsg) throws Exception {
		try{
			String[] msgs=errorMsg.split("&&");
			HmUpgradeLog upLog=new HmUpgradeLog();
			upLog.setFormerContent(msgs[0]);
			if(msgs.length>1){
			  upLog.setRecommendAction(msgs[1]);
			}else{
			  upLog.setRecommendAction("No action is required.");
			}
			upLog.setOwner(getDomain());
			upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),getDomain().getTimeZoneString()));
			upLog.setNeedRedirect(true);
			upLog.setDismissed(false);
			upLog.setHmUpdate(true);
			QueryUtil.createBo(upLog);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void dismissUpgradeData() throws Exception {
		//set needRedirect=false,avoid to redirect to upgrade page if upgrade HM failed many times. 
		QueryUtil.updateBo(HmUpgradeLog.class,"needRedirect = :s1, dismissed = :s2",
				 new FilterParams("owner.id=:s3",new Object[] { false, true,domainId}));
	}

	private String getFormatErrorMsg(String errorMsg) {
		errorMsg = "<div style='float: left;width:95%'>"+ errorMsg
				+ "</div><div style='float: right;'><a href='#' onclick='removeErrorMsg();'>"
				+"<img class='dinl' src='"+request.getContextPath()+"/images/cancel.png"+"' width='16' height='16' alt='Dismiss' title='Dismiss' /></a></div>";
		return errorMsg;
	}

	private String getUpgradeErrorMsg() {
		if (!HmUpgradeLog.isHasAccessPermission(userContext)) {
			return null;
		}
		HmUpgradeLog uplog =HmUpgradeLog.getHmUpgradeLog(userContext);
		if(null==uplog || uplog.isDismissed()){
			return null;
		}
		String errorMsg = uplog.getFormerContent();
		if (StringUtils.isBlank(errorMsg)) {
			return null;
		}
		return getFormatErrorMsg(errorMsg);
	}
	
	

	public boolean getDisabledLocal() {
		return !UPDATESOURCE_LOCALHOST.equals(updateSource);
	}

	public boolean getDisabledSCP() {
		return !UPDATESOURCE_REMOTESERVER.equals(updateSource);
	}

	public int getIpAddressLength() {
		return 15;
	}

	public int getSCPPortLength() {
		return 5;
	}

	public int getfilePathLength() {
		return 120;
	}

	public int getuserNameLength() {
		return 32;
	}

	public int getpasswdLength() {
		return 32;
	}

	public String getScpPort() {
		return scpPort;
	}

	public void setScpPort(String scpPort) {
		this.scpPort = scpPort;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public File getLocalFile() {
		return localFile;
	}

	public String getUpdateSource() {
		return updateSource;
	}

	public void setUpdateSource(String updateSource) {
		this.updateSource = updateSource;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLocalFileContentType() {
		return localFileContentType;
	}

	public void setLocalFileContentType(String localFileContentType) {
		this.localFileContentType = localFileContentType;
	}

	public String getLocalFileFileName() {
		return localFileFileName;
	}

	public void setLocalFileFileName(String localFileFileName) {
		this.localFileFileName = localFileFileName;
	}

	public boolean isRestartApp() {
		return restartApp;
	}

	public void setRestartApp(boolean restartApp) {
		this.restartApp = restartApp;
	}

	public boolean isRebootSys() {
		return rebootSys;
	}

	public void setRebootSys(boolean rebootSys) {
		this.rebootSys = rebootSys;
	}

	public String getUpdateScope() {
		return updateScope;
	}

	public void setUpdateScope(String updateScope) {
		this.updateScope = updateScope;
	}

	// the update process will be executed out of login HM
	// which is in UpdateSoftwareExternalAction;
	// default value is false.
	protected boolean	isExternalUpdate;

	/*
	 * for download image from license server
	 */
	private String		downloadFileName	= "";

	public String getShowUpgradeManually() {
		return (operation.equals("updateSoftware") && UPDATESOURCE_LICENSESERVER
				.equals(updateSource)) ? "none" : "";
	}

	public String getDescriptionForUpdate() {
		try {
			List<String> fileNames = HmBeOsUtil.getFileNamesOfDirecotry(AhDirTools
					.getHiveManagerImageDir());
			if (null != fileNames && fileNames.size() == 1) {
				// the file must have finished download
				if (!fileNames.get(0).endsWith(".download")) {
					downloadFileName = fileNames.get(0);
				}
			}
		} catch (Exception e) {
			log.error("getDescriptionForUpdate", e.getMessage(), e);
		}
		if ("".equals(downloadFileName)) {
			return MgrUtil.getUserMessage("license.server.available.software.update");
		} else {
			return "The software file ("
					+ downloadFileName
					+ ") has been downloaded to system. To activate it, click OK.";
		}
	}

	public String getDownloadFileName() {
		return downloadFileName;
	}

	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}

	public List<String> getVersionList() {
		List<String> versions = new ArrayList<String>();
		if (null != versionMap) {
			for (String ver : versionMap.keySet()) {
				versions.add(ver);
			}
		}

		if (versions.isEmpty()) {
			versions.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return versions;
	}

	private String	upVersion;

	public String getUpVersion() {
		return upVersion;
	}

	public void setUpVersion(String upVersion) {
		this.upVersion = upVersion;
	}

	public String redirectUrl;

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}

	public String getHideVersionList() {
		return hideVersionList;
	}

	public void setHideVersionList(String hideVersionList) {
		this.hideVersionList = hideVersionList;
	}

	public HmolConnTestResult getHmolConnTestResult() {
		return hmolConnTestResult;
	}

	public void setHmolConnTestResult(HmolConnTestResult hmolConnTestResult) {
		this.hmolConnTestResult = hmolConnTestResult;
	}

	public int getTestFailType() {
		return testFailType;
	}

	public void setTestFailType(int testFailType) {
		this.testFailType = testFailType;
	}

	public String getUpgradeButtonDisabled() {
		if (HAUtil.isSlave()) {
			if (NmsUtil.isHostedHMApplication()) {
				return userContext.isSuperUser() ? "" : "disabled";
			} else {
				return "disabled";
			}
		} else {
			return super.getWriteDisabled();
		}
	}
	
   public boolean isHaHHMApp(){
	 return haStatus != HAStatus.STATUS_STAND_ALONG && NmsUtil.isHostedHMApplication();
   }
}