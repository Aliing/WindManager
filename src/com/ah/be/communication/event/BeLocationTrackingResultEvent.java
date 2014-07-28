package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.mo.RSSIReading;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeLocationTrackingResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-25 03:27:27
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeLocationTrackingResultEvent extends BeCapwapClientResultEvent {

	public static final byte				ACKCODE_SUCCESS					= 0;

	public static final byte				ACKCODE_FAIL_GENERALERROR		= 1;

	public static final byte				ACKCODE_FAIL_UNKNOWNOPCODE		= 2;

	public static final byte				ACKCODE_FAIL_UNSUPPORTEDREQ		= 3;

	public static final byte				ACKCODE_FAIL_CORRUPTEDMESSAGE	= 4;

	private short							opCode;

	private Map<String, List<RSSIReading>>	clientRssiValuesMap				= new HashMap<String, List<RSSIReading>>();

	private byte							ackCode;

	private List<String>					unHandledMacList;

	private List<String>					unHandledOuiList;

	public BeLocationTrackingResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK;
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

			opCode = buf.getShort();
			buf.getShort(); // total length
			buf.getShort(); // tlvNumber

			if (opCode == BeLocationTrackingEvent.OPCODE_ACK) {
				short type = buf.getShort();
				if (type != BeLocationTrackingEvent.TLVTYPE_ACKCODE) {
					throw new BeCommunicationDecodeException(
							"ACK code TLV is necessary, packet format invalid.");
				}

				buf.getShort(); // tlv len
				ackCode = buf.get();

				while (buf.hasRemaining()) {
					type = buf.getShort();
					if (type == BeLocationTrackingEvent.TLVTYPE_CLIENTMAC) {
						buf.getShort(); // tlv len
						int macNumber = AhDecoder.byte2int(buf.get());
						unHandledMacList = new ArrayList<String>(macNumber);
						for (int i = 0; i < macNumber; i++) {
							unHandledMacList.add(AhDecoder.bytes2hex(buf, 6).toUpperCase());
						}
					} else if (type == BeLocationTrackingEvent.TLVTYPE_CLIENTOUI) {
						buf.getShort(); // tlv len
						int ouiNumber = AhDecoder.byte2int(buf.get());
						unHandledOuiList = new ArrayList<String>(ouiNumber);
						for (int i = 0; i < ouiNumber; i++) {
							unHandledOuiList.add(AhDecoder.bytes2hex(buf, 3).toUpperCase());
						}
					} else {
						throw new BeCommunicationDecodeException(
								"Parse ACK but tlv type is unknown type.");
					}
				}

			} else if (opCode == BeLocationTrackingEvent.OPCODE_RSSIREPORT) {
				List<RSSIReading> rssiList = null;
				while (buf.hasRemaining()) {
					short type = buf.getShort();
					buf.getShort();// len
					if (type == BeLocationTrackingEvent.TLVTYPE_CLIENTMAC) {
						buf.get(); // mac number
						String clientMac = AhDecoder.bytes2hex(buf, 6).toUpperCase();
						rssiList = new ArrayList<RSSIReading>();
						clientRssiValuesMap.put(clientMac, rssiList);
					} else if (type == BeLocationTrackingEvent.TLVTYPE_RSSIREADING) {
						String apMac = AhDecoder.bytes2hex(buf, 6).toUpperCase();
						int channelFreq = AhDecoder.short2int(buf.getShort());
						byte signalStrength = buf.get();
						rssiList.add(new RSSIReading(apMac, channelFreq, signalStrength));
					} else {
						throw new BeCommunicationDecodeException("Invalid tlv type " + type);
					}
				}
			} else {
				throw new BeCommunicationDecodeException("Invalid opCode " + opCode);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeLocationTrackingResultEvent.parsePacket() catch exception", e);
		}
	}

	public Map<String, List<RSSIReading>> getClientRssiValuesMap() {
		return clientRssiValuesMap;
	}

	public void setClientRssiValuesMap(Map<String, List<RSSIReading>> clientRssiValuesMap) {
		this.clientRssiValuesMap = clientRssiValuesMap;
	}

	public short getOpCode() {
		return opCode;
	}

	public void setOpCode(short opCode) {
		this.opCode = opCode;
	}

	public byte getAckCode() {
		return ackCode;
	}

	public void setAckCode(byte ackCode) {
		this.ackCode = ackCode;
	}

	public List<String> getUnHandledMacList() {
		return unHandledMacList;
	}

	public void setUnHandledMacList(List<String> unHandledMacList) {
		this.unHandledMacList = unHandledMacList;
	}

	public List<String> getUnHandledOuiList() {
		return unHandledOuiList;
	}

	public void setUnHandledOuiList(List<String> unHandledOuiList) {
		this.unHandledOuiList = unHandledOuiList;
	}
}
