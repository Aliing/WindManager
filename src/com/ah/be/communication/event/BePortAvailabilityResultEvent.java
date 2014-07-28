package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BePortAvailabilityResultEvent extends BeCapwapClientResultEvent {
	
	List<AhPortAvailability> interfAvailability = null;
	
	public BePortAvailabilityResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY;
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
			interfAvailability = new ArrayList<AhPortAvailability>();
			
			// count
			short count = buf.getShort();
			for(int i = 0; i < count; i++){
				AhPortAvailability interfAvail = new AhPortAvailability();
				short length = buf.getShort();
				int beginPos = buf.position();
				interfAvail.setInterfType(buf.get());
				interfAvail.setInterfMode(buf.get());
				interfAvail.setInterfStatus(buf.get());
				interfAvail.setMac(apMac);
				byte len = buf.get();
				interfAvail.setInterfName(AhDecoder.bytes2String(buf, len));
				
				if ((buf.position() - beginPos) < length) {
					interfAvail.setWanipaddress(buf.getInt());
					interfAvail.setWannetmask(buf.getInt());
					interfAvail.setWanactive(buf.get());
				}

				interfAvailability.add(interfAvail);
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeInterfaceAvailabilityResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhPortAvailability> getInterfAvailability() {
		return interfAvailability;
	}

	public void setInterfAvailability(
			List<AhPortAvailability> interfAvailability) {
		this.interfAvailability = interfAvailability;
	}
}
