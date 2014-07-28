package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BePSEStatusResultEvent extends BeCapwapClientResultEvent {
	
	private List<AhPSEStatus> pseStatusList = null;
	
	private float totalPower = 0.0f;
	
	private float powerUsed = 0.0f;
	
	public BePSEStatusResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PSE_STATUS;
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
			pseStatusList = new ArrayList<AhPSEStatus>();
			
			// total power
			this.totalPower = buf.getFloat();
			// power used
			this.powerUsed = buf.getFloat();
			// count
			short count = buf.getShort();
			for(int i = 0; i < count; i++) {
				AhPSEStatus pseStatus = new AhPSEStatus();
				short length = buf.getShort();
				int beginPos = buf.position();
				
				pseStatus.setStatus(buf.get());
				pseStatus.setPower(buf.getFloat());
				pseStatus.setPdType(buf.get());
				pseStatus.setPdClass(buf.get());
				pseStatus.setMac(apMac);
				byte len = buf.get();
				pseStatus.setInterfName(AhDecoder.bytes2String(buf, len));
				
				if((buf.position() - beginPos) < length) {
					pseStatus.setPowerCutoffPriority(buf.get());
				}

				pseStatusList.add(pseStatus);
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BePSEStatusResultEvent.parsePacket() catch exception", e);
		}
	}
	
	public List<AhPSEStatus> getPseStatusList() {
		return pseStatusList;
	}

	public void setPseStatusList(List<AhPSEStatus> pseStatusList) {
		this.pseStatusList = pseStatusList;
	}

	public float getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(float totalPower) {
		this.totalPower = totalPower;
	}

	public float getPowerUsed() {
		return powerUsed;
	}

	public void setPowerUsed(float powerUsed) {
		this.powerUsed = powerUsed;
	}
	
}
