package com.ah.be.config.create.source;

import com.ah.xml.be.config.DeviceGroupOwnershipValue;
public interface DeviceGroupInt {

	public String getDeviceGroupName();
	
	public boolean isConfigMacObject();
	
	public String getMacObjectName();
	
	public boolean isConfigOsObject();
	
	public String getOsObjectName();
	
	public boolean isConfigDomain();
	
	public String getDomainName();
	public DeviceGroupOwnershipValue getDeviceOwnership();
	public boolean isConfigOwnership();
}
