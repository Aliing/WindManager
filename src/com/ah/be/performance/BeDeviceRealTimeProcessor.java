package com.ah.be.performance;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BePSEStatusResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhDevicePSEPower;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.util.MgrUtil;

public class BeDeviceRealTimeProcessor {
	
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private static final int eventQueueSize = 10000;
	
	private boolean isContinue;
	
	private final DeviceRealTimeThread deviceRealTimeThread;
	
	public BeDeviceRealTimeProcessor() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		
		isContinue = true;
		
		// data collection process thread
		deviceRealTimeThread = new DeviceRealTimeThread();
		deviceRealTimeThread.setName("deviceRealTimeProcessorThread");
		deviceRealTimeThread.start();
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeDeviceRealTimeProcessor.addEvent(): Exception while add event to queue", e);
		}
	}
	
	public void startTask() {
		// nothing else to do at current time
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Device real time Processor is running...");
	}
	
	public boolean shutdown() {
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Device real time Processor is shutdown");
		
		return true;
	}
	
	public class DeviceRealTimeThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(deviceRealTimeThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Device real time Process Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PSE_STATUS) {
								BePSEStatusResultEvent pseResultEvent = (BePSEStatusResultEvent)resultEvent;
								handlePSEStatusResultEvent(pseResultEvent);
							}
							
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDeviceRealTimeProcessor.DeviceRealTimeThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDeviceRealTimeProcessor.DeviceRealTimeThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	
	private void handlePSEStatusResultEvent(BePSEStatusResultEvent resultEvent) {
		String apMac = resultEvent.getApMac();
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		AhDevicePSEPower devicePSEPower = QueryUtil.findBoByAttribute(AhDevicePSEPower.class,
				"mac", apMac, owner.getId());
		try {
			if (devicePSEPower != null) {
				devicePSEPower.setTotalPower(resultEvent.getTotalPower());
				devicePSEPower.setPowerUsed(resultEvent.getPowerUsed());
				QueryUtil.updateBo(devicePSEPower);
			} else {
				devicePSEPower = new AhDevicePSEPower();
				devicePSEPower.setMac(apMac);
				devicePSEPower.setTotalPower(resultEvent.getTotalPower());
				devicePSEPower.setPowerUsed(resultEvent.getPowerUsed());
				devicePSEPower.setOwner(owner);
				QueryUtil.createBo(devicePSEPower);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE, 
					"BeDeviceRealTimeProcessor.handlePSEStatusResultEvent(): exception when update ah_device_pse_power.", e);
		}
		
		List<AhPSEStatus> pseStatusList = resultEvent.getPseStatusList();
		if (pseStatusList != null
				&& !pseStatusList.isEmpty()) {
			for (AhPSEStatus pseStatus : pseStatusList) {
				pseStatus.setOwner(owner);
			}
		}
		if(resultEvent.getSequenceNum() != 0) {
			// refresh PSE info manually
			StringBuffer sql = new StringBuffer();
			sql.append("delete from ah_pse_status where mac = '").append(resultEvent.getApMac()).append("'");
			try {
				QueryUtil.executeNativeUpdate(sql.toString());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeDeviceRealTimeProcessor.handlePSEStatusResultEvent(): Exception when delete sql:"+sql.toString(), e);
			}
			
			//insert PSE info
			try {
				QueryUtil.bulkCreateBos(pseStatusList);
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeDeviceRealTimeProcessor.handlePSEStatusResultEvent(): Exception when bulk create port info", e);
			}
		} else {
			// PSE info was sent by device automatically
			if (pseStatusList != null && pseStatusList.size() > 0) {
				for (AhPSEStatus pseStatus : pseStatusList) {
					List<AhPSEStatus> updateBos = QueryUtil.executeQuery(AhPSEStatus.class, null,
							new FilterParams("interfname=:s1 and mac=:s2",
									new Object[] {pseStatus.getInterfName(),
									pseStatus.getMac()}), owner.getId());
					
					try {
						if (updateBos.isEmpty()) {
							QueryUtil.createBo(pseStatus);
						} else {
							AhPSEStatus bo = updateBos.get(0);
							bo.setInterfName(pseStatus.getInterfName());
							bo.setStatus(pseStatus.getStatus());
							bo.setPdType(pseStatus.getPdType());
							bo.setPdClass(pseStatus.getPdClass());
							bo.setPower(pseStatus.getPower());
							bo.setPowerCutoffPriority(pseStatus.getPowerCutoffPriority());
							QueryUtil.updateBo(bo);
						}
					} catch (Exception e) {
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeDeviceRealTimeProcessor.handlePSEStatusResultEvent(): Exception when create or update port info", e);
					}
				}
			}
		}
	}

}