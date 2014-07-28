package com.ah.be.common;

import java.io.File;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.config.CwpAction;
import com.ah.util.Tracer;

public final class AhDirTools {

	private static final Tracer log = new Tracer(AhDirTools.class.getSimpleName());

	public static String getHmRoot() {
		String hmRoot = System.getenv("HM_ROOT");

		if (hmRoot == null || hmRoot.trim().isEmpty()) {
			hmRoot = "/HiveManager/tomcat/webapps/hm";
		}

		if (!hmRoot.endsWith(File.separator)) {
			hmRoot += File.separator;
		}

		checkDir(hmRoot);

		return hmRoot;
	}

	/* This directory is used for conserving some temporary files, e.g. client monitoring log archive, etc. Note that overall contents in this directory will be completely deleted when launching HM */
	public static String getTempFileDir() {
		String os = System.getProperty("os.name");
		String tempFileDir = (os.toLowerCase().contains("windows") ? "C:" + File.separator : File.separator) + "tmp" + File.separator;
		checkDir(tempFileDir);

		return tempFileDir;
	}

	public static String getCmTempFileDir() {
		String cmTempFileDir = getTempFileDir() + "cm" + File.separator;
		checkDir(cmTempFileDir);

		return cmTempFileDir;
	}

	public static String getConstantConfigDir() {
		String constantFileDir = getHmRoot() + "WEB-INF" + File.separator + "hmconf"
				+ File.separator + "constant" + File.separator;
		checkDir(constantFileDir);

		return constantFileDir;
	}

	public static String getCliSchemaDir() {
		String schemaDir = getHmRoot() + "schema" + File.separator;
		checkDir(schemaDir);

		return schemaDir;
	}
	
	public static String getCliConfigPath(){
		return getCliSchemaDir() + "cli_config" + File.separator;
	}

	public static String getDefaultValueXmlDir() {
		String defaultValueXmlDir = getCliSchemaDir() + "defaultValue" + File.separator;
		checkDir(defaultValueXmlDir);

		return defaultValueXmlDir;
	}

	public static String getVersionXMLDir() {
		return getCliSchemaDir() + "versionXML" + File.separator;
	}

	public static String getVersionSchemaDir() {
		return getCliSchemaDir() + "versionSchema" + File.separator;
	}

	public static String getHmHome() {
		String hmHome = System.getenv("HM_CITADEL");

		if (hmHome == null || hmHome.trim().isEmpty()) {
			hmHome = "/HiveManager";
		}

		if (!hmHome.endsWith(File.separator)) {
			hmHome += File.separator;
		}

		checkDir(hmHome);

		return hmHome;
	}

	public static String getCapwapDir() {
		String capwapDir = getHmHome() + "capwap" + File.separator;
		checkDir(capwapDir);

		return capwapDir;
	}

	public static String getCliParserDir() {
		String cliParserDir = getHmHome() + "cli_parser" + File.separator;
		checkDir(cliParserDir);

		return cliParserDir;
	}

	public static String getCliParserLogDir() {
		String cliParserLogDir = getCliParserDir() + "log" + File.separator;
		checkDir(cliParserLogDir);

		return cliParserLogDir;
	}

	public static String getSshKeyDir() {
		String sshKeyDir = getHmHome() + "ssh_key" + File.separator;
		checkDir(sshKeyDir);

		return sshKeyDir;
	}

	public static String getOpenFileDir() {
		String openFileDir = getHmHome() + "open_file" + File.separator;
		checkDir(openFileDir);

		return openFileDir;
	}

	public static String getMibDir() {
		String mibDir = getOpenFileDir() + "mibs" + File.separator;
		checkDir(mibDir);

		return mibDir;
	}

	public static String getDictionaryDir() {
		String dictionaryDir = getOpenFileDir() + "dict" + File.separator;
		checkDir(dictionaryDir);

		return dictionaryDir;
	}

