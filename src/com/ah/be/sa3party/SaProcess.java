package com.ah.be.sa3party;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.app.DebugUtil;
import com.ah.be.tcp.util.ChannelProcess;
import com.ah.be.tcp.util.ServerHandle;
import com.ah.be.tcp.util.TcpException;
import com.ah.bo.hiveap.HiveAp;

public class SaProcess extends Thread implements ChannelProcess {
	
	private final List<Acceptsocket> m_clientChannel;
	
	private ServerHandle oHandle;
	
	public SaProcess() {
		m_clientChannel = new ArrayList<Acceptsocket>();
	}
	
	@Override
	public void handleAccept(SelectionKey key) throws TcpException {
		//run a thread to dealwith login process
		try
		{
			SocketChannel clntChan = ((ServerSocketChannel)key.channel()).accept();
			clntChan.configureBlocking(false);
			LoginProcess ologinprocress = new LoginProcess(key.selector(), clntChan);
			ologinprocress.start();			
		}
		catch(Exception ex)
		{
			throw new TcpException(ex);
		}
	}

	@Override
	public void handleRead(SelectionKey key) throws TcpException {
		//find accept socket
		SocketChannel clntChannel = (SocketChannel) key.channel();
		Acceptsocket  clntSocket = null;
		for (Acceptsocket socket : m_clientChannel) {
			if (socket.isSameClient(clntChannel)) {
				clntSocket = socket;
			}
		}
		
		if(null == clntSocket)
		{
			return;
		}
		
		ByteBuffer bRecv = clntSocket.getRecvBuffer();
		ByteBuffer bHandle = clntSocket.getHandleBuffer();
		//read byte
		bRecv.clear();
		
		try
		{
			if((!clntChannel.isConnected()) || (clntChannel.read(bRecv) <= 0))
			{
				clntSocket.closeSocket();
				return;
			}
			bRecv.flip();
			clntSocket.fillhandle(bHandle, bRecv);
			clntSocket.dealhandle(bHandle);			
		}
		catch(Exception ex)
		{
			clntSocket.closeSocket();
			throw new TcpException(ex);
		}
	}

	@Override
	public void handleWrite(SelectionKey key) throws TcpException {
	}

	public List<Acceptsocket> getClientChannelList() {
		return m_clientChannel;
	}

	private void stopsockets()
	{
		for (Acceptsocket socket : m_clientChannel) {
			socket.closeSocket();
		}
	}
	
	public void sendApConnStaus(HiveAp ap)
	{
		short smodle = ap.getHiveApModel();
		if((HiveAp.HIVEAP_MODEL_121 != smodle)
			&&(HiveAp.HIVEAP_MODEL_141 != smodle)
			&&(HiveAp.HIVEAP_MODEL_120 != smodle)
			&&(HiveAp.HIVEAP_MODEL_330 != smodle)
			&&(HiveAp.HIVEAP_MODEL_350!= smodle)
			&&(HiveAp.STATUS_MANAGED != ap.getManageStatus()))
		{
			return;
		}
		
		ByteBuffer bSend = ByteBuffer.allocate(SaComm.MAX_DATA_SIZE);
	    bSend.clear();
	    //length
	    bSend.putInt(0);
	    //type
	    bSend.putShort((short)SaComm.T_APINFO_N);		   
	    //sequence
	    bSend.putInt(0);		  
	    //timestamp
	    long time_second = System.currentTimeMillis()/1000;
	    byte[] btimestamp = new byte[4];
	    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
	    bSend.put(btimestamp, 0, 4);
	    
	    //elem
	    //type
	    bSend.putShort((short)SaComm.E_T_APINFO);
	    //length
	    ByteBuffer bap = packap(ap);
	    bSend.putInt(bap.limit()+2);
	    //apcount
	    bSend.putShort((short)1);
	    //apinfos
	    bSend.put(bap.array(), 0, bap.limit());
	    //send
	    bSend.flip();
	    bSend.putInt(bSend.limit()-4);
	    bSend.position(0);

		for (Acceptsocket soc : m_clientChannel) {
			if (soc.isRetrived()) {
				try {
					SaComm.send(soc.getClientChannel(), bSend);
				} catch (Exception ex) {
					soc.closeSocket();
					DebugUtil.commonDebugError("send ap info failed. " + ex.getMessage());
				}
			}
		}
	}
	
	public static ByteBuffer packap(HiveAp ap)
	{
		ByteBuffer bElem = ByteBuffer.allocate(SaComm.BUFFER_SIZE);
		bElem.clear();
		//length
		bElem.putShort((short)0);
		byte[] bMac = (ap.getMacAddress()).getBytes();
    	//mac_length
    	bElem.put((byte)bMac.length);
    	//Mac
    	bElem.put(bMac, 0, bMac.length);
    	
    	byte[] bHost = (ap.getHostName()).getBytes();
    	//host_length
    	bElem.put((byte)bHost.length);
    	//host
    	bElem.put(bHost, 0, bHost.length);
    	//apmodel
    	bElem.put((byte)ap.getHiveApModel());
    	//connect status
    	int iStaus  = ap.getConnectStatus();
    	if(0 == iStaus)
    	{
    		bElem.put((byte)0);
    	}
    	else
    	{
    		bElem.put((byte)1);
    	}		    	
    	//length
    	bElem.flip();
    	bElem.putShort((short)(bElem.limit()-2));
    	bElem.position(0);
    	
    	return bElem;
	}

	@Override
	public void run()
	{
		oHandle = new ServerHandle(SaComm.SA_PORT);
		try
		{
			oHandle.init(this);
			oHandle.handle();
		}
		catch(Exception ex)
		{
			DebugUtil.commonDebugError(ex.getMessage());
		}
	}
	
	public void stopProcess()
	{
		try
		{
			oHandle.uninit();
			stopsockets(); 	
		}
		catch(Exception ex)
		{
			DebugUtil.commonDebugError(ex.getMessage());
		}
	}

}