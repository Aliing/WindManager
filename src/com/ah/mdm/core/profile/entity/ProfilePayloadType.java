package com.ah.mdm.core.profile.entity;

public interface ProfilePayloadType
{
	public static final String	PAYLOAD_TYPE_DEVICE_CONFIGURATION	= "Configuration";
	public static final String	PAYLOAD_TYPE_REMOVAL_PASSWORD		= "com.apple.profileRemovalPassword";
	public static final String	PAYLOAD_TYPE_PASSWORD_POLICY		= "com.apple.mobiledevice.passwordpolicy";
	public static final String	PAYLOAD_TYPE_INDENTIFICATION		= "com.apple.configurationprofile.identification";
	public static final String	PAYLOAD_TYPE_EMAIL					= "com.apple.mail.managed";
	public static final String	PAYLOAD_TYPE_WEP_CLIP				= "com.apple.webClip.managed";
	public static final String	PAYLOAD_TYPE_RESTRICTIONS			= "com.apple.applicationaccess";
	public static final String	PAYLOAD_TYPE_LDAP					= "com.apple.ldap.account";
	public static final String	PAYLOAD_TYPE_CARD_DAV				= "com.apple.carddav.account";
	public static final String	PAYLOAD_TYPE_CAL_DAV				= "com.apple.caldav.account";
	public static final String	PAYLOAD_TYPE_SUB_CAL				= "com.apple.subscribedcalendar.account";
	public static final String	PAYLOAD_TYPE_SCEP					= "com.apple.security.scep";
	public static final String	PAYLOAD_TYPE_SYSTEM_POLICY_CONTROL	= "com.apple.systempolicy.control";
	public static final String	PAYLOAD_TYPE_SYSTEM_POLICY_RULE		= "com.apple.systempolicy.rule";
	public static final String	PAYLOAD_TYPE_SYSTEM_POLICY_MANAGED	= "com.apple.systempolicy.managed";
	public static final String	PAYLOAD_TYPE_GLOBAL_HTTP_PROXY		= "com.apple.proxy.http.global";
	public static final String	PAYLOAD_TYPE_APP_LOCK				= "com.apple.app.lock";
	public static final String	PAYLOAD_TYPE_APN					= "com.apple.apn.managed";
	public static final String	PAYLOAD_TYPE_EXCHANGE_IOS			= "com.apple.eas.account";
	public static final String	PAYLOAD_TYPE_EXCHANGE_OSX			= "com.apple.ews.account";
	public static final String	PAYLOAD_TYPE_VPN					= "com.apple.vpn.managed";
	public static final String	PAYLOAD_TYPE_WIFI					= "com.apple.wifi.managed";
	public static final String	PAYLOAD_TYPE_CREDENTIALS_CRT_ENTITY	= "com.apple.security.pkcs1";
	public static final String	PAYLOAD_TYPE_CREDENTIALS_P12		= "com.apple.security.pkcs12";
	public static final String	PAYLOAD_TYPE_CREDENTIALS_CRT_CA		= "com.apple.security.root";
	public static final String	PAYLOAD_TYPE_MDM					= "com.apple.mdm";
}
