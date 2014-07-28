package com.ah.be.performance;

import java.util.List;
import java.util.Map;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeInterferenceMapEvent;
import com.ah.be.communication.event.BeInterferenceMapResultEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

/**
 * query statistics data synchronously
 *@filename		BeStatisticsProcessor4Sync.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-3-4 11:00:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeStatisticsProcessor4Sync {
	
	/**
	 * reference to module instance
	 */
	private BePerformModuleImpl					module;

	public BeStatisticsProcessor4Sync() {
	}

	/**
	 * query statistics data synchronously, just update latest data
	 * 
	 * @param ap:
	 *            ap ,manage status should be managed
	 * @param tableInfos:
	 *            parameter map used when send request
	 * @return  1. Statistics data,format: <tableID - tableData><br>
	 *          2. Empty map: no corresponding data in ap<br>
	 *          3. Null. communication error or ap is not managed.
	 */
	public Map<Byte, List<HmBo>> syncQueryStatistics(HiveAp ap, Map<Byte, List<String>> tableInfos) {
		if (!isManagedAP(ap)) {
			DebugUtil.performanceDebugInfo("BeStatisticsProcessor4Sync.syncQueryStatistics(): AP("
					+ ap.getMacAddress() + ") can not be trusted.");
			return null;
		}

		BeGetStatisticEvent req = new BeGetStatisticEvent();
		req.setAp(ap);
		int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
		req.setSequenceNum(statsSerialNum);
		req.setStatsTableIndexMap(tableInfos);

		try {
			req.buildPacket();
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BeStatisticsProcessor4Sync.syncQueryStatistics(): build packet error.", e);

			// return, build packet error
			return null;
		}

		BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(req);
		// communication error
		if (response == null) {
			return null;
		}

		switch (response.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP: {
			DebugUtil
					.performanceDebugWarn("BeStatisticsProcessor4Sync.syncQueryStatistics(): Sync query statistics, failed getting response. Result is "
							+ response.getResult());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_MONITORING,
					MgrUtil.getUserMessage("hm.system.log.statistics.ap.error",ap.getMacAddress()) + response.getResult());

			return null;
		}

		case BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT: {
			BeStatisticResultEvent statEvent = (BeStatisticResultEvent) response;

			try {
				statEvent.parsePacket();
			} catch (BeCommunicationDecodeException e) {
				DebugUtil
						.performanceDebugWarn(
								"BeStatisticsProcessor4Sync.syncQueryStatistics(): Catch decode exception when receive statistics result",
								e);

				return null;
			}

			// Date statsTime = statsSerialNumTimeMap.get(statEvent.getStatsSerialNum());

			DebugUtil
					.performanceDebugInfo("BeStatisticsProcessor4Sync.syncQueryStatistics(): Get response statistics data from AP="
							+ ap.getMacAddress());

			Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
			if (rowData.isEmpty()) {
				DebugUtil
						.performanceDebugWarn("BeStatisticsProcessor4Sync.syncQueryStatistics(): parse receive statistics result, but there are no data in.");

				return null;
			}

			try {
				StringBuilder buffer = new StringBuilder();
				buffer.append(module.getClearPartialLastestStatsSQL(statEvent));

				for (Byte id : rowData.keySet()) {
					// mark: just save association data into db, for refresh client details.
					if (id == BeCommunicationConstant.STATTABLE_AHASSOCIATION) {
						List<HmBo> rowList = rowData.get(id);
						if (!rowList.isEmpty()) {
//								buffer.append(AhConvertBOToSQL.convertAssociationToSQL(rowList));
							BulkUpdateUtil.bulkInsertForAssociation(rowList);
						}
					}
				}

				String nativeSQL = buffer.toString();
				if (nativeSQL.trim().length() > 0) {
					QueryUtil.executeNativeUpdate(buffer.toString());
				}

				// update latest data
				try {
					QueryUtil.bulkCreateBos(module.getLatestStatsData(rowData));
				} catch (Exception e) {
					DebugUtil
							.performanceDebugError("BeStatisticsProcessor4Sync.syncQueryStatistics(): DB error when insert records.");
				}

				// log
				String logStr = "BeStatisticsProcessor4Sync.syncQueryStatistics(): Successfully update statistics to DB. statistics data content(tableID: rowNums): ";
				for (Byte id : rowData.keySet()) {
					logStr += (id + ":" + rowData.get(id).size()) + "	";
				}
				DebugUtil.performanceDebugInfo(logStr);
			} catch (Exception e) {
				DebugUtil
						.performanceDebugWarn(
								"BeStatisticsProcessor4Sync.syncQueryStatistics(): update statistic result to DB failed! ",
								e);

				return null;
			}

			return rowData;
		}

		default:
			break;
		}

		return null;
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
	 * @return  1. Statistics data,BeInterferenceMapResultEvent<br>
	 *          2. Null. communication error or ap is not managed.
	 */
	public BeInterferenceMapResultEvent syncQueryInterferenceStats(HiveAp ap, boolean isRequestAll,
			Map<Integer, Byte> ifindexRequestMap) {
		if (!isManagedAP(ap)) {
			DebugUtil
					.performanceDebugInfo("BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): AP("
							+ ap.getMacAddress() + ") can not be trusted.");
			return null;
		}

		// interference stats just for 11n stations now.
		if (!ap.is11nHiveAP()) {
			DebugUtil
					.performanceDebugInfo("BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): AP("
							+ ap.getMacAddress() + ") is not a 11n station.");
			return null;
		}

		BeInterferenceMapEvent interferenceEvent = new BeInterferenceMapEvent();
		interferenceEvent.setAp(ap);
		interferenceEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		interferenceEvent.setRequestAll(isRequestAll);
		interferenceEvent.setIfindexRequestMap(ifindexRequestMap);

		try {
			interferenceEvent.buildPacket();
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): build packet error.",
					e);

			return null;
		}

		BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(interferenceEvent);
		// communication error
		if (response == null) {
			return null;
		}

		switch (response.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP: {
			DebugUtil
					.performanceDebugWarn("BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): Sync query interference stats data, failed getting response. Result is "
							+ response.getResult());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_MONITORING,
					MgrUtil.getUserMessage("hm.system.log.interference.statistics.ap.error",ap.getMacAddress()) + response.getResult());

			return null;
		}

		case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT: {
			BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) response;
			if (resultEvent.getResultType() != BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP) {
				return null;
			}

			BeInterferenceMapResultEvent interferenceResultEvent = (BeInterferenceMapResultEvent) resultEvent;

			DebugUtil
					.performanceDebugInfo("BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): Get interference statistics data from AP="
							+ ap.getMacAddress());

			StringBuilder buffer = new StringBuilder();
			buffer.append(module.getClearLastestInterferenceSQL(interferenceResultEvent));

			// mark: also save interference stats data for GUI
