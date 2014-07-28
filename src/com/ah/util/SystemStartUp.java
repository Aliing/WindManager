package com.ah.util;

import java.io.File;
import java.security.Security;

import org.apache.log4j.PropertyConfigurator;

public class SystemStartUp {

	private static boolean logConfigLoaded = false;

	public synchronized static void readLogConfig() {
		if (logConfigLoaded) {
			return;
		}

		String webRoot = System.getenv("HM_ROOT");

		System.setProperty("catalina.home", System.getenv("CATALINA_HOME"));
		System.setProperty("hm.log.home", webRoot + File.separator + "WEB-INF" + File.separator
				+ "logs");

		String configFilename = webRoot + File.separator + "WEB-INF" + File.separator + "logconf"
				+ File.separator + "hivelog.properties";

		System.out.println("Loading log4j configuration - " + configFilename);
		//PropertyConfigurator.configure(configFilename);
		PropertyConfigurator.configureAndWatch(configFilename, 30000);
		System.out.println("Log4j configuration loaded.");

		Security.setProperty("networkaddress.cache.ttl", "3");
		Security.setProperty("networkaddress.cache.negative.ttl", "3");

		logConfigLoaded = true;
	}

	public synchronized static boolean isLogConfigLoaded() {
		return logConfigLoaded;
	}

}