package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketNewVersionFlagQuery;
import com.ah.be.ls.action.PacketNewVersionFlagResponse;
import com.ah.be.ls.data.PacketNewVersionFlagQueryData;
import com.ah.be.ls.data.PacketNewVersionFlagResponseData;
import com.ah.be.ls.util.CommConst;

public class NewVersionFlagQueryProcessor implements DataProcessor{
 
	private byte m_packet_type = CommConst.New_Version_Query_Packet_Type;
	
	private byte m_respone_type;
	
	private PacketNewVersionFlagQueryData m_query_data = new PacketNewVersionFlagQueryData();
	
	private PacketNewVersionFlagResponseData m_response_data = new PacketNewVersionFlagResponseData();
	
	@Override
	public int do_build_packet(byte[] out) {
		
		return PacketNewVersionFlagQuery.build3NewVersionFlagQuery(out, 0, m_query_data);
	}

	@Override
	public int do_parse_packet(byte[] input) {
		
		m_respone_type = input[0];
		
		return PacketNewVersionFlagResponse.parseNewVersionFlagtResponse(input, 0, m_response_data);
	}

	@Override
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public Object get_response() {
		
		return m_response_data;
	}

	@Override
	public byte get_response_type() {
		
		return m_respone_type;
	}

	@Override
	public void init_send_data(Object obj) {
		
		m_query_data = (PacketNewVersionFlagQueryData)obj;
		
		if(m_query_data.getNeedActKeyFlag())
		{
			m_query_data.setDataType(CommConst.Need_Act_Key_Packet_Type);
		}
		else
		{
			m_query_data.setDataType(CommConst.Not_Need_Act_Key_Packet_Type);
			m_query_data.setActKey(CommConst.No_Act_key);
		}
	}

	@Override
	public boolean is_need_response() {		
		return true;
	}

}
