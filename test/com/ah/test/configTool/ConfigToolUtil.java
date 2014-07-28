package com.ah.test.configTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ah.be.config.create.GenerateXML;


public class ConfigToolUtil {
	
	public static String getElementPath(Element ele) {
		Element element = ele;
		String elePath = "";
		while (true) {
			elePath = getCurrentNodePath(element) + elePath;
			if (element.isRootElement()) {
				break;
			}
			element = element.getParent();
		}
		return elePath;
	}

	public static String getCurrentNodePath(Element ele) {
		String nodePath = ele.getName();
		String attrName = ele.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
		if (attrName != null) {
			nodePath += "[@" + GenerateXML.ATTRIBUTE_NAME_NAME + "='"+attrName+"']";
		}
		return nodePath;
	}

	public static String getProjectPath(){
		String path = ConfigToolUtil.class.getResource("/").getPath();
		for(int i=0; i<2; i++){
			path = path.substring(0, path.lastIndexOf("/"));
		}
		return path;
	}
	
	public static String getCfgDefaultValuePath(String projectPath) {
		String defaultPath = "/webapps/schema/defaultValue";
		return projectPath + defaultPath;
	}
	
	public static String getCfgVersionXmlPath(String projectPath) {
		String versionXml = "/webapps/schema/versionXML";
		return projectPath + versionXml;
	}
	
	public static String getCfgSchemaPath(String projectPath) {
		String schema = "/webapps/schema";
		return projectPath + schema;
	}
	
	public static String getConfigXmlPath(){
		String path = ConfigToolUtil.class.getResource("").getPath();
		path += "config.xml";
		return path;
	}
	
	public static List<Element> stringToXml(String content){
		try{
			if(content == null || "".equals(content)){
				return null;
			}
			content = "<root>" + content + "</root>";
			Document document = DocumentHelper.parseText(content);
			
			List<Element> resList = new ArrayList<Element>();
			for(Object objEle : document.getRootElement().elements()){
				if(objEle instanceof Element){
					Element eleChild = (Element)objEle;
					resList.add(eleChild.createCopy());
				}
			}
			return resList;
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
	}
	
	public static class XmlFilter implements FilenameFilter {
		private Pattern pattern;
		private Matcher matcher;
		private List<String> rules = new ArrayList<String>();
		
		public XmlFilter(List<String> ruleList){
			for(String ruleStr : ruleList){
				this.rules.add(ruleStr.replace("*", ".*"));
			}
		}
		
		@Override
		public boolean accept(File dir, String name) {
			for(String rule : rules) {
				boolean takeBack = false;
				if(rule.startsWith("!")){
					rule = rule.substring(1);
					takeBack = true;
				}
				pattern = Pattern.compile(rule);
				matcher = pattern.matcher(name);
				
				boolean find = matcher.find();
				
				if(takeBack && !find){
					return true;
				}else if(!takeBack && find){
					return true;
				}
			}
			return false;
		}
	}
	
	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object cloneObjectSerial(Object object) {
		ByteArrayOutputStream outStream = null;
		ByteArrayInputStream inStream = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			outStream = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(outStream);
			oos.writeObject(object);
			oos.flush();
			
			inStream = new ByteArrayInputStream(outStream.toByteArray());    
			ois = new ObjectInputStream(inStream);    
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(outStream != null){
				try{
					outStream.close();
				}catch(Exception e){}
				
			}
			if(inStream != null){
				try{
					inStream.close();
				}catch(Exception e){}
			}
			if(oos != null){
				try{
					oos.close();
				}catch(Exception e){}
			}
			if(ois != null){
				try{
					ois.close();
				}catch(Exception e){}
			}
		}
		return null;
	}
	
	public Object restoreObj(InputStream inStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(inStream);

			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
