/**
 *@filename		AhUdpHandler.java
 *@version
 *@author		Long
 *@createtime	2007-8-4 01:15:06 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

// java import
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

// aerohive import
import com.ah.be.capwap.event.request.client.AhCapwapClientRequest;
import com.ah.util.Tracer;

/**
 * @author Long
 * @version V1.0.0.0
 */
class AhUdpHandler implements AhCapwapConstants {

	private static final long serialVersionUID = 1L;
	private static final Tracer logger = new Tracer(AhUdpHandler.class
			.getSimpleName());
	private static final int MAX_PACKET_SIZE = 65535;
	private static final int MAX_BUFFER_SIZE = 65535;

	private final DatagramChannel channel;
	private final Selector selector;
	private final ByteBuffer buffer;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param port
	 *            The port number to be used for listening.
	 * @throws IOException
	 *             if any problem occurs in opening udp socket.
	 */
	protected AhUdpHandler(int port) throws IOException {
		buffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
		selector = Selector.open();
		channel = DatagramChannel.open();
		channel.configureBlocking(false);// use non-block mode.
		channel.register(selector, SelectionKey.OP_READ);
		DatagramSocket socket = channel.socket();
		socket.setSendBufferSize(MAX_PACKET_SIZE);
		socket.setReceiveBufferSize(MAX_PACKET_SIZE);
		SocketAddress addr = new InetSocketAddress(port);
		socket.bind(addr);
		logger.info("AhUdpHandler", "Start up udp socket successfully");
	}

	protected AhCapwapClientRequest receive() throws IOException {
		AhCapwapClientRequest clientReq = null;

		try {
			if (selector.select(5000) > 0) {
				for (Iterator<SelectionKey> iter = selector.selectedKeys()
						.iterator(); iter.hasNext();) {
					SelectionKey key = iter.next();
					DatagramChannel dc = (DatagramChannel) key.channel();
					SocketAddress socket = dc.receive(buffer);

					if (socket != null) {
						buffer.flip();
						ByteBuffer packet = ByteBuffer.allocate(buffer.limit());
						packet.put(buffer);
						packet.flip();
						clientReq = new AhCapwapClientRequest(
								AhCapwapClientRequest.RequestType.CLIENT_REQUEST,
								socket, packet);
					}

					iter.remove();
				}
			}
		} finally {
			buffer.clear();
		}

		return clientReq;
	}

	protected void close() throws IOException {
		DatagramSocket socket = channel.socket();
		socket.close();
		channel.close();
		logger.info("close", "Shut down udp socket successfully");
	}

	protected void xmitMsg(ByteBuffer buf, SocketAddress socket)
			throws IOException {
		channel.send(buf, socket);
	}

}