package com.ah.be.config.cli.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.ah.be.config.cli.util.CLIConfigFileUtil;
import com.ah.be.config.cli.util.CLIGenUtil;

public class XmlOrderTool {
	
	public static final String SINGLE_QUOTE = "'";
	public static final String DOUBLE_QUOTE = "\"";
	
	private static XmlOrderTool instance;
	private int orderIndex = 0;
	
	private Map<String, Integer> standardMap = new HashMap<String, Integer>();
	
	public static XmlOrderTool getInstance(){
		if(instance == null){
			instance = new XmlOrderTool();
		}
		return instance;
	}
	
	public static XmlOrderTool getInstance(Document schemaDoc){
		if(instance == null){
			instance = new XmlOrderTool(schemaDoc);
		}
		return instance;
	}

	private XmlOrderTool(){
		String schemaPath = CLIConfigFileUtil.getCLITemplatePath();
		Document standardDoc = CLIConfigFileUtil.readXml(schemaPath);
		Element rootEle = standardDoc.getRootElement();
		initOrder(rootEle);
	}
	
	private XmlOrderTool(Document schemaDoc){
		Element rootEle = schemaDoc.getRootElement();
		initOrder(rootEle);
	}
	
	private void initOrder(Element rootEle){
		standardMap.put(getPath(rootEle, false), orderIndex++);
		
		List<?> childEles = rootEle.elements();
		if(childEles == null || childEles.isEmpty()){
			return;
		}
		
		for(Object obj : childEles) {
			if(obj instanceof Element){
				initOrder((Element)obj);
			}
		}
	}
	
	public void orderXml(String path) throws DocumentException, IOException{
		Document destDoc = CLIConfigFileUtil.readXml(path);
		
		orderXml(destDoc.getRootElement());
		CLIConfigFileUtil.writeXml(destDoc, path, false);
	}
	
	public Document orderXml(Document destDoc){
		orderXml(destDoc.getRootElement());
		return destDoc;
	}
	
	public void orderXml(Element element){
		if(element == null){
			return;
		}
		List<?> childEles = element.elements();
		if(childEles == null || childEles.isEmpty()){
			return;
		}
		
		//store Element to list and then sort.
		List<ElementPriority> elementList = new ArrayList<>();
		Element childEle = null;
		String path = null;
		Integer priorityInt = null;
		for(Object obj : childEles){
			if(!(obj instanceof Element)){
				continue;
			}
			childEle = (Element)obj;
			path = getPath(childEle, false);
			priorityInt = standardMap.get(path);
			childEle.setParent(null);
			if(priorityInt != null){
				elementList.add(new ElementPriority(childEle, priorityInt) );
//				elementList.add(new ElementPriority(childEle.createCopy(), priorityInt) );
			}
		}
		childEles = null;
		Collections.sort(elementList);
		
		//recreate child element
		element.clearContent();
		for(ElementPriority elePriorityObj : elementList){
			element.add(elePriorityObj.getElement());
			orderXml(elePriorityObj.getElement());
		}
		elementList.clear();
		elementList = null;
	}
	
	public static String getPathAttrContext(String attrName, String attrValue){
		if(attrValue.contains(DOUBLE_QUOTE)){
			return attrName + "=" + SINGLE_QUOTE + attrValue + SINGLE_QUOTE;
		}else{
			return attrName + "=" + DOUBLE_QUOTE + attrValue + DOUBLE_QUOTE;
		}
	}
	
	public static String getPath(Element element, boolean withName) {
		String eleName = element.getName();
		String attrStr = null;
		String path;
		
		if(withName){
			String attrName = element.attributeValue(CLIGenUtil.ATTR_NAME);
			if(attrName != null){
				attrStr = "@" + getPathAttrContext(CLIGenUtil.ATTR_NAME, attrName);
			}
		}
		
		path = "/" + eleName;
		if(attrStr != null){
			path += "[" + attrStr + "]";
		}
		
		if(element.getParent() != null){
			path = getPath(element.getParent(), withName) + path;
		}
		
		return path;
	}
	
	private class ElementPriority implements Comparable<ElementPriority>{
		private Element element;
		private Integer priority;
		
		public ElementPriority(Element element, Integer priority){
			this.element = element;
			this.priority = priority;
		}
		
		public Element getElement() {
			return element;
		}
		public Integer getPriority() {
			return priority;
		}

		@Override
		public int compareTo(ElementPriority o) {
			return priority - o.getPriority();
		}
	}
	
	public static void main(String[] args) throws Exception {
//		String verXmlPath = "D:/test/xml/VPN_Gateway_4.1.1.0.xml";
//		String destXmlPath = "D:/test/xml/test.xml";
		
//		SAXReader reader = new SAXReader();
//		Document doc = reader.read(new File(verXmlPath));
		
//		XmlOrderTool orderTool = new XmlOrderTool(verXmlPath);
//		orderTool.orderXml(destXmlPath);
	}
}
