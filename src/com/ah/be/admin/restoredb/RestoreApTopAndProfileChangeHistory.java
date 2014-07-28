package com.ah.be.admin.restoredb;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.bo.HmBo;
import com.ah.bo.monitor.NetworkDeviceHistory;
import com.ah.util.JdbcUtil;
/**
 * 
 * Description: restore network_device_history,user_profiles_history data
 * Todo:extensionBigInt,extensionTimeStamp,extensionText,extensionByteArray four extersion fields no restore
 * RestoreApTopAndProfileChangeHistory.java Create on Oct 10, 2012 3:06:42 AM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2012 Aerohive Networks Inc. All Rights Reserved.
 */
public class RestoreApTopAndProfileChangeHistory {
	public static final String networkDeviceHistoryTableName = "network_device_history";
	
	public static final String insertNetworkDeviceHistoryString = "INSERT INTO network_device_history(mac,begintimestamp,milliseconds2gmt,networkpolicy,topologygroup, vlan,owner,endtimestamp,tags) VALUES (?,?,?,?,?,?,?,?,?)";
	
	
	 public static boolean restoreNetworkDeviceHistory() {

	        try {
	            long start = System.currentTimeMillis();

	            List<NetworkDeviceHistory> allNetworkDeviceHistory = getAllNetworkDeviceHistory();

	            if (null == allNetworkDeviceHistory || allNetworkDeviceHistory.isEmpty()) {
	                AhRestoreDBTools.logRestoreMsg("NetworkDeviceHistory is null or empty.");
	            } else {
	                AhRestoreDBTools.logRestoreMsg("Restore NetworkDeviceHistory size:" + allNetworkDeviceHistory.size());
	                List<Long> networkDeviceHistoryIds = new ArrayList<Long>();

					for (NetworkDeviceHistory networkDeviceHistory : allNetworkDeviceHistory) {
						networkDeviceHistoryIds.add(networkDeviceHistory.getId());
					}
	                retoreApTopNetworkProfile(allNetworkDeviceHistory);
	                for(int i=0; i<allNetworkDeviceHistory.size(); i++)
					{
						AhRestoreNewMapTools.setMapNetworkDeviceHistory(networkDeviceHistoryIds.get(i), allNetworkDeviceHistory.get(i).getId());
					}
	            }
	            long end = System.currentTimeMillis();
	            AhRestoreDBTools.logRestoreMsg("Restore NetworkDeviceHistory completely. Count:"
	                    + (null == allNetworkDeviceHistory ? "0" : allNetworkDeviceHistory.size()) + ", cost:" + (end - start) + " ms.");
	        } catch (Exception e) {
	            AhRestoreDBTools.logRestoreMsg("Restore NetworkDeviceHistory error.", e);
	            return false;
	        }
	        return true;

	    }
	 
