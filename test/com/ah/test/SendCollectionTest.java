package com.ah.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.activation.ActivationKeyOperation;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.ls.stat.StatCenter;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.util.HmBeManager;

public class SendCollectionTest {

	private static Log log = LogFactory.getLog("commonlog.ClientSenderCenterTest");

	public static void main(String[] args) {
		SendCollectionTest test = new SendCollectionTest();

		System.out.println("test..start");
		try {
			HmBeManager be = new HmBeManager();

			be.startBe();

			test.test_1();

			be.stopBe();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test..end");
	}

	private void test_1() throws Exception {
		log.info("test_1..start");

		LicenseServerSetting lsSet = HmBeActivationUtil.getLicenseServerInfo();

		try {
			if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID
					&& HmBeActivationUtil.ACTIVATION_KEY_VALID) {
				// get new version flag
				ActivationKeyOperation.getNewVersionFlag(lsSet);
			}

			/*
			 * send collection information to license server
			 */
			// HiveManager and HiveAP information
			ActivationKeyOperation.sendCollectionInfo();

			// active client mac information
			ActivationKeyOperation.sendClientMacInfo();

			// report file information
			StatCenter.reportApUsageStat();
		} catch (Exception ex) {
			log.error("run_x failed!", ex);
		}

		log.info("test_1..end");
	}

}