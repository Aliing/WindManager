package com.ah.ui.actions.home;

import static java.io.File.separator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.os.NetConfigureDTO;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmNtpServerAndInterval;
import com.ah.bo.admin.HmRoute;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.tca.HmTCAMonitorModule;
import com.ah.bo.tca.TCAAlarm;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ha.HAUtil;
import com.ah.nms.worker.report.Utils;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.admin.UpdateSoftwareAction;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.CasTool;
import com.ah.util.EnumItem;
import com.ah.util.HibernateDbConfigTool;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.BaseUtils;
import com.opensymphony.xwork2.ActionContext;

public class HmSettingsAction extends BaseAction implements HMSettingsConstant, QueryBo {

	private static final long				serialVersionUID		= 1L;

	private static final Tracer				log						= new Tracer(
																			HmSettingsAction.class
																					.getSimpleName());

	private int								timezone;
	
	private String							currentTimeShow;

	// sync mode
	private final String					SYNCMODE_MANUALLY		= "manually";

	private final String					SYNCMODE_NTP			= "syncNTP";
	
	private final String					SYNCMODE_DATE_TIME_FORMAT		= "dateFormatSettings";

	private String							syncMode				= SYNCMODE_MANUALLY;

	private String							dateTime;

	private String							hour;

	private String							minute;

	private String							second;

	public static final EnumItem[]			ENUM_HOURS				= createEnumItems(24, "hr");

	public static final EnumItem[]			ENUM_MINUTES			= createEnumItems(60, "min");

	public static final EnumItem[]			ENUM_SECONDES			= createEnumItems(60, "sec");

	private boolean							disabledManually;
	
	private boolean							disabledDateFormat;

	private boolean							disabledSyncNTP			= true;

	private boolean							ntpServiceStart			= true;

	private String							ntpServer				= HmNtpServerAndInterval.DEFAULT_NTP_SERVER;

	private int								ntpInterval				= 60;

	private String							hostName;

	private String							networkDomain;

	private String							primaryDNS;

	private String							secondDNS;

	private String							tertiaryDNS;

	private String							defaultGateway;

	private String							ip_eth0;

	private String							mask_eth0;

	private String							ip_eth1;

	private String							mask_eth1;

	private boolean							enableLan;

	private String							rate_eth0;

	private String							rate_eth1;

	private boolean							enableHA;

	private boolean                         hdnEnableHA;

	private String							haStatus;

	private String							haDomainName;

	private String							primaryHostName;

	private String							secondaryHostName;

	private String							primaryMGTIP;

	private String							primaryMGTMask;

	private String							secondaryMGTIP;

	//private String							secondaryMGTMask;

	private String							primaryLANIP;

	private String							hdnPrimaryLANIP;

	private String							primaryLANMask;

	private String							hdnPrimaryLANMask;

	private String							secondaryLANIP;

	//private String							secondaryLANMask;

	private String							primaryGateway;

	//private String							secondaryGateway;

	private String							haSecret;

	//private String							confirmHASecret;

	private boolean							enableFallback;

	private final String					HAPORT_MGT				= "mgt";

	private final String					HAPORT_LAN				= "lan";

	private String							haPort					= HAPORT_MGT;

	private String							haPrimaryDNS;

	private String							haSecondDNS;

	private String							haTertiaryDNS;

	private boolean							enableProxy;

	private String							proxyServer;

	private int								proxyPort;

	private String							proxyUserName;

	private String							proxyPassword;

	private boolean							haEnableProxy;

	private String							haProxyServer;

	private int								haProxyPort;

	private String							haProxyUserName;

	private String							haProxyPassword;

	private boolean							enableExternalIP;

	private String							primaryExternalIP;

	private String							secondaryExternalIP;

	private List<HmRoute>					routeList				= new ArrayList<HmRoute>();

	private static final short				INDEX_DESTINATION		= 0;

	private static final short				INDEX_NETMASK			= 1;

	private static final short				INDEX_GATEWAY			= 2;

	private String							routeDest;

	private String							routeMask;

	private String							routeGateway;

	private long							nextId					= 1;

	private String							accessControlTypeShow;

	private String							denyBehaviorShow;

	private List<AccessControlIPAddress>	ipAddressList			= new ArrayList<AccessControlIPAddress>();

	private short							accessControlType		= HmAccessControl.CONTROL_TYPE_DENY;

	private short							denyBehavior			= HmAccessControl.BEHAVIOR_TYPE_BLANK;

	private List<String>					allowedIps;

	private List<String>					deniedIps;

	private boolean							showDateTimeOption;

	private boolean							showRouteOption;

	private boolean							showNetworkOption;

	private boolean							showLoginAccessOption;

	private boolean 						showCertOption;

	private boolean 						showSSHOption;

	private boolean 						showSessionOption;

	private boolean 						showImproveOption;

	private boolean 						showExpressModeOption;

	private boolean 						showLogExpirationOption;

	private boolean 						showMaxUploadNumOption;
    
	private boolean                         showAPIOption;
	
	private String							joinHASecret;

	private String							joinHAPrimaryLanIP;
	
	private short timeType = HMServicesSettings.TIME_TYPE_1;
	private short dateFormat = HMServicesSettings.DATE_FORMAT_TYPE_1;
	private short timeFormat = HMServicesSettings.TIME_FORMAT_TYPE_1;
	private short dateSeparator = HMServicesSettings.DATE_SEPARATOR_TYPE_1;
	
	private String              dateTimeFormatString;

	// https cert
	private String				passPhrase;

	private String				certificateFile;

	private String				privateKeyFile;

	private final String		UPDATECERTTYPE_SELFSIGNED	= "selfSignedCert";

	private final String		UPDATECERTTYPE_IMPORT		= "importCert";

	// enum value {"selfSignedCert","importCert"}
	private String				updateCertType				= UPDATECERTTYPE_SELFSIGNED;

	// set visibility of import cert section
	// visible:"" invisible:"none"
	private String				hideImportCert				= "none";

	// set visibility of cert update section
	// visible:"" invisible:"none"
	private String				hideCertUpdate				= "none";

	// import cert file name
	private String				fileName;

	private int					sshPortNumber;

	private boolean				sshKeyGen;

	private int					genAlgorithm;

	private int					sessionExpiration;

	private boolean				disabledSession;
	
	private boolean				finiteSession;

	private String sessionExpirationShow;

	private boolean				participateImprovement;

	private String improveParticipateShow;

	private boolean				dataOfImprovement;

	private String dataOfImprovementShow;

	private boolean enableExpressMode;

	private String expressModeShow;

	private boolean containsExpressModeVHM;

	// system log expiration days
	private int		syslogExpirationDays;

	// audit log expiration days
	private int		auditlogExpirationDays;

	// l3Firewall log expiration days
	private int		l3FirewallLogExpirationDays;


	// HA email notify
	private String haNotifyEmail;

	private int maxUpdateNum;

	private byte concurrentConfigGenNum;

	private boolean showGenerationThreadsOption;

	//user for set concurrent search user number
	private byte concurrentSearchUserNum;

	private boolean showSearchUserNumOption;

    private String  locatePosition;

    private String haPrimaryDbIp;

    private String haPrimaryDbPwd;

    private String haSecondaryDbIp;

    private String haSecondaryDbPwd;

    // For IDM
    private boolean showCloudAuthServerOption;
    private boolean enabledBetaIDM;
    
    //For HM API
    
    private String apiUserName;
    private String apiPassword;
    private boolean isAddOperation;
    private boolean enableApiAccess;
    
    
 //tca alarm
    
    private boolean showTCAAlarmOption;
    private List<TCAAlarm> tcaAlarmList = new ArrayList<TCAAlarm>();
    
    private boolean enableCollectAppData;
    
    /*Supplemental CLI*/
    private boolean showSupplementalCLIOption;
    private boolean enableSupplementalCLI;
    

	public boolean isEnableCollectAppData() {
		return enableCollectAppData;
	}

	public void setEnableCollectAppData(boolean enableCollectAppData) {
		this.enableCollectAppData = enableCollectAppData;
	}

	/**
	 * @return the tcaAlarmList
	 */
	public List<TCAAlarm> getTcaAlarmList() {
		return tcaAlarmList;
	}

	/**
	 * @param tcaAlarmList the tcaAlarmList to set
	 */
	public void setTcaAlarmList(List<TCAAlarm> tcaAlarmList) {
		this.tcaAlarmList = tcaAlarmList;
	}

	/**
	 * @return the showTCAAlarmOption
	 */
	public boolean isShowTCAAlarmOption() {
		return showTCAAlarmOption;
	}

	/**
	 * @param showTCAAlarmOption the showTCAAlarmOption to set
	 */
	public void setShowTCAAlarmOption(boolean showTCAAlarmOption) {
		this.showTCAAlarmOption = showTCAAlarmOption;
	}
	
	private void initTCAAlarm() {
		tcaAlarmList = QueryUtil.executeQuery(TCAAlarm.class, null, null);

	}

	private void initCustomDeviceTag() {
		Map<String, String> cusMap = DeviceTagUtil.getInstance()
				.getClassifierCustomTag(this.getDomainId());
		customTag1 = cusMap.get(DeviceTagUtil.CUSTOM_TAG1);
		customTag2 = cusMap.get(DeviceTagUtil.CUSTOM_TAG2);
		customTag3 = cusMap.get(DeviceTagUtil.CUSTOM_TAG3);
	}
	
	private JSONObject getRefreshMeasureInfo(Long id){
		TCAAlarm bo=QueryUtil.findBoById( TCAAlarm.class, id );
		JSONObject o=new JSONObject();
		try {
			o.put("high", bo.getHighThreshold());
			o.put("low", bo.getLowThreshold());
			o.put("interval", bo.getInterval());
		} catch (JSONException e) {
		}
		return o;
		
		
	}
	
	private String tcaMeasureItem;
	private Long tcaHighThreshold;
	private Long tcaLowThreshold;
	private Long tcaInterval;

//	private String tcaDescription;
	

    private long selectedTcaMeasureItem;
    
    /**
	 * @return the selectedTcaMeasureItem
	 */
	public long getSelectedTcaMeasureItem() {
		return selectedTcaMeasureItem;
	}

	/**
	 * @param selectedTcaMeasureItem the selectedTcaMeasureItem to set
	 */
	public void setSelectedTcaMeasureItem(long selectedTcaMeasureItem) {
		this.selectedTcaMeasureItem = selectedTcaMeasureItem;
	}
	
	/**
	 * @return the tcaInterval
	 */
	public Long getTcaInterval() {
		return tcaInterval;
	}

	/**
	 * @param tcaInterval the tcaInterval to set
	 */
	public void setTcaInterval(Long tcaInterval) {
		this.tcaInterval = tcaInterval;
	}


	/**
	 * @return the tcaMeasureItem
	 */
	public String getTcaMeasureItem() {
		return tcaMeasureItem;
	}

	/**
	 * @param tcaMeasureItem the tcaMeasureItem to set
	 */
	public void setTcaMeasureItem(String tcaMeasureItem) {
		this.tcaMeasureItem = tcaMeasureItem;
	}

	/**
	 * @return the tcaHighThreshold
	 */
	public Long getTcaHighThreshold() {
		return tcaHighThreshold;
	}

	/**
	 * @param tcaHighThreshold the tcaHighThreshold to set
	 */
	public void setTcaHighThreshold(Long tcaHighThreshold) {
		this.tcaHighThreshold = tcaHighThreshold;
	}

	/**
	 * @return the tcaLowThreshold
	 */
	public Long getTcaLowThreshold() {
		return tcaLowThreshold;
	}

	/**
	 * @param tcaLowThreshold the tcaLowThreshold to set
	 */
	public void setTcaLowThreshold(Long tcaLowThreshold) {
		this.tcaLowThreshold = tcaLowThreshold;
	}
	
	public String getDateTimeFormatString(){
		if(timeType == HMServicesSettings.TIME_TYPE_1){
			if(dateFormat == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(dateSeparator == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}else{
				if(dateSeparator == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}
		}else {
			if(dateFormat == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(dateSeparator == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}else{
				if(dateSeparator == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}
		}
		return dateTimeFormatString;
	}
//	/**
//	 * @return the tcaDescription
//	 */
//	public String getTcaDescription() {
//		return tcaDescription;
//	}
//
//	/**
//	 * @param tcaDescription the tcaDescription to set
//	 */
//	public void setTcaDescription(String tcaDescription) {
//		this.tcaDescription = tcaDescription;
//	}
	
	 private boolean updateTCAAlarm(){
		try {
			boolean result=false;
			TCAAlarm bo=QueryUtil.findBoById( TCAAlarm.class, Long.valueOf( selectedTcaMeasureItem ) );
			 if (null == bo) {
			    return result;
			 } else {
			     bo.setHighThreshold(tcaHighThreshold);
			     bo.setLowThreshold(tcaLowThreshold);
			     bo.setInterval(tcaInterval);
//			     bo.setDescription(tcaDescription.trim());
			     QueryUtil.updateBo(bo);
			 }
			 result = HmTCAMonitorModule.getInstance().updateTask(bo);
			 return result;
		} catch (Exception e) {
			log.error("updateTCAAlarm",
                    "Update TCAAlarm settings catch exception!", e);
            return false;
		}
		
	}
	
	
/*---------------------------------------end of tca alarm------------------------------------*/
	 //for custom device tag
	   private boolean showDeviceTagOption;
		private String customTag1;
		private String customTag2;
		private String customTag3;
		
		public String getCustomTag1() {
			return customTag1;
		}
		public void setCustomTag1(String customTag1) {
			this.customTag1 = customTag1;
		}
		public String getCustomTag2() {
			return customTag2;
		}
		public void setCustomTag2(String customTag2) {
			this.customTag2 = customTag2;
		}
		public String getCustomTag3() {
			return customTag3;
		}

		public void setCustomTag3(String customTag3) {
			this.customTag3 = customTag3;
		}
		public boolean isShowDeviceTagOption() {
			return showDeviceTagOption;
		}

		public void setShowDeviceTagOption(boolean showDeviceTagOption) {
			this.showDeviceTagOption = showDeviceTagOption;
		}
		
		/******************       for client profile        ******************/
		private boolean showClientProfileOption;
		private boolean enabledClientProfile;
		private String  enabledClientProfileShow;
		
		public boolean isEnabledClientProfile(){
			return enabledClientProfile;
		}
		
		public void setEnabledClientProfile(boolean enabledClientProfile){
			this.enabledClientProfile = enabledClientProfile;
		}
		
		public String getEnabledClientProfileShow(){
			return this.enabledClientProfileShow;
		}
		
		public void setEnabledClientProfileShow(String enabledClientProfileShow){
			this.enabledClientProfileShow = enabledClientProfileShow;
		}
		
		public void setShowClientProfileOption(boolean showClientProfileOption){
			this.showClientProfileOption = showClientProfileOption;
		}
		
		public boolean isShowClientProfileOption(){
			return this.showClientProfileOption;
		}
    
    //for db separation

	//remote database server ip adddress
	private static final String EXTERN_DB_IP_KEY      = "host";
	//remote database server port
	private static final String EXTERN_DB_PORT_KEY    = "port";
	//ssh admin user
	private static final String LOCAL_USER_KEY        = "user";
	//ssh admin password
	private static final String LOCAL_PASSWORD_KEY    = "password";
	private static final String CONFIG_FILE_NAME = "extdbsettings.properties";
	//property file for storing remote database connected info
	private static final String CONFIG_FILE_PATH      = separator+"HiveManager"+separator+"shell"+separator+CONFIG_FILE_NAME;
	
	private String                          remoteDbIp;
	
	private String                          remoteDbSshPwd;
	
	private boolean                         enableExternalDb;
	
	private boolean                         enableSeparateExternalDb;
	
	private String                          externalDbIpInfo;
	
	private boolean                         externalDbIpInfoDisplay = false;
	//for switch over db
	private boolean switchOverDb = false;
	
	private boolean enableRadarDetection;
	
	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("refresh".equals(operation)) {
				initValue();
				return SUCCESS;
			} else if("refreshMeasureInfo".equals(operation)){
				Long id=Long.parseLong(request.getParameter("measureId"));
				jsonObject = getRefreshMeasureInfo(id);
				return "json";
			}
			else if ("updateDateTime".equals(operation)) {
				jsonObject = new JSONObject();
				if(syncMode.equalsIgnoreCase(SYNCMODE_DATE_TIME_FORMAT)){
					HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
					bo.setDateFormat(dateFormat == 0 ? HMServicesSettings.DATE_FORMAT_TYPE_1 : dateFormat);
					bo.setTimeFormat(timeFormat == 0 ? HMServicesSettings.TIME_FORMAT_TYPE_1 : timeFormat);
					bo.setDateSeparator(dateSeparator == 0 ? HMServicesSettings.DATE_SEPARATOR_TYPE_1 : dateSeparator);
					bo.setTimeType(timeType == 0 ? HMServicesSettings.TIME_TYPE_1 : timeType);
					QueryUtil.updateBo(bo);
					CacheMgmt.getInstance().resetHMServiceSettingsForTimeZone(getDomain(), bo);
				}
				if (getIsInHomeDomain()) {
					String errorMessage = updateDateTimeConfig();
					String message;
					if ("".equals(errorMessage)) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.server.time"));
						// addActionMessage(HmBeResUtil.getString("success_dateTimeConfig"));
						message = HmBeResUtil.getString("datetime.update.success");
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.update.server.time"));
						// addActionError(HmBeResUtil.getString("error_update"));
						message = HmBeResUtil.getString("datetime.update.error") + " "
								+ errorMessage;
					}

					if (errorMessage != null && errorMessage.trim().length() > 0) {
						jsonObject.put("succ", false);
						jsonObject.put("message", message);
						return "json";
					}

					// update home domain time zone
					if (!updateDomainTimeZone()) {
						jsonObject.put("succ", false);
						jsonObject.put("message", MgrUtil.getUserMessage("hm.setting.unable.update.time.zone.message"));
						return "json";
					}

					jsonObject.put("succ", true);
					jsonObject.put("message", message);
					jsonObject.put("restart", true);

					return "json";
				} else {
					// vhm only permit update time zone.
					if (updateDomainTimeZone()) {
						Calendar calendar = HmBeOsUtil.getServerTime();
						initCurrentTimeShow(calendar);

						jsonObject.put("succ", true);
						jsonObject.put("currentTime", currentTimeShow);
						jsonObject.put("message", MgrUtil.getUserMessage("hm.setting.update.time.zone.success.message"));
					} else {
						jsonObject.put("succ", false);
						jsonObject.put("message",  MgrUtil.getUserMessage("hm.setting.unable.update.time.zone.message"));
					}

					return "json";
				}
			} else if ("restartHM".equals(operation)) {
				Thread.sleep(5000);
				boolean isSucc = HmBeAdminUtil.restartSoft();

				jsonObject = new JSONObject();
				jsonObject.put("succ", isSucc);

				return "json";
			} else if ("updateNetwork".equals(operation)) {
				initValue(updateNetworkSettings());
				return SUCCESS;
			} else if ("checkRouteValid".equals(operation)) {
				String message = checkRouteValid(routeDest, routeMask, routeGateway);
				jsonObject = new JSONObject();
				jsonObject.put("valid", message == null || message.trim().length() == 0);
				jsonObject.put("message", message);

				return "json";
			} else if ("addRoute".equals(operation)) {
				String message = HmBeOsUtil.addRoute(new String[] { routeDest, routeMask,
						routeGateway });
				if (message == null) {
					// succeed
					addActionMessage(MgrUtil.getUserMessage("message.create.new.route.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.add.a.route"));
				} else {
					// failed
					addActionError(message);
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.add.a.route"));
				}

				initValue();
				showRouteOption = true;
				return SUCCESS;
			} else if ("removeRoute".equals(operation)) {
				int removeCount = removeRoutes(getAllSelectedIds());
				if (removeCount > 0) {
					addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, removeCount + ""));
				}

				initValue();
				showRouteOption = true;
				return SUCCESS;
			} else if ("updateAccessControl".equals(operation)) {
				updateAccessControl();

				initValue();
				return SUCCESS;
			} else if ("haJoin".equals(operation)) {
				haJoin();

				initValue();
				return SUCCESS;
			} else if ("checkHAOnline".equals(operation)) {
				int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
						+ "check_ha_online_master.sh");

				jsonObject = new JSONObject();
				jsonObject.put("success", exitValue == 0);
				if (exitValue != 0) {
					jsonObject.put("error", getHAOperationExitMessage(exitValue));
				}

				return "json";
			} else if ("haSwitch".equals(operation)) {
				haSwitchOver();

				initValue();
				return SUCCESS;
			} else if ("testLS".equals(operation)) {
				String result = ClientSenderCenter.testForConnectingToLS(enableProxy, proxyServer, proxyPort, proxyUserName, proxyPassword);
				jsonObject = new JSONObject();
				jsonObject.put("message", result);
				return "json";
			} else if ("importCert".equals(operation)) {
				clearErrorsAndMessages();
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				MgrUtil.setSessionAttribute(getSelectedL2Feature().getKey() + "_installType",
						"cert");
				MgrUtil.setSessionAttribute("certificateFile_install", certificateFile);
				MgrUtil.setSessionAttribute("privateKey_install", privateKeyFile);

				return "newFile";
			} else if ("importKey".equals(operation)) {
				clearErrorsAndMessages();
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				MgrUtil
						.setSessionAttribute(getSelectedL2Feature().getKey() + "_installType",
								"key");
				MgrUtil.setSessionAttribute("certificateFile_install", certificateFile);
				MgrUtil.setSessionAttribute("privateKey_install", privateKeyFile);

				return "newFile";
			} else if ("continue".equals(operation)) {
				String installType = (String) MgrUtil.getSessionAttribute(getSelectedL2Feature()
						.getKey()
						+ "_installType");
				if (installType.equals("cert")) {
					if (fileName != null && fileName.length() > 0) {
						certificateFile = fileName;
					} else {
						certificateFile = (String) MgrUtil
								.getSessionAttribute("certificateFile_install");
					}
					MgrUtil.removeSessionAttribute("certificateFile_install");

					privateKeyFile = (String) MgrUtil.getSessionAttribute("privateKey_install");
					MgrUtil.removeSessionAttribute("privateKey_install");
				} else {
					if (fileName != null && fileName.length() > 0) {
						privateKeyFile = fileName;
					} else {
						privateKeyFile = (String) MgrUtil.getSessionAttribute("privateKey_install");
					}
					MgrUtil.removeSessionAttribute("privateKey_install");

					certificateFile = (String) MgrUtil
							.getSessionAttribute("certificateFile_install");
					MgrUtil.removeSessionAttribute("certificateFile_install");
				}

				initImportFileContinueValue();

				removeLstTitle();
				removeLstForward();

				return SUCCESS;
			} else if ("updateCert".equals(operation)) {
				try {
					installCertificate();

					addActionMessage(HmBeResUtil.getString("mgmtSettings.cert.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.install.certificate"));
				} catch (BeOperateException e) {
					addActionError(HmBeResUtil.getString("mgmtSettings.cert.update.error")
							+ " " + e.getMessage());
					log.error("execute", "install cert catch exception", e);
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.install.certificate"));
				} catch (Exception e) {
					addActionError(HmBeResUtil.getString("mgmtSettings.cert.update.error")
							+ " " + e.getMessage());
					log.error("execute", "Install certificate failed!", e);
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.install.certificate"));
				}

				initValue();
				return SUCCESS;
			} else if ("updateSSH".equals(operation)) {
				boolean isSucc = HmBeAdminUtil.setSshdPort(sshPortNumber);
				if (isSucc) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.sshscp.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.sshscp.settings"));
				} else {
					addActionError(HmBeResUtil.getString("mgmtSettings.sshscp.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,MgrUtil.getUserMessage("hm.audit.log.update.sshscp.settings"));
				}

				if (sshKeyGen) {
					boolean isSuccess = HmBeAdminUtil
							.generateAuthkeys(genAlgorithm == ALGORITHM_RSA ? "rsa" : "dsa");
					if (isSuccess) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.generate.ssh.key"));
						addActionMessage(HmBeResUtil
								.getString("mgmtSettings.sshkey.update.success"));
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.generate.ssh.key"));
						addActionError(HmBeResUtil
								.getString("mgmtSettings.sshkey.update.error"));
					}
				}

				initValue();
				return SUCCESS;
			} else if ("updateSession".equals(operation)) {
				boolean isSuccess = updateSessionExpiration();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.adminSession.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.admin.session.expiration"));
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.adminSession.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.admin.session.expiration"));
				}

