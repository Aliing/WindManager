package com.ah.util;

import org.apache.log4j.Logger;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeApp;
import com.ah.be.common.ConfigUtil;

public class HmBeManager {

	private final Logger log = Logger.getLogger("HmBeManager");

	public boolean startBe() {
		log.info("Starting BE...");

		try {
			HibernateUtil.init(false);

			ConfigUtil.setConfigInfo("debug", "tcpPort", "13334");
			AhAppContainer.HmBe = new HmBeApp();
			AhAppContainer.HmBe.createTestInstances();
			ConfigUtil.setConfigInfo("debug", "tcpPort", "13333");
		} catch (Exception e) {
			log.error("startBe failed!", e);
			return false;
		}

		log.info("BE started.");
		return true;
	}

	public boolean stopBe() {
		log.info("Stopping BE...");
		try {
			if (AhAppContainer.HmBe != null) {
				log.info("stopping BE Services");
				AhAppContainer.HmBe.stopApplication();
			}

			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				log.error("Thread sleep failed", e);
			}

			log.info("Closing Hibernate context");

			HibernateUtil.close();

			AhAppContainer.HmBe.stopDebug();
		} catch (Exception e) {
			log.error("stopBe failed!", e);
			return false;
		}
		log.info("BE stopped.");

		return true;
	}

}