package com.ah.be.config.create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.create.cli.ConfigCheckHandler;
import com.ah.be.config.create.cli.DependencyHandler;
import com.ah.be.config.create.common.AhControlMultiApVer;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.common.postprocess.CLIPostProcess;
import com.ah.be.config.create.common.postprocess.GenConfigCLIPostProcess;
import com.ah.be.config.create.source.SecurityObjectProfileInt;
import com.ah.be.config.create.source.TrackProfileInt;
import com.ah.be.config.create.source.UserProfileInt;
import com.ah.be.config.create.source.VlanProfileInt;
import com.ah.be.config.create.source.impl.AdminProfileImpl;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.DeviceGroupImpl;
import com.ah.be.config.create.source.impl.DomainObjectImpl;
import com.ah.be.config.create.source.impl.IpPolicyProfileImpl;
import com.ah.be.config.create.source.impl.LibrarySipPolicyImpl;
import com.ah.be.config.create.source.impl.MacObjectImpl;
import com.ah.be.config.create.source.impl.MacPolicyProfileImpl;
import com.ah.be.config.create.source.impl.MobileDevicePolicyImpl;
import com.ah.be.config.create.source.impl.MobilityPolicyProfileImpl;
import com.ah.be.config.create.source.impl.OsObjectImpl;
import com.ah.be.config.create.source.impl.OsVersionImpl;
import com.ah.be.config.create.source.impl.PskAutoUserGroupImpl;
import com.ah.be.config.create.source.impl.RouteProfileImpl;
import com.ah.be.config.create.source.impl.ScheduleProfileImpl;
import com.ah.be.config.create.source.impl.ServiceProfileImpl;
import com.ah.be.config.create.source.impl.SsidProfileImpl;
import com.ah.be.config.create.source.impl.UserGroupProfileImpl;
import com.ah.be.config.create.source.impl.UserImpl;
import com.ah.be.config.create.source.impl.VlanGroupProfileImpl;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.config.result.ap.AhApConfigGenerationResult;
import com.ah.be.config.result.user.AhUserConfigGenerationResult;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.LocalUser;
import com.ah.util.Tracer;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.AhEnumShow;
import com.ah.xml.be.config.AhNameActValueQuoteProhibited;
import com.ah.xml.be.config.AutoPskUserGroupObj;
import com.ah.xml.be.config.Configuration;
import com.ah.xml.be.config.NetworkAccessSecurityObj;
import com.ah.xml.be.config.ObjectFactory;

/**
 * 
 * @author zhang
 * 
 */
public class GenerateXML {

	private static final Tracer log = new Tracer(GenerateXML.class
			.getSimpleName());

	public static final String ATTRIBUTE_NAME_VALUE = "value";
	public static final String ATTRIBUTE_NAME_NAME = "name";
	public static final String ATTRIBUTE_NAME_OPERATION = "operation";
	public static final String ATTRIBUTE_NAME_FROM = "from";
	public static final String ATTRIBUTE_NAME_TO = "to";
	public static final String ATTRIBUTE_NAME_CONTAIN = "contains";
	public static final String CONTAIN_SPLIT = ",";
	public static final String ATTRIBUTE_NAME_DEBUG = "debug";
	public static final String ATTRIBUTE_NAME_QUOTEPROHIBITED = "quoteProhibited";
	public static final String ELEMENT_EMPTY_OPERATE = "cr";
	private static final String OPERATION_NO_WITH_VALUE = AhEnumActValue.NO_WITH_VALUE
			.value();
	private static final String OPERATION_NO_WITH_HIDDEN = AhEnumShow.NO_WITH_HIDDEN
			.value();
	public static final String AH_DELTA_ASSISTANT = "AH-DELTA-ASSISTANT";
	private static final String OPERATION_NO = "no";
	public static final String ROOT_ELEMENT_NAME = "configuration";

	private Configuration configure;
	private ConfigureProfileFunction profileFunction;
	private HiveAp hiveAp;
	private ConfigType configType;
	private String configVersion;
	private GenerateXMLDebug oDebug;
	private boolean generateXmlLoadError = false;
	public static final Map<String, String> errorCLIMap = new HashMap<String, String>();
	private AhConfigGenerationResult apConfigResult, userConfigResult;
	private boolean isView = false;
	private static JAXBContext context = null;
	private Marshaller marshaller = null;

	public GenerateXML(HiveAp hiveAp, ConfigType configType,
			String configVersion, boolean isView) throws CreateXMLException {
		try {
			this.hiveAp = hiveAp;
			this.configType = configType;
			this.configVersion = configVersion;
			this.isView = isView;
			this.hiveAp.setSoftVer(configVersion);
			oDebug = new GenerateXMLDebug();
			apConfigResult = new AhApConfigGenerationResult();
			userConfigResult = new AhUserConfigGenerationResult();
			
			/** check HiveAp current version */
			StringBuffer errorMsg = new StringBuffer();
			if(!AhControlMultiApVer.checkHiveApVersion(hiveAp, errorMsg)){
				throw new CreateXMLException(errorMsg.toString());
			}
			// AhTracer.CONFIG.info(GenerateXML.class, "GenerateXML",
			// "per-newInstance");
			if(context == null){
				context = JAXBContext.newInstance(ObjectFactory.class);
			}
			// AhTracer.CONFIG.info(GenerateXML.class, "GenerateXML",
			// "per-createMarshaller");
			marshaller = context.createMarshaller();
			// AhTracer.CONFIG.info(GenerateXML.class, "GenerateXML",
			// "per-ConfigureProfileFunction");
		} catch (CreateXMLException exCreate) {
			throw exCreate;
		} catch (Exception e) {
			String errMsg = NmsUtil
					.getUserMessage("error.be.config.create.generateXml");
			log.error("GenerateXML", errMsg, e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_CONFIGURATION, errMsg);
//			throw new CreateXMLException(errMsg, e);
			generateXmlLoadError = true;
		}
	}

	public GenerateXML(HiveAp hiveAp, ConfigType configType, boolean isView)
			throws CreateXMLException {
		this(hiveAp, configType, hiveAp.getSoftVer(), isView);
	}

	public AhConfigGenerationResult generateXML(String filePath) throws CreateXMLException, CLIGenerateException {
		// JAXBElement<Configuration> element = new JAXBElement<Configuration>(
		// new QName("http://www.aerohive.com/configuration", ROOT_ELEMENT_NAME,
		// "aaa"), Configuration.class,
		// configure);
		try{
			//fix bug 27807
//			if(filePath != null && filePath.contains("view")){
//				isView = true;
//			}
			
			long time = System.currentTimeMillis();
			profileFunction = new ConfigureProfileFunction(hiveAp, configType, isView);
			profileFunction.loadPrfile();
			log.debug("new ConfigureProfileFunction()", String.valueOf((System.currentTimeMillis() - time)));
			
			time = System.currentTimeMillis();
			load(isView);
			log.debug("load()", String.valueOf((System.currentTimeMillis() - time)));
		}catch (CreateXMLException exCreate){
			throw exCreate;
		}catch (Exception ex){
			String errMsg = NmsUtil.getUserMessage("error.be.config.create.generateXml");
			log.error("generateXML", errMsg, ex);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_CONFIGURATION, errMsg);
			generateXmlLoadError = true;
		}
		
		FileOutputStream output = null;
		
		JAXBElement<Configuration> element;
		try {
			element = new JAXBElement<Configuration>(
					new QName(ROOT_ELEMENT_NAME), Configuration.class, configure);
			long time = System.currentTimeMillis();

			try {
				output = new FileOutputStream(filePath);
			//	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(element, output);
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						log.error("generateXML", "IO Close Error.", e);
					}
				}
			}

			log.debug("marshal-element", String.valueOf((System.currentTimeMillis() - time)));
			
			if(generateXmlLoadError){
				SAXReader saxReader = new SAXReader();
				Document cfgDocument = saxReader.read(new File(filePath));
				String errMsg = oDebug.getLastErrorMsg(cfgDocument, filePath);
				if(errMsg == null || "".equals(errMsg)){
					errMsg = NmsUtil.getUserMessage("error.be.config.create.generateXml");
				}
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
						HmSystemLog.FEATURE_CONFIGURATION, errMsg);
				CreateXMLException createExe = new CreateXMLException(errMsg);
				createExe.setProfileKey(oDebug.getLastMsg().getGuiProfile());
				createExe.setProfileName(oDebug.getLastMsg().getPName());
				throw createExe;
			}
			
			//XML post process
			CLIPostProcess cliPostProcess = new GenConfigCLIPostProcess(this.profileFunction, filePath, this.configType);
			cliPostProcess.init();
			cliPostProcess.process();
			
