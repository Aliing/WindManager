package com.ah.be.debug;

/**
 * 
 *@filename		BeDebugModule.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-27 10:00:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public interface BeDebugModule
{
	/**
	 * debug message output target common.log with default level
	 */
	public void commonDebug(String debugInfo);

	/**
	 * debug message output target common.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void commonDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target common.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void commonDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target fault.log with default level
	 */
	public void faultDebug(String debugInfo);

	/**
	 * debug message output target fault.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void faultDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target fault.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void faultDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target config.log with default level
	 */
	public void configDebug(String debugInfo);

	/**
	 * debug message output target config.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void configDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target config.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void configDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target topo.log with default level
	 */
	public void topoDebug(String debugInfo);

	/**
	 * debug message output target topo.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void topoDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target topo.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void topoDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target admin.log with default level
	 */
	public void adminDebug(String debugInfo);

	/**
	 * debug message output target admin.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void adminDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target admin.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void adminDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target performance.log with default level
	 */
	public void performanceDebug(String debugInfo);

	/**
	 * debug message output target performance.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void performanceDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target performance.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void performanceDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target license.log with default level
	 */
	public void licenseDebug(String debugInfo);

	/**
	 * debug message output target license.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void licenseDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target license.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void licenseDebug(int debugLevel, String debugInfo, Throwable e);

	/**
	 * debug message output target parameter.log with default level
	 */
	public void parameterDebug(String debugInfo);

	/**
	 * debug message output target parameter.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void parameterDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target parameter.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void parameterDebug(int debugLevel, String debugInfo, Throwable e);
	
	/**
	 * debug message output target sge.log with default level
	 */
	public void sgeDebug(String debugInfo);

	/**
	 * debug message output target sge.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 */
	public void sgeDebug(int debugLevel, String debugInfo);

	/**
	 * debug message output target sge.log
	 * 
	 * @param debugLevel,
	 *            defination see DebugConstant.java
	 * @param e,
	 *            exception object
	 */
	public void sgeDebug(int debugLevel, String debugInfo, Throwable e);
}
