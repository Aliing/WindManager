/**
 *@filename		SnmpException.java
 *@version
 *@author		Frank
 *@createtime	2007-9-5 16:19:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.snmp;

/**
 * @author Frank
 * @version V1.0.0.0
 */
public class SnmpException extends Exception
{
	static final long	serialVersionUID	= 0L;

	private int			errorStatus			= 0;

	/**
	 * get error status
	 */
	public int getErrorStatus()
	{
		return errorStatus;
	}

	/**
	 * set error status
	 * 
	 * @param errorStatus
	 *            status of error
	 */
	public void setErrorStatus(int errorStatus)
	{
		this.errorStatus = errorStatus;
	}

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public SnmpException()
	{
	}

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public SnmpException(String message)
	{
		super(message);
	}

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public SnmpException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct method
	 * 
	 * @param
	 * @throws
	 */
	public SnmpException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
