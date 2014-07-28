package com.ah.be.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.app.HmBeLogUtil;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * configuration util
 *@filename		ConfigUtil.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-18 01:41:59
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history<br>
 * 1. 2007-11-21 juyizhou add function getProfileString()& setProfileString(), because API
 * Properties is not good choose for ini operation.<br>
 */
public class ConfigUtil {

	private static final Tracer log = new Tracer(ConfigUtil.class);

	private static final String	configFile							= AhDirTools.getHmRoot()
																			+ "config.ini";

	/**
	 * define section/key name
	 */
	// local
	public static final String	SECTION_LOCAL						= "resource local";
	public static final String	KEY_LOCAL							= "local";

	// debug
	public static final String	SECTION_DEBUG						= "debug";
	public static final String	KEY_TCPPORT							= "tcpPort";

	// email notification
	public static final String	SECTION_MAIL						= "mail";
	public static final String	KEY_CONCURRENT_NUM					= "concurrentNum";

	// communication
	public static final String	SECTION_COMMUNICATION				= "communication";
	public static final String	KEY_CAPWAPSERVER					= "capwapServer";
	public static final String	KEY_CAPWAPPORT						= "capwapPort";

	public static final String	SECTION_MEMORYDB					= "memorydb";
	public static final String	KEY_MEMDB_URL						= "url";
	public static final String	KEY_MEMDB_DRIVER					= "driver";
	public static final String	KEY_MEMDB_USER						= "user";
	public static final String	KEY_MEMDB_PASSWORD					= "password";

	// gui
	public static final String	SECTION_GUI							= "gui";
	public static final String	KEY_SLA_TIME_SPAN					= "sla_time_span";
	public static final String	KEY_SHOW_CLIWINDOW					= "show_cliwindow";

	// admin threads
	public static final String	SECTION_ADMIN						= "admin_threads";
	public static final String	KEY_THREAD_NUM						= "threadnum";

	// APPLICATION
	public static final String	SECTION_APPLICATION					= "application";
	public static final String	KEY_APPLICATION_TYPE				= "appType";
	public static final String	KEY_APPLICATION_SUPPORTSIMULATOR	= "supportsimulator";
	public static final String	KEY_APPLICATION_HTTPENABLE			= "httpEnable";
	public static final String	KEY_APPLICATION_SOFTWARE_MODEL		= "softwareModel";
	public static final String	KEY_ECWPSERVER						= "ecwpServer";

	public static final String	VALUE_ECWP_DEFAULT					= "0";
	public static final String	VALUE_ECWP_DEPAUL					= "1";
	public static final String	VALUE_ECWP_NNU						= "2";

	public static final String	VALUE_SOFTWARE_MODEL_RELEASE		= "release";
	public static final String	VALUE_SOFTWARE_MODEL_DEBUG			= "debug";

	public static final String	VALUE_APPLICATION_TYPE_HM			= "hm";
	public static final String	VALUE_APPLICATION_TYPE_HHM			= "hhm";
	public static final String	VALUE_APPLICATION_TYPE_PLANNER		= "planner";
	public static final String	VALUE_APPLICATION_TYPE_DEMO			= "demo";
	
	public static final String     ENABLE_YUI_CDN                      = "enable_YUI_CDN";

	// portal
	public static final String	SECTION_PORTAL						= "portal";
	public static final String	SUPPORT_MAIL_ADDRESS				= "support_mail_address";

	// performance
	public static final String	SECTION_PERFORMANCE					= "performance";
	public static final String	KEY_POLLING_DEVICE_NUMBER_PER_SEC	= "device_number_per_second";
	public static final String	KEY_ENABLE_TABLE_PARTITION			= "enable_table_partition";
	public static final String  KEY_HTTPCONNECTIONS_LIMIT			= "httpconnections_limit";
	public static final String  KEY_NETDUMP_FILE_MAX_NUMBER         = "netdump_file_max_number";
	public static final String  KEY_OPEN_REPORT_COLLECTION_HIGH_INTERVAL = "open_report_collection_high_interval";
	
	// support
	public static final String	SECTION_SUPPORT						= "support";
	public static final String	KEY_MYHIVE_URL						= "myhive_url";
	public static final String	KEY_REDIRECTOR_URL					= "redirector_url";
	public static final String	SUPPORT_PAGE_URL					= "support_page_url";

	public static final String	SECTION_DS							= "download_server";
	public static final String	KEY_DS_ENABLE						= "ds_enable";
	public static final String	KEY_DS_SERVER						= "ds_server";
	public static final String	KEY_DS_SIMULATOR					= "simulator_enable";

