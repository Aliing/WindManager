package com.ah.be.communication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

/**
 * manage request, if time out, send time out message and remove the request
 *@filename		BeCommunicationRequestManager.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-6 10:51:04
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeCommunicationRequestManager implements Runnable {

	private ScheduledExecutorService							scheduler;

	// synchronize function yet,let's use hashmap here
	/**
	 * key: serialNum<br>
	 * value: event obj
	 */
	private final ConcurrentMap<Integer, BeCommunicationEvent>	requestMap					= new ConcurrentHashMap<Integer, BeCommunicationEvent>();

	// mark: support different time out result, although serial number not equal with sequence
	// number.
	/**
	 * key: sequence number<br>
	 * value: event obj
	 */
	private final ConcurrentMap<Integer, BeCommunicationEvent>	resultEventMap				= new ConcurrentHashMap<Integer, BeCommunicationEvent>();

	/**
	 * this list cache ap mac which is executing time consuming cli request and have not return
	 * response.<br>
	 * i not use vector at here because code still need synchronize block.
	 */
	private final List<String>									apMacList4TimeConsumingCli	= new LinkedList<String>();

	// /**
	// * this list cache ap mac which is executing configuration cli request and have not return
	// * response.
	// */
	// private final List<String> apMacList4ConfigurationCli = new LinkedList<String>();

	/**
	 * this map cache apmac and request code pair which is for transaction request control key:
	 * apmac value: request code, valid request code is >= 0
	 */
	private final ConcurrentMap<String, Integer>				configurationCliMap			= new ConcurrentHashMap<String, Integer>();

	public BeCommunicationRequestManager() {
	}

	public void start() {
		/*
		 * the steps below is originally placed in the private constructor of this class
		 * however, this will not apply to the situation in HA model.
		 * because in HA model, BE could be started and shutdowned more than once,
		 * and the private constructor is called only once
		 *
		 * joseph chen, 09/02/13
		 */
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
				"<BE Thread> Communication request manager is running...");
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		try {
			clearTimeoutObj();
		} catch (Exception e) {
			DebugUtil
					.commonDebugWarn(
							"BeCommunicationRequestManager.run(): Error occur when running the task of clear timeout request, Exception message: ",
							e);
		} catch (Error e) {
			DebugUtil
			.commonDebugWarn(
					"BeCommunicationRequestManager.run(): Error occur when running the task of clear timeout request, Error: ",
					e);
		}
	}

	/**
	 * remove timeout obj from mem, and send timeout rsp to event module
	 *
	 * @throws Exception -
	 */
	private void clearTimeoutObj() throws Exception {
		BeCommunicationProcessor communicationProcessor = HmBeCommunicationUtil.getBeCommunicationProcessor();

		for (Iterator<Integer> iter = requestMap.keySet().iterator(); iter.hasNext();) {
			Integer serialNum = iter.next();
			BeCommunicationEvent event = requestMap.get(serialNum);
			// if decreased timeout value is zero,let's remove this event
			if (event != null && event.elapseTime() == 0) {
				removeCLIMap(event);
				communicationProcessor.dispatchResponse(BeCommunicationProcessor.createRspEvent(
						event, BeCommunicationConstant.RESULTTYPE_TIMEOUT),false);
				// java.util.ConcurrentModificationException, iterator should not
				// be removed by dispatchResponse()
				iter.remove();
			}
		}

		// add for result event
		for (Iterator<Integer> iter = resultEventMap.keySet().iterator(); iter.hasNext();) {
			Integer sequenceNum = iter.next();
			BeCommunicationEvent event = resultEventMap.get(sequenceNum);
			// if decreased timeout value is zero,let's remove this event
			if (event != null && event.elapseTime() == 0) {
				removeCLIMap(event);
				communicationProcessor.dispatchResponse(BeCommunicationProcessor.createRspEvent(
						event, BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT),false);
				iter.remove();
			}
		}
	}
	
	/**
	 * remove request obj from mem, and send disconnect rsp to event module when receive ap disconnect event
	 *
	 * @throws Exception -
	 */
	public void clearRequesetObjForAp(String apMac) throws Exception {
		if(apMac == null)
			return;
		BeCommunicationProcessor communicationProcessor = HmBeCommunicationUtil.getBeCommunicationProcessor();

		for (Iterator<Integer> iter = requestMap.keySet().iterator(); iter.hasNext();) {
			Integer serialNum = iter.next();
			BeCommunicationEvent event = requestMap.get(serialNum);
			// if decreased timeout value is zero,let's remove this event
			if (event != null && event.getApMac().equalsIgnoreCase(apMac)) {
				removeCLIMap(event);
				communicationProcessor.dispatchResponse(BeCommunicationProcessor.createRspEvent(
						event, BeCommunicationConstant.RESULTTYPE_NOFSM),false);
				// java.util.ConcurrentModificationException, iterator should not
				// be removed by dispatchResponse()
				iter.remove();
			}
		}

		// add for result event
		for (Iterator<Integer> iter = resultEventMap.keySet().iterator(); iter.hasNext();) {
			Integer sequenceNum = iter.next();
			BeCommunicationEvent event = resultEventMap.get(sequenceNum);
			// if decreased timeout value is zero,let's remove this event
			if (event != null && event.getApMac().equalsIgnoreCase(apMac)) {
				removeCLIMap(event);
				communicationProcessor.dispatchResponse(BeCommunicationProcessor.createRspEvent(
						event, BeCommunicationConstant.RESULTTYPE_NOFSM),false);
				iter.remove();
			}
		}
	}

	/**
	 * add request into request manager special return when event is cli request and ap is busy now.
	 *
	 * @param event -
	 * @return -
	 */
	public byte addRequest(BeCommunicationEvent event) {
		if (event.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIREQ) {
			BeCliEvent cliReq = (BeCliEvent) event;
			String apMac = cliReq.getApMac();
			if (apMac != null) {
				if (cliReq.isTimeConsuming()) {
					synchronized (apMacList4TimeConsumingCli) {
						if (apMacList4TimeConsumingCli.contains(apMac)) {
							return BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY;
						} else {
							apMacList4TimeConsumingCli.add(apMac);
						}
					}
				} else if (cliReq.isConfiguration()) {
					Integer oldValue = configurationCliMap.putIfAbsent(apMac, cliReq
							.getTransactionCode());
					if (oldValue != null && oldValue != cliReq.getTransactionCode()) {
						return BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY;
					}
				} else if (cliReq.isNormal()) {
					//no limit for normal
//					if (apMacList4TimeConsumingCli.contains(apMac)
//							&& configurationCliMap.containsKey(apMac)) {
//						return BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY;
//					}
				} else if (cliReq.isEnforce()) {
					// no limit.
				} else {
					return BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY;
				}
			}
		}

		int serialNum = event.getSerialNum();

		requestMap.putIfAbsent(serialNum, event);

		return BeCommunicationConstant.RESULTTYPE_SUCCESS;
	}

	/**
	 * Remove from requestMap and then add into resultEventMap.
	 *
	 * @param responseEvent -
	 */
	public void migrateRequest(BeCommunicationEvent responseEvent) {
		BeCommunicationEvent requestEvent = requestMap.remove(responseEvent.getSerialNum());
		if (requestEvent != null) {
			// special for debug event, client monitor and vlan probe maybe no result event.
			if (requestEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTREQ) {
				BeCapwapClientEvent capwapClientEvent = (BeCapwapClientEvent) requestEvent;
				if (capwapClientEvent.getQueryType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING
						|| capwapClientEvent.getQueryType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE) {
					return;
				}
			}

			resultEventMap.putIfAbsent(requestEvent.getSequenceNum(), requestEvent);
		}
	}

	public BeCommunicationEvent getResultEventRequestObj(Integer sequenceNum) {
		return resultEventMap.get(sequenceNum);
	}

	public void removeResultEventRequest(Integer sequenceNum) {
		BeCommunicationEvent event = resultEventMap.remove(sequenceNum);

		removeCLIMap(event);
	}

	/**
	 * get request obj from cache
	 *
	 * @param serialNum -
	 * @return -
	 */
	public BeCommunicationEvent getRequestObj(Integer serialNum) {
		return requestMap.get(serialNum);
	}

	/**
	 * remove request from manager.
	 *
	 * @param serialNumber -
	 */
	public void removeRequest(Integer serialNumber) {
		BeCommunicationEvent event = requestMap.remove(serialNumber);

		removeCLIMap(event);
	}

	private void removeCLIMap(BeCommunicationEvent event) {
		if (event != null && event.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIREQ) {
			BeCliEvent cliReq = (BeCliEvent) event;
			if (event.getApMac() != null) {
				if (cliReq.isTimeConsuming()) {
					synchronized (apMacList4TimeConsumingCli) {
						apMacList4TimeConsumingCli.remove(event.getApMac());
					}
				} else if (cliReq.isConfiguration() && !cliReq.isTrasactionConfigurationCli()) {
					configurationCliMap.remove(event.getApMac());
				}
			}
		}
	}

	private final Random random	= new Random();

	/**
	 * request transaction code for given ap mac
	 *
	 * @param apMac -
	 * @return valid request code is >=0, return -1 if resource busy.
	 */
	public int requestConfigurationCliPermit(String apMac) {
		int randomInt = random.nextInt(Integer.MAX_VALUE);
		Integer oldValue = configurationCliMap.putIfAbsent(apMac, randomInt);
		if (oldValue == null) {
			return randomInt;
		}

		return -1;
	}

	public boolean isHiveApInUsed(String apMac){
		return configurationCliMap.containsKey(apMac);
	}

	/**
	 * request configuration cli permit, for transaction
	 *
	 * @param apMacSet -
	 * @return map: key is ap mac, value is transaction code(>=0) or -1.
	 */
	public Map<String, Integer> requestConfigurationCliPermit4Transaction(Set<String> apMacSet) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>(apMacSet.size());

		// cache all ap mac which get permit
		Set<String> tmpSet = new HashSet<String>();
		for (String apMac : apMacSet) {
			int result = requestConfigurationCliPermit(apMac);
			resultMap.put(apMac, result);
			if (result >= 0) {
				// success
				tmpSet.add(apMac);
			}
		}

		// some failed
		if (tmpSet.size() < apMacSet.size()) {
			// cancel all
			for (String apMac : tmpSet) {
				configurationCliMap.remove(apMac);
			}
		}

		return resultMap;
	}

	/**
	 * request configuration cli permit, not for transaction
	 *
	 * @param apMacSet -
	 * @return ap mac set which failed.
	 */
	public Set<String> requestConfigurationCliPermit(Set<String> apMacSet) {
		// cache all ap mac which get permit
		Set<String> tmpSet = new HashSet<String>();
		for (String apMac : apMacSet) {
			Integer oldValue = configurationCliMap.putIfAbsent(apMac, -1);
			if (oldValue == null) {
				tmpSet.add(apMac);
			}
		}

		// all success
		if (apMacSet.size() == tmpSet.size()) {
			return new HashSet<String>();
		}

		// some failed.
		for (String apMac : tmpSet) {
			configurationCliMap.remove(apMac);
		}

		apMacSet.removeAll(tmpSet);

		return apMacSet;
	}

	/**
	 * dismiss transaction flag for given ap mac
	 *
	 * @param apMac -
	 * @param transactionCode -
	 * @return -
	 */
	public boolean dismissConfigurationCliPermit(String apMac, int transactionCode) {
		return configurationCliMap.remove(apMac, transactionCode);
	}

	/**
	 * forcefully release relative resource.
	 *
	 * @param apMac -
	 */
	public void dismissConfigurationCliPermit(String apMac) {
		configurationCliMap.remove(apMac);
	}

	/**
	 * dispose function
	 */
	public void shutDown() {
		if (scheduler != null && !scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
				"<BE Thread> Communication request manager is shutdown");
		}
	}

}