package com.ah.be.debug;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.BaseModule;
import com.ah.be.common.ConfigUtil;
import com.ah.be.debug.tcpserver.DefaultTcpChannelProcess;
import com.ah.be.debug.tcpserver.ITcpChannelProcess;
import com.ah.be.debug.tcpserver.TcpServerHandle;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

/**
 *@author		juyizhou
 *@createtime	2007-12-27 10:38:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 * @date 2009-6-29 add some methods, by jun
 */
public class BeDebugModuleImpl extends BaseModule implements BeDebugModule {

	private final BlockingQueue<BeDebugInfo>	debugQueue;

	private static final int			DEBUGQUEUESIZE			= 10000;

	private Thread						debugProcessThread;

	/**
	 * control debugProcessThread shutdown
	 */
	private boolean						isContinue				= true;

	/**
	 * debug level for be sub-system level definition see DebugConstant
	 */
	private int							debugLevel_common		= DebugConstant.INFO;
	private int							debugLevel_fault		= DebugConstant.INFO;
	private int							debugLevel_topo			= DebugConstant.INFO;
	private int							debugLevel_admin		= DebugConstant.INFO;
	private int							debugLevel_config		= DebugConstant.INFO;
	private int							debugLevel_performance	= DebugConstant.INFO;
	private int							debugLevel_threadinfo	= DebugConstant.INFO;
	private int							debugLevel_license		= DebugConstant.INFO;
	private int							debugLevel_parameter	= DebugConstant.INFO;
	private int							debugLevel_sge			= DebugConstant.INFO;
	private int							debugLevel_restore		= DebugConstant.INFO;
	private int							debugLevel_showshell	= DebugConstant.INFO;
	private int							debugLevel_tracer		= DebugConstant.INFO;
	private int							debugLevel_location		= DebugConstant.INFO;

	private int							tcpPort					= 13333;

	private static BeDebugModuleImpl	instance;

	/**
	 * Construct method
	 */
	private BeDebugModuleImpl() {
		super();
		setModuleId(BaseModule.ModuleID_Debug);
		setModuleName("BeDebugModule");

		debugQueue = new LinkedBlockingQueue<BeDebugInfo>(DEBUGQUEUESIZE);
	}

	public synchronized static BeDebugModuleImpl getInstance() {
		if (instance == null) {
			instance = new BeDebugModuleImpl();
		}

		return instance;
	}

	@Override
	public boolean init() {
		return super.init();
	}

	/**
	 * tcp channel processor
	 */
	private ITcpChannelProcess	tcpProcessor;

	private TcpServerHandle		serverHandle;

	/**
	 * start tcp server to listen on 13333 port
	 */
	private void startTcpServer() {
		try {
			tcpProcessor = new DefaultTcpChannelProcess();
			tcpProcessor.setDebugModuleImpl(this);
			tcpPort = Integer.valueOf(
					ConfigUtil.getConfigInfo(ConfigUtil.SECTION_DEBUG, ConfigUtil.KEY_TCPPORT,
							"13333"));
			serverHandle = new TcpServerHandle(tcpPort, tcpProcessor);
			MgrUtil.setThreadName(serverHandle, this.getClass().getSimpleName());
			serverHandle.start();
		} catch (Exception e) {
			commonDebug(DebugConstant.WARNING,
					"BeDebugModuleImpl.startTcpServer(): failed to start tcp server", e);
		}
	}

	private boolean	isRunning	= false;

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run() {
		if (!isRunning) {
			startTcpServer();

			// start processor
			startDebugProcessor();

			isRunning = true;
		}

		return super.run();
	}

	@Override
	public boolean shutdown() {
		return super.shutdown();
	}

