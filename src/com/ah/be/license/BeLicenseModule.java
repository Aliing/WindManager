/**
 *@filename		BeLicenseModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 02:04:23 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.license;

import java.util.List;

import com.ah.be.common.AhDirTools;
import com.ah.bo.admin.LicenseHistoryInfo;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public interface BeLicenseModule {

	// it is also the flag of use activation key to validate
	public static final String LICENSE_TYPE_EVALUATION_NUM = "01";

	public static final String LICENSE_TYPE_PERMANENT_NUM = "02";

	public static final String LICENSE_TYPE_DEVELOP_NUM = "03";

	public static final String LICENSE_TYPE_VMWARE_NUM = "04";
	
	public static final String LICENSE_TYPE_RENEW_NUM = "05";
	
	public static final String LICENSE_TYPE_GM_PERM_NUM = "08";
	
	public static final String LICENSE_TYPE_NO_ORDERKEY = "00";
	
	public static final String LICENSE_TYPE_SIMPLE_VHM = "10";
	
	public static final int ACM_LICENSE_TYPE = 20;

	public static final String LICENSE_DEVELOP_ENVIRONMENT = "Aerohive_HiveManager";

	public static final String LICENSE_FILE_NAME = AhDirTools.getHmRoot()+".hm_license";

	public static final String EVALUATION_LICENSE_FILE_NAME = "/etc/sysconfig/.system-config-timer";

	public static final int WINDOWS_DEVELOP_HIVEAP_NUM = 9999;
	
	public static final int WINDOWS_DEVELOP_CVG_NUM = 999;

	public static final int PRINT_SYSTEMLOG_HOURS = 73;

	public static final int LICENSE_TYPE_INDEX = 2;

	public static final int LICENSE_HIVEAP_NUM_INDEX = 8;

	public static final int LICENSE_KEY_LENGTH = 13;
	
	public static final int LICENSE_KEY_ADD_VHMNUMBER_LENGTH = 16;
	
	public static final int LICENSE_KEY_ADD_ACTIVATION_LENGTH = 18;

	public static final int AH_LICENSE_EXCESS_SUPPORT_AP_COUNT = 5;

	public static final int AH_LICENSE_NO_ENTITLEMENT_KEY_AP_COUNT = 100;
	
	public static final int AH_LICENSE_NO_ENTITLEMENT_KEY_AP_COUNT_VHM = 10;
	
	public static final int AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT = 100;
	
	public static final int AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT_VHM = 10;

	public static final int VMWARE_LICENSE_SUPPORT_AP_COUNT = 25;
	
	public static final int VMWARE_LICENSE_SUPPORT_VHM_COUNT = 5;
	
	public static final String HIVEMANAGER_SYSTEM_ID = HM_License.getInstance().get_system_id();
	
	/**
	 * Get the License information from database.
	 * @return LicenseInfo
	 */
	public LicenseInfo getLicenseInfo();

	/**
	 * Import the License key.
	 * @param licenseBoes license history info bo
	 * @param newLicense the new license object
	 * @return boolean -
	 */
	public boolean importLicenseKey(List<LicenseHistoryInfo> licenseBoes, LicenseInfo newLicense);
	
	/**
	 * Get the two system id if HA mode, null if single HiveManager mode
	 * @return String[] : primary and secondary system id
	 */
	public String[] getTwoSystemId();

}