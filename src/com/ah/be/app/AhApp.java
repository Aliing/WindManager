/**
 *@filename		AhApp.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2 03:09:26
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */

package com.ah.be.app;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface AhApp
{
	
	
	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public ServerStatus getServiceStatus();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setServerStatus(ServerStatus arg_Status);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void stopApplication();
}
