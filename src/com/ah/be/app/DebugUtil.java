package com.ah.be.app;

import java.util.HashMap;
import java.util.Map;

import com.ah.be.debug.BeDebugModuleImpl;
import com.ah.be.debug.DebugConstant;
import com.ah.be.log.HmLogConst;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 *@author		juyizhou
 *@createtime	2007-12-27 02:25:35
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 * @date 2009-6-29 add some methods, by jun
 */
public class DebugUtil {

	// mark: because in some context, HmBe instance maybe null(e.g. in restore process), we at best
	// ensure debug could work.
	private static final BeDebugModuleImpl	debugModule	= BeDebugModuleImpl.getInstance();

	private static final Map<Integer, Integer>	logMaps		= new HashMap<Integer, Integer>();

	static {
		logMaps.put(HmLogConst.M_COMMON, HmLogConst.M_COMMON);
		logMaps.put(HmLogConst.M_LICENSE, HmLogConst.M_LICENSE);
		logMaps.put(HmLogConst.M_FAULT, HmLogConst.M_FAULT);
		logMaps.put(HmLogConst.M_CONFIG, HmLogConst.M_CONFIG);
		logMaps.put(HmLogConst.M_TOPO, HmLogConst.M_TOPO);
		logMaps.put(HmLogConst.M_ADMIN, HmLogConst.M_ADMIN);
		logMaps.put(HmLogConst.M_PERFORMANCE, HmLogConst.M_PERFORMANCE);
		logMaps.put(HmLogConst.M_ThreadInfo, HmLogConst.M_ThreadInfo);
		logMaps.put(HmLogConst.M_PARAMETER, HmLogConst.M_PARAMETER);
		logMaps.put(HmLogConst.M_SGE, HmLogConst.M_SGE);
		logMaps.put(HmLogConst.M_RESTORE, HmLogConst.M_RESTORE);
		logMaps.put(HmLogConst.M_SHOWSHELL, HmLogConst.M_SHOWSHELL);
		logMaps.put(HmLogConst.M_TRACER, HmLogConst.M_TRACER);
		logMaps.put(HmLogConst.M_LOCATION, HmLogConst.M_LOCATION);
		logMaps.put(HmLogConst.M_GUIAUDIT, HmLogConst.M_GUIAUDIT);
		logMaps.put(HmLogConst.M_WS, HmLogConst.M_WS);
	}

