package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.fault.BeFaultConst;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.TrapMgmt;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public final class TrapMgmtImpl implements TrapMgmt {

	private static final Tracer log	= new Tracer(TrapMgmtImpl.class.getSimpleName());

	private TrapMgmtImpl() {
	}

	private static TrapMgmt instance;

	public synchronized static TrapMgmt getInstance() {
		if (instance == null) {
			instance = new TrapMgmtImpl();
		}

		return instance;
	}

	public Long createEvent(AhEvent event) throws Exception {
		Long eventId = createEventTrap(event);
		// For EventPagingCache
		BoObserver.notifyListeners(new BoEvent<>(event, BoEventType.CREATED));
		return eventId;
	}

	public synchronized Long createAlarm(AhAlarm alarm) throws Exception {
		Long alarmId = createAlarmTrap(alarm);
		SystemStatusCache.getInstance().incrementAlarmCount(alarm.getSeverity(),
				alarm.getOwner().getId());
		BoEvent<AhAlarm> boEvent = new BoEvent<>(alarm, BoEventType.CREATED);
		// For AlarmPagingCache
		BoObserver.notifyListeners(boEvent);
		// For Map propagation
		BoMgmt.getBoEventMgmt().publishBoEvent(boEvent);
		return alarmId;
	}

	private Long createEventTrap(AhEvent trap) throws Exception {
		if (trap.getOwner() == null) {
			// If no owner, use home domain
			trap.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		}
		if (trap.getTrapTimeStamp() == null) {
			// If no time stamp, use current time
			trap.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), trap.getOwner()
					.getTimeZoneString()));
		}
		return QueryUtil.createBo(trap);
	}

	private Long createAlarmTrap(AhAlarm trap) throws Exception {
		if (trap.getOwner() == null) {
			// If no owner, use home domain
			trap.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		}
		if (trap.getTrapTimeStamp() == null) {
			// If no time stamp, use current time
			trap.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), trap.getOwner()
					.getTimeZoneString()));
		}
		return QueryUtil.createBo(trap);
	}

	public synchronized boolean clearAlarmId(Long alarmId) throws Exception {
		AhAlarm alarm = QueryUtil.findBoById(AhAlarm.class, alarmId);
		return clearAlarm(alarm);
	}

	// feeling add 2008-01-24
	public synchronized boolean clearAlarm(AhAlarm alarm) throws Exception {
		if (alarm == null) {
			return false;
		}
		short severity = alarm.getSeverity();
		alarm.setSeverity(AhAlarm.AH_SEVERITY_UNDETERMINED);
		alarm.setClearTimeStamp(new HmTimeStamp(System.currentTimeMillis(), alarm.getOwner()
				.getTimeZoneString()));
		alarm.setModifyTimeStamp(alarm.getClearTimeStamp());

		AhAlarm mergedAlarm = QueryUtil.updateBo(alarm);
		if (severity != mergedAlarm.getSeverity()) {
			SystemStatusCache.getInstance().decrementAlarmCount(severity, alarm.getOwner().getId());
			SystemStatusCache.getInstance().incrementAlarmCount(mergedAlarm.getSeverity(),
					alarm.getOwner().getId());
		}
		BoMgmt.getBoEventMgmt().publishBoEvent(
				new BoEvent<>(mergedAlarm, BoEventType.UPDATED));
		return true;
	}

	public synchronized void updateAlarm(AhAlarm alarm, short old_severity) throws Exception {
		AhAlarm existingAlarm = QueryUtil.updateBo(alarm);

		if (old_severity != existingAlarm.getSeverity()) {
			SystemStatusCache.getInstance().decrementAlarmCount(old_severity,
					existingAlarm.getOwner().getId());
			SystemStatusCache.getInstance().incrementAlarmCount(existingAlarm.getSeverity(),
					existingAlarm.getOwner().getId());
		}
		BoMgmt.getBoEventMgmt().publishBoEvent(
				new BoEvent<>(existingAlarm, BoEventType.UPDATED));
	}

	public void updateAlarm(AhAlarm alarm) throws Exception {
		updateAlarm(alarm, alarm.getSeverity());
	}

	private void setCapwapAlarm2LinkDown(Long domainId) {
		String whereString = " alarmType=:s1 AND alarmSubType=:s2 AND severity=:s3";
		Object[] objs = new Object[3];
		objs[0] = BeFaultConst.ALARM_TYPE_CAPWAP;
		objs[1] = BeFaultConst.ALARM_SUBTYPE_CAPWAP_LINK;
		objs[2] = AhAlarm.AH_SEVERITY_UNDETERMINED;
		FilterParams filterParams = new FilterParams(whereString, objs);
		List<AhAlarm> list = QueryUtil.executeQuery(AhAlarm.class, null, filterParams, domainId);

		for (AhAlarm alarm : list) {
			short severity_old = alarm.getSeverity();

			if (severity_old != AhAlarm.AH_SEVERITY_CRITICAL) {
				alarm.setSeverity(AhAlarm.AH_SEVERITY_CRITICAL);
				alarm.setTrapDesc(BeFaultConst.TRAP_DESC_CAPWAP_LINK_DOWNX.getValue());
				alarm.setClearTimeStamp(null);
				alarm.setModifyTimeStamp(alarm.getTrapTimeStamp());
				SystemStatusCache.getInstance().decrementAlarmCount(severity_old,
						alarm.getOwner().getId());
				SystemStatusCache.getInstance().incrementAlarmCount(AhAlarm.AH_SEVERITY_CRITICAL,
						alarm.getOwner().getId());
			}
			BoMgmt.getBoEventMgmt().publishBoEvent(
					new BoEvent<>(alarm, BoEventType.UPDATED));
		}
		
		if(!list.isEmpty()) {
			try {
				StringBuilder updateSql = new StringBuilder();
				updateSql.append("update ah_alarm set severity = ").append(AhAlarm.AH_SEVERITY_CRITICAL);
				updateSql.append(", trapdesc='").append(BeFaultConst.TRAP_DESC_CAPWAP_LINK_DOWNX.getValue()).append("'");
				updateSql.append(",clear_time=0,modify_time=").append(System.currentTimeMillis());
				if(domainId == null) {
					updateSql.append(" where ").append(whereString);
				}
				else {
					updateSql.append(" where owner=").append(domainId).append(" and ").append(whereString);
				}
				QueryUtil.executeNativeUpdate(updateSql.toString(), objs);
			} catch (Exception e) {
					log.error("setCapwapAlarm2LinkDown", "Update CAPWAP link down alarm error.", e);
			}
		}
	}

	public void setCapwapAlarm2LinkDownByAll() {
		setCapwapAlarm2LinkDown(null);
	}

	public void setCapwapAlarm2LinkDownByDomain(Long domainId) {
		if (null == domainId) {
			return;
		}
		setCapwapAlarm2LinkDown(domainId);
	}

	public synchronized void queryAndUpdateAlarm(FilterParams filter, AhAlarm newAlarm) {
		try {
			List<AhAlarm> list = QueryUtil.executeQuery(AhAlarm.class, null, filter);
			if (!list.isEmpty()) {
				for(int i = 0; i < list.size(); i++) {//need confirm with xiaxy here, Oct.17 2012
					AhAlarm dbAlarm = list.get(i);
					short old_severity = dbAlarm.getSeverity();
					dbAlarm.setSeverity(newAlarm.getSeverity());
					dbAlarm.setTrapDesc(newAlarm.getTrapDesc());
					//dbAlarm.setTag1(newAlarm.getTag1());
					dbAlarm.setApName(newAlarm.getApName());
					dbAlarm.setObjectName(newAlarm.getObjectName());
					if (newAlarm.getSeverity() == BeFaultConst.ALERT_SERVERITY_CLEAR) {
						dbAlarm.setModifyTimeStamp(newAlarm.getModifyTimeStamp());
						dbAlarm.setClearTimeStamp(newAlarm.getTrapTimeStamp());
					} else {
						dbAlarm.setTrapTimeStamp(newAlarm.getTrapTimeStamp());
						dbAlarm.setModifyTimeStamp(null);
						dbAlarm.setClearTimeStamp(null);
					}
					updateAlarm(dbAlarm, old_severity);
				}
			//	log.info("queryAndUpdateAlarm", "update alarm successfully.");
			} else {
				if (newAlarm.getAlarmType() == BeFaultConst.ALARM_TYPE_CAPWAP
						&& newAlarm.getAlarmSubType() == BeFaultConst.ALARM_SUBTYPE_CAPWAP_LINK
						&& newAlarm.getSeverity() != BeFaultConst.ALERT_SERVERITY_CLEAR
						&& CacheMgmt.getInstance().getSimpleHiveAp(newAlarm.getApId()) == null) {
					log.info("queryAndUpdateAlarm", "link-down and no this ap, don't need create alarm for "
									+ newAlarm.getApId());
				} else if (newAlarm.getSeverity() == BeFaultConst.ALERT_SERVERITY_CLEAR
						&& !(newAlarm.getAlarmType() == BeFaultConst.ALARM_TYPE_CAPWAP && newAlarm
								.getAlarmSubType() == BeFaultConst.ALARM_SUBTYPE_CAPWAP_LINK)) {
					log.info("queryAndUpdateAlarm", "clear alarm, don't need create alarm for "
									+ newAlarm.getApId());
				} else {
					createAlarm(newAlarm);
				//	log.info("queryAndUpdateAlarm", "createAlarm successfully.");
				}
			}
		} catch (Exception e) {
			log.error("queryAndUpdateAlarm", "Query and update alarm error.", e);
		}
	}

	public synchronized void clearAlarmTables(long intervalDate,long unclearDate,int maxRecords,long reminderDate) throws Exception {
		// 1. bulk remove clear alarm
		String where = "trap_time < :s1 and severity = :s2";
		Object[] values = new Object[] { intervalDate, (short)BeFaultConst.ALERT_SERVERITY_CLEAR };
		List<AhAlarm> alarms = QueryUtil.executeQuery(AhAlarm.class, null, new FilterParams(where, values));
		removeAlams(alarms,0);
		alarms.clear();
		
		// 2. bulk remove minor and major alarm
		where="trap_time < :s1 and severity in (:s2))";
		List<Short> severityList=new ArrayList<Short>();
		severityList.add((short) BeFaultConst.ALERT_SERVERITY_MINOR);
		severityList.add((short) BeFaultConst.ALERT_SERVERITY_MAJOR);
		values = new Object[] { unclearDate,severityList};
		alarms = QueryUtil.executeQuery(AhAlarm.class, null, new FilterParams(where, values));
		removeAlams(alarms,1);
		alarms.clear();
		
		// 3. bulk remove out maxRecords alarm
		String query = "select max(id),min(id) from " + AhAlarm.class.getSimpleName();
		List<?> list = QueryUtil.executeQuery(query, 1);
		if (!list.isEmpty()) {
			Object[] obj=(Object[]) list.get(0);
			if(null==obj[0] || null==obj[1]){
				return;
			}
			long maxId =(long) obj[0];
			long minId=(long) obj[1];
			if(maxId>maxRecords){
				List<Long> ids =new ArrayList<Long>();
				for(long i=minId;i<(maxId-maxRecords);i++){
					ids.add(i);
				}
				if(!ids.isEmpty()){
					where="severity!=:s1 and id in (:s2))";
					values = new Object[] { (short)BeFaultConst.ALERT_SERVERITY_CRITICAL,ids};
					alarms = QueryUtil.executeQuery(AhAlarm.class, null, new FilterParams(where, values));
					removeAlams(alarms,2);
				}
			}
		}
	}
	
	
	public void removeAlams(List<AhAlarm> alarms,int type) throws Exception{
	   if(alarms.isEmpty()){
		  return;
		}
	   int count = 0;
		 List<Long> ids =new ArrayList<Long>();
		   for (AhAlarm ahAlarm : alarms) {
			    if(0!=type){
			    	clearAlarm(ahAlarm);
			    }
			    ids.add(ahAlarm.getId());
				count++;
			}
		   QueryUtil.removeBos(AhAlarm.class, ids);
		if(0==type){
			log.info("clearAlarmTables", "remove clear Alarm data, counts=" + count);
		}else if(1==type){
			log.info("clearAlarmTables", "remove minor and major Alarm data, counts=" + count);
		}else if(2==type){
			log.info("clearAlarmTables", "remove out of maxRecords Alarm data, counts=" + count);
		}else{
			log.info("clearAlarmTables", "remove Critical Alarm data, counts=" + count);
		}
		
	}

	@SuppressWarnings("unchecked")
	public synchronized void removeAlarms(HiveAp hiveAp) {
		try {
			if (null != hiveAp) {
				Long domainId = hiveAp.getOwner().getId();
				List<Long> ids = (List<Long>) QueryUtil.executeQuery(
						"select bo.id from " + AhAlarm.class.getSimpleName() + " bo", null, new FilterParams("apId", hiveAp
								.getMacAddress()), domainId);

				if (!ids.isEmpty()) {
					QueryUtil.removeBos(AhAlarm.class, ids);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void bulkRemoveAlarms(FilterParams filter) {
		try {
			QueryUtil.bulkRemoveBos(AhAlarm.class, filter, null, null);
		} catch (Exception e) {
			log.error("bulkRemoveAlarms", "bulkRemoveAlarms error.", e);
		}
	}
	
	public synchronized void removeNetworkDeviceHistorys(HiveAp hiveAp) {
		try {
			if (null != hiveAp) {
				QueryUtil.executeNativeUpdate("delete from network_device_history where mac='"+hiveAp.getMacAddress()+"'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}