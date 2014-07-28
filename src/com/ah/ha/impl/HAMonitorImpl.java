/**
 * @filename			HAMonitorImpl.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.ah.ha.HAException;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAStatusListener;
import com.ah.ha.communication.HAServer;
import com.ah.ha.event.HAStatusChangedEvent;
import com.ah.util.Tracer;

public class HAMonitorImpl implements HAMonitor {
	
	private static final Tracer log = new Tracer(HAMonitorImpl.class.getSimpleName());

	private static HAMonitorImpl instance;

	private final Collection<HAStatusListener> listeners;

	private final HAServer haServer;

	private HAStatus currentStatus;

	private boolean suspending;
	
	private HAMonitorImpl() {
		listeners = Collections.synchronizedCollection(new LinkedList<HAStatusListener>());
		haServer = new HAServer();
		currentStatus = new HAStatus(HAStatus.STATUS_UNKNOWN);
	}
	
	public static synchronized HAMonitorImpl getInstance() {
		if (instance == null) {
			instance = new HAMonitorImpl();
		}
		
		return instance;
	}

	@Override
	public synchronized void start() throws HAException {
		try {
			haServer.startServer(HAServer.DEFAULT_HA_SERVER_PORT);
		} catch (Exception e) {
			throw new HAException(e);
		}
	}

	@Override
	public synchronized void stop() throws HAException {
		try {
			haServer.stopServer();
		} catch (Exception e) {
			throw new HAException(e);
		}

		// Clear listener list.
		listeners.clear();
	}

	@Override
	public boolean isSuspending() {
		return suspending;
	}

	@Override
	public void setSuspending(boolean suspending) {
		this.suspending = suspending;
	}

	@Override
	public HAStatus getCurrentStatus() {
		return currentStatus;
	}

	@Override
	public synchronized void changeStatus(HAStatus newStatus) {
		if (newStatus == null) {
			throw new IllegalArgumentException("Argument must not be null.");
		}

		if (newStatus.equals(currentStatus)) {
			log.warn("changeStatus", "The new status (" + newStatus.getStatus() + ") was equivalent to the old status. Status change ignored.");
			return;
		}

		boolean statusChanged = true;
		HAStatusChangedEvent statusChangedEvent = new HAStatusChangedEvent(newStatus, currentStatus);

		switch (newStatus.getStatus()) {
			// Just "Standalone", "Master" and "Slave" are useful in any other places except for HA monitor.
			case HAStatus.STATUS_STAND_ALONG:
				if (currentStatus.getStatus() != HAStatus.STATUS_HA_SLAVE) {
					currentStatus = newStatus;
				} else {
					log.info("changeStatus", "Ignored changing HA status from '" + HAStatus.convert(statusChangedEvent.getOldStatus().getStatus()) + "' to '" + HAStatus.convert(newStatus.getStatus()) + "' since shutting down HiveManager in HA passive mode is even desired.");
					statusChanged = false;
				}
				break;
			case HAStatus.STATUS_HA_MASTER:
			case HAStatus.STATUS_HA_SLAVE:
				if (suspending) {
					log.warn("changeStatus", "HA monitor was in suspending, status change ignored. New Status: " + newStatus + "; Old Status: " + currentStatus);
					return;
				} else {
					currentStatus = newStatus;
				}
				break;
			default:
				break;
		}

		notifyListeners(statusChangedEvent);

		if (statusChanged) {
			log.info("changeStatus", "Changed HA status from '" + HAStatus.convert(statusChangedEvent.getOldStatus().getStatus()) + "' to '" + HAStatus.convert(newStatus.getStatus()) + "'.");
		}
	}

	@Override
	public synchronized boolean addListener(HAStatusListener newListener) {
		boolean added = false;

		if (!listeners.contains(newListener)) {
			added = listeners.add(newListener);
		}
		
		return added;
	}

	@Override
	public synchronized boolean removeListener(HAStatusListener oldListener) {
		return listeners.remove(oldListener);
	}

	@Override
	public synchronized void notifyListeners(HAStatusChangedEvent event) {
	//	synchronized (listeners) {
			for (HAStatusListener listener : listeners) {
				try {
					listener.statusChanged(event);
				} catch (Exception e) {
					log.error("notifyListeners", "Notification failed for " + event, e);
				}
			}
	//	}
	}

//	@Override
//	public String toString() {
//		return "HA Status: " + currentStatus + "; Suspending: " + suspending;
//	}

}