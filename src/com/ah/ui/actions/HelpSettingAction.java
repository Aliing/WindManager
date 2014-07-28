package com.ah.ui.actions;

import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		HelpSettingAction.java
 *@version		V1.0.0.0
 *@author		Fisher
 *@createtime	2007-12-26 11:11:29
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class HelpSettingAction extends BaseAction {
	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(HelpSettingAction.class
															.getSimpleName());

//	public static final String	DEFAULT_HELP_URL	= "http://www.aerohive.com/330000/docs/help/english/3.5r2";

	private boolean				useDefault			= false;

	private String				helpDir="";

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("updateHelpSetting".equals(operation)) {
				String errorMessage = updateHelpSetting();

				jsonObject = new JSONObject();
				jsonObject.put("success", errorMessage.trim().length() == 0);
				jsonObject.put("message", errorMessage);
				return "json";
			} else if ("init".equals(operation)) {
				initValue();

				jsonObject = new JSONObject();
				jsonObject.put("useDefault", useDefault);
				jsonObject.put("defaultDir", NmsUtil.getOEMCustomer().getHelpLink());
				jsonObject.put("helpDir", helpDir);
				return "json";
			} else if ("checkUpdate".equals(operation)) {
				boolean result = NmsUtil.existSoftUpdate();
				boolean permit = false;
				if (result) {
					// check whether user has update software feature permission
					HmPermission permission = getUserContext().getUserGroup()
							.getFeaturePermissions().get(Navigation.L2_FEATURE_UPDATE_SOFTWARE);
					if (permission != null) {
						permit = permission.hasAccess(HmPermission.OPERATION_WRITE);
					}
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", result);
				jsonObject.put("permit", permit);
				return "json";
			} else if("searchDomainUserName".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put("domainUserName", getUserContext() ==  null ?  null : getUserContext().getDomainUserName());
				return "json";
			} else {
				initValue();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HELPSETTING);
	}

	/**
	 * update help root info
	 * 
	 * @return -
	 */
	private String updateHelpSetting() {
		String tempHelpUrl="";
		try {
			if (useDefault) {
				tempHelpUrl = NmsUtil.getOEMCustomer().getHelpLink();
			} else {
				if (helpDir.endsWith("/")) {
					helpDir = helpDir.substring(0, helpDir.length() - 1);
				}

				tempHelpUrl = helpDir.trim();
			}

			HmUserGroup userGroup = QueryUtil.findBoById(HmUserGroup.class, userContext
					.getUserGroup().getId());
			if (useDefault) {
				userGroup.setHelpURL("");
			} else {
				userGroup.setHelpURL(tempHelpUrl);
			}
			QueryUtil.updateBo(userGroup);
			userContext.getUserGroup().setHelpURL(tempHelpUrl);
			return "";
		} catch (Exception e) {
			log.error("updateHelpSetting", "catch exception", e);
			return e.getMessage().replaceAll(" 'null'", "");
		}
	}

	/**
	 * init help root info
	 */
	private void initValue() {
		if (userContext.getUserGroup().getHelpURL() == null
				|| userContext.getUserGroup().getHelpURL().equals("")) {
			useDefault = true;
		} else {
			if (userContext.getUserGroup().getHelpURL().equals(NmsUtil.getOEMCustomer().getHelpLink())) {
				useDefault = true;
			} else {
				useDefault = false;
				helpDir = userContext.getUserGroup().getHelpURL();
			}
		}
	}

	public String getHelpDir() {
		return helpDir;
	}

	public void setHelpDir(String helpDir) {
		this.helpDir = helpDir;
	}

	public boolean isUseDefault() {
		return useDefault;
	}

	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}
}
