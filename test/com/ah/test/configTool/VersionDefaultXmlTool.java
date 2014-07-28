package com.ah.test.configTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.common.GenerateVersionControlFile;
import com.ah.be.config.create.common.XmlOrderTool;
import com.ah.test.configTool.tool.AutoCalculateDifference;
import com.ah.test.configTool.tool.ConfigParam;
import com.ah.test.configTool.tool.ConfigParam.DependPathParam;
import com.ah.test.configTool.tool.ConfigParam.ElementName;
import com.ah.test.configTool.tool.ConfigParam.Operation;
import com.ah.test.configTool.tool.XmlCache;
import com.ah.test.configTool.tool.XmlCache.DocumentInfo;

public class VersionDefaultXmlTool {
	
	private ConfigParam configParam;
	
	public VersionDefaultXmlTool(ConfigParam configParam){
		this.configParam = configParam;
	}
	
	public void generate() throws Exception {
		if(!configParam.isValidParm()){
			return;
		}
		
		File folder = new File(configParam.getFolderPath());
		File[] filFiles = folder.listFiles(new ConfigToolUtil.XmlFilter(configParam.getFileNames()));
		
		for(int i=0; i<filFiles.length; i++){
			if(filFiles[i].isDirectory()){
				continue;
			}
			
			boolean writeXml = false;
			
			//get document from cache.
			String filePath = filFiles[i].getPath();
			Document document = null;
			if(XmlCache.getInstance().isExists(filePath)){
				document = XmlCache.getInstance().getDocument(filePath);
			}else{
				document = CLICommonFunc.readXml(filePath);
				XmlCache.getInstance().putDocument(filePath, document);
			}
			
			//check depend element whether exists if not don't change this document.
			if(!this.checkDepend(document)){
				continue;
			}
			
			if(Operation.add == configParam.getOperation()){
				writeXml = addElement(document);
			}else if(Operation.remove == configParam.getOperation()){
				writeXml = removeElement(document);
			}else if(Operation.override == configParam.getOperation() || 
					Operation.override_attribute == configParam.getOperation()){
				writeXml = overrideElement(document);
			}else if(Operation.format == configParam.getOperation()){
				//format xml no need operate
				writeXml = true;
			}else if(Operation.add_version == configParam.getOperation() || 
					Operation.remove_version == configParam.getOperation()){
				addOrRemoveVersion(filFiles[i]);
				writeXml = false;
			}else{
				continue;
			}
			
			//document order
			if(writeXml){
				XmlCache.getInstance().putDocument(filePath, document, writeXml);
			}
		}
	}
	
	private boolean addElement(Document destDoc){
		boolean write = false;
		if(destDoc == null || configParam.getxPath() == null || configParam.getContentEles().isEmpty()){
			return write;
		}
		List<?> allNodes = destDoc.selectNodes(configParam.getxPath());
		for(Object obj : allNodes){
			if(obj instanceof Element){
				Element eleObj = (Element)obj;
				for(Element eleCld : configParam.getContentEles()){
					if(eleCld == null){
						System.out.println(configParam.getxPath());
					}
					Element copyChild = eleCld.createCopy();
					removeExistsNode(eleObj, copyChild);
					eleObj.add(copyChild);
					write = true;
				}
			}
		}
		return write;
	}
	
	private void removeExistsNode(Element parentEle, Element existsEle){
		if(parentEle == null || existsEle == null){
			return;
		}
		String curPath = ConfigToolUtil.getCurrentNodePath(existsEle);
		List<?> rmList = parentEle.selectNodes(curPath);
		if(rmList == null || rmList.isEmpty()){
			return;
		}
		for(Object obj : rmList){
			if(obj instanceof Node){
				Node nodeObj = (Node)obj;
				parentEle.remove(nodeObj);
			}
		}
	}
	
	private boolean removeElement(Document destDoc){
		boolean write = false;
		if(configParam.getxPath() == null){
			return write;
		}
		List<?> allNodes = destDoc.selectNodes(configParam.getxPath());
		for(Object obj : allNodes){
			if(obj instanceof Element){
				Element eleObj = (Element)obj;
				
				eleObj.getParent().remove(eleObj);
				write = true;
			}
		}
		return write;
	}
	
