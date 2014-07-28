/**
 *@filename		BeEventModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-4 09:56:36
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeEventModule
{

	/**
	 * @param
	 * 
	 * @return
	 */
	public void testMethod();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
//	public void registerSourceEventModule(
//		BaseModule arg_Module,
//		BeEventProcesser arg_Processer);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void registeEventDispatchListener(

	BeEventDispatchListener arg_Listener);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void unregisterEventDispatchListener(
		BeEventDispatchListener arg_Dispatcher);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
//	public void unregisterEventListener(BaseModule arg_Module);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void startEventProcesser(/*BaseModule arg_Module*/);
}
