package com.ah.mdm.core.profile.utils;

public interface IProtocolContants
{
	public static final String	VERSION_STR									= "version";
	public static final String	VERSION_VALUE								= "1.0";
	public static final String	DOCTYPE_PUBLIC_ID							= "-//Apple Computer//DTD PLIST 1.0//EN";
	public static final String	DOCTYPE_SYSTEM_ID							= "http://www.apple.com/DTDs/PropertyList-1.0.dtd";
	public static final String	PLIST										= "plist";
	public static final String	DICT										= "dict";
	public static final String	KEY											= "key";
	public static final String	STRING										= "string";
	public static final String	DATA										= "data";
	public static final String	ARRAY										= "array";
	public static final String	INTEGER										= "integer";
	public static final String	FLOAT										= "float";
	public static final String	TRUE										= "true";
	public static final String	FALSE										= "false";
	public static final String	REAL										= "real";

	public static final String	COMMAND										= "Command";
	public static final String	COMMANDUUID									= "CommandUUID";
	public static final String	REQUEST_TYPE								= "RequestType";

	public static final String	PAYLOAD_DESCRIPTION							= "PayloadDescription";
	public static final String	PAYLOAD_DISPLAYNAME							= "PayloadDisplayName";
	public static final String	PAYLOAD_USERPROFILEATTRIBUTE				= "PayloadUserProfileAttribute";
	public static final String	PAYLOAD_IDENTIFIER							= "PayloadIdentifier";
	public static final String	PAYLOAD_ORGANIZATION						= "PayloadOrganization";
	public static final String	PAYLOAD_TYPE								= "PayloadType";
	public static final String	PAYLOAD_UUID								= "PayloadUUID";
	public static final String	PAYLOAD_VERSION								= "PayloadVersion";

	public static final String	HAS_REMOVAL_PASSCODE						= "HasRemovalPasscode";
	public static final String	IS_ENCYPTED									= "IsEncrypted";
	public static final String	IS_MANAGED									= "IsManaged";
	public static final String	PAYLOAD_CONTENT								= "PayloadContent";
	public static final String	PAYLOAD_REMOVAL_DISALLOWED					= "PayloadRemovalDisallowed";
	public static final String	SIGNER_CERTIFICATES							= "SignerCertificates";

	public static final String	REMOVAL_PASSWORD							= "RemovalPassword";

	public static final String	ALLOW_SIMPLE								= "allowSimple";
	public static final String	FORCE_PIN									= "forcePIN";
	public static final String	MAX_FAILED_ATTEMPTS							= "maxFailedAttempts";
	public static final String	MAX_INACTIVITY								= "maxInactivity";
	public static final String	MAX_PIN_AGE_IN_DAYS							= "maxPINAgeInDays";
	public static final String	MIN_COMPLEX_CHARS							= "minComplexChars";
	public static final String	MIN_LENGTH									= "minLength";
	public static final String	REQUIRE_ALPHANUMERIC						= "requireAlphanumeric";
	public static final String	PIN_HISTORY									= "pinHistory";
	public static final String	MANUAL_FETCHING_WHEN_ROAMING				= "manualFetchingWhenRoaming";
	public static final String	MAX_GRACE_PERIOD							= "maxGracePeriod";

