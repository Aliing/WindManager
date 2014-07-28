package com.ah.be.config.create.common.postprocess;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.jfree.util.Log;

import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;

public class GenConfigCLIPostProcess extends AbstractCLIPostProcess {
	
	private ConfigType configType;

	public GenConfigCLIPostProcess(ConfigureProfileFunction profileFunction, String sourceXmlPath, ConfigType configType){
		this.profileFunction = profileFunction;
		this.hiveAp = profileFunction.getHiveAp();
		this.sourceXmlPath = sourceXmlPath;
		this.configType = configType;
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
		
		if(configType == ConfigType.AP_FULL){
			this.processOrder = PostConfigApFull;
		}else if(configType == ConfigType.USER_FULL){
			this.processOrder = PostConfigUserFull;
		}
	}
	
//	@Override
//	public void process(){
//		super.process();
////		appendCannotParseClis();
//	}
	
//	private void appendCannotParseClis(){
//		if(configType != ConfigType.AP_FULL){
//			return;
//		}else if(this.cliList == null || this.cliList.isEmpty()){
//			return;
//		}
//		
//		for(String cli : this.cliList){
//			Element crEle = this.document.getRootElement().addElement(GenerateXML.ELEMENT_EMPTY_OPERATE);
//			crEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, cli);
//			crEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_OPERATION, "yesWithShow");
//			crEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_QUOTEPROHIBITED, "yes");
//		}
//	}
}
