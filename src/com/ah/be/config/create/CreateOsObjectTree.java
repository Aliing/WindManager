package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.OsObjectInt;
import com.ah.xml.be.config.OsObjectObj;

public class CreateOsObjectTree {
	
	private OsObjectInt osObjectImpl;
	private GenerateXMLDebug oDebug;
	
	private OsObjectObj osObject;

	public CreateOsObjectTree(OsObjectInt osObjectImpl, GenerateXMLDebug oDebug){
		this.osObjectImpl = osObjectImpl;
		this.oDebug = oDebug;
	}
	
	public OsObjectObj getOsObjectObj(){
		return this.osObject;
	}
	
	public void generate(){
		if(osObjectImpl.isConfigOsObject()){
			osObject = new OsObjectObj();
			generateOsObjectLevel_1();
		}
	}
	
	private void generateOsObjectLevel_1(){
		
		/** attribute: operation */
		osObject.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		osObject.setName(osObjectImpl.getOsObjectName());
		
		/** element: <os-version> */
		for(int index=0; index<osObjectImpl.getOsVersionSize(); index++){
			osObject.getOsVersion().add(CLICommonFunc.createAhNameActValue(osObjectImpl.getOsVersion(index), true));
		}
	}
}
