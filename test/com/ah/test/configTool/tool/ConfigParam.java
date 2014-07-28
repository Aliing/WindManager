package com.ah.test.configTool.tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.ah.test.configTool.ConfigToolUtil;



public class ConfigParam implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static enum ConfigType{
		version_file("version-file"), 
		default_file("default-file");
		
		private String value;
		ConfigType(String value){
			this.value = value;
		}
		
		public static ConfigType getInstance(String value){
			if(version_file.value.equalsIgnoreCase(value)){
				return version_file;
			}else if(default_file.value.equalsIgnoreCase(value)){
				return default_file;
			}else{
				return null;
			}
		}
	}
	
	public static enum Operation{
		add, remove, override, override_attribute, 
		add_version, remove_version, format, auto;
		
		public static Operation getInstance(String name){
			if(add.name().equalsIgnoreCase(name)){
				return add;
			}else if(remove.name().equalsIgnoreCase(name)){
				return remove;
			}else if(override.name().equalsIgnoreCase(name)){
				return override;
			}else if(override_attribute.name().equalsIgnoreCase(name)){
				return override_attribute;
			}else if(add_version.name().equalsIgnoreCase(name)){
				return add_version;
			}else if(remove_version.name().equalsIgnoreCase(name)){
				return remove_version;
			}else if(format.name().equalsIgnoreCase(name)){
				return format;
			}else if(auto.name().equalsIgnoreCase(name)){
				return auto;
			}else{
				return null;
			}
		}
	}
	
	public static enum ElementName {
		root("config"),
		project_path("project-path"),
		operation("operation"),
		depend_path("depend-path"),
		element_path("element-path"),
		content("content"),
		file_name("file-name"),
		version("version"),
		base_version("base-version"),
		new_file_path("new-file-path"),
		old_file_path("old-file-path")
		;
		
		private String name;
		ElementName(String name){
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public static enum AttributeName{
		exists
	}
	
	public static String projectPath;
	public static Document schemaDoc;
	
	private boolean validParm = false;
	
	private ConfigType configType;
	
	private Operation operation;
	
	private String folderPath;
	
	private String xPath;
	
	private String version;
	
	private String base_version;
	
	private String new_file_path;
	
	private String old_file_path;
	
	private List<Element> contentEles;
	
	private List<String> fileNames;
	
	private List<DependPathParam> dependPaths;
	
	public ConfigParam(){}
	
	public ConfigParam(Element configEle){
		init(configEle);
	}
	
	private void init(Element configEle){
		if(configEle == null){
			return;
		}
		
		//set configType
		String eleName = configEle.getName();
		configType = ConfigType.getInstance(eleName);
		if(configType == null){
			return;
		}
		
		//set operation
		Element operationEle = (Element)configEle.selectSingleNode(ElementName.operation.getName());
		if(operationEle == null){
			return;
		}
		operation = Operation.getInstance(operationEle.getTextTrim());
		
		//set folderPath
		if(configType == ConfigType.version_file){
			folderPath = ConfigToolUtil.getCfgVersionXmlPath(projectPath);
		}else if(configType == ConfigType.default_file){
			folderPath = ConfigToolUtil.getCfgDefaultValuePath(projectPath);
		}
		
		//set depend-path
		List<?> dependObjs = configEle.selectNodes(ElementName.depend_path.getName());
		if(dependObjs != null){
			for(Object dObj : dependObjs){
				if(dObj instanceof Element){
					Element dependEle = (Element)dObj;
					String dPath = dependEle.getTextTrim();
					boolean chkExists = Boolean.valueOf(dependEle.attributeValue(AttributeName.exists.name()));
					this.getDependPaths().add(new DependPathParam(dPath, chkExists));
				}
			}
		}
		
		//set xPath
		Element elementPathEle = (Element)configEle.selectSingleNode(ElementName.element_path.getName());
		String cfgXPath = null;
		if(elementPathEle != null){
			cfgXPath = elementPathEle.getTextTrim();
			if(this.configType == ConfigType.version_file && this.operation == Operation.add){
				this.xPath = cfgXPath.substring(0, cfgXPath.lastIndexOf("/"));
			}else{
				this.xPath = cfgXPath;
			}
		}
		
		//set version
		Element versionEle = (Element)configEle.selectSingleNode(ElementName.version.getName());
		if(versionEle != null){
			this.version = versionEle.getTextTrim();
		}
		
		//set base-version
		Element baseVersionEle = (Element)configEle.selectSingleNode(ElementName.base_version.getName());
		if(baseVersionEle != null){
			this.base_version = baseVersionEle.getTextTrim();
		}
		
		//set new_file_path
		Element newPathEle = (Element)configEle.selectSingleNode(ElementName.new_file_path.getName());
		if(newPathEle != null){
			this.new_file_path = newPathEle.getTextTrim();
		}
		
		//set old_file_path
		Element oldPathEle = (Element)configEle.selectSingleNode(ElementName.old_file_path.getName());
		if(oldPathEle != null){
			this.old_file_path = oldPathEle.getTextTrim();
		}
		
		//set contentEles
		List<?> contLists = configEle.selectNodes(ElementName.content.getName());
		if(contLists != null && !contLists.isEmpty()){
			for(Object obj : contLists){
				if(obj instanceof Element){
					Element contentEle = (Element)obj;
					String contentStr = contentEle.getTextTrim();
					List<Element> contentEles = ConfigToolUtil.stringToXml(contentStr);
					if(contentEles != null && !contentEles.isEmpty()){
						this.getContentEles().addAll(contentEles);
					}
				}
			}
		}else if(cfgXPath != null){
			Element eleContent =  (Element)schemaDoc.selectSingleNode(cfgXPath);
			if(eleContent != null){
				this.getContentEles().add(eleContent);
			}
		}
		
		//set fileNames
		List<?> fileNameNodes = configEle.selectNodes(ElementName.file_name.getName());
		if(fileNameNodes != null){
			for(Object node : fileNameNodes){
				if(node instanceof Element){
					Element fNameEle = (Element)node;
					this.getFileNames().add(fNameEle.getTextTrim());
				}
			}
		}
		if(this.operation == Operation.add_version && this.base_version != null){
			this.getFileNames().add("*" + base_version + "*");
		}else if(this.operation == Operation.remove_version && this.version != null){
			this.getFileNames().add("*" + version + "*");
		}
		
		//set validParm
		this.validParm = true;
	}
	
	public static class DependPathParam {
		private String dependPath;
		private boolean checkExists;
		
		public DependPathParam(String dependPath, boolean checkExists){
			this.dependPath = dependPath;
			this.checkExists = checkExists;
		}
		
		public String getDependPath() {
			return dependPath;
		}
		public void setDependPath(String dependPath) {
			this.dependPath = dependPath;
		}
		public boolean isCheckExists() {
			return checkExists;
		}
		public void setCheckExists(boolean checkExists) {
			this.checkExists = checkExists;
		}
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public List<Element> getContentEles() {
		if(contentEles == null){
			contentEles = new ArrayList<Element>();
		}
		return contentEles;
	}

	public List<String> getFileNames() {
		if(fileNames == null){
			fileNames = new ArrayList<String>();
		}
		return fileNames;
	}

	public ConfigType getConfigType() {
		return configType;
	}

	public void setConfigType(ConfigType configType) {
		this.configType = configType;
	}

	public boolean isValidParm() {
		return validParm;
	}

	public void setValidParm(boolean validParm) {
		this.validParm = validParm;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBase_version() {
		return base_version;
	}

	public void setBase_version(String base_version) {
		this.base_version = base_version;
	}

	public String getNew_file_path() {
		return new_file_path;
	}

	public void setNew_file_path(String new_file_path) {
		this.new_file_path = new_file_path;
	}

	public String getOld_file_path() {
		return old_file_path;
	}

	public void setOld_file_path(String old_file_path) {
		this.old_file_path = old_file_path;
	}

	public List<DependPathParam> getDependPaths() {
		if(dependPaths == null){
			dependPaths = new ArrayList<DependPathParam>();
		}
		return dependPaths;
	}
}
