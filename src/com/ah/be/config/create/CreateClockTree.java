package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ClockProfileInt;
import com.ah.xml.be.config.ClockObj;
import com.ah.xml.be.config.TimeZoneExtra;
import com.ah.xml.be.config.TimeZoneOffset;

/**
 * 
 * @author zhang
 *
 */
public class CreateClockTree {
	
	private ClockProfileInt clockImpl;
	private ClockObj clockObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> clockChildList_1 = new ArrayList<Object>();
	private List<Object> clockChildList_2 = new ArrayList<Object>();

	public CreateClockTree(ClockProfileInt clockImpl, GenerateXMLDebug oDebug) {
		this.clockImpl = clockImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		clockObj = new ClockObj();
		generateClockLevel_1();
	}
	
	public ClockObj getClockObj(){
		return clockObj;
	}
	
	private void generateClockLevel_1() throws Exception{
		/**
		 * <clock>				ClockObj
		 */
		
		/** attribute: updateTime */
		clockObj.setUpdateTime(
				clockImpl.getUpdateTime()
		);
		
		/** element: <clock>.<time-zone> */
		ClockObj.TimeZone timeZoneObj = new ClockObj.TimeZone();
		clockChildList_1.add(timeZoneObj);
		clockObj.setTimeZone(timeZoneObj);
		
		generateClockLevel_2();
	}
	
	private void generateClockLevel_2() throws Exception{
		/**
		 * <clock>.<time-zone>				ClockObj.TimeZone
		 */
		for(Object childObj : clockChildList_1){
			
			/** element: <clock>.<time-zone> */
			if(childObj instanceof ClockObj.TimeZone){
				ClockObj.TimeZone timeZoneObj = (ClockObj.TimeZone)childObj;
				
				/** element: <clock>.<time-zone>.<cr> */
				TimeZoneOffset timeZone = new TimeZoneOffset();
				clockChildList_2.add(timeZone);
				timeZoneObj.setCr(timeZone);
				
				/** element: <clock>.<time-zone>.<daylight-saving-time> */
				oDebug.debug("/configuration/clock/time-zone", 
						"daylight-saving-time", GenerateXMLDebug.CONFIG_ELEMENT,
						clockImpl.getMgmtServiceTimeGuiName(), clockImpl.getMgmtServiceTimeName());
				if(clockImpl.isConfigureDaylightTime() ){
					
					oDebug.debug("/configuration/clock/time-zone", 
							"daylight-saving-time", GenerateXMLDebug.SET_VALUE,
							clockImpl.getMgmtServiceTimeGuiName(), clockImpl.getMgmtServiceTimeName());
					Object[][] dayLighParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, clockImpl.getDayLightTime()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()},
							{CLICommonFunc.ATTRIBUTE_QUOTEPROHIBITED, CLICommonFunc.getYesDefault()}
					};
					timeZoneObj.setDaylightSavingTime(
							(ClockObj.TimeZone.DaylightSavingTime)CLICommonFunc.createObjectWithName(
									ClockObj.TimeZone.DaylightSavingTime.class, dayLighParm)
					);
				}
			}
		}
		clockChildList_1.clear();
		generateClockLevel_3();
	}
	
	private void generateClockLevel_3() throws Exception{
		/**
		 * <clock>.<time-zone>.<cr>						TimeZoneOffset
		 */
		for(Object childObj : clockChildList_2){
			
			/** element: <clock>.<time-zone>.<cr> */
			if(childObj instanceof TimeZoneOffset){
				TimeZoneOffset timeZone = (TimeZoneOffset)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/clock/time-zone", 
						"cr", GenerateXMLDebug.SET_VALUE,
						clockImpl.getMgmtServiceTimeGuiName(), clockImpl.getMgmtServiceTimeName());
				timeZone.setValue(clockImpl.getTimeZone());
				
				/** element: <clock>.<time-zone>.<cr>.<cr> */
				oDebug.debug("/configuration/clock/time-zone/cr", 
						"cr", GenerateXMLDebug.SET_VALUE,
						clockImpl.getMgmtServiceTimeGuiName(), clockImpl.getMgmtServiceTimeName());
				Object[][] timeZoneMinParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, clockImpl.getTimeZoneMin()}
				};
				timeZone.setCr(
						(TimeZoneExtra)CLICommonFunc.createObjectWithName(TimeZoneExtra.class, timeZoneMinParm)
				);
			}
		}
		clockChildList_2.clear();
	}
}
