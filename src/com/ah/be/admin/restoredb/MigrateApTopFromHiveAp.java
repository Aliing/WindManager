package com.ah.be.admin.restoredb;

import java.io.File;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking.InterfaceType;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking.VlanObj;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.JdbcUtil;

/**
 * 
 * Description:Migrate all aps' topology data from hive_ap table to network_device_history table
 * NetworkDeviceConfigTracking.java Create on Oct 31, 2012 10:44:41 PM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2012 Aerohive Networks Inc. All Rights Reserved.
 */
public class MigrateApTopFromHiveAp {

	/**
	 * Description:
	 * Date:Oct 31, 2012
	 * @author Shaohua Zhou
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		System.out.println("migrate ap topology start......");
		if (!AhRestoreGetXML.checkXMLFileExist("network_device_history") && 
				NetworkDeviceConfigTracking.checkDBTableExist("network_device_history")) {
			boolean bol = migrateHiveApTopology();
			if(bol){
				System.out.println("migrate success...");
			}
		}
		System.out.println("migrate ap topology end......");
	}
	
	public static boolean migrateHiveApTopology(){
		boolean migrateFinishFlag = true;
		List<HmDomain> domains = QueryUtil.executeQuery(HmDomain.class, null, null);
		List<Long> allExistTopOwners = getExistDomainsFromNetworkApTop();
		if(null != allExistTopOwners && !allExistTopOwners.isEmpty()){
			if(allExistTopOwners.get(0).longValue() == -1){
				migrateFinishFlag = false;
				BeLogTools.error(HmLogConst.M_RESTORE,"migrate ap topology data into network_device_history table failure.");
			}else{
				migrateFinishFlag = moveVHMtoHHM(domains,allExistTopOwners);
			}
		}else{
			migrateFinishFlag = upgradeHMorHHM(domains);
		}
		return migrateFinishFlag;
	}
	/**
	 * Description:move vhm from 5.1rx to 6.0r2 between HMOL.
	 * Date:Apr 19, 2013
	 * @author Shaohua Zhou
	 * @param domains
	 * @return 
	 * @return boolean
	 */
	private static boolean moveVHMtoHHM(List<HmDomain> domains, List<Long> allExistTopOwners){
		String queryAp = "SELECT map_container_id,classificationTag1,classificationTag2,classificationTag3,macAddress,template_id,timezoneoffset FROM hive_ap WHERE owner = ?";
		String insertString = "INSERT INTO network_device_history(mac,begintimestamp,milliseconds2gmt,networkpolicy,owner,topologygroup,vLAN,tags)VALUES (?,?,?,?,?,?,?,?)";
		String getDefaultMapsql = "select id from map_node where parent_map_id = (select id from map_node where parent_map_id is null) and owner = ?";
		PreparedStatement insert = null;
		PreparedStatement queryAps = null;
		PreparedStatement queryDefaultMaps = null;
		ResultSet rs_ap = null;
		ResultSet rs_map = null;
		Connection connection = null;
		
		boolean flag = true;
		List<Long> allDomainIds = new ArrayList<Long>();
		Map<Long,String> map = new HashMap<Long,String>();
		for(HmDomain hd: domains){
			allDomainIds.add(hd.getId());
			map.put(hd.getId(), hd.getDomainName());
		}
		boolean bol = allDomainIds.removeAll(allExistTopOwners);
		if(bol){
			if(null != allDomainIds && !allDomainIds.isEmpty()){
				try{
				connection = NetworkDeviceConfigTracking.initConn();
				insert = connection.prepareStatement(insertString);
				queryAps = connection.prepareStatement(queryAp);
				queryDefaultMaps = connection.prepareStatement(getDefaultMapsql);
				for(Long owner : allDomainIds){
					long domain_id = owner;
					String domain_name = map.get(owner);
					try
					{
						if(null == connection){
							connection = NetworkDeviceConfigTracking.initConn();
							insert = connection.prepareStatement(insertString);
							queryAps = connection.prepareStatement(queryAp);
							queryDefaultMaps = connection.prepareStatement(getDefaultMapsql);
						}
							rs_ap = null;
							rs_ap = JdbcUtil.query(queryAps,new Object[]{domain_id});
							ResultSetMetaData rsmd_ap = rs_ap.getMetaData();
							int iCount_ap = rsmd_ap.getColumnCount();
							while(rs_ap.next()){
								long mapId = -1;
								String tag1 = "";
								String tag2 = "";
								String tag3 = "";
								String mac = "";
								long templateId = -1;
								byte ap_timezone = 0;
								try{
									for(int i=1; i<=iCount_ap; i++){
										String newStr  = rs_ap.getString(i);
										if ("map_container_id".equalsIgnoreCase(rsmd_ap.getColumnName(i))) {
											if(null != newStr && !"".equals(newStr)){
												mapId = Long.parseLong(newStr);
											}
										}else if("classificationTag1".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
											tag1 = newStr;
										}else if("classificationTag2".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
											tag2 = newStr;
										}else if ("classificationTag3".equalsIgnoreCase(rsmd_ap.getColumnName(i))) {
											tag3 = newStr;
										}else if("macAddress".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
											mac = newStr;
										}else if("template_id".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
											if(null != newStr && !"".equals(newStr)){
												templateId = Long.parseLong(newStr);
											}
										}else if("timezoneoffset".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
											if(null != newStr && !"".equals(newStr)){
												ap_timezone = Byte.parseByte(newStr);
											}
										}
									}
									Array topologygroupArray = null;
									if(mapId != -1){
										topologygroupArray = getApTopology(mapId,connection);
									}else{
										rs_map = null;
										rs_map = JdbcUtil.query(queryDefaultMaps,new Object[]{domain_id});
										ResultSetMetaData rsmd_map = rs_map.getMetaData();
										int iCount_map = rsmd_map.getColumnCount();
										long map_id = -1;
										while(rs_map.next()){
											for(int icol=1; icol<=iCount_map; icol++){
												String newStr  = rs_map.getString(icol);
												if ("id".equalsIgnoreCase(rsmd_map.getColumnName(icol))) {
													map_id = Long.parseLong(newStr);
												}
											}
											if(map_id != -1){
												break;
											}
										}
										if(map_id != -1){
											topologygroupArray = getApTopology(map_id,connection);
										}
									}
									String[] tags = null;
									List<String> tagsStr = new ArrayList<String>();
									if(null != tag1 && !"".equals(tag1)){
										tagsStr.add(tag1);
									}
									if(null != tag2 && !"".equals(tag2)){
										tagsStr.add(tag2);
									}
									if(null != tag3 && !"".equals(tag3)){
										tagsStr.add(tag3);
									}
									if(null != tagsStr && tagsStr.size() > 0){
										tags = new String[tagsStr.size()];
										tagsStr.toArray(tags);
									}
									if(null != topologygroupArray && null != mac && !"".equals(mac) && templateId != -1){
										Timestamp startTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
										//remove device vlan info
//										VlanObj[] vlans = getVlans(domain_name,mac);
										boolean bool = JdbcUtil.updateOrInsert(insert,new Object[]{mac,startTime,ap_timezone,templateId,domain_id,topologygroupArray,null,arrayCovert(tags,connection)});
										if(!bool){
											BeLogTools.error(HmLogConst.M_RESTORE,"move vhm migrate ["+domain_name+"] hive_ap ["+mac+"] data into network_device_history table failure.");
										}
									}
								}catch(Exception e){
									BeLogTools.error(HmLogConst.M_RESTORE,"migrate vhm ["+domain_name+"] ["+mac+"] ap data into network_device_history table exception: "+e.getMessage(),e);
									continue;
								}
							}
					}	
					catch(Exception ex)
					{
						flag = false; 
						BeLogTools.error(HmLogConst.M_RESTORE,"migrate ["+domain_name+"] hive_ap data into network_device_history table exception: "+ex.getMessage(),ex);
						return flag; 
					}finally
					{
						NetworkDeviceConfigTracking.free(rs_ap, null);
						NetworkDeviceConfigTracking.free(rs_map, null);
					}
				 }
				}catch(Exception e){
					flag = false;
					BeLogTools.error(HmLogConst.M_RESTORE,"migrate ap topology data into network_device_history table exception: "+e.getMessage(),e);
					return flag; 
				}finally{
					NetworkDeviceConfigTracking.freePSMT(queryAps);
					NetworkDeviceConfigTracking.freePSMT(queryDefaultMaps);
					NetworkDeviceConfigTracking.freePSMT(insert);
					NetworkDeviceConfigTracking.freeCon(connection);
				}
			}else{
				BeLogTools.error(HmLogConst.M_RESTORE,"migrate ap topology data into network_device_history table, have no need domain.");
			}
		}else{
			flag = false;
			BeLogTools.error(HmLogConst.M_RESTORE,"migrate ap topology data into network_device_history table failure,remove collection failure.");
		}
		return flag;
	}
	/**
	 * 
	 * Description: for moving vhms from 5.1rx hmol to 6.0r2 hmol, get exist owners from 6.0r2 hmol.
	 * Date:Apr 18, 2013
	 * @author Shaohua Zhou
	 * @return List<Long> 
	 */
	private static List<Long> getExistDomainsFromNetworkApTop(){
		List<Long> list = new ArrayList<Long>();
		String queryExistApOwnerSql = "select distinct owner from network_device_history";
		try{
			List<?> apTopList = QueryUtil.executeNativeQuery(queryExistApOwnerSql);
			if(null != apTopList && !apTopList.isEmpty()){
				for(Object oneObj: apTopList){
					Long domain_id = ((BigInteger) oneObj).longValue();
					list.add(domain_id);
				}
			}
		}catch(Exception e){
			BeLogTools.error(HmLogConst.M_RESTORE,"migrate query owner from network_device_history table exception: "+e.getMessage(),e);
			list.add(-1L);
			return list;
		}
		return list;
	}
	/**
	 * Description: migrate ap topology data when upgrade hm or hmol from 5.1rx to 6.0r2
	 * Date:Apr 19, 2013
	 * @author Shaohua Zhou
	 * @param domains
	 * @return boolean
	 */
	private static boolean upgradeHMorHHM(List<HmDomain> domains){
		boolean flag = true;    
		String queryAp = "SELECT map_container_id,classificationTag1,classificationTag2,classificationTag3,macAddress,template_id,timezoneoffset FROM hive_ap WHERE owner = ?";
		String insertString = "INSERT INTO network_device_history(mac,begintimestamp,milliseconds2gmt,networkpolicy,owner,topologygroup,vLAN,tags)VALUES (?,?,?,?,?,?,?,?)";
		String getDefaultMapsql = "select id from map_node where parent_map_id = (select id from map_node where parent_map_id is null) and owner = ?";
		PreparedStatement insert = null;
		PreparedStatement queryAps = null;
		PreparedStatement queryDefaultMaps = null;
		ResultSet rs_ap = null;
		ResultSet rs_map = null;
		Connection connection = null;
		try
		{
			connection = NetworkDeviceConfigTracking.initConn();
			insert = connection.prepareStatement(insertString);
			queryAps = connection.prepareStatement(queryAp);
			queryDefaultMaps = connection.prepareStatement(getDefaultMapsql);
			for(HmDomain hd :domains){
				if(null == connection){
					connection = NetworkDeviceConfigTracking.initConn();
					insert = connection.prepareStatement(insertString);
					queryAps = connection.prepareStatement(queryAp);
					queryDefaultMaps = connection.prepareStatement(getDefaultMapsql);
				}
				long domain_id = hd.getId();
				String domain_name = hd.getDomainName();
				rs_ap = null;
				rs_ap = JdbcUtil.query(queryAps,new Object[]{domain_id});
				ResultSetMetaData rsmd_ap = rs_ap.getMetaData();
				int iCount_ap = rsmd_ap.getColumnCount();
				while(rs_ap.next()){
					long mapId = -1;
					String tag1 = "";
					String tag2 = "";
					String tag3 = "";
					String mac = "";
					long templateId = -1;
					byte ap_timezone = 0;
					try{
						for(int i=1; i<=iCount_ap; i++){
							String newStr  = rs_ap.getString(i);
							if ("map_container_id".equalsIgnoreCase(rsmd_ap.getColumnName(i))) {
								if(null != newStr && !"".equals(newStr)){
									mapId = Long.parseLong(newStr);
								}
							}else if("classificationTag1".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								tag1 = newStr;
							}else if("classificationTag2".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								tag2 = newStr;
							}else if ("classificationTag3".equalsIgnoreCase(rsmd_ap.getColumnName(i))) {
								tag3 = newStr;
							}else if("macAddress".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								mac = newStr;
							}else if("template_id".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								if(null != newStr && !"".equals(newStr)){
									templateId = Long.parseLong(newStr);
								}
							}else if("timezoneoffset".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								if(null != newStr && !"".equals(newStr)){
									ap_timezone = Byte.parseByte(newStr);
								}
							}
						}
						Array topologygroupArray = null;
						if(mapId != -1){
							topologygroupArray = getApTopology(mapId,connection);
						}else{
							rs_map = null;
							rs_map = JdbcUtil.query(queryDefaultMaps,new Object[]{domain_id});
							ResultSetMetaData rsmd_map = rs_map.getMetaData();
							int iCount_map = rsmd_map.getColumnCount();
							long map_id = -1;
							while(rs_map.next()){
								for(int icol=1; icol<=iCount_map; icol++){
									String newStr  = rs_map.getString(icol);
									if ("id".equalsIgnoreCase(rsmd_map.getColumnName(icol))) {
										map_id = Long.parseLong(newStr);
									}
								}
								if(map_id != -1){
									break;
								}
							}
							if(map_id != -1){
								topologygroupArray = getApTopology(map_id,connection);
							}
						}
						String[] tags = null;
						List<String> tagsStr = new ArrayList<String>();
						if(null != tag1 && !"".equals(tag1)){
							tagsStr.add(tag1);
						}
						if(null != tag2 && !"".equals(tag2)){
							tagsStr.add(tag2);
						}
						if(null != tag3 && !"".equals(tag3)){
							tagsStr.add(tag3);
						}
						if(null != tagsStr && tagsStr.size() > 0){
							tags = new String[tagsStr.size()];
							tagsStr.toArray(tags);
						}
						if(null != topologygroupArray && null != mac && !"".equals(mac) && templateId != -1){
							Timestamp startTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
							//remove device vlan info
//							VlanObj[] vlans = getVlans(domain_name,mac);
							boolean bool = JdbcUtil.updateOrInsert(insert,new Object[]{mac,startTime,ap_timezone,templateId,domain_id,topologygroupArray,null,arrayCovert(tags,connection)});
							if(!bool){
								BeLogTools.error(HmLogConst.M_RESTORE,"upgrade migrate ["+domain_name+"] hive_ap ["+mac+"] data into network_device_history table failure.");
							}
						}
					}catch(Exception e){
						BeLogTools.error(HmLogConst.M_RESTORE,"upgrade hm or hmol,migrate ["+domain_name+"] ["+mac+"] ap data into network_device_history table exception: "+e.getMessage(),e);
						continue;
					}	
				}
		    }
		}	
		catch(Exception ex)
		{
			flag = false; 
			BeLogTools.error(HmLogConst.M_RESTORE,"upgrade hm or hmol,migrate hive_ap data into network_device_history table exception: "+ex.getMessage(),ex);
			return flag; 
		}finally
		{
			NetworkDeviceConfigTracking.free(rs_ap, null);
			NetworkDeviceConfigTracking.free(rs_map, null);
			NetworkDeviceConfigTracking.freePSMT(queryAps);
			NetworkDeviceConfigTracking.freePSMT(queryDefaultMaps);
			NetworkDeviceConfigTracking.freePSMT(insert);
			NetworkDeviceConfigTracking.freeCon(connection);
		}
		return flag; 
	}
	
	
	public static VlanObj[] getVlans(String domainName, String mac){
		VlanObj[] vLanIds = null;
		String newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(domainName,
				mac);
		SAXReader saxReader = new SAXReader();
		Document cfgDocument;
		try {
			cfgDocument = saxReader.read(new File(newXmlCfgPath));
			String vlanXpath ="//vlan";
			List<?> vlanList = cfgDocument.selectNodes(vlanXpath);
			vLanIds = new VlanObj[vlanList.size()];
			int index = 0;
			for(Object obj : vlanList){
				if(obj instanceof Element){
					Element vlanEle = (Element)obj;
					VlanObj vlan = new VlanObj();
					vlan.setVlan(Integer.valueOf(vlanEle.attributeValue("value")));
					if(vlanEle.getParent().getName().equals("mgt0")){
						vlan.setIntVlan(InterfaceType.mgt0);
					}else if(vlanEle.getParent().getName().equals("mgt0.1")){
						vlan.setIntVlan(InterfaceType.mgt0_1);
					}else if(vlanEle.getParent().getName().equals("mgt0.2")){
						vlan.setIntVlan(InterfaceType.mgt0_2);
					}else if(vlanEle.getParent().getName().equals("mgt0.3")){
						vlan.setIntVlan(InterfaceType.mgt0_3);
					}else if(vlanEle.getParent().getName().equals("mgt0.4")){
						vlan.setIntVlan(InterfaceType.mgt0_4);
					}else if(vlanEle.getParent().getName().equals("mgt0.5")){
						vlan.setIntVlan(InterfaceType.mgt0_5);
					}else if(vlanEle.getParent().getName().equals("mgt0.6")){
						vlan.setIntVlan(InterfaceType.mgt0_6);
					}else if(vlanEle.getParent().getName().equals("mgt0.7")){
						vlan.setIntVlan(InterfaceType.mgt0_7);
					}else if(vlanEle.getParent().getName().equals("mgt0.8")){
						vlan.setIntVlan(InterfaceType.mgt0_8);
					}else if(vlanEle.getParent().getName().equals("mgt0.9")){
						vlan.setIntVlan(InterfaceType.mgt0_9);
					}else if(vlanEle.getParent().getName().equals("mgt0.10")){
						vlan.setIntVlan(InterfaceType.mgt0_10);
					}else if(vlanEle.getParent().getName().equals("mgt0.11")){
						vlan.setIntVlan(InterfaceType.mgt0_11);
					}else if(vlanEle.getParent().getName().equals("mgt0.12")){
						vlan.setIntVlan(InterfaceType.mgt0_12);
					}else if(vlanEle.getParent().getName().equals("mgt0.13")){
						vlan.setIntVlan(InterfaceType.mgt0_13);
					}else if(vlanEle.getParent().getName().equals("mgt0.14")){
						vlan.setIntVlan(InterfaceType.mgt0_14);
					}else if(vlanEle.getParent().getName().equals("mgt0.15")){
						vlan.setIntVlan(InterfaceType.mgt0_15);
					}else if(vlanEle.getParent().getName().equals("mgt0.16")){
						vlan.setIntVlan(InterfaceType.mgt0_16);
					}
					vLanIds[index++] = vlan;
				}
			}
		} catch (Exception e) {
			vLanIds = null;
			return vLanIds;
		}
		return vLanIds;
	}
	
