package com.ah.be.config.create.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.GenerateXML;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

/**
 * @author zhang
 * @version 2008-5-21 15:12:33
 */

public class AhControlMultiApVer {

	private static final Tracer log = new Tracer(AhControlMultiApVer.class
			.getSimpleName());

	private static Map<String, Document> verXmlMap = new HashMap<>();
	public static final String XML_FILE_SUFIX = ".xml";
	public static final String LATEST_XML_VERSION = "6.1.6.0";

	private static String fristVer, lastVer;

	private Set<String> xPathSet;

	public static enum ProductType {
		Default, schema, HiveAp_ag, HiveAp_11n, HiveAp_120, HiveAp_121, HiveAp_170, HiveAp_110, HiveAp_330,HiveAp_370, HiveAp_390, HiveAp_100, HiveAp_cvg,HiveAp_230,
		VPN_Gateway, BranchRouter_330, BranchRouter_100, BranchRouter_200, BranchRouter_200_WP, BranchRouter_200_WP_LTE,
		VPN_BR_200, VPN_BR_200_WP, VPN_BR_200_WP_LTE, VPN_BR_330, SR_24
	}

	// static{
	// try {
	// loadAllVerFile();
	// } catch (DocumentException e) {
	// AhTracer.CONFIG.error(AhControlMultiApVer.class,
	// "loadAllVerFile",e.getMessage());
	// }
	// }

//	private String cfgXmlPath;
	private Document cfgDocument, verDocument;

	/**
	 *
	 * @param hiveAp -
	 * @param softVer HiveAp.softVer
	 * @param cfgXmlPath The XML that need filter.
	 * @throws DocumentException -
	 * @throws CreateXMLException -
	 */
	public AhControlMultiApVer(HiveAp hiveAp, Document cfgDocument) throws CreateXMLException, DocumentException {
		ProductType pType = getProductType(hiveAp);
		xPathSet = new HashSet<String>();
		
		this.cfgDocument = cfgDocument;
		verDocument = getVerDocument(pType, hiveAp.getSoftVer());
	}

	public static boolean checkHiveApVersion(HiveAp hiveAp, StringBuffer errorMsg) throws DocumentException{
		//from 6.1.6.0 no need xml
		String softVer = hiveAp.getSoftVer();
		String latestVer = getLatestVersion(hiveAp);
		if(NmsUtil.compareSoftwareVersion(softVer, latestVer) > 0){
			softVer = latestVer;
		}
		
		ProductType apType = getProductType(hiveAp);

		String key;
		if (apType == null || apType == ProductType.Default) {
			key = ProductType.Default.name();
		}else if(apType == ProductType.schema){
			key = ProductType.schema.name();
		} else {
			key = apType.name() + "_" + softVer;
		}

		//no need load all xml, only small part of them be used.
//		if (verXmlMap == null || verXmlMap.isEmpty()) {
//			try {
//				loadAllVerFile();
//			} catch (DocumentException e) {
//				log.error("loadAllVerFile", e.getMessage());
//			}
//		}

		if(!verXmlMap.containsKey(key)){
			loadVerFile(key + XML_FILE_SUFIX);
		}
		if (!verXmlMap.containsKey(key)) {
//			loadVerFile(key + XML_FILE_SUFIX);
			if(softVer == null || "".equals(softVer)){
				errorMsg.append(NmsUtil.getUserMessage(
						"error.be.config.create.NullVersion"));
				return false;
			}
			if(softVer.compareTo(lastVer) > 0){
				String[] errParam = {hiveAp.getProductName() + "  " + softVer,
						lastVer};
				errorMsg.append(NmsUtil.getUserMessage(
						"error.be.config.create.VersionTooNew", errParam));
				return false;
			}
			if(softVer.compareTo(fristVer) < 0){
				String[] errParam = {hiveAp.getProductName() + "  " + softVer,
						fristVer, lastVer};
				errorMsg.append(NmsUtil.getUserMessage(
						"error.be.config.create.VersionTooOld", errParam));
				return false;
			}

			String[] noFile =  new String[]{hiveAp.getProductName() + "  " + softVer };
			errorMsg.append(NmsUtil.getUserMessage(
					"error.be.config.create.loseVersionCtlFile", noFile));
			return false;
		}else{
			return true;
		}
	}
	
