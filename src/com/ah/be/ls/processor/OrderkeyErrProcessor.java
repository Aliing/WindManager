package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketCommErrResponse;
import com.ah.be.ls.action.PacketOrderkeyErr;
import com.ah.be.ls.action.PacketOrderkeyErrResponse;
import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.OrderkeyErrData;
import com.ah.be.ls.data.OrderkeyErrInfo;
import com.ah.be.ls.util.CommConst;

public class OrderkeyErrProcessor implements DataProcessor {

	private byte m_packet_type = CommConst.Order_Key_Err_Packet_Type;
	private OrderkeyErrData m_order_key_err_data = new OrderkeyErrData();
	private byte m_respone_type;
	private OrderkeyErrInfo  m_Orderkey_Err_Info         = new OrderkeyErrInfo();
	private CommErrInfo       m_Error_Info           = new CommErrInfo();
	
	public int do_build_packet(byte[] out) {
		
		return PacketOrderkeyErr.buildOrderKeyErr(out, 0, m_order_key_err_data);
	}

	
	public int do_parse_packet(byte[] input) {
		
		m_respone_type = input[0];
		
		switch(m_respone_type)
		{
			case CommConst.Order_key_Response_Data_type:
				return PacketOrderkeyErrResponse.parseOederkeyErrResponse(input, 0, m_Orderkey_Err_Info);
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
			case CommConst.Order_Key_Err_Response_Data_Type:
				return m_Orderkey_Err_Info.getResponseFlag();
			case CommConst.Order_Key_Err_err_Response_Data_Type:
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
		
		m_order_key_err_data = (OrderkeyErrData) obj;
		m_order_key_err_data.setDataType(CommConst.Order_Key_Err_Data_Type);
	}

	public boolean is_need_response() {
		
		return true;
	}

}
