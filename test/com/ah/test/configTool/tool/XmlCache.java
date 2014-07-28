package com.ah.test.configTool.tool;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class XmlCache {
	
	private static XmlCache instance;
	
	private Map<String, DocumentInfo> documentMap;

	private XmlCache(){
		documentMap = new HashMap<String, DocumentInfo>();
	}
	
	public static XmlCache getInstance(){
		if(instance == null){
			instance = new XmlCache();
		}
		return instance;
	}
	
	public Map<String, DocumentInfo> getDocumentMap() {
		return documentMap;
	}
	
	public boolean isExists(String path){
		return documentMap.get(path) != null;
	}
	
	public Document getDocument(String path){
		if(isExists(path)){
			return documentMap.get(path).getDocument();
		}else{
			return null;
		}
	}
	
	public void putDocument(String path, Document document, boolean... write){
		boolean isWrite = write.length > 0 ? write[0] : false;
		if(isExists(path)){
			documentMap.get(path).setDocument(document);
			documentMap.get(path).setWrite(isWrite);
		}else{
			DocumentInfo info = new DocumentInfo();
			info.setDocument(document);
			info.setWrite(isWrite);
			documentMap.put(path, info);
		}
	}

	public static class DocumentInfo{
		private Document document;
		private boolean write = false;
		
		public Document getDocument() {
			return document;
		}
		public void setDocument(Document document) {
			this.document = document;
		}
		public boolean isWrite() {
			return write;
		}
		public void setWrite(boolean write) {
			if(write){
				this.write = write;
			}
		}
	}
}
