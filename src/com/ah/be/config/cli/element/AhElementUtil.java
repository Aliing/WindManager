package com.ah.be.config.cli.element;

import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class AhElementUtil {
	
//	private static String BR_STR;
//	private static String T_STR = "\t";
//	static{
//		String os = System.getProperties().getProperty("os.name").toLowerCase();
//		if(os.contains("win")){
//			BR_STR = "\r\n";
//		}else{
//			BR_STR = "\n";
//		}
//	}

	//conver dom4j Element object to AhElement object
	public static AhElement converDomToAhElement(Element element){
		if(element == null){
			return null;
		}
		
		//set element name.
		AhElement ahElement = new AhElement();
		ahElement.setName(element.getName());
		
		//set element attribute.
		Iterator<?> attrItem = element.attributeIterator();
		if(attrItem != null){
			Attribute attrObj;
			String attrName, attrValue;
			while(attrItem.hasNext()){
				attrObj = (Attribute)attrItem.next();
				attrName = attrObj.getName();
				attrValue = attrObj.getValue();
				ahElement.addAttribute(attrName, attrValue);
			}
		}
		
		//set child elements.
		Iterator<?> childItem = element.elementIterator();
		if(childItem != null){
			Element childElement;
			AhElement ahChildElement;
			while(childItem.hasNext()){
				childElement = (Element)childItem.next();
				ahChildElement = converDomToAhElement(childElement);
				ahElement.addChildElement(ahChildElement);
			}
		}
		
		return ahElement;
	}
	
	public static Element converAhElementToElement(AhElement ahElement){
		if(ahElement == null){
			return null;
		}
		
		//set element name.
		Element element = DocumentHelper.createElement(ahElement.getName());
		
		//set attributes.
		if(ahElement.getAttributes() != null){
			for(AhAttribute attrObj : ahElement.getAttributes()){
				element.addAttribute(attrObj.getName(), attrObj.getValue());
			}
		}
		
		//set elements
		if(ahElement.getChilds() != null){
			Element cldElement = null;
			for(AhElement cldEle : ahElement.getChilds()){
				cldElement = converAhElementToElement(cldEle);
				if(cldElement != null){
					element.add(cldElement);
				}
			}
		}
		
		return element;
	}
	
//	public static AhElement converStringToAhElement(String xmlStr){
//		char[] charArgs = xmlStr.toCharArray();
//		int eleStart = -1, eleEnd= -1, endFlag = -1;
//		boolean blnEnd = false;
//		AhElement ahElement = null;
//		for(int i=0; i<charArgs.length; i++){
//			char c = charArgs[i];
//			if(c == '<'){
//				eleStart = i;
//			}else if(c == '>'){
//				eleEnd = i;
//			}else if(c == '/'){
//				endFlag = i;
//			}
//			
//			if(!(eleEnd > eleStart && eleStart > 0)){
//				continue;
//			}
//			
//			if(endFlag > 0){
//				if(eleEnd - endFlag ==1){
//					
//				}else if(eleStart - endFlag == 1){
//					
//				}
//			}
//		}
//		
//		return null;
//	}
	
}
