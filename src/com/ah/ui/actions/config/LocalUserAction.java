/**
 *@filename		LocalUserAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-26 PM 01:25:47
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.ConfigTemplateAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class LocalUserAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(LocalUserAction.class
			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_USERTYPE = 2;
	public static final int COLUMN_PSKTITLE = 3;
	public static final int COLUMN_USERGROUP = 4;
	public static final int COLUMN_STARTTIME = 5;
	public static final int COLUMN_ENDTIME = 6;
	public static final int COLUMN_EMAIL = 7;
	public static final int COLUMN_DESCRIPTION = 8;
	public static final int COLUMN_PASSWORDDIGEST = 9;
	public static final int COLUMN_PSKDIGEST = 10;

	/**
	 * get the description of column by id
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.localUser.userName";
			break;
		case COLUMN_USERTYPE:
			code = "config.localUserGroup.userType";
			break;
		case COLUMN_PSKTITLE:
			code = "config.localUser.PskTitle";
			break;
		case COLUMN_USERGROUP:
			if (isEasyMode()){
				code = "config.localUser.userGroup.express";
			} else {
				code = "config.localUserGroup.groupName";
			}
			break;
		case COLUMN_STARTTIME:
			code = "report.client.startTime";
			break;
		case COLUMN_ENDTIME:
			code = "report.client.endTime";
			break;
		case COLUMN_EMAIL:
			code = "config.localUser.emailTitle";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.localUserGroup.description";
			break;
		case COLUMN_PASSWORDDIGEST:
			code = "config.localUser.passwordDigest";
			break;
		case COLUMN_PSKDIGEST:
			code = "config.localUser.pskDigest";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	/**
	 * get all available columns
	 */
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_USERTYPE));
		columns.add(new HmTableColumn(COLUMN_PSKTITLE));
		columns.add(new HmTableColumn(COLUMN_USERGROUP));
		columns.add(new HmTableColumn(COLUMN_STARTTIME));
		columns.add(new HmTableColumn(COLUMN_ENDTIME));
		columns.add(new HmTableColumn(COLUMN_EMAIL));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_PASSWORDDIGEST));
		columns.add(new HmTableColumn(COLUMN_PSKDIGEST));
		return columns;
	}
	
	/**
	 * get default selected columns
	 */
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_USERTYPE));
		columns.add(new HmTableColumn(COLUMN_PSKTITLE));
		columns.add(new HmTableColumn(COLUMN_USERGROUP));
		columns.add(new HmTableColumn(COLUMN_STARTTIME));
		columns.add(new HmTableColumn(COLUMN_ENDTIME));
		columns.add(new HmTableColumn(COLUMN_EMAIL));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	public String returnAllValue(String defaultRet, String strEx){
		if (getLastExConfigGuide()!=null) {
			return strEx;
		}
		return defaultRet;
	}
	

	public String expressModeBoList() throws Exception {
		clearRotatedAccounts();
		if (getLastExConfigGuide()!=null) {
			return prepareBoListForExpressLocalUser();
		} else {
			return prepareBoList();
		}
	}
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.localUser.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new LocalUser());
				initLocalUserGroup(getDataSource().getUserType());
				return returnAllValue(INPUT,"localUserEx");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if(getDataSource() ==null){
					return expressModeBoList();
				}
				prepareSaveAllObject();
				boolean nameExistFlg = false;
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
					nameExistFlg = checkNameExists("lower(userName)", getDataSource().getUserName().toLowerCase());
				} else {
					nameExistFlg = checkNameExists("userName", getDataSource().getUserName());
				}
				if (nameExistFlg) {
					prepareInitAllObject();
					if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
						setAutoUserInfo();
					}
					return returnAllValue(INPUT,"localUserEx");
				}
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
					setAutoUserInfo();
					if (remainUser <= 0) {
						addActionError(MgrUtil.getUserMessage("action.error.user.capacity.in.group"));
						prepareInitAllObject();
						return returnAllValue(INPUT,"localUserEx");
					}
				}
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK
						|| getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
					if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
						if (!checkPasswordLength()) {
							prepareInitAllObject();
							return returnAllValue(INPUT,"localUserEx");
						}
					}
					int pskCount = (int) QueryUtil.findRowCount(
							LocalUser.class, new FilterParams(
									"localUserGroup.id=:s1 and revoked=:s2",
									new Object[]{userGroupId, false}));
					if (pskCount >= LocalUser.MAX_COUNT_AP30_LOCALUSER) {
						addActionError(MgrUtil.getUserMessage("action.error.user.capacity.in.group"));
						prepareInitAllObject();
						return returnAllValue(INPUT,"localUserEx");
					}
					if (!checkRelativedSsid(
							getDataSource().getLocalUserGroup(), 1)) {
						prepareInitAllObject();
						return returnAllValue(INPUT,"localUserEx");
					}
					if (!checkRelativedTemplate(getDataSource()
							.getLocalUserGroup(), 1)) {
						prepareInitAllObject();
						return returnAllValue(INPUT,"localUserEx");
					}
				}
				if ("create".equals(operation)) {
					this.getSessionFiltering();
					createBo(dataSource);
					
					// For configuration indication, specially for LocalUser
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							getDataSource(),
							ConfigurationChangedEvent.Operation.CREATE, null));
					return expressModeBoList();
				} else {
					id = createBo(dataSource);
					// For configuration indication, specially for LocalUser
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							getDataSource(),
							ConfigurationChangedEvent.Operation.CREATE, null));
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					addLstTitle(getText("config.title.localUser.edit") + " '"
							+ getChangedName() + "'");
				} else {
					return expressModeBoList();
				}
				prepareInitAllObject();
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
					userGroupId = getUserGroupId();
					setAutoUserInfo();
				}
				return returnAllValue(INPUT,"localUserEx");
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareSaveAllObject();
				}else{
					return expressModeBoList();
				}
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
					if (!checkPasswordLength()) {
						prepareInitAllObject();
						return returnAllValue(INPUT,"localUserEx");
					}
				}
				// get the previous LocalUser;
				LocalUser bo = QueryUtil.findBoById(LocalUser.class, dataSource
						.getId());
				if ("update".equals(operation)) {
					this.getSessionFiltering();
					updateBo(dataSource);
					// For configuration indication, specially for LocalUser
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							bo, ConfigurationChangedEvent.Operation.UPDATE,
							null));
					
					return expressModeBoList();
				} else {
					updateBo(dataSource);
					// For configuration indication, specially for LocalUser
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							bo, ConfigurationChangedEvent.Operation.UPDATE,
							null));
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				LocalUser profile = (LocalUser) findBoById(boClass, cloneId);
				profile.setId(null);
				profile.setUserName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				prepareInitAllObject();
				if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
					userGroupId = getUserGroupId();
					setAutoUserInfo();
					LocalUserGroup localUserGroup = QueryUtil
							.findBoById(LocalUserGroup.class, userGroupId);
					String userAllName = "";
					if (localUserGroup != null && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
						DecimalFormat df = new DecimalFormat("0000");
						userAllName = localUserGroup.getUserNamePrefix()
								+ df.format(activeUserSubfex + 1);
					}
					getDataSource().setUserName(userAllName);
				}
				addLstTitle(getText("config.title.localUser.new"));
				return returnAllValue(INPUT,"localUserEx");
			} else if ("newGroup".equals(operation)
					|| "editGroup".equals(operation)) {
				prepareSaveAllObject();
				addLstForward("localUser");
				clearErrorsAndMessages();
				return operation;
			} else if ("newGroupBulk".equals(operation)
					|| "editGroupBulk".equals(operation)) {
				prepareSaveAllObject();
				MgrUtil.setSessionAttribute("GENERATEUSERNUMBER", userNumber);
				addLstForward("localUserBulk");
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)
					|| "continueBulk".equals(operation)) {
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (dataSource == null) {
					this.getSessionFiltering();
					return expressModeBoList();
				} else {
					setId(dataSource.getId());
					prepareInitAllObject();
					if (userGroupId == null) {
						if (getDataSource().getLocalUserGroup() != null) {
							userGroupId = getDataSource().getLocalUserGroup()
									.getId();
						} else {
							userGroupId = (long) -1;
						}
					}
					if ("continue".equals(operation)) {
						setAutoUserInfo();
						if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
							LocalUserGroup localUserGroup = QueryUtil
									.findBoById(LocalUserGroup.class,
											userGroupId);
							String userAllName = "";
							if (localUserGroup != null && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
								DecimalFormat df = new DecimalFormat("0000");
								userAllName = localUserGroup
										.getUserNamePrefix()
										+ df.format(activeUserSubfex + 1);
							}
							getDataSource().setUserName(userAllName);
						}
						return returnAllValue(INPUT,"localUserEx");
					} else {
						userNumber = (Integer) MgrUtil
								.getSessionAttribute("GENERATEUSERNUMBER");
						setAutoUserInfo();
						return "newCreateBulk";
					}
				}
			} else if ("newCreateBulk".equals(operation)) {
				initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				if (!localUserGroup.isEmpty() && localUserGroup != null) {
					userGroupId = localUserGroup.get(0).getId();
					setAutoUserInfo();
				} else {
					userGroupId = (long) -1;
				}
				setTitleAndCheckAccess(getText("config.title.localUser.new"));
				setSessionDataSource(new LocalUser());
				getDataSource().setUserType(
						LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				return "newCreateBulk";
			} else if ("createBulk".equals(operation)) {
				prepareSaveAllObject();
				setAutoUserInfo();
				int prepareActiveUser = activeUser;
				int prepareActiveUserSubfex = activeUserSubfex;
				getDataSource().setUserType(
						LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				DecimalFormat df = new DecimalFormat("0000");
				if (!checkAutoGenerateUserNameExist()) {
					initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
					activeUser = prepareActiveUser;
					activeUserSubfex = prepareActiveUserSubfex;
					addActionError(MgrUtil.getUserMessage("error.objectExistsForBulkUser"));
					return "newCreateBulk";
				}
				activeUser = prepareActiveUser;
				activeUserSubfex = prepareActiveUserSubfex;
				if (activeUser + userNumber > LocalUser.MAX_COUNT_AP30_LOCALUSER || activeUserSubfex+userNumber>LocalUser.MAX_COUNT_AP30_LOCALUSER) {
					initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
//					activeUser = prepareActiveUser;
					addActionError(MgrUtil.getUserMessage("action.error.not.enough.user.capacity"));
					return "newCreateBulk";
				}
				if (!checkRelativedSsid(getDataSource().getLocalUserGroup(),
						userNumber)) {
					initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
//					activeUser = prepareActiveUser;
					return "newCreateBulk";
				}
				if (!checkRelativedTemplate(
						getDataSource().getLocalUserGroup(), userNumber)) {
					initLocalUserGroup(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
//					activeUser = prepareActiveUser;
					return "newCreateBulk";
				}
				if (userNumber > 1) {
					for (int i = 1; i < userNumber + 1; i++) {
						if (activeUser < LocalUser.MAX_COUNT_AP30_LOCALUSER && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
							if (activeUserSubfex > 0) {
								String newUserName = getDataSource()
										.getLocalUserGroup()
										.getUserNamePrefix()
										+ df.format(activeUserSubfex + 1);
								getDataSource().setUserName(newUserName);
							} else {
								String newUserName = getDataSource()
										.getLocalUserGroup()
										.getUserNamePrefix()
										+ df.format(1);
								getDataSource().setUserName(newUserName);
							}
							activeUser++;
							activeUserSubfex++;
							getDataSource().setId(null);
							createBo(dataSource);
						}
					}
				} else {
					if (activeUser < LocalUser.MAX_COUNT_AP30_LOCALUSER && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
						if (activeUserSubfex > 0) {
							String newUserName = getDataSource()
									.getLocalUserGroup().getUserNamePrefix()
									+ df.format(activeUserSubfex + 1);
							getDataSource().setUserName(newUserName);
						} else {
							String newUserName = getDataSource()
									.getLocalUserGroup().getUserNamePrefix()
									+ df.format(1);
							getDataSource().setUserName(newUserName);
						}
						createBo();
					}
				}
				// For configuration indication, specially for LocalUser
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						getDataSource(),
						ConfigurationChangedEvent.Operation.CREATE, null));
				baseOperation();
				this.getSessionFiltering();
				return expressModeBoList();
			} else if ("email".equals(operation)) {
				int emailCount = 0;
				this.getSessionFiltering();
				List<MailNotification> mailNotification = QueryUtil.executeQuery(
						MailNotification.class, null, null, getDomain().getId());
				if (mailNotification != null && mailNotification.size() > 0) {
					String serverName = mailNotification.get(0)
							.getServerName();
					String mailFrom = mailNotification.get(0)
							.getMailFrom();
					if (serverName == null || serverName.equals("") || mailFrom == null
							|| mailFrom.equals("")) {
						addActionError(MgrUtil.getUserMessage("action.error.smtp.server.setting"));
						return expressModeBoList();
					}

				} else {
					addActionError(MgrUtil.getUserMessage("action.error.smtp.server.setting"));
					return expressModeBoList();
				}

				if (isAllItemsSelected()) {
					List<LocalUser> lstLocalUser = QueryUtil.executeQuery(
							LocalUser.class, new SortParams("userName"), filterParams,
							getDomainId());
					for (LocalUser tmpClass : lstLocalUser) {
						if (!tmpClass.getMailAddress().equals("")
								&& !tmpClass.getRevoked()
								&& tmpClass.getStatus()!=LocalUser.STATUS_REVOKED
								&& tmpClass.getStatus()!=LocalUser.STATUS_PARTIAL_REVOKED
								&& tmpClass.getUserType()!=LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
							if (sendMailToUser(tmpClass, mailNotification)) {
								emailCount++;
							}
						}
					}
				} else {
					for (Long id : getAllSelectedIds()) {
						LocalUser localUser = findBoById(
								LocalUser.class, id);
						if (!localUser.getMailAddress().equals("")
								&& localUser.getUserType()!=LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
							if (sendMailToUser(localUser, mailNotification)) {
								emailCount++;
							}
						}
					}
				}
				if (emailCount>1) {
					addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, String
						.valueOf(emailCount) + " PSKs have "));
				} else {
					addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, String
							.valueOf(emailCount) + " PSK has "));
				}
				return expressModeBoList();
			} else if ("createDownloadData".equals(operation)) {
				boolean isSucc = generalCurrentCvsFile();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("createDownloadDataImport".equals(operation)) {
				boolean isSucc = generalCurrentCvsFileImport();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("download".equals(operation)) {
				File file = new File(getInputPath());
				if (!file.exists()) {
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					return expressModeBoList();
				}
				return "download";
			} else if ("download2".equals(operation)) {
				File file = new File(getInputPath2());
				if (!file.exists()) {
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					return expressModeBoList();
				}
				return "download2";
			} else if ("import".equals(operation)) {
				addLstForward("localUser");
				clearErrorsAndMessages();
				return operation;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("changeUserGroup".equals(operation)) {
				jsonObject = new JSONObject();
				setAutoUserInfo();
				DecimalFormat df = new DecimalFormat("0000");
				LocalUserGroup localUserGroup = QueryUtil
						.findBoById(LocalUserGroup.class, userGroupId);
				String userAllName = "";
				if (localUserGroup != null && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
					userAllName = localUserGroup.getUserNamePrefix()
							+ df.format(activeUserSubfex + 1);
				}
				jsonObject.put("userName", userAllName);
				jsonObject.put("active", activeUser);
				jsonObject.put("revoke", revokeUser);
				jsonObject.put("remain", remainUser);
				return "json";
			} else if ("genenatePassword".equals(operation)) {
				setAutoGeneratePassword();
				return "json";
			} else if ("changeUserType".equals(operation)) {
				initLocalUserGroup(userType);
				jsonArray = new JSONArray();

				for (CheckItem oneItem : localUserGroup) {
					jsonObject = new JSONObject();
					jsonObject.put("id", oneItem.getId());
					jsonObject.put("value", oneItem.getValue());
					jsonArray.put(jsonObject);
				}
				return "json";
			} else if ("search".equals(operation)) {
				Object lstCondition[] = new Object[6];
				lstCondition[0] = false;
				lstCondition[1] = "%"
						+ getFilterUserName().trim().toLowerCase() + "%";
				lstCondition[2] = "%" + getFilterEmail().trim().toLowerCase()
						+ "%";
				lstCondition[3] = "%"
						+ getFilterDescription().trim().toLowerCase() + "%";
				lstCondition[4] = LocalUser.STATUS_REVOKED;
				lstCondition[5] = LocalUser.STATUS_PARTIAL_REVOKED;
				filterParams = new FilterParams(
						"revoked=:s1 and lower(userName) like :s2 and lower(mailAddress) like :s3 " +
						"and lower(description) like :s4 and status!=:s5 and status!=:s6",
						lstCondition);
				setSessionFiltering();
				baseOperation();
				return expressModeBoList();
			} else if("filterRadiusUser".equals(operation)){
				filterParams = new FilterParams("userType",LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
				setSessionFiltering();
				return expressModeBoList();
			} else if("filterPskUser".equals(operation)){
				List<Integer> psks = new ArrayList<Integer>();
				psks.add(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
				psks.add(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
				String where = "userType in (:s1) and revoked = :s2 and status!=:s3 and status!=:s4";
				Object[] values = new Object[]{psks, false, LocalUser.STATUS_REVOKED,LocalUser.STATUS_PARTIAL_REVOKED};
				filterParams = new FilterParams(where, values);
				setSessionFiltering();
				return expressModeBoList();
			} else if ("showHidePPSK".equals(operation)) {
				this.getSessionFiltering();
				MgrUtil.setSessionAttribute("SHOWORHIDEALLPSK",getBlnShowOrHidePsk());
				return expressModeBoList();
			} else if ("remove".equals(operation)) {
				removeLocalUserOperation();
				this.getSessionFiltering();
				return expressModeBoList();
			} else if ("removeAllSessionAttr".equals(operation)) {
				MgrUtil.setSessionAttribute("lstTitle", MgrUtil.getSessionAttribute("SSID_PREVIEW_TITLE"));
				MgrUtil.setSessionAttribute("lstTabId", MgrUtil.getSessionAttribute("SSID_PREVIEW_TABID"));
				MgrUtil.setSessionAttribute("lstForward", MgrUtil.getSessionAttribute("SSID_PREVIEW_FORWARD"));
				MgrUtil.setSessionAttribute("lstFormChanged", MgrUtil.getSessionAttribute("SSID_PREVIEW_FORMCHANGED"));
				MgrUtil.removeSessionAttribute("SSID_PREVIEW_TITLE");
				MgrUtil.removeSessionAttribute("SSID_PREVIEW_TABID");
				MgrUtil.removeSessionAttribute("SSID_PREVIEW_FORWARD");
				MgrUtil.removeSessionAttribute("SSID_PREVIEW_FORMCHANGED");
				jsonObject = new JSONObject();
				jsonObject.put("v", "s");
				return "json";
			} else {
				if (baseOperation()) {
					this.getSessionFiltering();
				} else {
					Object lstCondition[] = new Object[6];
					lstCondition[0] = false;
					lstCondition[1] = "%"
							+ getFilterUserName().trim().toLowerCase() + "%";
					lstCondition[2] = "%"
							+ getFilterEmail().trim().toLowerCase() + "%";
					lstCondition[3] = "%"
							+ getFilterDescription().trim().toLowerCase() + "%";
					lstCondition[4] = LocalUser.STATUS_REVOKED;
					lstCondition[5] = LocalUser.STATUS_PARTIAL_REVOKED;
					filterParams = new FilterParams(
							"revoked=:s1 and lower(userName) like :s2 and lower(mailAddress) like :s3 " +
							"and lower(description) like :s4 and status!=:s5 and status!=:s6",
							lstCondition);
					setSessionFiltering();
				}
				if ("expressList".equals(operation)){
					MgrUtil.setSessionAttribute("SSID_PREVIEW_TITLE", MgrUtil.getSessionAttribute("lstTitle"));
					MgrUtil.setSessionAttribute("SSID_PREVIEW_TABID", MgrUtil.getSessionAttribute("lstTabId"));
					MgrUtil.setSessionAttribute("SSID_PREVIEW_FORWARD", MgrUtil.getSessionAttribute("lstForward"));
					MgrUtil.setSessionAttribute("SSID_PREVIEW_FORMCHANGED", MgrUtil
							.getSessionAttribute("lstFormChanged"));
				}
				return expressModeBoList();
			}
		} catch (Exception e) {
			this.getSessionFiltering();
			setSessionFiltering();
			return prepareActionError(e);
		}
	}

	protected boolean removeLocalUserOperation() throws Exception {
		int count = -1;
		boolean hasRemoveDefaultValue=false;
		Set<Long> revokeGroupIds = new HashSet<Long>();
		if (allItemsSelected) {
			setAllSelectedIds(null);
			this.getSessionFiltering();

			List<?> removeGroupIds = QueryUtil.executeQuery(
					"select localUserGroup.id from "
							+ LocalUser.class.getSimpleName() + " bo", null,
					filterParams);
			for (Object groupId : removeGroupIds) {
				revokeGroupIds.add(Long.valueOf(groupId.toString()));
			}
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
		} else if (getAllSelectedIds() != null
				&& !getAllSelectedIds().isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && getAllSelectedIds().removeAll(defaultIds)) {
				hasRemoveDefaultValue=true;
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
			}
			Collection<Long> revokeRemoveIds = new HashSet<Long>();

			List<LocalUser> revokeUsers = new ArrayList<LocalUser>();
			for (Long removeId : getAllSelectedIds()) {
				LocalUser localUser = QueryUtil.findBoById(
						LocalUser.class, removeId);
				if (localUser.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
					revokeRemoveIds.add(removeId);
					revokeUsers.add(localUser);
					revokeGroupIds.add(localUser.getLocalUserGroup().getId());
				}
			}
			Collection<Long> toRemoveIds = getAllSelectedIds();
			toRemoveIds.removeAll(revokeRemoveIds);
			setAllSelectedIds(null);
			if (toRemoveIds.size() > 0) {
				count = removeBos(boClass, toRemoveIds);
			} else {
				count = 0;
			}
			if (revokeRemoveIds.size() > 0) {
				Object bindings[] = new Object[2];
				bindings[0] = true;
				bindings[1] = revokeRemoveIds;
				count = count
						+ QueryUtil.updateBos(boClass, "revoked=:s1",
								"id in (:s2)", bindings);
				for(LocalUser revokedUser : revokeUsers){
					// For configuration indication, specially for LocalUser
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							revokedUser,
							ConfigurationChangedEvent.Operation.REVOKE, null));
				}
			}
		}
		for (Long revokedGroupId : revokeGroupIds) {
			Object bindings[] = new Object[2];
			bindings[0] = false;
			bindings[1] = revokedGroupId;
			FilterParams filter = new FilterParams(
					"revoked=:s1 and localUserGroup.id=:s2", bindings);
			long noRevokedCount = QueryUtil.findRowCount(boClass, filter);
			if (noRevokedCount == 0) {
				removeAllBos(boClass, new FilterParams("localUserGroup.id",
						revokedGroupId), null);
			}
		}
		log.info("removeOperation", "Count: " + count);
		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else if (count == 1) {
			if (hasRemoveDefaultValue){
				addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED_WITH_DEFAULT));
			} else {
				addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED));
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
	
	public void clearRotatedAccounts() throws Exception {
		/*
		 * first, get all target local user out of database
		 */
		SortParams sort = new SortParams("id");
		String where = "userType = :s1 AND status != :s2  AND localUserGroup.blnBulkType = :s3 AND owner = :s4";
		FilterParams filter = new FilterParams(where,
				new Object[] {LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,
				LocalUser.STATUS_FREE,
				Boolean.TRUE,
				getDomain()});
		
		Paging<LocalUser> page = new PagingImpl<LocalUser>(LocalUser.class);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		
		/*
		 * verify each local user, if PSK is rotated, update the local user
		 */
		while(page.hasNext()) {
			bos = page.next().executeQuery(sort, filter);
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				LocalUser user = (LocalUser)bo;
				
				if(user == null || user.getOldPPSK() == null) {
					continue;
				}
				
				/*
				 * PSK has been rotated
				 */
				if(!user.getOldPPSK().equals(user.getStrPsk())) {
					clearRotatedAccount(user);
					QueryUtil.updateBo(user);
				}
			}
		}
	}
	
	private void clearRotatedAccount(LocalUser user) {
		if(user == null) {
			return ;
		}
		
		user.setStatus(LocalUser.STATUS_FREE);
		user.setMailAddress("");
		user.setRevoked(false);
		user.setVisitorName(null);
		user.setVisitorCompany(null);
		user.setSponsor(null);
		user.setSsidName(null);
		user.setOldPPSK(null);
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LOCAL_USER);
		setDataSource(LocalUser.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_LOCAL_USER;
	}

	public void prepareInitAllObject() {
		prepareGetManualPassword();
		initLocalUserGroup(getDataSource().getUserType());
	}

	public void prepareSaveAllObject() throws Exception {
		prepareSetManualPassword();
		setUserGroup();
	}

	// struts download support
	private final String pskFileName = "ExportPSK.csv";
	
	private final String pskFileName2 = "ExportPSK_import.csv";

	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + pskFileName;
	}
	
	public String getInputPath2() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + pskFileName2;
	}

	public String getPskFileName() {
		return pskFileName;
	}
	
	public String getPskFileName2() {
		return pskFileName2;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	
	public InputStream getInputStream2() throws Exception {
		return new FileInputStream(getInputPath2());
	}

	public synchronized boolean generalCurrentCvsFile() {
		String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent
				+ File.separator + getDomain().getDomainName();
		File tmpFileDir = new File(currentFileDir);
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}
		StringBuffer strOutput;
		File tmpFile = new File(currentFileDir + File.separator + pskFileName);
		try {
			FileWriter out = new FileWriter(tmpFile);
			strOutput = new StringBuffer();
			strOutput.append("User Name,");
			strOutput.append("PSK,");
			strOutput.append("Start Time,");
			strOutput.append("End Time,");
			strOutput.append("Email Address,");
			strOutput.append("Description");
			strOutput.append("\n");
			out.write(strOutput.toString());

			strOutput = new StringBuffer();
			if (isAllItemsSelected()) {
				this.getSessionFiltering();
				List<LocalUser> lstLocalUser = QueryUtil.executeQuery(LocalUser.class,
						new SortParams("userName"), filterParams, getDomainId());
				for (LocalUser tmpClass : lstLocalUser) {
					if (!tmpClass.getRevoked()
							&& tmpClass.getStatus()!=LocalUser.STATUS_REVOKED
							&& tmpClass.getStatus()!=LocalUser.STATUS_PARTIAL_REVOKED
							&& tmpClass.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getUserName())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getStrPsk())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getStartTimeString())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getExpiredTimeString())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getMailAddress())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getDescription())).append("\n");
					}
				}
			} else {
				for (Long thisId : getAllSelectedIds()) {
					LocalUser tmpClass = QueryUtil.findBoById(
							LocalUser.class, thisId);
					if (tmpClass.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getUserName())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getStrPsk())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getStartTimeString())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getExpiredTimeString())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getMailAddress())).append(",");
						strOutput.append(MgrUtil.convertCsvField(tmpClass.getDescription())).append("\n");
					}
				}
			}
			out.write(strOutput.toString());
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			DebugUtil.configDebugWarn("exportCurrentData in private PSK:", e);
			return false;
		}
	}
	
	public synchronized boolean generalCurrentCvsFileImport() {
		String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent
				+ File.separator + getDomain().getDomainName();
		File tmpFileDir = new File(currentFileDir);
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}
		StringBuffer strOutput;
		File tmpFile = new File(currentFileDir + File.separator + pskFileName2);
		try {
			FileWriter out = new FileWriter(tmpFile);
			strOutput = new StringBuffer();
			strOutput.append("//User Name,");
			strOutput.append("User Type,");
			strOutput.append("User Group,");
			strOutput.append("Password,");
			strOutput.append("Email Address or Description,");
			strOutput.append("Description");
			strOutput.append("\n");
			out.write(strOutput.toString());

			strOutput = new StringBuffer();
			if (isAllItemsSelected()) {
				this.getSessionFiltering();
				List<LocalUser> lstLocalUser = QueryUtil.executeQuery(LocalUser.class,
						new SortParams("userName"), filterParams, getDomainId());
				for (LocalUser tmpClass : lstLocalUser) {
					if (tmpClass.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getUserName())).append(",");
						strOutput.append(tmpClass.getUserType()).append(",");
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getUserGroupName())).append(",");
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getLocalUserPassword())).append(",");
						if (tmpClass.getUserType()==LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
							strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getMailAddress())).append(",");
						}
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getDescription())).append("\n");
					}
				}
			} else {
				for (Long thisId : getAllSelectedIds()) {
					LocalUser tmpClass = QueryUtil.findBoById(
							LocalUser.class, thisId);
					if (tmpClass.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getUserName())).append(",");
						strOutput.append(tmpClass.getUserType()).append(",");
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getUserGroupName())).append(",");
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getLocalUserPassword())).append(",");
						if (tmpClass.getUserType()==LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
							strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getMailAddress())).append(",");
						}
						strOutput.append(MgrUtil.convertCsvFieldForImport(tmpClass.getDescription())).append("\n");
					}
				}
			}
			out.write(strOutput.toString());
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			DebugUtil.configDebugWarn("exportCurrentData in private PSK:", e);
			return false;
		}
	}

	public boolean sendMailToUser(LocalUser localUser, List<MailNotification> mailNotification) {
		StringBuilder mailFileText = new StringBuilder();
		mailFileText.append("PSK: ").append(localUser.getStrPsk()).append("\n");
		mailFileText.append("Description: ").append(localUser.getDescription()).append("\n");
		mailFileText.append("User Name: ").append(localUser.getUserName()).append("\n");
		mailFileText.append("Start Time: ").append(localUser.getStartTimeString()).append("\n");
		mailFileText.append("End Time: ").append(localUser.getExpiredTimeString()).append("\n");

		if (mailNotification != null && !mailNotification.isEmpty()) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification.get(0));
			mailUtil.setMailTo(localUser.getMailAddress());
			mailUtil.setSubject(localUser.getUserName() + " PSK");
			mailUtil.setText(mailFileText.toString());
			
			try {
				mailUtil.startSend();
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean checkAutoGenerateUserNameExist() {
		DecimalFormat df = new DecimalFormat("0000");
		Set<String> generateUserName = new HashSet<String>();
		if (userNumber > 1) {
			for (int i = 1; i < userNumber + 1; i++) {
				if (activeUser < LocalUser.MAX_COUNT_AP30_LOCALUSER && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
					if (activeUserSubfex > 0) {
						String newUserName = getDataSource()
								.getLocalUserGroup().getUserNamePrefix()
								+ df.format(activeUserSubfex + 1);
						generateUserName.add(newUserName);
					} else {
						String newUserName = getDataSource()
								.getLocalUserGroup().getUserNamePrefix()
								+ df.format(1);
						generateUserName.add(newUserName);
					}
					activeUser++;
					activeUserSubfex++;
				}
			}
		} else {
			if (activeUser < LocalUser.MAX_COUNT_AP30_LOCALUSER && activeUserSubfex<LocalUser.MAX_COUNT_AP30_LOCALUSER) {
				if (activeUserSubfex > 0) {
					String newUserName = getDataSource().getLocalUserGroup()
							.getUserNamePrefix()
							+ df.format(activeUserSubfex + 1);
					generateUserName.add(newUserName);
				} else {
					String newUserName = getDataSource().getLocalUserGroup()
							.getUserNamePrefix()
							+ df.format(1);
					generateUserName.add(newUserName);
				}
			}
		}

		Object bindings[] = new Object[2];
		bindings[0] = generateUserName;
		bindings[1] = getDomain().getId();
		if (generateUserName.size() > 0) {
			long existCount = QueryUtil.findRowCount(LocalUser.class,
					new FilterParams("userName in (:s1) and owner.id=:s2", bindings));
			if (existCount > 0) {
				return false;
			}
		}
		return true;
	}

	public void prepareGetManualPassword() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			pskPassword1 = getDataSource().getLocalUserPassword();
		}
	}

	public void prepareSetManualPassword() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			getDataSource().setLocalUserPassword(pskPassword1);
		}
	}

	private void setUserGroup() throws Exception {
		if (userGroupId != null && userGroupId > -1) {
			LocalUserGroup userGroup = findBoById(
					LocalUserGroup.class, userGroupId);
			getDataSource().setLocalUserGroup(userGroup);
		} else if (userGroupId != null && userGroupId < 0) {
			getDataSource().setLocalUserGroup(null);
		}
	}

	private void setAutoUserInfo() {
		Object values[] = new Object[2];
		values[0] = userGroupId;
		values[1] = false;
		activeUser = (int) QueryUtil.findRowCount(LocalUser.class,
				new FilterParams("localUserGroup.id=:s1 and revoked=:s2",
						values));
		values[1] = true;
		revokeUser = (int) QueryUtil.findRowCount(LocalUser.class,
				new FilterParams("localUserGroup.id=:s1 and revoked=:s2",
						values));
		
		remainUser = LocalUser.MAX_COUNT_AP30_LOCALUSER -  activeUser;
		activeUserSubfex = activeUser + revokeUser;
	}

	private void setAutoGeneratePassword() throws Exception {
		jsonObject = new JSONObject();
		if (getGenPasswordType() != null
				&& getGenPasswordType().equals("ascii")) {
			int charLimit = 7;
			int passwordLength=63;
			if (userGroupId != null && userGroupId > -1) {
				LocalUserGroup userGroup = findBoById(
						LocalUserGroup.class, userGroupId);
				charLimit = 0;
				if (userGroup.getBlnCharLetters()) {
					charLimit = charLimit + 1;
				}
				if (userGroup.getBlnCharDigits()) {
					charLimit = charLimit + 2;
				}
				if (userGroup.getBlnCharSpecial()) {
					charLimit = charLimit + 4;
				}
				if (userGroup.getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO) {
					if (userGroup.getBlnCharLetters()) {
						charLimit = 1;
					} else if (userGroup.getBlnCharDigits()) {
						charLimit = 2;
					} else {
						charLimit = 4;
					}
				}
				if (userGroup.getPskGenerateMethod()==LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME
					&& userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
					int existPassLength = userGroup.getConcatenateString().length() + getUserNameLen();
					passwordLength = 63 - existPassLength;
				}
			}
			jsonObject.put("v", MgrUtil.getRandomString(passwordLength, charLimit));
		} else {
			jsonObject.put("v", MgrUtil.getRandomString(63, 7));
		}
	}

	public boolean checkPasswordLength() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK
				&& getDataSource().getLocalUserGroup().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME) {
			int intPskLength = getDataSource().getLocalUserGroup()
					.getConcatenateString().length()
					+ getDataSource().getUserName().length()
					+ getDataSource().getLocalUserPassword().length();
			if (intPskLength > 63) {
				addActionError(getText("error.privatePsk.wrongPskLength"));
				return false;
			}
		}
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {

			String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String digStr = "1234567890";
			String spcStr = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			// String spcStr = "!#$()*+,-./:=@[]^_`{|}~";
			boolean blnStr = false;
			boolean blnDig = false;
			boolean blnSpc = false;

			char[] psbyte;
			String message;
			if (getDataSource().getLocalUserGroup().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME){
				psbyte = (getDataSource().getUserName() + getDataSource().getLocalUserGroup().getConcatenateString() +  getDataSource().getLocalUserPassword()).toCharArray();
				message = "user name + concatenating string + password";
			} else {
				psbyte =  getDataSource().getLocalUserPassword().toCharArray();
				message = "password";
			}
			for(char onebyte: psbyte){
				if (str.indexOf(onebyte)!=-1){
					blnStr = true;
					break;
				}
			}

			for (char onebyte : psbyte) {
				if (digStr.indexOf(onebyte) != -1) {
					blnDig = true;
					break;
				}
			}

			for (char onebyte : psbyte) {
				if (spcStr.indexOf(onebyte) != -1) {
					blnSpc = true;
					break;
				}
			}
			String asDefined ="";
			if (isFullMode()){
				asDefined=" "+MgrUtil.getUserMessage("action.error.defined.local.user.group");
			}
			if (getDataSource().getLocalUserGroup().getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
				if (!blnStr == getDataSource().getLocalUserGroup().getBlnCharLetters()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter",message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.letter",message) + asDefined +".");
					}
					return false;
				}
				if (!blnDig == getDataSource().getLocalUserGroup().getBlnCharDigits()){
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit",message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.digit",message) + asDefined +".");
					}
					return false;
				}
				if (!blnSpc == getDataSource().getLocalUserGroup().getBlnCharSpecial()){
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char",message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.special.char",message) + asDefined +".");
					}
					return false;
				}
			}
			if (getDataSource().getLocalUserGroup().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_OR){
				if (!getDataSource().getLocalUserGroup().getBlnCharLetters() && blnStr){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter",message) + asDefined +".");
					return false;
				}
				if (!getDataSource().getLocalUserGroup().getBlnCharDigits() && blnDig){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit",message) + asDefined +".");
					return false;
				}
				if (!getDataSource().getLocalUserGroup().getBlnCharSpecial() && blnSpc){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char",message) + asDefined +".");
					return false;
				}
			}

			if (getDataSource().getLocalUserGroup().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
				if (getDataSource().getLocalUserGroup().getBlnCharLetters()){
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit",message) + asDefined +".");
						return false;
					}
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char",message) + asDefined +".");
						return false;
					}
				} else if (getDataSource().getLocalUserGroup().getBlnCharDigits()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter",message) + asDefined +".");
						return false;
					}
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char",message) + asDefined +".");
						return false;
					}
				} else if (getDataSource().getLocalUserGroup().getBlnCharSpecial()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter",message) + asDefined +".");
						return false;
					}
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit",message) + asDefined +".");
						return false;
					}
				}
			}
		}
		return true;
	}

	public LocalUser getDataSource() {
		return (LocalUser) dataSource;
	}

	private JSONObject jsonObject = null;
	private JSONArray jsonArray = null;

	public String getJSONString() {
		if (jsonArray == null) {
			return jsonObject.toString();
		} else {
			return jsonArray.toString();
		}
	}

	public int getUserNameLength() {
		return getAttributeLength("userName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public int getPassLength() {
		return getAttributeLength("localUserPassword");
	}

	public String getChangedName() {
		return getDataSource().getUserName().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public String getUserGroupTitle() {
		if (isEasyMode()) {
			if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS){
				return MgrUtil.getUserMessage("hiveAp.tag");
			} else {
				return MgrUtil.getUserMessage("config.ssid.head.ssid");
			}
		} else {
			return MgrUtil.getUserMessage("config.localUser.userGroup");
		}
	}
	
	public String getUserGroupTitleForList() {
		if (isEasyMode()) {
			return MgrUtil.getUserMessage("config.localUser.userGroup.express");
		} else {
			return MgrUtil.getUserMessage("config.localUser.userGroup");
		}
	}
	
	public boolean getBlnEasyMode(){
		return isEasyMode();
	}

	public String getShowAutoUserInfo() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			return "";
		}
		return "none";
	}

	public String getShowPasswordInfo() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "";
		}
		return "none";
	}

	public String getShowEmailInfo(){
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "none";
		}
		return "";
	}

	public String getShowNewPicture() {
		if (getDataSource().getId() != null){
//				&& getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			return "";
		}
		return "show";
	}

	public String getShowNewGroupButton() {
		if (getWriteDisabled().equals("") && getShowNewPicture().equals("show")) {
			return "";
		} else {
			return "disabled";
		}
	}

	public String getShowPasswordAscii() {
		if (getDataSource().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			return "";
		}
		return "none";
	}

	private Long userGroupId;

	private int userNumber = 1;
	
	private int activeUser = 0;
	private int revokeUser = 0;
	private int remainUser = LocalUser.MAX_COUNT_AP30_LOCALUSER;
	private int activeUserPrifex;
	// begin add for 3.5
	private int activeUserSubfex=0;
	// end add
	private List<CheckItem> localUserGroup;
	private int userType;

	private String pskPassword1;
	private String pskConfirmPassword1;
	private String genPasswordType;
	private int userNameLen;

	private String filterUserName = "";
	private String filterEmail = "";
	private String filterDescription = "";

	public String getGenPasswordType() {
		return genPasswordType;
	}

	public void setGenPasswordType(String genPasswordType) {
		this.genPasswordType = genPasswordType;
	}

	public String getPskPassword1() {
		return pskPassword1;
	}

	public void setPskPassword1(String pskPassword1) {
		if (pskPassword1!=null){
			this.pskPassword1 = pskPassword1.trim();
		} else {
			this.pskPassword1 = pskPassword1;
		}
	}

	public String getPskConfirmPassword1() {
		return pskConfirmPassword1;
	}

	public void setPskConfirmPassword1(String pskConfirmPassword1) {
		if (pskConfirmPassword1!=null){
			this.pskConfirmPassword1 = pskConfirmPassword1.trim();
		} else {
			this.pskConfirmPassword1 = pskConfirmPassword1;
		}
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getActiveUserPrifex() {
		return activeUserPrifex;
	}

	public int getActiveUser() {
		return activeUser;
	}

	public int getRevokeUser() {
		return revokeUser;
	}

	public int getRemainUser() {
		return remainUser;
	}

	public Long getUserGroupId() {
		if (null == userGroupId)
			userGroupId = getDataSource().getLocalUserGroup().getId();
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public void initLocalUserGroup(int userType) {
		if (getDataSource()!=null && getDataSource().getId()!=null && getDataSource().getLocalUserGroup().isBlnBulkType()) {
			localUserGroup = getBoCheckItems("groupName", LocalUserGroup.class,
				new FilterParams("userType=:s1 and blnBulkType=:s2", 
						new Object[] {userType, true}), CHECK_ITEM_BEGIN_NO,
				CHECK_ITEM_END_NO);
		} else {
			localUserGroup = getBoCheckItems("groupName", LocalUserGroup.class,
					new FilterParams("userType=:s1 and blnBulkType=:s2", 
							new Object[] {userType, false}), CHECK_ITEM_BEGIN_NO,
					CHECK_ITEM_END_NO);
		}
	}

	public List<CheckItem> getLocalUserGroup() {
		return localUserGroup;
	}

	public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public String getChangeUserGroup() {
		return "changeUserGroup";
	}

	public String getGenenatePassword() {
		return "genenatePassword";
	}

	public String getChangeUserType() {
		return "changeUserType";
	}

	public String getFilterUserName() {
		return filterUserName;
	}

	public void setFilterUserName(String filterUserName) {
		this.filterUserName = filterUserName;
	}

	public String getFilterEmail() {
		return filterEmail;
	}

	public void setFilterEmail(String filterEmail) {
		this.filterEmail = filterEmail;
	}

	public String getFilterDescription() {
		return filterDescription;
	}

	public void setFilterDescription(String filterDescription) {
		this.filterDescription = filterDescription;
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

	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		List<LocalUser> list = QueryUtil.executeQuery(LocalUser.class, null,
				new FilterParams("id", ids));
		int result = super.removeBos(boClass, ids);
		for (LocalUser user : list) {
			// For configuration indication, specially for LocalUser
			log.info("customize removeBos", "LocalUser:" + user.getUserName()
					+ " has been removed.");
			HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(user,
					ConfigurationChangedEvent.Operation.REMOVE, null));
		}
		return result;
	}

	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		List<LocalUser> list = QueryUtil.executeQuery(LocalUser.class, null,
				filterParams);
		int result = super.removeAllBos(boClass, filterParams, defaultIds);
		for (LocalUser user : list) {
			if (null != defaultIds && defaultIds.contains(user.getId())) {
				continue;
			}
			// For configuration indication, specially for LocalUser
			log.info("customize removeAllBos", "LocalUser:"
					+ user.getUserName() + " has been removed.");
			HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(user,
					ConfigurationChangedEvent.Operation.REMOVE, null));
		}
		return result;
	}

	public int getUserNameLen() {
		return userNameLen;
	}

	public void setUserNameLen(int userNameLen) {
		this.userNameLen = userNameLen;
	}

	public boolean getShowOrHidePskString(){
		return !(MgrUtil.getSessionAttribute("SHOWORHIDEALLPSK") == null
				|| !(Boolean) MgrUtil.getSessionAttribute("SHOWORHIDEALLPSK"));
	}

	private boolean blnShowOrHidePsk=false;
	public boolean getBlnShowOrHidePsk() {
		return blnShowOrHidePsk;
	}

	public void setBlnShowOrHidePsk(boolean blnShowOrHidePsk) {
		this.blnShowOrHidePsk = blnShowOrHidePsk;
	}

}