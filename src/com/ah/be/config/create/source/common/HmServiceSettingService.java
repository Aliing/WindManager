package com.ah.be.config.create.source.common;

import java.util.List;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmStartConfig;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class HmServiceSettingService {
	
	public HMServicesSettings getServiceSetting(long ownerId) {
		HMServicesSettings settings = null;
		List<HMServicesSettings> list = MgrUtil.getQueryEntity().executeQuery(
				HMServicesSettings.class, null, null, ownerId, new ConfigLazyQueryBo());
		if(list != null && !list.isEmpty()){
			settings = list.get(0);
		}
		return settings;
	}
	
	public HmStartConfig getHmStartConfig(long ownerId) {
		HmStartConfig config = null;
		List<HmStartConfig> list = MgrUtil.getQueryEntity().executeQuery(
				HmStartConfig.class, null, null, ownerId, new ConfigLazyQueryBo());
		if (list != null && !list.isEmpty()){
			config = list.get(0);
		}
		else {
			config = new HmStartConfig();
		}
		return config;
	}
	
}
