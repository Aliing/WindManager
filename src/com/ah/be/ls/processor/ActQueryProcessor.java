/**
 *@filename		ActQueryProcessor.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */


package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketActQuery;
import com.ah.be.ls.action.PacketActResponse;
import com.ah.be.ls.data.PacketActQueryData;
import com.ah.be.ls.data.PacketInvalidActResponseData;
import com.ah.be.ls.data.PacketValidActResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class ActQueryProcessor implements DataProcessor
{
	private byte m_packet_type = CommConst.Act_Query_Packet_Type;
	
	private PacketActQueryData m_act_query_data = new PacketActQueryData();
	
	private byte m_respone_type;
	
	private PacketInvalidActResponseData m_invalid_response_data = new PacketInvalidActResponseData();
	
	private PacketValidActResponseData m_valid_respon_data = new PacketValidActResponseData(); 

	

	@Override
	public int do_build_packet(byte[] bOut) {
		
		return PacketActQuery.buildActQuery(bOut, 0, m_act_query_data);
	}

	@Override
	public int do_parse_packet(byte[] bInput) {
		
		m_respone_type = bInput[0];
		
		switch(m_respone_type)
		{
		    case CommConst.Act_Response_Data_Type:
		    	return PacketActResponse.parseValidActResponse(bInput, 0, m_valid_respon_data);
		    case CommConst.Act_Response_Deny_Data_Type:
		    	return PacketActResponse.parseInvalidActResponse(bInput, 0, m_invalid_response_data);
		    default:
		    	return 0;
		}		
	}

	@Override
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public void init_send_data(Object oData) {
		
		m_act_query_data = (PacketActQueryData)oData;	
		
		//add data type
		m_act_query_data.setDataType(CommConst.Act_Query_Data_Type);
		
		//add act code
		m_act_query_data.setActCode(CommTool.getRandInt());		
		
		//add update limited
		//m_act_query_data.setUpdateLimited(CommConst.HM_Update_Limit);		
		
	}

	@Override
	public boolean is_need_response() {
		
		return true;
	}

	@Override
	public Object get_response() {
		
		switch(m_respone_type)
		{
		    case CommConst.Act_Response_Data_Type:
		    	return m_valid_respon_data;
		    case CommConst.Act_Response_Deny_Data_Type:
		    	return m_invalid_response_data;
		    default:
		    	return null;
		}		
	}

	@Override
	public byte get_response_type() {
		
		return m_respone_type;
	}

}
