/**
 * @filename			BEHAStatusListener.java
 * @version				1.0
 * @author				Administrator
 * @since				3.3
 *
 * Copyright (c) 2006-2009 Aerohive Co., Ltd.
 * All right reserved.
 */
package com.ah.ha.be;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ericdaugherty.soht.server.SocketProxyServlet;
import com.ericdaugherty.sshwebproxy.SshConnectionFactory;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeApp;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BusinessUtil;
import com.ah.be.communication.event.BeCapwapHAStatusEvent;
import com.ah.bo.admin.HASettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAStatus;
import com.ah.ha.HAStatusListener;
import com.ah.ha.HAUtil;
import com.ah.ha.alert.HAAlertor;
import com.ah.ha.dns.VhnSynchronizer;
import com.ah.ha.event.HAStatusChangedEvent;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.HibernateUtil;
import com.ah.util.Tracer;

public class BeAppManager implements HAStatusListener {

	private static final Tracer log = new Tracer(BeAppManager.class
			.getSimpleName());

	/* (non-Javadoc)
	 * @see com.ah.ha.HAStatusListener#statusChanged(com.ah.util.ha.HAStatusChangedEvent)
	 */
	@Override
	public synchronized void statusChanged(HAStatusChangedEvent event) {
		HAStatus oldHAStatus = event.getOldStatus();
		HAStatus newHAStatus = event.getNewStatus();
		int oldStatus = oldHAStatus.getStatus();
		int newStatus = newHAStatus.getStatus();

		switch (oldStatus) {
			case HAStatus.STATUS_UNKNOWN:
				switch (newStatus) {
					case HAStatus.STATUS_STAND_ALONG: // BE startup in 'Standalone' mode.
						changeHAStatusFromUnknownToStandalone();
						break;
					case HAStatus.STATUS_HA_MASTER: // BE startup in HA 'Master' mode.
						changeHAStatusFromUnknownToMaster();
						break;
					case HAStatus.STATUS_HA_SLAVE: // BE startup in HA 'Slave' mode.
						changeHAStatusFromUnknownToSlave();
						break;
					case HAStatus.STATUS_UNKNOWN:
						log.warn("statusChanged", "Both the old and new HA status are 'Unknown'. HA status change ignored.");
						break;
					case HAStatus.STATUS_SHUT_DOWN:
					default:
						log.warn("statusChanged", "Unexpected new HA status: " + newStatus + "; Old status: " + oldStatus);
						break;
				}
				break;
			case HAStatus.STATUS_STAND_ALONG:
				switch (newStatus) {
					case HAStatus.STATUS_STAND_ALONG:
						log.warn("statusChanged", "Both the old and new HA status are 'Standalone'. HA status change ignored.");
						break;
					case HAStatus.STATUS_HA_MASTER: // join
						changeHAStatusFromStandaloneToMaster();
						break;
					case HAStatus.STATUS_HA_SLAVE: // join
						changeHAStatusFromStandaloneToSlave();
						break;
					case HAStatus.STATUS_SHUT_DOWN:
						changeHAStatusFromStandaloneToShutdown();
						break;
					case HAStatus.STATUS_UNKNOWN:
					default:
						log.warn("statusChanged", "Unexpected new HA status: " + newStatus + "; Old status: " + oldStatus);
						break;
				}
				break;
			case HAStatus.STATUS_HA_MASTER:
				switch (newStatus) {
					case HAStatus.STATUS_STAND_ALONG: // breakup
						changeHAStatusFromMasterToStandalone();
						break;
					case HAStatus.STATUS_HA_MASTER:
						log.warn("statusChanged", "Both the old and new HA status are 'Master'. HA status change ignored.");
						break;
					case HAStatus.STATUS_HA_SLAVE: // fail-over/switchover
						changeHAStatusFromMasterToSlave();
						break;
					case HAStatus.STATUS_SHUT_DOWN:
						changeHAStatusFromMasterToShutdown();
						break;
					case HAStatus.STATUS_UNKNOWN:
					default:
						log.warn("statusChanged", "Unexpected new HA status: " + newStatus + "; Old status: " + oldStatus);
						break;
				}
				break;
			case HAStatus.STATUS_HA_SLAVE:
				switch (newStatus) {
					case HAStatus.STATUS_STAND_ALONG: // breakup
						changeHAStatusFromSlaveToStandalone();
						break;
					case HAStatus.STATUS_HA_MASTER: // fail-over/switchover
						changeHAStatusFromSlaveToMaster();
						break;
					case HAStatus.STATUS_HA_SLAVE:
						log.warn("statusChanged", "Both the old and new HA status are 'Slave'. HA status change ignored.");
						break;
					case HAStatus.STATUS_SHUT_DOWN:
						changeHAStatusFromSlaveToShutdown();
						break;
					case HAStatus.STATUS_UNKNOWN:
					default:
						log.warn("statusChanged", "Unexpected new HA status: " + newStatus + "; Old status: " + oldStatus);
						break;
				}
				break;
			default:
				log.warn("statusChanged", "Unexpected old HA status: " + oldStatus + "; New status: " + newStatus);
				break;
		}
	}

