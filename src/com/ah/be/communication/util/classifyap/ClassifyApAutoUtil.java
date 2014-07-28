package com.ah.be.communication.util.classifyap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeAutoMitigationEvent;
import com.ah.be.communication.mo.classifyap.ClassifyAps;
import com.ah.be.communication.mo.classifyap.ClassifyBaseAp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.util.Tracer;

public class ClassifyApAutoUtil {
	private static final Tracer log = new Tracer(ClassifyApAutoUtil.class
			.getSimpleName());

	/**
	* used to deal with query from DA
	* friendly APs will be sent to DA, rogue APs will be sent to all report APs
	* @param s_ap DA
	*/
	public static void dealDAMitigationQueryEvent(SimpleHiveAp s_ap) 
			throws BeCommunicationEncodeException {
		// set domain to be dealt first
		if (s_ap == null || s_ap.getMacAddress() == null || "".equals(s_ap.getMacAddress())) {
			log.error("No DA is defined.");
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(s_ap.getDomainId());
		if (owner == null) {
			log.error("Ap with mac address: " + s_ap.getMacAddress() + " is not in any domain, ignore it.");
			return;
		}
		//first, fetch out all friendly APs and send to DA
		sendDAQueryResponse(s_ap.getMacAddress(), new ClassifyAps(ClassifyApUtil.encapClassifyFriendlyAps(fetchAllFriendlyAps(owner)),
								ClassifyBaseAp.CLASSIFY_TYPE_ROGUE_TO_FRIEND, ClassifyBaseAp.DATA_OPERATION_FLAG_ALL));
		//then, fetch out all rogue APs and send to report APs
		Map<String, Set<String>> rogueReportAps = ClassifyApUtil.getMapOfReportAps(fetchAllRogueAps(owner), owner);
		if (rogueReportAps != null && rogueReportAps.size() > 0) {
			for (Iterator<String> iter = rogueReportAps.keySet().iterator(); iter.hasNext();) {
				String reportApMac = iter.next();
				sendDAQueryResponse(reportApMac, 
						new ClassifyAps(ClassifyApUtil.encapClassifyRogueAps(new ArrayList<String>(rogueReportAps.get(reportApMac))),
								ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE, ClassifyBaseAp.DATA_OPERATION_FLAG_ADD));
			}
		}
	}
	
	/**
	 * fetch all friendly APs from database and save into classifyAps
	 */
	private static List<String> fetchAllFriendlyAps(HmDomain owner) {
		IdpSettings setting = HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().getIdpSetting(owner.getId());
		if (setting == null) {
			return null;
		}
		return setting.getEnclosedFriendlyAps();
	}
	
	/**
	 * fetch all rogue APs from database and save into classifyAps
	 */
	private static List<String> fetchAllRogueAps(HmDomain owner) {
		IdpSettings setting = HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().getIdpSetting(owner.getId());
		if (setting == null) {
			return null;
		}
		return setting.getEnclosedRogueAps();
	}

	private static void sendDAQueryResponse(String apMac, ClassifyAps classifyAps) 
			throws BeCommunicationEncodeException {
		Set<ClassifyAps> setTmp = new HashSet<ClassifyAps>();
		setTmp.add(classifyAps);
		BeAutoMitigationEvent event = ClassifyApUtil.encapAutoMitigationEvent(apMac, setTmp);
		HmBeCommunicationUtil.sendRequest(event);
	}
	
}