//			Document cfgDoc = null;
//			/** modify xml with ap version file */
//			AhControlMultiApVer apVer = new AhControlMultiApVer(hiveAp, configVersion, filePath);
//			cfgDoc = apVer.generate();
//			
//			//generate all CLIs with new implement
//			if(this.configType == ConfigType.AP_FULL){
//				try{
//					CompleteCfgGen allCLIGen = new CompleteCfgGen(this.profileFunction);
//					List<String> result = allCLIGen.generateAllCLIs();
//					CLIParseFactory parseFactory = CLIParseFactory.getInstance();
//					XmlMergeCache xmlCache = new XmlMergeCache(cfgDoc);
//					xmlCache.init();
//					List<String> cannotParseClis = parseFactory.parseCli(result.toArray(new String[result.size()]), xmlCache, allCLIGen.getDeviceConstraint());
//					cfgDoc = xmlCache.getDocument();
////					xmlCache.writeXml(filePath, true);
//					
//					if(cannotParseClis != null && !cannotParseClis.isEmpty()){
//						StringBuilder sBuilder = new StringBuilder();
//						for(String retCli : cannotParseClis){
//							sBuilder.append("\n").append(retCli);
//						}
//						String[] errParams = {sBuilder.toString(), hiveAp.getHostName()};
//						String errMsg = NmsUtil.getUserMessage(
//								"error.config.cli.parsing.newImplement.failure", errParams);
//						log.error("CLIParseNewImplement", errMsg);
//					}
//				}catch(CLIGenerateException e){
//					throw new CreateXMLException(e.getMessage());
//				}
//			}
//			
////			//auto fill element <AH-DELTA-ASSISTANT operation="yes"/>
////			Document cfgDoc = CLICommonFunc.readXml(filePath);
//			cfgDoc = FillAssistantElementAuto.getInstance().fillElement(cfgDoc);
//			// xml element sort
//			cfgDoc = XmlOrderTool.getInstance().orderXml(cfgDoc);
//			CLICommonFunc.writeXml(cfgDoc, filePath, true);
			
			if (this.configType == ConfigType.USER_FULL) {
				return this.userConfigResult;
			} else{
				AhApConfigGenerationResult apRes = (AhApConfigGenerationResult)apConfigResult;
				apRes.setHiveApMac(hiveAp.getMacAddress());
				apRes.setRootAdmin(configure.getAdmin().getRootAdmin().getValue());
				apRes.setRootPassword(AdminProfileImpl.getRootAdminPassword(this.hiveAp));
				return apRes;
			}
		} catch (CreateXMLException exCreate) {
			throw exCreate;
		} catch (Exception e) {
			log.error("generateXML", e.getMessage(), e);
			String errMsg = NmsUtil
					.getUserMessage("error.be.config.create.generateXml");
			throw new CreateXMLException(errMsg, e);
		} finally {
		//	element = null;
			configure = null;
//			if(VPNProfileImpl.isUsedVpnPass(hiveAp.getMacAddress())){
//				VPNProfileImpl.clearVpnUser(hiveAp.getMacAddress());
//			}
		}
	}

	private void load(boolean isView) throws Exception {
		if(configure == null){
			configure = new Configuration();
		}
		switch (configType) {
		case AP_FULL:
			generateSnmpTree(oDebug);
			generateSecurityObjectTree(oDebug);
			generateSsidTree(oDebug);
			generateScheduleTree(oDebug);
			generateUserProfileTree(oDebug);
			generateHiveTree(oDebug);
			generateSecurityTree(oDebug);
			generateAAATree(oDebug);
			generateAdminTree(oDebug);
			generateAmrpTree(oDebug);
			generateDnsTree(oDebug);
			generateInterfaceTree(oDebug);
			generateIpPolicyTree(oDebug);
			generateLoggingTree(oDebug);
			generateMacPolicyTree(oDebug);
			generateMobilityPolicyTree(oDebug);
			generateMobilityThresholdTree(oDebug);
			generateNtpTree(oDebug);
			generateRoamingTree(oDebug);
			generateRouteTree(oDebug);
			
			generateRadioTree(oDebug);
			generateClockTree(oDebug);
			generateQosTree(oDebug);
//			generateServiceTree(oDebug);
			generateResetButtonTree(oDebug);
			generateIpTree(oDebug);
			generateIpNatPolicyTree(oDebug);
			generateAlgTree(oDebug);
			generateLocationTree(oDebug);
			generateForwardingEngineTree(oDebug);
			generateCapwapTree(oDebug);
			generateHivemanagerTree(oDebug);
			generateHostnameTree(oDebug);
			generateConsoleTree(oDebug);
			generateSystemTree(oDebug);
			generateTrackTree(oDebug);
			generateTrackWanTree(oDebug);
			generateCacTree(oDebug);
			generateAccessConsoleTree(oDebug);
			generateLldpTree(oDebug);
			if(NmsUtil.compareSoftwareVersion("3.5.0.0", hiveAp.getSoftVer()) > 0){
				generateUserGroupTree(oDebug);
			}
			generateVpnTree(oDebug, isView);
			generateAirscreenTree(oDebug);
			generatePerformanceSentinelTree(oDebug);
			generateReportTree(oDebug);
			generateLibrarySipPolicy(oDebug);
			generateUserAttributePolicyTree(oDebug);
			generateDeviceGroupTree(oDebug);
			generateOsObjectTree(oDebug);
			generateMacObjectTree(oDebug);
			generateDomainObjectTree(oDebug);
			generateDataCollectionTree(oDebug);
			generateConfigTree(oDebug);
			generateNetworkFirewall(oDebug);
			generateWebSecurityProxyTree(oDebug);
			generateUsbmodemTree(oDebug);
			generateRoutingProfileTree(oDebug);
			generateLanPortTree(oDebug);
			generatePseTree(oDebug);
			generate8021XMacTableTree(oDebug);
			generateOsDetectionTree(oDebug);
			generateOsVersionTree(oDebug);
			generateBonjourGatewayTree(oDebug);
//			generateDesignatedServerTree(oDebug);
			generateApplicationTree(oDebug);
			generateVlanGroupTree(oDebug);
			//generateAdminConnectionAlarmTree(oDebug);
			generatePortChannelTree(oDebug);
			generateMacAddressTableTree(oDebug);
			generateCdpTree(oDebug);
			generateClientMode();
			generateMonitorTree();
			generateVlanProfileTree();
			generateVlanReserveTree();
			generateSpanningTree();
			generateStromControlTree();
			generateKddrTree();
			
			new ConfigCheckHandler().execute(configure, hiveAp);
			new DependencyHandler().execute(configure, hiveAp);
//			configure = new FillDeltaAssistantElement(configure)
//					.getConfiguration();
			break;
		case USER_FULL:
			log.info("load", "per-generateLocalUserTree");
			if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
				break;
			}
			if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.5.0.0") > -1){
				generatePpskScheduleTree(oDebug);
				generateUserTree(oDebug);
				generateUserGroupTree(oDebug);
				generateAutoPskGroupTree(oDebug);
			}else if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.3.0.0") > -1){
//				generateUserGroupTree();
				generateUserTree(oDebug);
				generateAutoPskGroupTree(oDebug);
//				generateSsidUserGroupBindTree();
//				generateRadiusBindGroupTree();
			}
			limitBr100User();
//			/
			break;
		default:
			log.warning("load", "Unexpected Config Type: "
					+ configType.toString());
			break;
		}
