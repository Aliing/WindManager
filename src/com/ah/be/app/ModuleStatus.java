/**
 *@filename		ModuleStatus.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2 05:08:34
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
public class ModuleStatus
{

	public static final int	Module_Not_Init			= 1;

	public static final int	Module_Init_Finished	= 2;

	public static final int	Module_Idle				= 3;

	public static final int	Module_Busy				= 4;

	public static final int	Module_Error			= 5;

	public static final int	Module_Shutdown			= 6;

	private int				Status					= Module_Not_Init;

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public int getModuleStatus()
	{

		return Status;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setModuleStatus(int arg_Status)
	{

		Status = arg_Status;
	}

	public String getModuleStatusDesc()
	{

		switch (this.Status)
		{
			case ModuleStatus.Module_Not_Init:
			{
				return "Not initiliaze";
			}
			case ModuleStatus.Module_Init_Finished:
			{
				return "Initiliaze finished";
			}
			case ModuleStatus.Module_Idle:
			{
				return "Idle";
			}
			case ModuleStatus.Module_Error:
			{
				return "Error";
			}
			case ModuleStatus.Module_Busy:
			{
				return "Busy";
			}
			case ModuleStatus.Module_Shutdown:
			{
				return "Shutdown";
			}
			default:
			{
				return "Not Initiliaze";
			}
		}
	}

	/**
	 * 
	 * @param arg_Status
	 */
	public void changeModuleStatus(int arg_Status)
	{

		this.Status = arg_Status;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToNotInitStatus()
	{

		this.Status = Module_Not_Init;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToInitFinishedStatus()
	{

		this.Status = Module_Init_Finished;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToIdleStatus()
	{

		this.Status = Module_Idle;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToBusyStatus()
	{

		this.Status = Module_Busy;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToErrorStatus()
	{

		this.Status = Module_Error;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void changeToShutdownStatus()
	{

		this.Status = Module_Shutdown;
	}

}
