package com.ah.be.config.fill_def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ah.be.config.create.GenerateXML;

/**
 * @author zhang
 * @version 2008-3-20 14:09:43
 */

public class CompleteRunXml {

	private static final String MULTI_SELECTED = "multi-selected";
	private static final String IGNORE_STR = "*";

	private final String defValueXmlPath;
	private Document hmDoc;
	private Document runDoc;
	private Document defDoc;
	private String runXmlPath;
	private String newXmlPath;

	public CompleteRunXml(String defValueXmlPath) {
		this.defValueXmlPath = defValueXmlPath;
	}

	public void fillRunXml(String runXmlPath, String newXmlPath)
			throws IOException, DocumentException {
		this.runXmlPath = runXmlPath;
		this.newXmlPath = newXmlPath;
		saveFileToFile(runXmlPath, runXmlPath + ".bak");
		readXml();
		treeWalkElement(hmDoc.getRootElement(), runDoc.getRootElement());
		writeRunXml();
	}

	private void readXml() throws DocumentException {
		SAXReader reader = new SAXReader();
		hmDoc = reader.read(new File(newXmlPath));
		runDoc = reader.read(new File(runXmlPath));
		defDoc = reader.read(new File(defValueXmlPath));
	}

	private void writeRunXml() throws IOException {
		XMLWriter writer = new XMLWriter(new FileWriter(new File(runXmlPath)));
		writer.write(runDoc);
		writer.close();
	}

	private void treeWalkElement(Element hmElement, Element runElement) {
		for (Iterator<?> hmIte = hmElement.elementIterator(); hmIte.hasNext();) {
			Element childElementHm = (Element) hmIte.next();
			Element childElementRun = null;
			boolean isFound = false;

			for (Iterator<?> RunIte = runElement.elementIterator(); RunIte
					.hasNext();) {
				childElementRun = (Element) RunIte.next();

				if (isSameElement(childElementHm, childElementRun)) {
					isFound = true;
					break;
				}
			}

			if (isFound) {
				treeWalkElement(childElementHm, childElementRun);
			} else {
				if (isCopyElement(childElementHm)) {
					Element runEle = runElement.addElement(childElementHm
							.getName());
					String value = childElementHm
							.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
					String name = childElementHm
							.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
					String operation = childElementHm
							.attributeValue(GenerateXML.ATTRIBUTE_NAME_OPERATION);

					if (value != null) {
						runEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_VALUE,
								value);
					}

					if (name != null) {
						runEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME,
								name);
					}

					if (operation != null) {
						runEle
								.addAttribute(
										GenerateXML.ATTRIBUTE_NAME_OPERATION,
										operation);
					}

					treeWalkElement(childElementHm, runEle);
				}
			}
		}
	}

	private boolean isSameElement(Element newEle, Element runEle) {
		if (newEle == null && runEle == null) {
			return true;
		} else if (newEle == null) {
			return false;
		} else if (runEle == null) {
			return false;
		}

		if (!newEle.getName().equals(runEle.getName())) {
			return false;
		}

		String newNameAttr = newEle
				.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
		String runNameAttr = runEle
				.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);

		return newNameAttr == null && runNameAttr == null
				|| !(newNameAttr != null && runNameAttr == null)
				&& newNameAttr != null && newNameAttr.equals(runNameAttr);
	}

	private boolean isCopyElement(Element hmElement) {
		String xPath = getHmElementPath(hmElement);
		Node node = defDoc.selectSingleNode(xPath);

		System.out.println(xPath);
		if (node instanceof Element) {
			Element defElement = (Element) node;
			String attrValueDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
			String attrOperationDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_OPERATION);
			String attrNameDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
			String attrMultiSelect = defElement.attributeValue(MULTI_SELECTED);
			String attrValueHm = hmElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
			String attrOperationHm = hmElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_OPERATION);
			String attrNameHm = hmElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
			boolean isCopy = false;

			if (
				((attrValueDef == null && attrValueHm == null) || (attrValueDef != null && attrValueDef
					.equals(attrValueHm)))
					&& ((attrNameDef == null && attrNameHm == null) || (attrNameDef != null && attrNameDef
							.equals(attrNameHm)))
					&& (attrOperationDef == null || (attrOperationDef
							.equals(attrOperationHm)))
				) {
				System.out.println(true);
				isCopy = true;
			}

			if (attrMultiSelect != null && isCopy) {
				String[] multiEle = attrMultiSelect.split("\\|");
				String xPathMul = getHmElementPath(hmElement.getParent());

				for (String e : multiEle) {
					if (runDoc.selectSingleNode(xPathMul + "/" + e) instanceof Element) {
						isCopy = false;
						break;
					}
				}
			}

			return isCopy;
		} else {
			return false;
		}
	}
	
	private String getHmElementPath(Element hmEle) {
		LinkedList<ElementPath> pathList = new LinkedList<ElementPath>();
		int nameCount = 0;
		StringBuffer xmlPath = new StringBuffer();

		while (true) {
			String elementName = hmEle.getName();
			String attrName = hmEle.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
			
			ElementPath elePath = new ElementPath();
			elePath.setElementName(elementName);
			if(attrName != null){
				elePath.setAttrName(attrName);
				nameCount++;
			}
			pathList.addFirst(elePath);

			if (hmEle.isRootElement()) {
				break;
			} else {
				hmEle = hmEle.getParent();
			}
		}
		
		if(nameCount > 0){
			boolean TempTure = true;
			for (ElementPath elePath : pathList) {
				String nameValue = elePath.getAttrName();
				if (nameValue != null && TempTure) {
					Node node = defDoc.selectSingleNode(xmlPath.toString() + elePath.getPath());
					if (node == null) {
						//set name to "*"
						elePath.setAttrName(IGNORE_STR);
						node = defDoc.selectSingleNode(xmlPath.toString() + elePath.getPath());
						if (node == null) {
							elePath.setAttrName(nameValue);
							TempTure = false;
						} else {
							xmlPath.append(elePath.getPath());
						}
					} else {
						xmlPath.append(elePath.getPath());
					}
				} else {
					xmlPath.append(elePath.getPath());
				}
			}
		}
		
		StringBuffer xPathBuf = new StringBuffer();
		for(ElementPath elePath : pathList){
			xPathBuf.append(elePath.getPath());
		}
		return xPathBuf.toString();
	}
	
	private static class ElementPath{
		private String elementName;
		private String attrName;
		
//		public String getElementName(){
//			return elementName;
//		}
		
		public void setElementName(String elementName){
			this.elementName = elementName;
		}
		
		public String getAttrName(){
			return this.attrName;
		}
		
		public void setAttrName(String attrName){
			this.attrName = attrName;
		}
		
		public String getPath(){
			if(attrName == null){
				return "/" + elementName;
			}else{
				return "/" + elementName + "[@" + GenerateXML.ATTRIBUTE_NAME_NAME + "='" + attrName
						+ "']";
			}
		}
	}

