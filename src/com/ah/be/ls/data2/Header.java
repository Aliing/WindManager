package com.ah.be.ls.data2;

public class Header {
	private byte packetType;

	private int length;

	private byte protocolVersion;

	private boolean secretFlag;

	public byte getType() {
		return packetType;
	}

	public void setPacketType(byte packetType) {
		this.packetType = packetType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public boolean isSecretFlag() {
		return secretFlag;
	}

	public void setSecretFlag(boolean secretFlag) {
		this.secretFlag = secretFlag;
	}

}
