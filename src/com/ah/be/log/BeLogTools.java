package com.ah.be.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ah.util.SystemStartUp;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 *@filename		BeLogTools.java
 *@author		Juyizhou, Lanbao, Jun
 *@createtime	2008-1-5 12:31:53
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */

/**
 * modify history:
 * 
 * @date 2009-6-24, support using java property ${hm.log.home}, by Jun
 * @date 2009-6-29, support using module as parameter, by Jun
 */
public class BeLogTools {

	static {
		// it's need when it is called by single java application
		SystemStartUp.readLogConfig();
	}

	public static final int							DEBUG				= Level.DEBUG_INT;

	public static final int							INFO				= Level.INFO_INT;

	public static final int							WARN				= Level.WARN_INT;

	public static final int							ERROR				= Level.ERROR_INT;
	
	public static final int							FATAL				= Level.FATAL_INT;

	private static final Map<Integer, Logger>	logMaps				= new HashMap<Integer, Logger>();

	static {
		addLogMap(HmLogConst.M_COMMON,"commonlog");
		addLogMap(HmLogConst.M_LICENSE, "licenselog");
		addLogMap(HmLogConst.M_FAULT, "faultlog");
		addLogMap(HmLogConst.M_CONFIG, "configlog");
		addLogMap(HmLogConst.M_TOPO, "topolog");
		addLogMap(HmLogConst.M_ADMIN, "adminlog");
		addLogMap(HmLogConst.M_PERFORMANCE, "performancelog");
		addLogMap(HmLogConst.M_PARAMETER, "parameterlog");
		addLogMap(HmLogConst.M_SGE, "sgelog");
		addLogMap(HmLogConst.M_RESTORE, "restorelog");
		addLogMap(HmLogConst.M_SHOWSHELL, "showshelllog");
		addLogMap(HmLogConst.M_TRACER, "tracerlog");
		addLogMap(HmLogConst.M_LOCATION, "locationlog");
		addLogMap(HmLogConst.M_GUIAUDIT, "guiaudit");
		addLogMap(HmLogConst.M_WS, "wslog");
		addLogMap(HmLogConst.M_ThreadInfo, "threadinfo");
		addLogMap(HmLogConst.M_PERFORMANCE_BULKOPERATION,"performancelog_bulkoperation");
		addLogMap(HmLogConst.M_PERFORMANCE_TABLEPARTITION,"performancelog_tablepartition");
	}

	public static void addLogMap(int logModule,String name) {
		Logger logger = Logger.getLogger(name);
		logMaps.put(logModule, logger);
	}
	
