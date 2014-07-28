package com.ah.be.admin.restoredb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.DeviceMstpInstancePriority;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.ForwardingDB;
import com.ah.bo.hiveap.HiveAPVirtualConnection;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApDynamicRoute;
import com.ah.bo.hiveap.HiveApEth;
import com.ah.bo.hiveap.HiveApInternalNetwork;
import com.ah.bo.hiveap.HiveApIpRoute;
import com.ah.bo.hiveap.HiveApL3cfgNeighbor;
import com.ah.bo.hiveap.HiveApLearningMac;
import com.ah.bo.hiveap.HiveApMultipleVlan;
import com.ah.bo.hiveap.HiveApPreferredSsid;
import com.ah.bo.hiveap.HiveApSsidAllocation;
import com.ah.bo.hiveap.HiveApStaticRoute;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.hiveap.InterfaceMstpSettings;
import com.ah.bo.hiveap.InterfaceStpSettings;
import com.ah.bo.hiveap.USBModemProfile;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.hiveap.HiveApAction;

public class RestoreHiveAp {

	// for restore Ethernet Access used
	private static Map<String, String> ap_template_map;
	private static Map<String, MapLeafNode> leafNodes;
	private static Map<String, String> second_vpn_gateway_map;
	private static String nmsName = NmsUtil.getOEMCustomer().getNmsName();
	private static String apName = NmsUtil.getOEMCustomer()
			.getAccessPonitName();
	public static boolean restore_from_50r3_before = isRestoreHmBeforeVersion("5.0.3.0");
	public static boolean restore_from_50r0_after = !isRestoreHmBeforeVersion("5.0.0.0");
	public static boolean restore_from_50r0_before = isRestoreHmBeforeVersion("5.0.0.0");
	public static boolean restore_from_50r4_before = isRestoreHmBeforeVersion("5.0.4.0");
	public static boolean restore_from_51r6_before = isRestoreHmBeforeVersion("5.1.6.0");
	public static boolean restore_from_60r1_before = isRestoreHmBeforeVersion("6.0.1.0");
	public static boolean restore_from_60r2_before = isRestoreHmBeforeVersion("6.0.2.0");
	public static boolean restore_from_fuji_before = isRestoreHmBeforeVersion("6.1.1.0");
	public static boolean restore_from_geneva_before = isRestoreHmBeforeVersion("6.1.2.0");
	public static boolean restore_from_glasgow_before = isRestoreHmBeforeVersion("6.1.3.0");
	public static boolean restore_from_gotham_before = isRestoreHmBeforeVersion("6.1.5.0");

	private static List<HiveAp> getAllHiveAp(String strTableName) throws Exception {
		List<HiveAp> hiveApInfo = new ArrayList<HiveAp>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_ap.xml
		 */
		//xmlParser.convertXMLfile("hive_ap");
		boolean restoreRet = xmlParser.readXMLOneFile(strTableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hive_ap table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		//AhRestoreDBTools.logRestoreMsg("Total " + apName + " count is :"
		//		+ rowCount);

		boolean isColPresent;
		String colName;
		HiveAp hiveAp;
		HiveApEth eth0;
		HiveApEth eth1;
		HiveApEth red0;
		HiveApEth agg0;
		HiveApWifi wifi0;
		HiveApWifi wifi1;

		for (int i = 0; i < rowCount; i++) {
			hiveAp = new HiveAp();
			eth0 = hiveAp.getEth0();
			eth1 = hiveAp.getEth1();
			red0 = hiveAp.getRed0();
			agg0 = hiveAp.getAgg0();
			wifi0 = hiveAp.getWifi0();
			wifi1 = hiveAp.getWifi1();
			
			/** HM Upgrade deivce must mismatch */
			hiveAp.setPending(true);
			hiveAp.setPendingIndex(ConfigurationResources.CONFIG_HIVEAP_HM_UPGRADE);

			/**
			 * Set hostName
			 */
			colName = "hostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'hostName' column is not exist.");
				/**
				 * The hostName column must be exist in the table of hive_ap
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'hostName' column value is null.");
				continue;
			}
			hiveAp.setHostName(name.trim());

			/**
			 * Set macaddress
			 */
			colName = "macaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'macaddress' column is not exist.");
				/**
				 * The macaddress column must be exist in the table of hive_ap
				 */
				continue;
			}

			String macaddress = xmlParser.getColVal(i, colName);
			if (macaddress == null || macaddress.trim().equals("")
					|| macaddress.trim().equalsIgnoreCase("null")) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'macaddress' column value is null.");
				continue;
			}
			hiveAp.setMacAddress(macaddress.trim().toUpperCase());

			/**
			 * Set managestatus
			 */
			colName = "managestatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String managestatus = isColPresent ? xmlParser
					.getColVal(i, colName) : String.valueOf(HiveAp.STATUS_NEW);
			int managedStatus = AhRestoreCommons.convertInt(managestatus);
			if (managedStatus != HiveAp.STATUS_NEW
					&& managedStatus != HiveAp.STATUS_MANAGED
					&& managedStatus != HiveAp.STATUS_PRECONFIG) {
				AhRestoreDBTools.logRestoreMsg("It's not " + apName + ":"
						+ hiveAp.getMacAddress() + ", managed status is:"
						+ managedStatus + ", ingore it.");
				continue;
			} else {
				hiveAp.setManageStatus((short) managedStatus);
			}

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			if (!isColPresent) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of hive_ap
				 */
				continue;
			}
			String id = xmlParser.getColVal(i, colName);
			if (id == null || id.trim().equals("")
					|| id.trim().equalsIgnoreCase("null")) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'id' column value is null.");
				continue;
			}
			hiveAp.setId(Long.valueOf(id));

			/**
			 * Set owner
			 */

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 0;

			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools
						.debug(HmLogConst.M_RESTORE,
								"Restore table 'hive_ap' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == ownerDomain)
				continue;
			hiveAp.setOwner(ownerDomain);

			/**
			 * Set addressOnly
			 */
			colName = "addressOnly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String addressOnly = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setAddressOnly(AhRestoreCommons
					.convertStringToBoolean(addressOnly));

			/**
			 * Set adminpassword
			 */
			colName = "adminpassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String adminpassword = isColPresent ? xmlParser.getColVal(i,
					colName) : NmsUtil.getOEMCustomer().getDefaultAPPassword();
			hiveAp.setAdminPassword(AhRestoreCommons
					.convertString(adminpassword));

			/**
			 * Set adminuser
			 */
			colName = "adminuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String adminuser = isColPresent ? xmlParser.getColVal(i, colName)
					: "admin";
			hiveAp.setAdminUser(AhRestoreCommons.convertString(adminuser));

			/**
			 * Set capwaplinkip
			 */
			colName = "capwaplinkip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String capwaplinkip = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setCapwapLinkIp(AhRestoreCommons.convertString(capwaplinkip));

			/**
			 * Set capwapclientip
			 */
			colName = "capwapclientip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String capwapclientip = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setCapwapClientIp(AhRestoreCommons
					.convertString(capwapclientip));

			/**
			 * Set cfgadminuser
			 */
			colName = "cfgadminuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgadminuser = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setCfgAdminUser(AhRestoreCommons.convertString(cfgadminuser));

			/**
			 * Set cfggateway
			 */
			colName = "cfggateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfggateway = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setCfgGateway(AhRestoreCommons.convertString(cfggateway));

			/**
			 * Set cfgipaddress
			 */
			colName = "cfgipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgipaddress = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setCfgIpAddress(AhRestoreCommons.convertString(cfgipaddress));

			/**
			 * Set cfgnetmask
			 */
			colName = "cfgnetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgnetmask = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setCfgNetmask(AhRestoreCommons.convertString(cfgnetmask));

			/**
			 * Set cfgpassword
			 */
			colName = "cfgpassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgpassword = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setCfgPassword(AhRestoreCommons.convertString(cfgpassword));

			/**
			 * Set cfgreadonlypassword
			 */
			colName = "cfgreadonlypassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgreadonlypassword = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setCfgReadOnlyPassword(AhRestoreCommons
					.convertString(cfgreadonlypassword));

			/**
			 * Set cfgreadonlyuser
			 */
			colName = "cfgreadonlyuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cfgreadonlyuser = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setCfgReadOnlyUser(AhRestoreCommons
					.convertString(cfgreadonlyuser));

			/**
			 * Set configVer
			 */
			colName = "configVer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String configVer = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			hiveAp.setConfigVer(AhRestoreCommons.convertInt(configVer));

			/**
			 * Set classificationtag1
			 */
			colName = "classificationtag1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String classificationtag1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setClassificationTag1(AhRestoreCommons
					.convertString(classificationtag1));

			/**
			 * Set classificationtag2
			 */
			colName = "classificationtag2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String classificationtag2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setClassificationTag2(AhRestoreCommons
					.convertString(classificationtag2));

			/**
			 * Set classificationtag3
			 */
			colName = "classificationtag3";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String classificationtag3 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setClassificationTag3(AhRestoreCommons
					.convertString(classificationtag3));

			/**
			 * Set distributedPriority
			 */
			colName = "distributedpriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String distributedpriority = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(HiveAp.DISTRIBUTED_PRIORITY_DEFAULT);
			hiveAp.setDistributedPriority((short) AhRestoreCommons
					.convertInt(distributedpriority));

			/**
			 * Set connchangedtime
			 */
			colName = "connchangedtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String connchangedtime = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setConnChangedTime(AhRestoreCommons
					.convertString2Long(connchangedtime));

			/**
			 * Set connected
			 */
			// colName = "connected";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String connected = isColPresent ? xmlParser.getColVal(i, colName)
			// : "false";
			// Always should be disconnected when restore.
			hiveAp.setConnected(false);

			/**
			 * Set delayTime
			 */
			hiveAp.setDelayTime(-1);

			/**
			 * Set disconnChangedTime
			 */
			colName = "disconnChangedTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String disconnTimeStr = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(System.currentTimeMillis());
			hiveAp.setDisconnChangedTime(AhRestoreCommons
					.convertInt(disconnTimeStr));

			/**
			 * Set countrycode
			 */
			colName = "countrycode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String countrycode = isColPresent ? xmlParser.getColVal(i, colName)
					: "840";
			hiveAp.setCountryCode(AhRestoreCommons.convertInt(countrycode));

			/*
			 * Set currentDtlsEnable
			 */
			colName = "currentDtlsEnable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String currentDtlsEnable = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setCurrentDtlsEnable(AhRestoreCommons
					.convertStringToBoolean(currentDtlsEnable));

			/*
			 * Set currentkeyid
			 */
			colName = "currentkeyid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String currentkeyid = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			hiveAp.setCurrentKeyId(AhRestoreCommons.convertInt(currentkeyid));

			/*
			 * Set enabledSameVlan
			 */
			colName = "enabledSameVlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enabledSameVlan = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setEnabledSameVlan(AhRestoreCommons
					.convertStringToBoolean(enabledSameVlan));

			if(restore_from_glasgow_before
					&& (HiveAp.is330HiveAP(hiveAp.getHiveApModel()) || HiveAp.is350HiveAP(hiveAp.getHiveApModel()))
					&& (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth())){
				hiveAp.setEnabledSameVlan(true);
			}
			
			/*
			 * Set currentpassphrase
			 */
			colName = "currentpassphrase";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String currentpassphrase = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setCurrentPassPhrase(AhRestoreCommons
					.convertString(currentpassphrase));

			/*
			 * Set dhcp
			 */
			colName = "dhcp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String dhcp = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			hiveAp.setDhcp(AhRestoreCommons.convertStringToBoolean(dhcp));

			/**
			 * Set dhcpFallback
			 */
			colName = "dhcpFallback";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String dhcpFallback = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setDhcpFallback(AhRestoreCommons
					.convertStringToBoolean(dhcpFallback));

			/**
			 * Set dhcpTimeout
			 */
			colName = "dhcpTimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String dhcpTimeout = isColPresent ? xmlParser.getColVal(i, colName)
					: "20";
			hiveAp.setDhcpTimeout(AhRestoreCommons.convertInt(dhcpTimeout));

			/**
			 * Set discoverytime
			 */
			colName = "discoverytime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String discoverytime = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setDiscoveryTime(AhRestoreCommons
					.convertString2Long(discoverytime));

			/**
			 * Set enableMDM
			 */
			colName = "enableMDM";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			String enableMDM = isColPresent ? xmlParser.getColVal(i, colName) : "";
			hiveAp.setEnableMDM(AhRestoreCommons.convertStringToBoolean(enableMDM));

            colName = "configmdm_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
            		"hive_ap", colName);
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
                hiveAp.setConfigTemplateMdm(config);
            }
            }



			/*
			 * Set nativevlan
			 */
			colName = "nativevlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			int nativevlan = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : 0;
			hiveAp.setNativeVlan(nativevlan > 4094 ? 4094 : nativevlan);

			/*
			 * Set mgtvlan
			 */
			colName = "mgtvlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			int mgtvlan = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0;
			hiveAp.setMgtVlan(mgtvlan > 4094 ? 4094 : mgtvlan);

			/*
			 * set hiveapmodel
			 */
			colName = "hiveapmodel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String hiveApModel = isColPresent ? xmlParser.getColVal(i, colName)
					: "1";
			hiveAp.setHiveApModel((short) AhRestoreCommons
					.convertInt(hiveApModel));

			/*
			 * set fields for eth0
			 * ==============================================================
			 */
			/*
			 * Set eth0_admin_state
			 */
			colName = "eth0_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			eth0.setAdminState((short) AhRestoreCommons
					.convertInt(eth0_admin_state));

			/*
			 * Set eth0_operation_mode
			 */
			colName = "eth0_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_operation_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
			eth0.setOperationMode((short) AhRestoreCommons
					.convertInt(eth0_operation_mode));

			/*
			 * Set eth0_duplex
			 */
			colName = "eth0_duplex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_duplex = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(AhInterface.ETH_DUPLEX_AUTO);
			eth0.setDuplex((short) AhRestoreCommons.convertInt(eth0_duplex));

			/*
			 * Set eth0_speed
			 */
			colName = "eth0_speed";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_speed = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(AhInterface.ETH_SPEED_AUTO);
			eth0.setSpeed((short) AhRestoreCommons.convertInt(eth0_speed));

			/*
			 * Set eth0_bind_interface
			 */
			colName = "eth0_bind_interface";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_bind_interface = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ETH_BIND_IF_NULL);
			eth0.setBindInterface((short) AhRestoreCommons
					.convertInt(eth0_bind_interface));

			/*
			 * Set eth0_bind_role
			 */
			colName = "eth0_bind_role";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_bind_role = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ETH_BIND_ROLE_NULL);
			eth0.setBindRole((short) AhRestoreCommons
					.convertInt(eth0_bind_role));

			/*
			 * Set eth0_learning_enabled
			 */
			colName = "eth0_learning_enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_learning_enabled = isColPresent ? xmlParser.getColVal(
					i, colName) : "true";
			eth0.setMacLearningEnabled(AhRestoreCommons
					.convertStringToBoolean(eth0_learning_enabled));

			/*
			 * Set eth0_idel_timeout
			 */
			colName = "eth0_idel_timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_idel_timeout = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveApEth.DEFAULT_IDEL_TIMEOUT);
			eth0.setIdelTimeout(AhRestoreCommons.convertInt(eth0_idel_timeout));

			/*
			 * Set eth0_allowed_vlan
			 */
			colName = "eth0_allowed_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_allowed_vlan = isColPresent ? xmlParser.getColVal(i,
					colName) : HiveApEth.ALLOWED_VLAN_ALL;
			eth0.setAllowedVlan(AhRestoreCommons
					.convertString(eth0_allowed_vlan));

			/*
			 * Set eth0_multinative_vlan
			 */
			colName = "eth0_multinative_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_multinative_vlan = isColPresent ? xmlParser.getColVal(
					i, colName) : "";
			try {
				Integer vlanInte = Integer.valueOf(eth0_multinative_vlan);
				eth0.setMultiNativeVlan(vlanInte);
			} catch (Exception ex) {
				eth0.setMultiNativeVlan(null);
			}

			/*
			 * set fields for eth1
			 * ==============================================================
			 */
			/*
			 * Set eth1_admin_state
			 */
			colName = "eth1_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			eth1.setAdminState((short) AhRestoreCommons
					.convertInt(eth1_admin_state));

			/*
			 * Set eth1_operation_mode
			 */
			colName = "eth1_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_operation_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
			eth1.setOperationMode((short) AhRestoreCommons
					.convertInt(eth1_operation_mode));

			/*
			 * Set eth1_duplex
			 */
			colName = "eth1_duplex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_duplex = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(AhInterface.ETH_DUPLEX_AUTO);
			eth1.setDuplex((short) AhRestoreCommons.convertInt(eth1_duplex));

			/*
			 * Set eth1_speed
			 */
			colName = "eth1_speed";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_speed = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(AhInterface.ETH_SPEED_AUTO);
			eth1.setSpeed((short) AhRestoreCommons.convertInt(eth1_speed));

			/*
			 * Set eth1_bind_interface
			 */
			colName = "eth1_bind_interface";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_bind_interface = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ETH_BIND_IF_NULL);
			eth1.setBindInterface((short) AhRestoreCommons
					.convertInt(eth1_bind_interface));

			/*
			 * Set eth1_bind_role
			 */
			colName = "eth1_bind_role";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_bind_role = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ETH_BIND_ROLE_NULL);
			eth1.setBindRole((short) AhRestoreCommons
					.convertInt(eth1_bind_role));

			/*
			 * Set eth1_learning_enabled
			 */
			colName = "eth1_learning_enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_learning_enabled = isColPresent ? xmlParser.getColVal(
					i, colName) : "true";
			eth1.setMacLearningEnabled(AhRestoreCommons
					.convertStringToBoolean(eth1_learning_enabled));

			/*
			 * Set eth1_idel_timeout
			 */
			colName = "eth1_idel_timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_idel_timeout = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveApEth.DEFAULT_IDEL_TIMEOUT);
			eth1.setIdelTimeout(AhRestoreCommons.convertInt(eth1_idel_timeout));

			/*
			 * Set eth1_allowed_vlan
			 */
			colName = "eth1_allowed_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_allowed_vlan = isColPresent ? xmlParser.getColVal(i,
					colName) : HiveApEth.ALLOWED_VLAN_ALL;
			eth1.setAllowedVlan(AhRestoreCommons
					.convertString(eth1_allowed_vlan));

			/*
			 * Set eth1_multinative_vlan
			 */
			colName = "eth1_multinative_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_multinative_vlan = isColPresent ? xmlParser.getColVal(
					i, colName) : "";
			try {
				Integer vlanInte1 = Integer.valueOf(eth1_multinative_vlan);
				eth1.setMultiNativeVlan(vlanInte1);
			} catch (Exception ex) {
				eth1.setMultiNativeVlan(null);
			}

			/*
			 * set fields for red0
			 * ==============================================================
			 */
			/*
			 * Set red0_admin_state
			 */
			colName = "red0_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			red0.setAdminState((short) AhRestoreCommons
					.convertInt(red0_admin_state));

			/*
			 * Set red0_operation_mode
			 */
			colName = "red0_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_operation_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
			// fix bug 22056
//			red0.setOperationMode((short) AhRestoreCommons
//					.convertInt(red0_operation_mode));
			red0.setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);


			/*
			 * Set red0_learning_enabled
			 */
			colName = "red0_learning_enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_learning_enabled = isColPresent ? xmlParser.getColVal(
					i, colName) : "true";
			red0.setMacLearningEnabled(AhRestoreCommons
					.convertStringToBoolean(red0_learning_enabled));

			/*
			 * Set red0_idel_timeout
			 */
			colName = "red0_idel_timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_idel_timeout = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveApEth.DEFAULT_IDEL_TIMEOUT);
			red0.setIdelTimeout(AhRestoreCommons.convertInt(red0_idel_timeout));

			/*
			 * Set red0_allowed_vlan
			 */
			colName = "red0_allowed_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_allowed_vlan = isColPresent ? xmlParser.getColVal(i,
					colName) : HiveApEth.ALLOWED_VLAN_ALL;
			red0.setAllowedVlan(AhRestoreCommons
					.convertString(red0_allowed_vlan));

			/*
			 * Set red0_multinative_vlan
			 */
			colName = "red0_multinative_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_multinative_vlan = isColPresent ? xmlParser.getColVal(
					i, colName) : "";
			try {
				Integer vlanInteRed0 = Integer.valueOf(red0_multinative_vlan);
				red0.setMultiNativeVlan(vlanInteRed0);
			} catch (Exception ex) {
				red0.setMultiNativeVlan(null);
			}

			/*
			 * set fields for agg0
			 * ==============================================================
			 */
			/*
			 * Set agg0_admin_state
			 */
			colName = "agg0_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			agg0.setAdminState((short) AhRestoreCommons
					.convertInt(agg0_admin_state));

			/*
			 * Set agg0_operation_mode
			 */
			colName = "agg0_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_operation_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
			// fix bug 22056
			agg0.setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
