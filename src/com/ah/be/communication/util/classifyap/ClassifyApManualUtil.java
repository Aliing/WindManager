package com.ah.be.communication.util.classifyap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.classifyap.ClassifyAps;
import com.ah.be.communication.mo.classifyap.ClassifyBaseAp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.Tracer;

public class ClassifyApManualUtil {
	private static final Tracer log = new Tracer(ClassifyApManualUtil.class
			.getSimpleName());
	
	public static void sendManualRogueApRequests(List<String> aps, byte opFlag, HmDomain owner) 
			throws BeCommunicationEncodeException {
		dealManualMitigationAps(aps, ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE, opFlag, owner);
	}

	public static void sendManualFriendlyApRequests(List<String> aps, byte opFlag, HmDomain owner) 
			throws BeCommunicationEncodeException {
		dealManualMitigationAps(aps, ClassifyBaseAp.CLASSIFY_TYPE_ROGUE_TO_FRIEND, opFlag, owner);
	}

	/**
	* used to add manually dealt friendly APs & rogue APs
	*/
	public void dealManualMitigationRogueAps(List<String> friendlyAps, byte friendlyOpFlag, 
														List<String> rogueAps, byte rogueOpFlag, HmDomain owner) 
				throws BeCommunicationEncodeException {
		Map<String, Set<String>> friendReportAps = ClassifyApUtil.getMapOfReportAps(friendlyAps, owner);
		Map<String, Set<String>> rogueReportAps = ClassifyApUtil.getMapOfReportAps(rogueAps, owner);
		if ((friendReportAps  == null || friendReportAps.isEmpty())
			&& (rogueReportAps == null || rogueReportAps.isEmpty())) {
			return;
		}
		Map<String, Set<ClassifyAps>> mapClassifyAps =  new HashMap<String, Set<ClassifyAps>>();
		if (friendlyAps != null && friendlyAps.size() > 0) {
			for (Iterator<String> iter = friendReportAps.keySet().iterator(); iter.hasNext();) {
				String reportApMac = iter.next();
				ClassifyApUtil.addToMapClassifyAps(mapClassifyAps, reportApMac, 
						new ClassifyAps(ClassifyApUtil.encapClassifyFriendlyAps(new ArrayList<String>(friendReportAps.get(reportApMac))),
								ClassifyBaseAp.CLASSIFY_TYPE_ROGUE_TO_FRIEND, friendlyOpFlag));
			}
		}
		if (rogueReportAps != null && rogueReportAps.size() > 0) {
			for (Iterator<String> iter = rogueReportAps.keySet().iterator(); iter.hasNext();) {
				String reportApMac = iter.next();
				ClassifyApUtil.addToMapClassifyAps(mapClassifyAps, reportApMac, 
						new ClassifyAps(ClassifyApUtil.encapClassifyRogueAps(new ArrayList<String>(rogueReportAps.get(reportApMac))),
								ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE, rogueOpFlag));
			}
		}
	
		sendGroupManualMitigationRequests(mapClassifyAps);
		
	}
	
	private static void sendGroupManualMitigationRequests(Map<String, Set<ClassifyAps>> mapClassifyAps) 
				throws BeCommunicationEncodeException {
		if (mapClassifyAps == null || mapClassifyAps.isEmpty()) {
			return;
		}
		for (Iterator<String> iter = mapClassifyAps.keySet().iterator(); iter.hasNext();) {
			String reportApMac = iter.next();
			HmBeCommunicationUtil.sendRequest(ClassifyApUtil.encapAutoMitigationEvent(reportApMac, mapClassifyAps.get(reportApMac)));
		}
	}

	/**
	 * used to add manually dealt rogue APs
	 */
	private static void dealManualMitigationAps(List<String> aps, byte classifyType, byte opFlag, HmDomain owner) 
				throws BeCommunicationEncodeException {
		//fetch report AP and APs to be sent to that report AP
		Map<String, Set<String>> reportAps = ClassifyApUtil.getMapOfReportAps(aps, owner);
		if (reportAps == null || reportAps.isEmpty()) {
			log.error("No AP is defined to send mitigation event.");
			return;
		}
		Map<String, Set<ClassifyAps>> mapClassifyAps =  new HashMap<String, Set<ClassifyAps>>();
		if (classifyType == ClassifyBaseAp.CLASSIFY_TYPE_ROGUE_TO_FRIEND) {
			for (Iterator<String> iter = reportAps.keySet().iterator(); iter.hasNext();) {
				String reportApMac = iter.next();
				ClassifyApUtil.addToMapClassifyAps(mapClassifyAps, reportApMac, 
						new ClassifyAps(ClassifyApUtil.encapClassifyFriendlyAps(new ArrayList<String>(reportAps.get(reportApMac))),
								ClassifyBaseAp.CLASSIFY_TYPE_ROGUE_TO_FRIEND, opFlag));
			}
		} else if (classifyType == ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE) {
			for (Iterator<String> iter = reportAps.keySet().iterator(); iter.hasNext();) {
				String reportApMac = iter.next();
				ClassifyApUtil.addToMapClassifyAps(mapClassifyAps, reportApMac, 
						new ClassifyAps(ClassifyApUtil.encapClassifyRogueAps(new ArrayList<String>(reportAps.get(reportApMac))),
								ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE, opFlag));
			}
		}
		
		sendGroupManualMitigationRequests(mapClassifyAps);
	}
	
}
