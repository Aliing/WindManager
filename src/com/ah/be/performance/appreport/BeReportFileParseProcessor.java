package com.ah.be.performance.appreport;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.ReportFileCacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.ApReportData;
import com.ah.util.HibernateUtil;

public class BeReportFileParseProcessor implements AppReportConstants {
		
	private boolean isContinue = false;
		
	//private Map<String, AhClientSession> clientCache = new HashMap<String, AhClientSession>();
	
	//	private AhClientSession getClientSession(String clientMac) {
	//	if (clientCache.get(clientMac) != null) {
	//		return clientCache.get(clientMac);
	//	}
	//	AhClientSession client = ReportHelper.queryClientSession(clientMac);
	//	if (client != null) {
	//		clientCache.put(clientMac, client);
	//	}
	//	return client;
	//}
	
	public void shutDown () {
		isContinue = false;
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor execute shutDown and will stoping to collect reporting data.");
	}
	
	public void startTask () {
		if (isContinue) {
			BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor is already started.");
			return;
		}
		ReportCacheMgmt.getInstance().loadFromDb();
		isContinue = true;
		Thread fileParseThread = new FileParseThread();
		fileParseThread.start();
	}
	
	public class FileParseThread extends Thread {
		public void run() {
			while (isContinue) {
				if (!ReportCacheMgmt.getInstance().isEnableSystemL7Switch()) {
					try {
						Thread.sleep(DISABLE_L7_SLEEPING_TIME);
					} catch (InterruptedException e) {
					}
					continue;
				}
				//ReportHelper.mockData(true, false);
				long t1 = System.currentTimeMillis();
				File file = new File(ReportHelper.getReportFilePath());
				File[] subFiles = file.listFiles();
				boolean hasRemainFile = false;
				int totalFileNum = subFiles.length;
				if (subFiles.length > FILE_LIMIT_NUM) {
					totalFileNum = FILE_LIMIT_NUM;
					hasRemainFile = true;
				}
			    int deleteFileNum = 0;
                boolean dbOperFlag = false;
		        boolean needsDeleteFlag = false;
		        List<ApReportData> list = new ArrayList<ApReportData>();
		        BeLogTools.info(HmLogConst.M_PERFORMANCE, String.format("BeReportFileParseProcessor starting execute..... current file num = %d", totalFileNum));
		        for (int i = 0; i < totalFileNum; i++) {
					DataInputStream dis = null;
					try {
						if (subFiles[i].isDirectory()) {
							continue;
						}
						String apMac = ReportHelper.getApMacByFileName(subFiles[i].getName());
						short fileType = ReportHelper.getReportTypeByFileName(subFiles[i].getName());
						if (fileType != FILE_TYPE_HOUR && fileType != FILE_TYPE_SECOND) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor error ap file type. file type = " + fileType + " fileName = " + subFiles[i].getName());
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
							continue;
						}
						if (apMac.length() != 12) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor error ap macaddress. ap macaddress = " + apMac + " fileName = " + subFiles[i].getName());
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
							continue;
						}
						if (ReportFileCacheMgmt.getInstance().isExistFileName(subFiles[i].getName())) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor delete repeated filename. ap macaddress = " + apMac + " fileName = " + subFiles[i].getName());
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
							continue;
						}
						dis = new DataInputStream(new FileInputStream(subFiles[i]));
						if (dis.available() <= 2) {//filter empty file
							dis.close();
							dis = null;
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
							continue;
						}
						byte[] buffer = new byte[VERSION_LENGTH];
						dis.read(buffer);
						short version = ByteBuffer.wrap(buffer).getShort();
						if (version <= 0) {//filter error version file
							BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor unknow ap file version. file version = " + version + " fileName = " + subFiles[i].getName());
							dis.close();
							dis = null;
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
							continue;
						}
						AppDataCollectorHandler appDataHandler = ReportHelper.getAppDataCollectorHandler(fileType);
						buffer = new byte[RECORD_EVERY_LENGTH];
						int index = 0;
						while((index = (dis.read(buffer))) != -1) {
							if (index < RECORD_EVERY_LENGTH) {
								continue;
							}
							//ApReportData entity = getReportData(apMac, fileType, buffer);
							ApReportData entity = appDataHandler.getSingleReportData(apMac, buffer);
							list.add(entity);
							if (list.size() >= RECORD_BATCH_NUM) {
								dbOperFlag = appDataHandler.handToDataCollector(list);
								if (dbOperFlag == true) {
									needsDeleteFlag = true;
								}
								list.clear();
							}
						}
						if (list.size() > 0) {
							dbOperFlag = appDataHandler.handToDataCollector(list);
							if (dbOperFlag == true) {
								needsDeleteFlag = true;
							}
							list.clear();
						}
					} catch(Exception e) {
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileParseProcessor execute parse error.", e);
					} finally {
						if (dis != null) try {dis.close();} catch(Exception e){}
					}
					//add to ReportFileCache to avoid deal repeated file
					ReportFileCacheMgmt.getInstance().saveFileName(subFiles[i].getName());
					
					try {
						if (needsDeleteFlag) {
							if (ReportHelper.handleReportingFile(subFiles[i])) {
								deleteFileNum ++;
							}
						}
					} catch(Exception e) {
						//log("BeReportFileParseProcessor delete file error.", "error", e);
					}
				}
				long t2 = System.currentTimeMillis();
				BeLogTools.info(HmLogConst.M_PERFORMANCE, String.format("BeReportFileParseProcessor execute once successfully. cost time = %d, total file num = %d,  delete file num = %d ", (t2 - t1), totalFileNum, deleteFileNum));
				try {
					if (!hasRemainFile) {
						Thread.sleep(RUN_LONG_INTERVAL_TIME);
					}
					else {
						Thread.sleep(RUN_SHORT_INTERVAL_TIME);
					}
				} catch (InterruptedException e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE,  "BeReportFileParseProcessor thread sleep interrupted. " + e.getMessage());
				}
			}
		
	    }
	
	}
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("HM_ROOT=" + System.getenv("HM_ROOT"));
		System.out.println(ReportHelper.getReportFilePath());
		
		//remember copy class/meta-inf to bin
		HibernateUtil.init(false);
		BulkOperationProcessor dbProcessor = new BulkOperationProcessor();
		dbProcessor.start();
		
		BeReportFileParseProcessor processor = new BeReportFileParseProcessor();
		//ReportHelper.setLocalDebug(true);
		//ReportHelper.mockData(false, true);
		processor.startTask();
		//Thread.sleep(30000);
		//processor.shutDown();
	}

}