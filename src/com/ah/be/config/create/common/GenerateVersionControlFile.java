package com.ah.be.config.create.common;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author zhang
 * @version 2008-6-25 10:23:03
 */

public class GenerateVersionControlFile {
	
	public static final String SCHEMA_FILE_SUFIX = ".xsd";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_QUOTEPROHIBITED = "quoteProhibited";
	public static final String ATTR_ENCRYPTED = "encrypted";
	public static final String ATTR_UPDATETIME = "updateTime";
	public static final String ATTR_CLI_NAME = "CLIName";
	
	public static final String ATTR_OPERATION = "operation";
	public static final String ATTR_TYPE = "type";
	public static final String ELEMENT = "element";
	public static final String ATTRIBUTE = "attribute";
	public static final String DEFAULT = "default";
	
	public static final String UNKNOW = "unknow";
	
	private Map<String, Document> verXmlMap;
	
	public GenerateVersionControlFile(String schemaFolderPath) throws DocumentException{
		loadAllSchema(schemaFolderPath);
	}
	
	public static class SchemaFile implements FilenameFilter{
		public boolean accept(File dir,   String name){  
			return   name.endsWith(SCHEMA_FILE_SUFIX);
		}
	}
	
	public void generateFile(String writePath) throws IOException{
		Document doc = generateDoc();
		writeXml(doc, writePath, true);
	}
	
	public Document generateDoc() {
		Document confgDoc = verXmlMap.get("configuration");
		if(confgDoc == null){
			return null;
		}
		
		Element confgEle = (Element)confgDoc.selectSingleNode("//xsd:element");
		Iterator<?> iteEle = confgEle.elementIterator();
		Document doc = DocumentHelper.createDocument();
		Element rootEle = doc.addElement("configuration");
		
		while(iteEle.hasNext()){
			Element childEle = (Element)iteEle.next();
			treeWalkSchema(childEle, rootEle);
		}
		
		specialOperation(doc);
		
		return doc;
	}
	
	public static void writeXml(Document doc, String path, boolean isPretty) throws IOException{
		OutputFormat format = null;
		if(isPretty){
			format = new OutputFormat();
		}else{
			format = OutputFormat.createCompactFormat();
		}
		format.setIndent("\t");
		format.setNewlines(true);
		format.setLineSeparator("\r\n");
		format.setTrimText(true);
		
		XMLWriter writer = new XMLWriter(new FileWriter(path), format);
		writer.write(doc);
		writer.close();
	}
	
	/**
	 * overview: Extract element and attribute "name" from xml schema.
	 * @param element: The element from xml schema.
	 * @param parentEle: The element from xml version control file.
	 */
	private void treeWalkSchema(Element element, Element parentEle){
		String elementName = element.getName();
		
		/**
		 * It's attribute and name is "name", need to extract.
		 */
		if(ATTRIBUTE.equals(elementName)){
			String attrName = element.attributeValue(ATTR_NAME);
			String defaultStr = element.attributeValue(DEFAULT);
			
			if(ATTR_NAME.equals(attrName)){
				parentEle.addAttribute(attrName, "*");
			}else if(ATTR_VALUE.equals(attrName)){
				parentEle.addAttribute(attrName, "*");
			}else if(ATTR_OPERATION.equals(attrName)){
				/**
				 * If attribute and name is "operation", extract and fill default value "yes" or "yesWithValue" or "yesWithShow"
				 */
				String optValue = element.attributeValue(ATTR_TYPE);
				String fillOptValue;
				if(optValue.equals("general:ah-enum-act")){
					fillOptValue = "yes";
				}else if(optValue.equals("general:ah-enum-act-value")){
					fillOptValue = "yesWithValue";
				}else if(optValue.equals("general:ah-enum-show")){
					fillOptValue = "yesWithShow";
				}else if(optValue.equals("general:ah-enum-act-value-show")){
					fillOptValue = "yesWithValue|yesWithShow";
				}else{
					fillOptValue = UNKNOW;
				}
				parentEle.addAttribute(attrName, fillOptValue);
			}else if(ATTR_QUOTEPROHIBITED.equals(attrName)){
				parentEle.addAttribute(attrName, "yes");
			}else if(ATTR_ENCRYPTED.equals(attrName)){
				if(defaultStr == null){
					defaultStr = UNKNOW;
				}
				parentEle.addAttribute(attrName, defaultStr);
			}else if(ATTR_UPDATETIME.equals(attrName)){
				// Ignore this attribute.
			}else if(ATTR_CLI_NAME.equals(attrName)){
				if(defaultStr == null){
					defaultStr = elementName;
				}
				parentEle.addAttribute(attrName, defaultStr);
			}else{
				parentEle.addAttribute(attrName, UNKNOW);
			}
		}else if(ELEMENT.equals(elementName)){
			/**
			 * Extract for child element.
			 */
			parentEle = parentEle.addElement(element.attributeValue(ATTR_NAME));
			String attrType = element.attributeValue(ATTR_TYPE);
			if(attrType != null && !"".equals(attrType)){
				Element nestingElement = getElementByType(attrType);
				if(nestingElement != null){
					element.add(nestingElement.createCopy());
				}
			}
		}
		
		Iterator<?> iteEle = element.elementIterator();
		while(iteEle.hasNext()){
			Element childEle = (Element)iteEle.next();
			treeWalkSchema(childEle, parentEle);
		}
	}
	
