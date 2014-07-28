/**
 *@filename		AeroActivationTimer.java
 *@version		v1.19
 *@author		Fiona
 *@createtime	Mar 13, 2007 1:24:46 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.activation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.hiveap.HiveApInfoForLs;
import com.ah.be.hiveap.SendHiveApInfoThread;
import com.ah.be.license.AeroLicenseTimer;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.OrderKeyManagement;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.ls.stat.StatCenter;
import com.ah.be.ls.stat.StatManager;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version v1.19
 */
public class AeroActivationTimer implements SessionKeys
{
	private static final Tracer log = new Tracer(AeroActivationTimer.class.getSimpleName());

	private ScheduledExecutorService activationTimer;
	
	private ScheduledExecutorService collectionTimer;
	
	private ScheduledExecutorService hiveApInfoTimer;
	
	private ScheduledExecutorService hiveApInfoIntervalTimer;
	
	private ScheduledFuture<?> activationFuture;
	
	private ScheduledFuture<?> collectionFuture;
	
	private ScheduledFuture<?> hiveApInfoFuture;
	
	private ScheduledFuture<?> hiveApInfoIntervalFuture;

	public void startAllActiveTimer() {
		try {
			if (!NmsUtil.isHMForOEM()) {
				// send HiveAP information to license server
				hiveApInfoFirstTimer();
				
				// send HiveAP information to license server by interval
				hiveApInfoIntervalTimer();
				
				// send collection information to license server
				collectionInfoTimer();
				
				// activation or order key check
				if ((null != HmBeLicenseUtil.getLicenseInfo() && HmBeLicenseUtil.getLicenseInfo().isUseActiveCheck()) || 
						(HM_License.getInstance().isVirtualMachineSystem() && !NmsUtil.isHostedHMApplication())) {	
					
					// activation key invalid
					if (HmBeLicenseUtil.getLicenseInfo().isUseActiveCheck() && 
							!HmBeActivationUtil.ifActivationKeyValid()) {
						HmBeActivationUtil.ACTIVATION_KEY_VALID = false;
						myDebug("The activation key of " + NmsUtil.getOEMCustomer().getNmsName() 
								+ " is invalid");
					}
					// send activation key or order key to validate
					activationKeyTimer();
				}
			}
		} catch (Exception e) {
			myDebug("startAllActiveTimer(): "+e.getMessage());
		}
	}
	
