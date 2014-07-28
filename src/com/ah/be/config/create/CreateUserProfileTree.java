package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.UserProfileInt;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateUserProfileTree {
	
	private UserProfileInt userProfileImp;
	private UserProfileObj userProfileObj;
	
	private int index;
	private int allSize;
	private String beforeUPName;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> userProfileChildList_1 = new ArrayList<Object>();
	private List<Object> userProfileChildList_2 = new ArrayList<Object>();

	public CreateUserProfileTree(UserProfileInt userProfileImp, int index, int allSize, String beforeUPName, GenerateXMLDebug oDebug){
		this.userProfileImp = userProfileImp;
		this.oDebug = oDebug;
		this.index = index;
		this.allSize = allSize;
		this.beforeUPName = beforeUPName;
	}
	
	public void generate() throws Exception{
		
		if(userProfileImp.isConfigUserProfile()){
			userProfileObj = new UserProfileObj();
			generateUserProfileLevel_1();
		}
	}
		
	public UserProfileObj getUserProfileObj(){
		return this.userProfileObj;
	}
	
	private void generateUserProfileLevel_1() throws Exception {
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"user-profile", GenerateXMLDebug.SET_NAME,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		userProfileObj.setName( userProfileImp.getUserProfileName() );
		
		/** attribute: updateTime */
		userProfileObj.setUpdateTime( userProfileImp.getUpdateTime() );
		
		/** attribute: operation */
		userProfileObj.setOperation( CLICommonFunc.getAhEnumActValue( CLICommonFunc.getYesDefault() ) );
		
		/** element: <cr> */
//		if(userProfileImp.isConfigCr()){
		userProfileObj.setCr("");
//		}
		
		/** element: <user-profile>.<group-id> */
//		if(userProfileImp.isConfigGroupId()){
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"group-id", GenerateXMLDebug.SET_VALUE,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		Object[][] groupIdParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, userProfileImp.getUserProfileGroupId()}
		};
		userProfileObj.setGroupId(
				(UserProfileObj.GroupId)CLICommonFunc.createObjectWithName(UserProfileObj.GroupId.class, groupIdParm)
		);
//		}
		
		/** element: <user-profile>.<qos-policy> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"qos-policy", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureQosPolicy()){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"qos-policy", GenerateXMLDebug.SET_NAME,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			Object[][] qosPolicyParm = {
					{CLICommonFunc.ATTRIBUTE_NAME, userProfileImp.getQosPolicyName()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userProfileObj.setQosPolicy(
					(UserProfileObj.QosPolicy)CLICommonFunc.createObjectWithName(UserProfileObj.QosPolicy.class, qosPolicyParm)
			);
		}
		
		/** element: <user-profile>.<vlan-id> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"vlan-id", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureVlan()){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"vlan-id", GenerateXMLDebug.SET_VALUE,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			Object[][] vlanIdParam = {
					{CLICommonFunc.ATTRIBUTE_VALUE, userProfileImp.getVlanId()}
			};
			userProfileObj.setVlanId(
					(UserProfileObj.VlanId)CLICommonFunc.createObjectWithName(UserProfileObj.VlanId.class, vlanIdParam)
			);
		}
		
		/** element: <user-profile>.<mobility-policy> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"mobility-policy", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureMobilityPolicy()){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"mobility-policy", GenerateXMLDebug.SET_NAME,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			userProfileObj.setMobilityPolicy(
					CLICommonFunc.createAhNameActObj(userProfileImp.getMobilityPolicyName(), CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <user-profile>.<attribute> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"attribute", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		for(int i=0; i<userProfileImp.getUserProfileAttributeSize(); i++){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"attribute", GenerateXMLDebug.SET_NAME,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			userProfileObj.getAttribute().add(
					CLICommonFunc.createAhNameActValueQuoteProhibited(userProfileImp.getUserProfileAttributeNextName(i), 
							CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <user-profile>.<schedule> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"schedule", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		for(int i=0; i<userProfileImp.getUserProfileScheduleSize(); i++){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"schedule", GenerateXMLDebug.SET_NAME,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			userProfileObj.getSchedule().add(
					CLICommonFunc.createAhNameActValue(userProfileImp.getUserProfileScheduleName(), 
							CLICommonFunc.getYesDefault())
					
			);
		}
		
		/** element: <user-profile>.<deny-action-for-schedule>**/
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"deny-action-for-schedule", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		userProfileObj.setDenyActionForSchedule(CLICommonFunc.createAhStringActObj(userProfileImp.getUserProfileScheduleDenyMode(), CLICommonFunc.getYesDefault()));
		
		/** element: <user-profile>.<ip-policy-default-action> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"ip-policy-default-action", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureIpPolicy()){
			UserProfileObj.IpPolicyDefaultAction ipActionObj = new UserProfileObj.IpPolicyDefaultAction();
			userProfileChildList_1.add(ipActionObj);
			userProfileObj.setIpPolicyDefaultAction(ipActionObj);
		}
		
		/** element: <user-profile>.<mac-policy-default-action> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"mac-policy-default-action", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureMacPolicy()){
			UserProfileObj.MacPolicyDefaultAction macActionObj = new UserProfileObj.MacPolicyDefaultAction();
			userProfileChildList_1.add(macActionObj);
			userProfileObj.setMacPolicyDefaultAction(macActionObj);
		}
		
		/** element: <user-profile>.<security> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"security", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigureIpOrMacPolicy()){
			UserProfileObj.Security securityObj = new UserProfileObj.Security();
			userProfileChildList_1.add(securityObj);
			userProfileObj.setSecurity(securityObj);
		}
		
		/** element: <cac> */
