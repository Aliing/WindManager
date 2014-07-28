package com.ah.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeInterfaceClientEvent;
import com.ah.be.communication.event.BeInterferenceMapEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

/**
 * 
 *@filename		QueryStatisticsTest.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-14 06:44:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
public class QueryStatisticsTest extends HmTest {
	private static final Tracer	log	= new Tracer(QueryStatisticsTest.class.getSimpleName());

	public void run() {
		List<SimpleHiveAp> apList = CacheMgmt.getInstance().getManagedApList();
		if (apList.isEmpty()) {
			return;
		}

		final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
		Map<Byte, List<String>> statsTableInfo = new HashMap<Byte, List<String>>();
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, EMPTY_STRING_LIST);
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHXIF, EMPTY_STRING_LIST);
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHNEIGHBOR, EMPTY_STRING_LIST);
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHRADIOSTATES, EMPTY_STRING_LIST);
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHVIFSTATS, EMPTY_STRING_LIST);
		statsTableInfo.put(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE, EMPTY_STRING_LIST);

		try {
			for (SimpleHiveAp ap : apList) {
				sendStatsRequest(ap, statsTableInfo);
				sendInterfaceClientRequest(ap);

				if (HiveAp.is11nHiveAP(ap.getHiveApModel())
						&& NmsUtil.compareSoftwareVersion("3.4.0.0", ap.getSoftVer()) <= 0) {
					sendInterferenceRequest(ap);
				}
			}
		} catch (Exception e) {
			log.error("run", "catch exception", e);
		}
	}

	private void sendInterfaceClientRequest(SimpleHiveAp ap) throws BeCommunicationEncodeException {
		BeInterfaceClientEvent request = new BeInterfaceClientEvent();
		request.setSimpleHiveAp(ap);
		request.setQueryAllTable(true);
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request.buildPacket();
		HmBeCommunicationUtil.sendRequest(request);
	}

	/**
	 * send request for interference statistics data
	 * 
	 * @param ap -
	 * @throws BeCommunicationEncodeException -
	 */
	private void sendInterferenceRequest(SimpleHiveAp ap) throws BeCommunicationEncodeException {
		BeInterferenceMapEvent interferenceEvent = new BeInterferenceMapEvent();
		interferenceEvent.setSimpleHiveAp(ap);
		interferenceEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		interferenceEvent.setRequestAll(true);

		interferenceEvent.buildPacket();

		int serialNum = HmBeCommunicationUtil.sendRequest(interferenceEvent);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			// connect closed
			return;
		}
	}

	private void sendStatsRequest(SimpleHiveAp ap, Map<Byte, List<String>> statsTableMap)
			throws BeCommunicationEncodeException {
		BeGetStatisticEvent req = new BeGetStatisticEvent();
		req.setSimpleHiveAp(ap);
		int statsSerialNum = HmBeCommunicationUtil.getSequenceNumber();
		req.setSequenceNum(statsSerialNum);
		req.setStatsTableIndexMap(statsTableMap);

		req.buildPacket();

		int serialNum = HmBeCommunicationUtil.sendRequest(req);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			return;
		}
	}
}
