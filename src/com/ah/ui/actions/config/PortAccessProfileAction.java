package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.hiveap.NetworkPolicyAction.NetworkPolicyType;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.userprofile.selection.PortUserProfileSelectionImpl;
import com.ah.util.bo.userprofile.selection.UserProfileSelection;
import com.ah.util.bo.userprofile.selection.UserProfileSelectionProxy;

public class PortAccessProfileAction extends IDMSupportAction implements QueryBo{
    
    private static final long serialVersionUID = 4668817211282860231L;
    private static final Tracer LOG = new Tracer(PortAccessProfileAction.class.getSimpleName());

    
    public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_PORT_TYPE = 3;
	public static final int COLUMN_DEVICE_TYPE = 4;

    protected List<Long> radiusUserGroupIds = new ArrayList<>();

    @Override
    public String execute() throws Exception {
        try {
            LOG.debug("execute, operation is "+operation);
            
            if("new".equals(operation)) {
                if (!setTitleAndCheckAccess(getText("config.title.lanProfile"))) {
                    setUpdateContext(true);
                    return getLstForward();
                }
                setSessionDataSource(new PortAccessProfile());
                
                //FIXME support IDM for AP/BR only
                if(notChesapeake()) {
                    refreshIDMStatus();
                }
                
                if(!isJsonMode() && normalView) {
                    getDataSource().setProduct(product);
                }
                prepareProductFlag();
                prepareAuthentication();
                prepareOptionalSettings();
                return getINPUTType();
            } else if("edit".equals(operation)) {
                setSessionDataSource(findBoById(boClass, id, this));
                if(normalView) {
                    prepareDeviceInfo();
                }
                
                //FIXME support IDM for AP/BR only
                if(notChesapeake() && getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
                    refreshIDMStatus();
                }
                
                prepareFlag4Page();
                prepareAuthentication();
                prepareOptionalSettings();
                if (dataSource == null) {
                    return prepareBoList();
                } else {
                    addLstTitle(getText("config.title.lanProfile.edit") + " '" + getChangedName()
                            + "'");
                    return getINPUTType();
                }
            } else if("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
                return createPortAccess();
            } else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
                return updatePortAccess();
            } else if("remove".equals(operation)) {
                boolean succ = false;
                jsonObject = new JSONObject();
                try {
                    succ = removeOperation();
                } catch (Exception e) {
                    LOG.error("Error when try to remove the port access. id="+id, e);
                    if(isJsonMode()) {
                        jsonObject.put("errMsg", convertRemoveErrorMsg(e));
                    } else {
                        addActionError(MgrUtil.getUserMessage(e));
                    }
                }
                if (isJsonMode()) {
                    if (succ) {
                        jsonObject.put("succ", true);
                    }
                    return "json";
                } else {
                    return prepareBoList();
                }
            } else if("clone".equals(operation)) {
                return clonePortAccess();
            } else if("showVlans4Access".equals(operation)) {
                return displayVlansDialog();
            } else if ("selectedVlans4Access".equals(operation)) {
                return selectedNativeVlan();
            } else if ("showUserProfile4Access".equals(operation)) {
                return displayUserProfilesDialog();
            } else if ("selectedUserProfile4Access".equals(operation)) {
                return selectedUserProfiles();
            } else if("newServiceFilter".equals(operation)
            		|| "editServiceFilter".equals(operation)){
            	addLstForward("portAccess");
            	return operation;
            } else if("newConfigmdmPolicy".equals(operation)
            		|| "editConfigmdmPolicy".equals(operation)){
            	
            	addLstForward("portAccess");
            	return operation;
            } else if("listAccess4Port".equals(operation)) {
                //TODO need to filter out the available access profiles according by port template
                initPortAccessList();
                if(null != selectedAccessId) {
                    selectedAccessIds = new ArrayList<>();
                    selectedAccessIds.add(selectedAccessId);
                }
            
                return "listAccess4Port";
            }  else if ("fetchAddRemoveUserGroup".equals(operation)){
				PortAccessProfile lanProfile = findBoById(PortAccessProfile.class, id, this);
				radiusUserGroupIds.clear();

				for(LocalUserGroup group: lanProfile.getRadiusUserGroups()){
					radiusUserGroupIds.add(group.getId());
				}
				
				return "userGroupSelectPage";
			} else if ("finishSelectLocalUserGroup".equals(operation)){
				jsonObject = new JSONObject();
				
				if (radiusUserGroupIds!=null && radiusUserGroupIds.size()>512){
					jsonObject.put("e", getText("error.template.morePskGroupPerTemplate"));
					return "json";
				}
				
				PortAccessProfile lanProfile = findBoById(PortAccessProfile.class, id, this);
				lanProfile = setSelectedLocalUserGroups(lanProfile);
				updateBo(lanProfile);
				jsonObject.put("t", true);
				return "json";
            }else if("continue".equals(operation)){
            	return continueOperation();
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
            return isJsonMode() ? prepareActionJSONError(e) : prepareActionError(e);
        }
    }

