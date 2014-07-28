package com.ah.be.log;


public class HmLogConst {

	/**
	 * log level definition
	 */
//	public static final int	L_DEBUG			= Level.DEBUG_INT;
//	public static final int	L_INFO			= Level.INFO_INT;
//	public static final int	L_WARN			= Level.WARN_INT;
//	public static final int	L_ERROR			= Level.ERROR_INT;
//	public static final int L_FATAL			= Level.FATAL_INT;

	/**
	 * log module definition
	 * 
	 * <br>
	 * note that there are just 16 modules could be allowed.
	 */
	
	private static int moduleNum = 0;
	public static final int	M_TRACER		= 1 << (moduleNum++);
	public static final int	M_COMMON		= 1 << (moduleNum++);
	public static final int	M_LICENSE		= 1 << (moduleNum++);
	public static final int	M_FAULT			= 1 << (moduleNum++);
	public static final int	M_CONFIG		= 1 << (moduleNum++);
	public static final int	M_TOPO			= 1 << (moduleNum++);
	public static final int	M_ADMIN			= 1 << (moduleNum++);
	public static final int	M_PERFORMANCE	= 1 << (moduleNum++);
	public static final int	M_ThreadInfo	= 1 << (moduleNum++);
	public static final int	M_PARAMETER		= 1 << (moduleNum++);
	public static final int	M_SGE			= 1 << (moduleNum++);
	public static final int	M_RESTORE		= 1 << (moduleNum++);
	public static final int	M_SHOWSHELL		= 1 << (moduleNum++);
	public static final int	M_LOCATION		= 1 << (moduleNum++);
	public static final int	M_GUIAUDIT		= 1 << (moduleNum++);
	public static final int	M_WS			= 1 << (moduleNum++);
	public static final int	MODULE_MAX		= 1 << (moduleNum++);
	public static final int M_PERFORMANCE_BULKOPERATION		= moduleNum++;
	public static final int M_PERFORMANCE_TABLEPARTITION	= moduleNum++;
	
}