package com.ah.be.fault;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeAPDTLSAuthorizeEvent;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.config.event.AhDeviceRebootResultEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.AhTimeoutEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.be.performance.db.BulkOperationProcessor;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.util.MgrUtil;

public class TrapCapwapThread extends Thread {

	private BlockingQueue<BeBaseEvent>	eq;

	private final BeFaultModule			Parent;

	private boolean						isContinue	= true;

	// define insert queue and max size
	private static final int MAX_CACHE_SIZE = 100;

	private final List<AhEvent> insertList;

	private final List<APConnectHistoryInfo> insertList2;

	// define schedule service and refresh timer interval
	private ScheduledExecutorService scheduler_ForClient;

	private static final int REFRESH_TIMER_INTERVAL = 5;

	class RefreshTimerGenerator implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());
			try {
				AhTimeoutEvent timer = new AhTimeoutEvent();
				addEvent(timer);
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_TRACER,
						"RefreshTimerGenerator:run() catch Exception: " + e.getMessage());
				BeLogTools.error(HmLogConst.M_FAULT, "RefreshTimerGenerator:run() catch Exception",
						e);
			} catch (Error e) {
				BeLogTools.error(HmLogConst.M_TRACER, "TrapToMailProcessThread:run() catch Error: "
						+ e.getMessage());
				BeLogTools
						.error(HmLogConst.M_FAULT, "TrapToMailProcessThread:run() catch Error", e);
			}
		}
	}

	/**
	 * add event to client session queue
	 *
	 * @param event
	 *            -
	 */
	public void addEvent(BeBaseEvent event) {
		try {
			eq.add(event);
		} catch (Exception e) {
			DebugUtil.faultDebugError("Exception while add event to trap queue", e);
		}
	}

	private synchronized void shutdownScheduler() {
		if (scheduler_ForClient == null || scheduler_ForClient.isShutdown()) {
			return;
		}

		// Disable new tasks from being submitted.
		scheduler_ForClient.shutdown();
	}

	public void stopTrapProcess() {
		// stop thread
		// isContinue = false;
		// SnmpTrapEvent stopThreadEvent = new SnmpTrapEvent();
		// trapQueue.add(stopThreadEvent);

		try {
			shutdownScheduler();
		} catch (Exception e) {
			DebugUtil.faultDebugError("Exception in shutting down scheduler.", e);
		}

		BeBaseEvent shutdownEvent = new AhShutdownEvent();
		eq.clear();
		eq.add(shutdownEvent);
	}

	/**
	 * refresh db from cache list
	 */
	private void refreshDb() {
		if (insertList.isEmpty()) {
			return;
		}

		try {
			BulkUpdateUtil.bulkInsertForEvent(insertList);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("TrapProcessThread.refreshDb(): Failed to insert db.", e);
		}
		insertList.clear();
	}

	private void refreshDb2() {
		if (insertList2.isEmpty()) {
			return;
		}

		try {
			BulkOperationProcessor.addBoList(APConnectHistoryInfo.class, insertList2);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("TrapProcessThread.refreshDb2(): Failed to insert db.", e);
		}
		insertList2.clear();
	}

	private void saveTrapEventToDB(BeTrapEvent arg_Event) {
		DebugUtil.faultDebugInfo("TrapProcessThread.saveTrapEventToDB::trap type is "
				+ arg_Event.getTrapType());

		if (arg_Event.getObjectName() == null || arg_Event.getObjectName().equals("")) {
			arg_Event.setObjectName("-");
		}
		short subType = BeFaultConst.ALARM_SUBTYPE_UNKNOWN;

		if (arg_Event.getTrapType() == BeTrapEvent.TYPE_CAPWAP_EVENT) {
			// for capwap-trap
			subType = arg_Event.getProbableCause();
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_CAPWAP, subType);
			saveToAPConnectHistory(arg_Event);
		} else if (arg_Event.getTrapType() == BeTrapEvent.TYPE_FAILURE) {
			// for snmp-trap
			// for detail info
			subType = arg_Event.getProbableCause();
			arg_Event.setAlarmTag1(BeFaultConst.ALARM_TAG1_FAILURE);
			
			//after devcie reboot, if exists CLI failed, need display these CLIs and store into update result table.
			if(subType == BeFaultConst.ALARM_SUBTYPE_FAILURE_AHCONFFAILURE){
				sendRebootCLIErrorEvent(arg_Event);
			}

			if (arg_Event.getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[8])) {
				saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_SNMP_RADIO, subType);
			} else {
				saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_SNMP_CONFIG, subType);
			}
		} else if (arg_Event.getTrapType() == BeTrapEvent.TYPE_SECURITY_ALARM) {
			// for security alarm trap
			arg_Event.setObjectName("Spoofed BSSIDs");
			arg_Event.setAlarmTag1(arg_Event.getIfIndex());
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_DOS, BeFaultConst.ALARM_SUBTYPE_DOS_BSSIDSPOOLING);
		} else if (arg_Event.getTrapType() == BeTrapEvent.TYPE_TIMEBOMBWARNING) {
			// for capwap-trap
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_BOMB_WARNING, BeFaultConst.ALARM_SUBTYPE_BOMB_WARNING_LICENSEEXPIRATION);
		} else if (arg_Event.getTrapType() == BeTrapEvent.TYPE_KERNEL_DUMP_EVENT) {
			// for kernel dump trap
			subType = arg_Event.getProbableCause();
			arg_Event.setAlarmTag1(BeFaultConst.ALARM_TAG1_KERNELDUMP);
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_SNMP_RADIO, subType);
		} else if (arg_Event.getTrapType() == BeTrapEvent.TYPE_INTERFERENCEALERT) {
			// for interference alert
			/*
			 * failure set: 1 - alert, 2 - clear alert
			 *
			 * failure flag: true-alert, false-clear alert
			 *
			 * Severity: 4 - major, 3 - minor, 2 - info.
			 *
			 * Interference situation determines the severity level:
			 *
			 * Use severity level 4: If it is caused by CRC error rate or by running average of
			 * Interference CU above their thresholds
			 *
			 * Use severity level 3: if it is caused by short period means average of interference
			 * CU
			 *
			 * Use severity level 2: if it us caused by snapshot interference CU
			 *
			 * Adjust: in order to display in GUI, when severity is 2, change it to 3.
			 */
			if (!arg_Event.isFailureFlag()) {
				if (arg_Event.getSeverity() != BeFaultConst.ALERT_SERVERITY_CLEAR) {
					DebugUtil.faultDebugWarn("adjust severity from " + arg_Event.getSeverity()
							+ " to clear");
					arg_Event.setSeverity((byte)BeFaultConst.ALERT_SERVERITY_CLEAR);
				}
			}
			if (arg_Event.getSeverity() == BeFaultConst.ALERT_SERVERITY_INFO) {
				DebugUtil.faultDebugWarn("adjust severity from info to minor");
				arg_Event.setSeverity((byte)BeFaultConst.ALERT_SERVERITY_MINOR);
			}

			// As interference include wifi0, wifi1, in order to fix bug by little workload, we set
			// the ifIndex to alarm.probableCase. 2009.9.10, Jonathan
			arg_Event.setAlarmTag1(BeFaultConst.ALARM_TAG1_INTERFERENCEALERT);
			arg_Event.setAlarmTag2(arg_Event.getIfIndex());
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_SNMP_RADIO, BeFaultConst.ALARM_SUBTYPE_FAILURE_INTERFERENCEALERT);
		} else if(arg_Event.getTrapType() == BeTrapEvent.TYPE_AD_ALARM){
			// save AD/LDAP alert
			byte reasonType = arg_Event.getReasonType();
			if(reasonType == BeFaultConst.TRAP_LDAP_ALERT_TYPEX[0].getKey()){
				subType = BeFaultConst.ALARM_SUBTYPE_AD_NETJOIN;
				arg_Event.setAlarmTag3(BeFaultConst.TRAP_LDAP_ALERT_TYPEX[0].getValue());
			} else if (reasonType == BeFaultConst.TRAP_LDAP_ALERT_TYPEX[1].getKey()){
				subType = BeFaultConst.ALARM_SUBTYPE_AD_BINDDN;
				arg_Event.setAlarmTag3(BeFaultConst.TRAP_LDAP_ALERT_TYPEX[1].getValue());
			}else {
				arg_Event.setAlarmTag3("Unknown Type");
			}
			arg_Event.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[16]);
			saveToAlarmDB(arg_Event, BeFaultConst.ALARM_TYPE_AD, subType);
		} else if(arg_Event.getTrapType() == BeTrapEvent.TYPE_CAPWAP_DELAY) {
			arg_Event.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[9]);
			updateDelayTime(arg_Event);
			//Add the switch for CAPWAP delay alarm from Guadalupe
			saveToAlarmDBForDelayAlarm(arg_Event, BeFaultConst.ALARM_TYPE_CAPWAP, BeFaultConst.ALARM_SUBTYPE_CAPWAP_DELAY);
		} else if(arg_Event.getTrapType() == BeTrapEvent.TYPE_ALARM) {
			saveToAlarmDB(arg_Event);
		}
		else {
			// not important, so save to event table
			saveToEventDB(arg_Event);
		}
	}

	private void updateDelayTime(BeTrapEvent arg_Event) {
		// set clause
		int delayTime;
		short connectStatus;

		// update capwap delay time
		int severity = arg_Event.getSeverity();
		if (severity == BeFaultConst.ALERT_SERVERITY_CLEAR) {
			connectStatus = HiveAp.CONNECT_UP;
			delayTime = (int) arg_Event.getAvgDelay();
		} else {
			if (severity == BeFaultConst.ALERT_SERVERITY_MINOR) {
				connectStatus = HiveAp.CONNECT_UP_MINOR;
			} else if (severity == BeFaultConst.ALERT_SERVERITY_MAJOR) {
				connectStatus = HiveAp.CONNECT_UP_MAJOR;
			} else {
				connectStatus = HiveAp.CONNECT_UP;
			}
			delayTime = (int) arg_Event.getCurDelay();
		}

		try {
			BoMgmt.getHiveApMgmt().updateDelayTime(arg_Event.getApMac(), delayTime, connectStatus);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("TrapProcess::update delay time failed", e);
		}
	}

	private void saveToEventDB(BeTrapEvent trapEvent) {
		CurrentLoadCache.getInstance().increaseNumberOfEvent();

		AhEvent eventBo = new AhEvent();

		// 1.set public value-1(apId, apName, code)
		eventBo.setApId(trapEvent.getSimpleHiveAp().getMacAddress());
		eventBo.setApName(trapEvent.getSimpleHiveAp().getHostname());
		if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE
				&& trapEvent.getObjectType() == BeTrapEvent.TRAP_OBJECT_TYPE_CLIENT_INDEX
				&& trapEvent.getCurrentState() == AhEvent.AH_STATE_DOWN) {
			int a = trapEvent.getCode() & 0x7ffffff8;
			eventBo.setCode(a);
		} else {
			eventBo.setCode(trapEvent.getCode());
		}
		eventBo.setObjectName(trapEvent.getObjectName());
		eventBo.setTrapDesc(trapEvent.getDescribe());
		eventBo.setTrapTimeStamp(new HmTimeStamp(trapEvent.getTimeStamp(), trapEvent.getTimeZone()));
		eventBo.setTag1(trapEvent.getEventTag1());

		// 2.set event type by trap type
		if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENTINFOMATION) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_CLIENTINFO_CHANGE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_THRESHOLDCROSSING) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_THRESHOLD_CROSSING);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_STATECHANGE) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_STATE_CHANGE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_POE) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_POE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CHANNELPOWERCHANGE) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_CHANNELPOWERCHANGE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_AIRSCREENREPORT) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_AIRSCREEN);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP) {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_INTERFACECLIENT);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_PSE_ERROR){
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_PSE);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CWP_INFO){
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_CWP_INFO);
		} else if (trapEvent.getTrapType() == BeTrapEvent.TYPE_POWER_MODE_CHANGE){
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_POWERMODECHANGE);
		} else {
			eventBo.setEventType(AhEvent.AH_EVENT_TYPE_UNKNOW);
		}

		// 3.set event attributes by event type
		switch (eventBo.getEventType()) {
		case AhEvent.AH_EVENT_TYPE_THRESHOLD_CROSSING:
			eventBo.setCurValue(trapEvent.getCurrentValue());
			eventBo.setThresholdHigh(trapEvent.getThresholdhigh());
			eventBo.setThresholdLow(trapEvent.getThresholdlow());
			break;
		case AhEvent.AH_EVENT_TYPE_AIRSCREEN:
			eventBo.setAsReportType(trapEvent.getReportType());
			eventBo.setAsNameType(trapEvent.getNameType());
			eventBo.setAsName(trapEvent.getName());
			eventBo.setAsSourceType(trapEvent.getSourceType());
			eventBo.setAsSourceID(trapEvent.getSourceID());
			eventBo.setAsTimeStamp(new HmTimeStamp(trapEvent.getAirScreenTime(), trapEvent
					.getMessageTimeZone()));
			eventBo.setAsRuleName(trapEvent.getRuleName());
			eventBo.setAsInstanceID(trapEvent.getInstanceID());
			break;
		case AhEvent.AH_EVENT_TYPE_STATE_CHANGE:
			eventBo.setPreviousState((short) trapEvent.getPreviousState());
			eventBo.setCurrentState((short) trapEvent.getCurrentState());
			break;
		case AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE:
			eventBo.setIfIndex(trapEvent.getIfIndex());
			eventBo.setRemoteId(trapEvent.getRemoteID());
			eventBo.setCurrentState((short) trapEvent.getCurrentState());
			eventBo.setObjectType((short) trapEvent.getObjectType());
			eventBo.setClientHostName(trapEvent.getClientHostName());
			eventBo.setClientIp(trapEvent.getClientIP());
			eventBo.setClientUserName(trapEvent.getClientUserName());
			eventBo.setSsid(trapEvent.getClientSSID());
			eventBo.setClientCWPUsed(trapEvent.getClientCWPUsed());
			eventBo.setClientAuthMethod(trapEvent.getClientAuthMethod());
			eventBo.setClientEncryptionMethod(trapEvent.getClientEncryptionMethod());
			eventBo.setClientMacProtocol(trapEvent.getClientMacProtocol());
			eventBo.setClientVLAN(trapEvent.getClientVLAN());
			eventBo.setClientUserProfId(trapEvent.getClientUserProfId());
			eventBo.setClientChannel(trapEvent.getClientChannel());
			eventBo.setClientBSSID(trapEvent.getClientBSSID());
			eventBo.setAssociationTime(trapEvent.getAssociationTime());
			break;
		case AhEvent.AH_EVENT_TYPE_CLIENTINFO_CHANGE:
			eventBo.setRemoteId(trapEvent.getRemoteID());
			eventBo.setClientHostName(trapEvent.getClientHostName());
			eventBo.setClientIp(trapEvent.getClientIP());
			eventBo.setClientUserName(trapEvent.getClientUserName());
			eventBo.setSsid(trapEvent.getClientSSID());
			eventBo.setClientCWPUsed(trapEvent.getClientCWPUsed());
			break;
		case AhEvent.AH_EVENT_TYPE_POE:
			eventBo.setPowerSource(trapEvent.getPowerSource());
			eventBo.setPoEEth0On(trapEvent.getPoEEth0On());
			eventBo.setPoEEth0Pwr(trapEvent.getPoEEth0Pwr());
			eventBo.setPoEEth1On(trapEvent.getPoEEth1On());
			eventBo.setPoEEth1Pwr(trapEvent.getPoEEth1Pwr());
			eventBo.setPoEEth0MaxSpeed(trapEvent.getPoEEth0MaxSpeed());
			eventBo.setPoEEth1MaxSpeed(trapEvent.getPoEEth1MaxSpeed());
			eventBo.setPoEWifi0Setting(trapEvent.getPoEWifi0Setting());
			eventBo.setPoEWifi1Setting(trapEvent.getPoEWifi1Setting());
			eventBo.setPoEWifi2Setting(trapEvent.getPoEWifi2Setting());
			break;
		case AhEvent.AH_EVENT_TYPE_CHANNELPOWERCHANGE:
			eventBo.setIfIndex(trapEvent.getIfIndex());
			eventBo.setRadioChannel(trapEvent.getRadioChannel());
			eventBo.setRadioTxPower(trapEvent.getRadioTxPower());

			// 2008.9.19, update AhLatestRadioAttribute - hm_latestradioattribute
			updateLatestRadioData(eventBo.getApId(), eventBo.getIfIndex(), eventBo
					.getRadioChannel(), eventBo.getRadioTxPower());
			break;
		case AhEvent.AH_EVENT_TYPE_INTERFACECLIENT:
			eventBo.setAsSourceType(trapEvent.getSourceType());
			eventBo.setIfIndex(trapEvent.getIfIndex());
			eventBo.setRemoteId(trapEvent.getRemoteID());
			eventBo.setSsid(trapEvent.getClientSSID());
			eventBo.setAlertType(trapEvent.getAlertType());
			eventBo.setThresholdValue(trapEvent.getThresholdValue());
			eventBo.setShorttermValue(trapEvent.getShorttermValue());
			eventBo.setSnapshotValue(trapEvent.getSnapshotValue());
			break;
		case AhEvent.AH_EVENT_TYPE_PSE:
			eventBo.setObjectName(trapEvent.getObjectName());
			eventBo.setTrapDesc(trapEvent.getDescribe());
			break;
		default:
			break;
		}

		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getSimpleHiveAp().getMacAddress());
		if (ap != null) {
			eventBo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));
		} else {
			eventBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		}

		try {
			// long result = BoMgmt.getTrapMgmt().createEvent(EventBo);
			createEvent(eventBo);
			// DebugUtil.faultDebugInfo("TrapMgmt.createEvent return value= " + result);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("saveToEventDB", e);
			e.printStackTrace();
		}
	}
	
	private void saveToAlarmDB(BeTrapEvent arg_Event) {
		List<AhAlarm> alarmList = arg_Event.getAlarmList();
		for (AhAlarm alarm : alarmList) {
			Parent.addAlarmToQueue(alarm);
		}
	}
	
	private void saveToAlarmDB(BeTrapEvent arg_Event, short type, short subType) {
		CurrentLoadCache.getInstance().increaseNumberOfAlarm();

		try {
			AhAlarm AlarmBo = new AhAlarm();
			AlarmBo.setApId(arg_Event.getSimpleHiveAp().getMacAddress());
			AlarmBo.setApName(arg_Event.getSimpleHiveAp().getHostname());
			AlarmBo.setCode(arg_Event.getCode());
			AlarmBo.setTrapDesc(arg_Event.getDescribe());
			AlarmBo.setTrapTimeStamp(new HmTimeStamp(arg_Event.getTimeStamp(), arg_Event
					.getTimeZone()));
			AlarmBo.setModifyTimeStamp(AlarmBo.getTrapTimeStamp());
			AlarmBo.setSeverity((short) arg_Event.getSeverity());
			AlarmBo.setAlarmSubType(subType);
			AlarmBo.setAlarmType(type);
			AlarmBo.setTag1(arg_Event.getAlarmTag1());
			AlarmBo.setTag2(arg_Event.getAlarmTag2());
			AlarmBo.setTag3(arg_Event.getAlarmTag3());
			AlarmBo.setObjectName(arg_Event.getObjectName());

			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(arg_Event.getApMac());
			if (ap != null) {
				AlarmBo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));
			} else {
				DebugUtil.faultDebugWarn("AP " + arg_Event.getApMac() + " not exist in cache!");

				// query from DB
				HiveAp ap2 = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", arg_Event
						.getApMac());
				if (ap2 != null) {
					AlarmBo.setOwner(ap2.getOwner());
				} else {
					DebugUtil.faultDebugWarn("AP " + arg_Event.getApMac() + " not exist in DB!");

					if (arg_Event.getDomainName() != null) {
						HmDomain domain = CacheMgmt.getInstance().getCacheDomainByName(
								arg_Event.getDomainName());
						if (domain != null) {
							AlarmBo.setOwner(domain);
						} else {
							AlarmBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
						}
					} else {
						AlarmBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
					}
				}
			}

			if (!arg_Event.isFailureFlag()) {
				AlarmBo.setSeverity((short) BeFaultConst.ALERT_SERVERITY_CLEAR);
			}

			Parent.addAlarmToQueue(AlarmBo);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("TrapProcess::save Alarm into queue failed", e);
		}
	}
	
	
