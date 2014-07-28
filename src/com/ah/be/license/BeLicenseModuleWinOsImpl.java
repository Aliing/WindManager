/**
 *@filename		BeLicenseModuleWinOsImpl.java
 *@version		v1.19
 *@author		Fiona
 *@createtime	2007-9-3 02:05:15 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.license;

import java.util.List;

import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.bo.admin.LicenseHistoryInfo;

/**
 * @author Fiona
 * @version v1.19
 */
public class BeLicenseModuleWinOsImpl extends BaseModule implements
		BeLicenseModule {

	/**
	 * Constructor
	 */
	public BeLicenseModuleWinOsImpl() {
		super();
		setModuleId(BaseModule.ModuleID_License);
		setModuleName("BeLicenseModule");
	}

	/**
	 * Start license scheduler
	 */
	public boolean run() {
		return true;
	}

	/**
	 * Get the License information. If the user is not a developer return null.
	 * 
	 * @return LicenseInfo
	 */
	public LicenseInfo getLicenseInfo() {
		LicenseInfo dto_License = null;

		// get the environment of developer
		String develop = System.getenv("DEVELOP_ENV");
		if (null != develop
				&& BeLicenseModule.LICENSE_DEVELOP_ENVIRONMENT.equals(develop)) {
			dto_License = new LicenseInfo();
			dto_License
					.setLicenseType(BeLicenseModule.LICENSE_TYPE_DEVELOP_NUM);
			dto_License.setHiveAps(BeLicenseModule.WINDOWS_DEVELOP_HIVEAP_NUM);
			dto_License.setCvgNumber(BeLicenseModule.WINDOWS_DEVELOP_CVG_NUM);
			dto_License.setVhmNumber(BeLicenseModule.VMWARE_LICENSE_SUPPORT_VHM_COUNT);
			
			// init gm light license
			LicenseInfo gm_license = new LicenseInfo();
			gm_license
					.setLicenseType(BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM);
			gm_license.setHiveAps(BeLicenseModule.VMWARE_LICENSE_SUPPORT_AP_COUNT);
			HmBeLicenseUtil.GM_LITE_LICENSE_INFO = gm_license;
			HmBeLicenseUtil.GM_LITE_LICENSE_VALID = true;
		}
		return dto_License;
	}

	/**
	 * Import the License key.
	 * @param licenseBoes license history info bo
	 * @param newLicense the new license object
	 * @return boolean -
	 */
	public boolean importLicenseKey(List<LicenseHistoryInfo> licenseBoes, LicenseInfo newLicense) {
		debug("Windows system cannot import license.");
		return false;
	}
	
	/**
	 * Get the two system id if HA mode, null if single HiveManager mode
	 * @return String[] : primary and secondary system id
	 */
	public String[] getTwoSystemId() {
		return null;
	}

	public BaseModule getModule() {
		return this;
	}

}