/**
 *@filename		HmBeApp.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2 03:50:32 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import java.util.Iterator;
import java.util.LinkedList;

import com.ah.be.activation.BeActivationModule;
import com.ah.be.activation.BeActivationModuleImpl;
import com.ah.be.admin.BeAdminModule;
import com.ah.be.admin.BeAdminModuleImpl;
import com.ah.be.cloudauth.BeIDMModule;
import com.ah.be.cloudauth.BeIDMModuleImpl;
import com.ah.be.communication.BeCommunicationModule;
import com.ah.be.communication.BeCommunicationModuleImpl;
import com.ah.be.config.BeConfigModule;
import com.ah.be.config.BeConfigModuleImpl;
import com.ah.be.db.BeDbModule;
import com.ah.be.db.BeDbModuleImpl;
import com.ah.be.debug.BeDebugModule;
import com.ah.be.debug.BeDebugModuleImpl;
import com.ah.be.event.BeEventListener;
import com.ah.be.event.BeEventModule;
import com.ah.be.event.BeEventModuleImpl;
import com.ah.be.fault.BeFaultModule;
import com.ah.be.fault.BeFaultModuleImpl;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.BeLicenseModuleCentOsImpl;
import com.ah.be.license.BeLicenseModuleWinOsImpl;
import com.ah.be.location.BeLocationModule;
import com.ah.be.location.BeLocationModuleImpl;
import com.ah.be.log.BeLogModule;
import com.ah.be.log.BeLogModuleStandImpl;
import com.ah.be.misc.BeMiscModule;
import com.ah.be.misc.BeMiscModuleImpl;
import com.ah.be.os.BeOSLayerModuleImpl;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.BeParaModuleDefImpl;
import com.ah.be.performance.BePerformModule;
import com.ah.be.performance.BePerformModuleImpl;
import com.ah.be.resource.BeResModule;
import com.ah.be.resource.BeResModuleImpl;
import com.ah.be.topo.BeTopoModule;
import com.ah.be.topo.BeTopoModuleImpl;
import com.ah.be.ts.TsModule;
import com.ah.be.ts.TsModuleImpl;
import com.ah.be.watchdog.WatchDog;
import com.ah.be.watchdog.WatchDogImpl;
import com.ah.be.watchdog.WatchDogListener;
import com.ah.util.Tracer;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeApp extends BaseModule implements AhApp {

	private static final Tracer log	= new Tracer(HmBeApp.class.getSimpleName());

	/**
	 *
	 */
	private WatchDog module_WatchDog;

	/**
	 *
	 */
	private BeParaModule module_Para;

	/**
	 *
	 */
	private BeAdminModule module_Admin;

	/**
	 *
	 */
	private BeConfigModule module_Config;

	/**
	 *
	 */
	private BeDbModule module_Db;

	/**
	 *
	 */
	private BeFaultModule module_Fault;

	/**
	 *
	 */
	private BeLicenseModule module_License;

	/**
	 *
	 */
	private BeActivationModule module_Activation;

	/**
	 *
	 */
	private BeLogModule module_Log;

	/**
	 * 
	 */
	private BeDebugModule module_Debug;

	/**
	 *
	 */
	private BeOsLayerModule module_Os;

	/**
	 *
	 */
	private BePerformModule module_Perform;

	/**
	 *
	 */
	private BeResModule module_Res;

	/**
	 *
	 */
	private BeTopoModule module_Topo;

	/**
	 *
	 */
	private BeEventModule module_Event;

	/**
	 *
	 */
	private BeCommunicationModule module_Communication;

	/**
	 * Location tracking
	 */
	private BeLocationModule module_Location;

	/**
	 *
	 */
	private TsModule module_Ts;

	/**
	 * The Module could be used to do miscellaneous tasks and things which are not easy to be
	 * classified into other modules.
	 */
	private BeMiscModule module_Misc;
	
    /**
     * For IDM
     */
    private BeIDMModule module_IDM;	

	/**
	 *
	 */
	private final LinkedList<BaseModule> moduleQueue = new LinkedList<BaseModule>();

	private ServerStatus BeServerStatus;

	public HmBeApp() {
		setModuleId(BaseModule.ModuleID_BeApp);
		setModuleName("HmBeApp");
		getDebuger().setModuleId(BaseModule.ModuleID_BeApp);
		getDebuger().setModuleName("HmBeApp");
	}
	
	/**
	 * create parts of the module instances and events for HA Slave
	 */
	public synchronized void createInstancesForHAPassiveMode() {
		module_Debug = BeDebugModuleImpl.getInstance();
		((BaseModule) module_Debug).init();
		((BaseModule) module_Debug).runModule();
		if (((BaseModule) module_Debug).getModuleStatus().getModuleStatus() == ModuleStatus.Module_Error) {
			log.error("createInstancesForHAPassiveMode", "Starting debug module error.");
		} else {
			log.info("createInstancesForHAPassiveMode", "Debug module started.");
		}
		module_Communication = new BeCommunicationModuleImpl();
		module_WatchDog = new WatchDogImpl();
		module_Event = new BeEventModuleImpl();
		module_Res = new BeResModuleImpl();

		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			module_License = new BeLicenseModuleWinOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_WINDOWS);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_WINDOWS);
		} else if (os.toLowerCase().contains("linux")) {
			module_License = new BeLicenseModuleCentOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_LINUX);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_LINUX);
		}

		moduleQueue.add((BaseModule) module_Debug);
		moduleQueue.add((BaseModule) module_Event);
		moduleQueue.add((BaseModule) module_Res);
		moduleQueue.add((BaseModule) module_Os);
		moduleQueue.add((BaseModule) module_License);
		moduleQueue.add((BaseModule) module_Admin);
		moduleQueue.add((BaseModule) module_Communication);
		
		log.info("createInstancesForHAPassiveMode", "Initiating HmBeApp in HA passive mode...");
		init();
		log.info("createInstancesForHAPassiveMode", "HmBeApp initiated.");

		log.info("createInstancesForHAPassiveMode", "Running HmBeApp...");
		run();
		log.info("createInstancesForHAPassiveMode", "HmBeApp is running.");
	}
	/**
	 * create parts of the module instances and events for test
	 */
	public synchronized void createTestInstances() {
		module_Debug = BeDebugModuleImpl.getInstance();
		((BaseModule) module_Debug).init();
		((BaseModule) module_Debug).runModule();
		if (((BaseModule) module_Debug).getModuleStatus().getModuleStatus() == ModuleStatus.Module_Error) {
			log.error("createTestInstances", "Starting debug module error.");
		} else {
			log.info("createTestInstances", "Debug module started.");
		}

		module_WatchDog = new WatchDogImpl();
		module_Event = new BeEventModuleImpl();
		module_Para = new BeParaModuleDefImpl();
		module_Res = new BeResModuleImpl();

		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			module_License = new BeLicenseModuleWinOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_WINDOWS);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_WINDOWS);
		} else if (os.toLowerCase().contains("linux")) {
			module_License = new BeLicenseModuleCentOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_LINUX);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_LINUX);
		}

		moduleQueue.add((BaseModule) module_Debug);
		moduleQueue.add((BaseModule) module_Event);
		moduleQueue.add((BaseModule) module_Para);
		moduleQueue.add((BaseModule) module_Res);
		moduleQueue.add((BaseModule) module_Os);
		moduleQueue.add((BaseModule) module_License);
		moduleQueue.add((BaseModule) module_Admin);
		moduleQueue.add((BaseModule) module_WatchDog);

		log.info("createTestInstances", "Initiating HmBeApp...");
		init();
		log.info("createTestInstances", "HmBeApp initiated.");

		log.info("createTestInstances", "Running HmBeApp...");
		run();
		log.info("createTestInstances", "HmBeApp is running.");
	}

	/**
	 * create all the module instances and events
	 */
	public synchronized void createInstances() {
		// run debug module at first.
		module_Debug = BeDebugModuleImpl.getInstance();
		((BaseModule) module_Debug).init();
		((BaseModule) module_Debug).runModule();
		if (((BaseModule) module_Debug).getModuleStatus().getModuleStatus() == ModuleStatus.Module_Error) {
			log.error("createInstances", "Starting debug module error.");
		} else {
			log.info("createInstances", "Debug module started.");
		}

		/*
		 * create all module instances
		 */
		module_Communication = new BeCommunicationModuleImpl();
		module_Location = new BeLocationModuleImpl();
		module_Config = new BeConfigModuleImpl();
		module_Db = new BeDbModuleImpl();
		module_Fault = new BeFaultModuleImpl();
		module_Para = new BeParaModuleDefImpl();
		module_Perform = new BePerformModuleImpl();
		module_Res = new BeResModuleImpl();
		module_Topo = new BeTopoModuleImpl();
		module_Ts = new TsModuleImpl();
		module_WatchDog = new WatchDogImpl();
		module_Event = new BeEventModuleImpl();
		module_Log = new BeLogModuleStandImpl();
		module_Activation = new BeActivationModuleImpl();
		module_Misc = new BeMiscModuleImpl();
		module_IDM = new BeIDMModuleImpl();

		String os = System.getProperty("os.name");

		if (os.toLowerCase().contains("windows")) {
			module_License = new BeLicenseModuleWinOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_WINDOWS);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_WINDOWS);
		} else if (os.toLowerCase().contains("linux")) {
			module_License = new BeLicenseModuleCentOsImpl();
			module_Os = new BeOSLayerModuleImpl(BeOsLayerModule.OS_LINUX);
			module_Admin = new BeAdminModuleImpl(BeOsLayerModule.OS_LINUX);
		}

		/*
		 * add all modules to queue
		 */
		moduleQueue.add((BaseModule) module_Debug);
		moduleQueue.add((BaseModule) module_Log);
		moduleQueue.add((BaseModule) module_Event);
		moduleQueue.add((BaseModule) module_Res);
		moduleQueue.add((BaseModule) module_Os);
		moduleQueue.add((BaseModule) module_Topo);

		// this module relates to os and topo module
		moduleQueue.add((BaseModule) module_License);

		// this module relates to os and license module
		moduleQueue.add((BaseModule) module_Activation);

		// this module relates to license module
		moduleQueue.add((BaseModule) module_Para);

		// this module relates to license module
		moduleQueue.add((BaseModule) module_Db);

		moduleQueue.add((BaseModule) module_Admin);
		moduleQueue.add((BaseModule) module_Fault);
		moduleQueue.add((BaseModule) module_Perform);
		moduleQueue.add((BaseModule) module_Config);
		moduleQueue.add((BaseModule) module_Ts);
		moduleQueue.add((BaseModule) module_WatchDog);
		moduleQueue.add((BaseModule) module_Location);
		moduleQueue.add((BaseModule) module_Misc);
		
		moduleQueue.add((BaseModule)module_IDM);

		// modify follow by chenyang's suggestion
		moduleQueue.add((BaseModule) module_Communication);

		log.info("createInstances", "Initiating HmBeApp...");
		init();
		log.info("createInstances", "HmBeApp initiated.");

		log.info("createInstances", "Running HmBeApp...");
		run();
		log.info("createInstances", "HmBeApp is running.");
	}

	/**
	 * @see com.ah.be.app.BaseModule#init()
	 */
	@Override
	public boolean init() {
		/*
		 * initialize all modules
		 */
		for (BaseModule module : moduleQueue) {
			try {
				log.info("init", "Initiating module [" + module.getModuleName() + "]...");
				long startTime = System.currentTimeMillis();
				module.initModule();
				long endTime = System.currentTimeMillis();
				log.info("init", "Module [" + module.getModuleName() + "] initiated. Elapsed Time: " + (endTime - startTime) + "ms.");
			} catch (Exception e) {
				log.error("init", "Initiating module [" + module.getModuleName() + "] error.", e);
			}
		}

		/*
		 * register modules to the WatchDog Module for status monitor
		 */
		/*
		 * init module's status listener
		 */
		for (BaseModule module : moduleQueue) {
			if (module.getModuleId() != BaseModule.ModuleID_WatchDog) {
				module_WatchDog.registerModule(module);
				module.setWatchDogListener((WatchDogListener) module_WatchDog);
			}
		}

		/*
		 * register modules to the system event module
		 */

		for (BaseModule regModule : moduleQueue) {
			regModule.setEventListener((BeEventListener) module_Event);
		}

		module_Event.startEventProcesser();

		/*
		 * set EventModule as the BeApp's EventListener
		 */
		setEventListener((BeEventListener) module_Event);

		/*
		 * register all modules to Event module as the BeApp event listener
		 */
		for (BaseModule regModule : moduleQueue) {
			module_Event.registeEventDispatchListener(regModule);
		}

		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run() {
		for (BaseModule module : moduleQueue) {
			try {
				log.info("run", "Running module [" + module.getModuleName() + "]...");
				long startTime = System.currentTimeMillis();
				module.runModule();
				long endTime = System.currentTimeMillis();
				log.info("run", "Module [" + module.getModuleName() + "] is running. Elapsed Time: " + (endTime - startTime) + "ms.");
			} catch (Exception e) {
				log.error("run", "Running module [" + module.getModuleName() + "] error.", e);
			}
		}

		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown() {
		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#restart()
	 */

	@Override
	public boolean restart() {
		return false;
	}

	/**
	 * @see com.ah.be.app.BaseModule#dump()
	 */
	@Override
	public void dump() {

	}

	public boolean getInitStatus() {
		return false;
	}

	/**
	 * @see com.ah.be.app.AhApp#getServiceStatus()
	 */
	public ServerStatus getServiceStatus() {
		return BeServerStatus;
	}

	/**
	 * @return -
	 */
	public WatchDog getWatchDogModule() {
		return module_WatchDog;
	}

	/**
	 * @param arg_Module
	 *            -
	 */
	public void setWatchDogModule(WatchDogImpl arg_Module) {
		module_WatchDog = arg_Module;
	}

	/**
	 * @return -
	 */
	public BeParaModule getBeParaModule() {
		return module_Para;
	}

	/**
	 * @param arg_Module
	 *            -
	 */
	public void setBeParaModule(BeParaModule arg_Module) {
		module_Para = arg_Module;
	}

	/**
	 * return resource module
	 * 
	 * @return -
	 */
	public BeResModule getResModule() {
		return module_Res;
	}

	/**
	 * get refer to perform module
	 * 
	 * @return -
	 */
	public BePerformModule getPerformModule() {
		return module_Perform;
	}

	/**
	 * return os module
	 * 
	 * @return -
	 */
	public BeLogModule getLogModule() {
		return module_Log;
	}

	/**
	 * return ref to debug module
	 * 
	 * @return -
	 */
	public BeDebugModule getDebugModule() {
		return module_Debug;
	}

	/**
	 * return os module
	 * 
	 * @return -
	 */
	public BeOsLayerModule getOsModule() {
		return module_Os;
	}

	/**
	 * return fault_module
	 * 
	 * @return -
	 */
	public BeFaultModule getFaultModule() {
		return module_Fault;
	}

	/**
	 * return license module
	 * 
	 * @return -
	 */
	public BeLicenseModule getLicenseModule() {
		return module_License;
	}

	/**
	 * return activation module
	 * 
	 * @return -
	 */
	public BeActivationModule getActivationModule() {
		return module_Activation;
	}

	/**
	 * @return admin module
	 */
	public BeAdminModule getAdminModule() {
		return module_Admin;
	}

	/**
	 * @return communication module
	 */
	public BeCommunicationModule getCommunicationModule() {
		return module_Communication;
	}

	public BeLocationModule getLocationModule() {
		return module_Location;
	}

	public BeMiscModule getMiscModule() {
		return module_Misc;
	}

	public BeConfigModule getBeConfigModule() {
		return module_Config;
	}

	public BeDbModule getBeDbModule() {
		return module_Db;
	}

	/**
	 * @return be topo module
	 */
	public BeTopoModule getBeTopoModule() {
		return module_Topo;
	}

	public TsModule getBeTsModule() {
		return module_Ts;
	}

	public BeIDMModule getModule_IDM() {
        return module_IDM;
    }

	/**
	 * @see com.ah.be.app.AhApp#stopApplication()
	 */
	public synchronized void stopApplication() {
		/*
		 * receive stop request from client
		 */
		log.info("stopApplication", "Client send shutdown request to BE server.");
		// BeBaseEvent StopEvent = new BeBaseEvent();
		// StopEvent.setModuleId(getModuleId());
		// StopEvent.setEventType(BeEventConst.Be_App_Shutdown_Request);
		// getEventListener().eventGenerated(StopEvent);

		// Instead of generating an event, invoke shutdown method directly.
		// In this way, the shutdown will be apply to modules one by one.
//		for (int i = moduleQueue.size(); i > 0; i--) {
//			moduleQueue.get(i - 1).shutdownModule();
//		}

		for (Iterator<BaseModule> moduleIterator = moduleQueue.descendingIterator(); moduleIterator.hasNext();) {
			BaseModule module = moduleIterator.next();

			try {
				log.info("stopApplication", "Shutting down module [" + module.getModuleName() + "]...");
				long startTime = System.currentTimeMillis();
				module.shutdownModule();
				long endTime = System.currentTimeMillis();
				log.info("stopApplication", "Module [" + module.getModuleName() + "] shutdown. Elapsed Time: " + (endTime - startTime) + "ms.");
			} catch (Exception e) {
				log.error("stopApplication", "Shutting down module [" + module.getModuleName() + "] error.", e);
			}
		}
	}

	public void stopDebug() {
		((BeDebugModuleImpl) module_Debug).shutdownDebug();
	}

	/**
	 * @see com.ah.be.app.AhApp#setServerStatus(com.ah.be.app.ServerStatus)
	 */
	public void setServerStatus(ServerStatus arg_Status) {
		BeServerStatus = arg_Status;
	}

}