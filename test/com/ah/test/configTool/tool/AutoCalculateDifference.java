package com.ah.test.configTool.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.common.GenerateVersionControlFile;
import com.ah.be.config.create.common.XmlOrderTool;
import com.ah.test.configTool.ConfigToolUtil;
import com.ah.test.configTool.tool.ConfigParam.Operation;

public class AutoCalculateDifference {
	
	private ConfigParam configParam;
	
	private Document newDocument;
	
	private Document oldDocument;
	
	private List<AhXmlAttribute> newXmlList;
	private List<AhXmlAttribute> oldXmlList;

	public AutoCalculateDifference(ConfigParam configParam) {
		this.configParam = configParam;
		try{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<ConfigParam> generateDifference(){
		if(newXmlList == null || oldXmlList == null){
			return null;
		}
		
		List<ConfigParam> resList = new ArrayList<ConfigParam>();
		
		Iterator<AhXmlAttribute> newItem = newXmlList.iterator();
		Iterator<AhXmlAttribute> oldItem = oldXmlList.iterator();
		
		//init new map
		Map<String, AhXmlAttribute> newXmlMap = new HashMap<String, AhXmlAttribute>();
		for(AhXmlAttribute attrObj : newXmlList){
			newXmlMap.put(attrObj.getxPath(), attrObj);
		}
		
		//init old map
		Map<String, AhXmlAttribute> oldXmlMap = new HashMap<String, AhXmlAttribute>();
		for(AhXmlAttribute attrObj : oldXmlList){
			oldXmlMap.put(attrObj.getxPath(), attrObj);
		}
		
		//tree walk newXmlMap
		for(;newItem.hasNext();){
			AhXmlAttribute attrObj = newItem.next();
			String xPath = attrObj.getxPath();
			AhXmlAttribute newAhXml = attrObj;
			AhXmlAttribute oldAhXml = oldXmlMap.get(xPath);
			
			ConfigParam param = null;
			if(oldAhXml == null){
				param = this.createConfigParam(Operation.add, attrObj);
			}else if(!newAhXml.equals(oldAhXml)){
				param = this.createConfigParam(Operation.override_attribute, attrObj);
			}
			
			if(param != null){
				resList.add(param);
			}
		}
		
		for(;oldItem.hasNext();){
			AhXmlAttribute attrObj = oldItem.next();
			String xPath = attrObj.getxPath();
			AhXmlAttribute newAhXml = newXmlMap.get(xPath);
			
			if(newAhXml == null){
				ConfigParam param = this.createConfigParam(Operation.remove, attrObj);
				resList.add(param);
			}
		}
		
		return resList;
	}
	
	private void init() throws DocumentException{
		String newPath = configParam.getNew_file_path();
		String oldPath = configParam.getOld_file_path();
		File newFile = new File(newPath);
		File oldFile = new File(oldPath);
		if(!newFile.isDirectory()){
			newDocument = CLICommonFunc.readXml(newPath);
		}else{
			GenerateVersionControlFile confile = new GenerateVersionControlFile(newPath);
			newDocument = confile.generateDoc();
		}
		if(!oldFile.isDirectory()){
			oldDocument = CLICommonFunc.readXml(oldPath);
		}else{
			GenerateVersionControlFile confile = new GenerateVersionControlFile(oldPath);
			oldDocument = confile.generateDoc();
		}
		
		if(newDocument == null || oldDocument == null){
			return;
		}
		
		newXmlList = new ArrayList<AhXmlAttribute>();
		oldXmlList = new ArrayList<AhXmlAttribute>();
		
		treeWalk(newXmlList, newDocument.getRootElement());
		treeWalk(oldXmlList, oldDocument.getRootElement());
		
	}
	
	private void treeWalk(List<AhXmlAttribute> contentList, Element element){
		
		//new instance
		AhXmlAttribute ahAttrObj = new AhXmlAttribute();
		
		//set elementName
		ahAttrObj.setElementName(element.getName());
		
		//set xpath
		ahAttrObj.setxPath(XmlOrderTool.getPath(element, true));
		
		//set parent xpath
		if(!element.isRootElement()){
			ahAttrObj.setParentXpath(XmlOrderTool.getPath(element.getParent(), true));
		}
		
		//set attribute list
		Iterator<?> attributes = element.attributeIterator();
		while(attributes.hasNext()){
			Attribute attrObj = (Attribute)attributes.next();
			AhXmlAttributeItem item = new AhXmlAttributeItem(attrObj.getName(), attrObj.getValue());
			ahAttrObj.getAttributes().add(item);
		}
		
		//put into list
		contentList.add(ahAttrObj);
		
		Iterator<?> elements = element.elementIterator();
		while(elements.hasNext()){
			Element childEle = (Element)elements.next();
			treeWalk(contentList, childEle);
		}
	}
	
	private ConfigParam createConfigParam(Operation operation, AhXmlAttribute attrObj){
		ConfigParam resParam = (ConfigParam)ConfigToolUtil.cloneObjectSerial(this.configParam);
		
		//set operation
		resParam.setOperation(operation);
		
		//set xpath
		if(resParam.getOperation() == Operation.add){
			resParam.setxPath(attrObj.getParentXpath());
		}else{
			resParam.setxPath(attrObj.getxPath());
		}
		
		//set content
		resParam.getContentEles().clear();
		resParam.getContentEles().add(attrObj.generateElement());
		
		return resParam;
	}
	
	public static class AhXmlAttribute {
		
		private String xPath;
		private String parentXpath;
		private String elementName;
		private List<AhXmlAttributeItem> attributes;

		public List<AhXmlAttributeItem> getAttributes() {
			if(attributes == null){
				attributes = new ArrayList<AhXmlAttributeItem>();
			}
			return attributes;
		}
		
		public String getElementName() {
			return elementName;
		}

		public void setElementName(String elementName) {
			this.elementName = elementName;
		}
		
		public Element generateElement(){
			String contentStr = elementName;
			
			for(AhXmlAttributeItem item : getAttributes()){
				String attrStr = item.getAttrName() + "=" + "\"" + item.getAttrValue() + "\"";
				contentStr += " " + attrStr;
			}
			
			contentStr = "<" + contentStr + "/>";
			
			List<Element> resList = ConfigToolUtil.stringToXml(contentStr);
			if(resList != null && !resList.isEmpty()){
				return resList.get(0);
			}else{
				return null;
			}
		}

		@Override
		public boolean equals(Object obj){
			AhXmlAttribute xmlAttr = null;
			if(obj instanceof AhXmlAttribute){
				xmlAttr = (AhXmlAttribute)obj;
			}
			if(xmlAttr == null){
				return false;
			}
			
			for(AhXmlAttributeItem item1 : getAttributes()){
				boolean isFound = false;
				for(AhXmlAttributeItem item2 : xmlAttr.getAttributes()){
					if(item1.equals(item2)){
						isFound = true;
						break;
					}
				}
				if(!isFound){
					return false;
				}
			}
			
			for(AhXmlAttributeItem item2 : xmlAttr.getAttributes()){
				boolean isFound = false;
				for(AhXmlAttributeItem item1 : getAttributes()){
					if(item2.equals(item1)){
						isFound = true;
						break;
					}
				}
				if(!isFound){
					return false;
				}
			}
			
			return new EqualsBuilder().append(elementName, xmlAttr.getElementName())
					.append(xPath, xmlAttr.getxPath())
					.append(parentXpath, xmlAttr.getParentXpath())
					.isEquals();
		}

		public String getxPath() {
			return xPath;
		}

		public void setxPath(String xPath) {
			this.xPath = xPath;
		}

		public String getParentXpath() {
			return parentXpath;
		}

		public void setParentXpath(String parentXpath) {
			this.parentXpath = parentXpath;
		}
	}
	
	public static class AhXmlAttributeItem {
		private String attrName;
		private String attrValue;
		
		public AhXmlAttributeItem(String attrName, String attrValue){
			this.attrName = attrName;
			this.attrValue = attrValue;
		}
		
		public String getAttrName() {
			return attrName;
		}

		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}

		public String getAttrValue() {
			return attrValue;
		}

		public void setAttrValue(String attrValue) {
			this.attrValue = attrValue;
		}

		@Override
		public boolean equals(Object obj){
			AhXmlAttributeItem attrItemObj = null;
			if(obj instanceof AhXmlAttributeItem){
				attrItemObj = (AhXmlAttributeItem)obj;
			}
			if(attrItemObj == null){
				return false;
			}
			return new EqualsBuilder().append(attrName, attrItemObj.getAttrName())
					.append(attrValue, attrItemObj.getAttrValue()).isEquals();
		}
	}
}
