package com.ah.be.performance.messagehandle;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.communication.event.BeApplicationFlowInfoResultEvent;
import com.ah.be.communication.event.BeL7SignatureFileVersionResultEvent;
import com.ah.be.communication.event.BeRadsecProxyInfoResultEvent;
import com.ah.be.communication.event.BeRouterLTEVZInfoResultEvent;
import com.ah.be.communication.event.BeSwitchPortInfoResultEvent;
import com.ah.be.communication.event.BeSwitchPortStatsReportResultEvent;
import com.ah.be.communication.event.BeSwitchPortStatsResultEvent;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.impl.ApplicationFlowInfoHandle;
import com.ah.be.performance.messagehandle.impl.HandleL7SignatureEvent;
import com.ah.be.performance.messagehandle.impl.NewDeviceTopologyHandle;
import com.ah.be.performance.messagehandle.impl.RadsecProxyInfoHandle;
import com.ah.be.performance.messagehandle.impl.RouterLTEVZInfoHandle;
import com.ah.be.performance.messagehandle.impl.SwitchPortInfoHandle;
import com.ah.be.performance.messagehandle.impl.SwitchPortStatsHandle;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhSwitchPortStats;



public class MessageHandleThreadPool {
	
	private int												processorThreadNum = 3;
	
	private Map<String, MessageHandleInfo>					infoMap = null;
	
	private boolean											isContinue = false;
	
	EventProcessorThread[]									processorThreadArray = null;
	
	static private BlockingQueue<BeBaseEvent> 				eventQueue = null;
		
	public MessageHandleThreadPool() {
		infoMap = Collections.synchronizedMap(new HashMap<String, MessageHandleInfo>());
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(10000);
	}
	
	
	public void start() {
		if(isContinue)
			return;
		isContinue = true;
		
		//register event handle
		registerMessageHandle(BeSwitchPortInfoResultEvent.class,new SwitchPortInfoHandle());
		registerMessageHandle(BeSwitchPortStatsResultEvent.class,SwitchPortStatsHandle.getInstance());
		registerMessageHandle(BeSwitchPortStatsReportResultEvent.class,SwitchPortStatsHandle.getInstance());
		registerMessageHandle(AhDiscoveryEvent.class,new NewDeviceTopologyHandle());
		registerMessageHandle(BeApplicationFlowInfoResultEvent.class,new ApplicationFlowInfoHandle());
		registerMessageHandle(BeL7SignatureFileVersionResultEvent.class,HandleL7SignatureEvent.getInstance());
		registerMessageHandle(BeRouterLTEVZInfoResultEvent.class,new RouterLTEVZInfoHandle());
		registerMessageHandle(BeRadsecProxyInfoResultEvent.class,new RadsecProxyInfoHandle());
		
		processorThreadArray = new EventProcessorThread[processorThreadNum];
		for(int i = 0; i < processorThreadNum; i++) {
			processorThreadArray[i] = new EventProcessorThread(i+1);
			processorThreadArray[i].start();
		}
	}

	public void stop() {
		if(!isContinue)
			return;
		isContinue = false;
		if(processorThreadArray != null) {
			AhShutdownEvent event = new AhShutdownEvent();
			for(int i = 0; i < processorThreadNum; i++) {
				addEvent(event);
			}
		}
	}

