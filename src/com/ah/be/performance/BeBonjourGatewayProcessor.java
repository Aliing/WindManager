package com.ah.be.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeBonjourGatewayEvent;
import com.ah.be.communication.event.BeBonjourGatewayInnerEvent;
import com.ah.be.communication.event.BeBonjourGatewayResultEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.BonjourGatewayMonitoring;
import com.ah.bo.network.BonjourRealm;
import com.ah.bo.network.BonjourServiceDetail;
import com.ah.ui.actions.monitor.BonjourGatewayMonitoringAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class BeBonjourGatewayProcessor implements Runnable {

	private static final Tracer log = new Tracer(BeBonjourGatewayProcessor.class
			.getSimpleName());
	
	private final BlockingQueue<BeBaseEvent> eventQueue;
	
	private final BlockingQueue<BeBaseEvent> eventInnerQueue;

	private static final int eventQueueSize = 10000;
	
	private boolean isContinue;
	
	private final BonjourGatewayThread BonjourGatewayThread;
	
	private final BonjourGatewayInnerThread bonjourgatewayinnerThread;
	
	private ScheduledExecutorService scheduler;
	
	// time unit is minutes
	private final int interval = 60;
	
	private final byte CYCLE_APNUM = 10;

	private final short RELAXTIME = 1000;
	
	private int index = 0;
	
	private static final int BONJOURGATEWAYINNERTYPE = 1;
	
	public BeBonjourGatewayProcessor() {
		eventQueue = new LinkedBlockingQueue<>(eventQueueSize);
		eventInnerQueue = new LinkedBlockingQueue<>(eventQueueSize);
		isContinue = true;
		
		// data collection process thread
		BonjourGatewayThread = new BonjourGatewayThread();
		BonjourGatewayThread.setName("bonjourGatewayProcessorThread");
		BonjourGatewayThread.start();
		
		bonjourgatewayinnerThread = new BonjourGatewayInnerThread();
		bonjourgatewayinnerThread.setName("bonjourgatewayinnerThread");
		bonjourgatewayinnerThread.start();
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeBonjourGatewayProcessor.addEvent(): Exception while add event to queue", e);
		}
	}
	
	public void addInnerEvent(BeBaseEvent event) {
		try {
			eventInnerQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeBonjourGatewayProcessor.addInnerEvent(): Exception while add event to queue", e);
		}
	}
	
	public void startTask() {
		// start scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, interval, interval,
					TimeUnit.MINUTES);
		}
		
		// nothing else to do at current time
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Bonjour Gateway Processor is running...");
	}
	
	public boolean shutdown() {
		if (!scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
		}
		
		isContinue = false;
		eventQueue.clear();
		eventInnerQueue.clear();
		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.offer(stopThreadEvent);
		eventInnerQueue.offer(stopThreadEvent);
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread>  Bonjour Gateway Processor is shutdown");
		
		return true;
	}
	
	// **************** BonjourGatewayInnerThread start ****************************
	public class BonjourGatewayInnerThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(bonjourgatewayinnerThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread>  Bonjour Gateway Process Inner Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventInnerQueue.take();
					if (null == event)
						continue;
					
					if(event.getEventType() == BONJOURGATEWAYINNERTYPE){
						BeBonjourGatewayInnerEvent bonjourGatewayInnerEvent = (BeBonjourGatewayInnerEvent)event;
						log.debug("BonjourGatewayInnerThread start");
						handleBonjourGatewayResultInnerEvent(bonjourGatewayInnerEvent);
						log.debug("BonjourGatewayInnerThread end");
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeBonjourGatewayProcessor.BonjourGatewayInnerThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeBonjourGatewayProcessor.BonjourGatewayInnerThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	private void handleBonjourGatewayResultInnerEvent(BeBonjourGatewayInnerEvent bonjourGatewayInnerEvent) {
		SimpleHiveAp simpleHiveAp = bonjourGatewayInnerEvent.getAp();
		if(simpleHiveAp!=null){
			if(bonjourGatewayInnerEvent.isBdd()){
				log.debug("BonjourGatewayInnerThread","bdd is up (macAddress:"+simpleHiveAp.getMacAddress()+")");
				MapContainerNode mapContainerNode = null;
				String apHiveName = bonjourGatewayInnerEvent.getHiveName();
				HiveAp hiveAp = findApTopologyData(simpleHiveAp.getMacAddress(),simpleHiveAp.getDomainId());
				String hiveRealmName = "";
				if(hiveAp != null){
//					 ConfigTemplate configTemplate = hiveAp.getConfigTemplate();
//					 if(apHiveName == null || "".equals(apHiveName)){
//						 apHiveName = configTemplate == null ? "" : configTemplate.getHiveProfile()
//									.getHiveName();
//					 }
					 apHiveName = hiveAp.getHiveName(); // hive name come from hivemanager, not device.
					 mapContainerNode = hiveAp.getMapContainer();
					 hiveRealmName = hiveAp.getRealmName();
				}
				if(mapContainerNode == null){
//					if(hiveRealmName != null && !"".equals(hiveRealmName)){
//						if(!bonjourGatewayInnerEvent.getRealmName().equals(hiveRealmName)){
//							setRealmName(simpleHiveAp,hiveRealmName);
//						}
//					}
					
					removeAllNeighbor(simpleHiveAp);
				} else {
//					String realmName = generateRealmName(mapContainerNode,apHiveName,hiveRealmName);
//					if(!bonjourGatewayInnerEvent.getRealmName().equals(realmName)){
//						setRealmName(simpleHiveAp,realmName);
//					}
					List<String> mapNeighbors = findNeighbors(mapContainerNode,apHiveName,simpleHiveAp.getMacAddress(),hiveRealmName);
					List<String> expiredNeighbor = findExpiredNeighbor(bonjourGatewayInnerEvent.getNeighbors(),mapNeighbors);
					List<String> neighbors = findNewNeighbor(bonjourGatewayInnerEvent.getNeighbors(),mapNeighbors);
					if(expiredNeighbor !=null && !expiredNeighbor.isEmpty()){
						removeExpiredNeighbor(simpleHiveAp,expiredNeighbor);
					}
					if(neighbors != null && !neighbors.isEmpty()){
						setNeighbors(simpleHiveAp,neighbors);
					}
				}
			} else {
				log.debug("BonjourGatewayInnerThread","bdd is down");
				removeAllNeighbor(simpleHiveAp);
				List<SimpleHiveAp> neighbors = findNeighbors(simpleHiveAp.getDomainId(),bonjourGatewayInnerEvent.getRealmName());
				if(!neighbors.isEmpty()){
					removeBddNeighbor(neighbors,bonjourGatewayInnerEvent.getAp().getIpAddress());
				}
			}
		}
	}
	
//	/*
//	 * realmName is generated like this building_hiveName
//	 */
//	private String generateRealmName(MapContainerNode mapContainerNode,String hiveName,String hiveRealmName){
//		String realmName = "";
//		if(hiveRealmName == null || "".equals(hiveRealmName)){
//			if(mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR){
//				if(mapContainerNode.getParentMap() != null){
//					MapContainerNode parentMap = mapContainerNode.getParentMap();
//					realmName = parentMap.getLabel() +  "_" + hiveName;
//				}
//			} else {
//				realmName = mapContainerNode.getLabel() + "_" + hiveName;
//			}
//		} else {
//			realmName = hiveRealmName;
//		}
//		
//		return realmName;
//	}
	
	private HiveAp findApTopologyData(String macAddress,Long domainId){
		//List<?> lists = QueryUtil.executeQuery(HiveAp.class, new String[]{"realmName","configTemplate","mapContainer"},null,new FilterParams("macAddress",macAddress), domainId, new BonjourGatewayMonitoringAction());
		List<HiveAp> hiveApList = QueryUtil.executeQuery(HiveAp.class,null,new FilterParams("macAddress",macAddress), domainId, new BonjourGatewayMonitoringAction());
		if(hiveApList.isEmpty()){
			return null;
		}
		
		return hiveApList.get(0);
	}
	
	private List<String> findExpiredNeighbor(List<String> deviceNb,List<String> mapNb){
		List<String> expiredNb = new ArrayList<>();
		if(deviceNb == null || deviceNb.isEmpty()){
			return null;
		}
		if(mapNb == null || mapNb.isEmpty()){
			return deviceNb;
		}
		for(String ip : deviceNb){
			if(!mapNb.contains(ip)){
				expiredNb.add(ip);
			}
		}
		return expiredNb;
	}
	
	private List<String> findNewNeighbor(List<String> deviceNb,List<String> mapNb){
		List<String> newNb = new ArrayList<>();
		if(mapNb == null || mapNb.isEmpty()){
			return null;
		}
		if(deviceNb == null || deviceNb.isEmpty()){
			return mapNb;
		}
		for(String ip : mapNb){
			if(!deviceNb.contains(ip)){
				newNb.add(ip);
			}
		}
		return newNb;
	}
	
	//for BDD is down
	public List<SimpleHiveAp> findNeighbors(Long domainId,String realmName){
		List<SimpleHiveAp> hiveAps = new ArrayList<>();
		if(realmName != null && !realmName.isEmpty()){
			List<BonjourGatewayMonitoring> bjgwMonitors = QueryUtil.executeQuery(BonjourGatewayMonitoring.class, null, new FilterParams("realmId", realmName), domainId);
			for(BonjourGatewayMonitoring bjgwMonitor : bjgwMonitors){
				SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(bjgwMonitor.getMacAddress());
				if (ap == null) {
					log.debug("findNeighbors-->getSimpleHiveAp null macAddress:("+bjgwMonitor.getMacAddress()+")");
				} else {
					hiveAps.add(ap);
				}
			}
		}
		
		return hiveAps;
	}
	
	//for BDD is up
	private List<String> findNeighbors(MapContainerNode mapContainerNode,String apHiveName,String macAddress,String realmName){
		// neighbor must be bdd and same hive
		List<HiveAp> HiveAps = new ArrayList<>();
		if(mapContainerNode.getParentMap() != null){
			Set<MapNode> childs = mapContainerNode.getChildNodes();
			for(MapNode node : childs){
				if(node.isLeafNode()){
					MapLeafNode leafNode = (MapLeafNode)node;
					HiveAp ap = leafNode.getHiveAp();
					String mac = ap.getMacAddress();
					String hiveName = ap.getHiveName();
					if(mac.equals(macAddress)){
						continue;
					}
					//Long count = QueryUtil.findRowCount(BonjourGatewayMonitoring.class, new FilterParams("macAddress = :s1 and realmId = :s2",new String[]{mac,realmName}));
					Long count = QueryUtil.findRowCount(BonjourGatewayMonitoring.class, new FilterParams("macAddress = :s1 and realmId is not null",new String[]{mac}));
					if(count > 0 && apHiveName.equals(hiveName)){
						HiveAps.add(ap);
					}
				}
			}
		}
		
		List<String> result = new ArrayList<>();
		for(HiveAp ap :HiveAps){
			if(ap.getIpAddress() != null && !"".equals(ap.getIpAddress())){
				result.add(ap.getIpAddress());
			}
		}
		
		return result;
	}
	
	private void setNeighbors(SimpleHiveAp ap,List<String> neighbors){
		List<String> nbList = new ArrayList<>();
		for (String neighbor : neighbors) {
			if (neighbor != null && !neighbor.isEmpty()) {
				nbList.add(neighbor);
			}
		}
		
		String[] clis = new String[nbList.size()];
		for(int i=0;i<nbList.size();i++){
			clis[i] = AhCliFactory.getSetBjgwNeighborCli(nbList.get(i));
		}
		
		sendCliRequest(ap,clis);
	}
	
	private void removeAllNeighbor(SimpleHiveAp ap){
		String cli =  AhCliFactory.getRemoveBjgwNeighborCli();
		sendCliRequest(ap,new String[]{ cli });
	}
	private void removeExpiredNeighbor(SimpleHiveAp ap,List<String> expiredNeighbors){
		List<String> nbList = new ArrayList<>();
		for (String expiredNeighbor : expiredNeighbors) {
			if (expiredNeighbor != null && !expiredNeighbor.isEmpty()) {
				nbList.add(expiredNeighbor);
			}
		}
		
		String[] clis = new String[nbList.size()];
		for(int i=0;i<nbList.size();i++){
			clis[i] = AhCliFactory.getRemoveBjgwNeighborCli(nbList.get(i));
		}
		
		sendCliRequest(ap,clis);
	}
	
	public void removeBddNeighbor(List<SimpleHiveAp> neighbors,String ipAddress){
		String cli =  AhCliFactory.getRemoveBjgwNeighborCli(ipAddress);
		for(SimpleHiveAp ap : neighbors){
			sendCliRequest(ap,new String[]{cli});
		}
	}
	
	private void setRealmName(SimpleHiveAp ap,String mapName){
		String cli =  AhCliFactory.getSetRealmNameCli(mapName);
		sendCliRequest(ap,new String[]{cli});
	}
	
	private static final int TIMEOUT_CLI = 35; // second

	/**
	 * send cli request
	 * 
	 * @param ap -
	 * @param clis -
	 * @return -
	 */
	private boolean sendCliRequest(SimpleHiveAp ap, String[] clis) {
		try {
			if(NmsUtil.compareSoftwareVersion("6.0.1.0", ap.getSoftVer()) > 0){
				log.debug("BeBonjourGatewayProcessor.sendCliRequest","The device version is lower then 6.0.1.0 [" + "macAddress:("+ap.getMacAddress()+")]");
				return false;
			}
			log.debug("sendCliRequest Start","macAddress:("+ap.getMacAddress()+")");
			BeCliEvent c_event = new BeCliEvent();
			c_event.setSimpleHiveAp(ap);
			c_event.setClis(clis);
			c_event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			c_event.buildPacket();
			int serialNum = HmBeCommunicationUtil.sendRequest(c_event,
					TIMEOUT_CLI);
			boolean result = serialNum != BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
			if(!result){
				log.error("sendCliRequest",
						"set cli error (macAddress:"+ap.getMacAddress()+",clis:"+clis.toString()+")");
			} 
			return result;
		} catch (Exception e) {
			log.error("sendCliRequest",
					"Communication closed or build packet error.", e);
			return false;
		}
	}
	// **************** BonjourGatewayInnerThread start ****************************
	
	// **************** BonjourGatewayThread start ****************************
	public class BonjourGatewayThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(BonjourGatewayThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread>  Bonjour Gateway Process Thread is running...");
			
			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY) {
								BeBonjourGatewayResultEvent bonjourGatewayResultEvent = (BeBonjourGatewayResultEvent)resultEvent;
								handleBonjourGatewayResultEvent(bonjourGatewayResultEvent);
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeBonjourGatewayProcessor.BonjourGatewayThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeBonjourGatewayProcessor.BonjourGatewayThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	private void handleBonjourGatewayResultEvent(BeBonjourGatewayResultEvent resultEvent) {
		try {
			String apMac = resultEvent.getApMac();
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			if(simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED){
				return;
			}
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());

			if (resultEvent.getSequenceNum() != 0) {
				BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class,
						"macAddress", apMac, owner.getId());
				if(bonjourGatewayMonitoring != null){
					QueryUtil.removeBo(BonjourGatewayMonitoring.class, bonjourGatewayMonitoring.getId());
				}
			}
				
			if(resultEvent.getOperTypeMap().containsKey(BeBonjourGatewayResultEvent.ELEMENT_TYPE_REALM)){
				BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class,
						"macAddress", apMac, owner.getId());
				if (bonjourGatewayMonitoring == null) {
					bonjourGatewayMonitoring = new BonjourGatewayMonitoring();
					bonjourGatewayMonitoring.setMacAddress(apMac);
					bonjourGatewayMonitoring.setHostName(simpleHiveAp.getHostname());
					bonjourGatewayMonitoring.setOwner(owner);
					bonjourGatewayMonitoring.setRealmId(resultEvent.getRealmId());
					QueryUtil.createBo(bonjourGatewayMonitoring);
				} else{
					if(!resultEvent.getRealmId().equals(bonjourGatewayMonitoring.getRealmId())){
						// remove bonjour realm if realm id is not existed
						removeReam(bonjourGatewayMonitoring.getRealmId(),owner.getId());
						
						bonjourGatewayMonitoring.setRealmId(resultEvent.getRealmId());
						QueryUtil.updateBo(bonjourGatewayMonitoring);
					}
				}
			}
			
			if(resultEvent.getOperTypeMap().containsKey(BeBonjourGatewayResultEvent.ELEMENT_TYPE_BDD)){
				BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class,
						"macAddress", apMac, owner.getId());
				// add bdd 
				if(resultEvent.getBddFlag() == 1) { 
					//clear exist services belong to the BDD ,when HiveOS actively send a BDD message to HM
					if (resultEvent.getSequenceNum() == 0) {
						StringBuilder sqlBuffer  = new StringBuilder();
						sqlBuffer.append(" delete from BONJOUR_SERVICE_DETAIL where ");
						sqlBuffer.append("  macAddress='");
						sqlBuffer.append(apMac);
						sqlBuffer.append("'");
						QueryUtil.executeNativeUpdate(sqlBuffer.toString());
					}
					
					if(bonjourGatewayMonitoring == null){
						bonjourGatewayMonitoring = new BonjourGatewayMonitoring();
						bonjourGatewayMonitoring.setMacAddress(apMac);
						bonjourGatewayMonitoring.setHostName(simpleHiveAp.getHostname());
						bonjourGatewayMonitoring.setOwner(owner);
						QueryUtil.createBo(bonjourGatewayMonitoring);
					} else {
						bonjourGatewayMonitoring.setMacAddress(apMac);
						bonjourGatewayMonitoring.setHostName(simpleHiveAp.getHostname());
						bonjourGatewayMonitoring.setOwner(owner);
						QueryUtil.updateBo(bonjourGatewayMonitoring);
					}
				} else if(resultEvent.getBddFlag() == 0){ // remove bdd
					//remove
					if(bonjourGatewayMonitoring != null){
						// remove bonjour realm if realm id is not existed
						removeReam(bonjourGatewayMonitoring.getRealmId(),owner.getId());
						QueryUtil.removeBo(BonjourGatewayMonitoring.class, bonjourGatewayMonitoring.getId());
					}
				}
			
				if(NmsUtil.compareSoftwareVersion("6.0.1.0", simpleHiveAp.getSoftVer()) <= 0){
					BeBonjourGatewayInnerEvent event = new BeBonjourGatewayInnerEvent();
					event.setAp(simpleHiveAp);
					event.setBdd(resultEvent.getBddFlag() == 1);
					if(bonjourGatewayMonitoring != null && bonjourGatewayMonitoring.getRealmId() != null){
						event.setRealmName(bonjourGatewayMonitoring.getRealmId());
					}
					event.setHiveName(resultEvent.getHiveName());
					event.setNeighbors(resultEvent.getNeighbors());
					event.setEventType(BONJOURGATEWAYINNERTYPE);
					addInnerEvent(event);
				}
			}
			
			if(resultEvent.getOperTypeMap().containsKey(BeBonjourGatewayResultEvent.ELEMENT_TYPE_SERVICE)){
				int operType = resultEvent.getOperTypeMap().get(BeBonjourGatewayResultEvent.ELEMENT_TYPE_SERVICE);
				if (operType == BeBonjourGatewayResultEvent.OPER_TYPE_ADD ) {
					BonjourGatewayMonitoring bonjourGatewayMonitoring = QueryUtil.findBoByAttribute(BonjourGatewayMonitoring.class,
							"macAddress", apMac, owner.getId(),new BonjourGatewayMonitoringAction());
					if (bonjourGatewayMonitoring == null) {
						bonjourGatewayMonitoring = new BonjourGatewayMonitoring();
						bonjourGatewayMonitoring.setMacAddress(apMac);
						bonjourGatewayMonitoring.setHostName(simpleHiveAp.getHostname());
						bonjourGatewayMonitoring.setOwner(owner);
						bonjourGatewayMonitoring.setBonjourServiceDetails(resultEvent.getServiceInfos());
						QueryUtil.createBo(bonjourGatewayMonitoring);
					} else{
						bonjourGatewayMonitoring.setBonjourServiceDetails(
								getNewBonjourServiceDetails(bonjourGatewayMonitoring.getBonjourServiceDetails(), resultEvent.getServiceInfos()));
						QueryUtil.updateBo(bonjourGatewayMonitoring);
					}
				} else if( operType == BeBonjourGatewayResultEvent.OPER_TYPE_UPDATE){
					StringBuilder updateSql = new StringBuilder();
					updateSql.append("update BONJOUR_SERVICE_DETAIL set shared=?,ip4=?,action=?,vlanGroupName=?,shareRomoteBdd=?,port=?,text=?,vlan=? where type=? and name=? and host=? and macAddress=?");
					List<Object[]> paraList = new ArrayList<Object[]>();
					for (BonjourServiceDetail detail : resultEvent.getServiceInfos()) {
						Object[] objs = new Object[12];
						objs[0] = detail.isShared();
						objs[1] = detail.getIp4();
						objs[2] = detail.getAction();
						objs[3] = detail.getVlanGroupName().replace("'", "''");
						objs[4] = detail.getShareRomoteBdd().replace("'", "''");
						objs[5] = detail.getPort();
						objs[6] = detail.getText().replace("'", "''");
						objs[7] = detail.getVlan();
						objs[8] = detail.getType().replace("'", "''");
						objs[9] = detail.getName().replace("'", "''");
						objs[10] = detail.getHost().replace("'", "''");
						objs[11] = detail.getMacAddress();
						paraList.add(objs);
					}
					QueryUtil.executeBatchUpdate(updateSql.toString(), paraList);
				} else if(operType == BeBonjourGatewayResultEvent.OPER_TYPE_REMOVE) {
					//delete 
					StringBuilder deleteSql = new StringBuilder();
					deleteSql.append("delete from BONJOUR_SERVICE_DETAIL where type=? and name=? and host=? and macAddress=?");
					List<Object[]> paraList = new ArrayList<Object[]>();
					for (BonjourServiceDetail detail : resultEvent.getServiceInfos()) {
						Object[] objs = new Object[4];
						objs[0] = detail.getType().replace("'", "''");
						objs[1] = detail.getName().replace("'", "''");
						objs[2] = detail.getHost().replace("'", "''");
						objs[3] = detail.getMacAddress();
						paraList.add(objs);
					}	
					QueryUtil.executeBatchUpdate(deleteSql.toString(), paraList);
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeBonjourGatewayProcessor.handleBonjourGatewayResultEvent(): catch exception.", e);
		}
	}
	
	public static void removeReam(String realmId,Long domainId) throws Exception{
		List<BonjourGatewayMonitoring> bonjourGatewayMonitorings = QueryUtil.executeQuery(BonjourGatewayMonitoring.class, null,new FilterParams("realmId", realmId), domainId);
		if(bonjourGatewayMonitorings.size()<=1){
			QueryUtil.bulkRemoveBos(BonjourRealm.class, new FilterParams("realmId", realmId), domainId);
		}
	}
	
	private List<BonjourServiceDetail> getNewBonjourServiceDetails(List<BonjourServiceDetail> dbServices,List<BonjourServiceDetail> addServices){
		List<BonjourServiceDetail> bonjourServiceDetails = new ArrayList<>();
		bonjourServiceDetails.addAll(dbServices);
		if(bonjourServiceDetails.isEmpty()){
			bonjourServiceDetails.addAll(addServices);
			return bonjourServiceDetails;
		}
		for(BonjourServiceDetail service:addServices){
			if(!isContainsService(bonjourServiceDetails,service)){
				bonjourServiceDetails.add(service);
			}
		}
		return bonjourServiceDetails;
	}
	
	// if name and type are same, the service is same
	private boolean isContainsService(List<BonjourServiceDetail> serviceList,BonjourServiceDetail service){
		if(serviceList == null || serviceList.isEmpty()){
			return false;
		}
		for(int i = serviceList.size()-1;i>=0;i--){
			BonjourServiceDetail serviceDetail = serviceList.get(i);
			if(serviceDetail == null){
				//serviceList.remove(i);
				continue;
			}
			String name = serviceDetail.getName();
			String type = serviceDetail.getType();
			boolean isNameEqual = name == null ? null == service.getName() : name.equals(service.getName());
			boolean isTypeEqual = type == null ? null == service.getType() : type.equals(service.getType());
			if (isNameEqual && isTypeEqual){
				//update service 
				serviceDetail.setHost(service.getHost());
				serviceDetail.setIp4(service.getIp4());
//				serviceDetail.setIp6(service.getIp6());
				serviceDetail.setPort(service.getPort());
				serviceDetail.setShared(service.isShared());
				serviceDetail.setText(service.getText());
				serviceDetail.setVlan(service.getVlan());
				return true;
			}
		}
		
		return false;
	}
	// **************** BonjourGatewayThread end ****************************
	
	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(getClass().getSimpleName());
			DebugUtil
					.faultDebugInfo("BeBonjourGatewayProcessor.run(): Start collect interface/clients stats thread.");

			List<SimpleHiveAp> apList = CacheMgmt.getInstance()
					.getManagedApList();
			if (apList.isEmpty()) {
				return;
			}
			
			index = 0;
			for (SimpleHiveAp ap : apList) {
				if (NmsUtil.compareSoftwareVersion("5.1.0.0", ap.getSoftVer()) > 0
						|| ap.isSimulated() 
						|| ap.getManageStatus() != HiveAp.STATUS_MANAGED
						|| ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
					continue;
				}
				
				BeBonjourGatewayEvent event = new BeBonjourGatewayEvent();
				
				int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
				event.setSimpleHiveAp(ap);
				event.setSequenceNum(sequenceNum);
				event.buildPacket();
				int serialNum = HmBeCommunicationUtil.sendRequest(event);

				if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
					// connect to capwap closed
					DebugUtil
							.performanceDebugError("BeBonjourGatewayProcessor.run(): Send request failed, capwap connect closed.");
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_MONITORING,
							MgrUtil.getUserMessage("hm.system.log.be.interface.client.capwap.closed"));
					return;
				}
				if (++index == CYCLE_APNUM) {
					try {
						Thread.sleep(RELAXTIME);
					} catch (Exception e) {
						DebugUtil
								.performanceDebugWarn(
										"BeBonjourGatewayProcessor.run() catch exception: ",
										e);
					}

					index = 0;
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeBonjourGatewayProcessor.run() catch exception", e);
		} catch (Error e) {
			DebugUtil.performanceDebugError(
					"BeBonjourGatewayProcessor.run() catch error.", e);
		}
	}
	
	/**
	 * calculate timeout value for request statistics data
	 * 
	 * @param apNum
	 *            -
	 * @return -
	 */
	private int getTimeOutValue(int apNum) {
		return Math.max(BeCommunicationConstant.DEFAULTTIMEOUT, (100 + apNum /2));
	}

	public static final int	SYNCREQUEST_RESULT_SUCCESS		= 0;

	public static final int	SYNCREQUEST_RESULT_FULLFAIL		= 1;

	public static final int	SYNCREQUEST_RESULT_PARTIALFAIL	= 2;
	
	/**
	 * sync request bonjour gateway
	 * 
	 * @param apList
	 *            -
	 * @return 0: success, 1: full fail, 2: partial fail
	 */
	int syncRequestBonjourGateway(Collection<SimpleHiveAp> apList) {
		if (apList == null || apList.isEmpty()) {
			return 0;
		}

		List<BeCommunicationEvent> requestList = new ArrayList<>(apList.size());

		try {
			for (SimpleHiveAp ap : apList) {
				BeBonjourGatewayEvent event = new BeBonjourGatewayEvent();
				int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
				event.setSimpleHiveAp(ap);
				event.setSequenceNum(sequenceNum);
				event.buildPacket();
		
				requestList.add(event);
			}
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugWarn(
					"BeBonjourGatewayProcessor.syncRequestBonjourGateway(): build packet error", e);
			return SYNCREQUEST_RESULT_FULLFAIL;
		}

		List<BeCommunicationEvent> responseList = HmBeCommunicationUtil.sendSyncGroupRequest(
				requestList, getTimeOutValue(apList.size()));
		if (responseList == null) {
			DebugUtil
					.performanceDebugWarn("BeBonjourGatewayProcessor.syncRequestBonjourGateway(): request failed, return null.");
			return SYNCREQUEST_RESULT_FULLFAIL;
		}

		// cache all ap that request time out
		List<HiveAp> apList_timeOut = new ArrayList<>();

		for (BeCommunicationEvent communicationEvent : responseList) {
			switch (communicationEvent.getMsgType()) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT: {
				BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
				if(resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY){
					addEvent(communicationEvent);
					break;
				}
			}
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP: {
				// maybe connect close
				if (communicationEvent.getResult() != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
					apList_timeOut.add(communicationEvent.getAp());
				}

				break;
			}
			}
		}

		if (apList_timeOut.size() == requestList.size()) {
			return SYNCREQUEST_RESULT_FULLFAIL;
		} else if (!apList_timeOut.isEmpty()) {
			return SYNCREQUEST_RESULT_PARTIALFAIL;
		}

		return SYNCREQUEST_RESULT_SUCCESS;
	}

}
