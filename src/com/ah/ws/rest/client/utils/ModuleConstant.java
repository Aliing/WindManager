package com.ah.ws.rest.client.utils;

public interface ModuleConstant {
	public static final String CLIENT_MODULE_REDIRECTOR 	= "redirector";
	public static final String CLIENT_MODULE_LICENSESERVER 	= "licserver";
	public static final String CLIENT_MODULE_PORTAL 	= "portal";
	public static final String CLIENT_MODULE_SALESFORCE = "salesforce";
	public static final String CLIENT_MODULE_GUESTANALYTICS 	= "guestAnalytics";

	public final static byte NONE 		= 0;
	public final static byte ROLE 		= 1;
	public final static byte SKEY 		= 2;
	public final static byte ROLE_SKEY 	= 3;
	public final static byte ALL 		= 4;
}