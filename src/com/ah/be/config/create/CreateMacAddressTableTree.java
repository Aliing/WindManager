package com.ah.be.config.create;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MacAddressTableInt;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.MacAddressTableLearning;
import com.ah.xml.be.config.MacAddressTableNotification;
import com.ah.xml.be.config.MacAddressTableObj;
import com.ah.xml.be.config.MacAddressTableStatic;

public class CreateMacAddressTableTree {
	
	private MacAddressTableInt macAddressTableService;
	private MacAddressTableObj macAddressTableObj;
	private GenerateXMLDebug oDebug;
	private List<Object> ssidChildList_1 = new ArrayList<Object>();

	public CreateMacAddressTableTree(MacAddressTableInt macAddressTableService, GenerateXMLDebug oDebug) throws Exception {
		this.macAddressTableService = macAddressTableService;
		this.oDebug = oDebug;
	}
		
	public void generate() throws Exception{
		if(macAddressTableService.isConfigFDB()){
			macAddressTableObj = new MacAddressTableObj();
			generateConnectionAlarmLevel_1();
		}
	}	
	
	public MacAddressTableObj getMacAddressTableObj() {
		return macAddressTableObj;
	}
	
	private void generateConnectionAlarmLevel_1(){
		MacAddressTableLearning learning = new MacAddressTableLearning();
		MacAddressTableNotification notification = new MacAddressTableNotification();
		macAddressTableObj.setLearning(learning);
		//hidden notification because hiveos has not this function
		//macAddressTableObj.setNotification(notification);
		macAddressTableObj.setIdleTimeout(CLICommonFunc.createAhIntActObj(macAddressTableService.getIdleTimeout(), true));
		for (int i = 0; i < macAddressTableService.getMacAddressStaticSize(); i++) {
			macAddressTableObj.getStatic().add(createMacaddressTableStatic(i));
		}
		ssidChildList_1.add(learning);
		ssidChildList_1.add(notification);
		generateConnectionAlarmLevel_2();
	}
	
	private MacAddressTableStatic createMacaddressTableStatic(int index) {
		MacAddressTableStatic tableStatic = new MacAddressTableStatic();
		tableStatic.setName(macAddressTableService.getStaticMacAddress(index) + " vlan " + macAddressTableService.getStaticVlanId(index));
		tableStatic.setQuoteProhibited(AhEnumAct.YES);
		tableStatic.setOperation(AhEnumActValue.YES_WITH_VALUE);
		tableStatic.setInterface(CLICommonFunc.getAhString(macAddressTableService.getStaticInterface(index)));
		return tableStatic;
	}
	
	private void generateConnectionAlarmLevel_2(){
		if(ssidChildList_1.isEmpty()){
			return;
		}
		for(Object childObj : ssidChildList_1){
			if(childObj instanceof MacAddressTableLearning){
				MacAddressTableLearning learning = (MacAddressTableLearning)childObj;
				if (macAddressTableService.isEnableMacLearnForAllVlans()) {  
					learning.getVlan().add(CLICommonFunc.createAhNameActValue("all", false));
				} else {
					for (int i = 0; i < macAddressTableService.getSelectVlanSize(); i++) {
						learning.getVlan().add(CLICommonFunc.createAhNameActValue(String.valueOf(macAddressTableService.getSelectVlanId(i)), false));
					}
				}
			}
			if(childObj instanceof MacAddressTableNotification){
				MacAddressTableNotification notification = (MacAddressTableNotification)childObj;
				if (macAddressTableService.isEnableNotification()) {
					notification.setCr(CLICommonFunc.getAhOnlyAct(true));
					notification.setInterval(CLICommonFunc.createAhIntActObj(macAddressTableService.getNotificationInterval(), true));
				} else {
					notification.setCr(CLICommonFunc.getAhOnlyAct(false));
				}
			}
		}
		ssidChildList_1.clear();
	}
	 
		 
}
