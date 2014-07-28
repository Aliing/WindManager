package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketCommErrResponse;
import com.ah.be.ls.action.PacketVersionInfo2Query;
import com.ah.be.ls.action.PacketVersionInfo2Response;
import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.PacketVersion2QueryData;
import com.ah.be.ls.data.PacketVersion2ResponseData;
import com.ah.be.ls.util.CommConst;

public class VersionInfoQuery2Processor implements DataProcessor{

	private byte m_packet_type = CommConst.Version_List_Query_Packet_Type;
	private PacketVersion2QueryData m_version_list_query_data = new PacketVersion2QueryData();
	private byte m_respone_type;
	private PacketVersion2ResponseData m_version_list_response_data = new PacketVersion2ResponseData();
	private CommErrInfo m_version_list_deny_data  = new CommErrInfo();
	
	@Override
	public int do_build_packet(byte[] out) {
		
		return PacketVersionInfo2Query.buildInfoToDownload_2(out, 0, m_version_list_query_data);
	}

	@Override
	public int do_parse_packet(byte[] input) {
		
		m_respone_type = input[0];
		
		switch(m_respone_type)
		{
		    case CommConst.Version_List_Response_Data_Type:
		    	return PacketVersionInfo2Response.parseInfoToDownload_2(input, 0, m_version_list_response_data);
		    case CommConst.Version_List_Response_Deny_Type:
		    	return PacketCommErrResponse.parseOederkeyResponse(input, 0, m_version_list_deny_data);
		    default:
		    	return 0;
		}
		
	}

	@Override
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public Object get_response() {

		switch(m_respone_type)
		{
		    case CommConst.Version_List_Response_Data_Type:
		    	return m_version_list_response_data;
		    case CommConst.Version_List_Response_Deny_Type:
		    	return m_version_list_deny_data;
		    default:
		    	return null;
		}
	}

	@Override
	public byte get_response_type() {
		
		return m_respone_type;
	}

	@Override
	public void init_send_data(Object obj) {
		m_version_list_query_data = (PacketVersion2QueryData)obj;
		
		m_version_list_query_data.setDataType(CommConst.Version_List_Query_Data_Type);
	}

	@Override
	public boolean is_need_response() {
		
		return true;
	}

}
