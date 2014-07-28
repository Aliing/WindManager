package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.SsidBindUserGroupInt;
import com.ah.xml.be.config.SsidObj;

/**
 * @author zhang
 * @version 2009-2-24 15:21:28
 */

public class CreateSsidBindUserGroupTree {
	
	private SsidBindUserGroupInt ssidBindGroupImpl;
	private SsidObj ssidObj;
	private GenerateXMLDebug oDebug;

	public CreateSsidBindUserGroupTree(SsidBindUserGroupInt ssidBindGroupImpl, GenerateXMLDebug oDebug){
		this.ssidBindGroupImpl = ssidBindGroupImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		ssidObj = new SsidObj();
		generateSsidLevel_1();
	}
	
	public SsidObj getSsidObj(){
		return this.ssidObj;
	}
	
	private void generateSsidLevel_1(){
		/**
		 * <ssid>				SsidObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"ssid", GenerateXMLDebug.SET_NAME,
				ssidBindGroupImpl.getSsidGuiName(), ssidBindGroupImpl.getSsidName());
		ssidObj.setName(ssidBindGroupImpl.getSsidName());
		
		/** attribute: operation */
		ssidObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <ssid>.<user-group> */
		oDebug.debug("/configuration/ssid[@name='"+ssidBindGroupImpl.getSsidName()+"']",
				"user-group", GenerateXMLDebug.CONFIG_ELEMENT,
				ssidBindGroupImpl.getSsidGuiName(), ssidBindGroupImpl.getSsidName());
		if(ssidBindGroupImpl.isSsidBindGroup()){
			for(int i=0; i<ssidBindGroupImpl.getUserGroupSize(); i++){
				
				oDebug.debug("/configuration/ssid[@name='"+ssidBindGroupImpl.getSsidName()+"']",
						"user-group", GenerateXMLDebug.SET_NAME,
						ssidBindGroupImpl.getSsidGuiName(), ssidBindGroupImpl.getSsidName());
				ssidObj.getUserGroup().add(CLICommonFunc.createAhNameActValue(
						ssidBindGroupImpl.getUserGroupName(i), CLICommonFunc.getYesDefault())
				);
			}
		}
	}
}
