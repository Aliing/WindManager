package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.FacilityValue;

/**
 * 
 * @author zhang
 *
 */
public interface LogingProfileInt {
	
//	public static final String LOGGING_FACILITY_AUTH = "auth";
//	public static final String LOGGING_FACILITY_AUTHPRIV = "authpriv";
//	public static final String LOGGING_FACILITY_SECURITY = "security";
//	public static final String LOGGING_FACILITY_USER = "user";
//	public static final String LOGGING_FACILITY_LOCAL0 = "local0";
//	public static final String LOGGING_FACILITY_LOCAL1 = "local1";
//	public static final String LOGGING_FACILITY_LOCAL2 = "local2";
//	public static final String LOGGING_FACILITY_LOCAL3 = "local3";
//	public static final String LOGGING_FACILITY_LOCAL4 = "local4";
//	public static final String LOGGING_FACILITY_LOCAL5 = "local5";
//	public static final String LOGGING_FACILITY_LOCAL6 = "local6";
//	public static final String LOGGING_FACILITY_LOCAL7 = "local7";
	
	public static final String SERVER_LEVEL_ALERT = "alert";
	public static final String SERVER_LEVEL_CRITICAL = "critical";
	public static final String SERVER_LEVEL_DEBUG = "debug";
	public static final String SERVER_LEVEL_EMERGENCY = "emergency";
	public static final String SERVER_LEVEL_ERROR = "error";
	public static final String SERVER_LEVEL_INFO = "info";
	public static final String SERVER_LEVEL_NOTIFICATION = "notification";
	public static final String SERVER_LEVEL_WARNING = "warning";
	
	public String getLoggingGuiName();
	
	public String getLoggingName();
	
	public String getApVersion();
	
	public boolean isConfigureLoging();
	
	public String getUpdateTime();
	
	public FacilityValue getLoggingFacilityValue();
	
	public int getLoggingServerSize();
	
	public String getLoggingServerName(int index) throws CreateXMLException;
	
	public String getLoggingServerLevel(int index);
	
	public boolean isEnableVpnTunnel(int index) throws CreateXMLException;
	
//	public boolean isConfigLogTunnel(int index);
}