	public static void debug(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.DEBUG, msg);
	}

	public static void debug(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.DEBUG, msg, t);
	}

	public static void info(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.INFO, msg);
	}

	public static void info(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.INFO, msg, t);
	}

	public static void warn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.WARNING, msg);
	}

	public static void warn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}
		debugModule.log(HmLogConst.M_TRACER, DebugConstant.WARNING, msg, t);
	}

	public static void error(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.log(HmLogConst.M_TRACER, DebugConstant.ERROR, msg);
	}

	public static void error(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.log(HmLogConst.M_TRACER, DebugConstant.ERROR, msg, t);
	}

	public static void debug(int logModule, String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.DEBUG, msg);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.DEBUG, msg);
				}
				i = i << 1;
			}
		}
	}

	public static void debug(int logModule, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.DEBUG, msg, t);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.DEBUG, msg, t);
				}
				i = i << 1;
			}
		}
	}

	public static void info(int logModule, String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.INFO, msg);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.INFO, msg);
				}
				i = i << 1;
			}
		}
	}

	public static void info(int logModule, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.INFO, msg, t);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.INFO, msg, t);
				}
				i = i << 1;
			}
		}
	}

	public static void warn(int logModule, String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.WARNING, msg);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.WARNING, msg);
				}
				i = i << 1;
			}
		}
	}

	public static void warn(int logModule, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.WARNING, msg, t);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.WARNING, msg, t);
				}
				i = i << 1;
			}
		}
	}

	public static void error(int logModule, String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.ERROR, msg);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.ERROR, msg);
				}
				i = i << 1;
			}
		}
	}

	public static void error(int logModule, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		if (logMaps.containsKey(logModule)) {
			debugModule.log(logMaps.get(logModule), DebugConstant.ERROR, msg, t);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					debugModule.log(logMaps.get(i), DebugConstant.ERROR, msg, t);
				}
				i = i << 1;
			}
		}
	}

	/**
	 * debug message output target common.log with default level
	 *
	 * @param msg -
	 */
	public static void commonDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.commonDebug(msg);
	}

	/**
	 * debug message output target common.log with warn level
	 *
	 * @param msg -
	 */
	public static void commonDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.commonDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target common.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void commonDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.commonDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target common.log with error level
	 *
	 * @param msg -
	 */
	public static void commonDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.commonDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target common.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void commonDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.commonDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target common.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void commonDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.commonDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target fault.log with default level
	 *
	 * @param msg -
	 */
	public static void faultDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.faultDebug(msg);
	}

	/**
	 * debug message output target fault.log with warn level
	 *
	 * @param msg -
	 */
	public static void faultDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.faultDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target fault.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void faultDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.faultDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target fault.log with error level
	 *
	 * @param msg -
	 */
	public static void faultDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.faultDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target fault.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void faultDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.faultDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target fault.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void faultDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.faultDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target config.log with default level
	 *
	 * @param msg -
	 */
	public static void configDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.configDebug(msg);
	}

	/**
	 * debug message output target config.log with warn level
	 *
	 * @param msg -
	 */
	public static void configDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.configDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target config.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void configDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.configDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target config.log with error level
	 *
	 * @param msg -
	 */
	public static void configDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.configDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target config.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void configDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.configDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target config.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void configDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.configDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target topo.log with default level
	 *
	 * @param msg -
	 */
	public static void topoDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.topoDebug(msg);
	}

	/**
	 * debug message output target topo.log with warn level
	 *
	 * @param msg -
	 */
	public static void topoDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.topoDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target topo.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void topoDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.topoDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target topo.log with error level
	 *
	 * @param msg -
	 */
	public static void topoDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.topoDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target topo.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void topoDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.topoDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target topo.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void topoDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.topoDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target admin.log with default level
	 *
	 * @param msg -
	 */
	public static void adminDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.adminDebug(msg);
	}

	/**
	 * debug message output target admin.log with warn level
	 *
	 * @param msg -
	 */
	public static void adminDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.adminDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target admin.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void adminDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.adminDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target admin.log with error level
	 *
	 * @param msg -
	 */
	public static void adminDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.adminDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target admin.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void adminDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.adminDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target admin.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void adminDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.adminDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target performance.log with default level
	 *
	 * @param msg -
	 */
	public static void performanceDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.performanceDebug(msg);
	}

	/**
	 * debug message output target performance.log with warn level
	 *
	 * @param msg -
	 */
	public static void performanceDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.performanceDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target performance.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void performanceDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.performanceDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target performance.log with error level
	 *
	 * @param msg -
	 */
	public static void performanceDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.performanceDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target performance.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void performanceDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.performanceDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target performance.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void performanceDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.performanceDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target license.log with default level
	 *
	 * @param msg -
	 */
	public static void licenseDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.licenseDebug(msg);
	}

	/**
	 * debug message output target license.log with warn level
	 *
	 * @param msg -
	 */
	public static void licenseDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.licenseDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target license.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void licenseDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.licenseDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target license.log with error level
	 *
	 * @param msg -
	 */
	public static void licenseDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.licenseDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target license.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void licenseDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.licenseDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target license.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void licenseDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.licenseDebug(debugLevel, msg, t);
	}

	/**
	 * debug message output target parameter.log with default level
	 *
	 * @param msg -
	 */
	public static void parameterDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.parameterDebug(msg);
	}

	/**
	 * debug message output target parameter.log with warn level
	 *
	 * @param msg -
	 */
	public static void parameterDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.parameterDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target parameter.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void parameterDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.parameterDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target parameter.log with error level
	 *
	 * @param msg -
	 */
	public static void parameterDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.parameterDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target parameter.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void parameterDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.parameterDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target parameter.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void parameterDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.parameterDebug(debugLevel, msg, t);
	}

	// ---- add sge.log for chenyang
	/**
	 * debug message output target sge.log with default level
	 *
	 * @param msg -
	 */
	public static void sgeDebugInfo(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.sgeDebug(msg);
	}

	/**
	 * debug message output target sge.log with warn level
	 *
	 * @param msg -
	 */
	public static void sgeDebugWarn(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.sgeDebug(DebugConstant.WARNING, msg);
	}

	/**
	 * debug message output target sge.log with warn level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void sgeDebugWarn(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.sgeDebug(DebugConstant.WARNING, msg, t);
	}

	/**
	 * debug message output target sge.log with error level
	 *
	 * @param msg -
	 */
	public static void sgeDebugError(String msg) {
		if (debugModule == null) {
			log(msg);
			return;
		}

		debugModule.sgeDebug(DebugConstant.ERROR, msg);
	}

	/**
	 * debug message output target sge.log with error level
	 *
	 * @param msg -
	 * @param t -
	 */
	public static void sgeDebugError(String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.sgeDebug(DebugConstant.ERROR, msg, t);
	}

	/**
	 * debug message output target sge.log<br>
	 * This is the most generic printing method. It is intended to be invoked by wrapper classes.
	 * 
	 * @param debugLevel,
	 *            definition see DebugConstant.java
	 * @param msg -
	 * @param t -
	 */
	public static void sgeDebug(int debugLevel, String msg, Throwable t) {
		if (debugModule == null) {
			log(msg, t);
			return;
		}

		debugModule.sgeDebug(debugLevel, msg, t);
	}

	private static void log(String msg) {
		String curTime = AhDateTimeUtil.getCurrentDateTime();
		System.out.println(curTime + " # " + msg);
	}

	private static void log(String msg, Throwable t) {
		String curTime = AhDateTimeUtil.getCurrentDateTime();
		System.out.println(curTime + " # " + msg);
		t.printStackTrace();
	}

}