	/**
	 * start thread which process event in debugQueue
	 */
	public void startDebugProcessor() {
		if (debugProcessThread != null && isContinue) {
			return;
		}

		isContinue = true;
		debugProcessThread = new Thread() {
			public void run() {
				System.out.println("debug processor thread begin to run");

				while (isContinue) {
					try {
						// take() method blocks
						BeDebugInfo debugInfo = debugQueue.take();
						// check before debug obj created
						// if (isDiscarded(debugInfo))
						// {
						// continue;
						// }

						// 1. output to file
						dispatch2LogFile(debugInfo);

						// 2. output to debug console
						dispatch2DebugConsole(debugInfo);
					} catch (Exception e) {
						// debug("Thread for processing debug catch exception: "
						// + e.getMessage());

						// if event module queue full,maybe cause trouble
						// setSystemLog(HmSystemLog.LEVEL_MAJOR,
						// HmSystemLog.FEATURE_MONITORING,
						// "Thread for processing debug catched some error!");
					} catch (Error e) {
						System.out.println("debug module catch error. ");
						e.printStackTrace();
					}
				}

				System.out.println("debug processor thread begin is stop running now");
			}
		};
		debugProcessThread.setName("debugProcessThread");
		MgrUtil.setThreadName(debugProcessThread, this.getClass().getSimpleName());
		debugProcessThread.start();
	}

	/**
	 * output to debug console
	 * 
	 * @param debugInfo -
	 */
	private void dispatch2DebugConsole(BeDebugInfo debugInfo) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sf.format(new Date());

		String debugStr = "[" + currentTime + "]  ["
				+ DebugConstant.debugLevelInt2Str(debugInfo.getLevel()) + "]   "
				+ debugInfo.getDebugInfo();

		if (debugInfo.getEx() != null) {
			debugStr = debugStr + "\r\n" + debugInfo.getEx();
		}

