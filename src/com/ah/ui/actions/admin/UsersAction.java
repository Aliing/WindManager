package com.ah.ui.actions.admin;

/*
 * @author Chris Scheers
 */

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.sync.VhmUserSync;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveApFilter;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.performance.AhAlarmsFilter;
import com.ah.bo.performance.AhEventsFilter;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.UserSettingsUtil;
import com.ah.ws.rest.server.bussiness.UsersBussiness;
import com.opensymphony.xwork2.ActionContext;

public class UsersAction extends BaseAction implements QueryBo {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(UsersAction.class.getSimpleName());

	private int					timezone;

	private boolean				updateLocalUserGroup;

	private OptionsTransfer		localUserGroupOptions;

	private List<Long>			localUserGroups;

	private boolean				updateSSIDProfile;

	private OptionsTransfer		ssidProfileOptions;

	private List<Long>			ssidProfiles;

	private boolean 			expressFromSsid = false;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.user.new"))) {
					return getLstForward();
				}
				setSessionDataSource(new HmUser());
				if (isExpressFromSsid()) {
					getDataSource().setUserGroup(
							QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName",
									HmUserGroup.GM_OPERATOR, domainId));
					if (getDataSource().getUserGroup()!=null) {
						setSelectedId(getDataSource().getUserGroup().getId());
					}
				}
				prepareUserPage(true);
				if("ssid".equals(this.getLastExConfigGuide())) {
					return  "ssidEx";
				} else {
				return INPUT;
				}
			} else if (("create" + getLstForward()).equals(operation)) {
				setUserGroupAndPassword();

				if (getDataSource().getUserGroup().isTcUserGroup()) {
					long count = QueryUtil.findRowCount(HmUser.class, new FilterParams(
							"userGroup.groupName=:s1 and owner=:s2", new Object[] {
									HmUserGroup.TEACHER, getDataSource().getDomain() }));
					if (count >= 1024) {
						addActionError(MgrUtil.getUserMessage("action.error.teacher.users.larger.than.maxsupport",
								NmsUtil.getOEMCustomer().getNmsName()));
						prepareUserPage(true);
						return INPUT;
					}
				}

				if (checkNameExists("lower(userName)", getDataSource().getUserName().toLowerCase())) {
					prepareUserPage(true);
					if("ssid".equals(this.getLastExConfigGuide())) {
						return  "ssidEx";
					} else {
					return INPUT;
				}
				}

				// check email address
				if (checkEmailAddressUnique()) {
					prepareUserPage(true);
					if("ssid".equals(this.getLastExConfigGuide())) {
						return  "ssidEx";
					} else {
					return INPUT;
				}
				}

				setOptions4GMOperator();
				setTableColumns();

				try {
					if (NmsUtil.isHostedHMApplication()) {
						UserInfo userInfo = getUserInfo();

						RemotePortalOperationRequest.createVhmUser(userInfo);
					}
				} catch (Exception e) {
					log.error("execute", e.getMessage(), e);
					addActionError(e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName() + " "
							+ e.getMessage());
					try {
						if("ssid".equals(this.getLastExConfigGuide())) {
							prepareUserPage(true);
							return "ssidEx";
						} else {
						return prepareUsersList();
						}
					} catch (Exception ne) {
						return prepareEmptyBoList();
					}
				}
				if (("create").equals(operation)) {
					createBo(dataSource);
					return prepareUsersList();
				} else {
					id = createBo(dataSource);
					tvClassUserName = getDataSource().getUserName();
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				forward = editBo(this);
				if (dataSource != null) {
					prepareUserPage(false);
					log.info("execute", "User's group ID: "
							+ getDataSource().getUserGroup().getId());
					setSelectedId(getDataSource().getUserGroup().getId());
					
					if (getDataSource().getDefaultFlag()) {
						updateAdminUser = true;
						updateAdminId = getDataSource().getUserGroup().getId();
					}
				}
				return forward;
			} else if ("update".equals(operation)) {
				// check email address
				if (checkEmailAddressUnique()) {
					prepareUserPage(false);
					return INPUT;
				}

				if (dataSource != null) {
					setUserGroupAndPassword();
					setOptions4GMOperator();

					if (getDataSource().getDefaultFlag()) {
						// fix customer issue: CFD-153 Primary HMOL login does not have MyHive access or Device Inventory button 
						// default user can always access MyHive 
						getDataSource().setAccessMyhive(true);
					} else if (!HmUserGroup.CONFIG.equals(getDataSource().getUserGroup().getGroupName())) {
						getDataSource().setAccessMyhive(false);
					}
				}

				// update shell admin password if update admin user
				if (getDataSource().getUserName().trim().equals(HmUser.ADMIN_USER)
						&& getDataSource().getDomain().isHomeDomain()) {
					updateShellAdminPwd();
				}

				if (getDataSource().getUserName().equals(getUserContext().getUserName())
						&& getDataSource().getDomain().getDomainName().equals(
								getUserContext().getDomain().getDomainName())) {
					getUserContext().setTimeZone(getDataSource().getTimeZone());
					setSessionUserContext(getUserContext());
				}

				setTableColumns();

				// Call update even if dataSource is null, to make sure proper
				// exception will be thrown
				updateBo(dataSource);

				if (NmsUtil.isHostedHMApplication()) {
					UserInfo userInfo = getUserInfo();
					VhmUserSync.syncForModifyVhmUser(userInfo);
				}

				return prepareUsersList();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					setTableColumns();
					return prepareUsersList();
				}
			}
			/**
			 * yyy change for US1375/
			 *
			 */
			else if ("updateUser".equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
				    Map<String, Object> map=ActionContext.getContext().getParameters();
					String showValue = getUrlValue(map,"displayNav");
					String onClickValue= getUrlValue(map,"onclick");
					int fromIndex=onClickValue.lastIndexOf("operation=");
					int toIndex=onClickValue.lastIndexOf("\"");
					String L1MenuName=onClickValue.substring(fromIndex+10,toIndex);
					int origineValue=NavigationCustomizationUtil.getNavCustomizationByUser(this.userContext.getId(),this.userContext.getEmailAddress());
					boolean isNeedUpdate=NavigationCustomizationUtil.isNeedChange(origineValue,L1MenuName, Boolean.valueOf(showValue));
					if (isNeedUpdate) {
						int updateValue=NavigationCustomizationUtil.getUpdateValue(origineValue, L1MenuName, Boolean.valueOf(showValue));
						/*HmUser currentUser =QueryUtil.findBoById(HmUser.class, this.userContext.getId());
						if(currentUser!=null){
							currentUser.setNavCustomization(updateValue);
							QueryUtil.updateBo(currentUser);
						}*/
						// changed in Geneva, for user setting columns separated from hm_user
						UserSettingsUtil.updateNavCustomization(this.userContext.getEmailAddress(), updateValue);
						NavigationCustomizationUtil.updateNavCustomization(this.userContext.getEmailAddress(), updateValue);
					}
					return null;
				}
			}
			/**
			 * yyy change end for US1375.
			 */

			else {
				if ("remove".equals(operation)) {
					removeUserOperation();
				} else {
					baseOperation();
				}

				setTableColumns();

				return prepareUsersList();
			}
		} catch (Exception e) {
			log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName() + " "
					+ MgrUtil.getUserMessage(e));
			try {
				return prepareUsersList();
			} catch (Exception ne) {
				return prepareEmptyBoList();
			}
		}
	}

	private String getUrlValue(Map<String, Object> map,String key) {
		String[] value = (String[]) map.get(key);
		String trueValue=value[0];
		return trueValue;
	}

	private UserInfo getUserInfo() throws Exception {
		UserInfo info = new UserInfo();
		info.setEmailAddress(getDataSource().getEmailAddress());
		info.setFullname(getDataSource().getUserFullName());
		info.setPassword(MgrUtil.digest(adminPassword));
		info.setUsername(getDataSource().getUserName());
		info.setVhmName(getDomain().getDomainName());
		info.setGroupAttribute((short) getDataSource().getUserGroup().getGroupAttribute());
		info.setTimeZone(getDataSource().getTimeZone());
		info.setAccessMyHive(getDataSource().isAccessMyhive());
		info.setDefaultFlag(getDataSource().getDefaultFlag());
		return info;
	}

	private void removeUserOperation() throws Exception {
		boolean bln_user = isRemoveAdminUser();
		if (bln_user && getAllSelectedIds().size() <= 0) {
			addActionError(MgrUtil.getUserMessage("action.error.remove.default.user"));
		}

		boolean removeUserSelf = isRemoveUserSelf();
		if (removeUserSelf && getAllSelectedIds().size() <= 0) {
			addActionError(MgrUtil.getUserMessage("action.error.delete.userself"));
		}

		HmUser refedHmUser = isRemoveTeacherUserRefed();
		if (refedHmUser != null && getAllSelectedIds().size() <= 0) {
			addActionError(MgrUtil.getUserMessage("action.error.delete.refedteacheruser", refedHmUser.getUserName()));
		}

		if (getAllSelectedIds().size() <= 0) {
			return;
		}

		// cache selected user name list
		List<String> userNameList = new ArrayList<String>();
		List<String> userEmailList = new ArrayList<String>();
		for (Long userID : getAllSelectedIds()) {
			HmUser user = QueryUtil.findBoById(HmUser.class, userID);
			if (user != null) {
				userNameList.add(user.getUserName());
				userEmailList.add(user.getEmailAddress());
			}
		}

		// remove operation
		removeOperation();

		// filter not removed user
		for (Iterator<String> iter = userNameList.iterator(); iter.hasNext();) {
			String userName = iter.next();
			HmUser user = QueryUtil.findBoByAttribute(HmUser.class, "userName", userName,
					getDomain().getId());
			if (user != null) {
				iter.remove();
			}
		}

		// sync remove users
		if (NmsUtil.isHostedHMApplication()) {
			syncRemoveUser(userNameList);
		}

		// remove filter for user
		removeFilters(userNameList);

		// remove cached session
		removeSession(getAllSelectedIds());
		
		// add in Geneva for remove user settings
		for (Iterator<String> iter = userEmailList.iterator(); iter.hasNext();) {
			String userEmail = iter.next();
			HmUser user = QueryUtil.findBoByAttribute(HmUser.class, "lower(emailAddress)", StringUtils.lowerCase(userEmail),
					getDomain().getId());
			if (user != null) {
				iter.remove();
			}
		}
		UsersBussiness.removeUserSettings(userEmailList);
	}

	private void syncRemoveUser(List<String> userNameList) throws Exception {
		for (String userName : userNameList) {
			VhmUserSync.syncForRemoveVhmUser(getDomain().getDomainName(), userName);
		}
	}

	private void prepareUserPage(boolean isCreate) throws Exception {
		prepareUserGroups();
		initTimeZone(isCreate);
		initLocalUserGroups();
		initSSIDProfiles();
	}

	public void initLocalUserGroups() throws Exception {
		List<CheckItem> availableList = getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType", LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK));
		Collection<CheckItem> removeList = new Vector<CheckItem>();

		for (CheckItem obj : availableList) {
			for (LocalUserGroup userGroup : getDataSource().getLocalUserGroups()) {
				if (userGroup.getId().longValue() == obj.getId()) {
					removeList.add(obj);
				}
			}
		}

		availableList.removeAll(removeList);

		String leftTitle = isEasyMode() ? "admin.user.avaliable.ssidProfile"
				: "admin.user.avaliable.pskUserGroup";
		String rightTitle = isEasyMode() ? "admin.user.selected.ssidProfile"
				: "admin.user.selected.pskUserGroup";

		localUserGroupOptions = new OptionsTransfer(MgrUtil.getUserMessage(leftTitle), MgrUtil
				.getUserMessage(rightTitle), availableList, getDataSource().getLocalUserGroups(),
				"id", "value", "localUserGroups", 8, "200px", "8", true);
	}

	public void initSSIDProfiles() throws Exception {
		List<CheckItem> availableList = getBoCheckItems("ssidName", SsidProfile.class, null);
		Collection<CheckItem> removeList = new Vector<CheckItem>();

		for (CheckItem obj : availableList) {
			for (SsidProfile ssidProfile : getDataSource().getSsidProfiles()) {
				if (ssidProfile.getId().longValue() == obj.getId()) {
					removeList.add(obj);
				}
			}
		}

		availableList.removeAll(removeList);

		ssidProfileOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("admin.user.avaliable.ssidProfile"), MgrUtil
				.getUserMessage("admin.user.selected.ssidProfile"), availableList, getDataSource()
				.getSsidProfiles(), "id", "value", "ssidProfiles", 8, "200px", "8", true);
	}

	private String prepareUsersList() throws Exception {
		List<Object> lstCondition = new ArrayList<Object>();
		String searchSQL = "";

		HmDomain domain = QueryUtil.findBoById(HmDomain.class, getDomainId());
		if (!(domain.isSupportGM() && HmBeLicenseUtil.GM_LITE_LICENSE_VALID)) {
			searchSQL = "userGroup.groupName!=:s1  and userGroup.groupName!=:s2";
			lstCondition.add(HmUserGroup.GM_ADMIN);
			lstCondition.add(HmUserGroup.GM_OPERATOR);
		}

		HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class,
				"owner", domain);
		if (!(settings.isEnableTeacher() && NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED)) {
			if (lstCondition.size() > 0) {
				searchSQL += " and ";
			}

			searchSQL += "userGroup.groupName!=:s" + (lstCondition.size() + 1);
			lstCondition.add(HmUserGroup.TEACHER);
		}

		if (lstCondition.size() > 0) {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}

		return prepareBoList();
	}

	public boolean checkEmailAddressUnique() {
		List<?> boIds;
		if (getDataSource().getId() == null) {
			// create
			boIds = QueryUtil.executeQuery("select id from " + HmUser.class.getSimpleName(), null,
					new FilterParams("lower(emailAddress)", getDataSource().getEmailAddress()
							.toLowerCase()));
		} else {
			// update
			boIds = QueryUtil.executeQuery("select id from " + HmUser.class.getSimpleName(), null,
					new FilterParams("lower(emailAddress)=:s1 and id!=:s2", new Object[] {
							getDataSource().getEmailAddress().toLowerCase(),
							getDataSource().getId() }));
		}

		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("action.error.email.exist",getDataSource().getEmailAddress()));
			return true;
		} else {
			return false;
		}
	}

	public boolean isRemoveAdminUser() {
		Set<Long> ids = getAllSelectedIds();
		boolean bln = false;
		if (ids != null && ids.size() > 0) {
			for (Iterator<Long> iter = ids.iterator(); iter.hasNext();) {
				Long userId = iter.next();
				HmUser user = QueryUtil.findBoById(HmUser.class, userId);
				if (user != null)
					// if (user.getUserName().trim().equals(HmUser.ADMIN_USER)) {
					if (user.getDefaultFlag()) {
						iter.remove();
						setAllSelectedIds(ids);
						bln = true;
					}
			}
		}
		return bln;
	}

	public boolean isRemoveUserSelf() {
		Set<Long> ids = getAllSelectedIds();
		boolean bln = false;
		if (ids != null && ids.size() > 0) {
			for (Iterator<Long> iter = ids.iterator(); iter.hasNext();) {
				Long userId = iter.next();
				HmUser user = QueryUtil.findBoById(HmUser.class, userId);
				if (user != null)
					if (user.getUserName().trim().equals(getUserContext().getUserName())
							&& user.getDomain().getId().equals(getDomainId())) {
						iter.remove();
						setAllSelectedIds(ids);
						bln = true;
					}
			}
		}
		return bln;
	}

	public HmUser isRemoveTeacherUserRefed(){
		Set<Long> ids = getAllSelectedIds();
		HmUser refedUser = null;
		if (ids != null && ids.size() > 0) {
			for (Iterator<Long> iter = ids.iterator(); iter.hasNext();) {
				Long userId = iter.next();
				HmUser user = QueryUtil.findBoById(HmUser.class, userId);
				if (user != null)
					if (user.getUserGroup().isTcUserGroup()
							&& user.getDomain().getId().equals(getDomainId())
							&& QueryUtil.executeQuery(TvClass.class, null, new FilterParams("lower(teacherId)", StringUtils.lowerCase(user.getEmailAddress()))).size() > 0 ) {
						iter.remove();
						setAllSelectedIds(ids);
						refedUser = user;
					}
			}
		}
		return refedUser;
	}

	public boolean removeFilters(List<String> userNameList) {
		if (userNameList == null) {
			return true;
		}
		try {
			Set<Long> rmids = new HashSet<Long>();
			for (String userName : userNameList) {
				List<?> filterIds = QueryUtil.executeQuery("select id from "
						+ HiveApFilter.class.getSimpleName(), null, new FilterParams(
						"userName=:s1 and owner.id=:s2", new Object[] { userName,
								getDomain().getId() }));
				if (!filterIds.isEmpty()) {
					for (Object obj : filterIds)
						rmids.add(Long.valueOf(obj.toString()));
				}
			}
			QueryUtil.removeBos(HiveApFilter.class, rmids);
		} catch (Exception e) {
			log.error("removeFilters", "remove hive ap filters catch error", e);
			return false;
		}

		try {
			Set<Long> rmids = new HashSet<Long>();
			for (String userName : userNameList) {
				List<?> filterIds = QueryUtil.executeQuery("select id from "
						+ ActiveClientFilter.class.getSimpleName(), null, new FilterParams(
						"userName=:s1 and owner.id=:s2", new Object[] { userName,
								getDomain().getId() }));
				if (!filterIds.isEmpty()) {
					for (Object obj : filterIds)
						rmids.add(Long.valueOf(obj.toString()));
				}
			}
			QueryUtil.removeBos(ActiveClientFilter.class, rmids);
		} catch (Exception e) {
			log.error("removeFilters", "remove active client filters catch error", e);
			return false;
		}

		try {
			Set<Long> rmids = new HashSet<Long>();
			for (String userName : userNameList) {
				List<?> filterIds = QueryUtil.executeQuery("select id from "
						+ AhAlarmsFilter.class.getSimpleName(), null, new FilterParams(
						"userName=:s1 and owner.id=:s2", new Object[] { userName,
								getDomain().getId() }));
				if (!filterIds.isEmpty()) {
					for (Object obj : filterIds)
						rmids.add(Long.valueOf(obj.toString()));
				}
			}
			QueryUtil.removeBos(AhAlarmsFilter.class, rmids);
		} catch (Exception e) {
			log.error("removeFilters", "remove alarm filters catch error", e);
			return false;
		}

		try {
			Set<Long> rmids = new HashSet<Long>();
			for (String userName : userNameList) {
				List<?> filterIds = QueryUtil.executeQuery("select id from "
						+ AhEventsFilter.class.getSimpleName(), null, new FilterParams(
						"userName=:s1 and owner.id=:s2", new Object[] { userName,
								getDomain().getId() }));
				if (!filterIds.isEmpty()) {
					for (Object obj : filterIds)
						rmids.add(Long.valueOf(obj.toString()));
				}
			}
			QueryUtil.removeBos(AhEventsFilter.class, rmids);
			return true;
		} catch (Exception e) {
			log.error("removeFilters", "remove event filters catch error", e);
			return false;
		}

	}

	public void removeSession(Set<Long> ids) {
		if (ids == null) {
			return;
		}

		CurrentUserCache.getInstance().invalidateSessions(ids);
	}

	public String getDisplayName() {
		return getDataSource().getUserName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public boolean getDisabledEmail() {
		return NmsUtil.isHostedHMApplication() && getDataSource() != null
				&& getDataSource().getDefaultFlag();
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ADMINISTRATORS);
		setDataSource(HmUser.class);
		keyColumnId = COLUMN_USERNAME;
		tableId = HmTableColumn.TABLE_ADMINISTRATORS;
	}

	public HmUser getDataSource() {
		return (HmUser) dataSource;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_USERNAME		= 1;

	public static final int	COLUMN_USERFULLNAME	= 2;

	public static final int	COLUMN_EMAIL		= 3;

	public static final int	COLUMN_GROUPNAME	= 4;

	/**
	 * get the description of column by id
	 *
	 * @param id
	 *            -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_USERNAME:
			code = "admin.user.userName";
			break;
		case COLUMN_USERFULLNAME:
			code = "admin.user.userFullName";
			break;
		case COLUMN_EMAIL:
			code = "admin.user.emailAddress";
			break;
		case COLUMN_GROUPNAME:
			code = "admin.user.userGroup";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	/**
	 * set the description of columns
	 *
	 * @param columns
	 *            -
	 */
	public final void setColumnDescription(List<HmTableColumn> columns) {
		for (HmTableColumn column : columns) {
			column.setColumnDescription(getColumnDescription(column.getColumnId()));
			column.setTableId(tableId);
		}
	}

	protected void setTableColumns() {

		selectedColumns = getUserContext().getTableViews().get(tableId);

		if (selectedColumns == null) {
			selectedColumns = getDefaultSelectedColums();
		}

		setColumnDescription(selectedColumns);
		availableColumns = getDefaultSelectedColums();
		setColumnDescription(availableColumns);
		availableColumns.removeAll(selectedColumns);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_USERFULLNAME));
		columns.add(new HmTableColumn(COLUMN_EMAIL));
		columns.add(new HmTableColumn(COLUMN_GROUPNAME));

		return columns;
	}

	protected void setUserGroupAndPassword() throws Exception {
		if (selectedId == null)
			selectedId = updateAdminId;
		HmUserGroup userGroup = findBoById(HmUserGroup.class, selectedId);
		getDataSource().setUserGroup(userGroup);
		if (!userGroup.isAdministrator()) {
			// User owner should be same as group owner, except for super users.
			getDataSource().setOwner(userGroup.getOwner());
		}
		if (adminPassword.length() > 0) {
			getDataSource().setPassword(MgrUtil.digest(adminPassword));
		}

		// set timezone
		getDataSource().setTimeZone(HmBeOsUtil.getTimeZoneString(timezone));
	}

	private void setOptions4GMOperator() throws Exception {
		if (getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)) {
			// local user group
			if (updateLocalUserGroup) {
				Set<LocalUserGroup> groups = getDataSource().getLocalUserGroups();
				getDataSource().removeLocalUserGroups();
				groups.clear();

				if (localUserGroups != null) {

					for (Long id : localUserGroups) {
						LocalUserGroup group = findBoById(LocalUserGroup.class, id);
						if (group != null) {
							groups.add(group);
						}
					}

					if (groups.size() != localUserGroups.size()) {
						String tempStr[] = { getText("admin.user.avaliable.pskUserGroup") };
						addActionError(getText("info.ssid.warning.deleteRecord", tempStr));
					}
				}
				if (groups.size() > 0) {
					getDataSource().setLocalUserGroups(groups);
					getDataSource().addLocalUserGroups();
				}
			} else {
				getDataSource().removeLocalUserGroups();
				getDataSource().getLocalUserGroups().clear();
			}

			// ssid profile
			if (updateSSIDProfile) {
				Set<SsidProfile> profiles = getDataSource().getSsidProfiles();
				getDataSource().removeSsidProfiles();
				profiles.clear();

				if (ssidProfiles != null) {

					for (Long id : ssidProfiles) {
						SsidProfile profile = findBoById(SsidProfile.class, id);
						if (profile != null) {
							profiles.add(profile);
						}
					}

					if (profiles.size() != ssidProfiles.size()) {
						String tempStr[] = { getText("admin.user.avaliable.ssidProfile") };
						addActionError(getText("info.ssid.warning.deleteRecord", tempStr));
					}
				}
				if (profiles.size() > 0) {
					getDataSource().setSsidProfiles(profiles);
					getDataSource().addSsidProfiles();
				}
			} else {
				getDataSource().removeSsidProfiles();
				getDataSource().getSsidProfiles().clear();
			}
		} else {
			getDataSource().removeLocalUserGroups();
			getDataSource().getLocalUserGroups().clear();
			getDataSource().removeSsidProfiles();
			getDataSource().getSsidProfiles().clear();
		}

		if (isEasyMode()) {
			getDataSource().removeSsidProfiles();
			getDataSource().getSsidProfiles().clear();
		}
	}

	/**
	 * synchronize shell admin password with HM admin password
	 */
	private void updateShellAdminPwd() {
		try {
			String password = adminPassword;

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
			log.error("updateAdminShellPwd", "catch exception", e);
		}
	}

	public void prepareUserGroups() throws Exception {
		if (userGroups == null) {
			Long ownerID = (getDataSource().getDomain() == null) ? getDomainId() : getDataSource()
					.getDomain().getId();

			List<Object> lstCondition = new ArrayList<Object>();
			String searchSQL = "owner.id=:s1";
			lstCondition.add(ownerID);
			if ("tvClass".equalsIgnoreCase(getLstForward())) {
				searchSQL = searchSQL + " AND groupName=:s" + (lstCondition.size() + 1);
				lstCondition.add(HmUserGroup.TEACHER);
			} else {
				HmDomain domain = QueryUtil.findBoById(HmDomain.class, ownerID);
				if (!(domain.isSupportGM() && HmBeLicenseUtil.GM_LITE_LICENSE_VALID)) {
					searchSQL = searchSQL + " AND groupName!=:s2 AND groupName!=:s3";
					lstCondition.add(HmUserGroup.GM_ADMIN);
					lstCondition.add(HmUserGroup.GM_OPERATOR);
				}

				HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class,
						"owner", domain);
				if (!(settings.isEnableTeacher() && NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED)) {
					searchSQL = searchSQL + " AND groupName!=:s" + (lstCondition.size() + 1);
					lstCondition.add(HmUserGroup.TEACHER);
				}
			}

			SortParams sort = new SortParams("groupName"); // fix bug 28471, make group list order by group name
			userGroups = QueryUtil.executeQuery(HmUserGroup.class, sort, new FilterParams(
					searchSQL, lstCondition.toArray()));
		}
	}