				initValue();
				return SUCCESS;
			} else if ("updateImprove".equals(operation)) {
				boolean isSuccess = saveImprovementSettings();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.improvement.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.produce.improvement.settings"));
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.improvement.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.produce.improvement.settings"));
				}

				initValue();
				return SUCCESS;
			} else if("updateExpressMode".equals(operation)){
				// express mode setting
				boolean isSuccess = updateExpressModeSettings();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.expressMode.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.express.mode.settings"));
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.expressMode.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.express.mode.settings"));
				}
				initValue();
				return SUCCESS;
			} else if("updateLogExpiration".equals(operation)){
				// update log expiration days
				updateLogExpiration();

				initValue();
				return SUCCESS;
			} else if ("updateMaxUpdate".equals(operation)) {
				boolean isSuccess = updateMaxUpdateNum();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.maxUpdateNum.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.max.hiveos.soft.one.time.settints"));
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.maxUpdateNum.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.max.hiveos.soft.one.time.settints"));
				}
				initValue();
				return SUCCESS;
			}else if ("updateConcurrentConfigGenNum".equals(operation)) {
				boolean isSuccess = updateConcurrentConfigGenNum();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.maxGenerations.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.max.concurrrent.hiveos.config") + concurrentConfigGenNum);
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.maxGenerations.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.max.concurrent.hiveos.config.failure"));
				}
				initValue();
				return SUCCESS;
			}else if ("updateConcurrentSearchUserNum".equals(operation)) {
				boolean isSuccess = updateConcurrentSearchUserNum();
				if (isSuccess) {
					addActionMessage(HmBeResUtil
							.getString("mgmtSettings.maxSearchUser.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("hm.audit.log.update.maximum.concurrent.user.count.settings") + concurrentSearchUserNum );
				} else {
					addActionError(HmBeResUtil
							.getString("mgmtSettings.maxSearchUser.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.update.maximum.concurrent.user.count.settings.failure"));
				}
				initValue();
				return SUCCESS;
			} else if ("updateCloudAuthServer".equals(operation)) {
			    boolean isSuccess = updateCloudAuthServer();
			    if(isSuccess) {
			    	if (!NmsUtil.isHostedHMApplication()) {
			    		BaseUtils.refreshPortalUrlForRestApi();
			    	}
			        addActionMessage(HmBeResUtil
			                .getString("mgmtSettings.cloudAuthServer.update.success"));
			        generateAuditLog(HmAuditLog.STATUS_SUCCESS,
			                MgrUtil.getUserMessage("hm.audit.log.update.cloudauth.server.settings"));
			    } else {
			        addActionError(HmBeResUtil
			                .getString("mgmtSettings.cloudAuthServer.update.error"));
			        generateAuditLog(HmAuditLog.STATUS_FAILURE,
			        		MgrUtil.getUserMessage("hm.audit.log.update.cloudauth.server.settings.failure"));
			    }
			    initValue();
			    return SUCCESS;
			}else if("updateTCAAlarm".equals(operation)){
				 boolean isSuccess = updateTCAAlarm();
				    if(isSuccess) {
				        addActionMessage(HmBeResUtil
				                .getString("hmSettings.tcaAlarm.diskusage.update.success"));
				        generateAuditLog(HmAuditLog.STATUS_SUCCESS,
				                MgrUtil.getUserMessage("home.hmSettings.tcaalarm.server.settings"));
				    } else {
				        addActionError(HmBeResUtil
				                .getString("hmSettings.tcaAlarm.diskusage.update.error"));
				        generateAuditLog(HmAuditLog.STATUS_FAILURE,
				        		MgrUtil.getUserMessage("home.hmSettings.tcaalarm.server.settings.failure"));
				    }
				    initValue();
				    return SUCCESS;
			}else if ("updateDFSSetting".equals(operation)) {
                boolean isSuccess = updateDFSSetting();
                updateAllRadioProfiles();
                if(isSuccess) {
                    addActionMessage(HmBeResUtil
                            .getString("mgmtSettings.dfssetting.update.success"));
                    generateAuditLog(HmAuditLog.STATUS_SUCCESS,
                            MgrUtil.getUserMessage("hm.audit.log.update.dfssetting.update.radardetection.success"));
                } else {
                    addActionError(HmBeResUtil
                            .getString("mgmtSettings.dfssetting.update.error"));
                    generateAuditLog(HmAuditLog.STATUS_FAILURE,
                            MgrUtil.getUserMessage("hm.audit.log.update.dfssetting.update.radardetection.failer"));
                }
                initValue();
                return SUCCESS;
            }else if("updateDeviceTag".equals(operation)){				
				Map<String, Object> map=ActionContext.getContext().getParameters();				
				String deviceTag1 = DeviceTagUtil.getInstance().getUrlValue(map,"customTag1");		
				String deviceTag2 = DeviceTagUtil.getInstance().getUrlValue(map,"customTag2");		
				String deviceTag3 = DeviceTagUtil.getInstance().getUrlValue(map,"customTag3");		
				boolean isSuccess=DeviceTagUtil.getInstance().updateClassifierTagSetting("Tag1="+deviceTag1+"*Tag2="+deviceTag2+"*Tag3="+deviceTag3,domainId);	
				    if(isSuccess) {
				        addActionMessage(HmBeResUtil
				                .getString("mgmtSettings.classifierTag.update.success"));
				        generateAuditLog(HmAuditLog.STATUS_SUCCESS,
				                MgrUtil.getUserMessage("hm.audit.log.update.classifiertag.server.settings"));
				    } else {
				        addActionError(HmBeResUtil
				                .getString("mgmtSettings.classifierTag.update.error"));
				        generateAuditLog(HmAuditLog.STATUS_FAILURE,
				        		MgrUtil.getUserMessage("hm.audit.log.update.classifiertag.server.settings.failure"));
				    }
				    initValue();
				    return SUCCESS;
			}else if("updateClientProfile".equals(operation)){
			    certificateOperation();
			    initValue();
			    return SUCCESS;
			}else if("updateAPISettings".equals(operation)){
				  if(!checkApiUserNameExists()){
					  boolean isSuccess=updateAPISettings();
					    if (isSuccess) {
							addActionMessage(MgrUtil.getUserMessage("mgmtSettings.APIsetting.update.success"));
							generateAuditLog(
									HmAuditLog.STATUS_SUCCESS,
									MgrUtil.getUserMessage("home.hmSettings.editAPI"));
						 } else {
							addActionError(HmBeResUtil
									.getString("mgmtSettings.APIsetting.update.failure"));
							generateAuditLog(
									HmAuditLog.STATUS_FAILURE,
									MgrUtil.getUserMessage("home.hmSettings.editAPI"));
						}
				   }
	               initValue();
	               return SUCCESS;
			}else if ("updateSupplementalCLI".equals(operation)) {
                boolean isSuccess = updateSupplementalCLI();
                if(isSuccess) {
                    addActionMessage(HmBeResUtil
                            .getString("mgmtSettings.supplemental.cli.update.success"));
                    generateAuditLog(HmAuditLog.STATUS_SUCCESS,
                            MgrUtil.getUserMessage("hm.audit.log.update.supplemental.cli.update.success"));
                } else {
                    addActionError(HmBeResUtil
                            .getString("mgmtSettings.supplemental.cli.update.error"));
                    generateAuditLog(HmAuditLog.STATUS_FAILURE,
                            MgrUtil.getUserMessage("hm.audit.log.update.supplemental.cli.failer"));
                }
                initValue();
                return SUCCESS;
            }else {
				initValue();
				return SUCCESS;
			}
		}catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", "catch exception", e);
			try {
				initValue();
			} catch (Exception ex) {
				log.error("initValue", "catch exception", ex);
			}
			return SUCCESS;
		}
	}

    @Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HM_SETTINGS);
	}

	private void initValue() throws Exception {
		initValue(true);
	}

	private void initValue(boolean netUpdate) throws Exception {
		initDateTimeValues();

		if (netUpdate)
			initNetworkValues();
		
		initExternalDbSetting();

		initRoutingValues();

		initLoginAccessValues();

		initCertValues();

		initSSHValues();

		initSessionExpiration();

		initImprovementSettings();
		// express mode
		initExpressModeSettings();
		// System/Audit/l3Firewall Log Expiration Days
		initLogExpirationSettings();

		initMaxUpdateNum();

		initConcurrentConfigGenNum();

		initConcurrentSearchUserNum();

		initCloudAuthServer();
		
		initTCAAlarm();
		initCustomDeviceTag();
		
		initDFSSetting();
		
		initClientProfile();
		
		initAPISetting();
		
		initSupplementalCLISetting();
	}

	private void initExternalDbSetting(){
		List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
		if (!list.isEmpty() && list.size()>0) {
			HASettings haSettings = list.get(0);
			if(haSettings.getEnableExternalDb() == HASettings.EXTERNALDB_DISABLEHA_REMOTE){
				externalDbIpInfo = haSettings.getPrimaryDbUrl();
				externalDbIpInfoDisplay = true;
			}
		}
	}
	
    private void initLogExpirationSettings() {
		LogSettings logSettings = QueryUtil.findBoByAttribute(LogSettings.class, "owner", getDomain());
		if (null == logSettings) {
			syslogExpirationDays = LogSettings.DEFAULT_SYSLOG_EXPIRATIONDAYS;
			auditlogExpirationDays = LogSettings.DEFAULT_AUDITLOG_EXPIRATIONDAYS;
			l3FirewallLogExpirationDays = LogSettings.DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS;
		} else {
			syslogExpirationDays = logSettings.getSyslogExpirationDays();
			auditlogExpirationDays = logSettings.getAuditlogExpirationDays();
			l3FirewallLogExpirationDays = logSettings.getL3FirewallLogExpirationDays();
		}
	}

	private void initExpressModeSettings() {
		if (getIsInHomeDomain()) {
			// If current setting is Express Mode enabled,
			// if any vHM(include Home domain) already use Express Mode,
			// the setting change isn't allowed.
			List<HmStartConfig> expressModeVHMs = QueryUtil.executeQuery(
					HmStartConfig.class, null, new FilterParams("modeType",HmStartConfig.HM_MODE_EASY ));
			if(expressModeVHMs.isEmpty()){
				containsExpressModeVHM=false;
			}else{
				containsExpressModeVHM=true;
				return;
			}

			List<HmExpressModeEnable> settings = QueryUtil.executeQuery(
					HmExpressModeEnable.class, null, null);
			if (settings.isEmpty()) {
				enableExpressMode = NmsUtil.getOEMCustomer()
						.getExpressModeEnable();
			} else {
				enableExpressMode = settings.get(0).isExpressModeEnable();
			}

			expressModeShow = enableExpressMode ? "Yes" : "No";
		}
	}

	private void initImprovementSettings() {
		if (getDomain().isHomeDomain()) {
			LicenseServerSetting lserverInfo = HmBeActivationUtil.getLicenseServerInfo();
			participateImprovement = lserverInfo.isSendStatistic();
			improveParticipateShow = participateImprovement ? "Yes" : "No";

			dataOfImprovement = lserverInfo.isSendStatistic();
			dataOfImprovementShow = dataOfImprovement ? "Yes" : "No";
		}
		HMServicesSettings servicesSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", getDomainId());
		enableCollectAppData = servicesSetting.isEnableCollectAppData();
		
	}

	@Override
	public void initSessionExpiration() {
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());

		if (bo == null) {
			log.debug("initSessionExpiration", "No settings bo in db!");
			sessionExpiration = request.getSession().getMaxInactiveInterval() / 60;
			finiteSession = true;
			disabledSession = false;

			return;
		}

		sessionExpiration = bo.getSessionExpiration();
		finiteSession = !bo.isInfiniteSession();
		if (bo.isInfiniteSession()) {
			disabledSession = true;
		}
		sessionExpirationShow = bo.isInfiniteSession() ? "Never" : bo.getSessionExpiration() + " minutes";
	}

	private void initMaxUpdateNum() {
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
		if(bo == null){
			maxUpdateNum = HMServicesSettings.MAX_HIVEOS_SOFTVER_UPDATE_NUM;
		}else {
			maxUpdateNum = bo.getMaxUpdateNum();
		}
	}

	private void initConcurrentConfigGenNum() {
		List<?> list = QueryUtil.executeQuery("select concurrentConfigGenNum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner", getDomain()), 1);
		if(list.isEmpty()){
			concurrentConfigGenNum = HMServicesSettings.DEFAULT_CONCURRENT_CONFIG_GEN_NUM;
		}else {
			concurrentConfigGenNum = (Byte)list.get(0);
		}
	}

	private void initConcurrentSearchUserNum() {
		List<?> list = QueryUtil.executeQuery("select concurrentSearchUserNum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner", getDomain()), 1);
		if(list.isEmpty()){
			concurrentSearchUserNum = HMServicesSettings.DEFAULT_CONCURRENT_SEARCH_USER_NUM;
		}else {
			concurrentSearchUserNum = (Byte)list.get(0);
		}
	}

	private void initSSHValues()
	{
		sshPortNumber = HmBeAdminUtil.getSshdPort();
		sshKeyGen = false;
		genAlgorithm = ALGORITHM_RSA;
	}

	private static final int	ALGORITHM_RSA	= 1;
	private static final int	ALGORITHM_DSA	= 2;

	public static EnumItem[] getEnumAlgorithm() {
		return MgrUtil.enumItems("enum.sshKeyGen.algorithm.", new int[] { ALGORITHM_RSA,
				ALGORITHM_DSA });
	}

	private void initCertValues()
	{
		// cert
		updateCertType = UPDATECERTTYPE_SELFSIGNED;
		hideImportCert = "none";
		hideCertUpdate = "none";
		certificateFile = null;
		privateKeyFile = null;
		passPhrase = null;
	}

	private void initCurrentTimeShow(Calendar calendar) {
		// date format of Date.toString()
		SimpleDateFormat formatter = new SimpleDateFormat(getDateTimeFormatString());
		formatter.setTimeZone(getDomain().getTimeZone());
		currentTimeShow = formatter.format(calendar.getTime());

		// get time zone, Don't use underscores for the names of countries and cities in time zones
		// String timeZoneShow = HmBeOsUtil.getTimeZoneString(timezone).replaceAll("_", " ");
		currentTimeShow += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + HmBeOsUtil.getTimeZoneStringWhole(timezone).replace(" ", "&nbsp;");

//		String timezoneOffSet = HmBeOsUtil.getTimeZoneOffSet(timezone);
//		String[] offSetInt = timezoneOffSet.split(":");
//		String utcShow = "GMT" + (Integer.valueOf(offSetInt[0]) > 0 ? ("+" + timezoneOffSet) : timezoneOffSet);
//		currentTimeShow += "&nbsp;&nbsp;" + utcShow;
	}

	private void initDateTimeValues() {
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
		dateFormat = bo.getDateFormat() != 0 ? bo.getDateFormat() : HMServicesSettings.DATE_FORMAT_TYPE_1;
		dateSeparator = bo.getDateSeparator() != 0 ? bo.getDateSeparator() : HMServicesSettings.DATE_SEPARATOR_TYPE_1;
		timeFormat = bo.getTimeFormat() != 0 ? bo.getTimeFormat() : HMServicesSettings.TIME_FORMAT_TYPE_1;
		timeType = bo.getTimeType() != 0 ? bo.getTimeType() : HMServicesSettings.TIME_TYPE_1;
		try {
			if (getUserContext().getDomain().isHomeDomain()) {
				if (getUserContext().getSwitchDomain() == null) {
					timezone = HmBeOsUtil.getServerTimeZoneIndex(null);
				} else {
					timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getSwitchDomain()
							.getTimeZoneString());
				}
			} else {
				timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getDomain()
						.getTimeZoneString());
			}
			
			// set date time
			Calendar calendar = HmBeOsUtil.getServerTime();

			initCurrentTimeShow(calendar);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(getDomain().getTimeZone());
			dateTime = formatter.format(calendar.getTime());
			hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
			minute = String.valueOf(calendar.get(Calendar.MINUTE));
			second = String.valueOf(calendar.get(Calendar.SECOND));

			// if HiveManager is an NTP server
			ntpServiceStart = HmBeOsUtil.ifNTPServiceStart();

			// if HiveManager synchronize with an NTP server
			HmNtpServerAndInterval ntpBo = QueryUtil.findBoByAttribute(
					HmNtpServerAndInterval.class, "timeType", BeOsLayerModule.STOP_NTP_SERVICE);
			syncMode = null != ntpBo ? SYNCMODE_NTP : SYNCMODE_MANUALLY;
			if (syncMode.equals(SYNCMODE_MANUALLY)) {
				disabledManually = false;
				disabledSyncNTP = true;
				disabledDateFormat = true;
			} else if(syncMode.equals(SYNCMODE_DATE_TIME_FORMAT)){
				disabledSyncNTP = true;
				disabledManually = true;
				disabledDateFormat = false;
			} else {
				disabledManually = true;
				disabledSyncNTP = false;
				disabledDateFormat = true;
				ntpServer = ntpBo.getNtpServer();
				ntpInterval = ntpBo.getNtpInterval();
			}
		} catch (Exception e) {
			log.error("initDateTimeValues", "catch exception", e);
		}
	}

	private final String	INTERFACESETTINGS_CACHE	= "cache_interfacesettings";

	private final String	HASETTINGS_CACHE		= "cache_hasettings";

	private void initNetworkValues() {
		try {
			NetConfigureDTO netConfig = HmBeOsUtil.getNetConfig();
			hostName = netConfig.getHostName();
			networkDomain = netConfig.getDomainName();
			defaultGateway = netConfig.getGateway();
			primaryDNS = netConfig.getPrimaryDns();
			secondDNS = netConfig.getSecondDns();
			tertiaryDNS = netConfig.getTertiaryDns();
			ip_eth0 = netConfig.getIpAddress_eth0();
			mask_eth0 = netConfig.getNetmask_eth0();
			enableLan = netConfig.isEnabled_eth1();
			ip_eth1 = netConfig.getIpAddress_eth1();
			mask_eth1 = netConfig.getNetmask_eth1();

			// speed & duplex
			List<String> mgtInfos = HmBeAdminUtil.getEthInfo("eth0");

			rate_eth0 = mgtInfos.get(1) + " " + mgtInfos.get(2);

			List<String> lanInfos = HmBeAdminUtil.getEthInfo("eth1");
			if (lanInfos.get(0).equalsIgnoreCase("off")) {
				rate_eth1 = " ";
			} else {
				rate_eth1 = lanInfos.get(1) + " " + lanInfos.get(2);
			}

			// Change 'Mb/s' to 'Mbps' and add a space between the number and the unit '1000 Mbps'
			if (rate_eth0.trim().length() == 0) {
				rate_eth0 = "unknown";
			} else {
				rate_eth0 = rate_eth0.replaceAll("Mb/s", " Mbps");
			}

			if (rate_eth1.trim().length() == 0) {
				rate_eth1 = "unknown";
			} else {
				rate_eth1 = rate_eth1.replaceAll("Mb/s", " Mbps");
			}

			// ha values
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
			if (list.isEmpty()) {
				return;
			}

			HASettings haSettings = list.get(0);
			enableHA = haSettings.isEnableHA();
			hdnEnableHA = haSettings.isEnableHA();
			enableFallback = haSettings.isEnableFailBack();
			secondaryHostName = haSettings.getSecondaryHostName();
			secondaryMGTIP = haSettings.getSecondaryMGTIP();
			//secondaryMGTMask = haSettings.getSecondaryMGTNetmask();
			secondaryLANIP = haSettings.getSecondaryLANIP();
			//secondaryLANMask = haSettings.getSecondaryLANNetmask();
			//secondaryGateway = haSettings.getSecondaryDefaultGateway();
			if(enableHA){
				if(haSettings.getEnableExternalDb() != HASettings.EXTERNALDB_DISABLEHA_INITIAL){
					enableExternalDb = true;
					haPrimaryDbIp = haSettings.getPrimaryDbUrl();
					haSecondaryDbIp = haSettings.getSecondaryDbUrl();
					haPrimaryDbPwd = haSettings.getPrimaryDbPwd();
					haSecondaryDbPwd = haSettings.getSecondaryDbPwd();
				}else{
					enableExternalDb = false;
					haPrimaryDbIp = "";
					haSecondaryDbIp = "";
					haPrimaryDbPwd = "";
					haSecondaryDbPwd = "";
				}
				int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
						+ "check_cross_ha.sh");
				if (exitValue == 0) {
					switchOverDb = true;
				} else {
					switchOverDb = false;
				}
			}else{
				if(haSettings.getEnableExternalDb() != HASettings.EXTERNALDB_DISABLEHA_INITIAL){
					enableSeparateExternalDb = true;
					remoteDbIp = haSettings.getPrimaryDbUrl();
					remoteDbSshPwd = haSettings.getPrimaryDbPwd();
				}else{
					enableSeparateExternalDb = false;
					remoteDbIp = "";
					remoteDbSshPwd = "";
				}
			}
			primaryHostName = enableHA ? haSettings.getPrimaryHostName() : hostName;
			haDomainName = enableHA ? haSettings.getDomainName() : networkDomain;
			primaryMGTIP = enableHA ? haSettings.getPrimaryMGTIP() : ip_eth0;
			primaryMGTMask = enableHA ? haSettings.getPrimaryMGTNetmask() : mask_eth0;

			//fix bug 14780 start
			if ((!enableHA)&& haSettings.getHaPort() == HASettings.HAPORT_LAN) {
				primaryLANIP = ip_eth1;
			} else {
				primaryLANIP = haSettings.getPrimaryLANIP();
			}
			if ((!enableHA)&& haSettings.getHaPort() == HASettings.HAPORT_LAN) {
				primaryLANMask = mask_eth1;
			} else {
				primaryLANMask = haSettings.getPrimaryLANNetmask();
			}
			hdnPrimaryLANIP = enableHA ? haSettings.getPrimaryLANIP() : ip_eth1;
			hdnPrimaryLANMask = enableHA ? haSettings.getPrimaryLANNetmask() : mask_eth1;
//			primaryLANIP = enableHA ? haSettings.getPrimaryLANIP() : ip_eth1;
//			primaryLANMask = enableHA ? haSettings.getPrimaryLANNetmask() : mask_eth1;
			//fix bug 14780 end

			primaryGateway = enableHA ? haSettings.getPrimaryDefaultGateway() : defaultGateway;
			haPrimaryDNS = primaryDNS;
			haSecondDNS = secondDNS;
			haTertiaryDNS = tertiaryDNS;

			haPort = haSettings.getHaPort() == HASettings.HAPORT_MGT ? HAPORT_MGT : HAPORT_LAN;
			enableExternalIP = haSettings.isUseExternalIPHostname();
			primaryExternalIP = haSettings.getPrimaryExternalIPHostname();
			secondaryExternalIP = haSettings.getSecondaryExternalIPHostname();

			// primary database and secondary database settings
//			haPrimaryDbIp = enableHA ? haSettings.getPrimaryDbUrl() : "";
//			haSecondaryDbIp = enableHA ? haSettings.getSecondaryDbUrl() : "";
//			haPrimaryDbPwd = enableHA ? haSettings.getPrimaryDbPwd() : "";
//			haSecondaryDbPwd = enableHA ? haSettings.getSecondaryDbPwd() : "";
			haSecret = enableHA ?  haSettings.getHaSecret() : "";

			// HA Notify Email
			haNotifyEmail = haSettings.getHaNotifyEmail();
			try {
				MgrUtil.setSessionAttribute(INTERFACESETTINGS_CACHE, netConfig);
				MgrUtil.setSessionAttribute(HASETTINGS_CACHE, haSettings);
			} catch (Exception e) {
				log.error("initNetworkValues", "catch exception, maybe session be invalidated.", e);
			}

			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
					+ "check_heartbeat_running.sh");
			if (exitValue == 0) {
				haStatus = "HA is running normally.";
			} else {
				haStatus = "HA is abnormal. " + getHAOperationExitMessage(exitValue);
			}

			// for proxy settings
			List<HMServicesSettings> list4Proxy = QueryUtil.executeQuery(HMServicesSettings.class,
					null, new FilterParams("owner.id", getDomainId()));
			if (!list4Proxy.isEmpty()) {
				HMServicesSettings bo = list4Proxy.get(0);
				enableProxy = haEnableProxy = bo.isEnableProxy();
				proxyServer = haProxyServer = bo.getProxyServer();
				proxyPort = haProxyPort = bo.getProxyPort();
				proxyUserName = haProxyUserName = bo.getProxyUserName();
				proxyPassword = haProxyPassword = bo.getProxyPassword();
			}
			
		} catch (Exception e) {
			log.error("initNetworkValues", "catch exception", e);
		}
	}

	private static final String	ROUTELIST_CACHE	= "routelist_cache";

	private void initRoutingValues() {
		try {
			Vector<Vector<String>> routeInfos = HmBeOsUtil.getRoute();
			if (routeInfos == null) {
				return;
			}
			routeList = new ArrayList<HmRoute>();
			for (Vector<String> routeInfo : routeInfos) {
				HmRoute route = new HmRoute(getNextId(), routeInfo.get(INDEX_DESTINATION),
						routeInfo.get(INDEX_NETMASK), routeInfo.get(INDEX_GATEWAY));
				routeList.add(route);
			}

			MgrUtil.setSessionAttribute(ROUTELIST_CACHE, routeList);
		} catch (Exception e) {
			log.error("initRoutingValues", "catch exception", e);
		}
	}

	private boolean	emptyLoginAccess;

	private void initLoginAccessValues() {
		List<HmAccessControl> list = QueryUtil.executeQuery(HmAccessControl.class, null,
				null, getDomainId(), this);
		if (list.isEmpty()) {
			// accessControlTypeShow = EMPTY;
			// denyBehaviorShow = EMPTY;
			//
			deniedIps = new ArrayList<String>();
			allowedIps = new ArrayList<String>();

			emptyLoginAccess = true;
		} else {
			HmAccessControl control = list.get(0);

			accessControlType = control.getControlType();
			denyBehavior = control.getDenyBehavior();
			accessControlTypeShow = MgrUtil.getEnumString("enum.hm.access.control.type."
					+ accessControlType);
			denyBehaviorShow = MgrUtil.getEnumString("enum.hm.access.control.behavior."
					+ denyBehavior);
			List<String> ipList = control.getIpAddresses();
			if (ipList != null && ipList.size() > 0) {
				for (String ipMask : ipList) {
					String[] array = ipMask.split("/");
					if (array.length == 2) {
						ipAddressList.add(new AccessControlIPAddress(array[0], array[1]));
					}
				}
			}

			deniedIps = new ArrayList<String>();
			allowedIps = new ArrayList<String>();
			if (accessControlType == HmAccessControl.CONTROL_TYPE_DENY) {
				if (null != ipList) {
					deniedIps = ipList;
				}
			} else if (accessControlType == HmAccessControl.CONTROL_TYPE_PERMIT) {
				if (null != ipList) {
					allowedIps = ipList;
				}
			}
		}
	}

	/**
	 * update System/Audit log expiration days
	 * @author Yunzhi Lin
	 * - Time: Feb 15, 2011 4:16:14 PM
	 */
	private void updateLogExpiration() {
		LogSettings logSettings = QueryUtil.findBoByAttribute(LogSettings.class, "owner", getDomain());
		boolean isUpdate=false;
		LogSettings hmBo;
		if (null==logSettings) {
			hmBo=new LogSettings();
			hmBo.setAlarmInterval(LogSettings.DEFAULT_ALARM_INTERVAL);
			hmBo.setEventInterval(LogSettings.DEFAULT_EVENT_INTERVAL);
			hmBo.setMaxPerfRecord(LogSettings.DEFAULT_MAX_PERFORM_RECORDS);
			hmBo.setMaxHistoryClientRecord(LogSettings.DEFAULT_MAX_HISTORY_CLIENT_RECORDS);
			hmBo.setInterfaceStatsInterval(LogSettings.DEFAULT_INTERFACE_STATS_INTERVAL);
			hmBo.setStatsStartMinute(LogSettings.DEFAULT_STATS_START_MINUTE);
			hmBo.setSyslogExpirationDays(LogSettings.DEFAULT_SYSLOG_EXPIRATIONDAYS);
			hmBo.setAuditlogExpirationDays(LogSettings.DEFAULT_AUDITLOG_EXPIRATIONDAYS);
			hmBo.setL3FirewallLogExpirationDays(LogSettings.DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS);
		} else {
			hmBo = logSettings;
			hmBo.setSyslogExpirationDays(syslogExpirationDays);
			hmBo.setAuditlogExpirationDays(auditlogExpirationDays);
			hmBo.setL3FirewallLogExpirationDays(l3FirewallLogExpirationDays);
			isUpdate=true;
		}
		try {
			if (isUpdate) {
				QueryUtil.updateBo(hmBo);
			} else {
				QueryUtil.createBo(hmBo);
			}
			addActionMessage(HmBeResUtil
					.getString("mgmtSettings.logExpirationDays.update.success"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS,
					MgrUtil.getUserMessage("hm.audit.log.update.log.expiration.day.settings"));

		} catch (Exception e) {
			log.error("updateLogExpiration", "update 'System/Audit Log Expiration Days' error!", e);
			addActionError(HmBeResUtil
					.getString("mgmtSettings.logExpirationDays.update.error"));
			generateAuditLog(HmAuditLog.STATUS_FAILURE,
					MgrUtil.getUserMessage("hm.audit.log.update.log.expiration.day.settings"));
		}
	}
	
	public Range getAuditLogExpirationDaysRange(){
		return getAttributeRange("auditlogExpirationDays",LogSettings.class);
	}
	
	public Range getSysLogExpirationDaysRange(){
		return getAttributeRange("syslogExpirationDays",LogSettings.class);
	}
	
	public Range getL3FirewallLogExpirationDaysRange(){
		return getAttributeRange("l3FirewallLogExpirationDays",LogSettings.class);
	}

	/**
	 * update admin session expiration
	 *
	 * @return -
	 */
	private boolean updateSessionExpiration() {
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			bo.setOwner(getDomain());
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		if (isFiniteSession()) {
			bo.setSessionExpiration(sessionExpiration);
			bo.setInfiniteSession(false);
		} else {
			bo.setInfiniteSession(true);
		}

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			if (isFiniteSession()) {
				if (userContext != null) {
					userContext.setSessionExpiration(sessionExpiration * 60);
				}
				request.getSession().setMaxInactiveInterval(sessionExpiration * 60);
			} else {
				if (userContext != null) {
					userContext.setSessionExpiration(-1);
				}
				request.getSession().setMaxInactiveInterval(-1);
			}

			return true;
		} catch (Exception e) {
			log.error("updateSessionExpiration",
					"Update session expiration settings catch exception!", e);
			return false;
		}
	}

	private boolean updateMaxUpdateNum(){
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			bo.setOwner(getDomain());
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		bo.setMaxUpdateNum(maxUpdateNum);

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("updateMaxUpdateNum",
					"Update max number of HiveOS softver update settings catch exception!", e);
			return false;
		}
	}

	/*Config generation threads*/
	private boolean updateConcurrentConfigGenNum(){
		// update db
		HMServicesSettings bo = null;
		List<?> list = QueryUtil.executeQuery("select concurrentConfigGenNum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner", getDomain()), 1);

		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			bo.setOwner(getDomain());
			bo.setConcurrentConfigGenNum(concurrentConfigGenNum);
		}

		try {
			AhAppContainer.getBeConfigModule().getConfigMgmt().adjustConfigGeneratorNumber(concurrentConfigGenNum);

			if (bo == null) {
				Byte originalConfigGenNum = (Byte) list.get(0);

				if (concurrentConfigGenNum != originalConfigGenNum) {
					QueryUtil.updateBos(HMServicesSettings.class, "concurrentConfigGenNum = :s1", null,
							new Object[] { concurrentConfigGenNum }, domainId);
				}
			} else {
				createBo(bo);
			}
			return true;
		} catch (Exception e) {
			log.error("updateConcurrentConfigGenNum",
					"Update config generation number of HiveOS softver update settings catch exception!", e);
			return false;
		}
	}
	//config the search user count
	private boolean updateConcurrentSearchUserNum(){
		// update db
		HMServicesSettings bo = null;
		List<?> list = QueryUtil.executeQuery("select concurrentSearchUserNum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner", getDomain()), 1);

		if(list.isEmpty()){
			bo = new HMServicesSettings();
			bo.setOwner(getDomain());
			bo.setConcurrentSearchUserNum(concurrentSearchUserNum);
		}

		try {
			if (null == bo) {
				Byte originalSearchNum = (Byte) list.get(0);

				if (concurrentSearchUserNum != originalSearchNum) {
					QueryUtil.updateBos(HMServicesSettings.class, "concurrentSearchUserNum = :s1", null,
							new Object[] { concurrentSearchUserNum }, domainId);
				}
			} else {
				createBo(bo);
			}

			return true;
		} catch (Exception e) {
			log.error("concurrentSearchUserNum",
					"Update Maximum Concurrent HiveManager Search User Count settings catch exception!", e);
			return false;
		}
	}

	private boolean updateExpressModeSettings(){
		// update db
		List<HmExpressModeEnable> settings = QueryUtil.executeQuery(
				HmExpressModeEnable.class, null, null);
		boolean isUpdate=false;
		HmExpressModeEnable hmBo;
		if (settings.isEmpty()) {
			hmBo=new HmExpressModeEnable();
			hmBo.setExpressModeEnable(enableExpressMode);
		} else {
			hmBo=settings.get(0);
			hmBo.setExpressModeEnable(enableExpressMode);
			isUpdate=true;
		}
		try {
			if (isUpdate) {
				QueryUtil.updateBo(hmBo);
			} else {
				QueryUtil.createBo(hmBo);
			}
		} catch (Exception e) {
			log.error("updateExpressModeSettings",
					"Update express mode settings catch exception!", e);
			return false;
		}
		return true;
	}
	
	private boolean saveImprovementSettings() {
		try {
			HMServicesSettings servicesSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", getDomainId());
			servicesSetting.setEnableCollectAppData(enableCollectAppData);
			QueryUtil.updateBo(servicesSetting);
			if (getDomain().isHomeDomain()) {
				return updateImprovementSettings();
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error("saveImprovementSettings",
					"Save product improvement settings catch exception!", e);
			return false;
		}
		
	}


	/**
	 * update improvement settings
	 *
	 * @return -
	 */
	private boolean updateImprovementSettings() {
		// update db
		try {
			LicenseServerSetting lserverInfo = HmBeActivationUtil.getLicenseServerInfo();
			// lserverInfo.setLserverUrl(baseURL);
			lserverInfo.setSendStatistic(participateImprovement);
//			lserverInfo.setSendDataCollection(dataOfImprovement);

			QueryUtil.updateBo(lserverInfo);

			HmBePerformUtil.updateDataCollect(participateImprovement);

			return true;
		} catch (Exception e) {
			log.error("updateImprovementSettings",
					"Update product improvement settings catch exception!", e);
			return false;
		}
	}

	private String updateDateTimeConfig() {
		// call be function
		String arg_Zone = HmBeOsUtil.getEnumsTimeZone()[timezone].getValue();
		String arg_Date = "";
		String[] arg_NTPServer = null;
		int arg_NTP = ntpServiceStart ? BeOsLayerModule.START_NTP_SERVICE
				: BeOsLayerModule.STOP_NTP_SERVICE;

		if(syncMode.equals(SYNCMODE_NTP)){
			arg_NTPServer = new String[] { ntpServer, String.valueOf(ntpInterval) };
		} else {
			arg_Date = getArg_Date();
		}

		String errorMessage;
		try {
			errorMessage = HmBeOsUtil.setServerTime(arg_Zone, arg_Date, arg_NTPServer, arg_NTP);
		} catch (Exception e) {
			log.error("updateDateTimeConfig", "update failed!", e);
			errorMessage = e.getMessage();
		}

		// initValue();

		return errorMessage;
	}

	// date format : MMDDHHmmYYYY.ss
	private String getArg_Date() {
		// 'dateTime' format: 1997-1-1
		String[] dateArray = dateTime.split("-");
		String tmp_hour = (hour == null) ? "0" : ENUM_HOURS[Integer.valueOf(hour)].getValue()
				.substring(0, 2);
		String tmp_min = (minute == null) ? "0" : ENUM_MINUTES[Integer.valueOf(minute)].getValue()
				.substring(0, 2);
		String tmp_second = (second == null) ? "0" : ENUM_SECONDES[Integer.valueOf(second)]
				.getValue().substring(0, 2);

		return format_prefixZero(dateArray[1], 2) + format_prefixZero(dateArray[2], 2)
				+ format_prefixZero(tmp_hour, 2) + format_prefixZero(tmp_min, 2) + dateArray[0]
				+ "." + format_prefixZero(tmp_second, 2);
	}

	private static String format_prefixZero(String s, int len) {
		if (s == null || len <= 0)
			return null;
		for (int i = s.length(); i < len; i++)
			s = "0" + s;
		return s;
	}

	private boolean updateDomainTimeZone() {
		try {
			HmDomain domain = findBoById(HmDomain.class, getDomainId());
			if (domain == null) {
				// addActionError(HmBeResUtil.getString("datetime.update.error") + " Query db data
				// error.");
				log.error("updateDomainTimeZone", "Query domain from db error, domain id = "
						+ getDomainId());
				return false;
			}

			String timeZoneStr = HmBeOsUtil.getTimeZoneString(timezone);
			String oldTimeZone = domain.getTimeZoneString();

			domain.setTimeZone(timeZoneStr);
			BoMgmt.getDomainMgmt().updateDomain(domain);
			
			// update relation report info
			Utils.updateDomainTimezone(getDomainId(), HmBeOsUtil.getTimeZoneWholeStr(timeZoneStr), HmBeOsUtil.getTimeZoneWholeStr(oldTimeZone));

			getUserContext().getDomain().setTimeZone(timeZoneStr);
			if (getUserContext().getSwitchDomain() != null) {
				getUserContext().getSwitchDomain().setTimeZone(timeZoneStr);
			}

			// addActionMessage(HmBeResUtil.getString("datetime.timezone.success"));

			return true;
		} catch (Exception e) {
			// addActionError(HmBeResUtil.getString("datetime.update.error") + " "+e.getMessage());
			log.error("updateDomainTimeZone", "Update time zone of domain failed, domain id = "
					+ getDomainId());
			return false;
		}
	}

	private final String	HOST_NETMASK	= "255.255.255.255";

	private String checkRouteValid(String dest, String mask, String gateway) {
		// 255.255.255.255 netmask express as a host address
		if (mask.equals(HOST_NETMASK) && Integer.parseInt(dest.split("\\.")[3]) == 0) {
			return "Destination ip address is invalid.!Ip address should be a host address when netmask is 255.255.255.255.";
		}

		// check ip validate with netmask
		if ((!mask.equals(HOST_NETMASK)) && (!isValidSubnetAddress(dest, mask))) {
			return "Destination ip address is not a valid net address.";
		}

		// check gateway, should in the same subnet with mgt/lan
		NetConfigureDTO dto = HmBeOsUtil.getNetConfig();
		String mgt_ip = dto.getIpAddress_eth0();
		String mgt_netmask = dto.getNetmask_eth0();
		String lan_ip = dto.getIpAddress_eth1();
		String lan_netmask = dto.getNetmask_eth1();

		if ((mgt_ip.length() == 0 || mgt_netmask.length() == 0)
				|| ((lan_ip.length() == 0 || lan_netmask.length() == 0) && !isInSameSubnet(gateway,
						mgt_ip, mgt_netmask))
				|| (!isInSameSubnet(gateway, mgt_ip, mgt_netmask) && !isInSameSubnet(gateway,
						lan_ip, lan_netmask))) {
			return "Gateway address of route should be in the same subnet with MGT or LAN.";
		}

		// check netmask, checked by hm.util.validateMask()

		return "";
	}

	/**
	 * check whether or not ip1 and ip2 are in same subnet
	 *
	 * @param ip1
	 *            -
	 * @param ip2
	 *            -
	 * @param netmask
	 *            -
	 * @return -
	 */
	private boolean isInSameSubnet(String ip1, String ip2, String netmask) {
		if (null == ip1 || null == ip2 || null == netmask)
			return false;
		String[] ipArray1 = ip1.split("\\.");
		String[] maskArray = netmask.split("\\.");
		String[] subnet1 = new String[4];
		String[] ipArray2 = ip2.split("\\.");
		String[] subnet2 = new String[4];

		try {
			for (int i = 0; i < ipArray1.length; i++) {
				subnet1[i] = String.valueOf(Integer.valueOf(ipArray1[i])
						& Integer.valueOf(maskArray[i]));
			}

			for (int i = 0; i < ipArray2.length; i++) {
				subnet2[i] = String.valueOf(Integer.valueOf(ipArray2[i])
						& Integer.valueOf(maskArray[i]));
			}

			for (int i = 0; i < subnet1.length; i++) {
				if (!subnet1[i].equals(subnet2[i])) {
					return false;
				}
			}
		} catch (Exception ex) {
			log.error("isInSameSubnet()", ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * check ip validate with netmask 1. when mask = 255.255.255.255, ip should be a host address 2.
	 * when mask is others, ip should be a net address
	 *
	 * @param
	 * @return
	 */
	/**
	 * check ip validate with netmask 1. when mask = 255.255.255.255, ip should be a host address 2.
	 * when mask is others, ip should be a net address
	 *
	 * @param ip
	 *            -
	 * @param netmask
	 *            -
	 * @return -
	 */
	private boolean isValidSubnetAddress(String ip, String netmask) {
		String[] ipArray = ip.split("\\.");
		String[] maskArray = netmask.split("\\.");
		String[] subnetAddr = new String[4];

		for (int i = 0; i < ipArray.length; i++) {
			subnetAddr[i] = String.valueOf(Integer.valueOf(ipArray[i])
					& Integer.valueOf(maskArray[i]));
		}

		for (int i = 0; i < subnetAddr.length; i++) {
			if (!subnetAddr[i].equals(ipArray[i])) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Synchronized load, remove, store of Route objects.
	 */
	@SuppressWarnings("unchecked")
	private int removeRoutes(Collection<Long> ids) {
		routeList = (List<HmRoute>) MgrUtil.getSessionAttribute(ROUTELIST_CACHE);

		// call be
		List<String[]> delRouteInfos = new ArrayList<String[]>(ids.size());
		for (Long id : ids) {
			for (HmRoute route : routeList) {
				if (route.getId().equals(id)) {
					String[] routeInfo = new String[3];
					routeInfo[INDEX_DESTINATION] = route.getDest();
					routeInfo[INDEX_NETMASK] = route.getMask();
					routeInfo[INDEX_GATEWAY] = route.getGateway();

					delRouteInfos.add(routeInfo);
				}
			}
		}

		String resultMsg = HmBeOsUtil.removeRoute(delRouteInfos);
		if (resultMsg == null) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.remove.selected.route"));
			return ids.size();
		} else {
			addActionError(resultMsg);
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.remove.selected.route"));
			return 0;
		}
	}

	/**
	 * get id for hmRoute
	 *
	 * @return -
	 */
	private long getNextId() {
		if (++nextId == 2147483647) {
			nextId = 1;
		}

		return nextId;
	}

	private void updateAccessControl() {
		try {
			if (accessControlType == HmAccessControl.CONTROL_TYPE_PERMIT) {
				if (null == allowedIps || allowedIps.isEmpty()) {
					addActionError(getText("error.pleaseAddItems"));
					return;
				}
			}

			HmAccessControl accessBo;
			List<HmAccessControl> list = QueryUtil.executeQuery(HmAccessControl.class, null, null);
			if (list.isEmpty()) {
				accessBo = new HmAccessControl();
				accessBo.setOwner(getDomain());
			} else {
				accessBo = list.get(0);
			}

			accessBo.setControlType(accessControlType);
			accessBo.setDenyBehavior(denyBehavior);
			accessBo
					.setIpAddresses(accessControlType == HmAccessControl.CONTROL_TYPE_DENY ? deniedIps
							: allowedIps);

			if (accessBo.getId() == null || accessBo.getId() == 0) {
				// create a new bo
				QueryUtil.createBo(accessBo);
			} else {
				QueryUtil.updateBo(accessBo);
			}

			addActionMessage(MgrUtil.getUserMessage("message.access.control.setting.updated.success"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.access.control.settings"));
		} catch (Exception e) {
			log.error("updateAccessControl", "catch exception", e);
			addActionError(MgrUtil.getUserMessage("action.error.unable.update.access.control") + e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.access.control.settings"));
		}
	}

	public boolean updateNetworkSettings() {
		HASettings originalHA = (HASettings) MgrUtil.getSessionAttribute(HASETTINGS_CACHE);
		boolean originalEnableHA = (originalHA != null) && originalHA.isEnableHA();
		boolean isChanged = false;

		// not support edit some fields on ha enabled
		if (originalHA.getHaStatus() == HASettings.HASTATUS_ENABLE) {
			enableFallback = originalHA.isEnableFailBack();
			haPort = originalHA.getHaPort() == HASettings.HAPORT_MGT ? HAPORT_MGT : HAPORT_LAN;
			enableExternalIP = originalHA.isUseExternalIPHostname();
		}
		

		if (enableHA) {
			// enable ha
			if (NmsUtil.isHostedHMApplication()) {
				if (!updateDNSConfiguration4HA())
					return false;

				// update HA Email Notify
				updateHANotifyEmail();

				return true;
			}

			// check domain
			if (!checkDomain(haDomainName)) {
				return false;
			}

			// check gateway should be in the same subnet with port ip
			if (!(isInSameSubnet(primaryMGTIP, primaryGateway, primaryMGTMask) || isInSameSubnet(
					primaryLANIP, primaryGateway, primaryLANMask))) {
				addActionError(MgrUtil.getUserMessage("action.error.primary.node.setting"));
				return false;
			}

//			if (!(isInSameSubnet(secondaryMGTIP, secondaryGateway, secondaryMGTMask) || isInSameSubnet(
//					secondaryLANIP, secondaryGateway, secondaryLANMask))) {
//				addActionError(MgrUtil.getUserMessage("action.error.secondary.node.setting"));
//				return false;
//			}

			// check activation key
//			if (!HmBeActivationUtil.ifActivationKeyValidForHa()) {
//				addActionError(HmBeResUtil.getString("ha.join.checkActivationKey"));
//				return false;
//			}

			String passive = secondaryMGTIP;
			if (enableExternalIP) {
				passive = secondaryExternalIP;
			} else {
				passive = haPort.equals(HAPORT_LAN) ? secondaryLANIP : secondaryMGTIP;
			}
			// check secondary node ip connect
			String command = "/HiveManager/script/shell/SSHpassword " + passive;
			if (null != haSecret && !haSecret.isEmpty())
				command += " '" + haSecret + '\'';  //TODO  escape '
			int exitValue = execCommand(command);
			if (exitValue != 0) {
				addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.ssh.password.cannot.connect", new String[]{"secondary node", (null != haSecret && !"".equals(haSecret)) ? "invalid" : "required",
					passive}));
				return false;
			}

			// check primary and secondary database settings
			final String scp;
			if (null != haPrimaryDbIp && !haPrimaryDbIp.isEmpty()) {
				command = "/HiveManager/script/shell/SSHpassword " + haPrimaryDbIp;
				if (null != haPrimaryDbPwd && !haPrimaryDbPwd.isEmpty())
					command += " '" + haPrimaryDbPwd + '\'';  //TODO  escape '
				exitValue = execCommand(command);
				if (exitValue != 0 || execCommand("ssh " + passive + (scp = " \"" + command + '"')) != 0) {
					addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.ssh.password.cannot.connect", new String[]{"primary database", (null != haPrimaryDbPwd && !"".equals(haPrimaryDbPwd)) ? "invalid" : "required",
						haPrimaryDbIp}));
					return false;
				}
			}
			else
				scp = null;

			// check secondary db connect
			if (null != haSecondaryDbIp && !haSecondaryDbIp.isEmpty()) {
				command = "/HiveManager/script/shell/SSHpassword " + haSecondaryDbIp;
				if (null != haSecondaryDbPwd && !haSecondaryDbPwd.isEmpty())
					command += " '" + haSecondaryDbPwd + '\'';  //TODO  escape '
				exitValue = execCommand(command);
				if (exitValue != 0 || execCommand("ssh " + passive + (command = " \"" + command + '"')) != 0
						|| null != scp && (execCommand("ssh " + haPrimaryDbIp + command) != 0 || execCommand("ssh " + haSecondaryDbIp + scp) != 0)) {
					addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.ssh.password.cannot.connect", new String[]{"secondary database", (null != haSecondaryDbPwd  && !"".equals(haSecondaryDbPwd)) ? "invalid" : "required",
						haSecondaryDbIp}));
					return false;
				}
			}

			// check secondary host name
			String secondaryName = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + passive + " uname -n");
			if (null != secondaryName && !secondaryName.isEmpty()) {
				// secondary domain name
//				String secDomainName = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + passive + " dnsdomainname");
//				if (null == secDomainName || secDomainName.isEmpty() || !secondaryName.endsWith(secDomainName)) {
//					addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.node.domainname", passive));
//					return false;
//				}
				if (!secondaryName.endsWith(haDomainName)) {
					addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.node.domainname.different", new String[]{passive, haDomainName}));
					return false;
				}
				String secHostName = secondaryName.substring(0, secondaryName.lastIndexOf(haDomainName)-1);
				if (null == secHostName || secHostName.isEmpty()) {
					addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.node.hostname", passive));
					return false;
				}
				secondaryHostName = secHostName;
			} else {
				addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.node.hostname", passive));
				return false;
			}
			
			// check 4 nodes, database status
			if (null != haPrimaryDbIp && !haPrimaryDbIp.isEmpty() && null != haSecondaryDbIp && !haSecondaryDbIp.isEmpty()) {
				boolean is4Nodes = false;
				if (!haPrimaryDbIp.equals(primaryMGTIP) && !haPrimaryDbIp.equals(secondaryMGTIP) && !haSecondaryDbIp.equals(primaryMGTIP) && !haSecondaryDbIp.equals(secondaryMGTIP)) {
					is4Nodes = true;
					if (null != primaryLANIP && !primaryLANIP.isEmpty()) {
						is4Nodes = false;
						if (!haPrimaryDbIp.equals(primaryLANIP) && !haSecondaryDbIp.equals(primaryLANIP)) {
							is4Nodes = true;
						}
					}
					if (null != secondaryLANIP && !secondaryLANIP.isEmpty()) {
						is4Nodes = false;
						if (!haPrimaryDbIp.equals(secondaryLANIP) && !haSecondaryDbIp.equals(secondaryLANIP)) {
							is4Nodes = true;
						}
					}
				}
				if (is4Nodes) {
					String filePath = "/HiveManager/tomcat/.dbOnly";
					String resultPriDb = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + haPrimaryDbIp + " file " + filePath);
					// not db only
					if (resultPriDb.contains("ERROR")) {
						addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.primary.not.db.only", haPrimaryDbIp));
						return false;
					}
					resultPriDb = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + haSecondaryDbIp + " file " + filePath);
					// not db only
					if (resultPriDb.contains("ERROR")) {
						addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.not.db.only", haSecondaryDbIp));
						return false;
					}
				}
			}
			
			// check 4 nodes, database status
			if (null != haPrimaryDbIp && !haPrimaryDbIp.isEmpty() && null != haSecondaryDbIp && !haSecondaryDbIp.isEmpty()) {
				boolean is4Nodes = false;
				if (!haPrimaryDbIp.equals(primaryMGTIP) && !haPrimaryDbIp.equals(secondaryMGTIP) && !haSecondaryDbIp.equals(primaryMGTIP) && !haSecondaryDbIp.equals(secondaryMGTIP)) {
					is4Nodes = true;
					if (null != primaryLANIP && !primaryLANIP.isEmpty()) {
						is4Nodes = false;
						if (!haPrimaryDbIp.equals(primaryLANIP) && !haSecondaryDbIp.equals(primaryLANIP)) {
							is4Nodes = true;
						}
					}
					if (null != secondaryLANIP && !secondaryLANIP.isEmpty()) {
						is4Nodes = false;
						if (!haPrimaryDbIp.equals(secondaryLANIP) && !haSecondaryDbIp.equals(secondaryLANIP)) {
							is4Nodes = true;
						}
					}
				}
				if (is4Nodes) {
					String filePath = "/HiveManager/tomcat/.dbOnly";
					String resultPriDb = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + haPrimaryDbIp + " file " + filePath);
					// not db only
					if (resultPriDb.contains("ERROR")) {
						addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.primary.not.db.only", haPrimaryDbIp));
						return false;
					}
					resultPriDb = BeAdminCentOSTools.getOutStreamExecCmd("ssh " + haSecondaryDbIp + " file " + filePath);
					// not db only
					if (resultPriDb.contains("ERROR")) {
						addActionError(MgrUtil.getUserMessage("error.admin.hm.ha.secondary.not.db.only", haSecondaryDbIp));
						return false;
					}
				}
			}
			
			// update net configuration if exists changes.
			if (isChangedHALocalNetConfiguration()) {
				isChanged = true;
				boolean isSuccess = updateNetConfiguration4HA();
				if (!isSuccess) {
					return false;
				}
			}

			// update ha settings and execute enable ha if exists changes.
			if (isChangedHASettings()) {
				isChanged = true;
				if (!enableHA())
					return false;
			}
			File file = new File(CONFIG_FILE_PATH);
			if(file.exists() && file.isFile()){
				file.delete();
			}
			// no changes, give some message
			if (!isChanged) {
				// addActionMessage("There are no changes on net configuration or HA
				// configuration.");
			}

			updateProxyConfiguration(true);
		} else {
			// disable ha

			// check domain
			if (!checkDomain(networkDomain)) {
				return false;
			}

			// check gateway, gateway should in same subnet with mgr or lan
			if (!(isInSameSubnet(defaultGateway, ip_eth0, mask_eth0) || (enableLan && isInSameSubnet(
					defaultGateway, ip_eth1, mask_eth1)))) {
				addActionError(MgrUtil.getUserMessage("action.error.gateway.address.setting"));
				return false;
			}

			// disable ha need update ha settings and execute script
			if (originalEnableHA) {
				isChanged = true;
				boolean isSuccess = disableHA();
				if (!isSuccess) {
					return false;
				}
			}

			// update net configuration
			// check whether or not modified.
			if (isChangedStandAloneNetConfiguration()) {
				isChanged = true;
				updateStandAloneNetConfiguration();
			}

			updateProxyConfiguration(false);
			//for db separation
			if(!NmsUtil.isHostedHMApplication()){
				FileOutputStream fos = null;
				File file = new File(CONFIG_FILE_PATH);
				if(!enableSeparateExternalDb){
					try {
						List<HASettings> hasettingsList = QueryUtil.executeQuery(HASettings.class, null, null);
						if(hasettingsList != null && hasettingsList.size()>0){
							HASettings haSetting = hasettingsList.get(0);
							List<String> hostAndPort = HibernateDbConfigTool.getHostAndPort();
							String hibernateDbIp = "";
							if(null != hostAndPort && hostAndPort.size()>0){
								hibernateDbIp = hostAndPort.get(0);
							}
							String localDbIp = super.getLocalHost();
							if(haSetting.getEnableExternalDb() != HASettings.EXTERNALDB_DISABLEHA_INITIAL){
								if(!hibernateDbIp.equalsIgnoreCase("localhost") && !hibernateDbIp.equalsIgnoreCase(localDbIp) && haSetting.getEnableExternalDb() == HASettings.EXTERNALDB_ENABLEHA_REMOTE){
									haSetting.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_REMOTE);
									haSetting.setPrimaryDbUrl(hibernateDbIp);
									if(hibernateDbIp.equals(haSetting.getPrimaryDbUrl())){
										haSetting.setPrimaryDbPwd(haSetting.getPrimaryDbPwd());
									}else if(hibernateDbIp.equals(haSetting.getSecondaryDbUrl())){
										haSetting.setPrimaryDbPwd(haSetting.getSecondaryDbPwd());
									}else{
										haSetting.setPrimaryDbPwd("");
									}
									QueryUtil.updateBo(haSetting);
									Properties prop = new Properties();
									prop.setProperty(EXTERN_DB_IP_KEY, hibernateDbIp);
									prop.setProperty(EXTERN_DB_PORT_KEY, "5432");
									prop.setProperty(LOCAL_USER_KEY, "admin");
									if(hibernateDbIp.equals(haSetting.getPrimaryDbUrl())){
										prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
									}else if(hibernateDbIp.equals(haSetting.getSecondaryDbUrl())){
										prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getSecondaryDbPwd());
									}else{
										prop.setProperty(LOCAL_PASSWORD_KEY, "");
									}
									
									fos = new FileOutputStream(file);
									prop.store(fos, null);
								}else if(!hibernateDbIp.equalsIgnoreCase("localhost") && !hibernateDbIp.equalsIgnoreCase(localDbIp) && haSetting.getEnableExternalDb() == HASettings.EXTERNALDB_DISABLEHA_REMOTE){
									StringBuilder cmds = new StringBuilder(BeAdminCentOSTools.ahShellRoot + "/Connect2HMDB.sh ");
									cmds.append("local");
									int msg = execCommand(cmds.toString());
									if(0 == msg){
										if(file.exists() && file.isFile()){
										    file.delete();
										}
										super.generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("admin.separatedb.local.success.log"));
										addActionMessage(MgrUtil.getUserMessage("message.admin.separatedb.local.success"));
										Thread thread = new Thread(){
											public void run(){
												try {
													sleep(5000);
													HmBeAdminUtil.restartSoft();
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										};
										thread.start();
									}else if(107 == msg){
										super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.local.failure.log"));
										addActionError(MgrUtil.getUserMessage("error.admin.separatedb.107"));
									}else if(109 == msg){
										super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.local.failure.log"));
										addActionError(MgrUtil.getUserMessage("error.admin.separatedb.109"));
									}else{
										super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.local.failure.log"));
										addActionError(MgrUtil.getUserMessage("message.admin.separatedb.local.failure"));
									}
								}
							}else{
								if(!hibernateDbIp.equalsIgnoreCase("localhost") && !hibernateDbIp.equalsIgnoreCase(localDbIp)){
									haSetting.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_REMOTE);
									haSetting.setPrimaryDbUrl(hibernateDbIp);
									if(null == haSetting.getHaSecret() || haSetting.getHaSecret().equals("") || "something".equals(haSetting.getHaSecret())){
										haSetting.setPrimaryDbPwd("");
									}else{
										haSetting.setPrimaryDbPwd(haSetting.getHaSecret());
									}
									
									QueryUtil.updateBo(haSetting);
									Properties prop = new Properties();
									prop.setProperty(EXTERN_DB_IP_KEY, hibernateDbIp);
									prop.setProperty(EXTERN_DB_PORT_KEY, "5432");
									prop.setProperty(LOCAL_USER_KEY, "admin");
									if(null == haSetting.getHaSecret() || haSetting.getHaSecret().equals("") || "something".equals(haSetting.getHaSecret())){
										prop.setProperty(LOCAL_PASSWORD_KEY, "");
									}else{
										prop.setProperty(LOCAL_PASSWORD_KEY,haSetting.getHaSecret());
									}
									
									fos = new FileOutputStream(file);
									prop.store(fos, null);
								}
							}
						}
					} catch (Exception e) {
						super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.local.failure.log"));
						addActionError(MgrUtil.getUserMessage("message.admin.separatedb.local.failure"));
					}finally{
						if(fos != null){
							try{
									fos.close();
							}catch(IOException e){
								log.error("FileOutputStream close failure");
							}
						}
					}
				}else{
					try {
							List<HASettings> hasettingsList = QueryUtil.executeQuery(HASettings.class, null, null);
							if(hasettingsList != null && hasettingsList.size()>0){
								HASettings haSetting = hasettingsList.get(0);
								if(haSetting.getEnableExternalDb() == HASettings.EXTERNALDB_DISABLEHA_REMOTE){
									if(!haSetting.getPrimaryDbUrl().equalsIgnoreCase(remoteDbIp) || !haSetting.getPrimaryDbPwd().equals(remoteDbSshPwd)){
										Properties prop = new Properties();
										prop.setProperty(EXTERN_DB_IP_KEY, remoteDbIp);
										prop.setProperty(EXTERN_DB_PORT_KEY, "5432");
										prop.setProperty(LOCAL_USER_KEY, "admin");
										prop.setProperty(LOCAL_PASSWORD_KEY, remoteDbSshPwd);
										fos = new FileOutputStream(file);
										prop.store(fos, null);
										if(file.exists() && file.isFile()){
											int returnMsg = execCommand(BeAdminCentOSTools.ahShellRoot + "/Connect2HMDB.sh");
											if(0 == returnMsg){
												haSetting.setPrimaryDbUrl(remoteDbIp);
												haSetting.setPrimaryDbPwd(remoteDbSshPwd);
												QueryUtil.updateBo(haSetting);
												super.generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("admin.separatedb.success.log"));
												addActionMessage(MgrUtil.getUserMessage("message.admin.separatedb.success"));
												Thread thread = new Thread(){
													public void run(){
														try {
															sleep(5000);
															HmBeAdminUtil.restartSoft();
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												};
												thread.start();
											}else if(100 == returnMsg){///'/HiveManager/tomcat/.dbOnly' file has not exist in remote host.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.100"));
											}else if(101 == returnMsg){///extdbsettings.properties file has not exist.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.101"));
											}else if(102 == returnMsg){///public key authentication establish failed.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.102"));
											}else if(103 == returnMsg){///local ip address getting failed.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.103"));
											}else if(104 == returnMsg){///pg_hba.conf file in remote host has not exist.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.104"));
											}else if(105 == returnMsg){///remote database start failure.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.105"));
											}else if(106 == returnMsg){///remote database start failure.
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.106"));
											}else if(108 == returnMsg){
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.108"));
											}else if(110 == returnMsg){
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb.110"));
											}else{
												prop.setProperty(EXTERN_DB_IP_KEY, haSetting.getPrimaryDbUrl());
												prop.setProperty(LOCAL_PASSWORD_KEY, haSetting.getPrimaryDbPwd());
												fos = new FileOutputStream(file);
												prop.store(fos, null);
												super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
												addActionError(MgrUtil.getUserMessage("error.admin.separatedb"));
											}
									    }else{
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											super.addActionError(MgrUtil.getUserMessage("error.admin.separatedb"));
										}
									}
								}else if(haSetting.getEnableExternalDb() == HASettings.EXTERNALDB_DISABLEHA_INITIAL){
									Properties prop = new Properties();
									prop.setProperty(EXTERN_DB_IP_KEY, remoteDbIp);
									prop.setProperty(EXTERN_DB_PORT_KEY, "5432");
									prop.setProperty(LOCAL_USER_KEY, "admin");
									prop.setProperty(LOCAL_PASSWORD_KEY, remoteDbSshPwd);
									fos = new FileOutputStream(file);
									prop.store(fos, null);
									if(file.exists() && file.isFile()){
										int returnMsg = execCommand(BeAdminCentOSTools.ahShellRoot + "/Connect2HMDB.sh");
										if(0 == returnMsg){
											haSetting.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_REMOTE);
											haSetting.setPrimaryDbUrl(remoteDbIp);
											haSetting.setPrimaryDbPwd(remoteDbSshPwd);
											QueryUtil.updateBo(haSetting);
											super.generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("admin.separatedb.success.log"));
											addActionMessage(MgrUtil.getUserMessage("message.admin.separatedb.success"));
											Thread thread = new Thread(){
												public void run(){
													try {
														sleep(5000);
														HmBeAdminUtil.restartSoft();
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											};
											thread.start();
										}else if(100 == returnMsg){///'/HiveManager/tomcat/.dbOnly' file has not exist in remote host.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.100"));
										}else if(101 == returnMsg){///extdbsettings.properties file has not exist.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.101"));
										}else if(102 == returnMsg){///public key authentication establish failed.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.102"));
										}else if(103 == returnMsg){///local ip address getting failed.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.103"));
										}else if(104 == returnMsg){///pg_hba.conf file in remote host has not exist.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.104"));
										}else if(105 == returnMsg){///remote database start failure.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.105"));
										}else if(106 == returnMsg){///remote database start failure.
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.106"));
										}else if(108 == returnMsg){
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.108"));
										}else if(110 == returnMsg){
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb.110"));
										}else{
											file.delete();
											super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
											addActionError(MgrUtil.getUserMessage("error.admin.separatedb"));
										}
								    }else{
										super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
										super.addActionError(MgrUtil.getUserMessage("error.admin.separatedb"));
									}
								}
							}
						}catch(Exception e){
							super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("admin.separatedb.failure.log"));
							super.addActionError(MgrUtil.getUserMessage("error.admin.separatedb"));
						}finally{
							if(fos != null){
								try{
										fos.close();
								}catch(IOException e){
									log.error("FileOutputStream close failure");
								}
							}
						}
				}
			}
			// no changes, give some message
			if (!isChanged) {
				// addActionMessage("There are no changes on net configuration.");
				return false;
			}

