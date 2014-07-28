package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * This event for request information from ap, but data will forward by AP later through event.<br>
 *@filename		BeCapwapClientEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-12-5 03:06:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapClientEvent extends BeCommunicationEvent {

	/**
	 * definination see as BeCommunicationConstant
	 */
	protected short				queryType;

	public static final byte	FLAG_AUTODELETE						= 0;

	public static final byte	FLAG_MANUALDELETE					= 1;

	protected byte				flag								= FLAG_AUTODELETE;

	public static final int		DEBUGRESULT_SUCCESSFUL				= 0;

	public static final int		DEBUGRESULT_FAILED_COOKIETOOMANY	= -999;

	public static final int		DEBUGRESULT_FAILED_MACTOOMANY		= -998;

	public static final int		DEBUGRESULT_FAILED_VLANPROBESTARTED	= -997;

	protected int				debugResult;

	public BeCapwapClientEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTREQ;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					result = buf.get();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_EVENTQUERYRESULT) {
					if (msgLen > 0) {
						queryType = buf.getShort();
						debugResult = buf.getInt();
					}
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeCapwapClientEvent, type value = "
									+ msgType);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapClientEvent.parsePacket() catch exception", e);
		}
	}

	public short getQueryType() {
		return queryType;
	}

	public void setQueryType(short queryType) {
		this.queryType = queryType;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public int getDebugResult() {
		return debugResult;
	}

	public void setDebugResult(int debugResult) {
		this.debugResult = debugResult;
	}

}
