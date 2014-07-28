package com.ah.test;

import org.apache.log4j.Logger;

import com.ah.be.sync.VhmUserSync;

public class VhmUserSyncTest {
	private static Logger log = Logger.getLogger("commonlog.VhmUserSyncTest");
	
	public void test_1() {
		log.info("test_1..start");
		VhmUserSync.syncAllUsersAfterRestoreFromWholeHm34();
	}

	public static void main(String[] args) {
		VhmUserSyncTest vt = new VhmUserSyncTest();
		
		vt.test_1();
	}
}
