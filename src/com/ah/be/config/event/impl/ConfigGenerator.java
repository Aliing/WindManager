package com.ah.be.config.event.impl;

import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.AUDIT_EXPIRED;
import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.DIFF_FAIL;
import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.DISCONNECT;
import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.FAIL;
import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.NO_DIFF;
import static com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType.SUCC;
import static com.ah.be.config.event.AhConfigGenerationProgressEvent.ConfigGenerationProgress.COMPARE_CONFIGS;
import static com.ah.be.config.event.AhConfigGenerationProgressEvent.ConfigGenerationProgress.FETCH_HIVEAP_CONFIG;
import static com.ah.be.config.event.AhConfigGenerationProgressEvent.ConfigGenerationProgress.GENERATE_HM_CONFIG;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.AhAppContainer;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.result.HmCloudAuthCertResult;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.config.AhConfigDisconnectException;
import com.ah.be.config.BeConfigModule;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.event.AhCloudAuthCAGenerateEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent;
import com.ah.be.config.event.AhConfigGenerationProgressEvent;
import com.ah.be.config.event.AhConfigGenerationProgressEvent.ConfigGenerationProgress;
import com.ah.be.config.event.AhConfigUpdatedEvent;
import com.ah.be.config.event.AhDeltaConfigGeneratedEvent;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.db.configuration.ConfigAuditProcessor;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.os.FileManager;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

class ConfigGenerator extends Thread implements QueryBo {

	private static final Tracer log = new Tracer(
			ConfigGenerator.class.getSimpleName());

	private final AhConfigEventMgmtImpl configEventMgmt;

	public ConfigGenerator(AhConfigEventMgmtImpl configEventMgmt) {
		this.configEventMgmt = configEventMgmt;
	}

	@Override
	public void run() {
		for (;;) {
			try {
				synchronized (configEventMgmt) {
					// This is used to protect any events from being taken
					// during number adjustment for config generators.
				}

				BeBaseEvent event = configEventMgmt.eventQueue.take();

				// If needs to close the current generator.
				boolean closing = false;

				// Running requests + 1
				configEventMgmt.perfStatUtil.increaseNumberOfConfigRunning();

				int eventType = event.getEventType();

				switch (eventType) {
				case BeEventConst.AH_BOOTSTRAP_GENERATED_EVENT:
				case BeEventConst.AH_DELTA_CONFIG_GENERATED_EVENT:
				case BeEventConst.AH_CONFIG_GENERATED_EVENT:
					generateConfig((AhConfigGeneratedEvent) event);
					break;
				case BeEventConst.AH_CONFIG_UPDATED_EVENT:
					handleConfigUpdateEvent((AhConfigUpdatedEvent) event);
					break;
				case BeEventConst.AH_CLOUD_AUTH_UPDATE_EVENT:
					generateCloudCA((AhCloudAuthCAGenerateEvent)event);
					break;
				case BeEventConst.AH_SHUTDOWN_EVENT:
					log.info("run", "Closed a config generator - " + getName());
					closing = true;
					break;
				default:
					log.warn("run", "Unknown event. Event Type: " + eventType);
					break;
				}

				// Both running and total requests - 1.
				configEventMgmt.perfStatUtil
						.decreaseNumberOfConfigRequestWithRunning();

				if (closing) {
					break;
				}
			} catch (Exception e) {
				log.error("run",
						"Exception occurred while generating a config.", e);
			} catch (Error e) {
				log.error("run", "Error occurred while generating a config.", e);
			}
		}

		// Remove the closed generator from the generator holder.
		configEventMgmt.removeGenerator(this);
	}

	private void generateConfig(AhConfigGeneratedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		//softver transfers through HiveAp object that in event.
//		hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId(), this);
		event.setHiveAp(hiveAp);
		ConfigType configType = event.getConfigType();
		log.info("generateConfig",
				"Received a config generation event. Config Type: "
						+ configType + "; Device: " + hiveAp);
		long startTime = System.nanoTime();

		switch (configType) {
		case BOOTSTRAP:
			generateBootstrapConfig((AhBootstrapGeneratedEvent) event);
			break;
		case AP_FULL:
		case USER_FULL:
			generateFullConfig(configType, event);
			break;
		case AP_DELTA:
		case USER_DELTA:
			generateDeltaConfig(configType, (AhDeltaConfigGeneratedEvent) event);
			break;
		case AP_AUDIT:
		case USER_AUDIT:
			generateAuditConfig(configType, (AhDeltaConfigGeneratedEvent) event);
			break;
		default:
			log.warn("generateConfig",
					"Unexpected Config Type: " + configType.toString());
			break;
		}

		long endTime = System.nanoTime();
		long timeCostNanos = endTime - startTime;
		long timeCostSeconds = TimeUnit.NANOSECONDS.toSeconds(timeCostNanos);
		log.info("generateConfig", "Time Elapsed(s): " + timeCostSeconds
				+ "; Config Type: " + configType + "; Device: " + hiveAp);

		configEventMgmt.notify(event);
	}

