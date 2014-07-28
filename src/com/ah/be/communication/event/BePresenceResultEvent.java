package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhPresence;
import com.ah.bo.performance.AhPresenceDeviceInfo;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BePresenceResultEvent extends BeCapwapClientResultEvent {
    AhPresence ahPresence;

	public BePresenceResultEvent() {
		super();
		ahPresence = new AhPresence();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PRESENCE;
	}

	/**
	 * Parse packet message to presence data
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			ahPresence.setVersionNumber(AhDecoder.byte2short(buf.get()));
			ahPresence.setMacAddress(AhDecoder.bytes2hex(buf, 6).toUpperCase());
			ahPresence.setSensorId(AhPresence.macAddressFormat(ahPresence.getMacAddress()));
			ahPresence.setSeqNumber(AhDecoder.int2long(buf.getInt()));
			ahPresence.setMessageType(0 == buf.get() ? "ht" : "hl");
			ahPresence.setTriggerType(buf.get());
			//ahPresence.setMacAddress(AhDecoder.bytes2hex(buf, 6).toUpperCase());

			List<AhPresenceDeviceInfo> deviceInfoList = new ArrayList<AhPresenceDeviceInfo>();
			int itemCount = AhDecoder.short2int(buf.getShort());
			for(int i = 0; i < itemCount; i++) {
				AhPresenceDeviceInfo deviceInfo = new AhPresenceDeviceInfo();

				int itemLength = AhDecoder.short2int(buf.getShort());
				int start = buf.position();

				deviceInfo.setStationIdEncrypt(AhPresence.macAddressFormat(AhDecoder.bytes2hex(buf, 6).toUpperCase()));
				deviceInfo.setBssIdEncrypt(AhPresence.macAddressFormat(AhDecoder.bytes2hex(buf, 6).toUpperCase()));
				deviceInfo.setStationOui(AhDecoder.bytes2hex(buf, 3).toUpperCase());
				deviceInfo.setIsAp(buf.get());
				deviceInfo.setFrameNumber(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setFirstFrameTimeStamp(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setLastFrameTimeStamp(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setFrequency(AhDecoder.short2int(buf.getShort()));
				deviceInfo.setIntervalSum(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setIntervalSquaredSum(buf.getLong());
				deviceInfo.setIntervalCubedSum(buf.getLong());
				deviceInfo.setMinInterval(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setMaxInterval(AhDecoder.int2long(buf.getInt()));
				deviceInfo.setSigStrength(buf.getInt());
				deviceInfo.setSigStrenghSquared(buf.getInt());
				deviceInfo.setSigStrenghCubed(buf.getInt());
				deviceInfo.setMinSigStrength(buf.get());
				deviceInfo.setMaxSigStrength(buf.get());
				deviceInfo.setFirstSigStrength(buf.get());
				deviceInfo.setLastSigStrength(buf.get());
				
		        if(itemLength > (buf.position()-start)) {	 
		            int sigStrenghSquaredHigh = buf.getInt();
		            int sigStrenghCubedHigh = buf.getInt();
		            deviceInfo.setSigStrenghSquared(AhDecoder.int2long(sigStrenghSquaredHigh, (int)deviceInfo.getSigStrenghSquared()));
		            deviceInfo.setSigStrenghCubed(AhDecoder.int2long(sigStrenghCubedHigh, (int)deviceInfo.getSigStrenghCubed()));
		        }

				deviceInfoList.add(deviceInfo);
				buf.position(start+ itemLength);
			}
			ahPresence.setDeviceInfoList(deviceInfoList);
		} catch(Exception e) {
			throw new BeCommunicationDecodeException(
					"BePresenceResultEvent.parsePacket() catch exception", e);
		}
	}
    public static void main(String[] args) {
         long l = AhDecoder.int2long(-1);
         l = l << 32;
         long low_long = AhDecoder.int2long(0);
         l = l | low_long;
         System.out.println("Value is " + l);
    }
        
	public AhPresence getAhPresence() {
		return ahPresence;
	}


}