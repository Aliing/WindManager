package com.ah.be.performance;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.monitor.NewReportExporting;
import com.ah.util.bo.dashboard.DaExportPdfUtil.ExportPdfFilePathResponse;
import com.ah.util.MgrUtil;

public class BeNetworkReportScheduleModule implements Runnable {

	public final static String fileDirPath = "/tmp/dapdf";
	public final static String fileDirPathCurrent = "/tmp/dacurpdf";
	
	private int runThread = 0;
	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> scheduledFuture;
	
	private boolean genPdfSucc=false;
	
	public BeNetworkReportScheduleModule() {

	}
	
	public synchronized void addOrRemoveRunThread(int value){
		runThread = runThread + value;
	}
	
	public synchronized int getRunThread(){
		return runThread;
	}
	
	public void start() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		makeCsvDir();
		long remainMinute = (calendar.getTimeInMillis() - System
				.currentTimeMillis()) / 60000;
		if (scheduler == null || scheduler.isShutdown()){
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduledFuture = scheduler.scheduleAtFixedRate(this, remainMinute + 1,
					60L, TimeUnit.MINUTES);
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> NetworkReport schedule module - scheduler is running...");
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		if (makeCsvDirOnly()) {
			try {
				addOrRemoveRunThread(1);
				generalPDFFile();
				addOrRemoveRunThread(-1);
			} catch (Exception e) {
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
						HmSystemLog.FEATURE_MONITORING,  e.getMessage());
				DebugUtil.performanceDebugError(
			       "BeNetworkReportScheduleModule.generalPDFFile()", e);
				addOrRemoveRunThread(-1);
			}
		}
	}
	
	public boolean makeCsvDirOnly() {
		try {
			File tmpFileDir = new File(fileDirPath);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
		} catch (Exception e) {
				DebugUtil
			      .performanceDebugWarn(
			       "BeNetworkReportScheduleModule.makeCsvDir():" + e.getMessage());
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
						HmSystemLog.FEATURE_MONITORING, e.getMessage());
				return false;
		}
		return true;
	}

	public boolean makeCsvDir() {
		try {
			File tmpFileDir = new File(fileDirPath);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			if (getRunThread()<=0) {
				String[] string_Path_Array = new String[3];
				string_Path_Array[0] = "bash";
				string_Path_Array[1] = "-c";
				string_Path_Array[2] = "cd " + fileDirPath + " && rm -rf *";
			
				Process process = Runtime.getRuntime().exec(string_Path_Array);
				// wait restart network end
				process.waitFor();
				if (process.exitValue() > 0) {
					String errorMsg = MgrUtil.getUserMessage("hm.system.log.be.network.report.schedule.restart.network.error");
					DebugUtil
				      .performanceDebugWarn(
				       "BeNetworkReportScheduleModule.makeCsvDir():" + errorMsg);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
							HmSystemLog.FEATURE_MONITORING, errorMsg);
					return false;
				}
			}
		} catch (Exception e) {
			DebugUtil
		      .performanceDebugWarn(
		       "BeNetworkReportScheduleModule.makeCsvDir():" + e.getMessage());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
					HmSystemLog.FEATURE_MONITORING, e.getMessage());
			return false;
		}

		return true;
	}
	
	public void generateAuditLog(short arg_Status, String arg_Comment, HmDomain dm) {
		HmAuditLog log = new HmAuditLog();
		log.setStatus(arg_Status);
		log.setOpeationComment(arg_Comment);
		log.setHostIP("127.0.0.1");
		try {
			log.setUserOwner("admin");
			log.setOwner(dm);
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(dm != null ? dm.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.info(HmLogConst.M_GUIAUDIT, "[" + log.getHostIP() + " "
					+ log.getOwner() + "." + log.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void generalPDFFile() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		List<AhDashboard> lstReport = QueryUtil.executeQuery(AhDashboard.class, null, 
				new FilterParams("owner.runStatus=:s1 and daType=:s2 and reportScheduleStatus=:s3",
						new Object[]{HmDomain.DOMAIN_DEFAULT_STATUS, AhDashboard.DASHBOARD_TYPE_REPORT,
						AhDashboard.REPORT_STATUS_SCHEDULED}));
		
		for (AhDashboard ahReport : lstReport) {
			if (ahReport.isRunSchedule(calendar.getTimeInMillis())) {
				long reportStartTime = System.currentTimeMillis();
				setGenPdfSucc(false);
				NewReportExporting exportProxy = new NewReportExporting(true, ahReport, ahReport.getOwner(), ahReport.getTz());
				exportProxy.run(new ExportPdfFilePathResponse(){
					public void respond(){
							String filePath=this.getResponse();
							if(filePath!=null&&!"".equals(filePath))
							{
								File pdfFile=new File(filePath);
								if(pdfFile.exists())
								{
									setGenPdfSucc(true);
								}
							}
						}
					});
//				boolean ret = BeNetworkReportScheduleImpl.excutePerformance(ahReport
//						,true, ahReport.getTz());
				if (genPdfSucc) {
//					mailCsvFile(ahReport, true, ahReport.getReEmailAddress());
				
					long diffTimer = System.currentTimeMillis() - reportStartTime;
					String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
	
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.network.summary.report")
							+ MgrUtil.getUserMessage("report.reportList.name") + " (" + ahReport.getDashName() + "),"
							+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + ahReport.getReportScheduleStatusString() + ")"
							+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr, ahReport.getOwner());
					
//				} else {
//					long diffTimer = System.currentTimeMillis() - reportStartTime;
//					String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
//					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.run.network.summary.report")
//							+ MgrUtil.getUserMessage("report.reportList.name") + "(" + ahReport.getName() + "),"
//							+ MgrUtil.getUserMessage("report.reportList.excuteType") + "(" + ahReport.getExcuteTypeString() + ")"
//							+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr, ahReport.getOwner());
				}
			}
		}
	}

//	public static void mailCsvFile(AhDashboard ahReport, boolean scheduled, String emailAddress) {
//		try {
//			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//			sf.setTimeZone(ahReport.getTz());
//			String mailFileName;
//			mailFileName = "NetworkSummary" + "_"  + ahReport.getDashName() + "_" + sf.format(new Date()) + ".pdf";
//			String filePath;
//			if (scheduled) {
//				filePath= fileDirPath + File.separator + ahReport.getOwner().getDomainName() + File.separator + mailFileName;
//			} else {
//				filePath= fileDirPathCurrent + File.separator + ahReport.getOwner().getDomainName() + File.separator + mailFileName;
//			}
//			
//			File tmpFile = new File(filePath);
//			if (tmpFile.exists()) {
//				EmailElement email = new EmailElement();
//				email.setDomainName(ahReport.getOwner().getDomainName());
//				email.setToEmail(emailAddress);
//				if (scheduled) {
//					email.setSubject("Schedule Network Summary Report--" + ahReport.getDashName());
//				} else {
//					email.setSubject("Current Network Summary Report--" + ahReport.getDashName());
//				}
//				email.setMailContent(mailFileName);
//				List<String> fileList = new ArrayList<String>();
//				fileList.add(filePath);
//				email.setDetachedFileList(fileList);
//				email.setMustBeSent(true);
//				
//				HmBeAdminUtil.sendEmail(email);
//			}
//		} catch (Exception e) {
//			DebugUtil
//		      .performanceDebugWarn(
//		       "BeNetworkReportScheduleModule.mailCsvFile(): Failed send mail! ",e);
//			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
//					HmSystemLog.FEATURE_MONITORING, e.getMessage());
//		}
//	}
	
//	public static void mailCsvFileCurrent(AhNewReport ahReport,String mailFileName) {
//		try {
//
//			String filePath = fileDirPathCurrent + File.separator + ahReport.getOwner().getDomainName() + File.separator + mailFileName;
//			
//			File tmpFile = new File(filePath);
//			if (tmpFile.exists()) {
//				EmailElement email = new EmailElement();
//				email.setDomainName(ahReport.getOwner().getDomainName());
//				email.setToEmail(ahReport.getEmailAddress());
//				email.setSubject("Network Summary Report--" + ahReport.getName());
//				email.setMailContent(mailFileName);
//				List<String> fileList = new ArrayList<String>();
//				fileList.add(filePath);
//				email.setDetachedFileList(fileList);
//				email.setMustBeSent(true);
//				
//				HmBeAdminUtil.sendEmail(email);
//			}
//		} catch (Exception e) {
//			DebugUtil
//		      .performanceDebugWarn(
//		       "BeNetworkReportScheduleModule.mailCsvFile(): Failed send mail! ",e);
//			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
//					HmSystemLog.FEATURE_MONITORING, e.getMessage());
//		}
//	}

	public void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		try {
			if (!scheduledFuture.isDone()) {
				scheduledFuture.cancel(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being cancelled.
		if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> NetworkReport schedule module - scheduler is not terminated completely");
		}
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
			"<BE Thread> NetworkReport schedule module - scheduler is shutdown");
	}

	public void setGenPdfSucc(boolean genPdfSucc) {
		this.genPdfSucc = genPdfSucc;
	}

}