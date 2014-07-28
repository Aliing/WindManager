/**
 *@filename		ModuleDebugerConsoleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5  09:10:19
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;


/**
 * @author Steven
 * @version V1.0.0.0
 */
public class ModuleDebugerConsoleImpl implements ModuleDebuger
{
	/**
	 * 
	 */
	private int	ModuleId	= -1;

	/**
	 *
	 */
	// private String ModuleName = "NotInit";
	/**
	 * @see com.ah.be.app.ModuleDebuger#debug(java.lang.String)
	 */
	public void debug(String arg_Msg)
	{
		switch (ModuleId)
		{
			case BaseModule.ModuleID_Admin:
				DebugUtil.adminDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_BeApp:
			case BaseModule.ModuleID_Communication:
			case BaseModule.ModuleID_DB:
			case BaseModule.ModuleID_Debug:
			case BaseModule.ModuleID_Log:
			case BaseModule.ModuleID_Event:
			case BaseModule.ModuleID_Os:
			case BaseModule.ModuleID_Resource:
			case BaseModule.ModuleID_SNMP:
			case BaseModule.ModuleID_WatchDog:
				DebugUtil.commonDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_Config:
				DebugUtil.configDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_Fault:
				DebugUtil.faultDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_License:
				DebugUtil.licenseDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_Parameter:
				DebugUtil.parameterDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleId_Performance:
				DebugUtil.performanceDebugInfo(arg_Msg);
				break;

			case BaseModule.ModuleID_Topo:
				DebugUtil.topoDebugInfo(arg_Msg);
				break;

			default:
				break;
		}
	}

	/**
	 * @see com.ah.be.app.ModuleDebuger#setModuleId(int)
	 */
	public void setModuleId(int arg_ModuleId)
	{

		ModuleId = arg_ModuleId;

	}

	/**
	 * @see com.ah.be.app.ModuleDebuger#setModuleName(java.lang.String)
	 */
	public void setModuleName(String arg_ModuleName)
	{

		// ModuleName = arg_ModuleName;
	}

}
