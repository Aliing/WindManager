/**
 *@filename		HmBePerformUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5  04:22:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

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
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhLLDPInformation;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBePerformUtil
{
	/**
	 * sync active clients when ap status changed managed status
	 *
	 * @param
	 * @return
	 */
	public static void retrieveActiveClients(HiveAp ap)
	{
		AhAppContainer.HmBe.getPerformModule().retrieveActiveClients(ap);
	}

	/**
	 * sync request active clients
	 *
	 * @param
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public static int syncRequestActiveClients(Collection<SimpleHiveAp> apList)
	{
		return AhAppContainer.HmBe.getPerformModule().syncRequestActiveClients(apList);
	}

	/**
	 * sync request Bonjour Gateway
	 *
	 * @param
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public static int syncRequestBonjourGateway(Collection<SimpleHiveAp> apList)
	{
		return AhAppContainer.HmBe.getPerformModule().syncRequestBonjourGateway(apList);
	}

	/**
	 * sync request all active clients
	 *
	 * @param
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	public static int syncRequestAllActiveClients()
	{
		return AhAppContainer.HmBe.getPerformModule().syncRequestAllActiveClients();
	}

	/**
	 * sync active clients when ap status changed not managed status
	 *
	 * @param
	 * @return
	 */
	public static void removeActiveClients(HiveAp ap)
	{
		AhAppContainer.HmBe.getPerformModule().removeActiveClients(ap);
	}

	/**
	 * query statistics data synchronously
	 *
	 * @param ap:
	 *            ap ,manage status should be managed
	 * @param tableIDList:
	 *            stats table id list, constant defined in
	 *            BeCommunicationConstant
	 * @return: 1. Statstics data,format: [tableID - tableData]<br>
	 *          2. Empty map: no corresponding data in ap<br>
	 *          3. Null. communication error or ap is not managed.
	 */
	public static Map<Byte, List<HmBo>> syncQueryStatistics(
		HiveAp ap,
		List<Byte> tableIDList)
	{
		return AhAppContainer.HmBe.getPerformModule().syncQueryStatistics(ap,
			tableIDList);
	}

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
	 * @return: 1. Statstics data,BeInterferenceMapResultEvent<br>
	 *          2. Null. communication error or ap is not managed.
	 */
	public static BeInterferenceMapResultEvent syncQueryInterferenceStats(HiveAp ap, boolean isRequestAll,
			Map<Integer, Byte> ifindexRequestMap)
	{
		return AhAppContainer.HmBe.getPerformModule().syncQueryInterferenceStats(ap, isRequestAll, ifindexRequestMap);
	}

	/**
	 * sync client info from ahassociation data
	 *
	 *@param
	 *
	 *@return success or failed.
	 */
	public static boolean updateClientInfo(AhClientSession client)
	{
		return AhAppContainer.HmBe.getPerformModule().updateClientInfo(client);
	}

	/**
	 * Update LLDP information persisted in database.
	 *
	 * @param reporterMac
	 * @param lldps
	 * @return
	 */
	public static boolean updateLLDPInfo(String reporterMac,
			Collection<AhLLDPInformation> lldps)
	{
		return AhAppContainer.HmBe.getPerformModule().updateLLDPInfo(reporterMac, lldps);
	}

	/**
	 * Remove LLDP information persisted in database.
	 *
	 * @param reporterMac
	 * @return
	 */
	public static boolean cleanLLDPInfo(String reporterMac)
	{
		return AhAppContainer.HmBe.getPerformModule().cleanLLDPInfo(reporterMac);
	}

	/**
	 * create query param for statistics
	 *
	 * @param
	 * @return
	 */
	public static Map<Byte, List<String>> createQueryStatsParams(
		List<Byte> tableIDList)
	{
		return AhAppContainer.HmBe.getPerformModule().createQueryStatsParams(
			tableIDList);
	}

