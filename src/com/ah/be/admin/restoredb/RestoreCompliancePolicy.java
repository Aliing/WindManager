package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.CompliancePolicy;

public class RestoreCompliancePolicy {

	public static final String tableName = "compliance_policy";

	private static List<CompliancePolicy> getAllCompliancePolicy() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of compliance_policy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in compliance_policy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<CompliancePolicy> compliancePolicyInfo = new ArrayList<CompliancePolicy>();
		boolean isColPresent;
		String colName;
		CompliancePolicy compliancePolicy;

		for (int i = 0; i < rowCount; i++)
		{
			compliancePolicy = new CompliancePolicy();

			/**
			 * Set clientopen
			 */
			colName = "clientopen";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int clientopen = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_POOR;
			compliancePolicy.setClientOpen(clientopen);

			/**
			 * Set clientopenauth
			 */
			colName = "clientopenauth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int clientopenauth = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_GOOD;
			compliancePolicy.setClientOpenAuth(clientopenauth);

			/**
			 * Set clientwep
			 */
			colName = "clientwep";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int clientwep = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_POOR;
			compliancePolicy.setClientWep(clientwep);

			/**
			 * Set clientpsk
			 */
			colName = "clientpsk";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int clientpsk = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_GOOD;
			compliancePolicy.setClientPsk(clientpsk);

			/**
			 * Set clientprivatepsk
			 */
			colName = "clientprivatepsk";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int clientprivatepsk = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
			compliancePolicy.setClientPrivatePsk(clientprivatepsk);

			/**
			 * Set client8021x
			 */
			colName = "client8021x";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int client8021x = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
			compliancePolicy.setClient8021x(client8021x);

			/**
			 * Set hiveapssh
			 */
			colName = "hiveapssh";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int hiveapssh = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
			compliancePolicy.setHiveApSsh(hiveapssh);

			/**
			 * Set hiveaptelnet
			 */
			colName = "hiveaptelnet";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int hiveaptelnet = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_POOR;
			compliancePolicy.setHiveApTelnet(hiveaptelnet);

			/**
			 * Set hiveapping
			 */
			colName = "hiveapping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int hiveapping = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_GOOD;
			compliancePolicy.setHiveApPing(hiveapping);

			/**
			 * Set hiveapsnmp
			 */
			colName = "hiveapsnmp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int hiveapsnmp = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName))
					: CompliancePolicy.COMPLIANCE_POLICY_POOR;
			compliancePolicy.setHiveApSnmp(hiveapsnmp);

			/**
			 * Set passwordssid
			 */
			colName = "passwordssid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String passwordssid = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			compliancePolicy.setPasswordSSID(AhRestoreCommons.convertStringToBoolean(passwordssid));

//			/**
//			 * Set passwordhm
//			 */
//			colName = "passwordhm";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//					tableName, colName);
//			String passwordhm = isColPresent ? xmlParser.getColVal(i, colName) : "true";
//			compliancePolicy.setPasswordHm(AhRestoreCommons.convertStringToBoolean(passwordhm));
//
			/**
			 * Set passwordhive
			 */
			colName = "passwordhive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String passwordhive = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			compliancePolicy.setPasswordHive(AhRestoreCommons.convertStringToBoolean(passwordhive));

			/**
			 * Set passwordcapwap
			 */
			colName = "passwordcapwap";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String passwordcapwap = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			compliancePolicy.setPasswordCapwap(AhRestoreCommons.convertStringToBoolean(passwordcapwap));

			/**
			 * Set passwordhiveap
			 */
			colName = "passwordhiveap";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String passwordhiveap = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			compliancePolicy.setPasswordHiveap(AhRestoreCommons.convertStringToBoolean(passwordhiveap));


			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table '"+tableName+"' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			compliancePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			compliancePolicyInfo.add(compliancePolicy);
		}

		return compliancePolicyInfo;
	}

	public static boolean restoreCompliancePolicy()
	{
		try
		{
			List<CompliancePolicy> allCompliancePolicy = getAllCompliancePolicy();
			if(null == allCompliancePolicy || allCompliancePolicy.size()<1)
			{
				AhRestoreDBTools.logRestoreMsg("allCompliancePolicy is null");

				return false;
			}
			else
			{
				QueryUtil.restoreBulkCreateBos(allCompliancePolicy);
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

}