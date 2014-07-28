package com.ah.be.license;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.activation.BeActivationModule;
import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.NmsUtil;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.ls.data.QueryLicenseInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.be.rest.ahmdm.model.AcmEntitleKeyModel;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class OrderKeyManagement {

	private static final Log log = LogFactory.getLog("commonlog.OrderKeyManagement");

	public static void installOrderKey() throws Exception {

	}

	private static int getHmType(String domainName) {
		int hmType = 0x00;
		
		if(domainName.equalsIgnoreCase(HmDomain.HOME_DOMAIN))
		{
			hmType |= CommConst.HM_TYPE1_WHOLEHM;
		}
		else
		{
			hmType |= CommConst.HM_TYPE1_VHM;
		}
		
		//hm or hmol
		if(NmsUtil.isHostedHMApplication())
		{
			hmType |= CommConst.HM_TYPE2_HMOL;
		}
		else
		{
			//1u 2u or vm
			if (HM_License.getInstance().isVirtualMachineSystem())
			{
				hmType |= CommConst.HM_TYPE2_VM;
			}else
			{
				if(is1U())
				{
					hmType |= CommConst.HM_TYPE2_1U;
				}
				else
				{
					hmType |= CommConst.HM_TYPE2_2U;
				}
			}
		}	
		
		return hmType;
	}
	
	private static boolean is1U()
	{
		List<String> strRsltList;
	    	
    	String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/ahGetPcModel.sh"};		
		
		strRsltList = BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
		
		if(null == strRsltList || strRsltList.isEmpty())
    	{
    		DebugUtil.adminDebugWarn(
			"BeOperateHMCentOSImpl.getHmKernelModel() no return could not charge");	    
    		
    		return true;    		
    	}
		
		String strRslt = strRsltList.get(0);

		return "1U".equalsIgnoreCase(strRslt);
	}

	public static void activateOrderKey(String orderKey, String domainName, String hmId)
			throws Exception {
		try {
			// record the total information
			// get order key information
			DomainOrderKeyInfo currentDom = LicenseOperationTool.getDomainOrderKeyInfoForHa(domainName);
			
			// compare the type of new order key and current one
			String currentType;
			int currentApNum = 0;
			int currentVhmNum = 0;
			int currentCvgNum = 0;
			boolean currentDef = false;
			if (HmDomain.HOME_DOMAIN.equals(domainName)) {
				currentType = AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getLicenseType();
				currentApNum = AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getHiveAps();
				currentVhmNum = AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getVhmNumber();
				currentCvgNum = AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getCvgNumber();
			} else {
				LicenseInfo vhmLsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domainName);
				if (null != vhmLsInfo) {
					if (DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(vhmLsInfo.getOrderKey())) {
						currentType = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY;
						currentDef = true;
					} else {
						currentType = vhmLsInfo.getLicenseType();
					}
					currentApNum = vhmLsInfo.getHiveAps();
					currentCvgNum = vhmLsInfo.getCvgNumber();
				} else if (null != currentDom){
					int[] lsInfo = currentDom.getOrderInfo();
					currentType = "0"+lsInfo[0];
					if (DomainOrderKeyInfo.DEFAULT_ORDER_KEY.equals(currentDom.getOrderKey())) {
						currentType = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY;
						currentDef = true;
					}
					currentApNum = lsInfo[1];
					currentCvgNum = lsInfo[4];
				} else {
					currentType = BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY;
				}
			}
			
			if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(currentType)) {
				currentApNum = 0;
				currentVhmNum = 0;
				currentCvgNum = 0;
			}
			
			QueryLicenseInfo orderLicenseInfo = ClientSenderCenter.initOrderKeyAndQueryLicenseInfoNew(
					getHmType(domainName), hmId, orderKey, NmsUtil.isHMForOEM() ? (currentType+"oem") : currentType, currentApNum, currentVhmNum, currentCvgNum);
			int lsType = orderLicenseInfo.getLicenseType();
			
			// ACM entitle key
			if (BeLicenseModule.ACM_LICENSE_TYPE == lsType) {
				try {
					// check the vhm mode, express mode does not support Client Manager
					List<?> modes = QueryUtil.executeQuery("select modeType from " + HmStartConfig.class.getSimpleName(), null,
							new FilterParams("owner.domainName", domainName));
					if (!modes.isEmpty()) {
						if (HmStartConfig.HM_MODE_EASY == (Short)modes.get(0)) {
							throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.express.mode"));
						}
					}
					orderLicenseInfo.setManageLicense(orderKey);
					dealwithAcmEntitleKey(orderLicenseInfo, hmId, domainName);
					return;
				} catch (Exception ex) {
					// the current entitle key
					List<?> entitleInfo = QueryUtil.executeQuery("select entitleKey from "+AcmEntitleKeyHistoryInfo.class.getSimpleName(), 
							null, new FilterParams("statusFlag = :s1 AND domainName = :s2", new Object[]{AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL, domainName}));
					if (!entitleInfo.isEmpty()) {
						orderKey += ("/"+entitleInfo.get(0));
					}
					// send the check failed info to license server
					ClientSenderCenter.sendEntitlementKey(orderKey);
					throw new Exception(ex.getMessage());
				}
			}

			OrderHistoryInfo orderInfo;
			// the entitlement key already exist in database
			List<OrderHistoryInfo> existKey = QueryUtil.executeQuery(OrderHistoryInfo.class, null, new FilterParams("orderKey = :s1 AND domainName = :s2", 
				new Object[]{orderKey, domainName}));
			boolean orderExist = false;
			if (existKey.isEmpty()) {
				orderInfo = new OrderHistoryInfo();
			} else {
				orderExist = true;
				orderInfo = existKey.get(0);
			}
			orderInfo.setOrderKey(orderKey);
			orderInfo.setActiveTime(System.currentTimeMillis());
			if (1 == lsType) {
				// evaluation order key
				orderInfo.setLicenseType(BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM);
				orderInfo.setNumberOfEvalValidDays(orderLicenseInfo.getNumberOfEvalValidDays());
			} else if (3 == lsType || 10 == lsType) {
				// permanent order key
				orderInfo.setLicenseType(3 == lsType ? BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM : BeLicenseModule.LICENSE_TYPE_RENEW_NUM);
				
				// subscription end date
				if (null != orderLicenseInfo.getSubEndDate() && !"".equals(orderLicenseInfo.getSubEndDate()) && orderLicenseInfo.getNumberOfAp() > 0) {
					orderInfo.setSubEndDate(Long.parseLong(orderLicenseInfo.getSubEndDate()));
				}
				// support end date
				if (null != orderLicenseInfo.getSupportEndDate() && !"".equals(orderLicenseInfo.getSupportEndDate())) {
					orderInfo.setSupportEndDate(Long.parseLong(orderLicenseInfo.getSupportEndDate()));
				}
				// CVG subscription end date
				if (null != orderLicenseInfo.getCvgSubEndDate() && !"".equals(orderLicenseInfo.getCvgSubEndDate()) && orderLicenseInfo.getNumberOfCvg() > 0) {
					orderInfo.setCvgSubEndDate(Long.parseLong(orderLicenseInfo.getCvgSubEndDate()));
				}
			} else {
				throw new Exception(MgrUtil.getUserMessage("error.license.orderkey.type"));
			}

//			if (10 == lsType && HmDomain.HOME_DOMAIN.equals(domainName)) {
//				orderInfo.setNumberOfAps(0);
//				orderInfo.setNumberOfCvgs(0);
//				orderInfo.setNumberOfVhms(0);
//			} else {
			orderInfo.setNumberOfAps(orderLicenseInfo.getNumberOfAp());
			orderInfo.setNumberOfCvgs(orderLicenseInfo.getNumberOfCvg());
			orderInfo.setNumberOfVhms(orderLicenseInfo.getNumberOfVhm());
			//}
			
			// check ap number of renew
			// treat renew license as a new license from Gotham 2014/2/12
//			int[] renewNumber = new int[2];
			//List<HmBo> updateBo = new ArrayList<>();
//			if (10 == lsType && !HmDomain.HOME_DOMAIN.equals(domainName) && (orderInfo.getNumberOfAps() > 0 || orderInfo.getNumberOfCvgs() > 0)) {
//				renewNumber = changeRenewNumber(domainName, orderLicenseInfo.getNumberOfAp(), orderInfo.getNumberOfCvgs(), updateBo);
//				
//				if (renewNumber[0] < 0) {
//					// send the check failed info to license server
//					ClientSenderCenter.sendEntitlementKey(orderKey);
//					throw new Exception(MgrUtil.getUserMessage("error.license.orderkey.renew.hiveap.number"));
//				}
//			}
			
			orderInfo.setDomainName(domainName);

			boolean addAp = true;
			if (currentDef) {
				addAp = false;
			}
			
			boolean domExist = null != currentDom;
			int apNumber = orderLicenseInfo.getNumberOfAp();
			int vhmNumber = orderLicenseInfo.getNumberOfVhm();
			int cvgNumber = orderLicenseInfo.getNumberOfCvg();
			if (!domExist) {
				currentDom = new DomainOrderKeyInfo();
			} else if (!HmDomain.HOME_DOMAIN.equals(domainName)) {
				int[] oldOnes = currentDom.getOrderInfo();
				if (addAp) {
					apNumber += oldOnes[1];
					cvgNumber += oldOnes[4];
				}
			}
			if (HmDomain.HOME_DOMAIN.equals(domainName) && 
				!BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getLicenseType())) {
				if (addAp) {
					apNumber += AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getHiveAps();
					vhmNumber += AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getVhmNumber();
					cvgNumber += AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getCvgNumber();
				}
			}
			
			// check ap, vhm or cvg number
			if (String.valueOf(apNumber).length() > 7 || String.valueOf(vhmNumber).length() > 4 || String.valueOf(cvgNumber).length() > 5) {
				// send the check failed info to license server
				ClientSenderCenter.sendEntitlementKey(orderKey);
				throw new Exception(MgrUtil.getUserMessage("error.license.orderkey.device.total.number"));
			}
			
			LicenseInfo newLicense = new LicenseInfo();

			// order key
			newLicense.setOrderKey(orderKey);

			// number of HiveAP
			newLicense.setHiveAps(apNumber);
			
			// number of CVG
			newLicense.setCvgNumber(cvgNumber);

			// number of VHM
			newLicense.setVhmNumber(vhmNumber);

			// license type
			if (BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(currentType)) {
				newLicense.setLicenseType(BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM);
			} else {
				newLicense.setLicenseType(1 == lsType ? BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM : BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM);
			}
			
			// system id
			newLicense.setSystemId(hmId);

			currentDom.setDomainName(domainName);
			
			// set system id
			currentDom.setSystemId(hmId);
			currentDom.setOrderKey(orderKey);
			currentDom.setCreateTime(System.currentTimeMillis());
			currentDom.setHoursUsed(currentDom.getEncriptString(0));
			currentDom.setEncryptLicense(newLicense.getLicenseType(), apNumber, vhmNumber, orderInfo
					.getNumberOfEvalValidDays(), cvgNumber);
			
			try {
				// domain entitlement key current info
				if (domExist) {
					QueryUtil.updateBo(currentDom);
				} else {
					QueryUtil.createBo(currentDom);
				}
				// entitlement key history info 
				if (orderExist) {
					QueryUtil.updateBo(orderInfo);
				} else {
					QueryUtil.createBo(orderInfo);
				}
			} catch (Exception e) {
				log.error("activateOrderKey() : do order key db info record!", e);
				ClientSenderCenter.sendEntitlementKey(orderKey);
				throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.license.orderkey.db.operation"));
			}

			int evalDays = orderInfo.getNumberOfEvalValidDays();
			// evaluation license
			if (evalDays > 0) {
				newLicense.setTotalDays(evalDays);
				newLicense.setLeftHours(evalDays * 24);
			}

			newLicense.setCreateTime(currentDom.getCreateTime());

			// update the whole system license information
			if (HmDomain.HOME_DOMAIN.equals(domainName)) {
				// update information in database
				QueryUtil.updateBos(LicenseHistoryInfo.class, "active = :s1", "active = :s2 and type = :s3", new Object[] { false, true, LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER } );
				
				// must input user manager license
				if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getLicenseType())
					&& null == HmBeLicenseUtil.GM_LITE_LICENSE_INFO) {
					HmBeLicenseUtil.GM_LITE_LICENSE_VALID = false;
				}

				// set new license information in session
				AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO = newLicense;
				HmBeLicenseUtil.LICENSE_TIMER_OBJ.stopEvaluationTimer();

				/*
				 * start new timer
				 */
				HmBeLicenseUtil.LICENSE_TIMER_OBJ.permanentEntitleKeyTimer();

				// restart the activation key and collection timer
				HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_VALID;
				HmBeActivationUtil.ACTIVATION_KEY_VALID = true;
				
				// does not use activation key
				QueryUtil.bulkRemoveBos(ActivationKeyInfo.class, null);
				
				// for check order key in period
				if (HM_License.getInstance().isVirtualMachineSystem() && !NmsUtil.isHostedHMApplication()) {
					ActivationKeyInfo activation = new ActivationKeyInfo();
					
					// set order key
					activation.setActivationKey(orderKey);
					
					// set the default value
					activation.setSystemId(hmId);
					activation.setQueryPeriod(15);
					QueryUtil.createBo(activation);
				}
			} else {
				HmBeLicenseUtil.VHM_ORDERKEY_INFO.put(domainName, newLicense);
				
				// remove the entitle key from portal
				try {
					QueryUtil.removeBos(OrderHistoryInfo.class, new FilterParams("licenseType = :s1 AND domainName = :s2", 
						new Object[]{BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY, domainName}));
				} catch (Exception ex) {
					log.error("activateOrderKey() : remove entitlement key from portal", ex);
				}
			}
			
			// deal with user information
			if (null != orderLicenseInfo.getUserRegInfo() && !NmsUtil.isHMForOEM()) {
				// remove the old user register info in database
				try {
					QueryUtil.removeBos(UserRegInfoForLs.class, new FilterParams("owner.domainName", domainName));
					// create new user register info in database
					orderLicenseInfo.getUserRegInfo().setOwner(QueryUtil.findBoByAttribute(HmDomain.class, "domainName", domainName));
					QueryUtil.createBo(orderLicenseInfo.getUserRegInfo());
				} catch (Exception e) {
					log.error("create register user info failed for domain : "+domainName+" error : "+e.getMessage());
				}
			}

			// deal with user manager license
			if (orderLicenseInfo.isUserManagerLicenseExistFlag()) {
				String umLicense = orderLicenseInfo.getManageLicense();

				HM_License hm_l = HM_License.getInstance();

				// select all the license history info
				String where = "systemId = :s1 AND licenseString = :s2 AND type = :s3 AND active = :s4";
				Object[] values = new Object[4];
				values[0] = hmId;
				values[1] = umLicense;
				values[2] = LicenseHistoryInfo.LICENSE_TYPE_GM_LITE;
				values[3] = true;
				List<?> boIds = QueryUtil.executeQuery("select id from " + LicenseHistoryInfo.class.getSimpleName(), null,
						new FilterParams(where, values));

				// cannot import the same license as before
				if (!boIds.isEmpty()) {
					return;
				}

				String decLicense = hm_l.decrypt_from_string(hmId, umLicense);

				// check the license key
				if (null == decLicense || decLicense.length() != BeLicenseModule.LICENSE_KEY_LENGTH) {
					throw new Exception(MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "user manager"));
				}

				if (!BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM.equals(decLicense.substring(0,
						BeLicenseModule.LICENSE_TYPE_INDEX))) {
					throw new Exception(MgrUtil.getUserMessage("error.licenseFailed.key.invalid", "user manager"));
				}

				LicenseInfo newLicenseInfo = new LicenseInfo();
				newLicenseInfo.setLicenseType(BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM);
				newLicenseInfo.setHiveAps(Integer.parseInt(decLicense.substring(
						BeLicenseModule.LICENSE_TYPE_INDEX,
						BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX)));

				LicenseHistoryInfo historyInfo = new LicenseHistoryInfo();
				historyInfo.setActive(true);
				historyInfo.setSystemId(hmId);
				historyInfo.setLicenseString(umLicense);
				AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(hmId);
				historyInfo.setHoursUsed(encryptTool.encrypt("0" + hmId));
				historyInfo.setType(LicenseHistoryInfo.LICENSE_TYPE_GM_LITE);

				// update information in database
				QueryUtil.updateBos(LicenseHistoryInfo.class, "active = :s1", "active = :s2 and type = :s3", new Object[] { false, true, LicenseHistoryInfo.LICENSE_TYPE_GM_LITE });
				QueryUtil.createBo(historyInfo);

				// does not exist GM license before
				if (!HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
					// insert default user management group
					HmBeParaUtil.insertDefaultGMUserGroup();
				}
				HmBeLicenseUtil.GM_LITE_LICENSE_INFO = newLicenseInfo;
				HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
			}
		} catch (Exception e) {
			log.error("activateOrderKey failed!", e);
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Deal with the ACM entitle key info from license server.
	 *
	 *@param key info; system id or vhm id; domain name
	 *
	 */
	private static void dealwithAcmEntitleKey(QueryLicenseInfo keyInfo, String hmId, String domName) throws Exception {
		if (null != keyInfo && BeLicenseModule.ACM_LICENSE_TYPE == keyInfo.getLicenseType()) {
			// send the key info to ACM
			AcmEntitleKeyModel keyMdInfo = new AcmEntitleKeyModel();
			String custId = LicenseOperationTool.getCustomerIdFromRemote(hmId);
			keyMdInfo.setCustomerId(custId);
			// the field of manage license is ACM entitle key
			String titleKey = keyInfo.getManageLicense();
			keyMdInfo.setEntitlementKey(titleKey);
			keyMdInfo.setHmId(hmId);
			// the field of number of ap is number of client
			keyMdInfo.setNumOfClients(String.valueOf(keyInfo.getNumberOfAp()));
			// the field of number of vhm is order type
			keyMdInfo.setOrderType(String.valueOf(keyInfo.getNumberOfVhm()));
			// the field of sub end date is support start date
			keyMdInfo.setStartDate(keyInfo.getSubEndDate());
			keyMdInfo.setEndDate(keyInfo.getSupportEndDate());
			
			// the field of cvg sub end date is sales order number
			keyMdInfo.setSalesOrder(keyInfo.getCvgSubEndDate());
			
			// hm version
			String hmversion = "6.1r3";
			BeVersionInfo verInfo = NmsUtil.getVersionInfo();
			if (null != verInfo) {
				hmversion = verInfo.getMainVersion() + "r" + verInfo.getSubVersion();
			}
			keyMdInfo.setHmVersion(hmversion);
			try {
				// send the entitle key info to ACM
				LicenseOperationTool.sendEntitleKeyToACM(keyMdInfo);
				
				// insert this entitle key to database
				AcmEntitleKeyHistoryInfo entitleInfo = QueryUtil.findBoByAttribute(AcmEntitleKeyHistoryInfo.class, "entitleKey", titleKey);
				if (null == entitleInfo) {
					entitleInfo = new AcmEntitleKeyHistoryInfo();
				}
				entitleInfo.setEntitleKey(titleKey);
				entitleInfo.setActiveTime(System.currentTimeMillis());
				entitleInfo.setDomainName(domName);
				entitleInfo.setNumberOfClients(keyInfo.getNumberOfAp());
				entitleInfo.setOrderType(keyInfo.getNumberOfVhm());
				entitleInfo.setStatusFlag(AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL);
				entitleInfo.setSupportStartDate(Long.parseLong(keyInfo.getSubEndDate()));
				entitleInfo.setSupportEndDate(Long.parseLong(keyInfo.getSupportEndDate()));
				entitleInfo.setOrderNumber(keyInfo.getCvgSubEndDate());
				try {
					// insert into database
					if (null == entitleInfo.getId()) {
						QueryUtil.createBo(entitleInfo);
					} else {
						QueryUtil.updateBo(entitleInfo);
					}
					// update other key's status to disabled
					QueryUtil.updateBos(AcmEntitleKeyHistoryInfo.class, "statusFlag = :s1", 
							"domainName = :s2 AND statusFlag = :s3 AND entitleKey != :s4", 
							new Object[]{AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_DISABLE, domName,
						AcmEntitleKeyHistoryInfo.ENTITLE_KEY_STATUS_NORMAL, titleKey});
				} catch (Exception ex) {
					log.error("dealwithAcmEntitleKey() : insert ACM entitle key to database ", ex);
					throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.insert.db"));
				}
			} catch (Exception ex) {
				log.error("dealwithAcmEntitleKey() : send entitle key to ACM ", ex);
				throw new Exception(MgrUtil.getUserMessage("glasgow_14.error.acm.entitlement.key.send.to.acm"));
			}
		} else {
			throw new Exception(MgrUtil.getUserMessage("error.license.orderkey.type"));
		}
	}

	/**
	 * check the renew number
	 *
	 * @param domainName domain name
	 * @param renewAp renew ap number
	 * @param renewCvg renew cvg number
	 * @param updateBo -
	 * @return need add ap and cvg number to memory
	 */
//	private static int[] changeRenewNumber(String domainName, int renewAp, int renewCvg, List<HmBo> updateBo) {
//		// select the entitle key history info overdue
//		String sqlStr = "domainName = :s1 AND (licenseType = :s2 OR licenseType = :s3) AND (statusFlag = :s4 OR cvgStatusFlag = :s5)";
//		List<Object> parameters = new ArrayList<>();
//		parameters.add(domainName);
//		parameters.add(BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM);
//		parameters.add(BeLicenseModule.LICENSE_TYPE_RENEW_NUM);
//		parameters.add(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
//		parameters.add(OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE);
//		
//		List<OrderHistoryInfo> historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, new SortParams("activeTime"), 
//				new FilterParams(sqlStr, parameters.toArray()));
//		
//		int[] resultInt = new int[2];
//		resultInt[0] = -1;
//		resultInt[1] = -1;
//		
//		int totalRenew = 0;
//		int totalRenewCvg = 0;
//		int addAp = 0;
//		int addCvg = 0;
//		Map<String, OrderHistoryInfo> mapOrderKey = new HashMap<>();
//		
//		// renew any simple overdue record
//		for (OrderHistoryInfo orderInfo : historyInfo) {
//			if (OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE == orderInfo.getStatusFlag()) {
//				if (renewAp == orderInfo.getNumberOfAps()) {
//					totalRenew = renewAp;
//					addAp = renewAp;
//					
//					OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//					if (null == mapKeyInfo) {
//						orderInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//					} else {
//						mapKeyInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//					}
//				}
//			}
//			if (OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE == orderInfo.getCvgStatusFlag()) {
//				if (renewCvg == orderInfo.getNumberOfCvgs()) {
//					totalRenewCvg = renewCvg;
//					addCvg = renewCvg;
//					
//					OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//					if (null == mapKeyInfo) {
//						orderInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//					} else {
//						mapKeyInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//					}
//				}
//			}
//		}
//		
//		if (renewAp != totalRenew || renewCvg != totalRenewCvg) {
//			for (OrderHistoryInfo orderInfo : historyInfo) {
//				// renew HiveAP
//				if (totalRenew < renewAp && OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE == orderInfo.getStatusFlag()) {
//					totalRenew += orderInfo.getNumberOfAps();
//					
//					if (renewAp < totalRenew) {
//						return resultInt;
//					} else {
//						addAp += orderInfo.getNumberOfAps();
//						
//						orderInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//						if (null == mapKeyInfo) {
//							mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//						} else {
//							mapKeyInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						}
//					}
//				}
//				
//				// renew CVG
//				if (totalRenewCvg < renewCvg && OrderHistoryInfo.ENTITLE_KEY_STATUS_OVERDUE == orderInfo.getCvgStatusFlag()) {
//					totalRenewCvg += orderInfo.getNumberOfCvgs();
//					
//					if (renewCvg < totalRenewCvg) {
//						return resultInt;
//					} else {
//						addCvg += orderInfo.getNumberOfCvgs();
//						
//						orderInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//						if (null == mapKeyInfo) {
//							mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//						} else {
//							mapKeyInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						}
//					}
//				}
//			}
//		}
//		
//		if (totalRenew < renewAp) {
//			// normal permanent entitle key
//			SortParams sortPara = new SortParams("subEndDate");
//			
//			sqlStr = "domainName = :s1 AND (licenseType = :s2 OR licenseType = :s3) AND statusFlag = :s4";
//			
//			parameters = new ArrayList<>();
//			parameters.add(domainName);
//			parameters.add(BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM);
//			parameters.add(BeLicenseModule.LICENSE_TYPE_RENEW_NUM);
//			parameters.add(OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL);
//			historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, sortPara, new FilterParams(sqlStr, parameters.toArray()));
//			
//			for (OrderHistoryInfo orderInfo : historyInfo) {
//				if (orderInfo.getNumberOfAps() == 0)
//					continue;
//				if (totalRenew < renewAp) {
//					totalRenew += orderInfo.getNumberOfAps();
//					if (renewAp < totalRenew) {
//						return resultInt;
//					} else {
//						orderInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//						if (null == mapKeyInfo) {
//							mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//						} else {
//							mapKeyInfo.setStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						}
//					}
//				} else {
//					break;
//				}
//			}
//		}
//		
//		if (totalRenewCvg < renewCvg) {
//			// normal permanent entitle key
//			SortParams sortPara = new SortParams("cvgSubEndDate");
//			
//			sqlStr = "domainName = :s1 AND (licenseType = :s2 OR licenseType = :s3) AND cvgStatusFlag = :s4";
//			
//			parameters = new ArrayList<>();
//			parameters.add(domainName);
//			parameters.add(BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM);
//			parameters.add(BeLicenseModule.LICENSE_TYPE_RENEW_NUM);
//			parameters.add(OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL);
//			historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, sortPara, new FilterParams(sqlStr, parameters.toArray()));
//			
//			for (OrderHistoryInfo orderInfo : historyInfo) {
//				if (orderInfo.getNumberOfCvgs() == 0)
//					continue;
//				if (totalRenewCvg < renewCvg) {
//					totalRenewCvg += orderInfo.getNumberOfCvgs();
//					if (renewCvg < totalRenewCvg) {
//						return resultInt;
//					} else {
//						orderInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						OrderHistoryInfo mapKeyInfo = mapOrderKey.get(orderInfo.getOrderKey());
//						if (null == mapKeyInfo) {
//							mapOrderKey.put(orderInfo.getOrderKey(), orderInfo);
//						} else {
//							mapKeyInfo.setCvgStatusFlag(OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE);
//						}
//					}
//				} else {
//					break;
//				}
//			}
//		}
//		
//		if (renewAp != totalRenew || renewCvg != totalRenewCvg) {
//			return resultInt;
//		} else {
//			if (!mapOrderKey.isEmpty()) {
//				updateBo.addAll(mapOrderKey.values());
//			}
//			resultInt[0] = addAp;
//			resultInt[1] = addCvg;
//			return resultInt;
//		}
//	}

	public static void updateUserManagerLicenseFromLs() throws Exception {
		try {
			// do process
		} catch (Exception e) {
			log.error("updateUserManagerLicenseFromLs failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	public static void checkValidityOfVmhm(ActivationKeyInfo arg_Info) {
		log.info("jtest..checkValidityOfVmhm..start");
		try {
			if (null == arg_Info) {
				log.warn("There is no entitlement key currently!! ignore the check");
				return;
			}
			String systemId = arg_Info.getSystemId();
			String currentOrderKey = arg_Info.getActivationKey();

			if (null == currentOrderKey || "".equals(currentOrderKey)) {
				log.warn("There is no entitlement key currently!! ignore the check");
				return;
			}

			boolean success = ClientSenderCenter.checkVmhmValidation(systemId, currentOrderKey);
			
			// valid response
			arg_Info.setStartRetryTimer(false);
			arg_Info.setHasRetryTime((byte)0);

			if (!success) {
				arg_Info.setActivateSuccess(false);
				dealwithInvalidEntitlementKey(currentOrderKey);
			} else {
				arg_Info.setActivateSuccess(true);
				arg_Info.initTheUsedHours();
			}
		} catch (Exception e) {
			if (arg_Info.getHasRetryTime() < arg_Info.getQueryRetryTime()) {
				arg_Info.setHasRetryTime((byte)(arg_Info.getHasRetryTime()+1));
				arg_Info.setStartRetryTimer(true);
				arg_Info.initTheUsedHours();
			} else {
				log.error("checkValidityOfVmhm failed! ", e);
				arg_Info.setStartRetryTimer(false);
				arg_Info.setHasRetryTime((byte)0);
				arg_Info.setActivateSuccess(false);
				dealwithInvalidEntitlementKey(arg_Info.getActivationKey());
			}
		}
		log.info("jtest..checkValidityOfVmhm..end");
	}
	
	private static void dealwithInvalidEntitlementKey(String currentOrderKey) {
		log.error("check failed! there is something wrong with this entitlement key from update server!");

		// remove entitlement key in history
		try {
			log.warn("remove all entitlement key history of this standalone HM");
			QueryUtil.bulkRemoveBos(OrderHistoryInfo.class, new FilterParams("domainName", HmDomain.HOME_DOMAIN));
		} catch (Exception e) {
			log.error("remove entitlement key " + currentOrderKey + " history info failed!");
		}
		
		HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
				HmSystemLog.FEATURE_ADMINISTRATION, "entitlement key " + currentOrderKey
						+ " has been removed because it has been abused from update server!");

		// invalid order key in memory
		HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.NO_LICENSE_MUST_INPUT;

		// remove this entitlement key
		try {
			log.warn("remove dom entitlement key of this standalone HM");
			QueryUtil.bulkRemoveBos(DomainOrderKeyInfo.class, new FilterParams("domainName", HmDomain.HOME_DOMAIN));
		} catch (Exception e) {
			log.error("remove entitlement key " + currentOrderKey + " current info failed!");
		}
		
		// create bo info
		DomainOrderKeyInfo domInfo = new DomainOrderKeyInfo();
		domInfo.setDomainName(HmDomain.HOME_DOMAIN);
		domInfo.setCreateTime(System.currentTimeMillis());
		domInfo.setSystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
		domInfo.setHoursUsed(domInfo.getEncriptString(BeActivationModule.ACTIVATION_KEY_GRACE_PERIOD*24));
		// create new domain current entitlement key info
		try {
			QueryUtil.createBo(domInfo);
		} catch (Exception e) {
			log.error("remove entitlement key " + currentOrderKey + " current info failed!");
		}

		// change the order key information in memory
		AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO = LicenseOperationTool
				.getOrderKeyInfoFromDatabase(HmDomain.HOME_DOMAIN);
	}

}