	 private static List<NetworkDeviceHistory> getAllNetworkDeviceHistory() throws AhRestoreException,
					     AhRestoreColNotExistException {
					 AhRestoreGetXML xmlParser = new AhRestoreGetXML();
					
					 /**
					  * Check validation of network_device_history.xml
					  */
					 boolean restoreRet = xmlParser.readXMLFile(networkDeviceHistoryTableName);
					 if (!restoreRet) {
					     return null;
					 }
					
					 /**
					  * No one row data stored in network_device_history table is allowed
					  */
					 int rowCount = xmlParser.getRowCount();
					 boolean isColPresent;
					 String colName;
					
					 List<NetworkDeviceHistory> networkDeviceHistorys = new ArrayList<NetworkDeviceHistory>();
					 NetworkDeviceHistory networkDeviceHistory;
					
					 for (int i = 0; i < rowCount; i++) {
						 networkDeviceHistory = new NetworkDeviceHistory();
					
						 /**
					      * set MAC
					      */
					     colName = "mac";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if (!isColPresent) {
					     	BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'network_device_history' data be lost, cause: 'mac' column is not exist.");
					         /**
					          * The mac column must be exist in the table.
					          */
					         continue;
					     }else{
					    	 String mac = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(mac)){
					    		 networkDeviceHistory.setMAC(mac);
					    	 }else{
					    		 BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'network_device_history' data be lost, cause: 'mac' column value is empty.");
						         continue;
					    	 }
					     }
					     /**
					      * set beginTimeStamp
					      */
					     colName = "begintimestamp";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if (!isColPresent) {
					    	 networkDeviceHistory.setBeginTimeStamp(null);
					     }else{
					    	 String begintime = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(begintime)){
					    		 networkDeviceHistory.setBeginTimeStamp(str2timestamp(begintime));
					    	 }else{
					    		 networkDeviceHistory.setBeginTimeStamp(null);
					    	 }
					     }
					     /**
					      * set endTimeStamp
					      */
					     colName = "endtimestamp";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if (!isColPresent) {
					    	 networkDeviceHistory.setEndTimeStamp(null);
					     }else{
					    	 String endtime = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(endtime)){
					    		 networkDeviceHistory.setEndTimeStamp(str2timestamp(endtime));
					    	 }else{
					    		 networkDeviceHistory.setEndTimeStamp(null);
					    	 }
					     }
					     /**
					      * set networkPolicy
					      */
					     colName = "networkpolicy";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if (isColPresent) {
					    	 String networkPolicy = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if (networkPolicy != null && !(networkPolicy.trim().equals(""))
										&& !(networkPolicy.trim().equalsIgnoreCase("null"))) {
									Long template_id_new = AhRestoreNewMapTools
											.getMapConfigTemplate(AhRestoreCommons
													.convertLong(networkPolicy));
									if (null != template_id_new) {
										networkDeviceHistory.setNetworkPolicy(template_id_new);
									} else {
										BeLogTools.debug(HmLogConst.M_RESTORE,"Cound not find the new config networkPolicy mapping to old networkPolicy:"
														+ networkPolicy);
									}
								}
					     }
					     
					     /**
					      * set milliSeconds2GMT
					      */
					     colName = "milliseconds2gmt";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if (isColPresent) {
					    	 String milliSeconds2GMT = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(milliSeconds2GMT)){
					    		 networkDeviceHistory.setMilliSeconds2GMT(AhRestoreCommons.convertInt(milliSeconds2GMT));
					    	 }
					     }
					     /**
					      * set vLAN
					      */
					     colName = "vlan";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if(! isColPresent){
					    	 networkDeviceHistory.setvLAN(null);
					     }else{
					    	 String vlan = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(vlan)){
					    		 String s = vlan.substring(1).substring(0,vlan.length()-2);
						    	 Short[] vlans = new Short[]{};
						    	 String[] ss = s.split(",") ;
						    	 if(null != ss && ss.length > 0){
						    		 vlans = new Short[ss.length];
						    		 for(int j=0; j<ss.length; j++){
						    			 vlans[j] = Short.parseShort(ss[j]);
							    	 }
						    	 }
						    	 networkDeviceHistory.setvLAN(vlans);
					    	 }else{
					    		 networkDeviceHistory.setvLAN(null);
					    	 }
					     }
					     /**
					      * set tags
					      */
					     colName = "tags";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if(! isColPresent){
					    	 networkDeviceHistory.setTags(null);
					     }else{
					    	 String tags = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(tags)){
					    		 String s = tags.substring(1).substring(0,tags.length()-2);
						    	 String[] ss = s.split(",") ;
						    	 networkDeviceHistory.setTags(ss);
					    	 }else{
					    		 networkDeviceHistory.setTags(null);
					    	 }
					     }
					     /**
					      * set topologyGroup
					      */
					     colName = "topologygroup";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     if(! isColPresent){
					    	 networkDeviceHistory.setTopologyGroup(null);
					     }else{
					    	 String topologygroup = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
					    	 if(!"".equals(topologygroup)){
					    		 String s = topologygroup.substring(1).substring(0,topologygroup.length()-2);
						    	 Long[] topologygroups = new Long[]{};
						    	 String[] ss = s.split(",") ;
						    	 if(null != ss && ss.length > 0){
						    		 topologygroups = new Long[ss.length];
						    		 for(int j=0; j<ss.length; j++){
						    			 if (ss[j] != null && !(ss[j].trim().equals(""))
													&& !(ss[j].trim().equalsIgnoreCase("null"))) {
												Long map_id_new = AhRestoreNewMapTools.getMapMapContainer(AhRestoreCommons
																.convertLong(ss[j]));
												if (null != map_id_new) {
													topologygroups[j] = map_id_new;
												} else {
													BeLogTools.debug(HmLogConst.M_RESTORE,"Cound not find the new topologygroup mapping to old topologygroup:"
																	+ ss[j]);
												}
											}
							    	 }
						    	 }
						    	 networkDeviceHistory.setTopologyGroup(topologygroups);
					    	 }else{
					    		 networkDeviceHistory.setTopologyGroup(null);
					    	 }
					     }
					     /**
					      * Set owner
					      */
					     colName = "owner";
					     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, networkDeviceHistoryTableName, colName);
					     long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					             colName)) : 1;
					
					     if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					     	BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'network_device_history' data be lost, cause: 'owner' column is not available.");
					         continue;
					     }
					
					     networkDeviceHistory.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
					
					     networkDeviceHistorys.add(networkDeviceHistory);
					 }
					 return networkDeviceHistorys;
				}
	 
	 
	 private static void retoreApTopNetworkProfile(Collection<? extends HmBo> hmBos){
			ResultSet rsTable = null;
			Statement stTable = null;
			Connection conTable = null;
			PreparedStatement insertNetworkDeviceHistory = null;
			conTable = NetworkDeviceConfigTracking.initConn();
			try{
				insertNetworkDeviceHistory = conTable.prepareStatement(insertNetworkDeviceHistoryString);
				if(null != hmBos && !hmBos.isEmpty()){
					for (HmBo bo : hmBos) {
						if ( bo instanceof NetworkDeviceHistory){
							NetworkDeviceHistory networkDeviceHistory = (NetworkDeviceHistory)bo;
							JdbcUtil.updateOrInsert(insertNetworkDeviceHistory,new Object[]{
									networkDeviceHistory.getMAC(),networkDeviceHistory.getBeginTimeStamp(),networkDeviceHistory.getMilliSeconds2GMT(),
									networkDeviceHistory.getNetworkPolicy(),arrayCovert(networkDeviceHistory.getTopologyGroup(),conTable),
									arrayCovert(networkDeviceHistory.getvLAN(),conTable),networkDeviceHistory.getOwner().getId(),
									networkDeviceHistory.getEndTimeStamp(),arrayCovert(networkDeviceHistory.getTags(),conTable)
									});
						}
					}
				}
			}catch (Exception ex) {
				BeLogTools.debug(BeLogTools.ERROR, ex.getMessage(),ex);
			}
			finally
			{
				NetworkDeviceConfigTracking.free(rsTable, stTable);
				NetworkDeviceConfigTracking.freePSMT(insertNetworkDeviceHistory);
				NetworkDeviceConfigTracking.freeCon(conTable);
			}
		}
		
	public static Array arrayCovert(Object[] object,Connection connection){
			Array param = null;
			if (object instanceof java.lang.String[]){
				if (object.length > 0) {
					try {
						param = connection.createArrayOf("varchar",object);
					} catch (SQLException e) {
						AhRestoreDBTools.logRestoreMsg("array covert failure: "+e.getMessage());
					}
			    }
			}else if(object instanceof Long[]){
				if (object.length > 0) {
					try {
						param = connection.createArrayOf("bigint",object);
					} catch (SQLException e) {
						AhRestoreDBTools.logRestoreMsg("array covert failure: "+e.getMessage());
					}
			    }
			}else if(object instanceof Short[]){
		    	if (object.length > 0) {
		    		try {
						param = connection.createArrayOf("smallint",object);
					} catch (SQLException e) {
						AhRestoreDBTools.logRestoreMsg("array covert failure: "+e.getMessage());
					}
			    }
			}
			return param;
		}
		
	private static Timestamp str2timestamp(String datestr){
		 if(null == datestr || "".equals(datestr)){
			 return null;
		 }
		 Timestamp timestamp = null;
		 final String DEFAULT_DATE_FORMAT_Mill = "yyyy-MM-dd HH:mm:ss.SSS";
		 final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		 SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT_Mill);
		 Date date = null;
		 try{
			 date = sdf.parse(datestr);
			 timestamp = new Timestamp(date.getTime());
		 }catch(ParseException e){
			 sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			 try {
				date = sdf.parse(datestr);
				timestamp = new Timestamp(date.getTime());
			} catch (ParseException e1) {
				return null;
			}
		 }
		  
		 return timestamp;
	 }
}
