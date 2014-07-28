package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.NavigationNode;
import com.ah.util.EnumItem;
import com.ah.util.UserSettingsUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "HM_USER")
@org.hibernate.annotations.Table(appliesTo = "HM_USER", indexes = {
		@Index(name = "HM_USER_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HM_USER_NAME", columnNames = { "USERNAME" })
		})
public class HmUser implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	// Owner of the user group should be the same as the owner of
	// the user, except for super users, where the group is owned by
	// 'global'
	@Transient
	public HmDomain getDomain() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = 128, nullable = false)
	private String userName;

	private String password;

	@Column(length = 128)
	private String userFullName;

	@Column(length = 128, unique = true, nullable = false)
	private String emailAddress;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GROUP_ID", nullable = false)
	private HmUserGroup userGroup;

	// default to popup
	@Transient
	private boolean promptChanges = true;

	@Transient
	private boolean endUserLicAgree;

	// do not display no device message again
	@Transient
	private boolean dontShowMessageInDashboard=false;

	private String timeZone = TimeZone.getDefault().getID();

	// for VAD user
	@Transient
	private int maxAPNum = 0;

	public final static short SYNC_RESULT_OK = 0;
	public final static short SYNC_RESULT_DUPLICATED = 1;

	@Transient
	private short syncResult = SYNC_RESULT_OK;

	public String getSyncResultDescription() {
		if (syncResultChanged) fillSyncResultChanged();
		if (syncResult == SYNC_RESULT_OK) {
			return "N/A";
		} else if (syncResult == SYNC_RESULT_DUPLICATED) {
			return "Duplicate user found in server. Restoration of this user is skipped. Please re-create this user if needed.";
		} else {
			return "Sync failed. Restoration of this user is skipped. Please re-create this user if needed.";
		}
	}

	@Transient
	private ConcurrentMap<Integer, List<HmTableColumn>> tableViews;

	public Map<Integer, List<HmTableColumn>> getTableViews() {
		return tableViews;
	}

	public void createTableViews() {
		tableViews = new ConcurrentHashMap<Integer, List<HmTableColumn>>();
		if (tableColumnsChanged) fillTableColumns();

		List<HmTableColumn> table = null;
		for (HmTableColumn column : tableColumns) {
			int tableId = column.getTableId();
			if (tableViews.containsKey(tableId)) {
				tableViews.get(tableId).add(column);
			} else {
				table = new ArrayList<HmTableColumn>();
				table.add(column);
				tableViews.put(tableId, table);
			}
		}
	}

	@Transient
	private ConcurrentMap<Integer, HmTableSize> tableSizeMappings;

	public Map<Integer, HmTableSize> getTableSizeMappings() {
		return tableSizeMappings;
	}

	public void createTableSizeMappings() {
		tableSizeMappings = new ConcurrentHashMap<Integer, HmTableSize>();
		if (tableSizesChanged) fillTableSizes();

		for (HmTableSize tableSize : tableSizes) {
			tableSizeMappings.put(tableSize.getTableId(), tableSize);
		}
	}

//	@ElementCollection(fetch = FetchType.LAZY)
//	@OrderColumn(name = "POSITION")
//	@CollectionTable(name = "HM_TABLE_COLUMN", joinColumns = @JoinColumn(name = "HM_USER_ID", nullable = true))
	@Transient
	private List<HmTableColumn> tableColumns = new ArrayList<HmTableColumn>();

	@Transient
	private boolean tableColumnsChanged = true;

	public List<HmTableColumn> getTableColumns() {
		if (tableColumnsChanged) fillTableColumns();
		return tableColumns;
	}

	public void setTableColumns(List<HmTableColumn> tableColumns) {
		this.tableColumns = tableColumns;
	}

