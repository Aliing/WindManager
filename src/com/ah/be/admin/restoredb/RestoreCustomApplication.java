package com.ah.be.admin.restoredb;

import static com.ah.be.admin.restoredb.AhRestoreNewMapTools.setMapCustomApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.CustomApplicationRule;

public class RestoreCustomApplication {
	
	
	/**
	 * Restore custom_application table
	 *
	 * @return true if table of custom_application restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreCustomApplication()
	{
		try {
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<CustomApplication> allCustomApplication = getAllCustomApplication(lstLogBo);
			if (null != allCustomApplication) {
				List<Long> lOldId = new ArrayList<Long>();

				for (CustomApplication policy : allCustomApplication) {
					lOldId.add(policy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allCustomApplication);

				for(int i=0; i<allCustomApplication.size(); i++)
				{
					setMapCustomApplication(lOldId.get(i), allCustomApplication.get(i).getId());
				}
			}
			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert custom application for custom application upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from custom_application_rule table
	 *
	 * @param lstLogBo -
	 * @return List<CustomApplicationRule> all CustomApplicationRule
	 * @throws AhRestoreColNotExistException -
	 *             if custom_application_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing custom_application_rule.xml.
	 */
	private static Map<String, List<CustomApplicationRule>> getAllCustomApplicationRuleInfo(Map<String, List<HmUpgradeLog>> lstLogBo)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of custom_application_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("custom_application_rule");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in custom_application_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<CustomApplicationRule>> allRules = new HashMap<String, List<CustomApplicationRule>>();

		boolean isColPresent;
		String colName;
		CustomApplicationRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new CustomApplicationRule();

			/**
			 * Set custom_application_id
			 */
			colName = "custom_application_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set detectionType
			 */
			colName = "detectiontype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			short detectionType = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : -1;
			if (detectionType == -1) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'custom_application_rule' data be lost, cause: 'detectiontype' column is not exist.");
				continue;
			}
			singleInfo.setDetectionType(detectionType);

			/**
			 * Set protocolId
			 */
			colName = "protocolid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			short protocolId = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : -1;
			if (protocolId == -1) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'custom_application_rule' data be lost, cause: 'protocolid' column is not exist.");
				continue;
			}
			singleInfo.setProtocolId(protocolId);
			
			/**
			 * Set headerNameType
			 */
			colName = "headernametype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			short headerNameType = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : 0;
			singleInfo.setHeaderNameType(headerNameType);
			
			/**
			 * Set ruleValue
			 */
			colName = "rulevalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			String ruleValue = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			singleInfo.setRuleValue(ruleValue);
			
			/**
			 * Set portNumber
			 */
			colName = "portnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"custom_application_rule", colName);
			int portNumber = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : -1;
			singleInfo.setPortNumber(portNumber);

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setRuleId((short) AhRestoreCommons.convertInt(ruleid));

			List<CustomApplicationRule> ruleInfo = allRules.get(id);
			if (null == ruleInfo) {
				ruleInfo = new ArrayList<CustomApplicationRule>();
				ruleInfo.add(singleInfo);
				allRules.put(id, ruleInfo);
			} else {
				ruleInfo.add(singleInfo);
			}
		}
		return allRules.size() > 0 ? allRules : null;
	}

	/**
	 * Get all information from custom_application table
	 *
	 * @param upLogs -
	 * @return List<CustomApplication> all CustomApplication BO
	 * @throws AhRestoreColNotExistException -
	 *             if custom_application.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing custom_application.xml.
	 */
	private static List<CustomApplication> getAllCustomApplication(List<HmUpgradeLog> upLogs)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of custom_application.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("custom_application");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in custom_application table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<CustomApplication> customappList = new ArrayList<CustomApplication>();
		Map<String, List<HmUpgradeLog>> lstLogBo = new HashMap<String, List<HmUpgradeLog>>();
		Map<String, List<CustomApplicationRule>> allRules = getAllCustomApplicationRuleInfo(lstLogBo);

		boolean isColPresent;
		String colName;
		CustomApplication singleApp;

		for (int i = 0; i < rowCount; i++)
		{
			singleApp = new CustomApplication();

			/**
			 * Set customAppName
			 */
			colName = "customappname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.trim().equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'custom_application' data be lost, cause: 'customappname' column is not exist.");
				continue;
			}
			singleApp.setCustomAppName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleApp.setId(AhRestoreCommons.convertLong(id));
			
			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singleApp.setDescription(AhRestoreCommons
				.convertString(description));
			
			/**
			 * Set customAppShortName
			 */
			colName = "customappshortname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String customAppShortName = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singleApp.setCustomAppShortName(AhRestoreCommons
				.convertString(customAppShortName));
			
			/**
			 * Set appGroupName
			 */
			colName = "appgroupname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String appGroupName = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singleApp.setAppGroupName(AhRestoreCommons
				.convertString(appGroupName));

			/**
			 * appCode
			 */
			colName = "appcode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"custom_application", colName);
			String appCode = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleApp.setAppCode(AhRestoreCommons.convertInt(appCode));
			
			/**
			 * Set idleTimeout
			 */
			colName = "idletimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			String idleTimeout = isColPresent ? xmlParser.getColVal(i, colName) : "300";
			singleApp.setIdleTimeout(AhRestoreCommons.convertInt(idleTimeout));
			
			/**
			 * Set deletedFlag
			 */
			colName = "deletedflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"custom_application", colName);
			boolean deletedFlag = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singleApp.setDeletedFlag(deletedFlag);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"custom_application", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'custom_application' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singleApp.setOwner(ownerDomain);

			singleApp.setRules(allRules.get(id));

			if (null != lstLogBo.get(id)) {
				// there is upgrde logs
				for (HmUpgradeLog upLog : lstLogBo.get(id)) {
					upLog.setFormerContent("A rule in Custom Application \""+name+"\" " + upLog.getFormerContent());
					upLog.setPostContent(upLog.getPostContent()+" the rule in Custom Application \""+name+"\".");
					upLog.setRecommendAction("No action is required.");
					upLog.setOwner(ownerDomain);
					upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
					upLog.setAnnotation("Click to add an annotation");
					upLogs.add(upLog);
				}
			}
			customappList.add(singleApp);
		}

		return customappList.size() > 0 ? customappList : null;
	}

}
