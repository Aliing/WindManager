package com.ah.test;

import java.util.List;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class ConfigAutoTest {
	
	public static void main (String[] args){
		List<HiveAp> testDevices = QueryUtil.executeQuery(HiveAp.class, null, null);
		
		boolean uploadConfig = true;
		boolean uploadImage = false;
		boolean isRebooting = false;
		String imageName = null;
		String imageVer = null;
		
		for(HiveAp hiveAp : testDevices){
			hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId(), new ConfigLazyQueryBo());
			ProvisionProcessor.process(hiveAp, isRebooting, uploadConfig, uploadImage, imageName, imageVer, hiveAp.getCountryCode(), UpdateParameters.COMPLETE_SCRIPT);
		}
	}

}
