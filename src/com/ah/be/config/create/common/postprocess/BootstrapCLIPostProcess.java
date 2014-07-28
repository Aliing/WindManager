package com.ah.be.config.create.common.postprocess;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.jfree.util.Log;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.hiveap.HiveAp;

public class BootstrapCLIPostProcess extends AbstractCLIPostProcess {

	public BootstrapCLIPostProcess(HiveAp hiveAp, String sourceXmlPath){
		this.hiveAp = hiveAp;
		this.sourceXmlPath = sourceXmlPath;
	}
	
	@Override
	public void init() {
		if(!StringUtils.isEmpty(sourceXmlPath)){
			try {
				document = CLICommonFunc.readXml(sourceXmlPath);
			} catch (DocumentException e) {
				Log.error("init()", e);
			}
		}
		
		this.processOrder = PostCLIBootstrap;
	}
}
