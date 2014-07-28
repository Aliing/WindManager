package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.lan.LanInterfacesMode;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.Cwp;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.userprofile.selection.LanUserProfileSelectionImpl;

public class LanProfilesAction extends BaseAction implements QueryBo {

    private static final String SEPARATOR_COMMA = ",";

	private static final long serialVersionUID = -768463956297070264L;
	/*----TA752: LAN profile column selection-----*/
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_ACCESS_MODE = 3;
	public static final int COLUMN_CWP_ENABLE = 4;
	public static final int COLUMN_MAC_AUTH_ENABLE = 5;
	
	/*
	 * Added for Denmark RADIUS attribute mapping
	 * 
	 * Jianliang Chen
	 * 2012-03-31
	 */
	protected List<Long> radiusUserGroupIds = new ArrayList<>();
	
	private static final Tracer log = new Tracer(LanProfilesAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		try {
			if("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.lanProfile"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new LanProfile());
				initDependenceObjects();
				return getINPUTType();
			} else if("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				return createLANProfile();
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				return updateLANProfile();
			} else if("edit".equals(operation)){
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					// prepare the initial values
					initDependenceObjects();
					addLstTitle(getText("config.title.lanProfile.edit") + " '" + getChangedName()
							+ "'");
					return getINPUTType();
				}
			} else if (isSubProfileOperation()){
				if(isJsonMode()) {
					return operation;
				} else {
					// save change into session
					getDataSource().setLanInterfacesMode(change2ModeObject(getDataSource().getLanInterfacesMode()));
					updateDependenceObjects();
					
					clearErrorsAndMessages();
					addLstForward(L2_FEATURE_LAN);
					
					return operation;
				}
			} else if ("continue".equals(operation)) {
				return continueOperation();
			} else if("remove".equals(operation)) {
				baseOperation();
				return prepareBoList();
			} else if("clone".equals(operation)) {
				cloneLanProfile();
				return getINPUTType();
			} else if("showNetworks4LAN".equals(operation)){
				return displayNetworksDialog();
			} else if("selectNetworks4LAN".equals(operation)) {
				return selectNetworksOperation();
			} else if("showVlans4LAN".equals(operation)) {
			    return displayVlansDialog();
			} else if("selectVlans4LAN".equals(operation)) {
			    return selectVlansOperation();
			} else if ("fetchAddRemoveUserGroup".equals(operation)){
				LanProfile lanProfile = findBoById(LanProfile.class, id, this);
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
				
				LanProfile lanProfile = findBoById(LanProfile.class, id, this);
				lanProfile = setSelectedLocalUserGroups(lanProfile);
				updateBo(lanProfile);
				jsonObject.put("t", true);
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

    // ----------------Methods for operation---------------------//
	private String getINPUTType() {
		return isJsonMode() ? "jsoninput" : INPUT;
	}
	
	private String createLANProfile() throws Exception {
		String profileName = getDataSource().getName();
		if(StringUtils.isNotBlank(profileName)) {
			String trProfileName = profileName.trim();
			// check the profile name
			if (trProfileName.length() > getProfileNameLength()) {
				addActionError(MgrUtil.getUserMessage("error.config.lanProfile.Name.exceed"));
				initDependenceObjects();
				return getINPUTType();
			}
			// check is the profile name duplicate
			if(checkNameExists("name", trProfileName)) {
				initDependenceObjects();
				return getINPUTType();
			}
			// check the profile description
			if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
				addActionError(MgrUtil.getUserMessage("error.config.lanProfile.Description.exceed"));
				initDependenceObjects();
				return getINPUTType();
			}
			
			// set the dependence objects
			updateDependenceObjects();
			
			// set the LAN Interfaces Mode
			LanInterfacesMode lanInterfacesMode = updateLANInterfacesMode(null);
			getDataSource().setLanInterfacesMode(lanInterfacesMode);
			
			// create a LAN profile
			id = createBo(dataSource);
			if ("create".equals(operation)) {
				if(isJsonMode()) {
					jsonObject = new JSONObject(); 
					jsonObject.put("succ", true);
					jsonObject.put("id", id);
					jsonObject.put("newState", true);
					return "json";
				} else {
					return prepareBoList();
				}
			} else {
				setUpdateContext(true);
				return getLstForward();
			}
		} else {
			addActionError(MgrUtil.getUserMessage("error.config.lanProfile.noName"));
			initDependenceObjects();
			return getINPUTType();
		}
	}