	public static final String	EMAIL_ACCOUNT_DESCRIPTION					= "EmailAccountDescription";
	public static final String	EMAIL_ACCOUNT_NAME							= "EmailAccountName";
	public static final String	EMAIL_ACCOUNT_TYPE							= "EmailAccountType";
	public static final String	EMAIL_ADDRESS								= "EmailAddress";
	public static final String	INCOMING_MAIL_SERVER_IMAPPATHPREFIX			= "IncomingMailServerIMAPPathPrefix";
	public static final String	INCOMING_MAIL_SERVER_AUTHENTICATION			= "IncomingMailServerAuthentication";
	public static final String	INCOMING_MAIL_SERVER_HOST_NAME				= "IncomingMailServerHostName";
	public static final String	INCOMING_MAIL_SERVER_PORT_NUMBER			= "IncomingMailServerPortNumber";
	public static final String	INCOMING_MAIL_SERVER_USE_SSL				= "IncomingMailServerUseSSL";
	public static final String	INCOMING_MAIL_SERVER_USERNAME				= "IncomingMailServerUsername";
	public static final String	INCOMING_PASSWORD							= "IncomingPassword";
	public static final String	OUTGOING_PASSWORD							= "OutgoingPassword";
	public static final String	OUTGOING_PASSWORD_SAME_AS_INCOMING_PASSWORD	= "OutgoingPasswordSameAsIncomingPassword";
	public static final String	OUTGOING_MAIL_SERVER_AUTHENTICATION			= "OutgoingMailServerAuthentication";
	public static final String	OUTGOING_MAIL_SERVER_HOST_NAME				= "OutgoingMailServerHostName";
	public static final String	OUTGOING_MAIL_SERVER_PORT_NUMBER			= "OutgoingMailServerPortNumber";
	public static final String	OUTGOING_MAIL_SERVER_USE_SSL				= "OutgoingMailServerUseSSL";
	public static final String	OUTGOING_MAIL_SERVER_USERNAME				= "OutgoingMailServerUsername";
	public static final String	PREVENT_MOVE								= "PreventMove";
	public static final String	PREVENT_APP_SHEET							= "PreventAppSheet";
	public static final String	SMIME_ENABLED								= "SMIMEEnabled";
	public static final String	SMIME_SIGNING_CERTIFICATE_UUID				= "SMIMESigningCertificateUUID";
	public static final String	SMIME_ENCRYPTION_CERTIFICATE_UUID			= "SMIMEEncryptionCertificateUUID";

	public static final String	URL											= "URL";
	public static final String	LABEL										= "Label";
	public static final String	ICON										= "Icon";
	public static final String	IS_REMOVABLE								= "IsRemovable";
	public static final String	FULL_SCREEN									= "FullScreen";
	public static final String	PRECOMPOSED									= "Precomposed";

	public static final String	ALLOW_APP_INSTALLATION						= "allowAppInstallation";
	public static final String	ALLOW_ASSISTANT								= "allowAssistant";
	public static final String	ALLOW_CAMERA								= "allowCamera";
	public static final String	ALLOW_EXPLICITCONTENT						= "allowExplicitContent";
	public static final String	ALLOW_SCREEN_SHOT							= "allowScreenShot";
	public static final String	ALLOW_YOUTUBE								= "allowYouTube";
	public static final String	ALLOW_ITUNES								= "allowiTunes";
	public static final String	FORCE_ITUNES_STORE_PASSWORD_ENTRY			= "forceITunesStorePasswordEntry";
	public static final String	ALLOW_SAFARI								= "allowSafari";
	public static final String	ALLOW_UNTRUSTED_TLS_PROMPT					= "allowUntrustedTLSPrompt";
	public static final String	ALLOW_CLOUD_BACKUP							= "allowCloudBackup";
	public static final String	ALLOW_CLOUD_DOCUMENT_SYNC					= "allowCloudDocumentSync";
	public static final String	ALLOW_PHOTO_STREAM							= "allowPhotoStream";

	public static final String	LDAP_ACCOUNT_DESCRIPTION					= "LDAPAccountDescription";
	public static final String	LDAP_ACCOUNT_NAME							= "LDAPAccountHostName";
	public static final String	LDAP_ACCOUNT_USE_SSL						= "LDAPAccountUseSSL";
	public static final String	LDAP_ACCOUNT_USER_NAME						= "LDAPAccountUserName";
	public static final String	LDAP_ACCOUNT_PASSWORD						= "LDAPAccountPassword";
	public static final String	LDAP_SEARCH_SETTINGS						= "LDAPSearchSettings";
	public static final String	LDAP_SEARCH_SETTINGS_DESCRIPTION			= "LDAPSearchSettingDescription";
	public static final String	LDAP_SEARCH_SETTINGS_BASE					= "LDAPSearchSettingSearchBase";
	public static final String	LDAP_SEARCH_SETTINGS_SCOPE					= "LDAPSearchSettingScope";

	public static final String	CALDAV_ACCOUNT_DESCRIPTION					= "CalDAVAccountDescription";
	public static final String	CALDAV_HOST_NAME							= "CalDAVHostName";
	public static final String	CALDAV_USERNAME								= "CalDAVUsername";
	public static final String	CALDAV_PASSWORD								= "CalDAVPassword";
	public static final String	CALDAV_USE_SSL								= "CalDAVUseSSL";
	public static final String	CALDAV_PORT									= "CalDAVPort";
	public static final String	CALDAV_PRINCIPAL_URL						= "CalDAVPrincipalURL";

