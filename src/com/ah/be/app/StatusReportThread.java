/**
 *@filename		StatusReportThread.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 08:45:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import com.ah.util.Tracer;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class StatusReportThread extends Thread
{

	private static final Tracer log = new Tracer(StatusReportThread.class
			.getSimpleName());

	private final BaseModule	ReportModule;

	private boolean		runFlag			= true;

	private int			ReportInterval	= 5;

	/**
	 * @param arg_Module -
	 */
	public StatusReportThread(BaseModule arg_Module)
	{
		ReportModule = arg_Module;
		ReportInterval = HmBeParaUtil.getWatchDogReportInterval();
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		int LoopCount = 0;

		while (this.runFlag)
		{
			if (LoopCount*100 >= ReportInterval && ReportModule.getWatchDogListener() != null)
			{
				LoopCount = 0;

				ReportModule.getWatchDogListener().reportModuleStatus(
					ReportModule, ReportModule.getModuleStatus());

				//myDebug("report status to WatchDog");
				/*
				 * test code snippet for event processor
				 */
				//				myDebug("send test event to EventModule");
				//				BeBaseEvent event = new BeBaseEvent();
				//				event.setEventType(ReportModule.getModuleId());
				//				event.setModuleId(ReportModule.getModuleId());
				//				ReportModule.getEventListener().eventGenerated(event);
			}

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ie)
			{
				log.error("run", "Thread sleep interrupted.", ie);
			}

			LoopCount = LoopCount + 1;
		}

		log.info("run", "StatusReportThread stopped.");
	}

	public void stopReport()
	{
		this.runFlag = false;
	}

}