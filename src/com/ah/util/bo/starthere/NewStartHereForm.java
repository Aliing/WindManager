package com.ah.util.bo.starthere;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.util.MgrUtil;

public class NewStartHereForm {
	
	private String country = "United States";
	private String timeZone = "US/Pacific";
	
	private String defIfNotFoundCountry = "United States";
	
	/**
	 * null, "" or hidden
	 */
	private String quickStartSsidPwdDisplay;
	
	private boolean hasOrderedKey = false;
	
	// this is for entitlement key(s) installed 
	private List<OrderHistoryInfo> orderedKeys;
	
	// this is for license key installed
	private LicenseInfo licenseInfo;
	
	private boolean blnVirtualMachine;
	
	// add from Glasgow 11/27/2013
	private String hivemanagerPwdDisplay = "";
	
	// add from Glasgow 11/27/2013
	private boolean noPwdFromMyHive;
	
	public void initializeArgs() {
		this.blnVirtualMachine = HM_License.getInstance().isVirtualMachineSystem();
	}
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("h:mma MMM d, yyyy");
	public String getCurrentTimeWithTimezone() {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(this.getTimeZone()));
		return DATE_FORMAT.format(new java.util.Date(System.currentTimeMillis()));
	}
	
	public int getTimeZoneIdx() {
		return HmBeOsUtil.getServerTimeZoneIndex(this.getTimeZone());
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getQuickStartSsidPwdDisplay() {
		return quickStartSsidPwdDisplay;
	}
	public void setQuickStartSsidPwdDisplay(String quickStartSsidPwdDisplay) {
		this.quickStartSsidPwdDisplay = quickStartSsidPwdDisplay;
	}
	
	public List<OrderHistoryInfo> getOrderedKeys() {
		return orderedKeys;
	}

	public void setOrderedKeys(List<OrderHistoryInfo> orderedKeys) {
		this.orderedKeys = orderedKeys;
		if (this.orderedKeys != null
				&& this.orderedKeys.size() > 0) {
			this.hasOrderedKey = true;
		} else {
			this.hasOrderedKey = false;
		}
	}
	
	public boolean isHasOrderedKey() {
		return hasOrderedKey;
	}

	public String getDefIfNotFoundCountry() {
		return defIfNotFoundCountry;
	}

	public boolean isBlnVirtualMachine() {
		return blnVirtualMachine;
	}

	public LicenseInfo getLicenseInfo() {
		return licenseInfo;
	}

	public void setLicenseInfo(LicenseInfo licenseInfo) {
		this.licenseInfo = licenseInfo;
		if (licenseInfo != null) {
			licenseKeySummary = getOrderedLicenseKeyInfo(licenseInfo.getOrderKey(), licenseInfo);
		}
	}
	
	private String licenseKeySummary;
	public String getLicenseKeySummary() {
		return licenseKeySummary;
	}

	public static String getOrderedLicenseKeyInfo(String licenseKey, LicenseInfo licenseInfo) {
		String result = "";
		
		if (StringUtils.isNotBlank(licenseKey)) {
			result = MgrUtil.getUserMessage("geneva_31.hm.missionux.wecomle.license.succ.license.single.info"
						, String.valueOf(licenseInfo.getHiveAps()));
			if (licenseInfo.getTotalDays() > 0) {
				if (licenseInfo.getTotalDays() > 1) {
					result += " for <span class='key_total_days'>" + licenseInfo.getTotalDays() + " days</span>";
				} else {
					result += " for <span class='key_total_days'>" + licenseInfo.getTotalDays() + " day</span>";
				} 
			}
		}
		
		return result;
	}

	public String getHivemanagerPwdDisplay() {
		return hivemanagerPwdDisplay;
	}

	public void setHivemanagerPwdDisplay(String hivemanagerPwdDisplay) {
		this.hivemanagerPwdDisplay = hivemanagerPwdDisplay;
	}

	public boolean isNoPwdFromMyHive() {
		return noPwdFromMyHive;
	}

	public void setNoPwdFromMyHive(boolean noPwdFromMyHive) {
		this.noPwdFromMyHive = noPwdFromMyHive;
	}
	
}
