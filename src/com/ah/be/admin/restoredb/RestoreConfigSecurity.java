/**
 *@filename		RestoreConfigSecurity.java
 *@version
 *@author		Fiona
 *@createtime	2007-11-9 PM 04:49:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *remove dns alg 2009-02-05
 *
 *add dns alg 2009-05-21
 */
package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MacPolicyRule;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.RadioProfileWmmInfo;
import com.ah.bo.wlan.RadioProfileWmmInfo.AccessCategory;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.ui.actions.config.RadioProfileAction;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class RestoreConfigSecurity {

	/**
	 * Get all information from ip_policy_rule table
	 *
	 * @param lstLogBo -
	 * @return List<IpPolicyRule> all IpPolicyRule
	 * @throws AhRestoreColNotExistException -
	 *             if ip_policy_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing ip_policy_rule.xml.
	 */
	private static Map<String, List<IpPolicyRule>> getAllIpRuleInfo(Map<String, List<HmUpgradeLog>> lstLogBo)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ip_policy_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ip_policy_rule");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ip_policy_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<IpPolicyRule>> allRules = new HashMap<String, List<IpPolicyRule>>();

		boolean isColPresent;
		String colName;
		IpPolicyRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new IpPolicyRule();

			/**
			 * Set ip_policy_id
			 */
			colName = "ip_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set filteraction
			 */
			colName = "filteraction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			short filteraction = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_ACTION_DENY;
			singleInfo.setFilterAction(filteraction);

			/**
			 * Set actionlog
			 */
			colName = "actionlog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			short actionlog = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_LOGGING_OFF;
			if ((IpPolicyRule.POLICY_ACTION_DENY == filteraction && IpPolicyRule.POLICY_LOGGING_INITIATE == actionlog)
				|| (IpPolicyRule.POLICY_ACTION_TRAFFIC_DROP == filteraction && IpPolicyRule.POLICY_LOGGING_BOTH == actionlog)) {
				singleInfo.setActionLog(IpPolicyRule.POLICY_LOGGING_DROP);
			} else {
				singleInfo.setActionLog(actionlog);
			}

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setRuleId((short) AhRestoreCommons.convertInt(ruleid));

			/**
			 * Set source_ip_id
			 */
			colName = "source_ip_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String source_ip_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			if (!"".equals(source_ip_id)) {
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				IpAddress ipAddress = RestoreConfigNetwork.getNewIpNetworkObj(source_ip_id, upgradeLog);
				if(null != ipAddress && ipAddress.getTypeFlag() == IpAddress.TYPE_IP_RANGE){
					continue;
				}
				singleInfo.setSourceIp(ipAddress);
				// there is upgrade log to record
				if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
					List<HmUpgradeLog> allLog = lstLogBo.get(id);
					if (null == allLog) {
						allLog = new ArrayList<HmUpgradeLog>();
						allLog.add(upgradeLog);
						lstLogBo.put(id, allLog);
					} else {
						allLog.add(upgradeLog);
					}
				}
			}

			/**
			 * Set destination_ip_id
			 */
			colName = "destination_ip_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String destination_ip_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(destination_ip_id)) {
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				IpAddress ipAddress = RestoreConfigNetwork.getNewIpNetworkObj(destination_ip_id, upgradeLog);
				if(null != ipAddress && ipAddress.getTypeFlag() == IpAddress.TYPE_IP_RANGE){
					continue;
				}
				singleInfo.setDesctinationIp(ipAddress);
				// there is upgrade log to record
				if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
					List<HmUpgradeLog> allLog = lstLogBo.get(id);
					if (null == allLog) {
						allLog = new ArrayList<HmUpgradeLog>();
						allLog.add(upgradeLog);
						lstLogBo.put(id, allLog);
					} else {
						allLog.add(upgradeLog);
					}
				}
			}

			/**
			 * Set network_service_id
			 */
			colName = "network_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String network_service_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(network_service_id)) {
				Long serviceId = AhRestoreNewMapTools.getMapNetworkService(AhRestoreCommons.convertLong(network_service_id));
				if(null != serviceId) {
					singleInfo.setNetworkService(AhRestoreNewTools.CreateBoWithId(NetworkService.class, serviceId));
				}
			}
			
			/**
			 * Set customapp_service_id
			 */
			colName = "customapp_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			String customapp_service_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(customapp_service_id)) {
				CustomApplication customApp = AhRestoreNewMapTools.getMapCustomApplication(AhRestoreCommons.convertLong(customapp_service_id));
				if(null != customApp) {
					singleInfo.setCustomApp(customApp);
				}
			}
			
			/**
			 * Set servicetype
			 */
			colName = "servicetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy_rule", colName);
			short servicetype = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.RULE_NETWORKSERVICE_TYPE;
			singleInfo.setServiceType(servicetype);
			
			List<IpPolicyRule> ruleInfo = allRules.get(id);
			if (null == ruleInfo) {
				ruleInfo = new ArrayList<IpPolicyRule>();
				ruleInfo.add(singleInfo);
				allRules.put(id, ruleInfo);
			} else {
				ruleInfo.add(singleInfo);
			}
		}
		return allRules.size() > 0 ? allRules : null;
	}

	/**
	 * Get all information from ip_policy table
	 *
	 * @param upLogs -
	 * @return List<IpPolicy> all IpPolicy BO
	 * @throws AhRestoreColNotExistException -
	 *             if ip_policy.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing ip_policy.xml.
	 */
	private static List<IpPolicy> getAllIpPolicy(List<HmUpgradeLog> upLogs)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ip_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ip_policy");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ip_policy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<IpPolicy> policy = new ArrayList<IpPolicy>();
		Map<String, List<HmUpgradeLog>> lstLogBo = new HashMap<String, List<HmUpgradeLog>>();
		Map<String, List<IpPolicyRule>> allRules = getAllIpRuleInfo(lstLogBo);

		boolean isColPresent;
		String colName;
		IpPolicy singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new IpPolicy();

			/**
			 * Set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.trim().equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_policy' data be lost, cause: 'policyname' column is not exist.");
				continue;
			}
			singlePolicy.setPolicyName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			if (name.equals(BeParaModule.DEFAULT_IPPOLICY_NAME)) {
				// set default ip policy new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("policyname", name);
				IpPolicy newIp = HmBeParaUtil.getDefaultProfile(IpPolicy.class, map);
				if (null != newIp) {
					AhRestoreNewMapTools.setMapIpPolicy(AhRestoreCommons.convertLong(id), newIp.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			singlePolicy.setDefaultFlag(false);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ip_policy", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singlePolicy.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_policy", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			singlePolicy.setRules(allRules.get(id));

			if (null != lstLogBo.get(id)) {
				// there is upgrde logs
				for (HmUpgradeLog upLog : lstLogBo.get(id)) {
					upLog.setFormerContent("A rule in IP Policy \""+name+"\" " + upLog.getFormerContent());
					upLog.setPostContent(upLog.getPostContent()+" the rule in IP Policy \""+name+"\".");
					upLog.setRecommendAction("No action is required.");
					upLog.setOwner(ownerDomain);
					upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
					upLog.setAnnotation("Click to add an annotation");
					upLogs.add(upLog);
				}
			}
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore ip_policy table
	 *
	 * @return true if table of ip_policy restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreIpPolicy()
	{
		try {
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<IpPolicy> allPolicy = getAllIpPolicy(lstLogBo);
			if (null != allPolicy) {
				List<Long> lOldId = new ArrayList<Long>();

				for (IpPolicy policy : allPolicy) {
					lOldId.add(policy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allPolicy);

				for(int i=0; i<allPolicy.size(); i++)
				{
					AhRestoreNewMapTools.setMapIpPolicy(lOldId.get(i), allPolicy.get(i).getId());
				}
			}
			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option for ip policy upgrade log error");
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
	 * Get all information from mac_policy_rule table
	 *
	 * @return List<MacPolicyRule> all MacPolicyRule
	 * @throws AhRestoreColNotExistException -
	 *             if mac_policy_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing mac_policy_rule.xml.
	 */
	private static List<MacPolicyRule> getAllMacRuleInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException, Exception
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of mac_policy_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mac_policy_rule");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in mac_policy_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MacPolicyRule> ruleInfo = new ArrayList<MacPolicyRule>();

		boolean isColPresent;
		String colName;
		MacPolicyRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new MacPolicyRule();

			/**
			 * Set mac_policy_id
			 */
			colName = "mac_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set filteraction
			 */
			colName = "filteraction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			short filteraction = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_ACTION_DENY;
			singleInfo.setFilterAction(filteraction);

			/**
			 * Set actionlog
			 */
			colName = "actionlog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			short actionlog = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_LOGGING_OFF;
			if ((IpPolicyRule.POLICY_ACTION_DENY == filteraction && IpPolicyRule.POLICY_LOGGING_INITIATE == actionlog)
				|| (IpPolicyRule.POLICY_ACTION_TRAFFIC_DROP == filteraction && IpPolicyRule.POLICY_LOGGING_BOTH == actionlog)) {
				singleInfo.setActionLog(IpPolicyRule.POLICY_LOGGING_DROP);
			} else {
				singleInfo.setActionLog(actionlog);
			}

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setRuleId((short) AhRestoreCommons.convertInt(ruleid));

			/**
			 * Set sourcemask
			 */
			colName = "sourcemask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			short sourcemask = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: MacPolicyRule.MAC_POLICY_MASK_THREE;

			/**
			 * Set destinationmask
			 */
			colName = "destinationmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			short destinationmask = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser.getColVal(i,
				colName)) : MacPolicyRule.MAC_POLICY_MASK_THREE;

			/**
			 * Set source_mac_id
			 */
			colName = "source_mac_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			String source_mac_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(source_mac_id) && MacPolicyRule.MAC_POLICY_MASK_ONE != sourcemask) {
				// get mac address new id from old one
				Long macId = AhRestoreNewMapTools.getMapMacAddress(AhRestoreCommons.convertLong(source_mac_id));
				if(null != macId) {
					MacOrOui source = AhRestoreNewTools.CreateBoWithId(MacOrOui.class, macId);
					if (MacPolicyRule.MAC_POLICY_MASK_TWO == sourcemask) {
						source = generateMacOuiObject(macId);
					}
					singleInfo.setSourceMac(source);
				}
			}

			/**
			 * Set destination_mac_id
			 */
			colName = "destination_mac_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy_rule", colName);
			String destination_mac_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(destination_mac_id) && MacPolicyRule.MAC_POLICY_MASK_ONE != destinationmask) {
				// get mac address new id from old one
				Long macId = AhRestoreNewMapTools.getMapMacAddress(AhRestoreCommons.convertLong(destination_mac_id));
				if(null != macId) {
					MacOrOui destination = AhRestoreNewTools.CreateBoWithId(MacOrOui.class, macId);
					if (MacPolicyRule.MAC_POLICY_MASK_TWO == destinationmask) {
						destination = generateMacOuiObject(macId);
					}
					singleInfo.setDestinationMac(destination);
				}
			}
			ruleInfo.add(singleInfo);
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from mac_policy table
	 *
	 * @return List<MacPolicy> all MacPolicy BO
	 * @throws AhRestoreColNotExistException -
	 *             if mac_policy.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing mac_policy.xml.
	 */
	private static List<MacPolicy> getAllMacPolicy()
		throws AhRestoreColNotExistException,
		AhRestoreException, Exception
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of mac_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mac_policy");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in mac_policy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MacPolicy> policy = new ArrayList<MacPolicy>();

		boolean isColPresent;
		String colName;
		MacPolicy singlePolicy;
		List<MacPolicyRule> allRule = null;
		// the main table must have records
		if (rowCount > 0) {
			allRule = getAllMacRuleInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new MacPolicy();

			/**
			 * Set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_policy' data be lost, cause: 'policyname' column is not exist.");
				continue;
			}
			singlePolicy.setPolicyName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"mac_policy", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_policy", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			if (null != allRule)
			{
				List<MacPolicyRule> thisRuleInfo = new ArrayList<MacPolicyRule>();
				for (MacPolicyRule ruleInfo : allRule)
				{
					if (id.equals(ruleInfo.getRestoreId()))
					{
						thisRuleInfo.add(ruleInfo);
					}
				}
				singlePolicy.setRules(thisRuleInfo);
			}
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore mac_policy table
	 *
	 * @return true if table of mac_policy restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreMacPolicy()
	{
		try {
			List<MacPolicy> allPolicy = getAllMacPolicy();
			if (null != allPolicy) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MacPolicy policy : allPolicy) {
					lOldId.add(policy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allPolicy);

				for(int i=0; i<allPolicy.size(); i++)
				{
					AhRestoreNewMapTools.setMapMacPolicy(lOldId.get(i), allPolicy.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from radio_profile table
	 *
	 * @return List<RadioProfile> all RadioProfile BO
	 * @throws AhRestoreColNotExistException -
	 *             if radio_profile.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radio_profile.xml.
	 */
	private static List<RadioProfile> getAllRadioProfile(List<HmUpgradeLog> lstLogBo) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radio_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radio_profile");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radio_profile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadioProfile> radio = new ArrayList<RadioProfile>();
		List<RadioProfileWmmInfo> allWmm = getAllRadioProfileWmmInfo();
		Map<String, Set<MacOrOui>> allMacOrOui = getAllIdsMacOrOui();

		boolean isColPresent;
		String colName;
		RadioProfile singleRadio;

		overlap:
		for (int i = 0; i < rowCount; i++)
		{
			singleRadio = new RadioProfile();

			/**
			 * Set radioname
			 */
			colName = "radioname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radio_profile' data be lost, cause: 'radioname' column is not exist.");
				continue;
			}
			singleRadio.setRadioName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleRadio.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Get defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean defaultflag = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));

			if (defaultflag) {
				// High-Capacity-11na-Profile change to High-Capacity-40MHz-11na-Profile
				if (BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA_OLD.equals(name)) {
					name = BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA;

				// High-Capacity-11ng-Profile change to High-Capacity-20MHz-11ng-Profile
				} else if (BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG_OLD.equals(name)) {
					name = BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG;
				}
			}

			for (String singleName : BeParaModule.RADIO_PROFILE_NAMES) {
				if (name.equals(singleName)) {
					// set default radio profile new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("radioname", name);
					RadioProfile newRadio = HmBeParaUtil.getDefaultProfile(RadioProfile.class, map);
					if (null != newRadio) {
						AhRestoreNewMapTools.setMapRadioProfile(singleRadio.getId(), newRadio.getId());
					}
					continue overlap;
				}
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radio_profile' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singleRadio.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set beaconperiod
			 */
			colName = "beaconperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short beaconperiod = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 100;

			// upgrade from old version
			if (beaconperiod < 40) {
				// add upgrade log
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent("The Beacon Period of Radio Profile \""+name+"\" is "+beaconperiod+" TUs.");
				upgradeLog.setPostContent("The range of Beacon Period changes from (25-1000) to (40-3500), so defined by default value 100 TUs.");
				upgradeLog.setRecommendAction("No action is required.");
				upgradeLog.setOwner(singleRadio.getOwner());
				upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),singleRadio.getOwner().getTimeZoneString()));
				upgradeLog.setAnnotation("Click to add an annotation");
				lstLogBo.add(upgradeLog);
				singleRadio.setBeaconPeriod((short)100);
			} else {
				singleRadio.setBeaconPeriod(beaconperiod);
			}

			/**
			 * Set holdtime
			 */
			colName = "holdtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String holdtime = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			singleRadio.setHoldTime((short)AhRestoreCommons.convertInt(holdtime));

			/**
			 * Set interval
			 */
			colName = "interval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String interval = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			singleRadio.setInterval(AhRestoreCommons.convertInt(interval));

			/**
			 * Set maxclients
			 */
			colName = "maxclients";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String maxclients = isColPresent ? xmlParser.getColVal(i, colName) : "100";
			singleRadio.setMaxClients((short)AhRestoreCommons.convertInt(maxclients));

			/**
			 * Set mincount
			 */
			colName = "mincount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String mincount = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			singleRadio.setMinCount((short)AhRestoreCommons.convertInt(mincount));

			/**
			 * Set radiomode
			 */
			colName = "radiomode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String radiomode = isColPresent ? xmlParser.getColVal(i, colName) :
				String.valueOf(RadioProfile.RADIO_PROFILE_MODE_BG);
			singleRadio.setRadioMode((short)AhRestoreCommons.convertInt(radiomode));

			/**
			 * Set turboMode
			 */
			colName = "turboMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String turboMode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setTurboMode(AhRestoreCommons.convertStringToBoolean(turboMode));

			/**
			 * Set enabledbssidspoof
			 */
			colName = "enabledbssidspoof";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enabledbssidspoof = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setEnabledBssidSpoof(AhRestoreCommons.convertStringToBoolean(enabledbssidspoof));

			/**
			 * Set roamingthreshold
			 */
			colName = "roamingthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String roamingthreshold = isColPresent ? xmlParser.getColVal(i, colName) :
				String.valueOf(RadioProfile.RADIO_ROAMING_THRESHOLD_VERYLOW);
			singleRadio.setRoamingThreshold((short)AhRestoreCommons.convertInt(roamingthreshold));

			/**
			 * Set shortpreamble
			 */
			colName = "shortpreamble";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String shortpreamble = isColPresent ? xmlParser.getColVal(i, colName) :
				String.valueOf(RadioProfile.RADIO_PROFILE_PREAMBLE_SHORT);

			/**
			 * Set usedefaultchannelmodel
			 */
			colName = "usedefaultchannelmodel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean defaultModel = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));

			/**
			 * Set channelregion
			 */
			colName = "channelregion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short channelregion = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US;

			/**
			 * Set channelmodel
			 */
			colName = "channelmodel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short channelmodel = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3;

			/**
			 * Set channelvalue
			 */
			colName = "channelvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String channelvalue = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName))
				: RadioProfile.DEFAULT_CHANNEL_VALUE;

			if (singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
				|| singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA) {
				singleRadio.setShortPreamble(RadioProfile.RADIO_PROFILE_PREAMBLE_SHORT);
				singleRadio.setUseDefaultChannelModel(true);
				singleRadio.setChannelRegion(RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US);
				singleRadio.setChannelModel(RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3);
				singleRadio.setChannelValue(RadioProfile.DEFAULT_CHANNEL_VALUE);
			} else {
				singleRadio.setShortPreamble((short)AhRestoreCommons.convertInt(shortpreamble));
				singleRadio.setUseDefaultChannelModel(defaultModel);
				singleRadio.setChannelRegion(channelregion);
				singleRadio.setChannelModel(channelmodel);
				singleRadio.setChannelValue(channelvalue);
			}

			/*
			 * For channel switch
			 */
			/**
			 * Set channelswitch
			 */
			colName = "channelswitch";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean channelswitch = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singleRadio.setChannelSwitch(channelswitch);

			/**
			 * Set stationconnect
			 */
			colName = "stationconnect";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean stationconnect = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			singleRadio.setStationConnect(stationconnect);

			/**
			 * Set iuthreshold
			 */
			colName = "iuthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short iuthreshold = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 25;
			singleRadio.setIuThreshold(iuthreshold);

			/**
			 * Set crcchannelthr
			 */
			colName = "crcchannelthr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short crcchannelthr = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 25;
			singleRadio.setCrcChannelThr(crcchannelthr);

			// only a or bg radio profile need to config antenna
			if (singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
				|| singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG) {
				/**
				 * Set antenna for hiveap20
				 */
				colName = "antennatype20";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
				if (isColPresent) {
					singleRadio.setAntennaType20((short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)));
				} else {
					/**
					 * Set antenna
					 */
					colName = "antenna";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
					boolean antenna = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
					if (antenna) {
						singleRadio.setAntennaType20(RadioProfile.RADIO_ANTENNA20_TYPE_E);
					}
				}

				/**
				 * Set antenna for hiveap28
				 */
				colName = "antennatype28";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
				if (isColPresent) {
					singleRadio.setAntennaType28((short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)));
				} else {
					/**
					 * Set usefixedantenna
					 */
					colName = "usefixedantenna";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
					boolean usefixedantenna = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));

					if (usefixedantenna) {
						/**
						 * Set fixedantennatype
						 */
						colName = "fixedantennatype";
						isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
						short fixedantennatype = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
							: RadioProfile.RADIO_ANTENNA28_TYPE_A;
						singleRadio.setAntennaType28(fixedantennatype);
					}
				}
			}

			/**
			 * Set threshold
			 */
			colName = "threshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String threshold = isColPresent ? xmlParser.getColVal(i, colName) : "70";
			singleRadio.setThreshold((short)AhRestoreCommons.convertInt(threshold));

			/**
			 * Set triggertime
			 */
			colName = "triggertime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String triggertime = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			singleRadio.setTriggerTime((short)AhRestoreCommons.convertInt(triggertime));

			/**
			 * Set defaultflag
			 */
			singleRadio.setDefaultFlag(false);
			singleRadio.setCliDefaultFlag(false);

			/**
			 * Set backgroundscan
			 */
			colName = "backgroundscan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String backgroundscan = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setBackgroundScan(AhRestoreCommons.convertStringToBoolean(backgroundscan));

			/**
			 * Set backhaulfailover
			 */
			colName = "backhaulfailover";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String backhaulfailover = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setBackhaulFailover(AhRestoreCommons.convertStringToBoolean(backhaulfailover));

			/**
			 * Set loadbalance
			 */
			colName = "loadbalance";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String loadbalance = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setLoadBalance(AhRestoreCommons.convertStringToBoolean(loadbalance));

			/**
			 * Set trafficvoice
			 */
			colName = "trafficvoice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String trafficvoice = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setTrafficVoice(AhRestoreCommons.convertStringToBoolean(trafficvoice));

			/**
			 * Set clientconnect
			 */
			colName = "clientconnect";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String clientconnect = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadio.setClientConnect(AhRestoreCommons.convertStringToBoolean(clientconnect));

			/**
			 * Set powersave
			 */
			colName = "powersave";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String powersave = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setPowerSave(AhRestoreCommons.convertStringToBoolean(powersave));

			/**
			 * Set enabledfs
			 */
			colName = "enabledfs";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enabledfs = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
					singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A ||
					singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
				singleRadio.setEnableDfs(AhRestoreCommons.convertStringToBoolean(enabledfs));
			}

			/**
			 * Set enableradardetect
			 */
			colName = "enableradardetect";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enableradardetect = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			singleRadio.setEnableRadarDetect(AhRestoreCommons.convertStringToBoolean(enableradardetect));

			/**
			 * Set radiorange
			 */
			colName = "radiorange";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String radiorange = isColPresent ? xmlParser.getColVal(i, colName) : "300";
			singleRadio.setRadioRange(AhRestoreCommons.convertInt(radiorange));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set scanAllChannel
			 */
			colName = "scanAllChannel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			String scanAllChannel = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadio.setScanAllChannel(AhRestoreCommons.convertStringToBoolean(scanAllChannel));
			

			/**
			 * Set scanChannels
			 */
			colName = "scanChannels";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String scanChannels = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setScanChannels(AhRestoreCommons.convertString(scanChannels));
			
			/**
			 * Set dellTime
			 */
			colName = "dellTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			String dellTime = isColPresent ? xmlParser.getColVal(i, colName) : "1200";
			singleRadio.setDellTime(AhRestoreCommons.convertInt(dellTime));
			
			/**
			 * Set enablechannel
			 */
			colName = "enablechannel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enablechannel = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadio.setEnableChannel(AhRestoreCommons.convertStringToBoolean(enablechannel));

			/**
			 * Set fromhour
			 */
			colName = "fromhour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String fromhour = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleRadio.setFromHour((short)AhRestoreCommons.convertInt(fromhour));

			/**
			 * Set fromminute
			 */
			colName = "fromminute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String fromminute = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleRadio.setFromMinute((short)AhRestoreCommons.convertInt(fromminute));

			/**
			 * Set tohour
			 */
			colName = "tohour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String tohour = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleRadio.setToHour((short)AhRestoreCommons.convertInt(tohour));

			/**
			 * Set tominute
			 */
			colName = "tominute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String tominute = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleRadio.setToMinute((short)AhRestoreCommons.convertInt(tominute));

			/**
			 * Set channelclient
			 */
			colName = "channelclient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String channelclient = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleRadio.setChannelClient((short)AhRestoreCommons.convertInt(channelclient));

			/**
			 * Set enablepower
			 */
			colName = "enablepower";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enablepower = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadio.setEnablePower(AhRestoreCommons.convertStringToBoolean(enablepower));

			/**
			 * Set transmitpower
			 */
			colName = "transmitpower";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short transmitpower = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 20;
			singleRadio.setTransmitPower(transmitpower<10 ? 20:transmitpower);

			/**
			 * Set aggregatempdu
			 */
			colName = "aggregatempdu";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String aggregatempdu = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadio.setAggregateMPDU(AhRestoreCommons.convertStringToBoolean(aggregatempdu));

			/**
			 * Set deny11b
			 */
			colName = "deny11b";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean deny11b = false;
			if (isColPresent) {
				deny11b = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			} else {
				/**
				 * Set allow11b
				 */
				colName = "allow11b";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
				if (isColPresent) {
					deny11b = !AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				}
			}
			singleRadio.setDeny11b(deny11b);

			/**
			 * Set deny11abg
			 */
			colName = "deny11abg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			boolean deny11n = false;
			if (isColPresent) {
				deny11n = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			} else {
				/**
				 * Set allow11n
				 */
				colName = "allow11n";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
				if (isColPresent) {
					deny11n = !AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				}
			}
			singleRadio.setDeny11abg(deny11n);

			/**
			 * Set guardinterval
			 */
			colName = "guardinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String guardinterval = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRadio.setGuardInterval(AhRestoreCommons.convertStringToBoolean(guardinterval));

			/**
			 * Set channelwidth
			 */
			colName = "channelwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String channelwidth = isColPresent ? xmlParser.getColVal(i, colName) :
				String.valueOf(RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
			singleRadio.setChannelWidth((short)AhRestoreCommons.convertInt(channelwidth));

			/**
			 * Set transmitchain
			 */
			colName = "transmitchain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short transmitchain = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				RadioProfile.RADIO_PROFILE_CHAIN_3;
			singleRadio.setTransmitChain(transmitchain);

			/**
			 * Set receivechain
			 */
			colName = "receivechain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short receivechain = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				RadioProfile.RADIO_PROFILE_CHAIN_3;
			singleRadio.setReceiveChain(receivechain);

			/**
			 * Set usedefaultchain
			 */
			colName = "usedefaultchain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			// add this field from 3.4r1
			if (isColPresent) {
				singleRadio.setUseDefaultChain(AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName)));
			} else {
				if (singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
					|| singleRadio.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG) {
					singleRadio.setUseDefaultChain(true);
					singleRadio.setTransmitChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
					singleRadio.setReceiveChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
				// only 11n mode support this field
				} else {
					if (RadioProfile.RADIO_PROFILE_CHAIN_3 == transmitchain && RadioProfile.RADIO_PROFILE_CHAIN_3 == receivechain) {
						singleRadio.setUseDefaultChain(true);
						singleRadio.setTransmitChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
						singleRadio.setReceiveChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
					} else {
						singleRadio.setUseDefaultChain(false);
					}
				}
			}

			/**
			 * Set enableinterfernce
			 */
			colName = "enableinterfernce";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enableinterfernce = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRadio.setEnableInterfernce(AhRestoreCommons.convertStringToBoolean(enableinterfernce));

			/**
			 * Set crcthreshold
			 */
			colName = "crcthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String crcthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "20";
			singleRadio.setCrcThreshold((short)AhRestoreCommons.convertInt(crcthreshold));

			/**
			 * Set channelthreshold
			 */
			colName = "channelthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String channelthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "20";
			singleRadio.setChannelThreshold((short)AhRestoreCommons.convertInt(channelthreshold));

			/**
			 * Set averageinterval
			 */
			colName = "averageinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String averageinterval = isColPresent ? xmlParser.getColVal(i, colName) : "5";
			singleRadio.setAverageInterval((short)AhRestoreCommons.convertInt(averageinterval));

			/**
			 * Set slathoughput
			 */
			colName = "slathoughput";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String slathoughput = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			singleRadio.setSlaThoughput((short)AhRestoreCommons.convertInt(slathoughput));

			/**
			 * Set enablecca
			 */
			colName = "enablecca";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			String enablecca = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadio.setEnableCca(AhRestoreCommons.convertStringToBoolean(enablecca));

			/**
			 * Set defaultccavalue
			 */
			colName = "defaultccavalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short defaultccavalue = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				(short)33;
			singleRadio.setDefaultCcaValue(defaultccavalue);

			/**
			 * Set maxccavalue
			 */
			colName = "maxccavalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile", colName);
			short maxccavalue = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				(short)55;
			singleRadio.setMaxCcaValue(maxccavalue);

			Map<String, RadioProfileWmmInfo> items;
			if (null == allWmm) {
				items = RadioProfileAction.getDefaultWmmInfo(singleRadio);
			} else {
				items = new HashMap<String, RadioProfileWmmInfo>();
				for (RadioProfileWmmInfo ruleInfo : allWmm) {
					if (items.size() == 4) {
						break;
					}
					if (id.equals(ruleInfo.getRestoreId())) {
						items.put(ruleInfo.getkey(), ruleInfo);
					}
				}
			}
			singleRadio.setWmmItems(items);

			/**
			 * Set enablehighdensity
			 */
			colName = "enablehighdensity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			String enablehighdensity = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			singleRadio.setEnableHighDensity(AhRestoreCommons
					.convertStringToBoolean(enablehighdensity));

			/**
			 * Set highdensitytransmitrate
			 */
			if(singleRadio.isEnableHighDensity()){
				colName = "highdensitytransmitrate";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				short highdensitytransmitrate = isColPresent ? (short) AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: RadioProfile.HIGH_DENSITY_TRANSMIT_RATE_LOW;
				singleRadio.setHighDensityTransmitRate(highdensitytransmitrate);
			}else{
				singleRadio.setHighDensityTransmitRate(RadioProfile.HIGH_DENSITY_TRANSMIT_RATE_LOW);
			}


			/**
			 * Set enablebroadcastprobe
			 */
			if (singleRadio.isEnableHighDensity()) {
				colName = "enablebroadcastprobe";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enablebroadcastprobe = isColPresent ? xmlParser
						.getColVal(i, colName) : "false";
				singleRadio.setEnableBroadcastProbe(AhRestoreCommons
						.convertStringToBoolean(enablebroadcastprobe));
			}else{
				singleRadio.setEnableBroadcastProbe(false);
			}
			
			
			/**
			 * Set enableSupressBPRByOUI
			 */
			Set<MacOrOui> macOuis = new HashSet<MacOrOui>();
			if (singleRadio.isEnableHighDensity()) {
				if(singleRadio.isEnableBroadcastProbe()){
					singleRadio.setEnableSupressBPRByOUI(false);
					singleRadio.setSupressBprOUIs(null);
				}else{
					colName = "enableSupressBPRByOUI";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
					String enableSupressBPRByOUI = isColPresent ? xmlParser
							.getColVal(i, colName) : "true";
					singleRadio.setEnableSupressBPRByOUI(AhRestoreCommons
							.convertStringToBoolean(enableSupressBPRByOUI));
					
					if(null != allMacOrOui && !allMacOrOui.isEmpty() && null != allMacOrOui.get(singleRadio.getId().toString())){
						singleRadio.setSupressBprOUIs(allMacOrOui.get(singleRadio.getId().toString()));
					}else{
						Map<String, Object> para = new HashMap<String, Object>();
						for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
							if(macInfo[0].startsWith("Samsung")){
								para.put("macOrOuiName", macInfo[0]);
								macOuis.add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, para));
							}
						}
						singleRadio.setSupressBprOUIs(macOuis);
					}
				}
			}else{
				singleRadio.setEnableSupressBPRByOUI(true);
				if (null != allMacOrOui && !allMacOrOui.isEmpty() && null != allMacOrOui.get(singleRadio.getId().toString())) {
					singleRadio.setSupressBprOUIs(allMacOrOui.get(singleRadio.getId().toString()));
				} else {
					Map<String, Object> para = new HashMap<String, Object>();
					for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
						if(macInfo[0].startsWith("Samsung")){
							para.put("macOrOuiName", macInfo[0]);
							macOuis.add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, para));
						}
					}
					singleRadio.setSupressBprOUIs(macOuis);
				}
			}
			
			
			

			/**
			 * Set enablebandsteering
			 */
			if(RestoreConfigNetwork.RESTORE_BEFORE_CASABLANCA_FLAG){
				if(singleRadio.isEnableHighDensity()){
					colName = "enablebandsteering";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
					String enablebandsteering = isColPresent ? xmlParser.getColVal(i,
							colName) : "false";
					singleRadio.setEnableBandSteering(AhRestoreCommons
							.convertStringToBoolean(enablebandsteering));
				}else{
					singleRadio.setEnableBandSteering(false);
				}
			}else{
				colName = "enablebandsteering";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enablebandsteering = isColPresent ? xmlParser.getColVal(i,
						colName) : "false";
				singleRadio.setEnableBandSteering(AhRestoreCommons
						.convertStringToBoolean(enablebandsteering));
			}

			/**
			 * set bandSteeringmode
			 */
			colName = "bandsteeringmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			short bandSteeringmode = isColPresent ? (short) AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.BAND_STEERING_MODE_BALANCEBAND;
			singleRadio.setBandSteeringMode(bandSteeringmode);

			/**
			 * set limitnumber
			 */
			colName = "limitnumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int limitnumber = isColPresent ?  AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_LIMIT_NUMBER;
			singleRadio.setLimitNumber(limitnumber);

			/**
			 * set minimumratio
			 */
			colName = "minimumratio";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int minimumratio = isColPresent ?  AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_MINIMUM_RATION;
			singleRadio.setMinimumRatio(minimumratio);

			/**
			 * Set enablecontinuousprobe
			 */
			if (singleRadio.isEnableHighDensity()) {
				colName = "enablecontinuousprobe";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enablecontinuousprobe = isColPresent ? xmlParser
						.getColVal(i, colName) : "false";
				singleRadio.setEnableContinuousProbe(AhRestoreCommons
						.convertStringToBoolean(enablecontinuousprobe));
			}else{
				singleRadio.setEnableContinuousProbe(false);
			}

			/**
			 * Set enableclientloadbalance
			 */
			if(RestoreConfigNetwork.RESTORE_BEFORE_CASABLANCA_FLAG){
				if (singleRadio.isEnableHighDensity()) {
					colName = "enableclientloadbalance";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
					String enableclientloadbalance = isColPresent ? xmlParser
							.getColVal(i, colName) : "false";
					singleRadio.setEnableClientLoadBalance(AhRestoreCommons
							.convertStringToBoolean(enableclientloadbalance));
				}else{
					singleRadio.setEnableClientLoadBalance(false);
				}
			}else{
				colName = "enableclientloadbalance";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enableclientloadbalance = isColPresent ? xmlParser
						.getColVal(i, colName) : "false";
				singleRadio.setEnableClientLoadBalance(AhRestoreCommons
						.convertStringToBoolean(enableclientloadbalance));
			}
			
			

			colName = "loadbalancingmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			short loadbalancingmode = isColPresent ? (short) AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.LOAD_BALANCE_MODE_AIRTIME_BASED;
			singleRadio.setLoadBalancingMode(loadbalancingmode);

			/**
			 * Set crcerrorlimit
			 */
			colName = "crcerrorlimit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int crcerrorlimit = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_MAX_CRC_ERROR_LIMIT;
			singleRadio.setCrcErrorLimit(crcerrorlimit);

			/**
			 * Set culimit
			 */
			colName = "culimit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int culimit = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : RadioProfile.DEFAULT_SA_MINIMUM;
			singleRadio.setCuLimit(culimit > 5 ? RadioProfile.DEFAULT_SA_MINIMUM : culimit);

			/**
			 * Set maxinterference
			 */
			colName = "maxinterference";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int maxinterference = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_MAX_INTERFERENCE;
			singleRadio.setMaxInterference(maxinterference);

			/**
			 * Set clientholdtime
			 */
			colName = "clientholdtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int clientholdtime = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_CLIENT_HOLD_TIME;
			singleRadio.setClientHoldTime(clientholdtime);

			/**
			 * Set queryInterval
			 */
			colName = "queryInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int queryInterval = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_QUERY_INTERVAL_TIME;
			singleRadio.setQueryInterval(queryInterval);

			/**
			 * Set enablesafetynet
			 */
			if(RestoreConfigNetwork.RESTORE_BEFORE_CASABLANCA_FLAG){
				if (singleRadio.isEnableHighDensity()) {
					colName = "enablesafetynet";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
					String enablesafetynet = isColPresent ? xmlParser.getColVal(i,
							colName) : "true";
					singleRadio.setEnableSafetyNet(AhRestoreCommons
							.convertStringToBoolean(enablesafetynet));
				}else{
					singleRadio.setEnableSafetyNet(true);
				}
			}else{
				colName = "enablesafetynet";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enablesafetynet = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				singleRadio.setEnableSafetyNet(AhRestoreCommons
						.convertStringToBoolean(enablesafetynet));
			}
			

			/**
			 * Set safetynettimeout
			 */
			colName = "safetynettimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int safetynettimeout = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_SAFETY_NET_TIMEOUT;
			singleRadio.setSafetyNetTimeout(safetynettimeout);

			/**
			 * Set enablesuppress
			 */
			if(RestoreConfigNetwork.RESTORE_BEFORE_CASABLANCA_FLAG){
				if (singleRadio.isEnableHighDensity()) {
					colName = "enablesuppress";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"radio_profile", colName);
					String enablesuppress = isColPresent ? xmlParser.getColVal(i,
							colName) : "false";
					singleRadio.setEnableSuppress(AhRestoreCommons
							.convertStringToBoolean(enablesuppress));
				}else{
					singleRadio.setEnableSuppress(false);
				}
			}else{
				colName = "enablesuppress";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radio_profile", colName);
				String enablesuppress = isColPresent ? xmlParser.getColVal(i,
						colName) : "false";
				singleRadio.setEnableSuppress(AhRestoreCommons
						.convertStringToBoolean(enablesuppress));
			}
			

			/**
			 * Set suppressthreshold
			 */
			colName = "suppressthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int suppressthreshold = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName))
					: RadioProfile.DEFAULT_SUPPRESS_THRESHOLD;
			singleRadio.setSuppressThreshold(suppressthreshold);

			/**
			 * Set enableWIPS
			 */
			colName = "enableWips";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			boolean enableWips = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			singleRadio.setEnableWips(enableWips);
					
			/**
			 * Set enabledPresence
			 */
			colName = "enabledPresence";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			boolean enabledPresence = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			singleRadio.setEnabledPresence(enabledPresence);
			
			/**
			 * Set enabledTxbeamforming
			 */
			colName = "enabledTxbeamforming";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			boolean enabledTxbeamforming = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			singleRadio.setEnabledTxbeamforming(enabledTxbeamforming);
			
			/**
			 * Set enableVHT
			 */
			colName = "enableVHT";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			boolean enableVHT = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			singleRadio.setEnableVHT(enableVHT);
			
			/**
			 * Set enableFrameburst
			 */
			colName = "enableFrameburst";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			boolean enableFrameburst = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			singleRadio.setEnableFrameburst(enableFrameburst);

			/**
			 * Set trapInterval
			 */
			colName = "trapInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int trapInterval = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : RadioProfile.DEFAULT_PRESENCE_TIME;
			singleRadio.setTrapInterval(trapInterval);

			/**
			 * Set agingTime
			 */
			colName = "agingTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int agingTime = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : RadioProfile.DEFAULT_PRESENCE_TIME;
			singleRadio.setAgingTime(agingTime);
			
			/**
			 * Set aggrInterval
			 */
			colName = "aggrInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile", colName);
			int aggrInterval = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : RadioProfile.DEFAULT_PRESENCE_TIME;
					singleRadio.setAggrInterval(aggrInterval);
					
			//Add the radar-detect-only remove upgrade log			
			if(RestoreHiveAp.restore_from_gotham_before && singleRadio.isEnableRadarDetect()){
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent("The ability to detect radar without changing channels was enabled in radio profile " + singleRadio.getRadioName());
				upgradeLog.setPostContent("Radar detection without changing channels is no longer allowed. The feature has been deprecated.");
				upgradeLog.setRecommendAction("Upload and activate a complete configuration to all devices using the radio profile.");
				upgradeLog.setOwner(ownerDomain);
				upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				lstLogBo.add(upgradeLog);
			}

			radio.add(singleRadio);
		}
		
		//Add the radar-detect-only remove upgrade log		
		if(RestoreHiveAp.restore_from_gotham_before){
			for(Long ownerId : AhRestoreNewMapTools.hmDomainMap.keySet()){
				HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);	
				if(HmDomain.GLOBAL_DOMAIN.equals(ownerDomain.getDomainName())){
					continue;
				}
				
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent("HiveOS releases prior to 6.1r5 allowed radar detection without changing channels.");
				upgradeLog.setPostContent("Radio profiles no longer support radar detection without changing channels. The feature has been deprecated.");
				upgradeLog.setRecommendAction("No action is required");
				upgradeLog.setOwner(ownerDomain);
				upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				lstLogBo.add(upgradeLog);			
			}			
		}		
		return radio.size() > 0 ? radio : null;
	}
	
	private static Map<String, Set<MacOrOui>> getAllIdsMacOrOui()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radio_profile_supress_bpr_oui.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radio_profile_supress_bpr_oui");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacOrOui>> macOrOuiInfo = new HashMap<String, Set<MacOrOui>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set RADIO_PROFILE_ID
			 */
			colName = "radio_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radio_profile_supress_bpr_oui", colName);
			if (!isColPresent) {
				/**
				 * The RADIO_PROFILE_ID column must be exist in the table of
				 * RADIO_PROFILE_SUPRESS_BPR_OUI
				 */
				continue;
			}

			String radioProfileId = xmlParser.getColVal(i, colName);
			if (radioProfileId == null || radioProfileId.trim().equals("")
					|| radioProfileId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set OUI_ID
			 */
			colName = "oui_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ids_policy_mac_or_oui", colName);
			if (!isColPresent) {
				/**
				 * The OUI_ID column must be exist in the table of
				 * RADIO_PROFILE_SUPRESS_BPR_OUI
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
					if (macOrOuiInfo.get(radioProfileId) == null) {
						Set<MacOrOui> macOrOuiSet = new HashSet<MacOrOui>();
						macOrOuiSet.add(macOrOui);
						macOrOuiInfo.put(radioProfileId, macOrOuiSet);
					} else {
						macOrOuiInfo.get(radioProfileId).add(macOrOui);
					}
				}
			}
		}
		return macOrOuiInfo;
	}



	/**
	 * Get all information from radio_profile_wmm_info table
	 *
	 * @return List<RadioProfileWmmInfo> all radio_profile_wmm_info
	 * @throws AhRestoreColNotExistException -
	 *             if radio_profile_wmm_info.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radio_profile_wmm_info.xml.
	 */
	private static List<RadioProfileWmmInfo> getAllRadioProfileWmmInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of alg_config_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radio_profile_wmm_info");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<RadioProfileWmmInfo> ruleInfo = new ArrayList<RadioProfileWmmInfo>();

		boolean isColPresent;
		String colName;
		RadioProfileWmmInfo singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadioProfileWmmInfo();

			/**
			 * Set radio_profile_id
			 */
			colName = "radio_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String mapkey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			for (AccessCategory acType : RadioProfileWmmInfo.AccessCategory.values()) {
				if (mapkey.equals(acType.name())) {
					singleInfo.setAcType(acType);
				}
			}

			if ("".equals(id) || null == singleInfo.getAcType()) {
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set noack
			 */
			colName = "noack";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String noack = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			singleInfo.setNoAck(AhRestoreCommons
				.convertStringToBoolean(noack));

			/**
			 * Set minimum
			 */
			colName = "minimum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String minimum = isColPresent ? xmlParser
				.getColVal(i, colName) : "0";
			singleInfo.setMinimum((short) AhRestoreCommons
				.convertInt(minimum));

			/**
			 * Set maximum
			 */
			colName = "maximum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String maximum = isColPresent ? xmlParser
				.getColVal(i, colName) : "0";
			singleInfo.setMaximum((short) AhRestoreCommons
				.convertInt(maximum));


			/**
			 * Set aifs
			 */
			colName = "aifs";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String aifs = isColPresent ? xmlParser
				.getColVal(i, colName) : "1";
			singleInfo.setAifs((short) AhRestoreCommons
				.convertInt(aifs));

			/**
			 * Set txoplimit
			 */
			colName = "txoplimit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radio_profile_wmm_info", colName);
			String txoplimit = isColPresent ? xmlParser.getColVal(i, colName)
				: "0";
			singleInfo.setTxoplimit(AhRestoreCommons.convertInt(txoplimit));

			ruleInfo.add(singleInfo);
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Restore radio_profile table
	 *
	 * @return true if table of radio_profile restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadioProfile()
	{
		try {
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<RadioProfile> allRadio = getAllRadioProfile(lstLogBo);

			// the default values have been inserted before restore
			if(null != allRadio) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadioProfile radio : allRadio) {
					lOldId.add(radio.getId());
				}

				QueryUtil.restoreBulkCreateBos(allRadio);

				for(int i=0; i<allRadio.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadioProfile(lOldId.get(i), allRadio.get(i).getId());
				}
			}

			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert radio profile upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/*Added from dakar_r6*/
	public static void updateHmSettingsRadarDetect(){
		boolean updateHMSetting = false;
		List<RadioProfile> radioProfiles = QueryUtil.executeQuery(RadioProfile.class, null, null);
		if(!radioProfiles.isEmpty()){
			for(RadioProfile rp:radioProfiles){
				if(rp.isEnableRadarDetect()){
					updateHMSetting = true;
					break;
				}
			}
		}
		
		if(updateHMSetting){
			List<HmUser> userList = QueryUtil.executeQuery(HmUser.class,
					null, null);
			if(!userList.isEmpty()){
				for(HmUser user :userList){
					if(user.isSuperUser()){
						HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
				                user.getDomain());
						if(null != bo && !bo.isEnableRadarDetection()){
							try {
								bo.setEnableRadarDetection(true);
								QueryUtil.updateBo(bo);
							} catch (Exception e) {
								AhRestoreDBTools.logRestoreMsg("In updateHmSettingsRadarDetect: update HM Settings error");
								AhRestoreDBTools.logRestoreMsg(e.getMessage());
							}
						}
					}
				}
			}
			
		}
	}

	/**
	 * Get all information from alg_config_info table
	 *
	 * @return List<AlgConfigurationInfo> all alg_config_info
	 * @throws AhRestoreColNotExistException -
	 *             if alg_config_info.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing alg_config_info.xml.
	 */
	private static List<AlgConfigurationInfo> getAllAlgConfigInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of alg_config_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("alg_config_info");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<AlgConfigurationInfo> ruleInfo = new ArrayList<AlgConfigurationInfo>();

		boolean isColPresent;
		String colName;
		AlgConfigurationInfo singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new AlgConfigurationInfo();

			/**
			 * Set alg_configuration_id
			 */
			colName = "alg_configuration_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String mapkey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType.values()) {
				if (mapkey.equals(gatewayType.name())) {
					singleInfo.setGatewayType(gatewayType);
				}
			}

			if ("".equals(id) || null == singleInfo.getGatewayType()) {
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set ifenable
			 */
			colName = "ifenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String ifenable = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			singleInfo.setIfEnable(AhRestoreCommons
				.convertStringToBoolean(ifenable));

			/**
			 * Set qosclass
			 */
			colName = "qosclass";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String qosclass = isColPresent ? xmlParser
				.getColVal(i, colName) : String
				.valueOf(EnumConstUtil.QOS_CLASS_EXCELLENT_EFFORT);
			singleInfo.setQosClass((short) AhRestoreCommons
				.convertInt(qosclass));

			/**
			 * Set timeout
			 */
			colName = "timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String timeout = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setTimeout(AhRestoreCommons.convertInt(timeout));

			/**
			 * Set duration
			 */
			colName = "duration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_config_info", colName);
			String duration = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setDuration(AhRestoreCommons.convertInt(duration));

			ruleInfo.add(singleInfo);
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from alg_configuration table
	 *
	 * @return List<AlgConfiguration> all AlgConfiguration BO
	 * @throws AhRestoreColNotExistException -
	 *             if alg_configuration.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing alg_configuration.xml.
	 */
	private static List<AlgConfiguration> getAllAlgConfiguration()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of alg_configuration.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("alg_configuration");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in alg_configuration table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AlgConfiguration> policy = new ArrayList<AlgConfiguration>();
		List<AlgConfiguration> policyNameEmpty = new ArrayList<AlgConfiguration>();

		boolean isColPresent;
		String colName;
		AlgConfiguration singlePolicy;
		List<AlgConfigurationInfo> allRule = getAllAlgConfigInfo();

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new AlgConfiguration();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_configuration", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));
			
			/**
			 * Set configname
			 */
			colName = "configname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_configuration", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			
			if (BeParaModule.DEFAULT_SERVICE_ALG_NAME.equals(name)) {
				// set default alg object new id to map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("configname", name);
				AlgConfiguration newAlg = HmBeParaUtil.getDefaultProfile(AlgConfiguration.class, map);
				if (null != newAlg) {
					AhRestoreNewMapTools.setMapAlgConfiguration(singlePolicy.getId(), newAlg.getId());
				}
				continue;
			}
			
			/**
			 * Set owner
			 */

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"alg_configuration", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'alg_configuration' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (name.trim().equals("")) {
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'alg_configuration' will reset the hive name, cause: 'configname' column value is null.");
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, singlePolicy.getOwner().getId());
				if (dm!=null) {
					name=dm.getDomainName();
					singlePolicy.setConfigName(name);
					policyNameEmpty.add(singlePolicy);
				} else {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'alg_configuration' will lost data, cause: 'configname' column value is null. domain ID:" + ownerId);
					continue;
				}
			}
			singlePolicy.setConfigName(name);

			/**
			 * Set defaultflag
			 */
			singlePolicy.setDefaultFlag(false);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"alg_configuration", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			if (null != allRule) {
				Map<String, AlgConfigurationInfo> items = new HashMap<String, AlgConfigurationInfo>();
				for (AlgConfigurationInfo ruleInfo : allRule) {
					if (id.equals(ruleInfo.getRestoreId())) {
						items.put(ruleInfo.getkey(), ruleInfo);
					}
				}
				if (items.get("DNS") == null) {
					AlgConfigurationInfo oneItem = new AlgConfigurationInfo();
					oneItem.setIfEnable(false);
					oneItem.setQosClass(EnumConstUtil.QOS_CLASS_BACKGROUND);
					oneItem.setTimeout(30);
					oneItem.setDuration(60);
					items.put("DNS", oneItem);
				}
				if (items.get("HTTP") == null) {
					AlgConfigurationInfo oneItem = new AlgConfigurationInfo();
					oneItem.setIfEnable(false);
					items.put("HTTP", oneItem);
				}
				singlePolicy.setItems(items);
			}
			policy.add(singlePolicy);
		}

		// fix bug 27502
		List<AlgConfiguration> policyNameExist = new ArrayList<AlgConfiguration>();
		if (!policyNameEmpty.isEmpty()) {
			for(AlgConfiguration hm: policyNameEmpty) {
				for(AlgConfiguration h: policy) {
					if (!hm.getId().equals(h.getId()) 
							&& hm.getConfigName().equals(h.getConfigName())
							&& hm.getOwner().getId().equals(h.getOwner().getId())) {
						policyNameExist.add(hm);
						break;
					}
				}
			}
		}
		if (!policyNameExist.isEmpty()) {
			int i=1;
			for(AlgConfiguration h: policyNameExist){
				boolean loopFlg=true;
				while (loopFlg) {
					boolean existFlg = false;
					String hName = h.getConfigName();
					if (hName.length()>28) {
						hName=hName.substring(0, 28) + "_" + i++;
					} else {
						hName=hName + "_" + i++;
					}
					for(AlgConfiguration hif: policy) {
						if (hif.getConfigName().equals(hName)
								&& hif.getOwner().getId().equals(h.getOwner().getId())) {
							existFlg = true;
							break;
						}
					}
					if (existFlg==false) {
						loopFlg=false;
						h.setConfigName(hName);
					}
				}
			}
		}
		for(AlgConfiguration h: policyNameExist){
			for(AlgConfiguration hif : policy){
				if(hif.getId().equals(h.getId())){
					hif.setConfigName(h.getConfigName());
				}
			}
		}
		
		//end fix bug 27502
				
		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore alg_configuration table
	 *
	 * @return true if table of alg_configuration restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreAlgConfiguration()
	{
		try {
			List<AlgConfiguration> allAlg = getAllAlgConfiguration();

			// the default value has been inserted before restore
			if (null != allAlg) {
				List<Long> lOldId = new ArrayList<Long>();

				for (AlgConfiguration alg : allAlg) {
					lOldId.add(alg.getId());
				}

				QueryUtil.restoreBulkCreateBos(allAlg);

				for(int i=0; i<allAlg.size(); i++)
				{
					AhRestoreNewMapTools.setMapAlgConfiguration(lOldId.get(i), allAlg.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from location_server table
	 *
	 * @return List<LocationServer> all LocationServer BO
	 * @throws AhRestoreColNotExistException -
	 *             if location_server.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing location_server.xml.
	 */
	private static List<LocationServer> getAllLocationServer()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of location_server.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("location_server");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in location_server table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LocationServer> policy = new ArrayList<LocationServer>();

		boolean isColPresent;
		String colName;
		LocationServer singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new LocationServer();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.trim().equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'location_server' data be lost, cause: 'name' column is not exist.");
				continue;
			}
			singlePolicy.setName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"location_server", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'location_server' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			singlePolicy.setOwner(owner);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set nameflag
			 */
			colName = "nameflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			if (isColPresent) {
				short nameflag = (short) AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
				if (IpAddress.TYPE_HOST_NAME == nameflag) {
					/**
					 * Set servername
					 */
					colName = "servername";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"location_server", colName);
					String servername = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
					if (!"".equals(servername)) {
						singlePolicy.setServerIP(CreateObjectAuto.createNewIP(servername, IpAddress.TYPE_HOST_NAME, owner, "For Location Server:"+name));
					}
				}
			}

			/**
			 * Set ipaddress_id
			 */
			colName = "ipaddress_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String oldIp = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(oldIp)) {
				Long ipId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(oldIp));
				if(null != ipId) {
					singlePolicy.setServerIP(AhRestoreNewTools.CreateBoWithId(IpAddress.class, ipId));
				}
			}

			/**
			 * Set enablerogue
			 */
			colName = "enablerogue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String enablerogue = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setEnableRogue(AhRestoreCommons.convertStringToBoolean(enablerogue));

			/**
			 * Set roguethreshold
			 */
			colName = "roguethreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String roguethreshold = isColPresent ? xmlParser.getColVal(i, colName) : "50";
			singlePolicy.setRogueThreshold(AhRestoreCommons.convertInt(roguethreshold));

			/**
			 * Set enableserver
			 */
			colName = "enableserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String enableserver = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setEnableServer(AhRestoreCommons.convertStringToBoolean(enableserver));

			/**
			 * Set enablestation
			 */
			colName = "enablestation";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String enablestation = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setEnableStation(AhRestoreCommons.convertStringToBoolean(enablestation));

			/**
			 * Set stationthreshold
			 */
			colName = "stationthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String stationthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "200";
			singlePolicy.setStationThreshold(AhRestoreCommons.convertInt(stationthreshold));

			/**
			 * Set enabletag
			 */
			colName = "enabletag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String enabletag = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setEnableTag(AhRestoreCommons.convertStringToBoolean(enabletag));

			/**
			 * Set tagthreshold
			 */
			colName = "tagthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String tagthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "1000";
			singlePolicy.setTagThreshold(AhRestoreCommons.convertInt(tagthreshold));

			/**
			 * Set ekahautagthreshold
			 */
			colName = "ekahautagthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String ekahautagthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "1000";
			singlePolicy.setEkahauTagThreshold(AhRestoreCommons.convertInt(ekahautagthreshold));


			/**
			 * Set serviceType
			 */
			colName = "serviceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String serviceType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocationServer.SERVICETYPE_AEROSCOUT);
			singlePolicy.setServiceType((byte)AhRestoreCommons.convertInt(serviceType));

			/**
			 * Set rssiChangeThreshold
			 */
			colName = "rssiChangeThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String rssiChangeThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			singlePolicy.setRssiChangeThreshold(AhRestoreCommons.convertInt(rssiChangeThreshold));

			/**
			 * Set locationReportInterval
			 */
			colName = "locationReportInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String locationReportInterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			singlePolicy.setLocationReportInterval(AhRestoreCommons.convertInt(locationReportInterval));

			/**
			 * Set rssiValidPeriod
			 */
			colName = "rssiValidPeriod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String rssiValidPeriod = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			singlePolicy.setRssiValidPeriod(AhRestoreCommons.convertInt(rssiValidPeriod));

			/**
			 * Set rssiHoldCount
			 */
			colName = "rssiHoldCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String rssiHoldCount = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singlePolicy.setRssiHoldCount(AhRestoreCommons.convertInt(rssiHoldCount));

			/**
			 * Set reportSuppressCount
			 */
			colName = "reportSuppressCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String reportSuppressCount = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			if (AhRestoreCommons.convertInt(reportSuppressCount)>80){
				singlePolicy.setReportSuppressCount(80);
			} else {
				singlePolicy.setReportSuppressCount(AhRestoreCommons.convertInt(reportSuppressCount));
			}
