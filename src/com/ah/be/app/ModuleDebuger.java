/**
 *@filename		ModuleDebuger.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5  09:07:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public interface ModuleDebuger
{

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void debug(String arg_Msg);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setModuleId(int arg_ModuleId);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setModuleName(String arg_ModuleName);
}
