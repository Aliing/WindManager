/**
 * @filename			CleaningTask.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.Calendar;
import java.util.Date;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * Task running for clean expired data in database
 */
public class CleaningTask implements Runnable {

	public static final Tracer		log = new Tracer(CleaningTask.class.getSimpleName());
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			/*
			 * calculate time
			 */
			Date now = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			
			c.set(Calendar.HOUR, 
					c.get(Calendar.HOUR) - BeLocationModuleImpl.CLEANING_TASK_INTERVAL / 3600);
			
			/*
			 * filter
			 */
			FilterParams filter = new FilterParams("reportTime <= :s1",
					new Object[] {c.getTime()});

			QueryUtil.bulkRemoveBos(LocationRssiReport.class, filter);

		} catch (Exception e) {
			log.error("run", "Failed to clear expired reports.", e);
		} catch (OutOfMemoryError oome) {
			System.gc();
			System.runFinalization();
			System.gc();
		}		
	}

}
