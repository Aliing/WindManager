package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingPolicyRule;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.config.RoutingPolicyAction;
import com.ah.ui.actions.config.UserProfilesAction;
import com.ah.ui.actions.config.VpnServiceAction;

public class RestoreRoutingPolicy {

	public static final short POLICYRULE_SPLIT = 1;
	public static final short POLICYRULE_ALL = 2;
	public static final short POLICYRULE_CUSTOM = 3;
	
	public static boolean restoreRoutingPolicies() {
		try {
			long start = System.currentTimeMillis();

			List<RoutingPolicy> routingPolicies = getAllRoutingPolicies();

			if (null == routingPolicies) {
				AhRestoreDBTools.logRestoreMsg("routing policy is null");
			} else {
				Map<String, List<RoutingPolicyRule>> routingPolicyRules = getAllRoutingPolicyRules();

				List<Long> oldIdList = new ArrayList<Long>(routingPolicies.size());
				for (RoutingPolicy routingPolicy : routingPolicies) {
					if (routingPolicy != null) {
						// set vpnGatewaySetting
						if(null != routingPolicyRules){
							List<RoutingPolicyRule> list = routingPolicyRules.get(routingPolicy.getId().toString());
							if(null != list){
						           Collections.sort(list,
					               new Comparator<RoutingPolicyRule>() {
					                  public int compare(RoutingPolicyRule obj1, RoutingPolicyRule obj2) {
					                         return obj1.getPosition() - obj2.getPosition();
					                  }
					               });
						           routingPolicy.setRoutingPolicyRuleList(list);
							}
						}

						oldIdList.add(routingPolicy.getId());
						//routingPolicy.setId(null);
					}
				}
				/**
				 * use the routing policy list to generate new routing profile policy instances
				QueryUtil.restoreBulkCreateBos(routingPolicies);
				for (int i = 0; i < routingPolicies.size(); i++) {
					AhRestoreNewMapTools.setMapRoutingPolicy(oldIdList.get(i),
							routingPolicies.get(i).getId());
				}
				*/
				RestoreRoutingProfilePolicy.restoreFromRoutingPolicy(routingPolicies);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore routing policy completely. cost:"
							+ (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore routing policy error.", e);
			return false;
		}
		return true;
	}
	
	private static boolean isNotNullString(String str) {
		return !StringUtils.isBlank(str)
					&& !str.trim().toLowerCase().equals("null");
	}

	private static Map<String, List<RoutingPolicyRule>> getAllRoutingPolicyRules() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "routing_policy_rule";
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount == 0) return null;

		Map<String, List<RoutingPolicyRule>> rules = new HashMap<String, List<RoutingPolicyRule>>();

		boolean isColPresent;
		String colName;
		RoutingPolicyRule rule;

		for (int i = 0; i < rowCount; i++) {
			rule = new RoutingPolicyRule();
			/**
			 * ROUTING_POLICY_RULE_ID
			 */
			colName = "routing_policy_rule_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				continue;
			}

			String policyId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(policyId)
					|| policyId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (rules.containsKey(policyId)) {
				rules.get(policyId).add(rule);
			} else {
				List<RoutingPolicyRule> ruleLst = new ArrayList<RoutingPolicyRule>();
				ruleLst.add(rule);
				rules.put(policyId, ruleLst);
			}

			/**
			 * USERPROFILEID
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String upId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (isNotNullString(upId)) {
				Long upIdNew = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons.convertLong(upId));
				if (null != upIdNew) {
					rule.setSourceUserProfile(AhRestoreNewTools.CreateBoWithId(
							UserProfile.class, upIdNew));
				}
			} else {
				rule.setSourceUserProfile(null);
			}


			/**
			 * IP_TRACK_PRI_ID
			 */
			colName = "ip_track_pri_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ipTrackId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (isNotNullString(ipTrackId)) {
				Long ipTrackIdNew = AhRestoreNewMapTools
						.getMapMgmtIpTracking(AhRestoreCommons.convertLong(ipTrackId));
				if (null != ipTrackIdNew) {
					rule.setIpTrackReachablePri(AhRestoreNewTools.CreateBoWithId(
							MgmtServiceIPTrack.class, ipTrackIdNew));
				}
			}


