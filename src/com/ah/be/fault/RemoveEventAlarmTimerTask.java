package com.ah.be.fault;

import java.util.List;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhEvent;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class RemoveEventAlarmTimerTask implements Runnable {

	private static final Tracer	logt			= new Tracer(RemoveEventAlarmTimerTask.class);

	private int					event_interval	= 7;
	private int					alarm_interval	= LogSettings.DEFAULT_ALARM_INTERVAL;
	private int					alarm_retainUnclearDays	= LogSettings.DEFAULT_ALARM_RETAIN_UNCLEAR_DAYS;
	private int					alarm_reminderDays	= LogSettings.DEFAULT_ALARM_REMINDER_DAYS;
	private int					alarm_maxRecords=LogSettings.getDefaultMaxRecords();
	private int					maxPerfRecord	= 100000;

	public RemoveEventAlarmTimerTask() {
	}

	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());

		BaseModule log = new BaseModule();
		try {
			logt.info("run", "clear data...");
			clearData();
			logt.info("run", "clear data end!");
			
			BeLogTools.info(HmLogConst.M_TRACER,"re init system status cache...");
			SystemStatusCache.getInstance().initCacheForAllDomain(CacheMgmt.getInstance().getCacheDomains());
			BeLogTools.info(HmLogConst.M_TRACER,"re init system status cache end");
			
		} catch (Error e) {
			try {
				BeLogTools.error(HmLogConst.M_TRACER,
						"RemoveEventAlarmTimerTask:run() catch Exception: " + e.getMessage());
				BeLogTools.error(HmLogConst.M_FAULT,
						"RemoveEventAlarmTimerTask:run() catch Exception", e);
			} catch (Error e1) {
			}
		} catch (Exception e) {
			log.setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
					"Clear event or alarm::" + e.getMessage());
			BeLogTools.error(HmLogConst.M_TRACER, "RemoveEventAlarmTimerTask:run() catch Error: "
					+ e.getMessage());
			BeLogTools.error(HmLogConst.M_FAULT, "RemoveEventAlarmTimerTask:run() catch Error", e);
		}
	}

	private void clearData() throws Exception {
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if (list.isEmpty()) {
			DebugUtil.faultDebugWarn("RemoveEventAlarmTimerTask: no log settings in db!");
			return;
		} else {
			LogSettings bo=list.get(0);
			event_interval =bo.getEventInterval();
			alarm_interval =bo.getAlarmInterval();
			alarm_retainUnclearDays=bo.getAlarmRetainUnclearDays();
			alarm_maxRecords=bo.getShowMaxRecordsValue();
			alarm_reminderDays=bo.getAlarmReminderDays();
			maxPerfRecord = list.get(0).getMaxPerfRecord();
		}
		clearEventTables(AhDateTimeUtil.getDateAfter2(-event_interval));
		//remove alarm data with filter
		BoMgmt.getTrapMgmt().clearAlarmTables(AhDateTimeUtil.getDateAfter2(-alarm_interval)
				,AhDateTimeUtil.getDateAfter2(-alarm_retainUnclearDays)
				,alarm_maxRecords,AhDateTimeUtil.getDateAfter2(-alarm_reminderDays));
	}

	private void clearEventTables(long date) throws Exception {
		int removeCount1 = QueryUtil.bulkRemoveBos(AhEvent.class, new FilterParams("time < :s1",
				new Object[] { (date) }), null, null);

		logt.info("clearEventTables", "remove date exceed event counts=" + removeCount1);

		removeRedundantRecordsById(AhEvent.class,maxPerfRecord);
	}
	private void removeRedundantRecordsById(Class<? extends HmBo> boClass, int maxNumber) {
		try {
			String query = "select max(id) from " + boClass.getSimpleName();
			List<?> list = QueryUtil.executeQuery(query, 1);
			if (list.isEmpty()) {
				DebugUtil.faultDebugInfo(boClass.getSimpleName() + " records number is normal.");

				return;
			}

			Long minId = (Long)list.get(0);
			int deleteCount = 0;
			if(null != minId) {
				minId = minId - maxNumber;
				Object[] objs = new Object[] { minId };	
				deleteCount = QueryUtil.bulkRemoveBos(boClass, new FilterParams("id"
						+ " <= :s1", objs), null, null);
			}

			logt.info("clearEventTables","Success remove redundant "
					+ boClass.getSimpleName() + " records number: " + deleteCount);

		} catch (Exception e) {
			logt.error("clearEventTables","clear "
					+ boClass.getSimpleName() + " redundant records catch exception.", e);
		}
	}
}