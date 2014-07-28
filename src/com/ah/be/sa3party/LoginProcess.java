package com.ah.be.sa3party;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.Iterator;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;

public class LoginProcess extends Thread {
	
//	private SelectionKey                     		 m_key;
	private Selector                                 m_select;
	private int                                      m_timeout = 5000;
	private int                                      m_buffer_size = 4096;
	private SocketChannel                            m_client_channel;
	
	
	private boolean check_longin(byte[] bremote, byte[] bradm, String strusr, String strPsd)
	{
		byte[] bPostfix = SaComm.MD5_POSTFIX.getBytes();
		byte[] bUsr = strusr.getBytes();
		byte[] bPsd = strPsd.getBytes();
		
		byte[] bmd5_src1 = new byte[bradm.length+bUsr.length+bPostfix.length];
		byte[] bmd5_src2 = new byte[bradm.length+bPsd.length+bPostfix.length];
		
		int index = 0;
		fillbuffer(bmd5_src1, bradm, index);		
		index += bradm.length;		
		fillbuffer(bmd5_src1, bUsr, index);
		index += bUsr.length;
		fillbuffer(bmd5_src1, bPostfix, index);
		
		index = 0;
		fillbuffer(bmd5_src2, bradm, index);
		index += bradm.length;
		fillbuffer(bmd5_src2, bPsd, index);
		index += bPsd.length;
		fillbuffer(bmd5_src2, bPostfix, index);
		
		byte[] bmd5_1 = new byte[16];
		bmd5_1 = SaComm.getMd5(bmd5_src1);
		byte[] bmd5_2 = new byte[16];
		bmd5_2 = SaComm.getMd5(bmd5_src2);
		
		if(!isEqual(bremote, bmd5_1, 16, 0))
		{
	    	DebugUtil.commonDebugError("Digest1 check faild!");
			return false;
		}
		
		if(!isEqual(bremote, bmd5_2, 16, 16))
		{
			DebugUtil.commonDebugError("Digest2 check faild!");
			return false;
		}
		
		return true;
	}
	
	private void fillbuffer(byte[] buffer, byte[] bsrc, int index)
	{
		for(int i = 0; i < bsrc.length; i++)
		{
			buffer[i+index] = bsrc[i];
		}
	}
	
	private boolean isEqual(byte[] b1, byte[] b2, int length,int index)
	{
		for(int i=0; i<length; i++)
		{
			if(b1[i+index] != b2[i])
			{
				return false;
			}
		}
		
		return true;
	}
	
	public LoginProcess(Selector select, SocketChannel clntChan)
	{
		m_select = select;	
	    m_client_channel = clntChan;
	}

	@Override
	public void run()
	{
		//deal with login
		//SocketChannel clntChan = null;
		Selector selector = null;
		Acceptsocket clntSocket = null;
		try
		{						
		    selector = Selector.open();
		    m_client_channel.configureBlocking(false);
		    m_client_channel.register(selector, SelectionKey.OP_READ);
		    
		    if(selector.select(m_timeout) == 0)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("use select accept timeout(5s)");
		    	return;
		    }
		    
		    Iterator<SelectionKey> keyiter = selector.selectedKeys().iterator();
		    while(keyiter.hasNext())
		    {
		    	SelectionKey key = keyiter.next();
		    	keyiter.remove();
		    }
		    
		    //login request
		    ByteBuffer recvBuffer = ByteBuffer.allocate(m_buffer_size);
		    recvBuffer.clear();
		    int buffer_length;
		    if((!m_client_channel.isConnected()) || (buffer_length = m_client_channel.read(recvBuffer)) <= 0)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("could not read bytes");
		    	return;
		    }
		    recvBuffer.flip();
		    //receive login request
		    //data length
		    int data_length = recvBuffer.getInt();
		    