	/**
	 * Deal with activation key query based on the server running time
	 */
	public void activationKeyTimer()
	{
		ActivationKeyTask taskEvalue = new ActivationKeyTask();
		if(null == activationTimer || activationTimer.isShutdown()) {
			activationTimer = Executors.newSingleThreadScheduledExecutor();
			activationFuture = activationTimer.scheduleWithFixedDelay(taskEvalue, 60 * 60, 60 * 60, TimeUnit.SECONDS);
			//activationFuture = activationTimer.scheduleAtFixedRate(taskEvalue, 60 * 2, 60 * 2, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Deal with collection information query based on the server running time
	 */
	public void collectionInfoTimer()
	{
		SendCollectionTask taskEvalue = new SendCollectionTask();
		if(null == collectionTimer || collectionTimer.isShutdown()) {
			collectionTimer = Executors.newSingleThreadScheduledExecutor();
			collectionFuture = collectionTimer.scheduleWithFixedDelay(taskEvalue, 60 * 60, 60 * 60, TimeUnit.SECONDS);
			//collectionFuture = collectionTimer.scheduleAtFixedRate(taskEvalue, 60 * 3, 60 * 3, TimeUnit.SECONDS);
		}	
	}
	
	/**
	 * Deal with HiveAP information query based on the server running time
	 */
	public void hiveApInfoFirstTimer()
	{
		if(null == hiveApInfoTimer || hiveApInfoTimer.isShutdown()) {
			hiveApInfoTimer = Executors.newSingleThreadScheduledExecutor();
			hiveApInfoFuture = hiveApInfoTimer.scheduleAtFixedRate(SendHiveApInfoThread.getInstance(), 1, 24, TimeUnit.HOURS);
			//hiveApInfoFuture = hiveApInfoTimer.scheduleAtFixedRate(SendHiveApInfoThread.getInstance(), 3, 3, TimeUnit.MINUTES);
		}	
	}
	
	/**
	 * Deal with HiveAP information query based on the 30 days interval
	 */
	public void hiveApInfoIntervalTimer()
	{
		if(null == hiveApInfoIntervalTimer || hiveApInfoIntervalTimer.isShutdown()) {
			HiveApInfoTask apTask = new HiveApInfoTask();
			hiveApInfoIntervalTimer = Executors.newSingleThreadScheduledExecutor();
			
			int interval = 15;
			// hm online send ap info every day
			if (NmsUtil.isHostedHMApplication()) {
				interval = 1;
			} else {
				LicenseServerSetting lsSet = HmBeActivationUtil.getLicenseServerInfo();
				if (null != lsSet) {
					interval = lsSet.getApTimerInterval();
				}
			}
			hiveApInfoIntervalFuture = hiveApInfoIntervalTimer.scheduleAtFixedRate(apTask, 24, 24*interval, TimeUnit.HOURS);
			//hiveApInfoIntervalFuture = hiveApInfoIntervalTimer.scheduleAtFixedRate(apTask, 5, 15, TimeUnit.MINUTES);
		}	
	}
	
	/**
	 * Write a system log every hour if the hours is less than 7 days.
	 *
	 * @param arg_Hours the left hours and if for activation key
	 * @param arg_Active -
	 */
	public static void writeSystemLog(int arg_Hours, boolean arg_Active) {
		// give user the valid hours in syslog every hour
		if (arg_Hours < BeLicenseModule.PRINT_SYSTEMLOG_HOURS) {
			String feedDay;
			if (arg_Hours > 0) {
				int days = arg_Hours / 24;
				feedDay = (arg_Active ?  MgrUtil.getUserMessage("hm.system.log.aero.active.timer.input.activation.key") : MgrUtil.getUserMessage("hm.system.log.aero.active.timer.key.valid"))
						+ (days > 0 ? days + (days > 1 ?" days":" day") : "")
						+ (arg_Hours % 24 > 0 ? " " + arg_Hours % 24 + (arg_Hours % 24 > 1 ?" hours.":" hour.") : ".");
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION, feedDay);
				
			// the license is overdue
			} else {
				feedDay = arg_Active ? MgrUtil.getUserMessage("hm.system.log.aero.active.timer.hivemanager.not.work") : MgrUtil.getUserMessage("hm.system.log.aero.active.timer.license.key.expired");
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION, feedDay);
			}	
		}
	}
	
