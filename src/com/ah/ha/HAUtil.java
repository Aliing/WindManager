/**
 * @filename			HAMonitorUtil.java
 * @version				1.0
 * @author				Administrator
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import com.ah.ha.alert.HAAlertor;
import com.ah.ha.impl.HAMonitorImpl;
import com.ah.util.Tracer;

public class HAUtil {

	private static final Tracer log = new Tracer(HAUtil.class.getSimpleName());
	
	public static final String HA_NODE_STATUS_PATH = "/HiveManager/ha/conf/ha_node_status";

	private static final HAAlertor haAlertor = HAAlertor.getInstance();

	private static final HAMonitor haMonitor = HAMonitorImpl.getInstance();

	public static HAAlertor getHaAlertor() {
		return haAlertor;
	}

	public static HAMonitor getHAMonitor() {
		return haMonitor;
	}
	
	/**
	 * Check if in HA Slave status.
	 *
	 * @return true, if it's slave; false, if it's master or standalone
	 * @author Joseph Chen
	 */
	public static boolean isSlave() {
		HAMonitor haMonitor = getHAMonitor();
		HAStatus currentStatus = haMonitor.getCurrentStatus();

		return currentStatus.getStatus() == HAStatus.STATUS_HA_SLAVE;
	}
	
	/**
	 * Query HA status from HA heartbeat.
	 * 
	 * @return -
	 * @throws HAException -
	 * @author Joseph Chen
	 */
	public static HAStatus queryHAStatus() throws HAException {
		String haStatusQueryScriptPath = "/HiveManager/ha/scripts/query_node_status.sh";
		String[] cmdArray = new String[] { "bash", "-c", haStatusQueryScriptPath };
		String queryResult = null;
		Process process = null;
		BufferedReader reader = null;
		Runtime runtime = Runtime.getRuntime();
		log.info("queryHAStatus", "Querying HA status.");

		try {
			process = runtime.exec(cmdArray);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			if (reader.ready()) {
				queryResult = reader.readLine();
			}
		} catch (Exception e) {
			throw new HAException("Error occurred while querying HA status.", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("queryHAStatus", "IO Close Error.", e);
				}
			}

			if (process != null) {
				process.destroy();
			}
		}

		log.info("queryHAStatus", "Query result: " + queryResult);

		if (queryResult == null) {
			throw new HAException("Nothing returned from HA heartbeat.");
		}

		try {
			/*
			 * In general, the HA status number should be 0/1/2/3
			 */
			int status = Integer.parseInt(queryResult.trim());
			return new HAStatus(status);
		} catch (NumberFormatException nfe) {
			throw new HAException("Invalid query result: " + queryResult, nfe);
		}
	}

	public static HAStatus queryHANodeStatus() throws HAException {
		File haNodeStatus = new File(HA_NODE_STATUS_PATH);

		if (!haNodeStatus.exists()) {
			log.warn("queryHANodeStatus", "The " + HA_NODE_STATUS_PATH + " did not exist, therefore, returning 'Standalone' by default.");
			return new HAStatus(HAStatus.STATUS_STAND_ALONG);
		}

		if (!haNodeStatus.isFile()) {
			log.warn("queryHANodeStatus", "The " + HA_NODE_STATUS_PATH + " was not a file, therefore, returning 'Standalone' by default.");
			return new HAStatus(HAStatus.STATUS_STAND_ALONG);
		}

		FileInputStream fis = null;
		BufferedReader bf = null;
		String strStatus = null;
		
		try {
			fis = new FileInputStream(haNodeStatus);
			bf = new BufferedReader(new InputStreamReader(fis));
			strStatus = bf.readLine();
		} catch (IOException ioe) {
			throw new HAException ("IO Error", ioe);
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException ioe) {
					log.error("queryHANodeStatus", "IO Close Error.", ioe);
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					log.error("queryHANodeStatus", "IO Close Error.", ioe);
				}
			}
		}

		if (strStatus == null) {
			throw new HAException("Nothing read from " + HA_NODE_STATUS_PATH);
		}

		try {
			int statusNum = Integer.parseInt(strStatus.trim());

			switch (statusNum) {
				case HAStatus.STATUS_STAND_ALONG:
				case HAStatus.STATUS_HA_MASTER:
				case HAStatus.STATUS_HA_SLAVE:
					log.info("queryHANodeStatus", "HA node status: " + statusNum);
					return new HAStatus(statusNum);
				default:
					throw new HAException("Invalid status number - " + statusNum);
			}
		} catch (NumberFormatException nfe) {
			throw new HAException ("Incorrect content [" + strStatus + "] read from " + HA_NODE_STATUS_PATH, nfe);
		}
	}

}