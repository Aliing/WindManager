package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * get statistics data message type
 *@filename		BeGetStatisticEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-13 02:49:29
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history<br>
 * 2007-12-8 juyizhou Msg format changed,tableid->tableInfo
 */
public class BeGetStatisticEvent extends BeCommunicationEvent
{

	private static final long		serialVersionUID	= 1L;

	// get from communication module
//	private int						statsSerialNum;

	// /**
	// * key: tableId,value definition see BeCommunicationConstant<br>
	// * value: IfIndex list, if list is empty, get all table data.
	// */
	// private Map<Byte, List<Integer>> statsTableInfoMap;

	/**
	 * key: tableID<br>
	 * value: index information
	 */
	private Map<Byte, List<String>>	statsTableIndexMap;

	public BeGetStatisticEvent()
	{
		msgType = BeCommunicationConstant.MESSAGETYPE_GETSTATISTICREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 */
	public byte[] buildPacket()
		throws BeCommunicationEncodeException
	{
		if (apMac == null)
		{
			throw new BeCommunicationEncodeException("AP is a necessary field!");
		}

		// if (statsTableInfoMap == null || statsTableInfoMap.size() == 0)
		// {
		// throw new BeCommunicationEncodeException(
		// "statsTableInfoMap is a necessary field!");
		// }

		if (statsTableIndexMap == null || statsTableIndexMap.size() == 0)
		{
			throw new BeCommunicationEncodeException(
				"StatsTableIndexMap field value is needed!");
		}

		if (sequenceNum <= 0)
		{
			throw new BeCommunicationEncodeException(
				"sequenceNum is a necessary field!");
		}

		try
		{
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * statistic table 's length = 6 + 4 + 1 + tableinfo <br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int statsTableLen = 11 + 2 * statsTableIndexMap.size();
			for (Byte tableID : statsTableIndexMap.keySet())
			{
				if (tableID == BeCommunicationConstant.STATTABLE_AHASSOCIATION
						|| tableID == BeCommunicationConstant.STATTABLE_AHETHCLIENT)
				{
					statsTableLen += (10 * statsTableIndexMap.get(tableID)
						.size());
					continue;
				}

				statsTableLen += (4 * statsTableIndexMap.get(tableID).size());
			}

			int bufLength = apIdentifierLen + statsTableLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICTABLE);
			buf.putInt(statsTableLen - 6);
			buf.putInt(sequenceNum);
			buf.put((byte) (statsTableIndexMap.size()));
			if (statsTableIndexMap.size() > 0)
			{
				for (Byte tableID : statsTableIndexMap.keySet())
				{
					buf.put(tableID);
					if (tableID == BeCommunicationConstant.STATTABLE_AHASSOCIATION
							|| tableID == BeCommunicationConstant.STATTABLE_AHETHCLIENT)
					{
						// index of association is : ifindex & clientmac
						buf
							.put((byte) (statsTableIndexMap.get(tableID).size() / 2));
						
						boolean isOdd = true;
						for (String index : statsTableIndexMap.get(tableID))
						{
							if (isOdd)
							{
								buf.putInt(Integer.valueOf(index));
							}
							else
							{
								buf.put(AhEncoder.hex2bytes(index));
							}
							
							isOdd = !isOdd;
						}
					}
					else
					{
						// index of others is : ifindex
						buf.put((byte) statsTableIndexMap.get(tableID).size());
						
						for (String index : statsTableIndexMap.get(tableID))
						{
							buf.putInt(Integer.valueOf(index));
						}
					}
				}
			}
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException(
				"BeGetStatisticEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	protected void parsePacket(byte[] data)
		throws BeCommunicationDecodeException
	{
		try
		{
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining())
			{
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER)
				{
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf,
						AhDecoder.byte2int(macLen)).toUpperCase();
				}
				else
					if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR)
					{
						// check length valid
						if (msgLen != 1)
						{
							throw new BeCommunicationDecodeException(
								"Invalid message length in BeGetStatisticEvent");
						}

						result = buf.get();
					}
					else
					{
						throw new BeCommunicationDecodeException(
							"Invalid message element type in BeGetStatisticEvent, type value = "
								+ msgType);
						// DebugUtil.commonDebugWarn("Invalid message type in
						// BeGetStatisticEvent, type value = "
						// + msgType);
						//
						// return;
					}
			}
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeGetStatisticEvent.parsePacket() catch exception", e);
		}
	}

//	public int getStatsSerialNum()
//	{
//		return statsSerialNum;
//	}
//
//	public void setStatsSerialNum(int statsSerialNum)
//	{
//		this.statsSerialNum = statsSerialNum;
//	}

	public Map<Byte, List<String>> getStatsTableIndexMap()
	{
		return statsTableIndexMap;
	}

	public void setStatsTableIndexMap(Map<Byte, List<String>> statsTableIndexMap)
	{
		this.statsTableIndexMap = statsTableIndexMap;
		
		//for compatible
		this.getSimpleHiveAp();
		if(this.simpleHiveAp != null) {
			if(statsTableIndexMap.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION) != null) {
				if (NmsUtil.compareSoftwareVersion("3.5.2.0", simpleHiveAp.getSoftVer()) <= 0) {
					statsTableIndexMap.put(BeCommunicationConstant.STATTABLE_AHETHCLIENT, new ArrayList<String>());
				}
				else {
					statsTableIndexMap.remove(BeCommunicationConstant.STATTABLE_AHETHCLIENT);
				}
			}
			
		}
	}

	public void addStatsTableIndex(Byte tableID, List<String> indexInformation)
	{
		this.statsTableIndexMap.put(tableID, indexInformation);
	}

	// public Map<Byte, List<Integer>> getStatsTableInfoMap()
	// {
	// return statsTableInfoMap;
	// }
	//
	// public void setStatsTableInfoMap(Map<Byte, List<Integer>>
	// statsTableInfoMap)
	// {
	// this.statsTableInfoMap = statsTableInfoMap;
	// }

}