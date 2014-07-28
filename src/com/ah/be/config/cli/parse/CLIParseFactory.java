package com.ah.be.config.cli.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.ah.be.config.cli.merge.XmlMergeCache;
import com.ah.be.config.cli.merge.XmlOrderTool;
import com.ah.be.config.cli.util.CLIConfigFileLoader;
import com.ah.be.config.cli.util.CLIConfigFileUtil;
import com.ah.be.config.cli.util.CLIGenUtil;
import com.ah.be.config.cli.util.CmdRegexUtil;
import com.ah.be.config.cli.xsdbean.CliParseType;
import com.ah.be.config.cli.xsdbean.Clis;
import com.ah.be.config.cli.xsdbean.ConstraintType;

public class CLIParseFactory {

	private static CLIParseFactory instance;
	private Map<String, List<CLIParseInstance>> cliParseMap;
	private Map<String, Map<String, String>> schemaAttrMap;
	
	private static final int PARSE_COUNTS_ONE_TIME = 80000;

	private CLIParseFactory() {
	}

	public static CLIParseFactory getInstance() {
		synchronized(CLIParseFactory.class){
			if (instance == null) {
				instance = new CLIParseFactory();
				instance.init();
			}
			return instance;
		}
	}

	private void init() {
		initCLIParseMap();
		initSchemaXml();
	}
	
	public XmlMergeCache parseCli(String[] cliArgs, ConstraintType... constraintArg){
		XmlMergeCache xmlCache = new XmlMergeCache();
		xmlCache.init();
		parseCli(cliArgs, xmlCache, constraintArg);
		return xmlCache;
	}
	
	public List<String> parseCli(String[] cliArgs, XmlMergeCache xmlCache, ConstraintType... constraintArg){
		String[] cldArgs;
		List<String> cannotParseClis = new ArrayList<>();
		int startIndex = 0, endIndex = cliArgs.length > startIndex + PARSE_COUNTS_ONE_TIME? startIndex + PARSE_COUNTS_ONE_TIME : cliArgs.length;
		while(endIndex > startIndex){
			cldArgs = new String[endIndex - startIndex];
			System.arraycopy(cliArgs, startIndex, cldArgs, 0, endIndex - startIndex);
			cannotParseClis.addAll(parseCliMultiple(cldArgs, xmlCache, constraintArg));
			
			startIndex = endIndex;
			endIndex = cliArgs.length > startIndex + PARSE_COUNTS_ONE_TIME? startIndex + PARSE_COUNTS_ONE_TIME : cliArgs.length;
			System.out.println("------------------------------------------------");
		}
		
		return cannotParseClis;
	}
	
	private List<String> parseCliMultiple(String[] cliArgs, XmlMergeCache xmlCache, ConstraintType... constraintArg){
		List<CLIParseResult> resList = new ArrayList<>();
		List<String> cannotParseClis = new ArrayList<>();
		
		CLIParseResult parseRes = null;
		for(String cliStr : cliArgs){
			parseRes = getParseResult(cliStr, constraintArg);
			if(parseRes != null){
				resList.add(parseRes);
			}else{
				cannotParseClis.add(cliStr);
			}
		}
		
		if(resList.isEmpty()){
			return cannotParseClis;
		}
		
		//merge all string xml.
		StringBuffer allXmlStr = new StringBuffer("<root>");
		for(CLIParseResult parseObj : resList){
			allXmlStr.append(parseObj.getXmlStr());
		}
		allXmlStr.append("</root>");
		
		Element allXmlNode = CLIGenUtil.converStringToXml(allXmlStr.toString());
		if(allXmlNode == null){
			return cannotParseClis;
		}
		
		List<?> allEleList = allXmlNode.elements();
		Element childEle = null;
		for(int index=0; index<allEleList.size(); index++){
			childEle = (Element)allEleList.get(index);
//			childEle.setParent(null);
			childEle = childEle.createCopy();
			parseRes = resList.get(index);
			
			//merge xml node attribute from schema.xml
			mergeNodeAttribute(childEle, parseRes);
			xmlCache.mergeElement(childEle);
		}
		
		return cannotParseClis;
	}

	public CLIParseResult parseCliSingle(String cliStr, ConstraintType... constraintArg) {
		CLIParseResult parseRes = getParseResult(cliStr, constraintArg);
		
		if(parseRes == null){
			return null;
		}
		
		//string to xml
		Element xmlNode = null;
		if(parseRes.getXmlStr() != null){
			xmlNode = CLIGenUtil.converStringToXml(parseRes.getXmlStr());
		}
		if(xmlNode == null){
			return parseRes;
		}
		
		//merge xml node attribute from schema.xml
		mergeNodeAttribute(xmlNode, parseRes);
		
		//set xml node
		parseRes.setXmlNode(xmlNode);

		return parseRes;
	}
	
