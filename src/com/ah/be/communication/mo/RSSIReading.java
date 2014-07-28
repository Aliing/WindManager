package com.ah.be.communication.mo;

/**
 * 
 *@filename		RSSIReading.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-25 05:40:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
public class RSSIReading {
	// reporting AP's MAC
	private String	apMac;

	// radio channel frequency
	private int		channelFrequency;

	// signal strength reading of the client
	private byte	signalStrength;

	public RSSIReading() {

	}

	public RSSIReading(String apMac, int channelFrequency, byte signalStrength) {
		this.apMac = apMac;
		this.channelFrequency = channelFrequency;
		this.signalStrength = signalStrength;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public byte getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(byte signalStrength) {
		this.signalStrength = signalStrength;
	}

	public int getChannelFrequency() {
		return channelFrequency;
	}

	public void setChannelFrequency(int channelFrequency) {
		this.channelFrequency = channelFrequency;
	}
}
