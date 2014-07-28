package com.ah.be.performance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
//import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.CpuMemoryUsage;
import com.ah.util.LinuxSystemInfoCollector;
import com.ah.util.MgrUtil;

/**
 * 
 * Description:collect HM cpu memory usage, add one item into table "hm_cpu_memory_usage" per 3 seconds
 * only maintain one day data. 
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2012 Aerohive Networks Inc. All Rights Reserved.
 */
public class BeServerCpuMemoryUsageProcessor implements Runnable {
	

	public final int TIMER_INTERVAL     = 30;
	
	private ScheduledExecutorService scheduler;
	
	public BeServerCpuMemoryUsageProcessor() {
		
	}
	
	public void start() {
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> cpu memory usage collect timer start...");
		// start scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, TIMER_INTERVAL, TIMER_INTERVAL,
							TimeUnit.SECONDS);
		}
	}
	
	public void stop() {
		BeLogTools.info(HmLogConst.M_TRACER,"<BE Thread> cpu memory usage collect timer stop");
		if (scheduler != null && !scheduler.isShutdown()) {
			// Disable new tasks from being submitted.
			scheduler.shutdown();
			scheduler = null;
		}
	}
	
	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());
		collectCpuMemoryUsage();
	}
	
	/**
	 * collect server cpu memory usage data
	 */
	private void collectCpuMemoryUsage(){
		HmDomain hmDomain = CacheMgmt.getInstance().getCacheDomainByName("home");
		float cpuUsage = LinuxSystemInfoCollector.getInstance().getCpuInfo() * 100;
		float memoryUsage = 0;
		try {
			long count[] = LinuxSystemInfoCollector.getInstance().getMemInfo();
			if(count[0] != 0){
				memoryUsage = ((float)(count[0] - count[1] - count[2] - count[3]) * 100 / count[0]);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"calculate memory usage failure.", e);
		}
		CpuMemoryUsage cmu = new CpuMemoryUsage();
		cmu.setCpuUsage(cpuUsage);
		cmu.setMemUsage(memoryUsage);
		cmu.setOwner(hmDomain);
		cmu.setTimeStamp((new Date()).getTime());
		try {
			QueryUtil.createBo(cmu);
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,"create CpuMemoryUsage failure.", e);
		}
	}
	
	
	public static long getCurrentDate(){
		Date currentTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(currentTime);
		Date today;
		long dal = 0; 
		try {
			today = sdf.parse(currentDate+" 00:00:00");
			dal = today.getTime();
		} catch (ParseException e) {
			dal = 0;
			return dal;
		}
		return dal;
	}

}
