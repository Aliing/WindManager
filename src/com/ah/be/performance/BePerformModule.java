package com.ah.be.performance;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeInterferenceMapResultEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeVPNStatusResultEvent;
import com.ah.be.communication.mo.SpectralAnalysisData;
import com.ah.be.communication.mo.SpectralAnalysisDataSample;
import com.ah.be.communication.mo.SpectralAnalysisInterference;
import com.ah.be.performance.dataretention.DataRetentionProcessor;
import com.ah.be.performance.dataretention.NetworkDeviceHistoryProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhLLDPInformation;

/**
 *
 *@filename		BePerformModule.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-6 11:12:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public interface BePerformModule
{
	/**
	 * sync active clients when ap status changed managed status
	 *
	 * @param ap -
	 */
	public void retrieveActiveClients(HiveAp ap);

	/**
	 * sync request active clients
	 *
	 * @param apList -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestActiveClients(Collection<SimpleHiveAp> apList);

	/**
	 * sync request all active clients
	 *
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestAllActiveClients();

	/**
	 * sync active clients when ap status changed not managed status
	 *
	 * @param ap -
	 */
	public void removeActiveClients(HiveAp ap);

	/**
	 * sync request bonjour gateway
	 *
	 * @param apList -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public int syncRequestBonjourGateway(Collection<SimpleHiveAp> apList);

	/**
	 * query statistics data synchronously
	 *
	 * @param ap:
	 *            ap ,manage status should be managed
	 * @param tableIDList:
	 *            stats table id list, constant defined in
	 *            BeCommunicationConstant
	 * @return  1. Statistics data,format: <tableID - tableData><br>
	 *          2. Empty map: no corresponding data in ap<br>
	 *          3. Null. communication error or ap is not managed.
	 */
	public Map<Byte, List<HmBo>> syncQueryStatistics(
		HiveAp ap,
		List<Byte> tableIDList);

	/**
	 * query interference statistics data synchronously, just update latest data table
	 *
	 * @param ap:
	 *            ap ,manage status should be managed
	 * @param isRequestAll:
	 *            if true, ifindexRequestMap could be null.
	 * @param ifindexRequestMap:
	 *            comments in {@link BeInterferenceMapResultEvent}
	 *
	 * @return 1. Statistics data,BeInterferenceMapResultEvent<br>
	 *         2. Null. communication error or ap is not managed.
	 */
	public BeInterferenceMapResultEvent syncQueryInterferenceStats(HiveAp ap, boolean isRequestAll,
			Map<Integer, Byte> ifindexRequestMap);

	/**
	 * sync client info from ahassociation data
	 *
	 * @param client -
	 * @return success or failed.
	 */
	public boolean updateClientInfo(AhClientSession client);

	/**
	 * Update LLDP information persisted in database.
	 *
	 * @param reporterMac -
	 * @param lldps -
	 * @return -
	 */
	public boolean updateLLDPInfo(String reporterMac,
			Collection<AhLLDPInformation> lldps);

	/**
	 * Remove LLDP information persisted in database.
	 *
	 * @param reporterMac -
	 * @return -
	 */
	public boolean cleanLLDPInfo(String reporterMac);

	/**
	 * create query param for statistics
	 *
	 * @param tableIDList -
	 * @return -
	 */
	public Map<Byte, List<String>> createQueryStatsParams(
		List<Byte> tableIDList);

