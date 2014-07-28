/**
 *@filename		FirewallPolicyRule.java
 *@version
 *@author		Fiona
 *@createtime	2011-6-15 PM 03:25:16
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.useraccess.UserProfile;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class FirewallPolicyRule implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	@Range(min = 1)
	private short ruleId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_IP_ID", nullable = true)
	private IpAddress sourceIp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_UP_ID", nullable = true)
	private UserProfile sourceUp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_NETWORK_ID", nullable = true)
	private VpnNetwork sourceNtObj;
	
	private short sourceType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DESTINATION_IP_ID", nullable = true)
	private IpAddress destinationIp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DESTINATION_NETWORK_ID", nullable = true)
	private VpnNetwork destinationNtObj;
	
	private short destType = FirewallPolicy.FIREWALL_POLICY_TYPE_ANY;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NETWORK_SERVICE_ID", nullable = true)
	private NetworkService networkService;
	
	private short filterAction = IpPolicyRule.POLICY_ACTION_DENY;
	
	public static final short POLICY_LOGGING_ON = 1;
	
	public static final short POLICY_LOGGING_OFF = 2;

	public static EnumItem[] ENUM_FIREWALL_POLICY_LOGGING = MgrUtil.enumItems(
			"enum.logging.", new int[] { POLICY_LOGGING_ON, POLICY_LOGGING_OFF});
	
	private short actionLog = POLICY_LOGGING_OFF;
	
	private boolean disableRule = false;

	public boolean isDisableRule()
	{
		return disableRule;
	}

	public void setDisableRule(boolean disableRule)
	{
		this.disableRule = disableRule;
	}

	public short getActionLog()
	{
		return actionLog;
	}

	public void setActionLog(short actionLog)
	{
		this.actionLog = actionLog;
	}

	@Transient
	public String getFilterActionString() {
		return MacFilter.getFilterActionString(filterAction);
	}

	public IpAddress getDestinationIp() {
		return destinationIp;
	}

	public void setDestinationIp(IpAddress destinationIp) {
		this.destinationIp = destinationIp;
	}

	public short getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public void setNetworkService(NetworkService networkService) {
		this.networkService = networkService;
	}

	public IpAddress getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(IpAddress sourceIp) {
		this.sourceIp = sourceIp;
	}
	
	@Transient
	private int reorder;

	public int getReorder() {
		return reorder;
	}

	public void setReorder(int reorder) {
		this.reorder = reorder;
	}

	public UserProfile getSourceUp()
	{
		return sourceUp;
	}

	public void setSourceUp(UserProfile sourceUp)
	{
		this.sourceUp = sourceUp;
	}

	public short getSourceType()
	{
		return sourceType;
	}

	public void setSourceType(short sourceType)
	{
		this.sourceType = sourceType;
	}

	public short getDestType()
	{
		return destType;
	}

	public void setDestType(short destType)
	{
		this.destType = destType;
	}
	
	@Transient
	public String getSourceStr() {
		switch (sourceType) {
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
				String typeStr = "";
				switch (sourceIp.getTypeFlag()) {
					case IpAddress.TYPE_IP_NETWORK:
						typeStr = "IP Network (";
						break;
					case IpAddress.TYPE_IP_RANGE:
						typeStr = "IP Range (";
						break;
					case IpAddress.TYPE_IP_WILDCARD:
						typeStr = "Wildcard (";
						break;
				}
				return typeStr+sourceIp.getAddressName()+")";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ:
				return "Network Object ("+sourceNtObj.getNetworkName()+")";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ:
				return "User Profile ("+sourceUp.getUserProfileName()+")";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_VPN:
				return "VPN";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_ANY:
				return MgrUtil.getUserMessage("config.ipPolicy.any");
			default:
				return "";
		}
	}
	
	@Transient
	public String getDestStr() {
		switch (destType) {
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD:
			case FirewallPolicy.FIREWALL_POLICY_TYPE_HOST:
				String typeStr = "";
				switch (destinationIp.getTypeFlag()) {
					case IpAddress.TYPE_IP_NETWORK:
						typeStr = "IP Network (";
						break;
					case IpAddress.TYPE_IP_RANGE:
						typeStr = "IP Range (";
						break;
					case IpAddress.TYPE_IP_WILDCARD:
						typeStr = "Wildcard (";
						break;
					case IpAddress.TYPE_HOST_NAME:
						typeStr = "Host Name (";
						break;
				}
				return typeStr+destinationIp.getAddressName()+")";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ:
				return "Network Object ("+destinationNtObj.getNetworkName()+")";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_VPN:
				return "VPN";
			case FirewallPolicy.FIREWALL_POLICY_TYPE_ANY:
				return MgrUtil.getUserMessage("config.ipPolicy.any");
			default:
				return "";
		}
	}

	public VpnNetwork getSourceNtObj()
	{
		return sourceNtObj;
	}

	public void setSourceNtObj(VpnNetwork sourceNtObj)
	{
		this.sourceNtObj = sourceNtObj;
	}

	public VpnNetwork getDestinationNtObj()
	{
		return destinationNtObj;
	}

	public void setDestinationNtObj(VpnNetwork destinationNtObj)
	{
		this.destinationNtObj = destinationNtObj;
	}

	public short getRuleId()
	{
		return ruleId;
	}

	public void setRuleId(short ruleId)
	{
		this.ruleId = ruleId;
	}
	
	@Transient
	private String sourceCliNetwork;

	@Transient
	private String destCliNetwork;
	
	public String getSourceCliNetwork() {
		return sourceCliNetwork;
	}

	public void setSourceCliNetwork(String sourceCliNetwork) {
		this.sourceCliNetwork = sourceCliNetwork;
	}

	public String getDestCliNetwork() {
		return destCliNetwork;
	}

	public void setDestCliNetwork(String destCliNetwork) {
		this.destCliNetwork = destCliNetwork;
	}
	
	
}
