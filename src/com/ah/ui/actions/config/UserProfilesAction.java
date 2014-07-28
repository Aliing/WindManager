package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.validator.constraints.Range;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Scheduler;
import com.ah.mdm.core.profile.impl.BindUserProfileServiceImpl;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * Modification History
 *
 * support VHM
 *     set owner to null when cloning
 *     modify function - findValues
 * joseph chen 05/07/2008
 */
public class UserProfilesAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(UserProfilesAction.class
			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_ATTRIBUTE_NUMBER = 2;
	public static final int COLUMN_DEFUALT_VLAN = 3;
	public static final int COLUMN_USER_MANAGER = 4;
	public static final int COLUMN_QOS_SETTING = 5;
	public static final int COLUMN_GRE_OR_VPN = 6;
	public static final int COLUMN_DESCRIPTION = 7;

	public static final int COLUMN_MAC_FW_FROM = 8;
	public static final int COLUMN_MAC_FW_TO = 9;
	public static final int COLUMN_IP_FW_FROM = 10;
	public static final int COLUMN_IP_FW_TO = 11;
	public static final int COLUMN_ALIAS_ATTRIBUTE = 12;
	public static final int COLUMN_GUARANTEED_TIME = 13;
	public static final int COLUMN_SHARE_TIME = 14;
	public static final int COLUMN_ENABLE_SLA = 15;
	
	private static final String[] NAMES_NOT_ALLOWED = new String[]{"a", "al", "all"}; 
	private BindUserProfileServiceImpl impl =new BindUserProfileServiceImpl();

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if ("continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.userProfile"))) {
					return getLstForward();
				}
				setTabId(0);
				setSessionDataSource(new UserProfile());
				initValues();
				storeJsonContext();
				hideCreateItem = "";
				hideNewButton = "none";
				// initDefaultValues();
				return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				prepareSaveValues();
				if (getDataSource().getUserProfileName() == null || "".equals(getDataSource().getUserProfileName())
						|| checkNameExists("userProfileName", getDataSource()
						.getUserProfileName())) {
					initValues();
					restoreJsonContext();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.objectExists", getDataSource().getUserProfileName()));
						jsonObject.put("parentId", getDealingParentId());
						jsonObject.put("whereFrom", getDealUserProfile4Who());
						jsonObject.put("upTabId", getUserProfileSubTabId());
						jsonObject.put("upType", getUpType());
                        initParam4LAN();
						return "json";
					}
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
				String nameAllowed = this.checkProfileNameAllowed(this.getDataSource().getUserProfileName(), NAMES_NOT_ALLOWED);
				if (nameAllowed != null) {
					initValues();
					restoreJsonContext();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", nameAllowed);
						jsonObject.put("parentId", getDealingParentId());
						jsonObject.put("whereFrom", getDealUserProfile4Who());
						jsonObject.put("upTabId", getUserProfileSubTabId());
						jsonObject.put("upType", getUpType());
                        initParam4LAN();
						return "json";
					}
					addActionError(nameAllowed);
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
				if (!checkRadioRateLimit() || !checkQosSettings()) {
					initValues();
					restoreJsonContext();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						jsonObject.put("parentId", getDealingParentId());
						jsonObject.put("whereFrom", getDealUserProfile4Who());
						jsonObject.put("upTabId", getUserProfileSubTabId());
						jsonObject.put("upType", getUpType());
						initParam4LAN();
						return "json";
					}
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
				setInputDirectlyVlan();
				setInputAttributeGroup();
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					Long addedId = saveUserProfile();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("addedId", addedId);
					jsonObject.put("addedName", getDataSource().getUserProfileName());
					jsonObject.put("parentId", getDealingParentId());
					jsonObject.put("whereFrom", getDealUserProfile4Who());
					jsonObject.put("upTabId", getUserProfileSubTabId());
					jsonObject.put("upType", getUpType());
					jsonObject.put("parentDomID", getParentDomID());
                    initParam4LAN();
					return "json";
				} else {
					if ("create".equals(operation)) {
						id = saveUserProfile();
						String returnValue = prepareBoList();
						getLazyInfoList();
						return returnValue;
					} else {
						id = saveUserProfile();
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setTabId(0);
				
				if (isJsonMode()) {
					editBo(this);
					initValues();
					storeJsonContext();
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
				
				if (this.getLastExConfigGuide()!=null && null != dataSource && null != dataSource.getId() && dataSource.getId().compareTo(id) == 0){
					// For the config-guided, if page redirect back to the current edit SSID get data from session  
					getSessionDataSource();
				}else{
					fw = editBo(this);
					storeJsonContext();
				}
//				if (getDataSource() != null) {
//					if (getDataSource().getId() != null) {
//						findBoById(boClass, getDataSource().getId(), this);
//					}
				initValues();
//				}
				addLstTitle(getText("config.title.userProfile.edit") + " '"
						+ getDisplayName() + "'");
				if("ssid".equals(this.getLastExConfigGuide())) {
					return  "ssidEx";
				} else {
					return fw;
				}
//				return fw;
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSaveValues();
					if (!checkRadioRateLimit() || !checkQosSettings()) {
						initValues();
						restoreJsonContext();
						if (this.isJsonMode()) {
							jsonObject = new JSONObject();
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", errMsgTmp);
							jsonObject.put("parentId", getDealingParentId());
							jsonObject.put("whereFrom", getDealUserProfile4Who());
	                        initParam4LAN();
							return "json";
						}
						if("ssid".equals(this.getLastExConfigGuide())) {
							return  "ssidEx";
						} else {
							return INPUT;
						}
					}
					setInputDirectlyVlan();
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					updateBo(dataSource);
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentId", getDealingParentId());
					jsonObject.put("whereFrom", getDealUserProfile4Who());
                    initParam4LAN();
					return "json";
				}
				if ("update".equals(operation)) {
					String returnValue = updateBo();
					getLazyInfoList();
					return returnValue;
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					setSessionDataSource(findBoById(boClass, getDataSource().getId(), this));
					return getLstForward();
				}
			} else if ("removeMacPolicyT".equals(operation)
					|| "removeMacPolicyF".equals(operation)
					|| "removeIpPolicyT".equals(operation)
					|| "removeIpPolicyF".equals(operation)
					|| "removeTunnel".equals(operation)) {
				if (dataSource != null) {
					prepareSaveValues();
				}
				try {
					if ("removeMacPolicyT".equals(operation)) {
						QueryUtil.removeBo(MacPolicy.class, macPolicyTo);
					} else if ("removeMacPolicyF".equals(operation)) {
						QueryUtil.removeBo(MacPolicy.class, macPolicyFrom);
					} else if ("removeIpPolicyT".equals(operation)) {
						QueryUtil.removeBo(IpPolicy.class, ipPolicyTo);
					} else if ("removeIpPolicyF".equals(operation)) {
						QueryUtil.removeBo(IpPolicy.class, ipPolicyFrom);
					} else if ("removeTunnel".equals(operation)) {
						QueryUtil.removeBo(TunnelSetting.class, tunnel);
					}
				} catch (Exception e) {
					addActionError(MgrUtil.getUserMessage("action.error.remove.object.fail"));
				}
				initValues();
				if("ssid".equals(this.getLastExConfigGuide())) {
					return  "ssidEx";
				} else {
					return INPUT;
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				UserProfile clone = (UserProfile) findBoById(boClass, cloneId,
						this);
				clone.setId(null);
				clone.setDefaultFlag(false);
				clone.setUserProfileName("");
				clone.setOwner(null); // joseph chen
				clone.setVersion(null); // joseph chen 06/17/2008

				setCloneFields(clone, clone);

				setSessionDataSource(clone);
//				setDataSourceTypeOfNetwork();
				storeJsonContext();
				setTabId(0);
				this.initValues();
				addLstTitle(getText("config.title.userProfile"));
				//return INPUT;
				return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
//					return this.getLastExConfigGuide() != null ? "guidedConfiguration" : ret;
				} else {
					baseOperation();
					String returnValue = prepareBoList();
					getLazyInfoList();
					return returnValue;
				}
			} else if ("newQos".equals(operation)
					|| "editQos".equals(operation)
					|| "newMarkerMap".equals(operation)
					|| "editMarkerMap".equals(operation)
					|| "newTunnel".equals(operation)
					|| "editTunnel".equals(operation)
					|| "newVlanId".equals(operation)
					|| "editVlanId".equals(operation)
					|| "newUserAttribute".equals(operation)
					|| "editUserAttribute".equals(operation)
					|| "newMacPolicyT".equals(operation)
					|| "editMacPolicyT".equals(operation)
					|| "newMacPolicyF".equals(operation)
					|| "editMacPolicyF".equals(operation)
					|| "newIpPolicyT".equals(operation)
					|| "editIpPolicyT".equals(operation)
					|| "newIpPolicyF".equals(operation)
					|| "editIpPolicyF".equals(operation)
					|| "newUserProfileSchedule".equals(operation)
					|| "editUserProfileSchedule".equals(operation)
					|| "newAsRuleGroup".equals(operation)
					|| "editAsRuleGroup".equals(operation)
					|| "newMdmProfiles".equals(operation)
					|| "editMdmProfiles".equals(operation)
					|| "newMac".equals(operation) || "newOs".equals(operation)
					|| "newDomain".equals(operation) || "editDomain".equals(operation)
					|| "editMac".equals(operation) || "editOs".equals(operation)
					|| "newNetworkObj".equals(operation) || "editNetworkObj".equals(operation)) {
				saveSetting();
				addLstForward("userProfileForward");
				addLstTabId(tabId);
				getDataSource().setCurrentOperation(operation);
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				initValues();
				hideCreateItem = "";
				hideNewButton = "none";
				setId(dataSource.getId());
				// removeLstTitle();
				// removeLstForward();
				setTabId(getLstTabId());
				// removeLstTabId();
				if (getUpdateContext()) {
					removeLstTitle();
					MgrUtil.setSessionAttribute("CURRENT_TABID", getTabId());
					removeLstTabId();
					removeLstForward();
					setUpdateContext(false);
				} else {
					setTabId(Integer.parseInt(MgrUtil.getSessionAttribute(
							"CURRENT_TABID").toString()));
				}
				if (this.getLastExConfigGuide() != null){
					saveSetting();
					 return "guidedConfiguration";
				} else {
					if (isJsonMode()) {
						prepareSaveValues();
					} else {
						// set MarKerMap in order to init qosMarkTypeMode disabled or not
						if (markerMapId != null && markerMapId != -1) {
							getDataSource().setMarkerMap(
								QueryUtil.findBoById(QosMarking.class, markerMapId, this));
						} else {
							getDataSource().setMarkerMap(null);
						}
					}
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
			} else if ("addPolicyRules".equals(operation)) {
				if (dataSource == null) {
					String rtnStr = prepareBoList();
					return getCertainReturnPath(rtnStr, "userProfilesV2Json", "userProfileJsonDlg");
				} else {
					saveSetting();
					setInputDirectlyVlan();
					setInputAttributeGroup();
					//updateRules();
					addSelectedRules();
					initValues();
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
			} else if ("removePolicyRules".equals(operation)
					|| "removePolicyRulesNone".equals(operation)) {
				hideCreateItem = "removePolicyRulesNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removePolicyRulesNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					String rtnStr = prepareBoList();
					return getCertainReturnPath(rtnStr, "userProfilesV2Json", "userProfileJsonDlg");
				} else {
					saveSetting();
					setInputDirectlyVlan();
					setInputAttributeGroup();
					Collection<DevicePolicyRule> removeList = findRulesToRemove();
					//updateRules();
					getDataSource().getAssignRules().removeAll(removeList);
					initValues();
					return getCertainReturnPath(INPUT, "userProfilesV2Json", "userProfileJsonDlg");
				}
			} else {
				baseOperation();
				String returnValue = prepareBoList();
				getLazyInfoList();
				return returnValue;
			}
		} catch (Exception e) {
			String returnValue = prepareActionError(e);
			getLazyInfoList();
			return returnValue;
		}
	}

    private void initParam4LAN() throws JSONException {
        if (getDealUserProfile4Who() == DEAL_USERPROFILE_FOR_LAN_JSON) {
            if(null != getDealingParentId()) {
                List<?> result = QueryUtil.executeQuery("select portType, product from "
                        + PortAccessProfile.class.getSimpleName(), null,
                        new FilterParams("id", getDealingParentId()),
                        getDomainId(), 1);
                if(!result.isEmpty()) {
                    final Object[] object = (Object[]) result.get(0);
                    if(null != object[0] 
                            && Short.parseShort(object[0].toString()) == PortAccessProfile.PORT_TYPE_PHONEDATA) {
                        jsonObject.put("configPhoneData", true);
                    }
                    if(null != object[1]
                            && Short.parseShort(object[1].toString()) != PortAccessProfile.CHESAPEAKE) {
                        jsonObject.put("support4LAN", true);
                    }
                }
            }
        }
    }

    private void setCloneFields(UserProfile source, UserProfile destination) {
		// set schedulers
		Set<Scheduler> schedulers = new HashSet<Scheduler>();

		for (Scheduler scheduler : source.getUserProfileSchedulers()) {
			schedulers.add(scheduler);
		}
		destination.setUserProfileSchedulers(schedulers);
		
		// used to clone policy rules assignment
		if (source.isEnableAssign() 
				&& source.getAssignRules() != null 
				&& source.getAssignRules().size() > 0) {
			List<DevicePolicyRule> assignedRules = new ArrayList<DevicePolicyRule>();
			for (DevicePolicyRule dpRule : source.getAssignRules()) {
				assignedRules.add(dpRule);				
			}
			destination.setAssignRules(assignedRules);
		}
	}

	// /**
	// * Check the QoS setting's rate limit if the user profile is used by WLAN
	// * Policy
	// *
	// * @return boolean
	// */
	// public boolean isUsing() {
	// boolean bln = false;
	// if (getDataSource() != null) {
	//
	// // get the selected QoS setting
	// QosRateControl qosRate = QueryUtil.findBoById(
	// QosRateControl.class, qos);
	// if (qosRate == null)
	// return bln;
	// int rate = qosRate.getRateLimit();
	// int rate11n = qosRate.getRateLimit11n();
	// String query =
	// "select policingRate, policingRate11n from config_template_qos where user_profile_id="
	// + getDataSource().getId();
	// List<?> list = QueryUtil.executeNativeQuery(query);
	// if (!list.isEmpty()) {
	// for (int j = 0; j < list.size(); j++) {
	// Object[] value = (Object[]) list.get(j);
	//
	// // check the rate limit
	// if (Integer.parseInt(value[0].toString()) < rate
	// || Integer.parseInt(value[1].toString()) < rate11n) {
	// bln = true;
	// break;
	// }
	// }
	// }
	// }
	//
	// return bln;
	// }

	public void getLazyInfoList() throws Exception {
		List<?> lst = getPage();
		List<UserProfile> lstValue = new ArrayList<UserProfile>();
		if (lst != null && lst.size() > 0) {
//			String query = "select bo.qosRateControl.id,bo.userProfileAttribute.id,bo.vlan.id,"
//					+ "bo.macPolicyFrom.id,bo.macPolicyTo.id,bo.ipPolicyFrom.id,bo.ipPolicyTo.id,bo.networkObj.id "
//					+ "from " + UserProfile.class.getSimpleName() + " bo";
			String query = "select bo.qosRateControl.id,bo.userProfileAttribute.id,bo.vlan.id,"
					+ "bo.macPolicyFrom.id,bo.macPolicyTo.id,bo.ipPolicyFrom.id,bo.ipPolicyTo.id "
					+ "from " + UserProfile.class.getSimpleName() + " bo";
			for (Object obj : lst) {
				UserProfile profile = (UserProfile) obj;
				List<?> lst_obj = QueryUtil.executeQuery(query, null,
						new FilterParams("id", profile.getId()));
				if (!lst_obj.isEmpty()) {
					Long id;
					Object[] ls = (Object[]) lst_obj.get(0);
					if (ls[0] != null) {
						id = Long.parseLong(ls[0].toString());
						QosRateControl qosRate = QueryUtil
								.findBoById(QosRateControl.class, id);
						if (qosRate != null)
							profile.setQosRateControl(qosRate);
					}
					if (ls[1] != null) {
						id = Long.parseLong(ls[1].toString());
						UserProfileAttribute attributeGroup = QueryUtil
								.findBoById(UserProfileAttribute.class, id);
						if (attributeGroup != null)
							profile.setUserProfileAttribute(attributeGroup);
					}
					if (ls[2] != null) {
						id = Long.parseLong(ls[2].toString());
						Vlan vlan = QueryUtil.findBoById(Vlan.class, id);
						if (vlan != null)
							profile.setVlan(vlan);
					}
					if (ls[3] != null) {
						id = Long.parseLong(ls[3].toString());
						MacPolicy macPolicyFrom = QueryUtil
								.findBoById(MacPolicy.class, id);
						if (macPolicyFrom != null)
							profile.setMacPolicyFrom(macPolicyFrom);
					}
					if (ls[4] != null) {
						id = Long.parseLong(ls[4].toString());
						MacPolicy macPolicyTo = QueryUtil
								.findBoById(MacPolicy.class, id);
						if (macPolicyTo != null)
							profile.setMacPolicyTo(macPolicyTo);
					}
					if (ls[5] != null) {
						id = Long.parseLong(ls[5].toString());
						IpPolicy ipPolicyFrom = QueryUtil
								.findBoById(IpPolicy.class, id);
						if (ipPolicyFrom != null)
							profile.setIpPolicyFrom(ipPolicyFrom);
					}
					if (ls[6] != null) {
						id = Long.parseLong(ls[6].toString());
						IpPolicy ipPolicyTo = QueryUtil.findBoById(
								IpPolicy.class, id);
						if (ipPolicyTo != null)
							profile.setIpPolicyTo(ipPolicyTo);
					}
//					if (ls[7] != null) {
//						id = Long.parseLong(ls[7].toString());
//						VpnNetwork netObj = QueryUtil.findBoById(
//							VpnNetwork.class, id, this);
//						if (netObj != null)
//							profile.setNetworkObj(netObj);
//					}
				}
				lstValue.add(profile);
			}
			super.page = lstValue;
		}
	}
	public List<CheckItem> getMdmProfilesList() {
		return mdmProfilesList;
	}
	public void setMdmProfilesList(List<CheckItem> mdmProfilesList) {
		this.mdmProfilesList = mdmProfilesList;
	}
	public Long getMdmProfiles() {
		return mdmProfiles;
	}
	public void setMdmProfiles(Long mdmProfiles) {
		this.mdmProfiles = mdmProfiles;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		if (isEasyMode()) {
			setSelectedL2Feature(L2_FEATURE_SSID_PROFILES);
		} else {
			setSelectedL2Feature(L2_FEATURE_USER_PROFILE);
		}
		setDataSource(UserProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_USER_PROFILE;
	}

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
			code = "config.userprofile.name";
			break;
		case COLUMN_ATTRIBUTE_NUMBER:
			code = "config.userprofile.attribute";
			break;
		case COLUMN_DEFUALT_VLAN:
			code = "config.userprofile.vlan";
			break;
		case COLUMN_USER_MANAGER:
			code = "config.userprofile.userManager.title";
			break;
		case COLUMN_QOS_SETTING:
			code = "config.userprofile.qoss";
			break;
		case COLUMN_GRE_OR_VPN:
			code = "config.userprofile.tunnels";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.userprofile.description";
			break;
		case COLUMN_MAC_FW_FROM:
			code = "config.userprofile.mac.from.policy";
			break;
		case COLUMN_MAC_FW_TO:
			code = "config.userprofile.mac.to.policy";
			break;
		case COLUMN_IP_FW_FROM:
			code = "config.userprofile.ip.from.policy";
			break;
		case COLUMN_IP_FW_TO:
			code = "config.userprofile.ip.to.policy";
			break;
		case COLUMN_ALIAS_ATTRIBUTE:
			code = "config.userprofile.attributes";
			break;
		case COLUMN_GUARANTEED_TIME:
			code = "config.userprofile.guarantedAirTime";
			break;
		case COLUMN_SHARE_TIME:
			code = "config.userprofile.enableShareTime";
			break;
		case COLUMN_ENABLE_SLA:
			code = "config.userprofile.sla.enable";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(15);
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ATTRIBUTE_NUMBER));
		columns.add(new HmTableColumn(COLUMN_DEFUALT_VLAN));
		columns.add(new HmTableColumn(COLUMN_USER_MANAGER));
		columns.add(new HmTableColumn(COLUMN_QOS_SETTING));
		columns.add(new HmTableColumn(COLUMN_GRE_OR_VPN));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		columns.add(new HmTableColumn(COLUMN_MAC_FW_FROM));
		columns.add(new HmTableColumn(COLUMN_MAC_FW_TO));
		columns.add(new HmTableColumn(COLUMN_IP_FW_FROM));
		columns.add(new HmTableColumn(COLUMN_IP_FW_TO));
		columns.add(new HmTableColumn(COLUMN_ALIAS_ATTRIBUTE));
		columns.add(new HmTableColumn(COLUMN_GUARANTEED_TIME));
		columns.add(new HmTableColumn(COLUMN_SHARE_TIME));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SLA));

		return columns;
	}

	/**
	 * get default selected columns
	 */
	@Override
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(7);
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ATTRIBUTE_NUMBER));
		columns.add(new HmTableColumn(COLUMN_DEFUALT_VLAN));
		columns.add(new HmTableColumn(COLUMN_USER_MANAGER));
		columns.add(new HmTableColumn(COLUMN_QOS_SETTING));
		columns.add(new HmTableColumn(COLUMN_GRE_OR_VPN));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		return columns;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof UserProfile) {
			dataSource = bo;
			// Just calling the get method will fetch the LAZY attributes
			// bo.getUserProfileAttribute();
			// Call additional LAZY methods
			if (getDataSource().getUserProfileAttribute() != null)
				getDataSource().getUserProfileAttribute().getId();
			if (getDataSource().getIpPolicyFrom() != null)
				getDataSource().getIpPolicyFrom().getId();
			if (getDataSource().getIpPolicyTo() != null)
				getDataSource().getIpPolicyTo().getId();
			if (getDataSource().getMacPolicyFrom() != null)
				getDataSource().getMacPolicyFrom().getId();
			if (getDataSource().getMacPolicyTo() != null)
				getDataSource().getMacPolicyTo().getId();
			if (getDataSource().getQosRateControl() != null) {
				getDataSource().getQosRateControl().getId();
				
				if(getDataSource().getQosRateControl().getQosRateLimit() != null) {
					getDataSource().getQosRateControl().getQosRateLimit().size();
				}
			}
			if (getDataSource().getMarkerMap() != null) 
				getDataSource().getMarkerMap().getId();
			if (getDataSource().getTunnelSetting() != null)
				getDataSource().getTunnelSetting().getId();
//			if (getDataSource().getNetworkObj() != null)
//				getDataSource().getNetworkObj().getId();
			if (getDataSource().getVlan() != null)
				getDataSource().getVlan().getId();
			if (getDataSource().getAsRuleGroup() != null) {
				getDataSource().getAsRuleGroup().getId();
			}
	
			if (getDataSource().getUserProfileSchedulers()!=null) {
				getDataSource().getUserProfileSchedulers().size();
			}
			if (getDataSource().getAssignRules()!=null) {
				getDataSource().getAssignRules().size();
				for (int i = 0; i < getDataSource().getAssignRules().size(); i++) {
					if(null != getDataSource().getAssignRules().get(i).getOsObj()){
						getDataSource().getAssignRules().get(i).getOsObj().getId();
						if(null != getDataSource().getAssignRules().get(i).getOsObj().getDhcpItems()){
							getDataSource().getAssignRules().get(i).getOsObj().getDhcpItems().size();
						}
					}
				}
			}
		}
		if(bo instanceof QosRateControl) {
			QosRateControl qosRate = (QosRateControl)bo;
			
			if(qosRate.getQosRateLimit() != null) {
				qosRate.getQosRateLimit().size();
			}
		}
