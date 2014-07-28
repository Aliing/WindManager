package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.QueueOperation.RestoreStatusItem;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgInfo;
import com.ah.be.admin.adminOperateImpl.BeUploadCfgTools;
import com.ah.be.admin.hhmoperate.HHMrestore;
import com.ah.be.admin.hhmoperate.RestoreInfo;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.protocol.AhFtpClient;
import com.ah.be.protocol.ssh.scp.AhScpMgmt;
import com.ah.be.protocol.ssh.scp.AhScpMgmtImpl;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.home.HmSettingsAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

@SuppressWarnings("serial")
public class RestoreDBAction extends BaseAction {

	private static final Tracer	log						= new Tracer(RestoreDBAction.class
																.getSimpleName());

	private File				restoreFile;

	private String				restoreFileContentType;

	private String				restoreFileFileName;

	// path of up load destination
	private String				savePath				= HmContextListener.context
																.getRealPath("WEB-INF"
																		+ File.separator
																		+ "downloads");

	private List<HmDomain>		vhmList;

	private Long				existVHMId;

	private boolean				advancedOption			= false;

	private String				vhmName;

	private final String		RESTOREOPTION_EXISTING	= "existVHMOption";

	private final String		RESTOREOPTION_NEW		= "newVHMOption";

	private String				restoreOption			= RESTOREOPTION_EXISTING;

	private boolean				needPoolStatus			= false;

	private final String		SESSIONKEY_DOMAINID		= "restore_domainID";

	private short				restoreProtocol;

	private String				remoteServer;

	private String				remotePort;

	private String				remoteFilePath;

	private String				remoteUserName;

	private String				remotePassword;

	private boolean             domainType;

	private String              hiddenDomainType = "";