	private boolean overrideElement(Document destDoc){
		boolean write = false;
		if(configParam.getxPath() == null || configParam.getContentEles().isEmpty()){
			return write;
		}
		if(configParam.getContentEles().size() > 1){
			System.out.println("overrideElement only allow one element content.");
			return write;
		}
		Element overrideNode = configParam.getContentEles().get(0);
		Element copyChild = overrideNode.createCopy();
		List<?> allNodes = destDoc.selectNodes(configParam.getxPath());
		for(Object obj : allNodes){
			if(obj instanceof Element){
				Element eleObj = (Element)obj;
				
				if(Operation.override == configParam.getOperation()){
					Element parentEle = eleObj.getParent();
					parentEle.remove(eleObj);
					parentEle.add(copyChild);
					write = true;
				}else if(Operation.override_attribute == configParam.getOperation()){
					int size = eleObj.attributes().size();
					List<?> sourceObjs = copyChild.attributes();
					
					//remove old attribute
					for(int i=0; i<size; i++){
						eleObj.attributes().remove(0);
					}
					
					//add new attribute
					if(sourceObjs != null){
						for(Object sourceObj : sourceObjs){
							if(sourceObj instanceof Attribute){
								Attribute sourceAttr = (Attribute)sourceObj;
								eleObj.addAttribute(sourceAttr.getName(), sourceAttr.getValue());
							}
						}
					}
					write = true;
				}
			}
		}
		
		return write;
	}
	
	private void addOrRemoveVersion(File file){
		if(configParam.getOperation() == Operation.remove_version){
			file.delete();
		}else if(configParam.getOperation() == Operation.add_version){
			String fileName = file.getName();
			String newFileName = fileName.replace(configParam.getBase_version(), configParam.getVersion());
			String newPath = file.getParent() + File.separator + newFileName;
			ConfigToolUtil.copyFile(file.getPath(), newPath);
		}
	}
	
	private boolean checkDepend(Document destDoc){
		if(configParam.getDependPaths() == null || configParam.getDependPaths().isEmpty()){
			return true;
		}
		
		for(DependPathParam depObj : configParam.getDependPaths()){
			String dPath = depObj.getDependPath();
			boolean chkExists = depObj.isCheckExists();
			Node depNode = destDoc.selectSingleNode(dPath);
			boolean nodeExists = depNode != null;
			
			if((chkExists && !nodeExists) || (!chkExists && nodeExists)){
				return false;
			}
		}
		
		return true;
	}

	public static void main(String[] args) throws Exception{
		
		//load config.xml file.
		String configXmlPath = ConfigToolUtil.getConfigXmlPath();
		Document configXmlDoc = CLICommonFunc.readXml(configXmlPath);
		
		//get project path
		String projectPath = null;
		Element projectPathEle = (Element)configXmlDoc.selectSingleNode("/"+ElementName.root.getName()+
																		"/"+ElementName.project_path.getName());
		if(projectPathEle != null){
			projectPath = projectPathEle.getText();
		}else{
			projectPath = ConfigToolUtil.getProjectPath();
		}
		ConfigParam.projectPath = projectPath;
		
		//load schema.xml file.
		String schemaXmlPath = ConfigToolUtil.getCfgSchemaPath(ConfigParam.projectPath);
		GenerateVersionControlFile confile = new GenerateVersionControlFile(schemaXmlPath);
		Document schemaXmlDoc = confile.generateDoc();
		ConfigParam.schemaDoc = schemaXmlDoc;
		
		//init ConfigParam
		List<ConfigParam> allConfigParams = new ArrayList<ConfigParam>();
		List<?> childObjs = configXmlDoc.getRootElement().elements();
		if(childObjs == null){
			return;
		}
		for(Object obj : childObjs){
			if(obj instanceof Element){
				Element cldEles = (Element)obj;
				ConfigParam cfgParam = new ConfigParam(cldEles);
				if(cfgParam.getOperation() == Operation.auto){
					AutoCalculateDifference deffTool = new AutoCalculateDifference(cfgParam);
					List<ConfigParam> paramList = deffTool.generateDifference();
					if(paramList != null && !paramList.isEmpty()){
						allConfigParams.addAll(paramList);
					}
				}else{
					allConfigParams.add(cfgParam);
				}
			}
		}
		
		//generate
		for(ConfigParam cfgParam : allConfigParams){
			VersionDefaultXmlTool xmlTool = new VersionDefaultXmlTool(cfgParam);
			xmlTool.generate();
		}
		
		//write xml
		Iterator<Entry<String, DocumentInfo>> iteratorDoc = XmlCache.getInstance().getDocumentMap().entrySet().iterator();
		while(iteratorDoc.hasNext()){
			Entry<String, DocumentInfo> entryObj = iteratorDoc.next();
			String filePath = entryObj.getKey();
			DocumentInfo docInfo = entryObj.getValue();
			
			if(!docInfo.isWrite()){
				continue;
			}
			Document writeDoc = XmlOrderTool.getInstance(ConfigParam.schemaDoc).orderXml(docInfo.getDocument());
			CLICommonFunc.writeXml(writeDoc, filePath, true);
		}
		
	}
}
