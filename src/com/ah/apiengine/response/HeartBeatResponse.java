package com.ah.apiengine.response;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import com.ah.apiengine.AbstractResponse;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.agent.HmApiEngineMastAgent;
import com.ah.apiengine.agent.HmApiEngineMastAgentImpl;
import com.ah.apiengine.agent.subagent.CommonAgent;
import com.ah.apiengine.element.StringList;
import com.ah.apiengine.element.UpdateDNS;
import com.ah.apiengine.request.HeartBeatRequest;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.util.Tracer;

public class HeartBeatResponse extends AbstractResponse<HeartBeatRequest> {

	/*
	 * Elements Required:
	 * 
	 * o Update DNS (optional)
	 */

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(HeartBeatResponse.class
															.getSimpleName());

	/* Update DNS */
	private UpdateDNS			updateDNS;

	/* Login success vhm list */
	private StringList			vhmList;

	public HeartBeatResponse() {
		super();
	}

	public UpdateDNS getUpdateDNS() {
		return updateDNS;
	}

	protected StringList getVhmList() {
		return vhmList;
	}

	// *********************************************************************************
	// Methods implement Message
	// *********************************************************************************

	@Override
	public int getMsgType() {
		return HEART_BEAT_RESPONSE;
	}

	@Override
	public String getMsgName() {
		return "Heart Beat Response";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				switch (e.getElemType()) {
				case UPDATE_DNS:
					updateDNS = (UpdateDNS) e;
					break;
				case STRING_LIST:
					vhmList = (StringList) e;
					break;
				default:
					break;
				}
			}
		}
	}

	// **********************************************************************************
	// Methods for API-Engine to execute API-Client request and build response messages
	// **********************************************************************************

	@Override
	public ByteBuffer build(HeartBeatRequest request) throws EncodeException {
		ByteBuffer respBB = super.build(request);

		/* Header */
		int headerLen = encodeHeader(respBB);
		int udLen = 0;

		queryDnsUpdateInfos();
		queryVhmLoginInfos();

		/* Update DNS */
		if (updateDNS != null) {
			udLen = updateDNS.encode(respBB);
		}

		/* vhm list */
		if (vhmList != null) {
			udLen += vhmList.encode(respBB);
		}

		/* Message Elements Length */
		fillPendingElementsLength(respBB, headerLen, udLen);

		return respBB;
	}

	private void queryVhmLoginInfos() {
		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			CommonAgent agent = mastAgent.getCommonAgent();
			List<String> strs = agent.doQueryVhmLoginInfos();

			vhmList = new StringList();
			vhmList.setStrs(strs);

		} catch (Exception e) {
			log.error("build", "Query VHM login info failed.", e);
		}
	}

	private void queryDnsUpdateInfos() {
		try {
			HmApiEngineMastAgent mastAgent = HmApiEngineMastAgentImpl.getInstance();
			Collection<HMUpdateSoftwareInfo> dnsUpdateInfos = mastAgent.queryDnsUpdateInfos();

			if (dnsUpdateInfos != null && !dnsUpdateInfos.isEmpty()) {
				updateDNS = new UpdateDNS();
				updateDNS.setDnsUpdateInfos(dnsUpdateInfos);
			}

		} catch (Exception e) {
			log.error("build", "Query DNS update information failed.", e);
		}
	}

}