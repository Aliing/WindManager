package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;

@Embeddable
@SuppressWarnings("serial")
public class ConfigTemplateSsidUserProfile implements Serializable {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SSID_PROFILE_ID")
	private SsidProfile ssidProfile;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_PROFILE_ID")
	private UserProfile userProfile;
	
	public static final short USERPORFILE_TYPE_DEFAULT = 1;
	public static final short USERPORFILE_TYPE_REGISTERED = 2;
	public static final short USERPORFILE_TYPE_AUTHENTICATED = 3;
	public static final short USERPORFILE_TYPE_RADIUSLEARNED = 4;
	private int upType;

	public SsidProfile getSsidProfile() {
		return ssidProfile;
	}

	public void setSsidProfile(SsidProfile ssidProfile) {
		this.ssidProfile = ssidProfile;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@Transient
	public String getKey() {
		return ssidProfile.getId() + "|" + userProfile.getId();
	}

	public int getUpType() {
		return upType;
	}

	public void setUpType(int upType) {
		this.upType = upType;
	}
}
