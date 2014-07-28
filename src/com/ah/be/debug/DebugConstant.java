package com.ah.be.debug;

import org.apache.log4j.Level;

/**
 * 
 *@filename		DebugConstant.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-27 11:30:44
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class DebugConstant {
	/**
	 * debug level level & constant value filter choose from log4j
	 */
	public static final int		NODEBUG						= Integer.MAX_VALUE;

	public static final int		DEBUG						= Level.DEBUG_INT;

	public static final int		INFO						= Level.INFO_INT;

	public static final int		WARNING						= Level.WARN_INT;

	public static final int		ERROR						= Level.ERROR_INT;

	public static final int		DEFAULTLEVEL				= INFO;

	/**
	 * message type
	 */
	public static final int		MESSAGETYPE_SETDEBUGLEVEL	= 1;

	public static final int		MESSAGETYPE_REPORTDEBUGINFO	= 2;

	/**
	 * log target
	 */
	public static final int		LOG_ALL						= 0;

	/**
	 * CLI_module
	 */
	public static final String	CLI_MODULE_ALL				= "all";

	public static final String	CLI_MODULE_TOPO				= "topo";

	public static final String	CLI_MODULE_CONFIG			= "config";

	public static final String	CLI_MODULE_ADMIN			= "admin";

	public static final String	CLI_MODULE_PERFORMANCE		= "performance";

	public static final String	CLI_MODULE_PARAMETER		= "parameter";

	public static final String	CLI_MODULE_COMMON			= "common";

	public static final String	CLI_MODULE_FAULT			= "fault";

	public static final String	CLI_MODULE_LICENSE			= "license";

	/**
	 * cli_
	 */
	public static final String	CLI_DEBUG					= "debug";

	public static final String	CLI_SHOW					= "show";

	public static final String	CLI_VERSION					= "version";

	public static final String	CLI_QUIT					= "quit";
	
	public static final String	CLI_HELP					= "help";

	public static final String	CLI_NO						= "no";

	public static final String	CLI_CONSOLE					= "console";

	/**
	 * cli_debuglevel
	 */
	public static final String	CLI_DEBUGLEVEL_NO			= "no";

	public static final String	CLI_DEBUGLEVEL_INFO			= "info";

	public static final String	CLI_DEBUGLEVEL_WARN			= "warn";

	public static final String	CLI_DEBUGLEVEL_ERROR		= "error";

	/**
	 * response msg
	 */
	public static final String	RSP_INCOMPLETECLI			= "Incomplete cli";

	public static final String	RSP_INVALIDCLI				= "Cli format is invalid";

	public static final String	RSP_INVALIDKEYCONTAINED		= "Input contain invalid key e.g. backspace";

	// ----- make room for util function

	public static final int		DEBUGLEVEL_INVALID			= -1;

	/**
	 * parse cli_debugLevel to int value
	 * 
	 * @param
	 * @return
	 */
	public static int debugLevelStr2Int(String debugLevelStr) {
		if (debugLevelStr.equalsIgnoreCase(DebugConstant.CLI_DEBUGLEVEL_NO)) {
			return DebugConstant.NODEBUG;
		} else if (debugLevelStr.equalsIgnoreCase(DebugConstant.CLI_DEBUGLEVEL_INFO)) {
			return DebugConstant.INFO;
		} else if (debugLevelStr.equalsIgnoreCase(DebugConstant.CLI_DEBUGLEVEL_WARN)) {
			return DebugConstant.WARNING;
		} else if (debugLevelStr.equalsIgnoreCase(DebugConstant.CLI_DEBUGLEVEL_ERROR)) {
			return DebugConstant.ERROR;
		} else {
			return DEBUGLEVEL_INVALID;
		}
	}

	/**
	 * debug level int2String
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static String debugLevelInt2Str(int debugLevel) {
		switch (debugLevel) {
		case DebugConstant.NODEBUG:
			return DebugConstant.CLI_DEBUGLEVEL_NO;

		case DebugConstant.INFO:
			return DebugConstant.CLI_DEBUGLEVEL_INFO;

		case DebugConstant.WARNING:
			return DebugConstant.CLI_DEBUGLEVEL_WARN;

		case DebugConstant.ERROR:
			return DebugConstant.CLI_DEBUGLEVEL_ERROR;

		default:
			return "UNKNOWN";
		}
	}
}
