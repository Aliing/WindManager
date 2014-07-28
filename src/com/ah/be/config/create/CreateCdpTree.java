package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.CdpProfileInt;
import com.ah.xml.be.config.CdpObj;

public class CreateCdpTree {
	
	private CdpProfileInt cdpProfileService;
	
	private CdpObj cdpObj;
	
	private GenerateXMLDebug oDebug;

	public CreateCdpTree(CdpProfileInt cdpProfileService, GenerateXMLDebug oDebug) throws Exception{
		this.cdpProfileService = cdpProfileService;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(cdpProfileService.isConfigCdp()){
			cdpObj = new CdpObj();
			genereateLldpLevel_1();
		}
	}
	
	public CdpObj getCdpObj(){
		return this.cdpObj;
	}
	
	private void genereateLldpLevel_1() throws Exception{
		
		/** element: <cdp>.<enable> */
		cdpObj.setEnable(CLICommonFunc.getAhOnlyAct(cdpProfileService.isEnableCdp()));
		
		if (cdpProfileService.isOverrideConfig()) {
			/** element: <cdp>.<max-entries> */
			cdpObj.setMaxEntries(CLICommonFunc.createAhIntActObj(
					cdpProfileService.getCdpMaxEntries(), CLICommonFunc.getYesDefault()));
		}

	}
}
