package com.ah.test;

import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.BeConfigModuleImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class ConfigTest {

	public static void main(String[] args) throws Exception {
		BeConfigModuleImpl configImpl = new BeConfigModuleImpl();
		Long apId=(long)2132;
		HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, apId);
		String clis = configImpl.viewConfig(hiveAp, ConfigType.AP_FULL);
		System.out.print(clis);
	}
}
