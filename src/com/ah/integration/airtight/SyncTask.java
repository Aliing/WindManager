package com.ah.integration.airtight;

import java.util.ArrayList;
import java.util.Collection;

import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.devices.Device;
import com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.integration.IntegrationException;
import com.ah.integration.airtight.SyncStage.Stage;
import com.ah.integration.airtight.SyncStage.Status;
import com.ah.integration.airtight.impl.ApSyncMgmtImpl;
import com.ah.integration.airtight.impl.AssociationSyncMgmtImpl;
import com.ah.integration.airtight.impl.ClientSyncMgmtImpl;
import com.ah.integration.airtight.impl.SignalStrengthReportMgmtImpl;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SyncTask implements Runnable {

	private static final Tracer log = new Tracer(SyncTask.class.getSimpleName());

	/* AP synchronization utility */
	private final ApSyncMgmt apSyncMgmt;

	/* Client synchronization utility */
	private final ClientSyncMgmt clientSyncMgmt;

	/* Association synchronization utility */
	private final AssociationSyncMgmt assoSyncMgmt;

	/* Signal strength monitor synchronization utility */
	private final SignalStrengthReportMgmt signalStrengthReportMgmt;

	private final SgeIntegrator sgeIntegrator = AhAppContainer.getBeMiscModule().getAirTightSgeIntegrator();

	private final Collection<HmDomain> hmDomains;

	/* Synchronization progress subscriber */
	private SyncProgressSubscriber subscriber;

	public SyncTask(Collection<HmDomain> hmDomains) {
		apSyncMgmt = new ApSyncMgmtImpl();
		clientSyncMgmt = new ClientSyncMgmtImpl();
		assoSyncMgmt = new AssociationSyncMgmtImpl();
		signalStrengthReportMgmt = new SignalStrengthReportMgmtImpl();
		this.hmDomains = hmDomains;
	}

	public SyncTask(Collection<HmDomain> hmDomains, SyncProgressSubscriber subscriber) {
		this(hmDomains);
		this.subscriber = subscriber;
	}

	public SyncProgressSubscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(SyncProgressSubscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public void run() {
		String systemLog;

		try {
			MgrUtil.setTimerName(getClass().getSimpleName());
			doSync();
			systemLog = MgrUtil.getUserMessage("hm.system.log.synctask.success");
		} catch (Exception e) {
			log.error("run", "Synchronization failed.", e);
			systemLog = MgrUtil.getUserMessage("hm.system.log.syntask.failure") + e.getMessage();
		} catch (Error e) {
			log.error("run", "Synchronization failed.", e);
			systemLog = MgrUtil.getUserMessage("hm.system.log.syntask.failure") + e.getMessage();
		}

		try {
			AhAppContainer.HmBe.getLogModule().addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_AIRTIGHT_INTEGRATION, systemLog);
		} catch (Exception e) {
			log.error("run", "Failed to add a system log.", e);
		}
	}

	/**
	 * This function can not only be used for an automatically scheduled task but also be used for a manual task submitted by user over GUI.
	 *
	 * @throws IntegrationException - if any error occurs during synchronization.
	 */
	protected void doSync() throws IntegrationException {
		// Keep session alive before synchronization.
		boolean isSessionKeptAlive = sgeIntegrator.keepSessionAlive();

		if (!isSessionKeptAlive) {
			// Open a new SGE session.
			sgeIntegrator.connect(subscriber);
		}

		APISession session = sgeIntegrator.getSession();
		ReportingEntity reporter = sgeIntegrator.getReportingEntity();

		/* Synchronize authorized HiveAPs */
		syncAuthorizedHiveAps(session, reporter);

		/* Synchronize uncategorized APs */
		syncUncategorizedAps(session, reporter);

		/* Synchronize clients */
		syncClients(session, reporter);

		// APs that were previously imported by the same HM.
		Collection<AP> fetchedAps = fetchAps(session, reporter);

		// Authorized HiveAPs.
		Collection<AP> authorizedHiveAps = new ArrayList<AP>(fetchedAps.size());

		int apWifiIfCount = 0;

		for (AP fetchedAp : fetchedAps) {
			String wiredMac = fetchedAp.getWiredMACAddress();
			String unformatWiredMac = SgeUtil.getUnformattedMac(wiredMac);
			int groupId = fetchedAp.getGroupId();

			// APs whose wired MAC starting with '001977' as well as in the authorized group are what we expect.
			if (unformatWiredMac != null && NmsUtil.isAhMacOui(unformatWiredMac) && groupId == Device.AP_AUTHORIZED_GROUP_ID) {
				authorizedHiveAps.add(fetchedAp);
			}

			apWifiIfCount += fetchedAp.getAllWiFiInterfaces().size();
		}

		// Clients that were previously imported by the same HM.
		Collection<Client> fetchedSgeClients = fetchClients(session, reporter);

		/* Synchronize associations */
		syncAssociations(session, authorizedHiveAps, fetchedSgeClients, reporter);

		/* Synchronize signal strength monitors */
		syncSignalStrengthMonitors(session, authorizedHiveAps);

		// Signal strength monitors existing in the SGE.
		Collection<WiFiInterface> rssiMonitors = getSignalStrengthMonitors(session);

		if (rssiMonitors != null) {
			/* Report client RSSI values */
			reportClientSignalStrengths(session, authorizedHiveAps, fetchedSgeClients, rssiMonitors, reporter);

			// RSSI transmitters.
			Collection<WiFiInterface> rssiTransmitters = new ArrayList<WiFiInterface>(apWifiIfCount + fetchedSgeClients.size());

			// Add AP transmitters.
			for (AP fetchedAp : fetchedAps) {
				for (WiFiInterface apWifiIf : fetchedAp.getAllWiFiInterfaces()) {
					rssiTransmitters.add(apWifiIf);
				}
			}

			// Add client transmitters.
			for (Client fetchedSgeClient : fetchedSgeClients) {
				WiFiInterface clientWifiIf = fetchedSgeClient.getWiFiInterface();
				rssiTransmitters.add(clientWifiIf);
			}

			/* Report detected device RSSI values */
			reportDetectedDeviceSignalStrengths(session, authorizedHiveAps, rssiTransmitters, rssiMonitors, reporter);
		}

		Collection<SyncStage> syncStages = setFinish();
		String description = MgrUtil.getUserMessage("info.airtight.sync.complete");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private void syncAuthorizedHiveAps(APISession session, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.AUTHORIZED_AP_SYNC;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.authorized.hiveap.sync.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			apSyncMgmt.syncAuthorizedHiveAps(session, hmDomains, reporter);
		} catch (Exception e) {
			log.error("syncAuthorizedHiveAps", "Authorized HiveAPs synchronization failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.authorized.hiveap.sync.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private void syncUncategorizedAps(APISession session, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.UNCATEGORIZED_AP_SYNC;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.uncategorized.ap.sync.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			apSyncMgmt.syncUncategorizedAps(session, hmDomains, reporter);
		} catch (Exception e) {
			log.error("syncUncategorizedAps", "Uncategorized APs synchronization failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.uncategorized.ap.sync.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private void syncClients(APISession session, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.CLIENT_SYNC;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.client.sync.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			clientSyncMgmt.syncClients(session, hmDomains, reporter);
		} catch (Exception e) {
			log.error("syncClients", "Clients synchronization failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.client.sync.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private Collection<AP> fetchAps(APISession session, ReportingEntity reporter) throws IntegrationException {
		try {
			return apSyncMgmt.fetchAps(session, reporter);
		} catch (Exception e) {
			log.error("fetchAps", "Fetching APs from SGE failed.", e);
			String description = getUserMsg(e);
			Collection<SyncStage> syncStages = setFinish(Stage.CLIENT_SYNC);
			sgeIntegrator.dispatch(syncStages, description, false, subscriber);
			throw new IntegrationException(description);
		}
	}

	private Collection<Client> fetchClients(APISession session, ReportingEntity reporter) throws IntegrationException {
		try {
			return clientSyncMgmt.fetchClients(session, reporter);
		} catch (Exception e) {
			log.error("fetchClients", "Fetching clients from SGE failed.", e);
			String description = getUserMsg(e);
			Collection<SyncStage> syncStages = setFinish(Stage.CLIENT_SYNC);
			sgeIntegrator.dispatch(syncStages, description, false, subscriber);
			throw new IntegrationException(description);
		}
	}

	private void syncAssociations(APISession session, Collection<AP> associatedHiveAps, Collection<Client> associatedClients, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.ASSOCIATION_SYNC;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.association.sync.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			assoSyncMgmt.syncInfrastructureAssociations(session, associatedHiveAps, associatedClients, reporter);
		} catch (Exception e) {
			log.error("syncAssociations", "Associations synchronization failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.association.sync.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private void syncSignalStrengthMonitors(APISession session, Collection<AP> authorizedHiveAps) throws IntegrationException {
		Stage currentStage = Stage.SIGNAL_STRENGTH_MONITOR_SYNC;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.signal.strength.monitor.sync.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			signalStrengthReportMgmt.syncSignalStrengthMonitors(session, authorizedHiveAps);
		} catch (Exception e) {
			log.error("syncSignalStrengthMonitors", "Signal strength monitors synchronization failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.signal.strength.monitor.sync.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private Collection<WiFiInterface> getSignalStrengthMonitors(APISession session) throws IntegrationException {
		try {
			return signalStrengthReportMgmt.getSignalStrengthMonitors(session);
		} catch (Exception e) {
			log.error("getSignalStrengthMonitors", "Fetching signal strength monitors from SGE failed.", e);
			String description = getUserMsg(e);
			Collection<SyncStage> syncStages = setFinish(Stage.SIGNAL_STRENGTH_MONITOR_SYNC);
			sgeIntegrator.dispatch(syncStages, description, false, subscriber);
			throw new IntegrationException(description);
		}
	}

	private void reportClientSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<Client> transmitterClients, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.CLIENT_RSSI_REPORT;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.client.rssi.report.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			signalStrengthReportMgmt.reportClientSignalStrengths(session, monitorHiveAps, transmitterClients, rssiMonitors, reporter);
		} catch (Exception e) {
			log.error("reportClientSignalStrengths", "Signal strength report for clients failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.client.rssi.report.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private void reportDetectedDeviceSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<WiFiInterface> rssiTransmitters, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws IntegrationException {
		Stage currentStage = Stage.DETECTED_DEVICE_RSSI_REPORT;
		Collection<SyncStage> syncStages = setRunning(currentStage);
		String description = MgrUtil.getUserMessage("info.airtight.stage.detected.device.rssi.report.begin");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);

		try {
			signalStrengthReportMgmt.reportDetectedDeviceSignalStrengths(session, monitorHiveAps, rssiTransmitters, rssiMonitors, reporter);
		} catch (Exception e) {
			log.error("reportDetectedDeviceSignalStrengths", "Signal strength report for detected devices failed.", e);
			description = getUserMsg(e);

			if (description != null) {
				sgeIntegrator.dispatch(syncStages, description, false, subscriber);
				throw new IntegrationException(description);
			}
		}

		syncStages = setFinish(currentStage);
		description = MgrUtil.getUserMessage("info.airtight.stage.detected.device.rssi.report.finish");
		sgeIntegrator.dispatch(syncStages, description, true, subscriber);
	}

	private Collection<SyncStage> setRunning(Stage runningStage) {
		Collection<SyncStage> syncStages = sgeIntegrator.getSyncStageList();

		for (SyncStage syncStage : syncStages) {
			Status status;

			if (syncStage.getStage().ordinal() < runningStage.ordinal()) {
				status = Status.FINISHED;
			} else if (syncStage.getStage().ordinal() == runningStage.ordinal()) {
				status = Status.RUNNING;
			} else {
				status = Status.PENDING;
			}

			syncStage.setStatus(status);
		}

		return syncStages;
	}

	private Collection<SyncStage> setFinish(Stage finishedStage) {
		Collection<SyncStage> syncStages = sgeIntegrator.getSyncStageList();

		for (SyncStage syncStage : syncStages) {
			Status status;

			if (syncStage.getStage().ordinal() <= finishedStage.ordinal()) {
				status = Status.FINISHED;
			} else {
				status = Status.PENDING;
			}

			syncStage.setStatus(status);
		}

		return syncStages;
	}

	private Collection<SyncStage> setFinish() {
		Collection<SyncStage> syncStages = sgeIntegrator.getSyncStageList();

		for (SyncStage syncStage : syncStages) {
			syncStage.setStatus(Status.FINISHED);
		}

		return syncStages;
	}

	private String getUserMsg(Exception e) {
		String userMsg;

		if (e instanceof APIException) {
			SgeUtil.printErrorDetail((APIException) e);
			userMsg = SgeUtil.getUserMsg((APIException) e);
		} else {
			userMsg = e.getMessage();
		}

		return userMsg;
	}

}