package com.ah.be.config.create.source.impl;

import java.util.List;

import com.ah.be.config.create.source.ConfigProfileInt;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class ConfigProfileImpl implements ConfigProfileInt {
	
	private HiveAp hiveAp;
	
	public ConfigProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public boolean isConfigRollbackEnable(){
		List<CapwapSettings> capwapSettings = MgrUtil.getQueryEntity().executeQuery(CapwapSettings.class, null,
				new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));

		if(capwapSettings == null || capwapSettings.isEmpty()){
			return false;
		}
		return capwapSettings.get(0).isEnableRollback();
	}
}