		    if(data_length > (buffer_length -4))
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the data lenth is not right!");
		    	return;
		    }
		    //type
		    int type = recvBuffer.getShort();
		    if(type != SaComm.T_LOGIN1_Q)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the request type is:"+type+ "not first login request type "+SaComm.T_LOGIN1_Q);
		    }
		    //seque number
		    byte[] bSeq = new byte[4];
		    recvBuffer.get(bSeq, 0, 4);
		    
		    //timestamp [not used]	   
		    byte[] btime = new byte[4];
		    recvBuffer.get(btime, 0, 4);
		    
		    //reponse
		    ByteBuffer sendBuffer = ByteBuffer.allocate(m_buffer_size);
		    sendBuffer.clear();
		    int sendLength=32;
		    //data length
		    sendBuffer.putInt(sendLength);
		    //response type
		    sendBuffer.putShort((short)SaComm.T_LOGIN1_R);
		    //sequence
		    sendBuffer.put(bSeq);
		    //timestamp
		    long time_second = System.currentTimeMillis()/1000;
		    byte[] btimestamp = new byte[4];
		    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
		    sendBuffer.put(btimestamp);
		    
		    //element type
		    sendBuffer.putShort((short)SaComm.E_T_LOGINFO_1);
		    //element value length
		    sendBuffer.putInt(16);
		    //radom
		    //get radomnumber
		    byte[] bRadm = new byte[16];
		    SaComm.get16randm(bRadm, 0);
		    sendBuffer.put(bRadm);
		    
		    sendBuffer.flip();
		    //send
		    SaComm.send(m_client_channel, sendBuffer);
		    
		    if(selector.select(m_timeout) == 0)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("use select accept timeout(5s)");
		    	return;
		    }
		    
		    while(keyiter.hasNext())
		    {
		    	SelectionKey key = keyiter.next();
		    	keyiter.remove();
		    }
		    
		    //login 2nd request		     
		    recvBuffer.clear();		   
		    if((!m_client_channel.isConnected()) || (buffer_length = m_client_channel.read(recvBuffer)) <= 0)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("could not read bytes");
		    	return;
		    }
		    recvBuffer.flip();
		    
		    //length
            data_length = recvBuffer.getInt();
		    
		    if(data_length > (buffer_length -4))
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the data lenth is not right!");
		    	return;
		    }
		    //type
		    type = recvBuffer.getShort();
		    if(type != SaComm.T_LOGIN2_Q)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the request type is:"+type+ "not second login request type "+SaComm.T_LOGIN2_Q);
		    }
		    //seque number
		    bSeq = new byte[4];
		    recvBuffer.get(bSeq, 0, 4);
		    //timestamp [not used]
		    btime = new byte[4];
		    recvBuffer.get(btime, 0, 4);
		    //element type
		    type = recvBuffer.getShort();
		    if(type != SaComm.E_T_LOGINFO_2)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the element request type is:"+type+ "not md5 response type "+SaComm.E_T_LOGINFO_2);
		    }
		    data_length = recvBuffer.getInt();
		    if(data_length != 32)
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("the second longin's response length is not 32");
		    }
		    byte[] brsp = new byte[32];
		    recvBuffer.get(brsp, 0, 32);
		    //check
		    if(!check_longin(brsp, bRadm, SaComm.getSAUsr(), SaComm.getSAPasswd()))
		    {
		    	m_client_channel.close();
		    	selector.close();
		    	DebugUtil.commonDebugError("Digest check faild");
		    }
		    
		    //add to clientlist
		    clntSocket = new Acceptsocket();
		    clntSocket.setClientChannel(m_client_channel);
			AhAppContainer.getBeAdminModule().getSaProcess().getClientChannelList().add(clntSocket);
		    m_client_channel.configureBlocking(false);
		    selector.close();
		    m_select.wakeup();
		    m_client_channel.register(m_select, SelectionKey.OP_READ);
		    
		    //send response
		    sendBuffer.clear();
		    sendLength=10;
		    //data length
		    sendBuffer.putInt(sendLength);
		    //response type
		    sendBuffer.putShort((short)SaComm.T_LOGIN2_R);
		    //sequence
		    sendBuffer.put(bSeq);
		    //timestamp
		    time_second = System.currentTimeMillis()/1000;
		    btimestamp = new byte[4];
		    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
		    sendBuffer.put(btimestamp);
		    
		    sendBuffer.flip();
		    //send
		    SaComm.send(m_client_channel, sendBuffer);	
		    
		}catch(Exception ex)
		{
			if(null != m_client_channel)
			{				
				try
				{
					m_client_channel.close();
					//remove
					if(clntSocket != null)
					{
						Iterator<Acceptsocket> sockets = AhAppContainer.getBeAdminModule().getSaProcess().getClientChannelList().iterator();
						while(sockets.hasNext())
						{
							Acceptsocket sock = sockets.next();
							if(clntSocket.isSameClient(sock.getClientChannel()))
							{
								sockets.remove();
							}
						}
					}
					
				}
				catch(Exception ex1)
				{
					DebugUtil.commonDebugError(ex1.getMessage());
				}
			}			
			
			DebugUtil.commonDebugError(ex.getMessage());
		}
		finally
		{
			if(null != selector)
			{				
				try
				{
					selector.close();
				}
				catch(Exception ex2)
				{
					DebugUtil.commonDebugError(ex2.getMessage());
				}
			}		
		}
	}

}
