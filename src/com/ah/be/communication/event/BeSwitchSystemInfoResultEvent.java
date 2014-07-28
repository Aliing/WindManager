package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

@SuppressWarnings("serial")
public class BeSwitchSystemInfoResultEvent extends BeCapwapClientResultEvent {
	
	private float	systemTemperature;
	
	private byte[]	fanStateArray;
	
	private byte	STPMode;
	
	private byte	STPState;
	
	private float	utilization;
	
	private byte	powerStatus;

	public BeSwitchSystemInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO;
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
			
			systemTemperature = (float)buf.getInt()/1000;
			int fanNumber = buf.get();
			fanStateArray = new byte[fanNumber];
			for(int i = 0; i < fanNumber; i++) {
				fanStateArray[i] = buf.get();
			}
			STPMode = buf.get();
			STPState = buf.get();
			
			if(buf.hasRemaining()) {
				utilization = (float)buf.getShort()/100;
			}
			
			if(buf.hasRemaining()) {
				powerStatus = buf.get();
			}
			
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSwitchSystemInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public float getSystemTemperature() {
		return systemTemperature;
	}

	public byte[] getFanStateArray() {
		return fanStateArray;
	}

	public byte getSTPMode() {
		return STPMode;
	}

	public byte getSTPState() {
		return STPState;
	}

	public float getUtilization() {
		return utilization;
	}

	public byte getPowerStatus() {
		return powerStatus;
	}

}
