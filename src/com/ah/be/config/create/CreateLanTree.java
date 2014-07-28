package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.LanPortInt;
import com.ah.be.config.create.source.LanPortInt.LanType;
import com.ah.xml.be.config.LanEthx;
import com.ah.xml.be.config.LanEthxMode;
import com.ah.xml.be.config.LanEthxVlanId;
import com.ah.xml.be.config.LanObj;

public class CreateLanTree {
	
	private LanPortInt lanImpl;
	
	private GenerateXMLDebug oDebug;
	
	private LanObj lanObj;
	
	private List<Object> LanChildList_1 = new ArrayList<Object>();

	public CreateLanTree(LanPortInt lanImpl, GenerateXMLDebug oDebug){
		this.lanImpl = lanImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		oDebug.debug("/configuration", 
				"lan", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(lanImpl.isConfigLanProfile()){
			lanObj = new LanObj();
			generateLanLevel_1();
		}
	}
	
	public LanObj getLanObj(){
		return this.lanObj;
	}
	
	private void generateLanLevel_1(){
		
		/** element: <lan>.<eth1> */
		lanObj.setEth1(createLanEthx(LanType.lan1));
		
		/** element: <lan>.<eth2> */
		lanObj.setEth2(createLanEthx(LanType.lan2));
		
		/** element: <lan>.<eth3> */
		lanObj.setEth3(createLanEthx(LanType.lan3));
		
		/** element: <lan>.<eth4> */
		lanObj.setEth4(createLanEthx(LanType.lan4));
		
		/** element: <lan>.<vlan-id-check> */
		if(lanImpl.isConfigVlanCheck()){
			oDebug.debug("/configuration/lan", 
					"vlan-id-check", GenerateXMLDebug.SET_OPERATION,
					null, null);
			lanObj.setVlanIdCheck(CLICommonFunc.getAhOnlyAct(lanImpl.isVlanCheck()));
		}
	}
	
	private LanEthx createLanEthx(LanType lanType){
		LanEthx lanProfile = new LanEthx();
		
		/** element: <lan>.<ethx>.<shutdown> */
		oDebug.debug("/configuration/lan/" + LanType.lan4.getValue(), 
				"shutdown", GenerateXMLDebug.SET_OPERATION,
				null, null);
		lanProfile.setShutdown(CLICommonFunc.getAhOnlyAct(lanImpl.isLanInterShutdown(lanType)));
		
		if(lanImpl.isConfigLanPort(lanType)){
			
			/** element: <lan>.<ethx>.<mode> */
			LanEthxMode lanMode = new LanEthxMode();
			LanChildList_1.add(lanMode);
			lanProfile.setMode(lanMode);
			
			/** element: <lan>.<ethx>.<vlan-id> */
			for(int index=0; index<lanImpl.getLanInterSize(lanType); index++){
				
				oDebug.debug("/configuration/lan/" + LanType.lan4.getValue(),
						"vlan-id", GenerateXMLDebug.SET_NAME,
						null, null);
				lanProfile.getVlanId().add(createLanEthxVlanId(lanImpl.getLanInterVlan(lanType, index)));
			}
		}
		
		
		generateLanChildLevel_1(lanType);
		
		return lanProfile;
	}
	
	private LanEthxVlanId createLanEthxVlanId(int vlanId){
		LanEthxVlanId vlanIdObj = new LanEthxVlanId();
		
		vlanIdObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		vlanIdObj.setName(vlanId);
		
		return vlanIdObj;
	}
	
	private void generateLanChildLevel_1(LanType lanType){
		/**
		 * <lan>.<ethx>.<mode>				LanEthxMode
		 */
		for(Object childObj : LanChildList_1){
			
			/** element : <lan>.<ethx>.<mode> */
			if(childObj instanceof LanEthxMode){
				LanEthxMode lanModel = (LanEthxMode)childObj;
				
				/** attribute: operation */
				lanModel.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				
				lanModel.setValue(lanImpl.getLanInterMode(lanType));
				
			}
		}
		LanChildList_1.clear();
	}
}
