package com.ah.ha.communication;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAStatusMessage;
import com.ah.ha.HAUtil;
import com.ah.ha.event.HAEvent;
import com.ah.ha.event.HAShutdownEvent;
import com.ah.util.Tracer;
import com.ah.util.coder.AhCodePrinter;

public class HAEventHandler extends Thread {

	private static final Tracer log	= new Tracer(HAEventHandler.class.getSimpleName());

	private final BlockingQueue<HAEvent> eventQueue;

	public HAEventHandler(BlockingQueue<HAEvent> eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public void run() {
		setName("HA Event Handler");
		
		for (;;) {
			try {
				HAEvent newHAEvent = eventQueue.take();

				if (newHAEvent instanceof HAShutdownEvent) {
					log.info("run", "HA event handler thread was gracefully shutdown.");
					break;
				}
				
				handleEvent(newHAEvent);
			} catch (InterruptedException e) {
				log.error("run", "HA event handler thread interrupted.", e);
			}
		}
	}
	
	protected void handleEvent(HAEvent newHAEvent) {
		HAStatusMessage newMsg = newHAEvent.getMessage();
		int msgType = newMsg.getType();

		switch (msgType) {
			case HAStatusMessage.TYPE_QUERY:
				queryCurrentHAStatus(newHAEvent);
				break;
			case HAStatusMessage.TYPE_UPDATE:
				changeHAStatus(newHAEvent);
				break;
			default:
				log.warn("handleEvent", "Could not handle the HA event " + newHAEvent + " that had an unknown message type: " + msgType);
				break;
		}
	}

	private void queryCurrentHAStatus(HAEvent newHAEvent) {
		log.info("queryCurrentHAStatus", "Handling HA status query event - " + newHAEvent);
		HAStatusMessage newHAMsg = newHAEvent.getMessage();
		HAMonitor haMonitor = HAUtil.getHAMonitor();
		ByteBuffer respBuf = ByteBuffer.allocate(HAStatusMessage.MESSAGE_LENGTH);
		respBuf.putInt(newHAMsg.getType());
		respBuf.putInt(haMonitor.getCurrentStatus().getStatus());
		respBuf.putInt(HAStatusMessage.RESPONSE_SUCCESS);
		respBuf.flip();

		if (log.getLogger().isDebugEnabled()) {
			String respPacket = AhCodePrinter.printHexString(respBuf.duplicate());
			log.debug("queryCurrentHAStatus", "HA status query event response packet:\n" + respPacket);
		}

		try {
			SocketChannel clientChannel = newHAEvent.getClientChannel();
			int writes = clientChannel.write(respBuf);
			log.info("queryCurrentHAStatus", "Successfully sent HA status query response. Bytes: " + writes);
		} catch (Exception e) {
			log.error("queryCurrentHAStatus", "Error in sending HA status query response.", e);
		}		
	}

	private void changeHAStatus(HAEvent newHAEvent) {
		log.info("changeHAStatus", "Handling HA status change event - " + newHAEvent);
		HAStatusMessage newHAMsg = newHAEvent.getMessage();
		ByteBuffer respBuf = ByteBuffer.allocate(HAStatusMessage.MESSAGE_LENGTH);
		respBuf.putInt(newHAMsg.getType());
		respBuf.putInt(newHAMsg.getStatus());
		respBuf.putInt(HAStatusMessage.RESPONSE_SUCCESS);
		respBuf.flip();

		if (log.getLogger().isDebugEnabled()) {
			String respPacket = AhCodePrinter.printHexString(respBuf.duplicate());
			log.debug("changeHAStatus", "HA status change event response packet:\n" + respPacket);
		}

		try {
			SocketChannel clientChannel = newHAEvent.getClientChannel();

			if (clientChannel.isOpen()) {
				int writes = clientChannel.write(respBuf);
				log.info("changeHAStatus", "Successfully sent HA status change response. Bytes: " + writes);
			} else {
				log.warn("changeHAStatus", "Socket[" + clientChannel.socket() + "] closed. Ignored sending HA status change response.");
			}
		} catch (Exception e) {
			log.error("changeHAStatus", "Error in sending HA status change response.", e);
		}

		HAMonitor haMonitor = HAUtil.getHAMonitor();
		int currentStatus = haMonitor.getCurrentStatus().getStatus();
		int newStatus = newHAMsg.getStatus();

		if (currentStatus != newStatus) {
			haMonitor.changeStatus(new HAStatus(newStatus));
		} else {
			log.info("changeHAStatus", "The new HA status[" + currentStatus + "] is the same as current. Ignored.");
		}
	}

}