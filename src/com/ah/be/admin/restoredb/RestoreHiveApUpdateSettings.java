package com.ah.be.admin.restoredb;

//import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.hiveap.HiveApUpdateSettings.ActivateType;
import com.ah.bo.hiveap.HiveApUpdateSettings.ConfigSelectType;
import com.ah.bo.hiveap.HiveApUpdateSettings.ImageSelectionType;
import com.ah.bo.hiveap.HiveApUpdateSettings.TransferType;
import com.ah.bo.mgmt.QueryUtil;
//import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
//import com.ah.be.common.NmsUtil;

public class RestoreHiveApUpdateSettings {

	public static boolean restoreHiveApUpdateSettings() {
		try {
			List<HiveApUpdateSettings> settings = getAllHiveApUpdateSettings();
			if (null == settings) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(settings);
			}
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Restore HiveAP update settings finished.");
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_RESTORE,
					"Restore HiveAP update settings catch exception ", e);
			return false;
		}
		return true;
	}

	private static List<HiveApUpdateSettings> getAllHiveApUpdateSettings()
			throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hive_ap_update_settings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hive_ap_update_settings");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read hive_ap_update_settings.xml file.");
			return null;
		}

		/**
		 * No one row data stored in hive_ap_update_settings table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApUpdateSettings> settings = new ArrayList<HiveApUpdateSettings>();

		boolean isColPresent;
		String colName;
		HiveApUpdateSettings setting;
		for (int i = 0; i < rowCount; i++) {
			try {
				setting = new HiveApUpdateSettings();

				/**
				 * Set configactivateoffset
				 */
				colName = "configactivateoffset";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long configactivateoffset = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.DEFAULT_IMAGE_ACTIVATE_OFFSET);
				setting.setConfigActivateOffset(configactivateoffset);

				/**
				 * Set configactivatetime
				 */
				colName = "configactivatetime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long configactivatetime = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 0);
				setting.setConfigActivateTime(configactivatetime);

				/**
				 * Set configactivatetype
				 */
				colName = "configactivatetype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configactivatetype = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				ActivateType type2 = AhRestoreCommons.convertStringToEnum(
						ActivateType.class, configactivatetype,
						ActivateType.activateNextTime);
				setting.setConfigActivateType(type2);

				/**
				 * Set configcertificate
				 */
				colName = "configcertificate";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configcertificate = isColPresent ? xmlParser.getColVal(
						i, colName) : "true";
				setting.setConfigCertificate(AhRestoreCommons
						.convertStringToBoolean(configcertificate));

				/**
				 * Set configconfiguration
				 */
				colName = "configconfiguration";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configconfiguration = isColPresent ? xmlParser
						.getColVal(i, colName) : "true";
				setting.setConfigConfiguration(AhRestoreCommons
						.convertStringToBoolean(configconfiguration));

				/**
				 * Set configcwp
				 */
				colName = "configcwp";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configcwp = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				setting.setConfigCwp(AhRestoreCommons
						.convertStringToBoolean(configcwp));

				/**
				 * Set configPpsk
				 */
				colName = "configPpsk";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configPpsk = isColPresent ? xmlParser.getColVal(i,
						colName) : "true";
				setting.setConfigCwp(AhRestoreCommons
						.convertStringToBoolean(configPpsk));

				/**
				 * Set configselecttype
				 */
				colName = "configselecttype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configselecttype = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				ConfigSelectType type1 = AhRestoreCommons.convertStringToEnum(
						ConfigSelectType.class, configselecttype,
						ConfigSelectType.auto);
				setting.setConfigSelectType(type1);

				/**
				 * Set configuserdatabase
				 */
				colName = "configuserdatabase";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String configuserdatabase = isColPresent ? xmlParser.getColVal(
						i, colName) : "true";
				setting.setConfigUserDatabase(AhRestoreCommons
						.convertStringToBoolean(configuserdatabase));

				/**
				 * Set imageactivateoffset
				 */
				colName = "imageactivateoffset";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long imageactivateoffset = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.DEFAULT_IMAGE_ACTIVATE_OFFSET);
				setting.setImageActivateOffset(imageactivateoffset);

				/**
				 * Set imageactivatetime
				 */
				colName = "imageactivatetime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long imageactivatetime = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 0);
				setting.setImageActivateTime(imageactivatetime);

				/**
				 * Set imageactivatetype
				 */
				colName = "imageactivatetype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String imageactivatetype = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				ActivateType type = AhRestoreCommons.convertStringToEnum(
						ActivateType.class, imageactivatetype,
						ActivateType.activateNextTime);
				setting.setImageActivateType(type);

				/**
				 * Set imageconntype
				 */
				colName = "imageconntype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				short imageconntype = (short) (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.CONNECT_TYPE_LOCAL);
				setting.setImageConnType(imageconntype);

				/**
				 * Set imageselecttype
				 */
				colName = "imageselecttype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String imageselecttype = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				ImageSelectionType imageSelectType = AhRestoreCommons
						.convertStringToEnum(ImageSelectionType.class,
								imageselecttype, ImageSelectionType.softVer);
				setting.setImageSelectType(imageSelectType);

				/**
				 * Set imagetimedout
				 */
				colName = "imagetimedout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long imagetimedout = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.DEFAULT_IMAGE_TIME_OUT);
				setting.setImageTimedout(imagetimedout);

				/**
				 * Set imagetransfer
				 */
				colName = "imagetransfer";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String imagetransfer = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				TransferType imageTransfer = AhRestoreCommons
						.convertStringToEnum(TransferType.class, imagetransfer,
								TransferType.scp);
				setting.setImageTransfer(imageTransfer);

				/**
				 * Set signatureConnType
				 */
				colName = "signatureConnType";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				short signatureConnType = (short) (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.CONNECT_TYPE_LOCAL);
				setting.setSignatureConnType(signatureConnType);

				/**
				 * Set signatureSelectType
				 */
				colName = "signatureSelectType";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String signatureSelectType = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				ImageSelectionType signatureSelectTypeEnum = AhRestoreCommons
						.convertStringToEnum(ImageSelectionType.class,
								signatureSelectType, ImageSelectionType.softVer);
				setting.setSignatureSelectType(signatureSelectTypeEnum);

				/**
				 * Set signatureTimedout
				 */
				colName = "signatureTimedout";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long signatureTimedout = (isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName))
						: HiveApUpdateSettings.DEFAULT_IMAGE_TIME_OUT);
				setting.setSignatureTimedout(signatureTimedout);

				/**
				 * Set distributedUpgrades
				 */
				colName = "distributedupgrades";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				String isDistributed = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "false";
				setting.setDistributedUpgrades(AhRestoreCommons.convertStringToBoolean(isDistributed));

