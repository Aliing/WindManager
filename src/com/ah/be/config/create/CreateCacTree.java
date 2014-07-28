package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.CacProfileInt;
import com.ah.xml.be.config.CacObj;

/**
 * @author zhang
 * @version 2008-8-7 10:24:53
 */

public class CreateCacTree {
	
	private CacProfileInt cacImpl;
	private CacObj cacObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> cacChildLevel_1 = new ArrayList<Object>();

	public CreateCacTree(CacProfileInt cacImpl, GenerateXMLDebug oDebug){
		this.cacImpl = cacImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		/** element: <cac> */
		oDebug.debug("/configuration", 
				"cac", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(cacImpl.isConfigCac()){
			cacObj = new CacObj();
			
			generateCacLevel_1();
		}
	}
	
	public CacObj getCacObj (){
		return this.cacObj;
	}
	
	private void generateCacLevel_1() throws Exception{
		/**
		 * <cac>				CacObj
		 */
		
		/** element: <cac>.<enable> */
		oDebug.debug("/configuration/cac", 
				"enable", GenerateXMLDebug.SET_OPERATION,
				cacImpl.getMgmtServiceGuiName(), cacImpl.getMgmtServiceName());
		cacObj.setEnable(CLICommonFunc.getAhOnlyAct(cacImpl.isEnableCac()));
		
		/** element: <cac>.<airtime-per-second> */
		oDebug.debug("/configuration/cac", 
				"airtime-per-second", GenerateXMLDebug.SET_VALUE,
				cacImpl.getMgmtServiceGuiName(), cacImpl.getMgmtServiceName());
		Object[][] airTimeParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, cacImpl.getCacAirtimePerSecond()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		cacObj.setAirtimePerSecond(
				(CacObj.AirtimePerSecond)CLICommonFunc.createObjectWithName(CacObj.AirtimePerSecond.class, airTimeParm)
		);
		
		/** element: <cac>.<roaming> */
		CacObj.Roaming roamingObj = new CacObj.Roaming();
		cacChildLevel_1.add(roamingObj);
		cacObj.setRoaming(roamingObj);
		
		generateCacLevel_2();
	}
	
	private void generateCacLevel_2() throws Exception{
		/**
		 * <cac>.<roaming>					CacObj.Roaming
		 */
		for(Object childObj : cacChildLevel_1){
			
			if(childObj instanceof CacObj.Roaming){
				CacObj.Roaming roamingObj = (CacObj.Roaming)childObj;
				
				/** element: <cac>.<roaming>.<airtime-percentage> */
				oDebug.debug("/configuration/cac/roaming", 
						"airtime-percentage", GenerateXMLDebug.SET_VALUE,
						cacImpl.getMgmtServiceGuiName(), cacImpl.getMgmtServiceName());
				Object[][] roamingAirtimeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, cacImpl.getRoamingAirtime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				roamingObj.setAirtimePercentage(
						(CacObj.Roaming.AirtimePercentage)CLICommonFunc.createObjectWithName(CacObj.Roaming.AirtimePercentage.class, roamingAirtimeParm)
				);
			}
		}
		cacChildLevel_1.clear();
	}

}
