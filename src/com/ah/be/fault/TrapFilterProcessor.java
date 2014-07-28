/**
 *@filename		TrapFilterProcessor.java
 *@version
 *@author		Frank
 *@createtime	2008-11-5 14:54:32
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.fault;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

/**
 * @author Frank
 * @version V1.0.0.0
 */
public class TrapFilterProcessor implements Runnable {

	private final BeFaultModule					parent;

	private int								filterInterval		= 5;											// seconds

	private final Map<String, ApCapwapLinkTrap>	capwapLinkTrapMap = Collections.synchronizedMap(new HashMap<String, ApCapwapLinkTrap>());

	private ScheduledExecutorService		filterScheduler;

	public TrapFilterProcessor(BeFaultModule arg_Module) {
		parent = arg_Module;
	}

	/**
	 * start filter task
	 */
	public void startTask() {
		// start timeout factory scheduler
		if (filterScheduler == null || filterScheduler.isShutdown()) {
			filterScheduler = Executors.newSingleThreadScheduledExecutor();
			filterScheduler.scheduleWithFixedDelay(this, 30, 1, TimeUnit.SECONDS);
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Trap filter processor is running...");
	}

	/**
	 * shutdown filter task
	 */
	public void shutdownTask() {
		if (filterScheduler != null && !filterScheduler.isShutdown()) {
			filterScheduler.shutdown();
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Trap filter processor is shutdown");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());

		synchronized (capwapLinkTrapMap) {
			try {
				Iterator<Map.Entry<String, ApCapwapLinkTrap>> ite = capwapLinkTrapMap.entrySet()
						.iterator();

				while (ite.hasNext()) {
					ApCapwapLinkTrap capwapTrap = ite.next().getValue();
					capwapTrap.setFilterTime(capwapTrap.getFilterTime() - 1);
					if (capwapTrap.getFilterTime() < 0) {
						if (null != capwapTrap.getDownEvent())
							parent.addTrapToQueue(capwapTrap.getDownEvent());
						ite.remove();
					}
				}
			} catch (Exception e) {
				DebugUtil.faultDebugError("TrapFilterProcessor:run():catch exception:", e);
			}
		}
	}

	/**
	 * set filter parameter
	 * 
	 * @param arg_FilterInterval -
	 */
	public void setFilterParameters(int arg_FilterInterval) {
		filterInterval = arg_FilterInterval;
	}

	/**
	 * Whether this event should be filter
	 * 
	 * @param arg_Event -
	 * @return boolean
	 */
	public boolean isFilter(BeCommunicationEvent arg_Event) {
		ApCapwapLinkTrap capwapTrap;
		boolean filter = false;
		if (filterInterval <= 0) {
			return filter;
		}
		switch (arg_Event.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
			synchronized (capwapLinkTrapMap) {
				SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(arg_Event.getApMac());
				if (null != ap) {
					capwapTrap = capwapLinkTrapMap.get(arg_Event.getApMac());
					if (null == capwapTrap) {
						capwapTrap = new ApCapwapLinkTrap();
					} else {
						DebugUtil
								.faultDebugInfo("TrapFilterProcessor:isFilter(): ap disconnect event is filtered["
										+ arg_Event.getApMac() + "]");
					}
					capwapTrap.setDownEvent(arg_Event);
					capwapTrap.setFilterTime(filterInterval + 1);
					capwapLinkTrapMap.put(arg_Event.getApMac(), capwapTrap);
					filter = true;
				}
			}
			break;
		case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
			synchronized (capwapLinkTrapMap) {
				capwapTrap = capwapLinkTrapMap.get(arg_Event.getApMac());
				if (null != capwapTrap) {
					if (null != capwapTrap.getDownEvent()) {
						filter = true;
						BeAPConnectEvent event = (BeAPConnectEvent) arg_Event;
						// be filtered when ap reboot or dtls state change
						if (BeAPConnectEvent.CLIENT_SERVER_TIMED_OUT == event.getReconnectReason()
								|| BeAPConnectEvent.CLIENT_UNKNOWN == event.getReconnectReason()) {
							DebugUtil
									.faultDebugInfo("TrapFilterProcessor:isFilter(): ap disconnect/connect events are filtered["
											+ arg_Event.getApMac() + "]");
						} else {
							parent.getTrapCapwapQueue().add(capwapTrap.getDownEvent());
							parent.getTrapCapwapQueue().add(arg_Event);
						}
						capwapTrap.setDownEvent(null);
					}
				}
			}
			break;
		default:
			break;
		}
		return filter;
	}

	class ApCapwapLinkTrap {
		private int				filterTime	= 5;
		BeCommunicationEvent	downEvent;

		public ApCapwapLinkTrap() {

		}

		public int getFilterTime() {
			return filterTime;
		}

		public void setFilterTime(int filterTime) {
			this.filterTime = filterTime;
		}

		public BeCommunicationEvent getDownEvent() {
			return downEvent;
		}

		public void setDownEvent(BeCommunicationEvent downEvent) {
			this.downEvent = downEvent;
		}
	}

}