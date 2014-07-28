/**
 *@filename		BeLogModuleStandImpl.java
 *@version
 *@author		xiaolanbao
 *@createtime	2007-9-3 02:07:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.log;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.BaseModule;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.clean.LogCleanProcessor;
import com.ah.be.log.clean.task.ClientMonitorLogCleanTask;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author xiaolanbao
 * @version V1.0.0.0
 */
public class BeLogModuleStandImpl extends BaseModule implements BeLogModule {

	private static final long LOG_CLEAN_TASK_INITIAL_DELAY = 1L;

	private static final long LOG_CLEAN_TASK_TASK_INTERVAL = 1L;

	/* Log Cleaning Processor */
	private LogCleanProcessor logCleanProcessor;

	public BeLogModuleStandImpl() {
		super();
		setModuleId(9);
		setModuleName("BeLogModule");
	}

	@Override
	public boolean init() {
		if (logCleanProcessor == null) {
			logCleanProcessor = new LogCleanProcessor();

			ClientMonitorLogCleanTask cmLogClientTask = new ClientMonitorLogCleanTask();
			
			logCleanProcessor.addTask(cmLogClientTask);
		}

		return true;
	}

	@Override
	public boolean run() {
		if (logCleanProcessor != null) {
			logCleanProcessor.start(LOG_CLEAN_TASK_INITIAL_DELAY, LOG_CLEAN_TASK_TASK_INTERVAL, TimeUnit.HOURS);
		}

		return true;
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		if (event.isShutdownRequestEvent()) {
			shutdown();
		}
	}

	@Override
	public boolean shutdown() {
		if (logCleanProcessor != null) {
			logCleanProcessor.stop();
		}

		return true;
	}

	public void addSystemLog(short logLevel, String moduleName, String msg) {
		HmSystemLog log = new HmSystemLog();
		log.setLevel(logLevel);
		log.setSource(moduleName);
		if (msg != null && msg.length() > HmSystemLog.MAX_SYSLOG_LENGTH) {
			msg = msg.substring(0, HmSystemLog.MAX_SYSLOG_LENGTH);
		}
		log.setSystemComment(msg);
		log.setLogTimeStamp(System.currentTimeMillis());
		log.setLogTimeZone(TimeZone.getDefault().getID());
		log.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		
		try {
			QueryUtil.createBo(log);
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, "add system log to db error.", ex);
		}
	}

}