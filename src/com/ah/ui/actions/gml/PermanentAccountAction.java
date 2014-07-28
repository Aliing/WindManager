/**
 * @filename			PermanentAccountAction.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.gml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.gml.PrintField;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class PermanentAccountAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer log = new Tracer(PermanentAccountAction.class
			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_USER_NAME = 1;
	public static final int COLUMN_USER_GROUP = 2;
	public static final int COLUMN_ACTIVATION = 3;
	public static final int COLUMN_DESCRIPTION = 4;
	public static final int COLUMN_EMAIL = 5;
	public static final int COLUMN_START_TIME = 6;
	public static final int COLUMN_END_TIME = 7;

	private static final int MAX_APS_PER_EXEC = 500;
	
	public String execute() throws Exception {
		/*
		 * get available user groups
		 */
		initUserGroups();
		List<Long> groupIds = new ArrayList<Long>();
		
		for(CheckItem item : this.localUserGroup) {
			groupIds.add(item.getId());
		}
		
		filterParams = new FilterParams("group_id", groupIds);
		setSessionFiltering();
		
		try {
			if("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("gml.permanent.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				
				LocalUser user = new LocalUser();
				user.setUserType(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
				setSessionDataSource(user);
				prepareView();
				return INPUT;
			} else if("create".equals(operation)) {
				prepareSubmit();
				
				if (checkNameExists("userName", getDataSource().getUserName())) {
					prepareView();
					return INPUT;
				}
			
				if (!checkPassword()) {
					prepareView();
					return INPUT;
				}
			
				this.getSessionFiltering();
				String result = createBo();
				// For configuration indication, specially for LocalUser
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						getDataSource(),
						ConfigurationChangedEvent.Operation.CREATE, null));
				return result;
				
			} else if("edit".equals(operation)) {
				String strForward = editBo();
				
				if (dataSource != null) {
					addLstTitle(getText("config.title.localUser.edit") + " '"
							+ getChangedName() + "'");
				}
				
				prepareView();
				return strForward;				
			} else if("activate".equals(operation)) {
				activateAccounts();
				return prepareBoList();
				
			}  else if("email".equals(operation)) {
				emailAccounts();
				return prepareBoList();
			}  else if("print".equals(operation)) {
				preparePrint();
				return operation;
			} else if("update".equals(operation)) {
				if(dataSource != null) {
					prepareSubmit();
				}
				
				// get the previous LocalUser;
				LocalUser bo = QueryUtil.findBoById(LocalUser.class, dataSource
						.getId());
				this.getSessionFiltering();
				getDataSource().setActivated(false);
				String result = updateBo();
				
				// For configuration indication, specially for LocalUser
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						bo, ConfigurationChangedEvent.Operation.UPDATE,
						null));
				
				return result;
			} else if("remove".equals(operation)) {
				removeAccounts();
				return prepareBoList();
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch(Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_UM_PERM);
		setDataSource(LocalUser.class);
		keyColumnId = COLUMN_USER_NAME;
		this.tableId = HmTableColumn.TABLE_GML_PERMANENT;

	}
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_USER_NAME:
			code = "gml.temporary.userName";
			break;
		case COLUMN_USER_GROUP:
			code = "gml.temporary.userGroup";
			break;
		case COLUMN_ACTIVATION:
			code = "gml.permanent.activation";
			break;
		case COLUMN_DESCRIPTION:
			code = "gml.permanent.description";
			break;
		case COLUMN_EMAIL:
			code = "gml.temporary.email";
			break;
		case COLUMN_START_TIME:
			code = "gml.temporary.startTime";
			break;
		case COLUMN_END_TIME:
			code = "gml.temporary.endTime";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_USER_NAME));
		columns.add(new HmTableColumn(COLUMN_USER_GROUP));
		columns.add(new HmTableColumn(COLUMN_ACTIVATION));
		columns.add(new HmTableColumn(COLUMN_START_TIME));
		columns.add(new HmTableColumn(COLUMN_END_TIME));
		columns.add(new HmTableColumn(COLUMN_EMAIL));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	public String getChangedName() {
		return getDataSource().getUserName().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	private void prepareView() {
		
	}
	
	private void initUserGroups() {
		localUserGroup = new ArrayList<CheckItem>();
		
		/*
		 * get user groups from database
		 */
		StringBuffer sql = new StringBuffer("SELECT DISTINCT g.id, g.groupname FROM local_user_group g, user_profile p");
		sql.append(" WHERE g.usertype=").append(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
		sql.append(" AND g.owner=").append(this.getDomainId());
		sql.append(" AND g.userprofileid = p.attributevalue");
		sql.append(" AND p.blnusermanager=true");
		sql.append(" AND P.owner=").append(this.getDomainId());
		sql.append(" ORDER BY g.id");
		
		List<?> bos = QueryUtil.executeNativeQuery(sql.toString());
		
		for(Object obj : bos) {
			Object[] group = (Object[])obj;
			BigInteger groupId = (BigInteger)group[0]; 
			CheckItem item = new CheckItem(groupId.longValue(), (String)group[1]);
			localUserGroup.add(item);
		}
		
		if (localUserGroup.size() == 0) {
			localUserGroup.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}
	
	private void prepareSubmit() throws Exception {
		setUserGroup();
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
	
	private List<CheckItem> localUserGroup;
	
	public List<CheckItem> getLocalUserGroup() {
		return localUserGroup;
	}

	private Long userGroupId;
	
	public Long getUserGroupId() {
		if (null == userGroupId)
			userGroupId = getDataSource().getLocalUserGroup().getId();
		
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}
	
	public int getUserNameLength() {
		return getAttributeLength("userName");
	}
	
	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public int getMailAddressLength() {
		return getAttributeLength("mailAddress");
	}
	
	private String password;
	
	private String confirmPassword;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password != null){
			this.password = password.trim();
		} else {
			this.password = password;
		}
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		if (confirmPassword != null){
			this.confirmPassword = confirmPassword.trim();
		} else {
			this.confirmPassword= confirmPassword;
		}
	}
	
	public LocalUser getDataSource() {
		return (LocalUser) dataSource;
	}

	private void activateAccounts() {
		int count = 0;
		
		if (isAllItemsSelected()) {
			List<LocalUser> users = QueryUtil.executeQuery(
					LocalUser.class, 
					new SortParams("userName"), 
					filterParams,
					getDomainId());
			
			for (LocalUser user : users) {
				if(activateAccount(user)){
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
					
					if(activateAccount(user)) {
						count++;
					}
					
				} catch (Exception e) {
					log.error("Cannot get user(id=" + id + ") from database", e);
				}
			}
		}
		
		setAllSelectedIds(null);
		
		/*
		 * return result
		 */
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.activate", 
					String.valueOf(count) + " user accounts have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.activate", 
					String.valueOf(count) + " user account has "));
		}
	}
	
	private boolean activateAccount(LocalUser user) {
		if(user == null || user.getLocalUserGroup() == null) {
			return false;
		}
		
		/*
		 * get HiveAPs
		 */
		Set<Long> apIds = ConfigurationUtils.getHiveAPsByLocalUser(user);
		
		if(apIds == null || apIds.size() == 0) {
			this.addActionMessage(MgrUtil.getUserMessage("info.gml.activate.account.isolated",
					new String[] {user.getUserName()}));
			
			return false;
		}

		int failCount = 0;
		List<HiveAp> hiveAps = new ArrayList<>();
		
		for(Long apid : apIds) {
			HiveAp ap = QueryUtil.findBoById(HiveAp.class, apid);
			
			if(ap == null) {
				log.error(MgrUtil.getUserMessage("error.gml.revoke.no.hiveap", 
						String.valueOf(apid)));
				continue;
			}
			
			//fix bug 27870
			if(ap.isCVGAppliance()){
				continue;
			}
			
			hiveAps.add(ap);
		}
		
		if (hiveAps != null && !hiveAps.isEmpty()) {
			failCount = failCount + splitDeviceToExecCli(hiveAps,getActivateExecClis(user));
		}
		
		if(failCount > 0) {
			this.addActionMessage(MgrUtil.getUserMessage("info.gml.activate.fail.count",
											new String[] {user.getUserName(),
															String.valueOf(failCount),
															String.valueOf(apIds.size())})
									);
			
			/*
			 * update activation status
			 */
			user.setActivated(false);
			
			try {
				QueryUtil.updateBo(user);
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.gml.permanent.activate.db.update.error", 
						user.getUserName()));
				this.addActionError(MgrUtil.getUserMessage("error.gml.permanent.activate.db.update.error", 
						user.getUserName()));
			}
			
			return false;
		} else {
			/*
			 * update activation status
			 */
			user.setActivated(true);
			
			try {
				QueryUtil.updateBo(user);
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.gml.permanent.activate.db.update.error", 
						user.getUserName()));
				this.addActionError(MgrUtil.getUserMessage("error.gml.permanent.activate.db.update.error", 
						user.getUserName()));
			}
		}
		
		return true;
	}
	
	/**
	 * if localUser password contain space, ?, \ and ", need input between " and "" and ";
	 * \ and " need input \ before it.
	 *
	 * @param password -
	 * @return -
	 * @author wpliang
	 */
	private String formatLocalUserPassword(String password){
		String result = "";
		if(password == null){
			result = password;
		} else {
			//\ and " need input \ before it.
			result = password.replace("\\", "\\\\").replace("\"", "\\\"");
			if(result.contains(" ") || result.contains("?") || result.contains("\\")||result.contains("\"")){
				result = "\""+result+"\"";
			}
		}
		return result;
	}
	
	private String[] getActivateExecClis(LocalUser user){
		String userName = NmsUtil.handleBlank(user.getUserName());
		String localUserPassword = formatLocalUserPassword(user.getLocalUserPassword());
		String groupName = user.getUserGroupName().indexOf(' ') == -1 
				? user.getUserGroupName() : "\"" + user.getUserGroupName() + "\"";
		String cli1 = "user " + userName + "\n";
		String cli2 = "user " + userName + " password " + localUserPassword  + "\n";
		String cli3 = "user " + userName + " group " + groupName + "\n";
		String cli4 = "save config users\n";
		String[] exeClis = new String[4];
		exeClis[0] = cli1;
		exeClis[1] = cli2;
		exeClis[2] = cli3;
		exeClis[3] = cli4;
		
		return exeClis;
	}
	
	private String[] getRemoveUserExecClis(LocalUser user){
		String cli1 = "no user " + NmsUtil.handleBlank(user.getUserName()) + "\n";
		String cli2 = "save config users\n";
		String[] exeClis = new String[2];
		exeClis[0] = cli1;
		exeClis[1] = cli2;
		
		return exeClis;
	}
	
	private int splitDeviceToExecCli(List<HiveAp> hiveAps, String[] exeClis){
		int failCount = 0;
		int apCounts = hiveAps.size();
		
		// deal max 500 APs per test
		int loops = (int)apCounts / MAX_APS_PER_EXEC + (apCounts % MAX_APS_PER_EXEC > 0 ? 1 : 0);
		int toIndex = 0;
		List<HiveAp> subApList;
		for (int i = 0; i < loops; i++) {
			toIndex = MAX_APS_PER_EXEC * (i + 1);
			toIndex = toIndex > apCounts ? apCounts : toIndex ;
			subApList = new ArrayList<HiveAp>(hiveAps.subList(MAX_APS_PER_EXEC * i, toIndex));
			
			failCount = failCount + executeCli(subApList,exeClis);
		}
		
		return failCount;
	}
	
	private int executeCli(List<HiveAp> hiveAps, String[] exeClis) {
		List<BeCommunicationEvent> requests = new ArrayList<BeCommunicationEvent>();
		BeCliEvent req;
		int reqSeqNum;
		int failCount = 0;
		for (HiveAp hiveAp : hiveAps) {
			if (!hiveAp.isConnected()) {
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
					String msg = BeTopoModuleUtil.parseCliRequestResult(result);
					boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
					
					if(!isSuccess) {
						if(msg!=null && !msg.contains("no&nbsp;user&nbsp;")){ // fix remove issue
							this.addActionMessage(msg);
							failCount++;
						}
					}
				} catch (Exception e) {
					String clis = "";
					for(String cli : exeClis){
						clis+=cli;
					}
					log.error(MgrUtil.getUserMessage("error.gml.revoke.cli.failed", 
							new String[] {clis, result.getApNoQuery().getHostName()}), e);
					failCount++;
				}
			}
		}
		
		return failCount;
		
	}
	
	private void removeAccounts() {
		Set<Long> removedAccounts = new HashSet<Long>();
		
		/*
		 * remove from AP
		 */
		if (isAllItemsSelected()) {
			List<LocalUser> users = QueryUtil.executeQuery(
					LocalUser.class, 
					new SortParams("userName"), 
					filterParams,
					getDomainId());
			
			for (LocalUser user : users) {
				if(user.isActivated()) {
					if(removeAccountFromAP(user)){
						removedAccounts.add(user.getId());
					}
				} else {
					removedAccounts.add(user.getId());
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
					
					if(user.isActivated()) {
						if(removeAccountFromAP(user)) {
							removedAccounts.add(id);
						}
					} else {
						removedAccounts.add(id);
					}
					
					
				} catch (Exception e) {
					log.error("Cannot get user(id=" + id + ") from database", e);
				}
			}
		}
		
		setAllSelectedIds(null);
		
		/*
		 * remove from database
		 */
		int count = 0;
		
		try {
			count = removeBos(boClass, removedAccounts);
		} catch (Exception e) {
			log.error("Error occurred when removing local users", e);
			
			if(removedAccounts.size() == 1) {
				MgrUtil.getUserMessage("error.gml.permanent.remove.db.error", 
						new String[] {String.valueOf(removedAccounts.size())});
			} else {
				MgrUtil.getUserMessage("error.gml.permanent.remove.db.errors", 
						new String[] {String.valueOf(removedAccounts.size())});
			}
		}
		
		/*
		 * return result
		 */
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.remove", 
					String.valueOf(count) + " user accounts have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage("info.gml.account.remove", 
					String.valueOf(count) + " user account has "));
		}
	}
	
	private boolean removeAccountFromAP(LocalUser user) {
		if(user == null || user.getLocalUserGroup() == null) {
			return false;
		}
		
		/*
		 * get HiveAPs
		 */
		Set<Long> apIds = ConfigurationUtils.getHiveAPsByLocalUser(user);
		
		if(apIds == null || apIds.size() == 0) {
			return true;
		}

		int failCount = 0;
		List<HiveAp> hiveAps = new ArrayList<>();
		for(Long apid : apIds) {
			HiveAp ap = QueryUtil.findBoById(HiveAp.class, apid);
			
			if(ap == null) {
				log.error(MgrUtil.getUserMessage("error.gml.revoke.no.hiveap", 
						String.valueOf(apid)));
				continue;
			}
			
			hiveAps.add(ap);
		}
		
		if (hiveAps != null && !hiveAps.isEmpty()) {
			
			failCount = failCount + splitDeviceToExecCli(hiveAps,getRemoveUserExecClis(user));
		}
		
		if(failCount > 0) {
			this.addActionMessage(MgrUtil.getUserMessage("info.gml.remove.fail.count",
											new String[] {user.getUserName(),
															String.valueOf(failCount),
															String.valueOf(apIds.size())})
									);
			
			return false;
		}
		
		return true;
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
			String serverName = mailNotification.get(0).getServerName();
			String mailFrom = mailNotification.get(0).getMailFrom();
			
			if (serverName == null 
					|| serverName.equals("")) { 
				addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
						"SMTP server"));
				return;
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
	
		return false;
	}
	
	private boolean sendMail(LocalUser user, List<MailNotification> mailNotification) {
		StringBuffer text = new StringBuffer();
		text.append(this.getHeaderHTML());
		text.append("<br><br>");
		text.append("<table border=\"1\" width=\"40%\"><tr>");
		text.append("<td width=\"200px\">User Name</td><td>").append(user.getUserName()).append("</td>");
		text.append("</tr><tr>");
		text.append("<td width=\"200px\">PSK</td><td>").append(user.getStrPsk()).append("</td>");
		text.append("</tr><tr>");
		text.append("<td width=\"200px\">Start Time</td><td>").append(user.getStartTimeString()).append("</td>");
		text.append("</tr><tr>");
		text.append("<td  width=\"200px\">End Time</td><td>").append(user.getExpiredTimeString()).append("</td>");
		text.append("</tr></table>");
		
		if (mailNotification != null && !mailNotification.isEmpty()) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification.get(0));
			mailUtil.setMailTo(user.getMailAddress());
			mailUtil.setSubject(user.getUserName() + " PSK");
			mailUtil.setMailContentType("text/html");
			mailUtil.setText(text.toString());
			
			try {
				mailUtil.startSend();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public List<PrintField> getTemplateFields(){
		getFromSession();
		
		if(getUserId() == null) {
			return null;
		}
		
		LocalUser user = QueryUtil.findBoById(LocalUser.class, getUserId());
		
		if(user == null) {
			return null;
		}
		
		List<PrintField> printFields = new ArrayList<PrintField>();
		printFields.add(new PrintField("User Name", user.getUserName()));
		printFields.add(new PrintField("PSK", user.getStrPsk()));
		printFields.add(new PrintField("Start Time", user.getStartTimeString()));
		printFields.add(new PrintField("End Time", user.getExpiredTimeString()));
		
		return printFields;
	}
	
	public String getHeaderHTML() {
		StringBuffer headerHTML = new StringBuffer();
		
		headerHTML.append("<table width=\"500px\"><tr><td colspan=\"2\">");
		headerHTML.append("How to connect to the wireless network:");
		headerHTML.append("</td></tr><tr><td height=\"10px\"></td></tr><tr><td width=\"20\" valign=\"top\">");
		headerHTML.append("1.");
		headerHTML.append("</td><td>");
		headerHTML.append("Make sure that your computer is set to receive its network settings dynamically through DHCP (Dynamic Host Control Protocol). This is the default setting for most computers.");
		headerHTML.append("</td></tr><tr><td width=\"20px\" valign=\"top\">");
		headerHTML.append("2.");
		headerHTML.append("</td><td>");
		headerHTML.append("Open the wireless network client on your computer and select the appropriate wireless network name, or SSID (service set identifier). If you do not know which SSID to use, please contact the network manager.");
		headerHTML.append("</td></tr><tr><td width=\"20px\" valign=\"top\">");
		headerHTML.append("3.");
		headerHTML.append("</td><td>");
		headerHTML.append("When prompted to enter a network key, type the PSK (preshared key) listed below.");
		headerHTML.append("</td></tr></table>");
		
		
		return headerHTML.toString();
	}
	
	public String getFooterHTML() {
		return null;
	}
	
	private void saveIntoSession() {
		String userKey = this.getUserContext().getId() + "User";
		MgrUtil.setSessionAttribute(userKey, userId);
//		String templateKey = this.getUserContext().getId() + "Template";
//		MgrUtil.setSessionAttribute(templateKey, templateId);
	}
	
	private void getFromSession() {
		String userKey = this.getUserContext().getId() + "User";
		userId = (Long)MgrUtil.getSessionAttribute(userKey);
//		String templateKey = this.getUserContext().getId() + "Template";
//		templateId = (Long)MgrUtil.getSessionAttribute(templateKey);
	}
	
	private Long userId = null;
	
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
	
	private void preparePrint() {
		this.saveIntoSession();
	}

	/*
	 * this method is a copy from LocalUserAction.checkPasswordLength()
	 */
	private boolean checkPassword() {
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
				asDefined=","+MgrUtil.getUserMessage("action.error.defined.local.user.group");
			}
			if (getDataSource().getLocalUserGroup().getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
				if (!blnStr == getDataSource().getLocalUserGroup().getBlnCharLetters()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter", message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.letter", message) + asDefined +".");
					}
					return false;
				}
				if (!blnDig == getDataSource().getLocalUserGroup().getBlnCharDigits()){
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit", message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.digit", message) + asDefined +".");
					}
					return false;
				}
				if (!blnSpc == getDataSource().getLocalUserGroup().getBlnCharSpecial()){
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char", message) + asDefined +".");
					} else {
						addActionError(MgrUtil.getUserMessage("action.error.must.contain.special.char", message) + asDefined +".");
					}
					return false;
				}
			}
			if (getDataSource().getLocalUserGroup().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_OR){
				if (!getDataSource().getLocalUserGroup().getBlnCharLetters() && blnStr){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter", message) + asDefined +".");
					return false;
				}
				if (!getDataSource().getLocalUserGroup().getBlnCharDigits() && blnDig){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit", message) + asDefined +".");
					return false;
				}
				if (!getDataSource().getLocalUserGroup().getBlnCharSpecial() && blnSpc){
					addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char", message) + asDefined +".");
					return false;
				}
			}

			if (getDataSource().getLocalUserGroup().getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
				if (getDataSource().getLocalUserGroup().getBlnCharLetters()){
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit", message) + asDefined +".");
						return false;
					}
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char", message) + asDefined +".");
						return false;
					}
				} else if (getDataSource().getLocalUserGroup().getBlnCharDigits()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter", message) + asDefined +".");
						return false;
					}
					if (blnSpc){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.special.char", message) + asDefined +".");
						return false;
					}
				} else if (getDataSource().getLocalUserGroup().getBlnCharSpecial()){
					if (blnStr){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.letter", message) + asDefined +".");
						return false;
					}
					if (blnDig){
						addActionError(MgrUtil.getUserMessage("action.error.cannot.contain.digit", message) + asDefined +".");
						return false;
					}
				}
			}
		}
		return true;
	}

}