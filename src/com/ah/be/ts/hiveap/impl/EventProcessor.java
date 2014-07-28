package com.ah.be.ts.hiveap.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.util.Tracer;

public abstract class EventProcessor implements AhEventMgmt<BeBaseEvent> {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(EventProcessor.class.getSimpleName());

	/* Event lost count */
	private final AtomicInteger lostEventCount = new AtomicInteger(0);

	/* Event queue */
	protected BlockingQueue<BeBaseEvent> eventQueue = new LinkedBlockingQueue<BeBaseEvent>(10000);

	/* HiveAP trouble shooting management implementation */
	protected HiveApDebugMgmtImpl hiveApDebug;

	/* Event processor */
	private Thread eventProc;

	@Override
	public synchronized void start() {
		log.info("start", "<BE Thread>Starting HiveAP trouble shooting " + getShortName().toLowerCase() + " processor...");
		boolean started = isStarted();

		// Start event processor.
		if (!started) {
			eventProc = new Thread() {
				@Override
				public void run() {
					for (;;) {
						try {
							BeBaseEvent event = eventQueue.take();
							int eventType = event.getEventType();

							switch (eventType) {
								case BeEventConst.COMMUNICATIONEVENTTYPE:
									hiveApDebug.handleEvent((BeCommunicationEvent) event);
									break;
								case BeEventConst.AH_SHUTDOWN_EVENT:
									log.info("stop", "<BE Thread>HiveAP trouble shooting " + getShortName().toLowerCase() + " processor was completely stopped. " + getShortName() + " events lost: " + lostEventCount);
									return;
								default:
									log.warn("start", "Unexpected event received. " + getShortName() + " Event Type: " + eventType);
									break;
							}
						} catch (Exception e) {
							log.error("start", "Exception occurred while handling HiveAP trouble shooting " + getShortName().toLowerCase() + " event.", e);
						} catch (Error e) {
							log.error("start", "Error occurred while handling HiveAP trouble shooting " + getShortName().toLowerCase() + " event.", e);
						}
					}
				}
			};

			eventProc.setName(NmsUtil.getOEMCustomer().getAccessPonitName()+" Trouble Shooting " + getShortName() + " Processor");
			eventProc.start();
			log.info("start", "<BE Thread>Successfully started HiveAP trouble shooting " + getShortName().toLowerCase() + " processor.");
		} else {
			log.info("start", "<BE Thread>HiveAP trouble shooting " + getShortName().toLowerCase() + " processor has already been started.");
		}
	}

	@Override
	public boolean isStarted() {
		return eventProc != null && eventProc.isAlive();
	}

	@Override
	public synchronized void stop() {
		log.info("stop", "<BE Thread>Stopping HiveAP trouble shooting " + getShortName().toLowerCase() + " processor...");

		// Clear queue before adding the shutdown event to stop the event processor immediately.
		if (eventQueue != null && !eventQueue.isEmpty()) {
			eventQueue.clear();
		}

		BeBaseEvent shutdownEvent = new AhShutdownEvent();

		if (eventProc != null && eventProc.isAlive() && eventQueue != null) {
			eventQueue.add(shutdownEvent);
		}
	}

	/*
	 * Add HiveAP debugging event. BlockingQueue by itself is thread safe, but in case offer() fails, we want to
	 * be able to remove the head of the queue and re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	@Override
	public synchronized void add(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();
			log.warn("add", "HiveAP trouble shooting " + getShortName().toLowerCase() + " queue is full, " + lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				log.warn("add", "Discard HiveAP trouble shooting " + getShortName().toLowerCase() + " event.");
			}

			if (!eventQueue.offer(event)) {
				log.warn("add", "HiveAP trouble shooting " + getShortName().toLowerCase() + " queue is full even after removing the head of queue.");
			}
		}
	}

	@Override
	public void notify(BeBaseEvent event) {
		AhAppContainer.getBeEventListener().eventGenerated(event);
	}

	@Override
	public int getEventQueueSize() {
		return eventQueue.size();
	}

	@Override
	public Thread[] getEventProcessThreads() {
		return new Thread[] { eventProc };
	}

	public abstract String getShortName();

}