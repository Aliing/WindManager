package com.ah.ha.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.ha.HAException;
import com.ah.ha.event.HAEvent;
import com.ah.ha.event.HAShutdownEvent;
import com.ah.util.Tracer;

public class HAServer extends Thread implements SelectionKeyHandler {

	private static final Tracer log	= new Tracer(HAServer.class.getSimpleName());

	/* Default HA server port */
	public static final int DEFAULT_HA_SERVER_PORT = 15550;

	/* The max receive buffer size */
	private static final int MAX_SO_RCVBUF = 65535;

	/* The max send buffer size */
	private static final int MAX_SO_SNDBUF = 65535;

	/* The max HA event queue size */
	private static final int MAX_HA_EVENT_QUEUE_SIZE = 1000;

	/* Indicates whether the HA server is running or not */
	private boolean running;

	/* Selector to select ready SelectionKeys */
	private Selector selector;

	/* ServerSocketChannel to accept new connections */
	private ServerSocketChannel serverSocketChannel;

	private final BlockingQueue<HAEvent> eventQueue;

	private final HAEventHandler eventHandler;

	private final AtomicInteger lostEventCount;
	
	public HAServer() {
		eventQueue = new LinkedBlockingQueue<HAEvent>(MAX_HA_EVENT_QUEUE_SIZE);
		eventHandler = new HAEventHandler(eventQueue);
		lostEventCount = new AtomicInteger(0);
	}

	public synchronized void startServer(int port) throws IOException, HAException {
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException("Invalid port number " + port);
		}

		if (isAlive()) {
			if (serverSocketChannel.socket().getLocalPort() == port) {
				throw new HAException("HA server already started on port " + port);
			} else {
				throw new HAException("Could not start HA server on port " + port + " since it has already started on port " + serverSocketChannel.socket().getLocalPort() + ".");
			}
		}

		// Open a Selector.
		selector = Selector.open();

		// Open a ServerSocketChannel.
		serverSocketChannel = ServerSocketChannel.open();

		// The server socket associated with this channel.
		ServerSocket serverSocket = serverSocketChannel.socket();

		// Set receive buffer size.
		serverSocket.setReceiveBufferSize(MAX_SO_RCVBUF);

		InetSocketAddress socketAddress = new InetSocketAddress(port);

		// Bind onto a specified local port.
		serverSocket.bind(socketAddress);

		// Configure the server socket channel in non-blocking mode.
		serverSocketChannel.configureBlocking(false);

		// Register the server socket channel with the selector.
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, this);

		log.info("startServer", "HA server started on port " + port + ".");

		eventQueue.clear();

		running = true;

		// Start HA server thread.
		start();

		// Start HA event handler thread.
		eventHandler.start();
	}

	public synchronized void stopServer() {
		log.info("stopServer", "Stopping HA server.");

		stopEventHandler();

		if (running) {
			running = false;
		}

		// Wait for both the HA event handler and server threads shutdown.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			log.error("stopServer", "Interrupted while waiting for server shutdown.", ie);
		}

		/*-
		if (eventHandler.isAlive()) {
			log.warn("stopServer", "The HA event handler thread is still alive, forcing interrupting.");

			try {
				eventHandler.interrupt();
			} catch (SecurityException se) {
				log.warn("stopServer", "Interrupting HA event handler thread error.", se);
			}
		}

		if (isAlive()) {
			log.warn("stopServer", "The HA server thread is still alive, forcing interrupting.");

			try {
				interrupt();
			} catch (SecurityException se) {
				log.warn("stopServer", "Interrupting HA server thread error.", se);
			}
		}*/
	}

	/**
	 * Process incoming connections and events from HA heartbeat.
	 */
	@Override
	public void run() {
		setName("HA Server");

		try {
			while (running && selector.isOpen() && serverSocketChannel.isOpen()) {
				if (selector.select(1000) > 0) {
					for (Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator(); keyIter.hasNext();) {
						SelectionKey key = keyIter.next();
						keyIter.remove();

						SelectionKeyHandler	keyHandler = (SelectionKeyHandler) key.attachment();
						keyHandler.handle(key);
					}
				}
			}
		} catch (ClosedSelectorException cse) {
			log.error("run", "Selector was closed.", cse);
		} catch (IOException ioe) {
			log.error("run", "IO Error.", ioe);
		} catch (Exception e) {
			log.error("run", "Unexpected Error.", e);
		} finally {
			if (selector != null && selector.isOpen()) {
				try {
					selector.close();
				} catch (IOException e) {
					log.error("run", "IO Close Error.", e);
				}
			}

			if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
				try {
					serverSocketChannel.close();
				} catch (IOException e) {
					log.error("run", "IO Close Error.", e);
				}
			}
		}

		if (!running) {
			log.info("run", "HA server thread was gracefully shutdown.");
		} else {
			log.warn("run", "HA server thread was exceptionally terminated.");
			// HA server thread was exceptionally terminated, should trigger shutdown for event handler.
			stopEventHandler();
		}

		log.info("run", "HA server stopped, events lost: " + lostEventCount.intValue() + ".");
	}

	/**
	 * Process acceptable SelectionKeys.
	 */
	@Override
	public void handle(SelectionKey key) {
		try {
			if (!key.isAcceptable()) {
				log.warn("handle", "SelectionKey was unacceptable. Ignored.");
				return;
			}
		} catch (CancelledKeyException cke) {
			log.error("handle", "SelectionKey was cancelled.", cke);
			return;
		}

		SocketChannel clientChannel = null;

		try {
			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
			clientChannel = serverChannel.accept();

			log.info("handle", "Received a new connection - " + clientChannel.socket());

			// Configure the client socket channel in non-blocking mode.
			clientChannel.configureBlocking(false);

			Socket clientSocket = clientChannel.socket();

			// Set send buffer size.
			clientSocket.setSendBufferSize(MAX_SO_SNDBUF);

			// Enable SO_KEEPALIVE
			clientSocket.setKeepAlive(true);

			// Disable the Nagle's algorithm.
			clientSocket.setTcpNoDelay(true);

			Selector selector = key.selector();

			// Register the client socket channel with the selector.
			clientChannel.register(selector, SelectionKey.OP_READ, new HAEventReceiver(this));
		} catch (Exception e) {
			log.error("handle", "Error occurred while handling a new connection.", e);

			if (clientChannel != null && clientChannel.isOpen()) {
				try {
					clientChannel.close();
				} catch (IOException ioe) {
					log.error("handle", "IO Close Error.", ioe);
				}
			}
		}
	}

	public synchronized void add(HAEvent newEvent) {
		if (!eventQueue.offer(newEvent)) {
			lostEventCount.incrementAndGet();
			log.warn("add", "HA event queue was full, " + lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order to add new event into the FIFO queue.
			HAEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				log.warn("add", "Discarded a HA event - " + newEvent);
			}

			if (!eventQueue.offer(newEvent)) {
				log.warn("add", "HA event queue was full even after removing the head of queue.");
			}
		}
	}

	private void stopEventHandler() {
		if (eventHandler.isAlive()) {
			// Clear queue before adding a shutdown event in order to make a quick shutdown.
			eventQueue.clear();
			add(new HAShutdownEvent());
		}
	}

}
