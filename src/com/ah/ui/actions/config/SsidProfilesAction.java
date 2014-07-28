package com.ah.ui.actions.config;

/*
 * @author Fisher
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.event.BeQueryADInfoResultEvent;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.BeParaModuleDefImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IdsPolicySsidProfile;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.SsidSecurity;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.ConfigTemplateAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.ActiveDirectoryTool;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class SsidProfilesAction extends IDMSupportAction implements QueryBo {

	private static final String SESSION_DISCARD_JOIN_CREDENTIALS = "DiscardJoinCredentials";

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(SsidProfilesAction.class.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_SSID = 2;
	public static final int COLUMN_ACCESS_SECURITY = 3;
	public static final int COLUMN_USER_CATEGORY = 4;
	public static final int COLUMN_CWPUSE = 5;
	public static final int COLUMN_ENABLEDMACAUTH = 6;
	public static final int COLUMN_MAXCLIENT = 7;
	public static final int COLUMN_DESCRIPTION = 8;
	public static final int COLUMN_USED_IDM = 9;

	private Long configmdmId;
	public Long getConfigmdmId() {
		return configmdmId;
	}

	public void setConfigmdmId(Long configmdmId) {
		this.configmdmId = configmdmId;
	}
	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.ssid.head.ssidName";
			break;
		case COLUMN_SSID:
			code = "config.ssid.head.ssid";
			break;
		case COLUMN_ACCESS_SECURITY:
			code = "report.reportList.compliance.accessSecurity";
			break;
		case COLUMN_USER_CATEGORY:
			code = "config.ssid.userInfo.userCategory";
			break;
		case COLUMN_CWPUSE:
			code = "report.client.table.clientCWPUsed";
			break;
		case COLUMN_ENABLEDMACAUTH:
			code = "config.ssid.enabledMAC";
			break;
		case COLUMN_MAXCLIENT:
			code = "config.ssid.maxClient";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ssid.description";
			break;
        case COLUMN_USED_IDM:
            code = "config.ssid.column.idm";
            break;
		}

		return MgrUtil.getUserMessage(code);
	}

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		if (isEasyMode()){
			Collection<String> condition = new Vector<String>();
			condition.add(BeParaModule.DEFAULT_SSID_PROFILE_NAME);
			condition.add(BeParaModule.SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER);
			condition.add(BeParaModule.SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS);
			condition.add(BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY);
			condition.add(BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY);
			condition.add(BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK);
			filterParams = new FilterParams("ssidName not in(:s1)",new Object[]{condition});
		}
		try {
			if ("new".equals(operation)) {
				if(isEasyMode() && this.getExConfigGuideFeature() != null) {
					MgrUtil.removeSessionAttribute("lstTitle");
				}
				if (!setTitleAndCheckAccess(getText("config.title.ssid"))) {
					if (isFullMode()){
						return getLstForward();
					}
				}
				
				refreshIDMStatus();
				
				setTabId(0);
				setSessionDataSource(new SsidProfile());
				if (isEasyMode()) {
					getDataSource().setAccessMode(SsidProfile.ACCESS_MODE_8021X);
					getDataSource().setMgmtKey(SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X);
					getDataSource().setEncryption(SsidProfile.KEY_ENC_CCMP);
					getDataSource().setAuthentication(SsidProfile.KEY_AUT_EAP);
					
					MgrUtil.removeSessionAttribute(GUIDED_CONFIG_FORM_CHANGE);
				}
				if(isEasyMode()){
				    showHiveRadiusServerInfo(false);
				}
				prepareDependentObjects();
				prepareRateSetInfo();
				
				return returnResultKeyWord(INPUT,"ssidEx");
			} else if (("create" + getLstForward()).equals(operation)) {
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				
				if(noAvailableCWP4IDM()) {
				    return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (checkNameExists("ssidName", getDataSource().getSsidName())) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
						&& getDataSource().isEnablePpskSelfReg()){
					if (checkNameExists("ssid", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						// setTabId(0);
						return INPUT;
					}
					if (checkNameExists("ppskOpenSsid", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						// setTabId(0);
						return INPUT;
					}
					if (getDataSource().getSsidName().equalsIgnoreCase(getDataSource().getPpskOpenSsid())
							|| getDataSource().getSsid().equalsIgnoreCase(getDataSource().getPpskOpenSsid())){
						prepareDependentObjects();
						addActionError(MgrUtil.getUserMessage("error.hiveap.ppsk.sameName"));
						return INPUT;
					}
				}
				if (checkHiveNameExists("hiveName", getDataSource().getSsid())) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (isEasyMode()) {
					if (checkNameExists("ssid", getDataSource().getSsid())) {
						prepareDependentObjects();
						// setTabId(0);
						return returnResultKeyWord(INPUT,"ssidEx");
					}
				}
				if (!checkUserProfileSize()) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					// setTabId(2);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				// one SSID profile cannot support more than 1024 PSK users
				if (!checkPskUserSize()) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (isEasyMode()){
					ConfigTemplate defaultTemplate = HmBeParaUtil.getEasyModeDefaultTemplate(domainId);
					defaultTemplate = findBoById(
							ConfigTemplate.class, defaultTemplate.getId(), this);
					ConfigTemplateSsid tmpConfigSsid = new ConfigTemplateSsid();
					tmpConfigSsid.setInterfaceName(getDataSource().getSsidName());
					Long tmpId = (long) 1;
					getDataSource().setId(tmpId);
					tmpConfigSsid.setSsidProfile(getDataSource());
					defaultTemplate.getSsidInterfaces().put(getDataSource().getId(), tmpConfigSsid);

					if (!checkRadioModeSize(defaultTemplate)){
						addActionError(strErrorMessage);
						strErrorMessage = "";
						prepareDependentObjects();
						getDataSource().setId(null);
						return returnResultKeyWord(INPUT,"ssidEx");
					}
					if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK){
						long count = ConfigTemplateAction.getTotalPmkUserSize(defaultTemplate);
						long newCount=0;
						if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
							newCount = getDataSource().getUserNumberPsk(); 
						} else {
							if (getDataSource().getUserPskMethod() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
								newCount = getDataSource().getUserNumberPsk(); 
							}
						}
						if (count+newCount > LocalUser.MAX_COUNT_AP30_USERPERAP) {
							addActionError(getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERPERAP)}));
							prepareDependentObjects();
							getDataSource().setId(null);
							return returnResultKeyWord(INPUT,"ssidEx");
						}
						
						long totalPskCount=ConfigTemplateAction.getTotalPSKUserSize(defaultTemplate);
						if (totalPskCount+newCount > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
							addActionError(getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP)}));
							prepareDependentObjects();
							getDataSource().setId(null);
							return returnResultKeyWord(INPUT,"ssidEx");
						}
						
					}

					getDataSource().setId(null);
					getDataSource().setUserProfileDefault(null);
					getDataSource().setUserProfileSelfReg(null);
					getDataSource().setLocalUserGroups(new HashSet<LocalUserGroup>());
					getDataSource().setRadiusAssignment(null);
					
					if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
						getDataSource().setEnabledwmm(false);
					}
					
					if (!functionExpressModeUpdateOrCreate()) {
						addActionError(MgrUtil.getUserMessage("action.error.create.update.profile.for.express.mode"));
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidEx");
					}
					
					// update DNS server for the AD integration
					updateDNSServerConfigInADIntegration(defaultTemplate);
					
//					getDataSource().setId(tmpId);
//					tmpConfigSsid.setSsidProfile(getDataSource());
//					defaultTemplate.getSsidInterfaces().put(getDataSource().getId(), tmpConfigSsid);
//					if (!checkCacAirTime(defaultTemplate)) {
//						addActionError(strErrorMessage);
//						strErrorMessage = "";
//						getDataSource().setId(null);
//						prepareDependentObjects();
//						return INPUT;
//					}
//					getDataSource().setId(null);
					Long newCreateId = createBo(dataSource);
					if (isEasyMode()){
						removeUnusedProfile();
					}
					SsidProfile newCreateSsid = findBoById(SsidProfile.class, newCreateId);
					tmpConfigSsid.setSsidProfile(newCreateSsid);
					defaultTemplate.getSsidInterfaces().remove(tmpId);
					defaultTemplate.getSsidInterfaces().put(newCreateId, tmpConfigSsid);
					Date oldVerD = defaultTemplate.getVersion();
					defaultTemplate = QueryUtil.updateBo(defaultTemplate);
					// generate an event to configuration indication process
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							defaultTemplate, ConfigurationChangedEvent.Operation.UPDATE,
							oldVerD, true));
					if (("create").equals(operation)){
						MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
						return returnPrepareBoList("create");
					} else {
						id =newCreateId;
						setUpdateContext(true);
						return getLstForward();
					}
				}
				if (("create").equals(operation)){
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				if (!StringUtils.isBlank(this.getManualLstForward())) {
					this.addLstForward(this.getManualLstForward());
				}
				String returnWord = null;
				if(null == id){
					// For the config-guided, get the data source from session when add a new SSID
					getSessionDataSource();
				} else{
					if (getExConfigGuideFeature() != null && null != dataSource && null != dataSource.getId() && dataSource.getId().compareTo(id) == 0){
						// For the config-guided, if page redirect back to the current edit SSID get data from session  
						getSessionDataSource();
					}else
						returnWord = editBo(this);
				}
				
				refreshIDMStatus();
				
				if (dataSource != null) {
					prepareEditSimpleModeRadiusValue();
					prepareDependentObjects();
					prepareRateSetInfo();
				}
				if(this.getExConfigGuideFeature() != null) {
					MgrUtil.removeSessionAttribute(GUIDED_CONFIG_FORM_CHANGE);
					
					if (getDataSource().getId()!=null) {
						setRemoveAllLstTitle(true);
						addLstTitle(getText("config.title.ssid.edit") + " '" + getChangedSsidName() + "'");
						setRemoveAllLstTitle(false);
					} else {
						setRemoveAllLstTitle(true);
						addLstTitle(getText("config.title.ssid"));
						setRemoveAllLstTitle(false);
					}
					
					return "ssidEx";
				} else {
					addLstTitle(getText("config.title.ssid.edit") + " '" + getChangedSsidName() + "'");
					return getReturnPathWithFullMode(returnWord, "configGuide2");
				}
			} else if ("clone".equals(operation)) {
				// setTabId(0);
				long cloneId = getSelectedIds().get(0);
				SsidProfile profile = (SsidProfile) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				setCloneFields(profile, profile);
				prepareEditSimpleModeRadiusValue();
				profile.setSsidName("");
				setSessionDataSource(profile);
				prepareDependentObjects();
				prepareRateSetInfo();
				if (isEasyMode()){
					getDataSource().setShowExpressUserAccess(true);
					getDataSource().setUserProfileDefault(null);
					getDataSource().setUserProfileSelfReg(null);
					getDataSource().setLocalUserGroups(new HashSet<LocalUserGroup>());
					getDataSource().setRadiusAssignment(null);
				}
				addLstTitle(getText("config.title.ssid"));
				return INPUT;
			} else if ("saveNewRadiusSetting".equals(operation)){
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				setNewRadiusPanelValue(true);
				if (checkNameExists("radiusName", getDataSource().getNewRadiusName(),RadiusAssignment.class)) {
					prepareDependentObjects();
					setTabId(5);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL){
					if (checkNameExists("radiusName", getDataSource().getNewRadiusName(),RadiusOnHiveap.class)) {
						prepareDependentObjects();
						setTabId(5);
						return returnResultKeyWord(INPUT,"ssidEx");
					}
				}
				RadiusAssignment createRadius = new RadiusAssignment();
				createRadius.setRadiusName(getDataSource().getNewRadiusName());
				RadiusServer addRadiusServer = new RadiusServer();
				addRadiusServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_PRIMARY);
				if (getDataSource().getNewRadiusType() == Cwp.CWP_EXTERNAL){
					addRadiusServer.setSharedSecret(getDataSource().getNewRadiusSecret());
				}

				addRadiusServer.setIpAddress(findBoById(IpAddress.class,
						getDataSource().getNewRadiusPrimaryIp()));
				createRadius.getServices().add(addRadiusServer);
				
				
				if (getDataSource().getNewRadiusSecondaryIp()!=null 
						&& getDataSource().getNewRadiusSecondaryIp()>0){
					RadiusServer secRadiusServer = new RadiusServer();
					secRadiusServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_BACKUP1);
					if (getDataSource().getNewRadiusType() == Cwp.CWP_EXTERNAL){
						secRadiusServer.setSharedSecret(getDataSource().getNewRadiusSecondSecret());
					}
					secRadiusServer.setIpAddress(findBoById(IpAddress.class,
							getDataSource().getNewRadiusSecondaryIp()));
					createRadius.getServices().add(secRadiusServer);
				}
				
				Long newRadiusId = createBo(createRadius);
				getDataSource().setRadiusAssignment(
						findBoById(RadiusAssignment.class, newRadiusId));

				// create new radius on hiveap profile
				if (getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL){
					RadiusOnHiveap radiusPro = new RadiusOnHiveap();
					radiusPro.setRadiusName(getDataSource().getNewRadiusName());
					Set<LocalUserGroup> userGroups = new HashSet<LocalUserGroup>();
					if (hiveApLocalUserGroupIds != null) {
						for (Long filterId : hiveApLocalUserGroupIds) {
							LocalUserGroup localUserGroup = findBoById(
									LocalUserGroup.class, filterId);
							if (localUserGroup != null) {
								userGroups.add(localUserGroup);
							}
						}
					}
					radiusPro.setLocalUserGroup(userGroups);
					createBo(radiusPro);
				}

				prepareDependentObjects();
				setTabId(0);
				return returnResultKeyWord(INPUT,"ssidEx");
			} else if ("saveSelfNewUserProfileSetting".equals(operation)){
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				setNewSelfUserProfilePanelValue(true);
				if (checkNameExists("userProfileName", 
						getDataSource().getNewSelfUserProfileName(),UserProfile.class)) {
					prepareDependentObjects();
					setTabId(6);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				UserProfile createUserProfile = new UserProfile();
				createUserProfile.setUserProfileName(getDataSource().getNewSelfUserProfileName());
				createUserProfile.setAttributeValue(getDataSource().getNewSelfAttributeValue());
				createUserProfile.setBlnUserManager(getDataSource().getNewSelfBlnUserManager());
				createUserProfile.setVlan(findBoById(Vlan.class, getDataSource().getNewSelfVlanId()));
				createUserProfile.setQosRateControl(HmBeParaUtil.getDefaultProfile(QosRateControl.class,null));
				Long newUserProfileId = createBo(createUserProfile);
				getDataSource().setUserProfileSelfReg(
						findBoById(UserProfile.class, newUserProfileId));

				prepareDependentObjects();
				setTabId(0);
				return returnResultKeyWord(INPUT,"ssidEx");
			} else if ("saveDefaultNewUserProfileSetting".equals(operation)){
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				setNewDefaultUserProfilePanelValue(true);
				if (checkNameExists("userProfileName", 
						getDataSource().getNewDefaultUserProfileName(),UserProfile.class)) {
					prepareDependentObjects();
					setTabId(7);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				UserProfile createUserProfile = new UserProfile();
				createUserProfile.setUserProfileName(getDataSource().getNewDefaultUserProfileName());
				createUserProfile.setAttributeValue(getDataSource().getNewDefaultAttributeValue());
				createUserProfile.setBlnUserManager(getDataSource().getNewDefaultBlnUserManager());
				createUserProfile.setVlan(findBoById(Vlan.class, getDataSource().getNewDefaultVlanId()));
				createUserProfile.setQosRateControl(HmBeParaUtil.getDefaultProfile(QosRateControl.class,null));
				Long newUserProfileId = createBo(createUserProfile);
				getDataSource().setUserProfileDefault(
						findBoById(UserProfile.class, newUserProfileId));

				prepareDependentObjects();
				setTabId(0);
				return returnResultKeyWord(INPUT,"ssidEx");
			} else if ("saveOptionNewUserProfileSetting".equals(operation)){
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				setNewOptionUserProfilePanelValue(true);
				if (checkNameExists("userProfileName", 
						getDataSource().getNewOptionUserProfileName(),UserProfile.class)) {
					prepareDependentObjects();
					setTabId(6);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				UserProfile createUserProfile = new UserProfile();
				createUserProfile.setUserProfileName(getDataSource().getNewOptionUserProfileName());
				createUserProfile.setAttributeValue(getDataSource().getNewOptionAttributeValue());
				createUserProfile.setBlnUserManager(getDataSource().getNewOptionBlnUserManager());
				createUserProfile.setVlan(findBoById(Vlan.class, getDataSource().getNewOptionVlanId()));
				createUserProfile.setQosRateControl(HmBeParaUtil.getDefaultProfile(QosRateControl.class,null));
				createBo(createUserProfile);
				prepareDependentObjects();
				setTabId(0);
				return returnResultKeyWord(INPUT,"ssidEx");
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					if (dosId != null) {
						if (selectDosType != null) {
							if (selectDosType.equals("mac")) {
								ssidDos = dosId;
							}
							if (selectDosType.equals("station")) {
								stationDos = dosId;
							}
							if("configmdm".equals(selectDosType)){
								configmdmId= dosId;
							}
						}
					}
					selectUserProfiles = (List<Long>) MgrUtil
							.getSessionAttribute("SELECT_RADIUS_USERPROFILE");
					macFilters = (List<Long>) MgrUtil.getSessionAttribute("SELECT_MACFILTER");
					schedulers = (List<Long>) MgrUtil.getSessionAttribute("SELECT_SCHEDULE");
					localUserGroupIds = (List<Long>) MgrUtil
							.getSessionAttribute("SELECT_LOCALUSERGROUP");
					prepareSetSaveObjects();
					if (newRadiusPrimaryIp!=null){
						getDataSource().setNewRadiusPrimaryIp(newRadiusPrimaryIp);
					}
					if (newRadiusSecondaryIp!=null){
						getDataSource().setNewRadiusSecondaryIp(newRadiusSecondaryIp);
					}
					
					if (newSelfVlanId!=null) {
						getDataSource().setNewSelfVlanId(newSelfVlanId);
					}
					if (newDefaultVlanId!=null) {
						getDataSource().setNewDefaultVlanId(newDefaultVlanId);
					}
					if (newOptionVlanId!=null) {
						getDataSource().setNewOptionVlanId(newOptionVlanId);
					}
					if (simpleModeUserProfileId != null) {
						getDataSource().setShowExpressUserAccess(false);
						getDataSource().setUserVlan(null);
						if (getDataSource().getCwp()!=null 
								&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
							getDataSource().setUserProfileSelfReg(
									findBoById(UserProfile.class, simpleModeUserProfileId, this));
							getDataSource().setBlnUserManager(getDataSource().getUserProfileSelfReg().getBlnUserManager());
							getDataSource().setUserProfileDefault(null);
						} else {
							getDataSource().setUserProfileDefault(
									findBoById(UserProfile.class, simpleModeUserProfileId, this));
							getDataSource().setBlnUserManager(getDataSource().getUserProfileDefault().getBlnUserManager());
							getDataSource().setUserProfileSelfReg(null);
						}
					}
					
					if (null != MgrUtil.getSessionAttribute("SELECT_DEVICEGROUP_CHECKBOX")) {
						enableOsDection = (Boolean)MgrUtil.getSessionAttribute("SELECT_DEVICEGROUP_CHECKBOX");
					}
					
					prepareDependentObjects();
					prepareRateSetInfo();
					setId(dataSource.getId());
					setTabId(getLstTabId());
					if (getUpdateContext()) {
						removeLstTitle();
						MgrUtil.setSessionAttribute("CURRENT_TABID", getTabId());
						removeLstTabId();
						removeLstForward();
						setUpdateContext(false);
					} else {
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute("CURRENT_TABID")
								.toString()));
					}
					if (this.getLastExConfigGuide() != null) {
						if("cwp".equals(exBackFrom)) {
							return "ssidEx";
						}
						
						if (simpleModeUserProfileId!=null || isBackFromUser()) {
							return "ssidEx";
						} else {
							return "guidedConfiguration";
						}
					} else {
						return INPUT;
					}
				}
			} else if (("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSetSaveObjects();
					prepareSetSsidSecurity();
					updateRateSetInfo();
				}
				if(noAvailableCWP4IDM()) {
				    return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (checkHiveNameExists("hiveName", getDataSource().getSsid())) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (isEasyMode()) {
					if (checkSsidExists("ssid", getDataSource().getSsid())) {
						prepareDependentObjects();
						// setTabId(0);
						return returnResultKeyWord(INPUT,"ssidEx");
					}
				}
				if (!checkUserProfileSize()) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					// setTabId(2);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				// one SSID profile cannot support more than 1024 PSK users
				if (!checkPskUserSize()) {
					prepareDependentObjects();
					// setTabId(0);
					return returnResultKeyWord(INPUT,"ssidEx");
				}
				if (isFullMode()){
					if (!checkRelativedTemplate(getDataSource())) {
						prepareDependentObjects();
						// setTabId(0);
						return INPUT;
					}
					
					if (getReferenceWlanPolicy(getDataSource().getId())) {
						for (CheckItem wlanpolicy : referenceWLAN) {
							ConfigTemplate configTemplate = QueryUtil.findBoById(
									ConfigTemplate.class, wlanpolicy.getId(), this);
							if (!checkAllCloseUpdate(resetConfigTemplateQos(configTemplate,getDataSource()))) {
								addActionError(MgrUtil.getUserMessage("action.error.newwork.policy.update.ssid" ,
										new String[]{wlanpolicy.getValue(),getDataSource().getSsidName()})	+ " " + strErrorMessage);
								strErrorMessage = "";
								prepareDependentObjects();
								return INPUT;
							}
						}
					}
				} else {
					ConfigTemplate defaultTemplate = HmBeParaUtil.getEasyModeDefaultTemplate(domainId);
					defaultTemplate = findBoById(
							ConfigTemplate.class, defaultTemplate.getId(), this);
					ConfigTemplateSsid tmpConfigSsid = new ConfigTemplateSsid();
					tmpConfigSsid.setInterfaceName(getDataSource().getSsidName());
					tmpConfigSsid.setSsidProfile(getDataSource());
					defaultTemplate.getSsidInterfaces().put(getDataSource().getId(), tmpConfigSsid);
					
					if (!checkRadioModeSize(defaultTemplate)){
						addActionError(strErrorMessage);
						strErrorMessage = "";
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidEx");
					}
					
					if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK){
						defaultTemplate.getSsidInterfaces().remove(getDataSource().getId());
						long count = ConfigTemplateAction.getTotalPmkUserSize(defaultTemplate);
						long newCount=0;
						if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
							newCount = getDataSource().getUserNumberPsk(); 
						} else {
							if (getDataSource().getUserPskMethod() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
								newCount = getDataSource().getUserNumberPsk(); 
							} 
						}
						if (count+newCount > LocalUser.MAX_COUNT_AP30_USERPERAP) {
							addActionError(getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERPERAP)}));
							prepareDependentObjects();
							return returnResultKeyWord(INPUT,"ssidEx");
						}
						
						long totalPskCount=ConfigTemplateAction.getTotalPSKUserSize(defaultTemplate);
						if (totalPskCount+newCount > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
							addActionError(getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP)}));
							prepareDependentObjects();
							return returnResultKeyWord(INPUT,"ssidEx");
						}
					}
					
					if (!functionExpressModeUpdateOrCreate()) {
						addActionError(MgrUtil.getUserMessage("action.error.create.update.profile.for.express.mode"));
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidEx");
					}
					
					// update DNS server for the AD integration
					updateDNSServerConfigInADIntegration(defaultTemplate);
					
//					tmpConfigSsid.setSsidProfile(getDataSource());
//					defaultTemplate.getSsidInterfaces().put(getDataSource().getId(), tmpConfigSsid);
//					if (!checkCacAirTime(defaultTemplate)) {
//						addActionError(strErrorMessage);
//						strErrorMessage = "";
//						prepareDependentObjects();
//						return INPUT;
//					}
				}
				if ("update".equals(operation)) {
					updateBo(dataSource);
					if (isEasyMode()){
						removeUnusedProfile();
						MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
					}
					return returnPrepareBoList("update");
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForwardAndRemove();
				}
			} else if ("newSsidDos".equals(operation) || "newMacDos".equals(operation)
					|| "newIpDos".equals(operation) || "newMacFilter".equals(operation)
					|| "newServiceFilter".equals(operation) || "newUserPolicy".equals(operation)
					|| "newScheduler".equals(operation) || "newCwp".equals(operation)
					|| "newRadius".equals(operation) || "newLocalUserGroup".equals(operation)
					|| "newUserProfileSelfReg".equals(operation)
					|| "newUserProfileDefault".equals(operation)
					|| "newUserProfileRadiusMore".equals(operation) || "editSsidDos".equals(operation)
					|| "editMacDos".equals(operation) || "editIpDos".equals(operation)
					|| "editMacFilter".equals(operation) || "editServiceFilter".equals(operation)
					|| "editUserPolicy".equals(operation) || "editScheduler".equals(operation)
					|| "editCwp".equals(operation) || "editRadius".equals(operation)
					|| "editLocalUserGroup".equals(operation)
					|| "editLocalUserGroupForRadius".equals(operation)
					|| "newLocalUserGroupForRadius".equals(operation)
					|| "editUserProfileSelfReg".equals(operation)
					|| "editUserProfileDefault".equals(operation)
					|| "editUserProfileRadius".equals(operation)
					|| "newAsRuleGroup".equals(operation) || "editAsRuleGroup".equals(operation)
					|| "newIpAddress".equals(operation) || "editIpAddress".equals(operation)
					|| "newIpAddressSec".equals(operation) || "editIpAddressSec".equals(operation)
					|| "newSelfVlan".equals(operation) || "editSelfVlan".equals(operation)
					|| "newDefaultVlan".equals(operation) || "editDefaultVlan".equals(operation)
					|| "newOptionVlan".equals(operation) || "editOptionVlan".equals(operation)
					|| "editSimpleModeUserProfile".equals(operation)
					|| "newDevicePolicy".equals(operation) || "editDevicePolicy".equals(operation) 
					|| "newPpskECwp".equals(operation) || "editPpskECwp".equals(operation)
					|| "newUserOperator".equals(operation)
					|| "newRadiusPpsk".equals(operation) || "editRadiusPpsk".equals(operation)||"newConfigmdmPolicy".equals(operation)||"editConfigmdmPolicy".equals(operation)){
				if (getExConfigGuideFeature() != null) {
					MgrUtil.setSessionAttribute(GUIDED_CONFIG_FORM_CHANGE, new Object());
				}
				prepareSetSsidSecurity();
				if (isFullMode()){
					setNewRadiusPanelValue(false);
					setNewSelfUserProfilePanelValue(false);
					setNewDefaultUserProfilePanelValue(false);
					setNewOptionUserProfilePanelValue(false);
				}
 				prepareSetSaveObjects();
 				prepareSetSimpleModeUserProfile(operation);
				updateRateSetInfo();
				clearErrorsAndMessages();
				if ("newUserProfileSelfReg".equals(operation)
						|| "editUserProfileSelfReg".equals(operation)) {
					addLstForward("ssidUserProfileSelfReg");
				} else if ("newUserProfileDefault".equals(operation)
						|| "editUserProfileDefault".equals(operation)) {
					addLstForward("ssidUserProfileDefault");
				} else if ("editSimpleModeUserProfile".equals(operation)) {
					addLstForward("simpleModeUserProfile");
				} else if ("newUserPolicy".equals(operation) || "editUserPolicy".equals(operation)
						|| "newIpAddressSec".equals(operation) || "editIpAddressSec".equals(operation)
						|| "newDefaultVlan".equals(operation) || "editDefaultVlan".equals(operation)
						|| "newRadiusPpsk".equals(operation) || "editRadiusPpsk".equals(operation)) {
					addLstForward("ssid2");
				} else if ("newOptionVlan".equals(operation) || "editOptionVlan".equals(operation)){
					addLstForward("ssid3");
				} else {
					addLstForward("ssid");
				}
				MgrUtil.setSessionAttribute("SELECT_RADIUS_USERPROFILE", selectUserProfiles);
				MgrUtil.setSessionAttribute("SELECT_MACFILTER", macFilters);
				MgrUtil.setSessionAttribute("SELECT_SCHEDULE", schedulers);
				MgrUtil.setSessionAttribute("SELECT_LOCALUSERGROUP", localUserGroupIds);
				MgrUtil.setSessionAttribute("SELECT_DEVICEGROUP_CHECKBOX", enableOsDection);

				if ("newSsidDos".equals(operation) || "editSsidDos".equals(operation)
						|| "newMacDos".equals(operation) || "editMacDos".equals(operation)
						|| "newIpDos".equals(operation) || "editIpDos".equals(operation)
						|| "newMacFilter".equals(operation) || "editMacFilter".equals(operation)
						|| "newServiceFilter".equals(operation)
						|| "editServiceFilter".equals(operation)) {
					setTabId(2);
				} else if ("newScheduler".equals(operation) || "editScheduler".equals(operation)) {
					setTabId(3);
				} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)
						|| "newIpAddressSec".equals(operation) || "editIpAddressSec".equals(operation)
						|| "editLocalUserGroupForRadius".equals(operation)
						|| "newLocalUserGroupForRadius".equals(operation)) {
					// new Radius panel
					setTabId(5);
				} else if ("newSelfVlan".equals(operation) || "editSelfVlan".equals(operation)){
					// new self user profile panel
					setTabId(6);
				} else if ("newDefaultVlan".equals(operation) || "editDefaultVlan".equals(operation)){
					// new Default user profile panel
					setTabId(7);
				} else if ("newOptionVlan".equals(operation) || "editOptionVlan".equals(operation)){
					// new option user profile panel
					setTabId(8);
				} else if("newConfigmdmPolicy".equals(operation)||"editConfigmdmPolicy".equals(operation)){
					setTabId(4);
				}
				else {
					setTabId(0);
				}
				addLstTabId(tabId);
				return operation;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (isEasyMode()&& getDataSource().getId()==null){
					if (!getDataSource().getSsidName().equals("")){
						getDataSource().setUserProfileDefault(null);
						getDataSource().setUserProfileSelfReg(null);
						try {
							QueryUtil.removeBos(UserProfile.class, 
								new FilterParams("userProfileName =:s1 and defaultFlag=:s2",
								new Object[]{getDataSource().getSsidName(),false}),getUserContext(),null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForwardAndRemove();
				} else {
					if (this.getLastExConfigGuide() != null) {
						setUpdateContext(false);
						if (getDataSource().getId()!=null) {
							setSessionDataSource(findBoById(boClass, getDataSource().getId(), this));
						} else {
							setSessionDataSource(null);
						}
						MgrUtil.removeSessionAttribute("lstTitle");
						MgrUtil.removeSessionAttribute("lstTabId");
						MgrUtil.removeSessionAttribute("lstForward");
						MgrUtil.removeSessionAttribute("lstFormChanged");
						return "guidedConfiguration";
					}
					baseOperation();
					return prepareBoList();
				}
			} else if ("changeCwpAuthOperation".equals(operation)) {
				// 1: all null
				// 2: self-reg
				// 3: open wep
				// 4: auth
				// 5: both
				jsonObject = new JSONObject();
				jsonObject.put("f", 0);
				if (cwpId < 0) {
					if (blnMacAuth || keyManagement == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
							|| keyManagement == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
							|| keyManagement == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
							|| keyManagement == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
						jsonObject.put("v", 4);
					} else {
						jsonObject.put("v", 3);
					}
				} else {
					Cwp cwpProfile = QueryUtil.findBoById(Cwp.class, cwpId);
					if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED 
							|| cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
						jsonObject.put("v", 4);
						if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
							if (blnMacAuth && keyManagement == SsidProfile.KEY_MGMT_OPEN) {
								String ecwpServer = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION, 
										ConfigUtil.KEY_ECWPSERVER, ConfigUtil.VALUE_ECWP_DEFAULT);
								if (ecwpServer.equals(ConfigUtil.VALUE_ECWP_DEPAUL) 
										|| ecwpServer.equals(ConfigUtil.VALUE_ECWP_NNU)){
									jsonObject.put("f", 1);
								}
							}
//							if (!blnMacAuth
//									&& keyManagement != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
//									&& keyManagement != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
//									&& keyManagement != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
//									&& keyManagement != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
//								if (cwpProfile.isPpskServer()) {
//									jsonObject.put("v", 3);
//								}
//							}
						}
						
					} else if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
						if (blnMacAuth
								|| keyManagement == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
								|| keyManagement == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
								|| keyManagement == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
								|| keyManagement == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
							jsonObject.put("v", 5);
						} else {
							jsonObject.put("v", 2);
						}
					} else if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
						if (blnMacAuth) {
							jsonObject.put("v", 4);
						} else {
							jsonObject.put("v", 3);
						}
					} else {
						jsonObject.put("v", 5);
					}
				}
				return "json";
			} else if ("removeSSID".equals(operation)) {
				operation = "remove";
				Set<Long> boIds = new HashSet<Long>();
				boIds.add(id);
				MgrUtil.setSessionAttribute("express-remove-ssidid",id);
				setAllSelectedIds(boIds);
				 if(removeOperation()) {
					 return "guidedConfiguration"; 
				 } else {
					 prepareEditSimpleModeRadiusValue();
					 prepareDependentObjects();
					 prepareRateSetInfo();
					 return "ssidEx";
				 }
			} else if("changePrimaryHiveAP".equals(operation)){
			    jsonObject = new JSONObject();
			    if(StringUtils.isNotBlank(primaryHiveApRadius)){
			        HiveAp queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", primaryHiveApRadius,getDomain().getId());
			        
			        if(null!=queryHiveAP){
			        	jsonObject.put("dhcp", queryHiveAP.isDhcp());
			        	
			            jsonObject.put("ipAddress", queryHiveAP.getCfgIpAddress());
			            jsonObject.put("netmask", queryHiveAP.getCfgNetmask());
			            jsonObject.put("gateway", queryHiveAP.getCfgGateway());
			            jsonObject.put("macAddress", queryHiveAP.getMacAddress());
			            
			            // Remove the checking so APs in NEW state will also be included in the dropdown list
			            boolean isNewStateAP = queryHiveAP.getManageStatus() == HiveAp.STATUS_NEW;
						jsonObject.put("newState", isNewStateAP);
			            
			            // query the selected hiveAP get cfg-info
			            getDataSource().setStaticHiveAPIpAddress(queryHiveAP.getCfgIpAddress());
			            getDataSource().setStaticHiveAPNetmask(queryHiveAP.getCfgNetmask());
			            getDataSource().setStaticHiveAPGateway(queryHiveAP.getCfgGateway());
			            // get the DNS information
			            String dnsServer = getDNSServerInExpress();
						if(null != dnsServer) {
			            	getDataSource().setDnsServer(dnsServer);
			            	jsonObject.put("dnsServer", dnsServer);
			            }
			            
                        if (queryHiveAP.isDhcp() || isNewStateAP) {
                            jsonObject.put("enableADIntegration", false);
                            resetAdIntegrationValues();
                        } else
                            initActiveDirectory(primaryHiveApRadius, queryHiveAP.getMacAddress(), jsonObject);
			        }
			    }
			    return "json";
			}else if("retrieveADInfo".equals(operation)){
			    jsonObject = new JSONObject();
			    HiveAp queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", primaryHiveApRadius,getDomain().getId());
			    if(null!=queryHiveAP){
                    // retrieve information from Active Directory
					ActiveDirectoryTool.retrieveOperation(jsonObject, queryHiveAP.getMacAddress(),
							adDomainFullName, getDataSource().isPushedConfig());
                    
//                    jsonObject.put("resCode", "0");
//                    jsonObject.put("msg", "failed to retrieve information...");
//                    jsonObject.put("domainName", "aerohive-nms");
//                    jsonObject.put("adServer", "aerohive-nms.com");
//                    jsonObject.put("baseDN", "dc=aerohive-nms, dc=com");
                    
                    if(jsonObject.get("resCode")=="0"){
                        jsonObject.put("msg",MgrUtil.getUserMessage("info.config.retrieveAd.success"));
                    }
			    }
			    return "json";
			}else if("testjoinDomain".equals(operation)){
			    jsonObject = new JSONObject();
			    HiveAp queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", primaryHiveApRadius,getDomain().getId());
			    if(null!=queryHiveAP){
					// add for LDAP SASL wrapping
					String ldapSaslWrappingString = ActiveDirectoryOrOpenLdap.getLdapSaslWrappingString(ldapSaslWrapping);
					String computerOu = "";
					
			    	// join into the domain
			    	String[] args = new String[] {
			    			adDomainAdmin, adDomainAdminPasswd, 
			    			adDomainName,  adDomainFullName, 
			    			adServerIpAddress, baseDN, computerOu, ldapSaslWrappingString};
			        ActiveDirectoryTool.testAAAOperation(jsonObject,
			                queryHiveAP.getMacAddress(), ActiveDirectoryTool.TEST_TYPE_JOIN,
			                args);
			    	
			        if(discardJoinInfo == DiscardJoinInfo.YES.getValue()){
			        	MgrUtil.setSessionAttribute(SESSION_DISCARD_JOIN_CREDENTIALS, true);
			        }
			        
//			        jsonObject.put("resCode", "0");
//			        jsonObject.put("msg", "failed in test join");
			        
			        if(jsonObject.get("resCode")=="0"){
			            jsonObject.put("msg", MgrUtil.getUserMessage("info.config.testJoin.success",
			            		new String[]{primaryHiveApRadius, adDomainFullName}));
			        }
			    }
				return "json";
			}else if("testAuth".equals(operation)){
			    jsonObject = new JSONObject();
                HiveAp queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", primaryHiveApRadius, getDomain().getId());
                if (null != queryHiveAP) {
                	// test authority
        	    	String[] args = new String[] {
        	    			adDomainTestUser, adDomainTestUserPasswd, 
        	    			adDomainName, adDomainFullName, 
        	    			adServerIpAddress, baseDN};
                    ActiveDirectoryTool.testAAAOperation(jsonObject,
                            queryHiveAP.getMacAddress(), ActiveDirectoryTool.TEST_TYPE_AUTH,
                            args);
                    
//                    jsonObject.put("resCode", "0");
//                    jsonObject.put("msg", "failed in test auth");
                    
                    if(jsonObject.get("resCode")=="0"){
                        jsonObject.put("msg",MgrUtil.getUserMessage("info.config.testAuth.success"));
                    }
                }
				
				return "json";
			}else if("pushConfigToAp".equals(operation)){
				jsonObject = new JSONObject();
				// push the config to HiveAP
				log.debug("IP:"+getDataSource().getStaticHiveAPIpAddress()+"\ngateway:"+getDataSource().getStaticHiveAPGateway()
						+"\nnetmask:"+getDataSource().getStaticHiveAPNetmask()+"\nDNS:"+getDataSource().getDnsServer());
				HiveAp queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", primaryHiveApRadius, getDomain().getId());
				if(null != queryHiveAP) {
					String[] args = new String[]{queryHiveAP.getMacAddress(),
							getDataSource().getStaticHiveAPIpAddress(), getDataSource().getStaticHiveAPNetmask(),
							getDataSource().getStaticHiveAPGateway(), getDataSource().getDnsServer()};
					jsonObject = ActiveDirectoryTool.pushConfigToAp(jsonObject, domainId, args);
					
					if (ActiveDirectoryTool.RESULT_CODE_SUCCESS.equals(jsonObject.get("resCode").toString())) {
						getDataSource().setPushedConfig(true);
					} else {
						getDataSource().setPushedConfig(false);
					}
				}
				return "json";
			} else if ("changePpskEcwp".equals(operation)) {
				jsonObject = new JSONObject();
				if (getPpskECwpId()==null || getPpskECwpId() <=0) {
					jsonObject.put("t", false);
				} else {
					Cwp ppskEcwpBo = findBoById(Cwp.class, getPpskECwpId());
					if (ppskEcwpBo!=null && ppskEcwpBo.getPpskServerType()==Cwp.PPSK_SERVER_TYPE_AUTH) {
						jsonObject.put("t", true);
					} else {
						jsonObject.put("t", false);
					}
				}
				return "json";
			} else if ("completeCustomer".equals(operation)) {
                jsonObject = new JSONObject();
                completeCustomerInfo();
                return "json";
            } else if ("trialSettings".equals(operation)) {
                jsonObject = new JSONObject();
                prepareTrialSettings();
                return "json";
            } else if ("createIDMCustomer".equals(operation)) {
                jsonObject = new JSONObject();
                createIDMCustomer();
                return "json";
            } else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(e));
			try {
				return returnPrepareBoList(operation);
				//return prepareBoList();
			} catch (Exception ne) {
				return prepareEmptyBoList();
			}
		}
	}

    private boolean noAvailableCWP4IDM() throws Exception {
        boolean prompt = false;
        if(getDataSource().isEnabledIDM()) {
            if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
                if(!(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        /*|| getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
                        || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH*/)) {
                    addActionError(MgrUtil.getUserMessage("error.template.idm.cwp.auth", ""));
                    prepareDependentObjects();
                    prompt = true;
                }
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
                    && getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
                    && !(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                            /*|| getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
                            || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH*/)) {
                addActionError(MgrUtil.getUserMessage("error.template.idm.cwp.auth", ""));
                prepareDependentObjects();
                prompt = true;
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN
                    && !(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                    || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA)) {
                addActionError(MgrUtil.getUserMessage("error.template.idm.cwp.auth", "or \""
                    +MgrUtil.getEnumString("enum.idm.registrationType."+Cwp.REGISTRATION_TYPE_EULA)+"\""));
                prepareDependentObjects();
                prompt = true;
            }
        }
        return prompt;
    }
	//////// Begin: AD integration-add DNS server setting into express mode ///////////////////////
	private String getDNSServerInExpress() {
		// get DNS server
		MgmtServiceDns dnsService = QueryUtil.findBoByAttribute(
				MgmtServiceDns.class, "owner.id", getDomain().getId(), this);
		if(null != dnsService) {
			if(null != dnsService.getDnsInfo() && !dnsService.getDnsInfo().isEmpty()) {
				return dnsService.getDnsInfo().get(0).getIpAddress().getAddressName();
			}
		}
		return DEFAULT_DNS_SERVER;
	}

	private void updateDNSServerConfigInADIntegration(ConfigTemplate defaultTemplate)
			throws Exception {
		if(getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_8021X) {
			if(getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL) {
				
				if(null == defaultTemplate.getMgmtServiceDns()) {
					// new a DNS server
					Long dnsServerBindId;
					MgmtServiceDns dnsServer = QueryUtil.findBoByAttribute(
							MgmtServiceDns.class, "owner.id", getDomain().getId(), this);
					if(null == dnsServer) {
						dnsServerBindId = createDNSServer();
					} else {
						updateDNSServer(dnsServer);
						
						dnsServerBindId= dnsServer.getId();
					}
					// bind the DNS server to WLAN
					if (null != dnsServerBindId) {
						defaultTemplate.setMgmtServiceDns(QueryUtil.findBoById(MgmtServiceDns.class, dnsServerBindId, this));
					}
				} else {
					// update existed DNS server
					updateDNSServer(defaultTemplate.getMgmtServiceDns());
				}
			}
		}
	}

	private void updateDNSServer(MgmtServiceDns dnsServer) throws Exception {
		// get the primary DNS server and update IPAddress
		if(null == dnsServer.getDnsInfo() || dnsServer.getDnsInfo().isEmpty()) {
			List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();
			dnsInfo.add(getDNSServerInfo());
		} else {
			//FIXME This code block is not strictly, the DNS server priority should be defined!
			//In StartHereAction.java, its order is depended the implement or operation of the database 
			MgmtServiceDnsInfo dnsInfo = dnsServer.getDnsInfo().get(0);
			if(StringUtils.isNotBlank(getDataSource().getDnsServer())) {
			    IpAddress ipAddress = CreateObjectAuto.createNewIP(getDataSource().getDnsServer(),
			            IpAddress.TYPE_IP_ADDRESS, getDomain(), "For Express Primary DNS Assignment");
			    if(null != ipAddress) {
			        dnsInfo.setIpAddress(ipAddress);
			    }
			}
		}
		QueryUtil.updateBo(dnsServer);
	}

	private Long createDNSServer() throws Exception {
		Long dnsServerBindId;
		MgmtServiceDns dnsServer;
		dnsServer = new MgmtServiceDns();
		dnsServer.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
		dnsServer.setOwner(getDomain());
		dnsServer.setMgmtName(getDomain().getDomainName());
		
		List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();
		dnsInfo.add(getDNSServerInfo());
		
		dnsServer.setDnsInfo(dnsInfo);
		
		dnsServerBindId = QueryUtil.createBo(dnsServer);
		return dnsServerBindId;
	}

	private MgmtServiceDnsInfo getDNSServerInfo() {
		// create the ip address object
		IpAddress ipObj = CreateObjectAuto.createNewIP(getDataSource().getDnsServer(), 
				IpAddress.TYPE_IP_ADDRESS,
				getDomain(), "For Express Primary DNS Assignment");
		// create the dns server item
		MgmtServiceDnsInfo singleInfo = new MgmtServiceDnsInfo();
		singleInfo.setIpAddress(ipObj);
		singleInfo.setDnsDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s (Primary)");
		return singleInfo;
	}
	////////End: AD integration-add DNS server setting into express mode ///////////////////////
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if("ssid".equals(this.getLastExConfigGuide())) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	protected String returnPrepareBoList(String operation) throws Exception{
		if("ssid".equals(this.getLastExConfigGuide())) {
			setSessionDataSource(findBoById(boClass, getDataSource().getId(), this));
			prepareEditSimpleModeRadiusValue();
			prepareDependentObjects();
			prepareRateSetInfo();
			if ("create".equalsIgnoreCase(operation)) {
				return  "guidedConfiguration";
			} else {
				return  "ssidEx";
			}
		} else {
			return prepareBoList();
		}
	}
	
	@Override
	protected boolean removeOperation() throws Exception {
		if (!"remove".equals(operation)) {
			return false;
		}
		
		int count = -1;
		boolean hasRemoveDefaultValue=false;
		if (allItemsSelected) {
			setAllSelectedIds(null);
			this.getSessionFiltering();
			if (isEasyMode()){
				ConfigTemplate defaultTemplate = HmBeParaUtil.getEasyModeDefaultTemplate(domainId);
				defaultTemplate.setSsidInterfaces(new HashMap<Long, ConfigTemplateSsid>());
				Date oldVerD=defaultTemplate.getVersion();
				defaultTemplate = QueryUtil.updateBo(defaultTemplate);
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						defaultTemplate, ConfigurationChangedEvent.Operation.UPDATE,
						oldVerD, true));
				
				List<IdsPolicy> idsPolicyList = QueryUtil.executeQuery(IdsPolicy.class, null, null, domainId);
				if (!idsPolicyList.isEmpty()){
					IdsPolicy idsPolicy = idsPolicyList.get(0);
					if (idsPolicy.getIdsSsids()!=null){
						idsPolicy.setIdsSsids(new ArrayList<IdsPolicySsidProfile>());
						QueryUtil.updateBo(idsPolicy);
					}
				}
				
				List<QosClassification> qosClassiferList = QueryUtil.executeQuery(QosClassification.class, null, null, domainId);
				if (!qosClassiferList.isEmpty()){
					QosClassification qosClass = qosClassiferList.get(0);
					if (qosClass.getQosSsids()!=null){
						qosClass.getQosSsids().clear();
						QueryUtil.updateBo(qosClass);
					}
				}
			}
			if (getShowDomain()) {
				count = removeAllBos(boClass, filterParams,
						getNonHomeDataInHomeDomain());
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_NONHOME_DOMAIN_VALUE));
			} else {
				Collection<Long> defaultIds = getDefaultIds();
				count = removeAllBos(boClass, filterParams, defaultIds);
				if (null != defaultIds && !defaultIds.isEmpty()) {
					hasRemoveDefaultValue=true;
					addActionMessage(MgrUtil
							.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
				}
			}
			if (isEasyMode()){
				if (getShowDomain()) {
					try {
						QueryUtil.removeBos(UserProfile.class, null, getUserContext(),
								getNonHomeDataInHomeDomain(UserProfile.class));
						QueryUtil.removeBos(RadiusAssignment.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(RadiusAssignment.class));
						QueryUtil.updateBos(HiveAp.class, "radiusServerProfile = :s1",null,
								new Object[] { null});
						QueryUtil.removeBos(RadiusOnHiveap.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(RadiusOnHiveap.class));
						QueryUtil.removeBos(LocalUser.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(LocalUser.class));
						QueryUtil.removeBos(LocalUserGroup.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(LocalUserGroup.class));
						QueryUtil.removeBos(Scheduler.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(Scheduler.class));
						QueryUtil.removeBos(IpPolicy.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(IpPolicy.class));
						QueryUtil.removeBos(QosRateControl.class, null,getUserContext(),
								getNonHomeDataInHomeDomain(QosRateControl.class));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
					try {
						Collection<Long> defaultIds = getDefaultIds(UserProfile.class);
						QueryUtil.removeBos(UserProfile.class, null, getUserContext(),defaultIds);
						
						defaultIds = getDefaultIds(RadiusAssignment.class);
						QueryUtil.removeBos(RadiusAssignment.class, null, getUserContext(),defaultIds);
						
						QueryUtil.updateBos(HiveAp.class, "radiusServerProfile = :s1",null,
								new Object[] { null}, getDomain().getId());
						
						defaultIds = getDefaultIds(RadiusOnHiveap.class);
						QueryUtil.removeBos(RadiusOnHiveap.class, null, getUserContext(),defaultIds);
						
						//defaultIds = getDefaultIds(LocalUser.class);
						QueryUtil.removeBos(LocalUser.class, null, getUserContext(),null);
						
						defaultIds = getDefaultIds(LocalUserGroup.class);
						QueryUtil.removeBos(LocalUserGroup.class, null, getUserContext(),defaultIds);
						
						defaultIds = getDefaultIds(Scheduler.class);
						QueryUtil.removeBos(Scheduler.class, null, getUserContext(),defaultIds);
						
						defaultIds = getDefaultIds(IpPolicy.class);
						QueryUtil.removeBos(IpPolicy.class, null, getUserContext(),defaultIds);
						
						defaultIds = getDefaultIds(QosRateControl.class);
						QueryUtil.removeBos(QosRateControl.class, null, getUserContext(),defaultIds);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//remove client self register info by ssid name
			QueryUtil.bulkRemoveBos(AhClientEditValues.class,new FilterParams("type = :s1",
					new Object[] {AhClientEditValues.TYPE_SELF_REGISTER}),getDomain().getId());
			CacheMgmt.getInstance().removeClientEditValues(getDomain().getId(),AhClientEditValues.TYPE_SELF_REGISTER);
			
		} else if (getAllSelectedIds() != null && !getAllSelectedIds().isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && getAllSelectedIds().removeAll(defaultIds)) {
				hasRemoveDefaultValue=true;
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
			}
			Collection<Long> toRemoveIds = getAllSelectedIds();
			
			if (isEasyMode()){
				ConfigTemplate defaultTemplate = HmBeParaUtil.getEasyModeDefaultTemplate(domainId);
				defaultTemplate = findBoById(
						ConfigTemplate.class, defaultTemplate.getId(), this);
				Collection<Long> removeConfigList = new Vector<Long>();
				for(Long remId:toRemoveIds){
					for(Long tmpClass: defaultTemplate.getSsidInterfaces().keySet()){
						if (tmpClass.equals(remId)){
							removeConfigList.add(tmpClass);
						}
					}
				}
				for(Long removeId: removeConfigList){
					defaultTemplate.getSsidInterfaces().remove(removeId);
				}
				Date oldVerD=defaultTemplate.getVersion();
				defaultTemplate = QueryUtil.updateBo(defaultTemplate);
				
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						defaultTemplate, ConfigurationChangedEvent.Operation.UPDATE,
						oldVerD, true));
				
				List<IdsPolicy> idsPolicyList = QueryUtil.executeQuery(IdsPolicy.class, null, null, domainId);
				if (!idsPolicyList.isEmpty()){
					IdsPolicy idsPolicy = idsPolicyList.get(0);
					idsPolicy = findBoById(
							IdsPolicy.class, idsPolicy.getId(), this);
					Collection<IdsPolicySsidProfile> removeList = new Vector<IdsPolicySsidProfile>();
					for(Long remId:toRemoveIds){
						for(IdsPolicySsidProfile idsClass: idsPolicy.getIdsSsids()){
							if (idsClass.getSsidProfile().getId().equals(remId)){
								removeList.add(idsClass);
							}
						}
					}
					idsPolicy.getIdsSsids().removeAll(removeList);
					QueryUtil.updateBo(idsPolicy);
				}
				
				List<QosClassification> qosClassiferList = QueryUtil.executeQuery(QosClassification.class, null, null, domainId);
				if (!qosClassiferList.isEmpty()){
					QosClassification qosClass = qosClassiferList.get(0);
					qosClass = findBoById(
							QosClassification.class, qosClass.getId(), this);
					if (qosClass.getQosSsids()!=null){
						for(Long remId:toRemoveIds){
							qosClass.getQosSsids().remove(remId);
						}
						QueryUtil.updateBo(qosClass);
					}
				}
			}
			
			if (!checkUsedProfile(toRemoveIds)) {
				return false;
			}
			setAllSelectedIds(null);
			count = removeBos(boClass, toRemoveIds);
		}
		if (count > 0 && isEasyMode()) {
			MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
		}
		
		log.info("removeOperation", "Count: " + count);
		
		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else if (count == 1) {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED_WITH_DEFAULT));
			} else {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_REMOVED));
			}
		} else {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECTS_REMOVED_WITH_DEFAULT, count + ""));
			} else {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECTS_REMOVED, count + ""));
			}
		}
		
		return true;
	}
	
	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)throws Exception{
		Collection<String> removeSsidName = new Vector<String>();
		Collection<String> removeSsidNameLocalGroup = new Vector<String>();
		if (ids!=null && !ids.isEmpty()) {
			List<?> ssidNameList = QueryUtil.executeQuery(
					"select ssidName from " + boClass.getSimpleName(),
					null, new FilterParams("id", ids));
			for(Object obj:ssidNameList){
				removeSsidName.add(obj.toString());
				removeSsidNameLocalGroup.add(obj.toString());
			}
		}
		int count = super.removeBos(boClass, ids);
		if (isEasyMode() && !removeSsidName.isEmpty()){
			try {
				Collection<Long> defaultIds = getDefaultIds(UserProfile.class);
				QueryUtil.removeBos(UserProfile.class, new FilterParams("userProfileName",removeSsidName),getUserContext(),defaultIds);
				QueryUtil.removeBos(RadiusAssignment.class, new FilterParams("radiusName",removeSsidName),getUserContext(),null);
				List<?> radiusIds = QueryUtil.executeQuery(
						"select id from " + RadiusOnHiveap.class.getSimpleName(),
						null, new FilterParams("radiusName", removeSsidName),getDomain().getId());
				if (!radiusIds.isEmpty()){
					QueryUtil.updateBos(HiveAp.class, "radiusServerProfile = :s1","radiusServerProfile.id in (:s2)",
						new Object[] {null,radiusIds},getDomain().getId());
				}
				QueryUtil.removeBos(RadiusOnHiveap.class, new FilterParams("radiusName",removeSsidName),getUserContext(),null);
				List<Long> userGroupIds = (List<Long>)QueryUtil.executeQuery(
						"select id from " + LocalUserGroup.class.getSimpleName(),
						null, new FilterParams("groupName", removeSsidNameLocalGroup),getDomain().getId());
				if (!userGroupIds.isEmpty()){
					QueryUtil.removeBos(LocalUser.class, new FilterParams("localUserGroup.id",userGroupIds),getUserContext(),null);
				}
				if (!userGroupIds.isEmpty()){
					QueryUtil.removeBos(LocalUserGroup.class, userGroupIds);
				}
				QueryUtil.removeBos(LocalUserGroup.class, new FilterParams("groupName",removeSsidNameLocalGroup),getUserContext(),null);
				QueryUtil.removeBos(Scheduler.class, new FilterParams("schedulerName",removeSsidName),getUserContext(),null);
				QueryUtil.removeBos(IpPolicy.class, new FilterParams("policyName",removeSsidName),getUserContext(),null);
				defaultIds = getDefaultIds(QosRateControl.class);
				QueryUtil.removeBos(QosRateControl.class, new FilterParams("qosName",removeSsidName),getUserContext(),defaultIds);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//remove client self register info by ssid name
		if (!removeSsidName.isEmpty()) {
			QueryUtil.bulkRemoveBos(AhClientEditValues.class,new FilterParams("type = :s1 and ssidname in(:s2)",
					new Object[] {AhClientEditValues.TYPE_SELF_REGISTER,removeSsidName}),getDomain().getId());
			for(String ssidName: removeSsidName)
				CacheMgmt.getInstance().removeClientEditValues(getDomain().getId(),AhClientEditValues.TYPE_SELF_REGISTER,ssidName);
		}
		return count;
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_ACCESS_SECURITY));
		if (isEasyMode()){
			columns.add(new HmTableColumn(COLUMN_USER_CATEGORY));
		}
		columns.add(new HmTableColumn(COLUMN_CWPUSE));
		columns.add(new HmTableColumn(COLUMN_ENABLEDMACAUTH));
		columns.add(new HmTableColumn(COLUMN_MAXCLIENT));
		columns.add(new HmTableColumn(COLUMN_USED_IDM));
		if (isFullMode()){
			columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		}
		
		return columns;
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
		setDataSource(SsidProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_SSID_PROFILE;
		enableSorting();
		if ("id".equals(sortParams.getOrderBy())) {
			sortParams.setOrderBy("ssidName");
			sortParams.setAscending(false);
			sortParams.setOrderByNumber(true);
		}
	}
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		SsidProfile source = QueryUtil.findBoById(SsidProfile.class,
				paintbrushSource, this);
		if (source == null) {
			return null;
		}
		List<SsidProfile> list = QueryUtil.executeQuery(SsidProfile.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (SsidProfile profile : list) {
			if (profile.getDefaultPrepareFlg()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			SsidProfile up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setSsidName(profile.getSsidName());
			up.setSsid(profile.getSsid());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			setCloneFields(source, up);
			boolean needAdd = true;
			try {
				if (getReferenceWlanPolicy(up.getId())) {
					for (CheckItem wlanpolicy : referenceWLAN) {
						ConfigTemplate configTemplate = findBoById(
								ConfigTemplate.class, wlanpolicy.getId(), this);
						if (!checkAllCloseUpdate(resetConfigTemplateQos(configTemplate,up))) {
							if (isEasyMode()){
								addActionError(strErrorMessage);
							} else {
								addActionError(
										MgrUtil.getUserMessage("action.error.newwork.policy.update.ssid",new String[]{
												wlanpolicy.getValue(),up.getSsidName()
										})
										+ " " + strErrorMessage);
							}
							strErrorMessage = "";
							needAdd = false;
							break;
						}
					}
				}
			} catch (Exception e) {
				addActionError(e.getMessage());
				needAdd = false;
			}
			if (needAdd){
				hmBos.add(up);
			}
		}
		return hmBos;
	}
	
	public void prepareSetSimpleModeUserProfile(String operation) throws Exception{
		if ("editSimpleModeUserProfile".equals(operation)){
			getDataSource().setUserProfileDefault(null);
			getDataSource().setUserProfileSelfReg(null);

			IpPolicy ipPolicyFrom;
			QosRateControl qosRate;
			UserProfile createUserProfile;
			
			createUserProfile = functionGetCreateUserProfile();
			qosRate = functionGetQosRateControl();
			ipPolicyFrom  = functionGetIpPolicyFrom();
			
			if (qosRate.getId()==null && getDataSource().getShowExpressUserAccess()) {
				Long tmpCreateId = QueryUtil.createBo(qosRate);
				qosRate = QueryUtil.findBoById(QosRateControl.class, tmpCreateId);
			} else {
				if (getDataSource().getShowExpressUserAccess()){
					if (qosRate.isDefaultFlag()){
						flgRemoveQosRate= true;
						createUserProfile.setPolicingRate(54000);
						createUserProfile.setPolicingRate11n(1000000);
						createUserProfile.setPolicingRate11ac(1000000);
					} else {
						qosRate = QueryUtil.updateBo(qosRate);
					}
				}
			}
			
			if (ipPolicyFrom!=null) {
				if (ipPolicyFrom.getId()==null && getDataSource().getShowExpressUserAccess()) {
					Long tmpCreateId = QueryUtil.createBo(ipPolicyFrom);
					ipPolicyFrom = QueryUtil.findBoById(IpPolicy.class, tmpCreateId);
				}
			} else {
				if (getDataSource().getShowExpressUserAccess()) {
					flgRemoveIpPolicyFrom= true;
				}
			}
			
			if (getDataSource().getShowExpressUserAccess()){
				createUserProfile.setQosRateControl(qosRate);
				createUserProfile.setIpPolicyFrom(ipPolicyFrom);
			}
			if (getDataSource().getShowExpressUserAccess() && ipPolicyFrom==null){
				createUserProfile.setActionIp((short)-1);
			}
			if (createUserProfile.getId()==null) {
				Long tmpCreateId = QueryUtil.createBo(createUserProfile);
				createUserProfile = QueryUtil.findBoById(UserProfile.class, tmpCreateId);
			} else {
				createUserProfile = QueryUtil.updateBo(createUserProfile);
				MgrUtil.setSessionAttribute(UserProfile.class.getSimpleName() + "Source",
						findBoById(UserProfile.class, createUserProfile.getId(), this));

			}
			
			if (getDataSource().getCwp()!=null 
					&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
				getDataSource().setUserProfileSelfReg(createUserProfile);
				simpleModeUserProfileId = getDataSource().getUserProfileSelfReg().getId();
				getDataSource().setUserProfileDefault(null);
			} else {
				getDataSource().setUserProfileDefault(createUserProfile);
				simpleModeUserProfileId = getDataSource().getUserProfileDefault().getId();
				getDataSource().setUserProfileSelfReg(null);
			}
		}
	}
	
	public void setCloneFields(SsidProfile source, SsidProfile destination){
		Set<Scheduler> cloneSchedulers = new HashSet<Scheduler>();
		for (Scheduler tempClass : source.getSchedulers()) {
			cloneSchedulers.add(tempClass);
		}
		destination.setSchedulers(cloneSchedulers);

		Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
		for (MacFilter tempClass : source.getMacFilters()) {
			cloneMacFilters.add(tempClass);
		}
		destination.setMacFilters(cloneMacFilters);

		Set<UserProfile> cloneRadiusUserProfiles = new HashSet<UserProfile>();
		for (UserProfile tempClass : source.getRadiusUserProfile()) {
			cloneRadiusUserProfiles.add(tempClass);
		}
		destination.setRadiusUserProfile(cloneRadiusUserProfiles);

		Set<LocalUserGroup> cloneLocalUserGroups = new HashSet<LocalUserGroup>();
		for (LocalUserGroup tempClass : source.getLocalUserGroups()) {
			cloneLocalUserGroups.add(tempClass);
		}
		destination.setLocalUserGroups(cloneLocalUserGroups);
	}
	public boolean getReferenceWlanPolicy(Long dataSourceId) {
		String queryString = "select DISTINCT bo2.id,bo2.configname "
				+ "from config_template_ssid bo1,config_template bo2 "
				+ "where bo1.config_template_id = bo2.id " + "and bo1.ssid_profile_id="
				+ dataSourceId;

		referenceWLAN = new HashSet<CheckItem>();
		List<?> list = QueryUtil.executeNativeQuery(queryString);
		for (Object object : list) {
			Object[] tmp = (Object[]) object;
			referenceWLAN.add(new CheckItem(Long.valueOf(tmp[0].toString()), tmp[1].toString()));
		}
		if (referenceWLAN.isEmpty()) {
			return false;
		} else if (referenceWLAN.size() == 1 && getLstForward().equals("configTemplate")) {
			HmBo configTemplate = (HmBo) MgrUtil.getSessionAttribute(ConfigTemplate.class
					.getSimpleName()
					+ "Source");
			return !(configTemplate != null && configTemplate.getId() != null);
		} else {
			return true;
		}
	}

	public void prepareDependentObjects() throws Exception {
	    enabledCWP4IDM();
	    
		if (getDataSource().getSsidDos() != null) {
			ssidDos = getDataSource().getSsidDos().getId();
		}
		if (getDataSource().getStationDos() != null) {
			stationDos = getDataSource().getStationDos().getId();
		}
		if (getDataSource().getIpDos() != null) {
			ipDos = getDataSource().getIpDos().getId();
		}
		if (getDataSource().getServiceFilter() != null) {
			serviceFilter = getDataSource().getServiceFilter().getId();
		}
		if (getDataSource().getAsRuleGroup() != null) {
			asRuleGroup = getDataSource().getAsRuleGroup().getId();
		}
		if (getDataSource().getCwp() != null) {
			cwpId = getDataSource().getCwp().getId();
		}
		if (getDataSource().getPpskECwp() != null){
			ppskECwpId = getDataSource().getPpskECwp().getId();
		}
		if (getDataSource().getWpaECwp() != null){
			wpaECwpId = getDataSource().getWpaECwp().getId();
		}
		if (getDataSource().getUserPolicy() != null) {
			userPolicyId = getDataSource().getUserPolicy().getId();	
		}
//		if (getDataSource().getCwp() == null && getDataSource().getUserPolicy() == null) {
//			enableCwpSelect = false;
//		} else {
//			enableCwpSelect = true;
//		}
		if (getDataSource().getRadiusAssignment() != null) {
			radiusId = getDataSource().getRadiusAssignment().getId();
		}
		
		if (getDataSource().getRadiusAssignmentPpsk() != null) {
			radiusPpskId = getDataSource().getRadiusAssignmentPpsk().getId();
		}
		
		if (getDataSource().getUserProfileSelfReg() != null) {
			userProfileSelfRegId = getDataSource().getUserProfileSelfReg().getId();
		}
		if (getDataSource().getUserProfileDefault() != null) {
			userProfileDefaultId = getDataSource().getUserProfileDefault().getId();
		}
		if (getDataSource().getConfigmdmId() != null) {
			configmdmId = getDataSource().getConfigmdmId().getId();
		}
		if (isEasyMode()){
			if (getDataSource().getUserVlan()!=null) {
				newUserInfoVlanId = getDataSource().getUserVlan().getId();
			} else {
				if (getDataSource().getCwp()!=null 
						&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
					if (getDataSource().getUserProfileSelfReg()!=null){
						UserProfile userProfileClass = QueryUtil.findBoById(UserProfile.class,
								getDataSource().getUserProfileSelfReg().getId(), this);
						getDataSource().setUserVlan(userProfileClass.getVlan());
						getDataSource().setBlnUserManager(userProfileClass.getBlnUserManager());
						if (getDataSource().getUserVlan()!=null) {
							newUserInfoVlanId = getDataSource().getUserVlan().getId();
						}
					}
				} else {
					if (getDataSource().getUserProfileDefault()!=null){
						UserProfile userProfileClass = QueryUtil.findBoById(UserProfile.class,
								getDataSource().getUserProfileDefault().getId(), this);
						getDataSource().setUserVlan(userProfileClass.getVlan());
						getDataSource().setBlnUserManager(userProfileClass.getBlnUserManager());
						if (getDataSource().getUserVlan()!=null) {
							newUserInfoVlanId = getDataSource().getUserVlan().getId();
						}
					}
				}
			}
			if (getDataSource().getUserVlan()==null) {
				getDataSource().setUserVlan(HmBeParaUtil.getDefaultProfile(Vlan.class,null));
				newUserInfoVlanId = getDataSource().getUserVlan().getId();
			}
		}

		prepareGetSsidSecurity();
		prepareAvailableFilters();
		prepareAvailableIpAddress();
		prepareAvailableVlan();
		prepareAvailableServiceFilters();
		prepareAirscreenRuleGroups();
		prepareAvailableSchedules();
		prepareDosParameterProfiles();
		prepareMDMParameterProfiles();
		prepareCwpProfiles();
		prepareUserPolicyProfiles();
		prepareRadiusProfiles();
		prepareRadiusUserProfile();
		prepareUserProfiles();
		prepareLocalUserGroups();
		prepareRadiusLocalUserGroups();
		if (isFullMode()) {
			prepareNewRadiusServerPanelValue();
		} else {
			prepareSimpleModeServerPanelValue();
			prepareIDMStatus();
		}
	}
	
	public void prepareEditSimpleModeRadiusValue() throws Exception{
		if (isEasyMode()){
			if (getDataSource().getRadiusAssignment()!=null){
				RadiusAssignment tmpProfile = findBoById(
						RadiusAssignment.class, getDataSource().getRadiusAssignment().getId(),this);
				if (tmpProfile.getServices()!=null && !tmpProfile.getServices().isEmpty()){
					for(RadiusServer server:tmpProfile.getServices()){
						if (server.getServerPriority()== RadiusServer.RADIUS_PRIORITY_PRIMARY) {
							if (getDataSource().getNewRadiusType() ==Cwp.CWP_INTERNAL){
								List<HiveAp> tmpHive = QueryUtil.executeQuery(HiveAp.class,
										null, new FilterParams("cfgIpAddress = :s1 and dhcp=:s2",
												new Object[]{server.getIpAddress().getAddressName(),false}),getDomain().getId());
								if (!tmpHive.isEmpty()){
								    HiveAp tmpHiveAp=tmpHive.get(0);
								    // set combo selected
									getDataSource().setSelectNewHiveApRadiusPrimaryIp(tmpHiveAp.getHostName());
									// set hiveAP
									getDataSource().setStaticHiveAPIpAddress(tmpHiveAp.getCfgIpAddress());
									getDataSource().setStaticHiveAPNetmask(tmpHiveAp.getCfgNetmask());
									getDataSource().setStaticHiveAPGateway(tmpHiveAp.getCfgGateway());
						            // get the DNS information
						            String dnsServer = getDNSServerInExpress();
									if(null != dnsServer) {
						            	getDataSource().setDnsServer(dnsServer);
						            }
									// set binded AD information
									prepareActiveDirectory(tmpHiveAp.getHostName());
								}
							} else {
								getDataSource().setNewRadiusPrimaryIp(server.getIpAddress().getId());
								getDataSource().setNewRadiusSecret(server.getSharedSecret());
							}
						} else {
							if (getDataSource().getNewRadiusType() ==Cwp.CWP_INTERNAL){
								List<?> tmpHostName = QueryUtil.executeQuery("select hostName from "+ HiveAp.class.getSimpleName(),
										null, new FilterParams("cfgIpAddress = :s1 and dhcp=:s2",
												new Object[]{server.getIpAddress().getAddressName(),false}),getDomain().getId());
								if (!tmpHostName.isEmpty()){
									getDataSource().setSelectNewHiveApRadiusSecondaryIp(tmpHostName.get(0).toString());
								}
//								getDataSource().setSelectNewHiveApRadiusSecondaryIp(ser.getIpAddress().getAddressName());
							} else {
								getDataSource().setNewRadiusSecondaryIp(server.getIpAddress().getId());
								getDataSource().setNewRadiusSecondSecret(server.getSharedSecret());
							}
							
						}
					}
				}
//				RadiusOnHiveap tmpradius = QueryUtil.findBoByAttribute(RadiusOnHiveap.class, "radiusName",
//						getDataSource().getSsidName(), getDomain().getId());
//				if (tmpradius!=null) {
//					getDataSource().setNewRadiusType(Cwp.CWP_INTERNAL);
//				} else {
//					getDataSource().setNewRadiusType(Cwp.CWP_EXTERNAL);
//				}
			}
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
				long groupId =0;
				if (getDataSource().getLocalUserGroups()!=null && !getDataSource().getLocalUserGroups().isEmpty()){
					for(LocalUserGroup tmpClass: getDataSource().getLocalUserGroups()){
						groupId = tmpClass.getId();
					}
				} 
				long userCount = QueryUtil.findRowCount(LocalUser.class, new FilterParams(
						"localUserGroup.id", groupId));
				getDataSource().setUserNumberPsk((int)userCount);
			}
			
			if (getDataSource().getCwp()!=null 
					&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
				if (getDataSource().getUserProfileSelfReg()!=null) {
					getDataSource().setBlnUserManager(getDataSource().getUserProfileSelfReg().getBlnUserManager());
				}
			} else {
				if (getDataSource().getUserProfileDefault()!=null) {
					getDataSource().setBlnUserManager(getDataSource().getUserProfileDefault().getBlnUserManager());
				}
			}
		}
	}

	private void prepareActiveDirectory(String hostName) throws JSONException {
		
		resetAdIntegrationValues();
		
		RadiusOnHiveap radiusOnHiveap=getRadiusServer(hostName);
		
		if(getDataSource().isEnableADIntegration() && null != radiusOnHiveap 
				&& !radiusOnHiveap.getDirectoryOrLdap().isEmpty()){
			
			ActiveDirectoryOrOpenLdap adOrLdap = radiusOnHiveap.getDirectoryOrLdap().get(0).getDirectoryOrLdap();
			
			if (adOrLdap.getAdDomains().isEmpty()) {
				setShowTestAdDiv("none");
			} else {
				ActiveDirectoryDomain domainObj = adOrLdap.getAdDomains().get(0);
				getDataSource().setAdDomainFullName(domainObj.getFullName());
				
				getDataSource().setAdServerIpAddress(adOrLdap.getAdServer().getItems().get(0).getIpAddress());
				if(null == adOrLdap.getUserNameA() || null == adOrLdap.getPasswordA()) {
					getDataSource().setAdDomainAdmin("");
					getDataSource().setAdDomainAdminPasswd("");
				} else {
					getDataSource().setAdDomainAdmin(adOrLdap.getUserNameA());
					getDataSource().setAdDomainAdminPasswd(adOrLdap.getPasswordA());
				}
				getDataSource().setBaseDN(adOrLdap.getBasedN());
				getDataSource().setAdDomainName(domainObj.getDomain());
				//setAdDomainFullName(domainObj.getFullName());
				String bindDnName = domainObj.getBindDnName();
				
				getDataSource().setAdDomainTestUser(bindDnName);
				getDataSource().setAdDomainTestUserPasswd(domainObj.getBindDnPass());
				
				getDataSource().setLdapSaslWrapping(adOrLdap.getLdapSaslWrapping());
				setShowTestAdDiv("");
			}
		}else{
			setShowTestAdDiv("none");
		}
		
	}
    private void initActiveDirectory(String hostName, String hiveAPMac,JSONObject jsonObject) throws JSONException {
        
        resetAdIntegrationValues();
        // query information from AD
        JSONObject queryApJsonObj = ActiveDirectoryTool.queryApOperation(jsonObject, hiveAPMac);
        
		String queryFullDN = null;
		byte queryResult = Byte.parseByte(queryApJsonObj.get("resCode").toString());
		boolean queryFlag = BeQueryADInfoResultEvent.RESULTCODE_SUCCESS == queryResult;
		
		if(queryFlag){
        	 queryFullDN = queryApJsonObj.get("fullDomainName").toString();
         }
        
        RadiusOnHiveap radiusOnHiveap=getRadiusServer(hostName);
        
        if(null != radiusOnHiveap && !radiusOnHiveap.getDirectoryOrLdap().isEmpty() && queryFlag
        		&& StringUtils.isNotBlank(queryFullDN)){
            getDataSource().setEnableADIntegration(true);
            ActiveDirectoryOrOpenLdap adOrLdap = radiusOnHiveap.getDirectoryOrLdap().get(0).getDirectoryOrLdap();
            getDataSource().setAdDomainFullName(queryFullDN);
			if (adOrLdap.getAdDomains().isEmpty()) {
				if (null != jsonObject){
					jsonObject.put("enableADIntegration", true);
					jsonObject.put("sameFullDN", false);
				}
				setShowTestAdDiv("none");
			} else {
				ActiveDirectoryDomain domainObj = adOrLdap.getAdDomains().get(0);
				if (domainObj.getFullName().equals(queryFullDN)) {// if had same domain info in database, show the details
					getDataSource().setAdServerIpAddress(adOrLdap.getAdServer().getItems().get(0).getIpAddress());
					if(null == adOrLdap.getUserNameA() || null == adOrLdap.getPasswordA()) {
						getDataSource().setAdDomainAdmin("");
						getDataSource().setAdDomainAdminPasswd("");
					} else {
						getDataSource().setAdDomainAdmin(adOrLdap.getUserNameA());
						getDataSource().setAdDomainAdminPasswd(adOrLdap.getPasswordA());
					}
					getDataSource().setBaseDN(adOrLdap.getBasedN());
					if (null != jsonObject) {
						jsonObject.put("sameFullDN", true);
						jsonObject.put("enableADIntegration", true);
						jsonObject.put("adServerIpAddress",
								getDataSource().getAdServerIpAddress());
						jsonObject.put("domainAdmin", getDataSource().getAdDomainAdmin());
						jsonObject.put("domainAdminPasswd",
								getDataSource().getAdDomainAdminPasswd());
						jsonObject.put("baseDN", getDataSource().getBaseDN());
					}
					getDataSource().setAdDomainName(domainObj.getDomain());
					//setAdDomainFullName(domainObj.getFullName());
					String bindDnName = domainObj.getBindDnName();
					
					getDataSource().setAdDomainTestUser(bindDnName);
					getDataSource().setAdDomainTestUserPasswd(domainObj.getBindDnPass());
					if (null != jsonObject) {
						jsonObject.put("domainName", getDataSource().getAdDomainName());
						jsonObject.put("domainFullName", getDataSource().getAdDomainFullName());
						jsonObject.put("domainTestUser", getDataSource().getAdDomainTestUser());
						jsonObject.put("domainTestUserPasswd",
								getDataSource().getAdDomainTestUserPasswd());
					}
					
					setShowTestAdDiv("");
				} else {
					if (null != jsonObject){// if not 
						jsonObject.put("enableADIntegration", true);
						jsonObject.put("sameFullDN", false);
					}
					setShowTestAdDiv("none");
				}
			}
        }else{
            setShowTestAdDiv("none");
            if(null != jsonObject)
                jsonObject.put("enableADIntegration", false);
        }
            
    }
	
	public void prepareSimpleModeServerPanelValue(){
//		newRadiusType=getDataSource().getNewRadiusType();
		newRadiusPrimaryIp=getDataSource().getNewRadiusPrimaryIp();
		newRadiusSecret=getDataSource().getNewRadiusSecret();
		newRadiusSecondaryIp=getDataSource().getNewRadiusSecondaryIp();
		newRadiusSecondSecret=getDataSource().getNewRadiusSecondSecret();
		
		// AD integration
		String hiveApRadiusPrimaryIp = getDataSource().getSelectNewHiveApRadiusPrimaryIp();
		if(StringUtils.isNotBlank(hiveApRadiusPrimaryIp) 
				&&  !StringUtils.equals(hiveApRadiusPrimaryIp, MgrUtil.getUserMessage("config.optionsTransfer.none"))) {

		}else {
			showHiveRadiusServerInfo(false);
		}
	}
	
	public void prepareNewRadiusServerPanelValue(){
//		newRadiusType=getDataSource().getNewRadiusType();
		newRadiusName=getDataSource().getNewRadiusName();
		newRadiusPrimaryIp=getDataSource().getNewRadiusPrimaryIp();
		newRadiusSecret=getDataSource().getNewRadiusSecret();
		newRadiusSecondaryIp=getDataSource().getNewRadiusSecondaryIp();
		newRadiusSecondSecret=getDataSource().getNewRadiusSecondSecret();
		
		newSelfUserProfileName=getDataSource().getNewSelfUserProfileName();
		newSelfAttributeValue=getDataSource().getNewSelfAttributeValue();
		newSelfVlanId=getDataSource().getNewSelfVlanId();
//		newSelfGuestAccess=getDataSource().getNewSelfGuestAccess();
		
		newDefaultUserProfileName=getDataSource().getNewDefaultUserProfileName();
		newDefaultAttributeValue=getDataSource().getNewDefaultAttributeValue();
		newDefaultVlanId=getDataSource().getNewDefaultVlanId();
//		newDefaultGuestAccess=getDataSource().getNewDefaultGuestAccess();
		
		newOptionUserProfileName=getDataSource().getNewOptionUserProfileName();
		newOptionAttributeValue=getDataSource().getNewOptionAttributeValue();
		newOptionVlanId=getDataSource().getNewOptionVlanId();
//		newOptionGuestAccess=getDataSource().getNewOptionGuestAccess();
	}
	
	public void prepareSetSaveObjects() throws Exception {

		if (ssidDos != null) {
			DosPrevention ssidDosClass = findBoById(DosPrevention.class, ssidDos);
			if (ssidDosClass == null && ssidDos != -1) {
				String tempStr[] = { getText("config.ssid.ssidDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setSsidDos(ssidDosClass);
		}

		if (stationDos != null) {
			DosPrevention stationDosClass = findBoById(DosPrevention.class,
					stationDos);
			if (stationDosClass == null && stationDos != -1) {
				String tempStr[] = { getText("config.ssid.macDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setStationDos(stationDosClass);
		}

		if (ipDos != null) {
			DosPrevention ipDosClass = findBoById(DosPrevention.class, ipDos);
			if (ipDosClass == null && ipDos != -1) {
				String tempStr[] = { getText("config.ssid.ipDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setIpDos(ipDosClass);
		}
		if (serviceFilter != null) {
			ServiceFilter serviceFilterClass = findBoById(ServiceFilter.class,
					serviceFilter);
			if (serviceFilterClass == null && serviceFilter != -1) {
				String tempStr[] = { getText("config.ssid.serviceFilter") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setServiceFilter(serviceFilterClass);
		}
		if(configmdmId!=null){
			ConfigTemplateMdm configtemplemdm=findBoById(ConfigTemplateMdm.class, configmdmId);
			if (configtemplemdm == null && configmdmId != -1) {
				String tempStr[] = { getText("config.ssid.ipDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setConfigmdmId(configtemplemdm);
		}
		

		if (asRuleGroup != null && asRuleGroup != -1) {
			getDataSource().setAsRuleGroup(
					findBoById(AirScreenRuleGroup.class, asRuleGroup));
		}

		if (userPolicyId != null) {
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
					|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				Cwp userPolicyClass = findBoById(Cwp.class, userPolicyId);
				if (userPolicyClass == null && userPolicyId != -1) {
					String tempStr[] = { getText("config.ssid.userPolicy") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setUserPolicy(userPolicyClass);
			} else {
				getDataSource().setUserPolicy(null);
			}
		}

		enabledCWP4IDM();
		if (cwpId != null) {
			Cwp cwpClass = findBoById(Cwp.class, cwpId);
//			if (cwpClass != null && cwpClass.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA) {
			if (cwpClass != null && getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK
					&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_8021X
					&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				getDataSource().setCwp(cwpClass);
			} else {
				getDataSource().setCwp(null);
			}
		}
		
		if (ppskECwpId != null){
			Cwp ppskCwpClass = findBoById(Cwp.class, ppskECwpId);
			if(ppskCwpClass != null && getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
				getDataSource().setPpskECwp(ppskCwpClass);
			}else {
				getDataSource().setPpskECwp(null);
			}
		}

		if (getDataSource().getCwp() != null
				&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
				&& !getDataSource().getMacAuthEnabled()
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			userProfileDefaultId = null;
			radiusId = null;
			selectUserProfiles = null;
			getDataSource().setRadiusAssignment(null);
			getDataSource().setUserProfileDefault(null);
		} else {
			if (userProfileDefaultId != null) {
				UserProfile userProfileClass = findBoById(UserProfile.class,
						userProfileDefaultId);
				getDataSource().setUserProfileDefault(userProfileClass);
			}
		}
		
		if (getDataSource().getCwp() != null
				&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA
				&& !getDataSource().getMacAuthEnabled()
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			radiusId = null;
			getDataSource().setRadiusAssignment(null);
			
			if (getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK) {
				selectUserProfiles = null;
			}
		}

		if (getDataSource().getCwp() == null
				&& !getDataSource().getMacAuthEnabled()
				&& !getDataSource().getEnabledUseGuestManager()
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			radiusId = null;
			getDataSource().setRadiusAssignment(null);
			if (getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK) {
				selectUserProfiles = null;
			} else {
				userProfileDefaultId = null;
				getDataSource().setUserProfileDefault(null);
			}
		}
		
		if (getDataSource().getCwp() != null) {
		    //resetIDM4CWP();
		}
		
//		if (getDataSource().getCwp() != null
//				&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
//				&& !getDataSource().getMacAuthEnabled()
//				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
//				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
//				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
//				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP
//				&& getDataSource().getCwp().isPpskServer()) {
//			radiusId = null;
//			getDataSource().setRadiusAssignment(null);
//			selectUserProfiles = null;
//		}

		if (radiusId != null) {
			RadiusAssignment radiusClass = findBoById(RadiusAssignment.class,
					radiusId);
			getDataSource().setRadiusAssignment(radiusClass);
		}
        if (getDataSource().isEnabledIDM()) {
            // reset the unrelated fields
            getDataSource().setRadiusAssignment(null);
            getDataSource().setRadiusAssignmentPpsk(null);
            getDataSource().getLocalUserGroups().clear();
            getDataSource().getRadiusUserGroups().clear();
        }

		if (getDataSource().getCwp() != null
				&& (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
					|| getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
			if (userProfileSelfRegId != null) {
				UserProfile userProfileClass = findBoById(UserProfile.class,
						userProfileSelfRegId);
				getDataSource().setUserProfileSelfReg(userProfileClass);
			}
		} else {
			userProfileSelfRegId = null;
			getDataSource().setUserProfileSelfReg(null);
		}
		setSelectedMacFilters();
		setSelectedSchedulers();
		setSelectedRadiusUserProfils();
		setSelectedLocalUserGroups();
		
		if (isEasyMode()){
			if ((null == newUserInfoVlanId || newUserInfoVlanId == -1)) {
				Vlan tmpVlan = CreateObjectAuto.createNewVlan(inputNewUserProfileVlanValue, getDomain(), "");
				if (tmpVlan!=null){
					getDataSource().setUserVlan(tmpVlan);
				}
			} else {
				Vlan tmpVlan = findBoById(Vlan.class, newUserInfoVlanId);
				getDataSource().setUserVlan(tmpVlan);
			}
		}
		if (!"continue".equals(operation)){
			setSimpleNewRadiusPanelValue();
		}
		if (getDataSource().getAccessMode()!=SsidProfile.ACCESS_MODE_PSK || !getDataSource().isEnablePpskSelfReg()) {
			radiusPpskId=null;
			getDataSource().setRadiusAssignmentPpsk(null);
		}
		if (getDataSource().getAccessMode()!=SsidProfile.ACCESS_MODE_WPA || !getDataSource().isEnableProvisionPersonal()) {
		}
		if (radiusPpskId != null) {
			RadiusAssignment radiusPpskClass = findBoById(RadiusAssignment.class,
					radiusPpskId);
			getDataSource().setRadiusAssignmentPpsk(radiusPpskClass);
		}
	}
	
    private void resetIDM4CWP() {
        if (getDataSource().isEnabledIDM()) {
            final byte registrationType = getDataSource().getCwp().getRegistrationType();
            if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
                if (!(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        || registrationType == Cwp.REGISTRATION_TYPE_REGISTERED
                        || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
                    getDataSource().setEnabledIDM(false);
                }
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
                if (!(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED // Auth
                        || registrationType == Cwp.REGISTRATION_TYPE_EULA // AA
                        || registrationType == Cwp.REGISTRATION_TYPE_REGISTERED // Slef-Reg
                        || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
                    getDataSource().setEnabledIDM(false);
                }
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
                    && getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
                    && !(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                    || registrationType == Cwp.REGISTRATION_TYPE_REGISTERED
                    || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
                getDataSource().setEnabledIDM(false);
            }
        }
    }
    
	public void setSimpleNewRadiusPanelValue(){
		if (isFullMode()){
			return ;
		}
 		boolean blnNeedSaveValue = false;
		if (getDataSource().getMacAuthEnabled() ||getDataSource().getEnabledUseGuestManager()
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			blnNeedSaveValue = true;
		} else {
			if (getDataSource().getCwp() != null) {
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
						 || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
					blnNeedSaveValue = true;
				}
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL){
					//if (!getDataSource().getCwp().isPpskServer()) {
						blnNeedSaveValue = true;
					//}
				}
			}
		}
		if (blnNeedSaveValue){
			if (null == newRadiusPrimaryIp || newRadiusPrimaryIp == -1) {
				short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputNewRadiusPrimaryIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
				IpAddress tmpIpAddress = CreateObjectAuto.createNewIP(inputNewRadiusPrimaryIpValue, ipType, getDomain(), "For AAA Client Setting");
				if (tmpIpAddress!=null){
					getDataSource().setNewRadiusPrimaryIp(tmpIpAddress.getId());
				}
			} else {
				getDataSource().setNewRadiusPrimaryIp(newRadiusPrimaryIp);
			}
			if (null == newRadiusSecondaryIp || newRadiusSecondaryIp == -1) {
				short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputNewRadiusSecondaryIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
				IpAddress tmpIpAddress = CreateObjectAuto.createNewIP(inputNewRadiusSecondaryIpValue, ipType, getDomain(), "For AAA Client Setting");
				if (tmpIpAddress!=null){
					getDataSource().setNewRadiusSecondaryIp(tmpIpAddress.getId());
				}
			} else {
				getDataSource().setNewRadiusSecondaryIp(newRadiusSecondaryIp);
			}

			getDataSource().setNewRadiusSecret(newRadiusSecret);
			getDataSource().setNewRadiusSecondSecret(newRadiusSecondSecret);
		}
	}
	
	public void setNewRadiusPanelValue(boolean saveAuto){
		if ((null == newRadiusPrimaryIp || newRadiusPrimaryIp == -1) && saveAuto) {
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputNewRadiusPrimaryIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			Long tmpId = CreateObjectAuto.createNewIP(inputNewRadiusPrimaryIpValue, ipType, getDomain(), "For AAA Client Setting").getId();
			getDataSource().setNewRadiusPrimaryIp(tmpId);
		} else {
			getDataSource().setNewRadiusPrimaryIp(newRadiusPrimaryIp);
		}
		if ((null == newRadiusSecondaryIp || newRadiusSecondaryIp == -1) 
				&& !inputNewRadiusSecondaryIpValue.equals("")&& saveAuto) {
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputNewRadiusSecondaryIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			Long tmpId = CreateObjectAuto.createNewIP(inputNewRadiusSecondaryIpValue, ipType, getDomain(), "For AAA Client Setting").getId();
			getDataSource().setNewRadiusSecondaryIp(tmpId);
		} else {
			getDataSource().setNewRadiusSecondaryIp(newRadiusSecondaryIp);
		}

		getDataSource().setNewRadiusName(newRadiusName);
		getDataSource().setNewRadiusSecret(newRadiusSecret);
		getDataSource().setNewRadiusSecondSecret(newRadiusSecondSecret);
		
	}
	public void setNewSelfUserProfilePanelValue(boolean saveAuto){
		if ((null == newSelfVlanId || newSelfVlanId == -1) && saveAuto) {
			Long tmpId = CreateObjectAuto.createNewVlan(inputNewSelfVlanValue, getDomain(), "").getId();
			getDataSource().setNewSelfVlanId(tmpId);
		} else {
			getDataSource().setNewSelfVlanId(newSelfVlanId);
		}
		getDataSource().setNewSelfUserProfileName(newSelfUserProfileName);
		getDataSource().setNewSelfAttributeValue(newSelfAttributeValue);
	}
	public void setNewDefaultUserProfilePanelValue(boolean saveAuto){
		if ((null == newDefaultVlanId || newDefaultVlanId == -1) && saveAuto) {
			Long tmpId = CreateObjectAuto.createNewVlan(inputNewDefaultVlanValue, getDomain(), "").getId();
			getDataSource().setNewDefaultVlanId(tmpId);
		} else {
			getDataSource().setNewDefaultVlanId(newDefaultVlanId);
		}
		getDataSource().setNewDefaultUserProfileName(newDefaultUserProfileName);
		getDataSource().setNewDefaultAttributeValue(newDefaultAttributeValue);
	}
	public void setNewOptionUserProfilePanelValue(boolean saveAuto){
		if ((null == newOptionVlanId || newOptionVlanId == -1) && saveAuto) {
			Long tmpId = CreateObjectAuto.createNewVlan(inputNewOptionVlanValue, getDomain(), "").getId();
			getDataSource().setNewOptionVlanId(tmpId);
		} else {
			getDataSource().setNewOptionVlanId(newOptionVlanId);
		}
		getDataSource().setNewOptionUserProfileName(newOptionUserProfileName);
		getDataSource().setNewOptionAttributeValue(newOptionAttributeValue);
	}

	private boolean backFromUser=false;
	
	private Long dosId;

	private Long ssidDos;

	private Long stationDos;

	private Long ipDos;

	private Long serviceFilter;

	private Long cwpId;
	
	private Long ppskECwpId;
	private Long wpaECwpId;

	private Long userPolicyId;

	private Long radiusId;
	
	private Long radiusPpskId;

	private Long userProfileSelfRegId;
	
	private Long simpleModeUserProfileId;

	private Long userProfileDefaultId;

	private Long asRuleGroup;

	protected OptionsTransfer userProfileOptions;

	protected List<Long> selectUserProfiles;

	protected OptionsTransfer localUserGroupOptions;

	protected List<Long> localUserGroupIds;
	
	protected OptionsTransfer hiveApLocalUserGroupOptions;
	protected List<Long> hiveApLocalUserGroupIds;

//	private boolean enableCwpSelect = false;

	private boolean enableOsDection = false;

	private boolean blnMacAuth;

	private int keyManagement;

	private Long editMacFilterId;
	private Long editScheduleId;
	private Long editSelectUserProfileId;
	private Long editLocalUserGroupId;
	private Long editLocalUserGroupIdForRadius;
	
	private boolean isHiveApADServer = false ;
	
//	private JSONObject jsonObject = null;
//	protected JSONArray jsonArray = null;

//	public String getJSONString() {
//		if (jsonArray == null) {
//			return jsonObject.toString();
//		} else {
//			return jsonArray.toString();
//		}
//	}

    public boolean isHiveApADServer() {
        return isHiveApADServer;
    }

    public void setHiveApADServer(boolean isHiveApADServer) {
        this.isHiveApADServer = isHiveApADServer;
    }

	public Long getAsRuleGroup() {
		return asRuleGroup;
	}

	public void setAsRuleGroup(Long asRuleGroup) {
		this.asRuleGroup = asRuleGroup;
	}

	public Long getDosId() {
		return dosId;
	}

	public void setDosId(Long dosId) {
		this.dosId = dosId;
	}

	public Long getSsidDos() {
		return ssidDos;
	}

	public void setSsidDos(Long ssidDos) {
		this.ssidDos = ssidDos;
	}

	public Long getStationDos() {
		return stationDos;
	}

	public void setStationDos(Long stationDos) {
		this.stationDos = stationDos;
	}

	public Long getIpDos() {
		return ipDos;
	}

	public void setIpDos(Long ipDos) {
		this.ipDos = ipDos;
	}

	public void prepareGetSsidSecurity() {

		if (getDataSource().getSsidSecurity() != null) {

			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
				if (getDataSource().getSsidSecurity().getRekeyPeriod() != 0) {
					setEnabledRekeyPeriod2(true);
					setRekeyPeriod2(getDataSource().getSsidSecurity().getRekeyPeriod());
				} else {
					setEnabledRekeyPeriod2(false);
					setRekeyPeriod2(0);
				}
				if (getDataSource().getSsidSecurity().getRekeyPeriodGMK() != 0) {
					setEnabledRekeyPeriodGMK(true);
					setRekeyPeriodGMK(getDataSource().getSsidSecurity().getRekeyPeriodGMK());
				} else {
					setEnabledRekeyPeriodGMK(false);
					setRekeyPeriodGMK(0);
				}
//				setRekeyPeriod(getDataSource().getSsidSecurity().getRekeyPeriod());
//				setRekeyPeriodGMK(getDataSource().getSsidSecurity().getRekeyPeriodGMK());
				
				setPtkTimeOut(getDataSource().getSsidSecurity().getPtkTimeOut());
				setPtkRetries(getDataSource().getSsidSecurity().getPtkRetries());
				setGtkTimeOut(getDataSource().getSsidSecurity().getGtkTimeOut());
				setGtkRetries(getDataSource().getSsidSecurity().getGtkRetries());
				setEnable80211w(getDataSource().getSsidSecurity().isEnable80211w());
				setWpa2mfpType(getDataSource().getSsidSecurity().getWpa2mfpType());
				if (getDataSource().getSsidSecurity().getRekeyPeriodPTK() != 0) {
					setEnabledRekeyPeriodPTK(true);
					setRekeyPeriodPTK(getDataSource().getSsidSecurity().getRekeyPeriodPTK());
				} else {
					setEnabledRekeyPeriodPTK(false);
					setRekeyPeriodPTK(0);
				}
				if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
						|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
						|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
					if (getDataSource().getSsidSecurity().getReauthInterval() != 0) {
						setEnabledReauthInterval(true);
						setReauthInterval(getDataSource().getSsidSecurity().getReauthInterval());
					} else {
						setEnabledReauthInterval(false);
						setReauthInterval(0);
					}
				}
				setReplayWindow(getDataSource().getSsidSecurity().getReplayWindow());
			}

			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
				setStrict2(getDataSource().getSsidSecurity().getStrict());
			} else if (getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_OPEN
					&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WEP_PSK
					&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				setStrict1(getDataSource().getSsidSecurity().getStrict());
			}

			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
				setKeyType3(getDataSource().getSsidSecurity().getKeyType());
				if (keyType3 == 1) {
					setFirstKeyValue0_1(getDataSource().getSsidSecurity().getFirstKeyValue());
				} else {
					setFirstKeyValue0(getDataSource().getSsidSecurity().getFirstKeyValue());
				}
				if (getDataSource().getSsidSecurity().getPskUserLimit() != 0) {
					setEnabledPskUserLimit(true);
					setPskUserLimit(getDataSource().getSsidSecurity().getPskUserLimit());
				} else {
					setEnabledPskUserLimit(false);
					setPskUserLimit(0);
				}
			}

			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
					&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
				setKeyType4(getDataSource().getSsidSecurity().getKeyType());
				setDefaultKeyIndex4(getDataSource().getSsidSecurity().getDefaultKeyIndex());
				if (keyType4 == 1) {
					setFirstKeyValue1_1(getDataSource().getSsidSecurity().getFirstKeyValue());
					setSecondKeyValue1_1(getDataSource().getSsidSecurity().getSecondKeyValue());
					setThirdKeyValue1_1(getDataSource().getSsidSecurity().getThirdKeyValue());
					setFourthValue1_1(getDataSource().getSsidSecurity().getFourthValue());
				} else {
					setFirstKeyValue1(getDataSource().getSsidSecurity().getFirstKeyValue());
					setSecondKeyValue1(getDataSource().getSsidSecurity().getSecondKeyValue());
					setThirdKeyValue1(getDataSource().getSsidSecurity().getThirdKeyValue());
					setFourthValue1(getDataSource().getSsidSecurity().getFourthValue());
				}

			}

			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
					&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
				setKeyType5(getDataSource().getSsidSecurity().getKeyType());
				setDefaultKeyIndex5(getDataSource().getSsidSecurity().getDefaultKeyIndex());
				if (keyType5 == 1) {
					setFirstKeyValue2_1(getDataSource().getSsidSecurity().getFirstKeyValue());
					setSecondKeyValue2_1(getDataSource().getSsidSecurity().getSecondKeyValue());
					setThirdKeyValue2_1(getDataSource().getSsidSecurity().getThirdKeyValue());
					setFourthValue2_1(getDataSource().getSsidSecurity().getFourthValue());
				} else {
					setFirstKeyValue2(getDataSource().getSsidSecurity().getFirstKeyValue());
					setSecondKeyValue2(getDataSource().getSsidSecurity().getSecondKeyValue());
					setThirdKeyValue2(getDataSource().getSsidSecurity().getThirdKeyValue());
					setFourthValue2(getDataSource().getSsidSecurity().getFourthValue());
				}

			}
			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				setRekeyPeriod(getDataSource().getSsidSecurity().getRekeyPeriod());
			}
		}
	}

	public void prepareSetSsidSecurity() {

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
			if (enabledRekeyPeriod2) {
				getDataSource().getSsidSecurity().setRekeyPeriod(rekeyPeriod2);
			} else {
				getDataSource().getSsidSecurity().setRekeyPeriod(0);
			}
			if (enabledRekeyPeriodGMK) {
				getDataSource().getSsidSecurity().setRekeyPeriodGMK(rekeyPeriodGMK);
			} else {
				getDataSource().getSsidSecurity().setRekeyPeriodGMK(0);
			}

			getDataSource().getSsidSecurity().setPtkTimeOut(ptkTimeOut);
			getDataSource().getSsidSecurity().setPtkRetries(ptkRetries);
			getDataSource().getSsidSecurity().setGtkTimeOut(gtkTimeOut);
			getDataSource().getSsidSecurity().setGtkRetries(gtkRetries);
			if (enabledRekeyPeriodPTK) {
				getDataSource().getSsidSecurity().setRekeyPeriodPTK(rekeyPeriodPTK);
			} else {
				getDataSource().getSsidSecurity().setRekeyPeriodPTK(0);
			}

			getDataSource().getSsidSecurity().setReplayWindow(replayWindow);
			if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
				getDataSource().getSsidSecurity().setStrict(strict2);
			} else {
				getDataSource().getSsidSecurity().setStrict(strict1);
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
			if (enabledReauthInterval) {
				getDataSource().getSsidSecurity().setReauthInterval(reauthInterval);
			} else {
				getDataSource().getSsidSecurity().setReauthInterval(0);
			}
		} else {
			getDataSource().getSsidSecurity().setReauthInterval(0);
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
			getDataSource().getSsidSecurity().setKeyType(keyType3);
			if (keyType3 == 0) {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue0);
			} else {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue0_1);
			}

			if (enabledPskUserLimit) {
				getDataSource().getSsidSecurity().setPskUserLimit(pskUserLimit);
			} else {
				getDataSource().getSsidSecurity().setPskUserLimit(0);
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			getDataSource().getSsidSecurity().setKeyType(keyType4);
			getDataSource().getSsidSecurity().setDefaultKeyIndex(defaultKeyIndex4);
			if (keyType4 == 0) {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue1);
				getDataSource().getSsidSecurity().setSecondKeyValue(secondKeyValue1);
				getDataSource().getSsidSecurity().setThirdKeyValue(thirdKeyValue1);
				getDataSource().getSsidSecurity().setFourthValue(fourthValue1);
			} else {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue1_1);
				getDataSource().getSsidSecurity().setSecondKeyValue(secondKeyValue1_1);
				getDataSource().getSsidSecurity().setThirdKeyValue(thirdKeyValue1_1);
				getDataSource().getSsidSecurity().setFourthValue(fourthValue1_1);
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			getDataSource().getSsidSecurity().setKeyType(keyType5);
			getDataSource().getSsidSecurity().setDefaultKeyIndex(defaultKeyIndex5);
			if (keyType5 == 0) {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue2);
				getDataSource().getSsidSecurity().setSecondKeyValue(secondKeyValue2);
				getDataSource().getSsidSecurity().setThirdKeyValue(thirdKeyValue2);
				getDataSource().getSsidSecurity().setFourthValue(fourthValue2);
			} else {
				getDataSource().getSsidSecurity().setFirstKeyValue(firstKeyValue2_1);
				getDataSource().getSsidSecurity().setSecondKeyValue(secondKeyValue2_1);
				getDataSource().getSsidSecurity().setThirdKeyValue(thirdKeyValue2_1);
				getDataSource().getSsidSecurity().setFourthValue(fourthValue2_1);
			}
		}
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			getDataSource().getSsidSecurity().setRekeyPeriod(rekeyPeriod);
		}
		
		getDataSource().getSsidSecurity().setEnable80211w(enable80211w);
		if(enable80211w){
			getDataSource().getSsidSecurity().setWpa2mfpType(wpa2mfpType);
		}else{
			getDataSource().getSsidSecurity().setWpa2mfpType(1);
		}
	}

	protected void prepareMDMParameterProfiles(){
		getMdmParameterProfiles();

	}

	protected List<CheckItem> getMdmParameterProfiles() {
		configmdmidParameterProfiles = getBoCheckItems("policyname", ConfigTemplateMdm.class, null);
	return configmdmidParameterProfiles;
	}
	
	protected void prepareDosParameterProfiles() {
		macDosParameterProfiles = getDosParameterProfiles(DosType.MAC);
		stationDosParameterProfiles = getDosParameterProfiles(DosType.MAC_STATION);
		ipDosParameterProfiles = getDosParameterProfiles(DosType.IP);
	}

	public String getCwpsJSONStr() {
        return cwpsJSONStr;
    }

    public String getIdmCwpsJSONStr() {
        return idmCwpsJSONStr;
    }

    public String getAuthCwpsJSONStr() {
        return authCwpsJSONStr;
    }

    private String cwpsJSONStr;
	private String idmCwpsJSONStr;
	private String authCwpsJSONStr;
	
	public void prepareCwpProfiles() {
		if (isEasyMode()){
		    if(getDataSource().isEnabledIDM()) {
		        initNormalCwpsJSON();
		        
		        if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
		              initAuthCWPsJSON();
		              
		              initOpenCwpsJSON();
		        } else {
		            initOpenCwpsJSON();
		            
		            initAuthCWPsJSON();
		        }
		    } else {
		        initAuthCWPsJSON();
		        
		        initOpenCwpsJSON();
		        
		        initNormalCwpsJSON();
		    }
		} else {
			list_cwp = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
					"registrationType!=:s1",
					new Object[] {Cwp.REGISTRATION_TYPE_PPSK}),CHECK_ITEM_BEGIN_BLANK,
					CHECK_ITEM_END_NO);
		}
		
		list_ppskECwp = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
				"registrationType =:s1",
				new Object[] { Cwp.REGISTRATION_TYPE_PPSK}),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

    private void initNormalCwpsJSON() {
        list_cwp = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
                "registrationType!=:s1 and registrationType!=:s2 and registrationType!=:s3",
                new Object[] { Cwp.REGISTRATION_TYPE_EXTERNAL, Cwp.REGISTRATION_TYPE_BOTH,
                        Cwp.REGISTRATION_TYPE_PPSK}),
                        CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
        cwpsJSONStr = buildCWPsJSON(list_cwp);
    }

    private void initOpenCwpsJSON() {
        list_cwp = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
                "(registrationType=:s1 or registrationType=:s2)",
                new Object[] { Cwp.REGISTRATION_TYPE_AUTHENTICATED, Cwp.REGISTRATION_TYPE_EULA}),
                CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
        idmCwpsJSONStr = buildCWPsJSON(list_cwp);
    }

    private void initAuthCWPsJSON() {
        list_cwp = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
                "(registrationType=:s1)",
                new Object[] { Cwp.REGISTRATION_TYPE_AUTHENTICATED}),
                CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
        authCwpsJSONStr = buildCWPsJSON(list_cwp);
    }

    private String buildCWPsJSON(List<CheckItem> list_cwp) {
        if(null != list_cwp && !list_cwp.isEmpty()) {
            String[] jsonItem = new String[list_cwp.size()];
            int index = 0;
            for (CheckItem item : list_cwp) {
                jsonItem[index++] = "{'key' : '" + item.getId()+ "', 'value' : '" + item.getValue() + "'}";
            }
            return Arrays.toString(jsonItem);
        }
        return null;
    }

	public void prepareRadiusProfiles() {
		list_radius = getBoCheckItems("radiusName", RadiusAssignment.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public void prepareUserPolicyProfiles() {
		userPolicyProfiles = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
				"registrationType", Cwp.REGISTRATION_TYPE_EULA), CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
		
//		userPolicyProfiles = getBoCheckItems("cwpName", Cwp.class, new FilterParams(
//				"(registrationType=:s1 or registrationType=:s2)", 
//				new Object[]{Cwp.REGISTRATION_TYPE_EULA, Cwp.REGISTRATION_TYPE_EXTERNAL}),
//				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public void prepareUserProfiles() {
		availableUserProfile = getBoCheckItems("userProfileName", UserProfile.class, null);
//		availableUserProfile = getUserProfileBoCheckItems();
	}

	protected List<CheckItem> getDosParameterProfiles(DosType dosType) {
		return getBoCheckItems("dosPreventionName", DosPrevention.class, new FilterParams(
				"dosType", dosType));
	}

	public void prepareLocalUserGroups() {
		Object lstCondition[] = new Object[1];
		lstCondition[0] = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
		List<CheckItem> availableFilters = getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType!=:s1", lstCondition));
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (LocalUserGroup localUserGroup : getDataSource().getLocalUserGroups()) {
				if (localUserGroup.getGroupName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);

		localUserGroupOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.configTemplate.wizard.avaliablePrivatePsk"), MgrUtil
				.getUserMessage("config.configTemplate.wizard.selectPrivatePsk"), availableFilters,
				getDataSource().getLocalUserGroups(), "id", "value", "localUserGroupIds", 512,
				"250px", "6", true, "LocalUserGroup");
	}

	protected void setSelectedLocalUserGroups() throws Exception {

		Set<LocalUserGroup> ssidLocalUserGroups = getDataSource().getLocalUserGroups();
		ssidLocalUserGroups.clear();
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			if (localUserGroupIds != null) {
				for (Long filterId : localUserGroupIds) {
					LocalUserGroup localUserGroup = findBoById(
							LocalUserGroup.class, filterId);
					if (localUserGroup != null) {
						ssidLocalUserGroups.add(localUserGroup);
					}
				}
			}
			getDataSource().setLocalUserGroups(ssidLocalUserGroups);
		} else {
			getDataSource().setLocalUserGroups(ssidLocalUserGroups);
		}
	}
	
	public void prepareRadiusLocalUserGroups() {
//		Object lstCondition[] = new Object[1];
//		lstCondition[0] = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
		List<CheckItem> availableFilters = getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType", LocalUserGroup.USERGROUP_USERTYPE_RADIUS));
//		List<CheckItem> removeList = new ArrayList<CheckItem>();
//
//		for (CheckItem oneItem : availableFilters) {
//			for (LocalUserGroup localUserGroup : getDataSource().getLocalUserGroups()) {
//				if (localUserGroup.getGroupName().equals(oneItem.getValue())) {
//					removeList.add(oneItem);
//				}
//			}
//		}
//		availableFilters.removeAll(removeList);

		hiveApLocalUserGroupOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.radiusOnHiveAp.availabel.group"), MgrUtil
				.getUserMessage("config.radiusOnHiveAp.selected.group"), availableFilters,
				new ArrayList<CheckItem>(), "id", "value", "hiveApLocalUserGroupIds", 512,
				"250px", "6", true, "LocalUserGroupForRadius");
	}

	public void prepareAvailableServiceFilters() {
		serviceFilterProfiles = getBoCheckItems("filterName", ServiceFilter.class, null);
	}

	public void prepareAirscreenRuleGroups() {
		asRuleGroupList = getBoCheckItems("profileName", AirScreenRuleGroup.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);
	}

	public void prepareAvailableFilters() throws Exception {
		List<CheckItem> availableFilters = getBoCheckItems("filterName", MacFilter.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (MacFilter savedMac : getDataSource().getMacFilters()) {
				if (savedMac.getFilterName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}
		availableFilters.removeAll(removeList);

		// For the OptionsTransfer component
		macFilterOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ssid.availableMacFilters"), MgrUtil
				.getUserMessage("config.ssid.selectedMacFilters"), availableFilters,
				getDataSource().getMacFilters(), "id", "value", "macFilters", 0, "250px", null,
				false, "MacFilter");
	}

	protected void setSelectedMacFilters() throws Exception {

		Set<MacFilter> ssidMacFilters = getDataSource().getMacFilters();
		ssidMacFilters.clear();
		if (macFilters != null) {

			for (Long filterId : macFilters) {
				MacFilter macFilter = findBoById(MacFilter.class, filterId);
				if (macFilter != null) {
					ssidMacFilters.add(macFilter);
				}
			}
			if (ssidMacFilters.size() != macFilters.size()) {
				String tempStr[] = { getText("config.ssid.selectedMacFilters") };
				addActionError(getText("info.ssid.warning.deleteRecord", tempStr));
			}
		}
		getDataSource().setMacFilters(ssidMacFilters);
		log.info("setSelectedMacFilters", "SSID " + getDataSource().getSsidName() + " has "
				+ ssidMacFilters.size() + " MAC filters.");
	}

	protected void setSelectedSchedulers() throws Exception {
		Set<Scheduler> ssidSchedulers = getDataSource().getSchedulers();
		ssidSchedulers.clear();
		if (schedulers != null) {

			for (Long schedulersId : schedulers) {
				Scheduler scheduler = findBoById(Scheduler.class, schedulersId);
				if (scheduler != null) {
					ssidSchedulers.add(scheduler);
				}
			}

			if (ssidSchedulers.size() != schedulers.size()) {
				String tempStr[] = { getText("config.ssid.selectedSchedulers") };
				addActionError(getText("info.ssid.warning.deleteRecord", tempStr));
			}
		}
		getDataSource().setSchedulers(ssidSchedulers);
		log.info("setSelectedSchedulers", "SSID " + getDataSource().getSsidName() + " has "
				+ ssidSchedulers.size() + " Schedulers.");
	}

	public void prepareAvailableSchedules() throws Exception {
		List<CheckItem> availableFilters = getBoCheckItems("schedulerName", Scheduler.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (Scheduler savedScheduler : getDataSource().getSchedulers()) {
				if (savedScheduler.getSchedulerName().equals(oneItem.getValue())) {
					removeList.add(oneItem);
				}
			}
		}

		availableFilters.removeAll(removeList);
		// For the OptionsTransfer component
		schedulerOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ssid.availableSchedulers"), MgrUtil
				.getUserMessage("config.ssid.selectedSchedulers"), availableFilters,
				getDataSource().getSchedulers(), "id", "value", "schedulers", 8, "250px", null,
				false, "Scheduler");
	}
	

