package com.ah.ui.actions.admin;

import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class ShutDownAppAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	// set visibility of cancel button
	// visible:"" invisible:"none"
	private String hideCancelBtn = "none";

//	// "ShutDown","OK"
//	private String shutDownBtnName = "Shut down";

	// set visibility of confirm div
	// visible:"" invisible:"none"
	private String hideConfirm = "none";

	// set visibility of explain div
	private String hideExplain = "";

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("shutDown".equals(operation)) {
				try {
					boolean isSucc = shutDownApp();
					if (!isSucc) {
						addActionError(HmBeResUtil.getString("shutdownApp.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE,
								MgrUtil.getUserMessage("hm.audit.log.shut.down.application"));
					} else {
						addActionMessage(HmBeResUtil.getString("shutdownApp.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,
								MgrUtil.getUserMessage("hm.audit.log.shut.down.application"));
					}
				} catch (BeOperateException e) {
					addActionError(HmBeResUtil.getString("shutdownApp.error") + " " +e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE,
							MgrUtil.getUserMessage("hm.audit.log.shut.down.application"));
				}

				return SUCCESS;
//			} else if ("cancel".equals(operation)) {
//				hideCancelBtn = "none";
//				hideConfirm = "none";
//				shutDownBtnName = "Shut down";
//				hideExplain = "";
//				return SUCCESS;
			} else {
//				try {
//					AccessControl.checkUserAccess(getUserContext(),
//							getSelectedL2FeatureKey(), CrudOperation.UPDATE);
//				} catch (HmException ex) {
//					addActionError(MgrUtil.getUserMessage(ex));
//				}

				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private boolean shutDownApp() throws Exception {
		// call function in be
		return HmBeAdminUtil.shdownSystem();
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SHUTDOWN_APPLIANCE);
	}

	public String getHideCancelBtn() {
		return hideCancelBtn;
	}

	public void setHideCancelBtn(String hideCancelBtn) {
		this.hideCancelBtn = hideCancelBtn;
	}

	public String getHideConfirm() {
		return hideConfirm;
	}

	public void setHideConfirm(String hideConfirm) {
		this.hideConfirm = hideConfirm;
	}

//	public String getShutDownBtnName() {
//		return shutDownBtnName;
//	}
//
//	public void setShutDownBtnName(String shutDownBtnName) {
//		this.shutDownBtnName = shutDownBtnName;
//	}

	public String getHideExplain() {
		return hideExplain;
	}

	public void setHideExplain(String hideExplain) {
		this.hideExplain = hideExplain;
	}

	public String getShutButtonDisabled() {
		if (HAUtil.isSlave() && userContext.isSuperUser()) {
			return "";
		} else {
			return super.getWriteDisabled();
		}
	}

}