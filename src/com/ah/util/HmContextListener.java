package com.ah.util;

/*
 * Initialize Hibernate at startup
 *
 * @author Chris Scheers
 */

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;

//import com.ah.be.app.AhAppContainer;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.HmSystemInfoUtil;
import com.ah.be.snmp.SnmpAgent;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.tca.HmTCAMonitorModule;
import com.ah.ha.HAException;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ha.be.BeAppManager;
import com.ah.ha.cas.CasSettingsUpdater;
import com.ah.ui.actions.Navigation;
import com.ericdaugherty.soht.server.SocketProxyServlet;
import com.ericdaugherty.sshwebproxy.SshConnectionFactory;

public final class HmContextListener implements ServletContextListener {

	private static final Tracer log = new Tracer(HmContextListener.class.getSimpleName());

	public static ServletContext context;

	private ScheduledExecutorService memoryMonitorScheduler;
	
	private LinuxSystemInfoCollector linuxSystemInfoCollector;

	public void contextInitialized(ServletContextEvent event) {
		log.info("contextInitialized", "Starting HM...");

		if (!precheck()) {
			log.error("contextInitialized", "HM web application precheck failed! Exit!");
			System.exit(1);
		}
		
	//	SystemStartUp.readLogConfig();

		printPreferences();

		context = event.getServletContext();

		log.info("contextInitialized", "Initializing Servlet context: " + context.getServerInfo());

	//	HibernateUtil.init();

		// Initialize navigation
		Navigation.loadXmlNavigationTree(context.getRealPath("/"));

		// Initialize access control module
		BoMgmt.getAccessControl().init();

		// Initialize HiveAP management component
		BoMgmt.getHiveApMgmt().init();

		// Initialize Map management component
		BoMgmt.getMapMgmt().init();

		// Start BO Event Notification thread
		BoMgmt.getBoEventMgmt().start();
		
		// Initialize memory DB
		DBOperationUtil.init();	
		
		// Start BE
		startBE();

		memoryMonitorScheduler = MgrUtil.getMemoryMonitorScheduler(60, 60, TimeUnit.SECONDS);
		
		String os = System.getProperty("os.name");

		if (!os.toLowerCase().contains("windows")) {
			linuxSystemInfoCollector = LinuxSystemInfoCollector.getInstance();
			linuxSystemInfoCollector.start();
		}

		//send snmp trap
		File file = new File(SnmpAgent.NORMAL_CLOSE);
		if(file.exists()) {
			SnmpAgent.sendWarmStartTrap();
			file.delete();
		} else {
			SnmpAgent.sendColdStartTrap();
		}
		
		HmTCAMonitorModule.getInstance().startMonitor();
		
		log.info("contextInitialized", "HM started.");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("contextDestroyed", "Stopping HM...");
		
		// Stop BO Event Notification thread
		BoMgmt.getBoEventMgmt().stop();

		// Shutdown Map management component
		BoMgmt.getMapMgmt().destroy();

		// Shutdown HiveAP management component
		BoMgmt.getHiveApMgmt().destroy();

		// Shutdown access control module
		BoMgmt.getAccessControl().destroy();

		// Close all SSH connections
		SshConnectionFactory.getInstance().closeAllSshConnections();

		// Close all proxy connections
		SocketProxyServlet.getConnectionManager().removeAllConnections();

		// Stop BE
		stopBE();

	//	try {
	//		Thread.sleep(6000);
	//	} catch (InterruptedException ie) {
	//		log.error("contextDestroyed", "Thread sleep failed");
	//	}

	//	AhAppContainer.HmBe.stopDebug();

	//	log.info("contextDestroyed", "Closing Hibernate...");

	//	HibernateUtil.closeEntityManagerFactory();

	//	HibernateUtil.closeSessionFactory();

	//	log.info("contextDestroyed", "Hibernate closed.");

		DBOperationUtil.uninit();

		if (memoryMonitorScheduler != null && !memoryMonitorScheduler.isShutdown()) {
			memoryMonitorScheduler.shutdown();
		}

		if (linuxSystemInfoCollector != null) {
			try {
				linuxSystemInfoCollector.shutdown();
			} catch (Exception e) {
				log.error("contextDestroyed", "Linux system information collector shutdown error.");
			}
		}

		log.info("contextDestroyed", "Deregistering JDBC driver...");
		deregisterJDBCDriver();
		log.info("contextDestroyed", "JDBC driver deregistered.");
		
		//create file for snmp server
		File file = new File(SnmpAgent.NORMAL_CLOSE);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		
		log.info("contextDestroyed", "HM stopped. Up Time: " + HmSystemInfoUtil.getJvmUptime() + "ms.");
		
		HmTCAMonitorModule.getInstance().stopMonitor();

		// Shutdown logger
		LogManager.shutdown();
	}

