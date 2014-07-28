package com.ah.be.config.create.source;



/**
 * 
 * @author zhang
 *
 */
public interface IpProfileInt {
	
	public static final String IGMP_POLICY_ENABLE_SNOOPING = "POLICY_ENABLE_SNOOPING";
	public static final String IGMP_POLICY_ENABLE_IMMEDIATE_LEAVE = "POLICY_ENABLE_IMMEDIATE_LEAVE";
	public static final String IGMP_POLICY_ENABLE_DELAYLEAVE_QUERY_INTERVAL = "POLICY_ENABLE_DELAYLEAVE_QUERY_INTERVAL";
	public static final String IGMP_POLICY_ENABLE_DELAYLEAVE_QUERY_COUNT = "POLICY_ENABLE_DELAYLEAVE_QUERY_COUNT";
	public static final String IGMP_POLICY_ROBUSTNESS_COUNT = "POLICY_ROBUSTNESS_COUNT";
	public static final String IGMP_POLICY_ROUTERPORT_AGINTIME = "POLICY_ROUTERPORT_AGINTIME";
	public static final String IGMP_POLICY_VLAN_ID = "POLICY_VLAN_ID";
	
	public static final String IGMP_MULTICAST_GROUP_INTERFACE_PORT = "GROUP_INTERFACE_PORT";
	public static final String IGMP_MULTICAST_GROUP_VLAN_ID = "GROUP_VLAN_ID";
	public static final String IGMP_MULTICAST_GROUP_IP_ADDRESS = "GROUP_IP_ADDRESS";
		
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();
	
	public String getIpNetName(int index);
	
	public int getIpNetSize();
	
	public boolean isConfigPathAndTcpMss();
	
	public boolean isIpPathMtuDiscoveryEnable();
	
	public boolean isIpTcpMssThresholdEnable();
	
	public boolean isConfigThresholdSize();
	
	public int getThresholdSize();
	
	public boolean isConfigL3VpnThresholdSize();
	
	public int getL3VpnThresholdSize();
	
//	public String getGateWay();
	
	public boolean isConfigIGMP();
	
	public boolean isEnableIgmpSnooping();
	
	public boolean isEnableImmediateLeave();
	
	public boolean isEnableReportSuppression();
	
	public int getGlobalDelayLeaveQueryInterval();
	 
	public int getGlobalDelayLeaveQueryCount();
	
	public int getGlobalRouterPortAginTime();
	 
	public int getGlobalRobustnessCount();
	
	public int getIgmpPolicySize();
	
	public boolean getIgmpPolicyEnableSnooping(int index);
    
    public boolean getIgmpPolicyEnableImmediateLeave(int index);
    
    public int getIgmpPolicyDelayLeaveQueryCount(int index);
    
    public int getIgmpPolicyDelayLeaveQueryInterval(int index);
    
    public int getIgmpPolicyRobustnessCount(int index);
    
    public int getIgmpPolicyRouterPortAginTime(int index);
    
    public int getIgmpPolicyVlanId(int index);
	
    public int getIgmpMulticastGroupSize();
    
    public String getMulticastGroupValue(int index);
    
    public String getIpHostName(int index);
	
	public int getIpHostSize();
	
	public boolean isConfigNatPolicy();
	
	public int getNatPolicySize();
	
	public String getNatPolicyName(int index);
	
	public boolean isNatPolicyConfigMatch(int index);
	
	public boolean isNatPolicyConfigVirtualHost(int index);
	
	public String getNatPolicyVhostInsideHostValue(int index);
	
	public String getNatPolicyMatchInsideValue(int index);
	
	public String getNatPolicyMatchOutsideValue(int index);
	
	public String getNatPolicyVhostInsidePortValue(int index);
	
	public String getNatPolicyVhostOutsidePortValue(int index);
	
	public String getNatPolicyVhostProtocolValue(int index);

}