	@SuppressWarnings("static-access")
	private void generateBootstrapConfig(AhBootstrapGeneratedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		hiveAp = MgrUtil.getQueryEntity().findBoById(HiveAp.class, hiveAp.getId(),this);
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		BeConfigModule configModule = AhAppContainer.getBeConfigModule();

		try {
			// Generate bootstrap config.
			String clis = configModule.generateBootstrapConfig(event);
			String bootstrapCfgPath = AhConfigUtil.getBootstrapConfigPath(
					domainName, hiveApMac);

			clis = configModule.cliFinalize(clis, null, ConfigType.BOOTSTRAP, hiveAp,
					null);

			// Generate bootstrap script.
			configModule.generateBootstrapScript(clis, bootstrapCfgPath);
			log.info("generateBootstrapConfig",
					"Successfully generated a bootstrap config. Device: "
							+ hiveAp);
			event.setConfigGenResultType(SUCC);
		} catch (Exception e) {
			log.error("generateBootstrapConfig",
					"Failed to generate a bootstrap config. Device: " + hiveAp,
					e);
			event.setConfigGenResultType(FAIL);
			String errorMsg = ConfigAuditProcessor.getDetailInfo(e,
					ConfigType.BOOTSTRAP, hiveAp.getLastCfgTime() == 0);
			event.setErrorMsg(errorMsg);
		}
	}

	private void generateFullConfig(ConfigType configType,
			AhConfigGeneratedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String softVer = hiveAp.getSoftVer();
		String configTypeName;
		String newXmlCfgPath;
		String cliScriptPath;

		switch (configType) {
		case AP_FULL:
			configTypeName = "AP full";
			newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(domainName,
					hiveApMac);
			cliScriptPath = AhConfigUtil.getFullNewConfigPath(domainName,
					hiveApMac);
			break;
		case USER_FULL:
			configTypeName = "user full";
			newXmlCfgPath = AhConfigUtil.getUserNewXmlConfigPath(domainName,
					hiveApMac);
			cliScriptPath = AhConfigUtil.getUserNewConfigPath(domainName,
					hiveApMac);
			break;
		default:
			log.warn("generateFullConfig", "Unexpected Config Type: "
					+ configType.toString());
			return;
		}

		BeConfigModule configModule = AhAppContainer.getBeConfigModule();
		log.info("generateFullConfig", "Generating a " + configTypeName
				+ " config. Device: " + hiveAp);

		// Notify progress - GENERATE_HM_CONFIG.
		configEventMgmt.notify(buildConfigGenProgEvent(event,
				GENERATE_HM_CONFIG));

		try {
			// Generate config by config type specified.
			AhConfigGenerationResult configGenResult = configModule
					.generateConfig(hiveAp, configType, newXmlCfgPath, softVer, false);
			String clis = configGenResult.getGenClis();

			// Empty file for user-config is disallowed, so HM has to give out
			// such a user-config file only containing a space character to deal
			// with this situation.
			if ("".equals(clis) && ConfigType.USER_FULL.equals(configType)) {
				clis = " ";
			}

			configModule.generateScript(clis, cliScriptPath);
			log.info("generateFullConfig", "Successfully generated a "
					+ configTypeName + " config. Device: " + hiveAp);
			event.setConfigGenResult(configGenResult);
			event.setConfigGenResultType(SUCC);
		} catch (Exception e) {
			log.error("generateFullConfig", "Failed to generate a "
					+ configTypeName + " config. Device: " + hiveAp, e);
			event.setConfigGenResultType(FAIL);
			String errorMsg = ConfigAuditProcessor.getDetailInfo(e, configType,
					hiveAp.getLastCfgTime() == 0);
			event.setErrorMsg(errorMsg);
		}
	}

