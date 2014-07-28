/**
 *@filename		LicenseOperationTool.java
 *@version
 *@author		Fiona
 *@createtime	Apr 10, 2009 10:51:52 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.license;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;

import com.ah.be.activation.BeActivationModule;
import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.rest.ahmdm.client.IResponseFromMDM;
import com.ah.be.rest.ahmdm.client.ResponseFromMDMImpl;
import com.ah.be.rest.ahmdm.model.AcmEntitleKeyModel;
import com.ah.be.rest.ahmdm.model.AcmResultModel;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.monitor.enrolledclient.tools.URLUtils;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class LicenseOperationTool {
	
	private static final Tracer	log	= new Tracer(LicenseOperationTool.class.getSimpleName());
	
	/**
	 * Get the used hours by encrypted string and systemId.
	 * @param encryptedHours : the original encrypted string of used hours
	 * @param systemId : the string of systemid
	 * @return int : the decrypted used hours
	 */
	public static int getDecryptedHours(String encryptedHours, String systemId) {
		/*
		 * the parameter is invalid
		 */
		if (null == encryptedHours || null == systemId || "".equals(encryptedHours) || "".equals(systemId)) {
			return -1;
		}
		
		/*
		 * decrypt the used hours
		 */
		AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(systemId);
		String strHours = encryptTool.decrypt(encryptedHours);
		// the string is invalid
		if (null == strHours || !strHours.endsWith(systemId)) {
			return -1;
		}
		return Integer.parseInt(strHours.substring(0, strHours
				.indexOf(systemId)));
	}
	
	/**
	 * Get the encrypted string by used hours and systemId.
	 * @param usedHours : the number of used hours
	 * @param systemId : the string of systemid
	 * @return String : the encrypted string of used hours
	 */
	public static String getEncryptedHours(int usedHours, String systemId) {
		if (usedHours < 0 || null == systemId || "".equals(systemId)) {
			return null;
		}
		AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(systemId);
		return encryptTool.encrypt(String.valueOf(usedHours)+systemId);
	}
	
	/**
	 * Get the active license information by system id from database.
	 *
	 * @param arg_SysId : the system id
	 * @param arg_Type -
	 * @return LicenseHistoryInfo
	 */
	public static LicenseHistoryInfo getActiveLicenseBySystemId(String arg_SysId, short arg_Type) {
		String where = "systemId = :s1 AND active = :s2 AND type = :s3";
		Object[] values = new Object[3];
		values[0] = arg_SysId;
		values[1] = true;
		values[2] = arg_Type;
		FilterParams filterPa = new FilterParams(where, values);
		List<LicenseHistoryInfo> bos = QueryUtil.executeQuery(LicenseHistoryInfo.class, new SortParams("id"), filterPa);
		if (bos.size() == 1) {
			return bos.get(0);
		}
		return null;
	}
	
	/**
	 * Compare the used hours of primary and secondary license.
	 *
	 * @param arg_SysIds : two system id;
	 * @param arg_MoreHours : 0 : check; 1 : license is in time
	 * @return int : left hours, there is something wrong with the license information if left hour is -1
	 */
	public static int updateTwoLicenseInfo(String[] arg_SysIds, int arg_MoreHours) {
		if (null == arg_SysIds || arg_SysIds.length != 2 || (arg_MoreHours != 0 && arg_MoreHours != 1)) {
			return -1;
		}
		
		// get primary license information from database
		LicenseHistoryInfo primary = getActiveLicenseBySystemId(arg_SysIds[0], LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER);
		
		// get secondary license information from database
		LicenseHistoryInfo secondary = getActiveLicenseBySystemId(arg_SysIds[1], LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER);
		int hours = -1;
		HM_License hm_l = HM_License.getInstance();
		if (primary != null && secondary != null
			&& hm_l.decrypt_from_string(arg_SysIds[0], primary.getLicenseString()).equals
			(hm_l.decrypt_from_string(arg_SysIds[1], secondary.getLicenseString()))) {
			List<LicenseHistoryInfo> allInfo = new ArrayList<>();
			String licenseKey = hm_l.decrypt_from_string(arg_SysIds[0], primary.getLicenseString());
			if (BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseKey.substring(0, BeLicenseModule.LICENSE_TYPE_INDEX))) {
				hours = 0;
			} else {
				// decrypt the primary used hours
				int priHours = getDecryptedHours(primary.getHoursUsed(), arg_SysIds[0]);
				
				// decrypt the secondary used hours
				int seconHours = getDecryptedHours(secondary.getHoursUsed(), arg_SysIds[1]);
				
				// set the less one is the same hours
				int lessHours = priHours != seconHours ? (priHours < seconHours ? priHours : seconHours) : priHours;
				
				/*
				 * get the left hours of this evaluation or vmware license
				 */
				int totalHours = Integer.parseInt(licenseKey
						.substring(BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX, BeLicenseModule.LICENSE_KEY_LENGTH));
				
				if (lessHours < totalHours) {
					if (priHours != seconHours || arg_MoreHours == 1) {
						
						/*
						 *  update the hour used in database
						 */
						primary.setHoursUsed(getEncryptedHours(lessHours+arg_MoreHours, arg_SysIds[0]));
						allInfo.add(primary);
						secondary.setHoursUsed(getEncryptedHours(lessHours+arg_MoreHours, arg_SysIds[1]));
						allInfo.add(secondary);					
					}
					if (!allInfo.isEmpty()) {
						/*
						 * update the two license information in database
						 */
						try {
							QueryUtil.bulkUpdateBos(allInfo);	
						} catch (Exception e) {
				        }	
					}			
					hours = totalHours-lessHours-arg_MoreHours;
				} else {
					hours = 0;
				}
			}
		}
		if (hours > -1) {
			if (arg_MoreHours == 1) {
				// change the license information in memory
				AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.setLeftHours(hours);
			}
		} else {
			AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO = null;
		}
		return hours;
	}
	
	/**
	 * Get the license info from the license key
	 * 
	 * @param arg_Key : the encrypted license key;
	 * @param arg_Hours : the encrypted string of used hours
	 * @param systemId -
	 * @return LicenseInfo
	 */
	public static LicenseInfo getLicenseInfoFromKey(String arg_Key, String arg_Hours, String systemId) {
		if (null == arg_Key || null == arg_Hours || "".equals(arg_Key) || "".equals(arg_Hours)) {
			return null;
		}
		LicenseInfo dto_License = null;
		String licenseKey = HM_License.getInstance().decrypt_from_string(systemId, arg_Key);
		try {
			// check the license if is right
			if (null != licenseKey
					&& (licenseKey.length() == BeLicenseModule.LICENSE_KEY_LENGTH
							|| licenseKey.length() == BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH
							|| licenseKey.length() == BeLicenseModule.LICENSE_KEY_ADD_ACTIVATION_LENGTH)) {
				dto_License = new LicenseInfo();
				
				// set license type
				String type = licenseKey.substring(0,
						BeLicenseModule.LICENSE_TYPE_INDEX);
				dto_License.setLicenseType(type);
				
				// set hiveAP or guess client number
				dto_License.setHiveAps(Integer.parseInt(licenseKey.substring(
						BeLicenseModule.LICENSE_TYPE_INDEX,
						BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX)));

				// the evaluation totals hours
				int hours = Integer.parseInt(licenseKey
								.substring(BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX, BeLicenseModule.LICENSE_KEY_LENGTH));
				dto_License.setTotalDays(hours/24);

				// evaluation or vmware license
				if ((BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(type) 
						|| BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(type))) {

					// the hours which is how long user can use
					int newHours = getDecryptedHours(arg_Hours, systemId);
					hours = newHours < hours ? (hours - newHours) : 0;
				}
				dto_License.setLeftHours(hours);
				
				// set the vhm number which the license supports
				if (BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(type)) {
					
					// vmware license supports hardcode 5 vhm
					dto_License.setVhmNumber(BeLicenseModule.VMWARE_LICENSE_SUPPORT_VHM_COUNT);
				} else {
					dto_License.setVhmNumber(licenseKey.length() == BeLicenseModule.LICENSE_KEY_LENGTH ? 1 : 
						Integer.parseInt(licenseKey.substring(BeLicenseModule.LICENSE_KEY_LENGTH, BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH)));
				}
				
				// set the flag of if use activation key to validate
				if (licenseKey.length() == BeLicenseModule.LICENSE_KEY_ADD_ACTIVATION_LENGTH) {
					dto_License.setUseActiveCheck(licenseKey.substring(BeLicenseModule.LICENSE_KEY_ADD_VHMNUMBER_LENGTH).equals(BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM));
				}
				
				// set system id
				dto_License.setSystemId(systemId);
			}
		} catch (Exception e) {
			log.error("getLicenseInfoFromKey() :", e);
			return null;
		}

		return dto_License;
	}
	
	/**
	 * Get the License information from database.
	 *
	 * @param licenseType -
	 * @param systemId -
	 * @return LicenseInfo
	 */
	public static LicenseInfo getLicenseInfoFromDatabase(short licenseType, String systemId) {
		LicenseInfo dto_License = null;
		try {
			/*
			 * the license information in database
			 */
			LicenseHistoryInfo activeLicense = getActiveLicenseBySystemId(systemId, licenseType);
			
			// get the license info from the decrypted license key
			if (null != activeLicense) {
				dto_License = getLicenseInfoFromKey(activeLicense.getLicenseString(), activeLicense.getHoursUsed(), systemId);
			}
		} catch (Exception e) {
			log.error("getLicenseInfoFromDatabase() :", e);
			return null;
		}

		return dto_License;
	}
	
	/**
	 * Get the order key object by different domain
	 * 
	 * @param domainName domain name
	 * @return the order key object
	 */
	public static LicenseInfo getOrderKeyInfoFromDatabase(String domainName) {
		return getOrderKeyInfoFromDatabase(domainName, true);
	}
	
	/**
	 * Get the order key object by different domain
	 * 
	 * @param domainName domain name
	 * @param ifNeedCreat if need create domain order key information
	 * @return the order key object
	 */
	public static LicenseInfo getOrderKeyInfoFromDatabase(String domainName, boolean ifNeedCreat) {
		// whole hmol does not have license info
		if (HmDomain.HOME_DOMAIN.equals(domainName) && NmsUtil.isHostedHMApplication()) {
			return null;
		}
		
		// get order key information
		DomainOrderKeyInfo domOrder = getDomainOrderKeyInfoForHa(domainName);
		
		LicenseInfo dto_License = new LicenseInfo();
		
		// get the domain
		HmDomain hmDom = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", domainName);
		
		// no order key has 30 days evaluation
		if (null == domOrder || (null == domOrder.getOrderKey() || "".equals(domOrder.getOrderKey()))) {
			// simple vhm
			if (!HmDomain.HOME_DOMAIN.equals(domainName) && !NmsUtil.isHostedHMApplication()) {
				
				dto_License.setLicenseType(BeLicenseModule.LICENSE_TYPE_SIMPLE_VHM);
				
				dto_License.setHiveAps(hmDom.getMaxApNum());
				
				dto_License.setVhmNumber(0);
				
				if (null != AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO) {
					// license type
					dto_License.setTotalDays(AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getTotalDays());
					dto_License.setLeftHours(AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getLeftHours());
				}
			} else {
				// license type
				dto_License.setLicenseType(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY);
				
				// get left hours
				int leftHours = BeActivationModule.ACTIVATION_KEY_GRACE_PERIOD*24;
				
				// has hours used information
				if (null != domOrder) {
					leftHours -= domOrder.getHoursUsedInt();
				} else {
					domOrder = new DomainOrderKeyInfo();
					domOrder.setDomainName(domainName);
					if (HmDomain.HOME_DOMAIN.equals(domainName)) {
						domOrder.setSystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					} else {
						domOrder.setSystemId(hmDom.getVhmID());
					}
					domOrder.setCreateTime(System.currentTimeMillis());
					
					if (ifNeedCreat && !HAUtil.isSlave()) {
						try {
							QueryUtil.createBo(domOrder);
						} catch (Exception e) {
							log.error("getOrderKeyInfoFromDatabase() : create dom entitlement key info", e);
				        }
					}
				}
				
				// total days
				dto_License.setTotalDays(BeActivationModule.ACTIVATION_KEY_GRACE_PERIOD);
				
				// evaluation HMOL
				if (leftHours <= 0 && NmsUtil.isDemoHHM() && !HmDomain.HOME_DOMAIN.equals(domainName)) {
					leftHours = 1;
				}
				
				// left hours
				dto_License.setLeftHours(leftHours);
				
				// vhm number
				dto_License.setVhmNumber(0);
				
				// hiveap number (100 HiveAPs for 30 days)
				dto_License.setHiveAps(leftHours <= 0 ? 0 : (HmDomain.HOME_DOMAIN.equals(domainName) ? 
					BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_AP_COUNT : BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_AP_COUNT_VHM));
				
				// CVG number (100 CVGs for 30 days)
				dto_License.setCvgNumber(leftHours <= 0 ? 0 : (HmDomain.HOME_DOMAIN.equals(domainName) ? 
					BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT : BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT_VHM));
				
				// create time
				dto_License.setCreateTime(domOrder.getCreateTime());
				
				// system id
				dto_License.setSystemId(domOrder.getSystemId());
			}
		} else {
			dto_License.setOrderKey(domOrder.getOrderKey());
			
			int[] orderInfo = domOrder.getOrderInfo();
			
			// license type
			dto_License.setLicenseType("0"+orderInfo[0]);
			
			// number of HiveAP
			dto_License.setHiveAps(orderInfo[1]);
			
			// number of CVG
			dto_License.setCvgNumber(orderInfo[4]);
			
			// default entitlement key
			// fix bug 12787
			if (DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(domOrder.getOrderKey()) && dto_License.getHiveAps() <= 0 && !HAUtil.isSlave()) {
				try {
					log.warn("getOrderKeyInfoFromDatabase() : remove dom entitlement key info, domain name is ("+domainName+"), demo key expired.");
					QueryUtil.bulkRemoveBos(DomainOrderKeyInfo.class, new FilterParams("domainName", domainName));
				} catch (Exception ex) {
					log.error("getOrderKeyInfoFromDatabase() : remove dom entitlement key info", ex);
				}
				
				return getOrderKeyInfoFromDatabase(domainName, true);
			}
			
			// number of VHM
			dto_License.setVhmNumber(orderInfo[2]);
			
			int evalDays = orderInfo[3];
			// evaluation license
			if (evalDays > 0) {
				dto_License.setTotalDays(evalDays);
				dto_License.setLeftHours(evalDays*24-domOrder.getHoursUsedInt());
				
				if (dto_License.getLeftHours() <= 0 && !HAUtil.isSlave()) {
					// update entitle key status
					if (!HmDomain.HOME_DOMAIN.equals(domainName) && NmsUtil.isHostedHMApplication()) {
						try {
							QueryUtil.updateBos(OrderHistoryInfo.class, "statusFlag = :s1, cvgStatusFlag = :s2", "orderKey = :s3", new Object[] { OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE, OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE, domOrder.getOrderKey() });
						} catch (Exception ex) {
							log.error("getOrderKeyInfoFromDatabase() : update entitlement key history info", ex);
						}
					}
				}
			}
			dto_License.setCreateTime(domOrder.getCreateTime());
			
			// system id
			dto_License.setSystemId(domOrder.getSystemId());
			
			if (dto_License.getHiveAps() == 0 && dto_License.getCvgNumber() == 0) {
				dto_License.setZeroDeviceKeyValid(null != getEntitlementKeyWithZeroDevice(domainName));
			}
		}
		return dto_License;
	}
	
	/**
	 * Get ACM entitlement key information
	 *
	 *@param String domain name
	 *
	 *@return String
	 */
	public static String getAcmEntitleKeyInfoStr(String domName) {
		if (StringUtils.isBlank(domName)) {
			return null;
		}
		String result = "Client Management License: ";
		// installed
		if (QueryUtil.findRowCount(AcmEntitleKeyHistoryInfo.class, new FilterParams("domainName", domName)) <= 0) {
			result += "<font color=\"red\">Not Installed</font>";
		} else {
			List<?> keyDate = QueryUtil.executeQuery("select supportEndDate from " + AcmEntitleKeyHistoryInfo.class.getSimpleName(), null, 
					new FilterParams("domainName = :s1 AND statusFlag = :s2", new Object[]{domName, AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL}));
			// the key is overdue
			if (keyDate.isEmpty()) {
				result += "<font color=\"red\">Expired</font>";
			// the key installed and valid
			} else {
				long spEndDate = (Long)keyDate.get(0);
				result += ("Installed valid to " + AhDateTimeUtil.getDateStrFromLongNoTimeZone(spEndDate));
			}
		}
		return result;
	}
	
	/**
	 * Send entitlement key to ACM
	 *
	 *@param AcmEntitleKeyModel
	 *@return true or false
	 */
	public static void sendEntitleKeyToACM(AcmEntitleKeyModel keyInfo) throws Exception {
		XStream xs = new XStream(new DomDriver());
		xs.processAnnotations(AcmEntitleKeyModel.class);		
		IResponseFromMDM res = new ResponseFromMDMImpl();
		try {
			// authorize
			ResponseModel resm = res.sendInfoToAcMWithAuth(ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL_ENTITLE_KEY, xs.toXML(keyInfo));
			if(HttpStatus.SC_OK == resm.getResponseCode()){
				String resmObj = resm.getResponseText();
				xs = new XStream(new DomDriver());
				xs.processAnnotations(AcmResultModel.class);
				AcmResultModel resMod = (AcmResultModel)xs.fromXML(resmObj);
				if (AcmResultModel.ACM_RESULT_STATUS_SUCCESS != Integer.valueOf(resMod.getStatusCode())) {
					log.error("sendEntitleKeyToACM()", resMod.getMessage());
					throw new Exception(resMod.getMessage());
				}
			} else {
				log.error("sendEntitleKeyToACM()", "HttpStatus.SC_OK != resm.getResponseCode()");
				throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.send.to.acm.res.exc"));
			}
		} catch (Exception e){
			log.error("sendEntitleKeyToACM()","Error when transEntitleKeyToACM",e);
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Get the customer id of this system or vhm
	 *
	 *@param vhm id or system id
	 *@return customer id
	 */
	public static String getCustomerIdFromRemote(String hmId) throws Exception {
		if (!StringUtils.isBlank(hmId)) {
			boolean isVhm = hmId.startsWith("VHM-");
			List<?> custIdList = QueryUtil.executeQuery("select customerId from "+CloudAuthCustomer.class.getSimpleName(), null, new FilterParams(isVhm?"owner.vhmID":"owner.domainName", isVhm?hmId:HmDomain.HOME_DOMAIN));
			String customerId = null;
			if (!custIdList.isEmpty()) {
				customerId = (String)custIdList.get(0);
			}
			if (StringUtils.isBlank(customerId)) {
				if (isVhm) {
					// get customer id from portal
					VHMCustomerInfo vhmCustomer = ClientUtils.getPortalResUtils().getVHMCustomerInfo(hmId);
					if (null != vhmCustomer && !StringUtils.isBlank(vhmCustomer.getCustomerId())) {
						return vhmCustomer.getCustomerId();
					} else {
						log.error("getCustomerIdFromRemote()","hm id is "+hmId+ ", no customer id");
						throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.no.customer.id.vhm", hmId));
					}
				} else {
					log.error("getCustomerIdFromRemote()","hm id is "+hmId+", no customer id");
					// customer id does not exist, need retrieve from IDM
					throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.no.customer.id.hm"));
				}
			} else {
				return (String)custIdList.get(0);
			}
		}
		log.error("getCustomerIdFromRemote()","hm id is null");
		return null;
	}
	
	/**
	 * Get the entitlement key info that support 0 device
	 *
	 *@param domain name
	 *
	 *@return OrderHistoryInfo
	 */
	public static OrderHistoryInfo getEntitlementKeyWithZeroDevice(String domName) {
		if (StringUtils.isBlank(domName)) {
			return null;
		}
		List<OrderHistoryInfo> deviceKey = QueryUtil.executeQuery(OrderHistoryInfo.class, new SortParams("activeTime", false), 
				new FilterParams("numberOfAps = :s1 AND numberOfCvgs = :s1 AND domainName = :s2 AND statusFlag = :s3 AND cvgStatusFlag = :s3",
						new Object[]{0, domName, OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL}), 1);
		if (!deviceKey.isEmpty()) {
			return deviceKey.get(0);
		}
		return null;
	}
	
	/**
	 * Get the domain order key info from database, there are two records in HA for home domain
	 *
	 *@param vhmName
	 *
	 *@return DomainOrderKeyInfo
	 */
	public static DomainOrderKeyInfo getDomainOrderKeyInfoForHa(String vhmName) {
		DomainOrderKeyInfo hmDom = null;
		if (!StringUtils.isBlank(vhmName)) {
			List<DomainOrderKeyInfo> domList = QueryUtil.executeQuery(DomainOrderKeyInfo.class, new SortParams("id", false), new FilterParams("domainName", vhmName));
			if (null != domList && !domList.isEmpty()) {
				if (domList.size() > 1) {
					if (HmDomain.HOME_DOMAIN.equals(vhmName)) {
						for (DomainOrderKeyInfo domInfo : domList) {
							if (BeLicenseModule.HIVEMANAGER_SYSTEM_ID.equals(domInfo.getSystemId())) {
								hmDom = domInfo;
								break;
							}
						}
					} else {
						hmDom = domList.get(0);
					}
				} else {
					hmDom = domList.get(0);
				}
			}
		}
		return hmDom;
	}

}