//			/**
//			 * Set ekahauServerType
//			 */
//			colName = "ekahauServerType";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"location_server", colName);
//			String ekahauServerType = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocationServer.EKAHAU_SERVERTYPE_IP);
//			singlePolicy.setEkahauServerType((byte)AhRestoreCommons.convertInt(ekahauServerType));
//
//			/**
//			 * Set ekahauIpAddress
//			 */
//			colName = "ekahauIpAddress";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"location_server", colName);
//			String ekahauIpAddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			singlePolicy.setEkahauIpAddress(ekahauIpAddress);
//
//			/**
//			 * Set ekahauDomain
//			 */
//			colName = "ekahauDomain";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"location_server", colName);
//			String ekahauDomain = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			singlePolicy.setEkahauDomain(ekahauDomain);
//
			/**
			 * Set ekahauPort
			 */
			colName = "ekahauPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String ekahauPort = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setEkahauPort(AhRestoreCommons.convertInt(ekahauPort));

			/**
			 * Set ekahauMac
			 */
			colName = "ekahauMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"location_server", colName);
			String ekahauMac = isColPresent ? xmlParser.getColVal(i, colName) : "01188E000000";
			singlePolicy.setEkahauMac(ekahauMac);

			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore location_server table
	 *
	 * @return true if table of location_server restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreLocationServer()
	{
		try {
			List<LocationServer> allServer = getAllLocationServer();
			if (null != allServer) {
				List<Long> lOldId = new ArrayList<Long>();

				for (LocationServer server : allServer) {
					lOldId.add(server.getId());
				}

				QueryUtil.restoreBulkCreateBos(allServer);

				for(int i=0; i<allServer.size(); i++)
				{
					AhRestoreNewMapTools.setMapLocationServer(lOldId.get(i), allServer.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from ethernet_access_mac table
	 *
	 * @return Map<String, Set<MacOrOui>> all EthernetMacOrOui
	 * @throws AhRestoreColNotExistException -
	 *             if ethernet_access_mac.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing ethernet_access_mac.xml.
	 */
	private static Map<String, Set<MacOrOui>> getAllEthernetMacInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ethernet_access_mac.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ethernet_access_mac");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacOrOui>> ruleInfo = new HashMap<String, Set<MacOrOui>>();

		boolean isColPresent;
		String colName;
		Set<MacOrOui> singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set ethernet_access_id
			 */
			colName = "ethernet_access_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access_mac", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}
			singleInfo = ruleInfo.get(id);

			/**
			 * Set mac_or_oui_id
			 */
			colName = "mac_or_oui_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access_mac", colName);
			String mac_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(mac_id)) {
				Long newMac = AhRestoreNewMapTools.getMapMacAddress(AhRestoreCommons.convertLong(mac_id));
				if(null != newMac) {
					MacOrOui sourceMac = AhRestoreNewTools.CreateBoWithId(MacOrOui.class, newMac);
					if(singleInfo == null) {
						singleInfo = new HashSet<MacOrOui>();
						singleInfo.add(sourceMac);
						ruleInfo.put(id, singleInfo);
					} else {
						singleInfo.add(sourceMac);
					}
				}
			}
		}

		return ruleInfo.size() > 0 ? ruleInfo : null;
	}

	/**
	 * Get all information from ethernet_access table
	 *
	 * @return List<EthernetAccess> all EthernetAccess BO
	 * @throws AhRestoreColNotExistException -
	 *             if ethernet_access.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing ethernet_access.xml.
	 */
	private static List<EthernetAccess> getAllEthernetAccess()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ethernet_access.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ethernet_access");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ethernet_access table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<EthernetAccess> policy = new ArrayList<EthernetAccess>();

		boolean isColPresent;
		String colName;
		EthernetAccess singlePolicy;
		Map<String, Set<MacOrOui>> allRule = null;
		// the main table must have records
		if (rowCount > 0) {
			allRule = getAllEthernetMacInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new EthernetAccess();

			/**
			 * Set ethernetname
			 */
			colName = "ethernetname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ethernet_access' data be lost, cause: 'ethernetname' column is not exist.");
				continue;
			}
			singlePolicy.setEthernetName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ethernet_access", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ethernet_access' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			singlePolicy.setOwner(owner);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			/**
			 * Set user_profile
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			UserProfile newUp = QueryUtil.findBoByAttribute(UserProfile.class, "defaultFlag", true);
			if (isColPresent) {
				Long upId = AhRestoreNewMapTools.getMapUserProfile(AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)));
				if(null != upId) {
					newUp = AhRestoreNewTools.CreateBoWithId(UserProfile.class, upId);
				}
			} else {
				/**
				 * Get attribute
				 */
				colName = "attribute";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ethernet_access", colName);
				if (isColPresent) {
					List<UserProfile> userProfile = QueryUtil.executeQuery(UserProfile.class,
						new SortParams("id"), new FilterParams("attributeValue", (short) AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))),
						owner.getId(), 1);
					if (!userProfile.isEmpty()) {
						newUp = userProfile.get(0);
					}
				}
			}
			singlePolicy.setUserProfile(newUp);

			/**
			 * Set maclearning
			 */
			colName = "maclearning";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String maclearning = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setMacLearning(AhRestoreCommons.convertStringToBoolean(maclearning));

			/**
			 * Set enableidle
			 */
			colName = "enableidle";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String enableidle = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singlePolicy.setEnableIdle(AhRestoreCommons.convertStringToBoolean(enableidle));

			/**
			 * Set idletimeout
			 */
			colName = "idletimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ethernet_access", colName);
			String idletimeout = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			singlePolicy.setIdleTimeout((short)AhRestoreCommons.convertInt(idletimeout));

			if (null != allRule)
			{
				singlePolicy.setMacAddress(allRule.get(id));
			}
			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore ethernet_access table
	 *
	 * @return true if table of ethernet_access restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreEthernetAccess()
	{
		try {
			List<EthernetAccess> allEthernet = getAllEthernetAccess();
			if (null != allEthernet) {
				for(EthernetAccess mybo :allEthernet) {
					AhRestoreNewMapTools.setMapEthernetAccessBo(mybo.getId(), mybo);
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from sla mapping table
	 *
	 * @return List<SlaMappingCustomize> all SlaMappingCustomize BO
	 * @throws AhRestoreColNotExistException -
	 *             if sla_mapping_customize.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing sla_mapping_customize.xml.
	 */
	private static List<SlaMappingCustomize> getAllSlaMappings()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of sla_mapping_customize.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("sla_mapping_customize");
		if (!restoreRet)
		{
			AhRestoreDBTools.logRestoreMsg("SAXReader cannot read sla_mapping_customize.xml file.");
			return null;
		}

		/**
		 * No one row data stored in sla_mapping_customize table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<SlaMappingCustomize> mappings = new ArrayList<SlaMappingCustomize>();

		boolean isColPresent;
		String colName;
		SlaMappingCustomize mapping;

		for (int i = 0; i < rowCount; i++)
		{
			mapping = new SlaMappingCustomize();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			if(!isColPresent){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'sla_mapping_customize' data be lost, cause: 'id' column is not exist.");
				continue;
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"sla_mapping_customize", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'sla_mapping_customize' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			mapping.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set itemorder
			 */
			colName = "itemorder";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			String itemorder = isColPresent ? xmlParser.getColVal(i, colName)
				: String.valueOf(SlaMappingCustomize.ITEM_ORDER_TOP);
			mapping.setItemOrder((short)AhRestoreCommons
				.convertInt(itemorder));


			/**
			 * Set level
			 */
			colName = "level";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			String level = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			mapping.setLevel((short)AhRestoreCommons.convertInt(level));

			/**
			 * Set phymode
			 */
			colName = "phymode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			if(!isColPresent){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'sla_mapping_customize' data be lost, cause: 'phymode' column is not exist.");
				continue;
			}
			String phymode = xmlParser.getColVal(i, colName);
			mapping.setPhymode(AhRestoreCommons.convertStringToEnum(ClientPhyMode.class, phymode, ClientPhyMode._11a));

			/**
			 * Set rate
			 */
			colName = "rate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			if(!isColPresent){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'sla_mapping_customize' data be lost, cause: 'rate' column is not exist.");
				continue;
			}
			String rate = xmlParser.getColVal(i, colName);
			mapping.setRate(AhRestoreCommons.convertString(rate));

			/**
			 * Set success
			 */
			colName = "success";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			String success = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			mapping.setSuccess(AhRestoreCommons.convertInt(success));

			/**
			 * Set usage
			 */
			colName = "usage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sla_mapping_customize", colName);
			String usage = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			mapping.setUsage(AhRestoreCommons.convertInt(usage));

			mappings.add(mapping);
		}
		AhRestoreDBTools.logRestoreMsg("SLA Customize Mapping count:"+mappings.size());
		return mappings.size() > 0 ? mappings : null;
	}

	/**
	 * Restore sla_mapping_customize table
	 *
	 * @return true if table of sla_mapping_customize restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreSlaMappings()
	{
		try {
			List<SlaMappingCustomize> allMappings = getAllSlaMappings();
			if (null != allMappings) {
				QueryUtil.restoreBulkCreateBos(allMappings);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Generate MAC OUI from MAC Address
	 * @param newMacId mac address id
	 * @return new MAC OUI
	 * @throws Exception -
	 */
	public static MacOrOui generateMacOuiObject(Long newMacId) throws Exception {
		if (null == newMacId)
			return null;
		Long newOui = AhRestoreNewMapTools.getMapMACAddressChangeToOUI(newMacId);
		MacOrOui macOui;
		if (null == newOui) {
			macOui = QueryUtil.findBoById(MacOrOui.class, newMacId, new QueryBo() {
				@Override
				public Collection<HmBo> load(HmBo bo) {
					if (bo instanceof MacOrOui) {
						MacOrOui macOrOui = (MacOrOui) bo;
						if (null != macOrOui.getItems())
							macOrOui.getItems().size();
					}
					return null;
				}
			});
			if (MacOrOui.TYPE_MAC_OUI == macOui.getTypeFlag()) {
				newOui = macOui.getId();
				AhRestoreNewMapTools.setMapMACAddressChangeToOUI(newMacId, newOui);
				return macOui;
			}
			macOui.setId(null);
			macOui.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			boolean bool = false;

			for (SingleTableItem info : macOui.getItems()) {
				info.setMacEntry(info.getMacEntry().substring(0, 6));
				items.add(info);
				if (!bool) {
					List<?> boIds = QueryUtil.executeQuery("select id from " + MacOrOui.class.getSimpleName(), null,
							new FilterParams("macOrOuiName", info.getMacEntry()), macOui.getOwner().getId());
					if (!boIds.isEmpty()) {
						newOui = (Long)boIds.get(0);
					} else {
						macOui.setMacOrOuiName(info.getMacEntry());
						macOui.setTypeFlag(MacOrOui.TYPE_MAC_OUI);
						bool = true;
					}
				}
			}
			macOui.setItems(items);
			if (bool) {
				newOui = QueryUtil.createBo(macOui);
			}
			AhRestoreNewMapTools.setMapMACAddressChangeToOUI(newMacId, newOui);
		}
		return AhRestoreNewTools.CreateBoWithId(MacOrOui.class, newOui);
	}

	/**
	 * Get all information from device_policy table
	 *
	 * @return List<DevicePolicy> all DevicePolicy BO
	 * @throws AhRestoreColNotExistException -
	 *             if device_policy.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing device_policy.xml.
	 */
