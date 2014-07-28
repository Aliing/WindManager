package com.ah.be.performance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.admin.util.EmailElement;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhReport;
import com.ah.ui.actions.Navigation;
import com.ah.util.MgrUtil;

public class AhPerformanceScheduleModule implements Runnable, QueryBo {

	public final static String fileDirPath = "/tmp/csv";
	public final static String fileDirPathCurrent = "/tmp/currentcsv";

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledFuture;
	
	public AhPerformanceScheduleModule() {

	}
	
	public void start() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		
		// clear the tmp/csv dir
		makeCsvDir();
		
		long remainMinute = (calendar.getTimeInMillis() - System
				.currentTimeMillis()) / 60000;
		if (scheduler == null || scheduler.isShutdown()){
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduledFuture = scheduler.scheduleAtFixedRate(this, remainMinute + 1,
					60L, TimeUnit.MINUTES);
			
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
				"<BE Thread> Performance schedule module - scheduler is running...");
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		if (makeCsvDirOnly()) {
			generalCsvFile();
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
		       "AhPerformanceScheduleModule.makeCsvDir():" + e.getMessage());
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
			String[] string_Path_Array = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = "cd " + fileDirPath + " && rm -rf *";
		
			Process process = Runtime.getRuntime().exec(string_Path_Array);
			// wait restart network end
			process.waitFor();
			if (process.exitValue() > 0) {
				String errorMsg = MgrUtil.getUserMessage("hm.system.log.ah.performance.schedule.restart.network.error");
				DebugUtil
			      .performanceDebugWarn(
			       "AhPerformanceScheduleModule.makeCsvDir():" + errorMsg);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
						HmSystemLog.FEATURE_MONITORING, errorMsg);
				return false;
			}
		} catch (Exception e) {
			DebugUtil
		      .performanceDebugWarn(
		       "AhPerformanceScheduleModule.makeCsvDir():" + e.getMessage());
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
			log.setUserOwner("scheduler report");
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

	public void generalCsvFile() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		List<AhReport> lstReport = QueryUtil.executeQuery(AhReport.class, null, null);
//		Map<Long, String> mapServerName = new HashMap<Long,String>();
//		Map<Long, String> mapMailFrom = new HashMap<Long,String>();
//
//		List<MailNotification> mailNotification = QueryUtil.executeQuery(MailNotification.class, null, null);
//
//		for(MailNotification mailObj : mailNotification){
//			mapServerName.put(mailObj.getOwner().getId(), mailObj.getServerName());
//			mapMailFrom.put(mailObj.getOwner().getId(), mailObj.getMailFrom());
//		}
		
		BePerformScheduleImpl bePerformScheduleImpl = new BePerformScheduleImpl();
		for (AhReport ahReport : lstReport) {
			AhReport lazyProfile = QueryUtil.findBoById(AhReport.class,
					ahReport.getId(), this);
			if (lazyProfile.getOwner().getRunStatus()!=HmDomain.DOMAIN_DEFAULT_STATUS) {
				continue;
			}
			if (lazyProfile.getRunScheduleTime(calendar)) {
				
				long reportStartTime = System.currentTimeMillis();
				
				bePerformScheduleImpl.excutePerformance(ahReport
						.getReportType(), ahReport.getId());
				mailCsvFile(lazyProfile, null);
				
				long diffTimer = System.currentTimeMillis() - reportStartTime;
				String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
				if (ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_VPNLATENCY)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_WANAVAILABILITY)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY)||
					ahReport.getReportType().equals(Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.report")
						+ MgrUtil.getUserMessage("report.reportList.name") + " (" + ahReport.getName() + "),"
						+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + ahReport.getReportTypeShowInGUI() + "),"
						+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + ahReport.getReportPeriodVpnString() + "),"
						+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + ahReport.getExcuteTypeString() + ")"
						+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used")+ diffTimerStr, lazyProfile.getOwner());
				} else {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.report")
							+ MgrUtil.getUserMessage("report.reportList.name") + " (" + ahReport.getName() + "),"
							+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + ahReport.getReportTypeShowInGUI() + "),"
							+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + ahReport.getReportPeriodString() + "),"
							+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + ahReport.getExcuteTypeString() + ")"
							+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used")+ diffTimerStr, lazyProfile.getOwner());
				}
				
//				String serverName = mapServerName.get(lazyProfile.getOwner().getId());
//				String mailFrom = mapMailFrom.get(lazyProfile.getOwner().getId());
//				if (serverName!=null && !serverName.equals("") && mailFrom !=null && !mailFrom.equals("")) {
//					mailCsvFile(lazyProfile,lazyProfile.getOwner().getDomainName(),serverName,mailFrom,null);
//				}
			}
		}
	}

//public static void mailCsvFile(AhReport ahReport,String ownerName, String serverName,String mailFrom,TimeZone tz) {
	public static void mailCsvFile(AhReport ahReport, TimeZone tz) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
			if (tz!=null){
				sf.setTimeZone(tz);
			} else {
				sf.setTimeZone(ahReport.getOwner().getTimeZone());
			}
			String mailFileName;
			if (ahReport.getReportType().equals("compliance") || 
					ahReport.getReportType().equals("pciCompliance")){
				mailFileName = ahReport.getReportTypeShow() + "-"  + ahReport.getName() + "-"+  sf.format(new Date()) + ".pdf";
			} else {
				mailFileName = ahReport.getReportTypeShow() + "-"  + ahReport.getName() + "-"+  sf.format(new Date()) + ".csv";
			}
			String filePath = fileDirPath + File.separator + ahReport.getOwner().getDomainName() + File.separator + mailFileName;
			File tmpFile = new File(filePath);
			if (tmpFile.exists()) {
//				SendMailUtil mailUtil = new SendMailUtil();
//				mailUtil.setSmtpServer(serverName);
//				mailUtil.setFromEmail(mailFrom);
//				mailUtil.addMailToAddr(ahReport.getEmailAddress());
//				mailUtil.setSubject("Schedule Report");
//				mailUtil.setText(mailFileName);
//				
//				mailUtil.attachfile(fileDirPath + File.separator + ownerName + File.separator + mailFileName);
//				mailUtil.startSend();
				
				EmailElement email = new EmailElement();
				email.setDomainName(ahReport.getOwner().getDomainName());
				email.setToEmail(ahReport.getEmailAddress());
				email.setSubject("Schedule Report");
				email.setMailContent(mailFileName);
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				email.setDetachedFileList(fileList);
				email.setMustBeSent(true);
				
				HmBeAdminUtil.sendEmail(email);
			}
		} catch (Exception e) {
			DebugUtil
		      .performanceDebugWarn(
		       "AhPerformanceScheduleModule.mailCsvFile(): Failed send mail! ",e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
					HmSystemLog.FEATURE_MONITORING, e.getMessage());
		}
	}

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
				"<BE Thread> Performance schedule module - scheduler is not terminated completely");
		}
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE, 
			"<BE Thread> Performance schedule module - scheduler is shutdown");
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AhReport) {
			AhReport ahReport = (AhReport) bo;
			if (ahReport.getOwner() != null) {
				ahReport.getOwner().getId();
				ahReport.getOwner().getDomainName();
			}
		}
		return null;
	}

}