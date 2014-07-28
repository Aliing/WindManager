package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AutoProvisionDeviceInterface;
import com.ah.bo.hiveap.DeviceIPSubNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApEth;
import com.ah.bo.hiveap.HiveApSerialNumber;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.CountryCode;

public class RestoreHiveApAutoProvision {

	private static final String mainTable = "hive_ap_auto_provision";

	private static final String macItemTable = "hive_ap_auto_provision_maces";

	private static final String snTable = "hive_ap_serial_number";

	private static final String ipSubNetworksTable = "device_interface_ipsubnetwork";

	private static final String ipItemTable = "device_auto_provision_ipsubnetworks";

	private static final String deviceIfTable = "device_auto_provision_interface";

	private static Map<String, List<String>> getAllProvisionConfigHiveAps()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_auto_provision_maces.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(macItemTable);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<String>> macAddressInfo = new HashMap<String, List<String>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hive_ap_auto_provision_id
			 */
			colName = "hive_ap_auto_provision_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macItemTable, colName);
			if (!isColPresent) {
				/**
				 * The hive_ap_auto_provision_id column must be exist in the
				 * table of hive_ap_auto_provision_maces
				 */
				continue;
			}

			String auto_provisioning_config_id = xmlParser
					.getColVal(i, colName);
			if (auto_provisioning_config_id == null
					|| auto_provisioning_config_id.trim().equals("")
					|| auto_provisioning_config_id.trim().equalsIgnoreCase(
							"null")) {
				continue;
			}

			/**
			 * Set macaddress
			 */
			colName = "macaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macItemTable, colName);
			if (!isColPresent) {
				/**
				 * The macaddress column must be exist in the table of
				 * hive_ap_auto_provision_maces
				 */
				continue;
			}

