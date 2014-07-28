package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhSwitchPortPeriodStats;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeSwitchPortStatsReportResultEvent extends BeCapwapClientResultEvent {
	
	List<AhSwitchPortPeriodStats> portStatsList = new ArrayList<AhSwitchPortPeriodStats>();
	
	public BeSwitchPortStatsReportResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS_REPORT;
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
				AhSwitchPortPeriodStats portStats = new AhSwitchPortPeriodStats();
				short length = buf.getShort();
				int beginPos = buf.position();
				byte len = 0;
				
				portStats.setApmac(apMac);
				
				portStats.setTimestamp((AhDecoder.int2long(buf.getInt())/60) * 60 * 1000);
				portStats.setCollectPeriod(buf.getShort());
				portStats.setUtilization((float)buf.getShort()/100);
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
				
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSwitchPortStatsResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhSwitchPortPeriodStats> getPortStatsList() {
		return portStatsList;
	}
}
