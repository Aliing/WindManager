package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.UserProfileInt;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Scheduler;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.AhPermitDenyValue;
import com.ah.xml.be.config.UpL3TunnelActionValue;

/**
 * 
 * @author zhang
 * 
 */
public class UserProfileImpl implements UserProfileInt {

	private static final int POLICY_ACTION_PERMIT = 1;
	// private static final int POLICY_ACTION_DENY = 2;
	

	private UserProfile userProfileObj;
	private HiveAp hiveAp;
	private ConfigureProfileFunction sourceFunction;
	private int vlan;
	private UserProfileAttribute userProfileAttribute;
//	private IpPolicy ipPolicyTo, ipPolicyFrom;
	private MacPolicy macPolicyTo, macPolicyFrom;
	private Iterator<Scheduler> userProfileSchedulers;
	private List<AirScreenRule> airScreenRules = null;

	private String[] userProfileAttributes;
	
	public static final int MAX_ATTRIBUTE = 4095;

	public UserProfileImpl(UserProfile userProfileObj, HiveAp hiveAp, ConfigureProfileFunction sourceFunction)
			throws CreateXMLException {
		this.userProfileObj = userProfileObj;
		this.hiveAp = hiveAp;
		this.sourceFunction = sourceFunction;
		vlan = InterfaceBRImpl.getUserProfileVlan(userProfileObj, hiveAp);

		if (userProfileObj.getUserProfileAttribute() != null) {
			userProfileAttribute = userProfileObj.getUserProfileAttribute();
		}

//		ipPolicyFrom = userProfileObj.getIpPolicyFrom();
//		ipPolicyTo = userProfileObj.getIpPolicyTo();
		macPolicyFrom = userProfileObj.getMacPolicyFrom();
		macPolicyTo = userProfileObj.getMacPolicyTo();
		
		//load airscreen rule
		if(userProfileObj.getAsRuleGroup() != null){
			if(userProfileObj.getAsRuleGroup().getRules() != null){
				airScreenRules = new ArrayList<AirScreenRule>(userProfileObj.getAsRuleGroup().getRules());
			}
		}
	}
	
	public String getUserProfileGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.userProfiles");
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public UserProfile getUserProfile() {
		return this.userProfileObj;
	}

	public boolean isConfigUserProfile() {
		return userProfileObj != null
				&& !userProfileObj.isDefaultFlag();
	}

	public String getUserProfileName() {
		return userProfileObj.getUserProfileName();
	}

	public String getUpdateTime() {
//		List<Object> userTimeList = new ArrayList<Object>();
//		userTimeList.add(hiveAp);
//		userTimeList.add(userProfileObj);
//		userTimeList.add(vlan);
//		userTimeList.add(userProfileAttribute);
//		userTimeList.add(ipPolicyTo);
//		userTimeList.add(ipPolicyFrom);
//		userTimeList.add(macPolicyTo);
//		userTimeList.add(macPolicyFrom);
//		if (userProfileObj.getUserProfileSchedulers() != null) {
//			userTimeList.addAll(userProfileObj.getUserProfileSchedulers());
//		}
//		userTimeList.add(userProfileObj.getQosRateControl());
//		if (userProfileObj.getTunnelSetting() != null) {
//			userTimeList.add(userProfileObj.getTunnelSetting());
//			userTimeList.add(userProfileObj.getTunnelSetting().getIpAddress());
//			if (userProfileObj.getTunnelSetting().getIpAddressList() != null) {
//				for (TunnelSettingIPAddress tunnelInfo : userProfileObj
//						.getTunnelSetting().getIpAddressList()) {
//					if (tunnelInfo != null) {
//						userTimeList.add(tunnelInfo.getIpAddress());
//					}
//				}
//			}
//		}
//		return CLICommonFunc.getLastUpdateTime(userTimeList);
		return CLICommonFunc.getLastUpdateTime(null);
	}

//	public boolean isConfigCr() {
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return false;
//		} else if (CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())) {
//			return true;
//		} else {
//			return true;
//		}
//	}

//	public boolean isConfigGroupId() {
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return true;
//		} else if (CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())) {
//			return false;
//		} else {
//			return false;
//		}
//	}

	public int getUserProfileGroupId() {
		return userProfileObj.getAttributeValue();
	}

	public boolean isConfigureQosPolicy() {
		return userProfileObj.getQosRateControl() != null;
	}

	public String getQosPolicyName() {
		return userProfileObj.getQosRateControl().getQosName();
	}

	public boolean isConfigureVlan() {
		return vlan > -1;
	}

	public int getVlanId() throws CreateXMLException {
		return vlan;
	}

	public boolean isConfigureMobilityPolicy() {
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP && userProfileObj.getTunnelSetting() != null;
	}

	public String getMobilityPolicyName() {
		return userProfileObj.getTunnelSetting().getTunnelName();
	}

