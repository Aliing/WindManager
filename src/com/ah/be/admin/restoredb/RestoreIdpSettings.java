package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.mgmt.QueryUtil;

public class RestoreIdpSettings {

	private static final String ENCLOSED_ROGUE_APS = "idp_enclosed_rogue_ap";
	private static final String ENCLOSED_FRIENDLY_APS = "idp_enclosed_friendly_ap";

	public static boolean restoreIdpSettings() {
		try {
			List<IdpSettings> list = getAllIdpSetting();
			if (null != list && !list.isEmpty()) {
				for (IdpSettings bo : list) {
					QueryUtil.createBo(bo);
				}
			}
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Restore IDP Settings finished.");
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"Restore IDP Settings catch exception ", e);
			return false;
		}
		return true;
	}

	private static Map<String, Set<String>> getAllEnclosedRogueAps()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		Map<String, Set<String>> enclosedRogueApInfo = new HashMap<String, Set<String>>();

		/**
		 * Check validation of idp_enclosed_rogue_ap.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(ENCLOSED_ROGUE_APS);
		if (!restoreRet) {
			return enclosedRogueApInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set idp_setting_id
			 */
			colName = "idp_setting_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ENCLOSED_ROGUE_APS, colName);
			if (!isColPresent) {
				/**
				 * The idp_setting_id column must be exist in the table of
				 * idp_enclosed_rogue_ap
				 */
				continue;
			}

			String idp_setting_id = xmlParser.getColVal(i, colName);
			if (idp_setting_id == null || idp_setting_id.trim().equals("")
					|| idp_setting_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set bssid
			 */
			colName = "bssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ENCLOSED_ROGUE_APS, colName);
			if (!isColPresent) {
				/**
				 * The bssid column must be exist in the table of
				 * idp_enclosed_rogue_ap
				 */
				continue;
			}

			String bssid = xmlParser.getColVal(i, colName);
			if (bssid == null || bssid.trim().equals("")
					|| bssid.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (enclosedRogueApInfo.get(idp_setting_id) == null) {
				Set<String> bssids = new HashSet<String>();
				bssids.add(bssid);
				enclosedRogueApInfo.put(idp_setting_id, bssids);
			} else {
				enclosedRogueApInfo.get(idp_setting_id).add(bssid);
			}
		}
		return enclosedRogueApInfo;
	}

	private static Map<String, Set<String>> getAllEnclosedFriendlyAps()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		Map<String, Set<String>> enclosedFriendlyApInfo = new HashMap<String, Set<String>>();

		/**
		 * Check validation of idp_enclosed_friendly_ap.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(ENCLOSED_FRIENDLY_APS);
		if (!restoreRet) {
			return enclosedFriendlyApInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set idp_setting_id
			 */
			colName = "idp_setting_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ENCLOSED_FRIENDLY_APS, colName);
			if (!isColPresent) {
				/**
				 * The idp_setting_id column must be exist in the table of
				 * idp_enclosed_friendly_ap
				 */
				continue;
			}

			String idp_setting_id = xmlParser.getColVal(i, colName);
			if (idp_setting_id == null || idp_setting_id.trim().equals("")
					|| idp_setting_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set bssid
			 */
			colName = "bssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ENCLOSED_FRIENDLY_APS, colName);
			if (!isColPresent) {
				/**
				 * The bssid column must be exist in the table of
				 * idp_enclosed_friendly_ap
				 */
				continue;
			}

			String bssid = xmlParser.getColVal(i, colName);
			if (bssid == null || bssid.trim().equals("")
					|| bssid.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (enclosedFriendlyApInfo.get(idp_setting_id) == null) {
				Set<String> bssids = new HashSet<String>();
				bssids.add(bssid);
				enclosedFriendlyApInfo.put(idp_setting_id, bssids);
			} else {
				enclosedFriendlyApInfo.get(idp_setting_id).add(bssid);
			}
		}
		return enclosedFriendlyApInfo;
	}

	private static List<IdpSettings> getAllIdpSetting()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of idp_settings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("idp_settings");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read idp_settings.xml file.");
			return null;
		}

		Map<String, Set<String>> enclosedRogueAps = getAllEnclosedRogueAps();
		Map<String, Set<String>> enclosedFriendlyAps = getAllEnclosedFriendlyAps();

		/**
		 * No one row data stored in idp_settings table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<IdpSettings> configs = new ArrayList<IdpSettings>();

		boolean isColPresent;
		String colName;
		IdpSettings config;
		for (int i = 0; i < rowCount; i++) {
			try {
				config = new IdpSettings();

				/**
				 * Set ID
				 */
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"idp_settings", colName);
				if (!isColPresent) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'idp_settings' data be lost, cause: 'id' column is not exist.");
					/**
					 * The id column must be exist in the table of idp_settings
					 */
					continue;
				}
				String id = xmlParser.getColVal(i, colName);
				if (id == null || id.trim().equals("")
						|| id.trim().equalsIgnoreCase("null")) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'idp_settings' data be lost, cause: 'id' column value is null.");
					continue;
				}
				Set<String> ros = enclosedRogueAps.get(id);
				Set<String> frs = enclosedFriendlyAps.get(id);
				if (null != ros) {
					config.setEnclosedRogueAps(new ArrayList<String>(ros));
				}
				if (null != frs) {
					config.setEnclosedFriendlyAps(new ArrayList<String>(frs));
				}

				/**
				 * Set interval
				 */
				colName = "interval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"idp_settings", colName);
				String interval = (isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(IdpSettings.DEFAULT_INTERVAL));
				config.setInterval(AhRestoreCommons.convertInt(interval));

				/**
				 * Set threshold
				 */
				colName = "threshold";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"idp_settings", colName);
				int threshold = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: IdpSettings.DEFAULT_THRESHOLD);
				config.setThreshold(threshold);
				
				/**
				 * Set filtermanagedhiveapbssid
				 */
				colName = "filtermanagedhiveapbssid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"idp_settings", colName);
				boolean filtermanagedhiveapbssid = (isColPresent ? AhRestoreCommons
						.convertStringToBoolean(xmlParser.getColVal(i, colName))
						: IdpSettings.DEFAULT_FILTER_MANAGED_HIVEAP_BSSID);
				config.setFilterManagedHiveAPBssid(filtermanagedhiveapbssid);

				/**
				 * Set owner
				 */

				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"idp_settings", colName);
				long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
				
				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'idp_settings' data be lost, cause: 'owner' column is not available.");
					continue;
				}
				
				config.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				configs.add(config);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get idp settings", e);
			}
		}
		return configs.size() > 0 ? configs : null;
	}

}