			String macaddress = xmlParser.getColVal(i, colName);
			if (macaddress == null || macaddress.trim().equals("")
					|| macaddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (macAddressInfo.get(auto_provisioning_config_id) == null) {
				List<String> macAddresses = new ArrayList<String>();
				macAddresses.add(macaddress);
				macAddressInfo.put(auto_provisioning_config_id, macAddresses);
			} else {
				macAddressInfo.get(auto_provisioning_config_id).add(macaddress);
			}
		}
		return macAddressInfo;
	}

	private static Map<String, List<String>> getAllProvisionConfigIpAddresses()
		throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean restoreRet = xmlParser.readXMLFile(ipItemTable);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<String>> ipAddressInfo = new HashMap<String, List<String>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set device_auto_provision_id
			 */
			colName = "device_auto_provision_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ipItemTable, colName);
			if (!isColPresent) {
				continue;
			}

			String device_auto_provision_id = xmlParser
					.getColVal(i, colName);
			if (device_auto_provision_id == null
					|| device_auto_provision_id.trim().equals("")
					|| device_auto_provision_id.trim().equalsIgnoreCase(
							"null")) {
				continue;
			}

			/**
			 * Set ipaddress
			 */
			colName = "ipsubnetwork";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					ipItemTable, colName);
			if (!isColPresent) {
				/**
				 * The ipaddress column must be exist
				 */
				continue;
			}

			String ipaddress = xmlParser.getColVal(i, colName);
			if (ipaddress == null || ipaddress.trim().equals("")
					|| ipaddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (ipAddressInfo.get(device_auto_provision_id) == null) {
				List<String> ipAddresses = new ArrayList<String>();
				ipAddresses.add(ipaddress);
				ipAddressInfo.put(device_auto_provision_id, ipAddresses);
			} else {
				ipAddressInfo.get(device_auto_provision_id).add(ipaddress);
			}
		}
		return ipAddressInfo;
	}

	private static Map<String, List<AutoProvisionDeviceInterface>> getAllProvisionConfigDeviceInterfaces()
		throws AhRestoreException, AhRestoreColNotExistException {
		Map<String, List<AutoProvisionDeviceInterface>> interfs = new HashMap<String, List<AutoProvisionDeviceInterface>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean restoreRet = xmlParser.readXMLFile(deviceIfTable);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read device_auto_provision_interface.xml file.");
			return interfs;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			colName = "device_auto_provision_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}

			String device_auto_provision_id = xmlParser
					.getColVal(i, colName);
			if (device_auto_provision_id == null
					|| device_auto_provision_id.trim().equals("")
					|| device_auto_provision_id.trim().equalsIgnoreCase(
							"null")) {
				continue;
			}

			colName = "adminstate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			int adminState = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));

			colName = "interfaceport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			int interfacePort = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));

			colName = "interfacerole";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			int interfaceRole = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));

			colName = "interfacetransmissiontype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			int interfaceTransmissionType = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));

			colName = "interfacespeed";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			int interfaceSpeed = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));

			colName = "interfacedownstreambandwidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			if (!isColPresent) {
				continue;
			}
			String interfaceDownstreamBandwidth = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));

			colName = "psestate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			int pseState = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : AhInterface.ETH_PSE_8023af;

			colName = "pseEnabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			boolean pseEnabled = true;
			if (!isColPresent) {
				pseEnabled = (pseState!=AhInterface.ETH_PSE_SHUTDOWN);
			} else {
				pseEnabled = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			}
			if (!pseEnabled) {
				pseState = AhInterface.ETH_PSE_8023af;
			}

			colName = "psePriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			String psePriority = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : AhInterface.ETH_PSE_PRIORITY_ETH2;
			if (!isColPresent && interfacePort==AhInterface.DEVICE_IF_TYPE_ETH1){
				psePriority = AhInterface.ETH_PSE_PRIORITY_ETH1;
			}

			colName = "enableNat";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					deviceIfTable, colName);
			boolean enableNat = true;
			if (isColPresent) {
				enableNat = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			}
			AutoProvisionDeviceInterface autoInf = new AutoProvisionDeviceInterface();
			autoInf.setAdminState((short)adminState);
			autoInf.setInterfacePort((short)interfacePort);
			autoInf.setInterfaceRole((short)interfaceRole);
			autoInf.setInterfaceTransmissionType((short)interfaceTransmissionType);
			autoInf.setInterfaceSpeed((short)interfaceSpeed);
			autoInf.setInterfaceDownstreamBandwidth(interfaceDownstreamBandwidth);
			autoInf.setPseState((short) pseState);
			autoInf.setPseEnabled(pseEnabled);
			autoInf.setPsePriority(psePriority);
			autoInf.setEnableNat(enableNat);

			if (interfs.get(device_auto_provision_id) == null) {
				List<AutoProvisionDeviceInterface> interf_list = new ArrayList<AutoProvisionDeviceInterface>();
				interf_list.add(autoInf);
				interfs.put(device_auto_provision_id, interf_list);
			} else {
				interfs.get(device_auto_provision_id).add(autoInf);
			}
		}

		return interfs;
	}

	private static List<HiveApAutoProvision> getAllProvisionConfig()
			throws AhRestoreException {
		Map<String, List<String>> macAddresses = null;
		Map<String, List<String>> ipAddresses = null;
		Map<String, List<AutoProvisionDeviceInterface>> deviceInfs = null;

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_auto_provision.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(mainTable);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_auto_provision.xml file.");
			return null;
		}

		/**
		 * No one row data stored in hive_ap_auto_provision table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApAutoProvision> configs = new ArrayList<HiveApAutoProvision>();

		if (rowCount <= 0) {
			return configs;
		}

		// fetch the child table afte make sure the parent table has data.
		try {
			macAddresses = getAllProvisionConfigHiveAps();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg(
					"get provision config macAddresses error", e1);
		}

		// fetch ip subnetwork data
		try {
			ipAddresses = getAllProvisionConfigIpAddresses();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg(
					"get provision config ipAddresses error", e1);
		}

		// fetch device interface data
		try {
			deviceInfs = getAllProvisionConfigDeviceInterfaces();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg(
					"get provision config device interfaces error", e1);
		}

		boolean isColPresent;
		String colName;
		HiveApAutoProvision config;
		HiveApEth eth0;
		HiveApEth eth1;
		HiveApEth red0;
		HiveApEth agg0;
		HiveApWifi wifi0;
		HiveApWifi wifi1;

		for (int i = 0; i < rowCount; i++) {
			try {
				config = new HiveApAutoProvision();
				eth0 = config.getEth0();
				eth1 = config.getEth1();
				red0 = config.getRed0();
				agg0 = config.getAgg0();
				wifi0 = config.getWifi0();
				wifi1 = config.getWifi1();
				/**
				 * Set ID
				 */
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				if (!isColPresent) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_ap_auto_provision' data be lost, cause: 'id' column is not exist.");
					/**
					 * The id column must be exist in the table of
					 * hive_ap_auto_provision
					 */
					continue;
				}
				String id = isColPresent ? xmlParser.getColVal(i, colName)
						: "1";

				if (null != macAddresses && null != macAddresses.get(id)
						&& macAddresses.get(id).size() > 0) {
					config.setMacAddresses(macAddresses.get(id));
					config.setAccessControled(true);
					// only using acl, restore aclType value
					/**
					 * Set acltype
					 */
					colName = "acltype";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							mainTable, colName);
					int acltype = (isColPresent ? AhRestoreCommons
							.convertInt(xmlParser.getColVal(i, colName))
							: HiveApAutoProvision.ACL_MANUAL_AP);
					config.setAclType((short) acltype);
				}

				if (null != ipAddresses && null != ipAddresses.get(id)
						&& ipAddresses.get(id).size() > 0) {
					config.setIpSubNetworks(ipAddresses.get(id));
					config.setAccessControled(true);
				}

				if (null != deviceInfs && null != deviceInfs.get(id)
						&& deviceInfs.get(id).size() > 0) {
					config.setDeviceInterfaces(deviceInfs.get(id));
				}

				/**
				 * Set autoprovision
				 */
				colName = "autoprovision";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String autoprovisioning = (isColPresent ? xmlParser.getColVal(
						i, colName) : "false");
				config.setAutoProvision(AhRestoreCommons
						.convertStringToBoolean(autoprovisioning));

				/**
				 * Set autoprovision
				 */
				colName = "enableOneTimePassword";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String enableOneTimePassword = (isColPresent ? xmlParser.getColVal(
						i, colName) : "false");
				config.setEnableOneTimePassword(AhRestoreCommons
						.convertStringToBoolean(enableOneTimePassword));

				/**
				 * Set imagename
				 */
				colName = "imagename";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String imagename = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setImageName(imagename.trim());

				/**
				 * Set imageversion
				 */
				colName = "imageversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String imageversion = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setImageVersion(imageversion.trim());

				/**
				 * Set provisioningname
				 */
				colName = "provisioningname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String provisioningname = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setProvisioningName(provisioningname.trim());

				/**
				 * Set name
				 */
				colName = "name";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String name = "";
				if (isColPresent) {
					name = AhRestoreCommons
					.convertString(xmlParser.getColVal(i, colName));
				} else if (AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, "provisioningname")) {
					name = AhRestoreCommons
					.convertString(xmlParser.getColVal(i, "provisioningname"));
				}
				config.setName(name.trim());

				/**
				 * Set deviceType
				 */
				colName = "devicetype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				int deviceType = isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName)) : HiveAp.Device_TYPE_HIVEAP;
				config.setDeviceType((short)deviceType);

				/**
				 * Set description
				 */
				colName = "description";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String description = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				if (description != null && description.length() > 255) {
					AhRestoreDBTools.logRestoreMsg("Description of auto provisioning profile with old id " + id + " has been shorten to 255 bytes.");
					description = description.substring(0, 255);
				}
				config.setDescription(description.trim());

				/**
				 * Set classificationTag1
				 */
				colName = "classificationtag1";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String classificationTag1 = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setClassificationTag1(classificationTag1.trim());

				/**
				 * Set classificationTag2
				 */
				colName = "classificationtag2";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String classificationTag2 = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setClassificationTag2(classificationTag2.trim());

				/**
				 * Set classificationTag3
				 */
				colName = "classificationtag3";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String classificationTag3 = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config.setClassificationTag3(classificationTag3.trim());

				/**
				 * Set rewritemap
				 */
				colName = "rewritemap";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String rewritemap = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				config.setRewriteMap(AhRestoreCommons
						.convertStringToBoolean(rewritemap));

				/**
				 * Set uploadconfig
				 */
				colName = "uploadconfig";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String uploadconfig = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				config.setUploadConfig(AhRestoreCommons
						.convertStringToBoolean(uploadconfig));

				/**
				 * Set uploadimage
				 */
				colName = "uploadimage";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String uploadimage = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				config.setUploadImage(AhRestoreCommons
						.convertStringToBoolean(uploadimage));

				/**
				 * Set rebooting
				 */
				colName = "rebooting";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String rebooting = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				config.setRebooting(AhRestoreCommons
						.convertStringToBoolean(rebooting));

				/**
				 * Set modeltype
				 */
				colName = "modeltype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String modeltype = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(HiveAp.HIVEAP_MODEL_20);
				config.setModelType((short) AhRestoreCommons
						.convertInt(modeltype));

				/**
				 * Set countrycode
				 */
				colName = "countrycode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String countrycode = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(CountryCode.COUNTRY_CODE_US);
				config.setCountryCode(AhRestoreCommons.convertInt(countrycode));

				/**
				 * Set cfgadminuser
				 */
				colName = "cfgadminuser";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String cfgadminuser = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				config.setCfgAdminUser(AhRestoreCommons
						.convertString(cfgadminuser));

				/**
				 * Set cfgpassword
				 */
				colName = "cfgpassword";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String cfgpassword = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				config.setCfgPassword(AhRestoreCommons
						.convertString(cfgpassword));

				/**
				 * Set cfgreadonlypassword
				 */
				colName = "cfgreadonlypassword";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String cfgreadonlypassword = isColPresent ? xmlParser
						.getColVal(i, colName) : "";
				config.setCfgReadOnlyPassword(AhRestoreCommons
						.convertString(cfgreadonlypassword));

				/**
				 * Set cfgreadonlyuser
				 */
				colName = "cfgreadonlyuser";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String cfgreadonlyuser = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				config.setCfgReadOnlyUser(AhRestoreCommons
						.convertString(cfgreadonlyuser));

				/**
				 * Set passPhrase
				 */
				colName = "passphrase";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String passPhrase = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				config
						.setPassPhrase(AhRestoreCommons
								.convertString(passPhrase));
				
				/**
				 * Set includeTopologyInfo
				 */
				colName = "includeTopologyInfo";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String includeTopologyInfo = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				config.setIncludeTopologyInfo(AhRestoreCommons
						.convertStringToBoolean(includeTopologyInfo));

				/*
				 * set fields for red0
				 * ==========================================
				 * ====================
				 */
				/*
				 * Set red0_admin_state
				 */
				colName = "red0_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String red0_admin_state = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
				red0.setAdminState((short) AhRestoreCommons
						.convertInt(red0_admin_state));

				/*
				 * Set red0_operation_mode
				 */
				colName = "red0_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String red0_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
				// fix bug 22056
				red0.setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