	@Deprecated
	public static void commonLog(int level_int, Object msg) {
		Logger commonLogger = logMaps.get(HmLogConst.M_COMMON);
		if (commonLogger == null) {
			log(msg);
			return;
		}

		commonLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void commonLog(int level_int, Object msg, Throwable t) {
		Logger commonLogger = logMaps.get(HmLogConst.M_COMMON);
		if (commonLogger == null) {
			log(msg, t);
			return;
		}

		commonLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void licenseLog(int level_int, Object msg) {
		Logger licenseLogger = logMaps.get(HmLogConst.M_LICENSE);
		if (licenseLogger == null) {
			log(msg);
			return;
		}

		licenseLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void licenseLog(int level_int, Object msg, Throwable t) {
		Logger licenseLogger = logMaps.get(HmLogConst.M_LICENSE);
		if (licenseLogger == null) {
			log(msg, t);
			return;
		}

		licenseLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void faultLog(int level_int, Object msg) {
		Logger faultLogger = logMaps.get(HmLogConst.M_FAULT);
		if (faultLogger == null) {
			log(msg);
			return;
		}

		faultLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void faultLog(int level_int, Object msg, Throwable t) {
		Logger faultLogger = logMaps.get(HmLogConst.M_FAULT);
		if (faultLogger == null) {
			log(msg, t);
			return;
		}

		faultLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void configLog(int level_int, Object msg) {
		Logger configLogger = logMaps.get(HmLogConst.M_CONFIG);
		if (configLogger == null) {
			log(msg);
			return;
		}

		configLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void configLog(int level_int, Object msg, Throwable t) {
		Logger configLogger = logMaps.get(HmLogConst.M_CONFIG);
		if (configLogger == null) {
			log(msg, t);
			return;
		}

		configLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void topoLog(int level_int, Object msg) {
		Logger topoLogger = logMaps.get(HmLogConst.M_TOPO);
		if (topoLogger == null) {
			log(msg);
			return;
		}

		topoLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void topoLog(int level_int, Object msg, Throwable t) {
		Logger topoLogger = logMaps.get(HmLogConst.M_TOPO);
		if (topoLogger == null) {
			log(msg, t);
			return;
		}

		topoLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void adminLog(int level_int, Object msg) {
		Logger adminLogger = logMaps.get(HmLogConst.M_ADMIN);
		if (adminLogger == null) {
			log(msg);
			return;
		}

		adminLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void adminLog(int level_int, Object msg, Throwable t) {
		Logger adminLogger = logMaps.get(HmLogConst.M_ADMIN);
		if (adminLogger == null) {
			log(msg, t);
			return;
		}

		adminLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void performanceLog(int level_int, Object msg) {
		Logger performanceLogger = logMaps.get(HmLogConst.M_PERFORMANCE);
		if (performanceLogger == null) {
			log(msg);
			return;
		}

		performanceLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void performanceLog(int level_int, Object msg, Throwable t) {
		Logger performanceLogger = logMaps.get(HmLogConst.M_PERFORMANCE);
		if (performanceLogger == null) {
			log(msg, t);
			return;
		}

		performanceLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void parameterLog(int level_int, Object msg) {
		Logger parameterLogger = logMaps.get(HmLogConst.M_PARAMETER);
		if (parameterLogger == null) {
			log(msg);
			return;
		}

		parameterLogger.log(Level.toLevel(level_int), msg);
	}
	
	@Deprecated
	public static void parameterLog(int level_int, Object msg, Throwable t) {
		Logger parameterLogger = logMaps.get(HmLogConst.M_PARAMETER);
		if (parameterLogger == null) {
			log(msg, t);
			return;
		}

		parameterLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void sgeLog(int level_int, Object msg) {
		Logger sgeLogger = logMaps.get(HmLogConst.M_SGE);
		if (sgeLogger == null) {
			log(msg);
			return;
		}

		sgeLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void sgeLog(int level_int, Object msg, Throwable t) {
		Logger sgeLogger = logMaps.get(HmLogConst.M_SGE);
		if (sgeLogger == null) {
			log(msg, t);
			return;
		}

		sgeLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void restoreLog(int level_int, Object msg) {
		Logger restoreLogger = logMaps.get(HmLogConst.M_RESTORE);
		if (restoreLogger == null) {
			log(msg);
			return;
		}

		restoreLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void restoreLog(int level_int, Object msg, Throwable t) {
		Logger restoreLogger = logMaps.get(HmLogConst.M_RESTORE);
		if (restoreLogger == null) {
			log(msg, t);
			return;
		}

		restoreLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void showshellLog(int level_int, Object msg) {
		Logger showshellLogger = logMaps.get(HmLogConst.M_SHOWSHELL);
		if (showshellLogger == null) {
			log(msg);
			return;
		}

		showshellLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void showshellLog(int level_int, Object msg, Throwable t) {
		Logger showshellLogger = logMaps.get(HmLogConst.M_SHOWSHELL);
		if (showshellLogger == null) {
			log(msg, t);
			return;
		}

		showshellLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void tracerLog(int level_int, Object msg) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_TRACER);
		if (tracerLogger == null) {
			log(msg);
			return;
		}

		tracerLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void tracerLog(int level_int, Object msg, Throwable t) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_TRACER);
		if (tracerLogger == null) {
			log(msg, t);
			return;
		}

		tracerLogger.log(Level.toLevel(level_int), msg, t);
	}

	@Deprecated
	public static void locationLog(int level_int, Object msg) {
		Logger locationLogger = logMaps.get(HmLogConst.M_LOCATION);
		if (locationLogger == null) {
			log(msg);
			return;
		}

		locationLogger.log(Level.toLevel(level_int), msg);
	}

	@Deprecated
	public static void locationLog(int level_int, Object msg, Throwable t) {
		Logger locationLogger = logMaps.get(HmLogConst.M_LOCATION);
		if (locationLogger == null) {
			log(msg, t);
			return;
		}

		locationLogger.log(Level.toLevel(level_int), msg, t);
	}

	// default is tracer log
	@Deprecated
	public static void debug(Object msg) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg);
			return;
		}

		tracerLogger.debug(msg);
	}

	@Deprecated
	public static void debug(Object msg, Throwable t) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg, t);
			return;
		}

		tracerLogger.debug(msg, t);
	}

	@Deprecated
	public static void info(Object msg) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg);
			return;
		}

		tracerLogger.info(msg);
	}

	@Deprecated
	public static void info(Object msg, Throwable t) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg, t);
			return;
		}

		tracerLogger.info(msg, t);
	}

	@Deprecated
	public static void warn(Object msg) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg);
			return;
		}

		tracerLogger.warn(msg);
	}

	@Deprecated
	public static void warn(Object msg, Throwable t) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg, t);
			return;
		}

		tracerLogger.warn(msg, t);
	}

