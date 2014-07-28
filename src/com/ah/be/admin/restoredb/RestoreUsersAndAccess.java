/**
m *@filename		RestoreUsersAndAccess.java
 *@version
 *@author		Fiona
 *@createtime	2007-11-9 AM 09:31:00
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.gml.TemplateField;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MACAuth;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusProxyRealm;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.util.CreateObjectAuto;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
public class RestoreUsersAndAccess {

	private static final Map<Long, Long> defaultUserGroups = new HashMap<Long, Long>();

	public static boolean RESTORE_FROM_40R1_BEFORE = false;
	public static boolean RESTORE_FROM_41R1_BEFORE = false;
	public static boolean RESTORE_FROM_50R1_BEFORE = false;

	/**
	 * Get all information from active_directory_or_ldap table
	 *
	 * @param gradeLog -
	 * @return List<ActiveDirectoryOrOpenLdap> all ActiveDirectoryOrOpenLdap
	 * @throws AhRestoreColNotExistException -
	 *             if active_directory_or_ldap.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing active_directory_or_ldap.xml.
	 */
	private static List<ActiveDirectoryOrOpenLdap> getActiveDirectoryOrOpenLdap(List<HmUpgradeLog> gradeLog) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of active_directory_or_ldap.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("active_directory_or_ldap");
		if (!restoreRet || null == gradeLog)
		{
			RESTORE_FROM_40R1_BEFORE = isDataFromOldVersionForTimeZone((float)4.0);
			return null;
		}

		/**
		 * No one row data stored in active_directory_or_ldap table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ActiveDirectoryOrOpenLdap> authInfo = new ArrayList<ActiveDirectoryOrOpenLdap>();

		boolean isColPresent;
		String colName;
		ActiveDirectoryOrOpenLdap singleInfo;
		Map<String, HmUpgradeLog> mapUpLog = new HashMap<String, HmUpgradeLog>();
		Map<String, List<ActiveDirectoryDomain>> adDomains = null;
		// the main table must have records
		if (rowCount > 0) {
			adDomains = getAllMultipleDomainInfo(mapUpLog);
		} else {
			RESTORE_FROM_40R1_BEFORE = isDataFromOldVersionForTimeZone((float)4.0);
		}
		RESTORE_FROM_41R1_BEFORE = isDataFromOldVersionForTimeZone((float)4.1);
		RESTORE_FROM_50R1_BEFORE = isDataFromOldVersionForTimeZone((float)5.0);

		List<ActiveDirectoryDomain> domainList;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new ActiveDirectoryOrOpenLdap();

			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'active_directory_or_ldap' data be lost, cause: 'name' column is not exist.");
				continue;
			}
			singleInfo.setName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set typeflag
			 */
			colName = "typeflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String typeflag = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
			singleInfo.setTypeFlag((short)AhRestoreCommons.convertInt(typeflag));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'active_directory_or_ldap' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singleInfo.setOwner(ownerDomain);

			/**
			 * Set ad_ipaddress_id
			 */
			colName = "ad_ipaddress_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			if (isColPresent) {
				Long adId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				if(null != adId) {
					Long newIp = AhRestoreNewMapTools.getMapIpAdddress(adId);
					if(null != newIp) {
						singleInfo.setAdServer(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));
					}
				}
			} else {
				/**
				 * Set adserver
				 */
				colName = "adserver";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				String adserver = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
				if (!"".equals(adserver)) {
					short adType = ImportCsvFileAction.getIpAddressWrongFlag(adserver) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
					singleInfo.setAdServer(CreateObjectAuto.createNewIP(adserver, adType, ownerDomain, name));
				}
			}

			/**
			 * Set ldap_ipaddress_id
			 */
			colName = "ldap_ipaddress_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			if (isColPresent) {
				Long ldapId = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				if(null != ldapId) {
					Long newIp = AhRestoreNewMapTools.getMapIpAdddress(ldapId);
					if(null != newIp) {
						singleInfo.setLdapServer(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));
					}
				}
			} else {
				/**
				 * Set ldapserver
				 */
				colName = "ldapserver";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				String ldapserver = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
				if (!"".equals(ldapserver)) {
					short ldapType = ImportCsvFileAction.getIpAddressWrongFlag(ldapserver) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
					singleInfo.setLdapServer(CreateObjectAuto.createNewIP(ldapserver, ldapType, ownerDomain, name));
				}
			}

			/**
			 * Set basedn
			 */
			colName = "basedn";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String basedn = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setBasedN(AhRestoreCommons.convertString(basedn));

			/**
			 * Set binddnname
			 */
			colName = "binddnname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String bindName;
			if (!isColPresent) {
				/**
				 * Set adminidentityname
				 */
				colName = "adminidentityname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				bindName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			} else {
				bindName = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			}
			singleInfo.setBindDnName(bindName);

			/**
			 * Set filterattr
			 */
			colName = "filterattr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String filterattr = isColPresent ? xmlParser.getColVal(i, colName) : "cn";
			if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY == singleInfo.getTypeFlag() && RESTORE_FROM_41R1_BEFORE) {
				filterattr = "uid";
			}
			singleInfo.setFilterAttr(AhRestoreCommons.convertString(filterattr));

			/**
			 * Set cacertfileo
			 */
			colName = "cacertfileo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String cacertfileo = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			String clientfile;
			String keyfileo;
			// ca cert file must be not blank
			if ("".equals(cacertfileo)) {
				cacertfileo = BeAdminCentOSTools.AH_NMS_DEFAULT_CA;
				clientfile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT;
				keyfileo = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY;

			// client and key file can be blank
			} else {
				/**
				 * Set clientfile
				 */
				colName = "clientfile";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				clientfile = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

				/**
				 * Set keyfileo
				 */
				colName = "keyfileo";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				keyfileo = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			}
			singleInfo.setCaCertFileO(cacertfileo);
			singleInfo.setClientFile(clientfile);
			singleInfo.setKeyFileO(keyfileo);

			/**
			 * Set passworda
			 */
			colName = "passworda";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String passworda = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setPasswordA(AhRestoreCommons.convertStringNoTrim(passworda));

			if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP != singleInfo.getTypeFlag()) {
				 domainList = null;
				if (null == adDomains) {
					/**
					 * Set workgroup
					 */
					colName = "workgroup";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"active_directory_or_ldap", colName);
					if (isColPresent) {
						domainList = new ArrayList<ActiveDirectoryDomain>();
						ActiveDirectoryDomain singleDomain = new ActiveDirectoryDomain();
						singleDomain.setDomain(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));

						/**
						 * Set realmname
						 */
						colName = "realmname";
						isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"active_directory_or_ldap", colName);
						singleDomain.setFullName(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));
						singleDomain.setServer(singleDomain.getFullName());

						/**
						 * Set identityname
						 */
						colName = "identityname";
						isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							"active_directory_or_ldap", colName);
						singleDomain.setBindDnName(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));

						singleDomain.setBindDnPass(AhRestoreCommons.convertString(passworda));
						singleDomain.setDefaultFlag(true);
						domainList.add(singleDomain);
					}
				} else {
					domainList = adDomains.get(id);
					HmUpgradeLog upgradeLog = mapUpLog.get(id);
					if (null != upgradeLog) {
						upgradeLog.setFormerContent("Active Directory "+upgradeLog.getFormerContent()+"in the Directory/LDAP Setting \""+singleInfo.getName()+"\"");
						upgradeLog.setRecommendAction("No action is required.");
						upgradeLog.setOwner(ownerDomain);
						upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
						upgradeLog.setAnnotation("Click to add an annotation");
						gradeLog.add(upgradeLog);
					}
				}
				singleInfo.setAdDomains(domainList);
			}

			/**
			 * Set passwordo
			 */
			colName = "passwordo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String passwordo = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setPasswordO(AhRestoreCommons.convertStringNoTrim(passwordo));

			/**
			 * Set ldapprotocol
			 */
			colName = "ldapprotocol";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			short protocol = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL_LDAP;
			singleInfo.setLdapProtocol(protocol);

			/**
			 * Set destinationport
			 */
			colName = "destinationport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String destinationport = isColPresent ? xmlParser.getColVal(i, colName) : "389";
			singleInfo.setDestinationPort(AhRestoreCommons.convertInt(destinationport));

			/**
			 * Set authtlsenable
			 */
			colName = "authtlsenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String authtlsenable = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setAuthTlsEnable(AhRestoreCommons.convertStringToBoolean(authtlsenable));

			/**
			 * Set usernamea
			 */
			colName = "usernamea";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String usernamea = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setUserNameA(AhRestoreCommons.convertString(usernamea));

			/**
			 * Set keypasswordo
			 */
			colName = "keypasswordo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String keypasso = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(clientfile) && "".equals(keyfileo)) {
				keypasso = "";
			}
			singleInfo.setKeyPasswordO(keypasso);

			/**
			 * Set verifyserver
			 */
			colName = "verifyserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String verifyserver = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setVerifyServer((short)AhRestoreCommons.convertInt(verifyserver));

			/**
			 * Set computerou
			 */
			colName = "computerou";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String computerou = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setComputerOU(AhRestoreCommons.convertString(computerou));

			/**
			 * Set apMac
			 */
			colName = "apmac";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			if (isColPresent) {
				singleInfo.setApMac(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));
			} else {
				RESTORE_FROM_40R1_BEFORE = true;
				singleInfo.setApMac("");
			}

			/**
			 * Set saveCredentials
			 */
			if (RESTORE_FROM_50R1_BEFORE &&
					null != singleInfo.getUserNameA() &&
					!"".equals(singleInfo.getUserNameA())) {
				singleInfo.setSaveCredentials(true);
			} else {
				colName = "saveCredentials";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"active_directory_or_ldap", colName);
				String saveCredentials = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleInfo.setSaveCredentials(AhRestoreCommons.convertStringToBoolean(saveCredentials));
			}
			
			/**
			 * stripFilter
			 */
			colName = "stripFilter";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String stripFilter = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setStripFilter(AhRestoreCommons.convertStringToBoolean(stripFilter));

			/**
			 * Set ldapSaslWrapping
			 */
			colName = "ldapSaslWrapping";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"active_directory_or_ldap", colName);
			String ldapSaslWrapping = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setLdapSaslWrapping((short)AhRestoreCommons.convertInt(ldapSaslWrapping));

			authInfo.add(singleInfo);
		}

		return authInfo.size() > 0 ? authInfo : null;
	}

	/**
	 * Restore active_directory_or_ldap table
	 *
	 * @return true if table of active_directory_or_ldap restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreActiveDirOrLdap()
	{
		try
		{
			List<HmUpgradeLog> gradeLog = new ArrayList<HmUpgradeLog>();
			List<ActiveDirectoryOrOpenLdap> allLdap = getActiveDirectoryOrOpenLdap(gradeLog);
			if(null != allLdap) {
				List<Long> lOldId = new ArrayList<Long>();

				for (ActiveDirectoryOrOpenLdap ldap : allLdap) {
					lOldId.add(ldap.getId());
				}

				QueryUtil.restoreBulkCreateBos(allLdap);

				for(int i=0; i<allLdap.size(); i++)
				{
					AhRestoreNewMapTools.setMapDirectoryOrLdap(lOldId.get(i), allLdap.get(i).getId());
				}
				if (!gradeLog.isEmpty()) {
					QueryUtil.restoreBulkCreateBos(gradeLog);
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from directory_openldap_info table
	 *
	 * @return List<ActiveDirectoryOrLdapInfo> all ActiveDirectoryOrLdapInfo
	 * @throws AhRestoreColNotExistException -
	 *             if directory_openldap_info.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing directory_openldap_info.xml.
	 */
	private static List<ActiveDirectoryOrLdapInfo> getActiveDirectoryOrLdapInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of directory_openldap_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("directory_openldap_info");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in directory_openldap_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<ActiveDirectoryOrLdapInfo> authInfo = new ArrayList<ActiveDirectoryOrLdapInfo>();

		boolean isColPresent;
		String colName;
		ActiveDirectoryOrLdapInfo singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new ActiveDirectoryOrLdapInfo();

			/**
			 * Set directory_openldap_id
			 */
			colName = "directory_openldap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"directory_openldap_info", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set serverpriority
			 */
			colName = "serverpriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"directory_openldap_info", colName);
			String serverpriority = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(RadiusServer.RADIUS_PRIORITY_PRIMARY);
			singleInfo.setServerPriority((short)AhRestoreCommons.convertInt(serverpriority));

			/**
			 * Set directory_or_ldap_id
			 */
			colName = "directory_or_ldap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"directory_openldap_info", colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(ipId)){
				Long newId = AhRestoreNewMapTools.getMapDirectoryOrLdap(AhRestoreCommons.convertLong(ipId));
				if(null != newId){
					singleInfo.setDirectoryOrLdap(AhRestoreNewTools.CreateBoWithId(ActiveDirectoryOrOpenLdap.class, newId));
				} else {
					continue;
				}
			} else {
				continue;
			}
			authInfo.add(singleInfo);
		}

		return authInfo.size() > 0 ? authInfo : null;
	}

	/**
	 * Get all information from radius_hiveap_auth table
	 *
	 * @param radiusId and radiusName
	 * @param radiusName -
	 * @param lstLogBo -
	 * @param hmDom -
	 * @return List<RadiusHiveapAuth> all RadiusHiveapAuth
	 * @throws AhRestoreColNotExistException -
	 *             if radius_hiveap_auth.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_hiveap_auth.xml.
	 */
	private static List<RadiusHiveapAuth> getHiveAPAuthInfoByRadiusId(String radiusId, String radiusName, List<HmUpgradeLog> lstLogBo, HmDomain hmDom) throws AhRestoreColNotExistException, AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_hiveap_auth.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_hiveap_auth");
		if (!restoreRet || null == radiusId || null == radiusName) {
			return null;
		}

		/**
		 * No one row data stored in radius_hiveap_auth table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusHiveapAuth> authInfo = new ArrayList<RadiusHiveapAuth>();

		boolean isColPresent;
		String colName;
		RadiusHiveapAuth singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadiusHiveapAuth();

			/**
			 * Set auth_id
			 */
			colName = "auth_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_auth", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id) || !radiusId.equals(id)) {
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_auth", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set nameflag
			 */
			colName = "nameflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_auth", colName);
			if (isColPresent) {
				short nameflag = (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
				if (IpAddress.TYPE_HOST_NAME == nameflag) {
					/**
					 * Set hostname
					 */
					colName = "hostname";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radius_hiveap_auth", colName);
					String servername = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
					if (!"".equals(servername)) {
						singleInfo.setIpAddress(CreateObjectAuto.createNewIP(servername, nameflag, hmDom, "For "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting : "+radiusName));
					}
				}
			}

			/**
			 * Set sharedkey
			 */
			colName = "sharedkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_auth", colName);
			String sharedkey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setSharedKey(AhRestoreCommons.convertString(sharedkey));

			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_auth", colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			singleInfo.setIpAddress(RestoreConfigNetwork.getNewIpNetworkObj(ipId, upgradeLog));

			// there is upgrade log to record
			if (null != upgradeLog.getFormerContent() && upgradeLog.getFormerContent().length() > 0) {
				lstLogBo.add(upgradeLog);
			}
			authInfo.add(singleInfo);
		}

		return authInfo.size() > 0 ? authInfo : null;
	}

	/**
	 * Get all information from radius_on_hiveap table
	 *
	 * @param upLogs -
	 * @return List<RadiusOnHiveap> all RadiusOnHiveap BO
	 * @throws AhRestoreColNotExistException -
	 *             if radius_on_hiveap.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_on_hiveap.xml.
	 */
	private static List<RadiusOnHiveap> getAllHiveapRadiusService(List<HmUpgradeLog> upLogs) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_on_hiveap.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_on_hiveap");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radius_on_hiveap table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusOnHiveap> radius = new ArrayList<RadiusOnHiveap>();

		boolean isColPresent;
		String colName;
		RadiusOnHiveap singleRadius;
		List<ActiveDirectoryOrLdapInfo> dirLdap = null;
		Map<String, Set<LocalUserGroup>> groupsForLdap = null;
		Map<String, Set<LocalUserGroup>> groupsForLocal = null;
		Map<String, Set<LocalUser>> users = null;
		Set<LocalUserGroup> groupInfo = null;
		Map<String, List<LdapServerOuUserProfile>> adOrLdapGroup = null;

		// the main table must have records
		if (rowCount > 0) {
			dirLdap = getActiveDirectoryOrLdapInfo();
			groupsForLdap = getAllLocalUserGroupInfo("radius_on_hiveap_local_group");
			groupsForLocal = getAllLocalUserGroupInfo("radius_on_hiveap_local_user_group");
			users = getAllLocalUserInfo();
			adOrLdapGroup = getAllAdOrLdapUserGroupInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singleRadius = new RadiusOnHiveap();

			/**
			 * Set radiusname
			 */
			colName = "radiusname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_on_hiveap' data be lost, cause: 'radiusname' column is not exist.");
				continue;
			}
			singleRadius.setRadiusName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set authtype
			 */
			colName = "authtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			short authtype = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL;
			singleRadius.setAuthType(authtype);
			
			/**
			 * Set authtypeDefault
			 */
			colName = "authTypeDefault";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			short authTypeDefault = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) :
				RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_PEAP;
			singleRadius.setAuthTypeDefault(authTypeDefault);

			/**
			 * Set localinterval
			 */
			colName = "localinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String localinterval = isColPresent ? xmlParser.getColVal(i, colName) : "300";
			singleRadius.setLocalInterval(AhRestoreCommons.convertInt(localinterval));

			/**
			 * Set remoteinterval
			 */
			colName = "remoteinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String remoteinterval = isColPresent ? xmlParser.getColVal(i, colName) : "30";
			singleRadius.setRemoteInterval(AhRestoreCommons.convertInt(remoteinterval));

			/**
			 * Set retryinterval
			 */
			colName = "retryinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String retryinterval = isColPresent ? xmlParser.getColVal(i, colName) : "600";
			singleRadius.setRetryInterval(AhRestoreCommons.convertInt(retryinterval));

			/**
			 * Set cacertfile
			 */
			colName = "cacertfile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String cacertfile = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(cacertfile)) {
				cacertfile = BeAdminCentOSTools.AH_NMS_DEFAULT_CA;
			}
			singleRadius.setCaCertFile(cacertfile);

			/**
			 * Set databasetype
			 */
			colName = "databasetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			short databasetype = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL;
			singleRadius.setDatabaseType(databasetype);

			/*
			 * local user exists when the local is selected
			 */
			if (RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL == databasetype || RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE == databasetype
				|| RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN == databasetype || RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT == databasetype) {
				if (null != groupsForLocal) {
					groupInfo = groupsForLocal.get(id);
				} else if (null != users) {
					Set<LocalUser> localUsers = users.get(id);
					if (null != localUsers) {
						groupInfo = new HashSet<LocalUserGroup>();
						Set<String> names = new HashSet<String>();
						for (LocalUser user : localUsers) {
							LocalUserGroup group = user.getLocalUserGroup();
							if (null != group && !names.contains(group.getGroupName())) {
								groupInfo.add(group);
								names.add(group.getGroupName());
							}
						}
					}
				} else {
					groupInfo = getUserGroupsByRadiusId(singleRadius.getId());
				}
				singleRadius.setLocalUserGroup(groupInfo);
			}

			/**
			 * Set useedirect
			 */
			colName = "useedirect";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String useedirect = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRadius.setUseEdirect(AhRestoreCommons.convertStringToBoolean(useedirect));

			/**
			 * Set accpolicy
			 */
			colName = "accpolicy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String accpolicy = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRadius.setAccPolicy(AhRestoreCommons.convertStringToBoolean(accpolicy));

			/**
			 * Set serverfile
			 */
			colName = "serverfile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String serverfile = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(serverfile)) {
				serverfile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT;
			}
			singleRadius.setServerFile(serverfile);

			/**
			 * Set keyfile
			 */
			colName = "keyfile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String keyfile = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(keyfile)) {
				if (RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP == authtype) {
					keyfile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY;
				} else {
					keyfile = serverfile;
				}
			}
			singleRadius.setKeyFile(keyfile);

			/**
			 * Set keypassword
			 */
			colName = "keypassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String keypassword = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setKeyPassword(AhRestoreCommons.convertString(keypassword));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
			Long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_on_hiveap' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			singleRadius.setOwner(ownerDomain);

			/**
			 * Set reauthtime
			 */
			colName = "reauthtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String reauthtime = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setReauthTime(AhRestoreCommons.convertString(reauthtime));

			/**
			 * Set serverport
			 */
			colName = "serverport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String serverport = isColPresent ? xmlParser.getColVal(i, colName) : "1812";
			singleRadius.setServerPort(AhRestoreCommons.convertInt(serverport));

			/**
			 * Set cachetime
			 */
			colName = "cachetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String cachetime = isColPresent ? xmlParser.getColVal(i, colName) : "86400";
			singleRadius.setCacheTime(AhRestoreCommons.convertInt(cachetime));

			/**
			 * Set cacheenable
			 */
			colName = "cacheenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String cacheenable = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setCacheEnable(AhRestoreCommons.convertStringToBoolean(cacheenable));

			/**
			 * Set mapenable
			 */
			colName = "mapenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String mapenable = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setMapEnable(AhRestoreCommons.convertStringToBoolean(mapenable));

			/**
			 * Set serverenable
			 */
			colName = "serverenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String serverenable = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleRadius.setServerEnable(AhRestoreCommons.convertStringToBoolean(serverenable));

			/**
			 * Set cnenable
			 */
			colName = "cnenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String cnenable = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setCnEnable(AhRestoreCommons.convertStringToBoolean(cnenable));

			/**
			 * Set dbenable
			 */
			colName = "dbenable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String dbenable = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setDbEnable(AhRestoreCommons.convertStringToBoolean(dbenable));

			/**
			 * Set ttlsCheckInDb
			 */
			colName = "ttlsCheckInDb";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String ttlsCheckInDb = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setTtlsCheckInDb(AhRestoreCommons.convertStringToBoolean(ttlsCheckInDb));

			/**
			 * Set peapCheckInDb
			 */
			colName = "peapCheckInDb";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String peapCheckInDb = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setPeapCheckInDb(AhRestoreCommons.convertStringToBoolean(peapCheckInDb));

			/**
			 * Set userprofileid
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String userprofileid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setUserProfileId(AhRestoreCommons.convertString(userprofileid));

			/**
			 * Set vlanid
			 */
			colName = "vlanid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String vlanid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setVlanId(AhRestoreCommons.convertString(vlanid));

			/**
			 * Set groupattribute
			 */
			colName = "groupattribute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String groupattribute = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setGroupAttribute(AhRestoreCommons.convertString(groupattribute));

			/**
			 * Set globalCatalog
			 */
			colName = "globalcatalog";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String catalog = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setGlobalCatalog(AhRestoreCommons.convertStringToBoolean(catalog));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setDescription(AhRestoreCommons.convertString(description));

			if(null != dirLdap && RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL != singleRadius.getDatabaseType()
				 && RadiusOnHiveap.RADIUS_SERVER_DBTYPE_NONE != singleRadius.getDatabaseType())
			{
				List<ActiveDirectoryOrLdapInfo> thisLdapInfo = new ArrayList<ActiveDirectoryOrLdapInfo>();
				for(ActiveDirectoryOrLdapInfo ldapInfo : dirLdap)
				{
					if(id.equals(ldapInfo.getRestoreId()))
					{
						thisLdapInfo.add(ldapInfo);
					}
				}
				singleRadius.setDirectoryOrLdap(thisLdapInfo);
			}

			/**
			 * Set mapbygrouporuser
			 */
			colName = "mapbygrouporuser";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			if (singleRadius.getMapEnable() && isColPresent) {
				singleRadius.setMapByGroupOrUser((short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)));

				if (RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI == singleRadius.getMapByGroupOrUser()) {
					/*
					 * local user group has no relationship with map group attribute
					 */
					groupInfo = null;
					if (null != groupsForLdap) {
						groupInfo = groupsForLdap.get(id);
					} else if (null != adOrLdapGroup) {
						singleRadius.setLdapOuUserProfiles(adOrLdapGroup.get(id));
					} else {
						groupInfo = getUserGroupsByRadiusId(singleRadius.getId());
					}
					if (null != groupInfo) {
						List<ActiveDirectoryOrLdapInfo> directoryOrLdap = singleRadius.getDirectoryOrLdap();
						short typeFlag = ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY;
						Long serverId = null;
						if (null != directoryOrLdap && !directoryOrLdap.isEmpty()) {
							typeFlag = directoryOrLdap.get(0).getDirectoryOrLdap().getTypeFlag();
							serverId = directoryOrLdap.get(0).getDirectoryOrLdap().getId();
						}
						List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
						singleRadius.setLdapOuUserProfiles(changeUserGroupToNewDesign(groupInfo, typeFlag, serverId, ownerDomain.getId(), lstLogBo));

						// there is upgrde logs
						String lastStr = " in "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting \""+name+"\".";
						HmTimeStamp logTime = new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString());
						for (HmUpgradeLog upLog : lstLogBo) {
							upLog.setFormerContent(upLog.getFormerContent()+lastStr);
							upLog.setPostContent(upLog.getPostContent());
							upLog.setRecommendAction("Create a new user profile with a matching attribute and map the LDAP user groups to it "+lastStr);
							upLog.setOwner(ownerDomain);
							upLog.setLogTime(logTime);
							upLog.setAnnotation("Click to add an annotation");
							upLogs.add(upLog);
						}
					}
				}
			} else {
				singleRadius.setMapByGroupOrUser(RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI);
			}

			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			singleRadius.setIpOrNames(getHiveAPAuthInfoByRadiusId(id, name, lstLogBo, ownerDomain));

			// there is upgrde logs
			for (HmUpgradeLog upLog : lstLogBo) {
				upLog.setFormerContent("A NAS in "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting \""+name+"\" " + upLog.getFormerContent());
				upLog.setPostContent(upLog.getPostContent()+" the NAS in "+NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Setting \""+name+"\".");
				upLog.setRecommendAction("No action is required.");
				upLog.setOwner(ownerDomain);
				upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(),ownerDomain.getTimeZoneString()));
				upLog.setAnnotation("Click to add an annotation");
				upLogs.add(upLog);
			}

			/**
			 * Library sip server
			 */
			/**
			 * Set librarysipcheck
			 */
			colName = "librarysipcheck";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap", colName);
			String librarysipcheck = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setLibrarySipCheck(AhRestoreCommons.convertStringToBoolean(librarysipcheck));

			if (singleRadius.isLibrarySipCheck()) {
				/**
				 * Set library_sip_server_id
				 */
				colName = "library_sip_server_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String sipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
				if (!"".equals(sipId)) {
					Long newId = AhRestoreNewMapTools.getMapIpAdddress(Long.valueOf(sipId));
					if (null != newId) {
						singleRadius.setSipServer(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newId));
					}
				}

				/**
				 * Set sipport
				 */
				colName = "sipport";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String sipport = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleRadius.setSipPort(AhRestoreCommons.convertInt(sipport));

				/**
				 * Set loginEnable
				 */
				colName = "loginenable";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String loginEnable = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				singleRadius.setLoginEnable(AhRestoreCommons.convertStringToBoolean(loginEnable));

				/**
				 * Set loginuser
				 */
				colName = "loginuser";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String loginuser = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleRadius.setLoginUser(AhRestoreCommons.convertString(loginuser));

				/**
				 * Set loginpwd
				 */
				colName = "loginpwd";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String loginpwd = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleRadius.setLoginPwd(AhRestoreCommons.convertString(loginpwd));

				/**
				 * Set institutionid
				 */
				colName = "institutionid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String institutionid = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleRadius.setInstitutionId(AhRestoreCommons.convertString(institutionid));

				/**
				 * Set separator
				 */
				colName = "separator";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String separator = isColPresent ? xmlParser.getColVal(i, colName) : "";
				singleRadius.setSeparator(AhRestoreCommons.convertString(separator));

				// sip policy
				/**
				 * Set library_sip_policy_id
				 */
				colName = "library_sip_policy_id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_on_hiveap", colName);
				String sipPolicyId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
				if (!"".equals(sipPolicyId)) {
					singleRadius.setSipPolicy(AhRestoreNewMapTools.getMapRadiusLibrarySip(Long.valueOf(sipPolicyId)));
				}
			}

			radius.add(singleRadius);
		}

		return radius.size() > 0 ? radius : null;
	}

	/**
	 * Get local user group list by radius id before 3.2r2 version.
	 *
	 *@param arg_RadiusId : RADIUS on HiveAP id
	 *@return Set<LocalUserGroup>
	 */
	private static Set<LocalUserGroup> getUserGroupsByRadiusId(Long arg_RadiusId) {
		if (null == arg_RadiusId) {
			return null;
		}
		Set<LocalUserGroup> groupInfo = new HashSet<LocalUserGroup>();
		Set<Long> groupIds = AhRestoreNewMapTools.getMapOldLocalUserGroup(arg_RadiusId);
		Set<Long> newGroupIds = new HashSet<Long>();
		if (null != groupIds) {
			for (Long groupId : groupIds) {
				Long group = AhRestoreNewMapTools.getMapLocalUserGroup(groupId);
				if (null != group) {
					newGroupIds.add(group);
					groupInfo.add(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, group));
				}
			}
		}
		// the group from local user
		Set<LocalUserGroup> newGroups = AhRestoreNewMapTools.getMapOldLocalUser(arg_RadiusId);
		if (null != newGroups) {
			for (LocalUserGroup group : newGroups) {
				if (!newGroupIds.contains(group.getId())) {
					groupInfo.add(group);
				}
			}
		}
		return groupInfo.size() > 0 ? groupInfo : null;
	}

	/**
	 * Restore radius_on_hiveap table
	 *
	 * @return true if table of radius_on_hiveap restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreHiveAPRadius()
	{
		try
		{
			List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
			List<RadiusOnHiveap> allRadius = getAllHiveapRadiusService(lstLogBo);
			if(null != allRadius) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusOnHiveap radius : allRadius) {
					lOldId.add(radius.getId());
				}

				QueryUtil.restoreBulkCreateBos(allRadius);

				for(int i=0; i<allRadius.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadiusServerOnHiveAP(lOldId.get(i), allRadius.get(i).getId());
				}
			}
			/*
			 * insert or update the data to database
			 */
			if (lstLogBo.size() > 0) {
				try {
					QueryUtil.bulkCreateBos(lstLogBo);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option for NAS upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from local_user_group table
	 *
	 * @return List<LocalUserGroup> all LocalUserGroup BO
	 * @throws AhRestoreColNotExistException -
	 *             if local_user_group.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing local_user_group.xml.
	 */
	private static List<LocalUserGroup> getAllLocalUserGroup() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of local_user_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("local_user_group");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in local_user_group table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LocalUserGroup> group = new ArrayList<LocalUserGroup>();

		boolean isColPresent;
		String colName;
		LocalUserGroup userGroup;
		Set<Long> radius;

		for (int i = 0; i < rowCount; i++)
		{
			userGroup = new LocalUserGroup();

			/**
			 * Set groupname
			 */
			colName = "groupname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'local_user_group' data be lost, cause: 'groupname' column is not exist.");
				continue;
			}
			userGroup.setGroupName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			userGroup.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set reauthtime
			 */
			colName = "reauthtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String reauthtime = isColPresent ? xmlParser.getColVal(i, colName) : "1800";
			if(AhRestoreCommons.convertInt(reauthtime)<0) {
				userGroup.setReauthTime(1800);
			} else {
				userGroup.setReauthTime(AhRestoreCommons.convertInt(reauthtime));
			}
			/**
			 * Set userprofileid
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String userprofileid = isColPresent ? xmlParser.getColVal(i, colName) : "-1";
			userGroup.setUserProfileId(AhRestoreCommons.convertInt(userprofileid));

			/**
			 * Set vlanid
			 */
			colName = "vlanid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			int vlanid = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : -1;
			if (vlanid==0) {
				userGroup.setVlanId(-1);
			} else {
				userGroup.setVlanId(vlanid > 4094 ? 4094 : vlanid);
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"local_user_group", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'local_user_group' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			userGroup.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			userGroup.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set blnchardigits
			 */
			colName = "blnchardigits";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String blnchardigits = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			userGroup.setBlnCharDigits(AhRestoreCommons.convertStringToBoolean(blnchardigits));

			/**
			 * Set blncharletters
			 */
			colName = "blncharletters";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String blncharletters = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			userGroup.setBlnCharLetters(AhRestoreCommons.convertStringToBoolean(blncharletters));

			/**
			 * Set blncharspecial
			 */
			colName = "blncharspecial";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String blncharspecial = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			userGroup.setBlnCharSpecial(AhRestoreCommons.convertStringToBoolean(blncharspecial));

			/**
			 * Set concatenatestring
			 */
			colName = "concatenatestring";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String concatenatestring = isColPresent ? xmlParser.getColVal(i, colName) : "#";
			userGroup.setConcatenateString(AhRestoreCommons.convertStringNoTrim(concatenatestring));

			/**
			 * Set credentialtype
			 */
			colName = "credentialtype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String credentialtype = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.USERGROUP_CREDENTIAL_FLASH);
			userGroup.setCredentialType(AhRestoreCommons.convertInt(credentialtype));

			/**
			 * Set timezonestr
			 */
			colName = "timezonestr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			if (isColPresent) {
				userGroup.setTimeZoneStr(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));
			} else {
				/**
				 * get timezone index
				 */
				colName = "timezone";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"local_user_group", colName);
				if (isColPresent) {
					int timezoneIndex = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					if (RESTORE_FROM_40R1_BEFORE) {
						userGroup.setTimeZoneStr(HmBeOsUtil.getNewTimeZoneByOldOne(timezoneIndex));
					} else {
						userGroup.setTimeZoneStr(HmBeOsUtil.getTimeZoneString(timezoneIndex));
					}
				}
			}

			/**
			 * Set starttime
			 */
			colName = "starttime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String starttime = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (starttime == null) {
				userGroup.setStartTime(null);
			} else {
				userGroup.setStartTime(AhRestoreCommons.convertDate(starttime));
			}

			/**
			 * Set expiredtime
			 */
			colName = "expiredtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String expiredtime = isColPresent ? xmlParser.getColVal(i, colName) : null;
			if (expiredtime == null) {
				userGroup.setExpiredTime(null);
			} else {
				userGroup.setExpiredTime(AhRestoreCommons.convertDate(expiredtime));
			}

			/**
			 * Set personpskcombo
			 */
			colName = "personpskcombo";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String personpskcombo = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.PSKFORMAT_COMBO_AND);
			userGroup.setPersonPskCombo(AhRestoreCommons.convertInt(personpskcombo));

			/**
			 * Set pskgeneratemethod
			 */
			colName = "pskgeneratemethod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String pskgeneratemethod = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.PSK_METHOD_PASSWORD_ONLY);
			userGroup.setPskGenerateMethod(AhRestoreCommons.convertInt(pskgeneratemethod));

			/**
			 * Set psklength
			 */
			colName = "psklength";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String psklength = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(8);
			userGroup.setPskLength(AhRestoreCommons.convertInt(psklength));

			/**
			 * Set psklocation
			 */
			colName = "psklocation";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String psklocation = isColPresent ? xmlParser.getColVal(i, colName) : "";
			userGroup.setPskLocation(AhRestoreCommons.convertStringNoTrim(psklocation));

			/**
			 * Set psksecret
			 */
			colName = "psksecret";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String psksecret = isColPresent ? xmlParser.getColVal(i, colName) : "";
			userGroup.setPskSecret(AhRestoreCommons.convertStringNoTrim(psksecret));

			/**
			 * Set usernameprefix
			 */
			colName = "usernameprefix";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String usernameprefix = isColPresent ? xmlParser.getColVal(i, colName) : "";
			userGroup.setUserNamePrefix(AhRestoreCommons.convertString(usernameprefix));
			userGroup.setUserNamePrefix(userGroup.getUserNamePrefix().replaceAll("@", "_"));

			/**
			 * Set usertype
			 */
			colName = "usertype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String usertype = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
			userGroup.setUserType(AhRestoreCommons.convertInt(usertype));

			/**
			 * Set bulknumber
			 */
			colName = "bulknumber";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String bulknumber = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			userGroup.setBulkNumber(AhRestoreCommons.convertInt(bulknumber));

			/**
			 * Set indexrange
			 */
			colName = "indexrange";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String indexrange = isColPresent ? xmlParser.getColVal(i, colName) : "10";
			userGroup.setIndexRange(AhRestoreCommons.convertInt(indexrange));

			/**
			 * Set intervalday
			 */
			colName = "intervalday";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String intervalday = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			userGroup.setIntervalDay(AhRestoreCommons.convertInt(intervalday));

			/**
			 * Set intervalhour
			 */
			colName = "intervalhour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String intervalhour = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			userGroup.setIntervalHour(AhRestoreCommons.convertInt(intervalhour));

			/**
			 * Set intervalmin
			 */
			colName = "intervalmin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String intervalmin = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			userGroup.setIntervalMin(AhRestoreCommons.convertInt(intervalmin));

			/**
			 * Set blnBulktype
			 */
			colName = "blnBulktype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String blnBulktype = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			userGroup.setBlnBulkType(AhRestoreCommons.convertStringToBoolean(blnBulktype));

			/**
			 * Set validtimetype
			 */
			colName = "validtimetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String validtimetype = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.VALIDTYME_TYPE_ALWAYS);
			userGroup.setValidTimeType(AhRestoreCommons.convertInt(validtimetype));

			/**
			 * Set schedule_id
			 */
			colName = "schedule_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String schedule_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if (!schedule_id.equals("") && !schedule_id.trim().equalsIgnoreCase("null")) {
				Long newSch = AhRestoreNewMapTools.getMapSchedule(AhRestoreCommons.convertLong(schedule_id.trim()));
				userGroup.setSchedule(AhRestoreNewTools.CreateBoWithId(Scheduler.class, newSch));
			}

			/**
			 * Set radius_id
			 */
			colName = "radius_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			if (isColPresent) {
				Long radius_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				radius = AhRestoreNewMapTools.getMapOldLocalUserGroup(radius_id);
				if (null == radius) {
					radius = new HashSet<Long>();
					radius.add(userGroup.getId());
					AhRestoreNewMapTools.setMapOldLocalUserGroup(radius_id, radius);
				} else {
					radius.add(userGroup.getId());
				}
			}
			
			/**
			 * Set voice device
			 */
			colName = "voiceDevice";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user_group", colName);
			String voiceDevice = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			userGroup.setVoiceDevice(AhRestoreCommons.convertStringToBoolean(voiceDevice));
			
			group.add(userGroup);
		}

		return group.size() > 0 ? group : null;
	}

	/**
	 * Restore local_user_group table
	 *
	 * @return true if table of local_user_group restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreLocalUserGroup()
	{
		try
		{
			List<LocalUserGroup> allGroup = getAllLocalUserGroup();
			if(null != allGroup) {
				List<Long> lOldId = new ArrayList<Long>();

				for (LocalUserGroup group : allGroup) {
					lOldId.add(group.getId());
				}

				QueryUtil.restoreBulkCreateBos(allGroup);

				for(int i=0; i<allGroup.size(); i++)
				{
					AhRestoreNewMapTools.setMapLocalUserGroup(lOldId.get(i), allGroup.get(i).getId());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from local_user table
	 *
	 * @return List<LocalUser> all LocalUser BO
	 * @throws AhRestoreColNotExistException -
	 *             if local_user.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing local_user.xml.
	 */
	private static List<LocalUser> getAllLocalUser() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of local_user.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("local_user");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in local_user table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LocalUser> user = new ArrayList<LocalUser>();

		boolean isColPresent;
		String colName;
		LocalUser localUser;
		Set<LocalUserGroup> radius;

		for (int i = 0; i < rowCount; i++)
		{
			localUser = new LocalUser();

			/**
			 * Set username
			 */
			colName = "username";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'local_user' data be lost, cause: 'username' column is not exist.");
				continue;
			}
			localUser.setUserName(name);
			localUser.setUserName(name.replaceAll("@", "_"));

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			localUser.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set localuserpassword
			 */
			colName = "localuserpassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String localuserpassword = isColPresent ? xmlParser.getColVal(i, colName) : "";
			localUser.setLocalUserPassword(AhRestoreCommons.convertStringNoTrim(localuserpassword));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"local_user", colName);
			Long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'local_user' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			localUser.setOwner(ownerDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			localUser.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			localUser.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultflag));

			/**
			 * Set group_id
			 */
			colName = "group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String group_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			// the user must belong to one user group
			if(!"".equals(group_id)) {
				Long newGroup = AhRestoreNewMapTools.getMapLocalUserGroup(AhRestoreCommons.convertLong(group_id));
				if(null != newGroup) {
					localUser.setLocalUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, newGroup));
				} else {
					localUser.setLocalUserGroup(getDefaultLocalUserGroup(name, ownerDomain));
				}
			} else {
				localUser.setLocalUserGroup(getDefaultLocalUserGroup(name, ownerDomain));
			}

			/**
			 * Set usertype
			 */
			colName = "usertype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String usertype = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(LocalUserGroup.USERGROUP_USERTYPE_RADIUS);
			localUser.setUserType(AhRestoreCommons.convertInt(usertype));

			/**
			 * Set mailaddress
			 */
			colName = "mailaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String mailaddress = isColPresent ? xmlParser.getColVal(i, colName) : "";
			localUser.setMailAddress(AhRestoreCommons.convertString(mailaddress));

			/**
			 * Set revoked
			 */
			colName = "revoked";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String revoked = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			localUser.setRevoked(AhRestoreCommons.convertStringToBoolean(revoked));

			/**
			 * Set radius_id
			 */
			colName = "radius_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			if (isColPresent) {
				Long radius_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				radius = AhRestoreNewMapTools.getMapOldLocalUser(radius_id);
				if (null == radius) {
					radius = new HashSet<LocalUserGroup>();
					radius.add(localUser.getLocalUserGroup());
					AhRestoreNewMapTools.setMapOldLocalUser(radius_id, radius);
				} else {
					radius.add(localUser.getLocalUserGroup());
				}
			}

			/*
			 * activated
			 */
			colName = "activated";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String activated = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			localUser.setActivated(AhRestoreCommons.convertStringToBoolean(activated));

			/*
			 * status
			 */
			colName = "status";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String status = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(LocalUser.STATUS_FREE);
			localUser.setStatus(AhRestoreCommons.convertInt(status));

			/*
			 * visitorCompany
			 */
			colName = "visitorCompany";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String visitorCompany = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			localUser.setVisitorCompany(AhRestoreCommons.convertString(visitorCompany));

			/*
			 * sponsor
			 */
			colName = "sponsor";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String sponsor = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			localUser.setSponsor(AhRestoreCommons.convertString(sponsor));

			/*
			 * ssidName
			 */
			colName = "ssidName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String ssidName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			localUser.setSsidName(AhRestoreCommons.convertString(ssidName));

			/*
			 * visitorName
			 */
			colName = "visitorName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String visitorName = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			localUser.setVisitorName(AhRestoreCommons.convertString(visitorName));

			/*
			 * oldPPSK
			 */
			colName = "oldPPSK";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"local_user", colName);
			String oldPPSK = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			localUser.setOldPPSK(AhRestoreCommons.convertString(oldPPSK));

			user.add(localUser);
		}

		return user.size() > 0 ? user : null;
	}
	
	
	private static List<MACAuth> getAllMACAuth() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of local_user.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mac_auth");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in local_user table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MACAuth> macAuthList = new ArrayList<MACAuth>();

		boolean isColPresent;
		String colName;
		MACAuth macAuth;

		for (int i = 0; i < rowCount; i++)
		{
			macAuth = new MACAuth();

			/**
			 * Set studentId
			 */
			colName = "studentId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_auth", colName);
			String studentId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (studentId.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_auth' data be lost, cause: 'studentId' column is not exist.");
				continue;
			}
			macAuth.setStudentId(studentId);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_auth", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			macAuth.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set studentName
			 */
			colName = "studentName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_auth", colName);
			String studentName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (studentName.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_auth' data be lost, cause: 'studentName' column is not exist.");
				continue;
			}
			macAuth.setStudentName(studentName);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"local_user", colName);
			Long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'local_user' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			macAuth.setOwner(ownerDomain);

			/**
			 * Set macAddress
			 */
			colName = "macAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_auth", colName);
			String macAddress = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (macAddress.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_auth' data be lost, cause: 'macAddress' column is not exist.");
				continue;
			}
			macAuth.setMacAddress(macAddress);
			
			/**
			 * Set schoolId
			 */
			colName = "schoolId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mac_auth", colName);
			String schoolId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (schoolId.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mac_auth' data be lost, cause: 'schoolId' column is not exist.");
				continue;
			}
			macAuth.setSchoolId(schoolId);

			macAuthList.add(macAuth);
		}

		return macAuthList.size() > 0 ? macAuthList : null;
	}

	/**
	 * Get all information from print_template table
	 *
	 * @return List<PrintTemplate> a list of PrintTemplate objects
	 *
	 * @throws AhRestoreColNotExistException -
	 *             if print_template.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing print_template.xml.
	 */
	private static List<PrintTemplate> getAllPrintTemplate() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/*
		 * Check validation of print_template.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("print_template");

		if (!restoreRet)
		{
			return null;
		}

		/*
		 * No one row data stored in print_template table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PrintTemplate> templates = new ArrayList<PrintTemplate>();

		boolean isColPresent;
		String colName;
		PrintTemplate printTemplate;
		List<TemplateField> allTemplateFields = getAllTemplateFields();

		for (int i = 0; i < rowCount; i++)
		{
			printTemplate = new PrintTemplate();

			/*
			 * name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'print_template' data be lost, cause: 'name' column is not exist.");
				continue;
			}

			printTemplate.setName(name);

			/*
			 * id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";

			/*
			 * asDefault
			 */
			colName = "asdefault";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			boolean asDefault = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			printTemplate.setAsDefault(asDefault);

			/*
			 * enabled
			 */
			colName = "enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			boolean enabled = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			printTemplate.setEnabled(enabled);

			/*
			 * headerHTML
			 */
			colName = "headerhtml";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			String headerHTML = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			printTemplate.setHeaderHTML(headerHTML);

			/*
			 * footerHTML
			 */
			colName = "footerhtml";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			String footerHTML = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			printTemplate.setFooterHTML(footerHTML);

			/*
			 * template fields
			 */
			if(allTemplateFields != null) {
				Map<String, TemplateField> fields = new HashMap<String, TemplateField>();

				for(TemplateField field : allTemplateFields) {
					if(id.equals(field.getRestoreId())) {
						fields.put(field.getField(), field);
					}
				}

				printTemplate.setFields(fields);
			}

			/*
			 * owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"print_template", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}

			printTemplate.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/*
			 * defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"print_template", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "";

			if(AhRestoreCommons.convertStringToBoolean(defaultflag)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'print_template' data be lost, cause: 'defaultflag' column is not exist.");
				continue;
			}

			printTemplate.setDefaultFlag(false);


			templates.add(printTemplate);
		}

		return templates;
	}

	private static List<TemplateField> getAllTemplateFields() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "template_field";

		/*
		 * Check validation of template_field.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<TemplateField> templateFields = new ArrayList<TemplateField>();

		boolean isColPresent;
		String colName;
		TemplateField field;

		for (int i = 0; i < rowCount; i++)
		{
			field = new TemplateField();

			/*
			 * template_id
			 */
			colName = "template_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if ("".equals(id))
			{
				continue;
			}

			field.setRestoreId(id);

			/*
			 * label
			 */
			colName = "label";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String label = isColPresent ? xmlParser.getColVal(i, colName) : "";
			field.setLabel(AhRestoreCommons.convertString(label));

			/*
			 * required
			 */
			colName = "required";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String required = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			field.setRequired(AhRestoreCommons.convertStringToBoolean(required));

			/*
			 * place
			 */
			colName = "place";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String place = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			field.setPlace((byte)AhRestoreCommons.convertInt(place));

			/*
			 * mapkey
			 */
			colName = "mapkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String mapkey = isColPresent ? xmlParser.getColVal(i, colName) : "";

			for (String key : TemplateField.FIELDS) {
				if (mapkey.equals(key)) {
					field.setField(key);
				}
			}

			templateFields.add(field);
		}

		return templateFields.size() > 0 ? templateFields : null;

	}

	/**
	 * Get default user group for non group user
	 *
	 * @param userName -
	 * @param hmDom -
	 * @return LocalUserGroup
	 */
	private static LocalUserGroup getDefaultLocalUserGroup(String userName, HmDomain hmDom) {
		Long groupId = defaultUserGroups.get(hmDom.getId());
		if (null == groupId) {
			LocalUserGroup localGroup = QueryUtil.findBoByAttribute(LocalUserGroup.class,
				"groupName", LocalUserGroup.DEFAULT_GROUP_NAME_FOR_RESTORE, hmDom.getId());
			if (null == localGroup) {
				localGroup = new LocalUserGroup();
				localGroup.setGroupName(LocalUserGroup.DEFAULT_GROUP_NAME_FOR_RESTORE);
				localGroup.setDescription("for the users which don't belong to any group");
				localGroup.setOwner(hmDom);
				try {
					// create the default local user group
					groupId = QueryUtil.createBo(localGroup);

					/*
					 * create upgrade log for local user group
					 */
					HmUpgradeLog upgradeLog = new HmUpgradeLog();
					upgradeLog.setFormerContent("Some Local Users did not belong to any existing Local User Group.");
					upgradeLog.setPostContent("The Local User Group \""+LocalUserGroup.DEFAULT_GROUP_NAME_FOR_RESTORE+"\" is created automatically.");
					upgradeLog.setRecommendAction("If you don't need this Local User Group, manually remove it.");
					upgradeLog.setAnnotation("Click to add an annotation");
					upgradeLog.setOwner(hmDom);
					upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(), hmDom.getTimeZoneString()));
					QueryUtil.createBo(upgradeLog);
				} catch(Exception e) {
					AhRestoreDBTools.logRestoreMsg("Create default local user group failed : " + e.getMessage());
				}
			} else {
				groupId = localGroup.getId();
			}
			defaultUserGroups.put(hmDom.getId(), groupId);
		}
		try {
			/*
			 * create upgrade log for local user
			 */
			HmUpgradeLog upgradeLog = new HmUpgradeLog();
			upgradeLog.setFormerContent("The Local User \""+userName+"\" did not belong to any Local User Group.");
			upgradeLog.setPostContent("The Local User belong to the Local User Group \""+LocalUserGroup.DEFAULT_GROUP_NAME_FOR_RESTORE+"\" automatically.");
			upgradeLog.setRecommendAction("If the Local User need belong to another Local User Group, manually change it.");
			upgradeLog.setAnnotation("Click to add an annotation");
			upgradeLog.setOwner(hmDom);
			upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(), hmDom.getTimeZoneString()));
			QueryUtil.createBo(upgradeLog);
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Create local user upgrade log failed : " + e.getMessage());
		}
		return AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, groupId);
	}

	/**
	 * Restore local_user table
	 *
	 * @return true if table of local_user restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreLocalUser()
	{
		try
		{
			List<LocalUser> allUser = getAllLocalUser();
			if(null != allUser) {
				List<Long> lOldId = new ArrayList<Long>();

				for (LocalUser user : allUser) {
					lOldId.add(user.getId());
				}

				QueryUtil.restoreBulkCreateBos(allUser);

				for(int i=0; i<allUser.size(); i++)
				{
					AhRestoreNewMapTools.setMapLocalUser(lOldId.get(i), allUser.get(i).getId());
				}
			}
			
			
			// remove duplicate name radius user 
			StringBuilder builder = new StringBuilder();
			builder.append("select owner,lower(userName) as username,count(id)")
					.append(" from LOCAL_USER where userType = 1 group by 1,2 having count(id)>1");
			List<?> qureyObj = QueryUtil.executeNativeQuery(builder.toString());
			List<HmUpgradeLog> lstLog = new ArrayList<HmUpgradeLog>();
			if (qureyObj!=null && !qureyObj.isEmpty()){
				for(Object obj: qureyObj) {
					Object[] oneItem = (Object[]) obj;
					Long owner = Long.parseLong(oneItem[0].toString());
					String name = oneItem[1].toString();
					List<LocalUser> lstUser = QueryUtil.executeQuery(LocalUser.class,
							null, new FilterParams("userType=:s1 and lower(userName)=:s2 and owner.id=:s3",
									new Object[]{1, name,owner})); 
					for (int i=0; i<lstUser.size()-1; i++) {
						HmDomain domain = QueryUtil.findBoById(HmDomain.class, owner);
						HmTimeStamp logTime = new HmTimeStamp(System.currentTimeMillis(),domain.getTimeZoneString());
						HmUpgradeLog logbo = new HmUpgradeLog();
						logbo.setFormerContent("Because the local RADIUS user database was case sensitive, it could store users with the same name capitalized differently.");
						logbo.setPostContent("The local database is now case insensitive, so users with duplicate names (" + lstUser.get(i).getUserName() + ") will automatically be removed.");
						logbo.setRecommendAction("Create new users for the ones that were automatically removed.");
						logbo.setOwner(domain);
						logbo.setLogTime(logTime);
						logbo.setAnnotation("Click to add an annotation");
				
						QueryUtil.removeBo(LocalUser.class, lstUser.get(i).getId());
						lstLog.add(logbo);
					}
				}
			}
			
			if (!lstLog.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(lstLog);
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean restoreMACAuth()
	{
		try
		{
			List<MACAuth> macAuthList = getAllMACAuth();
			if(null != macAuthList) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MACAuth macAuth : macAuthList) {
					lOldId.add(macAuth.getId());
				}

				QueryUtil.restoreBulkCreateBos(macAuthList);

				for(int i=0; i<macAuthList.size(); i++)
				{
					AhRestoreNewMapTools.setMapMacAuth(lOldId.get(i), macAuthList.get(i).getId());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * restore print_template table
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public static boolean restorePrintTemplate()
	{
		try {
			List<PrintTemplate> printTemplates = getAllPrintTemplate();

			if(null != printTemplates) {
				QueryUtil.restoreBulkCreateBos(printTemplates);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Get all information from radius_service table
	 *
	 * @param radiusId and radiusName
	 * @param radiusName -
	 * @param hmDom -
	 * @return List<RadiusServer> all RadiusServer
	 * @throws AhRestoreColNotExistException -
	 *             if radius_service.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_service.xml.
	 */
	private static List<RadiusServer> getRadiusServerInfoById(String radiusId, String radiusName, HmDomain hmDom) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_service.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_service");
		if (null == radiusId || null == radiusName || !restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radius_service table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusServer> serverInfo = new ArrayList<RadiusServer>();

		boolean isColPresent;
		String colName;
		RadiusServer singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadiusServer();

			/**
			 * Set assignment_id
			 */
			colName = "assignment_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id) || !radiusId.equals(id))
			{
				continue;
			}
			singleInfo.setRestoreId(id);

			/**
			 * Set description
			 */
//			colName = "description";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				"radius_service", colName);
//			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set nameflag
			 */
			colName = "nameflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			if (isColPresent) {
				short nameflag = (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
				if (IpAddress.TYPE_HOST_NAME == nameflag) {
					/**
					 * Set servername
					 */
					colName = "servername";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"radius_service", colName);
					String servername = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
					if (!"".equals(servername)) {
						singleInfo.setIpAddress(CreateObjectAuto.createNewIP(servername, nameflag, hmDom, "For AAA Client Setting:"+radiusName));
					}
				}
			}

			/**
			 * Set serverpriority
			 */
			colName = "serverpriority";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			String serverpriority = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setServerPriority((short)AhRestoreCommons.convertInt(serverpriority));

			/**
			 * Set sharedsecret
			 */
			colName = "sharedsecret";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			String sharedsecret = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setSharedSecret(AhRestoreCommons.convertString(sharedsecret));



			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(ipId)) {
				Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(ipId));
				if(null != newIp) {
					singleInfo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));
				}
			}

			/**
			 * Set authport
			 */
			colName = "authport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			String authport = isColPresent ? xmlParser.getColVal(i, colName) : "1812";
			singleInfo.setAuthPort(AhRestoreCommons.convertInt(authport));

			/**
			 * Set acctport
			 */
			colName = "acctport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			int acctport = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 1813;

			/**
			 * Set enableacc
			 */
			colName = "enableacc";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service", colName);
			if (isColPresent) {
				boolean enableacc = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				if (enableacc) {
					singleInfo.setAcctPort(acctport);
				} else {
					singleInfo.setServerType(RadiusServer.RADIUS_SERVER_TYPE_AUTH);
				}
			} else {
				/**
				 * Set servertype
				 */
				colName = "servertype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_service", colName);
				String servertype = isColPresent ? xmlParser.getColVal(i, colName) : "1";
				singleInfo.setServerType((short)AhRestoreCommons.convertInt(servertype));
			}

			serverInfo.add(singleInfo);
		}

		return serverInfo.size() > 0 ? serverInfo : null;
	}

	/**
	 * Get all information from radius_service_assign table
	 *
	 * @return List<RadiusAssignment> all RadiusServiceAssign BO
	 * @throws AhRestoreColNotExistException -
	 *             if radius_service_assign.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_service_assign.xml.
	 */
	private static List<RadiusAssignment> getAllRadiusServer() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_service_assign.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_service_assign");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radius_service_assign table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusAssignment> radius = new ArrayList<RadiusAssignment>();

		boolean isColPresent;
		String colName;
		RadiusAssignment singleRadius;

		for (int i = 0; i < rowCount; i++)
		{
			singleRadius = new RadiusAssignment();

			/**
			 * Set radiusname
			 */
			colName = "radiusname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_service_assign' data be lost, cause: 'radiusname' column is not exist.");
				continue;
			}
			singleRadius.setRadiusName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set retryinterval
			 */
			colName = "retryinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String retryinterval = isColPresent ? xmlParser.getColVal(i, colName) : "600";
			singleRadius.setRetryInterval(AhRestoreCommons.convertInt(retryinterval));

			/**
			 * Set updateinterval
			 */
			colName = "updateinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String updateinterval = isColPresent ? xmlParser.getColVal(i, colName) : "20";
			singleRadius.setUpdateInterval(AhRestoreCommons.convertInt(updateinterval));

			/**
			 * Set enableextensionradius
			 */
			colName = "enableextensionradius";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String enableextensionradius = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setEnableExtensionRadius(AhRestoreCommons.convertStringToBoolean(enableextensionradius));

			/**
			 * Set injectOperatorNmAttri
			 */
			colName = "injectOperatorNmAttri";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String injectOperatorNmAttri = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setInjectOperatorNmAttri(AhRestoreCommons.convertStringToBoolean(injectOperatorNmAttri));

			/**
			 * Set owner
			 */

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_service_assign", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_service_assign' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
			singleRadius.setOwner(owner);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_service_assign", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRadius.setDescription(AhRestoreCommons.convertString(description));

	         /**
             * Set enableDHCP4RadiusServer
             */
            colName = "enableDHCP4RadiusServer";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                "radius_service_assign", colName);
            String enableDHCP4RadiusServer = isColPresent ? xmlParser.getColVal(i, colName) : "";
            singleRadius.setEnableDHCP4RadiusServer(AhRestoreCommons.convertStringToBoolean(enableDHCP4RadiusServer));

			singleRadius.setServices(getRadiusServerInfoById(id, name, owner));
			radius.add(singleRadius);
		}

		return radius.size() > 0 ? radius : null;
	}

	/**
	 * Restore radius_service_assign table
	 *
	 * @return true if table of radius_service_assign restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadiusAssignment()
	{
		try
		{
			List<RadiusAssignment> allRadius = getAllRadiusServer();
			if(null != allRadius) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusAssignment radius : allRadius) {
					lOldId.add(radius.getId());
				}

				QueryUtil.restoreBulkCreateBos(allRadius);

				for(int i=0; i<allRadius.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadiusServerAssign(lOldId.get(i), allRadius.get(i).getId());
				}
			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from radius_ad_domain table
	 *
	 * @param mapLog -
	 * @return Map<String, List<ActiveDirectoryDomain>>
	 * @throws AhRestoreColNotExistException -
	 *             if radius_ad_domain.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_ad_domain.xml.
	 */
	private static Map<String, List<ActiveDirectoryDomain>> getAllMultipleDomainInfo(Map<String, HmUpgradeLog> mapLog) throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_ad_domain.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_ad_domain");
		if (!restoreRet || null == mapLog) {
			return null;
		}

		/**
		 * No one row data stored in radius_ad_domain table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<ActiveDirectoryDomain>> allInfo = new HashMap<String, List<ActiveDirectoryDomain>>();
		List<ActiveDirectoryDomain> domainInfo;

		boolean isColPresent;
		String colName;
		ActiveDirectoryDomain singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new ActiveDirectoryDomain();

			/**
			 * Set ad_domain_id
			 */
			colName = "ad_domain_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set domain
			 */
			colName = "domain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String domain = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDomain(AhRestoreCommons.convertString(domain));

			/**
			 * Set binddnname
			 */
			colName = "binddnname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String binddnname = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setBindDnName(AhRestoreCommons.convertString(binddnname));

			/**
			 * Set binddnpass
			 */
			colName = "binddnpass";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String binddnpass = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setBindDnPass(AhRestoreCommons.convertStringNoTrim(binddnpass));

			/**
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultflag));

			/**
			 * Set fullname
			 */
			colName = "fullname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			String fullname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			//if (singleInfo.isDefaultFlag()) {
				singleInfo.setFullName(fullname);
			//}

			/**
			 * Set server
			 */
			colName = "server";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_ad_domain", colName);
			if (isColPresent) {
				if (!singleInfo.isDefaultFlag()) {
					singleInfo.setServer(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));
				}
			} else {
				// write the upgrade log
				HmUpgradeLog upgradeLog = mapLog.get(id);
				if (null == upgradeLog) {
					upgradeLog = new HmUpgradeLog();
				}
				String baseDn = AhRestoreCommons.convertString(xmlParser.getColVal(i, "basedn"));
				String former = upgradeLog.getFormerContent();
				upgradeLog.setFormerContent((null == former ? "" : former) + (singleInfo.isDefaultFlag()?" default domain \""+singleInfo.getDomain()
					+"\" has baseDN \""+baseDn+"\" ":"undefault domain \""+singleInfo.getDomain()
					+"\" does not have server but has baseDN \""+baseDn+"\" and full name \""+fullname+"\" "));
				String post = upgradeLog.getPostContent();
				upgradeLog.setPostContent((null == post ? "" : post) + (singleInfo.isDefaultFlag()?" default domain \""+singleInfo.getDomain()
					+"\" does not have baseDN ":"undefault domain \""+singleInfo.getDomain()
					+"\" does not have full name and baseDN but has server \""+fullname+"\" "));
				mapLog.put(id, upgradeLog);
				singleInfo.setServer(singleInfo.isDefaultFlag()? "" : fullname);
			}

			if (null == allInfo.get(id)) {
				domainInfo = new ArrayList<ActiveDirectoryDomain>();
				domainInfo.add(singleInfo);
				allInfo.put(id, domainInfo);
			} else {
				allInfo.get(id).add(singleInfo);
			}
		}

		return allInfo;
	}

	/**
	 * Get all information from radius local user group table
	 *
	 * @param arg_Table : the radius local user group table name
	 * @return Map<String, Set<LocalUserGroup>> all LocalUserGroup
	 * @throws AhRestoreColNotExistException -
	 *             if radius_on_hiveap_local_group.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_on_hiveap_local_group.xml.
	 */
	private static Map<String, Set<LocalUserGroup>> getAllLocalUserGroupInfo(String arg_Table)
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		// check the parameter
		if (null == arg_Table || "".equals(arg_Table)) {
			return null;
		}
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of arg_Table.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(arg_Table);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<LocalUserGroup>> ruleInfo = new HashMap<String, Set<LocalUserGroup>>();

		boolean isColPresent;
		String colName;
		Set<LocalUserGroup> singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set radius_on_hiveap_id
			 */
			colName = "radius_on_hiveap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				arg_Table, colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			singleInfo = ruleInfo.get(id);

			/**
			 * Set local_user_group_id
			 */
			colName = "local_user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				arg_Table, colName);
			String group_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			if (!"".equals(group_id)) {
				Long newGroup = AhRestoreNewMapTools.getMapLocalUserGroup(AhRestoreCommons.convertLong(group_id));
				if(null != newGroup) {
					LocalUserGroup group = AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, newGroup);
					if(singleInfo == null) {
						singleInfo = new HashSet<LocalUserGroup>();
						singleInfo.add(group);
						ruleInfo.put(id, singleInfo);
					} else {
						singleInfo.add(group);
					}
				}
			}
		}

		return ruleInfo;
	}

	/**
	 * Get all information from radius_on_hiveap_local_user table
	 *
	 * @return Map<String, Set<LocalUser>> all LocalUser
	 * @throws AhRestoreColNotExistException -
	 *             if radius_on_hiveap_local_user.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_on_hiveap_local_user.xml.
	 */
	private static Map<String, Set<LocalUser>> getAllLocalUserInfo()
		throws AhRestoreColNotExistException,
		AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_on_hiveap_local_user.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_on_hiveap_local_user");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<LocalUser>> ruleInfo = new HashMap<String, Set<LocalUser>>();

		boolean isColPresent;
		String colName;
		Set<LocalUser> singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			/**
			 * Set radius_on_hiveap_id
			 */
			colName = "radius_on_hiveap_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap_local_user", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id)) {
				continue;
			}
			singleInfo = ruleInfo.get(id);

			/**
			 * Set local_user_id
			 */
			colName = "local_user_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_on_hiveap_local_user", colName);
			String user_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			if (!"".equals(user_id)) {
				Long newUser = AhRestoreNewMapTools.getMapLocalUser(AhRestoreCommons.convertLong(user_id));
				if(null != newUser) {
					LocalUser user = AhRestoreNewTools.CreateBoWithId(LocalUser.class, newUser);
					if(singleInfo == null) {
						singleInfo = new HashSet<LocalUser>();
						singleInfo.add(user);
						ruleInfo.put(id, singleInfo);
					} else {
						singleInfo.add(user);
					}
				}
			}
		}

		return ruleInfo;
	}

	/*
	 * For Radius Proxy
	 */
	/**
	 * Get all information from radius_proxy_nas table
	 *
	 * @return Map<String, List<RadiusHiveapAuth>> all RadiusHiveapAuth
	 * @throws AhRestoreColNotExistException -
	 *             if radius_proxy_nas.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_proxy_nas.xml.
	 */
	private static Map<String, List<RadiusHiveapAuth>> getRadiusNasInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_proxy_nas.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_proxy_nas");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in radius_proxy_nas table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<RadiusHiveapAuth>> allNas = new HashMap<String, List<RadiusHiveapAuth>>();
		List<RadiusHiveapAuth> authInfo;

		boolean isColPresent;
		String colName;
		RadiusHiveapAuth singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadiusHiveapAuth();

			/**
			 * Set radius_proxy_id
			 */
			colName = "radius_proxy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_nas", colName);
			String proId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(proId)) {
				continue;
			}
			authInfo = allNas.get(proId);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_nas", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set sharedkey
			 */
			colName = "sharedkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_nas", colName);
			String sharedkey = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setSharedKey(AhRestoreCommons.convertString(sharedkey));

			/**
			 * Set ip_address_id
			 */
			colName = "ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_nas", colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(ipId)) {
				Long newId = AhRestoreNewMapTools.getMapIpAdddress(Long.valueOf(ipId));
				if (null != newId) {
					singleInfo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newId));
				}
			}

			if (null == authInfo) {
				authInfo = new ArrayList<RadiusHiveapAuth>();
				authInfo.add(singleInfo);
				allNas.put(proId, authInfo);
			} else {
				authInfo.add(singleInfo);
			}
		}

		return allNas.isEmpty() ? null : allNas;
	}

	/**
	 * Get all information from radius_proxy_realm table
	 *
	 * @return Map<String, List<RadiusProxyRealm>> all RadiusProxyRealm
	 * @throws AhRestoreColNotExistException -
	 *             if radius_proxy_realm.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_proxy_realm.xml.
	 */
	private static Map<String, List<RadiusProxyRealm>> getRadiusRealmInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_proxy_realm.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_proxy_realm");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in radius_proxy_realm table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<RadiusProxyRealm>> allRealm = new HashMap<String, List<RadiusProxyRealm>>();
		List<RadiusProxyRealm> realmInfo;

		boolean isColPresent;
		String colName;
		RadiusProxyRealm singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadiusProxyRealm();

			/**
			 * Set radius_proxy_id
			 */
			colName = "radius_proxy_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_realm", colName);
			String proId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(proId)) {
				continue;
			}
			realmInfo = allRealm.get(proId);

			/**
			 * Set servername (actually it is the realm name)
			 *
			 * there are two default values : default and null
			 */
			colName = "servername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_realm", colName);
			String servername = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setServerName("_Null_".equalsIgnoreCase(servername)?RadiusProxyRealm.NULL_REALM_NAME:servername);

			/**
			 * Set strip
			 */
			colName = "strip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_realm", colName);
			String strip = isColPresent ? xmlParser.getColVal(i, colName) : "true";
			singleInfo.setStrip(AhRestoreCommons.convertStringToBoolean(strip));

			/**
			 * Set radius_server_id
			 */
			colName = "radius_server_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy_realm", colName);
			String radiusId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(radiusId)) {
				Long newId = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.valueOf(radiusId));
				if (null != newId) {
					singleInfo.setRadiusServer(AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class, newId));
				}
			}
			
	         /**
             * Set useIDM
             */
            colName = "useIDM";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                "radius_proxy_realm", colName);
            String useIDM = isColPresent ? xmlParser.getColVal(i, colName) : "false";
            singleInfo.setUseIDM(AhRestoreCommons.convertStringToBoolean(useIDM));
            
            /**
             * Set tlsPort
             */
            colName = "tlsPort";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
                    "radius_proxy_realm", colName);
            String tlsPort = isColPresent ? xmlParser.getColVal(i, colName) : "0";
            singleInfo.setTlsPort(AhRestoreCommons.convertInt(tlsPort));

			if (null == realmInfo) {
				realmInfo = new ArrayList<RadiusProxyRealm>();
				realmInfo.add(singleInfo);
				allRealm.put(proId, realmInfo);
			} else {
				realmInfo.add(singleInfo);
			}
		}

		return allRealm.isEmpty() ? null : allRealm;
	}

	/**
	 * Get all information from radius_proxy table
	 *
	 * @return List<RadiusProxy> all RadiusProxy BO
	 * @throws AhRestoreColNotExistException -
	 *             if radius_proxy.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_proxy.xml.
	 */
	private static List<RadiusProxy> getAllRadiusProxy() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_proxy.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_proxy");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radius_proxy table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusProxy> proxy = new ArrayList<RadiusProxy>();

		boolean isColPresent;
		String colName;
		RadiusProxy singleProxy;
		Map<String, List<RadiusProxyRealm>> radiusRealm = null;
		Map<String, List<RadiusHiveapAuth>> radiusNas = null;
		if (rowCount > 0) {
			radiusRealm = getRadiusRealmInfo();
			radiusNas = getRadiusNasInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singleProxy = new RadiusProxy();

			/**
			 * Set proxyname
			 */
			colName = "proxyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_proxy' data be lost, cause: 'proxyname' column is not exist.");
				continue;
			}
			singleProxy.setProxyName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if ("".equals(id)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_proxy' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			singleProxy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set proxyformat
			 */
			colName = "proxyformat";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String proxyformat = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleProxy.setProxyFormat((short)AhRestoreCommons.convertInt(proxyformat));

			/**
			 * Set retrydelay
			 */
			colName = "retrydelay";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String retrydelay = isColPresent ? xmlParser.getColVal(i, colName) : "5";
			singleProxy.setRetryDelay((short)AhRestoreCommons.convertInt(retrydelay));

			/**
			 * Set retrycount
			 */
			colName = "retrycount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String retrycount = isColPresent ? xmlParser.getColVal(i, colName) : "3";
			singleProxy.setRetryCount((short)AhRestoreCommons.convertInt(retrycount));

			/**
			 * Set deadtime
			 */
			colName = "deadtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String deadtime = isColPresent ? xmlParser.getColVal(i, colName) : "300";
			singleProxy.setDeadTime(AhRestoreCommons.convertInt(deadtime));

			/**
			 * Set injectOperatorNmAttri
			 */
			colName = "injectOperatorNmAttri";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String injectOperatorNmAttri = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleProxy.setInjectOperatorNmAttri(AhRestoreCommons.convertStringToBoolean(injectOperatorNmAttri));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_proxy", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			final HmDomain hmDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == hmDomain)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_proxy' data be lost, cause: 'owner' column is not exist.");
				continue;
			}
			singleProxy.setOwner(hmDomain);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_proxy", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleProxy.setDescription(AhRestoreCommons.convertString(description));

			if (null != radiusRealm) {
				singleProxy.setRadiusRealm(radiusRealm.get(id));
			}
			if (null != radiusNas) {
				singleProxy.setRadiusNas(radiusNas.get(id));
			}
			proxy.add(singleProxy);
		}

		return proxy.size() > 0 ? proxy : null;
	}

	/**
	 * Restore radius proxy table
	 *
	 * @return true if table of radius_proxy restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadiusProxy()
	{
		try {
			List<RadiusProxy> allProxy = getAllRadiusProxy();

			if(null != allProxy) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusProxy proxy : allProxy) {
					lOldId.add(proxy.getId());
				}

				QueryUtil.restoreBulkCreateBos(allProxy);

				for(int i=0; i<allProxy.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadiusProxy(lOldId.get(i), allProxy.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Get all information from sip_policy_rule table
	 *
	 * @return Map<String, List<RadiusLibrarySipRule>> all RadiusLibrarySipRule
	 * @throws AhRestoreColNotExistException -
	 *             if sip_policy_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing sip_policy_rule.xml.
	 */
	private static Map<String, List<RadiusLibrarySipRule>> getRadiusSipRuleInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of sip_policy_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("sip_policy_rule");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in sip_policy_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<RadiusLibrarySipRule>> allRule = new HashMap<String, List<RadiusLibrarySipRule>>();
		List<RadiusLibrarySipRule> ruleInfo;

		boolean isColPresent;
		String colName;
		RadiusLibrarySipRule singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new RadiusLibrarySipRule();

			/**
			 * Set radius_library_sip_id
			 */
			colName = "radius_library_sip_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String proId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(proId)) {
				continue;
			}
			ruleInfo = allRule.get(proId);

			/**
			 * Set field
			 */
			colName = "field";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String field = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setField(field);

			/**
			 * Set operator
			 */
			colName = "operator";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String operator = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setOperator((short)AhRestoreCommons.convertInt(operator));

			/**
			 * Set ruleid
			 */
			colName = "ruleid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String ruleid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setRuleId((short)AhRestoreCommons.convertInt(ruleid));

			/**
			 * Set valuestr
			 */
			colName = "valuestr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String valuestr = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setValueStr(valuestr);

			/**
			 * Set action
			 */
			colName = "action";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String action = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleInfo.setAction((short)AhRestoreCommons.convertInt(action));

			/**
			 * Set message
			 */
			colName = "message";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String message = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singleInfo.setMessage(message);

			/**
			 * Set user_group_id
			 */
			colName = "user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"sip_policy_rule", colName);
			String groupId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(groupId)) {
				Long newId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.valueOf(groupId));
				if (null != newId) {
					singleInfo.setUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, newId));
				}
			}

			if (null == ruleInfo) {
				ruleInfo = new ArrayList<RadiusLibrarySipRule>();
				ruleInfo.add(singleInfo);
				allRule.put(proId, ruleInfo);
			} else {
				ruleInfo.add(singleInfo);
			}
		}

		return allRule.isEmpty() ? null : allRule;
	}

	/**
	 * Get all information from radius_library_sip table
	 *
	 * @return List<RadiusLibrarySip> all RadiusLibrarySip BO
	 * @throws AhRestoreColNotExistException -
	 *             if radius_library_sip.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_library_sip.xml.
	 */
	private static List<RadiusLibrarySip> getAllRadiusLibrarySip() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_library_sip.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_library_sip");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in radius_library_sip table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusLibrarySip> sipPolicy = new ArrayList<RadiusLibrarySip>();

		boolean isColPresent;
		String colName;
		RadiusLibrarySip singlePolicy;
		Map<String, List<RadiusLibrarySipRule>> sipRule = null;
		if (rowCount > 0) {
			sipRule = getRadiusSipRuleInfo();
		}

		for (int i = 0; i < rowCount; i++)
		{
			singlePolicy = new RadiusLibrarySip();

			/**
			 * Set policyname
			 */
			colName = "policyname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_library_sip' data be lost, cause: 'policyname' column is not exist.");
				continue;
			}
			singlePolicy.setPolicyName(name);

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if ("".equals(id)) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_library_sip' data be lost, cause: 'id' column is not exist.");
				continue;
			}
			singlePolicy.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set default_user_group_id
			 */
			colName = "default_user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String groupId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(groupId)) {
				Long newId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.valueOf(groupId));
				if (null != newId) {
					singlePolicy.setDefUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, newId));
				}
			}

			/**
			 * Set defaction
			 */
			colName = "defaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String defaction = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singlePolicy.setDefAction((short)AhRestoreCommons.convertInt(defaction));

			/**
			 * Set defmessage
			 */
			colName = "defmessage";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String message = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singlePolicy.setDefMessage(message);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_library_sip", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_library_sip' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			singlePolicy.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_library_sip", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singlePolicy.setDescription(AhRestoreCommons.convertString(description));

			if (null != sipRule) {
				singlePolicy.setRules(sipRule.get(id));
			}
			sipPolicy.add(singlePolicy);
		}

		return sipPolicy.size() > 0 ? sipPolicy : null;
	}

	/**
	 * Restore radius library sip table
	 *
	 * @return true if table of radius_library_sip restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadiusLibrarySip()
	{
		try {
			List<RadiusLibrarySip> allSip = getAllRadiusLibrarySip();

			if(null != allSip) {
				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusLibrarySip sip : allSip) {
					lOldId.add(sip.getId());
				}

				QueryUtil.restoreBulkCreateBos(allSip);

				for(int i=0; i<allSip.size(); i++)
				{
					AhRestoreNewMapTools.setMapRadiusLibrarySip(lOldId.get(i), allSip.get(i).getId());
				}
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<LdapServerOuUserProfile> changeUserGroupToNewDesign(Set<LocalUserGroup> groupInfo, short dbType, Long serverId,
		Long ownerId, List<HmUpgradeLog> lstLogBo) {
		List<LdapServerOuUserProfile> ldapOuUserProfiles = null;
		if (null != groupInfo && !groupInfo.isEmpty()) {
			ldapOuUserProfiles = new ArrayList<LdapServerOuUserProfile>();
			LdapServerOuUserProfile oneGroupObj;
			int rowId = 1;
			Set<Long> groupIds = new HashSet<Long>();
			for (LocalUserGroup group : groupInfo) {
				groupIds.add(group.getId());
			}
			// get all user profile attribute
			List<?> boAttris = QueryUtil.executeQuery("select userProfileId, id, groupName from " + LocalUserGroup.class.getSimpleName(), null,
				new FilterParams("id", groupIds));
			Map<Long, Integer> attributes = new HashMap<Long, Integer>();
			for (int i = 0; i < boAttris.size(); i++) {
				Object[] idValues = (Object[])boAttris.get(i);
				attributes.put((Long)idValues[1], i);
			}

			for (LocalUserGroup group : groupInfo) {
				if (null == attributes.get(group.getId())) {
					continue;
				}

				int attributeIndex = attributes.get(group.getId());

				Object[] idValues = (Object[])boAttris.get(attributeIndex);

				oneGroupObj = new LdapServerOuUserProfile();

				int attribute = (Integer)idValues[0];

				// user profile name and id
				List<?> boNames = QueryUtil.executeQuery("select userProfileName, id from " + UserProfile.class.getSimpleName(), null,
					new FilterParams("attributeValue", (short)attribute), ownerId);
				if (!boNames.isEmpty()) {
					Object[] upValues = (Object[])boNames.get(0);
					oneGroupObj.setUserProfileName((String)upValues[0]);
					oneGroupObj.setUserProfileId((Long)upValues[1]);
				} else {
					HmUpgradeLog upgradeLog = new HmUpgradeLog();
					upgradeLog.setFormerContent("LDAP user groups were mapped to the local user group \""+idValues[2]+"\"");
					upgradeLog.setPostContent("Because no user profile has the same attribute as the LDAP groups ("+attribute+"), the mapped relationship was removed.");
					lstLogBo.add(upgradeLog);
					continue;
				}
				oneGroupObj.setLocalUserGroup(group);
				oneGroupObj.setRowId(rowId++);
				oneGroupObj.setServerId(serverId);
				oneGroupObj.setTypeFlag(dbType);
				oneGroupObj.setGroupAttributeValue(attribute == -1 ? "" : String.valueOf(attribute));
				ldapOuUserProfiles.add(oneGroupObj);
			}
		}
		return ldapOuUserProfiles;
	}

	/**
	 * Get all information from radius_hiveap_ldap_user_profile table
	 *
	 * @return Map<String, List<LdapServerOuUserProfile>>
	 * @throws AhRestoreColNotExistException -
	 *             if radius_hiveap_ldap_user_profile.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_hiveap_ldap_user_profile.xml.
	 */
	private static Map<String, List<LdapServerOuUserProfile>> getAllAdOrLdapUserGroupInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of radius_hiveap_ldap_user_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_hiveap_ldap_user_profile");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in radius_hiveap_ldap_user_profile table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<LdapServerOuUserProfile>> allInfo = new HashMap<String, List<LdapServerOuUserProfile>>();
		List<LdapServerOuUserProfile> groupInfo;

		boolean isColPresent;
		String colName;
		LdapServerOuUserProfile singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new LdapServerOuUserProfile();

			/**
			 * Set ldap_user_profile_id
			 */
			colName = "ldap_user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String radiusId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(radiusId)) {
				continue;
			}

			/**
			 * Set groupattributevalue
			 */
			colName = "groupattributevalue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String attribute = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setGroupAttributeValue(AhRestoreCommons.convertString(attribute));

			/**
			 * Set local_user_group_id
			 */
			colName = "local_user_group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String groupId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(groupId)) {
				Long newId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.valueOf(groupId));
				if (null != newId) {
					singleInfo.setLocalUserGroup(AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class, newId));
				}
			}

			/**
			 * Set rowid
			 */
			colName = "rowid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String rowid = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setRowId(AhRestoreCommons.convertInt(rowid));

			/**
			 * Set serverid (AD or LDAP id)
			 */
			colName = "serverid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String serverid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(serverid)) {
				Long newId = AhRestoreNewMapTools.getMapDirectoryOrLdap(Long.valueOf(serverid));
				singleInfo.setServerId(newId);
			}

			/**
			 * Set typeflag
			 */
			colName = "typeflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String typeflag = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleInfo.setTypeFlag((short)AhRestoreCommons.convertInt(typeflag));

			/**
			 * Set userprofilename
			 */
			colName = "userprofilename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			singleInfo.setUserProfileName(AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)));

			/**
			 * Set userprofileid
			 */
			colName = "userprofileid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_hiveap_ldap_user_profile", colName);
			String userprofileid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if (!"".equals(userprofileid)) {
				Long newId = AhRestoreNewMapTools.getMapUserProfile(Long.valueOf(userprofileid));
				if (null == newId)
					continue;
				singleInfo.setUserProfileId(newId);
			}

			if (null == allInfo.get(radiusId)) {
				groupInfo = new ArrayList<LdapServerOuUserProfile>();
				groupInfo.add(singleInfo);
				allInfo.put(radiusId, groupInfo);
			} else {
				allInfo.get(radiusId).add(singleInfo);
			}
		}

		return allInfo;
	}

	public static boolean isDataFromOldVersionForTimeZone(float fVersion)
	{
		BeVersionInfo oInfo = NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH+File.separatorChar+".."+File.separatorChar+"hivemanager.ver");

		String strMainVersion = oInfo.getMainVersion();

		if(null == strMainVersion || "".equalsIgnoreCase(strMainVersion))
		{
			//error
			//add log
			BeLogTools.debug(HmLogConst.M_RESTORE, "could not find main version in restore file");

			return false;
		}

		try
		{
			float f = Float.parseFloat(strMainVersion);

			if( f < fVersion)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "time zone change");

				return true;
			}
			else
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "time zone not change");

				return false;
			}

		}
		catch(Exception ex)
		{
			BeLogTools.restoreLog(BeLogTools.ERROR, "Version "+strMainVersion+" cannot parse to float from string");

			return false;
		}
	}

	/**
	 * 
	 * version in hivemanger.ver is like below:
	 * MAINVERSION=6.1
	 * SUBVERSION=3
	 * 
	 * @param mainVersion
	 * @param subVersion
	 * @return
	 */
	public static boolean isDataFromOldVersion(float mainVersion, int subVersion) {
		BeVersionInfo oInfo = NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH+File.separatorChar+".."+File.separatorChar+"hivemanager.ver");

		String strMainVersion = oInfo.getMainVersion();
		String strSubVersion = oInfo.getSubVersion();

		if(null == strMainVersion || "".equalsIgnoreCase(strMainVersion)) {
			//error
			//add log
			BeLogTools.debug(HmLogConst.M_RESTORE, "could not find main version in restore file");

			return false;
		}

		try {
			float fMainVer = Float.parseFloat(strMainVersion);
			if( fMainVer < mainVersion) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "time zone change");

				return true;
			} else if ( fMainVer == mainVersion) {
				if (StringUtils.isEmpty(strSubVersion)) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "could not find sub version in restore file");
					return false;
				}
				
				int fSubVer = Integer.parseInt(strSubVersion);
				if (fSubVer < subVersion) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		catch(Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, "Version "+strMainVersion+" cannot parse to float from string");

			return false;
		}
	}

}