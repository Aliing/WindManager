package com.ah.be.ls.processor;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.ls.action.PacketCommErrResponse;
import com.ah.be.ls.action.PacketVmVerifyQuery;
import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.VmVerifyInfo;
import com.ah.be.ls.util.CommConst;

public class VmVerifyProcessor implements DataProcessor {

	private byte m_packet_type = CommConst.Vm_Verify_Query_Packet_Type;
	private VmVerifyInfo  m_vm_verify_query_data = new VmVerifyInfo();
	private CommErrInfo   m_vm_verify_response_data = new CommErrInfo();
	
	
	public int do_build_packet(byte[] out) {
		
		return PacketVmVerifyQuery.buildOrderKeyQuery(out, 0, m_vm_verify_query_data);
	}

	
	public int do_parse_packet(byte[] input) {
		
		return PacketCommErrResponse.parseOederkeyResponse(input, 0, m_vm_verify_response_data);
	}

	
	public byte get_packet_type() {
	
		return m_packet_type;
	}

	@Override
	public Object get_response() {
		
		return m_vm_verify_response_data;
	}

	@Override
	public byte get_response_type() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init_send_data(Object obj) {
		
		m_vm_verify_query_data = (VmVerifyInfo)obj;
		m_vm_verify_query_data.setDataType(CommConst.Vm_verify_Query_Data_Type);
		m_vm_verify_query_data.setHmIp(HmBeOsUtil.getHiveManagerIPAddr());
	}

	@Override
	public boolean is_need_response() {
		
		return true;
	}

}
