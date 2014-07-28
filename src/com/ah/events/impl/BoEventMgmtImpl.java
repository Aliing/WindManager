package com.ah.events.impl;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.events.BoEvent;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.BoEventMgmt;
import com.ah.events.BoEvent.BoEventType;
import com.ah.ha.HAUtil;
import com.ah.bo.HmBo;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public final class BoEventMgmtImpl implements BoEventMgmt {

	private static final Tracer log = new Tracer(BoEventMgmtImpl.class
			.getSimpleName());

	private static final int REQUEST_QUEUE_SIZE = 20000;

	public synchronized static BoEventMgmtImpl getInstance() {
		if (instance == null) {
			instance = new BoEventMgmtImpl();
		}

		return instance;
	}

	private BoEventMgmtImpl() {
	}

	private static BoEventMgmtImpl instance;

	/*
	 * Register event listener
	 */
	@Override
	public <T extends HmBo> void addBoEventListener(BoEventListener<T> listener,
			BoEventFilter<T> filter) {
		if (listener == null || filter == null || filter.getBoClass() == null) {
			log.error("addBoEventListener",
					"Listener and class filter is required.");
			return;
		}
		classListeners.putIfAbsent(filter.getBoClass(),
				new Vector<BoEventListener<? extends HmBo>>());
		Collection<BoEventListener<? extends HmBo>> listeners = classListeners.get(filter
				.getBoClass());
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/*
	 * Unregister event listener
	 */
	@Override
	public <T extends HmBo> void removeBoEventListener(BoEventListener<T> listener) {
		for (Collection<BoEventListener<? extends HmBo>> listeners : classListeners.values()) {
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}
	}

	/*
	 * Publish BoEvent. BlockingQueue by itself is thread safe, but in case
	 * offer() fails, we want to be able to remove the head of the queue and
	 * re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	@Override
	public synchronized <T extends HmBo> void publishBoEvent(BoEvent<T> event) {
		Collection<BoEventListener<? extends HmBo>> listeners = classListeners.get(event
				.getSource().getClass());
		if (listeners == null && !(event instanceof ShutdownBoEvent)) {
			// No one is interested in this event
			return;
		}
		if (boEventQueue.offer(event)) {
			if (boEventQueue.size() > highQueueSize.get()) {
				highQueueSize.set(boEventQueue.size());
				if (highQueueSize.get() % 1000 == 0) {
					log.info("publishBoEvent", "Event queue size reached: "
							+ highQueueSize.get());
				}
			}
		} else {
			lostEventCount.incrementAndGet();
			if (lostEventCount.intValue() % 100 == 0) {
				log.error("publishBoEvent", "Request queue full, "
						+ lostEventCount.intValue() + " events lost so far.");
			}
			// New event is more important, so remove head of queue and add new
			// event into the FIFO queue
			BoEvent<? extends HmBo> lostEvent = boEventQueue.poll();
			if (lostEvent != null) {
				log.debug("publishBoEvent", "Discarding event: "
						+ lostEvent.getSource().getLabel());
			}
			if (!boEventQueue.offer(event)) {
				log
						.error("publishBoEvent",
								"Request queue full even after removing head of queue ???");
			}
		}
	}

	private final ConcurrentMap<Class<? extends HmBo>, Collection<BoEventListener<? extends HmBo>>> classListeners = new ConcurrentHashMap<Class<? extends HmBo>, Collection<BoEventListener<? extends HmBo>>>();

	private final BlockingQueue<BoEvent<? extends HmBo>> boEventQueue = new LinkedBlockingQueue<BoEvent<? extends HmBo>>(
			REQUEST_QUEUE_SIZE);

	private final AtomicInteger lostEventCount = new AtomicInteger(0);

	private final AtomicInteger highQueueSize = new AtomicInteger(0);

	private Thread eventMgr;

	@Override
	public synchronized void stop() {
		// Clean queue up to trigger a quick shutdown.
		boEventQueue.clear();
		publishBoEvent(new ShutdownBoEvent());
	}

	@Override
	public synchronized void start() {
		if (eventMgr != null) {
			return;
		}
		eventMgr = new Thread() {
			@Override
			public void run() {
				log.info("run", "BO Event Notification thread started.");
				try {
					while (true) {
						// take() method blocks
						BoEvent<? extends HmBo> event = boEventQueue.take();
						if (event instanceof ShutdownBoEvent) {
							log.info("run",
									"Shutting down BO Event Notification thread gracefully, high queue size: "
											+ highQueueSize.intValue()
											+ ", events lost: "
											+ lostEventCount.intValue() + ".");
							break;
						} else {
							if (boEventQueue.size() % 1000 == 0) {
								log.info("run",
										"Event queue size down to: "
												+ boEventQueue.size());
							}
							notifyListeners(event);
						}
					}
				} catch (InterruptedException e) {
					log.error("run",
							"take operation interrupted while waiting: ", e);
				}
			}
		};
		eventMgr.setName("BO Event Notifier");
		eventMgr.start();
	}

	@Override
	public <T extends HmBo> void notifyListeners(BoEvent<T> event) {
		boolean isHAPassiveMode = HAUtil.isSlave();
		if (isHAPassiveMode) {
			log.info("run in HA passive mode, ignore notify.");
			return;
		}
		Collection<BoEventListener<? extends HmBo>> listeners = classListeners.get(event
				.getSource().getClass());
		if (listeners == null) {
			return;
		}
		synchronized (listeners) {
			for (BoEventListener<? extends HmBo> listener : listeners) {
				try {
					BoEventListener<T> specificListener = (BoEventListener<T>) listener;
					if (BoEventType.CREATED.equals(event.getType())) {
						specificListener.boCreated(event.getSource());
					} else if (BoEventType.UPDATED.equals(event.getType())) {
						specificListener.boUpdated(event.getSource());
					} else if (BoEventType.REMOVED.equals(event.getType())) {
						specificListener.boRemoved(event.getSource());
					}
				} catch (Exception e) {
					log.error("notifyListeners", "Notification failed for BO: "
							+ event.getSource().getLabel(), e);
				}
			}
		}
	}

	protected void logClassListeners() {
		for (Class<? extends HmBo> listenerClass : classListeners.keySet()) {
			Collection<BoEventListener<? extends HmBo>> classListener = classListeners
					.get(listenerClass);
			log.debug("logClassListeners", "# listeners for class: "
					+ listenerClass.getSimpleName() + " is: "
					+ classListener.size());
		}
	}

}