package com.ah.be.common;

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.performance.AhClientSession;

public class AhConvertBOToSQL {
	
	final public static String[] CLIENTSESSION_FIELDS_ARRAY = new String[]{"id","apmac","apname","apserialnumber",
			"clientauthmethod","clientbssid","clientcwpused","clientchannel","clientencryptionmethod"
			,"clienthostname","clientip","clientmacprotocol","clientmac","clientssid",
			"clientuserprofid","clientusername","clientvlan","comment1","comment2","connectstate",
			"ifindex","mapid","memo","bandwidthsentinelstatus","starttimestamp","endtimestamp",
			"starttimezone","endtimezone","simulated","slaConnectScore","ipNetworkConnectivityScore",
			"applicationHealthScore","overallClientHealthScore","ifName","clientOsInfo","os_option55","clientrssi",
			"email","companyName","wirelessclient","owner", "userProfileName", "SNR","clientMacBasedAuthUsed", "managedStatus"};

	public static String CLIENTSESSION_FIELDS = null;
	public static String CLIENTSESSION_SQL = null;//"insert into ah_clientsession(apmac,apname,apserialnumber,clientauthmethod,clientbssid,clientcwpused,clientchannel,clientencryptionmethod,clienthostname,clientip,clientmacprotocol,clientmac,clientssid,clientuserprofid,clientusername,clientvlan,comment1,comment2,connectstate,ifindex,mapid,memo,bandwidthsentinelstatus,starttimestamp,endtimestamp,starttimezone,endtimezone,simulated,slaConnectScore,ipNetworkConnectivityScore,applicationHealthScore,overallClientHealthScore,ifName,clientOsInfo,owner) ";

	//final public static String ASSOCIATION_SQL = "insert into hm_association(apmac,apname,apserialnumber,clientassociatetime,clientauthmethod,clientbssid,clientcwpused,clientchannel,clientencryptionmethod,clienthostname,clientip,clientlastrxrate,clientlasttxrate,clientlinkuptime,clientmacprotocol,clientmac,clientrssi,clientrxairtime,clientrxbroadcastframes,clientrxdataframes,clientrxdataoctets,clientrxmicfailures,clientrxmgtframes,clientrxmulticastframes,clientrxunicastframes,clientssid,clienttxairtime,clienttxbedataframes,clienttxbgdataframes,clienttxbroadcastframes,clienttxdataframes,clienttxdataoctets,clienttxmgtframes,clienttxmulticastframes,clienttxunicastframes,clienttxvidataframes,clienttxvodataframes,clientuserprofid,clientusername,clientvlan,ifindex,time,time_zone,ifName,clientOsInfo,owner) ";

	//final public static String NEIGHBOR_SQL = "insert into hm_neighbor(apmac,apname,apserialnumber,ifindex,linkcost,linktype,linkuptime,neighborapid,rssi,rxbroadcastframes,rxdataframes,rxdataoctets,rxmgtframes,rxmulticastframes,rxunicastframes,txbedataframes,txbgdataframes,txbroadcastframes,txdataframes,txdataoctets,txmgtframes,txmulticastframes,txunicastframes,txvidataframes,txvodataframes,time,time_zone,owner) ";

//	final public static String EVENT_SQL = "insert into ah_event(apid,apname,code,objectname,trapdesc,owner,associationtime,clientauthmethod,clientbssid,clientcwpused,clientchannel,clientencryptionmethod,clienthostname,clientip,clientmacprotocol,clientusername,clientuserprofid,clientvlan,curvalue,currentstate,eventtype,tag1,ifindex,objecttype,poeeth0maxspeed,poeeth0on,poeeth0pwr,poeeth1maxspeed,poeeth1on,poeeth1pwr,poewifi0setting,poewifi1setting,poewifi2setting,powersource,previousstate,radiochannel,radiotxpower,remoteid,ssid,thresholdhigh,thresholdlow,asReportType,asNameType,asName,asSourceType,asSourceID,as_time,as_time_zone,asRuleName,asInstanceID,alertType,thresholdValue,shorttermValue,snapshotValue,time,time_zone) ";

//	final public static String ACSPNEIGHBOR_SQL = "insert into hm_acspneighbor(apmac,ifindex,bssid,neighbormac,neighborRadioMac,lastseen,channelnumber,txpower,rssi,ssid,time,time_zone,owner) ";

	//final public static String INTERFERENCEMAP_SQL = "insert into hm_interferencestats(apmac,apName,ifName,ifindex,channelNumber,interferenceCUThreshold,crcErrorRateThreshold,severity,averageTXCU,averageRXCU,averageInterferenceCU,averageNoiseFloor,shortTermTXCU,shortTermRXCU,shortTermInterferenceCU,shortTermNoiseFloor,snapShotTXCU,snapShotRXCU,snapShotInterferenceCU,snapShotNoiseFloor,crcError,time,time_zone,owner) ";

//	final public static String BANDWIDTHSENTINELHISTORY_SQL = "insert into hm_bandwidthsentinel_history(apmac,apName,clientmac,ifindex,guaranteedbandwidth,bandwidthsentinelstatus,actualbandwidth,action,time,time_zone,owner) ";

