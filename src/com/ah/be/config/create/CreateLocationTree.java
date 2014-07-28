package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.LocationProfileInt;
import com.ah.xml.be.config.LocationAerohive;
import com.ah.xml.be.config.LocationEkahau;
import com.ah.xml.be.config.LocationEkahauPort;
import com.ah.xml.be.config.LocationEkahauServer;
import com.ah.xml.be.config.LocationEkahauServerConfig;
import com.ah.xml.be.config.LocationObj;

/**
 * @author zhang
 * @version 2007-12-18  06:00:45
 */

public class CreateLocationTree {
	
	private LocationProfileInt locationImpl;
	private LocationObj locationObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> locationChildList_1 = new ArrayList<Object>();
	private List<Object> locationChildList_2 = new ArrayList<Object>();
	private List<Object> locationChildList_3 = new ArrayList<Object>();

	public CreateLocationTree(LocationProfileInt locationImpl, GenerateXMLDebug oDebug){
		this.locationImpl = locationImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws CreateXMLException, Exception{
		if(locationImpl.isConfigLocation()){
			locationObj = new LocationObj();
			generateLocationLevel_1();
		}
	}
	
	public LocationObj getLocationObj(){
		return this.locationObj;
	}
	
	private void generateLocationLevel_1() throws Exception{
		/**
		 * <location>		LocationObj
		 */
		
//		/** attribute: updateTime */
//		locationObj.setUpdateTime(locationImpl.getUpdateTime());
		
		/** element: <location>.<rateThreshold> */
		oDebug.debug("/configuration/location", 
				"rateThreshold", GenerateXMLDebug.CONFIG_ELEMENT,
				locationImpl.getLocationGuiName(), locationImpl.getLocationName());
		if(locationImpl.isConfigRateThreshold()){
			LocationObj.RateThreshold rateThresholdObj = new LocationObj.RateThreshold();
			locationChildList_1.add(rateThresholdObj);
			locationObj.setRateThreshold(rateThresholdObj);
		}
		
		if(locationImpl.isLocationAeroscout()){
			
			/** element: <location>.<aeroscout> */
			LocationObj.Aeroscout aeroscoutObj = new LocationObj.Aeroscout();
			locationChildList_1.add(aeroscoutObj);
			locationObj.setAeroscout(aeroscoutObj);
		}
		
		if(locationImpl.isLocationAerohive()){
			
			/** element: <location>.<aerohive> */
			LocationAerohive aerohiveObj = new LocationAerohive();
			locationChildList_1.add(aerohiveObj);
			locationObj.setAerohive(aerohiveObj);
		}
		
		if(locationImpl.isConfigLocationEkahau()){
			
			/** element: <location>.<ekahau> */
			LocationEkahau ekahauObj = new LocationEkahau();
			locationChildList_1.add(ekahauObj);
			locationObj.setEkahau(ekahauObj);
		}
		
		/** element: <location>.<tzsp> */
		locationObj.setTzsp(locationObj.getEkahau());
		
		generateLocationLevel_2();
	}
	
	private void generateLocationLevel_2() throws Exception{
		/**
		 * <location>.<aeroscout>			LocationObj.Aeroscout
		 * <location>.<rateThreshold>		LocationObj.RateThreshold
		 * <location>.<aerohive>			LocationAerohive
		 * <location>.<ekahau>				LocationEkahau
		 */
		for(Object childObj : locationChildList_1){
			
			/** element: <location>.<aeroscout> */
			if(childObj instanceof LocationObj.Aeroscout){
				LocationObj.Aeroscout aeroscoutObj = (LocationObj.Aeroscout)childObj;
				
				/** element: <location>.<aeroscout>.<enable> */
				oDebug.debug("/configuration/location/aeroscout", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aeroscoutObj.setEnable(
						CLICommonFunc.getAhOnlyAct(locationImpl.isEnableLocationServer())
				);
				
				/** element: <location>.<aeroscout>.<rogue-ap> */
				oDebug.debug("/configuration/location/aeroscout", 
						"rogue-ap", GenerateXMLDebug.SET_OPERATION,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aeroscoutObj.setRogueAp(
						CLICommonFunc.getAhOnlyAct(locationImpl.isEnableRogueAp())
				);
				
				/** element: <location>.<aeroscout>.<station> */
				oDebug.debug("/configuration/location/aeroscout", 
						"station", GenerateXMLDebug.SET_OPERATION,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aeroscoutObj.setStation(
						CLICommonFunc.getAhOnlyAct(locationImpl.isEnableStation())
				);
				
				/** element: <location>.<aeroscout>.<tag> */
				oDebug.debug("/configuration/location/aeroscout", 
						"tag", GenerateXMLDebug.SET_OPERATION,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aeroscoutObj.setTag(
						CLICommonFunc.getAhOnlyAct(locationImpl.isEnableTag())
				);
				
				/** element: <location>.<aeroscout>.<server> */
				oDebug.debug("/configuration/location/aeroscout", 
						"server", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aeroscoutObj.setServer(
						CLICommonFunc.createAhStringActObj(locationImpl.getLocationServer(), CLICommonFunc.getYesDefault())
				);
			}
			
			/** element: <location>.<rateThreshold> */
			if(childObj instanceof LocationObj.RateThreshold){
				LocationObj.RateThreshold aeroscoutObj = (LocationObj.RateThreshold)childObj;
				
				/** element: <location>.<rateThreshold>.<tag> */
				oDebug.debug("/configuration/location/rateThreshold", 
						"tag", GenerateXMLDebug.CONFIG_ELEMENT,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				if(locationImpl.isEnableTag()){
					
					oDebug.debug("/configuration/location/rateThreshold", 
							"tag", GenerateXMLDebug.SET_VALUE,
							locationImpl.getLocationGuiName(), locationImpl.getLocationName());
					Object[][] tagParm = {
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()},
							{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRateThresholdTag()}
					};
					aeroscoutObj.setTag(
							(LocationObj.RateThreshold.Tag)CLICommonFunc.createObjectWithName(LocationObj.RateThreshold.Tag.class, tagParm)
					);
				}
				
				/** element: <location>.<rateThreshold>.<station> */
				oDebug.debug("/configuration/location/rateThreshold", 
						"station", GenerateXMLDebug.CONFIG_ELEMENT,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				if(locationImpl.isEnableStation()){
					
					oDebug.debug("/configuration/location/rateThreshold", 
							"station", GenerateXMLDebug.SET_VALUE,
							locationImpl.getLocationGuiName(), locationImpl.getLocationName());
					Object[][] stationParm = {
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()},
							{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRateThresholdStation()}
					};
					aeroscoutObj.setStation(
							(LocationObj.RateThreshold.Station)CLICommonFunc.createObjectWithName(LocationObj.RateThreshold.Station.class, stationParm)
					);
				}
				
				/** element: <location>.<rateThreshold>.<rogue-ap> */
				oDebug.debug("/configuration/location/rateThreshold", 
						"rogue-ap", GenerateXMLDebug.CONFIG_ELEMENT,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				if(locationImpl.isEnableRogueAp()){
					
					oDebug.debug("/configuration/location/rateThreshold", 
							"rogue-ap", GenerateXMLDebug.SET_VALUE,
							locationImpl.getLocationGuiName(), locationImpl.getLocationName());
					Object[][] rogueParm = {
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()},
							{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRateThresholdRogue()}
					};
					aeroscoutObj.setRogueAp(
							(LocationObj.RateThreshold.RogueAp)CLICommonFunc.createObjectWithName(LocationObj.RateThreshold.RogueAp.class, rogueParm)
					);
				}
			}
			
			/** element: <location>.<aerohive> */
			if(childObj instanceof LocationAerohive){
				LocationAerohive aerohiveObj = (LocationAerohive)childObj;
				
				/** element: <location>.<aerohive>.<enable> */
				oDebug.debug("/configuration/location/aerohive", 
						"enable", GenerateXMLDebug.CONFIG_ELEMENT,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				aerohiveObj.setEnable(CLICommonFunc.getAhOnlyAct(locationImpl.isEnableLocationServer()));
				
				/** element: <location>.<aerohive>.<rssi-update-threshold> */
				oDebug.debug("/configuration/location/aerohive", 
						"rssi-update-threshold", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] rssiThresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRssiUpdateThreshold()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				aerohiveObj.setRssiUpdateThreshold(
						(LocationAerohive.RssiUpdateThreshold)CLICommonFunc.createObjectWithName(LocationAerohive.RssiUpdateThreshold.class, rssiThresholdParm)
				);
				
				/** element: <location>.<aerohive>.<rssi-valid-period> */
				oDebug.debug("/configuration/location/aerohive", 
						"rssi-valid-period", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] rssiPeriodParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRssiValidPeriod()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				aerohiveObj.setRssiValidPeriod(
						(LocationAerohive.RssiValidPeriod)CLICommonFunc.createObjectWithName(LocationAerohive.RssiValidPeriod.class, rssiPeriodParm)
				);
				
				/** element: <location>.<aerohive>.<rssi-hold-time> */
				oDebug.debug("/configuration/location/aerohive", 
						"rssi-hold-time", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] holdTimeParm ={
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getRssiHoldTime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				aerohiveObj.setRssiHoldTime(
						(LocationAerohive.RssiHoldTime)CLICommonFunc.createObjectWithName(LocationAerohive.RssiHoldTime.class, holdTimeParm)
				);
				
				/** element: <location>.<aerohive>.<report-interval> */
				oDebug.debug("/configuration/location/aerohive", 
						"report-interval", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] reportIntervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getReportInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				aerohiveObj.setReportInterval(
						(LocationAerohive.ReportInterval)CLICommonFunc.createObjectWithName(LocationAerohive.ReportInterval.class, reportIntervalParm)
				);
		
				/** element: <location>.<aerohive>.<suppress-report> */
				oDebug.debug("/configuration/location/aerohive", 
						"suppress-report", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] suppressReportParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getSuppressReport()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				aerohiveObj.setSuppressReport(
						(LocationAerohive.SuppressReport)CLICommonFunc.createObjectWithName(LocationAerohive.SuppressReport.class, suppressReportParm)
				);
				
				/** element: <location>.<aerohive>.<mac> */
				for(int index=0; index<locationImpl.getAerohiveMacSize(); index++){
					aerohiveObj.getMac().add(CLICommonFunc.createAhNameActValue(locationImpl.getAerohiveMacAddr(index), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <location>.<aerohive>.<oui> */
				for(int index=0; index<locationImpl.getAerohiveOuiSize(); index++){
					aerohiveObj.getOui().add(CLICommonFunc.createAhNameActValue(locationImpl.getAerohiveOuiAddr(index), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <location>.<aerohive>.<list-match> */
				LocationAerohive.ListMatch matchObj = new LocationAerohive.ListMatch();
				matchObj.setEnable(CLICommonFunc.getAhOnlyAct(locationImpl.isEnableListMatch()));
				aerohiveObj.setListMatch(matchObj);
			}
			
			/** element: <location>.<ekahau> */
			if(childObj instanceof LocationEkahau){
				LocationEkahau ekahauObj = (LocationEkahau)childObj;
				
				/** element: <location>.<ekahau>.<enable> */
				oDebug.debug("/configuration/location/ekahau", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				ekahauObj.setEnable(CLICommonFunc.getAhOnlyAct(locationImpl.isConfigLocationEkahau()));
				
				/** element: <location>.<ekahau>.<mcast-mac> */
				oDebug.debug("/configuration/location/ekahau", 
						"mcast-mac", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				ekahauObj.setMcastMac(CLICommonFunc.createAhStringActObj(locationImpl.getMcastMac(), CLICommonFunc.getYesDefault()));
				
				/** element: <location>.<ekahau>.<server-config> */
				if(locationImpl.isConfigEkahauServer()){
					LocationEkahauServerConfig serverConfigObj = new LocationEkahauServerConfig();
					locationChildList_2.add(serverConfigObj);
					ekahauObj.setServerConfig(serverConfigObj);
				}
			}
		}
		locationChildList_1.clear();
		generateLocationLevel_3();
	}
	
	private void generateLocationLevel_3() throws Exception{
		/**
		 * <location>.<ekahau>.<server-config>					LocationEkahauServerConfig
		 */
		for(Object childObj : locationChildList_2){
			
			/** element: <location>.<ekahau>.<server-config> */
			if(childObj instanceof LocationEkahauServerConfig){
				LocationEkahauServerConfig serverConfigObj = (LocationEkahauServerConfig)childObj;
				
				/** attribute: operation */
				serverConfigObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <location>.<ekahau>.<server-config>.<server> */
				LocationEkahauServer serverObj = new LocationEkahauServer();
				locationChildList_3.add(serverObj);
				serverConfigObj.setServer(serverObj);
			}
		}
		locationChildList_2.clear();
		generateLocationLevel_4();
	}
	
	private void generateLocationLevel_4() throws Exception{
		/**
		 * <location>.<ekahau>.<server-config>.<server>			LocationEkahauServer
		 */
		for(Object childObj : locationChildList_3){
			
			/** element: <location>.<ekahau>.<server-config>.<server> */
			if(childObj instanceof LocationEkahauServer){
				LocationEkahauServer serverObj = (LocationEkahauServer)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/location/ekahau/server-config", 
						"server", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				serverObj.setValue(locationImpl.getEkahauServerValue());
				
				/** element: <location>.<ekahau>.<server-config>.<server>.<port> */
				oDebug.debug("/configuration/location/ekahau/server-config/server", 
						"port", GenerateXMLDebug.SET_VALUE,
						locationImpl.getLocationGuiName(), locationImpl.getLocationName());
				Object[][] portArg = {
						{CLICommonFunc.ATTRIBUTE_VALUE, locationImpl.getEkahauServerPort()}
				};
				serverObj.setPort(
						(LocationEkahauPort)CLICommonFunc.createObjectWithName(LocationEkahauPort.class, portArg)
				);
			}
		}
		locationChildList_3.clear();
	}
}
