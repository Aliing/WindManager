package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ScheduleProfileInt;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateScheduleTree {
	
	private ScheduleProfileInt scheduleProfile;
	private ScheduleObj scheduleObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> scheduleChildList_1 = new ArrayList<Object>();
	private List<Object> scheduleChildList_2 = new ArrayList<Object>();
	private List<Object> scheduleChildList_3 = new ArrayList<Object>();
	private List<Object> scheduleChildList_4 = new ArrayList<Object>();
	private List<Object> scheduleChildList_5 = new ArrayList<Object>();
	private List<Object> scheduleChildList_6 = new ArrayList<Object>();
	private List<Object> scheduleChildList_7 = new ArrayList<Object>();
	private List<Object> scheduleChildList_8 = new ArrayList<Object>();
	private List<Object> scheduleChildList_9 = new ArrayList<Object>();

	public CreateScheduleTree(ScheduleProfileInt scheduleImp, GenerateXMLDebug oDebug){
		scheduleProfile = scheduleImp;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		scheduleObj = new  ScheduleObj();
		generateScheduleLevel_1();
	}
	
	public ScheduleObj getScheduleObj(){
		return scheduleObj;
	}
	
	private void generateScheduleLevel_1(){
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"schedule", GenerateXMLDebug.SET_NAME,
				scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
		scheduleObj.setName(scheduleProfile.getScheduleName() );
		
		/** attribute: operation */
		scheduleObj.setOperation(
				CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault())
		);
		
		/** attribute: updateTime */
		scheduleObj.setUpdateTime(scheduleProfile.getScheduleUpdateTime());
		
		if(scheduleProfile.isSelectOnceType()){
			/** element: <schedule>.<once> */
			oDebug.debug("/configuration/schedule", 
					"once", GenerateXMLDebug.CONFIG_ELEMENT,
					scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
			ScheduleOnce onceObj = new ScheduleOnce();
			scheduleChildList_1.add(onceObj);
			scheduleObj.setOnce(onceObj);
		}else{
			/** element: <schedule>.<recurrent> */
			oDebug.debug("/configuration/schedule", 
					"recurrent", GenerateXMLDebug.CONFIG_ELEMENT,
					scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
			ScheduleRecurrent recurrentObj = new ScheduleRecurrent();
			scheduleChildList_1.add(recurrentObj);
			scheduleObj.setRecurrent(recurrentObj);
		}
		
		generateScheduleLevel_2();
	}
	
	private void generateScheduleLevel_2(){
		
		/**
		 * <schedule>.<once>		ScheduleOnce
		 * <schedule>.<recurrent>	ScheduleRecurrent
		 */
		
		for(Object childObj : scheduleChildList_1){
			
			if(childObj instanceof ScheduleOnce){
				ScheduleOnce onceObj = (ScheduleOnce)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule", 
						"once", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				onceObj.setValue(scheduleProfile.getFromDateAndTimeOnce());
				
				/** attribute: quoteProhibited */
				onceObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: operation */
				onceObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <schedule>.<once>.<to> */
				ScheduleOnceTo onceToObj = new ScheduleOnceTo();
				scheduleChildList_2.add(onceToObj);
				onceObj.setTo(onceToObj);
			}
			
			if(childObj instanceof ScheduleRecurrent){
				ScheduleRecurrent recurrentObj = (ScheduleRecurrent)childObj;
				
				/** attribute: operation */
				recurrentObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <schedule>.<recurrent>.<date-range>*/
				ScheduleDateRange dateRangeObj = new ScheduleDateRange();
				scheduleChildList_2.add(dateRangeObj);
				recurrentObj.setDateRange(dateRangeObj);
			}
		}
		
		generateScheduleLevel_3();
	}
	
	private void generateScheduleLevel_3(){
		/**
		 * <schedule>.<once>.<to> 					ScheduleOnceTo
		 * <schedule>.<recurrent>.<date-range>		ScheduleDateRange
		 */
		
		for(Object childObj : scheduleChildList_2){
			
			/** element: <schedule>.<once>.<to> */
			if(childObj instanceof ScheduleOnceTo){
				ScheduleOnceTo onceToObj = (ScheduleOnceTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/once", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				onceToObj.setValue(scheduleProfile.getToDateAndTimeOnce());
				
				/** attribute: quoteProhibited */
				onceToObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <schedule>.<recurrent>.<date-range> */
			if(childObj instanceof ScheduleDateRange){
				ScheduleDateRange dateRangeObj = (ScheduleDateRange)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent", 
						"date-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				dateRangeObj.setValue(scheduleProfile.getFromDateRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to> */
				ScheduleDateRangeTo recurrentToDateObj = new ScheduleDateRangeTo();
				scheduleChildList_3.add(recurrentToDateObj);
				dateRangeObj.setTo(recurrentToDateObj);
			}
		}
		
		generateScheduleLevel_4();
	}
	
	private void generateScheduleLevel_4(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>		ScheduleDateRangeTo
		 */
		
		for(Object childObj : scheduleChildList_3){
			
			if(childObj instanceof ScheduleDateRangeTo){
				ScheduleDateRangeTo recurrentToDateObj = (ScheduleDateRangeTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				recurrentToDateObj.setValue(scheduleProfile.getToDateRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range> */
				ScheduleWeekdayRange weekRangeObj = new ScheduleWeekdayRange();
				scheduleChildList_4.add(weekRangeObj);
				recurrentToDateObj.setWeekdayRange(weekRangeObj);
			}
		}
		
		generateScheduleLevel_5();
	}
	
	private void generateScheduleLevel_5(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>		ScheduleWeekdayRange
		 */
		
		for(Object childObj : scheduleChildList_4){
			if(childObj instanceof ScheduleWeekdayRange){
				ScheduleWeekdayRange weekRangeObj = (ScheduleWeekdayRange)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to", 
						"weekday-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				weekRangeObj.setValue(scheduleProfile.getFromWeekRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to> */
				ScheduleWeekdayRangeTo weekRangeToObj = new ScheduleWeekdayRangeTo();
				scheduleChildList_5.add(weekRangeToObj);
				weekRangeObj.setTo(weekRangeToObj);
			}
		}
		generateScheduleLevel_6();
	}
	
	private void generateScheduleLevel_6(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>		ScheduleWeekdayRangeTo
		 */
		
		for(Object childObj : scheduleChildList_5){
			if(childObj instanceof ScheduleWeekdayRangeTo){
				ScheduleWeekdayRangeTo weekRangeToObj = (ScheduleWeekdayRangeTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to/weekday-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				weekRangeToObj.setValue(scheduleProfile.getToWeekRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range> */
				ScheduleTimeRange1 timeRangeType1 = new ScheduleTimeRange1();
				scheduleChildList_6.add(timeRangeType1);
				weekRangeToObj.setTimeRange(timeRangeType1);
			}
		}
		generateScheduleLevel_7();
	}
	
	private void generateScheduleLevel_7(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>		ScheduleTimeRange1
		 */
		
		for(Object childObj : scheduleChildList_6){
			if(childObj instanceof ScheduleTimeRange1){
				ScheduleTimeRange1 timeRangeType1 = (ScheduleTimeRange1)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to/weekday-range/to", 
						"time-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeType1.setValue(scheduleProfile.getFromTimeOneRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to> */
				ScheduleTimeRange1To timeRangeToType1 = new ScheduleTimeRange1To();
				scheduleChildList_7.add(timeRangeToType1);
				timeRangeType1.setTo(timeRangeToType1);
			}
		}
		generateScheduleLevel_8();
	}
	
	private void generateScheduleLevel_8(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to>		ScheduleTimeRange1To
		 */
		for(Object childObj : scheduleChildList_7){
			if(childObj instanceof ScheduleTimeRange1To){
				ScheduleTimeRange1To timeRangeToType1 = (ScheduleTimeRange1To)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to/weekday-range/to/time-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeToType1.setValue(scheduleProfile.getToTimeOneRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to>.<time-range> */
				ScheduleTimeRange2 timeRangeType2 = new ScheduleTimeRange2();
				scheduleChildList_8.add(timeRangeType2);
				timeRangeToType1.setTimeRange(timeRangeType2);
			}
		}
		generateScheduleLevel_9();
	}
	
	private void generateScheduleLevel_9(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to>.<time-range>		ScheduleTimeRange2
		 */
		
		for(Object childObj : scheduleChildList_8){
			if(childObj instanceof ScheduleTimeRange2){
				ScheduleTimeRange2 timeRangeType2 = (ScheduleTimeRange2)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to/weekday-range/to/time-range/to", 
						"time-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeType2.setValue(scheduleProfile.getFromTimeTwoRecurrent());
				
				/** element: <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to>.<time-range>.<to> */
				ScheduleTimeRange2To timeRangeToType2 = new ScheduleTimeRange2To();
				scheduleChildList_9.add(timeRangeToType2);
				timeRangeType2.setTo(timeRangeToType2);
			}
		}
		generateScheduleLevel_10();
	}
	
	private void generateScheduleLevel_10(){
		/**
		 * <schedule>.<recurrent>.<date-range>.<to>.<weekday-range>.<to>.<time-range>.<to>.<time-range>.<to>		ScheduleTimeRange2To
		 */
		
		for(Object childObj : scheduleChildList_9){
			if(childObj instanceof ScheduleTimeRange2To){
				ScheduleTimeRange2To timeRangeToType2 = (ScheduleTimeRange2To)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule/recurrent/date-range/to/weekday-range/to/time-range/to/time-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeToType2.setValue(scheduleProfile.getToTimeTwoRecurrent());
			}
		}
	}
}
