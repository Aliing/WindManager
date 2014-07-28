/**
 *@filename		RestoreConfigTemplate.java
 *@version
 *@author		Fisher
 *@createtime	2007-11-7 PM 06:55:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.resource.BeResModule_CWPImpl;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmTableSize;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateSsidUserProfile;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApFilter;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.InterRoaming;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.mobility.TunnelSettingIPAddress;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosParams.FrameType;
import com.ah.bo.network.DosParams.ScreeningType;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.USBModem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhAlarmsFilter;
import com.ah.bo.performance.AhEventsFilter;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.CwpPageCustomization;
import com.ah.bo.wlan.CwpPageField;
import com.ah.bo.wlan.CwpPageMultiLanguageRes;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.SsidSecurity;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.bo.wlan.WalledGardenItem;
import com.ah.ui.actions.config.CwpAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.TextItem;
import com.ah.util.bo.BoGenerationUtil;

/**
 * @author		Fisher
 * @version		V1.0.0.0
 */
public class RestoreConfigTemplate extends RestoreVersionHelper {

    // for upgrade log
	//private static int issueCount=1;
	/*
	 * for Tunnel Policy
	 */
	private static boolean tunnelChangeFlag;
	private static final Map<String, Map<String, Set<String>>> allTunnelPolicy = new HashMap<String, Map<String, Set<String>>>();
	private static final Map<String, Map<String, Map<String, Set<String>>>> allTunnelInformation = new HashMap<String, Map<String, Map<String, Set<String>>>>();

	/*
	 * for Mgmt Service Option's Radius Assignment
	 */
	private static final Map<String, Map<String, String>> allOptionRadius = new HashMap<String, Map<String, String>>();

	/*
     * for track wan
     */
	private static Map<Long, Long> oldTrackWan = new HashMap<Long, Long>();
	/*
	 * for WLAN Policy
	 */
	private static boolean bln31to32flg;
	private static final Map<String,Boolean> updateSsid = new HashMap<String,Boolean>();
	private static int nameCount=1;
	private static final List<Map<String,String>> issueMapList = new ArrayList<Map<String,String>>();
	private static final Map<String,String> ssidWlanIDMapping = new HashMap<String,String>();

	private static boolean bln32r1to32r2flg=false;

	private static boolean beforeEdinburgh=false;

	private static boolean needRestoreConfigTemplateMDM=false;

	public static final String ETH0_ACCESS_PROFILE="1";
	public static final String ETH0_BRIDGE_PROFILE="2";
	public static final String ETH1_ACCESS_PROFILE="3";
	public static final String ETH1_BRIDGE_PROFILE="4";
	public static final String AGG0_ACCESS_PROFILE="5";
	public static final String AGG0_BRIDGE_PROFILE="6";
	public static final String RED0_ACCESS_PROFILE="7";
	public static final String RED0_BRIDGE_PROFILE="8";
	
	private static final boolean RESTORE_BEFORE_6_1_R4 = isRestoreBefore("6.1.4.0");
	private static List<Cwp> generateCwpList = null;

	private static List<DosParams> getAllDosparams() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of dos_prevention_dos_params.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("dos_prevention_dos_params");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in dos_prevention_dos_params table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DosParams> dosParamsInfo = new ArrayList<DosParams>();
		boolean isColPresent;
		String colName;
		DosParams dosParams;

		for (int i = 0; i < rowCount; i++)
		{
			dosParams = new DosParams();

			/**
			 * Set dos_prevention_id
			 */
			colName = "dos_prevention_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if("".equals(id))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention_dos_params' data be lost, cause: 'dos_prevention_id' column is not exist.");
				continue;
			}
			dosParams.setRestoreId(id.trim());

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colName);
			String mapkey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if("".equals(mapkey))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention_dos_params' data be lost, cause: 'mapkey' column is not exist.");
				continue;
			}

			/**
			 * Set enabled
			 */
			colName = "enabled";
			boolean isEnabled = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colName);
			String enabled;

			/**
			 * Set alarminterval
			 */
			String colNameInterval = "alarminterval";
			boolean isAlarminterval = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colNameInterval);
			String alarminterval;

			/**
			 * Set alarmthreshold
			 */
			String colNameThreshold = "alarmthreshold";
			boolean isAlarmthreshold = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colNameThreshold);
			String alarmthreshold;

			/**
			 * Set dosaction
			 */
			String colNameAction = "dosaction";
			boolean isDosaction = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colNameAction);
			String dosaction;

			/**
			 * Set dosactiontime
			 */
			String colNameActiontime = "dosactiontime";
			boolean isDosactiontime = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention_dos_params", colNameActiontime);
			String dosactiontime;

			if (mapkey.equals(String.valueOf(ScreeningType.ICMP_FLOOD))) {
				dosParams.setScreeningType(ScreeningType.ICMP_FLOOD);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "20";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(ScreeningType.UDP_FLOOD))) {
				dosParams.setScreeningType(ScreeningType.UDP_FLOOD);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "50";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(ScreeningType.SYN_FLOOD))) {
				dosParams.setScreeningType(ScreeningType.SYN_FLOOD);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "1000";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(ScreeningType.ARP_FLOOD))) {
				dosParams.setScreeningType(ScreeningType.ARP_FLOOD);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "100";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(ScreeningType.ADDRESS_SWEEP))) {
				dosParams.setScreeningType(ScreeningType.ADDRESS_SWEEP);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "100";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(ScreeningType.PORT_SCAN))) {
				dosParams.setScreeningType(ScreeningType.PORT_SCAN);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "100";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(ScreeningType.IP_SPOOF))) {
				dosParams.setScreeningType(ScreeningType.IP_SPOOF);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "3";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(ScreeningType.RADIUS_ATTACK))) {
				dosParams.setScreeningType(ScreeningType.RADIUS_ATTACK);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "0";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "5";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosaction = isDosaction ? xmlParser.getColVal(i, colNameAction) : "0";
				dosParams.setDosAction(AhRestoreCommons.convertStringToDosAction(dosaction));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "10";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(FrameType.PROBE_REQ))) {
				dosParams.setFrameType(FrameType.PROBE_REQ);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "1200";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "0";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(FrameType.PROBE_RESP))) {
				dosParams.setFrameType(FrameType.PROBE_RESP);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "2400";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "0";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(FrameType.ASSOC_REQ))) {
				dosParams.setFrameType(FrameType.ASSOC_REQ);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "240";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "60";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(FrameType.ASSOC_RESP))) {
				dosParams.setFrameType(FrameType.ASSOC_RESP);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "240";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "0";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(FrameType.DISASSOC))) {
				dosParams.setFrameType(FrameType.DISASSOC);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "120";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "0";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(FrameType.AUTH))) {
				dosParams.setFrameType(FrameType.AUTH);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "120";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "60";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if(mapkey.equals(String.valueOf(FrameType.DEAUTH))) {
				dosParams.setFrameType(FrameType.DEAUTH);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "120";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "0";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else if (mapkey.equals(String.valueOf(FrameType.EAPOL))) {
				dosParams.setFrameType(FrameType.EAPOL);
				alarminterval = isAlarminterval ? xmlParser.getColVal(i, colNameInterval) : "60";
				dosParams.setAlarmInterval(AhRestoreCommons.convertInt(alarminterval));
				alarmthreshold= isAlarmthreshold ? xmlParser.getColVal(i, colNameThreshold) : "600";
				dosParams.setAlarmThreshold(AhRestoreCommons.convertInt(alarmthreshold));
				dosactiontime = isDosactiontime ? xmlParser.getColVal(i, colNameActiontime) : "60";
				dosParams.setDosActionTime(AhRestoreCommons.convertInt(dosactiontime));
				enabled = isEnabled ? xmlParser.getColVal(i, colName) : "true";
				dosParams.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));
			} else {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention_dos_params' data be lost, cause: 'mapkey' column is not available.");
				continue;
			}

			dosParamsInfo.add(dosParams);
		}

		return dosParamsInfo;
	}

	private static List<DosPrevention> getAllDosPrevention() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of dos_prevention.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("dos_prevention");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in dos_prevention table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DosPrevention> dosPrevention = new ArrayList<DosPrevention>();
		boolean isColPresent;
		String colName;
		DosPrevention dosPreventionDTO;

		for (int i = 0; i < rowCount; i++)
		{
			dosPreventionDTO = new DosPrevention();

			/**
			 * Set dospreventionname
			 */
			colName = "dospreventionname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention", colName);
			if (!isColPresent)
			{
				/**
				 * The dospreventionname column must be exist in the table of
				 * ip_address
				 */
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention' data be lost, cause: 'dospreventionname' column is not exist.");
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention' data be lost, cause: 'dospreventionname' column is null.");
				continue;
			}
			dosPreventionDTO.setDosPreventionName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if("".equals(id))
			{
				continue;
			}
			dosPreventionDTO.setId(Long.valueOf(id));

			if (BeParaModule.DEFAULT_IP_DOS_NAME.equals(name.trim())
					|| BeParaModule.DEFAULT_MAC_DOS_NAME.equals(name.trim())
					|| BeParaModule.DEFAULT_MAC_DOS_STATION_NAME.equals(name.trim())) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dosPreventionName", name);
				DosPrevention newBo = HmBeParaUtil.getDefaultProfile(DosPrevention.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapDosPrevention(dosPreventionDTO.getId(), newBo.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			dosPreventionDTO.setDefaultFlag(false);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			dosPreventionDTO.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set enabledsyncheck
			 */
			colName = "enabledsyncheck";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention", colName);
			String enabledsyncheck = isColPresent ? xmlParser.getColVal(i, colName) : "";
			dosPreventionDTO.setEnabledSynCheck(AhRestoreCommons.convertStringToBoolean(enabledsyncheck));

			/**
			 * Set dostype
			 */
			colName = "dostype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"dos_prevention", colName);
			String dostype = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			dosPreventionDTO.setDosType(AhRestoreCommons.convertStringToDosType(dostype));

//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"dos_prevention", colName);
//			String owner = isColPresent ? xmlParser.getColVal(i, colName) : "";
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"dos_prevention", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'dos_prevention' data be lost, cause: 'owner' column is  not available.");
				continue;
			}

			dosPreventionDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (dosPreventionDTO.getDefaultFlag()) {
				dosPrevention.add(0,dosPreventionDTO);
			} else {
				dosPrevention.add(dosPreventionDTO);
			}
		}

		return dosPrevention;
	}

	public static boolean restoreDosPrevention()
	{
		try
		{
			List<DosPrevention> allDos = getAllDosPrevention();
			List<DosParams> allParam = getAllDosparams();
			if(null == allDos)
			{
				AhRestoreDBTools.logRestoreMsg("allDos is null");

				return false;
			}
			else
			{
				for(DosPrevention dos : allDos)
				{
					Map<String, DosParams> dosParamsMap = new LinkedHashMap<String, DosParams>();
					if (dos.getDosType() == DosType.IP) {
						for (ScreeningType screeningType : DosParams.ScreeningType.values()) {
							for (DosParams dosParam : allParam) {
								if (dosParam.getRestoreId().equals(dos.getId().toString())
										&& dosParam.getScreeningType() == screeningType) {
									dosParamsMap.put(dosParam.getkey(), dosParam);
								}
							}
						}
					} else {
						for (FrameType frameType : DosParams.FrameType.values()) {
							for (DosParams dosParam : allParam) {
								if (dosParam.getRestoreId().equals(dos.getId().toString())
										&& dosParam.getFrameType() == frameType) {
									dosParamsMap.put(dosParam.getkey(), dosParam);
								}
							}
						}
					}
					dos.setDosParamsMap(dosParamsMap);
					Long oldId = dos.getId();
					AhRestoreNewMapTools.setMapDosPrevention(oldId, QueryUtil.createBo(dos));
//					QueryUtil.createBo(dos);
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());

			return false;
		}
		return true;
	}

	private static List<ServiceFilter> getAllServiceFilter() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of service_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("service_filter");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in service_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ServiceFilter> serviceFilterInfo = new ArrayList<ServiceFilter>();
		boolean isColPresent;
		String colName;
		ServiceFilter serviceFilterDTO;

		for (int i = 0; i < rowCount; i++)
		{
			serviceFilterDTO = new ServiceFilter();

			/**
			 * Set filtername
			 */
			colName = "filtername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'service_filter' data be lost, cause: 'filtername' column is not exist.");
				/**
				 * The filtername column must be exist in the table of service_filter
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'service_filter' data be lost, cause: 'filtername' column value is null.");
				continue;
			}
			serviceFilterDTO.setFilterName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			serviceFilterDTO.setId(Long.valueOf(id));

			if (BeParaModule.DEFAULT_SERVICE_FILTER_NAME.equals(name.trim())) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("filterName", name);
				ServiceFilter newBo = HmBeParaUtil.getDefaultProfile(ServiceFilter.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapMgtServiceFilter(serviceFilterDTO.getId(), newBo.getId());
				}
				continue;
			}

			/**
			 * Set defaultflag
			 */
			serviceFilterDTO.setDefaultFlag(false);

			/**
			 * Set enableping
			 */
			colName = "enableping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String enableping = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			serviceFilterDTO.setEnablePing(AhRestoreCommons.convertStringToBoolean(enableping));

			/**
			 * Set enablesnmp
			 */
			colName = "enablesnmp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String enablesnmp = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			serviceFilterDTO.setEnableSNMP(AhRestoreCommons.convertStringToBoolean(enablesnmp));

			/**
			 * Set enablessh
			 */
			colName = "enablessh";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String enablessh = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			serviceFilterDTO.setEnableSSH(AhRestoreCommons.convertStringToBoolean(enablessh));

			/**
			 * Set enabletelnet
			 */
			colName = "enabletelnet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String enabletelnet = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			serviceFilterDTO.setEnableTelnet(AhRestoreCommons.convertStringToBoolean(enabletelnet));

			/**
			 * Set ssidtraffic
			 */
			// the field exist in 3.2b1-b3 and removed from 3.2r1
//			colName = "ssidtraffic";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"service_filter", colName);
//			String ssidtraffic = isColPresent ? xmlParser.getColVal(i, colName) : "true";
//			serviceFilterDTO.setSsidTraffic(AhRestoreCommons.convertStringToBoolean(ssidtraffic));

			/**
			 * Set intertraffic
			 */
			colName = "intertraffic";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "service_filter", colName);
			boolean interTraffic = true;
			if (isColPresent) {
				interTraffic = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			} else {
				/**
				 * Set interfacetraffic
				 */
				// the field exist in 3.2b1-b3 and removed from 3.2r1
				colName = "interfacetraffic";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "service_filter", colName);
				if (isColPresent) {
					interTraffic = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				}
			}
			serviceFilterDTO.setInterTraffic(interTraffic);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"service_filter", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			serviceFilterDTO.setDescription(AhRestoreCommons.convertString(description));

//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"service_filter", colName);
//			String owner = isColPresent ? xmlParser.getColVal(i, colName) : "";
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"service_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'service_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			serviceFilterDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (serviceFilterDTO.getDefaultFlag()) {
				serviceFilterInfo.add(0,serviceFilterDTO);
			} else {
				serviceFilterInfo.add(serviceFilterDTO);
			}

		}

		return serviceFilterInfo;
	}

	public static boolean restoreServiceFilter()
	{
		try
		{
			List<ServiceFilter> allFilter = getAllServiceFilter();
			if(null == allFilter)
			{
				return false;
			}
			else
			{
				List<Long> lOldId = new ArrayList<Long>();

				for (ServiceFilter filter : allFilter) {
					lOldId.add(filter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allFilter);

				for(int i=0; i < allFilter.size(); ++i)
				{
					AhRestoreNewMapTools.setMapMgtServiceFilter(lOldId.get(i), allFilter.get(i).getId());
				}

			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<InterRoaming> getAllL3Roaming() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of inter_roaming.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("inter_roaming");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in inter_roaming table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<InterRoaming> l3RoamingInfo = new ArrayList<InterRoaming>();
		boolean isColPresent;
		String colName;
		InterRoaming interRoamingDTO;

		for (int i = 0; i < rowCount; i++)
		{
			interRoamingDTO = new InterRoaming();

			/**
			 * Set roamingname
			 */
			colName = "roamingname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"inter_roaming", colName);
			if (!isColPresent)
			{
				/**
				 * The roamingname column must be exist in the table of inter_roaming
				 */
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'inter_roaming' data be lost, cause: 'roamingname' column is not exist.");
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'inter_roaming' data be lost, cause: 'roamingname' column is null.");
				continue;
			}
			interRoamingDTO.setRoamingName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"inter_roaming", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			interRoamingDTO.setId(Long.valueOf(id));

//			AhRestoreMapTool.setMapLayer3Romaing(id,name.trim());

			/**
			 * Set enabledl3setting
			 */
			colName = "enabledl3setting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"inter_roaming", colName);
			String enabledl3setting = isColPresent ? xmlParser.getColVal(i, colName) : "";
			interRoamingDTO.setEnabledL3Setting(AhRestoreCommons.convertStringToBoolean(enabledl3setting));

			if (interRoamingDTO.getEnabledL3Setting()) {
				/**
				 * Set keepaliveinterval
				 */
				colName = "keepaliveinterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String keepaliveinterval = isColPresent ? xmlParser.getColVal(i, colName) : "10";
				interRoamingDTO.setKeepAliveInterval(AhRestoreCommons.convertInt(keepaliveinterval));

				/**
				 * Set keepaliveageout
				 */
				colName = "keepaliveageout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String keepaliveageout = isColPresent ? xmlParser.getColVal(i, colName) : "5";
				interRoamingDTO.setKeepAliveAgeout(AhRestoreCommons.convertInt(keepaliveageout));

				/**
				 * Set updateinterval
				 */
				colName = "updateinterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String updateinterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
				interRoamingDTO.setUpdateInterval(AhRestoreCommons.convertInt(updateinterval));

				/**
				 * Set updateageout
				 */
				colName = "updateageout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String updateageout = isColPresent ? xmlParser.getColVal(i, colName) : "60";
				interRoamingDTO.setUpdateAgeout(AhRestoreCommons.convertInt(updateageout));
			}

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"inter_roaming", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			interRoamingDTO.setDescription(AhRestoreCommons.convertString(description));

//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"inter_roaming", colName);
//			String owner = isColPresent ? xmlParser.getColVal(i, colName) : "";
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'inter_roaming' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			interRoamingDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			l3RoamingInfo.add(interRoamingDTO);
		}

		return l3RoamingInfo;
	}

	public static boolean restoreL3Roaming()
	{
		try
		{
			List<InterRoaming> allL3Roaming = getAllL3Roaming();
			if(null == allL3Roaming)
			{
				return false;
			}
			else
			{
				List<Long> lOldId = new ArrayList<Long>();

				for (InterRoaming l3Roaming : allL3Roaming) {
					lOldId.add(l3Roaming.getId());
				}

				QueryUtil.restoreBulkCreateBos(allL3Roaming);

				for(int i=0; i < allL3Roaming.size(); ++i)
				{
					AhRestoreNewMapTools.setMapLayer3Romaing(lOldId.get(i), allL3Roaming.get(i).getId());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<Cwp> getAllCwp(List<HmUpgradeLog> upLogs) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of cwp.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("cwp");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in cwp table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<Cwp> cwpInfo = new ArrayList<Cwp>();
		boolean isColPresent;
		String colName;
		Cwp cwpDTO;
		CwpPageCustomization pageCustomization;
		List<CwpPageField> pageFields = getAllCwpPageFields();
		List<CwpPageMultiLanguageRes> cwpMultiLanguageRes=getAllCwpMultiRes();
		boolean isBefore34R3 = false;

		for (int i = 0; i < rowCount; i++)
		{
			cwpDTO = new Cwp();

			/**
			 * Set cwpname
			 */
			colName = "cwpname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			if (!isColPresent)
			{
				/**
				 * The cwpname column must be exist in the table of cwp
				 */
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp' data be lost, cause: 'cwpname' column is not exist.");
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp' data be lost, cause: 'cwpname' column is null.");
				continue;
			}
			cwpDTO.setCwpName(name.trim());

			/*
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setId(Long.valueOf(id));

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"cwp", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp' data be lost, cause: 'owner' column is  not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			cwpDTO.setOwner(ownerDomain);

			CwpAction cwpAction = new CwpAction();
			cwpAction.setDomainName(ownerDomain.getDomainName());


			/**
			 * Set registrationperiod
			 */
			colName = "registrationperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String registrationperiod = isColPresent ? xmlParser.getColVal(i, colName) : "720";
			cwpDTO.setRegistrationPeriod(AhRestoreCommons.convertInt(registrationperiod));

			/**
			 * Set leasetime
			 */
			colName = "leasetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String leasetime = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			cwpDTO.setLeaseTime(AhRestoreCommons.convertInt(leasetime));

			/**
			 * Set authmethod
			 */
			colName = "authmethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String authmethod = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setAuthMethod(AhRestoreCommons.convertInt(authmethod));

			/**
			 * Set servertype
			 */
			colName = "servertype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String servertype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setServerType(AhRestoreCommons.convertInt(servertype));

			/**
			 * Set dhcpmode
			 */
			colName = "dhcpmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String dhcpmode = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			cwpDTO.setDhcpMode(AhRestoreCommons.convertInt(dhcpmode));

			/*
			 * override vlan
			 */
			colName = "overridevlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String overrideVlan = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setOverrideVlan(AhRestoreCommons.convertStringToBoolean(overrideVlan));

			/*
			 * vlan
			 * since version 3.3r1
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String vlan = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if(!vlan.equals("") && !vlan.trim().equalsIgnoreCase("null")) {
				Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(vlan.trim()));
				cwpDTO.setVlan(AhRestoreNewTools.CreateBoWithId(Vlan.class,
						newVlanId));
				cwpDTO.setOverrideVlan(true);
			}

			/*
			 * Set usedefaultnetwork
			 */
			colName = "usedefaultnetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String usedefaultnetwork = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			cwpDTO.setUseDefaultNetwork(AhRestoreCommons.convertStringToBoolean(usedefaultnetwork));

			/*
			 * Set ipforeth0
			 */
			colName = "ipforeth0";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipforeth0 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setIpForEth0(AhRestoreCommons.convertString(ipforeth0));

			/*
			 * Set ipforeth1
			 */
			colName = "ipforeth1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipforeth1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setIpForEth1(AhRestoreCommons.convertString(ipforeth1));

			/*
			 * Set ipforamode
			 */
			colName = "ipforamode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipforamode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setIpForAMode(AhRestoreCommons.convertString(ipforamode));

			/*
			 * Set ipforbgmode
			 */
			colName = "ipforbgmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipforbgmode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setIpForBGMode(AhRestoreCommons.convertString(ipforbgmode));

			/*
			 * Set maskforeth0
			 */
			colName = "maskforeth0";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String maskforeth0 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setMaskForEth0(AhRestoreCommons.convertString(maskforeth0));

			/*
			 * Set maskforeth1
			 */
			colName = "maskforeth1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String maskforeth1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setMaskForEth1(AhRestoreCommons.convertString(maskforeth1));

			/*
			 * Set maskforamode
			 */
			colName = "maskforamode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String maskforamode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setMaskForAMode(AhRestoreCommons.convertString(maskforamode));

			/*
			 * Set maskforbgmode
			 */
			colName = "maskforbgmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String maskforbgmode = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setMaskForBGMode(AhRestoreCommons.convertString(maskforbgmode));

			/*
			 * Set numberfield
			 */
			colName = "numberfield";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String numberfield = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			int convertNumberField= AhRestoreCommons.convertInt(numberfield);
			if (convertNumberField>8) {
				convertNumberField = 2;
			}
			cwpDTO.setNumberField(convertNumberField);

			/**
			 * Set requestfield
			 */
			colName = "requestfield";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String requestfield = isColPresent ? xmlParser.getColVal(i, colName) : "4";
			cwpDTO.setRequestField(AhRestoreCommons.convertInt(requestfield));

			/**
			 * Set directoryname
			 */
			colName = "directoryname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String directoryname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setDirectoryName(AhRestoreCommons.convertString(directoryname));

			/**
			 * Set webpagename
			 */
			colName = "webpagename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String webpagename = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setWebPageName(AhRestoreCommons.convertString(webpagename));

			/*
			 * Set resultpagename
			 */
			colName = "resultpagename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String resultpagename = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setResultPageName(AhRestoreCommons.convertString(resultpagename));

			/*
			 * Set failurepagename
			 */
			colName = "failurepagename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failurepagename = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setFailurePageName(AhRestoreCommons.convertString(failurepagename));


			/*
			 * Set enabledhttps(Rollback to before)
			 */
			colName = "enabledhttps";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String enabledhttps = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setEnabledHttps(AhRestoreCommons.convertStringToBoolean(enabledhttps));

			/*
			 * Set certificate
			 */
			colName = "certificate_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			if (isColPresent){
				String certificate_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!certificate_id.equals("")
						&& !certificate_id.trim().equalsIgnoreCase("null")) {
					Long newCertId = AhRestoreNewMapTools.getMapCwpCertificate(Long.parseLong(certificate_id.trim()));
					cwpDTO.setCertificate(AhRestoreNewTools.CreateBoWithId(CwpCertificate.class,
							newCertId));
				}
			}

			/*
			 * keyfilename
			 * version before 3.2r2
			 */
			colName = "keyfilename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String keyfilename = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if(!keyfilename.equals("")) {
				/*
				 * in version before 3.2r2, key file name has suffix '.pem'
				 * but in later version, it doesn't
				 */
				keyfilename = keyfilename.substring(0, keyfilename.indexOf(".pem"));
				CwpCertificate cwpCert = QueryUtil.findBoByAttribute(CwpCertificate.class,
						"certName",
						keyfilename,
						ownerDomain.getId());
				cwpDTO.setCertificate(cwpCert);

			}

			/*
			 * certificateDN
			 */
			colName = "certificatedn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String certificateDN = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setCertificateDN(AhRestoreCommons.convertStringToBoolean(certificateDN));

			/*
			 * serverDomainName
			 */
			colName = "serverdomainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String serverDomainName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setServerDomainName(AhRestoreCommons.convertString(serverDomainName));

			/*
			 * Set comment
			 */
			colName = "comment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String comment = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (comment != null && !"".equals(comment) && comment.length() > 64){
				comment = comment.substring(0, 64);
			}
			cwpDTO.setComment(AhRestoreCommons.convertString(comment));

			/*
			 * Set enabledpopup
			 */
			colName = "enabledpopup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String enabledPopup = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setEnabledPopup(AhRestoreCommons.convertStringToBoolean(enabledPopup));

			/*
			 * Set multiLanguageSupport
			 */
			colName = "multiLanguageSupport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String multiLanguageSupport = isColPresent ? xmlParser.getColVal(i, colName) : "128";
			cwpDTO.setMultiLanguageSupport(AhRestoreCommons.convertInt(multiLanguageSupport));

			/*
			 * Set defaultLanguage
			 */
			colName = "defaultLanguage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String defaultLanguage = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setDefaultLanguage(AhRestoreCommons.convertInt(defaultLanguage));


			/*
			 * Set enablednewwin
			 */
			colName = "enablednewwin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String enablednewwin = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setEnabledNewWin(AhRestoreCommons.convertStringToBoolean(enablednewwin));

			/*
			 * successPageSource
			 * check if it is version before 3.4R3
			 */
			colName = "successpagesource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);

			if(!isColPresent) {
				isBefore34R3 = true;
			}

			/*
			 * set registrationType
			 */
			colName = "registrationtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String registrationType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			byte registType = (byte)AhRestoreCommons.convertInt(registrationType);
			cwpDTO.setRegistrationType(registType);

			/*
			 * set webPageSource
			 */
			colName = "webpagesource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String webPageSource = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.WEB_PAGE_SOURCE_AUTOGENERATE);
			// it will be used later
			byte tempWebPageSource = (byte)(AhRestoreCommons.convertInt(webPageSource));

			if(tempWebPageSource != Cwp.WEB_PAGE_SOURCE_AUTOGENERATE
					&& tempWebPageSource != Cwp.WEB_PAGE_SOURCE_IMPORT) {
				tempWebPageSource = Cwp.WEB_PAGE_SOURCE_AUTOGENERATE;
			}

			cwpDTO.setWebPageSource(tempWebPageSource);

			/*
			 * Set idmSefReg
			 */
			colName = "idmSelfReg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String idmSelfReg = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setIdmSelfReg(AhRestoreCommons.convertStringToBoolean(idmSelfReg));


			pageCustomization = new CwpPageCustomization(false, false);

			/*
			 * set cwp page customization - userPolicy
			 */
			colName = "userpolicy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String userPolicy = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_USER_POLICY;
			pageCustomization.setDefaultUserPolicy();
			pageCustomization.setUserPolicy(getUserPolicyFromFile(AhRestoreCommons.convertString(userPolicy), ownerDomain.getDomainName()),1);

			if(isBefore34R3) {
				if(!CwpPageCustomization.DEFAULT_USER_POLICY.equalsIgnoreCase(pageCustomization.getUserPolicy())) {
					pageCustomization.setDefaultUserPolicy();
					restoreUsePolicy(cwpDTO.getCwpName(),
							pageCustomization, ownerDomain.getDomainName());
				}
			}

			/*
			 * set cwp page customization - backgroundImage
			 */
			colName = "backgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String backgroundImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D;
			pageCustomization.setBackgroundImage(getNewDefaultResource(AhRestoreCommons.convertString(backgroundImage)));

			/*
			 * set cwp page customization - backgroundImage tile
			 */
			colName = "tilebackgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String tile = isColPresent
				? xmlParser.getColVal(i, colName) : String.valueOf(CwpPageCustomization.DEFAULT_BACKGROUND_TILES);
			pageCustomization.setTileBackgroundImage(AhRestoreCommons.convertStringToBoolean(tile));

			/*
			 * set cwp page customization - foregroundColor
			 */
			colName = "foregroundcolor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String foregroundColor = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOREGROUND_COLOR;

			if(!foregroundColor.contains("#")) {
				/*
				 * the color is restored from version earlier than 3.2
				 */
				foregroundColor = changeColorStyle(foregroundColor);
			}

			pageCustomization.setForegroundColor(AhRestoreCommons.convertString(foregroundColor));

			/*
			 * set cwp page customization - headImage
			 */
			colName = "headimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String headImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_HEAD_IMAGE;
			pageCustomization.setHeadImage(getNewDefaultResource(AhRestoreCommons.convertString(headImage)));

			/*
			 * set cwp page customization - footImage
			 */
			colName = "footimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String footImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOOT_IMAGE;
			pageCustomization.setFootImage(getNewDefaultResource(AhRestoreCommons.convertString(footImage)));

			/*
			 * set cwp page customization - successBackgroundImage
			 */
			colName = "successbackgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successBackgroundImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK;
			pageCustomization.setSuccessBackgroundImage(getNewDefaultResource(AhRestoreCommons.convertString(successBackgroundImage)));

			/*
			 * set cwp page customization - backgroundImage tile
			 */
			colName = "tilesuccessbackgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String tileSuccess = isColPresent
				? xmlParser.getColVal(i, colName) : String.valueOf(CwpPageCustomization.DEFAULT_BACKGROUND_TILES);
			pageCustomization.setTileSuccessBackgroundImage(AhRestoreCommons.convertStringToBoolean(tileSuccess));

			/*
			 * set cwp page customization - successForegroundColor
			 */
			colName = "successforegroundcolor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successForegroundColor = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOREGROUND_COLOR;

			if(!successForegroundColor.contains("#")) {
				/*
				 * the color is restored from version earlier than 3.2
				 */
				successForegroundColor = changeColorStyle(successForegroundColor);
			}

			pageCustomization.setSuccessForegroundColor(AhRestoreCommons.convertString(successForegroundColor));

			/*
			 * set cwp page customization - success notice
			 */
			colName = "successnotice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successNotice = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_SUCCESS_NOTICE;
				pageCustomization.setDefaultSuccessNotice();
			pageCustomization.setSuccessNotice(AhRestoreCommons.convertString(successNotice).replaceAll("\r\n", "<br>"),1);

			/*
			 * set cwp page customization - Library SIP status
			 */
			colName = "successlibrarysipstatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String sipStatus = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_LIB_SIP_STATUS;
				pageCustomization.setDefaultSuccessLibrarySIPStatus();
			pageCustomization.setSuccessLibrarySIPStatus(AhRestoreCommons.convertString(sipStatus).replaceAll("\r\n", "<br>"),1);

			/*
			 * set cwp page customization - Library SIP fines
			 */
			colName = "successlibrarysipfines";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String sipFines = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_LIB_SIP_FINES;
			pageCustomization.setDefaultSuccessLibrarySIPFines();
			pageCustomization.setSuccessLibrarySIPFines(AhRestoreCommons.convertString(sipFines).replaceAll("\r\n", "<br>"),1);

			/*
			 * set cwp page customization - Library SIP
			 */
			colName = "successlibrarysip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String librarySIP = isColPresent
				? xmlParser.getColVal(i, colName) : "false";
			pageCustomization.setSuccessLibrarySIP(AhRestoreCommons.convertStringToBoolean(librarySIP));

			/*
			 * set cwp page customization - successHeadImage
			 */
			colName = "successheadimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successHeadImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_HEAD_IMAGE;
			pageCustomization.setSuccessHeadImage(getNewDefaultResource(AhRestoreCommons.convertString(successHeadImage)));

			/*
			 * set cwp page customization - footImage
			 */
			colName = "successfootimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successFootImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOOT_IMAGE;
			pageCustomization.setSuccessFootImage(getNewDefaultResource(AhRestoreCommons.convertString(successFootImage)));

			/*
			 * set cwp page customization - failureBackgroundImage
			 */
			colName = "failurebackgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureBackgroundImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK;
			pageCustomization.setFailureBackgroundImage(getNewDefaultResource(AhRestoreCommons.convertString(failureBackgroundImage)));

			/*
			 * set cwp page customization - backgroundImage tile
			 */
			colName = "tilefailurebackgroundimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String tileFailure= isColPresent
				? xmlParser.getColVal(i, colName) : String.valueOf(CwpPageCustomization.DEFAULT_BACKGROUND_TILES);
			pageCustomization.setTileFailureBackgroundImage(AhRestoreCommons.convertStringToBoolean(tileFailure));

			/*
			 * set cwp page customization - failureForegroundColor
			 */
			colName = "failureforegroundcolor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureForegroundColor = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOREGROUND_COLOR;
			pageCustomization.setFailureForegroundColor(AhRestoreCommons.convertString(failureForegroundColor));

			/*
			 * set cwp page customization - failureHeadImage
			 */
			colName = "failureheadimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureHeadImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_HEAD_IMAGE;
			pageCustomization.setFailureHeadImage(getNewDefaultResource(AhRestoreCommons.convertString(failureHeadImage)));

			/*
			 * set cwp page customization - failure footImage
			 */
			colName = "failurefootimage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureFootImage = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_FOOT_IMAGE;
			pageCustomization.setFailureFootImage(getNewDefaultResource(AhRestoreCommons.convertString(failureFootImage)));

			/*
			 * set cwp page customization - Library SIP
			 */
			colName = "failurelibrarysip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureLibrarySIP = isColPresent
				? xmlParser.getColVal(i, colName) : "false";
			pageCustomization.setFailureLibrarySIP(AhRestoreCommons.convertStringToBoolean(failureLibrarySIP));

			/*
			 * set cwp page customization - failure Library SIP fines
			 */
			colName = "failurelibrarysipfines";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String sipBlock = isColPresent
				? xmlParser.getColVal(i, colName) : CwpPageCustomization.DEFAULT_LIB_SIP_BLOCK;
				pageCustomization.setDefaultFailureLibrarySIPFines();
			pageCustomization.setFailureLibrarySIPFines(AhRestoreCommons.convertString(sipBlock).replaceAll("\r\n", "<br>"),1);


			/*
			 * set cwp page customization - page fields
			 */
			if(pageFields != null) {
				Map<String, CwpPageField> fields = new HashMap<String, CwpPageField>();

				for(CwpPageField field : pageFields) {
					if(id.equals(field.getRestoreId())) {
						fields.put(field.getField(), field);
					}
				}

				pageCustomization.setFields(fields);
			}

			/*
			 * set cwp page customization - page multi language
			 */


			if(cwpMultiLanguageRes != null) {
				Map<Integer, CwpPageMultiLanguageRes> mulRes = new HashMap<Integer, CwpPageMultiLanguageRes>();

				for(CwpPageMultiLanguageRes res : cwpMultiLanguageRes) {
					if(id.equals(res.getRestoreId())) {
						mulRes.put(res.getResLanguage(), res);
					}
				}

				pageCustomization.setMultiLanguageRes(mulRes);
			}

			cwpDTO.setPageCustomization(pageCustomization);

			/*
			 * Set usedefaultfile
			 * in order to support earlier version
			 */
			colName = "usedefaultfile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);

			if(isColPresent) { // earlier version
				String usedefaultfile = xmlParser.getColVal(i, colName);

				if(AhRestoreCommons.convertStringToBoolean(usedefaultfile)) {
					cwpDTO.setWebPageSource(Cwp.WEB_PAGE_SOURCE_AUTOGENERATE);
					cwpDTO.setRegistrationType(Cwp.REGISTRATION_TYPE_BOTH);

					/*
					 * create default web pages
					 */
					cwpAction.setCwpDataSource(cwpDTO);
					cwpAction.createCwpPage(cwpDTO, false);
				} else {
					cwpDTO.setWebPageSource(Cwp.WEB_PAGE_SOURCE_IMPORT);
				}
			}

			/*
			 * External CWP: PPSK Server Type
			 */
			colName = "ppskservertype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ppskServerType = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.PPSK_SERVER_DEFAULT_TYPE);
			cwpDTO.setPpskServerType((byte)AhRestoreCommons.convertInt(ppskServerType));

			/*
			 * External CWP: login URL
			 */
			colName = "loginurl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String loginURL = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setLoginURL(AhRestoreCommons.convertString(loginURL));

			/*
			 * External CWP: password encryption
			 */
			colName = "passwordencryption";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String encrypt = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setPasswordEncryption((byte)(AhRestoreCommons.convertInt(encrypt)));

			/*
			 * External CWP: shared secret
			 */
			colName = "sharedsecret";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String secret = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setSharedSecret(AhRestoreCommons.convertString(secret));

			/*
			 * External CWP: session expiration alert
			 */
			colName = "sessionalert";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String alert = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(Cwp.DEFAULT_SESSION_EXPIRATION_ALERT);
			cwpDTO.setSessionAlert((short)AhRestoreCommons.convertInt(alert));

			/*
			 * walled garden
			 */
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			cwpDTO.setWalledGarden(getAllWalledGardenItems(id, lstLogBo));

			// there is upgrde logs
			for (HmUpgradeLog upLog : lstLogBo) {
				upLog.setFormerContent("The walled garden in Captive Web Portal \""+name+"\" " + upLog.getFormerContent());
				upLog.setPostContent(upLog.getPostContent()+" the walled garden in Captive Web Portal \""+name+"\".");
				upLog.setRecommendAction("No action is required.");
				upLog.setOwner(ownerDomain);
				upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				upLog.setAnnotation("Click to add an annotation");
				upLogs.add(upLog);
			}

			/*
			 * showSuccessPage
			 */
			colName = "showsuccesspage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String showSuccess = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			cwpDTO.setShowSuccessPage(AhRestoreCommons.convertStringToBoolean(showSuccess));

			/*
			 * successPageSource
			 */
			colName = "successpagesource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successPageSource = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE);

			if(isBefore34R3) {
				byte tempPageSource;

				if(tempWebPageSource == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
					tempPageSource = Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE;
				} else if(tempWebPageSource == Cwp.WEB_PAGE_SOURCE_IMPORT) {
					tempPageSource = Cwp.SUCCESS_PAGE_SOURCE_IMPORT;
				} else {
					tempPageSource = Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE;
				}

				cwpDTO.setSuccessPageSource(tempPageSource);
			} else {
				cwpDTO.setSuccessPageSource((byte)AhRestoreCommons.convertInt(successPageSource));
			}

			/*
			 * successRedirection
			 */
			colName = "successredirection";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successRedirection = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.SUCCESS_REDIRECT_NO);
			cwpDTO.setSuccessRedirection((byte)AhRestoreCommons.convertInt(successRedirection));

			/*
			 * successExternalURL
			 */
			colName = "successexternalurl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successURL = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setSuccessExternalURL(AhRestoreCommons.convertString(successURL));

			/*
			 * successDelay
			 */
			colName = "successdelay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String successDelay = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.DEFAULT_SUCCESS_DELAY);
			cwpDTO.setSuccessDelay((byte)AhRestoreCommons.convertInt(successDelay));

			if(isBefore34R3) {
				/*
				 * set cwp page customization - use external success page
				 */
				colName = "useexternalsuccesspage";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"cwp", colName);
				String useExternal = isColPresent ? xmlParser.getColVal(i, colName) : "false";

				if(AhRestoreCommons.convertStringToBoolean(useExternal)) {
					cwpDTO.setSuccessRedirection(Cwp.SUCCESS_REDIRECT_EXTERNAL);

					/*
					 * set cwp page customization - external url
					 */
					colName = "externalurl";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"cwp", colName);
					String url = isColPresent
						? xmlParser.getColVal(i, colName) : "";
					cwpDTO.setSuccessExternalURL(AhRestoreCommons.convertString(url));

					/*
					 * set cwp page customization - external timeout
					 */
					colName = "externaltimeout";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"cwp", colName);
					String timeout = isColPresent
						? xmlParser.getColVal(i, colName) : "";
					cwpDTO.setSuccessDelay((byte)AhRestoreCommons.convertInt(timeout));
				}
			}

			/*
			 * showFailurePage
			 */
			colName = "showfailurepage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String showFailure = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			cwpDTO.setShowFailurePage(AhRestoreCommons.convertStringToBoolean(showFailure));

			/*
			 * useLoginAsFailure
			 */
			colName = "useLoginAsFailure";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String useLoginAsFailure = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setUseLoginAsFailure(AhRestoreCommons.convertStringToBoolean(useLoginAsFailure));

			/*
			 * failurePageSource
			 */
			colName = "failurepagesource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failurePageSource = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.FAILURE_PAGE_SOURCE_CUSTOMIZE);
			cwpDTO.setFailurePageSource((byte)AhRestoreCommons.convertInt(failurePageSource));

			/*
			 * failureRedirection
			 */
			colName = "failureredirection";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureRedirection = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.FAILURE_REDIRECT_NO);
			cwpDTO.setFailureRedirection((byte)AhRestoreCommons.convertInt(failureRedirection));

			/*
			 * failureExternalURL
			 */
			colName = "failureexternalurl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureURL = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setFailureExternalURL(AhRestoreCommons.convertString(failureURL));

			/*
			 * failureDelay
			 */
			colName = "failuredelay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String failureDelay = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(Cwp.DEFAULT_FAILURE_DELAY);
			cwpDTO.setFailureDelay((byte)AhRestoreCommons.convertInt(failureDelay));

			/*
			 * enabledHTTP302
			 */
			colName = "enabledhttp302";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String http302 = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setEnabledHTTP302(AhRestoreCommons.convertStringToBoolean(http302));

			/*
			 * disableRoamingLogin
			 */
			colName = "disableroaminglogin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String disableRoaming = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			cwpDTO.setDisableRoamingLogin(AhRestoreCommons.convertStringToBoolean(disableRoaming));

			/*
			 * needReassociate
			 */
			colName = "needreassociate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String needReassociate = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			cwpDTO.setNeedReassociate(AhRestoreCommons.convertStringToBoolean(needReassociate));


			/*
			 * blockRedirectURL
			 */
			colName = "blockredirecturl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String blockRedirectURL = isColPresent ? xmlParser.getColVal(i, colName) : "";
			cwpDTO.setBlockRedirectURL(AhRestoreCommons.convertString(blockRedirectURL));

			/*
			 * enableUsePolicy changed since from Dakar release
			 */
			colName = "enableUsePolicy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String enableUsePolicy = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cwpDTO.setEnableUsePolicy(AhRestoreCommons.convertStringToBoolean(enableUsePolicy));

			/*
			 * CWP template is changed since from Cannes release
			 * joseph chen
			 * 2011.08.10
			 */
			if("default_3d.jpg".equals(backgroundImage)
					|| "default_3d.jpg".equals(successBackgroundImage)
					|| "default_3d.jpg".equals(failureBackgroundImage)) {
				if("default_3d.jpg".equals(backgroundImage)) {
					cwpDTO.getPageCustomization().resetLoginPage();
				}

				if("default_3d.jpg".equals(successBackgroundImage)) {
					cwpDTO.getPageCustomization().resetSuccessPage();
				}

				if("default_3d.jpg".equals(failureBackgroundImage)) {
					cwpDTO.getPageCustomization().resetFailurePage();
				}

				cwpAction.createCwpPage(cwpDTO, false);
			}
			
			/* since version 6.1r3 Glasgow
			 * set externalURLSuccessType
			 */
			colName = "externalURLSuccessType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String externalURLSuccessType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setExternalURLSuccessType((byte)AhRestoreCommons.convertInt(externalURLSuccessType));
			
			/**
			 * set externalURLFailureType
			 */
			colName = "externalURLFailureType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"cwp", colName);
			String externalURLFailureType = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpDTO.setExternalURLFailureType((byte)AhRestoreCommons.convertInt(externalURLFailureType));
			
			/*
			 * ipAddressSuccess
			 * 
			 */
			colName = "IPADDRESS_SUCCESS_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipAddressSuccess = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if(!ipAddressSuccess.equals("") && !ipAddressSuccess.trim().equalsIgnoreCase("null")) {
				Long ipAddressSuccessId = AhRestoreNewMapTools.getMapIpAdddress(Long.parseLong(ipAddressSuccess.trim()));
				cwpDTO.setIpAddressSuccess(AhRestoreNewTools.CreateBoWithId(IpAddress.class,
						ipAddressSuccessId));
			}
			
			/*
			 * ipAddressFailure
			 * 
			 */
			colName = "IPADDRESS_FAILURE_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp", colName);
			String ipAddressFailure = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if(!ipAddressFailure.equals("") && !ipAddressFailure.trim().equalsIgnoreCase("null")) {
				Long ipAddressFailureId = AhRestoreNewMapTools.getMapIpAdddress(Long.parseLong(ipAddressFailure.trim()));
				cwpDTO.setIpAddressFailure(AhRestoreNewTools.CreateBoWithId(IpAddress.class,
						ipAddressFailureId));
			}


			cwpInfo.add(cwpDTO);
			
			if(RESTORE_BEFORE_6_1_R4 && cwpDTO.isIdmSelfReg()) {
			    if(null == generateCwpList) {
			        generateCwpList = new ArrayList<>();
			    }
			    generateCwpList.add(cwpDTO);
			}
		}

		return cwpInfo;
	}
	private final static String DEFAULT_RESORUCE_DIRECTORY = "resources";

	private final static String DEFAULT_RESORUCE_CWP_DIRECTORY = "cwp";

	private final static String DEFAULT_RESOURCE_PATH = AhDirTools.getHmRoot() +
			DEFAULT_RESORUCE_DIRECTORY + File.separator +
			DEFAULT_RESORUCE_CWP_DIRECTORY + File.separator;

	private static boolean isDefaultResource(String fileName) {
		if(fileName == null) {
			return false;
		}

		String[] defaultFiles = {CwpPageCustomization.DEFAULT_3D_BACKGROUND_IMAGE,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_LIGHT,
				CwpPageCustomization.DEFAULT_FOOT_IMAGE,
				CwpPageCustomization.DEFAULT_HEAD_IMAGE,
				CwpPageCustomization.DEFAULT_USER_POLICY
				};

		for(String defaultFile : defaultFiles) {
			if(defaultFile.equals(fileName)) {
				return true;
			}
		}

		return false;
	}

	private static String getUserPolicyFromFile(String fileName,String domainName) {
		String filePath;

		if(isDefaultResource(fileName)) { // get from default ones
			filePath = DEFAULT_RESOURCE_PATH + fileName;
		} else { // get from uploaded ones
			filePath = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy" + File.separator + fileName;
		}

		// get the content of user policy from file
		BufferedReader bufferReader;

		try {
			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			bufferReader = null;
		}

		if(bufferReader == null) {
			return null;
		}

		String textLine;
		StringBuilder userPolicy = new StringBuilder();

		try {
			while((textLine = bufferReader.readLine()) != null) {
				userPolicy.append(escapeCharacter(textLine)).append("\n");
			}
		} catch (IOException e) {

		}

		return userPolicy.toString();
	}

	private static String escapeCharacter(String source) {
		if(source == null) {
			return null;
		}

		// change the '\n' into '<br>'
		source = source.replace("\r\n", "<br>");

		// change '{', '}' into &123;, &125;
		source = source.replace("{", "&#123;");
		source = source.replace("}", "&#125;");
		source = source.replace("'", "&#180;");

		return source;
	}

	private static String getNewDefaultResource(String oldName) {
		String newName = oldName;

		if("aerohive_3d_bg.png".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_3D_BACKGROUND_IMAGE;
		} else if("aerohive_3d.jpg".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D;
		} else if("aerohive_hex_dark.jpg".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK;
		} else if("aerohive_hex_light.jpg".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_LIGHT;
		} else if("aerohive_logo.png".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_FOOT_IMAGE;
		} else if("aerohive_logo_reverse.png".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_FOOT_IMAGE;
		} else if("aerohive_spacer.png".equals(oldName)) {
			newName = CwpPageCustomization.DEFAULT_HEAD_IMAGE;
		}

		if("default_3d.jpg".equals(newName)) {
			newName = CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D;
		} else if("company_logo_reverse.png".equals(newName)){
			newName = CwpPageCustomization.DEFAULT_FOOT_IMAGE;
		}

		return newName;
	}

	private static String changeColorStyle(String color) {
		if(color == null || color.contains("#")) {
			return color;
		}

		StringBuilder newColor = new StringBuilder("#");
		Color objectColor = null;

		for(int i=0; i<CwpPageCustomization.FOREGROUND_COLOR.length; i++) {
			if(color.equalsIgnoreCase((String)CwpPageCustomization.FOREGROUND_COLOR[i][0])) {
				objectColor = (Color)CwpPageCustomization.FOREGROUND_COLOR[i][1];
				break;
			}
		}

		if(objectColor == null) {
			return color;
		}

		newColor.append(CwpAction.toHexString(objectColor.getRed()))
				.append(CwpAction.toHexString(objectColor.getGreen()))
				.append(CwpAction.toHexString(objectColor.getBlue()));

		return newColor.toString();
	}

	private static List<CwpPageField> getAllCwpPageFields() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "cwp_page_field";
		/*
		 * Check validation of cwp_page_field.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();


		List<CwpPageField> pageFields = new ArrayList<CwpPageField>();

		boolean isColPresent;
		String colName;
		CwpPageField field;

		for (int i = 0; i < rowCount; i++)
		{
			field = new CwpPageField();

			/*
			 * Set cwp_page_customization_id
			 */
			colName = "cwp_page_customization_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if ("".equals(id))
			{
				continue;
			}

			field.setRestoreId(id);

			/*
			 * label
			 */
			colName = "label";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label = isColPresent ? xmlParser.getColVal(i, colName) : "";
			field.setLabel(AhRestoreCommons.convertString(label));

			/*
			 * labelName
			 */
			colName = "labelName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String labelName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			field.setLabelName(AhRestoreCommons.convertString(labelName));

			/*
			 * mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String mapkey = isColPresent ? xmlParser.getColVal(i, colName) : "";

			for (String key : CwpPageField.FIELDS) {
				if (mapkey.equals(key)) {
					field.setField(key);
				}
			}

			setMultiLanguageFields(field);

			/*
			 * label2
			 */
			colName = "label2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label2 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel2();
			field.setLabel2(AhRestoreCommons.convertString(label2));

			/*
			 * label3
			 */
			colName = "label3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label3 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel3();
			field.setLabel3(AhRestoreCommons.convertString(label3));

			/*
			 * label4
			 */
			colName = "label4";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label4 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel4();
			field.setLabel4(AhRestoreCommons.convertString(label4));

			/*
			 * label5
			 */
			colName = "label5";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label5 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel5();
			field.setLabel5(AhRestoreCommons.convertString(label5));

			/*
			 * label6
			 */
			colName = "label6";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label6 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel6();
			field.setLabel6(AhRestoreCommons.convertString(label6));

			/*
			 * label7
			 */
			colName = "label7";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label7 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel7();
			field.setLabel7(AhRestoreCommons.convertString(label7));

			/*
			 * label8
			 */
			colName = "label8";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label8 = isColPresent ? xmlParser.getColVal(i, colName) : field.getLabel8();
			field.setLabel8(AhRestoreCommons.convertString(label8));

			/*
			 * label9
			 */
			colName = "label9";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label9 = isColPresent ? xmlParser.getColVal(i, colName) :field.getLabel9();
			field.setLabel9(AhRestoreCommons.convertString(label9));

			/*
			 * fieldmark
			 */
			colName = "fieldMark";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String fieldMark = isColPresent ? xmlParser.getColVal(i, colName) : field.getFieldMark();
			field.setFieldMark(AhRestoreCommons.convertString(fieldMark));

			/*
			 * enabled
			 */
			colName = "enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enabled = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			field.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));

			/*
			 * required
			 */
			colName = "required";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String required = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			field.setRequired(AhRestoreCommons.convertStringToBoolean(required));

			/*
			 * place
			 */
			colName = "place";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String place = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			field.setPlace((byte)AhRestoreCommons.convertInt(place));



			pageFields.add(field);
		}

		return pageFields.size() > 0 ? pageFields : null;
	}

	static BeResModule_CWPImpl cwpResReader=new BeResModule_CWPImpl();

	private static void setMultiLanguageFields(CwpPageField field){
		String searchWord="cwp.preview.ppsk.firstname_label";
		String mark=CwpPageField.FIRSTNAMEMARK;
		if(field.getField().equals(CwpPageField.FIRSTNAME)){
			searchWord="cwp.preview.ppsk.firstname_label";
			mark=CwpPageField.FIRSTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.LASTNAME)){
			searchWord="cwp.preview.ppsk.lastname_label";
			mark=CwpPageField.LASTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.EMAIL)){
			searchWord="cwp.preview.ppsk.email_label";
			mark=CwpPageField.EMAILMARK;
		}else if(field.getField().equals(CwpPageField.PHONE)){
			searchWord="cwp.preview.ppsk.phone_label";
			mark=CwpPageField.PHONEMARK;
		}else if(field.getField().equals(CwpPageField.VISITING)){
			searchWord="cwp.preview.ppsk.visiting_label";
			mark=CwpPageField.VISITINGMARK;
		}else if(field.getField().equals(CwpPageField.COMMENT)){
			searchWord="cwp.preview.ppsk.reason_label";
			mark=CwpPageField.COMMENTMARK;
		}else if(field.getField().equals(CwpPageField.REPRESENTING)){
			searchWord="cwp.preview.ppsk.idm.reason_label";
			mark=CwpPageField.REPRESENTINGMARK;
		}

		field.setFieldMark(mark);
		field.setLabel2(cwpResReader.getString(searchWord, getLocaleFromPreview(2)));
		field.setLabel3(cwpResReader.getString(searchWord, getLocaleFromPreview(3)));
		field.setLabel4(cwpResReader.getString(searchWord, getLocaleFromPreview(4)));
		field.setLabel5(cwpResReader.getString(searchWord, getLocaleFromPreview(5)));
		field.setLabel6(cwpResReader.getString(searchWord, getLocaleFromPreview(6)));
		field.setLabel7(cwpResReader.getString(searchWord, getLocaleFromPreview(7)));
		field.setLabel8(cwpResReader.getString(searchWord, getLocaleFromPreview(8)));
		field.setLabel9(cwpResReader.getString(searchWord, getLocaleFromPreview(9)));

	}

	private static Locale getLocaleFromPreview(int pLanguage ){
		Locale resultLocale;
		switch(pLanguage){
		case 1:
			resultLocale=Locale.ENGLISH;
			break;
		case 2:
			resultLocale=Locale.CHINA;
			break;
		case 3:
			resultLocale=Locale.GERMAN;
			break;
		case 4:
			resultLocale=Locale.FRANCE;
			break;
		case 5:
			resultLocale=Locale.KOREA;
			break;
		case 6:
			resultLocale=new Locale("nl","DU");;
			break;
		case 7:
			resultLocale=new Locale("es","SP");
			break;
		case 8:
			resultLocale=Locale.TAIWAN;
			break;
		case 9:
			resultLocale=Locale.ITALIAN;
			break;


		default:
				resultLocale=Locale.ENGLISH;
				break;
		}
		return resultLocale;
	}


	private static List<CwpPageMultiLanguageRes> getAllCwpMultiRes() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "CWP_PAGE_MULTI_LANGUAGE_RES";
		/*
		 * Check validation of cwp_page_field.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<CwpPageMultiLanguageRes> cwpPageRes = new ArrayList<CwpPageMultiLanguageRes>();

		boolean isColPresent;
		String colName;
		CwpPageMultiLanguageRes multRres;

		for (int i = 0; i < rowCount; i++)
		{
			multRres = new CwpPageMultiLanguageRes();

			/*
			 * Set CWP_PAGE_MULTI_LANGUAGE_RES_ID
			 */
			colName = "CWP_PAGE_MULTI_LANGUAGE_RES_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if ("".equals(id))
			{
				continue;
			}

			multRres.setRestoreId(id);

			/*
			 * successNotice
			 */
			colName = "successNotice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String successNotice = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setSuccessNotice(AhRestoreCommons.convertString(successNotice));

			/*
			 * successLibrarySIPStatus
			 */
			colName = "successLibrarySIPStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String successLibrarySIPStatus = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setSuccessLibrarySIPStatus(AhRestoreCommons.convertString(successLibrarySIPStatus));

			/*
			 * successLibrarySIPFines
			 */
			colName = "successLibrarySIPFines";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String successLibrarySIPFines = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setSuccessLibrarySIPFines(AhRestoreCommons.convertString(successLibrarySIPFines));

			/*
			 * failureLibrarySIPFines
			 */
			colName = "failureLibrarySIPFines";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String failureLibrarySIPFines = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setFailureLibrarySIPFines(AhRestoreCommons.convertString(failureLibrarySIPFines));

			/*
			 * userPolicy
			 */
			colName = "userPolicy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String userPolicy = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setUserPolicy(AhRestoreCommons.convertString(userPolicy));

			/*
			 * resLanguage
			 */
			colName = "resLanguage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String resLanguage = isColPresent ? xmlParser.getColVal(i, colName) : "";
			multRres.setResLanguage(AhRestoreCommons.convertInt(resLanguage));


			/*
			 * mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String mapkey = isColPresent ? xmlParser.getColVal(i, colName) : "";

//			for (int key : CwpPageMultiLanguageRes.languages) {
//				if (Integer.parseInt(mapkey)==key) {
//					multRres
//					field.setField(key);
//				}
//			}

			cwpPageRes.add(multRres);
		}

		return cwpPageRes.size() > 0 ? cwpPageRes : null;
	}

	private static List<WalledGardenItem> getAllWalledGardenItems(String cwpId, List<HmUpgradeLog> lstLogBo) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "walled_garden_item";

		/*
		 * Check validation of walled_garden_item.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet || null == cwpId)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		List<WalledGardenItem> items = new ArrayList<WalledGardenItem>();
		WalledGardenItem item;

		for (int i = 0; i < rowCount; i++)
		{
			item = new WalledGardenItem();

			/*
			 * walled_garden_id
			 */
			colName = "walled_garden_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if ("".equals(id) || !cwpId.equals(id))
			{
				continue;
			}

			item.setRestoreId(id);

			/*
			 * server
			 */
			colName = "walled_garden_item_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String ipAddressId = isColPresent ? xmlParser.getColVal(i, colName) : "";

			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			item.setServer(RestoreConfigNetwork.getNewIpNetworkObj(ipAddressId, upgradeLog));

			// there is upgrade log to record
			if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
				lstLogBo.add(upgradeLog);
			}

			/*
			 * service
			 */
			colName = "service";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String service = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			item.setService((byte)AhRestoreCommons.convertInt(service));

			/*
			 * Protocol
			 */
			colName = "protocol";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String protocol = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			item.setProtocol(AhRestoreCommons.convertInt(protocol));

			/*
			 * port
			 */
			colName = "port";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String port = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			item.setPort(AhRestoreCommons.convertInt(port));

			/*
			 * item id
			 */
			colName = "itemid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String itemId = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			item.setItemId(AhRestoreCommons.convertInt(itemId));

			items.add(item);
		}

		return items.size() > 0 ? items : null;

	}

	private static void restoreUsePolicy(String cwpName,
			CwpPageCustomization pageCustom,
			String domainName) {
		String baseDir = AhDirTools.getPageResourcesDir(domainName);
		String usePolicyDir = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy" + File.separator;

		FileManager fileManager = FileManager.getInstance();

		if(!fileManager.existsFile(usePolicyDir)) {
			fileManager.createDirectory(usePolicyDir);
		}

		if(!fileManager.existsFile(baseDir + pageCustom.getUserPolicy())) {
			return ;
		}

		try {
			fileManager.copyFile(baseDir + pageCustom.getUserPolicy(),
					usePolicyDir + cwpName + CwpAction.USE_POLICY_FILE_SUFFIX, false);

			pageCustom.setUserPolicy(getUserPolicyFromFile(usePolicyDir + cwpName + CwpAction.USE_POLICY_FILE_SUFFIX,domainName),1);
		} catch (IOException e) {
			AhRestoreDBTools.logRestoreMsg("Failed to restore use policy file for cwp<"
					+ cwpName + ">. Possible reason: " + e.getMessage());
		}
	}

	public static boolean restoreCwp()
	{
		try
		{
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<Cwp> allCwp = getAllCwp(lstLogBo);

			if(null == allCwp) {
				return false;
			}

			List<Long> oldIDs = new ArrayList<Long>();

			for (Cwp cwp : allCwp) {
				oldIDs.add(cwp.getId());
			}

			QueryUtil.restoreBulkCreateBos(allCwp);

			/*
			 * insert new old and new id to map
			 */
			for(int i=0; i<allCwp.size(); i++) {
				AhRestoreNewMapTools.setMapCapWebPortal(oldIDs.get(i),
						allCwp.get(i).getId());
			}

			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option for CWP upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
			
			/*
			 * Generate the Cwp page for IDM Self-reg
			 */
			regenerateCWPPage(generateCwpList);
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	public static void regenerateCWPPage(List<Cwp> cwps) {
	    if(null == cwps) {
	        return;
	    }
        try {
            CwpAction cwpAction = new CwpAction();
            for (Cwp cwp : cwps) {
                cwpAction.setCwpDataSource(cwp);
                cwpAction.setDomainName(cwp.getOwner().getDomainName());
                cwpAction.setIdmSelfReg(true);
                cwpAction.setCountryCodeEnabled(true);
                cwpAction.initMultiLanguageSupportCheckBox();
                cwpAction.createCwpPage(cwp, false);
            }
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Error when auto generation the IDM-Self CWP in restore");
            AhRestoreDBTools.logRestoreMsg(e.getMessage());
        }
	}
	
	public static boolean restoreTableColumnCustom() {
		try {
			Map<Long, List<HmTableColumn>> map = getTableColumnCustom();

			if(map == null) {
				return false;
			}

			List<HmTableColumn> htc = null;

			for(Long userId : map.keySet()) {
				Long newId = AhRestoreNewMapTools.getMapUser(userId);
				if (newId==null) {
					continue;
				}
				HmUser user = QueryUtil.findBoById(HmUser.class, newId, new ImplQueryBo());
				htc = map.get(userId);
				user.setTableColumns(htc);

				for (HmTableColumn hmTableColumn : htc) {
					hmTableColumn.setUseremail(user.getEmailAddress());
				}
				QueryUtil.restoreBulkCreateBos(htc);
			}

		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreTableSize() {
		try {
			Map<Long, List<HmTableSize>> map = getTableSizeCustom();

			if(map == null) {
				return false;
			}

			List<HmTableSize> hts = null;

			for(Long userId : map.keySet()) {
				Long newId = AhRestoreNewMapTools.getMapUser(userId);
				if (newId==null) {
					continue;
				}
				HmUser user = QueryUtil.findBoById(HmUser.class, newId, new ImplQueryBo());
				hts = map.get(userId);
				user.setTableSizes(hts);

				for (HmTableSize hmTableSize : hts) {
					hmTableSize.setUseremail(user.getEmailAddress());
				}
				QueryUtil.restoreBulkCreateBos(hts);
			}

		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreTableSizeNew() {
		try {
			List<HmTableSize> bos = getTableSizeCustomNew();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore hm_table_size_new error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreTableColumnCustomNew() {
		try {
			List<HmTableColumn> bos = getTableColumnCustomNew();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore hm_table_column_new error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	private static Map<Long, List<HmTableSize>> getTableSizeCustom()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_table_size";

		boolean flg = AhRestoreGetXML.checkXMLFileExist("hm_table_size_new");
		if (flg) return null;

		/*
		 * check validation of file 'hm_table_column.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<Long, List<HmTableSize>> tableSizes = new HashMap<Long, List<HmTableSize>>();
		HmTableSize tableSize;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			tableSize = new HmTableSize();

			/*
			 * hm_user_id
			 */
			colName = "hm_user_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String userId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setPosition(Integer.parseInt(position));

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setTableId(Integer.parseInt(tableId));

			/*
			 * tablesize
			 */
			colName = "tablesize";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tablesize = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setTableSize(Integer.parseInt(tablesize));

			addTableSizes(tableSizes, Long.parseLong(userId), tableSize);
		}

		return tableSizes;
	}

	private static void addTableSizes(Map<Long, List<HmTableSize>> map, Long userId, HmTableSize tableSize) {
		if(map.get(userId) != null) { // userId already in keys
			map.get(userId).add(tableSize);
		} else { // userId not in keys
			List<HmTableSize> hts = new ArrayList<HmTableSize>(10);
			hts.add(tableSize);
			map.put(userId, hts);
		}
	}

	private static Map<Long, List<HmTableColumn>> getTableColumnCustom() throws
						AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_table_column";

		boolean flg = AhRestoreGetXML.checkXMLFileExist("hm_table_column_new");
		if (flg) return null;

		/*
		 * check validation of file 'hm_table_column.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<Long, List<HmTableColumn>> userColumns = new HashMap<Long, List<HmTableColumn>>();
		HmTableColumn column;
		boolean isColPresent;
		String colName;


		for (int i = 0; i < rowCount; i++) {
			column = new HmTableColumn();

			/*
			 * hm_user_id
			 */
			colName = "hm_user_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String userId = isColPresent ? xmlParser.getColVal(i, colName) : "0";

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			column.setPosition(Integer.parseInt(position));

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			column.setTableId(Integer.parseInt(tableId));

			/*
			 * columnid
			 */
			colName = "columnid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String columnId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			column.setColumnId(Integer.parseInt(columnId));

			addUserColumn(userColumns, Long.parseLong(userId), column);
		}

		return userColumns;
	}

	private static List<HmTableColumn> getTableColumnCustomNew()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_table_column_new";

		/*
		 * check validation of file 'hm_table_column_new.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmTableColumn> userColumns = new ArrayList<HmTableColumn>();
		HmTableColumn column;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			column = new HmTableColumn();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			column.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			column.setTableId(Integer.parseInt(tableId));

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			column.setPosition(Integer.parseInt(position));

			/*
			 * columnid
			 */
			colName = "columnid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String columnId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			column.setColumnId(Integer.parseInt(columnId));

			userColumns.add(column);
		}

		return userColumns;
	}

	private static List<HmTableSize> getTableSizeCustomNew()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_table_size_new";

		/*
		 * check validation of file 'hm_table_column_new.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmTableSize> tableSizes = new ArrayList<HmTableSize>();
		HmTableSize tableSize;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			tableSize = new HmTableSize();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			tableSize.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setTableId(Integer.parseInt(tableId));

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setPosition(Integer.parseInt(position));

			/*
			 * columnid
			 */
			colName = "tablesize";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tablesize = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			tableSize.setTableSize(Integer.parseInt(tablesize));

			tableSizes.add(tableSize);
		}

		return tableSizes;
	}

	private static void addUserColumn(Map<Long, List<HmTableColumn>> map, Long userId, HmTableColumn column) {
		if(map.get(userId) != null) { // userId already in keys
			map.get(userId).add(column);
		} else { // userId not in keys
			List<HmTableColumn> columns = new ArrayList<HmTableColumn>(10);
			columns.add(column);
			map.put(userId, columns);
		}
	}

	private static Set<IpAddress> getAllIpFilterIpAddress(String filterId, List<HmUpgradeLog> lstLogBo) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ip_filter_ip_address.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ip_filter_ip_address");
		if (!restoreRet || null == filterId)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Set<IpAddress> ipAddressInfo = new HashSet<IpAddress>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set ip_filter_id
			 */
			colName = "ip_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_filter_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The ip_filter_id column must be exist in the table of ip_filter_ip_address
				 */
				continue;
			}

			String ipFilterId = xmlParser.getColVal(i, colName);
			if (ipFilterId == null || ipFilterId.trim().equals("")
				|| ipFilterId.trim().equalsIgnoreCase("null") || !filterId.equals(ipFilterId))
			{
				continue;
			}

			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_filter_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The ip_address_id column must be exist in the table of ip_filter_ip_address
				 */
				continue;
			}

			String ipAddressId = xmlParser.getColVal(i, colName);
			if (ipAddressId == null || ipAddressId.trim().equals("")
				|| ipAddressId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			IpAddress newip = RestoreConfigNetwork.getNewIpNetworkObj(ipAddressId, upgradeLog);
			ipAddressInfo.add(newip);

			// there is upgrade log to record
			if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
				lstLogBo.add(upgradeLog);
			}
		}

		return ipAddressInfo;
	}

	private static List<IpFilter> getAllIpFilter(List<HmUpgradeLog> upLogs) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ip_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ip_filter");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ip_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<IpFilter> ipFilterInfo = new ArrayList<IpFilter>();
		boolean isColPresent;
		String colName;
		IpFilter ipFilter;

		for (int i = 0; i < rowCount; i++)
		{
			ipFilter = new IpFilter();

			/**
			 * Set filtername
			 */
			colName = "filtername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_filter", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_filter' data be lost, cause: 'filtername' column is not exist.");
				/**
				 * The cwpname column must be exist in the table of cwp
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_filter' data be lost, cause: 'filtername' column is null.");
				continue;
			}
			ipFilter.setFilterName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_filter", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ipFilter.setId(Long.valueOf(id));

//			AhRestoreMapTool.setMapMgtIpFilter(id,name.trim());

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ip_filter", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ipFilter.setDescription(AhRestoreCommons.convertString(description));

//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"ip_filter", colName);
//			String owner = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			 TBD			ipFilter.setOwner(AhRestoreCommons.convertString(owner));
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ip_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ip_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			ipFilter.setOwner(ownerDomain);

			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			ipFilter.setIpAddress(getAllIpFilterIpAddress(id, lstLogBo));

			// there is upgrde logs
			for (HmUpgradeLog upLog : lstLogBo) {
				upLog.setFormerContent("Management IP Filter \""+name+"\" " + upLog.getFormerContent());
				upLog.setPostContent(upLog.getPostContent()+" the Management IP Filter \""+name+"\".");
				upLog.setRecommendAction("No action is required.");
				upLog.setOwner(ownerDomain);
				upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				upLog.setAnnotation("Click to add an annotation");
				upLogs.add(upLog);
			}

			ipFilterInfo.add(ipFilter);
		}

		return ipFilterInfo;
	}

	public static boolean restoreIpFilter()
	{
		try
		{
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<IpFilter> allIpFilter = getAllIpFilter(lstLogBo);
			if(null == allIpFilter)
			{
				AhRestoreDBTools.logRestoreMsg("allIpFilter is null");

				return false;
			}
			else
			{
				List<Long> lOldId = new ArrayList<Long>();

				for (IpFilter ipFilter : allIpFilter) {
					lOldId.add(ipFilter.getId());
				}

				QueryUtil.restoreBulkCreateBos(allIpFilter);

				for(int i=0; i < allIpFilter.size(); ++i)
				{
					AhRestoreNewMapTools.setMapMgtIpFilter(lOldId.get(i), allIpFilter.get(i).getId());
				}

			}
			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option for ip filter upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static Map<String, List<TunnelSettingIPAddress>> getAllTunnelSettingIPAddressOld() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of tunnel_setting_ip_address.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("tunnel_setting_ip_address");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in tunnel_setting_ip_address table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<TunnelSettingIPAddress>> allTunnelInfo = new HashMap<String, List<TunnelSettingIPAddress>>();
		List<TunnelSettingIPAddress> tunnelSettingIPInfo;
		boolean isColPresent;
		String colName;
		TunnelSettingIPAddress tunnelSettingIPAddress;

		for (int i = 0; i < rowCount; i++)
		{
			tunnelSettingIPAddress = new TunnelSettingIPAddress();

			/**
			 * Set tunnel_setting_id
			 */
			colName = "tunnel_setting_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The tunnel_setting_id column must be exist in the table of tunnel_setting_ip_address
				 */
				continue;
			}

			String tunnelSettingId = xmlParser.getColVal(i, colName);
			if (tunnelSettingId == null || tunnelSettingId.trim().equals("")
				|| tunnelSettingId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}
			tunnelSettingIPInfo = allTunnelInfo.get(tunnelSettingId.trim());

			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The ip_address_id column must be exist in the table of tunnel_setting_ip_address
				 */
				continue;
			}

			String ipAddressId = xmlParser.getColVal(i, colName);
			if (ipAddressId == null || ipAddressId.trim().equals("")
				|| ipAddressId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			if (!ipAddressId.equals("") && !ipAddressId.trim().equalsIgnoreCase("null")) {
				Long newIpId = AhRestoreNewMapTools.getMapIpAdddress(Long.parseLong(ipAddressId.trim()));
				tunnelSettingIPAddress.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class,newIpId));
			}
			if (tunnelSettingIPAddress.getIpAddress() == null ) {
				continue;
			}

			/**
			 * Set password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting_ip_address", colName);
			String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
			tunnelSettingIPAddress.setPassword(AhRestoreCommons.convertString(password));

			if (null == tunnelSettingIPInfo) {
				tunnelSettingIPInfo = new ArrayList<TunnelSettingIPAddress>();
				tunnelSettingIPInfo.add(tunnelSettingIPAddress);
				allTunnelInfo.put(tunnelSettingId.trim(), tunnelSettingIPInfo);
			} else {
				tunnelSettingIPInfo.add(tunnelSettingIPAddress);
			}
		}
		return allTunnelInfo;
	}

	private static boolean ifContainPasswordInTunnelSettingIPAddress() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of tunnel_setting_ip_address.xml
		 */
		boolean isColPresent = false;
		boolean restoreRet = xmlParser.readXMLFile("tunnel_setting_ip_address");
		if (!restoreRet) {
			return isColPresent;
		}
		String colName = "password";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			"tunnel_setting_ip_address", colName);
		return isColPresent;
	}

	private static Set<IpAddress> getAllTunnelSettingIPAddressNew(String tunnelId, List<HmUpgradeLog> lstLogBo) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of tunnel_setting_ip_address.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("tunnel_setting_ip_address");
		if (!restoreRet || null == tunnelId) {
			return null;
		}

		/**
		 * No one row data stored in tunnel_setting_ip_address table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Set<IpAddress> tunnelSettingIPInfo = new HashSet<IpAddress>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set tunnel_setting_id
			 */
			colName = "tunnel_setting_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The tunnel_setting_id column must be exist in the table of tunnel_setting_ip_address
				 */
				continue;
			}

			String tunnelSettingId = xmlParser.getColVal(i, colName);
			if (tunnelSettingId == null || tunnelSettingId.trim().equals("")
				|| tunnelSettingId.trim().equalsIgnoreCase("null") || !tunnelId.equals(tunnelSettingId))
			{
				continue;
			}

			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting_ip_address", colName);
			if (!isColPresent)
			{
				/**
				 * The ip_address_id column must be exist in the table of tunnel_setting_ip_address
				 */
				continue;
			}

			String ipAddressId = xmlParser.getColVal(i, colName);
			if (ipAddressId == null || ipAddressId.trim().equals("")
				|| ipAddressId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}
			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			IpAddress newip = RestoreConfigNetwork.getNewIpNetworkObj(ipAddressId, upgradeLog);
			tunnelSettingIPInfo.add(newip);

			// there is upgrade log to record
			if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
				lstLogBo.add(upgradeLog);
			}
		}

		return tunnelSettingIPInfo.size() > 0 ? tunnelSettingIPInfo : null;
	}

	private static List<TunnelSetting> getAllTunnelSetting(List<HmUpgradeLog> gradeLog) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of tunnel_setting.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("tunnel_setting");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in tunnel_setting table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<TunnelSetting> tunnelSettingInfo = new ArrayList<TunnelSetting>();
		boolean isColPresent;
		String colName;
		TunnelSetting tunnelSetting;
		tunnelChangeFlag = ifContainPasswordInTunnelSettingIPAddress();

		for (int i = 0; i < rowCount; i++)
		{
			tunnelSetting = new TunnelSetting();

			/**
			 * Set tunnelname
			 */
			colName = "tunnelname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tunnel_setting' data be lost, cause: 'tunnelname' column is not exist.");
				/**
				 * The tunnelname column must be exist in the table of tunnel_setting
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tunnel_setting' data be lost, cause: 'name' column is not exist.");
				continue;
			}
			tunnelSetting.setTunnelName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tunnelSetting.setId(Long.valueOf(id));

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tunnel_setting", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tunnel_setting' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			tunnelSetting.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			tunnelSetting.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set enabletype
			 */
			colName = "enabletype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"tunnel_setting", colName);
			String enabletype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tunnelSetting.setEnableType(AhRestoreCommons.convertInt(enabletype));

			if (tunnelSetting.getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING) {
				/**
				 * Set roamingenable
				 */
				colName = "roamingenable";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tunnel_setting", colName);
				// remove this field from 3.4r1
				if (isColPresent) {
					if (AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName))) {
						HmUpgradeLog upgradeLog = new HmUpgradeLog();
						upgradeLog.setFormerContent("Nomadic roaming was enabled in the tunnel policy \""+tunnelSetting.getTunnelName()+"\"");
						upgradeLog.setPostContent("Because nomadic roaming is no longer supported, " +
								NmsUtil.getOEMCustomer().getNmsName() + " has removed it from all tunnel policies in which it had been enabled.");
						upgradeLog.setRecommendAction("No action is required.");
						upgradeLog.setOwner(ownerDomain);
						upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
						upgradeLog.setAnnotation("Click to add an annotation");
						gradeLog.add(upgradeLog);
					}
				}

				/**
				 * Set unroaminginterval
				 */
				colName = "unroaminginterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String unroaminginterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
				tunnelSetting.setUnroamingInterval(AhRestoreCommons.convertInt(unroaminginterval));

				/**
				 * Set unroamingageout
				 */
				colName = "unroamingageout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"inter_roaming", colName);
				String unroamingageout = isColPresent ? xmlParser.getColVal(i, colName) : "0";
				tunnelSetting.setUnroamingAgeout(AhRestoreCommons.convertInt(unroamingageout));

			} else {
				if (tunnelSetting.getEnableType() == TunnelSetting.TUNNELSETTING_STATIC_TUNNELING) {
					/**
					 * Set tunneltotype
					 */
					colName = "tunneltotype";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tunnel_setting", colName);
					String tunneltotype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
					tunnelSetting.setTunnelToType(AhRestoreCommons.convertInt(tunneltotype));

					/**
					 * Set iprangestart
					 */
					colName = "iprangestart";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tunnel_setting", colName);
					String iprangestart = isColPresent ? xmlParser.getColVal(i, colName) : "";
					tunnelSetting.setIpRangeStart(AhRestoreCommons.convertString(iprangestart));

					/**
					 * Set iprangeend
					 */
					colName = "iprangeend";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tunnel_setting", colName);
					String iprangeend = isColPresent ? xmlParser.getColVal(i, colName) : "";
					tunnelSetting.setIpRangeEnd(AhRestoreCommons.convertString(iprangeend));

					/**
					 * Set password
					 */
					colName = "password";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tunnel_setting", colName);
					String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
					tunnelSetting.setPassword(AhRestoreCommons.convertString(password));

					/**
					 * Set ip_address_id
					 */
					colName = "ip_address_id";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tunnel_setting", colName);
					String ip_address_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
					IpAddress newip = null;
					if (!ip_address_id.equals("") && !ip_address_id.trim().equalsIgnoreCase("null")) {
					     Long newId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(ip_address_id.trim()));
					     newip = AhRestoreNewTools.CreateBoWithId(IpAddress.class, newId);
				    }
				    if (newip == null) {
				    	Map<String, Object> map = new HashMap<String, Object>();
						map.put("addressname", BeParaModule.DEFAULT_IP_ADDRESS_NAME);
				    	newip = HmBeParaUtil.getDefaultProfile(IpAddress.class, map);
					}
				    tunnelSetting.setIpAddress(newip);

				    /*
				     * set source ips
				     */
				    List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
				    tunnelSetting.setIpAddressList(getAllTunnelSettingIPAddressNew(id, lstLogBo));

				    // there is upgrde logs
					for (HmUpgradeLog upLog : lstLogBo) {
						upLog.setFormerContent("A source subnet in Tunnel Policy \""+name+"\" " + upLog.getFormerContent());
						upLog.setPostContent(upLog.getPostContent()+" the source subnet in Tunnel Policy \""+name+"\".");
						upLog.setRecommendAction("No action is required.");
						upLog.setOwner(ownerDomain);
						upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
						upLog.setAnnotation("Click to add an annotation");
						gradeLog.add(upLog);
					}
				}
				if (tunnelChangeFlag) {
					AhRestoreNewMapTools.setTunnelSettingOldInfo(id, tunnelSetting);
					continue;
				}
			}
			tunnelSettingInfo.add(tunnelSetting);
		}

		return tunnelSettingInfo;
	}

	public static boolean restoreTunnelSetting()
	{
		try
		{
			List<HmUpgradeLog> allUpgradeLog = new ArrayList<HmUpgradeLog>();
			List<TunnelSetting> allTunnelSetting = getAllTunnelSetting(allUpgradeLog);
			if(null == allTunnelSetting) {
				AhRestoreDBTools.logRestoreMsg("There is no Tunnel Policy to restore.");
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				for (TunnelSetting tunnelSetting : allTunnelSetting) {
					lOldId.add(tunnelSetting.getId());
				}

				QueryUtil.restoreBulkCreateBos(allTunnelSetting);

				for(int i=0; i<allTunnelSetting.size(); i++)
				{
					AhRestoreNewMapTools.setMapIdentityBasedTunnel(lOldId.get(i), allTunnelSetting.get(i).getId());
				}

				// insert upgrade log from 3.4r1
				if (!allUpgradeLog.isEmpty()) {
					try {
						QueryUtil.restoreBulkCreateBos(allUpgradeLog);
					} catch (Exception e) {
						AhRestoreDBTools
								.logRestoreMsg("insert upgrade log error for tunnel policy");
						AhRestoreDBTools.logRestoreMsg(e.getMessage());
					}
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static Map<String, Set<MacFilter>> getAllHiveProfileMacFilter() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_profile_mac_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_profile_mac_filter");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacFilter>> macFilterInfo = new HashMap<String, Set<MacFilter>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set hive_profile_id
			 */
			colName = "hive_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile_mac_filter", colName);
			if (!isColPresent)
			{
				/**
				 * The hive_profile_id column must be exist in the table of hive_profile_mac_filter
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set mac_filter_id
			 */
			colName = "mac_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile_mac_filter", colName);
			if (!isColPresent)
			{
				/**
				 * The mac_filter_id column must be exist in the table of hive_profile_mac_filter
				 */
				continue;
			}

			String macFilterId = xmlParser.getColVal(i, colName);
			if (macFilterId == null || macFilterId.trim().equals("")
				|| macFilterId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newMacFilterId = AhRestoreNewMapTools.getMapMacFilter(Long.parseLong(macFilterId.trim()));
			MacFilter newFilter = AhRestoreNewTools.CreateBoWithId(MacFilter.class,newMacFilterId);

			if (newFilter != null) {
				if (macFilterInfo.get(profileId) == null) {
					Set<MacFilter> macFilterSet= new HashSet<MacFilter>();
					macFilterSet.add(newFilter);
					macFilterInfo.put(profileId, macFilterSet);
				} else {
					macFilterInfo.get(profileId).add(newFilter);
				}
			}
		}

		return macFilterInfo;
	}

	private static List<HiveProfile> getAllHiveProfile() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_profile");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in hive_profile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveProfile> hiveProfileInfo = new ArrayList<HiveProfile>();
		List<HiveProfile> hiveProfileInfoNameEmpty = new ArrayList<HiveProfile>();
		boolean isColPresent;
		String colName;
		HiveProfile hiveProfileDTO;

		for (int i = 0; i < rowCount; i++)
		{
			hiveProfileDTO = new HiveProfile();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			hiveProfileDTO.setId(Long.valueOf(id));

			/**
			 * Set hivename
			 */
			colName = "hivename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_profile' data be lost, cause: 'hivename' column is not exist.");
				/**
				 * The hivename column must be exist in the table of hive_profile
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);

			if (BeParaModule.DEFAULT_HIVEID_PROFILE_NAME.equals(name.trim())) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("hiveName", name);
				HiveProfile newhiveProfile = HmBeParaUtil.getDefaultProfile(HiveProfile.class, map);
				if (null != newhiveProfile) {
					AhRestoreNewMapTools.setMapHives(AhRestoreCommons.convertLong(id), newhiveProfile.getId());
				}
				continue;
			}

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.restoreLog(BeLogTools.DEBUG, "Restore table 'hive_profile' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			hiveProfileDTO.setOwner(ownerDomain);

			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hive_profile' will reset the hive name, cause: 'hivename' column value is null.");
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, hiveProfileDTO.getOwner().getId());
				if (dm!=null) {
					name=dm.getDomainName();
					hiveProfileDTO.setHiveName(name.trim());
					hiveProfileInfoNameEmpty.add(hiveProfileDTO);
				} else {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'hive_profile' will lost data, cause: 'hivename' column value is null. domain ID:" + ownerId);
					continue;
				}
			}
			hiveProfileDTO.setHiveName(name.trim());

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			hiveProfileDTO.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_profile", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			if (AhRestoreCommons.convertStringToBoolean(defaultflag)){
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_profile' data be lost, cause: 'defaultflag' column is not exist.");
				continue;
			}

			/**
			 * Set defaultflag
			 */
			hiveProfileDTO.setDefaultFlag(false);
			/**
			 * Set defaultaction
			 */
			colName = "defaultaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String defaultaction = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			hiveProfileDTO.setDefaultAction(AhRestoreCommons.convertInt(defaultaction));

			/**
			 * Set enabledpassword
			 */
			colName = "enabledpassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String enabledpassword = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			hiveProfileDTO.setEnabledPassword(AhRestoreCommons.convertStringToBoolean(enabledpassword));

			/**
			 * Set generatepasswordtype
			 */
			colName = "generatepasswordtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String generatepasswordtype = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			hiveProfileDTO.setGeneratePasswordType(AhRestoreCommons.convertInt(generatepasswordtype));

			/**
			 * Set fragthreshold
			 */
			colName = "fragthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String fragthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "2346";
			hiveProfileDTO.setFragThreshold(AhRestoreCommons.convertInt(fragthreshold));

			/**
			 * Set hivepassword
			 */
			colName = "hivepassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String hivepassword = isColPresent ? xmlParser.getColVal(i, colName) : "";
			hiveProfileDTO.setHivePassword(AhRestoreCommons.convertString(hivepassword));

			/**
			 * Set eth0priority
			 */
			colName = "eth0priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String eth0priority = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			hiveProfileDTO.setEth0Priority(AhRestoreCommons.convertInt(eth0priority));

			/**
			 * Set eth1priority
			 */
			colName = "eth1priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String eth1priority = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			hiveProfileDTO.setEth1Priority(AhRestoreCommons.convertInt(eth1priority));

			/**
			 * Set agg0priority
			 */
			colName = "agg0priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String agg0priority = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			hiveProfileDTO.setAgg0Priority(AhRestoreCommons.convertInt(agg0priority));

			/**
			 * Set red0priority
			 */
			colName = "red0priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String red0priority = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			hiveProfileDTO.setRed0Priority(AhRestoreCommons.convertInt(red0priority));

			/**
			 * Set l3trafficport
			 */
			colName = "l3trafficport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String l3trafficport = isColPresent ? xmlParser.getColVal(i, colName) : "3000";
			if (AhRestoreCommons.convertInt(l3trafficport)<1500){
				hiveProfileDTO.setL3TrafficPort(1500);
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				upgradeLog.setOwner(ownerDomain);
				upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				upgradeLog.setAnnotation("Click to add an annotation");
				upgradeLog.setFormerContent("The control port number for " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " communications in " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " \"<" +hiveProfileDTO.getHiveName() + ">\" was <" + AhRestoreCommons.convertInt(l3trafficport) + ">");
				upgradeLog.setPostContent("Because the previous port number was outside the new range (1500-65000), " +
						NmsUtil.getOEMCustomer().getNmsName() + " changed it to <" + 1500 + ">.");
				upgradeLog.setRecommendAction("If there are firewall devices between " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " members, make sure their policies permit UDP traffic on the new port number.");
				try {
					QueryUtil.createBo(upgradeLog);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " profile upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			} else if (AhRestoreCommons.convertInt(l3trafficport)>65000){
				hiveProfileDTO.setL3TrafficPort(65000);
				HmUpgradeLog upgradeLog = new HmUpgradeLog();
				upgradeLog.setOwner(ownerDomain);
				upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				upgradeLog.setAnnotation("Click to add an annotation");
				upgradeLog.setFormerContent("The control port number for " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " communications in " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " \"<" +hiveProfileDTO.getHiveName() + ">\" was <" + AhRestoreCommons.convertInt(l3trafficport) + ">");
				upgradeLog.setPostContent("Because the previous port number was outside the new range (1500-65000), " +
						NmsUtil.getOEMCustomer().getNmsName() + " changed it to <" + 65000 + ">.");
				upgradeLog.setRecommendAction("If there are firewall devices between " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " members, make sure their policies permit UDP traffic on the new port number.");
				try {
					QueryUtil.createBo(upgradeLog);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " profile upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			} else {
				hiveProfileDTO.setL3TrafficPort(AhRestoreCommons.convertInt(l3trafficport));
			}
			/**
			 * Set rtsthreshold
			 */
			colName = "rtsthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String rtsthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "2346";
			hiveProfileDTO.setRtsThreshold(AhRestoreCommons.convertInt(rtsthreshold));

			/**
			 * Set enabledthreshold
			 */
			colName = "enabledthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String enabledthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "";
			hiveProfileDTO.setEnabledThreshold(AhRestoreCommons.convertStringToBoolean(enabledthreshold));

			/**
			 * Set connectionthreshold
			 */
			colName = "connectionthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String connectionthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "-80";
			if ((short)AhRestoreCommons.convertInt(connectionthreshold)==1){
				hiveProfileDTO.setConnectionThreshold((short)-85);
			} else if ((short)AhRestoreCommons.convertInt(connectionthreshold)==2){
				hiveProfileDTO.setConnectionThreshold((short)-80);
			} else if ((short)AhRestoreCommons.convertInt(connectionthreshold)==3){
				hiveProfileDTO.setConnectionThreshold((short)-75);
			} else {
				hiveProfileDTO.setConnectionThreshold((short)AhRestoreCommons.convertInt(connectionthreshold));
			}
			/**
			 * Set pollinginterval
			 */
			colName = "pollinginterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String pollinginterval = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			hiveProfileDTO.setPollingInterval(AhRestoreCommons.convertInt(pollinginterval));

			/**
			 * Set hive_dos_id
			 */
			colName = "hive_dos_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String hive_dos_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if (!hive_dos_id.equals("") && !hive_dos_id.trim().equalsIgnoreCase("null")) {
				Long newHiveDosId = AhRestoreNewMapTools.getMapDosPrevention(Long.parseLong(hive_dos_id.trim()));
				DosPrevention hiveDos = AhRestoreNewTools.CreateBoWithId(DosPrevention.class, newHiveDosId);
				if (hiveDos != null) {
					hiveProfileDTO.setHiveDos(hiveDos);
				}
			}

			/**
			 * Set station_dos_id
			 */
			colName = "station_dos_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			String station_dos_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if (!station_dos_id.equals("") && !station_dos_id.trim().equalsIgnoreCase("null")) {
				Long newStationDosId = AhRestoreNewMapTools.getMapDosPrevention(Long.parseLong(station_dos_id.trim()));
				DosPrevention stationDos = AhRestoreNewTools.CreateBoWithId(DosPrevention.class, newStationDosId);
				if (stationDos != null) {
					hiveProfileDTO.setStationDos(stationDos);
				}
			}

			/**
			 * Set inter_roaming_id
			 */
			colName = "inter_roaming_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hive_profile", colName);
			if (isColPresent) {
				String inter_roaming_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!inter_roaming_id.equals("") && !inter_roaming_id.trim().equalsIgnoreCase("null")) {
					Long newInterRoamingId = AhRestoreNewMapTools.getMapLayer3Romaing(Long.parseLong(inter_roaming_id.trim()));
					InterRoaming interRoaming=null;
					if (newInterRoamingId!=null) {
						interRoaming = QueryUtil.findBoById(InterRoaming.class, newInterRoamingId);
					}
					if(interRoaming!=null){
						hiveProfileDTO.setEnabledL3Setting(interRoaming.getEnabledL3Setting());
						hiveProfileDTO.setKeepAliveAgeout(interRoaming.getKeepAliveAgeout());
						hiveProfileDTO.setKeepAliveInterval(interRoaming.getKeepAliveInterval());
						hiveProfileDTO.setUpdateAgeout(interRoaming.getUpdateAgeout());
						hiveProfileDTO.setUpdateInterval(interRoaming.getUpdateInterval());
					}
				}
			} else {
				/**
				 * Set enabledl3setting
				 */
				colName = "enabledl3setting";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String enabledl3setting = isColPresent ? xmlParser.getColVal(i, colName) : "";
				hiveProfileDTO.setEnabledL3Setting(AhRestoreCommons.convertStringToBoolean(enabledl3setting));

				/**
				 * Set keepaliveinterval
				 */
				colName = "keepaliveinterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String keepaliveinterval = isColPresent ? xmlParser.getColVal(i, colName) : "10";
				hiveProfileDTO.setKeepAliveInterval(AhRestoreCommons.convertInt(keepaliveinterval));

				/**
				 * Set keepaliveageout
				 */
				colName = "keepaliveageout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String keepaliveageout = isColPresent ? xmlParser.getColVal(i, colName) : "5";
				hiveProfileDTO.setKeepAliveAgeout(AhRestoreCommons.convertInt(keepaliveageout));


				/**
				 * Set updateinterval
				 */
				colName = "updateinterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String updateinterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
				hiveProfileDTO.setUpdateInterval(AhRestoreCommons.convertInt(updateinterval));

				/**
				 * Set updateageout
				 */
				colName = "updateageout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String updateageout = isColPresent ? xmlParser.getColVal(i, colName) : "60";
				hiveProfileDTO.setUpdateAgeout(AhRestoreCommons.convertInt(updateageout));

				/**
				 * Set neighbortypeback
				 */
				colName = "neighbortypeback";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String neighbortypeback = isColPresent ? xmlParser.getColVal(i, colName) : "t";
				hiveProfileDTO.setNeighborTypeBack(AhRestoreCommons.convertStringToBoolean(neighbortypeback));

				/**
				 * Set neighbortypeaccess
				 */
				colName = "neighbortypeaccess";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_profile", colName);
				String neighbortypeaccess = isColPresent ? xmlParser.getColVal(i, colName) : "t";
				hiveProfileDTO.setNeighborTypeAccess(AhRestoreCommons.convertStringToBoolean(neighbortypeaccess));

			}

//			/**
//			 * Set owner
//			 */
//			colName = "owner";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"hive_profile", colName);
//			String owner = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			 TBD			hiveProfileDTO.setOwner(AhRestoreCommons.convertString(owner));


			if (hiveProfileDTO.getDefaultFlag()) {
				hiveProfileInfo.add(0, hiveProfileDTO);
			} else {
				hiveProfileInfo.add(hiveProfileDTO);
			}
		}
		// fix bug 27502
		List<HiveProfile> hiveProfileInfoNameExist = new ArrayList<HiveProfile>();
		if (!hiveProfileInfoNameEmpty.isEmpty()) {
			for(HiveProfile hm: hiveProfileInfoNameEmpty) {
				for(HiveProfile h: hiveProfileInfo) {
					if (!hm.getId().equals(h.getId()) 
							&& hm.getHiveName().equals(h.getHiveName())
							&& hm.getOwner().getId().equals(h.getOwner().getId())) {
						hiveProfileInfoNameExist.add(hm);
						break;
					}
				}
			}
		}
		if (!hiveProfileInfoNameExist.isEmpty()) {
			int i=1;
			for(HiveProfile h: hiveProfileInfoNameExist){
				boolean loopFlg=true;
				while (loopFlg) {
					boolean existFlg = false;
					String hName = h.getHiveName();
					if (hName.length()>28) {
						hName=hName.substring(0, 28) + "_" + i++;
					} else {
						hName=hName + "_" + i++;
					}
					for(HiveProfile hif: hiveProfileInfo) {
						if (hif.getHiveName().equals(hName)
								&& hif.getOwner().getId().equals(h.getOwner().getId())) {
							existFlg = true;
							break;
						}
					}
					if (existFlg==false) {
						loopFlg=false;
						h.setHiveName(hName);
					}
				}
			}
		}
		for(HiveProfile h: hiveProfileInfoNameExist){
			for(HiveProfile hif : hiveProfileInfo){
				if(hif.getId().equals(h.getId())){
					hif.setHiveName(h.getHiveName());
				}
			}
		}
		
		//end fix bug 27502

		return hiveProfileInfo;
	}

	public static boolean restoreHiveProfile()
	{
		try
		{
			List<HiveProfile> allHiveProfile = getAllHiveProfile();
			Map<String, Set<MacFilter>> allMacFilter = getAllHiveProfileMacFilter();
			if(null == allHiveProfile)
			{
				AhRestoreDBTools.logRestoreMsg("allHiveProfile is null");

				return false;
			}
			else
			{
				if (null != allMacFilter) {
					for (HiveProfile tempHiveProfile : allHiveProfile) {
						if (tempHiveProfile != null) {
							tempHiveProfile.setMacFilters(allMacFilter
									.get(tempHiveProfile.getId().toString()));
						}
					}
				}
				List<Long> lOldId = new ArrayList<Long>();

				for (HiveProfile hiveProfile : allHiveProfile) {
					lOldId.add(hiveProfile.getId());
				}

				QueryUtil.restoreBulkCreateBos(allHiveProfile);

				for(int i=0; i < allHiveProfile.size(); ++i)
				{
					AhRestoreNewMapTools.setMapHives(lOldId.get(i), allHiveProfile.get(i).getId());
				}

			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static Map<String, Set<MacFilter>> getAllMacFilters(String tableName,
			String... columnsName) throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of $tableName.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MacFilter>> macFilterInfo = new HashMap<String, Set<MacFilter>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * default set ssid_profile_id
			 */
			colName = (ArrayUtils.isEmpty(columnsName) ? "ssid_profile_id" : columnsName[0]);
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				/**
				 * The column must be exist in the table of $tableName
				 */
				continue;
			}
			String profileId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(profileId)) {
				continue;
			}

			/**
			 * default set mac_filter_id
			 */
			colName = (ArrayUtils.isEmpty(columnsName) ? "mac_filter_id" : columnsName[1]);
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				/**
				 * The column must be exist in the table of $tableName
				 */
				continue;
			}
			String macFilterId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(macFilterId)) {
				continue;
			}

			Long newMacFilterId = AhRestoreNewMapTools.getMapMacFilter(Long.parseLong(macFilterId
					.trim()));
			MacFilter newFilter = AhRestoreNewTools.CreateBoWithId(MacFilter.class, newMacFilterId);

			if (newFilter != null) {
				if (macFilterInfo.get(profileId) == null) {
					Set<MacFilter> macFilterSet = new HashSet<MacFilter>();
					macFilterSet.add(newFilter);
					macFilterInfo.put(profileId, macFilterSet);
				} else {
					macFilterInfo.get(profileId).add(newFilter);
				}
			}
		}

		return macFilterInfo;
	}

	private static Map<String, Set<Scheduler>> getAllSchedulers(String tableName,
			String... columnsName) throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		AhRestoreDBTools.logRestoreMsg("getAllSchedulers: for "+tableName);
		/**
		 * Check validation of $tableName.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<Scheduler>> schedulerInfo = new HashMap<String, Set<Scheduler>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * default set ssid_profile_id
			 */
			colName = (ArrayUtils.isEmpty(columnsName) ? "ssid_profile_id" : columnsName[0]);
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				/**
				 * The column must be exist in the table of $tableName
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(profileId)) {
				continue;
			}

			/**
			 * default set scheduler_id
			 */
			colName = (ArrayUtils.isEmpty(columnsName) ? "scheduler_id" : columnsName[1]);
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent) {
				/**
				 * The column must be exist in the table of $tableName
				 */
				continue;
			}

			String schedulerId = xmlParser.getColVal(i, colName);
			if (StringUtils.isBlank(schedulerId)) {
				continue;
			}

			Long newschedulerId = AhRestoreNewMapTools.getMapSchedule(Long.parseLong(schedulerId
					.trim()));
			Scheduler scheduler = AhRestoreNewTools.CreateBoWithId(Scheduler.class, newschedulerId);

			if (scheduler != null) {
				if (schedulerInfo.get(profileId) == null) {
					Set<Scheduler> schedulerSet = new HashSet<Scheduler>();
					schedulerSet.add(scheduler);
					schedulerInfo.put(profileId, schedulerSet);
				} else {
					schedulerInfo.get(profileId).add(scheduler);
				}
			}
		}

		return schedulerInfo;
	}

	private static Map<String, Set<LocalUserGroup>> getAllSsidProfileLocalUserGroup() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ssid_local_user_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ssid_local_user_group");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<LocalUserGroup>> localUserGroupInfo = new HashMap<String, Set<LocalUserGroup>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_local_user_group", colName);
			if (!isColPresent)
			{
				/**
				 * The ssid_profile_id column must be exist in the table of ssid_local_user_group
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set local_user_group_id
			 */
			colName = "local_user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_local_user_group", colName);
			if (!isColPresent)
			{
				/**
				 * The local_user_group_id column must be exist in the table of ssid_local_user_group
				 */
				continue;
			}

			String groupId = xmlParser.getColVal(i, colName);
			if (groupId == null || groupId.trim().equals("")
				|| groupId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newGroupId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.parseLong(groupId.trim()));
			LocalUserGroup localUserGroup = AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class,newGroupId);

			if (localUserGroup != null) {
				if (localUserGroupInfo.get(profileId) == null) {
					Set<LocalUserGroup> localUserGroupSet= new HashSet<LocalUserGroup>();
					localUserGroupSet.add(localUserGroup);
					localUserGroupInfo.put(profileId, localUserGroupSet);
				} else {
					localUserGroupInfo.get(profileId).add(localUserGroup);
				}
			}
		}
		return localUserGroupInfo;
	}

	private static Map<String, Set<LocalUserGroup>> getAllSsidProfileRadiusUserGroup() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ssid_local_user_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ssid_radius_user_group");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<LocalUserGroup>> localUserGroupInfo = new HashMap<String, Set<LocalUserGroup>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_radius_user_group", colName);
			if (!isColPresent)
			{
				/**
				 * The ssid_profile_id column must be exist in the table of ssid_local_user_group
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set local_user_group_id
			 */
			colName = "local_user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_radius_user_group", colName);

			if (!isColPresent)
			{
				/**
				 * The local_user_group_id column must be exist in the table of ssid_local_user_group
				 */
				continue;
			}

			String groupId = xmlParser.getColVal(i, colName);
			if (groupId == null || groupId.trim().equals("")
				|| groupId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newGroupId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.parseLong(groupId.trim()));
			LocalUserGroup localUserGroup = AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class,newGroupId);

			if (localUserGroup != null) {
				if (localUserGroupInfo.get(profileId) == null) {
					Set<LocalUserGroup> localUserGroupSet= new HashSet<LocalUserGroup>();
					localUserGroupSet.add(localUserGroup);
					localUserGroupInfo.put(profileId, localUserGroupSet);
				} else {
					localUserGroupInfo.get(profileId).add(localUserGroup);
				}
			}
		}
		return localUserGroupInfo;
	}


	private static Map<String, Set<UserProfile>> getAllSsidProfileRadiusUserProfiles() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of ssid_profile_user_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ssid_profile_user_profile");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<UserProfile>> userProfileInfo = new HashMap<String, Set<UserProfile>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile_user_profile", colName);
			if (!isColPresent)
			{
				/**
				 * The ssid_profile_id column must be exist in the table of ssid_profile_user_profile
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set user_profile_id
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile_user_profile", colName);
			if (!isColPresent)
			{
				/**
				 * The scheduler_id column must be exist in the table of ssid_profile_user_profile
				 */
				continue;
			}

			String userProfileId = xmlParser.getColVal(i, colName);
			if (userProfileId == null || userProfileId.trim().equals("")
				|| userProfileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newUserProfileId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userProfileId.trim()));
			UserProfile userProfile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserProfileId);

			if (userProfile != null) {
				if (userProfileInfo.get(profileId) == null) {
					Set<UserProfile> userProfileSet= new HashSet<UserProfile>();
					userProfileSet.add(userProfile);
					userProfileInfo.put(profileId, userProfileSet);
				} else {
					userProfileInfo.get(profileId).add(userProfile);
				}
			}

		}

		return userProfileInfo;
	}


	private static List<SsidProfile> getAllSsidProfile() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ssid_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ssid_profile");
		if (!restoreRet)
		{
			return null;
		}
		/**
		 * No one row data stored in ssid_profile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<SsidProfile> ssidProfileInfo = new ArrayList<SsidProfile>();
		boolean isColPresent;
		String colName;
		SsidProfile ssidProfileDTO;
		boolean blnColDefaultSetting;
		boolean blnColRadio;

		for (int i = 0; i < rowCount; i++)
		{
			ssidProfileDTO = new SsidProfile();

			/**
			 * Set ssidname
			 */
			colName = "ssidname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ssid_profile' data be lost, cause: 'ssidname' column is not exist.");
				/**
				 * The ssidname column must be exist in the table of ssid_profile
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ssid_profile' data be lost, cause: 'ssidname' column value is null.");
				continue;
			}
			ssidProfileDTO.setSsidName(name.trim());

			/**
			 * Set ssid
			 */
			colName = "ssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ssid = isColPresent ? xmlParser.getColVal(i, colName) : name;
			ssidProfileDTO.setSsid(AhRestoreCommons.convertString(ssid));

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setId(Long.valueOf(id));

//			AhRestoreMapTool.setMapSsid(id,name.trim());
			boolean bool = false;
			for (String singleName : BeParaModule.SSID_PROFILE_NAMES) {
				if (singleName.equals(name.trim())) {
				    bool = true;
				    break;
			    }
			}
			if (bool) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ssidName", name);
				SsidProfile newBo = HmBeParaUtil.getDefaultProfile(SsidProfile.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapSsid(AhRestoreCommons.convertLong(id), newBo.getId());
				}
				continue;
			}

			/**
			 * get PPSK_SERVER_ID to generate map
			 */
			colName = "ppsk_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ppskServerIdStr = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (ppskServerIdStr != null && !"".equals(ppskServerIdStr)) {
				AhRestoreNewMapTools.setMapPpskServerHiveAp(AhRestoreCommons.convertLong(ppskServerIdStr), Long.valueOf(id));
			}

			/**
			 * Set comment
			 */
			colName = "comment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setComment(AhRestoreCommons.convertString(description));

			/**
			 * Set defaultflag
			 */
			ssidProfileDTO.setDefaultFlag(false);

			/**
			 * Set defaultaction
			 */
			colName = "defaultaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String defaultaction = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setDefaultAction(AhRestoreCommons.convertInt(defaultaction));

			/**
			 * Set authentication
			 */
			colName = "authentication";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String authentication = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidProfileDTO.setAuthentication(AhRestoreCommons.convertInt(authentication));

			/**
			 * Set broadcase
			 */
			colName = "broadcase";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String broadcase = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setBroadcase(AhRestoreCommons.convertStringToBoolean(broadcase));

			/**
			 * Set dtimsetting
			 */
			colName = "dtimsetting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String dtimsetting = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setDtimSetting(AhRestoreCommons.convertInt(dtimsetting));

			/**
			 * Set encryption
			 */
			colName = "encryption";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String encryption = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidProfileDTO.setEncryption(AhRestoreCommons.convertInt(encryption));

			/**
			 * Set rtsthreshold
			 */
			colName = "rtsthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String rtsthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "2346";
			ssidProfileDTO.setRtsThreshold(AhRestoreCommons.convertInt(rtsthreshold));

			/**
			 * Set fragthreshold
			 */
			colName = "fragthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String fragthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "2346";
			ssidProfileDTO.setFragThreshold(AhRestoreCommons.convertInt(fragthreshold));

			/**
			 * Set updateinterval
			 */
			colName = "updateinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String updateinterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			ssidProfileDTO.setUpdateInterval(AhRestoreCommons.convertInt(updateinterval));

			/**
			 * Set ageout
			 */
			colName = "ageout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ageout = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			ssidProfileDTO.setAgeOut(AhRestoreCommons.convertInt(ageout));

			/**
			 * Set maxclient
			 */
			colName = "maxclient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String maxclient = isColPresent ? xmlParser.getColVal(i, colName) : "100";
			ssidProfileDTO.setMaxClient(AhRestoreCommons.convertInt(maxclient));

			/**
			 * Set clientageout
			 */
			colName = "clientageout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String clientageout = isColPresent ? xmlParser.getColVal(i, colName) : "5";
			ssidProfileDTO.setClientAgeOut(AhRestoreCommons.convertInt(clientageout));

			/**
			 * Set localcachetimeout
			 */
			colName = "localcachetimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String localcachetimeout = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SsidProfile.LOCAL_CACHE_TIMEOUT);
			ssidProfileDTO.setLocalCacheTimeout(AhRestoreCommons.convertInt(localcachetimeout));

			if(ssidProfileDTO.getLocalCacheTimeout()>604800){
				ssidProfileDTO.setLocalCacheTimeout(604800);
			}
			/**
			 * Set eaptimeout
			 */
			colName = "eaptimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String eaptimeout = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			ssidProfileDTO.setEapTimeOut(AhRestoreCommons.convertInt(eaptimeout));

			/**
			 * Set eapretries
			 */
			colName = "eapretries";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String eapretries = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidProfileDTO.setEapRetries(AhRestoreCommons.convertInt(eapretries));

			/**
			 * Set hide
			 */
			colName = "hide";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String hide = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setHide(AhRestoreCommons.convertStringToBoolean(hide));

			/**
			 * Set macauthenabled
			 */
			colName = "macauthenabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String macauthenabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setMacAuthEnabled(AhRestoreCommons.convertStringToBoolean(macauthenabled));

			/**
			 * Set fallbacktoecwp
			 */
			colName = "fallbacktoecwp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String fallbacktoecwp = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setFallBackToEcwp(AhRestoreCommons.convertStringToBoolean(fallbacktoecwp));


			/**
			 * Set enabledwmm
			 */
			colName = "enabledwmm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledwmm = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setEnabledwmm(AhRestoreCommons.convertStringToBoolean(enabledwmm));

			/**
			 * Set enabledlegacy
			 */
			colName = "enabledlegacy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledlegacy = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabledLegacy(AhRestoreCommons.convertStringToBoolean(enabledlegacy));

			/**
			 * Set enabledunscheduled
			 */
			colName = "enabledunscheduled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledunscheduled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnabledUnscheduled(AhRestoreCommons.convertStringToBoolean(enabledunscheduled));

			if(ssidProfileDTO.getEnabledUnscheduled()){
				ssidProfileDTO.setEnabledwmm(true);
			}

			/**
			 * Set mgmtkey
			 */
			colName = "mgmtkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String mgmtkey = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidProfileDTO.setMgmtKey(AhRestoreCommons.convertInt(mgmtkey));

			/**
			 * Set accessmode
			 */
			colName = "accessmode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			int accessmode;
			if (!isColPresent) {
				if (ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_OPEN) {
					accessmode = SsidProfile.ACCESS_MODE_OPEN;
				} else if (ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_WEP_PSK
						|| ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
					accessmode = SsidProfile.ACCESS_MODE_WEP;
				} else if (ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_WPA_PSK
						|| ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_WPA2_PSK
						|| ssidProfileDTO.getMgmtKey()== SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
					accessmode = SsidProfile.ACCESS_MODE_WPA;
				} else {
					accessmode = SsidProfile.ACCESS_MODE_8021X;
				}
			} else {
				accessmode = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
			}
			ssidProfileDTO.setAccessMode(accessmode);

			/**
			 * Set enableddefaultsetting
			 */
			colName = "enableddefaultsetting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableddefaultsetting = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnabledDefaultSetting(AhRestoreCommons.convertStringToBoolean(enableddefaultsetting));
			blnColDefaultSetting = isColPresent;

			/**
			 * Set enableduseguestmanager
			 */
			colName = "enableduseguestmanager";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableduseguestmanager = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnabledUseGuestManager(AhRestoreCommons.convertStringToBoolean(enableduseguestmanager));

			/**
			 * Set personpskradiusauth
			 */
			colName = "personpskradiusauth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String personpskradiusauth = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setPersonPskRadiusAuth(AhRestoreCommons.convertInt(personpskradiusauth));

			/**
			 * Set preauthenticationenabled
			 */
			colName = "preauthenticationenabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String preauthenticationenabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setPreauthenticationEnabled(AhRestoreCommons.convertStringToBoolean(preauthenticationenabled));

			/**
			 * Set userinternetaccess
			 */
			colName = "userinternetaccess";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String userinternetaccess = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setUserInternetAccess(AhRestoreCommons.convertStringToBoolean(userinternetaccess));

			/**
			 * Set showExpressUserAccess
			 */
			colName = "showExpressUserAccess";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String showExpressUserAccess = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setShowExpressUserAccess(AhRestoreCommons.convertStringToBoolean(showExpressUserAccess));

			/**
			 * Set userpskmethod
			 */
			colName = "userpskmethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String userpskmethod = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
			ssidProfileDTO.setUserPskMethod(AhRestoreCommons.convertInt(userpskmethod));

			/**
			 * Set userratelimit
			 */
			colName = "userratelimit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String userratelimit = isColPresent ? xmlParser.getColVal(i, colName) : "3000";
			ssidProfileDTO.setUserRatelimit(AhRestoreCommons.convertInt(userratelimit));

			/**
			 * Set newradiustype
			 */
			colName = "newradiustype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String newradiustype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setNewRadiusType(AhRestoreCommons.convertInt(newradiustype));

			/**
			 * Set usercategory
			 */
			colName = "usercategory";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String usercategory = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidProfileDTO.setUserCategory(AhRestoreCommons.convertInt(usercategory));

			SsidSecurity ssidSecurity = new SsidSecurity();

			/**
			 * Set defaultkeyindex
			 */
			colName = "defaultkeyindex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String defaultkeyindex = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidSecurity.setDefaultKeyIndex(AhRestoreCommons.convertInt(defaultkeyindex));

			/**
			 * Set firstkeyvalue
			 */
			colName = "firstkeyvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String firstkeyvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setFirstKeyValue(AhRestoreCommons.convertStringNoTrim(firstkeyvalue));

			/**
			 * Set secondkeyvalue
			 */
			colName = "secondkeyvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String secondkeyvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setSecondKeyValue(AhRestoreCommons.convertStringNoTrim(secondkeyvalue));

			/**
			 * Set thirdkeyvalue
			 */
			colName = "thirdkeyvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String thirdkeyvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setThirdKeyValue(AhRestoreCommons.convertStringNoTrim(thirdkeyvalue));

			/**
			 * Set fourthvalue
			 */
			colName = "fourthvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String fourthvalue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setFourthValue(AhRestoreCommons.convertStringNoTrim(fourthvalue));

			/**
			 * Set proactiveenabled
			 */
			colName = "proactiveenabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String proactiveenabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setProactiveEnabled(AhRestoreCommons.convertStringToBoolean(proactiveenabled));

			/**
			 * Set strict
			 */
			colName = "strict";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String strict = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidSecurity.setStrict(AhRestoreCommons.convertStringToBoolean(strict));

			/**
			 * Set remotetkip
			 */
			colName = "remotetkip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String remotetkip = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidSecurity.setRemoteTkip(AhRestoreCommons.convertStringToBoolean(remotetkip));

			/**
			 * Set localtkip
			 */
			colName = "localtkip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String localtkip = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidSecurity.setLocalTkip(AhRestoreCommons.convertStringToBoolean(localtkip));

			/**
			 * Set rekeyperiod
			 */
			colName = "rekeyperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String rekeyperiod = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setRekeyPeriod(AhRestoreCommons.convertInt(rekeyperiod));

			/**
			 * Set ptktimeout
			 */
			colName = "ptktimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ptktimeout = isColPresent ? xmlParser.getColVal(i, colName) : "4000";
			ssidSecurity.setPtkTimeOut(AhRestoreCommons.convertInt(ptktimeout));

			/**
			 * Set ptkretries
			 */
			colName = "ptkretries";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ptkretries = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidSecurity.setPtkRetries(AhRestoreCommons.convertInt(ptkretries));

			/**
			 * Set gtktimeout
			 */
			colName = "gtktimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String gtktimeout = isColPresent ? xmlParser.getColVal(i, colName) : "4000";
			ssidSecurity.setGtkTimeOut(AhRestoreCommons.convertInt(gtktimeout));

			/**
			 * Set gtkretries
			 */
			colName = "gtkretries";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String gtkretries = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidSecurity.setGtkRetries(AhRestoreCommons.convertInt(gtkretries));
			/**
			 * Set rekeyperiodgmk
			 */
			colName = "rekeyperiodgmk";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String rekeyperiodgmk = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setRekeyPeriodGMK(AhRestoreCommons.convertInt(rekeyperiodgmk));

			/**
			 * Set rekeyperiodptk
			 */
			colName = "rekeyperiodptk";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String rekeyperiodptk = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setRekeyPeriodPTK(AhRestoreCommons.convertInt(rekeyperiodptk));

			/**
			 * Set reauthinterval
			 */
			colName = "reauthinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String reauthinterval = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setReauthInterval(AhRestoreCommons.convertInt(reauthinterval));

			/**
			 * Set pskuserlimit
			 */
			colName = "pskuserlimit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String pskuserlimit = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setPskUserLimit(AhRestoreCommons.convertInt(pskuserlimit));

			/**
			 * Set blnmacbindingenable
			 */
			colName = "blnmacbindingenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String blnmacbindingenable = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidSecurity.setBlnMacBindingEnable(AhRestoreCommons.convertStringToBoolean(blnmacbindingenable));

			/**
			 * Set replaywindow
			 */
			colName = "replaywindow";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String replaywindow = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setReplayWindow(AhRestoreCommons.convertInt(replaywindow));

			/**
			 * Set keytype
			 */
			colName = "keytype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String keytype = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			ssidSecurity.setKeyType(AhRestoreCommons.convertInt(keytype));

			/**
			 * Set enable80211w
			 */
			colName = "enable80211w";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enable80211w = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidSecurity.setEnable80211w(AhRestoreCommons.convertStringToBoolean(enable80211w));

			/**
			 * Set enableBip
			 */
			colName = "enableBip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableBip = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidSecurity.setEnableBip(AhRestoreCommons.convertStringToBoolean(enableBip));

			/**
			 * Set wpa2mfptype
			 */
			colName = "wpa2mfptype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String wpa2mfptype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			ssidSecurity.setWpa2mfpType(AhRestoreCommons.convertInt(wpa2mfptype));

			ssidProfileDTO.setSsidSecurity(ssidSecurity);

			/**
			 * Set ip_dos_id
			 */
			colName = "ip_dos_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ip_dos_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if (!ip_dos_id.equals("") && !ip_dos_id.trim().equalsIgnoreCase("null")) {
				Long newIpDosId = AhRestoreNewMapTools.getMapDosPrevention(Long.parseLong(ip_dos_id.trim()));
				DosPrevention ipDos = AhRestoreNewTools.CreateBoWithId(DosPrevention.class,newIpDosId);
				if (ipDos != null) {
					ssidProfileDTO.setIpDos(ipDos);
				}
			}

			/**
			 * Set ssid_dos_id
			 */
			colName = "ssid_dos_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ssid_dos_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if (!ssid_dos_id.equals("") && !ssid_dos_id.trim().equalsIgnoreCase("null")) {
				Long newSsidDosId = AhRestoreNewMapTools.getMapDosPrevention(Long.parseLong(ssid_dos_id.trim()));
				DosPrevention ssidDos = AhRestoreNewTools.CreateBoWithId(DosPrevention.class,newSsidDosId);
				if (ssidDos != null) {
					ssidProfileDTO.setSsidDos(ssidDos);
				}
			}

			/**
			 * Set station_dos_id
			 */
			colName = "station_dos_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String station_dos_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if (!station_dos_id.equals("") && !station_dos_id.trim().equalsIgnoreCase("null")) {
				Long newStationDosId = AhRestoreNewMapTools.getMapDosPrevention(Long.parseLong(station_dos_id.trim()));
				DosPrevention stationDos = AhRestoreNewTools.CreateBoWithId(DosPrevention.class,newStationDosId);
				if (stationDos != null) {
					ssidProfileDTO.setStationDos(stationDos);
				}
			}

			/**
			 * Set cwp_id
			 */
			colName = "cwp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String cwp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!cwp_id.equals("") && !cwp_id.trim().equalsIgnoreCase("null")) {
				Long newCwpId = AhRestoreNewMapTools.getMapCapWebPortal(Long.parseLong(cwp_id.trim()));
				Cwp cwp=null;
				if (newCwpId!=null) {
					cwp = QueryUtil.findBoById(Cwp.class,newCwpId);
				}
				ssidProfileDTO.setCwp(cwp);
			}

			/**
			 * Set cwp_userpolicy_id
			 */
			colName = "cwp_userpolicy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String cwp_userpolicy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!cwp_userpolicy_id.equals("") && !cwp_userpolicy_id.trim().equalsIgnoreCase("null")) {
				Long newCwpUserpolicyId = AhRestoreNewMapTools.getMapCapWebPortal(Long.parseLong(cwp_userpolicy_id.trim()));
				Cwp cwp=null;
				if (newCwpUserpolicyId!=null) {
					cwp = QueryUtil.findBoById(Cwp.class,newCwpUserpolicyId);
				}
				ssidProfileDTO.setUserPolicy(cwp);
			}

			/**
			 * Set cwpSelectEnabled
			 */
			colName = "cwpSelectEnabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			if (!isColPresent) {
				if (ssidProfileDTO.getCwp()!=null || ssidProfileDTO.getUserPolicy()!=null) {
					ssidProfileDTO.setCwpSelectEnabled(true);
				}
			} else {
				String cwpSelectEnabled = isColPresent ? xmlParser.getColVal(i, colName) : "f";
				ssidProfileDTO.setCwpSelectEnabled(AhRestoreCommons.convertStringToBoolean(cwpSelectEnabled));
			}


			/**
			 * Set enablearateset
			 */
			colName = "enablearateset";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enablearateset = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setEnableARateSet(AhRestoreCommons.convertStringToBoolean(enablearateset));

			/**
			 * Set enablegrateset
			 */
			colName = "enablegrateset";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enablegrateset = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setEnableGRateSet(AhRestoreCommons.convertStringToBoolean(enablegrateset));

			/**
			 * Set enablenrateset
			 */
			colName = "enablenrateset";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enablenrateset = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setEnableNRateSet(AhRestoreCommons.convertStringToBoolean(enablenrateset));

			/**
			 * Set enableACRateSet
			 */
			colName = "enableACRateSet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableACRateSet = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			ssidProfileDTO.setEnableACRateSet(AhRestoreCommons.convertStringToBoolean(enableACRateSet));

			/**
			 * Set actiontime
			 */
			colName = "actiontime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String actiontime = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			ssidProfileDTO.setActionTime(AhRestoreCommons.convertInt(actiontime));

			/**
			 * Set denyaction
			 */
			colName = "denyaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String denyaction = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidProfileDTO.setDenyAction((short)AhRestoreCommons.convertInt(denyaction));

			/**
			 * Set chkuseronly
			 */
			colName = "chkuseronly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String chkuseronly = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setChkUserOnly(AhRestoreCommons.convertStringToBoolean(chkuseronly));

			/**
			 * Set chkdeauthenticate
			 */
			colName = "chkdeauthenticate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String chkdeauthenticate = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setChkDeauthenticate(AhRestoreCommons.convertStringToBoolean(chkdeauthenticate));

			/**
			 * Set authsequence
			 */
			colName = "authsequence";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String authsequence = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(SsidProfile.AUTH_SEQUENCE_MAC_SSID_CWP);
			ssidProfileDTO.setAuthSequence(AhRestoreCommons.convertInt(authsequence));

			/**
			 * Set radiomode
			 */
			colName = "radiomode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String radiomode = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidProfileDTO.setRadioMode(AhRestoreCommons.convertInt(radiomode));
			blnColRadio = isColPresent;

			/**
			 * Set service_filter_id
			 */
			colName = "service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ServiceFilter serviceFilter=null;
			if (!service_filter_id.equals("") && !service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newServiceFilterId = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(service_filter_id.trim()));
				serviceFilter = AhRestoreNewTools.CreateBoWithId(ServiceFilter.class,newServiceFilterId);
			}
			if (serviceFilter == null) {
				serviceFilter = QueryUtil.findBoByAttribute(ServiceFilter.class, "defaultFlag", true);
			}
			ssidProfileDTO.setServiceFilter(serviceFilter);

			/**
			 * Set as_rule_group_id
			 */
			colName = "as_rule_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String as_rule_group_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			AirScreenRuleGroup asRuleGroup=null;
			if (!as_rule_group_id.equals("") && !as_rule_group_id.trim().equalsIgnoreCase("null")) {
				Long newAsRuleGroupId = AhRestoreNewMapTools.getMapAirscreenRuleGroup(Long.parseLong(as_rule_group_id.trim()));
				asRuleGroup = AhRestoreNewTools.CreateBoWithId(AirScreenRuleGroup.class,newAsRuleGroupId);
			}
			ssidProfileDTO.setAsRuleGroup(asRuleGroup);

			/**
			 * Set radius_service_assign_id
			 */
			colName = "radius_service_assign_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String radius_service_assign_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!radius_service_assign_id.equals("") && !radius_service_assign_id.trim().equalsIgnoreCase("null")) {
				Long newRadiusServiceAssignId = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.parseLong(radius_service_assign_id.trim()));
				RadiusAssignment radiusAssignment = AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class,newRadiusServiceAssignId);
				ssidProfileDTO.setRadiusAssignment(radiusAssignment);
			}

			/**
			 * Set userprofile_default_id
			 */
			colName = "userprofile_default_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String userprofile_default_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!userprofile_default_id.equals("") && !userprofile_default_id.trim().equalsIgnoreCase("null")) {
				Long newUserprofileDefaultId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userprofile_default_id.trim()));
				UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserprofileDefaultId);
				ssidProfileDTO.setUserProfileDefault(userprofile);
			}

			/**
			 * Set userprofile_selfreg_id
			 */
			colName = "userprofile_selfreg_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String userprofile_selfreg_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!userprofile_selfreg_id.equals("") && !userprofile_selfreg_id.trim().equalsIgnoreCase("null")) {
				Long newUserprofileSelfregId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userprofile_selfreg_id.trim()));
				UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserprofileSelfregId);
				ssidProfileDTO.setUserProfileSelfReg(userprofile);
			}
			
			/**
			 * Set userprofile_guest_id
			 */
			colName = "userprofile_guest_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String userprofile_guest_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!userprofile_guest_id.equals("") && !userprofile_guest_id.trim().equalsIgnoreCase("null")) {
			    Long newUserprofileGuestId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userprofile_guest_id.trim()));
			    UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserprofileGuestId);
			    ssidProfileDTO.setUserProfileGuest(userprofile);
			}

			/**
			 * Set enableOsDection
			 */
			colName = "enableosdection";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enableosdection = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnableOsDection(AhRestoreCommons.convertStringToBoolean(enableosdection));

			/**
			 * Set enableAssignUserProfile
			 */
			colName = "enableAssignUserProfile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enableAssignUserProfile = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnableAssignUserProfile(AhRestoreCommons.convertStringToBoolean(enableAssignUserProfile));

			/**
			 * Set assignUserProfileAttributeId
			 */
			colName = "assignUserProfileAttributeId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String assignUserProfileAttributeId = isColPresent ? xmlParser.getColVal(i, colName) : "11";
			ssidProfileDTO.setAssignUserProfileAttributeId(AhRestoreCommons.convertInt(assignUserProfileAttributeId));

			/**
			 * Set assignUserProfileVenderId
			 */
			colName = "assignUserProfileVenderId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String assignUserProfileVenderId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setAssignUserProfileVenderId(AhRestoreCommons.convertInt(assignUserProfileVenderId));

			/**
			 * Set enablePpskSelfReg
			 */
			colName = "enableppskselfreg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enablePpskSelfReg = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnablePpskSelfReg(AhRestoreCommons.convertStringToBoolean(enablePpskSelfReg));

			/**
			 * Set ppskServerIp
			 */
			colName = "ppskserverip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String ppskServerIp = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setPpskServerIp(AhRestoreCommons.convertString(ppskServerIp));

			/**
			 * Set ppskOpenSsid
			 */
			colName = "ppskopenssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String ppskOpenSsid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setPpskOpenSsid(AhRestoreCommons.convertString(ppskOpenSsid));
			if (!ssidProfileDTO.isEnablePpskSelfReg()) {
				ssidProfileDTO.setPpskOpenSsid("");
			}

			/**
			 * Set blnBrAsPpskServer
			 */
			colName = "blnbrasppskserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String blnBrAsPpskServer = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setBlnBrAsPpskServer(AhRestoreCommons.convertStringToBoolean(blnBrAsPpskServer));

			/**
             * Set wpa_cwp_id
			 */
			colName = "WPA_CWP_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String wpa_cwp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!wpa_cwp_id.equals("") && !wpa_cwp_id.trim().equalsIgnoreCase("null")) {
				Long newWpaCwpId = AhRestoreNewMapTools.getMapCapWebPortal(Long.parseLong(wpa_cwp_id.trim()));
				Cwp wpaCwp=null;
				if (newWpaCwpId!=null) {
					wpaCwp = QueryUtil.findBoById(Cwp.class,newWpaCwpId);
				}
				ssidProfileDTO.setWpaECwp(wpaCwp);
			}
             /**
			 * Set ppsk_cwp_id
			 */
			colName = "ppsk_cwp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String ppsk_cwp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!ppsk_cwp_id.equals("") && !ppsk_cwp_id.trim().equalsIgnoreCase("null")) {
				Long newPpskCwpId = AhRestoreNewMapTools.getMapCapWebPortal(Long.parseLong(ppsk_cwp_id.trim()));
				Cwp ppskCwp=null;
				if (newPpskCwpId!=null) {
					ppskCwp = QueryUtil.findBoById(Cwp.class,newPpskCwpId);
				}
				ssidProfileDTO.setPpskECwp(ppskCwp);
			}

			/**
			 * Set radius_service_assign_id_ppsk
			 */
			colName = "radius_service_assign_id_ppsk";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String radius_service_assign_id_ppsk = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!radius_service_assign_id_ppsk.equals("") && !radius_service_assign_id_ppsk.trim().equalsIgnoreCase("null")) {
				Long newRadiusServiceAssignIdPpsk = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.parseLong(radius_service_assign_id_ppsk.trim()));
				RadiusAssignment radiusAssignment = AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class,newRadiusServiceAssignIdPpsk);
				ssidProfileDTO.setRadiusAssignmentPpsk(radiusAssignment);
			}

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ssid_profile", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ssid_profile' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			ssidProfileDTO.setOwner(ownerDomain);

			if (!blnColDefaultSetting && blnColRadio){
				bln32r1to32r2flg= true;
				if (ssidProfileDTO.getAccessMode()==SsidProfile.ACCESS_MODE_8021X
						|| ssidProfileDTO.getMgmtKey()==SsidProfile.KEY_MGMT_DYNAMIC_WEP){
					if (ssidProfileDTO.getCwp()!=null) {
						if (ssidProfileDTO.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_REGISTERED){
							ssidProfileDTO.setAccessMode(SsidProfile.ACCESS_MODE_OPEN);
							ssidProfileDTO.setMgmtKey(SsidProfile.KEY_MGMT_OPEN);
							ssidProfileDTO.setEncryption(SsidProfile.KEY_ENC_NONE);
							ssidProfileDTO.setAuthentication(SsidProfile.KEY_AUT_OPEN);
							ssidProfileDTO.setMacAuthEnabled(false);
						} else if (ssidProfileDTO.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_AUTHENTICATED){
							ssidProfileDTO.setCwp(null);
						} else {
							ssidProfileDTO.setCwp(null);
							ssidProfileDTO.setUserProfileSelfReg(null);
						}
					}
				}
			} else {
				bln32r1to32r2flg=false;
			}

			/**
			 * Set convtounicast
			 */
			colName = "convtounicast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String convtounicast = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			ssidProfileDTO.setConvtounicast((short) AhRestoreCommons.convertInt(convtounicast));

			/**
			 * Set cuthreshold
			 */
			colName = "cuthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String cuthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			ssidProfileDTO.setCuthreshold((short) AhRestoreCommons.convertInt(cuthreshold));

			/**
			 * Set memberthreshold
			 */
			colName = "memberthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String memberthreshold = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			ssidProfileDTO.setMemberthreshold((short) AhRestoreCommons.convertInt(memberthreshold));

			/**
			 * Set enableADIntegration
			 */
			colName = "enableADIntegration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enableADIntegration = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnableADIntegration(AhRestoreCommons.convertStringToBoolean(enableADIntegration));

			/**
			 * Set enableMDM
			 */
			colName = "enableMDM";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enableMDM = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setEnableMDM(AhRestoreCommons.convertStringToBoolean(enableMDM));

            colName = "configmdm_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
            		"ssid_profile", colName);
            String configMDM_id = isColPresent ? xmlParser.getColVal(i,
                    colName) : "";
            ConfigTemplateMdm config = null;
            if (NmsUtil.isNotBlankId(configMDM_id)) {
                Long newConfigMDM_id = AhRestoreNewMapTools
                        .getMapConfigTemplateMDM(Long
                                .parseLong(configMDM_id.trim()));
                if (null != newConfigMDM_id) {
                config = AhRestoreNewTools.CreateBoWithId(
                		ConfigTemplateMdm.class, newConfigMDM_id);
                ssidProfileDTO.setConfigmdmId(config);
            }
            }


			/**
			 * Set enableMDM
			 */
			colName = "enableMDM";
			boolean isenableMDM = AhRestoreCommons.isColumnPresent(xmlParser,
					"ssid_profile", colName);
			String enableMDMstr = isenableMDM ? xmlParser.getColVal(i, colName) : "";

			colName = "configmdm_id";
			boolean isConfigMDM = AhRestoreCommons.isColumnPresent(xmlParser,
					"ssid_profile", colName);

			if (enableMDMstr.equals("t") && !isConfigMDM) {
				ConfigTemplateMdm configMDM;
				needRestoreConfigTemplateMDM=true;
				configMDM = new ConfigTemplateMdm();


				/**
				 * Set mdmType
				 */
				colName = "mdmType";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String mdmType = isColPresent ? xmlParser.getColVal(i, colName)
						: "0";
				configMDM.setMdmType(AhRestoreCommons.convertInt(mdmType));


				/**
				 * Set rootURLPath
				 */
				colName = "rootURLPath";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String rootURLPath = isColPresent ? xmlParser.getColVal(i, colName)
						: "";
				configMDM.setRootURLPath(AhRestoreCommons
						.convertString(rootURLPath));

				/**
				 * Set mdmUserName
				 */
				colName = "mdmUserName";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String mdmUserName = isColPresent ? xmlParser.getColVal(i, colName)
						: "";
				configMDM.setMdmUserName(AhRestoreCommons
						.convertString(mdmUserName));

				/**
				 * Set mdmPassword
				 */
				colName = "mdmPassword";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String mdmPassword = isColPresent ? xmlParser.getColVal(i, colName)
						: "";
				configMDM.setMdmPassword(AhRestoreCommons
						.convertString(mdmPassword));

				/**
				 * Set enableAppleOs
				 */
				int enablemdmOs=0;
				colName = "enableAppleOs";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String enableAppleOs = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				if(enableAppleOs.equals("true")||enableAppleOs.equals("t")){
					enablemdmOs+=1;
				}


				/**
				 * Set enableAppleOs
				 */
				colName = "enableMacOs";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String enableMacOs = isColPresent ? xmlParser.getColVal(i, colName)
						: "true";
				if(enableMacOs.equals("true")||enableMacOs.equals("t")){
					enablemdmOs+=2;
				}
				configMDM.setEnableMdmOs(enablemdmOs);


				/**
				 * Set id
				 */
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				String prefixid = isColPresent ? xmlParser.getColVal(i, colName)
						: "12345";

				Long newConfigMDM_id = AhRestoreNewMapTools
	                    .getMapConfigTemplateMDM(Long
	                            .parseLong(prefixid.trim()));
				configMDM.setId(newConfigMDM_id);

				/**
				 * Set ssidname
				 */
				colName = "ssidname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				if (!isColPresent) {
					BeLogTools
							.debug(HmLogConst.M_RESTORE,
									"Restore table 'ssid_profile' data be lost, cause: 'ssidname' column is not exist.");
					/**
					 * The ssidname column must be exist in the table of
					 * ssid_profile
					 */
					continue;
				}

				String name1 = xmlParser.getColVal(i, colName);
				if (name1 == null || name1.trim().equals("")
						|| name1.trim().equalsIgnoreCase("null")) {
					BeLogTools
							.debug(HmLogConst.M_RESTORE,
									"Restore table 'ssid_profile' data be lost, cause: 'ssidname' column value is null.");
					continue;
				}
				name = prefixid + name;
				if (name.length() >= 32) {
					name = name.substring(0, 31);
				}
				configMDM.setPolicyname(name.trim());

				/*
				 * set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"ssid_profile", colName);
				long ownerId1 = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;
				if (null == AhRestoreNewMapTools.getHmDomain(ownerId1)) {
					BeLogTools
							.debug(HmLogConst.M_RESTORE,
									"Restore table 'ssid_profile' data be lost, cause: 'owner' column is not available.");
					continue;
				}
				HmDomain ownerDomain1 = AhRestoreNewMapTools.getHmDomain(ownerId1);

				configMDM.setOwner(ownerDomain1);

				ssidProfileDTO.setConfigmdmId(configMDM);
			}


//				//first ,get the ssid's id
//
//				colName = "id";
//				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"ssid_profile", colName);
//				String oldid = isColPresent ? xmlParser.getColVal(i, colName) : "1";
//				//second,create a new mdm to get a new id
//
//				ConfigTemplateMdm tempMDM = new ConfigTemplateMdm();
//
//				Long newConfigMDM_id = tempMDM.getId();
//				//third,write the relation into map




            /**
             * Change cloudAuthEnabled to (enableCA4PPSK) enableIDM
             */
            colName = "enabledIDM";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
            if(!isColPresent) {
                colName = "cloudAuthEnabled";
                isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
            }
            String cloudAuthEnabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
            ssidProfileDTO.setEnabledIDM(AhRestoreCommons.convertStringToBoolean(cloudAuthEnabled));
            
            /**
             * enabledSocialLogin
             */
            colName = "enabledSocialLogin";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
            String enabledSocialLogin = isColPresent ? xmlParser.getColVal(i, colName) : "";
            ssidProfileDTO.setEnabledSocialLogin(AhRestoreCommons.convertStringToBoolean(enabledSocialLogin));

            /**
             * Set enabledVoiceEnterprise
 			 */
 			colName = "enabledVoiceEnterprise";
 			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
 				"ssid_profile", colName);
 			String enabledVoiceEnterprise = isColPresent ? xmlParser.getColVal(i, colName) : "false";
 			ssidProfileDTO.setEnabledVoiceEnterprise(AhRestoreCommons.convertStringToBoolean(enabledVoiceEnterprise));

 			// fix bug 26406
 			if(ssidProfileDTO.isEnabledVoiceEnterprise()){
 				ssidProfileDTO.setEnabledwmm(true);
 				ssidProfileDTO.setEnabledAcVoice(true);
 			}

            /**
            * Set enabled80211k
			 */
			colName = "enabled80211k";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabled80211k = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabled80211k(AhRestoreCommons.convertStringToBoolean(enabled80211k));

			/**
	         * Set enabled80211v
			 */
			colName = "enabled80211v";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabled80211v = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabled80211v(AhRestoreCommons.convertStringToBoolean(enabled80211v));

			/**
	         * Set enabled80211r
			 */
			colName = "enabled80211r";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabled80211r = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabled80211r(AhRestoreCommons.convertStringToBoolean(enabled80211r));

			/**
			 * Set enabledAcBesteffort
			 */
			colName = "enabledAcBesteffort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledAcBesteffort = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabledAcBesteffort(AhRestoreCommons.convertStringToBoolean(enabledAcBesteffort));

			/**
			 * Set enabledAcBackground
			 */
			colName = "enabledAcBackground";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledAcBackground = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabledAcBackground(AhRestoreCommons.convertStringToBoolean(enabledAcBackground));

			/**
			 * Set enabledAcVideo
			 */
			colName = "enabledAcVideo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledAcVideo = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabledAcVideo(AhRestoreCommons.convertStringToBoolean(enabledAcVideo));

			/**
			 * Set enabledAcVoice
			 */
			colName = "enabledAcVoice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enabledAcVoice = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnabledAcVoice(AhRestoreCommons.convertStringToBoolean(enabledAcVoice));

			colName = "enableProvisionEnterprise";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableProvisionEnterprise = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnableProvisionEnterprise(AhRestoreCommons.convertStringToBoolean(enableProvisionEnterprise));
			colName = "enableProvisionPersonal";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableProvisionPersonal = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnableProvisionPersonal(AhRestoreCommons.convertStringToBoolean(enableProvisionPersonal));
			colName = "enableProvisionPrivate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String enableProvisionPrivate = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnableProvisionPrivate(AhRestoreCommons.convertStringToBoolean(enableProvisionPrivate));
			colName = "wpaOpenSsid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String wpaOpenSsid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setWpaOpenSsid(AhRestoreCommons.convertString(wpaOpenSsid));
			/*
			 * This block is added to support Single SSID based PPSK onboarding
			 */
			colName = "enableSingleSsid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String enableSingleSsid = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ssidProfileDTO.setEnableSingleSsid(AhRestoreCommons.convertStringToBoolean(enableSingleSsid));
			
			colName = "singleSsidValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String singleSsidValue = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ssidProfileDTO.setSingleSsidValue(AhRestoreCommons.convertString(singleSsidValue));
			
			colName = "privateSsidModel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"ssid_profile", colName);	
			int privateSsidModel = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : SsidProfile.OPEN_SSID_MODLE;
			ssidProfileDTO.setPrivateSsidModel(privateSsidModel);
			// end of adding single ssid
			ssidProfileInfo.add(ssidProfileDTO);
		}

		return ssidProfileInfo;
	}

	public static boolean restoreSsidProfile()
	{
		try
		{
			Map<String, TextItem> usProfileIdNameMap = new HashMap<String,TextItem>();
			List<SsidProfile> allSsidProfile = getAllSsidProfile();
			Map<String, Set<MacFilter>> allMacFilter = getAllMacFilters("ssid_profile_mac_filter");
			Map<String, Set<Scheduler>> allScheduler = getAllSchedulers("ssid_profile_scheduler");
			Map<String, Set<UserProfile>> allRadiusUserProfile = getAllSsidProfileRadiusUserProfiles();
			Map<String, Set<LocalUserGroup>> allLocalUserGroup = getAllSsidProfileLocalUserGroup();
			Map<String, Set<LocalUserGroup>> allRadiusUserGroup = getAllSsidProfileRadiusUserGroup();

			// get all ssid 11a rate set
			Map<String, Map<String, TX11aOr11gRateSetting>> aRateSets = getAllSsidRateSettings("a_rate_setting_info");
			// get all ssid 11g rate set
			Map<String, Map<String, TX11aOr11gRateSetting>> gRateSets = getAllSsidRateSettings("g_rate_setting_info");

			Map<String, Map<String, TX11aOr11gRateSetting>> nRateSets = getAllSsidRateSettings("n_rate_setting_info");

			//get all ssid 11ac rate set
			Map<String,List<Tx11acRateSettings>> acRateSets = getAllSsidAcRateSettings("ac_rate_setting_info");
			if(null == allSsidProfile)
			{
				AhRestoreDBTools.logRestoreMsg("allSsidProfile is null");

				return false;
			}
			else
			{
				for (SsidProfile tempSsidProfile : allSsidProfile) {
					if (tempSsidProfile != null) {
						if (allMacFilter != null) {
							tempSsidProfile.setMacFilters(allMacFilter.get(tempSsidProfile.getId().toString()));
						}
						if (allScheduler != null) {
							tempSsidProfile.setSchedulers(allScheduler.get(tempSsidProfile.getId().toString()));
						}
						if (allLocalUserGroup != null) {
							if (tempSsidProfile.isEnablePpskSelfReg()) {
								Set<LocalUserGroup> groupSet = new HashSet<LocalUserGroup>();
								boolean needUpgradeLog =false;
								Long leaveGroupId=null;
								if (allLocalUserGroup.get(tempSsidProfile.getId().toString())!=null) {
									for(LocalUserGroup group :allLocalUserGroup.get(tempSsidProfile.getId().toString())) {
										if (groupSet.size()<1){
											groupSet.add(group);
											leaveGroupId = group.getId();
										} else {
											needUpgradeLog=true;
										}
									}
								}
								tempSsidProfile.setLocalUserGroups(groupSet);

								if (needUpgradeLog) {
									try {
										List<?> gpName = QueryUtil.executeQuery("select groupName from "
												+ LocalUserGroup.class.getSimpleName(), null, new FilterParams("id", leaveGroupId));
										String leaveName = gpName.isEmpty()?"N/A" : gpName.get(0).toString();

										HmUpgradeLog upgradeLog = new HmUpgradeLog();
										upgradeLog.setOwner(tempSsidProfile.getOwner());
										upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),tempSsidProfile.getOwner().getTimeZoneString()));
										upgradeLog.setAnnotation("Click to add an annotation");
										upgradeLog.setFormerContent("Multiple user groups were assigned to SSID " + tempSsidProfile.getSsidName() + " although the private PSK server can only support one user group per SSID for self-registration.");
										upgradeLog.setPostContent(NmsUtil.getOEMCustomer().getNmsName() + " retained user group " + leaveName + " for the SSID on the private PSK server.");
										upgradeLog.setRecommendAction("Confirm that the user group choice meets with your approval.");

										QueryUtil.createBo(upgradeLog);
									} catch (Exception e) {
										AhRestoreDBTools.logRestoreMsg("insert SSID profile upgrade log error");
										AhRestoreDBTools.logRestoreMsg(e.getMessage());
									}
								}
							} else {
								if (allLocalUserGroup.get(tempSsidProfile.getId().toString())!=null) {
									tempSsidProfile.setLocalUserGroups(allLocalUserGroup.get(tempSsidProfile.getId().toString()));
								}
							}
						}

						if (allRadiusUserGroup != null) {
							if (allRadiusUserGroup.get(tempSsidProfile.getId().toString())!=null) {
								tempSsidProfile.setRadiusUserGroups(allRadiusUserGroup.get(tempSsidProfile.getId().toString()));
							}
						}

						if (allRadiusUserProfile != null) {
							tempSsidProfile.setRadiusUserProfile(allRadiusUserProfile.get(tempSsidProfile.getId().toString()));
						}
						if (RestoreConfigNetwork.RESTORE_BEFORE_DARKA_FLAG) {
							if (tempSsidProfile.getAccessMode()==SsidProfile.ACCESS_MODE_PSK) {
								if (tempSsidProfile.getUserProfileDefault()==null) {
									UserProfile removedUs=null;
									if (tempSsidProfile.getRadiusUserProfile()!=null && !tempSsidProfile.getRadiusUserProfile().isEmpty()) {
										for(UserProfile us: tempSsidProfile.getRadiusUserProfile()) {
											tempSsidProfile.setUserProfileDefault(us);
											removedUs=us;
											break;
										}
										if (removedUs!=null) {
											tempSsidProfile.getRadiusUserProfile().remove(removedUs);
										}
										try {
											if (usProfileIdNameMap.isEmpty()) {
												List<?> gpName = QueryUtil.executeQuery("select id,userProfileName,attributeValue from "
														+ UserProfile.class.getSimpleName(), null, null);
												for(Object obj:gpName) {
													Object[] oneRec = (Object[]) obj;
													usProfileIdNameMap.put(oneRec[0].toString(), new TextItem(oneRec[1].toString(), oneRec[2].toString()));
												}
											}

											HmUpgradeLog upgradeLog = new HmUpgradeLog();
											upgradeLog.setOwner(tempSsidProfile.getOwner());
											upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),tempSsidProfile.getOwner().getTimeZoneString()));
											upgradeLog.setAnnotation("Click to add an annotation");
											if (tempSsidProfile.getRadiusUserProfile().size()>0) {
												upgradeLog.setFormerContent("Multiple user profiles were assigned to SSID " + tempSsidProfile.getSsidName() + ", but no user profiles were designated as default or authenticated.");
												StringBuilder sbf = new StringBuilder();
												for(UserProfile usone: tempSsidProfile.getRadiusUserProfile()){
													if (sbf.length()>0) {
														sbf.append(", ");
													}
													sbf.append(usProfileIdNameMap.get(usone.getId().toString()).getKey()).append(" (").append(usProfileIdNameMap.get(usone.getId().toString()).getValue()).append(")");
												}

												upgradeLog.setPostContent("The default user profile for the SSID is now " + usProfileIdNameMap.get(removedUs.getId().toString()).getKey() + " (attribute " + usProfileIdNameMap.get(removedUs.getId().toString()).getValue() + ")," +
														" and the authenticated user profile names and attributes are " + sbf.toString() + ".");
												upgradeLog.setRecommendAction("Confirm that the default and authenticated user profile designations meet with your approval.");
											} else {
												upgradeLog.setFormerContent("User profile " + usProfileIdNameMap.get(removedUs.getId().toString()).getKey() + " (attribute " + usProfileIdNameMap.get(removedUs.getId().toString()).getValue() + ")  was assigned to SSID " + tempSsidProfile.getSsidName() + ", but it was not designated as the default user profile.");
												upgradeLog.setPostContent("User profile " + usProfileIdNameMap.get(removedUs.getId().toString()).getKey() + " is now designated as the default user profile for the SSID.");
												upgradeLog.setRecommendAction("None.");
											}

											QueryUtil.createBo(upgradeLog);
										} catch (Exception e) {
											AhRestoreDBTools.logRestoreMsg("insert SSID profile upgrade log error");
											AhRestoreDBTools.logRestoreMsg(e.getMessage());
										}
									}
								}
							}
						}


						// set this ssid 11a rate setting
						if (null == aRateSets || aRateSets.get(tempSsidProfile.getId().toString())==null) {
							Map<String, TX11aOr11gRateSetting> aRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (ARateType aType : ARateType.values()) {
								TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
								if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
								} else {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								}
								rateSet.setARateType(aType);
								aRateSet.put(rateSet.getkey(), rateSet);
							}
							tempSsidProfile.setARateSets(aRateSet);
						} else {
							tempSsidProfile.setARateSets(aRateSets.get(tempSsidProfile.getId().toString()));
						}

						// set this ssid 11g rate setting
						if (null == gRateSets || gRateSets.get(tempSsidProfile.getId().toString())==null) {
							Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (GRateType gType : GRateType.values()) {
								TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
								if (GRateType.one.equals(gType) || GRateType.two.equals(gType) || GRateType.five.equals(gType)
										|| GRateType.eleven.equals(gType)) {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
								} else {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								}
								rateSet.setGRateType(gType);
								gRateSet.put(rateSet.getkey(), rateSet);
							}
							tempSsidProfile.setGRateSets(gRateSet);
						} else {
							tempSsidProfile.setGRateSets(gRateSets.get(tempSsidProfile.getId().toString()));
						}

						if (null == nRateSets || nRateSets.get(tempSsidProfile.getId().toString())==null) {
							Map<String, TX11aOr11gRateSetting> nRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (NRateType nType : NRateType.values()) {
								TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
								rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								rateSet.setNRateType(nType);
								nRateSet.put(rateSet.getkey(), rateSet);
							}
							tempSsidProfile.setNRateSets(nRateSet);
						} else {
							Map<String, TX11aOr11gRateSetting> nRateSetMap = nRateSets.get(tempSsidProfile.getId().toString());
							if (nRateSetMap.size()<20){
								for (NRateType nType : NRateType.values()) {
									TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
									rateSet.setNRateType(nType);
									if (nRateSetMap.get(rateSet.getkey())==null){
										nRateSetMap.put(rateSet.getkey(), rateSet);
									}
								}
							}
							tempSsidProfile.setNRateSets(nRateSetMap);
						}

						// set this ssid 11ac rate setting
						if(acRateSets == null || acRateSets.get(tempSsidProfile.getId().toString()) == null){
							List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
							for (short i = Tx11acRateSettings.STREAM_TYPE_SINGLE; i <= Tx11acRateSettings.STREAM_TYPE_THREE; i ++){
								Tx11acRateSettings acRateSet = new Tx11acRateSettings();
								acRateSet.setStreamType(i);
								acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
								acRateList.add(acRateSet);
							}
							tempSsidProfile.setAcRateSets(acRateList);
						}else{
							List<Tx11acRateSettings> acRateList = acRateSets.get(tempSsidProfile.getId().toString());
							tempSsidProfile.setAcRateSets(acRateList);
						}

						/**-------**/
						ConfigTemplateMdm mdm=tempSsidProfile.getConfigmdmId();
						if(needRestoreConfigTemplateMDM && mdm!=null){
							QueryUtil.createBo(mdm);
						}

					}
				}
				List<Long> lOldId = new ArrayList<Long>();

				for (SsidProfile ssidProfile : allSsidProfile) {
					lOldId.add(ssidProfile.getId());
				}

				QueryUtil.restoreBulkCreateBos(allSsidProfile);

				for(int i=0; i < allSsidProfile.size(); ++i)
				{
					AhRestoreNewMapTools.setMapSsid(lOldId.get(i), allSsidProfile.get(i).getId());
				}
			}

		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

    private static Map<String, Set<PortGroupProfile>> getAllConfigTemplatePorts()
            throws AhRestoreColNotExistException, AhRestoreException {
        AhRestoreGetXML xmlParser = new AhRestoreGetXML();

        /**
         * Check validation of config_template_port.xml
         */
        final String tableName = "config_template_port";
        boolean restoreRet = xmlParser.readXMLFile(tableName);
        if (!restoreRet) {
            return null;
        }

        Map<String, Set<PortGroupProfile>> portTemplateMap = new HashMap<String, Set<PortGroupProfile>>();
        int rowCount = xmlParser.getRowCount();
        boolean isColPresent;
        String colName;

        for (int i = 0; i < rowCount; i++) {
            /**
             * Set config_template_id
             */
            colName = "config_template_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                /**
                 * The config_template_id column must be exist in the table of
                 * config_template_lan
                 */
                continue;
            }

            String profileId = xmlParser.getColVal(i, colName);
            if (profileId == null || profileId.trim().equals("")
                    || profileId.trim().equalsIgnoreCase("null")) {
                continue;
            }

            /**
             * Set portprofiles_id
             */
            colName = "portprofiles_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    tableName, colName);
            if (!isColPresent) {
                continue;
            }

            String lanId = xmlParser.getColVal(i, colName);
            if (lanId == null || lanId.trim().equals("")
                    || lanId.trim().equalsIgnoreCase("null")) {
                continue;
            }

            Long newLanId = AhRestoreNewMapTools.getMapWiredPortTemplateId(Long
                    .parseLong(lanId.trim()));
            PortGroupProfile portTemp = AhRestoreNewTools.CreateBoWithId(
                    PortGroupProfile.class, newLanId);

            if (portTemp != null) {
                if (portTemplateMap.get(profileId) == null) {
                    Set<PortGroupProfile> lanSet = new HashSet<PortGroupProfile>();
                    lanSet.add(portTemp);
                    portTemplateMap.put(profileId, lanSet);
                } else {
                    portTemplateMap.get(profileId).add(portTemp);
                }
            }
        }

        return portTemplateMap;
    }

	private static Map<String, Map<Long, ConfigTemplateSsid>> getAllConfigTemplateSsid() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		Map<String,SsidProfile> createSsidBefore = new HashMap<String,SsidProfile>();
		/**
		 * Check validation of config_template_ssid.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("config_template_ssid");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Map<Long, ConfigTemplateSsid>> configTemplateSsidInfo = new HashMap<String, Map<Long, ConfigTemplateSsid>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			ConfigTemplateSsid configTemplateSsidDTO = new ConfigTemplateSsid();

			/**
			 * Set config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid", colName);
			if (!isColPresent)
			{
				/**
				 * The config_template_id column must be exist in the table of config_template_ssid
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

//			if (AhRestoreNewMapTools.getMapConfigTemplate(Long.parseLong(profileId.trim()))==null) {
//				continue;
//			}

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid", colName);
			if (!isColPresent)
			{
				/**
				 * The mapkey column must be exist in the table of config_template_ssid
				 */
				continue;
			}

			String mapkey = xmlParser.getColVal(i, colName);
			if (mapkey == null || mapkey.trim().equals("")
				|| mapkey.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set interfacename
			 */
			colName = "interfacename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid", colName);
			String interfacename = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			if ("ETH0".equals(interfacename)) {
				interfacename = interfacename.toLowerCase();
			}
			configTemplateSsidDTO.setInterfaceName(AhRestoreCommons.convertString(interfacename));

			/**
			 * Set qos_classfier_and_marker
			 */
			colName = "qos_classfier_and_marker";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid", colName);
			if (isColPresent){
				String qos_classfier_and_marker = xmlParser.getColVal(i, colName);
				if (!qos_classfier_and_marker.equals("") && !qos_classfier_and_marker.trim().equalsIgnoreCase("null")) {
					Long newQosClassfierAndMarkerId = AhRestoreNewMapTools.getMapQosClassificationAndMark(Long.parseLong(qos_classfier_and_marker.trim()));
					QosClassfierAndMarker classfierAndMarker=null;
					if (newQosClassfierAndMarkerId!=null) {
						classfierAndMarker= QueryUtil.findBoById(QosClassfierAndMarker.class, newQosClassfierAndMarkerId);
					}
					if (classfierAndMarker!=null) {
						configTemplateSsidDTO.setNetworkServicesEnabled(classfierAndMarker.getNetworkServicesEnabled());
						configTemplateSsidDTO.setMacOuisEnabled(classfierAndMarker.getMacOuisEnabled());
						configTemplateSsidDTO.setSsidEnabled(classfierAndMarker.getSsidEnabled());
						configTemplateSsidDTO.setCheckE(classfierAndMarker.getCheckE());
						configTemplateSsidDTO.setCheckP(classfierAndMarker.getCheckP());
						configTemplateSsidDTO.setCheckD(classfierAndMarker.getCheckD());
						configTemplateSsidDTO.setCheckET(classfierAndMarker.getCheckET());
						configTemplateSsidDTO.setCheckPT(classfierAndMarker.getCheckPT());
						configTemplateSsidDTO.setCheckDT(classfierAndMarker.getCheckDT());
					}
				}
			} else {
				/**
				 * Set ssidonlyenabled
				 */
				colName = "ssidonlyenabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String ssidonlyenabled = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setSsidOnlyEnabled(AhRestoreCommons.convertStringToBoolean(ssidonlyenabled));

				/**
				 * Set networkservicesenabled
				 */
				colName = "networkservicesenabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String networkservicesenabled = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setNetworkServicesEnabled(AhRestoreCommons.convertStringToBoolean(networkservicesenabled));
				/**
				 * Set macouisenabled
				 */
				colName = "macouisenabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String macouisenabled = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setMacOuisEnabled(AhRestoreCommons.convertStringToBoolean(macouisenabled));
				/**
				 * Set ssidenabled
				 */
				colName = "ssidenabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String ssidenabled = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setSsidEnabled(AhRestoreCommons.convertStringToBoolean(ssidenabled));
				/**
				 * Set checkd
				 */
				colName = "checkd";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checkd = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckD(AhRestoreCommons.convertStringToBoolean(checkd));
				/**
				 * Set checke
				 */
				colName = "checke";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checke = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckE(AhRestoreCommons.convertStringToBoolean(checke));
				/**
				 * Set checkp
				 */
				colName = "checkp";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checkp = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckP(AhRestoreCommons.convertStringToBoolean(checkp));
				/**
				 * Set checkdt
				 */
				colName = "checkdt";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checkdt = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckDT(AhRestoreCommons.convertStringToBoolean(checkdt));
				/**
				 * Set checket
				 */
				colName = "checket";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checket = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckET(AhRestoreCommons.convertStringToBoolean(checket));
				/**
				 * Set checkpt
				 */
				colName = "checkpt";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String checkpt = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				configTemplateSsidDTO.setCheckPT(AhRestoreCommons.convertStringToBoolean(checkpt));
			}

			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid", colName);
			String ssid_profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (ssid_profile_id != null && !ssid_profile_id.trim().equals("")  && !ssid_profile_id.trim().equalsIgnoreCase("null")) {
				Long newSsidId = AhRestoreNewMapTools.getMapSsid(Long.parseLong(ssid_profile_id.trim()));
				if (newSsidId==null) {
					continue;
				}
				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, newSsidId);
				if (ssidProfile==null) {
					continue;
				}
				configTemplateSsidDTO.setSsidProfile(ssidProfile);
			}

			if (ssidWlanIDMapping.get(ssid_profile_id)== null){
				ssidWlanIDMapping.put(ssid_profile_id, profileId);
			}
			/**
			 * Set radiomode
			 */
			colName = "radiomode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
			if (isColPresent && !mapkey.equals("-1")
					&& !mapkey.equals("-2") && !mapkey.equals("-3") && !mapkey.equals("-4")
					&& !mapkey.equals("-5") && !mapkey.equals("-6") && !mapkey.equals("-7")){
				bln31to32flg = true;
				SsidProfile tmpSsidProfile = new SsidProfile();
				String radiomode = isColPresent ? xmlParser.getColVal(i, colName) : "1";
				tmpSsidProfile.setRadioMode(AhRestoreCommons.convertInt(radiomode));

				/**
				 * Set radius_service_assign_id
				 */
				colName = "radius_service_assign_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String radius_service_assign_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!radius_service_assign_id.equals("") && !radius_service_assign_id.trim().equalsIgnoreCase("null")) {
					Long newRadiusServiceAssignId = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.parseLong(radius_service_assign_id.trim()));
					RadiusAssignment radiusAssignment = AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class,newRadiusServiceAssignId);
					tmpSsidProfile.setRadiusAssignment(radiusAssignment);
				}

				/**
				 * Set radius_user_profile_rule_id
				 */
				colName = "radius_user_profile_rule_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				if (isColPresent){
					String radius_user_profile_rule_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
					if (!radius_user_profile_rule_id.equals("") && !radius_user_profile_rule_id.trim().equalsIgnoreCase("null")) {
						Long newRadiusUserProfileRuleId = AhRestoreNewMapTools.getMapRadiusUserProfileRule(Long.parseLong(radius_user_profile_rule_id.trim()));
						RadiusUserProfileRule rule=null;
						if (newRadiusUserProfileRuleId!=null) {
							rule= QueryUtil.findBoById(RadiusUserProfileRule.class, newRadiusUserProfileRuleId);
						}
						if (rule != null) {
							tmpSsidProfile.setActionTime(rule.getActionTime());
							tmpSsidProfile.setDenyAction(rule.getDenyAction());
							tmpSsidProfile.setChkDeauthenticate(rule.getStrict());
							tmpSsidProfile.setChkUserOnly(!rule.getAllUserProfilesPermitted());
						}
					}
				}

				/**
				 * Set service_filter_id
				 */
				colName = "service_filter_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template_ssid", colName);
				String service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!service_filter_id.equals("") && !service_filter_id.trim().equalsIgnoreCase("null")) {
					Long newServiceFilterId = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(service_filter_id.trim()));
					ServiceFilter serviceFilter = AhRestoreNewTools.CreateBoWithId(ServiceFilter.class,newServiceFilterId);
					if (serviceFilter == null) {
						serviceFilter = QueryUtil.findBoByAttribute(ServiceFilter.class, "defaultFlag", true);
					}
					tmpSsidProfile.setServiceFilter(serviceFilter);
				}


				boolean bool = false;
				for (String singleName : BeParaModule.SSID_PROFILE_NAMES) {
					if (singleName.equals(interfacename.trim())) {
					    bool = true;
					    break;
				    }
				}
				if (updateSsid.get(interfacename)!=null || bool) {
					SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsidDTO.getSsidProfile().getId(),new ImplQueryBo());
					boolean shouldCreateNew = true;
					Map<String,String> issueMap = new HashMap<String,String>();
					if (checkExistSsidProfile(tmpSsidProfile,ssidProfile,issueMap)) {
						shouldCreateNew = false;
					} else {
						for(int cnt=1;cnt<nameCount;cnt++) {
							String ssidname_sub;
							if (ssidProfile.getSsid().length()>29) {
								ssidname_sub = ssidProfile.getSsid().substring(0, 29)+ "_" + cnt;
							} else {
								ssidname_sub = ssidProfile.getSsid()+ "_" + cnt;
							}
							if (createSsidBefore.get(ssidname_sub)!=null) {
								SsidProfile ssidProfileWillbeSet = createSsidBefore.get(ssidname_sub);
								if (checkExistSsidProfile(tmpSsidProfile,ssidProfileWillbeSet,null)) {
									shouldCreateNew = false;
									configTemplateSsidDTO.setSsidProfile(ssidProfileWillbeSet);
									configTemplateSsidDTO.setInterfaceName(configTemplateSsidDTO.getSsidProfile().getSsidName());
									AhRestoreNewMapTools.setMapSsidTemplateIdSsidProfile(profileId + "|" + ssid_profile_id, ssidProfileWillbeSet);
									break;
								}
							}
						}
					}
					if (shouldCreateNew) {
						ssidProfile.setId(null);
						ssidProfile.setVersion(null);
						ssidProfile.setOwner(AhRestoreNewMapTools.getonlyDomain());
						ssidProfile.setDefaultFlag(false);
						ssidProfile.setServiceFilter(tmpSsidProfile.getServiceFilter());
						ssidProfile.setRadioMode(tmpSsidProfile.getRadioMode());
						ssidProfile.setRadiusAssignment(tmpSsidProfile.getRadiusAssignment());
						ssidProfile.setActionTime(tmpSsidProfile.getActionTime());
						ssidProfile.setDenyAction(tmpSsidProfile.getDenyAction());
						ssidProfile.setChkDeauthenticate(tmpSsidProfile.getChkDeauthenticate());
						ssidProfile.setChkUserOnly(tmpSsidProfile.getChkUserOnly());
						boolean nameExist = true;
						do {
							if (ssidProfile.getSsid().length()>29) {
								ssidProfile.setSsidName(ssidProfile.getSsid().substring(0, 29)+ "_" + nameCount);
							} else {
								ssidProfile.setSsidName(ssidProfile.getSsid()+ "_" + nameCount);
							}
							nameCount++;
							if(!checkHiveNameExists("hiveName",ssidProfile.getSsidName())&&
									!checkSsidNameExists("ssidName",ssidProfile.getSsidName())){
								nameExist = false;
							}
						} while(nameExist);

						issueMap.put("SAMESSIDNAME", ssidProfile.getSsid());
						issueMap.put("WLANPOLICYID", ssidWlanIDMapping.get(ssid_profile_id));
						issueMap.put("WLANPOLICYID2", profileId);
						issueMap.put("NEWSSIDNAME", ssidProfile.getSsidName());
						issueMapList.add(issueMap);

						Set<Scheduler> cloneSchedulers = new HashSet<Scheduler>();
						for (Scheduler tempClass : ssidProfile.getSchedulers()) {
							cloneSchedulers.add(tempClass);
						}
						ssidProfile.setSchedulers(cloneSchedulers);

						Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
						for (MacFilter tempClass : ssidProfile.getMacFilters()) {
							cloneMacFilters.add(tempClass);
						}
						ssidProfile.setMacFilters(cloneMacFilters);

						Set<UserProfile> cloneRadiusUserProfiles = new HashSet<UserProfile>();
						for (UserProfile tempClass : ssidProfile.getRadiusUserProfile()) {
							cloneRadiusUserProfiles.add(tempClass);
						}
						ssidProfile.setRadiusUserProfile(cloneRadiusUserProfiles);

						Set<LocalUserGroup> cloneLocalUserGroups = new HashSet<LocalUserGroup>();
						for (LocalUserGroup tempClass : ssidProfile.getLocalUserGroups()) {
							cloneLocalUserGroups.add(tempClass);
						}
						ssidProfile.setLocalUserGroups(cloneLocalUserGroups);

						Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
						for (GRateType gType : TX11aOr11gRateSetting.GRateType.values()) {
							TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(gType);
							if (null == rateSet) {
								rateSet = new TX11aOr11gRateSetting();
								if (GRateType.one.equals(gType) || GRateType.two.equals(gType) || GRateType.five.equals(gType)
										|| GRateType.eleven.equals(gType)) {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
								} else {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								}
							}
							rateSet.setGRateType(gType);
							gRateSet.put(rateSet.getkey(), rateSet);
						}
						ssidProfile.setGRateSets(gRateSet);

						Map<String, TX11aOr11gRateSetting> aRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
						for (ARateType aType : TX11aOr11gRateSetting.ARateType.values()) {
							TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(aType);
							if (null == rateSet) {
								rateSet = new TX11aOr11gRateSetting();
								if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
								} else {
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								}
							}
							rateSet.setARateType(aType);
							aRateSets.put(rateSet.getkey(), rateSet);
						}
						ssidProfile.setARateSets(aRateSets);

						Map<String, TX11aOr11gRateSetting> nRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
						for (NRateType nType : TX11aOr11gRateSetting.NRateType.values()) {
							TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(nType);
							if (null == rateSet) {
								rateSet = new TX11aOr11gRateSetting();
								rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
							}
							rateSet.setNRateType(nType);
							nRateSets.put(rateSet.getkey(), rateSet);
						}
						ssidProfile.setNRateSets(nRateSets);

						// set this ssid 11ac rate setting
						Map<String,List<Tx11acRateSettings>> acRateSets = getAllSsidAcRateSettings("ac_rate_settings_info");
						if(acRateSets == null || acRateSets.get(configTemplateSsidDTO.getSsidProfile().getId()) == null){
							List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
							for (short z = Tx11acRateSettings.STREAM_TYPE_SINGLE; z <= Tx11acRateSettings.STREAM_TYPE_THREE; z ++){
								Tx11acRateSettings acRateSet = new Tx11acRateSettings();
								acRateSet.setStreamType(z);
								acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
								acRateList.add(acRateSet);
							}
							ssidProfile.setAcRateSets(acRateList);
						}else{
							List<Tx11acRateSettings> acRateList = acRateSets.get(configTemplateSsidDTO.getSsidProfile().getId());
							ssidProfile.setAcRateSets(acRateList);
						}

						try {
							Long newId = QueryUtil.createBo(ssidProfile);
							ssidProfile = QueryUtil.findBoById(SsidProfile.class, newId);
							configTemplateSsidDTO.setSsidProfile(ssidProfile);
							configTemplateSsidDTO.setInterfaceName(configTemplateSsidDTO.getSsidProfile().getSsidName());
							AhRestoreNewMapTools.setMapSsidTemplateIdSsidProfile(profileId + "|" + ssid_profile_id, ssidProfile);
							createSsidBefore.put(ssidProfile.getSsidName(), ssidProfile);
						} catch (Exception e) {
							AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate create ssid profile error! Ssidname: " + ssidProfile.getSsidName() );
							e.printStackTrace();
						}
					}
				} else {
					SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsidDTO.getSsidProfile().getId());
					ssidProfile.setServiceFilter(tmpSsidProfile.getServiceFilter());
					ssidProfile.setRadioMode(tmpSsidProfile.getRadioMode());
					ssidProfile.setRadiusAssignment(tmpSsidProfile.getRadiusAssignment());
					ssidProfile.setActionTime(tmpSsidProfile.getActionTime());
					ssidProfile.setDenyAction(tmpSsidProfile.getDenyAction());
					ssidProfile.setChkDeauthenticate(tmpSsidProfile.getChkDeauthenticate());
					ssidProfile.setChkUserOnly(tmpSsidProfile.getChkUserOnly());
					try {
						QueryUtil.updateBo(ssidProfile);
						updateSsid.put(interfacename, true);
						ssidProfile = QueryUtil.findBoById(SsidProfile.class, ssidProfile.getId());
						configTemplateSsidDTO.setSsidProfile(ssidProfile);
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate update ssid profile error! Ssidname: " + ssidProfile.getSsidName() );
						e.printStackTrace();
					}
				}
			}

			if (!mapkey.equals("-1") && !mapkey.equals("-2") && !mapkey.equals("-3") && !mapkey.equals("-4")
					&& !mapkey.equals("-5") && !mapkey.equals("-6") && !mapkey.equals("-7")) {
				if (configTemplateSsidDTO.getSsidProfile()==null) {
					System.out.println(profileId + "====" + i);
					continue;
				}
			}

			if (configTemplateSsidInfo.get(profileId) != null ) {
				Map<Long, ConfigTemplateSsid> tempMap = configTemplateSsidInfo.get(profileId);
				if (!mapkey.equals("-1") && !mapkey.equals("-2") && !mapkey.equals("-3") && !mapkey.equals("-4")
						&& !mapkey.equals("-5") && !mapkey.equals("-6") && !mapkey.equals("-7")) {
					tempMap.put(configTemplateSsidDTO.getSsidProfile().getId(), configTemplateSsidDTO);
				} else {
					tempMap.put(Long.valueOf(mapkey), configTemplateSsidDTO);
				}
			} else {
				Map<Long, ConfigTemplateSsid> oneSsid = new HashMap<Long, ConfigTemplateSsid>();
				if (!mapkey.equals("-1") && !mapkey.equals("-2") && !mapkey.equals("-3") && !mapkey.equals("-4")
						&& !mapkey.equals("-5") && !mapkey.equals("-6") && !mapkey.equals("-7")) {
					oneSsid.put(configTemplateSsidDTO.getSsidProfile().getId(), configTemplateSsidDTO);
				} else {
					oneSsid.put(Long.valueOf(mapkey), configTemplateSsidDTO);
				}
				configTemplateSsidInfo.put(profileId, oneSsid);
			}
		}
		return configTemplateSsidInfo;
	}

//	private static Map<String, Map<String, ConfigTemplateQos>> getAllConfigTemplateQos() throws AhRestoreColNotExistException,AhRestoreException
//	{
//		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
//
//		/**
//		 * Check validation of config_template_qos.xml
//		 */
//		boolean restoreRet = xmlParser.readXMLFile("config_template_qos");
//		if (!restoreRet)
//		{
//			return null;
//		}
//
//		int rowCount = xmlParser.getRowCount();
//		Map<String, Map<String, ConfigTemplateQos>> configTemplateQosInfo = new HashMap<String, Map<String, ConfigTemplateQos>>();
//		boolean isColPresent;
//		String colName;
//
//		for (int i = 0; i < rowCount; i++)
//		{
//			ConfigTemplateQos configTemplateQosDTO = new ConfigTemplateQos();
//			/**
//			 * Set config_template_id
//			 */
//			colName = "config_template_id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			if (!isColPresent)
//			{
//				/**
//				 * The config_template_id column must be exist in the table of config_template_qos
//				 */
//				continue;
//			}
//
//			String profileId = xmlParser.getColVal(i, colName);
//			if (profileId == null || profileId.trim().equals("")
//				|| profileId.trim().equalsIgnoreCase("null"))
//			{
//				continue;
//			}
//
//			if (AhRestoreMapTool.getMapConfigTemplate(profileId.trim())==null) {
//				continue;
//			}
//
//			/**
//			 * Set mapkey
//			 */
//			colName = "mapkey";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			if (!isColPresent)
//			{
//				/**
//				 * The mapkey column must be exist in the table of config_template_qos
//				 */
//				continue;
//			}
//
//			String mapkey = xmlParser.getColVal(i, colName);
//			if (mapkey == null || mapkey.trim().equals("")
//				|| mapkey.trim().equalsIgnoreCase("null"))
//			{
//				continue;
//			}
//
//			/**
//			 * Set policingrate
//			 */
//			colName = "policingrate";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			int policingrate = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 54000;
//			configTemplateQosDTO.setPolicingRate(policingrate > 54000 ? 54000 : policingrate);
//
//			/**
//			 * Set policingrate11n
//			 */
//			colName = "policingrate11n";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			String policingrate11n = isColPresent ? xmlParser.getColVal(i, colName) : "1000000";
//			configTemplateQosDTO.setPolicingRate11n(AhRestoreCommons.convertInt(policingrate11n));
//
//
//			/**
//			 * Set radiomode
//			 */
//			colName = "radiomode";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			String radiomode = isColPresent ? xmlParser.getColVal(i, colName) : "1";
//			configTemplateQosDTO.setRadioMode(AhRestoreCommons.convertInt(radiomode));
//
//			/**
//			 * Set schedulingweight
//			 */
//			colName = "schedulingweight";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			String schedulingweight = isColPresent ? xmlParser.getColVal(i, colName) : "10";
//			configTemplateQosDTO.setSchedulingWeight(AhRestoreCommons.convertInt(schedulingweight));
//
//			/**
//			 * Set weightpercent
//			 */
//			colName = "weightpercent";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			String weightpercent = isColPresent ? xmlParser.getColVal(i, colName) : "0";
//			configTemplateQosDTO.setWeightPercent(AhRestoreCommons.convertFloat(weightpercent));
//
//			/**
//			 * Set user_profile_id
//			 */
//			colName = "user_profile_id";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template_qos", colName);
//			String user_profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!user_profile_id.equals("") && !user_profile_id.trim().equalsIgnoreCase("null")) {
//				String userProfileName = AhRestoreMapTool.getMapUserProfile(user_profile_id.trim());
//				UserProfile userProfile = QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName", userProfileName,AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateQosDTO.setUserProfile(userProfile);
//			}
//
//			if (configTemplateQosInfo.get(profileId) != null ) {
//				Map<String, ConfigTemplateQos> tempMap = configTemplateQosInfo.get(profileId);
//				String tempKey = configTemplateQosDTO.getUserProfile().getId().toString() + "|" + String.valueOf(configTemplateQosDTO.getRadioMode());
//				tempMap.put(tempKey, configTemplateQosDTO);
//			} else {
//				Map<String, ConfigTemplateQos> oneQos = new HashMap<String, ConfigTemplateQos>();
//				String tempKey = configTemplateQosDTO.getUserProfile().getId().toString() + "|" + String.valueOf(configTemplateQosDTO.getRadioMode());
//				oneQos.put(tempKey, configTemplateQosDTO);
//				configTemplateQosInfo.put(profileId, oneQos);
//			}
//		}
//
//		return configTemplateQosInfo;
//	}

	private static Map<String, Map<String, ConfigTemplateSsidUserProfile>> getAllTemplateSsidUserProfile() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of config_template_ssid_user_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("config_template_ssid_user_profile");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Map<String, ConfigTemplateSsidUserProfile>> templateSsidUserProfileInfo = new HashMap<String, Map<String, ConfigTemplateSsidUserProfile>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			ConfigTemplateSsidUserProfile templateSsidUserProfileDTO = new ConfigTemplateSsidUserProfile();
			/**
			 * Set config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid_user_profile", colName);
			if (!isColPresent)
			{
				/**
				 * The config_template_id column must be exist in the table of config_template_ssid_user_profile
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

//			if (AhRestoreNewMapTools.getMapConfigTemplate(Long.parseLong(profileId.trim()))==null) {
//				continue;
//			}

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid_user_profile", colName);
			if (!isColPresent)
			{
				/**
				 * The mapkey column must be exist in the table of config_template_ssid_user_profile
				 */
				continue;
			}

			String mapkey = xmlParser.getColVal(i, colName);
			if (mapkey == null || mapkey.trim().equals("")
				|| mapkey.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set user_profile_id
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid_user_profile", colName);
			String user_profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!user_profile_id.equals("") && !user_profile_id.trim().equalsIgnoreCase("null")) {
				Long newUserProfileId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(user_profile_id.trim()));
				UserProfile userProfile=null;
				if (newUserProfileId!=null) {
					userProfile= QueryUtil.findBoById(UserProfile.class, newUserProfileId);
				}
				templateSsidUserProfileDTO.setUserProfile(userProfile);
			}
			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid_user_profile", colName);
			String ssid_profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!ssid_profile_id.equals("") && !ssid_profile_id.trim().equalsIgnoreCase("null")) {
				if (AhRestoreNewMapTools.getMapSsidTemplateIdSsidProfile(profileId + "|" + ssid_profile_id)!=null) {
					templateSsidUserProfileDTO.setSsidProfile(AhRestoreNewMapTools.getMapSsidTemplateIdSsidProfile(profileId + "|" + ssid_profile_id));
				} else {
					Long newSsidProfileId = AhRestoreNewMapTools.getMapSsid(Long.parseLong(ssid_profile_id.trim()));
					SsidProfile ssidProfile=null;
					if (newSsidProfileId!=null) {
						ssidProfile = QueryUtil.findBoById(SsidProfile.class, newSsidProfileId);
					}
					templateSsidUserProfileDTO.setSsidProfile(ssidProfile);
				}
			}

			/**
			 * Set uptype
			 */
			colName = "uptype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ssid_user_profile", colName);
			String uptype = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			templateSsidUserProfileDTO.setUpType(AhRestoreCommons.convertInt(uptype));


			if (templateSsidUserProfileInfo.get(profileId) != null ) {
				Map<String, ConfigTemplateSsidUserProfile> tempMap = templateSsidUserProfileInfo.get(profileId);
				String tempKey = templateSsidUserProfileDTO.getKey();
				tempMap.put(tempKey, templateSsidUserProfileDTO);
			} else {
				Map<String, ConfigTemplateSsidUserProfile> oneSsidUserProfile = new HashMap<String, ConfigTemplateSsidUserProfile>();
				String tempKey = templateSsidUserProfileDTO.getKey();
				oneSsidUserProfile.put(tempKey, templateSsidUserProfileDTO);
				templateSsidUserProfileInfo.put(profileId, oneSsidUserProfile);
			}
		}

		return templateSsidUserProfileInfo;
	}

	private static Map<String, Set<MgmtServiceIPTrack>> getAllConfigTemplateIPTrack() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of config_template_ip_track.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("config_template_ip_track");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MgmtServiceIPTrack>> mapIpTrackInfo = new HashMap<String, Set<MgmtServiceIPTrack>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ip_track", colName);
			if (!isColPresent)
			{
				/**
				 * The config_template_id column must be exist in the table of config_template_ip_track
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set iptracks_id
			 */
			colName = "iptracks_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_ip_track", colName);
			if (!isColPresent)
			{
				/**
				 * The iptracks_id column must be exist in the table of config_template_ip_track
				 */
				continue;
			}

			String iptracks_id = xmlParser.getColVal(i, colName);
			if (iptracks_id == null || iptracks_id.trim().equals("")
				|| iptracks_id.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newIptracks = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(iptracks_id.trim()));
			MgmtServiceIPTrack ipTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,newIptracks);
			if (ipTrack != null) {
				if (mapIpTrackInfo.get(profileId) == null) {
					Set<MgmtServiceIPTrack> ipTrackSet= new HashSet<MgmtServiceIPTrack>();
					ipTrackSet.add(ipTrack);
					mapIpTrackInfo.put(profileId, ipTrackSet);
				} else {
					mapIpTrackInfo.get(profileId).add(ipTrack);
				}
			}
		}

		return mapIpTrackInfo;
	}

	private static Map<String, List<ConfigTemplateVlanNetwork>> getAllConfigTemplateVlanNetworkMaping() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "CONFIG_TEMPLATE_VLANNETWORK";
		if (!AhRestoreGetXML.checkXMLFileExist(tableName)){
			beforeEdinburgh=true;
			return null;
		}

		/**
		 * Check validation of CONFIG_TEMPLATE_VLANOVERRIDE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<ConfigTemplateVlanNetwork>> mapInfo = new HashMap<String, List<ConfigTemplateVlanNetwork>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent)
			{
				/**
				 * The config_template_id column must be exist in the table
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			List<ConfigTemplateVlanNetwork> listInfo= new ArrayList<ConfigTemplateVlanNetwork>();
			ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
			if (mapInfo.get(profileId) != null) {
				listInfo = mapInfo.get(profileId);
			} else {
				mapInfo.put(profileId, listInfo);
			}

			/**
			 * Set vlan_id
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (!isColPresent)
			{
				/**
				 * The vlan_id column must be exist in the table
				 */
				continue;
			}

			String vlan_id = xmlParser.getColVal(i, colName);
			if (vlan_id == null || vlan_id.trim().equals("")
				|| vlan_id.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long new_vlan_id = AhRestoreNewMapTools.getMapVlan(Long.parseLong(vlan_id.trim()));
			Vlan vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,new_vlan_id);
			if (vlanObj == null) {
				continue;
			} else {
				cvn.setVlan(vlanObj);
			}

			/**
			 * Set vpn_network_id
			 */
			colName = "vpn_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String vpn_network_id=null;
			if (isColPresent) {
				vpn_network_id = xmlParser.getColVal(i, colName);
			}

			if (vpn_network_id != null && !vpn_network_id.trim().equals("")
				&& !vpn_network_id.trim().equalsIgnoreCase("null")) {
				Long new_vpn_network_id = AhRestoreNewMapTools.getMapVpnNetwork(Long.parseLong(vpn_network_id.trim()));
				VpnNetwork networkObj = AhRestoreNewTools.CreateBoWithId(VpnNetwork.class,new_vpn_network_id);
				if (networkObj != null) {
					cvn.setNetworkObj(networkObj);
				}
			}

			/**
			 * Set blnUserAdd
			 */
			colName = "blnUserAdd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String blnUserAdd = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cvn.setBlnUserAdd(AhRestoreCommons.convertStringToBoolean(blnUserAdd));

			/**
			 * Set blnRemoved
			 */
			colName = "blnRemoved";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String blnRemoved = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			cvn.setBlnRemoved(AhRestoreCommons.convertStringToBoolean(blnRemoved));

			listInfo.add(cvn);
		}

		return mapInfo;
	}


	private static Map<String, Set<NetworkService>> getAllConfigTemplateNetworkService() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of config_template_tv_service.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("config_template_tv_service");
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<NetworkService>> mapNetworkServiceInfo = new HashMap<String, Set<NetworkService>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_tv_service", colName);
			if (!isColPresent)
			{
				/**
				 * The config_template_id column must be exist in the table of config_template_tv_service
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set tvnetworkservice_id
			 */
			colName = "tvnetworkservice_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template_tv_service", colName);
			if (!isColPresent)
			{
				/**
				 * The tvnetworkservice_id column must be exist in the table of config_template_tv_service
				 */
				continue;
			}

			String tvnetworkservice_id = xmlParser.getColVal(i, colName);
			if (tvnetworkservice_id == null || tvnetworkservice_id.trim().equals("")
				|| tvnetworkservice_id.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newNetworkServices = AhRestoreNewMapTools.getMapNetworkService(Long.parseLong(tvnetworkservice_id.trim()));
			NetworkService networkService = AhRestoreNewTools.CreateBoWithId(NetworkService.class,newNetworkServices);
			if (networkService != null) {
				if (mapNetworkServiceInfo.get(profileId) == null) {
					Set<NetworkService> networkServiceSet= new HashSet<NetworkService>();
					networkServiceSet.add(networkService);
					mapNetworkServiceInfo.put(profileId, networkServiceSet);
				} else {
					mapNetworkServiceInfo.get(profileId).add(networkService);
				}
			}
		}

		return mapNetworkServiceInfo;
	}


	private static List<ConfigTemplate> getAllTemplates(Map<String, SwitchSettings> allSwitchSettings) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of config_template.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("config_template");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in config_template table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ConfigTemplate> templatesInfo = new ArrayList<ConfigTemplate>();
		boolean isColPresent;
		String colName;
		ConfigTemplate configTemplateDTO;
		ServiceFilter serviceFilter = QueryUtil.findBoByAttribute(ServiceFilter.class, "defaultFlag", true);

		for (int i = 0; i < rowCount; i++)
		{
			configTemplateDTO = new ConfigTemplate();

			/**
			 * Set configname
			 */
			colName = "configname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'config_template' data be lost, cause: 'configname' column is not exist.");
				/**
				 * The configname column must be exist in the table of config_template
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'config_template' data be lost, cause: 'configname' column value is null.");
				continue;
			}
			configTemplateDTO.setConfigName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			configTemplateDTO.setId(AhRestoreCommons.convertLong(id));

			if (BeParaModule.DEFAULT_DEVICE_GROUP_NAME.equals(name.trim())) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("configName", name);
				ConfigTemplate newBo = HmBeParaUtil.getDefaultProfile(ConfigTemplate.class, map);
				if (null != newBo) {
					AhRestoreNewMapTools.setMapConfigTemplate(AhRestoreCommons.convertLong(id), newBo.getId());
					AhRestoreNewMapTools.setMapDefaultConfigTemplates(AhRestoreCommons.convertLong(id), newBo.getId());
				}
				continue;
			}

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			configTemplateDTO.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set defaultflag
			 */
			configTemplateDTO.setDefaultFlag(false);

			/**
			 * Set enableairtime
			 */
			colName = "enableairtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableairtime = isColPresent ? xmlParser.getColVal(i, colName) : "";
			configTemplateDTO.setEnableAirTime(AhRestoreCommons.convertStringToBoolean(enableairtime));

			/**
			 * Set enabledmapoverride
			 */
			colName = "enabledmapoverride";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enabledmapoverride = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			configTemplateDTO.setEnabledMapOverride(AhRestoreCommons.convertStringToBoolean(enabledmapoverride));

			colName = "enable_wireless";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String blnEnableWireless = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			configTemplateDTO.getConfigType().setWirelessEnabled(AhRestoreCommons.convertStringToBoolean(blnEnableWireless));

			colName = "enable_switch";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String blnEnableSwitch = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			configTemplateDTO.getConfigType().setSwitchEnabled(AhRestoreCommons.convertStringToBoolean(blnEnableSwitch));

			// because network policy type design changed many times, need one way to trace it
			// maybe we can use version to judge, but another way is to use field difference for judge
			boolean thirdGenNetworkPolicyType = isColPresent;

			colName = "enable_router";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String blnEnableRouter = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			configTemplateDTO.getConfigType().setRouterEnabled(AhRestoreCommons.convertStringToBoolean(blnEnableRouter));

			colName = "enable_bonjour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String blnEnableBonjour = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			configTemplateDTO.getConfigType().setBonjourEnabled(AhRestoreCommons.convertStringToBoolean(blnEnableBonjour));

			/**
			 * Set blnWirelessRouter
			 */
			//below two columns will be removed start Edinburgh
			colName = "blnWirelessRouter";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent) {
				String blnWirelessRouter = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				//configTemplateDTO.setBlnWirelessRouter(AhRestoreCommons.convertStringToBoolean(blnWirelessRouter));

				/**
				 * Set blnBonjourOnly
				 */
				colName = "blnBonjourOnly";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
				String blnBonjourOnly = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				//configTemplateDTO.setBlnBonjourOnly(AhRestoreCommons.convertStringToBoolean(blnBonjourOnly));
				if (!AhRestoreCommons.convertStringToBoolean(blnBonjourOnly)) {
					configTemplateDTO.getConfigType().setWirelessEnabled(true);
					if (AhRestoreCommons.convertStringToBoolean(blnWirelessRouter)) {
						configTemplateDTO.getConfigType().setRouterEnabled(true);
					}
				} else {
					configTemplateDTO.getConfigType().setBonjourEnabled(true);
				}
			} else if (!thirdGenNetworkPolicyType) {
				// this old version of HM must be older than we import wireless+router type, only wireless only type at that time
				configTemplateDTO.getConfigType().setWirelessEnabled(true);
			}

			/**
			 * Set slainterval
			 */
			colName = "slainterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String slainterval = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(ConfigTemplate.SLA_DEFAULT_NOTIFICATION_INTERVAL);
			configTemplateDTO.setSlaInterval(AhRestoreCommons.convertInt(slainterval));

			/**
			 * Set enablereportcollection
			 */
			colName = "enablereportcollection";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enablereportcollection = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			configTemplateDTO.setEnableReportCollection(AhRestoreCommons.convertStringToBoolean(enablereportcollection));

			/**
			 * Set collectioninterval
			 */
			colName = "collectioninterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectioninterval = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			configTemplateDTO.setCollectionInterval(AhRestoreCommons.convertInt(collectioninterval));

			if (configTemplateDTO.getCollectionInterval()!=1 &&
					configTemplateDTO.getCollectionInterval()!=5 &&
					configTemplateDTO.getCollectionInterval()!=10 &&
					configTemplateDTO.getCollectionInterval()!=30 &&
					configTemplateDTO.getCollectionInterval()!=60) {
				configTemplateDTO.setCollectionInterval(10);
			}
			/**
			 * Set collectionifcrc
			 */
			colName = "collectionifcrc";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionifcrc = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			configTemplateDTO.setCollectionIfCrc(AhRestoreCommons.convertInt(collectionifcrc));

			/**
			 * Set collectioniftxdrop
			 */
			colName = "collectioniftxdrop";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectioniftxdrop = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionIfTxDrop(AhRestoreCommons.convertInt(collectioniftxdrop));

			/**
			 * Set collectionifrxdrop
			 */
			colName = "collectionifrxdrop";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionifrxdrop = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionIfRxDrop(AhRestoreCommons.convertInt(collectionifrxdrop));

			/**
			 * Set collectioniftxretry
			 */
			colName = "collectioniftxretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectioniftxretry = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionIfTxRetry(AhRestoreCommons.convertInt(collectioniftxretry));

			/**
			 * Set collectionifairtime
			 */
			colName = "collectionifairtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionifairtime = isColPresent ? xmlParser.getColVal(i, colName) : "50";
			configTemplateDTO.setCollectionIfAirtime(AhRestoreCommons.convertInt(collectionifairtime));

			/**
			 * Set collectionclienttxdrop
			 */
			colName = "collectionclienttxdrop";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionclienttxdrop = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionClientTxDrop(AhRestoreCommons.convertInt(collectionclienttxdrop));

			/**
			 * Set collectionclientrxdrop
			 */
			colName = "collectionclientrxdrop";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionclientrxdrop = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionClientRxDrop(AhRestoreCommons.convertInt(collectionclientrxdrop));

			/**
			 * Set collectionclienttxretry
			 */
			colName = "collectionclienttxretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionclienttxretry = isColPresent ? xmlParser.getColVal(i, colName) : "40";
			configTemplateDTO.setCollectionClientTxRetry(AhRestoreCommons.convertInt(collectionclienttxretry));

			/**
			 * Set collectionclientairtime
			 */
			colName = "collectionclientairtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String collectionclientairtime = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			configTemplateDTO.setCollectionClientAirtime(AhRestoreCommons.convertInt(collectionclientairtime));

			/**
			 * Set enableConnectionAlarm
			 */
			colName="enableConnectionAlarm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String enableConnectionAlarm = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			configTemplateDTO.setEnableConnectionAlarm(AhRestoreCommons.convertStringToBoolean(enableConnectionAlarm));

			/**
			 * Set txRetryThreshold
			 */
			colName = "txRetryThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String txRetryThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "20";
			configTemplateDTO.setTxRetryThreshold(AhRestoreCommons.convertInt(txRetryThreshold));

			/**
			 * Set txFrameErrorThreshold
			 */
			colName = "txFrameErrorThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String txFrameErrorThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			configTemplateDTO.setTxFrameErrorThreshold(AhRestoreCommons.convertInt(txFrameErrorThreshold));

			/**
			 * Set probRequestThreshold
			 */
			colName = "probRequestThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String probRequestThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "5";
			configTemplateDTO.setProbRequestThreshold(AhRestoreCommons.convertInt(probRequestThreshold));

			/**
			 * Set egressMulticastThreshold
			 */
			colName = "egressMulticastThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String egressMulticastThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "5000";
			configTemplateDTO.setEgressMulticastThreshold(AhRestoreCommons.convertInt(egressMulticastThreshold));

			/**
			 * Set ingressMulticastThreshold
			 */
			colName = "ingressMulticastThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String ingressMulticastThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "5000";
			configTemplateDTO.setIngressMulticastThreshold(AhRestoreCommons.convertInt(ingressMulticastThreshold));

			/**
			 * Set channelUtilizationThreshold
			 */
			colName = "channelUtilizationThreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String channelUtilizationThreshold = isColPresent ? xmlParser.getColVal(i, colName) : "70";
			configTemplateDTO.setChannelUtilizationThreshold(AhRestoreCommons.convertInt(channelUtilizationThreshold));

			/**
			 * Set ids_policy_id
			 */
			colName = "ids_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String ids_policy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!ids_policy_id.equals("") && !ids_policy_id.trim().equalsIgnoreCase("null")) {
				Long newIdsPolicyId = AhRestoreNewMapTools.getMapIDSPolicy(Long.parseLong(ids_policy_id.trim()));
				IdsPolicy idsPolicy = AhRestoreNewTools.CreateBoWithId(IdsPolicy.class,newIdsPolicyId);
				configTemplateDTO.setIdsPolicy(idsPolicy);
			}

			/**
			 * Set lldpcdp_id
			 */
			colName = "lldpcdp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String lldpcdp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!lldpcdp_id.equals("") && !lldpcdp_id.trim().equalsIgnoreCase("null")) {
				Long newlldpcdpId = AhRestoreNewMapTools.getLLDPCDPProfileMap(Long.parseLong(lldpcdp_id.trim()));
				LLDPCDPProfile tmpProfile = AhRestoreNewTools.CreateBoWithId(LLDPCDPProfile.class,newlldpcdpId);
				configTemplateDTO.setLldpCdp(tmpProfile);
			}

			/**
			 * Set RADIUS_PROXY_ID
			 */
			colName = "radius_proxy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String radius_proxy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!radius_proxy_id.equals("") && !radius_proxy_id.trim().equalsIgnoreCase("null")) {
				RadiusProxy tmpProfile = AhRestoreNewMapTools.getMapRadiusProxy(Long.parseLong(radius_proxy_id.trim()));
				configTemplateDTO.setRadiusProxyProfile(tmpProfile);
			}

			/**
			 * Set enabledWanConfiguration
			 */
			colName = "enabledWanConfiguration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enabledWanConfiguration = isColPresent ? xmlParser.getColVal(i, colName) : "false";// restore from before Gotham, this field will be set false.
			configTemplateDTO.setEnabledWanConfiguration(AhRestoreCommons.convertStringToBoolean(enabledWanConfiguration));
			
			/**
			 * Set radius_server_id
			 */
			colName = "radius_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String radius_server_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!radius_server_id.equals("") && !radius_server_id.trim().equalsIgnoreCase("null")) {
				Long newradiusserverid = AhRestoreNewMapTools.getMapRadiusServerOnHiveAP(Long.parseLong(radius_server_id.trim()));
				RadiusOnHiveap tmpProfile = AhRestoreNewTools.CreateBoWithId(RadiusOnHiveap.class,newradiusserverid);
				configTemplateDTO.setRadiusServerProfile(tmpProfile);
			}

//			/**
//			 * Set enabledRouterPpskServer
//			 */
//			colName = "enabledRouterPpskServer";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"config_template", colName);
//			String enabledRouterPpskServer = isColPresent ? xmlParser.getColVal(i, colName) : "false";
//			configTemplateDTO.setEnabledRouterPpskServer(AhRestoreCommons.convertStringToBoolean(enabledRouterPpskServer));

			/**
			 * Set vlan_id
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String vlan_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!vlan_id.equals("") && !vlan_id.trim().equalsIgnoreCase("null")) {
				Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(vlan_id.trim()));
				Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,newVlanId);
				configTemplateDTO.setVlan(vlan);
			}

			if (configTemplateDTO.getVlan()==null) {
				Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
				configTemplateDTO.setVlan(vlanClass);
			}

			/**
			 * Set native_vlan_id
			 */
			colName = "native_vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String native_vlan_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!native_vlan_id.equals("") && !native_vlan_id.trim().equalsIgnoreCase("null")) {
				Long newNativeVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(native_vlan_id.trim()));
				Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,newNativeVlanId);
				configTemplateDTO.setVlanNative(vlan);
			}

			if (configTemplateDTO.getVlanNative()==null) {
				Vlan vlanClass = QueryUtil.findBoByAttribute(Vlan.class, "defaultFlag", true);
				configTemplateDTO.setVlanNative(vlanClass);
			}

			/**
			 * Set mgt_network_id
			 */
			colName = "mgt_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent) {
				String mgt_network_id = xmlParser.getColVal(i, colName);
				if (!mgt_network_id.equals("") && !mgt_network_id.trim().equalsIgnoreCase("null")) {
					Long vlanId=AhRestoreNewMapTools.getMapNetworkObjectVlan(Long.parseLong(mgt_network_id.trim()));
					if (vlanId!=null) {
						Long newVlanId = AhRestoreNewMapTools.getMapVlan(vlanId);
						Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,newVlanId);
						if (vlan!=null && configTemplateDTO.getConfigType().isRouterContained()) {
							configTemplateDTO.setVlan(vlan);
						}
					}
					Long newNetworkId = AhRestoreNewMapTools.getMapVpnNetwork(Long.parseLong(mgt_network_id.trim()));
					if(RestoreConfigNetwork.RESTORE_BEFORE_DARKA_FLAG) {
					    newNetworkId = createManagementNetwork(newNetworkId);
					}
					if (configTemplateDTO.getConfigType().isRouterContained() && newNetworkId !=null) {
						VpnNetwork network = AhRestoreNewTools.CreateBoWithId(VpnNetwork.class,newNetworkId);
						AhRestoreNewMapTools.setMapWlanNetworkObjectVlan(configTemplateDTO.getId(),configTemplateDTO.getVlan(),network);
					}
				}
			}

			/**
			 * Set ip_filter_id
			 */
			colName = "ip_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String ip_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!ip_filter_id.equals("") && !ip_filter_id.trim().equalsIgnoreCase("null")) {
				Long newIpFilterId = AhRestoreNewMapTools.getMapMgtIpFilter(Long.parseLong(ip_filter_id.trim()));
				IpFilter ipFilter = AhRestoreNewTools.CreateBoWithId(IpFilter.class,newIpFilterId);
				configTemplateDTO.setIpFilter(ipFilter);
			}

			/**
			 * Set firewall_policy_id
			 */
			colName = "firewall_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String firewall_policy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!firewall_policy_id.equals("") && !firewall_policy_id.trim().equalsIgnoreCase("null")) {
				Long newFirewallPolicyId = AhRestoreNewMapTools.getMapFirewallPolicy(Long.parseLong(firewall_policy_id.trim()));
				FirewallPolicy firewallPolicy = AhRestoreNewTools.CreateBoWithId(FirewallPolicy.class,newFirewallPolicyId);
				configTemplateDTO.setFwPolicy(firewallPolicy);
			}

			/**
			 * Set access_console_id
			 */
			colName = "access_console_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String access_console_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!access_console_id.equals("") && !access_console_id.trim().equalsIgnoreCase("null")) {
				Long newAccessConsoleId = AhRestoreNewMapTools.getMapAccessConsole(Long.parseLong(access_console_id.trim()));
				AccessConsole accessConsole = AhRestoreNewTools.CreateBoWithId(AccessConsole.class,newAccessConsoleId);
				configTemplateDTO.setAccessConsole(accessConsole);
			}

			/**
			 * Set mgmt_service_syslog_id
			 */
			colName = "mgmt_service_syslog_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String mgmt_service_syslog_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!mgmt_service_syslog_id.equals("") && !mgmt_service_syslog_id.trim().equalsIgnoreCase("null")) {
				Long newmgmt_service_syslogId = AhRestoreNewMapTools.getMapSyslog(Long.parseLong(mgmt_service_syslog_id.trim()));
				MgmtServiceSyslog mgmtServiceSyslog = AhRestoreNewTools.CreateBoWithId(MgmtServiceSyslog.class,newmgmt_service_syslogId);
				configTemplateDTO.setMgmtServiceSyslog(mgmtServiceSyslog);
			}

			/**
			 * Set mgmt_service_snmp_id
			 */
			colName = "mgmt_service_snmp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String mgmt_service_snmp_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!mgmt_service_snmp_id.equals("") && !mgmt_service_snmp_id.trim().equalsIgnoreCase("null")) {
				Long newmgmt_service_snmpId = AhRestoreNewMapTools.getMapSnmp(Long.parseLong(mgmt_service_snmp_id.trim()));
				MgmtServiceSnmp mgmtServiceSnmp = AhRestoreNewTools.CreateBoWithId(MgmtServiceSnmp.class,newmgmt_service_snmpId);
				configTemplateDTO.setMgmtServiceSnmp(mgmtServiceSnmp);
			}

			if (configTemplateDTO.getMgmtServiceSnmp()==null) {
				MgmtServiceSnmp mgmtServiceSnmpClass = QueryUtil.findBoByAttribute(MgmtServiceSnmp.class, "defaultFlag", true);
				configTemplateDTO.setMgmtServiceSnmp(mgmtServiceSnmpClass);
			}

			/**
			 * Set mgmt_service_dns_id
			 */
			colName = "mgmt_service_dns_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String mgmt_service_dns_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!mgmt_service_dns_id.equals("") && !mgmt_service_dns_id.trim().equalsIgnoreCase("null")) {
				Long newmgmt_service_dns_Id = AhRestoreNewMapTools.getMapDns(Long.parseLong(mgmt_service_dns_id.trim()));
				MgmtServiceDns mgmtServiceDns = AhRestoreNewTools.CreateBoWithId(MgmtServiceDns.class,newmgmt_service_dns_Id);
				configTemplateDTO.setMgmtServiceDns(mgmtServiceDns);
			}

			/**
			 * Set radius_attrs_id
			 */
			colName = "radius_attrs_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String radius_attrs_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!radius_attrs_id.equals("") && !radius_attrs_id.trim().equalsIgnoreCase("null")) {
				Long newradius_attrs_id = AhRestoreNewMapTools.getMapRadiusAttrs(Long.parseLong(radius_attrs_id.trim()));
				RadiusAttrs radiusAttrs = AhRestoreNewTools.CreateBoWithId(RadiusAttrs.class, newradius_attrs_id);
				configTemplateDTO.setRadiusAttrs(radiusAttrs);
			}

			/**
			 * Set mgmt_service_time_id
			 */
			colName = "mgmt_service_time_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String mgmt_service_time_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!mgmt_service_time_id.equals("") && !mgmt_service_time_id.trim().equalsIgnoreCase("null")) {
				Long newmgmt_service_time_Id = AhRestoreNewMapTools.getMapTimeAndDate(Long.parseLong(mgmt_service_time_id.trim()));
				MgmtServiceTime mgmtServiceTime = AhRestoreNewTools.CreateBoWithId(MgmtServiceTime.class,newmgmt_service_time_Id);
				configTemplateDTO.setMgmtServiceTime(mgmtServiceTime);
			}

			/**
			 * Set client_watch_id
			 */
			colName = "client_watch_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String client_watch_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!client_watch_id.equals("") && !client_watch_id.trim().equalsIgnoreCase("null")) {
				Long newclient_watch_Id = AhRestoreNewMapTools.getMapLocationClientWatch(Long.parseLong(client_watch_id.trim()));
				LocationClientWatch locationClientWatch = AhRestoreNewTools.CreateBoWithId(LocationClientWatch.class,newclient_watch_Id);
				configTemplateDTO.setClientWatch(locationClientWatch);
			}

			/**
			 * Set  track-wan  fix bug 26578
			 */
			try{
				if(RestoreHiveAp.restore_from_60r2_before){
					colName = "ip_track_id";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"config_template", colName);
					String ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
					if (!ip_track_id.equals("") && !ip_track_id.trim().equalsIgnoreCase("null")) {
						Long newIpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(ip_track_id.trim()));
						MgmtServiceIPTrack trackIp = QueryUtil.findBoById(MgmtServiceIPTrack.class, newIpTrackId);
						MgmtServiceIPTrack trackWan = null;
						if( !(oldTrackWan.containsKey(Long.parseLong(ip_track_id.trim())))){
							Long ids = null;
							trackWan = cloneTrack(trackIp);
							trackWan.setGroupType(1);
							if(trackWan.getTrackName().length() < 28){
								trackWan.setTrackName(trackWan.getTrackName() + "-WAN");
							}else{
								trackWan.setTrackName("Track-WAN-For-Upgrade-" + i);
							}
							try{
								ids = QueryUtil.createBo(trackWan);
							}catch(Exception e)
							{
								AhRestoreDBTools.logRestoreMsg(e.getMessage() + "try to load bo id" + ids);
							}
							oldTrackWan.put(Long.parseLong(ip_track_id.trim()), ids);
						}else{
							trackWan = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,oldTrackWan.get(Long.parseLong(ip_track_id.trim())));
						}
						configTemplateDTO.setPrimaryIpTrack(trackWan);
						configTemplateDTO.setBackup1IpTrack(trackWan);
						configTemplateDTO.setBackup2IpTrack(trackWan);
					}
				}
			}catch(Exception e){
				AhRestoreDBTools.logRestoreMsg("Failured to parse table config_template, field: ip_track_id");
			}

			/**
			 * Set routing_policy_id
			 */
			colName = "routing_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String routing_policy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!routing_policy_id.equals("") && !routing_policy_id.trim().equalsIgnoreCase("null")) {
				Long newroutingpolicyid = AhRestoreNewMapTools.getMapRoutingPolicy(Long.parseLong(routing_policy_id.trim()));
				RoutingProfilePolicy routingPolicy = AhRestoreNewTools.CreateBoWithId(RoutingProfilePolicy.class,newroutingpolicyid);
				configTemplateDTO.setRoutingProfilePolicy(routingPolicy);
			}

			/**
			 * Set routing_pbr_policy_id
			 */
			colName = "routing_pbr_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String routing_pbr_policy_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!routing_pbr_policy_id.equals("") && !routing_pbr_policy_id.trim().equalsIgnoreCase("null")) {
				Long newroutingpbrpolicyid = AhRestoreNewMapTools.getMapRoutingPolicy(Long.parseLong(routing_pbr_policy_id.trim()));
				RoutingProfilePolicy routingProfilePolicy = AhRestoreNewTools.CreateBoWithId(RoutingProfilePolicy.class,newroutingpbrpolicyid);
				configTemplateDTO.setRoutingProfilePolicy(routingProfilePolicy);
			}


			/**
			 * Set appProfileId
			 */
			colName = "appProfileId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String appProfileId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!appProfileId.equals("") && !appProfileId.trim().equalsIgnoreCase("null")) {
				ApplicationProfile appProfile = AhRestoreNewMapTools.getMapAppProfile(Long.parseLong(appProfileId.trim()));
				configTemplateDTO.setAppProfile(appProfile);
			}

			if (null == configTemplateDTO.getAppProfile()) {
				ApplicationProfile appProfile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "defaultFlag", true);
				configTemplateDTO.setAppProfile(appProfile);
			}

			/**
			 * Set hive_profile_id
			 */
			colName = "hive_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String hive_profile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!hive_profile_id.equals("") && !hive_profile_id.trim().equalsIgnoreCase("null")) {
				Long newhive_profile_Id = AhRestoreNewMapTools.getMapHives(Long.parseLong(hive_profile_id.trim()));
				if (newhive_profile_Id!=null) {
					HiveProfile hiveProfile = QueryUtil.findBoById(HiveProfile.class, newhive_profile_Id);
					configTemplateDTO.setHiveProfile(hiveProfile);
				}
			}

			if (configTemplateDTO.getHiveProfile()==null) {
				HiveProfile hiveProfileClass = QueryUtil.findBoByAttribute(HiveProfile.class, "defaultFlag", true);
				configTemplateDTO.setHiveProfile(hiveProfileClass);
				hive_profile_id = hiveProfileClass.getId().toString();
			}

			/**
			 * Set mgmt_service_option_id
			 */
			colName = "mgmt_service_option_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String mgmt_service_option_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			Long serviceOptionNew = AhRestoreNewMapTools.getMapOption(AhRestoreCommons.convertLong(mgmt_service_option_id.trim()));
			if (null != serviceOptionNew) {
				configTemplateDTO.setMgmtServiceOption(AhRestoreNewTools.CreateBoWithId(MgmtServiceOption.class, serviceOptionNew));
			}

			/**
			 * Set radius_service_assign_id, it is removed from 3.2r1
			 */
			colName = "radius_service_assign_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "config_template", colName);
			if (isColPresent) {
				String oldRadius = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
				boolean bool = false;
				if (null != oldRadius || "".equals(oldRadius)) {
					if (null == configTemplateDTO.getMgmtServiceOption()) {
						mgmt_service_option_id = "-1";
					}
					bool = true;
				} else if (null != configTemplateDTO.getMgmtServiceOption()) {
					oldRadius = "-1";
					bool = true;
				}
				if (bool) {
					Map<String, String> radius = allOptionRadius.get(mgmt_service_option_id);
					if (null == radius) {
						Map<String, String> singleRadius = new HashMap<String, String>();
						singleRadius.put(oldRadius, name.trim());
						allOptionRadius.put(mgmt_service_option_id, singleRadius);
					} else {
						String template = radius.get(oldRadius);
						if (null == template) {
							radius.put(oldRadius, name.trim());
						} else {
							radius.put(oldRadius, template + " " + name.trim());
						}
					}
				}
			}

			/**
			 * Set overrideTF4IndividualAPs
			 */
			colName = "overrideTF4IndividualAPs";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String overrideTF4IndividualAPs = isColPresent ? xmlParser.getColVal(i, colName) : "true";// restore from before Firenze, this field will be set true.
			configTemplateDTO.setOverrideTF4IndividualAPs(AhRestoreCommons.convertStringToBoolean(overrideTF4IndividualAPs));

			/**
			 * Set device_service_filter_id
			 */
			colName = "device_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String device_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!device_service_filter_id.equals("") && !device_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newdevice_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(device_service_filter_id.trim()));
				configTemplateDTO.setDeviceServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, newdevice_service_filter_Id));
			} else {
				configTemplateDTO.setDeviceServiceFilter(serviceFilter);
			}

			/**
			 * Set eth0_service_filter_id
			 */
			colName = "eth0_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String eth0_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!eth0_service_filter_id.equals("") && !eth0_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long neweth0_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(eth0_service_filter_id.trim()));
				configTemplateDTO.setEth0ServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, neweth0_service_filter_Id));
			} else {
				configTemplateDTO.setEth0ServiceFilter(serviceFilter);
			}

			/**
			 * Set eth0back_service_filter_id
			 */
			colName = "eth0back_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String eth0back_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!eth0back_service_filter_id.equals("") && !eth0back_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long neweth0back_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(eth0back_service_filter_id.trim()));
				configTemplateDTO.setEth0BackServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, neweth0back_service_filter_Id));
			} else {
				configTemplateDTO.setEth0BackServiceFilter(serviceFilter);
			}

			/**
			 * Set eth1back_service_filter_id
			 */
			colName = "eth1back_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String eth1back_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!eth1back_service_filter_id.equals("") && !eth1back_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long neweth1back_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(eth1back_service_filter_id.trim()));
				configTemplateDTO.setEth1BackServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, neweth1back_service_filter_Id));
			} else {
				configTemplateDTO.setEth1BackServiceFilter(serviceFilter);
			}

			/**
			 * Set red0back_service_filter_id
			 */
			colName = "red0back_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String red0back_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!red0back_service_filter_id.equals("") && !red0back_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newred0back_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(red0back_service_filter_id.trim()));
				configTemplateDTO.setRed0BackServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, newred0back_service_filter_Id));
			} else {
				configTemplateDTO.setRed0BackServiceFilter(serviceFilter);
				}

			/**
			 * Set agg0back_service_filter_id
			 */
			colName = "agg0back_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String agg0back_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!agg0back_service_filter_id.equals("") && !agg0back_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newagg0back_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(agg0back_service_filter_id.trim()));
				configTemplateDTO.setAgg0BackServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, newagg0back_service_filter_Id));
			} else {
				configTemplateDTO.setAgg0BackServiceFilter(serviceFilter);
			}

			/**
			 * Set wire_service_filter_id
			 */
			colName = "wire_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String wire_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!wire_service_filter_id.equals("") && !wire_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newwire_service_filter_Id = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(wire_service_filter_id.trim()));
				configTemplateDTO.setWireServiceFilter(AhRestoreNewTools.CreateBoWithId(ServiceFilter.class, newwire_service_filter_Id));
			}

			/**
			 * Set alg_configuration_id
			 */
			colName = "alg_configuration_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String alg_configuration_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!alg_configuration_id.equals("") && !alg_configuration_id.trim().equalsIgnoreCase("null")) {
				Long newAlgConfigurationId = AhRestoreNewMapTools.getMapAlgConfiguration(Long.parseLong(alg_configuration_id.trim()));
				AlgConfiguration algConfiguration = AhRestoreNewTools.CreateBoWithId(AlgConfiguration.class, newAlgConfigurationId);
				configTemplateDTO.setAlgConfiguration(algConfiguration);
			}

			if (configTemplateDTO.getAlgConfiguration()==null) {
				AlgConfiguration algConfigurationClass = QueryUtil.findBoByAttribute(AlgConfiguration.class, "defaultFlag", true);
				configTemplateDTO.setAlgConfiguration(algConfigurationClass);
			}

			/**
			 * Set location_server_id
			 */
			colName = "location_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String location_server_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!location_server_id.equals("") && !location_server_id.trim().equalsIgnoreCase("null")) {
				Long newLocationServerId = AhRestoreNewMapTools.getMapLocationServer(Long.parseLong(location_server_id.trim()));
				LocationServer locationServer = AhRestoreNewTools.CreateBoWithId(LocationServer.class, newLocationServerId);
				configTemplateDTO.setLocationServer(locationServer);
			}

			/**
			 * Set ethernet_access_id
			 */
			colName = "ethernet_access_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpEthernet_access_id=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpEthernet_access_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(ETH0_ACCESS_PROFILE,
							AhRestoreNewMapTools.getMapEthernetAccessBo(tmpEthernet_access_id));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(
							configTemplateDTO.getId()).put(ETH0_ACCESS_PROFILE,
									AhRestoreNewMapTools.getMapEthernetAccessBo(tmpEthernet_access_id));
				}
			}
//			String ethernet_access_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_access_id.equals("") && !ethernet_access_id.trim().equalsIgnoreCase("null")) {
//				String ethernetAccessName = AhRestoreMapTool.getMapEthernetAccess(ethernet_access_id.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetAccessName,AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetAccess(ethernetAccess);
//			}

			/**
			 * Set ethernet_bridge_id
			 */
			colName = "ethernet_bridge_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_bridge_id=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_bridge_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(ETH0_BRIDGE_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(ETH0_BRIDGE_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id));
				}
			}
//			String ethernet_bridge_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_bridge_id.equals("") && !ethernet_bridge_id.trim().equalsIgnoreCase("null")) {
//				String ethernetBridgeName = AhRestoreMapTool.getMapEthernetAccess(ethernet_bridge_id.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetBridgeName,AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetBridge(ethernetAccess);
//			}

			// Fiona add for dual port
			/**
			 * Set eth1_service_filter_id
			 */
			colName = "eth1_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String eth1_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!eth1_service_filter_id.equals("") && !eth1_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newEth1ServiceFilterId = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(eth1_service_filter_id.trim()));
				configTemplateDTO.setEth1ServiceFilter(QueryUtil.findBoById(ServiceFilter.class, newEth1ServiceFilterId));
			} else {
				configTemplateDTO.setEth1ServiceFilter(serviceFilter);
			}

			/**
			 * Set ethernet_access_id_eth1
			 */
			colName = "ethernet_access_id_eth1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_access_id_eth1=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_access_id_eth1 = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(ETH1_ACCESS_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_eth1));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(ETH1_ACCESS_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_eth1));
				}
			}
//			String ethernet_access_id_eth1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_access_id_eth1.equals("") && !ethernet_access_id_eth1.trim().equalsIgnoreCase("null")) {
//				String ethernetAccessName = AhRestoreMapTool.getMapEthernetAccess(ethernet_access_id_eth1.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetAccessName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetAccessEth1(ethernetAccess);
//			}

			/**
			 * Set ethernet_bridge_id_eth1
			 */
			colName = "ethernet_bridge_id_eth1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_bridge_id_eth1=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_bridge_id_eth1 = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(ETH1_BRIDGE_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_eth1));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(ETH1_BRIDGE_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_eth1));
				}
			}
//			String ethernet_bridge_id_eth1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_bridge_id_eth1.equals("") && !ethernet_bridge_id_eth1.trim().equalsIgnoreCase("null")) {
//				String ethernetBridgeName = AhRestoreMapTool.getMapEthernetAccess(ethernet_bridge_id_eth1.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetBridgeName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetBridgeEth1(ethernetAccess);
//			}
			/**
			 * Set red0_service_filter_id
			 */
			colName = "red0_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String red0_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!red0_service_filter_id.equals("") && !red0_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newRed0ServiceFilterId = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(red0_service_filter_id.trim()));
				configTemplateDTO.setRed0ServiceFilter(QueryUtil.findBoById(ServiceFilter.class, newRed0ServiceFilterId));
			} else {
				configTemplateDTO.setRed0ServiceFilter(serviceFilter);
			}

			/**
			 * Set ethernet_access_id_red
			 */
			colName = "ethernet_access_id_red";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_access_id_red=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_access_id_red = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(RED0_ACCESS_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_red));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(RED0_ACCESS_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_red));
				}
			}
//			String ethernet_access_id_red = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_access_id_red.equals("") && !ethernet_access_id_red.trim().equalsIgnoreCase("null")) {
//				String ethernetAccessName = AhRestoreMapTool.getMapEthernetAccess(ethernet_access_id_red.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetAccessName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetAccessRed(ethernetAccess);
//			}

			/**
			 * Set ethernet_bridge_id_red
			 */
			colName = "ethernet_bridge_id_red";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_bridge_id_red=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_bridge_id_red = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(RED0_BRIDGE_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_red));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(RED0_BRIDGE_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_red));
				}
			}
//			String ethernet_bridge_id_red = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_bridge_id_red.equals("") && !ethernet_bridge_id_red.trim().equalsIgnoreCase("null")) {
//				String ethernetBridgeName = AhRestoreMapTool.getMapEthernetAccess(ethernet_bridge_id_red.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetBridgeName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetBridgeRed(ethernetAccess);
//			}
			/**
			 * Set agg0_service_filter_id
			 */
			colName = "agg0_service_filter_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String agg0_service_filter_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!agg0_service_filter_id.equals("") && !agg0_service_filter_id.trim().equalsIgnoreCase("null")) {
				Long newAgg0ServiceFilterId = AhRestoreNewMapTools.getMapMgtServiceFilter(Long.parseLong(agg0_service_filter_id.trim()));
				configTemplateDTO.setAgg0ServiceFilter(QueryUtil.findBoById(ServiceFilter.class, newAgg0ServiceFilterId));
			} else {
				configTemplateDTO.setAgg0ServiceFilter(serviceFilter);
			}

			/**
			 * Set ethernet_access_id_agg
			 */
			colName = "ethernet_access_id_agg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_access_id_agg=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_access_id_agg = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(AGG0_ACCESS_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_agg));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(AGG0_ACCESS_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_access_id_agg));
				}
			}
//			String ethernet_access_id_agg = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_access_id_agg.equals("") && !ethernet_access_id_agg.trim().equalsIgnoreCase("null")) {
//				String ethernetAccessName = AhRestoreMapTool.getMapEthernetAccess(ethernet_access_id_agg.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetAccessName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetAccessAgg(ethernetAccess);
//			}

			/**
			 * Set ethernet_bridge_id_agg
			 */
			colName = "ethernet_bridge_id_agg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			if (isColPresent){
				Long tmpethernet_bridge_id_agg=null;
				if (!xmlParser.getColVal(i, colName).equals("") && !xmlParser.getColVal(i, colName).equalsIgnoreCase("null")){
					tmpethernet_bridge_id_agg = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				}
				if (AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())==null){
					Map<String,EthernetAccess> tmpHashmap = new HashMap<String,EthernetAccess>();
					tmpHashmap.put(AGG0_BRIDGE_PROFILE, AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_agg));
					AhRestoreNewMapTools.setMapEthernetAccessResotre(configTemplateDTO.getId(), tmpHashmap);
				} else {
					AhRestoreNewMapTools.getMapEthernetAccessResotre(configTemplateDTO.getId())
					.put(AGG0_BRIDGE_PROFILE,AhRestoreNewMapTools.getMapEthernetAccessBo(tmpethernet_bridge_id_agg));
				}
			}
//			String ethernet_bridge_id_agg = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			if (!ethernet_bridge_id_agg.equals("") && !ethernet_bridge_id_agg.trim().equalsIgnoreCase("null")) {
//				String ethernetBridgeName = AhRestoreMapTool.getMapEthernetAccess(ethernet_bridge_id_agg.trim());
//				EthernetAccess ethernetAccess = QueryUtil.findBoByAttribute(EthernetAccess.class, "ethernetName", ethernetBridgeName, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
//				configTemplateDTO.setEthernetBridgeAgg(ethernetAccess);
//			}
			// add end

			/**
			 * Set qos_classification_id
			 */
			colName = "qos_classification_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String qos_classification_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!qos_classification_id.equals("") && !qos_classification_id.trim().equalsIgnoreCase("null")) {
				Long newQosClassificationId = AhRestoreNewMapTools.getMapQosClassification(Long.parseLong(qos_classification_id.trim()));
				QosClassification qosClassification = QueryUtil.findBoById(QosClassification.class, newQosClassificationId);
				configTemplateDTO.setClassifierMap(qosClassification);
			}

			/**
			 * Set qos_marking_id
			 */
			colName = "qos_marking_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String qos_marking_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!qos_marking_id.equals("")&& !qos_marking_id.trim().equalsIgnoreCase("null")) {
				Long newQosMarkingId = AhRestoreNewMapTools.getMapMarking(Long.parseLong(qos_marking_id.trim()));
				QosMarking qosMarking = QueryUtil.findBoById(QosMarking.class, newQosMarkingId);
				configTemplateDTO.setMarkerMap(qosMarking);
			}

			/**
			 * Set vpn_service_id
			 */
			colName = "vpn_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String vpn_service_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!vpn_service_id.equals("") && !vpn_service_id.trim().equalsIgnoreCase("null")) {
				Long newVpnServiceId = AhRestoreNewMapTools.getMapVpnService(Long.parseLong(vpn_service_id.trim()));
				VpnService vpnService = QueryUtil.findBoById(VpnService.class, newVpnServiceId);
				configTemplateDTO.setVpnService(vpnService);
			}

			/**
			 * Set enableprobe
			 */
			colName = "enableProbe";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableProbe = isColPresent ? xmlParser.getColVal(i, colName) : "";
			configTemplateDTO.setEnableProbe(AhRestoreCommons.convertStringToBoolean(enableProbe));

			/**
			 * Set probeinterval
			 */
			colName = "probeinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String probeinterval = isColPresent ? xmlParser.getColVal(i, colName) : "60";
			configTemplateDTO.setProbeInterval(AhRestoreCommons.convertInt(probeinterval));

			/**
			 * Set proberetrycount
			 */
			colName = "proberetrycount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String proberetrycount = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			configTemplateDTO.setProbeRetryCount(AhRestoreCommons.convertInt(proberetrycount));

			/**
			 * Set proberetryinterval
			 */
			colName = "proberetryinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String proberetryinterval = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			configTemplateDTO.setProbeRetryInterval(AhRestoreCommons.convertInt(proberetryinterval));

			/**
			 * Set probeusername
			 */
			colName = "probeusername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String probeusername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			configTemplateDTO.setProbeUsername(probeusername);

			/**
			 * Set probepassword
			 */
			colName = "probepassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String probepassword = isColPresent ? xmlParser.getColVal(i, colName) : "";
			configTemplateDTO.setProbePassword(probepassword);

			/**
			 * Set enableosdurl
			 */
			colName = "enableosdurl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableosdurl = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			configTemplateDTO.setEnableOSDURL(AhRestoreCommons.convertStringToBoolean(enableosdurl));

			/**
			 * Set enabletvservice
			 */
			colName = "enabletvservice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enabletvservice = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableTVService(AhRestoreCommons.convertStringToBoolean(enabletvservice));

			/**
			 * Set enableHttpServer
			 */
			colName = "enableHttpServer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableHttpServer = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			configTemplateDTO.setEnableHttpServer(AhRestoreCommons.convertStringToBoolean(enableHttpServer));

			/**
			 * Set enableEth0LimitDownloadBandwidth
			 */
			colName = "enableEth0LimitDownloadBandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableEth0LimitDownloadBandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableEth0LimitDownloadBandwidth(AhRestoreCommons.convertStringToBoolean(enableEth0LimitDownloadBandwidth));

			/**
			 * Set enableEth0LimitUploadBandwidth
			 */
			colName = "enableEth0LimitUploadBandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableEth0LimitUploadBandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableEth0LimitUploadBandwidth(AhRestoreCommons.convertStringToBoolean(enableEth0LimitUploadBandwidth));

			/**
			 * Set eth0LimitDownloadRate
			 */
			colName = "eth0LimitDownloadRate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			int eth0LimitDownloadRate = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 100;
			configTemplateDTO.setEth0LimitDownloadRate((short)eth0LimitDownloadRate);

			/**
			 * Set eth0LimitUploadRate
			 */
			colName = "eth0LimitUploadRate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			int eth0LimitUploadRate = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 100;
			configTemplateDTO.setEth0LimitUploadRate((short)eth0LimitUploadRate);

			/**
			 * Set enableUSBLimitDownloadBandwidth
			 */
			colName = "enableUSBLimitDownloadBandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableUSBLimitDownloadBandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableUSBLimitDownloadBandwidth(AhRestoreCommons.convertStringToBoolean(enableUSBLimitDownloadBandwidth));

			/**
			 * Set enableUSBLimitUploadBandwidth
			 */
			colName = "enableUSBLimitDownloadBandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String enableUSBLimitUploadBandwidth = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableUSBLimitUploadBandwidth(AhRestoreCommons.convertStringToBoolean(enableUSBLimitUploadBandwidth));

			/**
			 * Set usbLimitDownloadRate
			 */
			colName = "usbLimitDownloadRate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			int usbLimitDownloadRate = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 100;
			configTemplateDTO.setUsbLimitDownloadRate((short)usbLimitDownloadRate);

			/**
			 * Set usbLimitUploadRate
			 */
			colName = "usbLimitUploadRate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			int usbLimitUploadRate = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 100;
			configTemplateDTO.setUsbLimitUploadRate((short)usbLimitUploadRate);


			//storm control
			/**
			 * Set switchStormControlMode
			 */
			colName = "switchStormControlMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			int switchStormControlMode = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 0;
			configTemplateDTO.setSwitchStormControlMode((short)switchStormControlMode);

			// for 802.1X
            colName = "clientExpireTime8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "config_template", colName);
            String clientExpireTime8021X = isColPresent ? xmlParser.getColVal(i, colName) : String
                    .valueOf(ConfigTemplate.DEFAULT_CLIENT_EXPIRE_TIME);
            configTemplateDTO.setClientExpireTime8021X(AhRestoreCommons.convertInt(clientExpireTime8021X));
            colName = "clientSuppressInterval8021X";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "config_template", colName);
            String clientSuppressInterval8021X = isColPresent ? xmlParser.getColVal(i, colName)
                    : String.valueOf(ConfigTemplate.DEFAULT_CLIENT_SUPPRESS_INTERVAL);
            configTemplateDTO.setClientSuppressInterval8021X(AhRestoreCommons
                    .convertInt(clientSuppressInterval8021X));

            /**
             * Set bonjour_gateway_id
             */
            colName = "bonjour_gateway_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                "config_template", colName);
            String bonjourId = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (!bonjourId.equals("") && !bonjourId.trim().equalsIgnoreCase("null")) {
                Long newBonjourId = AhRestoreNewMapTools.getMapBonjourGatewaySetting(Long
                        .parseLong(bonjourId.trim()));
                BonjourGatewaySettings bgSettings = AhRestoreNewTools.CreateBoWithId(
                        BonjourGatewaySettings.class, newBonjourId);
                configTemplateDTO.setBonjourGw(bgSettings);
            }
            
            /**
			 * Set enableDelayAlarm from Guadalupe
			 */
			colName="enableDelayAlarm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String enableDelayAlarm = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			configTemplateDTO.setEnableDelayAlarm(AhRestoreCommons.convertStringToBoolean(enableDelayAlarm));


			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'config_template' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			configTemplateDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (configTemplateDTO.isDefaultFlag()) {
				templatesInfo.add(0,configTemplateDTO);
			} else {
				templatesInfo.add(configTemplateDTO);
			}

			/**
			 * Set switch_settings_id
			 */
			colName = "switch_settings_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String switch_settings_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(!switch_settings_id.equals("") && !switch_settings_id.trim().equalsIgnoreCase("null")) {
				//restore stp and switch settings
				if(allSwitchSettings != null && !allSwitchSettings.isEmpty()){
					SwitchSettings switchSettings = allSwitchSettings.get(switch_settings_id);
					configTemplateDTO.setSwitchSettings(switchSettings);
				}
			}

			String oldTrackColName = "eth0_ip_track_id";
			boolean isoldTrackColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", oldTrackColName);

			if(isoldTrackColPresent){
				colName = "eth0_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String eth0_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!eth0_ip_track_id.equals("") && !eth0_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long newEth0IpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(eth0_ip_track_id.trim()));
					MgmtServiceIPTrack eth0IpTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,newEth0IpTrackId);
					configTemplateDTO.setPrimaryIpTrack(eth0IpTrack);
				}



				colName = "usbnet0_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String usbnet0_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!usbnet0_ip_track_id.equals("") && !usbnet0_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long newUsbnet0IpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(usbnet0_ip_track_id.trim()));
					MgmtServiceIPTrack ipTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,newUsbnet0IpTrackId);
					configTemplateDTO.setBackup1IpTrack(ipTrack);
				}

				colName = "thirdport_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String third_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!third_ip_track_id.equals("") && !third_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long thirdportIpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(third_ip_track_id.trim()));
					MgmtServiceIPTrack ipTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,thirdportIpTrackId);
					configTemplateDTO.setBackup2IpTrack(ipTrack);
				}
			}else{
//				PRIMARY_IP_TRACK_ID
//				BACKUP1_IP_TRACK_ID
//				BACKUP2_IP_TRACK_ID

				colName = "primary_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String primary_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!primary_ip_track_id.equals("") && !primary_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long newEth0IpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(primary_ip_track_id.trim()));
					MgmtServiceIPTrack eth0IpTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,newEth0IpTrackId);
					configTemplateDTO.setPrimaryIpTrack(eth0IpTrack);
				}

				colName = "backup1_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String backup1_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!backup1_ip_track_id.equals("") && !backup1_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long newUsbnet0IpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(backup1_ip_track_id.trim()));
					MgmtServiceIPTrack ipTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,newUsbnet0IpTrackId);
					configTemplateDTO.setBackup1IpTrack(ipTrack);
				}

				colName = "backup2_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
				String backup3_ip_track_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				if (!backup3_ip_track_id.equals("") && !backup3_ip_track_id.trim().equalsIgnoreCase("null")) {
					Long thirdportIpTrackId = AhRestoreNewMapTools.getMapMgmtIpTracking(Long.parseLong(backup3_ip_track_id.trim()));
					MgmtServiceIPTrack ipTrack = AhRestoreNewTools.CreateBoWithId(MgmtServiceIPTrack.class,thirdportIpTrackId);
					configTemplateDTO.setBackup2IpTrack(ipTrack);
				}


			}



			colName = "thirdport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
			String thirdportValueStr = isColPresent?xmlParser.getColVal(i, colName):"5";
			if (!thirdportValueStr.equals("") && !thirdportValueStr.trim().equalsIgnoreCase("null")) {
				long thirdportValue = Long.parseLong(thirdportValueStr);
				configTemplateDTO.setThirdPort(thirdportValue);
			}

			colName = "enablel7switch";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
			String enableL7SwitchStr = isColPresent ? xmlParser.getColVal(i, colName) : "t";
			configTemplateDTO.setEnableL7Switch(AhRestoreCommons.convertStringToBoolean(enableL7SwitchStr));
			
			colName = "enableKddr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,"config_template", colName);
			String enableNetdump = isColPresent ? xmlParser.getColVal(i, colName) : "f";
			configTemplateDTO.setEnableKddr(AhRestoreCommons.convertStringToBoolean(enableNetdump));
			
			/**
			 * Set supplemental_cli_id
			 */
			colName = "supplemental_cli_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"config_template", colName);
			String supplemental_cli_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (supplemental_cli_id != null && !(supplemental_cli_id.trim().equals(""))
					&& !(supplemental_cli_id.trim().equalsIgnoreCase("null"))) {
				Long supplemental_cli_id_new = AhRestoreNewMapTools
						.getMapCLIBlob(AhRestoreCommons
								.convertLong(supplemental_cli_id));
				
				if (null != supplemental_cli_id_new) {
					configTemplateDTO.setSupplementalCLI(AhRestoreNewTools.CreateBoWithId(
							CLIBlob.class, supplemental_cli_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new supplemental CLI id mapping to old id in network policy:"
									+ supplemental_cli_id);
				}
			}
		}

		return templatesInfo;
	}

	private static MgmtServiceIPTrack cloneTrack(MgmtServiceIPTrack track){
		if(track == null){
			return null;
		}
		MgmtServiceIPTrack trackwan = new MgmtServiceIPTrack();
		trackwan.setDescription(track.getDescription());
		trackwan.setGroupType(track.getGroupType());
		trackwan.setInterval(track.getInterval());
		trackwan.setRetryTime(track.getRetryTime());
		trackwan.setTrackName(track.getTrackName());
		trackwan.setTrackLogic(track.getTrackLogic());
		trackwan.setVersion(track.getVersion());
		trackwan.setIpAddresses(track.getIpAddresses());
		trackwan.setOwner(track.getOwner());
		trackwan.setUseGateway(track.isUseGateway());
		trackwan.setEnableTrack(track.isEnableTrack());
		trackwan.setVersion(new Timestamp(System.currentTimeMillis()));
		return trackwan;
	}

    /**
     * TA2644: create a new (management) network if necessary.
     * @author Yunzhi Lin
     * - Time: Feb 29, 2012 7:57:49 PM
     * @param networkId -
     * @return (new) networkId or NULL
     */
    public static Long createManagementNetwork(Long networkId) {
        VpnNetwork vpnNetwork = AhRestoreNewMapTools.getMapVpnNetworkBo(networkId);
        if(null != vpnNetwork
                && vpnNetwork.getNetworkType() != VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT) {
            try {
                String networkName = vpnNetwork.getNetworkName();
                // chceck if the object is exist
                List<VpnNetwork> list = QueryUtil.executeQuery(VpnNetwork.class, null, new FilterParams(
                        "networkType=:s1 and networkName=:s2", new Object[] {
                                VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT, networkName }), vpnNetwork
                        .getOwner().getId());
                if(!list.isEmpty()) {
                    return list.get(0).getId();
                }

                QueryUtil.updateBo(VpnNetwork.class, "networkType=:s1", new FilterParams("id=:s2",
                		new Object[] {VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT, networkId}));
                return networkId;
            } catch (Exception e) {
                AhRestoreDBTools.logRestoreMsg("create a new management Network error");
                AhRestoreDBTools.logRestoreMsg(e.getMessage());
            }
        } else {
            return networkId;
        }
        return null;
    }

	public static boolean restoreTemplates()
	{
		try
		{
			bln31to32flg= false;
			updateSsid.clear();
			nameCount=1;
			issueMapList.clear();
			ssidWlanIDMapping.clear();
			allTunnelPolicy.clear();
			allTunnelInformation.clear();
			allOptionRadius.clear();
			AhRestoreDBTools.logRestoreMsg("getAllTemplates() begin");
			final Map<String, StpSettings> allStpSettings = getAllStpSettings();
			final Map<String, SwitchSettings> allSwitchSettings = getAllSwitchSettings(allStpSettings);

			List<ConfigTemplate> allConfigTemplate = getAllTemplates(allSwitchSettings);
			AhRestoreDBTools.logRestoreMsg("getAllConfigTemplateSsid() begin");
			Map<String, Map<Long, ConfigTemplateSsid>> allConfigTemplateSsid = getAllConfigTemplateSsid();
			Map<String, Set<MgmtServiceIPTrack>> allIpTrack = getAllConfigTemplateIPTrack();
			Map<String, Set<NetworkService>> allNetworkService = getAllConfigTemplateNetworkService();

            Map<String, Set<PortGroupProfile>> allPortTemplates = null;
            if (RestoreWiredPortTemplate.RESTORE_HM_BEFORE_ESSON) {
                AhRestoreDBTools.logRestoreMsg("getAllConfigTemplateLANs() begin");
                allPortTemplates = RestoreWiredPortTemplate.convertLANs2PortTemplate(allConfigTemplate);
            } else {
                AhRestoreDBTools
                        .logRestoreMsg("getAllConfigTemplatePorts() begin");
                allPortTemplates = getAllConfigTemplatePorts();
            }

//			AhRestoreDBTools.logRestoreMsg("getAllConfigTemplateQos() begin");
//			Map<String, Map<String, ConfigTemplateQos>> allConfigTemplateQos = getAllConfigTemplateQos();
			AhRestoreDBTools.logRestoreMsg("getAllTemplateSsidUserProfile() begin");
			Map<String, Map<String, ConfigTemplateSsidUserProfile>> allTemplateSsidUserProfile = getAllTemplateSsidUserProfile();

			AhRestoreDBTools.logRestoreMsg("getAllVlanNetworkMaping() begin");
			Map<String, List<ConfigTemplateVlanNetwork>> allVlanNetworkMaping = getAllConfigTemplateVlanNetworkMaping();

			AhRestoreDBTools.logRestoreMsg("getAllUserProfileVlanMappings() begin");
			Map<String, Set<UserProfileVlanMapping>> upVlanMappings = getAllUserProfileVlanMappings();

			AhRestoreDBTools.logRestoreMsg("getAllConfigTemplateStormControl() begin");
			Map<String, List<ConfigTemplateStormControl>> allStormControl = getAllConfigTemplateStormControl();

			/*
			 * for Tunnel Policy
			 */
			Map<String, Set<String>> oneHive;
			Map<String, ConfigTemplateSsidUserProfile> allUserProfile;
			if(null == allConfigTemplate)
			{
				AhRestoreDBTools.logRestoreMsg("allConfigTemplate is null");
				return false;
			}
			else
			{
				AhRestoreDBTools.logRestoreMsg("set data to allConfigTemplate begin");
				for (ConfigTemplate tempConfigTemplate : allConfigTemplate) {
					if (tempConfigTemplate != null) {
						// edit for dual port by Fiona
						Map<Long, ConfigTemplateSsid> ssidInter = allConfigTemplateSsid.get(tempConfigTemplate.getId().toString());
						if (null == ssidInter) {
							ssidInter = new HashMap<Long, ConfigTemplateSsid>();
							AhRestoreDBTools.logRestoreMsg("Error--- ssidInter is null, Network Policy Name:" + tempConfigTemplate.getConfigName() + ", Old Id:" + tempConfigTemplate.getId());

							/*
							ConfigTemplateSsid newInter;

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("eth0");
							ssidInter.put((long) -1, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("eth1");
							ssidInter.put((long) -2, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("red0");
							ssidInter.put((long) -3, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("agg0");
							ssidInter.put((long) -4, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("eth2");
							ssidInter.put((long) -5, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("eth3");
							ssidInter.put((long) -6, newInter);

							newInter = new ConfigTemplateSsid();
							newInter.setInterfaceName("eth4");
							ssidInter.put((long) -7, newInter);
							*/
							ssidInter = BoGenerationUtil.genDefaultSsidInterfaces();

						} else {
							for (int k = -7; k < -1; k++) {
								String interName = "eth1";
								ConfigTemplateSsid newInter;
								if (null == ssidInter.get((long) k)) {
									newInter = new ConfigTemplateSsid();
									switch (k) {
										case -7:
											interName = "eth4";
											break;
										case -6:
											interName = "eth3";
											break;
										case -5:
											interName = "eth2";
											break;
										case -4:
											interName = "agg0";
											break;
										case -3:
											interName = "red0";
											break;
										default:
											break;

									}
									newInter.setInterfaceName(interName);
									ssidInter.put((long) k, newInter);
								}
							}
						}

						tempConfigTemplate.setSsidInterfaces(ssidInter);
						if (allIpTrack == null || allIpTrack.get(tempConfigTemplate.getId().toString()) == null) {
							tempConfigTemplate.setIpTracks(new HashSet<MgmtServiceIPTrack>());
						} else {
							tempConfigTemplate.setIpTracks(allIpTrack.get(tempConfigTemplate.getId().toString()));
						}

						if (allNetworkService == null || allNetworkService.get(tempConfigTemplate.getId().toString()) == null) {
							tempConfigTemplate.setTvNetworkService(new HashSet<NetworkService>());
						} else {
							tempConfigTemplate.setTvNetworkService(allNetworkService.get(tempConfigTemplate.getId().toString()));
						}
						// Port Template Profiles
						if (allPortTemplates == null || allPortTemplates.get(tempConfigTemplate.getId().toString()) == null) {
							tempConfigTemplate.setPortProfiles(new HashSet<PortGroupProfile>());
						} else {
						    tempConfigTemplate.setPortProfiles(allPortTemplates.get(tempConfigTemplate.getId().toString()));
						}

						if (allVlanNetworkMaping==null || allVlanNetworkMaping.get(tempConfigTemplate.getId().toString())==null) {
							tempConfigTemplate.setVlanNetwork(new ArrayList<ConfigTemplateVlanNetwork>());
						} else {
							tempConfigTemplate.setVlanNetwork(allVlanNetworkMaping.get(tempConfigTemplate.getId().toString()));
						}

						if (upVlanMappings==null || upVlanMappings.get(tempConfigTemplate.getId().toString())==null) {
							tempConfigTemplate.setUpVlanMapping(new HashSet<UserProfileVlanMapping>());
						} else {
							prepareNpRelatedUserProfileVlanMapping(upVlanMappings.get(tempConfigTemplate.getId().toString()), tempConfigTemplate);
							tempConfigTemplate.setUpVlanMapping(upVlanMappings.get(tempConfigTemplate.getId().toString()));
						}
						if(allStormControl==null || allStormControl.get(tempConfigTemplate.getId().toString())==null){
							tempConfigTemplate.setStormControlList(new ArrayList<ConfigTemplateStormControl>());
						} else {
							tempConfigTemplate.setStormControlList(allStormControl.get(tempConfigTemplate.getId().toString()));
						}


						// end edit

//						tempConfigTemplate.setQosPolicies(allConfigTemplateQos.get(tempConfigTemplate.getId().toString()));

						/*
						 * for Tunnel Policy
						 */
						if (tunnelChangeFlag) {
							String hiveName = tempConfigTemplate.getHiveProfile().getHiveName();
							oneHive = allTunnelPolicy.get(hiveName);
							if (null == oneHive) {
								oneHive = new HashMap<String, Set<String>>();
								allTunnelPolicy.put(hiveName, oneHive);
								oneHive = allTunnelPolicy.get(hiveName);
							}
							allUserProfile = allTemplateSsidUserProfile.get(tempConfigTemplate.getId().toString());
							if (null != allUserProfile) {
								String attribute;
								Set<String> tunnels;
								UserProfile oneUP;
								String tunnelId;
								for (ConfigTemplateSsidUserProfile ssidUser : allUserProfile.values()) {
									oneUP = ssidUser.getUserProfile();
									attribute = String.valueOf(oneUP.getAttributeValue());
									tunnels = oneHive.get(attribute);
									tunnelId = AhRestoreNewMapTools.getMapOldUserProfileTunnel(oneUP.getUserProfileName());
									if (null == AhRestoreNewMapTools.getTunnelSettingOldInfo(tunnelId)) {
										continue;
									}
									if (null == tunnels) {
										tunnels = new HashSet<String>();
										tunnels.add(tunnelId);
										oneHive.put(attribute, tunnels);
									} else {
										if (!tunnels.contains(tunnelId)) {
											tunnels.add(tunnelId);
										}
									}
									Map<String, Map<String, Set<String>>> hives = allTunnelInformation.get(tunnelId);
									if (null == hives) {
										hives = new HashMap<String, Map<String, Set<String>>>();
										Set<String> wlans = new HashSet<String>();
										wlans.add(tempConfigTemplate.getConfigName());
										Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
										attributes.put(attribute, wlans);
										hives.put(hiveName, attributes);
										allTunnelInformation.put(tunnelId, hives);
									} else {
										Map<String, Set<String>> attributes = hives.get(hiveName);
										if (null == attributes) {
											attributes = new HashMap<String, Set<String>>();
											Set<String> wlans = new HashSet<String>();
											wlans.add(tempConfigTemplate.getConfigName());
											attributes.put(attribute, wlans);
											hives.put(hiveName, attributes);
										} else {
											Set<String> wlans = attributes.get(attribute);
											if (null == wlans) {
												wlans = new HashSet<String>();
												wlans.add(tempConfigTemplate.getConfigName());
												attributes.put(attribute, wlans);
											} else {
												wlans.add(tempConfigTemplate.getConfigName());
											}
										}
									}
								}
							}
						}
					}
				}
				AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate table begin");

				if(bln31to32flg) {
					updateCwpProfile(allConfigTemplate,allTemplateSsidUserProfile);
					updateSsidUserProfile(allConfigTemplate,allTemplateSsidUserProfile);
					AhRestoreDBTools.logRestoreMsg("update Management Option begin");
					updateMgmtServiceOption(allConfigTemplate.get(0).getOwner());
					AhRestoreDBTools.logRestoreMsg("update Management Option end");
					insertUpdateLog();
				}
				if (bln32r1to32r2flg){
//					resetConfigTemplateQos(allConfigTemplate);
				}

				if (beforeEdinburgh) {
					resetConfigTemplateVlanNetworkMaping(allConfigTemplate);
				}

				List<Long> lOldId = new ArrayList<Long>();

				for (ConfigTemplate configTemplate : allConfigTemplate) {
					lOldId.add(configTemplate.getId());
				}

				QueryUtil.restoreBulkCreateBos(allConfigTemplate);

				for(int i=0; i < allConfigTemplate.size(); ++i)
				{
					AhRestoreNewMapTools.setMapConfigTemplate(lOldId.get(i), allConfigTemplate.get(i).getId());
				}

				AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate table end");
				/*update configTemplateid in table portgrouptemplate*/
				updatePortGroupItemConfigTemplateId();

				if (tunnelChangeFlag) {
					AhRestoreDBTools.logRestoreMsg("restore Static Identity-Based Tunnel Policy begin");
					updateTunnelPolicyAndUserProfile(allConfigTemplate.get(0).getOwner());
					AhRestoreDBTools.logRestoreMsg("restore Static Identity-Based Tunnel Policy end");
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static void updatePortGroupItemConfigTemplateId(){
		List<PortGroupProfile> existList = QueryUtil.executeQuery(PortGroupProfile.class, null,null);
		for(PortGroupProfile portGroup:existList){
			List<SingleTableItem> items=portGroup.getItems();
			for(SingleTableItem item:items){
				Long oldConfigId=item.getConfigTemplateId();
				Long newConfigId=AhRestoreNewMapTools.getMapConfigTemplate(oldConfigId);
				if(newConfigId!=null){
					item.setConfigTemplateId(newConfigId);
				}
			}
			try {
				QueryUtil.updateBo(portGroup);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("update ConfigTemplate in portGroup error");
				AhRestoreDBTools.logRestoreMsg(e.getMessage());
			}
		}

	}

	private static void resetConfigTemplateVlanNetworkMaping(List<ConfigTemplate> cft){
		// need restore the vlan and network mapping from lan profile
		for(ConfigTemplate cf: cft) {
			if (!cf.getConfigType().isTypeSupport(ConfigTemplateType.ROUTER)) {
				continue;
			}
			Set<ConfigTemplateVlanNetwork> cvnSet = new HashSet<ConfigTemplateVlanNetwork>();
			if (AhRestoreNewMapTools.getMapWlanNetworkObjectVlan(cf.getId())!=null) {
				cvnSet.add(AhRestoreNewMapTools.getMapWlanNetworkObjectVlan(cf.getId()));
			}
			Set<Long> upIdSet = new HashSet<Long>();
			for(ConfigTemplateSsid cs: cf.getSsidInterfaces().values()){
				if (cs.getSsidProfile()!=null) {
					SsidProfile ss = QueryUtil.findBoById(SsidProfile.class, cs.getSsidProfile().getId(), new ImplQueryBoVlanNetwork());
					if (ss.getUserProfileDefault()!=null) {
						upIdSet.add(ss.getUserProfileDefault().getId());
					}
					if (ss.getUserProfileSelfReg()!=null) {
						upIdSet.add(ss.getUserProfileSelfReg().getId());
					}
					if (ss.getRadiusUserProfile()!=null) {
						for(UserProfile up: ss.getRadiusUserProfile()){
							upIdSet.add(up.getId());
						}
					}
				}
			}
			for (PortGroupProfile portGroup : cf.getPortProfiles()) {
			    List<PortBasicProfile> basicProfiles = portGroup.getBasicProfiles();
			    if(null != basicProfiles) {
			        for (PortBasicProfile portBasicProfile : basicProfiles) {
			            if(null != portBasicProfile.getAccessProfile()) {
			                PortAccessProfile access = QueryUtil.findBoById(PortAccessProfile.class, portBasicProfile.getAccessProfile().getId(), new ImplQueryBoVlanNetwork());
                            if(access.getPortType() == PortAccessProfile.PORT_TYPE_8021Q) {
                                // handle the trunk mode - Network <-> VLAN
                                if(null != access.getNativeVlan()) {
                                    Long lanId = AhRestoreNewMapTools.getMapLanProfileReverse(access.getId());
                                    if(null != lanId) {
                                        Map<Long, Long> vlanObjNetwork = AhRestoreNewMapTools.getMapLANVLANObjectNetwork(lanId);
                                        if(null != vlanObjNetwork) {
                                            for (Long vlanId : vlanObjNetwork.keySet()) {
                                                Long networkId = vlanObjNetwork.get(vlanId);

                                                if(null != networkId && null != vlanId) {
                                                    VpnNetwork networkObj = AhRestoreNewTools.CreateBoWithId(VpnNetwork.class,networkId);
                                                    Vlan vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,vlanId);

                                                    if(networkObj!=null && vlanObj!=null) {
                                                        ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
                                                        cvn.setNetworkObj(networkObj);
                                                        cvn.setVlan(vlanObj);
                                                        cvnSet.add(cvn);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // handle the access mode
                                if(null != access.getDefUserProfile()) {
                                    upIdSet.add(access.getDefUserProfile().getId());
                                }
                                if(null != access.getSelfRegUserProfile()) {
                                    upIdSet.add(access.getSelfRegUserProfile().getId());
                                }
                                if (null != access.getAuthOkUserProfile()) {
                                    for (UserProfile up : access.getAuthOkUserProfile()) {
                                        upIdSet.add(up.getId());
                                    }
                                }
                            }
			            }
			        }
			    }
            }

			for(Long upId : upIdSet){
				Long networkId = AhRestoreNewMapTools.getMapUserProfileNetworkObjectWithNewID(upId);
				Long vlanId = null;
				if (networkId!=null) {
					vlanId = AhRestoreNewMapTools.getMapNetworkObjectVlanWithNewID(networkId);
				}
				VpnNetwork networkObj = AhRestoreNewTools.CreateBoWithId(VpnNetwork.class,networkId);
				Vlan vlanObj = null;
				if(vlanId!=null) {
					vlanObj = AhRestoreNewTools.CreateBoWithId(Vlan.class,vlanId);
				}
				if(networkObj!=null && vlanObj!=null) {
					ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
					cvn.setNetworkObj(networkObj);
					cvn.setVlan(vlanObj);
					cvnSet.add(cvn);
				}
			}

			if (!cvnSet.isEmpty()) {
				if (cf.getVlanNetwork()==null) {
					List<ConfigTemplateVlanNetwork> myList = new ArrayList<ConfigTemplateVlanNetwork>();
					cf.setVlanNetwork(myList);
				} else {
					cf.getVlanNetwork().clear();
				}
				cf.getVlanNetwork().addAll(cvnSet);
			}
		}
	}

	private static void insertUpdateLog(){
		List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
		for(Map<String,String> issueMap:issueMapList){
			String issueId = issueMap.get("ISSUEID");
			String sameSsidName = issueMap.get("SAMESSIDNAME");
			String newCwp = issueMap.get("NEWCWPNAME");
			Long wlanPolicyId = Long.parseLong(issueMap.get("WLANPOLICYID"));
			ConfigTemplate tmp1 = QueryUtil.findBoById(ConfigTemplate.class, wlanPolicyId);
			String wlanPolicy;
			if (tmp1==null) {
				wlanPolicy = BeParaModule.DEFAULT_DEVICE_GROUP_NAME;
			} else {
				wlanPolicy = tmp1.getConfigName();
			}

			Long wlanPolicyId2 = Long.parseLong(issueMap.get("WLANPOLICYID2"));
			ConfigTemplate tmp2 = QueryUtil.findBoById(ConfigTemplate.class, wlanPolicyId2);
			String wlanPolicy2;
			if (tmp2==null) {
				wlanPolicy2 = BeParaModule.DEFAULT_DEVICE_GROUP_NAME;
			} else {
				wlanPolicy2 = tmp2.getConfigName();
			}
			String newSsidName = issueMap.get("NEWSSIDNAME");

			String from1 = "Different traffic filters were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\".";
			String from2 = "Different radio were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\".";
			String from3 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, radio.";
			String from4 = "Different AAA server configurations were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\".";
			String from5 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, AAA server configurations.";
			String from6 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : radio, AAA server configurations.";
			String from7 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, radio, AAA server configurations.";
			String from8 = "Different user profiles or user profile roles were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\".";
			String from9  = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, user profiles or user profile roles.";
			String from10 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : radio, user profiles or user profile roles.";
			String from11 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, radio, user profiles or user profile roles.";
			String from12 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : AAA server configurations, user profiles or user profile roles.";
			String from13 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, AAA server configurations, user profiles or user profile roles.";
			String from14 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : radio, AAA server configurations, user profiles or user profile roles.";
			String from15 = "Different configuration objects of the following types were applied to the same SSID \""+ sameSsidName + "\" in multiple network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\" : traffic filters, radio, AAA server configurations, user profiles or user profile roles.";
			String from16 = "The SSID \"" + sameSsidName + "\" has association with different user profiles and/or user profile types in network policies \"" + wlanPolicy + ", " + wlanPolicy2 + "\".";

			String systemName = NmsUtil.getOEMCustomer().getNmsName();
			String after1 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter to new SSID profile \"" + newSsidName + "\".";
			String after2 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different radio to new SSID profile \"" + newSsidName + "\".";
			String after3 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, radio to new SSID profile \"" + newSsidName + "\".";
			String after4 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different AAA server configuration to new SSID profile \"" + newSsidName + "\".";
			String after5 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, AAA server configuration to new SSID profile \"" + newSsidName + "\".";
			String after6 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different radio, AAA server configuration to new SSID profile \"" + newSsidName + "\".";
			String after7 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, radio, AAA server configuration to new SSID profile \"" + newSsidName + "\".";
			String after8 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after9 = systemName + " generated a new SSID profile for the SSID in WLAN policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after10 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different radio, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after11 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, radio, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after12 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different AAA server configuration, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after13 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, AAA server configuration, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after14 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different radio, AAA server configuration, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after15 = systemName + " generated a new SSID profile for the SSID in network policy \"" + wlanPolicy2 + "\" and applied a different traffic filter, radio, AAA server configuration, user profile or user profile role to new SSID profile \"" + newSsidName + "\".";
			String after16 = "A new SSID profile \"" + newSsidName + "\" with the original SSID and a new Captive Web Portal \"" + newCwp + "\" are generated in network policy \"" + wlanPolicy2 + "\".";

			String action1 = "Check if you need to use different traffic filter. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action2 = "Check if you need to use different radio. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action3 = "Check if you need to use different traffic filter, radio. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action4 = "Check if you need to use different AAA server configuration. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action5 = "Check if you need to use different traffic filter, AAA server configuration. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action6 = "Check if you need to use different radio, AAA server configuration. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action7 = "Check if you need to use different traffic filter, radio, AAA server configuration. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action8 = "Check if you need to use different user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action9 = "Check if you need to use different traffic filter, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action10 = "Check if you need to use different radio, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action11 = "Check if you need to use different traffic filter, radio, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action12 = "Check if you need to use different AAA server configuration, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action13 = "Check if you need to use different traffic filter, AAA server configuration, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action14 = "Check if you need to use different radio, AAA server configuration, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action15 = "Check if you need to use different traffic filter, radio, AAA server configuration, user profiles or user profile roles. If you do, use all the newly generated SSID profiles. If not, use the ones you need and delete the others.";
			String action16 = "Please verify the user profile and Captive Web Portal in the SSID profile \"" + newSsidName + "\". Remove the unused SSID profile and Captive Web Portal.";

			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			upgradeLog.setOwner(AhRestoreNewMapTools.getonlyDomain());
			upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),AhRestoreNewMapTools.getonlyDomain().getTimeZoneString()));
			upgradeLog.setAnnotation("Click to add an annotation");
			switch (Integer.valueOf(issueId)) {
				case 1: upgradeLog.setFormerContent(from1);
						upgradeLog.setPostContent(after1);
						upgradeLog.setRecommendAction(action1);
						break;
				case 2: upgradeLog.setFormerContent(from2);
						upgradeLog.setPostContent(after2);
						upgradeLog.setRecommendAction(action2);
						break;
				case 3: upgradeLog.setFormerContent(from3);
						upgradeLog.setPostContent(after3);
						upgradeLog.setRecommendAction(action3);
						break;
				case 4: upgradeLog.setFormerContent(from4);
						upgradeLog.setPostContent(after4);
						upgradeLog.setRecommendAction(action4);
						break;
				case 5: upgradeLog.setFormerContent(from5);
						upgradeLog.setPostContent(after5);
						upgradeLog.setRecommendAction(action5);
						break;
				case 6: upgradeLog.setFormerContent(from6);
						upgradeLog.setPostContent(after6);
						upgradeLog.setRecommendAction(action6);
						break;
				case 7: upgradeLog.setFormerContent(from7);
						upgradeLog.setPostContent(after7);
						upgradeLog.setRecommendAction(action7);
						break;
				case 8: upgradeLog.setFormerContent(from8);
						upgradeLog.setPostContent(after8);
						upgradeLog.setRecommendAction(action8);
						break;
				case 9: upgradeLog.setFormerContent(from9);
						upgradeLog.setPostContent(after9);
						upgradeLog.setRecommendAction(action9);
						break;
				case 10: upgradeLog.setFormerContent(from10);
						upgradeLog.setPostContent(after10);
						upgradeLog.setRecommendAction(action10);
						break;
				case 11: upgradeLog.setFormerContent(from11);
						upgradeLog.setPostContent(after11);
						upgradeLog.setRecommendAction(action11);
						break;
				case 12: upgradeLog.setFormerContent(from12);
						upgradeLog.setPostContent(after12);
						upgradeLog.setRecommendAction(action12);
						break;
				case 13: upgradeLog.setFormerContent(from13);
						upgradeLog.setPostContent(after13);
						upgradeLog.setRecommendAction(action13);
						break;
				case 14: upgradeLog.setFormerContent(from14);
						upgradeLog.setPostContent(after14);
						upgradeLog.setRecommendAction(action14);
						break;
				case 15: upgradeLog.setFormerContent(from15);
						upgradeLog.setPostContent(after15);
						upgradeLog.setRecommendAction(action15);
						break;
				default: upgradeLog.setFormerContent(from16);
						upgradeLog.setPostContent(after16);
						upgradeLog.setRecommendAction(action16);
						break;

			}
			lstLogBo.add(upgradeLog);
		}
		try {
			QueryUtil.bulkCreateBos(lstLogBo);
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("insert upgrade log error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}


	/**
	 * Add Static Identity-Based Tunnel Policy and update the user profile which has relationship with
	 * when upgrade from 3.1 or 3.2b1,2,3 to 3.2r1
	 *
	 * @param hmDom -
	 * @throws Exception -
	 */
	private static void updateTunnelPolicyAndUserProfile(HmDomain hmDom) throws Exception {
		Map<String, Set<String>> fromTotoTunnels = new HashMap<String, Set<String>>();
		Map<String, Set<String>> toToFromTunnels = new HashMap<String, Set<String>>();
		Set<String> differentFroms = new HashSet<String>();
		Map<String, Set<String>> fromTotoSuccess = new HashMap<String, Set<String>>();
		if (null != allTunnelPolicy && allTunnelPolicy.size() > 0) {
			Map<String, List<TunnelSettingIPAddress>> allIpOldInfor = getAllTunnelSettingIPAddressOld();
			Map<String, TunnelSetting> needUpdateTunnels = new HashMap<String, TunnelSetting>();
			for (Map<String, Set<String>> tunnelList : allTunnelPolicy.values()) {
				Set<String> fromTunnelIds;
				Set<String> toTunnelIds;
				for (Set<String> tunnelIds : tunnelList.values()) {
					TunnelSetting singleTunnel;
					fromTunnelIds = new HashSet<String>();
					toTunnelIds = new HashSet<String>();
					for (String tunnelId : tunnelIds) {
						if (null != tunnelId && !fromTunnelIds.contains(tunnelId) && !toTunnelIds.contains(tunnelId)) {
							singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(tunnelId);
							if (null != singleTunnel) {
								if (singleTunnel.getEnableType() == TunnelSetting.TUNNELSETTING_TUNNELING_TO) {
									toTunnelIds.add(tunnelId);
								}
								if (singleTunnel.getEnableType() == TunnelSetting.TUNNELSETTING_TUNNELING_FROM) {
									fromTunnelIds.add(tunnelId);
								}
							}
						}
					}
					if (fromTunnelIds.size() > 0 && toTunnelIds.size() > 0) {
						Set<String> fromToToes;
						for (String from : fromTunnelIds) {
							fromToToes = fromTotoTunnels.get(from);
							if (null == fromToToes) {
								fromTotoTunnels.put(from, toTunnelIds);
							} else {
								fromToToes.addAll(toTunnelIds);
							}
						}
						Set<String> toToFromes;
						for (String to : toTunnelIds) {
							toToFromes = toToFromTunnels.get(to);
							if (null == toToFromes) {
								toToFromTunnels.put(to, fromTunnelIds);
							} else {
								toToFromes.addAll(fromTunnelIds);
							}
						}
					}
				}
			}
			if (fromTotoTunnels.size() > 0) {
				List<TunnelSettingIPAddress> ipList;
				TunnelSetting singleTunnel;
				TunnelSetting fromObj;
				for (String fromId : fromTotoTunnels.keySet()) {
					if (null != allIpOldInfor) {
						singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(fromId);
						fromObj = new TunnelSetting();
						fromObj.setTunnelName(singleTunnel.getTunnelName());
						fromObj.setDescription(singleTunnel.getDescription());
						fromObj.setEnableType(singleTunnel.getEnableType());
						fromObj.setOwner(singleTunnel.getOwner());
						ipList = allIpOldInfor.get(fromId);
						if (null != ipList) {
							Set<IpAddress> fromIpList = new HashSet<IpAddress>();
							for (TunnelSettingIPAddress oneIp : ipList) {
								fromIpList.add(oneIp.getIpAddress());
							}
							TunnelSetting toObj;
							for (String toId : fromTotoTunnels.get(fromId)) {
								singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(toId);
								toObj = new TunnelSetting();
								toObj.setTunnelName(singleTunnel.getTunnelName());
								toObj.setDescription(singleTunnel.getDescription());
								toObj.setEnableType(singleTunnel.getEnableType());
								toObj.setOwner(singleTunnel.getOwner());
								toObj.setTunnelToType(singleTunnel.getTunnelToType());
								toObj.setIpAddress(singleTunnel.getIpAddress());
								toObj.setIpRangeStart(singleTunnel.getIpRangeStart());
								toObj.setIpRangeEnd(singleTunnel.getIpRangeEnd());
								toObj.setPassword(singleTunnel.getPassword());
								for (TunnelSettingIPAddress singleIp : ipList) {
									boolean bool = false;
									if (singleIp.getPassword().equals(toObj.getPassword())) {
										if (null != needUpdateTunnels.get(toId)) {
											toObj = needUpdateTunnels.get(toId);
										}
										Set<IpAddress> ipAddressList = toObj.getIpAddressList();
										if (null == ipAddressList) {
											ipAddressList = new HashSet<IpAddress>();
										}
										if (null != needUpdateTunnels.get(fromId)) {
											fromObj = needUpdateTunnels.get(fromId);
										}
										if (TunnelSetting.TUNNELSETTING_STATIC_TUNNELING == fromObj.getEnableType()) {
											ipAddressList.add(singleIp.getIpAddress());
										} else {
											fromObj.setEnableType(TunnelSetting.TUNNELSETTING_STATIC_TUNNELING);
											fromObj.setIpAddress(toObj.getIpAddress());
											fromObj.setIpRangeStart(toObj.getIpRangeStart());
											fromObj.setIpRangeEnd(toObj.getIpRangeEnd());
											fromObj.setPassword(toObj.getPassword());
											fromObj.setTunnelToType(toObj.getTunnelToType());
											ipAddressList.addAll(fromIpList);
											fromObj.setIpAddressList(fromIpList);
											bool = true;
										}
										toObj.setIpAddressList(ipAddressList);
										if (null == needUpdateTunnels.get(toId)) {
											needUpdateTunnels.put(toId, toObj);
										}
										if (null == needUpdateTunnels.get(fromId)) {
											needUpdateTunnels.put(fromId, fromObj);
										}
										Set<String> toes = fromTotoSuccess.get(fromId + " " + fromObj.getTunnelName());
										if (null == toes) {
											toes = new HashSet<String>();
											toes.add(toId + " " + toObj.getTunnelName());
											fromTotoSuccess.put(fromId + " " + fromObj.getTunnelName(), toes);
										} else {
											toes.add(toId + " " + toObj.getTunnelName());
										}
										if (bool) {
											break;
										}
									}
								}
							}
						}
					}
				}
				for (String fromId : fromTotoTunnels.keySet()) {
					ipList = allIpOldInfor.get(fromId);
					Set<IpAddress> ipListTun = new HashSet<IpAddress>();
					for (TunnelSettingIPAddress oneIp : ipList) {
						ipListTun.add(oneIp.getIpAddress());
					}
					TunnelSetting toObj = new TunnelSetting();
					for (String toId : fromTotoTunnels.get(fromId)) {
						toObj = needUpdateTunnels.get(toId);
						if (null == toObj) {
							singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(toId);
							toObj = new TunnelSetting();
							toObj.setTunnelName(singleTunnel.getTunnelName());
							toObj.setDescription(singleTunnel.getDescription());
							toObj.setEnableType(singleTunnel.getEnableType());
							toObj.setOwner(singleTunnel.getOwner());
							toObj.setTunnelToType(singleTunnel.getTunnelToType());
							toObj.setIpAddress(singleTunnel.getIpAddress());
							toObj.setIpRangeStart(singleTunnel.getIpRangeStart());
							toObj.setIpRangeEnd(singleTunnel.getIpRangeEnd());
							toObj.setPassword(singleTunnel.getPassword());
							toObj.setIpAddressList(ipListTun);
							needUpdateTunnels.put(toId, toObj);
						}
					}
					if (null == needUpdateTunnels.get(fromId)) {
						singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(fromId);
						fromObj = new TunnelSetting();
						fromObj.setTunnelName(singleTunnel.getTunnelName());
						fromObj.setDescription(singleTunnel.getDescription());
						fromObj.setOwner(singleTunnel.getOwner());
						fromObj.setEnableType(TunnelSetting.TUNNELSETTING_STATIC_TUNNELING);
						fromObj.setIpAddress(toObj.getIpAddress());
						fromObj.setIpRangeStart(toObj.getIpRangeStart());
						fromObj.setIpRangeEnd(toObj.getIpRangeEnd());
						fromObj.setPassword(toObj.getPassword());
						fromObj.setTunnelToType(toObj.getTunnelToType());
						fromObj.setIpAddressList(ipListTun);
						needUpdateTunnels.put(fromId, fromObj);
						toObj.getIpAddressList().addAll(ipListTun);
						differentFroms.add(fromId + " " + fromObj.getTunnelName());
					}
				}
				if (needUpdateTunnels.size() > 0) {
					QueryUtil.bulkCreateBos(needUpdateTunnels.values());
					String[] tunnelResult = AhRestoreNewMapTools.getTunnelSettingResult(needUpdateTunnels.keySet());
					if (null != tunnelResult) {
						if (!"".equals(tunnelResult[0])) {
							AhRestoreDBTools.logRestoreMsg("The Static Identity-Based Tunnel Policy : \"" + tunnelResult[0] + "\" changed after restore.");
						}
						if (!"".equals(tunnelResult[1])) {
							AhRestoreDBTools.logRestoreMsg("The Static Identity-Based Tunnel Policy : \"" + tunnelResult[1] + "\" removed after restore.");
						}

					}
					Set<String> allUserPros;
					List<UserProfile> updateUserPros = new ArrayList<UserProfile>();
					TunnelSetting tunnelSet = null;
					for (String tunnel : needUpdateTunnels.keySet()) {
						allUserPros = AhRestoreNewMapTools.getUserProfileNamesByTunnel(tunnel);
						if (null != allUserPros) {
							Long newId = AhRestoreNewMapTools.getMapIdentityBasedTunnel(Long.valueOf(tunnel));
							if (null != newId) {
								tunnelSet = AhRestoreNewTools.CreateBoWithId(TunnelSetting.class, newId);
							}
							UserProfile userP;
							for (String userName : allUserPros) {
								userP = QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName", userName,
									hmDom.getId());
								userP.setTunnelSetting(tunnelSet);
								updateUserPros.add(userP);
							}
						}
					}
					if (updateUserPros.size() > 0) {
						QueryUtil.bulkUpdateBos(updateUserPros);
					}
				} else {
					AhRestoreDBTools.logRestoreMsg("No Static Identity-Based Tunnel Policy restored");
				}
			} else {
				AhRestoreDBTools.logRestoreMsg("No Static Identity-Based Tunnel Policy restored");
			}
		} else {
			AhRestoreDBTools.logRestoreMsg("No Static Identity-Based Tunnel Policy restored");
		}
		// insert log to database
		Map<String, String> toNotUsed = null;
		Map<String, String> fromNotUsed = null;
		if (fromTotoTunnels.size() > 0 && toToFromTunnels.size() > 0) {
			toNotUsed = getTunnelSettingSingleToOrFrom(fromTotoTunnels.keySet(), toToFromTunnels.keySet(), TunnelSetting.TUNNELSETTING_TUNNELING_TO);
			fromNotUsed = getTunnelSettingSingleToOrFrom(fromTotoTunnels.keySet(), toToFromTunnels.keySet(), TunnelSetting.TUNNELSETTING_TUNNELING_FROM);
		}
		createUpgradeLogForTunnelPolicy(toNotUsed, fromNotUsed, fromTotoSuccess, differentFroms, fromTotoTunnels, hmDom);
	}

	private static Map<String, String> getTunnelSettingSingleToOrFrom(Set<String> strFromKeys, Set<String> strToKeys, int tunnelType)
	{
		Map<String, String> result = new HashMap<String, String>();
		if (null == strFromKeys || null == strToKeys ) {
			return null;
		}
		TunnelSetting singleTunnel;
		for (String tunnelId : allTunnelInformation.keySet()) {
			if (!strFromKeys.contains(tunnelId) && !strToKeys.contains(tunnelId)) {
				singleTunnel = AhRestoreNewMapTools.getTunnelSettingOldInfo(tunnelId);
				if (tunnelType == singleTunnel.getEnableType()) {
					result.put(tunnelId, singleTunnel.getTunnelName());
				}
			}
		}
		return result;
	}

	private static void createUpgradeLogForTunnelPolicy(Map<String, String> toTunnels, Map<String, String> fromTunnels,
		Map<String, Set<String>> fromTotoSuccess, Set<String> diffTunnels, Map<String, Set<String>> fromTotoTunnels,
		HmDomain hmDom) {
		HmUpgradeLog upgradeLog;
		List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();

		/*
		 * the tunnels which were not used
		 */
		Set<String> noUsedTunnels = AhRestoreNewMapTools.getTunnelSettingNoUsed(allTunnelInformation.keySet());
		String post1 = "The tunnel policy was deleted.";
		String action1 = "If you need the tunnel policy, manually create a new one.";
		if (null != noUsedTunnels && noUsedTunnels.size() > 0) {
			String former1;
			for (String tunnelName : noUsedTunnels) {
				former1 = "Tunnel policy \"" + tunnelName + "\" was not used in any network policy.";
				upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent(former1);
				upgradeLog.setPostContent(post1);
				upgradeLog.setRecommendAction(action1);
				lstLogBo.add(upgradeLog);
			}
		}

		/*
		 * To tunnels have no source tunnel policy
		 */
		if (null != toTunnels && toTunnels.size() > 0) {
			StringBuffer former2;
			String subFormer;
			for (String tunnelId : toTunnels.keySet()) {
				former2 = new StringBuffer();
				former2.append("Tunnel policy \"").append(toTunnels.get(tunnelId)).append("\"");

				/*
				 * the length of FormerContent is 1024
				 */
				subFormer = getHiveAttributeAndWlanByTunnelId(tunnelId);
				if (subFormer.length() > 900) {
					subFormer = subFormer.substring(0, 900);
				}
				former2.append(subFormer);
				former2.append(" however, has no source tunnel policy information.");
				upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent(former2.toString());
				upgradeLog.setPostContent(post1);
				upgradeLog.setRecommendAction(action1);
				lstLogBo.add(upgradeLog);
			}
		}


		/*
		 * From tunnels have no destination tunnel policy
		 */
		if (null != fromTunnels && fromTunnels.size() > 0) {
			StringBuffer former3;
			String subFormer;
			for (String tunnelId : fromTunnels.keySet()) {
				former3 = new StringBuffer();
				former3.append("Tunnel policy \"").append(fromTunnels.get(tunnelId)).append("\"");

				/*
				 * the length of FormerContent is 1024
				 */
				subFormer = getHiveAttributeAndWlanByTunnelId(tunnelId);
				if (subFormer.length() > 900) {
					subFormer = subFormer.substring(0, 900);
				}
				former3.append(subFormer);
				former3.append(" however, has no destination tunnel policy information.");
				upgradeLog = new HmUpgradeLog();
				upgradeLog.setFormerContent(former3.toString());
				upgradeLog.setPostContent(post1);
				upgradeLog.setRecommendAction(action1);
				lstLogBo.add(upgradeLog);
			}
		}

		/*
		 * From tunnel and to tunnel have the same password
		 */
		if (null != fromTotoSuccess && fromTotoSuccess.size() > 0) {
			String fromId;
			String fromName;
			Set<String> toes;
			StringBuffer former4;
			for (String fromKey : fromTotoSuccess.keySet()) {
				toes = fromTotoSuccess.get(fromKey);
				fromId = fromKey.split(" ")[0];
				fromName = fromKey.split(" ")[1];
				String toId;
				String toName;
				String subFormer;
				for (String toKey : toes) {
					toId = toKey.split(" ")[0];
					toName = toKey.split(" ")[1];
					former4 = new StringBuffer();
					former4.append("Tunnel policies \"").append(fromName).append(", ").append(toName).append("\"");

					/*
					 * the length of FormerContent is 1024
					 */
					subFormer = getHiveAttributeAndWlan(fromId, toId);
					if (subFormer.length() > 900) {
						subFormer = subFormer.substring(0, 900);
					}
					former4.append(subFormer);
					former4.append(" and had matching passwords.");
					upgradeLog = new HmUpgradeLog();
					upgradeLog.setFormerContent(former4.toString());
					upgradeLog.setPostContent(NmsUtil.getOEMCustomer().getNmsName() +
							" combined the source and destination settings in both tunnel policies.");
					upgradeLog.setRecommendAction("If the only difference between the two network policies was the source and destination tunnel " +
							"policies, it is no longer necessary to retain two tunnel policies and two network policies. Apply the same tunnel " +
							"policy and network policy to the "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s at both ends of the tunnel and remove the redundant policies.");
					lstLogBo.add(upgradeLog);
				}
			}
		}

		/*
		 * From tunnel and to tunnel have the different password
		 */
		if (null != diffTunnels && diffTunnels.size() > 0) {
			String fromId;
			String fromName;
			StringBuffer former5;
			for (String strKey : diffTunnels) {
				fromId = strKey.split(" ")[0];
				fromName = strKey.split(" ")[1];
				String subFormer;
				for (String toId : fromTotoTunnels.get(fromId)) {
					String toName = AhRestoreNewMapTools.getTunnelSettingOldInfo(toId).getTunnelName();
					former5 = new StringBuffer();
					former5.append("Tunnel policies \"").append(fromName).append(", ").append(toName).append("\"");

					/*
					 * the length of FormerContent is 1024
					 */
					subFormer = getHiveAttributeAndWlan(fromId, toId);
					if (subFormer.length() > 900) {
						subFormer = subFormer.substring(0, 900);
					}
					former5.append(subFormer);
					former5.append(" however, their passwords were different.");
					upgradeLog = new HmUpgradeLog();
					upgradeLog.setFormerContent(former5.toString());
					upgradeLog.setPostContent(NmsUtil.getOEMCustomer().getNmsName() +
							" combined the source and destination settings in both tunnel policies and chose one password to use.");
					upgradeLog.setRecommendAction("If the only difference between the two network policies was the source and destination tunnel " +
							"policies, it is no longer necessary to retain two tunnel policies and two network policies. Apply the same tunnel " +
							"policy and network policy to the "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s at both ends of the tunnel and remove the redundant policies.");
					lstLogBo.add(upgradeLog);
				}
			}
		}

		/*
		 * insert the data to database
		 */
		if (lstLogBo.size() > 0) {
			for (HmUpgradeLog log : lstLogBo) {
				log.setOwner(hmDom);
				log.setLogTime(new HmTimeStamp(System.currentTimeMillis(),hmDom.getTimeZoneString()));
				log.setAnnotation("Click to add an annotation");
			}
			try {
				QueryUtil.bulkCreateBos(lstLogBo);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("insert tunnel policy upgrade log error");
				AhRestoreDBTools.logRestoreMsg(e.getMessage());
			}
		}
	}

	private static String getHiveAttributeAndWlanByTunnelId(String tunnelId) {
		Map<String, Map<String, Set<String>>> tunnels = allTunnelInformation.get(tunnelId);
		StringBuilder restult = new StringBuilder();
		for (String hiveName : tunnels.keySet()) {
			Map<String, Set<String>> hives = tunnels.get(hiveName);
			for (String attibute : hives.keySet()) {
				Set<String> wlans = hives.get(attibute);
				StringBuilder wlan = new StringBuilder();
				for (String wlanName : wlans) {
					wlan.append(wlanName).append(", ");
				}
				restult.append(" in network policy \"").append(wlan.toString().substring(0, wlan.toString().length() - 2)).append("\" belonged to the ").append(NmsUtil.getOEMCustomer().getWirelessUnitName()).append(" \"").append(hiveName).append("\", and referenced user profiles with the attribute ").append(attibute).append(";");
			}
		}
		return restult.toString();
	}

	private static String getHiveAttributeAndWlan(String tunnelId1, String tunnelId2) {
		Map<String, Map<String, Set<String>>> tunnel1s = allTunnelInformation.get(tunnelId1);
		Map<String, Map<String, Set<String>>> tunnel2s = allTunnelInformation.get(tunnelId2);
		StringBuilder restult = new StringBuilder();
		for (String hiveName : tunnel1s.keySet()) {
			Map<String, Set<String>> hive1s = tunnel1s.get(hiveName);
			Map<String, Set<String>> hive2s = tunnel2s.get(hiveName);
			if (null == hive2s) {
				continue;
			}
			for (String attibute : hive1s.keySet()) {
				Set<String> wlan1s = hive1s.get(attibute);
				Set<String> wlan2s = hive2s.get(attibute);
				if (null == wlan2s) {
					continue;
				}
				StringBuilder wlan = new StringBuilder();
				wlan1s.addAll(wlan2s);
				for (String wlanName : wlan1s) {
					wlan.append(wlanName).append(", ");
				}
				restult.append(" in network policy \"").append(wlan.toString().substring(0, wlan.toString().length() - 2)).append("\" belonged to the same ").append(NmsUtil.getOEMCustomer().getWirelessUnitName()).append(" \"").append(hiveName).append("\", and referenced user profiles with the same attribute ").append(attibute).append(";");
			}
		}
		return restult.toString();
	}

	private static void updateCwpProfile(List<ConfigTemplate> allConfigTemplate,Map<String, Map<String, ConfigTemplateSsidUserProfile>> allTemplateSsidUserProfile){
//		Map <Long,Boolean> cwpAuth = new HashMap<Long,Boolean>();
		Map <String,Boolean> updateSsidProfileBefore = new HashMap<String,Boolean>();
		Map <Long,String> updateCwpBefore = new HashMap<Long,String>();
		Map <Long,List<Cwp>> createCwpBefore = new HashMap<Long,List<Cwp>>();
		int cwpNameCount = 1;
		/*
		 * for re-generating web page
		 */
		CwpAction cwpAction = new CwpAction();
		cwpAction.setDomainName(AhRestoreNewMapTools.getonlyDomain().getDomainName());

		for(ConfigTemplate configTemplate :allConfigTemplate) {
			List<Long> removeKey = new ArrayList<Long>();
			List<ConfigTemplateSsid> addConfig = new ArrayList<ConfigTemplateSsid>();
			Map<String, ConfigTemplateSsidUserProfile> templateSsidUserProfile = allTemplateSsidUserProfile.get(configTemplate.getId().toString());
			for(ConfigTemplateSsid configTemplateSsid:configTemplate.getSsidInterfaces().values()){
				if (configTemplateSsid.getSsidProfile()!= null && configTemplateSsid.getSsidProfile().getCwp()!=null) {
					boolean auth = false;
					boolean reg=false;
					boolean needUpdate=false;
					for(ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()){
						if (configTemplateSsid.getSsidProfile().getId().equals(ssidUser.getSsidProfile().getId())){
							if(ssidUser.getUpType()==ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED){
//								cwpAuth.put(configTemplateSsid.getSsidProfile().getCwp().getId(), true);
								auth=true;
							}
							if(ssidUser.getUpType()==ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED){
//								cwpReg.put(configTemplateSsid.getSsidProfile().getCwp().getId(), true);
								reg=true;
							}
						}
					}
					Cwp tmpCwp = QueryUtil.findBoById(Cwp.class, configTemplateSsid.getSsidProfile().getCwp().getId(), new ImplQueryCwp());
					if(reg && auth) {
						if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
							tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_BOTH);
							needUpdate=true;
						}
					} else if(reg) {
						if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_REGISTERED){
							tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_REGISTERED);
							needUpdate=true;
						}
					} else if (auth) {
						if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_AUTHENTICATED){
							tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
							needUpdate=true;
						}
					}
//					if(reg && auth) {
//						if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
//							tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_BOTH);
//							needUpdate=true;
//						}
//					} else if(reg) {
//						if (cwpAuth.get(tmpCwp.getId())!=null) {
//							if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
//								tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_BOTH);
//								needUpdate=true;
//							}
//						} else {
//							if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_REGISTERED){
//								tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_REGISTERED);
//								needUpdate=true;
//							}
//						}
//					} else if (auth) {
//						if (cwpReg.get(tmpCwp.getId())!=null) {
//							if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
//								tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_BOTH);
//								needUpdate=true;
//							}
//						} else {
//							if (tmpCwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_AUTHENTICATED){
//								tmpCwp.setRegistrationType(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
//								needUpdate=true;
//							}
//						}
//					}
					if (needUpdate) {
						if (updateCwpBefore.get(tmpCwp.getId()) == null) {
							try {
								cwpAction.updatePageFields(tmpCwp);
								cwpAction.setCwpDataSource(tmpCwp);
								cwpAction.createCwpPage(tmpCwp, false);
								QueryUtil.updateBo(tmpCwp);
								updateCwpBefore.put(tmpCwp.getId(), configTemplate.getId().toString());
							} catch (Exception e) {
								AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate update cwp profile error! cwpname: " + tmpCwp.getCwpName() );
								e.printStackTrace();
							}
						} else {
							boolean needcreate = true;
							if (createCwpBefore.get(tmpCwp.getId())!=null && createCwpBefore.get(tmpCwp.getId()).size()>0) {
								for(Cwp newCwp :createCwpBefore.get(tmpCwp.getId())){
									if (newCwp.getRegistrationType() == tmpCwp.getRegistrationType()){
										needcreate = false;
										SsidProfile tmpSsidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId());
										try {
											tmpSsidProfile.setCwp(newCwp);
											if (updateSsidProfileBefore.get(tmpSsidProfile.getSsidName())==null) {
												QueryUtil.updateBo(tmpSsidProfile);
												updateSsidProfileBefore.put(tmpSsidProfile.getSsidName(), true);
											} else {
												String oldWlanPolicyId = updateCwpBefore.get(tmpCwp.getId());
												Map<String,String> issueMap = new HashMap<String,String>();
												issueMap.put("ISSUEID", "16");
												issueMap.put("NEWCWPNAME",newCwp.getCwpName());
												issueMap.put("WLANPOLICYID",oldWlanPolicyId);
												issueMap.put("WLANPOLICYID2",configTemplate.getId().toString());

												SsidProfile newSsidProfile = updateSsidProfileAndWlanPolicy(newCwp, configTemplateSsid,templateSsidUserProfile);
												updateSsidProfileBefore.put(newSsidProfile.getSsidName(), true);
												issueMap.put("SAMESSIDNAME", newSsidProfile.getSsid());
												issueMap.put("NEWSSIDNAME", newSsidProfile.getSsidName());
												issueMapList.add(issueMap);
												removeKey.add(configTemplateSsid.getSsidProfile().getId());
												ConfigTemplateSsid tmpconfig  = new ConfigTemplateSsid();
												tmpconfig.setSsidProfile(newSsidProfile);
												tmpconfig.setInterfaceName(newSsidProfile.getSsidName());

												tmpconfig.setNetworkServicesEnabled(configTemplateSsid.getNetworkServicesEnabled());
												tmpconfig.setMacOuisEnabled(configTemplateSsid.getMacOuisEnabled());
												tmpconfig.setSsidEnabled(configTemplateSsid.getSsidEnabled());
												tmpconfig.setCheckE(configTemplateSsid.getCheckE());
												tmpconfig.setCheckP(configTemplateSsid.getCheckP());
												tmpconfig.setCheckD(configTemplateSsid.getCheckD());
												tmpconfig.setCheckET(configTemplateSsid.getCheckET());
												tmpconfig.setCheckPT(configTemplateSsid.getCheckPT());
												tmpconfig.setCheckDT(configTemplateSsid.getCheckDT());

//												tmpconfig.setClassfierAndMarker(configTemplateSsid.getClassfierAndMarker());
												addConfig.add(tmpconfig);
											}
										} catch (Exception e) {
											AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate update Ssid profile error! ssidname: " + tmpSsidProfile.getSsidName() );
											e.printStackTrace();
										}
										break;
									}
								}
							}
							if (needcreate){
								boolean nameExist = true;
								String oldWlanPolicyId = updateCwpBefore.get(tmpCwp.getId());
								do {
									if (tmpCwp.getCwpName().length()>29) {
										tmpCwp.setCwpName(tmpCwp.getCwpName().substring(0, 29)+ "_" + cwpNameCount);
									} else {
										tmpCwp.setCwpName(tmpCwp.getCwpName()+ "_" + cwpNameCount);
									}
									cwpNameCount++;
									if(!checkCwpNameExists("cwpName",tmpCwp.getCwpName())){
										nameExist = false;
									}
								} while(nameExist);
								Long oldCwpId = tmpCwp.getId();
								tmpCwp.setId(null);
								tmpCwp.setOwner(AhRestoreNewMapTools.getonlyDomain());
								tmpCwp.setVersion(null);

								CwpPageCustomization pageCustomization = tmpCwp.getPageCustomization();
								Map<String, CwpPageField> fields = new LinkedHashMap<String, CwpPageField>();
								byte order = 1;

								for(String field : CwpPageField.FIELDS) {
									CwpPageField newField = pageCustomization.getPageField(field);
									if(newField == null) {
										newField = new CwpPageField();
										newField.setEnabled(true);

										if(field.equals("Phone") || field.equals("Comment") || field.equals(CwpPageField.REPRESENTING)) {
											newField.setRequired(false);
										} else {
											if(tmpCwp.isIdmSelfReg() &&  (field.equals(CwpPageField.VISITING))){
												newField.setRequired(false);
											}else{
												newField.setRequired(true);
											}
										}
										newField.setLabel(field);
										newField.setPlace(order++);
									}
									newField.setField(field);
									fields.put(field, newField);
								}
								pageCustomization.setFields(fields);

								try {
									cwpAction.updatePageFields(tmpCwp);
									cwpAction.setCwpDataSource(tmpCwp);
									cwpAction.createCwpPage(tmpCwp, false);
									QueryUtil.createBo(tmpCwp);

									if (createCwpBefore.get(oldCwpId) == null) {
										List<Cwp> lstCwp= new ArrayList<Cwp>();
										lstCwp.add(tmpCwp);
										createCwpBefore.put(oldCwpId, lstCwp);
									} else {
										createCwpBefore.get(oldCwpId).add(tmpCwp);
									}
									SsidProfile tmpSsidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId());
									if (updateSsidProfileBefore.get(tmpSsidProfile.getSsidName())==null) {
										tmpSsidProfile.setCwp(tmpCwp);
										QueryUtil.updateBo(tmpSsidProfile);
										updateSsidProfileBefore.put(tmpSsidProfile.getSsidName(), true);
									} else {
										Map<String,String> issueMap = new HashMap<String,String>();
										issueMap.put("ISSUEID", "16");
										issueMap.put("NEWCWPNAME",tmpCwp.getCwpName());
										issueMap.put("WLANPOLICYID",oldWlanPolicyId);
										issueMap.put("WLANPOLICYID2",configTemplate.getId().toString());

										SsidProfile newSsidProfile = updateSsidProfileAndWlanPolicy(tmpCwp, configTemplateSsid,templateSsidUserProfile);
										updateSsidProfileBefore.put(newSsidProfile.getSsidName(), true);
										issueMap.put("SAMESSIDNAME", newSsidProfile.getSsid());
										issueMap.put("NEWSSIDNAME", newSsidProfile.getSsidName());
										issueMapList.add(issueMap);
										removeKey.add(configTemplateSsid.getSsidProfile().getId());
										ConfigTemplateSsid tmpconfig  = new ConfigTemplateSsid();
										tmpconfig.setSsidProfile(newSsidProfile);
										tmpconfig.setInterfaceName(newSsidProfile.getSsidName());
										tmpconfig.setNetworkServicesEnabled(configTemplateSsid.getNetworkServicesEnabled());
										tmpconfig.setMacOuisEnabled(configTemplateSsid.getMacOuisEnabled());
										tmpconfig.setSsidEnabled(configTemplateSsid.getSsidEnabled());
										tmpconfig.setCheckE(configTemplateSsid.getCheckE());
										tmpconfig.setCheckP(configTemplateSsid.getCheckP());
										tmpconfig.setCheckD(configTemplateSsid.getCheckD());
										tmpconfig.setCheckET(configTemplateSsid.getCheckET());
										tmpconfig.setCheckPT(configTemplateSsid.getCheckPT());
										tmpconfig.setCheckDT(configTemplateSsid.getCheckDT());
//										tmpconfig.setClassfierAndMarker(configTemplateSsid.getClassfierAndMarker());
										addConfig.add(tmpconfig);
									}
								} catch (Exception e) {
									AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate create cwp profile error! cwpname: " + tmpCwp.getCwpName() );
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			if (removeKey.size()>0){
				for (Long key : removeKey) {
					configTemplate.getSsidInterfaces().remove(key);
				}
				for (ConfigTemplateSsid config : addConfig) {
					configTemplate.getSsidInterfaces().put(config.getSsidProfile().getId(), config);
				}
			}
		}
	}

	private static SsidProfile updateSsidProfileAndWlanPolicy(Cwp newCwp,ConfigTemplateSsid configTemplateSsid,Map<String, ConfigTemplateSsidUserProfile> templateSsidUserProfile){
		SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId(), new ImplQueryBo());
		Long oldSsidId = ssidProfile.getId();
		ssidProfile.setId(null);
		ssidProfile.setVersion(null);
		ssidProfile.setOwner(AhRestoreNewMapTools.getonlyDomain());
		ssidProfile.setDefaultFlag(false);
		boolean nameExist = true;
		do {
			if (ssidProfile.getSsid().length()>29) {
				ssidProfile.setSsidName(ssidProfile.getSsid().substring(0, 29)+ "_" + nameCount);
			} else {
				ssidProfile.setSsidName(ssidProfile.getSsid()+ "_" + nameCount);
			}
			nameCount++;
			if(!checkHiveNameExists("hiveName",ssidProfile.getSsidName())&&
					!checkSsidNameExists("ssidName",ssidProfile.getSsidName())){
				nameExist = false;
			}
		} while(nameExist);

		Set<Scheduler> cloneSchedulers = new HashSet<Scheduler>();
		for (Scheduler tempClass : ssidProfile.getSchedulers()) {
			cloneSchedulers.add(tempClass);
		}
		ssidProfile.setSchedulers(cloneSchedulers);

		Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
		for (MacFilter tempClass : ssidProfile.getMacFilters()) {
			cloneMacFilters.add(tempClass);
		}
		ssidProfile.setMacFilters(cloneMacFilters);

		Set<UserProfile> cloneRadiusUserProfiles = new HashSet<UserProfile>();
		for (UserProfile tempClass : ssidProfile.getRadiusUserProfile()) {
			cloneRadiusUserProfiles.add(tempClass);
		}
		ssidProfile.setRadiusUserProfile(cloneRadiusUserProfiles);

		Set<LocalUserGroup> cloneLocalUserGroups = new HashSet<LocalUserGroup>();
		for (LocalUserGroup tempClass : ssidProfile.getLocalUserGroups()) {
			cloneLocalUserGroups.add(tempClass);
		}
		ssidProfile.setLocalUserGroups(cloneLocalUserGroups);

		Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
		for (GRateType gType : TX11aOr11gRateSetting.GRateType.values()) {
			TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(gType);
			if (null == rateSet) {
				rateSet = new TX11aOr11gRateSetting();
				if (GRateType.one.equals(gType) || GRateType.two.equals(gType) || GRateType.five.equals(gType)
						|| GRateType.eleven.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
			rateSet.setGRateType(gType);
			gRateSet.put(rateSet.getkey(), rateSet);
		}
		ssidProfile.setGRateSets(gRateSet);

		Map<String, TX11aOr11gRateSetting> aRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
		for (ARateType aType : TX11aOr11gRateSetting.ARateType.values()) {
			TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(aType);
			if (null == rateSet) {
				rateSet = new TX11aOr11gRateSetting();
				if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
			rateSet.setARateType(aType);
			aRateSets.put(rateSet.getkey(), rateSet);
		}
		ssidProfile.setARateSets(aRateSets);

		Map<String, TX11aOr11gRateSetting> nRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
		for (NRateType nType : TX11aOr11gRateSetting.NRateType.values()) {
			TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(nType);
			if (null == rateSet) {
				rateSet = new TX11aOr11gRateSetting();
				rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
			}
			rateSet.setNRateType(nType);
			nRateSets.put(rateSet.getkey(), rateSet);
		}
		ssidProfile.setNRateSets(nRateSets);

		// set this ssid 11ac rate setting
		try {
			Map<String, List<Tx11acRateSettings>> acRateSets = getAllSsidAcRateSettings("ac_rate_settings_info");
			if (acRateSets == null
					|| acRateSets.get(configTemplateSsid.getSsidProfile()
							.getId()) == null) {
				List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
				for (short z = Tx11acRateSettings.STREAM_TYPE_SINGLE; z <= Tx11acRateSettings.STREAM_TYPE_THREE; z++) {
					Tx11acRateSettings acRateSet = new Tx11acRateSettings();
					acRateSet.setStreamType(z);
					acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
					acRateList.add(acRateSet);
				}
				ssidProfile.setAcRateSets(acRateList);
			} else {
				List<Tx11acRateSettings> acRateList = acRateSets
						.get(configTemplateSsid.getSsidProfile().getId());
				ssidProfile.setAcRateSets(acRateList);
			}
		} catch (AhRestoreColNotExistException e1) {
			AhRestoreDBTools
					.logRestoreMsg("restore ConfigTemplate create Ssid profile error! ssidname: "
							+ ssidProfile.getSsidName());
			e1.printStackTrace();
		} catch (AhRestoreException e1) {
			AhRestoreDBTools
					.logRestoreMsg("restore ConfigTemplate create Ssid profile error! ssidname: "
							+ ssidProfile.getSsidName());
			e1.printStackTrace();
		}

		ssidProfile.setCwp(newCwp);

		try {
			QueryUtil.createBo(ssidProfile);
			for(ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()){
				if (ssidUser.getSsidProfile().getId().equals(oldSsidId)){
					ssidUser.setSsidProfile(ssidProfile);
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate create Ssid profile error! ssidname: " + ssidProfile.getSsidName());
			e.printStackTrace();
			return null;
		}
		return ssidProfile;
	}

	private static void updateSsidUserProfile(List<ConfigTemplate> allConfigTemplate,Map<String, Map<String, ConfigTemplateSsidUserProfile>> allTemplateSsidUserProfile){
		Map<String,Long> updateSsidBefore = new HashMap<String,Long>();
		Map<String,SsidProfile> createSsidBefore = new HashMap<String,SsidProfile>();
		for (ConfigTemplate tempConfigTemplate : allConfigTemplate) {
			Map<String, ConfigTemplateSsidUserProfile> templateSsidUserProfile = allTemplateSsidUserProfile.get(tempConfigTemplate.getId().toString());
			Set<String> removeUserProfile = new HashSet<String>();
			Set<String> exitsUserProfile = new HashSet<String>();

			List<Long> removeKey = new ArrayList<Long>();
			List<ConfigTemplateSsid> addConfig = new ArrayList<ConfigTemplateSsid>();

			for (ConfigTemplateSsid configTemplateSsid : tempConfigTemplate.getSsidInterfaces().values()) {
				if (configTemplateSsid.getSsidProfile() == null) {
					continue;
				}
				SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId(), new ImplQueryBo());
				Set<UserProfile> radiusUserProfile = new HashSet<UserProfile>();

				if (ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
					ssidProfile.setCwp(null);
					ssidProfile.setUserProfileSelfReg(null);
					for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
						if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
							if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
								ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
								if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
								} else {
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
								}
							} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED) {
								if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
								} else {
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
								}
							} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED) {
								if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
								} else {
									removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
								}
							} else {
								radiusUserProfile.add(ssidUser.getUserProfile());
								if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
								} else {
									exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
								}
							}
						}
					}
				} else if (ssidProfile.getMacAuthEnabled()) {
					if (ssidProfile.getCwp() != null) {
						Cwp tmpCwp = QueryUtil.findBoById(Cwp.class, ssidProfile.getCwp().getId());
						if (tmpCwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED) {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED) {
										ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										radiusUserProfile.add(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						} else if (tmpCwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED) {
										ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED) {
										ssidProfile.setUserProfileSelfReg(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										radiusUserProfile.add(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						} else {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
										ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED) {
										ssidProfile.setUserProfileSelfReg(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										radiusUserProfile.add(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						}
					} else {
						for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
							if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
								if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
									ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
									if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
									} else {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
									}
								} else {
									radiusUserProfile.add(ssidUser.getUserProfile());
									if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
									} else {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
									}
								}
							}
						}
					}
				} else {
					if (ssidProfile.getCwp() != null) {
						Cwp tmpCwp = QueryUtil.findBoById(Cwp.class, ssidProfile.getCwp().getId());
						if (tmpCwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED) {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED) {
										ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										radiusUserProfile.add(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						} else if (tmpCwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED) {
										ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else if (ssidUser.getUpType() == ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED) {
										ssidProfile.setUserProfileSelfReg(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										radiusUserProfile.add(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						} else {
							for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
								if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
									if (ssidUser.getUpType() != ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_REGISTERED) {
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									} else {
										ssidProfile.setUserProfileSelfReg(ssidUser.getUserProfile());
										if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
										} else {
											exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
										}
									}
								}
							}
						}
					} else {
						for (ConfigTemplateSsidUserProfile ssidUser : templateSsidUserProfile.values()) {
							if (ssidUser.getSsidProfile().getId().equals(ssidProfile.getId())) {
								if (ssidUser.getUpType() != ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_DEFAULT) {
									if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
										removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
										removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
									} else {
										removeUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
									}
								} else {
									ssidProfile.setUserProfileDefault(ssidUser.getUserProfile());
									if (ssidProfile.getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_A);
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + SsidProfile.RADIOMODE_BG);
									} else {
										exitsUserProfile.add(ssidUser.getUserProfile().getId() + "|" + ssidProfile.getRadioMode());
									}
								}
							}
						}
					}
				}
				ssidProfile.setRadiusUserProfile(radiusUserProfile);
				try {
					boolean bool = false;
					for (String singleName : BeParaModule.SSID_PROFILE_NAMES) {
						if (singleName.equals(ssidProfile.getSsidName().trim())) {
							bool = true;
							break;
						}
					}
					UserProfile userSingle = QueryUtil.findBoByAttribute(UserProfile.class, "defaultFlag", true);

					if (bool && ssidProfile.getUserProfileSelfReg() == null
							&& (ssidProfile.getRadiusUserProfile() == null || ssidProfile.getRadiusUserProfile().size() < 1)
							&& ssidProfile.getUserProfileDefault().getId().equals(userSingle.getId())) {
						// do nothing
					} else if (updateSsidBefore.get(ssidProfile.getSsidName()) == null && !bool) {
						if (updateSsidBefore.get(ssidProfile.getSsid()) != null) {
							for (Map<String, String> issueMap : issueMapList) {
								String newSsidName = issueMap.get("NEWSSIDNAME");
								if (newSsidName.equals(ssidProfile.getSsidName())) {
									SsidProfile ssidProfileOld = QueryUtil.findBoByAttribute(SsidProfile.class, "ssidName", configTemplateSsid.getSsidProfile().getSsid(), AhRestoreNewMapTools.getonlyDomain().getId(), new ImplQueryBo());
									//SsidProfile ssidProfileOld = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId(),new ImplQueryBo());
									if (!compareTwoSsidSame(ssidProfileOld, ssidProfile, null)) {
										int issueId = Integer.parseInt(issueMap.get("ISSUEID"));
										issueId = issueId + 8;
										issueMap.put("ISSUEID", String.valueOf(issueId));
									}
								}
							}
						}
						if (ssidProfile.getSsid().equals(ssidProfile.getSsidName())) {
							for (Map<String, String> issueMap : issueMapList) {
								String sameSsidName = issueMap.get("SAMESSIDNAME");
								if (sameSsidName.equals(ssidProfile.getSsidName())) {
									String newSsidName = issueMap.get("NEWSSIDNAME");
									if (updateSsidBefore.get(newSsidName) == null) {
										continue;
									}
									SsidProfile ssidProfileOld = QueryUtil.findBoByAttribute(SsidProfile.class, "ssidName", newSsidName, AhRestoreNewMapTools.getonlyDomain().getId(), new ImplQueryBo());
									//SsidProfile ssidProfileOld = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId(),new ImplQueryBo());
									if (!compareTwoSsidSame(ssidProfileOld, ssidProfile, null)) {
										int issueId = Integer.parseInt(issueMap.get("ISSUEID"));
										if (issueId < 8) {
											issueId = issueId + 8;
											issueMap.put("ISSUEID", String.valueOf(issueId));
										}
									}
								}
							}
						}

						QueryUtil.updateBo(ssidProfile);
						updateSsidBefore.put(ssidProfile.getSsidName(), tempConfigTemplate.getId());
					} else if (updateSsidBefore.get(ssidProfile.getSsidName()) != null || bool) {
						SsidProfile ssidProfileOld = QueryUtil.findBoById(SsidProfile.class, configTemplateSsid.getSsidProfile().getId(), new ImplQueryBo());
						boolean shouldCreateNew = true;
						Map<String, String> issueMap = new HashMap<String, String>();
						if (compareTwoSsidSame(ssidProfileOld, ssidProfile, issueMap)) {
							shouldCreateNew = false;
						} else {
							for (int cnt = 1; cnt < nameCount; cnt++) {
								String ssidname_sub;
								if (ssidProfile.getSsid().length() > 29) {
									ssidname_sub = ssidProfile.getSsid().substring(0, 29) + "_" + cnt;
								} else {
									ssidname_sub = ssidProfile.getSsid() + "_" + cnt;
								}
								if (createSsidBefore.get(ssidname_sub) != null) {
									SsidProfile ssidProfileTmp = createSsidBefore.get(ssidname_sub);
									ssidProfileTmp = QueryUtil.findBoById(SsidProfile.class, ssidProfileTmp.getId(), new ImplQueryBo());
									if (compareTwoSsidSame(ssidProfileTmp, ssidProfile, null)) {
										shouldCreateNew = false;

										removeKey.add(configTemplateSsid.getSsidProfile().getId());
										ConfigTemplateSsid tmpconfig = new ConfigTemplateSsid();
										tmpconfig.setSsidProfile(ssidProfileTmp);
										tmpconfig.setInterfaceName(ssidProfileTmp.getSsidName());
										tmpconfig.setNetworkServicesEnabled(configTemplateSsid.getNetworkServicesEnabled());
										tmpconfig.setMacOuisEnabled(configTemplateSsid.getMacOuisEnabled());
										tmpconfig.setSsidEnabled(configTemplateSsid.getSsidEnabled());
										tmpconfig.setCheckE(configTemplateSsid.getCheckE());
										tmpconfig.setCheckP(configTemplateSsid.getCheckP());
										tmpconfig.setCheckD(configTemplateSsid.getCheckD());
										tmpconfig.setCheckET(configTemplateSsid.getCheckET());
										tmpconfig.setCheckPT(configTemplateSsid.getCheckPT());
										tmpconfig.setCheckDT(configTemplateSsid.getCheckDT());
//										tmpconfig.setClassfierAndMarker(configTemplateSsid.getClassfierAndMarker());
										addConfig.add(tmpconfig);

//										configTemplateSsid.setSsidProfile(ssidProfileTmp);
//										configTemplateSsid.setInterfaceName(ssidProfileTmp.getSsidName());
										break;
									}
								}
							}
						}

						if (shouldCreateNew) {
							ssidProfile.setId(null);
							ssidProfile.setVersion(null);
							ssidProfile.setOwner(AhRestoreNewMapTools.getonlyDomain());
							ssidProfile.setDefaultFlag(false);
							boolean nameExist = true;
							do {
								if (ssidProfile.getSsid().length() > 29) {
									ssidProfile.setSsidName(ssidProfile.getSsid().substring(0, 29) + "_" + nameCount);
								} else {
									ssidProfile.setSsidName(ssidProfile.getSsid() + "_" + nameCount);
								}
								nameCount++;
								if (!checkHiveNameExists("hiveName", ssidProfile.getSsidName()) &&
										!checkSsidNameExists("ssidName", ssidProfile.getSsidName())) {
									nameExist = false;
								}
							} while (nameExist);

							Set<Scheduler> cloneSchedulers = new HashSet<Scheduler>();
							for (Scheduler tempClass : ssidProfile.getSchedulers()) {
								cloneSchedulers.add(tempClass);
							}
							ssidProfile.setSchedulers(cloneSchedulers);

							Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
							for (MacFilter tempClass : ssidProfile.getMacFilters()) {
								cloneMacFilters.add(tempClass);
							}
							ssidProfile.setMacFilters(cloneMacFilters);

							Set<UserProfile> cloneRadiusUserProfiles = new HashSet<UserProfile>();
							for (UserProfile tempClass : ssidProfile.getRadiusUserProfile()) {
								cloneRadiusUserProfiles.add(tempClass);
							}
							ssidProfile.setRadiusUserProfile(cloneRadiusUserProfiles);

							Set<LocalUserGroup> cloneLocalUserGroups = new HashSet<LocalUserGroup>();
							for (LocalUserGroup tempClass : ssidProfile.getLocalUserGroups()) {
								cloneLocalUserGroups.add(tempClass);
							}
							ssidProfile.setLocalUserGroups(cloneLocalUserGroups);

							Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (GRateType gType : GRateType.values()) {
								TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(gType);
								if (null == rateSet) {
									rateSet = new TX11aOr11gRateSetting();
									if (GRateType.one.equals(gType) || GRateType.two.equals(gType) || GRateType.five.equals(gType)
											|| GRateType.eleven.equals(gType)) {
										rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
									} else {
										rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
									}
								}
								rateSet.setGRateType(gType);
								gRateSet.put(rateSet.getkey(), rateSet);
							}
							ssidProfile.setGRateSets(gRateSet);

							Map<String, TX11aOr11gRateSetting> aRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (ARateType aType : ARateType.values()) {
								TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(aType);
								if (null == rateSet) {
									rateSet = new TX11aOr11gRateSetting();
									if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
										rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
									} else {
										rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
									}
								}
								rateSet.setARateType(aType);
								aRateSets.put(rateSet.getkey(), rateSet);
							}
							ssidProfile.setARateSets(aRateSets);

							Map<String, TX11aOr11gRateSetting> nRateSets = new LinkedHashMap<String, TX11aOr11gRateSetting>();
							for (NRateType nType : NRateType.values()) {
								TX11aOr11gRateSetting rateSet = ssidProfile.getTX11aOr11gRateSetting(nType);
								if (null == rateSet) {
									rateSet = new TX11aOr11gRateSetting();
									rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
								}
								rateSet.setNRateType(nType);
								nRateSets.put(rateSet.getkey(), rateSet);
							}
							ssidProfile.setNRateSets(nRateSets);

							// set this ssid 11ac rate setting
							Map<String,List<Tx11acRateSettings>> acRateSets = getAllSsidAcRateSettings("ac_rate_settings_info");
							if(acRateSets == null || acRateSets.get(configTemplateSsid.getSsidProfile().getId()) == null){
								List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
								for (short z = Tx11acRateSettings.STREAM_TYPE_SINGLE; z <= Tx11acRateSettings.STREAM_TYPE_THREE; z ++){
									Tx11acRateSettings acRateSet = new Tx11acRateSettings();
									acRateSet.setStreamType(z);
									acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
									acRateList.add(acRateSet);
								}
								ssidProfile.setAcRateSets(acRateList);
							}else{
								List<Tx11acRateSettings> acRateList = acRateSets.get(configTemplateSsid.getSsidProfile().getId());
								ssidProfile.setAcRateSets(acRateList);
							}

							issueMap.put("WLANPOLICYID", String.valueOf(updateSsidBefore.get(ssidProfileOld.getSsidName())));
							issueMap.put("WLANPOLICYID2", String.valueOf(tempConfigTemplate.getId()));
							issueMap.put("NEWSSIDNAME", ssidProfile.getSsidName());
							issueMapList.add(issueMap);

							Long newId = QueryUtil.createBo(ssidProfile);
							ssidProfile = QueryUtil.findBoById(SsidProfile.class, newId);

							removeKey.add(configTemplateSsid.getSsidProfile().getId());
							ConfigTemplateSsid tmpconfig = new ConfigTemplateSsid();
							tmpconfig.setSsidProfile(ssidProfile);
							tmpconfig.setInterfaceName(ssidProfile.getSsidName());
							tmpconfig.setNetworkServicesEnabled(configTemplateSsid.getNetworkServicesEnabled());
							tmpconfig.setMacOuisEnabled(configTemplateSsid.getMacOuisEnabled());
							tmpconfig.setSsidEnabled(configTemplateSsid.getSsidEnabled());
							tmpconfig.setCheckE(configTemplateSsid.getCheckE());
							tmpconfig.setCheckP(configTemplateSsid.getCheckP());
							tmpconfig.setCheckD(configTemplateSsid.getCheckD());
							tmpconfig.setCheckET(configTemplateSsid.getCheckET());
							tmpconfig.setCheckPT(configTemplateSsid.getCheckPT());
							tmpconfig.setCheckDT(configTemplateSsid.getCheckDT());
//							tmpconfig.setClassfierAndMarker(configTemplateSsid.getClassfierAndMarker());
							addConfig.add(tmpconfig);

//							configTemplateSsid.setSsidProfile(ssidProfile);
//							configTemplateSsid.setInterfaceName(ssidProfile.getSsidName());
							createSsidBefore.put(ssidProfile.getSsidName(), ssidProfile);
						}
					}
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("restore ConfigTemplate update ssid profile error! Ssidname: " + ssidProfile.getSsidName());
					e.printStackTrace();
				}
			}
			removeUserProfile.removeAll(exitsUserProfile);
//			for (String strKey : removeUserProfile) {
//				tempConfigTemplate.getQosPolicies().remove(strKey);
//			}

			if (removeKey.size() > 0) {
				for (Long key : removeKey) {
					tempConfigTemplate.getSsidInterfaces().remove(key);
				}
				for (ConfigTemplateSsid config : addConfig) {
					tempConfigTemplate.getSsidInterfaces().put(config.getSsidProfile().getId(), config);
				}
			}
		}
	}

//	private static void resetConfigTemplateQos(List<ConfigTemplate> allConfigTemplate) {
//
//		for (int i=0; i< allConfigTemplate.size(); i++ ) {
//			ConfigTemplate tempConfigTemplate = allConfigTemplate.get(i);
//			Set<String> newUserProfile = new HashSet<String>();
//			for (ConfigTemplateSsid templateSsid : tempConfigTemplate.getSsidInterfaces().values()) {
//				if (templateSsid.getSsidProfile() != null) {
//					if (templateSsid.getSsidProfile().getUserProfileDefault() != null) {
//						if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//									.getId()
//									+ "|" + SsidProfile.RADIOMODE_A);
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//									.getId()
//									+ "|" + SsidProfile.RADIOMODE_BG);
//						} else {
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileDefault()
//									.getId()
//									+ "|" + templateSsid.getSsidProfile().getRadioMode());
//						}
//					}
//
//					if (templateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
//						if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//									.getId()
//									+ "|" + SsidProfile.RADIOMODE_A);
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//									.getId()
//									+ "|" + SsidProfile.RADIOMODE_BG);
//						} else {
//							newUserProfile.add(templateSsid.getSsidProfile().getUserProfileSelfReg()
//									.getId()
//									+ "|" + templateSsid.getSsidProfile().getRadioMode());
//						}
//					}
//					if (templateSsid.getSsidProfile().getRadiusUserProfile() != null) {
//						SsidProfile tmpSsidProfile = QueryUtil.findBoById(SsidProfile.class, templateSsid.getSsidProfile().getId(),new ImplQueryBo());
//						for (UserProfile uspr : tmpSsidProfile.getRadiusUserProfile()) {
//							if (templateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
//								newUserProfile.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_A);
//								newUserProfile.add(uspr.getId() + "|" + SsidProfile.RADIOMODE_BG);
//							} else {
//								newUserProfile.add(uspr.getId() + "|"
//										+ templateSsid.getSsidProfile().getRadioMode());
//							}
//						}
//					}
//				}
//			}
//			Set<String> removeKey = new HashSet<String>();
//			for (ConfigTemplateQos templateQos : tempConfigTemplate.getQosPolicies().values()) {
//				if (newUserProfile.contains(templateQos.getKey())) {
//					newUserProfile.remove(templateQos.getKey());
//				} else {
//					removeKey.add(templateQos.getKey());
//				}
//			}
//			if (removeKey.size() > 0) {
//				for (String key : removeKey) {
//					tempConfigTemplate.getQosPolicies().remove(key);
//				}
//			}
//
//			if (newUserProfile.size() > 0) {
//				for (String unusedId : newUserProfile) {
//					String[] IdMode = unusedId.split("\\|");
//					UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, Long
//							.valueOf(IdMode[0]));
//					if (userProfile != null) {
//						ConfigTemplateQos templateQos = new ConfigTemplateQos();
//						templateQos.setUserProfile(userProfile);
//						templateQos.setRadioMode(Integer.valueOf(IdMode[1]));
//						tempConfigTemplate.getQosPolicies().put(templateQos.getKey(), templateQos);
//					}
//				}
//			}
//		}
//	}


	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	private static boolean checkHiveNameExists(String name, Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select bo.id from " + HiveProfile.class.getSimpleName() + " bo", null, new FilterParams(
						name, value), AhRestoreNewMapTools.getonlyDomain().getId());
		return !boIds.isEmpty();
	}
	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	private static boolean checkSsidNameExists(String name, Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select bo.id from " + SsidProfile.class.getSimpleName() + " bo", null, new FilterParams(
						name, value), AhRestoreNewMapTools.getonlyDomain().getId());
		return !boIds.isEmpty();
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	private static boolean checkCwpNameExists(String name, Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select bo.id from " + Cwp.class.getSimpleName() + " bo", null, new FilterParams(
						name, value), AhRestoreNewMapTools.getonlyDomain().getId());
		return !boIds.isEmpty();
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	private static <T extends HmBo> boolean checkBONameExists(Class<T> boClass, String name, Object value) {
        List<?> boIds = QueryUtil.executeQuery(boClass, null,
                new FilterParams(name, value),
                AhRestoreNewMapTools.getonlyDomain().getId());
	    return !boIds.isEmpty();
	}

	private static boolean checkExistSsidProfile(SsidProfile ssidProfileOld,
			SsidProfile ssidProfileNew, Map<String,String> issueMap){
		boolean ret = true;
		int issueNumber = 0;
		if (!ssidProfileOld.getServiceFilter().getId().equals(ssidProfileNew.getServiceFilter().getId())) {
			ret= false;
			issueNumber= issueNumber+1;
		}
		if (ssidProfileOld.getRadioMode()!= ssidProfileNew.getRadioMode()) {
			ret= false;
			issueNumber= issueNumber+2;
		}
		if (ssidProfileOld.getRadiusAssignment()!=null && ssidProfileNew.getRadiusAssignment()==null){
			ret= false;
			issueNumber= issueNumber+4;
		}
		if (ssidProfileOld.getRadiusAssignment()==null && ssidProfileNew.getRadiusAssignment()!=null){
			ret= false;
			issueNumber= issueNumber+4;
		}
		if (ssidProfileOld.getRadiusAssignment()!=null
				&& ssidProfileNew.getRadiusAssignment()!=null
				&& !ssidProfileOld.getRadiusAssignment().getId().equals(ssidProfileNew.getRadiusAssignment().getId())){
			ret= false;
			issueNumber= issueNumber+4;
		}
		if (ssidProfileOld.getChkDeauthenticate()!= ssidProfileNew.getChkDeauthenticate()) {
			ret= false;
		}
		if (ssidProfileOld.getChkUserOnly()!= ssidProfileNew.getChkUserOnly()) {
			ret= false;
		}
		if (ssidProfileOld.getChkUserOnly()) {
			if ((ssidProfileOld.getActionTime()!= ssidProfileNew.getActionTime())
					|| (ssidProfileOld.getDenyAction()!= ssidProfileNew.getDenyAction())){
				ret= false;
			}
		}
		if (!ret && issueMap != null){
			issueMap.put("ISSUEID", String.valueOf(issueNumber));
		}
		return ret;
	}

	private static boolean compareTwoSsidSame(SsidProfile ssidProfileOld,
			SsidProfile ssidProfileNew, Map<String,String> issueMap){
		boolean ret = true;
//		if (ssidProfileOld.getCwp()==null && ssidProfileNew.getCwp()!= null) {
//			ret = false;
//		}
//		if (ssidProfileOld.getCwp()!=null && ssidProfileNew.getCwp()== null) {
//			ret = false;
//		}
//		if (ssidProfileOld.getCwp()!=null
//				&& ssidProfileNew.getCwp()!=null
//				&& !ssidProfileOld.getCwp().getId().equals(ssidProfileNew.getCwp().getId())){
//			ret = false;
//		}
		if (ssidProfileOld.getUserProfileDefault()==null && ssidProfileNew.getUserProfileDefault()!= null) {
			ret = false;
		}
		if (ssidProfileOld.getUserProfileDefault()!=null && ssidProfileNew.getUserProfileDefault()== null) {
			ret = false;
		}
		if (ssidProfileOld.getUserProfileDefault()!=null
				&& ssidProfileNew.getUserProfileDefault()!=null
				&& !ssidProfileOld.getUserProfileDefault().getId().equals(ssidProfileNew.getUserProfileDefault().getId())){
			ret = false;
		}
		if (ssidProfileOld.getUserProfileSelfReg()==null && ssidProfileNew.getUserProfileSelfReg()!= null) {
			ret = false;
		}
		if (ssidProfileOld.getUserProfileSelfReg()!=null && ssidProfileNew.getUserProfileSelfReg()== null) {
			ret = false;
		}
		if (ssidProfileOld.getUserProfileSelfReg()!=null
				&& ssidProfileNew.getUserProfileSelfReg()!=null
				&& !ssidProfileOld.getUserProfileSelfReg().getId().equals(ssidProfileNew.getUserProfileSelfReg().getId())){
			ret = false;
		}
		if ((ssidProfileOld.getRadiusUserProfile() == null || ssidProfileOld.getRadiusUserProfile().size()<1)
			&& (ssidProfileNew.getRadiusUserProfile() != null && ssidProfileNew.getRadiusUserProfile().size()>0)){
			ret = false;
		}
		if ((ssidProfileNew.getRadiusUserProfile() == null || ssidProfileNew.getRadiusUserProfile().size()<1)
			&& (ssidProfileOld.getRadiusUserProfile() != null && ssidProfileOld.getRadiusUserProfile().size()>0)){
			ret = false;
		}
		if ((ssidProfileNew.getRadiusUserProfile() != null && ssidProfileNew.getRadiusUserProfile().size()>0)
				&& (ssidProfileOld.getRadiusUserProfile() != null && ssidProfileOld.getRadiusUserProfile().size()>0)){
			Set<String> ssidNewUser = new HashSet<String>();
			Set<String> ssidOldUser = new HashSet<String>();
			for(UserProfile user: ssidProfileNew.getRadiusUserProfile()){
				ssidNewUser.add(user.getUserProfileName());
			}
			for(UserProfile user: ssidProfileOld.getRadiusUserProfile()){
				ssidOldUser.add(user.getUserProfileName());
			}
			if (ssidNewUser.size() == ssidOldUser.size()){
				ssidNewUser.removeAll(ssidOldUser);
				if (ssidNewUser.size()>0){
					ret = false;
				}
			} else {
				ret = false;
			}
		}
		if (!ret && issueMap != null){
			boolean existSsidFlg = false;
			for(Map<String,String> oneIssueMap:issueMapList){
				String newSsidName = oneIssueMap.get("NEWSSIDNAME");
				if (newSsidName.equals(ssidProfileOld.getSsidName())){
					int issueId = Integer.parseInt(oneIssueMap.get("ISSUEID"));
					if (issueId< 8){
						issueId = issueId + 8;
					}
					issueMap.put("ISSUEID",String.valueOf(issueId));
					existSsidFlg = true;
					break;
				}
			}
			if (!existSsidFlg){
				issueMap.put("ISSUEID", "8");
			}
			issueMap.put("SAMESSIDNAME", ssidProfileOld.getSsid());
		}
		return ret;
	}

	/**
	 * Radius server is setted in Mgmt Service Option from 3.2r1
	 *
	 * @param hmDom -
	 */
	private static void updateMgmtServiceOption(HmDomain hmDom) {
		if (allOptionRadius.size() > 0) {
			HmUpgradeLog upgradeLog;
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<MgmtServiceOption> updateOptionBo = new ArrayList<MgmtServiceOption>();

			String post12 = "The set of management options do not specify a RADIUS server.";
			String action1 = "If you need a RADIUS server for the set of management options, manually add it.";
			String action2 = "To keep the authentication method as local, leave the management options alone. To authenticate "+NmsUtil.getOEMCustomer().getAccessPonitName()+" admins through RADIUS, change the method of authentication to RADIUS or Both.";
			String post3 = NmsUtil.getOEMCustomer().getNmsName() +
				" chose a RADIUS server from one of the network policies and bound it to this set of management options.";
			String action3 = "If a network policy must use a different RADIUS server, clone this management options set, specify a different RADIUS server, and reference the newly cloned management options set in the network policy.";
			String post4 = NmsUtil.getOEMCustomer().getNmsName() + " bound the specified RADIUS server to this set of management options.";
			String action4 = "No action is required.";
			String post5 = "Because a network policy now only references a RADIUS server indirectly through a management options set, this network policy no longer references a RADIUS server.";
			String action5 = "If the network policy must use this RADIUS server, create a new set of management options that references it and specify that management options set in this network policy.";
			Map<String, String> allRadius;
			MgmtServiceOption mgmtOption;
			for (String optionIdOld : allOptionRadius.keySet()) {
				allRadius = allOptionRadius.get(optionIdOld);
				String former;
				if ("-1".equals(optionIdOld)) {
					/*
					 * these wlan policies have radius server but no mgmt service option
					 */
					for (String radiusName : allRadius.keySet()) {
						upgradeLog = new HmUpgradeLog();
						former = "Network policy \"" + allRadius.get(radiusName) + ") specified RADIUS server \"" + radiusName + ") but not a management options set.";
						upgradeLog.setFormerContent(former);
						upgradeLog.setPostContent(post5);
						upgradeLog.setRecommendAction(action5);
						lstLogBo.add(upgradeLog);
					}
				} else {
					/*
					 * get this mgmt service option information
					 */
					Long newId = AhRestoreNewMapTools.getMapOption(Long.valueOf(optionIdOld));
					mgmtOption = QueryUtil.findBoById(MgmtServiceOption.class, newId);
					String optionName = mgmtOption.getMgmtName();
					String authType = "";
					switch(mgmtOption.getUserAuth()) {
						case EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS :
							authType = "RADIUS";
							break;
						case EnumConstUtil.ADMIN_USER_AUTHENTICATION_BOTH :
							authType = "Both";
							break;
						default :
							break;
					}
					StringBuilder strBody = new StringBuilder();
					String radiusName = "";
					for (String radiusId : allRadius.keySet()) {
						/*
						 * set the radius server to this mgmt service option
						 */
						if (!"-1".equals(radiusId)) {
							newId = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.valueOf(radiusId));
							if (null != newId) {
								List<?> radiusSql = QueryUtil.executeQuery("SELECT radiusName FROM " + RadiusAssignment.class.getSimpleName(), null, new FilterParams("id", newId));
								if (!radiusSql.isEmpty()) {
									radiusName = (String)(radiusSql.get(0));
								}
							}
							if (!"".equals(authType) && null == mgmtOption.getRadiusServer() && null != newId) {
								mgmtOption.setRadiusServer(AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class, newId));
								updateOptionBo.add(mgmtOption);
							}
						}
						// get wlan policy name
						String[] wlans = allRadius.get(radiusId).split(" ");
						StringBuilder strEnd = new StringBuilder();
						strEnd.append("\"");
						if (wlans.length == 1) {
							strEnd.append(wlans[0]);
						} else {
							for (int i = 0; i < wlans.length; i ++) {
								if (i != wlans.length-1) {
									strEnd.append(wlans[i]).append(", ");
								} else {
									strEnd.append("and ").append(wlans[i]);
								}
							}
						}
						strEnd.append("\" ");
						if (!"".equals(authType) && allRadius.size() == 1) {
							/*
							 * the mgmt service option has no corresponding radius server
							 */
							if ("-1".equals(radiusId)) {
								upgradeLog = new HmUpgradeLog();
								strEnd.append("However, no RADIUS server was specified in ");
								strEnd.append((wlans.length == 1)?"it.":"these network policies.");
								former = "The management options set \"" + optionName + ") specified " + authType + " for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" admins authentication and was used in network policy " + strEnd.toString();
								upgradeLog.setFormerContent(former);
								upgradeLog.setPostContent(post12);
								upgradeLog.setRecommendAction(action1);
								lstLogBo.add(upgradeLog);
							/*
							 * the mgmt service option has one same radius server
							 */
							} else {
								upgradeLog = new HmUpgradeLog();
								strEnd.append((wlans.length == 1)?"which specified the RADIUS server \"":"all of which specified the same RADIUS server \"");
								former = "The management options set \"" + optionName + ") specified " + authType + " for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" admins authentication and was used in network policy " + strEnd.toString() + radiusName + ").";
								upgradeLog.setFormerContent(former);
								upgradeLog.setPostContent(post4);
								upgradeLog.setRecommendAction(action4);
								lstLogBo.add(upgradeLog);
							}
						} else {
							strBody.append(" in network policy ").append(strEnd.toString());
							strBody.append(radiusName.length() > 32 ? "no RADIUS server was specified in which;" : "which referenced RADIUS server \"" + radiusName + ");");
						}
					}
					if (strBody.length() > 0) {
						String strResult = strBody.toString().substring(0, strBody.toString().length()-1) + ".";
						/*
						 * the auth method is radius or both of the mgmt service option which has more than one corresponding radius server
						 */
						if (!"".equals(authType)) {
							upgradeLog = new HmUpgradeLog();
							former = "The management options set \"" + optionName + ") specified " + authType + " for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" admins authentication and was used" + strResult;
							upgradeLog.setFormerContent(former);
							upgradeLog.setPostContent(post3);
							upgradeLog.setRecommendAction(action3);
							lstLogBo.add(upgradeLog);
						/*
						 * the auth method is local of the mgmt service option which has more than one corresponding radius server
						 */
						} else {
							upgradeLog = new HmUpgradeLog();
							former = "The management options set \"" + optionName + ") specified Local for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" admins authentication and was used" + strResult;
							upgradeLog.setFormerContent(former);
							upgradeLog.setPostContent(post12);
							upgradeLog.setRecommendAction(action2);
							lstLogBo.add(upgradeLog);
						}
					}
				}
			}

			/*
			 * update the mgmt service option data in database
			 */
			if (updateOptionBo.size() > 0) {
				try {
					QueryUtil.bulkUpdateBos(updateOptionBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("update mgmt service option data error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}

			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				for (HmUpgradeLog log : lstLogBo) {
					log.setOwner(hmDom);
					log.setLogTime(new HmTimeStamp(System.currentTimeMillis(),hmDom.getTimeZoneString()));
					log.setAnnotation("Click to add an annotation");
				}
				try {
					QueryUtil.bulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert mgmt service option upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		}
	}

	/**
	 * Get all information from g_rate_setting_info or a_rate_setting_info table
	 *
	 * @param arg_Table - the table name
	 * @return Map<String, Map<String, TX11aOr11gRateSetting>> - the key is ssid_profile_id
	 * @throws AhRestoreColNotExistException - if arg_Table.xml is not exist.
	 * @throws AhRestoreException - if error in parsing arg_Table.xml.
	 */
	private static Map<String, Map<String, TX11aOr11gRateSetting>> getAllSsidRateSettings(String arg_Table)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of arg_Table.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(arg_Table);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Map<String, TX11aOr11gRateSetting>> rateSettings = new HashMap<String, Map<String, TX11aOr11gRateSetting>>();
		Map<String, TX11aOr11gRateSetting> rateSetting;
		TX11aOr11gRateSetting singleInfo;

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new TX11aOr11gRateSetting();

			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			rateSetting = rateSettings.get(id);
			if (null == rateSetting) {
				rateSetting = new HashMap<String, TX11aOr11gRateSetting>();
			}

			/**
			 * Set rateset
			 */
			colName = "rateset";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			if (arg_Table.equalsIgnoreCase("n_rate_setting_info")){
				String rateset = isColPresent ? xmlParser.getColVal(i, colName) : String
						.valueOf(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				singleInfo.setRateSet((short) AhRestoreCommons.convertInt(rateset));
			} else {
				String rateset = isColPresent ? xmlParser.getColVal(i, colName) : String
						.valueOf(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				singleInfo.setRateSet((short) AhRestoreCommons.convertInt(rateset));
			}

			/**
			 * Set mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String mapkey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			rateSetting.put(mapkey, singleInfo);

			rateSettings.put(id, rateSetting);
		}

		return rateSettings.isEmpty() ? null : rateSettings;
	}

	private static Map<String, List<Tx11acRateSettings>> getAllSsidAcRateSettings(String arg_Table) throws AhRestoreColNotExistException, AhRestoreException{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of arg_Table.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(arg_Table);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<Tx11acRateSettings>> rateSettings = new HashMap<String, List<Tx11acRateSettings>>();
		Tx11acRateSettings singleInfo;

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new Tx11acRateSettings();

			/**
			 * Set ssid_profile_id
			 */
			colName = "ssid_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id) || id == null || id.equalsIgnoreCase("null")) {
				continue;
			}

			colName="mcsvalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String mcsValue = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : Short.toString(Tx11acRateSettings.MAX_MCS_VALUE);
			singleInfo.setMcsValue(Short.valueOf(mcsValue));

			colName="streamenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String streamEnable = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "true";
			singleInfo.setStreamEnable(AhRestoreCommons.convertStringToBoolean(streamEnable));

			colName="streamtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, arg_Table, colName);
			String streamType = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "0";
			singleInfo.setStreamType(Short.valueOf(streamType));

			if (rateSettings.get(id) == null) {
				List<Tx11acRateSettings> list = new ArrayList<Tx11acRateSettings>();;
				list.add(singleInfo);
				rateSettings.put(id, list);
			} else {
				rateSettings.get(id).add(singleInfo);
			}
		}

		return rateSettings;
	}

	private static List<HiveApFilter> getAllHiveApFilter() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hiveap_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hiveap_filter");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in hiveap_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApFilter> hiveApFilterInfo = new ArrayList<HiveApFilter>();
		boolean isColPresent;
		String colName;
		HiveApFilter ahHiveApFilterDTO;

		for (int i = 0; i < rowCount; i++)
		{
			ahHiveApFilterDTO = new HiveApFilter();

			/**
			 * Set filtername
			 */
			colName = "filtername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hiveap_filter' data be lost, cause: 'filtername' column is not exist.");
				/**
				 * The filtername column must be exist in the table of hiveap_filter
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hiveap_filter' data be lost, cause: 'filtername' column value is null.");
				continue;
			}
			ahHiveApFilterDTO.setFilterName(name.trim());

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			if (username == null || username.trim().equals("")
				|| username.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hiveap_filter' data be lost, cause: 'username' column is not exist.");
				continue;
			}
			ahHiveApFilterDTO.setUserName(username);

			/**
			 * Set filterhive
			 */
			colName = "filterhive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterhive = isColPresent ? xmlParser.getColVal(i, colName) : "-1";

			if (filterhive.equalsIgnoreCase("null")){
				ahHiveApFilterDTO.setFilterHive(-2L);
			} else if (Long.parseLong(filterhive.trim())<0){
					ahHiveApFilterDTO.setFilterHive(-2L);
			} else {
				Long newfilterhiveId = AhRestoreNewMapTools.getMapHives(Long.parseLong(filterhive.trim()));
				HiveProfile hiveProfile=null;
				if (newfilterhiveId!=null) {
					hiveProfile = QueryUtil.findBoById(HiveProfile.class, newfilterhiveId);
				}
				ahHiveApFilterDTO.setFilterHive(hiveProfile==null ? -2 : hiveProfile.getId());
			}
			/**
			 * Set filtertemplate
			 */
			colName = "filtertemplate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filtertemplate = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			if (filtertemplate.equalsIgnoreCase("null")){
				ahHiveApFilterDTO.setFilterTemplate(-2L);
			} else if (Long.parseLong(filtertemplate.trim())<0){
					ahHiveApFilterDTO.setFilterTemplate(-2L);
			} else {
				Long newfiltertemplateId = AhRestoreNewMapTools.getMapConfigTemplate(Long.parseLong(filtertemplate.trim()));
				ConfigTemplate configTemplate=null;
				if (newfiltertemplateId!=null) {
					configTemplate = QueryUtil.findBoById(ConfigTemplate.class, newfiltertemplateId);
				}
				ahHiveApFilterDTO.setFilterTemplate(configTemplate==null ? -2 : configTemplate.getId());
			}
			/**
			 * Set filterprovision
			 */
			colName = "filterprovision";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterprovision = isColPresent ? xmlParser
				.getColVal(i, colName) : "1";
			ahHiveApFilterDTO.setFilterProvision(AhRestoreCommons.convertInt(filterprovision));

			/**
			 * Set filterprovisionflag
			 */
			colName = "filterprovisionflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterprovisionflag = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterProvisionFlag(AhRestoreCommons.convertStringToBoolean(filterprovisionflag));

			/**
			 * Set filtertopology
			 */
			colName = "filtertopology";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filtertopology = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			if (Long.parseLong(filtertopology.trim())==-2) {
				ahHiveApFilterDTO.setFilterTopology(-2L);
			} else {
				Long newfiltertopologyId = AhRestoreNewMapTools.getMapMapContainer(Long.parseLong(filtertopology.trim()));
				MapContainerNode location=null;
				if (newfiltertopologyId!=null) {
					location = QueryUtil.findBoById(MapContainerNode.class, newfiltertopologyId);
				}
				ahHiveApFilterDTO.setFilterTopology(location==null ? -1 : location.getId());
			}
			/**
			 * Set filterip
			 */
			colName = "filterip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterip = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			ahHiveApFilterDTO.setFilterIp(filterip);

			/**
			 * Set filterConfiguration
			 */
			colName = "filterConfiguration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String filterConfiguration = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			if (AhRestoreCommons.convertInt(filterConfiguration)<=0){
				ahHiveApFilterDTO.setFilterConfiguration(-2);
			} else {
				ahHiveApFilterDTO.setFilterConfiguration(AhRestoreCommons.convertInt(filterConfiguration));
			}
			/**
			 * Set filtervpnserver
			 */
			colName = "filtervpnserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filtervpnserver = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterVpnServer(AhRestoreCommons.convertStringToBoolean(filtervpnserver));

			/**
			 * Set filterradiusserver
			 */
			colName = "filterradiusserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterradiusserver = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterRadiusServer(AhRestoreCommons.convertStringToBoolean(filterradiusserver));

			/**
			 * Set filterradiusproxy
			 */
			colName = "filterradiusproxy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterradiusproxy = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterRadiusProxy(AhRestoreCommons.convertStringToBoolean(filterradiusproxy));

			/**
			 * Set filterdhcpserver
			 */
			colName = "filterdhcpserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filterdhcpserver = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterDhcpServer(AhRestoreCommons.convertStringToBoolean(filterdhcpserver));

			/**
			 * Set filtervpnclient
			 */
			colName = "filtervpnclient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_filter", colName);
			String filtervpnclient = isColPresent ? xmlParser
				.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setFilterVpnClient(AhRestoreCommons.convertStringToBoolean(filtervpnclient));

			/**
			 * Set hiveApType
			 */
			colName = "hiveApType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String hiveApType = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			if (AhRestoreCommons.convertInt(hiveApType)<0){
				ahHiveApFilterDTO.setHiveApType((short)-2);
			} else {
				ahHiveApFilterDTO.setHiveApType((short)AhRestoreCommons.convertInt(hiveApType));
			}
			/**
			 * Set hiveApModel
			 */
			colName = "hiveApModel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String hiveApModel = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			if (AhRestoreCommons.convertInt(hiveApModel)<0) {
				ahHiveApFilterDTO.setHiveApModel((short)-2);
			} else {
				ahHiveApFilterDTO.setHiveApModel((short)AhRestoreCommons.convertInt(hiveApModel));
			}

			/**
			 * Set filterDeviceType
			 */
			colName = "filterDeviceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String filterDeviceType = isColPresent ? xmlParser.getColVal(i, colName) : "-2";
			if (AhRestoreCommons.convertInt(filterDeviceType)<0) {
				ahHiveApFilterDTO.setFilterDeviceType((short)-2);
			} else {
				ahHiveApFilterDTO.setFilterDeviceType((short)AhRestoreCommons.convertInt(filterDeviceType));
			}

			/**
			 * Set displayVer
			 */
			colName = "displayVer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String displayVer = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setDisplayVer(displayVer.trim());

			/**
			 * Set hostname
			 */
			colName = "hostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String hostname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setHostname(hostname);

			/**
			 * Set classificationTag1
			 */
			colName = "classificationTag1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String classificationTag1 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setClassificationTag1(classificationTag1.trim());

			/**
			 * Set classificationTag2
			 */
			colName = "classificationTag2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String classificationTag2 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setClassificationTag2(classificationTag2.trim());

			/**
			 * Set classificationTag3
			 */
			colName = "classificationTag3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String classificationTag3 = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setClassificationTag3(classificationTag3.trim());


			/**
			 * Set eth0Bridge
			 */
			colName = "eth0Bridge";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String eth0Bridge = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setEth0Bridge(AhRestoreCommons.convertStringToBoolean(eth0Bridge));

			/**
			 * Set eth1Bridge
			 */
			colName = "eth1Bridge";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String eth1Bridge = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setEth1Bridge(AhRestoreCommons.convertStringToBoolean(eth1Bridge));

			/**
			 * Set red0Bridge
			 */
			colName = "red0Bridge";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String red0Bridge = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setRed0Bridge(AhRestoreCommons.convertStringToBoolean(red0Bridge));

			/**
			 * Set agg0Bridge
			 */
			colName = "agg0Bridge";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String agg0Bridge = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			ahHiveApFilterDTO.setAgg0Bridge(AhRestoreCommons.convertStringToBoolean(agg0Bridge));

			/**
			 * Set serialNumber
			 */
			colName = "serialNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			String serialNumber = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahHiveApFilterDTO.setSerialNumber(serialNumber);

			/**
			 * Set typeOfThisFilter
			 */
			colName = "typeOfThisFilter";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			if (isColPresent) {
				ahHiveApFilterDTO.setTypeOfThisFilter((short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)));
			} else {
				ahHiveApFilterDTO.setTypeOfThisFilter(HiveApFilter.FILTER_TYPE_MANAGED_DEVICE);
			}
			
			// /**
			// * Set owner
			// */
			// colName = "owner";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hm_report", colName);
			// String owner = isColPresent ? xmlParser.getColVal(i, colName) :
			// "";
			/*
			 * set owner
			 */

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hiveap_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			ahHiveApFilterDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			hiveApFilterInfo.add(ahHiveApFilterDTO);

		}

		return hiveApFilterInfo;
	}

	public static boolean restoreHiveApFilter()
	{
		try
		{
			List<HiveApFilter> allHiveApFilter = getAllHiveApFilter();
			if (null == allHiveApFilter)
			{
				return false;
			}
			else
			{
				QueryUtil.restoreBulkCreateBos(allHiveApFilter);
			}
		}
		catch (Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<USBModem> getAllUSBModems() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of usb_modem_parameter.xml
		 */
		String tableFileName = "usb_modem_parameter";
		boolean restoreRet = xmlParser.readXMLFile(tableFileName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<USBModem> usbModems = new ArrayList<USBModem>();
		boolean isColPresent;
		String colName;
		USBModem usbModem;

		for (int i = 0; i < rowCount; i++)
		{
			usbModem = new USBModem();

			/**
			 * Set modemName
			 */
			colName = "modemName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'usb_modem_parameter' data be lost, cause: 'modemName' column is not exist.");
				continue;
			}

			/**
			 * name(modem id) is needed
			 */
			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'usb_modem_parameter' data be lost, cause: 'modemName' column value is null.");
				continue;
			}
			usbModem.setModemName(name.trim());

			/**
			 * Set apn
			 */
			colName = "apn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String apn = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setApn(apn.trim());

			/**
			 * Set dailupNumber
			 */
			colName = "dailupNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String dailupNumber = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setDailupNumber(dailupNumber.trim());

			/**
			 * Set userId
			 */
			colName = "userId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String userId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setUserId(userId.trim());

			/**
			 * Set password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String password = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setPassword(password.trim());

			/**
			 * Set displayName
			 */
			colName = "displayName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String displayName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setDisplayName(displayName.trim());

			/**
			 * Set displayType
			 */
			colName = "displayType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String displayType = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setDisplayType(displayType.trim());

			/**
			 * Set usbVendorId
			 */
			colName = "usbVendorId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String usbVendorId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setUsbVendorId(usbVendorId.trim());

			/**
			 * Set usbProductId
			 */
			colName = "usbProductId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String usbProductId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setUsbProductId(usbProductId.trim());

			/**
			 * Set usbModule
			 */
			colName = "usbModule";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String usbModule = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setUsbModule(usbModule.trim());

			/**
			 * Set hiveOSVersionMin
			 */
			colName = "hiveOSVersionMin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String hiveOSVersionMin = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setHiveOSVersionMin(hiveOSVersionMin.trim());

			/**
			 * Set serialPort
			 */
			colName = "serialPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String serialPort = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setSerialPort(serialPort.trim());

			/**
			 * Set connectType
			 */
			colName = "connectType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String connectType = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setConnectType(connectType.trim());

			/**
			 * Set authType
			 */
			colName = "authType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			String authType = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			usbModem.setAuthType(authType.trim());

			/**
			 * Set usePeerDns
			 */
			colName = "usePeerDns";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			boolean usePeerDns = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			usbModem.setUsePeerDns(usePeerDns);

			/**
			 * Set selected
			 */
			colName = "selected";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableFileName, colName);
			boolean selected = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			usbModem.setSelected(selected);


			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableFileName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'usb_modem_parameter' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			usbModem.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			usbModems.add(usbModem);

		}

		return usbModems;
	}

	public static boolean restoreUSBModem()
	{
		try
		{
			List<USBModem> usbModems = getAllUSBModems();
			if (null == usbModems)
			{
				return false;
			}
			else
			{
				QueryUtil.restoreBulkCreateBos(usbModems);
			}
		}
		catch (Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<AhAlarmsFilter> getAllAhAlarmsFilter() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ah_alarms_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ah_alarms_filter");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ah_alarms_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhAlarmsFilter> ahAlarmsFilterInfo = new ArrayList<AhAlarmsFilter>();
		boolean isColPresent;
		String colName;
		AhAlarmsFilter ahAlarmsFilterDTO;

		for (int i = 0; i < rowCount; i++){

			ahAlarmsFilterDTO = new AhAlarmsFilter();

			/**
			 * Set filterName
			 */
			colName = "filtername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ah_alarms_filter", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_alarms_filter' data be lost, cause: 'filtername' column is not exist.");
				/**
				 * The filtername column must be exist in the table of ah_alarms_filter
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_alarms_filter' data be lost, cause: 'filtername' column value is null.");
				continue;
			}
			ahAlarmsFilterDTO.setFilterName(name.trim());

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ah_alarms_filter", colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			if (username == null || username.trim().equals("")
				|| username.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_alarms_filter' data be lost, cause: 'username' column is not exist.");
				continue;
			}
			ahAlarmsFilterDTO.setUserName(username);

			/**
			 * Set apId
			 */
			colName = "apId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			String apId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahAlarmsFilterDTO.setApId(apId.trim());

			/**
			 * Set component
			 */
			colName = "component";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			String component = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahAlarmsFilterDTO.setComponent(component.trim());

			/**
			 * Set severity
			 */
			colName = "severity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			String severity = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			ahAlarmsFilterDTO.setSeverity((short)AhRestoreCommons.convertInt(severity));

			/**
			 * Set startTime
			 */
			colName = "startTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			String startTime = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			ahAlarmsFilterDTO.setStartTime(AhRestoreCommons.convertLong(startTime));

			/**
			 * Set endTime
			 */
			colName = "endTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			String endTime = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			ahAlarmsFilterDTO.setEndTime(AhRestoreCommons.convertLong(endTime));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_alarms_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_alarms_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahAlarmsFilterDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahAlarmsFilterInfo.add(ahAlarmsFilterDTO);
		}

		return ahAlarmsFilterInfo;
	}

	public static boolean restoreAhAlarmsFilter()
	{
		try
		{
			List<AhAlarmsFilter> ahAlarmsFilterInfo = getAllAhAlarmsFilter();
			if (null == ahAlarmsFilterInfo)
			{
				return false;
			}
			else
			{
				QueryUtil.restoreBulkCreateBos(ahAlarmsFilterInfo);
			}
		}
		catch (Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<AhEventsFilter> getAllAhEventsFilter() throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ah_events_filter.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ah_events_filter");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ah_events_filter table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhEventsFilter> ahEventsFilterInfo = new ArrayList<AhEventsFilter>();
		boolean isColPresent;
		String colName;
		AhEventsFilter ahEventsFilterDTO;

		for (int i = 0; i < rowCount; i++){
			ahEventsFilterDTO = new AhEventsFilter();

			/**
			 * Set filtername
			 */
			colName = "filtername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ah_events_filter", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_events_filter' data be lost, cause: 'filtername' column is not exist.");
				/**
				 * The filtername column must be exist in the table of ah_events_filter
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_events_filter' data be lost, cause: 'filtername' column value is null.");
				continue;
			}
			ahEventsFilterDTO.setFilterName(name.trim());

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ah_events_filter", colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName)
				: "";
			if (username == null || username.trim().equals("")
				|| username.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_events_filter' data be lost, cause: 'username' column is not exist.");
				continue;
			}
			ahEventsFilterDTO.setUserName(username);

			/**
			 * Set apId
			 */
			colName = "apId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_events_filter", colName);
			String apId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahEventsFilterDTO.setApId(apId.trim());

			/**
			 * Set component
			 */
			colName = "component";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_events_filter", colName);
			String component = isColPresent ? xmlParser.getColVal(i, colName) : "";
			ahEventsFilterDTO.setComponent(component.trim());

			/**
			 * Set startTime
			 */
			colName = "startTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_events_filter", colName);
			String startTime = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			ahEventsFilterDTO.setStartTime(AhRestoreCommons.convertLong(startTime));

			/**
			 * Set endTime
			 */
			colName = "endTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_events_filter", colName);
			String endTime = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			ahEventsFilterDTO.setEndTime(AhRestoreCommons.convertLong(endTime));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ah_events_filter", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'ah_events_filter' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			ahEventsFilterDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			ahEventsFilterInfo.add(ahEventsFilterDTO);
		}
		return ahEventsFilterInfo;
	}

	public static boolean restoreAhEventsFilter()
	{
		try
		{
			List<AhEventsFilter> ahEventsFilterInfo = getAllAhEventsFilter();
			if (null == ahEventsFilterInfo)
			{
				return false;
			}
			else
			{
				QueryUtil.restoreBulkCreateBos(ahEventsFilterInfo);
			}
		}
		catch (Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof SsidProfile) {
				SsidProfile profile = (SsidProfile) bo;
				if (profile.getMacFilters() != null) {
					profile.getMacFilters().size();
				}
				if (profile.getSchedulers() != null) {
					profile.getSchedulers().size();
				}
				if (profile.getRadiusUserProfile() != null) {
					profile.getRadiusUserProfile().size();
				}
				if (profile.getLocalUserGroups() != null) {
					profile.getLocalUserGroups().size();
				}
				if (profile.getGRateSets()!=null){
					profile.getGRateSets().values();
				}
				if (profile.getARateSets()!=null){
					profile.getARateSets().values();
				}
				if (profile.getNRateSets()!=null){
					profile.getNRateSets().values();
				}
				if (profile.getAcRateSets() != null){
					profile.getAcRateSets().size();
				}
			}

			if(bo instanceof HmUser) {
				HmUser user = (HmUser)bo;
// cchen DONE
//				if(user.getLocalUserGroups() != null) {
//					user.getLocalUserGroups().size();
//				}
//
//				if(user.getSsidProfiles() != null) {
//					user.getSsidProfiles().size();
//				}
//
//				if(user.getTableColumns() != null) {
//					user.getTableColumns().size();
//				}
//
//				if(user.getTableSizes() != null) {
//					user.getTableSizes().size();
//				}
//
//				if (user.getAutoRefreshs() != null) {
//					user.getAutoRefreshs().size();
//				}
			}
			
			if (bo instanceof ConfigTemplate) {
				ConfigTemplate profile = (ConfigTemplate) bo;
				if (profile.getMgmtServiceOption() != null) {
					profile.getMgmtServiceOption().getId();
				}
				if (profile.getMgmtServiceDns() != null) {
					profile.getMgmtServiceDns().getId();
				}
				if (profile.getMgmtServiceTime() != null) {
					profile.getMgmtServiceTime().getId();
				}
				if (profile.getOwner() != null) {
					profile.getOwner().getId();
				}
			}

			return null;
		}
	}

	static class ImplQueryBoVlanNetwork implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof SsidProfile) {
				SsidProfile profile = (SsidProfile) bo;
				if (profile.getRadiusUserProfile() != null) {
					profile.getRadiusUserProfile().size();
				}
			}
			if(bo instanceof PortAccessProfile) {
			    PortAccessProfile accProfile = (PortAccessProfile) bo;
			    if(null != accProfile.getNativeVlan()) {
			        accProfile.getNativeVlan().getId();
			    }
			    if(null != accProfile.getDefUserProfile()) {
			        accProfile.getDefUserProfile().getId();
			    }
			    if(null != accProfile.getSelfRegUserProfile()) {
			        accProfile.getSelfRegUserProfile().getId();
			    }
			    if(null != accProfile.getAuthOkUserProfile()) {
			        accProfile.getAuthOkUserProfile().size();
			    }
			}
			if (bo instanceof ConfigTemplate) {
				ConfigTemplate profile = (ConfigTemplate) bo;
				if (profile.getVlanNetwork() != null) {
					profile.getVlanNetwork().size();
					for(ConfigTemplateVlanNetwork cvn :profile.getVlanNetwork()) {
						if (cvn.getVlan()!=null) {
							cvn.getVlan().getId();
						}
						if (cvn.getNetworkObj()!=null) {
							cvn.getNetworkObj().getId();
						}
					}
				}
			}
			return null;
		}
	}

	static class ImplQueryCwp implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof Cwp) {
				Cwp profile = (Cwp) bo;
				CwpPageCustomization pageCustom = profile.getPageCustomization();

				if(pageCustom.getFields() != null) {
					pageCustom.getFields().size();
				}
			}
			return null;
		}
	}

	public static void restoreCwpCert()
	{
		try
		{
			List<CwpCertificate> cwpCertList = getAllCwpCert();
			if(null != cwpCertList && cwpCertList.size() > 0)
			{
				List<Long> lOldId = new ArrayList<Long>();

				for (CwpCertificate cwpCert : cwpCertList) {
					lOldId.add(cwpCert.getId());
				}

				QueryUtil.restoreBulkCreateBos(cwpCertList);

				for(int i=0; i<cwpCertList.size(); i++)
				{
					AhRestoreNewMapTools.setMapCwpCertificate(lOldId.get(i), cwpCertList.get(i).getId());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg("Restore cwp certificate catch exeption", e);
		}
	}

	private static List<CwpCertificate> getAllCwpCert() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of cwp_certificate.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("cwp_certificate");
		if (!restoreRet)
		{
			// tablem cwp_certificate created in 3.2r2 version
//			return getCwpCertList4PreVersion();
			return null;
		}

		/**
		 * No one row data stored in cwp table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<CwpCertificate> boList = new ArrayList<CwpCertificate>();

		for (int i = 0; i < rowCount; i++)
		{
			CwpCertificate cwpCert = new CwpCertificate();

			/**
			 * Set certName
			 */
			String colName = "certName";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"cwp_certificate", colName);
			if (!isColPresent)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp_certificate' data be lost, cause: 'certName' column is not exist.");
				/**
				 * The cwpname column must be exist in the table of cwp
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp_certificate' data be lost, cause: 'certName' column is null.");
				continue;
			}
			cwpCert.setCertName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"cwp_certificate", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			cwpCert.setId(Long.valueOf(id));

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"cwp_certificate", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 2;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'cwp_certificate' data be lost, cause: 'owner' column is  not available.");
				continue;
			}

			cwpCert.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set defaultFlag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "cwp_certificate", colName);
			boolean defaultflag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			if (defaultflag)
			{
				CwpCertificate bo = QueryUtil.findBoByAttribute(CwpCertificate.class, "defaultFlag", true,
						AhRestoreNewMapTools.getHmDomain(ownerId).getId());
				if (bo != null)
				{
					AhRestoreNewMapTools.setMapCwpCertificate(Long.valueOf(id), bo.getId());
					continue;
				}
			}
			cwpCert.setDefaultFlag(defaultflag);

			/**
			 * Set srcCertName
			 */
			colName = "srcCertName";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "cwp_certificate", colName);
			String srcCertName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			cwpCert.setSrcCertName(srcCertName);

			/**
			 * Set srcKeyName
			 */
			colName = "srcKeyName";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "cwp_certificate", colName);
			String srcKeyName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			cwpCert.setSrcKeyName(srcKeyName);

			/**
			 * encrypted
			 */
			colName = "encrypted";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "cwp_certificate", colName);
			boolean isEncrypted = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName));
			cwpCert.setEncrypted(isEncrypted);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "cwp_certificate", colName);
			String description = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			cwpCert.setDescription(description);

			/**
			 * Set index
			 */
			colName = "index";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "cwp_certificate", colName);
			int index = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			cwpCert.setIndex(index);

			boList.add(cwpCert);
		}

		return boList;
	}

//	private static List<CwpCertificate> getCwpCertList4PreVersion() {
//		String domainName = AhRestoreDBTools.HM_RESTORE_DOMAIN.getDomainName();
//		List<CwpCertificate> boList = new ArrayList<CwpCertificate>();
//
//		// CWP Server Key
//		String serverKeyDir = AhDirTools.getCwpServerKeyDir(domainName);
//		List<File> cwpCertFileList = null;
//		try {
//			cwpCertFileList = FileManager.getInstance().getFilesFromFolder(new File(serverKeyDir),
//					false);
//		} catch (FileNotFoundException e) {
//			AhRestoreDBTools.logRestoreMsg(e.getMessage());
//		} catch (IllegalArgumentException e) {
//			AhRestoreDBTools.logRestoreMsg(e.getMessage());
//		} catch (BeNoPermissionException e) {
//			AhRestoreDBTools.logRestoreMsg(e.getMessage());
//		} catch (Exception e) {
//			AhRestoreDBTools.logRestoreMsg(
//					"Read file list from domain cwp cert file folder catch exeption", e);
//		}
//
//		if (cwpCertFileList == null) {
//			AhDirTools.checkDir(serverKeyDir);
//		}
//
//		if (cwpCertFileList == null || cwpCertFileList.size() == 0) {
//			// no cwp cert, create a default cert
//			CwpCertificate defaultCwpCert = createDefaultCwpCert();
//
//			if (defaultCwpCert != null) {
//				boList.add(defaultCwpCert);
//			}
//		} else {
//			// recur cert file list, create bo
//			for (Iterator iter = cwpCertFileList.iterator(); iter.hasNext();) {
//				File certFile = (File) iter.next();
//				if (!certFile.getName().endsWith(".pem")) {
//					continue;
//				}
//				String indexName = certFile.getName().substring(0,
//						certFile.getName().indexOf(".pem"));
//
//				boolean isInt = Pattern.matches("\\d+", indexName);
//				if (!isInt) {
//					continue;
//				}
//
//				int index_ = Integer.parseInt(indexName);
//
//				CwpCertificate cwpCert = new CwpCertificate();
//				cwpCert.setCertName(indexName);
//				cwpCert.setDescription("Old version cwp key file.");
//				cwpCert.setEncrypted(false);
//				cwpCert.setIndex(index_);
//				cwpCert.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);
//				cwpCert.setSrcCertName(certFile.getName());
//				cwpCert.setSrcKeyName(certFile.getName());
//
//				boList.add(cwpCert);
//			}
//
//			// if index 0 cert is empty ,create default cert
//			boolean isEmpty = true;
//			for (CwpCertificate cwpCert : boList) {
//				if (cwpCert.getIndex() == 0) {
//					isEmpty = false;
//					break;
//				}
//			}
//
//			if (isEmpty) {
//				CwpCertificate defaultCwpCert = createDefaultCwpCert();
//
//				if (defaultCwpCert != null) {
//					boList.add(defaultCwpCert);
//				}
//			}
//		}
//
//		return boList;
//	}

//	private static CwpCertificate createDefaultCwpCert() {
//		try {
//			String domainName = AhRestoreDBTools.HM_RESTORE_DOMAIN.getDomainName();
//			BeOperateHMCentOSImpl.createDefaultDomainCwp(domainName);
//
//			// create bo for relation
//			CwpCertificate cwpCert = new CwpCertificate();
//			cwpCert.setCertName("HM-default-CWPCert");
//			cwpCert.setDescription("Default cwp key file.");
//			cwpCert.setEncrypted(false);
//			cwpCert.setIndex(0);
//			cwpCert.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);
//			cwpCert.setSrcCertName("HM-default-CWPCert");
//			cwpCert.setSrcKeyName("HM-default-CWPCert");
//			cwpCert.setDefaultFlag(true);
//
//			return cwpCert;
//		} catch (Exception e) {
//			AhRestoreDBTools.logRestoreMsg("create default cwp cert catch exception", e);
//			return null;
//		}
//	}

	public static boolean adjustPpskServerOfSsidProfile() {
		try {
			if (AhRestoreNewMapTools.getMapPpskServerHiveAp().size() > 0) {
				List<SsidProfile> ssidProfiles = new ArrayList<SsidProfile>();
				for (Long hiveApId : AhRestoreNewMapTools.getMapPpskServerHiveAp().keySet()) {
					Long newHiveApId = AhRestoreNewMapTools.getMapHiveAP(hiveApId);
					if (newHiveApId != null) {
						HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, newHiveApId);
						if (hiveAp != null) {
							Set<Long> ssids = AhRestoreNewMapTools.getPpskServersOfHiveAp(hiveApId);
							for (Long ssid : ssids) {
								Long newSsidId = AhRestoreNewMapTools.getMapSsid(ssid);
								SsidProfile ssidProfile = QueryUtil.findBoById(SsidProfile.class, newSsidId);
								if (ssidProfile != null) {
									ssidProfile.setPpskServer(hiveAp);
									ssidProfiles.add(ssidProfile);
								}
							}
						}
					}
				}
				if (ssidProfiles.size() > 0) {
					try {
						QueryUtil.bulkUpdateBos(ssidProfiles);
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg("Failed to adjust ppsk server of ssid profile.");
						return false;
					}
				}
			}
			adjustPpskServerOfSsidProfileFromIp();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg("Failed to restore ppsk server from ppsk server ip.");
		}
		return true;
	}

	private static boolean adjustPpskServerOfSsidProfileFromIp() throws AhRestoreColNotExistException,AhRestoreException {
		List<SsidProfile> ssids = getAllSsidProfileOnlyWithPpskServerIp();
		List<SsidProfile> bulkSsids = new ArrayList<SsidProfile>();
		SsidProfile ssidBo;
		if (ssids != null && ssids.size() > 0) {
			for (SsidProfile ssidProfile : ssids) {
				ssidBo = QueryUtil.findBoById(SsidProfile.class, ssidProfile.getId());
				if (ssidBo != null) {
					if (ssidProfile.getPpskServerIp() != null && !"".equals(ssidProfile.getPpskServerIp().trim())) {
						HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class, "cfgIpAddress", ssidProfile.getPpskServerIp(), ssidProfile.getOwner().getId());
						if (hiveAp != null) {
							ssidBo.setPpskServer(hiveAp);
							bulkSsids.add(ssidBo);
						}
					}
				}
			}
			try {
				QueryUtil.bulkUpdateBos(bulkSsids);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("Failed to adjust ppsk server of ssid profile from older ppsk server ip.");
				return false;
			}
		}
		return true;
	}

	private static List<SsidProfile> getAllSsidProfileOnlyWithPpskServerIp() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of ssid_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ssid_profile");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in ssid_profile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<SsidProfile> ssidProfileInfo = new ArrayList<SsidProfile>();
		boolean isColPresent;
		String colName;
		SsidProfile ssidProfileDTO;

		for (int i = 0; i < rowCount; i++)
		{
			ssidProfileDTO = new SsidProfile();

			/**
			 * Set ssidname
			 */
			colName = "ssidname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			if (!isColPresent)
			{
				/**
				 * The ssidname column must be exist in the table of ssid_profile
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
				|| name.trim().equalsIgnoreCase("null"))
			{
				continue;
			}
			ssidProfileDTO.setSsidName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";

			if (AhRestoreNewMapTools.getMapSsid(Long.valueOf(id)) == null) {
				continue;
			} else {
				ssidProfileDTO.setId(AhRestoreNewMapTools.getMapSsid(Long.valueOf(id)));
			}

			/**
			 * get PPSK_SERVER_ID to generate map
			 */
			colName = "ppsk_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"ssid_profile", colName);
			String ppskServerIdStr = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (ppskServerIdStr != null && !"".equals(ppskServerIdStr.trim()) && !"null".equalsIgnoreCase(ppskServerIdStr.trim())) {
				continue;
			}

			/**
			 * Set ppskServerIp
			 */
			colName = "ppskserverip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ssid_profile", colName);
			String ppskServerIp = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (ppskServerIp == null || "".equals(ppskServerIp.trim()) || "null".equalsIgnoreCase(ppskServerIp.trim())) {
				continue;
			}
			ssidProfileDTO.setPpskServerIp(ppskServerIp);

			/*
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"ssid_profile", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			ssidProfileDTO.setOwner(ownerDomain);

			ssidProfileInfo.add(ssidProfileDTO);
		}

		return ssidProfileInfo;
	}

	public static void updateMgmtService(){
		try {
			List<ConfigTemplate> bos =  QueryUtil.executeQuery(ConfigTemplate.class,null,null,null,new ImplQueryBo());
			if(!bos.isEmpty()){
				for(ConfigTemplate ct:bos){
					List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null,
							new FilterParams("owner.id = :s1",new Object[] {ct.getOwner().getId()}));
					if(!list.isEmpty()){
						if(null == ct.getMgmtServiceOption()){
							MgmtServiceOption mgmtServiceOption = QueryUtil.findBoByAttribute(MgmtServiceOption.class, "mgmtName",list.get(0).getNetworkName(), ct.getOwner().getId());
							ct.setMgmtServiceOption(mgmtServiceOption);
						}
						if(null  == ct.getMgmtServiceDns()){
							MgmtServiceDns msDns = QueryUtil.findBoByAttribute(MgmtServiceDns.class, "mgmtName", list.get(0).getNetworkName(), ct.getOwner().getId());
							ct.setMgmtServiceDns(msDns);
						}
						if(null == ct.getMgmtServiceTime()){
							MgmtServiceTime msTime = QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName",list.get(0).getNetworkName(), ct.getOwner().getId());
							ct.setMgmtServiceTime(msTime);
						}
					}

				}
				QueryUtil.bulkUpdateBos(bos);
		}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Update management option of configTemplate error.", e);
		}
	}

	public static void updateNetworks(){
		try {
            List<VpnNetwork> vpnNetworks = QueryUtil.executeQuery(
                    VpnNetwork.class, null, null, null, new QueryBo() {
                        @Override
                        public Collection<HmBo> load(HmBo bo) {
                            if (bo instanceof VpnNetwork) {
                                VpnNetwork network = (VpnNetwork) bo;
                                if (network.getOwner() != null)
                                    network.getOwner().getId();
                            }
                            return null;
                        }
                    });
			if(!vpnNetworks.isEmpty()){
				List<VpnNetwork> needUpdateList = new ArrayList<VpnNetwork>();
				for(VpnNetwork vn:vpnNetworks){
					boolean updateFlag = true;
					if(!ConfigurationUtils.getRelevantHiveAp(vn).isEmpty()){
						updateFlag = false;
					}
					if(updateFlag){
						needUpdateList.add(vn);
					}
				}
				if(!needUpdateList.isEmpty()){
					// for remove network object in user profile
					for(VpnNetwork vnNetwork : needUpdateList){
						List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null,
								new FilterParams("owner.id = :s1",new Object[] {vnNetwork.getOwner().getId()}));
						if(!list.isEmpty()){
							if(vnNetwork.getNetworkName().equals(list.get(0).getNetworkName())){
								List<?> vlanName =QueryUtil.executeNativeQuery("select distinct b.vlanname from config_template_vlannetwork a, vlan b where a.vlan_id=b.id and a.vpn_network_id=" + vnNetwork.getId());
								if(!vlanName.isEmpty() && "4094".equals(vlanName.get(0).toString())){
									vnNetwork.setNetworkName(BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT);
									Vlan vlan = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
									List<ConfigTemplate> listCf = QueryUtil.executeQuery(ConfigTemplate.class, null, null, vnNetwork.getOwner().getId(), new ImplQueryBoVlanNetwork());
									List<ConfigTemplate> needUpdateCTList = new ArrayList<ConfigTemplate>();
									for(ConfigTemplate ct: listCf){
										if (ct.getVlanNetwork()!=null) {
											boolean needUpFlg = false;
											for(ConfigTemplateVlanNetwork cvn :ct.getVlanNetwork()){
												if (cvn.getNetworkObj().getId().equals(vnNetwork.getId())){
													cvn.setVlan(vlan);
													needUpFlg=true;
												}
											}
											if (needUpFlg) {
												needUpdateCTList.add(ct);
											}
										}
									}
									if (!needUpdateCTList.isEmpty()) {
										QueryUtil.bulkUpdateBos(needUpdateCTList);
									}
								}
							}else if(vnNetwork.getNetworkName().equals(BeParaModule.PRE_DEFINED_VPN_NETWORK_OBJECT_NAME)){
								vnNetwork.setNetworkName(BeParaModule.PRE_DEFINED_VPN_NETWORK_FOR_USERPROFILE);
								Vlan vlan = QueryUtil.findBoByAttribute(Vlan.class, "vlanName", "2", vnNetwork.getOwner().getId());
								if (null == vlan) {
									Vlan dto_Vlan = new Vlan();
									dto_Vlan.setVlanName("2");
									dto_Vlan.setOwner(vnNetwork.getOwner());
									List<SingleTableItem> items = new ArrayList<SingleTableItem>();
									SingleTableItem single = new SingleTableItem();
									single.setDescription("pre-defined VLAN for Network");
									single.setVlanId(2);
									single.setType(SingleTableItem.TYPE_GLOBAL);
									items.add(single);
									dto_Vlan.setItems(items);
									Long newId = QueryUtil.createBo(dto_Vlan);
									vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class, newId);
								}

								List<ConfigTemplate> listCf = QueryUtil.executeQuery(ConfigTemplate.class, null, null, vnNetwork.getOwner().getId(), new ImplQueryBoVlanNetwork());
								List<ConfigTemplate> needUpdateCTList = new ArrayList<ConfigTemplate>();
								for(ConfigTemplate ct: listCf){
									if (ct.getVlanNetwork()!=null) {
										boolean needUpFlg = false;
										for(ConfigTemplateVlanNetwork cvn :ct.getVlanNetwork()){
											if (cvn.getNetworkObj().getId().equals(vnNetwork.getId())){
												cvn.setVlan(vlan);
												needUpFlg=true;
											}
										}
										if (needUpFlg) {
											needUpdateCTList.add(ct);
										}
									}
								}
								if (!needUpdateCTList.isEmpty()) {
									QueryUtil.bulkUpdateBos(needUpdateCTList);
								}
							}
						}
					}
					QueryUtil.bulkUpdateBos(needUpdateList);
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Update networks of configTemplate error.", e);
		}
	}

	private static Map<String, Set<UserProfileVlanMapping>> getAllUserProfileVlanMappings()
			throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "user_profile_vlan_mapping";

		/**
		 * Check validation of user_profile_vlan_mapping.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<UserProfileVlanMapping>> upVlanMappingInfo = new HashMap<>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set userprofile_id
			 */
			colName = "userprofile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			if (!isColPresent)
			{
				continue;
			}

			String userProfileId = xmlParser.getColVal(i, colName);
			if (userProfileId == null || userProfileId.trim().equals("")
				|| userProfileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set vlan_id
			 */
			colName = "vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			if (!isColPresent)
			{
				continue;
			}

			String vlanId = xmlParser.getColVal(i, colName);
			if (vlanId == null || vlanId.trim().equals("")
				|| vlanId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * get config_template_id
			 */
			colName = "config_template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			if (!isColPresent)
			{
				continue;
			}

			String npId = xmlParser.getColVal(i, colName);
			if (npId == null || npId.trim().equals("")
				|| npId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}

			Long newUserProfileId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userProfileId.trim()));
			UserProfile userProfile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserProfileId);

			Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(vlanId.trim()));
			Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class, newVlanId);

			if (userProfile != null
					&& vlan != null) {
				UserProfileVlanMapping upVlan = new UserProfileVlanMapping();
				upVlan.setUserProfile(userProfile);
				upVlan.setVlan(vlan);
				upVlan.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
				if (!upVlanMappingInfo.containsKey(npId)) {
					Set<UserProfileVlanMapping> upVlanMappingSet = new HashSet<>();
					upVlanMappingInfo.put(npId, upVlanMappingSet);
				}
				upVlanMappingInfo.get(npId).add(upVlan);
			}
		}

		return upVlanMappingInfo;
	}

	private static Map<String, List<ConfigTemplateStormControl>> getAllConfigTemplateStormControl()
			throws AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "CONFIG_TEMPLATE_STORM_CONTROL";

		/**
		 * Check validation of CONFIG_TEMPLATE_STORM_CONTROL.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<ConfigTemplateStormControl>> stormControls = new HashMap<>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			ConfigTemplateStormControl stormControl = new ConfigTemplateStormControl();

			/**
			 * Set CONFIG_TEMPLATE_ID
			 */
			colName = "CONFIG_TEMPLATE_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				/**
				 * The CONFIG_TEMPLATE_ID column must be exist in the table of
				 * CONFIG_TEMPLATE_STORM_CONTROL
				 */
				continue;
			}

			String configTemplateId = xmlParser.getColVal(i, colName);
			if (configTemplateId == null || configTemplateId.trim().equals("")
					|| configTemplateId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set interfaceNum
			 */
			colName = "interfaceNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String interfaceNum = isColPresent? xmlParser.getColVal(i, colName) : "";
			stormControl.setInterfaceNum((short)AhRestoreCommons.convertInt(interfaceNum));

			/**
			 * Set interfaceType
			 */
			colName = "interfaceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String interfaceType = isColPresent? xmlParser.getColVal(i, colName) : "";
			stormControl.setInterfaceType(AhRestoreCommons.convertString(interfaceType));

			/**
			 * Set allTrafficType
			 */
			colName = "allTrafficType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String allTrafficType = isColPresent? xmlParser.getColVal(i, colName) : "false";
			stormControl.setAllTrafficType(AhRestoreCommons.convertStringToBoolean(allTrafficType));

			/**
			 * Set broadcast
			 */
			colName = "broadcast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String broadcast = isColPresent? xmlParser.getColVal(i, colName) : "false";
			stormControl.setBroadcast(AhRestoreCommons.convertStringToBoolean(broadcast));

			/**
			 * Set unknownUnicast
			 */
			colName = "unknownUnicast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String unknownUnicast = isColPresent? xmlParser.getColVal(i, colName) : "false";
			stormControl.setUnknownUnicast(AhRestoreCommons.convertStringToBoolean(unknownUnicast));

			/**
			 * Set multicast
			 */
			colName = "multicast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String multicast = isColPresent? xmlParser.getColVal(i, colName) : "false";
			stormControl.setMulticast(AhRestoreCommons.convertStringToBoolean(multicast));

			/**
			 * Set tcpsyn
			 */
			colName = "tcpsyn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tcpsyn = isColPresent? xmlParser.getColVal(i, colName) : "false";
			stormControl.setTcpsyn(AhRestoreCommons.convertStringToBoolean(tcpsyn));

			/**
			 * Set rateLimitType
			 */
			colName = "rateLimitType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String rateLimitType = isColPresent? xmlParser.getColVal(i, colName) : "0";
			stormControl.setRateLimitType(AhRestoreCommons.convertLong(rateLimitType));


			/**
			 * Set rateLimitValue
			 */
			colName = "rateLimitValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String rateLimitValue = isColPresent? xmlParser.getColVal(i, colName) : "0";
			if(RestoreHiveAp.restore_from_fuji_before
					&& AhRestoreCommons.convertLong(rateLimitType) == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
				//bps --> kbps
				long value = AhRestoreCommons.convertLong(rateLimitValue);
				stormControl.setRateLimitValue(value%1000 == 0 ? value/1000 : value/1000+1);
			} else {
				stormControl.setRateLimitValue(AhRestoreCommons
						.convertLong(rateLimitValue));
			}

			if (stormControls.get(configTemplateId) == null) {
				List<ConfigTemplateStormControl> addList = new ArrayList<ConfigTemplateStormControl>();
				addList.add(stormControl);
				stormControls.put(configTemplateId, addList);
			} else {
				stormControls.get(configTemplateId).add(stormControl);
			}
		}

		return stormControls;
	}

	private static void prepareNpRelatedUserProfileVlanMapping(Set<UserProfileVlanMapping> mappings, ConfigTemplate ct) {
		if (mappings != null
				&& !mappings.isEmpty()) {
			for (UserProfileVlanMapping upVlan : mappings) {
				upVlan.setNetworkPolicy(ct);
			}
		}
	}

	public static Map<String, SwitchSettings> getAllSwitchSettings (Map<String, StpSettings> allStpSettings) throws AhRestoreException, AhRestoreColNotExistException{
		Map<String, SwitchSettings> mapSwitchSettings = new HashMap<String,SwitchSettings>();

		String tableName = "switch_settings";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile(tableName);

	     if (!restoreRet) {
	         AhRestoreDBTools
	                 .logRestoreMsg("SAXReader cannot read switch_settings.xml file.");
	         return null;
	     }

	     int rowCount = xmlParser.getRowCount();

	     boolean isColPresent;
	     String colName;

	     for (int i = 0; i < rowCount; i++) {
	    	 SwitchSettings switchSettings = new SwitchSettings();
	    	 colName = "id";
             isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                     tableName, colName);
             String switch_id = isColPresent ? xmlParser.getColVal(i, colName) : "";

             if ("".equalsIgnoreCase(switch_id.trim()) || switch_id.trim().equalsIgnoreCase("NULL")){
            	 AhRestoreDBTools.logRestoreMsg("Restore table 'switch_settings' data be lost, cause: 'id' column value is null.");
                 continue;
             }

	         /**
				 * Set enableIgmpSnooping
				 */
				colName = "enableIgmpSnooping";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String enableIgmpSnooping = isColPresent ? xmlParser.getColVal(i,colName) : "true";
				switchSettings.setEnableIgmpSnooping(AhRestoreCommons.convertStringToBoolean(enableIgmpSnooping));


				/**
				 * Set enableImmediateLeave
				 */
				colName = "enableImmediateLeave";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String enableImmediateLeave = isColPresent ? xmlParser.getColVal(i,colName) : "true";
				switchSettings.setEnableImmediateLeave(AhRestoreCommons.convertStringToBoolean(enableImmediateLeave));

				/**
				 * Set enableReportSuppression
				 */
				colName = "enableReportSuppression";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String enableReportSuppression = isColPresent ? xmlParser.getColVal(i,colName) : "true";
				switchSettings.setEnableReportSuppression(AhRestoreCommons.convertStringToBoolean(enableReportSuppression));

				/**
				 * Set globalDelayLeaveQueryInterval
				 */
				colName = "globalDelayLeaveQueryInterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				String globalDelayLeaveQueryInterval = isColPresent ? xmlParser.getColVal(i, colName) : "1";
				switchSettings.setGlobalDelayLeaveQueryInterval(Integer.valueOf(globalDelayLeaveQueryInterval));
				/**
				 * Set globalDelayLeaveQueryCount
				 */
				colName = "globalDelayLeaveQueryCount";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				String globalDelayLeaveQueryCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
				switchSettings.setGlobalDelayLeaveQueryCount(Integer.valueOf(globalDelayLeaveQueryCount));
				/**
				 * Set globalRouterPortAginTime
				 */
				colName = "globalRouterPortAginTime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				String globalRouterPortAginTime = isColPresent ? xmlParser.getColVal(i, colName) : "250";
				switchSettings.setGlobalRouterPortAginTime(Integer.valueOf(globalRouterPortAginTime));
				/**
				 * Set globalRobustnessCount
				 */
				colName = "globalRobustnessCount";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				String globalRobustnessCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
				switchSettings.setGlobalRobustnessCount(Integer.valueOf(globalRobustnessCount));

				colName = "owner";
				  isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
				  long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

				  if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				  {
					  BeLogTools.restoreLog(BeLogTools.DEBUG, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is  not available.");
					  continue;
				  }

				 switchSettings.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				 colName = "stp_settings_id";
				 isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
				 String stp_settings_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
				 if(!("".equalsIgnoreCase(stp_settings_id)) || "NULL".equalsIgnoreCase(stp_settings_id)){
					 if(allStpSettings != null && !allStpSettings.isEmpty()){
						 StpSettings stpSettings = allStpSettings.get(stp_settings_id);
						 switchSettings.setStpSettings(stpSettings);
					 }
			     }

	            mapSwitchSettings.put(switch_id, switchSettings);
	     }
		return mapSwitchSettings;
	}

	public static Map<String, StpSettings> getAllStpSettings() throws AhRestoreException, AhRestoreColNotExistException{
		Map<String, StpSettings> stp_list = new HashMap<String,StpSettings>();

		String tableName = "stp_settings";
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean restoreRet = xmlParser.readXMLFile(tableName);

	     if (!restoreRet) {
	         AhRestoreDBTools
	                 .logRestoreMsg("SAXReader cannot read stp_settings.xml file.");
	         return null;
	     }

	     int rowCount = xmlParser.getRowCount();

	     boolean isColPresent;
	     String colName;

	     for(int i = 0; i < rowCount; i ++){
	    	 StpSettings stpSettings = new StpSettings();

	    	 colName="id";
	    	 isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
	                 tableName, colName);
	         String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

	         if("".equals(id) || "NULL".equalsIgnoreCase(id)) {
				continue;
			 }

	         colName = "enablestp";
	         isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
	                 tableName, colName);
	         String enableStp = isColPresent ? xmlParser.getColVal(i, colName)
	                 : "false";
	         stpSettings.setEnableStp(AhRestoreCommons.convertStringToBoolean(enableStp));

	         colName="stp_mode";
	         isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
	                 tableName, colName);
	         String stp_mode = isColPresent ? xmlParser.getColVal(i, colName) : Short.toString(StpSettings.STP_MODE_STP);
	         stpSettings.setStp_mode((short)AhRestoreCommons.convertInt(stp_mode));


	         colName="mstp_region_id";
	         isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
	                 tableName, colName);
	         String mstp_region_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

	         if(!("".equalsIgnoreCase(mstp_region_id)) || "null".equalsIgnoreCase(mstp_region_id)){
		        stpSettings.setMstpRegion(AhRestoreNewMapTools.getMapMstpRegions(AhRestoreCommons.convertString2Long(mstp_region_id)));
	         }

	         colName = "owner";
			 isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
			 long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			 if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			 {
				 BeLogTools.restoreLog(BeLogTools.DEBUG, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is  not available.");
				 continue;
			 }
			 stpSettings.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

             stp_list.put(id, stpSettings);
	     }
		return stp_list;
	}
}