package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * LAYER3 ROAMING message element type
 *@filename		BeCapwapL3RoamingEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:30:21
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeCapwapL3RoamingEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * Layer3 roaming configuration. 8 9 0 1 2 3 4 5 6 7 8 9 0 1 0 1 2 3 0 1 2 3
	 * 4 5 6 7 8 9 0 1 2 3 4 5 6 7
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Number of Include/Exclude Neighbor |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Neighbor Type(Include[0x400]/Exclude[0x800])...|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Neighbor Ip Address... |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Neighbor Netmask... |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */

	List<NeighborDto>	includeNeighborList;

	List<NeighborDto>	excludeNeighborList;

	public List<NeighborDto> getExcludeNeighborList()
	{
		return excludeNeighborList;
	}

	public void setExcludeNeighborList(List<NeighborDto> excludeNeighborList)
	{
		this.excludeNeighborList = excludeNeighborList;
	}

	public List<NeighborDto> getIncludeNeighborList()
	{
		return includeNeighborList;
	}

	public void setIncludeNeighborList(List<NeighborDto> includeNeighborList)
	{
		this.includeNeighborList = includeNeighborList;
	}

	/**
	 * build event data to packet message
	 *
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket()
		throws BeCommunicationEncodeException
	{
		try
		{
			int neighborNum = includeNeighborList == null ? 0
				: includeNeighborList.size();
			neighborNum += excludeNeighborList == null ? 0
				: excludeNeighborList.size();
			if (neighborNum == 0)
			{
				throw new BeCommunicationEncodeException(
					"includeNeighborList or excludeNeighborList are required!");
			}
			int bufLength = 4 + neighborNum * 12;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			buf.putInt(neighborNum);
			for (NeighborDto dto : includeNeighborList)
			{
				buf.putInt(BeCommunicationConstant.NEIGHBORFLAG_INCLUDE);
				buf.putInt(dto.getIpAddr());
				buf.putInt(dto.getNetMask());
			}
			for (NeighborDto dto : excludeNeighborList)
			{
				buf.putInt(BeCommunicationConstant.NEIGHBORFLAG_EXCLUDE);
				buf.putInt(dto.getIpAddr());
				buf.putInt(dto.getNetMask());
			}
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException("BeCapwapL3RoamingEvent.buildPacket() catch exception",e);
		}		
	}

	class NeighborDto
	{
		private int	ipAddr;

		private int	netMask;

		public int getIpAddr()
		{
			return ipAddr;
		}

		public void setIpAddr(int ipAddr)
		{
			this.ipAddr = ipAddr;
		}

		public int getNetMask()
		{
			return netMask;
		}

		public void setNetMask(int netMask)
		{
			this.netMask = netMask;
		}

		public NeighborDto(int ipAddr, int netmask)
		{
			this.ipAddr = ipAddr;
			this.netMask = netmask;
		}
	}

}