package com.ah.be.ls.processor;


import com.ah.be.ls.action.PacketVersionInfoQuery;
import com.ah.be.ls.action.PacketVersionInfoResponse;
import com.ah.be.ls.data.PacketVersionInfoQueryData;
import com.ah.be.ls.data.PacketVersionInfoResponseData;
import com.ah.be.ls.util.CommConst;

public class VersionInfoQueryProcessor implements DataProcessor{

	private byte m_packet_type = CommConst.Download_Query_Packet_Type;
	
	private PacketVersionInfoQueryData m_download_query_data = new PacketVersionInfoQueryData();
	
	private byte m_respone_type;
	
	private PacketVersionInfoResponseData m_valid_download_response_data = new PacketVersionInfoResponseData();
	
	

	@Override
	public int do_build_packet(byte[] bOut) {
		
		return PacketVersionInfoQuery.build2DownloadQuery(bOut, 0, m_download_query_data);
	}

	@Override
	public int do_parse_packet(byte[] bInput) {
		
        m_respone_type = bInput[0];
		
		switch(m_respone_type)
		{
		    case CommConst.Download_Response_Data_Type:
		    	return PacketVersionInfoResponse.parseDownloadResponse(bInput, 0, m_valid_download_response_data);
		    case CommConst.Download_Response_Deny_Type:
		    	return 1;
		    default:
		    	return 0;
		}		
	}

	@Override
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public void init_send_data(Object obj) {
		
		m_download_query_data = (PacketVersionInfoQueryData)obj;
		
		//data type
		m_download_query_data.setDataType(CommConst.Download_Query_Data_Type);		
		
	}	
	
	@Override
	public boolean is_need_response() {
		
		return true;
	}

	@Override
	public Object get_response() {
		switch(m_respone_type)
		{
		    case CommConst.Download_Response_Data_Type:
		    	return m_valid_download_response_data;
		    case CommConst.Download_Response_Deny_Type:
		    	return null;
		    default:
		    	return null;
		}	
	}

	@Override
	public byte get_response_type() {
		
		return m_respone_type;
	}


}
