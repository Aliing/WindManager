package com.ah.be.performance;

import java.math.BigInteger;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.admin.restoredb.MigrateApTopFromHiveAp;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.NetworkDeviceHistory;
import com.ah.util.JdbcUtil;
import com.ah.util.MgrUtil;

public class BeCleanDuplicateApTopProcessor implements Runnable {
	
	public final int TIMER_INTERVAL     = 24;
	
	private ScheduledExecutorService scheduler;
	
	public BeCleanDuplicateApTopProcessor() {}
	
	public void start() {
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> ap topology duplicate data clean timer start...");
		// start scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.schedule(this, TIMER_INTERVAL,TimeUnit.HOURS);
		}
	}
	
	public void stop() {
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> ap topology duplicate data  timer stop");
		if (scheduler != null && !scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
			scheduler = null;
		}
	}
	
	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());
		try{
			removeDupliateData();
			syncApTopology();
		}catch(Exception e){
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"synchronize ap topology data failure: "+e.getMessage(), e);
		}
	}
	
	private void removeDupliateData(){
		Connection connection = NetworkDeviceConfigTracking.initConn();
		PreparedStatement deleteSql = null;
		try{
			String sql = "select distinct mac, owner,begintimestamp from network_device_history order by begintimestamp desc";
			List<?> apTopList = QueryUtil.executeNativeQuery(sql);
			String deleteSqlStr = "delete from network_device_history where mac=? and owner=? and begintimestamp<?";
			deleteSql = connection.prepareStatement(deleteSqlStr);
			if(null != apTopList && !apTopList.isEmpty()){
				for(Object oneObj: apTopList){
					if(null == connection){
						connection = NetworkDeviceConfigTracking.initConn();
						deleteSql = connection.prepareStatement(deleteSqlStr);
					}
					Object[] objs = (Object[]) oneObj;
					String mac = (String)objs[0];
					Long ownerId = ((BigInteger) objs[1]).longValue();
					boolean bol = JdbcUtil.updateOrInsert(deleteSql, new Object[]{mac,ownerId,Timestamp.valueOf(objs[2].toString())});
					if(!bol){
						BeLogTools.error(HmLogConst.M_RESTORE,"bulk delete domain id ["+ownerId+"] duplicate ap ["+mac+"] topology data failure.");
					}
				}
			}
		}catch(Exception e){
			BeLogTools.error(HmLogConst.M_RESTORE,"bulk delete duplicate ap topology data failure: "+e.getMessage(), e);
		}finally{
			NetworkDeviceConfigTracking.freePSMT(deleteSql);
			NetworkDeviceConfigTracking.freeCon(connection);
		}
	}
	
	private void syncApTopology(){
		List<HmDomain> domains = QueryUtil.executeQuery(HmDomain.class, null, null);
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
						if(null == connection){
							connection = NetworkDeviceConfigTracking.initConn();
							insert = connection.prepareStatement(insertString);
							queryAps = connection.prepareStatement(queryAp);
							queryDefaultMaps = connection.prepareStatement(getDefaultMapsql);
						}
						String tempMac = "";
						for(int i=1; i<=iCount_ap; i++){
							String newStr  = rs_ap.getString(i);
							if("macAddress".equalsIgnoreCase(rsmd_ap.getColumnName(i))){
								tempMac = newStr;
								break;
							}
						}
						long count = QueryUtil.findRowCount(NetworkDeviceHistory.class, 
								new FilterParams("MAC = :s1 and owner.id = :s2",new Object[]{tempMac,domain_id}));
						if(count >=1){
							continue;
						}
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
							topologygroupArray = MigrateApTopFromHiveAp.getApTopology(mapId,connection);
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
								topologygroupArray =  MigrateApTopFromHiveAp.getApTopology(map_id,connection);
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
//							VlanObj[] vlans =  MigrateApTopFromHiveAp.getVlans(domain_name,mac);
							boolean bol = JdbcUtil.updateOrInsert(insert,new Object[]{mac,startTime,ap_timezone,templateId,domain_id,topologygroupArray,
									null, MigrateApTopFromHiveAp.arrayCovert(tags,connection)});
							if(!bol){
								BeLogTools.error(HmLogConst.M_RESTORE,"time synchronize ["+domain_name+"] hive_ap ["+mac+"] data into network_device_history table failure.");
							}
						}
					}catch(Exception e){
						BeLogTools.error(HmLogConst.M_RESTORE,"time synchronize ["+domain_name+"] hive_ap ["+mac+"] data into network_device_history table exception: "+e.getMessage(),e);
						continue;
					}
				}
		    }
		}	
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_RESTORE,"time synchronize hive_ap data into network_device_history table exception: "+ex.getMessage(),ex);
		}finally
		{
			NetworkDeviceConfigTracking.free(rs_ap, null);
			NetworkDeviceConfigTracking.free(rs_map, null);
			NetworkDeviceConfigTracking.freePSMT(queryAps);
			NetworkDeviceConfigTracking.freePSMT(queryDefaultMaps);
			NetworkDeviceConfigTracking.freePSMT(insert);
			NetworkDeviceConfigTracking.freeCon(connection);
		}
	
	}

}
