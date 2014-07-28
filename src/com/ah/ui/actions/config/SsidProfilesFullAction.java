package com.ah.ui.actions.config;

/*
 * @author Fisher
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.ui.actions.hiveap.ConfigTemplateAction;
import com.ah.ui.actions.monitor.enrolledclient.tools.URLUtils;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.userprofile.selection.SsidUserProfileSelectionImpl;

public class SsidProfilesFullAction extends IDMSupportAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(SsidProfilesFullAction.class.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_SSID = 2;
	public static final int COLUMN_ACCESS_SECURITY = 3;
	public static final int COLUMN_CWPUSE = 4;
	public static final int COLUMN_ENABLEDMACAUTH = 5;
	public static final int COLUMN_MAXCLIENT = 6;
	public static final int COLUMN_DESCRIPTION = 7;
	public static final int COLUMN_USED_CM = 8;
	public static final int COLUMN_USED_IDM = 9;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
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
        case COLUMN_USED_CM:
            code = "config.ssid.column.cm";
            break;
        case COLUMN_USED_IDM:
            code = "config.ssid.column.idm";
            break;			
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	public String execute() throws Exception {
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.ssid"))) {
					return getLstForward();
				}
				refreshIDMStatus();
				
				setTabId(0);
				setSessionDataSource(new SsidProfile());
				prepareDependentObjects();
				prepareRateSetInfo();
				return returnResultKeyWord(INPUT,"ssidOnly");
			} else if (("create" + getLstForward()).equals(operation) ||
					"create".equals(operation)) {
				prepareSetSaveObjects();
				prepareSetSsidSecurity();
				updateRateSetInfo();
				if (checkNameExists("ssidName", getDataSource().getSsidName())) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
						&& getDataSource().isEnablePpskSelfReg()){
					if (checkNameExists("ssid", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
					if (checkHiveNameExists("hiveName", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
					
//					if (checkNameExists("ppskOpenSsid", getDataSource().getPpskOpenSsid())) {
					if (checkPpskOpenSsid(operation,"ppskOpenSsid",getDataSource().getPpskOpenSsid(),getDataSource().isEnableSingleSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
					if (getDataSource().getSsidName().equalsIgnoreCase(getDataSource().getPpskOpenSsid())
							|| getDataSource().getSsid().equalsIgnoreCase(getDataSource().getPpskOpenSsid())){
						prepareDependentObjects();
						addActionError(MgrUtil.getUserMessage("error.hiveap.ppsk.sameName"));
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
				}
				if (checkHiveNameExists("hiveName", getDataSource().getSsid())) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}

				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				// one SSID profile cannot support more than 1024 PSK users
				if (!checkPskUserSize().equals("")) {
					addActionError(getText("error.template.morePskUsers"));
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				if (("create").equals(operation)){
					id=createBo(dataSource);
					if (isBlnJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("id", id);
						jsonObject.put("n", true);
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				if (!StringUtils.isBlank(this.getManualLstForward())) {
					this.addLstForward(this.getManualLstForward());
				}
				
				refreshIDMStatus();
				
				String returnWord;
				returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
					prepareRateSetInfo();
				}
				addLstTitle(getText("config.title.ssid.edit") + " '" + getChangedSsidName() + "'");
				return returnResultKeyWord(returnWord,"ssidOnly");
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				SsidProfile profile = (SsidProfile) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				setCloneFields(profile, profile);
				profile.setSsidName("");
				setSessionDataSource(profile);
				prepareDependentObjects();
				prepareRateSetInfo();
				addLstTitle(getText("config.title.ssid"));
				return returnResultKeyWord(INPUT,"ssidOnly");
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

					macFilters = (List<Long>) MgrUtil.getSessionAttribute("SELECT_MACFILTER");
					prepareSetSaveObjects();
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
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute("CURRENT_TABID").toString()));
					}
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
			} else if (("update" + getLstForward()).equals(operation) ||
					"update".equals(operation)) {
				if (dataSource != null) {
					prepareSetSaveObjects();
					prepareSetSsidSecurity();
					updateRateSetInfo();
				}
				if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
						&& getDataSource().isEnablePpskSelfReg()){
					if (checkNameExists("ssid", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
					if (checkHiveNameExists("hiveName", getDataSource().getPpskOpenSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}

//					if (checkNameExistsWhileUpdate(getDataSource().getId(),"ppskOpenSsid", getDataSource().getPpskOpenSsid())) {
					if (checkPpskOpenSsid(operation,"ppskOpenSsid", getDataSource().getPpskOpenSsid(),getDataSource().isEnableSingleSsid())) {
						prepareDependentObjects();
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
					if (getDataSource().getSsidName().equalsIgnoreCase(getDataSource().getPpskOpenSsid())
							|| getDataSource().getSsid().equalsIgnoreCase(getDataSource().getPpskOpenSsid())){
						prepareDependentObjects();
						addActionError(MgrUtil.getUserMessage("error.hiveap.ppsk.sameName"));
						return returnResultKeyWord(INPUT,"ssidOnly");
					}
				}
				
				if (checkHiveNameExists("hiveName", getDataSource().getSsid())) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				if (!checkMacFilterAction()) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				// one SSID profile cannot support more than 1024 PSK users
				if (!checkPskUserSize().equals("")) {
					addActionError(getText("error.template.morePskUsers"));
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}

				if (!checkRelativedTemplate(getDataSource())) {
					prepareDependentObjects();
					return returnResultKeyWord(INPUT,"ssidOnly");
				}
				
				if (getReferenceWlanPolicy(getDataSource().getId())) {
					for (CheckItem wlanpolicy : referenceWLAN) {
						ConfigTemplate configTemplate = QueryUtil.findBoById(
								ConfigTemplate.class, wlanpolicy.getId(), this);
						if (!checkAllCloseUpdate(resetConfigTemplateQos(configTemplate,getDataSource()))) {
							addActionError(MgrUtil.getUserMessage("action.error.newwork.policy.update.ssid",
									new String[]{wlanpolicy.getValue(), getDataSource().getSsidName()})
									+ " " + strErrorMessage);
							strErrorMessage = "";
							prepareDependentObjects();
							return returnResultKeyWord(INPUT,"ssidOnly");
						}
					}
				}

				if ("update".equals(operation)) {
//					if (isBlnJsonMode()){
//						setId(dataSource.getId());
//					}
					updateBo(dataSource);
					if (isBlnJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", false);
						return "json";
					} else {
						return returnPrepareBoList("update");
					}
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForwardAndRemove();
				}
			} else if ("newSsidDos".equals(operation) 
					|| "editSsidDos".equals(operation)
					|| "newMacDos".equals(operation)
					|| "editMacDos".equals(operation) 
					|| "newIpDos".equals(operation) 
					|| "editIpDos".equals(operation)
					|| "newMacFilter".equals(operation)
					|| "editMacFilter".equals(operation) 
					|| "newServiceFilter".equals(operation) 
					|| "editServiceFilter".equals(operation)
					|| "newConfigmdmPolicy".equals(operation)||"editConfigmdmPolicy".equals(operation)){
				if (isBlnJsonMode()){
					if("newSsidDos".equals(operation)
						|| "newMacDos".equals(operation)
						|| "editSsidDos".equals(operation)
						|| "editMacDos".equals(operation)
						|| "newIpDos".equals(operation)
						|| "editIpDos".equals(operation)
						|| "newMacFilter".equals(operation)
						|| "editMacFilter".equals(operation)
						|| "newServiceFilter".equals(operation)
						|| "editServiceFilter".equals(operation)
						|| "newConfigmdmPolicy".equals(operation)
						|| "editConfigmdmPolicy".equals(operation)){
						return operation;
					}
					return "json";
				}else{
					prepareSetSsidSecurity();
	 				prepareSetSaveObjects();
					updateRateSetInfo();
					clearErrorsAndMessages();
					addLstForward("ssidFull");
					MgrUtil.setSessionAttribute("SELECT_MACFILTER", macFilters);
	
					if ("newSsidDos".equals(operation) || "editSsidDos".equals(operation)
							|| "newMacDos".equals(operation) || "editMacDos".equals(operation)
							|| "newIpDos".equals(operation) || "editIpDos".equals(operation)
							|| "newMacFilter".equals(operation) || "editMacFilter".equals(operation)
							|| "newServiceFilter".equals(operation)
							|| "editServiceFilter".equals(operation)) {
						
						setTabId(2);
						
					} else if( "newConfigmdmPolicy".equals(operation)|| "editConfigmdmPolicy".equals(operation)){
						setTabId(4);
					}else {
						setTabId(0);
					}
					addLstTabId(tabId);
					return operation;
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForwardAndRemove();
				} else {
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
//				if (cwpId < 0) {
//					if (blnMacAuth || keyManagement == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
//							|| keyManagement == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
//							|| keyManagement == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
//							|| keyManagement == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
//						jsonObject.put("v", 4);
//					} else {
//						jsonObject.put("v", 3);
//					}
//				} else {
//					Cwp cwpProfile = QueryUtil.findBoById(Cwp.class, cwpId);
//					if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED 
//							|| cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
//						jsonObject.put("v", 4);
//						if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
							if (blnMacAuth && keyManagement == SsidProfile.KEY_MGMT_OPEN) {
								String ecwpServer = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION, 
										ConfigUtil.KEY_ECWPSERVER, ConfigUtil.VALUE_ECWP_DEFAULT);
								if (ecwpServer.equals(ConfigUtil.VALUE_ECWP_DEPAUL) 
										|| ecwpServer.equals(ConfigUtil.VALUE_ECWP_NNU)){
									jsonObject.put("f", 1);
								}
							}
//						}
//						
//					} else if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
//						if (blnMacAuth
//								|| keyManagement == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
//								|| keyManagement == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
//								|| keyManagement == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
//								|| keyManagement == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
//							jsonObject.put("v", 5);
//						} else {
//							jsonObject.put("v", 2);
//						}
//					} else if (cwpProfile.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
//						if (blnMacAuth) {
//							jsonObject.put("v", 4);
//						} else {
//							jsonObject.put("v", 3);
//						}
//					} else {
//						jsonObject.put("v", 5);
//					}
//				}
				return "json";
			} else if ("fetchAddRemoveUserGroup".equals(operation)){
				SsidProfile ssidPro= findBoById(SsidProfile.class, id, this);
				localUserGroupIds.clear();
				
				if(USER_GROUP_TARGET_PPSK.equals(this.userGroupTarget)) {
					for(LocalUserGroup group: ssidPro.getLocalUserGroups()){
						localUserGroupIds.add(group.getId());
					}
					if (ssidPro.isEnablePpskSelfReg()) {
						setEnabledPpskSelf(true);
						if (newAddGroupId!=null) {
							localUserGroupIds.clear();
							localUserGroupIds.add(newAddGroupId);
						}
					} else {
						setEnabledPpskSelf(false);
						if (newAddGroupId!=null) {
							localUserGroupIds.add(newAddGroupId);
						}
					}
				} else if (USER_GROUP_TARGET_RADIUS.equals(this.userGroupTarget)){
					for(LocalUserGroup group: ssidPro.getRadiusUserGroups()){
						localUserGroupIds.add(group.getId());
					}
				}
				
				return "userGroupSelectPage";
			} else if ("finishSelectLocalUserGroup".equals(operation)){
				jsonObject = new JSONObject();
				if (localUserGroupIds!=null && localUserGroupIds.size()>512){
					jsonObject.put("e", getText("error.template.morePskGroupPerTemplate"));
					return "json";
				}
				SsidProfile ssidPro = findBoById(SsidProfile.class, id, this);
				ssidPro = setSelectedLocalUserGroups(ssidPro);
				long count = getPskUserCount(ssidPro);
				if (count > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
					jsonObject.put("e", getText("error.template.morePskUsers"));
					return "json";
				}
				updateBo(ssidPro);
				jsonObject.put("t", true);
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
			} else if ("checkGASSIDs".equals(operation)) {
			    jsonObject = new JSONObject();
			    checkEnabledGASSIDs();
			    return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			if (isBlnJsonMode()) {
				log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				prepareDependentObjects();
				return returnResultKeyWord(INPUT,"ssidOnly");
			}
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

    protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isBlnJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	protected String returnPrepareBoList(String operation) throws Exception{
//		if("ssid".equals(this.getLastExConfigGuide())) {
//			setSessionDataSource(findBoById(boClass, getDataSource().getId(), this));
//			prepareEditSimpleModeRadiusValue();
//			prepareDependentObjects();
//			prepareRateSetInfo();
//			if ("create".equalsIgnoreCase(operation)) {
//				return  "guidedConfiguration";
//			} else {
//				return  "ssidEx";
//			}
//		} else {
			return prepareBoList();
//		}
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
			//remove client self register info by ssid name
			QueryUtil.bulkRemoveBos(AhClientEditValues.class,new FilterParams("type = :s1",
					new Object[] {AhClientEditValues.TYPE_SELF_REGISTER}),getDomain().getId());
			CacheMgmt.getInstance().removeClientEditValues(getDomain().getId(),AhClientEditValues.TYPE_SELF_REGISTER);
			
		} else if (getAllSelectedIds() != null && !getAllSelectedIds().isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && getAllSelectedIds().removeAll(defaultIds)) {
				hasRemoveDefaultValue=true;
				addActionMessage(MgrUtil.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
			}
			Collection<Long> toRemoveIds = getAllSelectedIds();
			
			if (!checkUsedProfile(toRemoveIds)) {
				return false;
			}
			setAllSelectedIds(null);
			count = removeBos(boClass, toRemoveIds);
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
		if (ids!=null && ids.size()>0) {
			List<?> ssidNameList = QueryUtil.executeQuery(
					"select ssidName from " + boClass.getSimpleName(),
					null, new FilterParams("id", ids));
			for(Object obj:ssidNameList){
				removeSsidName.add(obj.toString());
			}
		}
		int count = super.removeBos(boClass, ids);
		
		//remove client self register info by ssid name
		if (!removeSsidName.isEmpty()){
			QueryUtil.bulkRemoveBos(AhClientEditValues.class,new FilterParams("type = :s1 and ssidname in(:s2)",
					new Object[] {AhClientEditValues.TYPE_SELF_REGISTER,removeSsidName}),getDomain().getId());
			for(String ssidName: removeSsidName)
				CacheMgmt.getInstance().removeClientEditValues(getDomain().getId(),AhClientEditValues.TYPE_SELF_REGISTER,ssidName);
			}
		return count;
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(7);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_ACCESS_SECURITY));
		columns.add(new HmTableColumn(COLUMN_CWPUSE));
		columns.add(new HmTableColumn(COLUMN_ENABLEDMACAUTH));
		columns.add(new HmTableColumn(COLUMN_MAXCLIENT));
		columns.add(new HmTableColumn(COLUMN_USED_CM));
		columns.add(new HmTableColumn(COLUMN_USED_IDM));
		if (isFullMode()){
			columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		}
		
		return columns;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SSID_PROFILES_FULL);
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

	@Override
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
								addActionError(MgrUtil.getUserMessage("action.error.newwork.policy.update.ssid",new String[]{ wlanpolicy.getValue(),up.getSsidName()})
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
		if (referenceWLAN.size() == 0) {
			return false;
		} else if (referenceWLAN.size() == 1 && getLstForward().equals("configTemplate")) {
			HmBo configTemplate = (HmBo) MgrUtil.getSessionAttribute(ConfigTemplate.class
					.getSimpleName()
					+ "Source");
			if (configTemplate != null && configTemplate.getId() != null) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public void prepareDependentObjects() throws Exception {
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
		if (getDataSource().getServiceFilter() != null) {
			serviceFilter = getDataSource().getServiceFilter().getId();
		}
		if (getDataSource().getConfigmdmId() != null) {
			configmdmId = getDataSource().getConfigmdmId().getId();
		}

		enabledwmm = getDataSource().getEnabledwmm();
		enabled80211k = getDataSource().isEnabled80211k();
		enabled80211v = getDataSource().isEnabled80211v();
		enabled80211r = getDataSource().isEnabled80211r();
		enabledAcVoice = getDataSource().isEnabledAcVoice();
		
		prepareGetSsidSecurity();
		prepareAvailableFilters();
		prepareAvailableServiceFilters();
		prepareDosParameterProfiles();
		prepareMDMParameterProfiles();
//		prepareLocalUserGroups();
		prepareIDMStatus();
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
		if(configmdmId!=null){
			ConfigTemplateMdm configtemplemdm=findBoById(ConfigTemplateMdm.class, configmdmId);
			if (configtemplemdm == null && configmdmId != -1) {
				String tempStr[] = { getText("config.ssid.ipDos") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			getDataSource().setConfigmdmId(configtemplemdm);
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

		setSelectedMacFilters();
//		setSelectedLocalUserGroups();
		
        enabledCWP4IDM();
		
		if (!getDataSource().isCwpSelectEnabled()){
			getDataSource().setUserProfileSelfReg(null);
			getDataSource().setCwp(null);
			getDataSource().setUserPolicy(null);
		}
		if(getDataSource().isEnabledIDM()) {
		    // reset the unrelated fields
		    getDataSource().setRadiusAssignmentPpsk(null);
		    getDataSource().getLocalUserGroups().clear();
		    getDataSource().getRadiusUserGroups().clear();

		    if (!(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
                    && null != getDataSource().getCwp()
                    && getDataSource().getCwp().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH)) {
                getDataSource().setRadiusAssignment(null);
            }
		    
		    if(null != getDataSource().getCwp() 
		            && !getDataSource().getCwp().isIdmSelfReg()) {
		        getDataSource().setCwp(null);
		    }
		} else {
		    if(null != getDataSource().getCwp() 
		            && getDataSource().getCwp().isIdmSelfReg()) {
		        getDataSource().setCwp(null);
		    }
		}
		
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK ||
				getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X ||
						getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_DYNAMIC_WEP){
			getDataSource().setCwp(null);
			getDataSource().setUserProfileSelfReg(null);
		} else {
			getDataSource().setUserPolicy(null);
		}
			
		if (!getDataSource().isCwpSelectEnabled()
				&& !getDataSource().getMacAuthEnabled()
				&& !getDataSource().getEnabledUseGuestManager()
				&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_8021X
				&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
			getDataSource().setRadiusAssignment(null);
			if (getDataSource().getRadiusUserGroups()!=null) {
				getDataSource().getRadiusUserGroups().clear();
			}
			getDataSource().setEnableAssignUserProfile(false);
			
			if (getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK) {
				getDataSource().getRadiusUserProfile().clear();
			}
		}
		
		if (getDataSource().getAccessMode()!=SsidProfile.ACCESS_MODE_PSK){
			getDataSource().setPpskECwp(null);
			getDataSource().setRadiusAssignmentPpsk(null);
		} else {
		    // reset the flag when enable the IDM
		    if(getDataSource().isEnabledIDM()) {
		        getDataSource().setUserProfileSelfReg(null);
				
		        getDataSource().setEnabledUseGuestManager(false);
		        getDataSource().setMacAuthEnabled(false);
		        
		        getDataSource().setBlnBrAsPpskServer(false);
		        
		        getDataSource().getSsidSecurity().setBlnMacBindingEnable(false);
		    }
		    
			if (!getDataSource().isEnablePpskSelfReg() && !getDataSource().getSsidSecurity().isBlnMacBindingEnable()){
				getDataSource().setPpskServer(null);
			}
			if (!getDataSource().isEnablePpskSelfReg()){
				getDataSource().setPpskECwp(null);
				getDataSource().setRadiusAssignmentPpsk(null);
			} else {
				if (getDataSource().getPpskECwp()==null ||
						getDataSource().getPpskECwp().getPpskServerType()!=Cwp.PPSK_SERVER_TYPE_AUTH){
					getDataSource().setRadiusAssignmentPpsk(null);
				}
			}
		}
		
		if (getDataSource().isCwpSelectEnabled()){
			if (getDataSource().getCwp()!=null) {
				if (getDataSource().getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_REGISTERED
						&& !getDataSource().getMacAuthEnabled()
						&& !getDataSource().getEnabledUseGuestManager()){
					getDataSource().setUserProfileDefault(null);
					getDataSource().setRadiusAssignment(null);
					if (getDataSource().getRadiusUserGroups()!=null) {
						getDataSource().getRadiusUserGroups().clear();
					}
					getDataSource().setEnableAssignUserProfile(false);
					
					getDataSource().getRadiusUserProfile().clear();
				}
				
				if (getDataSource().getCwp().getRegistrationType()!=Cwp.REGISTRATION_TYPE_REGISTERED &&
						getDataSource().getCwp().getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
					getDataSource().setUserProfileSelfReg(null);
				}
				
				if (getDataSource().getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_EULA
						&& !getDataSource().getMacAuthEnabled()
						&& !getDataSource().getEnabledUseGuestManager()
						&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
						&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
						&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
						&& getDataSource().getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
					getDataSource().setRadiusAssignment(null);
					if (getDataSource().getRadiusUserGroups()!=null) {
						getDataSource().getRadiusUserGroups().clear();
					}
					getDataSource().setEnableAssignUserProfile(false);
					
					if (getDataSource().getAccessMode()!=SsidProfile.ACCESS_MODE_PSK) {
						getDataSource().getRadiusUserProfile().clear();
					}
				}
				// reset the IDM flag
                resetCWP4IDMEnabled();
			}
		}
		if (getDataSource().getAccessMode()!=SsidProfile.ACCESS_MODE_PSK 
		        || getDataSource().isEnabledIDM()){
			getDataSource().getLocalUserGroups().clear();
		}
		
		SsidUserProfileSelectionImpl ssidUpHelper = new SsidUserProfileSelectionImpl(getDataSource());
		ssidUpHelper.doUpSupportDataPrepare();
		if (!ssidUpHelper.isChkUserOnlyEnabled()) {
			getDataSource().setChkUserOnlyDefaults();
		}
		if (!ssidUpHelper.isDefaultUserProfileSupport()) {
			getDataSource().setUserProfileDefault(null);
		}
		if (!ssidUpHelper.isSelfUserProfileSupport()) {
			getDataSource().setUserProfileSelfReg(null);
		}
		if (!ssidUpHelper.isGuestUserProfileSupport()) {
		    getDataSource().setUserProfileGuest(null);
		}
		if (!ssidUpHelper.isAuthUserProfileSupport()) {
			getDataSource().getRadiusUserProfile().clear();
		}		
		
		if(!getDataSource().getBlnDisplayRadius()) {
			getDataSource().setEnableAssignUserProfile(false);
			getDataSource().setAssignUserProfileAttributeId(0);
			getDataSource().setAssignUserProfileVenderId(0);
			getDataSource().getRadiusUserGroups().clear();
			getDataSource().setEnableAssignUserProfile(false);
		}
		
		getDataSource().setEnabled80211k(enabled80211k);
		getDataSource().setEnabled80211r(enabled80211r);
		getDataSource().setEnabled80211v(enabled80211v);
		getDataSource().setEnabledAcVoice(enabledAcVoice);
		
		if(!getDataSource().getEnabledwmm()) {
			getDataSource().setEnabledAcVoice(false);
			getDataSource().setEnabledAcVideo(false);
			getDataSource().setEnabledAcBackground(false);
			getDataSource().setEnabledAcBesteffort(false);
			getDataSource().setEnabledUnscheduled(false);
		}
		
		if(getDataSource().isEnabledVoiceEnterprise()){
			getDataSource().setEnabledwmm(true);
			getDataSource().setEnabledAcVoice(true);
			getDataSource().setEnabled80211k(true);
			getDataSource().setEnabled80211r(true);
			getDataSource().setEnabled80211v(true);
		}
	}

	private Long dosId;
	private Long ssidDos;
	private Long stationDos;
	private Long configmdmId;
	private Long ipDos;
	private Long serviceFilter;

//	protected OptionsTransfer localUserGroupOptions;
	protected List<Long> localUserGroupIds=new ArrayList<Long>();
	
	private Long newAddGroupId;
	/**
	 * 1. PPSK
	 * 2. RADIUS
	 */
	public final static String USER_GROUP_TARGET_PPSK = "PPSK";
	public final static String USER_GROUP_TARGET_RADIUS = "RADIUS";
	
	private String userGroupTarget = null;
	
	// this field is used for open dialog when click local user group 
	private boolean enabledPpskSelf=false;
	
	private boolean blnMacAuth;

	private int keyManagement;

	private Long editMacFilterId;
