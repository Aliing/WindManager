package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.DeviceResetConfig;
import com.ah.bo.mgmt.QueryUtil;
/*
 * modification history
 *
 * support restoration for VHM
 * joseph chen, 05/04/2008
 */

public class RestoreAdditionalBo {

	private static List<DeviceResetConfig> getAllDeviceResetConfig() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		String restoreTableName = "device_resetconfig";
		
		/**
		 * Check validation of device_resetconfig.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(restoreTableName);
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in device_resetconfig table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DeviceResetConfig> deviceResetConfigInfo = new ArrayList<DeviceResetConfig>();
		boolean isColPresent;
		String colName;
		DeviceResetConfig deviceResetConfigDTO;

		for (int i = 0; i < rowCount; i++)
		{
			deviceResetConfigDTO = new DeviceResetConfig();

			/**
			 * Set serialNumber
			 */
			colName = "serialNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String serialNumber = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (serialNumber == null || serialNumber.trim().equals("")
					|| serialNumber.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + restoreTableName + "' data be lost, cause: 'serialNumber' column value is null.");
				continue;
			}

			deviceResetConfigDTO.setSerialNumber(AhRestoreCommons.convertString(serialNumber));

			/**
			 * Set timestamp
			 */
			colName = "timestamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String timestamp = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(System.currentTimeMillis());
			deviceResetConfigDTO.setTimestamp(AhRestoreCommons.convertString2Long(timestamp));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + restoreTableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			deviceResetConfigDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			deviceResetConfigInfo.add(deviceResetConfigDTO);

		}

		return deviceResetConfigInfo;
	}

	public static boolean restoreDeviceResetConfig()
	{
		try
		{
			List<DeviceResetConfig> allConfig = getAllDeviceResetConfig();
			if(null == allConfig) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(allConfig);
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static List<DeviceInventory> getAllDeviceInventory() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		String restoreTableName = "device_inventory";
		
		/**
		 * Check validation of device_inventory.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(restoreTableName);
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in device_inventory table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<DeviceInventory> deviceInventoryInfo = new ArrayList<DeviceInventory>();
		boolean isColPresent;
		String colName;
		DeviceInventory deviceInventoryDTO;

		for (int i = 0; i < rowCount; i++)
		{
			deviceInventoryDTO = new DeviceInventory();

			/**
			 * Set serialNumber
			 */
			colName = "serialNumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String serialNumber = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (serialNumber == null || serialNumber.trim().equals("")
					|| serialNumber.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + restoreTableName + "' data be lost, cause: 'serialNumber' column value is null.");
				continue;
			}

			deviceInventoryDTO.setSerialNumber(AhRestoreCommons.convertString(serialNumber));

			/**
			 * Set macAddress
			 */
			colName = "macAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String macAddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			deviceInventoryDTO.setMacAddress(AhRestoreCommons.convertString(macAddress));
			
			/**
			 * Set hostName
			 */
			colName = "hostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String hostName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			deviceInventoryDTO.setHostName(AhRestoreCommons.convertString(hostName));
			
			/**
			 * Set connectStatus
			 */
			colName = "connectStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String connectStatus = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			deviceInventoryDTO.setConnectStatus((short)AhRestoreCommons.convertInt(connectStatus));
			
			/**
			 * Set timestamp
			 */
			colName = "timestamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			String timestamp = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(System.currentTimeMillis());
			deviceInventoryDTO.setTimestamp(AhRestoreCommons.convertString2Long(timestamp));

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, restoreTableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '" + restoreTableName + "' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			deviceInventoryDTO.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			deviceInventoryInfo.add(deviceInventoryDTO);

		}

		return deviceInventoryInfo;
	}

	public static boolean restoreDeviceInventory()
	{
		try
		{
			List<DeviceInventory> allConfig = getAllDeviceInventory();
			if(null == allConfig) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(allConfig);
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}


}