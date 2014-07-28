package com.ah.test;

import org.apache.log4j.Logger;

import com.ah.be.admin.restoredb.RestoreEventAndAlarm;

public class RestoreTest {
	private static Logger log = Logger.getLogger("RestoreTest");
	public void test1() {
		RestoreEventAndAlarm aa = new RestoreEventAndAlarm();
		aa.restoreEvent();
	}
	
	public void test2() {
		RestoreEventAndAlarm aa = new RestoreEventAndAlarm();
		aa.restoreAlarm();
	}

	public static void main(String[] args) {
		RestoreTest rt = new RestoreTest();
		
		log.info("test start...");
		rt.test2();
		log.info("test end!");
	}
}