//	public List<CheckItem> getUserProfileBoCheckItems() {
//		// get list of id and name from database
//		String sql = "SELECT bo.id, bo.userProfileName, bo.guestAccess" + " FROM "
//				+ UserProfile.class.getSimpleName() + " bo";
//		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("id"),
//				null, getDomain().getId());
//		List<CheckItem> items = new ArrayList<CheckItem>();
//		for (Object obj : bos) {
//			Object[] item = (Object[]) obj;
//			String profileName = (String) item[1];
//			boolean guessAcess = (Boolean) item[2];
//			CheckItem checkItem;
//			if (guessAcess){
//				checkItem = new CheckItem((Long) item[0], profileName + " (Guest Access)");
//			} else {
//				checkItem = new CheckItem((Long) item[0], profileName);
//			}
//			items.add(checkItem);
//		}
//
//		if (items.isEmpty()) {
//			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
//					.getUserMessage("config.optionsTransfer.none")));
//		}
//		return items;
//	}

	public void prepareRadiusUserProfile() {
		List<CheckItem> availableFilters = getBoCheckItems("userProfileName", UserProfile.class,
				null);
//		List<CheckItem> availableFilters = getUserProfileBoCheckItems();
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : availableFilters) {
			for (UserProfile savedUserProfile : getDataSource().getRadiusUserProfile()) {
				if (savedUserProfile.getId().equals(oneItem.getId())) {
					removeList.add(oneItem);
				}
			}
		}
		if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && isFullMode()){
			if (getDataSource().getUserProfileDefault()!=null) {
				removeList.add(new CheckItem(
						getDataSource().getUserProfileDefault().getId(),
						getDataSource().getUserProfileDefault().getUserProfileName()));
				boolean needAddNew = true;
				for(UserProfile upro: getDataSource().getRadiusUserProfile()){
					if (upro.getId().equals(getDataSource().getUserProfileDefault().getId())) {
						needAddNew=false;
						break;
					}
				}
				if (needAddNew){
					getDataSource().getRadiusUserProfile().add(getDataSource().getUserProfileDefault());
				}
			}
		}
		availableFilters.removeAll(removeList);
		
		userProfileOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.configTemplate.wizard.avaliableUserProfile"), MgrUtil
				.getUserMessage("config.configTemplate.wizard.selectUserProfile"),
				availableFilters, getDataSource().getRadiusUserProfile(), "id", "value",
				"selectUserProfiles", 62, "250px", null, true, "UserProfileRadius");
	}

	protected void setSelectedRadiusUserProfils() throws Exception {
		Set<UserProfile> ssidUserProfiles = getDataSource().getRadiusUserProfile();
		ssidUserProfiles.clear();
		if (selectUserProfiles != null) {
			for (Long filterId : selectUserProfiles) {
				if (userProfileSelfRegId != null && userProfileSelfRegId.equals(filterId)) {
					continue;
				}
				if (userProfileDefaultId != null && userProfileDefaultId.equals(filterId)) {
					continue;
				}
				UserProfile userProfile = findBoById(UserProfile.class, filterId);
				if (userProfile != null) {
					ssidUserProfiles.add(userProfile);
				}
			}
		}
		getDataSource().setRadiusUserProfile(ssidUserProfiles);
	}

	protected ConfigTemplate resetConfigTemplateQos(ConfigTemplate modifyConfigTemplate,
			SsidProfile currentSsid)
			throws Exception {
		for (ConfigTemplateSsid templateSsid : modifyConfigTemplate.getSsidInterfaces().values()) {
			if (templateSsid.getSsidProfile() != null) {
				if (templateSsid.getSsidProfile().getId().equals(currentSsid.getId())) {
					templateSsid.setSsidProfile(currentSsid);
					templateSsid.setInterfaceName(currentSsid.getSsidName());
					break;
				}
			}
		}
		return modifyConfigTemplate;
	}

	/**
	 * Get the from changed session for sub profile
	 * 
	 * @author Yunzhi Lin
	 * - Time: Apr 1, 2011 10:01:37 AM
	 * @return -
	 */
	public boolean getFormChangedSession() {
		Object obj = MgrUtil.getSessionAttribute(GUIDED_CONFIG_FORM_CHANGE);
		MgrUtil.removeSessionAttribute(GUIDED_CONFIG_FORM_CHANGE);
		return null != obj;
	}
	
	public SsidProfile getDataSource() {
		return (SsidProfile) dataSource;
	}

	public Range getDtimSettingRange() {
		return getAttributeRange("dtimSetting");
	}
	
	public Range getClientAgeOutRange() {
		return getAttributeRange("clientAgeOut");
	}

	public Range getRtsThresholdRange() {
		return getAttributeRange("rtsThreshold");
	}

	public Range getFragThresholdRange() {
		return getAttributeRange("fragThreshold");
	}

	public Range getUpdateIntervalRange() {
		return getAttributeRange("updateInterval");
	}

	public Range getAgeOutRange() {
		return getAttributeRange("ageOut");
	}

	public Range getMaxClientRange() {
		return getAttributeRange("maxClient");
	}

	public Range getLocalCacheTimeoutRange() {
		return getAttributeRange("localCacheTimeout");
	}

	public int getSsidNameLength() {
		if (isFullMode()) {
			return getAttributeLength("ssidName");
		} else {
			return 28;
		}
	}

	public int getCommentLength() {
		return getAttributeLength("comment");
	}

	protected Set<CheckItem> referenceWLAN;
	// protected ArrayList<String> referenceMessage;
	protected String strErrorMessage;
	// protected Long wlanId;

	// protected int showMessagePanel=0;

	protected int rekeyPeriod = 600;
	protected int rekeyPeriod2 = 0;
	
	protected int rekeyPeriodGMK = 0;

	protected int rekeyPeriodPTK = 0;
	protected int reauthInterval = 0;
	protected int pskUserLimit = 0;
	protected boolean enabledRekeyPeriod2 = false;
	protected boolean enabledRekeyPeriodGMK = false;
	protected boolean enabledRekeyPeriodPTK = false;
	protected boolean enabledReauthInterval = false;
	protected boolean enabledPskUserLimit = false;
	
	protected int ptkTimeOut = 4000;

	protected int ptkRetries = 3;

	protected int gtkTimeOut = 4000;

	protected int gtkRetries = 3;

	private int replayWindow = 0;

	protected boolean strict1 = true;

	protected boolean strict2 = true;

	protected int keyType3 = 0;

	protected int keyType4 = 0;

	protected int keyType5 = 0;

	protected int defaultKeyIndex4 = 0;

	protected int defaultKeyIndex5 = 0;

	protected String firstKeyValue0 = "";

	protected String firstKeyValue1 = "";

	protected String secondKeyValue1 = "";

	protected String thirdKeyValue1 = "";

	protected String fourthValue1 = "";

	protected String firstKeyValue2 = "";

	protected String secondKeyValue2 = "";

	protected String thirdKeyValue2 = "";

	protected String fourthValue2 = "";

	protected String firstKeyValue0_1 = "";

	protected String firstKeyValue1_1 = "";

	protected String secondKeyValue1_1 = "";

	protected String thirdKeyValue1_1 = "";

	protected String fourthValue1_1 = "";

	protected String firstKeyValue2_1 = "";

	protected String secondKeyValue2_1 = "";

	protected String thirdKeyValue2_1 = "";

	protected String fourthValue2_1 = "";
	
	private boolean enable80211w = false;
	
	private int wpa2mfpType = 1;


	public boolean isEnable80211w() {
		return enable80211w;
	}

	public void setEnable80211w(boolean enable80211w) {
		this.enable80211w = enable80211w;
	}

	public int getWpa2mfpType() {
		return wpa2mfpType;
	}

	public void setWpa2mfpType(int wpa2mfpType) {
		this.wpa2mfpType = wpa2mfpType;
	}

	public boolean getStrict1() {
		return strict1;
	}

	public void setStrict1(boolean strict1) {
		this.strict1 = strict1;
	}

	public boolean getStrict2() {
		return strict2;
	}

	public void setStrict2(boolean strict2) {
		this.strict2 = strict2;
	}

	public int getKeyType3() {
		return keyType3;
	}

	public void setKeyType3(int keyType3) {
		this.keyType3 = keyType3;
	}

	public int getKeyType4() {
		return keyType4;
	}

	public void setKeyType4(int keyType4) {
		this.keyType4 = keyType4;
	}

	public int getKeyType5() {
		return keyType5;
	}

	public void setKeyType5(int keyType5) {
		this.keyType5 = keyType5;
	}

	public int getDefaultKeyIndex4() {
		return defaultKeyIndex4;
	}

	public void setDefaultKeyIndex4(int defaultKeyIndex4) {
		this.defaultKeyIndex4 = defaultKeyIndex4;
	}

	public int getDefaultKeyIndex5() {
		return defaultKeyIndex5;
	}

	public void setDefaultKeyIndex5(int defaultKeyIndex5) {
		this.defaultKeyIndex5 = defaultKeyIndex5;
	}

	public String getFirstKeyValue0() {
		return firstKeyValue0;
	}

	public void setFirstKeyValue0(String firstKeyValue0) {
		this.firstKeyValue0 = firstKeyValue0;
	}

	public String getFirstKeyValue1() {
		return firstKeyValue1;
	}

	public void setFirstKeyValue1(String firstKeyValue1) {
		this.firstKeyValue1 = firstKeyValue1;
	}

	public String getSecondKeyValue1() {
		return secondKeyValue1;
	}

	public void setSecondKeyValue1(String secondKeyValue1) {
		this.secondKeyValue1 = secondKeyValue1;
	}

	public String getThirdKeyValue1() {
		return thirdKeyValue1;
	}

	public void setThirdKeyValue1(String thirdKeyValue1) {
		this.thirdKeyValue1 = thirdKeyValue1;
	}

	public String getFourthValue1() {
		return fourthValue1;
	}

	public void setFourthValue1(String fourthValue1) {
		this.fourthValue1 = fourthValue1;
	}

	public String getFirstKeyValue2() {
		return firstKeyValue2;
	}

	public void setFirstKeyValue2(String firstKeyValue2) {
		this.firstKeyValue2 = firstKeyValue2;
	}

	public String getSecondKeyValue2() {
		return secondKeyValue2;
	}

	public void setSecondKeyValue2(String secondKeyValue2) {
		this.secondKeyValue2 = secondKeyValue2;
	}

	public String getThirdKeyValue2() {
		return thirdKeyValue2;
	}

	public void setThirdKeyValue2(String thirdKeyValue2) {
		this.thirdKeyValue2 = thirdKeyValue2;
	}

	public String getFourthValue2() {
		return fourthValue2;
	}

	public void setFourthValue2(String fourthValue2) {
		this.fourthValue2 = fourthValue2;
	}

	public String getFirstKeyValue0_1() {
		return firstKeyValue0_1;
	}

	public void setFirstKeyValue0_1(String firstKeyValue0_1) {
		this.firstKeyValue0_1 = firstKeyValue0_1;
	}

	public String getFirstKeyValue1_1() {
		return firstKeyValue1_1;
	}

	public void setFirstKeyValue1_1(String firstKeyValue1_1) {
		this.firstKeyValue1_1 = firstKeyValue1_1;
	}

	public String getSecondKeyValue1_1() {
		return secondKeyValue1_1;
	}

	public void setSecondKeyValue1_1(String secondKeyValue1_1) {
		this.secondKeyValue1_1 = secondKeyValue1_1;
	}

	public String getThirdKeyValue1_1() {
		return thirdKeyValue1_1;
	}

	public void setThirdKeyValue1_1(String thirdKeyValue1_1) {
		this.thirdKeyValue1_1 = thirdKeyValue1_1;
	}

	public String getFourthValue1_1() {
		return fourthValue1_1;
	}

	public void setFourthValue1_1(String fourthValue1_1) {
		this.fourthValue1_1 = fourthValue1_1;
	}

	public String getFirstKeyValue2_1() {
		return firstKeyValue2_1;
	}

	public void setFirstKeyValue2_1(String firstKeyValue2_1) {
		this.firstKeyValue2_1 = firstKeyValue2_1;
	}

	public String getSecondKeyValue2_1() {
		return secondKeyValue2_1;
	}

	public void setSecondKeyValue2_1(String secondKeyValue2_1) {
		this.secondKeyValue2_1 = secondKeyValue2_1;
	}

	public String getThirdKeyValue2_1() {
		return thirdKeyValue2_1;
	}

	public void setThirdKeyValue2_1(String thirdKeyValue2_1) {
		this.thirdKeyValue2_1 = thirdKeyValue2_1;
	}

	public String getFourthValue2_1() {
		return fourthValue2_1;
	}

	public void setFourthValue2_1(String fourthValue2_1) {
		this.fourthValue2_1 = fourthValue2_1;
	}

	public Long getCwpId() {
		return cwpId;
	}

	public void setCwpId(Long cwpId) {
		this.cwpId = cwpId;
	}
	
	public Long getPpskECwpId(){
		return this.ppskECwpId;
	}
	
	public void setPpskECwpId(Long ppskECwpId){
		this.ppskECwpId = ppskECwpId;
	}
	public Long getWpaECwpId() {
		return wpaECwpId;
	}
	public void setWpaECwpId(Long wpaECwpId) {
		this.wpaECwpId = wpaECwpId;
	}

