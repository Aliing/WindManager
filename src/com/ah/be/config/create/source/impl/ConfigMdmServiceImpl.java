package com.ah.be.config.create.source.impl;

import java.util.List;

import com.ah.be.config.create.source.ConfigMdmService;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class ConfigMdmServiceImpl implements ConfigMdmService{
	
	private HiveAp hiveAp;
	
	public ConfigMdmServiceImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public ConfigMdmServiceImpl(){}
	
	public ConfigTemplateMdm getConfigTemplateMdmByDomain(Long domainId){
		List<ConfigTemplateMdm> configData = MgrUtil.getQueryEntity().executeQuery(ConfigTemplateMdm.class, null, null, domainId);
		if(configData.isEmpty())
		{
			
			return null;
		}
		else 
		{
			return configData.get(0);
		}
		
	}
	public ConfigTemplateMdm getConfigTemplateMdmById(Long id){
		return 	MgrUtil.getQueryEntity().findBoById(ConfigTemplateMdm.class, id);
	}

}