//			try {
//				// for activation key
//				HmBeActivationUtil.deleteUselessActivationKey();
//
//				// for license
//				HmBeLicenseUtil.deleteUselessLicense();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

		//fix bug 17196
		HmBeOsUtil.setLocalHostName(hostName);

		return true;
	}

	private void updateHANotifyEmail() {
		List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
		if (list.isEmpty()) {
			log.warn("updateHANotifyEmail", "Unable to update HA Notify Email, cause HA Settings is Empty.");
			return;
		}

		HASettings haSettings = list.get(0);
		haSettings.setHaNotifyEmail(haNotifyEmail);

		try {
			QueryUtil.updateBo(haSettings);
		} catch (Exception e) {
			log.error("updateHANotifyEmail", "update HA notify email setting", e);
			addActionError(e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.ha.notify.email.setting"));
		}
	}

	/**
	 * Valid characters are A through Z, 0 through 9, and hyphen (-) There is a limit of 63
	 * characters (not including .COM, .WS, etc.) Domains can't start with a hyphen (-) Domains
	 * can't have a hyphen (-) immediately before the .COM, .WS, etc. .WS domains must be at least 4
	 * characters
	 *
	 * @param name
	 *            -
	 * @return -
	 */
	private boolean checkDomain(String name) {
		if (name.indexOf("-") == 0) {
			addActionError(MgrUtil.getUserMessage("action.error.define.domain.name.beginchar"));
			return false;
		}

		if ((name.lastIndexOf(".") - name.lastIndexOf("-")) == 1) {
			addActionError(MgrUtil.getUserMessage("action.error.define.domain.name.format"));
			return false;
		}

		if ((name.contains(".")) && (name.length() < 4)) {
			addActionError(MgrUtil.getUserMessage("action.error.define.domain.name.length"));
			return false;
		}

		return true;
	}

	/**
	 * check whether or not local net configuration has been modified when ha enable.
	 *
	 * @return true, settings has been modified.<br>
	 *         false, is same with original.
	 */
	private boolean isChangedHALocalNetConfiguration() {
		NetConfigureDTO originalConfig = null;

		try {
			originalConfig = (NetConfigureDTO) MgrUtil.getSessionAttribute(INTERFACESETTINGS_CACHE);
		} catch (Exception e) {
			log.error("isChangedHALocalNetConfiguration",
					"catch exception, maybe session be invalidated.", e);
		}

		if (originalConfig == null) {
			return true;
		}

		if (!originalConfig.getPrimaryDns().equals(haPrimaryDNS)
				|| !originalConfig.getSecondDns().equals(haSecondDNS)
				|| !originalConfig.getTertiaryDns().equals(haTertiaryDNS)
				|| !originalConfig.getDomainName().equals(haDomainName)) {
			return true;
		}

		return false;
	}

	/**
	 * check whether or not ha settings has been modified when ha enable.
	 *
	 * @return true, settings has been modified.<br>
	 *         false, is same with original.
	 */
	private boolean isChangedHASettings() {
		HASettings haSettings = null;

		try {
			haSettings = (HASettings) MgrUtil.getSessionAttribute(HASETTINGS_CACHE);
		} catch (Exception e) {
			log.error("isChangedHASettings", "catch exception, maybe session be invalidated.", e);
		}

		if (haSettings == null) {
			return true;
		}

        if (enableHA && haSettings.getHaStatus() == HASettings.HASTATUS_ENABLE) {
            // avoid to re-do enable HA again!!
            return false;
        }
	      
		if (haSettings.getHaStatus() != (enableHA ? HASettings.HASTATUS_ENABLE
				: HASettings.HASTATUS_DIABLE)) {
			return true;
		}

		if (haSettings.isEnableFailBack() != enableFallback) {
			return true;
		}
		if(enableExternalDb){
			if(haSettings.getEnableExternalDb() != HASettings.EXTERNALDB_ENABLEHA_REMOTE){
				return true;
			}
		}else{
			if(haSettings.getEnableExternalDb() != HASettings.EXTERNALDB_DISABLEHA_INITIAL){
				return true;
			}
		}
		if ((haSettings.getPrimaryHostName() == null && primaryHostName != null)
				|| !haSettings.getPrimaryHostName().equals(primaryHostName)) {
			return true;
		}

		if ((haSettings.getSecondaryHostName() == null && secondaryHostName != null)
				|| !haSettings.getSecondaryHostName().equals(secondaryHostName)) {
			return true;
		}

		if ((haSettings.getDomainName() == null && haDomainName != null)
				|| !haSettings.getDomainName().equals(haDomainName)) {
			return true;
		}

		if ((haSettings.getPrimaryMGTIP() == null && primaryMGTIP != null)
				|| !haSettings.getPrimaryMGTIP().equals(primaryMGTIP)) {
			return true;
		}

		if ((haSettings.getPrimaryMGTNetmask() == null && primaryMGTMask != null)
				|| !haSettings.getPrimaryMGTNetmask().equals(primaryMGTMask)) {
			return true;
		}

		if ((haSettings.getPrimaryDefaultGateway() == null && primaryGateway != null)
				|| !haSettings.getPrimaryDefaultGateway().equals(primaryGateway)) {
			return true;
		}

		if ((haSettings.getSecondaryMGTIP() == null && secondaryMGTIP != null)
				|| !haSettings.getSecondaryMGTIP().equals(secondaryMGTIP)) {
			return true;
		}

//		if ((haSettings.getSecondaryMGTNetmask() == null && secondaryMGTMask != null)
//				|| !haSettings.getSecondaryMGTNetmask().equals(secondaryMGTMask)) {
//			return true;
//		}

		if ((haSettings.getPrimaryLANIP() == null && primaryLANIP != null)
				|| !haSettings.getPrimaryLANIP().equals(primaryLANIP)) {
			return true;
		}

		if ((haSettings.getPrimaryLANNetmask() == null && primaryLANMask != null)
				|| !haSettings.getPrimaryLANNetmask().equals(primaryLANMask)) {
			return true;
		}

		if ((haSettings.getSecondaryLANIP() == null && secondaryLANIP != null)
				|| !haSettings.getSecondaryLANIP().equals(secondaryLANIP)) {
			return true;
		}

//		if ((haSettings.getSecondaryLANNetmask() == null && secondaryLANMask != null)
//				|| !haSettings.getSecondaryLANNetmask().equals(secondaryLANMask)) {
//			return true;
//		}
//
//		if ((haSettings.getSecondaryDefaultGateway() == null && secondaryGateway != null)
//				|| !haSettings.getSecondaryDefaultGateway().equals(secondaryGateway)) {
//			return true;
//		}

		if (haSettings.getHaPort() != (haPort.equals(HAPORT_MGT) ? HASettings.HAPORT_MGT
				: HASettings.HAPORT_LAN)) {
			return true;
		}

		if (haSettings.isUseExternalIPHostname() != enableExternalIP) {
			return true;
		}

		if ((haSettings.getPrimaryExternalIPHostname() == null && primaryExternalIP != null)
				|| !haSettings.getPrimaryExternalIPHostname().equals(primaryExternalIP)) {
			return true;
		}

		if ((haSettings.getSecondaryExternalIPHostname() == null && secondaryExternalIP != null)
				|| !haSettings.getSecondaryExternalIPHostname().equals(secondaryExternalIP)) {
			return true;
		}

		if ((haSettings.getPrimaryDbUrl() == null && haPrimaryDbIp != null)
				|| !haSettings.getPrimaryDbUrl().equals(haPrimaryDbIp)) {
			return true;
		}

		/*if ((haSettings.getPrimaryDbPwd() == null && haPrimaryDbPwd != null)
				|| !haSettings.getPrimaryDbPwd().equals(haPrimaryDbPwd)) {
			return true;
		} */

		if ((haSettings.getSecondaryDbUrl() == null && haSecondaryDbIp != null)
				|| !haSettings.getSecondaryDbUrl().equals(haSecondaryDbIp)) {
			return true;
		}

		/*if ((haSettings.getSecondaryDbPwd() == null && haSecondaryDbPwd != null)
				|| !haSettings.getSecondaryDbPwd().equals(haSecondaryDbPwd)) {
			return true;
		} */

		return false;
	}

	private boolean updateDNSConfiguration4HA() {
		try {
			HmBeOsUtil.updateDNSConfiguration(haPrimaryDNS, haSecondDNS, haTertiaryDNS);
			addActionMessage(MgrUtil.getUserMessage("message.interface.setting.updated.success"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			return true;
		} catch (Exception e) {
			// error message include in exception object
			addActionError(e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			return false;
		}
	}

	private boolean updateNetConfiguration4HA() {
		try {
			NetConfigureDTO netConfig = HmBeOsUtil.getNetConfig();
			netConfig.setDomainName(haDomainName);
			netConfig.setPrimaryDns(haPrimaryDNS);
			netConfig.setSecondDns(haSecondDNS);
			netConfig.setTertiaryDns(haTertiaryDNS);
			HmBeOsUtil.updateNetConfig(netConfig);
			addActionMessage(MgrUtil.getUserMessage("message.interface.setting.updated.success"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			return true;
		} catch (Exception e) {
			// error message include in exception object
			addActionError(e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			return false;
		}
	}

	/**
	 * check whether or not net configuration has been modified.
	 *
	 * @return true, net configuration has been modified.<br>
	 *         false, is same with original.
	 */
	private boolean isChangedStandAloneNetConfiguration() {
		NetConfigureDTO originalConfig = null;

		try {
			originalConfig = (NetConfigureDTO) MgrUtil.getSessionAttribute(INTERFACESETTINGS_CACHE);
		} catch (Exception e) {
			log.error("isChangedStandAloneNetConfiguration",
					"catch exception, maybe session be invalidated.", e);
		}

		if (originalConfig == null) {
			return true;
		}

		if (!originalConfig.getHostName().equals(hostName)
				|| !originalConfig.getDomainName().equals(networkDomain)
				|| !originalConfig.getIpAddress_eth0().equals(ip_eth0)
				|| !originalConfig.getNetmask_eth0().equals(mask_eth0)
				|| originalConfig.isEnabled_eth1() != enableLan
				|| !originalConfig.getGateway().equals(defaultGateway)
				|| !originalConfig.getPrimaryDns().equals(primaryDNS)
				|| !originalConfig.getSecondDns().equals(secondDNS)
				|| !originalConfig.getTertiaryDns().equals(tertiaryDNS)) {
			return true;
		}

		if (enableLan
				&& (!originalConfig.getIpAddress_eth1().equals(ip_eth1) || !originalConfig
						.getNetmask_eth1().equals(mask_eth1))) {
			return true;
		}

		return false;
	}

	private void updateProxyConfiguration(boolean isHA) {
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.id", getDomainId()));
		if (list.isEmpty()) {
			return;
		}

		HMServicesSettings bo = list.get(0);
		bo.setEnableProxy(isHA ? haEnableProxy : enableProxy);
		bo.setProxyServer(isHA ? haProxyServer : proxyServer);
		bo.setProxyPort(isHA ? haProxyPort : proxyPort);
		bo.setProxyUserName(isHA ? haProxyUserName : proxyUserName);
		bo.setProxyPassword(isHA ? haProxyPassword : proxyPassword);

		try {
			QueryUtil.updateBo(bo);
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.update.proxy.setting"));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.proxy.setting"));
			log.error("updateProxyConfiguration", e);
		}
	}

	/**
	 * update net configuration which is not ha enable
	 */
	private void updateStandAloneNetConfiguration() {
		String old_ip_eth0 = HmBeOsUtil.getIP_eth0();

		NetConfigureDTO netConfig = new NetConfigureDTO();
		netConfig.setHostName(hostName);
		netConfig.setDomainName(networkDomain);
		netConfig.setIpAddress_eth0(ip_eth0);
		netConfig.setIpAddress_eth1(ip_eth1);
		netConfig.setNetmask_eth0(mask_eth0);
		netConfig.setNetmask_eth1(mask_eth1);
		netConfig.setGateway(defaultGateway);
		netConfig.setPrimaryDns(primaryDNS);
		netConfig.setSecondDns(secondDNS);
		netConfig.setTertiaryDns(tertiaryDNS);
		netConfig.setEnabled_eth1(enableLan);

		try {
			HmBeOsUtil.updateNetConfig(netConfig);

			addActionMessage(MgrUtil.getUserMessage("message.interface.setting.updated.success"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));

			// update cas settings
			if (!old_ip_eth0.equals(ip_eth0)) {
				boolean needTip = false;

				if (NmsUtil.isHostedHMApplication()) {
					CasTool.setCASClientIP(ip_eth0);

					needTip = true;
				} else {
					CasTool.setCASServerIP(ip_eth0);
					CasTool.setCASClientIP(ip_eth0);

					if (NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED) {
						needTip = true;
					}
				}

				if (needTip) {
					addActionMessage(MgrUtil.getUserMessage("message.change.setting.restart",NmsUtil.getOEMCustomer().getNmsName()));
				}
			}
		} catch (IllegalArgumentException e) {
			// handle exception
			addActionError(MgrUtil.getUserMessage("action.error.update.interface.setting"));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			log.error("updateStandAloneNetConfiguration", e.getMessage());
		} catch (IOException e) {
			// no permission, must have root authority
			addActionError(MgrUtil.getUserMessage("action.error.no.permission.update.interface.setting"));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
			log.error("updateStandAloneNetConfiguration", e.getMessage());
		} catch (BeNoPermissionException e) {
			// no permission run on windows platform
			addActionError(e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.net.interface.setting"));
		}
	}

	/**
	 * enable ha
	 *
	 * @return -
	 */
	private boolean enableHA() {
		try {
			// update ha settings
			boolean isCreate = false;
			HASettings haSettings = (HASettings) MgrUtil.getSessionAttribute(HASETTINGS_CACHE);
			if (haSettings == null) {
				isCreate = true;
				haSettings = new HASettings();
			}

			// set ha status as initial at first
			haSettings.setHaStatus(HASettings.HASTATUS_INITIAL);
			haSettings.setPrimaryHostName(primaryHostName.toLowerCase()/*HA(HeartBeat)*/);
			haSettings.setSecondaryHostName(secondaryHostName.toLowerCase()/*HA(HeartBeat)*/);
			haSettings.setDomainName(haDomainName.toLowerCase());
			haSettings.setEnableFailBack(enableFallback);
			haSettings.setPrimaryMGTIP(primaryMGTIP);
			haSettings.setPrimaryMGTNetmask(primaryMGTMask);
			haSettings.setPrimaryDefaultGateway(primaryGateway);
			haSettings.setSecondaryMGTIP(secondaryMGTIP);
			haSettings.setSecondaryMGTNetmask(primaryMGTMask);
			haSettings.setPrimaryLANIP(primaryLANIP);
			haSettings.setPrimaryLANNetmask(primaryLANMask);
			haSettings.setSecondaryLANIP(secondaryLANIP);
			haSettings.setSecondaryLANNetmask(primaryLANMask);
			haSettings.setSecondaryDefaultGateway(primaryGateway);
			haSettings.setHaSecret(haSecret);
			haSettings.setUseExternalIPHostname(enableExternalIP);
			haSettings.setPrimaryExternalIPHostname(primaryExternalIP);
			haSettings.setSecondaryExternalIPHostname(secondaryExternalIP);
			haSettings.setHaPort(haPort.equals(HAPORT_MGT) ? HASettings.HAPORT_MGT
					: HASettings.HAPORT_LAN);
			haSettings.setPrimaryDbUrl(haPrimaryDbIp);
			haSettings.setPrimaryDbPwd(haPrimaryDbPwd);
			haSettings.setSecondaryDbUrl(haSecondaryDbIp);
			haSettings.setSecondaryDbPwd(haSecondaryDbPwd);
			haSettings.setEnableExternalDb(enableExternalDb?HASettings.EXTERNALDB_ENABLEHA_REMOTE:HASettings.EXTERNALDB_DISABLEHA_INITIAL);
			haSettings.setPrimaryUpTime(System.currentTimeMillis());
			haSettings.setSecondaryUpTime(System.currentTimeMillis());
			haSettings.setLastSwitchOverTime(0l);

			if (isCreate) {
				QueryUtil.createBo(haSettings);
			} else {
				QueryUtil.updateBo(haSettings);
			}

			// execute script
			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "ha_enable.sh"
					+ " >>/HiveManager/ha/logs/ha_enable"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log 2>&1");
			log.info("enableHA", "execute ha_enable.sh, exit value is " + exitValue);
			if (exitValue != 0) {
				List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
				if(!list.isEmpty() && list.size() >0){
					HASettings has = list.get(0);
					has.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_INITIAL);
					QueryUtil.updateBo(has);
				}
				log.error("enableHA", "execute ha_enable.sh failed, exit value is " + exitValue);
				addActionError(MgrUtil.getUserMessage("action.error.enable.ha") + getHAOperationExitMessage(exitValue));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.enable.ha"));
				return false;
			}
			List<HASettings> lists = QueryUtil.executeQuery(HASettings.class, null, null);
			if(!lists.isEmpty() && lists.size() >0){
				HASettings hass = lists.get(0);
				hass.setHaStatus(HASettings.HASTATUS_ENABLE);
				QueryUtil.updateBo(hass);
			}
			
			refreshNavigationTree();

			addActionMessage(MgrUtil.getUserMessage("message.ha.can.be.used"));
			addActionMessage(MgrUtil.getUserMessage("message.config.capwap.server.managed"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.enable.ha"));

			UpdateSoftwareAction.restartApp();

			return true;
		} catch (Exception e) {
			log.error("enableHA", "catch exception", e);

			addActionError(MgrUtil.getUserMessage("action.error.enable.ha") + e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.enable.ha"));

			return false;
		}
	}

	/**
	 * disable HA
	 *
	 * @return -
	 */
	private boolean disableHA() {
		try {
			QueryUtil.updateBo(HASettings.class, "haStatus = 0", null);

			// execute script
			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "ha_disable.sh"
					+ " >>/HiveManager/ha/logs/ha_disable"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log 2>&1");
			log.info("disableHA", "execute ha_disable.sh, exit value is " + exitValue);
			if (exitValue != 0) {
				log.error("disableHA", "execute ha_disable.sh failed, exit value is " + exitValue);
				addActionError(MgrUtil.getUserMessage("action.error.disable.ha") + getHAOperationExitMessage(exitValue));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.disable.ha"));
				return false;
			}

			//sleep 35 seconds to let hibernate clear all invalid DB connections
			Thread.sleep(35000);
			
			//check db connections and remove invalid connection in hibernate connection pool 
			for (int j = 0; j < 3000; j++){
				try{
					QueryUtil.executeNativeQuery("select 1");
					break;
				}
				catch (Exception e) {
					Thread.sleep(100);
				}
			}

			QueryUtil.updateBo(HASettings.class, "haSecret = ''", null);

			refreshNavigationTree();

			addActionMessage(MgrUtil.getUserMessage("message.ha.can.be.used"));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.disable.ha"));

			return true;
		} catch (Exception e) {
			log.error("disableHA", "catch exception", e);

			addActionError(MgrUtil.getUserMessage("action.error.disable.ha") + e.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.disable.ha"));

			return false;
		}
	}

	private void haJoin() {
		// // check local lan interface
		// if (!HmBeOsUtil.getEnable_Eth1()) {
		// addActionError(HmBeResUtil.getString("ha.join.checkLanPort"));
		// return;
		// }
		//
		// // check local lan interface ip
		// if (HmBeOsUtil.getIP_eth1().equals(joinHAPrimaryLanIP)
		// || HmBeOsUtil.getIP_eth0().equals(joinHAPrimaryLanIP)) {
		// addActionError(HmBeResUtil.getString("ha.join.checkPrimaryLanIP"));
		// return;
		// }

		// check activation key
		if (!HmBeActivationUtil.ifActivationKeyValidForHa()) {
			addActionError(HmBeResUtil.getString("ha.join.checkActivationKey"));
			return;
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String cmd = HmBeOsUtil.getHAScriptsPath() + "ha_join.sh " + joinHAPrimaryLanIP + " '"
				+ joinHASecret + "' >>/HiveManager/ha/logs/ha_join" + formatter.format(new Date())
				+ ".log 2>&1";
		int exitValue = execCommand(cmd);
		log.info("haJoin", "execute " + cmd);
		if (exitValue != 0) {
			log.info("haJoin", "execute ha_join.sh failed, exit value is " + exitValue);
			addActionError(HmBeResUtil.getString("ha.join.error") + " "
					+ getHAOperationExitMessage(exitValue));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.ha.join"));
			return;
		}

		try {
			// secondary up time
			QueryUtil.updateBo(HASettings.class, "secondaryUpTime = "+System.currentTimeMillis(), null);

			// for activation key
			HmBeActivationUtil.deleteUselessActivationKey();

			// for license
			HmBeLicenseUtil.deleteUselessLicense();
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("haJoin", "execute ha_join.sh successfully");
		addActionMessage(HmBeResUtil.getString("ha.join.success"));
		generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.ha.join"));
	}

	private void haSwitchOver() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String cmd = HmBeOsUtil.getHAScriptsPath() + "ha_switch_over.sh"
				+ " >>/HiveManager/ha/logs/ha_switchover" + formatter.format(new Date())
				+ ".log 2>&1";
		int exitValue = execCommand(cmd);
		log.info("haSwitchOver", "execute " + cmd);
		if (exitValue != 0) {
			addActionError(HmBeResUtil.getString("ha.switchover.error") + " "
					+ getHAOperationExitMessage(exitValue));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.ha.switch.over"));
			return;
		}

		// record the switch over time
		List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
		if (!list.isEmpty()) {
			HASettings haSet = list.get(0);
			haSet.setLastSwitchOverTime(System.currentTimeMillis());
			haSet.setPrimaryUpTime(System.currentTimeMillis());
			haSet.setSecondaryUpTime(System.currentTimeMillis());
			try {
				QueryUtil.updateBo(haSet);
			} catch (Exception ex) {
				log.error("haSwitchOver()", "Change HA switch over time error : "+ex.getMessage());
			}
		}

		addActionMessage(HmBeResUtil.getString("ha.switchover.success"));
	}

	public String getCertDetails() {
		String detailStr = "";

		List<String> list;
		try {
			list = HmBeAdminUtil.getKeystoreInfo();
		} catch (BeOperateException e) {
			return e.getMessage();
		}

		for (String line : list) {
			detailStr = detailStr + line + "<br>";
		}

		detailStr = detailStr.replaceAll("\\[", "&#91;");
		detailStr = detailStr.replaceAll("\\]", "&#93;");

		return detailStr;
	}

	/**
	 * install certificate
	 *
	 * @throws Exception
	 *             -
	 */
	private void installCertificate() throws Exception {
		if (updateCertType.equals(UPDATECERTTYPE_SELFSIGNED)) {
			HmBeAdminUtil.installCertAuto();
		} else if (updateCertType.equals(UPDATECERTTYPE_IMPORT)) {
			HmBeAdminUtil.installCert(certificateFile, privateKeyFile, passPhrase);
		}
	}

	private void initImportFileContinueValue() throws Exception {
		initValue();

		updateCertType = UPDATECERTTYPE_IMPORT;
		hideImportCert = "";
		hideCertUpdate = "";

		showCertOption = true;
	}

    private void initCloudAuthServer() {
        if (NmsUtil.isHostedHMApplication() ||getIsInHomeDomain()) {
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null != bo) {
                    enabledBetaIDM = bo.isEnabledBetaIDM();
                }
            } catch (Exception e) {
                log.error("initCloudAuthServer", "Error when init the ID Manager Server settings.", e);
            }
        }
    }
    
    private void initDFSSetting() {
        if (NmsUtil.isHostedHMApplication() ||getIsInHomeDomain()) {
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null != bo) {
                    enableRadarDetection = bo.isEnableRadarDetection();
                }
            } catch (Exception e) {
                log.error("initEnableDFSSetting", "Error when init the DFS settings.", e);
            }
        }
    }
    
    private void initAPISetting() {
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null != bo) {
                    apiUserName = bo.getApiUserName();
                    apiPassword = bo.getApiPassword();
                    enableApiAccess=bo.isEnableApiAccess();
                    isAddOperation=StringUtils.isBlank(apiPassword);
                }
            } catch (Exception e) {
                log.error("initCloudAuthServer", "Error when init the ID Manager Server settings.", e);
            }
    }
    
    private void initSupplementalCLISetting(){
    	if (isFullMode()) {
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null != bo) {
                    enableSupplementalCLI = bo.isEnableSupplementalCLI();
                }
            } catch (Exception e) {
                log.error("initSupplementalCLISetting", "Error when init the Supplemental CLI settings.", e);
            }
        }
    }

    private boolean updateCloudAuthServer() {
        try {
            HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
                    getDomain());

            if (null == bo) {
                bo = new HMServicesSettings();
                bo.setOwner(getDomain());
                
                bo.setEnabledBetaIDM(enabledBetaIDM);

                QueryUtil.createBo(bo);
            } else {
                bo.setEnabledBetaIDM(enabledBetaIDM);
                
                QueryUtil.updateBo(bo);
            }
            return true;
        } catch (Exception e) {
            log.error("updateCloudAuthServer",
                    "Update Cloud Auth Server settings catch exception!", e);
            return false;
        }
    }
    
    private HMServicesSettings updateClientProfile() {
        try {
            HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
                    getDomain());

            if (null == bo) {
                bo = new HMServicesSettings();
                bo.setOwner(getDomain());
                
                bo.setEnableClientManagement(enabledClientProfile);

                QueryUtil.createBo(bo);
            } else {
                bo.setEnableClientManagement(enabledClientProfile);
                
                QueryUtil.updateBo(bo);
            }
            return bo;
        } catch (Exception e) {
            log.error("updateClientManagement",
                    "Update Client Management settings catch exception!", e);
            return null;
        }
    }
    
    private void initClientProfile() {
    	if (true) {
            try {
                HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
                        "owner", getDomain());
                if (null != bo) {
                    enabledClientProfile = bo.isEnableClientManagement();
                    enabledClientProfileShow = enabledClientProfile ? "Yes" : "No";
                }
            } catch (Exception e) {
                log.error("initClientManagementService", "Error when init the client management settings.", e);
            }
        }
    }
    
    public void certificateOperation() throws Exception{
//    	try{
//    		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
//                        "owner", getDomain());
//    		if(bo == null){
//    			 bo = new HMServicesSettings();
//                 bo.setOwner(getDomain());
//                 QueryUtil.createBo(bo);
//    		}
//    		CertificateGenSV.certificateGenereate(false,enabledClientProfile,getDomain().getInstanceId(),
//    				                       getDomain().getDomainName(), "OnboardCA", "AuthServer", createData(),bo);
//    		bo.setEnableClientManagement(enabledClientProfile);
//    		QueryUtil.updateBo(bo);
//    		addActionMessage(HmBeResUtil
//	                .getString("mgmtSettings.clientManagement.update.success"));
//    	}catch(Exception e){
//    		addActionError(HmBeResUtil
//	                .getString("mgmtSettings.clientManagement.update.error"));
//    		log.error("certificateOperation()","Error when generate the certificate",e);
//    	}
    	
    }
    
    public BeRootCADTO createData(){
    	// create CSR file
    	BeRootCADTO dto = new BeRootCADTO();
    	dto.setCommName(CertificateGenSV.COMMON_NAME);
    	dto.setCountryCode(CertificateGenSV.COUNTRY);
    	dto.setKeySize("1024");
    	dto.setLocalityName(CertificateGenSV.LOCALITY_NAME);
    	dto.setOrgName(CertificateGenSV.ORGANIZATION);
    	dto.setOrgUnit(CertificateGenSV.ORGANIZATION_UNIT);
    	dto.setStateName(CertificateGenSV.STATE);
    	dto.setFileName("Default-Radius-Server");
    	dto.setPassword("");
    	dto.setDomainName(getDomain().getDomainName());
        return dto;
    }
    
    private boolean updateDFSSetting() {
        try {
            HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
                    getDomain());

            if (null == bo) {
                bo = new HMServicesSettings();
                bo.setOwner(getDomain());

                bo.setEnableRadarDetection(enableRadarDetection);

                QueryUtil.createBo(bo);
            } else {
                bo.setEnableRadarDetection(enableRadarDetection);

                QueryUtil.updateBo(bo);
            }
            return true;
        } catch (Exception e) {
            log.error("updateDFSSetting",
                    "Update DFS settings catch exception!", e);
            return false;
        }
    }
    
    public boolean checkApiUserNameExists() {
  		List<?> boIds = QueryUtil.executeQuery("select id from " + HMServicesSettings.class.getSimpleName(), null,
				new FilterParams("apiUserName=:s1 and owner.id!=:s2", new Object[] {apiUserName,getDomainId()}));
  		if (!boIds.isEmpty()) {
  			addActionError(MgrUtil.getUserMessage("mgmtSettings.APIsetting.userName.existed",apiUserName));
  			return true;
  		} else {
  			return false;
  		}
  	}
    
    private boolean updateAPISettings() {
		try {
			HMServicesSettings servicesSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", getDomainId());
			if(null==servicesSetting){
				return false;
			}
			if(enableApiAccess){
				servicesSetting.setApiUserName(apiUserName);
				if (!StringUtils.isBlank(apiPassword)) {
					  servicesSetting.setApiPassword(MgrUtil.digest(apiPassword));
				 }
			}
			servicesSetting.setEnableApiAccess(enableApiAccess);
			QueryUtil.updateBo(servicesSetting);
		} catch (Exception e) {
			log.error("updateAPISettings",
					"Save API  settings catch exception!", e);
			return false;
		}
		return true;
	}
  
    private void updateAllRadioProfiles(){
    	try {
			if(!enableRadarDetection){
				List<RadioProfile> list = QueryUtil.executeQuery(RadioProfile.class,
						null, null);
				List<RadioProfile> needUpdates = new ArrayList<RadioProfile>();
				if(!list.isEmpty()){
					for(RadioProfile rProfile :list){
						if(rProfile.isEnableRadarDetect()){
							rProfile.setEnableRadarDetect(false);
							needUpdates.add(rProfile);
						}
					}
				}
				if(!needUpdates.isEmpty()){
					QueryUtil.bulkUpdateBos(needUpdates);
				}
			}
		} catch (Exception e) {
			 log.error("updateAllRadioProfiles",
	                    "Update relevant radio profiles catch exception!", e);
		}
    }
    
    private boolean updateSupplementalCLI() {
        try {
            HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
                    getDomain());

            if (null == bo) {
                bo = new HMServicesSettings();
                bo.setOwner(getDomain());

                bo.setEnableSupplementalCLI(enableSupplementalCLI);

                QueryUtil.createBo(bo);
            } else {
                bo.setEnableSupplementalCLI(enableSupplementalCLI);

                QueryUtil.updateBo(bo);
            }
            refreshNavigationTree();
            return true;
        } catch (Exception e) {
            log.error("updateSupplementalCLI",
                    "Update Supplemental CLI settings catch exception!", e);
            return false;
        }
    }

	public String getAsNtpServerShow() {
		return ntpServiceStart ? "Yes" : "No";
	}

	/**
	 * hide some setting to vhm
	 *
	 * @return -
	 */
	public String getHide4VHM() {
		if (getIsInHomeDomain()) {
			return "";
		}

		return "none";
	}
	   
    public String getHide4StandaloneVHM() {
        if(isHMOnline() || getIsInHomeDomain()) {
            return "";
        } else {
            return "none";
        }
    }

	/**
	 * hide some setting to standalone
	 *
	 * @return -
	 */
	public String getHide4Standalone() {
		if (getIsInHomeDomain() && isHMOnline()) {
			return "";
		}

		return "none";
	}

	public String getHide4HHM() {
		if (NmsUtil.isHostedHMApplication()) {
			return "none";
		}

		return "";
	}

	public String getShow4HHMHA() {
		if (NmsUtil.isHostedHMApplication() && enableHA)
			return "";

		return "none";
	}

	public String getHide4HA() {
		if (NmsUtil.isHostedHMApplication()) {
			return "none";
		}

		HASettings originalHA = (HASettings) MgrUtil.getSessionAttribute(HASETTINGS_CACHE);

		if (null != originalHA) {
			if (originalHA.isEnableHA()) {
				return "";
			}
		} else if (enableHA) {
			return "";
		}

		return "none";
	}

	public String getHide4HAJoin() {
		if (NmsUtil.isHostedHMApplication()) {
			return "none";
		}

		if (enableHA) {
			return "none";
		}

		return "";
	}

	public String getHideIfNoPermission() {
		if (writePermission) {
			return "";
		}

		return "none";
	}
	
	public String getDfsSettingStyle(){
		if(getSuperAdminPermission()){
			return "";
		}
		return "none";
	}

	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}
	
	private static EnumItem[] createEnumItems(int len, String postfix) {
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

	public static EnumItem[] getEnumHours() {
		return ENUM_HOURS;
	}

	public static EnumItem[] getEnumMinutes() {
		return ENUM_MINUTES;
	}

	public static EnumItem[] getEnumSeconds() {
		return ENUM_SECONDES;
	}

	public String getCurrentTimeShow() {
		return currentTimeShow;
	}

	public void setCurrentTimeShow(String currentTimeShow) {
		this.currentTimeShow = currentTimeShow;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getDateTimeSyncShow() {
		return syncMode.equals(SYNCMODE_NTP) ? "NTP" : "No NTP";
	}

	public String getDnsServers() {
		String dns = primaryDNS;
		if (secondDNS != null && secondDNS.trim().length() > 0) {
			dns += (", " + secondDNS);
		}
		if (tertiaryDNS != null && tertiaryDNS.trim().length() > 0) {
			dns += (", " + tertiaryDNS);
		}

		return dns;
	}

	public String getStatus_eth0() {
		return "Up";
	}

	public String getStatus_eth1() {
		return enableLan ? "Up" : "Down";
	}

	public String getEnableHAShow() {
		return enableHA ? "YES" : "NO";
	}

	private int execCommand(String cmd) {
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

	public EnumItem[] getControlType1() {
		return new EnumItem[] { new EnumItem(HmAccessControl.CONTROL_TYPE_DENY, MgrUtil
				.getEnumString("enum.hm.access.control.type." + HmAccessControl.CONTROL_TYPE_DENY)) };
	}

	public EnumItem[] getControlType2() {
		return new EnumItem[] { new EnumItem(HmAccessControl.CONTROL_TYPE_PERMIT,
				MgrUtil.getEnumString("enum.hm.access.control.type."
						+ HmAccessControl.CONTROL_TYPE_PERMIT)) };
	}

	public EnumItem[] getDenyBehaviors() {
		return HmAccessControl.BEHAVIOR_TYPE;
	}

	public String getAllowStyle() {
		return accessControlType == HmAccessControl.CONTROL_TYPE_PERMIT ? "" : "none";
	}

	public String getDenyStyle() {
		return accessControlType == HmAccessControl.CONTROL_TYPE_DENY ? "" : "none";
	}

	public String getPassiveHostName() {
		if(HAUtil.isSlave()) {
			return primaryMGTIP.equals(ip_eth0) ? primaryHostName : secondaryHostName;
		} else {
			return secondaryMGTIP.equals(ip_eth0) ? primaryHostName : secondaryHostName;
		}
	}

	public String getPassiveIPMask() {
		if (haPort.equals(HAPORT_MGT)) {
			if(HAUtil.isSlave()) {
				return primaryMGTIP.equals(ip_eth0) ? primaryMGTIP + "/" + primaryMGTMask : secondaryMGTIP + "/" + primaryMGTMask;
			} else {
				return secondaryMGTIP.equals(ip_eth0) ? primaryMGTIP + "/" + primaryMGTMask : secondaryMGTIP + "/" + primaryMGTMask;
			}
		} else {
			if(HAUtil.isSlave()) {
				return primaryMGTIP.equals(ip_eth0) ? primaryLANIP + "/" + primaryLANMask : secondaryLANIP + "/" + primaryLANMask;
			} else {
				return secondaryMGTIP.equals(ip_eth0) ? primaryLANIP + "/" + primaryLANMask : secondaryLANIP + "/" + primaryLANMask;
			}
		}
	}

	/**
	 * if ha is enable, some ha settings field can't be modified at present.
	 *
	 * @return -
	 */
	public boolean getWriteDisable4HA() {
		if (!getWriteDisabled().isEmpty()) {
			return true;
		}

		HASettings haSettings = (HASettings) MgrUtil.getSessionAttribute(HASETTINGS_CACHE);
		if (haSettings == null) {
			return false;
		}

		if (haSettings.getHaStatus() == HASettings.HASTATUS_ENABLE) {
			return true;
		}

		return false;
	}

	public String getMaxUploadApSettingsStyle(){
		if (getIsInHomeDomain() && isHMOnline()) {
			return "";
		}

		return "none";
	}
	
	/**
	 * tca alarm only can be seen in local version
	 * @return
	 */
	public String getHide4TCAAlarm(){
		if(isHMOnline()){
			return "none";
		}
		if (getIsInHomeDomain()) {
			return "";
		}
		return "none";
	}	
	
	public String getHide4CustomDeviceTag(){		
		if (writePermission) {
			return "";
		}
		return "none";
	}

	public boolean isDisabledManually() {
		return disabledManually;
	}

	public void setDisabledManually(boolean disabledManually) {
		this.disabledManually = disabledManually;
	}

	public boolean isDisabledSyncNTP() {
		return disabledSyncNTP;
	}

	public void setDisabledSyncNTP(boolean disabledSyncNTP) {
		this.disabledSyncNTP = disabledSyncNTP;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public int getNtpInterval() {
		return ntpInterval;
	}

	public void setNtpInterval(int ntpInterval) {
		this.ntpInterval = ntpInterval;
	}

	public String getNtpServer() {
		return ntpServer;
	}

	public void setNtpServer(String ntpServer) {
		this.ntpServer = ntpServer;
	}

	public boolean isNtpServiceStart() {
		return ntpServiceStart;
	}

	public void setNtpServiceStart(boolean ntpServiceStart) {
		this.ntpServiceStart = ntpServiceStart;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getSyncMode() {
		return syncMode;
	}

	public void setSyncMode(String syncMode) {
		this.syncMode = syncMode;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getNetworkDomain() {
		return networkDomain;
	}

	public void setNetworkDomain(String networkDomain) {
		this.networkDomain = networkDomain;
	}

	public String getPrimaryDNS() {
		return primaryDNS;
	}

	public void setPrimaryDNS(String primaryDNS) {
		this.primaryDNS = primaryDNS;
	}

	public String getSecondDNS() {
		return secondDNS;
	}

	public void setSecondDNS(String secondDNS) {
		this.secondDNS = secondDNS;
	}

	public String getTertiaryDNS() {
		return tertiaryDNS;
	}

	public void setTertiaryDNS(String tertiaryDNS) {
		this.tertiaryDNS = tertiaryDNS;
	}

	public String getIp_eth0() {
		return ip_eth0;
	}

	public void setIp_eth0(String ip_eth0) {
		this.ip_eth0 = ip_eth0;
	}

	public String getIp_eth1() {
		return ip_eth1;
	}

	public void setIp_eth1(String ip_eth1) {
		this.ip_eth1 = ip_eth1;
	}

	public String getMask_eth0() {
		return mask_eth0;
	}

	public void setMask_eth0(String mask_eth0) {
		this.mask_eth0 = mask_eth0;
	}

	public String getMask_eth1() {
		return mask_eth1;
	}

	public void setMask_eth1(String mask_eth1) {
		this.mask_eth1 = mask_eth1;
	}

	public boolean isEnableLan() {
		return enableLan;
	}

	public void setEnableLan(boolean enableLan) {
		this.enableLan = enableLan;
	}

	public String getRate_eth0() {
		return rate_eth0;
	}

	public void setRate_eth0(String rate_eth0) {
		this.rate_eth0 = rate_eth0;
	}

	public String getRate_eth1() {
		return rate_eth1;
	}

	public void setRate_eth1(String rate_eth1) {
		this.rate_eth1 = rate_eth1;
	}

	public boolean isEnableHA() {
		return enableHA;
	}

	public void setEnableHA(boolean enableHA) {
		this.enableHA = enableHA;
	}

	public boolean isHdnEnableHA() {
		return hdnEnableHA;
	}

	public void setHdnEnableHA(boolean hdnEnableHA) {
		this.hdnEnableHA = hdnEnableHA;
	}

	public List<HmRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<HmRoute> routeList) {
		this.routeList = routeList;
	}

	public String getRouteDest() {
		return routeDest;
	}

	public void setRouteDest(String routeDest) {
		this.routeDest = routeDest;
	}

	public String getRouteGateway() {
		return routeGateway;
	}

	public void setRouteGateway(String routeGateway) {
		this.routeGateway = routeGateway;
	}

	public String getRouteMask() {
		return routeMask;
	}

	public void setRouteMask(String routeMask) {
		this.routeMask = routeMask;
	}

	public String getAccessControlTypeShow() {
		return accessControlTypeShow;
	}

	public void setAccessControlTypeShow(String accessControlTypeShow) {
		this.accessControlTypeShow = accessControlTypeShow;
	}

	public String getDenyBehaviorShow() {
		return denyBehaviorShow;
	}

	public void setDenyBehaviorShow(String denyBehaviorShow) {
		this.denyBehaviorShow = denyBehaviorShow;
	}

	public List<AccessControlIPAddress> getIpAddressList() {
		return ipAddressList;
	}

	public void setIpAddressList(List<AccessControlIPAddress> ipAddressList) {
		this.ipAddressList = ipAddressList;
	}

	public short getAccessControlType() {
		return accessControlType;
	}

	public void setAccessControlType(short accessControlType) {
		this.accessControlType = accessControlType;
	}

	public List<String> getAllowedIps() {
		return allowedIps;
	}

	public void setAllowedIps(List<String> allowedIps) {
		this.allowedIps = allowedIps;
	}

	public List<String> getDeniedIps() {
		return deniedIps;
	}

	public void setDeniedIps(List<String> deniedIps) {
		this.deniedIps = deniedIps;
	}

	public short getDenyBehavior() {
		return denyBehavior;
	}

	public void setDenyBehavior(short denyBehavior) {
		this.denyBehavior = denyBehavior;
	}

	public String getHaStatus() {
		return haStatus;
	}

	public void setHaStatus(String haStatus) {
		this.haStatus = haStatus;
	}

	public boolean isEnableFallback() {
		return enableFallback;
	}

	public void setEnableFallback(boolean enableFallback) {
		this.enableFallback = enableFallback;
	}

	public String getHaDomainName() {
		return haDomainName;
	}

	public void setHaDomainName(String haDomainName) {
		this.haDomainName = haDomainName;
	}

	public String getHaPrimaryDNS() {
		return haPrimaryDNS;
	}

	public void setHaPrimaryDNS(String haPrimaryDNS) {
		this.haPrimaryDNS = haPrimaryDNS;
	}

	public String getHaSecondDNS() {
		return haSecondDNS;
	}

	public void setHaSecondDNS(String haSecondDNS) {
		this.haSecondDNS = haSecondDNS;
	}

	public String getHaSecret() {
		return haSecret;
	}

	public void setHaSecret(String haSecret) {
		this.haSecret = haSecret;
	}

	public String getHaTertiaryDNS() {
		return haTertiaryDNS;
	}

	public void setHaTertiaryDNS(String haTertiaryDNS) {
		this.haTertiaryDNS = haTertiaryDNS;
	}

	public String getPrimaryGateway() {
		return primaryGateway;
	}

	public void setPrimaryGateway(String primaryGateway) {
		this.primaryGateway = primaryGateway;
	}

	public String getPrimaryHostName() {
		return primaryHostName;
	}

	public void setPrimaryHostName(String primaryHostName) {
		this.primaryHostName = primaryHostName;
	}

	public String getPrimaryLANIP() {
		return primaryLANIP;
	}

	public void setPrimaryLANIP(String primaryLANIP) {
		this.primaryLANIP = primaryLANIP;
	}

	public String getPrimaryLANMask() {
		return primaryLANMask;
	}

	public void setPrimaryLANMask(String primaryLANMask) {
		this.primaryLANMask = primaryLANMask;
	}

	public String getHdnPrimaryLANIP() {
		return hdnPrimaryLANIP;
	}

	public void setHdnPrimaryLANIP(String hdnPrimaryLANIP) {
		this.hdnPrimaryLANIP = hdnPrimaryLANIP;
	}

	public String getHdnPrimaryLANMask() {
		return hdnPrimaryLANMask;
	}

	public void setHdnPrimaryLANMask(String hdnPrimaryLANMask) {
		this.hdnPrimaryLANMask = hdnPrimaryLANMask;
	}

	public String getPrimaryMGTIP() {
		return primaryMGTIP;
	}

	public void setPrimaryMGTIP(String primaryMGTIP) {
		this.primaryMGTIP = primaryMGTIP;
	}

	public String getPrimaryMGTMask() {
		return primaryMGTMask;
	}

	public void setPrimaryMGTMask(String primaryMGTMask) {
		this.primaryMGTMask = primaryMGTMask;
	}

	public String getSecondaryHostName() {
		return secondaryHostName;
	}

	public void setSecondaryHostName(String secondaryHostName) {
		this.secondaryHostName = secondaryHostName;
	}

	public String getSecondaryLANIP() {
		return secondaryLANIP;
	}

	public void setSecondaryLANIP(String secondaryLANIP) {
		this.secondaryLANIP = secondaryLANIP;
	}

	public String getSecondaryMGTIP() {
		return secondaryMGTIP;
	}

	public void setSecondaryMGTIP(String secondaryMGTIP) {
		this.secondaryMGTIP = secondaryMGTIP;
	}

	public static String getHAOperationExitMessage(int exitValue) {
		String systemName = NmsUtil.getOEMCustomer().getNmsName();

		switch (exitValue) {
		case AH_HA_SUCCESS:
			return "HA operation is been executed successfully.";

		case AH_HA_ERR_GENERIC:
			return "An internal error has occurred.";

		case AH_HA_ERR_DB:
			return "A database error has occurred.";

		case AH_HA_ERR_HM_COMMUNICATION:
			return "A " + systemName + " communications error has occurred.";

		case AH_HA_NOT_RUNNING:
			return "An HA process error has occurred.";

		case AH_HA_ERR_STATUS_INITIALIZING:
			return "The local HA node is initializing.";

		case AH_HA_ERR_STATUS_STANDALONE:
			return "The local " + systemName + " is in standalone mode.";

		case AH_HA_ERR_STATUS_HA:
			return "The local " + systemName + " is in HA mode.";

		case AH_HA_ERR_STATUS_NODE_NUM:
			return "An internal error has occurred.";

		case AH_HA_ERR_JOIN_GENERIC:
			return "An error occurred while attempting to form an HA pair.";

		case AH_HA_ERR_JOIN_DENIED:
			return "Unable to form an HA pair. Check that the " + systemName + " software versions and the HA secret are the same on both devices.";

		case AH_HA_ERR_JOIN_TIME:
			return "Unable to form an HA pair because the time difference between the two devices is too great.";

		case AH_HA_ERR_JOIN_LICENSE:
			return "Unable to form an HA pair. Check that both " + systemName + " have the same license type and management capabilities.";

		case AH_HA_ERR_JOIN_NET:
			return "Unable to form an HA pair. Check if there is an HA secret mismatch or network error.";

		case AH_HA_ERR_JOIN_SETUP:
			return "An internal error has occurred.";

		case AH_HA_ERR_HAD_MASTER_NOT_RUNNING:
			return "An HA process error has occurred.";

		case AH_HA_ERR_HAD_SLAVE_NOT_RUNNING:
			return "An HA process error has occurred.";

		case AH_HA_ERR_VPN_NOT_RUNNING:
			return "An HA process error has occurred.";

		case AH_HA_ERR_SLONY_MASTER_SETUP:
			return "An internal error has occurred.";

		case AH_HA_ERR_SLONY_SLAVE_SETUP:
			return "An internal error has occurred.";

		case AH_HA_ERR_SLONY_NOT_SYNC:
			return "The database has not been fully synchronized between the active and passive HA nodes.";

		case AH_HA_ERR_SLONY_SWITCH:
			return "An internal error has occurred.";

		case AH_HA_ERR_HB_NOT_RUNNING:
			return "HA heartbeats are not being sent.";

		case AH_HA_ERR_HB_PROG:
			return "An internal error has occurred.";

		case AH_HA_ERR_HB_CONFIG:
			return "An HA process error has occurred.";

		case AH_HA_ERR_HB_START:
			return "Unable to start sending HA heartbeats.";

		case AH_HA_ERR_HB_NOT_MEMBER:
			return "The local " + systemName + " is not an HA node.";

		case AH_HA_ERR_HB_NODE_OFFLINE:
			return "The HA node is offline.";

		case AH_HA_ERR_HB_MASTER_OFFLINE:
			return "The active HA node is offline.";

		case AH_HA_ERR_HB_SLAVE_OFFLINE:
			return "The passive HA node is offline.";

		case AH_HA_ERR_HB_NOT_MASTER:
			return "The local HA node is not the active one.";

		default:
			return "An unknown error has occurred.";
		}
	}

	public boolean isShowRouteOption() {
		return showRouteOption;
	}

	public boolean isShowDateTimeOption() {
		return showDateTimeOption;
	}

	public boolean isShowLoginAccessOption() {
		return showLoginAccessOption;
	}

	public boolean isShowNetworkOption() {
		return showNetworkOption;
	}

	public boolean isEmptyLoginAccess() {
		return emptyLoginAccess;
	}

	public void setShowDateTimeOption(boolean showDateTimeOption) {
		this.showDateTimeOption = showDateTimeOption;
	}

	public void setShowLoginAccessOption(boolean showLoginAccessOption) {
		this.showLoginAccessOption = showLoginAccessOption;
	}

	public void setShowNetworkOption(boolean showNetworkOption) {
		this.showNetworkOption = showNetworkOption;
	}

	public void setShowRouteOption(boolean showRouteOption) {
		this.showRouteOption = showRouteOption;
	}

	public String getJoinHAPrimaryLanIP() {
		return joinHAPrimaryLanIP;
	}

	public void setJoinHAPrimaryLanIP(String joinHAPrimaryLanIP) {
		this.joinHAPrimaryLanIP = joinHAPrimaryLanIP;
	}

	public String getJoinHASecret() {
		return joinHASecret;
	}

	public void setJoinHASecret(String joinHASecret) {
		this.joinHASecret = joinHASecret;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public String getProxyConfiguration() {
		return enableProxy ? "Server: " + proxyServer + "|  Port: " + proxyPort : "Disabled";
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUserName() {
		return proxyUserName;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public boolean isHaEnableProxy() {
		return haEnableProxy;
	}

	public void setHaEnableProxy(boolean haEnableProxy) {
		this.haEnableProxy = haEnableProxy;
	}

	public String getHaProxyServer() {
		return haProxyServer;
	}

	public void setHaProxyServer(String haProxyServer) {
		this.haProxyServer = haProxyServer;
	}

	public int getHaProxyPort() {
		return haProxyPort;
	}

	public void setHaProxyPort(int haProxyPort) {
		this.haProxyPort = haProxyPort;
	}

	public String getHaProxyUserName() {
		return haProxyUserName;
	}

	public void setHaProxyUserName(String haProxyUserName) {
		this.haProxyUserName = haProxyUserName;
	}

	public String getHaProxyPassword() {
		return haProxyPassword;
	}

	public void setHaProxyPassword(String haProxyPassword) {
		this.haProxyPassword = haProxyPassword;
	}

	public String getHaPort() {
		return haPort;
	}

	public void setHaPort(String haPort) {
		this.haPort = haPort;
	}

	public int getHostNameLength() {
		return 32;
	}

	public int getDomainLength() {
		return 64;
	}

	public int getIpAddressLength() {
		return 18;
	}

	public boolean getDisabledLan() {
		return !enableLan;
	}

	public boolean getDisabledProxy() {
		return !enableProxy;
	}

	public boolean isEnableExternalIP() {
		return enableExternalIP;
	}

	public void setEnableExternalIP(boolean enableExternalIP) {
		this.enableExternalIP = enableExternalIP;
	}

	public String getPrimaryExternalIP() {
		return primaryExternalIP;
	}

	public void setPrimaryExternalIP(String primaryExternalIP) {
		this.primaryExternalIP = primaryExternalIP;
	}

	public String getSecondaryExternalIP() {
		return secondaryExternalIP;
	}

	public void setSecondaryExternalIP(String secondaryExternalIP) {
		this.secondaryExternalIP = secondaryExternalIP;
	}

	public boolean isShowCertOption() {
		return showCertOption;
	}

	public void setShowCertOption(boolean showCertOption) {
		this.showCertOption = showCertOption;
	}

	public String getHideCertUpdate() {
		return hideCertUpdate;
	}

	public void setHideCertUpdate(String hideCertUpdate) {
		this.hideCertUpdate = hideCertUpdate;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public String getCertificateFile() {
		return certificateFile;
	}

	public void setCertificateFile(String certificateFile) {
		this.certificateFile = certificateFile;
	}

	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public String getUpdateCertType() {
		return updateCertType;
	}

	public void setUpdateCertType(String updateCertType) {
		this.updateCertType = updateCertType;
	}

	public String getHideImportCert() {
		return hideImportCert;
	}

	public void setHideImportCert(String hideImportCert) {
		this.hideImportCert = hideImportCert;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getAvailableCaFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		return listFile;
	}

	public List<String> getAvailableKeyFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile)
			listFile = new ArrayList<String>();
		listFile.add("");
		return listFile;
	}

	public int getGenAlgorithm() {
		return genAlgorithm;
	}

	public void setGenAlgorithm(int genAlgorithm) {
		this.genAlgorithm = genAlgorithm;
	}

	public boolean isSshKeyGen() {
		return sshKeyGen;
	}

	public void setSshKeyGen(boolean sshKeyGen) {
		this.sshKeyGen = sshKeyGen;
	}

	public int getSshPortNumber() {
		return sshPortNumber;
	}

	public void setSshPortNumber(int sshPortNumber) {
		this.sshPortNumber = sshPortNumber;
	}

	public boolean isShowSSHOption() {
		return showSSHOption;
	}

	public void setShowSSHOption(boolean showSSHOption) {
		this.showSSHOption = showSSHOption;
	}

	public boolean isShowSessionOption() {
		return showSessionOption;
	}

	public void setShowSessionOption(boolean showSessionOption) {
		this.showSessionOption = showSessionOption;
	}

	public boolean isShowImproveOption() {
		return showImproveOption;
	}

	public void setShowImproveOption(boolean showImproveOption) {
		this.showImproveOption = showImproveOption;
	}

	public boolean isShowExpressModeOption() {
		return showExpressModeOption;
	}

	public void setShowExpressModeOption(boolean showExpressModeOption) {
		this.showExpressModeOption = showExpressModeOption;
	}

	public int getSessionExpiration() {
		return sessionExpiration;
	}

	public void setSessionExpiration(int sessionExpiration) {
		this.sessionExpiration = sessionExpiration;
	}

	public boolean isDisabledSession() {
		return disabledSession;
	}

	public void setDisabledSession(boolean disabledSession) {
		this.disabledSession = disabledSession;
	}
	
	public boolean isFiniteSession() {
		return finiteSession;
	}

	public void setFiniteSession(boolean finiteSession) {
		this.finiteSession = finiteSession;
	}

	public String getSessionExpirationShow() {
		return sessionExpirationShow;
	}

	public void setSessionExpirationShow(String sessionExpirationShow) {
		this.sessionExpirationShow = sessionExpirationShow;
	}

	public boolean isParticipateImprovement() {
		return participateImprovement;
	}

	public void setParticipateImprovement(boolean participateImprovement) {
		this.participateImprovement = participateImprovement;
	}

	public String getImproveParticipateShow() {
		return improveParticipateShow;
	}

	public void setImproveParticipateShow(String improveParticipateShow) {
		this.improveParticipateShow = improveParticipateShow;
	}

	public boolean isEnableExpressMode() {
		return enableExpressMode;
	}

	public void setEnableExpressMode(boolean enableExpressMode) {
		this.enableExpressMode = enableExpressMode;
	}

	public String getExpressModeShow() {
		return expressModeShow;
	}

	public void setExpressModeShow(String expressModeShow) {
		this.expressModeShow = expressModeShow;
	}

	public boolean isContainsExpressModeVHM() {
		return containsExpressModeVHM;
	}

	public void setContainsExpressModeVHM(boolean containsExpressModeVHM) {
		this.containsExpressModeVHM = containsExpressModeVHM;
	}

	public boolean isShowLogExpirationOption() {
		return showLogExpirationOption;
	}

	public void setShowLogExpirationOption(boolean showLogExpirationOption) {
		this.showLogExpirationOption = showLogExpirationOption;
	}

	public int getSyslogExpirationDays() {
		return syslogExpirationDays;
	}

	public void setSyslogExpirationDays(int syslogExpirationDays) {
		this.syslogExpirationDays = syslogExpirationDays;
	}

	public int getAuditlogExpirationDays() {
		return auditlogExpirationDays;
	}

	public void setAuditlogExpirationDays(int auditlogExpirationDays) {
		this.auditlogExpirationDays = auditlogExpirationDays;
	}

	public int getL3FirewallLogExpirationDays() {
		return l3FirewallLogExpirationDays;
	}

	public void setL3FirewallLogExpirationDays(int l3FirewallLogExpirationDays) {
		this.l3FirewallLogExpirationDays = l3FirewallLogExpirationDays;
	}


	public String getHaNotifyEmail() {
		return haNotifyEmail;
	}

	public void setHaNotifyEmail(String haNotifyEmail) {
		this.haNotifyEmail = haNotifyEmail;
	}

	public int getMaxUpdateNum(){
		return this.maxUpdateNum;
	}

	public void setMaxUpdateNum(int maxUpdateNum){
		this.maxUpdateNum = maxUpdateNum;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmAccessControl) {
			HmAccessControl oneClass = (HmAccessControl) bo;
			if (oneClass.getIpAddresses() != null)
				oneClass.getIpAddresses().size();
		}
		return null;
	}

	public boolean isDataOfImprovement() {
		return dataOfImprovement;
	}

	public void setDataOfImprovement(boolean dataOfImprovement) {
		this.dataOfImprovement = dataOfImprovement;
	}

	public String getDataOfImprovementShow() {
		return dataOfImprovementShow;
	}

	public void setDataOfImprovementShow(String dataOfImprovementShow) {
		this.dataOfImprovementShow = dataOfImprovementShow;
	}

	public boolean isShowMaxUploadNumOption() {
		return showMaxUploadNumOption;
	}

	public void setShowMaxUploadNumOption(boolean showMaxUploadNumOption) {
		this.showMaxUploadNumOption = showMaxUploadNumOption;
	}

	public boolean isShowAPIOption() {
		return showAPIOption;
	}

	public void setShowAPIOption(boolean showAPIOption) {
		this.showAPIOption = showAPIOption;
	}

	public boolean isShowGenerationThreadsOption() {
		return showGenerationThreadsOption;
	}

	public void setShowGenerationThreadsOption(boolean showGenerationThreadsOption) {
		this.showGenerationThreadsOption = showGenerationThreadsOption;
	}

	public byte getConcurrentConfigGenNum() {
		return concurrentConfigGenNum;
	}

	public void setConcurrentConfigGenNum(byte concurrentConfigGenNum) {
		this.concurrentConfigGenNum = concurrentConfigGenNum;
	}

	public boolean isShowSearchUserNumOption() {
		return showSearchUserNumOption;
	}

	public void setShowSearchUserNumOption(boolean showSearchUserNumOption) {
		this.showSearchUserNumOption = showSearchUserNumOption;
	}

	public byte getConcurrentSearchUserNum() {
		return concurrentSearchUserNum;
	}

	public void setConcurrentSearchUserNum(byte concurrentSearchUserNum) {
		this.concurrentSearchUserNum = concurrentSearchUserNum;
	}

	public String getLocatePosition() {
		return locatePosition;
	}

	public void setLocatePosition(String locatePosition) {
		this.locatePosition = locatePosition;
	}

    public boolean isShowCloudAuthServerOption() {
        return showCloudAuthServerOption;
    }

    public void setShowCloudAuthServerOption(boolean showCloudAuthServerOption) {
        this.showCloudAuthServerOption = showCloudAuthServerOption;
    }

	public String getHaPrimaryDbIp() {
		return haPrimaryDbIp;
	}

	public void setHaPrimaryDbIp(String haPrimaryDbIp) {
		this.haPrimaryDbIp = haPrimaryDbIp;
	}

	public String getHaPrimaryDbPwd() {
		return haPrimaryDbPwd;
	}

	public void setHaPrimaryDbPwd(String haPrimaryDbPwd) {
		this.haPrimaryDbPwd = haPrimaryDbPwd;
	}

	public String getHaSecondaryDbIp() {
		return haSecondaryDbIp;
	}

	public void setHaSecondaryDbIp(String haSecondaryDbIp) {
		this.haSecondaryDbIp = haSecondaryDbIp;
	}

	public String getHaSecondaryDbPwd() {
		return haSecondaryDbPwd;
	}

	public void setHaSecondaryDbPwd(String haSecondaryDbPwd) {
		this.haSecondaryDbPwd = haSecondaryDbPwd;
	}

    public boolean isEnabledBetaIDM() {
        return enabledBetaIDM;
    }

    public void setEnabledBetaIDM(boolean enabledBetaIDM) {
        this.enabledBetaIDM = enabledBetaIDM;
    }
    
	public String getRemoteDbIp() {
		return remoteDbIp;
	}

	public void setRemoteDbIp(String remoteDbIp) {
		this.remoteDbIp = remoteDbIp;
	}

	public String getRemoteDbSshPwd() {
		return remoteDbSshPwd;
	}

	public void setRemoteDbSshPwd(String remoteDbSshPwd) {
		this.remoteDbSshPwd = remoteDbSshPwd;
	}

	public boolean isEnableExternalDb() {
		return enableExternalDb;
	}

	public void setEnableExternalDb(boolean enableExternalDb) {
		this.enableExternalDb = enableExternalDb;
	}

	public boolean isEnableSeparateExternalDb() {
		return enableSeparateExternalDb;
	}

	public void setEnableSeparateExternalDb(boolean enableSeparateExternalDb) {
		this.enableSeparateExternalDb = enableSeparateExternalDb;
	}

	public String getExternalDbIpInfo() {
		return externalDbIpInfo;
	}

	public void setExternalDbIpInfo(String externalDbIpInfo) {
		this.externalDbIpInfo = externalDbIpInfo;
	}

	public boolean isExternalDbIpInfoDisplay() {
		return externalDbIpInfoDisplay;
	}

	public void setExternalDbIpInfoDisplay(boolean externalDbIpInfoDisplay) {
		this.externalDbIpInfoDisplay = externalDbIpInfoDisplay;
	}
	
	public short getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(short dateFormat) {
		this.dateFormat = dateFormat;
	}

	public short getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(short timeFormat) {
		this.timeFormat = timeFormat;
	}

	public short getDateSeparator() {
		return dateSeparator;
	}

	public void setDateSeparator(short dateSeparator) {
		this.dateSeparator = dateSeparator;
	}

	public short getTimeType() {
		return timeType;
	}

	public void setTimeType(short timeType) {
		this.timeType = timeType;
	}

	public boolean isDisabledDateFormat() {
		return disabledDateFormat;
	}

	public void setDisabledDateFormat(boolean disabledDateFormat) {
		this.disabledDateFormat = disabledDateFormat;
	}

	public boolean isSwitchOverDb() {
		return switchOverDb;
	}

	public void setSwitchOverDb(boolean switchOverDb) {
		this.switchOverDb = switchOverDb;
	}

	public EnumItem[] getDateFormatItem1() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.DATE_FORMAT_TYPE_1,
				HMServicesSettings.DATE_FORMAT_1) };
	}
	
	public EnumItem[] getDateFormatItem2() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.DATE_FORMAT_TYPE_2,
				HMServicesSettings.DATE_FORMAT_2) };
	}

	public EnumItem[] getDateSeparator1() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.DATE_SEPARATOR_TYPE_1,
				HMServicesSettings.DATE_SEPARATOR_1) };
	}
	
	public EnumItem[] getDateSeparator2() {
		return new EnumItem[] { new EnumItem(HMServicesSettings.DATE_SEPARATOR_TYPE_2,
				HMServicesSettings.DATE_SEPARATOR_2) };
	}
	
	public boolean isEnableRadarDetection() {
		return enableRadarDetection;
	}

	public void setEnableRadarDetection(boolean enableRadarDetection) {
		this.enableRadarDetection = enableRadarDetection;
	}

	public String getApiUserName() {
		return apiUserName;
	}

	public void setApiUserName(String apiUserName) {
		this.apiUserName = apiUserName;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}

	public boolean isAddOperation() {
		return isAddOperation;
	}

	public void setAddOperation(boolean isAddOperation) {
		this.isAddOperation = isAddOperation;
	}

	public boolean isEnableApiAccess() {
		return enableApiAccess;
	}

	public void setEnableApiAccess(boolean enableApiAccess) {
		this.enableApiAccess = enableApiAccess;
	}
	
    public String getEnableApiStyle(){
	   String style="";
	   if(!enableApiAccess){
		   style="none";
	   }
	   return style;
   }

	public boolean isEnableSupplementalCLI() {
		return enableSupplementalCLI;
	}

	public void setEnableSupplementalCLI(boolean enableSupplementalCLI) {
		this.enableSupplementalCLI = enableSupplementalCLI;
	}

	public boolean isShowSupplementalCLIOption() {
		return showSupplementalCLIOption;
	}

	public void setShowSupplementalCLIOption(boolean showSupplementalCLIOption) {
		this.showSupplementalCLIOption = showSupplementalCLIOption;
	}
}

