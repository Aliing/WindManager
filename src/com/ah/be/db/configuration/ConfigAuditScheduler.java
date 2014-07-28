package com.ah.be.db.configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.create.source.impl.ScheduleProfileImpl;
import com.ah.be.config.event.AhConfigGeneratedEvent.UseMode;
import com.ah.be.config.event.AhDeltaConfigGeneratedEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.Scheduler;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ConfigAuditScheduler implements Runnable {

	private static final Tracer log = new Tracer(ConfigAuditScheduler.class
			.getSimpleName());
	// unit in millisecond
	public static final long MAX_DELTA = ConfigurationResources.CONFIGURATION_AUDIT_INTERVAL * 60 * 1000;

	private ScheduledExecutorService timer;

	public ConfigAuditScheduler() {
	}
	
	public void start() {
		// do not start it when it is HMOL
		if(NmsUtil.isHostedHMApplication()){
			log.info("start", "It's HMOL, do not start Configuration audit task!");
			return;
		}
		if(timer == null || timer.isShutdown()) {
			timer = Executors.newSingleThreadScheduledExecutor();
			timer.scheduleWithFixedDelay(this,
					ConfigurationResources.CONFIGURATION_AUDIT_INIT,
					ConfigurationResources.CONFIGURATION_AUDIT_INTERVAL,
					TimeUnit.MINUTES);
		}
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());
		log.info("run", "<BE Thread> Configuration audit task begin running.");
		try {
			runConfigurationAuditTask();
		} catch (Exception e) {
			log.error("run", "Configuration audit task run exception", e);
		} catch (Error e) {
			log.error("run", "Configuration audit task run error", e);
		}
	}

	public void stop() {
		if(timer != null && !timer.isShutdown()) {
			timer.shutdown();
			log.info("stop", "<BE Thread> ConfigAuditScheduler timer has been shutdown.");
		}
	}

	public void sendRequest(HiveAp hiveAp, long endTime) {
		if (null == hiveAp) {
			return;
		}
		// Configuration Audit event;
		AhDeltaConfigGeneratedEvent request = new AhDeltaConfigGeneratedEvent();
		request.setConfigType(ConfigType.AP_AUDIT);
		request.setUseMode(UseMode.FULL_AUDIT);
		request.setHiveAp(hiveAp);
		request.setDeadline(endTime);
		AhAppContainer.getBeConfigModule().getConfigMgmt().add(request);
		// User Database Audit event;
		request = new AhDeltaConfigGeneratedEvent();
		request.setConfigType(ConfigType.USER_AUDIT);
		request.setUseMode(UseMode.RADIUS_USER_AUDIT);
		request.setHiveAp(hiveAp);
		request.setDeadline(endTime);
		AhAppContainer.getBeConfigModule().getConfigMgmt().add(request);
	}

	private void runConfigurationAuditTask() {
		long start = System.currentTimeMillis();
		List<Scheduler> list = QueryUtil.executeQuery(Scheduler.class, null, null);
		String msg = list.isEmpty() ? "No schedule profile existed in HM."
				: "Schedule profile count:" + list.size();
		log.info("runConfigurationAuditTask", msg);
		for (Scheduler sd : list) {
			try {
				Object[] result = analysis(sd, start);
				boolean activate = (Boolean) result[0];
				Long endTime = (Long) result[1];
				log.info("runConfigurationAuditTask", "Scheduler:"
						+ sd.getSchedulerName() + " ?activate:" + activate
						+ ", endTime:" + endTime);
				if (activate) {
					// getHiveAps bind with this profiles;
					List<HiveAp> boList = QueryUtil.executeQuery(HiveAp.class, null,
							new FilterParams("scheduler.id", sd.getId()));
					log.info("runConfigurationAuditTask",
							"relevant HiveAP count:" + boList.size());
					for (HiveAp hiveAp : boList) {
						sendRequest(hiveAp, endTime);
					}
				}
			} catch (Exception e) {
				log.error("runConfigurationAuditTask",
						"run configuration audit task:" + sd.getSchedulerName()
								+ " failed.");
			}
		}
		long end = System.currentTimeMillis();
		log.info("runConfigurationAuditTask",
				"Run configuration audit task cost time:" + (end - start)
						+ " ms.");
	}

	private Object[] analysis(Scheduler sd, long currentTime) {
		Object[] results = { false, null };
		int type = sd.getType();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (type == Scheduler.ONE_TIME) {
			String beginDate = sd.getBeginDate();
			String endDate = sd.getEndDate();
			String beginTime = sd.getBeginTime();
			String endTime = sd.getEndTime();
			String begin = beginDate + " " + beginTime;
			String end = endDate + " " + endTime;
			try {
				Date st = sf.parse(begin);
				Date et = sf.parse(end);
				long delta = st.getTime() - currentTime;
				long range = et.getTime() - st.getTime();
				if (delta > 0 && delta < MAX_DELTA) {
					results[0] = true;
					results[1] = currentTime + range;
					return results;
				}
			} catch (ParseException e) {
				log.error("analysis", "parse schedule time error."
						+ e.getMessage());
			}
		} else {
			String beginDate = sd.getBeginDate();
			String endDate = sd.getEndDate();
			boolean isDayValid = isDayValid(currentTime, beginDate, endDate);
			if (isDayValid) {
				return getResult(sd, currentTime);
			}
		}
		return results;
	}

	private boolean isDayValid(long currentTime, String beginDate,
			String endDate) {
		if (null == beginDate || "".equals(beginDate.trim())) {
			return true;
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date begin = sf.parse(beginDate);
			long beginTime = begin.getTime();
			if (null == endDate || "".equals(endDate)) {
				if (currentTime < beginTime) {
					return false;
				}
			} else {
				Date end = sf.parse(endDate);
				Calendar ca = Calendar.getInstance();
				ca.setTime(end);
				ca.add(Calendar.DAY_OF_YEAR, 1);
				long endTime = ca.getTimeInMillis();
				if (currentTime < beginTime || currentTime > endTime) {
					return false;
				}
			}
		} catch (ParseException e) {
			log.error("isDayValid", "parse schedule time error."
					+ e.getMessage());
		}
		return true;
	}

	private Object[] getResult(Scheduler sd, long currentTime) {
		String weeks = sd.getWeeks();
		String beginTime = sd.getBeginTime();
		String endTime = sd.getEndTime();
		String beginTime2 = sd.getBeginTimeS();
		String endTime2 = sd.getEndTimeS();
		Object[] result = getResult(currentTime, beginTime, endTime, weeks);
		if (!(Boolean) result[0] && null != beginTime2
				&& !"".equals(beginTime2.trim())) {
			result = getResult(currentTime, beginTime2, endTime2, weeks);
		}
		return result;
	}

	private Object[] getResult(long currentTime, String beginTime,
			String endTime, String weeks) {
		Object[] results = { false, null };
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sf_ = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(currentTime);
		String currentDate = sf_.format(date);
		String begin = currentDate + " " + beginTime;
		String end = currentDate + " " + endTime;
		try {
			Date st = sf.parse(begin);
			Date et = sf.parse(end);
			if (null == weeks || "".equals(weeks.trim())) {
				// no weeks assigned;
				long range = et.getTime() - st.getTime();
				long delta = st.getTime() - currentTime;
				if (delta > 0 && delta < MAX_DELTA) {
					results[0] = true;
					results[1] = currentTime + range;
					return results;
				}
			} else {
				String fromWeek = ScheduleProfileImpl.getFromWeek(weeks);
				String toWeek = ScheduleProfileImpl.getToWeek(weeks);
				log.info("getResult", "schedule from week:" + fromWeek + " to "
						+ toWeek);
				if ("".equals(toWeek)) {
					// no weeks assigned;
					long range = et.getTime() - st.getTime();
					long delta = st.getTime() - currentTime;
					if (delta > 0 && delta < MAX_DELTA) {
						results[0] = true;
						results[1] = currentTime + range;
						return results;
					}
				} else {
					int fromWeek_int = getDayInWeekIndex(fromWeek);
					int toWeek_int = getDayInWeekIndex(toWeek);
					if (0 == fromWeek_int || 0 == toWeek_int) {
						log.error("getResult", "Unknown Week String value..");
						return results;
					}
					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(currentTime);
					ca.set(Calendar.HOUR_OF_DAY, 0);
					ca.set(Calendar.MINUTE, 0);
					ca.set(Calendar.SECOND, 0);
					ca.set(Calendar.DAY_OF_WEEK, fromWeek_int);
					long from = ca.getTimeInMillis();
					Date fromDate = ca.getTime();
					ca.add(Calendar.WEEK_OF_MONTH, -1);
					long preFrom = ca.getTimeInMillis();
					Date preFromDate = ca.getTime();
					ca.add(Calendar.WEEK_OF_MONTH, 1);
					ca.set(Calendar.DAY_OF_WEEK, toWeek_int);
					if (toWeek_int < fromWeek_int) {
						ca.add(Calendar.WEEK_OF_MONTH, 1);
					}
					ca.set(Calendar.HOUR_OF_DAY, 24);
					long to = ca.getTimeInMillis();
					log.info("getResult", "week setting from value1:"
							+ fromDate + ",to:" + ca.getTime());
					ca.add(Calendar.WEEK_OF_MONTH, -1);
					long preTo = ca.getTimeInMillis();
					log.info("getResult", "week setting from value2:"
							+ preFromDate + ",to:" + ca.getTime());
					long range = et.getTime() - st.getTime();
					long delta = st.getTime() - currentTime;
					if ((currentTime > from && currentTime < to)
							|| (currentTime > preFrom && currentTime < preTo)) {
						if (delta > 0 && delta < MAX_DELTA) {
							results[0] = true;
							results[1] = currentTime + range;
							return results;
						}
					}
				}
			}
		} catch (ParseException e) {
			log.error("getResult", "parse schedule time error."
					+ e.getMessage());
		}
		return results;
	}

	private int getDayInWeekIndex(String weekName) {
		if ("Saturday".equals(weekName)) {
			return 7;
		} else if ("Friday".equals(weekName)) {
			return 6;
		} else if ("Thursday".equals(weekName)) {
			return 5;
		} else if ("Wednesday".equals(weekName)) {
			return 4;
		} else if ("Tuesday".equals(weekName)) {
			return 3;
		} else if ("Monday".equals(weekName)) {
			return 2;
		} else if ("Sunday".equals(weekName)) {
			return 1;
		} else {
			return 0;
		}
	}

}