	//final public static String INTERFACESTATS_SQL = "insert into hm_interface_stats(apmac,apName,timeStamp,collectPeriod,ifIndex,ifName,txDrops,rxDrops,crcErrorRate,txRetryRate,rxRetryRate,uniTxFrameCount,uniRxFrameCount,bcastTxFrameCount,bcastRxFrameCount,totalChannelUtilization,interferenceUtilization,txUtilization,rxUtilization,noiseFloor,txAirTime,rxAirTime,txRateInfo,rxRateInfo,alarmFlag,bandSteerSuppressCount,loadBalanceSuppressCount,weakSnrSuppressCount,safetyNetAnswerCount,probeRequestSuppressCount,authRequestSuppressCount,txByteCount,rxByteCount,totalTxBitSuccessRate,totalRxBitSuccessRate,owner) ";

	//final public static String CLIENTSTATS_SQL = "insert into hm_client_stats(apmac,apName,timeStamp,collectPeriod,ifIndex,clientMac,ssidName,slaConnectScore,bandWidthUsage,slaViolationTraps,txFrameDropped,rxFrameDropped,txFrameCount,txFrameByteCount,rxFrameCount,rxFrameByteCount,averageSNR,powerSaveModeTimes,txAirTime,rxAirTime,txRateInfo,rxRateInfo,alarmflag,ipNetworkConnectivityScore,applicationHealthScore,overallClientHealthScore,totalTxBitSuccessRate,totalRxBitSuccessRate,owner) ";

	//final public static String AP_CONNECT_HISTORY_SQL = "insert into ap_connect_history_info(apid,apname,mapid,trapmessage,traptime,traptype,owner) ";