	private void generateDeltaConfig(ConfigType configType,
			AhDeltaConfigGeneratedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		String domainName = hiveAp.getOwner().getDomainName();
		String hiveApMac = hiveAp.getMacAddress();
		String oldXmlCfgPath;
		String newXmlCfgPath;
		String configTypeName;
		boolean isOldXmlCfgExisting;
		ConfigType fullCfgType;

		switch (configType) {
		case AP_DELTA:
			isOldXmlCfgExisting = AhConfigUtil.fullOldXmlConfigExists(
					domainName, hiveApMac);
			oldXmlCfgPath = AhConfigUtil.getFullOldXmlConfigPath(domainName,
					hiveApMac);
			newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(domainName,
					hiveApMac);
			configTypeName = "AP delta";
			fullCfgType = ConfigType.AP_FULL;
			break;
		case USER_DELTA:
			isOldXmlCfgExisting = AhConfigUtil.userOldXmlConfigExists(
					domainName, hiveApMac);
			oldXmlCfgPath = AhConfigUtil.getUserOldXmlConfigPath(domainName,
					hiveApMac);
			newXmlCfgPath = AhConfigUtil.getUserNewXmlConfigPath(domainName,
					hiveApMac);
			configTypeName = "user delta";
			fullCfgType = ConfigType.USER_FULL;
			break;
		default:
			log.warn("generateDeltaConfig", "Unexpected Config Type: "
					+ configType.toString());
			return;
		}

		BeConfigModule configModule = AhAppContainer.getBeConfigModule();

		// Notify progress - GENERATE_HM_CONFIG.
		configEventMgmt.notify(buildConfigGenProgEvent(event,
				GENERATE_HM_CONFIG));

		try {
			// Generate new HM XML-formatted config.
			AhConfigGenerationResult configGenResult = configModule
					.generateXmlConfig(hiveAp, fullCfgType, newXmlCfgPath, false);
			String clis;

			if (isOldXmlCfgExisting) {
				// Notify progress - COMPARE_CONFIGS.
				configEventMgmt.notify(buildConfigGenProgEvent(event,
						COMPARE_CONFIGS));

				// Do comparison between previous and new HM XML-formatted
				// configs.
				clis = configModule.compareConfigs(oldXmlCfgPath,
						newXmlCfgPath, hiveAp);
			} else {
				// Regard the newly generated config as delta config if the
				// previous config for comparison doesn't exist.
				clis = configModule.convertXmlConfigIntoClis(newXmlCfgPath);
			}

			clis = configModule.cliFinalize(clis, null, configType, hiveAp, null);
			configGenResult.setGenClis(clis);
			event.setConfigGenResult(configGenResult);
			if (!clis.isEmpty()) {
				log.info("generateDeltaConfig", "Successfully generated a "
						+ configTypeName + " config. Device: " + hiveAp);
				event.setConfigGenResultType(SUCC);
			} else {
				event.setConfigGenResultType(NO_DIFF);
			}
		} catch (Exception e) {
			log.error("generateDeltaConfig", "Failed to generate a "
					+ configTypeName + " config. Device: " + hiveAp, e);
			event.setConfigGenResultType(DIFF_FAIL);
			String errorMsg = ConfigAuditProcessor.getDetailInfo(e, configType,
					hiveAp.getLastCfgTime() == 0);
			event.setErrorMsg(errorMsg);
		}
	}

