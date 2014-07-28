package com.ah.be.os;

import java.util.Calendar;
import java.util.List;

public interface ServerTimeInterface
{
	/**
	 * Get the server time.
	 * 
	 * @return ServerTime -
	 */
	public Calendar getServerTime();
	
	/**
	 * Get the server timezone.
	 * 
	 * @return ServerTimeZone -
	 */
	public String getServerTimeZone();

	/**
	 * Get all timezone in the server.
	 * 
	 * @return TimeZone -
	 */
	public List<String> getAllTimeZone();

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
		int arg_NTP);
	
	/**
	 * Check if NTP Service is running on HM.
	 *@return boolean
	 */
	public boolean ifNTPServiceStart();
}
