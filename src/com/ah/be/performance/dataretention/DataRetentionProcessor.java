package com.ah.be.performance.dataretention;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.ClientDeviceInfo;
import com.ah.bo.network.Application;
import com.ah.bo.network.NetworkService;
import com.ah.util.HibernateUtil;



public class DataRetentionProcessor {
	
	private boolean											isContinue = false;
	
	EventProcessorThread									processorThread = null;
	
	static private BlockingQueue<ClientHistory> 				eventQueue = null;
	
	static private long lost_num = 0;
		
	public DataRetentionProcessor() {
		eventQueue = new LinkedBlockingQueue<ClientHistory>(10000);
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
			ClientHistory clientHistory = new ClientHistory();
			clientHistory.setType(0);
			addEvent(clientHistory);
		}
	}

	
	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	public static void addEvent(ClientHistory event) {
		try {
				boolean result = eventQueue.offer(event);
				if(!result){
					lost_num++;
					if(lost_num == 1 || lost_num % 100 == 1){
						BeLogTools.error(HmLogConst.M_PERFORMANCE,
								"DataRetentionProcessor.addEvent(): queue is full , had been lost client num: "+lost_num);
					}
				}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
					"DataRetentionProcessor.addEvent(): Exception happened while adding event: ",e);
		}
	}

	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private ClientHistory getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,
							"DataRetentionProcessor.getEvent(): Exception while get event from queue",
							e);
			return null;
		}
	}
	
	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			this.setName("Data Retention Handle processor ");
			BeLogTools.info(HmLogConst.M_TRACER,
					"<BE Thread> Data Retention Handle processor - event processor is running...");

			while (isContinue) {
				try {
					ClientHistory event = getEvent();
					if(event == null)
						continue;
					
					if(event.getType() == ClientHistory.CLIENT_REQUEST_ASSOCIATE){
						List<ClientDeviceInfo> list = QueryUtil.executeQuery(ClientDeviceInfo.class,
								null, new FilterParams("mac = :s1",new Object[]{event.getClientMAC()}), event.getOwner());
						if(!list.isEmpty() && list.size()>0){
							ClientDeviceInfo entry = list.get(0);
							entry.setHostName(event.getHostName());
							entry.setOS_type(event.getOS_type());
							entry.setOption55(event.getOption55());
							entry.setUpdate_at((new Date()).getTime());
							try {
								QueryUtil.updateBo(entry);
							} catch (Exception e) {
								BeLogTools.error(HmLogConst.M_PERFORMANCE,
										"DataRetentionProcessor: Exception while update client associate",
										e);
							}
						}else{
							ClientDeviceInfo entry = new ClientDeviceInfo();
							entry.setHostName(event.getHostName());
							entry.setOS_type(event.getOS_type());
							entry.setOption55(event.getOption55());
							entry.setUpdate_at((new Date()).getTime());
							entry.setMAC(event.getClientMAC());
							entry.setOwner(QueryUtil.findBoById(HmDomain.class, event.getOwner()));
							try {
								QueryUtil.createBo(entry);
							} catch (Exception e) {
								BeLogTools.error(HmLogConst.M_PERFORMANCE,
										"DataRetentionProcessor: Exception while create client associate",
										e);
							}
						}
					}else if(event.getType() == ClientHistory.CLIENT_REQUEST_DEASSOCIATE){
							try {
								QueryUtil.updateBos(ClientDeviceInfo.class, 
										"update_at = :s1", "MAC = :s2 and owner.id = :s3",
										new Object[] { (new Date()).getTime(), event.getClientMAC(),event.getOwner() });
							} catch (Exception e) {
								BeLogTools.error(HmLogConst.M_PERFORMANCE,
										"DataRetentionProcessor: Exception while client deassociate",
										e);
							}
					}else if(event.getType() == ClientHistory.CLIENT_REQUEST_CLIENTINFO){
						try {
							QueryUtil.updateBos(ClientDeviceInfo.class, 
									"hostName = :s1", "MAC = :s2 and owner.id = :s3",
									new Object[] { event.getHostName(), event.getClientMAC(),event.getOwner() });
						} catch (Exception e) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE,
									"DataRetentionProcessor: Exception while client deassociate",
									e);
						}
					}else if(event.getType() == ClientHistory.CLIENT_REQUEST_CLIENTOSINFO){
						try {
							QueryUtil.updateBos(ClientDeviceInfo.class, 
									"OS_type = :s1", "MAC = :s2 and owner.id = :s3",
									new Object[] { event.getOS_type(), event.getClientMAC(),event.getOwner() });
						} catch (Exception e) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE,
									"DataRetentionProcessor: Exception while client deassociate",
									e);
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			DataRetentionProcessor pool = new DataRetentionProcessor();
//			pool.start();
//			ClientHistory clientHistory = new ClientHistory();
//			clientHistory.setTimeStampWithAerohiveDeviceTimeZone((new Date()).getTime());
//			clientHistory.setClientMAC("247703537995");
//			clientHistory.setHostName("nms-szhou");
//			clientHistory.setOS_type("windows7");
//			clientHistory.setNetworkDeviceMAC("AA33DDDD22F4");
//			clientHistory.setOwner(3L);
//			clientHistory.setUserName("SZHOU");
//			clientHistory.setUserProfile("NETWORK_TEST");
//			clientHistory.seteMail("SZHOU@AEROHIVE.COM");
//			clientHistory.setSSId("SSID-TEST");
//			byte a =1;
//			clientHistory.setAuthentication(a);
//			clientHistory.setIp4(AhEncoder.int2bytes(AhEncoder.ip2Int("10.155.20.64")));
//			clientHistory.setIp6(null);
//			clientHistory.setType(ClientHistory.CLIENT_REQUEST_ASSOCIATE);
////			clientHistory.setType(ClientHistory.CLIENT_REQUEST_DEASSOCIATE);
//			DataRetentionProcessor.addEvent(clientHistory);
//			Thread.sleep(10000);
//			
//			pool.stop();
			try {
				HibernateUtil.init(true);
//				List<ClientDeviceInfo> list = QueryUtil.executeQuery(ClientDeviceInfo.class,
//						null, new FilterParams("mac = :s1",new Object[]{"0012FE000400"}), 3l);
//				Set<String> setMac = new HashSet<String>();
//				setMac.add("0012FE001006");
//				setMac.add("0012FE001007");
//				setMac.add("0012FE001008");
//				setMac.add("0012FE001009");
//				List<ClientDeviceInfo> list = QueryUtil.executeQuery(ClientDeviceInfo.class, null,
//						new FilterParams("MAC in(:s1)",new Object[]{setMac}));
//				if(!list.isEmpty() && list.size()>0){
//					ClientDeviceInfo entry = list.get(0);
//					System.out.println("mac = "+entry.getMAC());
//					entry.setOS_type("windows7");
//					QueryUtil.updateBo(entry);
//					System.out.println("ostype = "+entry.getOS_type());
//					if(null != list && !list.isEmpty()){
//						List<ClientDeviceInfo> updateBos = new ArrayList<ClientDeviceInfo>();
//						for(ClientDeviceInfo cdinfo : list) {
//										cdinfo.setHostName(cdinfo.getHostName());
//										cdinfo.setOS_type("windows-"+cdinfo.getMAC());
//										cdinfo.setOwner(QueryUtil.findBoById(HmDomain.class, 3l));
//										cdinfo.setUpdate_at((new Date()).getTime());
//										updateBos.add(cdinfo);
//							}
//						if(null != updateBos && !updateBos.isEmpty()){
//							try {
//								QueryUtil.bulkUpdateBos(updateBos);
//							} catch (Exception e) {
//								BeLogTools.error(HmLogConst.M_PERFORMANCE, "ReportCacheMgmt batch update client_device_info exception: " + e.getMessage());
//							}
//						}
//					}
//				}
//				long rowCount = QueryUtil.findRowCount(NetworkService.class, new FilterParams("servicetype = :s1",new Object[]{NetworkService.SERVICE_TYPE_L7}));
//				System.out.println("rowCount==="+rowCount);
//				QueryUtil.updateBos(ClientDeviceInfo.class, 
//						"OS_type = :s1", "MAC = :s2 and owner.id = :s3",
//						new Object[] { "windows7", "0012FE000500",3l });
				
				
				
				Map<String, Object> map = new HashMap<>();
				map.put("serviceName", "L7-12306.CN");
				map.put("servicetype", NetworkService.SERVICE_TYPE_L7);
				map.put("owner", QueryUtil.findBoById(HmDomain.class, 2l));
				Long networkServiceId = HmBeParaUtil.getDefaultProfileId(NetworkService.class, map);
				System.out.println("id==="+networkServiceId);
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE,
						"DataRetentionProcessor: Exception while client deassociate",
						e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

