package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.useraccess.UserProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class UserProfileForTrafficL3 implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILEID")
	private UserProfile userProfile;
	
	private short vpnTunnelBehavior = VPNTUNNEL_MODE_NOTAPPLICABLE;

	public static final short VPNTUNNEL_MODE_DISABLED = 1;
	
	public static final short VPNTUNNEL_MODE_ENABLED = 2;
	
	public static final short VPNTUNNEL_MODE_NOTAPPLICABLE = 3;
	
	public static final short VPNTUNNEL_EXCEPTIONS_BEHAVIOR_NONE = 4;
	
	public static final short VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL = 5;
	
	public static final short VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS = 6;

	public static EnumItem[] ENUM_VPN_TUNNEL_MODE_SPLIT = MgrUtil.enumItems(
			"enum.config.vpnservices.userprofiles.management.", new int[] { VPNTUNNEL_EXCEPTIONS_BEHAVIOR_NONE,
					VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL, VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS });
	
	public static EnumItem[] ENUM_VPN_TUNNEL_MODE_ALL = MgrUtil.enumItems(
			"enum.config.vpnservices.userprofiles.management.", new int[] { VPNTUNNEL_EXCEPTIONS_BEHAVIOR_NONE,VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS });

	@Transient
	private int position;
	
	public String getVpnTunnelBehaviorShow(){
		if(null == userProfile){
			return null;
		}
		switch (vpnTunnelBehavior) {
			case UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_NONE:
				return "Tunnel None";
			case UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_ALL:
				return "Tunnel All";
			case UserProfileForTrafficL3.VPNTUNNEL_EXCEPTIONS_BEHAVIOR_EXCEPTIONS:
				return "Tunnel All w/ Exception";
			default:
				return "Tunnel None";
		}
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public short getVpnTunnelBehavior() {
		return vpnTunnelBehavior;
	}

	public void setVpnTunnelBehavior(short vpnTunnelBehavior) {
		this.vpnTunnelBehavior = vpnTunnelBehavior;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}