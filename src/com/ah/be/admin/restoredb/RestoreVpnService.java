package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.UserProfileForTrafficL2;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.config.DomainObjectAction;
import com.ah.ui.actions.config.VpnServiceAction;

public class RestoreVpnService {

	public static final String tableName = "vpn_service";

	public static final String subTableCredentialName = "vpn_service_credential";

	//add the new field for congo
	public static final String subTableGatewaySettings = "vpn_gateway_setting";

	public static final String subTableUserProfileTraffic = "vpn_userprofile_trafficl2";

	public static final String subTableTunnelExceptions = "vpn_userprofile_trafficl3";


	private static Map<String, List<VpnServiceCredential>> getAllCredentials()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<VpnServiceCredential>> credentialInfo = new HashMap<String, List<VpnServiceCredential>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vpn_service_credential.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(subTableCredentialName);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read vpn_service_credential.xml file.");
			return credentialInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set vpn_service_id
			 */
			colName = "vpn_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			if (!isColPresent) {
				/**
				 * The vpn_service_id column must be exist in the table of
				 * vpn_service_credential
				 */
				continue;
			}

			String vpn_service_id = xmlParser.getColVal(i, colName);
			if (vpn_service_id == null || vpn_service_id.trim().equals("")
					|| vpn_service_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set credential
			 */
			colName = "credential";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			if (!isColPresent) {
				/**
				 * The credential column must be exist in the table of
				 * vpn_service_credential
				 */
				continue;
			}

			String credential = xmlParser.getColVal(i, colName);
			if (credential == null || credential.trim().equals("")
					|| credential.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set clientname
			 */
			colName = "clientname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			if (!isColPresent) {
				/**
				 * The clientname column must be exist in the table of
				 * vpn_service_credential
				 */
				continue;
			}

			String clientname = xmlParser.getColVal(i, colName);
			if (clientname == null || clientname.trim().equals("")
					|| clientname.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set allocated
			 */
			colName = "allocated";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			String allocated = isColPresent ? xmlParser.getColVal(i, colName)
					: "false";
			
			/**
			 * Set allocatedStatus
			 */
			colName = "allocatedStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			String allocatedStatus = isColPresent ? xmlParser.getColVal(i, colName)
					: null;

			/**
			 * Set assignedclient
			 */
			colName = "assignedclient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			String assignedclient = isColPresent ? xmlParser.getColVal(i,
					colName) : null;

			/**
			 * Set primaryrole
			 */
			colName = "primaryrole";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			String primaryrole = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnServiceCredential.SERVER_ROLE_NONE);

			/**
			 * Set backuprole
			 */
			colName = "backuprole";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableCredentialName, colName);
			String backuprole = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnServiceCredential.SERVER_ROLE_NONE);

			VpnServiceCredential credentialItem = new VpnServiceCredential();
			credentialItem.setCredential(credential);
			credentialItem.setClientName(clientname);
			credentialItem.setPrimaryRole((short) AhRestoreCommons
					.convertInt(primaryrole));
			credentialItem.setBackupRole((short) AhRestoreCommons
					.convertInt(backuprole));
			
			if(allocatedStatus != null){
				credentialItem.setAllocatedStatus((short)AhRestoreCommons.convertInt(allocatedStatus));
			}else if(allocated != null){
				boolean blnAllocated = AhRestoreCommons.convertStringToBoolean(allocated);
				if(blnAllocated){
					credentialItem.setAllocatedStatus(VpnServiceCredential.ALLOCATED_STATUS_USED);
				}else{
					credentialItem.setAllocatedStatus(VpnServiceCredential.ALLOCATED_STATUS_FREE);
				}
			}else{
				credentialItem.setAllocatedStatus(VpnServiceCredential.ALLOCATED_STATUS_FREE);
			}
			
			credentialItem.setAssignedClient(assignedclient);

			if (credentialInfo.get(vpn_service_id) == null) {
				List<VpnServiceCredential> d_routeList = new ArrayList<VpnServiceCredential>();
				d_routeList.add(credentialItem);
				credentialInfo.put(vpn_service_id, d_routeList);
			} else {
				credentialInfo.get(vpn_service_id).add(credentialItem);
			}
		}
		return credentialInfo;
	}

	private static Map<String, List<VpnGatewaySetting>> getAllVpnGateWays()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<VpnGatewaySetting>> vpnGateWaySettingInfo = new HashMap<String, List<VpnGatewaySetting>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vpn_gateway_settingl.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(subTableGatewaySettings);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read vpn_gateway_setting.xml file.");
			return vpnGateWaySettingInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set vpn_gateway_setting_id
			 */
			colName = "vpn_gateway_setting_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableGatewaySettings, colName);
			if (!isColPresent) {
				/**
				 * The vpn_gateway_setting_id column must be exist in the table of
				 * vpn_gateway_setting
				 */
				continue;
			}

			String vpn_gateway_setting_id = xmlParser.getColVal(i, colName);
			if (vpn_gateway_setting_id == null || vpn_gateway_setting_id.trim().equals("")
					|| vpn_gateway_setting_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set hiveApId
			 */
			colName = "hiveapid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableGatewaySettings, colName);
			if (!isColPresent) {
				/**
				 * The hiveapid column must be exist in the table of
				 * vpn_gateway_setting
				 */
				continue;
			}

			String hiveapid = xmlParser.getColVal(i, colName);
			if (hiveapid == null || hiveapid.trim().equals("")
					|| hiveapid.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set externalipaddress
			 */
			colName = "externalipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableGatewaySettings, colName);
			if (!isColPresent) {
				/**
				 * The externalipaddress column must be exist in the table of
				 * vpn_gateway_setting
				 */
				continue;
			}

			String externalipaddress = xmlParser.getColVal(i, colName);
			if (externalipaddress == null || externalipaddress.trim().equals("")
					|| externalipaddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableGatewaySettings, colName);
			if (!isColPresent) {
				/**
				 * The position column must be exist in the table of
				 * vpn_gateway_setting
				 */
				continue;
			}

			String position = xmlParser.getColVal(i, colName);
			if (position == null || position.trim().equals("")
					|| position.trim().equalsIgnoreCase("null")) {
				continue;
			}


			VpnGatewaySetting vpnGatewaySettingItem = new VpnGatewaySetting();
			vpnGatewaySettingItem.setApId(AhRestoreCommons
						.convertLong(hiveapid));
			vpnGatewaySettingItem.setExternalIpAddress(externalipaddress);
			vpnGatewaySettingItem.setReorder(AhRestoreCommons.convertInt(position));

			if (vpnGateWaySettingInfo.get(vpn_gateway_setting_id) == null) {
				List<VpnGatewaySetting> d_routeList = new ArrayList<VpnGatewaySetting>();
				d_routeList.add(vpnGatewaySettingItem);
				vpnGateWaySettingInfo.put(vpn_gateway_setting_id, d_routeList);
			} else {
				vpnGateWaySettingInfo.get(vpn_gateway_setting_id).add(vpnGatewaySettingItem);
			}
		}
		return vpnGateWaySettingInfo;
	}

	private static Map<String, List<UserProfileForTrafficL2>> getUserProfileTrafficL2()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<UserProfileForTrafficL2>> UserProfileForTrafficL2Info = new HashMap<String, List<UserProfileForTrafficL2>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vpn_userprofile_trafficl2.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(subTableUserProfileTraffic);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read vpn_userprofile_trafficl2.xml file.");
			return UserProfileForTrafficL2Info;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set vpn_userprofile_trafficl2_id
			 */
			colName = "vpn_userprofile_trafficl2_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableUserProfileTraffic, colName);
			if (!isColPresent) {
				/**
				 * The vpn_userprofile_trafficl2_id column must be exist in the table of
				 * vpn_userprofile_trafficl2
				 */
				continue;
			}

			String vpn_userprofile_trafficl2_id = xmlParser.getColVal(i, colName);
			if (vpn_userprofile_trafficl2_id == null || vpn_userprofile_trafficl2_id.trim().equals("")
					|| vpn_userprofile_trafficl2_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set useeprofileId
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableUserProfileTraffic, colName);
			if (!isColPresent) {
				/**
				 * The hiveapid column must be exist in the table of
				 * vpn_userprofile_trafficl2
				 */
				continue;
			}

			String userprofileid = xmlParser.getColVal(i, colName);
			if (userprofileid == null || userprofileid.trim().equals("")
					|| userprofileid.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set tunnelSelected
			 */
			colName = "tunnelselected";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableUserProfileTraffic, colName);
			if (!isColPresent) {
				/**
				 * The tunnelselected column must be exist in the table of
				 * vpn_userprofile_trafficl2
				 */
				continue;
			}

			String tunnelselected = xmlParser.getColVal(i, colName);

			/**
			 * Set vpntunnelmodel2
			 */
			colName = "vpntunnelmodel2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableUserProfileTraffic, colName);
			if (!isColPresent) {
				/**
				 * The tunnelselected column must be exist in the table of
				 * vpn_userprofile_trafficl2
				 */
				continue;
			}

			String vpntunnelmodel2 = xmlParser.getColVal(i, colName);
			if (vpntunnelmodel2 == null || vpntunnelmodel2.trim().equals("")
					|| vpntunnelmodel2.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableUserProfileTraffic, colName);
			if (!isColPresent) {
				/**
				 * The position column must be exist in the table of
				 * vpn_userprofile_trafficl2
				 */
				continue;
			}

			String position = xmlParser.getColVal(i, colName);
			if (position == null || position.trim().equals("")
					|| position.trim().equalsIgnoreCase("null")) {
				continue;
			}

			UserProfileForTrafficL2 UserProfileForTrafficL2Item = new UserProfileForTrafficL2();

			if (!userprofileid.equals("") && !userprofileid.trim().equalsIgnoreCase("null")) {
				Long newUserprofile_id = AhRestoreNewMapTools.getMapUserProfile(AhRestoreCommons
						.convertLong(userprofileid.trim()));
				UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserprofile_id);
				UserProfileForTrafficL2Item.setUserProfile(userprofile);
			}

			UserProfileForTrafficL2Item.setTunnelSelected(tunnelselected);
			UserProfileForTrafficL2Item.setVpnTunnelModeL2((short) AhRestoreCommons
					.convertLong(vpntunnelmodel2));
			UserProfileForTrafficL2Item.setPosition(AhRestoreCommons.convertInt(position));

			if (UserProfileForTrafficL2Info.get(vpn_userprofile_trafficl2_id) == null) {
				List<UserProfileForTrafficL2> d_routeList = new ArrayList<UserProfileForTrafficL2>();
				d_routeList.add(UserProfileForTrafficL2Item);
				UserProfileForTrafficL2Info.put(vpn_userprofile_trafficl2_id, d_routeList);
			} else {
				UserProfileForTrafficL2Info.get(vpn_userprofile_trafficl2_id).add(UserProfileForTrafficL2Item);
			}
		}
		return UserProfileForTrafficL2Info;
	}

	private static Map<String, List<UserProfileForTrafficL3>> getUserProfileTrafficL3()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<UserProfileForTrafficL3>> UserProfileForTrafficL3Info = new HashMap<String, List<UserProfileForTrafficL3>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vpn_userprofile_trafficl3.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(subTableTunnelExceptions);
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read vpn_userprofile_trafficl3.xml file.");
			return UserProfileForTrafficL3Info;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set vpn_userprofile_trafficl3_id
			 */
			colName = "vpn_userprofile_trafficl3_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableTunnelExceptions, colName);
			if (!isColPresent) {
				/**
				 * The vpn_userprofile_trafficl3_id column must be exist in the table of
				 * vpn_userprofile_trafficl3
				 */
				continue;
			}

			String vpn_userprofile_trafficl3_id = xmlParser.getColVal(i, colName);
			if (vpn_userprofile_trafficl3_id == null || vpn_userprofile_trafficl3_id.trim().equals("")
					|| vpn_userprofile_trafficl3_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set useeprofileId
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableTunnelExceptions, colName);
			if (!isColPresent) {
				/**
				 * The hiveapid column must be exist in the table of
				 * vpn_userprofile_trafficl3
				 */
				continue;
			}

			String userprofileid = xmlParser.getColVal(i, colName);
			if (userprofileid == null || userprofileid.trim().equals("")
					|| userprofileid.trim().equalsIgnoreCase("null")) {
				continue;
			}


			/**
			 * Set vpntunnelbehavior
			 */
			colName = "vpntunnelbehavior";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableTunnelExceptions, colName);
			if (!isColPresent) {
				/**
				 * The vpntunnelbehavior column must be exist in the table of
				 * vpn_userprofile_trafficl3
				 */
				continue;
			}

			String vpntunnelbehavior = xmlParser.getColVal(i, colName);
			if (vpntunnelbehavior == null || vpntunnelbehavior.trim().equals("")
					|| vpntunnelbehavior.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					subTableTunnelExceptions, colName);
			if (!isColPresent) {
				/**
				 * The position column must be exist in the table of
				 * vpn_userprofile_trafficl3
				 */
				continue;
			}

			String position = xmlParser.getColVal(i, colName);
			if (position == null || position.trim().equals("")
					|| position.trim().equalsIgnoreCase("null")) {
				continue;
			}


			UserProfileForTrafficL3 UserProfileForTrafficL3Item = new UserProfileForTrafficL3();
			if (!userprofileid.equals("") && !userprofileid.trim().equalsIgnoreCase("null")) {
				Long newUserprofile_id = AhRestoreNewMapTools.getMapUserProfile(AhRestoreCommons
						.convertLong(userprofileid.trim()));
				UserProfile userprofile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserprofile_id);
				UserProfileForTrafficL3Item.setUserProfile(userprofile);
			}

			UserProfileForTrafficL3Item.setVpnTunnelBehavior((short) AhRestoreCommons
					.convertLong(vpntunnelbehavior));
			UserProfileForTrafficL3Item.setPosition(AhRestoreCommons.convertInt(position));

			if (UserProfileForTrafficL3Info.get(vpn_userprofile_trafficl3_id) == null) {
				List<UserProfileForTrafficL3> d_routeList = new ArrayList<UserProfileForTrafficL3>();
				d_routeList.add(UserProfileForTrafficL3Item);
				UserProfileForTrafficL3Info.put(vpn_userprofile_trafficl3_id, d_routeList);
			} else {
				UserProfileForTrafficL3Info.get(vpn_userprofile_trafficl3_id).add(UserProfileForTrafficL3Item);
			}
		}
		return UserProfileForTrafficL3Info;
	}


	private static List<VpnService> getAllVpnServices() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of vpn_service.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in vpn_service table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<VpnService> vpnServiceInfo = new ArrayList<VpnService>();
		boolean isColPresent;
		String colName;
		VpnService vpnService;

		for (int i = 0; i < rowCount; i++) {
			vpnService = new VpnService();

			/**
			 * Set profilename
			 */
			colName = "profilename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'profilename' column is not exist.");
				/**
				 * The profilename column must be exist in the table of
				 * vpn_service
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'profilename' column value is null.");
				continue;
			}
			vpnService.setProfileName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of vpn_service
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			vpnService.setId(Long.valueOf(id));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			vpnService.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set capwapthroughtunnel
			 */
			colName = "capwapthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String capwapthroughtunnel = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setCapwapThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(capwapthroughtunnel));

			/**
			 * Set certificate
			 */
			colName = "certificate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String certificate = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			vpnService.setCertificate(AhRestoreCommons
					.convertString(certificate));
			/**
			 * Set clientippoolend1
			 */
			colName = "clientippoolend1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolend1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolEnd1(AhRestoreCommons
					.convertString(clientippoolend1));
			/**
			 * Set clientippoolend2
			 */
			colName = "clientippoolend2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolend2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolEnd2(AhRestoreCommons
					.convertString(clientippoolend2));
			/**
			 * Set clientippoolnetmask1
			 */
			colName = "clientippoolnetmask1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolnetmask1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolNetmask1(AhRestoreCommons
					.convertString(clientippoolnetmask1));
			/**
			 * Set clientippoolnetmask2
			 */
			colName = "clientippoolnetmask2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolnetmask2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolNetmask2(AhRestoreCommons
					.convertString(clientippoolnetmask2));
			/**
			 * Set clientippoolstart1
			 */
			colName = "clientippoolstart1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolstart1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolStart1(AhRestoreCommons
					.convertString(clientippoolstart1));
			/**
			 * Set clientippoolstart2
			 */
			colName = "clientippoolstart2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String clientippoolstart2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setClientIpPoolStart2(AhRestoreCommons
					.convertString(clientippoolstart2));

			/**
			 * Set loadbalance
			 */
			colName = "loadbalance";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String loadbalance = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			vpnService.setLoadBalance(AhRestoreCommons
					.convertStringToBoolean(loadbalance));

			/**
			 * Set ikevalidation
			 */
			colName = "ikevalidation";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ikevalidation = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setIkeValidation(AhRestoreCommons
					.convertStringToBoolean(ikevalidation));
			/**
			 * Set keepalive
			 */
			colName = "keepalive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String keepalive = isColPresent ? xmlParser.getColVal(i, colName)
					: "true";
			vpnService.setKeepAlive(AhRestoreCommons
					.convertStringToBoolean(keepalive));
			/**
			 * Set logthroughtunnel
			 */
			colName = "logthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String logthroughtunnel = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setLogThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(logthroughtunnel));
			/**
			 * Set nattraversal
			 */
			colName = "nattraversal";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String nattraversal = isColPresent ? xmlParser
					.getColVal(i, colName) : "true";
			vpnService.setNatTraversal(AhRestoreCommons
					.convertStringToBoolean(nattraversal));
			/**
			 * Set ntpthroughtunnel
			 */
			colName = "ntpthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ntpthroughtunnel = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setNtpThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(ntpthroughtunnel));

			/**
			 * Set dbtypeadthroughtunnel
			 */
			colName = "dbtypeadthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dbtypeadthroughtunnel = isColPresent ? xmlParser.getColVal(
					i, colName) : "false";
			vpnService.setDbTypeAdThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(dbtypeadthroughtunnel));

			/**
			 * Set dbtypeldapthroughtunnel
			 */
			colName = "dbtypeldapthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dbtypeldapthroughtunnel = isColPresent ? xmlParser
					.getColVal(i, colName) : "false";
			vpnService.setDbTypeLdapThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(dbtypeldapthroughtunnel));

			/**
			 * Set phase1authmethod
			 */
			colName = "phase1authmethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase1authmethod = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.PHASE1_AUTH_METHOD_HYBRID));
			vpnService.setPhase1AuthMethod((short) AhRestoreCommons
					.convertInt(phase1authmethod));
			/**
			 * Set serverikeid
			 */
			colName = "serverikeid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverikeid = (isColPresent ? xmlParser
					.getColVal(i, colName) : String
					.valueOf(VpnService.IKE_ID_ASN1DN));
			vpnService.setServerIkeId(AhRestoreCommons.convertInt(serverikeid));

			/**
			 * Set phase1dhgroup
			 */
			colName = "phase1dhgroup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase1dhgroup = (isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(VpnService.PHASE1_DH_GROUP_2));
			vpnService.setPhase1DhGroup((short) AhRestoreCommons
					.convertInt(phase1dhgroup));
			/**
			 * Set phase1encrypalg
			 */
			colName = "phase1encrypalg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase1encrypalg = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.PHASE1_ENCRYP_ALG_AES128));
			vpnService.setPhase1EncrypAlg((short) AhRestoreCommons
					.convertInt(phase1encrypalg));
			/**
			 * Set phase1hash
			 */
			colName = "phase1hash";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase1hash = (isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnService.PHASE1_HASH_SHA1));
			vpnService.setPhase1Hash((short) AhRestoreCommons
					.convertInt(phase1hash));
			/**
			 * Set phase1lifetime
			 */
			colName = "phase1lifetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase1lifetime = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.DEFAULT_PHASE1_LIFE_TIME));
			vpnService.setPhase1LifeTime(AhRestoreCommons
					.convertInt(phase1lifetime));
			/**
			 * Set phase2encrypalg
			 */
			colName = "phase2encrypalg";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase2encrypalg = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.PHASE2_ENCRYP_ALG_AES128));
			vpnService.setPhase2EncrypAlg((short) AhRestoreCommons
					.convertInt(phase2encrypalg));
			/**
			 * Set phase2hash
			 */
			colName = "phase2hash";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase2hash = (isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnService.PHASE2_HASH_SHA1));
			vpnService.setPhase2Hash((short) AhRestoreCommons
					.convertInt(phase2hash));
			/**
			 * Set phase2lifetime
			 */
			colName = "phase2lifetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase2lifetime = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.DEFAULT_PHASE2_LEFE_TIME));
			vpnService.setPhase2LifeTime(AhRestoreCommons
					.convertInt(phase2lifetime));
			/**
			 * Set phase2pfsgroup
			 */
			colName = "phase2pfsgroup";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String phase2pfsgroup = (isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(VpnService.PHASE2_PFS_GROUP_2));
			vpnService.setPhase2PfsGroup((short) AhRestoreCommons
					.convertInt(phase2pfsgroup));
			/**
			 * Set privatekey
			 */
			colName = "privatekey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String privatekey = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			vpnService
					.setPrivateKey(AhRestoreCommons.convertString(privatekey));
			/**
			 * Set radiusthroughtunnel
			 */
			colName = "radiusthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String radiusthroughtunnel = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setRadiusThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(radiusthroughtunnel));

			/**
			 * Set rootca
			 */
			colName = "rootca";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String rootca = isColPresent ? xmlParser.getColVal(i, colName) : "";
			vpnService.setRootCa(AhRestoreCommons.convertString(rootca));

			/**
			 * Set serverprivateip1
			 */
			colName = "serverprivateip1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverprivateip1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerPrivateIp1(AhRestoreCommons
					.convertString(serverprivateip1));
			/**
			 * Set serverprivateip2
			 */
			colName = "serverprivateip2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverprivateip2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerPrivateIp2(AhRestoreCommons
					.convertString(serverprivateip2));
			/**
			 * Set serverpublicip1
			 */
			colName = "serverpublicip1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverpublicip1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerPublicIp1(AhRestoreCommons
					.convertString(serverpublicip1));
			/**
			 * Set serverpublicip2
			 */
			colName = "serverpublicip2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverpublicip2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerPublicIp2(AhRestoreCommons
					.convertString(serverpublicip2));
			/**
			 * Set snmpthroughtunnel
			 */
			colName = "snmpthroughtunnel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String snmpthroughtunnel = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			vpnService.setSnmpThroughTunnel(AhRestoreCommons
					.convertStringToBoolean(snmpthroughtunnel));

			/**
			 * Set dpdidelinterval
			 */
			colName = "dpdidelinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dpdidelinterval = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.DEFAULT_DPD_IDEL_INTERVAL));
			vpnService.setDpdIdelInterval(AhRestoreCommons
					.convertInt(dpdidelinterval));

			/**
			 * Set dpdretry
			 */
			colName = "dpdretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dpdretry = (isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnService.DEFAULT_DPD_RETRY));
			vpnService.setDpdRetry(AhRestoreCommons.convertInt(dpdretry));

			/**
			 * Set dpdretryinterval
			 */
			colName = "dpdretryinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dpdretryinterval = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.DEFAULT_DPD_RETRY_INTERVAL));
			vpnService.setDpdRetryInterval(AhRestoreCommons
					.convertInt(dpdretryinterval));

			/**
			 * Set amrpinterval
			 */
			colName = "amrpinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String amrpinterval = (isColPresent ? xmlParser.getColVal(i,
					colName) : String.valueOf(VpnService.DEFAULT_AMRP_INTERVAL));
			vpnService.setAmrpInterval(AhRestoreCommons
					.convertInt(amrpinterval));

			/**
			 * Set amrpretry
			 */
			colName = "amrpretry";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String amrpretry = (isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(VpnService.DEFAULT_AMRP_RETRY));
			vpnService.setAmrpRetry(AhRestoreCommons.convertInt(amrpretry));

			/**
			 * Set dns_ip
			 */
			colName = "dns_ip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dns_ip = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (dns_ip != null && !(dns_ip.trim().equals(""))
					&& !(dns_ip.trim().equalsIgnoreCase("null"))) {
				Long dns_ip_new = AhRestoreNewMapTools
						.getMapIpAdddress(AhRestoreCommons.convertLong(dns_ip));
				if (null != dns_ip_new) {
					vpnService.setDnsIp(AhRestoreNewTools.CreateBoWithId(
							IpAddress.class, dns_ip_new));
				}
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			vpnService.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/*added for Cango*/

			/**
			 * Set serverdeaultgateway1
			 */
			colName = "serverdeaultgateway1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverdeaultgateway1 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerDeaultGateway1(AhRestoreCommons
					.convertString(serverdeaultgateway1));

			/**
			 * Set serverdeaultgateway1
			 */
			colName = "serverdeaultgateway2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String serverdeaultgateway2 = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			vpnService.setServerDeaultGateway2(AhRestoreCommons
					.convertString(serverdeaultgateway2));

			/**
			 * Set hiveapvpnserver1
			 */
			colName = "hiveapvpnserver1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String hiveapvpnserver1 = (isColPresent ? xmlParser.getColVal(i,
					colName) : "");
			vpnService.setHiveApVpnServer1(AhRestoreCommons
					.convertLong(hiveapvpnserver1));

			/**
			 * Set hiveapvpnserver2
			 */
			colName = "hiveapvpnserver2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String hiveapvpnserver2 = (isColPresent ? xmlParser.getColVal(i,
					colName) : "");
			vpnService.setHiveApVpnServer2(AhRestoreCommons
					.convertLong(hiveapvpnserver2));

			/**
			 * Set domainobj
			 */
			colName = "domainobject_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String domainobject_id = isColPresent ? xmlParser.getColVal(i, colName)
					: null;
			if (domainobject_id != null && !(domainobject_id.trim().equals(""))
					&& !(domainobject_id.trim().equalsIgnoreCase("null"))) {
				DomainObject domainObject = AhRestoreNewMapTools.getMapDomainObject(AhRestoreCommons.convertLong(domainobject_id));
			    try {
			    	domainObject = QueryUtil.findBoById(DomainObject.class, domainObject.getId(), new DomainObjectAction());

			    	if(domainObject.getObjType() != DomainObject.VPN_TUNNEL){
			    		domainObject.setId(null);
			    		domainObject.setObjType(DomainObject.VPN_TUNNEL);
			    		if(domainObject.getItems() != null){
			    			List<DomainNameItem> items = new ArrayList<DomainNameItem>();
							items.addAll(domainObject.getItems());
							domainObject.setItems(items);
				    	}
			    		QueryUtil.createBo(domainObject);
			    	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				vpnService.setDomObj(domainObject);
			}

			/**
			 * Set ipsecvpntype
			 */
			colName = "ipsecvpntype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ipsecvpntype = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.IPSEC_VPN_LAYER_2));
			vpnService.setIpsecVpnType((short) AhRestoreCommons
					.convertInt(ipsecvpntype));

			/**
			 * Set routetraffictype
			 */
			colName = "routetraffictype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String routetraffictype = (isColPresent ? xmlParser.getColVal(i,
					colName) : String
					.valueOf(VpnService.ROUTE_VPNTUNNEL_TRAFFIC_INTERNAL));
			vpnService.setRouteTrafficType((short) AhRestoreCommons
					.convertInt(routetraffictype));

			/**
			 * Set upgradeFlag
			 */
			colName = "upgradeFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String upgradeFlag = isColPresent ? xmlParser.getColVal(i,
					colName) : "true";
			vpnService.setUpgradeFlag(AhRestoreCommons
					.convertStringToBoolean(upgradeFlag));


			vpnServiceInfo.add(vpnService);
		}
		return vpnServiceInfo;
	}


	public static boolean restoreVpnServices() {
		try {
			long start = System.currentTimeMillis();

			List<VpnService> allVpnServices = getAllVpnServices();

			if (null == allVpnServices) {
				AhRestoreDBTools.logRestoreMsg("VPN Services is null");
			} else {
				Map<String, List<VpnServiceCredential>> credentialInfo = getAllCredentials();
				Map<String, List<VpnGatewaySetting>> vpnGateWaySettingInfo = getAllVpnGateWays();
				Map<String, List<UserProfileForTrafficL2>> UserProfileForTrafficL2Info = getUserProfileTrafficL2();
				Map<String, List<UserProfileForTrafficL3>> UserProfileForTrafficL3Info = getUserProfileTrafficL3();

				List<Long> oldIdList = new ArrayList<Long>(allVpnServices
						.size());
				for (VpnService vpnService : allVpnServices) {
					if (vpnService != null) {
						// set credentials
						if (null != credentialInfo) {
							List<VpnServiceCredential> list = credentialInfo
									.get(vpnService.getId().toString());
							if (null != list) {
								vpnService.setVpnCredentials(list);
							}
						}

						// set vpnGatewaySetting
						if(null != vpnGateWaySettingInfo){
							List<VpnGatewaySetting> list = vpnGateWaySettingInfo.get(vpnService.getId().toString());
							if(null != list){
						           Collections.sort(list,
					               new Comparator<VpnGatewaySetting>() {
					                  public int compare(VpnGatewaySetting gateWay1, VpnGatewaySetting gateWay2) {
					                         Integer id1 = gateWay1.getReorder();
					                         Integer id2 = gateWay2.getReorder();
					                         return id1.compareTo(id2);
					                  }
					               });
								vpnService.setVpnGateWaysSetting(list);
							}
						}
						// set vpn_userprofile_trafficl2
						if(null != UserProfileForTrafficL2Info){
							List<UserProfileForTrafficL2> list = UserProfileForTrafficL2Info.get(vpnService.getId().toString());
							if(null != list){
								vpnService.setUserProfileTrafficL2(list);
							}
						}
						// set vpn_userprofile_trafficl3
						if(null != UserProfileForTrafficL3Info){
							List<UserProfileForTrafficL3> list = UserProfileForTrafficL3Info.get(vpnService.getId().toString());
							if(null != list){
								vpnService.setUserProfileTrafficL3(list);
							}
						}

						oldIdList.add(vpnService.getId());
						vpnService.setId(null);// set id to null
					}
				}
				QueryUtil.restoreBulkCreateBos(allVpnServices);
				// set id mapping to map tool.
				for (int i = 0; i < allVpnServices.size(); i++) {
					AhRestoreNewMapTools.setMapVpnService(oldIdList.get(i),
							allVpnServices.get(i).getId());
				}
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore VPN Services completely. cost:"
							+ (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore VPN Services error.", e);
			return false;
		}
		return true;
	}

	public static void updateVpnGateWaySetting() {
		try {
			List<VpnService> bos =  (List<VpnService>) QueryUtil.executeQuery(VpnService.class,null,null,null,new VpnServiceAction());
			if (!bos.isEmpty()) {
				for (VpnService vpnService : bos) {
					for (VpnGatewaySetting vgs : vpnService.getVpnGateWaysSetting()) {
						if(vgs.getApId() != null && vgs.getApId() != -1){
							if(null != AhRestoreNewMapTools.getMapHiveAP(vgs.getApId())){
								vgs.setApId(AhRestoreNewMapTools.getMapHiveAP(vgs.getApId()));
							}
						}
					}
				}
				QueryUtil.bulkUpdateBos(bos);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore vpnservice vpngateway setting error.", e);
		}
	}

	public static void updateVpnServiceHiveApVpnServer(){
		try {
			List<VpnService> bos = QueryUtil.executeQuery(VpnService.class,null,null);
			if(bos.size() <=0) {
				AhRestoreDBTools.logRestoreMsg("Table "+"vpn_service"+" is not exist");
			}
			for (VpnService bo : bos) {
				Long id = bo.getId();
				if (null == id) {
					continue;
				}
				Long hiveApVpnServer1 = bo.getHiveApVpnServer1();
				Long hiveApVpnServer2 = bo.getHiveApVpnServer2();
				if (hiveApVpnServer1 != null && hiveApVpnServer1 != -1) {
					Long newHiveApVpnServer1 = AhRestoreNewMapTools.getMapHiveAP(hiveApVpnServer1);
					if (newHiveApVpnServer1 != null) {
						bo.setHiveApVpnServer1(newHiveApVpnServer1);
					}
				}
				if(hiveApVpnServer2 != null && hiveApVpnServer2 != -1){
					Long newHiveApVpnServer2 = AhRestoreNewMapTools.getMapHiveAP(hiveApVpnServer2);
					if (newHiveApVpnServer2 != null) {
						bo.setHiveApVpnServer2(newHiveApVpnServer2);
					}
				}
			}
			if(bos != null && !bos.isEmpty()){
				QueryUtil.bulkUpdateBos(bos);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			AhRestoreDBTools.logRestoreMsg("Restore vpnservice HiveApVpnServer setting error.", e);
		}
	}
}