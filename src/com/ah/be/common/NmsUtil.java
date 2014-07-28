package com.ah.be.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.ah.be.activation.ActivationKeyOperation;
import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.config.AhConfigRetrievedException;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.hiveap.ImageInfo;
import com.ah.be.os.FileManager;
import com.ah.be.os.LinuxNetConfigImpl;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.be.os.WindowsNetConfigImpl;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.USBModemProfile;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.config.ImportTextFileAction;
import com.ah.util.CasTool;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;
import com.ah.util.devices.impl.Device;
import com.ah.xml.deviceProperties.DeviceObj;
import com.sun.management.OperatingSystemMXBean;

/**
 * @author root
 */
public class NmsUtil {

	private static final Tracer log = new Tracer(NmsUtil.class.getSimpleName());

	// the unit is minute
	public static int CAS_SERVER_SESSION_TIME_OUT = 60*2;

	private static final char[] AH_LEGAL_PASS_PHRASE_CHARS = { '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
			'A', 'B', 'C', 'D', 'E', 'F' };

//	public static final String IPCONFIG_ETH0_FILE = "/etc/sysconfig/network-scripts/ifcfg-eth0";

//	public static final String IPCONFIG_ETH1_FILE = "/etc/sysconfig/network-scripts/ifcfg-eth1";

	public static final int DEFAULT_HM_WEB_SERVER_REDIRECT_PORT = 8443;

	public static final int DEFAULT_HMOL_WEB_SERVER_REDIRECT_PORT = 443;

	public static final String DEFAULT_WEB_SERVER_AUTH_METHOD = "basic";

	private static final AtomicInteger debugGroupId = new AtomicInteger(1);

	private static String strPsd;

	private static final boolean isHHMApp;

	private static boolean isPlanner = false;

	private static final boolean isDemo;

	private static int webServerRedirectPort;

	private static String webServerLoginAuthMethod = DEFAULT_WEB_SERVER_AUTH_METHOD;

	private static OEMCustomer oemCustomer = null;

	private static final String OEM_RESOURCE_FILE_NAME = "oem-resource.txt";

	public static boolean TEACHER_VIEW_GLOBAL_ENABLED = false;

	private static String authServiceURL;

	private static String myHiveServiceURL;

	private static String redirectorServiceURL;

	private static String gmAPIKey;

	private static String gmLicenseKey;
	
	public static final String PORTAL_URL_FOR_ONPREMISE_HM = "portal.aerohive.com";
	
	public static final String PORTAL_URL_FOR_ONPREMISE_HM_BETA = "usqa-myhive-portal.aerohive.com";

	private static boolean enableYUICDN;
	
	static {
		final String appType = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_APPLICATION_TYPE, ConfigUtil.VALUE_APPLICATION_TYPE_HM);
		isHHMApp = ConfigUtil.VALUE_APPLICATION_TYPE_DEMO.equalsIgnoreCase(appType)
				|| ConfigUtil.VALUE_APPLICATION_TYPE_HHM.equalsIgnoreCase(appType)
				|| ConfigUtil.VALUE_APPLICATION_TYPE_PLANNER.equalsIgnoreCase(appType);
		isPlanner = ConfigUtil.VALUE_APPLICATION_TYPE_PLANNER.equalsIgnoreCase(appType);
		isDemo = ConfigUtil.VALUE_APPLICATION_TYPE_DEMO.equalsIgnoreCase(appType);

		gmAPIKey = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_GOOGLE_MAPS, ConfigUtil.KEY_GM_API_KEY, "");
		gmLicenseKey = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_GOOGLE_MAPS, ConfigUtil.KEY_GM_LICENSE_KEY, "");
		
		enableYUICDN = Boolean.parseBoolean(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION, 
		        ConfigUtil.ENABLE_YUI_CDN, "true"));

		// Read web server redirect port.
		if (webServerRedirectPort <= 0) {
			String catalinaHome = System.getenv("CATALINA_HOME");

			if (catalinaHome != null) {
				if (!catalinaHome.endsWith(File.separator)) {
					catalinaHome += File.separator;
				}

				String catalinaPropPath = catalinaHome + "conf" + File.separator + "catalina.properties";

				// Read port number from catalina.properties.
				try {
					InputStream in = new FileInputStream(catalinaPropPath);
					Properties catalinaProp = new Properties();
					catalinaProp.load(in);
					String httpsPort = catalinaProp.getProperty("https.port");

					if (httpsPort != null) {
						try {
							webServerRedirectPort = Integer.parseInt(httpsPort.trim());
						} catch (NumberFormatException nfe) {
							log.error("init", "The evaluation [" + httpsPort + "] to 'https.port' is not a numeric.", nfe);
						}
					}
				} catch (IOException e) {
					log.error("init", "Failed to load " + catalinaPropPath, e);
				}
			}
		}

		// Use default port if the port number could not be read out from the specified properties.
		if (webServerRedirectPort <= 0) {
			webServerRedirectPort = isHostedHMApplication() ? DEFAULT_HMOL_WEB_SERVER_REDIRECT_PORT : DEFAULT_HM_WEB_SERVER_REDIRECT_PORT;
		}

		// Read login authentication method from web.xml.
		String hmRoot = AhDirTools.getHmRoot();
		String webXmlPath = hmRoot + "WEB-INF" + File.separator + "web.xml";
		SAXReader reader = new SAXReader();

		try {
			Document webXmlDoc = reader.read(webXmlPath);
			Element root = webXmlDoc.getRootElement();
			String xPath = root.getPath() + "/*[name()='login-config']/*[name()='auth-method']";
			Node node = root.selectSingleNode(xPath);

			if (node != null) {
				webServerLoginAuthMethod = node.getText();

				if (webServerLoginAuthMethod != null && !webServerLoginAuthMethod.trim().isEmpty()) {
					webServerLoginAuthMethod = webServerLoginAuthMethod.trim().toLowerCase();
				} else {
					webServerLoginAuthMethod = DEFAULT_WEB_SERVER_AUTH_METHOD;
				}
			}
		} catch (Exception e) {
			log.error("init", "Failed to read " + webXmlPath + ", using default authentication method '" + webServerLoginAuthMethod + "' instead.");
		}
	}

