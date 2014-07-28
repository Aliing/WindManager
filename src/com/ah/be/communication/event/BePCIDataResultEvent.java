package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BePCIDataResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-11 10:15:20
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BePCIDataResultEvent extends BeCapwapClientResultEvent {

	private String	nodeID;

	private short	alertCode;

	private long	violationCounter;

	private String	srcObject;

	private String	destObject;

	private String	reportSystem;

	public BePCIDataResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PCIDATA;
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

			nodeID = AhDecoder.bytes2hex(buf,6).toUpperCase();
			alertCode = buf.getShort();
			violationCounter = AhDecoder.int2long(buf.getInt());
			short len = buf.getShort();
			srcObject = AhDecoder.bytes2String(buf, len);
			len = buf.getShort();
			destObject = AhDecoder.bytes2String(buf, len);
			len = buf.getShort();
			reportSystem = AhDecoder.bytes2String(buf, len);

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BePCIDataResultEvent.parsePacket() catch exception", e);
		}
	}

	public short getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(short alertCode) {
		this.alertCode = alertCode;
	}

	public String getDestObject() {
		return destObject;
	}

	public void setDestObject(String destObject) {
		this.destObject = destObject;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getReportSystem() {
		return reportSystem;
	}

	public void setReportSystem(String reportSystem) {
		this.reportSystem = reportSystem;
	}

	public String getSrcObject() {
		return srcObject;
	}

	public void setSrcObject(String srcObject) {
		this.srcObject = srcObject;
	}

	public long getViolationCounter() {
		return violationCounter;
	}

	public void setViolationCounter(long violationCounter) {
		this.violationCounter = violationCounter;
	}
}
