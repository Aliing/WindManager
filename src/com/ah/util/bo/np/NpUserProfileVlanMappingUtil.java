package com.ah.util.bo.np;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.HmBo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryCertainBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.util.bo.BoAssistant;
import com.ah.util.bo.BoAssistant.BoIdGetterInter;

public class NpUserProfileVlanMappingUtil {
	public static void printUpVlanMapping(Set<UserProfileVlanMapping> mappings, String tip) {
		for (UserProfileVlanMapping mapping : mappings) {
			System.out.println(tip + "-->" + mapping);
		}
	}
	public static <T extends HmBo> void setUpVlanMapping(ConfigTemplate ct, Long[] upIds, Long[] vlanIds) {
		if (ct == null
				|| upIds == null || upIds.length < 1
				|| vlanIds == null || vlanIds.length < 1
				|| upIds.length != vlanIds.length) {
			return;
		}
		
		List<Long> uniUpIds = new ArrayList<>(), 
				uniVlanIds = new ArrayList<>();
		for (int i = 0; i < upIds.length; i++) {
			if (!uniUpIds.contains(upIds[i])) {
				uniUpIds.add(upIds[i]);
				uniVlanIds.add(vlanIds[i]);
			}
		}
		Set<UserProfileVlanMapping> mapping = new HashSet<>();
		Map<Long, UserProfile> ups = BoAssistant.getIdObjectMap(UserProfile.class, uniUpIds, ct.getOwner(), new QueryCertainBo<UserProfile>() {
			public Collection<HmBo> loadBo(UserProfile userProfile) {
				if (userProfile.getAssignRules() != null) {
					userProfile.getAssignRules().size();
				}
				return null;
			}
		});
		Map<Long, Vlan> vlans = BoAssistant.getIdObjectMap(Vlan.class, uniVlanIds, ct.getOwner(), null);
		if (ups == null
				|| vlans == null) {
			return;
		}
		
		Map<Long, UserProfileVlanMapping> existedUpVlanMappings = 
				BoAssistant.getIdObjectMap(ct.getUpVlanMapping(), new BoIdGetterInter<UserProfileVlanMapping>(){
					@Override
					public Long getId(UserProfileVlanMapping bo) {
						return bo.getUserProfile().getId();
					}
				});
		
		Long upIdTmp;
		Set<Long> noExistMappingKeys = existedUpVlanMappings.keySet();
		for (int i = 0; i < uniUpIds.size(); i++) {
			upIdTmp = uniUpIds.get(i);
			if (existedUpVlanMappings.containsKey(upIdTmp)) {
				existedUpVlanMappings.get(upIdTmp).setVlan(vlans.get(uniVlanIds.get(i)));
				mapping.add(existedUpVlanMappings.get(upIdTmp));
				noExistMappingKeys.remove(upIdTmp);
			} else {
				mapping.add(new UserProfileVlanMapping(ups.get(upIdTmp), vlans.get(uniVlanIds.get(i)), ct, ct.getOwner()));
			}
		}
		
		Set<Long> noExistInNpUps = new HashSet<>();
		Map<Long, UserProfile> curNpUserProfiles = getUpVlanMappedUserProfileIds(ct);
		if (!noExistMappingKeys.isEmpty()) {
			for (Long idTmp : noExistMappingKeys) {
				if (curNpUserProfiles != null
						&& curNpUserProfiles.containsKey(idTmp)) {
					mapping.add(existedUpVlanMappings.get(idTmp));
				} else {
					noExistInNpUps.add(idTmp);
				}
			}
		}
		ct.setUpVlanMapping(mapping);
		
		//remove those user profile vlan mappings that user profile is not exist in network policy
		if (!noExistInNpUps.isEmpty()) {
			Set<Long> idsTmp = new HashSet<>();
			for (Long idTmp : noExistInNpUps) {
				idsTmp.add(existedUpVlanMappings.get(idTmp).getId());
			}
			try {
				QueryUtil.removeBos(UserProfileVlanMapping.class, idsTmp);
			} catch (Exception e) {
			}
		}
	}
	
	public static void removeNotExistUserProfileVlanMapping(ConfigTemplate ct) {
		if (ct.getUpVlanMapping() != null
				&& !ct.getUpVlanMapping().isEmpty()) {
			Map<Long, UserProfile> allUps = getAllUserProfiles(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, true, null, false, false, -1);
			Set<Long> noExistIds = new HashSet<>();
			if (allUps != null
					&& !allUps.isEmpty()) {
				Set<UserProfileVlanMapping> mappings = new HashSet<>();
				for (UserProfileVlanMapping mapping : ct.getUpVlanMapping()) {
					if (!allUps.containsKey(mapping.getUserProfile().getId())) {
						noExistIds.add(mapping.getId());
					} else {
						mappings.add(mapping);
					}
				}
				
				if (noExistIds != null
						&& !noExistIds.isEmpty()) {
					ct.setUpVlanMapping(mappings);
					try {
						QueryUtil.removeBos(UserProfileVlanMapping.class, noExistIds);
					} catch (Exception e) {
					}
				}
			}
		}
	}
	