	public static String getRadiusDictionaryDir() {
		String radiusDictionaryDir = getDictionaryDir() + "radius" + File.separator;
		checkDir(radiusDictionaryDir);

		return radiusDictionaryDir;
	}

	public static String getMacOuiDictionaryDir() {
		String macOuiDictionaryDir = getDictionaryDir() + "macoui" + File.separator;
		checkDir(macOuiDictionaryDir);

		return macOuiDictionaryDir;
	}
	
	public static String getCidClientsDir() {
		String cidClientsDir = getDictionaryDir() + "cidClients" + File.separator;
		checkDir(cidClientsDir);

		return cidClientsDir;
	}

	public static String getDownloadsDir() {
		String downloadsDir = getHmHome() + "downloads" + File.separator;
		checkDir(downloadsDir);

		return downloadsDir;
	}

	public static String getDumpDir() {
		String dumpDir = getDownloadsDir() + "home" + File.separator + "image" + File.separator + "dump" + File.separator;
		checkDir(dumpDir);

		return dumpDir;
	}

	public static String getHiveApDir() {
		String hiveApDir = File.separator + "HiveAP" + File.separator;
		checkDir(hiveApDir);

		return hiveApDir;
	}

	public static String getKernelDumpDir() {
		String os = System.getProperty("os.name");
		String kernelDumpDir = (os.toLowerCase().contains("windows") ? getDumpDir() : getHiveApDir()) + "kernel_dump" + File.separator;
		checkDir(kernelDumpDir);

		return kernelDumpDir;
	}

	public static String getKernelDumpDir(String hiveApMac) {
		String kernelDumpDir = getKernelDumpDir() + hiveApMac + File.separator;
		checkDir(kernelDumpDir);

		return kernelDumpDir;
	}

	public static String getTechDir() {
		String os = System.getProperty("os.name");
		String techDir = (os.toLowerCase().contains("windows") ? getDumpDir() : getHiveApDir()) + "tech_dump" + File.separator;
		checkDir(techDir);

		return techDir;
	}

	public static String getTechDir(String hiveApMac) {
		String techDir = getTechDir() + hiveApMac + File.separator;
		checkDir(techDir);

		return techDir;
	}

	public static String getDomainDir(String domainName) {
		String domainDir = getDownloadsDir() + domainName + File.separator;
		checkDir(domainDir);

		return domainDir;
	}

	public static String getCertificateDir(String domainName) {
		String certificateDir = getDomainDir(domainName) + "aerohiveca" + File.separator;
		checkDir(certificateDir);

		return certificateDir;
	}
	
	public static String getCloudAuthCaDir(String domainName, String macAddress){
		String cloudAuthCaDir = getDomainDir(domainName) + "cloudauthca" + File.separator + macAddress + File.separator;
		checkDir(cloudAuthCaDir);

		return cloudAuthCaDir;
	}

	/**
	 * Get the image directory path, but only 'home' domain contains image
	 * files.
	 * 
	 * @param domainName
	 *            the name of a specific domain.
	 * @return the image directory path.
	 */
	public static String getImageDir(String domainName) {
		String imageDir = getDomainDir(domainName) + "image" + File.separator;
		checkDir(imageDir);

		return imageDir;
	}

	/**
	 * Get the L7 signature directory path, but only 'home' domain contains
	 * signature files.
	 * 
	 * @param domainName
	 *            the name of a specific domain.
	 * @return the signature directory path.
	 */
	public static String getL7SignatureDir(String domainName) {
		String signatureDir = getDomainDir(domainName) + "signature"
				+ File.separator;
		checkDir(signatureDir);

		return signatureDir;
	}

	/**
	 * Get the osDetection file directory path.
	 * @return the image directory path.
	 */
	public static String getOsDetectionDir() {
		String fingerprintsDir = getDownloadsDir() + "home" + File.separator + "fingerprints" + File.separator;
		checkDir(fingerprintsDir);

		return fingerprintsDir;
	}

