package com.ah.be.config.cli.generate;

public interface CLIKeyParam {

/********************************************************************************************/
	/**  aaa radius-server local nas <string> tls  */
	public static final String AAA_RADIUS_SERVER_LOCAL_NAS_TLS = "aaa.radius-server.local.nas.tls";

	/**  aaa radius-server name <string> server <string> shared-secret <string>  */
	public static final String AAA_RADIUS_SERVER_NAME = "aaa.radius-server.name";

	/**  aaa radius-server name <string> server <string> tls  */
	public static final String AAA_RADIUS_SERVER_NAME_TLS = "aaa.radius-server.name.tls";

	/**  aaa radius-server name <string> tls-port <port>  */
	public static final String AAA_RADIUS_SERVER_NAME_TLS_PORT = "aaa.radius-server.name.tls-port";

	/**  {false:no|true:} aaa radius-server proxy radsec dynamic-auth-extension  */
	public static final String AAA_RADIUS_SERVER_PROXY_RADSEC_DYNAMIC_AUTH_EXTENSION = "aaa.radius-server.proxy.radsec.dynamic-auth-extension";

	/**  {false:no|true:} aaa radius-server proxy radsec enable  */
	public static final String AAA_RADIUS_SERVER_PROXY_RADSEC_ENABLE = "aaa.radius-server.proxy.radsec.enable";

	/**  aaa radius-server proxy radsec realm <string> {primary|backup} <string>  */
	public static final String AAA_RADIUS_SERVER_PROXY_RADSEC_REALM = "aaa.radius-server.proxy.radsec.realm";

	/**  aaa radius-server proxy realm <string> {primary|backup} <string>  */
	public static final String AAA_RADIUS_SERVER_PROXY_REALM = "aaa.radius-server.proxy.realm";


/********************************************************************************************/
	/**  {false:no|true:} alg {ftp|tftp|sip|dns|http} enable  */
	public static final String ALG_PROTOCOL_ENABLE = "alg.protocol.enable";


/********************************************************************************************/
	/**  {false:no|true:} designated-server idm-proxy announce  */
	public static final String DESIGNATED_SERVER_IDM_PROXY_ANNOUNCE = "designated-server.idm-proxy.announce";

	/**  {false:no|true:} designated-server idm-proxy dynamic  */
	public static final String DESIGNATED_SERVER_IDM_PROXY_DYNAMIC = "designated-server.idm-proxy.dynamic";


/********************************************************************************************/
	/**  {false:no|true:} hiveui wan-cfg redirect-page enable  */
	public static final String HIVEUI_WAN_CFG_REDIRECT_PAGE_ENABLE = "hiveui.wan-cfg.redirect-page.enable";


/********************************************************************************************/
	/**  interface <ethx/y|aggx> spanning-tree mst-instance <number> path-cost <number>  */
	public static final String INTERFACE_SPANNING_TREE_MST_INSTANCE_PATH_COST = "interface.spanning-tree.mst-instance.path-cost";

	/**  interface <ethx/y|aggx> spanning-tree mst-instance <number> priority <number>  */
	public static final String INTERFACE_SPANNING_TREE_MST_INSTANCE_PRIORITY = "interface.spanning-tree.mst-instance.priority";


/********************************************************************************************/
	/**  qos classifier-map service <string> qos <number> action {1:permit|2:deny}  */
	public static final String QOS_CLASSIFIER_MAP_SERVICE = "qos.classifier-map.service";

	/**  qos classifier-map service <string> qos <number> action log  */
	public static final String QOS_CLASSIFIER_MAP_SERVICE_LOG = "qos.classifier-map.service.log";


/********************************************************************************************/
	/**  radio profile <string> channel-width {1:20|5:40|4:80}  */
	public static final String RADIO_PROFILE_CHANNEL_WIDTH_NEW = "radio.profile.channel_width.new";

	/**  radio profile <string> channel-width {1:20|2:40-above|3:40-below}  */
	public static final String RADIO_PROFILE_CHANNEL_WIDTH_OLD = "radio.profile.channel_width.old";

