package com.ah.bo.performance;

import com.ah.bo.wlan.SsidProfile;

public class PciWeakSsidInfo {

	private String apName;

	private String ssidName;

	private int accessSecurity;

	private int encryption;

	public String getApName() {
		return apName;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public int getEncryption() {
		return encryption;
	}
	
	public String getEncryptionStr() {
		switch (encryption) {
		case SsidProfile.KEY_ENC_NONE:
			return "None";
		case SsidProfile.KEY_ENC_CCMP:
			return "CCMP (AES)";
		case SsidProfile.KEY_ENC_TKIP:
			return "TKIP";
		case SsidProfile.KEY_ENC_WEP104:
			return "WEP 104";
		case SsidProfile.KEY_ENC_WEP40:
			return "WEP 40";
		case SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP:
			return "Auto-TKIP or CCMP (AES)";
		default:
			return "Unknow";
		}
	}

	public void setEncryption(int encryption) {
		this.encryption = encryption;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getAccessSecurity() {
		return accessSecurity;
	}
	
	public String getAccessSecurityStr(){
		switch (accessSecurity){
			case SsidProfile.ACCESS_MODE_WPA:
				return "WPA/WPA2 PSK (Personal)";
			case SsidProfile.ACCESS_MODE_PSK:
				return "Private PSK";
			case SsidProfile.ACCESS_MODE_8021X:
				return "WPA/WPA2 802.1X (Enterprise)";
			case SsidProfile.ACCESS_MODE_WEP:
				return "WEP";
			case SsidProfile.ACCESS_MODE_OPEN:
				return "Open";
			default:
				return "Unknown";
		}
	}

	public void setAccessSecurity(int accessSecurity) {
		this.accessSecurity = accessSecurity;
	}


}
