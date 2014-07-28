/**
 *@filename		ServerStatus.java
 *@version
 *@author		Steven
 *@createtime	2007-9-8 05:03:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import java.util.Vector;

/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public class ServerStatus
{

	private Vector<BaseModule>		ModuleList;

	private Vector<ModuleStatus>	StatusList;

	/**
	 * 
	 * Construct method
	 *
	 * @param
	 *
	 * @throws
	 */
	public ServerStatus()
	{

	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public Vector<BaseModule> getModueList()
	{

		return ModuleList;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setModuleList(Vector<BaseModule> arg_List)
	{

		ModuleList = arg_List;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public Vector<ModuleStatus> getStatusList()
	{

		return StatusList;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setStatusList(Vector<ModuleStatus> arg_List)
	{

		StatusList = arg_List;
	}
}
