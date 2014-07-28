package com.ah.be.communication;

/**
 * constant class for communication layer
 *@filename		BeCommunicationConstant.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-12 03:07:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCommunicationConstant {

	// message element type
	public static final short	MESSAGEELEMENTTYPE_APIDENTIFIER						= 1;

	public static final short	MESSAGEELEMENTTYPE_APDESCRIPTOR						= 2;

	public static final short	MESSAGEELEMENTTYPE_RESULTDESCRIPTOR					= 3;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPSERVERDESCRIPTOR			= 4;

	public static final short	MESSAGEELEMENTTYPE_APDTLSDESCRIPTOR					= 5;

	public static final short	MESSAGEELEMENTTYPE_APDTLSAUTHORIZEDESCRIPTOR		= 6;

	public static final short	MESSAGEELEMENTTYPE_LICENSEDESCRIPTOR				= 7;

	public static final short	MESSAGEELEMENTTYPE_HASTATUS							= 8;

	public static final short	MESSAGEELEMENTTYPE_SIMULATEAPDESCRIPTION			= 9;

	public static final short	MESSAGEELEMENTTYPE_APFLAG							= 10;

	public static final short	MESSAGEELEMENTTYPE_SIMULATEAPRESULT					= 11;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPCLIENTDESCRIPTOR			= 12;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPCLIENTCONNECTDESCRIPTOR	= 13;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPSTATISTICS					= 14;

	public static final short	MESSAGEELEMENTTYPE_APPLICATIONINFO					= 15;

	public static final short	MESSAGEELEMENTTYPE_LAYER3ROAMING					= 6000;

	public static final short	MESSAGEELEMENTTYPE_IDPSTATISTICS					= 6001;

	public static final short	MESSAGEELEMENTTYPE_WTPEVENTCONTROL					= 6002;

	public static final short	MESSAGEELEMENTTYPE_CLI								= 6003;

	public static final short	MESSAGEELEMENTTYPE_STATISTICTABLE					= 6004;

	public static final short	MESSAGEELEMENTTYPE_IDPQUERY							= 6006;

	public static final short	MESSAGEELEMENTTYPE_CWPDIRECTORYQUERY				= 6009;

	public static final short	MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEY			= 6012;

	public static final short	MESSAGEELEMENTTYPE_ABORTDATA						= 6010;

	public static final short	MESSAGEELEMENTTYPE_ABORTRESULT						= 6011;

	public static final short	MESSAGEELEMENTTYPE_APTYPECHANGE						= 6101;

	public static final short	MESSAGEELEMENTTYPE_CLIRESULT						= 6102;

	public static final short	MESSAGEELEMENTTYPE_FILEDOWNLOADPROGRESS				= 6103;

	public static final short	MESSAGEELEMENTTYPE_STATISTICRESULT					= 6104;

	public static final short	MESSAGEELEMENTTYPE_REBOOTFAILEVENT					= 6105;

	public static final short	MESSAGEELEMENTTYPE_CONFIGVERSIONEVENT				= 6106;

	public static final short	MESSAGEELEMENTTYPE_CWPDIRECTORYRESULT				= 6107;

	public static final short	MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEYRESULT		= 6108;

	public static final short	MESSAGEELEMENTTYPE_HIVENAMECHANGE					= 6110;

	public static final short	MESSAGEELEMENTTYPE_TRAPEVENT						= 6200;

	public static final short	MESSAGEELEMENTTYPE_INFORMATIONQUERY					= 6210;

	public static final short	MESSAGEELEMENTTYPE_INFORMATIONRESULT				= 6211;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY			= 6212;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT			= 6213;

	public static final short	MESSAGEELEMENTTYPE_EVENTQUERYRESULT					= 6214;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPPAYLOADQUERY				= 7000;

	public static final short	MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT				= 7001;

	// message type
	public static final short	MESSAGETYPE_APCONNECT								= 1;

	public static final short	MESSAGETYPE_APDISCONNECT							= 2;

	public static final short	MESSAGETYPE_APWTPEVENT								= 3;

	public static final short	MESSAGETYPE_APDTLSAUTHORIZEEVENT					= 4;

	public static final short	MESSAGETYPE_CAPWAPCONNECT							= 5;

	public static final short	MESSAGETYPE_CAPWAPCLIENTCONNECT						= 6;

	public static final short	MESSAGETYPE_WTPCONTROLREQ							= 11;

	public static final short	MESSAGETYPE_WTPCONTROLRSP							= 12;

	public static final short	MESSAGETYPE_IDPQUERYREQ								= 13;

	public static final short	MESSAGETYPE_IDPQUERYRSP								= 14;

	public static final short	MESSAGETYPE_L3ROAMINGCONFIGREQ						= 15;

	public static final short	MESSAGETYPE_L3ROAMINGCONFIGRSP						= 16;

	public static final short	MESSAGETYPE_CLIREQ									= 17;

	public static final short	MESSAGETYPE_CLIRSP									= 18;

	public static final short	MESSAGETYPE_CAPWAPDTLSCONFIGREQ						= 21;

	public static final short	MESSAGETYPE_CAPWAPDTLSCONFIGRSP						= 22;

	public static final short	MESSAGETYPE_CAPWAPSERVERCONFIGREQ					= 23;

	public static final short	MESSAGETYPE_CAPWAPSERVERCONFIGRSP					= 24;

	public static final short	MESSAGETYPE_SHUTDOWNREQ								= 25;

	public static final short	MESSAGETYPE_SHUTDOWNRSP								= 26;

	public static final short	MESSAGETYPE_CAPWAPSERVERLICENSEREQ					= 27;

	public static final short	MESSAGETYPE_CAPWAPSERVERLICENSERSP					= 28;

	public static final short	MESSAGETYPE_DELETEAPCONNECTREQ						= 31;

	public static final short	MESSAGETYPE_DELETEAPCONNECTRSP						= 32;

	public static final short	MESSAGETYPE_GETSTATISTICREQ							= 33;

	public static final short	MESSAGETYPE_GETSTATISTICRSP							= 34;

	public static final short	MESSAGETYPE_ABORTREQ								= 35;

	public static final short	MESSAGETYPE_ABORTRSP								= 36;

	public static final short	MESSAGETYPE_SHOWCWPDIRECTORYREQ						= 37;

	public static final short	MESSAGETYPE_SHOWCWPDIRECTORYRSP						= 38;

	public static final short	MESSAGETYPE_HOSTIDENTIFICATIONKEYREQ				= 39;

	public static final short	MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP				= 40;

	public static final short	MESSAGETYPE_CAPWAPCLIENTINFOREQ						= 41;

	public static final short	MESSAGETYPE_CAPWAPCLIENTINFORSP						= 42;

	public static final short	MESSAGETYPE_CAPWAPCLIENTEVENTREQ					= 43;

	public static final short	MESSAGETYPE_CAPWAPCLIENTEVENTRSP					= 44;

	public static final short	MESSAGETYPE_HASETREQ								= 45;

	public static final short	MESSAGETYPE_HASETRSP								= 46;

	public static final short	MESSAGETYPE_HAQUERYREQ								= 47;

	public static final short	MESSAGETYPE_HAQUERYRSP								= 48;

	public static final short	MESSAGETYPE_CAPWAPSTATREQ							= 49;

	public static final short	MESSAGETYPE_CAPWAPSTATRSP							= 50;

	public static final short	MESSAGETYPE_APPLICATIONINFOREQ						= 51;

	public static final short	MESSAGETYPE_APPLICATIONINFORSP						= 52;

	public static final short	MESSAGETYPE_SIMULATEHIVEAPREQ						= 101;

	public static final short	MESSAGETYPE_SIMULATEHIVEAPRSP						= 102;

	public static final short	MESSAGETYPE_CAPWAPPAYLOADREQ						= 103;

	public static final short	MESSAGETYPE_CAPWAPPAYLOADRSP						= 104;

	public static final short	MESSAGETYPE_CAPWAPCLIENTPARAMCONFIGREQ				= 105;

	public static final short	MESSAGETYPE_CAPWAPCLIENTPARAMCONFIGRSP				= 106;

	// result type
	// 0: success
	// 1: disconnected
	// 2: state invalid
	// 3: unknown
	// 127: send request time out(no response return)
	// 126: connect close
	// 124: send request time out(get response event, but no result event return)
	// 123: resouce busy(cli)
	// 125: message exceed limit(cli)
	public static final byte	RESULTTYPE_SUCCESS									= 0;

	public static final byte	RESULTTYPE_NOFSM									= 1;

	public static final byte	RESULTTYPE_FSMNOTRUN								= 2;

	public static final byte	RESULTTYPE_UNKNOWNMSG								= 3;

	public static final byte	RESULTTYPE_TIMEOUT_NORESULT							= 124;

	public static final byte	RESULTTYPE_TIMEOUT									= 127;

	public static final byte	RESULTTYPE_CONNECTCLOSE								= 126;

	public static final byte	RESULTTYPE_MESSAGELENEXCEEDLIMIT					= 125;

	public static final byte	RESULTTYPE_RESOUCEBUSY								= 123;

	// timeout
	public static final int		DEFAULTTIMEOUT										= 100;

	// cli result
	public static final byte	CLIRESULT_SUCCESS									= 0;

	public static final byte	CLIRESULT_FAIL										= 1;

	public static final byte	CLIRESULT_QUEUEFULL									= 2;

	/**
	 * WTP Event Control.
	 */
	public static final int		WTP_EVENT_DISABLE									= 0;

	public static final int		WTP_EVENT_ENABLE									= 1;

	/**
	 * ap neighbor flag
	 */
	public static final int		NEIGHBORFLAG_INCLUDE								= 0X400;

	public static final int		NEIGHBORFLAG_EXCLUDE								= 0X800;

	/**
	 * statistics table id<br>
	 * 1:AhNeighbor<br>
	 * 2:AhXIf<br>
	 * 3:AhAssociation<br>
	 * 4:AhRadioStates<br>
	 * 5:AhVifStats<br>
	 * 6:AhRadioAttribute<br>
	 * 0:all table<br>
	 */
	public static final byte	STATTABLE_AHNEIGHBOR								= 1;

	public static final byte	STATTABLE_AHXIF										= 2;

	public static final byte	STATTABLE_AHASSOCIATION								= 3;

	public static final byte	STATTABLE_AHRADIOSTATES								= 4;

	public static final byte	STATTABLE_AHVIFSTATS								= 5;

	public static final byte	STATTABLE_AHRADIOATTRIBUTE							= 6;
	
	public static final byte	STATTABLE_AHETHCLIENT								= 7;

	public static final byte	STATTABLE_ALLTABLE									= 0;

	/**
	 * Ap Type.
	 */
	public static final byte	AP_TYPE_MP											= 0;
	public static final byte	AP_TYPE_PORTAL										= 1;
	public static final byte	AP_TYPE_UNKNOWN										= -1;

	/**
	 * IDP message type
	 */
	public static final int		IDP_MSG_QUERY										= 0;
	public static final int		IDP_MSG_REPORT										= 1;

	/**
	 * IDP removed flag
	 */
	public static final byte	IDP_FLAG_REMOVED									= 1;
	public static final byte	IDP_FLAG_ADD										= 0;

	/**
	 * IDP type
	 */
	public static final short	IDP_TYPE_ROGUE										= 1;
	public static final short	IDP_TYPE_VALID										= 2;
	public static final short	IDP_TYPE_EXTERNAL									= 3;

	/**
	 * IDP Station type
	 */

	public static final short	IDP_STATION_TYPE_AP									= 1;
	public static final short	IDP_STATION_TYPE_CLIENT								= 2;

	/**
	 * define IDP In-net attribute.
	 */
	public static final short	IDP_CONNECTION_NOT_SURE								= 0;
	public static final short	IDP_CONNECTION_IN_NET								= 1;

	/**
	 * dtls state
	 */
	public static final byte	DTLSSTATE_NODTLS									= 0;
	public static final byte	DTLSSTATE_USEDTLS									= 1;

	/**
	 * dtls operation type
	 */
	public static final byte	DTLSOPERTYPE_REMOVEAPCONNECTION						= 0;
	public static final byte	DTLSOPERTYPE_NOTREMOVEAPCONNECTION					= 1;

	/**
	 * passphrase state
	 */
	public static final byte	PASSPHRASESTATE_DEFAULT								= 0;
	public static final byte	PASSPHRASESTATE_USERDEFINEBOOTSTRAP					= 1;
	public static final byte	PASSPHRASESTATE_USERDEFINEPASSPHRASE				= 2;

	/**
	 * failed when send request, return this serial number
	 */
	public static final int		SERIALNUM_SENDREQUESTFAILED							= -1;

	/**
	 * error code defination for request
	 */
	public static final int		ERRORCODE_REQUEST_NOERROR							= 0;

	public static final int		ERRORCODE_REQUEST_CAPWAPDISCONNECT					= -1;

	public static final int		ERRORCODE_REQUEST_RESOUCEBUSY						= -2;

	// compress flag
	public static final byte	COMPRESS											= 1;

	public static final byte	NOTCOMPRESS											= 0;

	// capwap client event query type
	public static final short	CAPWAPCLIENTEVENT_TYPE_IDPMITIGATION				= 1;

	public static final short	CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK				= 2;

	public static final short	CAPWAPCLIENTEVENT_TYPE_SHOWCAPTURESTATUS			= 3;

	public static final short	CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING				= 4;

	public static final short	CAPWAPCLIENTEVENT_TYPE_VLANPROBE					= 5;

	public static final short	CAPWAPCLIENTEVENT_TYPE_PATHPROBE					= 6;

	public static final short	CAPWAPCLIENTEVENT_TYPE_SHOWACSPSTATS				= 7;

	public static final short	CAPWAPCLIENTEVENT_TYPE_POESTATUS					= 8;

	public static final short	CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP				= 9;

	public static final short	CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO					= 10;

	public static final short	CAPWAPCLIENTEVENT_TYPE_HIVECOMM						= 11;

	public static final short	CAPWAPCLIENTEVENT_TYPE_VPNSTATUS					= 12;

	public static final short	CAPWAPCLIENTEVENT_TYPE_PCIDATA						= 13;

	public static final short	CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT				= 14;

	public static final short	CAPWAPCLIENTEVENT_TYPE_AAATEST						= 15;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_TEACHERVIEW_STUDENTNOTFOUND	= 16;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO				= 17;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO					= 18;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_QUERYADINFO					= 19;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_STUDENTREPORT				= 20;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_SPECTRALANALYSIS				= 21;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_DATACOLLECTIONINFO			= 22;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_TV_CLIENT_REMOVE_INFOM		= 23;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_CLIENT_SELFREGISTER_INFO		= 24;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY			= 26;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_NAAS_LICENSE					= 27;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_MITIGATION_ARBITRATOR        = 28;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_AUTO_MITIGATION              = 29;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_PSE_STATUS                   = 30;
	
	public static final short   CAPWAPCLIENTEVENT_TYPE_OTP                          = 31;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION			= 32;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE			= 33;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY              = 34;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_REVOKEN        	= 35;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_APPREPORTCOLLECTIONINFO     	= 36;
	
	public static final short   CAPWAPCLIENTEVENT_TYPE_PRESENCE						= 37;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_INFO				= 38;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS			= 39;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO			= 40;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_APPLICATION_FLOW_INFO     	= 41;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_L7_SIGNATURE_FILE_VERSION	= 42;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_ROUTER_LTE_VZ_INFO			= 43;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS_REPORT		= 44;
	
	public static final short	CAPWAPCLIENTEVENT_TYPE_RADSEC_PROXY_INFO			= 47;
	
	// capwap client type
	public static final byte	CAPWAPCLIENTTYPE_AP									= 0;

	public static final byte	CAPWAPCLIENTTYPE_HM									= 10;

	public static final byte	CAPWAPCLIENTTYPE_HMOL								= 11;

	public static final byte	CAPWAPCLIENTTYPE_STAGINGSERVER						= 12;

	public static final byte	CAPWAPCLIENTTYPE_PORTAL								= 13;
	
	public static final byte	CAPWAPCLIENTTYPE_STUDENTMANAGER						= 14;
}
