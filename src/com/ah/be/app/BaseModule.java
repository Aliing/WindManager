/**
 *@filename		BaseModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2 04:25:41
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventDispatchListener;
import com.ah.be.event.BeEventListener;
import com.ah.be.event.NullBeEventListener;
import com.ah.be.watchdog.NullWatchDogListener;
import com.ah.be.watchdog.WatchDogListener;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BaseModule implements BeEventDispatchListener
{

	/**
	 * 
	 */
	private ModuleStatus		moduleStatus			= new ModuleStatus();

	/**
	 * 
	 */
	private WatchDogListener	statusListener			= null;

	/**
	 * 
	 */
	private BeEventListener		EventListener			= null;

	/**
	 * 
	 */
	private StatusReportThread	StatusReport			= null;

	/**
	 * 
	 */
	private int					ModuleId				= 999;

	/**
	 * 
	 */
	private ModuleDebuger		Debuger					= new ModuleDebugerConsoleImpl();

	/*
	 * Moudle ID Table WatchDog 1 BeEventModule 2 BeAdminModule 3
	 * BeCommunicationModule 4 BeConfigModule 5 BeDbModule 6 BeFaultModule 7
	 * BeLicenseModule 8 BeLogModule 9 BeOsModule 10 BeParaModule 11
	 * BePerformceModule 12 BeResourceModule 13 BeSnmpModule 14 BeTopoModule 15
	 */

	public static final int		ModuleID_BeApp			= 0;

	public static final int		ModuleID_WatchDog		= 1;

	public static final int		ModuleID_Event			= 2;

	public static final int		ModuleID_Admin			= 3;

	public static final int		ModuleID_Communication	= 4;

	public static final int		ModuleID_Config			= 5;

	public static final int		ModuleID_DB				= 6;

	public static final int		ModuleID_Fault			= 7;

	public static final int		ModuleID_License		= 8;

	public static final int		ModuleID_Log			= 9;

	public static final int		ModuleID_Os				= 10;

	public static final int		ModuleID_Parameter		= 11;

	public static final int		ModuleId_Performance	= 12;

	public static final int		ModuleID_Resource		= 13;

	public static final int		ModuleID_SNMP			= 14;

	public static final int		ModuleID_Topo			= 15;

	public static final int		ModuleID_Debug			= 16;

	public static final int		ModuleID_TroubleShooting = 17;
	
	public static final int		ModuleID_Location		= 18;
	
	public static final int		ModuleID_Activation		= 19;

	public static final int		ModuleID_Monitor		= 20;
	
	public static final int       ModuleID_IDM             = 21;

	/**
	 * 
	 */
	private String				ModuleName				= "HmBeModule";

	/*
	 * Module Name Table WatchDog 'WatchDog' BeEventModule 'BeEventModule'
	 * BeAdminModule 'BeAdminModule' BeCapwapModule 'BeCapwapModule'
	 * BeConfigModule 'BeConfigModule' BeDbModule 'BeDbModule' BeFaultModule
	 * 'BeFaultModule' BeLicenseModule 'BeLicenseModule' BeLogModule
	 * 'BeLogModule' BeOsModule 'BeOsModule' BeParaModule 'BeParameterModule'
	 * BePerformceModule 'BePerformaceModule' BeResourceModule
	 * 'BeResourceModule' BeSnmpModule 'BeSnmpModule' BeTopoModule
	 * 'BeTopoModule'
	 */

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public BaseModule()
	{

		/*
		 * when WatchDogModule don't finish initiliaze , BaseModule can invoke
		 * this implementation to avoid NullPointer Exception
		 */
		this.statusListener = new NullWatchDogListener();
		/*
		 * when BeEventModule don't finish initiliaze , BaseModule can invoke
		 * this implementation to avoid NullPointer Exception
		 */
		this.EventListener = new NullBeEventListener();
	}

	/**
	 * @param
	 * @return
	 */
	public void initModule()
	{

		// debug("BaseModule init begin");
		// StatusReport = new StatusReportThread(this);
		if (init())
		{
			this.changeToInitFinishedStatus();
		}
		else
		{
			this.changeToErrorStatus();
		}
		// debug("BaseModule init end");
	}

	/**
	 * @param
	 * @return
	 */
	public boolean init()
	{

		return true;
	}

	/**
	 * @param
	 * @return
	 */
	public void runModule()
	{

		// debug("BaseModule 'run' is called");
		if (run())
		{
			this.changeToIdleStatus();
		}
		else
		{
			this.changeToErrorStatus();
		}
	}

	/**
	 * @param
	 * @return
	 */
	public boolean run()
	{

		return true;
	}

	/**
	 * @param
	 * @return
	 */
	public void shutdownModule()
	{

		// debug("shutdownModule is called");
		/*
		 * stop reporting status to WatchDog module
		 */
		// this.StatusReport.stopReport();
		if (shutdown())
		{
			this.changeToShutdownStatus();
		}
		else
		{
			this.changeToErrorStatus();
		}

//		// report shutdown result
//		statusListener.reportModuleStatus(this, moduleStatus);
	}

	/**
	 * @param
	 * @return
	 */
	public boolean shutdown()
	{
		// debug("BaseModule shutdown method is called");
		return true;
	}

	/**
	 * @param
	 * @return
	 */
	public boolean restart()
	{

		return true;
	}

	/**
	 * @param
	 * @return
	 */
	public void dump()
	{

		return;
	}

	/**
	 * @param
	 * @return
	 */
	public void startReportStatus()
	{

		StatusReport.start();
		return;
	}

	/**
	 * @param
	 * @return
	 */
	public void stopReportStatus()
	{

		StatusReport.stopReport();
		return;
	}

	/**
	 * debug message output with default level
	 * 
	 * @param
	 * @return // *
	 */
	public void debug(String arg_Msg)
	{

		// try
		// {
		// getDebuger().debug(arg_Msg);
		// }
		// catch(Exception ex)
		// {
		// /*
		// *
		// */
		// }

		switch (ModuleId)
		{
			case ModuleID_Admin:
				DebugUtil.adminDebugInfo(arg_Msg);
				break;

			case ModuleID_BeApp:
			case ModuleID_Communication:
			case ModuleID_DB:
			case ModuleID_Debug:
			case ModuleID_Log:
			case ModuleID_Event:
			case ModuleID_Os:
			case ModuleID_Resource:
			case ModuleID_SNMP:
			case ModuleID_WatchDog:
				DebugUtil.commonDebugInfo(arg_Msg);
				break;

			case ModuleID_Config:
				DebugUtil.configDebugInfo(arg_Msg);
				break;

			case ModuleID_Fault:
				DebugUtil.faultDebugInfo(arg_Msg);
				break;

			case ModuleID_License:
				DebugUtil.licenseDebugInfo(arg_Msg);
				break;

			case ModuleID_Parameter:
				DebugUtil.parameterDebugInfo(arg_Msg);
				break;

			case ModuleId_Performance:
				DebugUtil.performanceDebugInfo(arg_Msg);
				break;

			case ModuleID_Topo:
				DebugUtil.topoDebugInfo(arg_Msg);
				break;

			default:
				break;
		}
	}

	/**
	 * @param sLevel:
	 *            the level of system log, which were defined in
	 *            com.ah.bo.admin.HmSystemLog
	 * @param strSrc:
	 *            which module set those event. (topology, monitoring, hiveap,
	 *            configuration, administration. which were defined in
	 *            com.ah.bo.admin.HmSystemLog)
	 * @param strComment:
	 *            the description of system log
	 */
	public void setSystemLog(short sLevel, String strSrc, String strComment)
	{
//		if (null == strSrc || null == strComment)
//		{
//			return;
//		}
//
//		BeSystemLogEvent oEvent = new BeSystemLogEvent(sLevel, strSrc,
//			strComment);
//
//		try
//		{
//			HmBeEventUtil.eventGenerated(oEvent);
//		}
//		catch (Exception ex)
//		{
//			DebugUtil.commonDebugWarn(
//				"BaseModule.setSystemLog() catch exception: ", ex);
//		}
		HmBeLogUtil.addSystemLog(sLevel, strSrc, strComment);
	}

	/**
	 * @param
	 * @return
	 */
	public ModuleDebuger getDebuger()
	{

		return Debuger;
	}

	/**
	 * @param
	 * @return
	 */
	public String getModuleName()
	{

		return ModuleName;
	}

	/**
	 * @param
	 * @return
	 */
	public void setModuleName(String arg_ModuleName)
	{

		ModuleName = arg_ModuleName;
		Debuger.setModuleName(arg_ModuleName);
	}

	/**
	 * @param
	 * @return
	 */
	public int getModuleId()
	{

		return ModuleId;
	}

	/**
	 * @param
	 * @return
	 */
	public void setModuleId(int arg_Id)
	{

		ModuleId = arg_Id;
		Debuger.setModuleId(arg_Id);
	}

	/**
	 * @param
	 * @return
	 */
	public ModuleStatus getModuleStatus()
	{

		return this.moduleStatus;
	}

	/**
	 * @param
	 * @return
	 */
	public WatchDogListener getWatchDogListener()
	{

		return statusListener;
	}

	/**
	 * @param
	 * @return
	 */
	public void setWatchDogListener(WatchDogListener arg_Listener)
	{

		statusListener = arg_Listener;
	}

	/**
	 * @param
	 * @return
	 */
	public BeEventListener getEventListener()
	{

		return EventListener;
	}

	/**
	 * @param
	 * @return
	 */
	public void setEventListener(BeEventListener arg_Listener)
	{

		EventListener = arg_Listener;
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToNotInitStatus()
	{

		this.moduleStatus.changeToNotInitStatus();
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToInitFinishedStatus()
	{

		this.moduleStatus.changeToInitFinishedStatus();
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToIdleStatus()
	{

		this.moduleStatus.changeToIdleStatus();
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToBusyStatus()
	{

		this.moduleStatus.changeToBusyStatus();
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToErrorStatus()
	{

		this.moduleStatus.changeToErrorStatus();
	}

	/**
	 * @param
	 * @return
	 */
	public void changeToShutdownStatus()
	{

		this.moduleStatus.changeToShutdownStatus();
	}

	/**
	 * @see com.ah.be.event.BeEventListener#getListenerId()
	 */
	public int getListenerId()
	{

		return this.getModuleId();
	}

	/**
	 * @see com.ah.be.event.BeEventDispatchListener#eventDispatched(com.ah.be.event.BeBaseEvent)
	 */
	public void eventDispatched(BeBaseEvent arg_Event)
	{
		// Current dispatch is broadcast, these msg could debug by override
		// method, comment it decrease log cost.

		// debug("Event received.source module=" + arg_Event.getMouleId()
		// + " event type = " + arg_Event.getEventType());
		// debug("BaseModule event handle process start");

		if (arg_Event.isShutdownRequestEvent())
		{
			handleShutdownRequest(arg_Event);
		}

//		if (arg_Event.isSnmpTrapEvent())
//		{
//			handleSnmpTrapEvent(arg_Event);
//		}
		// if (arg_Event.isEventProcessShutdown())
		// {
		// shutdownModule();
		// }
		// debug("BaseModule event handle process end");
	}

	private void handleShutdownRequest(BeBaseEvent arg_Event)
	{

		/*
		 * Shutdown this module
		 */
		shutdownModule();
		// this.changeToShutdownStatus();
		return;
	}

//	public void handleSnmpTrapEvent(BeBaseEvent arg_Event)
//	{
//
//		/*
//		 * this method will overide by Fault module
//		 */
//	}

	/**
	 * @param
	 * @return
	 */
	public void handleEventProcessShutdownEvent(BeBaseEvent arg_Event)
	{

		/*
		 * this method will be overided by Event module
		 */
	}
}
