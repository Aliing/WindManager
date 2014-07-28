package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.appreport.AppFlowHelper;
import com.ah.bo.performance.AhAppFlowLog;

@SuppressWarnings("serial")
public class BeApplicationFlowInfoResultEvent extends BeCapwapClientResultEvent {
	
	List<AhAppFlowLog> appFlowList = new ArrayList<AhAppFlowLog>();
	
	public BeApplicationFlowInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPLICATION_FLOW_INFO;
	}

	/** 
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			long timestamp = System.currentTimeMillis();
			long starttime = AppFlowHelper.convertAppFlowTimeStamp(timestamp);
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			short count = buf.getShort();
			for (int i = 0; i < count; i++) {
				short length = buf.getShort();
				int beginPos = buf.position();
				AhAppFlowLog appFlowDay = new AhAppFlowLog();
				appFlowDay.setAppCode(buf.getShort());
				appFlowDay.setBytes(buf.getLong());
			    appFlowDay.setPackets(buf.getInt());
			    appFlowDay.setTimeStamp(timestamp);
			    appFlowDay.setStartTime(starttime);
			    appFlowDay.setApMac(getApMac());
			    appFlowList.add(appFlowDay);
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeApplicationFlowInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhAppFlowLog> getAppFlowList() {
		return appFlowList;
	}

	 
}
