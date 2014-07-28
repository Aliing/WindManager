package com.ah.be.config.cli.merge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.ah.be.config.cli.util.CLIConfigFileUtil;
import com.ah.be.config.cli.util.CLIGenUtil;

public class XmlMergeCache {
	
	private Document document;
	
	private Map<String, Element> xmlCacheMap;

	public XmlMergeCache(){
		try{
			this.document = DocumentHelper.parseText("<configuration/>");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public XmlMergeCache(String path){
		this.document = CLIConfigFileUtil.readXml(path);
	}
	
	public XmlMergeCache(Document document){
		this.document = document;
	}
	
	public void init(){
		if(document != null){
			xmlCacheMap = new HashMap<String, Element>();
			CLIGenUtil.generatePathElementMap(document.getRootElement(), xmlCacheMap);
		}
	}
	
	public Document getDocument(){
		return this.document;
	}
	
	public void mergeElement(Element element){
		String xPath = CLIGenUtil.getElementPath(element);
		
		if(xmlCacheMap.containsKey(xPath)){
			Iterator<?> items = element.elementIterator();
			if(items == null){
				return;
			}
			while(items.hasNext()){
				mergeElement((Element)items.next());
			}
		}else{
			if(element.isRootElement()){
				return;
			}
			
			String parentPath = CLIGenUtil.getElementPath(element.getParent());
			Element parentElement = xmlCacheMap.get(parentPath);
			if(parentElement != null){
				//This change no need iterate child element.
//				element.setParent(null);
//				parentElement.add(element);
				
				element = element.createCopy();
				parentElement.add(element);
				CLIGenUtil.generatePathElementMap(element, xmlCacheMap);
			}
		}
	}
	
	public void writeXml(String path, boolean isPretty) throws IOException{
		OutputFormat format = null;
		String os = System.getProperties().getProperty("os.name").toLowerCase();
		if(isPretty){
			format = new OutputFormat();
		}else{
			format = OutputFormat.createCompactFormat();
		}
		format.setIndent("\t");
		format.setNewlines(true);
		if(os.contains("win")){
			format.setLineSeparator("\r\n");
		}else{
			format.setLineSeparator("\n");
		}
		format.setTrimText(true);
		XMLWriter writer = new XMLWriter(new FileWriter(path), format);
		//xmlOrder
		document = XmlOrderTool.getInstance().orderXml(document);
		writer.write(document);
		writer.close();
	}
	
	public void println(){
		System.out.println(document.asXML());
		System.out.println("-------------------------------------------------");
		for(String key : xmlCacheMap.keySet()){
			System.out.println(key);
		}
	}
}
