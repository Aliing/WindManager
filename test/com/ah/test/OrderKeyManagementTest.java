package com.ah.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.OrderKeyManagement;
import com.ah.bo.admin.HmDomain;

public class OrderKeyManagementTest {
	private static Log	log	= LogFactory.getLog("commonlog.OrderKeyManagementTest");

	public static void main(String[] args) {
		OrderKeyManagementTest ot = new OrderKeyManagementTest();

		System.out.println("test..start");
		ot.test1();
		System.out.println("test..end!");
		
		System.out.println("test2..start");
		ot.test2();
		System.out.println("test2..end!");
	}

	private void test1() {
		log.info("test1..start");
		//OrderKeyManagement.checkValidityOfVmhm();
		log.info("test1..end!");
	}

	private void test2() {
		log.info("test2..start");

		String orderKey = "oQlGk-UXMWe-68v43-i5Xdd-tPCiZ-gTd9J";
		//HmDomain domain = getHomeDomain();
		String hmId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
		try {
			OrderKeyManagement.activateOrderKey(orderKey, HmDomain.HOME_DOMAIN, hmId);
		} catch (Exception e) {
			log.error("test2 failed! " + e.getMessage());
		}
		log.info("test2..end!");
	}

}
