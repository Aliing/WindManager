package com.ah.be.tcp.util;

import java.nio.channels.SelectionKey;

public interface ChannelProcess {
	
	//accept
	public void handleAccept(SelectionKey key) throws TcpException;
	//read
    public void handleRead(SelectionKey key) throws TcpException;
	//write
    public void handleWrite(SelectionKey key) throws TcpException;
}
