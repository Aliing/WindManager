package com.ah.ui.actions.admin;

import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

/**
 *@filename		RestartSoftwareAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-4 10:31:47
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class RestartSoftwareAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	// set visibility of cancel button
	// visible:"" invisible:"none"
	private String				hideCancelBtn		= "none";

	// "Restart","OK"
	private String				restartBtnName		= "Restart";

	// set visibility of confirm div
	// visible:"" invisible:"none"
	private String				hideConfirm			= "none";

	// set visibility of explain div
	private String				hideExplain			= "";

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("restart".equals(operation)) {
				try {
					generateAuditLog(HmAuditLog.STATUS_EXECUTE, MgrUtil.getUserMessage("hm.audit.log.restart.software"));
					boolean isSucc = restartSoftware();
					if (isSucc) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.restart.software"));
						addActionMessage(HmBeResUtil.getString("restartSoft.restart.success"));
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.restart.software"));
						addActionError(HmBeResUtil.getString("restartSoft.restart.error"));
					}
				} catch (BeOperateException e) {
					addActionError(HmBeResUtil.getString("restartSoft.restart.error") + " "
							+ e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.restart.software"));
				}

				return SUCCESS;
			} else if ("cancel".equals(operation)) {
				hideCancelBtn = "none";
				hideConfirm = "none";
				restartBtnName = "Restart";
				hideExplain = "";
				return SUCCESS;
			} else {
				// try
				// {
				// AccessControl.checkUserAccess(getUserContext(),
				// getSelectedL2FeatureKey(), CrudOperation.UPDATE);
				// }
				// catch (HmException ex)
				// {
				// //
				// // MgrUtil.setSessionAttribute("errorMessage", MgrUtil
				// // .getUserMessage(ex));
				// addActionError(MgrUtil.getUserMessage(ex));
				// }

				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private boolean restartSoftware() throws Exception {
		// call function in be
		return HmBeAdminUtil.restartSoft();
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RESTART_SOFTWARE);
	}

	public String getHideConfirm() {
		return hideConfirm;
	}

	public void setHideConfirm(String hideConfirm) {
		this.hideConfirm = hideConfirm;
	}

	public String getRestartBtnName() {
		return restartBtnName;
	}

	public void setRestartBtnName(String restartBtnName) {
		this.restartBtnName = restartBtnName;
	}

	public String getHideCancelBtn() {
		return hideCancelBtn;
	}

	public void setHideCancelBtn(String hideCancelBtn) {
		this.hideCancelBtn = hideCancelBtn;
	}

	public String getHideExplain() {
		return hideExplain;
	}

	public void setHideExplain(String hideExplain) {
		this.hideExplain = hideExplain;
	}

	public String getRestartButtonDisabled() {
		if (HAUtil.isSlave() && userContext.isSuperUser()) {
			return "";
		} else {
			return super.getWriteDisabled();
		}
	}

}