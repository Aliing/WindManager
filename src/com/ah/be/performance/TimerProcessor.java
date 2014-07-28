package com.ah.be.performance;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.performance.appreport.AppFlowHelper;
import com.ah.be.performance.messagehandle.timer.TimerQueryApplicationFlow;
import com.ah.be.performance.messagehandle.timer.TimerQuerySwitchPortStats;

public class TimerProcessor {
	
	private Map<String, TimerInfo>							timerInfoMap = null;
	
	public TimerProcessor() {
		timerInfoMap = Collections.synchronizedMap(new HashMap<String, TimerInfo>());
		init();
	}
	
	private void init() {
		//register timer
		registerTimer(new TimerQuerySwitchPortStats(),TimerQuerySwitchPortStats.TIMER_PERIOD);
		registerTimer(new TimerQueryApplicationFlow(), AppFlowHelper.getAppFlowRequestDelaySeconds(), 
				AppFlowHelper.getAppFlowRequestPeriodSeconds());
		return;
	}
	/**
	 * register timer, the key is Runnable class name, if the key exists, timer will re-register
	 * @param timerRunnable
	 * @param delay		--	unit is second
	 * @param period	--	unit is second
	 */
	public void registerTimer(Runnable timerRunnable,int delay, int period) {
		synchronized(timerInfoMap) {
			TimerInfo info = timerInfoMap.get(timerRunnable.getClass().getName());
			if(info != null) {
				shutdownTimer(info);
			}
			info = new TimerInfo();
			info.setDelay(delay);
			info.setPeriod(period);
			info.setTimerRunnable(timerRunnable);
			timerInfoMap.put(timerRunnable.getClass().getName(), info);
			start();
		}
	}
	/**
	 * register timer, the key is Runnable class name, if the key exists, timer will re-register
	 * @param timerRunnable
	 * @param hour		--	hour that timer start at, [0,23], -1 means not specify hour, only special minute
	 * @param minute	--	minute that timer start at [0,59]. others is useless
	 * @param period	--	unit is second
	 */
	public void registerTimer(Runnable timerRunnable,int hour, int minute, int period) {
		Calendar executeCal = Calendar.getInstance();
		if(hour >=0 && hour <= 23) {
			if (executeCal.get(Calendar.HOUR_OF_DAY) > hour) {
				executeCal.add(Calendar.DATE, 1);
			} else if(executeCal.get(Calendar.HOUR_OF_DAY) == hour 
					&& executeCal.get(Calendar.MINUTE) >= minute) {
				executeCal.add(Calendar.DATE, 1);
			}
			executeCal.set(Calendar.HOUR_OF_DAY, hour);
			if(minute >= 0 && minute <= 59) {
				executeCal.set(Calendar.MINUTE, minute);
			}
		} else if(minute >= 0 && minute <= 59) {
			if (executeCal.get(Calendar.MINUTE) >= minute) {
				executeCal.add(Calendar.HOUR_OF_DAY, 1);
			}
			executeCal.set(Calendar.MINUTE, minute);
		}
		
		executeCal.set(Calendar.SECOND, 0);
		executeCal.set(Calendar.MILLISECOND, 0);

		int delay = (int)((executeCal.getTimeInMillis()+1000 - System.currentTimeMillis()) / 1000);
		registerTimer(timerRunnable,delay,period);
	}
	
	/**
	 * register timer at fixed time, the key is Runnable class name, if the key exists, timer will re-register
	 * if period is 10*60, this timer will start at 1 or 11 or 21 minute etc
	 * if period is 30*60, this timer will start at 1 or 31 minute
	 * @param timerRunnable
	 * @param period	--	unit is second, should not larger than 60*60
	 */
	public void registerTimer(Runnable timerRunnable, int period) {
		if(period > 60*60)
			return;
//		if(0 != (60*60 % period))
//			return;
		
		Calendar executeCal = Calendar.getInstance();
		int startWaitMinute = 1;
		int startMinute = startWaitMinute;
		int minute = executeCal.get(Calendar.MINUTE) +2;
		int hour = executeCal.get(Calendar.HOUR_OF_DAY);
		while(minute > (startMinute)) {
			startMinute += period/60;
		}
		if(startMinute >= 60) {
			startMinute = startWaitMinute;
			hour++;
		}
		
		registerTimer(timerRunnable,hour,startMinute,period);
	}
	
	public void start() {
		synchronized(timerInfoMap) {
			for(TimerInfo info: timerInfoMap.values()) {
				if(info.getTimer() == null || info.getTimer().isShutdown()) {
					ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
					ScheduledFuture<?> future =  timer.scheduleAtFixedRate(info.getTimerRunnable(), info.getDelay(), info.getPeriod(), TimeUnit.SECONDS);
					info.setFuture(future);
					info.setTimer(timer);
				}
			}
		}
	}
	
	public void stop() {
		synchronized(timerInfoMap) {
			for(TimerInfo info: timerInfoMap.values()) {
				shutdownTimer(info);
			}
		}
	}
	
	private void shutdownTimer(TimerInfo info) {
		try {
			if(info.getFuture() != null)
				info.getFuture().cancel(true);
			if(info.getTimer() != null && !info.getTimer().isShutdown()) {
				info.getTimer().shutdown();
			}
			info.setFuture(null);
			info.setTimer(null);
		} catch (Exception e) {
			
		}
	}
}

class TimerInfo {
	//timer
	private ScheduledExecutorService		timer = null;
	//scheduled furture
	private ScheduledFuture<?>					future = null;
	//unit is second
	private int								delay = 60;
	//unit is second
	private int								period = 60;
	//runnable
	private Runnable						timerRunnable = null;

	public ScheduledExecutorService getTimer() {
		return timer;
	}

	public void setTimer(ScheduledExecutorService timer) {
		this.timer = timer;
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public Runnable getTimerRunnable() {
		return timerRunnable;
	}

	public void setTimerRunnable(Runnable timerRunnable) {
		this.timerRunnable = timerRunnable;
	}
}