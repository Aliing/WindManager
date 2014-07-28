/**
 *@filename		BePerformModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 02:00:16 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.AhConvertBOToSQL;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeInterferenceMapResultEvent;
import com.ah.be.communication.event.BeNaasLicenseEvent;
import com.ah.be.communication.event.BeNaasLicenseResultEvent;
import com.ah.be.communication.event.BePortAvailabilityResultEvent;
import com.ah.be.communication.event.BePresenceResultEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.communication.event.BeVPNStatusResultEvent;
import com.ah.be.communication.mo.SpectralAnalysisData;
import com.ah.be.communication.mo.SpectralAnalysisDataSample;
import com.ah.be.communication.mo.SpectralAnalysisInterference;
import com.ah.be.event.AhTimeoutEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.appreport.BeApplicationFlowGatherProcessor;
import com.ah.be.performance.appreport.BeCopServerSendDataProcessor;
import com.ah.be.performance.appreport.BeReportCacheCleanProcessor;
import com.ah.be.performance.appreport.BeReportFileCacheCleanProcessor;
import com.ah.be.performance.appreport.BeReportFileParseProcessor;
import com.ah.be.performance.dataretention.DataRetentionProcessor;
import com.ah.be.performance.dataretention.NetworkDeviceHistoryProcessor;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.be.performance.db.TablePartitionProcessor;
import com.ah.be.performance.messagehandle.MessageHandleThreadPool;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.BonjourGatewayMonitoring;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLLDPInformation;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsAvailabilityLow;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsLatencyLow;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsThroughputLow;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.report.common.ReportEndStart_V2;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;

/**
 * @filename BePerformModuleImpl.java
 * @version V1.0.0.0
 * @author juyizhou
 * @createtime 2007-12-6 02:50:56
 * @Copyright (c) 2006-2008 Aerohive Co., Ltd. All right reserved.
 */
/**
 * modify history*
 */
public class BePerformModuleImpl extends BaseModule implements BePerformModule {

	private BePerformStatisticsProcessor			statisticsProcessor;

	private BeClientSessionProcessor				clientSessionProcessor;

	private BeStatisticsProcessor4Sync				statisticsProcessor4Sync;

	private AhPerformanceScheduleModule				bePerformanceSchedule;

	private BePerformSummaryPageModule				bePerformSummaryPageModule;

	private BeNetworkReportScheduleModule			beNetworkReportScheduleModule;

	private BeClearHighIntervalAppAp				beClearHighIntervalAppAp;
	
	private BeClearHighIntervalReportAp				beClearHighIntervalReportAp;

	private BePerformDataCollector					performDataCollector;

	private BePerformInterferenceMapProcessor		performInterferenceMapProcessor;

	private BeMaxClientsCountProcessor				maxClientsProcessor;

	private BeInterfaceClientStatsProcessor			interfaceClientStatsProcessor;

	private BeStudentStatsProcessor					studentStatsProcessor;

	private BeSLAStatsProcessor						slaStatsProcessor;

	private BeSpectralAnalysisProcessor				spectralAnaProcessor;

	private BeDataCollectionProcessor				dataCollectionProcessor;

	private BeAppReportCollectionProcessor          appReportCollectionProcessor;
	
	private BeNetdumpCollectionProcessor			netdumpCollectionProcessor;
	
	private BeInterfaceReportProcessor				interfaceReportProcessor;

	private BeDeviceRealTimeProcessor               deviceRealTimeProcessor;

	private ScheduledExecutorService				timer;

	private ScheduledExecutorService				timer2;

//	private BeProcedureProcessor					procedureProcessor;

	private BeBonjourGatewayProcessor               bonjourGatewayProcessor;

	private BeOTPEventProcessor                     otpEventProcessor;

	private BePresenceProcessor						presenceProcessor;

	private BeDAInforProcessor						dAInforProcessor;

	BulkOperationProcessor							bulkOperationProcessor;

	TablePartitionProcessor							tablePartitionProcessor;

	private BeReportFileParseProcessor              reportFileParseProcessor;

	private BeCopServerSendDataProcessor            copServerSendDataProcessor;

	private BeApplicationFlowGatherProcessor        applicationFlowGatherProcessor;

	private BeReportCacheCleanProcessor             reportCacheCleanProcessor;
	
	private BeReportFileCacheCleanProcessor         reportFileCacheCleanProcessor;

	private BeServerCpuMemoryUsageProcessor         serverCpuMemoryUsageProcessor;

	private MessageHandleThreadPool					messageHandlePool;

	private	TimerProcessor							timerProcessor = null;

	ReportEndStart_V2 reportStartV2;

	private DataRetentionProcessor                  dataRetentionProcessor;

	private NetworkDeviceHistoryProcessor   networkDeviceHistoryProcessor;

	private BeCleanDuplicateApTopProcessor         cleanDuplicateApTopProcessor;

	private BeDeviceInventorySyncScheduleModule    deviceInventorySyncProcessor = null;
	
	private BeLLDPCDPEventProcessor                lldpcdpEventProcessor = null;

	private final int								TIMER_INTERVAL = 5;

	/**
	 * keep track of get statistics request data, if rsp's result is no fsm, will send req again<br>
	 * key: serialNum<br>
	 * value: get statistics request obj
	 */
	private final Map<Integer, BeGetStatisticEvent>	reqSerialNumMap		= new Hashtable<Integer, BeGetStatisticEvent>();

	/**
	 * for snmp & statistics event
	 */
	private final BlockingQueue<BeBaseEvent>		eventQueue;

	private int lostEventCount = 0;

	private static final int						eventQueueSize		= 10000;

	/**
	 * control eventProcessThread shutdown
	 */
	private boolean									isContinue			= true;

	// /**
	// * cache latest query time for AhNeighbor table<br>
	// * apMac-datetime
	// */
	// private Map<String, Date> latestNeighborStatsTimeMap = new HashMap<String, Date>(
	// 5);

	/**
	 * empty list(String)
	 */
	private final List<String>						EMPTY_STRING_LIST	= new ArrayList<String>();

	private final int								MAX_EVENT_THREAD	= 1;

	private Thread[]								eventProcessThreadPool;

	private final CacheMgmt							cacheMgmt			= CacheMgmt.getInstance();

	private ScheduledExecutorService				timer_For_refresh;

	/**
	 * Construct method
	 */
	public BePerformModuleImpl() {
		setModuleId(BaseModule.ModuleId_Performance);
		setModuleName("BePerformanceModule");

		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
	}

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run() {
		//start timer processor
		timerProcessor = new TimerProcessor();
		timerProcessor.start();

		//start table partition processor
		tablePartitionProcessor = new TablePartitionProcessor();
		tablePartitionProcessor.start();

		// start statistic processor
		statisticsProcessor = new BePerformStatisticsProcessor();
		statisticsProcessor.setModule(this);
		statisticsProcessor.start();

		clientSessionProcessor = new BeClientSessionProcessor();
		clientSessionProcessor.setModule(this);
		clientSessionProcessor.startTask();

		performDataCollector = new BePerformDataCollector();
		performDataCollector.startTask();

		performInterferenceMapProcessor = new BePerformInterferenceMapProcessor();
		performInterferenceMapProcessor.setModule(this);
		performInterferenceMapProcessor.startTask();

		statisticsProcessor4Sync = new BeStatisticsProcessor4Sync();
		statisticsProcessor4Sync.setModule(this);

		startEventProcesser();

		bePerformanceSchedule = new AhPerformanceScheduleModule();
		bePerformanceSchedule.start();

		bePerformSummaryPageModule = new BePerformSummaryPageModule();
		bePerformSummaryPageModule.start();

		beNetworkReportScheduleModule = new BeNetworkReportScheduleModule();
		beNetworkReportScheduleModule.start();

		beClearHighIntervalAppAp = new BeClearHighIntervalAppAp();
		beClearHighIntervalAppAp.startTask();
		
		beClearHighIntervalReportAp = new BeClearHighIntervalReportAp();
		beClearHighIntervalReportAp.startTask();

		// process Load Statistics
		startLoadStatistics();

		// CAPWAP load collect
		startCapwapLoadCollect();

		maxClientsProcessor = new BeMaxClientsCountProcessor();
		maxClientsProcessor.startTask();

		interfaceClientStatsProcessor = new BeInterfaceClientStatsProcessor();
		interfaceClientStatsProcessor.startTask();

		studentStatsProcessor = new BeStudentStatsProcessor();
		studentStatsProcessor.startTask();

		slaStatsProcessor = new BeSLAStatsProcessor();
		slaStatsProcessor.startTask();

		spectralAnaProcessor = new BeSpectralAnalysisProcessor();
		spectralAnaProcessor.startTask();

		dataCollectionProcessor = new BeDataCollectionProcessor();
		dataCollectionProcessor.startTask();

		appReportCollectionProcessor = new BeAppReportCollectionProcessor();
		appReportCollectionProcessor.startTask();
		
		netdumpCollectionProcessor = new BeNetdumpCollectionProcessor();
		netdumpCollectionProcessor.startTask();
		
		interfaceReportProcessor = new BeInterfaceReportProcessor();
		interfaceReportProcessor.startTask();

		deviceRealTimeProcessor = new BeDeviceRealTimeProcessor();
		deviceRealTimeProcessor.startTask();

//		procedureProcessor = new BeProcedureProcessor();
//		procedureProcessor.startTask();

		bonjourGatewayProcessor = new BeBonjourGatewayProcessor();
		bonjourGatewayProcessor.startTask();

		otpEventProcessor = new BeOTPEventProcessor();
		otpEventProcessor.startTask();

		presenceProcessor = new BePresenceProcessor();
		presenceProcessor.startTask();

		dAInforProcessor = new BeDAInforProcessor();
		dAInforProcessor.startTask();

		bulkOperationProcessor = new BulkOperationProcessor();
		bulkOperationProcessor.start();

		reportFileParseProcessor = new BeReportFileParseProcessor();
		reportFileParseProcessor.startTask();

		copServerSendDataProcessor = new BeCopServerSendDataProcessor();
		copServerSendDataProcessor.startTask();

		applicationFlowGatherProcessor = new BeApplicationFlowGatherProcessor();
		applicationFlowGatherProcessor.startTask();

		reportCacheCleanProcessor = new BeReportCacheCleanProcessor();
		reportCacheCleanProcessor.startTask();
		
		reportFileCacheCleanProcessor = new BeReportFileCacheCleanProcessor();
		reportFileCacheCleanProcessor.startTask();

		serverCpuMemoryUsageProcessor = new BeServerCpuMemoryUsageProcessor();
		serverCpuMemoryUsageProcessor.start();

		messageHandlePool = new MessageHandleThreadPool();
		messageHandlePool.start();

//		dataRetentionProcessor = new DataRetentionProcessor();
//		dataRetentionProcessor.start();

		networkDeviceHistoryProcessor = new NetworkDeviceHistoryProcessor();
		networkDeviceHistoryProcessor.start();

		cleanDuplicateApTopProcessor = new BeCleanDuplicateApTopProcessor();
		cleanDuplicateApTopProcessor.start();

		deviceInventorySyncProcessor = new BeDeviceInventorySyncScheduleModule();
		deviceInventorySyncProcessor.start();
		
		//Process lldpcdp event individual from Holleywood
		lldpcdpEventProcessor = new BeLLDPCDPEventProcessor();
		lldpcdpEventProcessor.startTask();

		try {
			reportStartV2.run( );
		} catch (Throwable e) {
		    BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,"report schedule start failed : ", e);
		}

