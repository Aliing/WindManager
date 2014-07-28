/**
 * @filename			TeacherViewCleaner.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R1
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.misc.teacherview;

import com.ah.be.common.NmsUtil;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.ViewingClass;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * To clean the class viewing history
 */
public class TeacherViewCleaner implements Runnable {
	private static final Tracer log = new Tracer(TeacherViewCleaner.class
											.getSimpleName());
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		/*
		 * Teacher View must be enabled on HM/HMOL
		 */
		if(!NmsUtil.isGlobalTeacherViewEnabled()) {
			log.info("Teacher View is not enabled on this system.");
			return ;
		}
		
		FilterParams filter = new FilterParams("endTime <= :s1",
				new Object[] {Long.valueOf(System.currentTimeMillis())});
		
		try {
			QueryUtil.bulkRemoveBos(ViewingClass.class, filter);
		} catch(Exception e) {
			log.error("Failed to execute the cleaning SQL query.", e);
		}
	}

}