//	private static List<DevicePolicy> getAllDevicePolicy() throws AhRestoreColNotExistException,AhRestoreException
//	{
//		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
//
//		/**
//		 * Check validation of device_policy.xml
//		 */
//		boolean restoreRet = xmlParser.readXMLFile("device_policy");
//		if (!restoreRet)
//		{
//			return null;
//		}
//
//		int rowCount = xmlParser.getRowCount();
//		if (rowCount <= 0) {
//			return null;
//		}
//
//		List<DevicePolicy> policyList = new ArrayList<DevicePolicy>();
//		boolean isColPresent;
//		String colName;
//		Map<String, List<DevicePolicyRule>> allRule = getAllDevicePolicyRule();
//		DevicePolicy policyObj;
//
//		for (int i = 0; i < rowCount; i++)
//		{
//			policyObj = new DevicePolicy();
//
//			/**
//			 * Set policyname
//			 */
//			colName = "policyname";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
//			if (name.equals("")) {
//				continue;
//			}
//			policyObj.setPolicyName(name);
//
//			/**
//			 * Set id
//			 */
//			colName = "id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
//			policyObj.setId(AhRestoreCommons.convertLong(id));
//
//			/**
//			 * Set description
//			 */
//			colName = "description";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			policyObj.setDescription(AhRestoreCommons.convertString(description));
//
//			/**
//			 * Set enabledomain
//			 */
//			colName = "enabledomain";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String enabledomain = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			policyObj.setEnableDomain(AhRestoreCommons.convertStringToBoolean(enabledomain));
//
//			/**
//			 * Set enableos
//			 */
//			colName = "enableos";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String enableos = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			policyObj.setEnableOs(AhRestoreCommons.convertStringToBoolean(enableos));
//
//			/**
//			 * Set enableoui
//			 */
//			colName = "enableoui";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String enableoui = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			policyObj.setEnableOui(AhRestoreCommons.convertStringToBoolean(enableoui));
//
//			/**
//			 * Set enablesinglecheck
//			 */
//			colName = "enablesinglecheck";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"device_policy", colName);
//			String enablesinglecheck = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			policyObj.setEnableSingleCheck(AhRestoreCommons.convertStringToBoolean(enablesinglecheck));
//
//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"device_policy", colName);
//			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
//			policyObj.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
//
//			/**
//			 * Set rule Info
//			 */
//			policyObj.setRules(allRule.get(AhRestoreCommons.convertString(id)));
//
//			policyList.add(policyObj);
//		}
//
//		return policyList.isEmpty() ? null : policyList;
//	}

	/**
	 * Restore device_policy table
	 *
	 * @return true if table of device_policy restoration is success, false
	 *         otherwise.
	 */
