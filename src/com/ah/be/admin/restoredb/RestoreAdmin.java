package com.ah.be.admin.restoredb;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.be.os.LinuxServerTimeManager;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.AirtightSettings;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmAutoRefresh;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmLocalUserGroup;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmNtpServerAndInterval;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.HmUserSettings;
import com.ah.bo.admin.HmUserSsidProfile;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.MailNotification4VHM;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.admin.RemoteProcessCallSettings;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.DomainMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.config.DomainObjectAction;
import com.ah.util.EnumConstUtil;

/**
 * Admin feature restore process
 *@filename		RestoreAdmin.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-22 04:01:46
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history* 2009-01-19 put the current license information in database
 */
public class RestoreAdmin {

	private static final boolean isOnline = NmsUtil.isHostedHMApplication();
	private static final boolean isPlanner = NmsUtil.isPlanner();

	// ---------------------- hm_domain -----------------START---------
	public static void restoreDomain() {
		try {
			List<HmDomain> allDomains = getAllDomains();
			if (null == allDomains) {
				return;
			}

			List<Long> lOldId = new ArrayList<Long>();

			for (HmDomain hmDomain : allDomains) {
				lOldId.add(hmDomain.getId());
			}

			QueryUtil.restoreBulkCreateBos(allDomains);

			for (int i = 0; i < allDomains.size(); ++i) {
				AhRestoreNewMapTools.setHmDomain(lOldId.get(i), allDomains.get(i));
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreDomain() catch exception ", e);
		}
	}

	// init domain map in domain restore
	public static void initDomainmap(HmDomain oDomain, String oldName) {
		if (null == oDomain) {
			return;
		}

		try {
			List<HmDomain> allDomains = getAllDomains();
			Long lOldId = 0l;

			if(oldName.equalsIgnoreCase("home"))
			{
				for (Map.Entry<Long, HmDomain> entry : AhRestoreNewMapTools.getHmDomainMap().entrySet()) {
					HmDomain tmpDomain = entry.getValue();
					if (tmpDomain.getDomainName().equalsIgnoreCase("home")) {
						lOldId = entry.getKey();
						break;
					}
				}
			}
			else
			{
				if (null == allDomains || allDomains.isEmpty()) {
					BeLogTools.restoreLog(BeLogTools.ERROR,
							"could not read domain information from xml file");

					return;
				}
				lOldId = allDomains.get(0).getId();
			}

			AhRestoreNewMapTools.setHmDomain(lOldId, oDomain);
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.initDomainmap() catch exception ", ex);
		}
	}

	public static void changeDomainOrderKeyInfo(String domName, String vhmId, int apNumber, int validDay) {
		DomainOrderKeyInfo currentDom = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class,
				"domainName", domName);
		boolean domExist = null != currentDom;
		if (!domExist) {
			currentDom = new DomainOrderKeyInfo();

			currentDom.setDomainName(domName);
			currentDom.setCreateTime(System.currentTimeMillis());
			currentDom.setHoursUsed(currentDom.getEncriptString(0));
		}

		// set system id
		currentDom.setSystemId(vhmId);
		currentDom.setOrderKey(DomainOrderKeyInfo.DEFAULT_ORDER_KEY);
		currentDom.setEncryptLicense(BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM, apNumber, 0, validDay, 0);

		if (domExist) {
			try {
				QueryUtil.updateBo(currentDom);
			} catch (Exception e) {
				BeLogTools.restoreLog(BeLogTools.ERROR,
						"changeDomainOrderKeyInfo() : update order key domain record!", e);
			}
		} else {
			try {
				QueryUtil.createBo(currentDom);
			} catch (Exception e) {
				BeLogTools.restoreLog(BeLogTools.ERROR,
						"changeDomainOrderKeyInfo() : create order key domain record!", e);
			}
		}
	}

	/**
	 * just update target domain values
	 *
	 * @param oDomain -
	 */
	public static void restoreDomainExt(HmDomain oDomain) {
		try {
			if (oDomain.isHomeDomain()) {
				// not for home domain
				return;
			}

			List<HmDomain> allDomains = getAllDomains();
			if (null == allDomains || allDomains.isEmpty()) {
				return;
			}

			HmDomain srcDomain = allDomains.get(0);
			oDomain.setTimeZone(srcDomain.getTimeZoneString());
			oDomain.setSupportFullMode(srcDomain.isSupportFullMode());
			oDomain.setSupportGM(srcDomain.isSupportGM());
			oDomain.setAccessMode(srcDomain.getAccessMode());
			oDomain.setAuthorizationEndDate(srcDomain.getAuthorizationEndDate());
			oDomain.setAccessChanged(srcDomain.isAccessChanged());
			oDomain.setAuthorizedTime(srcDomain.getAuthorizedTime());

			BoMgmt.getDomainMgmt().updateDomain(oDomain);
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreDomainExt() catch exception ", e);
		}
	}

	/**
	 * restore default order okey
	 *
	 * @param oDomain -
	 */
	public static void initDefaultOrderKey(HmDomain oDomain) {
		if (oDomain.isHomeDomain()) {
			// not for home domain
			return;
		}

		if (NmsUtil.isHostedHMApplication() && AhRestoreDBTools.isNeedDefaultOrderkey() && oDomain.getMaxApInDb() > 0) {
			changeDomainOrderKeyInfo(oDomain.getDomainName(), oDomain.getVhmID(), oDomain.getMaxApInDb(), 30);
		}
	}

	/**
	 * Get all information from hm_domain table
	 *
	 * @return List<HmDomain> all HmDomain BO
	 * @throws Exception
	 *             -
	 */
	private static List<HmDomain> getAllDomains() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_user_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_domain");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_domain table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		List<HmDomain> domains = new ArrayList<HmDomain>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			HmDomain domain = new HmDomain();

			/**
			 * Set domain id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";

			long lId;

			try {
				lId = Long.parseLong(id);
			} catch (Exception ex) {
				BeLogTools.restoreLog(BeLogTools.ERROR, ex);
				lId = System.currentTimeMillis();
			}

			domain.setId(lId);
			/**
			 * Set timeZone
			 */
			colName = "timeZone";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String timeZone = isColPresent ? xmlParser.getColVal(i, colName) : TimeZone
					.getDefault().getID();
			domain.setTimeZone(timeZone);

			/**
			 * Set domain name
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (name.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE,"Restore table 'hm_domain' data be lost, cause: 'domainname' column is not exist.");
				continue;
			}

			// map id - name
			// AhRestoreMapTool.setMapDomain(id, name);

			if (name.equals(HmDomain.GLOBAL_DOMAIN) || name.equalsIgnoreCase(HmDomain.HOME_DOMAIN)) {
				// we need update.
				HmDomain domain_ = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", name);
				if (domain_ != null) {
					domain_.setTimeZone(timeZone);
					QueryUtil.updateBo(domain_);

					//add to the map
					if(!AhRestoreNewMapTools.getHmDomainMap().containsKey(lId))
					{
						AhRestoreNewMapTools.setHmDomain(lId, domain_);
					}
				}

				continue;
			}
			domain.setDomainName(name);

			/**
			 * Set domain maxApNum
			 */
			colName = "maxApNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			int maxApNum = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			domain.setMaxApNum(maxApNum);

			/**
			 * Set domain maxSimuAp
			 */
			colName = "maxSimuAp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			int maxSimuAp = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : HmDomain.MAX_SIMULATE_HIVEAP_DEFAULT;
			domain.setMaxSimuAp(maxSimuAp);

			/**
			 * Set domain maxSimuClient
			 */
			colName = "maxSimuClient";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			int maxSimuClient = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : HmDomain.MAX_SIMULATE_CLIENT_PERAP_DEFAULT;
			domain.setMaxSimuClient(maxSimuClient);

			/**
			 * Set domain runStatus
			 */
			colName = "runStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			int runStatus = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : HmDomain.DOMAIN_DEFAULT_STATUS;
			domain.setRunStatus(runStatus);

