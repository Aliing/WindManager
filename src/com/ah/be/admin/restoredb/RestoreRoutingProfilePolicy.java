package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import com.ah.bo.hiveap.AhInterface;
import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingPolicyRule;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.RoutingProfilePolicyRule;

public class RestoreRoutingProfilePolicy {
	// keep the mapping from old(before upgrade) domain object ID to domain name list
	private static Map<Long, List<DomainNameItem>> newDomainObjectId2DomainNameItems = new HashMap<Long, List<DomainNameItem>>();
	
	private static Map<Long, String> upId2Name = new HashMap<>();
	
	public static boolean restore() {
		try {
			long start = System.currentTimeMillis();

			List<RoutingProfilePolicy> routingProfilePolicies = getAllRoutingProfilePolicy();

			if (null == routingProfilePolicies) {
				AhRestoreDBTools.logRestoreMsg("RestoreRoutingProfilePolicy is null");
			} else {
				Map<String, List<RoutingProfilePolicyRule>> routingPolicyPolicyRules = getAllRoutingProfilePolicyRules();

				List<Long> oldIdList = new ArrayList<Long>(routingProfilePolicies.size());
				for (RoutingProfilePolicy routingProfilePolicy : routingProfilePolicies) {
					if (routingProfilePolicy != null) {
						// set vpnGatewaySetting
						if (null != routingPolicyPolicyRules) {
							List<RoutingProfilePolicyRule> list = routingPolicyPolicyRules.get(routingProfilePolicy.getId().toString());
							if (null != list) {
								sortRoutingProfilePolicyRuleList(list);
								routingProfilePolicy.setRoutingProfilePolicyRuleList(list);
							}
						}

						oldIdList.add(routingProfilePolicy.getId());
						routingProfilePolicy.setId(null);
					}
				}
				QueryUtil.restoreBulkCreateBos(routingProfilePolicies);
				for (int i = 0; i < routingProfilePolicies.size(); i++) {
					AhRestoreNewMapTools.setMapRoutingPolicy(oldIdList.get(i), routingProfilePolicies.get(i).getId());
				}
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] completely. cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] error.", e);
			return false;
		}

		return true;
	}

	private static List<RoutingProfilePolicy> getAllRoutingProfilePolicy()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "routing_profile_policy";

		/**
		 * Check validation of routing_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in table is not allowed
		 */
		int rowCount = xmlParser.getRowCount();
		if (rowCount == 0)
			return null;

		List<RoutingProfilePolicy> routingProfilePolicies = new ArrayList<RoutingProfilePolicy>();
		boolean isColPresent;
		String colName;
		RoutingProfilePolicy routingProfilePolicy;

		for (int i = 0; i < rowCount; i++) {
			routingProfilePolicy = new RoutingProfilePolicy();

			/**
			 * Set policyName
			 */
			colName = "profileName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(name)
					|| name.trim().equalsIgnoreCase("null")) {
				AhRestoreDBTools.logRestoreMsg("Restore table 'routing_profile_policy' data be lost, cause: 'policyName' column value is null.");
				continue;
			}
			routingProfilePolicy.setProfileName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				AhRestoreDBTools.logRestoreMsg("Restore table 'routing_profile_policy' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			routingProfilePolicy.setId(Long.valueOf(id));

			/**
			 * Set profiletype
			 */
			routingProfilePolicy.setProfileType((short)retrieveColumn_Int(xmlParser, tableName, "profiletype", i, RoutingProfilePolicy.POLICYRULE_CUSTOM));
			
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				AhRestoreDBTools.logRestoreMsg("Restore table 'routing_profile_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			routingProfilePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			routingProfilePolicy.setDescription(AhRestoreCommons.convertString(description));

			routingProfilePolicies.add(routingProfilePolicy);
		}

		return routingProfilePolicies;
	}

	private static Map<String, List<RoutingProfilePolicyRule>> getAllRoutingProfilePolicyRules()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "routing_profile_policy_rule";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount == 0)
			return null;

		Map<String, List<RoutingProfilePolicyRule>> rules = new HashMap<String, List<RoutingProfilePolicyRule>>();

		boolean isColPresent;
		String colName;
		RoutingProfilePolicyRule rule;
		int SOURCE_DESTINATION_DEFAULT_TYPE = -1;
		int POSITION_DEFAULT_VALUE = -1;

		for (int i = 0; i < rowCount; i++) {
			rule = new RoutingProfilePolicyRule();
			/**
			 * ROUTING_POLICY_RULE_ID
			 */
			colName = "routing_profile_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				continue;
			}

			String policyId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(policyId) || policyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			int position = retrieveColumn_Int(xmlParser, tableName, "position", i, POSITION_DEFAULT_VALUE);
			if (position == POSITION_DEFAULT_VALUE) {
				continue;
			} else {
				rule.setPriority(position);
			}

			if (rules.containsKey(policyId)) {
				rules.get(policyId).add(rule);
			} else {
				List<RoutingProfilePolicyRule> ruleLst = new ArrayList<RoutingProfilePolicyRule>();
				ruleLst.add(rule);
				rules.put(policyId, ruleLst);
			}

