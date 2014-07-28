package com.ah.ws.rest.client.utils;

import java.util.Collection;
import java.util.List;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.ws.rest.models.DeviceOptModel;
import com.ah.ws.rest.models.SerialNumberList;

public interface DeviceUtils {

	/**
	 * this function used for add device to redierctor
	 * @param apMappingListOk
	 * @param domain
	 * @param addToRedirector
	 */
	public List<SerialNumberList> addSerialNumbersToRedirector(List<String> apMappingListOk, HmDomain domain, boolean addToRedirector) throws Exception;
	
	/**
	 * this function used for add device to HM from redirector
	 * @param deviceList
	 */
	public List<DeviceOptModel> addSerialNumberToHm(List<DeviceOptModel> deviceList);

	/**
	 * this function used for add device to HM
	 * @param apMappingListOk
	 * @param domain
	 */
	public List<DeviceInventory> addSerialNumberToHm(List<String> apMappingListOk, HmDomain domain);

	
	/**
	 * this function used for add device to redierctor
	 * @param deviceList
	 */
	public List<DeviceOptModel> removeSerialNumbersFromRedirector(List<DeviceOptModel> deviceList);
	
	
	/**
	 * this function used for remove device from HM
	 * @param removeAps
	 * @param successRemovedLst
	 * @param generateLog
	 * @param domain
	 */
	public List<String> removeSerialNumberFromHm(List<HiveAp> removeAps, Collection<Long> successRemovedLst, boolean generateLog,HmDomain domain);
	
	/**
	 * this function used for sync device status from redierctor
	 * @param deviceList
	 */
	public List<DeviceOptModel> syncSerialNumbersFromRedirector(List<DeviceOptModel> deviceList);
	
	/**
	 * this function used for remove device from HM
	 * @param arg_Status
	 * @param arg_Comment
	 * @param domain
	 */
	public void generateAuditLog(short arg_Status, String arg_Comment, HmDomain domain);
	
	/**
	 * this function will automatically call RESTful API of Redirector to get list of device inventories
	 * @param domain
	 */
	public boolean syncDeviceInventoriesWithRedirector(HmDomain domain);
	
	/**
	 * this function will be used to update device inventories in DB according to device inventories passed in
	 * @param deviceInventories
	 * @param domain
	 */
	public boolean syncDeviceInventoriesWithRedirector(List<DeviceInventory> deviceInventories, HmDomain domain);
	
	public static final byte EXPORT_CSV_TYPE_SERIALNUMBER = 1;
	public static final byte EXPORT_CSV_TYPE_CONFIGURATION = 2;
	/**
	 * used to export device inventories in CSV file type, can be imported again with this file
	 * @param deviceInventories
	 * @param type
	 * @return
	 */
	public String getDeviceInventoryCSVString(List<DeviceInventory> deviceInventories, byte type, boolean easyMode);
	
}
