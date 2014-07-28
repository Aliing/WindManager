package com.ah.ui.actions.admin;

import java.util.List;

import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;

public class ClearDBAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(
														ClearDBAction.class
															.getSimpleName());

	// set visibility of cancel button
	// visible:"" invisible:"none"
	private String				hideCancelBtn		= "none";

	// "ClearDB","OK"
	// private String clearDBBtnName = "ClearDB";

	// set visibility of confirm div
	// visible:"" invisible:"none"
	private String				hideConfirm			= "none";

	// set visibility of explain div
	private String				hideExplain			= "";

	@Override
	public String execute() throws Exception
	{
		String fw = globalForward();
		if (fw != null)
		{
			return fw;
		}

		try
		{
			if ("clearDB".equals(operation))
			{
				try
				{
					if (getIsInHomeDomain())
					{
						int exitValue = 0;
						
						// If HM is in HA status, stop slony first
						boolean haEnableFlag = isHAEnable();
						if(haEnableFlag){
							String cmd = HmBeOsUtil.getHAScriptsPath() + "stop_replication.sh";
							exitValue = RestoreDBAction.execCommand(cmd);
							log.info("ClearDBAction", "execute " + cmd);
							if (exitValue != 0) {
								log.error("ClearDBAction", "execute " + cmd + " exit=" + exitValue);
								generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.clear.db"));
								addActionError(HmBeResUtil.getString("clearDB.error.ha"));
								return ERROR;
							}
						}
						//
						if (exitValue == 0){
							boolean isSucc = HmBeAdminUtil.cleanDB();
							if (isSucc)
							{
//								generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Clear DB");
//								addActionMessage(HmBeResUtil
//									.getString("success_clearDB"));								
							}
							else
							{
								generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.clear.db"));
								addActionError(HmBeResUtil.getString("clearDB.error"));
								return ERROR;
							}
						}
					}
					else
					{
						HmBeAdminUtil.execCleanDomainDB(getDomain());
						
						generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.clear.db"));
						addActionMessage(HmBeResUtil.getString("clearDB.success"));
						if (isHMOnline()) {
							DeviceUtils diu = DeviceImpUtils.getInstance();
							diu.syncDeviceInventoriesWithRedirector(getDomain());
						}
						// invalidate current session.
						CurrentUserCache.getInstance().invalidateSession(
								request.getSession().getId());
					}
				}
				catch (BeOperateException e)
				{
					addActionError(HmBeResUtil.getString("clearDB.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.clear.db"));
					log.error("execute", "Clear DB catch exception: " + e);
					return ERROR;
				}

				return SUCCESS;
			}
			else if ("checkHAStatus".equals(operation))
			{
				boolean haEnableFlag = isHAEnable();
				
				jsonObject = new JSONObject();
				if(haEnableFlag){
					jsonObject.put("haEnable", haEnableFlag);
					jsonObject.put("homeDomain", getIsInHomeDomain());
					// For HA in Home domain, if slaveOnline is true, continue erase database operation; else cancel operation.
					// For HA in VHM, if slaveOnline is false, show tips to notice that don't make the slave node online during this time. 
					String cmd = HmBeOsUtil.getHAScriptsPath() + "check_slave_online.sh";
					int exitValue = RestoreDBAction.execCommand(cmd);
					log.info("ClearDBAction", "execute " + cmd);
					if (exitValue == 0) {
						jsonObject.put("slaveOnline", true);
					}
				}
				
				return "json";
			}
			else
			{
				return SUCCESS;
			}
		}
		catch (Exception e)
		{
			return prepareActionError(e);
		}
	}

	private boolean isHAEnable(){
		List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
		if (list.isEmpty()) {
			log.warn("checkHAStatus","Cannot get the HASettings value.");
			return false;
		}

		HASettings haSettings = list.get(0);
		if (haSettings.getHaStatus() == HASettings.HASTATUS_ENABLE) {
			return true;
		}else{
			log.warn("checkHAStatus","HA wasn't enable on this hm.");
			return false;
		}
	}
	
	public void prepare() throws Exception
	{
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLEAR_DB);
	}

	public String getHideCancelBtn()
	{
		return hideCancelBtn;
	}

	public void setHideCancelBtn(String hideCancelBtn)
	{
		this.hideCancelBtn = hideCancelBtn;
	}

	public String getHideConfirm()
	{
		return hideConfirm;
	}

	public void setHideConfirm(String hideConfirm)
	{
		this.hideConfirm = hideConfirm;
	}

	public String getHideExplain()
	{
		return hideExplain;
	}

	public void setHideExplain(String hideExplain)
	{
		this.hideExplain = hideExplain;
	}

}