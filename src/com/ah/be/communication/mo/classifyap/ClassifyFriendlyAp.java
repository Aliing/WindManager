package com.ah.be.communication.mo.classifyap;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;

import com.ah.util.coder.AhEncoder;

public class ClassifyFriendlyAp implements ClassifyBaseAp {

	public ClassifyFriendlyAp() {

	}
	
	public ClassifyFriendlyAp(String macAddress) {
		this.macAddress = macAddress;
	}

	public byte[] getBytesOfObject() {
		ByteBuffer buf = ByteBuffer.allocate(50);
		buf.putShort(getDataLength());
		buf.put(AhEncoder.hex2bytes(macAddress));
		
		buf.flip();
		
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	public short getLength() {
		return (short)(getBytesOfObject().length);
	}
	
	private short getDataLength() {
		if (StringUtils.isBlank(macAddress)) {
			return 0;
		}
		return (short)AhEncoder.hex2bytes(macAddress).length;
	}
	
	private String macAddress;

	public String getMacAddress() {
		if (StringUtils.isBlank(macAddress)) {
			return "";
		}
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof ClassifyFriendlyAp
				&& getMacAddress().equals(((ClassifyFriendlyAp)other).getMacAddress());
	}
	
	@Override
	public int hashCode() {
		return getMacAddress().hashCode();
	}
}
