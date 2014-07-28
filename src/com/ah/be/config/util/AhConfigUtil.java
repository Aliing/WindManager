/**
 *@filename		AhConfigUtil.java
 *@version
 *@author		Francis
 *@createtime	2008-3-13 11:35:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.config.util;

import java.io.File;

import md5.MD5Crypt;

import com.ah.be.common.AhDirTools;
import com.ah.util.Tracer;
import com.ah.util.ahdes.AhCliSec;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public final class AhConfigUtil {

	private static final Tracer log = new Tracer(AhConfigUtil.class.getSimpleName());

	private static final char[] AH_AP_USER_PASSWORD_SUBJOIN = { 's', 'o', 'r', 'e', 'a' };

	private static final String AH_MD5_CRYPT_SALT = "$1$";

	/** File suffixes for relevant bootstrap configs */
	private static final String AH_BOOTSTRAP_XML_CONFIG_SUFFIX = "_bootstrap.xml";

	private static final String AH_BOOTSTRAP_CONFIG_SUFFIX = ".bootstrap";

	/** File suffixes for relevant full configs */
	private static final String AH_FULL_NEW_XML_CONFIG_SUFFIX = "_full_new.xml";

	private static final String AH_FULL_OLD_XML_CONFIG_SUFFIX = "_full_old.xml";

	private static final String AH_FULL_RUN_XML_CONFIG_SUFFIX = "_full_run.xml";

	private static final String AH_FULL_NEW_CONFIG_SUFFIX = "_full_new.config";

	private static final String AH_FULL_RUN_CONFIG_SUFFIX = "_full_run.config";
	
	private static final String AH_FULL_CPARSE_IGNORE_CONFIG_SUFFIX = "_full_cparse_ignore.config";

	/** File suffixes for relevant user configs */
	private static final String AH_USER_NEW_XML_CONFIG_SUFFIX = "_user_new.xml";

	private static final String AH_USER_OLD_XML_CONFIG_SUFFIX = "_user_old.xml";

	private static final String AH_USER_RUN_XML_CONFIG_SUFFIX = "_user_run.xml";

	private static final String AH_USER_NEW_CONFIG_SUFFIX = "_user_new.config";

	private static final String AH_USER_RUN_CONFIG_SUFFIX = "_user_run.config";
	
	private static final String AH_USER_CPARSE_IGNORE_CONFIG_SUFFIX = "_user_cparse_ignore.config";

	/* Relevant bootstrap names */
	public static String getBootstrapXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_BOOTSTRAP_XML_CONFIG_SUFFIX;
	}

	public static String getBootstrapConfigName(String hiveApMac) {
		return hiveApMac + AH_BOOTSTRAP_CONFIG_SUFFIX;
	}

	/* Relevant full config names */
	public static String getFullNewXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_NEW_XML_CONFIG_SUFFIX;
	}

	public static String getFullOldXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_OLD_XML_CONFIG_SUFFIX;
	}

	public static String getFullRunXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_RUN_XML_CONFIG_SUFFIX;
	}

	public static String getFullNewConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_NEW_CONFIG_SUFFIX;
	}

	public static String getFullRunConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_RUN_CONFIG_SUFFIX;
	}
	
	public static String getFullCParseIgnoreConfigName(String hiveApMac) {
		return hiveApMac + AH_FULL_CPARSE_IGNORE_CONFIG_SUFFIX;
	}

	/* Relevant user config names */
	public static String getUserNewXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_NEW_XML_CONFIG_SUFFIX;
	}

	public static String getUserOldXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_OLD_XML_CONFIG_SUFFIX;
	}

	public static String getUserRunXmlConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_RUN_XML_CONFIG_SUFFIX;
	}

	public static String getUserNewConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_NEW_CONFIG_SUFFIX;
	}

	public static String getUserRunConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_RUN_CONFIG_SUFFIX;
	}
	
	public static String getUserCParseIgnoreConfigName(String hiveApMac) {
		return hiveApMac + AH_USER_CPARSE_IGNORE_CONFIG_SUFFIX;
	}

	/* Relevant bootstrap config paths */
	public static String getBootstrapXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getBootstrapXmlConfigDir(domainName)
				+ getBootstrapXmlConfigName(hiveApMac);
	}

	public static String getBootstrapConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getBootstrapConfigDir(domainName) + getBootstrapConfigName(hiveApMac);
	}

	/* Relevant full config paths */
	public static String getFullNewXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getNewXmlConfigDir(domainName) + getFullNewXmlConfigName(hiveApMac);
	}

	public static String getFullOldXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getOldXmlConfigDir(domainName) + getFullOldXmlConfigName(hiveApMac);
	}

	public static String getFullRunXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunXmlConfigDir(domainName) + getFullRunXmlConfigName(hiveApMac);
	}

	public static String getFullNewConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getNewConfigDir(domainName) + getFullNewConfigName(hiveApMac);
	}

	public static String getFullRunConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunConfigDir(domainName) + getFullRunConfigName(hiveApMac);
	}
	
	public static String getFullCParseIgnoreConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunConfigDir(domainName) + getFullCParseIgnoreConfigName(hiveApMac);
	}

	/* Relevant user config paths */
	public static String getUserNewXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getNewXmlConfigDir(domainName) + getUserNewXmlConfigName(hiveApMac);
	}

	public static String getUserOldXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getOldXmlConfigDir(domainName) + getUserOldXmlConfigName(hiveApMac);
	}

	public static String getUserRunXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunXmlConfigDir(domainName) + getUserRunXmlConfigName(hiveApMac);
	}

	public static String getUserNewConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getNewConfigDir(domainName) + getUserNewConfigName(hiveApMac);
	}

	public static String getUserRunConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunConfigDir(domainName) + getUserRunConfigName(hiveApMac);
	}
	
	public static String getUserCParseIgnoreConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getRunConfigDir(domainName) + getUserCParseIgnoreConfigName(hiveApMac);
	}

	/* Relevant view-based full XML-formatted config paths */
	public static String getViewBasedFullNewXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getFullNewXmlConfigName(hiveApMac);
	}

	public static String getViewBasedFullRunXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getFullRunXmlConfigName(hiveApMac);
	}
	
	public static String getViewFullCParseIgnoreConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getFullCParseIgnoreConfigName(hiveApMac);
	}

	/* Relevant view-based user XML-formatted config paths */
	public static String getViewBasedUserNewXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getUserNewXmlConfigName(hiveApMac);
	}

	public static String getViewBasedUserRunXmlConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getUserRunXmlConfigName(hiveApMac);
	}
	
	public static String getViewUserCParseIgnoreConfigPath(String domainName, String hiveApMac) {
		return AhDirTools.getViewBasedXmlConfigDir(domainName) + getUserCParseIgnoreConfigName(hiveApMac);
	}

	public static boolean fullOldXmlConfigExists(String domainName, String hiveApMac) {
		return new File(getFullOldXmlConfigPath(domainName, hiveApMac)).exists();
	}

	public static boolean userOldXmlConfigExists(String domainName, String hiveApMac) {
		return new File(getUserOldXmlConfigPath(domainName, hiveApMac)).exists();
	}

	/* Remove relevant bootstrap configs */
	public static boolean removeBootstrapXmlConfig(String domainName, String hiveApMac) {
		String filePath = getBootstrapXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeBootstrapXmlConfig", "[" + hiveApMac
					+ "]The bootstrap XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeBootstrapConfig(String domainName, String hiveApMac) {
		String filePath = getBootstrapConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeBootstrapConfig", "[" + hiveApMac
					+ "]The bootstrap config was removed.");
		}

		return isRemoved;
	}

	/* Remove relevant full configs */
	public static boolean removeFullNewXmlConfig(String domainName, String hiveApMac) {
		String filePath = getFullNewXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeFullNewXmlConfig", "[" + hiveApMac
					+ "]The full new XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeFullOldXmlConfig(String domainName, String hiveApMac) {
		String filePath = getFullOldXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeFullOldXmlConfig", "[" + hiveApMac
					+ "]The full old XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeFullRunXmlConfig(String domainName, String hiveApMac) {
		String filePath = getFullRunXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeFullRunXmlConfig", "[" + hiveApMac
					+ "]The full running XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeFullNewConfig(String domainName, String hiveApMac) {
		String filePath = getFullNewConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeFullNewConfig", "[" + hiveApMac + "]The full new config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeFullRunConfig(String domainName, String hiveApMac) {
		String filePath = getFullRunConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeFullRunConfig", "[" + hiveApMac
					+ "]The full running config was removed.");
		}

		return isRemoved;
	}

	/* Remove relevant user configs */
	public static boolean removeUserNewXmlConfig(String domainName, String hiveApMac) {
		String filePath = getUserNewXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeUserNewXmlConfig", "[" + hiveApMac
					+ "]The user new XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeUserOldXmlConfig(String domainName, String hiveApMac) {
		String filePath = getUserOldXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeUserOldXmlConfig", "[" + hiveApMac
					+ "]The user old XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeUserRunXmlConfig(String domainName, String hiveApMac) {
		String filePath = getUserRunXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeUserRunXmlConfig", "[" + hiveApMac
					+ "]The user running XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeUserNewConfig(String domainName, String hiveApMac) {
		String filePath = getUserNewConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeUserNewConfig", "[" + hiveApMac + "]The user new config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeUserRunConfig(String domainName, String hiveApMac) {
		String filePath = getUserRunConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeUserRunConfig", "[" + hiveApMac
					+ "]The user running config was removed.");
		}

		return isRemoved;
	}

	/* Remove view-based full XML-formatted configs */
	public static boolean removeViewBasedFullNewXmlConfig(String domainName, String hiveApMac) {
		String filePath = getViewBasedFullNewXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeViewBasedFullNewXmlConfig", "[" + hiveApMac
					+ "]The view-based full new XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeViewBasedFullRunXmlConfig(String domainName, String hiveApMac) {
		String filePath = getViewBasedFullRunXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeViewBasedFullRunXmlConfig", "[" + hiveApMac
					+ "]The view-based full running XML-formatted config was removed.");
		}

		return isRemoved;
	}

	/* Remove view-based user XML-formatted configs */
	public static boolean removeViewBasedUserNewXmlConfig(String domainName, String hiveApMac) {
		String filePath = getViewBasedUserNewXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeViewBasedUserNewXmlConfig", "[" + hiveApMac
					+ "]The view-based user new XML-formatted config was removed.");
		}

		return isRemoved;
	}

	public static boolean removeViewBasedUserRunXmlConfig(String domainName, String hiveApMac) {
		String filePath = getViewBasedUserRunXmlConfigPath(domainName, hiveApMac);
		boolean isRemoved = removeFile(filePath);

		if (isRemoved) {
			log.info("removeViewBasedUserRunXmlConfig", "[" + hiveApMac
					+ "]The view-based user running XML-formatted config was removed.");
		}

		return isRemoved;
	}

	/* Remove relevant bootstrap configs */
	public static void removeRelevantBootstrapConfigs(String domainName, String hiveApMac) {
		// Bootstrap XML-formatted config.
		removeBootstrapXmlConfig(domainName, hiveApMac);

		// Bootstrap config.
		removeBootstrapConfig(domainName, hiveApMac);
	}

	/* Remove relevant full configs */
	public static void removeRelevantFullConfigs(String domainName, String hiveApMac) {
		// Full new XML-formatted config.
		removeFullNewXmlConfig(domainName, hiveApMac);

		// Full old XML-formatted config.
		removeFullOldXmlConfig(domainName, hiveApMac);

		// Full running XML-formatted config.
		removeFullRunXmlConfig(domainName, hiveApMac);

		// Full new config.
		removeFullNewConfig(domainName, hiveApMac);

		// Full running config.
		removeFullRunConfig(domainName, hiveApMac);
	}

	/* Remove relevant user configs */
	public static void removeRelevantUserConfigs(String domainName, String hiveApMac) {
		// User new XML-formatted config.
		removeUserNewXmlConfig(domainName, hiveApMac);

		// User old XML-formatted config.
		removeUserOldXmlConfig(domainName, hiveApMac);

		// User running XML-formatted config.
		removeUserRunXmlConfig(domainName, hiveApMac);

		// User new config.
		removeUserNewConfig(domainName, hiveApMac);

		// User running config.
		removeUserRunConfig(domainName, hiveApMac);
	}

	/* Remove relevant view-based configs */
	public static void removeRelevantViewBasedConfigs(String domainName, String hiveApMac) {
		// View-based full new XML-formatted config.
		removeViewBasedFullNewXmlConfig(domainName, hiveApMac);

		// View-based full running XML-formatted config.
		removeViewBasedFullRunXmlConfig(domainName, hiveApMac);

		// View-based user new XML-formatted config.
		removeViewBasedUserNewXmlConfig(domainName, hiveApMac);

		// View-based user running XML-formatted config.
		removeViewBasedUserRunXmlConfig(domainName, hiveApMac);
	}

	public static void removeAllHiveApConfigs(String domainName, String hiveApMac) {
		log.info("removeAllHiveApConfigs", "[" + hiveApMac
				+ "]Removing all HiveAP related configs.");

		// Relevant bootstrap configs.
		removeRelevantBootstrapConfigs(domainName, hiveApMac);

		// Relevant full configs.
		removeRelevantFullConfigs(domainName, hiveApMac);

		// Relevant user configs.
		removeRelevantUserConfigs(domainName, hiveApMac);

		// Relevant view-based configs.
		removeRelevantViewBasedConfigs(domainName, hiveApMac);

		log.info("removeAllHiveApConfigs", "[" + hiveApMac
				+ "]All configs related to HiveAP were removed.");
	}

	/**
	 * <p>
	 * Calculate new config version number.
	 * </p>
	 * 
	 * The number <tt>1</tt> is the initialized config version, so the new
	 * generated value starts from 2 to avoid conflicting with the
	 * initialization.
	 * 
	 * @param previousVerNum
	 *            the previous config version number.
	 * @return new config version.
	 */
	public static int computeNewConfigVerNum(int previousVerNum) {
		return previousVerNum > 0 && previousVerNum < Integer.MAX_VALUE ? previousVerNum + 1 : 2;
	}

	/**
	 * <p>
	 * Encrypt HiveAP user password with linux md5 crypt algorithm.
	 * </p>
	 * 
	 * @param plainText
	 *            the plain text password.
	 * @return an encrypted string with fixed 26 hex characters.
	 */
	public static String hiveApUserPwdEncrypt(String plainText) {
		String crypt = MD5Crypt.crypt(plainText, AH_MD5_CRYPT_SALT);
		StringBuilder buf = new StringBuilder();
		int length = AH_AP_USER_PASSWORD_SUBJOIN.length;

		for (int i = 0; i < length + 1; i++) {
			int pos = i * 5;

			if (i != length) {
				buf.append(crypt.substring(pos, pos + 5)).append(AH_AP_USER_PASSWORD_SUBJOIN[i]);
			} else {
				buf.append(crypt.substring(pos));
			}
		}

		return buf.toString().substring(4);
	}

	public static String hiveApCommonEncrypt(String plainText) {
		if (plainText == null) {
			throw new IllegalArgumentException("Illegal argument - " + plainText + " passed.");
		}

		if (plainText.trim().equals("")) {
			return plainText;
		}

		return AhCliSec.ah_encrypt(plainText);
	}

	/**
	 * <p>
	 * Encrypt HiveAP user password with linux md5 crypt algorithm.
	 * </p>
	 * 
	 * @param plainText
	 *            the plain text password.
	 * @return an encrypted string with 26 hex characters.
	 */
	public static String ahUserPwdEncrypt(String plainText) {
		if (plainText == null || plainText.trim().isEmpty()) {
			throw new IllegalArgumentException("Illegal argument - " + plainText + " passed.");
		}

		String crypt = MD5Crypt.crypt(plainText, AH_MD5_CRYPT_SALT);
		StringBuilder buf = new StringBuilder();
		int length = AH_AP_USER_PASSWORD_SUBJOIN.length;

		for (int i = 0; i < length + 1; i++) {
			int pos = i * 5;

			if (i != length) {
				buf.append(crypt.substring(pos, pos + 5)).append(AH_AP_USER_PASSWORD_SUBJOIN[i]);
			} else {
				buf.append(crypt.substring(pos));
			}
		}

		return buf.toString().substring(4);
	}

	public static String ahEncrypt(String plainText) {
		if (plainText == null) {
			throw new IllegalArgumentException("Illegal argument - " + plainText + " passed.");
		}

		if (plainText.trim().isEmpty()) {
			return plainText;
		}

		return AhCliSec.ah_encrypt(plainText);
	}

	private static boolean removeFile(String filePath) {
		File file = new File(filePath);

		return file.exists() && file.delete();
	}

}