//				red0.setOperationMode((short) AhRestoreCommons
//						.convertInt(red0_operation_mode));

				/*
				 * Set red0_allowed_vlan
				 */
				colName = "red0_allowed_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String red0_allowed_vlan = isColPresent ? xmlParser.getColVal(
						i, colName) : HiveApEth.ALLOWED_VLAN_ALL;
				red0.setAllowedVlan(AhRestoreCommons
						.convertString(red0_allowed_vlan));
				/*
				 * Set red0_multinative_vlan
				 */
				colName = "red0_multinative_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String red0_multinative_vlan = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				try{
					Integer vlanInteRed0 = Integer.valueOf(red0_multinative_vlan);
					red0.setMultiNativeVlan(vlanInteRed0);
				}catch(Exception ex){
					red0.setMultiNativeVlan(null);
				}
				/*
				 * set fields for agg0
				 * ==========================================
				 * ====================
				 */
				/*
				 * Set agg0_admin_state
				 */
				colName = "agg0_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String agg0_admin_state = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
				agg0.setAdminState((short) AhRestoreCommons
						.convertInt(agg0_admin_state));

				/*
				 * Set agg0_operation_mode
				 */
				colName = "agg0_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String agg0_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
				// fix bug 22056
				agg0.setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
//				agg0.setOperationMode((short) AhRestoreCommons
//						.convertInt(agg0_operation_mode));

				/*
				 * Set agg0_allowed_vlan
				 */
				colName = "agg0_allowed_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String agg0_allowed_vlan = isColPresent ? xmlParser.getColVal(
						i, colName) : HiveApEth.ALLOWED_VLAN_ALL;
				agg0.setAllowedVlan(AhRestoreCommons
						.convertString(agg0_allowed_vlan));

				/*
				 * Set agg0_multinative_vlan
				 */
				colName = "agg0_multinative_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String agg0_multinative_vlan = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				try{
					Integer vlanInteAgg0 = Integer.valueOf(agg0_multinative_vlan);
					agg0.setMultiNativeVlan(vlanInteAgg0);
				}catch(Exception ex){
					agg0.setMultiNativeVlan(null);
				}
				/*
				 * set fields for eth0
				 * ==========================================
				 * ====================
				 */
				/*
				 * Set eth0_admin_state
				 */
				colName = "eth0_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_admin_state = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
				eth0.setAdminState((short) AhRestoreCommons
						.convertInt(eth0_admin_state));

				/*
				 * Set eth0_operation_mode
				 */
				colName = "eth0_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
				eth0.setOperationMode((short) AhRestoreCommons
						.convertInt(eth0_operation_mode));

				/*
				 * Set eth0_duplex
				 */
				colName = "eth0_duplex";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_duplex = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ETH_DUPLEX_AUTO);
				eth0
						.setDuplex((short) AhRestoreCommons
								.convertInt(eth0_duplex));

				/*
				 * Set eth0_speed
				 */
				colName = "eth0_speed";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_speed = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ETH_SPEED_AUTO);
				eth0.setSpeed((short) AhRestoreCommons.convertInt(eth0_speed));

				/*
				 * Set eth0_bind_interface
				 */
				colName = "eth0_bind_interface";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_bind_interface = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.ETH_BIND_IF_NULL);
				eth0.setBindInterface((short) AhRestoreCommons
						.convertInt(eth0_bind_interface));

				/*
				 * Set eth0_bind_role
				 */
				colName = "eth0_bind_role";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_bind_role = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(AhInterface.ETH_BIND_ROLE_NULL);
				eth0.setBindRole((short) AhRestoreCommons
						.convertInt(eth0_bind_role));

				/*
				 * Set eth0_allowed_vlan
				 */
				colName = "eth0_allowed_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth0_allowed_vlan = isColPresent ? xmlParser.getColVal(
						i, colName) : HiveApEth.ALLOWED_VLAN_ALL;
				eth0.setAllowedVlan(AhRestoreCommons
						.convertString(eth0_allowed_vlan));

				/*
				 * Set eth0_multinative_vlan
				 */
				colName = "eth0_multinative_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String eth0_multinative_vlan = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				try{
					Integer vlanInteEth0 = Integer.valueOf(eth0_multinative_vlan);
					eth0.setMultiNativeVlan(vlanInteEth0);
				}catch(Exception ex){
					eth0.setMultiNativeVlan(null);
				}
				/*
				 * set fields for eth1
				 * ==========================================
				 * ====================
				 */
				/*
				 * Set eth1_admin_state
				 */
				colName = "eth1_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_admin_state = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ADMIN_STATE_UP);
				eth1.setAdminState((short) AhRestoreCommons
						.convertInt(eth1_admin_state));

				/*
				 * Set eth1_operation_mode
				 */
				colName = "eth1_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_BACKHAUL);
				eth1.setOperationMode((short) AhRestoreCommons
						.convertInt(eth1_operation_mode));

				/*
				 * Set eth1_duplex
				 */
				colName = "eth1_duplex";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_duplex = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ETH_DUPLEX_AUTO);
				eth1
						.setDuplex((short) AhRestoreCommons
								.convertInt(eth1_duplex));

				/*
				 * Set eth1_speed
				 */
				colName = "eth1_speed";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_speed = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.ETH_SPEED_AUTO);
				eth1.setSpeed((short) AhRestoreCommons.convertInt(eth1_speed));

				/*
				 * Set eth1_bind_interface
				 */
				colName = "eth1_bind_interface";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_bind_interface = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.ETH_BIND_IF_NULL);
				eth1.setBindInterface((short) AhRestoreCommons
						.convertInt(eth1_bind_interface));

				/*
				 * Set eth1_bind_role
				 */
				colName = "eth1_bind_role";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_bind_role = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(AhInterface.ETH_BIND_ROLE_NULL);
				eth1.setBindRole((short) AhRestoreCommons
						.convertInt(eth1_bind_role));

				/*
				 * Set eth1_allowed_vlan
				 */
				colName = "eth1_allowed_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String eth1_allowed_vlan = isColPresent ? xmlParser.getColVal(
						i, colName) : HiveApEth.ALLOWED_VLAN_ALL;
				eth1.setAllowedVlan(AhRestoreCommons
						.convertString(eth1_allowed_vlan));

				/*
				 * Set eth1_multinative_vlan
				 */
				colName = "eth1_multinative_vlan";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap", colName);
				String eth1_multinative_vlan = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				try{
					Integer vlanInteEth1 = Integer.valueOf(eth1_multinative_vlan);
					eth1.setMultiNativeVlan(vlanInteEth1);
				}catch(Exception ex){
					eth1.setMultiNativeVlan(null);
				}
				/**
				 * Set wifi0_admin_state
				 */
				colName = "wifi0_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0_admin_state = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(AhInterface.ADMIN_STATE_UP);
				wifi0.setAdminState((short) AhRestoreCommons
						.convertInt(wifi0_admin_state));
				/**
				 * Set wifi0_radio_channel
				 */
				colName = "wifi0_radio_channel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0_radio_channel = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.CHANNEL_BG_AUTO);
				wifi0.setChannel(AhRestoreCommons
						.convertInt(wifi0_radio_channel));

				/**
				 * Set wifi0_operation_mode
				 */
				colName = "wifi0_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_ACCESS);
				wifi0.setOperationMode((short) AhRestoreCommons
						.convertInt(wifi0_operation_mode));

				/**
				 * Set wifi0_radio_power
				 */
				colName = "wifi0_radio_power";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0_radio_power = isColPresent ? xmlParser.getColVal(
						i, colName) : String.valueOf(AhInterface.POWER_AUTO);
				wifi0.setPower(AhRestoreCommons.convertInt(wifi0_radio_power));

				/**
				 * Set wifi0_radio_mode
				 */
				colName = "wifi0_radio_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0_radio_mode = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.RADIO_MODE_BG);
				wifi0.setRadioMode((short) AhRestoreCommons
						.convertInt(wifi0_radio_mode));

				colName = "wifi1_admin_state";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1_admin_state = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(AhInterface.ADMIN_STATE_UP);
				wifi1.setAdminState((short) AhRestoreCommons
						.convertInt(wifi1_admin_state));

				/**
				 * Set wifi1_radio_channel
				 */
				colName = "wifi1_radio_channel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1_radio_channel = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.CHANNEL_A_AUTO);
				wifi1.setChannel(AhRestoreCommons
						.convertInt(wifi1_radio_channel));

				/**
				 * Set wifi1_operation_mode
				 */
				colName = "wifi1_operation_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1_operation_mode = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(AhInterface.OPERATION_MODE_DUAL);
				wifi1.setOperationMode((short) AhRestoreCommons
						.convertInt(wifi1_operation_mode));

				/**
				 * Set wifi1_radio_power
				 */
				colName = "wifi1_radio_power";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1_radio_power = isColPresent ? xmlParser.getColVal(
						i, colName) : String.valueOf(AhInterface.POWER_AUTO);
				wifi1.setPower((short) AhRestoreCommons
						.convertInt(wifi1_radio_power));

				/**
				 * Set wifi1_radio_mode
				 */
				colName = "wifi1_radio_mode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1_radio_mode = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(AhInterface.RADIO_MODE_A);
				wifi1.setRadioMode((short) AhRestoreCommons
						.convertInt(wifi1_radio_mode));

				/**
				 * Set configtemplateid
				 */
				colName = "configtemplateid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String config_template_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "-1";
				if (!"".equals(config_template_id)) {
					Long config_template_id_new = AhRestoreNewMapTools
							.getMapConfigTemplate(AhRestoreCommons
									.convertLong(config_template_id));
					if (null != config_template_id_new) {
						if (AhRestoreNewMapTools.getDefaultConfigTemplateId(AhRestoreCommons
									.convertLong(config_template_id)) != null) {
							config.setConfigTemplateId(null);
							AhRestoreDBTools
							.logRestoreMsg("The old id:" + config_template_id
												+ " was def-policy-configtemplate,it's not supported in auto provisioning now.");
						} else {
							config.setConfigTemplateId(config_template_id_new);
						}
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new config template id mapping to old id:"
										+ config_template_id);
					}
				}

				/**
				 * Set mapcontainerid
				 */
				colName = "mapcontainerid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String map_container_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
						i, colName)) : "-1";
				if (!"".equals(map_container_id)) {
					Long map_container_id_new = AhRestoreNewMapTools
							.getMapMapContainer(AhRestoreCommons.convertLong(map_container_id));
					if (null != map_container_id_new) {
						config.setMapContainerId(map_container_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new map container id mapping to old id:"
										+ map_container_id);
					}
				}


				/**
				 * Set wifi0profileid
				 */
				colName = "wifi0profileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi0profileid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "-1";
				if (!"".equals(wifi0profileid)) {
					Long wifi0profileid_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons.convertLong(wifi0profileid));
					if (null != wifi0profileid_new) {
						config.setWifi0ProfileId(wifi0profileid_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi0profileid);
					}
				}

				/**
				 * Set wifi1profileid
				 */
				colName = "wifi1profileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String wifi1profileid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "-1";
				if (!"".equals(wifi1profileid)) {
					Long wifi1profileid_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons.convertLong(wifi1profileid));
					if (null != wifi1profileid_new) {
						config.setWifi1ProfileId(wifi1profileid_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi1profileid);
					}
				}

				/**
				 * Set cfgcapwapipid
				 */
				colName = "cfgcapwapipid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String cfgcapwapipid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "-1";
				if (!"".equals(cfgcapwapipid)) {
					Long cfgcapwapipid_new = AhRestoreNewMapTools
							.getMapIpAdddress(AhRestoreCommons
									.convertLong(cfgcapwapipid));
					if (null != cfgcapwapipid_new) {
						config.setCfgCapwapIpId(cfgcapwapipid_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new ip address id mapping to old id:"
										+ cfgcapwapipid);
					}
				}

				/**
				 * Set capwapbackupipid
				 */
				colName = "capwapbackupipid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String capwapbackupipid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
						i, colName)) : "-1";
				if (!"".equals(capwapbackupipid)) {
					Long capwapbackupipid_new = AhRestoreNewMapTools
							.getMapIpAdddress(AhRestoreCommons.convertLong(capwapbackupipid));
					if (null != capwapbackupipid_new) {
						config.setCapwapBackupIpId(capwapbackupipid_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new ip address id mapping to old id:"
										+ capwapbackupipid);
					}
				}

				/*
				 * Set usbConnectionModel
				 */
				colName = "usbConnectionModel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				String usbConnectionModel = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(HiveAp.USB_CONNECTION_MODEL_NEEDED);
				config.setUsbConnectionModel((short) AhRestoreCommons.convertInt(usbConnectionModel));

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						mainTable, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_ap_auto_provision' data be lost, cause: 'owner' column is not available.");
					continue;
				}

				config.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
				config.setEth0(eth0);
				config.setEth1(eth1);
				config.setRed0(red0);
				config.setAgg0(agg0);
				config.setWifi0(wifi0);
				config.setWifi1(wifi1);

				configs.add(config);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get provision config", e);
			}
		}

		return configs;
	}

	public static boolean restoreHiveApAutoProvision() {
		restoreSerialNumbers();
		restoreIpSubnetworks();
		try {
			long start = System.currentTimeMillis();
			List<HiveApAutoProvision> list = getAllProvisionConfig();
			if (null == list) {
				// indicate hive_ap_auto_provision.xml is not existed.
				// try to restore the old files.
				AhRestoreDBTools
						.logRestoreMsg("Try to restore old data((Data old than 3.4.0.0))");
				return RestoreProvisionConfig.restoreProvisionConfig();
			} else {
				if (!list.isEmpty()) {
					List<Long> oldIdList = new ArrayList<Long>(list
							.size());
					for(HiveApAutoProvision autoProvision:list){
						oldIdList.add(autoProvision.getId());
						autoProvision.setId(null);
					}

					QueryUtil.restoreBulkCreateBos(list);

					// set id mapping to map tool.
					for (int i = 0; i < list.size(); i++) {
						AhRestoreNewMapTools.setMapHiveApAutoProvision(oldIdList.get(i),
								list.get(i).getId());
					}
				}
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore HiveAP autoprovisio completely, count:"
							+ list.size() + ". cost:" + (end - start) + " ms.");
			return true;
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(
					"Restore HiveAP autoprovision error.", e);
			return false;
		}
	}

	private static boolean restoreSerialNumbers() {
		try {
			long start = System.currentTimeMillis();
			List<HiveApSerialNumber> list = getAllSerialNumbers();
			int count = 0;
			if (null != list && !list.isEmpty()) {
				count = list.size();
				QueryUtil.restoreBulkCreateBos(list);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore HiveAP serial numbers finished, count:"
							+ count + ". cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(
					"Restore HiveAP serial numbers catch exception ", e);
			return false;
		}
		return true;
	}

	private static List<HiveApSerialNumber> getAllSerialNumbers() throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_serial_number.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(snTable);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_serial_number.xml file.");
			return null;
		}

		/**
		 * No one row data stored in hive_ap_serial_number table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApSerialNumber> configs = new ArrayList<HiveApSerialNumber>();

		boolean isColPresent;
		String colName;
		HiveApSerialNumber config;
		for (int i = 0; i < rowCount; i++) {
			try {
				config = new HiveApSerialNumber();

				/**
				 * Set ID
				 */
				// colName = "id";
				// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				// snTable, colName);
				// String id = isColPresent ? xmlParser.getColVal(i, colName)
				// : "1";
				/**
				 * Set serialnumber
				 */
				colName = "serialnumber";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						snTable, colName);
				String serialnumber = (isColPresent ? xmlParser.getColVal(i,
						colName) : "");
				if (serialnumber == null || serialnumber.trim().equals("")
						|| serialnumber.trim().equalsIgnoreCase("null")) {
					continue;
				}
				config.setSerialNumber(serialnumber);

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						snTable, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}

				config.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				configs.add(config);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get serial numbers", e);
			}
		}
		return configs.size() > 0 ? configs : null;
	}


	private static boolean restoreIpSubnetworks() {
		try {
			long start = System.currentTimeMillis();
			List<DeviceIPSubNetwork> list = getAllIpSubnetworks();
			int count = 0;
			if (null != list && !list.isEmpty()) {
				count = list.size();
				QueryUtil.restoreBulkCreateBos(list);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore HiveAP ip subNetworks finished, count:"
							+ count + ". cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(
					"Restore HiveAP ip subNetworks catch exception ", e);
			return false;
		}
		return true;
	}

	private static List<DeviceIPSubNetwork> getAllIpSubnetworks() throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean restoreRet = xmlParser.readXMLFile(ipSubNetworksTable);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read device_interface_ipsubnetwork.xml file.");
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<DeviceIPSubNetwork> configs = new ArrayList<DeviceIPSubNetwork>();

		boolean isColPresent;
		String colName;
		DeviceIPSubNetwork config;
		for (int i = 0; i < rowCount; i++) {
			try {
				config = new DeviceIPSubNetwork();

				/**
				 * Set ID
				 */
				// colName = "id";
				// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				// snTable, colName);
				// String id = isColPresent ? xmlParser.getColVal(i, colName)
				// : "1";
				/**
				 * Set serialnumber
				 */
				colName = "ipsubnetwork";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						ipSubNetworksTable, colName);
				String ipSubNetwork = (isColPresent ? xmlParser.getColVal(i,
						colName) : "");
				if (ipSubNetwork == null || ipSubNetwork.trim().equals("")
						|| ipSubNetwork.trim().equalsIgnoreCase("null")) {
					continue;
				}
				config.setIpSubNetwork(ipSubNetwork);

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						ipSubNetworksTable, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}

				config.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				configs.add(config);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get ip subNetworks", e);
			}
		}
		return configs.size() > 0 ? configs : null;
	}

}