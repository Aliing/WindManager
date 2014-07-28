/**
 *@filename		LinuxServerTimeManager.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-29 PM 04:20:38
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmNtpServerAndInterval;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class LinuxServerTimeManager implements ServerTimeInterface {

	private static final Tracer log = new Tracer(LinuxServerTimeManager.class.getSimpleName());

	public static final String				TIME_ZONE_FILE_PATH				= "/usr/share/zoneinfo/";

	public static final String				TIME_ZONE_MODIFY_FILE			= "/etc/localtime";

	public static final String				HIVEMAP_TIME_ZONE_MODIFY_FILE	= "/hivemap/etc/localtime";

	public static final String				TIME_CLOCK_FILE_PATH			= "/etc/sysconfig/clock";
	
	// NTP config file path
	public static final String              NTP_CONFIG_FILE_PATH            = "/etc/ntp.conf";
	
	public static final String				SUPPER_NTP_SERVERS_FILE			= "/etc/ntp/step-tickers";
	
	public static final String				SYNC_HWCLOCK_PATH			    = "/etc/sysconfig/ntpd";
	
	public static final String              HIVEMAP_NTP_CONFIG_FILE_PATH    = "/hivemap/etc/ntp.conf";
	
	public static final String				HIVEMAP_SUPPER_NTP_SERVERS_FILE = "/hivemap/etc/ntp/step-tickers";
	
	public static final String				HIVEMAP_SYNC_HWCLOCK_PATH	    = "/hivemap/etc/sysconfig/ntpd";

	/**
	 * Get the server time.
	 * 
	 * @return ServerTime -
	 */
	public Calendar getServerTime()
	{
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(new Date(System.currentTimeMillis()));
		return rightNow;
	}
	
	/**
	 * Get the server timezone.
	 * 
	 * @return ServerTimeZone -
	 */
	public String getServerTimeZone()
	{
		String strZone = "America/Los_Angeles";
		try
		{
			String timezone = FileManager.getInstance()
				.readFile(TIME_CLOCK_FILE_PATH, "ZONE");
			if (null != timezone && !"".equals(timezone))
			{
				strZone = timezone.substring(1, timezone.lastIndexOf("\""));
			}
		}
		catch (Exception e)
		{
			log.error("getServerTimeZone", "Get server time zone error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
				MgrUtil.getUserMessage("hm.system.log.linux.server.time.zone")+e.getMessage());
		}

		return strZone;
	}

	/**
	 * Get all timezone in the server.
	 * 
	 * @return TimeZone -
	 */
	public List<String> getAllTimeZone()
	{
		List<String> str = new Vector<String>();
		overloop:
		for (int i = 0; i < TimeZone.getAvailableIDs().length - 5; i++)
		{
			String str_TimeZone = TimeZone.getAvailableIDs()[i];
			if (!str_TimeZone.startsWith("Etc/GMT"))
			{
				File file = new File(TIME_ZONE_FILE_PATH + str_TimeZone);
				if (file.exists())
				{
					// remove the unsupport timezone for HA
					for (String timeZone : HmBeOsUtil.HIVEMANAGER_UNSUPPORT_TIMEZONE) {
						if (timeZone.equalsIgnoreCase(str_TimeZone)) {
							continue overloop;
						}
					}
					
					// the minutes
					int int_Offset = TimeZone.getTimeZone(str_TimeZone)
						.getRawOffset() / 60000;
					String str_Name;
					int offHour = int_Offset/60;
					int offMin = Math.abs(int_Offset%60);
					String offStr = ":00) ";
					if (offMin > 10) {
						offStr = ":"+String.valueOf(offMin)+") ";
					}
					if (offHour < 0)
					{
						if (offHour > -10)
							str_Name = "(GMT-0"
								+ String.valueOf(Math.abs(offHour))
								+ offStr + str_TimeZone;
						else
							str_Name = "(GMT-"
								+ String.valueOf(Math.abs(offHour))
								+ offStr + str_TimeZone;
					}
					else
						if (offHour == 0)
							str_Name = "(GMT) " + str_TimeZone;
						else
						{
							if (offHour < 10)
								str_Name = "(GMT+0"
									+ String.valueOf(offHour) + offStr
									+ str_TimeZone;
							else
								str_Name = "(GMT+" + String.valueOf(offHour)
									+ offStr + str_TimeZone;
						}
					str.add(str_Name);
				}
			}
		}
		return str.size() > 0 ? str : null;
	}
	
	/**
	 * Get all timezone in the server for before 4.0r1.
	 * 
	 * @return TimeZone -
	 */
	public List<String> getAllTimeZoneOldOne()
	{
		List<String> str = new Vector<String>();
		for (int i = 0; i < TimeZone.getAvailableIDs().length - 5; i++)
		{
			String str_TimeZone = TimeZone.getAvailableIDs()[i];
			if (!str_TimeZone.startsWith("Etc/GMT"))
			{
				File file = new File(TIME_ZONE_FILE_PATH + str_TimeZone);
				if (file.exists())
				{
					int int_Offset = TimeZone.getTimeZone(str_TimeZone)
						.getRawOffset() / 3600000;
					String str_Name;
					if (int_Offset < 0)
					{
						if (int_Offset > -10)
							str_Name = "(GMT-0"
								+ String.valueOf(Math.abs(int_Offset))
								+ ":00) " + str_TimeZone;
						else
							str_Name = "(GMT-"
								+ String.valueOf(Math.abs(int_Offset))
								+ ":00) " + str_TimeZone;
					}
					else
						if (int_Offset == 0)
							str_Name = "(GMT) " + str_TimeZone;
						else
						{
							if (int_Offset < 10)
								str_Name = "(GMT+0"
									+ String.valueOf(int_Offset) + ":00) "
									+ str_TimeZone;
							else
								str_Name = "(GMT+" + String.valueOf(int_Offset)
									+ ":00) " + str_TimeZone;
						}
					str.add(str_Name);
				}
			}
		}
		return str.size() > 0 ? str : null;
	}

	/**
	 * Set up the server time.
	 *
	 * @param arg_Zone -
	 *            the time zone string
	 * @param arg_Date -
	 *            set time manually : date format is MMDDHHmmYYYY.ss
	 *            synchronize with server :"", 
	 * @param arg_Server -
	 *            synchronize with server :the NTP server and interval
	 *            set time manually :null
	 * @param arg_NTP -
	 *            BeOsLayerModule.START_NTP_SERVICE: start NTP service
	 *            BeOsLayerModule.STOP_NTP_SERVICE:stop NTP service
	 * @return String : "" if set successful, the error message if set failed
	 */
	public String setServerTime(
		String arg_Zone,
		String arg_Date,
		String[] arg_Server,
		int arg_NTP)
	{
		try {
			HiveManagerIntervalNTP ntpInterval = AhAppContainer.getBeOsLayerModule().getNTPService();
			// stop ntp timer
			ntpInterval.stopNtpTimer();
			
			// if ntp service start
			boolean serviceStart = ifNTPServiceStart();
			
			/*
			 * set new timezone for HM
			 */
			arg_Zone = arg_Zone.substring(arg_Zone.indexOf(" ")).trim();
			String old_Zone = getServerTimeZone();
			if (!old_Zone.equals(arg_Zone)) {
				changeTimeZone(arg_Zone);
			}
			String setTimeResult;
			if (arg_NTP == BeOsLayerModule.START_NTP_SERVICE) {
				
				// stop NTP service
				if (serviceStart) {
					Runtime.getRuntime().exec("service ntpd stop");
					Thread.sleep(2000);
				}
				
				// Set time manually or synchronize with ntp server
				setTimeResult = setTimeOrSynchronize(arg_Date, arg_Server, serviceStart);
				if ("".equals(setTimeResult) || serviceStart) {
					if ("".equals(setTimeResult)) {
						Thread.sleep(5000);
					}
					
					// start NTP service
					Runtime.getRuntime().exec("service ntpd start");
					Runtime.getRuntime().exec("chkconfig --level 2345 ntpd on");
					Runtime.getRuntime().exec("chroot /hivemap chkconfig --level 2345 ntpd on");
				}
			} else {
				
				// stop NTP service
				if (serviceStart) {
					Runtime.getRuntime().exec("service ntpd stop");
					Runtime.getRuntime().exec("chkconfig --level 2345 ntpd off");
					Runtime.getRuntime().exec("chroot /hivemap chkconfig --level 2345 ntpd off");
					Thread.sleep(2000);
				}
				
				// Set time manually or synchronize with ntp server
				setTimeResult = setTimeOrSynchronize(arg_Date, arg_Server, false);
				if ("".equals(setTimeResult)) {
					Thread.sleep(5000);
				// start NTP service if set time failed
				} else if (serviceStart) {
					
					// start NTP service
					Runtime.getRuntime().exec("service ntpd start");
					Runtime.getRuntime().exec("chkconfig --level 2345 ntpd on");
					Runtime.getRuntime().exec("chroot /hivemap chkconfig --level 2345 ntpd on");
				}
			}
			// set time failed
			if (!"".equals(setTimeResult)) {
				// set the time zone to the older one
				if (!old_Zone.equals(getServerTimeZone()))
					changeTimeZone(old_Zone);
				// restart the ntp interval
				ntpInterval.startNTPTimer();
			}
			return setTimeResult;
		} catch (Exception e) {
			log.error("setServerTime", "Set server time error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
				MgrUtil.getUserMessage("hm.system.log.linux.server.date.time.update") + e.getMessage());
			return e.getMessage();
		}
	}
	
	/**
	 * Change the time zone of HiveManager
	 * @param arg_Zone : the time zone want to set
	 */
	private void changeTimeZone(String arg_Zone) {
		try {
			Runtime.getRuntime().exec(
				"ln -sf " + TIME_ZONE_FILE_PATH + arg_Zone.trim() + " "
						+ TIME_ZONE_MODIFY_FILE);
			FileManager.getInstance().writeFile(TIME_CLOCK_FILE_PATH, "ZONE", "\""
					+ arg_Zone.trim() + "\"", null);			
			//Thread.sleep(2000);			
			//String strSetTimeZone = System.getenv("HM_ROOT")+"/WEB-INF/shell/setTimeZone.sh";
			String strSetTimeZone = BeAdminCentOSTools.ahShellRoot+"/setTimeZone.sh";
			
			BeAdminCentOSTools.execCmdWithErr("sh "+strSetTimeZone, "");
		}
		catch (Exception e) {
			log.error("setServerTime", "Change timezone error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
					MgrUtil.getUserMessage("hm.system.log.linux.server.date.time.update") + e.getMessage());
		}
	}
	
	/**
	 * Set time manually or synchronize with ntp server
	 * @param arg_Date : date format is MMDDHHmmYYYY.ss
	 * @param arg_Server : ntp server and interval
	 * @param arg_Bool : if the ntp service start
	 * @return String : "" if set successful, the error message if set failed
	 */
	private String setTimeOrSynchronize(String arg_Date, String[] arg_Server, boolean arg_Bool) {
		try {
			// get the ntp server and interval
			HmNtpServerAndInterval ntpBo = QueryUtil.findBoByAttribute(HmNtpServerAndInterval.class,
					"timeType", BeOsLayerModule.STOP_NTP_SERVICE);
			
			if ("".equals(arg_Date)) {
				// synchronize HM clock with server
				String str_ChangeD = "ntpdate " + arg_Server[0];
				
				Process pro = Runtime.getRuntime().exec(str_ChangeD);
				BufferedReader bf = new BufferedReader(new InputStreamReader(
					pro.getErrorStream()));
				String str_Erro = bf.readLine();
				
				// get the error message
				if (str_Erro != null && !str_Erro.equals("")) {
					if (arg_Bool) {
						Runtime.getRuntime().exec("service ntpd start");
						Thread.sleep(2000);
					}
					log.error("setTimeOrSynchronize", "Fail to ntpdate the server!" + str_Erro);
					if (str_Erro.contains("Temporary failure in name resolution")) {
						str_Erro = "The host '" + arg_Server[0] + "' cannot be found for synchronization.";
					} else {
						str_Erro = "The NTP Server '" + arg_Server[0] + "' cannot be found for synchronization.";
					}
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.linux.server.date.time.update") + str_Erro);
					return "NTP Server error : " + str_Erro;
				} else {
					boolean bool = null == ntpBo;
					if (null == ntpBo) {
						ntpBo = new HmNtpServerAndInterval();
					}
					// set synchronize information
					ntpBo.setNtpServer(arg_Server[0]);
					ntpBo.setNtpInterval(Integer.valueOf(arg_Server[1]));
					ntpBo.setOwner(QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN));
					
					// set the information to database
					if (bool) {
						QueryUtil.createBo(ntpBo);
					} else {
						QueryUtil.updateBo(ntpBo);
					}
					pro = Runtime.getRuntime().exec(str_ChangeD);
					// set syslogs messages
					bf = new BufferedReader(
							new InputStreamReader(pro.getInputStream()));
					String str_Result = bf.readLine();
					log.info("setTimeOrSynchronize", "Ntpdate the server successfully!" + str_Result);
					// deal with the stream before use the command hwclock
					Runtime.getRuntime().exec("hwclock --systohc");
				}
				if (bf != null)
					bf.close();
			} else {
				// remove the synchronize information
				if (null != ntpBo) {
					QueryUtil.removeBoBase(ntpBo);
				}
				
				// set time manually for HM
				//String strSetTime =  System.getenv("HM_ROOT")+("/WEB-INF/shell/setTime.sh");
				String strSetTime = BeAdminCentOSTools.ahShellRoot+"/setTime.sh";
				
				String strCmd = "sh "+strSetTime+" "+arg_Date.trim();			
				BeAdminCentOSTools.execCmdWithErr(strCmd, "");
			}
		} catch (Exception e) {
			log.error("setTimeOrSynchronize", "Set time error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
					MgrUtil.getUserMessage("hm.system.log.linux.server.date.time.update") + e.getMessage());
			return e.getMessage();
		}
		return "";
	}
	
	/**
	 * Check if NTP Service is running on HM.
	 *@return boolean
	 */
	public boolean ifNTPServiceStart() {
		try
		{
			Process process = Runtime.getRuntime()
				.exec("service ntpd status");
			BufferedReader bf_ntpd = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
			String str_Res = bf_ntpd.readLine();
			return str_Res.contains("run");
		}
		catch (Exception e)
		{
			log.error("ifNTPServiceStart", "Check NTP server status error.", e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
					MgrUtil.getUserMessage("hm.system.log.linux.server.ntp.service.start") + e.getMessage());
			return false;
		}
	}

}