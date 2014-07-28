package com.ah.be.performance;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

public class BeNetdumpCollectionProcessor {
	private final BlockingQueue<BeBaseEvent> eventQueue;
	private static final int eventQueueSize = 1000;	
	private static int uploadFileCount = 10;
	private static int totalFileNumber = 100;
	private static int retryNumber = 1;
	private boolean isContinue = false;
	private final NetdumpCollectionThread collectionThread;
	private final Map<Integer, CLIStatus> cliMap;
	private String filePath;
	private File oldFile;
	public	BeNetdumpCollectionProcessor() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		cliMap = new HashMap<Integer, CLIStatus>();
		filePath = AhDirTools.getNetdumpUploadDir();

		isContinue = true;
		
		uploadFileCount = Integer.parseInt(ConfigUtil.getConfigInfo(
				ConfigUtil.SECTION_PERFORMANCE,
				ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
		
		totalFileNumber = Integer.parseInt(ConfigUtil.getConfigInfo(
				ConfigUtil.SECTION_PERFORMANCE,
				ConfigUtil.KEY_NETDUMP_FILE_MAX_NUMBER, "300"));
		
		
		collectionThread = new NetdumpCollectionThread();
		collectionThread.setName("netdumpCollectionThread");
		collectionThread.start();		
	}
	private class CLIStatus {
		long timestamp;
		int retryNumber;
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public int getRetryNumber() {
			return retryNumber;
		}
		public void setRetryNumber(int retryNumber) {
			this.retryNumber = retryNumber;
		}
		
	}
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch(Exception e) {
			DebugUtil.performanceDebugError(
					"BeNetdumpCollectionProcessor.addEvent():Exception while add event to queue", e);
		}
	}

	public void startTask() {
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Netdump Collection Processor is running...");		
	}
	
	public boolean shutdown(boolean isOff) {
		if(isOff){
			isContinue = false;
			eventQueue.clear();
			BeBaseEvent stopThreadEvent = new BeBaseEvent();
			eventQueue.offer(stopThreadEvent);

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Application Report Collection Processor is shutdown");			
		}
		
		return true;
	}	
	public class NetdumpCollectionThread extends Thread{
		public void run(){	
			MgrUtil.setThreadName(collectionThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Netdump Collection thread is running...");
			while(isContinue) {
				try{
					int curLoadFileCount = 0;
					while(isContinue && curLoadFileCount < uploadFileCount) {					
						if(BeHTTPConnectionControl.getInstance().isFull()) {
							DebugUtil.performanceDebugWarn("BeNetdumpCollectionProcessor:HTTP connections is full, do purge the old HTTP connection which is happened 500s ago.");
							purgeSerialNum();
							sleep(1000);
							continue;
						}
						
						curLoadFileCount++;
						BeBaseEvent event = eventQueue.take();
						if(null == event)
							continue;				
						
						if(BeEventConst.COMMUNICATIONEVENTTYPE == event.getEventType()) {
							BeCommunicationEvent communicationEvent =(BeCommunicationEvent) event;
							switch(communicationEvent.getMsgType()){
							case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
								//judge if netdump file number is large than 100, if yes, purge the oldest one.
								purgeNetdump(filePath);		
								SimpleHiveAp hiveAp = (SimpleHiveAp)communicationEvent.getSimpleHiveAp();
								sleep(1000);//to avoid of HTTPS 401 error
								handleNetdumpUpload(hiveAp);
								break;
										
							case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
								if(BeCommunicationConstant.RESULTTYPE_SUCCESS != communicationEvent.getResult()) {
									synchronized(cliMap) {
										deleteNetdumpSerialNum(communicationEvent.getSerialNum());
									}
								}							
								break;
								
							case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
								BeCapwapCliResultEvent cliEvent = (BeCapwapCliResultEvent)communicationEvent;
								SimpleHiveAp hiveApRetry = (SimpleHiveAp)cliEvent.getSimpleHiveAp();
//								synchronized(cliMap) {
//									deleteNetdumpSerialNum(communicationEvent.getSerialNum());
//								}
								
								if(BeCommunicationConstant.RESULTTYPE_SUCCESS != cliEvent.getCliResult()){
									//log cli execute failed
									DebugUtil.performanceDebugWarn("There is Netdump file upload failed. errorcode="+ cliEvent.getErrorCode());
									if(true == updateNetdumpSerialNum(communicationEvent.getSerialNum())){
										//retransmission
										DebugUtil.performanceDebugWarn("HM will retry send command to HOS:"+hiveApRetry.getMacAddress());
										sleep(1000);
										handleNetdumpUpload(hiveApRetry, communicationEvent.getSerialNum());
									}
								}	
								else
								{
									synchronized(cliMap) {
										deleteNetdumpSerialNum(communicationEvent.getSerialNum());
									}
								}
								break;
								
							default:
								break;		
							}
						}
					}
					sleep(1000);
				}catch(Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeNetdumpCollectionProcessor.NetdumpCollectionThread.run() Exception in processor thread", e);
				}catch(Error e){
					DebugUtil.performanceDebugWarn(
							"BeNetdumpCollectionProcessor.NetdumpCollectionThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	private void handleNetdumpUpload(SimpleHiveAp hiveAp) {
		 
		int serialNum = sendUploadNotify(hiveAp);
		return;
	}
	
	private void handleNetdumpUpload(SimpleHiveAp hiveAp, int serialNum) {
		sendUploadNotify(hiveAp, serialNum);
	}
	private int sendUploadNotify(SimpleHiveAp hiveAp, int serialNum){
		String cli;
		//need update here, need get this info from cache
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		
		String proxy = hiveAp.getProxyName();
		int proxyPort = hiveAp.getProxyPort();
		String proxyLoginUser = hiveAp.getProxyUsername();
		String proxyLoginPwd = hiveAp.getProxyPassword();
		
		cli = AhCliFactory.getNetdumpViaHTTPS(host, hiveAp.getMacAddress(), userName, password, hiveAp.getMacAddress(), proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
		//BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,"http command:" + cli);
		return pushClis(hiveAp, new String[]{cli}, serialNum);
	}
	private int sendUploadNotify(SimpleHiveAp hiveAp) {
		String cli;
		//need update here, need get this info from cache
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		
		String proxy = hiveAp.getProxyName();
		int proxyPort = hiveAp.getProxyPort();
		String proxyLoginUser = hiveAp.getProxyUsername();
		String proxyLoginPwd = hiveAp.getProxyPassword();
		
		cli = AhCliFactory.getNetdumpViaHTTPS(host, hiveAp.getMacAddress(), userName, password, hiveAp.getMacAddress(), proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
		//BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,"http command:" + cli);
		return pushClis(hiveAp, new String[]{cli});
	}

	private int pushClis(SimpleHiveAp hiveAp, String[] cmdLines, int serialNumber) {
		// send cli event to ap
		BeCliEvent cliEvent = new BeCliEvent();

		cliEvent.setSimpleHiveAp(hiveAp);
		cliEvent.setClis(cmdLines);
		//cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		cliEvent.setSequenceNum(serialNumber);
		
		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send clis", e);
			return -1;
		}

//		synchronized(cliMap) {
//			addNetdumpSerialNum(cliEvent.getSequenceNum());
//		}		
		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent, 200);
	
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			DebugUtil.performanceDebugError("BeAppReportCollectionProcessor.pushClis(), Failed to send cli to ap:" + hiveAp.getMacAddress());
//			synchronized(cliMap) {
//				deleteNetdumpSerialNum(cliEvent.getSequenceNum());
//			}
			return -1;
		}

		return serialNum;
	}
	private int pushClis(SimpleHiveAp hiveAp, String[] cmdLines){
		// send cli event to ap
		BeCliEvent cliEvent = new BeCliEvent();

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
			addNetdumpSerialNum(cliEvent.getSequenceNum());
		}		
		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent, 200);
	
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			DebugUtil.performanceDebugError("BeAppReportCollectionProcessor.pushClis(), Failed to send cli to ap:" + hiveAp.getMacAddress());
			synchronized(cliMap) {
				deleteNetdumpSerialNum(cliEvent.getSequenceNum());
			}
			return -1;
		}

		return serialNum;
	}	
	
