package com.ah.be.communication;


/**
 * 
 *@filename		SyncResponseListener.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-3-4 03:43:44
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class SyncResponseListener implements ResponseListener
{
	private BeCommunicationEvent	response	= null;
	
//	/**
//	 * positive number, zero default
//	 */
//	private int sequenceNumber = 0;
	
//	/**
//	 * 
//	 * Construct method
//	 *
//	 * @param
//	 *
//	 * @throws
//	 */
//	public SyncResponseListener(int sequenceNumber)
//	{
//		this.sequenceNumber = sequenceNumber;
//	}
	
	/**
	 * 
	 * Construct method
	 *
	 * @param
	 *
	 * @throws
	 */
	public SyncResponseListener()
	{
	}

	public synchronized void onResponse(BeCommunicationEvent event)
	{
		this.response = event;
		this.notify();
	}

	public BeCommunicationEvent getResponse()
	{
		return response;
	}

//	public int getSequenceNumber()
//	{
//		return sequenceNumber;
//	}
//
//	public void setSequenceNumber(int sequenceNumber)
//	{
//		this.sequenceNumber = sequenceNumber;
//	}
}
