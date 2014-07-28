package com.ah.be.performance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCPUMemInfoEvent;
import com.ah.be.communication.event.BeCapwapClientInfoEvent;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.autoserver.AutoSelectDeviceServer;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.DeviceDaInfo;
import com.ah.ui.actions.hiveap.HiveApUpdateAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class BeDAInforProcessor {
	
	private static final Tracer log = new Tracer(BeDAInforProcessor.class
			.getSimpleName());
	
	private final BlockingQueue<BeBaseEvent> eventQueue;
	
	private static final int eventQueueSize = 10000;
	
	// interval 1 hour, when device reconnect to HM will send request.
	private static final int GET_DA_MESSAGE_INTERVAL = 60 * 1000 * 60;
	
	// interval 5 minutes synchronized IDM proxy.
	private static final int SYNCHRONIZED_IMD_PROXY_INTERVAL = 1000 * 60 * 5;
	
	private boolean isContinue;
	
	private DAInforThread dAInforTread;
	
	private DAInformationTimer daTimerMsg;
	
	private IDMProxySynchronized idmProxySyn;

	public BeDAInforProcessor(){
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		isContinue = true;
		dAInforTread = new DAInforThread();
		dAInforTread.setName("getDAInformationThread");
		dAInforTread.start();
		
		daTimerMsg = new DAInformationTimer();
		daTimerMsg.start();
		
		idmProxySyn = new IDMProxySynchronized();
		idmProxySyn.start();
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeDAInforProcessor.addEvent(): Exception while add event to queue", e);
		}
	}
	
	public void startTask() {
		// nothing else to do at current time
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Get DA information Processor is running...");
	}
	
	public boolean shutdown() {
		isContinue = false;
		eventQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		
		daTimerMsg.stop();
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Get DA information Processor is shutdown");
		
		idmProxySyn.stop();
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Synchronized IDM Proxy Processor is shutdown");
		
		return true;
	}
	
	public class DAInforThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(dAInforTread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Get DA information Processor Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP) {
							BeCPUMemInfoEvent clientInfo = (BeCPUMemInfoEvent)event;
							if (clientInfo.getQueryType() == BeCapwapClientInfoEvent.TYPE_CPUMEMQUERY) {
								handleDAInfor(clientInfo);
							}
							
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDAInforProcessor.DAInforThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDAInforProcessor.DAInforThread.run() Error in processor thread", e);
				}
			}
		}
		
		private void handleDAInfor(BeCPUMemInfoEvent envent) {
			try{
				SimpleHiveAp hiveAp = envent.getSimpleHiveAp();
				String macAddr = hiveAp.getMacAddress();
				SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(macAddr);
				HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
				
				DeviceDaInfo daInfo = null;
				boolean isExists = true;
				daInfo = QueryUtil.findBoByAttribute(DeviceDaInfo.class, "macAddress", macAddr);
				if(daInfo == null){
					isExists = false;
					daInfo = new DeviceDaInfo();
				}
				
				daInfo.setMacAddress(hiveAp.getMacAddress());
				daInfo.setOwner(owner);
				
				daInfo.setCpuUsage1(envent.getCpuUsage1());
				daInfo.setCpuUsage2(envent.getCpuUsage2());
				daInfo.setTotalMem(envent.getTotalMem());
				daInfo.setFreeMem(envent.getFreeMem());
				daInfo.setUsedMem(envent.getUsedMem());
				daInfo.setDAMac(envent.getdAMac());
				daInfo.setBDAMac(envent.getbDAMac());
				daInfo.setPortalMac(envent.getPortalMac());
				
				if(isExists){
					QueryUtil.updateBo(daInfo);
				}else{
					QueryUtil.createBo(daInfo);
				}
			}catch (Exception ex){
				DebugUtil.performanceDebugWarn(
						"BeDAInforProcessor.DAInforThread.run.handleDAInfor Exception in processor thread", ex);
			}
			
		}
	}
	
	public static class DAInformationTimer implements Runnable, QueryBo {
		
		private ScheduledExecutorService timer;

		public void start() {
			if (timer == null || timer.isShutdown()) {
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleWithFixedDelay(this, UpdateParameters.TIMER_DELAY_10MIN,
						GET_DA_MESSAGE_INTERVAL, TimeUnit.MILLISECONDS);
			}
		}

		public void stop() {
			if (timer != null && !timer.isShutdown()) {
				timer.shutdown();
			}
		}

		public boolean isStart() {
			return timer != null && !timer.isShutdown();
		}

		@Override
		public void run() {
			//remove these DA message, where that device has been remove from HM.
			String deleteSql = "delete from DEVICE_DA_INFO da "+
					"where not exists(select id from HIVE_AP ap where da.macAddress = ap.macAddress)";
			try {
				QueryUtil.executeNativeUpdate(deleteSql);
			} catch (Exception e) {
				log.error("Remove data DeviceDaInfo error :"+ e);
			}
			
			List<SimpleHiveAp> allSimpleDevice = CacheMgmt.getInstance().getAllApList();
			int count = 0;
			for(SimpleHiveAp ap : allSimpleDevice){
				try{
					count ++;
					if(count%10 == 0){
						Thread.sleep(1000);
					}
					BeCPUMemInfoEvent event = new BeCPUMemInfoEvent();
					event.setSimpleHiveAp(ap);
					event.buildPacket();
					HmBeCommunicationUtil.sendRequest(event, 30);
				}catch (Exception ex){
					log.error("queryHiveApCpuMenInfo", "error, e:" + ex);
				}
			}
		}
		
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo != null){
				if(bo.getOwner() != null)
					bo.getOwner().getId();
			}
			return null;
		}
		
	}
	
	public class IDMProxySynchronized implements Runnable {
		
		private ScheduledExecutorService idmTimer;

		public void start() {
			if (idmTimer == null || idmTimer.isShutdown()) {
				idmTimer = Executors.newSingleThreadScheduledExecutor();
				idmTimer.scheduleWithFixedDelay(this, UpdateParameters.TIMER_DELAY_10MIN,
						SYNCHRONIZED_IMD_PROXY_INTERVAL, TimeUnit.MILLISECONDS);
			}
		}
		
		public void stop() {
			if (idmTimer != null && !idmTimer.isShutdown()) {
				idmTimer.shutdown();
			}
		}
		
		public boolean isStart() {
			return idmTimer != null && !idmTimer.isShutdown();
		}

		@Override
		public void run() {
			try{
				String sql = "select distinct ap.id, ap.IDMProxy from hive_ap ap, DEVICE_DA_INFO da "+
							"WHERE ap.macAddress = da.macAddress AND ap.softver < '6.2.1.0' AND ap.manageStatus="+HiveAp.STATUS_MANAGED;
				List<?> resList = QueryUtil.executeNativeQuery(sql);
				
				Map<Long, Boolean> resMap_1 = new HashMap<>();
				Object[] resArgs = null;
				for(Object resObj : resList){
					resArgs = (Object[])resObj;
					resMap_1.put(Long.valueOf(resArgs[0].toString()), Boolean.valueOf(resArgs[1].toString()));
				}
				
				//select IDM proxy.
				AutoSelectDeviceServer.getInstance().autoSelectIDManagerProxy(resMap_1.keySet(), true);
				
				resList = QueryUtil.executeNativeQuery(sql);
				Map<Long, Boolean> resMap_2 = new HashMap<>();
				for(Object resObj : resList){
					resArgs = (Object[])resObj;
					resMap_2.put(Long.valueOf(resArgs[0].toString()), Boolean.valueOf(resArgs[1].toString()));
				}
				
				Iterator<Entry<Long, Boolean>> resItem_1 = resMap_1.entrySet().iterator();
				while(resItem_1.hasNext()){
					Entry<Long, Boolean> entryObj = resItem_1.next();
					if(!entryObj.getValue().equals(resMap_2.get(entryObj.getKey()))){
						HiveAp device = QueryUtil.findBoById(HiveAp.class, entryObj.getKey(), new HiveApUpdateAction());
						ProvisionProcessor.process(device, false, true, false, null, null, device.getCountryCode(), UpdateParameters.DELTA_SCRIPT_RUNNING);
					}
				}
			}catch(Exception e){
				log.error("IDMProxySynchronized failed: ", e);
			}
		}
	}
	
}