//	@ElementCollection(fetch = FetchType.LAZY)
//	@OrderColumn(name = "POSITION")
//	@CollectionTable(name = "HM_TABLE_SIZE", joinColumns = @JoinColumn(name = "HM_USER_ID", nullable = true))
	@Transient
	private List<HmTableSize> tableSizes = new ArrayList<HmTableSize>();

	@Transient
	private boolean tableSizesChanged = true;

	public List<HmTableSize> getTableSizes() {
		if (tableSizesChanged) fillTableSizes();
		return tableSizes;
	}

	public void setTableSizes(List<HmTableSize> tableSizes) {
		this.tableSizes = tableSizes;
	}

	@Transient
	private ConcurrentMap<Integer, HmAutoRefresh> autoRefreshMappings;

	public Map<Integer, HmAutoRefresh> getAutoRefreshMappings() {
		return autoRefreshMappings;
	}

	public void createAutoRefreshMappings() {
		autoRefreshMappings = new ConcurrentHashMap<Integer, HmAutoRefresh>();
		if (autoRefreshsChanged) fillAutoRefreshs();
		for (HmAutoRefresh autoRefresh : autoRefreshs) {
			autoRefreshMappings.put(autoRefresh.getTableId(), autoRefresh);
		}
	}

//	@ElementCollection(fetch = FetchType.LAZY)
//	@OrderColumn(name = "POSITION")
//	@CollectionTable(name = "HM_AUTOREFRESH_SETTINGS", joinColumns = @JoinColumn(name = "HM_USER_ID", nullable = true))
	@Transient
	private List<HmAutoRefresh> autoRefreshs = new ArrayList<HmAutoRefresh>();

	@Transient
	private boolean autoRefreshsChanged = true;

	public List<HmAutoRefresh> getAutoRefreshs() {
		if (autoRefreshsChanged) fillAutoRefreshs();
		return autoRefreshs;
	}

	public void setAutoRefreshs(List<HmAutoRefresh> autoRefreshs) {
		this.autoRefreshs = autoRefreshs;
	}

	@Transient
	private String userIpAddress;

	public static final String ADMIN_USER = "admin";

	private boolean defaultFlag;

	// add this field for hm online sub admin user from 4.0r2
	private boolean accessMyhive;

	// add this field for default user of vhm
	public final static short NO_APPLICATION = 0;

	public final static short STAGING_SERVER = 2;

	public final static short HM_ONLINE = 3;

	private short defapplication = NO_APPLICATION;

	public final static EnumItem[] APPLICATIONS = new EnumItem[2];

	static {
		APPLICATIONS[0] = new EnumItem(HM_ONLINE, "HiveManager");
		APPLICATIONS[1] = new EnumItem(STAGING_SERVER, "Aerohive Redirector");
	}

	@Transient
	private short treeWidth = 220;

	public short getTreeWidthForRestore() {
		return treeWidth;
	}

	public short getTreeWidth() {
		if (treeWidthChanged) fillTreeWidthChanged();
		return treeWidth;
	}

	public void setTreeWidth(short treeWidth) {
		this.treeWidth = treeWidth;
	}

	@Transient
	private boolean orderFolders;

	public boolean isOrderFoldersForRestore() {
		return orderFolders;
	}

	public boolean isOrderFolders() {
		if (orderFoldersChanged) fillOrderFoldersChanged();
		return orderFolders;
	}

	public void setOrderFolders(boolean orderFolders) {
		this.orderFolders = orderFolders;
	}

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "USER_LOCALUSERGROUP", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCALUSERGROUP_ID") })
	@Transient
	private Set<LocalUserGroup> localUserGroups = new HashSet<LocalUserGroup>();

	@Transient
	private boolean localUserGroupsChanged = true;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "USER_SSIDPROFILE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "SSIDPROFILE_ID") })
	@Transient
	private Set<SsidProfile> ssidProfiles = new HashSet<SsidProfile>();

	@Transient
	private boolean ssidProfilesChanged = true;

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public HmUserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(HmUserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	//@Column(name = "navCustomization",  nullable = false,columnDefinition="INT default 16")
	@Transient
	private int navCustomization=16;

	public int getNavCustomizationForRestore() {
		return navCustomization;
	}

	public int getNavCustomization() {
		if (navCustomizationChanged) fillNavCustomizationChanged();
		return navCustomization;
	}

	public void setNavCustomization(int navCustomization) {
		this.navCustomization = navCustomization;
	}

	@Transient
	private NavigationNode navigationTree;

	@Transient
	private Map<String, NavigationNode> featureNodes;

	public NavigationNode getNavigationTree() {
		return navigationTree;
	}

	public void setNavigationTree(NavigationNode navigationTree) {
		this.navigationTree = navigationTree;
	}

	public Map<String, NavigationNode> getFeatureNodes() {
		return featureNodes;
	}

	public void setFeatureNodes(Map<String, NavigationNode> featureNodes) {
		this.featureNodes = featureNodes;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return userName;
	}

	public String getUserIpAddress() {
		return userIpAddress;
	}

	public void setUserIpAddress(String userIpAddress) {
		this.userIpAddress = userIpAddress;
	}

	public boolean isSuperUser() {
		return getUserGroup().isAdministrator() || isSuperUserForSwitchDomain();
	}

	public boolean isPlannerUser() {
		return getUserGroup().isPlUserGroup();
	}

	@Transient
	private int sessionExpiration;

	public int getSessionExpiration() {
		return sessionExpiration;
	}

	public void setSessionExpiration(int sessionExpiration) {
		this.sessionExpiration = sessionExpiration;
	}

	@Transient
	private HmDomain switchDomain;

	public HmDomain getSwitchDomain() {
		return switchDomain;
	}

	public String getDomainUserName() {
		// if user has no CID, show user name get from HMOL DB
		String userNameForTopPane = userName;
		
		// fix bug 19711, show user name on TopPanel if customer has CID
		if (!StringUtils.isEmpty(customerId)) {
			// if user has CID, show user name retrieved from MyHive
			userNameForTopPane = userNameInMyhive;
		}
		if (switchDomain == null) {
			return userNameForTopPane;
		} else {
			return switchDomain.getDomainName() + "//" + userNameForTopPane;
		}
	}
	
	public String getDomainUserId() {
		if (switchDomain == null) {
			return id.toString();
		} else {
			return switchDomain.getId() + "//" + id;
		}
	}

	public void setSwitchDomain(HmDomain switchDomain) {
		this.switchDomain = switchDomain;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isPromptChangesForRestore() {
		return promptChanges;
	}

	public boolean isPromptChanges() {
		if (promptChangesChanged) fillPromptChangesChanged();
		return promptChanges;
	}

	public void setPromptChanges(boolean promptChanges) {
		this.promptChanges = promptChanges;
	}

	public boolean isEndUserLicAgreeForRestore() {
		return endUserLicAgree;
	}

	public boolean isEndUserLicAgree() {
		if (endUserLicAgreeChanged) fillEndUserLicAgreeChanged();
		return endUserLicAgree;
	}

	public void setEndUserLicAgree(boolean endUserLicAgree) {
		this.endUserLicAgree = endUserLicAgree;
	}

	@Transient
	public boolean ifShowEualPage() {
//		return id > 0 && !endUserLicAgree;
		return /*id > 0 && */!isEndUserLicAgree(); // changed for user settings separated from HM_USER in Geneva
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Transient
	private short mode = 0;

	/**
	 * <li>when a user login to HiveManager, he/she will be in either full mode
	 * or easy mode, in order to reduce db query, just query the first time for
	 * per user. <li>Super user can switch to other domain, after switch
	 * operation, must to reset this value to 0, then pick up the value from db
	 * next time.
	 *
	 * @return -
	 */
	public short getMode() {
		if (mode == 0) {
			if (switchDomain != null) {
				List<HmStartConfig> list = QueryUtil.executeQuery(
						HmStartConfig.class, null, null, switchDomain.getId());
				if (!list.isEmpty()) {
					mode = list.get(0).getModeType();
				}
			} else {
				List<HmStartConfig> list = QueryUtil.executeQuery(
						HmStartConfig.class, null, null, owner.getId());
				if (!list.isEmpty()) {
					mode = list.get(0).getModeType();
				}
			}
		}
		return mode;
	}

	public void setMode(short mode) {
		this.mode = mode;
	}

	@Transient
	private String passwordInClearText;

	/**
	 * getter of passwordInClearText
	 *
	 * @return the passwordInClearText
	 */
	public String getPasswordInClearText() {
		return passwordInClearText;
	}

	/**
	 * setter of passwordInClearText
	 *
	 * @param passwordInClearText
	 *            the passwordInClearText to set
	 */
	public void setPasswordInClearText(String passwordInClearText) {
		this.passwordInClearText = passwordInClearText;
	}

	public short getDefapplication() {
		return defapplication;
	}

	public void setDefapplication(short defapplication) {
		this.defapplication = defapplication;
	}

	public boolean isVadAdmin() {
		return userGroup.getGroupName().equals(HmUserGroup.VAD)
				|| userGroup.getGroupName().equals(HmUserGroup.VAD_CONFIG_MONITOR)
				|| userGroup.getGroupName().equals(HmUserGroup.VAD_MONITOR)
				|| isVadForSwitchDomain();
	}

	@Transient
	private boolean redirectUser;

	public boolean isRedirectUser() {
		return redirectUser;
	}

	public void setRedirectUser(boolean redirectUser) {
		this.redirectUser = redirectUser;
	}

	public int getMaxAPNumForRestore() {
		return maxAPNum;
	}

	public int getMaxAPNum() {
		if (maxAPNumChanged) fillMaxAPNumChanged();
		return maxAPNum;
	}

	public void setMaxAPNum(int maxAPNum) {
		this.maxAPNum = maxAPNum;
	}

	public void setSyncResult(short syncResult) {
		this.syncResult = syncResult;
	}

	public short getSyncResultForRestore() {
		return syncResult;
	}

	public short getSyncResult() {
		if (syncResultChanged) fillSyncResultChanged();
		return syncResult;
	}

	public Set<LocalUserGroup> getLocalUserGroupsForRestore() {
		return localUserGroups;
	}

	public Set<LocalUserGroup> getLocalUserGroups() {
		if (localUserGroupsChanged) fillLocalUserGroups();
		return localUserGroups;
	}

	private void fillLocalUserGroups() {
		try {
			List<HmLocalUserGroup> localUserGroupsList = QueryUtil.executeQuery(HmLocalUserGroup.class, null , new FilterParams("useremail", this.emailAddress));
			this.localUserGroups.clear();

			for (HmLocalUserGroup hmLocalUserGroup : localUserGroupsList) {
				LocalUserGroup localUserGroup = QueryUtil.findBoById(LocalUserGroup.class, hmLocalUserGroup.getLocalusergroup_id());
				if (localUserGroup != null) {
					this.localUserGroups.add(localUserGroup);
				}
			}
			localUserGroupsChanged = false;
		} catch (Exception e) {
			localUserGroupsChanged = true;
		}
	}

	public void removeLocalUserGroups() {
		try {
			QueryUtil.bulkRemoveBos(HmLocalUserGroup.class, new FilterParams("useremail", this.emailAddress));

			localUserGroupsChanged = true;
		} catch (Exception e) {
			localUserGroupsChanged = false;
		}
	}

	public Set<LocalUserGroup> getLocalUserGroups(Long domainId) {
		if (localUserGroupsChanged) fillLocalUserGroups(domainId);
		return localUserGroups;
	}

	private void fillLocalUserGroups(Long domainId) {
		try {
			List<HmLocalUserGroup> localUserGroupsList = QueryUtil.executeQuery(HmLocalUserGroup.class, null , new FilterParams("useremail", this.emailAddress));
			this.localUserGroups.clear();

			for (HmLocalUserGroup hmLocalUserGroup : localUserGroupsList) {
//				LocalUserGroup localUserGroup = QueryUtil.findBoById(LocalUserGroup.class, hmLocalUserGroup.getLocalusergroup_id());
				LocalUserGroup localUserGroup = QueryUtil.findBoByAttribute(LocalUserGroup.class, "id", hmLocalUserGroup.getLocalusergroup_id(), domainId);
				if (localUserGroup != null) {
					this.localUserGroups.add(localUserGroup);
				}
			}
			localUserGroupsChanged = false;
		} catch (Exception e) {
			localUserGroupsChanged = true;
		}
	}

	public void removeLocalUserGroups(List<?> ids) {
		if (ids == null || ids.isEmpty()) {
			return;
		}
		try {
			FilterParams filterParams = new FilterParams(
					"useremail = :s1 and localusergroup_id in (:s2)",
					new Object[] { this.emailAddress, ids });
			QueryUtil.bulkRemoveBos(HmLocalUserGroup.class, filterParams);

			ssidProfilesChanged = true;
		} catch (Exception e) {
			ssidProfilesChanged = false;
		}
	}

	public void addLocalUserGroups() {
		try {
			Set<HmLocalUserGroup> hmLUGs = new HashSet<HmLocalUserGroup>();
			for (LocalUserGroup luserGroup : this.localUserGroups) {
				HmLocalUserGroup hmLUG = new HmLocalUserGroup();
				hmLUG.setUseremail(this.emailAddress);
				hmLUG.setLocalusergroup_id(luserGroup.getId());
				hmLUGs.add(hmLUG);
			}

			if (hmLUGs.size() > 0) {
				QueryUtil.bulkCreateBos(hmLUGs);
			}
			localUserGroupsChanged = true;
		} catch (Exception e) {
			localUserGroupsChanged = false;
		}
	}

	public void setLocalUserGroups(Set<LocalUserGroup> localUserGroups) {
		this.localUserGroups = localUserGroups;
	}

	public Set<SsidProfile> getSsidProfilesForRestore() {
		return ssidProfiles;
	}

	public Set<SsidProfile> getSsidProfiles() {
		if (ssidProfilesChanged) fillSsidProfiles();
		return ssidProfiles;
	}

	private void fillSsidProfiles() {
		try {
			List<HmUserSsidProfile> ssidProfilesList = QueryUtil.executeQuery(HmUserSsidProfile.class, null , new FilterParams("useremail", this.emailAddress));
			this.ssidProfiles.clear();

			for (HmUserSsidProfile userSsidProfile : ssidProfilesList) {
				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, userSsidProfile.getSsidprofile_id());
				if (ssidProfile != null) {
					this.ssidProfiles.add(ssidProfile);
				}
			}
			ssidProfilesChanged = false;
		} catch (Exception e) {
			ssidProfilesChanged = true;
		}
	}

	public void removeSsidProfiles() {
		try {
			QueryUtil.bulkRemoveBos(HmUserSsidProfile.class, new FilterParams("useremail", this.emailAddress));

			ssidProfilesChanged = true;
		} catch (Exception e) {
			ssidProfilesChanged = false;
		}
	}

	public Set<SsidProfile> getSsidProfiles(Long domainId) {
		if (ssidProfilesChanged) fillSsidProfiles(domainId);
		return ssidProfiles;
	}

	private void fillSsidProfiles(Long domainId) {
		try {
			List<HmUserSsidProfile> ssidProfilesList = QueryUtil.executeQuery(HmUserSsidProfile.class, null , new FilterParams("useremail", this.emailAddress));
			this.ssidProfiles.clear();

			for (HmUserSsidProfile userSsidProfile : ssidProfilesList) {
//				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, userSsidProfile.getSsidprofile_id());
				SsidProfile ssidProfile = QueryUtil.findBoByAttribute(SsidProfile.class, "id", userSsidProfile.getSsidprofile_id(), domainId);
				if (ssidProfile != null) {
					this.ssidProfiles.add(ssidProfile);
				}
			}
			ssidProfilesChanged = false;
		} catch (Exception e) {
			ssidProfilesChanged = true;
		}
	}

	public void removeSsidProfiles(List<?> ids) {
		if (ids == null || ids.isEmpty()) {
			return;
		}
		try {
			FilterParams filterParams = new FilterParams(
					"useremail = :s1 and ssidprofile_id in (:s2)",
					new Object[] { this.emailAddress, ids });
			QueryUtil.bulkRemoveBos(HmUserSsidProfile.class, filterParams);

			ssidProfilesChanged = true;
		} catch (Exception e) {
			ssidProfilesChanged = false;
		}
	}

	public void addSsidProfiles() {
		try {
			Set<HmUserSsidProfile> hmUSPs = new HashSet<HmUserSsidProfile>();
			for (SsidProfile ssidProfile : this.ssidProfiles) {
				HmUserSsidProfile hmUSP = new HmUserSsidProfile();
				hmUSP.setUseremail(this.emailAddress);
				hmUSP.setSsidprofile_id(ssidProfile.getId());
				hmUSPs.add(hmUSP);
			}

			if (hmUSPs.size() > 0) {
				QueryUtil.bulkCreateBos(hmUSPs);
			}
			ssidProfilesChanged = true;
		} catch (Exception e) {
			ssidProfilesChanged = false;
		}
	}

	public void setSsidProfiles(Set<SsidProfile> ssidProfiles) {
		this.ssidProfiles = ssidProfiles;
	}

	public boolean isAccessMyhive()
	{
		return accessMyhive;
	}

	public void setAccessMyhive(boolean accessMyhive)
	{
		this.accessMyhive = accessMyhive;
	}

	// if is temp access, show left time once after switch domain
	@Transient
	protected boolean showLeftAccessTimeForSwitchDomain;

	public boolean isShowLeftAccessTimeForSwitchDomain() {
		return showLeftAccessTimeForSwitchDomain;
	}

	public void setShowLeftAccessTimeForSwitchDomain(
			boolean showLeftAccessTimeForSwitchDomain) {
		this.showLeftAccessTimeForSwitchDomain = showLeftAccessTimeForSwitchDomain;
	}

	@Transient
	protected boolean isSuperUserForSwitchDomain;

	public boolean isSuperUserForSwitchDomain() {
		return isSuperUserForSwitchDomain;
	}

	public void setSuperUserForSwitchDomain(boolean isSuperUserForSwitchDomain) {
		this.isSuperUserForSwitchDomain = isSuperUserForSwitchDomain;
	}

	@Transient
	protected boolean isVadForSwitchDomain;

	public boolean isVadForSwitchDomain() {
		return isVadForSwitchDomain;
	}

	public void setVadForSwitchDomain(boolean isVadForSwitchDomain) {
		this.isVadForSwitchDomain = isVadForSwitchDomain;
	}

	@Transient
	private String customerId;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	// public long getLastLoginTime() {
	// return lastLoginTime;
	// }

	// @Transient
	// private long apCount;
	// @Transient
	// private long currentLoginCount;
	// @Transient
	// private TimeZone plannerAdminDomain;
	// public String getLastLoginTimeString() {
	// if (lastLoginTime==0) {
	// return " ";
	// }
	// if (plannerAdminDomain==null) {
	// return AhDateTimeUtil.getSpecifyDateTime(lastLoginTime);
	// }
	// return AhDateTimeUtil.getSpecifyDateTime(lastLoginTime,
	// plannerAdminDomain);
	// }
	//
	// public void setLastLoginTime(long lastLoginTime) {
	// this.lastLoginTime = lastLoginTime;
	// }

	// public long getTotalLoginTime() {
	// return totalLoginTime;
	// }
	//
	// public String getTotalLoginTimeString() {
	// if (totalLoginTime==0){
	// return " ";
	// }
	// return NmsUtil.transformTime((int) (totalLoginTime /
	// 1000)).replace(" 0 Secs", "");
	// }

	// public void setTotalLoginTime(long totalLoginTime) {
	// this.totalLoginTime = totalLoginTime;
	// }
	//
	// public long getLoginCount() {
	// return loginCount;
	// }

	// public void setLoginCount(long loginCount) {
	// this.loginCount = loginCount;
	// }
	//
	// public TimeZone getPlannerAdminDomain() {
	// return plannerAdminDomain;
	// }

	// public void setPlannerAdminDomain(TimeZone plannerAdminDomain) {
	// this.plannerAdminDomain = plannerAdminDomain;
	// }
	//
	// public long getApCount() {
	// return apCount;
	// }

	// public void setApCount(long apCount) {
	// this.apCount = apCount;
	// }
	//
	// public long getCurrentLoginCount() {
	// return currentLoginCount;
	// }
	//
	// public void setCurrentLoginCount(long currentLoginCount) {
	// this.currentLoginCount = currentLoginCount;
	// }

	// added for jump from other application
	@Transient
	private String sourceUrl;

	@Transient
	private int sourceId;

	@Transient
	public boolean isSourceFromIdm() {
		return sourceId > 1000 && sourceId < 2001;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isDontShowMessageInDashboardForRestore() {
		return dontShowMessageInDashboard;
	}

	public boolean isDontShowMessageInDashboard() {
		if (dontShowMessageInDbChanged) fillDontShowMessageInDbChanged();
		return dontShowMessageInDashboard;
	}

	public void setDontShowMessageInDashboard(boolean dontShowMessageInDashboard) {
		this.dontShowMessageInDashboard = dontShowMessageInDashboard;
	}

	private void fillTableColumns() {
		try {
			this.tableColumns = QueryUtil.executeQuery(HmTableColumn.class, new SortParams("position"), new FilterParams("useremail", this.emailAddress));
			tableColumnsChanged = false;
		} catch (Exception e) {
			tableColumnsChanged = true;
		}
	}

	private void fillTableSizes() {
		try {
			this.tableSizes = QueryUtil.executeQuery(HmTableSize.class, null , new FilterParams("useremail", this.emailAddress));
			tableSizesChanged = false;
		} catch (Exception e) {
			tableSizesChanged = true;
		}
	}

	private void fillAutoRefreshs() {
		try {
			this.autoRefreshs = QueryUtil.executeQuery(HmAutoRefresh.class, null , new FilterParams("useremail", this.emailAddress));
			autoRefreshsChanged = false;
		} catch (Exception e) {
			autoRefreshsChanged = true;
		}
	}

	public void addTableColumns(List<HmTableColumn> columns) {
		try {
			QueryUtil.bulkCreateBos(columns);
			tableColumnsChanged = true;
		} catch (Exception e) {
			tableColumnsChanged = false;
		}
	}

	public void removeTableColumns(int tableId) {
		try {
			QueryUtil.bulkRemoveBos(HmTableColumn.class, new FilterParams("useremail = :s1 and tableid = :s2", new Object[]{ this.emailAddress, tableId }));

			tableColumnsChanged = true;
		} catch (Exception e) {
			tableColumnsChanged = false;
		}
	}

	public void addTableSizes(HmTableSize size) {
		try {
			QueryUtil.createBo(size);
			tableSizesChanged = true;
		} catch (Exception e) {
			tableSizesChanged = false;
		}
	}

	public void removeTableSizes(int tableId) {
		try {
			QueryUtil.bulkRemoveBos(HmTableSize.class, new FilterParams("useremail = :s1 and tableid = :s2", new Object[]{ this.emailAddress, tableId }));

			tableSizesChanged = true;
		} catch (Exception e) {
			tableSizesChanged = false;
		}
	}

	public void addAutoRefreshs(HmAutoRefresh auto) {
		try {
			QueryUtil.createBo(auto);
			autoRefreshsChanged = true;
		} catch (Exception e) {
			autoRefreshsChanged  = false;
		}
	}

	public void removeAutoRefreshs(int tableId) {
		try {
			QueryUtil.bulkRemoveBos(HmAutoRefresh.class, new FilterParams("useremail = :s1 and tableid = :s2", new Object[]{ this.emailAddress, tableId }));

			autoRefreshsChanged  = true;
		} catch (Exception e) {
			autoRefreshsChanged  = false;
		}
	}

	@Transient
	private boolean dontShowMessageInDbChanged = true;
	private void fillDontShowMessageInDbChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			dontShowMessageInDashboard = userSettings.isDontShowMessageInDashboard();
			dontShowMessageInDbChanged = false;
		} catch (Exception e) {
			dontShowMessageInDbChanged = true;
		}
	}

	@Transient
	private boolean endUserLicAgreeChanged = true;
	private void fillEndUserLicAgreeChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			endUserLicAgree = userSettings.isEndUserLicAgree();
			endUserLicAgreeChanged = false;
		} catch (Exception e) {
			endUserLicAgreeChanged = true;
		}
	}

	@Transient
	private boolean maxAPNumChanged = true;
	private void fillMaxAPNumChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			maxAPNum = userSettings.getMaxAPNum();
			maxAPNumChanged = false;
		} catch (Exception e) {
			maxAPNumChanged = true;
		}
	}

	@Transient
	private boolean navCustomizationChanged = true;
	private void fillNavCustomizationChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			navCustomization = userSettings.getNavCustomization();
			navCustomizationChanged = false;
		} catch (Exception e) {
			navCustomizationChanged = true;
		}
	}

	@Transient
	private boolean orderFoldersChanged = true;
	private void fillOrderFoldersChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			orderFolders = userSettings.isOrderFolders();
			orderFoldersChanged = false;
		} catch (Exception e) {
			orderFoldersChanged = true;
		}
	}

	@Transient
	private boolean promptChangesChanged = true;
	private void fillPromptChangesChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			promptChanges = userSettings.isPromptChanges();
			promptChangesChanged = false;
		} catch (Exception e) {
			promptChangesChanged = true;
		}
	}

	@Transient
	private boolean syncResultChanged = true;
	private void fillSyncResultChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			syncResult = userSettings.getSyncResult();
			syncResultChanged = false;
		} catch (Exception e) {
			syncResultChanged = true;
		}
	}

	@Transient
	private boolean treeWidthChanged = true;
	private void fillTreeWidthChanged() {
		try {
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress);
			treeWidth = userSettings.getTreeWidth();
			treeWidthChanged = false;
		} catch (Exception e) {
			treeWidthChanged = true;
		}
	}
	
	// fix bug 19711, show user name on TopPanel if customer has CID
	@Transient
	private String userNameInMyhive;

	public String getUserNameInMyhive() {
		return userNameInMyhive;
	}

	public void setUserNameInMyhive(String userNameInMyhive) {
		this.userNameInMyhive = userNameInMyhive;
	}

	/*
	 * add for new Teacher on TV page start
	 */
    // English
    public final static short                           I18N_DEFAULT        = 0;
    // Japanese
    public final static short                           I18N_JAPANESE       = 1;
    // Chinese
    public final static short                           I18N_CHINESE        = 2;
    // French
    public final static short                           I18N_FRENCH         = 3;
    // German
    public final static short                           I18N_GERMAN         = 4;
    // Italian
    public final static short                           I18N_ITALIAN        = 5;
    // Spanish
    public final static short                           I18N_SPANISH        = 6;
    // Korean
    public final static short                           I18N_KOREAN         = 7;
    // Danish
    public final static short                           I18N_DANISH         = 8;
    // Dutch
    public final static short                           I18N_DUTCH          = 9;
    // Swedish
    public final static short                           I18N_SWEDISH        = 10;
    // English (Pirate)
    public final static short                           I18N_ENGLISH_PIRATE = 11;
    // Brasil
    public final static short                           I18N_BRASIL         = 12;

    // languages are alphabetical in this array
    public static int[] LANGUAGECODES = new int[]{I18N_CHINESE,
    	I18N_DANISH,
    	I18N_DUTCH,
    	I18N_DEFAULT,
    	I18N_ENGLISH_PIRATE,
    	I18N_FRENCH,
    	I18N_GERMAN,
    	I18N_ITALIAN,
    	I18N_JAPANESE,
    	I18N_KOREAN,
    	I18N_BRASIL,
    	I18N_SPANISH,
    	I18N_SWEDISH};

    public final static short                           CSS_USER_DEFAULT    = 0;
    public final static short                           CSS_USER_1          = 1;
    public final static short                           CSS_USER_2          = 2;

    public final static short                           DATEFORMAT_YYYYMMDD = 0;
    public final static short                           DATEFORMAT_MMDDYYYY = 1;
    public final static short                           DATEFORMAT_DDMMYYYY = 2;

    public final static short                           TIMEFORMAT_12HOURS  = 0;
    public final static short                           TIMEFORMAT_24HOURS  = 1;
	/*
	 * add for new Teacher on TV page start
	 */
}