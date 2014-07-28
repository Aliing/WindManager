package com.ah.ui.actions.admin;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.common.NmsUtil;
import com.ah.util.HmContextListener;
import com.ah.util.Tracer;

public class UpdateSoftwareExternalAction extends UpdateSoftwareAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			UpdateSoftwareExternalAction.class.getSimpleName());
	boolean writeDisabled = false;

	@Override
	public String execute() throws Exception {
		log.info("execute", "operation:" + operation);
		if (!isExternalUpdateAvailable()) {
			return "login";
		}
		userContext = null;
		isExternalUpdate = true;// indicate it's external execute
		return super.execute();
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		versionInfo = NmsUtil.getVersionInfo(HmContextListener.context
				.getRealPath("/WEB-INF/hmconf/hivemanager.ver"));
	}

	@Override
	public String getWriteDisabled() {
		return "";
	}

	public String getDisplayedTitle() {
		return "Update Software";
	}

	public boolean getExternalUpdate() {
		return true;
	}

	// check whether this action is available.
	private boolean isExternalUpdateAvailable() {
		return BeOperateHMCentOSImpl.IsValidUpdateLink();
	}

}
