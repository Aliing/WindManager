package com.ah.be.config.create.common;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.GenerateXML;
import com.ah.be.config.create.common.AhControlMultiApVer.ProductType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

/**
 * @author zhang
 * @version 2008-3-20 14:09:43
 */

public class CompleteRunXml {

	private static final Tracer log = new Tracer(CompleteRunXml.class
			.getSimpleName());
	private static final String MULTI_SELECTED = "multi-selected";
	private static final String IGNORE_STR = "*";
	private static final String DEFAULT_VALUE_PREFIX = "DefaultValue";
	private static final Map<String, Document> defValueXmlMap = Collections.synchronizedMap(new HashMap<String, Document>());

	// static{
	// try {
	// defValueXmlMap = new HashMap<String, Document>();
	// loadAllDefaultFile(AhDirTools.getDefaultValueXmlDir());
	// } catch (DocumentException e) {
	// AhTracer.CONFIG.error(CompleteRunXml.class,
	// "loadAllDefaultFile",e.getMessage());
	// }
	// }

	private Document hmDoc;
	private Document runDoc;
	private Document defDoc;
	private HiveAp hiveAp;

	public CompleteRunXml(HiveAp hiveAp)
			throws CreateXMLException {
		this.hiveAp = hiveAp;
		String productName = hiveAp.getProductName();
		String softVer = hiveAp.getSoftVer();
		String defValueKey = getKey(hiveAp, softVer);
		
		if(!defValueXmlMap.containsKey(defValueKey)){
			try{
				defDoc = loadDefaultFile(AhDirTools.getDefaultValueXmlDir() + defValueKey + ".xml");
				if(defDoc != null){
					defValueXmlMap.put(defValueKey, defDoc);
				}
			}catch(Exception e){
				log.error("CompleteRunXml", e);
			}
		}
		defDoc = defValueXmlMap.get(defValueKey);
		if (defDoc == null) {
			String[] errParam = { productName, softVer };
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.CannotSupportThisVer", errParam);
			throw new CreateXMLException(errMsg);
		}
	}

	public static Document getDefValueXml(HiveAp hiveAp, String softVer) {
		String defValueKey = getKey(hiveAp, softVer);
		
		if(!defValueXmlMap.containsKey(defValueKey)){
			try{
				Document defDoc = loadDefaultFile(AhDirTools.getDefaultValueXmlDir() + defValueKey + ".xml");
				if(defDoc != null){
					defValueXmlMap.put(defValueKey, defDoc);
				}
			}catch(Exception e){
				log.error("CompleteRunXml", e);
			}
		}
		return defValueXmlMap.get(defValueKey);
	}

	private static String getKey(HiveAp hiveAp, String softVer) {
		
		//from 6.1.6.0 no need xml
		String latestVer = AhControlMultiApVer.getLatestVersion(hiveAp);
		if(NmsUtil.compareSoftwareVersion(softVer,latestVer) > 0){
			softVer = latestVer;
		}
		
		String defValueKey;
		ProductType pType = AhControlMultiApVer.getProductType(hiveAp);
		if (pType == ProductType.Default) {
			defValueKey = pType.name();
		} else {
			defValueKey = DEFAULT_VALUE_PREFIX + "_" + pType.name() + "_"
					+ softVer;
		}
		return defValueKey;
	}
	
	public Document getRunDocument(){
		return this.runDoc;
	}

