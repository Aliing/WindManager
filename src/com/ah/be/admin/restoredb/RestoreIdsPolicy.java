package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IdsPolicySsidProfile;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.Vlan;
import com.ah.bo.wlan.SsidProfile;
//import com.ah.be.app.HmBeParaUtil;
//import com.ah.be.parameter.BeParaModule;

public class RestoreIdsPolicy {

	private static List<IdsPolicy> getAllIdsPolicy() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ids_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ids_policy");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in ids_policy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<IdsPolicy> idsPolicyInfo = new ArrayList<IdsPolicy>();

		boolean isColPresent;
		String colName;
		IdsPolicy idsPolicy;

		for (int i = 0; i < rowCount; i++) {
			idsPolicy = new IdsPolicy();

			/**
			 * Set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ids_policy' data be lost, cause: 'policyname' column is not exist.");
				/**
				 * The policyname column must be exist in the table of
				 * ids_policy
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ids_policy' data be lost, cause: 'policyname' column value is null.");
				continue;
			}
			idsPolicy.setPolicyName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			idsPolicy.setId(Long.valueOf(id));

//			if (BeParaModule.DEFAULT_WIPS_POLICY_NAME.equals(name.trim())) {
//				// set default wips object new id to map
//				IdsPolicy newIds = HmBeParaUtil.getDefaultProfile(IdsPolicy.class, null);
//				if (null != newIds) {
//					AhRestoreNewMapTools.setMapIDSPolicy(idsPolicy.getId(), newIds.getId());
//				}
//				continue;
//			}
			/**
			 * Default flag
			 */
			idsPolicy.setDefaultFlag(false);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			idsPolicy.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set mitigateperiod
			 */
			colName = "mitigateperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			int defaultVal = 1;
			if(isColPresent){
				defaultVal = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
			}
			int period = defaultVal;
			idsPolicy.setMitigatePeriod(period > 600 ? 600 : period);

			/**
			 * Set mitigateduration
			 */
			colName = "mitigateduration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String duration = isColPresent ? xmlParser.getColVal(i, colName)
					: "14400";
			idsPolicy
					.setMitigateDuration(AhRestoreCommons.convertInt(duration));

			/**
			 * Set mitigatequiet
			 */
			colName = "mitigatequiet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String quiet = isColPresent ? xmlParser.getColVal(i, colName)
					: "3600";
			idsPolicy.setMitigateQuiet(AhRestoreCommons.convertInt(quiet));

			/**
			 * Set mitigationmode
			 */
			colName = "mitigationmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			short mitiMode = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : IdsPolicy.MITIGATION_MODE_SEMIAUTO;
			idsPolicy.setMitigationMode(mitiMode);

			/**
			 * Set insamenetwork
			 */
			colName = "insamenetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			boolean insamenetwork = isColPresent ? AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: true;
			idsPolicy.setInSameNetwork(insamenetwork);

			/**
			 * Set detectoraps
			 */
			colName = "detectoraps";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			int detectoraps = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
					: 1;
			idsPolicy.setDetectorAps(detectoraps);

			/**
			 * Set deauthtime
			 */
			colName = "deauthtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			int deauthtime = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
					: 60;
			idsPolicy.setDeAuthTime(deauthtime);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ids_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			idsPolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set innetworkenable
			 */
			colName = "innetworkenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String innetworkEnable = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			idsPolicy.setInNetworkEnable(AhRestoreCommons
					.convertStringToBoolean(innetworkEnable));

