package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MACAuth;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class MACAuthAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MACAuthAction.class.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_STUDENTID = 1;
	public static final int COLUMN_STUDENTNAME = 2;
	public static final int COLUMN_MACADDRESS = 3;
	public static final int COLUMN_SCHOOLID = 4;
	
	private int totalBindStu = 0;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_STUDENTID:
			code = "config.tv.studentId";
			break;
		case COLUMN_STUDENTNAME:
			code = "config.tv.studentName";
			break;
		case COLUMN_MACADDRESS:
			code = "config.macOrOui.macAddress";
			break;
		case COLUMN_SCHOOLID:
			code = "config.tv.schoolId";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	/**
	 * get all available columns
	 */
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_STUDENTID));
		columns.add(new HmTableColumn(COLUMN_STUDENTNAME));
		columns.add(new HmTableColumn(COLUMN_MACADDRESS));
		columns.add(new HmTableColumn(COLUMN_SCHOOLID));
		return columns;
	}

	/**
	 * get default selected columns
	 */
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_STUDENTID));
		columns.add(new HmTableColumn(COLUMN_STUDENTNAME));
		columns.add(new HmTableColumn(COLUMN_MACADDRESS));
		columns.add(new HmTableColumn(COLUMN_SCHOOLID));
		return columns;
	}

	private List<CheckItem> localUserGroup;

	private Long userGroupId;

	private List<Long> selectedMACAuthIds;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("macAuthList".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.macauth"))) {
					return getLstForward();
				}
				return prepareInitData();
			} else if ("addToGroup".equals(operation)
					|| ("addToGroup" + getLstForward()).equals(operation)) {
				updateMACAuthLocalUsers(operation);
				return prepareInitData();
			} else if ("removeFromGroup".equals(operation)
					|| ("removeremoveFromGroup" + getLstForward())
							.equals(operation)) {
				updateMACAuthLocalUsers(operation);
				return prepareInitData();
			} else if ("newGroup".equals(operation)
					|| "editGroup".equals(operation)) {
				prepareSaveAllObject();
				addLstForward("macAuth");
				clearErrorsAndMessages();
				return operation;
			} else if ("changeUserGroup".equals(operation)) {
				return prepareInitData();
			} else if ("refresh".equals(operation)) {
				return prepareInitData();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if (Navigation.L2_FEATURE_LOCAL_USER.equals(operation)) {
				setUpdateContext(true);
				return "localUserList";
			} else if ("continue".equals(operation)) {
				initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
				String result = prepareBoList();
				selectedMACAuthIds = (List<Long>) MgrUtil
						.getSessionAttribute(boClass.getSimpleName()
								+ "SourceIds");
				for (MACAuth macAuth : (List<MACAuth>) this.page) {
					macAuth.setSelected(false);
					for (Long selecteMACAuthId : selectedMACAuthIds) {
						if (macAuth.getId() == selecteMACAuthId) {
							macAuth.setSelected(true);
						}
					}
				}
				return result;
			} else {
				if (baseOperation()) {
					this.getSessionFiltering();
				}
				setSessionFiltering();
				return prepareInitData();
			}

		} catch (Exception e) {
			this.getSessionFiltering();
			setSessionFiltering();
			return prepareActionError(e);
		}
	}

	public void initLocalUserGroup(int userType) {
		setLocalUserGroup(getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType=:s1 and blnBulkType=:s2",
						new Object[] { userType, false }),
				BaseAction.CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO));
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LOCAL_USER);
		keyColumnId = COLUMN_STUDENTID;
		setDataSource(MACAuth.class);
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_LOCAL_USER_MACAUTH;
	}

	private String prepareInitData() throws Exception {
		initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
		String result = prepareBoList();
		preperCheckedUser();
		return result;
	}

	private void preperCheckedUser() {
		if (userGroupId == null){
			for (MACAuth macAuth : (List<MACAuth>) this.page) {
				macAuth.setSelected(false);
			}
			
			return;
		}
		List<LocalUser> localUsers = QueryUtil.executeQuery(LocalUser.class,
				null, new FilterParams("localUserGroup.id", userGroupId),
				domainId);
		totalBindStu = 0;
		for (MACAuth macAuth : (List<MACAuth>) this.page) {
			macAuth.setSelected(false);
			for (LocalUser localUser : localUsers) {
				if (localUser.getUserName().equals(macAuth.getMacAddress())) {
					macAuth.setSelected(true);
					totalBindStu++;
				}
			}
		}
	}

	public void prepareSaveAllObject() throws Exception {
		selectedMACAuthIds = new ArrayList<Long>();
		List<Long> selectedIds = getSelectedIds();
		if (selectedIds != null && selectedIds.size() != 0) {
			selectedMACAuthIds.addAll(selectedIds);
		}

		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "SourceIds",
				selectedMACAuthIds);
	}

	private void updateMACAuthLocalUsers(String operation) throws Exception {
		Set<Long> macAuthIds = null;
		if (allItemsSelected) {
			List<?> boIds = QueryUtil.executeQuery(
					"select id from " + boClass.getSimpleName(), null, null);
			macAuthIds = new HashSet<Long>(boIds.size());
			for (Object o : boIds) {
				macAuthIds.add((Long) o);
			}
			setAllSelectedIds(macAuthIds);

		} else if (getAllSelectedIds() != null
				&& !getAllSelectedIds().isEmpty()) {
			Collection<Long> operateIds = getAllSelectedIds();
			macAuthIds = new HashSet<Long>(operateIds.size());
			macAuthIds.addAll(operateIds);
		}
		if (macAuthIds == null)
			return;

		if ("addToGroup".equals(operation)) {
			addMACAuthToLocalUserGroup(macAuthIds);
		} else if ("removeFromGroup".equals(operation)) {
			removeMACAuthToLocalUserGroup(macAuthIds);
		}
	}

	private void addMACAuthToLocalUserGroup(Collection<Long> operateIds)
			throws Exception {
		List<MACAuth> operateMacAuthList = new ArrayList<MACAuth>();
		for (Long addId : operateIds) {
			operateMacAuthList.add(QueryUtil.findBoById(MACAuth.class, addId));
		}

		List<LocalUser> createLocalUsers = new ArrayList<LocalUser>();
		List<MACAuth> macObjContainer = new ArrayList<MACAuth>();		
		LocalUserGroup userGroup = getUserGroup();
		for (MACAuth macAuth : operateMacAuthList) {
			boolean existFlag = false;
			for(MACAuth existObj : macObjContainer){
				if(existObj.macAuthEquals(macAuth)){
					existFlag = true;
					break;
				}
			}
			
			if(existFlag){
				continue;
			}
			
			macObjContainer.add(macAuth);
			
			List<LocalUser> addedLocalUser = QueryUtil
					.executeQuery(LocalUser.class, null,
							new FilterParams(
									"userName=:s1 AND localUserGroup.id=:s2",
									new Object[] { macAuth.getMacAddress(),
											userGroupId }), domainId);
			if (addedLocalUser.size() == 0) {
				LocalUser localUser = new LocalUser();
				localUser.setLocalUserGroup(userGroup);
				localUser.setUserName(macAuth.getMacAddress());
				localUser.setLocalUserPassword(macAuth.getMacAddress());
				localUser.setUserType(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
				localUser.setOwner(getDomain());
				createLocalUsers.add(localUser);
			} 
		}

		if (createLocalUsers.size() > 0) {
			QueryUtil.bulkCreateBos(createLocalUsers);
		}
		
		int recordsCount = createLocalUsers.size();
		if(recordsCount > 0){
			addActionMessage(MgrUtil.getUserMessage("info.macauth.addtogroup",new String[]{String.valueOf(recordsCount),userGroup.getGroupName()}));
		}else{
			addActionMessage(MgrUtil.getUserMessage("info.macauth.addtogroup.nodata",new String[]{userGroup.getGroupName()}));
		}
		
	}

	private void removeMACAuthToLocalUserGroup(Collection<Long> removeIds)
			throws Exception {
		List<MACAuth> macAuthList = new ArrayList<MACAuth>();
		List<Long>  removedIds = new ArrayList<Long>();
		for (Long addId : removeIds) {
			macAuthList.add(QueryUtil.findBoById(MACAuth.class, addId));
		}

		for (MACAuth macAuth : macAuthList) {
			List<LocalUser> localUsers = QueryUtil
					.executeQuery(LocalUser.class, null,
							new FilterParams(
									"userName=:s1 AND localUserGroup.id=:s2",
									new Object[] { macAuth.getMacAddress(),
											userGroupId }), domainId);
			if (localUsers.size() > 0) {
				LocalUser removeLocalUser = localUsers.get(0);
				removedIds.add(removeLocalUser.getId());
			}
		}
		
		LocalUserGroup userGroup = getUserGroup();
		int removeSize = removedIds.size();
		if(removeSize > 0){
			Object bindings[] = new Object[1];
			bindings[0]= removedIds;
			FilterParams myFilterParams = new FilterParams("id in (:s1)",bindings);
			QueryUtil.bulkRemoveBos(LocalUser.class, myFilterParams);
			addActionMessage(MgrUtil.getUserMessage("info.macauth.removetogroup",new String[]{String.valueOf(removeSize),userGroup.getGroupName()}));
		}else{
			addActionMessage(MgrUtil.getUserMessage("info.macauth.removetogroup.nodata",new String[]{userGroup.getGroupName()}));
		}
	}

	private LocalUserGroup getUserGroup() throws Exception {
		if (userGroupId != null && userGroupId > -1) {
			LocalUserGroup userGroup = findBoById(LocalUserGroup.class,
					userGroupId);
			return userGroup;

		} else {
			return null;
		}
	}

	public MACAuth getDataSource() {
		return (MACAuth) dataSource;
	}

	public String getUserGroupTitle() {
		return MgrUtil.getUserMessage("config.localUser.userGroup");
	}

	public void setLocalUserGroup(List<CheckItem> localUserGroup) {
		this.localUserGroup = localUserGroup;
	}

	public List<CheckItem> getLocalUserGroup() {
		return localUserGroup;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public int getTotalBindStu() {
		return totalBindStu;
	}
	
	public String getTotalBindStuStyle(){
		return userGroupId == null || userGroupId == -1 ? "none" : "";
	}
}
