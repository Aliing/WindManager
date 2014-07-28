package com.ah.be.sa3party;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.performance.BeSpectralAnalysisProcessor;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class Acceptsocket implements Sa3Infc{

	private SocketChannel   m_clntChan = null;
	private int				MAX_DATA_SIZE		= SaComm.MAX_DATA_SIZE;
	private int				BUFFER_SIZE			= SaComm.BUFFER_SIZE;

	private ByteBuffer     recvBuffer;
	private ByteBuffer     handleBuffer;
	private BlockingQueue<byte[]> EventQueue ;
	private boolean        m_processQueue_flag  = true;
	private boolean        m_isRetrived         = false;
	private long           channel_id           = 0;

	public long getId()
	{
		return channel_id;
	}

	public boolean isSameClient(SocketChannel client)
	{
		//cmp address
		byte[] destadd = client.socket().getInetAddress().getAddress();
		if(null == destadd)
		{
			return false;
		}

		byte[] srcadd = m_clntChan.socket().getInetAddress().getAddress();
		if(null == srcadd)
		{
			return false;
		}
		if(destadd.length != srcadd.length)
		{
			return false;
		}

		for(int i = 0; i < destadd.length; i++)
		{
			if(destadd[i] != srcadd[i])
			{
				return false;
			}
		}

		//cmp port
		if(client.socket().getPort() != m_clntChan.socket().getPort())
		{
			return false;
		}

		return true;
	}

	public ByteBuffer getRecvBuffer()
	{
		return recvBuffer;
	}

	public ByteBuffer getHandleBuffer()
	{
		return handleBuffer;
	}

	public Acceptsocket()
	{
		recvBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		handleBuffer = ByteBuffer.allocate(MAX_DATA_SIZE);
		EventQueue    = new LinkedBlockingQueue<byte[]>();
		channel_id = System.currentTimeMillis();
		processQueue oprocess = new processQueue();
		oprocess.start();
	}

	public void setClientChannel(SocketChannel sChannel)
	{
		m_clntChan = sChannel;
	}

	public SocketChannel getClientChannel()
	{
		return m_clntChan;
	}

	public void setProcessQFlag(boolean bFlag)
	{
		m_processQueue_flag = bFlag;
	}

	public int fillhandle(ByteBuffer handle, ByteBuffer recv)
	{
		handle.put(recv.array(),0, recv.limit());
		return recv.limit();
	}

	public void dealhandle(ByteBuffer handle)
	{
		int index = handle.position();
		if(index < 4)
		{
			return;
		}
		handle.position(0);

		int e_index = handle.position();
		int iLength = handle.getInt();
		handle.position(e_index);

		while((e_index+iLength+4) <= index)
		{
			e_index = cutEvent(handle, iLength+4);

			if(e_index +4 > index)
			{
				break;
			}

			iLength = handle.getInt();
			handle.position(e_index);
		}

		//remove handlebuffer
		if(e_index == 0)
		{
			handle.position(index);
		}
		else
		{
			int ibytes = index-e_index;
			byte[] btmp = new byte[ibytes];

			handle.get(btmp, 0, ibytes);
			handle.clear();
			handle.put(btmp);
		}

	}

	private int cutEvent(ByteBuffer handle, int iLength)
	{
		byte[] bTmp = new byte[iLength];
	    handle.get(bTmp, 0, iLength);

	    EventQueue.add(bTmp);
		return handle.position();
	}

	public void closeSocket()
	{
		try
		{
			m_clntChan.close();
			m_processQueue_flag = false;
			AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().stopFrom3rd(channel_id);

			Iterator<Acceptsocket> sockets = AhAppContainer.getBeAdminModule().getSaProcess().getClientChannelList().iterator();
			while(sockets.hasNext())
			{
				Acceptsocket sock = sockets.next();
				if(isSameClient(sock.getClientChannel()))
				{
					sockets.remove();
				}
			}
		}
		catch(Exception ex)
		{
			DebugUtil.commonDebugError(ex.getMessage());
		}
	}

    //parse retrieve aplist
	private void dealRetrieveAplist(ByteBuffer buffer)
	{
		//seque number
		byte[] bSeq = new byte[4];
		buffer.get(bSeq, 0, 4);
	    //timestamp [not used]
	    byte[] btime = new byte[4];
	    buffer.get(btime, 0, 4);

	    ByteBuffer bSend = ByteBuffer.allocate(MAX_DATA_SIZE);
	    bSend.clear();
	    //length
	    bSend.putInt(0);
	    //type
	    bSend.putShort((short)SaComm.T_RETRIVEAP_R);
	    //sequence
	    bSend.put(bSeq, 0, 4);
	    //timestamp
	    long time_second = System.currentTimeMillis()/1000;
	    byte[] btimestamp = new byte[4];
	    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
	    bSend.put(btimestamp, 0, 4);

	    //element value
	    //message type
	    bSend.putShort((short)SaComm.E_T_APINFO);

	    ByteBuffer baps = packAps();
	    baps.flip();
	    //length value
	    bSend.putInt(baps.limit());
	    //elem value
	    bSend.put(baps.array(), 0, baps.limit());

	    //send
	    bSend.flip();
	    bSend.putInt(bSend.limit()-4);
	    bSend.position(0);
	    try
	    {
	    	SaComm.send(m_clntChan, bSend);
	    	m_isRetrived = true;
	    }
	    catch(Exception ex)
	    {
	    	closeSocket();
	    	DebugUtil.commonDebugError("send repose for aps retrive failed. "+ex.getMessage());
	    }

	}

    private ByteBuffer  packAps()
    {
    	 //get ap list
	    String strSql = "select macAddress, hostName,hiveApModel,connectStatus from " + HiveAp.class.getSimpleName();
	    String where  = "hiveApModel= :s1 or hiveApModel= :s2 or hiveApModel= :s3 and manageStatus= :s4";

	    Object[] values = new Object[4];
	    values[0] = (short)5;
	    values[1] = (short)8;
	    values[2] = (short)9;
	    values[3] = (short)1;

	    ByteBuffer bReturn;

	    List<?> list = QueryUtil.executeQuery(strSql, null, new FilterParams(where, values));
	    if(list.isEmpty())
	    {
	    	bReturn = ByteBuffer.allocate(BUFFER_SIZE);
	    	bReturn.clear();
	    	bReturn.putShort((short)0);
	    	return bReturn;
	    }
	    else
	    {
	    	bReturn = ByteBuffer.allocate(list.size()*BUFFER_SIZE);
	    	bReturn.clear();
	    	bReturn.putShort((short)list.size());

	    	for(Object object:list)
		    {
	    		Object[] apinfo = (Object[]) object;

	    		ByteBuffer bElem = ByteBuffer.allocate(BUFFER_SIZE);
	    		bElem.clear();
	    		//legth
	    		bElem.putShort((short)0);
		    	byte[] bMac = ((String)apinfo[0]).getBytes();
		    	//mac_length
		    	bElem.put((byte)bMac.length);
		    	//Mac
		    	bElem.put(bMac, 0, bMac.length);

		    	byte[] bHost = ((String)apinfo[1]).getBytes();
		    	//host_length
		    	bElem.put((byte)bHost.length);
		    	//host
		    	bElem.put(bHost, 0, bHost.length);
		    	//apmodel
		    	bElem.put(((Short)apinfo[2]).byteValue());
		    	//connect status
		    	int iStaus  = (Short)apinfo[3];
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

		    	//add to return
		    	bReturn.put(bElem.array(), 0, bElem.limit());
		    }

	    	return bReturn;
	    }

    }

    private void DealStartSa(ByteBuffer buffer)
    {
    	//seque number
		byte[] bSeq = new byte[4];
		buffer.get(bSeq, 0, 4);
	    //timestamp [not used]
	    byte[] btime = new byte[4];
	    buffer.get(btime, 0, 4);

	    //elem1
	    //elem_type
	    int type = buffer.getShort();
	    if(type != SaComm.E_T_AP_IDENT)
	    {
	    	DebugUtil.commonDebugError("Element type is "+type+" , it is not ap identifier type "+SaComm.E_T_AP_IDENT);
	    	return;
	    }
	    //elem length
	    int length = buffer.getInt();
	    //elem values
	    //ap identifier
	    length = SaComm.byte2short(buffer.get());
	    //mac
	    byte[] bMac = new byte[length];
	    buffer.get(bMac, 0, bMac.length);
	    String strMac = new String(bMac);

	    //elem2
	    type = buffer.getShort();
	    if(type != SaComm.E_T_STARTSA_INFO)
	    {
	    	DebugUtil.commonDebugError("Element type is "+type+" , it is not start sa type "+SaComm.E_T_STARTSA_INFO);
	    	return;
	    }
	    //elem2 legth
	    length = buffer.getInt();
	    //elem2 values
	    //wifi count
	    int wifi_count = SaComm.byte2short(buffer.get());
	    if(0 == wifi_count)
	    {
	    	DebugUtil.commonDebugError("Element type is "+SaComm.E_T_STARTSA_INFO+", but wifi count is 0.");
	    	return;
	    }
	    Elem_wifi_info[] wifi_infos = new Elem_wifi_info[wifi_count];
	    Map<String, int[]> wifi_map= new HashMap<String, int[]>();
	    for(int i =0; i < wifi_infos.length; i++)
	    {
	    	wifi_infos[i] = new Elem_wifi_info();
	    	//wifi name
		    byte[] bwifi_name = new byte[5];
		    buffer.get(bwifi_name, 0, bwifi_name.length);
		    wifi_infos[i].setWifiName(new String(bwifi_name));
		    //report interval
		    wifi_infos[i].setReportInterval(SaComm.byte2short(buffer.get()));
		    //run_time
		    wifi_infos[i].setRuntime(buffer.getInt());
		    //number_channel
		    int num_channel = SaComm.byte2short(buffer.get());
		    wifi_infos[i].setNumChannel(num_channel);
		    for(int j=0; j<num_channel; j++)
		    {
		    	wifi_infos[i].setchannel(SaComm.byte2short(buffer.get()), j);
		    }

		    wifi_map.put(wifi_infos[i].getWifiName(), wifi_infos[i].getchannels());
	    }


	    int brslt = 0;
	    //start sa response
	    //brslt = startSpectralAnalysis(strMac, wifi_map, wifi_infos[0].getReportInterval(), wifi_infos[0].getRuntime()*60 ,this);
	    brslt = AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().startSpectralAnalysis(strMac, wifi_map,
	    		(short)wifi_infos[0].getReportInterval(), wifi_infos[0].getRuntime()*60, this);

	    //response
	    ByteBuffer bSend = ByteBuffer.allocate(MAX_DATA_SIZE);
	    bSend.clear();
	    //length
	    bSend.putInt(0);
	    //type
	    bSend.putShort((short)SaComm.T_STARTSA_R);
	    //sequence
	    bSend.put(bSeq, 0, 4);
	    //timestamp
	    long time_second = System.currentTimeMillis()/1000;
	    byte[] btimestamp = new byte[4];
	    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
	    bSend.put(btimestamp, 0, 4);

	    //elem
	    //type
	    bSend.putShort((short)SaComm.E_T_RESULT_DESC);
	    //length
	    bSend.putInt(1);
	    //values
	    if (brslt != BeSpectralAnalysisProcessor.STATUS_DOING && brslt > 0)
			brslt = BeSpectralAnalysisProcessor.STATUS_ERROR_CLI;

	    bSend.put((byte)brslt);

	    //send
	    bSend.flip();
	    bSend.putInt(bSend.limit()-4);
	    bSend.position(0);
	    try
	    {
	    	SaComm.send(m_clntChan, bSend);
	    }
	    catch(Exception ex)
	    {
	    	closeSocket();
	    	DebugUtil.commonDebugError("send repose for start SA reponse failed. "+ex.getMessage());
	    }

    }


    private void DealstopSa(ByteBuffer buffer)
    {
    	//seque number
		byte[] bSeq = new byte[4];
		buffer.get(bSeq, 0, 4);
	    //timestamp [not used]
	    byte[] btime = new byte[4];
	    buffer.get(btime, 0, 4);

	    //elem1
	    //elem_type
	    int type = buffer.getShort();
	    if(type != SaComm.E_T_AP_IDENT)
	    {
	    	DebugUtil.commonDebugError("Element type is "+type+" , it is not ap identifier type "+SaComm.E_T_AP_IDENT);
	    	return;
	    }
	    //elem length
	    int length = buffer.getInt();
	    //elem values
	    //ap identifier
	    length = SaComm.byte2short(buffer.get());
	    //mac
	    byte[] bMac = new byte[length];
	    buffer.get(bMac, 0, bMac.length);
	    String strMac = new String(bMac);

	    //elem2
	    type = buffer.getShort();
	    if(type != SaComm.E_T_STOPSA_INFO)
	    {
	    	DebugUtil.commonDebugError("Element type is "+type+" , it is not stop sa type "+SaComm.E_T_STOPSA_INFO);
	    	return;
	    }
	    //elem2 legth
	    length = buffer.getInt();

	    //values
	    //wifi_count
	    int wifi_count = SaComm.byte2short(buffer.get());
	    String[] wifi_names = new String[wifi_count];
	    for(int i=0; i < wifi_names.length; ++i)
	    {
	    	//wifi name
		    byte[] bwifi_name = new byte[5];
		    buffer.get(bwifi_name, 0, bwifi_name.length);
		    wifi_names[i] = new String(bwifi_name);
	    }

	    int brslt = 0;
	    //stop sa response
	    //brslt = stopSpectralAnalysis(strMac, wifi_names);
	    brslt = AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().stopSpectralAnalysis(strMac, wifi_names);

	    //response
	    ByteBuffer bSend = ByteBuffer.allocate(MAX_DATA_SIZE);
	    bSend.clear();
	    //length
	    bSend.putInt(0);
	    //type
	    bSend.putShort((short)SaComm.T_STOPSA_R);
	    //sequence
	    bSend.put(bSeq, 0, 4);
	    //timestamp
	    long time_second = System.currentTimeMillis()/1000;
	    byte[] btimestamp = new byte[4];
	    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
	    bSend.put(btimestamp, 0, 4);

	    //elem
	    //type
	    bSend.putShort((short)SaComm.E_T_RESULT_DESC);
	    //length
	    bSend.putInt(1);
	    //values
	    if (brslt != BeSpectralAnalysisProcessor.STATUS_DOING && brslt > 0)
			brslt = BeSpectralAnalysisProcessor.STATUS_ERROR_CLI;

	    bSend.put((byte)brslt);

	    //send
	    bSend.flip();
	    bSend.putInt(bSend.limit()-4);
	    bSend.position(0);
	    try
	    {
	    	SaComm.send(m_clntChan, bSend);
	    }
	    catch(Exception ex)
	    {
	    	closeSocket();
	    	DebugUtil.commonDebugError("send repose for stop SA reponse failed. "+ex.getMessage());
	    }
    }

    @Override
	public void sendSAdata(String strMac,byte[] bsData) {

    	 //response
	    ByteBuffer bSend = ByteBuffer.allocate(MAX_DATA_SIZE);
	    bSend.clear();
	    //length
	    bSend.putInt(0);
	    //type
	    bSend.putShort((short)SaComm.T_SADATA_N);
	    //sequence
	    bSend.putInt(0);
	    //timestamp
	    long time_second = System.currentTimeMillis()/1000;
	    byte[] btimestamp = new byte[4];
	    SaComm.long2unsignedbytes(btimestamp, time_second, 0);
	    bSend.put(btimestamp, 0, 4);

	    //elem1
	    //type
	    bSend.putShort((short)SaComm.E_T_AP_IDENT);
	    //length
	    int elem_index = bSend.position();
	    int elem_length = 0;
	    bSend.putInt(0);

	    //mac length
	    byte[] bmac = strMac.getBytes();
	    bSend.put((byte)bmac.length);
	    elem_length += 1;
	    //mac
	    bSend.put(bmac, 0, bmac.length);
	    elem_length += bmac.length;

	    //elem length
	    int elem_tail = bSend.position();
	    bSend.position(elem_index);
	    bSend.putInt(elem_length);
	    bSend.position(elem_tail);

	    //elem2
	    //type
	    bSend.putShort((short)SaComm.E_T_SPECDATA_INFO);
	    //length
	    bSend.putInt(bsData.length);
	    //values
	    bSend.put(bsData, 0 , bsData.length);

	    //send
	    bSend.flip();
	    bSend.putInt(bSend.limit()-4);
	    bSend.position(0);
	    try
	    {
	    	SaComm.send(m_clntChan, bSend);
	    }
	    catch(Exception ex)
	    {
	    	closeSocket();
	    	DebugUtil.commonDebugError("send SA data failed. "+ex.getMessage());
	    }

	}

    @Override
	public long getChannelID() {

    	return channel_id;
	}

    public boolean isRetrived()
    {
    	return m_isRetrived;
    }


    class processQueue extends Thread
    {
    	public void run()
    	{
    		while(m_processQueue_flag)
    		{
    			//getbuffer
    			byte[] btmp = EventQueue.poll();

    			if(null == btmp)
    			{
    				try
    				{
    					Thread.sleep(1000);
    				}
    				catch(Exception ex)
    				{
    					DebugUtil.commonDebugError(ex.getMessage());
    				}
    				continue;
    			}

    			ByteBuffer bBuffer = ByteBuffer.allocate(btmp.length);
    			bBuffer.clear();
    			bBuffer.put(btmp, 0, btmp.length);
    			bBuffer.flip();

    			//parse byte
    			//length
    			int data_length = bBuffer.getInt();
    			if(data_length > (btmp.length - 4))
    			{
    				continue;
    			}

    			//type
    			int type = bBuffer.getShort();

    			switch(type)
    			{
    			    case SaComm.T_RETRIVEAP_Q:
    			    	dealRetrieveAplist(bBuffer);
    			    	break;
    			    case SaComm.T_STARTSA_Q:
    			    	DealStartSa(bBuffer);
    			    	break;
    			    case SaComm.T_STOPSA_Q:
    			    	DealstopSa(bBuffer);
    			    	break;
    			    default:
    			    	break;
    			}
    		}
    	}
    }

	public static void main(String[] args)
	{
//		short c = 255;
//		byte b = (byte)c;
		byte b = -1;


		short s = 0;
		s |= (b&0xff);



		System.out.println(s);
	}

}