package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhSwitchPortInfo;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeSwitchPortInfoResultEvent extends BeCapwapClientResultEvent {
	
	List<AhSwitchPortInfo> portInfoList = new ArrayList<AhSwitchPortInfo>();
	
	public BeSwitchPortInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_INFO;
	}

	/** 
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			// count
			short count = buf.getShort();
			for(int i = 0; i < count; i++) {
				short length = buf.getShort();
				int beginPos = buf.position();
				int VLANCount = 0, k = 0, len = 0;
				StringBuffer VLANs = null;
				AhSwitchPortInfo portInfo = new AhSwitchPortInfo();
				portInfo.setMac(apMac);
				
				portInfo.setPortType(buf.get());
				portInfo.setEnableTimestamp(AhDecoder.int2long(buf.getInt())*1000);
				portInfo.setState(buf.get());
				portInfo.setLineProtocol(buf.get());
				
				VLANCount = AhDecoder.byte2int(buf.get());
				VLANs = new StringBuffer();
				for(k = 0; k < VLANCount; k++) {
					if(k != 0)
						VLANs.append(",");
					VLANs.append(buf.getShort());
				}
				portInfo.setVoiceVLANs(VLANs.toString());
				
				VLANCount = AhDecoder.byte2int(buf.get());
				VLANs = new StringBuffer();
				for(k = 0; k < VLANCount; k++) {
					if(k != 0)
						VLANs.append(",");
					VLANs.append(buf.getShort());
				}
				portInfo.setDataVLANs(VLANs.toString());
				
				portInfo.setAuthenticationState(buf.get());
				portInfo.setSTPMode(buf.get());
				portInfo.setSTPRole(buf.get());
				portInfo.setSTPState(buf.get());
				len = buf.get();
				portInfo.setPortName(AhDecoder.bytes2String(buf, len));
				
				if ((buf.position() - beginPos) < length) {
					int portCount = AhDecoder.byte2int(buf.get());
					StringBuffer ports = new StringBuffer();
					for(k = 0; k < portCount; k++) {
						int phyportLength = AhDecoder.byte2int(buf.get());
						int phyportPos = buf.position();
						
						if(k != 0)
							ports.append(",");
						len = buf.get();
						ports.append(AhDecoder.bytes2String(buf, len));
						
						buf.position(phyportPos+phyportLength);
					}
					portInfo.setPhysicalPorts(ports.toString());
				}
				
				if ((buf.position() - beginPos) < length) {
					portInfo.setMirrorPort(buf.get());
				}
				
				if ((buf.position() - beginPos) < length) {
					portInfo.setEnableTimestamp((System.currentTimeMillis()/1000-buf.getInt())*1000L);
				}
				
				if ((buf.position() - beginPos) < length) {
					portInfo.setPvid(buf.getShort());
				}
				
				portInfoList.add(portInfo);
				
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSwitchPortInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhSwitchPortInfo> getPortInfoList() {
		return portInfoList;
	}
}
