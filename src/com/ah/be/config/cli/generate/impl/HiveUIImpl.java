package com.ah.be.config.cli.generate.impl;

import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;

public class HiveUIImpl extends AbstractAutoAdaptiveCLIGenerate {

	@Override
	public void init() throws CLIGenerateException {
		// TODO Auto-generated method stub
		
	}
	
	@CLIConfig(HIVEUI_WAN_CFG_REDIRECT_PAGE_ENABLE)
	public CLIGenResult isEnableWanRedirect(){
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(new Object[]{hiveAp.getConfigTemplate().isEnabledWanConfiguration()});
		return cliRes;
	}
}
