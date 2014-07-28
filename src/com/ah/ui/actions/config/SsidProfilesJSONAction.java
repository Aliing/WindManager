package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.userprofile.selection.SsidUserProfileSelectionImpl;
import com.ah.util.bo.userprofile.selection.UserProfileSelection;
import com.ah.util.bo.userprofile.selection.UserProfileSelectionProxy;

/**
 * used to deal with some simple ssid operations
 * 
 */
public class SsidProfilesJSONAction extends BaseAction implements QueryBo {
	private static final long serialVersionUID = 6022528210209551628L;
	
	private static final Tracer log = new Tracer(SsidProfilesJSONAction.class.getSimpleName());
	
	private static final CheckItem specialBrPpskCheckItem = new CheckItem(-1111L,
							MgrUtil.getUserMessage("config.v2.select.ssid.profile.ppsk.br"));

	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CONFIGURATION_TEMPLATE);
		setDataSource(SsidProfile.class);
	}
	
	public String execute() throws Exception {
		try {
			if ("listUserProfiles".equals(operation)) {
				if (this.getSsidId() != null && this.getSsidProfileToDeal() == null) {
					this.setSsidProfileToDeal(QueryUtil.findBoById(SsidProfile.class, this.getSsidId(), this));
				}
				if (getAddedUserProfileId() != null && getAddedUserProfileId() > 0L) {
					this.getUpSelection().setAddedUserProfile(this.getAddedUserProfileId(), this.getSsidUserProfileType());
				} else {
					this.getUpSelection().setAddedUserProfile(null, this.getSsidUserProfileType());
				}
				setEnableOsDection(getSsidProfileToDeal().isEnableOsDection());
				if (this.isEnableOsDection()) {
					this.setHideOsDectionNote("");
				}
                setChkUserOnly(getSsidProfileToDeal().getChkUserOnly());
                setDenyAction(getSsidProfileToDeal().getDenyAction());
                setActionTime(getSsidProfileToDeal().getActionTime());
                setChkDeauthenticate(getSsidProfileToDeal().getChkDeauthenticate());
                setEnableAssignUserProfile(getSsidProfileToDeal().isEnableAssignUserProfile());
                setUserProfileAttributeType(getSsidProfileToDeal().getUserProfileAttributeType());
                if(SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED == getSsidProfileToDeal().getUserProfileAttributeType()){
                	 setAssignUserProfileAttributeId(getSsidProfileToDeal().getAssignUserProfileAttributeId());
                }else{
                	setAssignUserProfileAttributeCustomerId(getSsidProfileToDeal().getAssignUserProfileAttributeId());
                }
               
                setAssignUserProfileVenderId(getSsidProfileToDeal().getAssignUserProfileVenderId());
				return "listUserProfiles";
			}
			//to save those selection and setting from user profile list dialog
			else if ("save".equals(operation)) {
				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, this.getSsidId(), this);
				ssidProfile.setEnableOsDection(isEnableOsDection());
				ssidProfile.setChkUserOnly(isChkUserOnly());
				ssidProfile.setDenyAction(getDenyAction());
				ssidProfile.setActionTime(getActionTime());
				ssidProfile.setChkDeauthenticate(isChkDeauthenticate());
				ssidProfile.setRadiusUserProfile(new HashSet<UserProfile>());
				ssidProfile.setUserProfileDefault(null);
				ssidProfile.setUserProfileSelfReg(null);
				ssidProfile.setUserProfileGuest(null);
				ssidProfile.setEnableAssignUserProfile(isEnableAssignUserProfile());
				ssidProfile.setUserProfileAttributeType(getUserProfileAttributeType());
				if (isEnableAssignUserProfile()) {
					if(userProfileAttributeType == SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED){
						ssidProfile.setAssignUserProfileAttributeId(getAssignUserProfileAttributeId());
						ssidProfile.setAssignUserProfileVenderId(0);
					}else{
						ssidProfile.setAssignUserProfileAttributeId(getAssignUserProfileAttributeCustomerId());
						ssidProfile.setAssignUserProfileVenderId(getAssignUserProfileVenderId());
					}
				} else {
					ssidProfile.setAssignUserProfileAttributeId(0);
					ssidProfile.setAssignUserProfileVenderId(0);
					ssidProfile.getRadiusUserGroups().clear();
				}
				
				if (userProfileSelectionStr != null && !"".equals(userProfileSelectionStr)) {
					String[] strs = userProfileSelectionStr.split(",");
					String defId = "";
					String regId = "";
					//for default
					for (int i = 0 ; i < strs.length; i++) {
						if (strs[i] != null && !"".equals(strs[i])) {
							String[] strs2 = strs[i].split("_");
							if (strs2 != null && strs2.length > 1) {
								if(UserProfileSelection.USER_PROFILE_TYPE_DEFAULT == new Integer(strs2[1]).shortValue()) {
									ssidProfile.setUserProfileDefault(QueryUtil.findBoById(UserProfile.class, new Long(strs2[0]), this));
									defId = strs2[0];
									break;
								} 
							}
						}
					}
					//for registration
					for (int i = 0 ; i < strs.length; i++) {
						if (strs[i] != null && !"".equals(strs[i])) {
							String[] strs2 = strs[i].split("_");
							if (strs2 != null && strs2.length > 1) {
								if(UserProfileSelection.USER_PROFILE_TYPE_REGISTRATION == new Integer(strs2[1]).shortValue()) {
									ssidProfile.setUserProfileSelfReg(QueryUtil.findBoById(UserProfile.class, new Long(strs2[0]), this));
									regId = strs2[0];
									break;
								} 
							}
						}
					}
					//for guest (AirWatch)
					for (int i = 0 ; i < strs.length; i++) {
					    if (strs[i] != null && !"".equals(strs[i])) {
					        String[] strs2 = strs[i].split("_");
					        if (strs2 != null && strs2.length > 1) {
					            if(UserProfileSelection.USER_PROFILE_TYPE_GUEST == new Integer(strs2[1]).shortValue()) {
					                ssidProfile.setUserProfileGuest(QueryUtil.findBoById(UserProfile.class, new Long(strs2[0]), this));
					                regId = strs2[0];
					                break;
					            } 
					        }
					    }
					}
					//for radius
					for (int i = 0 ; i < strs.length; i++) {
						if (strs[i] != null && !"".equals(strs[i])) {
							String[] strs2 = strs[i].split("_");
							if (strs2 != null && strs2.length > 1) {
								if (strs2[0] != null && (strs2[0].equals(regId) || strs2[0].equals(defId))) {
									continue;
								}
								if(UserProfileSelection.USER_PROFILE_TYPE_AUTH == new Integer(strs2[1]).shortValue()) {
									ssidProfile.getRadiusUserProfile().add(QueryUtil.findBoById(UserProfile.class, new Long(strs2[0]), this));
								}
							}
						}
					}
				} 
				jsonObject = new JSONObject();
				try {
					updateBoWithEvent(ssidProfile);
					jsonObject.put("resultStatus", true);
				} catch (Exception e) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", 
							MgrUtil.getUserMessage("info.simple.object.create.failed"));
				}
				return "json";
			} else if ("fetchPpskServer".equals(operation)) {
				preparePpskServerList();
				defaultPpskServerIds = new ArrayList<Long>();
				ppskNoteInfo = MgrUtil.getUserMessage("info.select.ssid.profile.ppsk.br");
				if (getSsidProfileToDeal() != null) {
					if (getSsidProfileToDeal().isBlnBrAsPpskServer()) {
						defaultPpskServerIds.add(specialBrPpskCheckItem.getId());
					}
					if (getSsidProfileToDeal().getPpskServer() != null) {
						defaultPpskServerIds.add(getSsidProfileToDeal().getPpskServer().getId());
					}
				}
				return "ssidPpskServer";
			} else if ("finishSelectPpsk4Ssid".equals(operation)) {
				jsonObject = new JSONObject();
				boolean isBrPrivatePskServerSelected = isSpecialBrPpskServerSelected();
				Long ppskApServerId = getCommonPpskServerSelected();
				if (isBrPrivatePskServerSelected == false && ppskApServerId == null) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Please select a PPSK Server.");
					return "json";
				}
				//try to save the selected ppsk serverIp
				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, getSsidId(), this);
				if (ssidProfile == null) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Please select a ssid profile first.");
					return "json";
				}
				if (isBrPrivatePskServerSelected) {
					ssidProfile.setBlnBrAsPpskServer(true);
					ssidProfile.setPpskServer(null);
				} else {
					if (ppskApServerId != null && ppskApServerId > 0) {
						HiveAp ppskServer = QueryUtil.findBoById(HiveAp.class, ppskApServerId, this);
						if (ppskServer == null) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", "PPSK Server you selected is not defined.");
							return "json";
						}
						ssidProfile.setPpskServer(ppskServer);
					} else {
						ssidProfile.setPpskServer(null);
					}
					ssidProfile.setBlnBrAsPpskServer(false);
				}
				try {
					updateBoWithEvent(ssidProfile);
					jsonObject.put("resultStatus", true);
				} catch (Exception e) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
				}
				return "json";
			}
		} catch (Exception e) {
			log.error("Ssid deal exception. ", MgrUtil.getUserMessage(e), e);
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(e));
		}
		
		return SUCCESS;
	}

	// indicate which ssid is
	private Long ssidId;

	public Long getSsidId() {
		return ssidId;
	}

	public void setSsidId(Long ssidId) {
		this.ssidId = ssidId;
		//prepare a ssid profile from database
		if (ssidId != null) {
			this.setSsidProfileToDeal(QueryUtil.findBoById(SsidProfile.class, this.getSsidId(), this));
		}
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
		return null;
	}
	
	public SsidProfile getDataSource() {
		return (SsidProfile) dataSource;
	}
	
	private String hideOsDectionNote = "none";

	public String getHideOsDectionNote() {
		return hideOsDectionNote;
	}

	public void setHideOsDectionNote(String hideOsDectionNote) {
		this.hideOsDectionNote = hideOsDectionNote;
	}
	
	public EnumItem[] getEnumDenyAction() {
		return SsidProfile.DENY_ACTION;
	}
	
	public String getRadiusUserProfieStype() {
		return this.getUpSelection().isAuthUserProfileSupport() ? "" : "none";
	}

	public String getDefaultUserProfieStype() {
		return this.getUpSelection().isDefaultUserProfileSupport() ? "" : "none";
	}

	public String getSelfRegUserProfieStype() {
		return this.getUpSelection().isSelfUserProfileSupport() ? "" : "none";
	}
	
	public String getGuestUserProfieStype() {
	    return this.getUpSelection().isGuestUserProfileSupport() ? "" : "none";
	}

	private SsidProfile ssidProfileToDeal = null;
	
	public SsidProfile getSsidProfileToDeal() {
		return ssidProfileToDeal;
	}

	public void setSsidProfileToDeal(SsidProfile ssidProfileToDeal) {
		this.ssidProfileToDeal = ssidProfileToDeal;
	}
	
	@SuppressWarnings("all")
	private UserProfileSelectionProxy upSelection;

	public UserProfileSelectionProxy getUpSelection() {
		if (upSelection == null) {
			upSelection = new UserProfileSelectionProxy(
								new SsidUserProfileSelectionImpl(this.ssidId, 
										SsidProfile.class, 
										this.getDomainId()
								)
							);
		}
		return upSelection;
	}
	
	/*
	 * used only should save info into session
	 * 
	 * private ConfigTemplate getConfigTemplateSession() {
		return (ConfigTemplate) MgrUtil.getSessionAttribute(ConfigTemplate.class.getSimpleName()
				+ "Source");
	}
	
	private void setConfigTemplateSession(ConfigTemplate dataSourceTmp) {
		MgrUtil.setSessionAttribute(ConfigTemplate.class.getSimpleName() + "Source",
				dataSourceTmp);
	}
	*/
	
	private String userProfileSelectionStr;

	public String getUserProfileSelectionStr() {
		return userProfileSelectionStr;
	}

	public void setUserProfileSelectionStr(String userProfileSelectionStr) {
		this.userProfileSelectionStr = userProfileSelectionStr;
	}
	
	private boolean enableOsDection;
	private boolean chkUserOnly;
	private short denyAction;
	private long actionTime;
	private boolean chkDeauthenticate;

	public boolean isEnableOsDection() {
		return enableOsDection;
	}

	public void setEnableOsDection(boolean enableOsDection) {
		this.enableOsDection = enableOsDection;
	}

	public boolean isChkUserOnly() {
		return chkUserOnly;
	}

	public void setChkUserOnly(boolean chkUserOnly) {
		this.chkUserOnly = chkUserOnly;
	}

	public short getDenyAction() {
		return denyAction;
	}

	public void setDenyAction(short denyAction) {
		this.denyAction = denyAction;
	}

	public long getActionTime() {
		return actionTime;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

	public boolean isChkDeauthenticate() {
		return chkDeauthenticate;
	}

	public void setChkDeauthenticate(boolean chkDeauthenticate) {
		this.chkDeauthenticate = chkDeauthenticate;
	}
	
	private Long addedUserProfileId;

	public Long getAddedUserProfileId() {
		return addedUserProfileId;
	}

	public void setAddedUserProfileId(Long addedUserProfileId) {
		this.addedUserProfileId = addedUserProfileId;
	}
	
	public String getHideActionStyle() {
		if (this.isChkUserOnly()) {
			return "";
		} else {
			return "none";
		}
	}
	
	public boolean isSaveSsidPermit() {
		//can write
		if ("".equals(this.getWriteDisabled())) {
			//not the default one
			if(this.getSsidProfileToDeal() != null) {
				if (!this.getSsidProfileToDeal().getDefaultFlag()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private short ssidUserProfileType = UserProfileSelection.USER_PROFILE_TYPE_DEFAULT;

	public short getSsidUserProfileType() {
		return ssidUserProfileType;
	}

	public void setSsidUserProfileType(short ssidUserProfileType) {
		this.ssidUserProfileType = ssidUserProfileType;
	}
	
	
	private void preparePpskServerList(){
		List<CheckItem> resItem = new ArrayList<CheckItem>();
		resItem.add(specialBrPpskCheckItem);
		String queryStr = "select id, hostName, hiveApModel, softVer from "+HiveAp.class.getSimpleName()+" bo ";
		String searchSQL = "(((deviceType = :s1 or deviceType = :s2) and hiveApModel != :s3 and hiveApModel != :s4 and dhcp = :s5) or deviceType = :s6 or deviceType = :s7)";
		Object[] filterObjs = new Object[7];
		filterObjs[0] = HiveAp.Device_TYPE_HIVEAP;
		filterObjs[1] = HiveAp.Device_TYPE_SWITCH;
		filterObjs[2] = HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA;
		filterObjs[3] = HiveAp.HIVEAP_MODEL_VPN_GATEWAY;
		filterObjs[4] = false;
		filterObjs[5] = HiveAp.Device_TYPE_BRANCH_ROUTER;
		filterObjs[6] = HiveAp.Device_TYPE_VPN_BR;
		List<?> serverList = QueryUtil.executeQuery(queryStr, new SortParams("hostName"), 
				new FilterParams(searchSQL, filterObjs), 
				this.getDomainId());
		for(Object obj : serverList){
			Object[] objs = (Object[])obj;
			short hiveApModel = (Short)objs[2];
			String softVer = (String)objs[3];
			if (HiveAp.HIVEAP_MODEL_BR200 == hiveApModel
					&& NmsUtil.compareSoftwareVersion(softVer, "5.1.1.0") < 0) {
				continue;
			}
			Long deviceId = (Long)objs[0];
			String hostName = (String)objs[1];
			resItem.add(new CheckItem(deviceId, hostName));
		}
		this.setAllPpskServerList(resItem);

		/*
		specialBrPpskServerList = new ArrayList<Long>();
		specialBrPpskServerList.add(specialBrPpskCheckItem.getId());
		*/
	}
	
	private boolean isSpecialBrPpskServerSelected() {
		if (selectedPpskServerIds == null || selectedPpskServerIds.isEmpty()) {
			return false;
		}
		for (Long itemId : selectedPpskServerIds) {
			if (itemId.equals(specialBrPpskCheckItem.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private Long getCommonPpskServerSelected() {
		if (selectedPpskServerIds == null || selectedPpskServerIds.isEmpty()) {
			return null;
		}
		for (Long itemId : selectedPpskServerIds) {
			if (!itemId.equals(specialBrPpskCheckItem.getId())) {
				return itemId;
			}
		}
		return null;
	}
	
	private List<Long> specialBrPpskServerList;
	private List<CheckItem> allPpskServerList;
	private List<Long> defaultPpskServerIds;
	private List<Long> selectedPpskServerIds;
	private String ppskNoteInfo;
	
	public String getPpskNoteInfo() {
		return ppskNoteInfo;
	}

	public List<Long> getDefaultPpskServerIds() {
		return defaultPpskServerIds;
	}

	public void setDefaultPpskServerIds(List<Long> defaultPpskServerIds) {
		this.defaultPpskServerIds = defaultPpskServerIds;
	}

	public List<Long> getSelectedPpskServerIds() {
		return selectedPpskServerIds;
	}

	public void setSelectedPpskServerIds(List<Long> selectedPpskServerIds) {
		this.selectedPpskServerIds = selectedPpskServerIds;
	}

	public List<CheckItem> getAllPpskServerList() {
		return allPpskServerList;
	}

	public void setAllPpskServerList(List<CheckItem> allPpskServerList) {
		this.allPpskServerList = allPpskServerList;
	}

	public List<Long> getSpecialBrPpskServerList() {
		return specialBrPpskServerList;
	}

	public void setSpecialBrPpskServerList(List<Long> specialBrPpskServerList) {
		this.specialBrPpskServerList = specialBrPpskServerList;
	}
	
	private boolean enableAssignUserProfile = false;
	private int assignUserProfileAttributeId = 11;
	private int assignUserProfileVenderId;
	private String assignUserProfileAttributeIdShow = "11_Filter-Id";
	private short userProfileAttributeType = SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED;
	private int assignUserProfileAttributeCustomerId;
	
	public String getShowAssignUserProfile(){
		return getDataSource().getBlnDisplayRadius() ? "":"none";
	}
	
	public String getAssignUserProfileStyle(){
		return getDataSource().getBlnDisplayRadius()
				&& getDataSource().isEnableAssignUserProfile() ? "" : "none";
	}
	
	public String getAssignUserProfileVenderIdStr(){
		return getDataSource().getAssignUserProfileVenderId() == 0 ? "": String.valueOf(getDataSource().getAssignUserProfileVenderId());
	}
	
	public String getAssignUserProfileAttributeCustomerIdStr(){
		return assignUserProfileAttributeCustomerId == 0? "": String.valueOf(assignUserProfileAttributeCustomerId);
	}

	public boolean isEnableAssignUserProfile() {
		return enableAssignUserProfile;
	}

	public void setEnableAssignUserProfile(boolean enableAssignUserProfile) {
		this.enableAssignUserProfile = enableAssignUserProfile;
	}

	public int getAssignUserProfileAttributeId() {
		
		return assignUserProfileAttributeId;
	}

	public void setAssignUserProfileAttributeId(int assignUserProfileAttributeId) {
		this.assignUserProfileAttributeId = assignUserProfileAttributeId;
	}

	public int getAssignUserProfileVenderId() {
		return assignUserProfileVenderId;
	}

	public void setAssignUserProfileVenderId(int assignUserProfileVenderId) {
		this.assignUserProfileVenderId = assignUserProfileVenderId;
	}
	
	public String getAttributeListString(){
		List<TextItem> attributeList = getUserProfileAttributeList();
		StringBuffer result = new StringBuffer();
		TextItem ti = null;
		for (int i = 0; i < attributeList.size(); i++) {
			ti = attributeList.get(i);
			result.append("{");
			result.append("\"name\":\"");
			result.append(ti.getValue());
			result.append("\",");
			result.append("\"id\":\"");
			result.append(ti.getKey());
			result.append("\"");
			result.append("},");
		}
		result.deleteCharAt(result.length() - 1); // delete the last ','
		return result.toString();
	}
	
	public List<TextItem> getUserProfileAttributeList(){
		List<TextItem> attributeList = new ArrayList<TextItem>();
		attributeList.add(new TextItem(Integer.toString(1), "1_User-Name"));
		attributeList.add(new TextItem(Integer.toString(2), "2_User-Password"));
		attributeList.add(new TextItem(Integer.toString(4), "4_NAS-IP-Address"));
		attributeList.add(new TextItem(Integer.toString(5), "5_NAS-Port"));
		attributeList.add(new TextItem(Integer.toString(6), "6_Service-Type"));
		attributeList.add(new TextItem(Integer.toString(7), "7_Framed-Protocol"));
		attributeList.add(new TextItem(Integer.toString(8), "8_Framed-IP-Address"));
		attributeList.add(new TextItem(Integer.toString(9), "9_Framed-IP-Netmask"));
		attributeList.add(new TextItem(Integer.toString(10), "10_Framed-Routing"));
		attributeList.add(new TextItem(Integer.toString(11), "11_Filter-Id"));
		attributeList.add(new TextItem(Integer.toString(12), "12_Framed-MTU"));
		attributeList.add(new TextItem(Integer.toString(13), "13_Framed-Compression"));
		attributeList.add(new TextItem(Integer.toString(14), "14_Login-IP-Host"));
		attributeList.add(new TextItem(Integer.toString(15), "15_Login-Service"));
		attributeList.add(new TextItem(Integer.toString(16), "16_Login-TCP-Port"));
		attributeList.add(new TextItem(Integer.toString(17), "17_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(18), "18_Reply-Message"));
		attributeList.add(new TextItem(Integer.toString(19), "19_Callback-Number"));
		attributeList.add(new TextItem(Integer.toString(20), "20_Callback-Id"));
		attributeList.add(new TextItem(Integer.toString(21), "21_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(22), "22_Framed-Route"));
		attributeList.add(new TextItem(Integer.toString(23), "23_Framed-IPX-Network"));
		attributeList.add(new TextItem(Integer.toString(25), "25_Class"));
		attributeList.add(new TextItem(Integer.toString(26), "26_Vendor-Specific"));
		attributeList.add(new TextItem(Integer.toString(27), "27_Session-Timeout"));
		attributeList.add(new TextItem(Integer.toString(28), "28_Idle-Timeout"));
		attributeList.add(new TextItem(Integer.toString(29), "29_Termination-Action"));
		attributeList.add(new TextItem(Integer.toString(30), "30_Called-Station-Id"));
		attributeList.add(new TextItem(Integer.toString(31), "31_Calling-Station-Id"));
		attributeList.add(new TextItem(Integer.toString(32), "32_NAS-Identifier"));
		attributeList.add(new TextItem(Integer.toString(34), "34_Login-LAT-Service"));
		attributeList.add(new TextItem(Integer.toString(35), "35_Login-LAT-Node"));
		attributeList.add(new TextItem(Integer.toString(36), "36_Login-LAT-Group"));
		attributeList.add(new TextItem(Integer.toString(37), "37_Framed-AppleTalk-Link"));
		attributeList.add(new TextItem(Integer.toString(38), "38_Framed-AppleTalk-Network"));
		attributeList.add(new TextItem(Integer.toString(39), "39_Framed-AppleTalk-Zone"));
		attributeList.add(new TextItem(Integer.toString(54), "54_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(55), "55_Event-Timestamp"));
		attributeList.add(new TextItem(Integer.toString(56), "56_Egress-VLANID"));
		attributeList.add(new TextItem(Integer.toString(57), "57_Ingress-Filters"));
		attributeList.add(new TextItem(Integer.toString(58), "58_Egress-VLAN-Name"));
		attributeList.add(new TextItem(Integer.toString(59), "59_User-Priority-Table"));
		attributeList.add(new TextItem(Integer.toString(61), "61_NAS-Port-Type"));
		attributeList.add(new TextItem(Integer.toString(62), "62_Port-Limit"));
		attributeList.add(new TextItem(Integer.toString(63), "63_Login-LAT-Port"));
		attributeList.add(new TextItem(Integer.toString(64), "64_Tunnel-Type"));
		attributeList.add(new TextItem(Integer.toString(65), "65_Tunnel-Medium-Type"));
		attributeList.add(new TextItem(Integer.toString(66), "66_Tunnel-Client-Endpoint"));
		attributeList.add(new TextItem(Integer.toString(67), "67_Tunnel-Server-Endpoint"));
		attributeList.add(new TextItem(Integer.toString(68), "68_Acct-Tunnel-Connection"));
		attributeList.add(new TextItem(Integer.toString(70), "70_ARAP-Password"));
		attributeList.add(new TextItem(Integer.toString(71), "71_ARAP-Features"));
		attributeList.add(new TextItem(Integer.toString(72), "72_ARAP-Zone-Access"));
		attributeList.add(new TextItem(Integer.toString(73), "73_ARAP-Security"));
		attributeList.add(new TextItem(Integer.toString(74), "74_ARAP-Security-Data"));
		attributeList.add(new TextItem(Integer.toString(75), "75_Password-Retry"));
		attributeList.add(new TextItem(Integer.toString(76), "76_Prompt"));
		attributeList.add(new TextItem(Integer.toString(77), "77_Connect-Infol"));
		attributeList.add(new TextItem(Integer.toString(78), "78_Configuration-Token"));
		attributeList.add(new TextItem(Integer.toString(79), "79_EAP-Message"));
		attributeList.add(new TextItem(Integer.toString(80), "80_Message-Authenticator"));
		attributeList.add(new TextItem(Integer.toString(81), "81_Tunnel-Private-Group-ID"));
		attributeList.add(new TextItem(Integer.toString(82), "82_Tunnel-Assignment-ID"));
		attributeList.add(new TextItem(Integer.toString(83), "83_Tunnel-Preference"));
		attributeList.add(new TextItem(Integer.toString(84), "84_ARAP-Challenge-Response"));
		attributeList.add(new TextItem(Integer.toString(85), "85_Acct-Interim-Interval"));
		attributeList.add(new TextItem(Integer.toString(86), "86_Acct-Tunnel-Packets-Lost"));
		attributeList.add(new TextItem(Integer.toString(87), "87_NAS-Port-Id"));
		attributeList.add(new TextItem(Integer.toString(88), "88_Framed-Pool"));
		attributeList.add(new TextItem(Integer.toString(89), "89_CUI"));
		attributeList.add(new TextItem(Integer.toString(90), "90_Tunnel-Client-Auth-ID"));
		attributeList.add(new TextItem(Integer.toString(91), "91_Tunnel-Server-Auth-ID"));
		attributeList.add(new TextItem(Integer.toString(92), "92_NAS-Filter-Rule"));
		attributeList.add(new TextItem(Integer.toString(93), "93_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(94), "94_Originating-Line-Info"));
		attributeList.add(new TextItem(Integer.toString(95), "95_NAS-IPv6-Address"));
		attributeList.add(new TextItem(Integer.toString(96), "96_Framed-Interface-Id"));
		attributeList.add(new TextItem(Integer.toString(97), "97_Framed-IPv6-Prefix"));
		attributeList.add(new TextItem(Integer.toString(98), "98_Login-IPv6-Host"));
		attributeList.add(new TextItem(Integer.toString(99), "99_Framed-IPv6-Route"));
		attributeList.add(new TextItem(Integer.toString(100), "100_Framed-IPv6-Pool"));
		attributeList.add(new TextItem(Integer.toString(101), "101_Error-Cause Attribute"));
		attributeList.add(new TextItem(Integer.toString(102), "102_EAP-Key-Name"));
		attributeList.add(new TextItem(Integer.toString(103), "103_Digest-Response"));
		attributeList.add(new TextItem(Integer.toString(104), "104_Digest-Realm"));
		attributeList.add(new TextItem(Integer.toString(105), "105_Digest-Nonce"));
		attributeList.add(new TextItem(Integer.toString(106), "106_Digest-Response-Auth"));
		attributeList.add(new TextItem(Integer.toString(107), "107_Digest-Nextnonce"));
		attributeList.add(new TextItem(Integer.toString(108), "108_Digest-Method"));
		attributeList.add(new TextItem(Integer.toString(109), "109_Digest-URI"));
		attributeList.add(new TextItem(Integer.toString(110), "110_Digest-Qop"));
		attributeList.add(new TextItem(Integer.toString(111), "111_Digest-Algorithm"));
		attributeList.add(new TextItem(Integer.toString(112), "112_Digest-Entity-Body-Hash"));
		attributeList.add(new TextItem(Integer.toString(113), "113_Digest-CNonce"));
		attributeList.add(new TextItem(Integer.toString(114), "114_Digest-Nonce-Count"));
		attributeList.add(new TextItem(Integer.toString(115), "115_Digest-Username"));
		attributeList.add(new TextItem(Integer.toString(116), "116_Digest-Opaque"));
		attributeList.add(new TextItem(Integer.toString(117), "117_Digest-Auth-Param"));
		attributeList.add(new TextItem(Integer.toString(118), "118_Digest-AKA-Auts"));
		attributeList.add(new TextItem(Integer.toString(119), "119_Digest-Domain"));
		attributeList.add(new TextItem(Integer.toString(120), "120_Digest-Stale"));
		attributeList.add(new TextItem(Integer.toString(121), "121_Digest-HA1"));
		attributeList.add(new TextItem(Integer.toString(122), "122_SIP-AOR"));
		attributeList.add(new TextItem(Integer.toString(123), "123_Delegated-IPv6-Prefix"));
		attributeList.add(new TextItem(Integer.toString(124), "124_MIP6-Feature-Vector"));
		attributeList.add(new TextItem(Integer.toString(125), "125_MIP6-Home-Link-Prefix"));
		attributeList.add(new TextItem(Integer.toString(126), "126_Operator-Name"));
		attributeList.add(new TextItem(Integer.toString(127), "127_Location-Information"));
		attributeList.add(new TextItem(Integer.toString(128), "128_Location-Data"));
		attributeList.add(new TextItem(Integer.toString(129), "129_Basic-Location-Policy-Rules"));
		attributeList.add(new TextItem(Integer.toString(130), "130_Extended-Location-Policy-Rules"));
		attributeList.add(new TextItem(Integer.toString(131), "131_Location-Capable"));
		attributeList.add(new TextItem(Integer.toString(132), "132_Requested-Location-Info"));
		attributeList.add(new TextItem(Integer.toString(133), "133_Framed-Management-Protocol"));
		attributeList.add(new TextItem(Integer.toString(134), "134_Management-Transport-Protection"));
		attributeList.add(new TextItem(Integer.toString(135), "135_Management-Policy-Id"));
		attributeList.add(new TextItem(Integer.toString(136), "136_Management-Privilege-Level"));
		attributeList.add(new TextItem(Integer.toString(137), "137_PKM-SS-Cert"));
		attributeList.add(new TextItem(Integer.toString(138), "138_PKM-CA-Cert"));
		attributeList.add(new TextItem(Integer.toString(139), "139_PKM-Config-Settings"));
		attributeList.add(new TextItem(Integer.toString(140), "140_PKM-Cryptosuite-List"));
		attributeList.add(new TextItem(Integer.toString(141), "141_PKM-SAID"));
		attributeList.add(new TextItem(Integer.toString(142), "142_PKM-SA-Descriptor"));
		attributeList.add(new TextItem(Integer.toString(143), "143_PKM-Auth-Key"));
		attributeList.add(new TextItem(Integer.toString(144), "144_DS-Lite-Tunnel-Name"));
		attributeList.add(new TextItem(Integer.toString(145), "145_Mobile-Node-Identifier"));
		attributeList.add(new TextItem(Integer.toString(146), "146_Service-Selection"));
		attributeList.add(new TextItem(Integer.toString(147), "147_PMIP6-Home-LMA-IPv6-Address"));
		attributeList.add(new TextItem(Integer.toString(148), "148_PMIP6-Visited-LMA-IPv6-Address"));
		attributeList.add(new TextItem(Integer.toString(149), "149_PMIP6-Home-LMA-IPv4-Address"));
		attributeList.add(new TextItem(Integer.toString(150), "150_PMIP6-Visited-LMA-IPv4-Address"));
		attributeList.add(new TextItem(Integer.toString(151), "151_PMIP6-Home-HN-Prefix"));
		attributeList.add(new TextItem(Integer.toString(152), "152_PMIP6-Visited-HN-Prefix"));
		attributeList.add(new TextItem(Integer.toString(153), "153_PMIP6-Home-Interface-ID"));
		attributeList.add(new TextItem(Integer.toString(154), "154_PMIP6-Visited-Interface-ID"));
		attributeList.add(new TextItem(Integer.toString(155), "155_PMIP6-Home-IPv4-HoA"));
		attributeList.add(new TextItem(Integer.toString(156), "156_PMIP6-Visited-IPv4-HoA"));
		attributeList.add(new TextItem(Integer.toString(157), "157_PMIP6-Home-DHCP4-Server-Address"));
		attributeList.add(new TextItem(Integer.toString(158), "158_PMIP6-Visited-DHCP4-Server-Address"));
		attributeList.add(new TextItem(Integer.toString(159), "159_PMIP6-Home-DHCP6-Server-Address"));
		attributeList.add(new TextItem(Integer.toString(160), "160_PMIP6-Visited-DHCP6-Server-Address"));
		attributeList.add(new TextItem(Integer.toString(161), "161_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(162), "162_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(163), "163_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(164), "164_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(165), "165_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(166), "166_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(167), "167_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(168), "168_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(169), "169_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(170), "170_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(171), "171_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(172), "172_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(173), "173_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(174), "174_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(175), "175_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(176), "176_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(177), "177_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(178), "178_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(179), "179_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(180), "180_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(181), "181_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(182), "182_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(183), "183_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(184), "184_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(185), "185_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(186), "186_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(187), "187_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(188), "188_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(189), "189_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(190), "190_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(191), "191_Unassigned"));
		attributeList.add(new TextItem(Integer.toString(192), "192_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(193), "193_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(194), "194_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(195), "195_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(196), "196_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(197), "197_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(198), "198_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(199), "199_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(200), "200_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(201), "201_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(202), "202_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(203), "203_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(204), "204_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(205), "205_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(206), "206_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(207), "207_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(208), "208_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(209), "209_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(210), "210_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(211), "211_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(212), "212_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(213), "213_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(214), "214_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(215), "215_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(216), "216_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(217), "217_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(218), "218_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(219), "219_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(220), "220_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(221), "221_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(222), "222_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(223), "223_Experimental Use"));
		attributeList.add(new TextItem(Integer.toString(224), "224_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(225), "225_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(226), "226_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(227), "227_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(228), "228_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(229), "229_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(230), "230_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(231), "231_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(232), "232_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(233), "233_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(234), "234_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(235), "235_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(236), "236_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(237), "237_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(238), "238_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(239), "239_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(240), "240_Implementation Specific"));
		attributeList.add(new TextItem(Integer.toString(241), "241_Reserved"));
		attributeList.add(new TextItem(Integer.toString(242), "242_Reserved"));
		attributeList.add(new TextItem(Integer.toString(243), "243_Reserved"));
		attributeList.add(new TextItem(Integer.toString(244), "244_Reserved"));
		attributeList.add(new TextItem(Integer.toString(245), "245_Reserved"));
		attributeList.add(new TextItem(Integer.toString(246), "246_Reserved"));
		attributeList.add(new TextItem(Integer.toString(247), "247_Reserved"));
		attributeList.add(new TextItem(Integer.toString(248), "248_Reserved"));
		attributeList.add(new TextItem(Integer.toString(249), "249_Reserved"));
		attributeList.add(new TextItem(Integer.toString(250), "250_Reserved"));
		attributeList.add(new TextItem(Integer.toString(251), "251_Reserved"));
		attributeList.add(new TextItem(Integer.toString(252), "252_Reserved"));
		attributeList.add(new TextItem(Integer.toString(253), "253_Reserved"));
		attributeList.add(new TextItem(Integer.toString(254), "254_Reserved"));
		attributeList.add(new TextItem(Integer.toString(255), "255_Reserved"));
		
		return attributeList;
	}

	public String getAssignUserProfileAttributeIdShow() {
		List<TextItem> attributeList = getUserProfileAttributeList();
		for(int i=0;i<attributeList.size();i++){
			if( Integer.parseInt(attributeList.get(i).getKey()) == assignUserProfileAttributeId){
				assignUserProfileAttributeIdShow = attributeList.get(i).getValue();
			}
		}
		return assignUserProfileAttributeIdShow;
	}

	public void setAssignUserProfileAttributeIdShow(
			String assignUserProfileAttributeIdShow) {
		this.assignUserProfileAttributeIdShow = assignUserProfileAttributeIdShow;
		List<TextItem> attributeList = getUserProfileAttributeList();
		for(int i=0;i<attributeList.size();i++){
			if(assignUserProfileAttributeIdShow.equals(attributeList.get(i).getValue())){
				assignUserProfileAttributeId = Integer.parseInt(attributeList.get(i).getKey());
			}
		}
		
	}
	
	public EnumItem[] getUserProfileAttributeSpecified() {
		return new EnumItem[] { new EnumItem(
				SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED,
				getText("config.configTemplate.assignusrprofile.onradius.type.specified")) };
	}
	
	public EnumItem[] getUserProfileAttributeCustomer() {
		return new EnumItem[] { new EnumItem(
				SsidProfile.USERPROFILE_ATTRIBUTE_CUSTOMER,
				getText("config.configTemplate.assignusrprofile.onradius.type.customer")) };
	}

	public short getUserProfileAttributeType() {
		return userProfileAttributeType;
	}

	public void setUserProfileAttributeType(short userProfileAttributeType) {
		this.userProfileAttributeType = userProfileAttributeType;
	}
	
	public String getAttributeSpecifiedStyle(){
		if(SsidProfile.USERPROFILE_ATTRIBUTE_SPECIFIED == userProfileAttributeType){
			return "";
		}
		return "none";
	}
	
	public String getAttributeCustomerStyle(){
		if(SsidProfile.USERPROFILE_ATTRIBUTE_CUSTOMER == userProfileAttributeType){
			return "";
		}
		return "none";
	}

	public int getAssignUserProfileAttributeCustomerId() {
		return assignUserProfileAttributeCustomerId;
	}

	public void setAssignUserProfileAttributeCustomerId(
			int assignUserProfileAttributeCustomerId) {
		this.assignUserProfileAttributeCustomerId = assignUserProfileAttributeCustomerId;
	}
	
	public String getOsDetectionStyle() {
		if (this.getDataSource() != null) {
			return "";
		}
		
		return "none";
	}

}