		// start timer for refresh
		if (timer_For_refresh == null || timer_For_refresh.isShutdown()) {
			timer_For_refresh = Executors.newSingleThreadScheduledExecutor();
			timer_For_refresh.scheduleWithFixedDelay(new RefreshCacheTimer(),
					120, TIMER_INTERVAL, TimeUnit.SECONDS);
		}

		return true;
	}

	private void startLoadStatistics() {
		if (timer == null || timer.isShutdown()) {
			timer = Executors.newSingleThreadScheduledExecutor();
		}
		try {
			LoadStatisticsProcessor processor = new LoadStatisticsProcessor();

			DebugUtil
					.performanceDebugInfo("BePerformModuleImpl: Load-Statistics timer begin running");
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Timer of Load-Statistics is running...");

			timer.scheduleWithFixedDelay(processor, 120, 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("BePerformModuleImpl: Load-Statistics timer failed, e="
					+ e.getMessage(), e);
			setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, e
					.getMessage());
		}
	}

	private void startCapwapLoadCollect() {
		if (timer2 == null || timer2.isShutdown()) {
			timer2 = Executors.newSingleThreadScheduledExecutor();
		}
		try {
			CapwapLoadCollector processor = new CapwapLoadCollector();

			DebugUtil
					.performanceDebugInfo("BePerformModuleImpl: CapwapLoadCollector timer begin running");
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Timer of CapwapLoadCollector is running...");

			timer2.scheduleWithFixedDelay(processor, 120, 2, TimeUnit.SECONDS);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformModuleImpl: CapwapLoadCollector timer failed, e="
									+ e.getMessage(), e);
			setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, e
					.getMessage());
		}
	}

	@Override
	public boolean init() {
		try {
			// clear active clients
//			String where = "connectstate= :s1";
			Object[] values = new Object[1];
			values[0] = AhClientSession.CONNECT_STATE_UP;
//			int count = QueryUtil.bulkRemoveBos(AhClientSession.class, new FilterParams(where,
//					values), null, null);

			int count = DBOperationUtil.executeUpdate("delete from ah_clientsession where connectstate= ?",values);

			DebugUtil
					.performanceDebugInfo("BePerformModuleImpl.init(): success remove active clients number "
							+ count);

			//init report server
			reportStartV2 = new ReportEndStart_V2( HmContextListener.context );


			// clear latest data
			QueryUtil.bulkRemoveBos(AhLatestRadioAttribute.class, null);
			QueryUtil.bulkRemoveBos(AhLatestXif.class, null);
			QueryUtil.bulkRemoveBos(AhLatestNeighbor.class, null);
			QueryUtil.bulkRemoveBos(AhLatestACSPNeighbor.class, null);
			QueryUtil.bulkRemoveBos(AhLatestInterferenceStats.class, null);
			QueryUtil.bulkRemoveBos(AhVPNStatus.class, null);
			QueryUtil.bulkRemoveBos(AhLLDPInformation.class, null);

			// clear latest data(interface report data)
			QueryUtil.bulkRemoveBos(AhPortAvailability.class, null);
			QueryUtil.bulkRemoveBos(AhStatsLatencyHigh.class, null);
			QueryUtil.bulkRemoveBos(AhStatsThroughputHigh.class, null);
			QueryUtil.bulkRemoveBos(AhStatsAvailabilityHigh.class, null);
			QueryUtil.bulkRemoveBos(AhStatsLatencyLow.class, null);
			QueryUtil.bulkRemoveBos(AhStatsThroughputLow.class, null);
			QueryUtil.bulkRemoveBos(AhStatsAvailabilityLow.class, null);
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BePerformModuleImpl.init(): clear active clients failed.", e);
		}

		return true;
	}

	@Override
	public boolean shutdown() {
		// stop thread
		isContinue = false;
		for (int i = 0; i < MAX_EVENT_THREAD; i++) {
			BeBaseEvent stopThreadEvent = new BeBaseEvent();
			addEvent(stopThreadEvent);
		}

		// Stop Quartz and worker
		try {
			reportStartV2.shutdown( );
		} catch ( Throwable e ) {
		    BeLogTools.error(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,"report schedule stop failed : ", e);
		}

		boolean isSucc = false;
		if (statisticsProcessor != null) {
			isSucc = statisticsProcessor.shutdown();
		}

		if (clientSessionProcessor != null) {
			isSucc = isSucc & clientSessionProcessor.shutdown();
		}

		if (performDataCollector != null) {
			isSucc = isSucc & performDataCollector.shutdown();
		}

		if (performInterferenceMapProcessor != null) {
			isSucc = isSucc & performInterferenceMapProcessor.shutdown();
		}

		try {
			if (bePerformanceSchedule != null) {
				bePerformanceSchedule.shutdownScheduler();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			if (bePerformSummaryPageModule != null) {
				bePerformSummaryPageModule.shutdownScheduler();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (beClearHighIntervalAppAp != null) {
			isSucc = isSucc & beClearHighIntervalAppAp.shutdownScheduler();
		}
		
		if (beClearHighIntervalReportAp != null) {
			isSucc = isSucc & beClearHighIntervalReportAp.shutdownScheduler();
		}
		
		try {
			if (beNetworkReportScheduleModule != null) {
				beNetworkReportScheduleModule.shutdownScheduler();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reqSerialNumMap.clear();

		if (timer != null) {
			try {
				stopLoadStatisticsProcess();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (timer2 != null) {
			try {
				stopCapwapLoadCollect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (maxClientsProcessor != null) {
			isSucc = isSucc & maxClientsProcessor.shutdown();
		}

		if (interfaceClientStatsProcessor != null) {
			isSucc = isSucc & interfaceClientStatsProcessor.shutdown();
		}

		if (studentStatsProcessor != null) {
			isSucc = isSucc & studentStatsProcessor.shutdown();
		}

		if (slaStatsProcessor != null) {
			isSucc = isSucc & slaStatsProcessor.shutdown();
		}

		if (spectralAnaProcessor != null) {
			isSucc = isSucc & spectralAnaProcessor.shutdown();
		}

		if (dataCollectionProcessor != null) {
			isSucc = isSucc & dataCollectionProcessor.shutdown(true);
		}

		if (appReportCollectionProcessor != null) {
			isSucc = isSucc & appReportCollectionProcessor.shutdown(true);
		}
		
		if (netdumpCollectionProcessor != null) {
			isSucc = isSucc & netdumpCollectionProcessor.shutdown(true);
		}
		
		if (interfaceReportProcessor != null) {
			isSucc = isSucc & interfaceReportProcessor.shutdown();
		}

		if (deviceRealTimeProcessor != null) {
			isSucc = isSucc & deviceRealTimeProcessor.shutdown();
		}

//		if (procedureProcessor!=null) {
//			isSucc = isSucc & procedureProcessor.shutdownScheduler();
//		}

		if (bonjourGatewayProcessor!=null) {
			isSucc = isSucc & bonjourGatewayProcessor.shutdown();
		}
		
		if(lldpcdpEventProcessor != null){
			isSucc = isSucc & lldpcdpEventProcessor.shutdown();
		}

		if (timer_For_refresh != null && !timer_For_refresh.isShutdown()) {
			timer_For_refresh.shutdown();
		}

		if(otpEventProcessor != null){
			isSucc = isSucc & otpEventProcessor.shutdown();
		}

        if(presenceProcessor!=null){
        	isSucc = isSucc & presenceProcessor.shutdown(true);
        }

		if(dAInforProcessor != null){
			isSucc = isSucc & dAInforProcessor.shutdown();
		}

		if(bulkOperationProcessor != null) {
			bulkOperationProcessor.stop();
			bulkOperationProcessor = null;
		}

		if(tablePartitionProcessor != null) {
			tablePartitionProcessor.stop();
			tablePartitionProcessor = null;
		}

		if (reportFileParseProcessor != null) {
			reportFileParseProcessor.shutDown();
		}

		if (copServerSendDataProcessor != null) {
			copServerSendDataProcessor.shutDown();
		}

		if (applicationFlowGatherProcessor != null) {
			applicationFlowGatherProcessor.shutDown();
		}

		if (reportCacheCleanProcessor != null) {
			reportCacheCleanProcessor.shutDown();
		}
		
		if (reportFileCacheCleanProcessor != null) {
			reportFileCacheCleanProcessor.shutDown();
		}

		if (serverCpuMemoryUsageProcessor != null) {
			serverCpuMemoryUsageProcessor.stop();
		}

		if (messageHandlePool != null){
			messageHandlePool.stop();
		}

//		if(dataRetentionProcessor != null){
//			dataRetentionProcessor.stop();
//		}

		if(networkDeviceHistoryProcessor != null){
			networkDeviceHistoryProcessor.stop();
		}

		if(cleanDuplicateApTopProcessor != null){
			cleanDuplicateApTopProcessor.stop();
		}


		if (deviceInventorySyncProcessor != null) {
			try {
				deviceInventorySyncProcessor.shutdownScheduler();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		if(timerProcessor != null) {
			timerProcessor.stop();
			timerProcessor = null;
		}
		return isSucc;
	}

	private void stopLoadStatisticsProcess() throws InterruptedException {
		if (!timer.isShutdown()) {
			// Disable new tasks from being submitted.
			timer.shutdown();

			// Wait a while for existing tasks to terminate.
			if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
				// Cancel currently executing tasks.
				timer.shutdownNow();
				// Wait a while for tasks to respond to being canceled.
				if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
					DebugUtil
							.performanceDebugWarn("Remove Load-Statistics scheduler did not terminate.");
				}
			}

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Timer of Load-Statistics is shutdown");
		}
	}

	private void stopCapwapLoadCollect() throws InterruptedException {
		if (!timer2.isShutdown()) {
			// Disable new tasks from being submitted.
			timer2.shutdown();

			// Wait a while for existing tasks to terminate.
			if (!timer2.awaitTermination(10, TimeUnit.SECONDS)) {
				// Cancel currently executing tasks.
				timer2.shutdownNow();
				// Wait a while for tasks to respond to being canceled.
				if (!timer2.awaitTermination(10, TimeUnit.SECONDS)) {
					DebugUtil
							.performanceDebugWarn("Remove CapwapLoadCollect scheduler did not terminate.");

				}
			}

			// the below section will cause shutdown failed.
			// BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
			// "<BE Thread> Timer of CapwapLoadCollect is shutdown");
		}
	}

	/**
	 * start thread which process event in eventQueue
	 */
	public void startEventProcesser() {
		if (eventProcessThreadPool != null) {
			return;
		}

		DebugUtil
				.performanceDebugInfo("BePerformModuleImpl.startEventProcesser(): performance module will start "
						+ MAX_EVENT_THREAD + " threads to process event.");

		isContinue = true;

		eventProcessThreadPool = new EventProcesserThread[MAX_EVENT_THREAD];
		for (int i = 0; i < MAX_EVENT_THREAD; i++) {
			eventProcessThreadPool[i] = new EventProcesserThread();
			eventProcessThreadPool[i].setName("eventProcessThreadPool " + i);
			eventProcessThreadPool[i].start();
		}
	}

	class EventProcesserThread extends Thread {
		@Override
		public void run() {
			DebugUtil.performanceDebugInfo("statistics processor thread begin to run");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						// communication event
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						int msgType = communicationEvent.getMsgType();
						if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT) {
							statisticsProcessor.addEvent(communicationEvent);
						} else if (msgType == BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP) {
							handleCommunicationEventStatResponse(communicationEvent);
						} else if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
							handleCapwapEventResponse((BeCapwapClientEvent) communicationEvent);
						} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VPNSTATUS) {
								handleVPNStatusResultEvent((BeVPNStatusResultEvent) resultEvent);
							} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY) {
								handleAvailabilityResultEvent((BePortAvailabilityResultEvent) resultEvent);
							} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_NAAS_LICENSE) {
								handleNaasLicenseResultEvent((BeNaasLicenseResultEvent)resultEvent);
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugError(
							"EventProcesserThread: statistics processor thread--catch exception: ",
							e);
					setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_MONITORING,
							"Thread for processing statistics and SNMP event caught some error!");
				} catch (Error e) {
					DebugUtil.performanceDebugError(
							"EventProcesserThread: statistics processor thread--catch error: ", e);
					setSystemLog(HmSystemLog.LEVEL_CRITICAL, HmSystemLog.FEATURE_MONITORING,
							"VM internal error," + e.getMessage());
				}
			}

			DebugUtil
					.performanceDebugInfo("EventProcesserThread: statistics processor thread is stopped");
		}
	}

	private void handleAvailabilityResultEvent(BePortAvailabilityResultEvent resultEvent) {
		try {
			String apMac = resultEvent.getApMac();
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache or ap is not managed.");
			}

			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());

			for(AhPortAvailability portInfo:resultEvent.getInterfAvailability()) {
				portInfo.setOwner(owner);
			}

			if(resultEvent.getSequenceNum() != 0) {
				//refresh all info
				//delete by mac address
				StringBuffer sql = new StringBuffer();
				sql.append("delete from AH_PORT_AVAILABILITY where mac = '").append(resultEvent.getApMac()).append("'");
				try {
					QueryUtil.executeNativeUpdate(sql.toString());
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "handleAvailabilityResultEvent when delete sql:"+sql.toString(), e);
				}

				//insert info
				try {
					QueryUtil.bulkCreateBos(resultEvent.getInterfAvailability());
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "handleAvailabilityResultEvent,Exception when bulk create port info", e);
				}
			} else {
				//update or create info
				for (AhPortAvailability portInfo : resultEvent.getInterfAvailability()) {
					List<AhPortAvailability> updateBos = QueryUtil.executeQuery(AhPortAvailability.class, null,
							new FilterParams("mac=:s1 and interfname=:s2",
									new Object[] {portInfo.getMac(),
									portInfo.getInterfName()}));

					try {
						if (updateBos.isEmpty()) {
							QueryUtil.createBo(portInfo);
						} else {
							AhPortAvailability portInfo_temp = updateBos.get(0);
							portInfo.setId(portInfo_temp.getId());
							portInfo.setVersion(portInfo_temp.getVersion());
							QueryUtil.updateBo(portInfo);
						}
					} catch (Exception e) {
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "handleAvailabilityResultEvent,Exception when create or update port info", e);
					}
				}
			}

		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.handleAvailabilityResultEvent(): catch exception.", e);
		}
	}

	private void handleNaasLicenseResultEvent(BeNaasLicenseResultEvent resultEvent) {
		try {
			if(resultEvent.getOptType() != BeNaasLicenseEvent.NAAS_LICENSE_OPT_SYNC) {
				DebugUtil.performanceDebugWarn(
						"BePerformModuleImpl.handleNaasLicenseResultEvent(): Receive unknown naas license opt type:"+
						resultEvent.getOptType() + ", not sync type");
				return;
			}
			//do nothing when NAAS is disable
			if(resultEvent.getMaxCounter() == 0) {
				return;
			}
			//send stop message to disable NAAS
			BeNaasLicenseEvent event = new BeNaasLicenseEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setApMac(resultEvent.getApMac());
			event.setSequenceNum(sequenceNum);
			event.setOptType(BeNaasLicenseEvent.NAAS_LICENSE_OPT_STOP);
			event.setCurrentCounter(0);
			event.setMaxCounter(0);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.handleNaasLicenseResultEvent(): catch exception.", e);
		}
	}

	/**
	 * add event to queue
	 *
	 * @param event
	 *            -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(eventQueue.offer(event)) {
				if (lostEventCount > 0) {
					DebugUtil
							.performanceDebugError("BePerformModuleImpl.addEvent(): Lost "
									+ lostEventCount + " events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount++;
			}
		} catch (Exception e) {
			lostEventCount++;
			// DebugUtil.performanceDebugError(
			// "BePCIDataCollector.addEvent(): Exception while add event to queue"
			// , e);
		}
	}

	@Override
	public void eventDispatched(BeBaseEvent arg_Event) {
		if (arg_Event.isShutdownRequestEvent()) {
			shutdown();

			return;
		}
		super.eventDispatched(arg_Event);
		// communication event
		if (arg_Event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
			BeCommunicationEvent communicationEvent = (BeCommunicationEvent) arg_Event;

			// put interested communication event in queue
			switch (communicationEvent.getMsgType()) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT: {
				BeStatisticResultEvent statEvent = (BeStatisticResultEvent) communicationEvent;
				// validate stats serial number
				int statsSerialNumber = statEvent.parseStatsSerialNum();
				if (statsSerialNumber == 0) {
					// statistics result reported by AP actively.
					// put into insertList for performance.
//					clientSessionProcessor.addEvent(communicationEvent);
					performDataCollector.addEvent(communicationEvent);
				} else if (clientSessionProcessor.is4ClientTrapStatResult(statsSerialNumber)
						|| clientSessionProcessor.is4RefreshActiveCleints(statsSerialNumber)) {
					clientSessionProcessor.addEvent(communicationEvent);
				}
				// else if (statisticsProcessor.is4StatisticsRefresh(statsSerialNumber)) {
				// // statistics refresh result
				// eventQueue.add(communicationEvent);
				// }
				else {
					// 1. statistics refresh result
					// 2. ap request statistics result
//					eventQueue.add(communicationEvent);
					statisticsProcessor.addEvent(communicationEvent);
				}
				break;
			}

			case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP: {
				BeGetStatisticEvent req = getRequestObj(communicationEvent.getSerialNum());
				if (req != null) {
					addEvent(communicationEvent);
				}

				break;
			}

			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP: {
				BeCapwapClientEvent responseEvent = (BeCapwapClientEvent) communicationEvent;
				if (responseEvent.getQueryType() != BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP
						&& responseEvent.getQueryType() != BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT) {
					return;
				}

				addEvent(responseEvent);

				break;
			}

			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT: {
				BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
				if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PCIDATA) {
					performDataCollector.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SPECTRALANALYSIS) {
					spectralAnaProcessor.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT) {
					interfaceClientStatsProcessor.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_DATACOLLECTIONINFO) {
//					dataCollectionProcessor.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPREPORTCOLLECTIONINFO){
					if (ReportCacheMgmt.getInstance().isEnableSystemL7Switch()) {
						appReportCollectionProcessor.addEvent(resultEvent);
					}
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENT_SELFREGISTER_INFO) {
					performDataCollector.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP) {
					performInterferenceMapProcessor.addEvent(communicationEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VPNSTATUS) {
					addEvent(communicationEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY) {
					addEvent(communicationEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_NAAS_LICENSE) {
					addEvent(communicationEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PSE_STATUS) {
					deviceRealTimeProcessor.addEvent(resultEvent);
				} else if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY) {
					bonjourGatewayProcessor.addEvent(resultEvent);
				} else if(resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP){
					otpEventProcessor.addEvent(resultEvent);
				} else if(resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PRESENCE) {
					presenceProcessor.addEvent((BePresenceResultEvent)resultEvent);
				} else if(resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO){
					lldpcdpEventProcessor.addEvent(resultEvent);
				}
				break;
			}

			case BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT:
				handleBeTrapEvent(communicationEvent);
				break;
			// Connect Event
			case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
				spectralAnaProcessor.addEvent(communicationEvent);
				BeAPConnectEvent connectEvent = (BeAPConnectEvent)communicationEvent;
				if( 1 == connectEvent.getNetdumpFlag()) {
					netdumpCollectionProcessor.addEvent(connectEvent);
				}
				if( -1 != connectEvent.getRebootType()) {
					performDataCollector.addEvent(connectEvent);
				}
				break;
			case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
				slaStatsProcessor.addEvent(communicationEvent);
				break;
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP:
				dAInforProcessor.addEvent(communicationEvent);
				break;
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				if(appReportCollectionProcessor.isL7SerialNum(communicationEvent.getSerialNum())){
					appReportCollectionProcessor.addEvent(communicationEvent);
				}
				if(netdumpCollectionProcessor.isNetdumpSerialNum(communicationEvent.getSerialNum())){
					netdumpCollectionProcessor.addEvent(communicationEvent);
				}
				break;
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
				if(appReportCollectionProcessor.isL7SerialNum(communicationEvent.getSerialNum())){
					appReportCollectionProcessor.addEvent(communicationEvent);
				}
				if(netdumpCollectionProcessor.isNetdumpSerialNum(communicationEvent.getSerialNum())){
					netdumpCollectionProcessor.addEvent(communicationEvent);
				}				
				break;
			default:
				break;
			}
		}

		messageHandlePool.addEvent(arg_Event);
	}

	/**
	 * clear all latest data belong to given AP
	 *
	 * @param ap
	 *            -
	 */
	public void clearLatestStatsData(HiveAp ap) {
		clearLatestStatsData(ap.getMacAddress());
	}

	/**
	 * clear all latest data belong to given apMac
	 *
	 * @param apMac
	 *            -
	 */
	void clearLatestStatsData(String apMac) {
		String clearSQL = (new StringBuffer())
				.append("delete from hm_latestneighbor where apMac='").append(apMac).append("';")
				.append("delete from hm_latestradioattribute where apMac='").append(apMac).append(
						"';").append("delete from hm_latestxif where apMac='").append(apMac)
				.append("';").append("delete from hm_latestacspneighbor where apMac='").append(
						apMac).append("';").append(
						"delete from hm_latestinterferencestats where apMac='").append(apMac)
				.append("';").toString();

		try {
			QueryUtil.executeNativeUpdate(clearSQL);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.clearLatestStatsData(): catch exception,", e);
		}
	}

	/**
	 * clear all stats data belong to given AP
	 *
	 * @param ap -
	 */
	public void clearAllStatsData(HiveAp ap) {
		long begin = System.currentTimeMillis();
		String clearSQL = (new StringBuffer()).append("delete from hm_neighbor where apMac='")
				.append(ap.getMacAddress()).append("';").append(
						"delete from hm_radioattribute where apMac='").append(ap.getMacAddress())
				.append("';").append("delete from hm_xif where apMac='").append(ap.getMacAddress())
				.append("';").append("delete from hm_vifstats where apMac='").append(
						ap.getMacAddress()).append("';").append(
						"delete from hm_radiostats where apMac='").append(ap.getMacAddress())
				.append("';").append("delete from hm_association where apMac='").append(
						ap.getMacAddress()).append("';").append(
						"delete from ah_clientsession_history where apMac='").append(
						ap.getMacAddress()).append("';").append(
						"delete from hm_bandwidthsentinel_history where apMac='").append(
						ap.getMacAddress()).append("';").append(
						"delete from hm_acspneighbor where apMac='").append(ap.getMacAddress())
				.append("';").append("delete from hm_interferencestats where apMac='").append(
						ap.getMacAddress()).append("';").toString();

		try {
			QueryUtil.executeNativeUpdate(clearSQL);
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"Clear all stats data of " + ap.getHostName() + ", eclipse time is "
							+ (System.currentTimeMillis() - begin) + "ms");
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.clearAllStatsData(): catch exception,", e);
		}
	}

	/**
	 * clear partial latest data
	 *
	 * @param resultEvent
	 *            -
	 */
	void clearPartialLatestStatsData(BeStatisticResultEvent resultEvent) {
		String clearSQL = getClearPartialLastestStatsSQL(resultEvent);

		if (!clearSQL.isEmpty()) {
			try {
				QueryUtil.executeNativeUpdate(clearSQL);
			} catch (Exception e) {
				DebugUtil.performanceDebugError(
						"BePerformModuleImpl.clearPartialLatestStatsData(): catch exception,", e);
			}
		}
	}

	/**
	 * get delete latest table sql
	 *
	 * @param resultEvent
	 *            -
	 * @return -
	 */
	String getClearLastestInterferenceSQL(BeInterferenceMapResultEvent resultEvent) {
		String apMac = resultEvent.getApMac();

		String clearSQL = "";
		for (AhInterferenceStats interferenceStats : resultEvent.getInterferenceStatsList()) {
			int ifindex = interferenceStats.getIfIndex();

			clearSQL += "delete from hm_latestinterferencestats where apMac='" + apMac
					+ "' and ifindex=" + ifindex + ";";
			clearSQL += "delete from hm_latestacspneighbor where apMac='" + apMac
					+ "' and ifindex=" + ifindex + ";";
		}

		return clearSQL;
	}

	/**
	 * get latest data list
	 *
	 * @param statsData
	 *            -
	 * @return -
	 */
	List<HmBo> getLatestStatsData(Map<Byte, List<HmBo>> statsData) {
		List<HmBo> dataList = new ArrayList<HmBo>();

		for (Byte tableID : statsData.keySet()) {
			switch (tableID) {
			case BeCommunicationConstant.STATTABLE_AHNEIGHBOR: {

				List<AhLatestNeighbor> neighborList = convertToLatestNeighbor(statsData
						.get(tableID));
				if (!neighborList.isEmpty()) {
					dataList.addAll(neighborList);
				}

				break;
			}

			case BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE: {

				List<AhLatestRadioAttribute> radioAttributeList = convertToLatestRadioAttribute(statsData
						.get(tableID));
				if (!radioAttributeList.isEmpty()) {
					dataList.addAll(radioAttributeList);
				}

				break;
			}

			case BeCommunicationConstant.STATTABLE_AHXIF: {

				List<AhLatestXif> xifList = convertToLatestXif(statsData.get(tableID));
				if (!xifList.isEmpty()) {
					dataList.addAll(xifList);
				}

				break;
			}

			default:
				break;
			}
		}

		return dataList;
	}

	private List<AhLatestRadioAttribute> convertToLatestRadioAttribute(List<HmBo> boList) {
		if (boList == null || boList.isEmpty()) {
			return new ArrayList<AhLatestRadioAttribute>(0);
		}

		List<AhLatestRadioAttribute> resultList = new ArrayList<AhLatestRadioAttribute>(boList
				.size());

		for (HmBo bo : boList) {
			AhRadioAttribute attribute = (AhRadioAttribute) bo;
			AhLatestRadioAttribute latestRadioAttribute = new AhLatestRadioAttribute();
			latestRadioAttribute.setApMac(attribute.getApMac());
			latestRadioAttribute.setApSerialNumber(attribute.getApSerialNumber());
			latestRadioAttribute.setOwner(attribute.getOwner());
			latestRadioAttribute.setRadioChannel(attribute.getRadioChannel());
			latestRadioAttribute.setRadioNoiseFloor(attribute.getRadioNoiseFloor());
			latestRadioAttribute.setRadioTxPower(attribute.getRadioTxPower());
			latestRadioAttribute.setApName(attribute.getXifpk().getApName());
			latestRadioAttribute.setIfIndex(attribute.getXifpk().getIfIndex());
			latestRadioAttribute.setBeaconInterval(attribute.getBeaconInterval());
			latestRadioAttribute.setEirp(attribute.getEirp());
			latestRadioAttribute.setRadioType(attribute.getRadioType());
			latestRadioAttribute.setTimeStamp(new HmTimeStamp(attribute.getXifpk()
					.getStatTimeValue(), TimeZone.getDefault().getID()));

			resultList.add(latestRadioAttribute);
		}

		return resultList;
	}

	private List<AhLatestNeighbor> convertToLatestNeighbor(List<HmBo> boList) {
		if (boList == null || boList.isEmpty()) {
			return new ArrayList<AhLatestNeighbor>(0);
		}

		List<AhLatestNeighbor> resultList = new ArrayList<AhLatestNeighbor>(boList.size());

		for (HmBo bo : boList) {
			AhNeighbor neighbor = (AhNeighbor) bo;
			AhLatestNeighbor latestNeighbor = new AhLatestNeighbor();
			latestNeighbor.setApMac(neighbor.getApMac());
			latestNeighbor.setApName(neighbor.getApName());
			latestNeighbor.setApSerialNumber(neighbor.getApSerialNumber());
			latestNeighbor.setHostName(neighbor.getHostName());
			latestNeighbor.setIfIndex(neighbor.getIfIndex());
			latestNeighbor.setLinkCost(neighbor.getLinkCost());
			latestNeighbor.setLinkType(neighbor.getLinkType());
			latestNeighbor.setLinkUpTime(neighbor.getLinkUpTime());
			latestNeighbor.setNeighborAPID(neighbor.getNeighborAPID());
			latestNeighbor.setOwner(neighbor.getOwner());
			latestNeighbor.setRssi(neighbor.getRssi());
			latestNeighbor.setRxBroadcastFrames(neighbor.getRxBroadcastFrames());
			latestNeighbor.setRxDataFrames(neighbor.getRxDataFrames());
			latestNeighbor.setRxDataOctets(neighbor.getRxDataOctets());
			latestNeighbor.setRxMgtFrames(neighbor.getRxMgtFrames());
			latestNeighbor.setRxMulticastFrames(neighbor.getRxMulticastFrames());
			latestNeighbor.setRxUnicastFrames(neighbor.getRxUnicastFrames());
			latestNeighbor.setTimeStamp(neighbor.getTimeStamp());
			latestNeighbor.setTxBeDataFrames(neighbor.getTxBeDataFrames());
			latestNeighbor.setTxBgDataFrames(neighbor.getTxBgDataFrames());
			latestNeighbor.setTxBroadcastFrames(neighbor.getTxBroadcastFrames());
			latestNeighbor.setTxDataFrames(neighbor.getTxDataFrames());
			latestNeighbor.setTxDataOctets(neighbor.getTxDataOctets());
			latestNeighbor.setTxMgtFrames(neighbor.getTxMgtFrames());
			latestNeighbor.setTxMulticastFrames(neighbor.getTxMulticastFrames());
			latestNeighbor.setTxUnicastFrames(neighbor.getTxUnicastFrames());
			latestNeighbor.setTxViDataFrames(neighbor.getTxViDataFrames());
			latestNeighbor.setTxVoDataFrames(neighbor.getTxVoDataFrames());

			resultList.add(latestNeighbor);
		}

		return resultList;
	}

	private List<AhLatestXif> convertToLatestXif(List<HmBo> boList) {
		if (boList == null || boList.isEmpty()) {
			return new ArrayList<AhLatestXif>(0);
		}

		List<AhLatestXif> resultList = new ArrayList<AhLatestXif>(boList.size());

		for (HmBo bo : boList) {
			AhXIf xif = (AhXIf) bo;
			AhLatestXif latestXif = new AhLatestXif();
			latestXif.setApMac(xif.getApMac());
			latestXif.setApSerialNumber(xif.getApSerialNumber());
			latestXif.setIfAdminStatus(xif.getIfAdminStatus());
			latestXif.setIfConfMode(xif.getIfConfMode());
			latestXif.setIfMode(xif.getIfMode());
			latestXif.setIfName(xif.getIfName());
			latestXif.setIfOperStatus(xif.getIfOperStatus());
			latestXif.setIfPromiscuous(xif.getIfPromiscuous());
			latestXif.setIfType(xif.getIfType());
			latestXif.setOwner(xif.getOwner());
			latestXif.setSsidName(xif.getSsidName());
			latestXif.setApName(xif.getXifpk().getApName());
			latestXif.setIfIndex(xif.getXifpk().getIfIndex());
			latestXif.setTimeStamp(new HmTimeStamp(xif.getXifpk().getStatTimeValue(), TimeZone
					.getDefault().getID()));
			latestXif.setBssid(xif.getBssid());

			resultList.add(latestXif);
		}

		return resultList;
	}

	/**
	 * API for save statistics data
	 *
	 * @param statEvent
	 *            -
	 */
	public void saveStatisticsResultData(BeStatisticResultEvent statEvent) {
		try {
			statEvent.parsePacket();
		} catch (BeCommunicationDecodeException e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformModuleImpl.saveStatisticsResultData(): Catch decode exception when receive statistics result",
							e);
			setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_MONITORING,
					"Failed to parse received statistics result packet! AP Mac="
							+ statEvent.getApMac());

			return;
		}

		// update data into db
		Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
		if (rowData.isEmpty()) {
			// it maybe happen
			DebugUtil
					.performanceDebugInfo("BePerformModuleImpl.saveStatisticsResultData(): parse receive statistics result, but there are no data in.");
			return;
		}

		try {
			List<HmBo> dataList = new ArrayList<HmBo>();
			for (Byte id : rowData.keySet()) {
				List<HmBo> rowList = rowData.get(id);
				if (!rowList.isEmpty()) {
					dataList.addAll(rowList);
				}
			}

			// update latest data
			// clearLatestStatsData(statEvent.getApMac());
			clearPartialLatestStatsData(statEvent);
			dataList.addAll(getLatestStatsData(rowData));

			QueryUtil.bulkCreateBos(dataList);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformModuleImpl.saveStatisticsResultData(): update statistic result to DB failed! ",
							e);
			setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_MONITORING,
					"Failed to save statistics data into database! AP Mac=" + statEvent.getApMac());
		}
	}

	/**
	 * get latest data
	 *
	 * @param resultEvent
	 *            -
	 * @return -
	 */
	List<HmBo> getLatestInterferenceData(BeInterferenceMapResultEvent resultEvent) {
		List<HmBo> dataList = new ArrayList<HmBo>();

		List<AhLatestInterferenceStats> latestInterferenceList = convertToLatestInterferenceStats(resultEvent
				.getInterferenceStatsList());
		dataList.addAll(latestInterferenceList);

		List<AhLatestACSPNeighbor> latestNeighborList = convertToLatestACSPNeighbor(resultEvent
				.getNeighborList());
		dataList.addAll(latestNeighborList);

		return dataList;
	}

	private List<AhLatestInterferenceStats> convertToLatestInterferenceStats(
			List<AhInterferenceStats> interferenceList) {
		if (interferenceList == null || interferenceList.isEmpty()) {
			return new ArrayList<AhLatestInterferenceStats>(0);
		}

		List<AhLatestInterferenceStats> resultList = new ArrayList<AhLatestInterferenceStats>(
				interferenceList.size());

		for (AhInterferenceStats interferenceStats : interferenceList) {
			AhLatestInterferenceStats latestInterferenceStats = new AhLatestInterferenceStats();
			latestInterferenceStats.setApMac(interferenceStats.getApMac());
			latestInterferenceStats.setAverageInterferenceCU(interferenceStats
					.getAverageInterferenceCU());
			latestInterferenceStats.setAverageNoiseFloor(interferenceStats.getAverageNoiseFloor());
			latestInterferenceStats.setAverageRXCU(interferenceStats.getAverageRXCU());
			latestInterferenceStats.setAverageTXCU(interferenceStats.getAverageTXCU());
			latestInterferenceStats.setChannelNumber(interferenceStats.getChannelNumber());
			latestInterferenceStats.setCrcError(interferenceStats.getCrcError());
			latestInterferenceStats.setIfIndex(interferenceStats.getIfIndex());
			latestInterferenceStats.setOwner(interferenceStats.getOwner());
			latestInterferenceStats.setShortTermInterferenceCU(interferenceStats
					.getShortTermInterferenceCU());
			latestInterferenceStats.setShortTermNoiseFloor(interferenceStats
					.getShortTermNoiseFloor());
			latestInterferenceStats.setShortTermRXCU(interferenceStats.getShortTermRXCU());
			latestInterferenceStats.setShortTermTXCU(interferenceStats.getShortTermTXCU());
			latestInterferenceStats.setSnapShotInterferenceCU(interferenceStats
					.getSnapShotInterferenceCU());
			latestInterferenceStats
					.setSnapShotNoiseFloor(interferenceStats.getSnapShotNoiseFloor());
			latestInterferenceStats.setSnapShotRXCU(interferenceStats.getSnapShotRXCU());
			latestInterferenceStats.setSnapShotTXCU(interferenceStats.getSnapShotTXCU());
			latestInterferenceStats.setTimeStamp(interferenceStats.getTimeStamp());
			latestInterferenceStats.setInterferenceCUThreshold(interferenceStats
					.getInterferenceCUThreshold());
			latestInterferenceStats.setCrcErrorRateThreshold(interferenceStats
					.getCrcErrorRateThreshold());
			latestInterferenceStats.setSeverity(interferenceStats.getSeverity());

			resultList.add(latestInterferenceStats);
		}

		return resultList;
	}

	private List<AhLatestACSPNeighbor> convertToLatestACSPNeighbor(
			List<AhACSPNeighbor> acspNeighborList) {
		if (acspNeighborList == null || acspNeighborList.isEmpty()) {
			return new ArrayList<AhLatestACSPNeighbor>(0);
		}

		List<AhLatestACSPNeighbor> resultList = new ArrayList<AhLatestACSPNeighbor>(
				acspNeighborList.size());

		for (AhACSPNeighbor neighbor : acspNeighborList) {
			AhLatestACSPNeighbor latestACSPNeighbor = new AhLatestACSPNeighbor();
			latestACSPNeighbor.setApMac(neighbor.getApMac());
			latestACSPNeighbor.setIfIndex(neighbor.getIfIndex());
			latestACSPNeighbor.setChannelNumber(neighbor.getChannelNumber());
			latestACSPNeighbor.setLastSeen(neighbor.getLastSeen());
			latestACSPNeighbor.setNeighborMac(neighbor.getNeighborMac());
			latestACSPNeighbor.setNeighborRadioMac(neighbor.getNeighborRadioMac());
			latestACSPNeighbor.setOwner(neighbor.getOwner());
			latestACSPNeighbor.setRssi(neighbor.getRssi());
			latestACSPNeighbor
					.setSsid(AhConvertBOToSQL.changeUnPrintableString(neighbor.getSsid()));
			latestACSPNeighbor.setTimeStamp(neighbor.getTimeStamp());
			latestACSPNeighbor.setTxPower(neighbor.getTxPower());
			latestACSPNeighbor.setBssid(neighbor.getBssid());

			resultList.add(latestACSPNeighbor);
		}

		return resultList;
	}

	/**
	 * handle communication event of statistic response
	 *
	 * @param communicationEvent
	 *            -
	 */
	private void handleCommunicationEventStatResponse(BeCommunicationEvent communicationEvent) {
		BeGetStatisticEvent rsp = (BeGetStatisticEvent) communicationEvent;
		switch (rsp.getResult()) {
		case BeCommunicationConstant.RESULTTYPE_SUCCESS:
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE: {
			// del request serialNum from mem
			delRequestObj(rsp.getSerialNum());
			break;
		}

		case BeCommunicationConstant.RESULTTYPE_NOFSM:
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN: {
			// remove statistics number from cache
			BeGetStatisticEvent req = getRequestObj(rsp.getSerialNum());
			if (req == null) {
				return;
			}

			statisticsProcessor.removeStatsSerialNum(req.getSequenceNum());
			// statisticsProcessor4Sync.removeStatsSerialNumFromCache(req.getStatsSerialNum());
			clientSessionProcessor.removeStatsSerialNumFromCache(req.getSequenceNum());
			clientSessionProcessor.removeClientTrapStatSerialMap(req.getSequenceNum());
			// del request serialNum from mem
			delRequestObj(rsp.getSerialNum());

			// disconnected,log
//			DebugUtil
//					.performanceDebugWarn("BePerformModuleImpl.handleCommunicationEvent(): Failed get statistics data of AP( Mac:"
//							+ rsp.getApMac() + ") , result is " + rsp.getResult());
			break;
		}

			// // mark: resend request when response timeout or fsm not run.
			// case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			// case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
			// case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN: {
			// // send request again
			// int rspSerialNum = rsp.getSerialNum();
			//
			// BeGetStatisticEvent req = getRequestObj(rspSerialNum);
			// if (req == null) {
			// return;
			// }
			//
			// int newSerialNum = HmBeCommunicationUtil.sendRequest(req);
			// if (newSerialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// // connect closed
			// setSystemLog(HmSystemLog.LEVEL_MINOR, HmSystemLog.FEATURE_MONITORING,
			// "AP state machine is finite. Failed to send request for statistics data. AP Mac="
			// + rsp.getApMac());
			// break;
			// }
			//
			// // send again,log
			// DebugUtil
			// .performanceDebugWarn("BePerformModuleImpl.handleCommunicationEvent(): Statistics request respond fsm result, AP Mac="
			// + rsp.getApMac());
			//
			// // remove old, add new
			// delRequestObj(rspSerialNum);
			// addRequestObj(newSerialNum, req);
			// break;
			// }
		default:
			break;
		}
	}

	/**
	 * handle Interference response
	 *
	 * @param responseEvent
	 *            -
	 */
	private void handleCapwapEventResponse(BeCapwapClientEvent responseEvent) {
		switch (responseEvent.getResult()) {
		case BeCommunicationConstant.RESULTTYPE_SUCCESS: {
			break;
		}
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
		case BeCommunicationConstant.RESULTTYPE_NOFSM:
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN: {
			// disconnected,log
//			DebugUtil
//					.performanceDebugWarn("BePerformModuleImpl.handleCapwapEventResponse(): Failed get data of AP which mac address is "
//							+ responseEvent.getApMac()
//							+ " , result is "
//							+ responseEvent.getResult());
			break;
		}

			// // mark: resend request when response timeout or fsm not run.
			// case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			// case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
			// case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN: {
			// // send request again
			// responseEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			// responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTREQ);
			//
			// int newSerialNum = HmBeCommunicationUtil.sendRequest(responseEvent);
			// if (newSerialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// // connect closed
			// setSystemLog(HmSystemLog.LEVEL_MINOR, HmSystemLog.FEATURE_MONITORING,
			// "AP state machine is finite. AP Mac=" + responseEvent.getApMac());
			// break;
			// }
			//
			// // send again,log
			// DebugUtil
			// .performanceDebugWarn("BePerformModuleImpl.handleCapwapEventResponse(): Statistics request respond fsm result,AP Mac="
			// + responseEvent.getApMac());
			//
			// break;
			// }
		default:
			break;
		}
	}

	/**
	 * get delete latest table sql
	 *
	 * @param resultEvent
	 *            -
	 * @return -
	 */
	String getClearPartialLastestStatsSQL(BeStatisticResultEvent resultEvent) {
		String apMac = resultEvent.getApMac();
		Set<Byte> tableIDSet = resultEvent.getStatsRowData().keySet();

		String clearSQL = "";
		if (tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHNEIGHBOR)) {
			clearSQL += "delete from hm_latestneighbor where apMac='" + apMac + "';";
		}
		if (tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE)) {
			clearSQL += "delete from hm_latestradioattribute where apMac='" + apMac + "';";
		}
		if (tableIDSet.contains(BeCommunicationConstant.STATTABLE_AHXIF)) {
			clearSQL += "delete from hm_latestxif where apMac='" + apMac + "';";
		}

		return clearSQL;
	}


	/**
	 * clear vpn status data
	 */
	public void clearVpnStatusData(String apMac) {
		try {
			QueryUtil.bulkRemoveBos(AhVPNStatus.class, new FilterParams("lower(serverID)",
					apMac.toLowerCase()));
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.clearVpnStatusData(): catch exception.", e);
		}
	}

	/**
	 * clear bonjour gateway monitor data
	 */
	public void clearBonjourGatewayMonitorData(String apMac,Long domainId) {
		try {
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(domainId);
			BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class,
					"macAddress", apMac, owner.getId());
			if(bonjourGatewayMonitoring != null){
				// remove bonjour realm if realm id is not existed
				BeBonjourGatewayProcessor.removeReam(bonjourGatewayMonitoring.getRealmId(),owner.getId());

//				QueryUtil.removeBos(BonjourGatewayMonitoring.class, new FilterParams("lower(macAddress)",
//				apMac.toLowerCase()));
				String sql = "delete from BONJOUR_SERVICE_DETAIL where macAddress = '"+apMac+"'";
				QueryUtil.executeNativeUpdate(sql);
				sql = "delete from BONJOUR_GATEWAY_MONITORING where macAddress = '"+apMac+"'";
				QueryUtil.executeNativeUpdate(sql);

				if(bonjourGatewayMonitoring.getRealmId() != null){
					List<SimpleHiveAp> neighbors = bonjourGatewayProcessor.findNeighbors(owner.getId(),bonjourGatewayMonitoring.getRealmId());
					if(!neighbors.isEmpty()){
//						List<?> lists = QueryUtil.executeQuery(HiveAp.class, new String[]{"ipAddress"},null,new FilterParams("macAddress",apMac), owner.getId(),null);
						List<?> lists = QueryUtil.executeQuery("select bo.ipAddress from " + HiveAp.class.getSimpleName() + " bo", null, new FilterParams("macAddress", apMac), owner.getId(), null);
						if(lists != null && lists.get(0) != null){
							bonjourGatewayProcessor.removeBddNeighbor(neighbors,lists.get(0).toString());
						}
					}
				}
			}

		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.clearBonjourGatewayMonitorData(): catch exception.", e);
		}
	}

	/**
	 * handle vpn status result event
	 *
	 * @param vpnStatusResultEvent -
	 */
	public synchronized void handleVPNStatusResultEvent(BeVPNStatusResultEvent vpnStatusResultEvent) {
		try {
			String apMac = vpnStatusResultEvent.getApMac();
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());

			int vpnType = vpnStatusResultEvent.getVpnType();
			int status = vpnStatusResultEvent.getStatus();
			boolean reportAuto = vpnStatusResultEvent.getSequenceNum() == 0;

			if (vpnType == BeVPNStatusResultEvent.VPNTYPE_SERVER) {
				// server
				if (status == BeVPNStatusResultEvent.STATUS_DOWN) {
					QueryUtil.bulkRemoveBos(AhVPNStatus.class, new FilterParams("lower(serverID)",
							apMac.toLowerCase()));
				} else if (status == BeVPNStatusResultEvent.STATUS_UP) {
					if (!reportAuto) {
						QueryUtil.bulkRemoveBos(AhVPNStatus.class, new FilterParams(
								"lower(serverID)", apMac.toLowerCase()));
					}

					List<AhVPNStatus> createBoList = new ArrayList<AhVPNStatus>();
					List<AhVPNStatus> updateBoList = new ArrayList<AhVPNStatus>();

					// server status record
					List<AhVPNStatus> serverList = QueryUtil.executeQuery(AhVPNStatus.class, null,
							new FilterParams("clientID=:s1 and lower(serverID)=:s2", new Object[] {
									null, apMac.toLowerCase() }));
					if (serverList.isEmpty()) {
						AhVPNStatus bo = new AhVPNStatus();
						bo.setServerID(apMac);
						bo.setClientID(null);
						bo.setOwner(owner);
						createBoList.add(bo);
					}

					// create or update vpn channel record
					for (String remoteID : vpnStatusResultEvent.getRemoteNodeIDTimeMap().keySet()) {
						List<AhVPNStatus> list = QueryUtil
								.executeQuery(AhVPNStatus.class, null,
										new FilterParams(
												"lower(clientID)=:s1 and lower(serverID)=:s2",
												new Object[] { remoteID.toLowerCase(),
														apMac.toLowerCase() }));
						if (list.isEmpty()) {
							AhVPNStatus bo = new AhVPNStatus();
							bo.setServerID(apMac);
							bo.setClientID(remoteID);
							bo.setConnectTimeStamp(vpnStatusResultEvent.getRemoteNodeIDTimeMap()
									.get(remoteID));
							bo.setOwner(owner);
							createBoList.add(bo);
						} else {
							AhVPNStatus bo = list.get(0);
							bo.setConnectTimeStamp(vpnStatusResultEvent.getRemoteNodeIDTimeMap()
									.get(remoteID));
							updateBoList.add(bo);
						}
					}

					QueryUtil.bulkCreateBos(createBoList);
					QueryUtil.bulkUpdateBos(updateBoList);
				}
			} else if (vpnType == BeVPNStatusResultEvent.VPNTYPE_CLIENT) {
				// client
				if (status == BeVPNStatusResultEvent.STATUS_DOWN) {
					for (String remoteID : vpnStatusResultEvent.getRemoteNodeIDTimeMap().keySet()) {
						QueryUtil.removeBos(AhVPNStatus.class, new FilterParams(
								"lower(clientID)=:s1 and lower(serverID)=:s2", new Object[] {
										apMac.toLowerCase(), remoteID.toLowerCase() }));
					}
				} else if (status == BeVPNStatusResultEvent.STATUS_UP) {
					if (!reportAuto) {
						QueryUtil.bulkRemoveBos(AhVPNStatus.class, new FilterParams(
								"lower(clientID)", apMac.toLowerCase()));
					}
					List<AhVPNStatus> createBoList = new ArrayList<AhVPNStatus>();
					List<AhVPNStatus> updateBoList = new ArrayList<AhVPNStatus>();
					for (String remoteID : vpnStatusResultEvent.getRemoteNodeIDTimeMap().keySet()) {
						List<AhVPNStatus> list = QueryUtil
								.executeQuery(AhVPNStatus.class, null,
										new FilterParams(
												"lower(clientID)=:s1 and lower(serverID)=:s2",
												new Object[] { apMac.toLowerCase(),
														remoteID.toLowerCase() }));
						if (list.isEmpty()) {
							AhVPNStatus bo = new AhVPNStatus();
							bo.setServerID(remoteID);
							bo.setClientID(apMac);
							bo.setConnectTimeStamp(vpnStatusResultEvent.getRemoteNodeIDTimeMap()
									.get(remoteID));
							bo.setOwner(owner);
							createBoList.add(bo);
						} else {
							AhVPNStatus bo = list.get(0);
							bo.setConnectTimeStamp(vpnStatusResultEvent.getRemoteNodeIDTimeMap()
									.get(remoteID));
							updateBoList.add(bo);
						}
					}

					QueryUtil.bulkCreateBos(createBoList);
					QueryUtil.bulkUpdateBos(updateBoList);
				}
			} else if (vpnType == BeVPNStatusResultEvent.VPNTYPE_NOCONFIGURE) {
				// not vpn node
				QueryUtil.bulkRemoveBos(AhVPNStatus.class, new FilterParams(
						"lower(serverID)=:s1 or lower(clientID)=:s2", new Object[] {
								apMac.toLowerCase(), apMac.toLowerCase() }));
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.handleVPNStatusResultEvent(): catch exception.", e);
		}
	}

	// /**
	// * keep track of serialNum of send request for AhAssociation when trap
	// * client connect event.
	// */
	// private List<Short> association_SerialNumList = new ArrayList<Short>();
	//
	// /**
	// * keep track of statSerialNum&AhCurrentClientSession of send request for
	// * AhAssociation when trap client connect event.
	// */
	// private Map<Integer, AhCurrentClientSession> currentClientSessionMap = new HashMap<Integer,
	// AhCurrentClientSession>();

	public void handleBeTrapEvent(BeBaseEvent arg_Event) {
		BeTrapEvent trapEvent = (BeTrapEvent) arg_Event;
		if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE
				|| trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENTINFOMATION) {
			clientSessionProcessor.addEvent(trapEvent);
			slaStatsProcessor.addEvent(trapEvent);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT) {
			performDataCollector.addEvent(trapEvent);
			slaStatsProcessor.addEvent(trapEvent);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_INTERFERENCEALERT) {
			statisticsProcessor.addEvent(trapEvent);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
			interfaceClientStatsProcessor.addEvent(trapEvent);
			slaStatsProcessor.addEvent(trapEvent);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENTOSINFOMATION) {
			clientSessionProcessor.addEvent(trapEvent);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENT_SELF_REGISTER_INFO) {
			performDataCollector.addEvent(trapEvent);
		}
	}

	/**
	 * sync active clients when ap status changed managed status
	 *
	 * @param ap
	 *            -
	 */
	public void retrieveActiveClients(HiveAp ap) {
		clientSessionProcessor.retrieveActiveClients(ap);
	}

	/**
	 * sync request active clients
	 *
	 * @param apList
	 *            -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestActiveClients(Collection<SimpleHiveAp> apList) {
		return clientSessionProcessor.syncRequestActiveClients(apList);
	}

	/**
	 * query client AhAssociation list synchronized.
	 *
	 * @param apList -
	 * @return List<AhAssociation>, if return null, maybe disconnect with capwap
	 */
	public List<AhAssociation> syncQueryHiveAPsClients(List<SimpleHiveAp> apList) {
		return clientSessionProcessor.syncQueryHiveAPsClients(apList);
	}

	/**
	 * sync request all active clients
	 *
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestAllActiveClients() {
		return clientSessionProcessor.syncRequestAllActiveClients();
	}

	/**
	 * sync active clients when ap status changed not managed status
	 *
	 * @param ap
	 *            -
	 */
	public void removeActiveClients(HiveAp ap) {
		clientSessionProcessor.removeAllClientsByAp(ap);
	}

	/**
	 * query statistics data synchronously
	 *
	 * @param apList
	 *            -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestBonjourGateway(Collection<SimpleHiveAp> apList) {
		return bonjourGatewayProcessor.syncRequestBonjourGateway(apList);
	}

	/**
	 * query statistics data synchronously
	 *
	 * @param ap
	 *            : ap ,manage status should be managed
	 * @param tableIDList
	 *            : stats table id list, constant defined in BeCommunicationConstant
	 * @return 1. Statistics data,format: <tableID - tableData><br>
	 *         2. Empty map: no corresponding data in ap<br>
	 *         3. Null. communication error or ap is not managed.
	 */
	public Map<Byte, List<HmBo>> syncQueryStatistics(HiveAp ap, List<Byte> tableIDList) {
		return statisticsProcessor4Sync
				.syncQueryStatistics(ap, createQueryStatsParams(tableIDList));
	}

	/**
	 * query interference statistics data synchronously, just update latest data table
	 *
	 * @param ap
	 *            : ap ,manage status should be managed
	 * @param isRequestAll
	 *            : if true, ifindexRequestMap could be null.
	 * @param ifindexRequestMap
	 *            : comments in {@link BeInterferenceMapResultEvent}
	 *
	 * @return 1. Statistics data,BeInterferenceMapResultEvent<br>
	 *         2. Null. communication error or ap is not managed.
	 */
	public BeInterferenceMapResultEvent syncQueryInterferenceStats(HiveAp ap, boolean isRequestAll,
			Map<Integer, Byte> ifindexRequestMap) {
		return statisticsProcessor4Sync.syncQueryInterferenceStats(ap, isRequestAll,
				ifindexRequestMap);
	}

	/**
	 * sync client info from ahassociation data
	 *
	 * @param client
	 *            -
	 * @return success or failed.
	 */
	public boolean updateClientInfo(AhClientSession client) {
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", client
				.getApMac(), client.getOwner().getId());
		if (ap == null) {
			DebugUtil
					.performanceDebugError("BePerformModuleImpl.updateClientInfo: failed query corresponding ap from db which mac is "
							+ client.getApMac());
			return false;
		}

		Map<Byte, List<String>> statsTableInfo = new HashMap<Byte, List<String>>();
		List<String> tableInfo = new ArrayList<String>(2);
		tableInfo.add(String.valueOf(client.getIfIndex()));
		tableInfo.add(client.getClientMac());
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, tableInfo);

		Map<Byte, List<HmBo>> results = statisticsProcessor4Sync.syncQueryStatistics(ap,
				statsTableInfo);
		if (results == null) {
			return false;
		}

		List<HmBo> list = results.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
		if (list.isEmpty()) {
			return false;
		}

		boolean isUpdated = false;
		for (HmBo hmBo : list) {
			AhAssociation association = (AhAssociation) hmBo;
			if (association.getClientMac().equals(client.getClientMac())) {
//				updateCurrentClientSession(association, client);
				AhClientSession clientSession = clientSessionProcessor.createClientSessionFromAssociation(association);
				clientSession.setId(client.getId());
				clientSession.setVersion(client.getVersion());
				client = clientSession;
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			try {
				DBOperationUtil.updateBO(client);
//				QueryUtil.updateBo(client);
			} catch (Exception e) {
				DebugUtil.performanceDebugWarn(
						"BePerformModuleImpl.updateClientInfo(): catch exception", e);
			}
		}

		return isUpdated;
	}

	public boolean updateLLDPInfo(String reporterMac,
			Collection<AhLLDPInformation> lldps) {
		if (lldps == null) {
			DebugUtil
					.performanceDebugError("BePerformModuleImpl.updateLLDPInfo: reporter: "
							+ reporterMac + " lldp collection is null.");
			return false;
		}
		DebugUtil
				.performanceDebugInfo("BePerformModuleImpl.updateLLDPInfo: lldp info count: "
						+ lldps.size());
		boolean result = cleanLLDPInfo(reporterMac);
		DebugUtil
				.performanceDebugInfo("BePerformModuleImpl.updateLLDPInfo: remove previous data: "
						+ result);
		if (!result) {
			return false;
		}
		try {
			QueryUtil.bulkCreateBos(lldps);
			return true;
		} catch (Exception e) {
			DebugUtil
					.performanceDebugError(
							"BePerformModuleImpl.updateLLDPInfo(): catch exception.",
							e);
			return false;
		}
	}

	public boolean cleanLLDPInfo(String reporterMac){
		boolean result = true;
		try {
			QueryUtil.removeBos(AhLLDPInformation.class, new FilterParams("reporter", reporterMac));
		} catch (Exception e) {
			result = false;
			DebugUtil.performanceDebugError(
					"BePerformModuleImpl.cleanLLDPInfo(): catch exception.", e);
		}
		return result;
	}

	//
	// /**
	// * update cache for neighbor
	// *
	// * @param apMac -
	// * @param latestTime -
	// */
	// void updateNeighborLatestStatsTime(String apMac, Date latestTime) {
	// latestNeighborStatsTimeMap.put(apMac, latestTime);
	// }

	/**
	 * create query param for statistics
	 *
	 * @param tableIDList
	 *            -
	 * @return -
	 */
	public Map<Byte, List<String>> createQueryStatsParams(List<Byte> tableIDList) {
		Map<Byte, List<String>> statsMap;

		if (tableIDList.contains(BeCommunicationConstant.STATTABLE_ALLTABLE)) {
			statsMap = new HashMap<Byte, List<String>>(1);
			statsMap.put(BeCommunicationConstant.STATTABLE_ALLTABLE, EMPTY_STRING_LIST);
		}

		/**
		 * for data consistent, we need xif when query radiostats/neighbor/vifstats
		 */
		if ((tableIDList.contains(BeCommunicationConstant.STATTABLE_AHRADIOSTATES)
				|| tableIDList.contains(BeCommunicationConstant.STATTABLE_AHNEIGHBOR)
				|| tableIDList.contains(BeCommunicationConstant.STATTABLE_AHVIFSTATS) || tableIDList
				.contains(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE))
				&& (!tableIDList.contains(BeCommunicationConstant.STATTABLE_AHXIF))) {
			tableIDList.add(BeCommunicationConstant.STATTABLE_AHXIF);
		}

		statsMap = new HashMap<Byte, List<String>>(tableIDList.size());
		for (Byte tableID : tableIDList) {
			statsMap.put(tableID, EMPTY_STRING_LIST);
		}

		return statsMap;
	}

	public void activeClientAdd(SimpleHiveAp ap, int count, int radioMode) {
		BoMgmt.getMapHierarchyCache().activeClientAdded(ap, count);
		cacheMgmt.activeClientAdd(ap, count, radioMode);
	}

	public void activeClientRemove(SimpleHiveAp ap, int count,int radioMode) {
		BoMgmt.getMapHierarchyCache().activeClientRemoved(ap, count);
		cacheMgmt.activeClientRemove(ap, count, radioMode);
	}

	/**
	 * get req obj given serialNum
	 *
	 * @param serialNum
	 *            -
	 * @return -
	 */
	private BeGetStatisticEvent getRequestObj(int serialNum) {
		return reqSerialNumMap.get(serialNum);
	}

	/**
	 * keep track of send request and packet's serialNum
	 *
	 * @param serialNum
	 *            -
	 * @param event
	 *            -
	 */
	public void addRequestObj(int serialNum, BeGetStatisticEvent event) {
		reqSerialNumMap.put(serialNum, event);
	}

	/**
	 * request success, delete this serialNum from mem
	 *
	 * @param serialNum
	 *            -
	 */
	private void delRequestObj(int serialNum) {
		reqSerialNumMap.remove(serialNum);
	}

	/**
	 * add sequence number into cache, for receive corresponding result event.
	 *
	 * @param statsSerialNum -
	 */
	public void addStatsSerialNum4ClientRefresh(Integer statsSerialNum) {
		clientSessionProcessor.addStatsSerialNum4ClientRefresh(statsSerialNum);
	}

	public void updateInterfaceStatsPollInterval(int interval) {
		interfaceClientStatsProcessor.updateInterfaceStatsPollInterval(interval);
	}

	public void updateStatsStartMinute(int startMinute) {
		//interfaceClientStatsProcessor.updateInterfaceStatsPollInterval(startMinute);
		statisticsProcessor.updateStatsStartMinute(startMinute);

	}

	@Override
	public int startSpectralAnalysis(SimpleHiveAp ap, byte interf, String channel0, String channel1, short interval, int seconds) {
		return spectralAnaProcessor.startSpectralAnalysis(ap, interf, channel0, channel1, interval, seconds);
	}

	@Override
	public int stopSpectralAnalysis(SimpleHiveAp ap) {
		return spectralAnaProcessor.stopSpectralAnalysis(ap);
	}

	@Override
	public SpectralAnalysisDataSample[] fetchFFTDatas(SimpleHiveAp ap) {
		return spectralAnaProcessor.fetchFFTDatas(ap);
	}

	@Override
	public Map<Short, SpectralAnalysisData> fetchFFTData(SimpleHiveAp ap) {
		return spectralAnaProcessor.fetchFFTData(ap);
	}

	@Override
	public Map<Short, SpectralAnalysisData> fetchMaxHoldData(SimpleHiveAp ap) {
		return spectralAnaProcessor.fetchMaxHoldData(ap);
	}

	@Override
	public SpectralAnalysisDataSample[] fetchMaxHoldDatas(SimpleHiveAp ap) {
		return spectralAnaProcessor.fetchMaxHoldDatas(ap);
	}

	class RefreshCacheTimer implements Runnable {
		private int count_For_statistics = 0;
		private final int TIMEROUT_INTERVAL = 120;
		@Override
		public void run() {
			MgrUtil.setTimerName(BePerformModuleImpl.class.getSimpleName() + "." + this.getClass().getSimpleName());
			try {
				AhTimeoutEvent timer = new AhTimeoutEvent();

				//add timer event to perform data collection to refresh perform data to DB
				performDataCollector.addEvent(timer);
				//add timer event to bulk operation processor
				BulkOperationProcessor.addEvent(timer);

				//add timer event to statistics processor and interface client stats processor
				count_For_statistics ++;
				if(count_For_statistics >= (TIMEROUT_INTERVAL/TIMER_INTERVAL)) {
					statisticsProcessor.addEvent(timer);
					interfaceClientStatsProcessor.addEvent(timer);
					count_For_statistics = 0;
				}
			} catch (Exception e) {
				DebugUtil.performanceDebugError("RefreshCacheTimer.run() catch exception", e);
			} catch (Error e) {
				DebugUtil.performanceDebugError("RefreshCacheTimer.run() catch error", e);
			}
		}
	}

	@Override
	public void updateDataCollect(boolean dataOfImprovement) {
		dataCollectionProcessor.setCollectable(dataOfImprovement);
	}

	@Override
	public List<SpectralAnalysisInterference> fetchInterference(SimpleHiveAp ap) {
		return spectralAnaProcessor.fetchInterference(ap);
	}

	@Override
	public AhPerformanceScheduleModule getAhPerformanceScheduleModule() {
		return bePerformanceSchedule;
	}

	@Override
	public BeClientSessionProcessor getBeClientSessionProcessor() {
		return clientSessionProcessor;
	}

	@Override
	public BeDataCollectionProcessor getBeDataCollectionProcessor() {
		return dataCollectionProcessor;
	}

	@Override
	public BeAppReportCollectionProcessor getBeAppReportCollectionProcessor() {
		return appReportCollectionProcessor;
	}
	
	@Override
	public BeNetdumpCollectionProcessor getBeNetdumpCollectionProcessor() {
		return netdumpCollectionProcessor;
	}
	@Override
	public BeDeviceRealTimeProcessor getBeDeviceRealTimeProcessor() {
		return deviceRealTimeProcessor;
	}

	@Override
	public BeInterfaceClientStatsProcessor getBeInterfaceClientStatsProcessor() {
		return interfaceClientStatsProcessor;
	}

	@Override
	public BeMaxClientsCountProcessor getBeMaxClientsCountProcessor() {
		return maxClientsProcessor;
	}

	@Override
	public BePerformDataCollector getBePerformDataCollector() {
		return performDataCollector;
	}

	@Override
	public BePerformStatisticsProcessor getBePerformStatisticsProcessor() {
		return statisticsProcessor;
	}

	@Override
	public BePerformSummaryPageModule getBePerformSummaryPageModule() {
		return bePerformSummaryPageModule;
	}

	@Override
	public BeSLAStatsProcessor getBeSLAStatsProcessor() {
		return slaStatsProcessor;
	}

	@Override
	public BeSpectralAnalysisProcessor getBeSpectralAnalysisProcessor() {
		return spectralAnaProcessor;
	}

	@Override
	public BeStatisticsProcessor4Sync getBeStatisticsProcessor4Sync() {
		return statisticsProcessor4Sync;
	}

	@Override
	public BeStudentStatsProcessor getBeStudentStatsProcessor() {
		return studentStatsProcessor;
	}

	@Override
	public BeInterfaceReportProcessor getBeInterfaceReportProcessor() {
		return interfaceReportProcessor;
	}

//	@Override
//	public BeProcedureProcessor getBeProcedureProcessor() {
//		return procedureProcessor;
//	}

	@Override
	public BeBonjourGatewayProcessor getBeBonjourGatewayProcessor() {
		return bonjourGatewayProcessor;
	}

	@Override
	public BeOTPEventProcessor getBeOTPEventProcessor(){
		return otpEventProcessor;
	}

	@Override
	public BePresenceProcessor getBePresenceProcessor() {
		return presenceProcessor;
	}

	@Override
	public BeServerCpuMemoryUsageProcessor getBeServerCpuMemoryUsageProcessor(){
		return serverCpuMemoryUsageProcessor;
	}

	public DataRetentionProcessor getDataRetentionProcessor(){
		return dataRetentionProcessor;
	}

	public NetworkDeviceHistoryProcessor getNetworkDeviceHistoryProcessor(){
		return networkDeviceHistoryProcessor;
	}

	@Override
	public BeCleanDuplicateApTopProcessor getBeCleanDuplicateApTopProcessor(){
		return cleanDuplicateApTopProcessor;
	}

	public TimerProcessor getTimerProcessor() {
		return timerProcessor;
	}
}