package com.ah.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.ls.ClientSenderCenter;
import com.ah.util.HmBeManager;

public class ClientSenderCenterTest {
	private static Log log = LogFactory.getLog("commonlog.ClientSenderCenterTest");

	public static void main(String[] args) {
		ClientSenderCenterTest test = new ClientSenderCenterTest();

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

		boolean result = ClientSenderCenter.checkVmhmValidation(
				"9829-D539-A4DB-9260-91E4-C9B5-34E9-99BE", "Pb0mB-3OTUA-4ix7s-a13Nm-hPufm-6nn0w");

		log.info("result=" + result);
		log.info("test_1..end");
	}

}