	private void addNetdumpSerialNum(int serialNumber)
	{
		BeHTTPConnectionControl.getInstance().add();
		CLIStatus cliStatus = new CLIStatus();
		cliStatus.setTimestamp(System.currentTimeMillis());
		cliStatus.setRetryNumber(0);
		if(null != cliMap)
		{
			cliMap.put(serialNumber, cliStatus);
		}
	}

	private void deleteNetdumpSerialNum(int serialNumber)
	{	
		if(null != cliMap.remove(serialNumber)) {
			BeHTTPConnectionControl.getInstance().delete();
		}
	}

	private boolean updateNetdumpSerialNum(int serialNumber)
	{
		CLIStatus cliStatus = cliMap.get(serialNumber);
		if(cliStatus.getRetryNumber() < retryNumber) {
			cliStatus.setRetryNumber(cliStatus.getRetryNumber() + 1);
			return true;
		}
		else
		{
			deleteNetdumpSerialNum(serialNumber);
			return false;
		}
	}
	public boolean isNetdumpSerialNum(int serialNumber)
	{
		if(null != cliMap.get(serialNumber))
			return true;
		else
			return false;
	}
	
	private void purgeSerialNum(){
		Iterator<Integer> it = cliMap.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Long time = cliMap.get(key).getTimestamp();
			if((System.currentTimeMillis() - time) > 500000) {
				it.remove();
				BeHTTPConnectionControl.getInstance().delete();
			}
		}
	}	
	
	private void purgeNetdump(String filePath) {
		//read all netdump file, get the oldest one, then delete it
		long curFileNumber = getFileNumber(filePath);
		if(curFileNumber >= totalFileNumber) {
			try{
				 oldFile.delete();//if fail to delete file, need return out, or will go to endless loop 
				 if(curFileNumber == getFileNumber(filePath)) {
					 oldFile = null;
					 return;
				 }
			}catch(Exception e){
				DebugUtil.performanceDebugError("BeAppReportCollectionProcessor.purgeNetdump(), Failed to delete the oldest file:" + oldFile.getName());
				oldFile = null;
				return;
			}
		
			oldFile = null;
			purgeNetdump(filePath);
		}
		return;
	}
	
    public long getFileNumber(String filePath){
    	File file = new File(filePath);
        long num = 0;
        File fileList[] = file.listFiles();
        num=fileList.length;
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                num = num + getFileNumber(fileList[i].getPath());
                num--;
            }
            else
            {
            	if(null == oldFile)
            	{
            		oldFile = fileList[i];
            	}
            	else
            	{
            		if(oldFile.lastModified() > fileList[i].lastModified()){
            			oldFile = fileList[i];
            		}
            			
            	}
            }
        }
        return num;
   }	
    
    public File getOldFile(){
    	return oldFile;
    }
    
	public static void main(String[] args) {
		BeNetdumpCollectionProcessor test = new BeNetdumpCollectionProcessor();
			long num = test.getFileNumber("d:\\test\\");
			System.out.println("totol file num:"+ num);
			System.out.println("oldest File name is:" + test.getOldFile().getAbsolutePath());
			test.purgeNetdump("d:\\test\\");
			
			num = test.getFileNumber("d:\\test\\");
			System.out.println("totol file num after purge:"+ num);

	}    
}