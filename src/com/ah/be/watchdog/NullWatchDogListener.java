/**
 *@filename		NullWatchDogListener.java
 *@version
 *@author		Steven
 *@createtime	2007-9-7 02:04:34
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.watchdog;

import com.ah.be.app.BaseModule;
import com.ah.be.app.ModuleStatus;

/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public class NullWatchDogListener implements WatchDogListener
{

	/** 
	 * @see com.ah.be.watchdog.WatchDogListener#reportModuleStatus(com.ah.be.app.BaseModule, com.ah.be.app.ModuleStatus)
	 */
	public void reportModuleStatus(
		BaseModule arg_Module,
		ModuleStatus arg_Status)
	{
		return;
	}

}