	public int getUserProfileAttributeSize() throws CreateXMLException {
		if(userProfileAttributes == null){
			String attributes = "";
			attributes = String.valueOf(userProfileObj.getAttributeValue()) + ",";
//			if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//				// null
//			} else if (CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())) {
//				attributes = String.valueOf(userProfileObj.getAttributeValue())
//						+ ",";
//			} else {
//
//			}
			if (userProfileAttribute != null) {
				attributes += CLICommonFunc.getUserProfileAttr(
						userProfileAttribute, hiveAp).getAttributeValue();
			}
			
			
			
			if (!"".equals(attributes)) {
				userProfileAttributes = CLICommonFunc.mergeRange(attributes).split(",");
			}
		}
		
		return userProfileAttributes.length;
	}

	public String getUserProfileAttributeNextName(int i) {
		return userProfileAttributes[i];
	}

	public int getUserProfileScheduleSize() {
		return userProfileObj.getUserProfileSchedulers().size();
	}

	public String getUserProfileScheduleName() {
		if (userProfileSchedulers == null) {
			userProfileSchedulers = userProfileObj.getUserProfileSchedulers()
					.iterator();
		}

		if (userProfileSchedulers.hasNext()) {
			return userProfileSchedulers.next().getSchedulerName();
		} else {
			return null;
		}
	}

	public AhPermitDenyValue getIpPolicyActionType() {
		if (userProfileObj.getActionIp() == POLICY_ACTION_PERMIT) {
			return AhPermitDenyValue.PERMIT;
		} else {
			return AhPermitDenyValue.DENY;
		}
	}

	public AhPermitDenyValue getMacPolicyActionType() {
		if (userProfileObj.getActionMac() == POLICY_ACTION_PERMIT) {
			return AhPermitDenyValue.PERMIT;
		} else {
			return AhPermitDenyValue.DENY;
		}
	}

	public boolean isConfigureIpOrMacPolicy() {
		return this.isConfigureIpPolicy() || this.isConfigureMacPolicy();
	}

	public boolean isConfigureIpPolicy() {
//		return ((ipPolicyTo != null || ipPolicyFrom != null)
//				&& userProfileObj.getActionIp() != -1) || (
//						userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL ||
//						userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_ALL ||
//						userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET);
		String fromPolicy, toPolicy;
		fromPolicy = this.getIpFromAirPolicyName();
		toPolicy = this.getIpToAirPolicyName();
		return (fromPolicy != null && !"".equals(fromPolicy)) || (toPolicy != null && !"".equals(toPolicy));
	}

	public String getIpFromAirPolicyName() {
//		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT && (userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL || 
//				userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET)){
//			if (ipPolicyFrom == null) {
//				return hiveAp.getSerialNumber() + IpPolicyProfileImpl.IP_POLICY_FOR_VPN_AUTO;
//			} else {
//				return hiveAp.getSerialNumber() + "_" + ipPolicyFrom.getPolicyName().hashCode();
//			}
//		}
		
		if (userProfileObj.getIpPolicyFrom() == null) {
			return null;
		} else if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP && !userProfileObj.getIpPolicyFrom().isAutoGenerate()){
			return null;
		} else {
			return userProfileObj.getIpPolicyFrom().getPolicyName();
		}
	}

	public String getIpToAirPolicyName() {
		if (userProfileObj.getIpPolicyTo() == null) {
			return null;
		} else if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP && !userProfileObj.getIpPolicyTo().isAutoGenerate()){
			return null;
		}else {
			return userProfileObj.getIpPolicyTo().getPolicyName();
		}
	}

	public boolean isConfigureMacPolicy() {
		return (macPolicyTo != null || macPolicyFrom != null)
				&& userProfileObj.getActionMac() != -1;
	}

	public String getMacFromAirPolicyName() {
		if (macPolicyFrom == null) {
			return null;
		} else {
			return macPolicyFrom.getPolicyName();
		}
	}

	public String getMacToAirPolicyName() {
		if (macPolicyTo == null) {
			return null;
		} else {
			return macPolicyTo.getPolicyName();
		}
	}
	