	private String              restoreType = "dump";

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("restore".equals(operation)) {
				if (restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_LOCAL && restoreFile == null) {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					return SUCCESS;
				}

				try {
					// transfer file
					transferFile();

					HAMonitor haMonitor = HAUtil.getHAMonitor();
			        HAStatus currentHAStatus = haMonitor.getCurrentStatus();
			        int haStatus = currentHAStatus.getStatus();
					if(0 != haStatus && haStatus != HAStatus.STATUS_STAND_ALONG && NmsUtil.isHostedHMApplication()){
						if(getIsInHomeDomain()){
							if(!HmBeOsUtil.getMaintenanceModeFromDb()){
								addActionMessage(MgrUtil.getUserMessage("message.ensure.app.work"));
								return SUCCESS;
							}
						}
						haRestoreDB();

						if(getIsInHomeDomain()){
							restartApp();
						}
					}else{
						restoreDB();
					}
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.restore.db"));
				} catch (FileNotFoundException e) {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.restore.db"));

					endProgressConfigFile();
				} catch (Exception e) {
					addActionError(e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.restore.db"));

					endProgressConfigFile();
				}

				initValue();

				return SUCCESS;
			} else if ("pollRestoreStatus".equals(operation)) {
				Long resDomainId = (Long) MgrUtil.getSessionAttribute(SESSIONKEY_DOMAINID);
				RestoreStatusItem status = HmBeAdminUtil.getRestoreStatus(resDomainId);

				jsonObject = new JSONObject();
				if (status == null) {
					jsonObject.put("status", 0);
					return "json";
				}

				log.info("Poll restore status, status=" + status.getStatus());
				jsonObject.put("status", status.getStatus());

				String message = "";
				if (status.getStatus() == RestoreStatusItem.RESTORE_RUNNING) {
					// message = "Restoring database... ("
					// + NmsUtil.transformTime_((int) status.getWaittingTime()) + ")";
					message = MgrUtil.getUserMessage("restore.backup.file.now.message");
				} else if (status.getStatus() == RestoreStatusItem.RESTORE_WAITTING) {
					message = MgrUtil.getUserMessage("wait.for.other.restore.process.completion.message",NmsUtil.transformTime_((int) status.getWaittingTime()));
				} else if (status.getStatus() == RestoreStatusItem.RESTORE_FINISHED) {
					jsonObject.put("success", status.getRestoreInfo().getResult());
					message = status.getRestoreInfo().getResult() ? MgrUtil.getUserMessage("restore.data.success.message")
							: status.getRestoreInfo().getErrorMsg();
					if (!getIsInHomeDomain() && null != resDomainId)
						HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.remove(resDomainId);

					//fix bug 14131
					refreshNavigationTree();
				}

				jsonObject.put("message", message);

				return "json";
			} else if ("cancelRestore".equals(operation)) {

				Long domainID = (Long) MgrUtil.getSessionAttribute(SESSIONKEY_DOMAINID);
				if (domainID == null) {
					jsonObject = new JSONObject();
					jsonObject.put("success", false);

					return "json";
				}

				boolean success = HmBeAdminUtil.cancelRestoreInQueue(QueryUtil.findBoById(
						HmDomain.class, domainID));

				if (success) {
					if (getIsInHomeDomain()
							&& (advancedOption && restoreOption.equals(RESTOREOPTION_NEW))) {
						BoMgmt.getDomainMgmt().removeDomain(
								(Long) MgrUtil.getSessionAttribute(SESSIONKEY_DOMAINID), true);
					}
					if (!getIsInHomeDomain() && null != domainID) {
						HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.remove(domainID);
					}
				}

				generateAuditLog(success ? HmAuditLog.STATUS_SUCCESS : HmAuditLog.STATUS_FAILURE,
						MgrUtil.getUserMessage("hm.audit.log.cancel.restore.db"));

				jsonObject = new JSONObject();
				jsonObject.put("success", success);

				return "json";
			} else if ("initProgessConfig".equals(operation)) {
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

				initValue();
				return SUCCESS;
			} else if ("checkMasterNode".equals(operation)) {
				if(!NmsUtil.isHostedHMApplication()){
					String cmd = HmBeOsUtil.getHAScriptsPath() + "check_master_online.sh";
					int exitValue = execCommand(cmd);
					log.info("execute", "execute " + cmd);
	
					jsonObject = new JSONObject();
					jsonObject.put("online", exitValue == 0);
					jsonObject.put("message", "Unable to execute the restore operation. "
							+ HmSettingsAction.getHAOperationExitMessage(exitValue));
				}else{
					jsonObject = new JSONObject();
					jsonObject.put("online", true);
					//		+ HmSettingsAction.getHAOperationExitMessage(exitValue));
				}
				return "json";
			} else if ("checkSlaveNode".equals(operation)) {
				if(!NmsUtil.isHostedHMApplication()){
					String cmd = HmBeOsUtil.getHAScriptsPath() + "check_slave_online.sh";
					int exitValue = execCommand(cmd);
					log.info("execute", "execute " + cmd);
	
					jsonObject = new JSONObject();
					jsonObject.put("online", exitValue == 0);
				}else{
					jsonObject = new JSONObject();
					jsonObject.put("online", true);
				}
				return "json";
			} else if ("standbySlaveNode".equals(operation)) {
				if(!NmsUtil.isHostedHMApplication()){
					String cmd = HmBeOsUtil.getHAScriptsPath() + "standby_node.sh";
					int exitValue = execCommand(cmd);
					log.info("execute", "execute " + cmd);
	
					jsonObject = new JSONObject();
					jsonObject.put("success", exitValue == 0);
					jsonObject.put("message", "Unable to execute the restore operation. "
							+ HmSettingsAction.getHAOperationExitMessage(exitValue));
				}else{
					jsonObject = new JSONObject();
					jsonObject.put("success", true);
				}
				return "json";
			} else {
				initValue();

				return SUCCESS;
			}
		} catch (Exception e) {
			if (!getIsInHomeDomain()) {
				Long domainID = (Long) MgrUtil.getSessionAttribute(SESSIONKEY_DOMAINID);
				if (null != domainID) {
					HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.remove(domainID);
				}
			}
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RESTORE_DB);
	}

	private void initValue() {
		prepareVHMList();
	}

	private void haRestoreDB() throws Exception {
		if (getIsInHomeDomain()) {

			if (!advancedOption) {
				HmBeAdminUtil.haRestoreFullData(restoreFileFileName,restoreType);

				addActionMessage(MgrUtil.getUserMessage("message.restore.db.restart",NmsUtil.getOEMCustomer().getNmsName()));
			} else {
				if (restoreOption.equals(RESTOREOPTION_EXISTING)) {
					HmDomain domain = findBoById(HmDomain.class, existVHMId);
					if (domain == null) {
						throw new Exception("Can't find selected VHM.");
					}

					RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(domain, savePath,
							restoreFileFileName);
					if (!response.getResult()) {
						throw new Exception("Unable to execute restore operation. "
								+ response.getErrorMsg());
					}

					MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, domain.getId());

					// addActionMessage("The data was restored to VHM(" + domain.getDomainName()
					// + ") successfully.");
				} else if (restoreOption.equals(RESTOREOPTION_NEW)) {
					// 1. create new vhm
					Long newDomainID = createNewVHM();
					if (newDomainID == null || newDomainID == 0) {
						throw new Exception("Unable to create new VHM.");
					}

					RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(findBoById(
							HmDomain.class, newDomainID), savePath, restoreFileFileName);
					if (!response.getResult()) {
						throw new Exception("Unable to execute restore operation. "
								+ response.getErrorMsg());
					}

					MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, newDomainID);
				}

				needPoolStatus = true;
			}

		} else {
			RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(getDomain(), savePath,
					restoreFileFileName);
			if (!response.getResult()) {
				throw new Exception("Unable to execute restore operation. "
						+ response.getErrorMsg());
			}
			
			HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.put(getDomainId(), true);

			// addActionMessage("The data was restored successfully.");
			needPoolStatus = true;
			MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, getDomainId());
		}
	}

	private static void restartApp() throws IOException{
		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/ahRestartSoft.sh"};
		Runtime.getRuntime().exec(strCmds);
		//BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);
	}

	private static String checkMaintenaceMode(){
		String strErrMsg = "";
		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/check_ha_maintenance.sh"};

		return BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);
	}

	/**
	 * reset db conn when restore passive, it include hibernate, capwap and hadbconn
	 * @return void
	 * @author fhu
	 * @throws BeOperateException
	 * @date Apr 25, 2012
	 */
	private static void haResetDBConn() throws BeOperateException{
		String strErrMsg = "update_in_progress";

		String[] strCmds={"sh",BeAdminCentOSTools.ahUpdateRoot + "/HiveManager/script/shell" + "/haResetDBConn.sh"};

		String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,strErrMsg);

		if (strReturnMsg.equals(strErrMsg)) {

			String strExecErr = "adminBackupErr";

			BeUploadCfgTools.finishRunningFlag();

			throw new BeOperateException(HmBeResUtil.getString(strExecErr));
		}
	}

	public static int execCommand(String cmd) {

		try {
			String string_Path_Array[] = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = cmd;

			Process p = Runtime.getRuntime().exec(string_Path_Array);

			p.waitFor();

			return p.exitValue();
		} catch (Exception e) {
			log.error("execCommand", "catch exception", e);
			return 255;
		}
	}

	private void prepareVHMList() {
		if (vhmList == null) {
			vhmList = QueryUtil.executeQuery(HmDomain.class, new SortParams("domainName"),
					new FilterParams("domainName != :s1 and runStatus != :s2", new Object[] {
							HmDomain.GLOBAL_DOMAIN, HmDomain.DOMAIN_DISABLE_STATUS }));
		}
		domainType = getIsInHomeDomain();
		if(domainType != true){
			setHiddenDomainType("none");
		}
	}

	private boolean checkVHMNameIsExisted(String vhmName) {
		List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(
				"domainName", vhmName));
		return !list.isEmpty();
	}

	private final String	PROGRESS_CONFIGFILE	= "restore_progress_config";

	private void initProgressConfigFile() {
		BeUploadCfgInfo cfgInfo = new BeUploadCfgInfo();
		cfgInfo.setType(BeUploadCfgInfo.AH_UPLOAD_TYPE_RESTORE);
		cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNING_TRUE);
		cfgInfo.setLocation(BeUploadCfgInfo.AH_UPLOAD_LOCATION_LOCAL);
		cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_FALSE);
		cfgInfo.setName(getRestoreFileFileName());
		cfgInfo.setSize("0"); // can't get file size from javascript
		HmBeAdminUtil.initUploadCfg(cfgInfo);

		MgrUtil.setSessionAttribute(PROGRESS_CONFIGFILE, cfgInfo);

	}

	private void endProgressConfigFile() {
		BeUploadCfgInfo cfgInfo = (BeUploadCfgInfo) MgrUtil
				.getSessionAttribute(PROGRESS_CONFIGFILE);
		if (cfgInfo == null) {
			return;
		}
		cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
		cfgInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNINF_FALSE);
		HmBeAdminUtil.initUploadCfg(cfgInfo);
	}

	private void prepareSavePath() throws Exception {
		if (!getIsInHomeDomain()) {
			savePath = HHMrestore.getUploadDir(getDomain().getDomainName());
		} else if (advancedOption) {
			if (restoreOption.equals(RESTOREOPTION_EXISTING)) {
				HmDomain domain = findBoById(HmDomain.class, existVHMId);
				if (domain == null) {
					throw new Exception("Can't find selected VHM.");
				}
				savePath = HHMrestore.getUploadDir(domain.getDomainName());
			} else if (restoreOption.equals(RESTOREOPTION_NEW)) {
				if (null != HmBeLicenseUtil.getLicenseInfo()) {
					// check max VHM number
					int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
					if (CacheMgmt.getInstance().getCacheDomainCount() >= maxVHMCount) {
						throw new Exception(MgrUtil.getUserMessage("error.vhm.outofLincense.maximum",
								String.valueOf(maxVHMCount)));
					}
				}

				// check vhm name
				if (checkVHMNameIsExisted(vhmName)) {
					throw new Exception("The VHM (" + vhmName + ") already existed.");
				}

				// check vhm max ap number remaining
				int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
				if (remaining < 25) {
					throw new Exception(MgrUtil.getUserMessage("message.restore.db.vhm.no.enough.device"));
				}

				savePath = HHMrestore.getUploadDir(vhmName);
			}
		}

		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private void transferFile() throws Exception {

		// 1. prepare upload path
		prepareSavePath();

		AhScpMgmt scp = null;
		AhFtpClient ftp = null;
		
		// 2. transefer file
		// get file name for scp/ftp transfer
		if (restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_SCP || restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_FTP) {
			restoreFileFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
		}

		if (restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_FTP) {
			ftp = new AhFtpClient();
			ftp.open(remoteServer, Integer.valueOf(remotePort), remoteUserName,
					remotePassword);
			ftp.setFileType(AhFtpClient.BINARY_FILE_TYPE);
			boolean isSucc = ftp.download(remoteFilePath,savePath + File.separator + restoreFileFileName);
			if (!isSucc) {
				throw new Exception("Error occurs while actually transferring the file.");
			}
		} else if (restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_SCP) {
			scp = new AhScpMgmtImpl(remoteServer, Integer
					.valueOf(remotePort), remoteUserName, remotePassword);
			scp.scpGet(remoteFilePath, savePath);
		} else if (restoreProtocol == EnumConstUtil.RESTORE_PROTOCOL_LOCAL) {
			FileOutputStream fos;
			FileInputStream fis;
			fos = new FileOutputStream(savePath + File.separator + getRestoreFileFileName());
			fis = new FileInputStream(getRestoreFile());
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();

			fis.close();
		}

		// 3. for monitor progress
		BeUploadCfgInfo cfgInfo = (BeUploadCfgInfo) MgrUtil
				.getSessionAttribute(PROGRESS_CONFIGFILE);
		cfgInfo.setFinishFlag(BeUploadCfgInfo.AH_UPLOAD_FINISHED_TRUE);
		HmBeAdminUtil.initUploadCfg(cfgInfo);
		
		if (scp != null) {
			scp.close();
		}
		if (ftp != null) {
			ftp.close();
		}
	}

	private void restoreDB() throws Exception {
		if (getIsInHomeDomain()) {

			if (!advancedOption) {
				HmBeAdminUtil.restoreFullData(restoreFileFileName, restoreType);

				addActionMessage(MgrUtil.getUserMessage("message.restore.db.restart",NmsUtil.getOEMCustomer().getNmsName()));
			} else {
				if (restoreOption.equals(RESTOREOPTION_EXISTING)) {
					HmDomain domain = findBoById(HmDomain.class, existVHMId);
					if (domain == null) {
						throw new Exception("Can't find selected VHM.");
					}

					RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(domain, savePath,
							restoreFileFileName);
					if (!response.getResult()) {
						throw new Exception("Unable to execute restore operation. "
								+ response.getErrorMsg());
					}

					MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, domain.getId());

					// addActionMessage("The data was restored to VHM(" + domain.getDomainName()
					// + ") successfully.");
				} else if (restoreOption.equals(RESTOREOPTION_NEW)) {
					// 1. create new vhm
					Long newDomainID = createNewVHM();
					if (newDomainID == null || newDomainID == 0) {
						throw new Exception("Unable to create new VHM.");
					}

					RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(findBoById(
							HmDomain.class, newDomainID), savePath, restoreFileFileName);
					if (!response.getResult()) {
						throw new Exception("Unable to execute restore operation. "
								+ response.getErrorMsg());
					}

					MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, newDomainID);
				}

				needPoolStatus = true;
			}

		} else {
			RestoreInfo response = HmBeAdminUtil.restoreDomainDataInQueue(getDomain(), savePath,
					restoreFileFileName);
			if (!response.getResult()) {
				throw new Exception("Unable to execute restore operation. "
						+ response.getErrorMsg());
			}
			
			HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.put(getDomainId(), true);

			// addActionMessage("The data was restored successfully.");
			needPoolStatus = true;
			MgrUtil.setSessionAttribute(SESSIONKEY_DOMAINID, getDomainId());
		}
	}

	private Long createNewVHM() throws Exception {
		// create domain
		try {
			HmDomain newDomain = new HmDomain();
			newDomain.setDomainName(vhmName);
			newDomain.setMaxApNum(25);
			newDomain.setRunStatus(HmDomain.DOMAIN_DEFAULT_STATUS);

			return BoMgmt.getDomainMgmt().createDomain(newDomain);
		} catch (Exception e) {
			log.error("execute", "create domain catch exception.", e);

			generateAuditLog(HmAuditLog.STATUS_FAILURE, getText("admin.hmOperation.restoreDB.createVHM"));
			throw new Exception("Create new VHM error. " + e.getMessage());
		}
	}

	public File getRestoreFile() {
		return restoreFile;
	}

	public void setRestoreFile(File restoreFile) {
		this.restoreFile = restoreFile;
	}

	public String getRestoreFileContentType() {
		return restoreFileContentType;
	}

	public void setRestoreFileContentType(String restoreFileContentType) {
		this.restoreFileContentType = restoreFileContentType;
	}

	public String getRestoreFileFileName() {
		return restoreFileFileName;
	}

	public void setRestoreFileFileName(String restoreFileFileName) {
		this.restoreFileFileName = restoreFileFileName;
	}

	public boolean getRestartAfterRestore() {
		return getIsInHomeDomain();
	}

	public TextItem[] getRestoreOptionLst1(){
		return new TextItem[]{new TextItem("existVHMOption",getText("admin.vhmMgr.restoretoexist"))};
	}
	public TextItem[] getRestoreOptionLst2(){
		return new TextItem[]{new TextItem("newVHMOption",getText("admin.vhmMgr.restoretonew"))};
	}

	/**
	 * support restore option for home domain
	 *
	 * @return -
	 */
	public String getHide4VHM() {
		if (getIsInHomeDomain()) {
			return "";
		}

		return "none";
	}

	public boolean getIsExistVHM() {
		prepareVHMList();

		return vhmList != null && !vhmList.isEmpty();
	}

	public boolean getNeedCheckHA() {
		List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
		if (list.isEmpty()) {
			return false;
		}

		HASettings haSettings = list.get(0);
		if (haSettings.getHaStatus() != HASettings.HASTATUS_ENABLE) {
			return false;
		}

		if (!getIsInHomeDomain()) {
			return false;
		}

		return true;
	}

	public String getRestoreType() {
		return restoreType;
	}

	public void setRestoreType(String restoreType) {
		this.restoreType = restoreType;
	}

	public boolean isDomainType() {
		return domainType;
	}

	public void setDomainType(boolean domainType) {
		this.domainType = getIsInHomeDomain();
	}

	public Long getExistVHMId() {
		return existVHMId;
	}

	public void setExistVHMId(Long existVHMId) {
		this.existVHMId = existVHMId;
	}

	public List<HmDomain> getVhmList() {
		return vhmList;
	}

	public void setVhmList(List<HmDomain> vhmList) {
		this.vhmList = vhmList;
	}

	public boolean isAdvancedOption() {
		return advancedOption;
	}

	public void setAdvancedOption(boolean advancedOption) {
		this.advancedOption = advancedOption;
	}

	public String getRestoreOption() {
		return restoreOption;
	}

	public void setRestoreOption(String restoreOption) {
		this.restoreOption = restoreOption;
	}

	public boolean isNeedPoolStatus() {
		return needPoolStatus;
	}

	public void setNeedPoolStatus(boolean needPoolStatus) {
		this.needPoolStatus = needPoolStatus;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public short getRestoreProtocol() {
		return restoreProtocol;
	}

	public void setRestoreProtocol(short restoreProtocol) {
		this.restoreProtocol = restoreProtocol;
	}

	public EnumItem[] getRestoreProtocols() {
		return EnumConstUtil.ENUM_RESTORE_PROTOCOL;
	}

	public String getRemoteServer() {
		return remoteServer;
	}

	public void setRemoteServer(String remoteServer) {
		this.remoteServer = remoteServer;
	}

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	public String getRemoteUserName() {
		return remoteUserName;
	}

	public void setRemoteUserName(String remoteUserName) {
		this.remoteUserName = remoteUserName;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public void setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
	}

	public String getHiddenDomainType() {
		return hiddenDomainType;
	}

	public void setHiddenDomainType(String hiddenDomainType) {
		this.hiddenDomainType = hiddenDomainType;
	}


}