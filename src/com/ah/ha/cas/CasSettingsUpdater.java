/**
 * @filename			WebHAStatusListener.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R3(Beijing)
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha.cas;

import java.util.List;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.bo.admin.HASettings;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAStatus;
import com.ah.ha.HAStatusListener;
import com.ah.ha.event.HAStatusChangedEvent;
import com.ah.util.CasTool;
import com.ah.util.Tracer;

/**
 * A Web HA listener to respond to the changing of HA status.
 */
public class CasSettingsUpdater implements HAStatusListener {

	private static final Tracer log = new Tracer(CasSettingsUpdater.class
			.getSimpleName());

	public CasSettingsUpdater() {
	}

	/* (non-Javadoc)
	 * @see com.ah.ha.HAStatusListener#statusChanged(com.ah.util.ha.HAStatusChangedEvent)
	 */
	@Override
	public synchronized void statusChanged(HAStatusChangedEvent event) {
		HAStatus oldHAStatus = event.getOldStatus();
		HAStatus newHAStatus = event.getNewStatus();
		int oldStatus = oldHAStatus.getStatus();
		int newStatus = newHAStatus.getStatus();

		/*
		 * Do not need to notify if status changed from "STAND_ALONE" to "MASTER" or vice versa.
		 * That is because both HM/HMOL back-end and front-end don't need to do anything under this condition.
		if (oldStatus == HAStatus.STATUS_STAND_ALONG && newStatus == HAStatus.STATUS_HA_MASTER) {
			log.info("statusChanged", "HA status was changed from 'Standalone' to 'Master'. Nothing needs to do for this action.");
			return;
		}

		if (oldStatus == HAStatus.STATUS_HA_MASTER && newStatus == HAStatus.STATUS_STAND_ALONG) {
			log.info("statusChanged", "HA status was changed from 'Master' to 'Standalone'. Nothing needs to do for this action.");
			return;
		}*/

		/*-
		switch (newHAStatus.getStatus()) {
			case HAStatus.STATUS_STAND_ALONG:
				break;
			case HAStatus.STATUS_HA_MASTER:
				log.info("statusChanged", "HA status has been changed into Master (Standalone)");
				// HM should update its CAS settings for "Teacher View".
				if (!NmsUtil.isHostedHMApplication()) {
					updateCASSettings();
				}
				break;
			case HAStatus.STATUS_SHUT_DOWN:
			case HAStatus.STATUS_HA_SLAVE:
			case HAStatus.STATUS_UNKNOWN:
			default:
				// fall through
				break;
		}*/

		if (oldStatus == HAStatus.STATUS_HA_SLAVE && newStatus == HAStatus.STATUS_HA_MASTER) {
			// HM should update its CAS settings for "Teacher View".
			if (!NmsUtil.isHostedHMApplication()) {
				updateCASSettings();
			}
		}
	}

	private void updateCASSettings() {
		String localAddress = getLocalAddress();
		
		if (localAddress == null || localAddress.trim().isEmpty() || "0.0.0.0".equals(localAddress)) {
			return;
		}
		
		String casServer = CasTool.getCASServerIP();
		
		if (casServer == null) {
			return;
		}
		
		if (!casServer.equals(localAddress)) {
			CasTool.setCASServerIP(localAddress);
			CasTool.setCASClientIP(localAddress);

			log.info("updateCASSettings", "Restarting HM software for new CAS settings to take effect.");

			try {
				HmBeAdminUtil.restartSoft();
			} catch (Exception e) {
				log.error("updateCASSettings", "HM software restart failed.", e);
			}
		}
	}
	
	public String getLocalAddress() {
		List<?> haSettingsList = QueryUtil.executeQuery("select useExternalIPHostname, primaryMGTIP, primaryExternalIPHostname, secondaryMGTIP, secondaryExternalIPHostname from " + HASettings.class.getSimpleName(), null, null, (int) 1);
		
		if (haSettingsList.isEmpty()) {
			log.error("getLocalAddress", "Could not get HA settings from DB.");
			return null;
		}

		// Get local MGT IP address.
		NetConfigImplInterface networkService = AhAppContainer.getBeOsLayerModule().getNetworkService();
		String localIP = networkService.getIP_eth0();
		log.info("getLocalAddress", "Local MGT IP address: " + localIP);

		if (localIP.equals("")) {
			log.error("getLocalAddress", "Could not get local MGT IP address.");
			return null;
		}

		String localAddress = null;
		Object[] haSettings = (Object[]) haSettingsList.get(0);
		boolean useExternalIPHostname = (Boolean) haSettings[0];
		String primaryMGTIP = (String) haSettings[1];
		String primaryExternalIPHostname = (String) haSettings[2];
		String secondaryMGTIP = (String) haSettings[3];
		String secondaryExternalIPHostname = (String) haSettings[4];
		
		if (localIP.equals(primaryMGTIP)) {
			localAddress = useExternalIPHostname ? primaryExternalIPHostname : primaryMGTIP;
		} else if (localIP.equals(secondaryMGTIP)) {
			localAddress = useExternalIPHostname ? secondaryExternalIPHostname : secondaryMGTIP;
		}
		
		return localAddress;
	}

}