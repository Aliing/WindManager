package com.ah.ui.actions.home;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientParamConfigEvent;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.BaseUtils;

/**
 *
 *@filename		PortalSettingsAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-25 10:50:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class PortalSettingsAction extends BaseAction {

	private static final Tracer	log				= new Tracer(PortalSettingsAction.class
														.getSimpleName());

	private boolean				capwapEnable	= true;

	private short				capwapTimeOut	= 30;

	private short				deadInterval	= 105;

	private String				primaryCapwapIP	= "portal.aerohive.com";

	private String				backupCapwapIP;

	private byte				capwapTransportMode;

	private int 				capwapPort		= 12223;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("update".equals(operation)) {
				// resolve server ip
				if (!checkCapwapServerValid()) {
					return SUCCESS;
				}

				boolean isSuccess = updateSettings();
				if (isSuccess) {
					BaseUtils.refreshPortalUrlForRestApi();
					addActionMessage(HmBeResUtil.getString("portalSettings.update.success"));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.portal.settings"));
				} else {
					addActionMessage(HmBeResUtil.getString("portalSettings.update.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.portal.settings"));
				}
				return SUCCESS;
			} else {
				initValue();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", "catch exception", e);
			return ERROR;
		}
	}

	public EnumItem[] getCapwapTransportModes() {
		EnumItem[] enumItems = new EnumItem[2];
		enumItems[0] = new EnumItem(BeAPConnectEvent.TRANSFERMODE_TCP, "TCP");
		enumItems[1] = new EnumItem(BeAPConnectEvent.TRANSFERMODE_UDP, "UDP");

		return enumItems;
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_PORTAL_SETTINGS);
	}

	private void initValue() {
		CapwapClient setting = QueryUtil.findBoByAttribute(CapwapClient.class, "serverType",
				CapwapClient.SERVERTYPE_PORTAL);
		if (setting != null) {
			capwapEnable = setting.isCapwapEnable();
			capwapPort = setting.getUdpPort();
			capwapTransportMode = setting.getTransportMode();
			capwapTimeOut = setting.getTimeOut();
			deadInterval = setting.getNeighborDeadInterval();
			primaryCapwapIP = setting.getPrimaryCapwapIP();
			backupCapwapIP = setting.getBackupCapwapIP();
		}
	}

	private boolean checkCapwapServerValid() {
		if (primaryCapwapIP == null || primaryCapwapIP.length() == 0) {
			primaryCapwapIP = "0.0.0.0";
		} else {
			try {
				InetAddress.getByName(primaryCapwapIP);
//				primaryCapwapIP = address.getHostAddress();
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("action.error.check.capwap.server") + primaryCapwapIP);
				return false;
			}
		}

		if (backupCapwapIP == null || backupCapwapIP.length() == 0) {
			backupCapwapIP = "0.0.0.0";
		} else {
			try {
				InetAddress.getByName(backupCapwapIP);
//				backupCapwapIP = address.getHostAddress();
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("action.error.check.capwap.server") + backupCapwapIP);
				return false;
			}
		}

		return true;
	}

	private boolean updateSettings() {
		boolean isUpdate = true;

		CapwapClient setting = QueryUtil.findBoByAttribute(CapwapClient.class, "serverType",
				CapwapClient.SERVERTYPE_PORTAL);
		if (setting == null) {
			isUpdate = false;
			setting = new CapwapClient();
		}

		setting.setTimeOut(capwapTimeOut);
		setting.setNeighborDeadInterval(deadInterval);
		setting.setUdpPort(capwapPort);
		setting.setTransportMode(capwapTransportMode);
		setting.setPrimaryCapwapIP(primaryCapwapIP);
		setting.setBackupCapwapIP(backupCapwapIP);
		setting.setCapwapEnable(capwapEnable);

		try {
			// 1. update db
			if (isUpdate) {
				QueryUtil.updateBo(setting);
			} else {
				createBo(setting);
			}

			// 2. send settings
			BeCapwapClientParamConfigEvent capwapEvent = new BeCapwapClientParamConfigEvent();
			List<CapwapClient> list = new ArrayList<CapwapClient>();
			list.add(setting);
			capwapEvent.setCapwapClientList(list);
			capwapEvent.buildPacket();

			int returnCode = HmBeCommunicationUtil.sendRequest(capwapEvent);
			if (returnCode == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
				addActionError(HmBeResUtil.getString("portalSettings.sendEvent.error"));
			}
		} catch (Exception e) {
			log.error("updateSettings", "Update portal settings catch exception!", e);
			return false;
		}

		return true;
	}

	public String getBackupCapwapIP() {
		return backupCapwapIP;
	}

	public void setBackupCapwapIP(String backupCapwapIP) {
		this.backupCapwapIP = backupCapwapIP;
	}

	public boolean isCapwapEnable() {
		return capwapEnable;
	}

	public void setCapwapEnable(boolean capwapEnable) {
		this.capwapEnable = capwapEnable;
	}

	public short getCapwapTimeOut() {
		return capwapTimeOut;
	}

	public void setCapwapTimeOut(short capwapTimeOut) {
		this.capwapTimeOut = capwapTimeOut;
	}

	public short getDeadInterval() {
		return deadInterval;
	}

	public void setDeadInterval(short deadInterval) {
		this.deadInterval = deadInterval;
	}

	public String getPrimaryCapwapIP() {
		return primaryCapwapIP;
	}

	public void setPrimaryCapwapIP(String primaryCapwapIP) {
		this.primaryCapwapIP = primaryCapwapIP;
	}

	public byte getCapwapTransportMode() {
		return capwapTransportMode;
	}

	public void setCapwapTransportMode(byte capwapTransportMode) {
		this.capwapTransportMode = capwapTransportMode;
	}

	public int getCapwapPort() {
		return capwapPort;
	}

	public void setCapwapPort(int capwapPort) {
		this.capwapPort = capwapPort;
	}
}
