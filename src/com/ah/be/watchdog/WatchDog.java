/**
 *@filename		WatchDog.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2  04:42:19
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
 * @author		Steven
 * @version		V1.0.0.0 
 */
public interface WatchDog
{

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void registerModule(BaseModule arg_Module);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void unregisterModule(int arg_ModuleId);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public boolean startWatch();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public boolean stopWatch();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public Vector<BaseModule> getRegisteredModules();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public Vector<ModuleStatus> getModulesStatus();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public Vector<Long> getLastReportTime();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public BaseModule getModule();
}
