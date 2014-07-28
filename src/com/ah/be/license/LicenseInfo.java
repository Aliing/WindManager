/**
 *@filename		LicenseInforDTO.java
 *@version
 *@author		Fiona
 *@createtime	Mar 7, 2007 6:27:48 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.license;

import java.io.Serializable;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class LicenseInfo implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private int hiveAps = 0;
	private String licenseType;
	private int totalDays = 0;
	private int leftHours = 0;
	private int vhmNumber = 0;
	private int cvgNumber = 0;
	private boolean useActiveCheck = false;
	private boolean zeroDeviceKeyValid = false;
	
	/**
	 * For Order Key
	 */
	private long createTime;
	
	private String orderKey = "";
	
	private String systemId;
	
	public String getSystemId()
	{
		return systemId;
	}

	public void setSystemId(String systemId)
	{
		this.systemId = systemId;
	}

	public int getVhmNumber() {
		return vhmNumber;
	}

	public void setVhmNumber(int vhmNumber) {
		this.vhmNumber = vhmNumber;
	}

	public int getHiveAps()
	{
		return hiveAps;
	}

	public void setHiveAps(int hiveAps)
	{
		this.hiveAps = hiveAps;
	}

	public String getLicenseType()
	{
		return licenseType;
	}

	public void setLicenseType(String licenseType)
	{
		this.licenseType = licenseType;
	}

	public boolean isUseActiveCheck() {
		return useActiveCheck;
	}

	public void setUseActiveCheck(boolean useActiveCheck) {
		this.useActiveCheck = useActiveCheck;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public int getLeftHours() {
		return leftHours;
	}

	public void setLeftHours(int leftHours) {
		this.leftHours = leftHours;
	}
	
	public String getLicenseTypeStr() {
		String licenseT = "Invalid";
		if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(licenseType)) {
			licenseT = "Evaluation";
		} else if (BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseType) ||
			BeLicenseModule.LICENSE_TYPE_GM_PERM_NUM.equals(licenseType) ||
			BeLicenseModule.LICENSE_TYPE_RENEW_NUM.equals(licenseType)) {
			licenseT = systemId.length() > 10 ? "Permanent" : "Subscription";
		} else if (BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(licenseType)) {
			licenseT = "VMWare";
		} else if (BeLicenseModule.LICENSE_TYPE_DEVELOP_NUM.equals(licenseType)) {
			licenseT = "Develop";
		}
		return licenseT;
	}
	
	public String getExpireDate() {
		if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(licenseType)
				|| BeLicenseModule.LICENSE_TYPE_VMWARE_NUM.equals(licenseType)) {
			if (leftHours == 0) {
				return "Your " + getLicenseType() + " license has expired.";
			}

			int days = leftHours / 24;
			return days > 1 ? (days + " days") : "1 day";
		}
		return "";
	}
	
	public String getTotalTime() {
		if (totalDays > 0) {
			return totalDays + (totalDays > 1 ? " days" : " day");
		}

		return "";
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public int getCvgNumber()
	{
		return cvgNumber;
	}

	public void setCvgNumber(int cvgNumber)
	{
		this.cvgNumber = cvgNumber;
	}

	public boolean isZeroDeviceKeyValid() {
		return zeroDeviceKeyValid;
	}

	public void setZeroDeviceKeyValid(boolean zeroDeviceKeyValid) {
		this.zeroDeviceKeyValid = zeroDeviceKeyValid;
	}

}
