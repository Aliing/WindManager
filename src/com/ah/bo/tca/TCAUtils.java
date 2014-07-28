/**   
* @Title: TCAUtils.java 
* @Package com.ah.bo.tca 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.util.Tracer;
import com.ah.util.notificationmsg.message.HmTCADiskUsageWarningMSG;

/** 
 * @ClassName: TCAUtils 
 * @Description: TODO
 * @author xxu
 * @date 2012-8-2 
 *  
 */
public class TCAUtils {
	
	public final static String DISKUSAGE="Disk Usage";
	
	private static final Tracer log	= new Tracer(TCAUtils.class.getSimpleName());
	
	
	public static List<TCAMonitorRunnable> getTCAMonitorList(){
		List<TCAAlarm> alarmList = QueryUtil.executeQuery(
				TCAAlarm.class, null, null);
		List<TCAMonitorRunnable> tcaRunnableList=new ArrayList<TCAMonitorRunnable>();
		for(TCAAlarm alarm:alarmList){
			if(alarm.getLowThreshold()>=alarm.getHighThreshold())
			{
				log.error("Threshold of TCA Alarm "+alarm.getMeatureItem()+" is wrong");
				continue;
			}
			if(alarm.getMeatureItem().equals("Disk Usage")){
				//interval is minutes.
				DiskUsageMonitor disk = new DiskUsageMonitor(alarm.getInterval());
				disk.setHighThreshold(alarm.getHighThreshold());
				disk.setLowThreshold(alarm.getLowThreshold());
				tcaRunnableList.add(disk);
			}
		}
		return tcaRunnableList;
	}
	
	public static TCAMonitorRunnable convertTCAAlarm(TCAAlarm alarm){
		if(alarm==null){
			return null;
		}
		if(alarm.getMeatureItem().equals("Disk Usage")){
			//interval is minutes.
			DiskUsageMonitor disk = new DiskUsageMonitor(alarm.getInterval());
			disk.setHighThreshold(alarm.getHighThreshold());
			disk.setLowThreshold(alarm.getLowThreshold());
			return disk;
		}
		return null;
	}
	
	public static TCAAlarm getDiskFullAlarm(){
		List<TCAAlarm>	tcaAlarmList = QueryUtil.executeQuery(
					TCAAlarm.class, null, null);
		for(TCAAlarm alarm:tcaAlarmList){
			if(alarm.getLowThreshold()>=alarm.getHighThreshold())
			{
				log.error("Threshold of TCA Alarm "+alarm.getMeatureItem()+" is wrong");
				continue;
			}
			if(alarm.getMeatureItem().equals(DISKUSAGE)){
				return alarm;
			}
			
		}
		return null;
	}
	
	public static boolean isSameTCAAlarmExist(short alarmType,short alarmSubType,String objectName,String tag3,short severity){
		FilterParams filterParams = new FilterParams("code", HmTCADiskUsageWarningMSG.DISK_USAGE_AHALARM_CODE);
		List<AhAlarm> alarmLists = QueryUtil.executeQuery(AhAlarm.class, null, filterParams, 10);
		if(alarmLists==null || alarmLists.isEmpty()){
			return false;
		}

		for(AhAlarm alarm:alarmLists){
			if(alarm!=null && alarm.getAlarmType()==alarmType && alarm.getAlarmSubType()==alarmSubType
					&& alarm.getSeverity()==severity && alarm.getObjectName().equals(objectName) && alarm.getTag3().equals(tag3))
			{
				return true;
			}
		
			}
			return false;
		
	}
	
	void raiseAlarm(){
		
	}
	
	void raiseAlarminBanner(){
		
	}
	
	

}
