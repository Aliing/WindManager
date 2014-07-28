package com.ah.bo.performance;

import com.ah.bo.network.CompliancePolicy;

public class ComplianceSsidListInfo {
	
	public static final int SSID_OPEN = 1;
	public static final int SSID_OPEN_AUTH = 2;
	public static final int SSID_WEP = 3;
	public static final int SSID_PSK = 4;
	public static final int SSID_PRIVETE_PSK = 5;
	public static final int SSID_8021X = 6;
	public static final int ETH_WAN = 10;
	private String ssidName;
	private int ssidMethod;
	private int rating;
	private int ssidPass;
	private int blnSsh;
	private int blnTelnet;
	private int blnPing;
	private int blnSnmp;

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public int getSsidMethod() {
		return ssidMethod;
	}

	public String getSsidMethodString() {
		switch (ssidMethod) {
		case SSID_OPEN:
			return "Open";
		case SSID_OPEN_AUTH:
			return "Open with Authentication";
		case SSID_WEP:
			return "WEP";
		case SSID_PSK:
			return "WPA or WPA2 Personal (PSK)";
		case SSID_PRIVETE_PSK:
			return "WPA or WPA2 with Private PSK";
		case SSID_8021X:
			return "WPA or WPA2 Enterprise (802.1X)";
		case ETH_WAN:
			return "WAN";
		default:
			return "N/A";
		}
	}

	public void setSsidMethod(int ssidMethod) {
		this.ssidMethod = ssidMethod;
	}

	public int getRating() {
		return rating;
	}
	
	public String getRatingString(){
		return getMgtFilterString(rating);
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getSsidPass() {
		return this.ssidPass;
	}

	public void setSsidPass(int ssidPass) {
		this.ssidPass = ssidPass;
	}

	public String getSsidPassString() {
		return ComplianceResult.getPasswordStrengthString(ssidPass);
	}

	public int getBlnSsh() {
		return blnSsh;
	}

	public String getBlnSshString() {
		return getMgtFilterString(blnSsh);
	}

	public void setBlnSsh(int blnSsh) {
		this.blnSsh = blnSsh;
	}

	public int getBlnTelnet() {
		return blnTelnet;
	}

	public String getBlnTelnetString() {
		return getMgtFilterString(blnTelnet);
	}

	public void setBlnTelnet(int blnTelnet) {
		this.blnTelnet = blnTelnet;
	}

	public int getBlnPing() {
		return blnPing;
	}

	public String getBlnPingString() {
		return getMgtFilterString(blnPing);
	}

	public void setBlnPing(int blnPing) {
		this.blnPing = blnPing;
	}

	public int getBlnSnmp() {
		return blnSnmp;
	}

	public String getBlnSnmpString() {
		return getMgtFilterString(blnSnmp);
	}

	public void setBlnSnmp(int blnSnmp) {
		this.blnSnmp = blnSnmp;
	}
	
	public String getMgtFilterString(int type){
		if (ssidMethod==ETH_WAN) {
			return "N/A";
		}
		switch (type) {
		case CompliancePolicy.COMPLIANCE_POLICY_POOR:
			return "weak";
		case CompliancePolicy.COMPLIANCE_POLICY_GOOD:
			return "moderate";
		case CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT:
			return "strong";
		default:
			return "N/A";
		}
	}
}
