/**
 *@filename		ModuleCheckThread.java
 *@version
 *@author		Steven
 *@createtime	2007-9-6 03:26:17
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.watchdog;

import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeParaUtil;

//import com.ah.be.app.ModuleStatus;
import com.ah.be.app.ServerStatus;

/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public class ModuleCheckThread extends Thread
{

	public static final int	DefaultCheckInterval	= 5000;

	private boolean			runFlag					= true;

	/** 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		int runCount = 0;

		while (runFlag)
		{
			try {
				runCount = runCount + 1;
				if (runCount * 100 >= DefaultCheckInterval)
				{
					myDebug("WatchDog invoke module check thread");
					long CurrentTime = System.currentTimeMillis();
					for (int i = 0; i < WatchDogModule.getRegisteredModules()
						.size(); i++)
					{
					BaseModule Module = WatchDogModule.getRegisteredModules()
						.elementAt(i);
				//	ModuleStatus Status = WatchDogModule.getModulesStatus()
				//		.elementAt(i);
					Long ReportTime = WatchDogModule.getLastReportTime()
						.elementAt(i);

					if (ReportTime == WatchDogImpl.DefaultReportTime)
					{
						continue;
					}
					long TimeInterval = CurrentTime - ReportTime;
					//myDebug("TimeInterval = " + TimeInterval);
					if (TimeInterval > (HmBeParaUtil
						.getWatchDogReportInterval() * 2))
					{
						/**
						 * Some module lost heart break
						 */
						//					WatchDogModule.getModule().debug(
						//						Module.getModuleName() + "lost heart break");
						myDebug("warning!!! " + Module.getModuleName()
							+ " lost heart break");
					}
					runCount = 0;
				}
				/*
				 * report all module status to registered BE client
				 */
				}

				ServerStatus BeStatus = new ServerStatus();
				BeStatus.setModuleList(WatchDogModule.getRegisteredModules());
				BeStatus.setStatusList(WatchDogModule.getModulesStatus());

				Thread.sleep(300000); // 5 minutes
			}
			catch (InterruptedException ie)
			{
				WatchDogModule.getModule().debug(ie.getMessage());
				ie.printStackTrace();
			}
		}
	}

	public void stopModuleCheck()
	{
		runFlag = false;
	}

	public void startModuleCheck()
	{
		setName("watchdog checkModule");
		start();
	}

	private WatchDog	WatchDogModule;

	public ModuleCheckThread()
	{

	}

	public WatchDog getWatchDogModule()
	{
		return WatchDogModule;
	}

	public void setWatchDogModule(WatchDog arg_Module)
	{
		WatchDogModule = arg_Module;
	}

	public void myDebug(String arg_Msg)
	{
		WatchDogModule.getModule().debug(arg_Msg);
	}

}