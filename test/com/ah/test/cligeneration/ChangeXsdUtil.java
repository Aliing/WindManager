package com.ah.test.cligeneration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.config.create.CLICommonFunc;

public class ChangeXsdUtil {
 
	private static final String XSD_PATH = "D:/workspace/hivemanager-ed2/webapps/schema/versionSchema";
		
	private List<File> getXsdFileList(String xsdFileName, final String versionNo) {
		List<File> list = new ArrayList<File>();
		File file = new File(XSD_PATH);
		String[] xsdDirNames = file.list(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				String fileVersion = name.substring(name.lastIndexOf("_") + 1);
				if (fileVersion.compareTo(versionNo) < 0) {
					return false;
				}
//				if (!name.endsWith("BranchRouter_200_WP_6.0.2.0")) {
//					return false;
//				}
				return true;
			}});
		//String[] xsdDirNames = file.list();
		for (String xsdDirName : xsdDirNames) {
			File dir = new File(XSD_PATH + "/" + xsdDirName);
			String[] xsdFileNames = dir.list();
			for (String fileName : xsdFileNames) {
				if (xsdFileName.equals(fileName)) {
					list.add(new File(XSD_PATH + "/" + xsdDirName + "/" + fileName));
				}
			}
		}
		return list;
	}
		
	private void fillSingleFile(File file) throws Exception {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element root = document.getRootElement();
			Element parentElement = (Element) root.selectSingleNode("xsd:complexType[@name='bonjour-gateway-filter-rule']");
			if (parentElement != null) {
				Element assistantElement = (Element) parentElement.selectSingleNode("xsd:sequence/xsd:element[@name='AH-DELTA-ASSISTANT']");
				if (assistantElement != null) { //already exists
					return;
				}
				Element beforeElement = (Element) parentElement.selectSingleNode("xsd:sequence/xsd:element[@type='userProfile:bonjour-gateway-rule-before']");
				if (beforeElement == null) {
					return;
				}			
				
				Element newElement = beforeElement.getParent().addElement("xsd:element");
				newElement.addAttribute("name", "AH-DELTA-ASSISTANT");
				newElement.addAttribute("type", "general:ah-name");
				newElement.addAttribute("minOccurs", "0");
				CLICommonFunc.writeXml(document, file.getAbsolutePath(), false);
			}			

		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	private void fillUserGroupVoiceDevice(File file) throws Exception {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element root = document.getRootElement();
			Element parentElement = (Element) root.selectSingleNode("xsd:complexType[@name='user-group-obj']");
			if (parentElement != null) {
				Element newElement = (Element) parentElement.selectSingleNode("xsd:sequence/xsd:element[@name='voice-device']");
				if (newElement != null) { //already exists
					return;
				}	
				newElement = ((Element)parentElement.selectSingleNode("xsd:sequence")).addElement("xsd:element");
				newElement.addAttribute("name", "voice-device");
				newElement.addAttribute("type", "general:ah-only-act");
				newElement.addAttribute("minOccurs", "0");
				CLICommonFunc.writeXml(document, file.getAbsolutePath(), false);
				System.out.println(file.getAbsolutePath());
			}			

		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void execute(String xsdFileName) throws Exception {
		List<File> list = this.getXsdFileList("others.xsd", "6.0.1.0");
		for (File file : list) {
 			//fillSingleFile(file);
			fillUserGroupVoiceDevice(file);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String xsdFileName = "userProfile.xsd";
		ChangeXsdUtil ob = new ChangeXsdUtil();
		ob.execute(xsdFileName);

	}

}
