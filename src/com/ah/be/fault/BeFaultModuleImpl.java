/**
 *@filename		BeFaultModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:53:16 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.fault;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.fault.hiveap.AhApAlarmMgmt;
import com.ah.be.fault.hiveap.AhApAlarmMgmtImpl;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.monitor.AhAlarm;
import com.ah.util.Tracer;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeFaultModuleImpl extends BaseModule implements BeFaultModule {

	private static final Tracer logt = new Tracer(BeFaultModuleImpl.class);
	/*
	 * 
	 */
	private final BlockingQueue<BeBaseEvent> snmpTrapQueue;

	/**
	 * feeling, 2007-10-29
	 */
	private final BlockingQueue<Object> trapToMailQueue;

	private final BlockingQueue<AhAlarm> alarmQueue;

	/**
	 * feeling, 2008-02-26
	 */
	private BlockingQueue<BeBaseEvent> trapCapwapQueue = new LinkedBlockingQueue<BeBaseEvent>(
			QueueSize);

	private static boolean blnRunTrapToMailProcess = false;
	/*
	 * 
	 */
	private AlarmProcessThread alarmProcess = null;

	private TrapCapwapThread trapCapwapProcess = null;

	private final static int QueueSize = 15000;
	
	private int										lostEventCount					= 0;
	
	private TrapToMailProcessThread mailProcess[] = null;
	private int mailProcessNum = 1;

	private ScheduledExecutorService timer = null;

	private TrapFilterProcessor filterProcessor = null;

	private AhApAlarmMgmt hiveApAlarmMgmt;

	/**
	 * Construct method
	 */
	public BeFaultModuleImpl() {
		setModuleId(BaseModule.ModuleID_Fault);
		setModuleName("BeFaultModule");
		this.getDebuger().setModuleId(BaseModule.ModuleID_Fault);
		this.getDebuger().setModuleName("BeFaultModule");
		snmpTrapQueue = new LinkedBlockingQueue<BeBaseEvent>(QueueSize);
		trapToMailQueue = new LinkedBlockingQueue<Object>(QueueSize);
		alarmQueue = new LinkedBlockingQueue<AhAlarm>(QueueSize);
		trapCapwapQueue = new LinkedBlockingQueue<BeBaseEvent>(QueueSize);

		String value = null;
		try {
			value = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_MAIL,
					ConfigUtil.KEY_CONCURRENT_NUM, "1");
			mailProcessNum = Integer.valueOf(value);
		} catch (NumberFormatException e) {
			DebugUtil.faultDebugWarn("String to Integer failed! value=" + value);
		}

		if (mailProcessNum > 9) {
			DebugUtil.faultDebugWarn("concurrent mail process num must between 1 and 9!");
			mailProcessNum = 9;
		}
		if (mailProcessNum < 1) {
			DebugUtil.faultDebugWarn("concurrent mail process num must between 1 and 9!");
			mailProcessNum = 1;
		}
		logt.info("BeFaultModuleImpl", "concurrent mail process num=" + mailProcessNum);
	}

	/**
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown() {
		if (trapCapwapProcess != null) {
			trapCapwapProcess.stopTrapProcess();
		}

		if (alarmProcess != null) {
			alarmProcess.stopThread();
		}

		if (this.mailProcess != null) {
			trapToMailQueue.clear();
			BeBaseEvent shutdownEvent = new AhShutdownEvent();
			for (int i = 0; i < mailProcessNum; i++) {
				trapToMailQueue.add(shutdownEvent);
			}
		}

		if (timer != null) {
			try {
				stopTimerTaskToRemoveEventAndAlarm();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (beDBRecordsClearProcessor != null) {
			beDBRecordsClearProcessor.shutdown();
		}

		if (trapCapwapProcess != null) {
			trapCapwapProcess.stopThread();
		}

		if (filterProcessor != null) {
			filterProcessor.shutdownTask();
		}
		return true;
	}

	/**
	 * @param arg_Trap
	 *            -
	 */
	public void addTrapToQueue(BeBaseEvent arg_Trap) {
		try {
			//snmpTrapQueue.add(arg_Trap);
			// CCHEN MODIFY
			if(trapCapwapQueue.offer(arg_Trap)) {
				if(lostEventCount > 0) {
					DebugUtil.performanceDebugError("BeFaultModuleImpl.addTrapToQueue, Lost "+ lostEventCount +" events");
					lostEventCount = 0;
				}
			}
			else {
				lostEventCount++;
				if ( 0 == (lostEventCount % 500))
					DebugUtil.performanceDebugError("BeFaultModuleImpl.addTrapToQueue, Lost "+ lostEventCount +" events");
			}
		} catch (Exception e) {
			lostEventCount++;
//			DebugUtil.faultDebugWarn("BeFaultModuleImpl.addTrapToQueue() catch exception: " + e);
		}
	}

	/**
	 * feeling 2007-10-29
	 * 
	 * @param arg_Trap
	 *            -
	 * @throws Exception
	 *             -
	 */
	public void addTrapToMailQueue(Object arg_Trap) throws Exception {
		DebugUtil.faultDebugInfo("add trap to mail queue, size=" + trapToMailQueue.size());

		// if queue full, remove 1000 from head, 2007-7-28, by Jun & Yang
		synchronized (trapToMailQueue) {
			if (trapToMailQueue.size() >= QueueSize) {
				DebugUtil.faultDebugWarn("mail queue is full, poll 1000 from head");
				for (int i = 0; i < 1000; i++) {
					trapToMailQueue.poll();
				}
			}
		}
		trapToMailQueue.add(arg_Trap);
	}

	public void addAlarmToQueue(AhAlarm alarm) {
		try {
			DebugUtil.faultDebugInfo("add alarm to queue, size=" + alarmQueue.size());

			synchronized (alarmQueue) {
				if (alarmQueue.size() >= QueueSize) {
					DebugUtil.faultDebugWarn("alarm queue is full, poll 1 from head");
					for (int i = 0; i < 1; i++) {
						alarmQueue.poll();
					}
				}
			}
			alarmQueue.add(alarm);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("BeFaultModuleImpl.addAlarmToQueue() catch exception: " + e);
		}
	}

	/**
	 * @see com.ah.be.fault.BeFaultModule#getTrapQueue()
	 */
	public BlockingQueue<BeBaseEvent> getTrapQueue() {
		// return this.TrapQueue;
		return this.snmpTrapQueue;
	}

	public BaseModule getModule() {
		return this;
	}

	private BeDBRecordsClearProcessor beDBRecordsClearProcessor;

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run() {
		trapCapwapProcess = new TrapCapwapThread(this);
		trapCapwapProcess.setName("Thread:trapCapwapProcess");
		trapCapwapProcess.start();

		alarmProcess = new AlarmProcessThread(this);
		alarmProcess.setName("Thread:AlarmProcessThread");
		alarmProcess.startThread();

		filterProcessor = new TrapFilterProcessor(this);
		filterProcessor.startTask();

		// for mail notification
		mailProcess = new TrapToMailProcessThread[mailProcessNum];
		for (int i = 0; i < mailProcessNum; i++) {
			mailProcess[i] = new TrapToMailProcessThread(this);
			mailProcess[i].setName("Thread:TrapToMailProcessThread-" + (i + 1));
			mailProcess[i].start();
		}
		// run timer and timer task
		runTimerTaskToRemoveEventAndAlarm();

		// run dbRecordClear task
		beDBRecordsClearProcessor = new BeDBRecordsClearProcessor();
		beDBRecordsClearProcessor.startTask();

		try {
			// alarms should be set to 'CAPWAP link is down'
			logt.info("run", "Updating CAPWAP link down alarm...");
			BoMgmt.getTrapMgmt().setCapwapAlarm2LinkDownByAll();
			logt.info("run", "CAPWAP link down alarm updated.");
		} catch (Exception e) {
			logt.error("run", "Update CAPWAP link down alarm error.", e);
		}

		return true;
	}

	@Override
	public boolean init() {
		hiveApAlarmMgmt = new AhApAlarmMgmtImpl();

		return true;
	}

	public BlockingQueue<Object> getTrapToMailQueue() {
		return trapToMailQueue;
	}

	public BlockingQueue<AhAlarm> getAlarmQueue() {
		return alarmQueue;
	}

	public boolean isRunTrapToMailProcess() {
		return blnRunTrapToMailProcess;
	}

	public void setRunTrapToMailProcess(boolean isRun) {
		blnRunTrapToMailProcess = isRun;
	}

	/*
	 * for cap wap trap access (non-Javadoc)
	 * 
	 * @see com.ah.be.app.BaseModule#eventDispatched(com.ah.be.event.BeBaseEvent)
	 */
	@Override
	public void eventDispatched(BeBaseEvent arg_Event) {
		super.eventDispatched(arg_Event);

		if (arg_Event.getEventType() != BeEventConst.COMMUNICATIONEVENTTYPE) {
			return;
		}

		BeCommunicationEvent event = (BeCommunicationEvent) arg_Event;
		switch (event.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_APDTLSAUTHORIZEEVENT:
			DebugUtil
					.faultDebugInfo("BeFaultModuleImpl:eventDispatched(): process ap dtls authorize event");
			addTrapToQueue(arg_Event);
			break;
		case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
		case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
			DebugUtil
					.faultDebugInfo("BeFaultModuleImpl:eventDispatched(): process ap connect/disconnect event:"
							+ event.getMsgType());

			if (!filterProcessor.isFilter(event))
				addTrapToQueue(arg_Event);
			break;
		case BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT:
			BeTrapEvent trapEvent = (BeTrapEvent)arg_Event;
			if(trapEvent.getTrapType() != BeTrapEvent.TYPE_CLIENTOSINFOMATION &&
					trapEvent.getTrapType() != BeTrapEvent.TYPE_CLIENT_SELF_REGISTER_INFO) {
				addTrapToQueue(arg_Event);
			}
			break;
		default:
			break;
		}
	}

	public void stopTimerTaskToRemoveEventAndAlarm() throws InterruptedException {
		if (!timer.isShutdown()) {
			// Disable new tasks from being submitted.
			timer.shutdown();

			// Wait a while for existing tasks to terminate.
			if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
				// Cancel currently executing tasks.
				timer.shutdownNow();
				// Wait a while for tasks to respond to being canceled.
				if (!timer.awaitTermination(10, TimeUnit.SECONDS)) {
					DebugUtil.faultDebugWarn("Remove event/alarm scheduler did not terminate.");

				}
			}

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
					"<BE Thread> Timer of removing events/alarms is shutdown");

		}
	}

	public void runTimerTaskToRemoveEventAndAlarm() {
		// if re-timer the interval value
		if (timer == null || timer.isShutdown()) {
			timer = Executors.newSingleThreadScheduledExecutor();
		}
		try {
			RemoveEventAlarmTimerTask eventAlarmTimerTask = new RemoveEventAlarmTimerTask();
			DebugUtil.faultDebugInfo("BeFaultModuleImpl: event/alarm removing timer begin running");
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
					"<BE Thread> Timer of removing events/alarms is running...");

			timer.scheduleWithFixedDelay(eventAlarmTimerTask, BeFaultConst.REMOVE_EVENT_ALARM_INTERVAL,
					BeFaultConst.REMOVE_EVENT_ALARM_INTERVAL, TimeUnit.SECONDS);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("BeFaultModulList: event/alarm removing timer failed, e="
					+ e.getMessage(), e);
			super.setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, e
					.getMessage());
		}
	}

	public BlockingQueue<BeBaseEvent> getTrapCapwapQueue() {
		return trapCapwapQueue;
	}

	/**
	 * send alarm API
	 * 
	 * @param hiveAp
	 *            -
	 * @param severity
	 *            -
	 * @param description
	 *            -
	 */
	public void sendAlarm(HiveAp hiveAp, short severity, String description) {
		try {
			AhAlarm alarm = new AhAlarm();
			alarm.setApId(hiveAp.getMacAddress());
			alarm.setApName(hiveAp.getHostName());
			alarm.setTrapDesc(description);
			alarm.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), hiveAp.getOwner()
					.getTimeZoneString()));
			alarm.setSeverity(severity);
			alarm.setAlarmType(BeFaultConst.ALARM_TYPE_CAPWAP);
			alarm.setOwner(hiveAp.getOwner());

			String where = " apId=:s1  AND alarmType=:s2 AND owner=:s3	";
			Object[] bindings = new Object[3];
			bindings[0] = hiveAp.getMacAddress();
			bindings[1] = BeFaultConst.ALARM_TYPE_CAPWAP;
			bindings[2] = hiveAp.getOwner();
			FilterParams filter = new FilterParams(where, bindings);

			BoMgmt.getTrapMgmt().queryAndUpdateAlarm(filter, alarm);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("BeFaultModuleImpl.sendAlarm(): catch exception", e);
		}
	}

	/**
	 * save alarm
	 * 
	 * @param apMac
	 *            mac of ap
	 * @param desc
	 *            description of alarm
	 * @param severity
	 *            severity of alarm
	 * @param type
	 * @param probablecause
	 * @param objectname
	 */
	public void saveAlarm(String apMac, String desc, int severity, int type, short subType, int tag1, int tag2,
			String objectName, String domainName, String hostName) {
		try {
			AhAlarm AlarmBo = new AhAlarm();
			AlarmBo.setApId(apMac);

			AlarmBo.setCode(0);
			AlarmBo.setTrapDesc(desc);
			AlarmBo.setModifyTimeStamp(AlarmBo.getTrapTimeStamp());
			AlarmBo.setSeverity((short) severity);
			AlarmBo.setAlarmSubType(subType);
			AlarmBo.setAlarmType((short) type);
			AlarmBo.setObjectName(objectName);
			AlarmBo.setTag1(tag1);
			AlarmBo.setTag2(tag2);
			
			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			HmDomain domain = null;
			if (ap != null) {
				domain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				AlarmBo.setApName(ap.getHostname());
				AlarmBo.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), domain
						.getTimeZoneString()));
				AlarmBo.setOwner(domain);
			} else if (domainName != null) {
				domain = CacheMgmt.getInstance().getCacheDomainByName(domainName);
				if (domain == null) {
					domain = BoMgmt.getDomainMgmt().getHomeDomain();
				}
				AlarmBo.setApName(hostName);
				AlarmBo.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), domain
						.getTimeZoneString()));

				AlarmBo.setOwner(domain);
			} else {
				DebugUtil.faultDebugWarn("AP " + apMac + " not exist in cache!");
				return;
			}

			addAlarmToQueue(AlarmBo);
			addTrapToMailQueue(AlarmBo);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("FaultModule::save Alarm into queue failed", e);
		}
	}

	public void setCapwapTrapFilterInterval(int filterInterval) {
		if (null != filterProcessor)
			filterProcessor.setFilterParameters(filterInterval);
	}

	public AhApAlarmMgmt getHiveApAlarmMgmt() {
		return hiveApAlarmMgmt;
	}

}