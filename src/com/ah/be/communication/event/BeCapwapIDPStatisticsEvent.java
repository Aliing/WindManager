package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.hiveap.Idp;
import com.ah.util.coder.AhDecoder;

/**
 * IDP STATISTICS message element type
 *@filename		BeCapwapIDPStatisticsEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:29:47
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapIDPStatisticsEvent extends BeAPWTPEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * <p>
	 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | IDP
	 * Msg Type| Data Length (2 byte) |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Single IDP Message Length | Remote Id
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ Remote
	 * Id | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * If Index | Remove Flag | IDP Type | Channel |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | RSSI
	 * |In Network Flag| Station Type | Station Data
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ Station
	 * Data | Compliance | SSID Length |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * SSID... +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 */

	private byte				idpMsgType;

//	private int					idpSequenceNum;

	private List<Idp>	idpDtoList			= new ArrayList<Idp>();

	public BeCapwapIDPStatisticsEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_IDPSTATISTICS;
	}

	/**
	 * parse packet message to event data
	 *
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
		throws BeCommunicationDecodeException
	{
		try
		{
			super.parsePacket(data);
			byte[] buffer = getWtpMsgData();
			ByteBuffer buf = ByteBuffer.wrap(buffer);
			sequenceNum = buf.getInt();
			idpMsgType = buf.get();
			short msgLen = buf.getShort();
			if (msgLen == 0)
			{
				return;
			}
			int msgLen_int = AhDecoder.short2int(msgLen);
			while (buf.hasRemaining())
			{
				Idp idp = new Idp();

				short singleIdplen = buf.getShort();
				idp.setIfMacAddress(AhDecoder.bytes2hex(buf, 6));
				idp.setIfIndex(buf.get());
				idp.setRemovedFlag(buf.get());
				idp.setIdpType(buf.get());
				idp.setChannel(AhDecoder.byte2short(buf.get()));
				idp.setRssi(buf.get());
				idp.setInNetworkFlag(buf.get());
				idp.setStationType(buf.get());
				idp.setStationData(buf.getShort());
				idp.setCompliance(buf.getShort());
				byte ssidLen = buf.get();
				idp.setSsid(AhDecoder.bytes2String(buf, AhDecoder
					.byte2int(ssidLen)));

				// attention: singleIdplen include 2 bytes of field itself
				if (singleIdplen != 20 + AhDecoder.byte2int(ssidLen))
				{
					DebugUtil
						.commonDebugWarn("BeCapwapIDPStatisticsEvent.parsePacket(): single IDP length is not equal with real data length!");
					throw new BeCommunicationDecodeException(
						"Invalid single IDP length!");
				}

				msgLen_int -= singleIdplen;
				idp.setReportTime(new HmTimeStamp(getMessageTimeStamp(), getMessageTimeZone()));
				idpDtoList.add(idp);
			}
			if (msgLen_int > 0)
			{
				DebugUtil
					.commonDebugWarn("BeCapwapIDPStatisticsEvent.parsePacket(): IDP data has remainings.");
				throw new BeCommunicationDecodeException(
					"Invalid message length!");
			}
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeCapwapIDPStatisticsEvent.parsePacket() catch exception", e);
		}
	}

	public List<Idp> getIdpDtoList()
	{
		return idpDtoList;
	}

	public void setIdpDtoList(List<Idp> idpDtoList)
	{
		this.idpDtoList = idpDtoList;
	}

	public byte getIdpMsgType()
	{
		return idpMsgType;
	}

	public void setIdpMsgType(byte idpMsgType)
	{
		this.idpMsgType = idpMsgType;
	}
//
//	public int getIdpSequenceNum()
//	{
//		return idpSequenceNum;
//	}
//
//	public void setIdpSequenceNum(int idpSequenceNum)
//	{
//		this.idpSequenceNum = idpSequenceNum;
//	}

}