package com.ah.ha.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.ah.ha.HAStatusMessage;
import com.ah.ha.event.HAEvent;
import com.ah.util.Tracer;
import com.ah.util.coder.AhCodePrinter;

public class HAEventReceiver implements SelectionKeyHandler {

	private static final Tracer log	= new Tracer(HAEventReceiver.class.getSimpleName());

	private static final int MAX_BUFFER_SIZE = 65535;

	private final HAServer haServer;

	private final ByteBuffer recvBuffer;

	public HAEventReceiver(HAServer haServer) {
		this.haServer = haServer;
		recvBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
	}

	/**
	 * Receive HA events from heartbeat.
	 */
	@Override
	public void handle(SelectionKey key) {
		try {
			if (!key.isReadable()) {
				log.warn("handle", "SelectionKey was unreadable. Ignored.");
				return;
			}
		} catch (CancelledKeyException cke) {
			log.error("handle", "SelectionKey was cancelled.", cke);
			return;
		}

		try {
			SocketChannel clientChannel = (SocketChannel) key.channel();
			HAEvent newEvent = parseEvent(clientChannel);

			if (newEvent != null) {
				log.info("handle", "Received a new HA event - " + newEvent);
				haServer.add(newEvent);
			}
		} catch (Exception e) {
			log.error("handle", "Parsing HA event error.", e);
		}
	}

	protected HAEvent parseEvent(SocketChannel clientChannel) {
		HAEvent newEvent = null;

		try {
			int	reads = clientChannel.read(recvBuffer);

			if (reads > 0) {
				HAStatusMessage newMsg = parsePacket();

				if (newMsg != null) {
					newEvent = new HAEvent(clientChannel, newMsg);
				}
			} else {
				try {
					clientChannel.close();
				} catch (IOException ioe) {
					log.error("parseEvent", "IO Close Error.", ioe);
				}
			}
		} catch (Exception e) {
			log.error("parseEvent", "Parsing HA event error.", e);

			if (clientChannel != null) {
				try {
					clientChannel.close();
				} catch (IOException ioe) {
					log.error("parseEvent", "IO Close Error.", ioe);
				}
			}
		} finally {
			recvBuffer.clear();
		}

		return newEvent;
	}

	private HAStatusMessage parsePacket() {
		recvBuffer.flip();

		if (log.getLogger().isDebugEnabled()) {
			String packet = AhCodePrinter.printHexString(recvBuffer.duplicate());
			log.debug("parsePacket", "Received HA event packet:\n" + packet);
		}

		if (recvBuffer.remaining() < HAStatusMessage.MESSAGE_LENGTH) {
			// Event packet is wrong.
			String wrongPacket = AhCodePrinter.printHexString(recvBuffer);
			log.error("parsePacket", "Incorrect HA event packet:\n" + wrongPacket);
			return null;
		}

		int type = recvBuffer.getInt();
		int status = recvBuffer.getInt();
		int response = recvBuffer.getInt();

		return new HAStatusMessage(type, status, response);
	}

}