package com.ah.be.communication.mo;

public class HAEnableInfo {

	private String	primaryHMMac;

	private String	secondaryHostname;

	private String	haSecret;

	// don't set this field. only leave for later requirement.
	private boolean	useExternalIPHostname = true;

	private String	primaryExternalIPHostname;

	private String	secondaryExternalIPHostname;

	// don't set this field. only leave for later requirement.
	private boolean	enableFailBack	= true;

	public String getPrimaryHMMac() {
		return primaryHMMac;
	}

	public void setPrimaryHMMac(String primaryHMMac) {
		this.primaryHMMac = primaryHMMac;
	}

	public String getSecondaryHostname() {
		return secondaryHostname;
	}

	public void setSecondaryHostname(String secondaryHostname) {
		this.secondaryHostname = secondaryHostname;
	}

	public String getHaSecret() {
		return haSecret;
	}

	public void setHaSecret(String haSecret) {
		this.haSecret = haSecret;
	}

	public boolean isUseExternalIPHostname() {
		return useExternalIPHostname;
	}

	public void setUseExternalIPHostname(boolean useExternalIPHostname) {
		this.useExternalIPHostname = useExternalIPHostname;
	}

	public String getPrimaryExternalIPHostname() {
		return primaryExternalIPHostname;
	}

	public void setPrimaryExternalIPHostname(String primaryExternalIPHostname) {
		this.primaryExternalIPHostname = primaryExternalIPHostname;
	}

	public String getSecondaryExternalIPHostname() {
		return secondaryExternalIPHostname;
	}

	public void setSecondaryExternalIPHostname(String secondaryExternalIPHostname) {
		this.secondaryExternalIPHostname = secondaryExternalIPHostname;
	}

	public boolean isEnableFailBack() {
		return enableFailBack;
	}

	public void setEnableFailBack(boolean enableFailBack) {
		this.enableFailBack = enableFailBack;
	}

}