    private String updateLANProfile() throws Exception {
		// check the profile description
		if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
			addActionError(MgrUtil.getUserMessage("error.config.lanProfile.Description.exceed"));
			initDependenceObjects();
			return getINPUTType();
		}
		
		// set the dependence objects
		updateDependenceObjects();
		
		// set the LAN Interfaces Mode
		updateLANInterfacesMode(getDataSource().getLanInterfacesMode());
		
		if (isJsonMode()){
			setId(dataSource.getId());
		}
		// update a LAN profile
		if ("update".equals(operation)){
			updateBo(dataSource);
			if(isJsonMode()) {
				jsonObject = new JSONObject(); 
				jsonObject.put("succ", true);
				return "json";
			} else {
				return prepareBoList();
			}
		} else {
			updateBo(dataSource);
			setUpdateContext(true);
			return getLstForward();
		}
	}
	
	/**
	 * update the LAN Interfaces Mode
	 * 
	 * @author Yunzhi Lin
	 * - Time: Jul 12, 2011 2:32:05 PM
	 * @param mode null or object
	 * @return the LAN interfaces mode object
	 * @throws Exception 
	 */
	private LanInterfacesMode updateLANInterfacesMode(LanInterfacesMode mode) throws Exception {
		if(null == mode) {
			mode = new LanInterfacesMode();
		}
		log.debug("Interface mode is:"+getInterfacesMode());
		change2ModeObject(mode);
		log.debug("the lanInterfacesMode:"+mode);
		return mode;
	}

	private LanInterfacesMode change2ModeObject(LanInterfacesMode mode) {
		if(null == mode) {
			mode = new LanInterfacesMode();
		}
		
		String[] modeArray = StringUtils.split(getInterfacesMode(), SEPARATOR_COMMA);
		if(ArrayUtils.isEmpty(modeArray)){
			return mode;
		}
		log.debug("modeArray:"+Arrays.toString(modeArray));
		mode.setEth1On(Boolean.parseBoolean(modeArray[0]));
		mode.setEth2On(Boolean.parseBoolean(modeArray[1]));
		mode.setEth3On(Boolean.parseBoolean(modeArray[2]));
		mode.setEth4On(Boolean.parseBoolean(modeArray[3]));
		return mode;
	}
	
	private void updateDependenceObjects() throws Exception {
		// DoS Prevention
		if (null != ipDos) {
			DosPrevention ipDosObj = findBoById(DosPrevention.class, ipDos);
			if (null == ipDosObj && ipDos != -1) {
				addActionError(getText("info.ssid.warning", new String[]{getText("config.ssid.ipDos")}));
			}
			getDataSource().setIpDos(ipDosObj);
		}
		// Traffic Filter
		if (null != serviceFilter) {
			ServiceFilter serviceFilterObj = findBoById(ServiceFilter.class,
					serviceFilter);
			if (null == serviceFilterObj && serviceFilter != -1) {
				addActionError(getText("info.ssid.warning", new String[]{getText("config.ssid.serviceFilter")}));
			}
			getDataSource().setServiceFilter(serviceFilterObj);
		}
		
		if(getDataSource().isEnabled8021Q()) {
			getDataSource().setCwpSelectEnabled(false);
			getDataSource().setMacAuthEnabled(false);
			getDataSource().setUserProfileDefault(null);
		} else {
			getDataSource().setNativeNetwork(null);
			getDataSource().setRegularNetworks(new HashSet<VpnNetwork>());
			
			getDataSource().setNativeVlan(null);
			getDataSource().setRegularVlans(new HashSet<Vlan>());
		}
		
		if (!getDataSource().isCwpSelectEnabled()){
			getDataSource().setCwp(null);
		}
		
		if (!getDataSource().isCwpSelectEnabled()
				&& !getDataSource().isRadiusAuthEnable()) {
			getDataSource().setRadiusAssignment(null);
		}

		if (getDataSource().isCwpSelectEnabled()){
			if (getDataSource().getCwp()!=null) {
				if (!getDataSource().isRadiusAuthEnable()){
					getDataSource().setMacAuthEnabled(false);
					getDataSource().setRadiusAssignment(null);
				}
			}
		}
		
		LanUserProfileSelectionImpl lanUpHelper = new LanUserProfileSelectionImpl(getDataSource());
		lanUpHelper.doUpSupportDataPrepare();
		if (!lanUpHelper.isDefaultUserProfileSupport()) {
			getDataSource().setUserProfileDefault(null);
		}
		if (!lanUpHelper.isSelfUserProfileSupport()) {
			getDataSource().setUserProfileSelfReg(null);
		}
		if (!lanUpHelper.isAuthUserProfileSupport()) {
			getDataSource().getRadiusUserProfile().clear();
		}
		if (!lanUpHelper.isChkUserOnlyEnabled()) {
			getDataSource().setChkUserOnlyDefaults();
		}
		
		if(!getDataSource().isRadiusAuthEnable()) {
			getDataSource().setEnableAssignUserProfile(false);
			getDataSource().setAssignUserProfileAttributeId(0);
			getDataSource().setAssignUserProfileVenderId(0);
			getDataSource().getRadiusUserGroups().clear();
		}
	}

	private String continueOperation() throws Exception {
		if (dataSource == null) {
			return prepareBoList();
		} else {
			setId(dataSource.getId());
			setTabId(getLstTabId());
			// update data from session
			updateDependenceObjects();
			// initial data
			initDependenceObjects();
			
			if (getUpdateContext()) {
				removeLstTitle();
				removeLstTabId();
				removeLstForward();
				setUpdateContext(false);
			}
			return getINPUTType();
		}
	}
	
	private boolean isSubProfileOperation() {
		return "newIpDos".equals(operation) 
				|| "editIpDos".equals(operation)
				|| "newServiceFilter".equals(operation) 
				|| "editServiceFilter".equals(operation);
	}

	/**
	 * TA750: LAN profile clone operation
	 * @author Yunzhi Lin
	 * - Time: Jul 23, 2011 5:50:46 PM
	 * @throws Exception -
	 */
	private void cloneLanProfile() throws Exception {
		long cloneId = getSelectedIds().get(0);
		LanProfile clonedObj = findBoById(LanProfile.class, cloneId, this);
		clonedObj.setId(null);
		clonedObj.setName("");
		clonedObj.setOwner(null);
		clonedObj.setVersion(null);
		
		clonedObj.setSchedulers(new HashSet<>(clonedObj.getSchedulers()));
		clonedObj.setRegularNetworks(new HashSet<>(clonedObj.getRegularNetworks()));
		clonedObj.setRadiusUserProfile(new HashSet<>(clonedObj.getRadiusUserProfile()));
		
		setSessionDataSource(clonedObj);
		
		initDependenceObjects();
	}
	
	/**
	 * TA751: LAN profile paintbrush operation
	 * @author Yunzhi Lin
	 */
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSourceId, Set<Long> destinationIds) {
		LanProfile sourceObj = QueryUtil.findBoById(LanProfile.class, paintbrushSourceId, this);
		if (null == sourceObj) {
			return null;
		}
		List<LanProfile> list = QueryUtil.executeQuery(LanProfile.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (null == list || list.isEmpty()) {
			return null;
		}
		List<HmBo> paintedList = new ArrayList<>();
		for (LanProfile destObj : list) {
			if (destObj.getId().equals(paintbrushSourceId)) {
				continue;
			}
			LanProfile cloneObj = sourceObj.clone();
			if(null == cloneObj) {
				continue;
			}
			cloneObj.setId(destObj.getId());
			cloneObj.setVersion(destObj.getVersion());
			cloneObj.setName(destObj.getName());
			cloneObj.setOwner(destObj.getOwner());
			
			// handle the Interfaces Mode
			LanInterfacesMode lanInterfacesMode = cloneObj.getLanInterfacesMode();
			LanInterfacesMode destLanInterfacesMode = destObj.getLanInterfacesMode();
			if(null != lanInterfacesMode && null != destLanInterfacesMode) {
				destLanInterfacesMode.setEth1On(lanInterfacesMode.isEth1On());
				destLanInterfacesMode.setEth2On(lanInterfacesMode.isEth2On());
				destLanInterfacesMode.setEth3On(lanInterfacesMode.isEth3On());
				destLanInterfacesMode.setEth4On(lanInterfacesMode.isEth4On());
				
				try {
					cloneObj.setLanInterfacesMode(destLanInterfacesMode);
				} catch (Exception e) {
					log.error("Error when update the Interfaces Mode.", e);
				}
			}
			
			cloneObj.setSchedulers(new HashSet<>(sourceObj.getSchedulers()));
			cloneObj.setRegularNetworks(new HashSet<>(sourceObj.getRegularNetworks()));
			cloneObj.setRadiusUserProfile(new HashSet<>(cloneObj.getRadiusUserProfile()));
			
			paintedList.add(cloneObj);
		}
		
		return paintedList;
	}
	
	/**
	 * TA1119: add a select dialog for Networks
	 * - bind the selected Networks to the LAN profile
	 * @author Yunzhi Lin
	 * - Time: Aug 18, 2011 2:24:46 PM
	 * @return <code>JSON</code>
	 * @throws JSONException
	 */
	private String selectNetworksOperation() throws JSONException {
		log.debug("selectedLANId:"+selectedLANId);
		log.debug("untaggedNetworkId:"+untaggedNetworkId+" taggedNetworkIds:"+taggedNetworkIds);
		
		jsonObject = new JSONObject();
		if (null == selectedLANId) {
			jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.unknown"));
			return "json";
		} else {
			LanProfile lanProfile = QueryUtil.findBoById(LanProfile.class, selectedLANId, this);
			if (null == lanProfile) {
				jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.unknown"));
				return "json";
			} else {
				VpnNetwork network;
				
				// handle the Native(Untagged) Network
				if(untaggedNetworkId.isEmpty()) {
				    jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.config.vpnNetwork.noUntagged"));
				    return "json";
				} else {
					// support one Untagged Network currently
					for (Long untaggedItme : untaggedNetworkId) {
						network = QueryUtil.findBoById(VpnNetwork.class, untaggedItme, this);
						lanProfile.setNativeNetwork(network);
						break;
					}
				}
				
				// handle the Regular(Tagged) Network
				if(taggedNetworkIds.isEmpty()) {
					lanProfile.setRegularNetworks(new HashSet<VpnNetwork>());
				} else {
					Set<VpnNetwork> networkSet = lanProfile.getRegularNetworks();
					networkSet.clear();
					VpnNetwork nativeNetwork = lanProfile.getNativeNetwork();
					for (Long taggedItem : taggedNetworkIds) {
						if(null != nativeNetwork) {
							if(taggedItem.compareTo(nativeNetwork.getId()) == 0) {
								jsonObject.put("errorMsg", 
										MgrUtil.getUserMessage("error.config.vpnNetwork.sameDesignated", nativeNetwork.getNetworkName()));
								return "json";
							}
						}
						network = QueryUtil.findBoById(VpnNetwork.class, taggedItem, this);
						if(null != network) {
							networkSet.add(network);
						}
					}
				}
				
				try {
				    lanProfile.setNativeVlan(null);
				    lanProfile.setRegularVlans(new HashSet<Vlan>());
					updateBoWithEvent(lanProfile);
					jsonObject.put("succ", true);
				} catch (Exception e) {
					jsonObject.put("errorMsg", e.getMessage());
				}
			}
		}
		
		return "json";
	}

	/**
	 * TA1119: add a select dialog for Networks
	 * - show the dialog, and select values which are binded with the LAN profile
	 * @author Yunzhi Lin
	 * - Time: Aug 18, 2011 2:26:38 PM
	 * @return the specific dialog page
	 */
	private String displayNetworksDialog() {
		untaggedNetworkId.clear();
		taggedNetworkIds.clear();
		if(null == networkList) {
			networkList = new ArrayList<>();
		} else {
			networkList.clear();
		}
		
		LanProfile lanProfile = QueryUtil.findBoById(LanProfile.class, selectedLANId, this);
		if(null != lanProfile) {
			VpnNetwork untaggedNetwork = lanProfile.getNativeNetwork();
			if(null != untaggedNetwork) {
			    untaggedNetworkId.add(untaggedNetwork.getId());
			}
			Set<VpnNetwork> taggedNetworks = lanProfile.getRegularNetworks();
			if(!taggedNetworks.isEmpty()) {
				for (VpnNetwork network : taggedNetworks) {
					taggedNetworkIds.add(network.getId());
				}
			}
			if(null == untaggedNetwork && taggedNetworks.isEmpty()) {
			    noCorrelationNetworks = true;
			}
		}
		// get the Network list
//      List<VpnNetwork> list = QueryUtil.executeQuery(VpnNetwork.class, null, null,
//              getDomainId(), this);
//		for (VpnNetwork vpnNetwork : list) {
			// TODO for remove network object in user profile
//		    if(null != vpnNetwork.getVlan()) {
//		        String vlanName = vpnNetwork.getVlan().getVlanName();
//		        CheckItem item = new CheckItem(vpnNetwork.getId(), vpnNetwork.getNetworkName()
//		                + " (" + vlanName + ")");
//		        networkList.add(item);
//		        // set default native VLAN as the NetworkPolicy settings
//		        if (untaggedNetworkId.isEmpty() && StringUtils.isNotBlank(selectedVlanName)
//		                && StringUtils.isNotBlank(vlanName) && selectedVlanName.equals(vlanName)) {
//		            untaggedNetworkId.add(vpnNetwork.getId());
//		        }
//		    }
//		}
		
		return "selectNetworks4LAN";
	}
	
    private String displayVlansDialog() {
        untaggedVlanIds.clear();
        taggedVlanIds.clear();
        if(null == vlanList) {
            vlanList = new ArrayList<>();
        } else {
            vlanList.clear();
        }
        
        LanProfile lanProfile = QueryUtil.findBoById(LanProfile.class, selectedLANId, this);
        if(null != lanProfile) {
            Vlan untaggedVlan = lanProfile.getNativeVlan();
            if(null != untaggedVlan) {
                untaggedVlanIds.add(untaggedVlan.getId());
            }
            Set<Vlan> taggedVlans = lanProfile.getRegularVlans();
            if(!taggedVlans.isEmpty()) {
                for (Vlan vlan : taggedVlans) {
                    taggedVlanIds.add(vlan.getId());
                }
            }
            if(null == untaggedVlan && taggedVlans.isEmpty()) {
                noCorrelationNetworks = true;
            }
        }
        
        vlanList = getBoCheckItems("vlanName", Vlan.class, null);
        if(untaggedVlanIds.isEmpty() && StringUtils.isNotBlank(selectedVlanName)) {
            for (CheckItem item : vlanList) {
                if(item.getValue().equals(selectedVlanName)) {
                    untaggedVlanIds.add(item.getId());
                    break;
                }
            }
        }
        
        return "selectVlans4LAN";
    }
    
    private String selectVlansOperation() throws JSONException {
        log.debug("selectedLANId:"+selectedLANId);
        log.debug("untaggedVlanIds:"+untaggedVlanIds+" taggedVlanIds:"+taggedVlanIds);
        
        jsonObject = new JSONObject();
        if (null == selectedLANId) {
            jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.unknown"));
            return "json";
        } else {
            LanProfile lanProfile = QueryUtil.findBoById(LanProfile.class, selectedLANId, this);
            if (null == lanProfile) {
                jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.unknown"));
                return "json";
            } else {
                Vlan vlan;
                
                // handle the Native(Untagged) VLAN
                if(untaggedVlanIds.isEmpty()) {
                    jsonObject.put("errorMsg", MgrUtil.getUserMessage("error.config.vlan.noUntagged"));
                    return "json";
                } else {
                    // support one Untagged VLAN currently
                    for (Long untaggedItme : untaggedVlanIds) {
                        vlan = QueryUtil.findBoById(Vlan.class, untaggedItme, this);
                        lanProfile.setNativeVlan(vlan);
                        break;
                    }
                }
                
                // handle the Regular(Tagged) Vlan
                if(taggedVlanIds.isEmpty()) {
                    lanProfile.setRegularVlans(new HashSet<Vlan>());
                } else {
                    Set<Vlan> vlanSet = lanProfile.getRegularVlans();
                    vlanSet.clear();
                    Vlan nativeVlan = lanProfile.getNativeVlan();
                    for (Long taggedItem : taggedVlanIds) {
                        if(null != nativeVlan) {
                            if(taggedItem.compareTo(nativeVlan.getId()) == 0) {
                                jsonObject.put("errorMsg", 
                                        MgrUtil.getUserMessage("error.config.vlan.sameDesignated", nativeVlan.getVlanName()));
                                return "json";
                            }
                        }
                        vlan = QueryUtil.findBoById(Vlan.class, taggedItem, this);
                        if(null != vlan) {
                            vlanSet.add(vlan);
                        }
                    }
                }
                
                try {
                    lanProfile.setNativeNetwork(null);
                    lanProfile.setRegularNetworks(new HashSet<VpnNetwork>());
                	updateBoWithEvent(lanProfile);
                    jsonObject.put("succ", true);
                } catch (Exception e) {
                    jsonObject.put("errorMsg", e.getMessage());
                }
            }
        }
        
        return "json";
    }

	protected LanProfile setSelectedLocalUserGroups(LanProfile lanProfile) throws Exception {
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

	// ----------------Methods for JSP---------------------//
	private String interfacesMode;
	private List<CheckItem> ipDosParameterProfiles;
	private List<CheckItem> serviceFilterProfiles;
	// back from DoS
	private String selectDosType;
	
	private Long ipDos;
	private Long dosId;
	private Long serviceFilter;
	
	// Networks for LAN(use in dialog list)
	private Long selectedLANId;
	private String selectedVlanName; // associate with the Native VLAN in the 'Management Settings'
	private Set<Long> untaggedNetworkId = new HashSet<>();
	private Set<Long> taggedNetworkIds = new HashSet<>();
	private List<CheckItem> networkList = new ArrayList<>();
	private boolean isExistLANProfiles;
	private boolean noCorrelationNetworks;
	
	// VLANs for LAN(use in dialog list)
	private Set<Long> untaggedVlanIds = new HashSet<>();
	private Set<Long> taggedVlanIds = new HashSet<>();
	private List<CheckItem> vlanList = new ArrayList<>();
	
	// user for disable 802.1X in wireless mode
	private boolean wirelessMode;
	
	private void initDependenceObjects() throws Exception {
		if (null != getDataSource().getLanInterfacesMode()) {
			LanInterfacesMode mode = getDataSource().getLanInterfacesMode();
			StringBuilder builder = new StringBuilder();
			builder.append(mode.isEth1On());
			builder.append(SEPARATOR_COMMA);
			builder.append(mode.isEth2On());
			builder.append(SEPARATOR_COMMA);
			builder.append(mode.isEth3On());
			builder.append(SEPARATOR_COMMA);
			builder.append(mode.isEth4On());
			interfacesMode = builder.toString();
		}
		if (null != getDataSource().getIpDos()) {
			ipDos = getDataSource().getIpDos().getId();
		}
		if (null != getDataSource().getServiceFilter()) {
			serviceFilter = getDataSource().getServiceFilter().getId();
		}
		if (!getDataSource().isCwpSelectEnabled()) {
			getDataSource().setMacAuthEnabled(false);
		}
		//prepareDosParameterProfiles();
		prepareAvailableServiceFilters();
	}
	
	@SuppressWarnings("unused")
	private void prepareDosParameterProfiles() {
		ipDosParameterProfiles = getDosParameterProfiles(DosType.IP);
	}
	
	private void prepareAvailableServiceFilters() {
		serviceFilterProfiles = getBoCheckItems("filterName", ServiceFilter.class, null);
	}

	private List<CheckItem> getDosParameterProfiles(DosType dosType) {
		return getBoCheckItems("dosPreventionName", DosPrevention.class, new FilterParams(
				"dosType", dosType));
	}
	
	public EnumItem[] getEnumFilterAction() {
		return MacFilter.ENUM_FILTER_ACTION;
	}
	
	public EnumItem[] getEnumAuthSequence() {
		return LanProfile.ENUM_AUTH_SEQUENCE;
	}
	
	public EnumItem[] getEnumRadiusAuth() {
		return Cwp.ENUM_AUTH_METHOD;
	}
	
	public int getProfileNameLength() {
		return 32;
	}
	public int getProfileDescirptionLength() {
		return 64;
	}
	
	/**
	 * Fill in the list of dialog for selecting local user groups
	 * 
	 * Jianliang Chen
	 * 2012-03-31
	 * @return -
	 */
	public List<CheckItem> getLstLocalUserGroups() {
		return getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType=:s1", new Object[]{LocalUserGroup.USERGROUP_USERTYPE_RADIUS}));
	}
	// ----------------override methods---------------------//
	@Override
	public LanProfile getDataSource() {
		return (LanProfile) dataSource;
	}

	/**
	 * TA752: LAN profile column selection
	 */
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> tableColumns = new ArrayList<>(5);
		tableColumns.add(new HmTableColumn(COLUMN_NAME));
		tableColumns.add(new HmTableColumn(COLUMN_ACCESS_MODE));
		tableColumns.add(new HmTableColumn(COLUMN_CWP_ENABLE));
		tableColumns.add(new HmTableColumn(COLUMN_MAC_AUTH_ENABLE));
		tableColumns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return tableColumns;
	}

	/**
	 * TA752: LAN profile column selection
	 */
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
		case COLUMN_ACCESS_MODE:
			code = "config.dnsService.accessMode";
			break;
		case COLUMN_CWP_ENABLE:
			code = "config.dnsService.cwpEnable";
			break;
		case COLUMN_MAC_AUTH_ENABLE:
			code = "config.dnsService.macAuthEnable";
			break;
		}
		return null == code ? "" : MgrUtil.getUserMessage(code);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LAN);
		setDataSource(LanProfile.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_LAN_PROFILE;
	}
	
	public String getUpdateDisabled() {
		return getWriteDisabled();
	}
	
	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof LanProfile) {
			dataSource = bo;
			if(null != getDataSource().getUserProfileDefault()) {
				getDataSource().getUserProfileDefault().getId();
			}
			if(null != getDataSource().getUserProfileSelfReg()) {
				getDataSource().getUserProfileSelfReg().getId();
			}
			if(null != getDataSource().getRadiusUserProfile()) {
				getDataSource().getRadiusUserProfile().size();
			}
			if(null != getDataSource().getCwp()) {
				getDataSource().getCwp().getId();
			}
			if(null != getDataSource().getIpDos()) {
				getDataSource().getIpDos().getId();
			}
			if(null != getDataSource().getServiceFilter()) {
				getDataSource().getServiceFilter().getId();
			}
			if (null != getDataSource().getSchedulers()) {
				getDataSource().getSchedulers().size();
			}
			if(null != getDataSource().getRadiusAssignment()) {
				getDataSource().getRadiusAssignment().getId();
			}
			if(null != getDataSource().getNativeNetwork()) {
				getDataSource().getNativeNetwork().getId();
			}
			if(null != getDataSource().getRegularNetworks()) {
				getDataSource().getRegularNetworks().size();
			}
			if (getDataSource().getRadiusUserGroups() != null) {
				getDataSource().getRadiusUserGroups().size();
			}
			if(null != getDataSource().getNativeVlan()) {
			    getDataSource().getNativeVlan().getId();
			}
			if(null != getDataSource().getRegularVlans()) {
			    getDataSource().getRegularVlans().size();
			}
		}
		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