	private void initCLIParseMap() {
		//load cli parse config file.
		Clis parseConfig = CLIConfigFileLoader.getInstance().getCLIConfig();
		if(parseConfig == null || parseConfig.getCliParse() == null){
			return;
		}
		
		//init all parse instance.
		cliParseMap = new HashMap<String, List<CLIParseInstance>>();
		String key = null;
		String xmlStr = null;
		for(CliParseType parseCfg : parseConfig.getCliParse()){
			xmlStr = parseCfg.getXml();
			if(xmlStr != null){
				xmlStr = xmlStr.replaceAll("\\s*<", "<").replaceAll(">\\s*", ">");
			}
			CLIParseInstance parseIns = new CLIParseInstance(parseCfg.getCmd(), xmlStr, parseCfg.getConstraints());
			if(parseCfg.getGroupPostProcess() != null){
				parseIns.setPostProcess(parseCfg.getGroupPostProcess().getClazz(), parseCfg.getGroupPostProcess().getMethod());
			}
			parseIns.init();
			key = parseIns.getFirstKeyWord();
			if (cliParseMap.get(key) == null){
				cliParseMap.put(key, new ArrayList<CLIParseInstance>());
			}
			cliParseMap.get(key).add(parseIns);
		}
	}

	//load schema.xml
	private void initSchemaXml() {
		String path = CLIConfigFileUtil.getCLITemplatePath();
		Document destDoc = CLIConfigFileUtil.readXml(path);
		if (destDoc == null) {
			return;
		} else {
			schemaAttrMap = new HashMap<String, Map<String, String>>();
			treeWalkElement(destDoc.getRootElement(), schemaAttrMap);
			return;
		}
	}

	private void treeWalkElement(Element element, Map<String, Map<String, String>> schemaAttrMap) {
		//create linked hash map store attribute.
		LinkedHashMap<String, String> attrMap = new LinkedHashMap<>();
		schemaAttrMap.put(XmlOrderTool.getPath(element, false), attrMap);
		
		//Iterator node attribute to map.
		Iterator<?> attrIte = element.attributeIterator();
		while(attrIte.hasNext()){
			Attribute attrObj = (Attribute)attrIte.next();
			attrMap.put(attrObj.getName(), attrObj.getValue());
		}
		
		Iterator<?> childElements = element.elementIterator();
		if (childElements != null){
			while(childElements.hasNext()){
				treeWalkElement( (Element) childElements.next(), schemaAttrMap);
			}
		}
	}
	
	private CLIParseResult getParseResult(String cliStr, ConstraintType... constraintArg){
		if (cliStr == null){
			return null;
		}
		
		String firstKeyWord = CmdRegexUtil.getCmdGroupKey(cliStr);
		CLIParseResult parseRes = null;
		
		//CLI match, first key word may be "no".
		List<CLIParseInstance> relations = cliParseMap.get(firstKeyWord);
		if(relations != null){
			for(CLIParseInstance parseIns : relations){
				parseRes = parseIns.parse(cliStr, constraintArg);
				if(parseRes != null && parseRes.isMatche()){
					return parseRes;
				}
			}
		}
		
		//CLI match, first key word without "no".
		if("no".equalsIgnoreCase(firstKeyWord)){
			firstKeyWord = CmdRegexUtil.getCmdGroupKey(cliStr, true);
			relations = cliParseMap.get(firstKeyWord);
			if(relations != null){
				for(CLIParseInstance parseIns : relations){
					parseRes = parseIns.parse(cliStr, constraintArg);
					if(parseRes != null && parseRes.isMatche()){
						return parseRes;
					}
				}
			}
		}
		
		return null;
	}