//			agg0.setOperationMode((short) AhRestoreCommons
//					.convertInt(agg0_operation_mode));

			/*
			 * Set agg0_learning_enabled
			 */
			colName = "agg0_learning_enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_learning_enabled = isColPresent ? xmlParser.getColVal(
					i, colName) : "true";
			agg0.setMacLearningEnabled(AhRestoreCommons
					.convertStringToBoolean(agg0_learning_enabled));

			/*
			 * Set agg0_idel_timeout
			 */
			colName = "agg0_idel_timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_idel_timeout = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveApEth.DEFAULT_IDEL_TIMEOUT);
			agg0.setIdelTimeout(AhRestoreCommons.convertInt(agg0_idel_timeout));

			/*
			 * Set agg0_allowed_vlan
			 */
			colName = "agg0_allowed_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_allowed_vlan = isColPresent ? xmlParser.getColVal(i,
					colName) : HiveApEth.ALLOWED_VLAN_ALL;
			agg0.setAllowedVlan(AhRestoreCommons
					.convertString(agg0_allowed_vlan));

			/*
			 * Set agg0_multinative_vlan
			 */
			colName = "agg0_multinative_vlan";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_multinative_vlan = isColPresent ? xmlParser.getColVal(
					i, colName) : "";
			try {
				Integer vlanInteAgg0 = Integer.valueOf(agg0_multinative_vlan);
				agg0.setMultiNativeVlan(vlanInteAgg0);
			} catch (Exception ex) {
				agg0.setMultiNativeVlan(null);
			}

			/**
			 * Set enabledas
			 */
			colName = "enabledas";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enabledas = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setEnableDas(AhRestoreCommons
					.convertStringToBoolean(enabledas));

			/**
			 * Set gateway
			 */
			colName = "gateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String gateway = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setGateway(AhRestoreCommons.convertString(gateway));

			/**
			 * Set productName
			 */
			colName = "productName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String productName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setProductName(AhRestoreCommons.convertString(productName));

			if (restore_from_60r1_before &&
					(productName.equalsIgnoreCase("SR2024") || productName.equalsIgnoreCase("SR-2024"))) {
				BeLogTools.debug(HmLogConst.M_RESTORE,
						"Restore table 'hive_ap' data be lost, cause: switch not support before 6.0r1");
				continue;
			}

			if (restore_from_50r0_before &&
					(productName.equalsIgnoreCase("BR100") || productName.equalsIgnoreCase("BR200")
							|| productName.equalsIgnoreCase("BR200-WP"))) {
				BeLogTools.debug(HmLogConst.M_RESTORE,
						"Restore table 'hive_ap' data be lost, cause: BR not support before 5.0r1");
				continue;
			}

			/**
			 * Set hiveaptype
			 */
			colName = "hiveaptype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String hiveaptype = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveAp.HIVEAP_TYPE_MP);
			hiveAp.setHiveApType((short) AhRestoreCommons
					.convertInt(hiveaptype));

			/**
			 * Set ipaddress
			 */
			colName = "ipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ipaddress = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setIpAddress(AhRestoreCommons.convertString(ipaddress));

			/**
             * Set hardwareRevision
             */
            colName = "hardwarerevision";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    "hive_ap", colName);
            String hardwarerevision = isColPresent ? xmlParser.getColVal(i, colName)
                    : "01";
            hiveAp.setHardwareRevision(AhRestoreCommons.convertString(hardwarerevision));

			/**
			 * Set keyid
			 */
			colName = "keyid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String keyid = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			hiveAp.setKeyId(AhRestoreCommons.convertInt(keyid));

			/**
			 * Set lastAuditTime
			 */
			colName = "lastaudittime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String lastaudittime = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setLastAuditTime(AhRestoreCommons
					.convertString2Long(lastaudittime));

			/**
			 * Set lastcfgtime(do not set the cfg time while upgrade, since this
			 * field is used then HiveAP update)
			 */
			// colName = "lastcfgtime";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String lastcfgtime = isColPresent ? xmlParser.getColVal(i,
			// colName)
			// : "";
			// hiveAp.setLastCfgTime(AhRestoreCommons.convertDate(lastcfgtime));
			/**
			 * Set lastimagetime
			 */
			colName = "lastimagetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String lastimagetime = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setLastImageTime(AhRestoreCommons
					.convertString2Long(lastimagetime));

			/**
			 * Set lastSignatureTime
			 */
			colName = "lastSignatureTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String lastSignatureTime = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setLastSignatureTime(AhRestoreCommons
					.convertString2Long(lastSignatureTime));


			/**
			 * Set location
			 */
			colName = "location";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String location = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setLocation(AhRestoreCommons.convertString(location));

			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				hiveAp.setLocation(HiveAp.DEFAULT_LOCATION);	
			}
			
			/**
			 * Set includeTopologyInfo
			 */
			colName = "includeTopologyInfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String includeTopologyInfo = isColPresent ? xmlParser.getColVal(i,
					colName) : "true";
			hiveAp.setIncludeTopologyInfo(AhRestoreCommons
					.convertStringToBoolean(includeTopologyInfo));

			/**
			 * Set manageuponcontact
			 */
			colName = "manageuponcontact";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String manageuponcontact = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setManageUponContact(AhRestoreCommons
					.convertStringToBoolean(manageuponcontact));

			/**
			 * Set metric
			 */
			colName = "metric";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String metric = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveAp.METRIC_TYPE_NORMAL);
			hiveAp.setMetric((short) AhRestoreCommons.convertInt(metric));

			/**
			 * Set metricinteval
			 */
			colName = "metricinteval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String metricinteval = isColPresent ? xmlParser.getColVal(i,
					colName) : "60";
			hiveAp.setMetricInteval(AhRestoreCommons.convertInt(metricinteval));

			/**
			 * Set netmask
			 */
			colName = "netmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String netmask = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setNetmask(AhRestoreCommons.convertString(netmask));

			/**
			 * Set origin
			 */
			colName = "origin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String origin = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveAp.ORIGIN_DISCOVERED);
			hiveAp.setOrigin((short) AhRestoreCommons.convertInt(origin));

			/**
			 * Set signatureVer
			 */
			colName = "signatureVer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String signatureVer = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			hiveAp.setSignatureVer(AhRestoreCommons.convertInt(signatureVer));

			/**
			 * Set passPhrase
			 */
			colName = "passphrase";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String passPhrase = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setPassPhrase(AhRestoreCommons.convertString(passPhrase));

			/**
			 * Set pending
			 */
			// colName = "pending";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String pending = isColPresent ? xmlParser.getColVal(i, colName)
			// : "";
			// hiveAp.setPending(AhRestoreCommons.convertStringToBoolean(pending));
			/**
			 * Set pendingindex
			 */
			// colName = "pendingindex";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String pendingindex = isColPresent ? xmlParser
			// .getColVal(i, colName) : "0";
			// hiveAp.setPendingIndex(AhRestoreCommons.convertInt(pendingindex));
			/**
			 * Set pendingmsg
			 */
			// colName = "pendingmsg";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String pendingmsg = isColPresent ? xmlParser.getColVal(i,
			// colName)
			// : "";
			// hiveAp.setPendingMsg(AhRestoreCommons.convertString(pendingmsg));
			/**
			 * Set provision
			 */
			colName = "provision";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String provision = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			hiveAp.setProvision(AhRestoreCommons.convertInt(provision));

			/**
			 * Set radioconfigtype
			 */
			colName = "radioconfigtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String radioconfigtype = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveAp.RADIO_MODE_CUSTOMIZE);
			short sConfigtype = (short) AhRestoreCommons
					.convertInt(radioconfigtype);
			if (sConfigtype == HiveAp.RADIO_MODE_BRIDGE) {
				sConfigtype = HiveAp.RADIO_MODE_ACCESS_ONE;
			}
			hiveAp.setRadioConfigType(sConfigtype);

			/**
			 * Set enableEthBridge
			 */
			colName = "enableEthBridge";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableEthBridge = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(false);
			hiveAp.setEnableEthBridge(AhRestoreCommons
					.convertStringToBoolean(enableEthBridge));

			/**
			 * Set enableDynamicBandSwitch
			 */
			colName = "enableDynamicBandSwitch";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableDynamicBandSwitch = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(false);
			hiveAp.setEnableDynamicBandSwitch(AhRestoreCommons
					.convertStringToBoolean(enableDynamicBandSwitch));

			/**
			 * Set readonlypassword
			 */
			colName = "readonlypassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String readonlypassword = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setReadOnlyPassword(AhRestoreCommons
					.convertString(readonlypassword));

			/**
			 * Set readonlyuser
			 */
			colName = "readonlyuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String readonlyuser = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setReadOnlyUser(AhRestoreCommons.convertString(readonlyuser));

			/**
			 * Set regioncode
			 */
			colName = "regioncode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String regioncode = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setRegionCode(AhRestoreCommons.convertInt(regioncode));

			/**
			 * Set serialNumber
			 */
			colName = "serialNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String serialNumber = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			if (serialNumber.length() > 14) {
				// serialNumber > 14 is lawless AP
				AhRestoreDBTools.logRestoreMsg("HiveAP " + hiveAp.getHostName()
						+ "is be discarded, because serial number is lawless");
				continue;
			}
			hiveAp.setSerialNumber(AhRestoreCommons.convertString(serialNumber));

			/**
			 * Set severity
			 */
			Short severity = AhRestoreNewMapTools.getAlarmSeverity(macaddress);
			if (null != severity && severity > 0) {
				hiveAp.setSeverity(severity);
			} else {
				hiveAp.setSeverity(AhAlarm.AH_SEVERITY_UNDETERMINED);
			}

			/**
			 * Set softver
			 */
			colName = "softver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String softver = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hiveAp.setSoftVer(AhRestoreCommons.convertString(softver));

			/**
			 * Set displayver
			 */
			colName = "displayver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String displayver = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			String disVer = AhRestoreCommons.convertString(displayver);
			hiveAp.setDisplayVer("".equals(disVer) ? hiveAp.getSoftVer()
					: disVer);
			if(hiveAp.getDisplayVer().equals(hiveAp.getSoftVer())){
				hiveAp.setDisplayVer("HiveOS "+hiveAp.getSoftVerString()+" Release");
			}

			/**
			 * Set tunnelthreshold
			 */
			colName = "tunnelthreshold";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String tunnelthreshold = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(HiveAp.TUNNEL_THRESHOLD_HIGH);
			hiveAp.setTunnelThreshold((short) AhRestoreCommons
					.convertInt(tunnelthreshold));

			/**
			 * Set simulateclientinfo
			 */
			colName = "simulateclientinfo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String simulateclientinfo = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setSimulateClientInfo(AhRestoreCommons
					.convertString(simulateclientinfo));

			/**
			 * Set simulatecode
			 */
			colName = "simulatecode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String simulatecode = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setSimulateCode(AhRestoreCommons.convertInt(simulatecode));

			/**
			 * Set simulated
			 */
			colName = "simulated";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String simulated = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setSimulated(AhRestoreCommons
					.convertStringToBoolean(simulated));

			/**
			 * Set uptime /no need to restored it.
			 */
			// colName = "uptime";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hive_ap", colName);
			// String uptime = isColPresent ? xmlParser.getColVal(i, colName) :
			// "";
			// hiveAp.setUpTime(AhRestoreCommons.convertString2Long(uptime));
			/**
			 * Set vpnmark
			 */
			colName = "vpnmark";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String vpnmark = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveAp.VPN_MARK_NONE);
			hiveAp.setVpnMark((short) AhRestoreCommons.convertInt(vpnmark));

			/**
			 * Set ethcwplimituserprofiles
			 */
			colName = "ethcwplimituserprofiles";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwplimituserprofiles = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEthCwpLimitUserProfiles(AhRestoreCommons
					.convertStringToBoolean(ethcwplimituserprofiles));

			/**
			 * Set ethcwpenableethcwp
			 */
			colName = "ethcwpenableethcwp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpenableethcwp = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setEthCwpEnableEthCwp(AhRestoreCommons
					.convertStringToBoolean(ethcwpenableethcwp));

			/**
			 * Set ethcwpenablemacauth
			 */
			colName = "ethcwpenablemacauth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpenablemacauth = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			if(restore_from_fuji_before && !hiveAp.isEthCwpEnableEthCwp()){
				hiveAp.setEthCwpEnableMacAuth(false);
			}else{
				hiveAp.setEthCwpEnableMacAuth(AhRestoreCommons
						.convertStringToBoolean(ethcwpenablemacauth));
			}

			/**
			 * Set ethcwpenablestriction
			 */
			colName = "ethcwpenablestriction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpenablestriction = isColPresent ? xmlParser.getColVal(
					i, colName) : "false";
			hiveAp.setEthCwpEnableStriction(AhRestoreCommons
					.convertStringToBoolean(ethcwpenablestriction));

			/**
			 * Set ethcwpdenyaction
			 */
			colName = "ethcwpdenyaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpdenyaction = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(SsidProfile.DENY_ACTION_DISCONNECT);
			hiveAp.setEthCwpDenyAction((short) AhRestoreCommons
					.convertInt(ethcwpdenyaction));

			/**
			 * Set ethcwpauthmethod
			 */
			colName = "ethcwpauthmethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpauthmethod = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(Cwp.AUTH_METHOD_PAP);
			hiveAp.setEthCwpAuthMethod((short) AhRestoreCommons
					.convertInt(ethcwpauthmethod));

			/**
			 * Set ethcwpactivetime
			 */
			colName = "ethcwpactivetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethcwpactivetime = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(SsidProfile.DEFAULT_ACTION_TIME);
			hiveAp.setEthCwpActiveTime(AhRestoreCommons
					.convertInt(ethcwpactivetime));

			/**
			 * Set wifi0_radio_channel
			 */
			colName = "wifi0_radio_channel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_radio_channel = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.CHANNEL_BG_AUTO);
			wifi0.setChannel(AhRestoreCommons.convertInt(wifi0_radio_channel));

			/**
			 * Set wifi0_operation_mode
			 */
			colName = "wifi0_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_operation_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.OPERATION_MODE_ACCESS);
			wifi0.setOperationMode((short) AhRestoreCommons
					.convertInt(wifi0_operation_mode));

			/**
			 * Set wifi0_radio_power
			 */
			colName = "wifi0_radio_power";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_radio_power = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.POWER_AUTO);
			wifi0.setPower(AhRestoreCommons.convertInt(wifi0_radio_power));

			/**
			 * Set wifi0_radio_mode
			 */
			colName = "wifi0_radio_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_radio_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.RADIO_MODE_BG);
			wifi0.setRadioMode((short) AhRestoreCommons
					.convertInt(wifi0_radio_mode));

			/**
			 * Set wifi0_admin_state
			 */
			colName = "wifi0_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			wifi0.setAdminState((short) AhRestoreCommons
					.convertInt(wifi0_admin_state));

			/**
			 * Set wifi1_radio_channel
			 */
			colName = "wifi1_radio_channel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_radio_channel = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.CHANNEL_A_AUTO);
			wifi1.setChannel(AhRestoreCommons.convertInt(wifi1_radio_channel));

			/**
			 * Set wifi1_operation_mode
			 */
			colName = "wifi1_operation_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_operation_mode = null;
			if (softver != null && !"".equals(softver)
					&& NmsUtil.compareSoftwareVersion("4.0.1.0", softver) > 0) {
				wifi1_operation_mode = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
			} else {
				wifi1_operation_mode = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(AhInterface.OPERATION_MODE_DUAL);
			}

			wifi1.setOperationMode((short) AhRestoreCommons
					.convertInt(wifi1_operation_mode));

			/**
			 * Set wifi1_radio_power
			 */
			colName = "wifi1_radio_power";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_radio_power = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.POWER_AUTO);
			wifi1.setPower((short) AhRestoreCommons
					.convertInt(wifi1_radio_power));

			/**
			 * Set wifi1_radio_mode
			 */
			colName = "wifi1_radio_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_radio_mode = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.RADIO_MODE_A);
			wifi1.setRadioMode((short) AhRestoreCommons
					.convertInt(wifi1_radio_mode));

			/**
			 * Set wifi1_admin_state
			 */
			colName = "wifi1_admin_state";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_admin_state = isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
			wifi1.setAdminState((short) AhRestoreCommons
					.convertInt(wifi1_admin_state));

			/**
			 * Set totalConnectTime
			 */
			colName = "totalConnectTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String totalConnectTime = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			hiveAp.setTotalConnectTime(AhRestoreCommons
					.convertLong(totalConnectTime));

			/**
			 * Set totalConnectTimes
			 */
			colName = "totalConnectTimes";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String totalConnectTimes = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			hiveAp.setTotalConnectTimes(AhRestoreCommons
					.convertLong(totalConnectTimes));

			/**
			 * Set discoveryReported
			 */
			colName = "discoveryReported";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String discoveryReported = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setDiscoveryReported(AhRestoreCommons
					.convertStringToBoolean(discoveryReported));

			/**
			 * set deviceType
			 */
			colName = "deviceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String deviceType = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveAp.Device_TYPE_HIVEAP);
			hiveAp.setDeviceType((short) AhRestoreCommons
					.convertInt(deviceType));

			/**
			 * set enableVRRP
			 */
			colName = "enableVRRP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableVRRP = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setEnableVRRP(AhRestoreCommons
					.convertStringToBoolean(enableVRRP));

			/**
			 * set enableVRRP
			 */
			colName = "enablePreempt";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enablePreempt = isColPresent ? xmlParser.getColVal(i,
					colName) : "true";
			hiveAp.setEnablePreempt(AhRestoreCommons
					.convertStringToBoolean(enablePreempt));

			/**
			 * set virtualWanIp
			 */
			colName = "virtualWanIp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String virtualWanIp = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setVirtualWanIp(AhRestoreCommons.convertString(virtualWanIp));

			/**
			 * set virtualLanIp
			 */
			colName = "virtualLanIp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String virtualLanIp = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setVirtualLanIp(AhRestoreCommons.convertString(virtualLanIp));

			/**
			 * set vrrpId
			 */
			colName = "vrrpId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String vrrpId = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			hiveAp.setVrrpId(AhRestoreCommons.convertInt(vrrpId));

			/**
			 * set vrrpPriority
			 */
			colName = "vrrpPriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String vrrpPriority = isColPresent ? xmlParser
					.getColVal(i, colName) : "-1";
			hiveAp.setVrrpPriority(AhRestoreCommons.convertInt(vrrpPriority));

			/**
			 * set enableSwitchQosSettings
			 */
			colName = "enableSwitchQosSettings";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableSwitchQosSettings = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEnableSwitchPriority(AhRestoreCommons
					.convertStringToBoolean(enableSwitchQosSettings));

			/**
			 * set vrrpDelay
			 */
			colName = "vrrpDelay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String vrrpDelay = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			hiveAp.setVrrpDelay(AhRestoreCommons.convertInt(vrrpDelay));

			/**
			 * set routeInterval
			 */
			colName = "routeInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String routeInterval = isColPresent ? xmlParser.getColVal(i,
					colName) : "60";
			hiveAp.setRouteInterval(AhRestoreCommons.convertInt(routeInterval));

			/**
			 * set maxPowerSource
			 */
			colName = "maxPowerSource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			// max POE power value is 44W for BR200
			String maxPowerSource = isColPresent ? xmlParser.getColVal(i,
					colName) : "44";

			hiveAp.setMaxPowerSource(AhRestoreCommons
					.convertInt(maxPowerSource));

			/**
			 * set usbConnectionModel
			 */
			colName = "usbConnectionModel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String usbConnectionModel = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(HiveAp.USB_CONNECTION_MODEL_NEEDED);
			hiveAp.setUsbConnectionModel((short) AhRestoreCommons
					.convertInt(usbConnectionModel));

			/**
			 * set ethLanStatus
			 */
			colName = "ethLanStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethLanStatus = isColPresent ? xmlParser
					.getColVal(i, colName) : "1111";
			hiveAp.setEthLanStatus(ethLanStatus);

			/**
			 * set second_vpn_gateway_id
			 */
			colName = "second_vpn_gateway_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String second_vpn_gateway_id = isColPresent ? xmlParser.getColVal(
					i, colName) : null;
			if (second_vpn_gateway_id != null
					&& !(second_vpn_gateway_id.trim().equals(""))
					&& !(second_vpn_gateway_id.trim().equalsIgnoreCase("null"))) {
				second_vpn_gateway_map.put(hiveAp.getMacAddress(),
						second_vpn_gateway_id);
			}

			/**
			 * Set wifi0_radio_profile_id
			 */
			colName = "wifi0_radio_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi0_radio_profile_id = isColPresent ? xmlParser.getColVal(
					i, colName) : null;

			if (wifi0_radio_profile_id != null
					&& !(wifi0_radio_profile_id.trim().equals(""))
					&& !(wifi0_radio_profile_id.trim().equalsIgnoreCase("null"))) {
				Long wifi0_radio_profile_id_new = AhRestoreNewMapTools
						.getMapRadioProfile(AhRestoreCommons
								.convertLong(wifi0_radio_profile_id));
				if (null != wifi0_radio_profile_id_new) {
					hiveAp.setWifi0RadioProfile(AhRestoreNewTools
							.CreateBoWithId(RadioProfile.class,
									wifi0_radio_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
									+ wifi0_radio_profile_id);
				}
			}

			/**
			 * Set wifi1_radio_profile_id
			 */
			colName = "wifi1_radio_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String wifi1_radio_profile_id = isColPresent ? xmlParser.getColVal(
					i, colName) : null;

			if (wifi1_radio_profile_id != null
					&& !(wifi1_radio_profile_id.trim().equals(""))
					&& !(wifi1_radio_profile_id.trim().equalsIgnoreCase("null"))) {
				Long wifi1_radio_profile_id_new = AhRestoreNewMapTools
						.getMapRadioProfile(AhRestoreCommons
								.convertLong(wifi1_radio_profile_id));
				if (null != wifi1_radio_profile_id_new) {
					hiveAp.setWifi1RadioProfile(AhRestoreNewTools
							.CreateBoWithId(RadioProfile.class,
									wifi1_radio_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
									+ wifi1_radio_profile_id);
				}
			}

			/**
			 * Set eth0_user_profile_id
			 */
			colName = "eth0_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth0_user_profile_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (eth0_user_profile_id != null
					&& !(eth0_user_profile_id.trim().equals(""))
					&& !(eth0_user_profile_id.trim().equalsIgnoreCase("null"))) {
				Long eth0_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(eth0_user_profile_id));
				if (null != eth0_user_profile_id_new) {
					hiveAp.setEth0UserProfile(AhRestoreNewTools.CreateBoWithId(
							UserProfile.class, eth0_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ eth0_user_profile_id);
				}
			}

			/**
			 * Set eth1_user_profile_id
			 */
			colName = "eth1_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String eth1_user_profile_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (eth1_user_profile_id != null
					&& !(eth1_user_profile_id.trim().equals(""))
					&& !(eth1_user_profile_id.trim().equalsIgnoreCase("null"))) {
				Long eth1_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(eth1_user_profile_id));
				if (null != eth1_user_profile_id_new) {
					hiveAp.setEth1UserProfile(AhRestoreNewTools.CreateBoWithId(
							UserProfile.class, eth1_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ eth1_user_profile_id);
				}
			}

			/**
			 * Set agg0_user_profile_id
			 */
			colName = "agg0_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String agg0_user_profile_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (agg0_user_profile_id != null
					&& !(agg0_user_profile_id.trim().equals(""))
					&& !(agg0_user_profile_id.trim().equalsIgnoreCase("null"))) {
				Long agg0_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(agg0_user_profile_id));
				if (null != agg0_user_profile_id_new) {
					hiveAp.setAgg0UserProfile(AhRestoreNewTools.CreateBoWithId(
							UserProfile.class, agg0_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ agg0_user_profile_id);
				}
			}

			/**
			 * Set red0_user_profile_id
			 */
			colName = "red0_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String red0_user_profile_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (red0_user_profile_id != null
					&& !(red0_user_profile_id.trim().equals(""))
					&& !(red0_user_profile_id.trim().equalsIgnoreCase("null"))) {
				Long red0_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(red0_user_profile_id));
				if (null != red0_user_profile_id_new) {
					hiveAp.setRed0UserProfile(AhRestoreNewTools.CreateBoWithId(
							UserProfile.class, red0_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ red0_user_profile_id);
				}
			}

			/**
			 * Set ethernet_cwp_id
			 */
			colName = "ethernet_cwp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethernet_cwp_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (ethernet_cwp_id != null && !(ethernet_cwp_id.trim().equals(""))
					&& !(ethernet_cwp_id.trim().equalsIgnoreCase("null"))) {
				Long ethernet_cwp_id_new = AhRestoreNewMapTools
						.getMapCapWebPortal(AhRestoreCommons
								.convertLong(ethernet_cwp_id));
				if (null != ethernet_cwp_id_new) {
					hiveAp.setEthCwpCwpProfile(AhRestoreNewTools
							.CreateBoWithId(Cwp.class, ethernet_cwp_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new cwp profile id mapping to old id:"
									+ ethernet_cwp_id);
				}
			}

			/**
			 * Set default_eth_auth_user_profile_id
			 */
			colName = "default_eth_auth_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String default_eth_auth_user_profile_id = isColPresent ? xmlParser
					.getColVal(i, colName) : null;

			if (default_eth_auth_user_profile_id != null
					&& !(default_eth_auth_user_profile_id.trim().equals(""))
					&& !(default_eth_auth_user_profile_id.trim()
							.equalsIgnoreCase("null"))) {
				Long default_eth_auth_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(default_eth_auth_user_profile_id));
				if (null != default_eth_auth_user_profile_id_new) {
					hiveAp.setEthCwpDefaultAuthUserProfile(AhRestoreNewTools
							.CreateBoWithId(UserProfile.class,
									default_eth_auth_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ default_eth_auth_user_profile_id);
				}
			}

			/**
			 * Set default_eth_reg_user_profile_id
			 */
			colName = "default_eth_reg_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String default_eth_reg_user_profile_id = isColPresent ? xmlParser
					.getColVal(i, colName) : null;

			if (default_eth_reg_user_profile_id != null
					&& !(default_eth_reg_user_profile_id.trim().equals(""))
					&& !(default_eth_reg_user_profile_id.trim()
							.equalsIgnoreCase("null"))) {
				Long default_eth_reg_user_profile_id_new = AhRestoreNewMapTools
						.getMapUserProfile(AhRestoreCommons
								.convertLong(default_eth_reg_user_profile_id));
				if (null != default_eth_reg_user_profile_id_new) {
					hiveAp.setEthCwpDefaultRegUserProfile(AhRestoreNewTools
							.CreateBoWithId(UserProfile.class,
									default_eth_reg_user_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
									+ default_eth_reg_user_profile_id);
				}
			}

			/**
			 * Set radius_client_id
			 */
			colName = "radius_client_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String radius_client_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			if (radius_client_id != null
					&& !(radius_client_id.trim().equals(""))
					&& !(radius_client_id.trim().equalsIgnoreCase("null"))) {
				Long radius_client_id_new = AhRestoreNewMapTools
						.getMapRadiusServerAssign(AhRestoreCommons
								.convertLong(radius_client_id));
				if (null != radius_client_id_new) {
					hiveAp.setEthCwpRadiusClient(AhRestoreNewTools
							.CreateBoWithId(RadiusAssignment.class,
									radius_client_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new RADIUS assignment id mapping to old id:"
									+ radius_client_id);
				}
			}

			/**
			 * Set template_id
			 */
			colName = "template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String template_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (template_id != null && !(template_id.trim().equals(""))
					&& !(template_id.trim().equalsIgnoreCase("null"))) {
				Long template_id_new = AhRestoreNewMapTools
						.getMapConfigTemplate(AhRestoreCommons
								.convertLong(template_id));
				if (null != template_id_new) {
					hiveAp.setConfigTemplate(AhRestoreNewTools.CreateBoWithId(
							ConfigTemplate.class, template_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new config template id mapping to old id:"
									+ template_id);
				}
				// store ap id > template id
				ap_template_map.put(id, template_id);
			}

			/**
			 * Set capwap_ip_id
			 */
			if (softver != null) {
				colName = "capwap_ip_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String capwap_ip_id = isColPresent ? xmlParser.getColVal(i,
						colName) : null;
				if (capwap_ip_id != null && !(capwap_ip_id.trim().equals(""))
						&& !(capwap_ip_id.trim().equalsIgnoreCase("null"))) {
					Long capwap_ip_id_new = AhRestoreNewMapTools
							.getMapIpAdddress(AhRestoreCommons
									.convertLong(capwap_ip_id));
					if (null != capwap_ip_id_new) {
						hiveAp.setCapwapIpBind(AhRestoreNewTools
								.CreateBoWithId(IpAddress.class,
										capwap_ip_id_new));
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new ip address id mapping to old id:"
										+ capwap_ip_id);
					}
				}
			}

			/**
			 * Set capwap_backup_ip_id
			 */
			if (softver != null) {
				colName = "capwap_backup_ip_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String capwap_backup_ip_id = isColPresent ? xmlParser
						.getColVal(i, colName) : null;
				if (capwap_backup_ip_id != null
						&& !(capwap_backup_ip_id.trim().equals(""))
						&& !(capwap_backup_ip_id.trim()
								.equalsIgnoreCase("null"))) {
					Long capwap_backup_ip_id_new = AhRestoreNewMapTools
							.getMapIpAdddress(AhRestoreCommons
									.convertLong(capwap_backup_ip_id));
					if (null != capwap_backup_ip_id_new) {
						hiveAp.setCapwapBackupIpBind(AhRestoreNewTools
								.CreateBoWithId(IpAddress.class,
										capwap_backup_ip_id_new));
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new ip address id mapping to old id:"
										+ capwap_backup_ip_id);
					}
				}
			}

			/**
			 * Set scheduler_id
			 */
			if (softver != null) {
				colName = "scheduler_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String scheduler_id = isColPresent ? xmlParser.getColVal(i,
						colName) : null;
				if (scheduler_id != null && !(scheduler_id.trim().equals(""))
						&& !(scheduler_id.trim().equalsIgnoreCase("null"))) {
					Long scheduler_id_new = AhRestoreNewMapTools
							.getMapSchedule(AhRestoreCommons
									.convertLong(scheduler_id));
					if (null != scheduler_id_new) {
						hiveAp.setScheduler(AhRestoreNewTools.CreateBoWithId(
								Scheduler.class, scheduler_id_new));
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new scheduler id mapping to old id:"
										+ scheduler_id);
					}
				}
			}

			/**
			 * Set VPN Gateway IP Track
			 */
			if (HiveAp.Device_TYPE_VPN_GATEWAY == hiveAp.getDeviceType()) {
				colName = "vpn_ip_track_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String track_id = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : null;
				if (null != track_id && !track_id.equals("")) {
					Long track_id_new = AhRestoreNewMapTools
							.getMapMgmtIpTracking(AhRestoreCommons
									.convertLong(track_id));
					if (null != track_id_new) {
						hiveAp.setVpnIpTrack(AhRestoreNewTools.CreateBoWithId(
								MgmtServiceIPTrack.class, track_id_new));
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new mgmt ip track id mapping to old id:"
										+ track_id);
					}
				}
				// added for PMTUD start
				colName = "enableCvgPMTUD";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String enableCvgPMTUD = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				hiveAp.setEnableCvgPMTUD(AhRestoreCommons
						.convertStringToBoolean(enableCvgPMTUD));

				colName = "monitorCvgMSS";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String monitorCvgMSS = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				hiveAp.setMonitorCvgMSS(AhRestoreCommons
						.convertStringToBoolean(monitorCvgMSS));

				colName = "thresholdCvgForAllTCP";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				int thresholdCvgForAllTCP = isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName)) : 0;
				hiveAp.setThresholdCvgForAllTCP(thresholdCvgForAllTCP > 1460 ? 1460
						: thresholdCvgForAllTCP);

				colName = "thresholdCvgThroughVPNTunnel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				int thresholdCvgThroughVPNTunnel = isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName)) : 0;
				hiveAp.setThresholdCvgThroughVPNTunnel(thresholdCvgThroughVPNTunnel > 1460 ? 1460
						: thresholdCvgThroughVPNTunnel);
				// added for PMTUD end
			}

			if (HiveAp.Device_TYPE_BRANCH_ROUTER == hiveAp.getDeviceType()) {
				colName = "enableOverrideBrPMTUD";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String enableOverrideBrPMTUD = isColPresent ? xmlParser
						.getColVal(i, colName) : "false";
				hiveAp.setEnableOverrideBrPMTUD(AhRestoreCommons
						.convertStringToBoolean(enableOverrideBrPMTUD));

				colName = "enableBrPMTUD";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String enableBrPMTUD = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				hiveAp.setEnableBrPMTUD(AhRestoreCommons
						.convertStringToBoolean(enableBrPMTUD));

				colName = "monitorBrMSS";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String monitorBrMSS = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				hiveAp.setMonitorBrMSS(AhRestoreCommons
						.convertStringToBoolean(monitorBrMSS));

				colName = "thresholdBrForAllTCP";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				int thresholdBrForAllTCP = isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName)) : 0;
				hiveAp.setThresholdBrForAllTCP(thresholdBrForAllTCP > 1460 ? 1460
						: thresholdBrForAllTCP);

				colName = "thresholdBrThroughVPNTunnel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				int thresholdBrThroughVPNTunnel = isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName)) : 0;
				hiveAp.setThresholdBrThroughVPNTunnel(thresholdBrThroughVPNTunnel > 1460 ? 1460
						: thresholdBrThroughVPNTunnel);
			}

			/**
			 * Set radius_server_id
			 */
			colName = "radius_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String radius_server_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (radius_server_id != null
					&& !(radius_server_id.trim().equals(""))
					&& !(radius_server_id.trim().equalsIgnoreCase("null"))) {
				Long radius_server_id_new = AhRestoreNewMapTools
						.getMapRadiusServerOnHiveAP(AhRestoreCommons
								.convertLong(radius_server_id));
				if (null != radius_server_id_new) {
					hiveAp.setRadiusServerProfile(AhRestoreNewTools
							.CreateBoWithId(RadiusOnHiveap.class,
									radius_server_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new RADIUS server id mapping to old id:"
									+ radius_server_id);
				}
			}

			/**
			 * Set radius_proxy_id
			 */
			colName = "radius_proxy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String radius_proxy_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (radius_proxy_id != null && !(radius_proxy_id.trim().equals(""))
					&& !(radius_proxy_id.trim().equalsIgnoreCase("null"))) {
				hiveAp.setRadiusProxyProfile(AhRestoreNewMapTools
						.getMapRadiusProxy(Long.valueOf(radius_proxy_id)));
			}

			/**
			 * Set routing_policy_id
			 */
			colName = "routing_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String routing_policy_id = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			if (!routing_policy_id.equals("")
					&& !routing_policy_id.trim().equalsIgnoreCase("null")) {
				Long newroutingpolicyid = AhRestoreNewMapTools
						.getMapRoutingPolicy(Long.parseLong(routing_policy_id
								.trim()));
				RoutingProfilePolicy routingProfilePolicy = AhRestoreNewTools.CreateBoWithId(
						RoutingProfilePolicy.class, newroutingpolicyid);
				hiveAp.setRoutingProfilePolicy(routingProfilePolicy);
			}

			/**
			 * Set ROUTING_PBR_POLICY_ID
			 * */
			colName ="routing_pbr_policy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String routing_PBR_policy_id = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			if (!routing_PBR_policy_id.equals("")
					&& !routing_PBR_policy_id.trim().equalsIgnoreCase("null")) {
				Long newroutingProfilepolicyid = AhRestoreNewMapTools
						.getMapRoutingPolicy(Long.parseLong(routing_PBR_policy_id
								.trim()));
				RoutingProfilePolicy routingProfilePolicy = AhRestoreNewTools.CreateBoWithId(
						RoutingProfilePolicy.class, newroutingProfilepolicyid);
				hiveAp.setRoutingProfilePolicy(routingProfilePolicy);
			}
			/**
			 * set enabledBrAsRadiusServer
			 */
			colName = "enabledBrAsRadiusServer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enabledBrAsRadiusServer = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			hiveAp.setEnabledBrAsRadiusServer(AhRestoreCommons
					.convertStringToBoolean(enabledBrAsRadiusServer));

			/**
			 * set enabledBrAsPpskServer
			 */
			colName = "enabledBrAsPpskServer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enabledBrAsPpskServer = isColPresent ? xmlParser.getColVal(
					i, colName) : "true";
			hiveAp.setEnabledBrAsPpskServer(AhRestoreCommons
					.convertStringToBoolean(enabledBrAsPpskServer));

			/**
			 * set enabledOverrideVoipSetting
			 */
			colName = "enabledOverrideVoipSetting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enabledOverrideVoipSetting = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEnabledOverrideVoipSetting(AhRestoreCommons
					.convertStringToBoolean(enabledOverrideVoipSetting));

			/**
			 * set enablePppoe
			 */
			colName = "enablePppoe";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enablePppoe = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setEnablePppoe(AhRestoreCommons
					.convertStringToBoolean(enablePppoe));

			/**
			 * set PPPOE_AUTH_ID
			 */
			colName = "PPPOE_AUTH_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String pppoe_auth_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (pppoe_auth_id != null && !(pppoe_auth_id.trim().equals(""))
					&& !(pppoe_auth_id.trim().equalsIgnoreCase("null"))) {
				Long pppoe_auth_id_new = AhRestoreNewMapTools
						.getMapPPPoE(AhRestoreCommons
								.convertLong(pppoe_auth_id));
				if (null != pppoe_auth_id_new) {
					hiveAp.setPppoeAuthProfile(AhRestoreNewTools
							.CreateBoWithId(PPPoE.class, pppoe_auth_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new pppoe auth profile id mapping to old id:"
									+ pppoe_auth_id);
				}
			}

			/**
			 * set nasIdentifierType
			 */
			colName = "nasIdentifierType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String nasIdentifierType = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			hiveAp.setNasIdentifierType((short) AhRestoreCommons
					.convertInt(nasIdentifierType));

			/**
			 * set customizedNasIdentifier
			 */
			colName = "customizedNasIdentifier";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String customizedNasIdentifier = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setCustomizedNasIdentifier(AhRestoreCommons
					.convertString(customizedNasIdentifier));

			/**
			 * Set lldpcdp_id
			 */
			/*
			 * colName = "lldpcdp_id"; isColPresent =
			 * AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			 * String lldpcdp_id = isColPresent ? xmlParser.getColVal(i,
			 * colName) : null; if (lldpcdp_id != null &&
			 * !(lldpcdp_id.trim().equals("")) &&
			 * !(lldpcdp_id.trim().equalsIgnoreCase("null"))) { HmUpgradeLog
			 * upgradeLog = new HmUpgradeLog(); upgradeLog
			 * .setFormerContent(nmsName
			 * +" applied LLDP/CDP profiles in "+apName+" configurations.");
			 * upgradeLog .setPostContent(nmsName+
			 * " now applies LLDP/CDP profiles in network policies.");
			 * upgradeLog .setRecommendAction(
			 * "If you need to change an LLDP/CDP profile for one or more "
			 * +apName+"s, " +
			 * "go to the Configuration > Network Policies page, select a profile, and modify the values."
			 * ); upgradeLog.setOwner(ownerDomain); upgradeLog.setLogTime(new
			 * HmTimeStamp(System .currentTimeMillis(),
			 * ownerDomain.getTimeZoneString()));
			 * upgradeLog.setAnnotation("Click to add an annotation"); try {
			 * QueryUtil.createBo(upgradeLog); } catch (Exception e) {
			 * AhRestoreDBTools
			 * .logRestoreMsg("insert upgrade log error for user profile");
			 * AhRestoreDBTools.logRestoreMsg(e.getMessage()); } }
			 */

			/**
			 * Set map_container_id
			 */
			colName = "map_container_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String map_container_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (map_container_id != null
					&& !(map_container_id.trim().equals(""))
					&& !(map_container_id.trim().equalsIgnoreCase("null"))) {
				Long map_container_id_new = AhRestoreNewMapTools
						.getMapMapContainer(AhRestoreCommons
								.convertLong(map_container_id));
				if (null != map_container_id_new) {
					hiveAp.setMapContainer(AhRestoreNewTools.CreateBoWithId(
							MapContainerNode.class, map_container_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new map container id mapping to old id:"
									+ map_container_id);
				}
			}

			/**
			 * set ROUTING_PROFILE_ID
			 */
			colName = "ROUTING_PROFILE_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String routing_profile_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (routing_profile_id != null
					&& !(routing_profile_id.trim().equals(""))
					&& !(routing_profile_id.trim().equalsIgnoreCase("null"))) {
				Long routing_profile_id_new = AhRestoreNewMapTools
						.getMapRoutingProfile(AhRestoreCommons
								.convertLong(routing_profile_id));
				if (null != routing_profile_id_new) {
					hiveAp.setRoutingProfile(AhRestoreNewTools.CreateBoWithId(
							RoutingProfile.class, routing_profile_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new routing profile id mapping to old id:"
									+ routing_profile_id);
				}
			}

			hiveAp.setEth0(eth0);
			hiveAp.setEth1(eth1);
			hiveAp.setRed0(red0);
			hiveAp.setAgg0(agg0);
			hiveAp.setWifi0(wifi0);
			hiveAp.setWifi1(wifi1);

			/**
			 * Set ethconfigtype (put it to last!!)
			 */
			colName = "ethconfigtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String ethconfigtype;
			if (isColPresent) {
				ethconfigtype = xmlParser.getColVal(i, colName);
			} else {
				// set this value base on radio settings
				if (eth0.getBindInterface() == AhInterface.ETH_BIND_IF_AGG0
						&& eth1.getBindInterface() == AhInterface.ETH_BIND_IF_AGG0) {
					ethconfigtype = String.valueOf(HiveAp.USE_ETHERNET_AGG0);
				} else if (eth0.getBindInterface() == AhInterface.ETH_BIND_IF_RED0
						&& eth1.getBindInterface() == AhInterface.ETH_BIND_IF_RED0) {
					ethconfigtype = String.valueOf(HiveAp.USE_ETHERNET_RED0);
				} else {
					ethconfigtype = String.valueOf(HiveAp.USE_ETHERNET_BOTH);
				}
			}
			hiveAp.setEthConfigType((short) AhRestoreCommons
					.convertInt(ethconfigtype));

			/**
			 * Set isOutdoor
			 */
			colName = "isOutdoor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			Boolean isOutdoor = isColPresent ? Boolean.valueOf(xmlParser
					.getColVal(i, colName)) : Boolean.FALSE;
			hiveAp.setIsOutdoor(isOutdoor);

			/**
			 * Set cvg_mgt0_network_id
			 */
			colName = "cvg_mgt0_network_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_mgt0_network_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (cvg_mgt0_network_id != null
					&& !(cvg_mgt0_network_id.trim().equals(""))
					&& !(cvg_mgt0_network_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_mgt0_network_id_new = AhRestoreNewMapTools
						.getMapVpnNetwork(AhRestoreCommons
								.convertLong(cvg_mgt0_network_id));
				if (RestoreConfigNetwork.RESTORE_BEFORE_DARKA_FLAG) {
					cvg_mgt0_network_id_new = RestoreConfigTemplate
							.createManagementNetwork(cvg_mgt0_network_id_new);
				}
				if (null != cvg_mgt0_network_id_new) {
					hiveAp.getOrCreateCvgDPD().setMgtNetwork(
							AhRestoreNewTools.CreateBoWithId(VpnNetwork.class,
									cvg_mgt0_network_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new Management Network id mapping to old id:"
									+ cvg_mgt0_network_id);
				}
			}

			/**
			 * Set cvg_mgt0_vlan_id
			 */
			colName = "cvg_mgt0_vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_mgt0_vlan_id = null;
			if (RestoreConfigNetwork.restore_from_60r1_before) {
				Long cvg_mgt0_vlan_id_long = AhRestoreNewMapTools
						.getMapNetworkObjectVlan(AhRestoreCommons
								.convertLong(cvg_mgt0_network_id));
				if (cvg_mgt0_vlan_id_long != null) {
					cvg_mgt0_vlan_id = String.valueOf(cvg_mgt0_vlan_id_long);
				}
			} else {
				cvg_mgt0_vlan_id = isColPresent ? xmlParser.getColVal(i,
						colName) : null;
			}
			if (cvg_mgt0_vlan_id != null
					&& !(cvg_mgt0_vlan_id.trim().equals(""))
					&& !(cvg_mgt0_vlan_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_mgt0_vlan_id_new = AhRestoreNewMapTools
						.getMapVlan(AhRestoreCommons
								.convertLong(cvg_mgt0_vlan_id));
				if (null != cvg_mgt0_vlan_id_new) {
					hiveAp.getOrCreateCvgDPD().setMgtVlan(
							AhRestoreNewTools.CreateBoWithId(Vlan.class,
									cvg_mgt0_vlan_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new Management Vlan id mapping to old id:"
									+ cvg_mgt0_vlan_id);
				}
			}

			/**
			 * Set cvg_dns_id
			 */
			colName = "cvg_dns_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_dns_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (cvg_dns_id != null && !(cvg_dns_id.trim().equals(""))
					&& !(cvg_dns_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_dns_id_new = AhRestoreNewMapTools
						.getMapDns(AhRestoreCommons.convertLong(cvg_dns_id));
				if (null != cvg_dns_id_new) {
					hiveAp.getOrCreateCvgDPD().setDnsForCVG(
							AhRestoreNewTools.CreateBoWithId(
									MgmtServiceDns.class, cvg_dns_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new DNS Server id mapping to old id:"
									+ cvg_dns_id);
				}
			}

			/**
			 * Set cvg_ntp_id
			 */
			colName = "cvg_ntp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_ntp_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (cvg_ntp_id != null && !(cvg_ntp_id.trim().equals(""))
					&& !(cvg_ntp_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_ntp_id_new = AhRestoreNewMapTools
						.getMapTimeAndDate(AhRestoreCommons
								.convertLong(cvg_ntp_id));
				if (null != cvg_ntp_id_new) {
					hiveAp.getOrCreateCvgDPD().setNtpForCVG(
							AhRestoreNewTools.CreateBoWithId(
									MgmtServiceTime.class, cvg_ntp_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new NTP Server id mapping to old id:"
									+ cvg_ntp_id);
				}
			}

			/**
			 * CVG_SYSLOG_ID
			 */
			colName = "cvg_syslog_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_syslog_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (cvg_syslog_id != null && !(cvg_syslog_id.trim().equals(""))
					&& !(cvg_syslog_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_syslog_id_new = AhRestoreNewMapTools
						.getMapSyslog(AhRestoreCommons
								.convertLong(cvg_syslog_id));
				if (null != cvg_syslog_id_new) {
					hiveAp.getOrCreateCvgDPD().setMgmtServiceSyslog(
							AhRestoreNewTools.CreateBoWithId(
									MgmtServiceSyslog.class, cvg_syslog_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new System log Server id mapping to old id:"
									+ cvg_syslog_id);
				}
			}

			/**
			 * CVG_SNMP_ID
			 */
			colName = "cvg_snmp_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String cvg_snmp_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (cvg_snmp_id != null && !(cvg_snmp_id.trim().equals(""))
					&& !(cvg_snmp_id.trim().equalsIgnoreCase("null"))) {
				Long cvg_snmp_id_new = AhRestoreNewMapTools
						.getMapSnmp(AhRestoreCommons
								.convertLong(cvg_snmp_id));
				if (null != cvg_snmp_id_new) {
					hiveAp.getOrCreateCvgDPD().setMgmtServiceSnmp(
							AhRestoreNewTools.CreateBoWithId(
									MgmtServiceSnmp.class, cvg_snmp_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new Snmp Server id mapping to old id:"
									+ cvg_snmp_id);
				}
			}

			/**
			 * Set IDMProxy
			 */
			colName = "IDMProxy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String IDMProxy = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setIDMProxy(AhRestoreCommons
					.convertStringToBoolean(IDMProxy));
			
			/**
			 * Set IDM Auth Proxy
			 */
			colName = "enableIDMAuthProxy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableIDMAuthProxy = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if(enableIDMAuthProxy != null){
				hiveAp.setEnableIDMAuthProxy(AhRestoreCommons.convertStringToBoolean(enableIDMAuthProxy));
			}
			
			/**
			 * Set priority
			 */
			if(restore_from_60r1_before){
				hiveAp.setPriority(HiveAp.getDefaultBonjourPriority(hiveAp.getHiveApModel()));
			} else {
				colName = "priority";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String priority = isColPresent ? xmlParser.getColVal(i, colName)
						: "";
				hiveAp.setPriority(AhRestoreCommons.convertString(priority));
			}


			/**
			 * Set lockRealmName
			 */
			colName = "lockRealmName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String lockRealmName = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setLockRealmName(AhRestoreCommons.convertStringToBoolean(lockRealmName));

			/**
			 * Set realmName
			 */
			if(restore_from_60r1_before){
				Long template_id_new = AhRestoreNewMapTools
						.getMapConfigTemplate(AhRestoreCommons
								.convertLong(template_id));
				Long map_container_id_new = AhRestoreNewMapTools
						.getMapMapContainer(AhRestoreCommons
								.convertLong(map_container_id));
				String realmName = HiveApAction.generateRealmName(template_id_new,map_container_id_new,false,"");
				hiveAp.setRealmName(AhRestoreCommons.convertString(realmName));
			} else {
				colName = "realmName";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String realmName = isColPresent ? xmlParser.getColVal(i, colName)
						: "";
				hiveAp.setRealmName(AhRestoreCommons.convertString(realmName));
			}

			/**
			 * Set interfaceMtu4Mgt0
			 */
			colName = "interfaceMtu4Mgt0";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String interfaceMtu4Mgt0 = isColPresent ? xmlParser.getColVal(i, colName)
					: "1500";
			//hiveAp.setInterfaceMtu4Mgt0(AhRestoreCommons.convertInt(interfaceMtu4Mgt0));
			//SR MGT0 MTU Change 2013-11-12
			hiveAp.setInterfaceMtu4Mgt0(1500);

			/**
			 * Set interfaceMtu4Ethernet
			 */
			colName = "interfaceMtu4Ethernet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String interfaceMtu4Ethernet = isColPresent ? xmlParser.getColVal(i, colName)
					: "1536";
			hiveAp.setInterfaceMtu4Ethernet(AhRestoreCommons.convertInt(interfaceMtu4Ethernet));

			/* Added from Chesapeake start */
			/**
			 * Set forwardingDB
			 */
			colName = "FORWARDING_DB_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"config_template", colName);
			String forwardingDB_id = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			if (!forwardingDB_id.equals("")
					&& !forwardingDB_id.trim().equalsIgnoreCase("null")) {
				Long newforwardingDB_id = AhRestoreNewMapTools
						.getMapForwardingDBMap(Long.parseLong(forwardingDB_id
								.trim()));
				if (null != newforwardingDB_id) {
					ForwardingDB fdb = AhRestoreNewTools.CreateBoWithId(
							ForwardingDB.class, newforwardingDB_id);
					hiveAp.setForwardingDB(fdb);
				}
			}

			/**
			 * Set resrvedVlans
			 */
			colName = "resrvedVlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String resrvedVlans = isColPresent ? xmlParser
					.getColVal(i, colName) : String
					.valueOf(HiveAp.RESERVED_VLANS_DEFAULT);
			hiveAp.setResrvedVlans(AhRestoreCommons.convertInt(resrvedVlans));

			/**** Switch PSE restore start****/
			/**
			 * Set enableSwitchPse
			 */
			colName = "enableSwitchPse";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableSwitchPse = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			hiveAp.setEnableSwitchPse(AhRestoreCommons
					.convertStringToBoolean(enableSwitchPse));

			/**
			 * Set maxpowerBudget
			 */
			colName = "maxpowerBudget";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String maxpowerBudget = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			hiveAp.setMaxpowerBudget((short)AhRestoreCommons.convertInt(maxpowerBudget));

			/**
			 * Set managementType
			 */
			colName = "managementType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String managementType = isColPresent ? xmlParser
					.getColVal(i, colName) : String.valueOf(HiveAp.MANAGERMENT_TYPE_DYNAMIC);
			hiveAp.setManagementType((short)AhRestoreCommons.convertInt(managementType));

			/**
			 * Set enableSwitchPriority
			 */
			colName = "enableSwitchPriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableSwitchPriority = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			hiveAp.setEnableSwitchPriority(AhRestoreCommons
					.convertStringToBoolean(enableSwitchPriority));

			/**
			 * Set powerGuardBand
			 */
			colName = "powerGuardBand";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String powerGuardBand = isColPresent ? xmlParser
					.getColVal(i, colName) : "10";
			hiveAp.setPowerGuardBand((short)AhRestoreCommons.convertInt(powerGuardBand));

			/**
			 * Set enablePoeLegacy
			 */
			colName = "enablePoeLegacy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enablePoeLegacy = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEnablePoeLegacy(AhRestoreCommons
					.convertStringToBoolean(enablePoeLegacy));

			/**
			 * Set enablePoeLldp
			 */
			colName = "enablePoeLldp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enablePoeLldp = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEnablePoeLldp(AhRestoreCommons
					.convertStringToBoolean(enablePoeLldp));

			/************* end ****************/

			/**
			 * Set switchStormControlMode
			 */
			colName = "switchStormControlMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String switchStormControlMode = isColPresent ? xmlParser
					.getColVal(i, colName) : "0";
			hiveAp.setSwitchStormControlMode((short)AhRestoreCommons.convertInt(switchStormControlMode));

			/**
			 * Set enableOverrideStormControl
			 */
			colName = "enableOverrideStormControl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableOverrideStormControl = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			hiveAp.setEnableOverrideStormControl(AhRestoreCommons.convertStringToBoolean(enableOverrideStormControl));

			/* Added from Chesapeake end */
			/**
			 * Set overrideIgmpSnooping
			 */
			colName = "overrideIgmpSnooping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String overrideIgmpSnooping = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setOverrideIgmpSnooping(AhRestoreCommons
					.convertStringToBoolean(overrideIgmpSnooping));

			/**
			 * Set enableImmediateLeave
			 */
			colName = "enableImmediateLeave";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableImmediateLeave = isColPresent ? xmlParser.getColVal(i,colName) : "true";
			hiveAp.setEnableImmediateLeave(AhRestoreCommons.convertStringToBoolean(enableImmediateLeave));
			/**
			 * Set enableReportSuppression
			 */
			colName = "enableReportSuppression";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableReportSuppression = isColPresent ? xmlParser.getColVal(i,colName) : "true";
			hiveAp.setEnableReportSuppression(AhRestoreCommons.convertStringToBoolean(enableReportSuppression));

			/**
			 * Set globalDelayLeaveQueryInterval
			 */
			colName = "globalDelayLeaveQueryInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			String globalDelayLeaveQueryInterval = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			hiveAp.setGlobalDelayLeaveQueryInterval(Integer.valueOf(globalDelayLeaveQueryInterval));
			/**
			 * Set globalDelayLeaveQueryCount
			 */
			colName = "globalDelayLeaveQueryCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			String globalDelayLeaveQueryCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			hiveAp.setGlobalDelayLeaveQueryCount(Integer.valueOf(globalDelayLeaveQueryCount));
			/**
			 * Set globalRouterPortAginTime
			 */
			colName = "globalRouterPortAginTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			String globalRouterPortAginTime = isColPresent ? xmlParser.getColVal(i, colName) : "250";
			hiveAp.setGlobalRouterPortAginTime(Integer.valueOf(globalRouterPortAginTime));
			/**
			 * Set globalRobustnessCount
			 */
			colName = "globalRobustnessCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hive_ap", colName);
			String globalRobustnessCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			hiveAp.setGlobalRobustnessCount(Integer.valueOf(globalRobustnessCount));


			/**
			 * Set PoE Mode
			 */
			colName = "poemode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String poemode = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(HiveAp.POE_802_3_AUTO);
			hiveAp.setPoeMode(Short.valueOf(poemode));

			/**
			 * Set PoE Primary Eth
			 */
			colName = "poeprimaryeth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String poeprimaryeth = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(HiveAp.POE_PRIMARY_ETH0);
			hiveAp.setPoePrimaryEth(Short.valueOf(poeprimaryeth));

			/**
			 * Set overrideNetworkPolicySetting
			 */
			colName = "overrideNetworkPolicySetting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String overrideNetworkPolicySetting = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setOverrideNetworkPolicySetting(AhRestoreCommons
					.convertStringToBoolean(overrideNetworkPolicySetting));

			/**
			 * set stp settings
			 */
			colName = "device_stp_settings_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String device_stp_settings_id = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			if (device_stp_settings_id != null && !device_stp_settings_id.equalsIgnoreCase("-1") && !device_stp_settings_id.equalsIgnoreCase("null")){
				DeviceStpSettings settings = new DeviceStpSettings();
				settings.setId(AhRestoreCommons.convertString2Long(device_stp_settings_id));
				hiveAp.setDeviceStpSettings(settings);
			}

			/**
			 * Set overrideCaptureDataByCWP
			 */
			colName = "overrideCaptureDataByCWP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String overrideCaptureDataByCWP = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			hiveAp.setOverrideCaptureDataByCWP(AhRestoreCommons
					.convertStringToBoolean(overrideCaptureDataByCWP));

			/**
			 * Set enableCaptureDataByCWP
			 */
			colName = "enableCaptureDataByCWP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableCaptureDataByCWP = isColPresent ? xmlParser.getColVal(i,colName) : "true";
			hiveAp.setEnableCaptureDataByCWP(AhRestoreCommons.convertStringToBoolean(enableCaptureDataByCWP));

			/**
			 * set completeUpdateTag
			 */
			colName = "completeUpdateTag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String completeUpdateTag = isColPresent ? xmlParser.getColVal(i,colName) : null;
			hiveAp.setCompleteUpdateTag(completeUpdateTag);

			/**
			 * set device tx retry
			 */
			colName = "deviceTxRetry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String deviceTxRetry = isColPresent ? xmlParser.getColVal(i,colName) : String.valueOf(HiveAp.TX_RETRY_RATE);
			hiveAp.setDeviceTxRetry(AhRestoreCommons.convertInt(deviceTxRetry));

			/**
			 * set client tx retry
			 */
			colName = "clientTxRetry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String clientTxRetry = isColPresent ? xmlParser.getColVal(i,colName) : String.valueOf(HiveAp.TX_RETRY_RATE);
			hiveAp.setClientTxRetry(AhRestoreCommons.convertInt(clientTxRetry));
			
			/**
			 * Set overrideEnableDelayAlarm from Guadalupe
			 */
			colName = "overrideEnableDelayAlarm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String overrideEnableDelayAlarm = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			hiveAp.setOverrideEnableDelayAlarm(AhRestoreCommons
					.convertStringToBoolean(overrideEnableDelayAlarm));
			
			/**
			 * Set enableDelayAlarm from Guadalupe
			 */
			colName = "enableDelayAlarm";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String enableDelayAlarm = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			hiveAp.setEnableDelayAlarm(AhRestoreCommons
					.convertStringToBoolean(enableDelayAlarm));
			
			/**
			 * Set supplemental_cli_id
			 */
		
			colName = "supplemental_cli_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap", colName);
			String supplemental_cli_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (supplemental_cli_id != null && !(supplemental_cli_id.trim().equals(""))
					&& !(supplemental_cli_id.trim().equalsIgnoreCase("null"))) {
				Long supplemental_cli_id_new = AhRestoreNewMapTools
						.getMapCLIBlob(AhRestoreCommons
								.convertLong(supplemental_cli_id));
				if (null != supplemental_cli_id_new) {
					hiveAp.setSupplementalCLI(AhRestoreNewTools.CreateBoWithId(
							CLIBlob.class, supplemental_cli_id_new));
				} else {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find the new supplemental CLI id mapping to old id in ap:"
									+ supplemental_cli_id);
				}
			}
			

			hiveApInfo.add(hiveAp);
		}
		return hiveApInfo;
	}

	private static Map<String, List<HiveApIpRoute>> getAllIpRoutes()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApIpRoute>> ipRouteInfo = new HashMap<String, List<HiveApIpRoute>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_ip_route.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_ip_route");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_ip_route.xml file.");
			return ipRouteInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_ip_route
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set gateway
			 */
			colName = "gateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			if (!isColPresent) {
				/**
				 * The gateway column must be exist in the table of
				 * hive_ap_ip_route
				 */
				continue;
			}

			String gateway = xmlParser.getColVal(i, colName);
			if (gateway == null || gateway.trim().equals("")
					|| gateway.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set netmask
			 */
			colName = "netmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			if (!isColPresent) {
				/**
				 * The netmask column must be exist in the table of
				 * hive_ap_ip_route
				 */
				continue;
			}

			String netmask = xmlParser.getColVal(i, colName);
			if (netmask == null || netmask.trim().equals("")
					|| netmask.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set sourceip
			 */
			colName = "sourceip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			if (!isColPresent) {
				/**
				 * The sourceip column must be exist in the table of
				 * hive_ap_ip_route
				 */
				continue;
			}

			String sourceip = xmlParser.getColVal(i, colName);
			if (sourceip == null || sourceip.trim().equals("")
					|| sourceip.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set advertiseCvg
			 */
			colName = "advertiseCvg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			boolean advertiseCvg = AhRestoreCommons
					.convertStringToBoolean(isColPresent ? xmlParser.getColVal(
							i, colName) : "false");

			/**
			 * Set distributeBR
			 */
			colName = "distributeBR";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_route", colName);
			String distributeBR = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			boolean bDistributeBR = AhRestoreCommons
					.convertStringToBoolean(distributeBR);
			if (RestoreHiveAp.restore_from_50r0_after
					&& RestoreHiveAp.restore_from_50r3_before) {
				bDistributeBR = true;
			}

			HiveApIpRoute s_route = new HiveApIpRoute();
			s_route.setGateway(gateway);
			s_route.setNetmask(netmask);
			s_route.setSourceIp(sourceip);
			s_route.setAdvertiseCvg(advertiseCvg);
			s_route.setDistributeBR(bDistributeBR);

			if (ipRouteInfo.get(hiveApId) == null) {
				List<HiveApIpRoute> d_routeList = new ArrayList<HiveApIpRoute>();
				d_routeList.add(s_route);
				ipRouteInfo.put(hiveApId, d_routeList);
			} else {
				ipRouteInfo.get(hiveApId).add(s_route);
			}
		}
		return ipRouteInfo;
	}

	private static Map<String, List<HiveApDynamicRoute>> getAllDynamicRoutes()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApDynamicRoute>> dynamicRouteInfo = new HashMap<String, List<HiveApDynamicRoute>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_dynamic_route.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_dynamic_route");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_dynamic_route.xml file.");
			return dynamicRouteInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dynamic_route", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_dynamic_route
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set neighbormac
			 */
			colName = "neighbormac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dynamic_route", colName);
			if (!isColPresent) {
				/**
				 * The neighbormac column must be exist in the table of
				 * hive_ap_dynamic_route
				 */
				continue;
			}

			String neighbormac = xmlParser.getColVal(i, colName);
			if (neighbormac == null || neighbormac.trim().equals("")
					|| neighbormac.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set routemaximun
			 */
			colName = "routemaximun";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dynamic_route", colName);
			String routemaximun = isColPresent ? xmlParser
					.getColVal(i, colName) : "67";

			/**
			 * Set routeminimun
			 */
			colName = "routeminimun";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dynamic_route", colName);
			String routeminimun = isColPresent ? xmlParser
					.getColVal(i, colName) : "67";

			HiveApDynamicRoute d_route = new HiveApDynamicRoute();
			d_route.setNeighborMac(neighbormac.replaceAll(":", ""));
			d_route.setRouteMaximun(AhRestoreCommons.convertInt(routemaximun));
			d_route.setRouteMinimun(AhRestoreCommons.convertInt(routeminimun));

			if (dynamicRouteInfo.get(hiveApId) == null) {
				List<HiveApDynamicRoute> d_routeList = new ArrayList<HiveApDynamicRoute>();
				d_routeList.add(d_route);
				dynamicRouteInfo.put(hiveApId, d_routeList);
			} else {
				dynamicRouteInfo.get(hiveApId).add(d_route);
			}
		}
		return dynamicRouteInfo;
	}

	private static Map<String, List<HiveApStaticRoute>> getAllStaticRoutes()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApStaticRoute>> staticRouteInfo = new HashMap<String, List<HiveApStaticRoute>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_static_route.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_static_route");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_static_route.xml file.");
			return staticRouteInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_static_route", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_static_route
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set nexthopmac
			 */
			colName = "nexthopmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_static_route", colName);
			if (!isColPresent) {
				/**
				 * The nexthopmac column must be exist in the table of
				 * hive_ap_static_route
				 */
				continue;
			}

			String nexthopmac = xmlParser.getColVal(i, colName);
			if (nexthopmac == null || nexthopmac.trim().equals("")
					|| nexthopmac.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set destinationmac
			 */
			colName = "destinationmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_static_route", colName);
			if (!isColPresent) {
				/**
				 * The destinationmac column must be exist in the table of
				 * hive_ap_static_route
				 */
				continue;
			}

			String destinationmac = xmlParser.getColVal(i, colName);
			if (destinationmac == null || destinationmac.trim().equals("")
					|| destinationmac.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set interfacetype
			 */
			colName = "interfacetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_static_route", colName);
			String interfacetype = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(HiveApStaticRoute.STATIC_ROUTE_IF_ETH);

			HiveApStaticRoute s_route = new HiveApStaticRoute();
			s_route.setDestinationMac(destinationmac.replaceAll(":", ""));
			s_route.setNextHopMac(nexthopmac.replaceAll(":", ""));
			s_route.setInterfaceType((short) AhRestoreCommons
					.convertInt(interfacetype));

			if (staticRouteInfo.get(hiveApId) == null) {
				List<HiveApStaticRoute> d_routeList = new ArrayList<HiveApStaticRoute>();
				d_routeList.add(s_route);
				staticRouteInfo.put(hiveApId, d_routeList);
			} else {
				staticRouteInfo.get(hiveApId).add(s_route);
			}
		}
		return staticRouteInfo;
	}

	private static Map<String, Set<MgmtServiceIPTrack>> getAllIpTracks()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_ap_ip_track.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_ip_track");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<MgmtServiceIPTrack>> trackInfo = new HashMap<String, Set<MgmtServiceIPTrack>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_track", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_ip_track
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set iptracks_id
			 */
			colName = "iptracks_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_track", colName);
			if (!isColPresent) {
				/**
				 * The iptracks_id column must be exist in the table of
				 * hive_ap_ip_track
				 */
				continue;
			}

			String iptracks_id = xmlParser.getColVal(i, colName);
			if (iptracks_id == null || iptracks_id.trim().equals("")
					|| iptracks_id.trim().equalsIgnoreCase("null")) {
				continue;
			}
			Long iptracks_id_new = AhRestoreNewMapTools
					.getMapMgmtIpTracking(AhRestoreCommons
							.convertLong(iptracks_id));
			if (null != iptracks_id_new) {
				if (trackInfo.get(hive_ap_id) == null) {
					Set<MgmtServiceIPTrack> trackSet = new HashSet<MgmtServiceIPTrack>();
					trackInfo.put(hive_ap_id, trackSet);
				}
				trackInfo.get(hive_ap_id).add(
						AhRestoreNewTools.CreateBoWithId(
								MgmtServiceIPTrack.class, iptracks_id_new));
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new ip tracking id mapping to old id:"
								+ iptracks_id);
			}
		}
		return trackInfo;
	}

	private static Map<String, Set<UserProfile>> getAllEthCwpUserProfiles()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_ap_user_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_user_profile");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<UserProfile>> userProfileInfo = new HashMap<String, Set<UserProfile>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_track", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_user_profile
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set user_profile_id
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ip_track", colName);
			if (!isColPresent) {
				/**
				 * The user_profile_id column must be exist in the table of
				 * hive_ap_user_profile
				 */
				continue;
			}

			String user_profile_id = xmlParser.getColVal(i, colName);
			if (user_profile_id == null || user_profile_id.trim().equals("")
					|| user_profile_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			Long user_profile_id_new = AhRestoreNewMapTools
					.getMapUserProfile(AhRestoreCommons
							.convertLong(user_profile_id));
			if (null != user_profile_id_new) {
				if (userProfileInfo.get(hive_ap_id) == null) {
					Set<UserProfile> userProfileSet = new HashSet<UserProfile>();
					userProfileInfo.put(hive_ap_id, userProfileSet);
				}
				userProfileInfo.get(hive_ap_id).add(
						AhRestoreNewTools.CreateBoWithId(UserProfile.class,
								user_profile_id_new));
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new user profile id mapping to old id:"
								+ user_profile_id);
			}
		}
		return userProfileInfo;
	}

	private static Map<String, Set<VlanDhcpServer>> getAllDhcpServers()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_ap_dhcp_server.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_dhcp_server");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<VlanDhcpServer>> dhcpServerInfo = new HashMap<String, Set<VlanDhcpServer>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dhcp_server", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_dhcp_server
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set dhcpservers_id
			 */
			colName = "dhcpservers_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_dhcp_server", colName);
			if (!isColPresent) {
				/**
				 * The dhcpservers_id column must be exist in the table of
				 * hive_ap_dhcp_server
				 */
				continue;
			}

			String dhcpservers_id = xmlParser.getColVal(i, colName);
			if (dhcpservers_id == null || dhcpservers_id.trim().equals("")
					|| dhcpservers_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			Long dhcpservers_id_new = AhRestoreNewMapTools
					.getMapVlanDhcpServer(AhRestoreCommons
							.convertLong(dhcpservers_id));
			if (null != dhcpservers_id_new) {
				if (dhcpServerInfo.get(hive_ap_id) == null) {
					Set<VlanDhcpServer> dhcpServerSet = new HashSet<VlanDhcpServer>();
					dhcpServerInfo.put(hive_ap_id, dhcpServerSet);
				}
				dhcpServerInfo.get(hive_ap_id).add(
						AhRestoreNewTools.CreateBoWithId(VlanDhcpServer.class,
								dhcpservers_id_new));
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new DHCP server id mapping to old id:"
								+ dhcpservers_id);
			}
		}
		return dhcpServerInfo;
	}

	private static Map<String, List<HiveApSsidAllocation>> getAllDisabledSsids()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hive_ap_ssid_allocation.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_ssid_allocation");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<HiveApSsidAllocation>> ssidInfo = new HashMap<String, List<HiveApSsidAllocation>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ssid_allocation", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_ssid_allocation
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set ssid
			 */
			colName = "ssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ssid_allocation", colName);
			if (!isColPresent) {
				/**
				 * The ssid column must be exist in the table of
				 * hive_ap_ssid_allocation
				 */
				continue;
			}

			String ssid = xmlParser.getColVal(i, colName);
			if (ssid == null || ssid.trim().equals("")
					|| ssid.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set intertype
			 */
			colName = "intertype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_ssid_allocation", colName);
			String intertype = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(HiveApSsidAllocation.WIFI2G);

			Long ssid_new = AhRestoreNewMapTools.getMapSsid(AhRestoreCommons
					.convertLong(ssid));
			if (null != ssid_new) {
				if (ssidInfo.get(hive_ap_id) == null) {
					List<HiveApSsidAllocation> ssidList = new ArrayList<HiveApSsidAllocation>();
					ssidInfo.put(hive_ap_id, ssidList);
				}
				HiveApSsidAllocation disabledSsid = new HiveApSsidAllocation();
				disabledSsid.setSsid(ssid_new);
				disabledSsid.setInterType((short) AhRestoreCommons
						.convertInt(intertype));
				ssidInfo.get(hive_ap_id).add(disabledSsid);
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new ssid profile id mapping to old id:"
								+ ssid);
			}
		}
		return ssidInfo;
	}

	private static Map<String, List<HiveApMultipleVlan>> getAllMultipleVlan()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApMultipleVlan>> multipleVlanInfo = new HashMap<String, List<HiveApMultipleVlan>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_multiple_vlan.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_multiple_vlan");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_multiple_vlan.xml file.");
			return multipleVlanInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_multiple_vlan", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_multiple_vlan
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set vlanid
			 */
			colName = "vlanid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_multiple_vlan", colName);
			if (!isColPresent) {
				/**
				 * The gateway column must be exist in the table of
				 * hive_ap_multiple_vlan
				 */
				continue;
			}

			String vlanid = xmlParser.getColVal(i, colName);
			if (vlanid == null || vlanid.trim().equals("")
					|| vlanid.trim().equalsIgnoreCase("null")) {
				continue;
			}
			HiveApMultipleVlan s_route = new HiveApMultipleVlan();
			s_route.setVlanid(vlanid);

			if (multipleVlanInfo.get(hiveApId) == null) {
				List<HiveApMultipleVlan> d_routeList = new ArrayList<HiveApMultipleVlan>();
				d_routeList.add(s_route);
				multipleVlanInfo.put(hiveApId, d_routeList);
			} else {
				multipleVlanInfo.get(hiveApId).add(s_route);
			}
		}
		return multipleVlanInfo;
	}

	private static Map<String, List<HiveApPreferredSsid>> getAllHiveApPreferredSsids()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApPreferredSsid>> wfcmPreferredSsids = new HashMap<String, List<HiveApPreferredSsid>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hiveap_preferred_ssid.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hiveap_preferred_ssid");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hiveap_preferred_ssid.xml file.");
			return wfcmPreferredSsids;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			HiveApPreferredSsid preferredSsid = new HiveApPreferredSsid();
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_preferred_ssid", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hiveap_preferred_ssid
				 */
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'hiveap_preferred_ssid' data be lost, cause: 'hive_ap_id' column is not exists.");
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'hiveap_preferred_ssid' data be lost, cause: 'hive_ap_id' column value is null.");
				continue;
			}

			/**
			 * Set preferredid
			 */
			colName = "preferredid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_preferred_ssid", colName);
			if (!isColPresent) {
				/**
				 * The preferredid column must be exist in the table of
				 * hiveap_preferred_ssid
				 */
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'hiveap_preferred_ssid' data be lost, cause: 'preferredid' column is not exists.");
				continue;
			}

			String preferredid = xmlParser.getColVal(i, colName);
			Long wifiClientSsid = AhRestoreNewMapTools.getMapWifiClientPreferredSsid(AhRestoreCommons.convertLong(preferredid));
			if (preferredid == null || preferredid.trim().equals("")
				|| preferredid.trim().equalsIgnoreCase("null") || wifiClientSsid == null)
			{
				BeLogTools.error(BeLogTools.ERROR, "Restore table 'hiveap_preferred_ssid' data be lost, cause: 'preferredid' column value is null.");
				continue;
			}
			preferredSsid.setPreferredId(wifiClientSsid);

			/**
			 * Set priority
			 */
			colName = "priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"hiveap_preferred_ssid", colName);
			String priority = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			preferredSsid.setPriority(AhRestoreCommons.convertInt(priority));

			if (wfcmPreferredSsids.get(hiveApId) == null) {
				List<HiveApPreferredSsid> preferredSsids = new ArrayList<HiveApPreferredSsid>();
				preferredSsids.add(preferredSsid);
				wfcmPreferredSsids.put(hiveApId, preferredSsids);
			} else {
				wfcmPreferredSsids.get(hiveApId).add(preferredSsid);
			}
		}
		return wfcmPreferredSsids;
	}


	private static Map<String, List<HiveAPVirtualConnection>> getAllVirtualConnections()
			throws AhRestoreException, AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of HIVE_AP_VIRTUAL_CONNECTION.xml
		 */
		boolean restoreRet = xmlParser
				.readXMLFile("hive_ap_virtual_connection");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<HiveAPVirtualConnection>> map = new HashMap<String, List<HiveAPVirtualConnection>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_ssid_allocation
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			HiveAPVirtualConnection virtualBo = new HiveAPVirtualConnection();

			/**
			 * Set forwardName
			 */
			colName = "forwardName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			String forwardName = AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName));
			virtualBo.setForwardName(forwardName);

			/**
			 * Set forwardAction
			 */
			colName = "forwardAction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			int forwardAction = AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName));
			virtualBo.setForwardAction((byte) forwardAction);

			/**
			 * Set interface_in
			 */
			colName = "interface_in";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			int interface_in = AhRestoreCommons.convertInt(xmlParser.getColVal(
					i, colName));
			virtualBo.setInterface_in((byte) interface_in);

			/**
			 * Set interface_out
			 */
			colName = "interface_out";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			int interface_out = AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName));
			virtualBo.setInterface_out((byte) interface_out);

			/**
			 * Set sourceMac
			 */
			colName = "sourceMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			String sourceMac = AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName));
			virtualBo.setSourceMac(sourceMac);

			/**
			 * Set destMac
			 */
			colName = "destMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			String destMac = AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName));
			virtualBo.setDestMac(destMac);

			/**
			 * Set txMac
			 */
			colName = "txMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			String txMac = AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName));
			virtualBo.setTxMac(txMac);

			/**
			 * Set rxMac
			 */
			colName = "rxMac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_virtual_connection", colName);
			String rxMac = AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName));
			virtualBo.setRxMac(rxMac);

			if (map.get(hive_ap_id) == null) {
				List<HiveAPVirtualConnection> list = new ArrayList<HiveAPVirtualConnection>();
				map.put(hive_ap_id, list);
			}

			map.get(hive_ap_id).add(virtualBo);
		}
		return map;
	}

	private static Map<String, List<HiveApLearningMac>> getAllLearningMacs()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApLearningMac>> learningMacInfo = new HashMap<String, List<HiveApLearningMac>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_learning_mac.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_learning_mac");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_learning_mac.xml file.");
			return learningMacInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_learning_mac", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_learning_mac
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set learning_mac_id
			 */
			colName = "learning_mac_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_learning_mac", colName);
			if (!isColPresent) {
				/**
				 * The learning_mac_id column must be exist in the table of
				 * hive_ap_learning_mac
				 */
				continue;
			}

			String learning_mac_id = xmlParser.getColVal(i, colName);
			if (learning_mac_id == null || learning_mac_id.trim().equals("")
					|| learning_mac_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set learningmactype
			 */
			colName = "learningmactype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_learning_mac", colName);
			String learningmactype = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(HiveApLearningMac.LEARNING_MAC_ETH0);

			Long learning_mac_id_new = AhRestoreNewMapTools
					.getMapMacAddress(AhRestoreCommons
							.convertLong(learning_mac_id));
			if (null != learning_mac_id_new) {
				if (learningMacInfo.get(hive_ap_id) == null) {
					List<HiveApLearningMac> learningList = new ArrayList<HiveApLearningMac>();
					learningMacInfo.put(hive_ap_id, learningList);
				}
				HiveApLearningMac learningMac = new HiveApLearningMac();
				learningMac.setMac(AhRestoreNewTools.CreateBoWithId(
						MacOrOui.class, learning_mac_id_new));
				learningMac.setLearningMacType((short) AhRestoreCommons
						.convertInt(learningmactype));
				learningMacInfo.get(hive_ap_id).add(learningMac);
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new macOrOui id mapping to old id:"
								+ learning_mac_id);
			}
		}
		return learningMacInfo;
	}

	public static boolean restoreHiveAps() {
		ap_template_map = new HashMap<String, String>();
		//leafNodes = new HashMap<String, MapLeafNode>();
		second_vpn_gateway_map = new HashMap<String, String>();
		long start = System.currentTimeMillis();

		try {
//			List<HiveAp> allHiveAp = getAllHiveAp();
//
//			if (null == allHiveAp || allHiveAp.isEmpty()) {
//				AhRestoreDBTools
//						.logRestoreMsg("Total " + apName + " is empty.");
//				return false;
//			} else {
				Map<String, List<HiveApDynamicRoute>> allDynamicRoute = null;
				Map<String, List<HiveApStaticRoute>> allStaticRoute = null;
				Map<String, List<HiveApIpRoute>> allIpRoute = null;
				Map<String, Set<MgmtServiceIPTrack>> allIpTracks = null;
				Map<String, Set<VlanDhcpServer>> allDhcpServers = null;
				Map<String, List<HiveApSsidAllocation>> allSsidAllocation = null;

				Map<String, List<HiveApMultipleVlan>> allMultipleVlan = null;
				Map<String, List<HiveApLearningMac>> allLearningMacs = null;
				Map<String, Set<UserProfile>> allEthCwpUserProfiles = null;
				Map<String, List<HiveAPVirtualConnection>> allVirtualConnections = null;
				Map<String, Map<Long, DeviceInterface>> allDeviceInterfaces = null;
				Map<String, List<USBModemProfile>> allUsbModemList = null;
				Map<String, List<HiveApInternalNetwork>> allIntNetworkList = null;
				Map<String, List<ConfigTemplateStormControl>> allStormControlList = null;
				Map<String, List<HiveApPreferredSsid>> allPreferredSsids = null;
				Map<String, DeviceStpSettings> deviceStpSettings = null;
				Map<String, List<InterfaceStpSettings>> portLevelSettings = getAllPortLevelSettings();
				Map<String, List<DeviceMstpInstancePriority>> instancePrioritySettings = getAllInstancePrioritySettings();
				Map<String, List<InterfaceMstpSettings>> interfaceMstpSettings = getAllPortMstpSettings();

				try {
					allIpRoute = getAllIpRoutes();
				} catch (Exception e5) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get ipRoute from xml file.", e5);
				}
				try {
					allDynamicRoute = getAllDynamicRoutes();
				} catch (Exception e4) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get dynamicRoute from xml file.", e4);
				}
				try {
					allStaticRoute = getAllStaticRoutes();
				} catch (Exception e3) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get staticRoute from xml file.", e3);
				}
				try {
					allIpTracks = getAllIpTracks();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get IP track from xml file.", e1);
				}
				try {
					allDhcpServers = getAllDhcpServers();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get DHCP Servers Settings from xml file.",
							e1);
				}
				try {
					allSsidAllocation = getAllDisabledSsids();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ " SSID Allocation from xml file.", e1);
				}

				try {
					allMultipleVlan = getAllMultipleVlan();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ " Multiple Vlan from xml file.", e1);
				}

				try {
					allPreferredSsids = getAllHiveApPreferredSsids();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ "Preferred Ssids from xml file.", e1);
				}

				try {
					allVirtualConnections = getAllVirtualConnections();
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ " Virtual Connections from xml file.", e);
				}

				try {
					allLearningMacs = getAllLearningMacs();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ " Learning MAC from xml file.", e1);
				}

				try {
					allEthCwpUserProfiles = getAllEthCwpUserProfiles();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg("Cannot get " + apName
							+ " Learning MAC from xml file.", e1);
				}

				try {
					allDeviceInterfaces = getAllDeviceInterfaces();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get Device Interface from xml file.", e1);
				}

				try {
					allUsbModemList = getAllUSBModemProfiles();
				} catch (Exception e1) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get USB Modem from xml file.", e1);
				}

				try {
					allIntNetworkList = getAllIntNetwork();
				} catch (Exception e3) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get Internal Networks from xml file.", e3);
				}

				try {
					allStormControlList = getAllStormControl();
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get Internal Networks from xml file.", e);
				}

				try {
					deviceStpSettings = getAllDeviceStpSettings();
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg(
							"Cannot get Device Stp Settings from xml file.", e);
				}

				// default profiles
				RadioProfile radioProfilea = null;
				RadioProfile radioProfilebg = null;
				RadioProfile radioProfilena = null;
				RadioProfile radioProfileng = null;
				ConfigTemplate defaultTemplate = null;


				List<Long> oldIdList = new ArrayList<Long>();
				List<HiveAp> validHiveAps = new ArrayList<HiveAp>();

				AhRestoreGetXML xmlParser = new AhRestoreGetXML();
				/**
				 * Check validation of hive_ap.xml
				 */
				xmlParser.convertXMLfile("hive_ap");

				final String tableName = "convert_hive_ap";
				int index = 0;

				long totalApCount = 0;

				while (true) {
					String fileName = tableName;
					if (index > 0) {
						fileName = tableName + "_" + index;
					}
					index++;

					List<HiveAp> allHiveAp = getAllHiveAp(fileName);
					if (allHiveAp == null) {
						break;
					}
					if (!allHiveAp.isEmpty()) {
						totalApCount = totalApCount + allHiveAp.size();
					}

				for (HiveAp hiveAp : allHiveAp) {
					if (null != allIpRoute) {
						hiveAp.setIpRoutes(allIpRoute.get(hiveAp.getId()
								.toString()));
					}
					if (null != allDynamicRoute) {
						hiveAp.setDynamicRoutes(allDynamicRoute.get(hiveAp
								.getId().toString()));
					}
					if (null != allStaticRoute) {
						hiveAp.setStaticRoutes(allStaticRoute.get(hiveAp
								.getId().toString()));
					}
					if (null != allIntNetworkList) {
						hiveAp.setInternalNetworks(allIntNetworkList.get(hiveAp
								.getId().toString()));
					}
					if (null != allStormControlList) {
						hiveAp.setStormControlList(allStormControlList
								.get(hiveAp.getId().toString()));
					}
					if (null != allIpTracks) {// indicate track file existed
						Set<MgmtServiceIPTrack> tracks = allIpTracks.get(hiveAp
								.getId().toString());
						if (null != tracks) {
							HmUpgradeLog upgradeLog = new HmUpgradeLog();
							upgradeLog.setFormerContent(nmsName
									+ " applied IP tracking in " + apName
									+ " configurations.");
							upgradeLog
									.setPostContent(nmsName
											+ " now applies IP tracking in network policies.");
							upgradeLog
									.setRecommendAction("If you need to change IP tracking for one or more "
											+ apName
											+ "s, "
											+ "go to the Configuration > Network Policies page, select a profile, and modify the values.");
							upgradeLog.setOwner(AhRestoreNewTools
									.CreateBoWithId(HmDomain.class, hiveAp
											.getOwner().getId()));
							upgradeLog.setLogTime(new HmTimeStamp(System
									.currentTimeMillis(), hiveAp.getOwner()
									.getTimeZoneString()));
							upgradeLog
									.setAnnotation("Click to add an annotation");
							try {
								QueryUtil.createBo(upgradeLog);
							} catch (Exception e) {
								AhRestoreDBTools
										.logRestoreMsg("insert upgrade log error for user profile");
								AhRestoreDBTools.logRestoreMsg(e.getMessage());
							}
						}
					}
					if (null != allDhcpServers) {
						hiveAp.setDhcpServers(allDhcpServers.get(hiveAp.getId()
								.toString()));
						if (null != hiveAp.getDhcpServers()) {
							int count = 0;
							for (VlanDhcpServer server : hiveAp
									.getDhcpServers()) {
								if (server.getTypeFlag() == VlanDhcpServer.ENABLE_DHCP_SERVER) {
									count++;
								}
							}
							hiveAp.setDhcpServerCount(count);
						}
					}
					if (null != allSsidAllocation) {
						hiveAp.setDisabledSsids(allSsidAllocation.get(hiveAp
								.getId().toString()));
					}
					if (null != allMultipleVlan) {
						hiveAp.setMultipleVlan(allMultipleVlan.get(hiveAp
								.getId().toString()));
					}
					if (null != allPreferredSsids) {
						hiveAp.setWifiClientPreferredSsids(allPreferredSsids.get(hiveAp
								.getId().toString()));
					}
					if (null != allVirtualConnections) {
						hiveAp.setVirtualConnections(allVirtualConnections
								.get(hiveAp.getId().toString()));
					}
					if (null != allLearningMacs) {
						hiveAp.setLearningMacs(allLearningMacs.get(hiveAp
								.getId().toString()));
					}
					if (null != allEthCwpUserProfiles) {
						hiveAp.setEthCwpRadiusUserProfiles(allEthCwpUserProfiles
								.get(hiveAp.getId().toString()));
					}
					if (null != allUsbModemList) {
						hiveAp.setUsbModemList(allUsbModemList.get(hiveAp
								.getId().toString()));
					}

					// set wifi0 radio profile if no profile assigned.
					if (null == hiveAp.getWifi0RadioProfile()) {
						AhRestoreDBTools
								.logRestoreMsg(apName
										+ ":"
										+ hiveAp.getHostName()
										+ " have no wifi0 radio proifle, try to assign the default value.");
						if (hiveAp.is11nHiveAP()) {
							if (null == radioProfileng) {
								radioProfileng = HmBeParaUtil
										.getDefaultRadioNGProfile();
							}
							hiveAp.setWifi0RadioProfile(radioProfileng);
						} else {
							if (null == radioProfilebg) {
								radioProfilebg = HmBeParaUtil
										.getDefaultRadioBGProfile();
							}
							hiveAp.setWifi0RadioProfile(radioProfilebg);
						}
					}
					// set wifi1 radio profile if no profile assigned.
					if (null == hiveAp.getWifi1RadioProfile()) {
						AhRestoreDBTools
								.logRestoreMsg(apName
										+ ":"
										+ hiveAp.getHostName()
										+ " have no wifi1 radio proifle, try to assign the default value.");
						if (hiveAp.is11nHiveAP()) {
							if (null == radioProfilena) {
								radioProfilena = HmBeParaUtil
										.getDefaultRadioNAProfile();
							}
							hiveAp.setWifi1RadioProfile(radioProfilena);
						} else {
							if (null == radioProfilea) {
								radioProfilea = HmBeParaUtil
										.getDefaultRadioAProfile();
							}
							hiveAp.setWifi1RadioProfile(radioProfilea);
						}
					}

					// set config template if no profile assigned.
					if (null == hiveAp.getConfigTemplate()) {
						AhRestoreDBTools
								.logRestoreMsg(apName
										+ ":"
										+ hiveAp.getHostName()
										+ " have no network policy, try to assign the default value.");
						if (null == defaultTemplate) {
							defaultTemplate = HmBeParaUtil.getDefaultTemplate();
						}
						hiveAp.setConfigTemplate(defaultTemplate);
					}

					if (null == hiveAp.getDeviceStpSettings()) {
						if(hiveAp.isSwitchProduct()){
							AhRestoreDBTools
							.logRestoreMsg(apName
									+ ":"
									+ hiveAp.getHostName()
									+ " have no stp settings, try to assign the default value.");
							DeviceStpSettings settings = new DeviceStpSettings();
							settings.setOwner(hiveAp.getOwner());
							hiveAp.setDeviceStpSettings(settings);
						}
					}else{
						if(hiveAp.getDeviceStpSettings().getId() != null &&  hiveAp.getDeviceStpSettings().getId() != 0){
							String deviceStpSettingsId = hiveAp.getDeviceStpSettings().getId().toString();
							if(deviceStpSettingsId != null && !deviceStpSettingsId.isEmpty()){
								DeviceStpSettings deviceStp = deviceStpSettings.get(deviceStpSettingsId);
								if(portLevelSettings != null && !portLevelSettings.isEmpty()){
									deviceStp.setInterfaceStpSettings(portLevelSettings.get(deviceStpSettingsId));
								}
								if(interfaceMstpSettings != null && !interfaceMstpSettings.isEmpty()){
									deviceStp.setInterfaceMstpSettings(interfaceMstpSettings.get(deviceStpSettingsId));
								}

								if(instancePrioritySettings != null && !instancePrioritySettings.isEmpty()){
									deviceStp.setInstancePriority(instancePrioritySettings.get(deviceStpSettingsId));
								}
								deviceStp.setId(null);
								hiveAp.setDeviceStpSettings(deviceStp);
							}
						}
					}

					if (null != allDeviceInterfaces) {
						if (allDeviceInterfaces.get(hiveAp.getId().toString()) != null) {
							hiveAp.setDeviceInterfaces(allDeviceInterfaces
									.get(hiveAp.getId().toString()));
						}
						// fix bug 17774
						//restore pppoe config
						DeviceInterface eth0Interface = hiveAp
								.getDeviceInterfaces().get(
										(long) AhInterface.DEVICE_IF_TYPE_ETH0);
						DeviceInterface usbInterface = hiveAp
								.getDeviceInterfaces().get(
										(long) AhInterface.DEVICE_IF_TYPE_USB);
						if(restore_from_60r2_before && hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
								&& hiveAp.isEnablePppoe())
						{
							if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR24 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P ){

								setSwitchDeviceInterfacePPPoe(hiveAp);

							}else if(eth0Interface != null ){
									hiveAp.getEth0Interface().setConnectionType("3");
							}
						}
						
						//from 6.1r5 SR2124P and SR2148P SFP speed always Auto.
						short sfp_port_start = -1, sfp_port_end = -1;
						if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P){
							sfp_port_start = AhInterface.DEVICE_IF_TYPE_ETH25;
						}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2148P){
							sfp_port_start = AhInterface.DEVICE_IF_TYPE_ETH49;
						}
						if(sfp_port_start > 0 && hiveAp.getDeviceInterfaces() != null){
							sfp_port_end = (short)(sfp_port_start + 3);			//4 SFP port
							for(DeviceInterface dInterface : hiveAp.getDeviceInterfaces().values()){
								if(dInterface.getDeviceIfType() >= sfp_port_start && 
										dInterface.getDeviceIfType() <= sfp_port_end){
									dInterface.setSpeed(AhInterface.ETH_SPEED_AUTO);
								}
							}
						}
					}

					// set MAC learning information if there're configured
					// in WLAN policy
					// only for those 3.4 lower backup file
					restoreMacLearningInfos(hiveAp);

					if (!isValid(hiveAp)) {
						continue;
					}
					oldIdList.add(hiveAp.getId());
					validHiveAps.add(hiveAp);
					hiveAp.setId(null);// // set id to null
				}
				}

				if (totalApCount>0) {
					AhRestoreDBTools.logRestoreMsg("Total " + apName + " count is :"
							+ totalApCount);
				} else {
					AhRestoreDBTools
					.logRestoreMsg("Total " + apName + " is empty.");
				}

				if (validHiveAps.size() > 0) {
					QueryUtil.restoreBulkCreateBos(validHiveAps);
					// set id mapping to map tool.
					for (int i = 0; i < validHiveAps.size(); i++) {
						AhRestoreNewMapTools.setMapHiveAP(oldIdList.get(i),
								validHiveAps.get(i).getId());

						// if current AP has configuration for active directory
						// radius setting,
						// update macAddress of current AP to the referenced AD
						// settings.
						HiveAp ap = validHiveAps.get(i);
						if (RestoreUsersAndAccess.RESTORE_FROM_40R1_BEFORE
								&& ap.getRadiusServerProfile() != null) {
							RadiusOnHiveap radius = QueryUtil.findBoById(
									RadiusOnHiveap.class, ap
											.getRadiusServerProfile().getId(),
									new ImplQueryBo());
							if (radius != null
									&& (RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE == radius
											.getDatabaseType() || RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE == radius
											.getDatabaseType())) {
								// radius server is AD

								List<ActiveDirectoryOrLdapInfo> adServersInfo = radius
										.getDirectoryOrLdap();
								if (adServersInfo != null
										&& !adServersInfo.isEmpty()
										&& "".equals(adServersInfo.get(0)
												.getDirectoryOrLdap()
												.getApMac())) {
									// AP macAddress of AD RADIUS setting is
									// empty

									List<ActiveDirectoryOrOpenLdap> adServers = new ArrayList<ActiveDirectoryOrOpenLdap>(
											adServersInfo.size());
									for (ActiveDirectoryOrLdapInfo adServer : adServersInfo) {
										ActiveDirectoryOrOpenLdap directoryOrLdap = adServer
												.getDirectoryOrLdap();
										directoryOrLdap.setApMac(ap
												.getMacAddress());
										adServers.add(directoryOrLdap);
									}
									QueryUtil.bulkUpdateBos(adServers);
								}
							}
						}

						if (restore_from_50r3_before
								&& ap.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY
								&& ap.getConfigTemplate() != null) {
							ConfigTemplate template = QueryUtil.findBoById(
									ConfigTemplate.class, ap
											.getConfigTemplate().getId(),
									new ImplQueryBo());
							// TODO for remove network object in user profile
							// if(template.getMgtNetwork() != null){
							// ap.getOrCreateCvgDPD().setMgtNetwork(template.getMgtNetwork());
							// }
							if (template.getMgmtServiceDns() != null) {
								ap.getOrCreateCvgDPD().setDnsForCVG(
										template.getMgmtServiceDns());
							}
							if (template.getMgmtServiceTime() != null) {
								ap.getOrCreateCvgDPD().setNtpForCVG(
										template.getMgmtServiceTime());
							}

							String hostName = ap.getHostName();
							StringBuffer newHostName = new StringBuffer("");
							if (hostName.length() > 0) {
								// cannot start with number,replace it with n
								if ((hostName.charAt(0) >= 'a' && hostName
										.charAt(0) <= 'z')
										|| (hostName.charAt(0) >= 'A' && hostName
												.charAt(0) <= 'Z')) {
									newHostName.append(hostName.charAt(0));
								} else {
									newHostName.append("n");
								}
								for (int n = 0; n < hostName.length(); n++) {
									if ((hostName.charAt(n) >= '0' && hostName
											.charAt(n) <= '9')
											|| (hostName.charAt(n) >= 'a' && hostName
													.charAt(n) <= 'z')
											|| (hostName.charAt(0) >= 'A' && hostName
													.charAt(0) <= 'Z')
											|| hostName.charAt(n) == '-'
											|| hostName.charAt(n) == '.') {
										newHostName.append(hostName.charAt(n));
									} else {
										newHostName.append("-");
									}
								}
								// cannot end with - or . replace it with n
								if (hostName.charAt(hostName.length() - 1) == '-'
										|| hostName
												.charAt(hostName.length() - 1) == '.') {
									newHostName.append("n");
								}
							}
							ap.setHostName(newHostName.toString());
							QueryUtil.updateBo(ap);
						}

					}
				}
				updateHiveApNeighborInfo();
				updateSecondVPNGatewayInfo();
				long end = System.currentTimeMillis();
				AhRestoreDBTools.logRestoreMsg("Restore " + apName
						+ " completely. count:" + validHiveAps.size()
						+ ", cost:" + (end - start) + " ms.");
				return true;
