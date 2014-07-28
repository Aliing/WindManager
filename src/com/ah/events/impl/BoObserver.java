package com.ah.events.impl;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ah.events.BoEvent;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.BoEvent.BoEventType;
import com.ah.ha.HAUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public final class BoObserver {
	private static final Tracer log = new Tracer(BoObserver.class
			.getSimpleName());

	private BoObserver() {
	}

	/*
	 * Register event listener
	 */
	public static void addBoEventListener(BoEventListener listener,
			BoEventFilter filter) {
		if (listener == null || filter == null || filter.getBoClass() == null) {
			log.error("addBoEventListener",
					"Listener and class filter is required.");
			return;
		}
		classListeners.putIfAbsent(filter.getBoClass(),
				new Vector<BoEventListener>());
		Collection<BoEventListener> listeners = classListeners.get(filter
				.getBoClass());
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/*
	 * Unregister event listener
	 */
	public static void removeBoEventListener(BoEventListener listener) {
		for (Collection<BoEventListener> listeners : classListeners.values()) {
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}
	}

	private static final ConcurrentMap<Class<?>, Collection<BoEventListener>> classListeners = new ConcurrentHashMap<Class<?>, Collection<BoEventListener>>();

	/*
	 * Notify listeners directly, no queue/consumer thread.
	 */

	public synchronized static void notifyListeners(BoEvent event) {
		boolean isHAPassiveMode = HAUtil.isSlave();
		if (isHAPassiveMode) {
			log.info("run in HA passive mode, ignore notify event.");
			return;
		}
		Collection<BoEventListener> listeners = classListeners.get(event
				.getSource().getClass());
		if (listeners == null) {
			return;
		}
		synchronized (listeners) {
			for (BoEventListener listener : listeners) {
				try {
					if (BoEventType.CREATED.equals(event.getType())) {
						listener.boCreated(event.getSource());
					} else if (BoEventType.UPDATED.equals(event.getType())) {
						listener.boUpdated(event.getSource());
					} else if (BoEventType.REMOVED.equals(event.getType())) {
						listener.boRemoved(event.getSource());
					}
				} catch (Exception e) {
					log.error("notifyListeners", "Notification failed for BO: "
							+ event.getSource().getLabel(), e);
				}
			}
		}
	}
}
