/**
 *@filename		RadiusOnHiveApAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-10 AM 09:57:09
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.TreeNode;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.ActiveDirectoryTool;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.NetTool;
import com.ah.util.TextItem;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class RadiusOnHiveApAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private List<CheckItem> availableIpAddress;

	private List<CheckItem> availableActive;

	private List<CheckItem> availableLdap;
	
	private List<CheckItem> availableOpenDir;
	
	private List<CheckItem> availableUserProfile;

	private boolean local = false;
	
	private boolean externalDb = false;

	private boolean active = false;

	private boolean open = false;
	
	private boolean openDirect = false;

	private String radioGroupOrUser = "user";

	private String domainName;
	// for Router AAA server, hide the NAS settings
	private boolean enable4Router;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					("continue".equals(operation) ||
						"continueLocal".equals(operation) || 	// local user group
						"continueSip".equals(operation) || 		// sip IP address
						"continueUserProfile".equals(operation) || 		// sip IP address
						"continue1".equals(operation) || 		// sip IP address
						"continue2".equals(operation) || 		// sip IP address
						"continue3".equals(operation) ||
						"continueActive".equals(operation) ||
						"continueOpenDir".equals(operation) ||
						"continueLdap".equals(operation))) { // user profile
				restoreJsonContext();
			}
			
			setTabId(0); // active the first database tab as default
			domainName = CacheMgmt.getInstance().getCacheDomainById(domainId).getDomainName();
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.radiusService"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				// for Router AAA server, hide the NAS settings
				RadiusOnHiveap obj = new RadiusOnHiveap();
				obj.setServer4Router(enable4Router);
				
                setSessionDataSource(obj);
				prepareAvailableUserGroup();
				hideCreateItem = "";
				hideNewButton = "none";
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "radiusJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				setDatabaseAccessType();
				updateIps();
				updateActives();
				updateOpenDirs();
				updateLdaps();
				updateRoleMaps();
				if (isJsonMode()) {
					if (checkNameExists("radiusName", getDataSource().getRadiusName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getRadiusName()));
						return "json";
					}
					String retMsg = checkCAandLdapTlsAuth();
					if (!"".equals(retMsg)) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", retMsg);
						return "json";
					}
					setDatabaseValues();
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getRadiusName());
					try {
						goCreateUpdateBoForJson(operation);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if (checkNameExists("radiusName", getDataSource().getRadiusName()) || !"".equals(checkCAandLdapTlsAuth())) {
						prepareAvailableUserGroup();
						return INPUT;
					}
					setDatabaseValues();
					return goCreateBo(operation);
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (null != dataSource) {
					prepareAvailableUserGroup();
					addLstTitle(getText("config.title.radiusService.edit")
							+ " '" + getChangedName() + "'");
					// for Router AAA server, hide the NAS settings
					((RadiusOnHiveap)(dataSource)).setServer4Router(enable4Router);
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(strForward, "radiusJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if (dataSource != null) {
							setDatabaseAccessType();
							updateIps();
							updateActives();
							updateOpenDirs();
							updateLdaps();
							updateRoleMaps();
							String retMsg = checkCAandLdapTlsAuth();
							if (!"".equals(retMsg)) {
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", retMsg);
								return "json";
							}
							setDatabaseValues();
						}
						goCreateUpdateBoForJson(operation);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if (dataSource != null) {
						setDatabaseAccessType();
						updateIps();
						updateActives();
						updateOpenDirs();
						updateLdaps();
						updateRoleMaps();
						if (!"".equals(checkCAandLdapTlsAuth())) {
							prepareAvailableUserGroup();
							return INPUT;
						}
						setDatabaseValues();
					}
					return goUpdateBo(operation);
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadiusOnHiveap profile = (RadiusOnHiveap) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setRadiusName("");
				setCloneValues(profile, profile);
				profile.setOwner(null);
				setSessionDataSource(profile);
				prepareAvailableUserGroup();
				addLstTitle(getText("config.title.radiusService"));
				return INPUT;
			} else if ("addIpAddress".equals(operation)) {
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateIps();
					addSelectedIps();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("removeIpAddress".equals(operation)
					|| "removeIpAddressNone".equals(operation)) {
				hideCreateItem = "removeIpAddressNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeIpAddressNone".equals(operation) ? "none"
						: "";
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateIps();
					removeSelectedIps();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("addRoleMap".equals(operation)) {
				//add for ad configuration improvement
				setDatabaseAccessType();
				short type = 0;
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
					type = ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY;
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
					type = ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY;
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
					type = ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP;
				}
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateIps();
					updateRoleMaps();
					addRoleMaps(type);
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("removeRoleMapNone".equals(operation)) {
				//add for ad configuration improvement
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateIps();
					// Find rules to remove before reordering/updating
					List<LdapServerOuUserProfile> ouList= getRoleMaps();
					Collection<String> roleMapIndices = getRoleMapIndices();
					Collection<LdapServerOuUserProfile> removeList = findRoleMapsToRemove(ouList, roleMapIndices);
					updateRoleMaps();
					ouList.removeAll(removeList);
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("newIpAddress".equals(operation) || "newUserGroup".equals(operation) || "newLocalUserGroup".equals(operation)
				|| "editIpAddress".equals(operation) || "editUserGroup".equals(operation) || "newIpAddressSip".equals(operation)
				|| "editLocalUserGroup".equals(operation)|| "editIpAddressSip".equals(operation) || "newLibrarySip".equals(operation)
				|| "editLibrarySip".equals(operation) || "newUserProfile".equals(operation) || "editUserProfile".equals(operation)) {
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				updateIps();
				setLibrarySip(false);
				if ("newLocalUserGroup".equals(operation)) {
					operation = "newUserGroup";
				}
				if ("editLocalUserGroup".equals(operation)) {
					operation = "editUserGroup";
				}
				clearErrorsAndMessages();
				if ("newIpAddressSip".equals(operation) || "editIpAddressSip".equals(operation)) {
					addLstForward("radiusOnHiveApSip");
				} else {
					addLstForward("radiusOnHiveAp");
				}
				return operation;
			} else if ("addActive".equals(operation) || "addOpenDir".equals(operation) || "addLdap".equals(operation)) {
				setDatabaseAccessType();
				if ("addActive".equals(operation)) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				} else if ("addOpenDir".equals(operation)) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				} else {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				setHiveApAuthButtons();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					// add active directory profile
					if ("addActive".equals(operation)) {
						updateActives();
						addSelectedActive();
					// add open directory profile
					} else if ("addOpenDir".equals(operation)) {
						updateOpenDirs();
						addSelectedOpenDir();
					// add open LDAP profile
					} else {
						updateLdaps();
						addSelectedLdap();
					}
					prepareAvailableUserGroup();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("removeActive".equals(operation)
					|| "removeActiveNone".equals(operation)) {
				hideCreateActiveItem = "removeActiveNone".equals(operation) ? ""
						: "none";
				hideNewActiveButton = "removeActiveNone".equals(operation) ? "none"
						: "";
				setDatabaseAccessType();
				setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				setHiveApAuthButtons();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateActives();
					removeSelectedActive();
					prepareAvailableUserGroup();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("removeLdap".equals(operation)
				|| "removeLdapNone".equals(operation)) {
				hideCreateLdapItem = "removeLdapNone".equals(operation) ? ""
						: "none";
				hideNewLdapButton = "removeLdapNone".equals(operation) ? "none"
						: "";
				setDatabaseAccessType();
				setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				setHiveApAuthButtons();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateLdaps();
					removeSelectedLdap();
					prepareAvailableUserGroup();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("removeOpenDir".equals(operation)
				|| "removeOpenDirNone".equals(operation)) {
				hideCreateOpenDirItem = "removeOpenDirNone".equals(operation) ? ""
						: "none";
				hideNewOpenDirButton = "removeOpenDirNone".equals(operation) ? "none"
						: "";
				setDatabaseAccessType();
				setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				setHiveApAuthButtons();
				setLibrarySip(false);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateOpenDirs();
					removeSelectedOpenDir();
					prepareAvailableUserGroup();
					updateRoleMaps();
					return getReturnPathWithJsonMode(INPUT, "radiusJson");
				}
			} else if ("newActiveDirectory".equals(operation) || "newOpenLdap".equals(operation) || "newOpenDir".equals(operation) ||
					"editActiveDirectory".equals(operation) || "editOpenLdap".equals(operation) || "editOpenDir".equals(operation)) {
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
					addLstForward("radiusOnHiveApActive");
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
					addLstForward("radiusOnHiveApOpenDir");
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
					addLstForward("radiusOnHiveApOpenLdap");
				}
				updateActives();
				updateLdaps();
				updateOpenDirs();
				setLibrarySip(false);
				clearErrorsAndMessages();
				addLstTabId(tabId);
				return operation;
			} else if ("newFile1".equals(operation)
					|| "newFile2".equals(operation)
					|| "newFile3".equals(operation)) {
				setDatabaseAccessType();
				if (getActive()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
				}
				if (getOpenDirect()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
				}
				if (getOpen()) {
					setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
				}
				updateIps();
				clearErrorsAndMessages();
				setLibrarySip(false);
				addLstForward("radiusOnHiveAp" + operation.substring(7));
				addLstTabId(tabId);
				return "newFile";
			} else if ("continue1".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setCaCertFile(fileName);
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson"); 
			} else if ("continue2".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setServerFile(fileName);
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson"); 
			} else if ("continue3".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setKeyFile(fileName);
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson"); 
			} else if ("continue".equals(operation)) {
				hideCreateItem = "";
				hideNewButton = "none";
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson"); 
			} else if ("continueLdap".equals(operation)) {
				hideCreateLdapItem = "";
				hideNewLdapButton = "none";
				setHiveApAuthButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson");
			} else if ("continueActive".equals(operation)) {
				hideCreateActiveItem = "";
				hideNewActiveButton = "none";
				setHiveApAuthButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson");
			} else if ("continueOpenDir".equals(operation)) {
				hideCreateOpenDirItem = "";
				hideNewOpenDirButton = "none";
				setHiveApAuthButtons();
				prepareAvailableUserGroup();
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson");
			} else if ("continueLocal".equals(operation) || "continueUserProfile".equals(operation)) {
				// return from LocalUserGroupAction or UserProfilesAction
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				if ("continueLocal".equals(operation)) {
					// fix bug 32838
					setTabId(getLocalTabId());
				}
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson");
			} else if ("continueSip".equals(operation)) {
				// create IP for Sip
				setHiveApAuthButtons();
				setActiveOrOpenButtons();
				prepareAvailableUserGroup();
				setTabId(getSipTabId()); // fix bug 22221
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusJson");
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("loadNodeData".equals(operation)){
				selectedAp = getApForCommunication(selectedAp, serverType);
				jsonArray = ActiveDirectoryTool
						.expandOperation(jsonArray, jsonObject, serverId, dn,
								domainId, selectedDomainId,
								getDomainsForTree(serverType),
								getTreeInfos(serverType), selectedAp);
				return "json";
			} else if ("getNodeAttr".equals(operation)){
				selectedAp = getApForCommunication(selectedAp, serverType);
				jsonObject = ActiveDirectoryTool.getAttributeOperation(
						jsonObject, serverId, dn, groupAttributeName, domainId,
						selectedDomainId, getDomainsForTree(serverType), selectedAp);
				return "json";
			/*} else if ("refreshNode".equals(operation)){
				jsonObject = ActiveDirectoryTool
						.removeNodeFromTree(jsonObject, serverId, dn, domainId,
								selectedDomainId,
								getDomainsForTree(serverType),
								getTreeInfos(serverType), selectedAp);
				return "json";*/
			} else if ("getUserProfileAttr".equals(operation)){
				if (null == jsonObject) {jsonObject = new JSONObject();}
				if (userProfileId != null) {
					UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, userProfileId);
					if (userProfile != null) {
						jsonObject.put("attributeId", userProfile.getAttributeValue());
					}
				}
				return "json";
			} else if ("resetTree".equals(operation)){
				if (null == jsonObject) {jsonObject = new JSONObject();}
				setFirstNodeToTreeByDomainId(true, selectedDomainId,
						getDomainsForTree(serverType), getTreeInfos(serverType));
				jsonObject.put("expandedNodes", getExpandedNodes(serverType));
				return "json";
			} else {
				setUpdateContext(true);
				baseOperation();
				return prepareBoList();
			}
			
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	//add for ad configuration improvement
	private void doCreateLocalUserGroup(List<Long> ids) throws Exception {
		List<LdapServerOuUserProfile> roleMaps = getDataSource().getLdapOuUserProfiles();
		if(roleMaps != null) {
			Long id;
			for (LdapServerOuUserProfile ldapOuUserProfile : roleMaps) {
				LocalUserGroup localUserGroup = ldapOuUserProfile.getLocalUserGroup();
				if (localUserGroup != null) {
					// update local user group when user profile of each role maps was reselected
					localUserGroup.setUserProfileId(ldapOuUserProfile.getUserProfileAttribute());
					
					if (localUserGroup.getId() == null) {
						id = QueryUtil.createBo(localUserGroup);
						ldapOuUserProfile.setLocalUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, id));
						ids.add(id);
					} else {
						QueryUtil.updateBo(localUserGroup);
					}
				}
			}
		}
	}
	
	//add for ad configuration improvement
	private String goCreateBo(String operation) throws Exception {
		List<Long> ids = new ArrayList<Long>();
		try {
			doCreateLocalUserGroup(ids);
			if ("create".equals(operation)) {
				return createBo();
			} else {
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			}
		} catch (Exception e) {
			// remove all LocalUserGroups which create above if error occurs 
			QueryUtil.removeBos(LocalUserGroup.class, ids);
			throw e;
		}
	}
	
	//add for ad configuration improvement
	private String goUpdateBo(String operation) throws Exception {
		List<Long> ids = new ArrayList<Long>();
		try {
			doCreateLocalUserGroup(ids);
			if ("update".equals(operation)) {
				return updateBo();
			} else {
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			}
		} catch (Exception e) {
			// remove all LocalUserGroups which create above if error occurs 
			QueryUtil.removeBos(LocalUserGroup.class, ids);
			throw e;
		}
	}
	
	//add for JSON mode
	private void goCreateUpdateBoForJson(String operation) throws Exception {
		List<Long> ids = new ArrayList<Long>();
		try {
			doCreateLocalUserGroup(ids);
			if ("create".equals(operation)) {
				id = createBo(dataSource);
			} else {
				updateBo(dataSource);
			}
		} catch (Exception e) {
			// remove all LocalUserGroups which create above if error occurs 
			QueryUtil.removeBos(LocalUserGroup.class, ids);
			throw e;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIUS_SERVER_HIVEAP);
		setDataSource(RadiusOnHiveap.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_HIVEAP_AAA;
	}

	@Override
	public RadiusOnHiveap getDataSource() {
		return (RadiusOnHiveap) dataSource;
	}
	
	private void setCloneValues(RadiusOnHiveap source, RadiusOnHiveap dest) {
		Set<LocalUserGroup> localGroup = new HashSet<LocalUserGroup>();
		localGroup.addAll(source.getLocalUserGroup());
		dest.setLocalUserGroup(localGroup);
		List<RadiusHiveapAuth> newserver = new ArrayList<RadiusHiveapAuth>();
		newserver.addAll(source.getIpOrNames());
		dest.setIpOrNames(newserver);
		List<ActiveDirectoryOrLdapInfo> directory = new ArrayList<ActiveDirectoryOrLdapInfo>();
		directory.addAll(source.getDirectory());
		dest.setDirectory(directory);
		List<ActiveDirectoryOrLdapInfo> ldap = new ArrayList<ActiveDirectoryOrLdapInfo>();
		ldap.addAll(source.getLdap());
		dest.setLdap(ldap);
		List<ActiveDirectoryOrLdapInfo> openD = new ArrayList<ActiveDirectoryOrLdapInfo>();
		openD.addAll(source.getOpenDir());
		dest.setOpenDir(openD);
		
		//add for ad configuration improvement
		List<LdapServerOuUserProfile> dirOu = new ArrayList<LdapServerOuUserProfile>();
		dirOu.addAll(source.getDirectoryOu());
		dest.setDirectoryOu(dirOu);
		List<LdapServerOuUserProfile> ldapOu = new ArrayList<LdapServerOuUserProfile>();
		ldapOu.addAll(source.getLdapOu());
		dest.setLdapOu(ldapOu);
		List<LdapServerOuUserProfile> openDirOu = new ArrayList<LdapServerOuUserProfile>();
		openDirOu.addAll(source.getOpenDirOu());
		dest.setOpenDirOu(openDirOu);

		// fix bug 25759
		List<ActiveDirectoryOrLdapInfo> directoryOrLdap = new ArrayList<ActiveDirectoryOrLdapInfo>();
		directoryOrLdap.addAll(source.getDirectoryOrLdap());
		dest.setDirectoryOrLdap(directoryOrLdap);
		List<LdapServerOuUserProfile> ldapOuUserProfiles = new ArrayList<LdapServerOuUserProfile>();
		ldapOuUserProfiles.addAll(source.getLdapOuUserProfiles());
		dest.setLdapOuUserProfiles(ldapOuUserProfiles);
	}

	private String setContinueValue() throws Exception {
		if (getUpdateContext()) {
			removeLstTitle();
			removeLstForward();
			setUpdateContext(false);
		}
		if (dataSource == null) {
			return prepareBoList();
		} else {
			setId(dataSource.getId());
			return INPUT;
		}
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadiusOnHiveap source = QueryUtil.findBoById(RadiusOnHiveap.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadiusOnHiveap> list = QueryUtil.executeQuery(RadiusOnHiveap.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (RadiusOnHiveap profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			RadiusOnHiveap up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setRadiusName(profile.getRadiusName());
			up.setOwner(profile.getOwner());
			setCloneValues(source, up);
			hmBos.add(up);
		}
		return hmBos;
	}

	public int getRadiusNameLength() {
		return getAttributeLength("radiusName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getRadiusName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getPassLength() {
		return getAttributeLength("keyPassword");
	}

	public String getHideMap() {
		return getDataSource().getMapEnable() ? "" : "none";
	}

	public String getHideLocalUserGroup() {
		return RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI == getDataSource().getMapByGroupOrUser()  ? "" : "none";
	}

	public String getHideMapUserInfor() {
		return RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI == getDataSource().getMapByGroupOrUser()  ? "" : "none";
	}

	public String getHideCache() {
		return (getOpen() || getActive() || getOpenDirect()) ? "" : "none";
	}
	
	public String getLocalUserStyle(){
		return getLocal() ? "" : "none";
	}

	public boolean getTimeDis() {
		return !getDataSource().getCacheEnable();
	}

	public int getGridCount() {
		return getDataSource().getIpOrNames().size() == 0 ? 3 : 0;
	}

	public int getGridLdapCount() {//one grid always show, so change number to 2
		return getDataSource().getLdap().size() == 0 ? 2 : 0;
	}

	public int getGridActiveCount() {//one grid always show, so change number to 2
		return getDataSource().getDirectory().size() == 0 ? 2 : 0;
	}
	
	public int getGridOpenDirCount() {//one grid always show, so change number to 2
		return getDataSource().getOpenDir().size() == 0 ? 2 : 0;
	}

	public int getGridDirOuCount() {
		return getDataSource().getDirectoryOu().size() == 0 ? 3 : 0;
	}

	public int getGridLdapOuCount() {
		return getDataSource().getLdapOu().size() == 0 ? 3 : 0;
	}

	public int getGridOpenDirOuCount() {
		return getDataSource().getOpenDirOu().size() == 0 ? 3 : 0;
	}

	public EnumItem[] getAuthType() {
		return RadiusOnHiveap.RADIUS_AUTH_TYPE;
	}
	
	public EnumItem[] getAuthTypeDefault() {
		if(null != getDataSource()){
			switch (getDataSource().getAuthType()) {
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL_ADDED_MD5:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_0;
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_1;
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_PEAP:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_2;
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_TTLS:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_3;
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_TLS:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_4;
				case RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_5;
				default:
					return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_0;
			}
		}
		return RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_ALL_0;
	}

	public EnumItem[] getEnumPriority() {
		return RadiusServer.ENUM_RADIUS_PRIORITY;
	}
	
	public List<CheckItem> getLibrarySipPolicies() {
		return getBoCheckItems("policyName", RadiusLibrarySip.class, null);
	}

	private void setHiveApAuthButtons() {
		if (getDataSource().getIpOrNames().size() == 0) {
			hideCreateItem = "";
			hideNewButton = "none";
		}
	}

	private void setActiveOrOpenButtons() {
		if (getActive() && getDataSource().getDirectory().size() == 0) {
			hideCreateActiveItem = "";
			hideNewActiveButton = "none";
		} else if (getOpen() && getDataSource().getLdap().size() == 0) {
			hideCreateLdapItem = "";
			hideNewLdapButton = "none";
		} else if (getOpenDirect() && getDataSource().getOpenDir().size() == 0) {
			hideCreateOpenDirItem = "";
			hideNewOpenDirButton = "none";
		}
	}

	protected void addSelectedIps() throws Exception {
		RadiusHiveapAuth ipAuth = new RadiusHiveapAuth();
		IpAddress ipClass = null;
		// select the exist ip object
		if (ipAddress != null && ipAddress != -1) {
			ipClass = findBoById(IpAddress.class,
					ipAddress);
		// create new ip object by input value
		} else {
			for (RadiusHiveapAuth existSer : getDataSource().getIpOrNames()) {
				if (existSer.getIpAddress().getAddressName().equalsIgnoreCase(inputIpValue)) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", "IP Address / Host Name"));
					hideCreateItem = "";
					hideNewButton = "none";
					return;
				}
			}
			SingleTableItem ipObj = NetTool.getIpObjectByInput(inputIpValue, false);
			if (null != ipObj) {
				ipClass = CreateObjectAuto.createNewIP(ipObj.getIpAddress(), ipObj.getType(), getDomain(), "For NAS of "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting", ipObj.getNetmask());
			}
		}
		ipAuth.setIpAddress(ipClass);
		
		// refresh the ip address list
		for (CheckItem singleIp : getAvailableIpAddress()) {
			if (ipClass.getId().equals(singleIp.getId())) {
				availableIpAddress.remove(singleIp);
				break;
			}
		}
		ipAuth.setSharedKey(sharekey);
		ipAuth.setDescription(description);
		getDataSource().getIpOrNames().add(ipAuth);
		inputIpValue = "";
		sharekey = "";
		description = "";
	}

	protected void addSelectedActive() throws Exception {
		ActiveDirectoryOrLdapInfo activedir = new ActiveDirectoryOrLdapInfo();
		if (activeDir != null && activeDir != -1) {
			ActiveDirectoryOrOpenLdap directory = findBoById(ActiveDirectoryOrOpenLdap.class,
				activeDir, this);
			activedir.setDirectoryOrLdap(directory);
			activedir.setServerPriority(activePriority);
			getDataSource().getDirectory().add(activedir);
			
			for (CheckItem singleIp : getAvailableDirectory()) {
				if (activeDir.equals(singleIp.getId())) {
					availableActive.remove(singleIp);
					break;
				}
			}
		}
	}
	
	protected void addSelectedOpenDir() throws Exception {
		ActiveDirectoryOrLdapInfo opendir = new ActiveDirectoryOrLdapInfo();
		if (openDirectory != null && openDirectory != -1) {
			ActiveDirectoryOrOpenLdap directory = findBoById(ActiveDirectoryOrOpenLdap.class,
				openDirectory, this);
			opendir.setDirectoryOrLdap(directory);
			for (CheckItem singleIp : getAvailableOpenDirectory()) {
				if (openDirectory.equals(singleIp.getId())) {
					availableOpenDir.remove(singleIp);
					break;
				}
			}
		}
		opendir.setServerPriority(openDirPriority);
		getDataSource().getOpenDir().add(opendir);
	}

	protected void addSelectedLdap() throws Exception {
		ActiveDirectoryOrLdapInfo activedir = new ActiveDirectoryOrLdapInfo();
		if (openLdap != null && openLdap != -1) {
			ActiveDirectoryOrOpenLdap directory = findBoById(ActiveDirectoryOrOpenLdap.class,
				openLdap, this);
			activedir.setDirectoryOrLdap(directory);
			for (CheckItem singleIp : getAvailableLdap()) {
				if (openLdap.equals(singleIp.getId())) {
					availableLdap.remove(singleIp);
					break;
				}
			}
		}
		activedir.setServerPriority(ldapPriority);
		getDataSource().getLdap().add(activedir);
	}

	private void setDatabaseAccessType() throws Exception {
		short type = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_NONE;
		if (local) {
			type = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL;
			setSelectedLocalUserGroup();
			if (externalDb) {
				if (getDataSource().getExternalDbType() == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE) {
					type = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE;
				}
				if (getDataSource().getExternalDbType() == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN) {
					type = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN;
				}
				if (getDataSource().getExternalDbType() == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT) {
					type = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT;
				}
			}
		} else {
			getDataSource().setLocalUserGroup(null);
			if (externalDb) {
				type = getDataSource().getExternalDbType();				
			}
		}
		getDataSource().setDatabaseType(type);
	}

	private String checkCAandLdapTlsAuth() {
		String msg;
		// check if the certificate file valid
		if (RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP != getDataSource().getAuthType()) {
			try {
				String keyPass = "".equals(getDataSource().getKeyPassword()) ? null : getDataSource().getKeyPassword();
				if (!HmBeAdminUtil.verifyCertificate(getDataSource().getCaCertFile(), getDataSource().getServerFile(),
					getDataSource().getKeyFile(), keyPass, domainName, getDataSource().isCheckCA())) {
					msg = MgrUtil.getUserMessage("error.radius.checkCertificateFile");
					addActionError(msg);
//					setTabId(0);
					getDataSource().setRadiusSettingsStyle("");
					getDataSource().setDatabaseAccessStyle("");
					return msg;
				}
			} catch (BeOperateException boe) {
				msg = boe.getMessage();
				addActionError(msg);
//				setTabId(0);
				return msg;
			}
		}
		// check if LDAP server use TLS authentication when use eDirectory
		if (getOpen() && getDataSource().isUseEdirect()) {
			for (ActiveDirectoryOrLdapInfo info : getDataSource().getLdap()) {
				if (!info.getDirectoryOrLdap().isAuthTlsEnable()) {
					msg = MgrUtil.getUserMessage("error.radius.useEdirectory.mustTLS", "eDirectory");
					addActionError(msg);
//					setTabId(1);
					getDataSource().setRadiusSettingsStyle("");
					getDataSource().setDatabaseAccessStyle("");
					return msg;
				}
			}
		}

		// check if AD server contains the primary one
		if (getActive()) {
			boolean bool = false;
			for (ActiveDirectoryOrLdapInfo info : getDataSource().getDirectory()) {
				if (RadiusServer.RADIUS_PRIORITY_PRIMARY == info.getServerPriority()) {
					bool = true;
					break;
				}
			}
			if (!bool) {
				msg = MgrUtil.getUserMessage("error.requiredField", "The primary Active Directory Setting");
				addActionError(msg);
//				setTabId(1);
				getDataSource().setRadiusSettingsStyle("");
				getDataSource().setDatabaseAccessStyle("");
				return msg;
			}
		}
		return "";
	}

	private void setDatabaseValues() throws Exception {
		if (RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP == getDataSource().getAuthType()) {
			getDataSource().setCnEnable(false);
			getDataSource().setDbEnable(false);
			getDataSource().setCaCertFile(BeAdminCentOSTools.AH_NMS_DEFAULT_CA);
			getDataSource().setServerFile(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT);
			getDataSource().setKeyFile(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY);
			getDataSource().setKeyPassword("");
		}
		if (externalDb && (getActive() || getOpen() || getOpenDirect())) {
			if (!getDataSource().getCacheEnable())
				getDataSource().setCacheTime(86400);
		} else {
			getDataSource().setDirectoryOrLdap(null);
			getDataSource().setCacheEnable(false);
			getDataSource().setCacheTime(86400);
			getDataSource().setLocalInterval(300);
			getDataSource().setRetryInterval(600);
			getDataSource().setRemoteInterval(30);
			getDataSource().setMapEnable(false);
			setMapValue();
		}
		if (externalDb && getActive()) {
			getDataSource().setDirectoryOrLdap(getDataSource().getDirectory());
			/*
			 *  this method must put before 'setMapOpenOrActiveValue', 
			 *  otherwise table 'RADIUS_HIVEAP_LDAP_USER_PROFILE' data can not be cleared 
			 *  if LDAP attribute mapping be changed from enable to disable. 
			 *  or mapping type changed from 'map by group attribute' to 'map by user attribute'
			 */
			getDataSource().setLdapOuUserProfiles(getDataSource().getDirectoryOu());
			setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
		} else {
			getDataSource().setGlobalCatalog(false);
		}
		if (externalDb && getOpenDirect()) {
			getDataSource().setDirectoryOrLdap(getDataSource().getOpenDir());
			/*
			 *  this method must put before 'setMapOpenOrActiveValue', 
			 *  otherwise table 'RADIUS_HIVEAP_LDAP_USER_PROFILE' data can not be cleared 
			 *  if LDAP attribute mapping be changed from enable to disable. 
			 *  or mapping type changed from 'map by group attribute' to 'map by user attribute'
			 */
			getDataSource().setLdapOuUserProfiles(getDataSource().getOpenDirOu());
			setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
		}
		if (externalDb && getOpen()) {
			getDataSource().setDirectoryOrLdap(getDataSource().getLdap());
			/*
			 *  this method must put before 'setMapOpenOrActiveValue', 
			 *  otherwise table 'RADIUS_HIVEAP_LDAP_USER_PROFILE' data can not be cleared 
			 *  if LDAP attribute mapping be changed from enable to disable. 
			 *  or mapping type changed from 'map by group attribute' to 'map by user attribute'
			 */
			getDataSource().setLdapOuUserProfiles(getDataSource().getLdapOu());
			setMapOpenOrActiveValue(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
		} else {
			getDataSource().setUseEdirect(false);
			getDataSource().setAccPolicy(false);
		}
		setLibrarySip(true);
	}
	
	private void setLibrarySip(boolean setBlank) {
		// library sip
		if (getDataSource().isLibrarySipCheck()) {
			IpAddress ipClass;
			// select the exist ip object
			if (sipServerId != null && sipServerId != -1) {
				ipClass = AhRestoreNewTools.CreateBoWithId(IpAddress.class, sipServerId);
			// create new ip object by input value
			} else {
				short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputSipServer) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
				ipClass = CreateObjectAuto.createNewIP(inputSipServer, ipType, getDomain(), "For Library SIP server of "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting");
			}
			// sip server
			getDataSource().setSipServer(ipClass);
			
			// sip policy
			if (null != librarySipId && librarySipId > -1) {
				getDataSource().setSipPolicy(AhRestoreNewTools.CreateBoWithId(RadiusLibrarySip.class, librarySipId));
			}
		} else if (setBlank) {
			getDataSource().setSipServer(null);
			getDataSource().setSipPort(6001);
			getDataSource().setLoginEnable(false);
			getDataSource().setLoginUser("");
			getDataSource().setLoginPwd("");
			getDataSource().setInstitutionId("");
			getDataSource().setSeparator("|");
			getDataSource().setSipPolicy(null);
		}
	}

	private void setMapValue()
	{
		getDataSource().setMapByGroupOrUser(RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI);
		getDataSource().setReauthTime("");
		getDataSource().setUserProfileId("");
		getDataSource().setVlanId("");
		getDataSource().setGroupAttribute("");
		getDataSource().setLdapOuUserProfiles(new ArrayList<LdapServerOuUserProfile>());
	}

	private void setMapOpenOrActiveValue(short checkType) throws Exception
	{
		if (getDataSource().getMapEnable()) {
			if (radioGroupOrUser.equals("group")) {
				boolean isActive = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE == checkType;
				getDataSource().setMapByGroupOrUser(RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI);
				getDataSource().setReauthTime(!isActive ? "radiusServiceType" : "msRADIUSServiceType");
				getDataSource().setUserProfileId(!isActive ? "radiusCallbackNumber" : "msRADIUSCallbackNumber");
				getDataSource().setVlanId(!isActive ? "radiusCallbackID" : "msRASSavedCallbackNumber");
			} else {
				String groupAtr = "memberOf";
				if (RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN == checkType) {
					groupAtr = "radiusGroupName";
				} else if (RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE == checkType) {
					groupAtr = "memberOf";
				} else if (RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT == checkType) {
					groupAtr = "apple-group-realname";
				}
				getDataSource().setMapByGroupOrUser(RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI);
				getDataSource().setGroupAttribute(groupAtr);
				getDataSource().setLdapOuUserProfiles(new ArrayList<LdapServerOuUserProfile>());
			}
		} else {
			setMapValue();
		}
	}

	protected void removeSelectedIps() {
		if (ipIndices != null) {
			Collection<RadiusHiveapAuth> removeList = new Vector<RadiusHiveapAuth>();
			for (String serviceIndex : ipIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getIpOrNames().size()) {
						RadiusHiveapAuth singleAuth = getDataSource()
								.getIpOrNames().get(index);
						removeList.add(singleAuth);
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getIpOrNames().removeAll(removeList);
		}
	}

	protected void removeSelectedActive() {
		if (activeIndices != null && getActive()) {
			Collection<ActiveDirectoryOrLdapInfo> removeList = new Vector<ActiveDirectoryOrLdapInfo>();
			// for remove roleMaps
			List<Long> removeServerIds = new ArrayList<Long>();
			for (String serviceIndex : activeIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getDirectory().size()) {
						ActiveDirectoryOrLdapInfo singleAuth = getDataSource()
								.getDirectory().get(index);
						removeList.add(singleAuth);
						removeServerIds.add(singleAuth.getDirectoryOrLdap().getId());
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getDirectory().removeAll(removeList);
			removeRoleMapsAndTreeNodesAfterServerDeleted(removeServerIds, getDataSource()
					.getDirectoryOu(), getDataSource().getDirTreeInfos());
		}
	}

	protected void removeSelectedLdap() {
		if (ldapIndices != null) {
			Collection<ActiveDirectoryOrLdapInfo> removeList = new Vector<ActiveDirectoryOrLdapInfo>();
			// for remove roleMaps
			List<Long> removeServerIds = new ArrayList<Long>();
			for (String serviceIndex : ldapIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getLdap().size()) {
						ActiveDirectoryOrLdapInfo singleAuth = getDataSource()
								.getLdap().get(index);
						removeList.add(singleAuth);
						removeServerIds.add(singleAuth.getDirectoryOrLdap().getId());
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getLdap().removeAll(removeList);
			removeRoleMapsAndTreeNodesAfterServerDeleted(removeServerIds, getDataSource()
					.getLdapOu(), getDataSource().getLdapTreeInfos());
		}
	}
	
	protected void removeSelectedOpenDir() {
		if (openDirIndices != null) {
			Collection<ActiveDirectoryOrLdapInfo> removeList = new Vector<ActiveDirectoryOrLdapInfo>();
			// for remove roleMaps
			List<Long> removeServerIds = new ArrayList<Long>();
			for (String serviceIndex : openDirIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getOpenDir().size()) {
						ActiveDirectoryOrLdapInfo singleAuth = getDataSource()
								.getOpenDir().get(index);
						removeList.add(singleAuth);
						removeServerIds.add(singleAuth.getDirectoryOrLdap().getId());
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getOpenDir().removeAll(removeList);
			removeRoleMapsAndTreeNodesAfterServerDeleted(removeServerIds, getDataSource()
					.getOpenDirOu(), getDataSource().getOpenDirTreeInfos());
		}
	}

	protected void updateIps() {
		if (sharedSecrets != null) {
			for (int i = 0; i < sharedSecrets.length
					&& i < getDataSource().getIpOrNames().size(); i++) {
				getDataSource().getIpOrNames().get(i).setSharedKey(
					sharedSecrets[i]);
			}
		}
		if (descriptions != null) {
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getIpOrNames().size(); i++) {
				getDataSource().getIpOrNames().get(i).setDescription(
						descriptions[i]);
			}
		}
	}

	protected void updateActives() {
		if (activePriorities != null && getActive()) {
			for (int i = 0; i < activePriorities.length
					&& i < getDataSource().getDirectory().size(); i++) {
				getDataSource().getDirectory().get(i).setServerPriority(
					activePriorities[i]);
			}
		}
	}
	
	protected void updateOpenDirs() {
		if (openDirPriorities != null && getOpenDirect()) {
			for (int i = 0; i < openDirPriorities.length
					&& i < getDataSource().getOpenDir().size(); i++) {
				getDataSource().getOpenDir().get(i).setServerPriority(
					openDirPriorities[i]);
			}
		}
	}

	protected void updateLdaps() {
		if (ldapPriorities != null && getOpen()) {
			for (int i = 0; i < ldapPriorities.length
					&& i < getDataSource().getLdap().size(); i++) {
				getDataSource().getLdap().get(i).setServerPriority(
					ldapPriorities[i]);
			}
		}
	}
	
	/**
	 * bug 25218 fix.
	 * @return
	 */
	private List<CheckItem> getIpObjectsNotRangeAndWildCard() {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("(typeFlag != :s1 and typeFlag != :s2 and typeFlag != :s3) ",
						new Object[] { IpAddress.TYPE_IP_WILDCARD,IpAddress.TYPE_IP_RANGE,IpAddress.TYPE_WEB_PAGE}),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getAvailableIpAddress() {
		if (null == availableIpAddress) {
			availableIpAddress = getIpObjectsNotRangeAndWildCard();
			for (RadiusHiveapAuth oneIp : getDataSource().getIpOrNames()) {
					availableIpAddress.remove(new CheckItem(oneIp
							.getIpAddress().getId(), oneIp.getIpAddress()
							.getAddressName()));
				}
			}
		if (availableIpAddress.size() == 0) {
			availableIpAddress.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return availableIpAddress;
	}
	
	public List<CheckItem> getAvailableSipServers() {
		return getIpObjectsByIpAndName();
	}

	public List<CheckItem> getDirectoryOrLdapList(List<CheckItem> arg_List,
		FilterParams arg_Filter, List<ActiveDirectoryOrLdapInfo> arg_Ldap) {
		if(null == arg_List) {
			arg_List = getBoCheckItems("name", ActiveDirectoryOrOpenLdap.class, arg_Filter);
			for (ActiveDirectoryOrLdapInfo oneIp : arg_Ldap) {
				arg_List.remove(new CheckItem(oneIp
					.getDirectoryOrLdap().getId(), oneIp.getDirectoryOrLdap()
					.getName()));
			}
		}

		if (arg_List.size() == 0) {
			arg_List.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return arg_List;
	}

	public List<CheckItem> getAvailableDirectory() {
		FilterParams filter = new FilterParams("typeFlag", ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
		
/*		// once first AD server was selected,only AD server configured for same HiveAP as first selected AD will show in AD server drop down list.
		List<ActiveDirectoryOrLdapInfo> arg_Ldap = getDataSource().getDirectory();
		if (arg_Ldap != null && !arg_Ldap.isEmpty()) {
			filter = new FilterParams("typeFlag = :s1 AND apMac = :s2 ", 
					new Object[]{ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY, arg_Ldap.get(0).getDirectoryOrLdap().getApMac()});
		}*/
		availableActive = getDirectoryOrLdapList(availableActive, filter, getDataSource().getDirectory());
		return availableActive;
	}

	public List<CheckItem> getAvailableLdap() {
		availableLdap = getDirectoryOrLdapList(availableLdap, new FilterParams("typeFlag",
			ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP), getDataSource().getLdap());
		return availableLdap;
	}
	
	public List<CheckItem> getAvailableOpenDirectory() {
		availableOpenDir = getDirectoryOrLdapList(availableOpenDir, new FilterParams("typeFlag",
			ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY), getDataSource().getOpenDir());
		return availableOpenDir;
	}

	public List<String> getAvailableCaFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(domainName);
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		return listFile;
	}

	private Long ipAddress;
	
	private String inputIpValue = "";

	private String sharekey;

	private String description;
	
	private String[] sharedSecrets;

	private String[] descriptions;

	private Collection<String> ipIndices;

	public boolean getLocal() {
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL)
			local = true;
		return local;
	}

	public Long getIpAddress() {
		return ipAddress;
	}

	public boolean getExternalDb() {
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT)
			externalDb = true;
		return externalDb;
	}
	
	public int getLocalTabId () {
		int tabId = 0;
		if (getExternalDb()) {
			tabId ++;
		}
		return tabId;
	}
	
	public int getSipTabId () {
		int tabId = 0;
		if (getExternalDb()) {
			tabId ++;
		}
		if (getLocal()) {
			tabId ++;
		}
		return tabId;
	}

	public boolean getActive() {
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE)
			active = true;
		return active;
	}

	public boolean getOpen() {
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN)
			open = true;
		return open;
	}

	public void setIpIndices(Collection<String> ipIndices) {
		this.ipIndices = ipIndices;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setSharekey(String sharekey) {
		this.sharekey = sharekey;
	}

	public void setIpAddress(Long ipAddress) {
		this.ipAddress = ipAddress;
	}

	private String fileName = "";

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	private String hideCreateLdapItem = "none";

	public String getHideCreateLdapItem() {
		return hideCreateLdapItem;
	}

	private String hideNewLdapButton = "";

	public String getHideNewLdapButton() {
		return hideNewLdapButton;
	}

	private String hideCreateActiveItem = "none";

	public String getHideCreateActiveItem() {
		return hideCreateActiveItem;
	}

	private String hideNewActiveButton = "";

	public String getHideNewActiveButton() {
		return hideNewActiveButton;
	}
	
	private String hideCreateOpenDirItem = "none";

	public String getHideCreateOpenDirItem() {
		return hideCreateOpenDirItem;
	}

	private String hideNewOpenDirButton = "";

	public String getHideNewOpenDirButton() {
		return hideNewOpenDirButton;
	}
	
	public String getShowCertFile() {
		return RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP == getDataSource().getAuthType() ? "none" : "";
	}
	
	public boolean getDisablePeapCheck() {
		return RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL != getDataSource().getAuthType() && RadiusOnHiveap.RADIUS_AUTH_TYPE_PEAP 
				!= getDataSource().getAuthType();
	}
	
	public boolean getDisableTtlsCheck() {
		return RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL != getDataSource().getAuthType() && RadiusOnHiveap.RADIUS_AUTH_TYPE_TTLS 
				!= getDataSource().getAuthType();
	}

	private Long activeDir;

	public void setActiveDir(Long activeDir)
	{
		this.activeDir = activeDir;
	}

	private Long openLdap;
	
	private Long openDirectory;

	private short ldapPriority;

	private short activePriority;
	
	private short openDirPriority;

	private short[] ldapPriorities;

	private Collection<String> ldapIndices;

	private short[] activePriorities;

	private Collection<String> activeIndices;
	
	private short[] openDirPriorities;

	private Collection<String> openDirIndices;

	public void setOpenLdap(Long openLdap)
	{
		this.openLdap = openLdap;
	}

	public void setLdapPriority(short ldapPriority)
	{
		this.ldapPriority = ldapPriority;
	}

	public void setLdapPriorities(short[] ldapPriorities)
	{
		this.ldapPriorities = ldapPriorities;
	}

	public void setLdapIndices(Collection<String> ldapIndices)
	{
		this.ldapIndices = ldapIndices;
	}

	public Long getActiveDir()
	{
		return activeDir;
	}

	public Long getOpenLdap()
	{
		return openLdap;
	}

	public void setActivePriorities(short[] activePriorities)
	{
		this.activePriorities = activePriorities;
	}

	public void setActiveIndices(Collection<String> activeIndices)
	{
		this.activeIndices = activeIndices;
	}

	public void setActivePriority(short activePriority)
	{
		this.activePriority = activePriority;
	}

	public String getRadioGroupOrUser()
	{
		radioGroupOrUser = getDataSource().getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI ? "group"
			: "user";
		return radioGroupOrUser;
	}

	public void setRadioGroupOrUser(String radioGroupOrUser)
	{
		this.radioGroupOrUser = radioGroupOrUser;
	}

//	public String getImportDisabled() throws Exception {
//		if (domainId != null) {
//			HmBo domain = findBoById(HmDomain.class, domainId);
//			if (domain != null) {
//				if (!HmDomain.HOME_DOMAIN.equals(((HmDomain)domain).getDomainName())) {
//					return "disabled";
//				}
//			}
//		}
//		return "";
//	}

	protected OptionsTransfer localUserGroupOptions;
	
	protected List<Long> selectLocalUserGroup;

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusOnHiveap) {
			RadiusOnHiveap radius = (RadiusOnHiveap) bo;
			if (radius.getLocalUserGroup() != null)
				radius.getLocalUserGroup().size();
			if (radius.getDirectoryOrLdap() != null){
				radius.getDirectoryOrLdap().size();
				for (ActiveDirectoryOrLdapInfo element : radius.getDirectoryOrLdap()) {
					ActiveDirectoryOrOpenLdap adLdapOd = element.getDirectoryOrLdap();
					List<ActiveDirectoryDomain> domains = adLdapOd.getAdDomains();
					if(null != domains)
						domains.size();
                    IpAddress adServer = adLdapOd.getAdServer();
                    if (null != adServer && null != adServer.getItems()) {
                        adServer.getItems().size();
                    }
                    IpAddress ldapServer = adLdapOd.getLdapServer();
                    if (null != ldapServer && null != ldapServer.getItems()) {
                    	ldapServer.getItems().size();
                    }
				}
			}
			if (radius.getIpOrNames() != null)
				radius.getIpOrNames().size();
			
			//add for ad configuration improvement 
			if (radius.getLdapOuUserProfiles() != null)
				radius.getLdapOuUserProfiles().size();
//		} else if (bo instanceof UserProfile) {
			//add for ad configuration improvement 
//			UserProfile user = (UserProfile)bo;
//			user.getVlan().getId();
		} else if (bo instanceof ActiveDirectoryOrOpenLdap) {
			//add for ad configuration improvement 
			ActiveDirectoryOrOpenLdap adLdapOd = (ActiveDirectoryOrOpenLdap)bo;
			if (adLdapOd.getAdDomains() != null) {
				adLdapOd.getAdDomains().size();
			}
            IpAddress adServer = adLdapOd.getAdServer();
            if (null != adServer && null != adServer.getItems()) {
                adServer.getItems().size();
            }
            IpAddress ldapServer = adLdapOd.getLdapServer();
            if (null != ldapServer && null != ldapServer.getItems()) {
            	ldapServer.getItems().size();
            }
		}
		return null;
	}

	public void prepareAvailableUserGroup() throws Exception {
		/*
		 * user group for local database
		 */
		localUserGroupOptions = new OptionsTransfer(MgrUtil
			.getUserMessage("config.radiusOnHiveAp.availabel.group"),
			MgrUtil.getUserMessage("config.radiusOnHiveAp.selected.group"),
			getLeftOptionOfUserGroup(getDataSource().getLocalUserGroup()), getDataSource().getLocalUserGroup(), "id", "value",
			"selectLocalUserGroup", 512, "160px", "12", false, "LocalUserGroup");
		
		prepareUserProfiles();
		
		// domain for tree browser
		prepareDomainsForTree();
		
		prepareExternalDbType();
	}

	private List<CheckItem> getLeftOptionOfUserGroup(Set<LocalUserGroup> haveUsedGroup) {
		List<CheckItem> availableGroup = getBoCheckItems("groupName", LocalUserGroup.class, new FilterParams("userType",
			LocalUserGroup.USERGROUP_USERTYPE_RADIUS));
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		if (null != haveUsedGroup) {
			for (CheckItem oneItem : availableGroup) {
				for (LocalUserGroup savedGroup : haveUsedGroup) {
					if (savedGroup.getGroupName()
							.equals(oneItem.getValue())) {
						removeList.add(oneItem);
					}
				}
			}
			availableGroup.removeAll(removeList);
		}
		return availableGroup;
	}

	public void setSelectedLocalUserGroup() throws Exception {
		Set<LocalUserGroup> localUserGroup = new HashSet<LocalUserGroup>();
		if (null != selectLocalUserGroup) {
			for (Long group_id : selectLocalUserGroup) {
				LocalUserGroup group = findBoById(LocalUserGroup.class, group_id);
				if (group != null) {
					localUserGroup.add(group);
				}
			}
		}
		getDataSource().setLocalUserGroup(localUserGroup.size() > 0 ? localUserGroup : null);
	}

	public OptionsTransfer getLocalUserGroupOptions()
	{
		return localUserGroupOptions;
	}

	public void setLocalUserGroupOptions(OptionsTransfer localUserGroupOptions)
	{
		this.localUserGroupOptions = localUserGroupOptions;
	}

	public List<Long> getSelectLocalUserGroup()
	{
		return selectLocalUserGroup;
	}

	public void setSelectLocalUserGroup(List<Long> selectLocalUserGroup)
	{
		this.selectLocalUserGroup = selectLocalUserGroup;
	}

    public String getSharekey()
	{
		return sharekey;
	}

	public String getDescription()
	{
		return description;
	}

	public List<CheckItem> getLocalUserGroup() {
		return getBoCheckItems("groupName", LocalUserGroup.class, null,
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	private Long localUserGroupId;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_ENABLE = 2;

	public static final int COLUMN_PORT = 3;

	public static final int COLUMN_DESCRIPTION = 4;

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.radiusOnHiveAp.radiusName";
			break;
		case COLUMN_ENABLE:
			code = "config.radiusOnHiveAp.server";
			break;
		case COLUMN_PORT:
			code = "config.radiusOnHiveAp.port";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.radiusOnHiveAp.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(4);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ENABLE));
		columns.add(new HmTableColumn(COLUMN_PORT));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public Long getLocalUserGroupId() {
		return localUserGroupId;
	}

	public void setLocalUserGroupId(Long localUserGroupId) {
		this.localUserGroupId = localUserGroupId;
	}
	
	public String getInputIpValue()
	{
		if (null != ipAddress) {
			for (CheckItem item : getAvailableIpAddress()) {
				if (item.getId().longValue() == ipAddress.longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue)
	{
		this.inputIpValue = inputIpValue;
	}

	/**
	 * Get number of local user group and local user.
	 *
	 * @param arg_Radius -
	 * @return int[] : int[0] is the number of local user group; int[1] is the
	 * number of local user; int[2] is the max number of local user.
	 */
	private static int[] getNumberOfGroupAndUser(RadiusOnHiveap arg_Radius) {
		int[] result = new int[3];
		result[0] = 0;
		result[1] = 0;
		result[2] = 0;
		if (null != arg_Radius) {
			Set<LocalUserGroup> allGroups = new HashSet<LocalUserGroup>();
			Set<LocalUserGroup> localGroup = new HashSet<LocalUserGroup>();
			
			if (null != arg_Radius.getLdapOuUserProfiles()) {
				for (LdapServerOuUserProfile ldapOu : arg_Radius.getLdapOuUserProfiles()) {
					localGroup.add(ldapOu.getLocalUserGroup());
				}
			}

			// local database user group
			if (null != arg_Radius.getLocalUserGroup()) {
				allGroups.addAll(arg_Radius.getLocalUserGroup());

				// active directory or ldap user group
				for (LocalUserGroup group : localGroup) {
					boolean overlap = false;
					for (LocalUserGroup group1 : arg_Radius.getLocalUserGroup()) {
						if (group.getGroupName().equals(group1.getGroupName())) {
							overlap = true;
							break;
						}
					}
					if (!overlap) {
						allGroups.add(group);
					}
				}
			} else {
				allGroups.addAll(localGroup);
			}
			result[0] = allGroups.size();
			int userNumber = 0;
			long maxCount = 0;
			for (LocalUserGroup group : allGroups) {
				long count = QueryUtil.findRowCount(LocalUser.class,
					new FilterParams("localUserGroup", group));
				userNumber += count;
				if(count > maxCount){
					maxCount = count;
				}
			}
			result[1] = userNumber;
			result[2] = (int)maxCount;
		}
		return result;
	}

	public static int[] getNumberOfGroupAndUser(Long arg_RadiusId){
		RadiusOnHiveap radius = QueryUtil.findBoById(RadiusOnHiveap.class, arg_RadiusId, new RadiusOnHiveApAction());
		return getNumberOfGroupAndUser(radius);
	}

	public boolean getOpenDirect()
	{
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT)
			openDirect = true;
		return openDirect;
	}
	
	private Long sipServerId;
	
	private String inputSipServer;
	
	private Long librarySipId;

	public Long getLibrarySipId()
	{
		if (null == librarySipId && null != getDataSource().getSipPolicy()) {
			librarySipId = getDataSource().getSipPolicy().getId();
		}
		return librarySipId;
	}

	public void setLibrarySipId(Long librarySipId)
	{
		this.librarySipId = librarySipId;
	}

	public Long getSipServerId()
	{
		if (null == sipServerId && getDataSource().isLibrarySipCheck() && null != getDataSource().getSipServer()) {
			sipServerId = getDataSource().getSipServer().getId();
		}
		return sipServerId;
	}

	public void setSipServerId(Long sipServerId)
	{
		this.sipServerId = sipServerId;
	}

	public String getInputSipServer()
	{
		if (null != getSipServerId()) {
			for (CheckItem item : getAvailableSipServers()) {
				if (item.getId().longValue() == sipServerId.longValue()) {
					inputSipServer = item.getValue();
					break;
				}
			}
		}
		return inputSipServer;
	}

	public void setInputSipServer(String inputSipServer)
	{
		this.inputSipServer = inputSipServer;
	}

	public void setOpenDirect(boolean openDirect)
	{
		this.openDirect = openDirect;
	}

	public Long getOpenDirectory()
	{
		return openDirectory;
	}

	public void setOpenDirectory(Long openDirectory)
	{
		this.openDirectory = openDirectory;
	}

	public short getOpenDirPriority()
	{
		return openDirPriority;
	}

	public void setOpenDirPriority(short openDirPriority)
	{
		this.openDirPriority = openDirPriority;
	}

	public void setOpenDirPriorities(short[] openDirPriorities)
	{
		this.openDirPriorities = openDirPriorities;
	}

	public void setOpenDirIndices(Collection<String> openDirIndices)
	{
		this.openDirIndices = openDirIndices;
	}

	public void setSharedSecrets(String[] sharedSecrets)
	{
		this.sharedSecrets = sharedSecrets;
	}
	
	/**
	 * add for ad configuration improvement 
	 */
	private Long userProfile; // selected user profile when add role map 
	private String lastNodeServerId;
	private String lastNodeAttrValue;
	private String lastNodeObjectClassOfGroup;
	private String lastNodeDn;
	// parameter for expand node
	private Long serverId;
	private String dn;
	private short serverType;
	private int selectedDomainId;
	// parameter for retrieve node attribute
	private String groupAttributeName;
	private String selectedAp;

	private int[] orderingDir;
	private int[] orderingLdap;
	private int[] orderingOpenDir;
	
	private Long[] userProfilesDir;
	private short[] attributeIdsDir;
	private Long[] userProfilesLdap;
	private short[] attributeIdsLdap;
	private Long[] userProfilesOpenDir;
	private short[] attributeIdsOpenDir;
	Long userProfileId; // AJAX request's parameter

	private Collection<String> roleMapDirIndices;
	private Collection<String> roleMapLdapIndices;
	private Collection<String> roleMapOpenDirIndices;
	
	private String apFilterValue;

	public String getApFilterValue() {
		return apFilterValue;
	}

	public void setApFilterValue(String apFilterValue) {
		this.apFilterValue = apFilterValue;
	}

	public void setOrderingDir(int[] orderingDir) {
		this.orderingDir = orderingDir;
	}

	public void setOrderingLdap(int[] orderingLdap) {
		this.orderingLdap = orderingLdap;
	}

	public void setOrderingOpenDir(int[] orderingOpenDir) {
		this.orderingOpenDir = orderingOpenDir;
	}

	public void setRoleMapDirIndices(Collection<String> roleMapDirIndices) {
		this.roleMapDirIndices = roleMapDirIndices;
	}

	public void setRoleMapLdapIndices(Collection<String> roleMapLdapIndices) {
		this.roleMapLdapIndices = roleMapLdapIndices;
	}

	public void setRoleMapOpenDirIndices(Collection<String> roleMapOpenDirIndices) {
		this.roleMapOpenDirIndices = roleMapOpenDirIndices;
	}

	public void setGroupAttributeName(String groupAttributeName) {
		this.groupAttributeName = groupAttributeName;
	}

	public void setSelectedDomainId(int selectedDomainId) {
		this.selectedDomainId = selectedDomainId;
	}

	public void setServerType(short serverType) {
		this.serverType = serverType;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public void setLastNodeServerId(String lastNodeServerId) {
		this.lastNodeServerId = lastNodeServerId;
	}

	public void setLastNodeAttrValue(String lastNodeAttrValue) {
		this.lastNodeAttrValue = lastNodeAttrValue;
	}

	public void setLastNodeObjectClassOfGroup(String lastNodeObjectClassOfGroup) {
		this.lastNodeObjectClassOfGroup = lastNodeObjectClassOfGroup;
	}

	public String getLastNodeDn() {
		return lastNodeDn;
	}

	public void setLastNodeDn(String lastNodeDn) {
		this.lastNodeDn = lastNodeDn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public List<CheckItem> getAvailableUserProfile() {
		return availableUserProfile;
	}

	public void setUserProfilesDir(Long[] userProfilesDir) {
		this.userProfilesDir = userProfilesDir;
	}

	public void setAttributeIdsDir(short[] attributeIdsDir) {
		this.attributeIdsDir = attributeIdsDir;
	}

	public void setUserProfilesLdap(Long[] userProfilesLdap) {
		this.userProfilesLdap = userProfilesLdap;
	}

	public void setAttributeIdsLdap(short[] attributeIdsLdap) {
		this.attributeIdsLdap = attributeIdsLdap;
	}

	public void setUserProfilesOpenDir(Long[] userProfilesOpenDir) {
		this.userProfilesOpenDir = userProfilesOpenDir;
	}

	public void setAttributeIdsOpenDir(short[] attributeIdsOpenDir) {
		this.attributeIdsOpenDir = attributeIdsOpenDir;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public void setUserProfile(Long userProfile) {
		this.userProfile = userProfile;
	}

	public Long getUserProfile() {
		return userProfile;
	}

	public String getSelectedAp() {
		return selectedAp;
	}

	public void setSelectedAp(String selectedAp) {
		this.selectedAp = selectedAp;
	}

	public String getApListString() {
		return ActiveDirectoryTool.getApListString(domainId,
				ActiveDirectoryTool.QUERY_AP_MAC_FOR_KEY, null);
	}

	public void prepareUserProfiles() {
		if(null == availableUserProfile) {
			availableUserProfile = getBoCheckItems("userProfileName", UserProfile.class, null);
		}

		if (availableUserProfile.size() == 0) {
			availableUserProfile.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	public void prepareDomainsForTree() {
		List<ActiveDirectoryOrLdapInfo> directories = null;
		int domainCnt = 0;
		int domainForTree = 0;
		
		if (getActive()){
			directories = getDataSource().getDirectory();
			domainForTree = getDataSource().getDomainForTreeDir();
		} else if (getOpen()) {
			directories = getDataSource().getLdap();
			domainForTree = getDataSource().getDomainForTreeLdap();
		} else if (getOpenDirect()) {
			directories = getDataSource().getOpenDir();
			domainForTree = getDataSource().getDomainForTreeOpenDir();
		}
		
		List<ActiveDirectoryDomain> listDomainsForTree = new ArrayList<ActiveDirectoryDomain>();
		if (directories == null || directories.isEmpty()) {
			if(getActive()){
				getDataSource().setDomainsForTreeDir(listDomainsForTree);
			} else if (getOpen()) {
				getDataSource().setDomainsForTreeLdap(listDomainsForTree);
			} else if (getOpenDirect()) {
				getDataSource().setDomainsForTreeOpenDir(listDomainsForTree);
			}
			return;
		}
		
		ActiveDirectoryOrOpenLdap adOdLdapServer;
		ActiveDirectoryDomain domain;
		String serverPriority;
		for (ActiveDirectoryOrLdapInfo LdapInfo : directories) {
			switch (LdapInfo.getServerPriority()) {
			case RadiusServer.RADIUS_PRIORITY_PRIMARY:
				serverPriority = "Primary";
				break;
			case RadiusServer.RADIUS_PRIORITY_BACKUP1:
				serverPriority = "Backup1";
				break;
			case RadiusServer.RADIUS_PRIORITY_BACKUP2:
				serverPriority = "Backup2";
				break;
			case RadiusServer.RADIUS_PRIORITY_BACKUP3:
				serverPriority = "Backup3";
				break;
			default:
				serverPriority = "";
				break;
			}
			
			adOdLdapServer = LdapInfo.getDirectoryOrLdap();
			if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP == adOdLdapServer.getTypeFlag()) {
				//LDAP
				domain = new ActiveDirectoryDomain();
				domain.setOptGroupLabel(serverPriority);
				domain.setDomainId(domainCnt); // for decide which domain is being selected
				domain.setServerId(adOdLdapServer.getId());
				
				domain.setServer(adOdLdapServer.getLdapServer().getItems().get(0).getIpAddress()); // server IP
				domain.setBasedN(adOdLdapServer.getBasedN());
				domain.setBindDnName(adOdLdapServer.getBindDnName());
				domain.setBindDnPass(adOdLdapServer.getPasswordO());
				listDomainsForTree.add(domain);
				domainCnt ++;
			} else {
				//AD or OD
				String bindDn;
				for (ActiveDirectoryDomain adOrOd : adOdLdapServer.getAdDomains()) {
					domain = new ActiveDirectoryDomain();
					domain.setOptGroupLabel(serverPriority);
					domain.setDomainId(domainCnt); // for decide which domain is being selected
					domain.setServerId(adOdLdapServer.getId());
					bindDn = adOrOd.getBindDnName();
					
					if (adOrOd.isDefaultFlag()) {
						// default domain
						if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == adOdLdapServer.getTypeFlag()) {
							//AD
							domain.setServer(adOdLdapServer.getAdServer().getItems().get(0).getIpAddress()); // server IP
							domain.setBasedN(adOdLdapServer.getBasedN());
							bindDn = ActiveDirectoryTool.addFullNameToBindDn(bindDn, adOrOd.getFullName());
						} else {
							//OD
							domain.setServer(adOrOd.getFullName()); // domain full name
							domain.setBasedN(ActiveDirectoryTool.getBaseDnFromFullName(adOrOd.getFullName()));
						}
					} else {
						// AD's multi-domains
						domain.setServer(adOrOd.getServer()); // IP of multi-domain
						domain.setBasedN(ActiveDirectoryTool.getBaseDnFromFullName(adOrOd.getFullName()));
						bindDn = ActiveDirectoryTool.addFullNameToBindDn(bindDn, adOrOd.getFullName());
					}
					
					domain.setBindDnName(bindDn);
					domain.setBindDnPass(adOrOd.getBindDnPass());
					listDomainsForTree.add(domain);
					domainCnt ++;
				}
			}
		}
		
		if(getActive()){
			getDataSource().setDomainsForTreeDir(listDomainsForTree);
		} else if (getOpen()) {
			getDataSource().setDomainsForTreeLdap(listDomainsForTree);
		} else if (getOpenDirect()) {
			getDataSource().setDomainsForTreeOpenDir(listDomainsForTree);
		}
		
		// set the selected option for domain list
		setFirstNodeToTreeByDomainId(false, domainForTree, listDomainsForTree, null);
	}

	protected void reorderRoleMaps(List<LdapServerOuUserProfile> ouList) {
		int[] ordering = getReOrdering();
		if (ordering == null) {
			return;
		}

		boolean needsReordering = false;
		for (int i = 0; i < ordering.length; i++) {
			if (ordering[i] != i) {
				needsReordering = true;
			}
			if (ordering[i] < ouList.size()) {
				ouList.get(ordering[i]).setReorder(i);
			}
		}
		if (!needsReordering) {
			return;
		}
		Collections.sort(ouList,
				new Comparator<LdapServerOuUserProfile>() {
					@Override
					public int compare(LdapServerOuUserProfile roleMap1, LdapServerOuUserProfile roleMap2) {
						Integer id1 = roleMap1.getReorder();
						Integer id2 = roleMap2.getReorder();
						return id1.compareTo(id2);
					}
				});
	}

	protected void updateRoleMaps() {
		List<LdapServerOuUserProfile> ouList = getRoleMaps();
		reorderRoleMaps(ouList);
		UserProfile newUserProfile;
		Long[] userProfiles = null;
		short[] attributeIds = null;
		if (getActive()) {
			userProfiles = userProfilesDir;
			attributeIds = attributeIdsDir;
		} else if (getOpen()) {
			userProfiles = userProfilesLdap;
			attributeIds = attributeIdsLdap;
		} else if (getOpenDirect()) {
			userProfiles = userProfilesOpenDir;
			attributeIds = attributeIdsOpenDir;
		}
		if (userProfiles != null) {
			for (int i = 0; i < userProfiles.length
					&& i < ouList.size(); i++) {
				ouList.get(i).setUserProfileId(userProfiles[i]);
				newUserProfile = QueryUtil.findBoById(UserProfile.class, userProfiles[i], this);
				ouList.get(i).setUserProfileName(newUserProfile.getUserProfileName());
				ouList.get(i).setUserProfileAttribute(attributeIds[i]);
			}
		}
	}
	
	private String getLocalUserGroupName(String attrValue) {
		String userGroupName = null;
		if (attrValue != null) {
			String[] strArr = attrValue.split(",");
			if (strArr!= null && strArr.length > 0 ) {
				String[] strArr2 = strArr[0].split("=");
				if (strArr2 != null) {
					if (strArr2.length > 1) {
						userGroupName = strArr2[1];
					} else {
						userGroupName = strArr2[0];
					}
				}
			}
		}
		if (userGroupName != null && userGroupName.length() > HmBoBase.DEFAULT_STRING_LENGTH) {
			userGroupName = userGroupName.substring(0, HmBoBase.DEFAULT_STRING_LENGTH);
		}
		return userGroupName;
	}
	
	private String[] getArrayOfGroupAttrValue(boolean isGlobalCatalogAdd, String userGroup) {
		String[] strArr = null;
		if (isGlobalCatalogAdd) {
			// for type in user group
			strArr = new String[]{userGroup};
		} else {
			if (lastNodeObjectClassOfGroup != null
					&& !"".equals(lastNodeObjectClassOfGroup)) {
				// when group node be selected
//				strArr = new String[]{lastNodeDn};
				strArr = new String[]{ActiveDirectoryTool.replaceSpecialChar(lastNodeDn, ActiveDirectoryTool.HTML_TO_CHAR)}; // fix CFD-552
			} else {
				// user node be selected
				// values of attribute(AD:MEMBEROF,LDAP:RADIUSGROUPNAME,OD:APPLE-GROUP-REALNAME or other attribute input by user)
				if (lastNodeAttrValue != null && !"".equals(lastNodeAttrValue)) {
					if (lastNodeAttrValue.contains(";")) {
						strArr = lastNodeAttrValue.split(";");
					} else {
						strArr = new String[]{lastNodeAttrValue};
					}
				}
			}
		}
		
		return strArr;
	}
	
	private void addRoleMaps(short typeFlag) {
		String groupName;
		List<LdapServerOuUserProfile> roleMaps = getRoleMaps();
		
		//fix bug 17026. if user group for mapping is type in, isGlobalCatalogAdd is true. even the global catalog check box is unchecked. 
		boolean isGlobalCatalogAdd = false;
		String userGroup = getDataSource().getUserGroupForGlobalCatalog();
		if (/*getDataSource().isGlobalCatalog( //fix bug 17026
				&&*/ userGroup != null
				&& !"".equals(userGroup)) {
			isGlobalCatalogAdd = true;
		}
		String[] attrArray = getArrayOfGroupAttrValue(isGlobalCatalogAdd, userGroup);
		int count = 0;
		int oldCount = roleMaps.size();
		if (attrArray != null) {
			count = attrArray.length;
		}
		for (String attribute : attrArray) {
			groupName = getLocalUserGroupName(attribute);
			if (groupName == null || "".equals(groupName)) {
				continue;
			}
			if (roleMaps.size() > 0) {
				boolean isExistInOuList = false;
				for (LdapServerOuUserProfile oneItem : roleMaps) {
					if(groupName.equals(oneItem.getLocalUserGroup().getGroupName())) {
						isExistInOuList = true;
						break;
					}
				}
				if (isExistInOuList) {
					continue;
				} 
			}
			
			addRoleMap(roleMaps, typeFlag, attribute, groupName, isGlobalCatalogAdd);
		}

		if (oldCount + count > roleMaps.size()) {
			if (oldCount == roleMaps.size()) {
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
			} else {
				addActionError(MgrUtil
						.getUserMessage("error.addSomeObjectExists"));
			}
		} else {
			// clear UserGroup For GlobalCatalog
			if (isGlobalCatalogAdd) {
				getDataSource().setUserGroupForGlobalCatalog("");
			}
		}
	}

	protected void addRoleMap(List<LdapServerOuUserProfile> roleMaps,
			short typeFlag, String attribute, String groupName, boolean isGlobalCatalogAdd) {
		LdapServerOuUserProfile roleMap = new LdapServerOuUserProfile();
		// rowId
		roleMap.setRowId(getRowId(roleMaps));
		roleMap.setGroupAttributeValue(attribute);
		if (isGlobalCatalogAdd) {
			roleMap.setServerId(null);
		} else {
			roleMap.setServerId(Long.parseLong(lastNodeServerId));
		}
		roleMap.setTypeFlag(typeFlag);

		// set user profile
		UserProfile newUserProfile = null;
		if (userProfile != null && userProfile != -1) {
			newUserProfile = QueryUtil.findBoById(UserProfile.class, userProfile, this);
			roleMap.setUserProfileName(newUserProfile.getUserProfileName());
			roleMap.setUserProfileId(userProfile);
			roleMap.setUserProfileAttribute(newUserProfile.getAttributeValue());
		}

		// set local user group
		LocalUserGroup localUserGroup = QueryUtil.findBoByAttribute(
				LocalUserGroup.class, "groupname", groupName, domainId);
		if (localUserGroup == null) {
			localUserGroup = new LocalUserGroup();
			localUserGroup.setGroupName(groupName);
			localUserGroup.setOwner(getDomain());
			localUserGroup
					.setUserProfileId(newUserProfile != null ? newUserProfile
							.getAttributeValue() : -1);
		}
		roleMap.setLocalUserGroup(localUserGroup);
		roleMaps.add(roleMap);
	}
	
	private int getRowId(List<LdapServerOuUserProfile> roleMaps) {
		int intRet = 0;
		if (roleMaps != null) {
			Map<Integer, String> rowIds = new HashMap<Integer, String>();
			for (LdapServerOuUserProfile ldapServerOu : roleMaps) {
				rowIds.put(ldapServerOu.getRowId(), null);
			}
			int i = 1;
			while (intRet == 0) {
				if (!rowIds.containsKey(i)) {
					intRet = i;
					break;
				}
				i ++;
			}
		}
		
		return intRet;
	}

	protected Collection<LdapServerOuUserProfile> findRoleMapsToRemove(
			List<LdapServerOuUserProfile> ouList, Collection<String> roleMapIndices) {
		Collection<LdapServerOuUserProfile> removeList = new Vector<LdapServerOuUserProfile>();
		if (roleMapIndices != null) {
			for (String serviceIndex : roleMapIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < ouList.size()) {
						LdapServerOuUserProfile singleAuth = ouList.get(index);
						removeList.add(singleAuth);
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
				}
			}
		}
		return removeList;
	}
	
	// remove roleMaps when AD/LDAP/OD server is deleted
	protected void removeRoleMapsAndTreeNodesAfterServerDeleted(List<Long> removeServerIds,
			List<LdapServerOuUserProfile> roleMapsList, TreeNode treeInfos) {
		List<LdapServerOuUserProfile> removeList = new ArrayList<LdapServerOuUserProfile>(); 
		List<TreeNode> removeNodeList = new ArrayList<TreeNode>(); 
		for (Long serverId : removeServerIds) {
			for (LdapServerOuUserProfile oneLdap : roleMapsList) {
				if (oneLdap.getServerId() != null
						&& oneLdap.getServerId().longValue() == serverId
								.longValue()) {
					removeList.add(oneLdap);
				}
			}
			for (TreeNode node : treeInfos.getTreeNodes()) {
				if(node.getServerId().longValue() == serverId.longValue()) {
					removeNodeList.add(node);
				}
			}
		}
		getDataSource().getLdapOuUserProfiles().removeAll(removeList);
		treeInfos.getTreeNodes().removeAll(removeNodeList);
	}
	
	private void createNewNode(ActiveDirectoryDomain domain, TreeNode treeInfos) {
		TreeNode newNode = new TreeNode();
		newNode.setServerId(domain.getServerId());
		newNode.setLabel(!"".equals(domain.getDomain()) ? domain.getDomain() : domain.getBasedN());
		newNode.setDn(domain.getBasedN()); // case of AD's multi-domain, DN of first node must be empty ("")
		newNode.setDomain(domain.getFullName()); // for AD's multi-domain
		newNode.setParentId(-1);
		newNode.setParentDn("");
		newNode.setNodeId(treeInfos.getNodeCount());
		treeInfos.getTreeNodes().add(newNode);
		treeInfos.setNodeCount(treeInfos.getNodeCount() + 1);
	}
	
	// set domain tree INFOS by selected domain ID, if no domain selected, get the first domain form list
	protected void setFirstNodeToTreeByDomainId(boolean treeChange,
			int domainId, List<ActiveDirectoryDomain> domainsForTree,
			TreeNode treeInfos) {
		if (domainsForTree != null && domainsForTree.size() > 0) {
			if (treeInfos == null) {
				if(getActive()){
					treeInfos = getDataSource().getDirTreeInfos();
				} else if (getOpen()) {
					treeInfos = getDataSource().getLdapTreeInfos();
				} else if (getOpenDirect()) {
					treeInfos = getDataSource().getOpenDirTreeInfos();
				}
			}
			
			ActiveDirectoryDomain newDomain = null;
			for (ActiveDirectoryDomain domain : domainsForTree) {
				if (domainId == domain.getDomainId()) {
					newDomain = domain;
					break;
				}
			}
			
			// newDomain == null : initial time, treeChange : another domain be selected in the page
			if (newDomain == null || treeChange) {
				int domainForTree = domainId;
				if (newDomain == null) {
					newDomain = domainsForTree.get(0);
					domainForTree = newDomain.getDomainId();
				}
				
				// clear the data for expanded DNs of tree
				if(getActive()){
					getDataSource().setExpandDnsDir(null);
					getDataSource().setDomainForTreeDir(domainForTree);
				} else if (getOpen()) {
					getDataSource().setExpandDnsLdap(null);
					getDataSource().setDomainForTreeLdap(domainForTree);
				} else if (getOpenDirect()) {
					getDataSource().setExpandDnsOpenDir(null);
					getDataSource().setDomainForTreeOpenDir(domainForTree);
				}
				
				treeInfos.getTreeNodes().clear();
				treeInfos.setNodeCount(0);
				createNewNode(newDomain, treeInfos);
			}
		}
	}
	
	private List<LdapServerOuUserProfile> getRoleMaps() {
		List<LdapServerOuUserProfile> ouList = null;
		if (getActive()) {
			ouList = getDataSource().getDirectoryOu();
		} else if (getOpen()) {
			ouList = getDataSource().getLdapOu();
		} else if (getOpenDirect()) {
			ouList = getDataSource().getOpenDirOu();
		}
		return ouList;
	}
	
	private Collection<String> getRoleMapIndices() {
		Collection<String> roleMapIndices = null;
		if (getActive()) {
			roleMapIndices = roleMapDirIndices;
		} else if (getOpen()) {
			roleMapIndices = roleMapLdapIndices;
		} else if(getOpenDirect()) {
			roleMapIndices = roleMapOpenDirIndices;
		}
		return roleMapIndices;
	}
	
	private int[] getReOrdering() {
		int[] ordering = null;
		if (getActive()) {
			ordering = orderingDir;
		} else if (getOpen()) {
			ordering = orderingLdap;
		} else if (getOpenDirect()) {
			ordering = orderingOpenDir;
		}
		return ordering;
	}
	
	private TreeNode getTreeInfos(short serverType) {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == serverType) {
			// AD
			return getDataSource().getDirTreeInfos();
		} else if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP == serverType) {
			// LDAP
			return getDataSource().getLdapTreeInfos();
		} else {
			// OD
			return getDataSource().getOpenDirTreeInfos();
		}
	}
	
	private List<ActiveDirectoryDomain> getDomainsForTree(short serverType) {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == serverType) {
			// AD
			return getDataSource().getDomainsForTreeDir();
		} else if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP == serverType) {
			// LDAP
			return getDataSource().getDomainsForTreeLdap();
		} else {
			// OD
			return getDataSource().getDomainsForTreeOpenDir();
		}
	}
	
	private String getExpandedNodes(short serverType) {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == serverType) {
			// AD
			return getExpandedDirNodes();
		} else if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP == serverType) {
			// LDAP
			return getExpandedLdapNodes();
		} else {
			// OD
			return getExpandedOpenDirNodes();
		}
	}

	public String getExpandedDirNodes() {
		return getNodesString(getDataSource().getDirTreeInfos());
	}

	public String getExpandedLdapNodes() {
		return getNodesString(getDataSource().getLdapTreeInfos());
	}

	public String getExpandedOpenDirNodes() {
		return getNodesString(getDataSource().getOpenDirTreeInfos());
	}
	
	private String getNodesString(TreeNode treeInfos) {
		StringBuffer result = new StringBuffer();
		addNodeString(treeInfos, result);
		result.deleteCharAt(result.length() - 1); // delete the last ','
		return result.toString();
	}
	
	private void addNodeString(TreeNode treeInfos, StringBuffer result) {
		TreeNode treeNode;
		for (int i = 0; i < treeInfos.getTreeNodes().size(); i++) {
			treeNode = treeInfos.getTreeNodes().get(i);
			result.append("{");
			result.append("\"serverId\":\"");
			result.append(treeNode.getServerId());
			result.append("\",");
			result.append("\"label\":\"");
			result.append(treeNode.getLabel());
			result.append("\",");
			result.append("\"dn\":\"");
			result.append(treeNode.getDn());
			result.append("\",");
			result.append("\"domain\":\"");
			result.append(treeNode.getDomain());
			result.append("\",");
			result.append("\"parentId\":");
			result.append(treeNode.getParentId());
			result.append(",");
			result.append("\"nodeId\":");
			result.append(treeNode.getNodeId());
			result.append(",");
			result.append("\"dynamicLoadComplete\":");
			result.append(treeNode.isDynamicLoadComplete());
			result.append("},");
			if (treeNode.getTreeNodes() != null
					&& treeNode.getTreeNodes().size() > 0) {
				addNodeString(treeNode, result);
			}
		}
	}
	
	/**
	 * get AP MAC from selected AAA user directories(only when server is AD)
	 * 
	 * @param selectedAp -
	 * @param serverType -
	 * @return -
	 */
	private String getApForCommunication(String selectedAp, short serverType) {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == serverType) {
			// server is AD
			List<ActiveDirectoryOrLdapInfo> directory = getDataSource().getDirectory();
			if (directory != null && !directory.isEmpty()) {
				selectedAp = directory.get(0).getDirectoryOrLdap().getApMac();
			}
		}
		
		return selectedAp;
	}

	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
		getDataSource().setContentShowType(getContentShowType());
	}
	
	private void restoreJsonContext() {
		setParentDomID(getDataSource().getParentDomID());
		setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
		setContentShowType(getDataSource().getContentShowType());
	}

	public void setExternalDb(boolean externalDb) {
		this.externalDb = externalDb;
	}

	private void prepareExternalDbType() {
		short type = getDataSource().getDatabaseType();
		if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE) {
			
			getDataSource().setExternalDbType(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE);
		} else if (type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN) {
			
			getDataSource().setExternalDbType(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN);
		} else if(type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT
				|| type == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT) {

			getDataSource().setExternalDbType(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT);
		}
	}

	public TextItem[] getExternalDbType1() {
		return new TextItem[] { new TextItem(
				String.valueOf(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE),
				getText("config.radiusOnHiveAp.active")) };
	}

	public TextItem[] getExternalDbType2() {
		return new TextItem[] { new TextItem(
				String.valueOf(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN),
				getText("config.radiusOnHiveAp.open")) };
	}

	public TextItem[] getExternalDbType3() {
		return new TextItem[] { new TextItem(
				String.valueOf(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT),
				getText("config.radiusOnHiveAp.open.directory")) };
	}

    public boolean isEnable4Router() {
        return enable4Router;
    }

    public void setEnable4Router(boolean enable4Router) {
        this.enable4Router = enable4Router;
    }
	
}