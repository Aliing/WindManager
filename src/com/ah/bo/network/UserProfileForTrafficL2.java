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
public class UserProfileForTrafficL2 implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILEID")
	private UserProfile userProfile;

	private String tunnelSelected;
	
	public static final short VPNTUNNEL_MODE_DISABLED = 1;
	
	public static final short VPNTUNNEL_MODE_ENABLED = 2;
	
	public static final short VPNTUNNEL_MODE_NOTAPPLICABLE = 3;

	public static EnumItem[] ENUM_VPN_TUNNEL_MODE_L2 = MgrUtil.enumItems(
			"enum.config.vpnservices.userprofiles.management.", new int[] { VPNTUNNEL_MODE_DISABLED,
					VPNTUNNEL_MODE_ENABLED});
	
	private short vpnTunnelModeL2 = VPNTUNNEL_MODE_DISABLED;
	
	@Transient
	private int position;

	
	@Transient
	public String getTunnelTrafficStyle() {
		if (userProfile != null) {
			if(vpnTunnelModeL2 == VPNTUNNEL_MODE_ENABLED){
				return "";
			}
		}
		return "none";
	}
	
	public UserProfile getUserProfile() {
		return userProfile;
	}
	
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public String getTunnelSelected() {
		return tunnelSelected;
	}

	public void setTunnelSelected(String tunnelSelected) {
		this.tunnelSelected = tunnelSelected;
	}

	public short getVpnTunnelModeL2() {
		return vpnTunnelModeL2;
	}

	public void setVpnTunnelModeL2(short vpnTunnelModeL2) {
		this.vpnTunnelModeL2 = vpnTunnelModeL2;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}