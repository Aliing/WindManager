/**
 * @filename			TemporaryAccountAction.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.gml;


import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.admin.auth.AhAuthFactory;
import com.ah.be.admin.auth.agent.AhAuthAgent;
import com.ah.be.admin.auth.agent.AhAuthAgent.AuthMethod;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.gml.PrintField;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.gml.TemplateField;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.CheckItem;
import com.ah.util.HiveApUtils;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class TemporaryAccountAction extends BaseAction implements QueryBo {

	private static final long	serialVersionUID	= 1L;
	
	private static final Tracer log = new Tracer(TemporaryAccountAction.class
			.getSimpleName());


	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_USER_NAME = 1;
	public static final int COLUMN_PSK = 2;
	public static final int COLUMN_PSK_USERNAME = 3;
	public static final int COLUMN_USER_GROUP = 4;
	public static final int COLUMN_START_TIME = 5;
	public static final int COLUMN_END_TIME = 6;
	public static final int COLUMN_EMAIL = 7;
	public static final int COLUMN_COMMENT = 8;
	public static final int COLUMN_SSID = 9;

	private static final int MAX_APS_PER_EXEC = 500;
	
	private static final int FULL_REVOKED = 0;
	private static final int PATAIL_REVOKED = 1;
	private static final int NO_REVOKED = 2;
	
	@Override
	public String execute() throws Exception {
		if("createAccounts".equals(operationClass)) {
			setSelectedL2Feature(L2_FEATURE_UM_TEMP_CREATE);
			MgrUtil.setSessionAttribute("lastOperationClass", operationClass);
		} else if("revokeAccounts".equals(operationClass)) {
			setSelectedL2Feature(L2_FEATURE_UM_TEMP_REVOKE);
			MgrUtil.setSessionAttribute("lastOperationClass", operationClass);
		} else {
			String lastOperationClass = (String)MgrUtil.getSessionAttribute("lastOperationClass");
			
			if(lastOperationClass.equals("createAccounts")) {
				setSelectedL2Feature(L2_FEATURE_UM_TEMP_CREATE);
			} else {
				setSelectedL2Feature(L2_FEATURE_UM_TEMP_REVOKE);
			}
			
			operationClass = lastOperationClass;
		}
		
		boolean isGroupLimited = false;
		
		if(checkOperatorPermission()) {
/*			HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
			
			Collection<LocalUserGroup> userGroups = user.getLocalUserGroups();*/

			// fix bug 28726
			Collection<LocalUserGroup> userGroups;
			if (isHmolAndUserWithCid()) {
				// if is HMOL and user has CID, get local user group from session user
				userGroups = getUserContext().getLocalUserGroups(this.getDomainId());
			} else {
				// HMOL user has no CID or HM user
				HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
				userGroups = user.getLocalUserGroups();
			}
			
			if(userGroups != null && !userGroups.isEmpty()) {
				isGroupLimited = true;
			String where = "userType = :s1 AND (status = :s2 OR status = :s3) AND revoked = :s4 AND localUserGroup IN (:s5)";
			filterParams = new FilterParams(where,
					new Object[] {LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,
					LocalUser.STATUS_ALLOCATED,
					LocalUser.STATUS_PARTIAL_REVOKED,
					Boolean.FALSE,
//					user.getLocalUserGroups()});
					userGroups});
			}
			
						
		} 
		
		if(!isGroupLimited) {
		String where = "userType = :s1 AND (status = :s2 OR status = :s3) AND revoked = :s4";
		filterParams = new FilterParams(where,
				new Object[] {LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,
				LocalUser.STATUS_ALLOCATED,
				LocalUser.STATUS_PARTIAL_REVOKED,
				Boolean.FALSE});
		}
		
		/*
		 * as for the L2 feature is set here(not in method prepare()), so need to
		 * reset the write permission 
		 */
		this.resetPermission();
	
		try {
			if("allocating".equals(operation)) {
				if(getAvailableUserCount() <= 0) {
					addActionError(MgrUtil.getUserMessage("info.gml.account.no.available")); 
					return prepareBoList();
				} else {
					return "allocate";
				}				
			} else if("allocated".equals(operation)) {
				if(dataSource != null) {
					prepareSubmit();
				}
				
				getDataSource().setStatus(LocalUser.STATUS_ALLOCATED);
				getDataSource().setOldPPSK(getDataSource().getStrPsk());
				id = getDataSource().getId();
				
				/*
				 * update database
				 */
				return updateBo();
				
				/*
				 * send message to AP
				 */
				// to be done
			} else if("email".equals(operation)) {
				emailAccounts();
				return prepareBoList();
			} else if("revoke".equals(operation)) {
				revokeAccounts();
				return prepareBoList();
			} else if("checkPrint".equals(operation)) {
				checkPrint();
				return "json";
			} else if("print".equals(operation)) {
				
				return operation;
			} else if("changeUserGroup".equals(operation)) {
				changeUserGroup();
				return "json";
			} else if("selectPPSK".equals(operation)) {
				if(this.ppskId != null) {
					LocalUser user = QueryUtil.findBoById(LocalUser.class, ppskId);
					setSessionDataSource(user);
					userGroupId = user.getLocalUserGroup().getId();
					
					jsonObject = new JSONObject();
					jsonObject.put("psk", user.getStrPsk());
					jsonObject.put("email", user.getMailAddress());
					jsonObject.put("start", trimTime(user.getStartTimeString()));
					jsonObject.put("expired", trimTime(user.getExpiredTimeString()));
					jsonObject.put("sponsor", getUserContext().getUserName());
					jsonObject.put("comment", user.getDescription());
					jsonObject.put("ssidList", getSsidsByUserGroups(userGroupId));
				}
				
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
			
		} catch(Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(LocalUser.class);
		keyColumnId = COLUMN_USER_NAME;
		tableId = HmTableColumn.TABLE_GML_TEMPORARAY;
	}
	/*
	 * Fetch user account list
	 * 
	 * the reason override this method is that PPSK accounts could be rotated.
	 * if a PPSK is replaced by a new rotated one, the related user account
	 * should be cleared.
	 */
	@Override
	protected String prepareBoList() throws Exception {
		clearRotatedAccounts(this.getDomain());
		return super.prepareBoList();
	}
	
	public static void clearRotatedAccounts(HmDomain curDomain) throws Exception {
		/*
		 * first, get all target local user out of database
		 */
		SortParams sort = new SortParams("id");
		String where = "userType = :s1 AND status != :s2 AND localUserGroup.blnBulkType = :s3 AND owner = :s4";
		FilterParams filter = new FilterParams(where,
				new Object[] {LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,
				LocalUser.STATUS_FREE,
				Boolean.TRUE,
				curDomain});
		
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
				
				if(user.getOldPPSK() == null) {
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
	
	private static void clearRotatedAccount(LocalUser user) {
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
	
	/*
	 * Update Hive Manager Business Object
	 * 
	 * the reason override this method is that after creating a temporary account,
	 * as usual, a notice will be displayed saying the object is updated successfully.
	 * however, the name of the object is default 'getLabel()'. in LocalUser, that would
	 * be the userName. but userName is not visible to UM operator.
	 * so here, display visitor name in the notice. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected HmBo updateBo(HmBo hmBo) throws Exception {
		if (hmBo == null || hmBo.getId() == null || !hmBo.getId().equals(id)) {
			throw new HmException(
					"Update object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Update" });
		}
		try {
			Date oldVer = hmBo.getVersion();
			hmBo = BoMgmt.updateBo(hmBo, getUserContext(),
					getSelectedL2FeatureKey());
			if (hmBo instanceof LocalUser) {
				log
						.info("updateBo",
								"Update LocalUser is customized for configuration indication.");
			} else {
				// generate an event to configuration indication process
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						hmBo, ConfigurationChangedEvent.Operation.UPDATE,
						oldVer));
			}
			generateAuditLog(HmAuditLog.STATUS_SUCCESS,
					MgrUtil.getUserMessage("info.gml.account.updated",
							new String[] {getLastTitle(), hmBo.getLabel()}));
		} catch (Exception e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE,
					MgrUtil.getUserMessage("info.gml.account.updated",
							new String[] {getLastTitle(), hmBo.getLabel()}));
			throw e;
		}
		
		if("allocated".equals(operation)) {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.created", 
					((LocalUser)hmBo).getVisitorName()));
		} else {
			addActionMessage(MgrUtil
					.getUserMessage(OBJECT_CREATED, hmBo.getLabel()));
		}
		
		return hmBo;
	}

	@Override
	public LocalUser getDataSource() {
		return (LocalUser)dataSource;
	}
	
	public String getChangedName() {
		return getDataSource().getUserName().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public String getShowCreate() {
		if("createAccounts".equals(operationClass)) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowRevoke() {
		if("revokeAccounts".equals(operationClass)) {
			return "";
		} else {
			return "none";
		}
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
		case COLUMN_USER_NAME:
			code = "gml.temporary.visitor";
			break;
		case COLUMN_PSK:
			code = "gml.temporary.psk";
			break;
		case COLUMN_PSK_USERNAME:
			code = "gml.clientmonitor.userName";
			break;
		case COLUMN_USER_GROUP:
			if(isFullMode()) {
				code = "gml.temporary.userGroup";
			} else {
				code = "gml.temporary.ssid";
			}
			
			break;
		case COLUMN_START_TIME:
			code = "gml.temporary.startTime";
			break;
		case COLUMN_END_TIME:
			code = "gml.temporary.endTime";
			break;
		case COLUMN_EMAIL:
			code = "gml.temporary.email";
			break;
		case COLUMN_COMMENT:
			code = "gml.temporary.comment";
			break;
		case COLUMN_SSID:
			code = "gml.temporary.ssid";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(9);
		
		columns.add(new HmTableColumn(COLUMN_USER_NAME));
		columns.add(new HmTableColumn(COLUMN_PSK));
		columns.add(new HmTableColumn(COLUMN_PSK_USERNAME));
		columns.add(new HmTableColumn(COLUMN_USER_GROUP));
		
		if(this.isFullMode()) {
			columns.add(new HmTableColumn(COLUMN_SSID));
		}
		
		columns.add(new HmTableColumn(COLUMN_START_TIME));
		columns.add(new HmTableColumn(COLUMN_END_TIME));
		columns.add(new HmTableColumn(COLUMN_EMAIL));
		columns.add(new HmTableColumn(COLUMN_COMMENT));
		
		return columns;
	}

	public int getVisitorNameLength() {
		return getAttributeLength("visitorName");
	}

	public int getVisitorCompanyLength() {
		return getAttributeLength("visitorCompany");
	}

	public int getSponsorLength() {
		return getAttributeLength("sponsor");
	}
	
	public int getSsidNameLength() {
		return getAttributeLength("ssidName");
	}
	
	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	
	public int getMailAddressLength() {
		return getAttributeLength("mailAddress");
	}
	
	private String operationClass;

	/**
	 * getter of operationClass
	 * @return the operationClass
	 */
	public String getOperationClass() {
		return operationClass;
	}

	/**
	 * setter of operationClass
	 * @param operationClass the operationClass to set
	 */
	public void setOperationClass(String operationClass) {
		this.operationClass = operationClass;
	}
	
	private void prepareSubmit() {
		if(this.isEasyMode()) {
			getDataSource().setSsidName(getDataSource().getLocalUserGroup().getGroupName());
		}
	}
	
	private void emailAccounts() {
		int count = 0;
		
		/*
		 * get mail settings
		 */
		List<MailNotification> mailNotification = QueryUtil.executeQuery(MailNotification.class,
															null, 
															null, 
															getDomain().getId());

		if (!mailNotification.isEmpty()) {
			String serverName = mailNotification.get(0)
					.getServerName();
			String mailFrom = mailNotification.get(0)
					.getMailFrom();
			
			if (serverName == null 
					|| serverName.equals("")) { 
				addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
						"SMTP server"));
				return ;
			}
			
			if(mailFrom == null
					|| mailFrom.equals("")) {
				addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
					"Source mail address"));
			}

		} else {
			addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
				"Email setting"));
			return;
		}

		/*
		 * set mail
		 */
		if (isAllItemsSelected()) {
			List<LocalUser> users = QueryUtil.executeQuery(
					LocalUser.class, 
					new SortParams("userName"), 
					filterParams,
					getDomainId());
			
			for (LocalUser user : users) {
				if(emailAccount(user, mailNotification)) {
					count++;
				}
			}
		} else {
			for (Long id : getAllSelectedIds()) {
				LocalUser user;
				
				try {
					user = findBoById(LocalUser.class, id);
					
					if(user == null) {
						continue;
					}
					
					if(emailAccount(user, mailNotification)) {
						count++;
					}
					
				} catch (Exception e) {
					log.error("Cannot get user(id=" + id + ") from database", e);
				}
			}
		}
		
		/*
		 * return result
		 */
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, 
					String.valueOf(count) + " accounts have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, 
					String.valueOf(count) + " account has "));
		}
	}
	
	private boolean emailAccount(LocalUser user, List<MailNotification> mailNotification) {
		if(user.getStatus() == LocalUser.STATUS_ALLOCATED) {
			if (!user.getMailAddress().equals("")) {
				if (sendMail(user, mailNotification)) {
					return true;
				} else {
					addActionError(MgrUtil.getUserMessage("error.gml.email.send.fail", 
							user.getMailAddress()));
				}
			} else {
				addActionError(MgrUtil.getUserMessage("error.gml.email.address.null", 
						user.getVisitorName()));
			}
		} else {
			addActionError(MgrUtil.getUserMessage("error.gml.email.status.wrong", 
					user.getVisitorName()));
		}
	
		return false;
	}
	
	private boolean sendMail(LocalUser user, List<MailNotification> mailNotification) {
		StringBuilder text = new StringBuilder();
		setUserId(user.getId());
		saveIntoSession();
		if(getHeaderHTML() != null){
			text.append("<div>").append(getHeaderHTML()).append("</div>");
		}
		List<PrintField> printFields = getTemplateFields();
		if(printFields != null){
			text.append("<table width='100%' border='0' cellspacing='0' cellpadding='0'><tr><td height='15'></td></tr>");
			text.append("<tr><td><table cellspacing='0' cellpadding='0' border='1' width='360px' style='word-break:break-all'>");
			for (PrintField printField : printFields) {
				text.append("<tr><td style='padding: 1px 0 1px 4px' align='left' width='30%'>").append(printField.getLabel()).append("</td>");
				text.append("<td style='padding: 1px 0 1px 4px' align='left' width='30%'>").append(printField.getValue()).append("&nbsp;</td></tr>");
			}
			text.append("</table></td></tr><tr><td height='5'></td></tr></table>");
		}
		if(getFooterHTML() != null){
			text.append("<div>").append(getFooterHTML()).append("</div>");
		}
		if (mailNotification != null && !mailNotification.isEmpty()) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification.get(0));
			mailUtil.setMailTo(user.getMailAddress());
			mailUtil.setSubject(user.getVisitorName() + " PSK");
			mailUtil.setText(text.toString());
			mailUtil.setMailContentType("text/html");
			try {
				mailUtil.startSend();
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}
	
	private void revokeAccounts() {
		int count = 0;
		Set<Long> groupRevoked = new HashSet<Long>();
		List<Long> userRevokedRotated = new ArrayList<Long>();
		List<Long> userRevokedNotRotated = new ArrayList<Long>();
		List<Long> userPartialRevokedRotated = new ArrayList<Long>();
		List<Long> userPartialRevokedNotRotated = new ArrayList<Long>();
		
		if (isAllItemsSelected()) {
			List<LocalUser> users = QueryUtil.executeQuery(
					LocalUser.class, 
					new SortParams("userName"), 
					filterParams,
					getDomainId());
			
			for (LocalUser user : users) {
				int rusult = revokeAccount(user);
				if(rusult == FULL_REVOKED){
					groupRevoked.add(user.getLocalUserGroup().getId());
					
					if(user.getLocalUserGroup().isBlnBulkType()) {
						userRevokedRotated.add(user.getId());
					} else {
						userRevokedNotRotated.add(user.getId());
					}
					
					
					count++;
				} else if(rusult == PATAIL_REVOKED){
					if(user.getLocalUserGroup().isBlnBulkType()) {
						userPartialRevokedRotated.add(user.getId());
					} else {
						userPartialRevokedNotRotated.add(user.getId());
					}
				}
			}
		} else {
			for (Long id : getAllSelectedIds()) {
				LocalUser user;
				
				try {
					user = findBoById(LocalUser.class, id);
					
					if(user == null) {
						continue;
					}
					int result = revokeAccount(user);
					if(result == FULL_REVOKED) {
						groupRevoked.add(user.getLocalUserGroup().getId());
						
						if(user.getLocalUserGroup().isBlnBulkType()) {
							userRevokedRotated.add(user.getId());
						} else {
							userRevokedNotRotated.add(user.getId());
						}
						
						count++;
					} else if(result == PATAIL_REVOKED) {
						if(user.getLocalUserGroup().isBlnBulkType()) {
							userPartialRevokedRotated.add(user.getId());
						} else {
							userPartialRevokedNotRotated.add(user.getId());
						}
					}
					
				} catch (Exception e) {
					log.error("Cannot get user(id=" + id + ") from database", e);
				}
			}
		}
		
		setAllSelectedIds(null);
		
		/*
		 * update database table
		 * set user status to REVOKED for rotated users
		 */
		if(!userRevokedRotated.isEmpty()) {
			Object[] binding = new Object[2];
			binding[0] = LocalUser.STATUS_REVOKED;
			binding[1] = userRevokedRotated;
			
			try {
				QueryUtil.updateBos(boClass, 
						"status=:s1", 
						"id in (:s2)",
						binding);
			} catch (Exception e) {
				
			}
		}
		
		/*
		 * update database table
		 * set user status to PARTIAL_REVOKED for rotated users
		 */
		if(!userPartialRevokedRotated.isEmpty()) {
			Object[] binding = new Object[2];
			binding[0] = LocalUser.STATUS_PARTIAL_REVOKED;
			binding[1] = userPartialRevokedRotated;
			
			try {
				QueryUtil.updateBos(boClass, 
						"status=:s1", 
						"id in (:s2)",
						binding);
			} catch (Exception e) {
				
			}
		}
		
		/*
		 * update database table
		 * set user status to REVOKED for non-rotated users
		 */
		if(!userRevokedNotRotated.isEmpty()) {
			Object[] binding = new Object[3];
			binding[0] = true;
			binding[1] = LocalUser.STATUS_REVOKED;
			binding[2] = userRevokedNotRotated;
			
			try {
				QueryUtil.updateBos(boClass, 
						"revoked=:s1, status=:s2", 
						"id in (:s3)",
						binding);
			} catch (Exception e) {
				
			}
		}
		
		/*
		 * update database table
		 * set user status to PARTIAL_REVOKED for non-rotated users
		 */
		if(!userPartialRevokedNotRotated.isEmpty()) {
			Object[] binding = new Object[2];
			binding[0] = LocalUser.STATUS_PARTIAL_REVOKED;
			binding[1] = userPartialRevokedNotRotated;
			
			try {
				QueryUtil.updateBos(boClass, 
						"status=:s1", 
						"id in (:s2)",
						binding);
			} catch (Exception e) {
				
			}
		}
		
		/*
		 * update database table
		 * if all users in a group are REVOKED, remove the group
		 */
		for(Long groupId : groupRevoked) {
			Object[] binding = new Object[2];
			binding[0] = false;
			binding[1] = groupId;
			
			FilterParams filter = new FilterParams("revoked=:s1 AND localUserGroup.id=:s2",
													binding);
			
			if(QueryUtil.findRowCount(boClass, filter) == 0) {
				try {
					removeAllBos(boClass, new FilterParams("localUserGroup.id",
							groupId), null);
				} catch (Exception e) {
					
				}
			}
		}
		/*
		 * return result
		 */
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.revoke", 
					String.valueOf(count) + " user accounts have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.revoke", 
					String.valueOf(count) + " user account has "));
		}
	}
	
	private int revokeAccount(LocalUser user) {
		if(user == null || user.getLocalUserGroup() == null){
			return NO_REVOKED;
		} else {
			return revokeFromAP(user);
		}
		
		//return !(user == null || user.getLocalUserGroup() == null) && revokeFromAP(user);
	}
	
	private int revokeFromAP(LocalUser user) {
		if(user == null) {
			return NO_REVOKED;
		}
		
		/*
		 * get HiveAPs
		 */
		Set<Long> apIds = ConfigurationUtils.getHiveAPsByLocalUser(user);
		
		if(apIds == null || apIds.isEmpty()) {
			this.addActionError(MgrUtil.getUserMessage("info.gml.revoke.account.isolated",
					new String[] {user.getVisitorName()}));
			return NO_REVOKED;
		}
		
		/*
		 * send CLI request
		 */
		StringBuilder cli = new StringBuilder("user-group ")
								.append(NmsUtil.handleBlank(user.getUserGroupName()))
								.append(" auto-generation revoke-user ");
		int userIndex = getUserIndex(user);
		
		if(userIndex == -1) {
			return NO_REVOKED;
		}
		
		cli.append(userIndex).append("\n");
		int failedCount = 0;
		List<HiveAp> hiveAps = new ArrayList<>();
		for(Long apid : apIds) {
			HiveAp ap = QueryUtil.findBoById(HiveAp.class, apid,new HiveApAction());
			
			if(ap == null) {
				log.error(MgrUtil.getUserMessage("error.gml.revoke.no.hiveap", 
						String.valueOf(apid)));
				continue;
			}
			
			//fix bug 16550
			if(ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200) {
				continue;
			}
			
			//fix bug 25505
			if(ap.isSwitchProduct()){
				boolean isPpskServer = HiveApUtils.isPpskServer(apid);
				if(!isPpskServer){
					continue;
				}
			}
			
			//fix bug 27870
			if(ap.isCVGAppliance()){
				continue;
			}
			
			hiveAps.add(ap);
		}
		
		List<String> failedHostNames = new ArrayList<>();
		
		if (hiveAps != null && !hiveAps.isEmpty()) {
			
			int apCounts = hiveAps.size();
			
			// deal max 500 APs per test
			int loops = (int)apCounts / MAX_APS_PER_EXEC + (apCounts % MAX_APS_PER_EXEC > 0 ? 1 : 0);
			int toIndex = 0;
			List<HiveAp> subApList;
			for (int i = 0; i < loops; i++) {
				toIndex = MAX_APS_PER_EXEC * (i + 1);
				toIndex = toIndex > apCounts ? apCounts : toIndex ;
				subApList = new ArrayList<HiveAp>(hiveAps.subList(MAX_APS_PER_EXEC * i, toIndex));
				
				failedCount = failedCount + executeCli(subApList,new String[] {cli.toString(), "save config\n"},failedHostNames);
			}
		}
		
		if(failedCount > 0) {
			
			String msg = "";
			if(failedHostNames.size() == 1){
				msg = "The failed device is "+failedHostNames.get(0);
			} else if(failedHostNames.size() > 1){
				msg = "The failed devices are ";
				for(String hostname : failedHostNames){
					msg += hostname + ",";
				}
				msg=msg.substring(0,msg.length()-1);
			}
			msg = msg + ".";
			
			this.addActionError(MgrUtil.getUserMessage("info.gml.revoke.fail.count",
					new String[] {user.getVisitorName(),
							String.valueOf(failedCount),
							String.valueOf(apIds.size())}) + msg);
			
			if(failedCount == hiveAps.size()){
				return NO_REVOKED;
			} else {
				return PATAIL_REVOKED;
			}
		}
		
		return FULL_REVOKED;
	}
	
	private int executeCli(List<HiveAp> hiveAps, String[] exeClis,List<String> failedHostNames) {
		List<BeCommunicationEvent> requests = new ArrayList<BeCommunicationEvent>();
		BeCliEvent req;
		int reqSeqNum;
		int failCount = 0;
		for (HiveAp hiveAp : hiveAps) {
			if (!hiveAp.isConnected()) {
				failedHostNames.add(hiveAp.getHostName());
				failCount++;
			} else {
				// ( connected & not simulate )AP 
				
				reqSeqNum = AhAppContainer.getBeCommunicationModule()
						.getSequenceNumber();
				req = new BeCliEvent();
				req.setAp(hiveAp);
				req.setClis(exeClis);
				req.setSequenceNum(reqSeqNum);
				try {
					req.buildPacket();
				} catch (BeCommunicationEncodeException e) {
					log.error("closeSshTunnel",
							"Failed to build activate account request for HiveAP '"
									+ hiveAp.getMacAddress() + "'.", e);
				}
				requests.add(req);
			}
		}
		
		// if all APs are disconnected, do not send connection test request.
		if (requests.isEmpty()) {
			return failCount;
		}
		
		// send test request
		List<BeCommunicationEvent> results = HmBeCommunicationUtil.sendSyncGroupRequest(requests,30);
		if (null != results) {
			for (BeCommunicationEvent result : results) {
				try {
					BeTopoModuleUtil.parseCliRequestResult(result);
					boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
					
					if(!isSuccess) {
						failedHostNames.add(result.getApNoQuery().getHostName());
						failCount++;
					}
				} catch (Exception e) {
					String clis = "";
					for(String cli : exeClis){
						clis+=cli;
					}
					log.error(MgrUtil.getUserMessage("error.gml.revoke.cli.failed", 
							new String[] {clis, result.getApNoQuery().getHostName()}), e);
					failedHostNames.add(result.getApNoQuery().getHostName());
					failCount++;
				}
			}
		}
		
		return failCount;
		
	}
	
	private int getUserIndex(LocalUser user) {
		if(user == null || user.getLocalUserGroup() == null) {
			return -1;
		}
		
		if(user.getLocalUserGroup().getUserType() 
				!= LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			return -1;
		}
		
		String userName = user.getUserName();
		String strIndex = userName.substring(user.getLocalUserGroup().getUserNamePrefix().length());
		
		try {
			return Integer.parseInt(strIndex);
		} catch(Exception e) {
			log.error("Error in getting the index of local user " + user.getLabel(), e);
			return -1;
		}
	}
	
	private void sortValuesByAlpha(Collection<CheckItem> options) {
    	//sort by alphabetically
    	if(options.toArray()[0] instanceof CheckItem){
    		Collections.sort((List<CheckItem>)options, new Comparator<CheckItem>() {
    			@Override
    			public int compare(CheckItem o1, CheckItem o2) {
    				if(o1.getId() < 0 ){
    					return -1;
    				}
    				if(o2.getId() < 0){
    					return 1;
    				}
    				return o1.getValue().compareToIgnoreCase(o2.getValue());
    			}
    		});
    	}
    }
	
	public List<CheckItem> getPrintTemplateList(){
		List<CheckItem> templates = new ArrayList<CheckItem>();
		
		/*
		 * get print templates from database
		 */
		List<PrintTemplate> bos = QueryUtil.executeQuery(PrintTemplate.class,
										new SortParams("asDefault", false),
										new FilterParams("enabled", Boolean.TRUE),
										this.getUserContext().getOwner().getId()
										, new MyLoader());
		
		for(PrintTemplate template : bos) {
			CheckItem item = new CheckItem(template.getId(), template.getName());
			templates.add(item);
		}
		
		if (templates.isEmpty()) {
			templates.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		sortValuesByAlpha(templates);
		return templates;
	}
	
	private Long templateId;
	
	public Long getSelectedTemplateId() {
		long result = 0;
		List<PrintTemplate> bos = QueryUtil.executeQuery(PrintTemplate.class,
										null,
										new FilterParams("enabled=:s1 and asDefault=:s2", new Object[]{Boolean.TRUE,Boolean.TRUE}),
										this.getUserContext().getOwner().getId()
										, new MyLoader());
		if(!bos.isEmpty()){
			result =  bos.get(0).getId();
		}
		
		return result;
	}

	/**
	 * getter of templateId
	 * @return the templateId
	 */
	public Long getTemplateId() {
		return templateId;
	}

	/**
	 * setter of templateId
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	private Long userId;
	
	/**
	 * getter of userId
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * setter of userId
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	private void checkPrint() throws Exception {
		jsonObject = new JSONObject();
		
		/*
		 * get selected bo
		 */
		LocalUser user = QueryUtil.findBoById(LocalUser.class, userId);
		
		if(user == null) {
			jsonObject.put("a", false);
			jsonObject.put("e", MgrUtil.getUserMessage("error.gml.print.account.lost"));
		}
		
		if(user.getStatus() == LocalUser.STATUS_ALLOCATED) {
			
			// allow print
			jsonObject.put("a", true); 			// allow or not
			jsonObject.put("u", userId);		// user id	
			jsonObject.put("t", templateId);	// template id
			
			saveIntoSession();
		} else {
			// not allow print
			jsonObject.put("a", false);
			jsonObject.put("e", MgrUtil.getUserMessage("error.gml.print.status.wrong"));
		}
	}
	
	private void changeUserGroup() throws Exception {
		/*
		 * check local user
		 */
		LocalUserGroup userGroup = QueryUtil.findBoById(LocalUserGroup.class, userGroupId);
		
		if(userGroup == null) {
			return ;
		}
		
		if(userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
				&& userGroup.isBlnBulkType()) {
			/*
			 * user in this kind of user group could be rotated.
			 * get one user from each rotation.
			 */
			getAvailableUsers(userGroup);
		} else {
			/*
			 * get the first local user of the user group 
			 */
			LocalUser user = getNextUser(userGroupId);
			
			if(user == null) {
				return ;
			}
			
			jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			
			setSessionDataSource(user);
			userGroupId = user.getLocalUserGroup().getId();
			jsonObject.put("psk", user.getStrPsk());
			jsonObject.put("email", user.getMailAddress());
			jsonObject.put("start", trimTime(user.getStartTimeString()));
			jsonObject.put("expired", trimTime(user.getExpiredTimeString()));
			jsonObject.put("sponsor", getUserContext().getUserName());
			jsonObject.put("comment", user.getDescription());
			jsonObject.put("ssidList", getSsidsByUserGroups(userGroupId));
			jsonArray.put(jsonObject);
		}
		
	}
	
	private void getAvailableUsers(LocalUserGroup group) throws Exception {
		int totalCount = (int)QueryUtil.findRowCount(LocalUser.class, 
				new FilterParams("localUserGroup.id",
						group.getId()));
		
		jsonArray = new JSONArray();
		int step = group.getIndexRange();
		LocalUser tempUser = null;
		
		for(int i=0; i<totalCount/step; i++) {
			String sql = "SELECT id FROM local_user WHERE status=1 AND revoked='f' AND group_id=" + group.getId() +
					" AND to_number(substr(username, (char_length(username) - 3), 4), '9999') >= " + (i * step + 1) +
					" AND to_number(substr(username, (char_length(username) - 3), 4), '9999') <= " + (i+1) * step +
					" ORDER BY username";

			List<?> users = QueryUtil.executeNativeQuery(sql);
			
			if(users.isEmpty()) {
				continue;
			}
			
			
			LocalUser user = QueryUtil.findBoById(LocalUser.class, Long.valueOf(users.get(0).toString()));
			
			if(user == null
				|| "Expired".equals(user.getExpiredTimeString())) {
				continue;
			}
			
			tempUser = user;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", user.getId());
			jsonObject.put("psk", user.getStrPsk());
			jsonObject.put("email", user.getMailAddress());
			jsonObject.put("start", trimTime(user.getStartTimeString()));
			jsonObject.put("expired", trimTime(user.getExpiredTimeString()));
			jsonObject.put("sponsor", getUserContext().getUserName());
			jsonObject.put("comment", user.getDescription());
			jsonObject.put("ssidList", getSsidsByUserGroups(userGroupId));
			jsonArray.put(jsonObject);
		}
		
		if(jsonArray.length() == 1 && tempUser != null) {
			setSessionDataSource(tempUser);
		}
	}
	
	public List<PrintField> getTemplateFields(){
		getFromSession();
		
		if(getTemplateId() == null 
				|| getUserId() == null) {
			return null;
		}
		
		PrintTemplate template = QueryUtil.findBoById(PrintTemplate.class, getTemplateId(), new MyLoader());
		
		if(template == null) {
			return null;
		}
		
		LocalUser user = QueryUtil.findBoById(LocalUser.class, getUserId());
		
		if(user == null) {
			return null;
		}
		
		List<TemplateField> tempFields = template.getTemplateFields();
		List<PrintField> printFields = new ArrayList<PrintField>();
		
		for(TemplateField field : tempFields) {
			if(field.getRequired()) {
				String fieldName = TemplateField.getFieldName(field.getLabel());
				String methodName = "get" 
									+ fieldName.substring(0, 1).toUpperCase() 
									+ fieldName.substring(1);
				try {
					Class<?> c = user.getClass();
					Method m = c.getMethod(methodName);
					printFields.add(new PrintField(field.getLabel(), String.valueOf(m.invoke(user, (Object[])null))));
				} catch(Exception e) {
					log.error("Failed get field value from LocalUser object", e);
				}
			}
		}
		
		return printFields;
	}
	
	public String getHeaderHTML() {
		getFromSession();
		
		if(getTemplateId() == null 
				|| getUserId() == null) {
			return null;
		}
		
		PrintTemplate template = QueryUtil.findBoById(PrintTemplate.class, getTemplateId(), new MyLoader());
		
		if(template == null) {
			return null;
		}

		return template.getHeaderHTML();
	}
	
	public String getFooterHTML() {
		getFromSession();
		
		if(getTemplateId() == null 
				|| getUserId() == null) {
			return null;
		}
		
		PrintTemplate template = QueryUtil.findBoById(PrintTemplate.class, getTemplateId(), new MyLoader());
		
		if(template == null) {
			return null;
		}
		
		return template.getFooterHTML();
	}

	public String getLeftUserCount() {
		long count = getAvailableUserCount();
		String key = count > 1 ? "info.gml.account.counts.left" 
								: "info.gml.account.count.left";
		
		return MgrUtil.getUserMessage(key,String.valueOf(count));
	}
	
	private void saveIntoSession() {
		String userKey = this.getUserContext().getId() + "User";
		MgrUtil.setSessionAttribute(userKey, userId);
		String templateKey = this.getUserContext().getId() + "Template";
		MgrUtil.setSessionAttribute(templateKey, templateId);
	}
	
	private void getFromSession() {
		String userKey = this.getUserContext().getId() + "User";
		userId = (Long)MgrUtil.getSessionAttribute(userKey);
		String templateKey = this.getUserContext().getId() + "Template";
		templateId = (Long)MgrUtil.getSessionAttribute(templateKey);
	}
	
	private long getAvailableUserCount() {
		/*
		 * get user groups from database
		 */
		StringBuilder sql = new StringBuilder("SELECT DISTINCT u.id FROM local_user u, local_user_group g, user_profile p");
		sql.append(" WHERE u.usertype=").append(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
		sql.append(" AND u.status=").append(LocalUser.STATUS_FREE);
		sql.append(" AND u.revoked='f'");
		
		if(checkOperatorPermission()) {
/*			HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
			Collection<LocalUserGroup> userGroups = user.getLocalUserGroups();*/

			// fix bug 28726
			Collection<LocalUserGroup> userGroups;
			if (isHmolAndUserWithCid()) {
				// if is HMOL and user has CID, get local user group from session user
				userGroups = getUserContext().getLocalUserGroups(this.getDomainId());
			} else {
				// HMOL user has no CID or HM user
				HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
				userGroups = user.getLocalUserGroups();
			}
			
			if(userGroups != null && !userGroups.isEmpty()) {
				sql.append(" AND g.id IN (");
				
				int i=0;
				
				for(LocalUserGroup group : userGroups) {
					if(i++ == 0) {
						sql.append(group.getId());
					} else {
						sql.append(",").append(group.getId());
					}
				}
				
				sql.append(")");
			}
		}
		
		sql.append(" AND u.group_id=g.id");
		sql.append(" AND g.owner=").append(this.getDomainId());
		sql.append(" AND g.userprofileid = p.attributevalue");
		sql.append(" AND p.blnusermanager=true");
		
		List<?> bos = QueryUtil.executeNativeQuery(sql.toString());
		
		int count = 0;
		
		for(Object bo : bos) {
			if(bo == null) {
				continue;
			}
			
			LocalUser user = QueryUtil.findBoById(LocalUser.class, Long.valueOf(bo.toString()));
			
			if(user.getLocalUserGroup().isBlnBulkType()) {
				if(!"Expired".equals(user.getExpiredTimeString())) {
					count++;
				}
			} else {
				count++;
			}
		}
		
		return count;
	}
	
	private LocalUser getNextUser(Long userGroup) {
		String where = "userType = :s1 AND status = :s2 AND group_id = :s3 AND revoked = :s4";
		FilterParams filter = new FilterParams(where,
				new Object[] {LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK,
				LocalUser.STATUS_FREE,
				userGroup,
				Boolean.FALSE});
		
		List<?> users = QueryUtil.executeQuery(boClass, 
				new SortParams("id"), 
				filter, 
				domainId,
				1);
		
		if(users.isEmpty()) {
			return null;
		} else {
			return (LocalUser)users.get(0);
		}
	}
	
	private Long ppskId;
	
	
	public Long getPpskId() {
		return ppskId;
	}

	public void setPpskId(Long ppskId) {
		this.ppskId = ppskId;
	}

	private Long userGroupId;
	
	/**
	 * getter of userGroupId
	 * @return the userGroupId
	 */
	public Long getUserGroupId() {
		return userGroupId;
	}

	/**
	 * setter of userGroupId
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}
	
	public List<CheckItem> getAvailableUserGroups() {
		List<CheckItem> groups = new ArrayList<CheckItem>();
		
		/*
		 * get user groups from database
		 */
		StringBuilder sql = new StringBuilder("SELECT DISTINCT u.group_id, g.groupname FROM local_user u, local_user_group g, user_profile p");
		sql.append(" WHERE u.usertype=").append(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
		sql.append(" AND u.status=").append(LocalUser.STATUS_FREE);
		
		if(checkOperatorPermission()) {
/*			HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
			Collection<LocalUserGroup> userGroups = user.getLocalUserGroups();*/

			// fix bug 28726
			Collection<LocalUserGroup> userGroups;
			if (isHmolAndUserWithCid()) {
				// if is HMOL and user has CID, get local user group from session user
				userGroups = getUserContext().getLocalUserGroups(this.getDomainId());
			} else {
				// HMOL user has no CID or HM user
				HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
				userGroups = user.getLocalUserGroups();
			}
			
			if(userGroups != null && !userGroups.isEmpty()) {
			sql.append(" AND g.id IN (");
			
			int i=0;
			
			for(LocalUserGroup group : userGroups) {
				if(i++ == 0) {
					sql.append(group.getId());
				} else {
					sql.append(",").append(group.getId());
				}
			}
			
			sql.append(")");				
		}
		}
		
		sql.append(" AND u.group_id=g.id");
		sql.append(" AND g.owner=").append(this.getDomainId());
		sql.append(" AND g.userprofileid = p.attributevalue");
		sql.append(" AND p.blnusermanager=true");
		sql.append(" ORDER BY g.groupname");
		
		
		List<?> bos = QueryUtil.executeNativeQuery(sql.toString());
		
		for(Object obj : bos) {
			Object[] group = (Object[])obj;
			BigInteger groupId = (BigInteger)group[0]; 
			CheckItem item = new CheckItem(groupId.longValue(), (String)group[1]);
			groups.add(item);
		}
		
		if (groups.isEmpty()) {
			groups.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		
		return groups;
	}
	
	/*
	 * Trim seconds in a time string
	 * 07-26-2011 10:00:00  --->  07-26-2011 10:00
	 */
	private String trimTime(String time) {
		if(time == null) {
			return null;
		}
		
		if(!time.contains(":")) {
			return time;
		}
		
		return time.substring(0, time.lastIndexOf(':'));
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmUser) {
// cchen DONE			
//			if (((HmUser)bo).getSsidProfiles() != null) {
//				((HmUser)bo).getSsidProfiles().size();
//			}
//			if (((HmUser)bo).getLocalUserGroups() != null) {
//				((HmUser)bo).getLocalUserGroups().size();
//			}
		}
		return null;
	}
	
	//this method have not been used.
	public List<String> getOptionSsidName(long userGroupId){
		List<String> ssidNames = new ArrayList<String>();
		StringBuilder sql = new StringBuilder("select T.ssid,T.ssidName from (select s.id ssid,lug.id ugid,s.ssidName ssidName from ssid_profile  s,SSID_LOCAL_USER_GROUP slug, LOCAL_USER_GROUP lug where s.id=slug.SSID_PROFILE_ID and lug.id=slug.LOCAL_USER_GROUP_ID union select s.id ssid,lug.id ugid,s.ssidName ssidName from ssid_profile  s,SSID_RADIUS_USER_GROUP srug, LOCAL_USER_GROUP lug where s.id=srug.SSID_PROFILE_ID and lug.id=srug.LOCAL_USER_GROUP_ID) T");
		sql.append(" where T.ugid=").append(userGroupId);
		List<?> ssids = QueryUtil.executeNativeQuery(sql.toString());
		for(Object object : ssids) {
			Object[] attributes = (Object[]) object;
			long id = attributes[0] == null ? null :Long.parseLong(attributes[0].toString());
			String name = attributes[1] == null ? null :attributes[1].toString();
			StringBuilder sqlCount = new StringBuilder("select count(*) from (select s.id ssid from ssid_profile s ,USER_PROFILE u where (s.USERPROFILE_DEFAULT_ID =u.id or s.userprofile_selfreg_id=u.id) and u.blnUserManager = true union select s.id ssid from ssid_profile s ,USER_PROFILE u ,SSID_PROFILE_USER_PROFILE su where (s.USERPROFILE_DEFAULT_ID =u.id or s.userprofile_selfreg_id=u.id or (s.id =su.SSID_PROFILE_ID and u.id = su.USER_PROFILE_ID)) and u.blnUserManager = true) as t");
			sqlCount.append(" where t.ssid = ").append(id);
			List<?> counts = QueryUtil.executeNativeQuery(sqlCount.toString());
			long availableRowCount;
			if (counts == null || counts.isEmpty()) {
				availableRowCount = 0;
			} else {
				availableRowCount =Long.parseLong(counts.get(0).toString());
			}
			if(availableRowCount > 0){
				ssidNames.add(name);
			}
		}
		return ssidNames;
	}
	
	// check SSID--UserProfile
	//The SSID Access Security with Private PSK.
	// user profile must enabled "Manage users for profile via User Manager"
	public boolean checkSsidUserProfile(long ssid){
		StringBuilder sqlCount = new StringBuilder("select count(*) from (select s.id ssid from ssid_profile s ,USER_PROFILE u where (s.USERPROFILE_DEFAULT_ID =u.id or s.userprofile_selfreg_id=u.id) and u.blnUserManager = true and  s.accessMode =2  union select s.id ssid from ssid_profile s ,USER_PROFILE u ,SSID_PROFILE_USER_PROFILE su where (s.USERPROFILE_DEFAULT_ID =u.id or s.userprofile_selfreg_id=u.id or (s.id =su.SSID_PROFILE_ID and u.id = su.USER_PROFILE_ID)) and u.blnUserManager = true and s.accessMode =2) as t");
		sqlCount.append(" where t.ssid = ").append(ssid);
		List<?> counts = QueryUtil.executeNativeQuery(sqlCount.toString());
		long availableRowCount;
		if (counts == null || counts.isEmpty()) {
			availableRowCount = 0;
		} else {
			availableRowCount =Long.parseLong(counts.get(0).toString());
		}
		
		return availableRowCount>0;
	}
	
	// check SSID--UserGroup
	//The SSID Access Security with Private PSK.
	// user type of User Group must be Automatically generated private PSK users. --> bug 27869
	
	// user group must be selected in page --> bug 30571
	public boolean checkSsidUserGroup(long ssid,long groupId){
		StringBuilder sqlCount = new StringBuilder("select count(*) from (select s.id ssid,lug.userType userType, s.accessMode accessMode,lug.id usergroupid from ssid_profile s,SSID_LOCAL_USER_GROUP slug, LOCAL_USER_GROUP lug where s.id=slug.SSID_PROFILE_ID and lug.id=slug.LOCAL_USER_GROUP_ID union select s.id ssid,lug.userType userType, s.accessMode accessMode,lug.id usergroupid from ssid_profile  s,SSID_RADIUS_USER_GROUP srug, LOCAL_USER_GROUP lug where s.id=srug.SSID_PROFILE_ID and lug.id=srug.LOCAL_USER_GROUP_ID) as t");
		sqlCount.append(" where t.userType=2 and t.accessMode =2 and t.ssid = ").append(ssid).append(" and t.usergroupid=").append(groupId);
		List<?> counts = QueryUtil.executeNativeQuery(sqlCount.toString());
		long availableRowCount;
		if (counts == null || counts.isEmpty()) {
			availableRowCount = 0;
		} else {
			availableRowCount =Long.parseLong(counts.get(0).toString());
		}
		
		return availableRowCount>0;
	}
	
	private List<String> getSsidsByUserGroups(long groupId){
		List<String> ssidNames = new ArrayList<String>();
		
		if(checkOperatorPermission()) {
/*			HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
			Collection<SsidProfile> ssids = user.getSsidProfiles();*/

			// fix bug 28726
			Collection<SsidProfile> ssids;
			if (isHmolAndUserWithCid()) {
				// if is HMOL and user has CID, get local user group from session user
				ssids = getUserContext().getSsidProfiles(this.domainId);
			} else {
				// HMOL user has no CID or HM user
				HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
				ssids = user.getSsidProfiles();
			}
			
			if(ssids != null && !ssids.isEmpty()) {
			for(SsidProfile ssid : ssids) {
				if(checkSsidUserProfile(ssid.getId()) && checkSsidUserGroup(ssid.getId(),groupId)){
					ssidNames.add(ssid.getSsid());
				}
			}
				
				return ssidNames;
			}
		} 
	
			List<CheckItem> ssids = getBoCheckItems("ssid", SsidProfile.class, null);
			
			if(ssids == null) {
				return ssidNames;
			}
			
			for(CheckItem item : ssids) {
				if(checkSsidUserProfile(item.getId()) && checkSsidUserGroup(item.getId(),groupId)){
					ssidNames.add(item.getValue());
				}
			}
		
		return ssidNames;
	}
	
	public List<String> getAvailableSsids() {
		List<String> ssidNames = new ArrayList<String>();
		List<CheckItem> groups = getAvailableUserGroups();
		long groupId = groups.get(0).getId();
		ssidNames = getSsidsByUserGroups(groupId);
		
		return ssidNames;
	}
	
	private class MyLoader implements QueryBo{

		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof PrintTemplate) {
				PrintTemplate template = (PrintTemplate)bo;
				
				if(template.getFields() != null) {
					template.getFields().size();
				}
			}
			return null;
		}
		
	}
	
	private boolean checkOperatorPermission() {
		AhAuthAgent authAgent = AhAuthFactory.getInstance().getAuthAgent();
		
		if(authAgent.getAuthMethod() == AuthMethod.RADIUS) {
			return false;
		}
		
		if(HmUserGroup.GM_OPERATOR.equals(this.getUserContext().getUserGroup().getGroupName())) {
/*			HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
			
			if(user != null) {
				return true;
			}*/
			
			// fix bug 28726
			if (isHmolAndUserWithCid()) {
				// if is HMOL and user has CID, get local user group from session user
				return true;
			} else {
				// HMOL user has no CID or HM user
				HmUser user = QueryUtil.findBoById(HmUser.class, getUserContext().getId(), this);
				if(user != null) {
					return true;
				}
			}
		} 
		
		return false;
	}
	
	/**
	 * if is HMOL and user has CID
	 * 
	 * @return
	 */
	private boolean isHmolAndUserWithCid() {
		if (NmsUtil.isHostedHMApplication() && !StringUtils.isEmpty(getUserContext().getCustomerId())) {
			return true;
		}
		
		return false;
	}
	
}
