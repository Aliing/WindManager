package com.ah.be.performance.dataretention;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.JdbcUtil;



public class NetworkDeviceHistoryProcessor {
	
	private boolean											isContinue = false;
	
	EventProcessorThread									processorThread = null;
	
	static private BlockingQueue<DeviceHistory> 				eventQueue = null;
	
	static private long lost_num = 0;
		
	public NetworkDeviceHistoryProcessor() {
		eventQueue = new LinkedBlockingQueue<DeviceHistory>(5000);
	}
	
	
	public void start() {
		if(isContinue)
			return;
		isContinue = true;
		processorThread = new EventProcessorThread();
		processorThread.start();
	}

	public void stop() {
		if(!isContinue)
			return;
		isContinue = false;
		if(processorThread != null) {
			DeviceHistory deviceHistory = new DeviceHistory();
			deviceHistory.setType(0);
			addEvent(deviceHistory);
		}
	}

	
	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	public static void addEvent(DeviceHistory event) {
		try {
				boolean result = eventQueue.offer(event);
				if(!result){
					lost_num++;
					if(lost_num == 1 || lost_num % 500 == 1){
						BeLogTools.error(HmLogConst.M_PERFORMANCE,
								"NetworkDeviceHistoryProcessor.addEvent(): queue is full , had been lost new device number: "+lost_num);
					}
				}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
					"NetworkDeviceHistoryProcessor.addEvent(): Exception happened while adding event: ",e);
		}
	}

	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private DeviceHistory getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
							"NetworkDeviceHistoryProcessor.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}
	
	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			this.setName("NetworkDeviceHistory Handle processor ");
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> NetworkDeviceHistory Handle processor - event processor is running...");

			while (isContinue) {
				try {
					DeviceHistory event = getEvent();
					if(event == null)
						continue;
					
					if(event.getType() == DeviceHistory.DEVICE_HISTORY_TOPOLOGY_CHANGE){
						// judge deviceMac is new?
						String queryString = "SELECT 1 FROM network_device_history WHERE mac = ? and endtimestamp is null";
						String updateString = "UPDATE network_device_history SET begintimestamp=?, networkpolicy=? , milliseconds2gmt=?, topologygroup=?, tags=? WHERE mac=? and endtimestamp is null;";
						String insertString = "INSERT INTO network_device_history(mac,begintimestamp,milliseconds2gmt,networkpolicy,owner,topologygroup,tags)VALUES (?,?,?,?,?,?,?)";
						
						
						Timestamp startTime = new Timestamp(event.getTimeStampWithDeviceTimeZone().getTimeInMillis());
						
						String getNetworkpolicyID="SELECT template_id FROM hive_ap WHERE macaddress=?" ; 
						String getAllParentMapId = "select parent_map_id from map_node where id = ?";
						PreparedStatement getNetworkpolicy = null;
						PreparedStatement queryParentMap = null;
						PreparedStatement query = null;
						PreparedStatement update = null;
						PreparedStatement insert = null;
						ResultSet rs = null;
						Connection connection = null;
						try
						{
							 connection = NetworkDeviceConfigTracking.initConn();
							 getNetworkpolicy = connection.prepareStatement(getNetworkpolicyID);
							 queryParentMap = connection.prepareStatement(getAllParentMapId);
							 query = connection.prepareStatement(queryString);
							 update = connection.prepareStatement(updateString);
							 insert = connection.prepareStatement(insertString);
							// get Networkpolicy id by ap mac
							Long networkpolicyID = 0l ; 
							rs = JdbcUtil.query(getNetworkpolicy,new Object[]{event.getDeviceMAC()});
							if (rs.next()){
								networkpolicyID = rs.getLong(1);
							}
							
							List<Long> allMap = new ArrayList<Long>();
							if(null != event.getTopologyGroupPkFromTopToBottom() && event.getTopologyGroupPkFromTopToBottom().length > 0){
								long id = event.getTopologyGroupPkFromTopToBottom()[0];
								int label = 0;
								while(true){
									if(null == allMap || allMap.isEmpty() ){
										allMap.add(id);
									}
									rs = null;
									try {
										rs = JdbcUtil.query(queryParentMap,new Object[]{id});
									} catch (Exception e1) {
										BeLogTools.error(HmLogConst.M_PERFORMANCE,"query parent map failure: "+e1.getMessage());
									}
									if(null != rs){
											while (rs.next()) {
												if(rs.getLong(1) != 0){
													allMap.add(rs.getLong(1));
													id = rs.getLong(1);
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
							}
							
							Array topologygroupArray = null;
							if(null != allMap && allMap.size() >0){
								Long[] topologygroupLong = new Long[allMap.size()];
								allMap.toArray(topologygroupLong);
								Long[] reverseArr = NetworkDeviceConfigTracking.reverseArray(topologygroupLong);
								topologygroupArray = connection.createArrayOf("bigint",reverseArr);
							}
							
							rs= null;
							rs = JdbcUtil.query(query,new Object[]{event.getDeviceMAC()});
							if(rs.next())
							{
								JdbcUtil.updateOrInsert(update,new Object[]{startTime,networkpolicyID,event.getHours2GMT(),topologygroupArray,NetworkDeviceConfigTracking.arrayCovert(event.getTags(),connection),event.getDeviceMAC()});
							}
							else{
								JdbcUtil.updateOrInsert(insert,new Object[]{event.getDeviceMAC(),startTime,event.getHours2GMT(),networkpolicyID,event.getvHMdomain(),topologygroupArray,NetworkDeviceConfigTracking.arrayCovert(event.getTags(),connection)});
							}
						}
						catch(Exception ex)
						{
							BeLogTools.error(HmLogConst.M_PERFORMANCE,"topoplogy change failure: "+ex.getMessage(),ex);
						}finally
						{
							NetworkDeviceConfigTracking.free(rs, null);
							NetworkDeviceConfigTracking.freePSMT(getNetworkpolicy);
							NetworkDeviceConfigTracking.freePSMT(queryParentMap);
							NetworkDeviceConfigTracking.freePSMT(query);
							NetworkDeviceConfigTracking.freePSMT(update);
							NetworkDeviceConfigTracking.freePSMT(insert);
							NetworkDeviceConfigTracking.freeCon(connection);
						}
					}else if(event.getType() == DeviceHistory.DEVICE_HISTORY_TOPOLOGYGROUP_CHANGE){

						
						//get all ap topology contain current map
						String queryAllApMac = "select mac from network_device_history where endtimestamp is null and ?=ANY(topologyGroup)";
						//get ap's mapid
						String queryApMapId = "select map_container_id from hive_ap where macaddress = ?";
						// get MapNode.id from top(exclusive) to bottom(inclusive)
						String getAllParentMapId = "select parent_map_id from map_node where id = ?";
						String updateString = "UPDATE network_device_history SET topologyGroup=?  WHERE mac=? and endtimestamp is null" ;
						PreparedStatement queryAllAp = null;
						PreparedStatement queryApMap = null;
						PreparedStatement queryParentMap = null;
						PreparedStatement update = null;
						ResultSet rs = null;
						Connection connection = null;
						try
						{
							connection = NetworkDeviceConfigTracking.initConn();
							update = connection.prepareStatement(updateString);
							queryAllAp = connection.prepareStatement(queryAllApMac);
							queryApMap = connection.prepareStatement(queryApMapId);
							queryParentMap = connection.prepareStatement(getAllParentMapId);
							rs = JdbcUtil.query(queryAllAp,new Object[]{event.getTopologyContainerPK()});
							while(rs.next()){
								String mac = rs.getString(1);
								ResultSet rs1;
								rs1 = JdbcUtil.query(queryApMap,new Object[]{mac});
								if(rs1.next()){
									List<Long> allMap = new ArrayList<Long>();
									long id = rs1.getLong(1);
									int label = 0;
									while(true){
										if(null == allMap || allMap.isEmpty() ){
											allMap.add(id);
										}
										rs1 = null;
										try {
											rs1 = JdbcUtil.query(queryParentMap,new Object[]{id});
										} catch (Exception e1) {
											BeLogTools.error(HmLogConst.M_PERFORMANCE,"query parent map failure: "+e1.getMessage());
										}
										if(null != rs1){
												while (rs1.next()) {
													if(rs1.getLong(1) != 0){
														allMap.add(rs1.getLong(1));
														id = rs1.getLong(1);
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
									
									Long[] topologygroupLong = new Long[allMap.size()];
									Array topologygroupArray = null;
									if(null != allMap && allMap.size() >0){
										allMap.toArray(topologygroupLong);
										Long[] reverseArr = NetworkDeviceConfigTracking.reverseArray(topologygroupLong);
										topologygroupArray = connection.createArrayOf("bigint",reverseArr);
									}else{
										topologygroupLong = null;
									}
									JdbcUtil.updateOrInsert(update,new Object[]{topologygroupArray,mac});
								}
							}
						}
						catch(Exception ex)
						{
							BeLogTools.error(HmLogConst.M_PERFORMANCE,"topologyGroupChanged changed failure: "+ex.getMessage(),ex);
						}finally
						{
							NetworkDeviceConfigTracking.free(rs, null);
							NetworkDeviceConfigTracking.freePSMT(queryAllAp);
							NetworkDeviceConfigTracking.freePSMT(queryParentMap);
							NetworkDeviceConfigTracking.freePSMT(queryApMap);
							NetworkDeviceConfigTracking.freePSMT(update);
							NetworkDeviceConfigTracking.freeCon(connection);
						}
					}else if(event.getType() == DeviceHistory.DEVICE_HISTORY_POLICY_CHANGE){

						String queryString="select id from network_device_history WHERE mac=? and endtimestamp is null ";
						String updateString = "UPDATE network_device_history SET networkPolicy=? , milliseconds2gmt=? ,vLAN=?,tags=? WHERE id=?";

						PreparedStatement query = null;
						PreparedStatement update = null;
						ResultSet rs = null;
						Connection connection = null;
						try
						{
							connection = NetworkDeviceConfigTracking.initConn();
							query = connection.prepareStatement(queryString);
							update = connection.prepareStatement(updateString);
							Long networkDeviceHistoryID =null;
							rs = JdbcUtil.query(query,new Object[]{event.getDeviceMAC()});
							while (rs.next()) {
								networkDeviceHistoryID = rs.getLong(1);
								if (null != networkDeviceHistoryID ){
									JdbcUtil.updateOrInsert(update,new Object[]{event.getNetworkPolicyPK(),event.getMilliSeconds2GMT(),
											null,
											NetworkDeviceConfigTracking.arrayCovert(event.getTags(),connection),networkDeviceHistoryID});
								}
						    }
						}
						catch(Exception ex)
						{
							BeLogTools.error(HmLogConst.M_PERFORMANCE,"policyChanged Exception: " + ex.getMessage(),ex);
						}finally
						{
							NetworkDeviceConfigTracking.free(rs, null);
							NetworkDeviceConfigTracking.freePSMT(query);
							NetworkDeviceConfigTracking.freePSMT(update);
							NetworkDeviceConfigTracking.freeCon(connection);
						}
					}else if(event.getType() == DeviceHistory.DEVICE_HISTORY_TAGS_CHANGE){

						String updateString = "UPDATE network_device_history SET tags=? WHERE mac=? and endtimestamp is null";

						PreparedStatement update = null;
						ResultSet rs = null;
						Connection connection = null;
						try
						{
							connection = NetworkDeviceConfigTracking.initConn();
							update = connection.prepareStatement(updateString);
							JdbcUtil.updateOrInsert(update,new Object[]{NetworkDeviceConfigTracking.arrayCovert(event.getTags(),connection),event.getDeviceMAC()});
						}
						catch(Exception ex)
						{
							BeLogTools.error(HmLogConst.M_PERFORMANCE,"tags change Exception: " + ex.getMessage(),ex);
						}finally
						{
							NetworkDeviceConfigTracking.free(rs, null);
							NetworkDeviceConfigTracking.freePSMT(update);
							NetworkDeviceConfigTracking.freeCon(connection);
						}
					}
					
				} catch (Error e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"Error in Data Retention Handle processor thread ", e);
				}
			}
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Data Retention Handle processor - event processor is shutdown.");
		}
	}

}

