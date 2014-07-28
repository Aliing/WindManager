package com.ah.be.debug;

/**
 * 
 *@filename		BeDebugInfo.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-15 05:17:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeDebugInfo
{
	/**
	 * see log target defined in DebugConstant
	 */
	private int			target;

	private int			level;

	private String		debugInfo;

	private Throwable	ex;

	public BeDebugInfo()
	{

	}

	public BeDebugInfo(int target, int level, String debugInfo)
	{
		this.target = target;
		this.level = level;
		this.debugInfo = debugInfo;
	}

	public BeDebugInfo(int target, int level, String debugInfo, Throwable ex)
	{
		this.target = target;
		this.level = level;
		this.debugInfo = debugInfo;
		this.ex = ex;
	}

	public String getDebugInfo()
	{
		return debugInfo;
	}

	public void setDebugInfo(String debugInfo)
	{
		this.debugInfo = debugInfo;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getTarget()
	{
		return target;
	}

	public void setTarget(int target)
	{
		this.target = target;
	}
	
	public Throwable getEx()
	{
		return ex;
	}

	public void setEx(Throwable ex)
	{
		this.ex = ex;
	}
}
