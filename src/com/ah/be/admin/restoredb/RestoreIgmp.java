package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.igmp.MulticastGroupInterface;
import com.ah.bo.mgmt.QueryUtil;

/**
 * 
 * Description: switch IGMP policy restore
 * RestoreIgmpPolicy.java Create on Nov 16, 2012 9:58:11 AM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2012 Aerohive Networks Inc. All Rights Reserved.
 */
public class RestoreIgmp {
	public static final String igmpPolicyTableName = "igmp_policy";
	public static final String multicastGroupTableName = "multicast_group";
	public static final String multicastGroupInterfaceTableName = "multicast_group_interface";
	
	
	// ------------------ restoreIgmpPolicy --------------
	public static boolean restoreIgmpPolicy() {
		try {
	
			List<IgmpPolicy> igmpPolicyList = getAllIgmpPolicy();
			if (null != igmpPolicyList) {
				QueryUtil.restoreBulkCreateBos(igmpPolicyList);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	// ------------------ restoreMulticastGroup --------------
	public static boolean restoreMulticastGroup() {
		try {

			List<MulticastGroup> multicastGroupList = getAllMulticastGroup();
			if (null != multicastGroupList) {
				List<Long> multicastGroupIds = new ArrayList<Long>();

				for (MulticastGroup multicastGroup : multicastGroupList) {
					multicastGroupIds.add(multicastGroup.getId());
				}

				QueryUtil.restoreBulkCreateBos(multicastGroupList);

				for(int i=0; i<multicastGroupList.size(); i++)
				{
					AhRestoreNewMapTools.setMapMulticastGroup(multicastGroupIds.get(i), multicastGroupList.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	// ------------------ restoreMulticastGroupInterface --------------
		public static boolean restoreMulticastGroupInterface() {
			try {

				List<MulticastGroupInterface> multicastGroupInterfaceList = getAllMulticastGroupInterface();
				if (null != multicastGroupInterfaceList) {
					QueryUtil.restoreBulkCreateBos(multicastGroupInterfaceList);
				}
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg(e.getMessage());
				return false;
			}
			return true;
		}
	
	private static List<IgmpPolicy> getAllIgmpPolicy() throws AhRestoreException,
	AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		/**
		 * Check validation of IGMP_POLICY.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(igmpPolicyTableName);
		if (!restoreRet) {
			return null;
		}
		
		/**
		 * No one row data stored in IGMP_POLICY table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<IgmpPolicy> igmpPolicyInfo = new ArrayList<IgmpPolicy>();
		boolean isColPresent;
		String colName;
		IgmpPolicy igmpPolicy;
		
		for (int i = 0; i < rowCount; i++) {
			igmpPolicy = new IgmpPolicy();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					igmpPolicyTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'igmp_policy' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			igmpPolicy.setId(Long.valueOf(id));
		
			/**
			 * Set vlanId
			 */
			colName = "vlanId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					igmpPolicyTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'igmp_policy' data be lost, cause: 'vlanId' column is not exist.");
				continue;
			}
			String vlanId = xmlParser.getColVal(i,colName);
			igmpPolicy.setVlanId(Integer.valueOf(vlanId));
			
			/**
			 * Set igmpSnooping
			 */
			colName = "igmpSnooping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					igmpPolicyTableName, colName);
			String igmpSnooping = isColPresent ? xmlParser.getColVal(i,colName) : "true";
			igmpPolicy.setIgmpSnooping(AhRestoreCommons.convertStringToBoolean(igmpSnooping));
			
			
			/**
			 * Set immediateLeave
			 */
			colName = "immediateLeave";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					igmpPolicyTableName, colName);
			String immediateLeave = isColPresent ? xmlParser.getColVal(i,colName) : "true";
			igmpPolicy.setImmediateLeave(AhRestoreCommons.convertStringToBoolean(immediateLeave));
			
			
			/**
			 * Set delayLeaveQueryInterval
			 */
			colName = "delayLeaveQueryInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, igmpPolicyTableName, colName);
			String delayLeaveQueryInterval = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			igmpPolicy.setDelayLeaveQueryInterval(Integer.valueOf(delayLeaveQueryInterval));
			/**
			 * Set delayLeaveQueryCount
			 */
			colName = "delayLeaveQueryCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, igmpPolicyTableName, colName);
			String delayLeaveQueryCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			igmpPolicy.setDelayLeaveQueryCount(Integer.valueOf(delayLeaveQueryCount));
			/**
			 * Set routerPortAginTime
			 */
			colName = "routerPortAginTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, igmpPolicyTableName, colName);
			String routerPortAginTime = isColPresent ? xmlParser.getColVal(i, colName) : "250";
			igmpPolicy.setRouterPortAginTime(Integer.valueOf(routerPortAginTime));
			/**
			 * Set robustnessCount
			 */
			colName = "robustnessCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, igmpPolicyTableName, colName);
			String robustnessCount = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			igmpPolicy.setRobustnessCount(Integer.valueOf(robustnessCount));
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					igmpPolicyTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'igmp_policy' data be lost, cause: 'owner' column is not available");
				continue;
			}
			igmpPolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			/**
			 * Set HIVE_AP_ID
			 */
			colName = "HIVE_AP_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,igmpPolicyTableName, colName);
			String hiveApId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			Long hiveApIdNew = AhRestoreNewMapTools.getMapHiveAP(AhRestoreCommons.convertLong(hiveApId.trim()));
			if (null != hiveApIdNew) {
				HiveAp hiveAp = AhRestoreNewTools.CreateBoWithId(HiveAp.class, hiveApIdNew);
				if(null != hiveAp){
					igmpPolicy.setHiveAp(hiveAp);
				}else{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'igmp_policy' data be lost, cause: 'hive_ap_id' column is not available");
					continue;
				}
			}else{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'igmp_policy' data be lost, cause: 'hive_ap_id' column is not available");
				continue;
			}
			igmpPolicyInfo.add(igmpPolicy);
		}
		return igmpPolicyInfo.isEmpty() ? null : igmpPolicyInfo;
	}
	
	private static List<MulticastGroup> getAllMulticastGroup() throws AhRestoreException,
	AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		/**
		 * Check validation of multicast_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(multicastGroupTableName);
		if (!restoreRet) {
			return null;
		}
		
		/**
		 * No one row data stored in multicast_group table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MulticastGroup> MulticastGroupInfo = new ArrayList<MulticastGroup>();
		boolean isColPresent;
		String colName;
		MulticastGroup multicastGroup;
		
		for (int i = 0; i < rowCount; i++) {
			multicastGroup = new MulticastGroup();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			multicastGroup.setId(Long.valueOf(id));
		
			/**
			 * Set vlanId
			 */
			colName = "vlanId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'vlanId' column is not exist.");
				continue;
			}
			String vlanId = xmlParser.getColVal(i,colName);
			multicastGroup.setVlanId(Integer.valueOf(vlanId));
			
