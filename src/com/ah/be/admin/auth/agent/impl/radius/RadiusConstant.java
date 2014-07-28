package com.ah.be.admin.auth.agent.impl.radius;

public class RadiusConstant {
	/**
	 * radius timeout and interval
	 */
	public static final int TIMEOUT = 3; //second
	public static final int RETRIES = 3; //try times
	
	/**
	 * radius attribute
	 */
	public static final int AEROHIVE_ID = 26928;
	public static final int ATTRID_GROUP = 1;
	
	/**
	 * radius reply codes
	 */
	public static final byte RADIUS_CODE_ACCESS_ACCEPT = (byte) 2;
	public static final byte RADIUS_CODE_ACCESS_REJECT = (byte) 3;
	
	/**
	 * radius authenticate method
	 */
	public static final String METHOD_PAP = "PAP";
	public static final String METHOD_CHAP = "CHAP";
	public static final String METHOD_MSCHAPV2 = "mschapv2";
}