//		if(userProfileImp.isConfigCac()){
		UserProfileObj.Cac cacObj = new UserProfileObj.Cac();
		userProfileChildList_1.add(cacObj);
		userProfileObj.setCac(cacObj);
//		}
		
		/** element: <tunnel-policy> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"tunnel-policy", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigTunnelPolicy()){
			
			oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
					"tunnel-policy", GenerateXMLDebug.SET_NAME,
					userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
			userProfileObj.setTunnelPolicy(CLICommonFunc.createAhNameActObj(userProfileImp.getTunnelPolicyName(), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <user-profile>.<airscreen> */
		oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
				"airscreen", GenerateXMLDebug.CONFIG_ELEMENT,
				userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
		if(userProfileImp.isConfigAirScreen()){
			UserProfileObj.Airscreen airScreenObj = new UserProfileObj.Airscreen();
			userProfileChildList_1.add(airScreenObj);
			userProfileObj.setAirscreen(airScreenObj);
		}
		
		/** element: <user-profile>.<performance-sentinel> */
		UserProfileObj.PerformanceSentinel performanceObj = new UserProfileObj.PerformanceSentinel();
		userProfileChildList_1.add(performanceObj);
		userProfileObj.setPerformanceSentinel(performanceObj);
		
		/** element: <user-profile>.<before> */
		if(userProfileImp.isConfigBefore(index)){
			UserProfileSeq beforeUP = new UserProfileSeq();
			userProfileChildList_1.add(beforeUP);
			userProfileObj.setBefore(beforeUP);
		}
		
		/** element: <user-profile>.<l3-tunnel-action> */
		if(userProfileImp.isConfigL3TunnelAction()){
			UpL3TunnelAction l3Action = new UpL3TunnelAction();
			userProfileChildList_1.add(l3Action);
			userProfileObj.setL3TunnelAction(l3Action);
		}
		
		/** element: <user-profile>.<qos-marker-map> */
		if(userProfileImp.isConfigQosMap()){
			UserProfileQosMarkerMap markMap = new UserProfileQosMarkerMap();
			userProfileChildList_1.add(markMap);
			userProfileObj.setQosMarkerMap(markMap);
		}
		
		generateUserProfileLevel_2();
	}
	
	private void generateUserProfileLevel_2() throws Exception {
		/**
		 * <user-profile>.<security>							UserProfileObj.Security
		 * <user-profile>.<ip-policy-default-action>			UserProfileObj.IpPolicyDefaultAction
		 * <user-profile>.<mac-policy-default-action>			UserProfileObj.MacPolicyDefaultAction
		 * <user-profile>.<cac>									UserProfileObj.Cac
		 * <user-profile>.<airscreen>							UserProfileObj.Airscreen
		 * <user-profile>.<performance-sentinel>				UserProfileObj.PerformanceSentinel
		 * <user-profile>.<before>								UserProfileSeq
		 * <user-profile>.<l3-tunnel-action>					UpL3TunnelAction
		 * <user-profile>.<qos-marker-map>						UserProfileQosMarkerMap
		 */
		for(Object childObj : userProfileChildList_1){
			
			/** element: <user-profile>.<security> */
			if(childObj instanceof UserProfileObj.Security){
				UserProfileObj.Security security = (UserProfileObj.Security)childObj;
				
				/** element: <user-profile>.<security>.<ip-policy> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/security",
						"ip-policy", GenerateXMLDebug.CONFIG_ELEMENT,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				if(userProfileImp.isConfigureIpPolicy()){
					security.setIpPolicy(
							createUserSecurityPolicy(userProfileImp.getIpFromAirPolicyName(), userProfileImp.getIpToAirPolicyName())
					);
				}
				
				/** element: <user-profile>.<security>.<mac-policy> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/security",
						"mac-policy", GenerateXMLDebug.CONFIG_ELEMENT,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				if(userProfileImp.isConfigureMacPolicy()){
					security.setMacPolicy(
							createUserSecurityPolicy(userProfileImp.getMacFromAirPolicyName(), userProfileImp.getMacToAirPolicyName())
					);
				}
			}
			
			/** element: <user-profile>.<ip-policy-default-action> */
			if(childObj instanceof UserProfileObj.IpPolicyDefaultAction){
				UserProfileObj.IpPolicyDefaultAction ipActionObj = (UserProfileObj.IpPolicyDefaultAction)childObj;
				
				/** attribute: operation */
				ipActionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
						"ip-policy-default-action", GenerateXMLDebug.SET_VALUE,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				ipActionObj.setValue(userProfileImp.getIpPolicyActionType());
			}
			
			/** element: <user-profile>.<mac-policy-default-action> */
			if(childObj instanceof UserProfileObj.MacPolicyDefaultAction){
				UserProfileObj.MacPolicyDefaultAction macActionObj = (UserProfileObj.MacPolicyDefaultAction)childObj;
				
				/** attribute: operation */
				macActionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']",
						"mac-policy-default-action", GenerateXMLDebug.SET_VALUE,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				macActionObj.setValue(userProfileImp.getMacPolicyActionType());
			}
			
			/** element: <user-profile>.<cac> */
			if(childObj instanceof UserProfileObj.Cac){
				UserProfileObj.Cac cacObj = (UserProfileObj.Cac)childObj;
				
				/** element: <user-profile>.<cac>.<airtime-percentage> */
				UserProfileObj.Cac.AirtimePercentage airTimePercentageObj = new UserProfileObj.Cac.AirtimePercentage();
				userProfileChildList_2.add(airTimePercentageObj);
				cacObj.setAirtimePercentage(airTimePercentageObj);
//				Object[][] airTimeParm = {
//						{CLICommonFunc.ATTRIBUTE_VALUE, userProfileImp.getAirTime()},
//						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
//				};
//				cacObj.setAirtimePercentage(
//						(UserProfileObj.Cac.GuaranteedAirtimePercentage)CLICommonFunc.createObjectWithName(
//								UserProfileObj.Cac.GuaranteedAirtimePercentage.class, airTimeParm)
//				);
			}
			
			/** element: <user-profile>.<airscreen> */
			if(childObj instanceof UserProfileObj.Airscreen){
				UserProfileObj.Airscreen airscreenObj = (UserProfileObj.Airscreen)childObj;
				
				/** element: <user-profile>.<airscreen>.<rule> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/airscreen",
						"rule", GenerateXMLDebug.SET_NAME,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				for(int i=0; i<userProfileImp.getAirScreenSize(); i++){
					airscreenObj.getRule().add(
							CLICommonFunc.createAhNameActObj(userProfileImp.getAirScreenRuleName(i), CLICommonFunc.getYesDefault())
					);
				}
			}
			
			/** element: <user-profile>.<performance-sentinel> */
			if(childObj instanceof UserProfileObj.PerformanceSentinel){
				UserProfileObj.PerformanceSentinel bandwidthObj = (UserProfileObj.PerformanceSentinel)childObj;
				
				/** element: <user-profile>.<performance-sentinel>.<enable> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/performance-sentinel",
						"enable", GenerateXMLDebug.SET_OPERATION,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				bandwidthObj.setEnable(CLICommonFunc.getAhOnlyAct(userProfileImp.isBandwidthEnable()));
				
				/** element: <user-profile>.<performance-sentinel>.<guaranteed-bandwidth> */
				if(userProfileImp.isBandwidthEnable()){
					
					oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/performance-sentinel",
							"guaranteed-bandwidth", GenerateXMLDebug.SET_VALUE,
							userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
					Object[][] bandwidthParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, userProfileImp.getBandwidthValue()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bandwidthObj.setGuaranteedBandwidth(
							(UserProfileObj.PerformanceSentinel.GuaranteedBandwidth)CLICommonFunc.createObjectWithName(UserProfileObj.PerformanceSentinel.GuaranteedBandwidth.class, bandwidthParm)
					);
				}
				
				/** element: <user-profile>.<performance-sentinel>.<action> */
				if(userProfileImp.isBandwidthEnable()){
					PerformanceSentinelAction actionObj = new PerformanceSentinelAction();
					userProfileChildList_2.add(actionObj);
					bandwidthObj.setAction(actionObj);
				}
				
			}
			
			/** element: <user-profile>.<before> */
			if(childObj instanceof UserProfileSeq){
				UserProfileSeq beforeUP = (UserProfileSeq)childObj;
				
				/** attribute: operation */
				beforeUP.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: quoteProhibited */
				beforeUP.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				beforeUP.setValue(userProfileImp.getBeforeValue(index, allSize));
				
				/** element: <user-profile>.<before>.<cr> */
				beforeUP.setCr(CLICommonFunc.createAhStringObj(beforeUPName));
			}
			
			/** element: <user-profile>.<l3-tunnel-action> */
			if(childObj instanceof UpL3TunnelAction){
				
				UpL3TunnelAction l3ActionObj = (UpL3TunnelAction)childObj;
				
				/** attribute: operation */
				l3ActionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				l3ActionObj.setValue(userProfileImp.getL3TunnelAction());
			}
			
			/** element: <user-profile>.<qos-marker-map> */
			if(childObj instanceof UserProfileQosMarkerMap){
				UserProfileQosMarkerMap markerMap = (UserProfileQosMarkerMap)childObj;
				
				/** element: <user-profile>.<qos-marker-map>.<8021p> */
				if(userProfileImp.isConfigQosMap8021p()){
					markerMap.set8021P(CLICommonFunc.createAhNameActObj(
							userProfileImp.getQosMapName(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <user-profile>.<qos-marker-map>.<diffserv> */
				if(userProfileImp.isConfigQosMapDiffserv()){
					markerMap.setDiffserv(CLICommonFunc.createAhNameActObj(
							userProfileImp.getQosMapName(), CLICommonFunc.getYesDefault()));
				}
			}
		}
		userProfileChildList_1.clear();
		generateUserProfileLevel_3();
	}
	
	private void generateUserProfileLevel_3(){
		/**
		 * <user-profile>.<cac>.<airtime-percentage>			UserProfileObj.Cac.AirtimePercentage
		 * <user-profile>.<performance-sentinel>.<action>		PerformanceSentinelAction
		 */
		for(Object childObj : userProfileChildList_2){
			
			/** element: <user-profile>.<cac>.<airtime-percentage> */
			if(childObj instanceof UserProfileObj.Cac.AirtimePercentage){
				UserProfileObj.Cac.AirtimePercentage airtimePercentageObj = (UserProfileObj.Cac.AirtimePercentage)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/cac",
						"airtime-percentage", GenerateXMLDebug.SET_VALUE,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				airtimePercentageObj.setValue(userProfileImp.getAirTime());
				
				/** attribute: operation */
				airtimePercentageObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <user-profile>.<cac>.<airtime-percentage>.<share-time> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/cac/airtime-percentage",
						"share-time", GenerateXMLDebug.CONFIG_ELEMENT,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				if(userProfileImp.isConfigCacSharaTime()){
					airtimePercentageObj.setShareTime("");
				}
			}
			
			/** element: <user-profile>.<performance-sentinel>.<action> */
			if(childObj instanceof PerformanceSentinelAction){
				PerformanceSentinelAction actionObj = (PerformanceSentinelAction)childObj;
				
				/** attribute: operation */
				actionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <user-profile>.<performance-sentinel>.<action>.<log> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/performance-sentinel/action",
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				actionObj.setLog(CLICommonFunc.getAhOnlyAct(userProfileImp.isConfigActionLog()));
				
				/** element: <user-profile>.<performance-sentinel>.<action>.<boost> */
				oDebug.debug("/configuration/user-profile[@name='"+userProfileImp.getUserProfileName()+"']/performance-sentinel/action",
						"boost", GenerateXMLDebug.CONFIG_ELEMENT,
						userProfileImp.getUserProfileGuiName(), userProfileImp.getUserProfileName());
				actionObj.setBoost(CLICommonFunc.getAhOnlyAct(userProfileImp.isConfigActionBoost()));
			}
		}
		userProfileChildList_2.clear();
	}
	
	private UserSecurityPolicy createUserSecurityPolicy(String fromAirPolicy, String toAirPolicy) throws Exception{
		UserSecurityPolicy securityPolicy = new UserSecurityPolicy();
		
		/** attribute: operation */
		securityPolicy.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <from-air> */
		if(fromAirPolicy != null && !"".equals(fromAirPolicy)){
			securityPolicy.setFromAir(
					CLICommonFunc.createAhStringActObj(fromAirPolicy, CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <to-air> */
		if(toAirPolicy != null && !"".equals(toAirPolicy)){
			securityPolicy.setToAir(
					CLICommonFunc.createAhStringActObj(toAirPolicy, CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <from-access> */
		if(fromAirPolicy != null && !"".equals(fromAirPolicy)){
			securityPolicy.setFromAccess(
					CLICommonFunc.createAhStringActObj(fromAirPolicy, CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <to-access> */
		if(toAirPolicy != null && !"".equals(toAirPolicy)){
			securityPolicy.setToAccess(
					CLICommonFunc.createAhStringActObj(toAirPolicy, CLICommonFunc.getYesDefault())
			);
		}
		
		return securityPolicy;
	}
}
