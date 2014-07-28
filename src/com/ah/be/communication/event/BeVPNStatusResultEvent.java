package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeVPNStatusResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-9 14:03:40
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeVPNStatusResultEvent extends BeCapwapClientResultEvent {

	public static byte			VPNTYPE_NOCONFIGURE	= 0;

	public static byte			VPNTYPE_SERVER		= 1;

	public static byte			VPNTYPE_CLIENT		= 2;

	private byte				vpnType;

	public static byte			STATUS_UP			= 1;

	public static byte			STATUS_DOWN			= 2;

	private byte				status;

	/**
	 * key: remote ID value: connect time stamp, milli-seconds unit.
	 */
	private Map<String, Long>	remoteNodeIDTimeMap;

	public BeVPNStatusResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VPNSTATUS;
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

			vpnType = buf.get();
			status = buf.get();

			short nodeNum = buf.getShort();
			remoteNodeIDTimeMap = new HashMap<String, Long>(nodeNum);
			for (int i = 0; i < nodeNum; i++) {
				buf.getShort(); // length
				String remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase();
				long connectTime = AhDecoder.int2long(buf.getInt());

				remoteNodeIDTimeMap.put(remoteID, connectTime * 1000);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeVPNStatusResultEvent.parsePacket() catch exception", e);
		}
	}

	public Map<String, Long> getRemoteNodeIDTimeMap() {
		return remoteNodeIDTimeMap;
	}

	public void setRemoteNodeIDTimeMap(Map<String, Long> remoteNodeIDTimeMap) {
		this.remoteNodeIDTimeMap = remoteNodeIDTimeMap;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getVpnType() {
		return vpnType;
	}

	public void setVpnType(byte vpnType) {
		this.vpnType = vpnType;
	}
}
