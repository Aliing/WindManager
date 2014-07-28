package com.ah.be.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BePCIDataResultEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhDeviceRebootHistory;
import com.ah.bo.performance.AhPCIData;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BePerformDataCollector.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-12 10:26:26
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public class BePerformDataCollector {

	/**
	 * queue for event about client
	 */
	private final BlockingQueue<BeBaseEvent>	eventQueue;

	private static final int					eventQueueSize		= 10000;

	private boolean								isContinue			= true;

	private final List<HmBo>					insertList			= new ArrayList<>(
																			BeClientSessionProcessor.MAX_CACHE_SIZE);

	private int									lostEventCount			= 0;

	private EventProcessorThread				processEventThread;


	/**
	 * Construct method
	 */
	public BePerformDataCollector() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
	}

	public void startTask() {
		isContinue = true;
		
		// start event process thread
		processEventThread = new EventProcessorThread();
		processEventThread.setName("processPCIDataThread");
		processEventThread.start();

		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Perform data processor - scheduler for Perform data collector is running...");
	}

	/**
	 * add event to queue
	 * 
	 * @param event -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(eventQueue.offer(event)) {
				if(lostEventCount > 0) {
					DebugUtil.performanceDebugError(
							"BePerformDataCollector.addEvent(): Lost " + lostEventCount +" events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount ++;
			}
		} catch (Exception e) {
			lostEventCount ++;
		}
	}

	/**
	 * refresh db from cache list
	 * 
	 */
	private void refreshDb() {
		if (insertList.isEmpty()) {
			return;
		}

		try {
			BulkOperationProcessor.addBoList(AhAssociation.class, insertList);
			BulkOperationProcessor.addBoList(AhBandWidthSentinelHistory.class, insertList);
			BulkOperationProcessor.addBoList(AhDeviceRebootHistory.class, insertList);
			BulkOperationProcessor.addBoList(AhPCIData.class, insertList);
			List<HmBo> boList = new ArrayList<>(insertList.size());
			for(HmBo bo : insertList) {
				if(!(bo instanceof AhAssociation) &&
						!(bo instanceof AhBandWidthSentinelHistory) &&
						!(bo instanceof AhDeviceRebootHistory) &&
						!(bo instanceof AhPCIData)) {
					boList.add(bo);
				}
			}
			QueryUtil.bulkCreateBos(boList);
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn(
					"BePerformDataCollector.refreshDb(): Failed to insert db information.", e);
		}
		
		insertList.clear();
	}
	
	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeBaseEvent getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BePerformDataCollector.getEvent(): Exception while get event from queue", e);
			return null;
		}
	}
	
	public boolean shutdown() {
		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
						"<BE Thread> Perform data processor - scheduler for pci data collector is shutdown");
		
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		return true;
	}
	
	private void handleBandWidthTrap(BeTrapEvent trapEvent) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
		if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}

		AhBandWidthSentinelHistory historyBo = new AhBandWidthSentinelHistory();
		historyBo.setApMac(trapEvent.getApMac());
		historyBo.setApName(ap.getHostname());
		historyBo.setClientMac(trapEvent.getRemoteID());
		historyBo.setIfIndex(trapEvent.getIfIndex());
		historyBo.setBandWidthSentinelStatus(trapEvent.getBandwidthSentinelStatus());
		historyBo.setGuaranteedBandWidth(trapEvent.getGuaranteedBandWidth());
		historyBo.setActualBandWidth(trapEvent.getActualBandWidth());
		historyBo.setAction(trapEvent.getBandWidthAction());
		historyBo.setChannelUltil(trapEvent.getChannelUltil());
		historyBo.setInterferenceUltil(trapEvent.getInterferenceUltil());
		historyBo.setTxUltil(trapEvent.getTxUltil());
		historyBo.setRxUltil(trapEvent.getRxUltil());
		historyBo.setTimeStamp(new HmTimeStamp(trapEvent.getTimeStamp(), trapEvent.getTimeZone()));
		historyBo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));

		if (!insertList.add(historyBo)) {
			DebugUtil
					.performanceDebugError("BePerformDataCollectionProcessor.handleBandWidthTrap():Fail to add band width trap to insert list");
		}

		// update ah_clientsession bo
		// new status for client session only, suggested from nianrong.
		
