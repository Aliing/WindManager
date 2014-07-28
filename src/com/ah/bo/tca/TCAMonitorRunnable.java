/**   
* @Title: TCAMonitorRunnable.java 
* @Package com.aerohive.test 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import java.util.concurrent.TimeUnit;

import com.ah.bo.monitor.AhAlarm;

/** 
 * @ClassName: TCAMonitorRunnable 
 * @Description: TODO
 * @author xxu
 * @date 2012-8-2 
 *  
 */
public interface TCAMonitorRunnable extends Runnable{
	
	public static final String DISKUSAGE="Disk Usage";

	String getName();
	
	long getInterval();
	
	long getHighThreshold();
	
	long getLowThreshold();
	
	AhAlarm buildAlarm(short severity);
	
	TimeUnit getTimeUnit();
	
}
