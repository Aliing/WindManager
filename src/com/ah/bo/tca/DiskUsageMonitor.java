/**   
* @Title: DiskUsageMonitor.java 
* @Package com.aerohive.test 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.monitor.AhAlarm;
import com.ah.util.Tracer;

/** 
 * @ClassName: DiskUsageMonitor 
 * @Description: To check the Usage of Disk
 * @author xxu
 * @date 2012-8-2 
 *  
 */
public class DiskUsageMonitor implements TCAMonitorRunnable, TCANotificatitionInterface {

	int i=0;
	
	private long interval;
	
	public final static String CHECK_DISKFULL_SCRIPT="df -kP | grep / | awk -F ' ' ' {print $6\":\"$5}' | awk -F '%' ' {print $1}' | tr -s '\\n' ';'";
	
	public final static String RUNBASH="/bin/sh";
	
	public final static String PARAMETER2="-c";
	
	public final static String SYSTEMMEMORY="/dev/shm";
	
	public final static String MOUNTCDROM="/cdrom";
	
	public final static String MOUNTCDROMCAPS="/CDROM";
	
	public final static String MOUNTFLOPPY="/floppy";
	
	public final String[] arrayS={RUNBASH,PARAMETER2,CHECK_DISKFULL_SCRIPT};
	
	public final String TRAPINFO="Disk space usage of partition ";
	
	public final String TRAPINFOOVER=" on HiveManager is over ";
	
	public final String TRAPINFOBELOW=" on HiveManager is below ";
	
	private static final Tracer log	= new Tracer(DiskUsageMonitor.class.getSimpleName());
	
	private long highThreshold;
	
	private long lowThreshold;
	
//	private final BeFaultModule Parent;
	
	public static final String HIVEMANAGER="HiveManager";
	
	
	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setHighThreshold(long highThreshold) {
		this.highThreshold = highThreshold;
	}

