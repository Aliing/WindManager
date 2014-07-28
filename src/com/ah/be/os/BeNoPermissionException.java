package com.ah.be.os;

public class BeNoPermissionException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	public BeNoPermissionException()
	{
		super();
	}

	public BeNoPermissionException(String a_errorMsg)
	{
		super(a_errorMsg);
	}

	public BeNoPermissionException(String a_errorMsg, Throwable a_throwable)
	{
		super(a_errorMsg, a_throwable);
	}
}