		tcpProcessor.sendToAllDebugConsole(debugStr);
		tcpProcessor.sendToAllDebugConsole("\r\n");
	}

	/**
	 * output to file
	 * 
	 * @param debugInfo -
	 */
	private void dispatch2LogFile(BeDebugInfo debugInfo) {
		BeLogTools.log(debugInfo.getTarget(), debugInfo.getLevel(), debugInfo.getDebugInfo(),
				debugInfo.getEx());
	}

	private void addIntoQueue(BeDebugInfo debug) {
		try {
			debugQueue.add(debug);
		} catch (IllegalStateException e) {
			System.out
					.println("Add debug info into queue catch IllegalStateException, queue size = "
							+ debugQueue.size());
			debugQueue.clear();
			System.out.println("Clear debug queue, current size = " + debugQueue.size());
		} catch (Exception e) {
			System.out.println("Add debug info into queue catch exception" + e.getMessage());
		}
	}

	public void log(int logModule, int logLevel, String debugInfo) {
		int currentLevel = getCurrentLevel(logModule);

		if (logLevel < currentLevel) {
			return;
		}

		addIntoQueue(new BeDebugInfo(logModule, logLevel, debugInfo));
	}

	public void log(int logModule, int logLevel, String debugInfo, Throwable t) {
		int currentLevel = getCurrentLevel(logModule);

		if (logLevel < currentLevel) {
			return;
		}

		addIntoQueue(new BeDebugInfo(logModule, logLevel, debugInfo, t));
	}

	private int getCurrentLevel(int logModule) {
		int level = -1;
		if (logModule == HmLogConst.M_COMMON) {
			level = debugLevel_common;
		} else if (logModule == HmLogConst.M_LICENSE) {
			level = debugLevel_license;
		} else if (logModule == HmLogConst.M_FAULT) {
			level = debugLevel_fault;
		} else if (logModule == HmLogConst.M_CONFIG) {
			level = debugLevel_config;
		} else if (logModule == HmLogConst.M_TOPO) {
			level = debugLevel_topo;
		} else if (logModule == HmLogConst.M_ADMIN) {
			level = debugLevel_admin;
		} else if (logModule == HmLogConst.M_PERFORMANCE) {
			level = debugLevel_performance;
		} else if (logModule == HmLogConst.M_ThreadInfo) {
			level = debugLevel_threadinfo;
		} else if (logModule == HmLogConst.M_PARAMETER) {
			level = debugLevel_parameter;
		} else if (logModule == HmLogConst.M_SGE) {
			level = debugLevel_sge;
		} else if (logModule == HmLogConst.M_RESTORE) {
			level = debugLevel_restore;
		} else if (logModule == HmLogConst.M_SHOWSHELL) {
			level = debugLevel_showshell;
		} else if (logModule == HmLogConst.M_TRACER) {
			level = debugLevel_tracer;
		} else if (logModule == HmLogConst.M_LOCATION) {
			level = debugLevel_location;
		}
		return level;
	}

	/**
	 * debug message output target common.log with default level
	 */
	public void commonDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_common) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_COMMON, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target common.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void commonDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_common) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_COMMON, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#commonDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void commonDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_common) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_COMMON, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target fault.log with default level
	 */
	public void faultDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_fault) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_FAULT, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target fault.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void faultDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_fault) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_FAULT, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#faultDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void faultDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_fault) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_FAULT, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target config.log with default level
	 */
	public void configDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_config) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_CONFIG, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target config.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void configDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_config) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_CONFIG, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#configDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void configDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_config) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_CONFIG, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target topo.log with default level
	 */
	public void topoDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_topo) {
			return;
		}

		debugQueue.add(new BeDebugInfo(HmLogConst.M_TOPO, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target topo.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void topoDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_topo) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_TOPO, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#topoDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void topoDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_topo) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_TOPO, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target admin.log with default level
	 */
	public void adminDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_admin) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_ADMIN, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target admin.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void adminDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_admin) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_ADMIN, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#adminDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void adminDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_admin) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_ADMIN, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target performance.log with default level
	 */
	public void performanceDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_performance) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PERFORMANCE, DebugConstant.DEFAULTLEVEL,
				debugInfo));
	}

	/**
	 * debug message output target performance.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void performanceDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_performance) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PERFORMANCE, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#performanceDebug(int, java.lang.String,
	 *      java.lang.Throwable)
	 */
	public void performanceDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_performance) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PERFORMANCE, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target license.log with default level
	 */
	public void licenseDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_license) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_LICENSE, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target license.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void licenseDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_license) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_LICENSE, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#licenseDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void licenseDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_license) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_LICENSE, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target parameter.log with default level
	 */
	public void parameterDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_parameter) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PARAMETER, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target parameter.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void parameterDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_parameter) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PARAMETER, debugLevel, debugInfo));
	}

	/**
	 * @see com.ah.be.debug.BeDebugModule#parameterDebug(int, java.lang.String, java.lang.Throwable)
	 */
	public void parameterDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_parameter) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_PARAMETER, debugLevel, debugInfo, e));
	}

	/**
	 * debug message output target sge.log with default level
	 */
	public void sgeDebug(String debugInfo) {
		if (DebugConstant.DEFAULTLEVEL < debugLevel_common) {
			return;
		}

		debugQueue.add(new BeDebugInfo(HmLogConst.M_SGE, DebugConstant.DEFAULTLEVEL, debugInfo));
	}

	/**
	 * debug message output target sge.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 */
	public void sgeDebug(int debugLevel, String debugInfo) {
		if (debugLevel < debugLevel_common) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_SGE, debugLevel, debugInfo));
	}

	/**
	 * debug message output target sge.log
	 * 
	 * @param debugLevel
	 *            , definition see DebugConstant.java
	 * @param e
	 *            , exception object
	 */
	public void sgeDebug(int debugLevel, String debugInfo, Throwable e) {
		if (debugLevel < debugLevel_common) {
			return;
		}

		addIntoQueue(new BeDebugInfo(HmLogConst.M_SGE, debugLevel, debugInfo, e));
	}

	// -----
	public int getDebugLevel_admin() {
		return debugLevel_admin;
	}

	public void setDebugLevel_admin(int debugLevel_admin) {
		this.debugLevel_admin = debugLevel_admin;
	}

	public int getDebugLevel_common() {
		return debugLevel_common;
	}

	public void setDebugLevel_common(int debugLevel_common) {
		this.debugLevel_common = debugLevel_common;
	}

	public int getDebugLevel_config() {
		return debugLevel_config;
	}

	public void setDebugLevel_config(int debugLevel_config) {
		this.debugLevel_config = debugLevel_config;
	}

	public int getDebugLevel_fault() {
		return debugLevel_fault;
	}

	public void setDebugLevel_fault(int debugLevel_fault) {
		this.debugLevel_fault = debugLevel_fault;
	}

	public int getDebugLevel_license() {
		return debugLevel_license;
	}

	public void setDebugLevel_license(int debugLevel_license) {
		this.debugLevel_license = debugLevel_license;
	}

	public int getDebugLevel_parameter() {
		return debugLevel_parameter;
	}

	public void setDebugLevel_parameter(int debugLevel_parameter) {
		this.debugLevel_parameter = debugLevel_parameter;
	}

	public int getDebugLevel_performance() {
		return debugLevel_performance;
	}

	public void setDebugLevel_performance(int debugLevel_performance) {
		this.debugLevel_performance = debugLevel_performance;
	}

	public int getDebugLevel_topo() {
		return debugLevel_topo;
	}

	public void setDebugLevel_topo(int debugLevel_topo) {
		this.debugLevel_topo = debugLevel_topo;
	}

	public int getDebugLevel_sge() {
		return debugLevel_sge;
	}

	public void setDebugLevel_sge(int debugLevel_sge) {
		this.debugLevel_sge = debugLevel_sge;
	}

	public int getDebugLevel_restore() {
		return debugLevel_restore;
	}

	public void setDebugLevel_restore(int debugLevel_restore) {
		this.debugLevel_restore = debugLevel_restore;
	}

	public int getDebugLevel_showshell() {
		return debugLevel_showshell;
	}

	public void setDebugLevel_showshell(int debugLevel_showshell) {
		this.debugLevel_showshell = debugLevel_showshell;
	}

	public int getDebugLevel_tracer() {
		return debugLevel_tracer;
	}

	public void setDebugLevel_tracer(int debugLevel_tracer) {
		this.debugLevel_tracer = debugLevel_tracer;
	}

	public int getDebugLevel_location() {
		return debugLevel_location;
	}

	public void setDebugLevel_location(int debugLevel_location) {
		this.debugLevel_location = debugLevel_location;
	}

	public void setDebugLevel_All(int debugLevel) {
		debugLevel_common = debugLevel;
		debugLevel_fault = debugLevel;
		debugLevel_topo = debugLevel;
		debugLevel_admin = debugLevel;
		debugLevel_config = debugLevel;
		debugLevel_performance = debugLevel;
		debugLevel_threadinfo = debugLevel;
		debugLevel_license = debugLevel;
		debugLevel_parameter = debugLevel;
		debugLevel_sge = debugLevel;
		debugLevel_restore = debugLevel;
		debugLevel_showshell = debugLevel;
		debugLevel_tracer = debugLevel;
		debugLevel_location = debugLevel;
	}

	public String getAdminDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_admin);
	}

	public String getConfigDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_config);
	}

	public String getFaultDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_fault);
	}

	public String getTopoDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_topo);
	}

	public String getLicenseDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_license);
	}

	public String getPerformanceDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_performance);
	}

	public String getParameterDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_parameter);
	}

	public String getCommonDebugLevelStr() {
		return DebugConstant.debugLevelInt2Str(debugLevel_common);
	}

	public void shutdownDebug() {
		// stop thread
		commonDebug("Debug module start disposing.");
		isContinue = false;
		BeDebugInfo stopThreadEvent = new BeDebugInfo();
		addIntoQueue(stopThreadEvent);

		if (serverHandle != null) {
			serverHandle.stopHandle();
		}

		this.debugProcessThread.interrupt();

		isRunning = false;
	}

}