//		if(bo instanceof VpnNetwork) {
//			VpnNetwork netObj = (VpnNetwork)bo;
//			
//			if (netObj.getVlan() != null)
//				netObj.getVlan().getId();
//		}
		
		if(bo instanceof OsObject){
			OsObject osObject = (OsObject)bo;
			if(osObject != null && osObject.getDhcpItems() != null){
				osObject.getDhcpItems().size();
			}
		}
		
		if(bo instanceof HMServicesSettings){
			HMServicesSettings settings = (HMServicesSettings)bo;
			if(settings != null){
				if(settings.getOpenDNSAccount() != null){
					settings.getOpenDNSAccount().getId();
				}
			}
		}
		
		return null;
	}

	public void prepareSaveValues() throws Exception {
		if (getDataSource() == null)
			return;
		getLazyInfo();
		if (macAction != null && !macAction.equals("None")) {
			getDataSource().setActionMac(
					Short.parseShort(macAction.equals("Permit") ? "1" : "2"));
		} else {
			getDataSource().setActionMac((short) -1);
		}
		if (ipAction != null && !ipAction.equals("None")) {
			getDataSource().setActionIp(
					Short.parseShort(ipAction.equals("Permit") ? "1" : "2"));
		} else {
			getDataSource().setActionIp((short) -1);
		}

		// if(!getDataSource().getEnableCallAdmissionControl()) {
		// getDataSource().setGuarantedAirTime(UserProfile.DEFAULT_GUARANTEED_AIR_TIME);
		// }

		setSelectedSchedulers();
		setSelectedTunnelType();
		/*
		 * reassign user profile
		 */
		if (getDataSource().isEnableAssign()) {
			updateRules();
		} else {
			getDataSource().setAssignRules(null);
		}
	}

	public boolean checkRadioRateLimit() {
		if (getDataSource().getPolicingRate() < getDataSource()
				.getQosRateControl().getRateLimit()) {
			String tempStr[] = {
					String.valueOf(getDataSource().getPolicingRate()),
					getText("config.userprofile.abgMode") };
			errMsgTmp = getText("error.userprofile.policingRate", tempStr);
			addActionError(errMsgTmp);
			return false;
		}
		if (getDataSource().getPolicingRate11n() < getDataSource()
				.getQosRateControl().getRateLimit11n()) {
			String tempStr[] = {
					String.valueOf(getDataSource().getPolicingRate11n()),
					getText("config.userprofile.nMode") };
			errMsgTmp = getText("error.userprofile.policingRate", tempStr);
			addActionError(errMsgTmp);
			return false;
		}
		
		if (getDataSource().getPolicingRate11ac() < getDataSource()
				.getQosRateControl().getRateLimit11ac()) {
			String tempStr[] = {
					String.valueOf(getDataSource().getPolicingRate11ac()),
					getText("config.userprofile.acMode") };
			errMsgTmp = getText("error.userprofile.policingRate", tempStr);
			addActionError(errMsgTmp);
			return false;
		}
		return true;
	}

	private boolean checkQosSettings(){
		if(getDataSource().getMarkerMap() != null){
			String prtclP = getDataSource().getMarkerMap().getPrtclP();
			String prtclD = getDataSource().getMarkerMap().getPrtclD();
			boolean blnP = prtclP != null && !prtclP.isEmpty();
			boolean blnD = prtclD != null && !prtclD.isEmpty();
			
			short qosMarkTypeMode = getDataSource().getQosMarkTypeMode();
			boolean blnDSCP = qosMarkTypeMode==UserProfile.QOS_MARK_TYPE_MODE_DSCP;
			boolean bln8021p = qosMarkTypeMode==UserProfile.QOS_MARK_TYPE_MODE_8021P;
			
			if((blnDSCP && !blnD) || (bln8021p && !blnP)){
				errMsgTmp = getText("error.userprofile.qosType");
				addActionError(errMsgTmp);
				return false;
			}
		}
		
		return true;
	}
	
	public void getLazyInfo() throws Exception {
		if (userAttribute != null && userAttribute != -1) {
			getDataSource().setUserProfileAttribute(
					QueryUtil.findBoById(UserProfileAttribute.class, userAttribute));
		} else {
			getDataSource().setUserProfileAttribute(null);
		}

		if (qos != null && qos != -1) {
			getDataSource().setQosRateControl(
				QueryUtil.findBoById(QosRateControl.class, qos, this));
		} else {
			getDataSource().setQosRateControl(null);
		}
		
		if (markerMapId != null && markerMapId != -1) {
			getDataSource().setMarkerMap(
				QueryUtil.findBoById(QosMarking.class, markerMapId, this));
		} else {
			getDataSource().setMarkerMap(null);
		}
		
		if (vlanId != null && vlanId != -1) {
			getDataSource().setVlan(QueryUtil.findBoById(Vlan.class, vlanId));
		} else {
			getDataSource().setVlan(null);
		}
//		
//		if (isNetworkObjectExist() && isVlanObjectExist()) {
//			//judge whether network or vlan is selected
//			if (getDataSource().getTypeOfNetwork() == UserProfile.NETWORK_ROUTING_ENABLE) {
//				if (null != networkObjId && networkObjId > -1) {
//					getDataSource().setNetworkObj(QueryUtil.findBoById(VpnNetwork.class, networkObjId));
//				} else {
//					getDataSource().setNetworkObj(null);
//				}
//				getDataSource().setVlan(null);
//			} else if (getDataSource().getTypeOfNetwork() == UserProfile.NETWORK_WIRELESS_ONLY) {
//				if (vlanId != null && vlanId != -1) {
//					getDataSource().setVlan(QueryUtil.findBoById(Vlan.class, vlanId));
//				} else {
//					getDataSource().setVlan(null);
//				}
//				getDataSource().setNetworkObj(null);
//			}
//		} else if (isNetworkObjectExist()) {
//			//only network is there
//			if (null != networkObjId && networkObjId > -1) {
//				getDataSource().setNetworkObj(QueryUtil.findBoById(VpnNetwork.class, networkObjId));
//			} else {
//				getDataSource().setNetworkObj(null);
//			}
//			getDataSource().setVlan(null);
//		} else {
//			//only vlan is there
//			if (vlanId != null && vlanId != -1) {
//				getDataSource().setVlan(QueryUtil.findBoById(Vlan.class, vlanId));
//			} else {
//				getDataSource().setVlan(null);
//			}
//			getDataSource().setNetworkObj(null);
//		}
		
		/*if (isWirelessRoutingEnable()) {
			if (null != networkObjId && networkObjId > -1) {
				getDataSource().setNetworkObj(QueryUtil.findBoById(VpnNetwork.class, networkObjId));
			} else
				getDataSource().setNetworkObj(null);
		}

		if (null == getDataSource().getNetworkObj()) {
			if (vlanId != null && vlanId != -1) {
				getDataSource().setVlan(QueryUtil.findBoById(Vlan.class, vlanId));
			} else
				getDataSource().setVlan(null);
		} else {
			getDataSource().setVlan(null);
		}*/
		
		if (macPolicyTo != null && macPolicyTo != -1) {
			getDataSource().setMacPolicyTo(
				QueryUtil.findBoById(MacPolicy.class, macPolicyTo));
		} else
			getDataSource().setMacPolicyTo(null);

		if (macPolicyFrom != null && macPolicyFrom != -1) {
			getDataSource().setMacPolicyFrom(
				QueryUtil.findBoById(MacPolicy.class, macPolicyFrom));
		} else
			getDataSource().setMacPolicyFrom(null);

		if (ipPolicyTo != null && ipPolicyTo != -1) {
			getDataSource().setIpPolicyTo(
				QueryUtil.findBoById(IpPolicy.class, ipPolicyTo));
		} else
			getDataSource().setIpPolicyTo(null);

		if (ipPolicyFrom != null && ipPolicyFrom != -1) {
			getDataSource().setIpPolicyFrom(
				QueryUtil.findBoById(IpPolicy.class, ipPolicyFrom));
		} else
			getDataSource().setIpPolicyFrom(null);

		if (tunnel != null && tunnel != -1) {
			getDataSource().setTunnelSetting(
				QueryUtil.findBoById(TunnelSetting.class, tunnel));
		} else
			getDataSource().setTunnelSetting(null);

		if (asRuleGroup != null && asRuleGroup != -1) {
			getDataSource().setAsRuleGroup(
				QueryUtil.findBoById(AirScreenRuleGroup.class,
							asRuleGroup));
		} else {
			getDataSource().setAsRuleGroup(null);
		}
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		UserProfile source = QueryUtil.findBoById(UserProfile.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<UserProfile> list = QueryUtil.executeQuery(UserProfile.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (UserProfile profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			UserProfile up = source.clone();
			if (null == up) {
				continue;
			}
			setCloneFields(source, up);
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setUserProfileName(profile.getUserProfileName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			hmBos.add(up);
		}
		return hmBos;
	}

	@Override
	public UserProfile getDataSource() {
		return (UserProfile) dataSource;
	}

	public Range getNumberRange() {
		return super.getAttributeRange("attributeValue");
	}

	public Range getGuaranteedAirTimeRange() {
		return super.getAttributeRange("guarantedAirTime");
	}

	public Range getSlaBandwidthRange() {
		return super.getAttributeRange("slaBandwidth");
		
	}
	
	public boolean isEnableClientManagement(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain());
	     if(bo != null && bo.isEnableClientManagement()){
	    	 return true;
	     }
	     return false;
	}
	
	public String getClientProfileDisplayStyle(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getDomain());
	     if(bo != null && bo.isEnableClientManagement()){
	    	 return "";
	     }
		return "none";
	}

	public EnumItem[] getSlaAction() {
		return UserProfile.ENUM_SLA_ACTION;
	}

	// public EnumItem[] getTempEmployee() {
	// return MgrUtil.enumItems("enum.user.profile.template.", new int[] {
	// UserProfile.USER_CATEGORY_EMPLOOYEE });
	// }
	//	
	// public EnumItem[] getTempGuest() {
	// return MgrUtil.enumItems("enum.user.profile.template.", new int[] {
	// UserProfile.USER_CATEGORY_GUEST });
	// }
	//	
	// public EnumItem[] getTempVoice() {
	// return MgrUtil.enumItems("enum.user.profile.template.", new int[] {
	// UserProfile.USER_CATEGORY_VOICE });
	// }
	//	
	// public EnumItem[] getTempCustom() {
	// return MgrUtil.enumItems("enum.user.profile.template.", new int[] {
	// UserProfile.USER_CATEGORY_CUSTOM });
	// }

	public String getSlaConfigDisabled() {
		if (null != getDataSource()) {
			if (getDataSource().isSlaEnable()) {
				return "false";
			}
		}
		return "true";
	}

	public boolean getDisableGuaranteedAirTime() {
		return !getDataSource().getEnableCallAdmissionControl();
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisableNewButton() {
		return getDisableName() ? "disabled" : "";
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getDisplayName() {
		if (null != getDataSource() && null != getDataSource().getUserProfileName()) {
			return getDataSource().getUserProfileName().replace("\\", "\\\\").replace("'", "\\'");
		} else {
			return "";
		}
	}

	public int getNameLength() {
		return getAttributeLength("userProfileName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public void initValues() throws Exception {
		// init vlan object
		if (null == getDataSource().getVlan()) {
			Vlan defVlan = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
			getDataSource().setVlan(defVlan);
			inputVlanValue = defVlan.getVlanName();
		}
		initListValues();
		focusNewListItem();
		initTunnelType();
	}

	private void saveSetting() throws Exception {
		if (operation.equals("newMacPolicyT"))
			getDataSource().setPolicyType("macTo");
		if (operation.equals("newMacPolicyF"))
			getDataSource().setPolicyType("macFrom");
		if (operation.equals("newIpPolicyT"))
			getDataSource().setPolicyType("ipTo");
		if (operation.equals("newIpPolicyF"))
			getDataSource().setPolicyType("ipFrom");

		// for vlan text field
		getDataSource().setVlanInputValue(inputVlanValue);
		// for attribute text field
		getDataSource().setAttriInputValue(inputAttriValue);

		prepareSaveValues();
	}

	private void initListValues() throws Exception {
		this.userAttributeList = getBoCheckItems("attributeName",
				UserProfileAttribute.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);

		this.vlanIdList = getBoCheckItems("vlanName", Vlan.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);

		this.macPolicyFromList = getBoCheckItems("policyName", MacPolicy.class,
				null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);
		this.mdmProfilesList = getBoCheckItems("mdmProfilesName", MdmProfiles.class,
				null, BaseAction.CHECK_ITEM_BEGIN_NO,
				BaseAction.CHECK_ITEM_END_NO);

		this.macPolicyToList = macPolicyFromList;

		this.ipPolicyFromList = getBoCheckItems("policyName", IpPolicy.class,
				null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);

		this.ipPolicyToList = ipPolicyFromList;

		this.qosList = getBoCheckItems("qosName", QosRateControl.class, null,
				BaseAction.CHECK_ITEM_BEGIN_NO, BaseAction.CHECK_ITEM_END_NO);

		this.markerMapList = getBoCheckItems("qosName", QosMarking.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
		
		this.tunnelList = getBoCheckItems("tunnelName", TunnelSetting.class,
				null, BaseAction.CHECK_ITEM_BEGIN_NO,
				BaseAction.CHECK_ITEM_END_NO);

		this.asRuleGroupList = getBoCheckItems("profileName",
				AirScreenRuleGroup.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);

		this.initSchedules();
	}

	// private void initDefaultValues() {
	// if (qosList != null && qosList.size() > 1)
	// qos = qosList.get(1).getId();
	// if (vlanIdList != null && vlanIdList.size() > 1)
	// vlanId = vlanIdList.get(1).getId();
	// // if(userAttributeList!=null && userAttributeList.size()>1)
	// // userAttribute=(Long)(((CheckItem)userAttributeList.get(1)).getId());
	// }

	private void focusNewListItem() throws Exception {
		if (getDataSource() == null)
			return;

		if (macPolicyId != null && getDataSource().getPolicyType() != null) {
			if (getDataSource().getPolicyType().equals("macTo"))
				macPolicyTo = macPolicyId;
			else
				macPolicyFrom = macPolicyId;
		}

		if (ipPolicyId != null && getDataSource().getPolicyType() != null) {
			if (getDataSource().getPolicyType().equals("ipTo"))
				ipPolicyTo = ipPolicyId;
			else
				ipPolicyFrom = ipPolicyId;
		}

		if (getDataSource().getIpPolicyFrom() != null && ipPolicyFrom == null)
			ipPolicyFrom = getDataSource().getIpPolicyFrom().getId();

		if (getDataSource().getIpPolicyTo() != null && ipPolicyTo == null)
			ipPolicyTo = getDataSource().getIpPolicyTo().getId();

		if (getDataSource().getMacPolicyFrom() != null && macPolicyFrom == null)
			macPolicyFrom = getDataSource().getMacPolicyFrom().getId();

		if (getDataSource().getMacPolicyTo() != null && macPolicyTo == null)
			macPolicyTo = getDataSource().getMacPolicyTo().getId();

		if (getDataSource().getUserProfileAttribute() != null
				&& userAttribute == null)
			userAttribute = getDataSource().getUserProfileAttribute().getId();

		if (qos == null && getDataSource().getQosRateControl() != null)
			qos = getDataSource().getQosRateControl().getId();
		
		if (markerMapId == null && getDataSource().getMarkerMap() != null)
			markerMapId = getDataSource().getMarkerMap().getId();

		if (getDataSource().getVlan() != null && vlanId == null)
			vlanId = getDataSource().getVlan().getId();

		if (getDataSource().getTunnelSetting() != null && tunnel == null)
			tunnel = getDataSource().getTunnelSetting().getId();

		if (getDataSource().getAsRuleGroup() != null && asRuleGroup == null) {
			asRuleGroup = getDataSource().getAsRuleGroup().getId();
		}

		if (getDataSource().getActionIp() > 0)
			ipAction = getDataSource().getActionIp() == 1 ? "Permit" : "Deny";

		if (getDataSource().getActionMac() > 0)
			macAction = getDataSource().getActionMac() == 1 ? "Permit" : "Deny";
	}

	private void setInputDirectlyVlan() {
		if ((null == vlanId || vlanId == -1)) {
//		if ((null == vlanId || vlanId == -1) && null == getDataSource().getNetworkObj()) {
			getDataSource().setVlan(
					CreateObjectAuto.createNewVlan(inputVlanValue, getDomain(),
							"For User Profile : "
									+ getDataSource().getUserProfileName()));
		}
	}

	private void setInputAttributeGroup() {
		if ((null == userAttribute || userAttribute == -1) && !"".equals(inputAttriValue)) {
			getDataSource().setUserProfileAttribute(
					CreateObjectAuto.createNewUserAttribute(inputAttriValue,
							getDomain(), "For User Profile : "
									+ getDataSource().getUserProfileName()));
		}
	}

	// private List<CheckItem> findValues(String name, Class<?> boClass,
	// boolean isMustSelected) throws Exception {
	// List<CheckItem> valueList = new ArrayList<CheckItem>();
	// List<CheckItem> tempList = new ArrayList<CheckItem>();
	//
	// List<CheckItem> all = this.getBoCheckItems(name, boClass, null);
	//
	// if (all != null && all.size() > 0) {
	//
	// if(isMustSelected) {
	// valueList.add(new CheckItem((long) -1, ""));
	// }
	//
	// for (CheckItem item : all) {
	// int count = 0;
	// for (CheckItem tempItem : tempList) {
	// if (tempItem != null) {
	// if (tempItem.getValue().equals(item.getValue())) {
	// count++;
	// break;
	// }
	// }
	// }
	// if (count == 0) {
	// tempList.add(item);
	// valueList.add(item);
	// }
	// }
	// } else
	// valueList.add(new CheckItem((long) -1, MgrUtil
	// .getUserMessage("config.optionsTransfer.none")));
	// return valueList;
	// }
//	protected void setMdmProfiles() throws Exception {
//		MdmProfiles mdmProfiles = getDataSource().getMdmProfiles();
//
//	}
	protected void setSelectedSchedulers() throws Exception {
		Set<Scheduler> ssidSchedulers = getDataSource()
				.getUserProfileSchedulers();
		ssidSchedulers.clear();
		if (schedulers != null) {

			for (Long schedulersId : schedulers) {
				Scheduler scheduler = findBoById(Scheduler.class,
						schedulersId);
				if (scheduler != null) {
					ssidSchedulers.add(scheduler);
				}
			}

			if (ssidSchedulers.size() != schedulers.size()) {
				String tempStr[] = { getText("config.ssid.selectedSchedulers") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
			}
		}
		if (ssidSchedulers.size() > 0)
			getDataSource().setUserProfileSchedulers(ssidSchedulers);
		log.info("setSelectedSchedulers", "UserProfile "
				+ getDataSource().getUserProfileName() + " has "
				+ ssidSchedulers.size() + " Schedulers.");
	}

	public void initSchedules() throws Exception {
		List<CheckItem> availableSchedulers = getBoCheckItems("schedulerName",
				Scheduler.class, null);
		Collection<CheckItem> removeList = new Vector<CheckItem>();

		for (CheckItem obj : availableSchedulers) {
			for (Scheduler schedulerTmp : getDataSource()
					.getUserProfileSchedulers()) {
				if (schedulerTmp.getId().longValue() == obj.getId()) {
					removeList.add(obj);
				}
			}
		}

		availableSchedulers.removeAll(removeList);

		// For the OptionsTransfer component
		schedulerOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ssid.availableSchedulers"), MgrUtil
				.getUserMessage("config.ssid.selectedSchedulers"),
				availableSchedulers,
				getDataSource().getUserProfileSchedulers(), "id", "value",
				"schedulers", 8, "UserProfileSchedule");
	}

	private void initTunnelType() {
		if (null != getDataSource()) {
			if (null != getDataSource().getTunnelSetting()
					|| getDataSource().getTunnelType() == UserProfile.TUNNEL_GRE) {
				tunnelType = UserProfile.TUNNEL_GRE;
				greTunnelSettingStyle = "";
				vpnTunnelSettingStyle = "none";
				getDataSource().setTunnelTraffic(
						UserProfile.VPN_TUNNEL_TRAFFIC_ALL);
			} else {
				if (getDataSource().getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_ALL
						|| getDataSource().getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL
						|| getDataSource().getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET) {
					tunnelType = UserProfile.TUNNEL_VPN;
					greTunnelSettingStyle = "none";
					vpnTunnelSettingStyle = "";
				} else {
					tunnelType = UserProfile.TUNNEL_NO;
					greTunnelSettingStyle = "none";
					vpnTunnelSettingStyle = "none";
					getDataSource().setTunnelTraffic(
							UserProfile.VPN_TUNNEL_TRAFFIC_ALL);
				}
			}
		}
	}

	private void setSelectedTunnelType() {
		if (tunnelType == UserProfile.TUNNEL_GRE) {
			getDataSource().setTunnelTraffic((short) 0);
		} else if (tunnelType == UserProfile.TUNNEL_VPN) {
			getDataSource().setTunnelSetting(null);
		} else {
			getDataSource().setTunnelTraffic((short) 0);
			getDataSource().setTunnelSetting(null);
		}
		getDataSource().setTunnelType(tunnelType);
	}

	// public String getShowUserCategoryDiv(){
	// return "none";
	// }

	protected List<CheckItem> userAttributeList;

	protected List<CheckItem> qosList;
	
	protected List<CheckItem> markerMapList;

	protected List<CheckItem> vlanIdList;

	protected List<CheckItem> macPolicyToList;

	protected List<CheckItem> macPolicyFromList;
	protected List<CheckItem> mdmProfilesList;

	protected List<CheckItem> ipPolicyToList;

	protected List<CheckItem> ipPolicyFromList;

	protected List<CheckItem> tunnelList;

	protected List<CheckItem> asRuleGroupList;

	protected List<Long> schedulers;

	protected Long scheduler;

	protected Long userAttribute;

	private String inputAttriValue;

	protected Long qos;
	
	protected Long markerMapId;

	protected Long vlanId;

	private String inputVlanValue;

	protected Long macPolicyTo;

	protected Long macPolicyFrom;

	protected Long macPolicyId;

	protected Long ipPolicyTo;

	protected Long ipPolicyFrom;

	protected Long ipPolicyId;

	protected Long tunnel;
	
//	private Long networkObjId;
	protected Long mdmProfiles;

	protected Long asRuleGroup;

	protected String macAction;

	protected String ipAction;

	protected String macPolicyType;

	protected String ipPolicyType;

	private short tunnelType;

	private String greTunnelSettingStyle;

	private String vpnTunnelSettingStyle;

	public EnumItem[] getScheduleDenyModeList() {
		return new EnumItem[] {
				new EnumItem(UserProfile.SCHEDULE_DENY_MODE_BAN,
						getText("config.userprofile.denyaction.ban")),
				new EnumItem(UserProfile.SCHEDULE_DENY_MODE_QUARANTINE,
						getText("config.userprofile.denyaction.quarantine")) };
	}

	public EnumItem[] getTunnelOption1() {
		return new EnumItem[] { new EnumItem(UserProfile.TUNNEL_NO,
				getText("config.userprofile.tunnel.no")) };
	}

	public EnumItem[] getTunnelOption2() {
		return new EnumItem[] { new EnumItem(UserProfile.TUNNEL_GRE,
				getText("config.userprofile.tunnel.gre")) };
	}

	public EnumItem[] getTunnelOption3() {
		return new EnumItem[] { new EnumItem(UserProfile.TUNNEL_VPN,
				getText("config.userprofile.tunnel.vpn")) };
	}

	public EnumItem[] getVpnTunnelOption1() {
		return new EnumItem[] { new EnumItem(
				UserProfile.VPN_TUNNEL_TRAFFIC_ALL,
				getText("config.userprofile.tunnel.vpn.option1")) };
	}

	public EnumItem[] getVpnTunnelOption2() {
		return new EnumItem[] { new EnumItem(
				UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL,
				getText("config.userprofile.tunnel.vpn.option2")) };
	}

	public EnumItem[] getVpnTunnelOption3() {
		return new EnumItem[] { new EnumItem(
				UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET,
				getText("config.userprofile.tunnel.vpn.option3")) };
	}

	public enum DefaultAction {
		None, Permit, Deny;
		public String getKey() {
			return name();
		}

		public String getValue() {
			String value = name();
			if ("None".equals(value))
				value = "";
			return MgrUtil.getUserMessage(value);
		}
	}

	public DefaultAction[] getEnumDefaultAction() {
		return DefaultAction.values();
	}

	OptionsTransfer schedulerOptions;

	public List<CheckItem> getIpPolicyFromList() {
		return ipPolicyFromList;
	}

	public void setIpPolicyFromList(List<CheckItem> ipPolicyFromList) {
		this.ipPolicyFromList = ipPolicyFromList;
	}

	public List<CheckItem> getIpPolicyToList() {
		return ipPolicyToList;
	}

	public void setIpPolicyToList(List<CheckItem> ipPolicyToList) {
		this.ipPolicyToList = ipPolicyToList;
	}

	public List<CheckItem> getMacPolicyFromList() {
		return macPolicyFromList;
	}

	public void setMacPolicyFromList(List<CheckItem> macPolicyFromList) {
		this.macPolicyFromList = macPolicyFromList;
	}

	public Long getIpPolicyFrom() {
		return ipPolicyFrom;
	}

	public void setIpPolicyFrom(Long ipPolicyFrom) {
		this.ipPolicyFrom = ipPolicyFrom;
	}

	public Long getIpPolicyTo() {
		return ipPolicyTo;
	}

	public void setIpPolicyTo(Long ipPolicyTo) {
		this.ipPolicyTo = ipPolicyTo;
	}

	public Long getMacPolicyFrom() {
		return macPolicyFrom;
	}

	public void setMacPolicyFrom(Long macPolicyFrom) {
		this.macPolicyFrom = macPolicyFrom;
	}

	public Long getMacPolicyTo() {
		return macPolicyTo;
	}

	public void setMacPolicyTo(Long macPolicyTo) {
		this.macPolicyTo = macPolicyTo;
	}

	public List<CheckItem> getMacPolicyToList() {
		return macPolicyToList;
	}

	public void setMacPolicyToList(List<CheckItem> macPolicyToList) {
		this.macPolicyToList = macPolicyToList;
	}

	public Long getQos() {
		return qos;
	}

	public void setQos(Long qos) {
		this.qos = qos;
	}

	public List<CheckItem> getQosList() {
		return qosList;
	}

	public void setQosList(List<CheckItem> qosList) {
		this.qosList = qosList;
	}

	public List<CheckItem> getMarkerMapList() {
		return markerMapList;
	}

	public Long getMarkerMapId() {
		return markerMapId;
	}

	public void setMarkerMapList(List<CheckItem> markerMapList) {
		this.markerMapList = markerMapList;
	}

	public void setMarkerMapId(Long markerMapId) {
		this.markerMapId = markerMapId;
	}

	public OptionsTransfer getSchedulerOptions() {
		return schedulerOptions;
	}

	public void setSchedulerOptions(OptionsTransfer schedulerOptions) {
		this.schedulerOptions = schedulerOptions;
	}

	public Long getTunnel() {
		return tunnel;
	}

	public void setTunnel(Long tunnel) {
		this.tunnel = tunnel;
	}

	public Long getAsRuleGroup() {
		return asRuleGroup;
	}

	public void setAsRuleGroup(Long asRuleGroup) {
		this.asRuleGroup = asRuleGroup;
	}

	public List<CheckItem> getTunnelList() {
		return tunnelList;
	}
	
	public List<CheckItem> getAvailableNetworkList() {
        return getBoCheckItems("networkName", VpnNetwork.class, null,
                CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getAsRuleGroupList() {
		return asRuleGroupList;
	}

	public void setTunnelList(List<CheckItem> tunnelList) {
		this.tunnelList = tunnelList;
	}

	public Long getUserAttribute() {
		if (null == userAttribute && null != getDataSource().getUserProfileAttribute()) {
			userAttribute = getDataSource().getUserProfileAttribute().getId();
		}
		return userAttribute;
	}

	public void setUserAttribute(Long userAttribute) {
		this.userAttribute = userAttribute;
	}

	public List<CheckItem> getUserAttributeList() {
		return userAttributeList;
	}

	public void setUserAttributeList(List<CheckItem> userAttributeList) {
		this.userAttributeList = userAttributeList;
	}

	public Long getVlanId() {
		return vlanId;
	}

	public void setVlanId(Long vlanId) {
		this.vlanId = vlanId;
	}

	public List<CheckItem> getVlanIdList() {
		return vlanIdList;
	}

	public void setVlanIdList(List<CheckItem> vlanIdList) {
		this.vlanIdList = vlanIdList;
	}

	public String getMacAction() {
		return macAction;
	}

	public void setMacAction(String macAction) {
		this.macAction = macAction;
	}

	public String getIpAction() {
		return ipAction;
	}

	public void setIpAction(String ipAction) {
		this.ipAction = ipAction;
	}

	public List<Long> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(List<Long> schedulers) {
		this.schedulers = schedulers;
	}

	public String getIpPolicyType() {
		return ipPolicyType;
	}

	public void setIpPolicyType(String ipPolicyType) {
		this.ipPolicyType = ipPolicyType;
	}

	public String getMacPolicyType() {
		return macPolicyType;
	}

	public void setMacPolicyType(String macPolicyType) {
		this.macPolicyType = macPolicyType;
	}

	public Long getIpPolicyId() {
		return ipPolicyId;
	}

	public void setIpPolicyId(Long ipPolicyId) {
		this.ipPolicyId = ipPolicyId;
	}

	public Long getMacPolicyId() {
		return macPolicyId;
	}

	public void setMacPolicyId(Long macPolicyId) {
		this.macPolicyId = macPolicyId;
	}

	public String getAttributeTooltip() {
		return MgrUtil.getUserMessage("config.userprofile.attribute.tooltip");
	}

	public String getTunnelTooltip() {
		return MgrUtil.getUserMessage("config.userprofile.tunnel.tooltip");
	}

	public String getEnableShareTime() {
		return getDataSource().getGuarantedAirTime() == (short) 0 ? "true"
				: "false";
	}

	/**
	 * getter of scheduler
	 * 
	 * @return the scheduler
	 */
	public Long getScheduler() {
		return scheduler;
	}

	/**
	 * setter of scheduler
	 * 
	 * @param scheduler
	 *            the scheduler to set
	 */
	public void setScheduler(Long scheduler) {
		this.scheduler = scheduler;
	}

	public String getInputVlanValue() {
		if (null != vlanId) {
			for (CheckItem item : vlanIdList) {
				if (item.getId().longValue() == vlanId.longValue()) {
					inputVlanValue = item.getValue();
					break;
				}
			}
		} else {
			inputVlanValue = getDataSource().getVlanInputValue();
		}
		return inputVlanValue;
	}

	public void setInputVlanValue(String inputVlanValue) {
		this.inputVlanValue = inputVlanValue;
	}

	public short getTunnelType() {
		return tunnelType;
	}

	public void setTunnelType(short tunnelType) {
		this.tunnelType = tunnelType;
	}

	public String getGreTunnelSettingStyle() {
		return greTunnelSettingStyle;
	}

	public String getVpnTunnelSettingStyle() {
		return vpnTunnelSettingStyle;
	}

	public String getInputAttriValue() {
		if (null != userAttribute) {
			for (CheckItem item : userAttributeList) {
				if (item.getId().longValue() == userAttribute.longValue()) {
					inputAttriValue = item.getValue();
					break;
				}
			}
		} else {
			inputAttriValue = getDataSource().getAttriInputValue();
		}
		return inputAttriValue;
	}

	public void setInputAttriValue(String inputAttriValue) {
		this.inputAttriValue = inputAttriValue;
	}
	
	/*
	 * user profile reassign
	 */
	public int getGridCount() {
		return getDataSource().getAssignRules().size() == 0 ? 3 : 0;
	}
	
	public List<CheckItem> addAnyToList(List<CheckItem> list) {
		list.remove(new CheckItem((long) -1, MgrUtil.getUserMessage("config.optionsTransfer.none")));
		list.add(0, new CheckItem((long) -1, MgrUtil.getUserMessage("config.ipPolicy.any")));
		return list;
	}

	public List<CheckItem> getAvailableMacAddress() {
		return addAnyToList(getBoCheckItems("macOrOuiName", MacOrOui.class, null));
	}

	public List<CheckItem> getAvailableOsObjects() {
		return addAnyToList(getBoCheckItems("osName", OsObject.class, null));
	}
	
	public List<CheckItem> getAvailableDomainObjects() {
		return addAnyToList(getBoCheckItems("objName", DomainObject.class, 
				new FilterParams("autoGenerateFlag = :s1 and objType = :s2", new Object[]{false, DomainObject.CLASSIFICATION_POLICY})));
	}

	public List<CheckItem> getAvailableUserProfiles() {
		FilterParams params = new FilterParams("enableAssign = :s1", new Object[]{false});
		if (null != id) {
			params = new FilterParams(params.getWhere()+" AND id != :s2", new Object[]{params.getBindings()[0], id});
		}
		return getBoCheckItems("userProfileName", UserProfile.class, params);
	}
	
	protected void reorderRules() {
		if (ordering == null) {
			return;
		}

		boolean needsReordering = false;
		for (int i = 0; i < ordering.length; i++) {
			if (ordering[i] != i) {
				needsReordering = true;
			}
			if (ordering[i] < getDataSource().getAssignRules().size()) {
				getDataSource().getAssignRules().get(ordering[i]).setReorder(i);
			}
		}
		if (!needsReordering) {
			return;
		}
		log.info("reoderRules", "Needs re-ordering");
		Collections.sort(getDataSource().getAssignRules(),
				new Comparator<DevicePolicyRule>() {
					public int compare(DevicePolicyRule rule1, DevicePolicyRule rule2) {
						Integer id1 = rule1.getReorder();
						Integer id2 = rule2.getReorder();
						return id1.compareTo(id2);
					}
				});
	}

	protected void updateRules() {
		reorderRules();
		
		if (userProfileIds != null) {
			for (int i = 0; i < userProfileIds.length
					&& i < getDataSource().getAssignRules().size(); i++) {
				Long tmpUserProfileId = userProfileIds[i];
				if (tmpUserProfileId != null && tmpUserProfileId != -1) {
					UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, tmpUserProfileId);
					getDataSource().getAssignRules().get(i).setUserProfileId(tmpUserProfileId);
					getDataSource().getAssignRules().get(i).setUserProfileName(userProfile.getUserProfileName());
				}
			}
		}
	}

	private short getUnusedRuleId() {
		short ret = 0;
		// get unused number for rule id
		Map<Short, String> ruleIds = new HashMap<Short, String>();
		for (DevicePolicyRule rule : getDataSource().getAssignRules()) {
			ruleIds.put(rule.getRuleId(), null);
		}
		
		short i = 1;
		while (ret == 0) {
			if (!ruleIds.containsKey(i)) {
				ret = i;
				break;
			}
			i ++;
		}

		return ret;
	}

	protected boolean addSelectedRules() {
		if (null == getDataSource().getAssignRules()) {
			getDataSource().setAssignRules(new ArrayList<DevicePolicyRule>());
		}
		// check the item account
		if (getDataSource().getAssignRules().size()+1 > DevicePolicyRule.MAX_RULE_ID) {
			addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
			hideCreateItem = "";
			hideNewButton = "none";
			return false;
		}
		MacOrOui macObj = null;
		if (macObjId != null && macObjId != -1) {
			macObj = QueryUtil.findBoById(MacOrOui.class,macObjId);
		}
		OsObject osObj = null;
		if (osObjId != null && osObjId != -1) {
			osObj = QueryUtil.findBoById(OsObject.class,osObjId,this);
		}
		
		DomainObject domObj = null;
		if (domObjId != null && domObjId != -1) {
			domObj = QueryUtil.findBoById(DomainObject.class,domObjId);
		}
		
		UserProfile userProfile = null;
		if (userProfileId != null && userProfileId != -1) {
			userProfile = QueryUtil.findBoById(UserProfile.class, userProfileId);
		}
		
		if (getDataSource().getAssignRules() != null) {
			boolean hasSame = false;
			String thisIdentifier = DevicePolicyRule.getIdentifierString(
					macObj, osObj, domObj, userProfile.getUserProfileName(),ownershipId);
			// check duplication
			for (DevicePolicyRule rule : getDataSource().getAssignRules()) {
				if (rule.getIdentifier().equals(thisIdentifier)) {
					hasSame = true;
					break;
				}
			}
			if (hasSame) {
				hideCreateItem = "";
				hideNewButton = "none";
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
				return false;
			}
			
			//check osObject Parameter Request duplication
			if(osObj != null){
				if(checkOption55Existed(osObj)){
					hideCreateItem = "";
					hideNewButton = "none";
					addActionError(MgrUtil.getUserMessage("error.osobject.parameter.existed"));
					return false;
				}
			}
		}

		DevicePolicyRule devicePolicyRule;
		devicePolicyRule = new DevicePolicyRule();
		devicePolicyRule.setRuleId(getUnusedRuleId());
		devicePolicyRule.setMacObj(macObj);
		devicePolicyRule.setOsObj(osObj);
		devicePolicyRule.setDomObj(domObj);
		devicePolicyRule.setUserProfileId(userProfileId);
		devicePolicyRule.setUserProfileName(userProfile.getUserProfileName());
		devicePolicyRule.setOwnership(ownershipId); 

		getDataSource().getAssignRules().add(devicePolicyRule);
		macObjId = -1l;
		osObjId = -1l;
		domObjId = -1l;
		userProfileId = -1l;
		ownershipId = 0;
		return true;
	}

	private boolean checkOption55Existed(OsObject osObj){
		List<OsObjectVersion> dhcpItems = osObj.getDhcpItems();
		List<String> option55s = new ArrayList<String>();
		for (DevicePolicyRule rule : getDataSource().getAssignRules()) {
			if(rule.getOsObj() != null && rule.getOsObj().getDhcpItems() != null){
				for (OsObjectVersion objectVersion :rule.getOsObj().getDhcpItems()) {
					if(objectVersion.getOption55() != null){
						option55s.add(objectVersion.getOption55());
					}
				}
			}
		}
		if(dhcpItems != null){
			for(OsObjectVersion osObjectVersion : dhcpItems){
				if(option55s.contains(osObjectVersion.getOption55())){
					return true;
				}
			}
		}
		return false;
	}
	
	protected Collection<DevicePolicyRule> findRulesToRemove() {
		Collection<DevicePolicyRule> removeList = new Vector<DevicePolicyRule>();
		if (ruleIndices != null) {
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getAssignRules().size()) {
						removeList.add(getDataSource().getAssignRules().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
				}
			}
		}
		return removeList;
	}
	
	private Long saveUserProfile() throws Exception{
		Long upid = createBo(dataSource);
		UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, upid);
		HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", getDomainId(), this);
		if(settings != null && settings.isEnableOpenDNS()){
			OpenDNSAccount activeAccount = settings.getOpenDNSAccount();
			OpenDNSDevice defaultDevice = null;
			List<OpenDNSDevice> devices = QueryUtil.executeQuery(OpenDNSDevice.class, null, new FilterParams("openDNSAccount.id=:s1 and defaultDevice=:s2", new Object[]{activeAccount.getId(), true}));
			if(!devices.isEmpty()){
				defaultDevice = devices.get(0);
			}
			
			OpenDNSMapping mapping = new OpenDNSMapping();
			mapping.setOwner(getDomain());
			mapping.setOpenDNSAccount(activeAccount);
			mapping.setOpenDNSDevice(defaultDevice);
			mapping.setUserProfile(userProfile);
			QueryUtil.createBo(mapping);
		}
		return upid;
	}
	
	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass, FilterParams filterParams, Collection<Long> defaultIds) throws Exception {		
		int count = super.removeAllBos(boClass, filterParams, defaultIds);
		if(count > 0){
			String sql = "delete from opendns_mapping where user_profile_id in (" + NmsUtil.convertIDs2IDStr((ArrayList<Long>)defaultIds) + ")";
			QueryUtil.executeNativeUpdate(sql);
		}
		return count;
	}
	
	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids) throws Exception {	
		int count = super.removeBos(boClass, ids);
		if(count > 0){
			String sql = "delete from opendns_mapping where user_profile_id in (" + NmsUtil.convertIDs2IDStr((ArrayList<Long>)ids) + ")";
			QueryUtil.executeNativeUpdate(sql);
		}
		return count;
	}

	private Long macObjId;

	private Long osObjId;
	
	private Long domObjId;

	private Long userProfileId;

	private Long editObjId;
	private Long mdmProfilesId;
	private int ownershipId;
	public int getOwnershipId() {
		return ownershipId;
	}
	public void setOwnershipId(int ownershipId) {
		this.ownershipId = ownershipId;
	}
	public Long getMdmProfilesId() {
		return mdmProfilesId;
	}
	public void setMdmProfilesId(Long mdmProfilesId) {
		this.mdmProfilesId = mdmProfilesId;
	}

	private Long[] userProfileIds;

	private Collection<String> ruleIndices;
	
	private int[] ordering;

	public void setOrdering(int[] ordering)
	{
		this.ordering = ordering;
	}

	public Long getMacObjId()
	{
		return macObjId;
	}

	public void setMacObjId(Long macObjId)
	{
		this.macObjId = macObjId;
	}

	public Long getOsObjId()
	{
		return osObjId;
	}

	public void setOsObjId(Long osObjId)
	{
		this.osObjId = osObjId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public void setUserProfileIds(Long[] userProfileIds) {
		this.userProfileIds = userProfileIds;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public Long getEditObjId() {
		return editObjId;
	}

	public void setEditObjId(Long editObjId) {
		this.editObjId = editObjId;
	}
	
	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	public Long getDomObjId()
	{
		return domObjId;
	}

	public void setDomObjId(Long domObjId)
	{
		this.domObjId = domObjId;
	}

	public boolean getShowUpReassign() {
		if (null != id) {
			List<?> usedId = QueryUtil.executeNativeQuery("select user_profile_id from device_policy_rule where userprofileid = "+id);
			if (null != usedId && !usedId.isEmpty()) {
				return false;
			}
		}
		return true;
	}

//	public Long getNetworkObjId()
//	{
//		if (null == networkObjId) {
//			if (null != getDataSource().getNetworkObj()) {
//				networkObjId = getDataSource().getNetworkObj().getId();
//			}
//		}
//		return networkObjId;
//	}
//
//	public void setNetworkObjId(Long networkObjId)
//	{
//		this.networkObjId = networkObjId;
//	}

	/*-
	private String getReturnPath(String normalPath, String jsonModePath) {
		if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}*/
	
	private String getCertainReturnPath(String normalPath, String jsonModePath, String jsonModeDlgPath){
		if(isJsonMode() && isContentShownInDlg()) {
			return jsonModeDlgPath;
		} else if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}

	public boolean isSaveJsonModePermit() {
		if ("".equals(this.getWriteDisabled())) {
			if (this.getDataSource() == null 
					|| this.getDataSource().getId() == null 
					|| (!this.getDataSource().isDefaultFlag())){
				return true;
			}
		}
		
		return false;
	}
	
	// used to indicate whose user profile is now dealing
	private Long dealingParentId;
	private int dealUserProfile4Who;
	public static final int DEAL_USERPROFILE_FOR_SSID_JSON = 1;
	public static final int DEAL_USERPROFILE_FOR_LAN_JSON = 2;
	private String userProfileSubTabId;
	private short upType;

	public Long getDealingParentId() {
		return dealingParentId;
	}

	public void setDealingParentId(Long dealingParentId) {
		this.dealingParentId = dealingParentId;
	}

	public int getDealUserProfile4Who() {
		return dealUserProfile4Who;
	}

	public void setDealUserProfile4Who(int dealUserProfile4Who) {
		this.dealUserProfile4Who = dealUserProfile4Who;
	}

	public String getUserProfileSubTabId() {
		return userProfileSubTabId;
	}

	public void setUserProfileSubTabId(String userProfileSubTabId) {
		this.userProfileSubTabId = userProfileSubTabId;
	}

	public short getUpType() {
		return upType;
	}

	public void setUpType(short upType) {
		this.upType = upType;
	}
	
	private String schedulerListName;

	public String getSchedulerListName() {
		return schedulerListName;
	}

	public void setSchedulerListName(String schedulerListName) {
		this.schedulerListName = schedulerListName;
	}

	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private void storeJsonContext() {
		if (getDataSource() == null) {
			return;
		}
		if (isJsonMode()) {
			getDataSource().setParentDomID(getParentDomID());
			getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
			getDataSource().setContentShowType(getContentShowType());
		}
		getDataSource().setBlnForceUpControl(blnForceUpControl);
	}
	
	private void restoreJsonContext() {
		if (getDataSource() == null) {
			return;
		}
		if (isJsonMode()) {
			setParentDomID(getDataSource().getParentDomID());
			setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
			setContentShowType(getDataSource().getContentShowType());
		}
		setBlnForceUpControl(getDataSource().isBlnForceUpControl());
	}
	
	private String errMsgTmp;

	public String getErrMsgTmp() {
		return errMsgTmp;
	}

	public void setErrMsgTmp(String errMsgTmp) {
		this.errMsgTmp = errMsgTmp;
	}
	
	/**
	 * if you can judge whether network policy of this user profile is wireless only or wireless+routing,
	 * please pass argument wirelessRoutingEnable to here.
	 * 
	 * if you are dealing userprofile under certain network policy, you can pass the argument to here, then, 
	 * you may only deal with network or VLAN according to network policy.
	 */
	private Long networkPolicyId;

	public Long getNetworkPolicyId() {
		return networkPolicyId;
	}

	public void setNetworkPolicyId(Long networkPolicyId) {
		this.networkPolicyId = networkPolicyId;
	}
	
	/**
	 * alway use wirelessRoutingEnable, do not worry about anything else
	 */
	private boolean blnForceUpControl;

	public boolean isBlnForceUpControl() {
		return blnForceUpControl;
	}

	public void setBlnForceUpControl(boolean blnForceUpControl) {
		this.blnForceUpControl = blnForceUpControl;
	}
	
//	private void setDataSourceTypeOfNetwork() {
//		if (getDataSource() != null) {
//			if (getDataSource().getNetworkObj() != null) {
//				getDataSource().setTypeOfNetwork(UserProfile.NETWORK_ROUTING_ENABLE);
//			} else {
//				getDataSource().setTypeOfNetwork(UserProfile.NETWORK_WIRELESS_ONLY);
//			}
//		}
//	}
	
	public EnumItem[] getEnumOwnership(){
		return MgrUtil.enumItems(
				"enum.userprofile.ownership.type.", new int[] { DevicePolicyRule.ANY_TYPE,DevicePolicyRule.CID_TYPE,
						DevicePolicyRule.BYOD_TYPE });
	}
	public String getFirewallNote() {
		if (this.isFullMode()) {
			return MgrUtil.getUserMessage("config.userprofile.options.firewall.note");
		}
		return "";
	}
	
    public EnumItem[] getQosDscp() {
			return new EnumItem[] { new EnumItem(0,
					getText("config.port.qos.setting.classification.trusted.dscp")) };
	}
    
    public EnumItem[] getQos8021p() {
			return new EnumItem[] { new EnumItem(1,
					getText("config.port.qos.setting.classification.trusted.vlan")) };
	}
    
}