			/**
			 * Set ipAddress
			 */
			colName = "ipAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'ipAddress' column is not exist.");
				continue;
			}
			String ipAddress = xmlParser.getColVal(i,colName);
			multicastGroup.setIpAddress(ipAddress);
			
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'owner' column is not available");
				continue;
			}
			multicastGroup.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			/**
			 * Set HIVE_AP_ID
			 */
			colName = "HIVE_AP_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,multicastGroupTableName, colName);
			String hiveApId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			Long hiveApIdNew = AhRestoreNewMapTools.getMapHiveAP(AhRestoreCommons.convertLong(hiveApId.trim()));
			if (null != hiveApIdNew) {
				HiveAp hiveAp = AhRestoreNewTools.CreateBoWithId(HiveAp.class, hiveApIdNew);
				if(null != hiveAp){
					multicastGroup.setHiveAp(hiveAp);
				}else{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'hive_ap_id' column is not available");
					continue;
				}
			}else{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group' data be lost, cause: 'hive_ap_id' column is not available");
				continue;
			}
			MulticastGroupInfo.add(multicastGroup);
		}
		return MulticastGroupInfo.isEmpty() ? null : MulticastGroupInfo;
	}
	
	private static List<MulticastGroupInterface> getAllMulticastGroupInterface() throws AhRestoreException,
	AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		/**
		 * Check validation of multicast_group_interface.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(multicastGroupInterfaceTableName);
		if (!restoreRet) {
			return null;
		}
		
		/**
		 * No one row data stored in multicast_group_interface table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MulticastGroupInterface> MulticastGroupInterfaceInfo = new ArrayList<MulticastGroupInterface>();
		boolean isColPresent;
		String colName;
		MulticastGroupInterface multicastGroupInterface;
		
		for (int i = 0; i < rowCount; i++) {
			multicastGroupInterface = new MulticastGroupInterface();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupInterfaceTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			multicastGroupInterface.setId(Long.valueOf(id));
		
			/**
			 * Set interfaceType
			 */
			colName = "interfaceType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupInterfaceTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'interfaceType' column is not exist.");
				continue;
			}
			String interfaceType = xmlParser.getColVal(i,colName);
			multicastGroupInterface.setInterfaceType(Short.valueOf(interfaceType));
			
			/**
			 * Set interfacePort
			 */
			colName = "interfacePort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupInterfaceTableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'interfacePort' column is not exist.");
				continue;
			}
			String interfacePort = xmlParser.getColVal(i,colName);
			multicastGroupInterface.setInterfacePort(Integer.valueOf(interfacePort));
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					multicastGroupInterfaceTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'owner' column is not available");
				continue;
			}
			multicastGroupInterface.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			/**
			 * Set multicastGroupId
			 */
			colName = "multicastGroupId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,multicastGroupInterfaceTableName, colName);
			String multicastGroupId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			Long multicastGroupIdNew = AhRestoreNewMapTools.getMapMulticastGroup(AhRestoreCommons.convertLong(multicastGroupId.trim()));
			if (null != multicastGroupIdNew) {
				MulticastGroup multicastGroup =  AhRestoreNewTools.CreateBoWithId(MulticastGroup.class, multicastGroupIdNew);
				if(null != multicastGroup){
					multicastGroupInterface.setMulticastGroup(multicastGroup);
				}else{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'multicastGroupId' column is not available");
					continue;
				}
			}else{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'multicast_group_interface' data be lost, cause: 'multicastGroupId' column is not available");
				continue;
			}
			MulticastGroupInterfaceInfo.add(multicastGroupInterface);
		}
		return MulticastGroupInterfaceInfo.isEmpty() ? null : MulticastGroupInterfaceInfo;
	}
	
	
}
