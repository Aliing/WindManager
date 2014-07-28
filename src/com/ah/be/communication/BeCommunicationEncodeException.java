package com.ah.be.communication;

public class BeCommunicationEncodeException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	public BeCommunicationEncodeException()
	{
		super();
	}

	public BeCommunicationEncodeException(String errorMsg)
	{
		super(errorMsg);
	}

	public BeCommunicationEncodeException(String errorMsg, Throwable e)
	{
		super(errorMsg, e);
	}
}
