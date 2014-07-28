package com.ah.be.app;

import java.util.List;

import com.ah.be.communication.BeCAPWAPRequestProcessor;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.BeCommunicationProcessor;

public class HmBeCommunicationUtil {
	/**
	 * send sync request, time out is default value(100s)
	 * 
	 * @return: response packet, response result maybe include : connect close, time out, success<br>
	 *          special for wtp event, when send sync request for these message type, response event
	 *          type is special for these message<br>
	 */
	public static BeCommunicationEvent sendSyncRequest(BeCommunicationEvent req) {
		return AhAppContainer.HmBe.getCommunicationModule().sendSyncRequest(req);
	}

	/**
	 * send sync request
	 * 
	 * @param timeout:
	 *            time unit is second, default value is 100s
	 * @return: response packet, response result maybe include : connect close, time out, success<br>
	 *          special for wtp event, when send sync request for these message type, response event
	 *          type is special for these message<br>
	 */
	public static BeCommunicationEvent sendSyncRequest(BeCommunicationEvent req, int timeout) {
		return AhAppContainer.HmBe.getCommunicationModule().sendSyncRequest(req, timeout);
	}

	/**
	 * send group request synchronously
	 * 
	 * @return: response packet list, response result maybe include : connect close, time out,
	 *          success<br>
	 *          special for wtp event, when send sync request for these message type, response event
	 *          type is special for these message<br>
	 */
	public static List<BeCommunicationEvent> sendSyncGroupRequest(
			List<BeCommunicationEvent> requestList) {
		return AhAppContainer.HmBe.getCommunicationModule().sendSyncGroupRequest(requestList);
	}

	/**
	 * send group request synchronously
	 * 
	 * @param
	 * @return: response packet list, response result maybe include : connect close, time out,
	 *          success<br>
	 *          special for wtp event, when send sync request for these message type, response event
	 *          type is special for these message<br>
	 */
	public static List<BeCommunicationEvent> sendSyncGroupRequest(
			List<? extends BeCommunicationEvent> requestList, int timeOut) {
		return AhAppContainer.HmBe.getCommunicationModule().sendSyncGroupRequest(requestList,
				timeOut);
	}

	/**
	 * asynchronize request
	 * 
	 * @param req
	 *            req message obj
	 * @param timeOut
	 *            define time out number(unit : s)
	 * @return serialNum ,if disconnect to capwap, return
	 *         -1(BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED)
	 */
	public static int sendRequest(BeCommunicationEvent req, int timeOut) {
		return AhAppContainer.HmBe.getCommunicationModule().sendRequest(req, timeOut);
	}

	/**
	 * asynchronize request
	 * 
	 * @param req
	 *            req message obj,timeout is default value
	 * @return serialNum,if disconnect to capwap, return
	 *         -1(BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED)
	 */
	public static int sendRequest(BeCommunicationEvent req) {
		return AhAppContainer.HmBe.getCommunicationModule().sendRequest(req);
	}

	/**
	 * get next sequence number, 1 ~ (1<<31)-1
	 * 
	 * @param
	 * @return
	 */
	public static int getSequenceNumber() {
		return AhAppContainer.HmBe.getCommunicationModule().getSequenceNumber();
	}
	
	public static boolean isConnectedToPortal()
	{
		return AhAppContainer.HmBe.getCommunicationModule().isConnectedToPortal();
	}

	public static String getPortalIP()
	{
		return AhAppContainer.HmBe.getCommunicationModule().getPortalIP();
	}

	public static String getPortalMac()
	{
		return AhAppContainer.HmBe.getCommunicationModule().getPortalMac();
	}
	
	/**
	 * Send response event.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static boolean sendResponse(BeCommunicationEvent rsp) 
	{
		return AhAppContainer.HmBe.getCommunicationModule().sendResponse(rsp);
	}

	public static BeCAPWAPRequestProcessor getBeCAPWAPRequestProcessor() {
		return AhAppContainer.HmBe.getCommunicationModule().getBeCAPWAPRequestProcessor();
	}

	public static BeCommunicationProcessor getBeCommunicationProcessor() {
		return AhAppContainer.HmBe.getCommunicationModule().getBeCommunicationProcessor();
	}
}
