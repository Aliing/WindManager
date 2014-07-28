/**
 *@filename		RestoreOrderKey.java
 *@version
 *@author		Fiona
 *@createtime	2010-3-24 PM 03:02:09
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class RestoreOrderKey {
	
	private static Map<String, String> entitleKeyDomainNameMap = new HashMap<String, String>();

	public static void restoreOrderKey()
	{
		restoreOrderKeyDomainInfo();
		
		restoreOrderKeyHistoryInfo();
		
		restoreAcmEntitleKeyHistoryInfo();
	}
	
	
	// ---------------------- Order Key History Info -------------Start---------
	public static boolean restoreOrderKeyHistoryInfo() {
		try {
			List<OrderHistoryInfo> orders = getOrderKeyHistoryInfo();
			
			if (!entitleKeyDomainNameMap.isEmpty()) {
				Collection<String> domains = entitleKeyDomainNameMap.values();
				for (OrderHistoryInfo orderInfo : orders) {
					// this domain need change
					if (domains.contains(orderInfo.getDomainName())) {
						// the evaluation key need change status
						if (!BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(orderInfo.getLicenseType()) && !BeLicenseModule.LICENSE_TYPE_RENEW_NUM.equals(orderInfo.getLicenseType())) {
							orderInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
							orderInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
						}
					}
				}
			}

			if(null != orders) {
				QueryUtil.bulkCreateBos(orders);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from order_history_info table
	 * 
	 * @return OrderHistoryInfo
	 * @throws AhRestoreException -
	 *             if error in parsing order_history_info.xml.
	 */
	private static List<OrderHistoryInfo> getOrderKeyHistoryInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of order_history_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("order_history_info");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in order_history_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		OrderHistoryInfo orderInfo;
		List<OrderHistoryInfo> allInfo = new ArrayList<OrderHistoryInfo>();
		
		for (int i = 0; i < rowCount; i++) {
			orderInfo = new OrderHistoryInfo();
			/**
			 * Set orderkey
			 */
			colName = "orderkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			String orderkey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setOrderKey(orderkey);

			/**
			 * Set activetime
			 */
			colName = "activetime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			long activetime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) :
				0;
			orderInfo.setActiveTime(activetime);
			
			/**
			 * Set numberofaps
			 */
			colName = "numberofaps";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			int aps = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			orderInfo.setNumberOfAps(aps);
			
			/**
			 * Set numberofcvgs
			 */
			colName = "numberofcvgs";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			int cvgs = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			orderInfo.setNumberOfCvgs(cvgs);
			
			/**
			 * Set numberofevalvaliddays
			 */
			colName = "numberofevalvaliddays";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			int days = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			orderInfo.setNumberOfEvalValidDays(days);
			
			/**
			 * Set numberofvhms
			 */
			colName = "numberofvhms";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			int vhms = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : 0;
			orderInfo.setNumberOfVhms(vhms);
			
			/**
			 * Set licensetype
			 */
			colName = "licensetype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			String licensetype = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			if ("0".equals(licensetype)) {
				licensetype = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY;
			}
			orderInfo.setLicenseType(licensetype);
			
			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			String domainname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setDomainName(domainname);
			
			// whole hmol does not have license info
			if (NmsUtil.isHostedHMApplication() && HmDomain.HOME_DOMAIN.equals(domainname)) {
				continue;
			}
			
			/**
			 * Set supportenddate
			 */
			colName = "supportenddate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			long endDate = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0;
			orderInfo.setSupportEndDate(endDate);
			
			/**
			 * Set subEndDate
			 */
			colName = "subenddate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			if (!isColPresent) {
				if (!HmDomain.HOME_DOMAIN.equals(domainname)) {
					orderInfo.setSubEndDate(endDate);
					orderInfo.setSupportEndDate(0);
				}
			} else {
				long subEndDate = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				orderInfo.setSubEndDate(subEndDate);
			}
			
			/**
			 * Set cvgsubenddate
			 */
			colName = "cvgsubenddate";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			if (!isColPresent) {
				if (!HmDomain.HOME_DOMAIN.equals(domainname)) {
					orderInfo.setCvgSubEndDate(orderInfo.getSubEndDate());
				}
			} else {
				long subEndDate = AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName));
				orderInfo.setCvgSubEndDate(subEndDate);
			}
			
			/**
			 * Set statusflag
			 */
			colName = "statusflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			short statusflag = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL;
			orderInfo.setStatusFlag(statusflag);
			
			/**
			 * Set cvgstatusflag
			 */
			colName = "cvgstatusflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			if (!isColPresent) {
				orderInfo.setCvgStatusFlag(orderInfo.getStatusFlag());
			} else {
				statusflag = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i,
					colName)) : OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL;
				orderInfo.setCvgStatusFlag(statusflag);
			}
			
			/**
			 * Set sendemail
			 */
			colName = "sendemail";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "order_history_info",
					colName);
			boolean sendEmail = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,colName));
			orderInfo.setSendEmail(sendEmail);
			
			if (!entitleKeyDomainNameMap.isEmpty()) {
				if (null != entitleKeyDomainNameMap.get(orderkey)) {
					// the current type is permanent
					if (!BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licensetype) && !BeLicenseModule.LICENSE_TYPE_RENEW_NUM.equals(licensetype)) {
						entitleKeyDomainNameMap.remove(orderkey);
					}
				}
			}
			
			allInfo.add(orderInfo);
		}

		return allInfo.isEmpty() ? null : allInfo;
	}
	// ---------------------- Order Key History Info -------------END---------
	
	// ---------------------- Order Key Domain Info -------------Start---------
	public static boolean restoreOrderKeyDomainInfo() {
		try {
			List<DomainOrderKeyInfo> orders = getOrderKeyDomainInfo();

			if(null != orders) {
				QueryUtil.bulkCreateBos(orders);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from domain_order_key_info table
	 * 
	 * @return DomainOrderKeyInfo
	 * @throws AhRestoreException -
	 *             if error in parsing domain_order_key_info.xml.
	 */
	private static List<DomainOrderKeyInfo> getOrderKeyDomainInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of domain_order_key_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("domain_order_key_info");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in order_history_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		DomainOrderKeyInfo orderInfo;
		List<DomainOrderKeyInfo> allInfo = new ArrayList<DomainOrderKeyInfo>();
		
		for (int i = 0; i < rowCount; i++) {
			orderInfo = new DomainOrderKeyInfo();
			/**
			 * Set orderkey
			 */
			colName = "orderkey";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			String orderkey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setOrderKey(orderkey);

			/**
			 * Set createtime
			 */
			colName = "createtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			long createtime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) :
				0;
			orderInfo.setCreateTime(createtime);
			
			/**
			 * Set domainname
			 */
			colName = "domainname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			String domainname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setDomainName(domainname);
			
			// whole hmol does not have license info
			if (NmsUtil.isHostedHMApplication() && HmDomain.HOME_DOMAIN.equals(domainname)) {
				continue;
			}
			
			/**
			 * Set hoursused
			 */
			colName = "hoursused";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			String hoursused = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setHoursUsed(hoursused);
			
			/**
			 * Set licensestr
			 */
			colName = "licensestr";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			String licensestr = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setLicenseStr(licensestr);
			
			/**
			 * Set systemid
			 */
			colName = "systemid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info",
					colName);
			String systemid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
					colName)) : "";
			orderInfo.setSystemId(systemid);
			
			// from congo fcs we change the design of evaluation entitlement key
			if (!AhRestoreCommons.isColumnPresent(xmlParser, "domain_order_key_info", "evalkeychange") && null != orderkey && !"".equals(orderkey)
					&& !DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(orderkey))
				entitleKeyDomainNameMap.put(orderkey, domainname);
			
			allInfo.add(orderInfo);
		}

		return allInfo.isEmpty() ? null : allInfo;
	}
	// ---------------------- Order Key Domain Info -------------END---------
	
	// ---------------------- User Register Info ---------------Begin--------
	
	public static boolean restoreUserRegisterInfo() {
		try {
			List<UserRegInfoForLs> userInfos = getUserRegisterInfo();

			if(null != userInfos) {
				QueryUtil.bulkCreateBos(userInfos);
			}
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get all information from user_reg_info_for_ls table
	 * 
	 * @return UserRegInfoForLs
	 * @throws AhRestoreException -
	 *             if error in parsing user_reg_info_for_ls.xml.
	 */
	private static List<UserRegInfoForLs> getUserRegisterInfo() throws Exception {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of user_reg_info_for_ls.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("user_reg_info_for_ls");
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in order_history_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		boolean isColPresent;
		String colName;
		UserRegInfoForLs userInfo;
		List<UserRegInfoForLs> allInfo = new ArrayList<UserRegInfoForLs>();
		
		for (int i = 0; i < rowCount; i++) {
			userInfo = new UserRegInfoForLs();
			/**
			 * Set company
			 */
			colName = "company";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String company = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setCompany(company);

			/**
			 * Set country
			 */
			colName = "country";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String country = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setCountry(country);
			
			/**
			 * Set addressline1
			 */
			colName = "addressline1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String addressline1 = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setAddressLine1(addressline1);
			
			/**
			 * Set addressline2
			 */
			colName = "addressline2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String addressline2 = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setAddressLine2(addressline2);
			
			/**
			 * Set postalcode
			 */
			colName = "postalcode";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String postalcode = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setPostalCode(postalcode);
			
			/**
			 * Set name
			 */
			colName = "name";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setName(name);
			
			/**
			 * Set email
			 */
			colName = "email";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String email = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setEmail(email);
			
			/**
			 * Set telephone
			 */
			colName = "telephone";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			String telephone = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,colName)) : "";
			userInfo.setTelephone(telephone);
			
			/**
			 * Set activebyself
			 */
			colName = "activebyself";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			boolean acBySelf = isColPresent ? AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,colName)) : false;
			userInfo.setActiveBySelf(acBySelf);
			
			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "user_reg_info_for_ls", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain ownerDomain = AhRestoreNewMapTools.getHmDomain(ownerId);
			if(null == ownerDomain)
			{
			   continue;
			}
			userInfo.setOwner(ownerDomain);
			
			allInfo.add(userInfo);
		}

		return allInfo.isEmpty() ? null : allInfo;
	}
	
	// ---------------------- User Register Info ---------------End--------
	
	// ---------------------- ACM Entitlement Key History Info -------------Start---------
		public static boolean restoreAcmEntitleKeyHistoryInfo() {
			try {
				List<AcmEntitleKeyHistoryInfo> keyInfos = getAcmEntileKeyHistoryInfo();

				if(null != keyInfos && !keyInfos.isEmpty()) {
					QueryUtil.bulkCreateBos(keyInfos);
				}
			} catch(Exception e) {
				AhRestoreDBTools.logRestoreMsg(e.getMessage());
				return false;
			}
			return true;
		}
		
		/**
		 * Get all information from acm_entitle_key_history_info table
		 * 
		 * @return AcmEntitleKeyHistoryInfo
		 * @throws AhRestoreException -
		 *             if error in parsing acm_entitle_key_history_info.xml.
		 */
		private static List<AcmEntitleKeyHistoryInfo> getAcmEntileKeyHistoryInfo() throws Exception {
			AhRestoreGetXML xmlParser = new AhRestoreGetXML();

			/**
			 * Check validation of acm_entitle_key_history_info.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile("acm_entitle_key_history_info");
			if (!restoreRet) {
				return null;
			}

			/**
			 * No one row data stored in acm_entitle_key_history_info table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			boolean isColPresent;
			String colName;
			AcmEntitleKeyHistoryInfo keyInfo;
			List<AcmEntitleKeyHistoryInfo> allInfo = new ArrayList<AcmEntitleKeyHistoryInfo>();
			
			for (int i = 0; i < rowCount; i++) {
				keyInfo = new AcmEntitleKeyHistoryInfo();
				/**
				 * Set entitlekey
				 */
				colName = "entitlekey";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				String entitlekey = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "";
				keyInfo.setEntitleKey(entitlekey);

				/**
				 * Set activetime
				 */
				colName = "activetime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				long activetime = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0;
				keyInfo.setActiveTime(activetime);
				
				/**
				 * Set numberofclients
				 */
				colName = "numberofclients";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				int clients = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
						colName)) : 0;
				keyInfo.setNumberOfClients(clients);
				
				/**
				 * Set ordertype
				 */
				colName = "ordertype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				int ordertype = isColPresent ? AhRestoreCommons.convertInt(xmlParser.getColVal(i,
						colName)) : 0;
				keyInfo.setOrderType(ordertype);
				
				/**
				 * Set domainname
				 */
				colName = "domainname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				String domainname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "";
				keyInfo.setDomainName(domainname);
				
				// whole hmol does not have license info
				if (NmsUtil.isHostedHMApplication() && HmDomain.HOME_DOMAIN.equals(domainname)) {
					continue;
				}
				
				/**
				 * Set supportstartdate
				 */
				colName = "supportstartdate";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				long startDate = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0;
				keyInfo.setSupportStartDate(startDate);
				
				/**
				 * Set supportenddate
				 */
				colName = "supportenddate";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				long endDate = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 0;
				keyInfo.setSupportEndDate(endDate);
				
				/**
				 * Set statusflag
				 */
				colName = "statusflag";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				short statusflag = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i,
						colName)) : AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL;
				keyInfo.setStatusFlag(statusflag);
				
				/**
				 * Set sendemail
				 */
				colName = "sendemail";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "acm_entitle_key_history_info",
						colName);
				boolean sendEmail = isColPresent && AhRestoreCommons.convertStringToBoolean(xmlParser.getColVal(i,colName));
				keyInfo.setSendEmail(sendEmail);
				
				allInfo.add(keyInfo);
			}

			return allInfo.isEmpty() ? null : allInfo;
		}
		// ---------------------- Order Key History Info -------------END---------

}