//	/**
//	 * keep track of send request and packet's serialNum
//	 *
//	 * @param serialNum -
//	 * @param event -
//	 */
//	public void addRequestObj(short serialNum, BeGetStatisticEvent event);

	/**
	 * API for save statistics data
	 *
	 * @param statEvent -
	 */
	public void saveStatisticsResultData(BeStatisticResultEvent statEvent);

	/**
	 * clear all latest data belong to given AP
	 *
	 * @param ap -
	 */
	public void clearLatestStatsData(HiveAp ap);
	/**
	 * clear all stats data belong to given AP
	 *
	 * @param ap -
	 */
	public void clearAllStatsData(HiveAp ap);

	/**
	 * add sequence number into cache, for receive corresponding result event.
	 *
	 * @param statsSerialNum -
	 */
	public void addStatsSerialNum4ClientRefresh(Integer statsSerialNum);

	public AhPerformanceScheduleModule getAhPerformanceScheduleModule();

	public BeClientSessionProcessor getBeClientSessionProcessor();

	public BeDataCollectionProcessor getBeDataCollectionProcessor();

	public BeAppReportCollectionProcessor getBeAppReportCollectionProcessor();	
	
	public BeNetdumpCollectionProcessor getBeNetdumpCollectionProcessor();

	public BeDeviceRealTimeProcessor getBeDeviceRealTimeProcessor();

	public BeInterfaceClientStatsProcessor getBeInterfaceClientStatsProcessor();

	public BeMaxClientsCountProcessor getBeMaxClientsCountProcessor();

	public BePerformDataCollector getBePerformDataCollector();

	public BePerformStatisticsProcessor getBePerformStatisticsProcessor();

	public BePerformSummaryPageModule getBePerformSummaryPageModule();

	public BeSLAStatsProcessor getBeSLAStatsProcessor();

	public BeSpectralAnalysisProcessor getBeSpectralAnalysisProcessor();

	public BeStatisticsProcessor4Sync getBeStatisticsProcessor4Sync();

	public BeStudentStatsProcessor getBeStudentStatsProcessor();

	public BeInterfaceReportProcessor getBeInterfaceReportProcessor();

//	public BeProcedureProcessor getBeProcedureProcessor();

	public BeBonjourGatewayProcessor getBeBonjourGatewayProcessor();

	public BeOTPEventProcessor getBeOTPEventProcessor();

	public BePresenceProcessor getBePresenceProcessor();

	public BeServerCpuMemoryUsageProcessor getBeServerCpuMemoryUsageProcessor();

	public DataRetentionProcessor getDataRetentionProcessor();

	public NetworkDeviceHistoryProcessor getNetworkDeviceHistoryProcessor();

	public BeCleanDuplicateApTopProcessor getBeCleanDuplicateApTopProcessor();
	/**
	 * query client AhAssociation list synchronized.
	 *
	 * @param apList -
	 * @return List<AhAssociation>, if return null, maybe disconnect with capwap
	 */
	public List<AhAssociation> syncQueryHiveAPsClients(List<SimpleHiveAp> apList);

	/**
	 * handle vpn status result event
	 *
	 * @param vpnStatusResultEvent -
	 */
	public void handleVPNStatusResultEvent(BeVPNStatusResultEvent vpnStatusResultEvent);

	/**
	 * clear vpn status data
	 */
	public void clearVpnStatusData(String apMac) ;

	/**
	 * clear vpn status data
	 */
	public void clearBonjourGatewayMonitorData(String apMac,Long domainId) ;

	public void updateInterfaceStatsPollInterval(int interval);

	public int startSpectralAnalysis(SimpleHiveAp ap, byte interf, String channel0, String channel1, short interval, int seconds);

	public int stopSpectralAnalysis(SimpleHiveAp ap);

	public SpectralAnalysisDataSample[] fetchFFTDatas(SimpleHiveAp ap);

	public SpectralAnalysisDataSample[] fetchMaxHoldDatas(SimpleHiveAp ap);

	public Map<Short, SpectralAnalysisData> fetchFFTData(SimpleHiveAp ap);

	public Map<Short, SpectralAnalysisData> fetchMaxHoldData(SimpleHiveAp ap);

	public List<SpectralAnalysisInterference> fetchInterference(SimpleHiveAp ap);

	public void updateDataCollect(boolean dataOfImprovement);

	public void updateStatsStartMinute(int startMinute);

	public TimerProcessor getTimerProcessor();

}