//	public boolean getEnableCwpSelect() {
//		return enableCwpSelect;
//	}
//
//	public void setEnableCwpSelect(boolean enableCwpSelect) {
//		this.enableCwpSelect = enableCwpSelect;
//	}

	public boolean getEnableOsDection() {
		return enableOsDection;
	}

	public void setEnableOsDection(boolean enableOsDection) {
		this.enableOsDection = enableOsDection;
	}

	public List<CheckItem> getList_cwp() {
		return list_cwp;
	}
	
	public List<CheckItem> getList_ppskECwp(){
		return this.list_ppskECwp;
	}

	public boolean getBlnMacAuth() {
		return blnMacAuth;
	}

	public void setBlnMacAuth(boolean blnMacAuth) {
		this.blnMacAuth = blnMacAuth;
	}

	public int getKeyManagement() {
		return keyManagement;
	}

	public void setKeyManagement(int keyManagement) {
		this.keyManagement = keyManagement;
	}

	public String getChangeCwpAuthOperation() {
		return "changeCwpAuthOperation";
	}

	// public String getChangePskGroupOperation() {
	// return "changePskGroupOperation";
	// }

	public Long getRadiusId() {
		return radiusId;
	}

	public void setRadiusId(Long radiusId) {
		this.radiusId = radiusId;
	}

	public Long getUserProfileSelfRegId() {
		return userProfileSelfRegId;
	}

	public void setUserProfileSelfRegId(Long userProfileSelfRegId) {
		this.userProfileSelfRegId = userProfileSelfRegId;
	}

	public Long getUserProfileDefaultId() {
		return userProfileDefaultId;
	}

	public void setUserProfileDefaultId(Long userProfileDefaultId) {
		this.userProfileDefaultId = userProfileDefaultId;
	}

	public OptionsTransfer getUserProfileOptions() {
		return userProfileOptions;
	}

	public void setUserProfileOptions(OptionsTransfer userProfileOptions) {
		this.userProfileOptions = userProfileOptions;
	}

	public List<CheckItem> getList_radius() {
		return list_radius;
	}

	public List<CheckItem> getAvailableUserProfile() {
		return availableUserProfile;
	}

	public void setSelectUserProfiles(List<Long> selectUserProfiles) {
		this.selectUserProfiles = selectUserProfiles;
	}

	public Long getServiceFilter() {
		return serviceFilter;
	}

	public void setServiceFilter(Long serviceFilter) {
		this.serviceFilter = serviceFilter;
	}

	public List<CheckItem> getServiceFilterProfiles() {
		return serviceFilterProfiles;
	}

	protected List<CheckItem> macDosParameterProfiles;

	protected List<CheckItem> stationDosParameterProfiles;

	protected List<CheckItem> ipDosParameterProfiles;

	protected List<CheckItem> serviceFilterProfiles;
	
	protected List<CheckItem> configmdmidParameterProfiles;
	
	protected List<CheckItem> list_cwp;
	
	protected List<CheckItem> list_ppskECwp;

	protected List<CheckItem> userPolicyProfiles;

	protected List<CheckItem> list_radius;

	// protected List<CheckItem> list_localUserGroups;

	protected List<CheckItem> availableUserProfile;

	protected List<CheckItem> asRuleGroupList;
	
	protected List<CheckItem> availableIpAddress;

	protected List<CheckItem> availableVlan;

	public List<CheckItem> getMacDosParameterProfiles() {
		return macDosParameterProfiles;
	}

	public List<CheckItem> getStationDosParameterProfiles() {
		return stationDosParameterProfiles;
	}

	public List<CheckItem> getIpDosParameterProfiles() {
		return ipDosParameterProfiles;
	}
	public List<CheckItem> getConfigmdmidParameterProfiles() {
		return configmdmidParameterProfiles;
	}

	public void setConfigmdmidParameterProfiles(
			List<CheckItem> configmdmidParameterProfiles) {
		this.configmdmidParameterProfiles = configmdmidParameterProfiles;
	}

	public List<CheckItem> getAsRuleGroupList() {
		return asRuleGroupList;
	}

	private String selectDosType;

	public String getSelectDosType() {
		return selectDosType;
	}

	public void setSelectDosType(String selectDosType) {
		this.selectDosType = selectDosType;
	}

	public EnumItem[] getEnumKeyMgmt() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA
				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { 
					SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK,
					SsidProfile.KEY_MGMT_WPA_PSK, SsidProfile.KEY_MGMT_WPA2_PSK});
		} else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] {
					SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X, 
					SsidProfile.KEY_MGMT_WPA_EAP_802_1_X, SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X });
		} else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { SsidProfile.KEY_MGMT_WEP_PSK,
					SsidProfile.KEY_MGMT_DYNAMIC_WEP });
		} else {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { SsidProfile.KEY_MGMT_OPEN });
		}
	}

	public EnumItem[] getEnumRadioMode() {
		return SsidProfile.ENUM_RADIOMODE;
	}
	
	public EnumItem[] getEnumAuthSequence() {
		return SsidProfile.ENUM_AUTH_SEQUENCE;
	}

	public EnumItem[] getEnumDenyAction() {
		return SsidProfile.DENY_ACTION;
	}

	public EnumItem[] getEnumRadiusAuth() {
		return Cwp.ENUM_AUTH_METHOD;
	}

	public EnumItem[] getEnumHiveAPRadius() {
		return new EnumItem[]{new EnumItem(2, getText("config.ssid.newRadius.apRadius"))};
	}
	public EnumItem[] getEnumExternalRadius() {
		return new EnumItem[]{new EnumItem(1, getText("config.ssid.newRadius.externalRadius"))};
	}
	
	public EnumItem[] getLdapSaslWrappings() {
		return ActiveDirectoryOrOpenLdap.CLIENT_LDAP_SASL_WRAPPING;
	}

	public String getHideOsDectionNote(){
		if (getDataSource().isEnableOsDection()) {
			return "";
		}
		return "none";
	}
	
	public String getHideTkip() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_OPEN
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK) {
			return "none";
		} else if (getDataSource().getEncryption() == SsidProfile.KEY_ENC_TKIP
				|| getDataSource().getEncryption() == SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP) {
			return "";
		}
		return "none";
	}

	public String getHideRekeyPeriod() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideGmkRekeyPeriod() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_OPEN
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK) {
			return "none";
		} else {
			return "";
		}
	}

	public String getHideStrict() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_OPEN
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
			return "none";
		} else {
			return "";
		}
	}

	public String getHideAfterStrict() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideThird() {
//		if ((getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
//				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK || getDataSource()
//				.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK)
//				&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK
//				&& !getDataSource().getEnabledDefaultSetting()) {
//			return "";
//		} else {
			return "none";
//		}
	}

	public String getHideFourth() {
//		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
//				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104
//				&& !getDataSource().getEnabledDefaultSetting()) {
//			return "";
//		} else {
			return "none";
//		}
	}

	public String getHideFifth() {
//		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
//				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40
//				&& !getDataSource().getEnabledDefaultSetting()) {
//			return "";
//		} else {
			return "none";
//		}
	}

	public String getHideThird_one() {
		if ((getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK || getDataSource()
				.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK)
				&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK) {
			if (keyType3 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideThird_two() {
		if ((getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK || getDataSource()
				.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK)
				&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK) {
			if (keyType3 == 1) {
				return "";
			}
		}
		return "none";
	}

	public String getHideFourth_one() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			if (keyType4 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideFourth_two() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			if (keyType4 == 1) {
				return "";
			}
		}
		return "none";
	}

	public String getHideFifth_one() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			if (keyType5 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideFifth_two() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			if (keyType5 == 1) {
				return "";
			}
		}
		return "none";
	}
	
   public int getAccessSecurity() {
        if (getDataSource() == null) {
            return SsidProfile.ACCESS_MODE_OPEN;
        }
        return getDataSource().getAccessMode();
    }

	public String getHideEnabledDefaultSetting() {
//		if (getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_OPEN) {
//			return "";
//		}
		return "none";
	}

	public String getHideKeyManagement() {
//		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN
//				|| getDataSource().getEnabledDefaultSetting()) {
			return "none";
//		}
//		return "";
	}
	
	public String getHideKeyManagementNote() {
		if (getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WPA_EAP_802_1_X) {
			return "";
		}
		return "none";
	}

	public String getHideAuthMethord() {
//		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
//				&& getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
//				&& !getDataSource().getEnabledDefaultSetting()) {
//			return "";
//		}
		return "none";
	}

	public String getShowOption() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
			return "none";
		}
		return "";
	}

	public String getHideUseGuestManager() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK && isFullMode() && !NmsUtil.isHostedHMApplication()) {
			return "";
		}
		return "none";
	}
	
	public boolean getBlnHHMApplication(){
		return NmsUtil.isHostedHMApplication();
	}
	
	public String getHideUserInfo(){
		if (isEasyMode()) {
			return "";
		}
		return "none";
	}
	
	public String getHideUserCategory(){
		if (isEasyMode() && getDataSource().getUserCategory()!=SsidProfile.USER_CATEGORY_GUEST) {
			return "";
		}
		return "none";
	}

	public String getHidePSKDefaultUserProfile() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			if (getDataSource().getMacAuthEnabled()) {
				return "";
			}
		}
		return "none";
	}

	public String getHideRadiusPAPCHAP() {
		if (getDataSource().getEnabledUseGuestManager() || getDataSource().getMacAuthEnabled()) {
			return "";
		}
		return "none";
	}
	
	public String getHideFallBackToEcwp() {
		String ecwpServer = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION, 
				ConfigUtil.KEY_ECWPSERVER, ConfigUtil.VALUE_ECWP_DEFAULT);
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN &&
				getDataSource().getMacAuthEnabled() &&
				getDataSource().getCwp()!=null &&
				getDataSource().getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_EXTERNAL &&
				(ecwpServer.equals(ConfigUtil.VALUE_ECWP_DEPAUL) || ecwpServer.equals(ConfigUtil.VALUE_ECWP_NNU))) {
			return "";
		}
		return "none";
	}
	
    /*--------- Section for IDM -------start--*/
    public boolean isEnabledIDM() {
        boolean flag = this.usabledIDM && getDataSource().isEnabledIDM();
        if(null != getDataSource()) {
            getDataSource().setEnabledIDM(flag);
        }
        return flag;
    }
    
    private void enabledCWP4IDM() {
        if (getDataSource().isEnabledIDM()) {
            final int accessMode = getDataSource().getAccessMode();
            if (accessMode == SsidProfile.ACCESS_MODE_WPA
                    || accessMode == SsidProfile.ACCESS_MODE_OPEN
                    || (accessMode == SsidProfile.ACCESS_MODE_WEP && getDataSource()
                            .getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK)) {
                getDataSource().setCwpSelectEnabled(true);
            }
        }
    }
    /*--------- Section for IDM -------end--*/

	public String getShowAdvancePanelDiv() {
		if (getTabId() == 4) {
			return "none";
		}
		return "";
	}

	public String getHideAdvancePanelDiv() {
		if (getTabId() == 4) {
			return "";
		}
		return "none";
	}
	
	public String getHideNewRadiusPanelDiv() {
		if (getTabId() == 5) {
			return "";
		}
		return "none";
	}
	
	public String getHideNewRadiusPanelPassDiv(){
		if (getDataSource().getNewRadiusType()==Cwp.CWP_EXTERNAL){
			return "";
		}
		return "none";
	}

	public String getHideHiveApLocalUserGroupDiv(){
		if (getDataSource().getNewRadiusType()==Cwp.CWP_INTERNAL){
			return "";
		}
		return "none";
	}
	
	
	public String getHideSelfNewUserProfileDiv(){
		if (getTabId() == 6) {
			return "";
		}
		return "none";
	}
	
	public String getHideDefaultNewUserProfileDiv(){
		if (getTabId() == 7) {
			return "";
		}
		return "none";
	}
	public String getHideOptionNewUserProfileDiv(){
		if (getTabId() == 8) {
			return "";
		}
		return "none";
	}

	public String getShowScheduleDiv() {
		if (getTabId() == 3) {
			return "none";
		}
		return "";
	}

	public String getHideScheduleDiv() {
		if (getTabId() == 3) {
			return "";
		}
		return "none";
	}

	public String getShowSecurityDiv() {
		if (getTabId() == 2) {
			return "none";
		}
		return "";
	}

	public String getHideSecurityDiv() {
		if (getTabId() == 2) {
			return "";
		}
		return "none";
	}

	public String getHideEap() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			return "";
		}
		return "none";
	}

	public String getShowRadioRateDiv() {
		return "";
	}

	public String getHideRadioRateDiv() {
		return "none";
	}

	public String getHideLocalUserGroup() {
		if (isFullMode() && getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return "";
		}
		return "none";
	}

	public String getHideReauthInterval() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X) {
			return "";
		}
		return "none";
	}

	public String getHidePskUserLimit() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return "";
		}
		return "none";
	}
	
	public String getHidePskSelfReg() {
		if (isEasyMode()) {
			return "none";
		}
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return "";
		}else{
			return "none";
		}
	}
	
	public String getHidePskSelfRegAdv(){
		if (isEasyMode()) {
			return "none";
		}
		if(getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && getDataSource().isEnablePpskSelfReg()){
			return "";
		}else{
			return "none";
		}
	}
	

	public String getHide80211w() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getHideWpa2mfpType(){
		if (isEasyMode() && getDataSource().getSsidSecurity() == null) {
			return "none";
		}
		if(getDataSource().getSsidSecurity().isEnable80211w()){
			return "";
		}else{
			return "none";
		}
	}
	
	public String getHidePPskServerIpDiv(){
		if (isEasyMode()) {
			return "none";
		}
		if(getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && 
				(getDataSource().isEnablePpskSelfReg() || getDataSource().getSsidSecurity().isBlnMacBindingEnable())){
			return "";
		}else{
			return "none";
		}
	}
	
	public String getHidePpskRadiusServerTr() {
		if (isEasyMode()) {
			return "none";
		}
		if(getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && getDataSource().isEnablePpskSelfReg()){
			if (getDataSource().getPpskECwp()!=null &&
					getDataSource().getPpskECwp().getPpskServerType()==Cwp.PPSK_SERVER_TYPE_AUTH) {
				return "";
			}
		}
		return "none";
	}

	public String getHideCwpSelect() {
		if (getDataSource().isCwpSelectEnabled()) {
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				return "none";
			}
			return "";
		}
		return "none";
	}
	
	public String getHideUserPolicyDiv() {
		if (getDataSource().isCwpSelectEnabled()) {
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
					|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X
					|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
				return "";
			}
			return "none";
		}
		return "none";

	}

	public String getHideOsDetectionPolicy() {
		if (enableOsDection) {
			return "";
		}
		return "none";
	}

