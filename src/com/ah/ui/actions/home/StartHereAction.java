package com.ah.ui.actions.home;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.be.admin.util.EmailElement;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.sync.VhmUserSync;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourActiveService;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;
import com.ah.bo.wlan.SsidProfile;
import com.ah.nms.worker.report.Utils;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.ui.actions.config.VpnNetworksAction;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.SupportAccessUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.BoGenerationUtil;
import com.ah.util.bo.DeviceFreevalUtil;

public class StartHereAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(StartHereAction.class
			.getSimpleName());
	
	
	// radio option of access mode
	private String accessOption;
	
	private short defaultAccessMode;
	
	private int defaultAuthorizedTime=-1;
	
	private int authorizedTime1;
	
	private int authorizedTime2;
	
	private int leftHours;
	
	private short leftMinutes;
	
	private short leftSeconds;
	
	private  String endTime;
	
	private boolean writeDisable4Access=false;
	
	private boolean hideAccessPannel=false;
	
	// for top pannel use
	private boolean hideTopPannel = false;
	
	private boolean accessChanged=false;
	
	public String getAccessOption() {
		return accessOption;
	}


	public void setAccessOption(String accessOption) {
		this.accessOption = accessOption;
	}
	
	public TextItem[] getAccessOption0() {
		return new TextItem[] { new TextItem(
				SupportAccessUtil.ACCESS_OPTION_PREFIX+"0",
				MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+"0")) };
	}
	public TextItem[] getAccessOption1() {
		return new TextItem[] { new TextItem(
				SupportAccessUtil.ACCESS_OPTION_PREFIX+"1",
				MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+"1","")) };
	}
	public TextItem[] getAccessOption2() {
		return new TextItem[] { new TextItem(
				SupportAccessUtil.ACCESS_OPTION_PREFIX+"2",
				MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+"2","")) };
	}
	public TextItem[] getAccessOption3() {
		return new TextItem[] { new TextItem(
				SupportAccessUtil.ACCESS_OPTION_PREFIX+"3",
				MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+"3")) };
	}
	public TextItem[] getAccessOption4() {
		return new TextItem[] { new TextItem(
				SupportAccessUtil.ACCESS_OPTION_PREFIX+"4",
				MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+"4")) };
	}

	public short getDefaultAccessMode() {
		return defaultAccessMode;
	}


	public void setDefaultAccessMode(short defaultAccessMode) {
		this.defaultAccessMode = defaultAccessMode;
	}
	
	public int getDefaultAuthorizedTime() {
		return defaultAuthorizedTime;
	}


	public void setDefaultAuthorizedTime(int defaultAuthorizedTime) {
		this.defaultAuthorizedTime = defaultAuthorizedTime;
	}

	// access option 1 input field
	public int getAuthorizedTime1() {
		return authorizedTime1;
	}
	
	public void setAuthorizedTime1(int authorizedTime1) {
		this.authorizedTime1 = authorizedTime1;
	}
	
	// access option 2 input field
	public int getAuthorizedTime2() {
		return authorizedTime2;
	}
	public void setAuthorizedTime2(int authorizedTime2) {
		this.authorizedTime2 = authorizedTime2;
	}
	
	public int getLeftHours() {
		return leftHours;
	}


	public void setLeftHours(int leftHours) {
		this.leftHours = leftHours;
	}


	public short getLeftMinutes() {
		return leftMinutes;
	}


	public void setLeftMinutes(short leftMinutes) {
		this.leftMinutes = leftMinutes;
	}


	public short getLeftSeconds() {
		return leftSeconds;
	}


	public void setLeftSeconds(short leftSeconds) {
		this.leftSeconds = leftSeconds;
	}


	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime=endTime;
	}


	public boolean isWriteDisable4Access() {
		return writeDisable4Access;
	}


	public void setWriteDisable4Access(boolean writeDisable4Access) {
		this.writeDisable4Access = writeDisable4Access;
	}


	public boolean isHideAccessPannel() {
		return hideAccessPannel;
	}


	public void setHideAccessPannel(boolean hideAccessPannel) {
		this.hideAccessPannel = hideAccessPannel;
	}


	public boolean isAccessChanged() {
		return accessChanged;
	}


	public void setAccessChanged(boolean accessChanged) {
		this.accessChanged = accessChanged;
	}


	public boolean isHideTopPannel()
	{
		return hideTopPannel;
	}

	public void setHideTopPannel(boolean hideTopPannel)
	{
		this.hideTopPannel = hideTopPannel;
	}

	@Override
	public String execute() throws Exception {
 		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
				// SSL redirect (check this only in case of HTTP not enabled)
				return "ssl";
			}
			// Authentication.
			if (null == userContext) {
				// Need to redirect to login.
				return "login";
			}
			hideTopPannel = "/startHere.action".equals(request.getServletPath());
			
			if ("update".equals(operation) || "updateStart".equals(operation)) {
				log.info("execute", "operation:" + operation);
				
				try {
					saveObject(getDataSource(), adminPassword, domainId, userContext, getIsInHomeDomain(), ntpServer, timezone, this, 
						dnsSer1, dnsSer2, getDomain(), getDataSource().getModeType(),false,true);
					
					//Bug 25589 fix
					if(!updateAccessConsoleForQSWirelessOnlyNetworkPolicy(domainId,getDataSource().getNetworkName())){
						log.error("execute", "operation:" + operation + " updateAccessConsoleForQSWirelessOnlyNetworkPolicy error");
					}
					
					// generate log info
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.global.setting") + " (" + getDomain().getDomainName() + ")");
					addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, "global settings of "+getDomain().getDomainName()));
					
					if (null != getDataSource().getId()) {
						this.id = getDataSource().getId();
						
					}
					if(isAccessChanged())
					{
						saveAccessStatus();
					}
					
					setFormChanged(false);
					//initValues();
					return SUCCESS;
					
				} catch (Exception ex) {
					if (null != getDataSource().getId()) {
						this.id = getDataSource().getId();
					}
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.update.global.setting") + " (" + getDomain().getDomainName() + ")");
					addActionError(MgrUtil.getUserMessage(ex));
					return INPUT;
				}
			} 
			if("getEstamiteEndDate".equals(operation))
			{
				//caculate the endDate of authorized access
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, leftHours);
				Date endDate=cal.getTime();
				SimpleDateFormat sdf=new SimpleDateFormat("M/d/yy HH:mm a");
				sdf.setTimeZone(getUserTimeZone());
				jsonObject = new JSONObject();
				jsonObject.put("endDate", sdf.format(endDate));
				return "json";
			}else {
				initValues();
				setDefaultValue();
				return INPUT;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return INPUT;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setDataSource(HmStartConfig.class);
		setSelectedL2Feature(L2_FEATURE_START_HERE);
	}

	public HmStartConfig getDataSource() {
		return (HmStartConfig) dataSource;
	}

	public static boolean saveObject(HmStartConfig newObj, String hmPwd, Long domId, HmUser hmUser, boolean inHome, String ntp, int tzIndex, 
		QueryBo queryBo, String dns1, String dns2, HmDomain owner, short modeType,boolean rebootFlag,boolean updateNtpFlag) throws Exception {
		boolean updateTimeZoneFlag = false;
		try{
			// check mode
			boolean easyModeChange = false;
			boolean createQuickStart = false;
			if (null != newObj.getId()) {
				HmStartConfig oldObj = QueryUtil.findBoById(HmStartConfig.class, newObj.getId());
				// cannot change from full to easy
				if ((HmStartConfig.HM_MODE_FULL == modeType && HmStartConfig.HM_MODE_EASY == oldObj.getModeType() && newObj.isAdminUserLogin())
						|| HmStartConfig.HM_MODE_FULL == oldObj.getModeType()) {
					easyModeChange = true;
				}
				
//				if (HmStartConfig.HM_MODE_FULL == modeType && HmStartConfig.HM_MODE_EASY == oldObj.getModeType()
//					&& newObj.isAdminUserLogin()) {
//					ConfigTemplate wlan = QueryUtil.findBoByAttribute(ConfigTemplate.class, "defaultFlag", false, domId);
//					if (null != wlan) {
//						wlan.setBlnWirelessRouter(true);
//						QueryUtil.updateBo(wlan);
//					}
//				}
			}
			
			if (HmStartConfig.HM_MODE_FULL == modeType) {
				createQuickStart = true;
			}
			
			/*
			 *  deal with the data
			 */
			// update HiveManager password
			if (null != hmPwd && !"".equals(hmPwd)) {
				List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("defaultFlag", true),
					domId);
				HmUser defUser = null;
				if (!list.isEmpty()) {
					defUser = list.get(0);
					// encrypt the password
					defUser.setPassword(MgrUtil.digest(hmPwd));
					QueryUtil.updateBo(defUser);
				}
				
				//update shell admin password if update admin user
				if (hmUser.getDomain().isHomeDomain()) {
					updateShellAdminPwd(hmPwd);
				} else {
					// sys password change to portal
					if (null != defUser && NmsUtil.isHostedHMApplication()) {
						UserInfo info = new UserInfo();
						info.setEmailAddress(defUser.getEmailAddress());
						info.setFullname(defUser.getUserFullName());
						info.setPassword(MgrUtil.digest(hmPwd));
						info.setUsername(defUser.getUserName());
						info.setVhmName(defUser.getDomain().getDomainName());
						info.setGroupAttribute((short) HmUserGroup.CONFIG_ATTRIBUTE);
						VhmUserSync.syncForModifyVhmUser(info);
					}
				}
			}
			
			ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "defaultFlag", true);

			// network name
			ConfigTemplate wlanPolicy = null;
			if (!easyModeChange) {
				wlanPolicy = updateWlan(newObj, domId, owner, ntp, tzIndex, queryBo, dns1, dns2, modeType,rebootFlag,updateNtpFlag, profile);
			} else if (StringUtils.isNotBlank(newObj.getQuickStartPwd())) {
				// update access console password
				// remove quick start policies
				AccessConsole consoleObj = QueryUtil.findBoByAttribute(AccessConsole.class, "consoleName",  newObj.getNetworkName(), domId);
				if (consoleObj != null) {
					consoleObj.setAsciiKey(newObj.getQuickStartPwd());
					QueryUtil.updateBo(consoleObj);
				}
			}
			
			// create quick start object
			// remove quick start policies
			if (createQuickStart) {
				createQuickStartProfiles(domId, owner, newObj.getQuickStartPwd(), newObj.getNetworkName(), newObj.getQuickStartPwd(),ntp,tzIndex,queryBo,rebootFlag,updateNtpFlag, profile);
				prepareVpnNetworkObj(newObj.getNetworkName(), domId, owner);
			}
			
			// admin user change this config
			if (null != hmUser && null == hmUser.getSwitchDomain()) {
				newObj.setAdminUserLogin(true);
			}
