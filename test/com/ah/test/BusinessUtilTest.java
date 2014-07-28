package com.ah.test;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.communication.BusinessUtil;
import com.ah.be.communication.mo.UserInfo;

public class BusinessUtilTest {
	private static Log log = LogFactory.getLog("commonlog.BusinessUtilTest");

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java BusinessUtilTest no {the no is 1 or 2}");
			return;
		}
		int no = Integer.valueOf(args[0]);
		System.out.println("test for no:" + no);

		BusinessUtilTest objtest = new BusinessUtilTest();
		System.out.println("test..start");
		try {
			switch (no) {
			case 1:
				objtest.test_1();
				break;
			case 2:
				objtest.test_2();
				break;
			default:
				System.out.println("do nothing!");
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test..end!");
	}

	private void test_2() {
		List<UserInfo> users = BusinessUtil.queryVhmUsers("vhm_avanda1");
		log.info("users.size=" + users.size());
	}

	private void test_1() {
		// TODO Auto-generated method stub

	}
}
