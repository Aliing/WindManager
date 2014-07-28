/**
 *@filename		WinServerTimeManager.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-29 PM 04:22:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.os;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import com.ah.be.app.HmBeOsUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class WinServerTimeManager implements ServerTimeInterface {

	private static final Tracer log = new Tracer(WinServerTimeManager.class.getSimpleName());

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
		return "America/Los_Angeles";
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
		String result = "The server is running in windows system and can not change time.";
		log.info("setServerTime", result);
		return result;
	}
	
	/**
	 * Check if NTP Service is running on HM.
	 *@return boolean
	 */
	public boolean ifNTPServiceStart() {
		return false;
	}

}