/**
 *@filename		HmBeActivationUtil.java
 *@version
 *@author		Fiona
 *@createtime	May 21, 2009 7:37:38 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.activation.AeroActivationTimer;
import com.ah.be.activation.BeActivationModule;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.DownloadImageInfo;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HmBeActivationUtil {
	
	public static AeroActivationTimer ACTIVATION_KEY_TIMER;
	
	public static boolean ACTIVATION_KEY_VALID = true;
	
	public static Map<String, DownloadImageInfo> SOFTWARE_IMAGE_LIST_FROM_LS = new LinkedHashMap<String, DownloadImageInfo>();
	
	/**
	 * Get the activation key information.
	 *@return ActivationKeyInfo
	 */
	private static ActivationKeyInfo getActivationKeyInfo() {
		ActivationKeyInfo activation = null;
		try {
			activation = QueryUtil.findBoByAttribute(ActivationKeyInfo.class, "systemId",
					BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
		} catch (Exception e) {
		}
		if (null == activation) {
			activation = new ActivationKeyInfo();
			// set the default value
			activation.setSystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
			activation.setQueryPeriod(BeActivationModule.ACTIVATION_KEY_GRACE_PERIOD);
		}
		return activation;
	}
	
	/**
	 * Get the all activation key information from database.
	 * 
	 * @return List<ActivationKeyInfo>
	 */
	public static List<ActivationKeyInfo> getAllActivationInfoFromDb() {
		String[] twoSystemId = HmBeLicenseUtil.getTwoSystemId();
		List<ActivationKeyInfo> actKeyList = new ArrayList<ActivationKeyInfo>();
		// single HiveManager
		if (null == twoSystemId) {
			actKeyList.add(getActivationKeyInfo());
		// HA mode
		} else {
			// the primary activation key
			ActivationKeyInfo priAct = QueryUtil.findBoByAttribute(ActivationKeyInfo.class, "systemId", twoSystemId[0]);
			actKeyList.add(priAct);
			// the secondary activation key
			ActivationKeyInfo secAct = QueryUtil.findBoByAttribute(ActivationKeyInfo.class, "systemId", twoSystemId[1]);
			if (null == secAct) {
				priAct.setId(null);
				priAct.setVersion(null);
				priAct.setSystemId(twoSystemId[1]);
				priAct.initTheUsedHours();
				try {
					// create new secondary activation key
					Long newid = QueryUtil.createBo(priAct);
					secAct = QueryUtil.findBoById(ActivationKeyInfo.class, newid);
				} catch (Exception ex) {
					DebugUtil.error(HmLogConst.M_LICENSE, "getAllActivationInfoFromDb():create second activation key", ex);
				}	
			}
			actKeyList.add(secAct);
		}
		return actKeyList;
	}
	
	/**
	 * Get the license server information.
	 * @return LicenseServerSetting
	 */
	public static LicenseServerSetting getLicenseServerInfo() {
		LicenseServerSetting lserverInfo = null;
		try {
			List<LicenseServerSetting> bos = QueryUtil.executeQuery(LicenseServerSetting.class, null, null);
			if (bos.size() == 1) {
				lserverInfo = bos.get(0);
			}
		} catch (Exception e) {
		}
		return lserverInfo;
	}
	
	/**
	 * Get the activation key from database.
	 * @return String
	 */
	public static String getActivationKey() {
		String actKey = "";
		if (null != getActivationKeyInfo()) {
			actKey = getActivationKeyInfo().getActivationKey();
		}
		return actKey;
	}
	
	/**
	 * Check if activation key valid for GUI
	 *
	 *@return boolean
	 */
	public static boolean ifActivationKeyValid() {
		for (ActivationKeyInfo actInfo : getAllActivationInfoFromDb()) {
			if ("".equals(actInfo.getActivationKey())) {
				if (actInfo.getQueryPeriodLeft() <= 0) {
					return false;
				}
			} else if(!actInfo.isStartRetryTimer() && !actInfo.isActivateSuccess()
				&& HM_License.getInstance().isVirtualMachineSystem()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if activation key valid for GUI
	 *
	 *@return boolean
	 */
	public static boolean ifActivationKeyValidForHa() {
		LicenseInfo licInfo = HmBeLicenseUtil.getLicenseInfo();
		if (null != licInfo && licInfo.isUseActiveCheck()) {
			ActivationKeyInfo actInfo = getActivationKeyInfo();
			if ("".equals(actInfo.getActivationKey())) {
				return false;
			} else if(!actInfo.isStartRetryTimer() && !actInfo.isActivateSuccess()
				&& HM_License.getInstance().isVirtualMachineSystem()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Delete the activation key does not belong to this HiveManager
	 * @throws Exception 
	 */
	public static void deleteUselessActivationKey() throws Exception {
		QueryUtil.removeBos(ActivationKeyInfo.class, new FilterParams("systemid != :s1", new Object[]{BeLicenseModule.HIVEMANAGER_SYSTEM_ID}));
	}
	
}