	public void fillRunXml(HiveAp hiveAp, String runXmlPath, String newXmlPath, String cParseIgnoreCfgPath)
			throws IOException, DocumentException, CreateXMLException {
		//remove running xml namespace
		String context = FileUtils.readFileToString(new File(runXmlPath));
		context = context.replaceFirst("xmlns=\"http://www.aerohive.com/configuration\"", "");
		FileUtils.writeStringToFile(new File(runXmlPath), context);
		
		// saveFileToFile(runXmlPath, runXmlPath + ".bak");
		readConfigXml(newXmlPath, runXmlPath);
		backUpRunXml(runXmlPath + ".bak");
		
		//default value fill
		treeWalkElement(hmDoc.getRootElement(), runDoc.getRootElement());
		
//		/** modify xml with ap version file */
//		AhControlMultiApVer apVer = new AhControlMultiApVer(hiveAp, hiveAp.getSoftVer(), runXmlPath, runDoc);
//		runDoc = apVer.generate();
//		
//		//read CLIs that cannot parse with C parse model.
//		List<String> cParseThrowClis = CLICommonUtils.readCLIList(cParseIgnoreCfgPath);
//		
//		//generate all CLIs with new implement
//		if(!cParseThrowClis.isEmpty()){
//			CLIParseFactory parseFactory = CLIParseFactory.getInstance();
//			XmlMergeCache xmlCache = new XmlMergeCache(runDoc);
//			xmlCache.init();
//			
//			ConstraintType consObj = CLICommonUtils.getConstraintType(hiveAp);
//			List<String> cannotParseClis = parseFactory.parseCli(
//					cParseThrowClis.toArray(new String[cParseThrowClis.size()]), xmlCache, consObj);
//			runDoc = xmlCache.getDocument();
////			xmlCache.writeXml(runXmlPath, true);
//			if(cannotParseClis != null && !cannotParseClis.isEmpty()){
//				StringBuilder sBuilder = new StringBuilder();
//				for(String retCli : cannotParseClis){
//					sBuilder.append("\n").append(retCli);
//				}
//				String[] errParams = {sBuilder.toString(), hiveAp.getHostName()};
//				String errMsg = NmsUtil.getUserMessage(
//						"error.config.cli.parsing.newImplement.failure", errParams);
//				log.error("CLIParseNewImplement", errMsg);
//			}
//		}
//		
////		//read xml
////		runDoc = CLICommonFunc.readXml(runXmlPath);
//		//auto fill element <AH-DELTA-ASSISTANT operation="yes"/>
//		runDoc = FillAssistantElementAuto.getInstance().fillElement(runDoc);
//		// xml element sort
//		runDoc = XmlOrderTool.getInstance().orderXml(runDoc);
//		//write xml
//		CLICommonFunc.writeXml(runDoc, runXmlPath, true);
	}

	private void readConfigXml(String hmConfigPath, String runConfigPath)
			throws DocumentException, IOException {
		hmDoc = CLICommonFunc.readXml(hmConfigPath);
		runDoc = CLICommonFunc.readXml(runConfigPath);
	}

	private void backUpRunXml(String backRunPath) throws IOException {
		CLICommonFunc.writeXml(runDoc, backRunPath, false);
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
				if (isCopyElement(childElementHm, runElement)) {
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

	private boolean isCopyElement(Element hmElement, Element runElement) {
		String xPath = getHmElementPath(hmElement);
		Node node = defDoc.selectSingleNode(xPath);

		// System.out.println(xPath);
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
			String attrFromDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_FROM);
			String attrToDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_TO);
			String attrContainsDef = defElement
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_CONTAIN);
			boolean isCopy = false;

			if ( isObjectEquals(attrValueDef, attrValueHm)
					&& (isObjectEquals(attrNameHm, attrNameDef) || isContainsAttr(attrNameHm, attrContainsDef) 
							|| isInRangeAttr(attrNameHm, attrFromDef, attrToDef) )
					&& (attrOperationDef == null || (attrOperationDef
							.equals(attrOperationHm))) ) {
				isCopy = true;
			}

