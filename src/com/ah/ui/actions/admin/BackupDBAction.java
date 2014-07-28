package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.ah.be.admin.QueueOperation.BackupStatusItem;
import com.ah.be.admin.hhmoperate.BackupInfo;
import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.protocol.AhFtpClient;
import com.ah.be.protocol.ssh.scp.AhScpMgmtImpl;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;
import com.ah.util.NetTool;
import com.ah.util.Tracer;

/**
 *
 *@filename		BackupDBAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-5 02:00:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BackupDBAction extends BaseAction {

	private static final long	serialVersionUID		= 1L;

	private static final Tracer	log						= new Tracer(BackupDBAction.class
																.getSimpleName());

	private final String		BACKUPSCOPE_PARTBACKUP	= "partBackup";

	private final String		BACKUPSCOPE_FULLBACKUP	= "fullBackup";

	// enum value : {"partBackup","fullBackup"}
	private String				backupScope				= BACKUPSCOPE_FULLBACKUP;

	private String              backupType              = "dump";

	private boolean             domainType              = getIsInHomeDomain();

	private String				interval				= null;

	private String				serverIP				= null;

	private String				port					= null;

	private String				filePath				= null;

	private String				userName				= null;

	private String				password				= null;

	private boolean				disabledEndTime			= true;

	private boolean				disabledRecur			= true;

	private String				beginDateTime			= "";

	private String				beginDateTimeH			= "";

	private String				beginDateTimeM			= "";

	private String				endDateTime				= "";

	private String				endDateTimeH			= "";

	private String				endDateTimeM			= "";

	public static EnumItem[]	ENUM_HOURS				= enumItems(24, "hr");

	public static EnumItem[]	ENUM_MINUTES			= enumItems(60, "min");

	private static EnumItem[] enumItems(int len, String postfix) {
		EnumItem[] enumItems = new EnumItem[len];
		for (int i = 0; i < len; i++) {
			String tmp = String.valueOf(i);
			if (tmp.length() == 1) {
				tmp = "0" + tmp;
			}
			tmp = tmp + postfix;
			enumItems[i] = new EnumItem(i, tmp);
		}
		return enumItems;
	}

	private boolean				schedule				= false;

	// set visibility of set schedule section
	// visible:"" invisible:"none"
	private String				hideSchedule			= "none";

	// set visibility of backup Btn
	// visible:"" invisible:"none"
	private String				hideImmediate			= "";

	private boolean				recurring				= false;

	private boolean				endTime					= false;

	// should with suffix
	private String				backupFileName;

	private String				inputPath;

	private String				disabledStopSchedule	= "disabled";

	private String				backupBtnName			= "Backup";

	private static final String	SESSIONKEY_FILENAME		= "backupdb_fileName";

	private static final String	SESSIONKEY_INPUTPATH	= "backupdb_inputpath";

	private short				scheduleProtocol;

	private String				transferServer;

	private String				transferPort;

	private String				transferFilePath;

	private String				transferUserName;

	private String				transferPassword;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("backupSchedule".equals(operation)) {
				// set backup schedule
				clearErrorsAndMessages();
				boolean isSucc = backupSchedule();
				// reinit input field status
				reInit();
				if (!isSucc) {
					addActionError(HmBeResUtil.getString("backupDB.schedule.update.error"));
					initValue();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.set.backup.schedule"));

				} else {
					addActionMessage(HmBeResUtil.getString("backupDB.schedule.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.set.backup.schedule"));
				}

				return SUCCESS;
			} else if ("backupImmediate".equals(operation)) {
				int scope;
				if (backupScope.equals(BACKUPSCOPE_FULLBACKUP)) {
					scope = AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP;
				} else {
					scope = AhScheduleBackupData.BACKUPCONTENT_PARTLYBACKUP;
				}

				jsonObject = new JSONObject();

				try {
					if (getIsInHomeDomain()) {
						// home domain backup
						if("dump".equalsIgnoreCase(backupType)){
							backupFileName = HmBeAdminUtil.haBackupFullDataDump(scope);
						}else{
							backupFileName = HmBeAdminUtil.backupFullData(scope);
						}
						inputPath = HmContextListener.context
								.getRealPath("/WEB-INF/" + "downloads")
								+ File.separator + backupFileName;

						MgrUtil.setSessionAttribute(SESSIONKEY_FILENAME, backupFileName);
						MgrUtil.setSessionAttribute(SESSIONKEY_INPUTPATH, inputPath);

						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.backup.database"));
						jsonObject.put("result", true);
						jsonObject.put("message", 
								MgrUtil.getUserMessage("backup.file.is.ready.message",new String[]{backupFileName,formatFileSize(new File(inputPath).length())}));

						return "json";
					} else {
						// domain backup
						BackupInfo backupResponse = HmBeAdminUtil.backupDomainDataInQueue(
								getDomain(), scope);

						if (!backupResponse.getResult()) {
							jsonObject.put("result", false);
							jsonObject.put("message", MgrUtil.getUserMessage("unable.execute.backup.message")
									+ backupResponse.getErrorMsg());

							return "json";
						}

						jsonObject.put("result", true);

						return "json";
					}

				} catch (Exception e) {
					log.error("backupImmediate", "backup db catch exception", e);

					jsonObject.put("result", false);
					jsonObject.put("message", MgrUtil.getUserMessage("unable.execute.backup.message")+"<br/>"
							+ e.getMessage());

					return "json";
				}

			} else if ("download".equals(operation)) {

				backupFileName = (String) MgrUtil.getSessionAttribute(SESSIONKEY_FILENAME);
				inputPath = (String) MgrUtil.getSessionAttribute(SESSIONKEY_INPUTPATH);

				if(backupFileName.contains(","))backupFileName = backupFileName.replace(",", " ");
				// check file exist
				File file = new File(inputPath);
				if (!file.exists()) {
					addActionError(MgrUtil.getUserMessage("action.error.backup.file")
							+ inputPath);
					return SUCCESS;
				}

				return "download";
			} else if ("stop".equals(operation)) {
				boolean isSucc = HmBeAdminUtil.cancelBackupTask(getDomain());
				if (isSucc) {
					addActionMessage(HmBeResUtil.getString("backupDB.schedule.stop.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.stop.schedule.backup.database"));

					schedule = false;
				} else {
					addActionMessage(HmBeResUtil.getString("backupDB.schedule.stop.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.stop.schedule.backup.database"));
				}

				initValue();

				return SUCCESS;
			} else if ("pollBackupStatus".equals(operation)) {
				BackupStatusItem status = HmBeAdminUtil.getBackupStatus(getDomainId());

				jsonObject = new JSONObject();
				if (status == null) {
					jsonObject.put("status", 0);
					return "json";
				}

				log.info("Poll backup status, status=" + status.getStatus());
				jsonObject.put("status", status.getStatus());

				String message = "";
				if (status.getStatus() == BackupStatusItem.BACKUP_FINISHED) {
					BackupInfo backupResponse = status.getBackupInfo();
					jsonObject.put("success", backupResponse.getResult());
					if (backupResponse.getResult()) {
						inputPath = backupResponse.getFilePath() + File.separator
								+ backupResponse.getFileName();
						MgrUtil.setSessionAttribute(SESSIONKEY_FILENAME, backupResponse
								.getFileName());
						MgrUtil.setSessionAttribute(SESSIONKEY_INPUTPATH, inputPath);
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.backup.database"));
						message = MgrUtil.getUserMessage("backup.file.is.ready.message",new String[]{backupResponse.getFileName(),formatFileSize(new File(inputPath).length())});
					} else {
						message = backupResponse.getErrorMsg();
					}
				} else if (status.getStatus() == BackupStatusItem.BACKUP_RUNNING) {
					message = MgrUtil.getUserMessage("prepare.to.backup.file.message");
				} else if (status.getStatus() == BackupStatusItem.BACKUP_WAITTING) {
					message = MgrUtil.getUserMessage("wait.for.other.backup.process.message",NmsUtil.transformTime_((int) status.getWaittingTime()));
				} else {
					message = MgrUtil.getUserMessage("invalid.status.value.message") + status.getStatus();
				}

				jsonObject.put("message", message);

				return "json";
			} else if ("cancelBackup".equals(operation)) {
				boolean success = HmBeAdminUtil.cancelBackupInQueue(getDomain());
				generateAuditLog(success ? HmAuditLog.STATUS_SUCCESS : HmAuditLog.STATUS_FAILURE,
						MgrUtil.getUserMessage("hm.audit.log.cancel.backup.database"));

				jsonObject = new JSONObject();
				jsonObject.put("success", success);

				return "json";
			} else if ("ftpTransfer".equals(operation)) {
				String message = MgrUtil.getUserMessage("backup.file.be.sent.remote.server.success.message");
				boolean isSucc = false;
				AhFtpClient ftp = null;
				try {
					ftp = new AhFtpClient();
					ftp.open(transferServer, Integer.valueOf(transferPort), transferUserName,
							transferPassword);
					ftp.setFileType(AhFtpClient.BINARY_FILE_TYPE);
					isSucc = ftp.upload(transferFilePath + "/"
							+ (String) MgrUtil.getSessionAttribute(SESSIONKEY_FILENAME),
							(String) MgrUtil.getSessionAttribute(SESSIONKEY_INPUTPATH));
					if (!isSucc) {
						throw new Exception("Error occurs while actually transferring the file.");
					}
				} catch (Exception e) {
					log.error("execute", "ftp upload backup file catch exception", e);
					message = MgrUtil.getUserMessage("unable.to.upload.backup.file.message") + e.getMessage();
				} finally {
					ftp.close();
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				jsonObject.put("message", message);

				return "json";
			} else if ("scpTransfer".equals(operation)) {
				String message =  MgrUtil.getUserMessage("backup.file.be.sent.remote.server.success.message");
				boolean isSucc = true;
				AhScpMgmtImpl scp = null;
				try {
					scp = new AhScpMgmtImpl(transferServer, Integer
							.valueOf(transferPort), transferUserName, transferPassword);
					scp.scpPut((String) MgrUtil.getSessionAttribute(SESSIONKEY_INPUTPATH),
							transferFilePath);
				} catch (Exception e) {
					log.error("execute", "scp upload backup file catch exception", e);
					message = MgrUtil.getUserMessage("unable.to.upload.backup.file.message") + e.getMessage();
					isSucc = false;
				} finally {
					try {
						scp.close();
					} catch (Exception f) {
						// do nothing
					}
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				jsonObject.put("message", message);

				return "json";
			} else {
				initValue();
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_BACKUP_DB);
	}

	/**
	 * get data from db, init gui value
	 */
	private void initValue() {
		AhScheduleBackupData data = HmBeAdminUtil.getDomainScheduleData(getDomain());
		beginDateTime = data.getStartDate();
		beginDateTimeH = String.valueOf(data.getStartHour());
		beginDateTimeM = String.valueOf(data.getStartMinute());
		endTime = data.getEndDateFlag();
		if (endTime) {
			endDateTime = data.getEndDate();
			endDateTimeH = String.valueOf(data.getEndHour());
			endDateTimeM = String.valueOf(data.getEndMinute());
		} else {
			endDateTime = "";
			endDateTimeH = "";
			endDateTimeM = "";
		}
		recurring = data.getRescurFlag();
		if (recurring) {
			interval = String.valueOf(data.getInterval());
		} else {
			interval = "";
		}
		scheduleProtocol = data.getProtocol();
		serverIP = data.getScpIpAdd();
		port = String.valueOf(data.getScpPort());
		filePath = data.getScpFilePath();
		userName = data.getScpUsr();
		password = data.getScpPsd();

		if (data.getLiveFlag()) {
			disabledStopSchedule = "";
			schedule = true;
			hideSchedule = "";
			hideImmediate = "none";
		} else {
			disabledStopSchedule = "disabled";
			schedule = false;
			hideSchedule = "none";
			hideImmediate = "";
		}

		disabledEndTime = !endTime;
		disabledRecur = !recurring;

		if (data.getBackupContent() == AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP) {
			backupScope = BACKUPSCOPE_FULLBACKUP;
		} else {
			backupScope = BACKUPSCOPE_PARTBACKUP;
		}

		domainType = getIsInHomeDomain();
	}

	// struts download support
	public String getLocalFileName() {
		return backupFileName;
	}

	public InputStream getInputStream() throws Exception {
		InputStream inputStream = new FileInputStream(inputPath);

		return inputStream;
	}

	// backup schedule
	private boolean backupSchedule() {
		// call be function
		AhScheduleBackupData data = new AhScheduleBackupData();
		data.setLiveFlag(true);
		// begin time
		data.setStartDate(beginDateTime);
		data.setStartHour(Short.valueOf(beginDateTimeH));
		data.setStartMinute(Short.valueOf(beginDateTimeM));
		// end time& valid flag
		data.setEndDateFlag(endTime);
		if (endTime) {
			data.setEndDate(endDateTime);
			data.setEndHour(Short.valueOf(endDateTimeH));
			data.setEndMinute(Short.valueOf(endDateTimeM));
		}
		// recur valid flag & interval
		data.setRescurFlag(recurring);
		if (recurring) {
			data.setInterval(Integer.valueOf(interval));
		}
		// scp info
		data.setScpIpAdd(serverIP);
		data.setScpPort(Integer.valueOf(port));
		data.setScpFilePath(filePath);
		data.setScpUsr(userName);
		data.setScpPsd(password);

		// backup scope
		if (backupScope.equals(BACKUPSCOPE_FULLBACKUP)) {
			data.setBackupContent(AhScheduleBackupData.BACKUPCONTENT_FULLBACKUP);
		} else if (backupScope.equals(BACKUPSCOPE_PARTBACKUP)) {
			data.setBackupContent(AhScheduleBackupData.BACKUPCONTENT_PARTLYBACKUP);
		}

		data.setOwner(getDomain());
		data.setProtocol(scheduleProtocol);

		data.setBackupType(backupType);

		return HmBeAdminUtil.setDoaminBackupSchedule(data);
	}

	private void reInit() {
		// reinit input fields' enable property
		if (schedule) {
			disabledStopSchedule = "";
			hideSchedule = "";
			hideImmediate = "none";
		} else {
			disabledStopSchedule = "disabled";
			hideSchedule = "none";
			hideImmediate = "";
		}

		disabledEndTime = !endTime;
		disabledRecur = !recurring;
		domainType = getIsInHomeDomain();
	}


	public boolean isDomainType() {
		return domainType;
	}

	public void setDomainType(boolean domainType) {
		this.domainType = getIsInHomeDomain();
	}

	public String getBackupType() {
		return backupType;
	}

	public void setBackupType(String backupType) {
		this.backupType = backupType;
	}

	public String getBackupScope() {
		return backupScope;
	}

	public void setBackupScope(String scope) {
		this.backupScope = scope;
	}

	File	localFile;

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public File getLocalFile() {
		return localFile;
	}

	public static EnumItem[] getEnumHours() {
		return ENUM_HOURS;
	}

	public static void setEnumHours(EnumItem[] enum_hours) {
		ENUM_HOURS = enum_hours;
	}

	public static EnumItem[] getEnumMinutes() {
		return ENUM_MINUTES;
	}

	public static void setEnumMinutes(EnumItem[] enum_minutes) {
		ENUM_MINUTES = enum_minutes;
	}

	public String getBeginDateTime() {
		return beginDateTime;
	}

	public void setBeginDateTime(String beginDateTime) {
		this.beginDateTime = beginDateTime;
	}

	public String getBeginDateTimeH() {
		return beginDateTimeH;
	}

	public void setBeginDateTimeH(String beginDateTimeH) {
		this.beginDateTimeH = beginDateTimeH;
	}

	public String getBeginDateTimeM() {
		return beginDateTimeM;
	}

	public void setBeginDateTimeM(String beginDateTimeM) {
		this.beginDateTimeM = beginDateTimeM;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getEndDateTimeH() {
		return endDateTimeH;
	}

	public void setEndDateTimeH(String endDateTimeH) {
		this.endDateTimeH = endDateTimeH;
	}

	public String getEndDateTimeM() {
		return endDateTimeM;
	}

	public void setEndDateTimeM(String endDateTimeM) {
		this.endDateTimeM = endDateTimeM;
	}

	public int getIpAddressLength() {
		return 15;
	}

	public int getPortLength() {
		return 5;
	}

	public int getUserNameLength() {
		return 32;
	}

	public int getPasswdLength() {
		return 32;
	}

	public boolean isEndTime() {
		return endTime;
	}

	public void setEndTime(boolean endTime) {
		this.endTime = endTime;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}

	public boolean isDisabledEndTime() {
		return disabledEndTime;
	}

	public void setDisabledEndTime(boolean disabledEndTime) {
		this.disabledEndTime = disabledEndTime;
	}

	public boolean isDisabledRecur() {
		return disabledRecur;
	}

	public void setDisabledRecur(boolean disabledRecur) {
		this.disabledRecur = disabledRecur;
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

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean isSchedule() {
		return schedule;
	}

	public void setSchedule(boolean schedule) {
		this.schedule = schedule;
	}

	public String getHideSchedule() {
		return hideSchedule;
	}

	public void setHideSchedule(String hideSchedule) {
		this.hideSchedule = hideSchedule;
	}

	public String getDisabledStopSchedule() {
		return disabledStopSchedule;
	}

	public void setDisabledStopSchedule(String disabledStopSchedule) {
		this.disabledStopSchedule = disabledStopSchedule;
	}

	public String getBackupBtnName() {
		return backupBtnName;
	}

	public void setBackupBtnName(String backupBtnName) {
		this.backupBtnName = backupBtnName;
	}

	public String getHideImmediate() {
		return hideImmediate;
	}

	public void setHideImmediate(String hideImmediate) {
		this.hideImmediate = hideImmediate;
	}

	public String getDisabledEndCalButton() {
		if (endTime) {
			return "";
		}

		return "disabled";
	}

	public String getBackupFileName() {
		return backupFileName;
	}

	public void setBackupFileName(String backupFileName) {
		this.backupFileName = backupFileName;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public boolean getShowSchedule() {
		if (NmsUtil.isHostedHMApplication() && !getIsInHomeDomain()) {
			return false;
		}

		return true;
	}
	
	private String getLastBackupFilePath(boolean isInHomeDomain){
		
		String strBackupFilePath = null;
		String strBackupFile = null;
		String strBackupHome;
		String regex;
		String Host = NetTool.getHostName();
		if(isInHomeDomain){ // /HiveManager/tomcat/webapps/hm/WEB-INF/downloads/backup_dump_hm-1U-73.aerohive.com_061114155105.tar.gz 
			strBackupHome = HmContextListener.context.getRealPath("/WEB-INF/" + "downloads");
			//FILENAME="backup_dump_${HOST_NAME}_`date +%m%d%y%H%M%S`.tar.gz"
			//FILENAME="backup_${HOST_NAME}_`date +%m%d%y%H%M%S`.tar.gz"
			regex = "backup_(dump_)?"+Host+"_\\d+\\.tar\\.gz";
		} else { // /tmp/backup/Gotham_18/1402458010545/backup_hm-1U-73.aerohive.com_Gotham_18_1402458010545.tar.gz 
			strBackupHome = HHMConstant.BACKUP_DOWNLOAD_HOME+"/"+getDomain().getDomainName();
			//String strFileName = "backup_"+Host+"_"+oDomain.getDomainName()+"_"+strTmp+".tar.gz";
			regex = "backup_"+Host+"_"+getDomain().getDomainName()+"_\\d+\\.tar\\.gz";
		}
		
		String strBackupTmp = strBackupHome;
		File backupHomefile = new File(strBackupHome);
		if (backupHomefile.exists()) {
			String[] fileNames = backupHomefile.list();
			if(fileNames != null){
				Arrays.sort(fileNames);
				if(!isInHomeDomain){
					strBackupTmp += "/"+fileNames[fileNames.length-1];
					File backupSubfile = new File(strBackupTmp);
					fileNames = backupSubfile.list();
				}
				if(fileNames != null){
					for(String name : fileNames){
						if(name.matches(regex)){
							strBackupFile = name;
							break;
						}
					}
				}
			}
		}
		
		if(strBackupFile != null){
			strBackupFilePath = strBackupTmp + File.separator + strBackupFile;
		}
		return strBackupFilePath;
	}
	public boolean getShowLastBackup(){
		boolean result = false;
		String lastBackupFilePath = getLastBackupFilePath(getIsInHomeDomain());
		if(lastBackupFilePath != null){
			result = true;
			backupFileName = lastBackupFilePath.substring(lastBackupFilePath.lastIndexOf(File.separator)+1);
			inputPath = lastBackupFilePath;
			
			MgrUtil.setSessionAttribute(SESSIONKEY_FILENAME, backupFileName);
			MgrUtil.setSessionAttribute(SESSIONKEY_INPUTPATH, inputPath);
		}
		return result;
	}
	
	public String getLastBackupMsg(){
		String backupDate = null;
		
		backupFileName = (String) MgrUtil.getSessionAttribute(SESSIONKEY_FILENAME);
		inputPath = (String) MgrUtil.getSessionAttribute(SESSIONKEY_INPUTPATH);
		
		if(backupFileName != null){
			try {
				String Host = NetTool.getHostName();
				String regex = "backup_(dump_)?"+Host+"(_"+getDomain().getDomainName()+")?_(\\d+)\\.tar\\.gz";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(backupFileName);
				if(matcher.find()){
					String dateTmp = matcher.group(3);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					backupDate = df.format(new Date(Long.parseLong(dateTmp)));
				}
			} catch (Exception e) {
				log.debug("getLastBackupMsg() error, backupFileName=" + backupFileName);
			}
		}
		
		return MgrUtil.getUserMessage("backup.file.is.ready.message.last",new String[]{backupFileName,formatFileSize(new File(inputPath).length()),backupDate});
	}

	public EnumItem[] getScheduleProtocols() {
		return MgrUtil.enumItems("enum.restore.protocol.", new int[] {
				EnumConstUtil.RESTORE_PROTOCOL_SCP, EnumConstUtil.RESTORE_PROTOCOL_FTP });
	}

	public short getScheduleProtocol() {
		return scheduleProtocol;
	}

	public void setScheduleProtocol(short scheduleProtocol) {
		this.scheduleProtocol = scheduleProtocol;
	}

	private String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (size > 1024 * 1024) {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "MB");

		}
		if (size > 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			return (size + " Byte");
		}
	}

	public String getTransferServer() {
		return transferServer;
	}

	public void setTransferServer(String transferServer) {
		this.transferServer = transferServer;
	}

	public String getTransferPort() {
		return transferPort;
	}

	public void setTransferPort(String transferPort) {
		this.transferPort = transferPort;
	}

	public String getTransferFilePath() {
		return transferFilePath;
	}

	public void setTransferFilePath(String transferFilePath) {
		this.transferFilePath = transferFilePath;
	}

	public String getTransferUserName() {
		return transferUserName;
	}

	public void setTransferUserName(String transferUserName) {
		this.transferUserName = transferUserName;
	}

	public String getTransferPassword() {
		return transferPassword;
	}

	public void setTransferPassword(String transferPassword) {
		this.transferPassword = transferPassword;
	}
}