//	private Long editLocalUserGroupId;
	
	// this is used for ajax mode
	private boolean blnJsonMode=false;

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
	public Long getConfigmdmId() {
		return configmdmId;
	}

	public void setConfigmdmId(Long configmdmId) {
		this.configmdmId = configmdmId;
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

				setPtkTimeOut(getDataSource().getSsidSecurity().getPtkTimeOut());
				setPtkRetries(getDataSource().getSsidSecurity().getPtkRetries());
				setGtkTimeOut(getDataSource().getSsidSecurity().getGtkTimeOut());
				setGtkRetries(getDataSource().getSsidSecurity().getGtkRetries());
				setEnable80211w(getDataSource().getSsidSecurity().isEnable80211w());
				setEnableBip(getDataSource().getSsidSecurity().isEnableBip());
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
			getDataSource().getSsidSecurity().setEnable80211w(enable80211w);
			getDataSource().getSsidSecurity().setEnableBip(enableBip);
			if(enable80211w){
				getDataSource().getSsidSecurity().setWpa2mfpType(wpa2mfpType);
			}else{
				getDataSource().getSsidSecurity().setWpa2mfpType(1);
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
	}

	protected void prepareDosParameterProfiles() {
		macDosParameterProfiles = getDosParameterProfiles(DosType.MAC);
		stationDosParameterProfiles = getDosParameterProfiles(DosType.MAC_STATION);
		ipDosParameterProfiles = getDosParameterProfiles(DosType.IP);
	}
	protected void prepareMDMParameterProfiles(){
		getMdmParameterProfiles();

	}

	protected List<CheckItem> getMdmParameterProfiles() {
		configmdmidParameterProfiles = getBoCheckItems("policyname", ConfigTemplateMdm.class, null);
	return configmdmidParameterProfiles;
}
	protected List<CheckItem> getDosParameterProfiles(DosType dosType) {
		return getBoCheckItems("dosPreventionName", DosPrevention.class, new FilterParams(
				"dosType", dosType));
	}
	
	public List<CheckItem> getLstLocalUserGroups() {
		if(USER_GROUP_TARGET_PPSK.equals(this.userGroupTarget)) {
		return getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType!=:s1", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_RADIUS}));
		} else {
			return getBoCheckItems("groupName", LocalUserGroup.class,
					new FilterParams("userType=:s1", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_RADIUS}));
		}
		
	}

