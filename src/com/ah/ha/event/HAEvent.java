package com.ah.ha.event;

import java.nio.channels.SocketChannel;

import com.ah.ha.HAStatusMessage;

public class HAEvent {

	protected SocketChannel clientChannel;

	protected HAStatusMessage message;

	public HAEvent () {

	}

	public HAEvent(SocketChannel clientChannel, HAStatusMessage message) {
		this.clientChannel = clientChannel;
		this.message = message;
	}

	public SocketChannel getClientChannel() {
		return clientChannel;
	}

	public void setClientChannel(SocketChannel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public HAStatusMessage getMessage() {
		return message;
	}

	public void setMessage(HAStatusMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Client: " + (clientChannel != null ? clientChannel.socket() : null) + "; " + message;
	}

}