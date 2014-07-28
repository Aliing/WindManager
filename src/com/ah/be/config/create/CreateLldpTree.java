package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.LldpProfileInt;
import com.ah.xml.be.config.AhOnlyAct;
import com.ah.xml.be.config.LldpCdpHoldtime;
import com.ah.xml.be.config.LldpCdpMaxEntries;
import com.ah.xml.be.config.LldpCdpTimer;
import com.ah.xml.be.config.LldpEnable;
import com.ah.xml.be.config.LldpFastStart;
import com.ah.xml.be.config.LldpMaxPower;
import com.ah.xml.be.config.LldpObj;

/**
 * @author zhang
 * @version 2008-10-16 09:47:12
 */

public class CreateLldpTree {
	
	private LldpProfileInt lldpImpl;
	
	private LldpObj lldpObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> lldpChildLevel_1 = new ArrayList<Object>();

	public CreateLldpTree(LldpProfileInt lldpImpl, GenerateXMLDebug oDebug) throws Exception{
		this.lldpImpl = lldpImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(lldpImpl.isConfigLldp()){
			lldpObj = new LldpObj();
			
			genereateLldpLevel_1();
		}
	}
	
	public LldpObj getLldpObj(){
		return this.lldpObj;
	}
	
	private void genereateLldpLevel_1() throws Exception{
		/**
		 * <lldp>					LldpObj
		 */
		
		if(lldpImpl.isEnableLldp()){
			LldpEnable lldpEnableObj = new LldpEnable();
			lldpChildLevel_1.add(lldpEnableObj);
			lldpObj.setCr(lldpEnableObj);
			
			/** element: <lldp>.<enable> */
			lldpObj.setEnable(CLICommonFunc.getAhOnlyAct(true));
			
			if (lldpImpl.isOverrideConfig()) {
				oDebug.debug("/configuration/lldp", 
						"max-power", GenerateXMLDebug.SET_VALUE,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				Object[][] maxPowerParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, lldpImpl.getLldpMaxPower()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				lldpObj.setMaxPower((LldpMaxPower)CLICommonFunc.createObjectWithName(LldpMaxPower.class, maxPowerParm));
				
				/** element: <lldp>.<holdtime> */
				oDebug.debug("/configuration/lldp", 
						"holdtime", GenerateXMLDebug.SET_VALUE,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				Object[][] holdTimeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, lldpImpl.getHoldTime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				lldpObj.setHoldtime(
						(LldpCdpHoldtime)CLICommonFunc.createObjectWithName(LldpCdpHoldtime.class, holdTimeParm)
				);
				
				/** element: <lldp>.<timer> */
				oDebug.debug("/configuration/lldp", 
						"timer", GenerateXMLDebug.SET_VALUE,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				Object[][] timerParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, lldpImpl.getTimer()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				lldpObj.setTimer(
						(LldpCdpTimer)CLICommonFunc.createObjectWithName(LldpCdpTimer.class, timerParm)
				);
				
				/** element: <lldp>.<max-entries> */
				oDebug.debug("/configuration/lldp", 
						"max-entries", GenerateXMLDebug.SET_VALUE,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				Object[][] maxEntriesParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, lldpImpl.getLldpMaxEntries()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				lldpObj.setMaxEntries(
						(LldpCdpMaxEntries)CLICommonFunc.createObjectWithName(LldpCdpMaxEntries.class, maxEntriesParm)
				);
				
				/** element: <lldp>.<reinit> */
			    lldpObj.setReinit(CLICommonFunc.createAhIntActObj(lldpImpl.getDelayTime(), true));
			    /** element: <lldp>.<fast-start> */
			    LldpFastStart fastStart = new LldpFastStart();
			    lldpObj.setFastStart(fastStart);
			    lldpChildLevel_1.add(fastStart);
			}
		}
		
		/** element: <lldp>.<cdp> */
		oDebug.debug("/configuration/lldp", 
				"cdp", GenerateXMLDebug.CONFIG_ELEMENT,
				lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
		if(lldpImpl.isConfigCdp()){
			LldpObj.Cdp cdpObj = new LldpObj.Cdp();
			lldpChildLevel_1.add(cdpObj);
			lldpObj.setCdp(cdpObj);
		}
		
		genereateLldpLevel_2();
	}
	
	private void genereateLldpLevel_2() throws Exception{
		/**
		 * <lldp>.<cdp>				LldpObj.Cdp
		 * <lldp>.<cr>				LldpEnable
		 */
		for(Object childObj : lldpChildLevel_1){
			
			/** element: <lldp>.<cdp> */
			if(childObj instanceof LldpObj.Cdp){
				LldpObj.Cdp cdpObj = (LldpObj.Cdp)childObj;
				
				/** element: <lldp>.<cdp>.<cr> */
				oDebug.debug("/configuration/lldp/cdp",
						"cr", GenerateXMLDebug.SET_OPERATION,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				cdpObj.setCr(CLICommonFunc.getAhOnlyAct(lldpImpl.isEnableCdp()));
				
				/** element: <lldp>.<cdp>.<max-entries> */
				oDebug.debug("/configuration/lldp/cdp",
						"max-entries", GenerateXMLDebug.SET_VALUE,
						lldpImpl.getLLDPGuiName(), lldpImpl.getLLDPName());
				Object[][] maxEntriesParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, lldpImpl.getCdpMaxEntries()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				cdpObj.setMaxEntries(
						(LldpCdpMaxEntries)CLICommonFunc.createObjectWithName(LldpCdpMaxEntries.class, maxEntriesParm)
				);
			}
			
			/** element: <lldp>.<cr> */
			if(childObj instanceof LldpEnable){
				LldpEnable lldpEnableObj = (LldpEnable)childObj;
				
				/** attribute: operation */
				lldpEnableObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				if(lldpImpl.isEnableReceiveOnly()){
					/** element: <lldp>.<cr>.<receive-only> */
					lldpEnableObj.setReceiveOnly(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}else{
					/** element: <lldp>.<cr>.<cr> */
					lldpEnableObj.setCr(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
			}
			
			if(childObj instanceof LldpFastStart){
				LldpFastStart fastStart = (LldpFastStart) childObj;
				fastStart.setRepeatCount(CLICommonFunc.createAhIntActObj(lldpImpl.getRepeatCount(), true));
			}
		}
		lldpChildLevel_1.clear();
	}
}
