package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.DeviceGroupInt;
import com.ah.xml.be.config.CwpMultiLanguage;
import com.ah.xml.be.config.DeviceGroupObj;
import com.ah.xml.be.config.DeviceGroupOwnership;

public class CreateDeviceGroupTree {
	
	private DeviceGroupInt deviceGroupImpl;
	private GenerateXMLDebug oDebug;
	private List<Object> deviceGroupChildList_1 = new ArrayList<Object>();
	
	private DeviceGroupObj deviceGroup;

	public CreateDeviceGroupTree(DeviceGroupInt deviceGroupImpl, GenerateXMLDebug oDebug){
		this.deviceGroupImpl = deviceGroupImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		deviceGroup = new DeviceGroupObj();
		generateDeviceGroupLevel_1();
	}
	
	public DeviceGroupObj getDeviceGroupObj(){
		return this.deviceGroup;
	}
	
	private void generateDeviceGroupLevel_1(){
		
		/** attribute: operation */
		deviceGroup.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		deviceGroup.setName(deviceGroupImpl.getDeviceGroupName());
		
		/** element: <device-group>.<mac-object> */
		if(deviceGroupImpl.isConfigMacObject()){
			deviceGroup.setMacObject(CLICommonFunc.createAhNameActValue(deviceGroupImpl.getMacObjectName(), true));
		}
		
		/** element: <device-group>.<os-object> */
		if(deviceGroupImpl.isConfigOsObject()){
			deviceGroup.setOsObject(CLICommonFunc.createAhNameActValue(deviceGroupImpl.getOsObjectName(), true));
		}
		
		/** element: <device-group>.<domain-object> */
		if(deviceGroupImpl.isConfigDomain()){
			deviceGroup.setDomainObject(CLICommonFunc.createAhNameActValue(deviceGroupImpl.getDomainName(), true));
		}
		if(deviceGroupImpl.isConfigOwnership()){
		DeviceGroupOwnership deviceOwnership = new DeviceGroupOwnership();
		deviceGroupChildList_1.add(deviceOwnership);
		deviceGroup.setOwnership(deviceOwnership);
		}
		generateDeviceGroupLevel_2();
	}
	private void generateDeviceGroupLevel_2(){
		for(Object childObj : deviceGroupChildList_1){
			if(childObj instanceof DeviceGroupOwnership ){
				DeviceGroupOwnership deviceOwnership = (DeviceGroupOwnership)childObj;
				deviceOwnership.setValue(deviceGroupImpl.getDeviceOwnership());
				deviceOwnership.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
		}
		deviceGroupChildList_1.clear();
	}
}
