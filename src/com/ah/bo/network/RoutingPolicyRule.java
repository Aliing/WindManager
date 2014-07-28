package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class RoutingPolicyRule implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILEID")
	private UserProfile sourceUserProfile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_TRACK_PRI_ID")
	private MgmtServiceIPTrack ipTrackReachablePri;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_TRACK_SEC_ID")
	private MgmtServiceIPTrack ipTrackReachableSec;
		
	private short interfaceTypePri;
	private short interfaceTypeSec;
	
	public static final short FORWARDACTION_NOTUNNEL = 0;
	public static final short FORWARDACTION_ALL = 1;
	public static final short FORWARDACTION_DROP = 2;
	public static final short FORWARDACTION_SPLIT = 3;
	public static final short FORWARDACTION_EXCEPTION = 4;
	private short forwardActionTypePri;
	private short forwardActionTypeSec;
	
	public static final short ROUTING_POLICY_RULE_NONE = 0;
	public static final short ROUTING_POLICY_RULE_ETH0 = 1;
	public static final short ROUTING_POLICY_RULE_ETH1 = 2;
	public static final short ROUTING_POLICY_RULE_ETH2 = 3;
	public static final short ROUTING_POLICY_RULE_ETH3 = 4;
	public static final short ROUTING_POLICY_RULE_ETH4 = 5;
	public static final short ROUTING_POLICY_RULE_USB = 6;
	
	
	public static final short ROUTING_POLICY_RULE_ANY = -1;
	public static final short ROUTING_POLICY_RULE_ANYGUEST = -2;
	public static final short ROUTING_POLICY_RULE_USERPROFILE = 1;
	
	private short ruleType;
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_INTERFACE = MgrUtil.enumItems(
			"enum.routingpolicy.rule.interface.", new int[] {ROUTING_POLICY_RULE_ETH0,ROUTING_POLICY_RULE_USB,ROUTING_POLICY_RULE_NONE});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_INTERFACE_USB = MgrUtil.enumItems(
			"enum.routingpolicy.rule.interface.", new int[] {ROUTING_POLICY_RULE_USB,ROUTING_POLICY_RULE_ETH0,ROUTING_POLICY_RULE_NONE});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_INTERFACE_PRI = MgrUtil.enumItems(
			"enum.routingpolicy.rule.interface.", new int[] {ROUTING_POLICY_RULE_ETH0,ROUTING_POLICY_RULE_USB});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_ACTION = MgrUtil.enumItems(
			"enum.routingpolicy.rule.action.", new int[] {FORWARDACTION_DROP,FORWARDACTION_NOTUNNEL,FORWARDACTION_SPLIT,
					FORWARDACTION_ALL,FORWARDACTION_EXCEPTION});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_UP_ANY=MgrUtil.enumItems(
			"enum.routingpolicy.rule.userprofile.", new int[] { ROUTING_POLICY_RULE_ANY});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_UP_ANYGUEST=MgrUtil.enumItems(
			"enum.routingpolicy.rule.userprofile.guest.", new int[] { ROUTING_POLICY_RULE_ANYGUEST});
	
	public static EnumItem[] ENUM_ROUTING_POLICY_RULE_TRACKIP_NONE=MgrUtil.enumItems(
			"enum.routingpolicy.rule.trackip.", new int[] { ROUTING_POLICY_RULE_NONE});
	
	public UserProfile getSourceUserProfile() {
		return sourceUserProfile;
	}
	public void setSourceUserProfile(UserProfile sourceUserProfile) {
		this.sourceUserProfile = sourceUserProfile;
	}
	public MgmtServiceIPTrack getIpTrackReachablePri() {
		return ipTrackReachablePri;
	}
	public void setIpTrackReachablePri(MgmtServiceIPTrack ipTrackReachablePri) {
		this.ipTrackReachablePri = ipTrackReachablePri;
	}
	public MgmtServiceIPTrack getIpTrackReachableSec() {
		return ipTrackReachableSec;
	}
	public void setIpTrackReachableSec(MgmtServiceIPTrack ipTrackReachableSec) {
		this.ipTrackReachableSec = ipTrackReachableSec;
	}
	public short getInterfaceTypePri() {
		return interfaceTypePri;
	}
	public void setInterfaceTypePri(short interfaceTypePri) {
		this.interfaceTypePri = interfaceTypePri;
	}
	public short getInterfaceTypeSec() {
		return interfaceTypeSec;
	}
	public void setInterfaceTypeSec(short interfaceTypeSec) {
		this.interfaceTypeSec = interfaceTypeSec;
	}
	public short getForwardActionTypePri() {
		return forwardActionTypePri;
	}
	public void setForwardActionTypePri(short forwardActionTypePri) {
		this.forwardActionTypePri = forwardActionTypePri;
	}
	public short getForwardActionTypeSec() {
		return forwardActionTypeSec;
	}
	public void setForwardActionTypeSec(short forwardActionTypeSec) {
		this.forwardActionTypeSec = forwardActionTypeSec;
	}

	@Override
	public RoutingPolicyRule clone() {
		try {
			return (RoutingPolicyRule) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private int position;

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	@Transient
	public String getForwardActionTypePriStyle(){
		return interfaceTypePri != ROUTING_POLICY_RULE_NONE ? "":"none";
	}
	
	@Transient
	public String getForwardActionTypeSecStyle(){
		return interfaceTypeSec != ROUTING_POLICY_RULE_NONE ? "":"none";
	}
	public short getRuleType() {
		return ruleType;
	}
	public void setRuleType(short ruleType) {
		this.ruleType = ruleType;
	}
}