	@Deprecated
	public static void error(Object msg) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg);
			return;
		}

		tracerLogger.error(msg);
	}

	@Deprecated
	public static void error(Object msg, Throwable t) {
		Logger tracerLogger = logMaps.get(HmLogConst.M_COMMON);
		if (tracerLogger == null) {
			log(msg, t);
			return;
		}

		tracerLogger.error(msg, t);
	}

	private static void log(Logger logger, int logLevel, Object msg, Throwable t) {
		if (t == null) {
			if (logLevel == DEBUG) {
				logger.debug(msg);
			} else if (logLevel == INFO) {
				logger.info(msg);
			} else if (logLevel == WARN) {
				logger.warn(msg);
			} else if (logLevel == ERROR) {
				logger.error(msg);
			} else {
				logger.fatal(msg);
			}
		} else {
			if (logLevel == DEBUG) {
				logger.debug(msg, t);
			} else if (logLevel == INFO) {
				logger.info(msg, t);
			} else if (logLevel == WARN) {
				logger.warn(msg, t);
			} else if (logLevel == ERROR) {
				logger.error(msg, t);
			} else {
				logger.fatal(msg, t);
			}
		}
	}
	public static void log(int logModule, int logLevel, Object msg, Throwable t) {
		Logger logger = logMaps.get(logModule);
		if (logger != null) {
			log(logger, logLevel, msg, t);
		} else {
			int i = 1;
			while (i < HmLogConst.MODULE_MAX) {
				if ((i & logModule) != 0) {
					logger = logMaps.get(i);
					if (logger != null) {
						log(logger, logLevel, msg, t);
					}
				}
				i = i << 1;
			}
		}
	}

	// using log module as argument
	// using log module as argument
	public static void debug(int logModule, Object msg) {
		log(logModule,DEBUG,msg,null);
	}

	public static void debug(int logModule, Object msg, Throwable t) {
		log(logModule,DEBUG,msg,t);
	}

	public static void info(int logModule, Object msg) {
		log(logModule,INFO,msg,null);
	}

	public static void info(int logModule, Object msg, Throwable t) {
		log(logModule,INFO,msg,t);
	}

	public static void warn(int logModule, Object msg) {
		log(logModule,WARN,msg,null);
	}

	public static void warn(int logModule, Object msg, Throwable t) {
		log(logModule,WARN,msg,t);
	}

	public static void error(int logModule, Object msg) {
		log(logModule,ERROR,msg,null);
	}

	public static void error(int logModule, Object msg, Throwable t) {
		log(logModule,ERROR,msg,t);
	}

	public static void fatal(int logModule, Object msg) {
		log(logModule,FATAL,msg,null);
	}

	public static void fatal(int logModule, Object msg, Throwable t) {
		log(logModule,FATAL,msg,t);
	}
	
	private static void log(Object msg) {
		String curTime = AhDateTimeUtil.getCurrentDateTime();
		System.out.println(curTime + " # " + msg);
	}

	private static void log(Object msg, Throwable t) {
		String curTime = AhDateTimeUtil.getCurrentDateTime();
		System.out.println(curTime + " # " + msg);
		t.printStackTrace();
	}

}