	private void changeHAStatusFromUnknownToStandalone() {
		log.info("changeHAStatusFromUnknownToStandalone", "Changing HA status from 'Unknown' to 'Standalone'.");
		startBE(false);
	}

	private void changeHAStatusFromUnknownToMaster() {
		log.info("changeHAStatusFromUnknownToMaster", "Changing HA status from 'Unknown' to 'Master'.");
		startBE(false);
		startHAAlertor();

		if (NmsUtil.isHostedHMApplication()) {
			startDnsVhnSynchronizer();
		} else {
			setMasterUpTime();
		}
	}

	private void changeHAStatusFromUnknownToSlave() {
		log.info("changeHAStatusFromUnknownToSlave", "Changing HA status from 'Unknown' to 'Slave'.");
		startBE(true);
		startHAAlertor();
	}

	private void changeHAStatusFromStandaloneToMaster() {
		log.info("changeHAStatusFromStandaloneToMaster", "Changing HA status from 'Standalone' to 'Master'.");
		sendHAStatusToCAPWAPServer(HAStatus.STATUS_HA_MASTER);
		startHAAlertor();

		if (NmsUtil.isHostedHMApplication()) {
			startDnsVhnSynchronizer();
		} else {
			setMasterUpTime();
		}
	}

	private void changeHAStatusFromStandaloneToSlave() {
		log.info("changeHAStatusFromStandaloneToSlave", "Changing HA status from 'Standalone' to 'Slave'.");
		setHAMode(HAMode.PASSIVE);
		startHAAlertor();
	}

	private void changeHAStatusFromStandaloneToShutdown() {
		log.info("changeHAStatusFromStandaloneToShutdown", "Changing HA status from 'Standalone' to 'Shutdown'.");
		stopBE();
	}

	private void changeHAStatusFromMasterToStandalone() {
		log.info("changeHAStatusFromMasterToStandalone", "Changing HA status from 'Master' to 'Standalone'.");

		// Reset HA settings.
		try {
			QueryUtil.updateBo(HASettings.class, "haStatus = 0, haSecret = ''", null);
		} catch (Exception e) {
			log.error("changeHAStatusFromMasterToStandalone", "Reset HA settings error.", e);
		}

		sendHAStatusToCAPWAPServer(HAStatus.STATUS_STAND_ALONG);
		stopHAAlertor();

		if (NmsUtil.isHostedHMApplication()) {
			stopDnsVhnSynchronizer();
		}
	}

	private void changeHAStatusFromMasterToSlave() {
		log.info("changeHAStatusFromMasterToSlave", "Changing HA status from 'Master' to 'Slave'.");

		if (NmsUtil.isHostedHMApplication()) {
			stopDnsVhnSynchronizer();
		}

		stopHAAlertor();
		setHAMode(HAMode.PASSIVE);
		startHAAlertor();

		// send a notification email to super user
		NmsUtil.sendMailToAdminUser(HmBeResUtil.getString("ha.email.subject"),
				HmBeResUtil.getString("ha.email.content.passive",
						new String[]{HmBeOsUtil.getHostName(),HmBeOsUtil.getHiveManagerIPAddr()}));
	}