//			rule.setSourcename(retrieveColumn_Text(xmlParser, tableName, "sourcename", i));
			rule.setSourcetype(retrieveColumn_Int(xmlParser, tableName, "sourcetype", i, SOURCE_DESTINATION_DEFAULT_TYPE));
			rule.setSourcevalue(retrieveColumn_Text(xmlParser, tableName, "sourcevalue", i));

//			rule.setDestinationname(retrieveColumn_Text(xmlParser, tableName, "destinationname", i));
			rule.setDestinationtype(retrieveColumn_Int(xmlParser, tableName, "destinationtype", i, SOURCE_DESTINATION_DEFAULT_TYPE));
			rule.setDestinationvalue(retrieveColumn_Text(xmlParser, tableName, "destinationvalue", i));

			rule.setOut1(retrieveColumn_Text(xmlParser, tableName, "out1", i));
			rule.setOut2(retrieveColumn_Text(xmlParser, tableName, "out2", i));
			rule.setOut3(retrieveColumn_Text(xmlParser, tableName, "out3", i));
			rule.setOut4(retrieveColumn_Text(xmlParser, tableName, "out4", i));
			
		}
		
		return rules;
	}
	
	private static String retrieveColumn_Text(AhRestoreGetXML xmlParser, String tableName, String columnName, int row) 
			throws AhRestoreException, AhRestoreColNotExistException {
		boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, columnName);
		return isColPresent ? xmlParser.getColVal(row, columnName) : "";
	}

	private static int retrieveColumn_Int(AhRestoreGetXML xmlParser, String tableName, String columnName, int row, int defaultValue) 
			throws AhRestoreException, AhRestoreColNotExistException {
		boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, columnName);
		return isColPresent ? Integer.valueOf(xmlParser.getColVal(row, columnName)) : defaultValue;
	}
	
	public static void convertFromRoutingPolicy(List<RoutingPolicy> routingPolicies) {
		List<RoutingProfilePolicy> routingProfilePolicies = new ArrayList<RoutingProfilePolicy>();
		
		try {
			for (RoutingPolicy routingPolicy : routingPolicies) {
				try {
					AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy]: " + routingPolicy.getPolicyName()
							+ " with " + (routingPolicy.getRoutingPolicyRuleList() == null ? 0 : routingPolicy.getRoutingPolicyRuleList().size()) + " rules");
					RoutingProfilePolicy profilePolicy = new RoutingProfilePolicy();
					profilePolicy.setProfileName(routingPolicy.getPolicyName());
					profilePolicy.setDescription(routingPolicy.getDescription());
					profilePolicy.setOwner(routingPolicy.getOwner());
					profilePolicy.setProfileType(routingPolicy.getPolicyRuleType());
					
					List<RoutingProfilePolicyRule> profilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
					profilePolicy.setRoutingProfilePolicyRuleList(profilePolicyRuleList);
					sortRoutingPolicyRule(routingPolicy.getRoutingPolicyRuleList());
					
					switch (routingPolicy.getPolicyRuleType()) {
					case RoutingPolicy.POLICYRULE_SPLIT:
						upgradePolicyRule_SplitTunnel(profilePolicy, routingPolicy);
						break;
					case RoutingPolicy.POLICYRULE_ALL:
						if (routingPolicy.getDomainObjectForDesList() == null)
							upgradePolicyRule_TunnelAll(profilePolicy, routingPolicy);
						else
							upgradePolicyRule_Custom(profilePolicy, routingPolicy);
						break;
					case RoutingPolicy.POLICYRULE_CUSTOM:
						upgradePolicyRule_Custom(profilePolicy, routingPolicy);
						break;
					}

                    if (profilePolicy.getRoutingProfilePolicyRuleList() != null) {
                        for (RoutingProfilePolicyRule profilePolicyRule: profilePolicy.getRoutingProfilePolicyRuleList()) {
                            if (profilePolicyRule.getSourcevalue() == null)
                                profilePolicyRule.setSourcevalue("");
                            if (profilePolicyRule.getDestinationvalue() == null)
                                profilePolicyRule.setDestinationvalue("");
                            if (profilePolicyRule.getOut2() == null)
                                profilePolicyRule.setOut2("");

                            profilePolicyRule.setOut3("");
                            profilePolicyRule.setOut4("");
                        }
                    }
					routingProfilePolicies.add(profilePolicy);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg(e.getMessage(), e);
				}
			}

			QueryUtil.restoreBulkCreateBos(routingProfilePolicies);
			for (int i = 0; i < routingPolicies.size(); i++) {
				AhRestoreNewMapTools.setMapRoutingPolicy(routingPolicies.get(i).getId(), routingProfilePolicies.get(i).getId());
			}

		} catch (Exception exc) {
			AhRestoreDBTools.logRestoreMsg(exc.getMessage(), exc);
		}
	}

	// restore from previous version
	public static List<RoutingProfilePolicy> restoreFromRoutingPolicy(List<RoutingPolicy> routingPolicies) {
		List<RoutingProfilePolicy> routingProfilePolicies = new ArrayList<RoutingProfilePolicy>();
		
		try {
			for (RoutingPolicy routingPolicy : routingPolicies) {
				try {
					AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy]: " + routingPolicy.getPolicyName()
							+ " with " + (routingPolicy.getRoutingPolicyRuleList() == null ? 0 : routingPolicy.getRoutingPolicyRuleList().size()) + " rules");
					RoutingProfilePolicy profilePolicy = new RoutingProfilePolicy();
					profilePolicy.setProfileName(routingPolicy.getPolicyName());
					profilePolicy.setDescription(routingPolicy.getDescription());
					profilePolicy.setOwner(routingPolicy.getOwner());
					profilePolicy.setProfileType(routingPolicy.getPolicyRuleType());
					
					List<RoutingProfilePolicyRule> profilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
					profilePolicy.setRoutingProfilePolicyRuleList(profilePolicyRuleList);
					sortRoutingPolicyRule(routingPolicy.getRoutingPolicyRuleList());
					
					switch (routingPolicy.getPolicyRuleType()) {
					case RoutingPolicy.POLICYRULE_SPLIT:
						upgradePolicyRule_SplitTunnel(profilePolicy, routingPolicy);
						break;
					case RoutingPolicy.POLICYRULE_ALL:
						if (routingPolicy.getDomainObjectForDesList() == null)
							upgradePolicyRule_TunnelAll(profilePolicy, routingPolicy);
						else
							upgradePolicyRule_Custom(profilePolicy, routingPolicy);
						break;
					case RoutingPolicy.POLICYRULE_CUSTOM:
						upgradePolicyRule_Custom(profilePolicy, routingPolicy);
						break;
					}

                    if (profilePolicy.getRoutingProfilePolicyRuleList() != null) {
                        for (RoutingProfilePolicyRule profilePolicyRule: profilePolicy.getRoutingProfilePolicyRuleList()) {
                            if (profilePolicyRule.getSourcevalue() == null)
                                profilePolicyRule.setSourcevalue("");
                            if (profilePolicyRule.getDestinationvalue() == null)
                                profilePolicyRule.setDestinationvalue("");
                            if (profilePolicyRule.getOut2() == null)
                                profilePolicyRule.setOut2("");

                            profilePolicyRule.setOut3("");
                            profilePolicyRule.setOut4("");
                        }
                    }
					routingProfilePolicies.add(profilePolicy);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg(e.getMessage(), e);
				}
			}

			QueryUtil.restoreBulkCreateBos(routingProfilePolicies);
			for (int i = 0; i < routingPolicies.size(); i++) {
				AhRestoreNewMapTools.setMapRoutingPolicy(routingPolicies.get(i).getId(), routingProfilePolicies.get(i).getId());
			}
			
		} catch (Exception exc) {
			AhRestoreDBTools.logRestoreMsg(exc.getMessage(), exc);
		}
		return routingProfilePolicies;
	}
	
	private static void upgradePolicyRule_SplitTunnel(RoutingProfilePolicy profilePolicy, RoutingPolicy routingPolicy) {
		List<RoutingProfilePolicyRule> profilePolicyRuleList = profilePolicy.getRoutingProfilePolicyRuleList();
		RoutingProfilePolicyRule rule = new RoutingProfilePolicyRule();
		
		// first rule: from Any Guest user profile, to Private, via Drop
		rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE);