//	public boolean isConfigCac(){
//		return userProfileObj.getEnableCallAdmissionControl();
//	}
	
	public int getAirTime(){
		return userProfileObj.getGuarantedAirTime();
	}
	
	public boolean isConfigCacSharaTime(){
		return userProfileObj.getEnableShareTime();
	}
	
	public boolean isConfigTunnelPolicy(){
		return hiveAp.getConfigTemplate().getVpnService() != null && hiveAp.getVpnMark() != HiveAp.VPN_MARK_NONE &&
			(userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL ||
					userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_ALL ||
					userProfileObj.getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET);
	}
	
	public String getTunnelPolicyName(){
		return VPNProfileImpl.VPN_TUNNEL_POLICY_NAME;
	}
	
	public boolean isConfigAirScreen(){
		return airScreenRules != null && airScreenRules.size() > 0;
	}
	
	public int getAirScreenSize(){
		return airScreenRules.size();
	}
	
	public String getAirScreenRuleName(int index){
		return airScreenRules.get(index).getProfileName();
	}
	
	public boolean isBandwidthEnable(){
		return userProfileObj.isSlaEnable();
	}
	
	public int getBandwidthValue(){
		return userProfileObj.getSlaBandwidth();
	}
	
	public boolean isConfigActionLog(){
		return userProfileObj.getSlaAction() == UserProfile.SLA_ACTION_LOG ||
			userProfileObj.getSlaAction() == UserProfile.SLA_ACTION_LOG_BOOST;
	}
	
	public boolean isConfigActionBoost(){
		return userProfileObj.getSlaAction() == UserProfile.SLA_ACTION_BOOST ||
			userProfileObj.getSlaAction() == UserProfile.SLA_ACTION_LOG_BOOST;
	}
	
	public String getBeforeValue(int index, int allSize){
		index = allSize - index;
		StringBuffer strB = new StringBuffer("");
		for(int i=0; i<index; i++){
			strB.append(" ");
		}
		return strB.toString();
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public boolean isConfigL3TunnelAction(){
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			return hiveAp.getConfigTemplate().getVpnService() != null && 
					hiveAp.getConfigTemplate().getVpnService().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3;
		}else{
			return false;
		}
	}
	
	public UpL3TunnelActionValue getL3TunnelAction(){
		List<UserProfileForTrafficL3> lsUP = hiveAp.getConfigTemplate().getVpnService().getUserProfileTrafficL3();
		if(lsUP == null){
			return getUpL3TunnelActionValueType((short)-1);
		}
		for(UserProfileForTrafficL3 l3Item : lsUP){
			if(this.userProfileObj.getId().equals(l3Item.getUserProfile().getId())){
				return getUpL3TunnelActionValueType(l3Item.getVpnTunnelBehavior());
			}
		}
		return getUpL3TunnelActionValueType((short)-1);
	}
	
	private UpL3TunnelActionValue getUpL3TunnelActionValueType(short type){
		if(type == UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_NONE){
			return UpL3TunnelActionValue.DROP_TUNNEL_TRAFFIC;
		}else if(type == UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL){
			return UpL3TunnelActionValue.ALL;
		}else if(type == UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS){
			return UpL3TunnelActionValue.WITH_EXCEPTION;
		}
		
		VpnService vpn = hiveAp.getConfigTemplate().getVpnService();
		if(this.userProfileObj.getNetworkByVlan(hiveAp.getConfigTemplate()) != null 
				&& this.userProfileObj.getNetworkByVlan(hiveAp.getConfigTemplate()).getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_GUEST){
			return UpL3TunnelActionValue.DROP_TUNNEL_TRAFFIC;
		}else if(vpn != null && vpn.getRouteTrafficType() == VpnService.ROUTE_VPNTUNNEL_TRAFFIC_ALL){
			return UpL3TunnelActionValue.ALL;
		}else{
			return UpL3TunnelActionValue.SPLIT;
		}
	}

	@Override
	public String getUserProfileScheduleDenyMode() {
		return userProfileObj.getScheduleDenyModeName();
	}
	
	public boolean isConfigQosMap() {
		QosMarking qosMark = userProfileObj.getMarkerMap();
		return qosMark != null && 
				( (qosMark.getPrtclP() != null && !"".equals(qosMark.getPrtclP())) || (qosMark.getPrtclD() != null && !"".equals(qosMark.getPrtclD())) )
				&& this.sourceFunction.getQosProfileImpl() != null && this.sourceFunction.getQosProfileImpl().isQosEnable();
	}
	
	public boolean isConfigQosMap8021p() {
		return userProfileObj.getQosMarkTypeMode() == UserProfile.QOS_MARK_TYPE_MODE_8021P;
	}
	
	public boolean isConfigQosMapDiffserv() {
		return userProfileObj.getQosMarkTypeMode() == UserProfile.QOS_MARK_TYPE_MODE_DSCP;
	}
	
	public String getQosMapName() {
		return userProfileObj.getMarkerMap().getQosName();
	}
	
//	public boolean isConfigFromeAccess(){
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return true;
//		}else{
//			return false;
//		}
//	}
	
//	public boolean isConfigFromeAir(){
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return true;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return false;
//		}else{
//			return false;
//		}
//	}
	
//	public boolean isConfigToAccess(){
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return true;
//		}else{
//			return false;
//		}
//	}
	
//	public boolean isConfigToAir(){
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return true;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return false;
//		}else{
//			return false;
//		}
//	}

}