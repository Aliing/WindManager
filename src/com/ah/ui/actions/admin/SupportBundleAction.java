package com.ah.ui.actions.admin;

import java.io.File;
import java.io.InputStream;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import com.ah.be.app.HmBeAdminUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SupportBundleAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(SupportBundleAction.class
															.getSimpleName());

	// should with suffix
	private String				supportBundleFileName;

	private String				inputPath			= "WEB-INF" + File.separator + "downloads"
															+ File.separator
															+ supportBundleFileName;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("createData".equals(operation)) {
				// call be
				jsonObject = new JSONObject();
				try {
					supportBundleFileName = HmBeAdminUtil.getlogExecCmd();
					MgrUtil.setSessionAttribute("techSupportFile", supportBundleFileName);

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.technical.support.data"));
					
					jsonObject.put("success", true);
					return "json";
				} catch (Exception e) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.technical.support.data"));
					log.error("execute", "execute createData operation catch exception", e);
					
					jsonObject.put("success", false);
					jsonObject.put("message", MgrUtil.getUserMessage("unable.create.technical.support.package.message") + e.getMessage());
					return "json";
				}
			} else if ("download".equals(operation)) {
				supportBundleFileName = (String) MgrUtil.getSessionAttribute("techSupportFile");

				// check file exist
				inputPath = HmContextListener.context.getRealPath("/WEB-INF/" + "downloads")
						+ File.separator + supportBundleFileName;

				File file = new File(inputPath);
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.find.support.data.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return SUCCESS;
				}

				// generateAuditLog(HmAuditLog.STATUS_SUCCESS,
				// "Create technical support data");
				return "download";
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SUPPORT_BUNDLE);
	}

	// struts download support
	public String getLocalFileName() {
		return supportBundleFileName;
	}

	public InputStream getInputStream() throws Exception {
		inputPath = "/WEB-INF/" + "downloads" + File.separator + supportBundleFileName;

		return ServletActionContext.getServletContext().getResourceAsStream(inputPath);
	}

}