    /*-----------_methods---------------*/
    private String prepareActionJSONError(Exception e) {
        LOG.error("prepareActionError", MgrUtil.getUserMessage(e), e);
        addActionError(MgrUtil.getUserMessage(e));
        generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
                + " " + MgrUtil.getUserMessage(e));
        return getINPUTType();
    }
    
    private String convertRemoveErrorMsg(Exception e) {
        final String errMsg;
        if (e instanceof HmException) {
            HmException ee = (HmException) e;
            if(ee.getMessage().contains("failed, stale object state.")) {
                errMsg = MgrUtil.getUserMessage("error.port.access.objectInUse", ee.getParams());
            } else {
                errMsg = MgrUtil.getUserMessage(e);
            }
        } else {
            errMsg = MgrUtil.getUserMessage(e);
        }
        return errMsg;
    }
    
    private void prepareDeviceInfo() {
        if(getDataSource().getProduct() == PortAccessProfile.ACCESS_POINT) {
            portNum = 2;
            deviceType = HiveAp.Device_TYPE_BRANCH_ROUTER;
        } else if (getDataSource().getProduct() == PortAccessProfile.BRANCH_ROUTER){
            portNum = 5;
            if(NetworkPolicyType.get(limitType) == NetworkPolicyType.SUPPORT_AP) {
                deviceType = HiveAp.Device_TYPE_HIVEAP;
            } else {
                deviceType = HiveAp.Device_TYPE_BRANCH_ROUTER;
            }
        }
    }
    
    private void initPortAccessList() {
        FilterParams filter = null;
        if(portNum == 24 || portNum == 48) {
            // SR2024
            if(deviceType == HiveAp.Device_TYPE_SWITCH) {
                filter = new FilterParams("portType != :s1 and portType != :s2 and product = :s3", 
                        new Object[]{PortAccessProfile.PORT_TYPE_WAN, PortAccessProfile.PORT_TYPE_AP, PortAccessProfile.CHESAPEAKE});
            } else if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                filter = new FilterParams("portType != :s1 and product = :s2", 
                        new Object[]{PortAccessProfile.PORT_TYPE_AP, PortAccessProfile.CHESAPEAKE});
            }
        } else if(portNum == 5) {
            //BR100, BR200 and BR200WP
            if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                filter = new FilterParams("(portType = :s1 or portType = :s2 or portType = :s3) and product != :s4", 
                        new Object[]{PortAccessProfile.PORT_TYPE_ACCESS, PortAccessProfile.PORT_TYPE_8021Q, PortAccessProfile.PORT_TYPE_WAN, PortAccessProfile.CHESAPEAKE});
                if(null != portTemplateId) {
                    PortGroupProfile template = QueryUtil.findBoById(PortGroupProfile.class, portTemplateId);
                    // hide the IDM Access for BR100
                    if(null != template && template.getDeviceModels().contains(""+HiveAp.HIVEAP_MODEL_BR100)) {
                        filter = new FilterParams("(portType = :s1 or portType = :s2 or portType = :s3) and product != :s4 and enabledIDM = :s5", 
                                new Object[]{PortAccessProfile.PORT_TYPE_ACCESS, PortAccessProfile.PORT_TYPE_8021Q, PortAccessProfile.PORT_TYPE_WAN, PortAccessProfile.CHESAPEAKE, false});
                    }
                }
            } else if (deviceType == HiveAp.Device_TYPE_HIVEAP) {
                //BR100 only, hide the IDM Access for BR100
                filter = new FilterParams("(portType = :s1 or portType = :s2) and product != :s3 and enabledIDM = :s4", 
                        new Object[]{PortAccessProfile.PORT_TYPE_ACCESS, PortAccessProfile.PORT_TYPE_8021Q, PortAccessProfile.CHESAPEAKE, false});
            }
        } else if(portNum == 2) {
            if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                filter = new FilterParams("(portType = :s1 or portType = :s2 or portType = :s3) and product != :s4", 
                        new Object[]{PortAccessProfile.PORT_TYPE_ACCESS, PortAccessProfile.PORT_TYPE_8021Q, PortAccessProfile.PORT_TYPE_WAN, PortAccessProfile.CHESAPEAKE});
            }
        }
        List<CheckItem> items = getBoCheckItems("name", PortAccessProfile.class, filter);
        setPortAccessList(items);
    }
    
    private String clonePortAccess() throws Exception {
        long cloneId = getSelectedIds().get(0);
        PortAccessProfile clonedObj = findBoById(PortAccessProfile.class, cloneId, this);
        clonedObj.setId(null);
        clonedObj.setName("");
        clonedObj.setOwner(null);
        clonedObj.setVersion(null);
        
        clonedObj.setAuthOkUserProfile(new HashSet<UserProfile>(clonedObj.getAuthOkUserProfile()));
        clonedObj.setAuthOkDataUserProfile(new HashSet<UserProfile>(clonedObj.getAuthOkDataUserProfile()));
        clonedObj.setAuthFailUserProfile(new HashSet<UserProfile>(clonedObj.getAuthFailUserProfile()));
        clonedObj.setRadiusUserGroups(new HashSet<LocalUserGroup>(clonedObj.getRadiusUserGroups()));
        
        setSessionDataSource(clonedObj);
        // need to prepare for the page
        prepareProductFlag();
        prepareAuthentication();
        prepareOptionalSettings();
        
        return getINPUTType();
    }
    
    private String updatePortAccess() throws Exception {
        // check the profile description
        if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
            addActionError(MgrUtil
                    .getUserMessage("error.config.lanProfile.Description.exceed"));
            return getINPUTType();
        }

        setAuthentication();
        setOptionalSettings();
        changePortTypeFromMirror();
        
        updateBo(dataSource);
        if ("update".equals(operation)) {
            if (isJsonMode()) {
                jsonObject = new JSONObject();
                jsonObject.put("succ", true);
                jsonObject.put("normalView", normalView);
                jsonObject.put("id", id);
                return "json";
            } else {
                return prepareBoList();
            }
        } else {
            setUpdateContext(true);
            return getLstForward();
        }
    }

    private String createPortAccess() throws Exception, JSONException {
        String profileName = getDataSource().getName();
        if(StringUtils.isNotBlank(profileName)) {
            String trProfileName = profileName.trim();
            // check the profile name
            if (trProfileName.length() > getProfileNameLength()) {
                addActionError(MgrUtil.getUserMessage("error.config.lanProfile.Name.exceed"));
                return getINPUTType();
            }
            // check is the profile name duplicate
            if(checkNameExists("name", trProfileName)) {
                return getINPUTType();
            }
            // check the profile description
            if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
                addActionError(MgrUtil.getUserMessage("error.config.lanProfile.Description.exceed"));
                return getINPUTType();
            }
            
            setAuthentication();
            setOptionalSettings();
            
            id = createBo(dataSource);
            if ("create".equals(operation)) {
                if(isJsonMode()) {
                    jsonObject = new JSONObject(); 
                    jsonObject.put("succ", true);
                    jsonObject.put("parentDomID", getParentDomID());
                    jsonObject.put("id", id);
                    jsonObject.put("newState", true);
                    jsonObject.put("normalView", normalView);
                    return "json";
                } else {
                    return prepareBoList();
                }
            } else {
                setUpdateContext(true);
                return getLstForward();
            }
        }  else {
            addActionError(MgrUtil.getUserMessage("error.config.lanProfile.noName"));
            return getINPUTType();
        }
    }

    private void prepareProductFlag() {
        if(isJsonMode()) {
            switch (portNum) {
            case 2:
                getDataSource().setProduct(PortAccessProfile.ACCESS_POINT);
                break;
            case 5:
                getDataSource().setProduct(PortAccessProfile.BRANCH_ROUTER);
                break;
            default:
                getDataSource().setProduct(PortAccessProfile.CHESAPEAKE);
                break;
            }
        } else {
            // for navigation tree, enable WAN type
            deviceType = HiveAp.Device_TYPE_BRANCH_ROUTER;
            if(getDataSource().getProduct() == PortAccessProfile.CHESAPEAKE) {
                portNum = 24;
            } else {
                portNum = 5;
            }
        }
    }
    
    private enum AuthMethod {
        _8021X(1), _MBA(2), _CWP(3);
        private int value;
        private AuthMethod(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        
    }
    private void prepareAuthentication() {
        // 1-802.1Q, 2-MAC
        if(NetworkPolicyType.get(limitType) == NetworkPolicyType.SUPPORT_AP) {
            // wireless only (+ bonjour)
            getDataSource().setEnabledPrimaryAuth(getDataSource().isEnabledMAC());
        } else {
            getDataSource().setPrimaryAuth(getDataSource().isFirst8021X() ? AuthMethod._8021X.getValue() : AuthMethod._MBA.getValue());
            getDataSource().setEnabledPrimaryAuth(getDataSource().isEnabled8021X() || getDataSource().isEnabledMAC());
            getDataSource().setEnableSecondaryAuth(getDataSource().isEnabled8021X() && getDataSource().isEnabledMAC());
            if(!getDataSource().isEnabledPrimaryAuth()) {
                // if no any selection, set the 802.1X as default value
                getDataSource().setPrimaryAuth(AuthMethod._8021X.getValue());
            }
        }
        
        prepareIDMStatus();
    }

    private void setAuthentication() {
        // reset
        getDataSource().setEnabled8021X(false);
        getDataSource().setEnabledMAC(false);
        
        if(NetworkPolicyType.get(limitType) == NetworkPolicyType.SUPPORT_AP) {
            // wireless only (+ bonjour)
            getDataSource().setEnabledMAC(getDataSource().isEnabledPrimaryAuth());
            if(!getDataSource().isEnabledMAC() && getDataSource().isEnabled8021X()) {
                // rollback the sequence
                getDataSource().setFirst8021X(true);    
            } else if(getDataSource().isEnabledMAC() && !getDataSource().isEnabled8021X()) {
                getDataSource().setFirst8021X(false);    
            }
        } else {
            boolean first8021X = getDataSource().getPrimaryAuth() == AuthMethod._8021X.getValue();
            getDataSource().setFirst8021X(first8021X);
            
            
            if(getDataSource().isEnabledPrimaryAuth()) {
                if(first8021X) {
                    getDataSource().setEnabled8021X(true);
                } else {
                    getDataSource().setEnabledMAC(true);
                }
            }
            if(getDataSource().isEnableSecondaryAuth()) {
                if(first8021X) {
                    getDataSource().setEnabledMAC(true);
                } else {
                    getDataSource().setEnabled8021X(true);
                }
            }
            
            if(getDataSource().isEnabledMAC()) {
                getDataSource().setEnabledIDM(false);
            }
            if(getDataSource().isEnabledIDM()) {
                if(!getDataSource().isEnabled8021X()) {
                    getDataSource().setEnabledCWP(true); // IDM support, must enabled CWP if 802.1x is not selected
                }
            }
        }
        
        if(null != getDataSource().getId()) {
            // clear by type
            if(getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_WAN
                    || getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR
                    || getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_8021Q) {
                getDataSource().setServiceFilter(null);
                getDataSource().setEnabled8021X(false);
                getDataSource().setEnabledMAC(false);
                getDataSource().setRadiusAssignment(null);
                getDataSource().setEnabledApAuth(false);
                getDataSource().setEnabledCWP(false);
                getDataSource().setCwp(null);
                getDataSource().setDefUserProfile(null);
                getDataSource().setSelfRegUserProfile(null);
                getDataSource().setAuthOkUserProfile(new HashSet<UserProfile>());
                getDataSource().setAuthOkDataUserProfile(new HashSet<UserProfile>());
                getDataSource().setAuthFailUserProfile(new HashSet<UserProfile>());
                getDataSource().setRadiusUserGroups(new HashSet<LocalUserGroup>());
            } else if(getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
                if(getDataSource().isRadiusAuthEnable()) {
                    getDataSource().setAuthFailUserProfile(new HashSet<UserProfile>());
                    
                    getDataSource().setVoiceVlan(null);
                    getDataSource().setDataVlan(null);
                }
                getDataSource().setEnabledCWP(false);
                getDataSource().setCwp(null);
            } else if(getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS){
                getDataSource().setAuthOkDataUserProfile(new HashSet<UserProfile>());
                if (getDataSource().isEnabledCWP()) {
                    if (getDataSource().getCwp() != null) {
                        if (!getDataSource().isEnabledMAC() 
                                && getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
                            getDataSource().setDefUserProfile(null);
                        } else if (!(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
                                || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                            getDataSource().setSelfRegUserProfile(null);
                        }
                    }
                    getDataSource().setAuthFailUserProfile(new HashSet<UserProfile>());
                }
            }
            //normal clear
            if(getDataSource().isEnabledIDM() && getDataSource().getPortType() != PortAccessProfile.PORT_TYPE_ACCESS) {
                getDataSource().setEnabledIDM(false);
            }
            if (getDataSource().isEnabledIDM()
                    && !getDataSource().isEnabled8021X()
                    && getDataSource().isEnabledCWP()
                    && getDataSource().getCwp() != null
                    && !(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED 
                    || getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                getDataSource().setCwp(null);
            }
            if(getDataSource().isIDMAuthEnabled()) {
                if (getDataSource().isEnabled8021X()
                        && getDataSource().isEnabledCWP()
                        && getDataSource().getCwp() != null
                        && getDataSource().getCwp().getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA) {
                    //getDataSource().setCwp(null);
                }
                getDataSource().setRadiusAssignment(null);
            } else if(!getDataSource().isRadiusAuthEnable()) {
                if(!getDataSource().isEnabled8021X() && !getDataSource().isEnabledMAC()) {
                    getDataSource().setRadiusAssignment(null);
                }
                if(!getDataSource().isEnabledCWP()) {
                    getDataSource().setCwp(null);
                }
                getDataSource().setAuthOkUserProfile(new HashSet<UserProfile>());
                getDataSource().setAuthOkDataUserProfile(new HashSet<UserProfile>());
                getDataSource().setAuthFailUserProfile(new HashSet<UserProfile>());
            }
            if(getDataSource().isEnabledSameVlan()) {
                getDataSource().setAuthFailUserProfile(new HashSet<UserProfile>());
            }
            NetworkPolicyType type = NetworkPolicyType.get(limitType);
            if(normalView && getDataSource().getProduct() == PortAccessProfile.CHESAPEAKE 
                    && (type == NetworkPolicyType.SUPPORT_SWITCH || type == NetworkPolicyType.SUPPORT_NO_ROUTER)) {
                getDataSource().setEnabledCWP(false);
                getDataSource().setCwp(null);
            }
            if(getDataSource().getPortType() != PortAccessProfile.PORT_TYPE_8021Q) {
                getDataSource().setNativeVlan(null);
                getDataSource().setAllowedVlan(null);
            }
            if(getDataSource().getPortType() != PortAccessProfile.PORT_TYPE_PHONEDATA) {
                getDataSource().setVoiceVlan(null);
                getDataSource().setDataVlan(null);
            }
            
            if (!(getDataSource().isEnabled8021X() || getDataSource().isEnabledMAC())) {
                getDataSource().setChkUserOnlyDefaults();
            }
            
            if(null != getDataSource().getCwp()) {
                if ((getDataSource().isEnabledIDM() 
                        && !(getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        ||  (getDataSource().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH 
                                && getDataSource().getCwp().isIdmSelfReg())))
                        || (!getDataSource().isEnabledIDM() && getDataSource().getCwp().isIdmSelfReg())) {
                    getDataSource().setCwp(null);
                }
            }
        }
    }
    
    private String displayVlansDialog() {
        try {
            if(null != selectedAccessId) {
                PortAccessProfile accessProfile = QueryUtil.findBoById(PortAccessProfile.class, selectedAccessId, this);
                if(null != accessProfile) {
                    if(null != accessProfile.getNativeVlan()) {
                        selectedVlanId = accessProfile.getNativeVlan().getId();
                        selectedNativeVlanName = accessProfile.getNativeVlan().getVlanName();
                        allowVlans = accessProfile.getAllowedVlan();
                    } else {
                      Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
                      selectedVlanId = vlanClass.getId();
                      selectedNativeVlanName=vlanClass.getVlanName();
                    }
                    acc4Chesapeake = accessProfile.getProduct() == PortAccessProfile.CHESAPEAKE;
                }
            }
            
            vlanList = getBoCheckItems("vlanName", Vlan.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
        } catch (Exception e) {
            LOG.error("displayVlansDialog", "Error to get the VLAN dialog.", e);
        }
        
        return "showVlans4Access";
    }

    private String selectedNativeVlan() {
        jsonObject = new JSONObject();
        try {
            if(null == selectedAccessId) {
                jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
            } else {
                PortAccessProfile accessProfile = QueryUtil.findBoById(PortAccessProfile.class, selectedAccessId, this);
                if(null == accessProfile) {
                    jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
                } else {
                    if(null == selectedVlanId) {
                        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.port.access.nativeVlan.none"));
                    } else {
                        if(selectedVlanId == -1) {
                            Vlan myVlan = CreateObjectAuto.createNewVlan(selectedNativeVlanName, getDomain(),"");
                            if (myVlan!=null){
                                accessProfile.setNativeVlan(myVlan);
                                jsonObject.put("vlanId", myVlan.getId());
                            }
                        } else {
                            accessProfile.setNativeVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class, selectedVlanId));
                        }
                        accessProfile.setAllowedVlan(StringUtils.isBlank(allowVlans) ? null : allowVlans);
                        
                        updateBoWithEvent(accessProfile);
                        
                        jsonObject.put("allowedVlansStr", accessProfile.getAllowedVlanSubstr());
                        jsonObject.put("succ", true);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("selected Native VLAN for Access id=" + selectedAccessId, e);
        }
        return "json";
    }

    private String selectedUserProfiles() throws JSONException {
        //save user profile selection to Access
        jsonObject = new JSONObject();
        
        if (null == selectedAccessId || selectedAccessId < 0L
                || ((this.accUpDefaultIds == null || this.accUpDefaultIds.isEmpty())
                        && (this.accUpRegIds == null || this.accUpRegIds.isEmpty())
                        && (this.accUpGuestIds == null || this.accUpGuestIds.isEmpty())
                        && (this.accUpAuthIds ==  null || this.accUpAuthIds.isEmpty())
                        && (this.accUpAuthFailIds ==  null || this.accUpAuthFailIds.isEmpty())
                        && (this.accUpAuthDataIds ==  null || this.accUpAuthDataIds.isEmpty()))) {
            jsonObject.put("errMsg", MgrUtil.getUserMessage("error.pleaseSelectItemOfRemeved"));
            return "json";
        }
        try {
            PortAccessProfile accessProfile = QueryUtil.findBoById(PortAccessProfile.class, selectedAccessId, this);
            if (accessProfile == null || !checkUpSelectionOfAccess()) {
                // default/self-reg/auth/auth-fail user profile(s) should be different to each other
                jsonObject.put("errMsg",
                                MgrUtil.getUserMessage("info.simple.object.create.failed"));
            } else {
                accessProfile.setDefUserProfile(null);
                accessProfile.setSelfRegUserProfile(null);
                accessProfile.setGuestUserProfile(null);
                accessProfile.setAuthOkUserProfile(new HashSet<UserProfile>());
                accessProfile.setAuthFailUserProfile(new HashSet<UserProfile>());
                // for Phone&Data type
                accessProfile.setAuthOkDataUserProfile(new HashSet<UserProfile>());
                if (this.accUpDefaultIds != null
                        && this.accUpDefaultIds.size() > 0) {
                    UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, this.accUpDefaultIds.get(0));
                    accessProfile.setDefUserProfile(userProfile);
                }
                if (this.accUpRegIds != null
                        && this.accUpRegIds.size() > 0) {
                    UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, this.accUpRegIds.get(0));
                    accessProfile.setSelfRegUserProfile(userProfile);
                }
                if (this.accUpGuestIds != null
                        && this.accUpGuestIds.size() > 0) {
                    UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, this.accUpGuestIds.get(0));
                    accessProfile.setGuestUserProfile(userProfile);
                }
                if (this.accUpAuthIds != null
                        && this.accUpAuthIds.size() > 0) {
                    List<UserProfile> upList = QueryUtil.executeQuery(UserProfile.class, 
                            null, 
                            new FilterParams("id", this.accUpAuthIds), 
                            this.getDomainId());
                    accessProfile.setAuthOkUserProfile(new HashSet<UserProfile>(upList));
                }
                if (this.accUpAuthFailIds != null
                        && this.accUpAuthFailIds.size() > 0) {
                    List<UserProfile> upList = QueryUtil.executeQuery(UserProfile.class, 
                            null, 
                            new FilterParams("id", this.accUpAuthFailIds), 
                            this.getDomainId());
                    accessProfile.setAuthFailUserProfile(new HashSet<UserProfile>(upList));
                }
                // for Phone&Data type
                if (this.accUpAuthDataIds != null
                        && this.accUpAuthDataIds.size() > 0) {
                    List<UserProfile> upList = QueryUtil.executeQuery(UserProfile.class, 
                            null, 
                            new FilterParams("id", this.accUpAuthDataIds), 
                            this.getDomainId());
                    accessProfile.setAuthOkDataUserProfile(new HashSet<UserProfile>(upList));
                }
                
                accessProfile.setDenyAction(this.denyAction);
                accessProfile.setActionTime(this.actionTime);
                accessProfile.setChkUserOnly(this.chkUserOnly);
                accessProfile.setChkDeauthenticate(this.chkDeauthenticate);
                accessProfile.setEnableOsDection(this.isEnableOsDection());
                
                /*
                 * RADIUS attributs mapping fields
                 * Jianliang Chen
                 * 2012-03-31
                 */
                accessProfile.setEnableAssignUserProfile(this.isEnableAssignUserProfile());
                accessProfile.setUserProfileAttributeType(this.getUserProfileAttributeType());
                
                if (isEnableAssignUserProfile()) {
                    if(PortAccessProfile.USERPROFILE_ATTRIBUTE_SPECIFIED == this.getUserProfileAttributeType()){
                        accessProfile.setAssignUserProfileAttributeId(getAssignUserProfileAttributeId());
                        accessProfile.setAssignUserProfileVenderId(0);
                    }else{
                        accessProfile.setAssignUserProfileAttributeId(getAssignUserProfileAttributeCustomerId());
                        accessProfile.setAssignUserProfileVenderId(getAssignUserProfileVenderId());
                    }
                    
                } else {
                    accessProfile.setAssignUserProfileAttributeId(0);
                    accessProfile.setAssignUserProfileVenderId(0);
                    accessProfile.getRadiusUserGroups().clear();
                }
                
                updateNonAuthPhoneVLAN(accessProfile);
                
                updateBoWithEvent(accessProfile);
                
                jsonObject.put("succ", true);
                jsonObject.put("accessId", selectedAccessId);
            }
        } catch (Exception e) {
            jsonObject.put("errMsg", 
                    MgrUtil.getUserMessage("info.simple.object.create.failed"));
            LOG.error("selected UserProfiles for Access id=" + selectedAccessId, e);
        }
        return "json";
    }

    private String displayUserProfilesDialog() {
        if (getAddedUserProfileId() != null && getAddedUserProfileId() > 0L) {
            getUpSelection().setAddedUserProfile(getAddedUserProfileId(), getUpSelectType());
        } else {
            this.getUpSelection().setAddedUserProfile(null, this.getUpSelectType());
        }
        if (null != selectedAccessId) {
            PortAccessProfile accessProfile = QueryUtil.findBoById(PortAccessProfile.class, selectedAccessId, this);
            if (null != accessProfile) {
                setDenyAction(accessProfile.getDenyAction());
                setActionTime(accessProfile.getActionTime());
                setChkUserOnly(accessProfile.isChkUserOnly());
                setChkDeauthenticate(accessProfile.isChkDeauthenticate());
                setEnableOsDection(accessProfile.isEnableOsDection());
                if (isEnableOsDection()) {
                    setHideOsDectionNote("");
                }
                /*
                 * RADIUS attributs mapping fields
                 * Jianliang Chen
                 * 2012-03-31
                 */
                
                setRadiusAuthEnabled(accessProfile.isEnabledIDM() || accessProfile.isRadiusAuthEnable());
                setEnableAssignUserProfile(accessProfile.isEnableAssignUserProfile());
                setUserProfileAttributeType(accessProfile.getUserProfileAttributeType());
                if(PortAccessProfile.USERPROFILE_ATTRIBUTE_SPECIFIED == accessProfile.getUserProfileAttributeType()){
                    setAssignUserProfileAttributeId(accessProfile.getAssignUserProfileAttributeId());
                }else{
                    setAssignUserProfileAttributeCustomerId(accessProfile.getAssignUserProfileAttributeId());
                }
                setAssignUserProfileVenderId(accessProfile.getAssignUserProfileVenderId());
                
                prepareNonAuthPhoneVLAN(accessProfile);
            }
        }
        
        vlanList = getBoCheckItems("vlanName", Vlan.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
        
        return "showUserProfile4Access";
    }

    private void updateNonAuthPhoneVLAN(PortAccessProfile accessProfile)
            throws JSONException {
        if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
            if(accessProfile.isRadiusAuthEnable()) {
                accessProfile.setVoiceVlan(null);
                accessProfile.setDataVlan(null);
            } else {
                if(null == selectedVlanId) {
                    jsonObject.put("errMsg", MgrUtil.getUserMessage("error.port.access.vlan.none", "voice"));
                } else {
                    if(selectedVlanId == -1) {
                        Vlan myVlan = CreateObjectAuto.createNewVlan(selectedVoiceVlanName, getDomain(),"");
                        if (myVlan!=null){
                            accessProfile.setVoiceVlan(myVlan);
                            jsonObject.put("vlanId", myVlan.getId());
                        }
                    } else {
                        accessProfile.setVoiceVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class, selectedVlanId));
                    }
                }
                if(null == selectedDataVlanId) {
                    jsonObject.put("errMsg", MgrUtil.getUserMessage("error.port.access.vlan.none", "voice"));
                } else {
                    if(selectedDataVlanId == -1) {
                        Vlan myVlan = CreateObjectAuto.createNewVlan(selectedDataVlanName, getDomain(),"");
                        if (myVlan!=null){
                            accessProfile.setDataVlan(myVlan);
                            jsonObject.put("vlanId", myVlan.getId());
                        }
                    } else {
                        accessProfile.setDataVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class, selectedDataVlanId));
                    }
                }
            }
        }
    }

    private void prepareNonAuthPhoneVLAN(PortAccessProfile accessProfile) {
        if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
            if(accessProfile.isRadiusAuthEnable()) {
                return;
            }
            if(null != accessProfile.getVoiceVlan()) {
                selectedVlanId = accessProfile.getVoiceVlan().getId();
                selectedVoiceVlanName = accessProfile.getVoiceVlan().getVlanName();
            } else {
                // if not any Voice VLAN, choose the default one
                Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
                selectedVlanId = vlanClass.getId();
                selectedVoiceVlanName=vlanClass.getVlanName();
            }
            if(null != accessProfile.getDataVlan()) {
                selectedDataVlanId = accessProfile.getDataVlan().getId();
                selectedDataVlanName = accessProfile.getDataVlan().getVlanName();
            } else {
                // if not any Voice VLAN, choose the default one
                Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
                selectedDataVlanId = vlanClass.getId();
                selectedDataVlanName=vlanClass.getVlanName();
            }
        }
    }
    
    private void prepareFlag4Page() {
        try {
            if(null != getDataSource()
                    && null != getDataSource().getOwner()
                    && null != getDataSource().getId()) {
                
                final Long ownerId = getDataSource().getOwner().getId();
                String sql = getRelatedPortByAcceeSQL(ownerId, getDataSource().getId());
                List<?> list = QueryUtil.executeNativeQuery(sql);
                
                if (!list.isEmpty()) {
                    // flag to indicate whether the access is used on any device template
                    usedFlag = true;
                    //
                    refTemplateIds = getDeviceTemplateIDs(list);
                    
                    if(list.size() > 1) {
                        // set the used on more than one profile flag
                        multiRef = true;
                    }

                    // hide the WAN port type if used by any switch mode port template
                    if (getDataSource().getProduct() == PortAccessProfile.CHESAPEAKE) {
                        if (getDataSource().getPortType() != PortAccessProfile.PORT_TYPE_WAN && null != refTemplateIds) {
                            String str = Arrays.toString(refTemplateIds);
                            String sql2 = getSwitchModePortCountSQL(ownerId,
                                    str.substring(1, str.length() - 1));
                            list = QueryUtil.executeNativeQuery(sql2);
                            if (!list.isEmpty()) {
                                int count = Integer.parseInt(list.get(0)
                                        .toString());
                                if (count > 0) {
                                    disabledWAN = true;
                                }
                            }
                        }
                    }
                    
                    // avoid to change the WAN to another port-type if it has been assigned on USB port
                    if(getDataSource().getPortType() == PortAccessProfile.PORT_TYPE_WAN) {
                        String sql2 = getWANOnUSBSQL(getDataSource().getId());
                        list = QueryUtil.executeNativeQuery(sql2, 1);
                        unchangeWAN = !list.isEmpty();
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("prepareFlag4Page", "Error when query the relationship for port access.", e);
        }
    }

    private long[] getDeviceTemplateIDs(List<?> list) {
        if (null == list) {
            return null;
        }
        long[] portTemplateIds = new long[list.size()];
        int index = 0;
        for (Object result : list) {
            portTemplateIds[index++] = (long) (Long.parseLong(((Object[]) result)[0].toString()));
        }
        return portTemplateIds;
    }
    private String getRelatedPortByAcceeSQL(long ownerId, long accessId) {
        String query = "select distinct on (portgroups_id, accessprofile_id) portgroups_id, accessprofile_id " +
        		"from port_basic_profile where " +
        		"exists (select 1 from port_template_profile where owner = " + ownerId + " and portgroups_id = id ) " +
        		"and accessprofile_id = " + accessId;
        LOG.debug("getRelatedPortByAcceeSQL", "the sql sentence is: "+query);
        return query;
    }
    private String getSwitchModePortCountSQL(long ownerId, String inIds) {
        String query = "SELECT count(*) from port_template_profile where devicetype = " + HiveAp.Device_TYPE_SWITCH 
                + " and owner = " + ownerId + " and id in (" + inIds + ")";
        LOG.debug("getSwitchModePortCountSQL", "the sql sentence is: "+query);
        return query;
    }
    private String getWANOnUSBSQL(long accessId) {
        String query = "SELECT portgroups_id, accessprofile_id, usbports " +
        		"from port_basic_profile where usbports is not null and accessprofile_id=" + accessId;
        LOG.debug("getWANOnUSBSQL", "the sql sentence is: "+query);
        return query;
    }
    private String getBR100PortCountSQL(long ownerId, String inIds) {
        String query = "SELECT count(*) from port_template_profile where deviceModels like '%" + HiveAp.HIVEAP_MODEL_BR100 
                + "%' and owner = " + ownerId + " and id in (" + inIds + ")";
        LOG.debug("getSwitchModePortCountSQL", "the sql sentence is: "+query);
        return query;
    }
    
    public boolean isShowIDMIcon() {
        boolean apply2BR100 = false;
        if(notChesapeake()) {
            PortGroupProfile template = null; 
            if(null != portTemplateId && portTemplateId > 0) {
                template = QueryUtil.findBoById(PortGroupProfile.class, portTemplateId);
                if(null != template && template.getDeviceModels().contains(""+HiveAp.HIVEAP_MODEL_BR100)) {
                    apply2BR100 = true;
                }
            }
            if(!apply2BR100 && usedFlag && "edit".equals(operation)) {
                //adjust whether the port type has been assigned to the BR100 device template
                if(null != refTemplateIds) {
                    final String str = Arrays.toString(refTemplateIds);
                    String sql = getBR100PortCountSQL(getDataSource().getOwner().getId(), str.substring(1, str.length() - 1));
                    List<?> list = QueryUtil.executeNativeQuery(sql);
                    if (!list.isEmpty()) {
                        int count = Integer.parseInt(list.get(0).toString());
                        if (count > 0) {
                            apply2BR100 = true;
                        }
                    }
                }
            }
        }
        return !apply2BR100;
    }
	// =================== QoS Setting  start =============================
    public EnumItem[] getQosTrusted() {
			return new EnumItem[] { new EnumItem(0,
					getText("config.port.qos.setting.classification.trusted")) };
	}
    
    public EnumItem[] getQosUntrusted() {
    	return new EnumItem[] { new EnumItem(1,
				getText("config.port.qos.setting.classification.untrusted")) };
	}
    
    public EnumItem[] getQosDscp() {
			return new EnumItem[] { new EnumItem(0,
					getText("config.port.qos.setting.classification.trusted.dscp")) };
	}
    
    public EnumItem[] getQos8021p() {
			return new EnumItem[] { new EnumItem(1,
					getText("config.port.qos.setting.classification.trusted.vlan")) };
	}
    
	public EnumItem[] getEnumUntrustedPriority() {
		return EnumConstUtil.ENUM_QOS_CLASS;
	}
	
	public boolean getEnableQosTrusted(){
		if(null == getDataSource()){
			return false;
		}
		
		return getDataSource().getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_TRUSTED;
	}
	
	public boolean getEnableQosUntrustedPriority(){
		if(null == getDataSource()){
			return false;
		}
		
		return getEnableQosTrusted() && getDataSource().isEnableTrustedProiority();
	}
	// =================== QoS Setting  end =============================
	
	// =================== Optional Setting  start =============================
	public EnumItem[] getUsbConnectNeeded() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_NEEDED,
				getText("hiveAp.brRouter.usb.connect.needed")) };
	}

	public EnumItem[] getUsbConnectAlways() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_ALWAYS,
				getText("hiveAp.brRouter.usb.connect.always")) };
	}
	
	private void prepareOptionalSettings(){
		
		if(null != getDataSource().getServiceFilter()){
			serviceFilterId = getDataSource().getServiceFilter().getId();
		}
		if(null != getDataSource().getConfigtempleMdm()){
			configmdmId = getDataSource().getConfigtempleMdm().getId();
		}
		if(null == getDataSource().getId()) {
		    // new, set the default flag
		    final short portType = getDataSource().getPortType();
            if(portType == PortAccessProfile.PORT_TYPE_ACCESS
                    || portType == PortAccessProfile.PORT_TYPE_PHONEDATA
                    || portType == PortAccessProfile.PORT_TYPE_WAN) {
                getDataSource().setEnabledClientReport(true);
            }
		}
	}

	protected void setOptionalSettings() throws Exception{
		if (serviceFilterId != null) {
			ServiceFilter serviceFilter = findBoById(ServiceFilter.class,
					serviceFilterId);
			getDataSource().setServiceFilter(serviceFilter);
		}
		if(configmdmId !=null){
			ConfigTemplateMdm  configtempleMdm=findBoById(ConfigTemplateMdm.class,configmdmId);
			getDataSource().setConfigtempleMdm(configtempleMdm);
		}
		
        if (!getDataSource().isEnableMDM()
                || null == getDataSource().getConfigtempleMdm()
                || !(getDataSource().getConfigtempleMdm().getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH 
                && getDataSource().getConfigtempleMdm().getAwNonCompliance().isEnabledNonCompliance())) {
            getDataSource().setGuestUserProfile(null);
        } else {
            //FIXME AirWatch NonCompliance not support wired right now
            getDataSource().setGuestUserProfile(null);
        }
	}
	
	public void changePortTypeFromMirror(){
		PortAccessProfile oldProfile = QueryUtil.findBoById(PortAccessProfile.class, getDataSource().getId());
		if(oldProfile.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
			final Long ownerId = getDataSource().getOwner().getId();
            String sql = getRelatedPortByAcceeSQL(ownerId, getDataSource().getId());
            List<?> list = QueryUtil.executeNativeQuery(sql);
            if(!list.isEmpty()){
            	
            	Object[] portTemplateIds = new Object[list.size()];
                int index = 0;
                for (Object result : list) {
                    portTemplateIds[index++] = ((Object[]) result)[0].toString();
                }
                String str = Arrays.toString(portTemplateIds);
                String deleteSql = "delete from port_monitor_profile where portgroups_id in (" + str.substring(1, str.length() - 1) + ")";
                try {
					QueryUtil.executeNativeUpdate(deleteSql);
				} catch (Exception e) {
					LOG.error("changePortTypeFromMirror", "Error when delete the relationship for port access.", e);
				}
            	
            }
		}
	}
	
	private Long serviceFilterId;
	private Long configmdmId;

	public Long getConfigmdmId() {
		return configmdmId;
	}

	public void setConfigmdmId(Long configmdmId) {
		this.configmdmId = configmdmId;
	}

	public Long getServiceFilterId() {
		return serviceFilterId;
	}

	public void setServiceFilterId(Long serviceFilterId) {
		this.serviceFilterId = serviceFilterId;
	}

	public List<CheckItem> getServiceFilterList(){
		return getBoCheckItems("filterName", ServiceFilter.class, null);
	}
	public List<CheckItem> getConfigmdmList(){
		return getBoCheckItems("policyname", ConfigTemplateMdm.class, null);
	}
	
	// =================== Optional Setting  end =============================
    
	/*-----------Override---------------*/
    @Override
    public PortAccessProfile getDataSource() {
        return (PortAccessProfile) dataSource;
    }
  
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> tableColumns = new ArrayList<HmTableColumn>(5);
		tableColumns.add(new HmTableColumn(COLUMN_NAME));
		tableColumns.add(new HmTableColumn(COLUMN_DEVICE_TYPE));
		tableColumns.add(new HmTableColumn(COLUMN_PORT_TYPE));
		tableColumns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return tableColumns;
	}
	
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.dnsService.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.dnsService.description";
			break;
		case COLUMN_PORT_TYPE:
			code = "config.port.type.title";
			break;
		case COLUMN_DEVICE_TYPE:
			code = "config.port.device.type.title";
			break;
		}
		return null == code ? "" : MgrUtil.getUserMessage(code);
	}
    
    @Override
    public void prepare() throws Exception {
        super.prepare();
        setSelectedL2Feature(L2_FEATURE_PORTTYPE);
        setDataSource(PortAccessProfile.class);
        keyColumnId = COLUMN_NAME;
        this.tableId = HmTableColumn.TABLE_CONFIGURATION_PORTTYPE_PROFILE;
        // avoid cancel back error when open two edit view
        if(L2_FEATURE_PORTTYPE.equals(request.getParameter("operation"))
                || "create".equals(request.getParameter("operation")) 
                || ("create" + getLstForward()).equals(request.getParameter("operation"))
                || "update".equals(request.getParameter("operation")) 
                || ("update" + getLstForward()).equals(request.getParameter("operation"))) {
            if(null == getDataSource()) {
                //setSessionDataSource(new PortAccessProfile());
            }
        }
    }
    @Override
    public Collection<HmBo> load(HmBo bo) {
        if(bo instanceof PortAccessProfile) {
            PortAccessProfile profile = (PortAccessProfile)bo;
            if(null != profile.getNativeVlan()) {
                profile.getNativeVlan().getId();
            }
            if(null != profile.getCwp()) {
                profile.getCwp().getId();
            }
            if(null != profile.getDefUserProfile()) {
                profile.getDefUserProfile().getId();
            }
            if(null != profile.getSelfRegUserProfile()) {
                profile.getSelfRegUserProfile().getId();
            }
            if(null != profile.getGuestUserProfile()) {
                profile.getGuestUserProfile().getId();
            }
            if(!profile.getAuthOkUserProfile().isEmpty()) {
                profile.getAuthOkUserProfile().size();
            }
            if(!profile.getAuthOkDataUserProfile().isEmpty()) {
                profile.getAuthOkDataUserProfile().size();
            }
            if(!profile.getAuthFailUserProfile().isEmpty()) {
                profile.getAuthFailUserProfile().size();
            }
            if(!profile.getRadiusUserGroups().isEmpty()) {
                profile.getRadiusUserGroups().size();
            }
            if(null != profile.getServiceFilter()) {
                profile.getServiceFilter().getId();
            }
            if (null != profile.getConfigtempleMdm()) {
                profile.getConfigtempleMdm().getId();
            }
            if(null != profile.getVoiceVlan()) {
                profile.getVoiceVlan().getId();
            }
            if(null != profile.getDataVlan()) {
                profile.getDataVlan().getId();
            }
        }
        return null;
    }
    /*------------Fields---------------*/
    private boolean normalView;
    private boolean usedFlag;
    private boolean multiRef;
    private boolean disabledWAN;
    private boolean unchangeWAN;
    private long[] refTemplateIds;
    
    private List<CheckItem> vlanList;
    private Long selectedAccessId;
    private Long selectedVlanId;
    private String selectedNativeVlanName;
    private String allowVlans;
    private boolean acc4Chesapeake;
    
    // filter out the port type according current selected network policy
    private int limitType;
    
    // Id of Port Template (for list dialog)
    private Long portTemplateId;
    private short deviceType;
    private short portNum;
    private short product;
    private int tmpIndex = -1;
    
    // to distinguish whether support the LAN profile 
    private boolean support4LAN;
    
    // bind with access profiles
    private List<Long> selectedAccessIds;
    private List<CheckItem> portAccessList;

    private String getINPUTType() {
        return isJsonMode() ? getInputByProduct() : (notChesapeake() ? "lanProfileInput" :INPUT);
    }
    public String getChangedName() {
        return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
    }
    public int getProfileNameLength() {
        return 32;
    }
    public int getProfileDescirptionLength() {
        return 64;
    }
    
    public int getProfilePortDescirptionLength() {
        return 32;
    }
    
    public EnumItem[] getEnumPortType() {
        final int[] type4All = new int[] {
                PortAccessProfile.PORT_TYPE_ACCESS,
                PortAccessProfile.PORT_TYPE_PHONEDATA, /*PortAccessProfile.PORT_TYPE_AP,*/
                PortAccessProfile.PORT_TYPE_8021Q,
                PortAccessProfile.PORT_TYPE_MONITOR,
                PortAccessProfile.PORT_TYPE_WAN};
        final int[] type4Switch = new int[] {
                PortAccessProfile.PORT_TYPE_ACCESS,
                PortAccessProfile.PORT_TYPE_PHONEDATA, /*PortAccessProfile.PORT_TYPE_AP,*/
                PortAccessProfile.PORT_TYPE_8021Q,
                PortAccessProfile.PORT_TYPE_MONITOR};
        final int[] type4Router = new int[] {
                PortAccessProfile.PORT_TYPE_ACCESS,
                PortAccessProfile.PORT_TYPE_8021Q,
                PortAccessProfile.PORT_TYPE_WAN};
        final int[] type4AP = new int[] {
                PortAccessProfile.PORT_TYPE_ACCESS,
                PortAccessProfile.PORT_TYPE_8021Q};
        int[] portTypes = type4All;
        if(portNum != 0) {
            if(portNum == 24 || portNum == 48) {
                // SR2024
                if(deviceType == HiveAp.Device_TYPE_SWITCH) {
                    portTypes = type4Switch;
                } else if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                    portTypes = type4All;
                }
            } else if(portNum == 5) {
                //BR100, BR200 and BR200WP
                if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                    portTypes = type4Router;
                } else if (deviceType == HiveAp.Device_TYPE_HIVEAP) {
                    portTypes = type4AP;
                }
            } else if(portNum == 2) {
                if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                    portTypes = type4Router;
                }
            }
        } else {
            NetworkPolicyType type = NetworkPolicyType.get(limitType);
            switch (type) {
            case SUPPORT_SWITCH:
            case SUPPORT_NO_ROUTER:
                portTypes = type4Switch;
                break;
            case SUPPORT_AP:
                portTypes = type4AP;
                break;
            default:
                portTypes = type4All;
                break;
            }
        }
        return MgrUtil.enumItems("enum.portConfig.port.type.", portTypes);
    }
    
    private String getInputByProduct() {
        return notChesapeake() ? "jsonLANInput" : "jsoninput";
    }

    private boolean notChesapeake() {
        return portNum == 2 || portNum == 5;
    }
    
    public EnumItem[] getEnumRadiusAuth() {
        return Cwp.ENUM_AUTH_METHOD;
    }
    
    public boolean isBothAuthEnabled() {
        return getDataSource().isEnabled8021X() && getDataSource().isEnabledMAC();
    }
    
    public EnumItem[] getEnumAuthSequence() {
        return MgrUtil.enumItems("enum.lan.auth.sequence.",
                new int[] { PortAccessProfile.AUTH_SEQUENCE_MAC_LAN_CWP, PortAccessProfile.AUTH_SEQUENCE_MAC_CWP_LAN,
                PortAccessProfile.AUTH_SEQUENCE_LAN_MAC_CWP, PortAccessProfile.AUTH_SEQUENCE_LAN_CWP_MAC,
                PortAccessProfile.AUTH_SEQUENCE_CWP_MAC_LAN, PortAccessProfile.AUTH_SEQUENCE_CWP_LAN_MAC });
    }
    
    public boolean isChesapeakeAsRouter() {
        if(portNum == 24 || portNum == 48) {
            if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
                return true;
            }
        }
        return false;
    }
    
    // ==== end: add self-registration & radius user profiles ====
    public EnumItem[] getEnumDenyAction() {
        return MgrUtil.enumItems("enum.denyAction.", new int[] {
                PortAccessProfile.DENY_ACTION_BAN,
                PortAccessProfile.DENY_ACTION_BAN_FOREVER,
                PortAccessProfile.DENY_ACTION_DISCONNECT });
    }
    
    public String getHideActionStyle() {
        if (this.isChkUserOnly()) {
            return "";
        } else {
            return "none";
        }
    }

    public String getDefaultUserProfieStype() {
        return getUpSelection().isDefaultUserProfileSupport() ? "" : "none";
    }

    public String getSelfRegUserProfieStype() {
        return this.getUpSelection().isSelfUserProfileSupport() ? "" : "none";
    }
    
    public String getGuestUserProfieStype() {
        return this.getUpSelection().isGuestUserProfileSupport() ? "" : "none";
    }
    
    public String getAuthUserProfieStype() {
        return getUpSelection().isAuthUserProfileSupport() ? "" : "none";
    }
    
    public String getAuthDataUserProfieStype() {
        return getUpSelection().isAuthDataUserProfileSupport() ? "" : "none";
    }
    
    public String getAuthFailUserProfieStype() {
        return getUpSelection().isAuthFailUserProfileSupport() ? "" : "none";
    }
    
    private UserProfileSelectionProxy upSelection;
    
    private boolean configPhoneData;

    public UserProfileSelectionProxy getUpSelection() {
        if (upSelection == null) {
            upSelection = new UserProfileSelectionProxy(
                                new PortUserProfileSelectionImpl(selectedAccessId, 
                                        PortAccessProfile.class, 
                                        this.getDomainId(), isConfigPhoneData()));
        }
        return upSelection;
    }
    
    private Long addedUserProfileId;
    private List<Long> accUpDefaultIds;
    private List<Long> accUpRegIds;
    private List<Long> accUpAuthIds;
    private List<Long> accUpAuthFailIds;
    private List<Long> accUpGuestIds;
    
    private List<Long> accUpAuthDataIds;
    // Voice VLAN
    private String selectedVoiceVlanName;
    // Data VLAN
    private Long selectedDataVlanId;
    private String selectedDataVlanName;
    
    private boolean checkUpSelectionOfAccess() {
        if (this.accUpDefaultIds != null && this.accUpDefaultIds.size() > 0) {
            for (Long idTmp : this.accUpDefaultIds) {
                if ((this.accUpRegIds != null && this.accUpRegIds.contains(idTmp))
                        || (this.accUpGuestIds != null && this.accUpGuestIds.contains(idTmp))
                        || (this.accUpAuthIds != null && this.accUpAuthIds.contains(idTmp))
                        || (this.accUpAuthFailIds != null && this.accUpAuthFailIds.contains(idTmp))
                        || (this.accUpAuthDataIds != null && this.accUpAuthDataIds.contains(idTmp))) {
                    return false;
                }
            }
        }
        
        if (this.accUpRegIds != null && this.accUpRegIds.size() > 0) {
            for (Long idTmp : this.accUpRegIds) {
                if ((this.accUpAuthIds != null && this.accUpAuthIds.contains(idTmp))
                        || (this.accUpDefaultIds != null && this.accUpDefaultIds.contains(idTmp))
                        || (this.accUpGuestIds != null && this.accUpGuestIds.contains(idTmp))
                        || (this.accUpAuthFailIds != null && this.accUpAuthFailIds.contains(idTmp))
                        || (this.accUpAuthDataIds != null && this.accUpAuthDataIds.contains(idTmp))) {
                    return false;
                }
            }
        }
        if (this.accUpGuestIds != null && this.accUpGuestIds.size() > 0) {
            for (Long idTmp : this.accUpGuestIds) {
                if ((this.accUpAuthIds != null && this.accUpAuthIds.contains(idTmp))
                        || (this.accUpDefaultIds != null && this.accUpDefaultIds.contains(idTmp))
                        || (this.accUpRegIds != null && this.accUpRegIds.contains(idTmp))
                        || (this.accUpAuthFailIds != null && this.accUpAuthFailIds.contains(idTmp))
                        || (this.accUpAuthDataIds != null && this.accUpAuthDataIds.contains(idTmp))) {
                    return false;
                }
            }
        }
        
        // also, all selections should be in database
        List<Long> allUpSelected = new ArrayList<Long>();
        if (this.accUpDefaultIds != null && this.accUpDefaultIds.size() > 0) {
            allUpSelected.addAll(this.accUpDefaultIds);
        }
        if (this.accUpRegIds != null && this.accUpRegIds.size() > 0) {
            allUpSelected.addAll(this.accUpRegIds);
        }
        if (this.accUpGuestIds != null && this.accUpGuestIds.size() > 0) {
            allUpSelected.addAll(this.accUpGuestIds);
        }
        if (this.accUpAuthIds != null && this.accUpAuthIds.size() > 0) {
            allUpSelected.addAll(this.accUpAuthIds);
        }
        if (this.accUpAuthFailIds != null && this.accUpAuthFailIds.size() > 0) {
            allUpSelected.addAll(this.accUpAuthFailIds);
        }
        // for Phone&Data type
        if (this.accUpAuthDataIds != null && this.accUpAuthDataIds.size() > 0) {
            allUpSelected.addAll(this.accUpAuthDataIds);
        }
        
        List<UserProfile> upList = QueryUtil.executeQuery(UserProfile.class, 
                null, 
                new FilterParams("id", allUpSelected),
                this.getDomainId());
        
        if (upList == null
                || allUpSelected == null
                || upList.size() != allUpSelected.size()) {
            return false;
        }
        
        return true;
    }

    private short upSelectType = UserProfileSelection.USER_PROFILE_TYPE_DEFAULT;
    // ==== end: add self-registration & radius user profiles ====
            
    // ==== start: radius user groups and radius attribute mapping ====
    private short denyAction;
    private long actionTime;
    private boolean chkUserOnly;
    private boolean chkDeauthenticate;
    private boolean enableOsDection;
    private String hideOsDectionNote = "none";
    
    private boolean enableAssignUserProfile = false;
    private int assignUserProfileAttributeId = 11;
    private int assignUserProfileVenderId;
    private String assignUserProfileAttributeIdShow = "11_Filter-Id";
    private short userProfileAttributeType = PortAccessProfile.USERPROFILE_ATTRIBUTE_SPECIFIED;
    private int assignUserProfileAttributeCustomerId;
    
    private boolean isRadiusAuthEnabled = false;
    
    public String getShowAssignUserProfile(){
        return this.isRadiusAuthEnabled() ? "":"none";
    }
    public String getAssignUserProfileStyle(){
        return this.isRadiusAuthEnabled()
                && this.enableAssignUserProfile ? "" : "none";
    }
    
    public String getAssignUserProfileVenderIdStr(){
        return this.assignUserProfileVenderId == 0 ? "": String.valueOf(this.assignUserProfileVenderId);
    }
    
    public String getAssignUserProfileAttributeCustomerIdStr(){
        return assignUserProfileAttributeCustomerId == 0? "": String.valueOf(assignUserProfileAttributeCustomerId);
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
    // ==== end: radius user groups and radius attribute mapping ====
    
    /*------------Getter/Setter---------------*/

    public List<CheckItem> getVlanList() {
        return vlanList;
    }

    public Long getSelectedAccessId() {
        return selectedAccessId;
    }

    public Long getSelectedVlanId() {
        return selectedVlanId;
    }

    public String getAllowVlans() {
        return allowVlans;
    }

    public void setVlanList(List<CheckItem> vlanList) {
        this.vlanList = vlanList;
    }

    public void setSelectedAccessId(Long selectedAccessId) {
        this.selectedAccessId = selectedAccessId;
    }

    public void setSelectedVlanId(Long selectedVlanId) {
        this.selectedVlanId = selectedVlanId;
    }

    public void setAllowVlans(String allowVlans) {
        this.allowVlans = allowVlans;
    }

    public boolean isNormalView() {
        return normalView;
    }

    public void setNormalView(boolean normalView) {
        this.normalView = normalView;
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

    public boolean isChkUserOnly() {
        return chkUserOnly;
    }

    public void setChkUserOnly(boolean chkUserOnly) {
        this.chkUserOnly = chkUserOnly;
    }

    public boolean isChkDeauthenticate() {
        return chkDeauthenticate;
    }

    public void setChkDeauthenticate(boolean chkDeauthenticate) {
        this.chkDeauthenticate = chkDeauthenticate;
    }
    
    public boolean isEnableOsDection() {
        return enableOsDection;
    }

    public void setEnableOsDection(boolean enableOsDection) {
        this.enableOsDection = enableOsDection;
    }

    public String getHideOsDectionNote() {
        return hideOsDectionNote;
    }

    public void setHideOsDectionNote(String hideOsDectionNote) {
        this.hideOsDectionNote = hideOsDectionNote;
    }

    public short getUpSelectType() {
        return upSelectType;
    }

    public void setUpSelectType(short upSelectType) {
        this.upSelectType = upSelectType;
    }

    public Long getAddedUserProfileId() {
        return addedUserProfileId;
    }

    public void setAddedUserProfileId(Long addedUserProfileId) {
        this.addedUserProfileId = addedUserProfileId;
    }

    public boolean isEnableAssignUserProfile() {
        return enableAssignUserProfile;
    }

    public int getAssignUserProfileAttributeId() {
        return assignUserProfileAttributeId;
    }

    public int getAssignUserProfileVenderId() {
        return assignUserProfileVenderId;
    }

    public int getAssignUserProfileAttributeCustomerId() {
        return assignUserProfileAttributeCustomerId;
    }

    public boolean isRadiusAuthEnabled() {
        return isRadiusAuthEnabled;
    }

    public void setEnableAssignUserProfile(boolean enableAssignUserProfile) {
        this.enableAssignUserProfile = enableAssignUserProfile;
    }

    public void setAssignUserProfileAttributeId(int assignUserProfileAttributeId) {
        this.assignUserProfileAttributeId = assignUserProfileAttributeId;
    }

    public void setAssignUserProfileVenderId(int assignUserProfileVenderId) {
        this.assignUserProfileVenderId = assignUserProfileVenderId;
    }

    public void setAssignUserProfileAttributeCustomerId(
            int assignUserProfileAttributeCustomerId) {
        this.assignUserProfileAttributeCustomerId = assignUserProfileAttributeCustomerId;
    }

    public void setRadiusAuthEnabled(boolean isRadiusAuthEnabled) {
        this.isRadiusAuthEnabled = isRadiusAuthEnabled;
    }

    public short getUserProfileAttributeType() {
        return userProfileAttributeType;
    }

    public void setUserProfileAttributeType(short userProfileAttributeType) {
        this.userProfileAttributeType = userProfileAttributeType;
    }

    public void setAccUpDefaultIds(List<Long> accUpDefaultIds) {
        this.accUpDefaultIds = accUpDefaultIds;
    }

    public void setAccUpRegIds(List<Long> accUpRegIds) {
        this.accUpRegIds = accUpRegIds;
    }

    public void setAccUpGuestIds(List<Long> accUpGuestIds) {
        this.accUpGuestIds = accUpGuestIds;
    }

    public void setAccUpAuthIds(List<Long> accUpAuthIds) {
        this.accUpAuthIds = accUpAuthIds;
    }

    public void setAccUpAuthFailIds(List<Long> accUpAuthFailIds) {
        this.accUpAuthFailIds = accUpAuthFailIds;
    }

    public boolean isConfigPhoneData() {
        return configPhoneData;
    }

    public void setConfigPhoneData(boolean configPhoneData) {
        this.configPhoneData = configPhoneData;
    }

    public void setAccUpAuthDataIds(List<Long> accUpAuthDataIds) {
        this.accUpAuthDataIds = accUpAuthDataIds;
    }

    public String getSelectedVoiceVlanName() {
        return selectedVoiceVlanName;
    }

    public void setSelectedVoiceVlanName(String selectedVoiceVlanName) {
        this.selectedVoiceVlanName = selectedVoiceVlanName;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public Long getPortTemplateId() {
        return portTemplateId;
    }

    public void setPortTemplateId(Long portTemplateId) {
        this.portTemplateId = portTemplateId;
    }

    public short getDeviceType() {
        return deviceType;
    }

    public short getPortNum() {
        return portNum;
    }

    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }

    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }

    public List<Long> getSelectedAccessIds() {
        return selectedAccessIds;
    }

    public void setSelectedAccessIds(List<Long> selectedAccessIds) {
        this.selectedAccessIds = selectedAccessIds;
    }

    public boolean isSupport4LAN() {
        return support4LAN;
    }

    public void setSupport4LAN(boolean support4lan) {
        support4LAN = support4lan;
    }

    public void setPortAccessList(List<CheckItem> portAccessList) {
        this.portAccessList = portAccessList;
    }

    public List<CheckItem> getPortAccessList() {
        return portAccessList;
    }

    public boolean isAcc4Chesapeake() {
        return acc4Chesapeake;
    }

    public void setAcc4Chesapeake(boolean acc4Chesapeake) {
        this.acc4Chesapeake = acc4Chesapeake;
    }

    public boolean isMultiRef() {
        return multiRef;
    }

    public void setMultiRef(boolean multiRef) {
        this.multiRef = multiRef;
    }

    public boolean isDisabledWAN() {
        return disabledWAN;
    }

    public void setDisabledWAN(boolean disabledWAN) {
        this.disabledWAN = disabledWAN;
    }

    public boolean isUnchangeWAN() {
        return unchangeWAN;
    }

    public void setUnchangeWAN(boolean unchangeWAN) {
        this.unchangeWAN = unchangeWAN;
    }

    public String getSelectedNativeVlanName() {
        return selectedNativeVlanName;
    }

    public void setSelectedNativeVlanName(String selectedNativeVlanName) {
        this.selectedNativeVlanName = selectedNativeVlanName;
    }
    
    public boolean isUsedFlag() {
        return usedFlag;
    }

    public void setUsedFlag(boolean usedFlag) {
        this.usedFlag = usedFlag;
    }
    
    public Long getSelectedDataVlanId() {
        return selectedDataVlanId;
    }

    public String getSelectedDataVlanName() {
        return selectedDataVlanName;
    }

    public void setSelectedDataVlanId(Long selectedDataVlanId) {
        this.selectedDataVlanId = selectedDataVlanId;
    }

    public void setSelectedDataVlanName(String selectedDataVlanName) {
        this.selectedDataVlanName = selectedDataVlanName;
    }

    public long[] getRefTemplateIds() {
        return refTemplateIds;
    }

    public void setRefTemplateIds(long[] refTemplateIds) {
        this.refTemplateIds = refTemplateIds;
    }

    protected PortAccessProfile setSelectedLocalUserGroups(PortAccessProfile lanProfile) throws Exception {
		Set<LocalUserGroup> lanLocalUserGroups = lanProfile.getRadiusUserGroups();
		lanLocalUserGroups.clear();
		
		if (radiusUserGroupIds != null) {
			for (Long filterId : radiusUserGroupIds) {
				LocalUserGroup localUserGroup = findBoById(
						LocalUserGroup.class, filterId);
				if (localUserGroup != null) {
					lanLocalUserGroups.add(localUserGroup);
				}
			}
		}
		
		lanProfile.setRadiusUserGroups(lanLocalUserGroups);
		
		return lanProfile;
	}

	public List<Long> getRadiusUserGroupIds() {
		return radiusUserGroupIds;
	}

	public void setRadiusUserGroupIds(List<Long> radiusUserGroupIds) {
		this.radiusUserGroupIds = radiusUserGroupIds;
	}
	
	public List<CheckItem> getLstLocalUserGroups() {
		return getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType=:s1", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_RADIUS}));
	}
	
	public String getUpdateDisabled() {
		return getWriteDisabled();
	}

	public int getTmpIndex() {
		return tmpIndex;
	}

	public void setTmpIndex(int tmpIndex) {
		this.tmpIndex = tmpIndex;
	}
	
	public short getProduct() {
        return product;
    }

    public void setProduct(short product) {
        this.product = product;
    }

    @Override
	protected List<HmBo> paintbrushBos(Long paintbrushSourceId, Set<Long> destinationIds) {
		PortAccessProfile sourceObj = QueryUtil.findBoById(PortAccessProfile.class, paintbrushSourceId, this);
		if (null == sourceObj) {
			return null;
		}
		List<PortAccessProfile> list = QueryUtil.executeQuery(PortAccessProfile.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (null == list || list.isEmpty()) {
			return null;
		}
		List<HmBo> paintedList = new ArrayList<HmBo>();
		for (PortAccessProfile destObj : list) {
			if (destObj.getId().equals(paintbrushSourceId)) {
				continue;
			}
			PortAccessProfile cloneObj = sourceObj.clone();
			if(null == cloneObj) {
				continue;
			}
			cloneObj.setId(destObj.getId());
			cloneObj.setVersion(destObj.getVersion());
			cloneObj.setName(destObj.getName());
			cloneObj.setOwner(destObj.getOwner());
			
			cloneObj.setAuthOkUserProfile(new HashSet<UserProfile>(destObj.getAuthOkUserProfile()));
			cloneObj.setAuthOkDataUserProfile(new HashSet<UserProfile>(destObj.getAuthOkDataUserProfile()));
			cloneObj.setAuthFailUserProfile(new HashSet<UserProfile>(destObj.getAuthFailUserProfile()));
			cloneObj.setRadiusUserGroups(new HashSet<LocalUserGroup>(destObj.getRadiusUserGroups()));
			
			paintedList.add(cloneObj);
		}
		
		return paintedList;
	}
	
	private String continueOperation() throws Exception {
		if (dataSource == null) {
			return prepareBoList();
		} else {
			setId(dataSource.getId());
			setTabId(getLstTabId());
			//used for only from nav
			portNum = 5; 
			if (serviceFilterId != null) {
				ServiceFilter serviceFilter = findBoById(ServiceFilter.class,
						serviceFilterId);
				getDataSource().setServiceFilter(serviceFilter);
			}
			prepareProductFlag();
			prepareDeviceInfo();
			
			prepareFlag4Page();
			prepareAuthentication();
			prepareOptionalSettings();
			
             
			if (getUpdateContext()) {
				removeLstTitle();
				removeLstTabId();
				removeLstForward();
				setUpdateContext(false);
			}
			return getINPUTType();
		}
	}
}