	public static final String	SUB_CAL_ACCOUNT_DESCRIPTION					= "SubCalAccountDescription";
	public static final String	SUB_CAL_ACCOUNT_HOST_NAME					= "SubCalAccountHostName";
	public static final String	SUB_CAL_ACCOUNT_USERNAME					= "SubCalAccountUsername";
	public static final String	SUB_CAL_ACCOUNT_PASSWORD					= "SubCalAccountPassword";
	public static final String	SUB_CAL_ACCOUNT_USE_SSL						= "SubCalAccountUseSSL";

	public static final String	DEFAULTS_DATA								= "DefaultsData";
	public static final String	DEFAULTS_DOMAIN_NAME						= "DefaultsDomainName";
	public static final String	APNS										= "apns";
	public static final String	APN											= "apn";
	public static final String	USERNAME									= "username";
	public static final String	PASSWORD									= "password";
	public static final String	PROXY										= "proxy";
	public static final String	PROXY_PORT									= "proxyPort";

	public static final String	ERROR_CHAIN_ITEM_LOCAL_DESC					= "LocalizedDescription";
	public static final String	ERROR_CHAIN_ITEM_EN_DESC					= "USEnglishDescription";
	public static final String	ERROR_CHAIN_ITEM_ERROR_DOMAIN				= "ErrorDomain";
	public static final String	ERROR_CHAIN_ITEM_ERROR_CODE					= "ErrorCode";

	public static final int		ENROLLMENT_REQUEST							= 0;
	public static final int		DEVICE_IDLE_RESULT							= 1;
	public static final int		COMMAND_RESULT								= 2;
	public static final int		ERROR_RESULT								= -1;

	public static int			ACCEPT_EAP_TYPE_TLS							= 13;
	public static int			ACCEPT_EAP_TYPE_LEAP						= 17;
	public static int			ACCEPT_EAP_TYPE_TTLS						= 21;
	public static int			ACCEPT_EAP_TYPE_PEAP						= 25;
	public static int			ACCEPT_EAP_TYPE_EAPFAST						= 43;
	public static int			ACCEPT_EAP_TYPE_EAPSIM						= 18;

	public static String		ACCESS_RIGHTS								= "AccessRights";
	public static String		CHECK_IN_URL								= "CheckInURL";
	public static String		CHECK_OUT_WHEN_REMOVED						= "CheckOutWhenRemoved";
	public static String		IDENTITY_CERTIFICATE_UUID					= "IdentityCertificateUUID";
	public static String		SERVER_URL									= "ServerURL";
	public static String		SIGN_MESSAGE								= "SignMessage";
	public static String		TOPIC										= "Topic";
	public static String		USE_DEVELOPMENT_APNS						= "UseDevelopmentAPNS";

	// ///////////////////////////////Start Request Type////////////////////////
	public static final String	DEVICE_LOCK									= "DeviceLock";
	public static final String	PROFILE_LIST								= "ProfileList";
	public static final String	INSTALL_PROFILE								= "InstallProfile";
	public static final String	REMOVE_PROFILE								= "RemoveProfile";
	public static final String	PROVISION_PROFILE_LIST						= "ProvisioningProfileList";
	public static final String	INSTALL_PROVISION_PROFILE					= "InstallProvisioningProfile";
	public static final String	REMOVE_PROVISION_PROFILE					= "RemoveProvisioningProfile";
	public static final String	CERTIFICATE_LIST							= "CertificateList";
	public static final String	INSTALLED_APP_LIST							= "InstalledApplicationList";
	public static final String	DEVICE_INFORMATION							= "DeviceInformation";
	public static final String	SECURITY_INFO								= "SecurityInfo";
	public static final String	CLEAR_PASSCODE								= "ClearPasscode";
	public static final String	ERASE_DEVICE								= "EraseDevice";
	public static final String	RESTRICTIONS								= "Restrictions";
	public static final String	INSTALL_APPLICATION							= "InstallApplication";
	public static final String	APPLY_REDEMPTION_CODE						= "ApplyRedemptionCode";
	public static final String	MANAGED_APP_LIST							= "ManagedApplicationList";
	public static final String	REMOVE_APPLICATION							= "RemoveApplication";
	public static final String	SETTINGS									= "Settings";
	// /////////////////////////////End Request Type/////////////////////

}
