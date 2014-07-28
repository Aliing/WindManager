package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.ClientDeviceInfo;
/**
 * 
 * Description: restore client_device_info data
 * Todo: extensionBigInt,extensionTimeStamp,extensionText,extensionByteArray four extersion fields no restore
 * RestoreUserClientDevice.java Create on Oct 10, 2012 6:50:09 AM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2012 Aerohive Networks Inc. All Rights Reserved.
 */
public class RestoreUserClientDevice {
	public static final String clientDeviceInfoTableName = "client_device_info";
	
	 
	 public static boolean restoreClientDeviceInfos() {

	        try {
	            long start = System.currentTimeMillis();

	            List<ClientDeviceInfo> allClientDeviceInfos = getAllClientDeviceInfos();

	            if (null == allClientDeviceInfos || allClientDeviceInfos.isEmpty()) {
	                AhRestoreDBTools.logRestoreMsg("ClientDeviceInfo is null or empty.");
	            } else {
	                AhRestoreDBTools.logRestoreMsg("Restore ClientDeviceInfo size:" + allClientDeviceInfos.size());
	                QueryUtil.restoreBulkCreateBos(allClientDeviceInfos);
	            }
	            long end = System.currentTimeMillis();
	            AhRestoreDBTools.logRestoreMsg("Restore ClientDeviceInfo completely. Count:"
	                    + (null == allClientDeviceInfos ? "0" : allClientDeviceInfos.size()) + ", cost:" + (end - start) + " ms.");
	        } catch (Exception e) {
	            AhRestoreDBTools.logRestoreMsg("Restore ClientDeviceInfo error.", e);
	            return false;
	        }
	        return true;

	    }
	 
	 
	 private static List<ClientDeviceInfo> getAllClientDeviceInfos() throws AhRestoreException,
     				AhRestoreColNotExistException {
		 AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		
		 /**
		  * Check validation of client_device_info.xml
		  */
		 boolean restoreRet = xmlParser.readXMLFile(clientDeviceInfoTableName);
		 if (!restoreRet) {
		     return null;
		 }
		
		 /**
		  * No one row data stored in client_device_info table is allowed
		  */
		 int rowCount = xmlParser.getRowCount();
		 boolean isColPresent;
		 String colName;
		
		 List<ClientDeviceInfo> clientDeviceInfos = new ArrayList<ClientDeviceInfo>();
		 ClientDeviceInfo clientDeviceInfo;
		
		 for (int i = 0; i < rowCount; i++) {
		     clientDeviceInfo = new ClientDeviceInfo();
		
		     /**
		      * set MAC
		      */
		     colName = "mac";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     if (!isColPresent) {
		     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'client_device_info' data be lost, cause: 'mac' column is not exist.");
		         /**
		          * The mac column must be exist in the table.
		          */
		         continue;
		     }else{
		    	 String mac = AhRestoreCommons.convertString(xmlParser.getColVal(i,colName));
		    	 if("".equals(mac) || "null".equals(mac)){
		    		 BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'client_device_info' data be lost, cause: 'mac' column value is empty.");
			         continue;
		    	 }else{
		    		 clientDeviceInfo.setMAC(mac);
		    	 }
		    	 
		     }
		     /**
		      * set hostName
		      */
		     colName = "hostname";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     String hostName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
		     clientDeviceInfo.setHostName(hostName);
		     /**
		      * set OS_type
		      */
		     colName = "os_type";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     String OS_type = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
		     clientDeviceInfo.setOS_type(OS_type);
		     
		     /**
		      * set option55
		      */
		     colName = "option55";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     String option55 = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
		     clientDeviceInfo.setOption55(option55);
		     
		     /**
		      * set update_at
		      */
		     colName = "update_at";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     if (!isColPresent) {
		     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'client_device_info' data be lost, cause: 'update_at' column is not exist.");
		         /**
		          * The update_at column must be exist in the table.
		          */
		         continue;
		     }else{
		    	 String da = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
	    		 if("".equals(da) || "null".equals(da)){
	    			 BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'client_device_info' data be lost, cause: 'update_at' column value is empty.");
		    		 continue;
	    		 }else{
	    			 clientDeviceInfo.setUpdate_at(Long.parseLong(da));
	    		 }
		     }
		     /**
		      * Set owner
		      */
		     colName = "owner";
		     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, clientDeviceInfoTableName, colName);
		     long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
		             colName)) : 1;
		
		     if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
		     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'client_device_info' data be lost, cause: 'owner' column is not available.");
		         continue;
		     }
		
		     clientDeviceInfo.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
		
		     clientDeviceInfos.add(clientDeviceInfo);
		 }
		 return clientDeviceInfos;
		}
	 
}
