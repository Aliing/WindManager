package com.ah.be.performance;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeDataCollectionInfoEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.upload.UploadHandler;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.util.http.HttpFileTransferUtil;

public class BeDataCollectionProcessor {
	
	private final BlockingQueue<BeBaseEvent> eventQueue;

	private final BlockingQueue<Object[]> deleteQueue;

	private static final int eventQueueSize = 10000;
	
	private static final long DELAY_TIME = 1;
	
	private static final int FILE_TYPE_AP_DATA_CLLECTION = 1;
	
	private static final String DATA_FILE_SUFFIX = ".dcm";
	
	private boolean isContinue = false;
	
	private boolean isCollectable = false;
	
	private DataCollectionThread collectionThread;

	private DeleteFileThread deleteFileThread;

	private ScheduledExecutorService scheduler;

	public BeDataCollectionProcessor() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		deleteQueue = new LinkedBlockingQueue<Object[]>(eventQueueSize);

		isContinue = true;

//		deleteFileThread = new DeleteFileThread();
//		deleteFileThread.setName("deleteFileProcessThread");
//		deleteFileThread.start();

		// data collection process thread
//		collectionThread = new DataCollectionThread();
//		collectionThread.setName("dataCollectionProcessThread");
//		collectionThread.start();
	}
	
	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeDataCollectionProcessor.addEvent(): Exception while add event to queue", e);
		}
	}
	
	public void startTask() {
		// get initialization setting
		LicenseServerSetting lserverInfo = HmBeActivationUtil.getLicenseServerInfo();
		isCollectable = lserverInfo.isSendStatistic();

		// from 6.0r1 we no longer need data collection
		isCollectable = false;

		if (!isCollectable)return;
		
		// upload process timer
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			
			scheduler.scheduleWithFixedDelay(new FileUploadThread(), 0, DELAY_TIME, TimeUnit.HOURS);
		}
		
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Data Collection Processor is running...");
	}
	
	public boolean shutdown(boolean isOff) {
		if (isOff) {
			isContinue = false;
			eventQueue.clear();
			deleteQueue.clear();
			BeBaseEvent stopThreadEvent = new BeBaseEvent();
			eventQueue.offer(stopThreadEvent);
			deleteQueue.offer(new String[]{});

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Data Collection Processor is shutdown");
		}
		
		if (!isCollectable)return true;
		
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		return true;
	}
	
	public class FileUploadThread implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Data Collection File Upload is running...");
			
			if (isContinue && isCollectable) {
				try {
					String dataDir = AhDirTools.getDataCollectionUploadDir();
					File dir = new File(dataDir);
					if (dir.isDirectory()) {
						String[] files = dir.list(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(DATA_FILE_SUFFIX);
							}
						});
						
						if (files != null && files.length > 0) {
							if (!dataDir.endsWith(File.separator))dataDir += File.separator;
							Map<String, List<String>> perApFiles = new HashMap<String, List<String>>();
							for (String file : files) {
								String[] name = file.split("_");
								if (name.length <= 0 || name[0].length() != 12) continue;

								if (!perApFiles.containsKey(name[0])) {
									List<String> apFile = new ArrayList<String>();
									apFile.add(dataDir + file);
									perApFiles.put(name[0], apFile);
								} else {
									perApFiles.get(name[0]).add(dataDir + file);
								}
							}
							
							for (String apMac : perApFiles.keySet()) {
								String urlParam = UploadHandler.REQ_PARAM_FILE_TYPE
										+ "="
										+ FILE_TYPE_AP_DATA_CLLECTION
										+ "&"
										+ UploadHandler.REQ_PARAM_AP_NODE_ID
										+ "=" + apMac;
								
								List<String> filesUpload = perApFiles.get(apMac);
								if (filesUpload != null && filesUpload.size() > 0) {
									HttpFileTransferUtil.uploadFilesToLS(filesUpload.toArray(new String[filesUpload.size()]), urlParam, true);
								}
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.FileUploadThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.FileUploadThread.run() Error in processor thread", e);
				}
			}
		}
	}

	private class DeleteFileThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(deleteFileThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Delete Data Collection files Process Thread is running...");

			while (isContinue) {
				try {
					// take() method blocks
					Object[] event = deleteQueue.take();
					if (null == event || event.length != 2)
						continue;

					sleep(60 * 1000);

					deleteFile((HiveAp)(event[0]), (String)(event[1]));
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.DeleteFileThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.DeleteFileThread.run() Error in processor thread", e);
				}
			}
		}
	}

	public class DataCollectionThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setThreadName(collectionThread, this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Data Collection Process Thread is running...");
			
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
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_DATACOLLECTIONINFO) {
								BeDataCollectionInfoEvent infoEvent = (BeDataCollectionInfoEvent)resultEvent;
								// get ap info from db
								List<HiveAp> hiveAps = QueryUtil.executeQuery(
										HiveAp.class, null,
										new FilterParams("macaddress",
												resultEvent.getApMac()));
								
								if (hiveAps == null || hiveAps.size() <= 0) continue;
								
								// save files to hm
								HiveAp hiveAp = hiveAps.get(0);
								for (String file : infoEvent.getFiles()) {
									if (file != null && file.length() > 0) {
										if (isCollectable) {
											saveFile(hiveAp, file);
										}
										deleteQueue.add(new Object[]{hiveAp, file});
//										deleteFile(hiveAp, file);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.DataCollectionThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeDataCollectionProcessor.DataCollectionThread.run() Error in processor thread", e);
				}
			}
		}
	}
	
	private boolean saveFile(HiveAp hiveAp, String fileName) {
		String cli;
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		
		if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			String saveFileName = hiveAp.getMacAddress()+"_"+AhDateTimeUtil.getFormatDateTime(System.currentTimeMillis(), "yyyyMMdd_HHmmss_sss")+".dcm";
			
			cli = AhCliFactory.getSaveCollectFileViaSSH(host, userName, password, fileName, saveFileName);
		} else {
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			
			cli = AhCliFactory.getSaveCollectFileViaHTTPS(host, hiveAp.getMacAddress(), userName, password, fileName, proxy,
					proxyPort, proxyLoginUser, proxyLoginPwd);
		}
		
		return pushClis(hiveAp, new String[]{cli});
	}
	
	private boolean deleteFile(HiveAp hiveAp, String fileName) {
		return pushClis(hiveAp, new String[]{AhCliFactory.getClearCollectFileCli(fileName)});
	}
	
	private boolean pushClis(HiveAp hiveAp, String[] cmdLines){
		// send cli event to ap
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(hiveAp);
		cliEvent.setClis(cmdLines);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		
		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send clis", e);
			return false;
		}
		
		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent);
		if (serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			DebugUtil.performanceDebugError("BeDataCollectionProcessor.pushClis(), Failed to send cli to ap:" + hiveAp.getMacAddress());
					return false;
				}
				
		return true;
	}
	
	public boolean isCollectable() {
		return isCollectable;
	}
	
	public void setCollectable(boolean isCollectable) {
		if (isCollectable) {
			if (!this.isCollectable) {
				startTask();
			}
		} else {
			if (this.isCollectable) {
				shutdown(false);
			}
		}
		
		this.isCollectable = isCollectable;
	}

}