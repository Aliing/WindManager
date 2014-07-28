package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.mgmt.QueryUtil;

public class RestoreClientProfiles {
	

		public static final String clientProfileTableName = "mdm_profiles";

		public static boolean restoreClientProfiles() {

			try {
				long start = System.currentTimeMillis();

				List<MdmProfiles> allMdmProfiles = getAllMdmProfiles();

				if (null == allMdmProfiles || allMdmProfiles.isEmpty()) {
					AhRestoreDBTools.logRestoreMsg("allMdmProfiles is null or empty.");
				} else {
					AhRestoreDBTools.logRestoreMsg("Restore allMdmProfiles size:"
							+ allMdmProfiles.size());
					QueryUtil.restoreBulkCreateBos(allMdmProfiles);
				}
				long end = System.currentTimeMillis();
				AhRestoreDBTools
						.logRestoreMsg("Restore MdmProfiles completely. Count:"
								+ (null == allMdmProfiles ? "0" : allMdmProfiles.size())
								+ ", cost:" + (end - start) + " ms.");
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("Restore MdmProfiles error.", e);
				return false;
			}
			return true;

		}

		private static List<MdmProfiles> getAllMdmProfiles() throws AhRestoreException,
				AhRestoreColNotExistException {

			AhRestoreGetXML xmlParser = new AhRestoreGetXML();

			/**
			 * Check validation of MDM_PROFILES.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile(clientProfileTableName);
			if (!restoreRet) {
				return null;
			}

			/**
			 * No one row data stored in MdmProfiles table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			boolean isColPresent;
			String colName;

			List<MdmProfiles> allMdmProfiles = new ArrayList<MdmProfiles>();
			MdmProfiles mdmProfile;

			for (int i = 0; i < rowCount; i++) {
				mdmProfile = new MdmProfiles();
				
				/**
				 * set mdmProfilesName
				 */
				colName = "mdmProfilesName";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						clientProfileTableName, colName);
				String mdmProfilesName=isColPresent ? xmlParser.getColVal(i, colName) : "";
				mdmProfile.setMdmProfilesName(AhRestoreCommons.convertString(mdmProfilesName));

				/**
				 * set createTime
				 */
				colName = "createTime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						clientProfileTableName, colName);
				String createTime=isColPresent ? xmlParser.getColVal(i, colName) : "";
				mdmProfile.setCreateTime(AhRestoreCommons.convertLong(createTime));

				/**
				 * set updateTime
				 */
				colName = "updateTime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						clientProfileTableName, colName);
				String updateTime=isColPresent ? xmlParser.getColVal(i, colName) : "";
				mdmProfile.setUpdateTime(AhRestoreCommons.convertLong(updateTime));


				/**
				 * set userProfileAttributeValue
				 */
				colName = "userProfileAttributeValue";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						clientProfileTableName, colName);
				String userProfileAttributeValue=isColPresent ? xmlParser.getColVal(i, colName) : "1";
				mdmProfile.setUserProfileAttributeValue((short)AhRestoreCommons.convertInt(userProfileAttributeValue));


				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						clientProfileTableName, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					BeLogTools
							.restoreLog(
									BeLogTools.DEBUG,
									"Restore table 'clientProfileTableName' data be lost, cause: 'owner' column is not available.");
					continue;
				}

				mdmProfile.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				allMdmProfiles.add(mdmProfile);
			}

			return allMdmProfiles;
		}
		



}