//	public static boolean restoreDeviceGroupPolicy()
//	{
//		try {
//			List<DevicePolicy> allPolicy = getAllDevicePolicy();
//
//			if(null != allPolicy) {
//				List<Long> lOldId = new ArrayList<Long>();
//
//				for (DevicePolicy policy : allPolicy) {
//					lOldId.add(policy.getId());
//				}
//
//				QueryUtil.bulkCreateBos(allPolicy);
//
//				for(int i=0; i<allPolicy.size(); i++)
//				{
//					AhRestoreNewMapTools.setMapDeviceGroupPolicy(lOldId.get(i), allPolicy.get(i).getId());
//				}
//			}
//		} catch(Exception e) {
//			AhRestoreDBTools.logRestoreMsg(e.getMessage());
//			return false;
//		}
//		return true;
//	}

	/**
	 * Get all information from firewall_policy_rule table
	 *
	 * @return List<FirewallPolicyRule> all FirewallPolicyRule
	 * @throws AhRestoreColNotExistException -
	 *             if firewall_policy_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing firewall_policy_rule.xml.
	 */
	private static Map<String, List<FirewallPolicyRule>> getAllFwPolicyRuleInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of firewall_policy_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("firewall_policy_rule");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in firewall_policy_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<FirewallPolicyRule>> allRules = new HashMap<String, List<FirewallPolicyRule>>();

		boolean isColPresent;
		String colName;
		FirewallPolicyRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new FirewallPolicyRule();

			/**
			 * Set firewall_policy_id
			 */
			colName = "firewall_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}

			/**
			 * Set filteraction
			 */
			colName = "filteraction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			short filteraction = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_ACTION_DENY;
			singleInfo.setFilterAction(filteraction);

			/**
			 * Set actionlog
			 */
			colName = "actionlog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			short actionlog = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : FirewallPolicyRule.POLICY_LOGGING_OFF;
			singleInfo.setActionLog(actionlog);

			/**
			 * Set desttype
			 */
			colName = "desttype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			short desttype = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
			singleInfo.setDestType(desttype);

			/**
			 * Set disablerule
			 */
			colName = "disablerule";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			boolean disablerule = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser
				.getColVal(i, colName));
			singleInfo.setDisableRule(disablerule);

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName)
				: "1";
			singleInfo.setRuleId((short) AhRestoreCommons.convertInt(ruleid));

			/**
			 * Set sourcetype
			 */
			colName = "sourcetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			short sourcetype = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
			singleInfo.setSourceType(sourcetype);

			/**
			 * Set source_ip_id
			 */
			colName = "source_ip_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String source_ip_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			if (!"".equals(source_ip_id)) {
				Long srIpId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(source_ip_id));
				if(null != srIpId) {
					singleInfo.setSourceIp(AhRestoreNewTools.CreateBoWithId(IpAddress.class, srIpId));
				}
			}

			/**
			 * Set source_network_id
			 */
			colName = "source_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String source_net_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			if (!"".equals(source_net_id)) {
				Long srNetId = AhRestoreNewMapTools.getMapVpnNetwork(AhRestoreCommons.convertLong(source_net_id));
				if(null != srNetId) {
					singleInfo.setSourceNtObj(AhRestoreNewTools.CreateBoWithId(VpnNetwork.class, srNetId));
				}
			}

			/**
			 * Set source_up_id
			 */
			colName = "source_up_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String source_up_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			if (!"".equals(source_up_id)) {
				Long srUpId = AhRestoreNewMapTools.getMapUserProfile(AhRestoreCommons.convertLong(source_up_id));
				if(null != srUpId) {
					singleInfo.setSourceUp(AhRestoreNewTools.CreateBoWithId(UserProfile.class, srUpId));
				}
			}

			/**
			 * Set destination_ip_id
			 */
			colName = "destination_ip_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String destination_ip_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(destination_ip_id)) {
				Long destIpId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(destination_ip_id));
				if(null != destIpId) {
					singleInfo.setDestinationIp(AhRestoreNewTools.CreateBoWithId(IpAddress.class, destIpId));
				}
			}

			/**
			 * Set destination_network_id
			 */
			colName = "destination_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String dest_net_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
				.getColVal(i, colName)) : "";
			if (!"".equals(dest_net_id)) {
				Long destNetId = AhRestoreNewMapTools.getMapVpnNetwork(AhRestoreCommons.convertLong(dest_net_id));
				if(null != destNetId) {
					singleInfo.setDestinationNtObj(AhRestoreNewTools.CreateBoWithId(VpnNetwork.class, destNetId));
				}
			}

			/**
			 * Set network_service_id
			 */
			colName = "network_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy_rule", colName);
			String network_service_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";
			if (!"".equals(network_service_id)) {
				Long serviceId = AhRestoreNewMapTools.getMapNetworkService(AhRestoreCommons.convertLong(network_service_id));
				if(null != serviceId) {
					singleInfo.setNetworkService(AhRestoreNewTools.CreateBoWithId(NetworkService.class, serviceId));
				}
			}
			List<FirewallPolicyRule> ruleInfo = allRules.get(id);
			if (null == ruleInfo) {
				ruleInfo = new ArrayList<FirewallPolicyRule>();
				ruleInfo.add(singleInfo);
				allRules.put(id, ruleInfo);
			} else {
				ruleInfo.add(singleInfo);
			}
		}
		return allRules.size() > 0 ? allRules : null;
	}

	/**
	 * Get all information from firewall_policy table
	 *
	 * @return List<FirewallPolicy> all FirewallPolicy BO
	 * @throws AhRestoreColNotExistException -
	 *             if firewall_policy.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing firewall_policy.xml.
	 */
	private static List<FirewallPolicy> getAllFirewallPolicy()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of firewall_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("firewall_policy");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in firewall_policy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<FirewallPolicy> policy = new ArrayList<FirewallPolicy>();
		Map<String, List<FirewallPolicyRule>> allRules = null;
		if (rowCount > 0) {
			allRules = getAllFwPolicyRuleInfo();
		}

		boolean isColPresent;
		String colName;
		FirewallPolicy singlePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new FirewallPolicy();

			/**
			 * Set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.trim().equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'firewall_policy' data be lost, cause: 'policyname' column is not exist.");
				continue;
			}
			singlePolicy.setPolicyName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"firewall_policy", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'firewall_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singlePolicy.setOwner(ownerDomain);

			/**
			 * Set defruleaction
			 */
			colName = "defruleaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy", colName);
			short defruleaction = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : IpPolicyRule.POLICY_ACTION_PERMIT;
			singlePolicy.setDefRuleAction(defruleaction);

			/**
			 * Set defrulelog
			 */
			colName = "defrulelog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy", colName);
			short defrulelog = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
				.getColVal(i, colName)) : FirewallPolicyRule.POLICY_LOGGING_OFF;
			singlePolicy.setDefRuleLog(defrulelog);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"firewall_policy", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			singlePolicy.setDescription(AhRestoreCommons
				.convertString(description));

			// policy rule
			if (null != allRules) {
				singlePolicy.setRules(allRules.get(id));
			}

			policy.add(singlePolicy);
		}

		return policy.size() > 0 ? policy : null;
	}

	/**
	 * Restore firewall_policy table
	 *
	 * @return true if table of firewall_policy restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreFirewallPolicy()
	{
		try {
			List<FirewallPolicy> allPolicy = getAllFirewallPolicy();
			if (null != allPolicy) {
				List<Long> lOldId = new ArrayList<Long>();

				for (FirewallPolicy policy : allPolicy) {
					lOldId.add(policy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allPolicy);

				for(int i=0; i<allPolicy.size(); i++)
				{
					AhRestoreNewMapTools.setMapFirewallPolicy(lOldId.get(i), allPolicy.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

}