//	public String getHideAllCwp() {
//		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
//				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X
//				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
//			return "none";
//		}
//		return "";
//	}

	public String getHideRadius() {
		if (isEasyMode()){
			return "none";
		}
		if (getDataSource().getMacAuthEnabled()
				|| getDataSource().getEnabledUseGuestManager()
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			return "";
		} else {
			if (getDataSource().getCwp() != null) {
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
						|| getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
					return "";
				}
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL){
					//if (!getDataSource().getCwp().isPpskServer()){
						return "";
					//}
				}
			}
			return "none";
		}
	}
	
	public String getHideSimpleRadius(){
		if (isFullMode()){
			return "none";
		}
		if (getDataSource().getMacAuthEnabled() || getDataSource().getEnabledUseGuestManager()
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			return "";
		} else {
			if (getDataSource().getCwp() != null) {
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED) {
					return "";
				}
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL){
					//if (!getDataSource().getCwp().isPpskServer()){
						return "";
					//}
				}
			}
			return "none";
		}
	}
	
	public String getHideCreateLocal(){
		if (isFullMode()){
			return "none";
		}
		if (((getDataSource().getMacAuthEnabled() || getDataSource().getEnabledUseGuestManager()
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) 
				&& getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL && !getDataSource().isEnableADIntegration())
				|| getDataSource().getAccessMode()== SsidProfile.ACCESS_MODE_PSK) {
			return "";
		} else {
			if (getDataSource().getCwp() != null) {
				if ((getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED ||
						getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL)
						&& getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL
						&& !getDataSource().isEnableADIntegration()) {
					
					return "";
				}
			}
			return "none";
		}
	}

	public String getHideRadiusOrPSK() {
		if (getHideRadius().equals("")
				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return "";
		}
		return "none";
	}

	public String getHideRadiusOnly() {
		String retStr = getHideRadius();
		if (retStr.equals("")) {
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
					&& getDataSource().getMacAuthEnabled()) {
				return "none";
			}
		}
		return retStr;
	}

	public String getHidePSKUserMessage() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				&& !getDataSource().getEnabledUseGuestManager()) {
			return "";
		}
		return "none";
	}

	public String getHideSelfReg() {
		if (getDataSource().getCwp() != null) {
			if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED ||
					getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA ||
					getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideOpenWep() {
		if (getDataSource().getMacAuthEnabled()
				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			return "none";
		} else {
			if (getDataSource().getCwp() == null 
					|| getDataSource().getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_EULA) {
				return "";
			} 
		}
		return "none";
	}

	public String getHideOpenWepRadius() {
		if (getDataSource().getCwp() != null) {
			if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
					&& !getDataSource().getMacAuthEnabled()) {
				return "none";
			} else {
				return "";
			}
		} else {
			if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
					&& !getDataSource().getMacAuthEnabled()
					&& !getDataSource().getEnabledUseGuestManager()) {
				return "none";
			}
			return "";
		}
	}

	public String getHideAction() {
		if (getDataSource().getChkUserOnly()) {
			return "";
		}
		return "none";
	}

	public boolean getActionTimeDisabled() {
		return getDataSource().getDenyAction() != SsidProfile.DENY_ACTION_BAN;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedSsidName() {
		return getDataSource().getSsidName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public boolean getBlnShowSsid(){
		return !isEasyMode();
	}
	public String getHideSsid(){
		if (isEasyMode()) {
			return "none";
		}
		return "";
	}
	
	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}

	OptionsTransfer macFilterOptions;

	OptionsTransfer schedulerOptions;

	public OptionsTransfer getSchedulerOptions() {
		return schedulerOptions;
	}

	public void setSchedulerOptions(OptionsTransfer schedulerOptions) {
		this.schedulerOptions = schedulerOptions;
	}

	public OptionsTransfer getMacFilterOptions() {
		return macFilterOptions;
	}

	public void setMacFilterOptions(OptionsTransfer macFilterOptions) {
		this.macFilterOptions = macFilterOptions;
	}

	protected List<Long> macFilters;

	public void setMacFilters(List<Long> macFilters) {
		this.macFilters = macFilters;
	}

	protected List<Long> schedulers;

	public void setSchedulers(List<Long> schedulers) {
		this.schedulers = schedulers;
	}

	public int getRekeyPeriod() {
		return rekeyPeriod;
	}

	public void setRekeyPeriod(int rekeyPeriod) {
		this.rekeyPeriod = rekeyPeriod;
	}

	public int getRekeyPeriodGMK() {
		return rekeyPeriodGMK;
	}

	public void setRekeyPeriodGMK(int rekeyPeriodGMK) {
		this.rekeyPeriodGMK = rekeyPeriodGMK;
	}

	public int getPtkTimeOut() {
		return ptkTimeOut;
	}

	public void setPtkTimeOut(int ptkTimeOut) {
		this.ptkTimeOut = ptkTimeOut;
	}

	public int getPtkRetries() {
		return ptkRetries;
	}

	public void setPtkRetries(int ptkRetries) {
		this.ptkRetries = ptkRetries;
	}

	public int getGtkTimeOut() {
		return gtkTimeOut;
	}

	public void setGtkTimeOut(int gtkTimeOut) {
		this.gtkTimeOut = gtkTimeOut;
	}

	public int getGtkRetries() {
		return gtkRetries;
	}

	public void setGtkRetries(int gtkRetries) {
		this.gtkRetries = gtkRetries;
	}

	public int getReplayWindow() {
		return replayWindow;
	}

	public void setReplayWindow(int replayWindow) {
		this.replayWindow = replayWindow;
	}
	
//	public void functionCreateIpAddress(){
//		try {
//			IpAddress ip10 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "10.0.0.0/8",getDomain().getId());
//			if (ip10 == null){
//				IpAddress dto_ip = new IpAddress();
//				dto_ip.setAddressName("10.0.0.0/8");
//				dto_ip.setOwner(getDomain());
//				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
//				SingleTableItem single = new SingleTableItem();
//				single.setIpAddress("10.0.0.0");
//				single.setNetmask("255.0.0.0");
//				single.setType(SingleTableItem.TYPE_GLOBAL);
//				items.add(single);
//				dto_ip.setItems(items);
//				dto_ip.setDefaultFlag(false);
//				QueryUtil.createBo(dto_ip);
//			}
//			IpAddress ip172 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "172.16.0.0/12",getDomain().getId());
//			if (ip172 == null){
//				IpAddress dto_ip = new IpAddress();
//				dto_ip.setAddressName("172.16.0.0/12");
//				dto_ip.setOwner(getDomain());
//				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
//				SingleTableItem single = new SingleTableItem();
//				single.setIpAddress("172.16.0.0");
//				single.setNetmask("255.240.0.0");
//				single.setType(SingleTableItem.TYPE_GLOBAL);
//				items.add(single);
//				dto_ip.setItems(items);
//				dto_ip.setDefaultFlag(false);
//				QueryUtil.createBo(dto_ip);
//			}
//			IpAddress ip192 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "192.168.0.0/16",getDomain().getId());
//			if (ip192 == null){
//				IpAddress dto_ip = new IpAddress();
//				dto_ip.setAddressName("192.168.0.0/16");
//				dto_ip.setOwner(getDomain());
//				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
//				SingleTableItem single = new SingleTableItem();
//				single.setIpAddress("192.168.0.0");
//				single.setNetmask("255.255.0.0");
//				single.setType(SingleTableItem.TYPE_GLOBAL);
//				items.add(single);
//				dto_ip.setItems(items);
//				dto_ip.setDefaultFlag(false);
//				QueryUtil.createBo(dto_ip);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkHiveNameExists(String name, Object value) {
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		List<?> boIds = QueryUtil.executeQuery("select bo.id from " + HiveProfile.class.getSimpleName() + " bo", null,
				new FilterParams(name, value), domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.existsInHiveSsid", value.toString()));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkSsidExists(String name, Object value) {
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ boClass.getSimpleName(), null, new FilterParams(name, value),
				domainId);
		if (!boIds.isEmpty()) {
			if (boIds.size()>1) {
				addActionError(MgrUtil.getUserMessage("error.objectExists", value
					.toString()));
			} else {
				if (boIds.get(0).equals(getDataSource().getId())) {
					return false;
				} else {
					addActionError(MgrUtil.getUserMessage("error.objectExists", value
							.toString()));
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkUserProfileSize() {
		int ssidSize = 0;
		if (getDataSource().getUserProfileDefault() != null) {
			ssidSize++;
		}
		if (getDataSource().getUserProfileSelfReg() != null) {
			ssidSize++;
		}
		if (getDataSource().getRadiusUserProfile() != null) {
			ssidSize = ssidSize + getDataSource().getRadiusUserProfile().size();
		}
		if (ssidSize > 64) {
			addActionError(getText("error.template.moreUserProfile"));
			return false;
		}
		return true;
	}

	private boolean checkPskUserSize() {
		long count = getPskUserCount(getDataSource());
		if (count > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
			addActionError(getText("error.template.morePskUsers"));
			return false;
		}
		return true;
	}

	public static long getPskUserCount(SsidProfile ssidProfile) {
		long count = 0;
		if (null != ssidProfile) {
			if (null != ssidProfile.getLocalUserGroups()) {
				Set<Long> groupIds = new HashSet<Long>();
				for (LocalUserGroup group : ssidProfile.getLocalUserGroups()) {
					if (null != group) {
						groupIds.add(group.getId());
					}
				}
				if (!groupIds.isEmpty()) {
					count = QueryUtil.findRowCount(LocalUser.class, new FilterParams(
							"localUserGroup.id in(:s1) and revoked=:s2", 
							new Object[]{groupIds, false}));
				}
			}
		}
		return count;
	}

	private boolean checkRelativedTemplate(SsidProfile ssidProfile) {
		boolean result = true;
		long start = System.currentTimeMillis();
		int templateCount = 0;
		SsidProfile db_ssid = QueryUtil.findBoById(SsidProfile.class, ssidProfile
				.getId(), new SsidProfilesAction());
		long oldCount = getPskUserCount(db_ssid);
		long newCount = getPskUserCount(ssidProfile);
		long delta = newCount - oldCount;
		if (delta <= 0) {
			result = true;// new count is no more than old.
		}
		Set<Long> templateIds = ConfigurationUtils.getConfigTemplates(ssidProfile);

		for (Long templateId : templateIds) {
			templateCount++;
			ConfigTemplate template = QueryUtil.findBoById(
					ConfigTemplate.class, templateId, new ConfigTemplateAction());
			long count = ConfigTemplateAction.getTotalPmkUserSize(template);
			if (count + delta > LocalUser.MAX_COUNT_AP30_USERPERAP) {
				addActionError(getText("error.ssid.pskUser.overflow.updateSsid",
						new String[] { template.getConfigName() }));
				result = false;
				break;
			}
			
			long totalCount = ConfigTemplateAction.getTotalPSKUserSize(template);
			if (totalCount + delta > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
				addActionError(getText("error.ssid.pskUser.overflow.updateSsid.psk",
						new String[] { template.getConfigName() }));
				result = false;
				break;
			}
		}

		long end = System.currentTimeMillis();
		log.info("checkRelativedTemplate", "edit SSID profile check " + templateCount
				+ " Template for PSK cost:" + (end - start) + "ms.");
		return result;
	}

	public boolean checkMacFilterAction() throws Exception {
		Map<String, String> tmpMacFilter = new HashMap<String, String>();
		Set<String> totalMacOUI = new HashSet<String>();
		for (MacFilter lazyMacfilter : getDataSource().getMacFilters()) {
			MacFilter filter = findBoById(MacFilter.class, lazyMacfilter.getId(), this);
			for (MacFilterInfo filterInfo : filter.getFilterInfo()) {
				totalMacOUI.add(filterInfo.getMacOrOui().getMacOrOuiName());
				if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_PERMIT) {
					if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
							+ MacFilter.FILTER_ACTION_DENY) != null) {
						addActionError(MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName()));
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_PERMIT, "true");
					}
				} else if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_DENY) {
					if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
							+ MacFilter.FILTER_ACTION_PERMIT) != null) {
						addActionError(MgrUtil.getUserMessage("error.differentMacOuiAction",
								filterInfo.getMacOrOui().getMacOrOuiName()));
						return false;
					} else {
						tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_DENY, "true");
					}
				}
			}
		}
		if (totalMacOUI.size()>MacFiltersAction.MAX_MACFILTER_ENTER) {
			addActionError(getText("error.config.macFilter.maxNumber.reference",
					new String[]{String.valueOf(MacFiltersAction.MAX_MACFILTER_ENTER)} ));
			return false;
		}
		return true;
	}

	// ---------------add by Fiona for TX rate
	// setting------------begin---------------
	/*
	 * prepare 11g and 11a rate setting information
	 */
	protected void prepareRateSetInfo() {
		SsidProfile ssidInfo = getDataSource();

        Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
        for (GRateType gType : TX11aOr11gRateSetting.GRateType.values()) {
            TX11aOr11gRateSetting rateSet = ssidInfo.getTX11aOr11gRateSetting(gType);
            if (null == rateSet) {
                rateSet = new TX11aOr11gRateSetting();
                if (GRateType.one.equals(gType) || GRateType.two.equals(gType)
                        || GRateType.five.equals(gType) || GRateType.eleven.equals(gType)) {
                    rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
                } else {
                    rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
                }
            }
            rateSet.setGRateType(gType);
            gRateSet.put(rateSet.getkey(), rateSet);
        }
        ssidInfo.setGRateSets(gRateSet);

        Map<String, TX11aOr11gRateSetting> aRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
        for (ARateType aType : TX11aOr11gRateSetting.ARateType.values()) {
            TX11aOr11gRateSetting rateSet = ssidInfo.getTX11aOr11gRateSetting(aType);
            if (null == rateSet) {
                rateSet = new TX11aOr11gRateSetting();
                if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
                    rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
                } else {
                    rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
                }
            }
            rateSet.setARateType(aType);
            aRateSets.put(rateSet.getkey(), rateSet);
        }
        ssidInfo.setARateSets(aRateSets);
        
        Map<String, TX11aOr11gRateSetting> nRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
        for (NRateType nType : TX11aOr11gRateSetting.NRateType.values()) {
            TX11aOr11gRateSetting rateSet = ssidInfo.getTX11aOr11gRateSetting(nType);
            if (null == rateSet) {
                rateSet = new TX11aOr11gRateSetting();
                rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
            }
            rateSet.setNRateType(nType);
            nRateSets.put(rateSet.getkey(), rateSet);
        }
        ssidInfo.setNRateSets(nRateSets);
        
        //prepare 11ac rate settings
        init11acRateSets();
	}

	/*
	 * update 11g and 11a rate setting information
	 */
	protected void updateRateSetInfo() {
		SsidProfile ssidInfo = getDataSource();
		if (ssidInfo.isEnableGRateSet()) {
			int i = 0;
			int j = 0;
			for (TX11aOr11gRateSetting gRate : ssidInfo.getGRateSets().values()) {
				if (i < 6) {
					gRate.setRateSet(gRateSetType0[i++]);
				} else {
					gRate.setRateSet(gRateSetType1[j++]);
				}
			}
		} else {
			for (TX11aOr11gRateSetting gRate : ssidInfo.getGRateSets().values()) {
				if (GRateType.one.equals(gRate.getGRateType())
						|| GRateType.two.equals(gRate.getGRateType())
						|| GRateType.five.equals(gRate.getGRateType())
						|| GRateType.eleven.equals(gRate.getGRateType())) {
					gRate.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					gRate.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
		}
		if (ssidInfo.isEnableARateSet()) {
			int i = 0;
			int j = 0;
			for (TX11aOr11gRateSetting aRate : ssidInfo.getARateSets().values()) {
				if (i < 4) {
					aRate.setRateSet(aRateSetType0[i++]);
				} else {
					aRate.setRateSet(aRateSetType1[j++]);
				}
			}
		} else {
			for (TX11aOr11gRateSetting aRate : ssidInfo.getARateSets().values()) {
				if (ARateType.six.equals(aRate.getARateType()) || ARateType.twelve.equals(aRate.getARateType()) || ARateType.twenty_four.equals(aRate.getARateType())) {
					aRate.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					aRate.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
		}
		
		if (ssidInfo.getEnableNRateSet()) {
			int i = 0;
			int j = 0;
			int k = 0;
			for (TX11aOr11gRateSetting nRate : ssidInfo.getNRateSets().values()) {
				if (i < 8) {
					nRate.setRateSet(nRateSetType0[i++]);
				} else if (j < 8) {
					nRate.setRateSet(nRateSetType1[j++]);
				} else {
					nRate.setRateSet(nRateSetType2[k++]);
				}
			}
		} else {
			for (TX11aOr11gRateSetting nRate : ssidInfo.getNRateSets().values()) {
				nRate.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
			}
		}
		
		if(ssidInfo.isEnableACRateSet()){
			update11acRateSet();
		}
	}

	public EnumItem[] getEnumRateType() {
		return TX11aOr11gRateSetting.ENUM_RATE_SET_TYPE;
	}
	
	public EnumItem[] getEnumRateType11n() {
		return TX11aOr11gRateSetting.ENUM_RATE_SET_TYPE_11N;
	}
	
	private short[] gRateSetType0;

	private short[] aRateSetType0;

	private short[] gRateSetType1;

	private short[] aRateSetType1;
	
	private short[] nRateSetType0;

	private short[] nRateSetType1;
	
	private short[] nRateSetType2;

	public void setGRateSetType0(short[] rateSetType0) {
		gRateSetType0 = rateSetType0;
	}

	public void setARateSetType0(short[] rateSetType0) {
		aRateSetType0 = rateSetType0;
	}

	public void setGRateSetType1(short[] rateSetType1) {
		gRateSetType1 = rateSetType1;
	}

	public void setARateSetType1(short[] rateSetType1) {
		aRateSetType1 = rateSetType1;
	}

	public short[] getGRateSetType0() {
		return gRateSetType0;
	}

	public short[] getARateSetType0() {
		return aRateSetType0;
	}

	public short[] getGRateSetType1() {
		return gRateSetType1;
	}

	public short[] getARateSetType1() {
		return aRateSetType1;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof SsidProfile) {
			dataSource = bo;
			if (getDataSource().getMacFilters() != null) {
				getDataSource().getMacFilters().size();
			}
			if (getDataSource().getSchedulers() != null) {
				getDataSource().getSchedulers().size();
			}
			if (getDataSource().getRadiusUserProfile() != null) {
				getDataSource().getRadiusUserProfile().size();
			}
			if (getDataSource().getLocalUserGroups() != null) {
				getDataSource().getLocalUserGroups().size();
			}
			if (getDataSource().getGRateSets()!=null){
				getDataSource().getGRateSets().values();
			}
			if (getDataSource().getARateSets()!=null){
				getDataSource().getARateSets().values();
			}
			if (getDataSource().getNRateSets()!=null){
				getDataSource().getNRateSets().values();
			}
			if (getDataSource().getRadiusUserGroups() != null) {
			    getDataSource().getRadiusUserGroups().size();
			}
			if (getDataSource().getAcRateSets()!=null){
				getDataSource().getAcRateSets().size();
			}
		}
		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
		if (bo instanceof IdsPolicy) {
			IdsPolicy idsPolicy = (IdsPolicy) bo;
			idsPolicy.getIdsSsids().size();
		}
		
		if (bo instanceof ConfigTemplate) {
			ConfigTemplate configTemplate = (ConfigTemplate) bo;
			// Just calling the get method will fetch the LAZY attributes
			// bo.getUserProfileAttribute();
			// Call additional LAZY methods

			// if (getDataSource().getRadiusAssignment() != null)
			// getDataSource().getRadiusAssignment().getId();
			if (configTemplate.getMgmtServiceOption() != null)
				configTemplate.getMgmtServiceOption().getId();
//			if (configTemplate.getEthernetAccess() != null)
//				configTemplate.getEthernetAccess().getId();
//			if (configTemplate.getEthernetBridge() != null)
//				configTemplate.getEthernetBridge().getId();
//			if (configTemplate.getEthernetAccessEth1() != null)
//				configTemplate.getEthernetAccessEth1().getId();
//			if (configTemplate.getEthernetBridgeEth1() != null)
//				configTemplate.getEthernetBridgeEth1().getId();
//			if (configTemplate.getEthernetAccessAgg() != null)
//				configTemplate.getEthernetAccessAgg().getId();
//			if (configTemplate.getEthernetBridgeAgg() != null)
//				configTemplate.getEthernetBridgeAgg().getId();
//			if (configTemplate.getEthernetAccessRed() != null)
//				configTemplate.getEthernetAccessRed().getId();
//			if (configTemplate.getEthernetBridgeRed() != null)
//				configTemplate.getEthernetBridgeRed().getId();
			for (ConfigTemplateSsid tmpTemplate : configTemplate.getSsidInterfaces().values()) {
				if (tmpTemplate.getSsidProfile() != null) {
					if (tmpTemplate.getSsidProfile().getRadiusUserProfile() != null) {
						tmpTemplate.getSsidProfile().getRadiusUserProfile().size();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileDefault() != null) {
						tmpTemplate.getSsidProfile().getUserProfileDefault().getId();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileSelfReg() != null) {
						tmpTemplate.getSsidProfile().getUserProfileSelfReg().getId();
					}
					if (null != tmpTemplate.getSsidProfile().getLocalUserGroups()) {
						tmpTemplate.getSsidProfile().getLocalUserGroups().size();
					}
				}
			}
			// configTemplate.getQosPolicies().values();
			
			// add lazy load for DNS server setting
			if(null != configTemplate.getMgmtServiceDns()) {
				if(null != configTemplate.getMgmtServiceDns().getDnsInfo()) {
					configTemplate.getMgmtServiceDns().getDnsInfo().size();
					for (MgmtServiceDnsInfo dnsInfo : configTemplate.getMgmtServiceDns().getDnsInfo()) {
						if(null != dnsInfo.getIpAddress()) {
							dnsInfo.getIpAddress().getItems().size();
						}
					}
				}
			}
		}

		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
			if (userp.getUserProfileAttribute() != null)
				userp.getUserProfileAttribute().getId();
			if (userp.getIpPolicyFrom() != null)
				userp.getIpPolicyFrom().getId();
			if (userp.getIpPolicyTo() != null)
				userp.getIpPolicyTo().getId();
			if (userp.getMacPolicyFrom() != null)
				userp.getMacPolicyFrom().getId();
			if (userp.getMacPolicyTo() != null)
				userp.getMacPolicyTo().getId();
			if (userp.getQosRateControl() != null) {
				userp.getQosRateControl().getId();
				
				if(userp.getQosRateControl().getQosRateLimit() != null) {
					userp.getQosRateControl().getQosRateLimit().size();
				}
			}
			if (userp.getTunnelSetting() != null)
				userp.getTunnelSetting().getId();
			if (userp.getVlan()!=null){
				userp.getVlan().getId();
			}
			if (null != userp.getAssignRules())
				userp.getAssignRules().size();
			if (userp.getUserProfileAttribute() != null){
				userp.getUserProfileAttribute().getItems().size();
			}
			if (userp.getUserProfileSchedulers()!=null) {
				userp.getUserProfileSchedulers().size();
			}
		}
		if (bo instanceof EthernetAccess) {
			EthernetAccess ethe = (EthernetAccess) bo;
			if (ethe.getUserProfile() != null)
				ethe.getUserProfile().getId();
		}
		if (bo instanceof QosClassification) {
			QosClassification qos = (QosClassification) bo;
			if (qos.getQosSsids() != null)
				qos.getQosSsids().size();
		}
		
		if (bo instanceof RadiusAssignment) {
			RadiusAssignment radiusAssignment = (RadiusAssignment) bo;
			if (radiusAssignment.getServices() != null)
				radiusAssignment.getServices().size();
		}
		
		if(bo instanceof RadiusOnHiveap){
		    RadiusOnHiveap radiusOnHiveap = (RadiusOnHiveap) bo;
		    if(radiusOnHiveap.getDirectoryOrLdap() != null){
		        radiusOnHiveap.getDirectoryOrLdap().size();
		        for (ActiveDirectoryOrLdapInfo element : radiusOnHiveap.getDirectoryOrLdap()) {
		            ActiveDirectoryOrOpenLdap subElement = element.getDirectoryOrLdap();
		            if(null != subElement.getAdDomains())
		            	subElement.getAdDomains().size();
		            if(null != subElement && null != subElement.getAdServer() && null != subElement.getAdServer().getItems())
		                subElement.getAdServer().getItems().size();
                }
		    }
		}

        if (bo instanceof QosRateControl) {
            QosRateControl qosRate = (QosRateControl) bo;

            if (qosRate.getQosRateLimit() != null) {
                qosRate.getQosRateLimit().size();
            }
        }
        
        if (bo instanceof IpAddress) {
            IpAddress ipAddress = (IpAddress) bo;
            if(null != ipAddress.getItems())
                ipAddress.getItems().size();
        }

        if (bo instanceof MgmtServiceDns) {
			MgmtServiceDns dnsService = (MgmtServiceDns) bo;
			if(null != dnsService.getDnsInfo()) {
				dnsService.getDnsInfo().size();
			}
		}
		return null;
	}
	
	// fnr add for simple mode
	public short getFreeUserAttributeValue(){
		Set<String> attributeSet = new HashSet<String>();
		List<?> attributeList = QueryUtil.executeQuery("select attributeValue from " 
				+ UserProfile.class.getSimpleName(), null, null, getDomain().getId());

		for(Object obj:attributeList){
			attributeSet.add(obj.toString());
		}

		for(short i=1; i<4096; i++){
			if (!attributeSet.contains(String.valueOf(i))){
				return i;
			}
		}
		return 4095;
	}
	
	public UserProfile functionGetCreateUserProfile(){
		UserProfile createUserProfile;
//		if (getDataSource().getUserProfileDefault()!=null){
//			createUserProfile = getDataSource().getUserProfileDefault();
//		} else if (getDataSource().getUserProfileSelfReg()!=null){
//			createUserProfile = getDataSource().getUserProfileSelfReg();
//		} else {
		createUserProfile = QueryUtil.findBoByAttribute(
				UserProfile.class, "userProfileName", getDataSource().getSsidName(),
				getDomain().getId());
		if (createUserProfile == null){
			createUserProfile = new UserProfile();
		}
//		}
		if (createUserProfile.getId()==null) {
			short userAttribute = getFreeUserAttributeValue();
			createUserProfile.setAttributeValue(userAttribute);
			createUserProfile.setOwner(getDomain());
			createUserProfile.setUserProfileName(getDataSource().getSsidName());
		}
//		createUserProfile.setUserCategory(getDataSource().getUserCategory());
		createUserProfile.setBlnUserManager(getDataSource().getBlnUserManager());
		if (getDataSource().getShowExpressUserAccess()){
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_EMPLOOYEE){
				createUserProfile.setSlaBandwidth(5000);
				createUserProfile.setSlaEnable(true);
				createUserProfile.setSlaAction(UserProfile.SLA_ACTION_LOG);
			} else {
				createUserProfile.setSlaBandwidth(500);
				createUserProfile.setSlaEnable(false);
				createUserProfile.setSlaAction(UserProfile.SLA_ACTION_LOG);
			}
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
				createUserProfile.setPolicingRate(getDataSource().getUserRatelimit());
				createUserProfile.setPolicingRate11n(getDataSource().getUserRatelimit());
				createUserProfile.setPolicingRate11ac(getDataSource().getUserRatelimit());
				if (getDataSource().getUserInternetAccess()){
					createUserProfile.setActionIp(IpPolicyRule.POLICY_ACTION_DENY);
				} else {
					createUserProfile.setActionIp((short)-1);
				}
			} else {
				createUserProfile.setPolicingRate(54000);
				createUserProfile.setPolicingRate11n(1000000);
				createUserProfile.setPolicingRate11ac(1000000);
				createUserProfile.setActionIp((short)-1);
			}
		} 
		createUserProfile.setVlan(getDataSource().getUserVlan());

		return createUserProfile;
	}
	
	public IpPolicy functionGetIpPolicyFrom(){
		IpPolicy ipPolicyFrom = null;
		if (getDataSource().getShowExpressUserAccess()){
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST 
					&& getDataSource().getUserInternetAccess()){
//				functionCreateIpAddress();
				
				ipPolicyFrom = HmBeParaUtil.getDefaultProfile(IpPolicy.class, null);
				
//				ipPolicyFrom = QueryUtil.findBoByAttribute(
//						IpPolicy.class, "policyName", getDataSource().getSsidName(), getDomain().getId());
//				if (ipPolicyFrom == null) {
//					ipPolicyFrom = new IpPolicy();
//					ipPolicyFrom.setPolicyName(getDataSource().getSsidName());
//					ipPolicyFrom.setOwner(getDomain());
//					List<IpPolicyRule> lstRules = new ArrayList<IpPolicyRule>();
//					IpAddress ip10 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "10.0.0.0/8",getDomain().getId());
//					IpAddress ip172 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "172.16.0.0/12",getDomain().getId());
//					IpAddress ip192 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", "192.168.0.0/16",getDomain().getId());
//					NetworkService dhcp_server = null;
//					NetworkService dns = null;
//					List<?> netWorkList = QueryUtil.executeQuery(NetworkService.class, 
//							null, new FilterParams("(serviceName=:s1 or serviceName=:s2) and defaultFlag=:s3",
//									new Object[]{"DHCP-Server", "DNS",true}));
//					for(Object obj:netWorkList){
//						NetworkService oneObj = (NetworkService)obj; 
//						if (oneObj.getServiceName().equalsIgnoreCase("DHCP-Server")){
//							dhcp_server = oneObj;
//						} else {
//							dns = oneObj;
//						}
//					}
//					
//					IpPolicyRule rule = new IpPolicyRule();
//					rule.setRuleId((short)1);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(null);
//					rule.setNetworkService(dhcp_server);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
//					lstRules.add(rule);
//					
//					rule = new IpPolicyRule();
//					rule.setRuleId((short)2);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(null);
//					rule.setNetworkService(dns);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
//					lstRules.add(rule);
//					
//					rule = new IpPolicyRule();
//					rule.setRuleId((short)3);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(ip10);
//					rule.setNetworkService(null);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
//					lstRules.add(rule);
//					
//					rule = new IpPolicyRule();
//					rule.setRuleId((short)4);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(ip172);
//					rule.setNetworkService(null);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
//					lstRules.add(rule);
//					
//					rule = new IpPolicyRule();
//					rule.setRuleId((short)5);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(ip192);
//					rule.setNetworkService(null);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
//					lstRules.add(rule);
//					
//					rule = new IpPolicyRule();
//					rule.setRuleId((short)6);
//					rule.setSourceIp(null);
//					rule.setDesctinationIp(null);
//					rule.setNetworkService(null);
//					rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
//					lstRules.add(rule);
//					
//					ipPolicyFrom.setRules(lstRules);
//				}
			}
		}
		return ipPolicyFrom;
	}
	
	public QosRateControl functionGetQosRateControl(){
		QosRateControl qosRate;
		if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
			qosRate = QueryUtil.findBoByAttribute(QosRateControl.class,
					"qosName", getDataSource().getSsidName(),getDomain().getId(), this);
			if (qosRate==null) {		
				qosRate = new QosRateControl();
				qosRate.setQosName(getDataSource().getSsidName());
				qosRate.setOwner(getDomain());
			}
			if (getDataSource().getShowExpressUserAccess() && !qosRate.isDefaultFlag()){
				qosRate.setRateLimit(getDataSource().getUserRatelimit());
				qosRate.setRateLimit11n(getDataSource().getUserRatelimit());
				qosRate.setRateLimit11ac(getDataSource().getUserRatelimit());
				List<QosRateLimit> vector_QosC = new ArrayList<QosRateLimit>();
				for (int j = 7; j > -1; j--) {
					QosRateLimit dto_QosC0 = new QosRateLimit();
					if (j == 6 || j == 7) {
						dto_QosC0.setSchedulingType(QosRateLimit.STRICT);
						dto_QosC0.setPolicingRateLimit(256);
						dto_QosC0.setPolicing11nRateLimit(256);
						dto_QosC0.setPolicing11acRateLimit(256);
					} else {
						dto_QosC0.setSchedulingType(QosRateLimit.WEIGHTED_ROUND_ROBIN);
						dto_QosC0.setPolicingRateLimit(getDataSource().getUserRatelimit());
						dto_QosC0.setPolicing11nRateLimit(getDataSource().getUserRatelimit());
						dto_QosC0.setPolicing11acRateLimit(getDataSource().getUserRatelimit());
					}
					dto_QosC0.setSchedulingWeight(BeParaModule.DEFAULT_QOS_RATE_CONTROL_WEIGHT[j]);
					dto_QosC0.setQosClass((short) j);
					vector_QosC.add(dto_QosC0);
				}
				qosRate.setQosRateLimit(vector_QosC);
			}

		} else {
			if (getDataSource().getShowExpressUserAccess()){
				qosRate = HmBeParaUtil.getDefaultProfile(QosRateControl.class, null);
			} else {
				qosRate = QueryUtil.findBoByAttribute(QosRateControl.class,
						"qosName", getDataSource().getSsidName(),getDomain().getId(), this);
				if (qosRate==null) {
					qosRate = HmBeParaUtil.getDefaultProfile(QosRateControl.class, null);
				}
			}
		}

		return qosRate;
	}
	
	public boolean createLocalUserBaseGroup(LocalUserGroup group) throws Exception{
		if (group.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			QueryUtil.removeBos(LocalUser.class, 
					new FilterParams("localUserGroup.id = :s1 and userType =:s2",
							new Object[]{group.getId(),LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK}), 
					getUserContext(), null);
			long userCount = QueryUtil.findRowCount(LocalUser.class,
					new FilterParams("localUserGroup.id",group.getId()));
			DecimalFormat df = new DecimalFormat("0000");
			if (userCount>getDataSource().getUserNumberPsk()){
				Collection<String> removeNames = new Vector<String>();
				for (int i = getDataSource().getUserNumberPsk()+1; i < userCount + 1; i++) {
					removeNames.add(group.getUserNamePrefix() + df.format(i));
				}
				
				QueryUtil.removeBos(LocalUser.class, new FilterParams("userName",removeNames),
						getUserContext(), null);
			} else if (userCount<=getDataSource().getUserNumberPsk()){
				for (int i = (int)userCount+1; i < getDataSource().getUserNumberPsk() + 1; i++) {
					LocalUser oneItem = new LocalUser();
					oneItem.setOwner(getDomain());
					oneItem.setLocalUserGroup(group);
					oneItem.setUserType(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
					oneItem.setUserName(group.getUserNamePrefix() + df.format(i));
					QueryUtil.createBo(oneItem);
				}
			}
		} else {
			QueryUtil.removeBos(LocalUser.class, 
					new FilterParams("localUserGroup.id = :s1 and userType =:s2",
							new Object[]{group.getId(),LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK}), 
					getUserContext(), null);
		}
		return true;
	}
	
	public LocalUserGroup functinGetCreateLocalUserGroup() throws Exception{
		LocalUserGroup group=null;
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
			if (!getDataSource().getLocalUserGroups().isEmpty()){
				for(LocalUserGroup luGroup: getDataSource().getLocalUserGroups()){
					group = QueryUtil.findBoById(LocalUserGroup.class, luGroup.getId());
					break;
				}
			} else {
				group = QueryUtil.findBoByAttribute(LocalUserGroup.class, "groupName", 
						getDataSource().getSsidName(), getDomain().getId());
				if (group==null) {
					group = new LocalUserGroup();
					group.setGroupName(getDataSource().getSsidName());
					group.setOwner(getDomain());
				}
			}
			group.setTimeZoneStr(getDomain().getTimeZoneString());
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
				group.setUserType(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				group.setValidTimeType(LocalUserGroup.VALIDTYME_TYPE_SCHEDULE);
				group.setUserNamePrefix(getDataSource().getSsidName().replaceAll("@", "_"));
				group.setPskSecret(getDataSource().getSsidName());
			} else {
				if (getDataSource().getUserPskMethod() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
					group.setUserType(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
					group.setValidTimeType(LocalUserGroup.VALIDTYME_TYPE_ALWAYS);
					group.setUserNamePrefix(getDataSource().getSsidName().replaceAll("@", "_"));
					group.setPskSecret(getDataSource().getSsidName());
				} else {
					group.setUserType(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
					group.setValidTimeType(LocalUserGroup.VALIDTYME_TYPE_ALWAYS);
					group.setUserNamePrefix("");
					group.setPskSecret("");
				}
			}
		}
		return group;
	}
	
	public LocalUserGroup functionGetRadiusUserGroup(String name){
		LocalUserGroup group = QueryUtil.findBoByAttribute(LocalUserGroup.class, "groupName", 
				getDataSource().getSelectNewHiveApRadiusPrimaryIp(), getDomain().getId());
		if (group==null) {
			group = new LocalUserGroup();
			group.setOwner(getDomain());
			group.setGroupName(getDataSource().getSelectNewHiveApRadiusPrimaryIp());
			group.setUserType(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
		}
		return group;
	}
	
	public Scheduler functionGetCreateScheduler(){
		Scheduler createScheduler = null;
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST){
				createScheduler = QueryUtil.findBoByAttribute(
						Scheduler.class, "schedulerName", 
						getDataSource().getSsidName(), getDomain().getId());
				if (createScheduler==null) {
					createScheduler = new Scheduler();
					createScheduler.setSchedulerName(getDataSource().getSsidName());
					createScheduler.setBeginTime("00:00");
					createScheduler.setEndTime("23:59"); 
					createScheduler.setType(Scheduler.RECURRENT);
					createScheduler.setOwner(getDomain());
				}
			}
		}
		return createScheduler;
	}
	
	public boolean functionNeedCreateRadius(){
	    if(getDataSource().isEnabledIDM()) {
	        return true;
	    }
	    
		boolean blnNeedSaveValue = false;
		if (getDataSource().getMacAuthEnabled() ||getDataSource().getEnabledUseGuestManager()
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			blnNeedSaveValue = true;
		} else {
			if (getDataSource().getCwp() != null) {
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
						|| getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
					blnNeedSaveValue = true;
				}
				if (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
					//if (!getDataSource().getCwp().isPpskServer()) {
						blnNeedSaveValue = true;
					//}
				}
			}
		}
		return blnNeedSaveValue;
	}
	
	public RadiusAssignment functionGetRadiusAssignment() throws Exception {
		RadiusAssignment createRadius;
		
		if (getDataSource().getRadiusAssignment()!=null){
			createRadius = getDataSource().getRadiusAssignment();
			createRadius = QueryUtil.findBoById(RadiusAssignment.class, createRadius.getId(), this);
			createRadius.getServices().clear();
		} else {
			createRadius = new RadiusAssignment();
			createRadius.setRadiusName(getDataSource().getSsidName());
			createRadius.setOwner(getDomain());
		}
		
		RadiusServer addRadiusServer = new RadiusServer();
		addRadiusServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_PRIMARY);
		if (getDataSource().getNewRadiusType() == Cwp.CWP_EXTERNAL){
		    // For IDM authentication proxy
	        if (getDataSource().isEnabledIDM()
	                && StringUtils.isBlank(inputNewRadiusPrimaryIpValue)) {
	            return null;
	        }
			addRadiusServer.setSharedSecret(getDataSource().getNewRadiusSecret());
			addRadiusServer.setIpAddress(findBoById(IpAddress.class,
					getDataSource().getNewRadiusPrimaryIp()));
		} else {
		    // For IDM authentication proxy
            final String noneItemMsg = MgrUtil.getUserMessage("config.optionsTransfer.none");
            if (StringUtils.isBlank(getDataSource().getSelectNewHiveApRadiusPrimaryIp())
                    || getDataSource().getSelectNewHiveApRadiusPrimaryIp().equals(noneItemMsg)) {
                return null;
            }
            // handle dhcp & static IP
            IpAddress ip = updateHiveAPConfig();
            if (null != ip) {
                addRadiusServer.setIpAddress(ip);
            }
		}
		createRadius.getServices().add(addRadiusServer);
		
		if (getDataSource().getNewRadiusType() == Cwp.CWP_EXTERNAL){
			if (getDataSource().getNewRadiusSecondaryIp()!=null 
					&& getDataSource().getNewRadiusSecondaryIp()>0){
				RadiusServer secRadiusServer = new RadiusServer();
				secRadiusServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_BACKUP1);
				secRadiusServer.setSharedSecret(getDataSource().getNewRadiusSecondSecret());
		
				secRadiusServer.setIpAddress(findBoById(IpAddress.class,
						getDataSource().getNewRadiusSecondaryIp()));
				createRadius.getServices().add(secRadiusServer);
			}
		} else {
		    final String noneItemMsg = MgrUtil.getUserMessage("config.optionsTransfer.none");
			if (StringUtils.isNotBlank(getDataSource().getSelectNewHiveApRadiusSecondaryIp())
			        && !getDataSource().getSelectNewHiveApRadiusSecondaryIp().equals(noneItemMsg)){
				RadiusServer secRadiusServer = new RadiusServer();
				secRadiusServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_BACKUP1);
				List<?> tmpIpAddress = QueryUtil.executeQuery("select cfgIpAddress from " + HiveAp.class.getSimpleName()
						, null, new FilterParams("hostName = :s1 and dhcp = :s2", 
								new Object[]{getDataSource().getSelectNewHiveApRadiusSecondaryIp(), false}),getDomain().getId());
				if (!tmpIpAddress.isEmpty()){
					IpAddress ip = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", tmpIpAddress.get(0).toString(),getDomain().getId());
					if (ip == null) {
						ip = CreateObjectAuto.createNewIP(tmpIpAddress.get(0).toString(), IpAddress.TYPE_IP_ADDRESS, getDomain(), "For AAA Client Setting");
					}
					secRadiusServer.setIpAddress(ip);
				}
				createRadius.getServices().add(secRadiusServer);
			}
					
		}
		return createRadius;
	}

	private IpAddress updateHiveAPConfig() throws Exception {
		HiveAp hiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", getDataSource()
				.getSelectNewHiveApRadiusPrimaryIp(), getDomain().getId());

		if (null == hiveAP) {
		    return null;
		}

		IpAddress ip;
		String desc = "For AAA Client Setting";
		String staticHiveAPIpAddress = getDataSource().getStaticHiveAPIpAddress();
		String staticHiveAPNetmask = getDataSource().getStaticHiveAPNetmask();
		String staticHiveAPGateway = getDataSource().getStaticHiveAPGateway();
		if (hiveAP.isDhcp()) {
			// if the HiveAp' IpAddress is DHCP, set it to static IP
			hiveAP.setCfgIpAddress(staticHiveAPIpAddress);
			hiveAP.setCfgNetmask(staticHiveAPNetmask);
			hiveAP.setCfgGateway(staticHiveAPGateway);
			hiveAP.setDhcp(false);
			QueryUtil.updateBo(hiveAP);

			ip = updateIPAddress(staticHiveAPIpAddress, desc);
		} else {
			// allow to alter the static IP, update fields in HiveAp
			if (!hiveAP.getCfgIpAddress().equals(staticHiveAPIpAddress)
					|| !hiveAP.getCfgNetmask().equals(staticHiveAPNetmask)
					|| !hiveAP.getCfgGateway().equals(staticHiveAPGateway)) {
				hiveAP.setCfgIpAddress(staticHiveAPIpAddress);
				hiveAP.setCfgNetmask(staticHiveAPNetmask);
				hiveAP.setCfgGateway(staticHiveAPGateway);
				QueryUtil.updateBo(hiveAP);
			}
			ip = updateIPAddress(staticHiveAPIpAddress, desc);
		}
		return ip;
	}
	
	/**
	 * Update IpAddress:<br> 
	 * if it does not contain the IpAddress object in database, it will create a new IpAddress object;<br>
	 * else update the exist IpAddress Object.
	 * 
	 * @param ipAddress -
	 * @param description -
	 * @return a new IpAddress Object after <b>new</b> or <b>update</b>
	 * @throws Exception -
	 */
	private IpAddress updateIPAddress(String ipAddress, String description) throws Exception {

	    IpAddress ip = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", ipAddress,getDomain().getId(), this);
        if (ip == null) {
            //create a new object
            short ipType = ImportCsvFileAction.getIpAddressWrongFlag(ipAddress) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
            ip = CreateObjectAuto.createNewIP(ipAddress, ipType, getDomain(), 
                    description);
        }else{
            List<SingleTableItem> items=ip.getItems();
            if(items.isEmpty()){
                //insert a subObject
                SingleTableItem item = new SingleTableItem();
                item.setIpAddress(ipAddress);
                item.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
                item.setType(SingleTableItem.TYPE_GLOBAL);
                item.setDescription(description);
                
                items.add(item);
            }else if(!items.get(0).getIpAddress().equals(ipAddress)){
                items.get(0).setIpAddress(ipAddress);
            }
            ip = QueryUtil.updateBo(ip);
        }
        return ip;
    }

    public RadiusOnHiveap functionGetRadiusOnHiveap(String name) {
		RadiusOnHiveap radiusPro = null;
		// create new radius on hiveap profile
		if (getDataSource().getNewRadiusType() == Cwp.CWP_INTERNAL){
			radiusPro = getRadiusServer(name);
		}
		return radiusPro;
	}

	private RadiusOnHiveap getRadiusServer(String name) {
		RadiusOnHiveap radiusPro;
		radiusPro = QueryUtil.findBoByAttribute(RadiusOnHiveap.class, "radiusName",
				name, getDomain().getId(),this);
		if (radiusPro== null){
			radiusPro = new RadiusOnHiveap();
			radiusPro.setRadiusName(name);
			radiusPro.setOwner(getDomain());
		}
		return radiusPro;
	}
	
	public boolean functionExpressModeUpdateOrCreate() {
		try {
			RadiusAssignment radiusAssignment = null;
			RadiusOnHiveap radiusOnHiveap = null;
			RadiusOnHiveap radiusOnHiveapSec = null;
			LocalUserGroup radiusLocalUserGroup = null;
			LocalUserGroup radiusLocalUserGroupSec = null;
			LocalUserGroup createLocalUserGroup;
			Scheduler createScheduler;
			IpPolicy ipPolicyFrom;
			QosRateControl qosRate;
			UserProfile createUserProfile;
			
			boolean blnNeedSaveValue = functionNeedCreateRadius();
			if (blnNeedSaveValue){
				radiusAssignment = functionGetRadiusAssignment();
			}
			if (blnNeedSaveValue){
			    final String noneItemMsg = MgrUtil.getUserMessage("config.optionsTransfer.none");
                if(StringUtils.isNotBlank(getDataSource().getSelectNewHiveApRadiusPrimaryIp()) 
			            && !getDataSource().getSelectNewHiveApRadiusPrimaryIp().equals(noneItemMsg)) {
			        radiusOnHiveap = functionGetRadiusOnHiveap(getDataSource().getSelectNewHiveApRadiusPrimaryIp());
			    }
				if (StringUtils.isNotBlank(getDataSource().getSelectNewHiveApRadiusSecondaryIp())
				        && !getDataSource().getSelectNewHiveApRadiusSecondaryIp().equals(noneItemMsg)){
					radiusOnHiveapSec = functionGetRadiusOnHiveap(getDataSource().getSelectNewHiveApRadiusSecondaryIp());
				}
			}
			if (radiusOnHiveap!=null){
				radiusLocalUserGroup = functionGetRadiusUserGroup(getDataSource().getSelectNewHiveApRadiusPrimaryIp());
				if (getDataSource().getSelectNewHiveApRadiusSecondaryIp()!=null &&
						!getDataSource().getSelectNewHiveApRadiusSecondaryIp().equals("")){
					radiusLocalUserGroupSec = functionGetRadiusUserGroup(getDataSource().getSelectNewHiveApRadiusSecondaryIp());
				}
			}
			createLocalUserGroup = functinGetCreateLocalUserGroup();
			createScheduler = functionGetCreateScheduler();
			createUserProfile = functionGetCreateUserProfile();
			qosRate = functionGetQosRateControl();
			ipPolicyFrom  = functionGetIpPolicyFrom();
			
			flgRemoveSchedule = false;
			flgRemoveQosRate = false;
			flgRemoveIpPolicyFrom = false;
			flgRemoveRadiusAssignment = false;
//			flgRemoveRadiusOnHiveAp = false;
//			flgRemoveRadiusLocalUserGroup = false;
			flgRemovePskLocalUserGroup = false;
			
			if (createScheduler!=null){
				if (createScheduler.getId()==null) {
					Long tmpCreateId = QueryUtil.createBo(createScheduler);
					createScheduler = QueryUtil.findBoById(Scheduler.class, tmpCreateId);
				}
			} else {
				flgRemoveSchedule = true;
			}
	
			if (qosRate.getId()==null && getDataSource().getShowExpressUserAccess()) {
				Long tmpCreateId = QueryUtil.createBo(qosRate);
				qosRate = QueryUtil.findBoById(QosRateControl.class, tmpCreateId, this);
			} else {
				if (getDataSource().getShowExpressUserAccess()) {
					if (qosRate.isDefaultFlag()){
						flgRemoveQosRate= true;
						createUserProfile.setPolicingRate(54000);
						createUserProfile.setPolicingRate11n(1000000);
						createUserProfile.setPolicingRate11ac(1000000);
					} else {
						qosRate = QueryUtil.updateBo(qosRate);
					}
				}
			}
			
			if (ipPolicyFrom!=null) {
				if (ipPolicyFrom.getId()==null && getDataSource().getShowExpressUserAccess()) {
					Long tmpCreateId = QueryUtil.createBo(ipPolicyFrom);
					ipPolicyFrom = QueryUtil.findBoById(IpPolicy.class, tmpCreateId);
				}
			} else {
				if (getDataSource().getShowExpressUserAccess()) {
					flgRemoveIpPolicyFrom= true;
				}
			}
	
			if (radiusAssignment != null){
				if (radiusAssignment.getId()==null) {
					Long tmpCreateId = QueryUtil.createBo(radiusAssignment);
					radiusAssignment = QueryUtil.findBoById(RadiusAssignment.class, tmpCreateId);
				} else {
					radiusAssignment = QueryUtil.updateBo(radiusAssignment);
				}
			} else {
				flgRemoveRadiusAssignment=true;
			}
	
			if (radiusLocalUserGroup!=null) {
				if (radiusLocalUserGroup.getId() == null) {
					Long tmpCreateId = QueryUtil.createBo(radiusLocalUserGroup);
					radiusLocalUserGroup = QueryUtil.findBoById(LocalUserGroup.class, tmpCreateId);
				} 
//			} else {
//				flgRemoveRadiusLocalUserGroup = true;
			}
			if (radiusLocalUserGroupSec!=null) {
				if (radiusLocalUserGroupSec.getId() == null) {
					Long tmpCreateId = QueryUtil.createBo(radiusLocalUserGroupSec);
					radiusLocalUserGroupSec = QueryUtil.findBoById(LocalUserGroup.class, tmpCreateId);
				} 
//			} else {
//				flgRemoveRadiusLocalUserGroup = true;
			}
	
			if (createLocalUserGroup!=null) {
				createLocalUserGroup.setUserProfileId(createUserProfile.getAttributeValue());
				if (createLocalUserGroup.getId() == null) {
					createLocalUserGroup.setSchedule(createScheduler);
					Long tmpCreateId = QueryUtil.createBo(createLocalUserGroup);
					createLocalUserGroup = QueryUtil.findBoById(LocalUserGroup.class, tmpCreateId);
				} else {
					createLocalUserGroup.setSchedule(createScheduler);
					createLocalUserGroup = QueryUtil.updateBo(createLocalUserGroup);
				}
				createLocalUserBaseGroup(createLocalUserGroup);
			} else {
				LocalUserGroup lp = QueryUtil.findBoByAttribute(LocalUserGroup.class,
						"groupName", getDataSource().getSsidName(), getDomain().getId());
				if (lp!=null) {
					flgRemovePskLocalUserGroup = true;
				}
			}
			// bind Active Directory to HiveAp
			if (radiusOnHiveap != null){
				if (radiusOnHiveap.getId() == null){
					Set<LocalUserGroup> userGroups = new HashSet<LocalUserGroup>();
					userGroups.add(radiusLocalUserGroup);
					radiusOnHiveap.setLocalUserGroup(userGroups);
					
					setActiveDirectoryInRadiusOnHiveAp(radiusOnHiveap);
                    
					QueryUtil.createBo(radiusOnHiveap);
//					radiusOnHiveap = (RadiusOnHiveap)findBoById(RadiusOnHiveap.class, tmpCreateId);
				}else{
				    setActiveDirectoryInRadiusOnHiveAp(radiusOnHiveap);
				    QueryUtil.updateBo(radiusOnHiveap);
				}
				HiveAp firstAp = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", 
						getDataSource().getSelectNewHiveApRadiusPrimaryIp(),getDomain().getId());
				if (firstAp!=null) {
					firstAp.setRadiusServerProfile(radiusOnHiveap);
					QueryUtil.updateBo(firstAp);
				}
//			} else {
//				flgRemoveRadiusOnHiveAp = true;
			}
			if (radiusOnHiveapSec != null){
				if (radiusOnHiveapSec.getId() == null){
					Set<LocalUserGroup> userGroups = new HashSet<LocalUserGroup>();
					userGroups.add(radiusLocalUserGroupSec);
					radiusOnHiveapSec.setLocalUserGroup(userGroups);
					QueryUtil.createBo(radiusOnHiveapSec);
//					radiusOnHiveapSec = (RadiusOnHiveap)findBoById(RadiusOnHiveap.class, tmpCreateId);
				}
				HiveAp secondAp = QueryUtil.findBoByAttribute(HiveAp.class, "hostName", 
						getDataSource().getSelectNewHiveApRadiusSecondaryIp(),getDomain().getId());
				if (secondAp!=null) {
					secondAp.setRadiusServerProfile(radiusOnHiveapSec);
					QueryUtil.updateBo(secondAp);
				}
//			} else {
//				flgRemoveRadiusOnHiveAp = true;
			}
			
			// create UserProfile
			if (getDataSource().getShowExpressUserAccess()){
				createUserProfile.setQosRateControl(qosRate);
				createUserProfile.setIpPolicyFrom(ipPolicyFrom);
			}
			if (getDataSource().getShowExpressUserAccess() && ipPolicyFrom==null){
				createUserProfile.setActionIp((short)-1);
			}
			if (createUserProfile.getId()==null) {
				Long tmpCreateId = QueryUtil.createBo(createUserProfile);
				createUserProfile = QueryUtil.findBoById(UserProfile.class, tmpCreateId);
			} else {
				createUserProfile = QueryUtil.updateBo(createUserProfile);
			}
			// END
			
			if (getDataSource().getCwp()!=null 
					&& getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
				getDataSource().setUserProfileSelfReg(createUserProfile);
				getDataSource().setUserProfileDefault(null);
			} else {
				getDataSource().setUserProfileDefault(createUserProfile);
				getDataSource().setUserProfileSelfReg(null);
			}
			
			getDataSource().setRadiusAssignment(radiusAssignment);
			if (createLocalUserGroup!=null){
				getDataSource().getLocalUserGroups().clear();
				getDataSource().getLocalUserGroups().add(createLocalUserGroup);
			} else {
				getDataSource().getLocalUserGroups().clear();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * bind Active Directory to RadiusOnHiveAp
	 * 
	 * @param radiusOnHiveap -
	 * @throws Exception -
	 */
    private void setActiveDirectoryInRadiusOnHiveAp(RadiusOnHiveap radiusOnHiveap) throws Exception {
        if(getDataSource().isEnableADIntegration()){
            radiusOnHiveap.setDatabaseType(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
    
            if(!getDataSource().getAdDomainTestUser().contains("@")){
            	getDataSource().setAdDomainTestUser(getDataSource().getAdDomainTestUser() 
            			+ "@" + getDataSource().getAdDomainFullName());
            }
            
            String description = "for AAA User Directory Settings";
            
            HiveAp queryHiveAP = null;
            if(StringUtils.isNotBlank(getDataSource().getSelectNewHiveApRadiusPrimaryIp())) {
                queryHiveAP = QueryUtil.findBoByAttribute(HiveAp.class, "hostName",
                        getDataSource().getSelectNewHiveApRadiusPrimaryIp(), getDomain().getId());
            }
            
			if(radiusOnHiveap.getDirectoryOrLdap().isEmpty()){
                // create
            	List<ActiveDirectoryOrLdapInfo> directoryOrLdaps=new ArrayList<ActiveDirectoryOrLdapInfo>();
            	ActiveDirectoryOrLdapInfo elementActiveDirectory=new ActiveDirectoryOrLdapInfo();
            	
            	ActiveDirectoryOrOpenLdap directoryOrLdap=new ActiveDirectoryOrOpenLdap();
            	directoryOrLdap.setOwner(getDomain());
            	directoryOrLdap.setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
            	IpAddress adServer = updateIPAddress(getDataSource().getAdServerIpAddress(),description);//IpAddress
                directoryOrLdap.setAdServer(adServer);//Active Directory Servere
                
                directoryOrLdap.setName(getDataSource().getLabel() + "_Express");
                if(null != queryHiveAP) {
                    directoryOrLdap.setApMac(queryHiveAP.getMacAddress());
                }
                
                // save or discard admin credentials
                if(getSeesionDiscardJoin() == DiscardJoinInfo.YES.getValue()) {
                	directoryOrLdap.setUserNameA("");//set the Admin User empty
                	directoryOrLdap.setPasswordA("");//set the Admin Password empty
                } else {
                	directoryOrLdap.setUserNameA(getDataSource().getAdDomainAdmin());//Admin User
                	directoryOrLdap.setPasswordA(getDataSource().getAdDomainAdminPasswd());//Admin Password
            	}
            	directoryOrLdap.setBasedN(getDataSource().getBaseDN());
            	
            	ActiveDirectoryDomain defDomain=new ActiveDirectoryDomain();
            	defDomain.setDefaultFlag(true);
            	defDomain.setServer(getDataSource().getAdServerIpAddress());
            	defDomain.setDomain(getDataSource().getAdDomainName());
            	defDomain.setFullName(getDataSource().getAdDomainFullName());
            	defDomain.setBindDnName(getDataSource().getAdDomainTestUser());
            	defDomain.setBindDnPass(getDataSource().getAdDomainTestUserPasswd());
                List<ActiveDirectoryDomain> adDomains=new ArrayList<ActiveDirectoryDomain>();
                adDomains.add(defDomain);
                directoryOrLdap.setAdDomains(adDomains);
                // set client LDAP SASL wrapping to fix AD join issue
                directoryOrLdap.setLdapSaslWrapping(getDataSource().getLdapSaslWrapping());
                
                //create this object
                QueryUtil.createBo(directoryOrLdap);
            	
                elementActiveDirectory.setDirectoryOrLdap(directoryOrLdap);
                
                directoryOrLdaps.add(elementActiveDirectory);
                
                radiusOnHiveap.setDirectoryOrLdap(directoryOrLdaps);
            }else{
                // update
                ActiveDirectoryOrLdapInfo elementActiveDirectory = radiusOnHiveap.getDirectoryOrLdap().get(0);
                
                ActiveDirectoryOrOpenLdap directoryOrLdap = elementActiveDirectory.getDirectoryOrLdap();
            	// save or discard admin credentials
            	if(getSeesionDiscardJoin() == DiscardJoinInfo.YES.getValue()) {
                    directoryOrLdap.setUserNameA("");//set the Admin User empty
                    directoryOrLdap.setPasswordA("");//set the Admin Password empty
            	} else {
            		if(!directoryOrLdap.getUserNameA().equals(getDataSource().getAdDomainAdmin())||
            				!directoryOrLdap.getPasswordA().equals(getDataSource().getAdDomainAdminPasswd())){
                		directoryOrLdap.setUserNameA(getDataSource().getAdDomainAdmin());//Admin User
                		directoryOrLdap.setPasswordA(getDataSource().getAdDomainAdminPasswd());//Admin Password
            		}
            	}
                directoryOrLdap.setBasedN(getDataSource().getBaseDN());
                
                directoryOrLdap.setName(getDataSource().getLabel() + "_Express");
                if(null != queryHiveAP) {
                    directoryOrLdap.setApMac(queryHiveAP.getMacAddress());
                }
                
                List<SingleTableItem> items = directoryOrLdap.getAdServer().getItems();
                if(items.isEmpty()){
                    IpAddress adServer = updateIPAddress(getDataSource().getAdServerIpAddress(),description);//IpAddress
                    directoryOrLdap.setAdServer(adServer);//Active Directory Servere
                }else{
                    if (!items.get(0).getIpAddress().equals(getDataSource().getAdServerIpAddress())) {
                        //items.get(0).setIpAddress(ipAddress);
                        IpAddress adServer = updateIPAddress(getDataSource().getAdServerIpAddress(),description);//IpAddress
                        directoryOrLdap.setAdServer(adServer);//Active Directory Servere
                    }
                }
                
                List<ActiveDirectoryDomain> adDomains = directoryOrLdap.getAdDomains();
                if(!adDomains.get(0).getDomain().equals(getDataSource().getAdDomainName())||
                        !adDomains.get(0).getFullName().equals(getDataSource().getAdDomainFullName())||
                        !adDomains.get(0).getServer().equals(getDataSource().getAdServerIpAddress())||
                        !adDomains.get(0).getBindDnName().equals(getDataSource().getAdDomainTestUser())||
                        !adDomains.get(0).getBindDnPass().equals(getDataSource().getAdDomainTestUserPasswd())){ //???? is there adDomains.isEmpty()?
                    
                	adDomains.get(0).setServer(getDataSource().getAdServerIpAddress());
                    adDomains.get(0).setDomain(getDataSource().getAdDomainName());
                    adDomains.get(0).setFullName(getDataSource().getAdDomainFullName());
                    adDomains.get(0).setBindDnName(getDataSource().getAdDomainTestUser());
                    adDomains.get(0).setBindDnPass(getDataSource().getAdDomainTestUserPasswd());
                }
                // set client LDAP SASL wrapping to fix AD join issue
                directoryOrLdap.setLdapSaslWrapping(getDataSource().getLdapSaslWrapping());
                
                QueryUtil.updateBo(directoryOrLdap);
            }
        }
    }
    
    /**
     * If the session value equals 1, discard the join credentials; else save the credentials
     * @author Yunzhi Lin
     * - Time: May 9, 2011 3:59:56 PM
     * @return -
     */
	private int getSeesionDiscardJoin() {
		if(null == MgrUtil.getSessionAttribute(SESSION_DISCARD_JOIN_CREDENTIALS)){
			return DiscardJoinInfo.NO.getValue();
		} else {
			MgrUtil.removeSessionAttribute(SESSION_DISCARD_JOIN_CREDENTIALS);
			return DiscardJoinInfo.YES.getValue();
		}
	}
	
	private enum DiscardJoinInfo {
		YES(1), NO(0);

		private final int value;

		private DiscardJoinInfo(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public boolean flgRemoveSchedule = false;
	public boolean flgRemoveQosRate = false;
	public boolean flgRemoveIpPolicyFrom = false;
	public boolean flgRemoveRadiusAssignment = false;
//	public boolean flgRemoveRadiusOnHiveAp = false;
//	public boolean flgRemoveRadiusLocalUserGroup = false;
	public boolean flgRemovePskLocalUserGroup = false;
	
	public void removeUnusedProfile(){
		if (flgRemoveQosRate) {
			try{
				QueryUtil.removeBos(QosRateControl.class, new FilterParams("qosName",
				getDataSource().getSsidName()), getUserContext(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (flgRemoveIpPolicyFrom) {
			try{
				QueryUtil.removeBos(IpPolicy.class, new FilterParams("policyName",
						getDataSource().getSsidName()), getUserContext(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (flgRemoveRadiusAssignment) {
			try {
				QueryUtil.removeBos(RadiusAssignment.class, new FilterParams("radiusName",
					getDataSource().getSsidName()), getUserContext(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		if (flgRemoveRadiusOnHiveAp) {
//			RadiusOnHiveap raAp = QueryUtil.findBoByAttribute(RadiusOnHiveap.class, "radiusName", getDataSource().getSsidName(),getDomain().getId());
//			if (raAp!=null) {
//				QueryUtil.bulkUpdateBos(HiveAp.class, "radiusServerProfile = :s1" ,"radiusServerProfile.id = :s2",
//						new Object[] { null,raAp.getId()}, getDomain().getId());
//				QueryUtil.removeBos(RadiusOnHiveap.class, new FilterParams("radiusName",
//						getDataSource().getSsidName()), getUserContext(), null);
//			}
//		}
		
//		if (flgRemoveRadiusLocalUserGroup) {
//			QueryUtil.removeBos(LocalUser.class, new FilterParams("localUserGroup.groupName",
//					getDataSource().getSsidName() + "-RS"), getUserContext(), null);
//			QueryUtil.removeBos(LocalUserGroup.class, new FilterParams("groupName",
//				getDataSource().getSsidName() + "-RS"), getUserContext(), null);
//		}
		if (flgRemovePskLocalUserGroup) {
			try {
				LocalUserGroup lp = QueryUtil.findBoByAttribute(LocalUserGroup.class,
						"groupName", getDataSource().getSsidName(), getDomain().getId());
				QueryUtil.removeBos(LocalUser.class, new FilterParams("localUserGroup.id",lp.getId()),getUserContext(),null);
				QueryUtil.removeBo(LocalUserGroup.class, lp.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (flgRemoveSchedule){
			try {
				QueryUtil.removeBos(Scheduler.class, new FilterParams("schedulerName",
				getDataSource().getSsidName()), getUserContext(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// end add
	
	

	// ---------------add by Fiona for TX rate
	// setting------------end---------------

	public boolean checkAllCloseUpdate(ConfigTemplate modifyConfigTemplate) throws Exception {
		if (!checkExistSsid(modifyConfigTemplate)) {
			return false;
		}
		if (!checkRadioModeSize(modifyConfigTemplate)) {
			return false;
		}
		if (!checkIpPolicyAndMacPolicySize(modifyConfigTemplate)) {
			return false;
		}
		if (!checkUserProfileAttribute(modifyConfigTemplate)) {
			return false;
		}
		if (!checkCacAirTime(modifyConfigTemplate)) {
			return false;
		}
		if (!checkTotalPskGroupSize(modifyConfigTemplate)) {
			return false;
		}

		if (!checkTotalPskUserSize(modifyConfigTemplate)) {
			return false;
		}
		return true;
	}
	
	private boolean checkTotalPskUserSize(ConfigTemplate modifyConfigTemplate) {
		long count = ConfigTemplateAction.getTotalPmkUserSize(modifyConfigTemplate);
		if (count > LocalUser.MAX_COUNT_AP30_USERPERAP) {
			strErrorMessage=getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERPERAP)});
			return false;
		}
		
		long totalCount = ConfigTemplateAction.getTotalPSKUserSize(modifyConfigTemplate);
		if (totalCount > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
			strErrorMessage=getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP)});
			return false;
		}
		return true;
	}

	private boolean checkTotalPskGroupSize(ConfigTemplate modifyConfigTemplate) {
		long totalUserCount = ConfigTemplateAction.getTotalPskGroupId(modifyConfigTemplate).size();
		if (totalUserCount > 512) {
			strErrorMessage=getText("error.template.morePskGroupPerTemplate");
			return false;
		}
		return true;
	}
	
	public boolean checkExistSsid(ConfigTemplate modifyConfigTemplate) {
		Set<String> ssidSets = new HashSet<String>();
		for (ConfigTemplateSsid configTemplateSsid : modifyConfigTemplate.getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (ssidSets.contains(configTemplateSsid.getSsidProfile().getSsid())) {
					strErrorMessage = getText("error.template.existSsid");
					return false;
				} else {
					ssidSets.add(configTemplateSsid.getSsidProfile().getSsid());
				}
			}
		}
		return true;
	}

	public boolean checkRadioModeSize(ConfigTemplate modifyConfigTemplate) {
		int amodelCount = 0;
		int bmodelConnt = 0;

		for (ConfigTemplateSsid configTemplateSsid : modifyConfigTemplate.getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_A) {
					amodelCount++;
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BG) {
					bmodelConnt++;
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
					amodelCount++;
					bmodelConnt++;
				}
			}
		}
		if (amodelCount > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeA") };
			strErrorMessage = getText("error.assignSsid.range", tempStr);
			return false;
		}

		if (bmodelConnt > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeBG") };
			strErrorMessage = getText("error.assignSsid.range", tempStr);
			return false;
		}
		return true;
	}

	public boolean checkIpPolicyAndMacPolicySize(ConfigTemplate modifyConfigTemplate) {
		Set<String> ipPolicyName = new HashSet<String>();
		Set<String> macPolicyName = new HashSet<String>();
		Set<Long> userProIds = new HashSet<Long>();
		for (ConfigTemplateSsid configTemplateSsid : modifyConfigTemplate.getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileDefault()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						userProIds.add(tempUser.getId());
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						userProIds.add(rule.getUserProfile().getId());
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!userProIds.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : userProIds) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!userProIds.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							newSetUserProfile.add(rule.getUserProfileId());
						}
					}
				}
			}
			if (!newSetUserProfile.isEmpty()) {
				userProIds.addAll(newSetUserProfile);
			}
		}
		
		for (Long ids : userProIds) {
			UserProfile tempUserProfile = QueryUtil.findBoById(UserProfile.class, ids, this);
			
			if (tempUserProfile.getIpPolicyTo() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyTo() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getIpPolicyFrom() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyFrom().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyFrom() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyFrom().getPolicyName());
			}
		}

		if (ipPolicyName.size() > 32) {
			// ipPolicyName size must less than 32
			strErrorMessage = getText("error.template.moreIPPolicy");
			return false;
		}

		if (macPolicyName.size() > 32) {
			// macPolicyName size must less than 32
			strErrorMessage = getText("error.template.moreMACPolicy");
			return false;
		}
		return true;
	}

	protected boolean checkUserProfileAttribute(ConfigTemplate modifyConfigTemplate) {
		Set<String> setUsedUserProfile = new HashSet<String>();
		Set<String> setUsedAttrValue = new HashSet<String>();
		Set<String> userProfileCount = new HashSet<String>();
		Set<Long> userProIds = new HashSet<Long>();
		// check bind userProfile attribute value in wlan mapping
		for (ConfigTemplateSsid configTemplateSsid : modifyConfigTemplate.getSsidInterfaces()
				.values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileDefault()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					userProIds.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg()
							.getId());
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						userProIds.add(tempUser.getId());
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						userProIds.add(rule.getUserProfile().getId());
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!userProIds.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : userProIds) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!userProIds.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							newSetUserProfile.add(rule.getUserProfileId());
						}
					}
				}
			}
			if (!newSetUserProfile.isEmpty()) {
				userProIds.addAll(newSetUserProfile);
			}
		}
		
		for (Long ids : userProIds) {
			UserProfile forAttrUserProfile = QueryUtil.findBoById(
					UserProfile.class, ids, this);
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& setUsedAttrValue.contains(String.valueOf(forAttrUserProfile
							.getAttributeValue()))) {
				strErrorMessage = getText("error.template.sameAttribute");
				return false;
			}
			UserProfileAttribute userProfileAttr = forAttrUserProfile.getUserProfileAttribute();
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& userProfileAttr != null) {
				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								if (setUsedAttrValue.contains(String.valueOf(addCount))) {
									strErrorMessage = getText("error.template.sameAttribute");
									return false;
								}
							}
						} else {
							if (setUsedAttrValue.contains(attrRange[0])) {
								strErrorMessage = getText("error.template.sameAttribute");
								return false;
							}
						}
					}
				}

				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								setUsedAttrValue.add(String.valueOf(addCount));
							}
						} else {
							setUsedAttrValue.add(attrRange[0]);
						}
					}
				}
			}
			setUsedUserProfile.add(forAttrUserProfile.getId().toString());
			setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
			if (userProfileAttr != null) {
				userProfileCount.add(userProfileAttr.getId().toString());
			}
		}

		if (userProfileCount.size() > 64) {
			strErrorMessage = getText("error.template.moreUserProfileAttributeGroup");
			return false;
		}
		return true;
	}

	protected boolean ethernetAttrCheck(Set<String> setUsedUserProfile,
			Set<String> setUsedAttrValue, Long userProfileId, String msg,
			Set<String> userProfileCount) {

		UserProfile forAttrUserProfile = QueryUtil.findBoById(UserProfile.class,
				userProfileId, this);

		if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
				&& setUsedAttrValue
						.contains(String.valueOf(forAttrUserProfile.getAttributeValue()))) {
			String tempStr[] = { msg };
			strErrorMessage = getText("error.template.sameAttributeValue", tempStr);
			return false;
		}
		UserProfileAttribute userProfileAttr = forAttrUserProfile.getUserProfileAttribute();
		if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
				&& userProfileAttr != null) {
			for (SingleTableItem singleTable : userProfileAttr.getItems()) {
				String[] strAttrValue = singleTable.getAttributeValue().split(",");
				for (String attrValue : strAttrValue) {
					String[] attrRange = attrValue.split("-");
					if (attrRange.length > 1) {
						for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
								.parseInt(attrRange[1]) + 1; addCount++) {
							if (setUsedAttrValue.contains(String.valueOf(addCount))) {
								String tempStr[] = {msg};
								strErrorMessage = getText("error.template.sameAttributeValue",
										tempStr);
								return false;
							}
						}
					} else {
						if (setUsedAttrValue.contains(attrRange[0])) {
							String tempStr[] = {msg};
							strErrorMessage = getText("error.template.sameAttributeValue", tempStr);
							return false;
						}
					}
				}
			}

			for (SingleTableItem singleTable : userProfileAttr.getItems()) {
				String[] strAttrValue = singleTable.getAttributeValue().split(",");
				for (String attrValue : strAttrValue) {
					String[] attrRange = attrValue.split("-");
					if (attrRange.length > 1) {
						for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
								.parseInt(attrRange[1]) + 1; addCount++) {
							setUsedAttrValue.add(String.valueOf(addCount));
						}
					} else {
						setUsedAttrValue.add(attrRange[0]);
					}
				}
			}

		}
		setUsedUserProfile.add(forAttrUserProfile.getId().toString());
		setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
		if (userProfileAttr != null) {
			userProfileCount.add(userProfileAttr.getId().toString());
		}
		return true;
	}

	public boolean checkCacAirTime(ConfigTemplate modifyConfigTemplate) {
		int cacPercent = 0;
		if (modifyConfigTemplate.getMgmtServiceOption() != null) {
			if (!modifyConfigTemplate.getMgmtServiceOption().getDisableCallAdmissionControl()){
				cacPercent = cacPercent
					+ modifyConfigTemplate.getMgmtServiceOption().getRoamingGuaranteedAirtime();
			}
		} else {
			cacPercent = cacPercent + 20;
		}
		Set<Long> setUserProfile = new HashSet<Long>();
		for (ConfigTemplateSsid configTemplateSsid : modifyConfigTemplate.getSsidInterfaces().values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
					if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId())) {
						cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileDefault().getGuarantedAirTime();
						setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
					if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId())) {
						cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getGuarantedAirTime();
						setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId());
					}
				}
				if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
					for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
							.getRadiusUserProfile()) {
						if (!setUserProfile.contains(tempUser.getId())) {
							cacPercent = cacPercent + tempUser.getGuarantedAirTime();
							setUserProfile.add(tempUser.getId());
						}
					}
				}
