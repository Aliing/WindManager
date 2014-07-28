package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeInterferenceMapResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-5-4 03:05:48
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeInterferenceMapResultEvent extends BeCapwapClientResultEvent {

	private static final short			TLVTYPE_RADIOINFO			= 1;

	private static final short			TLVTYPE_INTERFERENCESTATS	= 2;

	private static final short			TLVTYPE_ACSPNEIGHBOR		= 3;

	private List<AhInterferenceStats>	interferenceStatsList		= new ArrayList<AhInterferenceStats>();

	private List<AhACSPNeighbor>		neighborList				= new ArrayList<AhACSPNeighbor>();

	public BeInterferenceMapResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP;
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

			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(
					simpleHiveAp.getDomainId());
			String apName = simpleHiveAp.getHostname();
			
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			buf.getShort(); //tlv number
			HmTimeStamp timeStamp = new HmTimeStamp(getMessageTimeStamp(), getMessageTimeZone());
			int ifIndex = 0;
			String ifName = "";

			while (buf.hasRemaining()) {

				short tlvType = buf.getShort();
				buf.getShort(); //tlv length
				buf.getShort(); //tlv version
				if (tlvType == TLVTYPE_RADIOINFO) {
					ifIndex = buf.getInt();
					byte len = buf.get();
					ifName = AhDecoder.bytes2String(buf, len);
				} else if (tlvType == TLVTYPE_INTERFERENCESTATS) {
					AhInterferenceStats interferenceStats = new AhInterferenceStats();
					interferenceStats.setApMac(apMac);
					interferenceStats.setTimeStamp(timeStamp);
					interferenceStats.setIfIndex(ifIndex);
					interferenceStats.setIfName(ifName);
					interferenceStats.setOwner(owner);
					interferenceStats.setApName(apName);

					interferenceStats.setChannelNumber((short)AhDecoder.byte2int(buf.get()));
					interferenceStats.setAverageTXCU(buf.get());
					interferenceStats.setAverageRXCU(buf.get());
					interferenceStats.setAverageInterferenceCU(buf.get());
					interferenceStats.setAverageNoiseFloor(buf.get());
					interferenceStats.setShortTermTXCU(buf.get());
					interferenceStats.setShortTermRXCU(buf.get());
					interferenceStats.setShortTermInterferenceCU(buf.get());
					interferenceStats.setShortTermNoiseFloor(buf.get());
					interferenceStats.setSnapShotTXCU(buf.get());
					interferenceStats.setSnapShotRXCU(buf.get());
					interferenceStats.setSnapShotInterferenceCU(buf.get());
					interferenceStats.setSnapShotNoiseFloor(buf.get());
					interferenceStats.setCrcError(buf.get());
					interferenceStats.setInterferenceCUThreshold(buf.get());
					interferenceStats.setCrcErrorRateThreshold(buf.get());
					interferenceStats.setSeverity(buf.get());
					
					interferenceStatsList.add(interferenceStats);
				} else if (tlvType == TLVTYPE_ACSPNEIGHBOR) {
					AhACSPNeighbor neighbor = new AhACSPNeighbor();
					neighbor.setApMac(apMac);
					neighbor.setTimeStamp(timeStamp);
					neighbor.setIfIndex(ifIndex);
					neighbor.setOwner(owner);

					neighbor.setBssid(AhDecoder.bytes2hex(buf, 6).toUpperCase());
					neighbor.setNeighborMac(AhDecoder.bytes2hex(buf, 6).toUpperCase());
					neighbor.setNeighborRadioMac(AhDecoder.bytes2hex(buf, 6).toUpperCase());
					neighbor.setLastSeen(AhDecoder.int2long(buf.getInt()) * 1000);
					neighbor.setChannelNumber(AhDecoder.byte2int(buf.get()));
					neighbor.setTxPower(buf.get());
					neighbor.setRssi(buf.get());
					byte len = buf.get();
					neighbor.setSsid(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)));

					neighborList.add(neighbor);
				}
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeInterferenceMapResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhACSPNeighbor> getNeighborList() {
		return neighborList;
	}

	public void setNeighborList(List<AhACSPNeighbor> neighborList) {
		this.neighborList = neighborList;
	}

	public List<AhInterferenceStats> getInterferenceStatsList() {
		return interferenceStatsList;
	}

	public void setInterferenceStatsList(List<AhInterferenceStats> interferenceStatsList) {
		this.interferenceStatsList = interferenceStatsList;
	}
}
