package com.ah.apiengine.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HmOperation {

	private static final Log	log	= LogFactory.getLog("tracerlog.HmOperation");

	/**
	 * backup VHM
	 * 
	 * @param cmd -
	 * @sample: backupvhm -n vhm1
	 */
	public String backupVhm(String cmd) {
		log.info("command=" + cmd);
		return "ok";
	}

	/**
	 * restore VHM
	 * 
	 * @param cmd -
	 * @sample: restorevhm -n vhm1 -uf aaa.img
	 */
	public String restoreVhm(String cmd) {
		log.info("command=" + cmd);
		return "ok";
	}

	/**
	 * query command execute progresss
	 * 
	 * @param cmd -
	 * @sample queryprog -s 10678, queryprog -c
	 * @return -
	 */
	public String queryCommandProgress(String cmd) {
		log.info("command=" + cmd);
		return "ok";
	}

}