			/**
			 * Set networkdetectionenable
			 */
			colName = "networkdetectionenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String networkDetectionEnable = isColPresent ? xmlParser.getColVal(
					i, colName) : "false";
			idsPolicy.setNetworkDetectionEnable(AhRestoreCommons
					.convertStringToBoolean(networkDetectionEnable));

			/**
			 * Set ouienable
			 */
			colName = "ouienable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String ouiEnable = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			idsPolicy.setOuiEnable(AhRestoreCommons
					.convertStringToBoolean(ouiEnable));

			/**
			 * Set roguedetectionenable
			 */
			colName = "roguedetectionenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String rogueDetectionEnable = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			idsPolicy.setRogueDetectionEnable(AhRestoreCommons
					.convertStringToBoolean(rogueDetectionEnable));

			/**
			 * Set shortbeanchintervalenable
			 */
			colName = "shortbeanchintervalenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String shortBeanch = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			idsPolicy.setShortBeanchIntervalEnable(AhRestoreCommons
					.convertStringToBoolean(shortBeanch));

			/**
			 * Set shortpreambleenable
			 */
			colName = "shortpreambleenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String shortPream = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			idsPolicy.setShortPreambleEnable(AhRestoreCommons
					.convertStringToBoolean(shortPream));

			/**
			 * Set ssidenable
			 */
			colName = "ssidenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String ssidEnable = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			idsPolicy.setSsidEnable(AhRestoreCommons
					.convertStringToBoolean(ssidEnable));

			/**
			 * Set wmmenable
			 */
			colName = "wmmenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String wmmEnable = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			idsPolicy.setWmmEnable(AhRestoreCommons
					.convertStringToBoolean(wmmEnable));

			/**
			 * Set stareportenabled
			 */
			colName = "stareportenabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String stareportenabled = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			idsPolicy.setStaReportEnabled(AhRestoreCommons
					.convertStringToBoolean(stareportenabled));

			/**
			 * Set stareportageout
			 */
			colName = "stareportageout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String stareportageout = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(IdsPolicy.DEFAULT_STA_REPORT_AGEOUT);
			idsPolicy.setStaReportAgeout(AhRestoreCommons
					.convertInt(stareportageout));

			/**
			 * Set stareportduration
			 */
			colName = "stareportduration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String stareportduration = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(IdsPolicy.DEFAULT_STA_REPORT_DURATION);
			idsPolicy.setStaReportDuration(AhRestoreCommons
					.convertInt(stareportduration));

			/**
			 * Set stareportinterval
			 */
			colName = "stareportinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String stareportinterval = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(IdsPolicy.DEFAULT_STA_REPORT_INTERVAL);
			idsPolicy.setStaReportInterval(AhRestoreCommons
					.convertInt(stareportinterval));

			/**
			 * Set stareportperiod
			 */
			colName = "stareportperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String stareportperiod = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(IdsPolicy.DEFAULT_STA_REPORT_PERIOD);
			idsPolicy.setStaReportPeriod(AhRestoreCommons
					.convertInt(stareportperiod));
			/**
			 * Set staReportAgeTime
			 */
			colName = "staReportAgeTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy", colName);
			String staReportAgeTime = isColPresent ? xmlParser.getColVal(i, colName)
					: "3600";
			idsPolicy.setStaReportAgeTime(AhRestoreCommons.convertInt(staReportAgeTime));
			
			idsPolicyInfo.add(idsPolicy);
		}

		return idsPolicyInfo.isEmpty() ? null : idsPolicyInfo;
	}

	private static Map<String, Set<MacOrOui>> getAllIdsMacOrOui()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ids_policy_mac_or_oui.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ids_policy_mac_or_oui");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacOrOui>> macOrOuiInfo = new HashMap<String, Set<MacOrOui>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set ids_policy_id
			 */
			colName = "ids_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_mac_or_oui", colName);
			if (!isColPresent) {
				/**
				 * The ids_policy_id column must be exist in the table of
				 * ids_policy_mac_or_oui
				 */
				continue;
			}

			String idsPolicyId = xmlParser.getColVal(i, colName);
			if (idsPolicyId == null || idsPolicyId.trim().equals("")
					|| idsPolicyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set mac_or_oui_id
			 */
			colName = "mac_or_oui_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_mac_or_oui", colName);
			if (!isColPresent) {
				/**
				 * The mac_or_oui_id column must be exist in the table of
				 * ids_policy_mac_or_oui
				 */
				continue;
			}

			String macOrOuiId = xmlParser.getColVal(i, colName);
			if (macOrOuiId == null || macOrOuiId.trim().equals("")
					|| macOrOuiId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			Long newMac = AhRestoreNewMapTools
					.getMapMacAddress(AhRestoreCommons.convertLong(macOrOuiId));
			if (null != newMac) {
				MacOrOui macOrOui = AhRestoreNewTools.CreateBoWithId(
						MacOrOui.class, newMac);

				if (null != macOrOui) {
					if (macOrOuiInfo.get(idsPolicyId) == null) {
						Set<MacOrOui> macOrOuiSet = new HashSet<MacOrOui>();
						macOrOuiSet.add(macOrOui);
						macOrOuiInfo.put(idsPolicyId, macOrOuiSet);
					} else {
						macOrOuiInfo.get(idsPolicyId).add(macOrOui);
					}
				}
			}
		}
		return macOrOuiInfo;
	}

	private static Map<String, Set<Vlan>> getAllIdsVlan()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ids_policy_vlan.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ids_policy_vlan");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<Vlan>> vlanInfo = new HashMap<String, Set<Vlan>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set ids_policy_id
			 */
			colName = "ids_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_vlan", colName);
			if (!isColPresent) {
				/**
				 * The ids_policy_id column must be exist in the table of
				 * ids_policy_vlan
				 */
				continue;
			}

			String idsPolicyId = xmlParser.getColVal(i, colName);
			if (idsPolicyId == null || idsPolicyId.trim().equals("")
					|| idsPolicyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set vlan_id
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_vlan", colName);
			if (!isColPresent) {
				/**
				 * The vlan_id column must be exist in the table of
				 * ids_policy_vlan
				 */
				continue;
			}

			String vlanId = xmlParser.getColVal(i, colName);
			if (vlanId == null || vlanId.trim().equals("")
					|| vlanId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			Long newVlan = AhRestoreNewMapTools.getMapVlan(AhRestoreCommons
					.convertLong(vlanId));

			if (null != newVlan) {
				Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,
						newVlan);

				if (null != vlan) {
					if (vlanInfo.get(idsPolicyId) == null) {
						Set<Vlan> vlanSet = new HashSet<Vlan>();
						vlanSet.add(vlan);
						vlanInfo.put(idsPolicyId, vlanSet);
					} else {
						vlanInfo.get(idsPolicyId).add(vlan);
					}
				}
			}
		}
		return vlanInfo;
	}

	private static Map<String, List<IdsPolicySsidProfile>> getAllIdsSsid()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ids_policy_ssid_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ids_policy_ssid_profile");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<IdsPolicySsidProfile>> ssidInfo = new HashMap<String, List<IdsPolicySsidProfile>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set ids_policy_id
			 */
			colName = "ids_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_ssid_profile", colName);
			if (!isColPresent) {
				/**
				 * The ids_policy_id column must be exist in the table of
				 * ids_policy_ssid_profile
				 */
				continue;
			}

			String idsPolicyId = xmlParser.getColVal(i, colName);
			if (idsPolicyId == null || idsPolicyId.trim().equals("")
					|| idsPolicyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_ssid_profile", colName);
			if (!isColPresent) {
				/**
				 * The ssid_profile_id column must be exist in the table of
				 * ids_policy_ssid_profile
				 */
				continue;
			}

			String ssidId = xmlParser.getColVal(i, colName);
			if (ssidId == null || ssidId.trim().equals("")
					|| ssidId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			colName = "encryptionenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_ssid_profile", colName);
			String encrypEnable = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";

			colName = "encryptiontype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_ssid_profile", colName);
			String encrypType = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(IdsPolicySsidProfile.ENCRYPTION_TYPE_OPEN);

			// get new ssid id through old id
			Long ssidNew = AhRestoreNewMapTools.getMapSsid(AhRestoreCommons
					.convertLong(ssidId));

			if (null != ssidNew) {
				SsidProfile ssid = AhRestoreNewTools.CreateBoWithId(
						SsidProfile.class, ssidNew);

				if (null != ssid) {
					if (ssidInfo.get(idsPolicyId) == null) {
						List<IdsPolicySsidProfile> ssidSet = new ArrayList<IdsPolicySsidProfile>();
						IdsPolicySsidProfile ids_ssid = new IdsPolicySsidProfile();
						ids_ssid.setEncryptionEnable(AhRestoreCommons
								.convertStringToBoolean(encrypEnable));
						ids_ssid.setEncryptionType(AhRestoreCommons
								.convertInt(encrypType));
						ids_ssid.setSsidProfile(ssid);
						ssidSet.add(ids_ssid);
						ssidInfo.put(idsPolicyId, ssidSet);
					} else {
						IdsPolicySsidProfile ids_ssid = new IdsPolicySsidProfile();
						ids_ssid.setEncryptionEnable(AhRestoreCommons
								.convertStringToBoolean(encrypEnable));
						ids_ssid.setEncryptionType(AhRestoreCommons
								.convertInt(encrypType));
						ids_ssid.setSsidProfile(ssid);
						ssidInfo.get(idsPolicyId).add(ids_ssid);
					}
				}
			}
		}
		return ssidInfo;
	}

	public static boolean restoreIdsPolicy() {
		try {
			List<IdsPolicy> allIdsPolicy = getAllIdsPolicy();

			if (null == allIdsPolicy) {
				AhRestoreDBTools.logRestoreMsg("allIdsPolicy is null");
			} else {
				Map<String, Set<MacOrOui>> allMacOrOui = getAllIdsMacOrOui();
				Map<String, Set<Vlan>> allVlan = getAllIdsVlan();
				Map<String, List<IdsPolicySsidProfile>> allIdsSsid = getAllIdsSsid();

				for (IdsPolicy idsPolicy : allIdsPolicy) {
					if (idsPolicy != null) {
						if (null != allMacOrOui) {
							idsPolicy.setMacOrOuis(allMacOrOui.get(idsPolicy
									.getId().toString()));
						}
						if (null != allVlan) {
							idsPolicy.setVlans(allVlan.get(idsPolicy.getId()
									.toString()));
						}
						if (null != allIdsSsid) {
							idsPolicy.setIdsSsids(allIdsSsid.get(idsPolicy
									.getId().toString()));
						}
					}
				}
				List<Long> lOldId = new ArrayList<Long>();

				for (IdsPolicy idsPolicy : allIdsPolicy) {
					lOldId.add(idsPolicy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allIdsPolicy);

				for (int i = 0; i < allIdsPolicy.size(); i++) {
					AhRestoreNewMapTools.setMapIDSPolicy(lOldId.get(i),
							allIdsPolicy.get(i).getId());
				}
			}
		} catch (Exception e) {

			AhRestoreDBTools.logRestoreMsg(e.getMessage());

			return false;
		}
		return true;
	}

}