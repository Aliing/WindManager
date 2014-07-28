/**
 *@filename		BeConfigModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:49:40 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.config;

import java.io.FileNotFoundException;
import java.io.Serializable;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.event.impl.AhConfigEventMgmtImpl;
import com.ah.be.config.hiveap.UpdateManager;
import com.ah.be.config.hiveap.UpdateObjectBuilder;
import com.ah.be.config.hiveap.UpdateResponseListener;
import com.ah.be.config.hiveap.distribution.ImageDistributor;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.config.image.ImageManager;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.config.xml.compare.AhConfigComparedException;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeConfigModule extends Serializable {

	/*
	 * <tt>DELTA</tt> denotes the delta config generation comes from the
	 * comparison between previous and new configs. <tt>AUDIT</tt> denotes the
	 * delta config generation comes from the comparison between running and new
	 * configs.
	 */
	enum ConfigType {
		BOOTSTRAP, AP_FULL, USER_FULL, AP_DELTA, AP_AUDIT, USER_DELTA, USER_AUDIT
	}

	/*
	 * The type of config to be fetched
	 */
	enum ConfigFetchType {
		RUNNING, USER
	}

	AhConfigEventMgmtImpl getConfigMgmt();

	String fetchConfig(HiveAp hiveAp, ConfigFetchType type, int timeout)
			throws AhConfigRetrievedException, AhConfigDisconnectException;

	/**
	 * Fetch running config from a specified HiveAP.
	 * 
	 * @param hiveAp
	 *            from which the running config is going to be fetched.
	 * @return the running config working on the specified HiveAP.
	 * @throws AhConfigRetrievedException
	 *             if any problem occurs while fetching the running config from
	 *             the specified HiveAP.
	 * @throws AhConfigDisconnectException 
	 */
	String fetchRunningConfig(HiveAp hiveAp) throws AhConfigRetrievedException, AhConfigDisconnectException;

	/**
	 * Fetch user config from a specified HiveAP.
	 * 
	 * @param hiveAp
	 *            from which the user config is going to be fetched.
	 * @return the user config working on the specified HiveAP.
	 * @throws AhConfigRetrievedException
	 *             if any problem occurs while fetching the user config from the
	 *             specified HiveAP.
	 * @throws AhConfigDisconnectException 
	 */
	String fetchUserConfig(HiveAp hiveAp) throws AhConfigRetrievedException, AhConfigDisconnectException;

	/**
	 * <p>
	 * Generate bootstrap XML-formatted config.
	 * </p>
	 * 
	 * @param event
	 *            Consist of all the necessary messages which are used for
	 *            generating bootstrap XML-formatted config.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in config generation.
	 */
	void generateBootstrapXmlConfig(AhBootstrapGeneratedEvent event)
			throws AhConfigGeneratedException;

	/**
	 * <p>
	 * Generate XML-formatted config matching with the config version given, and
	 * the config generated will be put in the specified path.
	 * </p>
	 * 
	 * @param hiveAp
	 *            the generated config to be used for.
	 * @param configType
	 *            the type of config to be generated.
	 * @param xmlCfgPath
	 *            the path where the config is going to be generated.
	 * @param softVer
	 *            the software version of HiveAP.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in config generation.
	 * @return An instance of <tt>AhConfigGenerationResult</tt>.
	 */
	AhConfigGenerationResult generateXmlConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, String softVer, boolean isView) throws AhConfigGeneratedException;

	/**
	 * <p>
	 * Generate XML-formatted config corresponds to the config version given,
	 * the config to be generated will be output into the specified path.
	 * </p>
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * generateXmlConfig(hiveAp, outputPath)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * is equivalent to:
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * generateXmlConfig(hiveAp, hiveAp.getSoftVer(), outputPath)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param hiveAp
	 *            the generated config to be used for.
	 * @param configType
	 *            the type of config to be generated.
	 * @param xmlCfgPath
	 *            the path where the config is going to be generated.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in generating config.
	 * @return An instance of <tt>AhConfigGenerationResult</tt>.
	 */
	AhConfigGenerationResult generateXmlConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, boolean isView) throws AhConfigGeneratedException;

	/**
	 * <p>
	 * Parse CLIs into XML-formatted config.
	 * </p>
	 * 
	 * @param hiveAp
	 *            the CLI config to be parsed belongs to.
	 * @param configType
	 *            the type of config to be parsed.
	 * @param cliCfgPath
	 *            the path of CLI config to be parsed.
	 * @param xmlCfgPath
	 *            The path of XML-formatted config gotten from the CLI parsing
	 *            process.
	 * @return <tt>true</tt> success of parsing, <tt>false</tt> otherwise.
	 * @throws FileNotFoundException
	 *             if the CLI config to be parsed doesn't exist.
	 * @throws AhConfigParsedException
	 *             if the CLI parsing attempt fails for any reason.
	 */
	boolean parseCli(HiveAp hiveAp, ConfigType configType, String cliCfgPath, String xmlCfgPath, String cParseIgnorePath)
			throws FileNotFoundException, AhConfigParsedException;

	/**
	 * <p>
	 * Reinforce necessary default CLI values into running XML-formatted config.
	 * </p>
	 * 
	 * @param hiveAp
	 *            The specific HiveAP to operate to.
	 * @param configType
	 *            the type of config to be generated.
	 * @param runXmlCfgPath
	 *            The path in which the running XML-formatted config resides.
	 * @param newXmlCfgPath
	 *            The path in which the new XML-formatted config resides.
	 * @throws AhConfigReinforcedException
	 *             if any problem occurs in XML-formatted config reinforcing.
	 * @throws CreateXMLException
	 *             if any problem occurs in generating XML-formatted config.
	 */
	void reinforceRunningXmlConfig(HiveAp hiveAp, ConfigType configType, String runXmlCfgPath,
			String newXmlCfgPath, String cParseIgnoreCfgPath) throws AhConfigReinforcedException, CreateXMLException;

	/**
	 * <p>
	 * Compare old and new XML-formatted configs which reside in the specified
	 * paths separately.
	 * </p>
	 * 
	 * @param oldXmlCfgPath
	 *            The path in which the old XML-formatted config resides.
	 * @param newXmlCfgPath
	 *            The path in which the new XML-formatted config resides.
	 * @param hiveAp
	 *            the comparison applies for.
	 * @return CLIs after comparison between two XML-formatted configs.
	 * @throws FileNotFoundException
	 *             if the XML-formatted config specified as argument is absent.
	 * @throws AhConfigComparedException
	 *             if any problem occurs comparing in XML-formatted config.
	 * @throws AhConfigConvertedException
	 *             if any problem occurs while converting XML-formatted config
	 *             into CLIs.
	 */
	String compareConfigs(String oldXmlCfgPath, String newXmlCfgPath, HiveAp hiveAp)
			throws FileNotFoundException, AhConfigComparedException, AhConfigConvertedException;

	/**
	 * <p>
	 * Convert XML-formatted config into CLIs.
	 * </p>
	 * 
	 * @param xmlCfgPath
	 *            The path in which the XML-formatted config resides.
	 * @return CLIs which are converted from a XML-formatted config resides in
	 *         the specified path.
	 * @throws AhConfigConvertedException
	 *             if any problem occurs while converting XML-formatted config
	 *             into CLIs.
	 */
	String convertXmlConfigIntoClis(String xmlCfgPath) throws AhConfigConvertedException;

	/**
	 * <p>
	 * Generate bootstrap script by the given two arguments.
	 * </p>
	 * 
	 * @param clis
	 *            From which the bootstrap is generated.
	 * @param bootstrapCfgPath
	 *            The path of the bootstrap config to be generated.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in generating bootstrap.
	 */
	void generateBootstrapScript(String clis, String bootstrapCfgPath)
			throws AhConfigGeneratedException;

	/**
	 * <p>
	 * Generate script by the specific CLIs and script path.
	 * </p>
	 * 
	 * @param clis
	 *            From which the script is generated.
	 * @param scriptPath
	 *            The path of script to be generated.
	 * @throws AhConfigGeneratedException
	 *             if any problem occurs in generating script.
	 */
	void generateScript(String clis, String scriptPath) throws AhConfigGeneratedException;

	public String cliFinalize(String clis, String runningCfg, ConfigType configType, HiveAp hiveAp, String softVer) throws AhConfigRetrievedException, AhConfigDisconnectException;

	String generateBootstrapConfig(AhBootstrapGeneratedEvent event)
			throws AhConfigConvertedException, AhConfigGeneratedException;

	AhConfigGenerationResult generateConfig(HiveAp hiveAp, ConfigType configType,
			String xmlCfgPath, String softVer, boolean isView) throws AhConfigConvertedException,
			AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException;

	AhConfigGenerationResult generateConfig(HiveAp hiveAp, ConfigType configType, String xmlCfgPath, boolean isView)
			throws AhConfigConvertedException, AhConfigGeneratedException, AhConfigRetrievedException, AhConfigDisconnectException;

	/**
	 * <p>
	 * View config.
	 * </p>
	 * 
	 * @param hiveAp
	 *            the specific HiveAP to operate to.
	 * @param configType
	 *            indicate whether viewing the full or delta config.
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
	 *             if any problem occurs while retrieving the user config from
	 *             the specified HiveAP.
	 * @throws CreateXMLException
	 *             if any problem occurs in generating XML-formatted config.
	 * @return HiveAp configuration CLIs.
	 * @throws AhConfigDisconnectException 
	 * @throws Exception 
	 */
	String viewConfig(HiveAp hiveAp, ConfigType configType) throws FileNotFoundException,
			AhConfigComparedException, AhConfigConvertedException, AhConfigParsedException,
			AhConfigGeneratedException, AhConfigReinforcedException, AhConfigRetrievedException,
			CreateXMLException, AhConfigDisconnectException, Exception;

	UpdateManager getUpdateManager();

	UpdateResponseListener getUpdateResponseListener();

	ImageDistributor getImageDistributor();

	UpdateObjectBuilder getUpdateObjectBuilder();
	
	ProvisionProcessor getProvisionProcessor();
	
	ImageManager getImageSynupLS();
}