	private void generateAuditConfig(ConfigType configType,
			AhDeltaConfigGeneratedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		String hiveApMac = hiveAp.getMacAddress();

		if (event.isExecutionTimeExpired()) {
			log.warn(
					"generateAuditConfig",
					"The execution time was beyond the effective time for scheduled audit config for device "
							+ hiveAp + ". Giving up generating audit config!");
			event.setConfigGenResultType(AUDIT_EXPIRED);
			event.setErrorMsg(MgrUtil
					.getUserMessage("error.config.audit.exceedSchedulerEndTime"));
			return;
		}

		String domainName = hiveAp.getOwner().getDomainName();
		String runXmlCfgPath;
		String newXmlCfgPath;
		String rtvCfg = null;
		String rtvCfgStorePath;
		String configTypeName = "";
		String cParseIgnoreCfgPath = null;
		ConfigType fullCfgType;
		BeConfigModule configModule = AhAppContainer.getBeConfigModule();

		// Notify progress - FETCH_HIVEAP_CONFIG.
		configEventMgmt.notify(buildConfigGenProgEvent(event,
				FETCH_HIVEAP_CONFIG));

		try {
			switch (configType) {
			case AP_AUDIT:
				configTypeName = "AP audit";

				// Fetch running config.
				rtvCfg = configModule.fetchRunningConfig(hiveAp);
				rtvCfgStorePath = AhConfigUtil.getFullRunConfigPath(domainName,
						hiveApMac);
				runXmlCfgPath = AhConfigUtil.getFullRunXmlConfigPath(
						domainName, hiveApMac);
				newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(
						domainName, hiveApMac);
				cParseIgnoreCfgPath = AhConfigUtil.getFullCParseIgnoreConfigPath(domainName, hiveApMac);
				fullCfgType = ConfigType.AP_FULL;
				break;
			case USER_AUDIT:
				configTypeName = "user audit";

				// Fetch user config.
				rtvCfg = configModule.fetchUserConfig(hiveAp);
				rtvCfgStorePath = AhConfigUtil.getUserRunConfigPath(domainName,
						hiveApMac);
				runXmlCfgPath = AhConfigUtil.getUserRunXmlConfigPath(
						domainName, hiveApMac);
				newXmlCfgPath = AhConfigUtil.getUserNewXmlConfigPath(
						domainName, hiveApMac);
				cParseIgnoreCfgPath = AhConfigUtil.getUserCParseIgnoreConfigPath(domainName, hiveApMac);
				fullCfgType = ConfigType.USER_FULL;
				break;
			default:
				log.warn("generateAuditConfig", "Unexpected Config Type: "
						+ configType.toString());
				return;
			}

			// Keep config fetched into a file.
			FileManager.getInstance().createFile(rtvCfg, rtvCfgStorePath);

			// Parse CLI into XML-formatted config.
//			boolean parseRet = configModule.parseCli(hiveAp, fullCfgType,
//					rtvCfgStorePath, runXmlCfgPath, cParseIgnoreCfgPath);
			 configModule.parseCli(hiveAp, fullCfgType, rtvCfgStorePath, runXmlCfgPath, cParseIgnoreCfgPath);

			//no need check parse result, current design will return all CLIs that cannot parse.
//			if (!parseRet) {
//				event.setConfigGenResultType(DIFF_FAIL);
//				event.setErrorMsg(MgrUtil
//						.getUserMessage("error.config.cli.parsing.failure"));
//				return;
//			}

			// Notify progress - GENERATE_HM_CONFIG.
			configEventMgmt.notify(buildConfigGenProgEvent(event,
					GENERATE_HM_CONFIG));

			// Generate HM XML-formatted config.
			AhConfigGenerationResult configGenResult = configModule
					.generateXmlConfig(hiveAp, fullCfgType, newXmlCfgPath, false);

			// Config reinforce: the XML-formatted config converted from running
			// config needs reprocessing upon the newly generated HM
			// XML-formatted config.
			// if (ConfigType.AP_AUDIT.equals(configType)) {
			configModule.reinforceRunningXmlConfig(hiveAp, fullCfgType,
					runXmlCfgPath, newXmlCfgPath, cParseIgnoreCfgPath);
			// }

			// Notify progress - COMPARE_CONFIGS.
			configEventMgmt.notify(buildConfigGenProgEvent(event,
					COMPARE_CONFIGS));

			// Do comparison between the converted and generated XML-formatted
			// configs.
			String clis = configModule.compareConfigs(runXmlCfgPath,
					newXmlCfgPath, hiveAp);

			clis = configModule.cliFinalize(clis, rtvCfg, configType, hiveAp, null);
			configGenResult.setGenClis(clis);
			event.setConfigGenResult(configGenResult);
			if (!clis.isEmpty()) {
				log.info("generateAuditConfig", "Successfully generated a "
						+ configTypeName + " config. Device: " + hiveAp);
				event.setConfigGenResultType(SUCC);
			} else {
				event.setConfigGenResultType(NO_DIFF);
			}
		} catch (Exception e) {
			log.error("generateAuditConfig", "Failed to generate a "
					+ configTypeName + " config. Device: " + hiveAp, e);
			if(e instanceof AhConfigDisconnectException){
				event.setConfigGenResultType(DISCONNECT);
			}else{
				event.setConfigGenResultType(DIFF_FAIL);
			}
			String errorMsg = ConfigAuditProcessor.getDetailInfo(e, configType,
					hiveAp.getLastCfgTime() == 0);
			event.setErrorMsg(errorMsg);
		} catch (AssertionError ae) {
			log.error("generateAuditConfig", "Failed to generate a "
					+ configTypeName + " config. Device: " + hiveAp, ae);
			event.setConfigGenResultType(DIFF_FAIL);
			event.setErrorMsg(ae.getMessage());
		}
	}