	/**
	 * 
	 * @param sType: Type name.
	 * @return: Type detail message, if not exists return null.
	 */
	public Element getElementByType(String sType){
		Element eleRest = null;
		int splitIndex = sType.indexOf(":");
		
		String key = sType.substring(0, splitIndex);
		String attrName = sType.substring(splitIndex+1);
		
		Document doc = (Document)verXmlMap.get(key);
		if(doc != null){
			Iterator<?> ite = doc.getRootElement().elementIterator();
			while(ite.hasNext()){
				Element element = (Element)ite.next();
				String attrNameValue = element.attributeValue(ATTR_NAME);
				if(attrName.equals(attrNameValue)){
					eleRest = element;
					break;
				}
			}
		}
		return eleRest;
	}
	
	private void loadAllSchema(String schemaFolderPath) throws DocumentException{
		File file = new File(schemaFolderPath);
		File[] fileList = file.listFiles(new SchemaFile());
		SAXReader saxReader = new SAXReader();
		verXmlMap = new HashMap<String, Document>();
		
		for(int i=0; i<fileList.length; i++){
			Document docXsd = saxReader.read(fileList[i]);
			String key = fileList[i].getName().replace(SCHEMA_FILE_SUFIX, "");
			verXmlMap.put(key, docXsd);
		}
	}
	
	private void specialOperation(Document optDocument){
		//remove some element.
		String[] xPathArg = {
				"/configuration/ssid/security/aaa/radius-server/local",
				"/configuration/ssid/security/aaa/mac-format"
		};
		Element element;
		for(int i=0; i<xPathArg.length; i++){
			element = (Element)optDocument.selectSingleNode(xPathArg[i]);
			if(element != null){
				element.getParent().remove(element);
			}
		}
		
		//add CLIName attribute for <cr/>
		List<?> crList = optDocument.selectNodes("//cr");
		Element cldElement = null;
		Attribute attrObj = null;
		for(Object cldObj : crList){
			cldElement = (Element)cldObj;
			attrObj = cldElement.attribute(ATTR_CLI_NAME);
			if(attrObj != null){
				continue;
			}
			cldElement.addAttribute(ATTR_CLI_NAME, "");
		}
	}
	
	public static void main(String[] args) throws Exception{

		System.out.println("generate multi-version control file.");
		String schemaPath = args[0];
		String xmlPath = args[1];
		
		File schemaFile = new File(schemaPath);
		String fileName = schemaFile.getName();
		String ctlFilePath = xmlPath + File.separator + fileName +".xml";
		GenerateVersionControlFile confile = new GenerateVersionControlFile(schemaFile.getPath());
		confile.generateFile(ctlFilePath);
	}

}
