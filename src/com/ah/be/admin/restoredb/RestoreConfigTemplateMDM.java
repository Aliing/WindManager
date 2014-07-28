package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.ConfigMDMAirWatchNonCompliance;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.mgmt.QueryUtil;

public class RestoreConfigTemplateMDM {

	public static final String CONFIGTEMPLATEMDMTABLENAME = "config_template_mdm";

	public static boolean restoreTCAAlarms() {

		try {
			long start = System.currentTimeMillis();

			List<ConfigTemplateMdm> allConfigMDMs = getAllConfigTemplateMDM();

			if (null == allConfigMDMs || allConfigMDMs.isEmpty()) {
				AhRestoreDBTools.logRestoreMsg("ConfigTemplateMDM is null or empty.");
			} else {
				AhRestoreDBTools.logRestoreMsg("Restore ConfigTemplateMDM size:"
						+ allConfigMDMs.size());
				 
				 
				 List<Long> lOldId = new ArrayList<Long>();

					for (ConfigTemplateMdm mdm : allConfigMDMs) {
						lOldId.add(mdm.getId());
					}

					QueryUtil.restoreBulkCreateBos(allConfigMDMs);
					

					for(int i=0; i<allConfigMDMs.size(); i++)
					{
						AhRestoreNewMapTools.setMapConfigTemplateMDM(lOldId.get(i), allConfigMDMs.get(i).getId());
					}
				 
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore ConfigTemplateMDM completely. Count:"
							+ (null == allConfigMDMs ? "0" : allConfigMDMs.size())
							+ ", cost:" + (end - start) + " ms.");
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore ConfigTemplateMDM error.", e);
			return false;
		}
		return true;

	}

	private static List<ConfigTemplateMdm> getAllConfigTemplateMDM() throws AhRestoreException,
			AhRestoreColNotExistException {

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of config_template_mdm.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(CONFIGTEMPLATEMDMTABLENAME);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in config_template_mdm table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;

		List<ConfigTemplateMdm> allConfigTemplateMDMs = new ArrayList<ConfigTemplateMdm>();
		ConfigTemplateMdm configMDM;

		for (int i = 0; i < rowCount; i++) {
			configMDM = new ConfigTemplateMdm();
			
			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if("".equals(id))
			{
				continue;
			}
			configMDM.setId(Long.valueOf(id));
			

			/**
			 * set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'policyname' column is not exist.");
				/**
				 * The policyname column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setPolicyname(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			/**
			 * set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'description' column is not exist.");
				/**
				 * The policyname column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setDescription(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			/**
			 * set apiKey
			 */
			colName = "apiKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'apiKey' column is not exist.");
				continue;
			} else {
				configMDM.setApiKey(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			/**
			 * set apiURL
			 */
			colName = "apiURL";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'apiURL' column is not exist.");
				/**
				 * The apiURL column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setApiURL(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			/**
			 * set mdmPassword
			 */
			colName = "mdmPassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'mdmPassword' column is not exist.");
				/**
				 * The mdmPassword column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setMdmPassword(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			/**
			 * set mdmUserName
			 */
			colName = "mdmUserName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'mdmUserName' column is not exist.");
				/**
				 * The mdmUserName column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setMdmUserName(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			
			/**
			 * set rootURLPath
			 */
			colName = "rootURLPath";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'rootURLPath' column is not exist.");
				/**
				 * The rootURLPath column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setRootURLPath(AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)));
			}
			
			
			/**
			 * set mdmType
			 */
			colName = "mdmType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			if (!isColPresent) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'mdmType' column is not exist.");
				/**
				 * The mdmType column must be exist in the table.
				 */
				continue;
			} else {
				configMDM.setMdmType(AhRestoreCommons.convertInt(xmlParser
						.getColVal(i, colName)));
			}
			
			
			/**
			 * Set enableMdmOs
			 */
			colName = "enableMdmOs";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			int enableMdmOs = isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : 0;
			
			configMDM.setEnableMdmOs(enableMdmOs);
			
			/**
			 * AirWatch Non-Compliance Settings
			 */
			ConfigMDMAirWatchNonCompliance nonComplianceSettings = configMDM.getAwNonCompliance();

            colName = "enabledNonCompliance";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            boolean enabledNonCompliance = isColPresent ? AhRestoreCommons
                    .convertStringToBoolean(xmlParser.getColVal(i, colName))
                    : false;
            nonComplianceSettings.setEnabledNonCompliance(enabledNonCompliance);

            colName = "notifyViaPush";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            boolean enabledPush = isColPresent ? AhRestoreCommons
                    .convertStringToBoolean(xmlParser.getColVal(i, colName))
                    : false;
            nonComplianceSettings.setNotifyViaPush(enabledPush);

            colName = "notifyViaSMS";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            boolean enabledSMS = isColPresent ? AhRestoreCommons
                    .convertStringToBoolean(xmlParser.getColVal(i, colName))
                    : false;
            nonComplianceSettings.setNotifyViaSMS(enabledSMS);

            colName = "notifyViaEmail";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            boolean enabledEmail = isColPresent ? AhRestoreCommons
                    .convertStringToBoolean(xmlParser.getColVal(i, colName))
                    : false;
            nonComplianceSettings.setNotifyViaEmail(enabledEmail);

            colName = "title";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            if (isColPresent) {
                nonComplianceSettings.setTitle(AhRestoreCommons
                        .convertStringNoTrim(xmlParser.getColVal(i, colName)));
            }
            
            colName = "content";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            if (isColPresent) {
                nonComplianceSettings.setContent(AhRestoreCommons
                        .convertStringNoTrim(xmlParser.getColVal(i, colName)));
            }
            
            colName = "disconnectVlanChanged";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            boolean disconnectVlanChanged = isColPresent ? AhRestoreCommons
                    .convertStringToBoolean(xmlParser.getColVal(i, colName))
                    : false;
            nonComplianceSettings.setDisconnectVlanChanged(disconnectVlanChanged);
            
            colName = "pollingInterval";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    CONFIGTEMPLATEMDMTABLENAME, colName);
            int pollingInterval = isColPresent ? AhRestoreCommons
                    .convertInt(xmlParser.getColVal(i, colName)) : 60;
            nonComplianceSettings
                    .setPollingInterval(pollingInterval);
			

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					CONFIGTEMPLATEMDMTABLENAME, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
				AhRestoreDBTools
                        .logRestoreMsg("Restore table 'config_template_mdm' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			configMDM.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			allConfigTemplateMDMs.add(configMDM);
		}

		return allConfigTemplateMDMs;
	}

}
