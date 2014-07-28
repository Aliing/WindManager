package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhSwitchPortStats;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeSwitchPortStatsResultEvent extends BeCapwapClientResultEvent {
	
	List<AhSwitchPortStats> portStatsList = new ArrayList<AhSwitchPortStats>();
	
	public BeSwitchPortStatsResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS;
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
			for(int i = 0; i < count; i++){
				AhSwitchPortStats portStats = new AhSwitchPortStats();
				short length = buf.getShort();
				int beginPos = buf.position();
				byte len = 0;
				
				portStats.setMac(apMac);
				
				portStats.setTxPacketCount(buf.getLong());
				portStats.setRxPacketCount(buf.getLong());
				portStats.setTxBytesCount(buf.getLong());
				portStats.setRxBytesCount(buf.getLong());
				portStats.setTxUnicastPackets(buf.getLong());
				portStats.setRxUnicastPackets(buf.getLong());
				portStats.setTxMuticastPackets(buf.getLong());
				portStats.setRxMuticastPackets(buf.getLong());
				portStats.setTxBroadcastPackets(buf.getLong());
				portStats.setRxBroadcastPackets(buf.getLong());
				len = buf.get();
				portStats.setPortName(AhDecoder.bytes2String(buf, len));

				portStatsList.add(portStats);
				
				if ((buf.position() - beginPos) < length) {
					portStats.setRxBadPauseFrames(buf.getLong());
					portStats.setRxUnreogMacFrames(buf.getLong());
					portStats.setRxFragmentFrames(buf.getLong());
					portStats.setRxJabberFrames(buf.getLong());
					portStats.setRxMACErrorFrames(buf.getLong());
					portStats.setRxCollisionsFrames(buf.getLong());
					portStats.setRxLateCollisionFrames(buf.getLong());
					portStats.setRxBadOctetsFrames(buf.getLong());
					portStats.setRxBadCRCFrames(buf.getLong());
					portStats.setRxErrorFrames(buf.getLong());
					portStats.setRxUndersizeFrames(buf.getLong());
					portStats.setRxOversizeFrames(buf.getLong());
					portStats.setRxOverrunFrames(buf.getLong());
					portStats.setTxExcessiveCollisionFrames(buf.getLong());
					portStats.setTxMACTransmitErrorFrames(buf.getLong());
				}
				
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSwitchPortStatsResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhSwitchPortStats> getPortStatsList() {
		return portStatsList;
	}
}