//			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore " + apName + " error.", e);
			return false;
		} finally {
			leafNodes = null;
			ap_template_map = null;
			second_vpn_gateway_map = null;
		}
	}
	
	public static boolean updateTopologyMapInfoGlobal(){
		leafNodes = new HashMap<String, MapLeafNode>();
		try{
			updateTopologyMapInfo();
			if (!AhRestoreGetXML.checkXMLFileExist("network_device_history") &&
					NetworkDeviceConfigTracking.checkDBTableExist("network_device_history")) {
				AhRestoreDBTools.logRestoreMsg("migrate hive_ap data into network_device_history table start...");
				boolean finishFlag = MigrateApTopFromHiveAp.migrateHiveApTopology();
				if(!finishFlag){
					AhRestoreDBTools.logRestoreMsg("migrate hive_ap data failure...");
				}
				AhRestoreDBTools.logRestoreMsg("migrate hive_ap data into network_device_history table end...");
			}
			return true;
		}catch(Exception e){
			AhRestoreDBTools.logRestoreMsg("Update Topology Map error.", e);
			return false;
		}finally{
			leafNodes = null;
		}
	}

	private static void setSwitchDeviceInterfacePPPoe(HiveAp hiveap){
		ConfigTemplate template = QueryUtil.findBoById(
				ConfigTemplate.class, hiveap.getConfigTemplate().getId(),new ImplQueryBo());
		PortGroupProfile portGroup = hiveap.getPortGroup(template);
		if(portGroup != null){
			List<Short> ethList=portGroup.getPortFinalValuesByPortType(DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
			if(ethList!=null && ethList.size()>0){
				for(Short eth:ethList){
					//switch don't have eth0 port
					if(eth == AhInterface.DEVICE_IF_TYPE_ETH0){
						continue;
					}
				DeviceInterface  deviceInterface=hiveap.getDeviceInterfaces().get((long)eth);
				deviceInterface.setConnectionType("3");
				return;
				}
			}
			List<Short> sfpList=portGroup.getPortFinalValuesByPortType(DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);
			if(sfpList!=null && sfpList.size()>0){
					for(Short sfp:sfpList){
						DeviceInterface  sfpdeviceInterface=hiveap.getDeviceInterfaces().get((long)sfp);
						sfpdeviceInterface.setConnectionType("3");
						return;
					}
				}
			List<Short> usbList=portGroup.getPortFinalValuesByPortType(DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
			if(usbList!=null && usbList.size()>0){
				for(Short usb:usbList)
				{
					DeviceInterface  usbdeviceInterface=hiveap.getDeviceInterfaces().get((long)usb);
					usbdeviceInterface.setConnectionType("3");
					return;
				}
			}
		}
	}

	private static boolean isValid(HiveAp hiveAp) {
		// Query from DB, check for whether it is already
		// existed in other domain.
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("macAddress", hiveAp.getMacAddress()));
		if (!list.isEmpty()) {
			HiveAp existedHiveAp = list.get(0);
			if (!HmDomain.HOME_DOMAIN.equals(existedHiveAp.getOwner()
					.getDomainName())) {
				// ignore this record.
				AhRestoreDBTools
						.logRestoreMsg("A "
								+ apName
								+ " with the same MAC address:"
								+ hiveAp.getMacAddress()
								+ " already in other domains, cannot restore this record into DB.");
				return false;
			} else {// Existed one is Home domain HiveAP
				if (existedHiveAp.getManageStatus() == HiveAp.STATUS_MANAGED) {
					// ignore this record
					AhRestoreDBTools
							.logRestoreMsg("A "
									+ apName
									+ " with the same MAC address:"
									+ hiveAp.getMacAddress()
									+ " already in home domain's ManagedList, cannot restore this record into DB.");
					return false;
				} else {
					// remove the existed record
					AhRestoreDBTools
							.logRestoreMsg("A "
									+ apName
									+ " with the same MAC address:"
									+ hiveAp.getMacAddress()
									+ " already in home domain's newList, remove it from DB, in order to restore this record.");
					try {
						Collection<Long> ids = new ArrayList<Long>(1);
						ids.add(existedHiveAp.getId());
						BoMgmt.getMapMgmt().removeHiveAps(ids);
						// set the HiveAP connection status to what the
						// existedHiveAP is;
						// since this AP need to sent disconnect event while
						// restore finished.
						hiveAp.setConnected(existedHiveAp.isConnected());
						// remove alarm;
						int alarmCount = QueryUtil.bulkRemoveBosByDomain(
								AhAlarm.class, new FilterParams("apId",
										existedHiveAp.getMacAddress()),
								existedHiveAp.getOwner().getId(), null);
						AhRestoreDBTools.logRestoreMsg(apName + " ["
								+ existedHiveAp.getMacAddress()
								+ "] related Alarm count:" + alarmCount);
						// remove event;
						int eventCount = QueryUtil.bulkRemoveBosByDomain(
								AhEvent.class, new FilterParams("apId",
										existedHiveAp.getMacAddress()),
								existedHiveAp.getOwner().getId(), null);
						AhRestoreDBTools.logRestoreMsg(apName + " ["
								+ existedHiveAp.getMacAddress()
								+ "] related Event count:" + eventCount);
					} catch (Exception e) {
						AhRestoreDBTools
								.logRestoreMsg(
										"Remove the "
												+ apName
												+ ":"
												+ hiveAp.getMacAddress()
												+ " in home domain error. cannot restore the record into DB",
										e);
						return false;
					}
				}
			}
		}
		return true;
	}

	private static Map<String, List<HiveApL3cfgNeighbor>> getAllL3Neighbors()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApL3cfgNeighbor>> l3NeighborInfo = new HashMap<String, List<HiveApL3cfgNeighbor>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_l3cfg_neighbor.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_l3cfg_neighbor");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_l3cfg_neighbor.xml file.");
			return l3NeighborInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_l3cfg_neighbor", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_l3cfg_neighbor
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set neighbor_ap_id
			 */
			String colName_id = "neighbor_ap_id";
			boolean isColPresent_id = AhRestoreCommons.isColumnPresent(
					xmlParser, "hive_ap_l3cfg_neighbor", colName_id);

			/**
			 * Set neighbormac
			 */
			String colName_mac = "neighbormac";
			boolean isColPresent_mac = AhRestoreCommons.isColumnPresent(
					xmlParser, "hive_ap_l3cfg_neighbor", colName_mac);

			if (!isColPresent_id && !isColPresent_mac) {
				/**
				 * The neighbor_ap_id or neighbormac column must be exist in the
				 * table of hive_ap_l3cfg_neighbor
				 */
				continue;
			}
			// either neighbormac field(new version) or query by
			// neighbor_ap_id(old version);
			String neighbormac = null;
			if (isColPresent_mac) {
				neighbormac = xmlParser.getColVal(i, colName_mac);
				if (neighbormac == null || neighbormac.trim().equals("")
						|| neighbormac.trim().equalsIgnoreCase("null")) {
					continue;
				}
			} else {
				String neighbor_ap_id = xmlParser.getColVal(i, colName_id);
				if (neighbor_ap_id == null || neighbor_ap_id.trim().equals("")
						|| neighbor_ap_id.trim().equalsIgnoreCase("null")) {
					continue;
				}
				Long neighbor_ap_id_new = AhRestoreNewMapTools
						.getMapHiveAP(AhRestoreCommons
								.convertLong(neighbor_ap_id));
				if (null != neighbor_ap_id_new) {
					HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class,
							neighbor_ap_id_new);
					if (null != hiveAp) {
						neighbormac = hiveAp.getMacAddress();
					}
				} else {
					AhRestoreDBTools.logRestoreMsg("Cound not find the new "
							+ apName + " id mapping to old id:"
							+ neighbor_ap_id);
				}
			}

			/**
			 * Set neighbortype
			 */
			colName = "neighbortype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_l3cfg_neighbor", colName);
			String neighbortype = isColPresent ? xmlParser
					.getColVal(i, colName) : String
					.valueOf(HiveApL3cfgNeighbor.NEIGHBOR_TYPE_INCLUDED);

			if (null != neighbormac && !"".equals(neighbormac)) {
				if (l3NeighborInfo.get(hive_ap_id) == null) {
					List<HiveApL3cfgNeighbor> neighborList = new ArrayList<HiveApL3cfgNeighbor>();
					l3NeighborInfo.put(hive_ap_id, neighborList);
				}
				HiveApL3cfgNeighbor l3Neighbor = new HiveApL3cfgNeighbor();
				l3Neighbor.setNeighborMac(neighbormac);
				l3Neighbor.setNeighborType((short) AhRestoreCommons
						.convertInt(neighbortype));
				l3NeighborInfo.get(hive_ap_id).add(l3Neighbor);
			}
		}
		return l3NeighborInfo;
	}

	private static void updateHiveApNeighborInfo() {

		try {
			Map<String, List<HiveApL3cfgNeighbor>> allL3Neighbors = getAllL3Neighbors();
			if (null == allL3Neighbors) {
				return;
			}
			List<HiveAp> hiveAp_list = new ArrayList<HiveAp>();
			for (String hive_ap_id : allL3Neighbors.keySet()) {
				Long hive_ap_id_new = AhRestoreNewMapTools
						.getMapHiveAP(AhRestoreCommons.convertLong(hive_ap_id));
				if (null != hive_ap_id_new) {
					HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class,
							hive_ap_id_new);
					if (null != hiveAp) {
						hiveAp.setL3Neighbors(allL3Neighbors.get(hive_ap_id));
						hiveAp_list.add(hiveAp);
					}
				}
			}
			if (hiveAp_list.size() > 0) {
				QueryUtil.bulkUpdateBos(hiveAp_list);
				AhRestoreDBTools.logRestoreMsg("Restore " + apName + " update "
						+ apName + " with neigbors, count:"
						+ hiveAp_list.size());
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restore " + apName
					+ " neighbors error.", e);
		}
	}

	private static void updateSecondVPNGatewayInfo() {
		try {
			for (String mac : second_vpn_gateway_map.keySet()) {
				Long hive_ap_id_new = AhRestoreNewMapTools
						.getMapHiveAP(AhRestoreCommons
								.convertLong(second_vpn_gateway_map.get(mac)));
				HiveAp secondVpnGateway = QueryUtil.findBoById(HiveAp.class,
						hive_ap_id_new);
				HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
						"macAddress", mac);
				if (secondVpnGateway == null || hiveAp == null) {
					continue;
				}
				hiveAp.setSecondVPNGateway(secondVpnGateway);
				QueryUtil.updateBo(hiveAp);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restore " + apName
					+ " second VPN gateway error.", e);
		}
	}

	/*
	 * Field:ethId,fetchLinksTimeout is not restored. we just keep them default
	 * value when restore the database. because them will be updated in the real
	 * environment.
	 */
	private static Map<String, MapContainerNode> getAllHiveApTopology()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, MapContainerNode> hiveApTopoInfo = new HashMap<String, MapContainerNode>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_node.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("map_node");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read map_node.xml file.");
			return hiveApTopoInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			/**
			 * judge the row entry is mapLeafNode record.
			 */
			colName = "node_type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			if (!isColPresent) {
				/**
				 * The node_type column must be exist in the table of map_node
				 */
				continue;
			}
			String nodetype = xmlParser.getColVal(i, colName);
			if (nodetype == null || nodetype.trim().equals("")
					|| nodetype.trim().equalsIgnoreCase("null")
					|| !(nodetype.trim().equals("LN"))) {
				continue;
			}

			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of map_node
				 */
				continue;
			}

			String hive_ap_id = xmlParser.getColVal(i, colName);
			if (hive_ap_id == null || hive_ap_id.trim().equals("")
					|| hive_ap_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set parent_map_id
			 */
			colName = "parent_map_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			if (!isColPresent) {
				/**
				 * The parent_map_id column must be exist in the table of
				 * map_node
				 */
				continue;
			}
			String parent_map_id = xmlParser.getColVal(i, colName);
			if (parent_map_id == null || parent_map_id.trim().equals("")
					|| parent_map_id.trim().equalsIgnoreCase("null")) {
				continue;
			}
			Long parent_map_id_new = AhRestoreNewMapTools
					.getMapMapContainer(AhRestoreCommons
							.convertLong(parent_map_id));
			if (parent_map_id_new == null) {
				continue;
			}

			MapLeafNode leafNode = new MapLeafNode();

			/**
			 * Set node X value
			 */
			colName = "x";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			String x = isColPresent ? xmlParser.getColVal(i, colName) : "";
			leafNode.setX(AhRestoreCommons.convertDouble(x));

			/**
			 * Set node Y value
			 */
			colName = "y";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			String y = isColPresent ? xmlParser.getColVal(i, colName) : "";
			leafNode.setY(AhRestoreCommons.convertDouble(y));

			/**
			 * Set icon
			 */
			colName = "iconName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_node", colName);
			String iconName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			leafNode.setIconName(AhRestoreCommons.convertString(iconName));

			MapContainerNode parentNode = QueryUtil.findBoById(
					MapContainerNode.class, parent_map_id_new);
			if (parentNode == null) {
				continue;
			}
			hiveApTopoInfo.put(hive_ap_id, parentNode);
			// if leaf node width or height is not configured
			if (leafNode.getX() == 0 || leafNode.getY() == 0) {
				BoMgmt.getMapMgmt().placeIcon(parentNode, leafNode);
			}
			leafNodes.put(hive_ap_id, leafNode);
		}
		return hiveApTopoInfo;
	}

	private static Map<String, Map<Long, DeviceInterface>> getAllDeviceInterfaces()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, Map<Long, DeviceInterface>> allDeviceInterfaces = new HashMap<String, Map<Long, DeviceInterface>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_node.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hiveap_device_interface");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hiveap_device_interface.xml file.");
			return allDeviceInterfaces;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			DeviceInterface dInterface = new DeviceInterface();

			/**
			 * Set HIVEAP_ID
			 */
			colName = "hiveap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hiveap_device_interface
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set deviceIfType
			 */
			colName = "deviceIfType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String deviceIfType = isColPresent ? xmlParser
					.getColVal(i, colName) : "-1";
			short deviceIfTypeShort = (short) AhRestoreCommons.convertInt(deviceIfType);
			if (restore_from_60r1_before && deviceIfTypeShort == 9){
				deviceIfTypeShort = AhInterface.DEVICE_IF_TYPE_USB;
			}else if(restore_from_geneva_before && deviceIfTypeShort >= 1001 && deviceIfTypeShort <= 1004){
				deviceIfTypeShort = (short)(deviceIfTypeShort - 1000 + AhInterface.DEVICE_IF_TYPE_ETH24);
			}
			dInterface.setDeviceIfType(deviceIfTypeShort);

			/**
			 * Set interfaceName
			 */
			colName = "interfaceName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String interfaceName = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			dInterface.setInterfaceName(AhRestoreCommons
					.convertString(interfaceName));
			// fix bug 17774
			if (dInterface.getInterfaceName().equals(
					"hiveAp.autoProvisioning.br100.if.port.eth0")) {
				dInterface.setInterfaceName("Eth0");
			}

			if (dInterface.getInterfaceName().equals(
					"hiveAp.autoProvisioning.br100.if.port.usb")) {
				dInterface.setInterfaceName("USB");
			}

			/**
			 * Set connectionType
			 */
			colName = "connectionType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			boolean isConnectionTypeExisted = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String connectionType = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			dInterface.setConnectionType(AhRestoreCommons
					.convertString(connectionType));

