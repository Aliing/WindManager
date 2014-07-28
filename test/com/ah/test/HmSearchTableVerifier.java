/**
 * @filename			HmSearchTableVerifier.java
 * @version
 * @author				jchen
 * @since
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.ah.be.common.file.XMLFileReadWriter;
import com.ah.be.search.SearchEngine;
import com.ah.be.search.SearchEngineImpl;

/**
 * This class could be used to check the changes of HM search tables 
 * before a version is going to be released.
 */
public class HmSearchTableVerifier {

	public HmSearchTableVerifier() {
		
	}
	
	
	private void verify(String filepath1, String node1, String filepath2, String node2) {
		File file1 = new File(filepath1);
		
		if(!file1.exists()) {
			log("file <" + filepath1 + "> is not existed.");
			return ;
		}
		
		File file2 = new File(filepath2);
		
		if(!file2.exists()) {
			log("file <" + filepath2 + "> is not existed.");
			return ;
		}
		
		Document document1 = null;
		Document document2 = null;
		
		try {
			document1 = XMLFileReadWriter.parser(file1);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		Element root1 = document1.getRootElement();
		List<?> elements1 = root1.elements();
		
		try {
			document2 = XMLFileReadWriter.parser(file2);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		Element root2 = document2.getRootElement();
		List<?> elements2 = root2.elements();
		
		verifyNodes(elements1, node1, elements2, node2);
	}
	
	private void verifyNodes(List<?> elements1, String node1, List<?> elements2, String node2) {
		for(Object obj : elements1) {
			Element element = (Element) obj;
			String value = element.attributeValue(node1); 
			
			if(value != null) {
				log("checking node - " + value + " .......................");
				
				if(!checkNode(value, elements2, node2)) {
					log("node [" + value + "]  is not in the file2. ====================================");
				}
			}
			
			if(element.elements() != null
					&& element.elements().size() > 0) {
				verifyNodes(element.elements(), node1, elements2, node2);
			}
		}
	}
	
	public void doVerfiy() {
		String file1 = SearchEngine.SEARCH_RESOURCES_PATH + SearchEngineImpl.SEARCH_CONTROL_FILE;
		String hmRoot = System.getenv("HM_ROOT");
		String file2 = hmRoot != null ? hmRoot + File.separator + "WEB-INF"
				+ File.separator + "navigation.xml" : "webapps" + File.separator + "hm"
				+ File.separator + "WEB-INF" + File.separator + "navigation.xml";
		
		log("checking nodes in file - " + file1 + "**********************************");
		verify(file1, "node", file2, "description");
		
		log("checking nodes in file - " + file2 + "**********************************");
		verify(file2, "description", file1, "node");
	}
	
	private boolean checkNode(String nodeValue, List<?> elements, String node2) {
		if(nodeValue == null
				|| elements == null
				|| elements.size() <= 0) {
			return false;
		}
		
		for(Object obj : elements) {
			Element element = (Element) obj;

			if(nodeValue.equalsIgnoreCase(element.attributeValue(node2))) {
				return true;
			}
			
			Iterator<?> it = element.elements().iterator();
			
			while(it.hasNext()) {
				Element node = (Element)it.next();
				
				if(nodeValue.equalsIgnoreCase(node.attributeValue(node2))) {
					return true;
				}
				
				if(node.elements() != null
						&& node.elements().size() > 0) {
					boolean existed = checkNode(nodeValue, node.elements(), node2);
					
					if(existed) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private void log(String log) {
		System.out.println(log);
	}
	
	/**
	 * @param args
	 * @author Joseph Chen
	 */
	public static void main(String[] args) {

	}

}