			/**
			 * IP_TRACK_SEC_ID
			 */
			colName = "ip_track_sec_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ipTrackSecId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (isNotNullString(ipTrackSecId)) {
				Long ipTrackSecIdNew = AhRestoreNewMapTools
						.getMapMgmtIpTracking(AhRestoreCommons.convertLong(ipTrackSecId));
				if (null != ipTrackSecIdNew) {
					rule.setIpTrackReachableSec(AhRestoreNewTools.CreateBoWithId(
							MgmtServiceIPTrack.class, ipTrackSecIdNew));
				}
			}


			/**
			 * interfaceTypePri
			 */
			colName = "interfaceTypePri";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short interfaceTypePri = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicyRule.ROUTING_POLICY_RULE_NONE;
			rule.setInterfaceTypePri(interfaceTypePri);


			/**
			 * interfaceTypeSec
			 */
			colName = "interfaceTypeSec";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short interfaceTypeSec = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicyRule.ROUTING_POLICY_RULE_NONE;
			rule.setInterfaceTypeSec(interfaceTypeSec);


			/**
			 * forwardActionTypePri
			 */
			colName = "forwardActionTypePri";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short forwardActionTypePri = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicyRule.FORWARDACTION_DROP;
			rule.setForwardActionTypePri(forwardActionTypePri);


			/**
			 * forwardActionTypeSec
			 */
			colName = "forwardActionTypeSec";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short forwardActionTypeSec = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicyRule.FORWARDACTION_DROP;
			rule.setForwardActionTypeSec(forwardActionTypeSec);

			/**
			 *  policyRuleType
			 */
			colName = "ruleType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short ruleType = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicyRule.ROUTING_POLICY_RULE_USERPROFILE;
			rule.setRuleType(ruleType);

			/**
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int position = isColPresent ? Integer.valueOf(xmlParser.getColVal(i,
					colName)) : 0;
			rule.setPosition(position);
		}
		return rules;
	}

	private static List<RoutingPolicy> getAllRoutingPolicies() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "routing_policy";

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
		if (rowCount == 0) return null;
		List<RoutingPolicy> routingPolicies = new ArrayList<RoutingPolicy>();
		boolean isColPresent;
		String colName;
		RoutingPolicy routingPolicy;

		for (int i = 0; i < rowCount; i++) {
			routingPolicy = new RoutingPolicy();

			/**
			 * Set policyName
			 */
			colName = "policyName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(name)
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'routing_policy' data be lost, cause: 'policyName' column value is null.");
				continue;
			}
			routingPolicy.setPolicyName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'routing_policy' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			routingPolicy.setId(Long.valueOf(id));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			routingPolicy.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set policyRuleType
			 */
			colName = "policyRuleType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			short policyRuleType = isColPresent ? Short.valueOf(xmlParser.getColVal(i,
					colName)) : RoutingPolicy.POLICYRULE_CUSTOM;
			routingPolicy.setPolicyRuleType(policyRuleType);

			/**
			 * Set enableIpTrackForCheck
			 */
			colName = "enableIpTrackForCheck";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String enableIpTrackForCheck = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			routingPolicy.setEnableIpTrackForCheck(AhRestoreCommons.convertStringToBoolean(enableIpTrackForCheck));

			/**
			 * Set enableDomainObjectForDesList
			 */
			colName = "enableDomainObjectForDesList";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String enableDomainObjectForDesList = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			routingPolicy.setEnableDomainObjectForDesList(AhRestoreCommons.convertStringToBoolean(enableDomainObjectForDesList));

			/**
			 * IP_TRACK_ID
			 */
			colName = "ip_track_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ipTrackId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (isNotNullString(ipTrackId)) {
				Long ipTrackIdNew = AhRestoreNewMapTools
						.getMapMgmtIpTracking(AhRestoreCommons.convertLong(ipTrackId));
				if (null != ipTrackIdNew) {
					routingPolicy.setIpTrackForCheck(AhRestoreNewTools.CreateBoWithId(
							MgmtServiceIPTrack.class, ipTrackIdNew));
				}
			}


			/**
			 * EXCEPTIONLIST_ID
			 */
			colName = "exceptionlist_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String exceptionId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (isNotNullString(exceptionId)) {
				routingPolicy.setDomainObjectForDesList(AhRestoreNewMapTools
						.getMapDomainObject(AhRestoreCommons.convertLong(exceptionId)));
			}


			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'routing_policy' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			routingPolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			routingPolicies.add(routingPolicy);
		}

		return routingPolicies;
	}
	
	private static RoutingProfilePolicy findRoutingProfile(List<RoutingProfilePolicy> list, String policyName, HmDomain owner) {
		if (StringUtils.isBlank(policyName)) {
			return null;
		}
		for (RoutingProfilePolicy policy : list) {
			if (policy.getOwner().getId().longValue() == owner.getId().longValue() && policy.getProfileName().equals(policyName)) {
				return policy;
			}
		}
		return null;
	}
	
	public static boolean RestoreRoutingPolicyByVpnService(){
		try {
			AhRestoreDBTools.logRestoreMsg(" RestoreRoutingPolicyByVpnService start");
			long start = System.currentTimeMillis();

			List<VpnService> allVpnServices =  (List<VpnService>) QueryUtil.executeQuery(VpnService.class,null,null,null,new VpnServiceAction());
			Map<Long, String> routingPolicyMap = new HashMap<Long, String>();
			
			if (null == allVpnServices) {
				AhRestoreDBTools.logRestoreMsg("VPN Services is null");
			} else {
				List<RoutingPolicy> rpList = new ArrayList<RoutingPolicy>();
				List<ConfigTemplate> cTemplates = new ArrayList<ConfigTemplate>();
				List<HiveAp> overrideRouterList = new ArrayList<HiveAp>();

				for (VpnService vpnService : allVpnServices) {
					if (vpnService != null && vpnService.isUpgradeFlag() && VpnService.IPSEC_VPN_LAYER_3 == vpnService.getIpsecVpnType()) {
						RoutingPolicy rPolicy = null;
						List<ConfigTemplate> configTemplate = QueryUtil.executeQuery(ConfigTemplate.class,
								null, new FilterParams("vpnService.id", vpnService.getId()),vpnService.getOwner().getId(),new ImplQueryBo());
						if(!configTemplate.isEmpty()){
							MgmtServiceIPTrack ipTrack = null;
							rPolicy = createRoutingPolicyByVpnServiceRules(vpnService,vpnService.getUserProfileTrafficL3(),ipTrack,true);
							for(ConfigTemplate ct: configTemplate){
								//ct.setRoutingPolicy(rPolicy);
								routingPolicyMap.put(ct.getId(), rPolicy.getPolicyName());
								cTemplates.add(ct);
								//set override routing policy
								List<HiveAp> apList = getOverrideRouterByConfigTemplate(ct,vpnService,vpnService.getUserProfileTrafficL3(),ipTrack);
								if(!apList.isEmpty()){
									overrideRouterList.addAll(apList);
								}
							}
						}else{
							 rPolicy = createRoutingPolicyByVpnServiceRules(vpnService,vpnService.getUserProfileTrafficL3(),null,true);
						}
						if(null != rPolicy){
							rpList.add(rPolicy);
						}
					}
				}
				List<RoutingProfilePolicy> routingProfilePolicyList = null;
				if(rpList.size() > 0){
					/**
					 * use the routing policy list to generate new routing profile policy instances
					QueryUtil.restoreBulkCreateBos(rpList);
					*/
					routingProfilePolicyList = RestoreRoutingProfilePolicy.restoreFromRoutingPolicy(rpList);
				}
				if(cTemplates.size() > 0){
					if (routingPolicyMap.size() > 0 && routingProfilePolicyList.size() > 0) {
						for(ConfigTemplate ct: cTemplates){
							RoutingProfilePolicy policy = findRoutingProfile(routingProfilePolicyList, routingPolicyMap.get(ct.getId()), ct.getOwner());
							if (policy != null && ct.getRoutingProfilePolicy() == null) {
								ct.setRoutingProfilePolicy(policy);
							}
						}
					}
					QueryUtil.bulkUpdateBos(cTemplates);
				}
				if(overrideRouterList.size() > 0){
					QueryUtil.bulkUpdateBos(overrideRouterList);
				}
				//delete UserProfileForTrafficL3 table
				for (VpnService vpnService : allVpnServices){
					if(vpnService.isUpgradeFlag()){
						vpnService.setUpgradeFlag(false);
						if(!vpnService.getUserProfileTrafficL3().isEmpty()){
							vpnService.getUserProfileTrafficL3().clear();
						}
					}
				}
				QueryUtil.bulkUpdateBos(allVpnServices);
			}

			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore Routing Policy according Vpn Service completely. cost:"
							+ (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore Routing Policy according Vpn Service error.", e);
			return false;
		}
		AhRestoreDBTools.logRestoreMsg(" RestoreRoutingPolicyByVpnService end");
		return true;
	}

	private static List<HiveAp> getOverrideRouterByConfigTemplate(ConfigTemplate configTemplate,VpnService vs,List<UserProfileForTrafficL3> uptl3,MgmtServiceIPTrack ipTrack){
		AhRestoreDBTools.logRestoreMsg(" getOverrideRouterByConfigTemplate start");
		try {
			List<HiveAp> overrideRouter = new ArrayList<HiveAp>();
			List<RoutingPolicy> routingPolicies = new ArrayList<RoutingPolicy>();
			String where = "deviceType = :s1 AND configTemplate.id = :s2";
			Object values[] = new Object[2];
			values[0] = HiveAp.Device_TYPE_BRANCH_ROUTER;
			values[1] = configTemplate.getId();
			FilterParams f_params = new FilterParams(where, values);
			List<HiveAp> primaryRouter  = QueryUtil.executeQuery(HiveAp.class, null,f_params,vs.getOwner().getId(),new ImplQueryBo());
			if(!primaryRouter.isEmpty()){
				for(HiveAp router: primaryRouter){
					if(null !=  router.getDeviceInterfaces()){
						DeviceInterface eth0Interface = router.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH0);
						if(null != eth0Interface && router.getRole(eth0Interface) != AhInterface.ROLE_PRIMARY){
							RoutingPolicy temPolicy = createRoutingPolicyByVpnServiceRules(vs,uptl3,ipTrack,false);
							if(null != temPolicy){
								router.setRoutingPolicy(temPolicy);
								routingPolicies.add(temPolicy);
								overrideRouter.add(router);
							}
						}
					}
				}
			}
			if(routingPolicies.size() > 0){
				QueryUtil.bulkCreateBos(routingPolicies);
			}
			AhRestoreDBTools.logRestoreMsg(" getOverrideRouterByConfigTemplate end");
			return overrideRouter;
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Create override Routing Policy for Router according Vpn Service error.", e);
			return null;
		}

	}

	private static RoutingPolicy createRoutingPolicyByVpnServiceRules(VpnService vs,List<UserProfileForTrafficL3> uptraffic,MgmtServiceIPTrack ipTrack,boolean flag){
		List<RoutingPolicy> boIds = QueryUtil.executeQuery(RoutingPolicy.class, null,
				new FilterParams("policyName", vs.getProfileName()), vs.getOwner().getId(),new RoutingPolicyAction());
		if (!boIds.isEmpty()) {
			return null;
		}

		RoutingPolicy rPolicy = new RoutingPolicy();
		if(flag){
			rPolicy.setPolicyName(vs.getProfileName());
		}else{
			rPolicy.setPolicyName(vs.getProfileName()+"_usb");
		}

		rPolicy.setDescription(vs.getDescription());
		rPolicy.setPolicyRuleType(RoutingPolicy.POLICYRULE_CUSTOM);
		rPolicy.setOwner(vs.getOwner());

		/*MgmtServiceIPTrack ipTrack = null;
		if(!vs.getVpnGateWaysSetting().isEmpty()){
			 HiveAp ap = QueryUtil.findBoById(HiveAp.class, vs.getVpnGateWaysSetting().get(0).getApId());
			 if(null != ap){
				 ipTrack = ap.getVpnIpTrack();
			 }
		}*/
		if(null != ipTrack){
			rPolicy.setEnableIpTrackForCheck(true);
			rPolicy.setIpTrackForCheck(ipTrack);
		}


		boolean whiteListFlag = false;
		List<RoutingPolicyRule> routingPolicyRuleList = new ArrayList<RoutingPolicyRule>();

		if(null != uptraffic){
			for(UserProfileForTrafficL3 userProfileForTrafficL3:uptraffic){
				if(userProfileForTrafficL3.getVpnTunnelBehavior() == UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS){
					whiteListFlag = true;
				}
				RoutingPolicyRule rule = createRoutingPolicyRule(vs, ipTrack, userProfileForTrafficL3, flag);
				if(null != rule){
					routingPolicyRuleList.add(rule);
				}
			}
		}

		if(whiteListFlag){
			rPolicy.setDomainObjectForDesList(vs.getDomObj());
		}
		//added for any
		RoutingPolicyRule anyRule = createRoutingPolicyRule(vs,ipTrack,null,flag);
		RoutingPolicyRule anyGuestRule = createRoutingPolicyRule(vs,ipTrack,null,flag);
		if(null != anyRule && null != anyGuestRule){
			anyRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
			anyGuestRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
			if(null != ipTrack){
				anyGuestRule.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
				anyGuestRule.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
			}else{
				anyGuestRule.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
			}
			routingPolicyRuleList.add(anyRule);
			routingPolicyRuleList.add(anyGuestRule);
		}

		rPolicy.setRoutingPolicyRuleList(routingPolicyRuleList);
		return rPolicy;
	}

	private static RoutingPolicyRule createRoutingPolicyRule(VpnService vs,MgmtServiceIPTrack ipTrack,UserProfileForTrafficL3  upft3, boolean flag){
		RoutingPolicyRule rpr = new RoutingPolicyRule();
		if(null != upft3 && null != upft3.getUserProfile()){
			UserProfile upProfile = QueryUtil.findBoById(UserProfile.class, upft3.getUserProfile().getId(),new UserProfilesAction());
			if(upProfile != null){
				rpr.setSourceUserProfile(upProfile);
				rpr.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_USERPROFILE);
			}else{
				return null;
			}

		}else{
			rpr.setSourceUserProfile(null);
		}
		if(vs.getVpnGateWaysSetting().size() > 0){
			if(null != ipTrack){
				rpr.setIpTrackReachablePri(ipTrack);
				rpr.setIpTrackReachableSec(ipTrack);
				if(!RestoreConfigNetwork.RESTORE_BEFORE_CASABLANCA_FLAG){
					if(flag){
						rpr.setInterfaceTypePri(RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0);
						rpr.setInterfaceTypeSec(RoutingPolicyRule.ROUTING_POLICY_RULE_USB);
					}else{
						rpr.setInterfaceTypePri(RoutingPolicyRule.ROUTING_POLICY_RULE_USB);
						rpr.setInterfaceTypeSec(RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0);
					}

				}else{
					rpr.setInterfaceTypePri(RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0);
					rpr.setInterfaceTypeSec(RoutingPolicyRule.ROUTING_POLICY_RULE_NONE);
				}
				if(null != upft3){
					if(UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL == upft3.getVpnTunnelBehavior()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
						rpr.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_ALL);
					}else if(UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS == upft3.getVpnTunnelBehavior()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
						rpr.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
					}else{
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
						rpr.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
					}
				}else{
					if(VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL == vs.getRouteTrafficType()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_SPLIT);
						rpr.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_SPLIT);
					}else{
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
						rpr.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_ALL);
					}
				}
			}else{
				rpr.setIpTrackReachablePri(null);
				if(flag){
					rpr.setInterfaceTypePri(RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0);
				}else{
					rpr.setInterfaceTypePri(RoutingPolicyRule.ROUTING_POLICY_RULE_USB);
				}
				if(null != upft3){
					if(UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL == upft3.getVpnTunnelBehavior()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
					}else if(UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS == upft3.getVpnTunnelBehavior()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
					}else{
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
					}
				}else{
					if(VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL == vs.getRouteTrafficType()){
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_SPLIT);
					}else{
						rpr.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
					}
				}
			}
		}
		return rpr;
	}

	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			 if (bo instanceof HiveAp) {
					HiveAp hiveAp = (HiveAp) bo;
					if (hiveAp.getConfigTemplate() != null) {
						hiveAp.getConfigTemplate().getId();
					}
					if (hiveAp.getDeviceInterfaces() != null){
						hiveAp.getDeviceInterfaces().values();
					}
			}else if(bo instanceof ConfigTemplate){
				ConfigTemplate cTemplate = (ConfigTemplate)bo;
				if (cTemplate.getOwner() != null)
					cTemplate.getOwner().getId();
//				if(null != cTemplate.getRouterIpTrack()){
//					cTemplate.getRouterIpTrack().getId();
//				}
			}

			return null;
		}
	}
}
