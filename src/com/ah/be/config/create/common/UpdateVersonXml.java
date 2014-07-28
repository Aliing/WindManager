package com.ah.be.config.create.common;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class UpdateVersonXml {
	
    private static Document document;
    
    private File file;
    
    private String xPath;
    
    private String dir;
    
    private String regex;
    
    
    public static enum UpdateType{
		addElement, updateElement, delElement,addAttr,updateAttr,delAttr
	}
    
    
    public UpdateVersonXml(String dir,String xPath,String regex){
    	this.dir = dir;
    	this.xPath = xPath;
    	this.regex = regex;
    }
    
    private Document getDocument() throws DocumentException {
    	SAXReader reader = new SAXReader();
    	Document document = reader.read(file);
        return document;
    }
    
    private List<Object> findXPath() throws Exception {
        XPath xpath = DocumentHelper.createXPath(xPath);
        List<Object> list = xpath.selectNodes(document);
        return list;
    }
    
    private void saveDocument() throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
        writer.write(document);
        writer.flush();
        writer.close();
	     
    }
    
    private void addElement(String str) throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
    		for (Object obj : list) {
    			 Element element =(Element)obj;
    			 Document  doc = DocumentHelper.parseText(str);
    			 element.add(doc.getRootElement());
    			 log(file.getName()+" add element:" + doc.getRootElement().asXML());
            }
    		 
    		saveDocument();
    	}
    }
    
    private void removeElement() throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
    		for (Object obj : list) {
                Element element =(Element)obj;
                Element preElement = element.getParent();
                if(preElement != null){
                	preElement.remove(element); 
                }else{
                	break;
                } 
                if(preElement.isTextOnly()){
                	preElement.clearContent();
                }
                log(file.getName()+" remove element:" +element.getName());
            }
    		saveDocument();
    	}
    }
    
    private void modifyElement(String name) throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
    		for (Object obj : list) {
                Element element =(Element)obj;
                log(file.getName()+" update element name:" +element.getName()+ "to" +name);
                element.setName(name);
            }
    		saveDocument(); 
    	}
		  	
    }
    
    private void updateXmlFileorAttr(UpdateType type,String eleName,String attrName,String attrValue) throws Exception{
    	File file = new File(dir);
    	if(file.isDirectory()){
    		File[] filelist = file.listFiles(new FileNameFilter(regex));
    		for(File childfile : filelist){
    			this.file = childfile;
    			UpdateXml(type,eleName,attrName,attrValue);
    		}
    	}
    }
    
    private void UpdateXml(UpdateType type,String eleName,String attrName,String attrValue) throws Exception{
    	switch(type){
	    	case addElement:
	    		addElement(eleName);
	    		break;
	    	case updateElement:
	    		modifyElement(eleName);
	    		break;
	    	case delElement:
	    		removeElement();
	    		break;
	    	case addAttr:
	    		addElementAttribute(attrName,attrValue);
	    		break;	
	    	case updateAttr:
	    		modifyElementAttribute(attrValue);
	    		break;
	    	case delAttr:
	    		removeElementAttribute();
	    		break;
	    	default:
	    		break;
    	}
	}
    
    private static void log(String message){
    	System.out.println(message);
    }
   
    private void addElementAttribute(String attr,String value) throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
			for (Object obj : list) {
	            Element element =(Element)obj;
	            element.addAttribute(attr, value);
	            log(file.getName() + " add attrbute:" + attr);
	        }
			saveDocument(); 	
    	}
    }
    
    private void removeElementAttribute() throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
			for (Object obj : list) {
				Attribute attr =(Attribute)obj;
	            attr.getParent().remove(attr);
	            log(file.getName() + " del attrbute:" + attr.getName());
	        }
			saveDocument();  
    	}
    }
    
    private void modifyElementAttribute(String value) throws Exception{
    	document = getDocument();
    	List<Object> list = findXPath();
    	if(!list.isEmpty()){
			for (Object obj : list) {
				Attribute attr =(Attribute)obj;
	            attr.setValue(value);
	            log(file.getName() + " modify attrbute" + attr.getName() + " to " + value);
	        }
			saveDocument();   
    	}
    }
    
    private class FileNameFilter implements FilenameFilter{
    	private String regex;

    	public FileNameFilter(String regex){
    		this.regex = regex;
    	}
    	
    	public boolean isXml(String file) {   
    		Pattern pattern = Pattern.compile(regex);
    		Matcher matcher = pattern.matcher(file);
    	    return matcher.matches();
    	}    
    	
    	public boolean accept(File dir, String name) {
    		return isXml(name);
    	}

    }
    
    public static void main(String args[]) throws Exception{
    	//select Directory
    	String filedir = "./test";
    	//select element
    	String xPath = "/configuration/os-detection/method";
    	//String xPath = "/configuration/snmp/reader/version/v1/community/@operation";
    	String regex ="\\w*5.0.4.0.xml";
    	
    	String eleName ="<AH-DELTA-ASSISTANT operation=\"yes\"/>";
			
    	UpdateVersonXml update =new UpdateVersonXml(filedir,xPath,regex);
    	//ReadAndWriteXml(eleName);
    	update.updateXmlFileorAttr(UpdateType.addElement,eleName,null, null);
    }
}