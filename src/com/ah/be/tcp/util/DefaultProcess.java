package com.ah.be.tcp.util;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class DefaultProcess implements  ChannelProcess{
	
	public DefaultProcess(){}
	
	private int m_bufSize;
	
	public DefaultProcess(int bSize)
	{
		m_bufSize= bSize;
	}

	@Override
	public void handleAccept(SelectionKey key) throws TcpException 
	{
		try
		{
		    SocketChannel clntChan = ((ServerSocketChannel)key.channel()).accept();
		    clntChan.configureBlocking(false);
		    clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(m_bufSize));
		}
		catch(Exception ex)
		{
			
		}
	}

	@Override
	public void handleRead(SelectionKey key) throws TcpException 
	{
		try
		{
			SocketChannel clntChan = (SocketChannel)key.channel();
			ByteBuffer buf = (ByteBuffer)key.attachment();
			long bytesRead = clntChan.read(buf);
			if(bytesRead == -1)
			{
				clntChan.close();
			}
			else if(bytesRead > 0 )
			{
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			}
		}
		catch(Exception ex)
		{
			
		}
		
	}

	@Override
	public void handleWrite(SelectionKey key) throws TcpException {
		ByteBuffer buf = (ByteBuffer)key.attachment();
		buf.flip();
		SocketChannel clntChan = (SocketChannel)key.channel();
		
		try
		{
			clntChan.write(buf);
			if(!buf.hasRemaining())
			{
				key.interestOps(SelectionKey.OP_READ);
			}
			
			buf.compact();
		}
		catch(Exception ex)
		{
		  	
		}
	}

}
