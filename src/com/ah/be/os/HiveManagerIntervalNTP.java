/**
 *@filename		HiveManagerIntervalNTP.java
 *@version
 *@author		Fiona
 *@createtime	2008-04-19 PM 04:20:38
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.os;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLogUtil;
import com.ah.bo.admin.HmNtpServerAndInterval;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class HiveManagerIntervalNTP {

	private static final Tracer log = new Tracer(HiveManagerIntervalNTP.class.getSimpleName());
	
	private ScheduledExecutorService timerInterval;
	
	private ScheduledFuture<?> ntpFuture;
	
	private HmNtpServerAndInterval ntpBo;

	public HiveManagerIntervalNTP() {
	}
	
	public void ntpTimeOnce() {
		try {		
			// get the NTP server and interval
			ntpBo = QueryUtil.findBoByAttribute(HmNtpServerAndInterval.class,
					"timeType", BeOsLayerModule.STOP_NTP_SERVICE);
			if (null != ntpBo) {
				ntpTimeOperation(ntpBo.getNtpServer());
			}
		} catch (Exception e) {
			log.error("ntpTimeOnce", "NTP Sync Error.", e);
		}
	}
	
	public void startNTPTimer() {
		try {
			// get the NTP server and interval
			ntpBo = QueryUtil.findBoByAttribute(HmNtpServerAndInterval.class,
					"timeType", BeOsLayerModule.STOP_NTP_SERVICE);
			if (null != ntpBo) {
				NtpdateTimerTask taskInterval = new NtpdateTimerTask(
						ntpBo.getNtpServer());
				timerInterval =  Executors.newSingleThreadScheduledExecutor();
				ntpFuture = timerInterval.scheduleAtFixedRate(taskInterval, 60 * ntpBo.getNtpInterval(), 60 * ntpBo.getNtpInterval(), TimeUnit.SECONDS);
				//ntpFuture = timerInterval.scheduleAtFixedRate(taskInterval, 60, 60, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			log.error("ntpTimeOnce", "Starting timer error.", e);
		}
	}
	
	/**
	 * Stop your defined ntp timer.
	 */
	public void stopNtpTimer()
	{
		try {
			if (null != timerInterval) {
				if (!timerInterval.isShutdown()) {
					
					// Disable new tasks from being submitted.
					if (null != ntpFuture) {
						ntpFuture.cancel(false);
					}		
					timerInterval.shutdown();
				}   	
			}
		} catch (Exception e) {
			log.error("stopNtpTimer", "There is something wrong with timer stop.", e);
        }
	}

	/**
	 * sync HM time to NTP server every interval time
	 * @author root
	 *
	 */
	private class NtpdateTimerTask implements Runnable
	{
		private String str_Server	= "";

		public NtpdateTimerTask(String arg_Server)
		{
			if (arg_Server != null)
				str_Server = arg_Server;
		}

		@Override
		public void run()
		{
			MgrUtil.setTimerName(getClass().getSimpleName());
			if (!str_Server.equals(""))
			{
				ntpTimeOperation(str_Server);
			}
		}
	}
	
	public void ntpTimeOperation(String arg_Server) {
		if (null != arg_Server && !"".equals(arg_Server)) {
			try {
				// stop NTP service
				ServerTimeInterface timeService = AhAppContainer.getBeOsLayerModule().getTimeService();
				boolean serviceStart = timeService.ifNTPServiceStart();
			
				String str_ChangeD = "ntpdate " + arg_Server.trim();
			
				if (serviceStart) {
					Runtime.getRuntime().exec("service ntpd stop");
					Thread.sleep(2000);
				}
				
				Process p = Runtime.getRuntime().exec(str_ChangeD);
				BufferedReader bf = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
				String str_Erro = bf.readLine();
				if (str_Erro != null && !str_Erro.equals("")) {
					log.error("ntpTimeOperation", "Fail to ntpdate the server!" + str_Erro);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_ADMINISTRATION, str_Erro);
				} else {
					p = Runtime.getRuntime().exec(str_ChangeD);
					bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String str_Result = bf.readLine();
					log.error("ntpTimeOperation", "Congratulation !Ntpdate the server successfully!" + str_Result);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_ADMINISTRATION, str_Result);
					Runtime.getRuntime().exec("hwclock --systohc");
				}
				if (bf != null)
					bf.close();
				if (serviceStart) {
					Runtime.getRuntime().exec("service ntpd start");
				}
			} catch (Exception ex) {
				log.error("ntpTimeOperation", "Fail to ntpdate the server!", ex);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION," Fail to ntpdate the server : " + arg_Server);
			} catch (Error e) {
				log.error("ntpTimeOperation", "Fail to ntpdate the server!", e);
			}
		}
	}

}