//	/**
//	 * keep track of send request and packet's serialNum
//	 *
//	 * @param serialNum -
//	 * @param event -
//	 */
//	public static void addRequestObj(short serialNum, BeGetStatisticEvent event)
//	{
//		AhAppContainer.HmBe.getPerformModule().addRequestObj(serialNum,event);
//	}

	/**
	 * API for save statistics data
	 *
	 *@param
	 *
	 *@return
	 */
	public static void saveStatisticsResultData(BeStatisticResultEvent statEvent)
	{
		AhAppContainer.HmBe.getPerformModule().saveStatisticsResultData(statEvent);
	}

	/**
	 * clear all latest data belong to given AP
	 *
	 *@param
	 *
	 *@return
	 */
	public static void clearLatestStatsData(HiveAp ap)
	{
		AhAppContainer.HmBe.getPerformModule().clearLatestStatsData(ap);
	}
	/**
	 * clear all stats data belong to given AP
	 *
	 *@param
	 *
	 *@return
	 */
	public static void clearAllStatsData(HiveAp ap)
	{
		AhAppContainer.HmBe.getPerformModule().clearAllStatsData(ap);
	}

	/**
	 * add sequence number into cache, for receive corresponding result event.
	 *
	 *@param
	 *
	 *@return
	 */
	public static void addStatsSerialNum4ClientRefresh(Integer statsSerialNum) {
		AhAppContainer.HmBe.getPerformModule().addStatsSerialNum4ClientRefresh(statsSerialNum);
	}

	/**
	 * query client AhAssociation list synchronized.
	 *
	 *@param
	 *
	 *@return List<AhAssociation>, if return null, maybe disconnect with capwap
	 */
	public static List<AhAssociation> syncQueryHiveAPsClients(List<SimpleHiveAp> apList)
	{
		return AhAppContainer.HmBe.getPerformModule().syncQueryHiveAPsClients(apList);
	}

	/**
	 * handle vpn status result event
	 *
	 * @param event -
	 */
	public static void handleVPNStatusResultEvent(BeVPNStatusResultEvent vpnStatusResultEvent)
	{
		AhAppContainer.HmBe.getPerformModule().handleVPNStatusResultEvent(vpnStatusResultEvent);
	}

	/**
	 * clear vpn status data
	 */
	public static void clearVpnStatusData(String apMac) {
		AhAppContainer.HmBe.getPerformModule().clearVpnStatusData(apMac);
	}

	/**
	 * clear bonjour gateway monitor data
	 */
	public static void clearBonjourGatewayMonitorData(String apMac,Long domainId) {
		AhAppContainer.HmBe.getPerformModule().clearBonjourGatewayMonitorData(apMac,domainId);
	}

	public static void updateInterfaceStatsPollInterval(int interval) {
		AhAppContainer.HmBe.getPerformModule().updateInterfaceStatsPollInterval(interval);
	}

	public static void updateStatsStartMinute(int startMinute) {
		AhAppContainer.HmBe.getPerformModule().updateStatsStartMinute(startMinute);
	}


	/**
	 * start spectral analysis
	 *
	 * @param
	 * @return see com.ah.be.performance.BeSpectralAnalysisProcessor.startSpectralAnalysis(SimpleHiveAp ap)
	 */
	public static int startSpectralAnalysis(SimpleHiveAp ap, byte interf, String channel0, String channel1, short interval, int seconds)
	{
		return AhAppContainer.HmBe.getPerformModule().startSpectralAnalysis(ap, interf, channel0, channel1, interval, seconds);
	}

	/**
	 * stop spectral analysis
	 *
	 * @param
	 * @return
	 */
	public static int stopSpectralAnalysis(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().stopSpectralAnalysis(ap);
	}

	public static SpectralAnalysisDataSample[] fetchFFTDatas(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().fetchFFTDatas(ap);
	}

	public static SpectralAnalysisDataSample[] fetchMaxHoldDatas(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().fetchMaxHoldDatas(ap);
	}

	/**
	 * fetch spectral analysis data
	 *
	 * @param
	 * @return
	 */
	public static Map<Short, SpectralAnalysisData> fetchFFTData(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().fetchFFTData(ap);
	}

	public static Map<Short, SpectralAnalysisData> fetchMaxHoldData(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().fetchMaxHoldData(ap);
	}

	public static List<SpectralAnalysisInterference> fetchInterference(SimpleHiveAp ap)
	{
		return AhAppContainer.HmBe.getPerformModule().fetchInterference(ap);
	}

	public static void updateDataCollect(boolean dataOfImprovement) {
		AhAppContainer.HmBe.getPerformModule().updateDataCollect(dataOfImprovement);
	}
}