//			/**
//			 * Set staticIp
//			 */
//			colName = "staticIp";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"hiveap_device_interface", colName);
//			String staticIp = isColPresent ? xmlParser.getColVal(i,
//					colName) : "";
//			dInterface.setStaticIp(AhRestoreCommons
//					.convertString(staticIp));
//
//			/**
//			 * Set defaultGateway
//			 */
//			colName = "defaultGateway";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"hiveap_device_interface", colName);
//			String defaultGateway = isColPresent ? xmlParser.getColVal(i,
//					colName) : "";
//			dInterface.setDefaultGateway(AhRestoreCommons
//					.convertString(defaultGateway));

			/**
			 * Set adminState
			 */
			colName = "adminState";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String adminState = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			dInterface.setAdminState((short) AhRestoreCommons
					.convertInt(adminState));

			/**
			 * Set ipAddress
			 */
			colName = "ipAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String ipAddress = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			dInterface.setIpAddress(AhRestoreCommons.convertString(ipAddress));

			/**
			 * Set netMask
			 */
			colName = "netMask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String netMask = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			dInterface.setNetMask(AhRestoreCommons.convertString(netMask));

			/**
			 * Set gateway
			 */
			colName = "gateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String gateway = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			dInterface.setGateway(AhRestoreCommons.convertString(gateway));

			/**
			 * Set enableDhcp
			 */
			colName = "enableDhcp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableDhcp = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			if(!isConnectionTypeExisted && isColPresent && enableDhcp.equals("t")){
				dInterface.setConnectionType("1");
			}else if(!isConnectionTypeExisted && isColPresent && enableDhcp.equals("f")){
				dInterface.setConnectionType("2");
			}
			dInterface.setEnableDhcp(AhRestoreCommons
					.convertStringToBoolean(enableDhcp));

			/**
			 * Set duplex
			 */
			colName = "duplex";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String duplex = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			dInterface.setDuplex((short) AhRestoreCommons.convertInt(duplex));

			/**
			 * Set speed
			 */
			colName = "speed";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String speed = isColPresent ? xmlParser.getColVal(i, colName)
					: "-1";
			dInterface.setSpeed((short) AhRestoreCommons.convertInt(speed));

			/**
			 * set enableNat
			 */
			colName = "enableNat";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableNat = isColPresent? xmlParser.getColVal(i, colName) : "true";
			dInterface.setEnableNat(AhRestoreCommons.convertStringToBoolean(enableNat));
			/**
			 * set disablePortForwarding
			 */
			colName = "disablePortForwarding";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String disablePortForwarding = isColPresent? xmlParser.getColVal(i, colName) : "true";
			dInterface.setDisablePortForwarding(AhRestoreCommons.convertStringToBoolean(disablePortForwarding));

			/**
			 * set priority
			 */
			colName = "priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String priority;
			if (dInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB /*&& dInterface.getRole() == AhInterface.ROLE_PRIMARY*/) {
				priority = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			} else if (dInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB /*&& dInterface.getRole() == AhInterface.ROLE_BACKUP*/) {
				priority = isColPresent ? xmlParser.getColVal(i, colName) : "2000";
			} else {
				priority = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			}
			dInterface.setPriority(AhRestoreCommons.convertInt(priority));

			/**
			 * Set psestate
			 */
			colName = "wanOrder";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			int wanOrder = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0;
			dInterface.setWanOrder(wanOrder);

			/**
			 * Set psestate
			 */
			colName = "pseState";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			int pseState = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : AhInterface.ETH_PSE_8023af;
			dInterface.setPseState((short) pseState);

			/**
			 * Set pseEnabled
			 */
			colName = "pseEnabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			boolean pseEnabled = true;
			if (!isColPresent) {
				pseEnabled = (dInterface.getPseState() != AhInterface.ETH_PSE_SHUTDOWN);
			} else {
				pseEnabled = AhRestoreCommons.convertStringToBoolean(xmlParser
						.getColVal(i, colName));
			}
			dInterface.setPseEnabled(pseEnabled);
			if (!pseEnabled) {
				dInterface.setPseState(AhInterface.ETH_PSE_8023af);
			}

			/**
			 * Set psePriority
			 */
			colName = "psePriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String psePriority = isColPresent ? AhRestoreCommons
					.convertString(xmlParser.getColVal(i, colName))
					: AhInterface.ETH_PSE_PRIORITY_ETH2;
			if (!isColPresent
					&& dInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH1) {
				dInterface.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
			} else {
				dInterface.setPsePriority(psePriority);
			}

			// /**
			// * Set powerThreshold
			// */
			// colName = "powerThreshold";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hiveap_device_interface", colName);
			// int powerThreshold = isColPresent ?
			// AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
			// AhInterface.PSE_POWER_THRESHOLD_CLASSBASE;
			// dInterface.setPowerThreshold((short)powerThreshold);
			//
			// /**
			// * Set powerNumber
			// */
			// colName = "powerNumber";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
			// "hiveap_device_interface", colName);
			// int powerNumber = isColPresent ?
			// AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
			// 20;
			// dInterface.setPowerNumber((short)powerNumber);
			//
			/**
			 * Set enableMaxDownload
			 */
			colName = "enableMaxDownload";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableMaxDownload = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			dInterface.setEnableMaxDownload(AhRestoreCommons
					.convertStringToBoolean(enableMaxDownload));

			/**
			 * Set enableMaxUpload
			 */
			colName = "enableMaxUpload";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableMaxUpload = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			dInterface.setEnableMaxUpload(AhRestoreCommons
					.convertStringToBoolean(enableMaxUpload));

			/**
			 * Set maxDownload
			 */
			colName = "maxdownload";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String maxdownload = isColPresent ? xmlParser.getColVal(i, colName)
					: "100";
			dInterface.setMaxDownload((short) AhRestoreCommons
					.convertInt(maxdownload));

			/**
			 * Set role
			 */
			colName = "role";

			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String role = isColPresent ? xmlParser.getColVal(i, colName) : "0";
