package com.ah.be.communication;

import java.util.List;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeCapwapClientConnectEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;

public class BeCommunicationModuleImpl extends BaseModule
															implements
															BeCommunicationModule
{
	private BeCommunicationProcessor	processor;
	
	// this thread only for hmol, handle request from capwap server.
	private BeCAPWAPRequestProcessor capwapRequestProcessor;

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run()
	{
		// start thread
		processor = new BeCommunicationProcessor();
		processor.startTask();
		
		if (NmsUtil.isHostedHMApplication()) {
			capwapRequestProcessor = new BeCAPWAPRequestProcessor();
			capwapRequestProcessor.startTask();
		}

		return super.run();
	}

	@Override
	public boolean init()
	{
		return true;
	}

	@Override
	public boolean shutdown()
	{
		// stop thread
		if (processor != null) {
			processor.shutdown();
		}
		
		if (capwapRequestProcessor != null) {
			capwapRequestProcessor.shutdown();
		}

		return true;
	}

	/**
	 * Construct method
	 *
	 * @param
	 * @throws
	 */
	public BeCommunicationModuleImpl()
	{
		super();
		setModuleId(ModuleID_Communication);
		setModuleName("BeCommunicationModule");
	}

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj
	 * @param timeOut
	 *            define time out number
	 * @return serialNum
	 */
	public int sendRequest(BeCommunicationEvent req, int timeOut)
	{
		try
		{
			return processor.sendRequest(req, timeOut);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendRequest(): failed to send request, maybe encode exception.",
					e);
			return BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
		}
	}

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj,timeout is default value
	 * @return serialNum
	 */
	public int sendRequest(BeCommunicationEvent req)
	{
		try
		{
			return processor.sendRequest(req);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendRequest(): failed to send request, maybe encode exception.",
					e);
			return BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
		}
	}

	/**
	 * send sync request
	 *
	 * @return: response packet
	 */
	public BeCommunicationEvent sendSyncRequest(BeCommunicationEvent req)
	{
		try
		{
			return processor.sendSyncRequest(req);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendSyncRequest(): failed to send sync request, maybe encode exception.",
					e);
			return null;
		}
	}

	/**
	 * send sync request
	 *
	 * @return: response packet
	 */
	public BeCommunicationEvent sendSyncRequest(
		BeCommunicationEvent req,
		int timeout)
	{
		try
		{
			return processor.sendSyncRequest(req, timeout);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendSyncRequest(): failed to send sync request, maybe encode exception.",
					e);
			return null;
		}
	}

	/**
	 * send group request synchronously
	 *
	 * @return: return response list
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(
		List<BeCommunicationEvent> requestList)
	{
		try
		{
			return processor.sendSyncGroupRequest(requestList);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendSyncGroupRequest(): failed to send sync request, maybe encode exception.",
					e);
			return null;
		}
	}

	/**
	 * send group request synchronously
	 *
	 * @param
	 * @return: return response list
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(
		List<? extends BeCommunicationEvent> requestList,
		int timeOut)
	{
		try
		{
			return processor.sendSyncGroupRequest(requestList, timeOut);
		}
		catch (Exception e)
		{
			// encode exception
			DebugUtil
				.commonDebugWarn(
					"BeCommunicationModuleImpl.sendSyncGroupRequest(): failed to send sync request, maybe encode exception.",
					e);
			return null;
		}
	}

	/**
	 * get next sequence number, 1 ~ (1<<31)-1
	 *
	 * @param
	 * @return
	 */
	public synchronized int getSequenceNumber()
	{
		return processor.getNextSerialNum();
	}
	
	private boolean	connectedToPortal	= false;
	private String	portalIP;
	private String	portalMac;

	@Override
	public void eventDispatched(BeBaseEvent arg_Event) {
		super.eventDispatched(arg_Event);

		// communication event
		if (arg_Event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
			BeCommunicationEvent communicationEvent = (BeCommunicationEvent) arg_Event;

			switch (communicationEvent.getMsgType()) {
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTCONNECT: {
				BeCapwapClientConnectEvent connectEvent = (BeCapwapClientConnectEvent) communicationEvent;
				if (connectEvent.getServerType() == BeCapwapClientConnectEvent.SERVERTYPE_PORTAL) {
					connectedToPortal = connectEvent.isConnected();
					portalIP = connectEvent.getCapwapServerIP();
					portalMac = connectEvent.getCapwapServerMac();
				}
			}
			}
		}
	}

	public boolean isConnectedToPortal() {
		return connectedToPortal;
	}

	public String getPortalIP() {
		return portalIP;
	}

	public String getPortalMac() {
		return portalMac;
	}
	
	/**
	 * Send response event.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public boolean sendResponse(BeCommunicationEvent rsp) 
	{
		return processor.sendResponse(rsp);
	}

	@Override
	public BeCAPWAPRequestProcessor getBeCAPWAPRequestProcessor() {
		return capwapRequestProcessor;
	}

	@Override
	public BeCommunicationProcessor getBeCommunicationProcessor() {
		return processor;
	}
}