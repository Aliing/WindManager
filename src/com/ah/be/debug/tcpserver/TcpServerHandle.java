package com.ah.be.debug.tcpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.ah.be.app.DebugUtil;
import com.ah.be.debug.BeDebugModuleImpl;

/**
 * 
 *@filename		TcpServerHandle.java
 *@version		V1.0.0.0
 *@author		xiaxiaoyin & juyizhou
 *@createtime	2008-1-8 02:39:38
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class TcpServerHandle extends Thread
{

	private InetSocketAddress	m_address			= null;

	ServerSocketChannel			m_serverChannel		= null;

	private Selector			m_selector			= null;

	private boolean				m_boolean_Running	= true;

	private int					m_int_timeout		= 5000;

	ITcpChannelProcess			m_tcpChannelProcess	= null;

	private BeDebugModuleImpl	debugModuleImpl;

	public BeDebugModuleImpl getDebugModuleImpl()
	{
		return debugModuleImpl;
	}

	public void setDebugModuleImpl(BeDebugModuleImpl debugModuleImpl)
	{
		this.debugModuleImpl = debugModuleImpl;
	}

	/**
	 * Construct method
	 * 
	 * @param arg_Port
	 *            listen port
	 * @param arg_Process -
	 * @throws TcpChannelException -
	 */
	public TcpServerHandle(int arg_Port, ITcpChannelProcess arg_Process)
																		throws TcpChannelException
	{
		m_address = new InetSocketAddress(arg_Port);
		m_tcpChannelProcess = arg_Process;
		init();
	}

	private void init() throws TcpChannelException
	{
		try
		{
			// open server channel and bind and set not block
			m_serverChannel = ServerSocketChannel.open();
			m_serverChannel.socket().bind(m_address);
			m_serverChannel.configureBlocking(false);
			// register to selector
			m_selector = Selector.open();
			m_serverChannel.register(m_selector, SelectionKey.OP_ACCEPT);
			System.out.println("Init tcp server on host "
				+ m_address.getHostName() + "and port:" + m_address.getPort());
		}
		catch (Exception e)
		{
			stopHandle();
			throw new TcpChannelException("Fail to init tcp handle:"
				+ e.getMessage());
		}
	}

	public InetSocketAddress getAddress()
	{
		return m_address;
	}

	/**
	 * stop tcp server handle
	 */
	public void stopHandle()
	{
		try
		{
			this.interrupt();
			m_boolean_Running = false;
			
			//close client connect first
			m_tcpChannelProcess.removeAllClientConnection();
			
			//close server socket
			if (m_serverChannel != null)
			{
				m_serverChannel.close();
				m_serverChannel = null;
			}
			if (m_selector != null)
			{
				m_selector.close();
				m_selector = null;
			}
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"TcpServerHandle.stopHandle(): catch exception.", e);
			DebugUtil
				.commonDebugError("Failed to close tcp socket for debug console, port is still possessed in os. If debug console failed to connect to HM when process restart, you can reboot HiveManager station or configure another socket port on configuration file.");
		}
	}

	/**
	 * set timeout interval
	 * 
	 * @param arg_Timeout
	 *            interval of ms
	 */
	public void setTimeout(int arg_Timeout)
	{
		this.m_int_timeout = arg_Timeout;
	}

	private void addConnection(SocketChannel arg_Channel)
	{
		try
		{
			arg_Channel.configureBlocking(false);
			arg_Channel.register(m_selector, SelectionKey.OP_READ);
			if (m_tcpChannelProcess != null)
				m_tcpChannelProcess.addConnection(arg_Channel);
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"TcpServerHandle.addConnection() Fail to accept connection", e);
			try
			{
				arg_Channel.close();
			}
			catch (IOException e1)
			{
				DebugUtil
					.commonDebugWarn(
						"TcpServerHandle.addConnection() Fail to close socketChannel",
						e);
			}
		}
	}

	private void process(SocketChannel arg_Channel)
	{
		try
		{
			if (m_tcpChannelProcess != null)
				m_tcpChannelProcess.process(arg_Channel);
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"TcpServerHandle.process() Fail to process connection", e);
		}
	}

	@Override
	public void run()
	{
		Iterator<SelectionKey> it;
		SelectionKey sk;
		ServerSocketChannel serverChannel;
		SocketChannel clientChannel;
		DebugUtil.commonDebugInfo("tcp server handle thead begin...");
		while (m_boolean_Running)
		{
			try
			{
				if (m_selector.select(m_int_timeout) > 0)
				{
					// Get an iterator over the set of selected keys
					it = m_selector.selectedKeys().iterator();
					// will be exactly one key in the set
					while (it.hasNext())
					{
						sk = it.next();
						if (sk.isAcceptable())
						{
							// have a new connection
							serverChannel = (ServerSocketChannel) sk.channel();
							clientChannel = serverChannel.accept();
							addConnection(clientChannel);
						}
						if (sk.isReadable())
						{
							clientChannel = (SocketChannel) sk.channel();
							process(clientChannel);
						}
						it.remove();
					}
				}
			}
			catch (Exception e)
			{
				DebugUtil.commonDebugWarn(
					"TcpServerHandle.run() Error in tcp server handle thread",
					e);
			}
		}
		DebugUtil.commonDebugInfo("tcp server handle thead exit...");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DefaultTcpChannelProcess process = new DefaultTcpChannelProcess();
			TcpServerHandle serverHandle = new TcpServerHandle(1111, process);
			serverHandle.start();
			// int count = 0;
			// while (true)
			// {
			// count++;
			// Thread.sleep(1000);
			// process.sendToAllClient(String.valueOf(count).getBytes());
			// }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}