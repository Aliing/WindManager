/**
m *@filename		RestoreLicenseAndActivation.java
 *@version
 *@author		Fiona
 *@createtime	Apr 24, 2009 4:44:21 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class RestoreLicenseAndActivation {
	
	// for license if contains active flag
	public static boolean	ifContainsActiveLicense	= false;
	
	// ---------------------- License History Info -------------START---------
	public static boolean restoreLicenseHistoryInfo() {
		try {
			List<LicenseHistoryInfo> info = getLicenseHistoryInfo();
			if (!ifContainsActiveLicense) {
				if ((new File(BeLicenseModule.LICENSE_FILE_NAME)).exists()) {
					String existLic = "";
					try {
						// get the license key in the license file
						BufferedReader readerLicense = new BufferedReader(new FileReader(
								BeLicenseModule.LICENSE_FILE_NAME));
						if (readerLicense.ready()) {
							String line;
							// replace "\n" with "-"
							while ((line = readerLicense.readLine()) != null) {
								existLic += line + "-";
							}
							existLic = existLic.substring(0, existLic.length() - 1);
							readerLicense.close();
						}
					} catch (Exception e) {
						BeLogTools
								.restoreLog(BeLogTools.ERROR, 
										"RestoreAdmin.restoreLicenseHistoryInfo() catch exception when read license file",
										e);
						return false;
					}
					HM_License hm_l = HM_License.getInstance();
					String systemId = hm_l.get_system_id();
					String str_License = hm_l.decrypt_from_file(systemId,
							BeLicenseModule.LICENSE_FILE_NAME);
					// the license key must be valid
					if (str_License != null
							&& (str_License.length() == BeLicenseModule.LICENSE_KEY_LENGTH || str_License
									.length() == BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH)) {
						LicenseHistoryInfo licenseInfo = new LicenseHistoryInfo();
						licenseInfo.setSystemId(systemId);
						licenseInfo.setLicenseString(existLic);
						licenseInfo.setActive(true);
						licenseInfo.setType(LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER);

						// get the field of hoursused if evaluation or vmvare license, it is 0 if
						// permanent license
						int usedHours = getUsedHours();
						AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(systemId);

						// encrypt license hours
						String hoursUsed = encryptTool
								.encrypt(String.valueOf(usedHours) + systemId);
						licenseInfo.setHoursUsed(hoursUsed);
						if (null == info) {
							info = new ArrayList<LicenseHistoryInfo>();
						}
						info.add(licenseInfo);
					}
				}
			}
			if (null != info) {
				QueryUtil.bulkCreateBos(info);
			}
			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR, 
					"RestoreAdmin.restoreLicenseHistoryInfo() catch exception ", e);
			return false;
		}
	}

	/**
	 * Get all information from license_history_info table
	 * 
	 * @return List<LicenseHistoryInfo> all LicenseHistoryInfo BO
	 * @throws AhRestoreException -
	 *             if error in parsing license_history_info.xml.
	 */
	private static List<LicenseHistoryInfo> getLicenseHistoryInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		LicenseHistoryInfo licenseInfo;
		List<LicenseHistoryInfo> allInfo = new ArrayList<LicenseHistoryInfo>();

		/**
		 * Check validation of license_history_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("license_history_info");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in license_history_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {
			licenseInfo = new LicenseHistoryInfo();

			/**
			 * Set systemid
			 */
			colName = "systemid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_history_info",
					colName);
			String systemid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			licenseInfo.setSystemId(systemid);

			/**
			 * Set licensestring
			 */
			colName = "licensestring";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_history_info",
					colName);
			String licensestring = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			licenseInfo.setLicenseString(licensestring);

			/**
			 * Set active
			 */
			colName = "active";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_history_info",
					colName);
			boolean active = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			licenseInfo.setActive(active);
			if (active) {
				ifContainsActiveLicense = true;
			}

			/**
			 * Set hoursused
			 */
			colName = "hoursused";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_history_info",
					colName);
			String hoursused = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			licenseInfo.setHoursUsed(hoursused);
			
			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_history_info",
					colName);
			short type = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER;
			licenseInfo.setType(type);

			allInfo.add(licenseInfo);
		}

		return allInfo.isEmpty() ? null : allInfo;
	}

	/**
	 * Get the MacAddress.
	 * 
	 * @return MacAddress -
	 */
	private static String getMacAddress() {
		String str_Address = "";
		try {
			Process process = Runtime.getRuntime().exec("ifconfig");
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			if ((line = input.readLine()) != null)
				if (line.indexOf("HWaddr") > 0) {
					str_Address = line.substring(line.indexOf("HWaddr") + 7);
					if (!str_Address.equals("")) {
						str_Address = str_Address.replaceAll(":", "");
						if (str_Address.contains("\\")) {
							StringBuffer strbuf = new StringBuffer();
							StringTokenizer st = new StringTokenizer(str_Address, "\\");
							while (st.hasMoreTokens()) {
								strbuf.append(st.nextToken());
							}
							str_Address = strbuf.toString();
						}
					}
				}
		} catch (IOException e) {
			BeLogTools.restoreLog(BeLogTools.ERROR, "getMacAddress : " + e.getMessage());
			return "";
		}
		return str_Address.trim();
	}

	/**
	 * Get the used hours by evaluation or vmware customers.
	 * 
	 * @return the used hours in file. return 0 if there is any error
	 */
	private static int getUsedHours() {
		int usedHours = 0;
		if (new File(BeLicenseModule.EVALUATION_LICENSE_FILE_NAME).exists()) {
			try {
				// the hours which is how long user can use
				String[] str = FileManager.getInstance().readFile(
						BeLicenseModule.EVALUATION_LICENSE_FILE_NAME);
				if (null != str && str.length == 1) {
					AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(getMacAddress());
					String leaftHours = encryptTool.decrypt(str[0]);

					// check the hours string
					if (null != leaftHours && leaftHours.endsWith(getMacAddress())) {
						return Integer.parseInt(leaftHours.substring(0, leaftHours
								.indexOf(getMacAddress())));
					} else {
						BeLogTools.restoreLog(BeLogTools.ERROR, "The hours is destroyed : " + leaftHours
								+ ".The Mac is " + getMacAddress());
					}
				} else {
					BeLogTools.restoreLog(BeLogTools.ERROR, "The hours file is destroyed : "
							+ (null == str ? "null" : str.length));
				}
			} catch (Exception e) {
				BeLogTools.restoreLog(BeLogTools.ERROR, "There is something wrong with the hours file : "
						+ e.getMessage());
			}
		}
		return usedHours;
	}

	// ---------------------- License History Info -------------END---------
	
	// ---------------------- Activation Key Info -------------Start---------
	public static boolean restoreActivationKeyInfo() {
		try {
			List<ActivationKeyInfo> activation = getActivationKeyInfo();

			if(null != activation) {
				QueryUtil.bulkCreateBos(activation);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from activation_key_info table
	 * 
	 * @return ActivationKeyInfo
	 * @throws AhRestoreException -
	 *             if error in parsing activation_key_info.xml.
	 */
	private static List<ActivationKeyInfo> getActivationKeyInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of license_history_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("activation_key_info");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in activation_key_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		ActivationKeyInfo activeInfo;
		List<ActivationKeyInfo> allInfo = new ArrayList<ActivationKeyInfo>();
		
		for (int i = 0; i < rowCount; i++) {
			 activeInfo = new ActivationKeyInfo();
			/**
			 * Set activationkey
			 */
			colName = "activationkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			String activeKey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			activeInfo.setActivationKey(activeKey);

			/**
			 * Set queryperiod
			 */
			colName = "queryperiod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			int period = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				15;
			activeInfo.setQueryPeriod(period);

			/**
			 * Set activateSuccess
			 */
			colName = "activateSuccess";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			boolean ifAcSuccess = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			activeInfo.setActivateSuccess(ifAcSuccess);
			
			/**
			 * Set queryinterval
			 */
			colName = "queryinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			int interval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 3;
			activeInfo.setQueryInterval(interval);
			
			/**
			 * Set queryretrytime
			 */
			colName = "queryretrytime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			int retry = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 3;
			activeInfo.setQueryRetryTime((byte)retry);
			
			/**
			 * Set startretrytimer
			 */
			colName = "startretrytimer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			boolean ifStartRetry = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			activeInfo.setStartRetryTimer(ifStartRetry);
			
			/**
			 * Set hasretrytime
			 */
			colName = "hasretrytime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			int hasretrytime = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			activeInfo.setHasRetryTime((byte)hasretrytime);
			
			/**
			 * Set hoursused
			 */
			colName = "hoursused";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			String hoursused = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			activeInfo.setHoursUsed(hoursused);
			
			/**
			 * Set systemid
			 */
			colName = "systemid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "activation_key_info",
					colName);
			String systemid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			activeInfo.setSystemId(systemid);
			
			allInfo.add(activeInfo);
		}

		return allInfo.isEmpty() ? null : allInfo;
	}
	// ---------------------- Activation Key Info -------------END---------
	
	// ---------------------- License Server Setting -------------Start---------
	public static boolean restoreLicenseServerSetting() {
		try {
			LicenseServerSetting lsSet = getLicenseServerSetting();
			if(null != lsSet) {
				LicenseServerSetting lserverInfo = HmBeActivationUtil.getLicenseServerInfo();
				
				// update license server setting
				if (null != lserverInfo) {
					lserverInfo.setLserverUrl(lsSet.getLserverUrl());
					lserverInfo.setSendStatistic(lsSet.isSendStatistic());
					lserverInfo.setAvailableSoftToUpdate(false);
					QueryUtil.updateBo(lserverInfo);
				
				// create license server setting
				} else {
					QueryUtil.createBo(lsSet);
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from license_server_setting table
	 * 
	 * @return LicenseServerSetting
	 * @throws AhRestoreException -
	 *             if error in parsing license_server_setting.xml.
	 */
	private static LicenseServerSetting getLicenseServerSetting() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of license_server_setting.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("license_server_setting");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in license_server_setting table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		if (1 != rowCount) {
			return null;
		}

		boolean isColPresent;
		String colName;
		LicenseServerSetting lsSetting = new LicenseServerSetting();

		/**
		 * Set lserverurl
		 */
		colName = "lserverurl";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_server_setting",
				colName);
		String lserverurl = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(0,
				colName)) : "";
		if (NmsUtil.isHMForOEM() && (LicenseServerSetting.DEFAULT_LICENSE_SERVER_URL.equals(lserverurl)
			|| "bbupdates.blackbox.com".equals(lserverurl))) {
			lserverurl = NmsUtil.getOEMCustomer().getDefaultLsUrl();
		}
		lsSetting.setLserverUrl(lserverurl);
		
		/**
		 * Set sendstatistic
		 */
		colName = "sendstatistic";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_server_setting",
				colName);
		boolean sendstatistic = isColPresent
				&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(0, colName));
		lsSetting.setSendStatistic(sendstatistic);
		
		// init the flag of available software to upgrade
		lsSetting.setAvailableSoftToUpdate(false);
		
		/**
		 * Set hoursused
		 */
		colName = "hoursused";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "license_server_setting",
				colName);
		int hoursused = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(0,
				colName)) : 0;
		lsSetting.setHoursUsed(hoursused);
		
		/**
		 * set owner
		 */
		colName = "owner";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"license_server_setting", colName);
		long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(0, colName)) : 1;
		if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
		{
			BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'license_server_setting' data be lost, cause: 'owner' column is not available.");
		   return null;
		}
		lsSetting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

		return lsSetting;
	}
	// ---------------------- License Server Setting -------------END---------

}