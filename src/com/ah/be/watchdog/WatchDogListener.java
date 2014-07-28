/**
 *@filename		WatchDogListener.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 10:23:40
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
 * @author Steven
 * @version V1.0.0.0
 */
public interface WatchDogListener
{

	/**
	 * @param
	 * 
	 * @return
	 */
	public void reportModuleStatus(
		BaseModule arg_Module,
		ModuleStatus arg_Status);
}
