package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCPUMemInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-3-30 10:27:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeCPUMemInfoEvent extends BeCapwapClientInfoEvent {

	//Integer part of usage of CPU * 10000
	private int cpuUsage1;
	
	//(Decimal part of usage of CPU*100)*1000
	private int cpuUsage2;
	
	//The unit is KB
	private int totalMem;
	
	//The unit is KB
	private int freeMem;
	
	//The unit is KB
	private int usedMem;
	
	private String dAMac;

	private String bDAMac;
	
	private String portalMac;
	
	public BeCPUMemInfoEvent() {
		super();
		queryType = BeCapwapClientInfoEvent.TYPE_CPUMEMQUERY;
	}
	
	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		try {
			/**
			 * AP identifier 's length = 6 + 1 + apMac.length()<br>
			 * query's length = 6 + 6
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 12;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_INFORMATIONQUERY);
			buf.putInt(6);
			buf.putShort(queryType);
			buf.putInt(0); // data length
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCPUMemInfoEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		super.parsePacket(data);
		
		if (infoData == null || infoData.length == 0) {
			return;
		}
		ByteBuffer buf = ByteBuffer.wrap(infoData);
		cpuUsage1 = buf.getInt();
		cpuUsage2 = buf.getInt();
		totalMem = buf.getInt();
		freeMem = buf.getInt();
		usedMem = buf.getInt();
		if(buf.position() < buf.limit()){
			dAMac = AhDecoder.bytes2hex(buf, 6);
			bDAMac = AhDecoder.bytes2hex(buf, 6);
			portalMac = AhDecoder.bytes2hex(buf, 6);
		}
	}
	
	public int getCpuUsage1() {
		return cpuUsage1;
	}

	public void setCpuUsage1(int cpuUsage1) {
		this.cpuUsage1 = cpuUsage1;
	}

	public int getCpuUsage2() {
		return cpuUsage2;
	}

	public void setCpuUsage2(int cpuUsage2) {
		this.cpuUsage2 = cpuUsage2;
	}

	public int getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(int freeMem) {
		this.freeMem = freeMem;
	}

	public int getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(int totalMem) {
		this.totalMem = totalMem;
	}

	public int getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(int usedMem) {
		this.usedMem = usedMem;
	}
	
	public String getdAMac() {
		return dAMac;
	}

	public void setdAMac(String dAMac) {
		this.dAMac = dAMac;
	}

	public String getbDAMac() {
		return bDAMac;
	}

	public void setbDAMac(String bDAMac) {
		this.bDAMac = bDAMac;
	}

	public String getPortalMac() {
		return portalMac;
	}

	public void setPortalMac(String portalMac) {
		this.portalMac = portalMac;
	}
	
}
