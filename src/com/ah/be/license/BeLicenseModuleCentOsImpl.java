/**
 *@filename		BeLicenseModuleCentOsImpl.java
 *@version		v1.19
 *@author		Fiona
 *@createtime	2007-9-3 02:05:58 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.license;

import java.util.List;

import com.ah.be.activation.BeActivationModule;
import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;

/**
 * @author Fiona
 * @version v1.19
 */
public class BeLicenseModuleCentOsImpl extends BaseModule implements
		BeLicenseModule {
	
	public final boolean ifRestore = null == AhAppContainer.HmBe;

	/**
	 * Constructor
	 */
	public BeLicenseModuleCentOsImpl() {
		setModuleId(BaseModule.ModuleID_License);
		setModuleName("BeLicenseModule");
	}

	/**
	 * Start license scheduler
	 */
	@Override
	public boolean run() {
		HmBeLicenseUtil.LICENSE_TIMER_OBJ = new AeroLicenseTimer();
		HmBeLicenseUtil.LICENSE_TIMER_OBJ.startAllLicenseTimer();
		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown() {
		if (null != HmBeLicenseUtil.LICENSE_TIMER_OBJ) {
			HmBeLicenseUtil.LICENSE_TIMER_OBJ.stopAllLicenseTimer();
		}
		return super.shutdown();
	}

	/**
	 * Get the License information.
	 * 
	 * @return LicenseInfo
	 */
	public LicenseInfo getLicenseInfo() {
		return AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO;
	}
	
	/**
	 * Import the License key.
	 * @param licenseBoes license history info bo
	 * @param newLicense the new license object
	 * @return boolean -
	 */
	public boolean importLicenseKey(List<LicenseHistoryInfo> licenseBoes, LicenseInfo newLicense) {
		if (null == licenseBoes || null == newLicense || (licenseBoes.size() != 1 && licenseBoes.size() != 2)) {
			return false;
		}
		try {
			// HiveManager license or GM Light license
			short appType = BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM.equals(newLicense.getLicenseType()) ? 
				LicenseHistoryInfo.LICENSE_TYPE_GM_LITE : LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER;
			// HiveManager license need to deal with activation key
			if (appType == LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER) {
				// deal with activation key
				if ((null == HmBeLicenseUtil.getLicenseInfo() || !HmBeLicenseUtil.getLicenseInfo().isUseActiveCheck())
						&& newLicense.isUseActiveCheck()) {
					
					// modify the hour used
					ActivationKeyInfo activation = new ActivationKeyInfo();
					// set the default value
					activation.setSystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					activation.setQueryPeriod(BeActivationModule.ACTIVATION_KEY_GRACE_PERIOD);
					QueryUtil.createBo(activation);
					
					// start the activation key collection timer
					HmBeActivationUtil.ACTIVATION_KEY_TIMER.activationKeyTimer();
				}
				
				// must input user manager license
				if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO.getLicenseType())
					&& null == HmBeLicenseUtil.GM_LITE_LICENSE_INFO) {
					HmBeLicenseUtil.GM_LITE_LICENSE_VALID = false;
				}
				
				// set new license information in session
				AeroLicenseTimer.HIVEMANAGER_LICENSE_INFO = newLicense;
				HmBeLicenseUtil.LICENSE_TIMER_OBJ.stopAllLicenseTimer();
				
				/*
				 * start new timer
				 */
				if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(newLicense.getLicenseType())
						|| BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(newLicense.getLicenseType())) {
					HmBeLicenseUtil.LICENSE_TIMER_OBJ.evaluationOrVmwareTimer();
				}
				
				// restart the activation key and collection timer
				if (HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID != HmBeLicenseUtil.LICENSE_VALID) {
					HmBeActivationUtil.ACTIVATION_KEY_TIMER.collectionInfoTimer();
				}
				
				// update information in database
				QueryUtil.updateBos(OrderHistoryInfo.class, "statusFlag = :s1, cvgStatusFlag = :s2", "domainName = :s3", new Object[] { OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE, OrderHistoryInfo.ENTITLE_KEY_STATUS_DISABLE, HmDomain.HOME_DOMAIN });
			}
			
			// update information in database
			QueryUtil.updateBos(LicenseHistoryInfo.class, "active = :s1", "active = :s2 and type = :s3", new Object[]{ false, true, appType });
			QueryUtil.bulkCreateBos(licenseBoes);
			if (appType == LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER) {
				HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID = HmBeLicenseUtil.LICENSE_VALID;
				// remove order key information
				DomainOrderKeyInfo currentDom = LicenseOperationTool.getDomainOrderKeyInfoForHa(HmDomain.HOME_DOMAIN);
				if (null != currentDom) {
					currentDom.setOrderKey("");
					currentDom.setCreateTime(System.currentTimeMillis());
					currentDom.setHoursUsed(currentDom.getEncriptString(0));
					currentDom.setLicenseStr("");
					try {
						QueryUtil.updateBo(currentDom);
					} catch (Exception e) {
						setDebugMessage("importLicenseKey() : update order key domain record! "+e.getMessage());
					}
				}
				if (NmsUtil.isHostedHMApplication()) {
					HmBeLicenseUtil.LICENSE_TIMER_OBJ.permanentEntitleKeyTimer();
				}
			} else {
				// does not exist GM license before
				if (!HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
					// insert default user management group
					HmBeParaUtil.insertDefaultGMUserGroup();
				}
				HmBeLicenseUtil.GM_LITE_LICENSE_INFO = newLicense;
				HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
			}
			return true;
		} catch (Exception e) {
			setDebugMessage(e.getMessage());
			setSystemLog(HmSystemLog.LEVEL_MAJOR,HmSystemLog.FEATURE_ADMINISTRATION,
					"importLicenseKey : update or create information error");
			return false;
		}
	}
	
	/**
	 * Get the two system id if HA mode, null if single HiveManager mode
	 * @return String[] : primary and secondary system id
	 */
	public String[] getTwoSystemId() {
		String sql = "SELECT bo.haStatus, bo.primarySystemId, bo.secondarySystemId FROM " + HASettings.class.getSimpleName() + " bo";
		List<?> objects = QueryUtil.executeQuery(sql, new SortParams("id"), null);
		if (objects.size() == 1) {
			Object[] item = (Object[]) objects.get(0);
			// if ha enabled
			if (Byte.parseByte(item[0].toString()) == HASettings.HASTATUS_ENABLE) {
				if (null != item[1] && null != item[2]) {
					String systemId1 = (String)item[1];
					String systemId2 = (String)item[2];
					// both of two system id exist at the same time
					if (checkSystemIdValid(systemId1) && checkSystemIdValid(systemId2) && !systemId1.equals(systemId2)) {
						String[] result = new String[2];
						result[0] = systemId1;
						result[1] = systemId2;
						return result;
					}
				}
			}
		}
		return null;
	}
	
	private boolean checkSystemIdValid(String arg_System) {
		return null != arg_System && !"".equals(arg_System) && arg_System.length() == 39 && arg_System.split("-").length == 8;
	}
	
	/**
	 * set the message to restore log when restore 3.0r2 to 3.0r3, else set to
	 * license log
	 *
	 * @param str_Message -
	 */
	private void setDebugMessage(String str_Message) {
		if (ifRestore) {
			AhRestoreDBTools.logRestoreMsg(str_Message);
		} else {
			debug(str_Message);
		}
	}

}