//		rule.setSourcename(generateSourcename(null));
		rule.setSourcevalue(RoutingProfilePolicyRule.USER_PROFILES_ANY_GUEST);
		
		rule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
//		rule.setDestinationname(generateSourcename(null));
		
		rule.setOut1(ROUTING_PROFILE_VIA_DROP);
		rule.setPriority(1);
		profilePolicyRuleList.add(rule);
		
		// second rule: from Any, to Private, via Vpn 
		rule = new RoutingProfilePolicyRule();
		rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
//		rule.setSourcename(generateSourcename(null));
		
		rule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
//		rule.setDestinationname(generateSourcename(null));
		
		rule.setOut1(ROUTING_PROFILE_VIA_VPN);
		rule.setPriority(2);
		profilePolicyRuleList.add(rule);

		// third rule: from Any, to Any, via the interface in routingPolicy
		rule = new RoutingProfilePolicyRule();
		rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
//		rule.setSourcename(generateSourcename(null));
		
		rule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
//		rule.setDestinationname(generateSourcename(null));
		
		List<RoutingPolicyRule> routingPolicyRuleList = routingPolicy.getRoutingPolicyRuleList();
		RoutingPolicyRule routingPolicyRule = routingPolicyRuleList.get(routingPolicyRuleList.size() - 1);
		String outInterfacePri = routingPolicyRule.getInterfaceTypePri() == RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0
				? ROUTING_PROFILE_VIA_ETH0 : ROUTING_PROFILE_VIA_USB0;
		rule.setOut1(outInterfacePri);
		rule.setPriority(3);

		if (routingPolicy.getIpTrackForCheck() != null) {
			String outInterfaceSec = routingPolicyRule.getInterfaceTypeSec() == RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0
					? ROUTING_PROFILE_VIA_ETH0 : ROUTING_PROFILE_VIA_USB0;
			if (outInterfaceSec != outInterfacePri) {
				rule.setOut2(outInterfaceSec);
			}
		}
		profilePolicyRuleList.add(rule);
	}
	
	private static void upgradePolicyRule_TunnelAll(RoutingProfilePolicy profilePolicy, RoutingPolicy routingPolicy) {
		List<RoutingProfilePolicyRule> profilePolicyRuleList = profilePolicy.getRoutingProfilePolicyRuleList();
		RoutingProfilePolicyRule rule = new RoutingProfilePolicyRule();
		
		// first rule: from Any Guest user profile, to Private, via Drop
		rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE);
