/**
 *@filename		AhCapwapServer.java
 *@version
 *@author		Long
 *@createtime	2007-8-4 11:35:03 AM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.capwap;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.capwap.event.AhCapwapEvent;
import com.ah.be.capwap.event.AhCapwapShutdownEvent;
import com.ah.be.capwap.event.impl.AhCapwapEventMgmtImpl;
import com.ah.be.capwap.event.request.client.AhCapwapClientRequest;
import com.ah.be.capwap.event.request.client.AhCapwapClientRequest.RequestType;
import com.ah.util.Tracer;

/**
 * @author Long
 * @version V1.0.0.0
 */
public class AhCapwapServer extends Thread {

	private static final Tracer logger = new Tracer(AhCapwapServer.class
			.getSimpleName());

	private static final int DEFAULT_LISTENING_PORT = 12222;

	private static final int MAX_QUEUE_SIZE = 20000;

	protected static final int TIMEOUT_EVENT_INTERVAL = 1000;

	// true if receiving capwap request is permitted, otherwise denied.
	private boolean listening;

	// true if processing capwap request is permitted, otherwise denied.
	private boolean action;

	private final int threadNum;

	private int port = DEFAULT_LISTENING_PORT;

	private ScheduledExecutorService scheduler;

	private AhClientRequestProcessor[] processors;

	protected final Map<String, AhCapwapFsm> fsmHash = Collections
			.synchronizedMap(new LinkedHashMap<String, AhCapwapFsm>());

	protected final BlockingQueue<AhCapwapEvent> clientReqQue = new LinkedBlockingQueue<>(
			MAX_QUEUE_SIZE);

	protected AhUdpHandler udpHandler;

	public AhCapwapServer(int threadNum, int port) throws IOException {
		this.threadNum = threadNum;

		if (port > 0 && port < 65536) {
			this.port = port;
		} else {
			logger.error("AhCapwapServer", "Invalid port number " + port
					+ ", using default listening port "
					+ DEFAULT_LISTENING_PORT + " instead.");
			this.port = DEFAULT_LISTENING_PORT;
		}

		init();
		startClientReqProcThreads();
		startTimeoutWatcher();
	}

	public AhCapwapServer(int threadNum) throws IOException {
		this(threadNum, DEFAULT_LISTENING_PORT);
	}

	/**
	 * @throws IOException
	 *             if any problems occurs in opening udp socket.
	 */
	private void init() throws IOException {
		// Udp socket.
		udpHandler = new AhUdpHandler(port);

		// Start capwap request processors.
		processors = new AhClientRequestProcessor[threadNum];

		// Start capwap event processor.
		AhCapwapEventMgmtImpl eventMgmt = AhCapwapEventMgmtImpl.getInstance();
		eventMgmt.setCapwapServer(this);
		eventMgmt.start();

		// Set receiving capwap request permitted.
		listening = true;

		// Set processing capwap request permitted.
		action = true;
	}

	public Map<String, AhCapwapFsm> getFsms() {
		return fsmHash;
	}

	/**
	 * Start capwap request processing threads.
	 */
	private void startClientReqProcThreads() {
		for (int i = 0; i < processors.length; i++) {
			processors[i] = new AhClientRequestProcessor(this);
			processors[i].setName("CAPWAP Client Request Processor" + (i + 1));
			processors[i].start();
		}
	}

	/**
	 * Stop capwap client request processing threads.
	 */
	private void stopClientReqProcThreads() {
		// Clear all the requests present in the capwap request queue.
		clientReqQue.clear();

		// Add the same number shutdown requests as that of
		// AhClientRequestProcessor's
		// into the capwap client request queue to stop capwap request
		// processing threads.
		AhCapwapShutdownEvent shutdownReq = new AhCapwapShutdownEvent();

		for (int i = 0; i < threadNum; i++) {
			clientReqQue.offer(shutdownReq);
		}
	}

	/**
	 * Start timeout watcher task to generate timeout event into capwap queue
	 * periodically.
	 */
	private void startTimeoutWatcher() {
		Runnable timeoutWatcher = new Runnable() {
			final AhCapwapClientRequest timeoutRequest = new AhCapwapClientRequest(RequestType.TIMEOUT_REQUEST);

			@Override
			public void run() {
				Thread.currentThread().setName("CAPWAP Timeout Event Watcher");
				clientReqQue.offer(timeoutRequest);
			}
		};

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(timeoutWatcher, TIMEOUT_EVENT_INTERVAL,
				TIMEOUT_EVENT_INTERVAL, TimeUnit.MILLISECONDS);
	}

	/**
	 * Shut down capwap server.
	 * <p>
	 * 
	 * @throws IOException
	 *             if any problem occurs in closing udp socket.
	 */
	public void shutdown() throws IOException {
		scheduler.shutdown();
		AhCapwapEventMgmtImpl.getInstance().stop();
		
		// Exit dead loop to stop receiving any capwap client request anymore.
		listening = false;
		udpHandler.close();
	}

	public synchronized void pauseRecvClientReq() {
		logger.debug("pauseRecvClientReq",
				"Pause receiving capwap client request");
		action = false;
	}

	public synchronized void resumeRecvClientReq() {
		logger.debug("resumeRecvClientReq",
				"Resume receiving capwap client request");
		action = true;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("CAPWAP Server");

		while (listening) {
			try {
				AhCapwapClientRequest clientReq = udpHandler.receive();

				if (clientReq != null && action) {
					if (clientReqQue.remainingCapacity() > 0) {
						// Add capwap request into client request queue.
						boolean ret = clientReqQue.offer(clientReq);

						if (!ret) {
							logger.error("run", "Discarding capwap client request since the capwap client request queue is full");
						}
					} else {
						logger
								.error(
										"run",
										"Discarding capwap client request since the capwap client request queue is full");
					}
				}
			} catch (Exception e) {
				logger
						.error("run", "Receiving capwap client request failed",
								e);
			}
		}

		stopClientReqProcThreads();
	}

}