//			if (HmStartConfig.HM_MODE_FULL == modeType) {	
//				newObj.setNetworkName("");
//				newObj.setUseAccessConsole(false);
//				
//				// remove the default express mode config
//				if (!newObj.isAdminUserLogin()) {
//					try {
//						FilterParams defParam = new FilterParams("owner.id", domId);
//						// remove wlan policy
//						QueryUtil.removeBos(ConfigTemplate.class, defParam);
//						
//						// remove hive profile
//						QueryUtil.removeBos(HiveProfile.class, defParam);
//						
//						// remove mgmt service time
//						QueryUtil.removeBos(MgmtServiceTime.class, defParam);
//						
//						// remove mgmt service option
//						QueryUtil.removeBos(MgmtServiceOption.class, defParam);
//						
//						// remove alg service
//						QueryUtil.removeBos(AlgConfiguration.class, defParam);
//						
//						// remove LLDPCDPProfile
//						QueryUtil.bulkRemoveBos(LLDPCDPProfile.class, null, domId);
//					} catch (Exception ex) {
//						log.error("saveObject", "remove default express config failed.", ex);
//					}
//				}
//			}
			
			boolean isChangedHiveAPPswEx = false;
			// HiveAP
			if (HmStartConfig.HM_MODE_EASY == newObj.getModeType()) {
				ConfigTemplate wlanPolicyAp = wlanPolicy;
				if (wlanPolicyAp == null) {
					wlanPolicyAp = HmBeParaUtil.getDefaultProfile(ConfigTemplate.class, null);
				}
				assignHiveApInfos(wlanPolicy, domId);
				if(null != newObj.getHiveApPassword()){
					if(!newObj.getHiveApPassword().equals(newObj.getOldHiveApPassword())){
						isChangedHiveAPPswEx = true;
					}
				}
				
			}
			
			
			// set server time when user is admin
			if (inHome && rebootFlag) {
				String errorMessage = updateDateTimeConfig(tzIndex);
				if ("".equals(errorMessage)) {
					updateTimeZoneFlag = true;
					log.info("updateDateTimeConfig in saveObject", HmBeResUtil.getString("datetime.timezone.success"));
				} else {
					log.error("updateDateTimeConfig in saveObject", HmBeResUtil.getString("datetime.update.error"));
				}
			}
			
			//update the default timezone of administration
			if(rebootFlag){
				// update virtual hm time zone
				HmDomain vhmdomain = QueryUtil.findBoById(HmDomain.class, domId);
				if (null != vhmdomain) {
					String timeZoneStr = HmBeOsUtil.getTimeZoneString(tzIndex);
					String oldTimeZone = vhmdomain.getTimeZoneString();

					vhmdomain.setTimeZone(timeZoneStr);
					BoMgmt.getDomainMgmt().updateDomain(vhmdomain);
					
					// update relation report info
					Utils.updateDomainTimezone(vhmdomain.getId(), HmBeOsUtil.getTimeZoneWholeStr(timeZoneStr), HmBeOsUtil.getTimeZoneWholeStr(oldTimeZone));
					
					hmUser.getDomain().setTimeZone(timeZoneStr);
					
					if(null == hmUser.getCustomerId()
							|| hmUser.getCustomerId().isEmpty()){
						//update timeZone in hmUser
						hmUser.setTimeZone(timeZoneStr);
						if (hmUser.getSwitchDomain() != null) {
							hmUser.getSwitchDomain().setTimeZone(timeZoneStr);
						}
						QueryUtil.updateBo(HmUser.class, "timeZone=:s1", new FilterParams("id=:s2",new Object[]{timeZoneStr,hmUser.getId()}));
					}
					
				}
			}
			
			// set owner
			newObj.setOwner(owner);
			
			// create or update the start here config
			if (null != newObj.getId()) {
				//this.id = newObj.getId();
				QueryUtil.updateBo(newObj);
			} else {
				QueryUtil.createBo(newObj);
			}
			
			
			/**
			 * this variable is only a switcher, do not re-use it unless you know what it means
			 */
			boolean createBonjourQuickStart = false;
			if(createBonjourQuickStart){
				createBonjourNetworkPolicy(domId, owner, newObj.getQuickStartPwd(), newObj.getNetworkName(),ntp,tzIndex,queryBo,rebootFlag,updateNtpFlag, profile);
			}
			
			if (HmStartConfig.HM_MODE_FULL == modeType 
					&& StringUtils.isNotBlank(newObj.getQuickStartPwd())) {
				// create a special QS profile here, it's QS-Bonjour-Service
				// the if condition is compared to previous version for createBonjourQuickStart, this profile is part of createBonjourNetworkPolicy
				getBonjourDefaultProfileForAerohive(domId, owner);
			}
			
			if(isChangedHiveAPPswEx) {
				// if change HiveAP settings, show the warning message
				MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
			}
			
			if (null != hmUser) {
				// init or update the mode
				hmUser.setMode(newObj.getModeType());
				
				// refresh navigation tree for user
				refreshNavigationTree(hmUser, queryBo);
			}
		}catch(Exception e){
			log.error("saveObject", "save HmStartConfig Object failed.", e);
			throw e;
		}
		return updateTimeZoneFlag;
	}
	
	private static String updateDateTimeConfig(int tzIndex) {
		// call be function
		String arg_Zone = HmBeOsUtil.getEnumsTimeZone()[tzIndex].getValue();
		Calendar calendar = HmBeOsUtil.getServerTime();
		SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmyyyy.ss");
		String currentTimeShow = formatter.format(calendar.getTime());
		
		int arg_NTP = HmBeOsUtil.ifNTPServiceStart() ? BeOsLayerModule.START_NTP_SERVICE
				: BeOsLayerModule.STOP_NTP_SERVICE;
		String errorMessage;
		try {
			errorMessage = HmBeOsUtil.setServerTime(arg_Zone, currentTimeShow, null, arg_NTP);
		} catch (Exception e) {
			log.error("updateDateTimeConfig", "update failed!", e);
			errorMessage = e.getMessage();
		}

		return errorMessage;
	}
	
	/**
	 * synchronize shell admin password with HM admin password
	 */
	public static void updateShellAdminPwd(String hmPwd)
	{
		try {
			String password = hmPwd;
			
			String[] cmd = { "bash", "-c", "passwd admin" };
			Process proc = Runtime.getRuntime().exec(cmd);

			PrintWriter out = new PrintWriter(new OutputStreamWriter(proc.getOutputStream()));
			// new password
			out.println(password);
			out.flush();

			// confirm password
			out.println(password);
			out.flush();
		} catch (Exception e) {
			log.error("updateAdminShellPwd", "catch exception",e);
		}
	}
	
	public EnumItem[] getEnumSystemLed() {
		return MgmtServiceOption.ENUM_SYSTEM_LED_BRIGHTNESS;
	}
	
	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}
	
	public static ConfigTemplate updateWlan(HmStartConfig newObj, Long domId, HmDomain owner, String ntp, int tzIndex, QueryBo queryBo,
		String dns1, String dns2, short modeType,boolean rebootFlag,boolean updateNtpFlag, ApplicationProfile profile) throws Exception {
		// new wlan name
		String wlanName = newObj.getNetworkName();
		ConfigTemplate wlan = null;
		if (!"".equals(wlanName)) {
			// get the hive object
			Long hiveId = updateHive(wlanName, domId, owner);
			HiveProfile hive = HmBeParaUtil.getDefaultProfile(HiveProfile.class, null);
			if (null != hiveId) {
				hive = QueryUtil.findBoById(HiveProfile.class, hiveId);
			}
			
			if (!DeviceFreevalUtil.isDeviceFreevalDefinedInVHM(owner)) {
				wlan = QueryUtil.findBoByAttribute(ConfigTemplate.class, "defaultFlag", false, domId);
			} else {
				wlan = DeviceFreevalUtil.getFirstConfigTemplateNotDeviceFreeval(owner, null, null);
			}
			if (null != wlan) {
				// set wlan object
				//wlan.setConfigName(BeParaModule.DEFAULT_NETWORK_POLICY_NAME);
				wlan.setHiveProfile(hive);
				
				// set some object is null
				wlan.setMgmtServiceDns(null);
				wlan.setMgmtServiceTime(null);
				wlan.setAccessConsole(null);
				wlan.setMgmtServiceOption(null);
				wlan.setAlgConfiguration(null);
				wlan.setLldpCdp(null);
				wlan.setIpTracks(null);
				Date oldVerD = wlan.getVersion();
				wlan = QueryUtil.updateBo(wlan);
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						wlan, ConfigurationChangedEvent.Operation.UPDATE,
						oldVerD, true));
			} else if (HmStartConfig.HM_MODE_EASY == modeType) {
				// create ntp server object
				wlan = new ConfigTemplate(ConfigTemplateType.WIRELESS);
				// set wlan object
				wlan.setConfigName(wlanName);
				wlan.setDescription(MgrUtil.getUserMessage("policy.predefined.description.network.policy.domain"));
				wlan.setHiveProfile(hive);
				wlan.setOwner(owner);
				wlan.setMgmtServiceSnmp(HmBeParaUtil.getDefaultProfile(
						MgmtServiceSnmp.class, null));
				Vlan defVlan = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
				wlan.setVlan(defVlan);
				wlan.setVlanNative(defVlan);
//				wlan.setAlgConfiguration((AlgConfiguration)HmBeParaUtil.getDefaultProfile(
//						AlgConfiguration.class, null));
				Map<Long, ConfigTemplateSsid> ssidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
				ServiceFilter serviceFilter = HmBeParaUtil.getDefaultProfile(ServiceFilter.class,
						null);
				wlan.setDeviceServiceFilter(serviceFilter);
				wlan.setEth0ServiceFilter(serviceFilter);
				wlan.setEth1ServiceFilter(serviceFilter);
				wlan.setRed0ServiceFilter(serviceFilter);
				wlan.setAgg0ServiceFilter(serviceFilter);
				wlan.setWireServiceFilter(serviceFilter);
				wlan.setEth0BackServiceFilter(serviceFilter);
				wlan.setEth1BackServiceFilter(serviceFilter);
				wlan.setRed0BackServiceFilter(serviceFilter);
				wlan.setAgg0BackServiceFilter(serviceFilter);
				wlan.getConfigType().setWirelessEnabled(true);
				ssidInterfaces = BoGenerationUtil.genDefaultSsidInterfaces();
				wlan.setSsidInterfaces(ssidInterfaces);
			}
			
			// these objects should always be created
			Long ntpId = updateNtpServer(wlanName, domId, owner, ntp, tzIndex, queryBo,rebootFlag,updateNtpFlag);
			Long dnsId = updateDnsServer(wlanName, domId, owner, dns1, dns2, queryBo);
			Long mgmtId = getMgmtOption(wlanName, domId, owner, newObj.getLedBrightness());
			Long algId = getAlgService(wlanName, domId, owner);
			Long lldpId = getLLDPCDPProfile(wlanName, domId, owner);
			Long qosId = getQosClassification(wlanName, domId, owner);
			if (wlan == null) {
				if (newObj.isUseAccessConsole()) {
					getIpTrack(wlanName, domId, owner);
					getAccessConsole(wlanName, domId, owner, newObj.getAsciiKey());
				}
			} else {
				// get the ntp server object
				if (null != ntpId) {
					wlan.setMgmtServiceTime(QueryUtil.findBoById(MgmtServiceTime.class, ntpId, queryBo));
				}
				
				// get new dns id
				if (null != dnsId) {
					wlan.setMgmtServiceDns(QueryUtil.findBoById(MgmtServiceDns.class, dnsId, queryBo));
				}
				
				// set mgmt service option
				if (null != mgmtId) {
					wlan.setMgmtServiceOption(QueryUtil.findBoById(MgmtServiceOption.class, mgmtId));
				}
				
				// set ALG service
				if (null != mgmtId) {
					wlan.setAlgConfiguration(QueryUtil.findBoById(AlgConfiguration.class, algId));
				}
				
				// set LLDPCDPProfile
				if (null != lldpId) {
					wlan.setLldpCdp(QueryUtil.findBoById(LLDPCDPProfile.class, lldpId));
				}
				
				// set Qos map
				if(null != qosId){
					wlan.setClassifierMap(QueryUtil.findBoById(QosClassification.class, qosId));
				}
				
				// use virtual access
				if (newObj.isUseAccessConsole()) {
					
					// set ip track
					Set<MgmtServiceIPTrack> ipTracks = new HashSet<MgmtServiceIPTrack>();
					Long trackId = getIpTrack(wlanName, domId, owner);
					if (null != trackId) {
						ipTracks.add(QueryUtil.findBoById(MgmtServiceIPTrack.class, trackId));
					}
					wlan.setIpTracks(ipTracks);
					
					// set access console
					wlan.setAccessConsole(QueryUtil.findBoById(AccessConsole.class, getAccessConsole(wlanName, domId, owner, newObj.getAsciiKey())));
				}
				
				/*if (wlan.getVlanNetwork()==null) {
					List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<ConfigTemplateVlanNetwork>();
					wlan.setVlanNetwork(vlanNetwork);
				} else {
					wlan.getVlanNetwork().clear();
				}*/
				List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<ConfigTemplateVlanNetwork>();
				wlan.setVlanNetwork(vlanNetwork);
				
				if (profile != null) {
					wlan.setAppProfile(profile);
				}
				
				// wireless router
				//wlan.setBlnWirelessRouter(HmStartConfig.HM_MODE_FULL == modeType);
				
				// create new wlan policy
				if (null == wlan.getId()) {
					Long newId = QueryUtil.createBo(wlan);
					wlan = QueryUtil.findBoById(ConfigTemplate.class, newId);
				// update the exist wlan policy
				} else {
					Date oldVerD = wlan.getVersion();
					wlan = QueryUtil.updateBo(wlan);
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							wlan, ConfigurationChangedEvent.Operation.UPDATE,
							oldVerD, true));
				}
			}
		}
		return wlan;
	}
	
	private static void createQuickStartProfiles(Long domId, HmDomain owner, String password, String hiveName, String acPwd,String ntp,int tzIndex, QueryBo queryBo,boolean rebootFlag,boolean updateNtpFlag, ApplicationProfile profile) {
		// vpn network
		VpnNetwork vpnNet = QueryUtil.findBoByAttribute(VpnNetwork.class, "networkName", BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_USERPROFILE,
			domId);
		if (null == vpnNet) {
			vpnNet = new VpnNetwork();
			vpnNet.setNetworkName(BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_USERPROFILE);
			vpnNet.setNetworkType(VpnNetwork.VPN_NETWORK_TYPE_INTERNAL);
			vpnNet.setOwner(owner);
			vpnNet.setDescription(MgrUtil.getUserMessage("policy.predefined.description.network.usr"));

			Vlan vlan = QueryUtil.findBoByAttribute(Vlan.class, "vlanName", "2", domId);
			if (null == vlan) {
				Vlan dto_Vlan = new Vlan();
				dto_Vlan.setVlanName("2");
				dto_Vlan.setOwner(owner);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				SingleTableItem single = new SingleTableItem();
				single.setDescription(MgrUtil.getUserMessage("policy.predefined.description.vlan2.network"));
				single.setVlanId(2);
				single.setType(SingleTableItem.TYPE_GLOBAL);
				items.add(single);
				dto_Vlan.setItems(items);
				try {
					Long newId = QueryUtil.createBo(dto_Vlan);
					vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class, newId);
				} catch (Exception ex) {
					log.error("getVpnNetworkObj()", "insert pre-defined VLAN : "+ex.getMessage());
				}
			}
//			vpnNet.setVlan(vlan);
			
			DnsServiceProfile dnsObj = QueryUtil.findBoByAttribute(DnsServiceProfile.class, "serviceName", hiveName, domId);
			if (null == dnsObj) {
				DnsServiceProfile dto_Dns = new DnsServiceProfile();
				dto_Dns.setServiceName(hiveName);
				dto_Dns.setOwner(owner);
				dto_Dns.setServiceType(DnsServiceProfile.SAME_DNS);
				dto_Dns.setExternalServerType(DnsServiceProfile.LOCAL_DNS_TYPE);
				dto_Dns.setDescription(MgrUtil.getUserMessage("policy.predefined.description.dns.service.network"));
				try {
					Long newId = QueryUtil.createBo(dto_Dns);
					dnsObj = AhRestoreNewTools.CreateBoWithId(DnsServiceProfile.class, newId);
				} catch (Exception ex) {
					log.error("createQuickStartNetworkPolicy()", "insert pre-defined DNS service : "+ex.getMessage());
				}
			}
			vpnNet.setVpnDnsService(dnsObj);
			vpnNet.setEnableDhcp(false);
			
			List<VpnNetworkSub> subItems = new ArrayList<VpnNetworkSub>();
			VpnNetworkSub subItem = new VpnNetworkSub();
			subItem.setKey(1);
			subItem.setIpNetwork("172.28.0.0/16");
			subItem.setLocalIpNetwork("172.28.0.0/16");
			subItem.setIpBranches(512);
			subItem.setEnableDhcp(true);
			subItem.setLeftEnd(10);
			subItem.setRightEnd(10);
			subItems.add(subItem);
			vpnNet.setSubItems(subItems);
			
			try {
				Long netNewId = QueryUtil.createBo(vpnNet);
				vpnNet = AhRestoreNewTools.CreateBoWithId(VpnNetwork.class, netNewId);
				VpnNetworksAction.prepareSubNetworkRes(netNewId, null);
			} catch (Exception ex) {
				log.error("createQuickStartNetworkPolicy()", "create new network object error : "+ex.getMessage());
			}
		}
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static void createBonjourNetworkPolicy(Long domId, HmDomain owner, String acPwd, String hiveName,String ntp,int tzIndex, QueryBo queryBo,boolean rebootFlag,boolean updateNtpFlag, ApplicationProfile profile){
		ConfigTemplate wlan = QueryUtil.findBoByAttribute(ConfigTemplate.class, "configName", BeParaModule.PRE_DEFINED_BONJOUR_NETWORK_POLICY,
			domId);
		Long accId = getAccessConsole(hiveName, domId, owner, acPwd);
		if (null == wlan) {
			wlan = new ConfigTemplate(ConfigTemplateType.BONJOUR);
			// set wlan object
			wlan.setConfigName(BeParaModule.PRE_DEFINED_BONJOUR_NETWORK_POLICY);
			wlan.setDescription(MgrUtil.getUserMessage("policy.predefined.description.qs.bonjour.policy"));
			// wlan.setHiveProfile(HmBeParaUtil.getDefaultProfile(HiveProfile.class, null));
			wlan.setHiveProfile(QueryUtil.findBoById(HiveProfile.class, updateHive(hiveName, domId, owner)));
			wlan.setOwner(owner);
			/*wlan.setBlnBonjourOnly(true);
			wlan.setBlnWirelessRouter(false);*/
			wlan.setMgmtServiceSnmp(HmBeParaUtil.getDefaultProfile(MgmtServiceSnmp.class, null));
			Vlan defVlan = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
			wlan.setVlan(defVlan);
			wlan.setVlanNative(defVlan);
			//create default profile for aerohive
			getBonjourDefaultProfileForAerohive(domId,owner);
			//Set default profile for apple
			wlan.setBonjourGw(getBonjourDefaultProfileForApple(domId,owner));
			
			wlan.setAlgConfiguration(HmBeParaUtil.getDefaultProfile(
				AlgConfiguration.class, null));
			
			Map<Long, ConfigTemplateSsid> ssidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
			ssidInterfaces = BoGenerationUtil.genDefaultSsidInterfaces();
			wlan.setSsidInterfaces(ssidInterfaces);
			
			ServiceFilter serviceFilter = HmBeParaUtil.getDefaultProfile(ServiceFilter.class, null);
			wlan.setDeviceServiceFilter(serviceFilter);
			wlan.setEth0ServiceFilter(serviceFilter);
			wlan.setEth1ServiceFilter(serviceFilter);
			wlan.setRed0ServiceFilter(serviceFilter);
			wlan.setAgg0ServiceFilter(serviceFilter);
			wlan.setWireServiceFilter(serviceFilter);
			wlan.setEth0BackServiceFilter(serviceFilter);
			wlan.setEth1BackServiceFilter(serviceFilter);
			wlan.setRed0BackServiceFilter(serviceFilter);
			wlan.setAgg0BackServiceFilter(serviceFilter);
		
			// vpn network object
//			wlan.setMgtNetwork(QueryUtil.findBoById(VpnNetwork.class, getVpnNetworkObj(hiveName, domId, owner)));
			// Access Console
			wlan.setAccessConsole(QueryUtil.findBoById(AccessConsole.class, accId));
			// NTP server
			if(rebootFlag){
				Long ntpId = updateNtpServer(hiveName, domId, owner, ntp, tzIndex, queryBo,rebootFlag,updateNtpFlag);
				if (null != ntpId) {
					wlan.setMgmtServiceTime(QueryUtil.findBoById(MgmtServiceTime.class, ntpId, queryBo));
				}
			}else{
				wlan.setMgmtServiceTime(QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", hiveName, domId));
			}
			//wlan.setMgmtServiceTime(QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", hiveName, domId));
			// Link Discover Protocol
			wlan.setLldpCdp(QueryUtil.findBoByAttribute(LLDPCDPProfile.class, "profileName", hiveName, domId));
			// Classifier Map
			wlan.setClassifierMap(QueryUtil.findBoById(QosClassification.class, getQosClassification(hiveName, domId, owner)));
			// set mgmt service option
			wlan.setMgmtServiceOption(QueryUtil.findBoByAttribute(MgmtServiceOption.class,"mgmtName",hiveName, domId));
			//set mgmt dns 
			wlan.setMgmtServiceDns(QueryUtil.findBoByAttribute(MgmtServiceDns.class,"mgmtName",hiveName, domId));
			// set ip track
			Set<MgmtServiceIPTrack> ipTracks = new HashSet<MgmtServiceIPTrack>();
			Long trackId = getIpTrack(hiveName, domId, owner);
			if (null != trackId) {
				ipTracks.add(QueryUtil.findBoById(MgmtServiceIPTrack.class, trackId));
			}
			wlan.setIpTracks(ipTracks);
			if (profile != null) {
				wlan.setAppProfile(profile);
			}
			
			try {
				QueryUtil.createBo(wlan);
			} catch (Exception ex) {
				log.error("createBonjourNetworkPolicy()", "create new Bonjour only Network Policy error : "+ex.getMessage());
			}
		}
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static BonjourGatewaySettings getBonjourDefaultProfileForAerohive(Long domId, HmDomain owner){
		// BonjourGatewaySettings profile
		BonjourGatewaySettings bgsAerohive = QueryUtil.findBoByAttribute(BonjourGatewaySettings.class, "bonjourGwName", BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_AEROHIVE,
			domId);
		try {
			if (null == bgsAerohive) {
				bgsAerohive = new BonjourGatewaySettings();
				bgsAerohive.setBonjourGwName(BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_AEROHIVE);
				bgsAerohive.setOwner(owner);
				bgsAerohive.setDescription(MgrUtil.getUserMessage("policy.predefined.description.qs.bonjour.gateway.aerohive"));
				//set Aerohive service
				bgsAerohive.setBonjourActiveServices(getBonjourAerohiveService(domId));
				//add rules for service
				bgsAerohive.setRules(getBonjouFilterRule(bgsAerohive.getBonjourActiveServices()));
				
				Long netNewId = QueryUtil.createBo(bgsAerohive);
				bgsAerohive = AhRestoreNewTools.CreateBoWithId(BonjourGatewaySettings.class, netNewId);
			} 
				
		} catch (Exception ex) {
			log.error("getBonjourDefaultProfileForAerohive()", "create new Bonjour Gateway Profile for Aerohive users error : "+ex.getMessage());
		}
		return bgsAerohive;
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static List<BonjourActiveService> getBonjourAerohiveService(Long domId){
		List<BonjourActiveService> bonjourActiveServices = new ArrayList<BonjourActiveService>();
		List<String> allServiceList = new ArrayList<String>();
		allServiceList.add(BonjourService.AEROHIVE_HTTP_PROXY_CONFIGURATION_TYPE);
		allServiceList.add(BonjourService.AEROHIVE_SERVICES_TCP_TYPE);
		allServiceList.add(BonjourService.AEROHIVE_SERVICES_UDP_TYPE);
		
		for(String type:allServiceList){
			BonjourActiveService bas = new BonjourActiveService();
			BonjourService bService = QueryUtil.findBoByAttribute(BonjourService.class, "type", type, domId);
			bas.setBonjourService(bService);
			bonjourActiveServices.add(bas);
		}
		
		return bonjourActiveServices;
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static BonjourGatewaySettings getBonjourDefaultProfileForApple(Long domId, HmDomain owner){
		// BonjourGatewaySettings profile
		BonjourGatewaySettings bgsApple = QueryUtil.findBoByAttribute(BonjourGatewaySettings.class, "bonjourGwName", BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_APPLE,
			domId);
		try {
			if (null == bgsApple) {
				bgsApple = new BonjourGatewaySettings();
				bgsApple.setBonjourGwName(BeParaModule.PRE_DEFINED_BONJOUR_PROFILE_APPLE);
				bgsApple.setOwner(owner);
				bgsApple.setDescription(MgrUtil.getUserMessage("policy.predefined.description.qs.bonjour.gateway.apple"));
				//all service should be selected
				bgsApple.setBonjourActiveServices(getAllBonjourService(domId));
				//add rules for service
				bgsApple.setRules(getBonjouFilterRule(bgsApple.getBonjourActiveServices()));
				
				Long netNewId = QueryUtil.createBo(bgsApple);
				bgsApple = AhRestoreNewTools.CreateBoWithId(BonjourGatewaySettings.class, netNewId);
			} 
			
		} catch (Exception ex) {
			log.error("getBonjourDefaultProfileForApple()", "create new Bonjour Gateway Profile for Apple users error : "+ex.getMessage());
		}
		return bgsApple;
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static List<BonjourActiveService> getAllBonjourService(Long domId){
		List<BonjourActiveService> bonjourActiveServices = new ArrayList<BonjourActiveService>();
		List<String> allServiceList = new ArrayList<String>();
		allServiceList.add(BonjourService.AEROHIVE_HTTP_PROXY_CONFIGURATION_TYPE);
		allServiceList.add(BonjourService.AEROHIVE_SERVICES_TCP_TYPE);
		allServiceList.add(BonjourService.AEROHIVE_SERVICES_UDP_TYPE);
		allServiceList.add(BonjourService.AFP_TYPE);
		allServiceList.add(BonjourService.AIRPLAY_TYPE);
		allServiceList.add(BonjourService.BITTORRENT_TYPE);
		allServiceList.add(BonjourService.FTP_TYPE);
		allServiceList.add(BonjourService.ICHAT_TYPE);
		allServiceList.add(BonjourService.INTERNET_PRINTING_PROTOCOL_TYPE);
		allServiceList.add(BonjourService.ITUNES_TYPE);
		allServiceList.add(BonjourService.JETDIRECT_TYPE);
		allServiceList.add(BonjourService.LPR_TYPE);
		allServiceList.add(BonjourService.SAMBA_TYPE);
		allServiceList.add(BonjourService.SHELL_TYPE);
		allServiceList.add(BonjourService.SSH_TYPE);
		allServiceList.add(BonjourService.TELNET_TYPE);
		allServiceList.add(BonjourService.REMOTE_AUDIO_OUTPUT_SERVICES_TYPE);
		allServiceList.add(BonjourService.APPLE_TV_SERVICES_TYPE);
		allServiceList.add(BonjourService.HOME_SHARING_SERVICES_TYPE);
		
		for(String type:allServiceList){
			BonjourActiveService bas = new BonjourActiveService();
			BonjourService bService = QueryUtil.findBoByAttribute(BonjourService.class, "type", type, domId);
			bas.setBonjourService(bService);
			bonjourActiveServices.add(bas);
		}
		
		return bonjourActiveServices;
	}
	
	/**
	 * <b>Please do not refer to this method, it should be removed once Bonjour quick start network policy is removed</b>
	 */
	private static List<BonjourFilterRule> getBonjouFilterRule(List<BonjourActiveService> services){
		List<BonjourFilterRule> rules = new ArrayList<BonjourFilterRule>();
		if(null != services){
			short i = 1;
			for(BonjourActiveService service : services){
				BonjourFilterRule rule = new BonjourFilterRule();
				rule.setBonjourService(service.getBonjourService());
				//rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
				rule.setRuleId(i);
				i++;
//				rule.setFromVlanGroup(null);
//				rule.setMetric(null);
//				rule.setToVlanGroup(null);
				rules.add(rule);
			}
		}

		return rules;
	}
	
	private static Long updateNtpServer(String netName, Long domId, HmDomain owner, String ntp, int tzIndex, QueryBo queryBo,boolean rebootFlag,boolean updateFlag) {
		List<MgmtServiceTimeInfo> timeInfo = null;
		if (!"".equals(ntp)) {
			// create the ip address object
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(ntp) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			IpAddress ipObj = CreateObjectAuto.createNewIP(ntp, ipType, owner, "For Express NTP Assignment");
			// create the ntp server item
			timeInfo = new ArrayList<MgmtServiceTimeInfo>();
			MgmtServiceTimeInfo singleInfo = new MgmtServiceTimeInfo();
			singleInfo.setIpAddress(ipObj);
			singleInfo.setTimeDescription(MgrUtil.getUserMessage("policy.predefined.description.ntp.np"));
			timeInfo.add(singleInfo);
		}
		MgmtServiceTime ntpServer;
		List<MgmtServiceTime> list = QueryUtil.executeQuery(MgmtServiceTime.class, null, new FilterParams("owner.id", domId), null, queryBo);
		if (!list.isEmpty()) {
			ntpServer = list.get(0);
		} else {
			// create ntp server object
			ntpServer = new MgmtServiceTime();
		}
		ntpServer.setMgmtName(netName);
		ntpServer.setEnableClock(null == timeInfo);
		if(rebootFlag || updateFlag){
			ntpServer.setTimeZoneStr(HmBeOsUtil.getTimeZoneString(tzIndex));
		}else{
			ntpServer.setTimeZoneStr(owner.getTimeZoneString());
		}
		
		ntpServer.setTimeInfo(timeInfo);
		if (!list.isEmpty()) {
			try {
				QueryUtil.updateBo(ntpServer);
			} catch (Exception ex) {
				log.error("updateNtpServer()", "update error : "+ex.getMessage());
			}	
			return ntpServer.getId();
		} else {
			// create ntp server object
			ntpServer.setDescription(MgrUtil.getUserMessage("policy.predefined.description.ntp.np"));
			ntpServer.setOwner(owner);
			try {
				return QueryUtil.createBo(ntpServer);
			} catch (Exception ex) {
				log.error("updateNtpServer()", "create error : "+ex.getMessage());
			}
		}
		return null;
	}
	
	private static Long updateDnsServer(String netName, Long domId, HmDomain owner, String dns1, String dns2, QueryBo queryBo) {
		// get all the management dns from database
		List<MgmtServiceDns> list = QueryUtil.executeQuery(MgmtServiceDns.class, null, new FilterParams("owner.id", domId), null, queryBo);
		MgmtServiceDns dnsServer;
		if ("".equals(dns1) && "".equals(dns2)) {
			try {
				// remove all the dns records
				QueryUtil.removeBos(MgmtServiceDns.class, new FilterParams("owner.id", domId));
			} catch (Exception ex) {
				log.error("updateDnsServer()", "remove error : "+ex.getMessage());
			}
		} else {
			if (list.isEmpty()) {
				dnsServer = new MgmtServiceDns();
				dnsServer.setDescription(MgrUtil.getUserMessage("policy.predefined.description.dns.network"));
				dnsServer.setOwner(owner);
			} else {
				dnsServer = list.get(0);
			}
			dnsServer.setMgmtName(netName); 
			
			// create the dns server item
			List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();
			if (!"".equals(dns1)) {
				dnsInfo.add(getMgmtDns(dns1, "Primary", owner));
			}
			if (!"".equals(dns2)) {
				dnsInfo.add(getMgmtDns(dns2, "Secondary", owner));
			}
			dnsServer.setDnsInfo(dnsInfo);
			
			if (list.isEmpty()) {
				try {
					return QueryUtil.createBo(dnsServer);
				} catch (Exception ex) {
					log.error("updateDnsServer()", "create new dns service error : "+ex.getMessage());
				}
			} else {
				try {
					QueryUtil.updateBo(dnsServer);
				} catch (Exception ex) {
					log.error("updateDnsServer()", "update the exist dns service error : "+ex.getMessage());
				}
				return dnsServer.getId();
			}
		}
		return null;
	}
	
	private static MgmtServiceDnsInfo getMgmtDns(String dnsIp, String dnsPrio, HmDomain owner) {
		// create the ip address object
		IpAddress ipObj = CreateObjectAuto.createNewIP(dnsIp, IpAddress.TYPE_IP_ADDRESS, owner, "For Express "+dnsPrio+" DNS Assignment");
		
		// create the dns server item
		MgmtServiceDnsInfo singleInfo = new MgmtServiceDnsInfo();
		singleInfo.setIpAddress(ipObj);
		singleInfo.setServerName(ipObj.getAddressName());
		singleInfo.setDnsDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s ("+dnsPrio+")");
		return singleInfo;
	}
	
	private static Long getIpTrack(String proName, Long domId, HmDomain owner) {
		// get all the ip tracks from database
		List<MgmtServiceIPTrack> list = QueryUtil.executeQuery(MgmtServiceIPTrack.class, null, new FilterParams("owner.id", domId));
		MgmtServiceIPTrack ipTrack;
		if (list.isEmpty()) {
			ipTrack = new MgmtServiceIPTrack();
			ipTrack.setTrackName(proName);
			ipTrack.setEnableAccess(true);
			ipTrack.setUseGateway(true);
			ipTrack.setOwner(owner);
			ipTrack.setDescription(MgrUtil.getUserMessage("policy.predefined.description.ip.track.domain"));
			try {
				return QueryUtil.createBo(ipTrack);
			} catch (Exception ex) {
				log.error("getIpTrack()", "create new ip track error : "+ex.getMessage());
			}
		} else {
			ipTrack = list.get(0);
			// the track name change
			if (!proName.equals(ipTrack.getTrackName())) {
				ipTrack.setTrackName(proName);
				try {
					QueryUtil.updateBo(ipTrack);
				} catch (Exception ex) {
					log.error("getIpTrack()", "update the exist ip track error : "+ex.getMessage());
				}
			}
			return ipTrack.getId();
		}
		return null;
	}
	
	private static Long getAccessConsole(String proName, Long domId, HmDomain owner, String shareKey) {
		// get all the access console from database
		List<AccessConsole> list = QueryUtil.executeQuery(AccessConsole.class, null, new FilterParams("owner.id", domId));
		AccessConsole accConsole;
		if (list.isEmpty()) {
			accConsole = new AccessConsole();
			accConsole.setConsoleName(proName);
			accConsole.setConsoleMode(AccessConsole.ACCESS_CONSOLE_MODE_AUTO);
			accConsole.setMgmtKey(SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK);
			accConsole.setEncryption(SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP);
			accConsole.setAsciiKey(shareKey);
			accConsole.setHideSsid(false);
			accConsole.setEnableTelnet(true);
			accConsole.setOwner(owner);
			accConsole.setDescription(MgrUtil.getUserMessage("policy.predefined.description.access.console.domain"));
			try {
				return QueryUtil.createBo(accConsole);
			} catch (Exception ex) {
				log.error("getAccessConsole()", "create new access console error : "+ex.getMessage());
			}
		} else {
			accConsole = list.get(0);
			// the access console name change
//			if (!proName.equals(accConsole.getConsoleName()) || (null != shareKey &&
//				!shareKey.equals(accConsole.getAsciiKey()))) {
				accConsole.setConsoleName(proName);
				accConsole.setAsciiKey(shareKey);
				try {
					QueryUtil.updateBo(accConsole);
				} catch (Exception ex) {
					log.error("getAccessConsole()", "update the exist access console error : "+ex.getMessage());
				}
			//}
			return accConsole.getId();
		}
		return null;
	}
	
	private static Long getMgmtOption(String proName, Long domId, HmDomain owner, short ledBrignt) {
		// get all the mgmt service option from database
		List<MgmtServiceOption> list = QueryUtil.executeQuery(MgmtServiceOption.class, null, new FilterParams("owner.id", domId));
		MgmtServiceOption mgmtOption;
		if (list.isEmpty()) {
			mgmtOption = new MgmtServiceOption();
			mgmtOption.setMgmtName(proName);
			mgmtOption.setSystemLedBrightness(ledBrignt);
			mgmtOption.setOwner(owner);
			mgmtOption.setDescription(MgrUtil.getUserMessage("policy.predefined.description.mgmt.option.domain"));
			try {
				return QueryUtil.createBo(mgmtOption);
			} catch (Exception ex) {
				log.error("getMgmtOption()", "create new mgmt option error : "+ex.getMessage());
			}
		} else {
			mgmtOption = list.get(0);
			// the mgmt option name change
			if (!proName.equals(mgmtOption.getMgmtName()) || 
				ledBrignt != mgmtOption.getSystemLedBrightness()) {
				mgmtOption.setMgmtName(proName);
				mgmtOption.setSystemLedBrightness(ledBrignt);
				try {
					QueryUtil.updateBo(mgmtOption);
				} catch (Exception ex) {
					log.error("getMgmtOption()", "update the exist mgmt option error : "+ex.getMessage());
				}
			}
			return mgmtOption.getId();
		}
		return null;
	}
	
	private static Long getAlgService(String proName, Long domId, HmDomain owner) {
		// get all the ALG from database
		AlgConfiguration defAlg = QueryUtil.findBoByAttribute(AlgConfiguration.class, "defaultFlag", false, domId);
		if (null == defAlg) {
			defAlg = new AlgConfiguration();
			defAlg.setConfigName(proName);
			Map<String, AlgConfigurationInfo> items = new LinkedHashMap<String, AlgConfigurationInfo>();
			for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType.values()) {
				AlgConfigurationInfo oneItem = defAlg.getAlgInfo(gatewayType);
				if (oneItem == null) {
					oneItem = new AlgConfigurationInfo();
					oneItem.setIfEnable(!(GatewayType.DNS.equals(gatewayType) || GatewayType.HTTP.equals(gatewayType)));
					if (GatewayType.SIP.equals(gatewayType)) {
						oneItem.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
						oneItem.setTimeout(60);
						oneItem.setDuration(720);
					}
				}
				oneItem.setGatewayType(gatewayType);
				items.put(oneItem.getkey(), oneItem);
			}
			defAlg.setItems(items);
			defAlg.setOwner(owner);
			defAlg.setDescription(MgrUtil.getUserMessage("policy.predefined.description.alg.service.domain"));
			try {
				return QueryUtil.createBo(defAlg);
			} catch (Exception ex) {
				log.error("getAlgService()", "create new ALG service error : "+ex.getMessage());
			}
		} else {
			// the alg profile name change
			if (!proName.equals(defAlg.getConfigName())) {
				defAlg.setConfigName(proName);
				try {
					QueryUtil.updateBo(defAlg);
				} catch (Exception ex) {
					log.error("getAlgService()", "update the exist ALG service error : "+ex.getMessage());
				}
			}
			return defAlg.getId();
		}
		return null;
	}
	
	private static Long getLLDPCDPProfile(String proName, Long domId, HmDomain owner) {
		// get all the LLDPCDPProfile from database
		List<LLDPCDPProfile> list = QueryUtil.executeQuery(LLDPCDPProfile.class, null, new FilterParams("owner.id", domId));
		LLDPCDPProfile lldpOrCdp;
		if (list.isEmpty()) {
			lldpOrCdp = new LLDPCDPProfile();
			lldpOrCdp.setProfileName(proName);
			lldpOrCdp.setOwner(owner);
			lldpOrCdp.setDescription(MgrUtil.getUserMessage("policy.predefined.description.lldp.cdp.domain"));
			try {
				return QueryUtil.createBo(lldpOrCdp);
			} catch (Exception ex) {
				log.error("getLLDPCDPProfile()", "create new LLDPCDPProfile error : "+ex.getMessage());
			}
		} else {
			lldpOrCdp = list.get(0);
			// the profile name change
			if (!proName.equals(lldpOrCdp.getProfileName())) {
				lldpOrCdp.setProfileName(proName);
				try {
					QueryUtil.updateBo(lldpOrCdp);
				} catch (Exception ex) {
					log.error("getLLDPCDPProfile()", "update the exist LLDPCDPProfile error : "+ex.getMessage());
				}
			}
			return lldpOrCdp.getId();
		}
		return null;
	}
	
	private static Long getQosClassification(String proName, Long domId, HmDomain owner){
		// classification
		QosClassification qosClass = QueryUtil.findBoByAttribute(QosClassification.class, "classificationName", proName,
			domId);
		if (null == qosClass) {
			qosClass = new QosClassification();
			qosClass.setClassificationName(proName);
			qosClass.setOwner(owner);
			qosClass.setDescription(MgrUtil.getUserMessage("policy.predefined.description.classifier.map.domain"));
			qosClass.setNetworkServicesEnabled(true);
			
			Map<Long, QosNetworkService> networkServices = new HashMap<Long, QosNetworkService>();
			QosNetworkService serviceInfo;
			for (String[] preServiceInfo : BeParaModule.NETWORK_PRE_DEFIND_SERVICES_FOR_QOS) {
				serviceInfo = new QosNetworkService();
				NetworkService netSer = QueryUtil.findBoByAttribute(NetworkService.class, "serviceName", preServiceInfo[0]);
				if (null != netSer) {
					serviceInfo.setNetworkService(netSer);
					serviceInfo.setQosClass((short)Integer.parseInt(preServiceInfo[1]));
					serviceInfo.setFilterAction(MacFilter.FILTER_ACTION_PERMIT);
					serviceInfo.setLogging(EnumConstUtil.DISABLE);
					networkServices.put(netSer.getId(), serviceInfo);
				}
			}
			qosClass.setNetworkServices(networkServices);
			
			try {
				return QueryUtil.createBo(qosClass);
			} catch (Exception ex) {
				log.error("createQuickStartNetworkPolicy()", "create new classification map error : "+ex.getMessage());
			}
		}
		return qosClass.getId();
	}
	
	private static Long prepareVpnNetworkObj(String proName, Long domId, HmDomain owner) {
		// get all the vpn network object from database
		List<VpnNetwork> list = QueryUtil.executeQuery(VpnNetwork.class, null, new FilterParams("owner.id", domId));
		List<String> deviceFreevalNetworkObjNames = null;
		if (DeviceFreevalUtil.isDeviceFreevalDefinedInVHM(owner)) {
			deviceFreevalNetworkObjNames = DeviceFreevalUtil.getNetworkObjectNamesOfDeviceFreeval(owner, null);
		}
		if (deviceFreevalNetworkObjNames == null) {
			deviceFreevalNetworkObjNames = new ArrayList<String>(1);
		}
		
		// get DNS service object
		DnsServiceProfile dnsObj = QueryUtil.findBoByAttribute(DnsServiceProfile.class, "serviceName", proName, domId);
		if (null == dnsObj) {
			DnsServiceProfile dto_Dns = new DnsServiceProfile();
			dto_Dns.setServiceName(proName);
			dto_Dns.setOwner(owner);
			dto_Dns.setServiceType(DnsServiceProfile.SAME_DNS);
			dto_Dns.setExternalServerType(DnsServiceProfile.LOCAL_DNS_TYPE);
			dto_Dns.setDescription(MgrUtil.getUserMessage("policy.predefined.description.dns.network"));
			try {
				Long newId = QueryUtil.createBo(dto_Dns);
				dnsObj = AhRestoreNewTools.CreateBoWithId(DnsServiceProfile.class, newId);
			} catch (Exception ex) {
				log.error("getVpnNetworkObj()", "insert pre-defined DNS service : "+ex.getMessage());
			}
		}
		
		VpnNetwork vpnNet = null;
		for (VpnNetwork netObj : list) {
			if (!BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_USERPROFILE.equals(netObj.getNetworkName())
					&& !deviceFreevalNetworkObjNames.contains(netObj.getNetworkName())) {
				vpnNet = netObj;
			}
		}
		if (null == vpnNet) {
			vpnNet = new VpnNetwork();
			//vpnNet.setNetworkName(proName);
			vpnNet.setNetworkName(BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT);
			vpnNet.setOwner(owner);
			vpnNet.setDescription(MgrUtil.getUserMessage("policy.predefined.description.network.mgt"));
//			vpnNet.setVlan(defVlan);
			vpnNet.setVpnDnsService(dnsObj);
			vpnNet.setEnableDhcp(false);
			vpnNet.setNetworkType(VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT);
			
			List<VpnNetworkSub> subItems = new ArrayList<VpnNetworkSub>();
			VpnNetworkSub subItem = new VpnNetworkSub();
			subItem.setKey(1);
			subItem.setIpNetwork("172.18.0.0/16");
			subItem.setLocalIpNetwork("172.18.0.0/16");
			subItem.setIpBranches(512);
			subItem.setEnableDhcp(true);
			subItems.add(subItem);
			vpnNet.setSubItems(subItems);
		}	else {
			vpnNet.setNetworkName(BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT);
		}

		
		if (null == vpnNet.getId()) {
			try {
				Long id = QueryUtil.createBo(vpnNet);
				VpnNetworksAction.prepareSubNetworkRes(id, null);
				return id;
			} catch (Exception ex) {
				log.error("getVpnNetworkObj()", "create new network error : "+ex.getMessage());
			}
		} else {
			try {
				QueryUtil.updateBo(vpnNet);
				return vpnNet.getId();
			} catch (Exception ex) {
				log.error("getVpnNetworkObj()", "update the exist network error : "+ex.getMessage());
			}
		}
		return null;
	}
	
	private static Long updateHive(String hiveName, Long domId, HmDomain owner) {
		// new hive name
		//String hiveName = getDataSource().getNetworkName();
		HiveProfile hive = null;
		if (!DeviceFreevalUtil.isDeviceFreevalDefinedInVHM(owner)) {
			hive = QueryUtil.findBoByAttribute(HiveProfile.class, "defaultFlag", false,
					domId);
		} else {
			hive = DeviceFreevalUtil.getFirstHiveProfileNotDeviceFreeval(owner, null);
		}
		if (null != hive) {
			if (!hive.getHiveName().equals(hiveName)) {
				// update hive object
				hive.setHiveName(hiveName);
				try {
					QueryUtil.updateBo(hive);	
				} catch (Exception ex) {
					log.error("updateHive()", "update error : "+ex.getMessage());
				}
			}
			return hive.getId();
		} else {
			// create hive object
			hive = new HiveProfile();
			hive.setHiveName(hiveName);
			hive.setHivePassword(MgrUtil.getRandomString(63,7));
			hive.setOwner(owner);
			hive.setDescription(MgrUtil.getUserMessage("policy.predefined.description.hive.domain"));
			try {
				return QueryUtil.createBo(hive);
			} catch (Exception ex) {
				log.error("updateHive()", "create error : "+ex.getMessage());
			}
		}
		return null;
	}

	private void initValues() {
		HmStartConfig startConf = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.id", domainId);
		if (null == startConf) {
			setSessionDataSource(new HmStartConfig());
			// set default network name
			if (null != getDomain() && !HmDomain.HOME_DOMAIN.equals(getDomain().getDomainName())){
				getDataSource().setNetworkName(getDomain().getDomainName());
			}
		} else {
			setSessionDataSource(startConf);
		}
		
		boolean showExpress = false;
		List<HmExpressModeEnable> settings = QueryUtil.executeQuery(HmExpressModeEnable.class,
				null, null);
		if (settings == null || settings.isEmpty()) {
			showExpress = NmsUtil.getOEMCustomer().getExpressModeEnable();
		} else {
			showExpress = settings.get(0).isExpressModeEnable();
		}
		
		if (isOEMSystem() && !showExpress){
			getDataSource().setModeType(HmStartConfig.HM_MODE_FULL);
		}
		
		if (HmStartConfig.HM_MODE_EASY == getDataSource().getModeType()) {
			// store the HiveAP password
			getDataSource().setOldHiveApPassword(getDataSource().getHiveApPassword());
			
			// set network admin time zone and ntp server
			MgmtServiceTime ntpService = QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", getDataSource().getNetworkName(), domainId, this);
			if (null != ntpService) {
				timezone = HmBeOsUtil.getServerTimeZoneIndex(ntpService.getTimeZoneStr());
				List<MgmtServiceTimeInfo> ntpInfo = ntpService.getTimeInfo();
				if (null != ntpInfo && !ntpInfo.isEmpty()) {
					ntpServer = ntpInfo.get(0).getIpAddress().getAddressName();
				}
			} else {
				timezone = HmBeOsUtil.getServerTimeZoneIndex("America/Los_Angeles");
			}
			
			// set dns server value
			MgmtServiceDns dnsService = QueryUtil.findBoByAttribute(MgmtServiceDns.class, "mgmtName", getDataSource().getNetworkName(), domainId, this);
			if (null != dnsService) {
				List<MgmtServiceDnsInfo> dnsInfo = dnsService.getDnsInfo();
				if (null != dnsInfo && !dnsInfo.isEmpty()) {
					dnsSer1 = dnsInfo.get(0).getIpAddress().getAddressName();
					if (dnsInfo.size() > 1) {
						dnsSer2 = dnsInfo.get(1).getIpAddress().getAddressName();
					} else {
						dnsSer2 = "";
					}
				}
			}
			
			// set access console assic key
			AccessConsole acObj = QueryUtil.findBoByAttribute(AccessConsole.class, "consoleName", getDataSource().getNetworkName(), domainId);
			if (null != acObj && getDataSource().isUseAccessConsole()) {
				getDataSource().setAsciiKey(acObj.getAsciiKey());
			}
		}
	}
	
//	public boolean getSwitchOperation()
//	{
//		HmStartConfig bo = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.id", getDomain().getId());
//		if (bo == null) {
//			return false;
//		}
//		
//		if (!bo.isAdminUserLogin()) {
//			return false;
//		}
//		
//		return true;
//	}
	
	public static void assignHiveApInfos(ConfigTemplate wlan, Long domainId) throws Exception{
		if(null == wlan){
			return;
		}
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null, null, domainId);
		log.info("assignHiveApInfos", "Update already existed HiveAPs for domain:"+domainId+", count:"+list.size());
		if(!list.isEmpty()){
			for(HiveAp hiveAp : list){
				hiveAp.setConfigTemplate(wlan);
//				if(null != configPassword && !"".equals(configPassword)){
//					hiveAp.setCfgAdminUser("admin");
//					hiveAp.setCfgPassword(configPassword);
//				}
			}
			QueryUtil.bulkUpdateBos(list);
			// if change the WLAN/HiveAP settings, show the warning message
			MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
		}
	}

	public static boolean isStartHereConfigured(Long domainId) {
		if (domainId==null) return false;
		HmStartConfig hmStartConfig= QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.id", domainId);
		if (hmStartConfig==null) return false;
//		if (!hmStartConfig.isAdminUserLogin()) {
//			return false;
//		}
		return true; 
	}

	public EnumItem[] getModeType1() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_EASY,
				MgrUtil.getEnumString("enum.hm.start.mode."
						+ HmStartConfig.HM_MODE_EASY)) };
	}

	public EnumItem[] getModeType2() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_FULL,
				MgrUtil.getEnumString("enum.hm.start.mode."
						+ HmStartConfig.HM_MODE_FULL)) };
	}
	
	public String getDisplayNtpSet() {
		return getDataSource().getModeType() == HmStartConfig.HM_MODE_FULL ? "none" : "";
	}

	public boolean getDisableMode() {
		return !getDomain().isSupportFullMode() || getDataSource().getModeType() == HmStartConfig.HM_MODE_FULL;
	}
	
	private String adminPassword;
	
	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	
	private int timezone;
	
	public int getTimezone()
	{
		return timezone;
	}

	public void setTimezone(int timezone)
	{
		this.timezone = timezone;
	}
	
	private String dnsSer1 = HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1;
	
	private String dnsSer2 = HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2;
	
	private String ntpServer = HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER;

	public String getNtpServer()
	{
		return ntpServer;
	}

	public void setNtpServer(String ntpServer)
	{
		this.ntpServer = ntpServer;
	}

	public String getDnsSer1()
	{
		return dnsSer1;
	}

	public void setDnsSer1(String dnsSer1)
	{
		this.dnsSer1 = dnsSer1;
	}

	public String getDnsSer2()
	{
		return dnsSer2;
	}

	public void setDnsSer2(String dnsSer2)
	{
		this.dnsSer2 = dnsSer2;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MgmtServiceDns) {
			MgmtServiceDns dns = (MgmtServiceDns)bo;

			if(dns.getDnsInfo() != null) {
				dns.getDnsInfo().size();
			}
		}

		if(bo instanceof MgmtServiceTime) {
			MgmtServiceTime time = (MgmtServiceTime)bo;

			if(time.getTimeInfo() != null) {
				time.getTimeInfo().size();
			}
		}
		
		if (bo instanceof HmUserGroup) {
			HmUserGroup profile = (HmUserGroup) bo;
			if (profile.getFeaturePermissions() != null) {
				profile.getFeaturePermissions().size();
			}
			if (profile.getInstancePermissions() != null) {
				profile.getInstancePermissions().size();
			}
		}

		return null;
	}
	
	private void setDefaultValue()
	{
		if(!isHMOnline()||(getUserContext().isVadAdmin()&&getUserContext().getSwitchDomain()==null)
				||(getUserContext().getDomain().isHomeDomain()&&getUserContext().getSwitchDomain()==null))
		{
			setHideAccessPannel(true);
		}
		else
		{
			HmDomain hmDomain;
			TimeZone timezone;
			if((getUserContext().getDomain().isHomeDomain()&&getUserContext().getSwitchDomain()!=null))
			{
				hmDomain=getUserContext().getSwitchDomain();
				timezone=hmDomain.getTimeZone();
			}
			else{
				hmDomain=QueryUtil.findBoById(HmDomain.class, domainId);
				timezone=getUserTimeZone();
			}
			// set the default access mode of current user
			defaultAccessMode=hmDomain.getAccessMode();
			
			if(defaultAccessMode==1||defaultAccessMode==2)
			{
				Long endDate=hmDomain.getAuthorizationEndDate();
				Long currentDate=new Date().getTime();
				//set the end time of temporary access
				if(endDate>currentDate)
				{
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
					sdf.setTimeZone(timezone);
					setEndTime(sdf.format(new Date(endDate)));
					
					//recaculate the left time of temporary access
					Long leftMilliSeconds=endDate-currentDate;
					Long oneHourInMilliSeconds=(long)60*60*1000;
					Long oneMinuteInMilliSeconds=(long)60*1000;
					leftHours=(int) (leftMilliSeconds/oneHourInMilliSeconds);
					if(leftMilliSeconds-leftHours*oneHourInMilliSeconds>0)
					{
					leftMinutes=(short)((leftMilliSeconds-leftHours*oneHourInMilliSeconds)/oneMinuteInMilliSeconds);
					}
					if(leftMilliSeconds-leftHours*oneHourInMilliSeconds-leftMinutes*oneMinuteInMilliSeconds>0)
					{
					leftSeconds=(short)((leftMilliSeconds-leftHours*oneHourInMilliSeconds-leftMinutes*oneMinuteInMilliSeconds)/(long)1000);
					}
					setDefaultAuthorizedTime(hmDomain.getAuthorizedTime());
				}
				else{
					//access permission is out of time,but the timer has not reset the config in db table yet
					setDefaultAccessMode((short)0);
				}
			}
				
			setAccessOption(SupportAccessUtil.ACCESS_OPTION_PREFIX+defaultAccessMode);
			
			//Free accounts (Planner, Demo, and Freeval),will have option 4 (read and write by default)
			//and  user has no way to change the option
			if(NmsUtil.isPlanner()||NmsUtil.isDemoHHM()
					||DeviceFreevalUtil.isHMForDeviceFreeval()||getUserContext().getSwitchDomain()!=null)
			{
				setWriteDisable4Access(true);
			}
			
	//        if (!isHMOnline()||(getUserContext().isVadAdmin()&&getUserContext().getSwitchDomain()== null ))
	//        {
	//              setHideAccessPannel( true );
	//        }
		}

	}
	
	private void saveAccessStatus() throws Exception
	{
		short accessMode=Short.valueOf(accessOption.substring(SupportAccessUtil.ACCESS_OPTION_PREFIX.length()));
		Long authorizationEndDate=-1L;
		try{
			
			HmDomain hmDomain=QueryUtil.findBoById(HmDomain.class, domainId);
			hmDomain.setAccessMode(accessMode);
			if(accessMode==1||accessMode==2)
			{
				switch(accessMode)
				{
				case 1:defaultAuthorizedTime=authorizedTime1;break;
				case 2:defaultAuthorizedTime=authorizedTime2;break;
				}
				//calculate the endDate of authorized access
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, defaultAuthorizedTime);
				authorizationEndDate=cal.getTimeInMillis();
			}
			hmDomain.setAuthorizedTime(defaultAuthorizedTime);
			hmDomain.setAuthorizationEndDate(authorizationEndDate);
			hmDomain.setAccessChanged(true);
			MgrUtil.setSessionAttribute("hm.domain.access.mode",accessMode);
			//save changes to db
				BoMgmt.getDomainMgmt().updateDomain(hmDomain);
				VhmInfo vhmInfo=new VhmInfo();
				vhmInfo.setAccessMode(accessMode);
				vhmInfo.setAuthorizationEndDate(authorizationEndDate);
				vhmInfo.setVhmName(hmDomain.getDomainName());
				RemotePortalOperationRequest.modifyVhmInfo(vhmInfo);
				sendMail(accessMode);
			} catch (Exception e) {
				log.error("StartHereAction-saveAccessStatus", "save HmDomain Object failed.", e);
				throw e;
			}
	}
	
	private void sendMail(short accessMode)
	{
		try{
		List<HmUser> userList=QueryUtil.executeQuery(HmUser.class,null,
				new FilterParams("owner.id=:s1 AND defaultflag=:s2",
						new Object[]{domainId,true}));
		if(null!=userList&&userList.size()>0)
		{
			EmailElement email = new EmailElement();
			String accessMessage=null;
			for(HmUser user:userList)
			{
				switch(accessMode)
				{
				case 1:accessMessage=MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+accessMode,String.valueOf(authorizedTime1))+" hours";break;
				case 2:accessMessage=MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+accessMode,String.valueOf(authorizedTime2))+" hours";break;
				default:accessMessage=MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+accessMode);
				}
				
				String title=MgrUtil.getUserMessage("info.home.start.here.email.title", getSessionUserContext().getDomain().getVhmID());
				String body=MgrUtil.getUserMessage("info.home.start.here.email.body", accessMessage);
				String subject=MgrUtil.getUserMessage("info.home.start.here.email.subject");
				
				StringBuilder mailContent=new StringBuilder();
				mailContent.append(title);
				mailContent.append(body);
				
				
				email.setMustBeSent(true);
				email.setMailContent(mailContent.toString());
				email.setSubject(subject);
				email.setToEmail(user.getEmailAddress());
				email.setContentType("text/html");
				email.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");
				HmBeAdminUtil.sendEmail(email);
			}
		}
		}
		catch (Exception e) {
			log.error("StartHereAction", "SendMail Exception", e);
		}
	}
	
	/**
	 * bug 25589 fix
	 * @return
	 */
	public static boolean updateAccessConsoleForQSWirelessOnlyNetworkPolicy(Long domId,String hiveName){
		// remove quick start policies
		// no pre-defined quick start wireless only network policy now 
		ConfigTemplate wlan = QueryUtil.findBoByAttribute(ConfigTemplate.class, "configName", hiveName,
				domId);
		if(null == wlan){
			return false;
		}
		
		if(null != hiveName && !"".equals(hiveName)){
			try {
				wlan.setAccessConsole(QueryUtil.findBoByAttribute(AccessConsole.class, "consoleName",  hiveName, domId));
				QueryUtil.updateBo(wlan);
				return true;
			} catch (Exception e) {
				log.error("updateAccessConsoleForQSWirelessOnlyNetworkPolicy()", "update QuickStart-Wireless-Only network policy error : "+e.getMessage());
			}
			
		}
		
		return false;
	}
	
}