	/**  radio profile <string> primary-channel-offset {auto|0|1|2|3}  */
	public static final String RADIO_PROFILE_PRIMARY_CHANNEL_OFFSET = "radio.profile.primary-channel-offset";


/********************************************************************************************/
	/**  security-object <string> security additional-auth-method captive-web-portal cloud-cwp api-key <string> api-nonce <string>  */
	public static final String SECURITY_OBJECT_CLOUD_CWP_API_KEY_API_NONCE = "security_object.cloud-cwp.api-key.api-nonce";

	/**  security-object <string> security additional-auth-method captive-web-portal cloud-cwp customer-id <string>  */
	public static final String SECURITY_OBJECT_CLOUD_CWP_CUSTOMER_ID = "security_object.cloud-cwp.customer-id";

	/**  {false:no|true:} security-object <string> security additional-auth-method captive-web-portal cloud-cwp enable  */
	public static final String SECURITY_OBJECT_CLOUD_CWP_ENABLE = "security_object.cloud-cwp.enable";

	/**  security-object <string> security additional-auth-method captive-web-portal cloud-cwp service-id <number>  */
	public static final String SECURITY_OBJECT_CLOUD_CWP_SERVICE_ID = "security_object.cloud-cwp.service-id";

	/**  security-object <string> security additional-auth-method captive-web-portal cloud-cwp url-root-path <string>  */
	public static final String SECURITY_OBJECT_CLOUD_CWP_URL_ROOT_PATH = "security_object.cloud-cwp.url-root-path";

	/**  {false:no|true:} security-object <string> security aaa radius-server idm  */
	public static final String SECURITY_OBJECT_SECURITY_AAA_RADIUS_SERVER_IDM = "security_object.security.aaa.radius-server.idm";

	/**  security-object <string> user-profile-deny action ban [ <number> ]  */
	public static final String SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_BAN_TIME = "security_object.user-profile-deny.action.ban.time";

	/**  security-object <string> user-profile-deny action {1:ban|2:ban-forever|3:disconnect} [ {true:strict|false:} ]   */
	public static final String SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_STRICT = "security_object.user-profile-deny.action.strict";

	/**  security-object <string> walled-garden hostname <string> [ service {all|web} ]  */
	public static final String SECURITY_OBJECT_WALLED_GARDEN_HOSTNAME_SERVICE = "security_object.walled-garden.hostname.service";


/********************************************************************************************/
	/**  service <string> alg {2:ftp|3:tftp|4:sip|5:dns|6:http}  */
	public static final String SERVICE_ALG = "service.alg";

	/**  service <string> alg {2:ftp|3:tftp|4:sip}  */
	public static final String SERVICE_ALG_OLD = "service.alg.old";

	/**  service <string> alg {2:ftp|3:tftp|4:sip|5:dns|6:tv}  */
	public static final String SERVICE_ALG_TV = "service.alg.tv";

	/**  service <string> app-id <number> [ timeout <number> ]  */
	public static final String SERVICE_APP_ID = "service.app-id";

	/**  service <string> protocol <number> [ port <number> ] [ timeout <number> ]  */
	public static final String SERVICE_PROTOCOL_NUM_PORT_TIMEOUT = "service.protocol.num.port.timeout";

	/**  service <string> protocol {1:tcp|2:udp|3:svp} [ port <number> ] [ timeout <number> ]  */
	public static final String SERVICE_PROTOCOL_PORT_TIMEOUT = "service.protocol.port.timeout";


/********************************************************************************************/
	/**  ssid <string> user-profile-deny action ban [ <number> ]  */
	public static final String SSID_USER_PROFILE_DENY_ACTION_BAN_TIME = "ssid.user-profile-deny.action.ban.time";

	/**  ssid <string> user-profile-deny action {1:ban|2:ban-forever|3:disconnect} [ {true:strict|false:} ]  */
	public static final String SSID_USER_PROFILE_DENY_ACTION_STRICT = "ssid.user-profile-deny.action.strict";


}