	/**
	 * Get the HiveManager image directory path, but only 'home' domain contains HiveManager image
	 * files which got from license server.
	 * 
	 * @return the image directory path.
	 */
	public static String getHiveManagerImageDir() {
		String imageDir = getDomainDir(HmDomain.HOME_DOMAIN) + "hiveManagerImage" + File.separator;
		checkDir(imageDir);

		return imageDir;
	}

	/**
	 * Get the image directory path, for external used.
	 * \HiveManager\downloads\home\image\ as the root.
	 * 
	 * @return the image directory path
	 */
	public static String getExtenalImageDir() {
		return "";
	}

	public static String getPageResourcesDir(String domainName) {
		String pageResourcesDir = BeTopoModuleUtil.FILE_ROOT + File.separator + domainName
				+ File.separator + "CwpPageResources" + File.separator;
		checkDir(pageResourcesDir);

		return pageResourcesDir;
	}

	public static String getConfigDir(String domainName) {
		String configDir = getDomainDir(domainName) + "script" + File.separator;
		checkDir(configDir);

		return configDir;
	}

	public static String getBootstrapConfigDir(String domainName) {
		String bootstrapConfigDir = getConfigDir(domainName) + "bootstrap" + File.separator;
		checkDir(bootstrapConfigDir);

		return bootstrapConfigDir;
	}

	public static String getNewConfigDir(String domainName) {
		String newConfigDir = getConfigDir(domainName) + "new" + File.separator;
		checkDir(newConfigDir);

		return newConfigDir;
	}

	public static String getRunConfigDir(String domainName) {
		String runConfigDir = getConfigDir(domainName) + "run" + File.separator;
		checkDir(runConfigDir);

		return runConfigDir;
	}

	public static String getXmlConfigDir(String domainName) {
		String xmlConfigDir = getConfigDir(domainName) + "xml" + File.separator;
		checkDir(xmlConfigDir);

		return xmlConfigDir;
	}
	
	public static String getSimulateConfigDir(String domainName) {
		String simulateConfigDir = getConfigDir(domainName) + "simulate" + File.separator;
		checkDir(simulateConfigDir);

		return simulateConfigDir;
	}

	public static String getBootstrapXmlConfigDir(String domainName) {
		String bootstrapXmlConfigDir = getXmlConfigDir(domainName) + "bootstrap" + File.separator;
		checkDir(bootstrapXmlConfigDir);

		return bootstrapXmlConfigDir;
	}

	public static String getNewXmlConfigDir(String domainName) {
		String newXmlConfigDir = getXmlConfigDir(domainName) + "new" + File.separator;
		checkDir(newXmlConfigDir);

		return newXmlConfigDir;
	}

	public static String getOldXmlConfigDir(String domainName) {
		String oldXmlConfigDir = getXmlConfigDir(domainName) + "old" + File.separator;
		checkDir(oldXmlConfigDir);

		return oldXmlConfigDir;
	}

	public static String getRunXmlConfigDir(String domainName) {
		String runXmlConfigDir = getXmlConfigDir(domainName) + "run" + File.separator;
		checkDir(runXmlConfigDir);

		return runXmlConfigDir;
	}

	public static String getViewBasedXmlConfigDir(String domainName) {
		String viewBasedXmlConfigDir = getXmlConfigDir(domainName) + "view" + File.separator;
		checkDir(viewBasedXmlConfigDir);

		return viewBasedXmlConfigDir;
	}
	
	public static String getTempXmlConfigDir(String domainName) {
		String tempXmlConfigDir = getXmlConfigDir(domainName) + "temp" + File.separator;
		checkDir(tempXmlConfigDir);

		return tempXmlConfigDir;
	}

	public static String getCwpDir(String domainName) {
		String cwpDir = getDomainDir(domainName) + "cwp" + File.separator;
		checkDir(cwpDir);

		return cwpDir;
	}