//		rule.setSourcename(generateSourcename(null));
		rule.setSourcevalue(RoutingProfilePolicyRule.USER_PROFILES_ANY_GUEST);
		
		rule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
//		rule.setDestinationname(generateSourcename(null));
		
		rule.setOut1(ROUTING_PROFILE_VIA_DROP);
		rule.setPriority(1);
		profilePolicyRuleList.add(rule);
		
		// second rule: from Any, to Private, via Vpn 
		rule = new RoutingProfilePolicyRule();
		rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
//		rule.setSourcename(generateSourcename(null));
		
		rule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
//		rule.setDestinationname(generateSourcename(null));
		
		rule.setOut1(ROUTING_PROFILE_VIA_VPN);
		rule.setPriority(2);
		profilePolicyRuleList.add(rule);
	}
	
	private static void upgradePolicyRule_Custom(RoutingProfilePolicy profilePolicy, RoutingPolicy routingPolicy) {
		List<RoutingProfilePolicyRule> profilePolicyRuleList = profilePolicy.getRoutingProfilePolicyRuleList();
		for (RoutingPolicyRule rule : routingPolicy.getRoutingPolicyRuleList()) {
			List<RoutingProfilePolicyRule> profilePolicyRules = restoreProfilePolicyRule(routingPolicy.getOwner().getId(),
					routingPolicy.getDomainObjectForDesList() == null ? null : routingPolicy.getDomainObjectForDesList().getId(),
					rule, routingPolicy.getIpTrackForCheck() != null);
			if (profilePolicyRules != null) {
				profilePolicyRuleList.addAll(profilePolicyRules);
			}
		}
		
		int priority = 1;
		for (RoutingProfilePolicyRule profilePolicyRule : profilePolicyRuleList) {
			profilePolicyRule.setPriority(priority ++);
		}
	}

	private static final short ROUTING_POLICY_RULE_TYPE_ANY_GUEST = -2;
	private static final short ROUTING_POLICY_RULE_TYPE_ANY       = -1;
	
	private static final String USER_PROFILE_NAME_PLACEHOLD = "";	// used only in user profile == Any
	private static List<RoutingProfilePolicyRule> restoreProfilePolicyRule(Long owner, Long domainObjectId,
			RoutingPolicyRule routingPolicyRule, boolean hasBackupInterface) {
		List<RoutingProfilePolicyRule> profilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
		
		if (routingPolicyRule.getRuleType() == ROUTING_POLICY_RULE_TYPE_ANY_GUEST) {
			List<RoutingProfilePolicyRule> rules = restoreFromRoutingPolicyRule_UserProfile(
					RoutingProfilePolicyRule.USER_PROFILES_ANY_GUEST, routingPolicyRule, domainObjectId, false);
			if (rules != null) {
				profilePolicyRuleList.addAll(rules);
			}
		} else if (routingPolicyRule.getRuleType() == ROUTING_POLICY_RULE_TYPE_ANY) {
			List<RoutingProfilePolicyRule> rules = restoreFromRoutingPolicyRule_UserProfile(
					USER_PROFILE_NAME_PLACEHOLD, routingPolicyRule, domainObjectId, hasBackupInterface);
			if (rules != null) {
				for (RoutingProfilePolicyRule rule : rules) {
					rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
					rule.setSourcevalue(null);
					profilePolicyRuleList.add(rule);
				}
			}
		} else {
			if (routingPolicyRule.getSourceUserProfile() == null) {
				AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] meet null user profile in a RoutingPolicyRule, ignore it");
				return profilePolicyRuleList;
			}

			String userProfileName = upId2Name.get(routingPolicyRule.getSourceUserProfile().getId());
			List<RoutingProfilePolicyRule> rules = restoreFromRoutingPolicyRule_UserProfile(
					userProfileName, routingPolicyRule, domainObjectId, hasBackupInterface);
			if (rules != null) {
				profilePolicyRuleList.addAll(rules);
			}
		}

		return profilePolicyRuleList;
	}
	
	private static List<RoutingProfilePolicyRule> restoreFromRoutingPolicyRule_UserProfile(String userProfileName,
			RoutingPolicyRule routingPolicyRule, Long domainObjectId, boolean hasBackupInterface) {
		
		if (userProfileName == null) {
			AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] rule error: cannot find user profile name with previous ID: "
					+ routingPolicyRule.getSourceUserProfile().getId());
			return null;
		}
		
		List<RoutingProfilePolicyRule> primaryList = null;
		String outInterfacePri = routingPolicyRule.getInterfaceTypePri() == RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0
				? ROUTING_PROFILE_VIA_ETH0 : ROUTING_PROFILE_VIA_USB0;
		String outInterfaceSec = routingPolicyRule.getInterfaceTypeSec() == RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0
				? ROUTING_PROFILE_VIA_ETH0 : ROUTING_PROFILE_VIA_USB0;
		
		primaryList = restoreFromRoutingPolicyRule_UserProfile(routingPolicyRule.getForwardActionTypePri(), outInterfacePri, domainObjectId);
		if (hasBackupInterface) {
			List<RoutingProfilePolicyRule> backupList = restoreFromRoutingPolicyRule_UserProfile(
						routingPolicyRule.getForwardActionTypeSec(), outInterfaceSec, domainObjectId);
			mergePrimaryBackupRules(routingPolicyRule.getForwardActionTypePri(), primaryList,
			        routingPolicyRule.getForwardActionTypeSec(), backupList);
		}

		if (primaryList != null) {
			for (RoutingProfilePolicyRule rule : primaryList) {
//				rule.setSourcename(generateSourcename(routingPolicyRule));
				rule.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE);
				rule.setSourcevalue(userProfileName);
//				rule.setDestinationname(generateDestinationname(routingPolicyRule));
			}
		}
		
		return primaryList;
	}
	
	private static List<RoutingProfilePolicyRule> restoreFromRoutingPolicyRule_UserProfile(short action,
			String outInterface, Long domainObjectId) {
		List<RoutingProfilePolicyRule> list = null;
		
		switch (action) {
			case RoutingPolicyRule.FORWARDACTION_NOTUNNEL: {
                list = new ArrayList<RoutingProfilePolicyRule>();
                RoutingProfilePolicyRule rule = translateAction_ForwardNoTunnel(outInterface);
                list.add(rule);
				return list;
            }
			case RoutingPolicyRule.FORWARDACTION_ALL: {
				list = new ArrayList<RoutingProfilePolicyRule>();
				RoutingProfilePolicyRule rule = translateAction_TunnelAll(outInterface);
				list.add(rule);
				break;
			}
			case RoutingPolicyRule.FORWARDACTION_DROP: {
				list = new ArrayList<RoutingProfilePolicyRule>();
				RoutingProfilePolicyRule rule = translateAction_Drop(outInterface);
				list.add(rule);
				break;
			}
			case RoutingPolicyRule.FORWARDACTION_SPLIT: {
				list = translateAction_SplitTunnel(outInterface);
				break;
			}
			case RoutingPolicyRule.FORWARDACTION_EXCEPTION: {
				list = translateAction_TunnelWithException(outInterface, domainObjectId);
				break;
			}
		}
		
		return list;
	}
	
	private static final String ROUTING_PROFILE_VIA_DROP = RoutingProfilePolicyRule.DEVICE_TYPE_DROP_VALUE;
	private static final String ROUTING_PROFILE_VIA_VPN = RoutingProfilePolicyRule.DEVICE_TYPE_CORPORATE_NETWORK_VPN_VALUE;
	private static final String ROUTING_PROFILE_VIA_ETH0 = String.valueOf(AhInterface.DEVICE_IF_TYPE_ETH0);
	private static final String ROUTING_PROFILE_VIA_USB0 = String.valueOf(AhInterface.DEVICE_IF_TYPE_USB);

    private static RoutingProfilePolicyRule translateAction_ForwardNoTunnel(String outInterface) {
        RoutingProfilePolicyRule profilePolicyRule = new RoutingProfilePolicyRule();
        profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
        profilePolicyRule.setOut1(outInterface);

        return profilePolicyRule;
    }
	
	private static RoutingProfilePolicyRule translateAction_TunnelAll(String outInterface) {
		RoutingProfilePolicyRule profilePolicyRule = new RoutingProfilePolicyRule();
		profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
		profilePolicyRule.setOut1(ROUTING_PROFILE_VIA_VPN);
		
		return profilePolicyRule;
	}

	private static RoutingProfilePolicyRule translateAction_Drop(String outInterface) {
		RoutingProfilePolicyRule profilePolicyRule = new RoutingProfilePolicyRule();
		profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
		profilePolicyRule.setOut1(ROUTING_PROFILE_VIA_DROP);
		
		return profilePolicyRule;
	}
	
	private static List<RoutingProfilePolicyRule> translateAction_SplitTunnel(String outInterface) {
		RoutingProfilePolicyRule profilePolicyRule1 = new RoutingProfilePolicyRule();
		profilePolicyRule1.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
		profilePolicyRule1.setOut1(ROUTING_PROFILE_VIA_VPN);
		
		RoutingProfilePolicyRule profilePolicyRule2 = new RoutingProfilePolicyRule();
		profilePolicyRule2.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
		profilePolicyRule2.setOut1(outInterface);
		
		List<RoutingProfilePolicyRule> profilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
		profilePolicyRuleList.add(profilePolicyRule1);
		profilePolicyRuleList.add(profilePolicyRule2);
		
		return profilePolicyRuleList;
	}
	
	private static List<RoutingProfilePolicyRule> translateAction_TunnelWithException(String outInterface, Long domainObjectId) {
		List<RoutingProfilePolicyRule> profilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
		if (domainObjectId != null) {
			List<DomainNameItem> list = newDomainObjectId2DomainNameItems.get(domainObjectId);
			if (list != null) {
				for (DomainNameItem item : list) {
					RoutingProfilePolicyRule profilePolicyRule = new RoutingProfilePolicyRule();
//					profilePolicyRule.setDestinationname(generateDestinationname(null));
					profilePolicyRule.setOut1(outInterface);
					
					if (isIP(item.getDomainName())) {
						profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE);
						profilePolicyRule.setDestinationvalue(item.getDomainName()+"~"+item.getDomainName());
					} else {
						profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME);
						profilePolicyRule.setDestinationvalue(item.getDomainName());
					}
					
					profilePolicyRuleList.add(profilePolicyRule);
				}
			}
		}
		
		RoutingProfilePolicyRule profilePolicyRule = new RoutingProfilePolicyRule();
		profilePolicyRule.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
		profilePolicyRule.setOut1(ROUTING_PROFILE_VIA_VPN);
		
		profilePolicyRuleList.add(profilePolicyRule);

		return profilePolicyRuleList;
	}
	
	private static void mergePrimaryBackupRules(short primaryAction, List<RoutingProfilePolicyRule> primaryRuleList, 
	        short backupAction, List<RoutingProfilePolicyRule> backupRuleList) {
	    switch (primaryAction) {
	    case RoutingPolicyRule.FORWARDACTION_ALL:
	        if (backupAction == RoutingPolicyRule.FORWARDACTION_NOTUNNEL) {
	            primaryRuleList.get(0).setOut2(backupRuleList.get(0).getOut1());
	        } else if (backupAction == RoutingPolicyRule.FORWARDACTION_SPLIT) {
	            // rule 0, to: private, out1: vpn
	            // rule 1, to: any,     out1: vpn, out2: backupRuleList[1].out1
                primaryRuleList.add(0, backupRuleList.get(0));
	            primaryRuleList.get(1).setOut2(backupRuleList.get(1).getOut1());
	        } else if (backupAction == RoutingPolicyRule.FORWARDACTION_EXCEPTION) {
	            for (int i=0; i<backupRuleList.size()-1; i++) {
	                backupRuleList.get(i).setOut2(backupRuleList.get(i).getOut1());
	                backupRuleList.get(i).setOut1(ROUTING_PROFILE_VIA_VPN);
	            }
	            primaryRuleList.clear();
	            primaryRuleList.addAll(backupRuleList);
	        }
	        break;
	    case RoutingPolicyRule.FORWARDACTION_NOTUNNEL:
            if (backupAction == RoutingPolicyRule.FORWARDACTION_NOTUNNEL) {
                primaryRuleList.get(0).setOut2(backupRuleList.get(0).getOut1());
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_ALL) {
                primaryRuleList.get(0).setOut2(ROUTING_PROFILE_VIA_VPN);
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_SPLIT) {
                // rule 0, to: private, out1: primaryRuleList[0].out1, out2: vpn
                // rule 1, to: any,     out1: primaryRuleList[0].out1, out2: backupRuleList[1].out1
                primaryRuleList.add(0, backupRuleList.get(0));
                primaryRuleList.get(0).setOut1(primaryRuleList.get(1).getOut1());
                primaryRuleList.get(0).setOut2(ROUTING_PROFILE_VIA_VPN);
                primaryRuleList.get(1).setOut2(backupRuleList.get(1).getOut1());
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_EXCEPTION) {
                for (int i=0; i<backupRuleList.size()-1; i++) {
                    backupRuleList.get(i).setOut2(backupRuleList.get(i).getOut1());
                    backupRuleList.get(i).setOut1(primaryRuleList.get(0).getOut1());
                }
                backupRuleList.get(backupRuleList.size()-1).setOut2(ROUTING_PROFILE_VIA_VPN);
                backupRuleList.get(backupRuleList.size()-1).setOut1(primaryRuleList.get(0).getOut1());
                
                primaryRuleList.clear();
                primaryRuleList.addAll(backupRuleList);
            }
            break;
	    case RoutingPolicyRule.FORWARDACTION_SPLIT: 
            if (backupAction == RoutingPolicyRule.FORWARDACTION_NOTUNNEL) {
                primaryRuleList.get(0).setOut2(backupRuleList.get(0).getOut1());
                primaryRuleList.get(1).setOut2(backupRuleList.get(0).getOut1());
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_ALL) {
                primaryRuleList.get(1).setOut2(ROUTING_PROFILE_VIA_VPN);
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_SPLIT) {
                primaryRuleList.get(1).setOut2(backupRuleList.get(1).getOut1());
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_EXCEPTION) {
                String out1OfPrimaryRule1 = primaryRuleList.get(1).getOut1();
                for (int i=0; i<backupRuleList.size()-1; i++) {
                    backupRuleList.get(i).setOut2(backupRuleList.get(i).getOut1());
                    backupRuleList.get(i).setOut1(out1OfPrimaryRule1);
                    primaryRuleList.add(1+i, backupRuleList.get(i));
                }
                
                primaryRuleList.get(primaryRuleList.size()-1).setOut2(ROUTING_PROFILE_VIA_VPN);
            }
            break;
	    case RoutingPolicyRule.FORWARDACTION_EXCEPTION:
	        int primaryRuleSize = primaryRuleList.size();
            if (backupAction == RoutingPolicyRule.FORWARDACTION_NOTUNNEL
                    || backupAction == RoutingPolicyRule.FORWARDACTION_ALL) {
                for (int i=0; i<primaryRuleSize; i++) {
                    primaryRuleList.get(i).setOut2(backupRuleList.get(0).getOut1());
                }
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_SPLIT) {
                for (int i=0; i<primaryRuleSize-1; i++) {
                    primaryRuleList.get(i).setOut2(backupRuleList.get(1).getOut1());
                }
                primaryRuleList.add(primaryRuleSize-1, backupRuleList.get(0));  // to: private, out1: vpn
                primaryRuleList.get(primaryRuleSize).setOut2(backupRuleList.get(1).getOut1());
            } else if (backupAction == RoutingPolicyRule.FORWARDACTION_EXCEPTION) {
                if (backupRuleList.size() > 1) {
                    for (int i=0; i<primaryRuleSize-1; i++) {
                        primaryRuleList.get(i).setOut2(backupRuleList.get(0).getOut1());
                    }
                }
            }
            break;
	    }
	    
	    for (int i=0; i<primaryRuleList.size(); i++) {
	        if (primaryRuleList.get(i).getOut1().equals(primaryRuleList.get(i).getOut2()))
	            primaryRuleList.get(i).setOut2(null);
	    }
	}
	