//	public Long getGMOperatorID() {
//		for (HmUserGroup userGroup : userGroups) {
//			if (userGroup.getGroupName().equals(HmUserGroup.GM_OPERATOR)) {
//				return userGroup.getId();
//			}
//		}
//
//		return 0L;
//	}

	public void initTimeZone(boolean isCreate) {
		if (isCreate) {
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
		} else {
			// update user
			timezone = HmBeOsUtil.getServerTimeZoneIndex(getDataSource().getTimeZone());
		}
	}

	protected List<HmUserGroup>	userGroups;

	public List<HmUserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<HmUserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	private String	adminPassword;

	private boolean	updateAdminUser	= false;

	Long			updateAdminId;

	private String	tvClassUserName	= "";

	public Long getUpdateAdminId() {
		return updateAdminId;
	}

	public void setUpdateAdminId(Long updateAdminId) {
		this.updateAdminId = updateAdminId;
	}

	public boolean getUpdateAdminUser() {
		return updateAdminUser;
	}

	public int getUserNameLength() {
		return getAttributeLength("userName");
	}

	public int getPasswdLength() {
		return 32;
	}

	public int getUserFullNameLength() {
		return getAttributeLength("userFullName");
	}

	public int getEmailLength() {
		return getAttributeLength("emailAddress");
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the tvClassUserName
	 */
	public String getTvClassUserName() {
		return tvClassUserName;
	}

	/**
	 * @param tvClassUserName
	 *            the tvClassUserName to set
	 */
	public void setTvClassUserName(String tvClassUserName) {
		this.tvClassUserName = tvClassUserName;
	}

	public OptionsTransfer getLocalUserGroupOptions() {
		return localUserGroupOptions;
	}

	public void setLocalUserGroupOptions(OptionsTransfer localUserGroupOptions) {
		this.localUserGroupOptions = localUserGroupOptions;
	}

	public boolean isUpdateLocalUserGroup() {
		if (getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)
				&& !getDataSource().getLocalUserGroups().isEmpty()) {
			return true;
		}

		return updateLocalUserGroup;
	}

	public void setUpdateLocalUserGroup(boolean updateLocalUserGroup) {
		this.updateLocalUserGroup = updateLocalUserGroup;
	}

	public boolean isUpdateSSIDProfile() {
		if (getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)
				&& !getDataSource().getSsidProfiles().isEmpty()) {
			return true;
		}

		return updateSSIDProfile;
	}

	public void setUpdateSSIDProfile(boolean updateSSIDProfile) {
		this.updateSSIDProfile = updateSSIDProfile;
	}

	public OptionsTransfer getSsidProfileOptions() {
		return ssidProfileOptions;
	}

	public void setSsidProfileOptions(OptionsTransfer ssidProfileOptions) {
		this.ssidProfileOptions = ssidProfileOptions;
	}

	public List<Long> getLocalUserGroups() {
		return localUserGroups;
	}

	public void setLocalUserGroups(List<Long> localUserGroups) {
		this.localUserGroups = localUserGroups;
	}

	public List<Long> getSsidProfiles() {
		return ssidProfiles;
	}

	public void setSsidProfiles(List<Long> ssidProfiles) {
		this.ssidProfiles = ssidProfiles;
	}

	public String getHide4Operator() {
		if (getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHide4LocalUserGroup() {
		if (getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)
				&& !getDataSource().getLocalUserGroups().isEmpty()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHide4EasyMode() {
		if (isEasyMode()) {
			return "none";
		}

		return "";
	}

	public String getHide4SSIDProfile() {
		if (getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.GM_OPERATOR)
				&& !getDataSource().getSsidProfiles().isEmpty()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowUpAccessMyHive() {
		if (getVhmAdminUser() && getDataSource() != null && getDataSource().getUserGroup() != null
				&& getDataSource().getUserGroup().getGroupName().equals(HmUserGroup.CONFIG) && !getDataSource().getDefaultFlag()) {
			return "";
		} else {
			return "none";
		}
	}

	public boolean getVhmAdminUser() {
		return NmsUtil.isHostedHMApplication() && userContext.getSwitchDomain() == null && !userContext.getDomain().isHomeDomain()
		&& userContext.getDefaultFlag();
	}

	public String getLocalUserGroupText() {
		if (isEasyMode()) {
			return MgrUtil.getUserMessage("admin.user.limit.ssidProfile");
		} else {
			return MgrUtil.getUserMessage("admin.user.limit.localUserGroup");
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmUser) {
			dataSource = bo;
// cchen DONE
//			if (getDataSource().getSsidProfiles() != null) {
//				getDataSource().getSsidProfiles().size();
//			}
//			if (getDataSource().getLocalUserGroups() != null) {
//				getDataSource().getLocalUserGroups().size();
//			}
		}

		return null;
	}

	public boolean isExpressFromSsid() {
		return expressFromSsid;
	}

	public void setExpressFromSsid(boolean expressFromSsid) {
		this.expressFromSsid = expressFromSsid;
	}
}