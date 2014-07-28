/**
 *@filename		AhCapwapEventMgmtImpl.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.capwap.event.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.capwap.AhCapwapConstants;
import com.ah.be.capwap.AhCapwapFsm;
import com.ah.be.capwap.AhCapwapServer;
import com.ah.be.capwap.event.*;
import com.ah.be.capwap.event.request.server.*;
import com.ah.util.Tracer;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public final class AhCapwapEventMgmtImpl implements AhCapwapConstants, AhCapwapEventMgmt
{

	private static final long serialVersionUID = 1L;
	private static final Tracer logger = new Tracer(AhCapwapEventMgmtImpl.class.getSimpleName());

	private final int REQUEST_QUEUE_SIZE = 20000;
	private final Set<AhCapwapEventListener> listeners = Collections.synchronizedSet(new LinkedHashSet<AhCapwapEventListener>());	
	private final BlockingQueue<AhCapwapEvent> eventQueue = new LinkedBlockingQueue<>(REQUEST_QUEUE_SIZE);
	private final AtomicInteger lostEventCount = new AtomicInteger(0);
	private static AhCapwapEventMgmtImpl instance;
	private Thread eventMgr;
	private AhCapwapServer capwapServer;

	private AhCapwapEventMgmtImpl() {

	}

	public synchronized static AhCapwapEventMgmtImpl getInstance() {
		if (instance == null) {
			instance = new AhCapwapEventMgmtImpl();
		}

		return instance;
	}

	public void setCapwapServer(AhCapwapServer capwapServer) {
		this.capwapServer = capwapServer;
	}

	public void start() {
		if (isStart()) {
			return;
		}

		eventMgr = new Thread() {
			@Override
			public void run() {
				logger.info("start", "Capwap Event Notification thread started.");

				while (true) {
					try {
						// take() method blocks
						AhCapwapEvent event = eventQueue.take();

						if (event instanceof AhCapwapShutdownEvent) {
							logger.info("start", "Shutting down Capwap Event Notification thread gracefully, events lost: " + lostEventCount.intValue());
							break;
						}

						processEvent(event);
					} catch (InterruptedException e) {
						logger.error("start", "take operation interrupted while waiting: ", e);
					}
				}
			}
		};

		eventMgr.setName("CAPWAP Event Processor");
		eventMgr.start();
	}

	public boolean isStart() {
		return eventMgr != null && eventMgr.isAlive();
	}

	public void stop() {
		if (isStart()) {
			// Clear queue before adding the shutdown event to stop the event processor immediately.
			eventQueue.clear();
			addEvent(new AhCapwapShutdownEvent());
		}
	}

	/*
	 * Register capwap event listener.
	 */
	public void register(AhCapwapEventListener listener) {
		if (listener == null) {
			logger.error("register", "Listener is required.");

			return;
		}

		synchronized (listeners) {
			boolean ret = listeners.add(listener);
			logger.debug("register", "Add new Capwap Event Listener " + (ret ? "successfully." : "failed.") + " size = " + listeners.size());
		}
	}

	/*
	 * Unregister capwap event listener.
	 */
	public void unregister(AhCapwapEventListener listener) {
		synchronized (listeners) {
			boolean ret = listeners.remove(listener);
			logger.debug("unregister", "Remove Capwap Event Listener " + (ret ? "successfully." : "failed.") + " size = " + listeners.size());
		}
	}

	/*
	 * Add capwap request event. BlockingQueue by itself is thread safe, but in case
	 * offer() fails, we want to be able to remove the head of the queue and
	 * re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	public synchronized void addEvent(AhCapwapEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();
			logger.error("addEvent", "Capwap request queue is full, " + lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order to add new event into the FIFO queue.
			AhCapwapEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				logger.info("addEvent", "Discarding capwap event, type = " + lostEvent.getType());
			}

			if (!eventQueue.offer(event)) {
				logger.error("addEvent", "Capwap request queue is full even after removing the head of queue.");
			}
		}
	}

	public void notify(AhCapwapEvent event) {
		if (event == null) {
			return;
		}
		
		synchronized (listeners) {
			if (event instanceof AhWtpEventControlRequest) {
				for (AhCapwapEventListener listener : listeners) {
					listener.wtpEventControlResp((AhWtpEventControlRequest)event);
				}
			} else if (event instanceof AhIdpQueryRequest) {
				for (AhCapwapEventListener listener : listeners) {
					listener.idpQueryResp((AhIdpQueryRequest)event);
				}
			} else if (event instanceof AhLayer3RoamingConfigRequest) {
				for (AhCapwapEventListener listener : listeners) {
					listener.l3RoamConfigResp((AhLayer3RoamingConfigRequest)event);
				}
			} else if (event instanceof AhFileDownloadRequest) {
				for (AhCapwapEventListener listener : listeners) {
					listener.fileDownloadResp((AhFileDownloadRequest)event);
				}
			} else if (event instanceof AhIdpReportEvent) {
				for (AhCapwapEventListener listener : listeners) {
					listener.idpReport((AhIdpReportEvent)event);
				}
			} else if (event instanceof AhApTypeChangeEvent) {
				for (AhCapwapEventListener listener : listeners) {
					listener.apTypeChange((AhApTypeChangeEvent)event);
				}
			} else if (event instanceof AhFileDownloadFinishEvent) {
				for (AhCapwapEventListener listener : listeners) {
					listener.fileDownloadFinish((AhFileDownloadFinishEvent)event);
				}
			} else if (event instanceof AhFileDownloadProgressEvent) {
				for (AhCapwapEventListener listener : listeners) {
					listener.fileDownloadProgress((AhFileDownloadProgressEvent)event);
				}
			}
		}
	}

	private void processEvent(AhCapwapEvent event) {
		String serialNum = event.getSerialNum();
		AhCapwapFsm fsm = getFsm(serialNum);

		if (event instanceof AhCapwapServerRequest) {
			AhCapwapServerRequest serverReq = (AhCapwapServerRequest)event;

			if (fsm == null || fsm.getFsmState() != FsmState.RUN) {
				serverReq.setReqRet(AhCapwapServerRequest.AH_CAPWAP_REQUEST_DISCONNECT);
				notify(serverReq);
			} else {
				fsm.sendRequest(serverReq);
			}
		}
	}

	/**
	 * Remove fsm after ap has been removed or only disconnect the communication between AC and WTP.
	 * Do not need do any database operation after removing ap go with removing fsm. Also, some database operations
	 * should been done after removing fsm but not need removing ap while fsm is in RUN state.
	 * <p>
	 * @param serialNum  The serial number of ap used for searching fsm.
	 * @param removeAp  Indicate whether need removing ap going with removing fsm.
	 */
	public void removeFsm(String serialNum, boolean removeAp) {
		AhCapwapFsm fsm = getFsm(serialNum);

		if (fsm == null) {
			return;
		}

		if (removeAp) {
			logger.debug("removeFsm", "Reinitialize FSM for client " + serialNum);
			fsm.initFsm();
		} else {
			logger.debug("removeFsm", "Clear timer of FSM for client " + serialNum);
			fsm.clearTimer();
		}
	}

	private AhCapwapFsm getFsm(String serialNum) {
		if (capwapServer != null) {
			Map<String, AhCapwapFsm> fsmHash = capwapServer.getFsms();

			synchronized (fsmHash) {
				Collection<AhCapwapFsm> fsms = fsmHash.values();

				for (AhCapwapFsm fsm : fsms) {
					if (serialNum.equals(fsm.getSerialNum())) {
						return fsm;
					}
				}
			}
		}

		return null;
	}

}