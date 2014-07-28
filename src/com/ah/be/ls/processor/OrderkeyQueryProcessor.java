package com.ah.be.ls.processor;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.ls.action.PacketCommErrResponse;
import com.ah.be.ls.action.PacketOrderkeyQuery;
import com.ah.be.ls.action.PacketOrderkeyResponse;
import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.OrderkeyQueryData;
import com.ah.be.ls.data.QueryLicenseInfo;
import com.ah.be.ls.util.CommConst;

public class OrderkeyQueryProcessor implements DataProcessor {

	private byte m_packet_type = CommConst.Order_Key_Query_Packet_Type;
	private OrderkeyQueryData m_order_key_query_data = new OrderkeyQueryData();
	private byte m_respone_type;
	private QueryLicenseInfo  m_License_Info         = new QueryLicenseInfo();
	private CommErrInfo       m_Error_Info           = new CommErrInfo();
	
	
	public int do_build_packet(byte[] out) {
		
		return PacketOrderkeyQuery.buildOrderKeyQuery(out, 0, m_order_key_query_data);
	}

	
	public int do_parse_packet(byte[] input) {
		
		m_respone_type = input[0];
		
		switch(m_respone_type)
		{
		case CommConst.Order_key_Response_Data_type:
			return PacketOrderkeyResponse.parseOederkeyResponse(input, 0, m_License_Info);
		case CommConst.Order_key_err_Response_Data_Type:
			return PacketCommErrResponse.parseOederkeyResponse(input, 0, m_Error_Info);
		default:
			return 0;
		}
	}

	
	public byte get_packet_type() {
		
		return m_packet_type;
	}

	@Override
	public Object get_response() {
		
		switch(m_respone_type)
		{
		case CommConst.Order_key_Response_Data_type:
			return m_License_Info;
		case CommConst.Order_key_err_Response_Data_Type:
			return m_Error_Info;
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
		
		m_order_key_query_data = (OrderkeyQueryData) obj;
		m_order_key_query_data.setDataType(CommConst.Order_key_Query_Data_Type);
		m_order_key_query_data.setHmIp(HmBeOsUtil.getHiveManagerIPAddr());
	}

	public boolean is_need_response() {
		
		return true;
	}

}
