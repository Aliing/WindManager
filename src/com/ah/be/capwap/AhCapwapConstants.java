/**
 *@filename		AhCapwapConstants.java
 *@version
 *@author		Francis
 *@createtime	2007-8-4 05:06:31 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

//java import
import java.io.Serializable;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public interface AhCapwapConstants extends Serializable {

	/*
	 * Message type constant definition (ietf capwap protocol specification
	 * draft). Section 4.3.1.1 Message type
	 */
	int	DISCOVERY_REQUEST			= 1;
	int	DISCOVERY_RESPONSE			= 2;
	int	JOIN_REQUEST				= 3;
	int	JOIN_RESPONSE				= 4;
	int	WTP_EVENT_REQUEST			= 9;
	int	WTP_EVENT_RESPONSE			= 10;
	int	CHANGE_STATE_REQUEST		= 11;
	int	CHANGE_STATE_RESPONSE		= 12;
	int	ECHO_REQUEST				= 13;
	int	ECHO_RESPONSE				= 14;
	int	LAYER3_ROAMING_CONFIG_REQUEST	= 1001;
	int	LAYER3_ROAMING_CONFIG_RESPONSE	= 1002;
	int	IDP_QUERY_REQUEST			= 1003;
	int IDP_QUERY_RESPONSE			= 1004;
	int	WTP_EVENT_CONTROL_REQUEST	= 1005;
	int	WTP_EVENT_CONTROL_RESPONSE	= 1006;
	int	WTP_FILE_DOWNLOAD_REQUEST	= 1007;
	int	WTP_FILE_DOWNLOAD_RESPONSE	= 1008;

	/**
	 * Capwap Request Message Types.
	 */
	int DISCOVERY_TYPE				= 20;
	int	LOCATION_DATE				= 27;
	int	RADIO_ADMINISTRATIVE_STATE	= 29;
	int	SESSION_ID					= 32;
	int	DESCRIPTOR					= 36;
	int	FALLBACK					= 37;
	int	FRAME_TUNNEL_MODE			= 38;
	int	IPV4						= 39;
	int	MAC_TYPE					= 40;
	int	WTP_NAME					= 41;
	int	OPERATIONAL_STATISTICS		= 46;

	/**
	 * Private Message Types defined by Aerohive.
	 */
	int	MAC_ADDRESS					= 5000;// Mac address
	int	NETMASK						= 5001;// Netmask
	int	GATEWAY						= 5002;// Gateway
	int	REGION_CODE					= 5003;// Region code
	int	COUNTRY_CODE				= 5004;// Country code
	int	IP_TYPE						= 5005;// Ip type
	int	AP_TYPE						= 5006;// Ap Type
	int	IDP_STATISTICS				= 6001;// Idp statistics

	/**
	 * Capwap Response Message Types.
	 */
	int	AC_DESCRIPTOR				= 1;
	int	AC_NAME						= 4;
	int	AC_IPV4						= 10;
	int	AC_IPV6						= 11;
	int	LAYER3_ROAMING				= 6000;
	int	WTP_EVENT_CONTROL			= 6002;
	int FILE_DOWNLOAD				= 6003;

	/**
	 * WTP Event Message Type.
	 */
	int	AP_TYPE_CHANGE				= 6101;
	int FILE_DOWNLOAD_RESULT		= 6102;
	int FILE_DOWNLOAD_PROGRESS		= 6103;

	enum FsmState {
		RESET, IDLE, JOIN, RUN
	}

	enum ClientReqType {
		DISCOVER, JOIN, CHANGE_STATE, ECHO, TIMEOUT
	}

	int	CAPWAP_VERSION_V2			= 0;
	int	CAPWAP_VERSION_V4			= 4;

	/**
	 * WTP Event Control.
	 */
	int	WTP_EVENT_DISABLE			= 0;
	int	WTP_EVENT_ENABLE			= 1;

	/**
	 * Ip Type.
	 */
	int	IP_TYPE_DYNAMIC				= 0;
	int	IP_TYPE_STATIC				= 1;

	/**
	 * Ap Type.
	 */
	int	AP_TYPE_MP					= 0;
	int	AP_TYPE_PORTAL				= 1;
	int	AP_TYPE_UNKNOWN				= -1;

	int	MAXIMUN_TRANSMIT_COUNT		= 3;
	int	RETRANSMIT_INTERVAL			= 35000;

	int IANA						= 26928;
	String AH_AC_NAME				= "hivemanager";

}