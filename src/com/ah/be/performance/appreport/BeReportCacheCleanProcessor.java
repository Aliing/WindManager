package com.ah.be.performance.appreport;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;


public class BeReportCacheCleanProcessor implements AppReportConstants {
		
	private boolean isContinue = true;
	
	private ScheduledExecutorService scheduler;
					
	public void shutDown () {
		isContinue = false;
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportCacheCleanProcessor execute shutDown and will stoping to clean the report cache data.");
	}
	
	public void startTask () {
		isContinue = true;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(new CacheCleanThread(), 5, 5, TimeUnit.MINUTES);
		}
		
	}
			
	public class CacheCleanThread extends Thread {
		public void run() {
			if (isContinue) {
				try{
					MgrUtil.setTimerName(getClass().getSimpleName());
					long t1 = System.currentTimeMillis();
					BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportCacheCleanProcessor before clean the data. current cache size = " + ReportCacheMgmt.getInstance().size());
					ReportCacheMgmt.getInstance().writeDbFromClientCache();
					ReportCacheMgmt.getInstance().cleanHistoryData();
					long t2 = System.currentTimeMillis();
					BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeReportCacheCleanProcessor after clean the data. current cache size = " + ReportCacheMgmt.getInstance().size() + " cost time = " + (t2 - t1));
				}catch(Exception e){
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeReportCacheCleanProcessor execute cache collect exception: " + e.getMessage());
				}
			}
		
	    }
	
	}
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 1000; i++) {
			ClientInfoBean bean = new ClientInfoBean();
			if (i >= 800 && i < 900) {
				bean.setOnline(true);
				//bean.setTimeout(System.currentTimeMillis());
			}
			else if (i >= 900) {
				bean.setOnline(false);
				bean.setTimeout(System.currentTimeMillis() + 7200 * 1000);
			}
			else {
				bean.setOnline(false);
				bean.setTimeout(System.currentTimeMillis());
			}
			//bean.setOnline(true);
			
			ReportCacheMgmt.getInstance().saveClientInfo("client" + i, bean);
		}
		ReportHelper.setLocalDebug(true);
		BeReportCacheCleanProcessor processor = new BeReportCacheCleanProcessor();
		processor.startTask();
	}

}