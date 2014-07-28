package com.ah.be.communication.util.classifyap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAutoMitigationEvent;
import com.ah.be.communication.event.BeAutoMitigationResultEvent;
import com.ah.be.communication.mo.classifyap.ClassifyAps;
import com.ah.be.communication.mo.classifyap.ClassifyBaseAp;
import com.ah.be.communication.mo.classifyap.ClassifyFriendlyAp;
import com.ah.be.communication.mo.classifyap.ClassifyRogueAp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class ClassifyApUtil {
	private static final Tracer log = new Tracer(ClassifyApUtil.class
			.getSimpleName());
	
	public static void addToMapClassifyAps(Map<String, Set<ClassifyAps>> mapClassifyAps, String reportMac, ClassifyAps classifyAp) {
		if (mapClassifyAps.containsKey(reportMac)) {
			mapClassifyAps.get(reportMac).add(classifyAp);
		} else {
			Set<ClassifyAps> setTmp = new HashSet<ClassifyAps>();
			setTmp.add(classifyAp);
			mapClassifyAps.put(reportMac, setTmp);
		}
	}
	
	/**
	 * fetch all report APs of rogue/friendly AP with certain mac address
	 */
	public static Map<String, Set<String>> getMapOfReportAps(List<String> aps2Deal, HmDomain owner) {
		if (aps2Deal == null || aps2Deal.isEmpty()) {
			return null;
		}
		List<Idp> reportIdps = QueryUtil.executeQuery(Idp.class, null, new FilterParams("ifMacAddress",aps2Deal), owner.getId());
		if (reportIdps == null || reportIdps.isEmpty()) {
			return null;
		}
		Map<String, Set<String>> mapReportIdps = new HashMap<String, Set<String>>();
		for (Idp idp : reportIdps) {
			if (mapReportIdps.get(idp.getReportNodeId()) == null) {
				Set<String> setTmp = new HashSet<String>();
				setTmp.add(idp.getIfMacAddress());
				mapReportIdps.put(idp.getReportNodeId(), setTmp);
			} else {
				mapReportIdps.get(idp.getReportNodeId()).add(idp.getIfMacAddress());
			}
		}
		return mapReportIdps;
	}
	
	public static Set<ClassifyBaseAp> encapClassifyFriendlyAps(List<String> aps) {
		if (aps == null || aps.isEmpty()) {
			return null;
		}
		Set<ClassifyBaseAp> classifyApsTmp = new HashSet<ClassifyBaseAp>();
		for (String apMac : aps) {
			classifyApsTmp.add((ClassifyBaseAp)new ClassifyFriendlyAp(apMac));
		}
		return classifyApsTmp;
	}
	
	public static Set<ClassifyBaseAp> encapClassifyRogueAps(List<String> aps) {
		if (aps == null || aps.isEmpty()) {
			return null;
		}
		Set<ClassifyBaseAp> classifyApsTmp = new HashSet<ClassifyBaseAp>();
		for (String apMac : aps) {
			classifyApsTmp.add((ClassifyBaseAp)new ClassifyRogueAp(apMac));
		}
		return classifyApsTmp;
	}
	
	public static BeAutoMitigationEvent encapAutoMitigationEvent(String reportAp, Set<ClassifyAps> classifyAps)
			throws BeCommunicationEncodeException {
		BeAutoMitigationEvent event = new BeAutoMitigationEvent();
		event.setApMac(reportAp);
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		event.setSequenceNum(sequenceNum);
		event.setClassifyAps(new ArrayList<ClassifyAps>(classifyAps));
		event.buildPacket();
		return event;
	}
	
	/**
	 * not in use currently
	 * @param c_event
	 * @return
	 */
	public static BeAutoMitigationResultEvent getAutoMitigationResult(BeCommunicationEvent c_event) {
		try {
			int msgType = c_event.getMsgType();
			int result = c_event.getResult();
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
				return (BeAutoMitigationResultEvent) c_event;
			} else {
				log.error("getAutoMitigationResult, cannot get auto mitigation result of HiveAp:"
								+ c_event.getApMac()
								+ ", msgType:"
								+ msgType
								+ ", result:" + result);
			}
		} catch (Exception e) {
			log.error("getAutoMitigationResult", e);
		}
		return null;
	}

}
