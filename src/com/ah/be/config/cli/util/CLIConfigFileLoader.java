package com.ah.be.config.cli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.config.cli.xsdbean.CliGenType;
import com.ah.be.config.cli.xsdbean.CliParseType;
import com.ah.be.config.cli.xsdbean.Clis;
import com.ah.be.config.cli.xsdbean.ConstraintType;
import com.ah.be.config.cli.xsdbean.DefaultValueType;
import com.ah.be.config.cli.xsdbean.ParamType;
import com.ah.be.parameter.device.DevicePropertyManage;

public class CLIConfigFileLoader {

	private static CLIConfigFileLoader instance = null;
	
	private Clis cliConfig;
	
	private Map<String, String> allExpressionMap;
	
	public static CLIConfigFileLoader getInstance(){
		synchronized(CLIConfigFileLoader.class){
			if(instance == null){
				instance = new CLIConfigFileLoader();
				instance.init();
			}
			return instance;
		}
	}
	
	private CLIConfigFileLoader(){
		
	}
	
	private void init(){
		cliConfig = getAllCLIConfig();
		
		//init constraints params.
		allExpressionMap = initConstraintsDefine(cliConfig);
	}
	
	public Clis getCLIConfig(){
		return this.cliConfig;
	}
	
	public String getConstraintExpression(String consName){
		return this.allExpressionMap.get(consName);
	}
	
	//load all CLI generate and CLI parse configure file.
	private Clis getAllCLIConfig(){
		Clis cliConfig = null;
		try {
			JAXBContext jc = JAXBContext.newInstance("com.ah.be.config.cli.xsdbean");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			//load constraints.xml
			InputStream inStream = new FileInputStream(CLIConfigFileUtil.getCLIConstraintsPath());
			cliConfig = (Clis) unmarshaller.unmarshal(inStream);
			
			//load all xml in fold cli_gen
			File[] allGenXmls = CLIConfigFileUtil.getAllXmlFile(CLIConfigFileUtil.getCLIGenPath());
			File[] allParseXmls = CLIConfigFileUtil.getAllXmlFile(CLIConfigFileUtil.getCLIParsePath());
			File[] allXmls = new File[allGenXmls.length + allParseXmls.length];
			System.arraycopy(allGenXmls, 0, allXmls, 0, allGenXmls.length);
			System.arraycopy(allParseXmls, 0, allXmls, allGenXmls.length, allParseXmls.length);
			
			Clis subConfig = null;
			for(File xml : allXmls){
				inStream = new FileInputStream(xml);
				subConfig = (Clis) unmarshaller.unmarshal(inStream);
				if(subConfig.getCliGen() != null){
					cliConfig.getCliGen().addAll(subConfig.getCliGen());
				}
				if(subConfig.getCliParse() != null){
					cliConfig.getCliParse().addAll(subConfig.getCliParse());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cliConfig;
	}
	
	private Map<String, String> initConstraintsDefine(Clis cliConfig){
		if(cliConfig == null){
			return null;
		}
		
		Map<String, String> allExpressionMap = new HashMap<>();
		List<ConstraintType> allConstraints = new ArrayList<ConstraintType>();
		
		//init all definition params
		if(cliConfig.getParams() != null && cliConfig.getParams().getParam() != null){
			for(ParamType paramObj : cliConfig.getParams().getParam()){
				if(!StringUtils.isEmpty(paramObj.getDeviceProperty())) {
					paramObj.setContent(DevicePropertyManage.getInstance().getSupportDeviceRegex(paramObj.getDeviceProperty()));
				}
				allExpressionMap.put(paramObj.getName(), paramObj.getContent());
			}
		}
		
		//constraint from /clis/params/constraints.
		if(cliConfig.getParams() != null && cliConfig.getParams().getConstraints() != null){
			allConstraints.addAll(cliConfig.getParams().getConstraints());
		}
		//constraint from /clis/cli-gen/constraints.
		//constraint from /clis/cli-gen/cli-default/constraints.
		if(cliConfig.getCliGen() != null){
			for(CliGenType genType : cliConfig.getCliGen()){
				if(genType.getConstraints() != null){
					allConstraints.add(genType.getConstraints());
				}
				if(genType.getCliDefault() != null){
					for(DefaultValueType defType : genType.getCliDefault()){
						if(defType.getConstraints() != null){
							allConstraints.add(defType.getConstraints());
						}
					}
				}
			}
		}
		//constraint from /clis/cli-parse/cli-default/constraints.
		if(cliConfig.getCliParse() != null){
			for(CliParseType parseType : cliConfig.getCliParse()){
				if(parseType.getConstraints() != null){
					allConstraints.add(parseType.getConstraints());
				}
			}
		}
		
		//get platform, type, version from params
		for(ConstraintType consType : allConstraints){
			if(allExpressionMap.containsKey(consType.getPlatform())){
				consType.setPlatform(allExpressionMap.get(consType.getPlatform()));
			}
			if(allExpressionMap.containsKey(consType.getType())){
				consType.setType(allExpressionMap.get(consType.getType()));
			}
			if(allExpressionMap.containsKey(consType.getVersion())){
				consType.setVersion(allExpressionMap.get(consType.getVersion()));
			}
		}
		
		//generate expression for all constraint
		String consKey = null, consExpression = null;
		for(ConstraintType consType : allConstraints){
			if(!StringUtils.isEmpty(consType.getNameRef())){
				//reference another Constraint
				continue;
			}
			consExpression = ConstraintCheckUtil.getExpression(consType);
			consKey = consType.getName() != null ? consType.getName() : UUID.randomUUID().toString();
			allExpressionMap.put(consKey, consExpression);
			consType.setName(consKey);
		}
		
		allExpressionMap = CLIConfigFileUtil.processReferenceRelation(allExpressionMap);
		if(cliConfig.getParams() != null && cliConfig.getParams().getParam() != null){
			for(ParamType paramObj : cliConfig.getParams().getParam()){
				paramObj.setContent(allExpressionMap.get(paramObj.getName()));
			}
		}
		for(ConstraintType consType : allConstraints){
			if(!StringUtils.isEmpty(consType.getNameRef())){
				consType.setExpression(allExpressionMap.get(consType.getNameRef()));
			}else{
				consType.setExpression(allExpressionMap.get(consType.getName()));
			}
			consType.setPlatform(null);
			consType.setType(null);
			consType.setVersion(null);
		}
		
		return allExpressionMap;
	}
}
