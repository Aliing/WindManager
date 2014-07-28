package com.ah.be.config.create.common.postprocess;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;

import com.ah.be.config.create.common.CLICommonUtils;
import com.ah.bo.hiveap.HiveAp;

public class ParseCLIPostProcess extends AbstractCLIPostProcess {
	
	private String ignoreCLIPath;
	
	public ParseCLIPostProcess(HiveAp hiveAp, Document document, String ignoreCLIPath){
		this.hiveAp = hiveAp;
		this.document = document;
		this.ignoreCLIPath = ignoreCLIPath;
	}

	@Override
	public void init() {
		if(!StringUtils.isEmpty(ignoreCLIPath)){
			this.cliList = CLICommonUtils.readCLIList(ignoreCLIPath);
		}
		this.processOrder = PostCLIParse;
	}

}