//	private static String generateSourcename(RoutingPolicyRule routingPolicyRule) {
//		return UUID.randomUUID().toString().replaceAll("-", "");
//	}
//	
//	private static String generateDestinationname(RoutingPolicyRule routingPolicyRule) {
//		return UUID.randomUUID().toString();
//	}
	
	// check text is a domain name or IP
	private static boolean isIP(String text) {
		StringTokenizer st = new StringTokenizer(text, ".");
		if (st.countTokens() != 4)
			return false;
		
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			try {
				int value = Integer.parseInt(token);
				if (value > 255 || value < 0)
					return false;
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}
	
	private static void sortRoutingProfilePolicyRuleList(List<RoutingProfilePolicyRule> list) {
		Collections.sort(list, new Comparator<RoutingProfilePolicyRule>() {
			public int compare(RoutingProfilePolicyRule obj1,
					RoutingProfilePolicyRule obj2) {
				return obj1.getPriority() - obj2.getPriority();
			}
		});
	}
	
	private static List<RoutingPolicyRule> sortRoutingPolicyRule(List<RoutingPolicyRule> list) {
		Collections.sort(list, new Comparator<RoutingPolicyRule>() {
			@Override
			public int compare(RoutingPolicyRule r1, RoutingPolicyRule r2) {
				if (r1.getRuleType() > 0 && r2.getRuleType() > 0)	// both are not ANY or ANY Guest
					return r1.getPosition() - r2.getPosition();
				else if (r1.getRuleType() < 0 && r2.getRuleType() < 0)
					return r1.getRuleType() - r2.getRuleType();		// one is ANY Guest (-2), another is ANY (-1), ANY Guest should be ahead
				else												// one is ANY Guest or ANY, another is custom rule, custom rule should be ahead
					return r1.getRuleType() > 0 ? -1 : 1;
			}
		});
		
		return list;
	}
	
	public static void saveDomainNameItems(Long domainObjectId, List<DomainNameItem> list) {
		AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] Domain object ID: " + domainObjectId);
		if (list != null) {
			for (DomainNameItem item : list) {
				AhRestoreDBTools.logRestoreMsg("[RestoreRoutingProfilePolicy] DomainNameItem, name: " + item.getDomainName());
			}
		}
		newDomainObjectId2DomainNameItems.put(domainObjectId, list);
	}
	
	public static void saveUserProfileName(Long id, String name) {
		upId2Name.put(id, name);
	}
	
	private static class UserProfileInfo {
		public String name;
		public long vpnNetworkId;
		
		public UserProfileInfo(String name, Long vpnNetworkId) {
			this.name = name;
			this.vpnNetworkId = vpnNetworkId;
		}
	}
	
}