	private void mergeNodeAttribute(Element element, CLIParseResult parseRes) {
		//get Element path.
		String xPath = XmlOrderTool.getPath(element, false);
		
		Map<String, String> standardAttrMap = schemaAttrMap.get(xPath);
		if (standardAttrMap == null){
			System.err.println("Cannot find xml node by xpath \"" + xPath + "\".");
		}
		
		//load mapping result attribute to map.
		Map<String, String> attrMap = new HashMap<String, String>();
		Iterator<?> attrIte = element.attributeIterator();
		if (attrIte != null) {
			Attribute attribute;
			while(attrIte.hasNext()){
				attribute = (Attribute) attrIte.next();
				attrMap.put(attribute.getName(), attribute.getValue());
			}
		}
		
		//if current element invalid, remove it.
		if (!isElementValid(attrMap, standardAttrMap, element.getName())) {
			element.getParent().remove(element);
			return;
		}
		
		//whether current element is the lastest element.
		List<?> childsList = element.elements();
		boolean isLatestNode = childsList == null || childsList.isEmpty();
		boolean isEncryptAttr = "1".equals(standardAttrMap.get(CLIGenUtil.ATTR_ENCRYPTED));
		
		//recreate attribute according standard XML template.
		Iterator<Entry<String, String>> standardAttrItems = standardAttrMap.entrySet().iterator();
		if (standardAttrItems != null) {
			//clear old attribute
			element.attributes().clear();
			
			String attrName, attrValue;
			boolean existNoCmdAttr = attrMap.containsKey(CLIGenUtil.ATTR_NOCMD);
			Entry<String, String> attrEntry;
			while(standardAttrItems.hasNext()){
				attrEntry = standardAttrItems.next();
				attrName = attrEntry.getKey();
				attrValue = attrEntry.getValue();
				
				if(parseRes.isNoCmdManual() && parseRes.isNoCmd() && existNoCmdAttr &&
						CLIGenUtil.ATTR_OPERATION.equals(attrName) ){
					//use attribute "noCmd" mapping no command.
					attrValue = CLIGenUtil.getNoOperation(attrValue);
				}else if(!parseRes.isNoCmdManual() && isLatestNode && parseRes.isNoCmd() && 
						CLIGenUtil.ATTR_OPERATION.equals(attrName)){
					//if no command, set latest node "operation" attribute to "noXX".
					attrValue = CLIGenUtil.getNoOperation(attrValue);
				}else if(attrMap.containsKey(attrName)){
					//use parse attribute value.
					attrValue = attrMap.get(attrName);
				}
				
				if(isEncryptAttr && 
						(CLIGenUtil.ATTR_VALUE.equals(attrName) || CLIGenUtil.ATTR_NAME.equals(attrName))){
					attrValue = CLIGenUtil.decryptPassword(attrValue);
				}
				
				element.addAttribute(attrName, attrValue);
			}
		}
		
		//tree walk child element.
		for(Object cldNode : childsList){
			mergeNodeAttribute((Element)cldNode, parseRes);
		}
		
		//after iterator all child elements be removed, so current jump node is invalid.
		if(!isLatestNode && 
				standardAttrMap.containsKey(CLIGenUtil.ATTR_NULL_VALUE_VALID) && 
				("".equals(attrMap.get(CLIGenUtil.ATTR_VALUE)) || "".equals(attrMap.get(CLIGenUtil.ATTR_NAME))) && 
				element.elements().isEmpty() ){
			element.getParent().remove(element);
		}
		
		attrMap.clear();
		attrMap = null;
	}

	private boolean isElementValid(Map<String, String> attrMap, Map<String, String> standAttrMap, String eleName) {
		if (attrMap == null || attrMap.isEmpty()){
			return true;
		}
		
		String attrName, attrValue;
		Entry<String, String> mapEntry;
		Iterator<Entry<String, String>> items = attrMap.entrySet().iterator();
		while(items.hasNext()){
			mapEntry = items.next();
			attrName = (String)mapEntry.getKey();
			attrValue = (String)mapEntry.getValue();
			
			if(CLIGenUtil.ATTR_EXIST.equalsIgnoreCase(attrName)){
				if("".equals(attrValue)){
					return false;
				}
			}if(CLIGenUtil.ATTR_NOT_EXIST.equalsIgnoreCase(attrName)){
				if(!"".equals(attrValue)){
					return false;
				}
			}else if(CLIGenUtil.ATTR_KEYWORD.equalsIgnoreCase(attrName)){
				attrValue = attrValue.trim();
				if(!attrValue.equalsIgnoreCase(eleName) && !attrValue.equalsIgnoreCase(standAttrMap.get(CLIGenUtil.ATTR_CLI_NAME)) ){
					return false;
				}
			}else if(StringUtils.isEmpty(attrValue) && 
						(attrName.equals(CLIGenUtil.ATTR_NAME) || attrName.equals(CLIGenUtil.ATTR_VALUE)) && 
						!standAttrMap.containsKey(CLIGenUtil.ATTR_NULL_VALUE_VALID) ){
				return false;
			}
		}
		return true;
	}

}