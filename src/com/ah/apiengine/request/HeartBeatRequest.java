package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.UpdateDNS;
import com.ah.apiengine.response.HeartBeatResponse;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.util.Tracer;

public class HeartBeatRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 */

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(HeartBeatRequest.class.getSimpleName());

	private HeartBeatResponse response;

	public HeartBeatRequest() {
		super();
	}

    //*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return HEART_BEAT_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "Heart Beat Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {

	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		response = new HeartBeatResponse();

		return response.build(this);
	}

	@Override
	public void callback() {
		log.info("callback", "Doing callback for " + getMsgName());

		if (response != null) {
			UpdateDNS updateDns = response.getUpdateDNS();

			if (updateDns != null) {
				Collection<HMUpdateSoftwareInfo> dnsUpdateInfos = updateDns.getDnsUpdateInfos();

				log.info("callback", "Doing the other things after notifying 'Portal' of updating DNS.");
				HHMoperate.doAfterChangeDNS(dnsUpdateInfos);
			}			
		}
	}

//    //*********************************************************************************
//    // Methods for API-Client to build request messages to API-Engine.
//    //*********************************************************************************
//
//	@Override
//	public ByteBuffer build() throws EncodeException {
//		ByteBuffer reqBB = super.build();
//
//		try {
//			/* Header */
//			int headerLen = encodeHeader(reqBB);
//
//			/* Message Elements Length */
//			fillPendingElementsLength(reqBB, headerLen, 0);
//
//			return reqBB;
//		} catch (Exception e) {
//			throw new EncodeException("Build '" + getMsgName() + "' Error.", e);
//		}
//	}

}