package com.ah.be.config.create.source.impl;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.DeviceGroupInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.OsObject;
import com.ah.xml.be.config.CwpMultiLanguageValue;
import com.ah.xml.be.config.DeviceGroupOwnershipValue;

public class DeviceGroupImpl implements DeviceGroupInt {
	
	private DeviceGroup deviceGroup;
	private HiveAp hiveAp;

	public DeviceGroupImpl(DeviceGroup deviceGroup,HiveAp hiveAp){
		this.deviceGroup = deviceGroup;
		this.hiveAp = hiveAp;
	}
	
	public DeviceGroup getDeviceGroup(){
		return this.deviceGroup;
	}
	
	public String getDeviceGroupName(){
		return this.deviceGroup.getDeviceGroupName();
	}
	
	public boolean isConfigMacObject(){
		return deviceGroup.getMacObj() != null;
	}
	@Override
	public boolean isConfigOwnership() {
		return deviceGroup.getDeviceOwnership() ==DevicePolicyRule.CID_TYPE
				|| deviceGroup.getDeviceOwnership() ==DevicePolicyRule.BYOD_TYPE;
	}
	
	public String getMacObjectName(){
		return deviceGroup.getMacObj().getMacOrOuiName();
	}
	
	public boolean isConfigOsObject(){
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
			if(hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
				return deviceGroup.getOsObj() != null;
			}else{
				return deviceGroup.getOsObj() != null && deviceGroup.getOsObj().getDhcpItems().size() > 0;
			}
		}else{
			return deviceGroup.getOsObj() != null && deviceGroup.getOsObj().getItems().size() > 0;
		}	
	}
	
	public String getOsObjectName(){
		return deviceGroup.getOsObj().getOsName();
	}
	
	public boolean isConfigDomain(){
		return deviceGroup.getDomainObj() != null
				&& deviceGroup.getDomainObj().getObjName() != null 
				&& !"".equals(deviceGroup.getDomainObj().getObjName());
	}
	
	public String getDomainName(){
		return deviceGroup.getDomainObj().getObjName();
	}
	
	public DeviceGroupOwnershipValue getDeviceOwnership(){
		switch (deviceGroup.getDeviceOwnership())
		{
		case 2:
			return DeviceGroupOwnershipValue.CID;
		case 3:
			return DeviceGroupOwnershipValue.BYOD;
		}
		return null;
	}
	public static class DeviceGroup{
		
		private MacOrOui macObj;
		private OsObject osObj;
		private DomainObject domainObj;
		private int deviceOwnership; 
		
		public int getDeviceOwnership() {
			return deviceOwnership;
		}
		public void setDeviceOwnership(int deviceOwnership) {
			this.deviceOwnership = deviceOwnership;
		}
		public String getDeviceGroupName(){
			return DeviceGroup.getDeviceGroupName(this.macObj, this.osObj, this.domainObj,this.deviceOwnership);
		}
		
		public static String getDeviceGroupName(MacOrOui macObj, OsObject osObj, DomainObject domainObj,int deviceOwnership){
			long macIndex = (macObj != null)? macObj.getId() : -1;
			long osIndex = (osObj != null)? osObj.getId() : -1;
			long domainIndex = (domainObj != null)? domainObj.getId() : -1;
			return "DG_" + macIndex + "_" + osIndex + "_" + domainIndex + "_" + deviceOwnership;
		}
		
		public MacOrOui getMacObj(){
			return this.macObj;
		}
		
		public void setMacObj(MacOrOui macObj){
			this.macObj = macObj;
		}
		
		public OsObject getOsObj(){
			return this.osObj;
		}
		
		public void setOsObj(OsObject osObj){
			this.osObj = osObj;
		}
		
		public DomainObject getDomainObj(){
			return this.domainObj;
		}
		
		public void setDomainObj(DomainObject domainObj){
			this.domainObj = domainObj;
		}
		
		public boolean equals(Object obj){
			if(obj == null){
				return false;
			}
			
			if(!(obj instanceof DeviceGroupImpl.DeviceGroup)){
				return false;
			}
			
			DeviceGroupImpl.DeviceGroup dGroup = (DeviceGroupImpl.DeviceGroup)obj;
			boolean equalsMacObj = false;
			boolean equalsOsObj = false;
			boolean equalsDomainObj = false;
			boolean equalsOwnership = false;
			
			equalsMacObj = (this.macObj != null && dGroup.getMacObj() != null && this.macObj.getId().equals(dGroup.getMacObj().getId()));
			equalsOsObj = (this.osObj != null && dGroup.getOsObj() != null && this.osObj.getId().equals(dGroup.getOsObj().getId()));
			equalsDomainObj = (this.domainObj != null && dGroup.getDomainObj() != null && this.domainObj.getId().equals(dGroup.getDomainObj().getId()));
			equalsOwnership =  this.deviceOwnership == dGroup.getDeviceOwnership();
			return equalsMacObj && equalsOsObj && equalsDomainObj && equalsOwnership;
		}
	}
}
