package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ah.bo.wlan.SsidProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class IdsPolicySsidProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SSID_PROFILE_ID", nullable = true)
	private SsidProfile ssidProfile;

	private boolean encryptionEnable;

	public static final short ENCRYPTION_TYPE_OPEN = 1;
	public static final short ENCRYPTION_TYPE_WEP = 2;
	public static final short ENCRYPTION_TYPE_WPA_WPA2 = 3;

	public static EnumItem[] ENCRYPTION_TYPE = MgrUtil.enumItems(
			"enum.encryption.", new int[] { ENCRYPTION_TYPE_OPEN,
					ENCRYPTION_TYPE_WEP, ENCRYPTION_TYPE_WPA_WPA2 });

	private int encryptionType;

	public int getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(int encryptionType) {
		this.encryptionType = encryptionType;
	}

	public SsidProfile getSsidProfile() {
		return ssidProfile;
	}

	public void setSsidProfile(SsidProfile ssidProfile) {
		this.ssidProfile = ssidProfile;
	}

	public boolean isEncryptionEnable() {
		return encryptionEnable;
	}

	public void setEncryptionEnable(boolean encryptionEnable) {
		this.encryptionEnable = encryptionEnable;
	}

}