	private void startBE() {
		/*
		 * from 3.3, HM supports HA. and under HA model, BE's running depends on the HA status. if
		 * local machine is Master or stand-alone, then run BE. else BE will not be run
		 */
		// Boolean beAppEnabled = Boolean.parseBoolean(System.getProperty(
		// "hm.beAppEnabled", "true"));
		// log.info("startBE", "beAppEnabled ? " + beAppEnabled);
		// if (beAppEnabled) {
		// log.info("startBE", "Starting BE Services");
		// AhAppContainer.HmBe = new HmBeApp();
		// AhAppContainer.HmBe.createInstances();
		// }
		HAMonitor haMonitor = HAUtil.getHAMonitor();
		haMonitor.addListener(new BeAppManager());
		haMonitor.addListener(new CasSettingsUpdater());
		HAStatus initStatus;

		try {
			initStatus = HAUtil.queryHANodeStatus();
		} catch (Exception e) {
			log.error("startBE", "HA node status query error. Will start as a standalone HM.", e);
			initStatus = new HAStatus(HAStatus.STATUS_STAND_ALONG);
		}

		/*
		 * if cannot get ha status, let it be stand-alone
		 */
//		if (initStatus == null) {
//			initStatus = new HAStatus(HAStatus.STATUS_STAND_ALONG);
//		}

		haMonitor.changeStatus(initStatus);

		// Start HA monitor.
		try {
			haMonitor.start();
		} catch (HAException e) {
			log.error("startBE", "HA monitor start error.", e);
		}
	}

	private void stopBE() {
//		if (AhAppContainer.HmBe != null) {
//			log.info("stopBE", "Stopping BE.");
//			AhAppContainer.HmBe.stopApplication();
//		}

		HAStatus termStatus = new HAStatus(HAStatus.STATUS_SHUT_DOWN);
		HAMonitor haMonitor = HAUtil.getHAMonitor();

		// Stop BE
		haMonitor.changeStatus(termStatus);

		// Stop HA monitor
		try {
			haMonitor.stop();
		} catch (HAException e) {
			log.error("stopBE", "HA monitor stop error.", e);
		}
	}

	private void printPreferences() {
		// System Preferences
		String osInfoStart = "\n***** System Preferences Start *****";
		String osName = "\n\tOS Name: " + HmSystemInfoUtil.getOsName();
		String osVer = "\n\tOS Version: " + HmSystemInfoUtil.getOsVersion();
		String osArch = "\n\tArchitecture: " + HmSystemInfoUtil.getOsArch();
		String processorInfo = "\n\tProcessors Available: "
				+ HmSystemInfoUtil.getAvailableProcessors();
		String osInfoEnd = "\n***** System Preferences End *****\n";

		// JVM Preferences
		String jvmInfoStart = "\n***** JVM Preferences Start *****";
		String jvmName = "\n\tName: " + HmSystemInfoUtil.getJvmName();
		String runJvmName = "\n\tRunning JVM Name: " + HmSystemInfoUtil.getRunningJvmName();
		String vmVer = "\n\tVersion: " + HmSystemInfoUtil.getJvmVersion();
		String classPath = "\n\tClass Path: " + HmSystemInfoUtil.getClassPath();
		String bootClassPath = "\n\tBoot Class Path: " + HmSystemInfoUtil.getBootClassPath();
		String libPath = "\n\tLibrary Path: " + HmSystemInfoUtil.getLibraryPath();
		String jvmStartTime = "\n\tStart Time: "
				+ new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date(HmSystemInfoUtil
						.getJvmStartTime()));
		String jvmInfoEnd = "\n***** JVM Preferences End *****\n";

		String preferBuf = osInfoStart + osName + osVer + osArch + processorInfo + osInfoEnd + jvmInfoStart + jvmName + runJvmName + vmVer + classPath + bootClassPath + libPath + jvmStartTime + jvmInfoEnd;
		log.info("printPreferences", preferBuf);
	}

	private boolean precheck() {
		if (System.getenv("CATALINA_HOME") == null) {
			log.error("precheck", "ENV CATALINA_HOME must be set!");
			return false;
		}

		if (System.getenv("HM_ROOT") == null) {
			log.error("precheck", "ENV HM_ROOT must be set!");
			return false;
		}

		return true;
	}
	
	private void deregisterJDBCDriver() {
		for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
			Driver driver = drivers.nextElement();

			if (driver instanceof org.hsqldb.jdbc.JDBCDriver || driver instanceof org.postgresql.Driver) {
				log.info("deregisterJDBCDriver", String.format("Deregistering JDBC driver: %s", driver));

				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					log.error("deregisterJDBCDriver", String.format("Error deregistering JDBC driver %s", driver), e);
				}
			}
		}
	}

}