	static{
		int i = 0;
		StringBuffer buffer = new StringBuffer();
		for(i = 1; i < CLIENTSESSION_FIELDS_ARRAY.length; i++) {
			buffer.append(CLIENTSESSION_FIELDS_ARRAY[i]);
			if(i != CLIENTSESSION_FIELDS_ARRAY.length - 1)
				buffer.append(",");
		}
		CLIENTSESSION_SQL = "insert into ah_clientsession(" + buffer.toString() +") ";
		CLIENTSESSION_FIELDS = buffer.toString();
	}
	/**
	 * convert client session list to SQL
	 */
	static public String convertClientSessionToSQL(Collection<? extends HmBo> boList) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(CLIENTSESSION_SQL);
		boolean first = true;
		for (HmBo bo : boList) {
			if (bo instanceof AhClientSession) {
				if (!first) {
					buffer.append(" , ");
				}
				else
				{
					buffer.append(" values ");
				}
				buffer.append(convertClientSessionToSQL((AhClientSession) bo).toString());
				first = false;
			}
		}
		buffer.append(";");
		if (first) {
			return "";
		} else {
			return buffer.toString();//.replaceAll(Matcher.quoteReplacement("\\"),
					//Matcher.quoteReplacement("\\\\"));
		}
	}

	/**
	 * convert client session to SQL
	 */
	static public String convertClientSessionToSQL(AhClientSession bo) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("( ");
		// apmac,apname
		if (null != bo.getApMac()) {
			buffer.append("'").append(bo.getApMac()).append("',");
		} else {
			buffer.append("null,");
		}
		if (null != bo.getApName()) {
			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		// apserialnumber,clientauthmethod
		if (null != bo.getApSerialNumber()) {
			buffer.append("'").append(bo.getApSerialNumber()).append("',");
		} else {
			buffer.append("null,");
		}
		buffer.append(bo.getClientAuthMethod()).append(",");
		// clientbssid,clientcwpused
		if (null != bo.getClientBSSID()) {
			buffer.append("'").append(bo.getClientBSSID()).append("',");
		} else {
			buffer.append("null,");
		}
		buffer.append(bo.getClientCWPUsed()).append(",");
		// clientchannel,clientencryptionmethod
		buffer.append(bo.getClientChannel()).append(",").append(bo.getClientEncryptionMethod())
				.append(",");
		// clienthostname,clientip
		if (null != bo.getClientHostname()) {
			buffer.append("'").append(bo.getClientHostname().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		if (null != bo.getClientIP()) {
			buffer.append("'").append(bo.getClientIP()).append("',");
		} else {
			buffer.append("null,");
		}
		// clientmacprotocol,clientmac
		buffer.append(bo.getClientMACProtocol()).append(",");
		if (null != bo.getClientMac()) {
			buffer.append("'").append(bo.getClientMac()).append("',");
		} else {
			buffer.append("null,");
		}
		// clientssid,clientuserprofid
		if (null != bo.getClientSSID()) {
			buffer.append("'").append(bo.getClientSSID().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		buffer.append(bo.getClientUserProfId()).append(",");
		// clientusername,clientvlan
		if (null != bo.getClientUsername()) {
			buffer.append("'").append(bo.getClientUsername().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		buffer.append(bo.getClientVLAN()).append(",");
		// comment1,comment2
		if (null != bo.getComment1()) {
			buffer.append("'").append(bo.getComment1().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		if (null != bo.getComment2()) {
			buffer.append("'").append(bo.getComment2().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		// connectstate
		buffer.append(bo.getConnectstate()).append(",");

		// ifindex,mapid
		buffer.append(bo.getIfIndex()).append(",");
		if (null != bo.getMapId()) {
			buffer.append(bo.getMapId()).append(",");
		} else {
			buffer.append("CAST(null as bigint),");
		}
		// memo
		if (null != bo.getMemo()) {
			buffer.append("'").append(bo.getMemo().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}

		buffer.append(bo.getBandWidthSentinelStatus()).append(",");

		// start time, end time
		buffer.append(bo.getStartTimeStamp()).append(",").append(bo.getEndTimeStamp()).append(",'")
				.append(bo.getStartTimeZone()).append("','").append(bo.getEndTimeZone()).append(
						"',");

		// simulated
		buffer.append(bo.isSimulated()).append(",").append(bo.getSlaConnectScore()).append(",");
		
		buffer.append(bo.getIpNetworkConnectivityScore()).append(",").append(bo.getApplicationHealthScore()).append(",").append(bo.getOverallClientHealthScore()).append(",");
		
		// ifName
		if (null != bo.getIfName()) {
			buffer.append("'").append(bo.getIfName().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		
		// clientOsInfo
		if (null != bo.getClientOsInfo()) {
			buffer.append("'").append(bo.getClientOsInfo().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		if (null != bo.getOs_option55() && !(bo.getOs_option55().equalsIgnoreCase(""))) {
			buffer.append("'").append(bo.getOs_option55().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		
		//clientrssi
		buffer.append(bo.getClientRssi()).append(",");
		
		// email
		if (null != bo.getEmail()) {
			buffer.append("'").append(bo.getEmail().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		
		// company name
		if (null != bo.getCompanyName()) {
			buffer.append("'").append(bo.getCompanyName().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		
		//wirelessclient
		buffer.append(bo.isWirelessClient()).append(",");
		
		// owner
		if (null != bo.getOwner()) {
			buffer.append(bo.getOwner().getId()).append(",");
		} else {
			buffer.append("CAST(null as bigint),");
		}
		
		// userprofilename
		if (null != bo.getUserProfileName()) {
			buffer.append("'").append(bo.getUserProfileName().replaceAll("'", "''")).append("',");
		} else {
			buffer.append("null,");
		}
		
		// SNR
		buffer.append(bo.getSNR());
		
		// clientMacBasedAuthUsed
		buffer.append(",").append(bo.getClientMacBasedAuthUsed());
		
		// client managed status
		buffer.append(",").append(bo.getManagedStatus());

		buffer.append(") ");
		return buffer.toString();
	}

	/**
	 * convert association list to SQL
	 */
//	static public String convertAssociationToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(ASSOCIATION_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhAssociation) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertAssociationToSQL((AhAssociation) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	/**
//	 * convert association to SQL
//	 */
//	static public String convertAssociationToSQL(AhAssociation bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		// apmac,apname
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// apserialnumber,clientassociatetime,clientauthmethod
//		if (null != bo.getApSerialNumber()) {
//			buffer.append("'").append(bo.getApSerialNumber()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		buffer.append(bo.getClientAssociateTime()).append(",").append(bo.getClientAuthMethod())
//				.append(",");
//		// clientbssid,clientcwpused
//		if (null != bo.getClientBSSID()) {
//			buffer.append("'").append(bo.getClientBSSID()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		buffer.append(bo.getClientCWPUsed()).append(",");
//		// clientchannel,clientencryptionmethod
//		buffer.append(bo.getClientChannel()).append(",").append(bo.getClientEncryptionMethod())
//				.append(",");
//		// clienthostname,clientip
//		if (null != bo.getClientHostname()) {
//			buffer.append("'").append(bo.getClientHostname().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		if (null != bo.getClientIP()) {
//			buffer.append("'").append(bo.getClientIP()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// clientlastrxrate,clientlasttxrate,clientlinkuptime
//		buffer.append(bo.getClientLastRxRate()).append(",").append(bo.getClientLastTxRate())
//				.append(",").append(bo.getClientLinkUptime()).append(",");
//		// clientmacprotocol,clientmac
//		buffer.append(bo.getClientMACProtocol()).append(",");
//		if (null != bo.getClientMac()) {
//			buffer.append("'").append(bo.getClientMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// clientrssi,clientrxairtime
//		buffer.append(bo.getClientRSSI()).append(",").append(bo.getClientRxAirtime()).append(",");
//		// clientrxbroadcastframes,clientrxdataframes
//		buffer.append(bo.getClientRxBroadcastFrames()).append(",").append(
//				bo.getClientRxDataFrames()).append(",");
//		// clientrxdataoctets,clientrxmicfailures
//		buffer.append(bo.getClientRxDataOctets()).append(",").append(bo.getClientRxMICFailures())
//				.append(",");
//		// clientrxmgtframes,clientrxmulticastframes
//		buffer.append(bo.getClientRxMgtFrames()).append(",")
//				.append(bo.getClientRxMulticastFrames()).append(",");
//		// clientrxunicastframes,clientssid
//		buffer.append(bo.getClientRxUnicastFrames()).append(",");
//		if (null != bo.getClientSSID()) {
//			buffer.append("'").append(bo.getClientSSID().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// clienttxairtime,clienttxbedataframes
//		buffer.append(bo.getClientTxAirtime()).append(",").append(bo.getClientTxBeDataFrames())
//				.append(",");
//		// clienttxbgdataframes,clienttxbroadcastframes
//		buffer.append(bo.getClientTxBgDataFrames()).append(",").append(
//				bo.getClientTxBroadcastFrames()).append(",");
//		// clienttxdataframes,clienttxdataoctets
//		buffer.append(bo.getClientTxDataFrames()).append(",").append(bo.getClientTxDataOctets())
//				.append(",");
//		// clienttxmgtframes,clienttxmulticastframes
//		buffer.append(bo.getClientTxMgtFrames()).append(",")
//				.append(bo.getClientTxMulticastFrames()).append(",");
//		// clienttxunicastframes,clienttxvidataframes
//		buffer.append(bo.getClientTxUnicastFrames()).append(",").append(
//				bo.getClientTxViDataFrames()).append(",");
//		// clienttxvodataframes,clientuserprofid
//		buffer.append(bo.getClientTxVoDataFrames()).append(",").append(bo.getClientUserProfId())
//				.append(",");
//		// clientusername,clientvlan
//		if (null != bo.getClientUsername()) {
//			buffer.append("'").append(bo.getClientUsername().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		buffer.append(bo.getClientVLAN()).append(",");
//		// ifindex,stattime
//		buffer.append(bo.getIfIndex()).append(",").append(bo.getTimeStamp().getTime()).append(",'")
//				.append(bo.getTimeStamp().getTimeZone()).append("',");
//		// ifName
//		if (null != bo.getIfName()) {
//			buffer.append("'").append(bo.getIfName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		
//		// clientOsInfo
//		if (null != bo.getClientOsInfo()) {
//			buffer.append("'").append(bo.getClientOsInfo().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}
//
//	/**
//	 * convert neighbor list to SQL
//	 */
//	static public String convertNeighborToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(NEIGHBOR_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhNeighbor) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertNeighborToSQL((AhNeighbor) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	/**
//	 * convert neighbor to SQL
//	 */
//	static public String convertNeighborToSQL(AhNeighbor bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		// apmac,apname,apserialnumber
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		if (null != bo.getApSerialNumber()) {
//			buffer.append("'").append(bo.getApSerialNumber()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// ifindex,linkcost,linktype,linkuptime,neighborapid,rssi
//		buffer.append(bo.getIfIndex()).append(",").append(bo.getLinkCost()).append(",").append(
//				bo.getLinkType()).append(",").append(bo.getLinkUpTime()).append(",");
//
//		if (null != bo.getNeighborAPID()) {
//			buffer.append("'").append(bo.getNeighborAPID()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getRssi()).append(",");
//
//		// rxbroadcastframes,rxdataframes,rxdataoctets,rxmgtframes,rxmulticastframes,rxunicastframes
//		buffer.append(bo.getRxBroadcastFrames()).append(",").append(bo.getRxDataFrames()).append(
//				",").append(bo.getRxDataOctets()).append(",").append(bo.getRxMgtFrames()).append(
//				",").append(bo.getRxMulticastFrames()).append(",").append(bo.getRxUnicastFrames())
//				.append(",");
//
//		// stattime
//		// if (null != bo.getStatTimeValue())
//		// buffer.append("timestamp '").append(
//		// AhDateTimeUtil.getFormatDateTime(bo.getStatTimeValue(),
//		// TimeZone.getDefault())).append("',");
//		// else
//		// buffer.append("CAST(null as timestamp),");
//
//		// txbedataframes,txbgdataframes,txbroadcastframes,txdataframes,txdataoctets,txmgtframes,txmulticastframes,txunicastframes,txvidataframes,txvodataframes
//		buffer.append(bo.getTxBeDataFrames()).append(",").append(bo.getTxBgDataFrames())
//				.append(",").append(bo.getTxBroadcastFrames()).append(",").append(
//						bo.getTxDataFrames()).append(",").append(bo.getTxDataOctets()).append(",")
//				.append(bo.getTxMgtFrames()).append(",").append(bo.getTxMulticastFrames()).append(
//						",").append(bo.getTxUnicastFrames()).append(",").append(
//						bo.getTxViDataFrames()).append(",").append(bo.getTxVoDataFrames()).append(
//						",");
//
//		buffer.append(bo.getTimeStamp().getTime()).append(",'").append(
//				bo.getTimeStamp().getTimeZone()).append("',");
//
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

	/**
	 * convert AhACSPNeighbor list to SQL
	 */
//	static public String convertACSPNeighborToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(ACSPNEIGHBOR_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhACSPNeighbor) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertACSPNeighborToSQL((AhACSPNeighbor) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	/**
//	 * convert AhACSPNeighbor to SQL
//	 */
//	static public String convertACSPNeighborToSQL(AhACSPNeighbor bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		// apmac
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// ifindex
//		buffer.append(bo.getIfIndex()).append(",");
//
//		// bssid
//		if (null != bo.getBssid()) {
//			buffer.append("'").append(bo.getBssid()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// neighbormac
//		if (null != bo.getNeighborMac()) {
//			buffer.append("'").append(bo.getNeighborMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// neighborradiomac
//		if (null != bo.getNeighborRadioMac()) {
//			buffer.append("'").append(bo.getNeighborRadioMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// last seen
//		// if (null != bo.getLastSeen())
//		// buffer.append("timestamp '").append(
//		// AhDateTimeUtil.getSpecifyDateTime(bo.getLastSeen(), TimeZone.getDefault()))
//		// .append("',");
//		// else
//		// buffer.append("CAST(null as timestamp),");
//		buffer.append(bo.getLastSeen()).append(",");
//
//		// channel number, txpower, rssi
//		buffer.append(bo.getChannelNumber()).append(",").append(bo.getTxPower()).append(",")
//				.append(bo.getRssi()).append(",");
//
//		// ssid
//		if (null != bo.getSsid()) {
//			// buffer.append("'").append(bo.getSsid()).append("',");
//			buffer.append("'").append(changeUnPrintableString(bo.getSsid()).replaceAll("'", "''"))
//					.append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getTimeStamp().getTime()).append(",'").append(
//				bo.getTimeStamp().getTimeZone()).append("',");
//
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

	/**
	 * convert AhInterferenceStats list to SQL
	 */
//	static public String convertInterferenceToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(INTERFERENCEMAP_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhInterferenceStats) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertInterferenceToSQL((AhInterferenceStats) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	/**
//	 * convert AhInterferenceStats to SQL
//	 */
//	static public String convertInterferenceToSQL(AhInterferenceStats bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		// apmac
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getIfName()) {
//			buffer.append("'").append(bo.getIfName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// ifindex,channelNumber,averageTXCU,averageRXCU,averageInterferenceCU,averageNoiseFloor,shortTermTXCU,
//		// shortTermRXCU,shortTermInterferenceCU,shortTermNoiseFloor,snapShotTXCU,snapShotRXCU,snapShotInterferenceCU,
//		// snapShotNoiseFloor,crcError
//		buffer.append(bo.getIfIndex()).append(",").append(bo.getChannelNumber()).append(",")
//				.append(bo.getInterferenceCUThreshold()).append(",").append(
//						bo.getCrcErrorRateThreshold()).append(",").append(bo.getSeverity()).append(
//						",").append(bo.getAverageTXCU()).append(",").append(bo.getAverageRXCU())
//				.append(",").append(bo.getAverageInterferenceCU()).append(",").append(
//						bo.getAverageNoiseFloor()).append(",").append(bo.getShortTermTXCU())
//				.append(",").append(bo.getShortTermRXCU()).append(",").append(
//						bo.getShortTermInterferenceCU()).append(",").append(
//						bo.getShortTermNoiseFloor()).append(",").append(bo.getSnapShotTXCU())
//				.append(",").append(bo.getSnapShotRXCU()).append(",").append(
//						bo.getSnapShotInterferenceCU()).append(",").append(
//						bo.getSnapShotNoiseFloor()).append(",").append(bo.getCrcError())
//				.append(",");
//
//		buffer.append(bo.getTimeStamp().getTime()).append(",'").append(
//				bo.getTimeStamp().getTimeZone()).append("',");
//
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

//	public static String convertEventToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer(4096);
//
//		buffer.append(EVENT_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhEvent) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertEventToSQL((AhEvent) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	public static String convertEventToSQL(AhEvent bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//
//		// field 1 xx;
//		if (null != bo.getApId()) {
//			buffer.append("'").append(bo.getApId()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 2 xx;
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 3 xx;
//		buffer.append(bo.getCode()).append(",");
//		// field 4 xx;
//		if (null != bo.getObjectName()) {
//			buffer.append("'").append(bo.getObjectName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 5 xx;
//		if (null != bo.getTrapDesc()) {
//			buffer.append("'").append(bo.getTrapDesc().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 6 trapTime, remove by jun on 0615;
//		// field 7 owner;
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId()).append(",");
//		} else {
//			buffer.append("CAST(null as bigint),");
//		}
//		// field 8 xx;
//		buffer.append(bo.getAssociationTime()).append(",");
//		// field 9 xx;
//		buffer.append(bo.getClientAuthMethod()).append(",");
//		// field 10 xx;
//		if (null != bo.getClientBSSID()) {
//			buffer.append("'").append(bo.getClientBSSID()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 11 xx;
//		buffer.append(bo.getClientCWPUsed()).append(",");
//		// field 12 xx;
//		buffer.append(bo.getClientChannel()).append(",");
//		// field 13 xx;
//		buffer.append(bo.getClientEncryptionMethod()).append(",");
//		// field 14 xx;
//		if (null != bo.getClientHostName()) {
//			buffer.append("'").append(bo.getClientHostName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 15 xx;
//		if (null != bo.getClientIp()) {
//			buffer.append("'").append(bo.getClientIp()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 16 xx;
//		buffer.append(bo.getClientMacProtocol()).append(",");
//		// field 17 xx;
//		if (null != bo.getClientUserName()) {
//			buffer.append("'").append(bo.getClientUserName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 18 xx;
//		buffer.append(bo.getClientUserProfId()).append(",");
//		// field 19 xx;
//		buffer.append(bo.getClientVLAN()).append(",");
//		// field 20 xx;
//		buffer.append(bo.getCurValue()).append(",");
//		// field 21 xx;
//		buffer.append(bo.getCurrentState()).append(",");
//		// field 22 xx;
//		buffer.append(bo.getEventType()).append(",");
//		// tag1
//		if (null != bo.getTag1()) {
//			buffer.append("'").append(bo.getTag1()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 23 xx;
//		buffer.append(bo.getIfIndex()).append(",");
//		// field 24 xx;
//		buffer.append(bo.getObjectType()).append(",");
//		// field 25 xx;
//		buffer.append(bo.getPoEEth0MaxSpeed()).append(",");
//		// field 26 xx;
//		buffer.append(bo.getPoEEth0On()).append(",");
//		// field 27 xx;
//		buffer.append(bo.getPoEEth0Pwr()).append(",");
//		// field 28 xx;
//		buffer.append(bo.getPoEEth1MaxSpeed()).append(",");
//		// field 29 xx;
//		buffer.append(bo.getPoEEth1On()).append(",");
//		// field 30 xx;
//		buffer.append(bo.getPoEEth1Pwr()).append(",");
//		// field 31 xx;
//		buffer.append(bo.getPoEWifi0Setting()).append(",");
//		// field 32 xx;
//		buffer.append(bo.getPoEWifi1Setting()).append(",");
//		// field 33 xx;
//		buffer.append(bo.getPoEWifi2Setting()).append(",");
//		// field 34 xx;
//		buffer.append(bo.getPowerSource()).append(",");
//		// field 35 xx;
//		buffer.append(bo.getPreviousState()).append(",");
//		// field 36 xx;
//		buffer.append(bo.getRadioChannel()).append(",");
//		// field 37 xx;
//		buffer.append(bo.getRadioTxPower()).append(",");
//		// field 38 xx;
//		if (null != bo.getRemoteId()) {
//			buffer.append("'").append(bo.getRemoteId()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 39 xx;
//		if (null != bo.getSsid()) {
//			buffer.append("'").append(bo.getSsid().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 40 xx;
//		buffer.append(bo.getThresholdHigh()).append(",");
//		// field 41 xx;
//		buffer.append(bo.getThresholdLow()).append(",");
//
//		// field as.1 xx;
//		buffer.append(bo.getAsReportType()).append(",");
//		// field as.2 xx;
//		buffer.append(bo.getAsNameType()).append(",");
//		// field as.3 xx;
//		if (null != bo.getAsName()) {
//			buffer.append("'").append(bo.getAsName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field as.4 xx;
//		buffer.append(bo.getAsSourceType()).append(",");
//		// field as.5 source;
//		if (null != bo.getAsSourceID()) {
//			buffer.append("'").append(bo.getAsSourceID()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// field as.6 asTime;
//		if (bo.getAsTimeStamp() == null) {
//			buffer.append("0,null,");
//		} else {
//			buffer.append(bo.getAsTimeStamp().getTime()).append(",");
//			if (null != bo.getAsTimeStamp().getTimeZone()) {
//				buffer.append("'").append(bo.getAsTimeStamp().getTimeZone()).append("',");
//			} else {
//				buffer.append("null,");
//			}
//		}
//
//		// field as.7 asRuleName;
//		if (null != bo.getAsRuleName()) {
//			buffer.append("'").append(bo.getAsRuleName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field as.8 asInstanceId;
//		buffer.append(bo.getAsInstanceID()).append(",").append(bo.getAlertType()).append(",")
//				.append(bo.getThresholdValue()).append(",").append(bo.getShorttermValue()).append(
//						",").append(bo.getSnapshotValue()).append(",");
//
//		// below added by jun on 2009.6.13
//		// field time, time_zone
//		if (bo.getTrapTimeStamp() == null) {
//			buffer.append("0,null");
//		} else {
//			buffer.append(bo.getTrapTimeStamp().getTime()).append(",");
//			if (null != bo.getTrapTimeStamp().getTimeZone()) {
//				buffer.append("'").append(bo.getTrapTimeStamp().getTimeZone()).append("' ");
//			} else {
//				buffer.append("null ");
//			}
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

//	public static String convertAPConnectHistoryToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer(4096);
//
//		buffer.append(AP_CONNECT_HISTORY_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof APConnectHistoryInfo) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertAPConnectHistoryToSQL((APConnectHistoryInfo) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	private static String convertAPConnectHistoryToSQL(APConnectHistoryInfo bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//
//		// field 1 xx;
//		if (null != bo.getApId()) {
//			buffer.append("'").append(bo.getApId()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 2 xx;
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 3 xx;
//		if (null != bo.getMapId()) {
//			buffer.append(bo.getMapId()).append(",");
//		} else {
//			buffer.append("CAST(null as bigint),");
//		}
//		// field 4 xx;
//		if (null != bo.getTrapMessage()) {
//			buffer.append("'").append(bo.getTrapMessage().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		// field 5 xx;
//		buffer.append(bo.getTrapTime()).append(",");
//		// field 6 xx;
//		buffer.append(bo.getTrapType()).append(",");
//		// field 7 owner;
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId()).append(" ");
//		} else {
//			buffer.append("CAST(null as bigint) ");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

	/**
	 * convert bandwidth history bo to sql
	 */
//	static public String convertBandWidthSentinelHistoryToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(BANDWIDTHSENTINELHISTORY_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhBandWidthSentinelHistory) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertBandWidthSentinelHistoryToSQL((AhBandWidthSentinelHistory) bo)
//						.toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	/**
//	 * convert bandwidth history bo to sql
//	 */
//	static public String convertBandWidthSentinelHistoryToSQL(AhBandWidthSentinelHistory bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		// apmac,apname,clientmac
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getClientMac()) {
//			buffer.append("'").append(bo.getClientMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		// ifindex,guaranteedbandwidth,bandwidthsentinelstatus,actualbandwidth,action
//		buffer.append(bo.getIfIndex()).append(",").append(bo.getGuaranteedBandWidth()).append(",")
//				.append(bo.getBandWidthSentinelStatus()).append(",")
//				.append(bo.getActualBandWidth()).append(",").append(bo.getAction()).append(",");
//
//		// time,timeszone
//		buffer.append(bo.getTimeStamp().getTime()).append(",'").append(
//				bo.getTimeStamp().getTimeZone()).append("',");
//
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

	// convert AhInterfaceStats
//	static public String convertInterfaceStatsToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(INTERFACESTATS_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhInterfaceStats) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertInterfaceStatsToSQL((AhInterfaceStats) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	// convert AhInterfaceStats
//	static public String convertInterfaceStatsToSQL(AhInterfaceStats bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getTimeStamp()).append(",").append(bo.getCollectPeriod()).append(",")
//				.append(bo.getIfIndex()).append(",");
//
//		if (null != bo.getIfName()) {
//			buffer.append("'").append(bo.getIfName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getTxDrops()).append(",").append(bo.getRxDrops()).append(",").append(
//				bo.getCrcErrorRate()).append(",").append(bo.getTxRetryRate()).append(",").append(
//				bo.getRxRetryRate()).append(",").append(bo.getUniTxFrameCount()).append(",")
//				.append(bo.getUniRxFrameCount()).append(",").append(bo.getBcastTxFrameCount())
//				.append(",").append(bo.getBcastRxFrameCount()).append(",").append(
//						bo.getTotalChannelUtilization()).append(",").append(
//						bo.getInterferenceUtilization()).append(",").append(bo.getTxUtilization())
//				.append(",").append(bo.getRxUtilization()).append(",").append(bo.getNoiseFloor())
//				.append(",").append(bo.getTxAirTime()).append(",").append(bo.getRxAirTime())
//				.append(",");
//
//		if (null != bo.getTxRateInfo()) {
//			buffer.append("'").append(bo.getTxRateInfo()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getRxRateInfo()) {
//			buffer.append("'").append(bo.getRxRateInfo()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getAlarmFlag()).append(",").append(bo.getBandSteerSuppressCount()).append(
//				",").append(bo.getLoadBalanceSuppressCount()).append(",").append(
//				bo.getWeakSnrSuppressCount()).append(",").append(bo.getSafetyNetAnswerCount())
//				.append(",").append(bo.getProbeRequestSuppressCount()).append(",").append(
//						bo.getAuthRequestSuppressCount()).append(",");
//
//		buffer.append(bo.getTxByteCount()).append(",").append(bo.getRxByteCount()).append(",").append(
//				bo.getTotalTxBitSuccessRate()).append(",").append(bo.getTotalRxBitSuccessRate()).append(",");
//				
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}
//
//	// convert AhClientStats
//	static public String convertClientStatsToSQL(Collection<? extends HmBo> boList) {
//		StringBuffer buffer = new StringBuffer();
//
//		buffer.append(CLIENTSTATS_SQL);
//		boolean first = true;
//		for (HmBo bo : boList) {
//			if (bo instanceof AhClientStats) {
//				if (!first) {
//					buffer.append(" union all ");
//				}
//				buffer.append(convertClientStatsToSQL((AhClientStats) bo).toString());
//				first = false;
//			}
//		}
//		buffer.append(";");
//		if (first) {
//			return "";
//		} else {
//			return buffer.toString().replaceAll(Matcher.quoteReplacement("\\"),
//					Matcher.quoteReplacement("\\\\"));
//		}
//	}
//
//	// convert AhClientStats
//	static public String convertClientStatsToSQL(AhClientStats bo) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("select ");
//		if (null != bo.getApMac()) {
//			buffer.append("'").append(bo.getApMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getApName()) {
//			buffer.append("'").append(bo.getApName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getTimeStamp()).append(",").append(bo.getCollectPeriod()).append(",")
//				.append(bo.getIfIndex()).append(",");
//
//		if (null != bo.getClientMac()) {
//			buffer.append("'").append(bo.getClientMac()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getSsidName()) {
//			buffer.append("'").append(bo.getSsidName().replaceAll("'", "''")).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		buffer.append(bo.getSlaConnectScore()).append(",").append(bo.getBandWidthUsage()).append(
//				",").append(bo.getSlaViolationTraps()).append(",").append(bo.getTxFrameDropped())
//				.append(",").append(bo.getRxFrameDropped()).append(",")
//				.append(bo.getTxFrameCount()).append(",").append(bo.getTxFrameByteCount()).append(
//						",").append(bo.getRxFrameCount()).append(",").append(
//						bo.getRxFrameByteCount()).append(",").append(bo.getAverageSNR())
//				.append(",").append(bo.getPowerSaveModeTimes()).append(",").append(
//						bo.getTxAirTime()).append(",").append(bo.getRxAirTime()).append(",");
//
//		if (null != bo.getTxRateInfo()) {
//			buffer.append("'").append(bo.getTxRateInfo()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//
//		if (null != bo.getRxRateInfo()) {
//			buffer.append("'").append(bo.getRxRateInfo()).append("',");
//		} else {
//			buffer.append("null,");
//		}
//		
//		buffer.append(bo.getAlarmFlag()).append(",");
//		
//		buffer.append(bo.getIpNetworkConnectivityScore()).append(",");
//		buffer.append(bo.getApplicationHealthScore()).append(",");
//		buffer.append(bo.getOverallClientHealthScore()).append(",");
//		
//		buffer.append(bo.getTotalTxBitSuccessRate()).append(",").append(bo.getTotalRxBitSuccessRate()).append(",");
//		
//		// owner
//		if (null != bo.getOwner()) {
//			buffer.append(bo.getOwner().getId());
//		} else {
//			buffer.append("CAST(null as bigint)");
//		}
//
//		buffer.append(" ");
//		return buffer.toString();
//	}

	public static String changeUnPrintableString(String msg) {
		if (null == msg)
			return null;
		byte[] buf = msg.getBytes();
		boolean isPrintable = true;
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] >= 0 && buf[i] <= 0X1F) {
				isPrintable = false;
			}
		}
		if (isPrintable)
			return msg;
		else
			return "Unprintable String";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