	// google maps
	public static final String	SECTION_GOOGLE_MAPS					= "google_maps";
	public static final String	KEY_GM_API_KEY						= "gm_api_key";
	public static final String	KEY_GM_LICENSE_KEY					= "gm_license_key";
	
	// aerohive mdm standard 
	public static final String	SECTION_AEROHIVE_MDM				= "aerohive_mdm";
	public static final String	KEY_URL_ROOT_PATH					= "acm_url_gateway";
	public static final String  HM_ACM_USER                         = "hm_auth_username";
	public static final String  HM_ACM_PASSWORD                     = "hm_auth_password";
	public static final String	KEY_API_VERSION						= "api_version";
	public static final String  KEY_URL_ROOT_VIEW_PATH              = "acm_url_console";
	
	// aerohive mdm beta
	public static final String KEY_BETA_URL_ROOT_PATH 				= "beta_acm_url_gateway";
	public static final String KEY_BETA_URL_ROOT_VIEW_PATH 			= "beta_acm_url_console";
	
	//OpenDNS
	public static final String OPENDNS_SECTION = "OpenDNS";
	public static final String OPENDNS_URL = "openDNS_url";
	public static final String OPENDNS_API_KEY = "openDNS_api_key";

	/**
	 * modify config info
	 *
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param value
	 *            new value
	 * @return true if operation success, otherwise false
	 */
	public static boolean setConfigInfo(String section, String key, String value) {
		return setConfigInfo(configFile, section, key, value);
	}

	/**
	 * modify config info
	 *
	 * @param file
	 *            file Path
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param value
	 *            new value
	 * @return true if operation success, otherwise false
	 */
	public static boolean setConfigInfo(String file, String section, String key, String value) {
		boolean isSucc;
		try {
			isSucc = setProfileString(file, section, key, value);
		} catch (IOException e) {
			log.error("setConfigInfo", "Set configuration failed! Input value(file=" + file
					+ ",section=" + section + ",key=" + key + ",value=" + value + ")", e);

			return false;
		}

		return isSucc;
	}

	/**
	 * get config info from config.ini configuration file
	 *
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @return value, if can't read value, will return null
	 */
	public static String getConfigInfo(String section, String key) {
		return getConfigInfo(section, key, null);
	}

	/**
	 * get config info from config.ini configuration file
	 *
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param defaultValue
	 *            default value
	 * @return value, if can't read value, will return defaultvalue
	 */
	public static String getConfigInfo(String section, String key, String defaultValue) {
		return getConfigInfo(configFile, section, key, defaultValue);
	}

	/**
	 * get config info from .ini configuration file
	 *
	 * @param file
	 *            file Path
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param defaultValue
	 *            default value
	 * @return value, if can't read value, will return defaultvalue
	 */
	public static String getConfigInfo(String file, String section, String key, String defaultValue) {
		String value;
		try {
			value = getProfileString(file, section, key, defaultValue);
		} catch (IOException e) {
			log.error("getConfigInfo",
					"Get configuration failed! Input value(file=" + file + ",section=" + section
							+ ",key=" + key + ",defaultValue=" + defaultValue + ")", e);
			return defaultValue;
		}

		return value;
	}