//			buffer.append(AhConvertBOToSQL.convertInterferenceToSQL(interferenceResultEvent
//					.getInterferenceStatsList()));
			//
			// List<AhACSPNeighbor> neighborList = new ArrayList<AhACSPNeighbor>();
			// neighborList.addAll(interferenceResultEvent.getNeighborList());
			// buffer.append(AhConvertBOToSQL.convertACSPNeighborToSQL(neighborList));
			String nativeSQL = buffer.toString();
			
			try {
				if (nativeSQL.trim().length() > 0) {
					QueryUtil.executeNativeUpdate(buffer.toString());
				}

				BulkUpdateUtil.bulkInsertForInterference(interferenceResultEvent.getInterferenceStatsList());
				BulkUpdateUtil.bulkInsertForACSPNeighbor(interferenceResultEvent.getNeighborList());
				QueryUtil.bulkCreateBos(module.getLatestInterferenceData(interferenceResultEvent));
			} catch (Exception e) {
				DebugUtil
						.performanceDebugError("BeStatisticsProcessor4Sync.syncQueryInterferenceStats(): DB error when insert records.",e);
			}

			return interferenceResultEvent;
		}

		default:
			break;
		}

		return null;
	}

	public void setModule(BePerformModuleImpl module) {
		this.module = module;
	}

	/**
	 * check ap whether could be trusted
	 * 
	 * @param ap -
	 * @return -
	 */
	boolean isManagedAP(HiveAp ap) {
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(ap.getMacAddress());
		return !(simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED);
	}

}