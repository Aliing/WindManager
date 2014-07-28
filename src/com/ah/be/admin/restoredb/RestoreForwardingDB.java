package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.ForwardingDB;
import com.ah.bo.hiveap.MacAddressLearningEntry;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Vlan;

public class RestoreForwardingDB {
	
	public static final String tableName = "forwarding_db";
	public static final String macAddressEntriesTableName = "static_macaddress_entries";
	public static final String selectedVlans = "forwarding_db_selected_vlans";
	
	private static List<ForwardingDB> getAllForwardingDB() throws AhRestoreException,
	AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		/**
		 * Check validation of forwarding_db.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}
		
		/**
		 * No one row data stored in forwarding_db table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ForwardingDB> forwardingDBInfo = new ArrayList<ForwardingDB>();
		boolean isColPresent;
		String colName;
		ForwardingDB forwardingDB;
		
		for (int i = 0; i < rowCount; i++) {
			forwardingDB = new ForwardingDB();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				//BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'id' column is not exist.");
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'forwarding_db' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of vpn_service
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			forwardingDB.setId(Long.valueOf(id));
		
			/**
			 * Set disableMacLearnForAllVlans
			 */
			colName = "disableMacLearnForAllVlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String disableMacLearnForAllVlans = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			forwardingDB.setDisableMacLearnForAllVlans(AhRestoreCommons
					.convertStringToBoolean(disableMacLearnForAllVlans));
			
			/**
			 * Set disableMacLearnForPartVlans
			 */
			colName = "disableMacLearnForPartVlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String disableMacLearnForPartVlans = isColPresent ? xmlParser.getColVal(i,
					colName) : "false";
			forwardingDB.setDisableMacLearnForPartVlans(AhRestoreCommons
					.convertStringToBoolean(disableMacLearnForPartVlans));
			
			/**
			 * set vlans
			 */
			colName = "vlans";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String vlans = isColPresent ? xmlParser.getColVal(i, colName) : "";
			forwardingDB.setVlans(vlans);
			
			
			colName = "idleTimeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String idleTimeout = isColPresent ? xmlParser.getColVal(i, colName) : "300";
			forwardingDB.setIdleTimeout(AhRestoreCommons.convertInt(idleTimeout));
		
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
				//BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'vpn_service' data be lost, cause: 'owner' column is not available.");
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'forwarding_db' data be lost, cause: 'owner' column is not available");
				continue;
			}
			forwardingDB.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
		
			forwardingDBInfo.add(forwardingDB);
		}
		return forwardingDBInfo;
	}
	
	private static Map<String, List<MacAddressLearningEntry>> getMacAddressLearingEntry()
			throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<MacAddressLearningEntry>> macAddressLearningEntryInfo = new HashMap<String, List<MacAddressLearningEntry>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of static_macaddress_entries.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(macAddressEntriesTableName);
		if (!restoreRet) {
			//AhRestoreDBTools.logRestoreMsg("SAXReader cannot read static_macaddress_entries.xml file.");
			BeLogTools.debug(HmLogConst.M_RESTORE, "SAXReader cannot read static_macaddress_entries.xml file.");
			return macAddressLearningEntryInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set MACADDRESS_ENTRIES_ID
			 */
			colName = "macaddress_entries_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macAddressEntriesTableName, colName);
			if (!isColPresent) {
				/**
				 * The macaddress_entries_id column must be exist in the table of
				 * static_macaddress_entries
				 */
				continue;
			}

			String macaddress_entries_id = xmlParser.getColVal(i, colName);
			if (macaddress_entries_id == null || macaddress_entries_id.trim().equals("")
					|| macaddress_entries_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set vlanId
			 */
			colName = "vlanId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macAddressEntriesTableName, colName);
			if (!isColPresent) {
				continue;
			}

			String vlanId = xmlParser.getColVal(i, colName);
			if (vlanId == null || vlanId.trim().equals("")
					|| vlanId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set deviceInfoConstant
			 */
			colName = "deviceInfoConstant";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macAddressEntriesTableName, colName);
			if (!isColPresent) {
				continue;
			}

			String ethInterface = xmlParser.getColVal(i, colName);
			if(ethInterface == null || ethInterface.trim().equals("")
					|| ethInterface.trim().equalsIgnoreCase("null")){
				continue;
			}

			/**
			 * Set macAddress
			 */
			colName = "macAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					macAddressEntriesTableName, colName);
			if (!isColPresent) {
				continue;
			}

			String macAddress = xmlParser.getColVal(i, colName);
			if (macAddress == null || macAddress.trim().equals("")
					|| macAddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			MacAddressLearningEntry macAddressLearningEntryItem = new MacAddressLearningEntry();
		
			/*Long new_vlanId = AhRestoreNewMapTools.getMapVlan(AhRestoreCommons
					.convertLong(vlanId.trim()));*/
			//Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,new_vlanId);
			//macAddressLearningEntryItem.setVlanProfile(vlan);
			macAddressLearningEntryItem.setVlanId(AhRestoreCommons.convertInt(vlanId));

			macAddressLearningEntryItem.setDeviceInfoConstant((short)AhRestoreCommons
					.convertInt(ethInterface));
			
			macAddressLearningEntryItem.setMacAddress(macAddress);

			if (macAddressLearningEntryInfo.get(macaddress_entries_id) == null) {
				List<MacAddressLearningEntry> d_routeList = new ArrayList<MacAddressLearningEntry>();
				d_routeList.add(macAddressLearningEntryItem);
				macAddressLearningEntryInfo.put(macaddress_entries_id, d_routeList);
			} else {
				macAddressLearningEntryInfo.get(macaddress_entries_id).add(macAddressLearningEntryItem);
			}
		}
		return macAddressLearningEntryInfo;
	}
	
	private static Map<String, Set<Vlan>> getAllSelectedVlans() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of FORWARDING_DB_SELECTED_VLANS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(selectedVlans);
		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<Vlan>> mapVlanInfo = new HashMap<String, Set<Vlan>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set FORWARDING_DB_ID
			 */
			colName = "forwarding_db_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					selectedVlans, colName);
			if (!isColPresent)
			{
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
				|| profileId.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			/**
			 * Set SELECT_VLAN_ID
			 */
			colName = "select_vlan_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					selectedVlans, colName);
			if (!isColPresent)
			{
			
				continue;
			}

			String select_vlan_id = xmlParser.getColVal(i, colName);
			if (select_vlan_id == null || select_vlan_id.trim().equals("")
				|| select_vlan_id.trim().equalsIgnoreCase("null"))
			{
				continue;
			}

			Long newVlanId = AhRestoreNewMapTools.getMapVlan(Long.parseLong(select_vlan_id.trim()));
			Vlan vlan = AhRestoreNewTools.CreateBoWithId(Vlan.class,newVlanId);
			if (vlan != null) {
				if (mapVlanInfo.get(profileId) == null) {
					Set<Vlan> vlanSet= new HashSet<Vlan>();
					vlanSet.add(vlan);
					mapVlanInfo.put(profileId, vlanSet);
				} else {
					mapVlanInfo.get(profileId).add(vlan);
				}
			}
		}

		return mapVlanInfo;
	}
	
	public static boolean restoreForwardingDB() {
		try {
			long start = System.currentTimeMillis();

			List<ForwardingDB> allForwardingDBs = getAllForwardingDB();

			if (null == allForwardingDBs) {
				//AhRestoreDBTools.logRestoreMsg("Forwarding DB is null");
				BeLogTools.info(HmLogConst.M_RESTORE, "Forwarding DB is null");
			} else {
				Map<String, Set<Vlan>> vlanInfo = getAllSelectedVlans();
				Map<String, List<MacAddressLearningEntry>> macAddressInfo = getMacAddressLearingEntry();
				

				List<Long> oldIdList = new ArrayList<Long>(allForwardingDBs
						.size());
				for (ForwardingDB fdb : allForwardingDBs) {
					if (fdb != null) {
						// set macAddressInfo
						if(null != macAddressInfo){
							List<MacAddressLearningEntry> list = macAddressInfo.get(fdb.getId().toString());
							if(null != list){
								fdb.setMacAddressEntries(list);
							}
						}

						oldIdList.add(fdb.getId());
						fdb.setId(null);// set id to null
					}
				}
				QueryUtil.restoreBulkCreateBos(allForwardingDBs);
				// set id mapping to map tool.
				for (int i = 0; i < allForwardingDBs.size(); i++) {
					AhRestoreNewMapTools.setMapForwardingDBMap(oldIdList.get(i),
							allForwardingDBs.get(i).getId());
				}
			}
			long end = System.currentTimeMillis();
			//AhRestoreDBTools.logRestoreMsg("Restore Forwarding DB completely. cost:"+ (end - start) + " ms.");
			BeLogTools.info(HmLogConst.M_RESTORE, "Restore Forwarding DB completely. cost:"+ (end - start) + " ms.");
		} catch (Exception e) {
			//AhRestoreDBTools.logRestoreMsg("Restore Forwarding DB error.", e);
			BeLogTools.error(HmLogConst.M_RESTORE, "Restore Forwarding DB error.", e);
			return false;
		}
		return true;
	}
}
