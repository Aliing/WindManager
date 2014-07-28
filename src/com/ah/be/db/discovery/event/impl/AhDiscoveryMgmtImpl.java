package com.ah.be.db.discovery.event.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.db.discovery.AhCapwapDiscovery;
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.AhTimeoutEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmSystemLog;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public final class AhDiscoveryMgmtImpl implements AhEventMgmt<BeBaseEvent>, Runnable {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhDiscoveryMgmtImpl.class.getSimpleName());

	private final int mgrCount;

	private final BlockingQueue<BeBaseEvent> eventQueue;

	private final ScheduledExecutorService scheduler;

	private final AtomicInteger lostEventCount;

	private final AhTimeoutEvent timeoutEvent;

	private ScheduledFuture<?> scheduledTask;

	private Thread[] eventMgrs;

	public AhDiscoveryMgmtImpl(int mgrCount) {
		this.mgrCount = mgrCount;
		eventQueue = new LinkedBlockingQueue<>(10000);
		scheduler = Executors.newSingleThreadScheduledExecutor();
		lostEventCount = new AtomicInteger(0);
		timeoutEvent = new AhTimeoutEvent();
	}

	@Override
	public void run() {
		Thread.currentThread().setName("HiveOS Discovery Timeout Event Watcher");
		add(timeoutEvent);
	}

	@Override
	public synchronized void start() {
		log.info("start", "<BE Thread>Starting HiveAP discovery process...");
		eventMgrs = new Thread[mgrCount];

		for (int i = 0; i < eventMgrs.length; i++) {
			final int index = i;

			eventMgrs[i] = new Thread() {
				@Override
				public void run() {
					long threadId = eventMgrs[index].getId();
					log.info("start", "Discovery event notification thread-" + threadId
							+ " started.");

					for (;;) {
						try {
							BeBaseEvent event = eventQueue.take();
							int eventType = event.getEventType();

							switch (eventType) {
							case BeEventConst.AH_TIMEOUT_EVENT:
								handleTimeoutEvent();
								break;
							case BeEventConst.COMMUNICATIONEVENTTYPE:
								handleEvent((BeCommunicationEvent) event);
								break;
							case BeEventConst.AH_SHUTDOWN_EVENT:
								log.info("start", "Discovery event notification thread-" + threadId
										+ " shut down.");
								return;
							default:
								log.warning("start", "Unknown event received. Event Type: "
										+ eventType);
								break;
							}
						} catch (Exception e) {
							log
									.error("start",
											"Exception occurred in HiveAP discovery process.", e);
						} catch (Error e) {
							log.error("start", "Error occurred in HiveAP discovery process.", e);
						}
					}
				}
			};

			eventMgrs[i].setName("HiveOS Discovery Processor");
			eventMgrs[i].start();
		}

		// Start timeout event task to generate timeout event every second.
		// This is used to flush timer for bulk of database operations.
		scheduledTask = scheduler.scheduleWithFixedDelay(this, 0L, 1L, TimeUnit.SECONDS);
		log.info("start", "Discovery process was successfully started.");
	}

	@Override
	public boolean isStarted() {
		boolean isStarted = false;

		if (eventMgrs != null) {
			for (Thread eventMgr : eventMgrs) {
				if (eventMgr != null && eventMgr.isAlive()) {
					isStarted = true;
					break;
				}
			}
		}

		return isStarted;
	}

	@Override
	public synchronized void stop() {
		log.info("stop", "Stopping discovery process...");

		try {
			// Shutdown scheduler.
			shutdownScheduler();
			log.info("stop", "Successfully shut down scheduler for discovery.");
		} catch (InterruptedException ie) {
			log.error("stop", "Failed to shut down scheduler for discovery.", ie);
		}

		// Clear queue before adding the shutdown event to stop the event
		// processor immediately.
		eventQueue.clear();

		if (eventMgrs != null) {
			BeBaseEvent shutdownEvent = new AhShutdownEvent();
			int threadCount = eventMgrs.length;

			for (int i = 0; i < threadCount; i++) {
				add(shutdownEvent);
			}
		}

		log.info("stop", "<BE Thread>Discovery process was successfully stopped. Events lost: "
				+ lostEventCount.intValue());
	}

	/*
	 * Add discovery event. BlockingQueue by itself is thread safe, but in case
	 * offer() fails, we want to be able to remove the head of the queue and
	 * re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	@Override
	public synchronized void add(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();
			log.warning("add", "Discovery queue is full, " + lostEventCount.intValue()
					+ " events lost.");

			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				log.warning("add", "Discard discovery event.");
			}

			if (!eventQueue.offer(event)) {
				log
						.warning("add",
								"Discovery queue is full even after removing the head of queue.");
			}
		}
	}

	@Override
	public void notify(BeBaseEvent event) {
		HmBeEventUtil.eventGenerated(event);
	}

	@Override
	public int getEventQueueSize() {
		return eventQueue.size();
	}

	@Override
	public Thread[] getEventProcessThreads() {
		return eventMgrs;
	}

	private void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Cancel scheduled task.
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being canceled.
		if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			log.warning("shutdownScheduler", "Scheduler was not terminated completely.");
		}
	}

	private void handleTimeoutEvent() {
		try {
			AhCapwapDiscovery.decreaseFlushTimer();
		} catch (Exception e) {
			String errorMsg = MgrUtil.getUserMessage("hm.system.log.ad.discovery.mgmt.capwap.flush.fail");
			log.error("handleTimeoutEvent", errorMsg, e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_DISCOVERY,
					errorMsg);
		}
	}

	private void handleEvent(BeCommunicationEvent event) {
		switch (event.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
		case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
			handleCapwapConnectEvent((BeAPConnectEvent) event);
			break;
		default:
			log.warning("handleEvent", "Unknown event received. Event Type: " + event.getMsgType());
			break;
		}
	}

	private void handleCapwapConnectEvent(BeAPConnectEvent event) {
		log.info("handleCapwapConnectEvent", "[" + event.getApMac() + "]Received a CAPWAP "
				+ (event.isConnectState() ? "connect event." : "disconnect event."));
		 if (event.getCapwapClientType() == BeCommunicationConstant.CAPWAPCLIENTTYPE_STUDENTMANAGER) {
			CacheMgmt.getInstance().getSMInfo().setConnected(event.isConnectState());
			log.info("Student Manager " + event.getApMac() +"/"+ event.getIpAddr()+ " is " +
					(event.isConnectState() ? "connected":"disconnected"));
			return;
		} 
		if (event.getCapwapClientType() == BeCommunicationConstant.CAPWAPCLIENTTYPE_AP 
				&& event.isConnectState()) {
			Boolean isTrue = AhConstantUtil
					.isTrue(AhConstantUtil.getDeviceByModel(AhConstantUtil
							.getHiveApModelByProductName(event.getProductName())));
			if (isTrue == null || !isTrue) {
				BeTopoModuleUtil.sendBeDeleteAPConnectRequest(event.getApMac(), true);
				log.warn("handleCapwapConnectEvent", "Device [" + event.getProductName() + "] unsupported.");
				return;
			}
		}
		AhCapwapDiscovery.discover(event);
	}

}