/**
 *@filename		BeConfigModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:50:38 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.BaseModule;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.GenerateXML;
import com.ah.be.config.create.bootstrap.GenerateBootstrap;
import com.ah.be.config.create.cli.HmCliSpecialHandling;
import com.ah.be.config.create.common.CompleteRunXml;
import com.ah.be.config.create.common.postprocess.AbstractCLIPostProcess;
import com.ah.be.config.create.common.postprocess.CLIPostProcess;
import com.ah.be.config.create.common.postprocess.ParseCLIPostProcess;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.event.impl.AhConfigEventMgmtImpl;
import com.ah.be.config.hiveap.UpdateManager;
import com.ah.be.config.hiveap.UpdateObjectBuilder;
import com.ah.be.config.hiveap.UpdatePeriodicTask;
import com.ah.be.config.hiveap.UpdateResponseListener;
import com.ah.be.config.hiveap.distribution.ImageDistributor;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.config.image.ImageManager;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.config.xml.compare.AhConfigComparator;
import com.ah.be.config.xml.compare.AhConfigComparedException;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.DownloadInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
//import com.ah.be.config.create.source.impl.PskAutoUserGroupImpl;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeConfigModuleImpl extends BaseModule implements BeConfigModule {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(BeConfigModuleImpl.class.getSimpleName());

	private UpdateResponseListener updateResponseListener;

	private UpdateManager updateManager;

	private UpdatePeriodicTask updatePeriodic;

	private AhConfigEventMgmtImpl configMgmt;

	private ImageDistributor imageDistributor;

	private UpdateObjectBuilder updateObjectBuilder;
	
	private ProvisionProcessor provisionProcessor;
	
	private ImageManager ImageSynupLS;

	/**
	 * Constructor
	 */
	public BeConfigModuleImpl() {
		setModuleId(BaseModule.ModuleID_Config);
		setModuleName("BeConfigModule");
	}

	@Override
	public boolean init() {
		imageDistributor = new ImageDistributor();
		updateResponseListener = new UpdateResponseListener();
		updateManager = new UpdateManager();
		updatePeriodic = new UpdatePeriodicTask();
		updateObjectBuilder = new UpdateObjectBuilder();
		configMgmt = new AhConfigEventMgmtImpl();
		provisionProcessor = new ProvisionProcessor();
		ImageSynupLS = new ImageManager();

		return true;
	}

	@Override
	public boolean run() {
		if (configMgmt != null && !configMgmt.isStarted()) {
			List<?> concurrentConfigGenNums = QueryUtil.executeQuery("select concurrentConfigGenNum from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), 1);
			int concurrentConfigGenNum;

			if (!concurrentConfigGenNums.isEmpty()) {
				concurrentConfigGenNum = (Byte) concurrentConfigGenNums.get(0);

				if (concurrentConfigGenNum < HMServicesSettings.MIN_CONCURRENT_CONFIG_GEN_NUM || concurrentConfigGenNum > HMServicesSettings.MAX_CONCURRENT_CONFIG_GEN_NUM) {
					log.warn("The configured number '" + concurrentConfigGenNum + "' was invalid. It must be >= " + HMServicesSettings.MIN_CONCURRENT_CONFIG_GEN_NUM + " & <= " + HMServicesSettings.MAX_CONCURRENT_CONFIG_GEN_NUM + ", using default number " + HMServicesSettings.DEFAULT_CONCURRENT_CONFIG_GEN_NUM + " instead.");
					concurrentConfigGenNum = HMServicesSettings.DEFAULT_CONCURRENT_CONFIG_GEN_NUM;
				}
			} else {
				concurrentConfigGenNum = HMServicesSettings.DEFAULT_CONCURRENT_CONFIG_GEN_NUM;
			}

			configMgmt.start(concurrentConfigGenNum);
		}

		if (updateManager != null) {
			updateManager.start();
		}

		if (updateResponseListener != null && !updateResponseListener.isStart()) {
			updateResponseListener.start();
		}

		if (updatePeriodic != null && !updatePeriodic.isStart()) {
			updatePeriodic.start();
		}
		
		if (ImageSynupLS != null && !ImageSynupLS.isStart()){
			ImageSynupLS.start();
		}

		return true;
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		if (event.isShutdownRequestEvent()) {
			shutdown();

			return;
		}

		switch (event.getEventType()) {
		case BeEventConst.COMMUNICATIONEVENTTYPE:
			switch (((BeCommunicationEvent) event).getMsgType()) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_FILEDOWNLOADPROGRESS:
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
			case BeCommunicationConstant.MESSAGETYPE_ABORTRSP:
			case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP:
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT:
				updateResponseListener.addResponseEvent(event);
				break;
			default:
				break;
			}
			break;
		case BeEventConst.AH_BOOTSTRAP_GENERATED_EVENT:
		case BeEventConst.AH_DELTA_CONFIG_GENERATED_EVENT:
		case BeEventConst.AH_CONFIG_GENERATED_EVENT:
		case BeEventConst.AH_DISCOVERY_EVENT:
		case BeEventConst.AH_CONFIG_GENERATION_PROGRESS_EVENT:
		case BeEventConst.AH_CLOUD_AUTH_UPDATE_EVENT:
		case BeEventConst.AH_DEVICE_REBOOT_RESULT_EVENT:
			updateResponseListener.addResponseEvent(event);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean shutdown() {
		if (updatePeriodic != null) {
			updatePeriodic.stop();
		}

		if (updateResponseListener != null) {
			updateResponseListener.stop();
		}

		if (updateManager != null) {
			updateManager.stop();
		}

		if (configMgmt != null) {
			configMgmt.stop();
		}
		
		if(ImageSynupLS != null){
			ImageSynupLS.shutdown();
		}

		return true;
	}

	public AhConfigEventMgmtImpl getConfigMgmt() {
		return configMgmt;
	}

	public String fetchConfig(HiveAp hiveAp, ConfigFetchType fetchType, int timeout)
			throws AhConfigRetrievedException, AhConfigDisconnectException {
		String cfgType;
		List<String> showCfgCmdList = new ArrayList<>();;

		switch (fetchType) {
		case RUNNING:
			cfgType = "running config";
			showCfgCmdList.add(AhCliFactory.showRunningConfig(hiveAp.getSoftVer(), true));
			if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.5.0") >= 0){
				showCfgCmdList.add(AhCliFactory.showVpnUserConfig());
			}
			break;
		case USER:
			cfgType = "user config";
			showCfgCmdList.add(AhCliFactory.showUserConfig(hiveAp.getSoftVer(), true, true));
			break;
		default:
			throw new AssertionError("Unexpected Config Fetch Type: " + fetchType.toString());
		}

		if (hiveAp.isSimulated()) {
			throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
					"warn.simulated.ap.audit.config.nonsupport", "Audit Config"));
		}
		
		StringBuffer resClisBuff = new StringBuffer();
		
		for(String cliStr : showCfgCmdList){
			BeCliEvent fetchCfgReq = new BeCliEvent();
			int sequenceNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
			fetchCfgReq.setAp(hiveAp);
			fetchCfgReq.setClis(new String[] {cliStr});
			fetchCfgReq.setSequenceNum(sequenceNum);
			log.info("fetchConfig", "Fetching " + cfgType + " from HiveAP " + hiveAp);

			try {
				fetchCfgReq.buildPacket();
			} catch (Exception e) {
				log.error("fetchConfig", "Failed to build request to fetch the " + cfgType
						+ " from HiveAP " + hiveAp, e);
				throw new AhConfigRetrievedException("Failed to build request to fetch the " + cfgType
						+ " from the " + NmsUtil.getOEMCustomer().getAccessPonitName() + " you selected.", e);
			}

			BeCommunicationEvent fetchConfigResp = AhAppContainer.getBeCommunicationModule()
					.sendSyncRequest(fetchCfgReq, timeout);

			if (fetchConfigResp == null) {
				throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
						"error.config.fetch.failed", cfgType));
			}

			int respMsgType = fetchConfigResp.getMsgType();

			switch (respMsgType) {
				case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
					try {
						fetchConfigResp.parsePacket();
					} catch (Exception e) {
						log.error("fetchConfig", "Failed to parse the response of " + cfgType
								+ " retrieval for HiveAP " + hiveAp);
						throw new AhConfigRetrievedException("Parsing the response of " + cfgType
								+ " retrieval failure.", e);
					}

					BeCapwapCliResultEvent cliRetEvent = (BeCapwapCliResultEvent) fetchConfigResp;

					if (!cliRetEvent.isCliSuccessful()) {
						String errorCli = cliRetEvent.getErrorCli();
						log.error("fetchConfig", "Failed to fetch the " + cfgType + " from HiveAP " + hiveAp
								+ " using CLI: " + errorCli);
						throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
								"error.config.fetch.failed", cfgType));
					}

					log.info("fetchConfig", "Successfully fetched " + cfgType + " from HiveAP " + hiveAp);

					String cliResult = cliRetEvent.getCliSucceedMessage();

					if (!StringUtils.isEmpty(cliResult)) {
						if(resClisBuff.length() == 0){
							resClisBuff.append(cliResult);
						}else{
							resClisBuff.append("\n").append(cliResult);
						}
					}
					break;
				case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				default:
					String errorMsg;
					byte configFetchResult = fetchConfigResp.getResult();
					log.error("fetchConfig", "Failed to fetch the " + cfgType + " from HiveAP " + hiveAp + ". Msg Type: " + respMsgType + "; Error Code: " + configFetchResult);

					switch (configFetchResult) {
						case BeCommunicationConstant.RESULTTYPE_NOFSM:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.ap.disconnected", cfgType);
							break;
						case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.ap.connecting", cfgType);
							break;
						case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
						case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.request.timeout", cfgType);
							break;
						case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.capwap.server.disconnected", cfgType);
							break;
						case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.request.rejected", cfgType);
							break;
						default:
							errorMsg = MgrUtil.getUserMessage("error.config.fetch.failed", cfgType);
							break;
					}

					throw new AhConfigDisconnectException(errorMsg);
			}
		}
		
		return resClisBuff.toString();
	}

	public String fetchRunningConfig(HiveAp hiveAp) throws AhConfigRetrievedException, AhConfigDisconnectException {
		String fetchClis = fetchConfig(hiveAp, ConfigFetchType.RUNNING, 300);

		// Fix the SNMP issue in the HM3.5r4 release.
		fetchClis = HmCliSpecialHandling.runningConfigReplace(fetchClis);

		return fetchClis;
	}

	public String fetchUserConfig(HiveAp hiveAp) throws AhConfigRetrievedException, AhConfigDisconnectException {
		String softVer = hiveAp.getSoftVer();

		if (NmsUtil.compareSoftwareVersion("3.2.0.0", softVer) > 0) {
			throw new AhConfigRetrievedException("Operation denied! The "+NmsUtil.getOEMCustomer().getAccessPonitName()+" with version "
					+ softVer + " doesn't support user configuration setting.");
		}

		return fetchConfig(hiveAp, ConfigFetchType.USER, 300);
	}

	public void generateBootstrapXmlConfig(AhBootstrapGeneratedEvent event)
			throws AhConfigGeneratedException {
		try {
			HiveAp hiveAp = event.getHiveAp();
			String domainName = hiveAp.getOwner().getDomainName();
			String hiveApMac = hiveAp.getMacAddress();
			String bootstrapXmlCfgPath = AhConfigUtil.getBootstrapXmlConfigPath(domainName,
					hiveApMac);
			GenerateBootstrap generator = new GenerateBootstrap(event);
			generator.generateBootstrapXmlConfig(bootstrapXmlCfgPath);
		} catch (Exception e) {
			throw new AhConfigGeneratedException(
					"Failed to generate bootstrap XML-formatted config.", e);
		}
	}

	public AhConfigGenerationResult generateXmlConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, String softVer, boolean isView) throws AhConfigGeneratedException {
		try {
			long startTime = System.currentTimeMillis();
			GenerateXML generator = new GenerateXML(hiveAp, configType, softVer, isView);
			AhConfigGenerationResult configGenResult = generator.generateXML(xmlCfgPath);
			long endTime = System.currentTimeMillis();
			log.info("generateXmlConfig", "It took " + (endTime - startTime)
					+ "ms to generate a full XML config {" + xmlCfgPath + "}. HiveAP: " + hiveAp);
			return configGenResult;
		} catch (CreateXMLException | CLIGenerateException e) {
			throw new AhConfigGeneratedException("Failed to generate XML-formatted config.", e);
		} catch (OutOfMemoryError oome) {
			throw new AhConfigGeneratedException(
					"Out of memory occurred while generating XML-formatted config.", oome);
		}
	}

	public AhConfigGenerationResult generateXmlConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, boolean isView) throws AhConfigGeneratedException {
		return generateXmlConfig(hiveAp, configType, xmlCfgPath, hiveAp.getSoftVer(), isView);
	}

	public boolean parseCli(HiveAp hiveAp, ConfigType configType, String cliCfgPath,
			String xmlCfgPath, String cParseIgnorePath) throws FileNotFoundException, AhConfigParsedException {
		if (!new File(cliCfgPath).exists()) {
			log.error("parseCli", cliCfgPath + " doesn't exist. Giving up parsing CLI.");
			throw new FileNotFoundException(
					MgrUtil.getUserMessage("error.config.absentRunningConfig"));
		}

		String parseCliCmd = getParsingCliCmd(configType, cliCfgPath, xmlCfgPath, cParseIgnorePath, hiveAp);
		String[] cmdArray = new String[] { "bash", "-c", parseCliCmd };
		String cmd = cmdArray[0] + " " + cmdArray[1] + " " + cmdArray[2];
		Process process = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		boolean parsingResult = false;
		log.info("parseCli", "Parsing CLI with external command: " + cmd);
		Runtime runtime = Runtime.getRuntime();

		try {
			process = runtime.exec(cmdArray);
			process.waitFor();
//			int exitValue = 

//			if (exitValue != 0) {
//				log.error("parseCli", "Abnormal exit value " + exitValue
//						+ " returned from CLI parsing process.");
//				throw new AhConfigParsedException(
//						MgrUtil.getUserMessage("error.config.cli.parsing.failure"));
//			}

			is = process.getInputStream();
			isr = new InputStreamReader(is);
			in = new BufferedReader(isr);
			String returnValue = in.readLine();
			log.info("parseCli", "CLI parsing result: " + returnValue);
			parsingResult = returnValue != null && Integer.parseInt(returnValue.trim()) == 0;

			return parsingResult;
		} catch (Exception e) {
			if (e instanceof AhConfigParsedException) {
				throw (AhConfigParsedException) e;
			} else {
				throw new AhConfigParsedException(
						MgrUtil.getUserMessage("error.config.cli.parsing.failure"), e);
			}
		} finally {
			if (!parsingResult) {
				conserveConfig(cliCfgPath);
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("parseCli", "IO Close Error.", e);
				}
			}

			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					log.error("parseCli", "IO Close Error.", e);
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("parseCli", "IO Close Error.", e);
				}
			}

			if (process != null) {
				process.destroy();
			}
		}
	}

	public void reinforceRunningXmlConfig(HiveAp hiveAp, ConfigType configType,
			String runXmlCfgPath, String newXmlCfgPath, String cParseIgnoreCfgPath) throws AhConfigReinforcedException,
			CreateXMLException {
		CompleteRunXml reinforce = new CompleteRunXml(hiveAp);

		try {
			reinforce.fillRunXml(hiveAp, runXmlCfgPath, newXmlCfgPath, cParseIgnoreCfgPath);
			
			//XML post process
			CLIPostProcess cliPostProcess = new ParseCLIPostProcess(hiveAp, reinforce.getRunDocument(), cParseIgnoreCfgPath);
			cliPostProcess.init();
			cliPostProcess.process();
			cliPostProcess.writeResult(runXmlCfgPath);
		} catch (IOException ioe) {
			throw new AhConfigReinforcedException(
					"Failed to reinforce default value into running XML-formatted config.", ioe);
		} catch (DocumentException de) {
			throw new AhConfigReinforcedException("Invalid XML-formatted config.", de);
		}
	}

	public String compareConfigs(String oldXmlCfgPath, String newXmlCfgPath, HiveAp hiveAp)
			throws FileNotFoundException, AhConfigComparedException, AhConfigConvertedException {
		if (!new File(oldXmlCfgPath).exists()) {
			log.error("compareConfigs",
					"The old/running config doesn't exist. Giving up comparing.");
			throw new FileNotFoundException(MgrUtil.getUserMessage("error.config.absentOldConfig"));
		}

		if (!new File(newXmlCfgPath).exists()) {
			log.error("compareConfigs", "The new config doesn't exist. Giving up comparing.");
			throw new FileNotFoundException(MgrUtil.getUserMessage("error.config.absentNewConfig"));
		}

		try {
			// Compare old/running with new XML-formatted config.
			AhConfigComparator comparator = new AhConfigComparator(oldXmlCfgPath, newXmlCfgPath,
					hiveAp);
			boolean compareResult = comparator.compare();

			if (!compareResult) {
				throw new AhConfigComparedException("Comparing configs failure.");
			}

			// Generate the difference CLIs.
			long startTime = System.currentTimeMillis();
			String diffClis = comparator.generateDiffClis();
			long endTime = System.currentTimeMillis();
			log.info("compareConfigs", "It took " + (endTime - startTime)
					+ "ms to generate the difference CLIs. HiveAP: " + hiveAp);
			return diffClis;
		} catch (Exception e) {
			// Conserve configs mutually compared if comparison fails.
			conserveConfig(oldXmlCfgPath);
			conserveConfig(newXmlCfgPath);

			if (e instanceof AhConfigComparedException) {
				throw (AhConfigComparedException) e;
			} else if (e instanceof AhConfigConvertedException) {
				throw (AhConfigConvertedException) e;
			} else if (e instanceof DocumentException) {
				throw new AhConfigComparedException("Invalid XML-formatted config.", e);
			} else {
				throw new AhConfigComparedException("Unknown Error.", e);
			}
		}
	}

	public String convertXmlConfigIntoClis(String xmlCfgPath) throws AhConfigConvertedException {
		try {
			return GenerateXML.convertXmlIntoClis(xmlCfgPath);
		} catch (Exception e) {
			throw new AhConfigConvertedException(
					"Failed to convert XML-formatted config into CLIs.", e);
		}
	}

	public void generateBootstrapScript(String clis, String bootstrapCfgPath)
			throws AhConfigGeneratedException {
		try {
			GenerateBootstrap.generateBootstrapScript(clis, bootstrapCfgPath);
		} catch (IOException e) {
			throw new AhConfigGeneratedException("Failed to generate bootstrap script.", e);
		}
	}

	public void generateScript(String clis, String scriptPath) throws AhConfigGeneratedException {
		try {
			GenerateXML.generateScript(clis, scriptPath);
		} catch (IOException e) {
			throw new AhConfigGeneratedException("Failed to generate full config script.", e);
		}
	}

	public String generateBootstrapConfig(AhBootstrapGeneratedEvent event)
			throws AhConfigConvertedException, AhConfigGeneratedException {
		// Generate bootstrap XML-formatted config.
		generateBootstrapXmlConfig(event);

		HiveAp hiveAp = event.getHiveAp();
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String bootstrapXmlCfgPath = AhConfigUtil.getBootstrapXmlConfigPath(domainName, hiveApMac);

		return convertXmlConfigIntoClis(bootstrapXmlCfgPath);
	}

	// ***************************************************************
	// Config Generation
	// ***************************************************************

	public AhConfigGenerationResult generateConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, String softVer, boolean isView) throws AhConfigConvertedException,
			AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException {
		// Generate XML-formatted config.
		AhConfigGenerationResult configGenResult = generateXmlConfig(hiveAp, configType,
				xmlCfgPath, softVer, isView);

		// Convert XML-formatted into CLIs.
		String genClis = convertXmlConfigIntoClis(xmlCfgPath);
		
		//CLI from supplement CLIs that cannot parse, append to full config last;
		String ignoreClis = AbstractCLIPostProcess.getIgnoreCLIContent(hiveAp);
		if(!StringUtils.isEmpty(ignoreClis)){
			if(!genClis.endsWith("\n")){
				genClis += "\n";
			}
			genClis += ignoreClis + "\n";
		}
		
//		switch (configType) {
//		case AP_FULL:
//			// Append new config version in the end.
//			int newConfigVer = hiveAp.getNewConfigVerNum();
//			String configVerCli = AhCliFactory.configVerNum(newConfigVer);
//			genClis += configVerCli;
//			break;
//		case USER_FULL:
//			// Ahead of all user removal CLI for the HiveAPs with software
//			// version lower than 3.3r1.
//			if (NmsUtil.compareSoftwareVersion("3.3.1.0", softVer) > 0) {
//				String cachedOnlyConfig = HmCliSpecialHandling.generateCachedOnlyConfig(hiveAp, false);
//				genClis = AhCliFactory.removeAllLocalUsers() + genClis + cachedOnlyConfig;
//			}
//			break;
//		default:
//			log.warn("generateConfig", "Unexpected Config Type: " + configType.toString());
//			break;
//		}

		String finalClis = cliFinalize(genClis, null, configType, hiveAp, softVer);
		configGenResult.setGenClis(finalClis);

		return configGenResult;
	}
	
	public String cliFinalize(String clis, String runningCfg, ConfigType configType, HiveAp hiveAp, String softVer) throws AhConfigRetrievedException, AhConfigDisconnectException {
		String recoveredClis = HmCliSpecialHandling.recoverClis(clis, runningCfg, configType, hiveAp, softVer);

		return HmCliSpecialHandling.reorganizeIpRelatedClis(recoveredClis);
	}

	public AhConfigGenerationResult generateConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, boolean isView) throws AhConfigConvertedException, AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException {
		return generateConfig(hiveAp, configType, xmlCfgPath, hiveAp.getSoftVer(), isView);
	}

	

	public String viewConfig(HiveAp hiveAp, ConfigType configType) throws Exception {
		//set download info for view config.
		DownloadInfo dwnInfo = UpdateManager.createDownloadInfo(hiveAp, true);
		hiveAp.setDownloadInfoView(dwnInfo);
		
		String clis;
		long startTime = System.currentTimeMillis();

		switch (configType) {
		case AP_FULL:
		case USER_FULL:
			clis = viewFullConfig(hiveAp, configType);
			break;
		case AP_DELTA:
		case USER_DELTA:
			clis = viewDeltaConfig(hiveAp, configType);
			break;
		case AP_AUDIT:
		case USER_AUDIT:
			clis = viewAuditConfig(hiveAp, configType);
			break;
		default:
			throw new AssertionError("Unexpected Config Type: " + configType.toString());
		}

		long endTime = System.currentTimeMillis();
		log.info("viewConfig", "It took " + (endTime - startTime) + "ms to view a "
				+ configType.toString().toLowerCase() + " config. HiveAP: " + hiveAp);
		return clis;
	}

	private String viewFullConfig(HiveAp hiveAp, ConfigType configType)
			throws AhConfigConvertedException, AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException {
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String viewBasedNewXmlCfgPath;

		switch (configType) {
		case AP_FULL:
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedFullNewXmlConfigPath(domainName,
					hiveApMac);
			break;
		case USER_FULL:
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedUserNewXmlConfigPath(domainName,
					hiveApMac);
			break;
		default:
			throw new AssertionError("Unexpected Config Type: " + configType.toString());
		}

		AhConfigGenerationResult configGenResult = generateConfig(hiveAp, configType,
				viewBasedNewXmlCfgPath, true);
		String clis = configGenResult.getGenClis();

		return HmCliSpecialHandling.hideSensitiveMessages(clis);
	}

	/**
	 * <p>
	 * View delta configurations from the comparison between previous and new
	 * configurations.
	 * </p>
	 * 
	 * @param hiveAp
	 *            which is used to generate the specific config.
	 * @param configType
	 *            the type of config to be generated.
	 * @throws FileNotFoundException
	 *             if the generated XML-formatted config is absent.
	 * @throws AhConfigComparedException
	 *             if any problem occurs in XML-formatted config comparing.
	 * @throws AhConfigConvertedException
	 *             if any problem occurs while converting XML-formatted config
	 *             into CLIs.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in XML-formatted config generating.
	 * @return The difference CLIs come from comparison between old and new
	 *         XML-formatted configs.
	 * @throws AhConfigRetrievedException 
	 * @throws AhConfigDisconnectException 
	 */
	private String viewDeltaConfig(HiveAp hiveAp, ConfigType configType)
			throws FileNotFoundException, AhConfigComparedException, AhConfigConvertedException,
			AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException {
		boolean oldXmlCfgExists;
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String oldXmlCfgPath;
		String viewBasedNewXmlCfgPath;
		ConfigType fullCfgType;

		switch (configType) {
		case AP_DELTA:
			oldXmlCfgExists = AhConfigUtil.fullOldXmlConfigExists(domainName, hiveApMac);
			oldXmlCfgPath = AhConfigUtil.getFullOldXmlConfigPath(domainName, hiveApMac);
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedFullNewXmlConfigPath(domainName,
					hiveApMac);
			fullCfgType = ConfigType.AP_FULL;
			break;
		case USER_DELTA:
			oldXmlCfgExists = AhConfigUtil.userOldXmlConfigExists(domainName, hiveApMac);
			oldXmlCfgPath = AhConfigUtil.getUserOldXmlConfigPath(domainName, hiveApMac);
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedUserNewXmlConfigPath(domainName,
					hiveApMac);
			fullCfgType = ConfigType.USER_FULL;
			break;
		default:
			throw new AssertionError("Unexpected Config Type: " + configType.toString());
		}

		String clis;

		if (oldXmlCfgExists) {
			// Generate view-based new XML-formatted config.
			generateXmlConfig(hiveAp, fullCfgType, viewBasedNewXmlCfgPath, true);

			// Compare configs.
			clis = compareConfigs(oldXmlCfgPath, viewBasedNewXmlCfgPath, hiveAp);

			if (!clis.isEmpty()) {
				clis = cliFinalize(clis, null, configType, hiveAp, null);

				// Hide sensitive messages, e.g. password.
				clis = HmCliSpecialHandling.hideSensitiveMessages(clis);
			}
		} else {
			clis = viewFullConfig(hiveAp, fullCfgType);
		}

		return clis;
	}

	/**
	 * <p>
	 * View delta configurations from the comparison between running and new
	 * configurations.
	 * </p>
	 * 
	 * @param hiveAp
	 *            the specific HiveAP to be operated to.
	 * @param configType
	 *            the type of config to be generated.
	 * @throws FileNotFoundException
	 *             if the generated XML-formatted config is absent.
	 * @throws AhConfigComparedException
	 *             if any problem occurs in comparing XML-formatted config.
	 * @throws AhConfigConvertedException
	 *             if any problem occurs while converting XML-formatted config
	 *             into CLIs.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in generating XML-formatted config.
	 * @throws AhConfigParsedException
	 *             if any problem occurs in converting XML-formatted config.
	 * @throws AhConfigReinforcedException
	 *             if any problem occurs in reinforcing XML-formatted config.
	 * @throws AhConfigRetrievedException
	 *             if any problem occurs while fetching the user config from the
	 *             specified HiveAP.
	 * @throws CreateXMLException
	 *             if any problem occurs in generating XML-formatted config.
	 * @return The difference CLIs come from comparison between old and new
	 *         XML-formatted configs.
	 * @throws AhConfigDisconnectException 
	 */
	private String viewAuditConfig(final HiveAp hiveAp, ConfigType configType)
			throws FileNotFoundException, AhConfigComparedException, AhConfigConvertedException,
			AhConfigGeneratedException, AhConfigParsedException, AhConfigReinforcedException,
			AhConfigRetrievedException, CreateXMLException, AhConfigDisconnectException {
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String viewBasedRunXmlCfgPath;
		String viewBasedNewXmlCfgPath;
		String fetchedConfig = null;
		String fetchedCfgPath;
		String fetchedCfgType;
		String cParseIgnoreCfgPath = null;
		ConfigType fullCfgType;

		switch (configType) {
		case AP_AUDIT:
			fetchedConfig = fetchRunningConfig(hiveAp);
			fetchedCfgPath = AhConfigUtil.getFullRunConfigPath(domainName, hiveApMac);
			fetchedCfgType = "running";
			viewBasedRunXmlCfgPath = AhConfigUtil.getViewBasedFullRunXmlConfigPath(domainName,
					hiveApMac);
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedFullNewXmlConfigPath(domainName,
					hiveApMac);
			cParseIgnoreCfgPath = AhConfigUtil.getViewFullCParseIgnoreConfigPath(domainName, hiveApMac);
			fullCfgType = ConfigType.AP_FULL;
			break;
		case USER_AUDIT:
			fetchedConfig = fetchUserConfig(hiveAp);
			fetchedCfgPath = AhConfigUtil.getUserRunConfigPath(domainName, hiveApMac);
			fetchedCfgType = "user";
			viewBasedRunXmlCfgPath = AhConfigUtil.getViewBasedUserRunXmlConfigPath(domainName,
					hiveApMac);
			viewBasedNewXmlCfgPath = AhConfigUtil.getViewBasedUserNewXmlConfigPath(domainName,
					hiveApMac);
			cParseIgnoreCfgPath = AhConfigUtil.getViewUserCParseIgnoreConfigPath(domainName, hiveApMac);
			fullCfgType = ConfigType.USER_FULL;
			break;
		default:
			throw new AssertionError("Unexpected Config Type: " + configType.toString());
		}

		try {
			// Keep config fetched from HiveAP into a file.
			log.debug("viewAuditConfig", "Keeping " + fetchedCfgType + " config into "
					+ fetchedCfgPath);
			FileManager.getInstance().createFile(fetchedConfig, fetchedCfgPath);
			log.debug("viewAuditConfig", "Successfully kept " + fetchedCfgType + " config into "
					+ fetchedCfgPath);
		} catch (IOException ioe) {
			log.error("viewAuditConfig", "Failed to keep " + fetchedCfgType + " config into "
					+ fetchedCfgPath);
			throw new AhConfigGeneratedException(
					"Keeping config fetched from "+NmsUtil.getOEMCustomer().getAccessPonitName()+" into a file failure.");
		}

		// Parse CLI into XML-formatted config.
//		boolean parseRet = parseCli(hiveAp, fullCfgType, fetchedCfgPath, viewBasedRunXmlCfgPath, cParseIgnoreCfgPath);
		parseCli(hiveAp, fullCfgType, fetchedCfgPath, viewBasedRunXmlCfgPath, cParseIgnoreCfgPath);

		//no need check parse result, current design will return all CLIs that cannot parse.
//		if (!parseRet) {
//			throw new AhConfigParsedException("Parsing CLI failure.");
//		}

		// Generate view-based new XML-formatted config.
		generateXmlConfig(hiveAp, fullCfgType, viewBasedNewXmlCfgPath, true);

		// Config reinforce: the XML-formatted config converted from running
		// config needs reprocessing upon the newly generated HM XML-formatted
		// config.
		// if (ConfigType.AP_AUDIT.equals(configType)) {
		reinforceRunningXmlConfig(hiveAp, fullCfgType, viewBasedRunXmlCfgPath,
				viewBasedNewXmlCfgPath, cParseIgnoreCfgPath);
		// }

		// Do comparison between the converted and generated XML-formatted
		// configs.
		String clis = compareConfigs(viewBasedRunXmlCfgPath, viewBasedNewXmlCfgPath, hiveAp);

		if (!clis.isEmpty()) {
			clis = cliFinalize(clis, fetchedConfig, configType, hiveAp, null);

			// Hide sensitive messages, e.g. password.
			clis = HmCliSpecialHandling.hideSensitiveMessages(clis);
		}

		return clis;
	}

	private String getParsingCliCmd(ConfigType configType, String cliCfgPath, String xmlCfgPath, String cParseIgnorePath,
			HiveAp hiveAp) {
		String parsingType;

		switch (configType) {
		case USER_FULL:
			parsingType = "user";
			break;
		case AP_FULL:
		default:
			parsingType = "full";
			break;
		}

		String cliParserPath = AhDirTools.getCliParserDir();
		String hiveApVer = hiveAp.getSoftVer();
		String parseCmd = null;
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			parseCmd = "parse_cli_sr";
		}else if(hiveAp.isMillauImage()){
			parseCmd = "parse_cli_11ac";
		}else{
			parseCmd = "parse_cli";
		}

		if (!cliParserPath.endsWith(File.separator)) {
			cliParserPath += File.separator;
		}

		String cliParserLogPath = AhDirTools.getCliParserLogDir();

		if (!cliParserLogPath.endsWith(File.separator)) {
			cliParserLogPath += File.separator;
		}

		cliParserLogPath += "cli_parser.log";

		return cliParserPath + parseCmd + " -t " + parsingType + " -i " + cliCfgPath + " -o "
				+ xmlCfgPath + " -v " + hiveApVer + " -d " + cliParserLogPath 
				+ " -e "  + cParseIgnorePath;
	}

	/**
	 * <p>
	 * Conserve the config (with either plain text or XML formatted) which
	 * results in the failure during CLI parsing or config comparing. The config
	 * will be conserved into a certain place where it is able to be packed
	 * into a HM tech support data file, user may obtain this packed file from
	 * HM GUI so that technical engineers can parse causes for some errors
	 * through it.
	 * </p>
	 * 
	 * @param cfgPath
	 *            the path of config to be conserved.
	 */
	private void conserveConfig(String cfgPath) {
		String cliParserLogDir = AhDirTools.getCliParserLogDir();
		String configName = new File(cfgPath).getName();
		String reservedConfig = cliParserLogDir + configName;

		try {
			log.info("conserveConfig", "Conserving config: " + configName);
			FileManager.getInstance().copyFile(cfgPath, reservedConfig);
			log.info("conserveConfig", "Config " + configName + " was conserved.");
		} catch (Exception e) {
			log.error("conserveConfig", "Failed to conserve config " + configName, e);
		}
	}

	

	/*-
	private String mergeUserIndexRange(String newClis) {
		String rest = "\n"+newClis+"\n";
		String regex = "\nuser-group (.+) auto-generation index-range (.+)";
		String rangeStr= null;
		boolean[] rmUsers = new boolean[PskAutoUserGroupImpl.AUTO_USER_COUNT], newUsers = new boolean[PskAutoUserGroupImpl.AUTO_USER_COUNT];
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(rest);
		
		while (matcher.find()) {
			rangeStr = matcher.group(2);

			if (rangeStr.contains(" ")) {
				int start, end;
				int index = rangeStr.indexOf(" ");
				start = Integer.valueOf(rangeStr.substring(0, index).trim());
				end = Integer.valueOf(rangeStr.substring(index+1).trim());

				for (int i=start; i<end+1; i++) {
					newUsers[i] = true;
				}
			} else {
				newUsers[Integer.valueOf(rangeStr.trim())] = true;
			}
		}
		
		regex = "\nno user-group (.+) auto-generation index-range (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(rest);

		while (matcher.find()) {
			rangeStr = matcher.group(1);

			if ( rangeStr.contains(" ")) {
				int start, end;
				int index = rangeStr.indexOf(" ");
				start = Integer.valueOf(rangeStr.substring(0, index).trim());
				end = Integer.valueOf(rangeStr.substring(index+1).trim());
				for(int i=start; i<end+1; i++) {
					rmUsers[i] = true;
				}
			} else {
				rmUsers[Integer.valueOf(rangeStr.trim())] = true;
			}
		}
		
		for (int i=0; i<rmUsers.length; i++) {
			if (rmUsers[i] && newUsers[i]) {
				rmUsers[i] = false;
				newUsers[i] = false;
			}
		}
		
		List<String> rmUserList, newUserList;
		rmUserList = PskAutoUserGroupImpl.generateRange(rmUsers);
		newUserList = PskAutoUserGroupImpl.generateRange(newUsers);

		if (rmUserList != null) {
			for (String rmCLI : rmUserList) {
				rest +=rest.endsWith("\n")? "no user-group (.+) auto-generation index-range "+rmCLI : "\nno user-group (.+) auto-generation index-range "+rmCLI;
			}
		}

		if (newUserList != null) {
			for(String newCLI : newUserList) {
				rest +=rest.endsWith("\n")? "user-group (.+) auto-generation index-range "+newCLI : "\nuser-group (.+) auto-generation index-range "+newCLI;
			}
		}
		
		return rest;
	}*/
	
	
	
	
	

	/*public static void main(String[] args){
//		String cli = "hive zhang password test11111\n" +
//				"no hive zhang\n" +
//				"no hive test security wlan dos hive-level frame-type all\n" +
//				"interface wifi0 hive testzzz shutdown\n"+
//				"location aerohive enable\n" +
//				"interface mgt0 dhcp-server options hivemanager dfsa\n"+
//				"interface mgt0 dhcp client option custom hivemanager 11 ip\n"+
//				"ssid ssid_test";
//		String[][] newSTR = {
//				{"hive", "cluster"},
//				{"aerohive", "Black-Box"},
//				{"hivemanager", "SmartPath-EMS"}
//		};
//		System.out.print(getOEMClis(cli, newSTR));
		String[] clis = {
				//"no hive zhang\n",
				//"no hive test security wlan dos hive-level frame-type all\n",
				"no interface mgt0 hive\n",
				//"location aerohive enable\n",
				"interface mgt0.1 dhcp-server options hivemanager HM200\n",
				"interface mgt0.1 dhcp-server options hivemanager 192.168.20.30\n",
				"interface mgt0.1 dhcp client option custom hivemanager 11 ip\n",
				//"ssid ssid_test\n",
				//"hiveui enable",
				//"hiveui cas client server name 1111",
				//"hiveui cas client server name 22"
		};

		String cliStr = "";
		for (String cli : clis) {
			cliStr += cli;
			if (!cliStr.endsWith("\n")) {
				cliStr += "\n";
			}
		}
		if(cliStr != null && !"".equals(cliStr)){
			String[][] newSTR = {
					{"hive", "cluster"},
					{"aerohive", "Black-Box"},
					{"hivemanager", "SmartPath-EMS"},
					{"hiveui", "clusterui"}
			};
			cliStr = BeConfigModuleImpl.getOEMClis(cliStr, newSTR);
			String[] resClis = cliStr.split("\n");
			for(int j=0; j<resClis.length; j++){
				if(!resClis[j].endsWith("\n")){
					resClis[j] += "\n";
				}
			}
			for (String resCli : resClis) {
				System.out.print(resCli);
			}
		}
	}*/

	@Override
	public UpdateManager getUpdateManager() {
		return updateManager;
	}

	@Override
	public UpdateResponseListener getUpdateResponseListener() {
		return updateResponseListener;
	}

	@Override
	public ImageDistributor getImageDistributor() {
		return imageDistributor;
	}

	@Override
	public UpdateObjectBuilder getUpdateObjectBuilder() {
		return updateObjectBuilder;
	}
	
	@Override
	public ProvisionProcessor getProvisionProcessor(){
		return this.provisionProcessor;
	}
	
	@Override
	public ImageManager getImageSynupLS(){
		return this.ImageSynupLS;
	}

}