//		if (bo instanceof VpnNetwork) {
//			VpnNetwork network = (VpnNetwork) bo;
//			if(null != network.getVlan()) {
//				network.getVlan().getId();
//			}
//		}
		return null;
	}

	// ----------------Getter&Setter---------------------//
	public List<CheckItem> getIpDosParameterProfiles() {
		return ipDosParameterProfiles;
	}
	
	public void setIpDosParameterProfiles(List<CheckItem> ipDosParameterProfiles) {
		this.ipDosParameterProfiles = ipDosParameterProfiles;
	}
	
	public List<CheckItem> getServiceFilterProfiles() {
		return serviceFilterProfiles;
	}
	
	public void setServiceFilterProfiles(List<CheckItem> serviceFilterProfiles) {
		this.serviceFilterProfiles = serviceFilterProfiles;
	}

	public Long getIpDos() {
		return ipDos;
	}

	public void setIpDos(Long ipDos) {
		this.ipDos = ipDos;
	}

	public Long getDosId() {
		return dosId;
	}

	public void setDosId(Long dosId) {
		this.dosId = dosId;
	}

	public Long getServiceFilter() {
		return serviceFilter;
	}

	public void setServiceFilter(Long serviceFilter) {
		this.serviceFilter = serviceFilter;
	}

	public String getInterfacesMode() {
		return interfacesMode;
	}

	public void setInterfacesMode(String interfacesMode) {
		this.interfacesMode = interfacesMode;
	}

	public String getSelectDosType() {
		return selectDosType;
	}

	public void setSelectDosType(String selectDosType) {
		this.selectDosType = selectDosType;
	}

	public Set<Long> getUntaggedNetworkId() {
		return untaggedNetworkId;
	}

	public void setUntaggedNetworkId(Set<Long> untaggedNetworkId) {
		this.untaggedNetworkId = untaggedNetworkId;
	}

	public Set<Long> getTaggedNetworkIds() {
		return taggedNetworkIds;
	}

	public void setTaggedNetworkIds(Set<Long> taggedNetworkIds) {
		this.taggedNetworkIds = taggedNetworkIds;
	}

	public Long getSelectedLANId() {
		return selectedLANId;
	}

	public void setSelectedLANId(Long selectedLANId) {
		this.selectedLANId = selectedLANId;
	}

	public List<CheckItem> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(List<CheckItem> networkList) {
		this.networkList = networkList;
	}

	public boolean isExistLANProfiles() {
		return isExistLANProfiles;
	}

	public void setExistLANProfiles(boolean isExistLANProfiles) {
		this.isExistLANProfiles = isExistLANProfiles;
	}

    public String getSelectedVlanName() {
        return selectedVlanName;
    }

    public void setSelectedVlanName(String selectedVlanName) {
        this.selectedVlanName = selectedVlanName;
    }

    public boolean isNoCorrelationNetworks() {
        return noCorrelationNetworks;
    }

    public void setNoCorrelationNetworks(boolean noCorrelationNetworks) {
        this.noCorrelationNetworks = noCorrelationNetworks;
    }

	public List<Long> getRadiusUserGroupIds() {
		return radiusUserGroupIds;
	}

	public void setRadiusUserGroupIds(List<Long> radiusUserGroupIds) {
		this.radiusUserGroupIds = radiusUserGroupIds;
	}

    public List<CheckItem> getVlanList() {
        return vlanList;
    }

    public void setVlanList(List<CheckItem> vlanList) {
        this.vlanList = vlanList;
    }

    public Set<Long> getUntaggedVlanIds() {
        return untaggedVlanIds;
    }

    public Set<Long> getTaggedVlanIds() {
        return taggedVlanIds;
    }

    public void setUntaggedVlanIds(Set<Long> untaggedVlanIds) {
        this.untaggedVlanIds = untaggedVlanIds;
    }

    public void setTaggedVlanIds(Set<Long> taggedVlanIds) {
        this.taggedVlanIds = taggedVlanIds;
    }
    
    public boolean isWirelessMode() {
        return wirelessMode;
    }

    public void setWirelessMode(boolean wirelessMode) {
        this.wirelessMode = wirelessMode;
    }

}