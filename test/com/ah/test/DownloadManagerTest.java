package com.ah.test;

import java.util.HashMap;
import java.util.Map;

import com.ah.be.ls.DownloadManager;
import com.ah.util.HmBeManager;

public class DownloadManagerTest {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java DownloadManagerTest no {the no is 1 or 2}");
			return;
		}
		int no = Integer.valueOf(args[0]);
		System.out.println("test for no:" + no);

		DownloadManagerTest dmt = new DownloadManagerTest();
		System.out.println("test..start");
		try {
			HmBeManager be = new HmBeManager();

			be.startBe();

			switch (no) {
			case 1:
				dmt.test_1();
				break;
			case 2:
				dmt.test_2();
				break;
			default:
				System.out.println("do nothing!");
				break;
			}

			be.stopBe();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test..end!");
	}

	private void test_1() throws Exception {
		DownloadManager.downloadHMSoftware("3.5r1", 1001);
	}

	private void test_2() throws Exception {
		Map<String, Integer> hulist = new HashMap<String, Integer>();

		hulist.put("hiveap20", 1);

		DownloadManager.downloadHiveApSoftware(hulist);
	}
}
