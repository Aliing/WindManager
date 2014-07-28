package com.ah.be.config.create.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author zhang
 * @version 2008-8-12 14:42:05
 */

public class FillAssistantElementForDefauleFile {
	
	private String defFilePath;//, ctlFilePath;
	private Document defDoc, ctlDoc;
	
	public FillAssistantElementForDefauleFile(String defFilePath, String ctlFilePath) throws DocumentException, IOException{
		this.defFilePath = defFilePath;
//		this.ctlFilePath = ctlFilePath;
		SAXReader read = new SAXReader();
		defDoc = read.read(new File(defFilePath));
		ctlDoc = read.read(new File(ctlFilePath));
		
		FillAssistantElementAutomatic();
		writeDefaultFile();
	}
	
	public void FillAssistantElementAutomatic(){
		List<?> listNode = ctlDoc.selectNodes("//AH-DELTA-ASSISTANT");
		for(Object assNode : listNode){
			Element assEle = (Element)assNode;
			createAssistantElement(assEle, defDoc);
		}
	}
	
	public void writeDefaultFile() throws IOException{
		XMLWriter writer = new XMLWriter(new FileWriter(new File(defFilePath)));
		writer.write(defDoc);
		writer.close();
	}
	
	private void createAssistantElement(Element assEle, Document cfgDocument){
		if(assEle == null || cfgDocument == null){
			return;
		}
		
		/** if element is exists return */
		if(cfgDocument.selectNodes(assEle.getPath()).size() > 0){
			return;
		}
		LinkedList<Element> assList = new LinkedList<Element>();
		while(true){
			assList.addFirst(assEle);
			if(assEle.isRootElement()){
				break;
			}else{
				assEle = assEle.getParent();
			}
		}

		for(Element ele : assList){
			if(cfgDocument.selectNodes(ele.getPath()).size() >0 ){
				
			}else{
				List<?> cfgChildList = cfgDocument.selectNodes(ele.getParent().getPath());
				for(Object cfgChildObj : cfgChildList){
					Element cfgEleObj = (Element)cfgChildObj;
					if("AH-DELTA-ASSISTANT".equals(ele.getName())){
						cfgEleObj.add(ele.createCopy());
					}else{
						cfgEleObj.addElement(ele.getName());
					}
				}
			}
		}
	}
	
//	public static void main(String[] args) throws DocumentException, IOException{
//		String defFile = "C:/test/defaultValue/DefaultValue_HiveAp_11n_3.2.1.0.xml";
//		String ctlFile = "C:/test/versionXML/HiveAp_11n_3.2.1.0.xml";
//		FillAssistantElementForDefauleFile fillDef = new FillAssistantElementForDefauleFile(defFile, ctlFile);
//	}
	
}