//				if (configTemplateSsid.getSsidProfile().getDevicePolicy() != null) {
//					DevicePolicy devicePolicy = QueryUtil.findBoById(DevicePolicy.class, 
//						configTemplateSsid.getSsidProfile().getDevicePolicy().getId(), this);
//					for (DevicePolicyRule rule : devicePolicy.getRules()) {
//						setUserProfile.add(rule.getUserProfile().getId());
//						UserProfile tempUser = rule.getUserProfile();
//						if (!setUserProfile.contains(tempUser.getId())) {
//							cacPercent = cacPercent + tempUser.getGuarantedAirTime();
//							setUserProfile.add(tempUser.getId());
//						}
//					}
//				}
			}
		}
		
		// check reassign user profile
		if (!setUserProfile.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<Long>();
			for (Long upId : setUserProfile) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (upObj.isEnableAssign()) {
					for (DevicePolicyRule rule : upObj.getAssignRules()) {
						if (!setUserProfile.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
							upObj = QueryUtil.findBoById(UserProfile.class, rule.getUserProfileId());
							cacPercent = cacPercent + upObj.getGuarantedAirTime();
							newSetUserProfile.add(rule.getUserProfileId());
						}
					}
				}
			}
		}

		if (cacPercent > 100) {
			strErrorMessage = getText("error.template.guaranteedAirTime");
			return false;
		}
		return true;
	}
	
	public int getSsidCount() {
		FilterParams ssidFilter = new FilterParams("ssidName not in(:s1)", 
				new Object[]{Arrays.asList(BeParaModule.SSID_PROFILE_NAMES)});
		List<SsidProfile> ssids = QueryUtil.executeQuery(SsidProfile.class, null, ssidFilter, getDomainId());
		
		if(ssids != null) {
			return ssids.size();
		} else {
			return 0;
		}
	}
	
	// fnr add for apply new radius server
	