private boolean checkDelayAlarmOff(BeTrapEvent arg_Event){
	if(null !=arg_Event && arg_Event.getTrapType() == BeTrapEvent.TYPE_CAPWAP_DELAY){
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(arg_Event.getApMac());
		if(null != ap){
			if(!ap.isEnableDelayAlarm()){
				return true;
			}
		}else{
			DebugUtil.faultDebugWarn("AP " + arg_Event.getApMac() + " not exist in cache!");

			// query from DB
			HiveAp ap2 = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", arg_Event
					.getApMac());
			if (null != ap2){
				if(!ap2.isEnableDelayAlarm()){
					return true;
				}
			}
		}
	}
	
	return false;
}
	
private void saveToAlarmDBForDelayAlarm(BeTrapEvent arg_Event, short type, short subType) {
		
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(arg_Event.getApMac());
		
		AhAlarm AlarmBo = new AhAlarm();
		
		if (ap != null) {
			if(ap.isEnableDelayAlarm()){
				AlarmBo.setOwner(CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId()));
			}else{
				return;
			}
		} else {
			DebugUtil.faultDebugWarn("AP " + arg_Event.getApMac() + " not exist in cache!");

			// query from DB
			HiveAp ap2 = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", arg_Event
					.getApMac());
			if (ap2 != null) {
				if(ap2.isEnableDelayAlarm()){
					AlarmBo.setOwner(ap2.getOwner());
				}else{
					return;
				}
			} else {
				DebugUtil.faultDebugWarn("AP " + arg_Event.getApMac() + " not exist in DB!");

				if (arg_Event.getDomainName() != null) {
					HmDomain domain = CacheMgmt.getInstance().getCacheDomainByName(
							arg_Event.getDomainName());
					if (domain != null) {
						AlarmBo.setOwner(domain);
					} else {
						AlarmBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
					}
				} else {
					AlarmBo.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
				}
			}
		}
		
		CurrentLoadCache.getInstance().increaseNumberOfAlarm();
		
		try{
			AlarmBo.setApId(arg_Event.getSimpleHiveAp().getMacAddress());
			AlarmBo.setApName(arg_Event.getSimpleHiveAp().getHostname());
			AlarmBo.setCode(arg_Event.getCode());
			AlarmBo.setTrapDesc(arg_Event.getDescribe());
			AlarmBo.setTrapTimeStamp(new HmTimeStamp(arg_Event.getTimeStamp(), arg_Event
					.getTimeZone()));
			AlarmBo.setModifyTimeStamp(AlarmBo.getTrapTimeStamp());
			AlarmBo.setSeverity((short) arg_Event.getSeverity());
			AlarmBo.setAlarmSubType(subType);
			AlarmBo.setAlarmType(type);
			AlarmBo.setTag1(arg_Event.getAlarmTag1());
			AlarmBo.setTag2(arg_Event.getAlarmTag2());
			AlarmBo.setTag3(arg_Event.getAlarmTag3());
			AlarmBo.setObjectName(arg_Event.getObjectName());
			
			if (!arg_Event.isFailureFlag()) {
				AlarmBo.setSeverity((short) BeFaultConst.ALERT_SERVERITY_CLEAR);
			}

			Parent.addAlarmToQueue(AlarmBo);
		}catch(Exception e){
			DebugUtil.faultDebugWarn("TrapProcess::save Alarm into queue failed", e);
		}
	}

	/**
	 * update Latest radio data 2009.9.19
	 *
	 * @param apMac -
	 * @param ifIndex -
	 * @param radioChannel -
	 * @param radioRxPower -
	 * @return -
	 */
	private int updateLatestRadioData(String apMac, int ifIndex, int radioChannel, int radioRxPower) {
		try {
			return QueryUtil.updateBos(AhLatestRadioAttribute.class, "radioChannel = :s1,radioTxPower = :s2,time = :s3",
					"apMac = :s4 and ifIndex = :s5",
					new Object[] { (long)radioChannel, (long)radioRxPower,
							System.currentTimeMillis(), apMac, ifIndex });
		} catch (Exception e) {
			DebugUtil.faultDebugError("updateLatestRadioData exception", e);
			return -1;
		}
	}

	private void saveToAPConnectHistory(BeTrapEvent argEvent) {
		try {
			APConnectHistoryInfo apConnect = new APConnectHistoryInfo();
			apConnect.setApId(argEvent.getSimpleHiveAp().getMacAddress());
			apConnect.setApName(argEvent.getSimpleHiveAp().getHostname());
			apConnect.setTrapTime(System.currentTimeMillis());
			apConnect.setTrapMessage(argEvent.getDescribe());
			if (argEvent.isFailureFlag()) {
				apConnect.setTrapType(APConnectHistoryInfo.TRAP_DISCONNECT);
			} else {
				apConnect.setTrapType(APConnectHistoryInfo.TRAP_CONNECT);
			}

			HmDomain domain;
			Long mapId = null;
			MapContainerNode mapContainer;

			String apId = apConnect.getApId();
			SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(apId);
			if (ap != null) {
				domain = CacheMgmt.getInstance().getCacheDomainById(ap.getDomainId());
				mapId = ap.getMapContainerId();
			} else {
				DebugUtil.faultDebugWarn("AP " + apId + " not exist in cache!");

				// query from DB
				HiveAp ap2 = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apId);
				if (ap2 != null) {
					domain = ap2.getOwner();
					mapContainer = ap2.getMapContainer();
					if (mapContainer != null) {
						mapId = mapContainer.getId();
					}
				} else {
					DebugUtil.faultDebugWarn("AP " + apId + " not exist in DB!");

					if (argEvent.getDomainName() != null) {
						domain = CacheMgmt.getInstance().getCacheDomainByName(
								argEvent.getDomainName());
						if (domain == null) {
							domain = BoMgmt.getDomainMgmt().getHomeDomain();
						}
					} else {
						domain = BoMgmt.getDomainMgmt().getHomeDomain();
					}
				}
			}

			apConnect.setOwner(domain);
			apConnect.setMapId(mapId);
			createAPConnectHistory(apConnect);
			// QueryUtil.createBo(apConnect);
		} catch (Exception e) {
			DebugUtil.faultDebugWarn("saveToAPConnectHistory failed!", e);
		}
	}

	private void createEvent(AhEvent event) {
		insertList.add(event);
		BoObserver.notifyListeners(new BoEvent<>(event, BoEventType.CREATED));
	}

	private void createAPConnectHistory(APConnectHistoryInfo apConnect) {
		insertList2.add(apConnect);
	}

	public TrapCapwapThread(BeFaultModule arg_Module) {
		Parent = arg_Module;
		insertList = new ArrayList<>(MAX_CACHE_SIZE);
		insertList2 = new ArrayList<>(MAX_CACHE_SIZE);
	}

	@Override
	public void run() {
		eq = Parent.getTrapCapwapQueue();
		if (eq == null) {
			DebugUtil.faultDebugWarn(" TrapCapwap thread :: trap capwap queue is null");
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Trap CAPWAP processor is running...");

		// start timeout factory scheduler
		if (scheduler_ForClient == null || scheduler_ForClient.isShutdown()) {
			scheduler_ForClient = Executors.newSingleThreadScheduledExecutor();
			scheduler_ForClient.scheduleWithFixedDelay(new RefreshTimerGenerator(),
					REFRESH_TIMER_INTERVAL, REFRESH_TIMER_INTERVAL, TimeUnit.SECONDS);
		}
			
		while (isContinue) {
			try {
				BeBaseEvent arg_Event = eq.take();

				// prevent save shut down event into db, follow francis suggestions
				if (arg_Event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
					BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
							"<BE Thread> Trap processor is shutdown.");
					break;
				}

				// if timeout, refresh DB
				if (arg_Event.getEventType() == BeEventConst.AH_TIMEOUT_EVENT) {
					refreshDb();
					refreshDb2();
					continue;
				}

				// if queue is full, refresh DB
				if (insertList.size() >= MAX_CACHE_SIZE) {
					refreshDb();
				}
				if (insertList2.size() >= MAX_CACHE_SIZE) {
					refreshDb2();
				}

				BeCommunicationEvent event = (BeCommunicationEvent) arg_Event;
				switch (event.getMsgType()) {
				case BeCommunicationConstant.MESSAGETYPE_APDTLSAUTHORIZEEVENT:
					try {
						event.parsePacket();
						BeAPDTLSAuthorizeEvent dtlsEvent = (BeAPDTLSAuthorizeEvent) event;
						if (dtlsEvent.getAuthState() == BeAPDTLSAuthorizeEvent.AUTHSTATE_FAIL) {
							changeDtlsHandshakeTrapToBeTarpEvent(dtlsEvent);
						}
					} catch (Exception e) {
						DebugUtil
								.faultDebugWarn(
										"BeFaultModuleImpl:eventDispatched:: exchange Communication (belong cap wap) trap to snmp failed:: ",
										e);
					}
					break;
				case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
				case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
					event.parsePacket();
					BeAPConnectEvent connectEvent = (BeAPConnectEvent) event;
					if(connectEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_APCONNECT)
					{
						//save passphrase alarm
						if(connectEvent.getPassPhraseType() == 2) {
							AhAppContainer.getBeFaultModule().saveAlarm(event.getApMac(), "Default DTLS passphrase has been replaced.",
									BeFaultConst.ALERT_SERVERITY_CLEAR, BeFaultConst.ALARM_TYPE_CAPWAP,
									BeFaultConst.ALARM_SUBTYPE_CAPWAP_PASSPHRASE,connectEvent.getCapwapClientType(), 0, BeFaultConst.TRAP_SEND_MAIL_TYPEX[9],null,null);
						}
						else {
							AhAppContainer.getBeFaultModule().saveAlarm(event.getApMac(),
									"Default DTLS passphrase is in use. Push a complete config to update the passphrase automatically, or set it manually and push a complete or delta config.",
									BeFaultConst.ALERT_SERVERITY_MAJOR, BeFaultConst.ALARM_TYPE_CAPWAP,
									BeFaultConst.ALARM_SUBTYPE_CAPWAP_PASSPHRASE,connectEvent.getCapwapClientType(), 0, BeFaultConst.TRAP_SEND_MAIL_TYPEX[9],
									connectEvent.getDomainName(),connectEvent.getWtpName());
						}
					}
					changeApConnectTrap(connectEvent);
					break;
				case BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT:
					// get trap event
					BeTrapEvent trapEvent = (BeTrapEvent) event;

					if (trapEvent.getTrapType() == BeTrapEvent.TYPE_TIMEBOMBWARNING) {
						trapEvent.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[12]);
					}

					SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(trapEvent.getApMac());
					if (ap == null
							|| (ap.getManageStatus() != HiveAp.STATUS_MANAGED && ap.getManageStatus() != HiveAp.STATUS_NEW)) {
						continue;
					}

					// CAPWAP Down event doesn't include APName info, so set it from cache.
					if (trapEvent.getApName().equals("")) {
						trapEvent.setApName(ap.getHostname());
					}

					long begin = System.currentTimeMillis();
					// save the trap to DB (alarm or event)
					saveTrapEventToDB(trapEvent);
					DebugUtil.faultDebugInfo("save trap cost: " + ap.getMacAddress() + ", "
							+ (System.currentTimeMillis() - begin));
					
					//add the switch for CAPWAP dealy alarm from Guadalupe
					if(!checkDelayAlarmOff(trapEvent)){
						Parent.addTrapToMailQueue(trapEvent);
					}
					
					break;
				}
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_TRACER, "TrapCapwapThread:run() catch Exception: "
						+ e.getMessage());
				BeLogTools.error(HmLogConst.M_FAULT, "TrapCapwapThread:run() catch Exception", e);
			} catch (Error e) {
				BeLogTools.error(HmLogConst.M_TRACER, "TrapCapwapThread:run() catch Error: "
						+ e.getMessage());
				BeLogTools.error(HmLogConst.M_FAULT, "TrapCapwapThread:run() catch Error", e);
			}
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_FAULT,
				"<BE Thread> Trap CAPWAP processor is shutdown.");
	}

	private void changeDtlsHandshakeTrapToBeTarpEvent(BeAPDTLSAuthorizeEvent arg_Event) {
		BeTrapEvent trapEvent = new BeTrapEvent();
		trapEvent.setEventType(arg_Event.getEventType());
		trapEvent.setApMac(arg_Event.getApMac());
	    trapEvent.setApName(null == arg_Event.getSimpleHiveAp() ? "" : arg_Event
				.getSimpleHiveAp().getHostname());
		trapEvent.setTrapType(BeTrapEvent.TYPE_DTLS_HANDSHAKE_FAIL);
		trapEvent.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[9]);
		// snmp_Event.setAhTime(System.currentTimeMillis());
		// snmp_Event.setAhTimeDisplay(AhDateTimeUtil
		// .getCurrentDate(AhDateTimeUtil.DATA_FORMAT_AMERICAN));
		trapEvent.setTimeStamp(arg_Event.getMessageTimeStamp());
		trapEvent.setTimeZone(arg_Event.getMessageTimeZone());
		trapEvent.setDescribe(arg_Event.getDescription());
		Parent.addTrapToQueue(trapEvent);
	}

	private void changeApConnectTrap(BeAPConnectEvent arg_Event) {
		if (null == arg_Event) {
			return;
		}
		BeTrapEvent trapEvent = new BeTrapEvent();

		trapEvent.setEventType(arg_Event.getEventType());
		trapEvent.setApMac(arg_Event.getApMac());
		trapEvent.setApName(arg_Event.getWtpName());
		trapEvent.setTrapType(BeTrapEvent.TYPE_CAPWAP_EVENT);
		trapEvent.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[9]);
		// snmp_Event.setAhTime(System.currentTimeMillis());
		// snmp_Event.setAhTimeDisplay(AhDateTimeUtil
		// .getCurrentDate(AhDateTimeUtil.DATA_FORMAT_AMERICAN));
		trapEvent.setTimeStamp(arg_Event.getMessageTimeStamp());
		trapEvent.setTimeZone(arg_Event.getMessageTimeZone());
		trapEvent.setProbableCause((byte)BeFaultConst.ALARM_SUBTYPE_CAPWAP_LINK);
		trapEvent.setAlarmTag1(arg_Event.getCapwapClientType());

		if (arg_Event.getMsgType() == BeCommunicationConstant.MESSAGETYPE_APCONNECT) {
//			snmp_Event.setAhProbableCause((short) BeFaultConst.TRAP_DESC_CAPWAP_LINK_UPX.getKey());
			// modify trap description from 'link UP' to reconnect reason. 2009.3.5 by Jun.
			// snmp_Event.setAhTrapDesc(BeFaultConst.TRAP_DESC_CAPWAP_LINK_UPX.getValue());

			// String reasonDesc = BeAPConnectEvent.getDescription(arg_Event.getReconnectReason());
			// if (arg_Event.getReconnectReason() == BeAPConnectEvent.CLIENT_SERVER_TIMED_OUT) {
			// if (arg_Event.getPreviousCapwapClientIP() != null
			// && !arg_Event.getCapwapClientIP().equals(
			// arg_Event.getPreviousCapwapClientIP())) {
			// reasonDesc = HmBeResUtil
			// .getString("fault.alarm.description.reconnectReason.apServerTimedOut.publicIpChanged");
			// }
			// }
			String reasonDesc = arg_Event.getReconnectDescription();
			DebugUtil.faultDebugInfo("reconnectReason=[" + arg_Event.getReconnectReason() + ", "
					+ reasonDesc + "]");

			trapEvent.setDescribe(reasonDesc);
			trapEvent.setSeverity((byte)BeFaultConst.ALERT_SERVERITY_CLEAR);
			trapEvent.setCode(BeFaultConst.TRAP_CODE_TYPEX[0].getKey());
			trapEvent.setFailureFlag(false);
		} else {
//			snmp_Event
//					.setAhProbableCause((short) BeFaultConst.TRAP_DESC_CAPWAP_LINK_DOWNX.getKey());
			trapEvent.setDescribe(BeFaultConst.TRAP_DESC_CAPWAP_LINK_DOWNX.getValue());
			trapEvent.setSeverity((byte)BeFaultConst.ALERT_SERVERITY_CRITICAL);
			trapEvent.setCode(BeFaultConst.TRAP_CODE_TYPEX[1].getKey());
			trapEvent.setFailureFlag(true);
		}

		// // Jonathan add 2008-7-21, use for transfer hivemanger IP
		// snmp_Event.setClientIp(arg_Event.getHivemanagerIP());

		// 2009.8.26, Jonathan
		trapEvent.setDomainName(arg_Event.getDomainName());

		myDebug("changeApConnectTrapToSnmp::connect trap info : msgType=" + arg_Event.getMsgType()
				+ ",apid=" + trapEvent.getApMac() + ",severity=" + trapEvent.getSeverity()
				+ ",description=" + trapEvent.getDescribe() + ",probableCause="
				+ trapEvent.getAlarmTag1());

		Parent.addTrapToQueue(trapEvent);
	}

	public void stopThread() {
		// stop thread
		isContinue = false;

		BeBaseEvent ShutDown = new AhShutdownEvent();
		eq.clear();
		eq.add(ShutDown);
	}

	private void myDebug(String arg_Msg) {
		try {
			Parent.getModule().getDebuger().debug(arg_Msg);
		} catch (Exception ex) {
			/*
			 * write log file error
			 */
		}
	}
	
	private void sendRebootCLIErrorEvent(BeTrapEvent arg_Event){
		AhDeviceRebootResultEvent rebootEvent = new AhDeviceRebootResultEvent();
		rebootEvent.setDeviceMac(arg_Event.getSimpleHiveAp().getMacAddress());
		rebootEvent.setOperation(AhDeviceRebootResultEvent.OPERATION_UPDATE_MESSAGE);
		rebootEvent.setMessage(arg_Event.getDescribe());
		
		AhAppContainer.getBeConfigModule().getConfigMgmt().add(rebootEvent);
	}

}