	private void registerMessageHandle(Class<? extends BeBaseEvent> eventClass, MessageHandleInterface handle) {
		synchronized(infoMap) {
			MessageHandleInfo handleInfo = infoMap.get(eventClass.getName());
			if(handleInfo == null) {
				handleInfo = new MessageHandleInfo();
				handleInfo.getHandleList().add(handle);
				infoMap.put(eventClass.getName(), handleInfo);
			} else {
				handleInfo.getHandleList().add(handle);
			}
		}
		
		return;
	}
	
	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			if(infoMap.get(event.getClass().getName()) != null) {
				if(false == eventQueue.offer(event)) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,
							"MessageHandleThreadPool.addEvent(): Queue is full,discard event"+event.getClass().getName());
				}
			} else if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
				eventQueue.offer(event);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
					"MessageHandleThreadPool.addEvent(): Exception while add event to queue",
					e);
		}
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
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
							"MessageHandleThreadPool.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}
	
	class EventProcessorThread extends Thread {
		private int index = 1;
		
		public EventProcessorThread(int index) {
			this.index = index;
		}
		@Override
		public void run() {
			this.setName("Message Handle processor ["+index+"]");
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Message Handle processor ["+index+"] - event processor is running...");

			while (isContinue) {
				try {
					BeBaseEvent event = getEvent();
					if(event == null)
						continue;
					
					MessageHandleInfo handleInfo = null;
					synchronized(infoMap) {
						handleInfo = infoMap.get(event.getClass().getName());
						if(null != handleInfo) {
							//allow only one thread handle this message handle info
							if(handleInfo.getConcurrentCount() < handleInfo.getMaxConcurrentNum()) {
								handleInfo.setConcurrentCount(handleInfo.getConcurrentCount()+1);
							} else {
								//there has a thread to handle this message info, add event to queue of this message info
								System.out.println("Queue size:"+handleInfo.getEventQueue().size());
								if(false == handleInfo.getEventQueue().offer(event)) {
									BeLogTools.error(HmLogConst.M_PERFORMANCE,
											"MessageHandleThreadPool.EventProcessorThread: Queue is full,discard event"+event.getClass().getName());
								}
								handleInfo = null;
							}
						}
					}
					
					if(null == handleInfo) {
						continue;
					} else {
						try {
							while(handleInfo.getEventQueue().size() > 0) {
								BeBaseEvent baseEvent = handleInfo.getEventQueue().take();
								if(null != baseEvent) {
									for(MessageHandleInterface handle : handleInfo.getHandleList()) {
										handle.handleMessage(baseEvent);
									}
								}
							}
							for(MessageHandleInterface handle : handleInfo.getHandleList()) {
								handle.handleMessage(event);
							}
						} catch (Exception e) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE,"Exception in Message Handle processor thread ["+index+"]", e);
						}
					}
					
					synchronized(infoMap) {
						handleInfo = infoMap.get(event.getClass().getName());
						if(null != handleInfo) {
							handleInfo.setConcurrentCount(handleInfo.getConcurrentCount()-1);
							try {
								while(handleInfo.getEventQueue().size() > 0) {
									BeBaseEvent baseEvent = handleInfo.getEventQueue().take();
									if(null != baseEvent) {
										for(MessageHandleInterface handle : handleInfo.getHandleList()) {
											handle.handleMessage(baseEvent);
										}
									}
								}
							} catch (Exception e) {
								BeLogTools.error(HmLogConst.M_PERFORMANCE,"Exception in Message Handle processor thread ["+index+"],Fail to handle message", e);
							}
						}
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"Exception in Message Handle processor thread ["+index+"]", e);
				} catch (Error e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"Error in Message Handle processor thread ["+index+"]", e);
				}
			}
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Message Handle processor ["+index+"] - event processor is shutdown.");
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MessageHandleThreadPool pool = new MessageHandleThreadPool();
			pool.start();
			BeSwitchPortStatsResultEvent event = null;
			QueryUtil.executeQuery(HmDomain.class,null,null);
			HmDomain domain = new HmDomain();
			domain.setId(2L);
			domain.setVersion(new Timestamp(System.currentTimeMillis()));
			for(int i = 0; i < 1; i++) {
				event = new BeSwitchPortStatsResultEvent();
				event.setApMac("001977000000");
				event.setSequenceNum(10000);
				for(int j = 0; j < 5 ;j++) {
					AhSwitchPortStats port = new AhSwitchPortStats();
					port.setMac("001977000000");
					port.setPortName("eth"+j);
					port.setOwner(domain);
					port.setTxPacketCount(10);
					event.getPortStatsList().add(port);
				}
				pool.addEvent(event);
			}
			
			Thread.sleep(5000);
			for(int i = 0; i < 1; i++) {
				event = new BeSwitchPortStatsResultEvent();
				event.setApMac("001977000000");
				event.setSequenceNum(10000);
				for(int j = 0; j < 5 ;j++) {
					AhSwitchPortStats port = new AhSwitchPortStats();
					port.setMac("001977000000");
					port.setPortName("eth"+j);
					port.setOwner(domain);
					port.setTxPacketCount(14);
					event.getPortStatsList().add(port);
				}
				pool.addEvent(event);
			}
			
//			System.out.println(event.getClass());
//			BeBaseEvent baseEvent = event;
//			System.out.println(baseEvent.getClass());
			Thread.sleep(5000);
			pool.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