	public static String getCwpServerKeyDir(String domainName) {
		String cwpServerKeyDir = getCwpDir(domainName) + "serverkey" + File.separator;
		checkDir(cwpServerKeyDir);

		return cwpServerKeyDir;
	}

	public static String getCwpWebDir(String domainName) {
		String cwpWebDir = getCwpDir(domainName) + "webpage" + File.separator;
		checkDir(cwpWebDir);

		return cwpWebDir;
	}

	public static String getCwpWebPageDir(String domainName, String webPageDirName) {
		String cwpWebPageDir = getCwpWebDir(domainName) + webPageDirName + File.separator;
		checkDir(cwpWebPageDir);

		return cwpWebPageDir;
	}

	/* Trouble Shooting */
	public static String getTsDir(String domainName) {
		String cwpDir = getDomainDir(domainName) + "ts" + File.separator;
		checkDir(cwpDir);

		return cwpDir;
	}

	/* AP Trouble Shooting */
	public static String getTsApDir(String domainName) {
		String tsApDir = getTsDir(domainName) + "ap" + File.separator;
		checkDir(tsApDir);

		return tsApDir;
	}

	/* Client Monitoring */
	public static String getCmDir(String domainName) {
		String cmDir = getTsApDir(domainName) + "cm" + File.separator;
		checkDir(cmDir);

		return cmDir;
	}
	
	/* Spectrum analysis */
	public static String getSADataDir(String domainName, String apMac) {
		String saDir = getHmRoot() + CwpAction.DEFAULT_DOMAINS_DIRECTORY
				+ File.separator + domainName + File.separator + "sa"
				+ File.separator + apMac + File.separator;
		checkDir(saDir);

		return saDir;
	}

	/**
	 * get upload directory
	 */
	public static String getUploadDir() {
		String saDir = getHmRoot() + "upload" + File.separator;
		checkDir(saDir);
		
		return saDir;
	}
	
	/**
	 * get data collection upload directory
	 */
	public static String getDataCollectionUploadDir() {
		String saDir = getUploadDir() + "datacollection" + File.separator;
		checkDir(saDir);
		
		return saDir;
	}
	
	/**
	 * get interface report upload directory
	 */
	public static String getInterfaceReportUploadDir(String subDir) {
		String dir = getUploadDir() + "interfacereport" + File.separator + subDir + File.separator;
		checkDir(dir);
		
		return dir;
	}
	
	/**
	 * get application report upload directory
	 */
	public static String getApplicationReportUploadDir() {
		String dir = getUploadDir() + "appreport" + File.separator;
		checkDir(dir);
		
		return dir;
	}
	
 
	public static String getApplicationReportBackupUploadDir() {
		String dir = getUploadDir() + "appreport_backup" + File.separator;
		checkDir(dir);
		return dir;
	}
	
	/*
	 * get net dump upload directory
	 */
	public static String getNetdumpUploadDir() {
		String dir = getUploadDir() + "kddr" + File.separator;
		checkDir(dir);
		return dir;
	}
	
	public static void checkDir(String dirName) {
		File dir = new File(dirName);

		if (!dir.isDirectory()) {						
			boolean isSucc = dir.mkdirs();

			if (isSucc) {
				grantWritePermission(dir.getAbsolutePath());
				log.info("checkDir", dirName + " - Directory was created.");
			} else {
				log.error("checkDir", dirName + " - Directory creation was failure.");
			}
		}
	}
	
	public static String getImageConfigFilePath(){
		String path = getImageDir("home");
		return path + "ImageConfig.xml";
	}
	
	public static boolean grantWritePermission(String dirName){
		boolean isSucc = false;
		File dir = new File(dirName);	
		if (dir.isDirectory()) {
			isSucc =  BeAdminCentOSTools.exeSysCmd("chmod o+rw " + dirName);	
		}
		
		if (isSucc) {
			log.info("checkDir", dirName + " - Write permission granted successfully.");
		} else {
			log.error("checkDir", dirName + " - Write permission granted failed.");
		}
		return isSucc;
	}
}