//	private static final String PORTAL_DNS_NAME = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_PORTAL, ConfigUtil.KEY_DNS_NAME);

	private static final String SUPPORT_MAIL_ADDRESS = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_PORTAL, ConfigUtil.SUPPORT_MAIL_ADDRESS);

	private static final boolean useHttpProxy = isHttpProxy();

	private static final boolean httpEnable = "1".equals(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION, ConfigUtil.KEY_APPLICATION_HTTPENABLE, "0"));

	private static final String ADMIN_THREADS_NUM = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_ADMIN, ConfigUtil.KEY_THREAD_NUM, "5");

	//private static String CAS_SERVER = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_COMMUNICATION, ConfigUtil.KEY_CASSERVER);
	
	private static final String SUPPORT_PAGE_URL = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_SUPPORT, ConfigUtil.SUPPORT_PAGE_URL);

	public static String getSupportPageUrl() {
		return SUPPORT_PAGE_URL;
	}
	
	public static String getHMScpUser() {
		return System.getProperty("hm.scp.username");
	}

	public static String getHMScpPsd() {
		if (null != strPsd) {
			return strPsd;
		}

		strPsd = System.getProperty("hm.scp.password");

		String strPsdExec = "/HiveManager/encryptscpuser/getScpuserPsd.sh";

		File fExecFile = new File(strPsdExec);

		if (!fExecFile.exists() || !fExecFile.isFile()) {
			DebugUtil
					.adminDebugWarn("NmsUtil.getHMScpPsd() no find /HiveManager/encryptscpuser/getScpuserPsd.sh");

			return strPsd;
		}

		// call script to verify the certificate
		String strCmd = "sh " + strPsdExec;

		List<String> strRsltList = BeAdminCentOSTools
				.getOutStreamsExecCmd(strCmd);

		if (null == strRsltList || 0 == strRsltList.size()) {
			DebugUtil
					.adminDebugWarn("NmsUtil.getHMScpPsd() no return could not charge");

			return strPsd;
		}

		strPsd = strRsltList.get(0);

		return strPsd;
	}

	/**
	 * Get the inner version of HiveManager
	 *
	 *@return null if there is something wrong
	 */
	public static String getInnerVersion() {
		FileManager fileM = FileManager.getInstance();
		try {
			String[] version = fileM.readFile(AhDirTools.getHmRoot()
					+ "WEB-INF/hmconf/.inner.ver");
			if (null != version && version.length == 1) {
				return version[0];
			}
		} catch (Exception ex) {
			DebugUtil.commonDebugWarn(
					"NmsUtil.getInnerVersion() catch exception", ex);
		}
		return null;
	}

	public static BeVersionInfo getVersionInfo() {
		return getVersionInfo(System.getenv("HM_ROOT")
				+ "/WEB-INF/hmconf/hivemanager.ver");
	}
	
	public static String getHMCurrentVersion() {
		BeVersionInfo verInfo = getVersionInfo();
		String strMainVersion = verInfo.getMainVersion();
		String strSubVersion = verInfo.getSubVersion();
		return strMainVersion + "." + strSubVersion + ".0";
	}

	/**
	 * If this system is a hosted hm application
	 *
	 *@return boolean
	 */
	public static boolean isHostedHMApplication() {
		return isHHMApp;
	}

	public static boolean isPlanner() {
		return isPlanner;
	}

	public static boolean isDemoHHM() {
		return isDemo;
	}

	public static String getGmAPIKey() {
		return gmAPIKey;
	}

	public static String getGmLicenseKey() {
		return gmLicenseKey;
	}
	
	public static boolean isEnableYUICDN() {
        return enableYUICDN;
    }

    /**
	 * check whether support http connection.
	 *
	 * @return -
	 */
	public static boolean isHTTPEnable() {
		return httpEnable;
	}

	public static BeVersionInfo getVersionInfo(String strFile) {
		BeVersionInfo oVerInfo = new BeVersionInfo();
		File file = new File(strFile);
		if(!file.exists()){
			oVerInfo.setMainVersion("");
			oVerInfo.setSubVersion("");
			oVerInfo.setStatus("");
			oVerInfo.setBuildTime("");
			oVerInfo.setImageUid(0);
			oVerInfo.setTvMainVer("");
			oVerInfo.setTvSubVer("");
		}else{
			try {
				String strMainVersion = FileManager.getInstance().readFile(strFile, "MAINVERSION");
				oVerInfo.setMainVersion(strMainVersion == null ? ""
						: strMainVersion);

				String strSubVersion = FileManager.getInstance().readFile(strFile, "SUBVERSION");
				oVerInfo.setSubVersion(strSubVersion == null ? "" : strSubVersion);

				String strStatus = FileManager.getInstance().readFile(strFile, "STATUS");
				oVerInfo.setStatus(strStatus == null ? "" : strStatus);

				String strBuildTime = FileManager.getInstance().readFile(strFile, "BUILDTIME");
				oVerInfo.setBuildTime(strBuildTime == null ? "" : strBuildTime);

				String strUid = FileManager.getInstance().readFile(strFile, "UID");
				oVerInfo.setImageUid(strUid == null ? 0 : Integer.parseInt(strUid));

				String strTvMainVer = FileManager.getInstance().readFile(strFile, "TVMAINVER");
				oVerInfo.setTvMainVer(strTvMainVer == null ? "" : strTvMainVer);

				String strTvSubVer = FileManager.getInstance().readFile(strFile, "TVSUBVER");
				oVerInfo.setTvSubVer(strTvSubVer == null ? "" : strTvSubVer);


			} catch (Exception e) {
				DebugUtil.commonDebugWarn(
						"NmsUtil.getVersionInfo() catch exception", e);
			}
		}
		return oVerInfo;
	}

	public static String getHiveOSVersion(BeVersionInfo hmVersion) {
		if (null != hmVersion) {
			String ver = hmVersion.getMainVersion();
			String reVer = hmVersion.getSubVersion();
			String end = ".0";
			return ver + "." + reVer + end;
		}
		return null;
	}

	public static String getUserMessage(String code) {
		return MgrUtil.getUserMessage(code);
	}

	public static String getUserMessage(String code, String[] params) {
		return MgrUtil.getUserMessage(code, params);
	}

	public static String getHiveosErrorMessage(int errorCode) {
		String str_code = AhDecoder.bytes2hex(
				AhDecoder.toByteArray(errorCode, 4), true).toLowerCase();
		return getUserMessage("hm.hiveos.code." + str_code);
	}

	/**
	 * This function is used to get the IP address prefix, just like
	 * '192.168.23.5', returned value is '192.168.0.0'
	 *
	 * @param ipAddress
	 *            -
	 * @return -
	 */
	public static String getIpPrefix(String ipAddress) {
		if (null == ipAddress || ipAddress.isEmpty()) {
			return ipAddress;
		}
		int index = ipAddress.indexOf('.');
		if (index > 0) {
			String first = ipAddress.substring(0, index);
			int second_index = ipAddress.indexOf('.', index + 1);
			if (second_index > 0) {
				String second = ipAddress.substring(index + 1, second_index);
				return first + "." + second + ".0.0";
			}
		}
		return ipAddress;
	}

	/**
	 * This function is used to get the net mask string, just like
	 * '16', returned value is '255.255.0.0'
	 *
	 * @param masknum
	 *            -
	 * @return -
	 */
	public static String getNetmask(int masknum) {
		int mask = 0;
		for (int i = 0; i < masknum; i++) {
			mask = mask | (1 << (31 - i));
		}

		StringBuilder stringBuffer = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int value = 0XFF & (mask >> (24 - 8 * i));
			if (0 != i){
				stringBuffer.append(".");
			}
			stringBuffer.append(value);
		}
		return stringBuffer.toString();
	}

	private static final int YEAR_VALUE_IN_SECOND = 3600 * 24 * 365;
	private static final int DAY_VALUE_IN_SECOND = 3600 * 24;
	private static final int HOUR_VALUE_IN_SECOND = 3600;
	private static final int MINUTE_VALUE_IN_SECOND = 60;

	/**
	 * This Method is transform time int value to String value to display
	 *
	 * @param second
	 *            -
	 * @return -
	 */
	public static String transformTime(int second) {
		second = second < 0 ? 0 : second;
		String str_time;
		int int_year = second / YEAR_VALUE_IN_SECOND;
		int remain_days = second % YEAR_VALUE_IN_SECOND;
		int int_day = remain_days / DAY_VALUE_IN_SECOND;
		int remain_hours = remain_days % DAY_VALUE_IN_SECOND;
		int int_hour = remain_hours / HOUR_VALUE_IN_SECOND;
		int remain_minutes = remain_hours % HOUR_VALUE_IN_SECOND;
		int int_min = remain_minutes / MINUTE_VALUE_IN_SECOND;
		int int_sec = remain_minutes % MINUTE_VALUE_IN_SECOND;

		if (second >= YEAR_VALUE_IN_SECOND) {
			str_time = int_year + " Years " + int_day + " Days, " + int_hour
					+ " Hrs " + int_min + " Mins " + int_sec + " Secs";
		} else if (second >= DAY_VALUE_IN_SECOND
				&& second < YEAR_VALUE_IN_SECOND) {
			str_time = int_day + " Days, " + int_hour + " Hrs " + int_min
					+ " Mins " + int_sec + " Secs";
		} else if (second >= HOUR_VALUE_IN_SECOND
				&& second < DAY_VALUE_IN_SECOND) {
			str_time = int_hour + " Hrs " + int_min + " Mins " + int_sec
					+ " Secs";
		} else if (second >= MINUTE_VALUE_IN_SECOND
				&& second < HOUR_VALUE_IN_SECOND) {
			str_time = int_min + " Mins " + int_sec + " Secs";
		} else {
			str_time = int_sec + " Secs";
		}
		return str_time;
	}

	/**
	 * refactor transformTime(). <yizhou>
	 *
	 * @param second -
	 * @return -
	 */
	public static String transformTime_(int second) {
		String _str = "";
		int year = second / YEAR_VALUE_IN_SECOND;
		if (year > 0) {
			_str = year + " years";
		}

		second = second % YEAR_VALUE_IN_SECOND;
		int day = second / DAY_VALUE_IN_SECOND;
		if (day > 0) {
			if (_str.length() > 0) {
				_str = _str + " ";
			}

			_str = _str + day + " days";
		}

		second = second % DAY_VALUE_IN_SECOND;
		int hour = second / HOUR_VALUE_IN_SECOND;
		if (hour > 0) {
			if (_str.length() > 0) {
				_str = _str + " ";
			}

			_str = _str + hour + " hrs";
		}

		second = second % HOUR_VALUE_IN_SECOND;
		int min = second / MINUTE_VALUE_IN_SECOND;
		if (min > 0) {
			if (_str.length() > 0) {
				_str = _str + " ";
			}

			_str = _str + min + " mins";
		}

		second = second % MINUTE_VALUE_IN_SECOND;
		if (second > 0) {
			if (_str.length() > 0) {
				_str = _str + " ";
			}

			_str = _str + second + " secs";
		}

		_str = _str.length() > 0 ? _str : "0 secs";

		return _str;
	}

	public static String getCLIFormatString(int value_seconds) {
		if (value_seconds < 0) {
			return "00:00:00";
		}
		int hour = value_seconds / 3600;
		int minute = value_seconds % 3600 / 60;
		int second = value_seconds % 3600 % 60;

		String str_hour;
		String str_min;
		String str_sec;
		if (hour < 10) {
			str_hour = "0" + hour;
		} else {
			str_hour = String.valueOf(hour);
		}
		if (minute < 10) {
			str_min = "0" + minute;
		} else {
			str_min = String.valueOf(minute);
		}
		if (second < 10) {
			str_sec = "0" + second;
		} else {
			str_sec = String.valueOf(second);
		}
		return str_hour + ":" + str_min + ":" + str_sec;
	}

	public static String generatePassphrase() {
		Random random = new Random();
		StringBuilder passphrase = new StringBuilder();
		int range = AH_LEGAL_PASS_PHRASE_CHARS.length;

		for (int i = 0; i < 16; i++) {
			int randomIndex = random.nextInt(range);
			passphrase.append(AH_LEGAL_PASS_PHRASE_CHARS[randomIndex]);
		}

		return passphrase.toString();
	}

	public static int getNewDtlsKeyId(int currentId) {
		int newId = currentId + 1;
		if (newId > 255) {
			newId = 1;
		}
		return newId;
	}

	/*-
	public static String getHostAddress(String domainName) {
		String hostAddress = domainName;

		if (domainName != null) {
			try {
				log.info("getHostAddress", "# domain name " + domainName);

				InetAddress ia = InetAddress.getByName(domainName.trim());
				hostAddress = ia.getHostAddress();

				log.info("getHostAddress", "# parsed host address " + hostAddress);
			} catch (UnknownHostException uhe) {
				log.error("getHostAddress", "Parsing domain failed.", uhe);
			}
		}

		return hostAddress;
	}*/

	/**
	 *
	 * get the Configured CAPWAP Server of the given HiveAP.
	 *
	 * @param hiveAp
	 *            Object. it must be loaded of capwapIpBind item.
	 * @param isPrimary -
	 * @return -
	 */
	public static String getCapwapServer(HiveAp hiveAp, boolean isPrimary) {
		if (null == hiveAp) {
			throw new IllegalArgumentException("invalid hiveAp object.");
		}
		if (isPrimary) {
			//HM online get from HMServicesSettings
			if (NmsUtil.isHostedHMApplication()){
				HMServicesSettings hmSet = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.domainName", HmDomain.HOME_DOMAIN);
				if (null != hmSet && hmSet.getVirtualHostName() != null && !"".equals(hmSet.getVirtualHostName())) {
					return hmSet.getVirtualHostName();
				}
			}

			IpAddress capwapIp = hiveAp.getCapwapIpBind();
			// get from configured IP;
			if (null != capwapIp) {
				try {
					SingleTableItem item = CLICommonFunc.getIpAddress(capwapIp,
							hiveAp);
					return item.getIpAddress();
				} catch (CreateXMLException e) {
					log.debug("getCapwapServer",
							"no matched item exist in IP Address object.");
				}
			}
			// get from CAPWAP Settings
			List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null,
					null);
			if (!list.isEmpty()) {
				CapwapSettings setting = list.get(0);
				String ip1 = setting.getPrimaryCapwapIP();
				log.info("getCapwapServer", "CAPWAP Settings, primary ip:"
						+ ip1);
				if (null != ip1 && !"".equals(ip1.trim())
						&& !"0.0.0.0".equals(ip1.trim())) {
					return ip1;
				}
			}

			String bindIp = hiveAp.getCapwapLinkIp();
			// get from bind CAPWAP IP;
			if (null != bindIp && !"".equals(bindIp.trim())
					&& !"0.0.0.0".equals(bindIp.trim())) {
				return bindIp;
			}

			// get from default HiveManager IP;
			return HmBeOsUtil.getHiveManagerIPAddr();
		} else {
			IpAddress capwapIp = hiveAp.getCapwapBackupIpBind();
			// get from configured IP;
			if (null != capwapIp) {
				try {
					SingleTableItem item = CLICommonFunc.getIpAddress(capwapIp,
							hiveAp);
					return item.getIpAddress();
				} catch (CreateXMLException e) {
					log
							.debug("getCapwapServer",
									"no matched backup item exist in IP Address object.");
				}
			}
			// get from CAPWAP Settings
			List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null,
					null);
			if (!list.isEmpty()) {
				CapwapSettings setting = list.get(0);
				String ip2 = setting.getBackupCapwapIP();
				log
						.info("getCapwapServer", "CAPWAP Settings, backup ip:"
								+ ip2);
				if (null != ip2 && !"".equals(ip2.trim())
						&& !"0.0.0.0".equals(ip2.trim())) {
					return ip2;
				}
			}

			return null;
		}
	}

	/**
	 *
	 * get the Running CAPWAP server of the given HiveAP.
	 *
	 * @param hiveAp
	 *            -
	 * @return -
	 */
	public static String getRunningCapwapServer(HiveAp hiveAp) {
		if (null == hiveAp) {
			throw new IllegalArgumentException("invalid hiveAp object.");
		}
		String capwapIp = hiveAp.getCapwapLinkIp();
		// get from bind CAPWAP IP;
		if (null != capwapIp && !"".equals(capwapIp.trim())
				&& !"0.0.0.0".equals(capwapIp.trim())) {
			return capwapIp;
		}
		// get from CAPWAP Settings
		List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null, null);
		if (!list.isEmpty()) {
			CapwapSettings setting = list.get(0);
			String ip1 = setting.getPrimaryCapwapIP();
			String ip2 = setting.getBackupCapwapIP();
			log.info("getRunningCapwapServer", "CAPWAP Settings, primary ip:"
					+ ip1 + ", backup ip:" + ip2);
			if (null != ip1 && !"".equals(ip1.trim())
					&& !"0.0.0.0".equals(ip1.trim())) {
				return ip1;
			}
			if (null != ip2 && !"".equals(ip2.trim())
					&& !"0.0.0.0".equals(ip2.trim())) {
				return ip2;
			}
		}
		// get from default HiveManager IP;
		return HmBeOsUtil.getHiveManagerIPAddr();
	}

	/**
	 *
	 * get the Running CAPWAP server of the given SimpleHiveAP.
	 *
	 * @param hiveAp
	 *            -
	 * @return -
	 */
	public static String getRunningCapwapServer(SimpleHiveAp hiveAp) {
		if (null == hiveAp) {
			throw new IllegalArgumentException("invalid hiveAp object.");
		}
		String capwapIp = hiveAp.getCapwapLinkIp();
		// get from bind CAPWAP IP;
		if (null != capwapIp && !"".equals(capwapIp.trim())
				&& !"0.0.0.0".equals(capwapIp.trim())) {
			return capwapIp;
		}
		// get from CAPWAP Settings
		List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null, null);
		if (!list.isEmpty()) {
			CapwapSettings setting = list.get(0);
			String ip1 = setting.getPrimaryCapwapIP();
			String ip2 = setting.getBackupCapwapIP();
			log.info("getRunningCapwapServer", "CAPWAP Settings, primary ip:"
					+ ip1 + ", backup ip:" + ip2);
			if (null != ip1 && !"".equals(ip1.trim())
					&& !"0.0.0.0".equals(ip1.trim())) {
				return ip1;
			}
			if (null != ip2 && !"".equals(ip2.trim())
					&& !"0.0.0.0".equals(ip2.trim())) {
				return ip2;
			}
		}
		// get from default HiveManager IP;
		return HmBeOsUtil.getHiveManagerIPAddr();
	}

	/**
	 * <p>
	 * Check if the port specified is occupied by some certain process/socket in
	 * the current system.
	 * </p>
	 *
	 * @param port
	 *            the number of port to be checked for.
	 * @return <tt>true</tt> if the port given is occupied by a certain
	 *         process/socket, <tt>false</tt> otherwise.
	 * @throws IOException If an I/O error occurs.
	 * @throws InterruptedException if the current thread is interrupted by another thread while it is waiting for the result.
	 */
	public static boolean checkPortOccupancy(int port) throws IOException, InterruptedException {
		boolean occupied = false;
		String cmdForWindows = "netstat -ano|findstr \":" + port + "\\>\"";
		String cmdForLinux = "netstat -anp|grep \":" + port + " \"";
		String os = System.getProperty("os.name");
		String[] cmdArray = os.toLowerCase().contains("windows") ? new String[] {
				"cmd.exe", "/C", cmdForWindows }
				: new String[] { "bash", "-c", cmdForLinux };
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		BufferedReader reader = null;
		log.info("checkPortOccupancy", "Executing netstat cmd: " + cmdArray[0]
				+ " " + cmdArray[1] + " " + cmdArray[2]);

		try {
			proc = runtime.exec(cmdArray);
			int exitValue = proc.waitFor();
			log.info("checkPortOccupancy",
					"Netstat cmd was executed and exit value related to the process was "
							+ exitValue);

			if (exitValue == 0) {
				InputStream input = proc.getInputStream();

				if (input != null) {
					reader = new BufferedReader(new InputStreamReader(input));
					String readLine = reader.readLine();
					occupied = readLine != null;
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					log.error("checkPortOccupancy", "I/O Error in closing BufferedReader", ioe);
				}
			}

			if (proc != null) {
				proc.destroy();
			}
		}

		return occupied;
	}

	public static List<Integer> checkUsingPorts() throws IOException {
		String cmdForWindows = "netstat -ano";
		String cmdForLinux = "netstat -anp";
		String os = System.getProperty("os.name");
		String[] cmdArray = os.toLowerCase().contains("windows") ? new String[] {
				"cmd.exe", "/C", cmdForWindows }
				: new String[] { "bash", "-c", cmdForLinux };
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		BufferedReader reader = null;
		log.info("checkUsingPorts", "Executing netstat cmd: " + cmdArray[0]
				+ " " + cmdArray[1] + " " + cmdArray[2]);

		try {
			proc = runtime.exec(cmdArray);
			InputStream in = proc.getInputStream();

			if (in == null) {
				throw new IOException("Could not get the input stream from Process when executing " + cmdArray[0]+ " " + cmdArray[1] + " " + cmdArray[2]);
			}

			reader = new BufferedReader(new InputStreamReader(in));
			List<Integer> usingPorts = new ArrayList<Integer>();
			String rawLine;

			while ((rawLine = reader.readLine()) != null) {
				rawLine = rawLine.trim().toUpperCase();

				if (rawLine.startsWith("TCP") || rawLine.startsWith("UDP")) {
					for (StringTokenizer token = new StringTokenizer(rawLine, " "); token.hasMoreTokens();) {
						String field = token.nextToken();

						// The string token firstly containing the character of ":" is the "Local Address"
						// field and the sub-string behind the last ":" is the port number being used.
						int lastColonIndex = field.lastIndexOf(":");

						if (lastColonIndex != -1) {
							String strPort = null;

							try {
								strPort = field.substring(lastColonIndex + 1);
								int usingPort = Integer.parseInt(strPort);

								if (!usingPorts.contains(usingPort)) {
									usingPorts.add(usingPort);
								}
							} catch (NumberFormatException nfe) {
								log.error("checkUsingPorts", strPort + " is not a numeric.", nfe);
							}

							break;
						}
					}
				}
			}

			Collections.sort(usingPorts);

			return usingPorts;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					log.error("checkUsingPorts", "I/O Error in closing BufferedReader", ioe);
				}
			}

			if (proc != null) {
				proc.destroy();
			}
		}
	}

	/*
	 * @description: check if need show the info about restore data
	 *
	 * @author Lanbao
	 *
	 * @Input: null
	 *
	 * @return: boolean yes: means need; false:do not need;
	 */
	public static boolean isShowUpdateLog(HmDomain oDomain) {
		String strFile = "/HiveManager/downloads/"+oDomain.getDomainName()+"/.need_show_logs";

		File oFile = new File(strFile);

		if (!oFile.exists() || !oFile.isFile()) {
			return false;
		}

		// check the database if have the data for updatelog.
		List<HmUpgradeLog> listData = QueryUtil.executeQuery(HmUpgradeLog.class, null,
				null,oDomain.getId());

		return !listData.isEmpty();
	}

	/*
	 * @description: clean the flag file
	 *
	 * @author Lanbao
	 *
	 * @Input: null
	 *
	 * @return: null
	 */
	public static void clearShowUpdateLogFlag(HmDomain oDomain) {
		String strFile = "/HiveManager/downloads/"+oDomain.getDomainName()+"/.need_show_logs";

		File oFile = new File(strFile);

		if (oFile.exists() && oFile.isFile()) {
			if (!oFile.delete()) {
				oFile.delete();
			}
		}
	}

	public synchronized static int getDebugGroupId() {
		int value = debugGroupId.getAndIncrement();
		if (value == Integer.MAX_VALUE) {
			debugGroupId.set(1);
		}
		return value;
	}

	public static int compareSoftwareVersion(String ver1, String ver2) {
		if (ver1 == null || ver2 == null) {
			return 0;
		}

		int diffValue = 0;

		for (StringTokenizer st1 = new StringTokenizer(ver1, "."), st2 = new StringTokenizer(
				ver2, "."); st1.hasMoreTokens() && st2.hasMoreTokens();) {
			diffValue = Integer.valueOf(st1.nextToken())
					- Integer.valueOf(st2.nextToken());

			if (diffValue != 0) {
				break;
			}
		}

		return diffValue;
	}

	static final char[]	str	= { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9' };
	
	static final char[]	strUpper = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	
	static final char[]	strNumber = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * generate random string base on given string length
	 *
	 * @param len -
	 * @return -
	 */
	public static String genRandomString(int len) {
		StringBuilder result = new StringBuilder("");
		Random r = new Random();

		int count = 0;
		while (count < len) {
			int i = Math.abs(r.nextInt(str.length - 1));

			if (i >= 0 && i < str.length) {
				result.append(str[i]);
				count++;
			}
		}

		return result.toString();
	}
	
	
	/**
	 * generate random string base on given string length
	 *
	 * @param len -
	 * @return -
	 */
	public static String genRandomStringAdvanced(int len) {
		StringBuilder result = new StringBuilder("");
		Random r = new Random();

		boolean hasUpCase = false;
		boolean hasNumber = false;
		// must include at least one number and one uppercase character
		while (!hasUpCase || !hasNumber) {
			result = new StringBuilder("");
			int count = 0;
			while (count < len) {
				int i = Math.abs(r.nextInt(str.length - 1));

				if (i >= 0 && i < str.length) {
					if (!hasUpCase) {
						hasUpCase = strUpper.toString().contains(String.valueOf(str[i]));
					}
					if (!hasNumber) {
						hasNumber = strNumber.toString().contains(String.valueOf(str[i]));
					}
					result.append(str[i]);
					count++;
				}
			}
		}
		return result.toString();
	}

	public static boolean isHmInService() {
		HAMonitor haMonitor = HAUtil.getHAMonitor();
		HAStatus haStatus = haMonitor.getCurrentStatus();
		int status = haStatus.getStatus();

		return status == HAStatus.STATUS_STAND_ALONG || status == HAStatus.STATUS_HA_MASTER;
	}

	/**
	 * check is the IP address is local
	 *
	 * @param ip	the given IpAddress object
	 * @return		true if the IP address is local, false if not
	 * @author Joseph Chen
	 */
	public static boolean isLocalAddress(IpAddress ip) {
		if(ip == null) {
			return false;
		}

		/*
		 * get local ip address
		 */
		Set<String> localIps = getHMAddress();

		if(localIps == null) {
			return false;
		}

		/*
		 * compare
		 */
		for(SingleTableItem item : ip.getItems()) {
			if(item == null) {
				continue;
			}

			if(localIps.contains(item.getIpAddress())) {
				return true;
			}
		}

		return false;
	}

	private static Set<String> getHMAddress() {
		Set<String> ipSet = new HashSet<String>();

		/*
		 * get IP address from eth0/eth1
		 */
		NetConfigImplInterface networkService;

		if (AhAppContainer.HmBe == null) {
			String os = System.getProperty("os.name");
			networkService = os.toLowerCase().contains("windows") ? new WindowsNetConfigImpl() : new LinuxNetConfigImpl();
		} else {
			networkService = AhAppContainer.HmBe.getOsModule().getNetworkService();
		}

		String eth0 = networkService.getIP_eth0();

		if (eth0 != null && !eth0.equals("")) {
			ipSet.add(eth0);
		}

		String eth1 = networkService.getIP_eth1();

		if (eth1 != null && !eth1.equals("")) {
			ipSet.add(eth1);
		}

		/*
		 * get IP address from CAPWAP setting
		 */
		List<?> capwaps = QueryUtil.executeQuery("select bo.primaryCapwapIP, bo.backupCapwapIP from " + CapwapSettings.class.getSimpleName() + " bo",
													null, null);

		if(capwaps != null) {
			for(Object obj : capwaps) {
				if(obj == null) {
					continue;
				}

				Object[] list = (Object[])obj;

				if(list[0] != null) {
					if(!"".equals(list[0]))
						ipSet.add((String)list[0]);
				}

				if(list[1] != null) {
					if(!"".equals(list[1]))
						ipSet.add((String)list[1]);
				}
			}
		}

		/*
		 * get IP address from HiveAP table
		 */
		List<?> hiveaps = QueryUtil.executeQuery("select bo.capwapLinkIp from " + HiveAp.class.getSimpleName() + " bo",
				null,
				new FilterParams("connected", true));

		if(hiveaps != null) {
			for(Object obj : hiveaps) {
				if(obj == null) {
					continue;
				}

				if(!"".equals(obj))
					ipSet.add((String)obj);
			}
		}

		return ipSet;
	}

//	/**
//	 * get the DNS name of Portal
//	 *
//	 * @return -
//	 * @author Joseph Chen
//	 */
//	public static String getPortalDNSName() {
//		return PORTAL_DNS_NAME;
//	}

	/**
	 * get the e-mail address of Aerohive Support Center
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static String getSupportMail() {
		return SUPPORT_MAIL_ADDRESS;
	}

	/**
	 * Check software updates exist in license server
	 *
	 *@return boolean
	 */
	public static boolean existSoftUpdate() {
		LicenseServerSetting lsSet = HmBeActivationUtil.getLicenseServerInfo();
		// get new version flag
		ActivationKeyOperation.getNewVersionFlag(lsSet);
		try {
			QueryUtil.updateBo(lsSet);
		} catch (Exception ex) {
			log.error("existSoftUpdate", "Update license server setting Error.", ex);
		}
		return lsSet.isAvailableSoftToUpdate();
	}

	/**
	 * charge the proxy for http
	 *
	 * @return boolean true
	 * @author Lanbao xiao
	 */
	public static boolean isHttpProxy()
	{
//		String strGmDir = "/var/www/html/gm";
//		String strHttpdConf = "/etc/httpd/conf/httpd.conf";

//		File fGmDir = new File(strGmDir);
//		File fHttpdConf = new File(strHttpdConf);

//		return fGmDir.exists() && fGmDir.isDirectory() && fHttpdConf.exists() && fHttpdConf.isFile();
//		return fHttpdConf.exists() && fHttpdConf.isFile();
		return false;
	}

	/**
	 * The function will return the web application url String with http (not https)
	 *
	 * @param request -
	 * @return -
	 */
	public static String getWebAppHttpUrl(HttpServletRequest request){
		String httpUrl;
		if(useHttpProxy){
			// with proxy setting, the url will be hard coded
			httpUrl = "http://"+request.getServerName()+":8080"+request.getContextPath()+"/";
		}else{
			// rewrite by the request information
			if("https".equals(request.getScheme())){
				if(request.getServerPort() == 8443){
					httpUrl = "http://"+request.getServerName()+":8080"+request.getContextPath()+"/";
				}else{
					httpUrl = "http://"+request.getServerName()+request.getContextPath()+"/";
				}
			}else{
				httpUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
			}
		}
		log.info("getWebAppHttpUrl", httpUrl);
		return httpUrl;
	}


	/**
	 *The function will return the number of admin therads
	 *@return the num for threads
	 */
	public static int getAdminThreadsNum()
	{
		try
		{
			return Integer.parseInt(ADMIN_THREADS_NUM);
		}
		catch(Exception ex)
		{
			log.info(ex.getMessage());

			return 5;
		}
	}

	/**
	 * is debug model
	 *
	 * @return -
	 */
	public static boolean isDebugModel() {
		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_APPLICATION_SOFTWARE_MODEL, ConfigUtil.VALUE_SOFTWARE_MODEL_RELEASE)
				.equalsIgnoreCase(ConfigUtil.VALUE_SOFTWARE_MODEL_DEBUG);
	}

	/**
	 * Get the url of CAS Client (local machine)
	 *
	 *@return String
	 */
	public static String getCasClient() {
		String casClient = CasTool.getCasClient();
		/*
		 * if casClient is null, return the IP address of HM
		 */
		return casClient != null ? casClient
				: "https://" + HmBeOsUtil.getHiveManagerIPAddr();
	}

	/**
	 * Get the URL of Auth Server.
	 *
	 * @return the URL of Auth Server.
	 */
	public static String getAuthServiceURL() {
		if (authServiceURL == null) {
			authServiceURL = CasTool.getCasServer();
		}

		return authServiceURL + "/cas";
	}

	/**
	 * Get the URL of MyHive.
	 *
	 * @return the URL of MyHive.
	 */
	public static String getMyHiveServiceURL() {
		if (myHiveServiceURL == null) {
			myHiveServiceURL = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_SUPPORT,
				ConfigUtil.KEY_MYHIVE_URL, "https://changeme.aerohive.com:443");
		}
		return myHiveServiceURL;
	}

	/**
	 * Get the URL of Portal.
	 *
	 *@return the URL of Portal.
	 */
	public static String getPortalServiceURL() {
		// get the value from database
		List<?> portalUrls = QueryUtil.executeQuery("SELECT primaryCapwapIP FROM " + CapwapClient.class.getSimpleName(), null, new FilterParams("serverType", CapwapClient.SERVERTYPE_PORTAL), (Long) null, 1);
		
		// HMOL use the value in database
		if (isHostedHMApplication()) {
		// On-Premise HM
		} else {
			// check beta flag
			List<?> betaFlags = QueryUtil.executeQuery("SELECT enabledBetaIDM FROM " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), (Long) null, 1);
			boolean useBeta = false;
			if (!betaFlags.isEmpty()) {
				useBeta = (Boolean)betaFlags.get(0);
			}
			if (useBeta) {
				return "https://" + PORTAL_URL_FOR_ONPREMISE_HM_BETA + ":443";
			}
		}
		String portalStr = PORTAL_URL_FOR_ONPREMISE_HM;
		// the value exists in database
		if (!portalUrls.isEmpty()) {
			// the value is not '' or null
			if (!StringUtils.isBlank((String)portalUrls.get(0))) {
				portalStr = (String)portalUrls.get(0);
			}
		}
		return "https://" + portalStr + ":443";
	}

	/**
	 * Get the URL of Redirector.
	 *
	 * @return the URL of Redirector
	 */
	public static String getRedirectorServiceURL() {
		if (redirectorServiceURL == null) {
			redirectorServiceURL = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_SUPPORT,
					ConfigUtil.KEY_REDIRECTOR_URL, "https://changeme.aerohive.com:443/staging");
		}
		return redirectorServiceURL;
	}

	/**
	 * check if it is default ECWP server
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static boolean isEcwpDefault() {
		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_ECWPSERVER,
				ConfigUtil.VALUE_ECWP_DEFAULT).equalsIgnoreCase(ConfigUtil.VALUE_ECWP_DEFAULT);
	}

	/**
	 * check if it is DePaul ECWP server
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static boolean isEcwpDepaul() {
		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_ECWPSERVER,
				ConfigUtil.VALUE_ECWP_DEFAULT).equalsIgnoreCase(ConfigUtil.VALUE_ECWP_DEPAUL);
	}

	/**
	 * check if it is DePaul ECWP server
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static boolean isEcwpNnu() {
		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_ECWPSERVER,
				ConfigUtil.VALUE_ECWP_DEFAULT).equalsIgnoreCase(ConfigUtil.VALUE_ECWP_NNU);
	}

	public static String getHiveApMacOuis() {
		StringBuilder result = new StringBuilder();
		String[] macs = getHiveApMacOui();
		if (macs.length > 0) {
			result.append(macs[0]);
			for (int i = 1; i < macs.length; i++) {
				result.append(",");
				result.append(macs[i]);
			}
		}
		return result.toString();
	}
	public static String[] getHiveApMacOui() {
		return getOEMCustomer().getMACOUI();
	}

	public static boolean isAhMacOui(String mac) {
		if (mac == null || mac.length() < 6) return false;

		for (String str : getOEMCustomer().getMACOUI()) {
			if (mac.toUpperCase().startsWith(str.toUpperCase())) return true;
		}
		return false;
	}

	/*
	 * get simulate flag by ap name
	 */
	public static boolean isSimulatedHiveAPByApName(String apName) {
		if(null == apName)
			return false;
		int index = apName.indexOf("SIMU-");
		return index == 0;
	}
	/*
	 * get simulate flag by ap mac
	 */
	public static boolean isSimulatedHiveAPByApMac(String apMac) {
		if(null == apMac)
			return false;
		if(apMac.length() != 12)
			return false;

		try {
			int macNumber = Integer.parseInt(apMac.substring(6), 16);
			return macNumber < 80000;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return the redirect port of the web server.
	 */
	public static int getWebServerRedirectPort() {
		return webServerRedirectPort;
	}

	/**
	 * @return the authentication method used for logging in the web server.
	 */
	public static String getWebServerLoginAuthMethod() {
		return webServerLoginAuthMethod;
	}

	public static boolean isGlobalTeacherViewEnabled() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT enableteacher FROM hmservicessettings s, hm_domain d ");
		sql.append("WHERE s.owner=d.id AND d.domainname=\'")
			.append(HmDomain.HOME_DOMAIN).append("\'");

		List<?> results = QueryUtil.executeNativeQuery(sql.toString());

		return !results.isEmpty() && (Boolean) results.get(0);
	}

	public static boolean isTeacherViewEnabled(HmUser userContext) {
		if(!isGlobalTeacherViewEnabled()) {
			return false;
		}

		HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class,
				"owner", userContext.getOwner());

		return settings.isEnableTeacher() && NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED;
	}
	
	public static boolean isOpenDNSEnabled(HmDomain domain) {

		HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class,
				"owner", domain);

		return settings.isEnableOpenDNS();
	}

	public static boolean isProduction() {
		return isHHMApp && !isPlanner && !isDemo;
	}

	public static OEMCustomer getOEMCustomer() {

		if(oemCustomer != null) {
			return oemCustomer;
		}

		oemCustomer = new OEMCustomer();
		String path = AhDirTools.getHmRoot() + "resources" + File.separator + OEM_RESOURCE_FILE_NAME;

		InputStream in;

		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			setDefaultOEM();
			return oemCustomer;
		}

		Properties properties = new Properties();

		try {
			properties.load(in);
		} catch (IOException e) {
			setDefaultOEM();
			log.error("Failed to load the OEM customer from properties file.", e);
			return oemCustomer;
		}
		
		oemCustomer.setBlackboxFlag(properties.getProperty("blackbox", "false"));

		/*
		 * company name
		 */
		oemCustomer.setCompanyName(properties.getProperty("company_name", "Aerohive"));

		/*
		 * company full name
		 */
		oemCustomer.setCompanyFullName(properties.getProperty("company_full_name", "Aerohive Networks, Inc."));

		/*
		 * abbreviation of company name
		 */
		oemCustomer.setCompanyAbbreviation(properties.getProperty("company_name_abbreviation", "AH"));

		/*
		 * home page
		 */
		oemCustomer.setHomePage(properties.getProperty("home_page", "http://www.aerohive.com"));

		/*
		 * access point name
		 */
		oemCustomer.setAccessPonitName(properties.getProperty("access_point_name", "Device"));

		/*
		 * access point name HiveBR
		 */
		oemCustomer.setAccessPonitNameBR(properties.getProperty("access_point_name_br", "Routers"));

		/*
		 * access point name CVG
		 */
		oemCustomer.setAccessPonitNameCVG(properties.getProperty("access_point_name_cvg", "HiveOS Virtual Appliance"));

		/*
		 * NMS Name
		 */
		oemCustomer.setNmsName(properties.getProperty("nms_name", "HiveManager"));

		/*
		 * NMS Name abbreviation
		 */
		oemCustomer.setNmsNameAbbreviation(properties.getProperty("nms_name_abbreviation", "HM"));

		/*
		 * access point OS
		 */
		oemCustomer.setAccessPointOS(properties.getProperty("access_point_os", "HiveOS"));

		/*
		 * wireless network unit
		 */
		oemCustomer.setWirelessUnitName(properties.getProperty("wireless_unit_name", "Hive"));

		/*
		 * password for HM super user
		 */
		oemCustomer.setDefaultHMPassword(properties.getProperty("hm_root_password", "aerohive"));

		/*
		 * password for AP super user
		 */
		oemCustomer.setDefaultAPPassword(properties.getProperty("ap_root_password", "aerohive"));

		/*
		 * password for AP access console
		 */
		oemCustomer.setDefaultAccessConsolePassword(properties.getProperty("access_console_password", "aerohive"));

		/*
		 * SNMP contact
		 */
		oemCustomer.setSNMPContact(properties.getProperty("snmp_contact", "admin@aerohive.com"));

		/*
		 * MAC OUI
		 */
		oemCustomer.setMACOUI(properties.getProperty("mac_oui", "001977").split(","));

		/*
		 * support mail address
		 */
		oemCustomer.setSupportMail(properties.getProperty("support_mail_address", "inside-sales@aerohive.com"));

		/*
		 * sales mail address
		 */
		oemCustomer.setSalesMail(properties.getProperty("sales_mail_address", "sales@aerohive.com"));

		/*
		 * orders mail address
		 */
		oemCustomer.setOrdersMail(properties.getProperty("orders_mail_address", "orders@aerohive.com"));

		/*
		 * help link
		 */
		oemCustomer.setHelpLink(properties.getProperty("help_link", "http://www.aerohive.com/330000/docs/help/english/3.5r3"));

		/*
		 * company address
		 */
		oemCustomer.setCompanyAddress(properties.getProperty("company_address", "330 Gibraltar Drive, Sunnyvale CA 94089"));

		/*
		 * register URL
		 */
		oemCustomer.setRegisterUrl(properties.getProperty("register_url", "http://www.aerohive.com/register/"));

		/*
		 * copyright
		 */
		oemCustomer.setNmsCopyright(properties.getProperty("nms_copyright", getCopyrightDate()));

		/*
		 * express mode enable
		 */
		oemCustomer.setExpressModeEnable(Boolean.parseBoolean(properties.getProperty("express_mode_enable", "true")));

		/*
		 * ap series
		 */
		String apSeriesString = properties.getProperty("ap_series", "");
		setApSeries(apSeriesString);

		/*
		 * hm model number
		 */
		String hmModelNumber = properties.getProperty("hm_model_number", "");
		setHMModelNumber(hmModelNumber);

		/*
		 * lowest ap version
		 */
		oemCustomer.setApLowestVersion(properties.getProperty("ap_lowest_version", "3.5r3"));

		/*
		 * default license server url
		 */
		oemCustomer.setDefaultLsUrl(properties.getProperty("default_license_server", LicenseServerSetting.DEFAULT_LICENSE_SERVER_URL));

		oemCustomer.setDnsSuffix(properties.getProperty("dns_suffix", "aerohive.com"));

		oemCustomer.setTellMeMoreIDM(properties.getProperty("tell_me_more"));
		
	    /*
         * support phone numbers
         */
        oemCustomer.setSupportPhoneNumber(properties.getProperty("support_phone_number", "+1-408-510-6100"));
        oemCustomer.setSupportPhoneNumberUS(properties.getProperty("support_phone_number_us", "1-866-365-9918"));

		return oemCustomer;
	}

	private static void setApSeries(String apSeriesString) {
		if(apSeriesString == null || apSeriesString.trim().length() == 0) {
			return ;
		}

		/*
		 * format: HiveAP120<->LWN602A series, HiveAP340<->LWN602HA series
		 */
		String[] segments = apSeriesString.split(",");

		if(segments == null || segments.length == 0) {
			return ;
		}

		for(String segment : segments) {
			String[] apSegs = segment.split("<->");

			if(apSegs == null || apSegs.length != 2) {
				continue;
			}

			oemCustomer.addApSeries(apSegs[0].trim(), apSegs[1].trim());
		}
	}

	private static void setHMModelNumber(String modelString) {
		if(modelString == null || modelString.trim().length() == 0) {
			return ;
		}

		/*
		 * format: APP_1U<->LWN600MA, APP_2U<->LWN601MA
		 */
		String[] segments = modelString.split(",");

		if(segments == null || segments.length == 0) {
			return ;
		}

		for(String segment : segments) {
			String[] apSegs = segment.split("<->");

			if(apSegs == null || apSegs.length != 2) {
				continue;
			}

			oemCustomer.addHMModelNumber(apSegs[0].trim(), apSegs[1].trim());
		}
	}

	private static void setDefaultOEM() {
		/*
		 * company name
		 */
		oemCustomer.setCompanyName("Aerohive");

		/*
		 * company name
		 */
		oemCustomer.setCompanyFullName("Aerohive Networks Inc.");

		/*
		 * abbreviation of company name
		 */
		oemCustomer.setCompanyAbbreviation("AH");

		/*
		 * home page
		 */
		oemCustomer.setHomePage("http://www.aerohive.com");

		/*
		 * access point name
		 */
		oemCustomer.setAccessPonitName("Device");

		/*
		 * NMS Name
		 */
		oemCustomer.setNmsName("HiveManager");

		/*
		 * NMS Name abbreviation
		 */
		oemCustomer.setNmsNameAbbreviation("HM");

		/*
		 * access point OS
		 */
		oemCustomer.setAccessPointOS("HiveOS");

		/*
		 * wireless network unit
		 */
		oemCustomer.setWirelessUnitName("Hive");

		/*
		 * password for HM super user
		 */
		oemCustomer.setDefaultHMPassword("aerohive");

		/*
		 * password for AP super user
		 */
		oemCustomer.setDefaultAPPassword("aerohive");

		/*
		 * password for AP access console
		 */
		oemCustomer.setDefaultAccessConsolePassword("aerohive");

		/*
		 * SNMP contact
		 */
		oemCustomer.setSNMPContact("admin@aerohive.com");

		/*
		 * MAC OUI
		 */
		oemCustomer.setMACOUI(new String[] { "001977", "4018B1", "E01C41", "08EA44" });

		/*
		 * support mail address
		 */
		oemCustomer.setSupportMail("inside-sales@aerohive.com");

		/*
		 * sales mail address
		 */
		oemCustomer.setSalesMail("sales@aerohive.com");

		/*
		 * orders mail address
		 */
		oemCustomer.setOrdersMail("orders@aerohive.com");

		/*
		 * help link
		 */
		oemCustomer.setHelpLink("http://www.aerohive.com/330000/docs/help/english/3.5r3");

		/*
		 * company address
		 */
		oemCustomer.setCompanyAddress("330 Gibraltar Drive, Sunnyvale CA 94089");

		/*
		 * register URL
		 */
		oemCustomer.setRegisterUrl("http://www.aerohive.com/register/");

		/*
		 * copyright
		 */
		oemCustomer.setNmsCopyright(getCopyrightDate());

		oemCustomer.setExpressModeEnable(true);

		/*
		 * license server
		 */
		oemCustomer.setDefaultLsUrl(LicenseServerSetting.DEFAULT_LICENSE_SERVER_URL);

		oemCustomer.setDnsSuffix("aerohive.com");
	}

	/**
	 * To check if the current NMS is of OEM version
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static boolean isHMForOEM() {
//		return !(NmsUtil.getOEMCustomer().getCompanyName().toLowerCase().contains("aerohive")
//				&& NmsUtil.getOEMCustomer().getHomePage().toLowerCase().contains("aerohive"));
		return NmsUtil.getOEMCustomer().getBlackboxFlag().equalsIgnoreCase("true");
	}

	public static EnumItem[] filterHiveAPModel(EnumItem[] enumModel, boolean isEasyMode){
		EnumItem[] modeEnum = enumModel;
		modeEnum = filterHiveAPModelForEasyMode(modeEnum, isEasyMode);
		modeEnum = filterHiveAPModelForOEM(modeEnum);
		Arrays.sort(modeEnum, new Comparator<EnumItem>() {
			@Override
			public int compare(EnumItem o1, EnumItem o2) {
				String value1 = o1.getValue() == null ? "" : o1.getValue();
				String value2 = o2.getValue() == null ? "" : o2.getValue();
				return value1.compareTo(value2);
			}
		});
		return modeEnum;
	}
	
	public static String filterHiveAPModelString(EnumItem[] enumModel, boolean isEasyMode){
		StringBuffer bf = new StringBuffer();
		EnumItem[] modeEnum = enumModel;
		modeEnum = filterHiveAPModelForEasyMode(modeEnum, isEasyMode);
		modeEnum = filterHiveAPModelForOEM(modeEnum);
		Arrays.sort(modeEnum, new Comparator<EnumItem>() {
			@Override
			public int compare(EnumItem o1, EnumItem o2) {
				String value1 = o1.getValue() == null ? "" : o1.getValue();
				String value2 = o2.getValue() == null ? "" : o2.getValue();
				return value1.compareTo(value2);
			}
		});
		for(EnumItem item: modeEnum) {
			if (HiveAp.isCVGAppliance((short)item.getKey())) {
				continue;
			}
			if (bf.length()>0) {
				bf.append(", ");
			}
			bf.append(item.getValue());
		}
		return bf.toString();
	}

	public static EnumItem[] filterHiveAPModelPlanning(EnumItem[] enumModel,
			boolean isEasyMode) {
		EnumItem[] modeEnum = enumModel;
		modeEnum = filterHiveAPModelForEasyMode(modeEnum, isEasyMode);
		modeEnum = filterHiveAPModelForOEM(modeEnum);
		return modeEnum;
	}

	public static EnumItem[] filterHiveAPModelForEasyMode(EnumItem[] enumModel, boolean isEasyMode){
		if(isEasyMode){
			List<EnumItem> enumList = new ArrayList<EnumItem>();
			for (EnumItem model : enumModel) {
				if (AhConstantUtil.isTrueAll(Device.SUPPORTED_HM_EXPRESS, (short) model.getKey())) {
					enumList.add(model);
				}
			}

			EnumItem[] resEnums = new EnumItem[enumList.size()];
			int index=0;
			for(EnumItem enumIte : enumList){
				resEnums[index] = enumIte;
				index++;
			}
			return resEnums;
		}else{
			return enumModel;
		}

	}

	public static EnumItem[] filterHiveAPModelForOEM(EnumItem[] enumModel){
		if(!NmsUtil.isHMForOEM()){
			return enumModel;
		}

		Set<Integer> filterSet = new HashSet<Integer>();
		for (EnumItem model : enumModel) {
			filterSet.add(model.getKey());
		}

		Map<String, String> apModelMap = NmsUtil.getOEMCustomer().getApSeries();
		if(apModelMap == null || apModelMap.isEmpty()){
			return enumModel;
		}
		for(String keyStr : apModelMap.keySet()){
			short modelKey=-1;
			if("HiveAP28".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_28;
			}else if("HiveAP20".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_20;
			}else if("HiveAP320".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_320;
			}else if("HiveAP340".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_340;
			}else if("HiveAP380".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_380;
			}else if("HiveAP330".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_330;
			}else if("HiveAP350".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_350;
			}else if("HiveAP370".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_370;
			}else if("HiveAP390".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_390;
			}else if("HiveAP120".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_120;
			}else if("HiveAP110".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_110;
			}else if("HiveAP121".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_121;
			}else if("HiveAP141".equalsIgnoreCase(keyStr)){
				modelKey = HiveAp.HIVEAP_MODEL_141;
			}
			filterSet.remove((int)modelKey);
		}

		List<EnumItem> enumList = new ArrayList<EnumItem>();
		for (EnumItem model : enumModel) {
			if (!filterSet.contains(model.getKey())) {
				enumList.add(model);
			}
		}
		EnumItem[] resEnums = new EnumItem[enumList.size()];
		int index=0;
		for(EnumItem enumIte : enumList){
			resEnums[index] = enumIte;
			index++;
		}
		return resEnums;
	}

	public static boolean isHiveApADServer(HiveAp hiveAp) throws AhConfigRetrievedException{
		if(hiveAp == null){
			throw new AhConfigRetrievedException(NmsUtil.getOEMCustomer().getAccessPonitName()+" is null");
		}

		if(hiveAp.getIpAddress() == null || "".equals(hiveAp.getIpAddress())){
			throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
					"error.hiveApNoIp", hiveAp.getHostName()));
		}

		if (hiveAp.isSimulated()) {
			throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
					"warn.simulated.ap.config.fetch.nonsupport", "show running config"));
		}

		String cliResult;
		String showCfgCmd = AhCliFactory.showRunningConfig(hiveAp.getSoftVer(), true);
		BeCliEvent fetchCfgReq = new BeCliEvent();
		int sequenceNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		fetchCfgReq.setAp(hiveAp);
		fetchCfgReq.setClis(new String[] { showCfgCmd });
		fetchCfgReq.setSequenceNum(sequenceNum);

		try {
			fetchCfgReq.buildPacket();
		} catch (Exception e) {
			log.error("fetchConfig",
					"Failed to build request to fetch running config from "+NmsUtil.getOEMCustomer().getAccessPonitName()+" " + hiveAp, e);
			throw new AhConfigRetrievedException(MgrUtil.getUserMessage(
					"Failed to build request to fetch running config from "+NmsUtil.getOEMCustomer().getAccessPonitName()));
		}

		BeCommunicationEvent fetchConfigResp = AhAppContainer.getBeCommunicationModule()
			.sendSyncRequest(fetchCfgReq, 105);

		if (fetchConfigResp == null) {
			throw new AhConfigRetrievedException(MgrUtil.getUserMessage("error.config.fetch.failed"));
		}

		int respMsgType = fetchConfigResp.getMsgType();

		switch (respMsgType) {
		case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
			try {
				fetchConfigResp.parsePacket();
			} catch (Exception e) {
				throw new AhConfigRetrievedException(
						"Failed to parse the response of running config retrieval for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" " + hiveAp);
			}

			BeCapwapCliResultEvent cliRetEvent = (BeCapwapCliResultEvent) fetchConfigResp;

			if (!cliRetEvent.isCliSuccessful()) {
				String errorCli = cliRetEvent.getErrorCli();
				log.error("fetchConfig", "Failed to fetch running config from HiveAP " + hiveAp
						+ " using CLI: " + errorCli);
				throw new AhConfigRetrievedException(
						"Failed to fetch running config from "+NmsUtil.getOEMCustomer().getAccessPonitName()+" " + hiveAp + " using CLI: " + errorCli);
			}

			cliResult = cliRetEvent.getCliSucceedMessage();
			break;
		case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
		default:
			throw new AhConfigRetrievedException(
					"Failed to fetch running config from "+NmsUtil.getOEMCustomer().getAccessPonitName()+" " + hiveAp);
		}

		if(cliResult == null || "".equals(cliResult)){
			throw new AhConfigRetrievedException(
					"Failed to fetch running config from "+NmsUtil.getOEMCustomer().getAccessPonitName()+" " + hiveAp);
		}

		String regex = "aaa radius-server local db-type active-directory (primary|backup1|backup2|backup3)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cliResult);
		return matcher.matches();
	}

	public static void test() {
		/*
		 * test all fields of OEMCustomer which should be read from the properties file.
		 */
		OEMCustomer oem = NmsUtil.getOEMCustomer();

		Field[] fields = OEMCustomer.class.getDeclaredFields();

		try {
			for (Field field : fields) {
				field.setAccessible(true);
				String value = (String)field.get(oem);
				System.out.println(field.getName() + ": \t" + value);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * send an email to the HA email user, default email address is <b>'techops_mon@aerohive.com'</b>
	 *
	 * @author Yunzhi Lin
	 * - Time: Jan 13, 2011 5:03:47 PM
	 * @param subject -
	 * @param mailContent -
	 */
	public static void sendMailToAdminUser(String subject ,String mailContent){
//		if (!NmsUtil.isHostedHMApplication()){
//			log.warn("it isn't an online HM, no need to send mail!");
//			return;
//		}
		log.info("---------start send email to admin user---------------");
		//get the home domain of the MailNotification
		List<MailNotification> mailNotification = QueryUtil.executeQuery(
				MailNotification.class, null, null, BoMgmt.getDomainMgmt().getHomeDomain().getId());

		if (null == mailNotification || mailNotification.isEmpty()) {
			log.error("sendMailToAdminUser", "Unable to get the mail server setting. Please configure it.");
		}else{
			MailNotification notification = mailNotification.get(0);
			String serverName = notification.getServerName();
			String mailFrom = notification.getMailFrom();
			if(StringUtils.isBlank(serverName) || StringUtils.isBlank(mailFrom)){
				log.error("sendMailToAdminUser",
						"The mail server setting is incorrect. Please check settings - serverName:"+serverName+" mailFrom:"+mailFrom);
				return;
			}

			String superUserEmail;
			if (NmsUtil.isHostedHMApplication()) {
				// get the HA Notify Email
				List<?> nativeQueryResult = QueryUtil.executeNativeQuery("select hanotifyemail from ha_settings");
				if(null == nativeQueryResult || nativeQueryResult.isEmpty()){
					log.error("sendMailToAdminUser", "Unable to get the HA Notify email address.");
					return;
				}
				superUserEmail = nativeQueryResult.get(0).toString();
			} else {
				if(StringUtils.isBlank(notification.getMailTo())){
					log.error("sendMailToAdminUser",
							"The mail server setting is incorrect. Please check settings - mailTo:"+notification.getMailTo());
					return;
				}
				superUserEmail = notification.getMailTo();
			}
			SendMailUtil mailUtil = new SendMailUtil(notification);
			mailUtil.setMailTo(superUserEmail);
			mailUtil.setSubject(subject);
			mailUtil.setMailContentType("text/html");
			mailUtil.setText(SendMailUtil.addHeadAndFoot(mailContent));
			mailUtil.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");
			try {
				log.info("will send email to "+superUserEmail+" subject:"+subject+" mailContent:"+mailContent);
				mailUtil.startSend();
				log.info("---------end send email to admin user---------------");
			} catch (Exception e) {
				log.error("sendMailToAdminUser", "Unable to send main to "+superUserEmail,e);
			}
		}
	}

	// get numbers from number range string (1,4-8,7-10 => 1,4,5,6,7,8,9,10)
	public static Short[] getNumbersFromRange(String range) {
		List<Short> numbers = new ArrayList<Short>();
		if (range == null || range.trim().length() == 0)return null;

		String[] strs = range.split(",");
		for (String str : strs) {
			if (str.trim().length() == 0) {
				return null;
			}

			String[] nums = str.split("-");
			short min;
			short max = 0;
			if (nums.length == 1) {
				if (nums[0].trim().length() == 0 || !nums[0].equals(str))return null;
			} else if (nums.length != 2 || nums[0].trim().length() == 0 || nums[1].trim().length() == 0) {
				return null;
			}

			try {
				min = Short.valueOf(nums[0].trim());
				if (nums.length > 1)max = Short.valueOf(nums[1].trim());
			} catch (NumberFormatException e) {
				log.error("string does not have the appropriate format", e);
				return null;
			}
			if (nums.length == 1)max = min;
			if (min > max || min == 0 || max == 0)return null;

			for (short i = 0; min + i <= max; i++) {
				if (!numbers.contains(Short.valueOf((short)(min + i)))) {
					numbers.add((short) (min + i));
				}
			}
		}

		return numbers.toArray(new Short[numbers.size()]);
	}

	/**
	 * add quote marks around the source string if the string contains blank space
	 * @param source -
	 * @return if the given string contains black spaces, return "source"; else,
	 * 			return source itself
	 */
	public static String handleBlank(String source) {
		return source.indexOf(' ') == -1
			? source : "\"" + source + "\"";

	}

	public static boolean isValidSerialNumber(String serialNumber) {
		if (serialNumber == null || serialNumber.trim().isEmpty()) {
			return false;
		}

		String regex = "\\d{14}";

		return Pattern.matches(regex, serialNumber);
	}

	/**
	 * <p>Checks if a String is not empty (""), not null , not "null"(ignore case), not "12 3", and not whitespace only.</p>
	 *
	 * <pre>
	 * NmsUtil.isNotBlankId("123")     = true<br>
     * NmsUtil.isNotBlankId(null)      = false
     * NmsUtil.isNotBlankId("null")    = false
     * NmsUtil.isNotBlankId("NULL")    = false
     * NmsUtil.isNotBlankId("NuLL")    = false
     * NmsUtil.isNotBlankId("")        = false
     * NmsUtil.isNotBlankId(" ")       = false
     * NmsUtil.isNotBlankId("bob")     = false
     * NmsUtil.isNotBlankId("  bob  ") = false
     * NmsUtil.isNotBlankId("  bob  ") = false
     * NmsUtil.isNotBlankId("12 3")    = false
     * NmsUtil.isNotBlankId("ab2c")    = false
     * NmsUtil.isNotBlankId("12-3")    = false
     * NmsUtil.isNotBlankId("12.3")    = false
     * </pre>
	 * @author Yunzhi Lin
	 * - Time: Aug 31, 2011 3:05:00 PM
	 * @param idStr The string to check
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean isNotBlankId(String idStr) {
		return !(StringUtils.isBlank(idStr) || idStr.equalsIgnoreCase("null")) && StringUtils.isNumeric(idStr);
	}

	/**
	 * @param path: resource file path
	 * @param key: the key of resource
	 * @author wpliang
	 *
	 * @return the value of key in resource
	 */
	public static String getText(String path,String key){
		Locale locale = Locale.getDefault();
		ResourceBundle localResource = ResourceBundle.getBundle(path, locale);
	    //ResourceBundle localResource = ResourceBundle.getBundle("/resources/hmResources", locale);
		return localResource.getString(key);
	}

	/**
	 * Check if function HM Search is enabled or not
	 * @return -
	 */
	public static boolean isSearchEnabled() {
		return !NmsUtil.isHostedHMApplication();
	}

	public static boolean isTempReadAccessPermission(short accessMode, Long authorizationEndDate) {
		if (HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_R == accessMode ||
				HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_RW == accessMode) {
			if (System.currentTimeMillis() < authorizationEndDate) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasReadAccessPermission(short accessMode, Long authorizationEndDate) {
		if (HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_R == accessMode ||
				HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_RW == accessMode) {
//			Calendar cal = Calendar.getInstance();
			if (System.currentTimeMillis() < authorizationEndDate) {
				return true;
			}
		} else if (HmDomain.ACCESS_MODE_TECH_OP_PARTNER_R == accessMode ||
				HmDomain.ACCESS_MODE_TECH_OP_PARTNER_RW == accessMode) {
			return true;
		}

		return false;
	}

	public static boolean hasRwAccessPermission(short accessMode, Long authorizationEndDate) {
		if (HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_RW == accessMode) {
			if (System.currentTimeMillis() < authorizationEndDate) {
				return true;
			}
		} else if (HmDomain.ACCESS_MODE_TECH_OP_PARTNER_RW == accessMode) {
			return true;
		}

		return false;
	}

	/**
	 * read File by line
	 * if File is not exist,return null
	 *
	 * @param filePath -
	 * @return List<String>
	 * @throws IOException
	 */
	public static List<String> readFileByLines(String filePath)
		throws IOException {
		List<String> result = null;
		File file = new File(filePath);
		if (!file.exists()) {
			return result;
		}
		FileReader fr = null;
		BufferedReader reader = null;
		try {
			fr = new FileReader(file);
			reader = new BufferedReader(fr);
			String tempString;
			result = new ArrayList<String>();
			while ((tempString = reader.readLine()) != null) {
				result.add(tempString);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("readFileByLines", "IO Close Error.", e);
				}
			}

			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					log.error("readFileByLines", "IO Close Error.", e);
				}
			}
		}
		return result;
	}

	/**
	 * get file "os_dhcp_fingerprints.txt" version
	 * In "os_dhcp_fingerprints.txt", the first line must be 'Version=0.1'
	 * if "os_dhcp_fingerprints.txt" not exist return 0.1
	 *
	 * @param filePath -
	 * @return -
	 * @throws IOException
	 */
	public static String getOSOptionFileVersion(String filePath) throws IOException {
		String result = "0.1";
		List<String> lines = readFileByLines(filePath);
		if (lines != null && !lines.isEmpty()) {
			String line = lines.get(0);
			if (line != null && line.startsWith(ImportTextFileAction.VERSION_STR)) {
				result = line.substring(line.indexOf(ImportTextFileAction.VERSION_STR)
						+ ImportTextFileAction.VERSION_STR.length());
			}
		}
		return result;
	}

	/**
	 * get the value which defined in the property file
	 * @param the key of the property
	 * @return the value
	 */
	public static String getConfigProperty(String key){
		return System.getProperty(key);
	}

	public static boolean isVhmEnableIdm(Long domainId){
		try{
			return new HmCloudAuthCertMgmtImpl().isIDManagerEnabled(domainId);
		}catch(Exception e){
			return false;
		}
	}

    public static String changeNumberSequence2NumberSestion(String numberSeq) {
        final String regex_comma = ",", regex_oneSpace = " ", regex_catenation = "-";
        if(StringUtils.isNotBlank(numberSeq)) {
            String[] numberStrs = numberSeq.split(regex_comma);
            if(null != numberStrs) {
                List<Integer> numberSeqList = new ArrayList<>();
                List<String> numberStrSectionList = new ArrayList<>();
                for (String numberStr : numberStrs) {
                    if(NumberUtils.isNumber(numberStr.trim())) {
                        Integer number = Integer.valueOf(numberStr.trim());
                        if(!numberSeqList.contains(number)) {
                            numberSeqList.add(number);
                        }
                    }
                }
                if(!numberSeqList.isEmpty()) {
                    final int size = numberSeqList.size();
                    if(size == 1) {
                        return numberSeqList.get(0).toString();
                    } else {
                        Collections.sort(numberSeqList);
                        int start = 0, end = 0;
                        for (int i = 0; i < size; i++) {
                            int cur = numberSeqList.get(i).intValue();
                            if(end == 0) {
                                start = cur;
                            }
                            int next = i+1 >= size ? 0 : numberSeqList.get(i+1).intValue();
                            if(cur+1 == next) {
                                end = next;
                            } else {
                                numberStrSectionList.add(start + (end==0 ? "": (start + 1 == end ? regex_comma+regex_oneSpace:regex_catenation)+end));
                                end = 0;
                            }
                        }
                        String[] numberSectionArray = numberStrSectionList.toArray(new String[0]);
                        return StringUtils.join(numberSectionArray, regex_comma+regex_oneSpace);
                    }
                }
            }
        }
        return null;
    }

    public static long getSysTotalMemory() {
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osmb.getTotalPhysicalMemorySize() / 1024/1024;
    }
    /**
     * @description convert the mcs to the new format, MCS12 - MCS4/2 for 11n( Interval is 8 )
     * @param mcsRate,interval
     * @return
     * @huihe
     */
    public static String mcsFormatConvert(int mcsRate, int interval){
    	if(interval <= 0 || mcsRate < 0){
    		return null;
    	}
    	int stream = mcsRate/interval;
    	int rate = mcsRate - interval*stream;
    	return "mcs" + rate + "/" + (stream + 1);
    }
    
    public static DeviceInfo getDeviceInfo(short hiveApModel){
    	DeviceInfo deviceInfo = new DeviceInfo(hiveApModel);
		DeviceObj property = DevicePropertyManage.getInstance().getDeviceProperty(hiveApModel);
		DevicePropertyManage.getInstance().clone(property, deviceInfo);
		deviceInfo.init();
		return deviceInfo;
    }
    
    public static String convertSqlStr(String str){
    	if (str==null) {
    		return "";
    	}
    	return str.replace("\\", "\\\\\\\\").replace("'", "''");
    }

    public static ImageInfo getImageInfoFromFile(String imageFile) {
		if (!(new File(imageFile).exists()))
			return null;

    	String[] exeCmds = new String[]{"bash", "-c", ""};
    	exeCmds[2] = "od -S13 -N200 " + imageFile;

    	InputStream inputStream = null;
    	BufferedReader bufferedReader = null;
    	ImageInfo imageInfo = null;
    	Process process = null;

    	try {
			process = Runtime.getRuntime().exec(exeCmds);
			inputStream = process.getInputStream();

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 2048);

			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				if (imageInfo == null)
					imageInfo = new ImageInfo();

				if (line.contains("Type:")) {
					String type = line.substring(line.indexOf("Type:") + "Type:".length(), line.lastIndexOf("$")).trim();
					imageInfo.setType(type);
				} else if (line.contains("Reversion:")) {
					String reversion = line.substring(line.indexOf("Reversion:") + "Reversion:".length(), line.lastIndexOf("$")).trim();
					imageInfo.setReversion(reversion);
				} else if (line.contains("DATE:")) {
					String date = line.substring(line.indexOf("DATE:") + "DATE:".length(), line.lastIndexOf("$")).trim();
					imageInfo.setDate(date);
				} else if (line.contains("Size:")) {
					String size = line.substring(line.indexOf("Size:") + "Size:".length(), line.lastIndexOf("$")).trim();
					imageInfo.setSize(size);
				}
			}

			if (imageInfo != null)
				imageInfo.getTargetNameByTypeAndVersion();

		} catch (IOException e) {
			log.error("get the image info of file: " + imageFile + " failed.", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ioe) {
					log.error("close buffered reader failed.", ioe);
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					log.error("close input stream failed.", ioe);
				}
			}

			if (process != null) {
				process.destroy();
			}
		}

    	return imageInfo;
    }
    
    /**
     * @description format a long number to a string split by comma
     * Example:1234567890 -> 1,234,567,890; 123456789->123,456,789
     * @param number
     * @return String
     * @author Wpliang
     *
     */
	public static String formatNumberByComma(long number){
		String strNum = String.valueOf(number);
		String result = strNum;
		int n = strNum.length() % 3;
		if(n == 0){
			result = strNum.replaceAll("(\\d{3})", ",$1").substring(1);
		} else {
			result = strNum.substring(0, n)+strNum.substring(n).replaceAll("(\\d{3})", ",$1");
		}
		
		return result;
	}
    
    public static String convertIDs2IDStr(ArrayList<Long> ids){
    	StringBuffer idStr = new StringBuffer();
    	for (int i = 0; i < ids.size(); i++) {
    		idStr.append(ids.get(i));
    		if(i != ids.size() -1){
    			idStr.append(",");
    		}
    	}
    	return idStr.toString();
    }
    
    //get USB information from XML.
    public static List<USBModemProfile> getUSBModemInfo(){
    	List<USBModemProfile> usbInfos = new ArrayList<>();
    	
    	try{
			String cfgPath = AhDirTools.getConstantConfigDir() + "hiveos_usb_modem_config.xml";
			Document usbDoc = CLICommonFunc.readXml(cfgPath);
			List<?> nodeList = usbDoc.selectNodes("/hiveos-modem-support-list/modems/modem");
			if(nodeList != null){
				for(Object node : nodeList){
					Element eleObj = (Element)node;
					
					USBModemProfile usbProfile = new USBModemProfile();
					usbProfile.setModemName(eleObj.attributeValue("id"));
					
					Node disNode = eleObj.selectSingleNode("display");
					if(disNode != null){
						usbProfile.setDisplayName( ((Element)disNode).attributeValue("name") );
					}
					
					Node apnNode = eleObj.selectSingleNode("connect/apn");
					if(apnNode != null){
						usbProfile.setApn( ((Element)apnNode).attributeValue("value") );
					}
					
					Node dialstringNode = eleObj.selectSingleNode("connect/dialstring");
					if(dialstringNode != null){
						usbProfile.setDialupNum( ((Element)dialstringNode).attributeValue("value") );
					}
					
					Node usernameNode = eleObj.selectSingleNode("connect/user-auth/username");
					if(usernameNode != null){
						usbProfile.setUserId( ((Element)usernameNode).attributeValue("value") );
					}
					
					Node passwordNode = eleObj.selectSingleNode("connect/user-auth/password");
					if(passwordNode != null){
						usbProfile.setPassword( ((Element)passwordNode).attributeValue("value") );
					}
					
					Node versionNode = eleObj.selectSingleNode("hiveos-version");
					if(versionNode != null){
						usbProfile.setOsVersion(((Element)versionNode).attributeValue("min") + ".0");
					}
					
					usbInfos.add(usbProfile);
				}
				
				//add for BR200_LTE
				USBModemProfile usb8 = new USBModemProfile();
				usb8.setDisplayName("Verizon Embedded LTE");
				usb8.setModemName("novatel_E362");
				usb8.setOsVersion("6.0.1.0");
				usb8.setCellularMode(USBModemProfile.CELLULAR_MODE_AUTO);
				usbInfos.add(usb8);
			}
		}catch(Throwable t){
			log.error("Init USB modem failed.", t);
		}
    	
    	return usbInfos;
    }
    
	public static String getCopyrightDate(){
		return "Copyright (C) 2006-" + HmBeOsUtil.getServerTime().get(Calendar.YEAR);
	}
	
	public static String convertNumToEnglish(int count,boolean firstFlag){
		if(count > -1 && count < 10){
			 String[] numsAry = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
			 if(firstFlag){
				 return numsAry[count].substring(0,1).toUpperCase() + numsAry[count].substring(1);
			 }else{
				 return numsAry[count];
			 }
		}else{
			return String.valueOf(count);
		}
	}
}