	/**
	 * get config info from .ini configuration file
	 *
	 * @param file
	 *            file Path
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param defaultValue
	 *            if error happens when operation, will return default value
	 * @return value
	 * @throws IOException
	 *             -
	 */
	private static String getProfileString(String file, String section, String key,
			String defaultValue) throws IOException {
		String strLine, value;
		boolean isInSection = false;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				strLine = strLine.split("[;]")[0];
				Pattern p;
				Matcher m;

				p = Pattern.compile("\\[\\s*.*\\s*\\]");
				m = p.matcher((strLine));

				if (m.matches()) {
					p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
					m = p.matcher(strLine);
					isInSection = m.matches();
				}

				if (isInSection) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");

					if (strArray.length == 1) {
						value = strArray[0].trim();

						if (value.equalsIgnoreCase(key)) {
							value = "";
							return value;
						}
					} else if (strArray.length == 2) {
						value = strArray[0].trim();

						if (value.equalsIgnoreCase(key)) {
							value = strArray[1].trim();
							return value;
						}
					} else if (strArray.length > 2) {
						value = strArray[0].trim();

						if (value.equalsIgnoreCase(key)) {
							value = strLine.substring(strLine.indexOf("=") + 1).trim();
							return value;
						}
					}
				}
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}

			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}
		}

		return defaultValue;

	}

	/**
	 * modify config info
	 *
	 * @param file
	 *            file Path
	 * @param section
	 *            section of config info, necessary,can not empty
	 * @param key
	 *            key of config info
	 * @param value
	 *            new value
	 * @return true if operation success, otherwise false
	 * @throws IOException
	 *             -
	 */
	private static boolean setProfileString(String file, String section, String key, String value)
			throws IOException {
		String fileContent, allLine, strLine, newLine, remarkStr;
		String getValue;
		boolean isInSection = false;
		fileContent = "";
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while ((allLine = br.readLine()) != null) {
				allLine = allLine.trim();
				if (allLine.split("[;]").length > 1) {
					remarkStr = ";" + allLine.split(";")[1];
				} else {
					remarkStr = "";
				}

				strLine = allLine.split(";")[0];
				Pattern p = Pattern.compile("\\[\\s*.*\\s*\\]");
				Matcher m = p.matcher((strLine));
				if (m.matches()) {
					p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
					m = p.matcher(strLine);
					isInSection = m.matches();
				}

				if (isInSection) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					getValue = strArray[0].trim();

					if (getValue.equalsIgnoreCase(key)) {
						newLine = getValue + " = " + value + " " + remarkStr;
						fileContent += newLine + "\r\n";
						while ((allLine = br.readLine()) != null) {
							fileContent += allLine + "\r\n";
						}

						fw = new FileWriter(file, false);
						bw = new BufferedWriter(fw);
						bw.write(fileContent);

						return true;
					}
				}
				fileContent += allLine + "\r\n";
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}

			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}

			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}

			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}
		}

		return false;
	}

	/**
	 * Get upgrade info for one vhm by domain name before 3.5
	 *
	 * @param domainName -
	 * @return Map<String, HhmUpgradeVersionInfo>
	 */
	public static Map<String, HhmUpgradeVersionInfo> getUpgradeInfoForVHM(String domainName) {
		try {
			List<HhmUpgradeVersionInfo> allHhm = QueryUtil.executeQuery(HhmUpgradeVersionInfo.class, new SortParams(
					"hmVersion"), null);
			String erroMsg = "VHM (" + domainName + ") cannot upgrade. ";
			if (!allHhm.isEmpty()) {
				Map<String, List<HhmUpgradeVersionInfo>> allVersion = new HashMap<String, List<HhmUpgradeVersionInfo>>();
				HhmUpgradeVersionInfo hhm;
				List<HhmUpgradeVersionInfo> verHhm;

				// set object as version
				for (Object obj : allHhm) {
					hhm = (HhmUpgradeVersionInfo) obj;
					verHhm = allVersion.get(hhm.getHmVersion());
					if (null == verHhm) {
						verHhm = new ArrayList<HhmUpgradeVersionInfo>();
						verHhm.add(hhm);
						allVersion.put(hhm.getHmVersion(), verHhm);
					} else {
						verHhm.add(hhm);
					}
				}
				Set<String> versions = allVersion.keySet();
				if (!versions.isEmpty()) {
					// sort the version
					List<String> sortList = new ArrayList<String>(versions);
					Collections.sort(sortList, new Comparator<String>() {
						@Override
						public int compare(String arg0, String arg1) {
							return arg0.hashCode() - arg1.hashCode();
						}
					});
					float curVer = Float.parseFloat(NmsUtil.getVersionInfo().getMainVersion());
					HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class,
							"domainName", domainName);
					for (int i = 0; i < sortList.size(); i++) {
						float sortVer = Float.parseFloat(sortList.get(i));
						// get can upgrade version and object
						if (sortVer >= curVer) {
							Map<String, HhmUpgradeVersionInfo> result = new LinkedHashMap<String, HhmUpgradeVersionInfo>();
							for (int j = i; j < sortList.size(); j++) {
								HhmUpgradeVersionInfo upVersion = getTheBestHHMToUpgrade(allVersion
										.get(sortList.get(j)), domain.getMaxApNum());
								if (null != upVersion) {
									result.put(sortList.get(j), upVersion);
								}
							}
							if (result.isEmpty()) {
								HmBeLogUtil
										.addSystemLog(
												HmSystemLog.LEVEL_MAJOR,
												HmSystemLog.FEATURE_ADMINISTRATION,
												erroMsg
														+ MgrUtil
																.getUserMessage(
																		"administrate.vhm.upgrade.system.log.no.appropriate.server",
																		String.valueOf(domain
																				.getMaxApNum())));
								return null;
							} else {
								return result;
							}
						}
					}
					HmBeLogUtil
							.addSystemLog(
									HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_ADMINISTRATION,
									erroMsg
											+ MgrUtil
													.getUserMessage(
															"administrate.vhm.upgrade.system.log.no.appropriate.version.server",
															NmsUtil.getVersionInfo()
																	.getMainVersion()));
				}
			} else {
				HmBeLogUtil
						.addSystemLog(
								HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_ADMINISTRATION,
								erroMsg
										+ MgrUtil
												.getUserMessage("administrate.vhm.upgrade.system.log.no.server"));
			}
		} catch (Exception e) {
			log.error("getUpgradeInfoForVHM", "Get upgrade information error.", e);
		}
		return null;
	}

	/**
	 * Get the best upgrade info for one version before 3.5
	 *
	 * @param hostedHm the hhms of this version
	 * @param currentAP -
	 * @return HhmUpgradeVersionInfo
	 */
	public static HhmUpgradeVersionInfo getTheBestHHMToUpgrade(
			List<HhmUpgradeVersionInfo> hostedHm, int currentAP) {
		HhmUpgradeVersionInfo bestResult = null;
		// more than one object
		if (hostedHm.size() > 1) {
			int maxAp = 0;
			for (HhmUpgradeVersionInfo hhm : hostedHm) {
				// the hhm can add vhm
				if (hhm.getLeftVhmCount() > 0) {
					// the hhm has the most left ap
					if (hhm.getLeftApCount() > maxAp) {
						maxAp = hhm.getLeftApCount();
						bestResult = hhm;
					}
				}
			}
			if (maxAp >= currentAP) {
				return bestResult;
			}
		} else {
			bestResult = hostedHm.get(0);
			if (bestResult.getLeftApCount() >= currentAP && bestResult.getLeftVhmCount() > 0) {
				return bestResult;
			}
		}
		return null;
	}

	/**
	 * Check if exist upgrade version for this vhm from 3.5r4
	 *
	 * @param domainName domain name
	 * @param apNumber this vhm ap number
	 * @return boolean
	 */
	public static boolean existUpgradeInfoForVHM(String domainName, int apNumber) {
		List<HhmUpgradeVersionInfo> allHhm = QueryUtil.executeQuery(HhmUpgradeVersionInfo.class, new SortParams(
			"hmVersion"), null);
		for (HhmUpgradeVersionInfo upInfo : allHhm) {
			if (upInfo.getLeftApCount() >= apNumber) {
				if (QueryUtil.findRowCount(HMUpdateSoftwareInfo.class, new FilterParams("domainName = :s1 AND hmVersion = :s2",
					new Object[]{domainName, upInfo.getHmVersion()})) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getBetaConfigUrl(){
		return getConfigInfo(SECTION_AEROHIVE_MDM, KEY_BETA_URL_ROOT_PATH);
	}
	public static String getStandardConfigUrl(){
		return getConfigInfo(SECTION_AEROHIVE_MDM, KEY_URL_ROOT_PATH);
	}
	
	public static String getBetaConfigViewUrl(){
		return getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, KEY_BETA_URL_ROOT_VIEW_PATH);
	}
	
	public static String getStandardConfigViewUrl(){
		return getConfigInfo(SECTION_AEROHIVE_MDM, KEY_URL_ROOT_VIEW_PATH);
	}
	public static String getVersion(){
		return getConfigInfo(SECTION_AEROHIVE_MDM,KEY_API_VERSION);
	}
	
	//This will return the URL for related Acm rest service, like onboarding, configure acm server to AP etc
	public static String getACMConfigServerUrl(){		
		return isEnableBetaAcm() ? getBetaConfigUrl() : getStandardConfigUrl();
	}
	
	// This will return URL when viewing ACM from HM
	public static String getACMConfigServerViewUrl(){
		boolean useBetaAcm = isEnableBetaAcm();
		if(useBetaAcm){
			return getBetaConfigViewUrl() != null ? getBetaConfigViewUrl() :  getBetaConfigUrl() + "/console";
		}else{
			return getStandardConfigViewUrl() != null ? getStandardConfigViewUrl() : getStandardConfigUrl() + "/console";
		}
	}
	
	private static boolean isEnableBetaAcm(){
		boolean betaConfig = false;
		List<?> betaFlags = QueryUtil.executeQuery("SELECT enabledBetaIDM FROM " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), (Long) null, 1);
		if(betaFlags != null && !betaFlags.isEmpty()){
			betaConfig = (Boolean)betaFlags.get(0);
		}
		return betaConfig;
	}
}