			/**
			 * Set supportGM
			 */
			colName = "supportGM";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			boolean supportGM = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser
					.getColVal(i, colName));
			domain.setSupportGM(supportGM);

			/**
			 * Set comment
			 */
			colName = "comment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String comment = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			domain.setComment(comment);

			/**
			 * Set supportFullMode
			 */
			colName = "supportFullMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			boolean supportFullMode = !isColPresent || AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName));
			domain.setSupportFullMode(supportFullMode);

			/**
			 * Set vhmID
			 */
			colName = "vhmID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String vhmID = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			domain.setVhmID(vhmID);

			/**
			 * owner user
			 */
			colName = "user_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			if (isColPresent) {
				Long user_id = AhRestoreCommons.convertLong(xmlParser.getColVal(i,colName));
				if (null != user_id) {
					// cannot query user because restore domain at first, we need refresh the user id
					// value later
					AhRestoreNewMapTools.setDomainNameVADID(name, user_id);
				}
			}

			/**
			 * Set partner id
			 */
			colName = "partnerId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			String partnerId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			domain.setPartnerId(partnerId);

			/**
			 * accessMode
			 */
			if (!NmsUtil.isHostedHMApplication()) {
				// for HMOP, access mode should always 4 (config and monitor)  fix bug CFD-125 (Jira)
				domain.setAccessMode(HmDomain.ACCESS_MODE_TECH_OP_PARTNER_RW);
			} else {
				// for HMOL, restore access mode as value in backup file
				colName = "accessMode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
				int accessMode = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
						colName)) : HmDomain.ACCESS_MODE_TECH_OP_PARTNER_RW;
				domain.setAccessMode((short)accessMode);
			}
			/**
			 * authorizationEndDate
			 */
			colName = "authorizationEndDate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			Long authorizationEndDate = isColPresent ? AhRestoreCommons.convertString2Long(xmlParser.getColVal(i,
					colName)) : -1;
			domain.setAuthorizationEndDate(authorizationEndDate);

			/**
			 * accessChanged
			 */
			colName = "accessChanged";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			boolean accessChanged = isColPresent ? AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName)) : true;
			domain.setAccessChanged(accessChanged);

			/**
			 * authorizedTime
			 */
			colName = "authorizedTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_domain", colName);
			int authorizedTime = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : -1;
			domain.setAuthorizedTime(authorizedTime);

			domains.add(domain);
		}

		return !domains.isEmpty() ? domains : null;
	}

	// ---------------------- hm_domain -----------------END-----------

	// ---------------------- hm_updatesoftwareinfo -----------------START---------
	// only home do it
	public static void restoreUpdateSoftwareInfo() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return;
		// }

		try {
			Collection<HMUpdateSoftwareInfo> infoList = getUpdateSoftwareInfo();
			if (null == infoList) {
				return;
			}

			QueryUtil.restoreBulkCreateBos(infoList);
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreUpdateSoftwareInfo() catch exception ", e);
		}
	}

	/**
	 * Get all information from hm_updatesoftwareinfo table
	 *
	 * @return -
	 * @throws Exception -
	 */
	private static Collection<HMUpdateSoftwareInfo> getUpdateSoftwareInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_updatesoftwareinfo.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_updatesoftwareinfo");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_updatesoftwareinfo table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		List<HMUpdateSoftwareInfo> infoList = new ArrayList<HMUpdateSoftwareInfo>();

		for (int i = 0; i < rowCount; i++) {
			HMUpdateSoftwareInfo info = new HMUpdateSoftwareInfo();

			/**
			 * Set domain name
			 */
			String colName = "domainname";
			String name = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			info.setDomainName(name);

			/**
			 * Set ipAddress
			 */
			colName = "ipAddress";
			String ipAddress = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			info.setIpAddress(ipAddress);

			/**
			 * Set hmVersion
			 */
			colName = "hmVersion";
			String hmVersion = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			info.setHmVersion(hmVersion);

			/**
			 * Set status
			 */
			colName = "status";
			int status = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
			info.setStatus(status);

			/**
			 * Set apSwithStatus
			 */
			colName = "apSwithStatus";
			boolean apSwithStatus = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
					colName));
			info.setApSwithStatus(apSwithStatus);

			infoList.add(info);
		}

		return !infoList.isEmpty() ? infoList : null;
	}

	// ---------------------- hm_updatesoftwareinfo -----------------END-----------

	// ---------------------- hm_user_group -------------START---------

	private static Map<String, HmPermission>	monitorPermissions		= null;

	private static Map<String, HmPermission>	configPermissions		= null;

	private static Map<String, HmPermission>	plannerPermissions		= null;

	private static Map<String, HmPermission>	gmAdminPermissions		= null;

	private static Map<String, HmPermission>	gmOperatorPermissions	= null;

	private static Map<String, HmPermission>	tcOperatorPermissions	= null;

	public static boolean restoreUserGroup() {
		try {
			// prepare feature/instance permissions
			prepareFeaturePermissionsMap();
			prepareInstancePermissionsMap();

			// // update the default groups instance permission
			// updateDefaultUserGroupPermission();

			DomainMgmt domainMgmt = BoMgmt.getDomainMgmt();
			domainMgmt.createFeatureKeys();
			monitorPermissions = domainMgmt.getPermissionReadOnly();
			configPermissions = domainMgmt.getPermissionWrite();
			plannerPermissions = domainMgmt.getPermissionPlanning();
			gmAdminPermissions = domainMgmt.getGMPermission(HmUserGroup.GM_ADMIN);
			gmOperatorPermissions = domainMgmt.getGMPermission(HmUserGroup.GM_OPERATOR);
			tcOperatorPermissions = domainMgmt.getTeacherPermission();

			List<HmUserGroup> groupList = getAllUserGroups();
			if (null != groupList && !groupList.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (HmUserGroup userGroup : groupList) {
					lOldId.add(userGroup.getId());
				}

				// planningGroupOperation(groupList);
				QueryUtil.restoreBulkCreateBos(groupList);

				for (int i = 0; i < groupList.size(); i++) {
					AhRestoreNewMapTools.setMapUserGroup(lOldId.get(i), groupList.get(i).getId());
				}
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreUserGroup() catch exception ", e);
			return false;
		}
		return true;
	}

	/**
	 * Get all information from hm_user_group table
	 *
	 * @return List<HmUserGroup> all HmUserGroup BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_user_group.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_user_group.xml.
	 */
	private static List<HmUserGroup> getAllUserGroups() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_user_group.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_user_group");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in local_user table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HmUserGroup> userGroups = new ArrayList<HmUserGroup>();

		int attribute = 9;
		boolean isAlreadySupportVhmRadius = false;
		boolean isLowerThan40r1 = RestoreUsersAndAccess.isDataFromOldVersionForTimeZone((float)4.0);

		for (int i = 0; i < rowCount; i++) {
			HmUserGroup userGroup = new HmUserGroup();

			/**
			 * Set helpurl
			 */
			String colName = "helpurl";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group",
					colName);
			String helpurl = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			userGroup.setHelpURL(helpurl);

			/**
			 * Set group id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			userGroup.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set group name
			 */
			colName = "groupname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (name.equals("")) {
				continue;
			}
			userGroup.setGroupName(name);

			/**
			 * Set defaultFlag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group", colName);
			boolean defaultflag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			userGroup.setDefaultFlag(defaultflag);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 2;
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if (null == ownerDomain)
				continue;
			userGroup.setOwner(ownerDomain);

			// special user group
			if (defaultflag) {
				if (name.equalsIgnoreCase("vad")) {
					name = HmUserGroup.VAD;
				}

				// we need update.
				HmUserGroup userGroupBo = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", name,
						ownerDomain.getId(),new AdminQueryBo());
				if (userGroupBo != null) {
					userGroupBo.setHelpURL(helpurl);
					updateDefaultUserGroupInstancePermission(userGroupBo);
					QueryUtil.updateBo(userGroupBo);

					// map id - name
					AhRestoreNewMapTools.setMapUserGroup(Long.valueOf(id), userGroupBo.getId());

					continue;
				}

				// if HM->VHM , we need discard super user group
				if (!ownerDomain.isHomeDomain() && userGroup.isAdministrator()) {
					continue;
				}
			}

			if (NmsUtil.isHostedHMApplication()) {//HMOL
				/**
				 * set groupattribute and feature permissions
				 */
				if (userGroup.getGroupName().equals(HmUserGroup.ADMINISTRATOR)) {
					userGroup.setGroupAttribute(HmUserGroup.ADMINISTRATOR_ATTRIBUTE);
				} else if (userGroup.getGroupName().equals(HmUserGroup.MONITOR)) {
					userGroup.setGroupAttribute(HmUserGroup.MONITOR_ATTRIBUTE);
					userGroup.setFeaturePermissions(monitorPermissions);
				} else if (userGroup.getGroupName().equals(HmUserGroup.CONFIG)) {
					userGroup.setGroupAttribute(HmUserGroup.CONFIG_ATTRIBUTE);
					userGroup.setFeaturePermissions(configPermissions);
				} else if (userGroup.getGroupName().equals(HmUserGroup.PLANNING)) {
					userGroup.setGroupAttribute(HmUserGroup.PLANNING_ATTRIBUTE);
					userGroup.setFeaturePermissions(plannerPermissions);
				} else if (userGroup.getGroupName().equals(HmUserGroup.GM_ADMIN)) {
					userGroup.setGroupAttribute(HmUserGroup.GM_ADMIN_ATTRIBUTE);
					userGroup.setFeaturePermissions(gmAdminPermissions);
				} else if (userGroup.getGroupName().equals(HmUserGroup.GM_OPERATOR)) {
					userGroup.setGroupAttribute(HmUserGroup.GM_OPERATOR_ATTRIBUTE);
					userGroup.setFeaturePermissions(gmOperatorPermissions);
				} else if (userGroup.getGroupName().equals(HmUserGroup.TEACHER)) {
					userGroup.setGroupAttribute(HmUserGroup.TEACHER_ATTRIBUTE);
					userGroup.setFeaturePermissions(tcOperatorPermissions);
				} else {
					int groupAttribute = attribute++;
					userGroup.setGroupAttribute(groupAttribute);
					setReportLostFeaturePermission(Long.valueOf(id));
					userGroup.setFeaturePermissions(featurePermissionsMap.get(Long.valueOf(id)));
				}
			} else {//stand alone
				/**
				 * Set group attribute
				 */
				colName = "groupattribute";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user_group", colName);
				int groupattribute = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
						colName)) : -1;
				userGroup.setGroupAttribute(groupattribute);

				// set feature permissions
				setFeaturePermission(userGroup);

				/**
				 * check if it is already Support VHM Radius
				 */
				if (!isLowerThan40r1 && !isAlreadySupportVhmRadius) {
					isAlreadySupportVhmRadius = true;
				}
			}

			/**
			 * set instancePermissions
			 */
			userGroup.setInstancePermissions(instancePermissionsMap.get(Long.valueOf(id)));

			userGroups.add(userGroup);
		}

		/**
		 * set unique group attribute for each user group
		 */
		if (!NmsUtil.isHostedHMApplication()) {
			setUniqueGroupAttribute(userGroups, isAlreadySupportVhmRadius);
		}

		return !userGroups.isEmpty() ? userGroups : null;
	}

	/**
	 * groupID - instance permissions
	 */
	private static Map<Long, Map<Long, HmPermission>>	instancePermissionsMap	= new HashMap<Long, Map<Long, HmPermission>>();

	/**
	 * groupID - feature permissions
	 */
	private static Map<Long, Map<String, HmPermission>>	featurePermissionsMap	= new HashMap<Long, Map<String, HmPermission>>();

	private static void prepareInstancePermissionsMap() throws Exception {
		instancePermissionsMap = new HashMap<Long, Map<Long, HmPermission>>();

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile("hm_instance_permission");
		if (!restoreRet) {
			return;
		}

		int rowCount = xmlParser.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			HmPermission permission = new HmPermission();

			/**
			 * get mapKey
			 */
			String colName = "mapkey";
			Long mapKey = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
			Long newKey = AhRestoreNewMapTools.getMapMapContainer(mapKey);
			if (newKey == null) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_instance_permission' data be lost, cause: 'mapkey' column is not exist.");
				continue;
			}

			/**
			 * Set label
			 */
			colName = "label";
			String label = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			permission.setLabel(label);

			/**
			 * Set operations
			 */
			colName = "operations";
			short operations = (short) (AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)));
			permission.setOperations(operations);

			/**
			 * get group id
			 */
			colName = "hm_user_group_id";
			Long groupID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			Map<Long, HmPermission> permissions = instancePermissionsMap.get(groupID);
			if (permissions == null) {
				permissions = new HashMap<Long, HmPermission>();
				permissions.put(newKey, permission);
				instancePermissionsMap.put(groupID, permissions);
			} else {
				permissions.put(newKey, permission);
			}
		}
	}

	private static void prepareFeaturePermissionsMap() throws Exception {
		featurePermissionsMap = new HashMap<Long, Map<String, HmPermission>>();

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile("hm_feature_permission");
		if (!restoreRet) {
			return;
		}

		int rowCount = xmlParser.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			/**
			 * get group id
			 */
			String colName = "hm_user_group_id";
			Long groupID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			HmPermission permission = new HmPermission();

			/**
			 * Set label
			 */
			colName = "label";
			String label = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			permission.setLabel(label);

			/**
			 * Set operations
			 */
			colName = "operations";
			short operations = (short) (AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)));
			permission.setOperations(operations);

			/**
			 * get mapKey
			 */
			colName = "mapkey";
			String mapKey = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			if (mapKey.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_feature_permission' data be lost, cause: 'mapkey' column is not exist.");
				continue;

				// has read and write permission for user password modify feature always
			} else if (Navigation.L2_FEATURE_USER_PASSWORD_MODIFY.equals(mapKey)) {
				permission.setOperations(HmPermission.OPERATION_READ);
				permission.addOperation(HmPermission.OPERATION_WRITE);

				// only has read permission for date time and license feature
			} else if (Navigation.L2_FEATURE_LICENSEMGR.equals(mapKey)) {
				permission.setOperations(HmPermission.OPERATION_READ);
			}

			Map<String, HmPermission> permissions = featurePermissionsMap.get(groupID);
			if (permissions == null) {
				permissions = new HashMap<String, HmPermission>();
				permissions.put(mapKey, permission);
				featurePermissionsMap.put(groupID, permissions);
			} else {
				permissions.put(mapKey, permission);
			}
		}
	}

	/**
	 * domain - map node list
	 */
	private static final Map<HmDomain, List<?>>	mapContainerNodeMap	= new HashMap<HmDomain, List<?>>();

	private static void updateDefaultUserGroupInstancePermission(HmUserGroup userGroup) {
		HmPermission mapPermission = new HmPermission();
		if (userGroup.isAdministrator()
				|| userGroup.getGroupName().equalsIgnoreCase(HmUserGroup.CONFIG)
				|| userGroup.getGroupName().equalsIgnoreCase(HmUserGroup.PLANNING)) {
			mapPermission.addOperation(HmPermission.OPERATION_READ);
			mapPermission.addOperation(HmPermission.OPERATION_WRITE);
		} else if (userGroup.getGroupName().equalsIgnoreCase(HmUserGroup.MONITOR)) {
			mapPermission.addOperation(HmPermission.OPERATION_READ);
		} else {
			return;
		}

		List<?> mapNodeList = mapContainerNodeMap.get(userGroup.getOwner());
		if (mapNodeList == null) {
			mapNodeList = QueryUtil.executeQuery("select id from "
					+ MapContainerNode.class.getSimpleName(), null, null, userGroup.getOwner()
					.getId());
			mapContainerNodeMap.put(userGroup.getOwner(), mapNodeList);
		}

		for (Object object : mapNodeList) {
			Long id = (Long) object;
			if (id.equals(BoMgmt.getMapMgmt().getRootMapId())) {
				continue;
			}
			userGroup.getInstancePermissions().put(id, mapPermission);
		}

		// try {
		// // update Monitor group instance permission;
		// HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class,
		// "groupName", HmUserGroup.MONITOR, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
		// if (null != group) {
		// HmPermission mapPermission = new HmPermission();
		// mapPermission.addOperation(HmPermission.OPERATION_READ);
		// for (Object object : boIds) {
		// Long id = (Long) object;
		// if (id.equals(MapMgmtImpl.getInstance().getRootMapId())) {
		// continue;
		// }
		// group.getInstancePermissions().put(id, mapPermission);
		// }
		// QueryUtil.updateBo(group);
		// }
		// } catch (Exception e) {
		// BeLogTools.restoreLog(BeLogTools.ERROR, "update default monitor group error", e);
		// }
		// try {
		// // update Configuration & Monitor group instance permission;
		// HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class,
		// "groupName", HmUserGroup.CONFIG, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
		// if (null != group) {
		// HmPermission mapPermission = new HmPermission();
		// mapPermission.addOperation(HmPermission.OPERATION_READ);
		// mapPermission.addOperation(HmPermission.OPERATION_WRITE);
		// for (Object object : boIds) {
		// Long id = (Long) object;
		// if (id.equals(MapMgmtImpl.getInstance().getRootMapId())) {
		// continue;
		// }
		// group.getInstancePermissions().put(id, mapPermission);
		// }
		// QueryUtil.updateBo(group);
		// }
		// } catch (Exception e) {
		// BeLogTools.restoreLog(BeLogTools.ERROR, "update default config group error", e);
		// }
		//
		// // update super user instance permission
		// try {
		// HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class,
		// "groupName", HmUserGroup.ADMINISTRATOR, AhRestoreDBTools.HM_RESTORE_DOMAIN
		// .getId());
		// if (null != group) {
		// HmPermission mapPermission = new HmPermission();
		// mapPermission.addOperation(HmPermission.OPERATION_READ);
		// mapPermission.addOperation(HmPermission.OPERATION_WRITE);
		// for (Object object : boIds) {
		// Long id = (Long) object;
		// if (id.equals(MapMgmtImpl.getInstance().getRootMapId())) {
		// continue;
		// }
		// group.getInstancePermissions().put(id, mapPermission);
		// }
		// QueryUtil.updateBo(group);
		// }
		// } catch (Exception e) {
		// BeLogTools.restoreLog(BeLogTools.ERROR, "update default super user group error", e);
		// }
	}

	// /**
	// * from 3.4version, vhm default user group add planning group, so we add this special
	// operation
	// * for vhm user group restore
	// *
	// * @param
	// *
	// * @return
	// */
	// private static void planningGroupOperation(List<HmUserGroup> groupList) {
	// if (AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
	// // only for vHM
	// return;
	// }
	//
	// HmUserGroup planGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName",
	// HmUserGroup.PLANNING, AhRestoreDBTools.HM_RESTORE_DOMAIN.getId());
	// if (planGroup != null) {
	// return;
	// }
	//
	// boolean containPlanGroup = false;
	// Map<Long, HmPermission> mapPermissions = null;
	//
	// for (HmUserGroup group : groupList) {
	// if (group.getGroupName().equals(HmUserGroup.PLANNING)) {
	// containPlanGroup = true;
	// break;
	// }
	//
	// if (group.getGroupName().equals(HmUserGroup.CONFIG)) {
	// mapPermissions = group.getInstancePermissions();
	// }
	// }
	//
	// if (!containPlanGroup) {
	// // add planning group from 3.5
	// HmUserGroup group = new HmUserGroup();
	// group.setGroupName(HmUserGroup.PLANNING);
	// group.setDefaultFlag(true);
	// group.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);
	// group.setGroupAttribute(HmUserGroup.PLANNING_ATTRIBUTE);
	//
	// Map<String, HmPermission> map = new HashMap<String, HmPermission>();
	// HmPermission permission = new HmPermission();
	// permission.setOperations(HmPermission.OPERATION_READ);
	// permission.addOperation(HmPermission.OPERATION_WRITE);
	// map.put(Navigation.L2_FEATURE_MAP_VIEW, permission);
	// group.setFeaturePermissions(map);
	//
	// group.setInstancePermissions(mapPermissions);
	//
	// groupList.add(group);
	// }
	// }

	// ---------------------- hm_user_group -------------END---------

	// ---------------------- hm_user -------------START---------

	public static void restoreUser() {
		try {
			prepareUserSSIDProfileMap();
			prepareUserLocalUserGroupMap();
			
			// clear user email
			AhRestoreNewMapTools.clearVhmEmails();
			
			List<HmUser> users = getAllUsers();
			if (null == users) {
				return;
			}

			if (!users.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (HmUser user : users) {
					lOldId.add(user.getId());
				}

				QueryUtil.restoreBulkCreateBos(users);

				for (int i = 0; i < users.size(); i++) {
					AhRestoreNewMapTools.setMapUser(lOldId.get(i), users.get(i).getId());
					
					// only needed for single VHM move/upgrade on HMOL
					if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
						// save all user emails for filter user customization settings, fix bug 32249
						AhRestoreNewMapTools.addVhmEmails(users.get(i).getEmailAddress());
					}
				}
			}

			// for owner user field of HmDomain
			refreshOwnerUser4Domain();
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR, "RestoreAdmin.restoreUser() catch exception ",
					e);
		}
	}

	private static void refreshOwnerUser4Domain() throws Exception {
		List<HmDomain> updateList = new ArrayList<HmDomain>();
		List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, new SortParams("id"), new FilterParams("domainName != :s1 AND domainName != :s2",
				new Object[]{HmDomain.HOME_DOMAIN, HmDomain.GLOBAL_DOMAIN}));
		for (HmDomain domain : list) {
			Long ownerUserID = AhRestoreNewMapTools.getOwnerUserIDFromDomainName(domain.getDomainName());
			if (ownerUserID != null) {
				Long userId = AhRestoreNewMapTools.getMapUser(ownerUserID);
				if (null != userId) {
					List<?> userMails = QueryUtil.executeQuery("select emailAddress from "+HmUser.class.getSimpleName(), null, new FilterParams("id", userId));
					if (null != userMails && !userMails.isEmpty()) {
						domain.setPartnerId((String)userMails.get(0));
						updateList.add(domain);
					}
				}
			}
		}
		if (!updateList.isEmpty())
			QueryUtil.bulkUpdateBos(updateList);
	}

	private static HmUserGroup vadUserGroup = null;

	/**
	 * Get all information from hm_user table
	 *
	 * @return List<HmUser> all HmUser BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_user.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_user.xml.
	 */
	private static List<HmUser> getAllUsers() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_user.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_user");
		if (!restoreRet) {
			return null;
		}
		// cchen DONE
		boolean hus_restoreRet = AhRestoreGetXML.checkXMLFileExist("hm_user_settings");

		/**
		 * No one row data stored in hm_user table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HmUser> users = new ArrayList<HmUser>();
		List<HmUpgradeLog> logList = new ArrayList<HmUpgradeLog>();

		boolean isColPresent;
		String colName;
		HmUser user;
		HmTimeStamp logTimeStamp = null;
		for (int i = 0; i < rowCount; i++) {
			user = new HmUser();

			/**
			 * Set user id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			Long id = isColPresent ? Long.valueOf(xmlParser.getColVal(i, colName)) : 1;
			user.setId(id);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_user' data be lost, cause: cannot get domain by old owner id("+ownerId+"), user old id is "+id);
				continue;
			}
			user.setOwner(ownerDomain);

			/**
			 * Set password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String password = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (!NmsUtil.isHostedHMApplication()) {
				// fix bug 23704, user with Customer ID(has IDM) has no password on HMOL, so only stand alone HM need do this check
				if (password.equals("")) {
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_user' data be lost, cause: 'password' is null, user old id is "+id);
					continue;
				}
			}
			user.setPassword(password);

			/**
			 * Set userFullName
			 */
			colName = "userFullName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String userFullName = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			user.setUserFullName(userFullName);

			/**
			 * Set emailAddress
			 */
			colName = "emailAddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String emailAddress = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			user.setEmailAddress(emailAddress);

			/**
			 * Set timeZone
			 */
			colName = "timeZone";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String timeZone = isColPresent ? xmlParser.getColVal(i, colName) : ownerDomain
					.getTimeZoneString();
			user.setTimeZone(timeZone);

			/**
			 * Set promptChanges
			 */
			colName = "promptChanges";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String promptChanges = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "false";
			user.setPromptChanges(AhRestoreCommons.convertStringToBoolean(promptChanges));

			/**
			 * Set dontShowMessageInDashboard
			 */
			colName = "dontShowMessageInDashboard";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String dontShowMessageInDashboard = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "false";
			user.setDontShowMessageInDashboard(AhRestoreCommons.convertStringToBoolean(dontShowMessageInDashboard));

			/**
			 * Set userName
			 */
			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String userName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (userName.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_user' data be lost, cause: 'userName' is null, user old id is "+id);
				continue;
			}

			/**
			 * Set navCustomization ,added via yyy for us1375
			 */
			colName = "navCustomization";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String navCustomization = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			user.setNavCustomization(AhRestoreCommons.convertInt(navCustomization,16));

			/**
			 * Set defaultFlag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			boolean defaultflag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			if (defaultflag || (userName.equals(HmUser.ADMIN_USER) && ownerDomain.isHomeDomain())) {
				// we need update.
				HmUser userBo = QueryUtil.findBoByAttribute(HmUser.class, "userName", userName,
						ownerDomain.getId());
				if (userBo != null) {
					userBo.setPassword(user.getPassword());
					userBo.setEmailAddress(user.getEmailAddress());
					userBo.setUserFullName(user.getUserFullName());
					userBo.setTimeZone(user.getTimeZone());
					userBo.setAccessMyhive(true);
					//add for bug 20914
					userBo.setNavCustomization(user.getNavCustomizationForRestore());

					QueryUtil.updateBo(userBo);

					AhRestoreNewMapTools.setMapUser(id, userBo.getId());

					continue;
				}
			}

			/**
			 * Set group_id
			 */
			colName = "group_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			String group_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (group_id.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_user' data be lost, cause: 'group_id' is null, user old id is "+id);
				continue;
			}
			Long newGroupID = AhRestoreNewMapTools.getMapUserGroup(Long.valueOf(group_id));
			if (newGroupID == null) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_user' data be lost, cause: cannot get user group by old group id("+group_id+"), user old id is "+id);
				continue;
			}

			//reset newgroupid if dst is online and is not planner and user is default
			if(isOnline && !isPlanner && defaultflag){
				HmUserGroup oldGroupObj = QueryUtil.findBoById(HmUserGroup.class, newGroupID);
				//if src group is planner
				if(HmUserGroup.PLANNING.equalsIgnoreCase(oldGroupObj.getGroupName())){
					List<HmUserGroup> list = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams("groupName",HmUserGroup.CONFIG), ownerDomain.getId());
					if(null != list && list.size() > 0){
						newGroupID = list.get(0).getId();
					}
				}
			}

			HmUserGroup group = AhRestoreNewTools.CreateBoWithId(HmUserGroup.class, newGroupID);
			user.setUserGroup(group);

			// check user name unique in domain
			HmUser bo = QueryUtil.findBoByAttribute(HmUser.class, "userName", userName, ownerDomain
					.getId());
			if (bo != null) {
				HmUpgradeLog log = new HmUpgradeLog();
				log.setOwner(ownerDomain);
				log.setFormerContent("The user name for admin \"" + userName
						+ "\" is already exists.");
				log.setPostContent("Admin name must be unique, so "+NmsUtil.getOEMCustomer().getNmsName()+" preserved its first use and discarded any other occurrences.");
				log.setRecommendAction("Manually create a new user account with a unique user name for admin\"" + userName + "\".");

				if (logTimeStamp == null) {
					logTimeStamp = new HmTimeStamp(System.currentTimeMillis(), ownerDomain
							.getTimeZoneString());
				}
				log.setLogTime(logTimeStamp);

				logList.add(log);

				if (bo.getDefaultFlag()) {
					bo.setAccessMyhive(true);
					QueryUtil.updateBo(bo);
				}

				continue;
			}

			// check default user of vhm, must keep only one default user in vhm
			if (defaultflag && !ownerDomain.isHomeDomain()) {
				HmUser _defaultuser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
						ownerDomain.getId());
				if (_defaultuser != null) {
					_defaultuser.setAccessMyhive(true);
					QueryUtil.updateBo(_defaultuser);

					defaultflag = false;
				}
			}

			user.setDefaultFlag(defaultflag);
			user.setUserName(userName);

			// add into upgrade log
			if (emailAddress.length() == 0) {
				//
				emailAddress = userName + "@" + ownerDomain.getDomainName();
				user.setEmailAddress(emailAddress);

				//
				HmUpgradeLog log = new HmUpgradeLog();
				log.setOwner(ownerDomain);
				log.setFormerContent("The email address for admin \"" + userName
						+ "\" was not set.");
				log
						.setPostContent("The admin email address is a required setting, so system set a temporary email ("+emailAddress+") for this admin.");
				log.setRecommendAction("Check email address for admin \"" + userName + "\".");

				if (logTimeStamp == null) {
					logTimeStamp = new HmTimeStamp(System.currentTimeMillis(), ownerDomain
							.getTimeZoneString());
				}
				log.setLogTime(logTimeStamp);

				logList.add(log);
			} else {
				HmUser oldUser = QueryUtil.findBoByAttribute(HmUser.class, "emailAddress",
						emailAddress);
				if (oldUser != null) {
					HmUpgradeLog log = new HmUpgradeLog();
					log.setOwner(ownerDomain);
					log.setFormerContent("The email address for admin \"" + userName
							+ "\" is already used by admin \"" + oldUser.getUserName() + "\".");
					log
							.setPostContent("Admin email addresses must be unique globally, so system preserved its first use and discarded any other occurrences.");
					log.setRecommendAction("Set a globally unique email address for admin\""
							+ userName + "\".");

					if (logTimeStamp == null) {
						logTimeStamp = new HmTimeStamp(System.currentTimeMillis(), ownerDomain
								.getTimeZoneString());
					}
					log.setLogTime(logTimeStamp);

					logList.add(log);

					continue;
				}
			}

			/**
			 * Set vadAdmin
			 */
			colName = "vadAdmin";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			boolean vadAdmin = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			if (vadAdmin) {
				if (vadUserGroup == null) {
					vadUserGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupAttribute", HmUserGroup.VAD_ATTRIBUTE);
				}
				if (vadUserGroup != null) {
					user.setUserGroup(vadUserGroup);
				}
			}

			/**
			 * Set maxAPNum
			 */
			colName = "maxApNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			int maxApNum = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			user.setMaxAPNum(maxApNum);

			/**
			 * Set accessmyhive
			 */
			colName = "accessmyhive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_user", colName);
			boolean accessmyhive = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			if (defaultflag) {
				user.setAccessMyhive(true);
			} else {
				user.setAccessMyhive(accessmyhive);
			}

			/**
			 * localUserGroups & ssidprofiles
			 */
			user.setLocalUserGroups(getLocalUserGroupSet(id));
			user.setSsidProfiles(getSSIDProfileSet(id));

			// User management enhancement
			if (!hus_restoreRet) {
				List<HmLocalUserGroup> user_localusergroup = new ArrayList<HmLocalUserGroup>();
				for (LocalUserGroup userGroup : user.getLocalUserGroupsForRestore()) {
					HmLocalUserGroup user_lug = new HmLocalUserGroup();
					user_lug.setLocalusergroup_id(userGroup.getId());
					user_lug.setUseremail(user.getEmailAddress());
					user_localusergroup.add(user_lug);
				}

				try {
					if (user_localusergroup.size() > 0) {
						QueryUtil.restoreBulkCreateBos(user_localusergroup);
					}
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("Restore user_localusergroup error.", e);
				}
			}

			if (!hus_restoreRet) {
				List<HmUserSsidProfile> user_ssidprofile = new ArrayList<HmUserSsidProfile>();
				for (SsidProfile ssidprofile : user.getSsidProfilesForRestore()) {
					HmUserSsidProfile u_ssidprofile = new HmUserSsidProfile();
					u_ssidprofile.setSsidprofile_id(ssidprofile.getId());
					u_ssidprofile.setUseremail(user.getEmailAddress());
					user_ssidprofile.add(u_ssidprofile);
				}

				try {
					if (user_ssidprofile.size() > 0) {
						QueryUtil.restoreBulkCreateBos(user_ssidprofile);
					}
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("Restore user_ssidprofile error.", e);
				}
			}

			if (!hus_restoreRet) {
				// add newly added user setting table
				HmUserSettings u_settings = new HmUserSettings();
				u_settings.setUseremail(user.getEmailAddress());
				u_settings.setDontShowMessageInDashboard(user.isDontShowMessageInDashboardForRestore());
				u_settings.setEndUserLicAgree(user.isEndUserLicAgreeForRestore());
				u_settings.setMaxAPNum(user.getMaxAPNumForRestore());
				u_settings.setNavCustomization(user.getNavCustomizationForRestore());
				u_settings.setOrderFolders(user.isOrderFoldersForRestore());
				u_settings.setPromptChanges(user.isPromptChangesForRestore());
				u_settings.setSyncResult(user.getSyncResultForRestore());
				u_settings.setTreeWidth(user.getTreeWidthForRestore());

				try {
					QueryUtil.createBo(u_settings);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("Create hm_user_settings error.", e);
				}
			}

			users.add(user);
		}

		// create upgrade log for user
		if (!logList.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(logList);
			} catch (Exception e) {
				BeLogTools.restoreLog(BeLogTools.ERROR,
						"RestoreAdmin.getAllUsers() catch exception ", e);
			}
		}

		return !users.isEmpty() ? users : null;
	}

	private static Map<Long, Set<Long>> userSSIDProfileMap = new HashMap<Long, Set<Long>>();
	private static Map<Long, Set<Long>> userLocalUserGroupMap = new HashMap<Long, Set<Long>>();

	private static void prepareUserSSIDProfileMap() throws Exception {
		userSSIDProfileMap = new HashMap<Long, Set<Long>>();

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile("user_ssidprofile");
		if (!restoreRet) {
			return;
		}

		int rowCount = xmlParser.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			/**
			 * get group id
			 */
			String colName = "USER_ID";
			Long userID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			colName = "SSIDPROFILE_ID";
			Long profileID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			Long newProfileID = AhRestoreNewMapTools.getMapSsid(profileID);
			if (newProfileID != null) {
				Set<Long> set = userSSIDProfileMap.get(userID);
				if (set == null) {
					set = new HashSet<Long>();
					userSSIDProfileMap.put(userID, set);
				}
				set.add(newProfileID);
			}
		}
	}

	private static void prepareUserLocalUserGroupMap() throws Exception {
		userLocalUserGroupMap = new HashMap<Long, Set<Long>>();

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		boolean restoreRet = xmlParser.readXMLFile("user_localusergroup");
		if (!restoreRet) {
			return;
		}

		int rowCount = xmlParser.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			/**
			 * get group id
			 */
			String colName = "USER_ID";
			Long userID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			colName = "LOCALUSERGROUP_ID";
			Long groupID = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));

			Long newGroupID = AhRestoreNewMapTools.getMapLocalUserGroup(groupID);
			if (newGroupID != null) {
				Set<Long> set = userLocalUserGroupMap.get(userID);
				if (set == null) {
					set = new HashSet<Long>();
					userLocalUserGroupMap.put(userID, set);
				}
				set.add(newGroupID);
			}
		}
	}

	private static Set<LocalUserGroup> getLocalUserGroupSet(Long userID)
	{
		Set<LocalUserGroup> groupSet = new HashSet<LocalUserGroup>();

		Set<Long> set = userLocalUserGroupMap.get(userID);
		if (set != null && !set.isEmpty()) {
			for (Long id : set) {
				LocalUserGroup group = AhRestoreNewTools.CreateBoWithId(LocalUserGroup.class,id);
				groupSet.add(group);
			}
		}

		return groupSet;
	}

	private static Set<SsidProfile> getSSIDProfileSet(Long userID)
	{
		Set<SsidProfile> profileSet = new HashSet<SsidProfile>();

		Set<Long> set = userSSIDProfileMap.get(userID);
		if (set != null && !set.isEmpty()) {
			for (Long id : set) {
				SsidProfile profile = AhRestoreNewTools.CreateBoWithId(SsidProfile.class,id);
				profileSet.add(profile);
			}
		}

		return profileSet;
	}

	// ---------------------- hm_user -------------END---------

	// ---------------------- hm_auditlog -------------START---------

	public static boolean restoreAuditLog() {
		try {
			List<HmAuditLog> logs = getAllAuditLog();
			if (null == logs) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(logs);
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreAuditLog() catch exception ", e);
			return false;
		}
		return true;
	}

	public static boolean restoreSystemLog(){
		try {
			List<HmSystemLog> logs = getAllSystemLog();
			if (null == logs) {
				return false;
			} else {
				QueryUtil.restoreBulkCreateBos(logs);
			}
		} catch (Exception e) {
			BeLogTools.error(HmLogConst.M_RESTORE, "RestoreAdmin.restoreSystemLog() catch exception ", e);
			return false;
		}
		return true;
	}

	/**
	 * Get all information from hm_auditlog table
	 *
	 * @return List<HmAuditLog> all HmAuditLog BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_auditlog.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_auditlog.xml.
	 */
	private static List<HmAuditLog> getAllAuditLog() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_auditlog.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_auditlog");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HmAuditLog> logs = new ArrayList<HmAuditLog>();

		boolean isColPresent;
		String colName;
		HmAuditLog auditLog;
		for (int i = 0; i < rowCount; i++) {
			auditLog = new HmAuditLog();

			/**
			 * Set opeationComment
			 */
			colName = "opeationcomment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			String opeationComment = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			auditLog.setOpeationComment(opeationComment);

			/**
			 * Set hostIP
			 */
			colName = "hostip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			String hostIP = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			auditLog.setHostIP(hostIP);

			/**
			 * Set status
			 */
			colName = "status";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			short status = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(
					i, colName)) : 0);
			auditLog.setStatus(status);

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_auditlog' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			auditLog.setOwner(ownerDomain);
			/**
			 * Set logTimeStamp
			 */
			colName = "logTimeStamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			if (isColPresent) {
				long logTimeStamp = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				auditLog.setLogTimeStamp(logTimeStamp);

				colName = "logTimeZone";
				String logTimeZone = AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName));
				auditLog.setLogTimeZone(logTimeZone);
			} else {
				colName = "logTime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
				if (isColPresent) {
					Date logTime = AhRestoreCommons.convertDate(xmlParser.getColVal(i, colName));
					auditLog.setLogTimeStamp(logTime.getTime());
					auditLog.setLogTimeZone(ownerDomain.getTimeZoneString());
				}
			}

			/**
			 * Set userOwner
			 */
			colName = "userOwner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog", colName);
			if (!isColPresent) {
				// not existed userOwner column
				String userOwner = AhRestoreCommons.isColumnPresent(xmlParser, "hm_auditlog",
						"owner") ? xmlParser.getColVal(i, "owner") : "";
				auditLog.setUserOwner(AhRestoreCommons.convertString(userOwner));
			} else {
				auditLog.setUserOwner(AhRestoreCommons.convertString(xmlParser.getColVal(i,
						"userOwner")));
			}

			logs.add(auditLog);
		}

		return !logs.isEmpty() ? logs : null;
	}

	// ---------------------- hm_auditlog -------------END---------

	private static List<HmSystemLog> getAllSystemLog() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_auditlog.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_systemlog");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HmSystemLog> logs = new ArrayList<HmSystemLog>();

		boolean isColPresent;
		String colName;
		HmSystemLog sysLog;
		for (int i = 0; i < rowCount; i++) {
			sysLog = new HmSystemLog();

			colName = "level";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_systemlog", colName);
			short level = (short)(isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0);
			sysLog.setLevel(level);

			colName = "source";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_systemlog", colName);
			String source = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			sysLog.setSource(source);

			colName = "systemcomment";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_systemlog", colName);
			String systemcomment = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName)) : "";
			sysLog.setSystemComment(systemcomment);

			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_systemlog", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_auditlog' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			sysLog.setOwner(ownerDomain);

			colName = "logTimeStamp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_systemlog", colName);
			if (isColPresent) {
				long logTimeStamp = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				sysLog.setLogTimeStamp(logTimeStamp);

				colName = "logTimeZone";
				String logTimeZone = AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName));
				sysLog.setLogTimeZone(logTimeZone);
			}

			logs.add(sysLog);
		}

		return !logs.isEmpty() ? logs : null;
	}

	// ---------------------- schedule_backup -------------START---------

	public static boolean restoreScheduleBackup() {
		try {
			List<AhScheduleBackupData> schedules = getAllScheduleBackup();
			if (null == schedules) {
				return false;
			} else {
				// only restore one row data
				QueryUtil.restoreBulkCreateBos(schedules);
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreScheduleBackup() catch exception ", e);
			return false;
		}
		return true;
	}

	/**
	 * Get all information from schedule_backup table
	 *
	 * @return List<AhScheduleBackupData> all AhScheduleBackupData BO
	 * @throws AhRestoreColNotExistException
	 *             - if schedule_backup.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing schedule_backup.xml.
	 */
	private static List<AhScheduleBackupData> getAllScheduleBackup() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of schedule_backup.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("schedule_backup");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<AhScheduleBackupData> schedules = new ArrayList<AhScheduleBackupData>();

		boolean isColPresent;
		String colName;
		AhScheduleBackupData schedule;

		for (int i = 0; i < rowCount; i++) {
			schedule = new AhScheduleBackupData();

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			schedule.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * Set protocol
			 */
			colName = "protocol";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short protocol = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : EnumConstUtil.RESTORE_PROTOCOL_SCP);
			schedule.setProtocol(protocol);

			/**
			 * Set backupContent
			 */
			colName = "backupcontent";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short backupContent = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0); // 0:
			// full bak
			schedule.setBackupContent(backupContent);

			/**
			 * Set startdate
			 */
			colName = "startdate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String startDate = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (startDate.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'startdate' column is not exist.");
				continue;
			}
			schedule.setStartDate(startDate);

			/**
			 * Set startHour
			 */
			colName = "starthour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short startHour = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0);
			schedule.setStartHour(startHour);

			/**
			 * Set startMinute
			 */
			colName = "startminute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short startMinute = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0);
			schedule.setStartMinute(startMinute);

			/**
			 * Set endDateFlag
			 */
			colName = "enddateflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			boolean endDateFlag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			schedule.setEndDateFlag(endDateFlag);

			/**
			 * Set endDate
			 */
			colName = "enddate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String endDate = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (endDateFlag && endDate.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'enddate' column is not exist.");
				continue;
			}
			schedule.setEndDate(endDate);

			/**
			 * Set endHour
			 */
			colName = "endhour";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short endHour = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0);
			schedule.setEndHour(endHour);

			/**
			 * Set endMinute
			 */
			colName = "endminute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			short endMinute = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0);
			schedule.setEndMinute(endMinute);

			/**
			 * Set rescurFlag
			 */
			colName = "rescurFlag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			boolean rescurFlag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			schedule.setRescurFlag(rescurFlag);

			/**
			 * Set interval
			 */
			colName = "interval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			int interval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			schedule.setInterval(interval);

			/**
			 * Set scpIpAdd
			 */
			colName = "scpipadd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String scpIpAdd = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (scpIpAdd.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'scpipadd' column is not exist.");
				continue;
			}
			schedule.setScpIpAdd(scpIpAdd);

			/**
			 * Set scpPort
			 */
			colName = "scpport";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			int scpPort = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 22;
			schedule.setScpPort(scpPort);

			/**
			 * Set scpFilePath
			 */
			colName = "scpfilepath";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String scpFilePath = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName)) : "";
			if (scpFilePath.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'scpfilepath' column is not exist.");
				continue;
			}
			schedule.setScpFilePath(scpFilePath);

			/**
			 * Set scpUsr
			 */
			colName = "scpUsr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String scpUsr = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (scpUsr.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'scpUsr' column is not exist.");
				continue;
			}
			schedule.setScpUsr(scpUsr);

			/**
			 * Set scpPsd
			 */
			colName = "scpPsd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			String scpPsd = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (scpPsd.equals("")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'schedule_backup' data be lost, cause: 'scpPsd' column is not exist.");
				continue;
			}
			schedule.setScpPsd(scpPsd);

			/**
			 * Set liveFlag
			 */
			colName = "liveflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "schedule_backup", colName);
			boolean liveFlag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			schedule.setLiveFlag(liveFlag);

			schedules.add(schedule);
		}

		return !schedules.isEmpty() ? schedules : null;
	}

	// ---------------------- schedule_backup -------------END---------

	// ---------------------- mail_notification -------------START---------

	public static boolean restoreEmailNotfication() {
		try {
			restoreMailNotification();

			restoreMailNotification4VHM();

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreEmailNotfication() catch exception ", e);
			return false;
		}
	}

	private static void restoreMailNotification() {
		try {
			List<MailNotification> list = getAllEmailNotficationSettings();
			if (null == list || list.isEmpty()) {
				return;
			}

			updateDBMailNotificationBo(list);

		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreMailNotification() catch exception ", e);
		}
	}

	private static void updateDBMailNotificationBo(List<MailNotification> list) throws Exception {
		List<MailNotification> updateList = new ArrayList<MailNotification>();
		List<MailNotification> createList = new ArrayList<MailNotification>();
		for (MailNotification mailNotification : list) {
			MailNotification destBo = QueryUtil.findBoByAttribute(MailNotification.class, "owner.id",
					mailNotification.getOwner().getId());

			if (destBo == null) {
				createList.add(mailNotification);
			} else {
				// update to db
				destBo.setAuth(mailNotification.isAuth());
				destBo.setCapWap(mailNotification.getCapWap());
				destBo.setSecurity(mailNotification.getSecurity());
				destBo.setConfig(mailNotification.getConfig());
				destBo.setHdCpu(mailNotification.isHdCpu());
				destBo.setHdMemory(mailNotification.isHdMemory());
				destBo.setHdRadio(mailNotification.getHdRadio());
				destBo.setInterfaceValue(mailNotification.isInterfaceValue());
				destBo.setL2Dos(mailNotification.isL2Dos());
				destBo.setMailFrom(mailNotification.getMailFrom());
				destBo.setMailTo(mailNotification.getMailTo());
				destBo.setScreen(mailNotification.isScreen());
				destBo.setTimeBomb(mailNotification.getTimeBomb());
				destBo.setSendMailFlag(mailNotification.getSendMailFlag());
				destBo.setServerName(mailNotification.getServerName());
				destBo.setSupportPwdAuth(mailNotification.isSupportPwdAuth());
				destBo.setSupportSSL(mailNotification.isSupportSSL());
				destBo.setSupportTLS(mailNotification.isSupportTLS());
				destBo.setPort(mailNotification.getPort());
				destBo.setEmailUserName(mailNotification.getEmailUserName());
				destBo.setEmailPassword(mailNotification.getEmailPassword());
				destBo.setClientMonitor(mailNotification.isClientMonitor());
				destBo.setAd(mailNotification.getAd());
				destBo.setTca(mailNotification.getTca());
				destBo.setInNetIdp(mailNotification.isInNetIdp());
				destBo.setAirScreen(mailNotification.isAirScreen());
				destBo.setVpn(mailNotification.isVpn());
				destBo.setClient(mailNotification.getClient());

				updateList.add(destBo);
			}
		}
		if (!createList.isEmpty()) {
			QueryUtil.restoreBulkCreateBos(createList);
		}
		if (!updateList.isEmpty()) {
			QueryUtil.bulkUpdateBos(updateList);
		}
	}

	private static void restoreMailNotification4VHM() throws Exception {
		List<MailNotification4VHM> list = getEmailNotficationSettings4VHM();
		if (null == list || list.isEmpty()) {
			return;
		}

		List<MailNotification4VHM> updateList = new ArrayList<MailNotification4VHM>();
		List<MailNotification4VHM> createList = new ArrayList<MailNotification4VHM>();
		for (MailNotification4VHM bo : list) {
			MailNotification4VHM destBo = QueryUtil.findBoByAttribute(MailNotification4VHM.class,
					"owner.id", bo.getOwner().getId());

			if (destBo == null) {
				createList.add(bo);
			} else {
				// update to db
				destBo.setMailTo(bo.getMailTo());
				updateList.add(destBo);
			}
		}
		if (!createList.isEmpty()) {
			QueryUtil.bulkCreateBos(createList);
		}
		if (!updateList.isEmpty()) {
			QueryUtil.bulkUpdateBos(updateList);
		}
	}

	// 3.4version, remove event type from gui and add vpn,airscreen type.
	private static boolean	fromPreTo3dot4	= false;

	/**
	 * Get all information from mail_notification table
	 *
	 * @return List<MailNotification> all MailNotification BO
	 * @throws AhRestoreColNotExistException
	 *             - if mail_notification.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing mail_notification.xml.
	 */
	public static List<MailNotification> getAllEmailNotficationSettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of schedule_backup.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mail_notification");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in local_user table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MailNotification> notifications = new ArrayList<MailNotification>();

		boolean isColPresent;
		String colName;
		MailNotification notification;

		for (int i = 0; i < rowCount; i++) {
			notification = new MailNotification();

			/**
			 * Set servername
			 */
			colName = "servername";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String serverName = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName)) : "";
			notification.setServerName(serverName);

			/**
			 * Set mailfrom
			 */
			colName = "mailfrom";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String mailFrom = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			notification.setMailFrom(mailFrom);

			/**
			 * Set mailto
			 */
			colName = "mailto";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String mailTo = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			notification.setMailTo(mailTo);

			/**
			 * idp comment: add this field in 3.4r2
			 */
			colName = "inNetIdp";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				boolean inNetIdp = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
						colName));
				notification.setInNetIdp(inNetIdp);
			}

			/**
			 * vpn comment: add this field in 3.4
			 */
			colName = "vpn";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				boolean vpn = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
						colName));
				notification.setVpn(vpn);
			} else {
				fromPreTo3dot4 = true;
			}

			/**
			 * airscreen comment: add this field in 3.4
			 */
			colName = "airScreen";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				boolean airScreen = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
						colName));
				notification.setAirScreen(airScreen);
			}

			/**
			 * Set hdCpu
			 */
			colName = "hdcpu";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int hdCpu = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					notification.setHdCpu(hdCpu > 0);
				} else {
					boolean hdCpu = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
							colName));
					notification.setHdCpu(hdCpu);
				}
			}

			/**
			 * Set hdMemory
			 */
			colName = "hdmemory";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int hdMemory = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					notification.setHdMemory(hdMemory > 0);
				} else {
					boolean hdMemory = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(
							i, colName));
					notification.setHdMemory(hdMemory);
				}
			}

			/**
			 * Set auth
			 */
			colName = "auth";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int auth = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					notification.setAuth(auth > 0);
				} else {
					boolean auth = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
							colName));
					notification.setAuth(auth);
				}
			}

			/**
			 * Set interfaceValue
			 */
			colName = "interfacevalue";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int interfaceValue = AhRestoreCommons.convertInt(xmlParser
							.getColVal(i, colName));
					notification.setInterfaceValue(interfaceValue > 0);
				} else {
					boolean interfaceValue = AhRestoreCommons.convertStringToBoolean(xmlParser
							.getColVal(i, colName));
					notification.setInterfaceValue(interfaceValue);
				}
			}

			/**
			 * Set clientMonitor
			 */
			colName = "clientMonitor";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				boolean clientMonitor = AhRestoreCommons.convertStringToBoolean(xmlParser
						.getColVal(i, colName));
				notification.setClientMonitor(clientMonitor);
			}

			/**
			 * Set clientRegister
			 */
			colName = "clientRegister";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				boolean clientRegister = AhRestoreCommons.convertStringToBoolean(xmlParser
						.getColVal(i, colName));
				notification.setClientRegister(clientRegister);
			}

			/**
			 * Set l2Dos
			 */
			colName = "l2dos";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int l2DOS = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					notification.setL2Dos(l2DOS > 0);
				} else {
					boolean l2DOS = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
							colName));
					notification.setL2Dos(l2DOS);
				}
			}

			/**
			 * Set screen
			 */
			colName = "screen";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				if (fromPreTo3dot4) {
					int screen = AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName));
					notification.setScreen(screen > 0);
				} else {
					boolean screen = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,
							colName));
					notification.setScreen(screen);
				}
			}

			/**
			 * Set hdRadio
			 */
			colName = "hdradio";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String hdRadio = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "16";
			notification.setHdRadio(Byte.valueOf(hdRadio));

			/**
			 * Set capWap
			 */
			colName = "capwap";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String capWap = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "29";
			notification.setCapWap(Byte.valueOf(capWap));

			/**
			 * Set ad
			 */
			colName = "ad";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String ad = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "9";
			notification.setAd(Byte.valueOf(ad));

			/**
			 * Set tca
			 */
			colName = "tca";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String tca = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "17";
			notification.setTca(Byte.valueOf(tca));

			/**
			 * Set security
			 */
			colName = "security";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String security = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "8";
			notification.setSecurity(Byte.valueOf(security));

			/**
			 * Set config
			 */
			colName = "config";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String config = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "8";
			notification.setConfig(Byte.valueOf(config));

			/**
			 * Set System
			 */
			colName = "system";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String system = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "9";
			notification.setSystem(Byte.valueOf(system));

			/**
			 * Set timeBomb
			 */
			colName = "timeBomb";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String timeBomb = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "28";
			notification.setTimeBomb(Byte.valueOf(timeBomb));


			/**
			 * Set client
			 */
			colName = "client";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser,  "mail_notification", colName);
			String client = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "5";
			notification.setClient(Byte.valueOf(client));


			/**
			 * Set sendMailFlag
			 */
			colName = "sendmailflag";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			boolean sendMailFlag = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			notification.setSendMailFlag(sendMailFlag);

			/**
			 * Set supportSSL
			 */
			colName = "supportSSL";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			boolean supportSSL = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			notification.setSupportSSL(supportSSL);

			/**
			 * Set supportTLS
			 */
			colName = "supportTLS";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			boolean supportTLS = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			notification.setSupportTLS(supportTLS);

			/**
			 * Set port
			 */
			colName = "port";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			if (isColPresent) {
				notification.setPort(AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)));
			} else {
				if (supportSSL) {
					notification.setPort(465);
				}
			}

			/**
			 * Set supportPwdAuth
			 */
			colName = "supportPwdAuth";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			boolean supportPwdAuth = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			notification.setSupportPwdAuth(supportPwdAuth);

			/**
			 * Set emailUserName
			 */
			colName = "emailUserName";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String emailUserName = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			notification.setEmailUserName(emailUserName);

			/**
			 * Set emailPassword
			 */
			colName = "emailPassword";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			String emailPassword = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			notification.setEmailPassword(emailPassword);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'mail_notification' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			notification.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (null == notification.getOwner())
				continue;

			notifications.add(notification);
		}

		return !notifications.isEmpty() ? notifications : null;
	}

	/**
	 * Get all information from mail_notification_vhm table
	 *
	 * @return List<MailNotification4VHM> all MailNotification4VHM BO
	 * @throws AhRestoreColNotExistException
	 *             - if mail_notification.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing mail_notification.xml.
	 */
	public static List<MailNotification4VHM> getEmailNotficationSettings4VHM() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of mail_notification_vhm.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mail_notification_vhm");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<MailNotification4VHM> notifications = new ArrayList<MailNotification4VHM>();

		for (int i = 0; i < rowCount; i++) {
			MailNotification4VHM notification = new MailNotification4VHM();

			/**
			 * Set mailto
			 */
			String colName = "mailto";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "mail_notification_vhm",
					colName);
			String mailTo = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			// if (mailTo.equals(""))
			// {
			// continue;
			// }
			notification.setMailTo(mailTo);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons
					.isColumnPresent(xmlParser, "mail_notification_vhm", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}
			notification.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (null == notification.getOwner())
				continue;

			notifications.add(notification);
		}

		return !notifications.isEmpty() ? notifications : null;
	}

	// ---------------------- mail_notification -------------END---------

	// ---------------------- capwapsettings -------------START---------

	public static boolean restoreCapwapSettings() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return true;
		// }

		try {
			List<CapwapSettings> capwapSettings = getAllCapwapSettings();
			if (null == capwapSettings || capwapSettings.isEmpty()) {
				return false;
			}

			List<CapwapSettings> list = QueryUtil.executeQuery(CapwapSettings.class, null, null);

			if (list.isEmpty()) {
				QueryUtil.createBo(capwapSettings.get(0));
			} else {
				CapwapSettings destBo = list.get(0);
				CapwapSettings srcBo = capwapSettings.get(0);
				destBo.setBootStrap(srcBo.getBootStrap());
				destBo.setDtlsCapability(srcBo.getDtlsCapability());
				destBo.setNeighborDeadInterval(srcBo.getNeighborDeadInterval());
				destBo.setOwner(srcBo.getOwner());
				destBo.setTimeOut(srcBo.getTimeOut());
				destBo.setUdpPort(srcBo.getUdpPort());
				destBo.setPrimaryCapwapIP(srcBo.getPrimaryCapwapIP());
				destBo.setBackupCapwapIP(srcBo.getBackupCapwapIP());
				QueryUtil.updateBo(destBo);
			}

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreCapwapSettings() catch exception ", e);
			return false;
		}
	}

	/**
	 * Get all information from capwapsettings table
	 *
	 * @return List<CapwapSettings> all CapwapSettings BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_capwapsettings.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_capwapsettings.xml.
	 */
	private static List<CapwapSettings> getAllCapwapSettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of capwapsettings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("capwapsettings");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<CapwapSettings> settings = new ArrayList<CapwapSettings>();

		boolean isColPresent;
		String colName;
		CapwapSettings capwapsettings;
		for (int i = 0; i < rowCount; i++) {
			capwapsettings = new CapwapSettings();

			/**
			 * Set udpPort
			 */
			colName = "udpPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			int port = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
					: 12222;
			capwapsettings.setUdpPort(port);

			/**
			 * Set timeOut
			 */
			colName = "timeOut";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			short timeOut = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30);
			capwapsettings.setTimeOut(timeOut);

			/**
			 * Set neightbor dead interval
			 */
			colName = "neighborDeadInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			short deadInterval = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 105);
			capwapsettings.setNeighborDeadInterval(deadInterval);

			/**
			 * Set trapFilterInterval
			 */
			colName = "trapFilterInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			short trapFilterInterval = (short) (isColPresent ? AhRestoreCommons
					.convertInt(xmlParser.getColVal(i, colName)) : 3);
			capwapsettings.setTrapFilterInterval(trapFilterInterval);

			// /**
			// * Set dtlsCapability
			// */
			// colName = "dtlsCapability";
			// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings",
			// colName);
			// byte dtlsCapability = (byte) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
			// .getColVal(i, colName)) : 2);
			// capwapsettings.setDtlsCapability(dtlsCapability);
			capwapsettings.setDtlsCapability(CapwapSettings.DTLS_DTLSONLY);

			/**
			 * Set bootStrap
			 */
			colName = "bootStrap";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			String bootStrap = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			capwapsettings.setBootStrap(bootStrap);

			/**
			 * Set primaryCapwapIP
			 */
			colName = "primaryCapwapIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			String primaryCapwapIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			capwapsettings.setPrimaryCapwapIP(AhRestoreCommons.convertString(primaryCapwapIP));

			/**
			 * Set backupCapwapIP
			 */
			colName = "backupCapwapIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			String backupCapwapIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			capwapsettings.setBackupCapwapIP(AhRestoreCommons.convertString(backupCapwapIP));

			/**
			 * Set enableRollback
			 */
			colName = "enableRollback";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			boolean enableRollback = !isColPresent || AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName));
			capwapsettings.setEnableRollback(enableRollback);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "capwapsettings", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}

			capwapsettings.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			settings.add(capwapsettings);
		}

		return !settings.isEmpty() ? settings : null;
	}

	// ---------------------- capwapsettings -------------END---------

	// ---------------------- logsettings -------------START---------

	public static boolean restoreLogSettings() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return true;
		// }

		try {
			List<LogSettings> logSettings = getAllLogSettings();
			if (null == logSettings || logSettings.isEmpty()) {
				return false;
			}

			List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);

			if (list.isEmpty()) {
				QueryUtil.createBo(logSettings.get(0));
			} else {
				LogSettings destBo = list.get(0);
				LogSettings srcBo = logSettings.get(0);
				destBo.setAlarmInterval(srcBo.getAlarmInterval());
				destBo.setAlarmRetainUnclearDays(srcBo.getAlarmRetainUnclearDays());
				destBo.setAlarmMaxRecords(srcBo.getAlarmMaxRecords());
				destBo.setAlarmReminderDays(srcBo.getAlarmReminderDays());
				destBo.setEventInterval(srcBo.getEventInterval());
				destBo.setMaxPerfRecord(srcBo.getMaxPerfRecord());
				destBo.setMaxHistoryClientRecord(srcBo.getMaxHistoryClientRecord());
				destBo.setSyslogExpirationDays(srcBo.getSyslogExpirationDays());
				destBo.setAuditlogExpirationDays(srcBo.getAuditlogExpirationDays());
				destBo.setL3FirewallLogExpirationDays(srcBo.getL3FirewallLogExpirationDays());
				destBo.setInterfaceStatsInterval(srcBo.getInterfaceStatsInterval()<=0?30:srcBo.getInterfaceStatsInterval());
				destBo.setStatsStartMinute(srcBo.getStatsStartMinute()<0?0:srcBo.getStatsStartMinute());

				destBo.setMaxOriginalCount(srcBo.getMaxOriginalCount());
				destBo.setMaxHourValue(srcBo.getMaxHourValue());
				destBo.setMaxDayValue(srcBo.getMaxDayValue());
				destBo.setMaxWeekValue(srcBo.getMaxWeekValue());
				destBo.setSlaPeriod(srcBo.getSlaPeriod());
				destBo.setClientPeriod(srcBo.getClientPeriod());
				destBo.setMaxSupportAp(srcBo.getMaxSupportAp());

				destBo.setReportIntervalMinute(srcBo.getReportIntervalMinute());
				destBo.setReportMaxApCount(srcBo.getReportMaxApCount());
				destBo.setReportMaxApForSystem(srcBo.getReportMaxApForSystem());

				destBo.setReportDbHourly(srcBo.getReportDbHourly());
				destBo.setReportDbDaily(srcBo.getReportDbDaily());
				destBo.setReportDbWeekly(srcBo.getReportDbWeekly());

				destBo.setIntervalTablePartPer(srcBo.getIntervalTablePartPer());
				destBo.setMaxTimeTablePerSave(srcBo.getMaxTimeTablePerSave());
				destBo.setIntervalTablePartCli(srcBo.getIntervalTablePartCli());
				destBo.setMaxTimeTableCliSave(srcBo.getMaxTimeTableCliSave());

				destBo.setOwner(srcBo.getOwner());
				QueryUtil.updateBo(destBo);
			}

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreLogSettings() catch exception ", e);
			return false;
		}
	}

	/**
	 * Get all information from logsettings table
	 *
	 * @return List<LogSettings> all logsettings BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_logsettings.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_logsettings.xml.
	 */
	private static List<LogSettings> getAllLogSettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of capwapsettings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("logsettings");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<LogSettings> settings = new ArrayList<LogSettings>();

		boolean isColPresent;
		String colName;
		LogSettings logSettings;
		for (int i = 0; i < rowCount; i++) {
			logSettings = new LogSettings();

			/**
			 * Set alarmInterval
			 */
			colName = "alarmInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int alarmInterval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : LogSettings.DEFAULT_ALARM_INTERVAL;
			logSettings.setAlarmInterval(alarmInterval);
			
			/**
			 * Set alarmRetainUnclearDays
			 */
			colName = "alarmRetainUnclearDays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int alarmRetainUnclearDays = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : LogSettings.DEFAULT_ALARM_RETAIN_UNCLEAR_DAYS;
			logSettings.setAlarmRetainUnclearDays(alarmRetainUnclearDays);
			/**
			 * Set alarmMaxRecords
			 */
			colName = "alarmMaxRecords";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int alarmMaxRecords = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : -1;
			logSettings.setAlarmMaxRecords(alarmMaxRecords);
			/**
			 * Set alarmReminderDays
			 */
			colName = "alarmReminderDays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int alarmReminderDays = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : LogSettings.DEFAULT_ALARM_REMINDER_DAYS;
			logSettings.setAlarmReminderDays(alarmReminderDays);

			/**
			 * Set eventInterval
			 */
			colName = "eventInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int eventInterval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 7;
			logSettings.setEventInterval(eventInterval);

			/**
			 * Set maxPerfRecord
			 */
			colName = "maxPerfRecord";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxPerfRecord = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 500000;
			if (maxPerfRecord > 2000000 || maxPerfRecord < 20000) {
				// define max performance record range in 3.2
				maxPerfRecord = 500000;
			}

			logSettings.setMaxPerfRecord(maxPerfRecord);

			/**
			 * Set maxHistoryClientRecord
			 */
			colName = "maxHistoryClientRecord";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxHistoryClientRecord = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 2000000;
			logSettings.setMaxHistoryClientRecord(maxHistoryClientRecord);
			if (maxHistoryClientRecord==0) {
				logSettings.setMaxHistoryClientRecord(2000000);
			}
			/**
			 * Set interfaceStatsInterval
			 */
			colName = "interfaceStatsInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int interfaceStatsInterval = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30;
			if ((interfaceStatsInterval<=0 || interfaceStatsInterval>60)) {
				interfaceStatsInterval = 30;
			}
			if ((interfaceStatsInterval==15 || interfaceStatsInterval==45)) {
				interfaceStatsInterval = 30;
			}
			logSettings.setInterfaceStatsInterval(interfaceStatsInterval);

			/**
			 * Set statsStartMinute
			 */
			colName = "statsStartMinute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int statsStartMinute = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 0;
			logSettings.setStatsStartMinute(statsStartMinute<0?0:statsStartMinute);

			/**
			 * Set syslogExpirationDays
			 */
			colName = "syslogExpirationDays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int syslogExpirationDays = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : LogSettings.DEFAULT_SYSLOG_EXPIRATIONDAYS;
			logSettings.setSyslogExpirationDays(syslogExpirationDays);

			/**
			 * Set auditlogExpirationDays
			 */
			colName = "auditlogExpirationDays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int auditlogExpirationDays = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : LogSettings.DEFAULT_AUDITLOG_EXPIRATIONDAYS;
			logSettings.setAuditlogExpirationDays(auditlogExpirationDays);

			/**
			 * Set l3FirewallLogExpirationDays
			 */
			colName = "l3FirewallLogExpirationDays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int l3FirewallLogExpirationDays = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : LogSettings.DEFAULT_L3FIREWALLLOG_EXPIRATIONDAYS;
			logSettings.setL3FirewallLogExpirationDays(l3FirewallLogExpirationDays);

			/**
			 * Set maxOriginalCount
			 */
			colName = "maxOriginalCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxOriginalCount = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 24;
			logSettings.setMaxOriginalCount(maxOriginalCount);
			if (logSettings.getMaxOriginalCount() >48 || logSettings.getMaxOriginalCount() <24) {
				logSettings.setMaxOriginalCount(24);
			}

			/**
			 * Set maxHourValue
			 */
			colName = "maxHourValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxHourValue = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 2;
			if (RestoreHiveAp.restore_from_60r1_before){
				if (maxHourValue==7) {
					logSettings.setMaxHourValue(2);
				} else {
					logSettings.setMaxHourValue(maxHourValue);
				}
			} else {
				logSettings.setMaxHourValue(maxHourValue);
			}

			if (logSettings.getMaxHourValue() >7 || logSettings.getMaxHourValue() <2) {
				logSettings.setMaxHourValue(2);
			}

			/**
			 * Set maxDayValue
			 */
			colName = "maxDayValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxDayValue = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 5;
			logSettings.setMaxDayValue(maxDayValue);

			if (logSettings.getMaxDayValue() >8 || logSettings.getMaxDayValue() <5) {
				logSettings.setMaxDayValue(5);
			}

			/**
			 * Set maxWeekValue
			 */
			colName = "maxWeekValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxWeekValue = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 12;
			logSettings.setMaxWeekValue(maxWeekValue);
			if (logSettings.getMaxWeekValue() >24 || logSettings.getMaxWeekValue() <12) {
				logSettings.setMaxWeekValue(12);
			}

			/**
			 * Set maxSupportAp
			 */
			colName = "maxSupportAp";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxSupportAp = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 200;
			logSettings.setMaxSupportAp(maxSupportAp);

			/**
			 * Set slaPeriod
			 */
			colName = "slaPeriod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int slaPeriod = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 3;
			logSettings.setSlaPeriod(slaPeriod);

			/**
			 * Set clientPeriod
			 */
			colName = "clientPeriod";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int clientPeriod = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 10;
			logSettings.setClientPeriod(clientPeriod);

			/*
			 * Set reportIntervalMinute
			 */
			colName = "reportIntervalMinute";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportIntervalMinute = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 10;
			logSettings.setReportIntervalMinute(reportIntervalMinute);

			/*
			 * Set reportMaxApCount
			 */
			colName = "reportMaxApCount";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportMaxApCount = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 20;
			logSettings.setReportMaxApCount(reportMaxApCount);

			/*
			 * Set reportMaxApForSystem
			 */
			colName = "reportMaxApForSystem";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportMaxApForSystem = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 100;
			logSettings.setReportMaxApForSystem(reportMaxApForSystem);

			/*
			 * Set reportDbHourly
			 */
			colName = "reportDbHourly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportDbHourly = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30000000;
			logSettings.setReportDbHourly(reportDbHourly);

			/*
			 * Set reportDbDaily
			 */
			colName = "reportDbDaily";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportDbDaily = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30000000;
			logSettings.setReportDbDaily(reportDbDaily);

			/*
			 * Set reportDbWeekly
			 */
			colName = "reportDbWeekly";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int reportDbWeekly = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30000000;
			logSettings.setReportDbWeekly(reportDbWeekly);

			/**
			 * Set intervalTablePartPer
			 */
			colName = "intervalTablePartPer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int intervalTablePartPer = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 1;
			logSettings.setIntervalTablePartPer(intervalTablePartPer);

			/**
			 * Set maxTimeTablePerSave
			 */
			colName = "maxTimeTablePerSave";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxTimeTablePerSave = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 3;
			logSettings.setMaxTimeTablePerSave(maxTimeTablePerSave);

			/**
			 * Set intervalTablePartCli
			 */
			colName = "intervalTablePartCli";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int intervalTablePartCli = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 8;
			logSettings.setIntervalTablePartCli(intervalTablePartCli);

			/**
			 * Set maxTimeTableCliSave
			 */
			colName = "maxTimeTableCliSave";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			int maxTimeTableCliSave = isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 3;
			logSettings.setMaxTimeTableCliSave(maxTimeTableCliSave);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "logsettings", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				continue;
			}

			logSettings.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			settings.add(logSettings);
		}

		return !settings.isEmpty() ? settings : null;
	}

	// ---------------------- logSettings -------------END---------

	// ---------------------- HMServicesSettings -------------START---------

	public static boolean restoreHMServicesSettings() {
		try {
			List<HMServicesSettings> hmServiceSettings = getAllHMServicesSettings();
			if (null == hmServiceSettings || hmServiceSettings.isEmpty()) {
				return false;
			}

			// fix issue
			updateHMServicesSettings(hmServiceSettings);

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreActiveClientSettings() catch exception ", e);
			return false;
		}
	}

	private static void updateHMServicesSettings(List<HMServicesSettings> list) throws Exception {
		List<HMServicesSettings> updateList = new ArrayList<HMServicesSettings>();
		List<HMServicesSettings> createList = new ArrayList<HMServicesSettings>();
		List<HmUpgradeLog> logList = new ArrayList<HmUpgradeLog>();
		for (HMServicesSettings bo : list) {
			HMServicesSettings destBo = QueryUtil.findBoByAttribute(HMServicesSettings.class,
					"owner.id", bo.getOwner().getId());

			if (destBo == null) {
				createList.add(bo);
			} else {
				// update to db
				destBo.setEnableClientRefresh(bo.isEnableClientRefresh());
				destBo.setEnableClientManagement(bo.isEnableClientManagement());
				destBo.setEnableCidPolicyEnforcement(bo.isEnableCidPolicyEnforcement());
				destBo.setApiKey(bo.getApiKey());
				destBo.setInfiniteSession(bo.isInfiniteSession());
				destBo.setRefreshFilterName(bo.getRefreshFilterName());
				destBo.setRefreshInterval(bo.getRefreshInterval());
				destBo.setSessionExpiration(bo.getSessionExpiration());
				destBo.setSnmpCommunity(bo.getSnmpCommunity());
				destBo.setSnmpReceiverIP(bo.getSnmpReceiverIP());
				destBo.setShowNotifyInfo(bo.isShowNotifyInfo());
				destBo.setNotifyInformationTitle(bo.getNotifyInformationTitle());
				destBo.setNotifyInformation(bo.getNotifyInformation());
				destBo.setEnableProxy(bo.isEnableProxy());
				destBo.setProxyServer(bo.getProxyServer());
				destBo.setProxyPort(bo.getProxyPort());
				destBo.setProxyUserName(bo.getProxyUserName());
				destBo.setProxyPassword(bo.getProxyPassword());
				destBo.setEnableTeacher(bo.isEnableTeacher());
				destBo.setPresenceEnable(bo.isPresenceEnable());
				destBo.setApSlaType(bo.getApSlaType());
				destBo.setClientSlaType(bo.getClientSlaType());
				destBo.setSnpMaximum(bo.getSnpMaximum());
				destBo.setMaxUpdateNum(bo.getMaxUpdateNum());
				destBo.setAuthorizationKey(bo.getAuthorizationKey());
				destBo.setServiceHost(bo.getServiceHost());
				destBo.setServicePort(bo.getServicePort());
				destBo.setWindowsDomain(bo.getWindowsDomain());
				destBo.setBarracudaDefaultUserName(bo.getBarracudaDefaultUserName());
				destBo.setAccountID(bo.getAccountID());
				destBo.setSecurityKey(bo.getSecurityKey());
				destBo.setWebSenseServiceHost(bo.getWebSenseServiceHost());
				destBo.setPort(bo.getPort());
				destBo.setWensenseMode(bo.getWensenseMode());
				destBo.setDefaultDomain(bo.getDefaultDomain());
				destBo.setWebSenseDefaultUserName(bo.getWebSenseDefaultUserName());
				destBo.setBarracudaWhitelist(bo.getBarracudaWhitelist());
				destBo.setWebsenseWhitelist(bo.getWebsenseWhitelist());
				destBo.setEnableBarracuda(bo.isEnableBarracuda());
				destBo.setEnableWebsense(bo.isEnableWebsense());
				destBo.setConcurrentConfigGenNum(bo.getConcurrentConfigGenNum());
				destBo.setConcurrentSearchUserNum(bo.getConcurrentSearchUserNum());
				destBo.setVirtualHostName(bo.getVirtualHostName());

				destBo.setEnabledBetaIDM(bo.isEnabledBetaIDM());
				destBo.setEnableCollectAppData(bo.isEnableCollectAppData());
				destBo.setClassifierTag(bo.getClassifierTag());
				destBo.setTimeFormat(bo.getTimeFormat());
				destBo.setTimeType(bo.getTimeType());
				destBo.setDateFormat(bo.getDateFormat());
				destBo.setDateSeparator(bo.getDateSeparator());

				destBo.setEnableTVProxy(bo.isEnableTVProxy());
				destBo.setEnableCaseSensitive(bo.getEnableCaseSensitive());
				destBo.setTvAutoProxyFile(bo.getTvAutoProxyFile());
				destBo.setTvProxyIP(bo.getTvProxyIP());
				destBo.setTvProxyPort(bo.getTvProxyPort());

				destBo.setEnableRadarDetection(bo.isEnableRadarDetection());
				destBo.setEnableSystemL7Switch(bo.isEnableSystemL7Switch());
				destBo.setNotifyCleanWatchList(bo.isNotifyCleanWatchList());
				destBo.setNotifyUpdateWatchList(bo.isNotifyUpdateWatchList());
				destBo.setNotifyDisableL7(bo.isNotifyDisableL7());
				destBo.setEnableSupplementalCLI(bo.isEnableSupplementalCLI());
				
				//HM API
				HMServicesSettings oldSettings = QueryUtil.findBoByAttribute(HMServicesSettings.class, "apiUserName",
						bo.getApiUserName());
				if(null==oldSettings){
					destBo.setApiUserName(bo.getApiUserName());
					destBo.setApiPassword(bo.getApiPassword());
					destBo.setEnableApiAccess(bo.isEnableApiAccess());
				}
				//For OpenDNS Feature
				destBo.setEnableOpenDNS(bo.isEnableOpenDNS());           
				destBo.setOpenDNSAccount(bo.getOpenDNSAccount());	

				// the hivemanager online if is in maintenance mode
				if (NmsUtil.isHostedHMApplication() && bo.getOwner().isHomeDomain()) {
					destBo.setHmStatus(bo.getHmStatus());
				}
				// the teacher view flag
				if (bo.getOwner().isHomeDomain()) {
					NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED = bo.isEnableTeacher();
				}
				updateList.add(destBo);
			}
		}
		if (!createList.isEmpty()) {
			QueryUtil.restoreBulkCreateBos(createList);
		}
		if (!updateList.isEmpty()) {
			QueryUtil.bulkUpdateBos(updateList);
		}
		if (!logList.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(logList);
			} catch (Exception e) {
				BeLogTools.restoreLog(BeLogTools.ERROR,
						"RestoreAdmin.updateHMServicesSettings() catch exception ", e);
			}
		}

	}

	/**
	 * Get all information from clientMonitorSettings table
	 *
	 * @return List<HMServicesSettings> all logsettings BO
	 * @throws AhRestoreColNotExistException
	 *             - if hm_clientMonitorSettings.xml is not exist.
	 * @throws AhRestoreException
	 *             - if error in parsing hm_clientMonitorSettings.xml.
	 */
	private static List<HMServicesSettings> getAllHMServicesSettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		String fileName = "clientmonitorsettings";
		if (!AhRestoreGetXML.checkXMLFileExist(fileName)) {
			fileName = "hmservicessettings";
		}
		String tableName = fileName;

		/**
		 * Check validation of clientmonitorsettings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(fileName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in schedule_backup table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HMServicesSettings> settings = new ArrayList<HMServicesSettings>();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {
			HMServicesSettings bo = new HMServicesSettings();

			/**
			 * Set enableClientRefresh
			 */
			colName = "enableClientRefresh";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableClientRefresh = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			bo.setEnableClientRefresh(enableClientRefresh);