// no used , comment by nianrong
//		int newStatus = historyBo.getBandWidthSentinelStatus();
//		if (historyBo.getBandWidthSentinelStatus() == AhBandWidthSentinelHistory.STATUS_CLEAR
//				&& historyBo.getAction() == AhBandWidthSentinelHistory.ACTION_STATUS_BOOST_YES) {
//			newStatus = AhBandWidthSentinelHistory.STATUS_ACTION;
//		}
//
//		boolean inCache = false;
//		for (HmBo bo : insertList) {
//			if (bo instanceof AhClientSession) {
//				AhClientSession currentClient = (AhClientSession) bo;
//				if (currentClient.getClientMac().equals(historyBo.getClientMac())
//						&& currentClient.getApMac().equals(historyBo.getApMac())
//						&& currentClient.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
//					currentClient.setBandWidthSentinelStatus(newStatus);
//					inCache = true;
//					break;
//				}
//			}
//		}
//		if (!inCache) {
//			// update database
//			try {
//				QueryUtil.updateBo(AhClientSession.class, "bandWidthSentinelStatus = :s1",
//						new FilterParams("clientMac = :s2 and connectstate = :s3", new Object[] {
//								newStatus, historyBo.getClientMac(),
//								AhClientSession.CONNECT_STATE_UP }));
//			} catch (Exception e) {
//				DebugUtil
//						.performanceDebugError(
//								"BeClientSessionProcessor.handleBandWidthTrap: Error when update client bandwidth status into db",
//								e);
//			}
//		}
	}

	private void handleClientSelfRegisterEvent(BeTrapEvent event) {
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(event.getApMac());
		if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
		if (owner == null)
			return;
		AhClientEditValues value = new AhClientEditValues();
		value.setClientMac(event.getRemoteID());
		value.setType(AhClientEditValues.TYPE_SELF_REGISTER);
		value.setClientUsername(event.getClientUserName());
		value.setEmail(event.getEmail());
		value.setCompanyName(event.getCompanyName());
		value.setExpirationTime(event.getExpirationTime());
		value.setSsidName(event.getClientSSID());
		value.setOwner(owner);

		try {
			List<AhClientEditValues> updateBos = QueryUtil.executeQuery(AhClientEditValues.class, null,
					new FilterParams("clientmac=:s1 and owner.id=:s2 and type=:s3 and ssidname=:s4",
							new Object[] {event.getRemoteID(),
							owner.getId(),AhClientEditValues.TYPE_SELF_REGISTER,event.getClientSSID()}));
			if (updateBos.isEmpty()) {
				QueryUtil.createBo(value);
			} else {
				AhClientEditValues value_temp = updateBos.get(0);
				value.setId(value_temp.getId());
				QueryUtil.updateBo(value);
			}
			CacheMgmt.getInstance().addClientEditValues(value);
			
			//update active client
			DBOperationUtil.executeUpdate("update ah_clientsession set clientusername = ? ,email = ? , companyName = ? where clientMac = ? and connectstate = ? and clientssid = ?",
					new Object[] {
					value.getClientUsername(),
					value.getEmail(),
					value.getCompanyName(),
					value.getClientMac(),
					AhClientSession.CONNECT_STATE_UP,
					value.getSsidName()});
		} catch (Exception e) {
			DebugUtil
			.performanceDebugError(
					"BePerformDataCollectionProcessor.handleClientSelfRegisterEvent: Error when update  into db",
					e);
		}
	}
		
	private void handleStatsPacket_ReportByAP(BeStatisticResultEvent statEvent) {
		Map<Byte, List<HmBo>> rowData = statEvent.getStatsRowData();
		if (rowData.isEmpty()) {
			return;
		}

		try {
			List<HmBo> dataList = new ArrayList<>();
			for (Byte id : rowData.keySet()) {
				List<HmBo> rowList = rowData.get(id);
				if (!rowList.isEmpty()) {
					// QueryUtil.bulkCreateBos(rowList);
					dataList.addAll(rowList);
				}
			}

			// update latest data
			// mark: current version, only association data will be report by ap actively
			Set<Byte> tableIDSet = statEvent.getStatsRowData().keySet();
			if (!(tableIDSet.size() == 1 && tableIDSet
					.contains(BeCommunicationConstant.STATTABLE_AHASSOCIATION))) {
				return;
				// module.clearPartialLatestStatsData(statEvent);
				// insertList.addAll(module.getLatestStatsData(rowData));
			}

			insertList.addAll(dataList);
		} catch (Exception e) {
			DebugUtil
					.performanceDebugWarn(
							"BePerformDataCollectionProcessor.handleStatsPacket_ReportByAP(): update statistic result to DB failed! ",
							e);
		}
	}

	
	private void handleDeviceRebootHistory(BeAPConnectEvent event) {
		if(-1 == event.getRebootType())
			return;
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(event.getApMac());
		if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		

		AhDeviceRebootHistory bo = new AhDeviceRebootHistory();
		bo.setMac(ap.getMacAddress());
		bo.setDeviceType(ap.getDeviceType());
		bo.setRebootType(event.getRebootType());
		bo.setRebootTimestamp(event.getRebootTimestamp());
		bo.setReceivedTimestamp(System.currentTimeMillis());
		bo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));

		if (!insertList.add(bo)) {
			DebugUtil
					.performanceDebugError("BePerformDataCollectionProcessor.handleDeviceRebootHistory():Fail to add to insert list");
		}
	}
		
	/**
	 * process for client trap and client event
	 * 
	 */
	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Perform data processor - event processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = getEvent();
					if (null == event)
						continue;
					
					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;

						int msgType = communicationEvent.getMsgType();
						if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PCIDATA) {
								SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
								if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
									return;
								}
								
								BePCIDataResultEvent pciResultEvent = (BePCIDataResultEvent)resultEvent;
								AhPCIData pciBo = new AhPCIData();
								pciBo.setAlertCode(pciResultEvent.getAlertCode());
								pciBo.setDestObject(pciResultEvent.getDestObject());
								pciBo.setNodeID(pciResultEvent.getNodeID());
								pciBo.setReportSystem(pciResultEvent.getReportSystem());
								pciBo.setSrcObject(pciResultEvent.getSrcObject());
								pciBo.setViolationCounter(pciResultEvent.getViolationCounter());
								pciBo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));
								pciBo.setMapID(ap.getMapContainerId());
								pciBo.setReportTime(System.currentTimeMillis());
								
								insertList.add(pciBo);
							}
						} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT) {
							BeStatisticResultEvent statEvent = (BeStatisticResultEvent) communicationEvent;
							// validate stats serial number
							int statsSerialNumber = statEvent.parseStatsSerialNum();
							if (statsSerialNumber == 0) {
								// parse statistics event to bo list and then insert into insertList
								handleStatsPacket_ReportByAP(statEvent);
							}
						} else if(msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT) {
							BeTrapEvent trapEvent = (BeTrapEvent) event;
							if (trapEvent.getTrapType() == BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT) {
								handleBandWidthTrap(trapEvent);
							} else if(trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENT_SELF_REGISTER_INFO) {
								handleClientSelfRegisterEvent(trapEvent);
							}
						} else if(msgType == BeCommunicationConstant.MESSAGETYPE_APCONNECT) {
							BeAPConnectEvent connectEvent = (BeAPConnectEvent)event;
							handleDeviceRebootHistory(connectEvent);
						}
						if (insertList.size() >= BeClientSessionProcessor.MAX_CACHE_SIZE) {
							refreshDb();
						}
					} else if (event.getEventType() == BeEventConst.AH_TIMEOUT_EVENT) {
						refreshDb();
					}
					
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn("BePerformDataCollector.EventProcessorThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn("BePerformDataCollector.EventProcessorThread.run() Error in processor thread", e);
				}
			}
			
			refreshDb();
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Perform data processor - event processor is shutdown.");
		}
	}

}