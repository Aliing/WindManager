package com.ah.ui.actions.admin;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.sync.VhmUserSync;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.UserSettingsUtil;

/**
 * modify mark:<br>
 * update user session will invoke "java.lang.IllegalStateException:
 * getAttribute: Session already invalidated "
 *
 */
public class UserPasswordModifyAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer	log					= new Tracer(UserPasswordModifyAction.class
															.getSimpleName());

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("update".equals(operation)) {
				// HmUser user = getUserContext();
				UserSettingsUtil.updatePromptChanges(this.userContext.getEmailAddress(), isPromptChanges());
				userContext.setPromptChanges(isPromptChanges());
				
				List<HmUser> userList = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("userName",
						getUserContext().getUserName()), getUserContext().getDomain().getId());
				if (!userList.isEmpty()) {
/*					addActionError(MgrUtil.getUserMessage("action.error.query.user.account"));
					initValues();
					return INPUT;
				}*/
				
					HmUser user = userList.get(0);
	
					if (isEnableChangePassword()) {
						String pw = MgrUtil.digest(passwordOld);
						if (!pw.equals(user.getPassword())) {
							addActionError(HmBeResUtil.getString("userModify.update.oldpwdInCorrect"));
							initValues();
							return INPUT;
						} else {
							user.setPassword(MgrUtil.digest(passwordNew));
							
							// update shell admin password if update admin user
							if (user.getUserName().trim().equals(HmUser.ADMIN_USER) && getUserContext().getDomain().isHomeDomain()) {
								updateShellAdminPwd();
							}
						}
					}
	//				user.setPromptChanges(isPromptChanges());
					// changed in Geneva, for user setting columns separated from hm_user
	
					QueryUtil.updateBo(user);
					VhmUserSync.syncForModifyVhmUser(UserInfo.getUserInfo(user));
					
					userContext.setPasswordInClearText(passwordNew);
				}
				super.setFormChanged(false);// avoid prompt again
				
				initValues();
				super.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.user.account.preference", getUserContext().getUserName()));
				addActionMessage(HmBeResUtil.getString("userModify.update.success"));
				return INPUT;
			} else if ("changePassword4Plan".equals(operation)) {
				setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
				resetPermission();
				return operation;
			} else if ("changPassword".equals(operation)) {
				jsonObject = new JSONObject();
				
				List<HmUser> userList = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("userName",
						getUserContext().getUserName()), getUserContext().getDomain().getId());
				if (userList.isEmpty()) {
					jsonObject.put("success", false);
					jsonObject.put("message", MgrUtil.getUserMessage("user.password.modify.user.not.exist.database.message"));
					return "json";
				}
				
				HmUser user = userList.get(0);

				if (!MgrUtil.digest(passwordOld).equals(user.getPassword())) {
					jsonObject.put("success", false);
					jsonObject.put("message", HmBeResUtil.getString("userModify.update.oldpwdInCorrect"));
					return "json";
				}
				
				user.setPassword(MgrUtil.digest(passwordNew));
				
				QueryUtil.updateBo(user);
				VhmUserSync.syncForModifyVhmUser(UserInfo.getUserInfo(user));
				
				userContext.setPasswordInClearText(passwordNew);
				
				super.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.user.password.modify",user.getUserName()));
								
				jsonObject.put("success", true);
				jsonObject.put("message", HmBeResUtil.getString("password.update.success"));
				return "json";
			} else {
				initValues();
				
				return INPUT;
			}
		} catch (Exception e) {
			initValues();
			super.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.user.setting.update",getUserContext().getUserName()));
			return INPUT;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_USER_PASSWORD_MODIFY);
	}
	
	/**
	 * synchronize shell admin password with HM admin password
	 */
	private void updateShellAdminPwd()
	{
		try {
			String password = passwordNew;
			
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
			log.error("updateAdminShellPwd", "catch exception",e);
		}
	}

	private String passwordOld;
	private String passwordNew;
	private String groupName;
	private boolean promptChanges;
	private boolean enableChangePassword;

	public boolean isEnableChangePassword() {
		return enableChangePassword;
	}

	public void setEnableChangePassword(boolean enableChangePassword) {
		this.enableChangePassword = enableChangePassword;
	}

	public boolean isPromptChanges() {
		return promptChanges;
	}

	public void setPromptChanges(boolean promptChanges) {
		this.promptChanges = promptChanges;
	}

	public void setPasswordOld(String passwordOld) {
		this.passwordOld = passwordOld;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	public HmUser getDataSource() {
		return (HmUser) dataSource;
	}

	public int getPasswdLength() {
		return 32;
	}

	private void initValues() throws Exception {
		dataSource = getUserContext();
		if (getDataSource().getUserGroup() != null) {
			HmUserGroup group = findBoById(HmUserGroup.class,
					getDataSource().getUserGroup().getId());
			if (null != group) {
				groupName = group.getGroupName();
			}
		}
		
		promptChanges = getDataSource().isPromptChanges();
	}

	/**
	 * override this function for user which auth from radius
	 * @see com.ah.ui.actions.BaseAction#getWriteDisabled()
	 */
	@Override
	public String getWriteDisabled() {
		if (writePermission) {
			if (getUserContext().getId() > 0) {
				return "";
			}
		}

		return "disabled";
	}
	
	@Override
	public String getWriteDisabled4HHM() {
		if (NmsUtil.isHostedHMApplication()) {
			if (getUserContext().getDomain().isHomeDomain()) {
				return "disabled";
			} else if (null != getUserContext().getCustomerId()) {
				return "disabled";
			}
		}

		return getWriteDisabled();
	}

}