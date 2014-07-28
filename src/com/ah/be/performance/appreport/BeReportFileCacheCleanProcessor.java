package com.ah.be.performance.appreport;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.ReportFileCacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;


public class BeReportFileCacheCleanProcessor implements AppReportConstants {
		
	private boolean isContinue = true;
	
	private ScheduledExecutorService scheduler;
					
	public void shutDown () {
		isContinue = false;
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportFileCacheCleanProcessor execute shutDown and will stoping to clean the report file cache data.");
	}
	
	public void startTask () {
		isContinue = true;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(new CacheCleanThread(), 60, 60, TimeUnit.MINUTES);
		}
		
	}
			
	public class CacheCleanThread extends Thread {
		public void run() {
			if (isContinue) {
				try{
					MgrUtil.setTimerName(getClass().getSimpleName());
					//long t1 = System.currentTimeMillis();
					//BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportFileCacheCleanProcessor before clean the data. current cache size = " + ReportCacheMgmt.getInstance().size());
					ReportFileCacheMgmt.getInstance().cleanHistoryData();
					//long t2 = System.currentTimeMillis();
					//BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportFileCacheCleanProcessor after clean the data. current cache size = " + ReportCacheMgmt.getInstance().size() + " cost time = " + (t2 - t1));
				}catch(Exception e){
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportFileCacheCleanProcessor execute cache collect exception: " + e.getMessage(), e);
				}
			}
		
	    }
	
	}
	
	public static void main(String[] args) throws Exception {
		ReportFileCacheMgmt cache = ReportFileCacheMgmt.getInstance();
		cache.saveFileName("111111.lpr");
		cache.saveFileName("222222.hpr");
		System.out.println(cache.size());
		System.out.println(cache.isExistFileName("111111.lpr"));
		Thread.sleep(3000);
		ReportFileCacheMgmt.getInstance().cleanHistoryData();
		System.out.println(cache.size());
		System.out.println(cache.isExistFileName("111111.lpr"));
//		BeReportFileCacheCleanProcessor processor = new BeReportFileCacheCleanProcessor();
//		processor.startTask();
	}

}