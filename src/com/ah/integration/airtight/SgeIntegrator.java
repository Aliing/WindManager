package com.ah.integration.airtight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.airtight.spectraguard.api.dataobjects.Version;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.AuthenticationInfo;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.dataobjects.session.UserCredentialsAuth;
import com.airtight.spectraguard.api.exceptions.APIException;
import com.airtight.spectraguard.common.CommonConstants;

import com.ah.be.app.AhAppContainer;
import com.ah.be.license.HM_License;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.AirtightSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.integration.IntegrationException;
import com.ah.integration.airtight.stage.AssociationSyncStage;
import com.ah.integration.airtight.stage.AuthorizedApSyncStage;
import com.ah.integration.airtight.stage.ClientRssiReportStage;
import com.ah.integration.airtight.stage.ClientSyncStage;
import com.ah.integration.airtight.stage.DetectedDeviceRssiReportStage;
import com.ah.integration.airtight.stage.SignalStrengthMonitorSyncStage;
import com.ah.integration.airtight.stage.UncategorizedApSyncStage;
import com.ah.integration.airtight.util.SgeUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SgeIntegrator {

	private static final Tracer log = new Tracer(SgeIntegrator.class, HmLogConst.M_SGE);

	/* Maximum Session Timeout (in seconds) */
	private static final int MAX_SGE_SESSION_TIMEOUT = 7200;

	/* The time to delay the first task execution of synchronization from HM to SGE (unit: minute) */
	private static final int SYNC_TASK_INITIAL_DELAY = 1;

	/* The delay after which a scheduled task will be executed */
	private static final long SCHEDULED_TASK_EXECUTION_DELAY = 5L;

	/* The HM domains to which the APs and clients belonging will be reported to SGE server */
	private final Collection<HmDomain> hmDomains;

	/* SGE Configuration */
	private AirtightSettings config;

	/* Client Identifier Prefix */
	private String clientIdPrefix;

	/* SGE Session */
	private APISession session;

	/* Reporting Entity */
	private ReportingEntity reportingEntity;

	/* Scheduled synchronization task future */
	private ScheduledFuture<?> scheduledTaskFuture;

	/* One-time synchronization task future */
	private Future<?> taskFuture;

	/* Synchronization Scheduler */
	private ScheduledExecutorService scheduler;

	public SgeIntegrator() {
		hmDomains = new ArrayList<HmDomain>(1);
		HmDomain homeDomain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);
		hmDomains.add(homeDomain);

		String clientIdSuffix = null;
		String hmSystemId = HM_License.getInstance().get_system_id();

		if (hmSystemId != null && !hmSystemId.isEmpty()) {
		 	clientIdSuffix = hmSystemId.replace("-", "");
		} else {
			try {
				clientIdSuffix = AhAppContainer.getBeOsLayerModule().getMacAddress();
			} catch (Exception e) {
				log.error("WiFiScanner", "Getting HM MAC failed.", e);
			}
		}

		clientIdPrefix = clientIdSuffix != null ? "Hive" + clientIdSuffix : AirtightSettings.DEFAULT_CLIENT_IDENTIFIER;

		if (clientIdPrefix.length() > CommonConstants.CLIENT_IDENTIFIER_LENGTH) {
			clientIdPrefix = clientIdPrefix.substring(0, CommonConstants.CLIENT_IDENTIFIER_LENGTH);
		}

		String reportingId = clientIdSuffix != null ? clientIdSuffix : clientIdPrefix;

		if (reportingId.length() > CommonConstants.REPORTING_ENTITY_LENGTH) {
			reportingId = reportingId.substring(0, CommonConstants.REPORTING_ENTITY_LENGTH);
		}

		try {
			reportingEntity = new ReportingEntity(reportingId);
		} catch (Exception e) {
			log.error("WiFiScanner", "Failed to create an instance of ReportingEntity.", e);
		}
	}

	/**
	 * Returns the SGE client identifier prefix that is comprised of "Hive" + (HM System ID or HM MAC Address).
	 *
	 * @return a string of client identifier prefix that is used to identify a SGE client connecting with a SGE server.
	 */
	public String getClientIdentifierPrefix() {
		return clientIdPrefix;
	}

	/**
	 * Start the synchronization service.
	 *
	 * @param config -
	 * @param enforcing true indicates the started synchronization scheduler will not terminated if a SGE session is not opened,
	 * 						otherwise will start the synchronization scheduler when and only when successfully opens a SGE session.
	 * @throws IntegrationException if any error occurs when opening a SGE session.
	 */
	public synchronized void start(AirtightSettings config, boolean enforcing) throws IntegrationException {
		log.info("start", "Starting HM & SGE integration service.");
		this.config = config;

		if (enforcing) {
			// Start the synchronization scheduler.
			startScheduler();

			// Connect to SGE server.
			connect();
		} else {
			// Connect to SGE server.
			connect();

			// Start the synchronization scheduler.
			startScheduler();
		}

		log.info("start", "Successfully started HM & SGE integration service.");
	}

	public boolean isStarted() {
		return scheduler != null && !scheduler.isShutdown();
	}

	public synchronized void stop() {
		log.info("stop", "Shutting down HM & SGE integration service.");

		// Shut down the synchronization scheduler.
		try {
			shutdownScheduler();
		} catch (InterruptedException ie) {
			log.error("stop", "Interrupted while shutting down HM & SGE synchronization scheduler.");
		}

		// Close session.
		disconnect();

		log.info("stop", "Successfully shut down HM & SGE integration service.");
	}

	/**
	 * Connect to the SGE server and open a SGE session.
	 *
	 * @param subscriber to which the connecting messages will be dispatched.
	 * @throws IntegrationException if any errors occurs during connecting.
	 */
	public void connect(SyncProgressSubscriber subscriber) throws IntegrationException {
		String description = "Opening a SGE session.";
		Collection<SyncStage> syncStages = getSyncStageList();
		dispatch(syncStages, description, true, subscriber);

		String sgeServer = config.getServerURL();
		String userName = config.getUserName();
		String password = config.getPassword();
		AuthenticationInfo authInfo = new UserCredentialsAuth(userName, password);

		try {
			session = new APISession(sgeServer, authInfo, clientIdPrefix, MAX_SGE_SESSION_TIMEOUT);
			log.info("connect", description);
			Version version = session.connect();
			log.info("connect", "Opened a SGE session. Server Version: " + version.getVersion() + "; Server IP: " + session.getServerIPAddress() + "; Client Identifier: " + session.getClientIdentifier() + "; Timeout(s): " + session.getTimeout());
			session.registerReportingEntity(reportingEntity);
		} catch (APIException e) {
			log.error("connect", "Failed to open a SGE session.", e);
			description = SgeUtil.getUserMsg(e);
			dispatch(syncStages, description, false, subscriber);
			throw new IntegrationException(description);
		}
	}

	/**
	 * Connect to the SGE server and open a SGE session.
	 *
	 * @throws IntegrationException if any errors occurs during connecting.
	 */
	public void connect() throws IntegrationException {
		connect(null);
	}

	public APISession getSession() {
		return session;
	}

	public ReportingEntity getReportingEntity() {
		return reportingEntity;
	}

	/**
	 * Keep session alive.
	 *
	 * @return true session keep-alive was success, false otherwise.
	 */
	public boolean keepSessionAlive() {
		boolean isSessionKeptAlive = false;

		if (session.isConnected()) {
			try {
				session.keepSessionAlive();
				isSessionKeptAlive = true;
				log.info("keepSessionAlive", "Session keep-alive succeeded.");
			} catch (APIException e) {
				log.error("keepSessionAlive", "Session keep-alive failed.", e);
			}
		}

		return isSessionKeptAlive;
	}

	public void disconnect() {
		if (session != null && session.isConnected()) {
		//	try {
		//		Collection<ReportingEntity> registeredReportingEntities = session.getRegisteredReportingEntities();
	    //
		//		if (registeredReportingEntities != null && !registeredReportingEntities.isEmpty()) {
		//			session.unregisterReportingEntity(registeredReportingEntities);
		//			log.info("disconnect", "Reporting entities were totally unregistered.");
		//		}
		//	} catch (Exception e) {
		//		log.error("disconnect", "Un-registration for reporting entity failed.", e);
		//	}

			try {
				session.disconnect();
				log.info("disconnect", "Successfully closed the SGE session.");
			} catch (Exception e) {
				log.error("disconnect", "Failed to close the SGE session.", e);
			}
		}
	}

	public synchronized void submit(SyncProgressSubscriber subscriber) throws RejectedExecutionException {
		if (scheduler == null) {
			throw new RejectedExecutionException(MgrUtil.getUserMessage("error.airtight.sync.task.submit.service.unstarted"));
		} else if (scheduler.isShutdown()) {
			throw new RejectedExecutionException(MgrUtil.getUserMessage("error.airtight.sync.task.submit.reject.service.disabled"));
		} else {
			// zero or negative value indicates the delay has already elapsed for the task being executed,
			// other values indicate the remaining to perform the next task.
			if (taskFuture != null && !taskFuture.isDone()) {
				// The previous manually submitted task is not done now.
				throw new RejectedExecutionException(MgrUtil.getUserMessage("error.airtight.sync.task.submit.reject.previous.task.undone"));
			} else {
				// New tasks are disallowed to be submitted in the following cases.
				long delay = scheduledTaskFuture.getDelay(TimeUnit.SECONDS);
				log.info("submit", "Task delay: " + delay);

				if (delay < 0 || delay < SCHEDULED_TASK_EXECUTION_DELAY) {
					// A periodical task is being or to be executed within several seconds.
					throw new RejectedExecutionException(MgrUtil.getUserMessage("error.airtight.sync.task.submit.reject.periodical.task.running"));
				}
			}

			try {
				SyncTask syncTask = new SyncTask(hmDomains, subscriber);
				taskFuture = scheduler.submit(syncTask);
			} catch (RejectedExecutionException ree) {
				log.error("submit", "Failed to submit a synchronization task.", ree);
				throw new RejectedExecutionException(MgrUtil.getUserMessage("error.airtight.sync.task.submit.reject.service.disabled"));
			}
		}
	}

	public Collection<SyncStage> getSyncStageList() {
		AuthorizedApSyncStage authApSyncStage = new AuthorizedApSyncStage();
		UncategorizedApSyncStage uncateApSyncStage = new UncategorizedApSyncStage();
		ClientSyncStage clientSyncStage = new ClientSyncStage();
		AssociationSyncStage assoSyncStage = new AssociationSyncStage();
		SignalStrengthMonitorSyncStage rssiMonitorSyncStage = new SignalStrengthMonitorSyncStage();
		ClientRssiReportStage clientRssiReportStage = new ClientRssiReportStage();
		DetectedDeviceRssiReportStage detectedDeviceRssiReportStage = new DetectedDeviceRssiReportStage();
		Collection<SyncStage> syncStages = new ArrayList<SyncStage>(7);
		syncStages.add(authApSyncStage);
		syncStages.add(uncateApSyncStage);
		syncStages.add(clientSyncStage);
		syncStages.add(assoSyncStage);
		syncStages.add(rssiMonitorSyncStage);
		syncStages.add(clientRssiReportStage);
		syncStages.add(detectedDeviceRssiReportStage);

		return syncStages;
	}

	public void dispatch(Collection<SyncStage> syncStages, String description, boolean succDesc, SyncProgressSubscriber subscriber) {
		if (subscriber == null) {
			return;
		}

		SyncProgressEvent event = new SyncProgressEvent(syncStages, description, succDesc);
		dispatch(event, subscriber);
	}

	public void dispatch(SyncProgressEvent event, SyncProgressSubscriber subscriber) {
		if (subscriber == null) {
			return;
		}

		try {
			subscriber.progressUpdated(event);
		} catch (Exception e) {
			log.error("dispatch", "Failed to dispatch the event [" + event + "] to the subscriber [" + subscriber + "].", e);
		}
	}

	/**
	 * Start HM & SGE integration service.
	 */
	public void startService() {
		if (isStarted()) {
			return;
		}

		try {
			List<AirtightSettings> configs = QueryUtil.executeQuery(AirtightSettings.class, null, new FilterParams("owner.domainName = :s1 and enabled = :s2", new Object[] { HmDomain.HOME_DOMAIN, true }), 1);

			if (!configs.isEmpty()) {
				AirtightSettings config = configs.get(0);
				start(config, true);
			}
		} catch (Exception e) {
			log.error("startService", "The HM & SGE integration service was not started because could not open a SGE session. Session re-open task is going to be executed periodically until an available session is opened.", e);
		}
	}

	private void startScheduler() {
		if (scheduler == null || scheduler.isShutdown()) {
			log.info("startScheduler", "Starting HM & SGE synchronization scheduler.");
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduledTaskFuture = scheduler.scheduleWithFixedDelay(new SyncTask(hmDomains), SYNC_TASK_INITIAL_DELAY, config.getSyncInterval(), TimeUnit.MINUTES);
			log.info("startScheduler", "Successfully started HM & SGE synchronization scheduler.");
		}
	}

	private void shutdownScheduler() throws InterruptedException {
		log.info("shutdownScheduler", "Shutting down HM & SGE synchronization scheduler.");

		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Attempt to cancel the execution of a scheduled task.
		if (scheduledTaskFuture != null) {
			scheduledTaskFuture.cancel(false);
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(30L, TimeUnit.SECONDS)) {
			log.info("shutdownScheduler", "The HM & SGE synchronization scheduler was completely shut down.");
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being canceled.
		long extraWaiting = 10L;

		if (!scheduler.awaitTermination(extraWaiting, TimeUnit.SECONDS)) {
			log.warning("shutdownScheduler", "The HM & SGE synchronization scheduler was not terminated even if making extra " + extraWaiting + " seconds' waiting.");
		} else {
			log.info("shutdownScheduler", "The HM & SGE synchronization scheduler was completely shut down.");
		}
	}

	/*-
	private static com.airtight.spectraguard.api.dataobjects.devices.AP createApWithBssid() throws com.airtight.spectraguard.api.exceptions.ValidationException {
		// MAC Address
		String macAddress = SgeUtil.getSgeFormatMac("222222222222");

		// Channel
		int channel = 1;

		// Protocol
		int protocol = SgeUtil.getProtocolByChannel(channel);

		// IP Address
		String ipAddress = "";

		// Activity Status
		boolean activeStatus = false;

		// SSID
		String ssid = null;

		// Network Tag
		String networkTag = "";

		// Group ID
		int groupId = com.airtight.spectraguard.api.dataobjects.devices.Device.AP_UNCATEGORIZED_GROUP_ID;

		// Vendor Name
		String vendorName = "Aerohive";

		// Authentication Type
		int authType = com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface.AUTH_UNKNOWN;

		// Security Settings
		int securitySettings = com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface.SECURITY_UNKNOWN;

		// Pairwise Encryption
		int pairwiseEncryption = com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface.CIPHER_UNKNOWN;

		// Groupwise Encryption
		int groupwiseEncryption = com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface.CIPHER_UNKNOWN;

		com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface apWifiIf = new com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface(macAddress, protocol);
		apWifiIf.setSSID(ssid);
		apWifiIf.setAuthType(authType);
		apWifiIf.setChannel(channel);
		apWifiIf.setActiveStatus(activeStatus);
		apWifiIf.setNetworkTag(networkTag);
		apWifiIf.setPairwiseEncryption(pairwiseEncryption);
		apWifiIf.setSecuritySettings(securitySettings);
		apWifiIf.setGroupwiseEncryption(groupwiseEncryption);

		// Networked Status
		int networkedStatus = com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface.DEVICE_IS_NETWORKED;

		apWifiIf.setNetworkedStatus(networkedStatus);

		Collection<com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface> apWifiIfs = new ArrayList<com.airtight.spectraguard.api.dataobjects.devices.APWiFiInterface>(1);
		apWifiIfs.add(apWifiIf);

		com.airtight.spectraguard.api.dataobjects.devices.AP ap = new com.airtight.spectraguard.api.dataobjects.devices.AP(apWifiIfs);
		ap.setGroupId(groupId);
		ap.setIpAddress(ipAddress);
		ap.setVendorName(vendorName);
		ap.setWiredMACAddress(SgeUtil.getSgeFormatMac("222222222222"));
		log.info("createApWithBssid", ap + " - AP creation with single BSSID succeeded.");
		return ap;
	}

	public static void main(String[] args) {
		String server = "10.155.30.222";
	//	String clientIdPrefix = "HiveF04DA22050AD";
		String clientIdPrefix = "Hive29B20E9D12D0F73D4E9BEE7BF2895C52";
		String userName = "admin";
		String password = "aerohive";
		AuthenticationInfo authInfo = new UserCredentialsAuth(userName, password);
		APISession session = null;

		try {
			session = new APISession(server, authInfo, clientIdPrefix, MAX_SGE_SESSION_TIMEOUT);
			Version version = session.connect();
			System.out.println("Opened a SGE session. Server Version: " + version.getVersion() + "; Server IP: " + session.getServerIPAddress() + "; Client Identifier: " + session.getClientIdentifier() + "; Timeout: " + session.getTimeout() + "(s).");
			ReportingEntity reporter = new ReportingEntity(clientIdPrefix);
			session.registerReportingEntity(reporter);

			for (ReportingEntity reportingEntity : session.getRegisteredReportingEntities()) {
				System.out.println("Reporting Entity ID: " + reportingEntity.getId());
			}

		//	ReportingEntity reporter2 = session.getRegisteredReportingEntities().iterator().next();

			com.airtight.spectraguard.api.datamanagers.DeviceManager devMgr = new com.airtight.spectraguard.api.datamanagers.DeviceManager(session);
//			com.airtight.spectraguard.api.dataobjects.devices.AP ap = createApWithBssid();
//			Collection<com.airtight.spectraguard.api.dataobjects.devices.AP> aps = new ArrayList<com.airtight.spectraguard.api.dataobjects.devices.AP>(1);
//			aps.add(ap);
//			devMgr.addOrUpdateAPs(aps, reporter);
//
			Collection<com.airtight.spectraguard.api.dataobjects.devices.AP> fetchedAps = devMgr.getAPs(reporter);
			int apCount = fetchedAps.size();
			System.out.println("AP Count: " + apCount);
//			Collection<com.airtight.spectraguard.api.dataobjects.devices.Client> fetchedClients = devMgr.getClients(reporter);
//			int clientCount = fetchedClients.size();
//			System.out.println("Client Count: " + clientCount);
		} catch (APIException e) {
			e.printStackTrace();
			String description = SgeUtil.getUserMsg(e);
			String errorMsg = e.getMessage();

			if (errorMsg != null && errorMsg.toLowerCase().contains("error fetching devices")) {
				System.out.println("Failed to get APs if none APs were imported previously.");
			}

			System.out.println("Description: " + description);
		} finally {
			if (session != null && session.isConnected()) {
				try {
					session.unregisterReportingEntity(session.getRegisteredReportingEntities());
					session.disconnect();
				} catch (APIException e) {
					e.printStackTrace();
				}
			}
		}

//		try {
//			Thread.sleep(5000);
//			com.airtight.spectraguard.api.datamanagers.DeviceManager devMgr = new com.airtight.spectraguard.api.datamanagers.DeviceManager(session);
//			Collection<com.airtight.spectraguard.api.dataobjects.devices.AP> importedAps = devMgr.getAPs(reporter);
//			System.out.println(importedAps);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}*/

}