//	private int newRadiusType=Cwp.CWP_EXTERNAL;
	private String newRadiusName;
	private Long newRadiusPrimaryIp;
	private String inputNewRadiusPrimaryIpValue="";
	private String newRadiusSecret;
	private Long newRadiusSecondaryIp;
	private String inputNewRadiusSecondaryIpValue="";
	private String newRadiusSecondSecret;
	
	private String newSelfUserProfileName;
	private short newSelfAttributeValue=1;
	private Long newSelfVlanId;
	private String inputNewSelfVlanValue="";
//	private int newSelfGuestAccess = UserProfile.USER_CATEGORY_CUSTOM;
	
	private String newDefaultUserProfileName;
	private short newDefaultAttributeValue=1;
	private Long newDefaultVlanId;
	private String inputNewDefaultVlanValue="";
//	private int newDefaultGuestAccess = UserProfile.USER_CATEGORY_CUSTOM;
	
	private String newOptionUserProfileName;
	private short newOptionAttributeValue=1;
	private Long newOptionVlanId;
	private String inputNewOptionVlanValue="";
//	private int newOptionGuestAccess = UserProfile.USER_CATEGORY_CUSTOM;
	
	public void prepareAvailableVlan(){
		availableVlan = getBoCheckItems("vlanName", Vlan.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);
	}
	
	public void prepareAvailableIpAddress() {
		availableIpAddress = getIpObjectsByIpAndName();
	}

	/*
	 * Change log:<br>
	 * May 20, 2011 5:42:02 PM, Remove the checking so APs in NEW state will also be included in the dropdown list
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAvailablePrimaryHiveApIpAddress() {
		//String whereStr = "connected = :s1 and simulated = :s2";
		String whereStr = "simulated = :s1";
		Object[] values = new Object[]{false};
	    FilterParams apFilterParams = new FilterParams(whereStr, values);
		List<String> tmpIpAddress = (List<String>) QueryUtil.executeQuery("select hostName from " + HiveAp.class.getSimpleName()
	    		, null, apFilterParams, getDomain().getId());
	    if(tmpIpAddress.isEmpty()){
	    	tmpIpAddress.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
	    }
	    return tmpIpAddress;
	}

	public List<String> getAvailableHiveApIpAddress() {
		return (List<String>) QueryUtil.executeQuery("select hostName from " + HiveAp.class.getSimpleName()
				, null, new FilterParams("dhcp", false),getDomain().getId());
	}

	// fnr add end
	
	private String primaryHiveApRadius;
	
	public String getPrimaryHiveApRadius() {
	    return primaryHiveApRadius;
	}
	
	public void setPrimaryHiveApRadius(String primaryHiveApRadius) {
	    this.primaryHiveApRadius = primaryHiveApRadius;
	}
	
	public int getRekeyPeriodPTK() {
		return rekeyPeriodPTK;
	}

    public void setRekeyPeriodPTK(int rekeyPeriodPTK) {
		this.rekeyPeriodPTK = rekeyPeriodPTK;
	}

	public int getReauthInterval() {
		return reauthInterval;
	}

	public void setReauthInterval(int reauthInterval) {
		this.reauthInterval = reauthInterval;
	}

	public boolean getEnabledRekeyPeriodPTK() {
		return enabledRekeyPeriodPTK;
	}

	public void setEnabledRekeyPeriodPTK(boolean enabledRekeyPeriodPTK) {
		this.enabledRekeyPeriodPTK = enabledRekeyPeriodPTK;
	}

	public boolean getEnabledReauthInterval() {
		return enabledReauthInterval;
	}

	public void setEnabledReauthInterval(boolean enabledReauthInterval) {
		this.enabledReauthInterval = enabledReauthInterval;
	}

	public OptionsTransfer getLocalUserGroupOptions() {
		return localUserGroupOptions;
	}

	public void setLocalUserGroupOptions(OptionsTransfer localUserGroupOptions) {
		this.localUserGroupOptions = localUserGroupOptions;
	}

	public void setLocalUserGroupIds(List<Long> localUserGroupIds) {
		this.localUserGroupIds = localUserGroupIds;
	}

	public Set<CheckItem> getReferenceWLAN() {
		return referenceWLAN;
	}

	public Long getEditMacFilterId() {
		return editMacFilterId;
	}

	public void setEditMacFilterId(Long editMacFilterId) {
		this.editMacFilterId = editMacFilterId;
	}

	public Long getEditScheduleId() {
		return editScheduleId;
	}

	public void setEditScheduleId(Long editScheduleId) {
		this.editScheduleId = editScheduleId;
	}

	public Long getEditSelectUserProfileId() {
		return editSelectUserProfileId;
	}

	public void setEditSelectUserProfileId(Long editSelectUserProfileId) {
		this.editSelectUserProfileId = editSelectUserProfileId;
	}

	public Long getEditLocalUserGroupId() {
		return editLocalUserGroupId;
	}

	public void setEditLocalUserGroupId(Long editLocalUserGroupId) {
		this.editLocalUserGroupId = editLocalUserGroupId;
	}

	public Long getUserPolicyId() {
		return userPolicyId;
	}

	public void setUserPolicyId(Long userPolicyId) {
		this.userPolicyId = userPolicyId;
	}

	public List<CheckItem> getUserPolicyProfiles() {
		return userPolicyProfiles;
	}

	public int getPskUserLimit() {
		return pskUserLimit;
	}

	public void setPskUserLimit(int pskUserLimit) {
		this.pskUserLimit = pskUserLimit;
	}

	public boolean getEnabledPskUserLimit() {
		return enabledPskUserLimit;
	}

	public void setEnabledPskUserLimit(boolean enabledPskUserLimit) {
		this.enabledPskUserLimit = enabledPskUserLimit;
	}

//	public int getNewRadiusType() {
//		return newRadiusType;
//	}
//
//	public void setNewRadiusType(int newRadiusType) {
//		this.newRadiusType = newRadiusType;
//	}

	public String getNewRadiusName() {
		return newRadiusName;
	}

	public void setNewRadiusName(String newRadiusName) {
		this.newRadiusName = newRadiusName;
	}

	public Long getNewRadiusPrimaryIp() {
		return newRadiusPrimaryIp;
	}

	public void setNewRadiusPrimaryIp(Long newRadiusPrimaryIp) {
		this.newRadiusPrimaryIp = newRadiusPrimaryIp;
	}

	public String getNewRadiusSecret() {
		return newRadiusSecret;
	}

	public void setNewRadiusSecret(String newRadiusSecret) {
		this.newRadiusSecret = newRadiusSecret;
	}

	public Long getNewRadiusSecondaryIp() {
		return newRadiusSecondaryIp;
	}

	public void setNewRadiusSecondaryIp(Long newRadiusSecondaryIp) {
		this.newRadiusSecondaryIp = newRadiusSecondaryIp;
	}

	public String getNewRadiusSecondSecret() {
		return newRadiusSecondSecret;
	}

	public void setNewRadiusSecondSecret(String newRadiusSecondSecret) {
		this.newRadiusSecondSecret = newRadiusSecondSecret;
	}

	public String getNewSelfUserProfileName() {
		return newSelfUserProfileName;
	}

	public void setNewSelfUserProfileName(String newSelfUserProfileName) {
		this.newSelfUserProfileName = newSelfUserProfileName;
	}

	public short getNewSelfAttributeValue() {
		return newSelfAttributeValue;
	}

	public void setNewSelfAttributeValue(short newSelfAttributeValue) {
		this.newSelfAttributeValue = newSelfAttributeValue;
	}

	public Long getNewSelfVlanId() {
		return newSelfVlanId;
	}

	public void setNewSelfVlanId(Long newSelfVlanId) {
		this.newSelfVlanId = newSelfVlanId;
	}

//	public int getNewSelfGuestAccess() {
//		return newSelfGuestAccess;
//	}
//
//	public void setNewSelfGuestAccess(int newSelfGuestAccess) {
//		this.newSelfGuestAccess = newSelfGuestAccess;
//	}

	public String getNewDefaultUserProfileName() {
		return newDefaultUserProfileName;
	}

	public void setNewDefaultUserProfileName(String newDefaultUserProfileName) {
		this.newDefaultUserProfileName = newDefaultUserProfileName;
	}

	public short getNewDefaultAttributeValue() {
		return newDefaultAttributeValue;
	}

	public void setNewDefaultAttributeValue(short newDefaultAttributeValue) {
		this.newDefaultAttributeValue = newDefaultAttributeValue;
	}

	public Long getNewDefaultVlanId() {
		return newDefaultVlanId;
	}

	public void setNewDefaultVlanId(Long newDefaultVlanId) {
		this.newDefaultVlanId = newDefaultVlanId;
	}

//	public int getNewDefaultGuestAccess() {
//		return newDefaultGuestAccess;
//	}
//
//	public void setNewDefaultGuestAccess(int newDefaultGuestAccess) {
//		this.newDefaultGuestAccess = newDefaultGuestAccess;
//	}

	public String getNewOptionUserProfileName() {
		return newOptionUserProfileName;
	}

	public void setNewOptionUserProfileName(String newOptionUserProfileName) {
		this.newOptionUserProfileName = newOptionUserProfileName;
	}

	public short getNewOptionAttributeValue() {
		return newOptionAttributeValue;
	}

	public void setNewOptionAttributeValue(short newOptionAttributeValue) {
		this.newOptionAttributeValue = newOptionAttributeValue;
	}

	public Long getNewOptionVlanId() {
		return newOptionVlanId;
	}

	public void setNewOptionVlanId(Long newOptionVlanId) {
		this.newOptionVlanId = newOptionVlanId;
	}

//	public int getNewOptionGuestAccess() {
//		return newOptionGuestAccess;
//	}
//
//	public void setNewOptionGuestAccess(int newOptionGuestAccess) {
//		this.newOptionGuestAccess = newOptionGuestAccess;
//	}

	public String getInputNewRadiusPrimaryIpValue() {
		if (null != getNewRadiusPrimaryIp()) {
			for (CheckItem item : availableIpAddress) {
				if (item.getId().longValue() == getNewRadiusPrimaryIp().longValue()) {
					inputNewRadiusPrimaryIpValue = item.getValue();
					break;
				}
			}
		}
		return inputNewRadiusPrimaryIpValue;
	}
	public String getInputNewRadiusSecondaryIpValue() {
		if (null != getNewRadiusSecondaryIp()) {
			for (CheckItem item : availableIpAddress) {
				if (item.getId().longValue() == getNewRadiusSecondaryIp().longValue()) {
					inputNewRadiusSecondaryIpValue = item.getValue();
					break;
				}
			}
		}
		return inputNewRadiusSecondaryIpValue;
	}

	public void setInputNewRadiusPrimaryIpValue(String inputNewRadiusPrimaryIpValue) {
		this.inputNewRadiusPrimaryIpValue = inputNewRadiusPrimaryIpValue;
	}

	public List<CheckItem> getAvailableIpAddress() {
		return availableIpAddress;
	}

	public List<CheckItem> getAvailableVlan() {
		return availableVlan;
	}

	public void setInputNewRadiusSecondaryIpValue(String inputNewRadiusSecondaryIpValue) {
		this.inputNewRadiusSecondaryIpValue = inputNewRadiusSecondaryIpValue;
	}

	public String getInputNewSelfVlanValue() {
		if (null != getNewSelfVlanId()) {
			for (CheckItem item : availableVlan) {
				if (item.getId().longValue() == getNewSelfVlanId().longValue()) {
					inputNewSelfVlanValue = item.getValue();
					break;
				}
			}
		}
		return inputNewSelfVlanValue;
	}

	public void setInputNewSelfVlanValue(String inputNewSelfVlanValue) {
		this.inputNewSelfVlanValue = inputNewSelfVlanValue;
	}

	public String getInputNewDefaultVlanValue() {
		if (null != getNewDefaultVlanId()) {
			for (CheckItem item : availableVlan) {
				if (item.getId().longValue() == getNewDefaultVlanId().longValue()) {
					inputNewDefaultVlanValue = item.getValue();
					break;
				}
			}
		}
		return inputNewDefaultVlanValue;
	}

	public void setInputNewDefaultVlanValue(String inputNewDefaultVlanValue) {
		this.inputNewDefaultVlanValue = inputNewDefaultVlanValue;
	}

	public String getInputNewOptionVlanValue() {
		if (null != getNewOptionVlanId()) {
			for (CheckItem item : availableVlan) {
				if (item.getId().longValue() == getNewOptionVlanId().longValue()) {
					inputNewOptionVlanValue = item.getValue();
					break;
				}
			}
		}
		return inputNewOptionVlanValue;
	}

	public void setInputNewOptionVlanValue(String inputNewOptionVlanValue) {
		this.inputNewOptionVlanValue = inputNewOptionVlanValue;
	}

	public OptionsTransfer getHiveApLocalUserGroupOptions() {
		return hiveApLocalUserGroupOptions;
	}

	public List<Long> getHiveApLocalUserGroupIds() {
		return hiveApLocalUserGroupIds;
	}

	public void setHiveApLocalUserGroupIds(List<Long> hiveApLocalUserGroupIds) {
		this.hiveApLocalUserGroupIds = hiveApLocalUserGroupIds;
	}

	public Long getEditLocalUserGroupIdForRadius() {
		return editLocalUserGroupIdForRadius;
	}

	public void setEditLocalUserGroupIdForRadius(Long editLocalUserGroupIdForRadius) {
		this.editLocalUserGroupIdForRadius = editLocalUserGroupIdForRadius;
	}
	
	// fnr add for user category
	
	private Long newUserInfoVlanId;
	private String inputNewUserProfileVlanValue="";
	
	public String getHideUserInternetAccess(){
		if (isEasyMode()){
			if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST 
					&& getDataSource().getShowExpressUserAccess()){
				return "";
			} else {
				return "none";
			}
		}
		return "none";
	}
	public String getHideUserNumberPsk(){
		if (isEasyMode() && getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
			if (getDataSource().getUserCategory() != SsidProfile.USER_CATEGORY_GUEST){
				if (getDataSource().getUserPskMethod() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
					return "";
				} else {
					return "none";
				}
			} else {
				return "";
			}
		}
		return "none";
	}
	public String getHideUserPskMethod(){
		if (isEasyMode() && getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
			if (getDataSource().getUserCategory() != SsidProfile.USER_CATEGORY_GUEST){
				return "";
			}
		}
		return "none";
	}
	public String getHideUserRateLimit(){
		if (isEasyMode()){
			if (getDataSource().getUserCategory() != SsidProfile.USER_CATEGORY_GUEST){
				return "none";
			} else if (getDataSource().getShowExpressUserAccess()) {
				return "";
			}
		}
		return "none";
	}
	public String getHideUserManagerTr(){
		if (isEasyMode()){
			if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK) {
				return "";
			}
		}
		return "none";
	}
	
	public String getHideMacAuthTr(){
		if (isEasyMode()){
			if (getDataSource().getUserCategory()==SsidProfile.USER_CATEGORY_GUEST) {
				return "none";
			}
		}
		return "";
	}
	
	// end add

	public Long getNewUserInfoVlanId() {
		return newUserInfoVlanId;
	}

	public void setNewUserInfoVlanId(Long newUserInfoVlanId) {
		this.newUserInfoVlanId = newUserInfoVlanId;
	}
	
	public String getInputNewUserProfileVlanValue() {
		if (null != getNewUserInfoVlanId()) {
			for (CheckItem item : availableVlan) {
				if (item.getId().longValue() == getNewUserInfoVlanId().longValue()) {
					inputNewUserProfileVlanValue = item.getValue();
					break;
				}
			}
		}
		return inputNewUserProfileVlanValue;
	}

	public void setInputNewUserProfileVlanValue(String inputNewUserProfileVlanValue) {
		this.inputNewUserProfileVlanValue = inputNewUserProfileVlanValue;
	}

	public Long getSimpleModeUserProfileId() {
		return simpleModeUserProfileId;
	}

	public void setSimpleModeUserProfileId(Long simpleModeUserProfileId) {
		this.simpleModeUserProfileId = simpleModeUserProfileId;
	}

	public String getMoreSettingWord() {
		return "Advanced User Profile Settings...";
//		if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_GUEST) {
//			return "Advanced firewall, QoS, and tunnel settings";
//		} else if (getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_EMPLOOYEE ||
//				getDataSource().getUserCategory() == SsidProfile.USER_CATEGORY_VOICE) {
//			return "Advanced firewall, QoS, and SLA settings";
//		} else {
//			return "Advanced firewall, QoS, tunnel, and SLA settings";
//		}
	}

	public String getExpressEmployeeTitle() {
		return getText("config.ssid.express.employee.title");
	}
	public String getExpressGuestTitle() {
		return getText("config.ssid.express.guest.title");
	}

	public boolean getEnabledRekeyPeriod2() {
		return enabledRekeyPeriod2;
	}

	public void setEnabledRekeyPeriod2(boolean enabledRekeyPeriod2) {
		this.enabledRekeyPeriod2 = enabledRekeyPeriod2;
	}

	public boolean getEnabledRekeyPeriodGMK() {
		return enabledRekeyPeriodGMK;
	}

	public void setEnabledRekeyPeriodGMK(boolean enabledRekeyPeriodGMK) {
		this.enabledRekeyPeriodGMK = enabledRekeyPeriodGMK;
	}

	public int getRekeyPeriod2() {
		return rekeyPeriod2;
	}

	public void setRekeyPeriod2(int rekeyPeriod2) {
		this.rekeyPeriod2 = rekeyPeriod2;
	}
	
	public boolean getDisabledName() {
		if (isFullMode()){
			return getDataSource() != null && getDataSource().getId() != null;
		} else {
			return !getDataSource().getShowExpressUserAccess() || getDataSource() != null && getDataSource().getId() != null;
		}
	}

	public short[] getNRateSetType0() {
		return nRateSetType0;
	}

	public void setNRateSetType0(short[] rateSetType0) {
		nRateSetType0 = rateSetType0;
	}

	public short[] getNRateSetType1() {
		return nRateSetType1;
	}

	public void setNRateSetType1(short[] rateSetType1) {
		nRateSetType1 = rateSetType1;
	}
	
	public short[] getNRateSetType2() {
		return nRateSetType2;
	}

	public void setNRateSetType2(short[] rateSetType2) {
		nRateSetType2 = rateSetType2;
	}

    private String hideHiveRadiusServerInfoDiv;

    public String getHideHiveRadiusServerInfoDiv() {
        return hideHiveRadiusServerInfoDiv;
    }

    public void setHideHiveRadiusServerInfoDiv(String hideHiveRadiusServerInfoDiv) {
        this.hideHiveRadiusServerInfoDiv = hideHiveRadiusServerInfoDiv;
    }

    private void showHiveRadiusServerInfo(boolean flag){
        if (flag)
            setHideHiveRadiusServerInfoDiv("");
        else
            setHideHiveRadiusServerInfoDiv("none");
    }
    
    private String showAdIntegrationDiv;
    
    public String getShowAdIntegrationDiv() {
        if(getDataSource().isEnableADIntegration())
            showAdIntegrationDiv="";
        else
            showAdIntegrationDiv="none";
        return showAdIntegrationDiv;
    }
    private String showTestAdDiv;
    
    public void setShowTestAdDiv(String showTestAdDiv) {
        this.showTestAdDiv = showTestAdDiv;
    }

    public String getShowTestAdDiv() {
        return showTestAdDiv;
    }

    private String testADDisabled;
    
    public String getTestADDisabled() {
        return testADDisabled;
    }

    public void setTestADDisabled(String testADDisabled) {
        this.testADDisabled = testADDisabled;
    }

    private void resetAdIntegrationValues(){
        getDataSource().setAdServerIpAddress(null);
        getDataSource().setAdDomainAdmin(null);
        getDataSource().setAdDomainAdminPasswd(null);
        getDataSource().setBaseDN(null);

        getDataSource().setAdDomainName(null);
        getDataSource().setAdDomainFullName(null);
        getDataSource().setAdDomainTestUser(null);
        getDataSource().setAdDomainTestUserPasswd(null);
        
        getDataSource().setLdapSaslWrapping(ActiveDirectoryOrOpenLdap.CLIENT_LDAP_SASL_WRAPPING_PLAIN);
    }
    
    public List<TextItem> getPpskServerList(){
		List<TextItem> resItem = new ArrayList<TextItem>();
		resItem.add(new TextItem("", ""));
		String queryStr = "select cfgIpAddress from "+HiveAp.class.getSimpleName()+" bo ";
		List<?> serverList = QueryUtil.executeQuery(queryStr, null, 
				new FilterParams("dhcp = :s1", 
						new Object[] {false}), 
				this.getDomainId());
		for(Object obj : serverList){
			String ipAddr = (String)obj;
			resItem.add(new TextItem(ipAddr, ipAddr));
		}
		return resItem;
	}
	
	public boolean isBackFromUser() {
		return backFromUser;
	}

	public void setBackFromUser(boolean backFromUser) {
		this.backFromUser = backFromUser;
	}

	private String exBackFrom;

	/**
	 * getter of exBackFrom
	 * @return the exBackFrom
	 */
	public String getExBackFrom() {
		return exBackFrom;
	}

	/**
	 * setter of exBackFrom
	 * @param exBackFrom the exBackFrom to set
	 */
	public void setExBackFrom(String exBackFrom) {
		this.exBackFrom = exBackFrom;
	}
	// for the express mode
    private String adDomainFullName;
    private String adDomainAdmin;
    private String adDomainAdminPasswd;
    private String adDomainTestUser;
    private String adDomainTestUserPasswd;
    
    private final String DEFAULT_DNS_SERVER = "8.8.8.8";
	//--hidden field in jsp
	private String adDomainName;
    private String adServerIpAddress;
	private String baseDN;
	//--if value is equals 1, discard the join information
	private int discardJoinInfo;
	private short ldapSaslWrapping;

	public short getLdapSaslWrapping() {
		return ldapSaslWrapping;
	}

	public void setLdapSaslWrapping(short ldapSaslWrapping) {
		this.ldapSaslWrapping = ldapSaslWrapping;
	}

	public String getAdDomainFullName() {
		return adDomainFullName;
	}

	public void setAdDomainFullName(String adDomainFullName) {
		this.adDomainFullName = adDomainFullName;
	}

	public String getAdDomainAdmin() {
		return adDomainAdmin;
	}

	public void setAdDomainAdmin(String adDomainAdmin) {
		this.adDomainAdmin = adDomainAdmin;
	}

	public String getAdDomainAdminPasswd() {
		return adDomainAdminPasswd;
	}

	public void setAdDomainAdminPasswd(String adDomainAdminPasswd) {
		this.adDomainAdminPasswd = adDomainAdminPasswd;
	}

	public String getAdDomainTestUser() {
		return adDomainTestUser;
	}

	public void setAdDomainTestUser(String adDomainTestUser) {
		this.adDomainTestUser = adDomainTestUser;
	}

	public String getAdDomainTestUserPasswd() {
		return adDomainTestUserPasswd;
	}

	public void setAdDomainTestUserPasswd(String adDomainTestUserPasswd) {
		this.adDomainTestUserPasswd = adDomainTestUserPasswd;
	}

	public String getAdDomainName() {
		return adDomainName;
	}

	public void setAdDomainName(String adDomainName) {
		this.adDomainName = adDomainName;
	}

	public String getAdServerIpAddress() {
		return adServerIpAddress;
	}

	public void setAdServerIpAddress(String adServerIpAddress) {
		this.adServerIpAddress = adServerIpAddress;
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	public int getDiscardJoinInfo() {
		return discardJoinInfo;
	}

	public void setDiscardJoinInfo(int discardJoinInfo) {
		this.discardJoinInfo = discardJoinInfo;
	}

	// for the express mode
	public Long getRadiusPpskId() {
		return radiusPpskId;
	}

	public void setRadiusPpskId(Long radiusPpskId) {
		this.radiusPpskId = radiusPpskId;
	}
	
	private String getReturnPathWithFullMode(String normalPath, String fullModePath) {
		if (isFullMode() && getNetworkPolicyId4Drawer() > 0) {
			return fullModePath;
		}
		return normalPath;
	}
	
	private Long fromObjId;

	public Long getFromObjId() {
		return fromObjId;
	}

	public void setFromObjId(Long fromObjId) {
		this.fromObjId = fromObjId;
	}
	
	private String getLstForwardAndRemove() {
		String lstForwardTmp = getLstForward();
		if (!StringUtils.isBlank(this.getManualLstForward())) {
			this.removeLstForward();
		}
		return lstForwardTmp;
	}
	
//	public EnumItem[] getMdmTypeList() {
//		return SsidProfile.ENUM_MDM_ENROLL_TYPE;
//	}
	
	public boolean getDisplayNewUserOperation(){
		if(getWriteDisabled().equals("disabled")) {
			return false;
		}
		try {
			if(getFeatureNodes()==null || getFeatureNodes().get(L2_FEATURE_ADMINISTRATORS)==null) {
				return false;
			}
			
			HmPermission permission = getUserContext().getUserGroup().getFeaturePermissions()
					.get(L2_FEATURE_ADMINISTRATORS);
			if (permission == null) {
				return false;
			} else {
				if (!permission.hasAccess(HmPermission.OPERATION_WRITE)) {
					return false;
				}
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
    public void createSSID4IDMExpress(Long domainId,HmUser hmUser) {
        
        final String methodName = "createSSID4IDMExpress";
        final String defSSIDName = "IDM-SSID";
        
        ConfigTemplate defaultTemplate = HmBeParaUtil.getEasyModeDefaultTemplate(domainId);
        if(null == defaultTemplate) {
            log.warn(methodName, "Unable to get the default express network policy for domain ID: "+domainId);
            return;
        }
        defaultTemplate = QueryUtil.findBoById(
                ConfigTemplate.class, defaultTemplate.getId(), this);
        if(null == defaultTemplate) {
            log.warn(methodName, "Unable to get the default express network policy for domain ID: "+domainId);
            return;
        }
        
        try {
            SsidProfile ssid4IDM;
            if(defaultTemplate.getSsidInterfaces().isEmpty() || isNotExistSSID4IDM(defaultTemplate, defSSIDName)) {
                
                new HmCloudAuthCertMgmtImpl().refreshIDManagerStatus(domainId, hmUser);
                
                setUserContext(hmUser);
                setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
                
                ConfigTemplateSsid tmpConfigSsid = new ConfigTemplateSsid();
                tmpConfigSsid.setInterfaceName(defSSIDName);
                
                ssid4IDM = new SsidProfile();
                ssid4IDM.setSsid(defSSIDName);
                ssid4IDM.setSsidName(defSSIDName);
                ssid4IDM.setEnabledIDM(NmsUtil.isVhmEnableIdm(domainId));
                ssid4IDM.setSsidSecurity(new SsidSecurity());
                ssid4IDM.setAccessMode(SsidProfile.ACCESS_MODE_PSK);
                ssid4IDM.setMgmtKey(SsidProfile.KEY_MGMT_WPA2_PSK);
                ssid4IDM.setEncryption(SsidProfile.KEY_ENC_CCMP);
                ssid4IDM.setAuthentication(SsidProfile.KEY_AUT_EAP);
                ssid4IDM.setOwner(hmUser.getSwitchDomain() == null ? hmUser.getDomain()
                        : hmUser.getSwitchDomain());
                
                ssid4IDM.setHide(false);
                ssid4IDM.setBroadcase(false);
                ssid4IDM.setDtimSetting(1);
                ssid4IDM.setRtsThreshold(2346);
                ssid4IDM.setFragThreshold(2346);
                ssid4IDM.setPreauthenticationEnabled(false);
                ssid4IDM.setMacAuthEnabled(false);
                ssid4IDM.setEnabledUnscheduled(false);
                ssid4IDM.setEnabledwmm(true);
                ssid4IDM.setMaxClient(100);
                ssid4IDM.setUpdateInterval(60);
                ssid4IDM.setAgeOut(60);
                ssid4IDM.setAuthentication(0);
                Map<String, Object> map = new HashMap<String, Object>();
				map.put("dosType", DosType.MAC);
				ssid4IDM.setSsidDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				map.remove("dosType");
				map.put("dosType", DosType.MAC_STATION);
				ssid4IDM.setStationDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				map.remove("dosType");
				map.put("dosType", DosType.IP);
				ssid4IDM.setIpDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				ServiceFilter serviceFilter = HmBeParaUtil.getDefaultProfile(ServiceFilter.class,
						null);
				ssid4IDM.setServiceFilter(serviceFilter);
				UserProfile userProfile = HmBeParaUtil.getDefaultProfile(UserProfile.class, null);
				ssid4IDM.setUserProfileDefault(userProfile);
				ssid4IDM.setEnableGRateSet(true);
				ssid4IDM.setGRateSets(BeParaModuleDefImpl.getSsidGrateSettings(ssid4IDM.getSsidName()));
				ssid4IDM.setEnableARateSet(true);
				ssid4IDM.setARateSets(BeParaModuleDefImpl.getSsidArateSettings(ssid4IDM.getSsidName()));
				ssid4IDM.setEnableNRateSet(true);
				ssid4IDM.setNRateSets(BeParaModuleDefImpl.getSsidNrateSettings(ssid4IDM.getSsidName()));
				ssid4IDM.setEnableACRateSet(true);
				ssid4IDM.setAcRateSets(BeParaModuleDefImpl.getSsidAcRateSettings());
				// bind VLAN
				ssid4IDM.setUserVlan(HmBeParaUtil.getDefaultProfile(Vlan.class,null));
                
                setDataSource(SsidProfile.class);
                setSessionDataSource(ssid4IDM);
                // set Radio and Rate
                prepareRateSetInfo();
                
                if(functionExpressModeUpdateOrCreate()) {
                    // create SSID
                    Long newCreateId = QueryUtil.createBo(ssid4IDM);
                    removeUnusedProfile();
                    
                    SsidProfile newCreateSsid = QueryUtil.findBoById(SsidProfile.class, newCreateId);
                    tmpConfigSsid.setSsidProfile(newCreateSsid);
                    defaultTemplate.getSsidInterfaces().put(newCreateId, tmpConfigSsid);
                    Date oldVerD = defaultTemplate.getVersion();
                    
                    defaultTemplate = QueryUtil.updateBo(defaultTemplate);
                    
                    // generate an event to configuration indication process
                    HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
                            defaultTemplate, ConfigurationChangedEvent.Operation.UPDATE,
                            oldVerD, true));
                }

                clearDataSource();
            }
        } catch (Exception e) {
            log.error(methodName, "Error when auto create the SSID for IDM.", e);
        }
        
    }

    public boolean isNotExistSSID4IDM(ConfigTemplate defaultTemplate, String defSSIDName) {
        for (ConfigTemplateSsid templateSSID : defaultTemplate.getSsidInterfaces().values()) {
            if (null != templateSSID.getSsidProfile()
                    && (templateSSID.getSsidProfile().isEnabledIDM() || defSSIDName
                            .equals(templateSSID.getSsidProfile().getSsidName()))) {
                return false;
            }
        }
        return true;
    }
    
    // support 11ac MCS
    private boolean[] streamEnable;
    private short[] mcsValue;
    
    public void init11acRateSets(){
		if(getDataSource().getAcRateSets().isEmpty()){
			List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
			for (short i = Tx11acRateSettings.STREAM_TYPE_SINGLE; i <= Tx11acRateSettings.STREAM_TYPE_THREE; i ++){
				Tx11acRateSettings acRateSet = new Tx11acRateSettings();
				acRateSet.setStreamType(i);
				acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
				acRateList.add(acRateSet);
			}
			getDataSource().setAcRateSets(acRateList);
		}
	}
    
    public void update11acRateSet(){
    	List<Tx11acRateSettings> acRateSets = getDataSource().getAcRateSets();
    	for(int i = 0; i < acRateSets.size(); i ++){
    		acRateSets.get(i).setStreamEnable(streamEnable[i]);
    		acRateSets.get(i).setMcsValue(mcsValue[i]);
    	}
    	getDataSource().setAcRateSets(acRateSets);
    }

	public boolean[] getStreamEnable() {
		return streamEnable;
	}

	public void setStreamEnable(boolean[] streamEnable) {
		this.streamEnable = streamEnable;
	}

	public short[] getMcsValue() {
		return mcsValue;
	}

	public void setMcsValue(short[] mcsValue) {
		this.mcsValue = mcsValue;
	}
}