	public static String getLatestVersion(ProductType pType){
		if(pType == ProductType.HiveAp_230){
			return "6.2.1.0";
		}else{
			return LATEST_XML_VERSION;
		}
	}
	
	public static String getLatestVersion(HiveAp hiveAp){
		ProductType pType = getProductType(hiveAp);
		return getLatestVersion(pType);
	}
	
	public static Document getAllSchemaXml(){
		try{
			String key = ProductType.schema.name();
			if(!verXmlMap.containsKey(key) || verXmlMap.get(key) == null){
				loadVerFile(key + XML_FILE_SUFIX);
			}
			return verXmlMap.get(key);
		}catch(Exception e){
			log.error("AhControlMultiApVer.getAllSchemaXml()", e);
		}
		return null;
	}

	public static ProductType getProductType(HiveAp hiveAp) {
		short apMode = hiveAp.getHiveApModel();
		switch(apMode){
			case HiveAp.HIVEAP_MODEL_20 :
			case HiveAp.HIVEAP_MODEL_28 :
				return ProductType.HiveAp_ag;
			case HiveAp.HIVEAP_MODEL_120:
				return ProductType.HiveAp_120;
			case HiveAp.HIVEAP_MODEL_121:
			case HiveAp.HIVEAP_MODEL_141:
				return ProductType.HiveAp_121;
			case HiveAp.HIVEAP_MODEL_170:
				return ProductType.HiveAp_170;
			case HiveAp.HIVEAP_MODEL_110:
				return ProductType.HiveAp_110;
			case HiveAp.HIVEAP_MODEL_320:
			case HiveAp.HIVEAP_MODEL_340:
			case HiveAp.HIVEAP_MODEL_380:
				return ProductType.HiveAp_11n;
			case HiveAp.HIVEAP_MODEL_330:
			case HiveAp.HIVEAP_MODEL_350:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
					return ProductType.HiveAp_330;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
					return ProductType.BranchRouter_330;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
					return ProductType.VPN_BR_330;
				}
			case HiveAp.HIVEAP_MODEL_370:
			case HiveAp.HIVEAP_MODEL_390:
				return ProductType.HiveAp_370;
			case HiveAp.HIVEAP_MODEL_230:
				return ProductType.HiveAp_230;
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
					return ProductType.HiveAp_cvg;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
					return ProductType.VPN_Gateway;
				}
			case HiveAp.HIVEAP_MODEL_BR100:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
					return ProductType.HiveAp_100;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
					return ProductType.BranchRouter_100;
				}
			case HiveAp.HIVEAP_MODEL_BR200:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
					return ProductType.BranchRouter_200;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
					return ProductType.VPN_BR_200;
				}
			case HiveAp.HIVEAP_MODEL_BR200_WP:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
					return ProductType.BranchRouter_200_WP;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
					return ProductType.VPN_BR_200_WP;
				}
			case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
				if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
					return ProductType.BranchRouter_200_WP_LTE;
				}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
					return ProductType.VPN_BR_200_WP_LTE;
				}
			case HiveAp.HIVEAP_MODEL_SR24:
			case HiveAp.HIVEAP_MODEL_SR2024P:
			case HiveAp.HIVEAP_MODEL_SR2124P:
			case HiveAp.HIVEAP_MODEL_SR2148P:
			case HiveAp.HIVEAP_MODEL_SR48:
				return ProductType.SR_24;
			default:
				return ProductType.Default;
		}
	}

	/**
	 * overview: use this function, it will filter the elements that not allowed
	 * in version control file.
	 *
	 * @return -
	 * @throws IOException -
	 */
	public Document generate() throws IOException {
//		long time = System.currentTimeMillis();
		xPathSet.clear();
		treeWalk(cfgDocument.getRootElement());
		xPathSet.clear();
//		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@----AllTime: ", String.valueOf(System.currentTimeMillis() - time));
		// FillAssistantElementAutomatic();
//		writeXml();
		return cfgDocument;
	}

	/**
	 * overview: load all version control to buffer map, file simple name is the
	 * key of the map file document is the value of the map.
	 *
	 * @throws DocumentException -
	 */
	public static synchronized void loadAllVerFile() throws DocumentException {
		if(verXmlMap != null && !verXmlMap.isEmpty()){
			return;
		}
		File fileVer = new File(AhDirTools.getVersionXMLDir());
		File[] allFile = fileVer.listFiles(new XmlFile());
		verXmlMap = new HashMap<String, Document>();
		SAXReader reader = new SAXReader();

		for (File file : allFile) {
			Document xmlDocument = reader.read(file);
			String fileName = file.getName();
			String key = fileName
					.substring(0, fileName.indexOf(XML_FILE_SUFIX));
			loadVerRange(key);
			verXmlMap.put(key, xmlDocument);
		}
	}

	public static synchronized void loadVerFile(String fName) throws DocumentException {
		String key = fName.substring(0, fName.indexOf(XML_FILE_SUFIX));
		if(verXmlMap.containsKey(key) && verXmlMap.get(key) != null){
			return;
		}
		
		File fileVer = new File(AhDirTools.getVersionXMLDir() + fName);
		if(!fileVer.isFile()){
			return;
		}
		SAXReader reader = new SAXReader();
		Document xmlDocument = reader.read(fileVer);
		
		loadVerRange(key);
		verXmlMap.put(key, xmlDocument);
	}

	private static void loadVerRange(String verStr){

		if(verStr == null){
			return;
		}

		verStr = verStr.replace(ProductType.HiveAp_11n.name() + "_", "");
		verStr = verStr.replace(ProductType.HiveAp_ag.name() + "_", "");
		verStr = verStr.replace(ProductType.Default.name(), "");
		verStr = verStr.replace(ProductType.schema.name(), "");

		if(fristVer == null || "".equals(fristVer)){
			fristVer = verStr;
		}

		if(lastVer == null || "".equals(lastVer)){
			lastVer = verStr;
		}

		if(verStr.compareTo(lastVer) >0){
			lastVer = verStr;
		}

		if(verStr.compareTo(fristVer) < 0){
			fristVer = verStr;
		}
	}

	private Document getVerDocument(ProductType pType, String softVer)
			throws CreateXMLException, DocumentException {
		
		//from 6.1.6.0 no need xml
		String latestVer = getLatestVersion(pType);
		if(NmsUtil.compareSoftwareVersion(softVer, latestVer) > 0){
			softVer = latestVer;
		}
		
		/**
		 * the key is product type name + version
		 */
		String key;
		if (pType == null || pType == ProductType.Default) {
			key = ProductType.Default.name();
		} else {
			key = pType.name() + "_" + softVer;
		}

//		if (verXmlMap == null || verXmlMap.isEmpty()) {
//			try {
//				loadAllVerFile();
//			} catch (DocumentException e) {
//				log.error("loadAllVerFile", e.getMessage());
//			}
//		}
		
		if(!verXmlMap.containsKey(key)){
			loadVerFile(key + XML_FILE_SUFIX);
		}

		Object docObj = verXmlMap.get(key);
		if (docObj == null) {
//			String[] errParam = {softVer};
//			String errMsg = NmsUtil.getUserMessage(
//					"error.be.config.create.CannotSupportThisVer", errParam);
			throw new CreateXMLException();
		}

		return (Document) docObj;
	}

	/**
	 * overview: tree walk the every element in the xml document, and do some
	 * operate for each.
	 *
	 * @param cfgEle -
	 */
	private void treeWalk(Element cfgEle) {
		/**
		 * Remove iterative element in configuration xml.
		 */
		String eleXPath = getHmElementPath(cfgEle);
		if(xPathSet.contains(eleXPath)){
			cfgEle.getParent().remove(cfgEle);
			return;
		}else{
			xPathSet.add(eleXPath);
		}
		
		/**
		 * special Handle Element
		 */
		specialHandleElement(cfgEle);

		/**
		 * If the element not exists in version control file, delete the
		 * element.
		 */
		Node node = verDocument.selectSingleNode(cfgEle.getPath());
		if (node instanceof Element) {
			/**
			 * merge every operation attribute for every version element
			 */
			uniteAttrOperation(cfgEle, (Element) node);

			Iterator<?> eleIte = cfgEle.elementIterator();
			while (eleIte.hasNext()) {
				treeWalk((Element) eleIte.next());
			}
		} else {
			cfgEle.getParent().remove(cfgEle);
//			removeElement(cfgEle);
		}
	}

	/*-
	private void removeElement(Element cfgEle){
		if(cfgEle.isRootElement()){
			return;
		}
		Element parentEle = cfgEle.getParent();
		parentEle.remove(cfgEle);
		if(parentEle.elements().size() > 0){
			return;
		}else{
			removeElement(parentEle);
		}
	}*/

	private void uniteAttrOperation(Element cfgEle, Element verEle) {
		Attribute cfgOpt = cfgEle
				.attribute(GenerateXML.ATTRIBUTE_NAME_OPERATION);
		Attribute verOpt = verEle
				.attribute(GenerateXML.ATTRIBUTE_NAME_OPERATION);

		if (cfgOpt != null) {
			if (verOpt == null) {
				cfgEle.remove(cfgOpt);
			} else {
				String cfgOptValue = cfgOpt.getValue();
				String verOptValue = verOpt.getValue();
				if (!verOptValue.contains("|")
						&& !OperationType.isSameType(cfgOptValue, verOptValue)) {
					cfgOptValue = OperationType.turnToGoalType(cfgOptValue,
							OperationType.getOperationType(verOptValue));
					cfgOpt.setValue(cfgOptValue);
				}
			}
		}
	}

	/**
	 * overview: generate the element's xpath in the xml file.
	 *
	 * @param hmEle the end element of the result path.
	 * @return The result is the path of current element to root element(e.g.
	 *          /configuration/ssid[@name='ssid_test']/hide-ssid)
	 */
	public static String getHmElementPath(Element hmEle) {
		if (hmEle == null) {
			return null;
		}

		String elePath = null, elementName, attrName, localPath;
		while (true) {
			elementName = hmEle.getName();
			attrName = hmEle.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
			localPath = "/" + elementName;

			if (attrName != null && !"".equals(attrName)) {
				attrName = attrName.replace("\'", "&apos;");
				localPath = localPath + "[@" + XmlOrderTool.getPathAttrContext(GenerateXML.ATTRIBUTE_NAME_NAME, attrName) + "]";
			}

			if (elePath == null) {
				elePath = localPath;
			} else {
				elePath = localPath + elePath;
			}

			if (hmEle.isRootElement()) {
				break;
			} else {
				hmEle = hmEle.getParent();
			}
		}

		return elePath;
	}

	public static class XmlFile implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(XML_FILE_SUFIX);
		}
	}

	public void FillAssistantElementAutomatic() {
		List<?> listNode = verDocument.selectNodes("//AH-DELTA-ASSISTANT");
		for (Object assNode : listNode) {
			Element assEle = (Element) assNode;
			createAssistantElement(assEle, cfgDocument);
		}
		// System.out.println("FillAssistantElementAutomatic running time: " +
		// (System.currentTimeMillis() - startTime));
	}

	private void createAssistantElement(Element assEle, Document cfgDocument) {
		if (assEle == null || cfgDocument == null) {
			return;
		}
		LinkedList<Element> assList = new LinkedList<Element>();
		while (true) {
			assList.addFirst(assEle);
			if (assEle.isRootElement()) {
				break;
			} else {
				assEle = assEle.getParent();
			}
		}

		// Element parentElement = cfgDocument.getRootElement();
		for (Element ele : assList) {
			Attribute attrName = ele.attribute(GenerateXML.ATTRIBUTE_NAME_NAME);
			List<?> eleChildList = cfgDocument.selectNodes(ele.getPath());

			if (eleChildList.size() == 0 && attrName != null) {
				// If the element contain attribute "name", but not exists in
				// configure file, then return
				return;
			} else if (eleChildList.size() > 0) {
				// Null operation
			} else {
				// Size == 0, and attribute name != null, create element
				List<?> cfgChildList = cfgDocument.selectNodes(ele.getParent()
						.getPath());
				for (Object cfgChildObj : cfgChildList) {
					Element cfgEleObj = (Element) cfgChildObj;
					if ("AH-DELTA-ASSISTANT".equals(ele.getName())) {
						cfgEleObj.add(ele.createCopy());
					} else {
						cfgEleObj.addElement(ele.getName());
					}
				}
			}
			// if(eleChild == null && attrName == null){
			// parentElement = parentElement.addElement(ele.getName());
			// }else if(eleChild == null && attrName != null){
			// break;
			// }
			// if(attrName != null){
			// Element eleChild =
			// (Element)cfgDocument.selectSingleNode(ele.getPath());
			// if(eleChild == null){
			// break;
			// }
			// }else{
			//
			// }
		}
	}

	private static class OperationType {
		private static final Map<String, String> optMap = new HashMap<String, String>();

		public static final String TYPE_AH_ENUM_ACT = "general:ah-enum-act";
		public static final String TYPE_AH_ENUM_ACT_VALUE = "general:ah-enum-act-value";
		public static final String TYPE_AH_ENUM_ACT_SHOW = "general:ah-enum-show";

		static {
			optMap.put("yes", TYPE_AH_ENUM_ACT);
			optMap.put("no", TYPE_AH_ENUM_ACT);
			optMap.put("yesWithValue", TYPE_AH_ENUM_ACT_VALUE);
			optMap.put("noWithValue", TYPE_AH_ENUM_ACT_VALUE);
			optMap.put("yesWithShow", TYPE_AH_ENUM_ACT_SHOW);
			optMap.put("noWithShow", TYPE_AH_ENUM_ACT_SHOW);
		}

		public static String getOperationType(String operationValue) {
			return optMap.get(operationValue);
		}

		public static boolean isSameType(String optValue1, String optValue2) {
			String optType1 = optMap.get(optValue1);
			String optType2 = optMap.get(optValue2);
			return optType1 != null && optType2 != null && optType1.equals(optType2);
		}

		public static String turnToGoalType(String optValue, String goalType) {
			boolean isStartWithYes = optValue.startsWith("yes");

			if (TYPE_AH_ENUM_ACT.equals(goalType)) {
				if (isStartWithYes) {
					return "yes";
				} else {
					return "no";
				}
			}

			if (TYPE_AH_ENUM_ACT_VALUE.equals(goalType)) {
				if (isStartWithYes) {
					return "yesWithValue";
				} else {
					return "noWithValue";
				}
			}

			if (TYPE_AH_ENUM_ACT_SHOW.equals(goalType)) {
				if (isStartWithYes) {
					return "yesWithShow";
				} else {
					return "noWithShow";
				}
			}

			return null;
		}
	}

	public static String getFirstVer(){
		return fristVer;
	}

	public static String getLastVer(){
		return lastVer;
	}
	
	private static void specialHandleElement(Element element){
		String eleName = element.getName();
		
		if("dhcp-server".equals(eleName)){
			specialHandleDhcp(element);
		}else if("dns-server".equals(eleName)){
			specialHandleDns(element);
		}else if("restart".equals(eleName)) {
			specialHandlePse(element);
		}else if("session".equals(eleName)) {
			specialHandleMonitorSession(element);
		}else if("bonjour-gateway".equals(eleName)) {
			specialHandleBonjourGateway(element);
		}else if("match-map".equals(eleName)){
			specialHandlePBR(element);
		}
	}
	
	public static void specialHandleBonjourGateway(Element bonjourGatewayElement){
		if(bonjourGatewayElement == null){
			return;
		}
		
		List<?> ruleList = bonjourGatewayElement.selectNodes("filter/rule");
		if (ruleList == null || ruleList.size() < 1) {
			return;
		}
		for (Object object : ruleList) {
			Element rule = (Element) object;
			Element bonjourAssistant = (Element) rule.selectSingleNode(GenerateXML.AH_DELTA_ASSISTANT);
			if (bonjourAssistant == null) {
				bonjourAssistant = rule.addElement(GenerateXML.AH_DELTA_ASSISTANT);
			}
		}
		
		List<?> bonjourAssistantList = bonjourGatewayElement.selectNodes("filter/rule/" + GenerateXML.AH_DELTA_ASSISTANT);
		List<?> vlanGroupList = bonjourGatewayElement.getParent().selectNodes("vlan-group");
		if (vlanGroupList == null || vlanGroupList.size() < 1) {
			return;
		}
		
		for (Object object : bonjourAssistantList) {
			Element bonjourAssistant = (Element) object;
			StringBuffer sb = new StringBuffer();
			sb.append("from ");
			Element from = (Element) bonjourAssistant.getParent().selectSingleNode("from");
			Element to = (Element) bonjourAssistant.getParent().selectSingleNode("from/cr/to");
			if (from != null && from.attributeValue("value") != null) {
				for (Object every : vlanGroupList) {
					Element vlanGroup = (Element) every;
					if (from.attributeValue("value").equalsIgnoreCase(vlanGroup.attributeValue("name"))) {
						Element vlanGroupCr = (Element)vlanGroup.selectSingleNode("cr");
						if (vlanGroupCr != null && vlanGroupCr.attributeValue("name") != null) {
							sb.append(vlanGroupCr.attributeValue("name") + " ");
						}
					}
				}
			}
			sb.append("to ");
			if (to != null && to.attributeValue("value") != null) {
				for (Object every : vlanGroupList) {
					Element vlanGroup = (Element) every;
					if (to.attributeValue("value").equalsIgnoreCase(vlanGroup.attributeValue("name"))) {
						Element vlanGroupCr = (Element)vlanGroup.selectSingleNode("cr");
						if (vlanGroupCr != null && vlanGroupCr.attributeValue("name") != null) {
							sb.append(vlanGroupCr.attributeValue("name"));
						}
					}
				}
			}
			bonjourAssistant.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, sb.toString());
		}
		
	}
	
	private static void specialHandlePse(Element restartEle){
		if (restartEle == null) {
			return;
		}
		Element elePse = restartEle.getParent();
		if(elePse == null){
			return;
		}
		Element restart = null;
		Element priority = null;
		Element guardBand = null;
		Element powerManagementType = null;
		Element legacy = null;
		Element priorityLldp = null;
		
		Iterator<?> eleIte = elePse.elementIterator();
		while (eleIte.hasNext()) {
			Element child = (Element)eleIte.next();
			if(child.getName().equals("restart")){
				restart = child;
			} else if (child.getName().equals("priority")) {
				priority = child;
			} else if (child.getName().equals("guard-band")) {
				guardBand = child;
			} else if (child.getName().equals("power-management-type")) {
				powerManagementType = child;
			} else if (child.getName().equals("legacy")) {
				legacy = child;
			} else if (child.getName().equals("priority-lldp")) {
			    priorityLldp = child;
            } 
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("priority ");
		if (priority != null && priority.element("enable") != null && priority.element("enable").attributeValue("operation") != null) {
			sb.append(priority.element("enable").attributeValue("operation") + " ");
		}
		sb.append("guard-band ");
		if (guardBand != null && guardBand.attributeValue("value") != null) {
			sb.append(guardBand.attributeValue("value") + " ");
		}
		sb.append("power-type ");
		if (powerManagementType != null && powerManagementType.attributeValue("value") != null) {
			sb.append(powerManagementType.attributeValue("value") + " ");
		}
		sb.append("legacy ");
		if (legacy != null && legacy.element("enable") != null && legacy.element("enable").attributeValue("operation") != null) {
			sb.append(legacy.element("enable").attributeValue("operation") + " ");
		}
		sb.append("priority-lldp ");
        if (priorityLldp != null && priorityLldp.element("enable") != null && priorityLldp.element("enable").attributeValue("operation") != null) {
            sb.append(priorityLldp.element("enable").attributeValue("operation") + " ");
        }
		
		if(restart != null){
			List<?> elementList = restart.elements();
			Element eleCr = null;
			Element eleAssistant = null;
			for(Object obj : elementList){
				if(obj instanceof Element){
					Element eleObj = (Element)obj;
					if(GenerateXML.ELEMENT_EMPTY_OPERATE.equals(eleObj.getName())){
						eleCr = eleObj;
					}else if(GenerateXML.AH_DELTA_ASSISTANT.equals(eleObj.getName())){
						eleAssistant = eleObj;
					}
				}
			}
			if(eleCr == null){
				eleCr = restart.addElement(GenerateXML.ELEMENT_EMPTY_OPERATE);
			}
			if(eleAssistant == null){
				eleAssistant = restart.addElement(GenerateXML.AH_DELTA_ASSISTANT);
			}
			eleAssistant.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, sb.toString());
		}
	}
	
	private static void specialHandleDhcp(Element eleDhcpServer){
		if(eleDhcpServer == null){
			return;
		}
		
		List<Element> mgtPools = new ArrayList<Element>();
		Element mgtDhcp = null;
		Element mgtIp = null;
		Element mgtVlan = null;
		
		Iterator<?> eleIte = eleDhcpServer.elementIterator();
		while (eleIte.hasNext()) {
			Element cldEle = (Element)eleIte.next();
			if(cldEle.getName().equals("ip-pool")){
				mgtPools.add(cldEle);
			}else if(cldEle.getName().equals("enable")){
				mgtDhcp = cldEle;
			}
		}
		
		Iterator<?> parentEleIte = eleDhcpServer.getParent().elementIterator();
		while (parentEleIte.hasNext()) {
			Element cldEle = (Element)parentEleIte.next();
			if(cldEle.getName().equals("ip")){
				mgtIp = cldEle;
			}else if(cldEle.getName().equals("vlan")){
				mgtVlan = cldEle;
			}
		}
		
		String ipStr = "";
		if(mgtIp != null){
			ipStr = mgtIp.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
		}
		
		String vlanStr = "";
		if(mgtVlan != null){
			vlanStr = mgtVlan.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
		}
		
		String ip_pool_Str = "";
		if(mgtPools != null){
			List<String> poolList = new ArrayList<String>();
			for(Element ele : mgtPools){
				poolList.add(ele.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME));
			}
			Collections.sort(poolList);
			for(String pool : poolList){
				if("".equals(ip_pool_Str)){
					ip_pool_Str += pool;
				}else{
					ip_pool_Str += ", " + pool;
				}
			}
		}
		
		if(mgtDhcp != null){
			List<?> elementList = mgtDhcp.elements();
			Element eleCr = null;
			Element eleAssistant = null;
			for(Object obj : elementList){
				if(obj instanceof Element){
					Element eleObj = (Element)obj;
					if(GenerateXML.ELEMENT_EMPTY_OPERATE.equals(eleObj.getName())){
						eleCr = eleObj;
					}else if(GenerateXML.AH_DELTA_ASSISTANT.equals(eleObj.getName())){
						eleAssistant = eleObj;
					}
				}
			}
			if(eleCr == null){
				eleCr = mgtDhcp.addElement(GenerateXML.ELEMENT_EMPTY_OPERATE);
			}
			if(eleAssistant == null){
				eleAssistant = mgtDhcp.addElement(GenerateXML.AH_DELTA_ASSISTANT);
			}
			eleAssistant.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, vlanStr + ", " + ipStr + ", " + ip_pool_Str);
		}
	}
	
	private static void specialHandleDns(Element eleDnsServer){
		if(eleDnsServer == null){
			return;
		}
		
		Element mgtDns = null;
		Element mgtVlan = null;
		
		Iterator<?> eleIte = eleDnsServer.elementIterator();
		while (eleIte.hasNext()) {
			Element cldEle = (Element)eleIte.next();
			if(cldEle.getName().equals("enable")){
				mgtDns = cldEle;
			}
		}
		
		Iterator<?> parentEleIte = eleDnsServer.getParent().elementIterator();
		while (parentEleIte.hasNext()) {
			Element cldEle = (Element)parentEleIte.next();
			if(cldEle.getName().equals("vlan")){
				mgtVlan = cldEle;
			}
		}
		
		String vlanStr = "";
		if(mgtVlan != null){
			vlanStr = mgtVlan.attributeValue(GenerateXML.ATTRIBUTE_NAME_VALUE);
		}
		
		
		if(mgtDns != null){
			List<?> elementList = mgtDns.elements();
			Element eleCr = null;
			Element eleAssistant = null;
			for(Object obj : elementList){
				if(obj instanceof Element){
					Element eleObj = (Element)obj;
					if(GenerateXML.ELEMENT_EMPTY_OPERATE.equals(eleObj.getName())){
						eleCr = eleObj;
					}else if(GenerateXML.AH_DELTA_ASSISTANT.equals(eleObj.getName())){
						eleAssistant = eleObj;
					}
				}
			}
			if(eleCr == null){
				eleCr = mgtDns.addElement(GenerateXML.ELEMENT_EMPTY_OPERATE);
			}
			if(eleAssistant == null){
				eleAssistant = mgtDns.addElement(GenerateXML.AH_DELTA_ASSISTANT);
			}
			eleAssistant.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, vlanStr);
		}
	}
	
	private static void specialHandleMonitorSession(Element eleMonitor){
		if(eleMonitor == null){
			return;
		}
		if(!"monitor".equals(eleMonitor.getParent().getName())){
			return;
		}
		String eleContent = eleMonitor.asXML();
		
		Element eleEnable = (Element)eleMonitor.selectSingleNode("enable");
		if(eleEnable == null){
			return;
		}
		
		Element eleCr = (Element)eleMonitor.selectSingleNode("enable/cr");
		Element eleAssistant = (Element)eleMonitor.selectSingleNode("enable/" + GenerateXML.AH_DELTA_ASSISTANT);
		if(eleCr == null){
			eleCr = eleEnable.addElement(GenerateXML.ELEMENT_EMPTY_OPERATE);
		}
		if(eleAssistant == null){
			eleAssistant = eleEnable.addElement(GenerateXML.AH_DELTA_ASSISTANT);
		}
		
		eleAssistant.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, eleContent);
	}
	
	private static void specialHandlePBR(Element element){
		if(element == null){
			return;
		}
		if(!"/configuration/routing/policy/id/match-map".equals(XmlOrderTool.getPath(element, false))){
			return;
		}
		
		Element policyEle = element.getParent().getParent();
		Element routingEle = policyEle.getParent();
		
		String policyName = policyEle.attributeValue(GenerateXML.ATTRIBUTE_NAME_NAME);
		
		Node matchMapNode = routingEle.selectSingleNode("match-map[@name='"+policyName+"']");
		Node routeMapNode = routingEle.selectSingleNode("route-map[@name='"+policyName+"']");
		
		String matchMapStr = matchMapNode != null ? matchMapNode.asXML() : "";
		String routeMapStr = routeMapNode != null ? routeMapNode.asXML() : "";
		
		Element ahDeltaEle = element.getParent().addElement(GenerateXML.AH_DELTA_ASSISTANT);
		ahDeltaEle.addAttribute(GenerateXML.ATTRIBUTE_NAME_NAME, matchMapStr + routeMapStr);
	}
	
	public static Map<String, Document> getVerXmlMap() {
		return verXmlMap;
	}

	public static void main(String[] args) throws DocumentException,
			IOException {
		String filePath = "D:/test/";
		File file = new File(filePath);
		File[] fileList = file.listFiles(new XmlFile());

		for (File subFile : fileList) {
			String name = subFile.getName();
			System.out.println(name.subSequence(0, name.indexOf(".xml")));
		}
	}

}