	/**
	 * Do the activation key task.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class ActivationKeyTask implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			try {		
				if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID) {
					LicenseInfo lsInfo = HmBeLicenseUtil.getLicenseInfo();
					if (lsInfo.isUseActiveCheck()) {
						if (HmBeActivationUtil.ACTIVATION_KEY_VALID) {
							// add the used hours
							for (ActivationKeyInfo singleOne : HmBeActivationUtil.getAllActivationInfoFromDb()) {
								operateKey(singleOne, false);
							}
						}
					} else if (null != lsInfo.getOrderKey() && !"".equals(lsInfo.getOrderKey())
						&& !DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(lsInfo.getOrderKey())) {
						ActivationKeyInfo activation;
						try {
							activation = QueryUtil.findBoByAttribute(ActivationKeyInfo.class, "systemId",
									lsInfo.getSystemId());
							operateKey(activation, true);
						} catch (Exception e) {
						}
					}
				}
			} catch (Exception ex) {
				myDebug("startAllActiveTimer(): "+ex.getMessage());
			} catch (Error e) {
				myDebug("startAllActiveTimer(): "+e.getMessage());
			}
		}		
	}
	
	private void operateKey(ActivationKeyInfo singleOne, boolean isOrder) {
		if (HmBeActivationUtil.ACTIVATION_KEY_VALID) {
			singleOne.changeTheUsedHours();
			/*
			 * Distinguish query period timer and retry timer
			 */
			if (singleOne.isStartRetryTimer()) {
				if (singleOne.getRetryIntervalLeft() <= 0) {
					if (isOrder) {
						OrderKeyManagement.checkValidityOfVmhm(singleOne);
					} else {
						sendQueryToLS(singleOne);
					}
				}
			} else {
				int hours = singleOne.getQueryPeriodLeft();
				
				// activation key does not exist
				if ("".equals(singleOne.getActivationKey())) {
					if (hours <= 0) {
						HmBeActivationUtil.ACTIVATION_KEY_VALID = false;
					}
					writeSystemLog(hours, true);
				} else {
					if (hours <= 0) {					
						if (isOrder) {
							OrderKeyManagement.checkValidityOfVmhm(singleOne);
						} else {
							sendQueryToLS(singleOne);
						}
					}
				}
			}
			try {
				if (null == singleOne.getId()) {
					QueryUtil.createBo(singleOne);
				} else {
					QueryUtil.updateBo(singleOne);
				}
			} catch (Exception ex) {
				myDebug("operateKey(): activation key is "+singleOne.getActivationKey()+", "+ex.getMessage());
			}
		}
	}
	
	/**
	 * Send activation key, HiveManager version or collection information to license server.
	 *
	 *@param arg_ActKey timer file key
	 */
	private void sendQueryToLS(ActivationKeyInfo arg_ActKey) {
		// send query to validate activation key
		String resultMes = ActivationKeyOperation.sendQueryAndReceive(arg_ActKey, true, null);
		// send successfully
		if ("".equals(resultMes)) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.aero.active.timer.activation.key.send.success",new String[]{NmsUtil.getOEMCustomer().getNmsName(),arg_ActKey.getActivationKey()}));
			
		// send failed
		} else {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_ADMINISTRATION, 
					MgrUtil.getUserMessage("hm.system.log.aero.active.timer.activation.key.send.failure",new String[]{NmsUtil.getOEMCustomer().getNmsName(),arg_ActKey.getActivationKey(),resultMes}));
		}
	}
	
	/**
	 * Do the send collection task.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class SendCollectionTask implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			try {
				LicenseServerSetting lsSet = HmBeActivationUtil.getLicenseServerInfo();
				StatManager dataMiningMgr = StatManager.getInstance();
				/*
				 * check if allow send statistic information
				 */
				if (lsSet.isSendStatistic()) {
					// add the used hours
					lsSet.setHoursUsed(lsSet.getHoursUsed()+1);
					dataMiningMgr.idel(1);

					if (lsSet.getStatisticPeriodLeft() <= 0) {
						if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID
							&& HmBeActivationUtil.ACTIVATION_KEY_VALID) {
							// get new version flag
							ActivationKeyOperation.getNewVersionFlag(lsSet);
						}
								
						/*
						 * send collection information to license server
						 */
						// HiveManager and HiveAP information
						ActivationKeyOperation.sendCollectionInfo();
						
						// active client mac information
						ActivationKeyOperation.sendClientMacInfo();

						// modify the timer file
						lsSet.setHoursUsed(0);
					}
					// data mining reporting
					if (dataMiningMgr.isActived()) {
						// report data mining information
						StatCenter.reportApUsageStat();

						dataMiningMgr.restartIdel();
					}
					QueryUtil.updateBo(lsSet);
				}
			} catch (Exception ex) {
				myDebug("SendCollectionTask(): "+ex.getMessage());
			} catch (Error e) {
				myDebug("SendCollectionTask(): "+e.getMessage());
			}
		}
	}
	
	/**
	 * Do the HiveAP info be send to license server task.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class HiveApInfoTask implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			
			// get the information from database
			List<HiveAp> firstHiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("manageStatus = :s1 " +
					"AND discoveryReported = :s2 AND simulated = :s3 AND connChangedTime > :s4 AND owner.runStatus != :s5",
				new Object[]{HiveAp.STATUS_MANAGED, true, false, 0L, HmDomain.DOMAIN_DISABLE_STATUS}));
			List<HiveApInfoForLs> apListForLs = new ArrayList<HiveApInfoForLs>(firstHiveAps.size());
			if (!firstHiveAps.isEmpty()) {
				boolean hmOnline = NmsUtil.isHostedHMApplication();
				for (HiveAp singleAp : firstHiveAps) {
					HiveApInfoForLs apForLs = new HiveApInfoForLs();

					if (!NmsUtil.isValidSerialNumber(singleAp.getSerialNumber())) {
						continue;
					}
					
					// serial number
					apForLs.setSerialNumber(singleAp.getSerialNumber());
					
					// mac address
					apForLs.setMacAddress(singleAp.getMacAddress());

					// first connect time
					apForLs.setFirstConnectTime(singleAp.getDiscoveryTime());
					
					// last connect time
					apForLs.setLastConnectTime(singleAp.getConnChangedTime());
					
					// total connect time
					apForLs.setTotalConnectTime(singleAp.getTotalConnectTime());
					
					// total connect times
					apForLs.setTotalConnectTimes(singleAp.getTotalConnectTimes());
					
					// ap product name
					apForLs.setProductName(singleAp.getProductName());
					
					// ap soft version
					apForLs.setSoftVer(singleAp.getSoftVer());
					
					HmDomain hmDom = singleAp.getOwner();
					// system id or vhm id
					apForLs.setHmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					if (!hmDom.isHomeDomain() && hmOnline) {
						apForLs.setHmId(hmDom.getVhmID());
					}
					
					// vhm name
					apForLs.setVhmName(hmDom.getDomainName());
					
					// time zone
					apForLs.setTimeZone(hmDom.getTimeZoneString());
					
					apListForLs.add(apForLs);
				}
			}
			try {
				// send the information to license server
				ClientSenderCenter.sendAPConnectStatInfo(apListForLs);
				SendHiveApInfoThread.getInstance().setApInfoList(null);
			} catch (Exception ex) {
				if (!apListForLs.isEmpty()) {
					SendHiveApInfoThread.getInstance().setApInfoList(apListForLs);
				}
				myDebug("HiveApInfoTask(): "+ex.getMessage());
			}
		}		
	}
	
	public void stopActiveTimer() {
		if(null != activationTimer) {
			String errorMes = AeroLicenseTimer.stopLicenseTimer(activationTimer, activationFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
		}
	}
	
	public void stopCollectionTimer() {
		if(null != collectionTimer) {
			String errorMes = AeroLicenseTimer.stopLicenseTimer(collectionTimer, collectionFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
		}
	}
	
	public void stopHiveApInfoTimer() {
		if(null != hiveApInfoTimer) {
			String errorMes = AeroLicenseTimer.stopLicenseTimer(hiveApInfoTimer, hiveApInfoFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
		}
	}
	
	public void stopHiveApInfoIntervalTimer() {
		if(null != hiveApInfoIntervalTimer) {
			String errorMes = AeroLicenseTimer.stopLicenseTimer(hiveApInfoIntervalTimer, hiveApInfoIntervalFuture);
			if (null != errorMes) {
				myDebug(errorMes);
			}
		}
	}
	
	public void stopAllActiveTimer() {
		stopActiveTimer();
		stopHiveApInfoTimer();
		stopHiveApInfoIntervalTimer();
		stopCollectionTimer();
	}
	
	/**
	 * Record the debug message.
	 * @param arg_Msg -
	 */
	public void myDebug(String arg_Msg) {
		log.error(arg_Msg);
	}

}