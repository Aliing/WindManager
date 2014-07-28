package com.ah.be.config.create.source;

import com.ah.bo.hiveap.ConfigTemplateMdm;

public interface ConfigMdmService {
	public ConfigTemplateMdm getConfigTemplateMdmByDomain(Long domainId);
	public ConfigTemplateMdm getConfigTemplateMdmById(Long Id);
	
}
