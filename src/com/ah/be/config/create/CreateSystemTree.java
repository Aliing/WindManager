package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.SystemProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.PowerModeObj;
import com.ah.xml.be.config.SystemFans;
import com.ah.xml.be.config.SystemObj;
import com.ah.xml.be.config.SystemPowerMode;
import com.ah.xml.be.config.SystemWebServer;

/**
 * @author zhang
 * @version 2008-4-15 15:19:13
 */

public class CreateSystemTree {

	private SystemProfileInt systemImpl;
	private SystemObj systemObj;
	private GenerateXMLDebug oDebug;
	
	private List<Object> radioChildList_1 = new ArrayList<Object>();
	private List<Object> radioChildList_2 = new ArrayList<Object>();
	
	public CreateSystemTree(SystemProfileInt systemImpl, GenerateXMLDebug oDebug){
		this.systemImpl = systemImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		/** element: <system> */
		systemObj = new SystemObj();
		generateSystemLevel_1();
	}
	
	public SystemObj getSystemObj(){
		return this.systemObj;
	}
	
	private void generateSystemLevel_1() throws Exception{
		/**
		 * <system>				SystemObj
		 */
		
		if(systemImpl.isConfigMgmtOpt()){
			
			/** element: <system>.<temperature> */
			oDebug.debug("/configuration/system", "temperature",
					GenerateXMLDebug.CONFIG_ELEMENT,
					systemImpl.getMgmtOptionGuiName(),
					systemImpl.getMgmtOptionName());
			SystemObj.Temperature temperatureObj = new SystemObj.Temperature();
			radioChildList_1.add(temperatureObj);
			systemObj.setTemperature(temperatureObj);

			/** element: <system>.<fans> */
			oDebug.debug("/configuration/system", "fans",
					GenerateXMLDebug.CONFIG_ELEMENT,
					systemImpl.getMgmtOptionGuiName(),
					systemImpl.getMgmtOptionName());
			SystemFans fansObj = new SystemFans();
			radioChildList_1.add(fansObj);
			systemObj.setFans(fansObj);
			
			/** element: <system>.<smart-poe> */
			oDebug.debug("/configuration/system",
					"smart-poe", GenerateXMLDebug.CONFIG_ELEMENT,
					systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
			if(systemImpl.isHiveAp11n() && systemImpl.isConfigHighThreshold()){
				SystemObj.SmartPoe smartPoeObj = new SystemObj.SmartPoe();
				radioChildList_1.add(smartPoeObj);
				systemObj.setSmartPoe(smartPoeObj);
			}
			
			/** element: <system>.<led> */
			SystemObj.Led ledObj = new SystemObj.Led();
			radioChildList_1.add(ledObj);
			systemObj.setLed(ledObj);
			
			/** element: <system>.<icmp-redirect> */
			SystemObj.IcmpRedirect icmpObj = new SystemObj.IcmpRedirect();
			radioChildList_1.add(icmpObj);
			systemObj.setIcmpRedirect(icmpObj);
		}
		
		/** element: <system>.<web-server> */
		SystemWebServer webServerObj = new SystemWebServer();
		radioChildList_1.add(webServerObj);
		systemObj.setWebServer(webServerObj);
		
		/** element: <system>.<power-mode>**/
		if(systemImpl.isSupportPoEMode()){
			SystemPowerMode powerMode = new SystemPowerMode();
			radioChildList_1.add(powerMode);
			systemObj.setPowerMode(powerMode);
		}
		
		generateSystemLevel_2();
	}
	
	private void generateSystemLevel_2() throws Exception{
		/**
		 * <system>.<temperature>				SystemObj.Temperature
		 * <system>.<smart-poe>					SystemObj.SmartPoe
		 * <system>.<led>						SystemObj.Led
		 * <system>.<icmp-redirect>				SystemObj.IcmpRedirect
		 * <system>.<web-server>				SystemWebServer
		 * <system>.<fans>						SystemFans
		 */
		for(Object childObj : radioChildList_1){
			
			/** element: <system>.<temperature> */
			if(childObj instanceof SystemObj.Temperature){
				SystemObj.Temperature temperatureObj = (SystemObj.Temperature)childObj;
				
				/** element: <system>.<temperature>.<high-threshold> */
				oDebug.debug("/configuration/system/temperature",
						"high-threshold", GenerateXMLDebug.SET_VALUE,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				Object[][] temperatureParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, systemImpl.getHighThreshold()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				temperatureObj.setHighThreshold(
						(SystemObj.Temperature.HighThreshold)CLICommonFunc.createObjectWithName(
								SystemObj.Temperature.HighThreshold.class, temperatureParm)
				);
			}
			
			/** element: <system>.<fans> */
			if(childObj instanceof SystemFans){
				SystemFans fansObj = (SystemFans)childObj;
										
				fansObj.setUnderspeedThreshold(CLICommonFunc.createAhIntActObj(systemImpl.getUnderSpeedThreshold(), true));
			}
			
			/** element: <system>.<smart-poe> */
			if(childObj instanceof SystemObj.SmartPoe){
				SystemObj.SmartPoe smartPoeObj = (SystemObj.SmartPoe)childObj;
				
				/** element: <system>.<smart-poe>.<enable> */
				oDebug.debug("/configuration/system/smart-poe",
						"enable", GenerateXMLDebug.SET_OPERATION,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				smartPoeObj.setEnable(CLICommonFunc.getAhOnlyAct(systemImpl.getSmartPoeEnable()));
			}
			
			/** element: <system>.<led> */
			if(childObj instanceof SystemObj.Led){
				SystemObj.Led ledObj = (SystemObj.Led)childObj;
				
				/** element: <system>.<led>.<brightness> */
				SystemObj.Led.Brightness brightnessObj = new SystemObj.Led.Brightness();
				radioChildList_2.add(brightnessObj);
				ledObj.setBrightness(brightnessObj);
			}
			
			/** element: <system>.<icmp-redirect> */
			if(childObj instanceof SystemObj.IcmpRedirect){
				SystemObj.IcmpRedirect icmpObj = (SystemObj.IcmpRedirect)childObj;
				
				/** element: <system>.<icmp-redirect>.<enable> */
				oDebug.debug("/configuration/system/icmp-redirect",
						"enable", GenerateXMLDebug.SET_OPERATION,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				icmpObj.setEnable(CLICommonFunc.getAhOnlyAct(systemImpl.isIcmpRedirectEnable()));
			}
			
			/** element: <system>.<web-server> */
			if(childObj instanceof SystemWebServer){
				SystemWebServer webServerObj = (SystemWebServer)childObj;
				
				/** element: <system>.<web-server>.<enable> */
				oDebug.debug("/configuration/system/web-server",
						"enable", GenerateXMLDebug.SET_OPERATION,
						null, null);
				webServerObj.setEnable(CLICommonFunc.getAhOnlyAct(systemImpl.isWebServerEnable()));
			}
			
			/** element: <system>.<power-mode>.<auto/802.3af/802.3at>.<primary-eth><eth0/eht1>**/
			if(childObj instanceof SystemPowerMode){
				SystemPowerMode systemPowerMode = (SystemPowerMode)childObj;
				
				if(HiveAp.POE_802_3_AT == systemImpl.getPoEPowerMode()){
					systemPowerMode.set8023At("");
				}else{
					PowerModeObj powerMode = new PowerModeObj();
					powerMode.setPrimaryEth(CLICommonFunc.getAhString(systemImpl.getPoEPrimaryEth()));
					if(HiveAp.POE_802_3_AF == systemImpl.getPoEPowerMode()){
						systemPowerMode.set8023Af(powerMode);
					}else{
						systemPowerMode.setAuto(powerMode);
					}
				}

				systemPowerMode.setOperation(CLICommonFunc.getAhEnumAct(true));
			}
		}
		radioChildList_1.clear();
		generateSystemLevel_3();
	}
	
	private void generateSystemLevel_3(){
		/**
		 * <system>.<led>.<brightness>		SystemObj.Led.Brightness
		 * <system>.<fans>					FansOverspeedThreshold
		 */
		for(Object childObj : radioChildList_2){
			
			/** element: <system>.<led>.<brightness> */
			if(childObj instanceof SystemObj.Led.Brightness){
				SystemObj.Led.Brightness brightnessObj = (SystemObj.Led.Brightness)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/system/led",
						"brightness", GenerateXMLDebug.SET_OPERATION,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				brightnessObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <system>.<led>.<brightness>.<soft> */
				oDebug.debug("/configuration/system/led/brightness",
						"soft", GenerateXMLDebug.CONFIG_ELEMENT,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				if(systemImpl.isSystemLedSoft()){
					brightnessObj.setSoft("");
				}
				
				/** element: <system>.<led>.<brightness>.<dim> */
				oDebug.debug("/configuration/system/led/brightness",
						"dim", GenerateXMLDebug.CONFIG_ELEMENT,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				if(systemImpl.isSystemLedDim()){
					brightnessObj.setDim("");
				}
				
				/** element: <system>.<led>.<brightness>.<off> */
				oDebug.debug("/configuration/system/led/brightness",
						"off", GenerateXMLDebug.CONFIG_ELEMENT,
						systemImpl.getMgmtOptionGuiName(), systemImpl.getMgmtOptionName());
				if(systemImpl.isSystemLedOff()){
					brightnessObj.setOff("");
				}
			}
			
		}
	}
}
