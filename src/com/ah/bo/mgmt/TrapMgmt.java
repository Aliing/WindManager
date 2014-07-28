package com.ah.bo.mgmt;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;

/*
 * @author Chris Scheers
 */

public interface TrapMgmt {

	Long createEvent(AhEvent event) throws Exception;

	Long createAlarm(AhAlarm alarm) throws Exception;

	boolean clearAlarm(AhAlarm alarm) throws Exception;

	boolean clearAlarmId(Long alarmId) throws Exception;

	// feeling add 2007-10-29
	void updateAlarm(AhAlarm alarm, short severity) throws Exception;

	// feeling add 2007-12-25
	void updateAlarm(AhAlarm alarm) throws Exception;

	/**
	 * Jonathan add 07/17/2008 Set VHM Alarm severity to critical when restore VHM data
	 */
	public void setCapwapAlarm2LinkDownByDomain(Long domainId);

	/**
	 * Jonathan add 07/17/2008 Set All Alarm severity to critical when restart HM
	 */
	public void setCapwapAlarm2LinkDownByAll();

	/**
	 * Jonathan add 08/29/2008 query by filter and update alarm use new alarm
	 * 
	 * @param filter
	 * @param newAlarm
	 */
	public void queryAndUpdateAlarm(FilterParams filter, AhAlarm newAlarm);

	/**
	 * Jonathan add 08/29/2008 clear alarm table
	 * 
	 * @param date
	 * @throws Exception
	 */
	public void clearAlarmTables(long intervalDate,long unclearDate,int maxRecords,long reminderDate) throws Exception;

	/**
	 * Jonathan add 08/29/2008 remove all alarms by AP
	 * 
	 * @param hiveAp
	 */
	public void removeAlarms(HiveAp hiveAp);

	/**
	 * Jonathan add 08/29/2008 remove alarms by filter
	 * 
	 * @param filter
	 */
	public void bulkRemoveAlarms(FilterParams filter);
	/**
	 * Shaohua Zhou add 01/11/2013 remove network device history by AP
	 * 
	 * @param hiveAp
	 */
	public void removeNetworkDeviceHistorys(HiveAp hiveAp);
}