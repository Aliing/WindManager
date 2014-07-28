package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeDHCPProbeResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-24 03:14:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeVLANProbeResultEvent extends BeCapwapClientResultEvent {

	private boolean isComplete;
	
	private short	vlanID;

	private String	ipAddress;

	private short	maskLen;

	private String	defaultGateway;

	private String	dns;

	private String	description;

	public BeVLANProbeResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE;
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
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			isComplete = (buf.getShort() == 1);
			vlanID = buf.getShort();
			ipAddress = AhDecoder.int2IP(buf.getInt());
			maskLen = buf.getShort();
			defaultGateway = AhDecoder.int2IP(buf.getInt());
			dns = AhDecoder.int2IP(buf.getInt());
			short len = buf.getShort();
			description = AhDecoder.bytes2String(buf, AhDecoder.short2int(len));

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeVLANProbeResultEvent.parsePacket() catch exception", e);
		}
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public short getMaskLen() {
		return maskLen;
	}

	public void setMaskLen(short maskLen) {
		this.maskLen = maskLen;
	}

	public short getVlanID() {
		return vlanID;
	}

	public void setVlanID(short vlanID) {
		this.vlanID = vlanID;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}
}