//	public void prepareLocalUserGroups() {
//		Object lstCondition[] = new Object[1];
//		lstCondition[0] = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
//		List<CheckItem> availableFilters = getBoCheckItems("groupName", LocalUserGroup.class,
//				new FilterParams("userType!=:s1", lstCondition));
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
//
//		localUserGroupOptions = new OptionsTransfer(MgrUtil
//				.getUserMessage("config.configTemplate.wizard.avaliablePrivatePsk"), MgrUtil
//				.getUserMessage("config.configTemplate.wizard.selectPrivatePsk"), availableFilters,
//				getDataSource().getLocalUserGroups(), "id", "value", "localUserGroupIds", 512,
//				"250px", "6", true, "LocalUserGroup");
//	}

	protected SsidProfile setSelectedLocalUserGroups(SsidProfile ssidPro) throws Exception {
		Set<LocalUserGroup> ssidLocalUserGroups = null;
		
		if(USER_GROUP_TARGET_PPSK.equals(this.userGroupTarget)) {
			ssidLocalUserGroups = ssidPro.getLocalUserGroups();
		} else {
			ssidLocalUserGroups = ssidPro.getRadiusUserGroups();
		}
		
		ssidLocalUserGroups.clear();
		if (localUserGroupIds != null) {
			for (Long filterId : localUserGroupIds) {
				LocalUserGroup localUserGroup = findBoById(
						LocalUserGroup.class, filterId);
				if (localUserGroup != null) {
					ssidLocalUserGroups.add(localUserGroup);
				}
			}
		}
		
		if(USER_GROUP_TARGET_PPSK.equals(this.userGroupTarget)) {
		ssidPro.setLocalUserGroups(ssidLocalUserGroups);
		} else {
			ssidPro.setRadiusUserGroups(ssidLocalUserGroups);
		}
		
		return ssidPro;
	}

	public void prepareAvailableServiceFilters() {
		serviceFilterProfiles = getBoCheckItems("filterName", ServiceFilter.class, null);
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

	@Override
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
	
	protected String strErrorMessage;

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
	private boolean enableBip = false;
	private int wpa2mfpType = 1;
	
	public int getWpa2mfpType() {
		return wpa2mfpType;
	}

	public void setWpa2mfpType(int wpa2mfpType) {
		this.wpa2mfpType = wpa2mfpType;
	}

	public boolean isEnableBip() {
		return enableBip;
	}

	public void setEnableBip(boolean enableBip) {
		this.enableBip = enableBip;
	}

	public boolean isEnable80211w() {
		return enable80211w;
	}
	
	public void setEnable80211w(boolean enable80211w) {
		this.enable80211w = enable80211w;
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
	
	protected List<CheckItem> configmdmidParameterProfiles;

	protected List<CheckItem> ipDosParameterProfiles;

	protected List<CheckItem> serviceFilterProfiles;
	
	

	public List<CheckItem> getConfigmdmidParameterProfiles() {
		return configmdmidParameterProfiles;
	}

	public void setConfigmdmidParameterProfiles(
			List<CheckItem> configmdmidParameterProfiles) {
		this.configmdmidParameterProfiles = configmdmidParameterProfiles;
	}

	public List<CheckItem> getMacDosParameterProfiles() {
		return macDosParameterProfiles;
	}

	public List<CheckItem> getStationDosParameterProfiles() {
		return stationDosParameterProfiles;
	}

	public List<CheckItem> getIpDosParameterProfiles() {
		return ipDosParameterProfiles;
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
					SsidProfile.KEY_MGMT_WPA_EAP_802_1_X, SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X});
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
		if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_WPA){
			return "";
		} else {
			return "none";
		}
	}

	public String getHideFourth() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideFifth() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideThird_one() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
			if (keyType3 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideThird_two() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
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
	
	public String getHideKeyManagement() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
			return "none";
		}
		return "";
	}
	
	public String getHideKeyManagementNote() {
		if (getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WPA_EAP_802_1_X) {
			return "";
		}
		return "none";
	}

	public String getHideAuthMethord() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
				&& getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK) {
			return "";
		}
		return "none";
	}

	public String getHideOption() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN
				|| getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WEP_PSK) {
			return "none";
		}
		return "none";
	}
	
	public String getShowOption() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN
				|| getDataSource().getMgmtKey()==SsidProfile.KEY_MGMT_WEP_PSK) {
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


	public String getHideRadiusPAPCHAP() {
		if (getDataSource().getEnabledUseGuestManager() || getDataSource().getMacAuthEnabled()) {
			return "";
		}
		return "none";
	}
	/**
	 * beginning of Provisioning onboard enable setting
	 * function: Used to decide whether provisioning onboard enable should be displayed or not
	 * @return String
	 */
	public String getViewClientProfileURL(){
		return URLUtils.getViewClientProfileURL();
	}
	
	public boolean getHmSuperUser(){
		return !getUserContext().isSuperUser();
	}
	public String getHidePersonalViewURL(){
		if(getDataSource().isEnableProvisionPersonal()
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	public String getHidePrivateViewURL(){
		if(getDataSource().isEnableProvisionPrivate()
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	public String getHideEnterViewURL(){
		if(getDataSource().isEnableProvisionEnterprise()
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	public boolean getEnableClientManagement(){
		return QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement();
	}
	public String getHidePrivateProvisionEnable(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	
	public String getHidePersonalProvisionEnable(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	
	public String getHideEnterpriseProvisionEnable(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	public String getHideProvisionSsidEnable(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA
				&& getDataSource().isEnableProvisionPersonal()
				&& (getDataSource().getWpaOpenSsid() != null)
				&& getDataSource().getWpaOpenSsid() != ""
				&& QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain()).isEnableClientManagement()){
			return "";
		}
		return "none";
	}
	//enable Single SSID
	public String getHideSingleSsid(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				&& getDataSource().isEnableSingleSsid()
				&& getDataSource().isEnableProvisionPrivate()
				&& getDataSource().isEnablePpskSelfReg()){
			return "";
		}
		return "none";
	}
	
	public boolean getHideSingleCheckBox(){
		if(getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK
				&& getDataSource().getPrivateSsidModel() == SsidProfile.SINGLE_SSIDL_MODEL
				&& getDataSource().isEnableProvisionPrivate()){
			return false;
		}
		return true;
	}
	/**
	 * End of Provisioning onboard enable setting
	 */
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

    private void resetCWP4IDMEnabled() {
        if (getDataSource().isEnabledIDM()) {
            final Cwp cwp = getDataSource().getCwp();
            if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
                if (!(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                    getDataSource().setCwp(null);
                }
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
                if (!(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                    getDataSource().setCwp(null);
                }
            } else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
                    && getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
                    && !(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                            || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                getDataSource().setCwp(null);
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

//	public String getHideLocalUserGroup() {
//		if (isFullMode() && getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
//			return "";
//		}
//		return "none";
//	}

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
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return "";
		}else{
			return "none";
		}
	}
	
	public String getHidePskSelfRegAdv(){
		if(getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && getDataSource().isEnablePpskSelfReg()){
			return "";
		}else{
			return "none";
		}
	}
	
	public String getHide80211w() {
		if ((getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK)
				&& getDataSource().getEncryption() != SsidProfile.KEY_ENC_TKIP 
				&& getDataSource().getAccessMode() != SsidProfile.ACCESS_MODE_PSK){
			return "";
		} else {
			return "none";
		}
	}
	
	public String getHideWpa2mfpType(){
		if(enable80211w){
			return "";
		}else{
			return "none";
		}
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

	
	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}

	OptionsTransfer macFilterOptions;

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

	private String checkPskUserSize() {
		long count = getPskUserCount(getDataSource());
		if (count > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
			return getText("error.template.morePskUsers");
		}
		return "";
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
				.getId(), new SsidProfilesFullAction());
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
			if (getDataSource().getRadiusUserGroups() != null) {
				getDataSource().getRadiusUserGroups().size();
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
			if (configTemplate.getMgmtServiceOption() != null)
				configTemplate.getMgmtServiceOption().getId();
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
					
					if (null != tmpTemplate.getSsidProfile().getRadiusUserGroups()) {
						tmpTemplate.getSsidProfile().getRadiusUserGroups().size();
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
        
        if(bo instanceof OsObject){
        	OsObject osObject=(OsObject)bo;
        	if(null != osObject){
        		osObject.getId();
        	}
        }
		return null;
	}
	

	public boolean checkAllCloseUpdate(ConfigTemplate modifyConfigTemplate) throws Exception {
		if (!checkExistSsid(modifyConfigTemplate)) {
			return false;
		}
		if (!checkRadioModeSize(modifyConfigTemplate)) {
			return false;
		}
//		if (!checkIpPolicyAndMacPolicySize(modifyConfigTemplate)) {
//			return false;
//		}
//		if (!checkUserProfileAttribute(modifyConfigTemplate)) {
//			return false;
//		}
//		if (!checkCacAirTime(modifyConfigTemplate)) {
//			return false;
//		}
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

	public Set<CheckItem> getReferenceWLAN() {
		return referenceWLAN;
	}

	public Long getEditMacFilterId() {
		return editMacFilterId;
	}

	public void setEditMacFilterId(Long editMacFilterId) {
		this.editMacFilterId = editMacFilterId;
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

	public boolean isBlnJsonMode() {
		return blnJsonMode;
	}

	public void setBlnJsonMode(boolean blnJsonMode) {
		setJsonMode(blnJsonMode);
		this.blnJsonMode = blnJsonMode;
	}

	public List<Long> getLocalUserGroupIds() {
		return localUserGroupIds;
	}

	public void setLocalUserGroupIds(List<Long> localUserGroupIds) {
		this.localUserGroupIds = localUserGroupIds;
	}
	
	public String getUserGroupTarget() {
		return this.userGroupTarget;
	}
	
	public void setUserGroupTarget(String userGroupTarget) {
		this.userGroupTarget = userGroupTarget;
	}
	

		
	public String getFullModeConfigStyle() {
		if (isFullMode()) {
			return "";
		} else {
			return "none";
		}
	}

	public boolean isEnabledPpskSelf() {
		return enabledPpskSelf;
	}

	public void setEnabledPpskSelf(boolean enabledPpskSelf) {
		this.enabledPpskSelf = enabledPpskSelf;
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

	// support 11ac MCS
    private boolean[] streamEnable;
    private short[] mcsValue;
    
    public void init11acRateSets(){
		if(getDataSource().getAcRateSets() == null || getDataSource().getAcRateSets().isEmpty()){
			List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
			for (short i = Tx11acRateSettings.STREAM_TYPE_SINGLE; i <= Tx11acRateSettings.STREAM_TYPE_THREE; i ++){
				Tx11acRateSettings acRateSet = new Tx11acRateSettings();
				acRateSet.setStreamType(i);
				acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
				acRateList.add(acRateSet);
			}
			getDataSource().setAcRateSets(acRateList);
		}else{
			List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
			for(Tx11acRateSettings settings : getDataSource().getAcRateSets()){
				Tx11acRateSettings acRateSettings = new Tx11acRateSettings();
				acRateSettings.setStreamEnable(settings.isStreamEnable());
				acRateSettings.setMcsValue(settings.getMcsValue());
				acRateSettings.setStreamType(settings.getStreamType());
				acRateList.add(acRateSettings);
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
	
	/*
	 * Block was added for single ssid
	 * No check for ppskOpenSsid when SingleSsid is enabled;
	 */
	public boolean checkPpskOpenSsid(String operation, String name, String value, boolean tag){
		if(tag){
			return false;
		}else{
			if(("create" + getLstForward()).equals(operation) ||
					"create".equals(operation)){
				return checkNameExists(name, value);
			}
			if(("update" + getLstForward()).equals(operation) ||
					"update".equals(operation)){
				return checkNameExistsWhileUpdate(getDataSource().getId(),name, value);
			}
			return false;
		}
		
	}
	private boolean enabled80211k;
	private boolean enabled80211v;
	private boolean enabled80211r;
	private boolean enabledwmm;
	private boolean enabledAcVoice;

	public boolean isEnabled80211k() {
		return enabled80211k;
	}

	public void setEnabled80211k(boolean enabled80211k) {
		this.enabled80211k = enabled80211k;
	}

	public boolean isEnabled80211v() {
		return enabled80211v;
	}

	public void setEnabled80211v(boolean enabled80211v) {
		this.enabled80211v = enabled80211v;
	}

	public boolean isEnabled80211r() {
		return enabled80211r;
	}

	public void setEnabled80211r(boolean enabled80211r) {
		this.enabled80211r = enabled80211r;
	}

	public boolean isEnabledwmm() {
		return enabledwmm;
	}

	public void setEnabledwmm(boolean enabledwmm) {
		this.enabledwmm = enabledwmm;
	}

	public boolean isEnabledAcVoice() {
		return enabledAcVoice;
	}

	public void setEnabledAcVoice(boolean enabledAcVoice) {
		this.enabledAcVoice = enabledAcVoice;
	}
	
	public boolean isDisaplySocialLogin() {
	    GuestAnalyticsInfo info;
        if (NmsUtil.isHostedHMApplication()) {
            // HMOL
            info = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.id", getDomain().getId());
        } else {
            // Stand Alone
            info = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.domainName",
                    HmDomain.HOME_DOMAIN);
        }
        if(null != info && StringUtils.isNotBlank(info.getApiKey()) && StringUtils.isNotBlank(info.getApiNonce())) {
            return true;
        } else {
            return false;
        }
	}

    private void checkEnabledGASSIDs() throws JSONException {
        try {
            List<SsidProfile> list = null;
            if (NmsUtil.isHostedHMApplication()) {
                list = QueryUtil.executeQuery(SsidProfile.class, new SortParams("ssidName"), new FilterParams(
                        "accessMode = :s1 and enabledSocialLogin = :s2",
                        new Object[] { SsidProfile.ACCESS_MODE_OPEN, true }),
                        getDomain().getId());
            } else {
                // only fetch 100 rows
                list = QueryUtil.executeQuery(SsidProfile.class, new SortParams("ssidName"), new FilterParams(
                        "accessMode = :s1 and enabledSocialLogin = :s2",
                        new Object[] { SsidProfile.ACCESS_MODE_OPEN, true }), 100);
            }
            List<String> names = new ArrayList<>();
            if(null != list) {
                for (SsidProfile ssidProfile : list) {
                    names.add(ssidProfile.getSsidName());
/*                        + (NmsUtil.isHostedHMApplication() ? "" : (ssidProfile
                                    .getOwner().getDomainName()
                                    .equals(HmDomain.HOME_DOMAIN) ? ""
                                    : " (" + ssidProfile.getOwner().getDomainName() + ")")));*/
                }
            }
            
            jsonObject.put("succ", true);
            jsonObject.put("values", "");
            if(!names.isEmpty()) {
                final String str = names.toString();
                jsonObject.put("values", str.subSequence(1, str.length()-1));
            }
        } catch (Exception e) {
            log.error("checkEnabledGASSIDs", e);
        }
    }

	public Long getNewAddGroupId() {
		return newAddGroupId;
	}

	public void setNewAddGroupId(Long newAddGroupId) {
		this.newAddGroupId = newAddGroupId;
	}
}