package com.ah.be.performance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAppReportCollectionInfoEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class BeAppReportCollectionProcessor  implements Runnable {
	
	private final BlockingQueue<BeBaseEvent> eventQueue;
	private static final int eventQueueSize = 10000;
	private static int uploadFileCount = 10;
	private boolean isContinue = false;
	private final AppReportCollectionThread collectionThread;
	private final HandleFileQueueThread handleFileQueueThread;
	private final Map<String, SimpleHiveAp> fileTotalMap;
	private final Map<Integer, Integer> cliMap;
	private ScheduledExecutorService scheduler;
	private static int CONNECTION_TIMEOUT = 500;
	private static int TIMER_INTERVAL = 60;
	
	public	BeAppReportCollectionProcessor(){	
		fileTotalMap = new HashMap<String, SimpleHiveAp>(eventQueueSize);
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		cliMap = new HashMap<Integer, Integer>();
		
		uploadFileCount = Integer.parseInt(ConfigUtil.getConfigInfo(
				ConfigUtil.SECTION_PERFORMANCE,
				ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
				
		isContinue = true;
		
		collectionThread = new AppReportCollectionThread();
		collectionThread.setName("appReportCollectionThread");
		collectionThread.start();
		
		handleFileQueueThread = new HandleFileQueueThread();
		handleFileQueueThread.setName("handleFileQueueThread");
		handleFileQueueThread.start();
	}
	
	public void startTask() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(this, TIMER_INTERVAL, TIMER_INTERVAL, TimeUnit.SECONDS);
        }	    
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Application Report Collection Processor is running...");		
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			if(false == eventQueue.offer(event)) {
			    DebugUtil.performanceDebugError(
	                    "BeAppReportCollectionProcessor.addEvent():Queue is full");
			}
		} catch(Exception e) {
			DebugUtil.performanceDebugError(
					"BeAppReportCollectionProcessor.addEvent():Exception while add event to queue", e);
		}
	}

    @Override
    public void run() {
        MgrUtil.setTimerName(this.getClass().getSimpleName());
        try {
            purgeSerialNum();
        } catch (Exception e) {
            DebugUtil
                    .commonDebugWarn(
                            "BeAppReportCollectionProcessor.run(): Error occur when running the task of clear timeout request, Exception message: ",
                            e);
        } catch (Error e) {
            DebugUtil
            .commonDebugWarn(
                    "BeAppReportCollectionProcessor.run(): Error occur when running the task of clear timeout request, Error: ",
                    e);
        }
    }
	
	public class AppReportCollectionThread extends Thread{
		public void run(){
			MgrUtil.setThreadName(collectionThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Applicatons Report Collection Process Thread is running...");
			
			while(isContinue) {
				try{
					if (!ReportCacheMgmt.getInstance().isEnableSystemL7Switch()) {
					      sleep(30000);
					     continue;
					}					
					BeBaseEvent event = eventQueue.take();
					if(null == event)
						continue;
					
					if(event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent =(BeCommunicationEvent) event;
						switch(communicationEvent.getMsgType()){
						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT:
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent)communicationEvent;
							if(resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPREPORTCOLLECTIONINFO){
								BeAppReportCollectionInfoEvent infoEvent = (BeAppReportCollectionInfoEvent)resultEvent;
								
								//judge whether the AP is in managed status
								SimpleHiveAp hiveAp = infoEvent.getSimpleHiveAp();
								if(null == hiveAp || HiveAp.STATUS_MANAGED != hiveAp.getManageStatus())
									continue;
								
								//handle application report file
								for(String file : infoEvent.getFiles()){
									if(null != file && 0 < file.length()) {
										//fileTotalMap.put(file, infoEvent.getAp());
										synchronized(fileTotalMap) {
											fileTotalMap.put(file, hiveAp);
										}
									}
								}							
							}							
							break;
						case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
							if(BeCommunicationConstant.RESULTTYPE_SUCCESS != communicationEvent.getResult()) {
								synchronized(cliMap) {
									deleteL7SerialNum(communicationEvent.getSerialNum());
								}
							}							
							break;
						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
							BeCapwapCliResultEvent cliEvent = (BeCapwapCliResultEvent)communicationEvent;
							synchronized(cliMap) {
								deleteL7SerialNum(communicationEvent.getSerialNum());
							}
							
							if(BeCommunicationConstant.RESULTTYPE_SUCCESS != cliEvent.getCliResult()){
								//log cli execute failed
								DebugUtil.performanceDebugWarn("There is L7 report file upload failed. errorcode="+ cliEvent.getErrorCode());
							}													
							break;
						default:
							break;
						}	
					}
					
				}catch(Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeAppReportCollectionProcessor.AppReportCollectionThread.run() Exception in processor thread", e);
				}catch(Error e){
					DebugUtil.performanceDebugWarn(
							"BeAppReportCollectionProcessor.AppReportCollectionThread.run() Error in processor thread", e);
				}
			}
		}
	}

	private void addL7SerialNum(int serialNumber)
	{
		BeHTTPConnectionControl.getInstance().add();
		if(null != cliMap)
		{
			cliMap.put(serialNumber, CONNECTION_TIMEOUT);
		}
	}
	
	private void purgeSerialNum(){
		Iterator<Integer> it = cliMap.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Integer value = cliMap.get(key);
			value = value - TIMER_INTERVAL;
			cliMap.put((Integer)key, value);
			
			if(value <= 0) {
			    it.remove();
			    BeHTTPConnectionControl.getInstance().delete();
			}
		}
	}
	public static void main(String[] args) {
		BeAppReportCollectionProcessor test = new BeAppReportCollectionProcessor();
		for(int i = 0; i < 100; i++) {
		    test.addL7SerialNum(i);
		}
		test.purgeSerialNum();
		test.purgeSerialNum();
	}
	public boolean isL7SerialNum(int serialNumber)
	{
		if(null != cliMap.get(serialNumber))
			return true;
		else
			return false;
	}
	
	private void deleteL7SerialNum(int serialNumber)
	{	
		if(null != cliMap.remove(serialNumber)) {
			BeHTTPConnectionControl.getInstance().delete();
		}
	}
	
	public class HandleFileQueueThread extends Thread{
		public void run(){
			MgrUtil.setThreadName(handleFileQueueThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Handle File Queue thread is running...");
			
			while(isContinue) {
				try{
					if (!ReportCacheMgmt.getInstance().isEnableSystemL7Switch()) {
					      sleep(30000);
					     continue;
					}
					
					int curLoadFileCount = 0;
					
					while(uploadFileCount > curLoadFileCount) {
    					Map.Entry<String, SimpleHiveAp> entry;
    					String fileName = new String();
    					SimpleHiveAp hiveAp = new SimpleHiveAp();
    					synchronized(fileTotalMap) {
    					    Iterator<Map.Entry<String, SimpleHiveAp>> iter = fileTotalMap.entrySet().iterator();
    						if(iter.hasNext()) {						    
    						    entry = (Map.Entry<String, SimpleHiveAp>) iter.next();
    	                        fileName = (String)entry.getKey();
    	                        hiveAp = (SimpleHiveAp)entry.getValue();
    	                        iter.remove();	                        
    						}
    					}
    					
    					if(fileName.isEmpty()){
    					    //sleep(1000);//TODO need confirm the time interval
    					    break;
    					}
    					
						if(BeHTTPConnectionControl.getInstance().isFull()) {
							DebugUtil.performanceDebugWarn("HTTP connections is full, do purge the old HTTP connection which is happened 500s ago.");
							sleep(100);
							continue;
						}
						//below is for clock-hour-report case
						//query db, check current AP is still in collect last hour status
						//if no, should send stop cli command to HOS
						try {
							if(-1 != fileName.indexOf(".hpr")) {
								List<AhDashboardAppAp> hiveAps = QueryUtil.executeQuery(
										AhDashboardAppAp.class, null,
										new FilterParams("apMac",
												hiveAp.getMacAddress()));
								if( null == hiveAps || 0 >= hiveAps.size()) {
									stopAppReportCollect(new String[] {hiveAp.getMacAddress()});
									continue;
								}
							}
							
							//send notify information to specified AP, let AP do upload file
							curLoadFileCount++;
							int serialNum = sendUploadNotify(hiveAp, fileName);
							if(-1 == serialNum) {
				                 synchronized(fileTotalMap) {
				                     fileTotalMap.put(fileName, hiveAp);
				                 }
							}
						} catch(Exception e) {
							DebugUtil.performanceDebugError(
									"BeAppReportCollectProcessor.HandleFileQueueThread.run() Exception, ignore filename:" + fileName, e);
						}				
    					
					}
					sleep(1 * 1000);//delay 1 second, then send upload request message	
				} catch (Exception e) {
					DebugUtil.performanceDebugError(
							"BeAppReportCollectProcessor.HandleFileQueueThread.run() Exception", e);
				}
			}			
		}
	}
	
	public boolean shutdown(boolean isOff) {
		if(isOff){
			isContinue = false;
			eventQueue.clear();
			BeBaseEvent stopThreadEvent = new BeBaseEvent();
			eventQueue.offer(stopThreadEvent);
	        if (scheduler != null && !scheduler.isShutdown()) {
	            scheduler.shutdown();
	        }
	        
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Application Report Collection Processor is shutdown");			
		}
		
		return true;
	}
	
	private int sendUploadNotify(SimpleHiveAp hiveAp, String fileName) {
		String cli;
		//need update here, need get this info from cache
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		
		String proxy = hiveAp.getProxyName();
		int proxyPort = hiveAp.getProxyPort();
		String proxyLoginUser = hiveAp.getProxyUsername();
		String proxyLoginPwd = hiveAp.getProxyPassword();
		
		cli = AhCliFactory.getAppReportViaHTTPS(host, hiveAp.getMacAddress(), userName, password, fileName, proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
		//BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,"http command:" + cli);
		return pushClis(hiveAp, new String[]{cli});
	}
	
	private int pushClis(SimpleHiveAp hiveAp, String[] cmdLines){
		// send cli event to ap
		BeCliEvent cliEvent = new BeCliEvent();
		//cliEvent.setAp(hiveAp);
		cliEvent.setSimpleHiveAp(hiveAp);
		cliEvent.setClis(cmdLines);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		
		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send clis", e);
			return -1;
		}

		synchronized(cliMap) {
			addL7SerialNum(cliEvent.getSequenceNum());
		}		
		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent, 200);
	
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			DebugUtil.performanceDebugError("BeAppReportCollectionProcessor.pushClis(), Failed to send cli to ap:" + hiveAp.getMacAddress());
			synchronized(cliMap) {
				deleteL7SerialNum(cliEvent.getSequenceNum());
			}
			return -1;
		}
		//cliExeStat.addCLI(serialNum);		
		return serialNum;
	}	
	
	public void startAppReportCollect(String[] szMacAddress) {
		//Get time interval from db
		int interval = 600;
		int collect_interval = 60;
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
				null, null);
		
		if (!list.isEmpty()) {
			//need update here
			interval = list.get(0).getReportIntervalMinute()*60;
			if(interval <= 0)
				interval = 600;
		}

		//Get Ap accordint to Mac Address
		String cli = new String();	

		for(int i = 0; i < szMacAddress.length; i++){
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(szMacAddress[i]);
			cli = AhCliFactory.startAppReportCollectCli(collect_interval, interval);
			pushCli(simpleHiveAp, cli);
		}

		return;
	}
	
	public void stopAppReportCollect(String[] szMacAddress) {
		for(int i = 0; i < szMacAddress.length; i++) {
			String cli = new String();
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(szMacAddress[i]);
			cli = AhCliFactory.stopAppReportCollectCli();
			
			pushCli(simpleHiveAp, cli);
		}
		
		return;
	}
	
	private void pushCli(SimpleHiveAp simpleHiveAp, String cmdLine){
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setSimpleHiveAp(simpleHiveAp);
		cliEvent.setClis(new String[] {cmdLine});
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		
		try {
			cliEvent.buildPacket();
		} catch(BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send clis", e);
		}
		
		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			DebugUtil.performanceDebugError("BeAppReportCollectionProcessor.pushCli(), Failed to send cli to ap:" + simpleHiveAp.getMacAddress());
		}
				
		return;
	}
}