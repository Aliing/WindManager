/**
 *@filename		AhCliFactory.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.impl.VPNProfileImpl;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhReport;
import com.ah.ui.actions.hiveap.HiveApUpdateAction;
import com.ah.upload.UploadHandler;
import com.ah.upload.handler.hiveap.VpnReportUploadHandler;
import com.ah.util.HibernateUtil;
import com.ah.util.MgrUtil;
import com.ah.util.NameValuePair;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public final class AhCliFactory {

	private static final int DEFAULT_SSH_PORT = 22;

	// private static final int DEFAULT_HTTPS_PORT = 443;

	private static final String AH_CLI_NO_PROMPT = " no-prompt";

	private static final String FILE_UPLOAD_SERVLET_URL_PATTERN = "/upload";

	private static final String AH_CLI_SUFFIX = "\n";

	/**
	 * This method is just only used for windows system. You may establish a SCP
	 * daemon on windows system through 'Cygwin' or installing 'freeSSHd'
	 * directly. After installation, you need to put the 'downloads' directory
	 * including its sub directories into the root directory where the 'OpenSSH'
	 * or 'freeSHHd' is installed. Besides these, an environment variable and
	 * two property values in the hmConfig.properties are needed to be set and
	 * modified separately as well.
	 * 
	 * <tt>Environment variable</tt> Environment name: HM_CITADEL Environment
	 * value: C:\Program Files\OpenSSH (root directory of the SCP daemon
	 * installed.)
	 * 
	 * <tt>hmConfig.properties</tt> hm.scp.username=administrator (username for
	 * windows login.) hm.scp.password=****** (password for windows login.)
	 * 
	 * @param path
	 *            unprocessed path.
	 * @return processed path used for assembling file upload CLIs.
	 */
	private static String adjustPath(String path) {
		return adjustPath(path, true);
	}

	private static String adjustPath(String path, boolean includingHmHome) {
		String adjustedPath = path;

		if (path != null
				&& (!includingHmHome || System.getProperty("os.name")
						.toLowerCase().contains("windows"))) {
			String hmHome = AhDirTools.getHmHome();
			int pos = hmHome.endsWith(File.separator) ? hmHome.length() - 1
					: hmHome.length();
			adjustedPath = path.substring(pos).replace("\\", "/");
		}

		return adjustedPath;
	}

	private static String getSshComponent(String userName, String host,
			String path, String fileName) {
		StringBuilder buf = new StringBuilder(" scp://").append(userName)
				.append("@").append(host);
		int port = AhAppContainer.getBeAdminModule().getSshdPort();

		if (port != DEFAULT_SSH_PORT) {
			buf.append(":").append(port);
		}

		String adjustedPath = adjustPath(path);

		if (adjustedPath == null || adjustedPath.isEmpty()) {
			adjustedPath = ":";
		} else {
			if (!adjustedPath.startsWith(":")) {
				adjustedPath = ":" + adjustedPath;
			}

			if (!adjustedPath.endsWith("/")) {
				adjustedPath += "/";
			}
		}

		buf.append(adjustedPath).append(fileName);

		return addQuoteTwoSide(buf.toString());
	}

	private static String getTftpComponent(String host, String path,
			String fileName) {
		String adjustedPath = path;

		if (adjustedPath == null || adjustedPath.isEmpty()) {
			adjustedPath = ":";
		} else {
			if (!adjustedPath.startsWith(":")) {
				adjustedPath = ":" + adjustedPath;
			}

			if (!adjustedPath.endsWith("/")) {
				adjustedPath += "/";
			}
		}

		return new StringBuilder(" tftp://").append(host).append(adjustedPath)
				.append(fileName).toString();
	}

	private static StringBuilder getHttpUrlComponent(String host,
			boolean requiringQuote) {
		StringBuilder httpUrlBuf = new StringBuilder(" ");

		if (requiringQuote) {
			httpUrlBuf.append("\"");
		}

		httpUrlBuf.append("https://").append(host);

		// int port = NmsUtil.getWebServerRedirectPort();
		//
		// if (port != DEFAULT_HTTPS_PORT) {
		// httpUrlBuf.append(":").append(port);
		// }

		// Append HM deployment directory.
		String hmRoot = AhDirTools.getHmRoot();
		String catalinaHome = System.getenv("CATALINA_HOME");

		if (catalinaHome != null) {
			if (!catalinaHome.endsWith(File.separator)) {
				catalinaHome += File.separator;
			}

			// The HM deployment directory is placed under $HM_ROOT/webapps/
			String webappsDir = catalinaHome + "webapps";
			String hmDeployDir = hmRoot.substring(webappsDir.length());

			// "ROOT" is a special directory that should be excluded from the
			// URL.
			if (hmDeployDir.startsWith(File.separator + "ROOT")) {
				hmDeployDir = hmDeployDir.substring((File.separator + "ROOT")
						.length());
			}

			hmDeployDir = hmDeployDir.replace("\\", "/");

			if (!hmDeployDir.endsWith("/")) {
				hmDeployDir += "/";
			}

			httpUrlBuf.append(hmDeployDir);
		} else {
			httpUrlBuf.append("/hm/");
		}

		return httpUrlBuf;
	}

	private static String getHttpUploadUrl(String host, String paramPairs) {
		StringBuilder httpUrlBuf = getHttpUrlComponent(host, true);

		// Append file upload servlet url pattern.
		if (FILE_UPLOAD_SERVLET_URL_PATTERN.startsWith("/")) {
			httpUrlBuf.append(FILE_UPLOAD_SERVLET_URL_PATTERN.substring(1));
		} else {
			httpUrlBuf.append(FILE_UPLOAD_SERVLET_URL_PATTERN);
		}

		// Append parameter pairs.
		if (paramPairs != null && !paramPairs.trim().isEmpty()) {
			if (!paramPairs.startsWith("?")) {
				httpUrlBuf.append("?");
			}

			httpUrlBuf.append(paramPairs);
		}

		httpUrlBuf.append("\"");

		return httpUrlBuf.toString();
	}

	private static String getHttpUploadUrl(String host,
			Map<String, String> paramPairMap) {
		StringBuilder paramPairBuf = new StringBuilder();

		if (paramPairMap != null) {
			for (Iterator<String> paramNameIter = paramPairMap.keySet()
					.iterator(); paramNameIter.hasNext();) {
				String paramName = paramNameIter.next();
				String paramValue = paramPairMap.get(paramName);
				paramPairBuf.append(paramName).append("=").append(paramValue);

				if (paramNameIter.hasNext()) {
					paramPairBuf.append("&");
				}
			}
		}

		String paramPairs = paramPairBuf.toString();

		return getHttpUploadUrl(host, paramPairs);
	}
	
	private static String addQuoteTwoSide(String string){
		if(string == null){
			return string;
		}
		string = string.trim();
		if(string.contains(" ")){
			string =  "\"" + string + "\"";
		}
		return " " + string;
	}

	private static String getHttpDownloadUrl(String host, String path,
			String filename) {
		StringBuilder httpUrlBuf = getHttpUrlComponent(host, false);

		// Overall resources are placed under $HM_ROOT/res
		httpUrlBuf.append("res/");

		// Remove the $HM_HOME from the path.
		String adjustedPath = adjustPath(path, false);

		if (adjustedPath != null && !adjustedPath.isEmpty()) {
			// Remove the "downloads" from the path.
			if (adjustedPath.startsWith("/downloads/")) {
				adjustedPath = adjustedPath.substring("/downloads/".length());
			} else if (adjustedPath.startsWith("/downloads")) {
				adjustedPath = adjustedPath.substring("/downloads".length());
			}

			if (!adjustedPath.isEmpty()) {
				if (!adjustedPath.endsWith("/")) {
					adjustedPath += "/";
				}

				httpUrlBuf.append(adjustedPath);
			}
		}

		httpUrlBuf.append(filename);

		return addQuoteTwoSide(httpUrlBuf.toString());
	}

	public static String getImageDSUrl(short hiveApModel, String softVer) {
		StringBuilder httpUrlBuf = new StringBuilder();
		httpUrlBuf.append("https://");
		httpUrlBuf.append(MgrUtil.getDownloadServerHost());
		httpUrlBuf.append("/ds/downimage.action");

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("deviceType", String
				.valueOf(hiveApModel)));
		parameters.add(new NameValuePair("osVersion", softVer));

		if (!parameters.isEmpty()) {
			httpUrlBuf.append("?");
		}

		for (NameValuePair pair : parameters) {
			if (httpUrlBuf.toString().endsWith("?")
					|| httpUrlBuf.toString().endsWith("&")) {

			} else {
				httpUrlBuf.append("&");
			}
			httpUrlBuf.append(pair.getName());
			httpUrlBuf.append("=");
			httpUrlBuf.append(pair.getValue());
		}

		return "\"" + httpUrlBuf.toString() + "\"";
	}

	public static String getImageDSUrl(String imageName) {
		StringBuilder httpUrlBuf = new StringBuilder();
		httpUrlBuf.append("https://");
		httpUrlBuf.append(MgrUtil.getDownloadServerHost());
		httpUrlBuf.append("/ds/downimage.action");

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("imageName", imageName));

		if (!parameters.isEmpty()) {
			httpUrlBuf.append("?");
		}

		for (NameValuePair pair : parameters) {
			if (httpUrlBuf.toString().endsWith("?")
					|| httpUrlBuf.toString().endsWith("&")) {

			} else {
				httpUrlBuf.append("&");
			}
			httpUrlBuf.append(pair.getName());
			httpUrlBuf.append("=");
			httpUrlBuf.append(pair.getValue());
		}

		return "\"" + httpUrlBuf.toString() + "\"";
	}

	public static String getConfigDSUrl(Long deviceId, short uploadType,
			int... sequenceNum) {
		StringBuilder httpUrlBuf = new StringBuilder();
		httpUrlBuf.append("https://");
		httpUrlBuf.append(MgrUtil.getDownloadServerHost());
		httpUrlBuf.append("/ds/downcfg.action");

		String dbUrl = HibernateUtil.getConfiguration().getProperty(
				"hibernate.connection.url");
		String hmIp = MgrUtil.getHiveManagerIp();
		dbUrl = dbUrl.toLowerCase();
		dbUrl = dbUrl.replaceAll("localhost", hmIp);
		dbUrl = dbUrl.replaceAll("127.0.0.1", hmIp);

		String dbUserName = HibernateUtil.getConfiguration().getProperty(
				"hibernate.connection.username");
		String dbPassword = HibernateUtil.getConfiguration().getProperty(
				"hibernate.connection.password");
		String passKey = CLICommonFunc.getDBPasswordKey(String.valueOf(deviceId));
		AerohiveEncryptTool enTool = new AerohiveEncryptTool(passKey);
		dbPassword = enTool.encrypt(dbPassword);
		dbPassword = dbPassword.replace("+", "%2B");

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("dbUrl", dbUrl));
		parameters.add(new NameValuePair("userName", dbUserName));
		parameters.add(new NameValuePair("password", dbPassword));
		parameters.add(new NameValuePair("deviceId", String.valueOf(deviceId)));
		parameters.add(new NameValuePair("uploadType", String
				.valueOf(uploadType)));
		parameters.add(new NameValuePair("hmIp", hmIp));
		if (sequenceNum != null && sequenceNum.length > 0) {
			parameters.add(new NameValuePair("cookId", String
					.valueOf(sequenceNum[0])));
		}

		if (!parameters.isEmpty()) {
			httpUrlBuf.append("?");
		}

		for (NameValuePair pair : parameters) {
			if (httpUrlBuf.toString().endsWith("?")
					|| httpUrlBuf.toString().endsWith("&")) {

			} else {
				httpUrlBuf.append("&");
			}
			httpUrlBuf.append(pair.getName());
			httpUrlBuf.append("=");
			httpUrlBuf.append(pair.getValue());
		}

		return "\"" + httpUrlBuf.toString() + "\"";
	}

	private static String getPasswordComponent(String password) {
		return AH_CLI_NO_PROMPT + " _password " + password;
	}

	private static String getHttpAuthComponent(String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		String authMethod = NmsUtil.getWebServerLoginAuthMethod();
		StringBuilder buf = new StringBuilder().append(" admin ")
				.append(webServerLoginUser).append(" password ")
				.append(webServerLoginPwd).append(" ").append(authMethod);

		if (proxy != null && !proxy.trim().isEmpty()) {
			buf.append(" proxy ").append(proxy).append(":").append(proxyPort);

			if (proxyLoginUser != null && !proxyLoginUser.trim().isEmpty()) {
				buf.append(" proxy-admin ").append(proxyLoginUser)
						.append(" password ").append(proxyLoginPwd);
			}
		}

		return buf.toString();
	}

	public static String getClearCollectFileCli(String fileName) {
		StringBuffer buf = new StringBuffer("exec clear data-collection ");

		buf.append(fileName);

		buf.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	public static String getSaveCollectFileViaSSH(String host, String userName,
			String password, String fileName, String saveFileName) {
		StringBuffer buf = new StringBuffer("save data-collect local ");
		String path;

		buf.append(fileName);

		path = AhDirTools.getDataCollectionUploadDir();
		String sshPart = getSshComponent(userName, host, path, saveFileName);
		buf.append(sshPart);

		buf.append(" _password " + password);

		buf.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	public static String getSaveVPNReportFileViaSSH(String host,
			String userName, String password, int period, String path) {
		StringBuffer buf = getVpnReportCli(period);
		if (buf == null)
			return null;

		String sshPart = getSshComponent(userName, host, path, "vpn_report.tgz");
		buf.append(sshPart);

		buf.append(" _password " + password);

		buf.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	private static StringBuffer getVpnReportCli(int period) {
		StringBuffer buf = new StringBuffer();
		switch (period) {
		case AhReport.REPORT_PERIOD_VPN_ONEHOUR:
			buf.append("save high-resolution-vpn-report local vpn_report.tgz");
			break;

		case AhReport.REPORT_PERIOD_VPN_ONEDAY:
		case AhReport.REPORT_PERIOD_VPN_TWODAY:
		case AhReport.REPORT_PERIOD_VPN_THREEDAY:
		case AhReport.REPORT_PERIOD_VPN_ONEWEEK:
		case AhReport.REPORT_PERIOD_VPN_TWOWEEK:
		case AhReport.REPORT_PERIOD_VPN_THREEWEEK:
		case AhReport.REPORT_PERIOD_VPN_ONEMONTH: {
			buf.append("save low-resolution-vpn-report local vpn_report.tgz time-interval ");
			switch (period) {
			case AhReport.REPORT_PERIOD_VPN_ONEDAY:
				buf.append(1);
				break;
			case AhReport.REPORT_PERIOD_VPN_TWODAY:
				buf.append(2);
				break;
			case AhReport.REPORT_PERIOD_VPN_THREEDAY:
				buf.append(3);
				break;
			case AhReport.REPORT_PERIOD_VPN_ONEWEEK:
				buf.append(7);
				break;
			case AhReport.REPORT_PERIOD_VPN_TWOWEEK:
				buf.append(14);
				break;
			case AhReport.REPORT_PERIOD_VPN_THREEWEEK:
				buf.append(21);
				break;
			case AhReport.REPORT_PERIOD_VPN_ONEMONTH:
				buf.append(30);
				break;
			}
		}
			break;

		default:
			return null;
		}

		return buf;
	}

	public static String getSaveVPNReportViaHTTPS(String host, String apMac,
			String userName, String password, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd, int period) {
		StringBuffer buf = getVpnReportCli(period);
		if (buf == null)
			return null;

		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(1);
		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_VPN_REPORT;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));

		// Parameter: Ap Node ID
		paramPairMap.put(UploadHandler.REQ_PARAM_AP_NODE_ID, apMac);

		// Parameter: Period
		paramPairMap.put(VpnReportUploadHandler.REQ_PARAM_REPORT_PERIOD,
				String.valueOf(period));

		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(userName, password, proxy,
				proxyPort, proxyLoginUser, proxyLoginPwd);
		buf.append(httpAuthPart);

		buf.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	public static String getSaveCollectFileViaHTTPS(String host, String apMac,
			String userName, String password, String fileName, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuffer buf = new StringBuffer("save data-collect local ");
		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(1);

		buf.append(fileName + " ");

		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_DATA_COLLECTION;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));

		// Parameter: Ap Node ID
		paramPairMap.put(UploadHandler.REQ_PARAM_AP_NODE_ID, apMac);

		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(userName, password, proxy,
				proxyPort, proxyLoginUser, proxyLoginPwd);
		buf.append(httpAuthPart);

		buf.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	public static String getAppReportViaHTTPS(String host, String apMac,
			String userName, String password, String fileName, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		// need update here, according to HOS
		StringBuffer buf = new StringBuffer("save app-report local ");
		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(1);

		buf.append(fileName + " ");

		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_APPLICATION_REPORT;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));

		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(userName, password, proxy,
				proxyPort, proxyLoginUser, proxyLoginPwd);
		buf.append(httpAuthPart);

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	public static String getNetdumpViaHTTPS(String host, String apMac,
			String userName, String password, String hiveApNodeId, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		// need update here, according to HOS
		StringBuffer buf = new StringBuffer("save _kddr new ");
		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(1);

		//buf.append(fileName + " ");

		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_NETDUMP;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));
		
		paramPairMap.put(UploadHandler.REQ_PARAM_AP_NODE_ID, hiveApNodeId);
		
		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(userName, password, proxy,
				proxyPort, proxyLoginUser, proxyLoginPwd);
		buf.append(httpAuthPart);

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	public static String startAppReportCollectCli(int collectionPeriod,
			int reportPeriod) {
		// need update here
		StringBuffer buf = new StringBuffer(
				"application reporting collection-period ");
		buf.append(String.valueOf(collectionPeriod) + " ");

		buf.append("report-period ");
		buf.append(String.valueOf(reportPeriod));

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String stopAppReportCollectCli() {
		// need update here
		StringBuffer buf = new StringBuffer(
				"application reporting collection-period 3600 report-period 3600");
		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	
	/* L7 Signature based CLI*/
	/**
	 * The CLI for save L7 signatures via SCP
	 * 
	 * @param host
	 * @param signatureName
	 * @param userName
	 * @param password
	 * @param tftp
	 * @param limit
	 * @return
	 */
	public static String downloadSignature(String host, String signatureName,
			String userName, String password, int limit) {
		StringBuilder buf = new StringBuilder("save signature-file ");
		String path;

		// always with SCP
		path = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
		String sshPart = getSshComponent(userName, host, path, signatureName);
		buf.append(sshPart);
		String passwordComponent = getPasswordComponent(password);
		buf.append(passwordComponent);

		if (limit > 0) {
			buf.append(" limit ").append(limit);
		}

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * The CLI for save L7 signatures via HTTPs
	 * 
	 * @param host
	 * @param signatureName
	 * @param webServerLoginUser
	 * @param webServerLoginPwd
	 * @param proxy
	 * @param proxyPort
	 * @param proxyLoginUser
	 * @param proxyLoginPwd
	 * @return
	 */
	public static String downloadSignatureViaHttp(String host, String signatureName,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save signature-file");
		String path = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
		String httpUrl = getHttpDownloadUrl(host, path, signatureName);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	// ***************************************************************
	// Image based CLIs
	// ***************************************************************

	/**
	 * <p>
	 * The CLI of save image.
	 * </p>
	 * <p>
	 * Example : save image scp://admin@10.155.20.7:/usr/hap20-HiveOS.img offset
	 * 00:00:10 no-prompt _password aerohive
	 * </p>
	 * 
	 * @param host
	 *            The host where connect to.
	 * @param imageName
	 *            The name of image to be updated.
	 * @param rebootTime
	 *            The specific time for device reboot.
	 * @param isRelativeTime
	 *            true if using relative time, false otherwise.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @param enableSignature
	 *            Enable the digital signature check.
	 * @param tftp
	 *            indicates if use TFTP or SCP.
	 * @param limit
	 *            Limit the amount of bandwidth used for uploading the image
	 *            file
	 * @return The cli of image update.
	 */
	/* Download image via SSH */
	public static String downloadImage(String host, String imageName,
			String rebootTime, boolean isRelativeTime, String userName,
			String password, boolean enableSignature, boolean tftp, int limit) {
		StringBuilder buf = new StringBuilder("save image ");
		String path;

		// if(useDs){
		// path = this.
		// }else
		if (tftp) {
			path = AhDirTools.getExtenalImageDir();
			String tftpComponent = getTftpComponent(host, path, imageName);
			buf.append(tftpComponent);
		} else {
			path = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
			String sshPart = getSshComponent(userName, host, path, imageName);
			buf.append(sshPart);
		}

		if (rebootTime != null && !rebootTime.isEmpty()) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		if (!enableSignature) {
			buf.append(" no-signature-check ");
		}

		if (tftp) {
			buf.append(AH_CLI_NO_PROMPT);
		} else {
			String passwordComponent = getPasswordComponent(password);
			buf.append(passwordComponent);
		}

		if (limit > 0) {
			buf.append(" limit ").append(limit);
		}

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download image via HTTPS */
	public static String downloadImageViaHttp(String host, String imageName,
			String rebootTime, boolean isRelativeTime,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save image");
		String path = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
		String httpUrl = getHttpDownloadUrl(host, path, imageName);
		buf.append(httpUrl);

		if (rebootTime != null && !rebootTime.isEmpty()) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String downloadImageViaDS(short hiveApModel, String softVer,
			String rebootTime, boolean isRelativeTime,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save image ");
		String httpUrl = getImageDSUrl(hiveApModel, softVer);
		buf.append(httpUrl);

		if (rebootTime != null && !rebootTime.isEmpty()) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String downloadImageViaDS(String imageName,
			String rebootTime, boolean isRelativeTime,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save image ");
		String httpUrl = getImageDSUrl(imageName);
		buf.append(httpUrl);

		if (rebootTime != null && !rebootTime.isEmpty()) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	// ***************************************************************
	// Config based CLIs
	// ***************************************************************

	/**
	 * <p>
	 * The CLI of upload running config.
	 * </p>
	 * <p>
	 * Example : save config current
	 * scp://root@10.155.20.63:/home/1234567890abcdef_run.script _password
	 * aerohive
	 * </p>
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            The host into which the running config is uploaded.
	 * @param scriptName
	 *            The name is used for the running config to be uploaded.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of upload running config.
	 */
	public static String uploadRunningConfig(String domainName, String host,
			String scriptName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save config current");
		String path = AhDirTools.getRunConfigDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, scriptName);
		buf.append(sshComponent).append(" _password ").append(password)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * <p>
	 * The CLI of upload config.
	 * </p>
	 * <p>
	 * Example : save config scp://root@1.1.1.1:/11111111111111 current now
	 * no-prompt _password aerohive
	 * </p>
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            The host where connect to.
	 * @param script
	 *            The script name.
	 * @param rebootTime
	 *            The specific time for device reboot.
	 * @param isRelativeTime
	 *            true if using relative time, false otherwise.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of script update.
	 */
	/* Download config via SSH */
	public static String downloadConfig(String domainName, String host,
			String script, String rebootTime, boolean isRelativeTime,
			String userName, String password) {
		StringBuilder buf = new StringBuilder("save config");
		String path = AhDirTools.getNewConfigDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, script);
		buf.append(sshComponent).append(" current");

		if (rebootTime != null && !rebootTime.equals("")) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		String passwordComponent = getPasswordComponent(password);
		buf.append(passwordComponent).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download config via HTTPS */
	public static String downloadConfigViaHttp(HiveAp hiveAp, String host,
			String script, String rebootTime, boolean isRelativeTime,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd, 
			boolean enableDS) {
		String domainName = hiveAp.getOwner().getDomainName();
		StringBuilder buf = new StringBuilder("save config ");
		String path = AhDirTools.getNewConfigDir(domainName);
		String httpUrl;
		if (enableDS) {
			httpUrl = getConfigDSUrl(hiveAp.getId(),
					UpdateParameters.UPLOAD_TYPE_COMPLETE_CONFIG);
		} else {
			httpUrl = getHttpDownloadUrl(host, path, script);
		}

		buf.append(httpUrl).append(" current");

		if (rebootTime != null && !rebootTime.equals("")) {
			if (isRelativeTime) {
				buf.append(" offset ").append(rebootTime);
			} else {
				buf.append(" ").append(rebootTime);
			}
		}

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download config from Download Server */
	public static String downloadConfigFromDS(HiveAp hiveAp, short uploadType,
			int sequenceNum) {
		String httpUrl = getConfigDSUrl(hiveAp.getId(), uploadType, sequenceNum);
		if (httpUrl != null && !"".equals(httpUrl)) {
			return "show all running-config " + httpUrl + AH_CLI_SUFFIX;
		} else {
			return null;
		}
	}

	/* Download user config via SSH */
	public static String downloadPsk(String domainName, String host,
			String script, String userName, String password) {
		StringBuilder buf = new StringBuilder("save users");
		String path = AhDirTools.getNewConfigDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, script);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download user config via HTTPS */
	public static String downloadPskViaHttp(HiveAp hiveAp, String host,
			String script, String webServerLoginUser, String webServerLoginPwd,
			String proxy, int proxyPort, String proxyLoginUser,
			String proxyLoginPwd, boolean enableDS) {
		StringBuilder buf = new StringBuilder("save users ");
		String domainName = hiveAp.getOwner().getDomainName();
		String path = AhDirTools.getNewConfigDir(domainName);
		String httpUrl;
		if (enableDS) {
			httpUrl = getConfigDSUrl(hiveAp.getId(),
					UpdateParameters.UPLOAD_TYPE_COMPLETE_USER);
		} else {
			httpUrl = getHttpDownloadUrl(host, path, script);
		}
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * <p>
	 * The CLI of upload bootstrap.
	 * </p>
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            The host where connect to.
	 * @param fileName
	 *            The name of bootstrap.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of boot strap update.
	 */
	/* Download bootstrap via SSH */
	public static String downloadBootstrap(String domainName, String host,
			String fileName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save config");
		String path = AhDirTools.getBootstrapConfigDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, fileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(" bootstrap").append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download bootstrap via HTTPS */
	public static String downloadBootstrapViaHttp(String domainName,
			String host, String script, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save config");
		String path = AhDirTools.getBootstrapConfigDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, script);
		buf.append(httpUrl).append(" bootstrap");
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String configVerNum(int verNum) {
		return "config version " + (verNum > 0 ? verNum : 1) + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// Running Config CLIs
	// ***************************************************************

	/*
	 * Show running-config
	 */
	public static String showRunningConfig(String softVer, boolean displayingPwd) {
		return (NmsUtil.compareSoftwareVersion("3.2.0.0", softVer) > 0
				|| !displayingPwd ? "show running-config"
				: "show running-config password") + AH_CLI_SUFFIX;
	}

	/*
	 * Save config
	 */
	public static String getSaveConfigCli() {
		return "save config" + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// User Config CLIs
	// ***************************************************************

	/*
	 * Show user-config
	 */
	public static String showUserConfig(String softVer, boolean displayPwd,
			boolean displayAll) {
		if (NmsUtil.compareSoftwareVersion(softVer, "3.3.1.0") >= 0) {
			String showUserConfig = "show running-config users";

			if (displayPwd) {
				showUserConfig += " password";
			}

			if (displayAll) {
				showUserConfig += " all";
			}

			return showUserConfig + AH_CLI_SUFFIX;
		} else {
			return (displayPwd ? "show user-config password"
					: "show user-config") + AH_CLI_SUFFIX;
		}
	}

	/*
	 * Save user-config
	 */
	public static String saveUserConfig(String version) {
		return (NmsUtil.compareSoftwareVersion(version, "3.3.1.0") >= 0 ? "save config users"
				: "save user-config")
				+ AH_CLI_SUFFIX;
	}
	
	/*
	 * Show running-config xauth-clients
	 */
	public static String showVpnUserConfig() {
		String showVpnUserConfig = "show running-config xauth-clients password";
		return showVpnUserConfig + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// Captive Web Portal based CLIs
	// ***************************************************************

	/**
	 * <p>
	 * Get CWP key update cli.
	 * </p>
	 * <p>
	 * Syntax : save web-server-key <key-index>
	 * scp://username@location:path/filename no-prompt _password <string>
	 * Example : save web-server-key 1 scp://root@1.1.1.1:/1.key no-prompt
	 * _password aerohive
	 * </p>
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            The host where connect to.
	 * @param index
	 *            The index of the CWP key.
	 * @param key
	 *            The specified key which needs to be uploaded.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of CWP key update.
	 */
	/* Download CWP web server key via SSH */
	public static String downloadCwpServerKey(String domainName, String host,
			int index, String key, String userName, String password) {
		StringBuilder buf = new StringBuilder("save web-server-key ")
				.append(index);
		String path = AhDirTools.getCwpServerKeyDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, key);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download CWP web server key via HTTPS */
	public static String downloadCwpServerKeyViaHttp(String domainName,
			String host, int index, String key, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save web-server-key ")
				.append(index);
		String path = AhDirTools.getCwpServerKeyDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, key);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * <p>
	 * Get CWP web page update cli.
	 * </p>
	 * <p>
	 * Syntax : save web-page web-directory <web-dir>
	 * scp://username@location:path/filename no-prompt _password <string>
	 * Example : save web-page web-directory aaa scp://root@1.1.1.1:/1.html
	 * no-prompt _password aerohive
	 * </p>
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            The host where connect to.
	 * @param webPageDir
	 *            Where the web page is uploaded.
	 * @param webPage
	 *            The specified web page which needs to be uploaded.
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of CWP web page update.
	 */
	/* Download CWP web web page via SSH */
	public static String downloadCwpWebPage(String domainName, String host,
			String webPageDir, String webPage, String userName, String password) {
		StringBuilder buf = new StringBuilder("save web-page web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebPageDir(domainName, webPageDir);
		String sshComponent = getSshComponent(userName, host, path, webPage);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	
	/* Download CWP web web page via SSH with tar package */
	public static String downloadCwpWebPageTar(String domainName, String host,
			String webPageDir, String tarFileName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save web-page web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, tarFileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String downloadPpskWebPage(String domainName, String host,
			String webPageDir, String webPage, String userName, String password) {
		StringBuilder buf = new StringBuilder(
				"save web-page ppsk-self-reg web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebPageDir(domainName, webPageDir);
		String sshComponent = getSshComponent(userName, host, path, webPage);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	
	public static String downloadPpskWebPageTar(String domainName, String host,
			String webPageDir, String tarFileName, String userName, String password) {
		StringBuilder buf = new StringBuilder(
				"save web-page ppsk-self-reg web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, tarFileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download CWP web web page via HTTPS */
	public static String downloadCwpWebPageViaHttp(String domainName,
			String host, String webPageDir, String webPage,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save web-page web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebPageDir(domainName, webPageDir);
		String httpUrl = getHttpDownloadUrl(host, path, webPage);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	
	/* Download CWP web web page via HTTPS with tar package */
	public static String downloadCwpWebPageViaHttpTar(String domainName,
			String host, String webPageDir, String tarFileName,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save web-page web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, tarFileName);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String downloadPpskWebPageViaHttp(String domainName,
			String host, String webPageDir, String webPage,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder(
				"save web-page ppsk-self-reg web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebPageDir(domainName, webPageDir);
		String httpUrl = getHttpDownloadUrl(host, path, webPage);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}
	
	public static String downloadPpskWebPageViaHttpTar(String domainName,
			String host, String webPageDir, String tarFileName,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder(
				"save web-page ppsk-self-reg web-directory ")
				.append(webPageDir);
		String path = AhDirTools.getCwpWebDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, tarFileName);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * Get creating web page directory cli.
	 * <p>
	 * Syntax : web-directory <dir>
	 * </p>
	 * Example : web-directory ychen
	 * 
	 * @param dir
	 *            Web page directory which is used to save cwp web pages.
	 * @return Cli of creating web page directory.
	 */
	public static String getCreateWebPageDirCli(String dir) {
		return "web-directory " + dir + AH_CLI_SUFFIX;
	}

	public static String getCreatePpskWebPageDirCli(String dir) {
		return "web-directory ppsk-self-reg " + dir + AH_CLI_SUFFIX;
	}

	/**
	 * Get removing web page directory cli.
	 * <p>
	 * Syntax : no web-directory <dir>
	 * </p>
	 * Example : no web-directory ychen
	 * 
	 * @param dir
	 *            Web page directory which is used to save cwp web pages.
	 * @return The cli of removing web page directory.
	 */
	public static String getRemoveWebPageDirCli(String dir) {
		return "no web-directory " + dir + AH_CLI_SUFFIX;
	}

	public static String getRemovePpskWebPageDirCli(String dir) {
		return "no web-directory ppsk-self-reg " + dir + AH_CLI_SUFFIX;
	}

	/**
	 * 
	 * Remove all web directories
	 * 
	 * 
	 * @return The cli of removing all web page directory.
	 */
	public static String getRemoveAllWebPageDirCli() {
		return "clear web-directory" + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// Radius server based CLIs
	// ***************************************************************

	/**
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            host The host where connect to.
	 * @param key
	 *            the file which download;
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of radius server key update.
	 */
	/* Download radius server key via SSH */
	public static String downloadRadiusServerKey(String domainName,
			String host, String key, String userName, String password) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key radius-server");
		String path = AhDirTools.getCertificateDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, key);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download radius server key via HTTPS */
	public static String downloadRadiusServerKeyViaHttp(String domainName,
			String host, String key, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key radius-server");
		String path = AhDirTools.getCertificateDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, key);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            host The host where connect to.
	 * @param key
	 *            the file which download;
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of radius server key update.
	 */
	/* Download LDAP client key via SSH */
	public static String downloadLdapClientKey(String domainName, String host,
			String key, String userName, String password) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key ldap-client");
		String path = AhDirTools.getCertificateDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, key);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download LDAP client key via HTTPS */
	public static String downloadLdapClientKeyViaHttp(String domainName,
			String host, String key, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key ldap-client");
		String path = AhDirTools.getCertificateDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, key);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * 
	 * @return The cli of save certificate and private key files used by the
	 *         internal web and RADIUS servers.
	 */
	public static String getSaveServerFilesCli() {
		return "save server-files" + AH_CLI_SUFFIX;
	}

	/**
	 * 
	 * @return The cli of radius server key clear.
	 */
	public static String getRadiusServerClearCli() {
		return "clear aaa radius-server-key radiusd" + AH_CLI_SUFFIX;
	}

	/**
	 * 
	 * @return The cli of ldap client ca clear.
	 */
	public static String getLdapClientClearCli() {
		return "clear aaa radius-server-key openldap" + AH_CLI_SUFFIX;
	}

	public static String removeAllLocalUsers() {
		return "no aaa radius-server local all-users" + AH_CLI_SUFFIX;
	}

	public static String removeCachedLocalUsers() {
		return "no aaa radius-server local all-cached-users" + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// CAPWAP based CLIs
	// ***************************************************************

	/**
	 * <p>
	 * Get CAPWAP DTLS configuration cli.
	 * </p>
	 * 
	 * @param enableDtls
	 *            Indicates whether enabling DTLS.
	 * @param passPhrase
	 *            DTLS pass phrase.
	 * @param keyId
	 *            Key id.
	 * @return the cli of CAPWAP DTLS configuration.
	 */
	public static String[] getCapwapDtlsConfigClis(boolean enableDtls,
			String passPhrase, int keyId) {
		String[] clis;

		if (enableDtls) {
			clis = new String[2];
			clis[0] = "capwap client dtls enable no-disconnect" + AH_CLI_SUFFIX;
			clis[1] = "capwap client dtls hm-defined-passphrase " + passPhrase
					+ " key-id " + keyId + AH_CLI_SUFFIX;
		} else {
			clis = new String[1];
			clis[0] = "no capwap client dtls enable no-disconnect"
					+ AH_CLI_SUFFIX;
		}

		return clis;
	}

	// ***************************************************************
	// SSH based CLIs
	// ***************************************************************

	/**
	 * <p>
	 * Set up SSH tunnel between HiveAP and Aerohive SSH server.
	 * </p>
	 * 
	 * @param sshServer
	 *            domain name or IP address of the Aerohive SSH server.
	 * @param sshServerPort
	 *            the number of port to connect to SSH server.
	 * @param tunnelPort
	 *            the port number that the SSH server uses to identify the
	 *            tunnel.
	 * @param sshServerUser
	 *            the user name for logging in to the SSH server.
	 * @param sshServerPwd
	 *            password for logging in to the SSH server.
	 * @param tunnelTimeout
	 *            tunnel timeout in minutes during which the tunnel between the
	 *            HiveAP and the Aerohive SSH server will be up.
	 * @return The CLI of setting up SSH tunnel between HiveAP and Aerohive SSH
	 *         server.
	 */
	public static String setupSshTunnel(String sshServer, int sshServerPort,
			int tunnelPort, String sshServerUser, String sshServerPwd,
			int tunnelTimeout) {
		return "ssh-tunnel server " + sshServer + ":" + sshServerPort
				+ " tunnel-port " + tunnelPort + " user " + sshServerUser
				+ " password " + sshServerPwd + " timeout " + tunnelTimeout
				+ AH_CLI_SUFFIX;
	}

	/**
	 * <p>
	 * Close the SSH tunnel which has been already set up.
	 * </p>
	 * 
	 * @return The CLI of closing SSH tunnel.
	 */
	public static String closeSshTunnel() {
		return "no ssh-tunnel" + AH_CLI_SUFFIX;
	}

	public static String showSshTunnel() {
		return "show ssh-tunnel" + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// Reboot based CLIs
	// ***************************************************************

	/*
	 * Reboot Note: it should be delay for device cli event.
	 */
	public static String getRebootCli(String offset_str) {
		return "reboot offset " + offset_str + " " + AH_CLI_NO_PROMPT
				+ AH_CLI_SUFFIX;
	}

	public static String getRebootCli(String date, String time) {
		return "reboot date " + date + " time " + time + " " + AH_CLI_NO_PROMPT
				+ AH_CLI_SUFFIX;
	}
	
	/***
	 * 
	 * @param rebootCli
	 * @return
	 */
	private static Pattern rebootPattern;
	public static boolean isRebootImmediately(String rebootCli){
		if(rebootPattern == null){
			String regex = "reboot +offset +00:00:(\\d+) +"+AH_CLI_NO_PROMPT+"\\n*";
			rebootPattern = Pattern.compile(regex);
		}
		Matcher rebootMatcher = rebootPattern.matcher(rebootCli);
		if(rebootMatcher.matches()){
			String timeStr = rebootMatcher.group(1);
			return Integer.valueOf(timeStr) <= 60;
		}else{
			return false;
		}
	}

	/*
	 * Reboot backup Note: it should be delay for device cli event.
	 */
	public static String getRebootBackupCli(String offset_str) {
		return "reboot backup offset " + offset_str + " " + AH_CLI_NO_PROMPT
				+ AH_CLI_SUFFIX;
	}

	/*
	 * Reboot current Note: it should be delay for device cli event.
	 */
	public static String getRebootCurrentCli(String offset_str) {
		return "reboot current offset " + offset_str + " " + AH_CLI_NO_PROMPT
				+ AH_CLI_SUFFIX;
	}

	/*
	 * 
	 */
	public static String getQosEnableCli(boolean toggle) {
		if(toggle){
			return "qos enable"+ AH_CLI_SUFFIX;
		} else {
			return "no qos enable "+AH_CLI_NO_PROMPT+ AH_CLI_SUFFIX;
		}
	}
	
	/*
	 * Get Country Code CLI.
	 */
	public static String getCountryCodeCli(int countryCode) {
		return "boot-param country-code " + countryCode + " "
				+ AH_CLI_NO_PROMPT + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// Capture interface based CLIs
	// ***************************************************************

	/**
	 * capture save interface <wifix> {{tftp://server:/path/filename} |
	 * <filename>}
	 * 
	 * @param interfaceName
	 *            -
	 * @param transferPtcl
	 *            -
	 * @param server
	 *            -
	 * @param pathName
	 *            -
	 * @param fileName
	 *            -
	 * @return -
	 */
	public static String getSaveCaptureCli(String interfaceName,
			String transferPtcl, String server, String pathName, String fileName) {
		String cli = "capture save interface " + interfaceName;

		if (transferPtcl.equalsIgnoreCase("local")) {
			cli += " " + fileName;
		} else if (transferPtcl.equalsIgnoreCase("tftp")) {
			cli += " tftp://" + server + ":" + pathName;
		} else {
			return null;
		}

		return cli + AH_CLI_SUFFIX;
	}

	/**
	 * capture interface <wifix> [count <packet_count>] [filter <filter_id>]
	 * 
	 * @param wifi
	 *            -
	 * @param count
	 *            -
	 * @param captureByFilter
	 *            -
	 * @param filterID
	 *            -
	 * @param isPromiscuous
	 *            -
	 * @return -
	 */
	public static String getStartCaptureCli(String wifi, String count,
			boolean captureByFilter, String filterID, boolean isPromiscuous) {
		String cli = "capture interface " + wifi + " count " + count;

		if (captureByFilter) {
			cli += " filter " + filterID;
		}

		if (isPromiscuous) {
			cli += " promiscuous";
		}

		return cli + AH_CLI_SUFFIX;
	}

	/**
	 * no capture interface <wifix>
	 * 
	 * @param wifi
	 *            -
	 * @return -
	 */
	public static String getStopCaptureCli(String wifi) {
		return "no capture interface " + wifi + AH_CLI_SUFFIX;
	}

	/**
	 * show capture interface <wifix>
	 * 
	 * @param wifi
	 *            -
	 * @return -
	 */
	public static String getShowCaptureCli(String wifi) {
		return "show capture interface " + wifi + AH_CLI_SUFFIX;
	}

	// ***************************************************************
	// AMRP based CLIs
	// ***************************************************************

	/*
	 * amrp dnxp neighbor information(added from 3.4r1)
	 */
	public static String getDnxpNeighbor() {
		return "show amrp dnxp neighbor" + AH_CLI_SUFFIX;
	}

	/*
	 * amrp dnxp cache table(added from 3.4r1)
	 */
	public static String getDnxpCache() {
		return "show amrp dnxp cache" + AH_CLI_SUFFIX;
	}

	/*
	 * Ping
	 */
	public static String getPingCli(String hostIp) {
		return "ping " + hostIp + AH_CLI_SUFFIX;
	}

	/*
	 * Trace Route
	 */
	public static String getTracerouteCli(String hostIp) {
		return "tracert " + hostIp + AH_CLI_SUFFIX;
	}

	/**
	 * filter cli syntax as below
	 * 
	 * filter [<filter_id>] [data|ctl|mgmt] [subtype <hex>] [src-mac <mac>]
	 * [dst-mac <mac>] [bssid <mac>] [tx-mac <mac>] [rx-mac <mac>] [error
	 * {crc|decrypt|mic|all|no}] [etype <hex>]
	 * 
	 * @param filterID
	 *            -
	 * @param trafficType
	 *            -
	 * @param subtype
	 *            -
	 * @param bssid
	 *            -
	 * @param srcMac
	 *            -
	 * @param destMac
	 *            -
	 * @param txMac
	 *            -
	 * @param rxMac
	 *            -
	 * @param errorValue
	 *            -
	 * @param ethType
	 *            -
	 * @return -
	 */
	public static String getAddFilterCli(String filterID, String trafficType,
			String subtype, String bssid, String srcMac, String destMac,
			String txMac, String rxMac, String errorValue, String ethType) {
		// mark: filter cli modify, current HM just support set l2 filter
		String cli = "filter " + filterID + " l2";

		if (!trafficType.equalsIgnoreCase("all")) {
			cli += " " + trafficType;
		}

		if (subtype != null && subtype.length() > 0) {
			cli += " subtype " + subtype;
		}

		if (srcMac != null && srcMac.length() > 0) {
			cli += " src-mac " + srcMac;
		}

		if (destMac != null && destMac.length() > 0) {
			cli += " dst-mac " + destMac;
		}

		if (bssid != null && bssid.length() > 0) {
			cli += " bssid " + bssid;
		}

		if (txMac != null && txMac.length() > 0) {
			cli += " tx-mac " + txMac;
		}

		if (rxMac != null && rxMac.length() > 0) {
			cli += " rx-mac " + rxMac;
		}

		cli += " error " + errorValue;

		if (!ethType.equalsIgnoreCase("all")) {
			cli += " etype " + ethType;
		}

		return cli + AH_CLI_SUFFIX;
	}

	/**
	 * remove filter cli syntax as below:
	 * 
	 * no filter <filter_id>
	 * 
	 * @param filterID
	 *            -
	 * 
	 * @return -
	 */
	public static String getRemoveFilterCli(String filterID) {
		return "no filter " + filterID + AH_CLI_SUFFIX;
	}

	/**
	 * Upload local captured file to remote server (tftp or scp)
	 * 
	 * save capture local <filename> <location>
	 * 
	 * e.g. save capture local src.dmp tftp://10.155.20.223:/dump/dest.dmp
	 * 
	 * @param localFile
	 *            -
	 * @param location
	 *            -
	 * @return -
	 */
	/* Upload capture via TFTP */
	public static String uploadCapture(String localFile, String location) {
		return "save capture local " + localFile + " " + location
				+ AH_CLI_SUFFIX;
	}

	/* Upload capture via HTTPS */
	public static String uploadCaptureViaHttp(String host, String localFile,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder cliBuf = new StringBuilder("save capture local ")
				.append(localFile);
		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(1);

		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_PACKET_CAPTURE;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));
		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		cliBuf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		cliBuf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return cliBuf.toString();
	}

	/*
	 * lldp cdp interface
	 */
	public static String getSaveLldpCdpInterfaceCli(boolean enable,
			String smallCli, boolean blnCdp) {
		String cli = "";

		if (!enable) {
			cli = cli + "no ";
		}

		cli = cli + smallCli + " lldp";

		if (blnCdp) {
			cli = cli + " " + "cdp";
		}

		return cli + " enable" + AH_CLI_SUFFIX;
	}

	/*
	 * lldp cdp parameter
	 */
	public static String getSaveLldpCdpParameterCli(boolean blnCdp,
			String paraKey, int value) {
		String cli = "lldp";

		if (blnCdp) {
			cli = cli + " " + "cdp";
		}

		return cli + " " + paraKey + " " + value + AH_CLI_SUFFIX;
	}

	/*
	 * lldp cdp clear
	 */
	public static String getSaveLldpClearCli() {
		return "clear lldp" + " table" + AH_CLI_SUFFIX;
	}
	
	/*
	 * cdp clear
	 */
	public static String getSaveCdpClearCli(HiveAp hiveAp) {
		if(HiveAp.isSwitchProduct(hiveAp.getHiveApModel())){
			return "clear cdp " + " table" + AH_CLI_SUFFIX;
		}else{
			return "clear lldp cdp " + " table" + AH_CLI_SUFFIX;
		}
	}

	/*
	 * show lldp
	 */
	public static String getLldpParameterCli() {
		return "show lldp" + AH_CLI_SUFFIX;
	}

	/*
	 * show lldp cdp
	 */
	public static String getCdpParameterCli() {
		return "show lldp cdp" + AH_CLI_SUFFIX;
	}
	
	/*
	 * show lldp cdp
	 */
	public static String getCdpParameterCliForSwitch() {
		return "show cdp" + AH_CLI_SUFFIX;
	}

	/*
	 * show lldp neighbour
	 */
	public static String getLldpNeighborCli() {
		return "show lldp neighbor" + AH_CLI_SUFFIX;
	}

	/*
	 * show lldp cdp neighbour
	 */
	public static String getCdpNeighborCli() {
		return "show lldp cdp neighbor" + AH_CLI_SUFFIX;
	}
	
	/*
	 * show lldp cdp neighbour for switch
	 */
	public static String getCdpNeighborCliForSwitch() {
		return "show cdp neighbor" + AH_CLI_SUFFIX;
	}
	
	public static String getPseShutdownCLi(String eth,boolean delayOption,boolean isNo) {
		String cli = "";
		if(isNo){
			cli = "no interface "+eth+" pse shutdown";
		} else {
			if(delayOption){
				cli = "interface "+eth+" pse shutdown delay";
			} else {
				cli = "interface "+eth+" pse shutdown";
			}
		}
		cli = cli + AH_CLI_SUFFIX;
		
		return cli;
	}

	/*
	 * Log
	 */
	public static String getLogCli() {
		return "show logging buffered tail 500" + AH_CLI_SUFFIX;
	}

	public static String getFdbCli() {
		return "show mac-address-table all" + AH_CLI_SUFFIX;
	}

	/*
	 * Version
	 */
	public static String getVersionCli() {
		return "show version" + AH_CLI_SUFFIX;
	}

	/*
	 * Version detail
	 */
	public static String getVersionDetailCli() {
		return "show version detail" + AH_CLI_SUFFIX;
	}

	/*
	 * IP Route
	 */
	public static String getIPRouteCli() {
		return "show ip route" + AH_CLI_SUFFIX;
	}

	/*
	 * MAC Route
	 */
	public static String getMACRouteCli() {
		return "show route" + AH_CLI_SUFFIX;
	}

	/*
	 * ARP Cache
	 */
	public static String getARPCacheCli() {
		return "show arp-cache" + AH_CLI_SUFFIX;
	}

	/*
	 * Roaming Cache
	 */
	public static String getRoamingCacheCli() {
		return "show roaming cache" + AH_CLI_SUFFIX;
	}

	/*
	 * CPU
	 */
	public static String getCPUCli() {
		return "show cpu" + AH_CLI_SUFFIX;
	}

	/*
	 * Memory
	 */
	public static String getMemoryCli() {
		return "show memory" + AH_CLI_SUFFIX;
	}

	/*
	 * ACSP
	 */
	public static String getACSPCli() {
		return "show acsp" + AH_CLI_SUFFIX;
	}

	/*
	 * AMRP tunnel
	 */
	public static String getAmrpTunnelCli() {
		return "show amrp tunnel" + AH_CLI_SUFFIX;
	}

	/*
	 * VPN GRE tunnel
	 */
	public static String getVpnGreTunnelCli() {
		return "show vpn gre-tunnel" + AH_CLI_SUFFIX;
	}

	/*
	 * VPN IKE event
	 */
	public static String getVpnIkeEventCli() {
		return "show vpn ike event" + AH_CLI_SUFFIX;
	}

	/*
	 * VPN IKE sa
	 */
	public static String getVpnIkeSaCli() {
		return "show vpn ike sa" + AH_CLI_SUFFIX;
	}

	/*
	 * VPN IPsec sa
	 */
	public static String getVpnIpSecSaCli() {
		return "show vpn ipsec sa" + AH_CLI_SUFFIX;
	}

	/*
	 * VPN IPsec-tunnel
	 */
	public static String getVpnIpSecTunnelCli() {
		return "show vpn ipsec-tunnel" + AH_CLI_SUFFIX;
	}

	/*
	 * Interface(if subIf is null of '', show all Interface)
	 */
	public static String getInterfaceCli(String subIf) {
		if (null == subIf || "".equals(subIf.trim())) {
			return "show interface" + AH_CLI_SUFFIX;
		} else {
			return "show interface " + subIf + AH_CLI_SUFFIX;
		}
	}
	
	/*
	 * Interface(if subIf is null of '', show all Interface)
	 */
	public static String getInterfaceCli(String subIf,String regex) {
		if (null == subIf || "".equals(subIf.trim())) {
			return "show interface" + AH_CLI_SUFFIX;
		} else {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(subIf);
			
			if(matcher.matches()){
				return "show l3 interface " + subIf + AH_CLI_SUFFIX;
			} else {
				return "show interface " + subIf + AH_CLI_SUFFIX;
			}
			
		}
	}

	/*
	 * dhcp client allocation
	 */
	public static String getDhcpClientAllocation(String subIf) {
		if (null == subIf || "".equals(subIf.trim())) {
			return "show interface mgt0 dhcp-server detail " + AH_CLI_SUFFIX;
		} else {
			return "show interface " + subIf + " dhcp-server detail "
					+ AH_CLI_SUFFIX;
		}
	}

	/*
	 * Running Config
	 */
	public static String getRunningConfig(String include) {
		if (null == include || "".equals(include.trim())) {
			return "show running-config " + AH_CLI_SUFFIX;
		} else {
			return "show running-config | inc \"" + include + "\" "
					+ AH_CLI_SUFFIX;
		}
	}

	/*
	 * station (Client Summary States)
	 */
	public static String getStationCli() {
		return "show station" + AH_CLI_SUFFIX;
	}

	/*
	 * Interface(if callId is null or '', show all callId)
	 */
	public static String getAlgSipCli(String callId) {
		if (null == callId || "".equals(callId.trim())) {
			return "show alg sip calls" + AH_CLI_SUFFIX;
		} else {
			return "show alg sip calls " + callId + AH_CLI_SUFFIX;
		}
	}

	/*
	 * Path Mtu Discovery
	 */
	public static String getPathMtuDiscoveryCli() {

		return "show ip path-mtu-discovery " + AH_CLI_SUFFIX;

	}

	/*
	 * Tcp Mss Threshold
	 */
	public static String getTcpMssThresholdCli() {

		return "show ip tcp-mss-threshold " + AH_CLI_SUFFIX;

	}

	/*
	 * reset PSE module
	 */
	public static String getPseResetCli() {

		return "pse reset " + AH_CLI_SUFFIX;

	}
	
	/*
	 * reset USB module reset
	 */
	public static String getUsbModemResetCli() {

		return "usbmodem power cycle " + AH_CLI_SUFFIX;

	}

	/*
	 * clear ID management
	 */
	public static String getClearRadsecCertCli() {

		return "clear aaa radius-server-key radsec end-cert " + AH_CLI_SUFFIX;

	}
	
	/**
	 * Disable HiveUI configuration
	 * @return CLIs
	 */
	public static String getDisableHiveUIConfigCli() {

		return "no hiveui wan-cfg enable" + AH_CLI_SUFFIX;

	}
	
	/*
	 * reset device to default
	 */
	public static String getResetDeviceToDefaultCli() {

		return "reset config no-prompt " + AH_CLI_SUFFIX;

	}

	/*
	 * exec mitigate rogue ap
	 */
	public static String getExecMitigateCli(String wifix, String bssid,
			boolean verFlag) {
		if (verFlag) {
			if (wifix == null) {
				return "exec wlan-idp mitigate rogue-ap " + bssid
						+ AH_CLI_SUFFIX;
			} else {
				return "exec wlan-idp mitigate rogue-ap " + bssid
						+ " interface " + wifix + AH_CLI_SUFFIX;
			}
		} else {
			return "exec interface " + wifix + " wlan-idp mitigate rogue-ap "
					+ bssid + AH_CLI_SUFFIX;
		}
	}

	/*
	 * cancel mitigate rogue ap
	 */
	public static String getCancelMitigateCli(String wifix, String bssid,
			boolean verFlag) {
		if (verFlag) {
			if (wifix == null) {
				return "no exec wlan-idp mitigate rogue-ap " + bssid
						+ AH_CLI_SUFFIX;
			} else {
				return "no exec wlan-idp mitigate rogue-ap " + bssid
						+ " interface " + wifix + AH_CLI_SUFFIX;
			}
		} else {
			return "no exec interface " + wifix
					+ " wlan-idp mitigate rogue-ap " + bssid + AH_CLI_SUFFIX;
		}
	}

	/*
	 * show mitigate rogue clients
	 */
	public static String getMitigateClients(String wifix, String bssid) {
		return "show interface " + wifix + " wlan-idp mitigate rogue-ap "
				+ bssid + AH_CLI_SUFFIX;
	}

	/*
	 * Get PoE max power CLI.
	 */
	public static String getPoeMaxPowerCli(int maxPower) {
		switch (maxPower) {
		case HiveApUpdateAction.MAX_POWER_HIGH:
			return "boot-param poe-max-power high" + AH_CLI_SUFFIX;
		case HiveApUpdateAction.MAX_POWER_MEDIUM_HIGH:
			return "boot-param poe-max-power medium-high" + AH_CLI_SUFFIX;
		case HiveApUpdateAction.MAX_POWER_MEDIUM:
			return "boot-param poe-max-power medium" + AH_CLI_SUFFIX;
		case HiveApUpdateAction.MAX_POWER_LOW:
			return "boot-param poe-max-power low" + AH_CLI_SUFFIX;
		case HiveApUpdateAction.MAX_POWER_802_3AF:
			return "boot-param poe-max-power 802.3af" + AH_CLI_SUFFIX;
		default:
			return "no boot-param poe-max-power" + AH_CLI_SUFFIX;
		}
	}

	/*
	 * Get show PoE max power CLI
	 */
	public static String showPoeMaxPowerCli() {
		return "show system power" + AH_CLI_SUFFIX;
	}

	public static String showPoePowerStatusCli() {
		return "show system power status" + AH_CLI_SUFFIX;
	}

	/*
	 * Get show Pse CLI
	 */
	public static String showPseCli() {
		return "show pse" + AH_CLI_SUFFIX;
	}
	
	/*
	 * Get show PoE max power CLI
	 */
	public static String getMulticastMonitorCli(String wifiInterface) {
		if ("1".equals(wifiInterface)) {
			return "show interface wifi1 multicast" + AH_CLI_SUFFIX;
		} else {
			return "show interface wifi0 multicast" + AH_CLI_SUFFIX;
		}

	}

	/*
	 * upload netdump cli
	 */

	public static String getEnableNetdumpCli(boolean enable) {
		if (enable) {
			return "boot-param netdump enable" + AH_CLI_SUFFIX;
		} else {
			return "no boot-param netdump enable" + AH_CLI_SUFFIX;
		}
	}

	public static String getNetdumpServerCli(String serverIp) {
		if (serverIp == null || "".equals(serverIp)) {
			return "no boot-param server" + AH_CLI_SUFFIX;
		} else {
			return "boot-param server " + serverIp + AH_CLI_SUFFIX;
		}
	}
	
	public static String getNetdumpManagerPortCli(String managerPortStr) {
		if(StringUtils.isBlank(managerPortStr)){
			return "";
		}
		return "boot-param management-port "+managerPortStr+AH_CLI_SUFFIX;
	}

	public static String getNetdumpFileCli() {
		return "boot-param netdump dump-file" + AH_CLI_SUFFIX;
	}

	public static String getNetdumpVlanCli(String vlanId,
			boolean enableNetdump, String defualtValue) {
		if (enableNetdump) {
			if (vlanId == null || "".equals(vlanId)) {
				return "boot-param vlan " + defualtValue + AH_CLI_SUFFIX;
			} else {
				return "boot-param vlan " + vlanId + AH_CLI_SUFFIX;
			}
		} else {
			return "no boot-param vlan" + AH_CLI_SUFFIX;
		}

	}

	public static String getNetdumpNVlanCli(String nVlanId,
			boolean enableNetdump, String defualtValue) {
		if (enableNetdump) {
			if (nVlanId == null || "".equals(nVlanId)) {
				return "boot-param native-vlan " + defualtValue + AH_CLI_SUFFIX;
			} else {
				return "boot-param native-vlan " + nVlanId + AH_CLI_SUFFIX;
			}
		} else {
			return "no boot-param native-vlan" + AH_CLI_SUFFIX;
		}

	}

	public static String getNetdumpDeviceCli(String device, String ipMode) {
		if ("1".equals(ipMode)) {
			return "no boot-param device" + AH_CLI_SUFFIX;
		} else {
			if (device == null || "".equals(device)) {
				return "no boot-param device" + AH_CLI_SUFFIX;
			} else {
				return "boot-param device " + device + AH_CLI_SUFFIX;
			}
		}
	}

	public static String getNetdumpGatewayCli(String gateway, String ipMode) {
		if ("1".equals(ipMode)) {
			return "no boot-param gateway" + AH_CLI_SUFFIX;
		} else {
			if (gateway == null || "".equals(gateway)) {
				return "no boot-param gateway" + AH_CLI_SUFFIX;
			} else {
				return "boot-param gateway " + gateway + AH_CLI_SUFFIX;
			}
		}
	}

	/*
	 * upload kernel dump file
	 */
	public static String uploadKernelDumpFile(String userName, String password,
			String host, String path) {
		StringBuilder buf = new StringBuilder("save _kernel all");
		String sshComponent = getSshComponent(userName, host, path, "");
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Upload HiveAP tech via SSH */
	public static String uploadTech(String userName, String password,
			String host, String location, String fileName) {
		StringBuilder buf = new StringBuilder("show tech >");
		String sshComponent = getSshComponent(userName, host, location,
				fileName);
		buf.append(sshComponent).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Upload HiveAP tech via HTTPS */
	public static String uploadTechViaHttp(String host, String filename,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder cliBuf = new StringBuilder("show tech");
		Map<String, String> paramPairMap = new LinkedHashMap<String, String>(2);

		// Parameter: File Type
		int fileType = UploadHandler.FILE_TYPE_AP_TECH;
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_TYPE,
				String.valueOf(fileType));

		// Parameter: Filename
		paramPairMap.put(UploadHandler.REQ_PARAM_FILE_NAME, filename);

		String httpUrl = getHttpUploadUrl(host, paramPairMap);
		cliBuf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		cliBuf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return cliBuf.toString();
	}

	/* Download VPN CA certificate via SSH */
	public static String downloadVpnCaCert(String domainName, String host,
			String fileName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save vpn ca-cert");
		String path = AhDirTools.getCertificateDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, fileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	/* Download VPN CA certificate via HTTPS */
	public static String downloadVpnCaCertViaHttp(String domainName,
			String host, String caCert, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save vpn ca-cert");
		String path = AhDirTools.getCertificateDir(domainName);
		String httpUrlPart = getHttpDownloadUrl(host, path, caCert);
		buf.append(httpUrlPart);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download Cloud Auth certificate via SSH */
	public static String downloadCloudAuthCaCert(String domainName,
			String macAddress, String host, String fileName, String userName,
			String password) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key radsec cert");
		String path = AhDirTools.getCloudAuthCaDir(domainName, macAddress);
		String sshComponent = getSshComponent(userName, host, path, fileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	/* Download Cloud Auth certificate via HTTPS */
	public static String downloadCloudAuthCaCertViaHttp(String domainName,
			String macAddress, String host, String caCert,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder(
				"save radius-server-key radsec cert");
		String path = AhDirTools.getCloudAuthCaDir(domainName, macAddress);
		String httpUrlPart = getHttpDownloadUrl(host, path, caCert);
		buf.append(httpUrlPart);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download VPN EE certificate via SSH */
	public static String downloadVpnEeCert(String domainName, String host,
			String fileName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save vpn ee-cert");
		String path = AhDirTools.getCertificateDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, fileName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	/* Download VPN EE certificate via HTTPS */
	public static String downloadVpnEeCertViaHttp(String domainName,
			String host, String eeCert, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save vpn ee-cert");
		String path = AhDirTools.getCertificateDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, eeCert);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download VPN private key via SSH */
	public static String downloadVpnPrivateKey(String domainName, String host,
			String keyName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save vpn private-key");
		String path = AhDirTools.getCertificateDir(domainName);
		String sshComponent = getSshComponent(userName, host, path, keyName);
		String passwordComponent = getPasswordComponent(password);
		buf.append(sshComponent).append(passwordComponent)
				.append(AH_CLI_SUFFIX);
		return buf.toString();
	}

	/* Download VPN private key via HTTPS */
	public static String downloadVpnPrivateKeyViaHttp(String domainName,
			String host, String key, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save vpn private-key");
		String path = AhDirTools.getCertificateDir(domainName);
		String httpUrl = getHttpDownloadUrl(host, path, key);
		buf.append(httpUrl);
		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String getConfigCapwapServerCli(String softVer,
			String capwapServer, boolean isPrimary, boolean delay) {
		if (NmsUtil.compareSoftwareVersion(softVer, "3.4.2.0") >= 0) {
			return "capwap client server " + (isPrimary ? "" : "backup ")
					+ "name " + capwapServer + " connect-delay 35"
					+ AH_CLI_SUFFIX;
		} else {
			return "capwap client server " + (isPrimary ? "primary " : "")
					+ "name " + capwapServer + AH_CLI_SUFFIX;
		}
	}

	// Set report interval for spectral scan
	public static String getSpectralScanIntervalCli(String interf,
			short interval, boolean isNo) {
		String no = "no ";
		String cli = "Exec interface ";
		if (isNo) {
			cli += (interf + " spectral-scan report-interval " + AH_CLI_SUFFIX);
		} else {
			cli += (interf + " spectral-scan report-interval " + interval + AH_CLI_SUFFIX);
		}
		if (isNo)
			cli = no + cli;
		return cli;
	}

	// Add channel to scan channel list.
	public static String getSpectralScanChannelCli(String interf,
			Short channel, boolean isNo) {
		String no = "no ";
		String cli = "Exec interface ";
		if (channel != null) {
			cli += (interf + " spectral-scan channel " + channel + AH_CLI_SUFFIX);
		} else {
			cli += (interf + " spectral-scan channel " + AH_CLI_SUFFIX);
		}
		if (isNo)
			cli = no + cli;
		return cli;
	}

	// Set channel to scan channel list.
	public static String getExecScanCli(String interf, String action) {
		return "Exec interface " + interf + " spectral-scan " + action
				+ AH_CLI_SUFFIX;
	}

	/**
	 * configure HiveManager without disconnect.
	 * 
	 * @param softVer
	 *            -
	 * @param capwapServer
	 *            -
	 * @param isPrimary
	 *            -
	 * @return -
	 */
	public static String getHmConfigNoDisconnectCli(String softVer,
			String capwapServer, boolean isPrimary) {
		if (NmsUtil.compareSoftwareVersion(softVer, "3.4.2.0") >= 0) {
			return "capwap client server " + (isPrimary ? "" : "backup ")
					+ "name " + capwapServer + " no-disconnect" + AH_CLI_SUFFIX;
		} else {
			return "capwap client server " + (isPrimary ? "primary " : "")
					+ "name " + capwapServer + " no-disconnect" + AH_CLI_SUFFIX;
		}
	}

	public static String getNoLedCli() {
		return "no _led color" + AH_CLI_SUFFIX;
	}

	public static String getLedIndexColorBlinkCli(String index, String color,
			String blink) {
		return "_led index " + index + " color " + color + " " + blink
				+ AH_CLI_SUFFIX;
	}
	
	public static String getLedSysColorBlinkCli(String color, String blink) {
		return "_led color sys " + color + " " + blink + AH_CLI_SUFFIX;
	}

	public static String getLedColorBlinkCli(String color, String blink) {
		return "_led color " + color + " " + blink + AH_CLI_SUFFIX;
	}

	/* Download portal image via SSH */
	public static String downloadPortalImage(String host, String imageName,
			String userName, String password, boolean tftp, int limit) {
		StringBuilder buf = new StringBuilder("_save file");
		String path;

		if (tftp) {
			path = AhDirTools.getExtenalImageDir();
			String tftpComponent = getTftpComponent(host, path, imageName);
			buf.append(tftpComponent);
		} else {
			path = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
			String sshPart = getSshComponent(userName, host, path, imageName);
			buf.append(sshPart);
		}

		if (tftp) {
			buf.append(AH_CLI_NO_PROMPT);
		} else {
			String passwordComponent = getPasswordComponent(password);
			buf.append(passwordComponent);
		}

		if (limit > 0) {
			buf.append(" limit ").append(limit);
		}

		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download portal image via HTTPS */
	public static String downloadPortalImageViaHttp(String host,
			String imageName, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("_save file");
		String path = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
		String httpUrl = getHttpDownloadUrl(host, path, imageName);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download portal image via Download Server */
	public static String downloadPortalImageViaDS(String imageName,
			String webServerLoginUser, String webServerLoginPwd, String proxy,
			int proxyPort, String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("_save file ");
		String httpUrl = getImageDSUrl(imageName);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download portal image via Download Server */
	public static String downloadPortalImageViaDS(short hiveApModel,
			String version, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("_save file ");
		String httpUrl = getImageDSUrl(hiveApModel, version);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_NO_PROMPT).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/**
	 * 
	 * @param domainName
	 *            The name of VHM which is used to get the specific path of file
	 *            to be uploaded.
	 * @param host
	 *            host The host where connect to.
	 * @param fileName
	 *            the file which download;
	 * @param userName
	 *            The user name used for ssh login.
	 * @param password
	 *            The password used for ssh login.
	 * @return The cli of OsDetection file update.
	 */
	/* Download OSDetection file via SSH */
	public static String downloadOsDetectionFile(String domainName,
			String host, String fileName, String userName, String password) {
		StringBuilder buf = new StringBuilder("save dhcp-fingerprint option55");
		String path = AhDirTools.getOsDetectionDir();
		String sshPart = getSshComponent(userName, host, path, fileName);
		buf.append(sshPart);
		String passwordComponent = getPasswordComponent(password);
		buf.append(passwordComponent);
		buf.append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	/* Download OSDetection file via HTTPS */
	public static String downloadOsDetectionFileViaHttp(String domainName,
			String host, String fileName, String webServerLoginUser,
			String webServerLoginPwd, String proxy, int proxyPort,
			String proxyLoginUser, String proxyLoginPwd) {
		StringBuilder buf = new StringBuilder("save dhcp-fingerprint option55");
		String path = AhDirTools.getOsDetectionDir();
		String httpUrl = getHttpDownloadUrl(host, path, fileName);
		buf.append(httpUrl);

		String httpAuthPart = getHttpAuthComponent(webServerLoginUser,
				webServerLoginPwd, proxy, proxyPort, proxyLoginUser,
				proxyLoginPwd);
		buf.append(httpAuthPart).append(AH_CLI_SUFFIX);

		return buf.toString();
	}

	public static String showOsDetectionVersion() {
		return "show os-detection dhcp-fingerprint-version" + AH_CLI_SUFFIX;
	}

	public static String clearPortalImage() {
		return "_clear save file" + AH_CLI_SUFFIX;
	}

	public static String getConfigRollbackEnableCli() {
		return "config rollback enable" + AH_CLI_SUFFIX;
	}

	public static String getConfigRollbackNextRebootCli() {
		return "config rollback next-reboot" + AH_CLI_SUFFIX;
	}

	/*
	 * upload ip/netmask/gateway/dns cli
	 */
	public static String getConfigIpAndNetmaskCli(String ipAddress,
			String netMask) {
		if (ipAddress == null || "".equals(ipAddress)) {
			return "no interface mgt0 ip " + AH_CLI_SUFFIX;
		} else {
			return "interface mgt0 ip " + ipAddress + " " + netMask
					+ AH_CLI_SUFFIX;
		}
	}

	public static String getConfigGatewayCli(String gateWay) {
		return "ip route default gateway " + gateWay + AH_CLI_SUFFIX;
	}

	public static String getConfigDnsCli(String dnsIp) {
		if (dnsIp == null || "".equals(dnsIp)) {
			return "no dns server-ip " + AH_CLI_SUFFIX;
		} else {
			return "dns server-ip " + dnsIp + AH_CLI_SUFFIX;
		}
	}

	public static String getOutdoorSettingsCli(boolean isOutdoor) {
		String door = "indoor";
		if (isOutdoor) {
			door = "outdoor";
		}
		return "system environment " + door + " " + AH_CLI_SUFFIX;
	}

	/**
	 * UPD connectivity
	 * 
	 * @param hmolServerAddr
	 *            : hmol server ip or domain name
	 * @param timeOut
	 *            : 1-60seconds
	 * @return
	 */
	public static String getUdpTestCli(String hmolServerAddr, int port,
			int timeOut) {
		return "capwap ping " + hmolServerAddr + " port " + port + " timeout "
				+ timeOut + AH_CLI_SUFFIX;
	}

	/**
	 * TCP connectivity
	 * 
	 * @param hmolServerAddr
	 *            : hmol server ip
	 * @param port
	 *            : 1-65535
	 * @param timeOut
	 *            : 1-60seconds
	 * @return
	 */
	public static String getTcpTestCli(String hmolServerAddr, int port,
			int timeOut) {
		return "exec _test tcp-service host " + hmolServerAddr + " port "
				+ port + " timeout " + timeOut + AH_CLI_SUFFIX;
	}

	/**
	 * MDM enroll status
	 * 
	 * @param mac
	 *            :the format is like this: 1111:1111:1111
	 * @return
	 */
	public static String getEnrollStatusCli(String formatMac) {
		return "exec jss-check mobile-device " + formatMac + " enroll-status "
				+ AH_CLI_SUFFIX;
	}

	/**
	 * 
	 * @return
	 */
	public static String getOTPRevokeResetCli() {
		return "reset config no-prompt" + AH_CLI_SUFFIX;
	}

	/**
	 * OTP Revoke send cvg cli
	 * 
	 * @param macAddress
	 * @param clientName
	 * @return
	 */
	public static String getOTPRevokeCvgCli(String macAddress, String clientName) {
		return "no vpn xauth-client-list " + macAddress
				+ VPNProfileImpl.clientListSuffix + " client-name "
				+ clientName + AH_CLI_SUFFIX;
	}

	public static String getRemoveBjgwNeighborCli(String neighbor) {
		if (neighbor == null || neighbor.isEmpty()) {
			return "no bonjour-gateway neighbor" + AH_CLI_SUFFIX;
		} else {
			return "no bonjour-gateway neighbor " + neighbor + AH_CLI_SUFFIX;
		}
	}

	public static String getRemoveBjgwNeighborCli() {
		return "no bonjour-gateway neighbor" + AH_CLI_SUFFIX;
	}

	public static String getSetRealmNameCli(String name) {
		return "bonjour-gateway realm " + name + AH_CLI_SUFFIX;
	}

	public static String getSetBjgwNeighborCli(String neighbor) {
		return "bonjour-gateway neighbor " + neighbor + AH_CLI_SUFFIX;
	}

	/**
	 * This CLI is used to test the TLS connectivity from RadSec or AUTH proxy
	 * to ID Manager gateway
	 * 
	 * @param mode
	 * @return
	 */
	public static String sendIdmTestRequest(String mode) {
		return "exec aaa idm-test " + mode + "-proxy" + AH_CLI_SUFFIX;
	}
	
	/**
	 * <p>
	 * Generate DELTS message to sta with <MAC address> and  TID <num> of SSID <string>
	 * </p>
	 *
	 * @param ssid
	 *            SSID profile name
	 * @param macAddress
	 *            client MAC address
	 * @param tid
	 *            tid
	 * @return a cli of WMM-AC DTLS 
	 */
	public static String deleteTsInfo(String ssid,
			String macAddress, int tid) {
		return "ssid " + ssid + " admctl delts sta " + macAddress + " tid " + tid + AH_CLI_SUFFIX;
	}
	
	/**
	 * <p>
	 * Show TS info for this STA that is associated to the SSID.
	 * </p>
	 * 
	 * @param ssid
	 * 			SSID profile name
	 * @param macAddress
	 * 			client MAC address
	 * @return a cli of show TS info
	 */
	public static String getTsInfoForSSID(String ssid, String macAddress){
		return "show ssid " + ssid + " admctl tsinfo sta " + macAddress + AH_CLI_SUFFIX;
	} 

	/**
	 * clear switch port counters
	 * 
	 * 
	 * @return
	 */
	public static String clearInterfaceCounters() {
		return "clear interface _counters " + AH_CLI_SUFFIX;
	}
}