package com.ah.be.communication;

import java.util.List;


/**
 *
 *@filename		BeCommunicationModule.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-6 01:57:36
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public interface BeCommunicationModule
{
	/**
	 * send sync request
	 *
	 * @return: response packet
	 */
	public BeCommunicationEvent sendSyncRequest(
		BeCommunicationEvent req);

	/**
	 * send sync request
	 *
	 * @return: response packet
	 */
	public BeCommunicationEvent sendSyncRequest(
		BeCommunicationEvent req,
		int timeout);

	/**
	 * send group request synchronously
	 *
	 * @return: return response list
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(
		List<BeCommunicationEvent> requestList);

	/**
	 * send group request synchronously
	 *
	 * @param
	 * @return:  return response list
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(
		List<? extends BeCommunicationEvent> requestList,
		int timeOut);

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj
	 * @param timeOut
	 *            define time out number
	 * @return serialNum ,if catch exception when encode, return 0
	 */
	public int sendRequest(BeCommunicationEvent req, int timeOut);

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj,timeout is default value
	 * @return serialNum ,if catch exception when encode, return 0
	 */
	public int sendRequest(BeCommunicationEvent req);

	/**
	 * get next sequence number, 1 ~ (1<<31)-1
	 *
	 * @param
	 * @return
	 */
	public int getSequenceNumber();
	
	public boolean isConnectedToPortal();

	public String getPortalIP();

	public String getPortalMac();

	/**
	 * Send response event.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public boolean sendResponse(BeCommunicationEvent rsp);

	public BeCAPWAPRequestProcessor getBeCAPWAPRequestProcessor();

	public BeCommunicationProcessor getBeCommunicationProcessor();
}
