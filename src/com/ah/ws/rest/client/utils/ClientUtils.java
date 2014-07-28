package com.ah.ws.rest.client.utils;

public abstract class ClientUtils {
	public static RedirectorResUtils getRedirectorResUtils() {
		return RedirectorResUtils.getInstance();
	}

	public static LicenseResUtils getLicenseResUtils() {
		return LicenseResUtils.getInstance();
	}
	
	public static PortalResUtils getPortalResUtils() {
	    return PortalResUtils.getInstance();
	}
	
	public static SalesforceResUtils getSalesforceResUtils() {
	    return SalesforceResUtils.getInstance();
	}
}
