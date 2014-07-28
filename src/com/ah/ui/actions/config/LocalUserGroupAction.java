/**
 *@filename		LocalUserGroup.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-25 PM 03:59:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.ConfigTemplateAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class LocalUserGroupAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(LocalUserGroupAction.class
			.getSimpleName());
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_ATTRIBUTE = 2;
	public static final int COLUMN_VLAN = 3;
	public static final int COLUMN_TIME = 4;
	public static final int COLUMN_USERTYPE = 5;
	public static final int COLUMN_DESCRIPTION = 6;
	
	/**
	 * get the description of column by id
	 * @param id -
	 * @return String
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.localUserGroup.groupName";
			break;
		case COLUMN_ATTRIBUTE:
			code = "config.localUserGroup.profileId";
			break;
		case COLUMN_VLAN:
			code = "config.localUserGroup.vlanId";
			break;
		case COLUMN_TIME:
			code = "config.localUserGroup.reauthTime";
			break;
		case COLUMN_USERTYPE:
			code = "config.localUserGroup.userType";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.localUserGroup.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ATTRIBUTE));
		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_TIME));
		columns.add(new HmTableColumn(COLUMN_USERTYPE));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && "continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.localUserGroup.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new LocalUserGroup());
				initTimeZone();
				prepareDependentObjects();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "userGroupOnly", "localUserGroupJsonDlg");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
//				setNumbers();
				prepareSetSaveObjects();
				if (checkNameExists("groupName", getDataSource().getGroupName())) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				if (!checkPskCharacter()) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				if (!checkMaxPskUser()) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				if (!checkCreateUserName(null)) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				
				// timezone str
				getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimezone()));
				
				if ("create".equals(operation)) {
					this.getSessionFiltering();
					long createId =  createBo(dataSource);
					createOrUpdateLocalUser(createId);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("n", true);
						jsonObject.put("id", createId);
						jsonObject.put("t", true);
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					id = createBo(dataSource);
					createOrUpdateLocalUser(id);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo();
				if (dataSource != null) {
					addLstTitle(getText("config.title.localUserGroup.edit") + " '"
							+ getChangedName() + "'");
				}
				getDataSource().setTimezone(HmBeOsUtil.getServerTimeZoneIndex(getDataSource().getTimeZoneStr()));
				
				prepareDependentObjects();
				if (!getUpdateDisabled().equals("")){
					if (getDataSource().getUserType()== LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK 
						&& getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
						&& getDataSource().isBlnBulkType()){
						//can update
					} else {
						addActionMessage(MgrUtil.getUserMessage("message.cannot.modify.profile"));
					}
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(strForward, "userGroupOnly", "localUserGroupJsonDlg");
			} else if ("modify".equals(operation)){
				List<Long> lstSelectIds = getSelectedUserGroupIds();
				if (lstSelectIds != null && lstSelectIds.size() > 1) {
					setSessionDataSource(new LocalUserGroup());
					addLstTitle(getText("config.title.localUserGroup.modify.multi"));
				} else if (lstSelectIds != null)  {
					id=lstSelectIds.get(0);
					String strForward = editBo();
					addLstTitle(getText("config.title.localUserGroup.modify") + " '" + getChangedName() + "'");
					getDataSource().setTimezone(HmBeOsUtil.getServerTimeZoneIndex(getDataSource().getTimeZoneStr()));
					
					prepareDependentObjects();
					if (!getUpdateDisabled().equals("")){
						if (getDataSource().getUserType()== LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK 
							&& getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
							&& getDataSource().isBlnBulkType()){
							//can update
						} else {
							addActionMessage(MgrUtil.getUserMessage("message.cannot.modify.profile"));
						}
					}
					return getReturnPathWithJsonMode(strForward, "userGroupOnly", "localUserGroupJsonDlg");
				}
				//setIntCredentialType(0);
				MgrUtil.setSessionAttribute("selectLocalUserGroups",lstSelectIds);
				return "multiModify";
			} else if ("updateMulti".equals(operation)){
				if (strReauthTime.equals(getStrNoChange()) 
						&& strUserProfileId.equals(getStrNoChange())
						&& strVlanId.equals(getStrNoChange())
						&& strDescription.equals(getStrNoChange())
						&& getIntCredentialType()==0){
					this.getSessionFiltering();
					return prepareBoList();
				}
				multiUpdateOperation();
				this.getSessionFiltering();
				return prepareBoList();
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
//					setNumbers();
					prepareSetSaveObjects();
				}
				if (!checkPskCharacter()) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				
				if (!checkMaxPskUser()) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				
				if (!checkCreateUserName(getDataSource().getId())) {
					prepareDependentObjects();
					return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
				}
				
				// timezone str
				getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimezone()));
				
				if ("update".equals(operation)) {
					this.getSessionFiltering();
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						if (isJsonMode()){
							addActionError(MgrUtil.getUserMessage(e));
							generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
									+ " " + MgrUtil.getUserMessage(e));
							prepareDependentObjects();
							return getReturnPathWithJsonMode(INPUT,"userGroupOnly", "localUserGroupJsonDlg");
						} else {
							throw e;
						}
					}
					createOrUpdateLocalUser(getDataSource().getId());
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("n", false);
						jsonObject.put("t", true);
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					updateBo(dataSource);
					createOrUpdateLocalUser(getDataSource().getId());
					setUpdateContext(true);
					return getLstForward();
				}			
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				LocalUserGroup profile = (LocalUserGroup) findBoById(boClass,
						cloneId);
				profile.setId(null);
				profile.setGroupName("");
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setTimezone(HmBeOsUtil.getServerTimeZoneIndex(profile.getTimeZoneStr()));
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.localUserGroup.new"));
				prepareDependentObjects();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if ("cancel".equals(operation)){
					this.getSessionFiltering();
					return getLstForward();
				}
				setUpdateContext(true);
				return getLstForward();
			} else if ("newSchedule".equals(operation) || "editSchedule".equals(operation)) {
//				setNumbers();
				prepareSetSaveObjects();
				addLstForward("localUserGroup");
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (dataSource == null) {
					this.getSessionFiltering();
					String str = prepareBoList();
					return getReturnPathWithJsonMode(str, "userGroupOnly", "localUserGroupJsonDlg");
				} else {
					setId(dataSource.getId());
					prepareDependentObjects();
					blnShowOption=true;
					return getReturnPathWithJsonMode(INPUT, "userGroupOnly", "localUserGroupJsonDlg");
				}
			} else if ("import".equals(operation)) {
				addLstForward("localUserGroup");
				clearErrorsAndMessages();
				return operation;
			} else if ("search".equals(operation)) {
				if (getFilterUserType().intValue() != -1){
					Object lstCondition[] = new Object[2];
					lstCondition[0]=getFilterUserType().intValue();
					lstCondition[1]="%" + getFilterUserGroupName().trim().toLowerCase() + "%";
					filterParams = new FilterParams("userType=:s1 and lower(groupName) like :s2",lstCondition);
				} else {
					Object lstCondition[] = new Object[1];
					lstCondition[0]="%" + getFilterUserGroupName().trim().toLowerCase() + "%";
					filterParams = new FilterParams("lower(groupName) like :s1",lstCondition);
				}
				setSessionFiltering();
				baseOperation();
				return prepareBoList();
			} else if("filterRadiusGroup".equals(operation)){
				filterParams = new FilterParams("userType",LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
				setSessionFiltering();
				return prepareBoList();
			} else if("filterPskGroup".equals(operation)){
				List<Integer> psks = new ArrayList<Integer>();
				psks.add(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				psks.add(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
				filterParams = new FilterParams("userType",psks);
				setSessionFiltering();
				return prepareBoList();
			} else {
				if ("remove".equals(operation)) {
					if (checkLocalUserGroupUsed()){
						removeLocalUserGroupOperation();
					}
				}
				if (!baseOperation()) {
					filterParams=null;
					setSessionFiltering();
				} else {
					this.getSessionFiltering();
				}
				return prepareBoList();
			}
		} catch (Exception e) {
			this.getSessionFiltering();
			return prepareActionError(e);
		}
	}
	
	protected boolean checkCreateUserName(Long createId) throws Exception {
		if (createId != null) {
			if(getDataSource().getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
				QueryUtil.bulkRemoveBos(LocalUser.class, new FilterParams("localUserGroup.id",createId));
			}
		}
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE){
				if (getDataSource().isBlnBulkType()){
					DecimalFormat df = new DecimalFormat("0000");
					Set<String> generateUserName = new HashSet<String>();
					for(int i=1;i<=getDataSource().getBulkUserCount();i++){
						generateUserName.add(getDataSource().getUserNamePrefix()
								+ df.format(i));
					}
					Object bindings[] = new Object[2];
					bindings[0] = generateUserName;
					bindings[1] = getDomain().getId();
					if (generateUserName.size() > 0) {
						long existCount = QueryUtil.findRowCount(LocalUser.class,
								new FilterParams("userName in (:s1) and owner.id=:s2", bindings));
						if (existCount > 0) {
							addActionError(MgrUtil.getUserMessage("error.objectExistsForBulkUser"));
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	protected void createOrUpdateLocalUser(long createId) throws Exception {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE){
				if (getDataSource().isBlnBulkType()){
					LocalUserGroup localUserGroup = findBoById(LocalUserGroup.class, createId);
					List<LocalUser> createLst = new ArrayList<LocalUser>();
					for(int i=1;i<=getDataSource().getBulkUserCount();i++){
						LocalUser tmpUser = new LocalUser();
						tmpUser.setDefaultFlag(true);
						tmpUser.setOwner(getDomain());
						tmpUser.setLocalUserGroup(localUserGroup);
						tmpUser.setUserType(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
						DecimalFormat df = new DecimalFormat("0000");
						tmpUser.setUserName(localUserGroup.getUserNamePrefix()
								+ df.format(i));
						createLst.add(tmpUser);
					}
					if (!createLst.isEmpty()) {
						QueryUtil.bulkCreateBos(createLst);
					}
				}
			}
		}
	}
	
	protected boolean checkLocalUserGroupUsed() throws Exception {
		if (allItemsSelected) {
			this.getSessionFiltering();
			String strSql = "select a.ssid_profile_id from ssid_local_user_group a, ssid_profile b where a.ssid_profile_id=b.id and b.owner='"+ getDomain().getId()+"'";
			if (filterParams!=null && filterParams.getBindings() !=null ) {
				if (filterParams.getBindings().length>1) {
					String strtype = filterParams.getBindings()[0].toString();
					String strname = filterParams.getBindings()[1].toString();
					strSql = "select a.ssid_profile_id from ssid_local_user_group a, ssid_profile b, local_user_group c " +
							"where a.ssid_profile_id=b.id and a.local_user_group_id = c.id " +
							"and c.usertype =" + strtype + " and lower(c.groupname) like '"+ NmsUtil.convertSqlStr(strname) + "' and b.owner='"+ getDomain().getId()+"'";
				} else {
					String strname = filterParams.getBindings()[0].toString();
					strSql = "select a.ssid_profile_id from ssid_local_user_group a, ssid_profile b, local_user_group c " +
							"where a.ssid_profile_id=b.id and a.local_user_group_id = c.id " +
							"and lower(c.groupname) like '"+ NmsUtil.convertSqlStr(strname) + "' and b.owner='"+ getDomain().getId()+"'";
				}
			}			
			List<?> boIds = QueryUtil.executeNativeQuery(strSql);
			if (boIds.size()>0) {
				return false;
			}
		} else if (getAllSelectedIds() != null && !getAllSelectedIds().isEmpty()) {
			String inSql = "";
			boolean addBefore = false;
			for(long id: getAllSelectedIds()){
				if (!addBefore){
					inSql=inSql + id;
				} else {
					inSql=inSql + "," + id;
				}
			}
			List<?> boIds = QueryUtil.executeNativeQuery("select * from ssid_local_user_group where local_user_group_id in ("+ inSql+")");
			if (boIds.size()>0) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean removeLocalUserGroupOperation() throws Exception {
		if (allItemsSelected) {
			FilterParams myfilterParams = null;
			if (filterParams!=null && filterParams.getBindings() != null) {
				if (filterParams.getBindings().length>1) {
					myfilterParams = new FilterParams("localUserGroup.userType=:s1 and lower(localUserGroup.groupName) like :s2",filterParams.getBindings());
				} else {
					myfilterParams = new FilterParams("lower(localUserGroup.groupName) like :s1",filterParams.getBindings());
				}
			}
			if (getShowDomain()) {
				removeAllBos(LocalUser.class, myfilterParams,
						getNonHomeDataInHomeDomain());
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_NONHOME_DOMAIN_VALUE));
			} else {
				//Collection<Long> defaultIds = getDefaultIds();
				removeAllBos(LocalUser.class, myfilterParams, null);
			}
		} else if (getAllSelectedIds() != null && !getAllSelectedIds().isEmpty()) {
//			Collection<Long> defaultIds = getDefaultIds();
//			if (defaultIds != null && getAllSelectedIds().removeAll(defaultIds)) {
//				addActionMessage(MgrUtil
//						.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
//			}
			Object bindings[] = new Object[1];
			bindings[0]= getAllSelectedIds();
			FilterParams myFilterParams = new FilterParams("localUserGroup.id in (:s1)",bindings);
			QueryUtil.bulkRemoveBos(LocalUser.class, myFilterParams);
		}
		return true;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LOCAL_USER_GROUP);
		setDataSource(LocalUserGroup.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_LOCAL_GROUP;
	}

	@Override
	public LocalUserGroup getDataSource() {
		return (LocalUserGroup) dataSource;
	}
	
//	private void setNumbers() throws Exception {
//		getDataSource().setUserProfileId(
//				"".equals(strUserProfileId) ? -1 : Integer
//						.valueOf(strUserProfileId));
//		getDataSource().setVlanId(
//				"".equals(strVlanId) ? -1 : Integer.valueOf(strVlanId));
//		getDataSource().setReauthTime(
//				"".equals(strReauthTime) ? -1 : Integer.valueOf(strReauthTime));
//	}

	public void prepareDependentObjects() throws Exception {
		schedulerProfiles = getBoCheckItems("schedulerName", Scheduler.class,
				null);
		if (schedulerId == null && getDataSource().getSchedule() != null) {
			schedulerId = getDataSource().getSchedule().getId();
		}
		
		Date tmpStartTime = getDataSource().getStartTime();
		int st=0;
		int se=0;
		if (tmpStartTime != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(tmpStartTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK && 
				getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE &&
				getDataSource().isBlnBulkType()) {
				st = calendar.get(Calendar.DST_OFFSET);
				startTimeBulk = formatter.format(tmpStartTime);
				startHourBulk = calendar.get(Calendar.HOUR_OF_DAY);
				startMinBulk = calendar.get(Calendar.MINUTE);
				blnStartTime=false;
			} else {
				startTime = formatter.format(tmpStartTime);
				startHour = calendar.get(Calendar.HOUR_OF_DAY);
				startMin = calendar.get(Calendar.MINUTE);
				blnStartTime = true;
			}
		} else {
			blnStartTime = false;
		}

		Date tmpEndTime = getDataSource().getExpiredTime();
		if (tmpEndTime != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(tmpEndTime);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK && 
					getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE &&
					getDataSource().isBlnBulkType()) {
				 	se = calendar.get(Calendar.DST_OFFSET);
					lifeTimeDay = (int)((tmpEndTime.getTime() - st + se - tmpStartTime.getTime())/(1000L*60*60*24));
					lifeTimeHour = (int)((tmpEndTime.getTime() - st + se - tmpStartTime.getTime()-1000L*60*60*24*lifeTimeDay)/(60*1000*60));
					lifeTimeMin = (int)((tmpEndTime.getTime() - st + se - tmpStartTime.getTime()-1000L*60*60*24*lifeTimeDay - 1000L*60*60*lifeTimeHour)/(60*1000));
					blnStartTime=false;
			} else {
				endTime = formatter.format(tmpEndTime);
				endHour = calendar.get(Calendar.HOUR_OF_DAY);
				endMin = calendar.get(Calendar.MINUTE);
				blnEndTime = true;
			}
		} else {
			blnEndTime = false;
		}
	}

	public void prepareSetSaveObjects() throws Exception {
		if (schedulerId != null && schedulerId > 0) {
			Scheduler schedulerClass = findBoById(Scheduler.class,
					schedulerId);
			if (schedulerClass != null) {
				getDataSource().setSchedule(schedulerClass);
			}
		}
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK && 
				getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE &&
				getDataSource().isBlnBulkType()) {
			if (startTimeBulk != null && !startTimeBulk.equals("")) {
				String datetime[] = startTimeBulk.split("-");
				Calendar calendar = Calendar.getInstance();
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
				calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
				calendar.set(Calendar.HOUR_OF_DAY, startHourBulk);
				calendar.set(Calendar.MINUTE, startMinBulk);
				getDataSource().setStartTime(calendar.getTime());
				calendar.add(Calendar.DAY_OF_MONTH, lifeTimeDay);
				calendar.add(Calendar.HOUR_OF_DAY, lifeTimeHour);
				calendar.add(Calendar.MINUTE, lifeTimeMin);
				getDataSource().setExpiredTime(calendar.getTime());
			} else {
				getDataSource().setStartTime(null);
				getDataSource().setExpiredTime(null);
			}
		} else {
			if (blnStartTime && startTime != null && !startTime.equals("")) {
				String datetime[] = startTime.split("-");
				Calendar calendar = Calendar.getInstance();
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
				calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
				calendar.set(Calendar.HOUR_OF_DAY, startHour);
				calendar.set(Calendar.MINUTE, startMin);
				getDataSource().setStartTime(calendar.getTime());
			} else {
				getDataSource().setStartTime(null);
			}
	
			if (blnEndTime && endTime != null && !endTime.equals("")) {
				String datetime[] = endTime.split("-");
				Calendar calendar = Calendar.getInstance();
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
				calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
				calendar.set(Calendar.HOUR_OF_DAY, endHour);
				calendar.set(Calendar.MINUTE, endMin);
				getDataSource().setExpiredTime(calendar.getTime());
			} else {
				getDataSource().setExpiredTime(null);
			}
		}
		
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			if (getDataSource().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ALWAYS) {
				getDataSource().setStartTime(null);
				getDataSource().setExpiredTime(null);
				getDataSource().setSchedule(null);
			}
			if (getDataSource().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ONCE) {
				getDataSource().setSchedule(null);
			}
			if (getDataSource().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) {
				if (getDataSource().isBlnBulkType()) {
					getDataSource().setSchedule(null);
				} else {
					getDataSource().setStartTime(null);
					getDataSource().setExpiredTime(null);
				}
			}
		} else if(getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			getDataSource().setSchedule(null);
			if (getDataSource().getValidTimeType() != LocalUserGroup.VALIDTYME_TYPE_ONCE) {
				getDataSource().setStartTime(null);
				getDataSource().setExpiredTime(null);
			}
		} else {
			getDataSource().setValidTimeType(LocalUserGroup.VALIDTYME_TYPE_ALWAYS);
			getDataSource().setSchedule(null);
			getDataSource().setStartTime(null);
			getDataSource().setExpiredTime(null);
		}
		
		getDataSource().setVlanId(
				"".equals(strOneVlanId) ? -1 : Integer.valueOf(strOneVlanId));
		
		getDataSource().setUserProfileId(
				"".equals(strOneUserProfileId) ? -1 : Integer.valueOf(strOneUserProfileId));
	}
	
	// while accept, update, multi-modify, must using this method to get
	// the real selected Ids.
	private List<Long> getSelectedUserGroupIds() {
		if (allItemsSelected) {
			getSessionFiltering();
			if (domainId == null) {
				domainId = QueryUtil.getDependentDomainFilter(userContext);
			}
			List<?> ids = QueryUtil.executeQuery("select id from "
					+ boClass.getSimpleName(), null, filterParams, domainId);
			List<Long> selectedIds = new ArrayList<Long>(ids.size());

			for (Object obj : ids) {
				selectedIds.add((Long) obj);
			}

			return selectedIds;
		} else {
			Set<Long> allSelectedIds = getAllSelectedIds();
			List<Long> sIds = new ArrayList<Long>();
			if (null != allSelectedIds) {
				sIds.addAll(allSelectedIds);
			}
			return sIds;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void multiUpdateOperation() throws Exception{
		List<Long> selectLocalUserGroups = (List<Long>) MgrUtil
			.getSessionAttribute("selectLocalUserGroups");
		
		for (Long id : selectLocalUserGroups) {
			LocalUserGroup localUserGroup = findBoById(LocalUserGroup.class, id);
			if (!getStrReauthTime().equals(getStrNoChange())){
				localUserGroup.setReauthTime(Integer.valueOf(strReauthTime));
			}
			if (!getStrUserProfileId().equals(getStrNoChange())){
				if (strUserProfileId.equals("")){
					localUserGroup.setUserProfileId(-1);
				} else {
					localUserGroup.setUserProfileId(Integer.valueOf(strUserProfileId));
				}
			}
			if (!getStrVlanId().equals(getStrNoChange())){
				if (strVlanId.equals("")){
					localUserGroup.setVlanId(-1);
				} else {
					localUserGroup.setVlanId(Integer.valueOf(strVlanId));
				}
			}
			if (!getStrDescription().equals(getStrNoChange())){
				localUserGroup.setDescription(strDescription);
			}
			if (getIntCredentialType()!=0){
				if (localUserGroup.getUserType()==LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
					localUserGroup.setCredentialType(getIntCredentialType());
				} else {
					localUserGroup.setCredentialType(LocalUserGroup.USERGROUP_CREDENTIAL_FLASH);
				}
			}
			
			try {
				setId(id);
				updateBo(localUserGroup);
//				addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, localUserGroup
//						.getLabel()));
//				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Update Local User Group ("
//						+ localUserGroup.getLabel() + ")");
			} catch (RuntimeException e) {
//				generateAuditLog(HmAuditLog.STATUS_FAILURE, "Update Local User Group ("
//						+ localUserGroup.getLabel() + ")");
				throw new RuntimeException(e);
			}
		}
	}
	
	public boolean checkMaxPskUser(){
		if (getDataSource().getBulkUserCount()>LocalUser.MAX_COUNT_AP30_LOCALUSER){
			addActionError(getText("error.template.morePskUsers"));
			return false;
		}
		
		if (getDataSource().getUserType()== LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK 
				&& getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
				&& getDataSource().isBlnBulkType()){
			if (getDataSource().getId()!=null) {
				long oldCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams("localUserGroup.id", getDataSource().getId()));
				if (getDataSource().getBulkUserCount()-oldCount>0){
					if (!checkRelativedSsid(getDataSource(), getDataSource().getBulkUserCount()-oldCount)) {
						return false;
					}
					if (!checkRelativedTemplate(getDataSource(),getDataSource().getBulkUserCount()-oldCount)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean checkRelativedSsid(LocalUserGroup group, long newCount) {
		boolean result = true;
		long start = System.currentTimeMillis();
		int ssidCount = 0;
		Set<Long> ssidIds = ConfigurationUtils.getSsidProfiles(
				group);
		if (!ssidIds.isEmpty()) {
			for (Long ssidId : ssidIds) {
				ssidCount++;
				SsidProfile ssid = QueryUtil.findBoById(
						SsidProfile.class, ssidId, new SsidProfilesAction());
				long count = SsidProfilesAction.getPskUserCount(ssid);
				if (count + newCount > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
					addActionError(getText(
							"error.ssid.pskUser.overflow.createMoreUser",
							new String[] { ssid.getSsidName() }));
					result = false;
					break;
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("checkRelativedSsid", "add new psk user check "+ ssidCount +" SSID for PSK cost:"+(end-start)+"ms.");
		return result;
	}

	private boolean checkRelativedTemplate(LocalUserGroup group, long newCount) {
		boolean result = true;
		long start = System.currentTimeMillis();
		int templateCount = 0;
		List<Long> templateIds = ConfigurationUtils.getConfigTemplates(group);

		if (!templateIds.isEmpty()) {
			// used to calculate how many times the new count should be.
			Map<Long, Integer> times = new HashMap<Long, Integer>();
			for (Long templateId : templateIds) {
				if (null == times.get(templateId)) {
					times.put(templateId, 0);
				}
				times.put(templateId, times.get(templateId) + 1);
			}
			for (Long templateId : times.keySet()) {
				templateCount++;
				ConfigTemplate template = QueryUtil
						.findBoById(ConfigTemplate.class, templateId,
								new ConfigTemplateAction());
				int newTimes =0;
				if (template.getSsidInterfaces()!=null) {
					for (ConfigTemplateSsid configSsid : template.getSsidInterfaces()
							.values()) {
						if (configSsid.getSsidProfile()!=null && configSsid.getSsidProfile().getLocalUserGroups()!=null) {
							for(LocalUserGroup userGroup:configSsid.getSsidProfile().getLocalUserGroups()){
								if (null != userGroup && userGroup.getId().equals(group.getId())) {
									newTimes++;
								}
							}
						}
					}
				}
				
				long count = ConfigTemplateAction.getTotalPmkUserSize(template);
				if ((count + newCount*newTimes) > LocalUser.MAX_COUNT_AP30_USERPERAP) {
					addActionError(getText(
							"error.template.pskUser.overflow.createMoreUser",
							new String[] { template.getConfigName() }));
					result = false;
				}
				
				long pskCount = ConfigTemplateAction.getTotalPSKUserSize(template);
				if ((pskCount + newCount) > LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP) {
					addActionError(getText(
							"error.template.pskUser.overflow.createMoreUser.psk",
							new String[] { template.getConfigName() }));
					result = false;
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("checkRelativedTemplate", "add new psk user check "+ templateCount +" template for PSK cost:"+(end-start)+"ms.");
		return result;
	}
	
	public boolean checkPskCharacter(){
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String digStr = "1234567890";
		String spcStr = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		if (getDataSource().getUserType()== LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME){
				boolean blnStr = false;
				boolean blnDig = false;
				boolean blnSpc = false;
				char[] psbyte = (getDataSource().getUserNamePrefix()+"0001" + getDataSource().getConcatenateString()).toCharArray();
	
				for(char onebyte: psbyte){
					if (str.indexOf(onebyte)!=-1){
						blnStr = true;
						break;
					}
				}
				for(char onebyte: psbyte){
					if (digStr.indexOf(onebyte)!=-1){
						blnDig = true;
						break;
					}
				}
				for(char onebyte: psbyte){
					if (spcStr.indexOf(onebyte)!=-1){
						blnSpc = true;
						break;
					}
				}
				
				if (getDataSource().getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
					if (!blnStr == getDataSource().getBlnCharLetters()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.letter"));
							return false;
						}
					}
					if (!blnDig == getDataSource().getBlnCharDigits()){
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.digit"));
							return false;
						}
					}
					if (!blnSpc == getDataSource().getBlnCharSpecial()){
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.char"));
							return false;
						}
					}
				}
				if (getDataSource().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_OR){
					if (!getDataSource().getBlnCharLetters() && blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.letter"));
						return false;
					}
					if (!getDataSource().getBlnCharDigits() && blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.digit"));
						return false;
					}
					if (!getDataSource().getBlnCharSpecial() && blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.char"));
						return false;
					}
				}

				if (getDataSource().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
					if (getDataSource().getBlnCharLetters()){
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.digit"));
							return false;
						}
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.char"));
							return false;
						}
					} else if (getDataSource().getBlnCharDigits()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.letter"));
							return false;
						}
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.char"));
							return false;
						}
					} else if (getDataSource().getBlnCharSpecial()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.letter"));
							return false;
						}
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.user.name.prefix.contain.digit"));
							return false;
						}
					}
				}
			}
		}
		if (getDataSource().getUserType()== LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK){
			if (getDataSource().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME){
				boolean blnStr = false;
				boolean blnDig = false;
				boolean blnSpc = false;
				char[] psbyte = getDataSource().getConcatenateString().toCharArray();
	
				for(char onebyte: psbyte){
					if (str.indexOf(onebyte)!=-1){
						blnStr = true;
						break;
					}
				}
				for(char onebyte: psbyte){
					if (digStr.indexOf(onebyte)!=-1){
						blnDig = true;
						break;
					}
				}
				for(char onebyte: psbyte){
					if (spcStr.indexOf(onebyte)!=-1){
						blnSpc = true;
						break;
					}
				}
				
				if (getDataSource().getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
					if (!blnStr == getDataSource().getBlnCharLetters()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.letter"));
							return false;
						}
					}
					if (!blnDig == getDataSource().getBlnCharDigits()){
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.digit"));
							return false;
						}
					}
					if (!blnSpc == getDataSource().getBlnCharSpecial()){
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.char"));
							return false;
						}
					}
				}
				if (getDataSource().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_OR){
					if (!getDataSource().getBlnCharLetters() && blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.letter"));
						return false;
					}
					if (!getDataSource().getBlnCharDigits() && blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.digit"));
						return false;
					}
					if (!getDataSource().getBlnCharSpecial() && blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.char"));
						return false;
					}
				}

				if (getDataSource().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
					if (getDataSource().getBlnCharLetters()){
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.digit"));
							return false;
						}
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.char"));
							return false;
						}
					} else if (getDataSource().getBlnCharDigits()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.letter"));
							return false;
						}
						if (blnSpc){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.char"));
							return false;
						}
					} else if (getDataSource().getBlnCharSpecial()){
						if (blnStr){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.letter"));
							return false;
						}
						if (blnDig){
							addActionError(MgrUtil.getUserMessage("action.error.concatenating.string.contain.digit"));
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private JSONObject jsonObject;

	@Override
	public String getJSONString() {
		return jsonObject.toString();
	}

	private String strUserProfileId = "[-No Change-]";
	private String strVlanId = "[-No Change-]";
	private String strReauthTime ="[-No Change-]";
	private String strDescription ="[-No Change-]";
	private int intCredentialType=0;
	private final String strNoChange="[-No Change-]";
	
	private String strOneVlanId = "";
	private String strOneUserProfileId ="";
	
	private List<CheckItem> schedulerProfiles;
	private Long schedulerId;
	private String startTime;
	private int startHour;
	private int startMin;
	private String endTime;
	private int endHour;
	private int endMin;
	private boolean blnStartTime = false;
	private boolean blnEndTime = false;
	private boolean blnShowOption = false;
	
	
	private String startTimeBulk;
	private int startHourBulk;
	private int startMinBulk;
	
	private int lifeTimeDay;
	private int lifeTimeHour;
	private int lifeTimeMin;
	
	
	private Long			filterUserType;
	private String			filterUserGroupName;

	private boolean changeUserTypeFlag = false;
	
	public List<CheckItem> getFilterUserTypes(){
		List<CheckItem> lstCheckItem = new ArrayList<CheckItem>();
		lstCheckItem.add(new CheckItem((long) LocalUserGroup.USERGROUP_USERTYPE_RADIUS,"RADIUS"));
		lstCheckItem.add(new CheckItem((long) LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,"Private PSK-Auto"));
		lstCheckItem.add(new CheckItem((long) LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK,"Private PSK-Manual"));
		return lstCheckItem;
	}
	
	public EnumItem[] getEnumPskGenerateMethod(){
		return LocalUserGroup.ENUM_PSK_METHOD;
	}
	public EnumItem[] getEnumValidTimeType(){
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK){
			return MgrUtil .enumItems("enum.validTime.type.", new int[] { 
					LocalUserGroup.VALIDTYME_TYPE_ALWAYS, LocalUserGroup.VALIDTYME_TYPE_ONCE});
		}
		return LocalUserGroup.ENUM_VALIDTIME_TYPE;
	}
	public EnumItem[] getLstHours() {
		return SchedulerAction.ENUM_HOURS;
	}
	public EnumItem[] getLstMins() {
		return SchedulerAction.ENUM_MINUTES;
	}
	
	public EnumItem[] getLstPersonPskCombo() {
		return LocalUserGroup.ENUM_PSKFORMAT_COMBO_TYPE;
	}
	
	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}
	
	public int getGroupNameLength() {
		return getAttributeLength("groupName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getGroupName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public String getGenenatePassword() {
		return "genenatePassword";
	}
	
	public boolean getChangeDisabled(){
		if (getDataSource() == null || getDataSource().getId() == null){
			return false;
		}
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
				&& getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
				&& getDataSource().isBlnBulkType()){
			return true;
		}
		
		return false;
	}
	
	public String getUpdateDisabled(){
		if (getWriteDisabled().equals("")) {
			if (getDataSource() == null || getDataSource().getId() == null){
				return "";
			}
			if (getDataSource().getId()!=null) {
				long localUserCount = QueryUtil.findRowCount(
						LocalUser.class, new FilterParams("localUserGroup.id",getDataSource().getId()));
				if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
					if(localUserCount > 0){
						changeUserTypeFlag = true;
					}else{
						changeUserTypeFlag = false;
					}
					return "";
				} else {
					if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
							&& getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
							&& getDataSource().isBlnBulkType()){
						return "";
					}
					
					if (localUserCount==0){
						return "";
					}
				}
			}
		}
		return "disabled";
	}
	
	public String getHideOption(){
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
			return "none";
		}
		if (blnShowOption){
			return "";
		}
		return "none";
	}
	public String getShowOption(){
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
			return "none";
		}
		if (blnShowOption){
			return "none";
		}
		return "";
	}
	public String getShowAutoCreate(){
		if (getDataSource().getUserType()==LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			return "";
		}
		return "none";
	}
	public String getShowConcatenateString(){
		if (getDataSource().getPskGenerateMethod()==LocalUserGroup.PSK_METHOD_PASSWORD_ONLY){
			return "none";
		}
		return "";
	}
	public String getShowSchedule(){
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE){
				if (!getDataSource().isBlnBulkType()){
					return "";
				}
			}
		}
		return "none";
	}
	public String getShowTime(){
		if (getDataSource().getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_ONCE){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowBulkType(){
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE){
				if (getDataSource().isBlnBulkType()){
					return "";
				}
			}
		}
		return "none";
	}
	
	public String getShowBulkEnable() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
			if (getDataSource().getValidTimeType()==LocalUserGroup.VALIDTYME_TYPE_SCHEDULE){
				return "";
			}
		}
		return "none";
	}
	public String getShowRadioDram(){
		if (getDataSource().getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
			return "none";
		}
		return "";
	}
	
	public String getShowNoteMessage(){
		if (getDataSource().getPersonPskCombo()== LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
			return "";
		}
		return "none";
	}
	
	public String getStrUserProfileId() {
		return strUserProfileId;
	}

	public void setStrUserProfileId(String strUserProfileId) {
		this.strUserProfileId = strUserProfileId;
	}

	public String getStrVlanId() {
		return strVlanId;
	}

	public void setStrVlanId(String strVlanId) {
		this.strVlanId = strVlanId;
	}

	public String getStrReauthTime() {
		return strReauthTime;
	}

	public void setStrReauthTime(String strReauthTime) {
		this.strReauthTime = strReauthTime;
	}
	
	public Long getSchedulerId() {
		return schedulerId;
	}

	public void setSchedulerId(Long schedulerId) {
		this.schedulerId = schedulerId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMin() {
		return startMin;
	}

	public void setStartMin(int startMin) {
		this.startMin = startMin;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMin() {
		return endMin;
	}

	public void setEndMin(int endMin) {
		this.endMin = endMin;
	}

	public boolean getBlnStartTime() {
		return blnStartTime;
	}

	public void setBlnStartTime(boolean blnStartTime) {
		this.blnStartTime = blnStartTime;
	}

	public boolean getBlnEndTime() {
		return blnEndTime;
	}

	public void setBlnEndTime(boolean blnEndTime) {
		this.blnEndTime = blnEndTime;
	}

	public List<CheckItem> getSchedulerProfiles() {
		return schedulerProfiles;
	}

	public boolean getBlnShowOption() {
		return blnShowOption;
	}

	public void setBlnShowOption(boolean blnShowOption) {
		this.blnShowOption = blnShowOption;
	}

	public Long getFilterUserType() {
		return filterUserType;
	}

	public void setFilterUserType(Long filterUserType) {
		this.filterUserType = filterUserType;
	}

	public String getFilterUserGroupName() {
		return filterUserGroupName;
	}

	public void setFilterUserGroupName(String filterUserGroupName) {
		this.filterUserGroupName = filterUserGroupName;
	}

	public int getIntCredentialType() {
		return intCredentialType;
	}

	public void setIntCredentialType(int intCredentialType) {
		this.intCredentialType = intCredentialType;
	}

	public String getStrNoChange() {
		return strNoChange;
	}

	public String getStrDescription() {
		return strDescription;
	}

	public void setStrDescription(String strDescription) {
		this.strDescription = strDescription;
	}

	public String getStrOneVlanId() {
		strOneVlanId = getDataSource().getVlanId() == -1 ? "" : String
				.valueOf(getDataSource().getVlanId());
		return strOneVlanId;
	}

	public void setStrOneVlanId(String strOneVlanId) {
		this.strOneVlanId = strOneVlanId;
	}

	public String getStrOneUserProfileId() {
		strOneUserProfileId = getDataSource().getUserProfileId() == -1 ? "" : String
				.valueOf(getDataSource().getUserProfileId());
		return strOneUserProfileId;
	}

	public void setStrOneUserProfileId(String strOneUserProfileId) {
		this.strOneUserProfileId = strOneUserProfileId;
	}

	public String getStartTimeBulk() {
		return startTimeBulk;
	}

	public void setStartTimeBulk(String startTimeBulk) {
		this.startTimeBulk = startTimeBulk;
	}

	public int getStartHourBulk() {
		return startHourBulk;
	}

	public void setStartHourBulk(int startHourBulk) {
		this.startHourBulk = startHourBulk;
	}

	public int getStartMinBulk() {
		return startMinBulk;
	}

	public void setStartMinBulk(int startMinBulk) {
		this.startMinBulk = startMinBulk;
	}

	public int getLifeTimeDay() {
		return lifeTimeDay;
	}

	public void setLifeTimeDay(int lifeTimeDay) {
		this.lifeTimeDay = lifeTimeDay;
	}

	public int getLifeTimeHour() {
		return lifeTimeHour;
	}

	public void setLifeTimeHour(int lifeTimeHour) {
		this.lifeTimeHour = lifeTimeHour;
	}

	public int getLifeTimeMin() {
		return lifeTimeMin;
	}

	public void setLifeTimeMin(int lifeTimeMin) {
		this.lifeTimeMin = lifeTimeMin;
	}
	
	private String schedulerListName;

	public String getSchedulerListName() {
		return schedulerListName;
	}

	public void setSchedulerListName(String schedulerListName) {
		this.schedulerListName = schedulerListName;
	}
	
	public boolean isChangeUserTypeFlag() {
		return changeUserTypeFlag;
	}

	public void setChangeUserTypeFlag(boolean changeUserTypeFlag) {
		this.changeUserTypeFlag = changeUserTypeFlag;
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

	public String getShowRADIUS() {
		if(getDataSource() == null ||
				getDataSource().getId() == null) {
			return "";
		}
		
		List<?> boIds = QueryUtil.executeNativeQuery(
				"select b.ssidname from ssid_local_user_group a, ssid_profile b where a.ssid_profile_id=b.id and a.local_user_group_id='"+ getDataSource().getId()+"'");
		
		if (boIds.size()>0) {
			return "none";
		} else {
			return "";
		}
	}
	
	public String getShowPSK() {
		if(getDataSource() == null ||
				getDataSource().getId() == null) {
			return "";
		}
		
		List<?> boIds = QueryUtil.executeNativeQuery(
				"select b.ssidname from ssid_radius_user_group a, ssid_profile b where a.ssid_profile_id=b.id and a.local_user_group_id='"+ getDataSource().getId()+"'");
		if (boIds.size()>0) {
			return "none";
		} else {
			return "";
		}
	}
	
	private void initTimeZone(){
		if(null != getDataSource()){
			int timezone = HmBeOsUtil.getServerTimeZoneIndex(null);
			if (getUserContext().getDomain().isHomeDomain()) {
				if (getUserContext().getSwitchDomain() != null) {
					timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getSwitchDomain()
							.getTimeZoneString());
				}
			} else {
				timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getDomain()
						.getTimeZoneString());
			}
			getDataSource().setTimezone(timezone);
		}
	}
	
	public String getHideVoiceDevice(){
		if(getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
			return "";
		}
		return "none";
	}
}