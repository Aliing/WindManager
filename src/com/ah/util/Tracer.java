package com.ah.util;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.log.HmLogConst;
import com.ah.util.datetime.AhDateTimeUtil;

public class Tracer implements Serializable {

	private static final HashMap<Integer, String>	logMaps				= new HashMap<Integer, String>(
																				16);

	static {
		// it's need when it is called by single java application
		SystemStartUp.readLogConfig();
	}
	static {
		logMaps.put(HmLogConst.M_COMMON, "commonlog");
		logMaps.put(HmLogConst.M_LICENSE, "licenselog");
		logMaps.put(HmLogConst.M_FAULT, "faultlog");
		logMaps.put(HmLogConst.M_CONFIG, "configlog");
		logMaps.put(HmLogConst.M_TOPO, "topolog");
		logMaps.put(HmLogConst.M_ADMIN, "adminlog");
		logMaps.put(HmLogConst.M_PERFORMANCE, "performancelog");
		logMaps.put(HmLogConst.M_PARAMETER, "parameterlog");
		logMaps.put(HmLogConst.M_SGE, "sgelog");
		logMaps.put(HmLogConst.M_RESTORE, "restorelog");
		logMaps.put(HmLogConst.M_SHOWSHELL, "showshelllog");
		logMaps.put(HmLogConst.M_TRACER, "tracerlog");
		logMaps.put(HmLogConst.M_LOCATION, "locationlog");
		logMaps.put(HmLogConst.M_WS, "wslog");
		logMaps.put(HmLogConst.M_GUIAUDIT, "guiaudit");
		logMaps.put(HmLogConst.M_ThreadInfo, "threadinfo");
	}

	private static final long						serialVersionUID	= 1L;

	private Log										logger;

	private String									srcClass;

	public Tracer(Class<?> srcClass) {
		this.srcClass = srcClass.getSimpleName();
		this.logger = LogFactory.getLog("tracerlog");
	}

	public Tracer(String srcClass) {
		this.srcClass = srcClass;
		this.logger = LogFactory.getLog("tracerlog");
	}

	public Tracer(Class<?> srcClass, String logger) {
		this.srcClass = srcClass.getSimpleName();

		if (logMaps.containsValue(logger)) {
			this.logger = LogFactory.getLog(logger);
		} else {
			this.logger = LogFactory.getLog("tracerlog");
		}
	}

	public Tracer(Class<?> srcClass, int logModule) {
		this.srcClass = srcClass.getSimpleName();

		if (logMaps.containsKey(logModule)) {
			this.logger = LogFactory.getLog(logMaps.get(logModule));
		} else {
			this.logger = LogFactory.getLog("tracerlog");
		}
	}

	public Log getLogger() {
		return logger;
	}

	public void error(String srcMethod, String msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.error(srcClass + "  " + srcMethod + "\r\n[Message] " + msg);
	}

	public void error(String srcMethod, String msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.error(srcClass + "  " + srcMethod + "\r\n[Message] " + msg, t);
	}

	public void warning(String srcMethod, String msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.warn(srcClass + "  " + srcMethod + "\r\n[Message] " + msg);
	}

	public void warning(String srcMethod, String msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.warn(srcClass + "  " + srcMethod + "\r\n[Message] " + msg, t);
	}

	public void warn(String srcMethod, String msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.warn(srcClass + "  " + srcMethod + "\r\n[Message] " + msg);
	}

	public void warn(String srcMethod, String msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.warn(srcClass + "  " + srcMethod + "\r\n[Message] " + msg, t);
	}

	public void info(String srcMethod, String msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.info(srcClass + "  " + srcMethod + "\r\n[Message] " + msg);
	}

	public void info(String srcMethod, String msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.info(srcClass + "  " + srcMethod + "\r\n[Message] " + msg, t);
	}

	public void debug(String srcMethod, String msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.debug(srcClass + "  " + srcMethod + "\r\n[Message] " + msg);
	}

	public void debug(String srcMethod, String msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.debug(srcClass + "  " + srcMethod + "\r\n[Message] " + msg, t);
	}

	// below method same with log4j
	public void debug(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.debug(srcClass + "# " + msg);
	}

	public void debug(Object msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.debug(srcClass + "# " + msg, t);
	}

	public void info(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.info(srcClass + "# " + msg);
	}

	public void info(Object msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.info(srcClass + "# " + msg, t);
	}

	public void warn(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.warn(srcClass + "# " + msg);
	}

	public void warn(Object msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.warn(srcClass + "# " + msg, t);
	}

	public void error(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.error(srcClass + "# " + msg);
	}

	public void error(Object msg, Throwable t) {
		if (logger == null) {
			log(msg, t);
			return;
		}

		logger.error(srcClass + "# " + msg, t);
	}

	// special for location
	public void info_ln(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.info(msg + "\n");
	}

	public void info_non(Object msg) {
		if (logger == null) {
			log(msg);
			return;
		}

		logger.info(msg);
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