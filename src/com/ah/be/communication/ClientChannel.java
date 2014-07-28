/**
 *@filename		ClientChannel.java
 *@version
 *@author		Frank
 *@createtime	2007-10-31
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.Iterator;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.ConfigUtil;

public class ClientChannel
{
	
	private String			m_string_ServerIp	= ConfigUtil.getConfigInfo(
														ConfigUtil.SECTION_COMMUNICATION,
														ConfigUtil.KEY_CAPWAPSERVER, "127.0.0.1");
	private int				m_int_Port			= Integer.valueOf(ConfigUtil.getConfigInfo(
														ConfigUtil.SECTION_COMMUNICATION,
														ConfigUtil.KEY_CAPWAPPORT, "18047"));
	private boolean			m_bool_Connect		= false;

	private int				m_int_ConnTimeout	= 5000;
	private int				m_int_RecvTimeout	= 5000;

	private SocketChannel	m_channel			= null;
	private Selector		m_selector			= null;

	private int				MAX_DATA_SIZE		= 0X80000;// 512K
	private int				BUFFER_SIZE			= 0X4000;//16K
	private int				m_int_ActualLength	= 0;
	private int				m_int_ReadPosition	= 0;
	private ByteBuffer		m_recvByteBuffer;
	private ByteBuffer		m_lengthByteBuffer;

	private SocketChannel	m_channel_Temp		= null;

	/**
	 * set connect time out
	 * 
	 * @param arg_Timeout
	 *            millisecond
	 */
	public void setConnTimeout(int arg_Timeout)
	{
		m_int_ConnTimeout = arg_Timeout;
	}

	/**
	 * set receive time out
	 * 
	 * @param arg_Timeout
	 *            millisecond
	 */
	public void setRecvTimeout(int arg_Timeout)
	{
		m_int_RecvTimeout = arg_Timeout;
	}

	/**
	 * set server ip address
	 *
	 * @param arg_ServerIp -
	 */
	public void setServerIp(String arg_ServerIp)
	{
		m_string_ServerIp = arg_ServerIp;
	}

	/**
	 * get server ip address
	 * 
	 * @return -
	 */
	public String getServerIp()
	{
		return m_string_ServerIp;
	}

	/**
	 * set port
	 *
	 * @param arg_Port -
	 */
	public void setPort(int arg_Port)
	{
		m_int_Port = arg_Port;
	}

	/**
	 * get port
	 * 
	 * @return -
	 */
	public int getPort()
	{
		return m_int_Port;
	}

	/**
	 * get connect state
	 * 
	 * @return boolean
	 */
	public boolean getConnectState()
	{
		return m_bool_Connect;
	}

	public ClientChannel()
	{
		m_recvByteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		m_lengthByteBuffer = ByteBuffer.allocate(4);
	}

    private synchronized int connect()
	{
		try
		{
			m_channel = SocketChannel.open();
			m_channel.configureBlocking(false);
			m_channel.connect(new java.net.InetSocketAddress(m_string_ServerIp, m_int_Port));
			m_selector = Selector.open();
			m_channel.register(m_selector, SelectionKey.OP_READ
				| SelectionKey.OP_CONNECT);

			if (m_selector.select(m_int_ConnTimeout) > 0)
			{
				for (Iterator<SelectionKey> iter = m_selector.selectedKeys().iterator(); iter.hasNext();)
				{
					SelectionKey key = iter.next();
					iter.remove();
					m_channel_Temp = (SocketChannel) key.channel();
					if (key.isConnectable())
					{
						if (m_channel_Temp.isConnectionPending())
						{
							m_channel_Temp.finishConnect();
						}
						else
						{
							return -1;
						}
					}
				}
			}
			if (m_channel != null && m_channel.isConnected())
			{
				DebugUtil.commonDebugInfo("connect server success");
				m_bool_Connect = true;
				m_channel.register(m_selector, SelectionKey.OP_READ);
				return 0;
			}
			else
			{
				return -1;
			}
		}
		catch (IOException ie)
		{
			DebugUtil.commonDebugWarn("ClientChannel.connect() io exception: "+
				ie);
			return -1;
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn("ClientChannel.connect() exception: ", e);
			return -1;
		}
	}

	private int handshake()
	{
		ByteBuffer buffer = ByteBuffer.allocate(24);
		buffer.clear();
		buffer.putInt(4);
		buffer.putInt(0x2BD7C98D);
		byte[] byte_Buffer = new byte[16];

		// need to add
		ByteBuffer bufRecv;
		try
		{
			bufRecv = recv();
			if (bufRecv == null)
				return -1;
			int int_Header = bufRecv.getInt();
			if (int_Header != 0XACF37B8D)
				return -1;
			if (bufRecv.hasRemaining())
			{
				bufRecv.get(byte_Buffer);
				// general MD5
				ByteBuffer bufMD5 = ByteBuffer.allocate(35);
				bufMD5.put(byte_Buffer);
				bufMD5.put("AeroHiVeHivEmANageR".getBytes());
				bufMD5.flip();
				MessageDigest dig = MessageDigest.getInstance("MD5");
				dig.update(bufMD5.array());
				byte[] md5 = dig.digest();
				buffer.put(md5);
				buffer.putInt(0, 20);
			}
			buffer.flip();
			send(buffer);
			bufRecv = recv();
			if (bufRecv == null)
				return -1;
			int_Header = bufRecv.getInt();
			if (int_Header != 0XACF37B8D)
				return -1;
		}// end try
		catch (IOException ie)
		{
			DebugUtil.commonDebugWarn(
				"ClientChannel.handshake() io exception: ", ie);
			ie.printStackTrace();
			return -1;
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"ClientChannel.handshake() exception happen: ", e);
			return -1;
		}

		buffer.clear();
		DebugUtil.commonDebugInfo("hand shake success");
		return 0;
	}

	/**
	 * send data
	 * 
	 * @param arg_Data data bytes
	 * @return -
	 * @throws IOException -
	 */
	public int send(ByteBuffer arg_Data) throws IOException
	{
		if (!m_bool_Connect)
			throw new IOException("Have not connected");
		int length = arg_Data.limit();
		while(length > 0) {
			int int_Ret = m_channel.write(arg_Data);
			if (int_Ret < 0)
				throw new IOException("Fail to send data");
			length -= int_Ret;
		}
		return 0;
	}

	/**
	 * receive data
	 *
	 * @return buffer -
	 * @throws IOException -
	 */
	public ByteBuffer recv() throws IOException
	{
		if (!m_bool_Connect)
			throw new IOException("Have not connected");
		// receive 4 bytes, get the length of data
		m_lengthByteBuffer.clear();
		if (0 >= recv(m_lengthByteBuffer, 4))
			return null;
		int int_Length = m_lengthByteBuffer.getInt();
		if (int_Length <= 0 || int_Length > MAX_DATA_SIZE)
		{
			DebugUtil
				.commonDebugWarn("Recv data:data length is invalid, length is "
					+ int_Length);
			throw new IOException("Recv data:data length is invalid");
		}
		// receive data
		ByteBuffer buffer = ByteBuffer.allocate(int_Length);
		if (0 >= recv(buffer, int_Length))
			return null;

		return buffer;
	}

	/**
	 * receive special length of data to buffer
	 * 
	 * @param arg_Buf
	 *            buffer
	 * @param arg_Length
	 *            length of data
	 * @return length of data, 0 is time out
	 * @throws IOException -
	 */
	private int recv(ByteBuffer arg_Buf, int arg_Length) throws IOException
	{
		int int_Length;
		int int_HasRecv;
		if (m_int_ActualLength - m_int_ReadPosition >= arg_Length)
		{
			// get the length of data
			for (int i = 0; i < arg_Length; i++)
				arg_Buf.put(m_recvByteBuffer.get());
			m_int_ReadPosition += arg_Length;
			arg_Buf.flip();
			return arg_Length;
		}
		else
		{
			int_HasRecv = m_int_ActualLength - m_int_ReadPosition;
			for (int i = 0; i < int_HasRecv; i++)
			{
				arg_Buf.put(m_recvByteBuffer.get());
			}
			int_Length = arg_Length - int_HasRecv;

			// receive data from channel
			m_int_ReadPosition = 0;
			m_int_ActualLength = 0;
			if (0 >= recvActualData())
				return 0;
			return recv(arg_Buf, int_Length);
		}
	}

	/**
	 * receive data from channel
	 *
	 * @return length of data, 0 is time out
	 * @throws IOException -
	 */
	private int recvActualData() throws IOException
	{
		m_int_ReadPosition = 0;
		m_int_ActualLength = 0;
		m_recvByteBuffer.clear();

		if (m_selector.isOpen() && m_selector.select(m_int_RecvTimeout) > 0)
		{
			for (Iterator<SelectionKey> iter = m_selector.selectedKeys().iterator(); iter.hasNext();)
			{
				SelectionKey key = iter.next();
				iter.remove();

				if (key.isValid() && key.isReadable())
				{
					m_channel_Temp = (SocketChannel) key.channel();
					
					if (m_channel_Temp.isOpen()) {
						m_int_ActualLength = m_channel_Temp.read(m_recvByteBuffer);

						if (m_int_ActualLength < 0) {
							throw new IOException("Net error");
						}

						m_recvByteBuffer.flip();
					}

					break;
				}
			}
		}

		return m_int_ActualLength;
	}

	/**
	 * open channel and connect
	 * 
	 * @return 0 success -1 failure
	 */
	public int open()
	{
		if (0 == connect())
		{
			if (-1 == handshake()) {
				DebugUtil.commonDebugWarn("Fail to handshake");
				close();
				return -1;
			}
		}
		else {
			close();
			return -1;
		}

		return 0;
	}

	/**
	 * Close channel
	 */
	public synchronized void close()
	{
		try
		{
			m_int_ReadPosition = 0;
			m_int_ActualLength = 0;
			m_recvByteBuffer.clear();
			m_bool_Connect = false;
			if (null != m_channel)
			{
				m_channel.close();
				m_channel = null;
			}
			if (null != m_selector)
			{
				m_selector.close();
				m_selector = null;
			}
		}
		catch (IOException ie)
		{
			DebugUtil.commonDebugWarn(
				"ClientChannel.close(): catch IOException ", ie);
		}
	}

	public String byte2hex(byte[] b)
	{
		String hs = "";
		String stmp;
		for (int n = 0; n < b.length; n++)
		{
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}

	public static void main(String[] args)
	{
		try
		{
			ClientChannel myClientSocket = new ClientChannel();
			myClientSocket.setServerIp("10.155.20.68");
			if (0 != myClientSocket.open())
				return;

			ByteBuffer buffer = ByteBuffer.allocate(256);

			// send delete ap connect requeest
			// buffer.putInt(29);
			// buffer.putShort((short)31);
			// buffer.putShort((short)7);
			// buffer.putShort((short)1);
			// buffer.putInt(19);
			// buffer.putInt(0X0a9B1409);
			// buffer.put((byte)14);
			// buffer.put(new String("00106121300052").getBytes());

			// send idp query request
			// buffer.putInt(29);
			// buffer.putShort((short)13);
			// buffer.putShort((short)6);
			// buffer.putShort((short)1);
			// buffer.putInt(19);
			// buffer.putInt(0X0a9B1409);
			// buffer.put((byte)14);
			// buffer.put(new String("00106121300052").getBytes());

			// send shutdown request
			buffer.putInt(6);
			buffer.putShort((short) 25);
			buffer.putShort((short) 6);
			buffer.putShort((short) 1);

			// send wtp event control request
			// buffer.putInt(36);
			// buffer.putShort((short)11);
			// buffer.putShort((short)6);
			// buffer.putShort((short)1);
			// buffer.putInt(19);
			// buffer.putInt(0X0a9B1409);
			// buffer.put((byte)14);
			// buffer.put(new String("00106121300052").getBytes());
			// buffer.putShort((short)6002);
			// buffer.putInt(1);
			// buffer.put((byte)1);

			buffer.flip();

			myClientSocket.send(buffer);
			while (true)
			{
				buffer = myClientSocket.recv();
				if (null != buffer)
					System.out.println(buffer);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}