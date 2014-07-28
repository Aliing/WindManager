package com.ah.be.config.create.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.GenerateXML;

public class FillAssistantElementAuto {
	
	private static FillAssistantElementAuto instance;
	private List<Element> allAssistants = new ArrayList<Element>();
	
	public static FillAssistantElementAuto getInstance(){
		if(instance == null){
			instance = new FillAssistantElementAuto();
		}
		return instance;
	}
	
	private FillAssistantElementAuto(){
		Document allXmlDoc = AhControlMultiApVer.getAllSchemaXml();
		fillAssistantElementAuto(allXmlDoc);
	}
	
	private void fillAssistantElementAuto(Document allXmlDoc){
		List<?> nodeList = allXmlDoc.selectNodes("//"+GenerateXML.AH_DELTA_ASSISTANT);
		if(nodeList != null){
			for(Object obj : nodeList){
				if(obj instanceof Element){
					Element eleObj = (Element)obj;
					allAssistants.add(eleObj);
				}
			}
		}
	}
	
	public void	fillElement(String path) throws DocumentException, IOException{
		Document resDoc = CLICommonFunc.readXml(path);
		resDoc = fillAllAssistant(resDoc);
		CLICommonFunc.writeXml(resDoc, path, false);
	}
	
	public Document fillElement(Document doc){
		Document resDoc = fillAllAssistant(doc);
		return resDoc;
	}
	
	private Document fillAllAssistant(Document doc){
		for(Element eleObj : allAssistants){
			createElement(eleObj, doc);
		}
		return doc;
	}
	
	private void createElement(Element element, Document destDoc){
		LinkedList<Element> eleLists = new LinkedList<Element>();
		String nameElePath = null;
		while(!element.isRootElement()){
			String eleName = element.getName();
			String attrName = element.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
			String attrValue = element.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
			String attrOpt = element.attributeValue(GenerateXML.ATTRIBUTE_NAME_OPERATION);
			if(!eleName.equals(GenerateXML.AH_DELTA_ASSISTANT) && 
					(attrName != null || attrValue != null || attrOpt != null)){
				nameElePath = XmlOrderTool.getPath(element, false);
				break;
			}else{
				eleLists.addFirst(element);
				element = element.getParent();
			}
			
		}
		
		if(nameElePath == null){
			copyElement(eleLists, destDoc.getRootElement());
		}else{
			List<?> destLists = destDoc.selectNodes(nameElePath);
			for(Object obj : destLists){
				Element eleObj = (Element)obj;
				copyElement(eleLists, eleObj);
			}
		}
	}
	
	private void copyElement(List<Element> eleList, Element destEle){
		for(Element eleNext : eleList){
			String eleName = eleNext.getName();
			List<?> desLists = destEle.elements();
			boolean found = false;
			for(Object obj : desLists){
				if(obj instanceof Element){
					Element ele1 = (Element)obj;
					if(eleName.equals(ele1.getName())){
						destEle = ele1;
						found = true;
						break;
					}
				}
			}
			if(!found){
				destEle = destEle.addElement(eleName);
				List<?> attrs = eleNext.attributes();
				for(Object obj : attrs){
					if(obj instanceof Attribute){
						Attribute attr = (Attribute)obj;
						destEle.addAttribute(attr.getName(), attr.getValue());
					}
				}
			}
		}
	}
	
//	private List<Element> createElement2(Element element, Document destDoc){
//		List<?> elements = destDoc.selectNodes(element.getPath());
//		if(elements != null && !elements.isEmpty()){
//			List<Element> resList = new ArrayList<Element>();
//			for(Object obj : elements){
//				if(obj instanceof Element){
//					resList.add((Element)obj);
//				}
//			}
//			return resList;
//		}
//		
//		if(element.attribute(GenerateXML.ATTRIBUTE_NAME_NAME) != null){
//			return null;
//		}
//		
//		List<Element> eleParents = createElement(element.getParent(), destDoc);
//		if(eleParents == null) {
//			return null;
//		}
//		
//		String eleName = element.getName();
//		List<?> attrs = element.attributes();
//		
//		List<Element> resList = new ArrayList<Element>();
//		for(Element eleObj : eleParents){
//			Element addEle = eleObj.addElement(eleName);
//			for(Object attrObj : attrs){
//				if(attrObj instanceof Attribute){
//					Attribute attrNode = (Attribute)attrObj;
//					addEle.addAttribute(attrNode.getName(), attrNode.getValue());
//				}
//			}
//			resList.add(addEle);
//		}
//		
//		return resList;
//	}
}