	private AhConfigGenerationProgressEvent buildConfigGenProgEvent(
			AhConfigGeneratedEvent event,
			ConfigGenerationProgress configGenProgress) {
		AhConfigGenerationProgressEvent configGenProgEvent = new AhConfigGenerationProgressEvent();
		configGenProgEvent.setHiveAp(event.getHiveAp());
		configGenProgEvent.setSeqNum(event.getSeqNum());
		configGenProgEvent.setUseMode(event.getUseMode());
		configGenProgEvent.setConfigGenProgress(configGenProgress);

		return configGenProgEvent;
	}

	private void handleConfigUpdateEvent(AhConfigUpdatedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		ConfigType configType = event.getConfigType();
		byte updateResult = event.getUpdateResult();
		log.info("handleConfigUpdateEvent",
				"Received a config update event. Device: " + hiveAp
						+ "; Config Type: " + configType.toString()
						+ "; Update Result: " + updateResult);

		if (updateResult == BeCommunicationConstant.CLIRESULT_SUCCESS) {
			replaceConfig(hiveAp, configType);
		}
	}

	/**
	 * Replace old XML-formatted config with newly generated XML-formatted
	 * config from which the CLIs converted were just successfully uploaded to
	 * its corresponding device.
	 * 
	 * @param hiveAp
	 *            whose old config with type specified as <tt>configType</tt> is
	 *            going to be replaced with newly generated config.
	 * 
	 * @param configType
	 *            the type of config to be replaced.
	 */
	private void replaceConfig(HiveAp hiveAp, ConfigType configType) {
		String hiveApMac = hiveAp.getMacAddress();
		String domainName = hiveAp.getOwner().getDomainName();
		String oldXmlCfgPath;
		String newXmlCfgPath;

		switch (configType) {
		case AP_FULL:
		case AP_DELTA:
		case AP_AUDIT:
			oldXmlCfgPath = AhConfigUtil.getFullOldXmlConfigPath(domainName,
					hiveApMac);
			newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(domainName,
					hiveApMac);
			break;
		case USER_FULL:
		case USER_DELTA:
		case USER_AUDIT:
			oldXmlCfgPath = AhConfigUtil.getUserOldXmlConfigPath(domainName,
					hiveApMac);
			newXmlCfgPath = AhConfigUtil.getUserNewXmlConfigPath(domainName,
					hiveApMac);
			break;
		default:
			log.warn("replaceConfig",
					"Unexpected Config Type: " + configType.toString());
			return;
		}

		File newXmlCfg = new File(newXmlCfgPath);

		if (newXmlCfg.exists()) {
			File oldXmlCfg = new File(oldXmlCfgPath);

			try {
				log.info("replaceConfig",
						"Replacing config " + oldXmlCfg.getName() + " with "
								+ newXmlCfg.getName());
				FileManager.getInstance().copyFile(newXmlCfg, oldXmlCfg);
				log.info("replaceConfig", "Successfully replaced config "
						+ oldXmlCfg.getName() + " with " + newXmlCfg.getName());
			} catch (IOException e) {
				log.error("replaceConfig", "Config replacing failed. Device: "
						+ hiveAp + "; Config Type: " + configType.toString(), e);
			}
		} else {
			log.warn("replaceConfig", newXmlCfgPath
					+ " doesn't exist. Give up config replacing.");
		}
	}
	
	private void generateCloudCA(AhCloudAuthCAGenerateEvent event){
		HiveAp hiveAp = event.getHiveAp();
		
		HmCloudAuthCertMgmtImpl caImpl = new HmCloudAuthCertMgmtImpl();
		HmCloudAuthCertResult caResult = caImpl.updateCertification(hiveAp);
		event.setResult(caResult);
		
		configEventMgmt.notify(event);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof HiveAp){
			HiveAp hiveAp = (HiveAp)bo;
			if(hiveAp.getDownloadInfo() != null)
				hiveAp.getDownloadInfo().getId();
			if(hiveAp.getPppoeAuthProfile() != null)
				hiveAp.getPppoeAuthProfile().getId();
			if(hiveAp.getConfigTemplate() != null)
				hiveAp.getConfigTemplate().getId();
		}
		return null;
	}

}