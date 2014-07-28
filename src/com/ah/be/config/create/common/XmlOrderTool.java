package com.ah.be.config.create.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.GenerateXML;

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
		Document standardDoc = AhControlMultiApVer.getAllSchemaXml();
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
		Document orderDoc = DocumentHelper.createDocument();
		Document destDoc = CLICommonFunc.readXml(path);
		Element rootEle = destDoc.getRootElement();
		
		Map<String, Element> eleMap = new HashMap<String, Element>();
		eleMap.put(getPath(rootEle, true), orderDoc.addElement(rootEle.getName()));
		
		orderXml(destDoc.getRootElement(), eleMap);
		CLICommonFunc.writeXml(orderDoc, path, false);
		orderDoc = null;
		destDoc = null;
		rootEle = null;
		eleMap = null;
	}
	
	public Document orderXml(Document destDoc){
		Document orderDoc = DocumentHelper.createDocument();
		Element rootEle = destDoc.getRootElement();
		
		Map<String, Element> eleMap = new HashMap<String, Element>();
		eleMap.put(getPath(rootEle, true), orderDoc.addElement(rootEle.getName()));
		
		orderXml(destDoc.getRootElement(), eleMap);
		destDoc = null;
		rootEle = null;
		eleMap = null;
		
		return orderDoc;
	}
	
	private void orderXml(Element ele, Map<String, Element> eleMap){
		copyElement(ele, eleMap);
		
		List<?> eleLists = ele.elements();
		if(eleLists == null || eleLists.isEmpty()){
			return;
		}
		
		List<ElementPriority> priorityList = new ArrayList<ElementPriority>();
		for(Object obj : eleLists){
			if(obj instanceof Element){
				Element childEle = (Element)obj;
				String path = getPath(childEle, false);
				Integer orderIndex = standardMap.get(path);
				if(orderIndex != null){
					priorityList.add(new ElementPriority(childEle, orderIndex));
				}else{
					System.out.println(path);
				}
			}
		}
		
		Collections.sort(priorityList, new Comparator<ElementPriority>() {
			@Override
			public int compare(ElementPriority o1, ElementPriority o2) {
				return o1.getPriority().intValue() - o2.getPriority().intValue();
			}
		});
		
		for(ElementPriority priObj : priorityList){
			Element eleTemp = priObj.getElement();
			orderXml(eleTemp, eleMap);
		}
		priorityList = null;
		eleLists = null;
	}
	
	private void copyElement(Element ele, Map<String, Element> eleMap){
		String eleName = ele.getName();
		List<?> atts = ele.attributes();
		
		Element copyEle = null;
		if(!ele.isRootElement()){
			String parentPath = getPath(ele.getParent(), true);
			Element copyParent = eleMap.get(parentPath);
			copyEle = copyParent.addElement(eleName);
			eleMap.put(getPath(ele, true), copyEle);
		}
		
		if(copyEle == null){
			return;
		}
		
		//copy attributes
		for(Object obj : atts){
			if(obj instanceof Attribute){
				Attribute attr = (Attribute)obj;
				copyEle.addAttribute(attr.getName(), attr.getValue());
			}
		}
		atts = null;
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
		String attrName = element.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
		String attrValue = element.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
		String attrStr = null;
		String path;
		
		if(withName){
			if(attrName != null){
				attrStr = "@" + getPathAttrContext(GenerateXML.ATTRIBUTE_NAME_NAME, attrName);
			}else if(attrValue != null){
				attrStr = "@" + getPathAttrContext(GenerateXML.ATTRIBUTE_NAME_VALUE, attrValue);
			}
		}
		
		path = "/" + eleName;
		if(attrStr != null){
			path += "[" + attrStr + "]";
		}
		
//		if (attrName == null || !withName) {
//			path = "/" + eleName;
//		} else {
//			path = "/" + eleName + "[@"
//					+ GenerateXML.ATTRIBUTE_NAME_NAME + "='" + attrName
//					+ "']";
//		}
		
		if(!element.isRootElement()){
			path = getPath(element.getParent(), withName) + path;
		}
		
		return path;
	}
	
	private class ElementPriority{
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
