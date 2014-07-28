package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.wlan.Scheduler;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "USER_PROFILE", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "USERPROFILENAME" }) })
@org.hibernate.annotations.Table(appliesTo = "USER_PROFILE", indexes = {
		@Index(name = "USER_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class UserProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String userProfileName;

	@Range(min = 0, max = 4095)
	private short attributeValue = 1;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean defaultFlag;

	private boolean enableCallAdmissionControl;

	public static final short DEFAULT_GUARANTEED_AIR_TIME = 0;

	@Range(min = 0, max = 100)
	private short guarantedAirTime = DEFAULT_GUARANTEED_AIR_TIME;

	private boolean enableShareTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATTRITUTE_GROUP_ID")
	private UserProfileAttribute userProfileAttribute;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VLAN_ID")
	private Vlan vlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOS_RATE_CONTROL_ID")
	private QosRateControl qosRateControl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IDENTITY_BASED_TUNNEL_ID")
	private TunnelSetting tunnelSetting;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_POLICE_TO_ID")
	private IpPolicy ipPolicyTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_POLICE_FROM_ID")
	private IpPolicy ipPolicyFrom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAC_POLICY_TO_ID")
	private MacPolicy macPolicyTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAC_POLICY_FROM_ID")
	private MacPolicy macPolicyFrom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AS_RULE_GROUP_ID")
	private AirScreenRuleGroup asRuleGroup;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "VPN_NETWORK_ID")
//	private VpnNetwork networkObj;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "USER_PROFILE_SCHEDULER", joinColumns = { @JoinColumn(name = "USER_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEDULER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Scheduler> userProfileSchedulers = new HashSet<Scheduler>();

	public static final short SCHEDULE_DENY_MODE_BAN = 1;
	public static final short SCHEDULE_DENY_MODE_QUARANTINE = 2;
	private short scheduleDenyMode = 1;
	// public static final short DEFAULT_ACTION_PERMIT = 1;
	//
	// public static final short DEFAULT_ACTION_DENY = 2;
	//
	// public static EnumItem[] ENUM_DEFAULT_ACTION = MgrUtil.enumItems(
	// "enum.defaultAction.", new int[] { DEFAULT_ACTION_PERMIT,
	// DEFAULT_ACTION_DENY });
	//
	private short actionMac = -1;

	private short actionIp = -1;

	public static final short VPN_TUNNEL_TRAFFIC_NOT_LOCAL = 1;
	public static final short VPN_TUNNEL_TRAFFIC_ALL = 2;
	public static final short VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET = 3;
	private short tunnelTraffic;// no default value

	private boolean slaEnable;

	@Range(min = 100, max = 500000)
	private int slaBandwidth = 500;
	
	public static final short SLA_ACTION_NONE = 0;
	public static final short SLA_ACTION_LOG = 1;
	public static final short SLA_ACTION_BOOST = 2;
	public static final short SLA_ACTION_LOG_BOOST = 3;

	public static EnumItem[] ENUM_SLA_ACTION = MgrUtil.enumItems(
			"enum.sla.action.", new int[] { SLA_ACTION_NONE, SLA_ACTION_LOG,
					SLA_ACTION_BOOST, SLA_ACTION_LOG_BOOST });

	private short slaAction = SLA_ACTION_LOG;
	
	@Range(min = 0, max = 54000)
	private int policingRate = 54000;
	
	@Range(min = 0, max = 2000000)
	private int policingRate11n = 1000000;
	
	@Range(min = 0, max = 2000000)
	private int policingRate11ac = 1000000;
	
	@Range(min = 0, max = 1000)
	private int schedulingWeight = 10;
	
//	public static final int USER_CATEGORY_EMPLOOYEE = 1;
//	
//	public static final int USER_CATEGORY_GUEST = 2;
//	
//	public static final int USER_CATEGORY_VOICE = 3;
//	
//	public static final int USER_CATEGORY_CUSTOM = 4;
//	
//	// add this field from 3.4r10
//	private int userCategory = USER_CATEGORY_CUSTOM;
	
	private boolean blnUserManager;
	
	/*
	 * user profile reassign
	 */
	private boolean enableAssign;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DEVICE_POLICY_RULE", joinColumns = @JoinColumn(name = "USER_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DevicePolicyRule> assignRules = new ArrayList<DevicePolicyRule>();
	// reassign end
	
	public static final short QOS_MARK_TYPE_MODE_DSCP = 0;
	public static final short QOS_MARK_TYPE_MODE_8021P = 1;
    private short qosMarkTypeMode = QOS_MARK_TYPE_MODE_DSCP;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOS_MARKING_ID")
	private QosMarking markerMap;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "userProfileName", "attributeValue",
				"description", "actionMac", "actionIp", "defaultFlag",
				"ATTRITUTE_GROUP_ID", "VLAN_ID", "QOS_RATE_CONTROL_ID",
				"IDENTITY_BASED_TUNNEL_ID", "IP_POLICE_TO_ID",
				"IP_POLICE_FROM_ID", "MAC_POLICY_TO_ID", "MAC_POLICY_FROM_ID",
				"enableCallAdmissionControl", "guarantedAirTime",
				"enableShareTime", "tunnelTraffic", "as_rule_group_id", 
				"slaenable", "slabandwidth", "slaaction", 
				"policingRate", "policingRate11n","schedulingWeight",
				"blnUserManager","OWNER", "enableAssign", "vpn_network_id","scheduleDenyMode",
				"qosMarkTypeMode","QOS_MARKING_ID", "policingRate11ac"};
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return this.userProfileName;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public short getActionIp() {
		return actionIp;
	}

	public void setActionIp(short actionIp) {
		this.actionIp = actionIp;
	}

	public short getActionMac() {
		return actionMac;
	}

	public void setActionMac(short actionMac) {
		this.actionMac = actionMac;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MacPolicy getMacPolicyFrom() {
		return macPolicyFrom;
	}
	
	public String getMacPolicyFromValue() {
		if (this.getMacPolicyFrom() == null)
			return "";
		return getMacPolicyFrom().getPolicyName();
	}

	public void setMacPolicyFrom(MacPolicy macPolicyFrom) {
		this.macPolicyFrom = macPolicyFrom;
	}

	public MacPolicy getMacPolicyTo() {
		return macPolicyTo;
	}
	public String getMacPolicyToValue() {
		if (this.getMacPolicyTo() == null)
			return "";
		return getMacPolicyTo().getPolicyName();
	}

	public void setMacPolicyTo(MacPolicy macPolicyTo) {
		this.macPolicyTo = macPolicyTo;
	}

	public QosRateControl getQosRateControl() {
		return qosRateControl;
	}

	public void setQosRateControl(QosRateControl qosRateControl) {
		this.qosRateControl = qosRateControl;
	}

	public TunnelSetting getTunnelSetting() {
		return tunnelSetting;
	}

	public void setTunnelSetting(TunnelSetting tunnelSetting) {
		this.tunnelSetting = tunnelSetting;
	}

	public UserProfileAttribute getUserProfileAttribute() {
		return userProfileAttribute;
	}

	public void setUserProfileAttribute(
			UserProfileAttribute userProfileAttribute) {
		this.userProfileAttribute = userProfileAttribute;
	}

	public String getUserProfileName() {
		return userProfileName;
	}
	
	public String getUserProfileNameSubstr() {
		if (userProfileName==null) {
			return "";
		}
		if (userProfileName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return userProfileName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public Set<Scheduler> getUserProfileSchedulers() {
		return userProfileSchedulers;
	}

	public void setUserProfileSchedulers(Set<Scheduler> userProfileSchedulers) {
		this.userProfileSchedulers = userProfileSchedulers;
	}

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	public IpPolicy getIpPolicyFrom() {
		return ipPolicyFrom;
	}
	
	public String getIpPolicyFromValue() {
		if (this.getIpPolicyFrom() == null)
			return "";
		return getIpPolicyFrom().getPolicyName();
	}

	public void setIpPolicyFrom(IpPolicy ipPolicyFrom) {
		this.ipPolicyFrom = ipPolicyFrom;
	}

	public IpPolicy getIpPolicyTo() {
		return ipPolicyTo;
	}
	public String getIpPolicyToValue() {
		if (this.getIpPolicyTo() == null)
			return "";
		return getIpPolicyTo().getPolicyName();
	}

	public void setIpPolicyTo(IpPolicy ipPolicyTo) {
		this.ipPolicyTo = ipPolicyTo;
	}

	@Transient
	private String policyType;

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public short getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(short attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Transient
	public String getTunnelValue() {
		if (getTunnelSetting() == null)
			return "";
		return getTunnelSetting().getTunnelName();
	}

	@Transient
	public String getVlanValue() {
		if (getVlan() == null) {
//			if (null != getNetworkObj()) {
//				return getNetworkObj().getVlan().getVlanName();
//			}
			return "";
		}
		return getVlan().getVlanName();
	}

	@Transient
	public String getAttributeGroupValue() {
		if (this.getUserProfileAttribute() == null)
			return "";
		return this.getUserProfileAttribute().getAttributeName();
	}

	@Transient
	public String getQosValue() {
		if (this.getQosRateControl() == null)
			return "";
		return getQosRateControl().getQosName();
	}

	@Transient
	public boolean getEnableQosMarkType(){
		if(this.markerMap == null || this.markerMap.getId() == -1){
			return false;
		}
		
		return true;
	}
	
	@Transient
	private String currentOperation;

	public String getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(String currentOperation) {
		this.currentOperation = currentOperation;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public boolean getEnableCallAdmissionControl() {
		return this.enableCallAdmissionControl;
	}

	public void setEnableCallAdmissionControl(boolean enableCallAdmissionControl) {
		this.enableCallAdmissionControl = enableCallAdmissionControl;
	}

	public short getGuarantedAirTime() {
		return this.guarantedAirTime;
	}

	public void setGuarantedAirTime(short guarantedAirTime) {
		this.guarantedAirTime = guarantedAirTime;
	}

	public AirScreenRuleGroup getAsRuleGroup() {
		return asRuleGroup;
	}

	public void setAsRuleGroup(AirScreenRuleGroup asRuleGroup) {
		this.asRuleGroup = asRuleGroup;
	}

	@Transient
	public String getGuarantedAirTimeValue() {
		return String.valueOf(this.guarantedAirTime) + "%";
	}

	@Transient
	public String getEnableShareTimeValue() {
		return getEnableShareTime() ? "True" : "False";
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getValue() {
		return this.userProfileName;
	}

	/**
	 * getter of enableShareTime
	 * 
	 * @return the enableShareTime
	 */
	public boolean getEnableShareTime() {
		return enableShareTime;
	}

	/**
	 * setter of enableShareTime
	 * 
	 * @param enableShareTime
	 *            the enableShareTime to set
	 */
	public void setEnableShareTime(boolean enableShareTime) {
		this.enableShareTime = enableShareTime;
	}

	public short getTunnelTraffic() {
		return tunnelTraffic;
	}

	public void setTunnelTraffic(short tunnelTraffic) {
		this.tunnelTraffic = tunnelTraffic;
	}

	public boolean isSlaEnable() {
		return slaEnable;
	}

	public void setSlaEnable(boolean slaEnable) {
		this.slaEnable = slaEnable;
	}

	public int getSlaBandwidth() {
		return slaBandwidth;
	}

	public void setSlaBandwidth(int slaBandwidth) {
		this.slaBandwidth = slaBandwidth;
	}

	public short getSlaAction() {
		return slaAction;
	}

	public void setSlaAction(short slaAction) {
		this.slaAction = slaAction;
	}

	public String getTunnelUsedString() {
		if (tunnelSetting != null) {
			return "GRE Tunnel";
		} else {
			return "No Tunnel";
		}
	}

	public String getSplitTunnelString() {
		if (getTunnelTraffic() == VPN_TUNNEL_TRAFFIC_NOT_LOCAL
				|| getTunnelTraffic() == VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET) {
			return "Yes";
		} else if (getTunnelTraffic() == VPN_TUNNEL_TRAFFIC_ALL) {
			return "No";
		}
		return "-";
	}

	@Transient
	private String firewallDisplayStyle = "none"; // by default
	@Transient
	private String qosSettingDisplayStyle = "none";
	@Transient
	private String greVpnTunnelDisplayStyle = "none";
	@Transient
	private String scheduleDisplayStyle = "none";
	@Transient
	private String slaDisplayStyle = "none";
	@Transient
	private String clientClassDisplayStyle = "none";
	@Transient
	private String mdmDisplayStyle = "none";
	public String getMdmDisplayStyle() {
		return mdmDisplayStyle;
	}
	public void setMdmDisplayStyle(String mdmDisplayStyle) {
		this.mdmDisplayStyle = mdmDisplayStyle;
	}
	@Transient
	private String advSettingDisplayStyle = "none";

	public String getFirewallDisplayStyle() {
		return firewallDisplayStyle;
	}

	public void setFirewallDisplayStyle(String firewallDisplayStyle) {
		this.firewallDisplayStyle = firewallDisplayStyle;
	}

	public String getQosSettingDisplayStyle() {
		return qosSettingDisplayStyle;
	}

	public void setQosSettingDisplayStyle(String qosSettingDisplayStyle) {
		this.qosSettingDisplayStyle = qosSettingDisplayStyle;
	}

	public String getGreVpnTunnelDisplayStyle() {
		return greVpnTunnelDisplayStyle;
	}

	public void setGreVpnTunnelDisplayStyle(String greVpnTunnelDisplayStyle) {
		this.greVpnTunnelDisplayStyle = greVpnTunnelDisplayStyle;
	}

	public String getScheduleDisplayStyle() {
		return scheduleDisplayStyle;
	}

	public void setScheduleDisplayStyle(String scheduleDisplayStyle) {
		this.scheduleDisplayStyle = scheduleDisplayStyle;
	}

	public String getSlaDisplayStyle() {
		return slaDisplayStyle;
	}

	public void setSlaDisplayStyle(String slaDisplayStyle) {
		this.slaDisplayStyle = slaDisplayStyle;
	}

	public String getAdvSettingDisplayStyle() {
		return advSettingDisplayStyle;
	}

	public void setAdvSettingDisplayStyle(String advSettingDisplayStyle) {
		this.advSettingDisplayStyle = advSettingDisplayStyle;
	}

	public int getPolicingRate() {
		return policingRate;
	}

	public void setPolicingRate(int policingRate) {
		this.policingRate = policingRate;
	}

	public int getPolicingRate11n() {
		return policingRate11n;
	}

	public void setPolicingRate11n(int policingRate11n) {
		this.policingRate11n = policingRate11n;
	}
	
	public int getPolicingRate11ac() {
		return policingRate11ac;
	}

	public void setPolicingRate11ac(int policingRate11ac) {
		this.policingRate11ac = policingRate11ac;
	}

	public int getSchedulingWeight() {
		return schedulingWeight;
	}

	public void setSchedulingWeight(int schedulingWeight) {
		this.schedulingWeight = schedulingWeight;
	}

	public short getQosMarkTypeMode() {
		return qosMarkTypeMode;
	}

	public QosMarking getMarkerMap() {
		return markerMap;
	}

	public void setQosMarkTypeMode(short qosMarkTypeMode) {
		this.qosMarkTypeMode = qosMarkTypeMode;
	}

	public void setMarkerMap(QosMarking markerMap) {
		this.markerMap = markerMap;
	}

	@Override
	public UserProfile clone() {
		try {
			return (UserProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

//	public int getUserCategory() {
//		return userCategory;
//	}
//
//	public void setUserCategory(int userCategory) {
//		this.userCategory = userCategory;
//	}
	
	public static final short TUNNEL_NO = 1;
	public static final short TUNNEL_GRE = 2;
	public static final short TUNNEL_VPN = 3;
	
	@Transient
	public short tunnelType = TUNNEL_NO;

	public short getTunnelType()
	{
		return tunnelType;
	}

	public void setTunnelType(short tunnelType)
	{
		this.tunnelType = tunnelType;
	}

	public boolean getBlnUserManager() {
		return blnUserManager;
	}

	public void setBlnUserManager(boolean blnUserManager) {
		this.blnUserManager = blnUserManager;
	}
	
	@Transient
	private String vlanInputValue;

	public String getVlanInputValue() {
		return vlanInputValue;
	}

	public void setVlanInputValue(String vlanInputValue) {
		this.vlanInputValue = vlanInputValue;
	}
	
	@Transient
	private String attriInputValue;

	public String getAttriInputValue() {
		return attriInputValue;
	}

	public void setAttriInputValue(String attriInputValue) {
		this.attriInputValue = attriInputValue;
	}

	public boolean isEnableAssign()
	{
		return enableAssign;
	}

	public void setEnableAssign(boolean enableAssign)
	{
		this.enableAssign = enableAssign;
	}

	public List<DevicePolicyRule> getAssignRules()
	{
		return assignRules;
	}

	public void setAssignRules(List<DevicePolicyRule> assignRules)
	{
		this.assignRules = assignRules;
	}

	public String getClientClassDisplayStyle()
	{
		return clientClassDisplayStyle;
	}

	public void setClientClassDisplayStyle(String clientClassDisplayStyle)
	{
		this.clientClassDisplayStyle = clientClassDisplayStyle;
	}

//	public VpnNetwork getNetworkObj()
//	{
//		return networkObj;
//	}
//
//	public void setNetworkObj(VpnNetwork networkObj)
//	{
//		this.networkObj = networkObj;
//	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	@Transient
	private boolean blnForceUpControl;
	
	public boolean isBlnForceUpControl() {
		return blnForceUpControl;
	}

	public void setBlnForceUpControl(boolean blnForceUpControl) {
		this.blnForceUpControl = blnForceUpControl;
	}

	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}
	
	@Transient
	private String displayUserManager = "Disabled";
	
	public String getDisplayUserManager() {
		if(getBlnUserManager()) {
			displayUserManager = "Enabled";
		} else {
			displayUserManager = "Disabled";
		}
		return displayUserManager;
	}

	public void setDisplayUserManager(String displayUserManager) {
		this.displayUserManager = displayUserManager;
	}
	
	@Transient
	public VpnNetwork getNetworkByVlan(ConfigTemplate configTemp){
		if(configTemp == null){
			return null;
		}
		if(this.vlan == null){
			return null;
		}
		return configTemp.getNetworkByVlan(this.vlan);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof UserProfile)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((UserProfile) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	public short getScheduleDenyMode() {
		return scheduleDenyMode;
	}

	public void setScheduleDenyMode(short scheduleDenyMode) {
		this.scheduleDenyMode = scheduleDenyMode;
	}
	
	public String getScheduleDenyModeName(){
		if(this.scheduleDenyMode == SCHEDULE_DENY_MODE_BAN){
			return "ban";
		}else if(this.scheduleDenyMode == SCHEDULE_DENY_MODE_QUARANTINE){
			return "quarantine";
		}else{
			return null;
		}
	}
}