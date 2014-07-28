package com.ah.test;

import org.apache.log4j.Logger;

import com.ah.be.app.AhAppContainer;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ha.be.BeAppManager;

public class HmBeTest {

	private static final Logger	log = Logger.getLogger("HmBeTest");

	public boolean startBe() {
		log.info("start be...");

		HAMonitor haMonitor = HAUtil.getHAMonitor();
		haMonitor.addListener(new BeAppManager());
		HAStatus currentStatus = null;

		try {
			currentStatus = HAUtil.queryHANodeStatus();
		} catch (Exception e) {
			log.error("startBE" + "Error in getting current ha status.", e);
		}

		/*
		 * if cannot get ha status, let it be stand-alone
		 */
		if (currentStatus == null) {
			currentStatus = new HAStatus(HAStatus.STATUS_STAND_ALONG);
		}

		haMonitor.changeStatus(currentStatus);
		try {
			haMonitor.start();	
		} catch (Exception e) {
			log.error("HA monitor start error.", e);
		}

		log.info("be has started!");
		return true;
	}

	public boolean stopBe() {
		log.info("stop be...");
		if (AhAppContainer.HmBe != null) {
			log.info("contextDestroyed" + "Stopping BE Services");
			AhAppContainer.HmBe.stopApplication();
		}

		HAMonitor haMonitor = HAUtil.getHAMonitor();
		try {
			haMonitor.stop();	
		} catch (Exception e) {
			log.error("HA monitor stop error.", e);
		}
		
		log.info("be has stopped!");

		return true;
	}
	
	public static void main(String[] args){
		HmBeTest hbt = new HmBeTest();
		
		log.info("test start...");
		hbt.startBe();
		log.info("be started.");
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("be stopping...");
		hbt.stopBe();
		log.info("test end!");
	}

}