	public static Array getApTopology(long aptop,Connection connection){
		Array topologygroupArray = null;
		String getAllParentMapId = "select parent_map_id from map_node where id = ?";
		PreparedStatement queryParentMap = null;
		ResultSet rs_map = null;
		try
		{
			if(null == connection){
				connection = NetworkDeviceConfigTracking.initConn();
			}
			queryParentMap = connection.prepareStatement(getAllParentMapId);
			List<Long> allMap = new ArrayList<Long>();
			long id = aptop;
			int label = 0;
			while(true){
				if(null == allMap || allMap.isEmpty() ){
					allMap.add(id);
				}
				rs_map = null;
				try {
					rs_map = JdbcUtil.query(queryParentMap,new Object[]{id});
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"query parent map failure: "+e.getMessage());
					break;
				}
				if(null != rs_map){
						while (rs_map.next()) {
							if(rs_map.getLong(1) != 0){
								allMap.add(rs_map.getLong(1));
								id = rs_map.getLong(1);
								continue;
							}else{
								label = 1;
								break;
							}
						}
				}else{
					break;
				}
				if(label == 1){
					break;
				}
			}
			if(null != allMap && allMap.size() >0){
				Long[] topologygroupLong = new Long[allMap.size()];
				allMap.toArray(topologygroupLong);
				Long[] reverseArr = NetworkDeviceConfigTracking.reverseArray(topologygroupLong);
				topologygroupArray = connection.createArrayOf("bigint",reverseArr);
			}
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE,"query ap topology error: "+ex.getMessage());
			topologygroupArray = null;
			return topologygroupArray;
		}finally
		{
			NetworkDeviceConfigTracking.free(rs_map, null);
			NetworkDeviceConfigTracking.freePSMT(queryParentMap);
		}
		return topologygroupArray;
	}
	
	public static Array arrayCovert(Object[] object,Connection connection){
		Array param = null;
		if (object instanceof java.lang.String[]){
			if (object.length > 0) {
				try {
					param = connection.createArrayOf("varchar",object);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"varchar array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}else if(object instanceof Long[]){
			if (object.length > 0) {
				try {
					param = connection.createArrayOf("bigint",object);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"bigint array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}else if(object instanceof VlanObj[]){
			VlanObj[] obj =( VlanObj[]) object ;
			Short[] temp = new Short[obj.length];
	    	if (obj.length > 0) {
	    		for(int i=0; i<obj.length; i++){
	    			temp[i] = (short)obj[i].getVlan();
	    		}
	    		try {
					param = connection.createArrayOf("smallint",temp);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"smallint array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}
		return param;
	}
	
}