	private void changeHAStatusFromMasterToShutdown() {
		log.info("changeHAStatusFromMasterToShutdown", "Changing HA status from 'Master' to 'Shutdown'.");

		if (NmsUtil.isHostedHMApplication()) {
			stopDnsVhnSynchronizer();
		}

		stopHAAlertor();
		stopBE();
	}

	private void changeHAStatusFromSlaveToStandalone() {
	//	log.info("changeHAStatusFromSlaveToStandalone", "Changing HA status from 'Slave' to 'Standalone'.");
		stopHAAlertor();
	//	setHAMode(HAMode.ACTIVE);

		try {
			StringBuilder cmd = new StringBuilder(BeAdminCentOSTools.ahShellRoot + "/haRebootPassive.sh");
			String[] localSettings = new String[] { "localhost", "5432", "hm", "hivemanager", "aerohive", "t" };

			for (String item : localSettings) {
				cmd.append(" ").append(item);
			}

			cmd.append(" >>/HiveManager/ha/logs/ha_disable").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append(".log 2>&1");
			log.info("changeHAStatusFromSlaveToStandalone", "Shutting down HiveManager...");
			BusinessUtil.execCommand(cmd.toString());
		} catch (Exception e) {
			log.error("changeHAStatusFromSlaveToStandalone", "Shutdown HiveManager Error.", e);
		}
	}

	private void changeHAStatusFromSlaveToMaster() {
		log.info("changeHAStatusFromSlaveToMaster", "Changing HA status from 'Slave' to 'Master'.");
		stopHAAlertor();
		setHAMode(HAMode.ACTIVE);
		startHAAlertor();

		if (NmsUtil.isHostedHMApplication()) {
			startDnsVhnSynchronizer();
		} else {
			setMasterUpTime();
		}

		// send a notification email to super user
		NmsUtil.sendMailToAdminUser(HmBeResUtil.getString("ha.email.subject"),
				HmBeResUtil.getString("ha.email.content.active",
						new String[]{HmBeOsUtil.getHostName(),HmBeOsUtil.getHiveManagerIPAddr()}));
	}

	private void changeHAStatusFromSlaveToShutdown() {
		log.info("changeHAStatusFromSlaveToShutdown", "Changing HA status from 'Slave' to 'Shutdown'.");
		stopHAAlertor();
		stopBE();
	}

	private void sendHAStatusToCAPWAPServer(int haStatus) {
		BeCapwapHAStatusEvent haStatusChangedEvent = new BeCapwapHAStatusEvent();
		haStatusChangedEvent.setHaStatus(haStatus);

		try {
			haStatusChangedEvent.buildPacket();
			AhAppContainer.getBeCommunicationModule().sendRequest(haStatusChangedEvent);
			log.info("sendHAStatusToCAPWAPServer", "Sent HA status (" + haStatus+ ") to CAPWAP server.");
		} catch (Exception e) {
			log.error("sendHAStatusToCAPWAPServer", "Error in sending HA status [" + haStatus + "] to CAPWAP server.", e);
		}
	}

	private void setMasterUpTime() {
		try {
			List<?> haSettingsList = QueryUtil.executeQuery("select id, primaryMGTIP from " + HASettings.class.getSimpleName(), null, new FilterParams("haStatus", HASettings.HASTATUS_ENABLE), 1);

			if (haSettingsList.isEmpty()) {
				return;
			}

			Object[] haSettings = (Object[]) haSettingsList.get(0);
			Long id = (Long) haSettings[0];
			String primaryMgtIp = (String) haSettings[1];
			String sysIp = HmBeOsUtil.getIP_eth0();

			if (sysIp.equals(primaryMgtIp)) {
				QueryUtil.updateBos(HASettings.class, "primaryUpTime = :s1", "id = :s2", new Object[] { System.currentTimeMillis(), id });
			} else {
				QueryUtil.updateBos(HASettings.class, "secondaryUpTime = :s1", "id = :s2", new Object[] { System.currentTimeMillis(), id });
			}
		} catch (Exception e) {
			log.error("setMasterUpTime", "Set master up time error.", e);
		}
	}

	private void startDnsVhnSynchronizer() {
		if (NmsUtil.isHostedHMApplication()) {
			VhnSynchronizer vhnSynchronizer = VhnSynchronizer.getInstance();
			vhnSynchronizer.start();
		}
	}