interface HMSettingsConstant {
	// generic error code
	public static final int	AH_HA_SUCCESS						= 0;
	public static final int	AH_HA_ERR_GENERIC					= 1;
	public static final int	AH_HA_ERR_DB						= 2;

	// communication
	public static final int	AH_HA_ERR_HM_COMMUNICATION			= 3;

	public static final int	AH_HA_NOT_RUNNING					= 7;

	// ha status error code(10-19)
	public static final int	AH_HA_ERR_STATUS_INITIALIZING		= 10;
	public static final int	AH_HA_ERR_STATUS_STANDALONE			= 11;
	public static final int	AH_HA_ERR_STATUS_HA					= 12;
	public static final int	AH_HA_ERR_STATUS_NODE_NUM			= 13;

	// ha-d error_code(20-49)
	public static final int	AH_HA_ERR_JOIN_GENERIC				= 20;
	public static final int	AH_HA_ERR_JOIN_DENIED				= 21;
	public static final int	AH_HA_ERR_JOIN_TIME					= 22;
	public static final int	AH_HA_ERR_JOIN_LICENSE				= 23;
	public static final int	AH_HA_ERR_JOIN_NET					= 24;
	public static final int	AH_HA_ERR_JOIN_SETUP				= 25;
	public static final int	AH_HA_ERR_HAD_MASTER_NOT_RUNNING	= 30;
	public static final int	AH_HA_ERR_HAD_SLAVE_NOT_RUNNING		= 31;
	public static final int	AH_HA_ERR_VPN_NOT_RUNNING			= 32;
	public static final int	AH_HA_ERR_SLONY_MASTER_SETUP		= 33;
	public static final int	AH_HA_ERR_SLONY_SLAVE_SETUP			= 34;
	public static final int	AH_HA_ERR_SLONY_NOT_SYNC			= 35;
	public static final int	AH_HA_ERR_SLONY_SWITCH				= 36;

	// heartbeat error code(50-69)
	public static final int	AH_HA_ERR_HB_NOT_RUNNING			= 51;
	public static final int	AH_HA_ERR_HB_PROG					= 52;
	public static final int	AH_HA_ERR_HB_CONFIG					= 53;
	public static final int	AH_HA_ERR_HB_START					= 54;
	public static final int	AH_HA_ERR_HB_NOT_MEMBER				= 55;
	public static final int	AH_HA_ERR_HB_NODE_OFFLINE			= 56;
	public static final int	AH_HA_ERR_HB_MASTER_OFFLINE			= 57;
	public static final int	AH_HA_ERR_HB_SLAVE_OFFLINE			= 58;
	public static final int	AH_HA_ERR_HB_NOT_MASTER				= 59;
}

class AccessControlIPAddress {
	String	ipAddress;

	String	netmask;

	public AccessControlIPAddress(String ipAddress, String netmask) {
		this.ipAddress = ipAddress;
		this.netmask = netmask;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

}