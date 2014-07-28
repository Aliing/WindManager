/**
 *@filename		WatchDogImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2  04:43:22
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.watchdog;

import java.util.Vector;

import com.ah.be.app.BaseModule;
import com.ah.be.app.ModuleStatus;


/**
 * @author Steven
 * @version V1.0.0.0
 */
public class WatchDogImpl extends BaseModule
											implements
											WatchDog,
											WatchDogListener
{

	/**
	 * 
	 */
	public static final int			DefaultReportInterval	= 10000;

	/**
	 * 
	 */
	public static final long		DefaultReportTime		= -1;

	/**
	 * 
	 */
	private Vector<BaseModule>		WatchModules;

	/**
	 * 
	 */
	private Vector<ModuleStatus>	ModulesStatus;

	/**
	 * 
	 */
	private Vector<Long>			LastReportTime;

	/**
	 * 
	 */
	private ModuleCheckThread		ModuleChecker;

	/**
	 * 
	 * Construct method
	 *
	 * @param
	 *
	 * @throws
	 */
	public WatchDogImpl()
	{

		setModuleId(BaseModule.ModuleID_WatchDog);
		setModuleName("WatchDog");
		
		this.getDebuger().setModuleId(BaseModule.ModuleID_WatchDog);
		this.getDebuger().setModuleName("WatchDog");
		WatchModules = new Vector<BaseModule>();
		ModulesStatus = new Vector<ModuleStatus>();
		LastReportTime = new Vector<Long>();
	}

	/** 
	 * @see com.ah.be.app.BaseModule#init()
	 */
	@Override
	public boolean init()
	{

		super.init();
		ModuleChecker = new ModuleCheckThread();
		ModuleChecker.setWatchDogModule(this);
		return true;
	}

	/**
	 * @see com.ah.be.watchdog.WatchDog#startWatch()
	 */
	public boolean startWatch()
	{
		ModuleChecker.setName("ModuleChecker");
		ModuleChecker.start();
		return true;
	}

	/**
	 * @see com.ah.be.watchdog.WatchDog#stopWatch()
	 */
	@SuppressWarnings("deprecation")
	public boolean stopWatch()
	{

		ModuleChecker.stop();
		return true;
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDog#registerModule(com.ah.be.app.BaseModule)
	 */
	public void registerModule(BaseModule arg_Module)
	{

		if (WatchModules.size() == 0)
		{
			WatchModules.add(arg_Module);
			ModulesStatus.add(arg_Module.getModuleStatus());
			LastReportTime.add(DefaultReportTime);
		}
		else
		{
			for (int i = 0; i < WatchModules.size(); i++)
			{
				if (WatchModules.elementAt(i).getModuleId() == arg_Module
					.getModuleId())
				{
					/*
					 * This module already registered
					 */
					WatchModules.set(i, arg_Module);
					ModulesStatus.set(i, arg_Module.getModuleStatus());
					LastReportTime.set(i, DefaultReportTime);
					return;
				}
			}
			WatchModules.add(arg_Module);
			ModulesStatus.add(arg_Module.getModuleStatus());
			LastReportTime.add(DefaultReportTime);
		}
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDog#unregisterModule(int)
	 */
	public void unregisterModule(int arg_ModuleId)
	{

		for (int i = 0; i < WatchModules.size(); i++)
		{
			if (WatchModules.elementAt(i).getModuleId() == arg_ModuleId)
			{
				WatchModules.remove(i);
				ModulesStatus.remove(i);
				LastReportTime.remove(i);
				return;
			}
		}
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDogListener#reportModuleStatus(com.ah.be.app.BaseModule, com.ah.be.app.ModuleStatus)
	 */
	public void reportModuleStatus(
		BaseModule arg_Module,
		ModuleStatus arg_Status)
	{

		/*
		 * some module report current status to WatchDog
		 */
		debug("Receive heart break from module " + arg_Module.getModuleName());
		for (int i = 0; i < WatchModules.size(); i++)
		{

			if (WatchModules.elementAt(i).getModuleId() == arg_Module
				.getModuleId())
			{
				ModulesStatus.set(i, arg_Status);
				LastReportTime.set(i, System.currentTimeMillis());
				break;
			}
		}
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDog#getLastReportTime()
	 */
	public Vector<Long> getLastReportTime()
	{

		return LastReportTime;
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDog#getModulesStatus()
	 */
	public Vector<ModuleStatus> getModulesStatus()
	{

		return ModulesStatus;
	}

	/**
	 * 
	 * @see com.ah.be.watchdog.WatchDog#getRegisteredModules()
	 */
	public Vector<BaseModule> getRegisteredModules()
	{

		return WatchModules;
	}

	public BaseModule getModule()
	{

		return this;
	}

	/** 
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown()
	{

		this.ModuleChecker.stopModuleCheck();
		
		return super.shutdown();
	}
}
