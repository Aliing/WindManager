package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.OneTimePassword;

public class RestoreOneTimePassword {
	public static final String tableName = "onetime_password";

	private static List<OneTimePassword> getAllOneTimePasswords() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of onetime_password.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in onetime_password table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<OneTimePassword> otpInfo = new ArrayList<OneTimePassword>();
		boolean isColPresent;
		String colName;
		OneTimePassword otp;

		for (int i = 0; i < rowCount; i++) {
			otp = new OneTimePassword();

			/**
			 * Set oneTimePassword
			 */
			colName = "oneTimePassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'onetime_password' data be lost, cause: 'oneTimePassword' column is not exist.");
				/**
				 * The oneTimePassword column must be exist in the table of
				 * onetime_password
				 */
				continue;
			}

			String oneTimePassword = xmlParser.getColVal(i, colName);
			if (oneTimePassword == null || oneTimePassword.trim().equals("")
					|| oneTimePassword.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'onetime_password' data be lost, cause: 'oneTimePassword' column value is null.");
				continue;
			}
			otp.setOneTimePassword(oneTimePassword.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'onetime_password' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of onetime_password
				 */
				continue;
			}
			String id = xmlParser.getColVal(i, colName);
			if (id == null || id.trim().equals("")
					|| id.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'onetime_password' data be lost, cause: 'id' column value is null.");
				continue;
			}
			otp.setId(Long.valueOf(id));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'onetime_password' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			otp.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set username
			 */
			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String userName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setUserName(AhRestoreCommons.convertString(userName));

			/**
			 * Set emailAddress
			 */
			colName = "emailAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String emailAddress = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setEmailAddress(AhRestoreCommons.convertString(emailAddress));

			/**
			 * Set dateSentStamp
			 */
			colName = "dateSentStamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dateSentStamp = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setDateSentStamp(AhRestoreCommons.convertLong(dateSentStamp));

			/**
			 * Set dateActivateStamp
			 */
			colName = "dateActivateStamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String dateActivateStamp = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setDateActivateStamp(AhRestoreCommons.convertLong(dateActivateStamp));

			/**
			 * Set deviceModel
			 */
			colName = "deviceModel";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String deviceModel = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setDeviceModel((short)AhRestoreCommons.convertInt(deviceModel));

			/**
			 * Set description
			 */
			colName = "macAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String macAddress = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setMacAddress(AhRestoreCommons.convertString(macAddress));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			otp.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set hiveApAutoProvision
			 */
			colName = "hiveApAutoProvision";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String hiveApAutoProvisionId = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (hiveApAutoProvisionId != null && !(hiveApAutoProvisionId.trim().equals(""))
							&& !(hiveApAutoProvisionId.trim().equalsIgnoreCase("null"))){
				otp.setHiveApAutoProvision(AhRestoreNewMapTools.getMapHiveApAutoProvision(AhRestoreCommons.convertLong(hiveApAutoProvisionId)));
			}


			otpInfo.add(otp);
		}
		return otpInfo;
	}


	public static boolean restoreOneTimePasswords() {
		try {
			long start = System.currentTimeMillis();

			List<OneTimePassword> allOTPS = getAllOneTimePasswords();

			if (null == allOTPS) {
				AhRestoreDBTools.logRestoreMsg("OneTime Password is null");
			} else {
				List<Long> oldIdList = new ArrayList<Long>(allOTPS
						.size());
				for (OneTimePassword otp : allOTPS) {
					if (otp != null) {
						oldIdList.add(otp.getId());
						otp.setId(null);// set id to null
					}
				}
				QueryUtil.restoreBulkCreateBos(allOTPS);
				// set id mapping to map tool.
				for (int i = 0; i < allOTPS.size(); i++) {
					AhRestoreNewMapTools.setMapOneTimePassword(oldIdList.get(i),
							allOTPS.get(i).getId());
				}
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore OneTime Password completely. cost:"
							+ (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore OneTime Password error.", e);
			return false;
		}
		return true;
	}
}
