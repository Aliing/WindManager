package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketTrap;
import com.ah.be.ls.data.PacketTrapData;
import com.ah.be.ls.util.CommConst;

public class TrapInformProcessor implements DataProcessor{

	private byte m_packet_type = CommConst.Trap_Inform_Packet_Type;
	private PacketTrapData m_trap_data = new PacketTrapData();
	
	

	@Override
	public int do_build_packet(byte[] out) {
		
		return PacketTrap.buildTrap(out, 0, m_trap_data);
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
	public void init_send_data(Object obj) {
		//TODO
		m_trap_data = (PacketTrapData)obj;
		
		//m_trap_data.setDataType();
		
	}

	@Override
	public boolean is_need_response() {
		
		return false;
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
	

}