	public static <T extends HmBo> void addOrEditUpVlanMapping(ConfigTemplate ct, Long upId, Long vlanId) {
		Vlan vlan = QueryUtil.findBoById(Vlan.class, vlanId);
		if (vlan == null) {
			return;
		}
		boolean blnExist = false;
		for (UserProfileVlanMapping mapping : ct.getUpVlanMapping()) {
			if (upId.equals(mapping.getUserProfile().getId())) {
				blnExist = true;
				mapping.setVlan(vlan);
			}
		}
		if (!blnExist) {
			UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, upId);
			if (userProfile != null) {
				ct.getUpVlanMapping().add(new UserProfileVlanMapping(userProfile, vlan, ct, ct.getOwner()));
			}
		}
	}
	
	public static Map<Long, UserProfileVlanMapping> getUpVlanMappedUserProfiles(ConfigTemplate ct) {
		return getUpVlanMappedUserProfiles(ct, null);
	}
	public static <T extends HmBo> Map<Long, UserProfileVlanMapping> getUpVlanMappedUserProfiles(ConfigTemplate ct, T relativeBo) {
		Map<Long, UserProfileVlanMapping> result = new HashMap<>();
		if (!ct.getUpVlanMapping().isEmpty()) {
			Map<Long, UserProfile> curNpUserProfiles = null;
			boolean blnForCertainProfile = false;
			if (isMappingForSsidProfile(relativeBo)
					|| isMappingForPortAccessProfile(relativeBo)) {
				blnForCertainProfile = true;
				curNpUserProfiles = getUpVlanMappedUserProfileIds(ct, relativeBo);
			}
			for (UserProfileVlanMapping mapping : ct.getUpVlanMapping()) {
				if (!blnForCertainProfile
						|| (curNpUserProfiles != null 
							&& curNpUserProfiles.containsKey(mapping.getUserProfile().getId()))) {
					result.put(mapping.getUserProfile().getId(), mapping);
				}
			}
		}
		return result;
	}
	
	public static Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct) {
		return getFullUpVlanMappings(ct, true);
	}
	public static Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct, boolean blnWithReAssigned) {
		return getFullUpVlanMappings(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, blnWithReAssigned, false, -1);
	}
	public static <T extends HmBo> Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct, T relativeBo) {
		return getFullUpVlanMappings(ct, relativeBo, true);
	}
	public static <T extends HmBo> Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct, T relativeBo, boolean blnWithReAssigned) {
		return getFullUpVlanMappings(ct, whatMappingIsFor(relativeBo), relativeBo, blnWithReAssigned, false, -1);
	}
	
	public static<T extends HmBo> Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct, boolean blnWithReAssigned, boolean routingOnly, int hiveApModel) {
		return getFullUpVlanMappings(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, blnWithReAssigned, routingOnly, hiveApModel);
	}
	
	public static <T extends HmBo> Map<Long, UserProfileVlanMapping> getFullUpVlanMappings(ConfigTemplate ct, String type, T relativeBo, boolean blnWithReAssigned, boolean routingOnly, int hiveApModel) {
		//final Map<Long, UserProfile> upsLevel0 = getAllUserProfiles(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, false, null, true);
		/*Map<Long, UserProfile> allUps = getAllUserProfiles(ct, type, relativeBo, blnWithReAssigned, new UserProfileFilter() {
			@Override
			public boolean filter(UserProfile userProfile,
					UserProfile oriUserProfile, boolean blnReAssigned) {
				if (!blnReAssigned) {
					return true;
				} else if (upsLevel0.containsKey(userProfile.getId())) {
					return true;
				}
				return false;
			}
		}, true);*/
		if (routingOnly && !ct.getConfigType().isRouterContained()) {
			return new HashMap<>();
		}
		Map<Long, UserProfile> allUps = getAllUserProfiles(ct, type, relativeBo, blnWithReAssigned, null, true, routingOnly, hiveApModel);
		
		Map<Long, UserProfileVlanMapping> result = new HashMap<>();
		Map<Long, UserProfileVlanMapping> existedMapping =  
				BoAssistant.getIdObjectMap(ct.getUpVlanMapping(), new BoIdGetterInter<UserProfileVlanMapping>(){
					@Override
					public Long getId(UserProfileVlanMapping bo) {
						return bo.getUserProfile().getId();
					}
				});
		
		for (UserProfile up : allUps.values()) {
			if (!existedMapping.containsKey(up.getId())) {
				result.put(up.getId(), 
						new UserProfileVlanMapping(up, up.getVlan(), ct, ct.getOwner()));
			} else {
				result.put(up.getId(), existedMapping.get(up.getId()));
			}
		}
		
		return result;
	}
	
	public static Set<Vlan> getAllRoutingNativeVlan(ConfigTemplate ct, int hiveApModel, List<String> allowVlans) {
		Set<Vlan> set = new HashSet<Vlan>();
       QueryBo accessQueryBo = new QueryCertainBo<PortAccessProfile>() {
           @Override
           public Collection<HmBo> loadBo(PortAccessProfile profile) {
        	  if ( profile.getNativeVlan()!=null) {
        		  profile.getNativeVlan().getId();
        	  }
               return null;
           }
       };
       List<PortAccessProfile> profiles = new ArrayList<>();
       PortAccessProfile portAccessProfile;

       if (!ct.getPortProfiles().isEmpty()) {
           for (PortGroupProfile groupProfile : ct.getPortProfiles()) {
	           	if (groupProfile.getDeviceType()!=HiveAp.Device_TYPE_BRANCH_ROUTER) {
	           		continue;
	           	}
	           	if (hiveApModel!=-1) {
            		String[] models = groupProfile.getDeviceModelStrs();
            		if (models==null) {
            			continue;
            		}
            		boolean continueOp = true;
            		for(String key: models){
            			if (key.equals(String.valueOf(hiveApModel))){
            				continueOp=false;
            			}
            		}
            		if (continueOp) {
            			continue;
            		}
            	}
	           	
               for (PortBasicProfile basic : groupProfile.getBasicProfiles()) {
                   portAccessProfile = QueryUtil.findBoById(
                           PortAccessProfile.class, basic
                                   .getAccessProfile().getId(),
                           accessQueryBo);
                   profiles.add(portAccessProfile);
               }
           }
       }

       for (PortAccessProfile profile : profiles) {
           if (null == profile) {
               continue;
           }
           if (profile.getNativeVlan() != null) {
        	   set.add(profile.getNativeVlan());
           }
           if (allowVlans!=null) {
	           if (profile.getAllowedVlan()!=null && !StringUtils.isEmpty(profile.getAllowedVlan())) {
	        	   allowVlans.add(profile.getAllowedVlan());
	           }
           }
       }
       
       return set;
	}
	
	
	/**
	 * if mapping is set for a user profile, use that mapping, otherwise use raw mapping
	 * @param ct
	 * @param type
	 * @param queryBo
	 * @return
	 */
	public static <T extends HmBo> List<UserProfileVlanRelation> prepareUserProfileVlanMappingRelation(ConfigTemplate ct, String type, T relativeBo, QueryBo queryBo) {
		List<UserProfileVlanRelation> upVlanMapping = new ArrayList<>();
		Map<Long, UserProfileVlanMapping> existMapping = getUpVlanMappedUserProfiles(ct);
		Long relativeBoId = null;
		if (relativeBo != null) {
			relativeBoId = relativeBo.getId();
		}
		
		//final Map<Long, UserProfile> upsLevel0 = getAllUserProfiles(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, false, null, true);
		UserProfileFilter upFilter = null;
		/*UserProfileFilter upFilter = new UserProfileFilter() {
			@Override
			public boolean filter(UserProfile userProfile,
					UserProfile oriUserProfile, boolean blnReAssigned) {
				if (!blnReAssigned) {
					return true;
				} else if (upsLevel0.containsKey(userProfile.getId())) {
					return true;
				}
				return false;
			}
		};*/
		
		if (relativeBo == null || isMappingForSsidProfile(type)) {
			prepareUserProfileVlanMappingRelationOnSsid(upVlanMapping, existMapping, ct, relativeBoId, upFilter, queryBo);
		} 
		if (relativeBo == null || isMappingForPortAccessProfile(type)) {
			prepareUserProfileVlanMappingRelationOnPortAccess(upVlanMapping, existMapping, ct, relativeBoId, upFilter, queryBo);
		} 
		
		return upVlanMapping;
	}
	private static void prepareUserProfileVlanMappingRelationOnSsid(List<UserProfileVlanRelation> upVlanMapping, 
			Map<Long, UserProfileVlanMapping> existMapping, ConfigTemplate ct, Long id, UserProfileFilter upFilter, QueryBo queryBo) {
		if (ct.getSsidInterfaces() != null) {
			List<SsidProfile> ssidProfiles = null;
			List<Long> ids = new ArrayList<>();
			for (ConfigTemplateSsid ctSsid : ct.getSsidInterfaces().values()) {
				if (ctSsid.getSsidProfile() == null) {
					continue;
				}
				if (id != null
						&& !id.equals(ctSsid.getSsidProfile().getId())) {
					continue;
				}
				ids.add(ctSsid.getSsidProfile().getId());
			}
			if (!ids.isEmpty()) {
				if (queryBo != null) {
					ssidProfiles = QueryUtil.executeQuery(SsidProfile.class, 
							null, 
							new FilterParams("id", ids), 
							ct.getOwner().getId(),
							queryBo);
				} else {
					ssidProfiles = QueryUtil.executeQuery(SsidProfile.class, 
							null, 
							new FilterParams("id", ids), 
							ct.getOwner().getId());
				}
			}
			if (ssidProfiles != null
					&& !ssidProfiles.isEmpty()) {
				for (SsidProfile ssidProfile : ssidProfiles) {
					if (ssidProfile == null) {
						continue;
					}
					if (ssidProfile.getUserProfileDefault() != null) {
						upVlanMapping.addAll(getUpVlanMappingCommon(ssidProfile.getUserProfileDefault(), existMapping,
								MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default"), upFilter));
					}
					if (ssidProfile.getUserProfileSelfReg() != null) {
						upVlanMapping.addAll(getUpVlanMappingCommon(ssidProfile.getUserProfileSelfReg(), existMapping,
								MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.reg"), upFilter));
					}
					if (ssidProfile.getUserProfileGuest() != null) {
					    upVlanMapping.addAll(getUpVlanMappingCommon(ssidProfile.getUserProfileGuest(), existMapping,
					            MgrUtil.getUserMessage("glasgow_10.config.v2.select.user.profile.popup.tab.guest"), upFilter));
					}
					if (ssidProfile.getRadiusUserProfile() != null) {
						for (UserProfile upTmp : ssidProfile.getRadiusUserProfile()) {
							upVlanMapping.addAll(getUpVlanMappingCommon(upTmp, existMapping,
									MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.auth"), upFilter));
						}
					}
				}
			}
		}
	}
	private static void prepareUserProfileVlanMappingRelationOnPortAccess(List<UserProfileVlanRelation> upVlanMapping, 
			Map<Long, UserProfileVlanMapping> existMapping, ConfigTemplate ct, Long id, UserProfileFilter upFilter, QueryBo queryBo) {
        if (!ct.getAccessProfiles().isEmpty()) {
                for (PortAccessProfile accessProfile : ct.getAccessProfiles()) {
                    if (null == accessProfile || accessProfile.getId().compareTo(id) != 0) {
                        continue;
                    }
                    if(null != queryBo) {
                        accessProfile = QueryUtil.findBoById(PortAccessProfile.class, accessProfile.getId(),queryBo);
                    }
                    if (null != accessProfile.getDefUserProfile()) {
                        upVlanMapping.addAll(getUpVlanMappingCommon(accessProfile.getDefUserProfile(), existMapping,
                                MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.default") 
                                + (accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA 
                                && !accessProfile.isRadiusAuthEnable() ? " Voice" : ""), 
                                upFilter));
                    }
                    if (null != accessProfile.getSelfRegUserProfile()) {
                        upVlanMapping.addAll(getUpVlanMappingCommon(accessProfile.getSelfRegUserProfile(), existMapping,
                                MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.reg"), upFilter));
                    }
                    if (null != accessProfile.getGuestUserProfile()) {
                        upVlanMapping.addAll(getUpVlanMappingCommon(accessProfile.getGuestUserProfile(), existMapping,
                                MgrUtil.getUserMessage("glasgow_10.config.v2.select.user.profile.popup.tab.guest"), upFilter));
                    }
                    if (!accessProfile.getAuthOkUserProfile().isEmpty()) {
                        for (UserProfile userProfile : accessProfile.getAuthOkUserProfile()) {
                            upVlanMapping.addAll(getUpVlanMappingCommon(userProfile, existMapping,
                                    accessProfile.getProduct() == PortAccessProfile.CHESAPEAKE ? 
                                            MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")
                                            + (accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA ? " Voice" : "") :
                                                MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.auth")
                                    , upFilter));
                        }
                    }
                    if (!accessProfile.getAuthFailUserProfile().isEmpty()) {
                        for (UserProfile userProfile : accessProfile.getAuthFailUserProfile()) {
                            upVlanMapping.addAll(getUpVlanMappingCommon(userProfile, existMapping,
                                    MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authfail"), upFilter));
                        }
                    }
                    // for Phone&Data type
                    if (!accessProfile.getAuthOkDataUserProfile().isEmpty()) {
                        for (UserProfile userProfile : accessProfile.getAuthOkDataUserProfile()) {
                            upVlanMapping.addAll(getUpVlanMappingCommon(userProfile, existMapping,
                                    MgrUtil.getUserMessage("config.v2.select.user.profile.popup.tab.authok")+" Data", upFilter));
                        }
                    }
                }
        }
    }
	private static List<UserProfileVlanRelation> getUpVlanMappingCommon(UserProfile userProfile, 
			Map<Long, UserProfileVlanMapping> existMapping, String postfix, UserProfileFilter upFilter) {
		if (existMapping.containsKey(userProfile.getId())) {
			return getUpVlanMappingFromUserProfile(existMapping.get(userProfile.getId()), existMapping, postfix, upFilter);
		} else {
			return getUpVlanMappingFromUserProfile(userProfile, existMapping, postfix, upFilter);
		}
	}
	private static List<UserProfileVlanRelation> getUpVlanMappingFromUserProfile(UserProfileVlanMapping upVlanMapping, 
			Map<Long, UserProfileVlanMapping> existMapping, String postfix, UserProfileFilter upFilter) {
		return getUpVlanMappingFromUserProfile(upVlanMapping.getUserProfile(), upVlanMapping.getVlan(), existMapping, postfix, upFilter);
	}
	private static List<UserProfileVlanRelation> getUpVlanMappingFromUserProfile(UserProfile up, 
			Map<Long, UserProfileVlanMapping> existMapping, String postfix, UserProfileFilter upFilter) {
		return getUpVlanMappingFromUserProfile(up, up.getVlan(), existMapping, postfix, upFilter);
	}
	private static List<UserProfileVlanRelation> getUpVlanMappingFromUserProfile(UserProfile up, Vlan vlan, 
			Map<Long, UserProfileVlanMapping> existMapping, String postfix, UserProfileFilter upFilter) {
		List<UserProfileVlanRelation> upVlans = new ArrayList<>();
		String upName = up.getUserProfileName();
		if (StringUtils.isNotBlank(postfix)) {
			upName += " (" + postfix + ")";
		}
		if (upFilter == null
				|| upFilter.filter(up, up, false)) {
			upVlans.add(new UserProfileVlanRelation(upName, up.getId(), vlan.getVlanName(), vlan.getId()).setBlnUpDefault(up.isDefaultFlag()));
		}
		
		UserProfile upTmp;
		Vlan vlanTmp;
		if (up.isEnableAssign()
				&& up.getAssignRules() != null) {
			for (DevicePolicyRule rule : up.getAssignRules()) {
				if (existMapping.containsKey(rule.getUserProfileId())) {
					upTmp = existMapping.get(rule.getUserProfileId()).getUserProfile();
					vlanTmp = existMapping.get(rule.getUserProfileId()).getVlan();
				} else {
					upTmp = getUserProfileAndVlan(rule.getUserProfileId());
					vlanTmp = upTmp.getVlan();
				}
				if (upFilter == null
						|| upFilter.filter(upTmp, up, true)) {
					upVlans.add(new UserProfileVlanRelation(upTmp.getUserProfileName(), 
						upTmp.getId(), vlanTmp.getVlanName(), vlanTmp.getId(), UserProfileVlanRelation.UP_TYPE_REASSIGN).setBlnUpDefault(upTmp.isDefaultFlag()));
				}
			}
		}
		
		return upVlans;
	}
	private static UserProfile getUserProfileAndVlan(Long upId) {
		return QueryUtil.findBoById(UserProfile.class, upId, new QueryCertainBo<UserProfile>() {
			@Override
			public Collection<HmBo> loadBo(UserProfile up) {
					if (up.getVlan() != null) {
						up.getVlan().getId();
					}
				return null;
			}
		});
	}
	private static List<UserProfile> getUserProfileAndVlanByIds(Set<Long> upIds, Long domainId) {
		return QueryUtil.executeQuery(UserProfile.class, null, new FilterParams("id", upIds), domainId, new QueryCertainBo<UserProfile>() {
			@Override
			public Collection<HmBo> loadBo(UserProfile up) {
				if (up.getVlan() != null) {
					up.getVlan().getId();
				}
				if (up.getAssignRules() != null) {
					up.getAssignRules().size();
				}
				return null;
			}
		});
	}
	private static List<UserProfile> getUserProfileByIds(Set<Long> upIds, Long domainId) {
		return QueryUtil.executeQuery(UserProfile.class, null, new FilterParams("id", upIds), domainId);
	}
	
	private static interface UserProfileFilter {
		public boolean filter(UserProfile userProfile, UserProfile oriUserProfile, boolean blnReAssigned);
	}

	private static Map<Long, UserProfile> getUpVlanMappedUserProfileIds(ConfigTemplate ct) {
		return getAllUserProfiles(ct, UserProfileVlanMapping.MAPPING_TYPE_ALL, null, true, null);
	}
	private static <T extends HmBo> Map<Long, UserProfile> getUpVlanMappedUserProfileIds(ConfigTemplate ct, T relativeBo) {
		return getAllUserProfiles(ct, whatMappingIsFor(relativeBo), relativeBo, true, null);
	}
	public static <T extends HmBo> Map<Long, UserProfile> getAllUserProfiles(ConfigTemplate ct, String type, T relativeBo,
			boolean blnWithReAssigned, UserProfileFilter upFilter) {
		return getAllUserProfiles(ct, type, relativeBo, blnWithReAssigned, upFilter, false, false, -1);
	}
	public static <T extends HmBo> Map<Long, UserProfile> getAllUserProfiles(ConfigTemplate ct, String type, T relativeBo,
			boolean blnWithReAssigned, UserProfileFilter upFilter, boolean blnQueryBo, boolean routingOnly, int hiveApModel) {
		Map<Long, UserProfile> upContainer = new HashMap<>();
		boolean blnMappingForAll = isMappingForAll(type);
		
		if (isMappingForSsidProfile(type)) {
			SsidProfile profile = null;
			if (relativeBo != null) {
				profile = (SsidProfile)relativeBo;
			}
			prepareAllUserProfilesOfSsid(upContainer, ct, blnWithReAssigned, 
					upFilter, profile, blnQueryBo);
		} else if (isMappingForPortAccessProfile(type)) {
			PortAccessProfile profile = null;
			if (relativeBo != null) {
				profile = (PortAccessProfile)relativeBo;
			}
			prepareAllUserProfilesOfPortAccess(upContainer, ct, blnWithReAssigned, 
					upFilter, profile, blnQueryBo, routingOnly, hiveApModel);
		} else if(blnMappingForAll) {
			// no ssid device did not do ssid mapping
			if (wirelessIncludeByDevice(hiveApModel)){
			    prepareAllUserProfilesOfSsid(upContainer, ct, blnWithReAssigned, 
			            upFilter, null, blnQueryBo);
			}
		    prepareAllUserProfilesOfPortAccess(upContainer, ct, blnWithReAssigned, 
		            upFilter, null, blnQueryBo, routingOnly,hiveApModel);
		    
		}
		
		return upContainer;
	}
	
	public static boolean wirelessIncludeByDevice(int hiveApModel){
		if (hiveApModel==HiveAp.HIVEAP_MODEL_SR24
				 || hiveApModel==HiveAp.HIVEAP_MODEL_SR48
				 || hiveApModel==HiveAp.HIVEAP_MODEL_SR2124P
				 || hiveApModel==HiveAp.HIVEAP_MODEL_SR2024P
				 || hiveApModel==HiveAp.HIVEAP_MODEL_SR2148P
				 || hiveApModel==HiveAp.HIVEAP_MODEL_BR200
				 || HiveAp.isCVGAppliance((short)hiveApModel)) {
			return false;
		}
		return true;
	}
	
	private static void prepareAllUserProfilesOfSsid(Map<Long, UserProfile> upContainer, ConfigTemplate ct, 
			boolean blnWithReAssigned, UserProfileFilter upFilter, SsidProfile profile, boolean blnQueryBo) {
		List<SsidProfile> ssidProfiles = null;
		QueryBo ssidQueryBo = new QueryCertainBo<SsidProfile>() {
				@Override
				public Collection<HmBo> loadBo(SsidProfile ssidProfile) {
					loadSingleUserProfile(ssidProfile.getUserProfileDefault());
					loadSingleUserProfile(ssidProfile.getUserProfileSelfReg());
					loadSingleUserProfile(ssidProfile.getUserProfileGuest());
					if (ssidProfile.getRadiusUserProfile() != null) {
						ssidProfile.getRadiusUserProfile().size();
						for (UserProfile userProfile : ssidProfile.getRadiusUserProfile()) {
							loadSingleUserProfile(userProfile);
						}
					}
					return null;
				}
			};
		if (profile == null) {
			if (ct.getSsidInterfaces() != null) {
				List<Long> ids = new ArrayList<>();
				for (ConfigTemplateSsid ctSsid : ct.getSsidInterfaces().values()) {
					if (ctSsid.getSsidProfile() == null) {
						continue;
					}
					ids.add(ctSsid.getSsidProfile().getId());
				}
				if (!ids.isEmpty()) {
					ssidProfiles = QueryUtil.executeQuery(SsidProfile.class, 
							null, 
							new FilterParams("id", ids), 
							ct.getOwner().getId(),
							ssidQueryBo);
				}
			}
		} else {
			ssidProfiles = new ArrayList<>();
			ssidProfiles.add(QueryUtil.findBoById(SsidProfile.class, profile.getId(), ssidQueryBo));
		}
		if (ssidProfiles != null
				&& !ssidProfiles.isEmpty()) {
			for (SsidProfile ssidProfile : ssidProfiles) {
				if (ssidProfile == null) {
					continue;
				}
				if (ssidProfile.getUserProfileDefault() != null) {
					getUserProfilesOfUserProfile(upContainer, 
							ssidProfile.getUserProfileDefault(), 
							blnWithReAssigned, 
							ct.getOwner().getId(), 
							upFilter,
							blnQueryBo);
				}
				if (ssidProfile.getUserProfileSelfReg() != null) {
					getUserProfilesOfUserProfile(upContainer, 
							ssidProfile.getUserProfileSelfReg(), 
							blnWithReAssigned, 
							ct.getOwner().getId(), 
							upFilter,
							blnQueryBo);
				}
				if (ssidProfile.getUserProfileGuest() != null) {
				    getUserProfilesOfUserProfile(upContainer, 
				            ssidProfile.getUserProfileGuest(), 
				            blnWithReAssigned, 
				            ct.getOwner().getId(), 
				            upFilter,
				            blnQueryBo);
				}
				if (ssidProfile.getRadiusUserProfile() != null) {
					for (UserProfile upTmp : ssidProfile.getRadiusUserProfile()) {
						getUserProfilesOfUserProfile(upContainer, 
								upTmp, 
								blnWithReAssigned, 
								ct.getOwner().getId(), 
								upFilter,
								blnQueryBo);
					}
				}
			}
		}
	}

    private static void prepareAllUserProfilesOfPortAccess(
            Map<Long, UserProfile> upContainer, ConfigTemplate ct,
            boolean blnWithReAssigned, UserProfileFilter upFilter,
            PortAccessProfile accprofile, boolean blnQueryBo, boolean routingOnly, int hiveApModel) {
        QueryBo accessQueryBo = new QueryCertainBo<PortAccessProfile>() {
            @Override
            public Collection<HmBo> loadBo(PortAccessProfile accessProfile) {
                loadSingleUserProfile(accessProfile.getDefUserProfile());
                loadSingleUserProfile(accessProfile.getSelfRegUserProfile());
                loadSingleUserProfile(accessProfile.getGuestUserProfile());
                if (!accessProfile.getAuthOkUserProfile().isEmpty()) {
                    accessProfile.getAuthOkUserProfile().size();
                    for (UserProfile userProfile : accessProfile
                            .getAuthOkUserProfile()) {
                        loadSingleUserProfile(userProfile);
                    }
                }
                if (!accessProfile.getAuthFailUserProfile().isEmpty()) {
                    accessProfile.getAuthFailUserProfile().size();
                    for (UserProfile userProfile : accessProfile
                            .getAuthFailUserProfile()) {
                        loadSingleUserProfile(userProfile);
                    }
                }
                // for Phone&Data
                if (!accessProfile.getAuthOkDataUserProfile().isEmpty()) {
                    accessProfile.getAuthOkDataUserProfile().size();
                    for (UserProfile userProfile : accessProfile
                            .getAuthOkDataUserProfile()) {
                        loadSingleUserProfile(userProfile);
                    }
                }
                return null;
            }
        };
        List<PortAccessProfile> profiles = new ArrayList<>();
        PortAccessProfile portAccessProfile;
        if (accprofile == null) {
            if (!ct.getPortProfiles().isEmpty()) {
            	//==============================================================================
            	List<PortGroupProfile> groupProfileList =  new ArrayList<PortGroupProfile>();
            	for (PortGroupProfile profile : ct.getPortProfiles()) {
            		groupProfileList.add(profile);
        			for (SingleTableItem item : profile.getItems()) {
        				if(null != item.getNonDefault()){
        					groupProfileList.add(item.getNonDefault());
        				}
        			}
            	}
            	for (PortGroupProfile groupProfile : groupProfileList) {
                //for (PortGroupProfile groupProfile : ct.getPortProfiles()) {
            	//===============================================================================
                	if (routingOnly) {
                		if (groupProfile.getDeviceType()!=HiveAp.Device_TYPE_BRANCH_ROUTER) {
                			continue;
                		}
	                	if (hiveApModel!=-1) {
	                		String[] models = groupProfile.getDeviceModelStrs();
	                		if (models==null) {
	                			continue;
	                		}
	                		boolean continueOp = true;
	                		for(String key: models){
	                			if (key.equals(String.valueOf(hiveApModel))){
	                				continueOp=false;
	                			}
	                		}
	                		if (continueOp) {
	                			continue;
	                		}
	                	}
                	}
                	
                    for (PortBasicProfile basic : groupProfile.getBasicProfiles()) {
                        portAccessProfile = QueryUtil.findBoById(
                                PortAccessProfile.class, basic
                                        .getAccessProfile().getId(),
                                accessQueryBo);
                        profiles.add(portAccessProfile);
                    }
                }
            }
        } else {
            portAccessProfile = QueryUtil.findBoById(PortAccessProfile.class,
                    accprofile.getId(), accessQueryBo);
            profiles.add(portAccessProfile);
        }
        for (PortAccessProfile profile : profiles) {
            if (null == profile) {
                continue;
            }
            if (profile.getDefUserProfile() != null) {
                getUserProfilesOfUserProfile(upContainer,
                        profile.getDefUserProfile(), blnWithReAssigned, ct
                                .getOwner().getId(), upFilter, blnQueryBo);
            }
            if (profile.getSelfRegUserProfile() != null) {
                getUserProfilesOfUserProfile(upContainer,
                        profile.getSelfRegUserProfile(), blnWithReAssigned, ct
                        .getOwner().getId(), upFilter, blnQueryBo);
            }
            if (profile.getGuestUserProfile() != null) {
                getUserProfilesOfUserProfile(upContainer,
                        profile.getGuestUserProfile(), blnWithReAssigned, ct
                        .getOwner().getId(), upFilter, blnQueryBo);
            }
            if (!profile.getAuthOkUserProfile().isEmpty()) {
                for (UserProfile upTmp : profile.getAuthOkUserProfile()) {
                    getUserProfilesOfUserProfile(upContainer, upTmp,
                            blnWithReAssigned, ct.getOwner().getId(), upFilter,
                            blnQueryBo);
                }
            }
            if (!profile.getAuthFailUserProfile().isEmpty()) {
                for (UserProfile upTmp : profile.getAuthFailUserProfile()) {
                    getUserProfilesOfUserProfile(upContainer, upTmp,
                            blnWithReAssigned, ct.getOwner().getId(), upFilter,
                            blnQueryBo);
                }
            }
            // for Phone&Data type
            if (!profile.getAuthOkDataUserProfile().isEmpty()) {
                for (UserProfile upTmp : profile.getAuthOkDataUserProfile()) {
                    getUserProfilesOfUserProfile(upContainer, upTmp,
                            blnWithReAssigned, ct.getOwner().getId(), upFilter,
                            blnQueryBo);
                }
            }
        }
    }
	
	private static void getUserProfilesOfUserProfile(Map<Long, UserProfile> upContainer, UserProfile userProfile, 
			boolean blnWithReAssigned, Long domainId, UserProfileFilter upFilter, boolean blnQueryBo) {
		if (upFilter == null
				|| upFilter.filter(userProfile, userProfile, false)) {
			upContainer.put(userProfile.getId(), userProfile);
		}
		if (userProfile.isEnableAssign()
				&& blnWithReAssigned) {
			Set<Long> upIds = new HashSet<>();
			for (DevicePolicyRule rule : userProfile.getAssignRules()) {
				upIds.add(rule.getUserProfileId());
			}
			if (upIds == null
					|| upIds.isEmpty()) {
				return;
			}
			List<UserProfile> ups;
			if (blnQueryBo) {
				ups = getUserProfileAndVlanByIds(upIds, domainId);
			} else {
				ups = getUserProfileByIds(upIds, domainId);
			}
			if (ups != null
					&& !ups.isEmpty()) {
				for (UserProfile up : ups) {
					if (upFilter == null
							|| upFilter.filter(up, userProfile, true)) {
						upContainer.put(up.getId(), up);
					}
				}
			}
		}
	}
	private static void loadSingleUserProfile(UserProfile userProfile) {
		if (userProfile != null) {
			userProfile.getId();
			if (userProfile.getVlan() != null) {
				userProfile.getVlan().getId();
			}
			if (userProfile.getAssignRules() != null) {
				userProfile.getAssignRules().size();
			}
		}
	}
	
	private static <T extends HmBo> String whatMappingIsFor(T relativeBo) {
		if (relativeBo instanceof SsidProfile) {
			return UserProfileVlanMapping.MAPPING_TYPE_SSID;
		} else if (relativeBo instanceof PortAccessProfile) {
			return UserProfileVlanMapping.MAPPING_TYPE_PORT_ACCESS;
		}
		return null;
	}
	private static <T extends HmBo> boolean isMappingForSsidProfile(T relativeBo) {
		return isMappingForSsidProfile(whatMappingIsFor(relativeBo));
	}
	private static boolean isMappingForSsidProfile(String type) {
		return UserProfileVlanMapping.MAPPING_TYPE_SSID.equals(type);
	}
	private static <T extends HmBo> boolean isMappingForPortAccessProfile(T relativeBo) {
		return isMappingForPortAccessProfile(whatMappingIsFor(relativeBo));
	}
	private static boolean isMappingForPortAccessProfile(String type) {
		return UserProfileVlanMapping.MAPPING_TYPE_PORT_ACCESS.equals(type);
	}
	private static boolean isMappingForAll(String type) {
		return UserProfileVlanMapping.MAPPING_TYPE_ALL.equals(type);
	}
	
	public static class UserProfileVlanRelation {
		private String upName;
    	private Long upId;
    	private String vlanName;
    	private Long vlanId;
    	private String upType;
    	private boolean blnUpDefault;
    	
    	public static final String UP_TYPE_NORMAL = "normal";
    	public static final String UP_TYPE_REASSIGN = "reassign";
    	
    	public UserProfileVlanRelation(String upName, Long upId, String vlanName, Long vlanId) {
    		this(upName, upId, vlanName, vlanId, UP_TYPE_NORMAL);
    	}
    	public UserProfileVlanRelation(String upName, Long upId, String vlanName, Long vlanId, String upType) {
    		this.upName = upName;
    		this.upId = upId;
    		this.vlanName = vlanName;
    		this.vlanId = vlanId;
    		this.upType = upType;
    	}
    	public boolean isBlnUpDefault() {
			return blnUpDefault;
		}
		public UserProfileVlanRelation setBlnUpDefault(boolean blnUpDefault) {
			this.blnUpDefault = blnUpDefault;
			return this;
		}
		public String getUpName() {
			return upName;
		}
		public void setUpName(String upName) {
			this.upName = upName;
		}
		public Long getUpId() {
			return upId;
		}
		public void setUpId(Long upId) {
			this.upId = upId;
		}
		public String getVlanName() {
			return vlanName;
		}
		public void setVlanName(String vlanName) {
			this.vlanName = vlanName;
		}
		public Long getVlanId() {
			return vlanId;
		}
		public void setVlanId(Long vlanId) {
			this.vlanId = vlanId;
		}
		public String getUpType() {
			return upType;
		}
		public void setUpType(String upType) {
			this.upType = upType;
		}
    }
}
