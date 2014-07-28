package com.ah.be.fault;

import java.util.concurrent.BlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.monitor.AhAlarm;

public class AlarmProcessThread extends Thread {

	private BlockingQueue<AhAlarm> eq = null;
	private BeFaultModule parent = null;

	private String SHUTDOWN_ID = "99999999";

	public AlarmProcessThread(BeFaultModule arg_Module) {
		parent = arg_Module;
	}

	@Override
	public void run() {
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Alarm processor is running...");

		eq = parent.getAlarmQueue();
		if (eq == null) {
			DebugUtil.faultDebugWarn(" Alarm processor: queue is empty");
		}

		while (true) {
			try {
				AhAlarm alarm = eq.take();
				DebugUtil.faultDebugInfo("processing an alarm, remain size=" + eq.size());

				if (alarm.getApId().equals(SHUTDOWN_ID)) {
					BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
							"<BE Thread> Alarm processor is shutdown. " + eq.size()
									+ " traps lost.");

					break;
				}

				processAlarm(alarm);
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_TRACER, "AlarmProcessThread: run() catch Exception: "
						+ e.getMessage());
				BeLogTools
						.error(HmLogConst.M_FAULT, "AlarmProcessThread: run() catch Exception", e);
			} catch (Error e) {
				BeLogTools.error(HmLogConst.M_TRACER, "AlarmProcessThread: run() catch Error: "
						+ e.getMessage());
				BeLogTools.error(HmLogConst.M_FAULT, "AlarmProcessThread: run() catch Error", e);
			}
		}
	}

	private void processAlarm(AhAlarm alarm) {
		long begin = System.currentTimeMillis();

		FilterParams filter = getFilter(alarm);
		BoMgmt.getTrapMgmt().queryAndUpdateAlarm(filter, alarm);
		DebugUtil.faultDebugInfo("processAlarm cost: " + alarm.getApId() + ", "
				+ (System.currentTimeMillis() - begin));
	}

	/*private FilterParams getFilter(AhAlarm alarm) {
		String whereString;
		String whereStringNull;
		FilterParams filter;
		Object[] bindings;
		
		whereString = " apId=:s1 AND alarmType=:s2 AND alarmSubType=:s3 AND owner=:s4 AND tag1=:s5 AND tag2=:s6 AND tag3=:s7";
		whereStringNull = " apId=:s1 AND alarmType=:s2 AND alarmSubType=:s3 AND owner=:s4 AND tag1=:s5 AND tag2=:s6 AND tag3 IS NULL";
		if(alarm.getTag3() == null){
			bindings = new Object[6];
		} else {
			bindings = new Object[7];
		}
		bindings[0] = alarm.getApId();
		bindings[1] = alarm.getAlarmType();
		bindings[2] = alarm.getAlarmSubType();
		bindings[3] = alarm.getOwner();
		bindings[4] = alarm.getTag1();
		bindings[5] = alarm.getTag2();
		
		if(alarm.getTag3() == null){
			filter = new FilterParams(whereStringNull, bindings);
		} else {
			bindings[6] = alarm.getTag3();
			filter = new FilterParams(whereString, bindings);
		}
		return filter;
	}*/
	
	private FilterParams getFilter(AhAlarm alarm) {
		Object[] bindings;
		FilterParams filter;
		StringBuffer whereString = new StringBuffer();
		whereString.append("alarmType=:s1 AND alarmSubType=:s2 AND apId=:s3");	
		
		if(null == alarm.getTag3()) {
			whereString.append(" AND tag1=:s4 AND tag2=:s5");
			whereString.append(" AND tag3 is NULL");
			bindings = new Object[5];
			
			bindings[0] = alarm.getAlarmType();
			bindings[1] = alarm.getAlarmSubType();
			bindings[2] = alarm.getApId();
			bindings[3] = alarm.getTag1();
			bindings[4] = alarm.getTag2();
		}
		else {
			if(BeFaultConst.ALARM_TYPE_CLIENT == alarm.getAlarmType() && -1 == alarm.getTag1() && -1 == alarm.getTag2() && alarm.getTag3().isEmpty()){

					bindings = new Object[3];
					
					bindings[0] = alarm.getAlarmType();
					bindings[1] = alarm.getAlarmSubType();
					bindings[2] = alarm.getApId();
			}
			else {
				whereString.append(" AND tag1=:s4 AND tag2=:s5");
				whereString.append(" AND tag3=:s6");
				bindings = new Object[6];
				
				bindings[0] = alarm.getAlarmType();
				bindings[1] = alarm.getAlarmSubType();
				bindings[2] = alarm.getApId();
				bindings[3] = alarm.getTag1();
				bindings[4] = alarm.getTag2();
				bindings[5] = alarm.getTag3();
			}
			
		}
		filter = new FilterParams(whereString.toString(), bindings);	
		return filter;
	}
	
	public void stopThread() {
		// stop thread
		AhAlarm alarm = new AhAlarm();
		alarm.setApId(SHUTDOWN_ID);
		eq.clear();
		eq.add(alarm);
	}

	public void startThread() {
		this.start();
	}

}