	private void stopDnsVhnSynchronizer() {
		if (NmsUtil.isHostedHMApplication()) {
			VhnSynchronizer vhnSynchronizer = VhnSynchronizer.getInstance();
			vhnSynchronizer.stop();
		}
	}

	/**
	 * Start HA alertor.
	 */
	private void startHAAlertor() {
		HAAlertor haAlertor = HAUtil.getHaAlertor();
		haAlertor.start();
	}

	/**
	 * Stop HA alertor.
	 */
	private void stopHAAlertor() {
		HAAlertor haAlertor = HAUtil.getHaAlertor();
		haAlertor.stop();
	}

	private void setHAMode(HAMode mode) {
		switch (mode) {
			case ACTIVE:
				setActiveMode();
				break;
			case PASSIVE:
				setPassiveMode();
				break;
			default:
				log.warn("setHAMode", "Unknown HA mode: " + mode);
				break;
		}
	}

	/**
	 * Set HA in passive mode.
	 *
	 * 1. Close all SSH proxy connections.
	 * 2. Invalidate all user sessions.
	 * 3. Stop BE.
	 * 4. Restart BE in HA passive mode.
	 */
	private void setPassiveMode() {
		/*
		 * Step 1: Close all SSH proxy connections.
		 */
		SshConnectionFactory.getInstance().closeAllSshConnections();
		SocketProxyServlet.getConnectionManager().removeAllConnections();

		/*
		 * Step 2: Invalidate all user sessions.
		 */
		CurrentUserCache.getInstance().invalidateAllSessions();

		/*
		 * Step 3: Stop BE.
		 */
		stopBE();

		/*
		 * Step 4: Restart BE in HA passive mode.
		 */
		log.info("setPassiveMode", "Restarting BE in HA passive mode under which all DB connections will be in read-only mode and Hibernate caching will be disabled.");
		startBE(true);
	}

	/**
	 * Set HA in active mode.
	 *
	 * 1. Invalidate all user sessions.
	 * 2. Stop BE.
	 * 3. Restart BE.
	 */
	private void setActiveMode() {
		/*
		 * Step 1: Invalidate all user sessions.
		 */
		CurrentUserCache.getInstance().invalidateAllSessions();

		/*
		 * Step 2: Stop BE.
		 */
		stopBE();

		/*
		 * Step 3: Restart BE.
		 */
		log.info("setActiveMode", "Restarting BE where Hibernate caching will be enabled.");
		startBE(false);
	}

	/**
	 * Start BE.
	 *
	 * 1. Initialize Hibernate.
	 * 2. Start BE.
	 *
	 * @param startingInHAPassiveMode if <>true</> start BE in HA passive mode.
	 */
	private void startBE(boolean startingInHAPassiveMode) {
		/*
		 * Step 1: Initialize Hibernate. Hibernate caching should be disabled when HM/HMOL is in HA passive mode.
		 */
		log.info("startBE", "Initializing Hibernate...");
		HibernateUtil.init(!startingInHAPassiveMode);
		log.info("startBE", "Hibernate initialized.");

		/*
		 * Step 2: Start BE.
		 */
		log.info("startBE", "Starting BE...");
		AhAppContainer.HmBe = new HmBeApp();

		if (startingInHAPassiveMode) {
			AhAppContainer.HmBe.createInstancesForHAPassiveMode();
		} else {
			AhAppContainer.HmBe.createInstances();
		}

		log.info("startBE", "BE started.");
	}

	private void stopBE() {
		log.info("stopBE", "Stopping BE...");
		AhAppContainer.HmBe.stopApplication();

		// Wait a few seconds for shutdown for BE before stopping debug server.
		try {
			Thread.sleep(15000);
		} catch(InterruptedException ie) {
			log.error("stopBE", "Interrupted while waiting for shutdown for BE.", ie);
		}

		AhAppContainer.HmBe.stopDebug();
		log.info("stopBE", "Closing Hibernate...");
		HibernateUtil.close();
		log.info("stopBE", "Hibernate closed.");

		// Wait extra seconds to make sure BE can be completely stopped.
//		try {
//			Thread.sleep(10000);
//		} catch(InterruptedException ie) {
//			log.error("stopBE", "Interrupted while waiting for shutdown for BE.", ie);
//		}
	}

}