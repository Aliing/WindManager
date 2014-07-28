package com.ah.be.debug.tcpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.debug.BeDebugModuleImpl;

/**
 * 
 *@filename		DefaultTcpChannelProcess.java
 *@version		V1.0.0.0
 *@author		xiaxiaoyin & juyizhou
 *@createtime	2008-1-8 02:38:58
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public class DefaultTcpChannelProcess implements ITcpChannelProcess
{

	private static final int			BUFFER_SIZE		= 65535;
	private final List<SocketChannel>	m_list_Channel;
	private final ByteBuffer			m_byteBuffer;
	private String						m_string_Buffer;
	private final CLIParserInterface	parser;
	private BeDebugModuleImpl			debugModuleImpl;

	/**
	 * cache debug console channel
	 */
	private final List<SocketChannel>	debugChannelList;

	/**
	 * get debug module
	 *
	 * @return -
	 */
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
	 */
	public DefaultTcpChannelProcess()
	{
		m_list_Channel = new LinkedList<SocketChannel>();
		debugChannelList = new LinkedList<SocketChannel>();
		m_byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		m_string_Buffer = "";
		parser = new DefaultCLIParser(this);
	}

	/**
	 * add tcp connection
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void addConnection(SocketChannel arg_Channel)
	{
		Socket clientSocket;

		synchronized (m_list_Channel)
		{
			if (m_list_Channel.contains(arg_Channel))
				m_list_Channel.remove(arg_Channel);
			m_list_Channel.add(arg_Channel);

			clientSocket = arg_Channel.socket();

			try {
				clientSocket.setKeepAlive(true);
			} catch (SocketException se) {
				DebugUtil.commonDebugWarn("Error occurred while setting the keepalive option for an incoming TCP socket.", se);
			}

			showWelcomeMsg(arg_Channel);
			sendToClient(arg_Channel, ">");
		}
		System.out.println("accept a connection from "
			+ clientSocket.getInetAddress().getHostName());
	}
	
	/**
	 * show welcome message
	 * 
	 * @param clientChannel -
	 */
	private void showWelcomeMsg(SocketChannel clientChannel)
	{
		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String version = versionInfo.getMainVersion() + "r"
			+ versionInfo.getSubVersion();
		String release = versionInfo.getStatus() + "  "
			+ versionInfo.getBuildTime();

		responseMessage(clientChannel, "++++++++++++++++++++++++++++++++++");
		responseMessage(clientChannel, "| Welcome to HiveManager v" + version
			+ "  |");
		responseMessage(clientChannel, "| " + release + "   |");
		responseMessage(clientChannel, "|        Send 'Quit' to exit     |");
		responseMessage(clientChannel, "++++++++++++++++++++++++++++++++++");
	}
	
	/**
	 * send message to given client channel
	 * 
	 * @param clientChannel -
	 * @param message -
	 */
	private void responseMessage(SocketChannel clientChannel, String message)
	{
		sendToClient(clientChannel, message);
		sendToClient(clientChannel, "\r\n");
	}

	/**
	 * remove tcp connection
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void removeConnection(SocketChannel arg_Channel)
	{
		if (null == arg_Channel)
		{
			return;
		}

		synchronized (m_list_Channel)
		{
			m_list_Channel.remove(arg_Channel);
		}
		synchronized (debugChannelList)
		{
			debugChannelList.remove(arg_Channel);
		}
		closeConnection(arg_Channel);
	}

	/**
	 * remove all tcp client connection.
	 */
	public void removeAllClientConnection()
	{
		synchronized (m_list_Channel)
		{
			for (Iterator<SocketChannel> iter = m_list_Channel.iterator(); iter.hasNext();)
			{
				SocketChannel socketChannel = iter.next();
				iter.remove();
				closeConnection(socketChannel);
			}
		}
		
		synchronized (debugChannelList)
		{
			for (Iterator<SocketChannel> iter = debugChannelList.iterator(); iter.hasNext();)
			{
				SocketChannel socketChannel = iter.next();
				iter.remove();
				closeConnection(socketChannel);
			}
		}
	}
	
	private void closeConnection(SocketChannel arg_Channel)
	{
		System.out.println("remove a connection from "
			+ arg_Channel.socket().getInetAddress().getHostName());
		try
		{
			arg_Channel.close();
		}
		catch (IOException e)
		{
			DebugUtil.commonDebugWarn("DefaultTcpChannelProcess.closeConnection() catch ioexception.", e);
		}
	}

	/**
	 * process tcp channel when data arrival
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void process(SocketChannel arg_Channel)
	{
		if (arg_Channel == null) {
			return;
		}
		
		if (!arg_Channel.isOpen()) {
			removeConnection(arg_Channel);

			return;
		}

		try
		{
			m_byteBuffer.clear();
			int int_ReadBytes = arg_Channel.read(m_byteBuffer);
			if (int_ReadBytes <= 0)
			{
				removeConnection(arg_Channel);
				return;
			}
			m_byteBuffer.flip();
			byte[] byte_Array = new byte[int_ReadBytes];
			m_byteBuffer.get(byte_Array);
			m_string_Buffer += new String(byte_Array);
			int int_Index = m_string_Buffer.indexOf("\n");
			if (0 < int_Index)
			{
				// receive a full cmd and execute
				String string_Cmd = m_string_Buffer.substring(0, int_Index - 1);
				m_string_Buffer = m_string_Buffer.substring(int_Index + 1);
				// debug - send cmd to all client
				// sendToAllClient(string_Cmd.getBytes());

				parser.parseCli(string_Cmd, arg_Channel);
			}
			else
				if (0 == int_Index)
				{
					m_string_Buffer = m_string_Buffer.substring(1);
				}
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn("DefaultTcpChannelProcess.process() catch exception, close this connect.", e);
			removeConnection(arg_Channel);
		}
	}

	/**
	 * send data to all client
	 * 
	 * @param msg
	 *            data be sent
	 */
	public void sendToAllClient(String msg)
	{
		byte[] arg_Buf = msg.getBytes();

		synchronized (m_list_Channel)
		{
			Iterator<SocketChannel> it = m_list_Channel.iterator();
			while (it.hasNext())
			{
				SocketChannel clientChannel = it.next();
				try
				{
					clientChannel.write(ByteBuffer.wrap(arg_Buf));
				}
				catch (IOException e)
				{
					DebugUtil.commonDebugWarn("DefaultTcpChannelProcess.sendToAllClient() catch exception, close this connect.", e);
					closeConnection(clientChannel);
					it.remove();
				}
			}
		}
	}

	/**
	 * send data to client channel
	 * 
	 * @param clientChannel -
	 * @param msg -
	 */
	public void sendToClient(SocketChannel clientChannel, String msg)
	{
		byte[] arg_Buf = msg.getBytes();

		try
		{
			clientChannel.write(ByteBuffer.wrap(arg_Buf));
		}
		catch (IOException e)
		{
			DebugUtil.commonDebugWarn("DefaultTcpChannelProcess.sendToClient() catch exception, close this connect.", e);
			closeConnection(clientChannel);
		}
	}

	/**
	 * send message to all debug console
	 * 
	 * @param msg -
	 */
	public void sendToAllDebugConsole(String msg)
	{
		byte[] arg_Buf = msg.getBytes();

		synchronized (debugChannelList)
		{
			for (Iterator<SocketChannel> it = debugChannelList.iterator(); it.hasNext(); )
			{
				SocketChannel clientChannel = it.next();

				try
				{
					clientChannel.write(ByteBuffer.wrap(arg_Buf));
				}
				catch (IOException e)
				{
					DebugUtil.commonDebugWarn("DefaultTcpChannelProcess.sendToAllDebugConsole() catch exception, close this connect.", e);
					closeConnection(clientChannel);
					it.remove();
				}
			}
		}
	}
	
	/**
	 * cache arg_Channel as a debug console
	 *
	 * @param arg_Channel -
	 */
	public void addDebugConsole(SocketChannel arg_Channel)
	{
		synchronized (debugChannelList)
		{
			InetAddress newIP = arg_Channel.socket().getInetAddress();
			for (SocketChannel channel: debugChannelList)
			{
				if (channel.socket().getInetAddress().equals(newIP))
				{
					return;
				}
			}
			
			debugChannelList.add(arg_Channel);
		}
	}
	
	/**
	 * remove arg_Channel from cache
	 *
	 * @param arg_Channel -
	 */
	public void removeDebugConsole(SocketChannel arg_Channel)
	{
		synchronized (debugChannelList)
		{
			debugChannelList.remove(arg_Channel);
		}
	}

}