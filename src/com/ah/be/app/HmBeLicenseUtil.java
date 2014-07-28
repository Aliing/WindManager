/**
 *@filename		HmBeLicenseUtil.java
 *@version		v1.14
 *@author		Fiona
 *@createtime	2007-9-5 04:21:27 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.license.AeroLicenseTimer;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

/**
 * @author Fiona
 * @version v1.14
 */
public class HmBeLicenseUtil {

	public static AeroLicenseTimer LICENSE_TIMER_OBJ;
	
	public static final int NO_LICENSE_HAS_PERIOD = 1;
	
	public static final int NO_LICENSE_MUST_INPUT = 2;
	
	public static final int LICENSE_INVALID = 3;
	
	public static final int LICENSE_VALID = 4;
	
	public static int HIVEMANAGER_LICENSE_VALID = LICENSE_VALID;
	
	public static LicenseInfo GM_LITE_LICENSE_INFO = null;
	
	public static boolean GM_LITE_LICENSE_VALID = false;
	
	public static Map<String, LicenseInfo> VHM_ORDERKEY_INFO = new HashMap<String, LicenseInfo>();

	/**
	 * Get the License information from database.
	 * @return LicenseInfo
	 */
	public static LicenseInfo getLicenseInfo() {
		return AhAppContainer.HmBe.getLicenseModule().getLicenseInfo();
	}

	/**
	 * Import the License key.
	 * @param licenseBoes license history info bo
	 * @param newLicense the new license object
	 * @return boolean -
	 */
	public static boolean importLicenseKey(List<LicenseHistoryInfo> licenseBoes, LicenseInfo newLicense) {
		return AhAppContainer.HmBe.getLicenseModule().importLicenseKey(licenseBoes, newLicense);
	}
	
	/**
	 * Get the two system id if HA mode, null if single HiveManager mode
	 * @return String[] : primary and secondary system id
	 */
	public static String[] getTwoSystemId() {
		return AhAppContainer.HmBe.getLicenseModule().getTwoSystemId();
	}
	
	/**
	 * Delete the license does not belong to this HiveManager
	 * @throws Exception 
	 */
	public static void deleteUselessLicense() throws Exception {
		QueryUtil.removeBos(LicenseHistoryInfo.class, new FilterParams("systemid != :s1", new Object[]{BeLicenseModule.HIVEMANAGER_SYSTEM_ID}));
	}
	
}