	public void setLowThreshold(long lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public DiskUsageMonitor(long interval) {
		this.interval=interval;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
    public void run() {
		// This feature is unavailable when HiveManager is running on Windows.
		String os = System.getProperty("os.name");

		if (os.toLowerCase().contains("windows")) {
			return;
		}

		Process process;
		try {
			process = Runtime.getRuntime().exec(arrayS);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				log.error("execute", "DiskUsageMonitor thread execute Failed!", e);
			}
		
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		LineNumberReader input = new LineNumberReader(ir);
		//BufferedReader br=new BufferedReader(ir);
		BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String buffer;
		while ((buffer=r.readLine()) != null) {
			System.out.println(buffer);
		}
		//OutputStreamReader //
		String line;
		while ((line = input.readLine()) != null) {
			String[] temp=line.trim().split(";");
			if (temp==null || temp.length==0) {
				return;
			}
			for (String tt:temp) {
				String[] partition=tt.split(":");
				if (partition==null || partition.length<2) {
				    continue;	
				}
				if(partition[0].equals(SYSTEMMEMORY)){
					continue;
				}
				//any cdrom or floppy shouldn't arise alarm
				if(partition[0].contains(MOUNTCDROM) || partition[0].contains(MOUNTCDROMCAPS)|| partition[0].contains(MOUNTFLOPPY)){
					continue;
				}
				int pertenage=Integer.parseInt(partition[1]);
				//Normally 100 usage is impossible.In most case it's should be customer mount a cdrom or some media
				if(pertenage==100){
					continue;
				}
				if (pertenage>=getHighThreshold()) {
					AhAlarm alarm=buildAlarm(AhAlarm.AH_SEVERITY_CRITICAL);
					alarm.setTrapDesc(TRAPINFO + "\"" + partition[0]+"\"" + TRAPINFOOVER + pertenage + "%");
					alarm.setObjectName(HIVEMANAGER + ":" + partition[0]);
					alarm.setTag3(partition[0]);
					raiseAlarm(alarm);
					sendTrap(alarm);
				} else if (pertenage<=getLowThreshold()) {
					if(TCAUtils.isSameTCAAlarmExist(TCAAlarm.TCA_ALARM_TYPE, TCAAlarm.DISK_USAGE_ALARM_TYPE, HIVEMANAGER + ":" + partition[0], partition[0], AhAlarm.AH_SEVERITY_CRITICAL)){
					AhAlarm alarm=buildAlarm(AhAlarm.AH_SEVERITY_UNDETERMINED);
					alarm.setTrapDesc(TRAPINFO + "\""+partition[0]+"\""+TRAPINFOBELOW + pertenage + "%");
					alarm.setObjectName(HIVEMANAGER + ":" + partition[0]);
					alarm.setTag3(partition[0]);
					raiseAlarm(alarm);
					sendTrap(alarm);
					}
				}else {//update the disk usage 
					if(TCAUtils.isSameTCAAlarmExist(TCAAlarm.TCA_ALARM_TYPE, TCAAlarm.DISK_USAGE_ALARM_TYPE, HIVEMANAGER + ":" + partition[0], partition[0], AhAlarm.AH_SEVERITY_CRITICAL)){
						AhAlarm alarm=buildAlarm(AhAlarm.AH_SEVERITY_CRITICAL);
						alarm.setTrapDesc(TRAPINFO + "\"" + partition[0]+"\"" + TRAPINFOOVER + pertenage + "%");
						alarm.setObjectName(HIVEMANAGER + ":" + partition[0]);
						alarm.setTag3(partition[0]);
						raiseAlarm(alarm);
						//sendTrap(alarm); don't need to send trap
					}
				}
				log.debug(TRAPINFO+partition[0] + " on HiveManager is "+ pertenage + "%");
			}
		}
		} catch (IOException e) {
			log.error("execute", "DiskUsageMonitor thread execute Failed!", e);
		}
    }
	
	@Override
	public boolean sendTrap(AhAlarm alarm) {
		boolean result;
		BeTrapEvent trapEvent = new BeTrapEvent();

		trapEvent.setEventType(BeTrapEvent.TYPE_TCA_ALARM);
		trapEvent.setApMac(alarm.getApId());
		trapEvent.setApName(alarm.getApName()==null?"HiveManager":alarm.getApName());
		trapEvent.setTrapType(BeTrapEvent.TYPE_TCA_ALARM );
		trapEvent.setObjectName(alarm.getObjectName());
		trapEvent.setApMac(alarm.getApId());
//		trapEvent.setTimeStamp(alarm.getTrapTimeStamp());
//		trapEvent.setTimeZone(arg_Event.getMessageTimeZone());
//		trapEvent.setProbableCause(BeTrapEvent.ALARM_SUBTYPE_TCA);
		trapEvent.setAlarmTag1(alarm.getTag1());
		trapEvent.setAlarmTag3(alarm.getTag3());
		trapEvent.setDescribe(alarm.getTrapDesc());
		trapEvent.setCode(alarm.getCode());
		trapEvent.setSeverity((byte) alarm.getSeverity());

		try {
			AhAppContainer.getBeFaultModule().addTrapToMailQueue(trapEvent);
			result=true;
		} catch (Exception e) {
			result=false;
			log.error("execute", "DiskUsageMonitor thread send trap to mailList Failed!", e);
		}

		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.aerohive.test.TCAMonitorRunnable#getName()
	 */
	@Override
	public String getName() {
		return DISKUSAGE;
	}
	/* (non-Javadoc)
	 * @see com.aerohive.test.TCAMonitorRunnable#getInterval()
	 */
	@Override
	public long getInterval() {
		return interval;
	}
	/* (non-Javadoc)
	 * @see com.aerohive.test.TCAMonitorRunnable#getHighThreshold()
	 */
	@Override
	public long getHighThreshold() {
		return highThreshold;
	}
	/* (non-Javadoc)
	 * @see com.aerohive.test.TCAMonitorRunnable#getLowThreshold()
	 */
	@Override
	public long getLowThreshold() {
		return lowThreshold;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.tca.TCAMonitorRunnable#buildAlarm()
	 */
	@Override
	public AhAlarm buildAlarm(short severity) {
		AhAlarm AlarmBo = new AhAlarm();
		AlarmBo.setApId(HmBeOsUtil.getHiveManagerIPAddr());
		AlarmBo.setApName(HmBeOsUtil.getHostName());

		// 10000 means NMS alarm,100 means TCA Alarm,1 means Disk Usage Alarm
		AlarmBo.setCode(10101);
//		AlarmBo.setTrapDesc("Disk Full");
		
		AlarmBo.setSeverity(severity);
		AlarmBo.setAlarmSubType(TCAAlarm.DISK_USAGE_ALARM_TYPE);
		AlarmBo.setAlarmType(TCAAlarm.TCA_ALARM_TYPE);
//		AlarmBo.setObjectName("HiveManager");
		
//		AlarmBo.setTag1(tag1);
//		AlarmBo.setTag2(tag2);
		AlarmBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		if (severity==AhAlarm.AH_SEVERITY_UNDETERMINED) {
			AlarmBo.setClearTimeStamp(new HmTimeStamp(System.currentTimeMillis(), BoMgmt.getDomainMgmt().getHomeDomain()
				.getTimeZoneString()));
			AlarmBo.setModifyTimeStamp(new HmTimeStamp(System.currentTimeMillis(), BoMgmt.getDomainMgmt().getHomeDomain()
					.getTimeZoneString()));
		} else {
			AlarmBo.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), BoMgmt.getDomainMgmt().getHomeDomain()
					.getTimeZoneString()));
			AlarmBo.setModifyTimeStamp(null);
			AlarmBo.setClearTimeStamp(null);
		}
		
		return AlarmBo;
		
	}

	@Override
	public boolean raiseAlarm(AhAlarm alarm) {
		AhAppContainer.getBeFaultModule().addAlarmToQueue(alarm);
		return true;
	}

	@Override
	public boolean clearAlarm(AhAlarm alarm) {
		return false;
	}

	@Override
	public TimeUnit getTimeUnit() {
		//return TimeUnit.MINUTES;
		return TimeUnit.MINUTES;
	}

}