/**
			 * Set enableClientManagement
			 */
			colName = "enableClientManagement";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableClientManagement = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			bo.setEnableClientManagement(enableClientManagement);

			/**
			 * Set enableCidClientEnforcement
			 */
			colName = "enableCidPolicyEnforcement";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableCidPolicyEnforcement = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			bo.setEnableCidPolicyEnforcement(enableCidPolicyEnforcement);
/**
			 * Set apiKey
			 */
			colName = "apiKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String apiKey = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			bo.setApiKey(apiKey);
			/**
			 * Set refreshInterval
			 */
			colName = "refreshInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int refreshInterval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 60;

			if (enableClientRefresh && (refreshInterval < 10 || refreshInterval > 1440)) {
				refreshInterval = 60;
			}

			bo.setRefreshInterval(refreshInterval);

			/**
			 * Set refreshFilterName
			 */
			colName = "refreshFilterName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String refreshFilterName = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			bo.setRefreshFilterName(refreshFilterName);

			/**
			 * Set session expiration
			 */
			colName = "sessionExpiration";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int sessionExpiration = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(
					i, colName)) : 15;
			bo.setSessionExpiration(sessionExpiration);

			/**
			 * Set session finite
			 */
			colName = "infiniteSession";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean infiniteSession = isColPresent
					&& AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
			bo.setInfiniteSession(infiniteSession);

			/**
			 * Set snmpCommunity
			 */
			colName = "snmpCommunity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String snmpCommunity = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "";
			bo.setSnmpCommunity(snmpCommunity);

			/**
			 * Set snmpreceiverip
			 */
			colName = "snmpreceiverip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if (!"".equals(ipId)) {
				Long newID = AhRestoreNewMapTools.getMapIpAdddress(Long.valueOf(ipId));
				if (null != newID) {
					bo.setSnmpReceiverIP(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newID));
				}
			}

            /**
             * Set showNotifyInfo
             */
            colName = "showNotifyInfo";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            boolean showNotifyInfo = isColPresent
                    && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
            bo.setShowNotifyInfo(showNotifyInfo);

            /**
             * Set notifyInformationTitle
             */
            colName = "notifyInformationTitle";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String notifyInformationTitle = isColPresent ? AhRestoreCommons.convertString(xmlParser
                    .getColVal(i, colName)) : "";
            bo.setNotifyInformationTitle(notifyInformationTitle);

            /**
             * Set notifyInformation
             */
            colName = "notifyInformation";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String notifyInformation = isColPresent ? AhRestoreCommons.convertString(xmlParser
                    .getColVal(i, colName)) : "";
            bo.setNotifyInformation(notifyInformation);

			/**
			 * Set maintenance mode flag
			 */
			colName = "hmStatus";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			short hmStatus = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : HMServicesSettings.HM_OLINE_STATUS_NORMAL;
			bo.setHmStatus(hmStatus);

			/**
			 * Set virtual host name
			 */
			colName = "virtualHostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String virtualHostName = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : null;
			bo.setVirtualHostName(virtualHostName);

			/**
			 * Set enableTeacher
			 */
			colName = "enableTeacher";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableTeacher = isColPresent && AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName));
			bo.setEnableTeacher(enableTeacher);

			/**
			 * Set presenceEnable
			 */
			colName = "presenceEnable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean presenceEnable = isColPresent && AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName));
			bo.setPresenceEnable(presenceEnable);

			/**
			 * Set apSlaType
			 */
			colName = "apSlaType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int apSlaType = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.AP_SLA_STATS_ALL;
			bo.setApSlaType(apSlaType);

			/**
			 * Set clientSlaType
			 */
			colName = "clientSlaType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int clientSlaType = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.CLIENT_SLA_STATS_ALL;
			bo.setClientSlaType(clientSlaType);

			/**
			 * Set enableProxy
			 */
			colName = "enableProxy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableProxy = isColPresent && AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName));
			bo.setEnableProxy(enableProxy);

			/**
			 * Set proxyServer
			 */
			colName = "proxyServer";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String proxyServer = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setProxyServer(proxyServer);

			/**
			 * Set proxyPort
			 */
			colName = "proxyPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int proxyPort = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : 0;
			bo.setProxyPort(proxyPort);

			/**
			 * Set proxyUserName
			 */
			colName = "proxyUserName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String proxyUserName = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setProxyUserName(proxyUserName);

			/**
			 * Set proxyPassword
			 */
			colName = "proxyPassword";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String proxyPassword = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setProxyPassword(proxyPassword);

			/**
			 * Set snpMaximum
			 */
			colName = "snpMaximum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int snpMaximum = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : 20;
			bo.setSnpMaximum(snpMaximum);

			/**
			 * Set maxUpdateNum
			 */
			colName = "maxUpdateNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int maxUpdateNum = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.MAX_HIVEOS_SOFTVER_UPDATE_NUM;
			bo.setMaxUpdateNum(maxUpdateNum);

			/**
			 * Set concurrentConfigGenNum
			 */
			colName = "concurrentConfigGenNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String concurrentConfigGenNum = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) :"3";
			bo.setConcurrentConfigGenNum(Byte.valueOf(concurrentConfigGenNum));

			/**
			 * Set concurrentConfigGenNum
			 */
			colName = "concurrentSearchUserNum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String concurrentSearchUserNum = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) :"1";
			bo.setConcurrentSearchUserNum(Byte.valueOf(concurrentSearchUserNum));

			/**
			 * Set tvProxyIP
			 */
			colName = "tvProxyIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String tvProxyIP = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setTvProxyIP(tvProxyIP);

			/**
			 * Set tvProxyIP
			 */
			colName = "tvProxyPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int tvProxyPort = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) :0;
			bo.setTvProxyPort(tvProxyPort);

			/**
			 * Set tvAutoProxyFile
			 */
			colName = "tvAutoProxyFile";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String tvAutoProxyFile = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setTvAutoProxyFile(tvAutoProxyFile);
			/**
			 * Set enableTVProxy
			 */
			colName = "enableTVProxy";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableTVProxy = isColPresent && AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName));
			bo.setEnableTVProxy(enableTVProxy);

			/**
			 * Set enableCaseSensitive
			 */
			colName = "enableCaseSensitive";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);

			int enableCaseSensitive = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 1;
			bo.setEnableCaseSensitive((short)enableCaseSensitive);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;

			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'clientmonitorsettings' data be lost, cause: 'owner' column is not available.");
				continue;
			}

			bo.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if (null == bo.getOwner())
				continue;

			// web security
			// Barracuda
			/**
			 * Set authorizationKey
			 */
			colName = "authorizationKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String authorizationKey =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setAuthorizationKey(authorizationKey);

			/**
			 * Set serviceHost
			 */
			colName = "serviceHost";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String serviceHost =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setServiceHost(serviceHost);

			/**
			 * Set servicePort
			 */
			colName = "servicePort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int servicePort = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.SERVICEPROT_DEFAULT_VALUE;
			bo.setServicePort(servicePort);

			/**
			 * Set windowsDomain
			 */
			colName = "windowsDomain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String windowsDomain =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setWindowsDomain(windowsDomain);

			/**
			 * Set barracudaDefaultUserName
			 */
			colName = "barracudaDefaultUserName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String barracudaDefaultUserName =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setBarracudaDefaultUserName(barracudaDefaultUserName);

			/**
			 * Set enableBarracuda
			 */
			colName = "enableBarracuda";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableBarracuda =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName)) : false;
			bo.setEnableBarracuda(enableBarracuda);


			// WebSense
			/**
			 * Set accountID
			 */
			colName = "accountID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String accountID =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setAccountID(accountID);

			/**
			 * Set securityKey
			 */
			colName = "securityKey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String securityKey =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setSecurityKey(securityKey);

			/**
			 * Set webSenseServiceHost
			 */
			colName = "webSenseServiceHost";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String webSenseServiceHost =  isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : NmsUtil.getText("resources.hmResources","admin.management.webSecurity.websense.serviceHost.hosted");
			bo.setWebSenseServiceHost(webSenseServiceHost);

			/**
			 * Set port
			 */
			colName = "port";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int port = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.PORT_DEFAULT_VALUE;
			bo.setPort(port);

			/**
			 * Set wensenseMode
			 */
			colName = "wensenseMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			int wensenseMode = isColPresent ? AhRestoreCommons.convertInt(
					xmlParser.getColVal(i, colName)) : HMServicesSettings.WEBSENSEMODE_HOSTED;
			bo.setWensenseMode((short)wensenseMode);

			/**
			 * Set defaultDomain
			 */
			colName = "defaultDomain";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String defaultDomain = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setDefaultDomain(defaultDomain);


			/**
			 * Set websense default user name
			 */
			colName = "webSenseDefaultUserName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String webSenseDefaultUserName = isColPresent ? AhRestoreCommons.convertString(
					xmlParser.getColVal(i, colName)) : "";
			bo.setWebSenseDefaultUserName(webSenseDefaultUserName);

			/**
			 * Set enableWebsense
			 */
			colName = "enableWebsense";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			boolean enableWebsense =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
					xmlParser.getColVal(i, colName)) : false;
			bo.setEnableWebsense(enableWebsense);

			/**
			 * set BARRACUDAWHITELIST_ID
			 */
			colName = "BARRACUDAWHITELIST_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String barracudawhitelist_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (barracudawhitelist_id != null
					&& !(barracudawhitelist_id.trim().equals(""))
					&& !(barracudawhitelist_id.trim().equalsIgnoreCase("null"))) {
				DomainObject domainObj = AhRestoreNewMapTools
						.getMapDomainObject(AhRestoreCommons
								.convertLong(barracudawhitelist_id));
				if (domainObj != null) {
					try {
						domainObj = QueryUtil.findBoById(DomainObject.class, domainObj.getId(), new DomainObjectAction());

				    	if(domainObj.getObjType() != DomainObject.WEB_SECURITY){
				    		domainObj.setId(null);
				    		domainObj.setObjType(DomainObject.WEB_SECURITY);
				    		if(domainObj.getItems() != null){
				    			List<DomainNameItem> items = new ArrayList<DomainNameItem>();
								items.addAll(domainObj.getItems());
								domainObj.setItems(items);
					    	}
				    		QueryUtil.createBo(domainObj);
				    	}
						//QueryUtil.updateBo(domainObj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				bo.setBarracudaWhitelist(domainObj);
				if (null == domainObj) {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find BARRACUDAWHITELIST_ID:"
									+ barracudawhitelist_id);
				}
			}

			/**
			 * set WEBSENSEWHITELIST_ID
			 */
			colName = "WEBSENSEWHITELIST_ID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String websenseWhitelist_id = isColPresent ? xmlParser.getColVal(i,
					colName) : null;
			if (websenseWhitelist_id != null
					&& !(websenseWhitelist_id.trim().equals(""))
					&& !(websenseWhitelist_id.trim().equalsIgnoreCase("null"))) {
				DomainObject domainObj = AhRestoreNewMapTools
						.getMapDomainObject(AhRestoreCommons
								.convertLong(websenseWhitelist_id));
				if (domainObj != null) {
					try {
						domainObj = QueryUtil.findBoById(DomainObject.class, domainObj.getId(), new DomainObjectAction());

				    	if(domainObj.getObjType() != DomainObject.WEB_SECURITY){
				    		domainObj.setId(null);
				    		domainObj.setObjType(DomainObject.WEB_SECURITY);
				    		if(domainObj.getItems() != null){
				    			List<DomainNameItem> items = new ArrayList<DomainNameItem>();
								items.addAll(domainObj.getItems());
								domainObj.setItems(items);
					    	}
				    		QueryUtil.createBo(domainObj);
				    	}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				bo.setWebsenseWhitelist(domainObj);
				if (null == domainObj) {
					AhRestoreDBTools
							.logRestoreMsg("Cound not find WEBSENSEWHITELIST_ID:"
									+ websenseWhitelist_id);
				}
			}

			/**
			 * Set enableDistributedUpgrade
			 */
			colName = "enableDistributedUpgrade";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			if (isColPresent) {
				boolean enableDistributedUpgrade = AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i, colName));
				if (enableDistributedUpgrade) {
					// update to HiveApUpdateSettings bo
					HiveApUpdateSettings updateSetting = QueryUtil.findBoByAttribute(HiveApUpdateSettings.class, "owner", AhRestoreNewMapTools.getHmDomain(ownerId));
					if (updateSetting != null) {
						updateSetting.setDistributedUpgrades(true);
						QueryUtil.updateBo(updateSetting);
					}
				}
			}

            /**
            * Set enabledBetaIDM
            */
           colName = "enabledBetaIDM";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           boolean enabledBetaIDM =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
                   xmlParser.getColVal(i, colName)) : false;
           bo.setEnabledBetaIDM(enabledBetaIDM);

           /**
            * Set enableRadarDetection
            */
           colName = "enableRadarDetection";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           boolean enableRadarDetection =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
                   xmlParser.getColVal(i, colName)) : false;
           bo.setEnableRadarDetection(enableRadarDetection);

           /**
            * Set enableCollectAppData
            */
           colName = "enableCollectAppData";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           boolean enableCollectAppData =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
                   xmlParser.getColVal(i, colName)) : false;
           bo.setEnableCollectAppData(enableCollectAppData);

           /**
            * Set classifier tag setting
            */
           colName = "classifiertag";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           String classifiertag =  isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
           bo.setClassifierTag(classifiertag);

           /**
            * set date format
            */
           colName = "timeType";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           short timeType =  (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : HMServicesSettings.TIME_TYPE_1);
           bo.setTimeType(timeType);

           colName = "dateFormat";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           short dateFormat = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : HMServicesSettings.DATE_FORMAT_TYPE_1);
           bo.setDateFormat(dateFormat);

           colName = "timeFormat";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           short timeFormat = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : HMServicesSettings.TIME_FORMAT_TYPE_1);
           bo.setTimeFormat(timeFormat);

           colName = "dateSeparator";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           short dateSeparator = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : HMServicesSettings.DATE_SEPARATOR_TYPE_1);
           bo.setDateSeparator(dateSeparator);
           
           colName = "apiUserName";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           String apiUserName =  isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
           bo.setApiUserName(apiUserName);
           
           colName = "apiPassword";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           String apiPassword =  isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
           bo.setApiPassword(apiPassword);
           
           colName = "enableApiAccess";
           isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
           boolean enableApiAccess =  isColPresent ? AhRestoreCommons.convertStringToBoolean(
        		   xmlParser.getColVal(i, colName)): false;
           bo.setEnableApiAccess(enableApiAccess);
           
            //For OpenDNS Feature
			/**
			 * Set enableOpenDNS
			 */
			colName = "enableOpenDNS";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			boolean enableOpenDNS = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			bo.setEnableOpenDNS(enableOpenDNS);
			
			/**
			 * Set openDNSAccount
			 */
            colName = "opendns_account_id";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
            String opendns_account_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
            if (NmsUtil.isNotBlankId(opendns_account_id)) {
                Long newOpendnsAccountId = AhRestoreNewMapTools.getMapOpenDNSAccount(Long.parseLong(opendns_account_id.trim()));
                OpenDNSAccount openDNSAccount = AhRestoreNewTools.CreateBoWithId(OpenDNSAccount.class, newOpendnsAccountId);	             
                bo.setOpenDNSAccount(openDNSAccount);	
            }
           /**
            * Set enableSupplementalCLI
            */
            colName = "enableSupplementalCLI";
            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
            boolean enableSupplementalCLI = isColPresent ? AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName))
					: false;
			bo.setEnableSupplementalCLI(enableSupplementalCLI);
            
        
           settings.add(bo);

		}

		return !settings.isEmpty() ? settings : null;
	}

	// ---------------------- HMServicesSettings -------------END---------
	// ---------------------- OpenDNS - OpenDNSMapping -------------START---------
	public static boolean restoreOpenDNSMappings()
	{
		try {
			List<OpenDNSMapping> allMappings = getOpenDNSMappings();
			if (null != allMappings) {
				QueryUtil.restoreBulkCreateBos(allMappings);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static List<OpenDNSMapping> getOpenDNSMappings() throws AhRestoreColNotExistException, AhRestoreException {

			AhRestoreGetXML xmlParser = new AhRestoreGetXML();
			
			String tableName = "opendns_mapping";

			/**
			 * Check validation of opendns_mapping.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile(tableName);
			if (!restoreRet) {
				return null;
			}

			/**
			 * No one row data stored in opendns_mapping table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			List<OpenDNSMapping> openDNSMappings = new ArrayList<OpenDNSMapping>();

			boolean isColPresent;
			String colName;
			OpenDNSMapping openDNSMapping;

			for (int i = 0; i < rowCount; i++) {
				openDNSMapping = new OpenDNSMapping();
	
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "";
	
				if ("".equals(id)) {
					continue;
				}
				openDNSMapping.setId(AhRestoreCommons.convertString2Long(id));
	
				/*
				 * set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;
				if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					continue;
				}
				HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
				openDNSMapping.setOwner(ownerDomain);
				
				/**
				 * Set openDNSAccount
				 */
	            colName = "opendns_account_id";
	            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
	            String opendns_account_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
	            if (NmsUtil.isNotBlankId(opendns_account_id)) {
	                Long newOpendnsAccountId = AhRestoreNewMapTools.getMapOpenDNSAccount(Long.parseLong(opendns_account_id.trim()));
	                OpenDNSAccount openDNSAccount = AhRestoreNewTools.CreateBoWithId(OpenDNSAccount.class, newOpendnsAccountId);	             
	                openDNSMapping.setOpenDNSAccount(openDNSAccount);	
	            }
	            
				/**
				 * Set openDNSDevices
				 */
	            colName = "opendns_device_id";
	            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
	            String opendns_device_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
	            if (NmsUtil.isNotBlankId(opendns_device_id)) {
	                Long new_opendns_device_id = AhRestoreNewMapTools.getMapOpenDNSDevice(Long.parseLong(opendns_device_id.trim()));
	                OpenDNSDevice openDNSDevice = AhRestoreNewTools.CreateBoWithId(OpenDNSDevice.class, new_opendns_device_id);	             
	                openDNSMapping.setOpenDNSDevice(openDNSDevice);
	            }
	            
	            
				/**
				 * Set UserProfiles
				 */
	            colName = "user_profile_id";
	            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
	            String userprofile_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
	            if (NmsUtil.isNotBlankId(userprofile_id)) {
	                Long new_userprofile_id = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(userprofile_id.trim()));	                
	                openDNSMapping.setUserProfileId(new_userprofile_id);
	            }
	
	            openDNSMappings.add(openDNSMapping);
			}

			return openDNSMappings.size() > 0 ? openDNSMappings : null;

		}

	// ---------------------- OpenDNS - OpenDNSMapping------------END---------
	
	// ---------------------- OpenDNS - OpenDNSDevice -------------START---------
	public static boolean restoreOpenDNSDevices()
	{
		try {
			List<OpenDNSDevice> allDevices = getOpenDNSDevices();
			if (null != allDevices) {
				List<Long> lOldId = new ArrayList<Long>();

				for (OpenDNSDevice openDNSDevice : allDevices) {
					lOldId.add(openDNSDevice.getId());
				}

				QueryUtil.restoreBulkCreateBos(allDevices);

				for(int i=0; i<allDevices.size(); i++)
				{
					AhRestoreNewMapTools.setMapOpenDNSDevice(lOldId.get(i), allDevices.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static List<OpenDNSDevice> getOpenDNSDevices() throws AhRestoreColNotExistException, AhRestoreException {

			AhRestoreGetXML xmlParser = new AhRestoreGetXML();
			
			String tableName = "opendns_device";

			/**
			 * Check validation of opendns_device.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile(tableName);
			if (!restoreRet) {
				return null;
			}

			/**
			 * No one row data stored in opendns_device table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			List<OpenDNSDevice> openDNSDevices = new ArrayList<OpenDNSDevice>();

			boolean isColPresent;
			String colName;
			OpenDNSDevice openDNSDevice;

			for (int i = 0; i < rowCount; i++) {
				openDNSDevice = new OpenDNSDevice();
	
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "";
	
				if ("".equals(id)) {
					continue;
				}
				openDNSDevice.setId(AhRestoreCommons.convertString2Long(id));
	
				/*
				 * set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;
				if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					continue;
				}
				HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
				openDNSDevice.setOwner(ownerDomain);
	
				/**
				 * Set deviceLabel
				 */
				colName = "deviceLabel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
				String deviceLabel = isColPresent ? xmlParser.getColVal(i, colName): null;
				openDNSDevice.setDeviceLabel(deviceLabel);
				
				/**
				 * Set deviceId
				 */
				colName = "deviceId";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
				String deviceId = isColPresent ? xmlParser.getColVal(i, colName): null;
				openDNSDevice.setDeviceId(deviceId);
				
				/**
				 * Set deviceKey
				 */
				colName = "deviceKey";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);
				String deviceKey = isColPresent ? xmlParser.getColVal(i, colName): null;
				openDNSDevice.setDeviceKey(deviceKey);
				
				/**
				 * Set defaultDevice
				 */
				colName = "defaultDevice";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,tableName, colName);				
				String defaultDevice = isColPresent ? xmlParser.getColVal(i, colName) : "false";
				openDNSDevice.setDefaultDevice(AhRestoreCommons.convertStringToBoolean(defaultDevice));
				
				/**
				 * Set openDNSAccount
				 */
	            colName = "opendns_account_id";
	            isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
	            String opendns_account_id = isColPresent ? xmlParser.getColVal(i, colName) : "";
	            if (NmsUtil.isNotBlankId(opendns_account_id)) {
	                Long newOpendnsAccountId = AhRestoreNewMapTools.getMapOpenDNSAccount(Long.parseLong(opendns_account_id.trim()));
	                OpenDNSAccount openDNSAccount = AhRestoreNewTools.CreateBoWithId(OpenDNSAccount.class, newOpendnsAccountId);	             
	                openDNSDevice.setOpenDNSAccount(openDNSAccount);	
	            }
	
				openDNSDevices.add(openDNSDevice);
			}

			return openDNSDevices.size() > 0 ? openDNSDevices : null;

		}

	// ---------------------- OpenDNS - OpenDNSDevice-------------END---------
	
	// ---------------------- OpenDNS - OpenDNSAccount-------------START---------
	
	public static boolean restoreOpenDNSAccounts()
	{
		try {
			List<OpenDNSAccount> allAccounts = getOpenDNSAccounts();
			if (null != allAccounts) {
				List<Long> lOldId = new ArrayList<Long>();

				for (OpenDNSAccount openDNSAccount : allAccounts) {
					lOldId.add(openDNSAccount.getId());
				}

				QueryUtil.restoreBulkCreateBos(allAccounts);

				for(int i=0; i<allAccounts.size(); i++)
				{
					AhRestoreNewMapTools.setMapOpenDNSAccount(lOldId.get(i), allAccounts.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	private static List<OpenDNSAccount> getOpenDNSAccounts() throws AhRestoreColNotExistException, AhRestoreException {

			AhRestoreGetXML xmlParser = new AhRestoreGetXML();
			
			String tableName = "opendns_account";

			/**
			 * Check validation of opendns_account.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile(tableName);
			if (!restoreRet) {
				return null;
			}

			/**
			 * No one row data stored in opendns_account table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			List<OpenDNSAccount> openDNSAccounts = new ArrayList<OpenDNSAccount>();

			boolean isColPresent;
			String colName;
			OpenDNSAccount openDNSAccount;

			for (int i = 0; i < rowCount; i++) {
				openDNSAccount = new OpenDNSAccount();
	
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "";
	
				if ("".equals(id)) {
					continue;
				}
				openDNSAccount.setId(AhRestoreCommons.convertString2Long(id));
	
				/*
				 * set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;
				if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
					continue;
				}
				HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
				openDNSAccount.setOwner(ownerDomain);
	
				/**
				 * Set userName
				 */
				colName = "userName";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String userName = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				openDNSAccount.setUserName(userName);

				/**
				 * Set password
				 */
				colName = "password";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String password = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				openDNSAccount.setPassword(password);

				/**
				 * Set token
				 */
				colName = "token";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String token = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				openDNSAccount.setToken(token);

				/**
				 * Set dnsServer1
				 */
				colName = "dnsServer1";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String dnsServer1 = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				openDNSAccount.setDnsServer1(dnsServer1);
				
				/**
				 * Set dnsServer2
				 */
				colName = "dnsServer2";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
				String dnsServer2 = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				openDNSAccount.setDnsServer2(dnsServer2);
	
				openDNSAccounts.add(openDNSAccount);
			}

			return openDNSAccounts.size() > 0 ? openDNSAccounts : null;

		}	
	
	// ---------------------- OpenDNS - OpenDNSAccount -------------END---------

	// ---------------------- HM_LOGIN_AUTHENTICATION-------------START---------

	public static boolean restoreLoginAuth() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return true;
		// }

		try {
			HmLoginAuthentication loginAuth = getLoginAuth();
			if (null != loginAuth) {
				List<HmLoginAuthentication> list = QueryUtil.executeQuery(HmLoginAuthentication.class, null, null);

				if (list.isEmpty()) {
					QueryUtil.createBo(loginAuth);
				} else {
					HmLoginAuthentication destBo = list.get(0);
					destBo.setAuthType(loginAuth.getAuthType());
					destBo.setHmAdminAuth(loginAuth.getHmAdminAuth());
					destBo.setRadiusAssignment(loginAuth.getRadiusAssignment());

					QueryUtil.updateBo(destBo);
				}
			}

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreLoginAuth() catch exception ", e);
			return false;
		}
	}

	/**
	 * get login auth records
	 *
	 * @return -
	 * @throws Exception
	 *             -
	 */
	private static HmLoginAuthentication getLoginAuth() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_login_authentication.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_login_authentication");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		if (rowCount == 0) {
			return null;
		}

		if (rowCount > 1) {
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Mutiple rows records in hm_login_authentication, retore head row");
		}

		boolean isColPresent;
		String colName;
		HmLoginAuthentication loginAuth = new HmLoginAuthentication();

		/**
		 * Set owner
		 */
		colName = "owner";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_login_authentication", colName);
		long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(0, colName)) : 1;
		if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
		{
		   return null;
		}
		HmDomain owner = AhRestoreNewMapTools.getHmDomain(ownerId);
		if (null == owner || !owner.isHomeDomain())
			return null;
		loginAuth.setOwner(owner);

		/**
		 * Set hmAdminAuth
		 */
		colName = "hmAdminAuth";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_login_authentication",
				colName);
		int hmAdminAuth = isColPresent ? AhRestoreCommons.convertInt(xmlParser
				.getColVal(0, colName)) : EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;
		loginAuth.setHmAdminAuth((short) hmAdminAuth);

		/**
		 * Set authType
		 */
		colName = "authType";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_login_authentication",
				colName);
		int authType = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(0, colName))
				: Cwp.AUTH_METHOD_PAP;
		loginAuth.setAuthType(authType);

		/**
		 * Set radius_service_assign_id
		 */
		colName = "radius_service_assign_id";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_login_authentication",
				colName);
		String radius_service_assign_id = isColPresent ? xmlParser.getColVal(0, colName) : "";
		if (!radius_service_assign_id.equals("")
				&& !radius_service_assign_id.trim().equalsIgnoreCase("null")) {
			Long newID = AhRestoreNewMapTools.getMapRadiusServerAssign(Long.valueOf(radius_service_assign_id));
			if (newID != null) {
				loginAuth.setRadiusAssignment(AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class, newID));
			}
		}
		return loginAuth;
	}

	// ---------------------- HM_LOGIN_AUTHENTICATION -------------END---------

	// ---------------------- NTP Server and interval
	// -------------START---------

	public static boolean restoreNTPInfo() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return true;
		// }

		try {
			HmNtpServerAndInterval info = getNtpServerAndInterval();
			if (null != info) {
				QueryUtil.createBo(info);
			}

			return true;
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreNTPInfo() catch exception ", e);
			return false;
		}
	}

	/**
	 * Get all information from hm_ntp_server_interval table
	 *
	 * @return HmNtpServerAndInterval
	 * @throws AhRestoreException
	 *             - if error in parsing hm_ntp_server_interval.xml.
	 */
	private static HmNtpServerAndInterval getNtpServerAndInterval() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		HmNtpServerAndInterval ntpInfo = null;

		/**
		 * Check validation of hm_ntp_server_interval.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_ntp_server_interval");
		FileManager fileMg = FileManager.getInstance();

		// read the ntp server file
		String[] server = fileMg.readFile(LinuxServerTimeManager.SUPPER_NTP_SERVERS_FILE);

		if (server.length > 0) {
			fileMg
					.writeFile(LinuxServerTimeManager.SUPPER_NTP_SERVERS_FILE, new String[] {},
							false);
		}

		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_ntp_server_interval table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;
		for (int i = 0; i < rowCount; i++) {
			ntpInfo = new HmNtpServerAndInterval();

			/**
			 * Set ntpserver
			 */
			colName = "ntpserver";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_ntp_server_interval",
					colName);
			String ntpserver = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			ntpInfo.setNtpServer(ntpserver);

			/**
			 * Set ntpinterval
			 */
			colName = "ntpinterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_ntp_server_interval",
					colName);
			int ntpinterval = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 1440;
			ntpInfo.setNtpInterval(ntpinterval);

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_ntp_server_interval",
					colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
			   continue;
			}
			ntpInfo.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
		}

		return ntpInfo;
	}

	/**
	 * Get all information from HmExpressModeEnable table
	 *
	 * @return List<HmExpressModeEnable> all HmDomain BO
	 * @throws Exception
	 *             -
	 */
	private static List<HmExpressModeEnable> getAllHmExpressModeEnable() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of HM_EXPRESSMODE_ENABLE.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_expressmode_enable");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_domain table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		List<HmExpressModeEnable> list = new ArrayList<HmExpressModeEnable>();

		for (int i = 0; i < rowCount; i++) {
			HmExpressModeEnable hmExpressModeEnable = new HmExpressModeEnable();

			/**
			 * Set expressModeEnable
			 */
			String colName = "expressModeEnable";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_expressmode_enable",
					colName);
			boolean expressModeEnable = isColPresent ?
					AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,colName)) :
						NmsUtil.getOEMCustomer().getExpressModeEnable();
			hmExpressModeEnable.setExpressModeEnable(expressModeEnable);

			list.add(hmExpressModeEnable);
		}

		return !list.isEmpty() ? list : null;
	}

	public static void restoreHmExpressModeEnable() {
		try {
			List<HmExpressModeEnable> lstSettings = getAllHmExpressModeEnable();
			if (null == lstSettings || lstSettings.isEmpty()) {
				return;
			}

			HmExpressModeEnable srcBo = lstSettings.get(0);

			List<HmExpressModeEnable> list = QueryUtil.executeQuery(HmExpressModeEnable.class, null, null);

			if (list.isEmpty()) {
				QueryUtil.createBo(srcBo);
			} else {
				HmExpressModeEnable destBo = list.get(0);
				destBo.setExpressModeEnable(srcBo.isExpressModeEnable());
				QueryUtil.updateBo(destBo);
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreHmExpressModeEnable() catch exception ", e);
		}
	}


	// ---------------------- NTP Server and interval -------------END---------

	// ---------------------- HA Settings -------------START---------

	public static void restoreHASettings() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return;
		// }

		try {
			List<HASettings> haSettings = getAllHASettings();
			if (null == haSettings || haSettings.isEmpty()) {
				return;
			}

			HASettings srcBo = haSettings.get(0);

			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);

			if (list.isEmpty()) {
				QueryUtil.createBo(srcBo);
			} else {
				HASettings destBo = list.get(0);

				srcBo.setId(destBo.getId());
				srcBo.setVersion(destBo.getVersion());

				QueryUtil.updateBo(srcBo);
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreHASettings() catch exception ", e);
		}
	}

	/**
	 * Get all information from HA_SETTINGS table
	 *
	 * @return List<HASettings> all HmDomain BO
	 * @throws Exception
	 *             -
	 */
	private static List<HASettings> getAllHASettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of HA_SETTINGS.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("ha_settings");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in hm_domain table is allowed
		 */
		int rowCount = xmlParser.getRowCount();

		List<HASettings> list = new ArrayList<HASettings>();

		for (int i = 0; i < rowCount; i++) {
			HASettings haSetting = new HASettings();

			/**
			 * Set haStatus
			 */
			String colName = "haStatus";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings",
					colName);
			int haStatus = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			haSetting.setHaStatus((byte) haStatus);

			/**
			 * Set enableFailBack
			 */
			colName = "enableFailBack";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			boolean enableFailBack = isColPresent && AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName));
			haSetting.setEnableFailBack(enableFailBack);

			/**
			 * Set primaryHostName
			 */
			colName = "primaryHostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryHostName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryHostName(AhRestoreCommons.convertString(primaryHostName));

			/**
			 * Set secondaryHostName
			 */
			colName = "secondaryHostName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryHostName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryHostName(AhRestoreCommons.convertString(secondaryHostName));

			/**
			 * Set domainName
			 */
			colName = "domainName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String domainName = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setDomainName(AhRestoreCommons.convertString(domainName));

			/**
			 * Set primaryMGTIP
			 */
			colName = "primaryMGTIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryMGTIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryMGTIP(AhRestoreCommons.convertString(primaryMGTIP));

			/**
			 * Set primaryMGTNetmask
			 */
			colName = "primaryMGTNetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryMGTNetmask = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryMGTNetmask(AhRestoreCommons.convertString(primaryMGTNetmask));

			/**
			 * Set secondaryMGTIP
			 */
			colName = "secondaryMGTIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryMGTIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryMGTIP(AhRestoreCommons.convertString(secondaryMGTIP));

			/**
			 * Set secondaryMGTNetmask
			 */
			colName = "secondaryMGTNetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryMGTNetmask = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryMGTNetmask(AhRestoreCommons.convertString(secondaryMGTNetmask));

			/**
			 * Set primaryLANIP
			 */
			colName = "primaryLANIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryLANIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryLANIP(AhRestoreCommons.convertString(primaryLANIP));

			/**
			 * Set primaryLANNetmask
			 */
			colName = "primaryLANNetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryLANNetmask = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryLANNetmask(AhRestoreCommons.convertString(primaryLANNetmask));

			/**
			 * Set secondaryLANIP
			 */
			colName = "secondaryLANIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryLANIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryLANIP(AhRestoreCommons.convertString(secondaryLANIP));

			/**
			 * Set secondaryLANNetmask
			 */
			colName = "secondaryLANNetmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryLANNetmask = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryLANNetmask(AhRestoreCommons.convertString(secondaryLANNetmask));

			/**
			 * Set primaryDefaultGateway
			 */
			colName = "primaryDefaultGateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryDefaultGateway = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimaryDefaultGateway(AhRestoreCommons
					.convertString(primaryDefaultGateway));

			/**
			 * Set secondaryDefaultGateway
			 */
			colName = "secondaryDefaultGateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryDefaultGateway = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondaryDefaultGateway(AhRestoreCommons
					.convertString(secondaryDefaultGateway));

			/**
			 * Set haSecret
			 */
			colName = "haSecret";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String haSecret = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setHaSecret(AhRestoreCommons.convertString(haSecret));

			/**
			 * Set masterHostNameSticky
			 */
			colName = "masterHostNameSticky";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String masterHostNameSticky = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setMasterHostNameSticky(AhRestoreCommons.convertString(masterHostNameSticky));

			/**
			 * Set primarySystemId
			 */
			colName = "primarySystemId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primarySystemId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setPrimarySystemId(AhRestoreCommons.convertString(primarySystemId));

			/**
			 * Set secondarySystemId
			 */
			colName = "secondarySystemId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondarySystemId = isColPresent ? xmlParser.getColVal(i, colName) : "";
			haSetting.setSecondarySystemId(AhRestoreCommons.convertString(secondarySystemId));

			/**
			 * Set haPort
			 */
			colName = "haPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings",
					colName);
			int haPort = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : HASettings.HAPORT_LAN;
			haSetting.setHaPort((byte)haPort);

			/**
			 * Set useExternalIPHostname
			 */
			colName = "useExternalIPHostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			boolean useExternalIPHostname = isColPresent && AhRestoreCommons
					.convertStringToBoolean(xmlParser.getColVal(i, colName));
			haSetting.setUseExternalIPHostname(useExternalIPHostname);

			/**
			 * Set primaryExternalIPHostname
			 */
			colName = "primaryExternalIPHostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryExternalIPHostname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setPrimaryExternalIPHostname(primaryExternalIPHostname);

			/**
			 * Set secondaryExternalIPHostname
			 */
			colName = "secondaryExternalIPHostname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryExternalIPHostname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setSecondaryExternalIPHostname(secondaryExternalIPHostname);

			/**
			 * Set haNotifyEmail
			 */
			colName = "haNotifyEmail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String haNotifyEmail = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : HASettings.DEFAULT_HA_NOTIFY_EMAIL;
			haSetting.setHaNotifyEmail(haNotifyEmail);

			/**
			 * Set primaryUpTime
			 */
			colName = "primaryUpTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			long primaryUpTime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0l;
			haSetting.setPrimaryUpTime(primaryUpTime);

			/**
			 * Set secondaryUpTime
			 */
			colName = "secondaryUpTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			long secondaryUpTime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0l;
			haSetting.setSecondaryUpTime(secondaryUpTime);

			/**
			 * Set lastSwitchOverTime
			 */
			colName = "lastSwitchOverTime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			long lastSwitchOverTime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0l;
			haSetting.setLastSwitchOverTime(lastSwitchOverTime);

			/**
			 * Set heartbeatTimeOutValue
			 */
			colName = "heartbeatTimeOutValue";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			int heartbeatTimeOutValue = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName)) : 60;
			haSetting.setHeartbeatTimeOutValue(heartbeatTimeOutValue);

			/**
			 * Set primaryDbUrl
			 */
			colName = "primaryDbUrl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryDbUrl = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setPrimaryDbUrl(primaryDbUrl);
			if (primaryDbUrl.trim().length() == 0) {
				updatePrimaryDbUrl(haSetting);
			}

			/**
			 * Set secondaryDbUrl
			 */
			colName = "secondaryDbUrl";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryDbUrl = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setSecondaryDbUrl(secondaryDbUrl);

			/**
			 * Set primaryDbPwd
			 */
			colName = "primaryDbPwd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String primaryDbPwd = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setPrimaryDbPwd(primaryDbPwd);

			/**
			 * Set secondaryDbPwd
			 */
			colName = "secondaryDbPwd";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			String secondaryDbPwd = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			haSetting.setSecondaryDbPwd(secondaryDbPwd);

			/**
			 * Set enableExternalDb
			 */
			colName = "enableExternalDb";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "ha_settings", colName);
			int enableExternalDb = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : HASettings.EXTERNALDB_DISABLEHA_INITIAL;
			haSetting.setEnableExternalDb((byte)enableExternalDb);
			updateEnableExternalDb(haSetting, (byte)enableExternalDb);

			list.add(haSetting);
		}

		return !list.isEmpty() ? list : null;
	}

	public static void updateEnableExternalDb(HASettings haSetting, byte oldEnableExternalDb) {
		if (oldEnableExternalDb == HASettings.EXTERNALDB_ENABLEHA_REMOTE)
			return;
		try {
			File dbOnly = new File("/HiveManager/tomcat/.dbOnly");
			if (dbOnly.exists() && dbOnly.isFile()) {
				AhRestoreDBTools.logRestoreMsg("db only: set enableExternalDb = 1");
				haSetting.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_REMOTE);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreErrorMsg("update enableExternalDb info failed. " + e.getMessage());
		}
	}

	private static void updatePrimaryDbUrl(HASettings haSetting) {
		try {
			File dbOnly = new File("/HiveManager/tomcat/.dbOnly");
			if (dbOnly.exists() && dbOnly.isFile()) {
				AhRestoreDBTools.logRestoreMsg("db only: set primaryDbUrl = eth0 ip");
				haSetting.setPrimaryDbUrl(getEth0Ip());
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreErrorMsg("update primaryDbUrl info failed. " + e.getMessage());
		}
	}

	private static String getEth0Ip() throws SocketException, UnknownHostException {
		String eth0Ip = null;

		overloop:
		for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements();) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			String networkInterfaceName = networkInterface.getName();

			if ("eth0".equalsIgnoreCase(networkInterfaceName)) {
				Enumeration<InetAddress> eth0Addresses = networkInterface.getInetAddresses();

				while (eth0Addresses.hasMoreElements()) {
					InetAddress eth0Address = eth0Addresses.nextElement();

					if (eth0Address instanceof Inet4Address) {
						eth0Ip = eth0Address.getHostAddress();
						break overloop;
					}
				}
			}
		}

		if (eth0Ip == null) {
			InetAddress localAddress = InetAddress.getLocalHost();

			if (localAddress instanceof Inet4Address) {
				eth0Ip = localAddress.getHostAddress();
			}
		}

		return eth0Ip;
	}
	// ---------------------- HA Settings -------------END---------

	// HM Access control
	public static boolean restoreHmAcl() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return true;
		// }

		try {
			long start = System.currentTimeMillis();
			List<HmAccessControl> list = getHmAcl();
			if (null != list && !list.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(list);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools.logRestoreMsg("Restore HM Acl, count:"
					+ (list == null ? 0 : list.size()) + ". cost:" + (end - start) + " ms.");
			return true;
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore HM Acl.", e);
			return false;
		}
	}

	private static List<HmAccessControl> getHmAcl() throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_access_control.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_access_control");
		if (!restoreRet) {
			AhRestoreDBTools.logRestoreMsg("SAXReader cannot read hm_access_control.xml file.");
			return null;
		}

		Map<String, List<String>> ipaddresses = null;
		try {
			ipaddresses = getHmAclItems();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg("get HM Acl ipaddresses error", e1);
		}

		/**
		 * No one row data stored in hm_access_control table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HmAccessControl> configs = new ArrayList<HmAccessControl>();

		boolean isColPresent;
		String colName;
		HmAccessControl acl;
		for (int i = 0; i < rowCount; i++) {
			try {
				acl = new HmAccessControl();

				/**
				 * Set ID
				 */
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control",
						colName);
				if (!isColPresent) {
					/**
					 * The id column must be exist in the table of auto_provisioning_config
					 */
					continue;
				}
				String id = xmlParser.getColVal(i, colName);
				if (id == null || id.trim().equals("") || id.trim().equalsIgnoreCase("null")) {
					continue;
				}

				/**
				 * Set controltype
				 */
				colName = "controltype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control",
						colName);
				String controltype = isColPresent ? xmlParser.getColVal(i, colName) : String
						.valueOf(HmAccessControl.CONTROL_TYPE_DENY);
				acl.setControlType((short) AhRestoreCommons.convertInt(controltype));

				/**
				 * Set denybehavior
				 */
				colName = "denybehavior";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control",
						colName);
				String denybehavior = isColPresent ? xmlParser.getColVal(i, colName) : String
						.valueOf(HmAccessControl.BEHAVIOR_TYPE_BLANK);
				acl.setDenyBehavior((short) AhRestoreCommons.convertInt(denybehavior));

				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control",
						colName);
				long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
						colName)) : 1;
				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}
				acl.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				acl.setIpAddresses(ipaddresses.get(id));

				configs.add(acl);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get HM Acl config", e);
			}
		}

		return !configs.isEmpty() ? configs : null;
	}

	private static Map<String, List<String>> getHmAclItems() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_access_control_ip.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_access_control_ip");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<String>> aclItemInfo = new HashMap<String, List<String>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set hm_access_control_id
			 */
			colName = "hm_access_control_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control_ip",
					colName);
			if (!isColPresent) {
				/**
				 * The hm_access_control_id column must be exist in the table of
				 * hm_access_control_ip
				 */
				continue;
			}

			String hm_access_control_id = xmlParser.getColVal(i, colName);
			if (hm_access_control_id == null || hm_access_control_id.trim().equals("")
					|| hm_access_control_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set ipaddress
			 */
			colName = "ipaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_access_control_ip",
					colName);
			if (!isColPresent) {
				/**
				 * The ipaddress column must be exist in the table of hm_access_control_ip
				 */
				continue;
			}

			String ipaddress = xmlParser.getColVal(i, colName);
			if (ipaddress == null || ipaddress.trim().equals("")
					|| ipaddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (aclItemInfo.get(hm_access_control_id) == null) {
				List<String> ipaddresses = new ArrayList<String>();
				ipaddresses.add(ipaddress);
				aclItemInfo.put(hm_access_control_id, ipaddresses);
			} else {
				aclItemInfo.get(hm_access_control_id).add(ipaddress);
			}
		}
		return aclItemInfo;
	}

	/*
	 * HM Start Config
	 */
	public static void restoreHmStartConfig() {
		try {
			Map<String, List<HmStartConfig>> hmConf = getAllHmStartConfig();
			if (null != hmConf) {
				if (null != hmConf.get("new")) {
					QueryUtil.restoreBulkCreateBos(hmConf.get("new"));
				}
				if (null != hmConf.get("update")) {
					QueryUtil.bulkUpdateBos(hmConf.get("update"));
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restoreHmStartConfig()", e);
		}
	}

	public static List<HmStartConfig> getHmStartConfigModel()
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_start_config.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_start_config");
		HmStartConfig conf;
		List<HmStartConfig> configs = new ArrayList<HmStartConfig>();
		if(!restoreRet)
		{
			return null;
		}

		try
		{
			int rowCount = xmlParser.getRowCount();
			boolean isColPresent;
			String colName;

			for (int i = 0; i < rowCount; i++)
			{
				conf = new HmStartConfig();

				colName = "modetype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
						colName);
				short modetype = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
						.getColVal(i, colName)) : HmStartConfig.HM_MODE_FULL;
				conf.setModeType(modetype);
				configs.add(conf);
			}

			return configs;
		}
		catch(Exception ex)
		{
			AhRestoreDBTools.logRestoreMsg("getHmStartConfigModel()", ex);
			return null;
		}
	}

	public static Map<String, List<HmStartConfig>> getAllHmStartConfig() {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of hm_start_config.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_start_config");
		HmStartConfig conf;
		Map<String, List<HmStartConfig>> resultObj = new HashMap<String, List<HmStartConfig>>();
		List<HmStartConfig> configs;

		// upgrade from older version
		if (!restoreRet) {
			for (HmDomain hmDom : AhRestoreNewMapTools.hmDomainMap.values()) {
				// select database check if exist
				List<HmStartConfig> existConf = QueryUtil.executeQuery(HmStartConfig.class, null, null, hmDom.getId());
				if (existConf.isEmpty()) {
					conf = new HmStartConfig();
				} else {
					conf = existConf.get(0);
				}
				conf.setAdminUserLogin(true);
				conf.setModeType(HmStartConfig.HM_MODE_FULL);
				conf.setOwner(hmDom);

				String mapKey = existConf.isEmpty() ? "new" : "update";
				configs = resultObj.get(mapKey);
				if (null == configs) {
					configs = new ArrayList<HmStartConfig>();
					configs.add(conf);
				} else {
					configs.add(conf);
				}
			}
			AhRestoreDBTools.logRestoreMsg("hm_start_config.xml does not exist.");

		} else {
			/**
			 * No one row data stored in hm_start_config table is not allowed
			 */
			try {
				int rowCount = xmlParser.getRowCount();
				boolean isColPresent;
				String colName;
				for (int i = 0; i < rowCount; i++) {
					/**
					 * Set ID
					 */
					colName = "id";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					if (!isColPresent) {
						/**
						 * The id column must be exist in the table of hm_start_config
						 */
						continue;
					}
					String id = xmlParser.getColVal(i, colName);
					if (id == null || id.trim().equals("") || id.trim().equalsIgnoreCase("null")) {
						continue;
					}

					/**
					 * Set owner
					 */
					colName = "owner";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(
							i, colName)) : 1;

					if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
					{
						continue;
					}

					HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(ownerId);

					if (null == hmDom)
						continue;

					// select database check if exist
					List<HmStartConfig> existConf = QueryUtil.executeQuery(HmStartConfig.class, null, null, hmDom.getId());
					if (existConf.isEmpty()) {
						conf = new HmStartConfig();
					} else {
						conf = existConf.get(0);
					}
					conf.setOwner(hmDom);

					/**
					 * Set modetype
					 */
					colName = "modetype";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					short modetype = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
							.getColVal(i, colName)) : HmStartConfig.HM_MODE_FULL;
					conf.setModeType(modetype);

					/**
					 * Set hiveappassword
					 */
					colName = "hiveappassword";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					String apPass = isColPresent ? AhRestoreCommons.convertString(xmlParser
							.getColVal(i, colName)) : "";
					conf.setHiveApPassword(apPass);

					/**
					 * Set networkname
					 */
					colName = "networkname";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					String netName = isColPresent ? AhRestoreCommons.convertString(xmlParser
							.getColVal(i, colName)) : "";
					conf.setNetworkName(netName);

					/**
					 * Set useAccessConsole
					 */
					colName = "useaccessconsole";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					boolean useAccess = isColPresent && AhRestoreCommons
							.convertStringToBoolean(xmlParser.getColVal(i, colName));
					conf.setUseAccessConsole(useAccess);

					/**
					 * Set ledBrightness
					 */
					colName = "ledbrightness";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					short sysLed = isColPresent ? (short) AhRestoreCommons.convertInt(xmlParser
							.getColVal(i, colName)) : MgmtServiceOption.SYSTEM_LED_BRIGHT;
					conf.setLedBrightness(sysLed);

					/**
					 * Set adminUserLogin
					 */
					colName = "adminuserlogin";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_start_config",
							colName);
					boolean userLogin = !isColPresent || AhRestoreCommons
							.convertStringToBoolean(xmlParser.getColVal(i, colName));
					conf.setAdminUserLogin(userLogin);

					String mapKey = existConf.isEmpty() ? "new" : "update";
					configs = resultObj.get(mapKey);
					if (null == configs) {
						configs = new ArrayList<HmStartConfig>();
						configs.add(conf);
						resultObj.put(mapKey, configs);
					} else {
						configs.add(conf);
					}

					// express mode
					if (HmStartConfig.HM_MODE_EASY == conf.getModeType()) {
						ConfigTemplate wlan = QueryUtil.findBoByAttribute(ConfigTemplate.class,
								"defaultFlag", false, hmDom.getId());
						if (null != wlan) {
							// get the LLDPCDPProfile in wlan
							LLDPCDPProfile lldpObj = wlan.getLldpCdp();
							boolean needUpdateWlan = false;
							// there is no LLDPCDPProfile
							if (null == lldpObj) {
								lldpObj = new LLDPCDPProfile();
								lldpObj.setProfileName(wlan.getConfigName());
								lldpObj.setOwner(hmDom);
								lldpObj.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
								// create new LLDPCDPProfile for wlan
								QueryUtil.createBo(lldpObj);
								// add LLDPCDPProfile for wlan
								wlan.setLldpCdp(lldpObj);
								needUpdateWlan=true;
							}
							// remove quick start policies
							if (needUpdateWlan) {
								QueryUtil.updateBo(wlan);
							}
						}
					}
				}
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("getAllHmStartConfig()", e);
				return null;
			}
		}
		return resultObj;
	}

	// ---------------------- Capwap Client -------------START---------

	public static void restoreCapwapClient() {
		// if (AhRestoreDBTools.HM_RESTORE_DOMAIN != null
		// && !AhRestoreDBTools.HM_RESTORE_DOMAIN.isHomeDomain()) {
		// return;
		// }

		try {
			Collection<CapwapClient> list = getAllCapwapClientSettings();
			if (null == list || list.isEmpty()) {
				return;
			}

			List<CapwapClient> createList = new ArrayList<CapwapClient>();
			List<CapwapClient> updateList = new ArrayList<CapwapClient>();
			for (CapwapClient capwapClient : list) {
				CapwapClient existBo = QueryUtil.findBoByAttribute(CapwapClient.class, "serverType", capwapClient.getServerType());
				if (existBo != null) {
					existBo.setBackupCapwapIP(capwapClient.getBackupCapwapIP());
					existBo.setCapwapEnable(capwapClient.isCapwapEnable());
					existBo.setDtlsEnable(capwapClient.isDtlsEnable());
					existBo.setNeighborDeadInterval(capwapClient.getNeighborDeadInterval());
					existBo.setPassphrase(capwapClient.getPassphrase());
					existBo.setPrimaryCapwapIP(capwapClient.getPrimaryCapwapIP());
					existBo.setTimeOut(capwapClient.getTimeOut());
					existBo.setUdpPort(capwapClient.getUdpPort());
					existBo.setTransportMode(capwapClient.getTransportMode());

					updateList.add(existBo);
				} else {
					createList.add(capwapClient);
				}
			}

			if (!createList.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(createList);
			}
			if (!updateList.isEmpty()) {
				QueryUtil.bulkUpdateBos(updateList);
			}
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"RestoreAdmin.restoreCapwapClient() catch exception ", e);
		}
	}

	/**
	 * Get all information from hm_capwapclient table
	 *
	 * @return List<CapwapClient> all HmDomain BO
	 * @throws Exception
	 *             -
	 */
	private static Collection<CapwapClient> getAllCapwapClientSettings() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of hm_capwapclient.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("hm_capwapclient");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		Map<Byte, CapwapClient> map = new HashMap<Byte, CapwapClient>();

		for (int i = 0; i < rowCount; i++) {
			CapwapClient bo = new CapwapClient();

			/**
			 * Set serverType
			 */
			String colName = "serverType";
			boolean isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient",
					colName);
			byte serverType = (byte) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : CapwapClient.SERVERTYPE_PORTAL);
			bo.setServerType(serverType);

			/**
			 * Set capwapEnable
			 */
			colName = "capwapEnable";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			boolean capwapEnable = !isColPresent || AhRestoreCommons.convertStringToBoolean(xmlParser
					.getColVal(i, colName));
			bo.setCapwapEnable(capwapEnable);

			/**
			 * Set udpPort
			 */
			colName = "udpPort";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			int port = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
					: 12222;
			bo.setUdpPort(port);

			/**
			 * Set timeOut
			 */
			colName = "timeOut";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			short timeOut = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 30);
			bo.setTimeOut(timeOut);

			/**
			 * Set neightbor dead interval
			 */
			colName = "neighborDeadInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			short deadInterval = (short) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : 105);
			bo.setNeighborDeadInterval(deadInterval);

			/**
			 * Set passphrase
			 */
			colName = "passphrase";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			String passphrase = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
					i, colName)) : "";
			bo.setPassphrase(passphrase);

			/**
			 * Set primaryCapwapIP
			 */
			colName = "primaryCapwapIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			String primaryCapwapIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			bo.setPrimaryCapwapIP(AhRestoreCommons.convertString(primaryCapwapIP));
			
			// from Glasgow we will use this field for HM-Onpremise for client management
			if (!NmsUtil.isHostedHMApplication() && serverType == CapwapClient.SERVERTYPE_PORTAL && 
					!StringUtils.isBlank(bo.getPrimaryCapwapIP()) && "myhive.aerohive.com".equalsIgnoreCase(bo.getPrimaryCapwapIP())) {
				bo.setPrimaryCapwapIP("");
			}

			/**
			 * Set backupCapwapIP
			 */
			colName = "backupCapwapIP";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient", colName);
			String backupCapwapIP = isColPresent ? xmlParser.getColVal(i, colName) : "";
			bo.setBackupCapwapIP(AhRestoreCommons.convertString(backupCapwapIP));

			/**
			 * Set transportMode
			 */
			colName = "transportMode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "hm_capwapclient",
					colName);
			byte transportMode = (byte) (isColPresent ? AhRestoreCommons.convertInt(xmlParser
					.getColVal(i, colName)) : BeAPConnectEvent.TRANSFERMODE_UDP);
			bo.setTransportMode(transportMode);

			map.put(serverType, bo);
		}

		return !map.isEmpty() ? map.values() : null;
	}

	// ---------------------- Capwap Client -------------END---------

	/**
	 * restore AirTight SGE settings
	 *
	 * @author Joseph Chen
	 */
	public static void restoreSGESettings() {
		try {
			List<AirtightSettings> settings = getSGESettings();

			if (settings == null) {
				return;
			}

			QueryUtil.restoreBulkCreateBos(settings);
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restoreSGESettings()", e);
		}
	}

	private static List<AirtightSettings> getSGESettings() throws AhRestoreColNotExistException,
			AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/*
		 * Check validation of air_tight_settings.xml
		 */
		String tableName = "air_tight_settings";
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		if (rowCount <= 0) {
			return null;
		}

		List<AirtightSettings> settings = new ArrayList<AirtightSettings>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			AirtightSettings setting = new AirtightSettings();

			/*
			 * enabled
			 */
			colName = "enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));

			/*
			 * server URL
			 */
			colName = "serverURL";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String url = isColPresent ? xmlParser.getColVal(i, colName) : "";
			if(url.startsWith("https://")){
				url = url.substring(8);
			}
			setting.setServerURL(AhRestoreCommons.convertString(url));

			/*
			 * user name
			 */
			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setUserName(AhRestoreCommons.convertString(username));

			/*
			 * password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setPassword(AhRestoreCommons.convertString(password));

			/*
			 * sync interval
			 */
			colName = "syncInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String interval = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(AirtightSettings.DEFAULT_SYNC_INTERVAL);

			int intValue = AhRestoreCommons.convertInt(interval);

			if (intValue > 60) {
				intValue = intValue / 60; // from seconds to minutes
			}

			setting.setSyncInterval(intValue);

			/*
			 * client id
			 */
			/*colName = "clientID";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String clientid = isColPresent ? xmlParser.getColVal(i, colName)
					: AirtightSettings.DEFAULT_CLIENT_IDENTIFIER;
			setting.setClientID(AhRestoreCommons.convertString(clientid));*/

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
			   continue;
			}
			setting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			settings.add(setting);
		}

		return settings;
	}

	// ---------------- start RPC ---------------------
	/**
	 * restore Remote Process Call (RPC) Settings
	 *
	 * @author Yunzhi Lin
	 */
	public static void restoreRPCSettings() {
		try {
			List<RemoteProcessCallSettings> settings = getRPCSettings();

			if (null == settings || settings.isEmpty()) {
				return;
			}

			List<RemoteProcessCallSettings> list = QueryUtil.executeQuery(RemoteProcessCallSettings.class, null, null);
			if (list.isEmpty()){
				QueryUtil.createBo(settings.get(0));
			}else{
				RemoteProcessCallSettings destBo = list.get(0);
				RemoteProcessCallSettings srcBo = settings.get(0);
				destBo.setEnabled(srcBo.isEnabled());
				destBo.setOwner(srcBo.getOwner());
				destBo.setPassword(srcBo.getPassword());
				destBo.setTimeout(srcBo.getTimeout());
				destBo.setUserName(srcBo.getUserName());

				QueryUtil.updateBo(destBo);
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg("restoreRPCSettings()", e);
		}
	}

	private static List<RemoteProcessCallSettings> getRPCSettings()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/*
		 * Check validation of rcp_settings.xml
		 */
		String tableName = "rpc_settings";
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();

		if (rowCount <= 0) {
			return null;
		}

		List<RemoteProcessCallSettings> settings = new ArrayList<RemoteProcessCallSettings>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			RemoteProcessCallSettings setting = new RemoteProcessCallSettings();

			/*
			 * user name
			 */
			colName = "userName";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String username = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setUserName(AhRestoreCommons.convertString(username));

			/*
			 * password
			 */
			colName = "password";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String password = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setPassword(AhRestoreCommons.convertString(password));

			/*
			 * sync interval
			 */
			colName = "timeout";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String interval = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(RemoteProcessCallSettings.DEFAULT_OVERTIME);

			int intValue = AhRestoreCommons.convertInt(interval);

			setting.setTimeout(intValue);

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
					colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
			   continue;
			}
			setting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			/**
			 * enabled
			 */
			colName = "enabled";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enabled = isColPresent ? xmlParser.getColVal(i, colName) : "";
			setting.setEnabled(AhRestoreCommons.convertStringToBoolean(enabled));

			settings.add(setting);
		}

		return settings;
	}
	// ---------------- end RPC ---------------------

	static class AdminQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof HmUserGroup) {
				HmUserGroup profile = (HmUserGroup) bo;
				if (profile.getFeaturePermissions() != null) {
					profile.getFeaturePermissions().size();
				}
				if (profile.getInstancePermissions() != null) {
					profile.getInstancePermissions().size();
				}
			}

			if(bo instanceof HmUser) {
				HmUser user = (HmUser)bo;

//				if(user.getLocalUserGroups() != null) {
//					user.getLocalUserGroups().size();
//				}
//
//				if(user.getSsidProfiles() != null) {
//					user.getSsidProfiles().size();
//				}
//
//				if(user.getTableColumns() != null) {
//					user.getTableColumns().size();
//				}
//
//				if(user.getTableSizes() != null) {
//					user.getTableSizes().size();
//				}
//
//				if (user.getAutoRefreshs() != null) {
//					user.getAutoRefreshs().size();
//				}
			}

			return null;
		}
	}

	private static void setFeaturePermission(HmUserGroup userGroup) {
		// set feature permissions
		if (HmUserGroup.MONITOR.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(monitorPermissions);
		} else if (HmUserGroup.CONFIG.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(configPermissions);
		} else if (HmUserGroup.PLANNING.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(plannerPermissions);
		} else if (HmUserGroup.GM_ADMIN.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(gmAdminPermissions);
		} else if (HmUserGroup.GM_OPERATOR.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(gmOperatorPermissions);
		} else if (HmUserGroup.TEACHER.equals(userGroup.getGroupName())) {
			userGroup.setFeaturePermissions(tcOperatorPermissions);
		} else {
			setReportLostFeaturePermission(userGroup.getId());
			userGroup.setFeaturePermissions(featurePermissionsMap.get(userGroup.getId()));
		}
	}
	
	private static void setReportLostFeaturePermission(Long groupId) {
		if (groupId==null) return;
		if (featurePermissionsMap.get(groupId)==null) return;
		if (featurePermissionsMap.get(groupId).get("legacyReport")==null) {
			HmPermission  hp = new HmPermission ();
			hp.setOperations(HmPermission.OPERATION_READ);
			hp.addOperation(HmPermission.OPERATION_WRITE);
			featurePermissionsMap.get(groupId).put("legacyReport", hp);
		}
		
	}

	// first value of group attribute (for groups except default group of HOME)
	private static final int FIRST_GROUP_ATTRIBUTE_FOR_NEW_GROUP = 10;

	private static void setUniqueGroupAttribute(List<HmUserGroup> userGroups,
			boolean isAlreadySupportVhmRadius) {
		int iniGroupAttribute = FIRST_GROUP_ATTRIBUTE_FOR_NEW_GROUP;
		Set<Integer> groupAttrSet = new HashSet<Integer>(userGroups.size());
		for (HmUserGroup o : userGroups) {
			if (o.getGroupAttribute() >= 0) {
				groupAttrSet.add(o.getGroupAttribute());
			}
		}

		if (isAlreadySupportVhmRadius) {
			// already support VHM radius, if got negative group attribute, reset it with unused attribute
			for (HmUserGroup userGroup : userGroups) {
				if (userGroup.getGroupAttribute() < 0) {

					iniGroupAttribute = getUserGroupAttribute(iniGroupAttribute, groupAttrSet);
					groupAttrSet.add(iniGroupAttribute);
					userGroup.setGroupAttribute(iniGroupAttribute);
				}
			}
		} else {
			// add teacher group for every VHM's
			addDefaultUserGroup(userGroups);

			groupAttrSet = new HashSet<Integer>();

			// to restore 3.4R4 to 4.0r1(every VHM got one data file[hm_user_group.xml])
			List<HmUserGroup> otherVhmUserGroups = QueryUtil.executeQuery(HmUserGroup.class, null, null);
			Integer groupAttr;
			for (HmUserGroup hmUserGroup : otherVhmUserGroups) {
				groupAttr = hmUserGroup.getGroupAttribute();
				if (!groupAttrSet.contains(groupAttr)) {
					groupAttrSet.add(groupAttr);
				}
			}

			for (HmUserGroup userGroup : userGroups) {
				boolean isDefaultGroupOfHome = false;
				if (userGroup.getOwner().isHomeDomain()) {

					// HOME's default user group should keep fixed attributes
					if (HmUserGroup.ADMINISTRATOR.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.ADMINISTRATOR_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.MONITOR.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.MONITOR_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.CONFIG.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.CONFIG_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.PLANNING.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.PLANNING_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.GM_ADMIN.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.GM_ADMIN_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.GM_OPERATOR.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.GM_OPERATOR_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.TEACHER.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.TEACHER_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.VAD.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.VAD_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					} else if (HmUserGroup.STANDALONE_HM.equals(userGroup.getGroupName())) {
						userGroup.setGroupAttribute(HmUserGroup.STANDALONE_HM_ATTRIBUTE);
						isDefaultGroupOfHome = true;
					}
				}

				if (!isDefaultGroupOfHome) {
					// other user groups
					iniGroupAttribute = getUserGroupAttribute(iniGroupAttribute, groupAttrSet);
					groupAttrSet.add(iniGroupAttribute);
					userGroup.setGroupAttribute(iniGroupAttribute);
				}
			}
		}
	}

	// get minimum unused attributes(from 10 to 65535)
	private static int getUserGroupAttribute(int init, Set<Integer> groupAttrSet) {
		Integer initId = init;
		while (groupAttrSet.contains(initId)) {
			initId++;
		}
		return initId;
	}

	private static void addDefaultUserGroup(List<HmUserGroup> userGroups) {
		// Map<vhmname, Set<groupname>>
		Map<HmDomain, Set<String>> ht_group = new HashMap<HmDomain, Set<String>>();

		Set<String> groupnames;
		for (HmUserGroup userGroup :userGroups){
			// Teacher group for home domain was created before restore DB(in class:BeParaModuleDefImpl.java)
			if (userGroup.getOwner().isHomeDomain()) {
				continue;
			}

			groupnames = ht_group.get(userGroup.getOwner());
			if (groupnames == null) {
				groupnames = new HashSet<String>();
			}
			groupnames.add(userGroup.getGroupName());
			ht_group.put(userGroup.getOwner(), groupnames);
		}

		// check whether every VHM has user group for teacher.
		for (HmDomain vhm : ht_group.keySet()) {
			Set<String> groupsPerVhm = ht_group.get(vhm);
			if (!groupsPerVhm.contains(HmUserGroup.TEACHER)) {
				HmUserGroup teacherGroup = new HmUserGroup();
				teacherGroup.setGroupName(HmUserGroup.TEACHER);
				teacherGroup.setDefaultFlag(true);
				teacherGroup.setOwner(vhm);
				teacherGroup.setFeaturePermissions(getTeacherPermission());
				userGroups.add(teacherGroup);
			}
		}
	}

	public static Map<String, HmPermission> getTeacherPermission() {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();

		HmPermission readWritePermission = new HmPermission();
		readWritePermission.addOperation(HmPermission.OPERATION_READ);
		readWritePermission.addOperation(HmPermission.OPERATION_WRITE);
		map.put(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY, readWritePermission);
		return map;
	}

	public static boolean restoreHmUserAboutSettings() {
		try {
			Map<Long, List<HmAutoRefresh>> map = getUserAutoRefreshCustom();

			if(map == null) {
				return false;
			}

			List<HmAutoRefresh> autorefs = null;

			for(Long userId : map.keySet()) {
				Long newId = AhRestoreNewMapTools.getMapUser(userId);
				if (newId==null) {
					continue;
				}
				HmUser user = QueryUtil.findBoById(HmUser.class, newId, new AdminQueryBo());
				autorefs = map.get(userId);
				user.setAutoRefreshs(autorefs);

				for (HmAutoRefresh autor : autorefs) {
					autor.setUseremail(user.getEmailAddress());
				}
				QueryUtil.restoreBulkCreateBos(autorefs);
			}

		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreHmUserAboutSettingsNew() {
		try {
			List<HmAutoRefresh> bos = getUserAutoRefreshCustomNew();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore hm_autorefresh_settings_new error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreLocalUserGroupNew() {
		try {
			List<HmLocalUserGroup> bos = getLocalUserGroupNew();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore user_localusergroup_new error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreUserSsidProfileNew() {
		try {
			List<HmUserSsidProfile> bos = getUserSsidProfileNew();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore user_ssidprofile_new error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	public static boolean restoreHmUserSettings() {
		try {
			List<HmUserSettings> bos = getHmUserSettings();

			if (bos != null && bos.size() > 0) {
				QueryUtil.restoreBulkCreateBos(bos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg("Restore hm_user_settings error");
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	private static Map<Long, List<HmAutoRefresh>> getUserAutoRefreshCustom() throws
							AhRestoreColNotExistException,AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_autorefresh_settings";

		boolean flg = AhRestoreGetXML.checkXMLFileExist("hm_autorefresh_settings_new");
		if (flg) return null;

		/*
		 * check validation of file 'hm_table_column.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<Long, List<HmAutoRefresh>> autoRefreshSettings = new HashMap<Long, List<HmAutoRefresh>>();
		HmAutoRefresh autoRefresh;
		boolean isColPresent;
		String colName;


		for (int i = 0; i < rowCount; i++) {
			autoRefresh = new HmAutoRefresh();

			/*
			 * hm_user_id
			 */
			colName = "hm_user_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String userId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			Long userIdl = Long.parseLong(userId);

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			autoRefresh.setPosition(Integer.parseInt(position));

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			autoRefresh.setTableId(Integer.parseInt(tableId));

			/*
			 * autorefresh
			 */
			colName = "autorefresh";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String autoRefreshStr = isColPresent ? xmlParser.getColVal(i, colName) : "";
			autoRefresh.setAutoRefresh(AhRestoreCommons.convertStringToBoolean(autoRefreshStr));

			/*
			 * refInterval
			 */
			colName = "refInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String refInterval = isColPresent ? xmlParser.getColVal(i, colName) : HmAutoRefresh.DEFAULT_INTERVAL;
			autoRefresh.setRefInterval(AhRestoreCommons.convertString(refInterval));

			if (autoRefreshSettings.get(userIdl) != null) {
				autoRefreshSettings.get(userIdl).add(autoRefresh);
			} else {
				List<HmAutoRefresh> refreshs = new ArrayList<HmAutoRefresh>();
				refreshs.add(autoRefresh);
				autoRefreshSettings.put(userIdl, refreshs);
			}
		}

		return autoRefreshSettings;
	}

	private static List<HmAutoRefresh> getUserAutoRefreshCustomNew()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_autorefresh_settings_new";

		/*
		 * check validation of file 'hm_table_column.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmAutoRefresh> autoRefreshSettings = new ArrayList<HmAutoRefresh>();
		HmAutoRefresh autoRefresh;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			autoRefresh = new HmAutoRefresh();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			autoRefresh.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/*
			 * position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			autoRefresh.setPosition(Integer.parseInt(position));

			/*
			 * tableid
			 */
			colName = "tableid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String tableId = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			autoRefresh.setTableId(Integer.parseInt(tableId));

			/*
			 * autorefresh
			 */
			colName = "autorefresh";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String autoRefreshStr = isColPresent ? xmlParser.getColVal(i,
					colName) : "";
			autoRefresh.setAutoRefresh(AhRestoreCommons
					.convertStringToBoolean(autoRefreshStr));

			/*
			 * refInterval
			 */
			colName = "refInterval";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String refInterval = isColPresent ? xmlParser.getColVal(i, colName)
					: HmAutoRefresh.DEFAULT_INTERVAL;
			autoRefresh.setRefInterval(AhRestoreCommons
					.convertString(refInterval));

			autoRefreshSettings.add(autoRefresh);
		}

		return autoRefreshSettings;
	}

	private static List<HmLocalUserGroup> getLocalUserGroupNew()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "user_localusergroup_new";

		/*
		 * check validation of file 'user_localusergroup.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmLocalUserGroup> hlug = new ArrayList<HmLocalUserGroup>();
		HmLocalUserGroup localUserGroup;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			localUserGroup = new HmLocalUserGroup();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			localUserGroup.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/*
			 * localusergroup_id
			 */
			colName = "localusergroup_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String localusergroup_id = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			if (StringUtils.isEmpty(localusergroup_id)) continue;

			Long newId = AhRestoreNewMapTools.getMapLocalUserGroup(Long.valueOf(localusergroup_id));
			if (null != newId) {
				localUserGroup.setLocalusergroup_id(newId);
			} else {
				continue;
			}

			hlug.add(localUserGroup);
		}

		return hlug;
	}

	private static List<HmUserSsidProfile> getUserSsidProfileNew()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "user_ssidprofile_new";

		/*
		 * check validation of file 'user_ssidprofile_new.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmUserSsidProfile> husp = new ArrayList<HmUserSsidProfile>();
		HmUserSsidProfile userSsidProfile;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			userSsidProfile = new HmUserSsidProfile();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			userSsidProfile.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/*
			 * ssidprofile_id
			 */
			colName = "ssidprofile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String ssidprofile_id = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			if (StringUtils.isEmpty(ssidprofile_id)) continue;
			Long newId = AhRestoreNewMapTools.getMapSsid(Long.valueOf(ssidprofile_id));
			if (null != newId) {
				userSsidProfile.setSsidprofile_id(newId);
			} else {
				continue;
			}

			husp.add(userSsidProfile);
		}

		return husp;
	}

	private static List<HmUserSettings> getHmUserSettings()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "hm_user_settings";

		/*
		 * check validation of file 'hm_user_settings.xml'
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);

		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		List<HmUserSettings> hmUserSettings = new ArrayList<HmUserSettings>();
		HmUserSettings hmUserSetting;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			hmUserSetting = new HmUserSettings();

			/*
			 * useremail
			 */
			colName = "useremail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String useremail = isColPresent ? xmlParser.getColVal(i, colName)
					: "null_value";
			hmUserSetting.setUseremail(useremail);
			
			// fix bug 32249, notice if email list is empty, restore all records
			// only needed for single VHM move/upgrade on HMOL
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				if (StringUtils.isEmpty(useremail)) {
					// user email is null
					continue;
				} else if (AhRestoreNewMapTools.getVhmEmails() != null
						&& !AhRestoreNewMapTools.getVhmEmails().isEmpty()
						&& !AhRestoreNewMapTools.getVhmEmails().contains(useremail)) {
					// email list is not empty & current user email not included in email list, ignore this record
					continue;
				}
			}

			/**
			 * Set dontShowMessageInDashboard
			 */
			colName = "dontShowMessageInDashboard";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String dontShowMessageInDashboard = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "false";
			hmUserSetting.setDontShowMessageInDashboard(AhRestoreCommons.convertStringToBoolean(dontShowMessageInDashboard));

			/**
			 * Set enduserlicagree
			 */
			colName = "enduserlicagree";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String enduserlicagree = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "false";
			hmUserSetting.setEndUserLicAgree(AhRestoreCommons.convertStringToBoolean(enduserlicagree));

			/*
			 * maxapnum
			 */
			colName = "maxapnum";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String maxapnum = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";
			hmUserSetting.setMaxAPNum(AhRestoreCommons.convertInt(maxapnum));

			/*
			 * navcustomization
			 */
			colName = "navcustomization";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String navcustomization = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			hmUserSetting.setNavCustomization(AhRestoreCommons.convertInt(navcustomization, 16));

			/**
			 * Set orderfolders
			 */
			colName = "orderfolders";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String orderfolders = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "false";
			hmUserSetting.setOrderFolders(AhRestoreCommons.convertStringToBoolean(orderfolders));


			/**
			 * Set promptchanges
			 */
			colName = "promptchanges";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			String promptchanges = isColPresent ? AhRestoreCommons.convertString(xmlParser
					.getColVal(i, colName)) : "true";
			hmUserSetting.setPromptChanges(AhRestoreCommons.convertStringToBoolean(promptchanges));

			/*
			 * syncresult
			 */
			colName = "syncresult";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String syncresult = isColPresent ? xmlParser.getColVal(i, colName)
					: "0";// HmUser.SYNC_RESULT_OK = 0
			hmUserSetting.setSyncResult((short) AhRestoreCommons.convertInt(syncresult));

			/*
			 * treewidth
			 */
			colName = "treewidth";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String treewidth = isColPresent ? xmlParser.getColVal(i, colName)
					: "220";
			hmUserSetting.setTreeWidth((short) AhRestoreCommons.convertInt(treewidth));

			hmUserSettings.add(hmUserSetting);
		}

		return hmUserSettings;
	}
}
