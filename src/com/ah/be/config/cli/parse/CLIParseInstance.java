package com.ah.be.config.cli.parse;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.cli.brackets.CLIBracketUtil;
import com.ah.be.config.cli.util.CLIGenUtil;
import com.ah.be.config.cli.util.CmdRegexUtil;
import com.ah.be.config.cli.util.ConstraintCheckUtil;
import com.ah.be.config.cli.xsdbean.ConstraintType;

public class CLIParseInstance {
	
	private Pattern pattern;

	private String cmd;
	
	private String xml;
	
	private String regex;
	
	private String firstKeyWord;
	
	private boolean noCmdManual;
	
	private ConstraintType constraint;
	
	private int[] macOuiGroupIds;
	
	private Class<?> postProcessClass;
	
	private Method postProcessMethod;
	
	public CLIParseInstance(){}
	
	public CLIParseInstance(String cmd, String xml, ConstraintType... constraintArg){
		this.cmd = cmd;
		this.xml = xml;
		this.constraint = constraintArg.length > 0 ? constraintArg[0] : null;
	}
	
	public void init(){
		if(cmd != null){
			cmd = cmd.trim();
			regex = CmdRegexUtil.generate(cmd);
			firstKeyWord = CmdRegexUtil.getCmdGroupKey(cmd);
			pattern = Pattern.compile(regex);
		}
		if(xml != null){
			xml = xml.trim();
			noCmdManual = xml.contains(CLIGenUtil.ATTR_NOCMD);
		}
		macOuiGroupIds = CLIBracketUtil.getMacOuiGroupIds(this.cmd);
	}
	
	public CLIParseResult parse(String cli, ConstraintType... constraintArg){
		ConstraintType curConstraint = constraintArg.length > 0 ? constraintArg[0] : null;
		if(StringUtils.isEmpty(cli) || !ConstraintCheckUtil.isMatch(curConstraint, constraint)){
			return null;
		}
		
		String parseCli = cli;
		
		//judge whether no cmd.
		boolean noCmd = CLIGenUtil.isNoCLI(parseCli);
		if(noCmd){
			parseCli = CLIGenUtil.getNormalCLI(parseCli);
		}
		
		//judge whether matches.
		Matcher matcher = pattern.matcher(parseCli);
		boolean match = matcher.matches();
		if(!match){
			return null;
		}
		
		CLIParseResult parseResult = new CLIParseResult();
		//set matche
		parseResult.setMatche(match);
		//set is no cmd
		parseResult.setNoCmd(noCmd);
		//set whether use "noCmd" mapping no command
		parseResult.setNoCmdManual(this.noCmdManual);
		//set regex
		parseResult.setRegex(this.regex);
		//set cli and cli define
		parseResult.setCli(cli);
		parseResult.setCmd(this.cmd);
		//set params group
		String[][] paramGroup = getParamsGroup(matcher);
		paramGroup = specialHandleParamGroup(paramGroup);
		parseResult.setGroup(paramGroup);
		
		//set xml
		if(xml != null){
			String xmlCopy = xml;
			//params mapping
			String key, value;
			for(int index=0; index<paramGroup.length; index++){
				value = paramGroup[index][1];
				if(value == null){
					value = "";
				}
				key = "{"+paramGroup[index][0]+"}";
				xmlCopy = xmlCopy.replace(key, value);
			}
			parseResult.setXmlStr(xmlCopy);
		}
		
		return parseResult;
	}
	
	private String[][] specialHandleParamGroup(String[][] paramGroup){
		if(paramGroup == null || paramGroup.length == 0){
			return paramGroup;
		}
		
		//format mac_addr or oui
		for(int macIndex : macOuiGroupIds){
			paramGroup[macIndex][1] = CmdRegexUtil.formatMacOui(paramGroup[macIndex][1]);
		}
		
		if(postProcessClass == null || postProcessMethod == null){
			return paramGroup;
		}
		
		//use java reflect handle param group
		try{
			paramGroup = (String[][])postProcessMethod.invoke(null, new Object[]{paramGroup});
		}catch(Throwable t){
			t.printStackTrace();
			throw new RuntimeException(t);
		}
		
		return paramGroup;
	}
	
	private String[][] getParamsGroup(Matcher matcher){
		int groupCount = matcher.groupCount();
		String[][] groupArg = new String[groupCount+1][2];
		String groupStr;
		for(int i=0; i<=groupCount; i++){
			groupStr = matcher.group(i);
			groupStr = CLIGenUtil.removeQuoteTwoSide(groupStr);
			groupArg[i][0] = String.valueOf(i);
			groupArg[i][1] = groupStr;
		}
		
		return groupArg;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getFirstKeyWord() {
		return firstKeyWord;
	}

	public void setFirstKeyWord(String firstKeyWord) {
		this.firstKeyWord = firstKeyWord;
	}

	public boolean isNoCmdManual() {
		return noCmdManual;
	}

	public void setNoCmdManual(boolean noCmdManual) {
		this.noCmdManual = noCmdManual;
	}

	public void setPostProcess(String className, String method) {
		try{
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			this.postProcessClass = clazz;
			this.postProcessMethod = clazz.getDeclaredMethod(method, String[][].class);
		}catch(Throwable t){
			System.err.println("Init "+className+" instance failed.");
			t.printStackTrace();
		}
	}
	
}
