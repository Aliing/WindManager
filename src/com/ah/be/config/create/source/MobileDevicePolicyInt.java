package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

public interface MobileDevicePolicyInt {

	public String getDevicePolicyGuiName();
	
	public String getDevicePolicyName();
	
	public boolean isConfigDeviceGroup();
	
	public boolean isConfigApplyOnce();
	
	public boolean isConfigMultiple();
	
	public boolean isClassificationMac();
	
	public boolean isClassificationOs();
	
	public boolean isClassificationDomain();
	
	public int getRuleSize();
	
	public String getOriginalAttribute(int index);
	
	public String getDeviceGroupName(int index);
	
	public int getMappedAttribute(int index);
	
	public int getPolicyRuleId(int index) throws CreateXMLException;
	
	public boolean isConfigBefore(int index);
	
	public int getBeforeValue(int index);
	
	public int getBeforeRuleId(int index) throws CreateXMLException;
}
