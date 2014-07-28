package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.performance.XIfPK;
import com.ah.util.coder.AhDecoder;

/**
 * statics reslut message type
 *@filename		BeStatisticResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-13 07:56:34
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeStatisticResultEvent extends BeAPWTPEvent {

	private static final long		serialVersionUID	= 1L;

	// private int statsSerialNum;

	/**
	 * statistics table id<br>
	 * 1:AhNeighbor<br>
	 * 2:AhXIf<br>
	 * 3:AhAssociation<br>
	 * 4:AhRadioStates<br>
	 * 5:AhVifStats<br>
	 */
	/**
	 * key: statistics table id<br>
	 * value: statistics table row list
	 */
	private Map<Byte, List<HmBo>>	statsRowData		= new HashMap<Byte, List<HmBo>>();

	private String					apName;

	private String					apSerialNum;

	private final List<HmBo>		EMPTY_HMBO_LIST		= new ArrayList<HmBo>(0);

	/**
	 * ifindex defination
	 */
	public static final int			IFINDEX_MGT0		= 5;

	public static final int			IFINDEX_ETH0		= 2;

	private HmDomain				owner				= null;

	private HmTimeStamp				timeStamp			= null;

	/**
	 * Construct method
	 */
	public BeStatisticResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			byte[] buffer = getWtpMsgData();
			ByteBuffer buf = ByteBuffer.wrap(buffer);

			sequenceNum = buf.getInt();

			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}

			apName = simpleHiveAp.getHostname();
			apSerialNum = simpleHiveAp.getSerialNumber();

			owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
			
			// add milli-seconds for data unique
			long statTimeStamp = getMessageTimeStamp() + Calendar.getInstance().get(Calendar.MILLISECOND);
			timeStamp = new HmTimeStamp(statTimeStamp, getMessageTimeZone());

			List<HmBo> associateList = new ArrayList<HmBo>();
			while (buf.hasRemaining()) {
				byte statTabelID = buf.get();
				List<HmBo> rowList;
				switch (statTabelID) {
				case BeCommunicationConstant.STATTABLE_AHNEIGHBOR:
					rowList = parseNeighbor(buf);
					break;

				case BeCommunicationConstant.STATTABLE_AHXIF:
					rowList = parseXIf(buf);
					break;

				case BeCommunicationConstant.STATTABLE_AHASSOCIATION:
					rowList = parseAssociation(buf);
					associateList.addAll(rowList);
					break;

				case BeCommunicationConstant.STATTABLE_AHRADIOSTATES:
					rowList = parseRadioStats(buf);
					break;

				case BeCommunicationConstant.STATTABLE_AHVIFSTATS:
					rowList = parseVIfStats(buf);
					break;

				case BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE:
					rowList = parseRadioAttribute(buf);
					break;
					
				case BeCommunicationConstant.STATTABLE_AHETHCLIENT:
					rowList = parseAssociation(buf);
					associateList.addAll(rowList);

				default:
//					rowList = EMPTY_HMBO_LIST;
//					return;
					continue;
				}

				statsRowData.put(statTabelID, rowList);
			}
			statsRowData.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, associateList);
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeStatisticResultEvent.parsePacket() catch exception", e);
		}
	}

	/**
	 * parse AhNeightbor table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseNeighbor(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhNeighbor bo = new AhNeighbor();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			bo.setTimeStamp(timeStamp);
			bo.setApName(apName);
			bo.setApMac(apMac);
			bo.setApSerialNumber(apSerialNum);

			bo.setIfIndex(buf.getInt());
			bo.setNeighborAPID(AhDecoder.bytes2hex(buf, 6));
			bo.setLinkCost(AhDecoder.int2long(buf.getInt()));
			bo.setRssi(buf.getInt());
			bo.setLinkUpTime(AhDecoder.int2long(buf.getInt()));
			bo.setLinkType(buf.get());
			bo.setRxDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxDataOctets(AhDecoder.int2long(buf.getInt()));
			bo.setRxMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxUnicastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxMulticastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxDataOctets(AhDecoder.int2long(buf.getInt()));
			bo.setTxUnicastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxMulticastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxBroadcastFrames(AhDecoder.int2long(buf.getInt()));

			if ((buf.position() - start) < rowLen) {
				bo.setTxBeDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxBgDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxViDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxVoDataFrames(AhDecoder.int2long(buf.getInt()));
			}

			rowList.add(bo);

			// check rowNum
			if (--rowsNum == 0) {
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseNeighbor(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse AhXIf table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseXIf(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhXIf bo = new AhXIf();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			XIfPK pk = new XIfPK();
//			pk.setTimeZone(timeStamp.getTimeZone());
			pk.setStatTime(timeStamp.getTime());
			pk.setApName(apName);
			int ifindex = buf.getInt();
			pk.setIfIndex(ifindex);
			bo.setXifpk(pk);
			bo.setApMac(getApMac());
			bo.setApSerialNumber(apSerialNum);

			bo.setIfPromiscuous(buf.get());
			bo.setIfType(buf.get());
			bo.setIfMode(buf.get());
			byte ifNameLen = buf.get();
			bo.setIfName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(ifNameLen)).trim());
			byte ssidNameLen = buf.get();
			bo.setSsidName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(ssidNameLen)).trim());

			if ((buf.position() - start) < rowLen) {
				bo.setIfConfMode(buf.get());
				bo.setIfAdminStatus(buf.get());
				bo.setIfOperStatus(buf.get());
			}
			
			if ((buf.position() - start) < rowLen) {
				bo.setBssid(AhDecoder.bytes2hex(buf, 6).toUpperCase());
			}

			rowList.add(bo);

			// check rowNum
			if (--rowsNum == 0) {
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseXIf(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse AhAssociation table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseAssociation(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhAssociation bo = new AhAssociation();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			bo.setTimeStamp(timeStamp);
			bo.setApName(apName);
			bo.setApMac(getApMac());
			bo.setApSerialNumber(apSerialNum);

			bo.setIfIndex(buf.getInt());
			bo.setClientMac(AhDecoder.bytes2hex(buf, 6));
			bo.setSNR((short)buf.getInt());
			bo.setClientLinkUptime(AhDecoder.int2long(buf.getInt()));
			bo.setClientAuthMethod(buf.get());
			bo.setClientEncryptionMethod(buf.get());
			bo.setClientMACProtocol(buf.get());
			bo.setClientCWPUsed(buf.get());
			bo.setClientVLAN(buf.getInt());
			bo.setClientUserProfId(buf.getInt());
			bo.setClientChannel(buf.getInt());
			bo.setClientLastTxRate(buf.getInt());
			bo.setClientLastRxRate(buf.getInt());
			bo.setClientRxDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxDataOctets(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxUnicastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxMulticastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientRxMICFailures(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxDataOctets(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxUnicastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxMulticastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientTxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
			bo.setClientIP(AhDecoder.int2IP(buf.getInt()));
			byte hostNameLen = buf.get();
			bo.setClientHostname(AhDecoder.bytes2String(buf, AhDecoder.byte2int(hostNameLen))
					.trim());
			byte ssidLen = buf.get();
			bo.setClientSSID(AhDecoder.bytes2String(buf, AhDecoder.byte2int(ssidLen)).trim());
			byte userNameLen = buf.get();
			bo.setClientUsername(AhDecoder.bytes2String(buf, AhDecoder.byte2int(userNameLen))
					.trim());

			if ((buf.position() - start) < rowLen) {
				// add these fields in 3.1
				bo.setClientTxBeDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setClientTxBgDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setClientTxViDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setClientTxVoDataFrames(AhDecoder.int2long(buf.getInt()));
			}
			if ((buf.position() - start) < rowLen) {
				// add these fields in 3.2
				// airtime value unit is us, let's convert to 'ms'
				bo.setClientRxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
				bo.setClientTxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
				bo.setClientBSSID(AhDecoder.bytes2hex(buf, 6));
				bo.setClientAssociateTime(AhDecoder.int2long(buf.getInt()));
					
			}
			if ((buf.position() - start) < rowLen) {
				byte len = buf.get();
				bo.setIfName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len))
						.trim());
			}
			if((buf.position() - start) < rowLen) {
				byte len = buf.get();
				bo.setClientOsInfo(AhDecoder.bytes2StringForUtf8(buf, AhDecoder.byte2int(len))
						.trim());
			}
			if((buf.position() - start) < rowLen) {
				bo.setIpNetworkConnectivityScore(buf.get());
				bo.setApplicationHealthScore(buf.get());
				bo.setSlaConnectScore(buf.get());
				bo.setOverallClientHealthScore(buf.get());
			}
			
			if((buf.position() - start) < rowLen) {
				byte len = buf.get();
				bo.setOs_option55(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len))
						.trim());
			}
			
			if((buf.position() - start) < rowLen) {
				byte len = buf.get();
				bo.setUserProfileName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len))
						.trim());
			}
			
			if((buf.position() - start) < rowLen) {
				bo.setClientRSSI(buf.getShort());
			} else {
				//for compatibility
				bo.setClientRSSI(bo.getSNR()-95);
			}

			if((buf.position() - start) < rowLen) {
				bo.setClientMacBasedAuthUsed(buf.get());
			}
			
			if((buf.position() - start) < rowLen) {
				bo.setManagedStatus(buf.getShort());
			}
			
			rowList.add(bo);
			buf.position(start + rowLen);
			
			// check rowNum
			if (--rowsNum == 0) {
				// special code for client mac protocol
				if (NmsUtil.compareSoftwareVersion("3.5.0.0", simpleHiveAp.getSoftVer()) <= 0) {
					for (HmBo hmBo : rowList) {
						AhAssociation association = (AhAssociation)hmBo;
						byte clientMacProtocol = association.getClientMACProtocol();
						if (clientMacProtocol == 3) {
							clientMacProtocol = AhAssociation.CLIENTMACPROTOCOL_NAMODE;
						} else if (clientMacProtocol == 4) {
							clientMacProtocol = AhAssociation.CLIENTMACPROTOCOL_NGMODE;
						}
						association.setClientMACProtocol(clientMacProtocol);
					}
				}
				
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseAssociation(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse AhRadioStats table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseRadioStats(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhRadioStats bo = new AhRadioStats();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			XIfPK pk = new XIfPK();
//			pk.setTimeZone(timeStamp.getTimeZone());
			pk.setStatTime(timeStamp.getTime());
			pk.setApName(apName);
			pk.setIfIndex(buf.getInt());
			bo.setXifpk(pk);
			bo.setApMac(getApMac());
			bo.setApSerialNumber(apSerialNum);

			bo.setRadioTxDataFrames(AhDecoder.int2long(buf.getInt()));

			bo.setRadioTxNonBeaconMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxUnicastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxMulticastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxBroadcastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxBeaconFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxTotalRetries(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxTotalFramesDropped(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxTotalFrameErrors(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxFEForExcessiveHWRetries(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxTotalDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxUnicastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxMulticastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxBroadcastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxMgtFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRadioRxTotalFrameDropped(AhDecoder.int2long(buf.getInt()));

			if ((buf.position() - start) < rowLen) {
				bo.setRadioTxBeDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setRadioTxBgDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setRadioTxViDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setRadioTxVoDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setRadioTXRTSFailures(AhDecoder.int2long(buf.getInt()));
			}

			if ((buf.position() - start) < rowLen) {
				bo.setRadioRxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
				bo.setRadioTxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
			}

			if ((buf.position() - start) < rowLen) {
				bo.setBandWidth(buf.getInt());
			}

			rowList.add(bo);

			// check rowNum
			if (--rowsNum == 0) {
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseRadioStats(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse AhVIfStats table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseVIfStats(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhVIfStats bo = new AhVIfStats();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			XIfPK pk = new XIfPK();
//			pk.setTimeZone(timeStamp.getTimeZone());
			pk.setStatTime(timeStamp.getTime());
			pk.setApName(apName);
			pk.setIfIndex(buf.getInt());
			bo.setXifpk(pk);
			bo.setApMac(getApMac());
			bo.setApSerialNumber(apSerialNum);

			bo.setRxVIfDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxVIfUnicastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxVIfMulticastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxVIfBroadcastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxVIfErrorFrames(AhDecoder.int2long(buf.getInt()));
			bo.setRxVIfDroppedFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfUnicastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfMulticastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfBroadcastDataFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfErrorFrames(AhDecoder.int2long(buf.getInt()));
			bo.setTxVIfDroppedFrames(AhDecoder.int2long(buf.getInt()));

			if ((buf.position() - start) < rowLen) {
				bo.setTxVIfBeDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxVIfBgDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxVIfViDataFrames(AhDecoder.int2long(buf.getInt()));
				bo.setTxVIfVoDataFrames(AhDecoder.int2long(buf.getInt()));
			}

			if ((buf.position() - start) < rowLen) {
				bo.setRxVifAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
				bo.setTxVifAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
			}

			rowList.add(bo);

			// check rowNum
			if (--rowsNum == 0) {
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseVIfStats(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse AhRadioAttribute table data
	 * 
	 * @param buf -
	 * @return List<HmBo>
	 */
	private List<HmBo> parseRadioAttribute(ByteBuffer buf) {
		short rowsNum = buf.getShort();
		if (rowsNum <= 0) {
			return EMPTY_HMBO_LIST;
		}
		List<HmBo> rowList = new ArrayList<HmBo>(rowsNum);
		while (buf.hasRemaining()) {
			short rowLen = buf.getShort();
			int start = buf.position();
			AhRadioAttribute bo = new AhRadioAttribute();

			// Don't make this null because the database will throw an exception
			bo.setOwner(owner);

			XIfPK pk = new XIfPK();
//			pk.setTimeZone(timeStamp.getTimeZone());
			pk.setStatTime(timeStamp.getTime());
			pk.setApName(apName);
			pk.setIfIndex(buf.getInt());
			bo.setXifpk(pk);
			bo.setApMac(getApMac());
			bo.setApSerialNumber(apSerialNum);

			bo.setRadioChannel(AhDecoder.int2long(buf.getInt()));
			bo.setRadioTxPower(AhDecoder.int2long(buf.getInt()));
			bo.setRadioNoiseFloor(buf.getInt());
			
			if ((buf.position() - start) < rowLen) {
				bo.setBeaconInterval(buf.getInt());
			}
			
			if ((buf.position() - start) < rowLen) {
				bo.setEirp(buf.getFloat());
			} else {
				//set default eirp if ap has no this value
				bo.setEirp(bo.getRadioTxPower());
			}
			
			if ((buf.position() - start) < rowLen) {
				bo.setRadioType(Integer.valueOf(buf.getInt()).shortValue());
			} else {
				bo.setRadioType(AhRadioAttribute.RADIO_TYPE_INVALID);
			}
			
			rowList.add(bo);

			// check rowNum
			if (--rowsNum == 0) {
				return rowList;
			}
		}

		DebugUtil
				.commonDebugWarn("BeStatisticResultEvent.parseRadioAttribute(): Rows total number is not right, rows in packet remain "
						+ rowsNum + " row data");
		return EMPTY_HMBO_LIST;
	}

	/**
	 * parse statistics serial number
	 * 
	 * @return if catch decode exception, return 0
	 */
	public int parseStatsSerialNum() {
		// try
		// {
		// super.parsePacket(BeCommunicationMessageData.wrap(getPacket()));
		// }
		// catch (Exception e)
		// {
		// DebugUtil
		// .commonDebugWarn("BeStatisticResult.isValidMessage(): decode exception!");
		// return 0;
		// }
		//
		// byte[] buffer = getWtpMsgData().getMessage();
		// ByteBuffer buf = ByteBuffer.wrap(buffer);
		//
		// sequenceNum = buf.getInt();

		try {
			parsePacket();
		} catch (Exception e) {
			DebugUtil.commonDebugWarn("BeStatisticResult.parseStatsSerialNum(): decode exception!");
			return 0;
		}

		return sequenceNum;
	}

	// public int getStatsSerialNum()
	// {
	// return statsSerialNum;
	// }
	//
	// public void setStatsSerialNum(int statsSerialNum)
	// {
	// this.statsSerialNum = statsSerialNum;
	// }

	public Map<Byte, List<HmBo>> getStatsRowData() {
		return statsRowData;
	}

	public void setStatsRowData(Map<Byte, List<HmBo>> statsRowData) {
		this.statsRowData = statsRowData;
	}

	@Override
	// we use simple ap in this class, so let's override this function, prevent
	// NPE
	public HiveAp getAp() {
		if (ap == null) {
			List<HiveAp> apList = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams(
					"macAddress", simpleHiveAp.getMacAddress()));
			if (!apList.isEmpty()) {
				ap = apList.get(0);
			}
		}

		return ap;
	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

}