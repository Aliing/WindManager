package com.ah.test;

import org.apache.log4j.Logger;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class HmTestUtil {
	public static Logger	logf	= Logger.getLogger("file");

	public static HiveAp getHiveApByMac(String macAddress) {
		HiveAp ap = (HiveAp) QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", macAddress);
		
		return ap;
	}
}
