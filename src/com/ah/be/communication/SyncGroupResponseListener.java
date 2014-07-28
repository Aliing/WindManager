package com.ah.be.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 *@filename		SyncGroupResponseListener.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-3-11 10:42:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class SyncGroupResponseListener implements ResponseListener {
	// private List<BeCommunicationEvent> responseList = null;

	// /**
	// * member number in this group
	// */
	// private int rspNumber = 0;

	// serial number pair to communication event
	private Map<Integer, BeCommunicationEvent>		responseMap	= null;

	private List<? extends BeCommunicationEvent>	requestList	= null;

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public SyncGroupResponseListener(List<? extends BeCommunicationEvent> requestList) {
		// responseList = new ArrayList<BeCommunicationEvent>(size);
		responseMap = new HashMap<Integer, BeCommunicationEvent>(requestList.size());

		this.requestList = requestList;
	}

	/**
	 * @see com.ah.be.communication.ResponseListener#onResponse(com.ah.be.communication.BeCommunicationEvent)
	 */
	public synchronized void onResponse(BeCommunicationEvent event) {
		// responseList.add(event);
		// if (responseList.size() == rspNumber)
		// {
		// this.notify();
		// }

		responseMap.put(event.getSerialNum(), event);
		if (responseMap.size() == requestList.size()) {
			this.notify();
		}
	}
	
	/**
	 * add this api for add response obj when send request.
	 *
	 *@param 
	 *
	 *@return true: request is all satisfied.
	 */
	public synchronized boolean onResponseExt(BeCommunicationEvent event)
	{
		responseMap.put(event.getSerialNum(), event);
		if (responseMap.size() == requestList.size()) {
			return true;
		}
		
		return false;
	}

	public List<BeCommunicationEvent> getResponseList() {
		// return responseList;

		// to maintain original order, we need this special code.
		List<BeCommunicationEvent> responseList = new ArrayList<BeCommunicationEvent>(requestList
				.size());
		for (BeCommunicationEvent requestEvent : requestList) {
			BeCommunicationEvent responseEvent = responseMap.get(requestEvent.getSerialNum());
			if (responseEvent == null) {
				responseEvent = BeCommunicationProcessor.createRspEvent(requestEvent,
						BeCommunicationConstant.RESULTTYPE_TIMEOUT);
			}

			responseList.add(responseEvent);
		}

		return responseList;
	}
	//
	// public void setResponseList(List<BeCommunicationEvent> responseList)
	// {
	// this.responseList = responseList;
	// }
}
