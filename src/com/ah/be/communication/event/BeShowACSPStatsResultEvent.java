package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

/**
 * 
 *@filename		BeShowACSPStatsResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-3-30 11:04:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeShowACSPStatsResultEvent extends BeCapwapClientResultEvent {

	private int channel;
	
	private String bssid;
	
	private int averageRSSI;
	
	//TODO
	
	public BeShowACSPStatsResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWACSPSTATS;
	}
	
	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
//			ByteBuffer buf = ByteBuffer.wrap(resultData);

//			wifiInterface = buf.get();
//			capturing = (buf.get() == CAPTURING_NOINPROGRESS) ? CAPTURING_NOINPROGRESS
//					: CAPTURING_INPROGRESS;
//			totalFramesCaptured =buf.getInt();
//			txFramesCaptured = buf.getInt();
//			rxFramesCaptured = buf.getInt();
//			byte len = buf.get();
//			saveFileName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
//			len = buf.get();
//			lastResultFileName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeShowCaptureStatusResultEvent.parsePacket() catch exception", e);
		}
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getAverageRSSI() {
		return averageRSSI;
	}

	public void setAverageRSSI(int averageRSSI) {
		this.averageRSSI = averageRSSI;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	
}
