package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketAPClientInfo;
import com.ah.be.ls.data.PacketAPClientInfoData;
import com.ah.be.ls.util.CommConst;

public class ApClientInfoProcessor implements DataProcessor{

	private byte m_packet_type = CommConst.Ap_Client_Info_Packet_Type;
	
	private PacketAPClientInfoData  m_sender_data = new PacketAPClientInfoData();
	
	@Override
	public int do_build_packet(byte[] out) {
		
		return PacketAPClientInfo.buildApClientInfo(out, 0, m_sender_data);
	}

	@Override
	public int do_parse_packet(byte[] input) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public Object get_response() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte get_response_type() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init_send_data(Object obj) {
        
		m_sender_data = (PacketAPClientInfoData)obj;
		
		if(m_sender_data.getNeedActKeyFlag())
		{
			m_sender_data.setDataType(CommConst.Need_Act_Key_Packet_Type);
		}
		else
		{
			m_sender_data.setDataType(CommConst.Not_Need_Act_Key_Packet_Type);
			m_sender_data.setActKey(CommConst.No_Act_key);
		}		
	}

	@Override
	public boolean is_need_response() {
		
		return false;
	}

}