//		configure = new FillDeltaAssistantElement(configure)
//		.getConfiguration();
	}

	public static String convertXmlIntoClis(String xmlPath) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(xmlPath));
		Element rootElement = document.getRootElement();
		StringBuffer cliBuffer = new StringBuffer();
		StringBuffer allCliCmd = new StringBuffer();
		treeWalkElement(rootElement, cliBuffer, allCliCmd, null);

		return allCliCmd.toString();
	}

	public static void generateScript(String clis, String scriptPath)
			throws IOException {
		FileOutputStream output = null;

		try {
			output = new FileOutputStream(scriptPath);
			output.write(clis.getBytes());
			output.flush();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public static void treeWalkElement(Element element,
			StringBuffer cliBuffer, StringBuffer allCliCmd, String errorKey) {
		boolean isneedValue = true, isNoCmd = false;
		if (!element.isRootElement()) {
			Attribute attributeValue = element.attribute(ATTRIBUTE_NAME_VALUE);
			Attribute attributeName = element.attribute(ATTRIBUTE_NAME_NAME);
			Attribute attributeOperation = element
					.attribute(ATTRIBUTE_NAME_OPERATION);
			Attribute attributeQuote = element
					.attribute(ATTRIBUTE_NAME_QUOTEPROHIBITED);
			String elementName = element.getName();
			// new rule when operation value is 'noOutput' throw away this
			// element
			if (attributeOperation != null) {
				String operationValue = attributeOperation.getValue();
				if (OPERATION_NO_WITH_HIDDEN.equals(operationValue)) {
					return;
				}
			}
			if (elementName != null) {
				if ((attributeValue != null && "".equals(attributeValue
						.getValue()))
						|| (attributeName != null && "".equals(attributeName
								.getValue()))
						|| elementName.equals(ELEMENT_EMPTY_OPERATE)) {
					// null;
				} else if (AH_DELTA_ASSISTANT.equals(elementName)) {
					return;
				} else {
					if (!cliBuffer.toString().equals("")
							&& !cliBuffer.toString().endsWith(" ")) {
						cliBuffer.append(" ");
					}
					cliBuffer.append(elementName);
				}
			}
			if (attributeOperation != null) {
				String operationValue = attributeOperation.getValue();
				if (OPERATION_NO_WITH_VALUE.equals(operationValue)) {
					isneedValue = true;
					isNoCmd = true;
				}
				if (OPERATION_NO.equals(operationValue)) {
					isneedValue = false;
					isNoCmd = true;
				}
			}
			if (attributeName != null && !"".equals(attributeName.getValue())
					&& isneedValue) {
				if (!cliBuffer.toString().equals("")
						&& !cliBuffer.toString().endsWith(" ")) {
					cliBuffer.append(" ");
				}
				String attrName = attributeName.getValue();
				if ((attrName.contains(" ") || attrName.contains("?") || attrName.contains("\"") || attrName.contains("\\")) && attributeQuote == null) {
					attrName = attrName.replace("\\", "\\\\");
					attrName = attrName.replace("\"", "\\\"");
					attrName = "\"" + attrName + "\"";
				}
				cliBuffer.append(attrName);
			}
			if (attributeValue != null && !"".equals(attributeValue.getValue())
					&& isneedValue) {
				if (!cliBuffer.toString().equals("")
						&& !cliBuffer.toString().endsWith(" ")) {
					cliBuffer.append(" ");
				}
				String attrValue = attributeValue.getValue();
				if ((attrValue.contains(" ") || attrValue.contains("?") || attrValue.contains("\"") || attrValue.contains("\\")) && attributeQuote == null) {
					attrValue = attrValue.replace("\\", "\\\\");
					attrValue = attrValue.replace("\"", "\\\"");
					attrValue = "\"" + attrValue + "\"";
				}
				cliBuffer.append(attrValue);
			}
		}
		if(element.attribute(ATTRIBUTE_NAME_DEBUG) != null){
			allCliCmd.append(cliBuffer.toString());
			errorCLIMap.put(errorKey, cliBuffer.toString());
			return;
		}
		if (element.elements().size() == 0 || isNoCmd) {
			String cliCmd = cliBuffer.toString();
			if (cliBuffer.toString() != null
					&& !"".equals(cliBuffer.toString())) {
				cliCmd += "\n";
			}
			if (isNoCmd) {
				cliCmd = OPERATION_NO + " " + cliCmd;
			}
			allCliCmd.append(cliCmd);
			return;
		}
		
		Iterator<?> eleIte = element.elementIterator();
		while (eleIte.hasNext()) {
			Element childElement = (Element) eleIte.next();
			StringBuffer cliCmdBuffer = new StringBuffer();
			cliCmdBuffer.append(cliBuffer);
			treeWalkElement(childElement, cliCmdBuffer, allCliCmd, errorKey);
		}
	}

	private void generateSsidTree(GenerateXMLDebug oDebug) throws Exception {
		for (SsidProfileImpl ssidProfile : profileFunction.getSsidProfileList()) {
			CreateSsidTree ssidTree = null;
			String ssidName = ssidProfile.getSsidName();
			try{
				NetworkAccessSecurityObj securityForSsid = null;
				for(NetworkAccessSecurityObj securityObj : configure.getSecurityObject()){
					if(ssidName.equals(securityObj.getName())){
						securityForSsid = securityObj;
					}
				}
				ssidTree = new CreateSsidTree(ssidProfile, securityForSsid, oDebug);
				ssidTree.generate();
			}catch(Exception e){
				if(ssidTree != null && ssidTree.getSsidObj() != null){
					configure.getSsid().add(ssidTree.getSsidObj());
				}
				throw e;
			}
			if(ssidTree != null && ssidTree.getSsidObj() != null){
				configure.getSsid().add(ssidTree.getSsidObj());
			}
		}
	}
	
	private void generateSecurityObjectTree(GenerateXMLDebug oDebug) throws Exception{
		for(SecurityObjectProfileInt securityImpl : profileFunction.getSecurityObjList()){
			CreateSecurityObjectTree securityTree = null;
			try{
				securityTree = new CreateSecurityObjectTree(securityImpl, oDebug);
				securityTree.generate();
			}catch(Exception e){
				if(securityTree != null && securityTree.getSecurityObj() != null){
					configure.getSecurityObject().add(securityTree.getSecurityObj());
				}
				throw e;
			}
			if(securityTree != null && securityTree.getSecurityObj() != null){
				configure.getSecurityObject().add(securityTree.getSecurityObj());
			}
		}
	}

	private void generateScheduleTree(GenerateXMLDebug oDebug) throws Exception {
		for (ScheduleProfileImpl scheduleObj : profileFunction
				.getScheduleList()) {
			
			CreateScheduleTree scheduleTree = null;
			try{
				scheduleTree = new CreateScheduleTree(scheduleObj, oDebug);
				scheduleTree.generate();
			}catch(Exception e){
				if(scheduleTree != null && scheduleTree.getScheduleObj() != null){
					configure.getSchedule().add(scheduleTree.getScheduleObj());
				}
				throw e;
			}
			if(scheduleTree != null && scheduleTree.getScheduleObj() != null){
				configure.getSchedule().add(scheduleTree.getScheduleObj());
			}
		}
	}
	
	private void generateLibrarySipPolicy(GenerateXMLDebug oDebug) throws Exception{
		for(LibrarySipPolicyImpl sipImpl : profileFunction.getLibrarySipPolicyList()){
			CreateLibrarySipPolicyTree sipTree = null;
			try{
				sipTree = new CreateLibrarySipPolicyTree(sipImpl, oDebug);
				sipTree.generate();
			}catch(Exception e){
				if(sipTree != null && sipTree.getLibrarySipPolicyObj() != null){
					configure.getLibrarySipPolicy().add(sipTree.getLibrarySipPolicyObj());
				}
				throw e;
			}
			if(sipTree != null && sipTree.getLibrarySipPolicyObj() != null){
				configure.getLibrarySipPolicy().add(sipTree.getLibrarySipPolicyObj());
			}
		}
	}
	
	private void generateUserAttributePolicyTree(GenerateXMLDebug oDebug) throws Exception{
		for(MobileDevicePolicyImpl userAttrPolicyImpl : profileFunction.getDevicePolicyList()){
			CreateMobileDevicePolicyTree userPolicyTree = null;
			try{
				userPolicyTree = new CreateMobileDevicePolicyTree(userAttrPolicyImpl, oDebug);
				userPolicyTree.generate();
			}catch(Exception e){
				if(userPolicyTree != null && userPolicyTree.getUserAttributePolicyObj() != null){
					configure.getMobileDevicePolicy().add(userPolicyTree.getUserAttributePolicyObj());
				}
				throw e;
			}
			if(userPolicyTree != null && userPolicyTree.getUserAttributePolicyObj() != null){
				configure.getMobileDevicePolicy().add(userPolicyTree.getUserAttributePolicyObj());
			}
		}
	}
	
	private void generateDeviceGroupTree(GenerateXMLDebug oDebug) throws Exception{
		for(DeviceGroupImpl dgImpl : profileFunction.getDeviceGroupList()){
			CreateDeviceGroupTree dgTree = null;
			try{
				dgTree = new CreateDeviceGroupTree(dgImpl, oDebug);
				dgTree.generate();
			}catch(Exception e){
				if(dgTree != null && dgTree.getDeviceGroupObj() != null){
					configure.getDeviceGroup().add(dgTree.getDeviceGroupObj());
				}
				throw e;
			}
			if(dgTree != null && dgTree.getDeviceGroupObj() != null){
				configure.getDeviceGroup().add(dgTree.getDeviceGroupObj());
			}
		}
	}
	
	private void generateOsObjectTree(GenerateXMLDebug oDebug) throws Exception{
		for(OsObjectImpl osImp : profileFunction.getOsObjectList()){
			CreateOsObjectTree osTree = null;
			try{
				osTree = new CreateOsObjectTree(osImp, oDebug);
				osTree.generate();
			}catch(Exception e){
				if(osTree != null && osTree.getOsObjectObj() != null){
					configure.getOsObject().add(osTree.getOsObjectObj());
				}
				throw e;
			}
			if(osTree != null && osTree.getOsObjectObj() != null){
				configure.getOsObject().add(osTree.getOsObjectObj());
			}
		}
	}
	
	private void generateOsDetectionTree(GenerateXMLDebug oDebug) throws Exception{
		CreateOsDetectionTree osDetectionTree = null;
		try{
			osDetectionTree = new CreateOsDetectionTree(profileFunction.getOsDetectionImpl(), oDebug);
			osDetectionTree.generate();
		}catch(Exception e){
			if(osDetectionTree != null && osDetectionTree.getOsDetectionObj() != null){
				configure.setOsDetection(osDetectionTree.getOsDetectionObj());
			}
			throw e;
		}
		if(osDetectionTree != null && osDetectionTree.getOsDetectionObj() != null){
			configure.setOsDetection(osDetectionTree.getOsDetectionObj());
		}
		
	}
	
	private void generateOsVersionTree(GenerateXMLDebug oDebug) throws Exception{
		for(OsVersionImpl osVersionImp : profileFunction.getOsVersionList()){
			CreateOsVersionTree osVersionTree = null;
			try{
				osVersionTree = new CreateOsVersionTree(osVersionImp, oDebug);
				osVersionTree.generate();
			}catch(Exception e){
				if(osVersionTree != null && osVersionTree.getOsVersionObj() != null){
					configure.getOsVersion().add(osVersionTree.getOsVersionObj());
				}
				throw e;
			}
			if(osVersionTree != null && osVersionTree.getOsVersionObj() != null){
				configure.getOsVersion().add(osVersionTree.getOsVersionObj());
			}
		}
	}
	
	private void generateMacObjectTree(GenerateXMLDebug oDebug) throws Exception{
		for(MacObjectImpl macImpl : profileFunction.getMacObjectList()){
			CreateMacObjectTree macTree = null;
			try{
				macTree = new CreateMacObjectTree(macImpl, oDebug);
				macTree.generate();
			}catch(Exception e){
				if(macTree != null && macTree.getMacObjectObj() != null){
					configure.getMacObject().add(macTree.getMacObjectObj());
				}
				throw e;
			}
			if(macTree != null && macTree.getMacObjectObj() != null){
				configure.getMacObject().add(macTree.getMacObjectObj());
			}
		}
	}
	
	private void generateDomainObjectTree(GenerateXMLDebug oDebug) throws Exception{
		for(DomainObjectImpl domainImpl : profileFunction.getDomainObjectList()){
			CreateDomainObjectTree domainTree = null;
			try{
				domainTree = new CreateDomainObjectTree(domainImpl, oDebug);
				domainTree.generate();
			}catch(Exception e){
				if(domainTree != null && domainTree.getDomainObjectObj() != null){
					configure.getDomainObject().add(domainTree.getDomainObjectObj());
				}
				throw e;
			}
			if(domainTree != null && domainTree.getDomainObjectObj() != null){
				configure.getDomainObject().add(domainTree.getDomainObjectObj());
			}
		}
	}
	
	private void generateBonjourGatewayTree(GenerateXMLDebug oDebug) throws Exception{
		CreateBonjourGatewayTree bonjourGatewayTree = null;
		try{
			bonjourGatewayTree = new CreateBonjourGatewayTree(profileFunction.getBonjourGatewayImpl(), oDebug);
			bonjourGatewayTree.generate();
		}catch(Exception e){
			if(bonjourGatewayTree != null && bonjourGatewayTree.getBonjourGatewayObj() != null){
				configure.setBonjourGateway(bonjourGatewayTree.getBonjourGatewayObj());
			}
			throw e;
		}
		if(bonjourGatewayTree != null && bonjourGatewayTree.getBonjourGatewayObj() != null){
			configure.setBonjourGateway(bonjourGatewayTree.getBonjourGatewayObj());
		}
	}
	
	private void generateCdpTree(GenerateXMLDebug oDebug) throws Exception{
		CreateCdpTree cdpTree = null;
		try{
			cdpTree = new CreateCdpTree(profileFunction.getCdpProfileImpl(), oDebug);
			cdpTree.generate();
		}catch(Exception e){
			if(cdpTree != null && cdpTree.getCdpObj() != null){
				configure.setCdp(cdpTree.getCdpObj());
			}
			throw e;
		}
		if(cdpTree != null && cdpTree.getCdpObj() != null){
			configure.setCdp(cdpTree.getCdpObj());
		}
	}
	
	private void generateClientMode() throws Exception{
		CreateClientModeTree clientModeTree = null;
		try{
			clientModeTree = new CreateClientModeTree(profileFunction.getClientModeImpl());
			clientModeTree.generate();
		}catch(Exception e){
			if(clientModeTree != null && clientModeTree.getClientModeObj() != null){
				configure.setClientMode(clientModeTree.getClientModeObj());
			}
			throw e;
		}
		
		if(clientModeTree != null && clientModeTree.getClientModeObj() != null){
			configure.setClientMode(clientModeTree.getClientModeObj());
		}
	}
	
	private void generatePortChannelTree(GenerateXMLDebug oDebug) throws Exception{
		CreatePortChannelTree portChannelTree = null;
		try{
			portChannelTree = new CreatePortChannelTree(profileFunction.getPortChannelImpl());
			portChannelTree.generate();
		}catch(Exception e){
			if(portChannelTree != null && portChannelTree.getPortChannelObj() != null){
				configure.setAgg(portChannelTree.getPortChannelObj());
			}
			throw e;
		}
		if(portChannelTree != null && portChannelTree.getPortChannelObj() != null){
			configure.setAgg(portChannelTree.getPortChannelObj());
		}
	}
	
	private void generateMacAddressTableTree(GenerateXMLDebug oDebug) throws Exception{
		CreateMacAddressTableTree macAddressTableTree = null;
		try{
			macAddressTableTree = new CreateMacAddressTableTree(profileFunction.getMacAddressTableImpl(), oDebug);
			macAddressTableTree.generate();
		}catch(Exception e){
			if(macAddressTableTree != null && macAddressTableTree.getMacAddressTableObj() != null){
				configure.setMacAddressTable(macAddressTableTree.getMacAddressTableObj());
			}
			throw e;
		}
		if(macAddressTableTree != null && macAddressTableTree.getMacAddressTableObj() != null){
			configure.setMacAddressTable(macAddressTableTree.getMacAddressTableObj());
		}
	}
	
	private void generateAdminConnectionAlarmTree(GenerateXMLDebug oDebug) throws Exception{
		CreateAdminConnectionAlarmTree connectionAlarmTree = null;
		try{
			connectionAlarmTree = new CreateAdminConnectionAlarmTree(profileFunction.getAdminConnectionAlarmImpl(), oDebug);
			connectionAlarmTree.generate();
		}catch(Exception e){
			if(connectionAlarmTree != null && connectionAlarmTree.getAdminConnectionAlarmObj() != null){
				configure.setConnectionAlarming(connectionAlarmTree.getAdminConnectionAlarmObj());
			}
			throw e;
		}
		if(connectionAlarmTree != null && connectionAlarmTree.getAdminConnectionAlarmObj() != null){
			configure.setConnectionAlarming(connectionAlarmTree.getAdminConnectionAlarmObj());
		}
	}
	
	private void generateApplicationTree(GenerateXMLDebug oDebug) throws Exception{
		CreateApplicationTree applicationTree = null;
		try{
			applicationTree = new CreateApplicationTree(profileFunction.getApplicationProfileImpl(), oDebug);
			applicationTree.generate();
		}catch(Exception e){
			if(applicationTree != null && applicationTree.getApplicationObj() != null){
				configure.setApplication(applicationTree.getApplicationObj());
			}
			throw e;
		}
		if(applicationTree != null && applicationTree.getApplicationObj() != null){
			configure.setApplication(applicationTree.getApplicationObj());
		}
	}
	
	private void generateVlanGroupTree(GenerateXMLDebug oDebug) throws Exception{
		for (VlanGroupProfileImpl vlanGroupProfileImpl : profileFunction.getVlanGroupList()) {
			CreateVlanGroupTree vlanGroupTree = null;
			try{
				vlanGroupTree = new CreateVlanGroupTree(vlanGroupProfileImpl, oDebug);
				vlanGroupTree.generate();
			}catch(Exception e){
				if(vlanGroupTree != null && vlanGroupTree.getVlanGroupObj() != null){
					configure.getVlanGroup().add(vlanGroupTree.getVlanGroupObj());
				}
				throw e;
			}
			if(vlanGroupTree != null && vlanGroupTree.getVlanGroupObj() != null){
				configure.getVlanGroup().add(vlanGroupTree.getVlanGroupObj());
			}
		}
	}
	
//	private void generateDesignatedServerTree(GenerateXMLDebug oDebug) throws Exception{
//		CreateDesignatedServerTree designatedTree = null;
//		try{
//			designatedTree = new CreateDesignatedServerTree(profileFunction.getDesignatedServerImpl(), oDebug);
//			designatedTree.generate();
//		}catch(Exception e){
//			if(designatedTree != null && designatedTree.getDesignatedServerObj() != null){
//				configure.setDesignatedServer(designatedTree.getDesignatedServerObj());
//			}
//			throw e;
//		}
//		if(designatedTree != null && designatedTree.getDesignatedServerObj() != null){
//			configure.setDesignatedServer(designatedTree.getDesignatedServerObj());
//		}
//	}
	
	private void generateDataCollectionTree(GenerateXMLDebug oDebug) throws Exception {
		CreateDataCollectionTree collectionTree = null;
		try{
			collectionTree = new CreateDataCollectionTree(profileFunction.getCollectionImpl(), oDebug);
			collectionTree.generate();
		}catch(Exception ex){
			if(collectionTree != null && collectionTree.getDataCollectionObj() != null){
				configure.setDataCollection(collectionTree.getDataCollectionObj());
			}
			throw ex;
		}
		if(collectionTree != null && collectionTree.getDataCollectionObj() != null){
			configure.setDataCollection(collectionTree.getDataCollectionObj());
		}
	}
	
	private void generateConfigTree(GenerateXMLDebug oDebug) throws Exception {
		CreateConfigTree configTree = null;
		try{
			configTree = new CreateConfigTree(profileFunction.getConfigProfileImpl(), oDebug);
			configTree.generate();
		}catch(Exception ex){
			if(configTree != null && configTree.getConfigObj() != null){
				configure.setConfig(configTree.getConfigObj());
			}
			throw ex;
		}
		if(configTree != null && configTree.getConfigObj() != null){
			configure.setConfig(configTree.getConfigObj());
		}
	}
	
	private void generateNetworkFirewall(GenerateXMLDebug oDebug) throws Exception {
		CreateNetworkFirewallTree firewallTree = null;
		try{
			firewallTree = new CreateNetworkFirewallTree(profileFunction.getNetworkFirewallImpl(), oDebug);
			firewallTree.generate();
		}catch(Exception ex){
			if(firewallTree != null && firewallTree.getNetworkFirewallObj() != null){
				configure.setNetworkFirewall(firewallTree.getNetworkFirewallObj());
			}
			throw ex;
		}
		if(firewallTree != null && firewallTree.getNetworkFirewallObj() != null){
			configure.setNetworkFirewall(firewallTree.getNetworkFirewallObj());
		}
	}
	
	private void generateWebSecurityProxyTree(GenerateXMLDebug oDebug) throws Exception {
		CreateWebSecurityProxyTree webTree = null;
		try{
			webTree = new CreateWebSecurityProxyTree(profileFunction.getWebProxyImpl(), profileFunction.getUserProfileList(), oDebug);
			webTree.generate();
		}catch(Exception ex){
			if(webTree != null && webTree.getWebSecurityProxyObj() != null){
				configure.setWebSecurityProxy(webTree.getWebSecurityProxyObj());
			}
			throw ex;
		}
		if(webTree != null && webTree.getWebSecurityProxyObj() != null){
			configure.setWebSecurityProxy(webTree.getWebSecurityProxyObj());
		}
	}
	
	private void generateUsbmodemTree(GenerateXMLDebug oDebug) throws Exception {
		CreateUsbmodemTree usbTree = null;
		try{
			usbTree = new CreateUsbmodemTree(profileFunction.getUsbmodemImpl(), oDebug);
			usbTree.generate();
		}catch(Exception ex){
			if(usbTree != null && usbTree.getUsbmodemObj() != null){
				configure.setUsbmodem(usbTree.getUsbmodemObj());
			}
			throw ex;
		}
		if(usbTree != null && usbTree.getUsbmodemObj() != null){
			configure.setUsbmodem(usbTree.getUsbmodemObj());
		}
	}
	
	private void generateRoutingProfileTree(GenerateXMLDebug oDebug) throws Exception {
		CreateRoutingTree routingTree = null;
		try{
			routingTree = new CreateRoutingTree(profileFunction.getRoutingImpl(), oDebug);
			routingTree.generate();
		}catch(Exception ex){
			if(routingTree != null && routingTree.getRoutingObj() != null){
				configure.setRouting(routingTree.getRoutingObj());
			}
			throw ex;
		}
		if(routingTree != null && routingTree.getRoutingObj() != null){
			configure.setRouting(routingTree.getRoutingObj());
		}
	}
	
	private void generateLanPortTree(GenerateXMLDebug oDebug) throws Exception {
		CreateLanTree lanTree = null;
		try{
			lanTree = new CreateLanTree(profileFunction.getLanPortImpl(), oDebug);
			lanTree.generate();
		}catch(Exception ex){
			if(lanTree != null && lanTree.getLanObj() != null){
				configure.setLan(lanTree.getLanObj());
			}
			throw ex;
		}
		if(lanTree != null && lanTree.getLanObj() != null){
			configure.setLan(lanTree.getLanObj());
		}
	}
	
	private void generatePpskScheduleTree(GenerateXMLDebug oDebug) throws Exception {
		for (ScheduleProfileImpl scheduleObj : profileFunction.getPpskScheduleList()) {
			
			CreatePpskScheduleTree ppskScheduleTree = null;
			try{
				ppskScheduleTree = new CreatePpskScheduleTree(scheduleObj, oDebug);
				ppskScheduleTree.generate();
			}catch(Exception e){
				if(ppskScheduleTree != null && ppskScheduleTree.getPpskScheduleObj() != null){
					configure.getSchedulePpsk().add(ppskScheduleTree.getPpskScheduleObj());
				}
				throw e;
			}
			if(ppskScheduleTree != null && ppskScheduleTree.getPpskScheduleObj() != null){
				configure.getSchedulePpsk().add(ppskScheduleTree.getPpskScheduleObj());
			}
		}
	}

	private void generateUserProfileTree(GenerateXMLDebug oDebug) throws Exception {
		int allsize = profileFunction.getUserProfileList().size();
		String beforeUPName = null;
		for (int index=0; index<allsize; index++) {
			UserProfileInt userProfileObj = profileFunction.getUserProfileList().get(index);
			if(index != 0){
				beforeUPName = profileFunction.getUserProfileList().get(index-1).getUserProfileName();
			}
			CreateUserProfileTree userProfileTree = null;
			try{
				userProfileTree = new CreateUserProfileTree(userProfileObj, index, allsize, beforeUPName, oDebug);
				userProfileTree.generate();
			}catch(Exception e){
				if(userProfileTree != null && userProfileTree.getUserProfileObj() != null){
					configure.getUserProfile().add(userProfileTree.getUserProfileObj());
				}
				throw e;
			}
			if(userProfileTree != null && userProfileTree.getUserProfileObj() != null){
				configure.getUserProfile().add(userProfileTree.getUserProfileObj());
			}
		}
	}

	private void generateHiveTree(GenerateXMLDebug oDebug) throws Exception {
		CreateHiveTree hiveTree = null;
		try{
			hiveTree = new CreateHiveTree(profileFunction.getHiveProfile(), oDebug);
			hiveTree.generate();
		}catch(Exception e){
			if(hiveTree != null && hiveTree.getHiveObj() != null){
				configure.getHive().add(hiveTree.getHiveObj());
			}
			throw e;
		}
		if(hiveTree != null && hiveTree.getHiveObj() != null){
			configure.getHive().add(hiveTree.getHiveObj());
		}
	}

	private void generateSecurityTree(GenerateXMLDebug oDebug) throws Exception {
		CreateSecurityTree securityTree = null;
		try{
			securityTree = new CreateSecurityTree(profileFunction.getSecurityProfile(), oDebug);
			securityTree.generate();
		}catch(Exception e){
			if(securityTree != null){
				configure.setSecurity(securityTree.getSecurityObj());
			}
			throw e;
		}
		if(securityTree != null){
			configure.setSecurity(securityTree.getSecurityObj());
		}
	}

	private void generateAAATree(GenerateXMLDebug oDebug) throws Exception {		
		CreateAAATree aaaTree = null;
		try{
			aaaTree = new CreateAAATree(profileFunction.getAAAProfileImpl(), oDebug);
			aaaTree.generate();
		}catch(Exception e){
			if(aaaTree != null){
				configure.setAaa(aaaTree.getAaaObj());
			}
			throw e;
		}
		if(aaaTree != null){
			configure.setAaa(aaaTree.getAaaObj());
		}
	}

	private void generateAdminTree(GenerateXMLDebug oDebug) throws Exception {
		CreateAdminTree adminTree = null;
		try{
			adminTree = new CreateAdminTree(profileFunction.getAdminProfileImpl(), oDebug);
			adminTree.generate();
		}catch(Exception e){
			if(adminTree != null){
				configure.setAdmin(adminTree.getAdminObj());
			}
			throw e;
		}
		if (adminTree != null) {
			configure.setAdmin(adminTree.getAdminObj());
		}
	}

	private void generateAmrpTree(GenerateXMLDebug oDebug) throws Exception {
		CreateAmrpTree amrpTree = null;
		try{
			amrpTree = new CreateAmrpTree(profileFunction.getAmrpProfileImpl(), oDebug);
			amrpTree.generate();
		}catch(Exception e){
			if(amrpTree != null){
				configure.setAmrp(amrpTree.getAmrpObj());
			}
			throw e;
		}
		if (amrpTree != null) {
			configure.setAmrp(amrpTree.getAmrpObj());
		}
	}

	private void generateDnsTree(GenerateXMLDebug oDebug) throws Exception {
		CreateDnsTree dnsTree = null;
		try{
			dnsTree = new CreateDnsTree(profileFunction.getDnsProfileImpl(), oDebug);
			dnsTree.generate();
		}catch(Exception e){
			if(dnsTree != null){
				configure.setDns(dnsTree.getDnsObj());
			}
			throw e;
		}
		if(dnsTree != null){
			configure.setDns(dnsTree.getDnsObj());
		}
	}

	private void generateInterfaceTree(GenerateXMLDebug oDebug) throws Exception {
		CreateInterfaceTree interfaceTree = null;
		try{
			interfaceTree = new CreateInterfaceTree(profileFunction.getInterfaceProfileImpl(), oDebug);
			interfaceTree.generate();
		}catch(Exception e){
			if(interfaceTree != null){
				configure.setInterface(interfaceTree.getInterfaceObj());
			}
			throw e;
		}
		if(interfaceTree != null){
			configure.setInterface(interfaceTree.getInterfaceObj());
		}
	}

	private void generateIpPolicyTree(GenerateXMLDebug oDebug) throws Exception {
		for (IpPolicyProfileImpl ipPolicyImpl : profileFunction
				.getIpPolicyProfileImplList()) {
			
			CreateIpPolicyTree ipPolicyTree = null;
			try{
				 ipPolicyTree = new CreateIpPolicyTree(ipPolicyImpl, oDebug);
				 ipPolicyTree.generate();
			}catch(Exception e){
				if(ipPolicyTree != null && ipPolicyTree.getIpPolicyObj() != null){
					configure.getIpPolicy().add(ipPolicyTree.getIpPolicyObj());
				}
				throw e;
			}
			if(ipPolicyTree != null && ipPolicyTree.getIpPolicyObj() != null){
				configure.getIpPolicy().add(ipPolicyTree.getIpPolicyObj());
			}
		}
	}

	private void generateLoggingTree(GenerateXMLDebug oDebug) throws Exception {
		CreateLogingTree loggingTree = null;
		try{
			loggingTree = new CreateLogingTree(profileFunction.getLogingProfileImpl(), oDebug);
			loggingTree.generate();
		}catch(Exception e){
			if(loggingTree != null){
				configure.setLogging(loggingTree.getLoggingObj());
			}
			throw e;
		}
		if(loggingTree != null){
			configure.setLogging(loggingTree.getLoggingObj());
		}
	}

	private void generateMacPolicyTree(GenerateXMLDebug oDebug) throws Exception {
		for (MacPolicyProfileImpl macPolicyImp : profileFunction
				.getMacPolicyProfileImplList()) {
			
			CreateMacPolicyTree macPolicyTree = null;
			try{
				macPolicyTree = new CreateMacPolicyTree(macPolicyImp, oDebug);
				macPolicyTree.generate();
			}catch(Exception e){
				if(macPolicyTree != null && macPolicyTree.getMacPolicyObj() != null){
					configure.getMacPolicy().add(macPolicyTree.getMacPolicyObj());
				}
				throw e;
			}
			if(macPolicyTree != null && macPolicyTree.getMacPolicyObj() != null){
				configure.getMacPolicy().add(macPolicyTree.getMacPolicyObj());
			}
		}
	}

	private void generateMobilityPolicyTree(GenerateXMLDebug oDebug) throws Exception {
		for (MobilityPolicyProfileImpl mobilityPolicyImpl : profileFunction
				.getMobilityPolicyProfileImplList()) {
			
			CreateMobilityPolicyTree mobilityPolicyTree = null;
			try{
				mobilityPolicyTree = new CreateMobilityPolicyTree(mobilityPolicyImpl, oDebug);
				mobilityPolicyTree.generate();
			}catch(Exception e){
				if(mobilityPolicyTree != null && mobilityPolicyTree.getMobilityPolicyObj() != null){
					configure.getMobilityPolicy().add(
							mobilityPolicyTree.getMobilityPolicyObj());
				}
				throw e;
			}
			if(mobilityPolicyTree != null && mobilityPolicyTree.getMobilityPolicyObj() != null){
				configure.getMobilityPolicy().add(
						mobilityPolicyTree.getMobilityPolicyObj());
			}
		}
	}

	private void generateMobilityThresholdTree(GenerateXMLDebug oDebug) throws Exception {
		CreateMobilityThresholdTree mobilityThresholdTree = null;
		try{
			mobilityThresholdTree = new CreateMobilityThresholdTree(profileFunction.getMobilityThresholdProfileImpl(), oDebug);
			mobilityThresholdTree.generate();
		}catch(Exception e){
			if(mobilityThresholdTree != null){
				configure.setMobilityThreshold(mobilityThresholdTree.getMobilityThresholdObj());
			}
			throw e;
		}
		if(mobilityThresholdTree != null){
			configure.setMobilityThreshold(mobilityThresholdTree
					.getMobilityThresholdObj());
		}
	}

	private void generateNtpTree(GenerateXMLDebug oDebug) throws Exception {
		CreateNtpTree ntpTree = null;
		try{
			ntpTree = new CreateNtpTree(profileFunction.getNtpProfileImpl(), oDebug);
			ntpTree.generate();
		}catch(Exception e){
			if(ntpTree != null){
				configure.setNtp(ntpTree.getNtpObj());
			}
			throw e;
		}
		if(ntpTree != null){
			configure.setNtp(ntpTree.getNtpObj());
		}
	}

	private void generateRadioTree(GenerateXMLDebug oDebug) throws Exception {
		CreateRadioTree radioTree = null;
		try{
			radioTree = new CreateRadioTree(profileFunction.getRadioProfileImpl(), oDebug);
			radioTree.generate();
		}catch(Exception e){
			if(radioTree != null){
				configure.setRadio(radioTree.getRadioObj());
			}
			throw e;
		}
		if(radioTree != null){
			configure.setRadio(radioTree.getRadioObj());
		}
	}

	private void generateRoamingTree(GenerateXMLDebug oDebug) throws Exception {
		CreateRoamingTree roamingTree = null;
		try{
			roamingTree = new CreateRoamingTree(profileFunction.getRoamingProfileImpl(), oDebug);
			roamingTree.generate();
		}catch(Exception e){
			if(roamingTree != null){
				configure.setRoaming(roamingTree.getRoamingObj());
			}
			throw e;
		}
		if(roamingTree != null){
			configure.setRoaming(roamingTree.getRoamingObj());
		}
	}

	private void generateRouteTree(GenerateXMLDebug oDebug) throws Exception {
		for (RouteProfileImpl routeImpl : profileFunction
				.getRouteProfileImplList()) {
			CreateRouteTree routeTree = null;
			try{
				routeTree = new CreateRouteTree(routeImpl, oDebug);
				routeTree.generate();
			}catch(Exception e){
				if(routeTree != null && routeTree.getRouteObj() != null){
					configure.getRoute().add(routeTree.getRouteObj());
				}
				throw e;
			}
			if(routeTree != null && routeTree.getRouteObj() != null){
				configure.getRoute().add(routeTree.getRouteObj());
			}
		}
	}

	private void generateSnmpTree(GenerateXMLDebug oDebug) throws Exception {
		CreateSnmpProfileTree snmpTree = null;
		try{
			snmpTree = new CreateSnmpProfileTree(profileFunction.getSnmpProfileImpl(), oDebug);
			snmpTree.generate();
		}catch(Exception e){
			if(snmpTree != null){
				configure.setSnmp(snmpTree.getSnmpObj());
			}
			throw e;
		}
		if(snmpTree != null){
			configure.setSnmp(snmpTree.getSnmpObj());
		}
	}

	private void generateClockTree(GenerateXMLDebug oDebug) throws Exception {
		CreateClockTree clockTree = null;
		try{
			 clockTree = new CreateClockTree(profileFunction.getClockProfileImpl(), oDebug);
			 clockTree.generate();
		}catch(Exception e){
			if(clockTree != null){
				configure.setClock(clockTree.getClockObj());
			}
			throw e;
		}
		if(clockTree != null){
			configure.setClock(clockTree.getClockObj());
		}
	}

	private void generateQosTree(GenerateXMLDebug oDebug) throws Exception {
		CreateQosTree qosTree = null;
		
		try{
			qosTree = new CreateQosTree(profileFunction.getQosProfileImpl(), oDebug);
			qosTree.generate();
		}catch(Exception e){
			if(qosTree != null){
				configure.setQos(qosTree.getQosObj());
			}
			throw e;
		}
		if(qosTree != null){
			configure.setQos(qosTree.getQosObj());
		}
	}

	private void generateServiceTree(GenerateXMLDebug oDebug) throws Exception {
		for (ServiceProfileImpl serviceImpl : profileFunction
				.getServiceProfileImplList()) {
			CreateServiceTree serviceTree = null;
			try{
				serviceTree = new CreateServiceTree(serviceImpl, oDebug);
				serviceTree.generate();
			}catch(Exception e){
				if(serviceTree != null && serviceTree.getServiceObj() != null){
					configure.getService().add(serviceTree.getServiceObj());
				}
				throw e;
			}
			if(serviceTree != null){
				configure.getService().add(serviceTree.getServiceObj());
			}
		}
	}

	private void generateResetButtonTree(GenerateXMLDebug oDebug) throws Exception {
		CreateResetButtonTree resetButtonTree = null;
		try{
			resetButtonTree = new CreateResetButtonTree(profileFunction.getResetButtonImpl(), oDebug);
			resetButtonTree.generate();
		}catch(Exception e){
			if(resetButtonTree != null){
				configure.setResetButton(resetButtonTree.getResetButtonObj());
			}
			throw e;
		}
		if(resetButtonTree != null){
			configure.setResetButton(resetButtonTree.getResetButtonObj());
		}
	}
	
	private void generateIpNatPolicyTree(GenerateXMLDebug oDebug) throws Exception {
		CreateIpNatPolicyTree ipNatPolicyTree = null;
		try{
			ipNatPolicyTree = new CreateIpNatPolicyTree(profileFunction.getIpProfileImpl(), oDebug);
			ipNatPolicyTree.generate();
		}catch(Exception e){
			if(ipNatPolicyTree != null){
				configure.setIpNatPolicy(ipNatPolicyTree.getIpNatPolicyObj());
			}
			throw e;
		}
		if(ipNatPolicyTree != null){
			configure.setIpNatPolicy(ipNatPolicyTree.getIpNatPolicyObj());
		}
	}

	private void generateIpTree(GenerateXMLDebug oDebug) throws Exception {
		CreateIpTree ipTree = null;
		try{
			ipTree = new CreateIpTree(profileFunction.getIpProfileImpl(), oDebug);
			ipTree.generate();
		}catch(Exception e){
			if(ipTree != null){
				configure.setIp(ipTree.getIpObj());
			}
			throw e;
		}
		if(ipTree != null){
			configure.setIp(ipTree.getIpObj());
		}
	}

	private void generateAlgTree(GenerateXMLDebug oDebug) throws Exception {
		CreateAlgTree algTree = null;
		try{
			 algTree = new CreateAlgTree(profileFunction.getAlgProfileImpl(), oDebug);
			 algTree.generate();
		}catch(Exception e){
			if(algTree != null){
				configure.setAlg(algTree.getAlgObj());
			}
			throw e;
		}
		if (algTree != null) {
			configure.setAlg(algTree.getAlgObj());
		}
	}
	
	private void generatePseTree(GenerateXMLDebug oDebug) throws Exception {
		CreatePseTree pseTree = null;
		try{
			 pseTree = new CreatePseTree(profileFunction.getPseProfileImpl(), oDebug);
			 pseTree.generate();
		}catch(Exception e){
			if(pseTree != null){
				configure.setPse(pseTree.getPseObj());
			}
			throw e;
		}
		if (pseTree != null) {
			configure.setPse(pseTree.getPseObj());
		}
	}
	
	private void generate8021XMacTableTree(GenerateXMLDebug oDebug) throws Exception {
		Create8021XMacTableTree macTableTree = null;
		try{
			macTableTree = new Create8021XMacTableTree(profileFunction.getMacTableProfileImpl(), oDebug);
			macTableTree.generate();
		}catch(Exception e){
			if(macTableTree != null){
				configure.set8021XMacTable(macTableTree.getEthx802Dot1XMacTableObj());
			}
			throw e;
		}
		if (macTableTree != null) {
			configure.set8021XMacTable(macTableTree.getEthx802Dot1XMacTableObj());
		}		
	}

	private void generateLocationTree(GenerateXMLDebug oDebug) throws Exception {
		CreateLocationTree locationTree = null;
		try{
			locationTree = new CreateLocationTree(profileFunction.getLocationProfileImpl(), oDebug);
			locationTree.generate();
		}catch(Exception e){
			if(locationTree != null){
				configure.setLocation(locationTree.getLocationObj());
			}
			throw e;
		}
		if(locationTree != null){
			configure.setLocation(locationTree.getLocationObj());
		}
	}

	private void generateForwardingEngineTree(GenerateXMLDebug oDebug) throws Exception {
		CreateForwardingEngineTree forwardingEngineTree = null;
		try{
			forwardingEngineTree = new CreateForwardingEngineTree(profileFunction.getForwardingEngineImpl(), oDebug);
			forwardingEngineTree.generate();
		}catch(Exception e){
			if(forwardingEngineTree != null){
				configure.setForwardingEngine(forwardingEngineTree
						.getForwardingEngineObj());
			}
			throw e;
		}
		if(forwardingEngineTree != null){
			configure.setForwardingEngine(forwardingEngineTree
					.getForwardingEngineObj());
		}
	}

	private void generateCapwapTree(GenerateXMLDebug oDebug) throws Exception {
		CreateCapwapTree capwapTree = null;
		try{
			capwapTree = new CreateCapwapTree(profileFunction.getCapwapProfileImpl(), oDebug);
			capwapTree.generate();
		}catch(Exception e){
			if(capwapTree != null){
				configure.setCapwap(capwapTree.getCapwapObj());
			}
			throw e;
		}
		if(capwapTree != null){
			configure.setCapwap(capwapTree.getCapwapObj());
		}
	}

	private void generateHivemanagerTree(GenerateXMLDebug oDebug) throws Exception {
		CreateHivemanagerTree hivemanagerTree = null;
		try{
			hivemanagerTree = new CreateHivemanagerTree(profileFunction.getHivemanagerProfileImpl(), oDebug);
			hivemanagerTree.generate();
		}catch(Exception e){
			if(hivemanagerTree != null){
				configure.setHivemanager(hivemanagerTree.getHivemanagerObj());
			}
			throw e;
		}
		if(hivemanagerTree != null){
			configure.setHivemanager(hivemanagerTree.getHivemanagerObj());
		}
	}

	private void generateHostnameTree(GenerateXMLDebug oDebug) throws Exception {
		CreateHostnameTree hostnameTree = null;
		try{
			hostnameTree = new CreateHostnameTree(profileFunction.getHostnameProfileImpl(), oDebug);
			hostnameTree.generate();
		}catch(Exception e){
			if(hostnameTree != null){
				configure.setHostname(hostnameTree.getHostnameObj());
			}
			throw e;
		}
		if(hostnameTree != null){
			configure.setHostname(hostnameTree.getHostnameObj());
		}
	}

	private void generateConsoleTree(GenerateXMLDebug oDebug) throws Exception {
		CreateConsoleTree consoleTree = null;
		try{
			consoleTree = new CreateConsoleTree(profileFunction.getConsoleProfileImp(), oDebug);
			consoleTree.generate();
		}catch(Exception e){
			if(consoleTree != null){
				configure.setConsole(consoleTree.getConsoleObj());
			}
			throw e;
		}
		if(consoleTree != null){
			configure.setConsole(consoleTree.getConsoleObj());
		}
	}

	private void generateSystemTree(GenerateXMLDebug oDebug) throws Exception {
		CreateSystemTree systemTree = null;
		try{
			systemTree = new CreateSystemTree(profileFunction.getsystemProfileImpl(), oDebug);
			systemTree.generate();
		}catch(Exception e){
			if(systemTree != null){
				configure.setSystem(systemTree.getSystemObj());
			}
			throw e;
		}
		if(systemTree != null){
			configure.setSystem(systemTree.getSystemObj());
		}
	}

	private void generateTrackTree(GenerateXMLDebug oDebug) throws Exception {
		List<TrackProfileInt> trackList = profileFunction.getTrackProfileImplList();
		
		if (trackList != null && trackList.size() > 0) {
			for (TrackProfileInt trackObj : trackList) {
				CreateTrackProfileTree trackTree = null;
				try{
					trackTree = new CreateTrackProfileTree(trackObj, oDebug);
					trackTree.generate();
				}catch(Exception e){
					if(trackTree != null && trackTree.getTrackObj() != null){
						configure.getTrack().add(trackTree.getTrackObj());
					}
					throw e;
				}
				if(trackTree != null && trackTree.getTrackObj() != null){
					configure.getTrack().add(trackTree.getTrackObj());
				}
			}
		}
	}
	
	private void generateTrackWanTree(GenerateXMLDebug oDebug) throws Exception {
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.1.0") < 0) {
			return;
		}
		List<TrackProfileInt> trackList = profileFunction.getTrackWanProfileImplList();
		if (trackList != null && trackList.size() > 0) {
			for (TrackProfileInt trackObj : trackList) {
				CreateTrackWanProfileTree trackTree = null;
				try{
					trackTree = new CreateTrackWanProfileTree(trackObj, oDebug);
                    trackTree.setHiveAp(hiveAp);
					trackTree.generate();
				} finally {
					if(trackTree != null && trackTree.getTrackWanObj() != null){
						configure.getTrackWan().add(trackTree.getTrackWanObj());
					}
				}
			}
		}
	}

	private void generateCacTree(GenerateXMLDebug oDebug) throws Exception {
		CreateCacTree cacTree = null;
		try{
			cacTree = new CreateCacTree(profileFunction.getCacProfileImpl(), oDebug);
			cacTree.generate();
		}catch(Exception e){
			if(cacTree != null){
				configure.setCac(cacTree.getCacObj());
			}
			throw e;
		}
		if(cacTree != null){
			configure.setCac(cacTree.getCacObj());
		}
	}

	private void generateAccessConsoleTree(GenerateXMLDebug oDebug) throws Exception {
		CreateAccessConsoleTree accessConsoleTree = null;
		try{
			accessConsoleTree = new CreateAccessConsoleTree(profileFunction.getAccessConsoleImpl(), oDebug);
			accessConsoleTree.generate();
		}catch(Exception e){
			if(accessConsoleTree != null){
				configure.setAccessConsole(accessConsoleTree.getAccessConsoleObj());
			}
			throw e;
		}
		if(accessConsoleTree != null){
			configure.setAccessConsole(accessConsoleTree.getAccessConsoleObj());
		}
		
	}

	private void generateLldpTree(GenerateXMLDebug oDebug) throws Exception {
		CreateLldpTree lldpTree = null;
		try{
			lldpTree = new CreateLldpTree(profileFunction.getLldpProfileImpl(), oDebug);
			lldpTree.generate();
		}catch(Exception e){
			if(lldpTree != null){
				configure.setLldp(lldpTree.getLldpObj());
			}
			throw e;
		}
		if(lldpTree != null){
			configure.setLldp(lldpTree.getLldpObj());
		}
	}
	
	private void generateUserGroupTree(GenerateXMLDebug oDebug) throws Exception{
		for(UserGroupProfileImpl userGroupImpl : profileFunction.getUserGroupList()){
			CreateUserGroupTree pskUserTree =null;
			try{
//				pskUserTree = new CreateUserGroupTree(userGroupImpl, oDebug);
				pskUserTree = new CreateUserGroupTree(userGroupImpl);
//				pskUserTree.generate();
			}catch(Exception e){
				if(pskUserTree != null && pskUserTree.getUserGroupObj() != null){
					configure.getUserGroup().add(pskUserTree.getUserGroupObj());
				}
				throw e;
			}
			if(pskUserTree != null && pskUserTree.getUserGroupObj() != null){
				configure.getUserGroup().add(pskUserTree.getUserGroupObj());
			}
		}
	}
	
	private void generateVpnTree(GenerateXMLDebug oDebug, boolean isView) throws Exception{
		CreateVPNTree vpnTree = null;
		try{
			vpnTree = new CreateVPNTree(profileFunction.getVpnProfileImpl(), oDebug, isView);
			vpnTree.generate();
		}catch(Exception e){
			if(vpnTree != null){
				configure.setVpn(vpnTree.getVpnObj());
			}
			throw e;
		}
		if(vpnTree != null){
			configure.setVpn(vpnTree.getVpnObj());
		}
	}
	
	private void generateAirscreenTree(GenerateXMLDebug oDebug) throws Exception{
		CreateAirScreen airScreenObj = null;
		try{
			airScreenObj = new CreateAirScreen(profileFunction.getAirScreenProfileImpl(), oDebug);
			airScreenObj.generate();
		}catch(Exception e){
			if(airScreenObj != null && airScreenObj.getAirScreenObj() != null){
				configure.setAirscreen(airScreenObj.getAirScreenObj());
			}
			throw e;
		}
		if(airScreenObj != null && airScreenObj.getAirScreenObj() != null){
			configure.setAirscreen(airScreenObj.getAirScreenObj());
		}
	}
	
	private void generatePerformanceSentinelTree(GenerateXMLDebug oDebug) throws Exception{
		CreatePerformanceSentinelTree performanceObj = null;
		try{
			performanceObj = new CreatePerformanceSentinelTree(profileFunction.getPerformanceSentinelImpl(), oDebug);
			performanceObj.generate();
		}catch(Exception e){
			if(performanceObj != null && performanceObj.getPerformanceSentinelObj() != null){
				configure.setPerformanceSentinel(performanceObj.getPerformanceSentinelObj());
			}
			throw e;
		}
		if(performanceObj != null && performanceObj.getPerformanceSentinelObj() != null){
			configure.setPerformanceSentinel(performanceObj.getPerformanceSentinelObj());
		}
	}
	
	private void generateReportTree(GenerateXMLDebug oDebug) throws Exception{
		CreateReportTree reportTree = null;
		try{
			reportTree = new CreateReportTree(profileFunction.getReportProfileImpl(), oDebug);
			reportTree.generate();
		}catch(Exception ex){
			if(reportTree != null && reportTree.getReportObj() != null){
				configure.setReport(reportTree.getReportObj());
			}
			throw ex;
		}
		if(reportTree != null && reportTree.getReportObj() != null){
			configure.setReport(reportTree.getReportObj());
		}
	}
	
	private void generateUserTree(GenerateXMLDebug oDebug) throws Exception{
		for(UserImpl userImpl : profileFunction.getUserList()){
			CreateUserTree userTree = null;
			try{
				userTree = new CreateUserTree(userImpl, oDebug);
				userTree.generate();
			}catch(Exception e){
				if(userTree != null && userTree.getUserObj() != null){
					configure.getUser().add(userTree.getUserObj());
				}
				throw e;
			}
			if(userTree != null && userTree.getUserObj() != null){
				configure.getUser().add(userTree.getUserObj());
			}
		}
	}
	
	private void generateAutoPskGroupTree(GenerateXMLDebug oDebug) throws Exception{
		for(PskAutoUserGroupImpl autoGroup : profileFunction.getPskAutoUserGroupList()){
			CreatePskAutoUserGroup autoGroupTree = null;
			try{
				autoGroupTree = new CreatePskAutoUserGroup(autoGroup, oDebug);
				autoGroupTree.generate();
			}catch(Exception e){
				if(autoGroupTree != null && autoGroupTree.getAutoPskUserGroupObj() != null){
					configure.getAutoPskUserGroup().add(autoGroupTree.getAutoPskUserGroupObj());
				}
				throw e;
			}
			if(autoGroupTree != null && autoGroupTree.getAutoPskUserGroupObj() != null){
				configure.getAutoPskUserGroup().add(autoGroupTree.getAutoPskUserGroupObj());
			}
		}
	}
	
	private void generateVlanProfileTree() throws Exception{
		for(VlanProfileInt vlanImpl : profileFunction.getVlanImplList()){
			CreateVlanTree vlanTree = null;
			try{
				vlanTree = new CreateVlanTree(vlanImpl);
			}catch(Exception e){
				if(vlanTree != null && vlanTree.getVlanObj() != null){
					configure.getVlan().add(vlanTree.getVlanObj());
				}
				throw e;
			}
			if(vlanTree != null && vlanTree.getVlanObj() != null){
				configure.getVlan().add(vlanTree.getVlanObj());
			}
		}
	}
	
	private void generateMonitorTree() throws Exception {
		CreateMonitorTree monitorTree = null;
		try{
			monitorTree = new CreateMonitorTree(profileFunction.getMonitorImpl());
			monitorTree.generate();
		}catch(Exception ex){
			if(monitorTree != null && monitorTree.getMonitorObj() != null){
				configure.setMonitor(monitorTree.getMonitorObj());
			}
			throw ex;
		}
		if(monitorTree != null && monitorTree.getMonitorObj() != null){
			configure.setMonitor(monitorTree.getMonitorObj());
		}
	}
	
	private void generateVlanReserveTree() throws Exception {
		CreateVlanReserveTree vlanReserveTree = null;
		try{
			vlanReserveTree = new CreateVlanReserveTree(profileFunction.getVlanReserveImpl());
			vlanReserveTree.generate();
		}catch(Exception e){
			if(vlanReserveTree != null && vlanReserveTree.getVlanReserve() != null){
				configure.setVlanReserve(vlanReserveTree.getVlanReserve());
			}
			throw e;
		}
		if(vlanReserveTree != null && vlanReserveTree.getVlanReserve() != null){
			configure.setVlanReserve(vlanReserveTree.getVlanReserve());
		}
	}
	
	private void generateSpanningTree() throws Exception {
		CreateSpanningTree spanningTree = null;
		try{
			spanningTree = new CreateSpanningTree(profileFunction.getSpanningTreeImpl());
			spanningTree.generate();
		}catch(Exception e){
			if(spanningTree != null && spanningTree.getSpanningTreeObj() != null){
				configure.setSpanningTree(spanningTree.getSpanningTreeObj());
			}
			throw e;
		}
		if(spanningTree != null && spanningTree.getSpanningTreeObj() != null){
			configure.setSpanningTree(spanningTree.getSpanningTreeObj());
		}
	}
	
	private void generateStromControlTree() throws Exception {
		CreateStrormControlTree stormTree = null;
		try{
			stormTree = new CreateStrormControlTree(profileFunction.getStromControlImpl());
			stormTree.generate();
		}catch(Exception e){
			if(stormTree != null && stormTree.getStormControlObj() != null){
				configure.setStormControl(stormTree.getStormControlObj());
			}
			throw e;
		}
		if(stormTree != null && stormTree.getStormControlObj() != null){
			configure.setStormControl(stormTree.getStormControlObj());
		}
	}
	
	private void generateKddrTree() throws Exception {
		CreateKddrTree kddrTree = null;
		try{
			kddrTree = new CreateKddrTree(profileFunction.getKddrImpl());
			kddrTree.generate();
		}catch(Exception e){
			if(kddrTree != null && kddrTree.getKddrObj() != null){
				configure.setKddr(kddrTree.getKddrObj());
			}
			throw e;
		}
		if(kddrTree != null && kddrTree.getKddrObj() != null){
			configure.setKddr(kddrTree.getKddrObj());
		}
	}
	
//	private void generateSsidUserGroupBindTree(){
//		for(SsidBindUserGroupImpl ssidBind : profileFunction.getSsidUserGroupBindList()){
//			CreateSsidBindUserGroupTree ssidBindTree = new CreateSsidBindUserGroupTree(ssidBind);
//			configure.getSsid().add(ssidBindTree.getSsidObj());
//		}
//	}
//	
//	private void generateRadiusBindGroupTree(){
//		CreateRadiusBindGroupTree radiusGroupTree = new CreateRadiusBindGroupTree(profileFunction.getRadiusBindGroupImpl());
//		configure.setAaa(radiusGroupTree.getAaaObj());
//	}

	
	private void limitBr100User(){
		if(this.hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
			return;
		}
		
		while(configure.getUser().size() > LocalUser.MAX_COUNT_BR100_USERPERBR){
			configure.getUser().remove(configure.getUser().size() - 1);
		}
		int remainCount = LocalUser.MAX_COUNT_BR100_USERPERBR - configure.getUser().size();
		
		for(AutoPskUserGroupObj group : configure.getAutoPskUserGroup()){
			if(group.getAutoGeneration() == null || group.getAutoGeneration().getIndexRange() == null ||
					group.getAutoGeneration().getIndexRange().isEmpty()){
				continue;
			}
			List<AhNameActValueQuoteProhibited> rmList = new ArrayList<AhNameActValueQuoteProhibited>();
			for(AhNameActValueQuoteProhibited rangeObj : group.getAutoGeneration().getIndexRange()){
				if(remainCount <= 0){
					rmList.add(rangeObj);
					continue;
				}
				if(rangeObj.getName() == null){
					continue;
				}
				String strRange = rangeObj.getName();
				if(strRange == null || "".equals(strRange)){
					continue;
				}
				if(!strRange.contains(" ")){
					remainCount--;
					continue;
				}
				String[] rangeArg = strRange.split(" ");
				int start = Integer.valueOf(rangeArg[0]);
				int end = Integer.valueOf(rangeArg[1]);
				int counts = end - start + 1;
				if(counts <= remainCount){
					remainCount -= counts;
					continue;
				}else{
					end = start + remainCount - 1;
					rangeObj.setName(String.valueOf(start) + " " + String.valueOf(end));
					remainCount = 0;
					continue;
				}
				
			}
			if(!rmList.isEmpty()){
				group.getAutoGeneration().getIndexRange().removeAll(rmList);
			}
		}
	}

}