package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * FILE DOWNLOAD PROGRESS message element type
 *@filename		BeCapwapFileDownProgressEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:29:00
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapFileDownProgressEvent extends BeAPWTPEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * <p>
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Download Type | Status | Finish Size
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ Finish
	 * Size | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * Download Type : 1 - Image. 2 - Script. 3 - Bootstrap. 4 - Captive web
	 * portal key. 5 - Captive web portal page. 6 - Radius certificate.
	 * <p>
	 * Status : 1 - Downloading. 2 - Finish.
	 * <p>
	 * Finish Size : Indicates how many bytes of the file have been downloaded
	 * completely.
	 * <p>
	 */

	private short	downloadType;

	private short	status;

	private int		finishSize;

	public BeCapwapFileDownProgressEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_FILEDOWNLOADPROGRESS;
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
			downloadType = buffer[0];
			status = buffer[0];
			finishSize = AhDecoder.bytes2int(buffer, 2, 4);
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeCapwapFileDownProgressEvent.parsePacket catch exception", e);
		}
	}

	public short getDownloadType()
	{
		return downloadType;
	}

	public void setDownloadType(short downloadType)
	{
		this.downloadType = downloadType;
	}

	public int getFinishSize()
	{
		return finishSize;
	}

	public void setFinishSize(int finishSize)
	{
		this.finishSize = finishSize;
	}

	public short getStatus()
	{
		return status;
	}

	public void setStatus(short status)
	{
		this.status = status;
	}

}