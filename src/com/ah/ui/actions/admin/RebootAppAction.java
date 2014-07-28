package com.ah.ui.actions.admin;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.ah.be.admin.LocalAddresses;
import com.ah.be.admin.adminOperateImpl.BeRebootInfoDTO;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.util.HAadminTool;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class RebootAppAction extends BaseAction {

	private static final long			serialVersionUID	= 1L;

	private static final Tracer			log					= new Tracer(RebootAppAction.class
																	.getSimpleName());

	private List<BeRebootInfoDTO>		bootInfoList;

	private String						rebootSoft;

	private List<HMUpdateSoftwareInfo>	revertList;

	private String						revertTarget;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("reboot".equals(operation)) {
				// without reboot target
				if (rebootSoft == null) {
					addActionError(MgrUtil.getUserMessage("action.error.find.reboot.target"));
					return SUCCESS;
				}

				// reboot
				try {
					//UpdateSoftwareAction ua = new UpdateSoftwareAction();
					//hm-ha-4-node
					if(!NmsUtil.isHostedHMApplication() && HAadminTool.isHaModel() && !is2node(getPrimaryDbUrl()) && HAadminTool.isValidMaster()){
						HmBeAdminUtil.fourNodeRebootByLabel(rebootSoft);
					}else{
						HmBeAdminUtil.rebootByLabel(rebootSoft);
					}
					addActionMessage(NmsUtil.getOEMCustomer().getNmsName() + " appliance is rebooting...");
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.reboot.application"));
				} catch (Exception e) {
					log.error("reboot", "catch exception", e);

					addActionError(MgrUtil.getUserMessage("action.error.reboot.appliance",
							NmsUtil.getOEMCustomer().getNmsName()) + e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.reboot.application"));
				}

				initValue();
				return SUCCESS;
			} else if ("revert".equals(operation)) {

				if (revertTarget == null) {
					addActionError(MgrUtil.getUserMessage("action.error.find.revert.target"));
					return SUCCESS;
				}

				revertList = (List<HMUpdateSoftwareInfo>) MgrUtil
						.getSessionAttribute(SESSIONKEY_REVERTLIST);

				HMUpdateSoftwareInfo revertInfo = null;
				for (HMUpdateSoftwareInfo info : revertList) {
					if (info.getIpAddress().equals(revertTarget)) {
						if (info.getStatus() == HMUpdateSoftwareInfo.STATUS_ACTIVE) {
							addActionError(MgrUtil.getUserMessage("action.error.revert.an.active.target"));
							return SUCCESS;
						} else {
							revertInfo = info;
							break;
						}
					}
				}

				// revert
				boolean isSucc = HHMoperate.revertOperation(revertInfo);
				if (isSucc) {
					addActionMessage(NmsUtil.getOEMCustomer().getNmsName() + " is reverted successfully.");
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("hm.audit.log.revert.version",NmsUtil.getOEMCustomer().getNmsName()));

					invalidateCurrentSession();
				} else {
					addActionError(MgrUtil.getUserMessage("action.error.revert.version",NmsUtil.getOEMCustomer().getNmsName()));
					generateAuditLog(HmAuditLog.STATUS_FAILURE,MgrUtil.getUserMessage("hm.audit.log.revert.version",NmsUtil.getOEMCustomer().getNmsName()));
					
					initValue();
				}

				return SUCCESS;
			} else {
				initValue();
				
				return SUCCESS;
			}
		} catch (Exception e) {
			initValue();
			return prepareActionError(e);
		}
	}

	public Object[] getPrimaryDbUrl() throws IllegalAccessException, InvocationTargetException{
		String hql = " select primaryDbUrl,secondaryDbUrl from " + HASettings.class.getSimpleName();
		List<?> rsList = QueryUtil.executeQuery(hql, 1);
		Object[] obj = new Object[2];
		if(rsList.isEmpty()){
			return obj;
		}
		obj = (Object[]) rsList.get(0);
		return obj;
	}
	
	public boolean is2node(Object[] param) throws SocketException
	{
		final Object primaryDbUrl = param[0];
		final Object secondaryDbUrl = param[1];
		  //select primaryDbUrl, secondaryDbUrl from ha_settings
		  return null == primaryDbUrl
		      || null == secondaryDbUrl
		      || "".equals(primaryDbUrl)
		      || "".equals(secondaryDbUrl)
		      || null != LocalAddresses.iterate
		    (
		      new LocalAddresses.Iteration()
		      {
		        public  boolean stop  (String address,String network)
		        {
		          return address.equals(primaryDbUrl)
		              || address.equals(secondaryDbUrl);
		        }
		      }
		    );
		}
	
	/**
	 * invalidate all current domain users
	 */
	private void invalidateCurrentSession() {
		HmUser currentUser = getUserContext();
		Set<Long> userIDSet = new HashSet<Long>();
		for (HttpSession session : CurrentUserCache.getInstance().getActiveSessions()) {
			HmUser user=null;
			try {
				user = (HmUser) session.getAttribute(USER_CONTEXT);
			} catch (Exception e){
				log.error(e);
				continue;
			}
			if (user.getOwner().getDomainName().equals(currentUser.getOwner().getDomainName())) {
				userIDSet.add(user.getId());
			}
		}

		if (!userIDSet.isEmpty()) {
			CurrentUserCache.getInstance().invalidateSessions(userIDSet);
		}
	}

	private final String	SESSIONKEY_BOOTLIST		= "session_bootlist";
	private final String	SESSIONKEY_REVERTLIST	= "session_revertlist";

	/**
	 * init gui info
	 */
	private void initValue() {
		// init reboot target list
		bootInfoList = HmBeAdminUtil.getRebootInfo();

		for (BeRebootInfoDTO info : bootInfoList) {
			// select the corresponding radio
			if (info.getIsBootLabel()) {
				rebootSoft = info.getLabel();
			}
			// show some message while version is blank string
			if (info.getVersion().length() == 0) {
				info.setVersion("Error: version is a blank string");
			}
		}

		MgrUtil.setSessionAttribute(SESSIONKEY_BOOTLIST, bootInfoList);

		if (NmsUtil.isHostedHMApplication() && !getIsInHomeDomain()) {
			try {
				revertList = new ArrayList<HMUpdateSoftwareInfo>();
				
				HmolInfo hm4Revert = RemotePortalOperationRequest.requestRevertVhm(getDomain()
						.getDomainName());
				HMUpdateSoftwareInfo standby = new HMUpdateSoftwareInfo();
				standby.setDomainName(getDomain().getDomainName());
				standby.setHmVersion(hm4Revert.getHmolVersion());
				standby.setIpAddress(hm4Revert.getHmolIpAddress());
				standby.setStatus(HMUpdateSoftwareInfo.STATUS_STANDBY);
				if (!(hm4Revert.getHmolIpAddress().isEmpty() && hm4Revert.getHmolVersion().isEmpty())) {
					// there are no standby revert target
					revertList.add(standby);
				}

				HMUpdateSoftwareInfo active = new HMUpdateSoftwareInfo();
				active.setDomainName(getDomain().getDomainName());
				BeVersionInfo version = NmsUtil.getVersionInfo();
				active.setHmVersion(version.getMainVersion() + "r" + version.getSubVersion());
				active.setIpAddress(HmBeOsUtil.getIP_eth0());
				revertList.add(active);

				revertTarget = active.getIpAddress();

				MgrUtil.setSessionAttribute(SESSIONKEY_REVERTLIST, revertList);
			} catch (Exception e) {
				log.error("initValue", "catch exception", e);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.reboot.app.exception")
								+ e.getMessage());
				addActionError(MgrUtil.getUserMessage("action.error.query.revert.target") + e.getMessage());
			}
		}

	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_REBOOTAPP);
	}

	public List<BeRebootInfoDTO> getBootInfoList() {
		return bootInfoList;
	}

	public void setBootInfoList(List<BeRebootInfoDTO> bootInfoList) {
		this.bootInfoList = bootInfoList;
	}

	public String getRebootSoft() {
		return rebootSoft;
	}

	public void setRebootSoft(String rebootSoft) {
		this.rebootSoft = rebootSoft;
	}

	public String getRevertTarget() {
		return revertTarget;
	}

	public void setRevertTarget(String revertTarget) {
		this.revertTarget = revertTarget;
	}

	public List<HMUpdateSoftwareInfo> getRevertList() {
		return revertList;
	}

	public void setRevertList(List<HMUpdateSoftwareInfo> revertList) {
		this.revertList = revertList;
	}

	public boolean isNeedInit() {
		return MgrUtil.getSessionAttribute(SESSIONKEY_BOOTLIST) == null
				&& MgrUtil.getSessionAttribute(SESSIONKEY_REVERTLIST) == null;
	}

	public String getRebootButtonDisabled() {
		if (HAUtil.isSlave() && userContext.isSuperUser()) {
			return "";
		} else {
			return super.getWriteDisabled();
		}
	}

}