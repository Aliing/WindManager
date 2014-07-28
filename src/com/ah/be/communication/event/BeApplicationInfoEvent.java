package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * 
 *@filename		BeApplicationInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-12-2 11:42:44
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeApplicationInfoEvent extends BeCommunicationEvent {

	private byte	applicationType	= BeCommunicationConstant.CAPWAPCLIENTTYPE_HM;

	private String	hostname;

	private String	systemID;

	private String	version;

	private String	innerVersion;

	private int		apCount;

	private int		vhmCount;

	public BeApplicationInfoEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_APPLICATIONINFOREQ;
	}

	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (hostname == null) {
				hostname = "";
			}

			if (systemID == null) {
				systemID = "";
			}

			if (version == null) {
				version = "";
			}

			if (innerVersion == null) {
				innerVersion = "";
			}

			int bufLength = 6 + 13;
			bufLength += hostname.length();
			bufLength += systemID.length();
			bufLength += version.length();
			bufLength += innerVersion.length();
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APPLICATIONINFO);
			buf.putInt(bufLength - 6);

			buf.put(applicationType);
			buf.put((byte) hostname.length());
			buf.put(hostname.getBytes());
			buf.put((byte) systemID.length());
			buf.put(systemID.getBytes());
			buf.put((byte) version.length());
			buf.put(version.getBytes());
			buf.put((byte) innerVersion.length());
			buf.put(innerVersion.getBytes());
			buf.putInt(apCount);
			buf.putInt(vhmCount);

			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeApplicationInfoEvent.buildPacket() catch exception", e);
		}
	}

	@Override
	public synchronized void parsePacket() throws BeCommunicationDecodeException {
		super.parsePacket();
	}

	public int getApCount() {
		return apCount;
	}

	public void setApCount(int apCount) {
		this.apCount = apCount;
	}

	public byte getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(byte applicationType) {
		this.applicationType = applicationType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getInnerVersion() {
		return innerVersion;
	}

	public void setInnerVersion(String innerVersion) {
		this.innerVersion = innerVersion;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVhmCount() {
		return vhmCount;
	}

	public void setVhmCount(int vhmCount) {
		this.vhmCount = vhmCount;
	}
}