			if (attrMultiSelect != null && isCopy) {
				String[] multiEle = attrMultiSelect.split("\\|");
				// String xPathMul = getHmElementPath(hmElement.getParent());

//				boolean isFoundMultiEle = false;
				for (String e : multiEle) {
					// System.out.println(xPathMul + "/" + e);
					if (isExistsChildEle(runElement, e)) {
//						isFoundMultiEle = true;
						isCopy = false;
						break;
					}
				}
//				if (!isFoundMultiEle && attrOperationDef == null) {
//					isCopy = false;
//				}
			}
			// System.out.println(isCopy);
			return isCopy;
		} else {
			return false;
		}
	}
	
	private boolean isObjectEquals(String obj1, String obj2){
		return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2));
	}
	
	private boolean isContainsAttr(String attr, String defContains){
		if(attr == null || defContains == null){
			return false;
		}
		try{
			String[] containsArg = defContains.split(GenerateXML.CONTAIN_SPLIT);
			for(int i=0; i<containsArg.length; i++){
				if(attr.equals(containsArg[i].trim())){
					return true;
				}
			}
			return false;
		}catch(Exception e){
			log.error(CompleteRunXml.class.getSimpleName(), e);
			return false;
		}
	}
	
	private boolean isInRangeAttr(String attr, String from, String to){
		if(attr == null || from == null || to == null){
			return false;
		}
		try{
			int attrInt = Integer.valueOf(attr.trim());
			int fromInt = Integer.valueOf(from.trim());
			int toInt = Integer.valueOf(to.trim());
			return attrInt >= fromInt && attrInt <= toInt;
		}catch(Exception e){
			log.error(CompleteRunXml.class.getSimpleName(), e);
			return false;
		}
	}

	private boolean isExistsChildEle(Element element, String childName) {
		if (childName == null) {
			return false;
		}
		Iterator<?> childIte = element.elementIterator();
		while (childIte.hasNext()) {
			Element childElementHm = (Element) childIte.next();
			if (childName.equals(childElementHm.getName())) {
				return true;
			}
		}
		return false;
	}

	private String getHmElementPath(Element hmEle) {
		return getHmElementPath(hmEle, this.defDoc);
	}
	
	public static String getHmElementPath(Element hmEle, Document defDoc){
		LinkedList<ElementPath> pathList = new LinkedList<ElementPath>();
		int nameCount = 0;

		while (true) {
			String elementName = hmEle.getName();
			String attrName = hmEle
					.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);

			ElementPath elePath = new ElementPath();
			elePath.setElementName(elementName);
			if (attrName != null) {
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
		
		//match name="*"
		if(nameCount > 0 && !isNodeExists(defDoc, pathList)){
			Iterator<ElementPath> pathItem =  pathList.iterator();
			while(pathItem.hasNext()){
				ElementPath elePath = pathItem.next();
				if(elePath.getAttrName() != null){
					elePath.setAttrName(IGNORE_STR);
					if(isNodeExists(defDoc, pathList)){
						break;
					}
				}
			}
		}

		StringBuilder xPathBuf = new StringBuilder();
		for (ElementPath elePath : pathList) {
			xPathBuf.append(elePath.getPath());
		}
		return xPathBuf.toString();
	}
	
	private static boolean isNodeExists(Document defDoc, List<ElementPath> pathList){
		if(defDoc == null || pathList == null || pathList.isEmpty()){
			return false;
		}
		StringBuilder xPathBuf = new StringBuilder();
		for (ElementPath elePath : pathList) {
			xPathBuf.append(elePath.getPath());
		}
		Node node = defDoc.selectSingleNode(xPathBuf.toString());
		return node != null;
	}

	private static class ElementPath {
		private String elementName;
		private String attrName;

//		public String getElementName() {
//			return elementName;
//		}

		public void setElementName(String elementName) {
			this.elementName = elementName;
		}

		public String getAttrName() {
			return this.attrName;
		}

		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}

		public String getPath() {
			if (attrName == null) {
				return "/" + elementName;
			} else {
				return "/" + elementName + "[@"
						+ XmlOrderTool.getPathAttrContext(GenerateXML.ATTRIBUTE_NAME_NAME, attrName)
						+ "]";
			}
		}
	}

	// void treeWalkSingEle(Element ele) {
	// if (ele.elements().size() == 0) {
	// System.out.println(getHmElementPath(ele));
	// }
	//
	// for (Iterator<?> ite = ele.elementIterator(); ite.hasNext();) {
	// Element eleChild = (Element) ite.next();
	// treeWalkSingEle(eleChild);
	// }
	// }


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

	/**
	 * Load all cli default value configuration to memory
	 * 
	 * @param xmlFolderPath
	 *            : the path of default value configuration
	 * @throws DocumentException
	 *             -
	 */
	public static synchronized void loadAllDefaultFile(String xmlFolderPath)
			throws DocumentException {
		if (!defValueXmlMap.isEmpty()) {
			return;
		}

		File defaultFile = new File(xmlFolderPath);
		File[] allDefFile = defaultFile
				.listFiles(new AhControlMultiApVer.XmlFile());
		SAXReader reader = new SAXReader();

		for (File defFile : allDefFile) {
			Document xmlDocument = reader.read(defFile);
			String fileName = defFile.getName();
			String key = fileName.substring(0, fileName
					.indexOf(AhControlMultiApVer.XML_FILE_SUFIX));
			defValueXmlMap.put(key, xmlDocument);
		}
	}
	
	public static Document loadDefaultFile(String filePath) throws DocumentException{
		return CLICommonFunc.readXml(filePath);
	}

	private void specialOperation() {
		/**
		 * special operation for hostname
		 */
		String hostName = hiveAp.getHostName();
		String defHostName = "AH-"
				+ hiveAp.getMacAddress().substring(
						hiveAp.getMacAddress().length() - 6);
		boolean isDefHostName = hostName.equalsIgnoreCase(defHostName);

		if (isDefHostName) {
			Iterator<?> iteEle = runDoc.getRootElement().elementIterator();
			boolean isExistsHostName = false;
			while (iteEle.hasNext()) {
				Element childEle = (Element) iteEle.next();
				if ("hostname".equals(childEle.getName())) {
					String value = childEle.attributeValue("value");
					if (defHostName.equalsIgnoreCase(value)) {
						childEle.remove(childEle.attribute("value"));
						childEle.addAttribute("value", hostName);
					}
					isExistsHostName = true;
				}
			}
			if (!isExistsHostName) {
				Element hostNameEle = runDoc.getRootElement().addElement(
						"hostname");
				hostNameEle.addAttribute("value", hostName);
				hostNameEle.addAttribute("operation", "yes");
			}
		}
	}

	public static String getElementPath(Element ele) {
		Element element = ele;
		String elePath = "";
		while (true) {
			elePath = getNodePath(element) + elePath;
			if (element.isRootElement()) {
				break;
			}
			element = element.getParent();
		}
		return elePath;
	}

	private static String getNodePath(Element ele) {
		String nodePath = "/" + ele.getName();
		String attrName = ele.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
		if (attrName != null) {
			nodePath += "[@" + XmlOrderTool.getPathAttrContext(GenerateXML.ATTRIBUTE_NAME_NAME, "*") + "]";
		}
		return nodePath;
	}
	
	public static Map<String, Document> getDefValueXmlMap() {
		return defValueXmlMap;
	}
	
	public void fillDefaultValueForTest(String runXmlPath, String newXmlPath)
			throws IOException, DocumentException, CreateXMLException {
		readConfigXml(newXmlPath, runXmlPath);
		backUpRunXml(runXmlPath + ".bak");
		//runDoc = FillAssistantElementAuto.getInstance().fillElement(runDoc);
		//default value fill
		treeWalkElement(hmDoc.getRootElement(), runDoc.getRootElement());
		
		CLICommonFunc.writeXml(runDoc, runXmlPath, false);
		//AhControlMultiApVer apVer = new AhControlMultiApVer(hiveAp, hiveAp.getSoftVer(), runXmlPath, runDoc);
		//apVer.generate();
	}

//	public static void main(String[] args) throws IOException,
//			DocumentException, CreateXMLException {
//		 String defPath = "d:/test/DefaultValue_SR_24_6.0.1.0.xml";
//		 String newPath = "d:/test/08EA4466DA00_full_new.xml";
//		 String runPath = "d:/test/08EA4466DA00_full_run.xml";
//		 String cParseIgnore = "d:/test/08EA4466DA00_full_cparse_ignore.config";
//		 CompleteRunXml fill = new CompleteRunXml(runPath, newPath, defPath, cParseIgnore);
//	}

}