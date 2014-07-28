package com.ah.test;

import org.apache.log4j.Logger;

import com.ah.be.ls.stat.StatCenter;
import com.ah.util.HmBeManager;

public class StatCenterTest {
	private static Logger log = Logger.getLogger("commonlog.StatCenterTest");

	public void test_1() {
		log.info("test_1");
		StatCenter.reportApUsageStat();
	}

	public static void main(String[] args) {
		StatCenterTest dmt = new StatCenterTest();
		System.out.println("test..start");
		try {
			HmBeManager be = new HmBeManager();

			be.startBe();

			dmt.test_1();

			be.stopBe();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test..end!");
	}
}
