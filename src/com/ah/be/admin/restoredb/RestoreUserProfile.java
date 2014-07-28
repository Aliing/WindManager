package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.Vlan;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.HmTimeStamp;

/*
 * support restoration for VHM
 * joseph chen, 05/04/2008
 */

public class RestoreUserProfile implements QueryBo {

	private final Set<Long> DEVICE_POLICY_RULE_OLD_ID = new HashSet<>();

	public void saveToDatabase() {
		try{
			saveUserProfile();
		}
		catch(Exception e){
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}

	private boolean saveUserProfile() throws Exception{
		AhRestoreGetXML xmlFile = new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("USER_PROFILE"))
			return false;

		List<String[]> list_user= xmlFile.getM_lst_xmlFile();
		String[] column_user= xmlFile.getM_str_colName();
		List<String[]> list_schedule=null;
		String[] column_schedule=null;

		boolean bln_readFile=false;
		boolean blnAddLogFlg = false;

		List<UserProfile> listBo=new ArrayList<>();

		if(list_user!=null && !list_user.isEmpty()){
			// reassign device policy rules
			DEVICE_POLICY_RULE_OLD_ID.clear();
			Map<String, List<DevicePolicyRule>> allrules = getAllDevicePolicyRule();

			for (String[] userAttrs : list_user) {
				Long id = null;
				UserProfile userProfile = new UserProfile();
				for (int j = 0; j < userProfile.getFieldValues().length; j++) {
					if (!blnAddLogFlg && userProfile.getFieldValues().length >= 27 &&
							AhRestoreCommons.checkColExist("schedulingWeight", column_user) == -1) {
						HmUpgradeLog upgradeLog = new HmUpgradeLog();
						upgradeLog.setFormerContent(NmsUtil.getOEMCustomer().getNmsName()+" applied QoS policing rates and scheduling weights for user profiles in the network policy.");
						upgradeLog.setPostContent(NmsUtil.getOEMCustomer().getNmsName()+" now applies user profile-based QoS policing rates and scheduling weights in user profile definitions and has assigned default values to all user profiles.");
						upgradeLog.setRecommendAction("If you need to change the policing rate and scheduling weight values for one or more user profiles, " +
								"go to the Configuration > User Profiles page, select a profile, and modify the values.");
						upgradeLog.setOwner(AhRestoreNewMapTools.getonlyDomain());
						upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(), AhRestoreNewMapTools.getonlyDomain().getTimeZoneString()));
						upgradeLog.setAnnotation("Click to add an annotation");
						try {
							QueryUtil.createBo(upgradeLog);
						} catch (Exception e) {
							AhRestoreDBTools.logRestoreMsg("insert upgrade log error for user profile");
							AhRestoreDBTools.logRestoreMsg(e.getMessage());
						}
						blnAddLogFlg = true;
					}
					String colName = userProfile.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName, column_user);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(userAttrs[col_index]); // joseph chen , 06/02/2008

//						String name=null;
						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									userProfile.setId(id);
									continue;
								case 1:
									userProfile.setUserProfileName(value);
									continue;
								case 2:
									userProfile.setAttributeValue(Short.parseShort(value));
									continue;
								case 3:
									userProfile.setDescription(value);
									continue;
								case 4:
									userProfile.setActionMac(Short.parseShort(value));
									continue;
								case 5:
									userProfile.setActionIp(Short.parseShort(value));
									continue;
								case 6:
									userProfile.setDefaultFlag(false);
									continue;
								case 7:
									Long newUserProfileAttributeId = AhRestoreNewMapTools.getMapUserAttribute(Long.parseLong(value));
									userProfile.setUserProfileAttribute(AhRestoreNewTools.CreateBoWithId(UserProfileAttribute.class, newUserProfileAttributeId));
									continue;
								case 8:
									Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(value));
									userProfile.setVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class, newVlanId));
									continue;
								case 9:
									Long newQosRateControlId = AhRestoreNewMapTools.getMapQosRateControlAndQueu(Long.parseLong(value));
									userProfile.setQosRateControl(AhRestoreNewTools.CreateBoWithId(QosRateControl.class, newQosRateControlId));
									/*if (null == userProfile.getQosRateControl()) {
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("qosName", BeParaModule.DEFAULT_QOS_RATE_CONTROL_NAME);
										QosRateControl newBo = HmBeParaUtil.getDefaultProfile(QosRateControl.class, map);
										userProfile.setQosRateControl(newBo);
									}*/
									continue;
								case 10:
									AhRestoreNewMapTools.setMapOldUserProfileTunnel(userProfile.getUserProfileName(), value);
									Long newTunnelSettingId = AhRestoreNewMapTools.getMapIdentityBasedTunnel(Long.parseLong(value));
									userProfile.setTunnelSetting(AhRestoreNewTools.CreateBoWithId(TunnelSetting.class, newTunnelSettingId));
									continue;
								case 11:
									Long newIpPolicyId = AhRestoreNewMapTools.getMapIpPolicy(Long.parseLong(value));
									userProfile.setIpPolicyTo(AhRestoreNewTools.CreateBoWithId(IpPolicy.class, newIpPolicyId));
									continue;
								case 12:
									Long newIpPolicyFromId = AhRestoreNewMapTools.getMapIpPolicy(Long.parseLong(value));
									userProfile.setIpPolicyFrom(AhRestoreNewTools.CreateBoWithId(IpPolicy.class, newIpPolicyFromId));
									continue;
								case 13:
									Long newMacPolicyId = AhRestoreNewMapTools.getMapMacPolicy(Long.parseLong(value));
									userProfile.setMacPolicyTo(AhRestoreNewTools.CreateBoWithId(MacPolicy.class, newMacPolicyId));
									continue;
								case 14:
									Long newMacPolicyFromId = AhRestoreNewMapTools.getMapMacPolicy(Long.parseLong(value));
									userProfile.setMacPolicyFrom(AhRestoreNewTools.CreateBoWithId(MacPolicy.class, newMacPolicyFromId));
									continue;
								case 15:
									userProfile.setEnableCallAdmissionControl(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 16:
									userProfile.setGuarantedAirTime(Short.parseShort(value));
									continue;
								case 17:
									userProfile.setEnableShareTime(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 18:
									userProfile.setTunnelTraffic((short) AhRestoreCommons.convertInt(value));
								case 19:
									Long newAirScreenRuleGroupId = AhRestoreNewMapTools.getMapAirscreenRuleGroup(Long.parseLong(value));
									userProfile.setAsRuleGroup(AhRestoreNewTools.CreateBoWithId(AirScreenRuleGroup.class, newAirScreenRuleGroupId));
									continue;
								case 20:
									userProfile.setSlaEnable(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 21:
									userProfile.setSlaBandwidth(AhRestoreCommons.convertInt(value, 500));
									continue;
								case 22:
									userProfile.setSlaAction((short) AhRestoreCommons.convertInt(value, UserProfile.SLA_ACTION_LOG));
									continue;
								case 23:
									userProfile.setPolicingRate(AhRestoreCommons.convertInt(value, 54000));
									continue;
								case 24:
									userProfile.setPolicingRate11n(AhRestoreCommons.convertInt(value, 1000000));
									continue;
								case 25:
									userProfile.setSchedulingWeight(AhRestoreCommons.convertInt(value, 10));
									continue;
								case 26:
									userProfile.setBlnUserManager(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 27:
									userProfile.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								case 28:
									userProfile.setEnableAssign(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 29:
									// TODO for remove network object in user profile
									AhRestoreNewMapTools.setMapUserProfileNetworkObject(id, AhRestoreCommons.convertLong(value));
									
									Long vlanId = AhRestoreNewMapTools.getMapNetworkObjectVlan(AhRestoreCommons.convertLong(value));
									if(null != vlanId) {
										Long newVlanIdSec = AhRestoreNewMapTools.getMapVlan(vlanId);
										userProfile.setVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class, newVlanIdSec));
									}
//									Long netId = AhRestoreNewMapTools.getMapVpnNetwork(AhRestoreCommons.convertLong(value));
//									if(null != netId) {
//										userProfile.setNetworkObj(AhRestoreNewTools.CreateBoWithId(VpnNetwork.class, netId));
//									}
									continue;
								case 30:
									userProfile.setScheduleDenyMode(Short.parseShort(value));
									continue;
								case 31:
									userProfile.setQosMarkTypeMode(Short.parseShort(value));
									continue;
								case 32:
									Long newMarkerMapId = AhRestoreNewMapTools.getMapMarking(Long.parseLong(value));
									userProfile.setMarkerMap(AhRestoreNewTools.CreateBoWithId(QosMarking.class, newMarkerMapId));
									continue;
								case 33:
									userProfile.setPolicingRate11ac(AhRestoreCommons.convertInt(value, 1000000));
									continue;
								default:
									continue;
							}
						}
						// ensure that the QosRateControl is not null
						if (j == 9) {
							if (null == userProfile.getQosRateControl()) {
								Map<String, Object> map = new HashMap<>();
								map.put("qosName", BeParaModule.DEFAULT_QOS_RATE_CONTROL_NAME);
								QosRateControl newBo = HmBeParaUtil.getDefaultProfile(QosRateControl.class, map);
								userProfile.setQosRateControl(newBo);
							}
						}
						// add user category from 3.4r10
//					} else if (26 == j) {
//						int accIndex = AhRestoreCommons.checkColExist("guestAccess",column_user);
//						if (accIndex > -1) {
//							boolean enableGuess = AhRestoreCommons.convertStringToBoolean(AhRestoreCommons.convertString(list_user.get(i)[accIndex]));
//							if (!enableGuess) {
//								userProfile.setUserCategory(UserProfile.USER_CATEGORY_CUSTOM);
//							}
//						}
					}
				}
				//read schedule from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("USER_PROFILE_SCHEDULER")) {
						list_schedule = xmlFile.getM_lst_xmlFile();
						column_schedule = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
				}
				//set schedule
				if (list_schedule != null && !list_schedule.isEmpty())
					userProfile.setUserProfileSchedulers(getSetOfSchedule(id, list_schedule, column_schedule));
				//AhRestoreMapTool.setMapUserProfile(String.valueOf(id), userProfile.getUserProfileName());

				// the default value has been inserted before this
				if (BeParaModule.DEFAULT_USER_PROFILE_NAME.equals(userProfile.getUserProfileName())) {
					Map<String, Object> map = new HashMap<>();
					map.put("userProfileName", BeParaModule.DEFAULT_USER_PROFILE_NAME);
					UserProfile newBo = HmBeParaUtil.getDefaultProfile(UserProfile.class, map);
					if (null != newBo) {
						AhRestoreNewMapTools.setMapUserProfile(id, newBo.getId());
						RestoreRoutingProfilePolicy.saveUserProfileName(newBo.getId(), BeParaModule.DEFAULT_USER_PROFILE_NAME);
					}
					continue;
				}
				
				// set owner, joseph chen 05/04/2008
				if (userProfile.getOwner() == null) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'user_profile' data be lost, cause: 'owner' column is not available.");
					continue;
					//userProfile.setOwner(AhRestoreNewMapTools.getonlyDomain());
				}

				QosRateControl qsr = QueryUtil.findBoById(QosRateControl.class, userProfile.getQosRateControl().getId());
				if (qsr!=null && qsr.getRateLimit11n() > userProfile.getPolicingRate11n()) {
					userProfile.setPolicingRate11n(qsr.getRateLimit11n());
				}
				
				if (qsr!=null && qsr.getRateLimit11ac() > userProfile.getPolicingRate11ac()) {
					userProfile.setPolicingRate11ac(qsr.getRateLimit11ac());
				}

				// reassign device policy rule
				if (userProfile.isEnableAssign() && null != allrules) {
					userProfile.setAssignRules(allrules.get(id.toString()));
				}

				// just remain for older customers
				if (userProfile.getUserProfileName().equals(BeParaModule.PRE_DEFINED_USER_PROFILE_NAME)
						&& userProfile.getDescription()!=null
						&& userProfile.getDescription().equals("Predefined for the QuickStart-Wireless-Routing network policy")){
					userProfile.setDescription("Predefined for the QuickStart network policies");
				}

				// user profile name is needed, any way.
				if (userProfile.getUserProfileName() != null && !"".equals(userProfile.getUserProfileName().trim())) {
					listBo.add(userProfile);
				} else {
					AhRestoreDBTools.logRestoreMsg("Ignore the user profile id: "+userProfile.getId()+".");
				}
			}
			for(UserProfile onePro : listBo) {
				if (null == onePro.getIpPolicyFrom() && null == onePro.getIpPolicyTo()) {
					onePro.setActionIp((short)-1);
				}
				if (null == onePro.getMacPolicyFrom() && null == onePro.getMacPolicyTo()) {
					onePro.setActionMac((short)-1);
				}
			}

			List<Long> lOldId = new ArrayList<>();

			for (UserProfile bo : listBo) {
				lOldId.add(bo.getId());
			}

			QueryUtil.restoreBulkCreateBos(listBo);

			for(int i=0; i < listBo.size(); ++i)
			{
				AhRestoreNewMapTools.setMapUserProfile(lOldId.get(i), listBo.get(i).getId());
				RestoreRoutingProfilePolicy.saveUserProfileName(listBo.get(i).getId(), listBo.get(i).getUserProfileName());
			}

			if (!DEVICE_POLICY_RULE_OLD_ID.isEmpty()) {
				for (Long oldId : DEVICE_POLICY_RULE_OLD_ID) {
					Long newId = AhRestoreNewMapTools.getMapUserProfile(oldId);
					List<UserProfile> userProfiles = (List<UserProfile>) QueryUtil.executeQuery("select distinct bo from " + UserProfile.class.getSimpleName() + " as bo join bo.assignRules as joined", null, new FilterParams("joined.userProfileId", oldId), null, this);

					if (!userProfiles.isEmpty()) {
						if (newId == null) {
							for (UserProfile userProfile : userProfiles) {
								for (Iterator<DevicePolicyRule> devicePolicyRuleIter = userProfile.getAssignRules().iterator(); devicePolicyRuleIter.hasNext();) {
									DevicePolicyRule devicePolicyRule = devicePolicyRuleIter.next();
									Long userProfileId = devicePolicyRule.getUserProfileId();

									if (oldId.equals(userProfileId)) {
										devicePolicyRuleIter.remove();
									}
								}
							}
						} else {
							for (UserProfile userProfile : userProfiles) {
								for (DevicePolicyRule devicePolicyRule : userProfile.getAssignRules()) {
									Long userProfileId = devicePolicyRule.getUserProfileId();

									if (oldId.equals(userProfileId)) {
										devicePolicyRule.setUserProfileId(newId);
									}
								}
							}
						}

						QueryUtil.bulkUpdateBos(userProfiles);
					}
				}
			}
		}

		return true;
	}

	private Set<Scheduler> getSetOfSchedule(Long id,List<String[]> list,String[] columns) {
		Set<Scheduler> set=null;
		if(id==null || list==null || list.isEmpty())
			return null;

		String[] fields={"user_profile_id","scheduler_id"};
		for (String[] attrs : list) {
			int col_index = AhRestoreCommons.checkColExist(fields[0], columns);
			if (col_index >= 0) {
				String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

				if (!"".equals(value)) {
					value = value.trim();
					Long id_value = Long.parseLong(value);
					if (!id_value.equals(id))
						continue;
					col_index = AhRestoreCommons.checkColExist(fields[1], columns);
					value = attrs[col_index];

					Long newSchedulerId = AhRestoreNewMapTools.getMapSchedule(Long.parseLong(value.trim()));
					Scheduler scheduler = AhRestoreNewTools.CreateBoWithId(Scheduler.class, newSchedulerId);
					if (set == null)
						set = new HashSet<>();
					set.add(scheduler);
				}
			}
		}
		return set;
	}

	/**
	 * Get all information from device_policy_rule table
	 *
	 * @return Map<String, List<DevicePolicyRule>>
	 * @throws AhRestoreColNotExistException -
	 *             if device_policy_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing device_policy_rule.xml.
	 */
	private Map<String, List<DevicePolicyRule>> getAllDevicePolicyRule() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of device_policy_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("device_policy_rule");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in device_policy_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<DevicePolicyRule>> policyRules = new HashMap<>();

		boolean isColPresent;
		String colName;
		DevicePolicyRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new DevicePolicyRule();

			/**
			 * Set user_profile_id
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set userprofilename
			 */
			colName = "userprofilename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String userprofilename = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setUserProfileName(AhRestoreCommons.convertString(userprofilename));

			/**
			 * Set mac_obj_id
			 */
			colName = "mac_obj_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String oldMac = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(oldMac)) {
				Long macId = AhRestoreNewMapTools.getMapMacAddress(AhRestoreCommons.convertLong(oldMac));
				if(null != macId) {
					singleInfo.setMacObj(AhRestoreNewTools.CreateBoWithId(MacOrOui.class, macId));
				}
			}

			/**
			 * Set os_obj_id
			 */
			colName = "os_obj_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String oldOs = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(oldOs)) {
				singleInfo.setOsObj(AhRestoreNewMapTools.getMapOsObject(AhRestoreCommons.convertLong(oldOs)));
			}

			/**
			 * Set domain_obj_id
			 */
			colName = "domain_obj_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String oldDom = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(oldDom)) {
				singleInfo.setDomObj(AhRestoreNewMapTools.getMapDomainObject(AhRestoreCommons.convertLong(oldDom)));
			}

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setRuleId((short)AhRestoreCommons.convertInt(ruleid));

			/**
			 * Set ownership
			 */
			colName = "ownership";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String ownership = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setOwnership(AhRestoreCommons.convertInt(ownership));

			
			/**	
			 * Set userProfileId
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String userprofileid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singleInfo.setUserProfileId(AhRestoreCommons.convertLong(userprofileid));

			/**
			 * Set reorder
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"device_policy_rule", colName);
			String reorder = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setReorder(AhRestoreCommons.convertInt(reorder));

			// collect the old id info
			if (!DEVICE_POLICY_RULE_OLD_ID.contains(singleInfo.getUserProfileId()))
				DEVICE_POLICY_RULE_OLD_ID.add(singleInfo.getUserProfileId());

			List<DevicePolicyRule> ruleList = policyRules.get(id);
			if (null == ruleList) {
				ruleList = new ArrayList<>();
				ruleList.add(singleInfo);
				policyRules.put(id, ruleList);
			} else {
				ruleList.add(singleInfo);
			}
		}

		// make device policy rule in order
		if (policyRules != null && !policyRules.isEmpty()) {
			for (String s : policyRules.keySet()) {
				List<DevicePolicyRule> ruleList = policyRules.get(s);
				Collections.sort(ruleList,
						new Comparator<DevicePolicyRule>() {
							@Override
							public int compare(DevicePolicyRule rule1, DevicePolicyRule rule2) {
								Integer id1 = rule1.getReorder();
								Integer id2 = rule2.getReorder();
								return id1.compareTo(id2);
							}
						});
			}
		}

		return policyRules;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}

		if (bo instanceof UserProfile) {
			UserProfile profile = (UserProfile) bo;

			if (profile.getAssignRules() != null) {
				profile.getAssignRules().size();
			}
		}

		return null;
	}

}