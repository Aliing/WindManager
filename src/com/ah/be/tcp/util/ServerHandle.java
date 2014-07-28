package com.ah.be.tcp.util;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;


public class ServerHandle {

	//(milliseconds)
	private int                                     m_timeout = 5000;
	private InetSocketAddress	m_address			= null;
	private ServerSocketChannel m_listenChannel     = null;
	private Selector            m_selector          = null;
	private ChannelProcess      m_process           = null;
	private boolean             m_active_flag       = true;
	
	public ServerHandle(int iPort)
	{
		m_address = new InetSocketAddress(iPort);
	}
	
	public InetSocketAddress getAddress()
	{
		return m_address;
	}
	
	public void setProcess(ChannelProcess process)
	{
		m_process = process;
	}
	
	public void init(ChannelProcess process) throws TcpException
	{
		m_process = process;
		
		try
		{
		    m_listenChannel = ServerSocketChannel.open();
		    m_listenChannel.socket().bind(m_address);
		    m_listenChannel.configureBlocking(false);
		  
		    m_selector = Selector.open();
		    //regist selector
		    m_listenChannel.register(m_selector, SelectionKey.OP_ACCEPT);
		}    
		catch(Exception ex)
		{
			uninit();
			throw new TcpException("Fail to init tcp handle: "
					+ ex.getMessage());
		}		
	}
	
	public void uninit() throws TcpException
	{
		try
		{
			m_active_flag = false;
			
			if(null != m_listenChannel)
			{
				m_listenChannel.close();
				m_listenChannel = null;
			}
			
			if(null != m_selector)
			{
				m_selector.close();
				m_selector = null;
			}
		}
		catch(Exception ex)
		{
			//add log
			throw new TcpException("Fail to init tcp handle: "
					+ ex.getMessage());
		}
		
	}
	
	public void setActiveFlag(boolean bFlag)
	{
		m_active_flag = bFlag;
	}
	
	public boolean getActiveFlag()
	{
		return m_active_flag;
	}
	
	public void handle() throws TcpException
	{
		try
		{
			if(null == m_process)
			{
				m_process = new DefaultProcess();
			}
			
			while(m_active_flag)
			{
				if(m_selector.select(m_timeout) == 0)
				{
					//add log
				    Thread.sleep(200);
					continue;
				}
				
				Iterator<SelectionKey> keyIter = m_selector.selectedKeys().iterator();
				
				while(keyIter.hasNext())
				{
					SelectionKey key = keyIter.next();
					keyIter.remove();
					
					//server socket channel panding connect request
					if(key.isAcceptable())
					{
						try
						{
							m_process.handleAccept(key);	
						}
						catch(Exception ex1)
						{
							ex1.printStackTrace();
						}
					}
					
					//client socket channel pending data
					if(key.isReadable())
					{
						try
						{
							m_process.handleRead(key);
						}
						catch(Exception ex2)
						{
							ex2.printStackTrace();
						}
					}
					
					//client socket channel is available for writing and key is valid
					if(key.isValid() && key.isWritable())
					{
						try
						{
							m_process.handleWrite(key);	
						}
						catch(Exception ex3)
						{
							ex3.printStackTrace();
						}
					}				
				}
				
			}
		}
		catch(Exception ex)
		{
			uninit();
			throw new TcpException("Fail to handle tcp: "
					+ ex.getMessage());
		}
		
	}
	
}