//	void treeWalkSingEle(Element ele) {
//		if (ele.elements().size() == 0) {
//			System.out.println(getHmElementPath(ele));
//		}
//
//		for (Iterator<?> ite = ele.elementIterator(); ite.hasNext();) {
//			Element eleChild = (Element) ite.next();
//			treeWalkSingEle(eleChild);
//		}
//	}

	public static void formatXml(String xmlPath) throws IOException,
			DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(xmlPath));
		treeWalkFormat(document.getRootElement());

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(new File(xmlPath)),
				format);
		writer.write(document);
		writer.close();
	}

	private static void treeWalkFormat(Element element) {
		Attribute updateTime = element.attribute("updateTime");
		Attribute name = element.attribute("name");
		Attribute value = element.attribute("value");
		Attribute operation = element.attribute("operation");

		if (updateTime != null) {
			element.remove(updateTime);
		}

		if (name != null) {
			element.remove(name);
		}

		if (value != null) {
			element.remove(value);
		}

		if (operation != null) {
			element.remove(operation);
		}

		if (name != null) {
			element.add(name);
		}

		if (value != null) {
			element.add(value);
		}

		if (operation != null) {
			element.add(operation);
		}

		for (Iterator<?> ite = element.elementIterator(); ite.hasNext();) {
			Element childEle = (Element) ite.next();
			treeWalkFormat(childEle);
		}
	}

	private void saveFileToFile(String file1, String file2) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(file1);
			fos = new FileOutputStream(file2);

			byte[] buffer = new byte[10000];
			int len;

			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}

			if (fos != null) {
				fos.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, DocumentException{
//		String defPath = "D:/Java/apache-tomcat-6.0.13/webapps/hm/schema/def/default_value_3.0.1.0.xml";
//		String newPath = "D:/Java/apache-tomcat-6.0.13/webapps/hm/WEB-INF/downloads/script/xml/view/ZHANG_TEST_new.xml";
//		String runPath = "D:/Java/apache-tomcat-6.0.13/webapps/hm/WEB-INF/downloads/script/xml/old/zhang_test_old.xml";
		String defPath = "D:/test/default_value_3.0.1.0.xml";
		String newPath = "d:/test/001977002710_new.xml";
		String runPath = "d:/test/001977002710_run.xml";
		CompleteRunXml fill = new CompleteRunXml(defPath);
		fill.fillRunXml(runPath, newPath);
	}

}