//				BeVersionInfo oInfo = NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH+File.separatorChar+".."+File.separatorChar+"hivemanager.ver");
//				String strMainVersion = oInfo.getMainVersion();
//				if(null == strMainVersion || "".equalsIgnoreCase(strMainVersion)){
//					BeLogTools.debug(HmLogConst.M_RESTORE, "could not find main version in restore file");
//				}
//
//				try{
//					float f = Float.parseFloat(strMainVersion);
//
//					if( f < 4.0){
//						setting.setDistributedUpgrades(true);
//					}else{
//						colName = "distributedupgrades";
//						boolean defDistValue = NmsUtil.isHostedHMApplication()? true : false;
//						isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//								"hive_ap_update_settings", colName);
//						String isDistributed = isColPresent ? AhRestoreCommons
//								.convertString(xmlParser.getColVal(i, colName)) : String.valueOf(defDistValue);
//						setting.setDistributedUpgrades(AhRestoreCommons.convertStringToBoolean(isDistributed));
//					}
//
//				}
//				catch(Exception ex)
//				{
//					BeLogTools.restoreLog(BeLogTools.ERROR, "could not parse to float from string "+strMainVersion, ex);
//				}

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hive_ap_update_settings", colName);
				long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_ap_update_settings' data be lost, cause: 'owner' column is not available.");
					continue;
				}

				setting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				if (null == setting.getOwner()) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hive_ap_update_settings' data be lost, cause: 'owner' column is not available.");
					continue;
				}

				settings.add(setting);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get HiveAP update settings", e);
			}
		}

		return settings.size() > 0 ? settings : null;
	}

	public static boolean restoreHiveApImageInfo() {
		try {
			List<HiveApImageInfo> allInfo = getAllHiveApImageInfo();
			if (null == allInfo) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(allInfo);
			}
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Restore HiveAP image info finished.");
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_RESTORE,
					"Restore HiveAP image info catch exception ", e);
			return false;
		}
		return true;
	}

	private static List<HiveApImageInfo> getAllHiveApImageInfo()
			throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hiveap_image_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hiveap_image_info");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hiveap_image_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApImageInfo> allInfo = new ArrayList<HiveApImageInfo>();

		boolean isColPresent;
		String colName;
		HiveApImageInfo imgInfo;
		for (int i = 0; i < rowCount; i++) {
			try {
				imgInfo = new HiveApImageInfo();

				/**
				 * Set imagename
				 */
				colName = "imagename";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String imagename = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setImageName(AhRestoreCommons.convertString(imagename));

				/**
				 * Set majorversion
				 */
				colName = "majorversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String majorversion = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setMajorVersion(AhRestoreCommons
						.convertString(majorversion));

				/**
				 * Set minorversion
				 */
				colName = "minorversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String minorversion = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setMinorVersion(AhRestoreCommons
						.convertString(minorversion));

				/**
				 * Set productname
				 */
				colName = "productname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String productname = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setProductName(AhRestoreCommons
						.convertString(productname));

				/**
				 * Set relversion
				 */
				colName = "relversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String relversion = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setRelVersion(AhRestoreCommons
						.convertString(relversion));

				/**
				 * Set patchversion
				 */
				colName = "patchversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String patchversion = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setPatchVersion(AhRestoreCommons
						.convertString(patchversion));

				/**
				 * Set releasedata
				 */
				colName = "releasedata";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String releasedata = isColPresent ? xmlParser.getColVal(i,
						colName) : "";
				imgInfo.setReleaseData(AhRestoreCommons.convertString(releasedata));

				/**
				 * Set imageuid
				 */
				colName = "imageuid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String imageUid = isColPresent ? xmlParser.getColVal(i,
						colName) : "0";
				imgInfo.setImageUid(AhRestoreCommons.convertInt(imageUid));

				/**
				 * Set imagesize
				 */
				colName = "imagesize";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String imagesize = isColPresent ? xmlParser.getColVal(i,
						colName) : "0";
				imgInfo.setImageSize(AhRestoreCommons.convertLong(imagesize));

				/**
				 * Set sourceType
				 */
				colName = "sourcetype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				String sourcetype = isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(HiveApImageInfo.SOURCE_TYPE_LICENSESERVER);
				imgInfo.setSourceType((byte) AhRestoreCommons.convertInt(sourcetype));

                /**
                 * set owner
                 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"hiveap_image_info", colName);
				long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}

				imgInfo.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				allInfo.add(imgInfo);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get HiveAP image info", e);
			}
		}

		return allInfo.size() > 0 ? allInfo : null;
	}

}