//			if (dInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH0) {
//				role = isColPresent ? xmlParser.getColVal(i, colName) : "3";
//			} else if (dInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB) {
//				role = isColPresent ? xmlParser.getColVal(i, colName) : "1";
//			} else {
//				role = isColPresent ? xmlParser.getColVal(i, colName) : "0";
//			}
			dInterface.setRole((short) AhRestoreCommons.convertInt(role));

			if(restore_from_60r2_before){
				dInterface.setWanOrder(AhRestoreCommons.convertInt(role));
			}

			/**
			 * Set maxUpload
			 */
			colName = "maxupload";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String maxupload = isColPresent ? xmlParser.getColVal(i, colName)
					: "100";
			dInterface.setMaxUpload((short) AhRestoreCommons
					.convertInt(maxupload));

			/**
			 * Set ifActive
			 */
			colName = "ifActive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String ifActive = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			dInterface.setIfActive(AhRestoreCommons
					.convertStringToBoolean(ifActive));

//			/**
//			 * Set nativeVlan
//			 */
//			colName = "nativeVlan";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"hiveap_device_interface", colName);
//			String nativeVlan = isColPresent ? xmlParser.getColVal(i, colName)
//					: "1";
//			dInterface.setNativeVlan(AhRestoreCommons.convertInt(nativeVlan));
//
//			/**
//			 * Set allowedVlan
//			 */
//			colName = "allowedVlan";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					"hiveap_device_interface", colName);
//			String allowedVlan = isColPresent ? xmlParser.getColVal(i, colName)
//					: "All";
//			dInterface.setAllowedVlan(AhRestoreCommons
//					.convertString(allowedVlan));

			/**
			 * Set flowControlStatus
			 */
			colName = "flowControlStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String flowControlStatus = isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(AhInterface.FLOW_CONTROL_STATUS_DISABLE);
			dInterface.setFlowControlStatus((short) AhRestoreCommons
					.convertInt(flowControlStatus));

			/**
			 * Set lldpTransmit
			 */
			colName = "lldpTransmit";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String lldpTransmit = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			dInterface.setLldpTransmit(AhRestoreCommons
					.convertStringToBoolean(lldpTransmit));

			/**
			 * Set lldpReceive
			 */
			colName = "lldpReceive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String lldpReceive = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			dInterface.setLldpReceive(AhRestoreCommons
					.convertStringToBoolean(lldpReceive));

			/**
			 * Set cdpReceive
			 */
			colName = "cdpReceive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String cdpReceive = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			dInterface.setCdpReceive(AhRestoreCommons
					.convertStringToBoolean(cdpReceive));

			/**
			 * Set lldpEnable
			 */
			colName = "lldpEnable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String lldpEnable = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			dInterface.setLldpEnable(AhRestoreCommons
					.convertStringToBoolean(lldpEnable));

			/**
			 * Set cdpEnable
			 */
			colName = "cdpEnable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String cdpEnable = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			dInterface.setCdpEnable(AhRestoreCommons
					.convertStringToBoolean(cdpEnable));
			/**
			 * Set clientReporting
			 */
			colName = "clientReporting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String clientReporting = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			dInterface.setClientReporting(AhRestoreCommons
					.convertStringToBoolean(clientReporting));

			/**
			 * Set enableClientReporting
			 */
			colName = "enableClientReporting";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableClientReporting = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			dInterface.setEnableClientReporting(AhRestoreCommons
					.convertStringToBoolean(enableClientReporting));
			
			
			/**
			 * Set enableOverridePortDescription
			 */
			colName = "enableOverridePortDescription";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String enableOverridePortDescription = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			dInterface.setEnableOverridePortDescription(AhRestoreCommons
					.convertStringToBoolean(enableOverridePortDescription));
			
			/**
			 * Set portDescription
			 */
			colName = "portDescription";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String portDescription = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			dInterface.setPortDescription(AhRestoreCommons.convertString(portDescription));
			

			/**
			 * Set autoMdix
			 */
			colName = "autoMdix";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String autoMdix = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			dInterface.setAutoMdix(AhRestoreCommons
					.convertStringToBoolean(autoMdix));

			/**
			 * Set mtu
			 */
			colName = "mtu";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String mtu = isColPresent ? xmlParser.getColVal(i, colName)
					: "1500";
			dInterface.setMtu(AhRestoreCommons.convertInt(mtu));

			/**
			 * Set debounceTimer
			 */
			colName = "debounceTimer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hiveap_device_interface", colName);
			String debounceTimer = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			dInterface.setDebounceTimer(AhRestoreCommons
					.convertInt(debounceTimer));

			if (allDeviceInterfaces.get(hiveApId) == null) {
				allDeviceInterfaces.put(hiveApId,
						new HashMap<Long, DeviceInterface>());
			}
			allDeviceInterfaces.get(hiveApId).put(
					Long.valueOf(dInterface.getDeviceIfType()), dInterface);
		}

		return allDeviceInterfaces;
	}

	private static Map<String, List<USBModemProfile>> getAllUSBModemProfiles()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<USBModemProfile>> allUSBModemProfiles = new HashMap<String, List<USBModemProfile>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_node.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_usb_modem");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_usb_modem.xml file.");
			return allUSBModemProfiles;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			USBModemProfile usbModem = new USBModemProfile();

			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_usb_modem
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set modemName
			 */
			colName = "modemName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String modemName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			usbModem.setModemName(AhRestoreCommons.convertString(modemName));
			
			/**
			 * Set displayName
			 */
			colName = "displayName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String displayName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			//retore before 6.1.3.0 display name is null.
			if(StringUtils.isEmpty(displayName) || "null".equalsIgnoreCase(displayName)){
				if("sierra_308".equals(usbModem.getModemName())){
					displayName = "ATT Shockwave Sierra Wireless 308U, 310U, 312U, 319U, 320U, 326U";
				}else if("sierra_313".equals(usbModem.getModemName())){
					displayName = "ATT Momentum Sierra Wireless 313U";
				}else if("pantech_uml".equals(usbModem.getModemName())){
					displayName = "Verizon Pantech UML";
				}else if("novatel_551L".equals(usbModem.getModemName())){
					displayName = "Verizon Novatel USB 551L";
				}else if("novatel_E362".equals(usbModem.getModemName())){
					displayName = "Verizon Embedded LTE";
				}else if("huawei_e220".equals(usbModem.getModemName())){
					displayName = "Huawei E220";
				}else if("huawei_e1752".equals(usbModem.getModemName())){
					displayName = "Huawei E1752";
				}else if("huawei_e366".equals(usbModem.getModemName())){
					displayName = "Huawei UMG366";
				}else if("pantech_uml295".equals(usbModem.getModemName())){
					displayName = "Verizon Pantech UML295";
				}else if("netgear_ac340u".equals(usbModem.getModemName())){
					displayName = "ATT Netgear AC340U";
				}else if("netgear_ac341u".equals(usbModem.getModemName())){
					displayName = "Sprint Netgear AC341U";
				}else if("zte_mf683".equals(usbModem.getModemName())){
					displayName = "T-Mobile Jet 3.0";
				}
			}
			usbModem.setDisplayName(AhRestoreCommons.convertString(displayName));

			/**
			 * Set apn
			 */
			colName = "apn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String apn = isColPresent ? xmlParser.getColVal(i, colName) : "";
			usbModem.setApn(AhRestoreCommons.convertString(apn));

			/**
			 * Set dialupNum
			 */
			colName = "dialupNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String dialupNum = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			usbModem.setDialupNum(dialupNum);

			/**
			 * Set userId
			 */
			colName = "userId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String userId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			usbModem.setUserId(AhRestoreCommons.convertString(userId));

			/**
			 * Set password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String password = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			usbModem.setPassword(AhRestoreCommons.convertString(password));

			/**
			 * Set osVersion
			 */
			if (restore_from_50r4_before) {
				// Version add in 5.0.4.0, before version no this column.
				if ("sierra_308".equals(usbModem.getModemName())
						|| "pantech_uml".equals(usbModem.getModemName())) {
					usbModem.setOsVersion("5.0.0.0");
				} else if ("sierra_313".equals(usbModem.getModemName())) {
					usbModem.setOsVersion("5.0.2.0");
				}
			} else {
				colName = "osVersion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_usb_modem", colName);
				String osVersion = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				usbModem.setOsVersion(AhRestoreCommons.convertString(osVersion));
			}

			/**
			 * Set cellularMode
			 */
			colName = "cellularMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_usb_modem", colName);
			String cellularMode = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			usbModem.setCellularMode((short)AhRestoreCommons.convertInt(cellularMode));

			if (allUSBModemProfiles.get(hiveApId) == null) {
				List<USBModemProfile> usbModemList = new ArrayList<USBModemProfile>();
				usbModemList.add(usbModem);
				allUSBModemProfiles.put(hiveApId, usbModemList);
			} else {
				allUSBModemProfiles.get(hiveApId).add(usbModem);
			}
		}

		return allUSBModemProfiles;
	}

	private static Map<String, List<HiveApInternalNetwork>> getAllIntNetwork()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<HiveApInternalNetwork>> allIntNetworks = new HashMap<String, List<HiveApInternalNetwork>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_internal_network.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_internal_network");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_internal_network.xml file.");
			return allIntNetworks;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			HiveApInternalNetwork intNetwork = new HiveApInternalNetwork();

			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_internal_network", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * hive_ap_internal_network
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set internalNetwork
			 */
			colName = "internalNetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_internal_network", colName);
			String internalNetwork = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			intNetwork.setInternalNetwork(AhRestoreCommons
					.convertString(internalNetwork));

			/**
			 * Set netmask
			 */
			colName = "netmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"hive_ap_internal_network", colName);
			String netmask = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			intNetwork.setNetmask(AhRestoreCommons.convertString(netmask));

			if (allIntNetworks.get(hiveApId) == null) {
				List<HiveApInternalNetwork> addList = new ArrayList<HiveApInternalNetwork>();
				addList.add(intNetwork);
				allIntNetworks.put(hiveApId, addList);
			} else {
				allIntNetworks.get(hiveApId).add(intNetwork);
			}
		}

		return allIntNetworks;
	}

	private static Map<String, List<ConfigTemplateStormControl>> getAllStormControl()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<ConfigTemplateStormControl>> allStormControl = new HashMap<String, List<ConfigTemplateStormControl>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of HIVE_AP_STORM_CONTROL.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("HIVE_AP_STORM_CONTROL");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read HIVE_AP_STORM_CONTROL.xml file.");
			return allStormControl;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			ConfigTemplateStormControl stormControl = new ConfigTemplateStormControl();

			/**
			 * Set hive_ap_id
			 */
			colName = "hive_ap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_id column must be exist in the table of
				 * HIVE_AP_STORM_CONTROL
				 */
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set interfaceNum
			 */
			colName = "interfaceNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String interfaceNum = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			stormControl.setInterfaceNum((short) AhRestoreCommons
					.convertInt(interfaceNum));

			/**
			 * Set interfaceType
			 */
			colName = "interfaceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String interfaceType = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			stormControl.setInterfaceType(AhRestoreCommons
					.convertString(interfaceType));

			/**
			 * Set allTrafficType
			 */
			colName = "allTrafficType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String allTrafficType = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			stormControl.setAllTrafficType(AhRestoreCommons
					.convertStringToBoolean(allTrafficType));

			/**
			 * Set broadcast
			 */
			colName = "broadcast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String broadcast = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			stormControl.setBroadcast(AhRestoreCommons
					.convertStringToBoolean(broadcast));

			/**
			 * Set unknownUnicast
			 */
			colName = "unknownUnicast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String unknownUnicast = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			stormControl.setUnknownUnicast(AhRestoreCommons
					.convertStringToBoolean(unknownUnicast));

			/**
			 * Set multicast
			 */
			colName = "multicast";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String multicast = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			stormControl.setMulticast(AhRestoreCommons
					.convertStringToBoolean(multicast));

			/**
			 * Set tcpsyn
			 */
			colName = "tcpsyn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String tcpsyn = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			stormControl.setTcpsyn(AhRestoreCommons
					.convertStringToBoolean(tcpsyn));

			/**
			 * Set rateLimitType
			 */
			colName = "rateLimitType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String rateLimitType = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			stormControl.setRateLimitType(AhRestoreCommons
					.convertLong(rateLimitType));

			/**
			 * Set rateLimitValue
			 */
			colName = "rateLimitValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"HIVE_AP_STORM_CONTROL", colName);
			String rateLimitValue = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			if(restore_from_fuji_before
				&& AhRestoreCommons.convertLong(rateLimitType) == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
				//bps --> kbps
				long value = AhRestoreCommons.convertLong(rateLimitValue);
				stormControl.setRateLimitValue(value%1000 == 0 ? value/1000 : value/1000+1);
			} else {
				stormControl.setRateLimitValue(AhRestoreCommons
						.convertLong(rateLimitValue));
			}

			if (allStormControl.get(hiveApId) == null) {
				List<ConfigTemplateStormControl> addList = new ArrayList<ConfigTemplateStormControl>();
				addList.add(stormControl);
				allStormControl.put(hiveApId, addList);
			} else {
				allStormControl.get(hiveApId).add(stormControl);
			}
		}

		return allStormControl;
	}

	private static void updateTopologyMapInfo() {

		try {
			Map<String, MapContainerNode> ap_topoInfo = getAllHiveApTopology();
			if (null == ap_topoInfo) {
				return;
			}
			for (String hive_ap_id : ap_topoInfo.keySet()) {
				MapContainerNode parentContainer = ap_topoInfo.get(hive_ap_id);
				Long hive_ap_id_new = AhRestoreNewMapTools
						.getMapHiveAP(AhRestoreCommons.convertLong(hive_ap_id));

				if (null != hive_ap_id_new) {
					HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class,
							hive_ap_id_new);

					if (null != hiveAp) {
						hiveAp.setMapContainer(parentContainer);
						MapLeafNode leafNode = leafNodes.get(hive_ap_id);
						if (null == leafNode) {
							leafNode = new MapLeafNode();
							leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
							BoMgmt.getMapMgmt().placeIcon(parentContainer,
									leafNode);
						}
						try {
							leafNode.setSeverity(hiveAp.getSeverity());
							BoMgmt.getMapMgmt().createMapLeafNode(hiveAp,
									leafNode, parentContainer);
						} catch (Exception e) {
							AhRestoreDBTools.logRestoreMsg("update " + apName
									+ " topology map error.", e);
						}
					}
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("update " + apName
					+ " topology map error.", e);
		}
	}

	private static void restoreMacLearningInfos(HiveAp hiveAp) {
		Long id = hiveAp.getId();
		String template_id = ap_template_map.get(String.valueOf(id));
		if (null == template_id) {
			return;
		}
		Map<String, EthernetAccess> map = AhRestoreNewMapTools
				.getMapEthernetAccessResotre(Long.parseLong(template_id));
		if (null != map) {
			try {
				AhRestoreDBTools
						.logRestoreMsg("EthernetAccess binded in network policy, try to restore to "
								+ apName + " object:" + hiveAp.getHostName());
				if (null == hiveAp.getLearningMacs()) {
					hiveAp.setLearningMacs(new ArrayList<HiveApLearningMac>());
				}
				// eth0
				EthernetAccess eth0Access = map
						.get(RestoreConfigTemplate.ETH0_ACCESS_PROFILE);
				EthernetAccess eth0Bridge = map
						.get(RestoreConfigTemplate.ETH0_BRIDGE_PROFILE);
				if (null != eth0Access
						&& hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
					hiveAp.getEth0()
							.setIdelTimeout(eth0Access.getIdleTimeout());
					hiveAp.getEth0().setMacLearningEnabled(
							eth0Access.isMacLearning());
					hiveAp.setEth0UserProfile(eth0Access.getUserProfile());
					Set<MacOrOui> macs = eth0Access.getMacAddress();
					if (null != macs) {
						for (MacOrOui mac : macs) {
							HiveApLearningMac learning = new HiveApLearningMac();
							learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_ETH0);
							learning.setMac(mac);
							hiveAp.getLearningMacs().add(learning);
						}
					}
				} else if (null != eth0Bridge
						&& hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
					hiveAp.getEth0()
							.setIdelTimeout(eth0Bridge.getIdleTimeout());
					hiveAp.getEth0().setMacLearningEnabled(
							eth0Bridge.isMacLearning());
					hiveAp.setEth0UserProfile(eth0Bridge.getUserProfile());
					Set<MacOrOui> macs = eth0Bridge.getMacAddress();
					if (null != macs) {
						for (MacOrOui mac : macs) {
							HiveApLearningMac learning = new HiveApLearningMac();
							learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_ETH0);
							learning.setMac(mac);
							hiveAp.getLearningMacs().add(learning);
						}
					}
				}

				// eth1 is available
				if (hiveAp.isEth1Available()) {
					if (hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH) {
						// eth1
						EthernetAccess eth1Access = map
								.get(RestoreConfigTemplate.ETH1_ACCESS_PROFILE);
						EthernetAccess eth1Bridge = map
								.get(RestoreConfigTemplate.ETH1_BRIDGE_PROFILE);
						if (null != eth1Access
								&& hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
							hiveAp.getEth1().setIdelTimeout(
									eth1Access.getIdleTimeout());
							hiveAp.getEth1().setMacLearningEnabled(
									eth1Access.isMacLearning());
							hiveAp.setEth1UserProfile(eth1Access
									.getUserProfile());
							Set<MacOrOui> macs = eth1Access.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_ETH1);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						} else if (null != eth1Bridge
								&& hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
							hiveAp.getEth1().setIdelTimeout(
									eth1Bridge.getIdleTimeout());
							hiveAp.getEth1().setMacLearningEnabled(
									eth1Bridge.isMacLearning());
							hiveAp.setEth1UserProfile(eth1Bridge
									.getUserProfile());
							Set<MacOrOui> macs = eth1Bridge.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_ETH1);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						}
					} else if (hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_AGG0) {
						// agg0
						EthernetAccess agg0Access = map
								.get(RestoreConfigTemplate.AGG0_ACCESS_PROFILE);
						EthernetAccess agg0Bridge = map
								.get(RestoreConfigTemplate.AGG0_BRIDGE_PROFILE);
						if (null != agg0Access
								&& hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
							hiveAp.getAgg0().setIdelTimeout(
									agg0Access.getIdleTimeout());
							hiveAp.getAgg0().setMacLearningEnabled(
									agg0Access.isMacLearning());
							hiveAp.setAgg0UserProfile(agg0Access
									.getUserProfile());
							Set<MacOrOui> macs = agg0Access.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_AGG0);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						} else if (null != agg0Bridge
								&& hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
							hiveAp.getAgg0().setIdelTimeout(
									agg0Bridge.getIdleTimeout());
							hiveAp.getAgg0().setMacLearningEnabled(
									agg0Bridge.isMacLearning());
							hiveAp.setAgg0UserProfile(agg0Bridge
									.getUserProfile());
							Set<MacOrOui> macs = agg0Bridge.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_AGG0);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						}
					} else if (hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_RED0) {
						// red0
						EthernetAccess red0Access = map
								.get(RestoreConfigTemplate.RED0_ACCESS_PROFILE);
						EthernetAccess red0Bridge = map
								.get(RestoreConfigTemplate.RED0_ACCESS_PROFILE);
						if (null != red0Access
								&& hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
							hiveAp.getRed0().setIdelTimeout(
									red0Access.getIdleTimeout());
							hiveAp.getRed0().setMacLearningEnabled(
									red0Access.isMacLearning());
							hiveAp.setRed0UserProfile(red0Access
									.getUserProfile());
							Set<MacOrOui> macs = red0Access.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_RED0);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						} else if (null != red0Bridge
								&& hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
							hiveAp.getRed0().setIdelTimeout(
									red0Bridge.getIdleTimeout());
							hiveAp.getRed0().setMacLearningEnabled(
									red0Bridge.isMacLearning());
							hiveAp.setRed0UserProfile(red0Bridge
									.getUserProfile());
							Set<MacOrOui> macs = red0Bridge.getMacAddress();
							if (null != macs) {
								for (MacOrOui mac : macs) {
									HiveApLearningMac learning = new HiveApLearningMac();
									learning.setLearningMacType(HiveApLearningMac.LEARNING_MAC_RED0);
									learning.setMac(mac);
									hiveAp.getLearningMacs().add(learning);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				AhRestoreDBTools
						.logRestoreMsg("EthernetAccess binded in network policy, try to restore to "
								+ apName
								+ " object:"
								+ hiveAp.getHostName()
								+ " error.");
			}
		}
	}

	private static void restoreWifiClientModeSsids(HiveAp hiveAp) {
		Long id = hiveAp.getId();
		String template_id = ap_template_map.get(String.valueOf(id));
		if (null == template_id) {
			return;
		}
		Map<String, EthernetAccess> map = AhRestoreNewMapTools
				.getMapEthernetAccessResotre(Long.parseLong(template_id));
		if (null != map) {
			try {
				AhRestoreDBTools
						.logRestoreMsg("EthernetAccess binded in network policy, try to restore to "
								+ apName + " object:" + hiveAp.getHostName());
				if (null == hiveAp.getWifiClientPreferredSsids()) {
					hiveAp.setWifiClientPreferredSsids(new ArrayList<HiveApPreferredSsid>());
				}
			} catch (Exception e) {
				AhRestoreDBTools
						.logRestoreMsg("EthernetAccess binded in network policy, try to restore to "
								+ apName
								+ " object:"
								+ hiveAp.getHostName()
								+ " error.");
			}
		}
	}

	public static boolean isRestoreHmBeforeVersion(String version) {
		BeVersionInfo oInfo = NmsUtil
				.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH
						+ File.separatorChar + ".." + File.separatorChar
						+ "hivemanager.ver");
		String strMainVersion = oInfo.getMainVersion();
		String strSubVersion = oInfo.getSubVersion();
		try {
			String ver = strMainVersion + "." + strSubVersion + ".0";
			return NmsUtil.compareSoftwareVersion(version, ver) > 0;
		} catch (Exception ex) {
			return false;
		}
	}

	public static Map<String, DeviceStpSettings> getAllDeviceStpSettings() throws Exception{
		Map<String, DeviceStpSettings> settings = new HashMap<String, DeviceStpSettings>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "device_stp_settings";
		/**
		 * Check validation of DEVICE_STP_SETTINGS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read " + tableName + " file.");
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {

			DeviceStpSettings deviceStp = new DeviceStpSettings();

			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);

			if (!isColPresent) {
				continue;
			}
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";

			if(isIllegalString(id)){
				continue;
			}

			colName = "stp_mode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String stp_mode = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(StpSettings.STP_MODE_STP);
			deviceStp.setStp_mode(Short.valueOf(stp_mode));

			colName = "hellotime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String hellotime = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(DeviceStpSettings.DEFAULT_HELLO_TIME);
			if (NumberUtils.isNumber(hellotime)) {
				if (AhRestoreCommons.convertInt(hellotime) > DeviceStpSettings.DEFAULT_HELLO_TIME) {
					hellotime = Short
							.toString(DeviceStpSettings.DEFAULT_HELLO_TIME);
				}
			}
			deviceStp.setHelloTime(Short.valueOf((hellotime)));

			colName = "forwardTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String forwardtime = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(DeviceStpSettings.DEFAULT_FORWARD_DELAY);
			deviceStp.setForwardTime(Short.valueOf(forwardtime));

			colName = "maxage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String maxage = isColPresent ? xmlParser.getColVal(i, colName)
					: Short.toString(DeviceStpSettings.DEFAULT_MAX_AGE);
			deviceStp.setMaxAge(Short.valueOf((maxage)));

			colName = "forceversion";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String forceversion = isColPresent ? xmlParser
					.getColVal(i, colName) : Short
					.toString(DeviceStpSettings.DEFAULT_FORCE_VERSION);
			deviceStp.setForceVersion((short) AhRestoreCommons
					.convertInt(forceversion));

			colName = "priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String priority = isColPresent ? xmlParser.getColVal(i, colName)
					: Integer.toString(DeviceStpSettings.DEFAULT_PRIORITY);
			deviceStp.setPriority(AhRestoreCommons.convertInt(priority));

			colName = "enableStp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String enableStp = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			deviceStp.setEnableStp(AhRestoreCommons
					.convertStringToBoolean(enableStp));

			colName = "overrideStp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String overrideStp = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			deviceStp.setOverrideStp(AhRestoreCommons
					.convertStringToBoolean(overrideStp));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				BeLogTools.restoreLog(BeLogTools.DEBUG, "Restore table '" + tableName + "' data be lost, cause: 'owner' column is  not available.");
				continue;
			}

			deviceStp.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			settings.put(id, deviceStp);
		}
		return settings;

	}

	public static Map<String, List<InterfaceStpSettings>> getAllPortLevelSettings() throws Exception{
		Map<String, List<InterfaceStpSettings>> settings = new HashMap<String, List<InterfaceStpSettings>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "interface_stp_settings";
		/**
		 * Check validation of DEVICE_STP_SETTINGS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read " + tableName + " file.");
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {

			InterfaceStpSettings stpSettings = new InterfaceStpSettings();

			colName = "device_stp_settings_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);

			if (!isColPresent) {
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			colName = "interfaceNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String interfaceNum = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			interfaceNum = RestoreWiredPortTemplate.replaceSFPPort(interfaceNum);
			stpSettings.setInterfaceNum(Short.valueOf(interfaceNum));

			if(stpSettings.getInterfaceNum() == AhInterface.DEVICE_IF_TYPE_USB){
				continue;
			}

			colName = "enableStp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String enableStp = isColPresent ? xmlParser.getColVal(i,
					colName) : "true";
			stpSettings.setEnableStp(AhRestoreCommons
					.convertStringToBoolean(enableStp));

			colName = "edgePort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String edgePort = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			stpSettings.setEdgePort(AhRestoreCommons
					.convertStringToBoolean(edgePort));

			colName = "bpduMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String bpduMode = isColPresent ? xmlParser.getColVal(i,
					colName) : Short.toString(InterfaceStpSettings.BPDU_DEFAULT_MODE);
			stpSettings.setBpduMode(Short.valueOf(bpduMode));

			colName = "devicePriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String devicePriority = isColPresent ? xmlParser.getColVal(i,
					colName) : Short.toString(InterfaceStpSettings.DEVICE_DEFAULT_PRIORITY);
			stpSettings.setDevicePriority(AhRestoreCommons
					.convertInt(devicePriority));

			colName = "devicePathCost";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String devicePathCost = isColPresent ? xmlParser.getColVal(i,
					colName) : Integer.toString(InterfaceStpSettings.DEVICE_DEFAULT_PATH_COST);
			stpSettings.setDevicePathCost(AhRestoreCommons
					.convertInt(devicePathCost));

			if (settings.get(hiveApId) == null) {
            	 List<InterfaceStpSettings> list = new ArrayList<InterfaceStpSettings>();;
            	 list.add(stpSettings);
            	 settings.put(hiveApId, list);
 			} else {
 				settings.get(hiveApId).add(stpSettings);
 			}
		}
		return settings;
	}

	public static Map<String, List<InterfaceMstpSettings>> getAllPortMstpSettings() throws Exception{
		Map<String, List<InterfaceMstpSettings>> settings = new HashMap<String, List<InterfaceMstpSettings>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "interface_mstp_settings";
		/**
		 * Check validation of INTERFACE_MSTP_SETTINGS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read " + tableName + " file.");
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {

			InterfaceMstpSettings stpSettings = new InterfaceMstpSettings();

			colName = "device_stp_settings_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);

			if (!isColPresent) {
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			colName = "interfaceNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String interfaceNum = isColPresent ? xmlParser
					.getColVal(i, colName) : "";
			stpSettings.setInterfaceNum(Short.valueOf(interfaceNum));

			colName = "instance";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String instance = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			stpSettings.setInstance(Short.valueOf(instance));

			colName = "devicePriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String devicePriority = isColPresent ? xmlParser.getColVal(i,
					colName) : Short.toString(InterfaceStpSettings.DEVICE_DEFAULT_PRIORITY);
			stpSettings.setDevicePriority(AhRestoreCommons
					.convertInt(devicePriority));

			colName = "devicePathCost";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String devicePathCost = isColPresent ? xmlParser.getColVal(i,
					colName) : Integer.toString(InterfaceStpSettings.DEVICE_DEFAULT_PATH_COST);
			stpSettings.setDevicePathCost(AhRestoreCommons
					.convertInt(devicePathCost));

			if (settings.get(hiveApId) == null) {
            	 List<InterfaceMstpSettings> list = new ArrayList<InterfaceMstpSettings>();
            	 if(stpSettings.getInstance() > 0){
            		 list.add(stpSettings);
                	 settings.put(hiveApId, list);
            	 }
 			} else {
 				if(stpSettings.getInstance() > 0){
 					settings.get(hiveApId).add(stpSettings);
 				}
 			}
		}
		return settings;
	}

	public static Map<String, List<DeviceMstpInstancePriority>> getAllInstancePrioritySettings() throws Exception{
		Map<String, List<DeviceMstpInstancePriority>> settings = new HashMap<String, List<DeviceMstpInstancePriority>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String tableName = "mstp_instance_priority";
		/**
		 * Check validation of MSTP_INSTANCE_PRIORITY.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read " + tableName + " file.");
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {

			DeviceMstpInstancePriority stpSettings = new DeviceMstpInstancePriority();

			colName = "device_stp_settings_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);

			if (!isColPresent) {
				continue;
			}

			String hiveApId = xmlParser.getColVal(i, colName);
			if (hiveApId == null || hiveApId.trim().equals("")
					|| hiveApId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			colName = "instance";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String instance = isColPresent ? xmlParser.getColVal(i,
					colName) : "0";
			stpSettings.setInstance(Short.valueOf(instance));

			colName = "priority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String priority = isColPresent ? xmlParser.getColVal(i,
					colName) : Short.toString(InterfaceStpSettings.DEVICE_DEFAULT_PRIORITY);
			stpSettings.setPriority(AhRestoreCommons
					.convertInt(priority));


			if (settings.get(hiveApId) == null) {
				List<DeviceMstpInstancePriority> list = new ArrayList<DeviceMstpInstancePriority>();
				if (stpSettings.getInstance() > 0) {
					list.add(stpSettings);
					settings.put(hiveApId, list);
				}
			} else {
				if (stpSettings.getInstance() > 0) {
					settings.get(hiveApId).add(stpSettings);
				}
			}
		}
		return settings;
	}

	private static boolean isIllegalString(String str) {
		return StringUtils.isBlank(str) || str.trim().equalsIgnoreCase("null")
				|| str.trim().toLowerCase().startsWith("_null");
	}

	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}

			if (bo instanceof RadiusOnHiveap) {
				RadiusOnHiveap radius = (RadiusOnHiveap) bo;
				if (radius.getDirectoryOrLdap() != null) {
					radius.getDirectoryOrLdap().size();
				}
			}

			if (bo instanceof ConfigTemplate) {
				ConfigTemplate configTempObj = (ConfigTemplate) bo;
				if (configTempObj.getMgmtServiceDns() != null)
					configTempObj.getMgmtServiceDns().getId();
				if (configTempObj.getMgmtServiceTime() != null)
					configTempObj.getMgmtServiceTime().getId();
				// TODO for remove network object in user profile
				// if(configTempObj.getMgtNetwork() != null)
				// configTempObj.getMgtNetwork().getId();
				if(null != configTempObj.getSwitchSettings()){
					if(null != configTempObj.getSwitchSettings().getStpSettings()){
						if(null != configTempObj.getSwitchSettings().getStpSettings().getMstpRegion()){
							if(null != configTempObj.getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList()){
								configTempObj.getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList().size();
							}
						}
					}
				}
				if (null != configTempObj.getPortProfiles()){
					for(PortGroupProfile pgp: configTempObj.getPortProfiles()){
						if(pgp.getBasicProfiles() != null){
							pgp.getBasicProfiles().size();
							for(PortBasicProfile basicPort : pgp.getBasicProfiles()){
								if(basicPort.getAccessProfile() != null){
									basicPort.getAccessProfile().getId();
									if(null != basicPort.getAccessProfile().getCwp()){
										basicPort.getAccessProfile().getCwp().getId();
									}
								}
							}
						}
					}
			}
			}

			return null;
		}
	}

}