package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ScheduleProfileInt;
import com.ah.xml.be.config.ScheduleOnceTo;
import com.ah.xml.be.config.SchedulePpsk;
import com.ah.xml.be.config.SchedulePpskDateRange;
import com.ah.xml.be.config.SchedulePpskDateRangeTo;
import com.ah.xml.be.config.SchedulePpskObj;
import com.ah.xml.be.config.SchedulePpskOnce;
import com.ah.xml.be.config.SchedulePpskRecurrent;
import com.ah.xml.be.config.SchedulePpskWeekday;
import com.ah.xml.be.config.ScheduleTimeRange1;
import com.ah.xml.be.config.ScheduleTimeRange1To;
import com.ah.xml.be.config.ScheduleTimeRange2;
import com.ah.xml.be.config.ScheduleTimeRange2To;

/**
 * @author zhang
 * @version 2009-12-17 11:12:39
 */

public class CreatePpskScheduleTree {
	
	private ScheduleProfileInt scheduleProfile;
	private SchedulePpskObj ppskScheduleObj;
	
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

	public CreatePpskScheduleTree(ScheduleProfileInt scheduleImp, GenerateXMLDebug oDebug){
		scheduleProfile = scheduleImp;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		ppskScheduleObj = new SchedulePpskObj();
		generatePpskScheduleLevel_1();
	}
	
	public SchedulePpskObj getPpskScheduleObj(){
		return ppskScheduleObj;
	}
	
	private void generatePpskScheduleLevel_1(){

		/** attribute: name */
		oDebug.debug("/configuration", 
				"schedule-ppsk", GenerateXMLDebug.SET_NAME,
				scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
		ppskScheduleObj.setName(scheduleProfile.getScheduleName() );
		
		/** attribute: operation */
		ppskScheduleObj.setOperation(
				CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault())
		);
		
		/** element: <schedule-ppsk>.<ppsk> */
		SchedulePpsk ppskObj = new SchedulePpsk();
		scheduleChildList_1.add(ppskObj);
		ppskScheduleObj.setPpsk(ppskObj);
		
		generatePpskScheduleLevel_2();
	}
	
	private void generatePpskScheduleLevel_2(){
		/**
		 * <schedule-ppsk>.<ppsk>		SchedulePpsk
		 */
		for(Object childObj : scheduleChildList_1){
			
			/** element: <schedule-ppsk>.<ppsk> */
			if(childObj instanceof SchedulePpsk){
				SchedulePpsk ppskObj = (SchedulePpsk)childObj;
				
				/** attribute: operation */
				ppskObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				if(scheduleProfile.isSelectOnceType()){
					
					/** element: <schedule-ppsk>.<ppsk>.<once> */
					oDebug.debug("/configuration/schedule-ppsk/ppsk", 
							"once", GenerateXMLDebug.CONFIG_ELEMENT,
							scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
					SchedulePpskOnce onceObj = new SchedulePpskOnce();
					scheduleChildList_2.add(onceObj);
					ppskObj.setOnce(onceObj);
				}else{
					
					/** element: <schedule-ppsk>.<ppsk>.<recurrent> */
					oDebug.debug("/configuration/schedule-ppsk/ppsk", 
							"recurrent", GenerateXMLDebug.CONFIG_ELEMENT,
							scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
					SchedulePpskRecurrent recurrentObj = new SchedulePpskRecurrent();
					scheduleChildList_2.add(recurrentObj);
					ppskObj.setRecurrent(recurrentObj);
				}
			}
		}
		scheduleChildList_1.clear();
		generatePpskScheduleLevel_3();
	}
	
	private void generatePpskScheduleLevel_3(){
		/**
		 * <schedule-ppsk>.<ppsk>.<once>					SchedulePpskOnce
		 * <schedule-ppsk>.<ppsk>.<recurrent>				SchedulePpskRecurrent
		 */
		for(Object childObj : scheduleChildList_2){
			
			/** element: <schedule-ppsk>.<ppsk>.<once> */
			if(childObj instanceof SchedulePpskOnce){
				SchedulePpskOnce onceObj = (SchedulePpskOnce)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk", 
						"once", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				onceObj.setValue(scheduleProfile.getFromDateAndTimeOnce());
				
				/** attribute: quoteProhibited */
				onceObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <schedule-ppsk>.<ppsk>.<once>.<to> */
				ScheduleOnceTo onceToObj = new ScheduleOnceTo();
				scheduleChildList_3.add(onceToObj);
				onceObj.setTo(onceToObj);
			}
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent> */
			if(childObj instanceof SchedulePpskRecurrent){
				SchedulePpskRecurrent recurrentObj = (SchedulePpskRecurrent)childObj;
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range> */
				SchedulePpskDateRange dateRangeObj = new SchedulePpskDateRange();
				scheduleChildList_3.add(dateRangeObj);
				recurrentObj.setDateRange(dateRangeObj);
			}
		}
		scheduleChildList_2.clear();
		generatePpskScheduleLevel_4();
	}
	
	private void generatePpskScheduleLevel_4(){
		/**
		 * <schedule-ppsk>.<ppsk>.<once>.<to>				ScheduleOnceTo
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>	SchedulePpskDateRange
		 */
		for(Object childObj : scheduleChildList_3){
			
			/** element: <schedule-ppsk>.<ppsk>.<once>.<to> */
			if(childObj instanceof ScheduleOnceTo){
				ScheduleOnceTo onceToObj = (ScheduleOnceTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/once", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				onceToObj.setValue(scheduleProfile.getToDateAndTimeOnce());
				
				/** attribute: quoteProhibited */
				onceToObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range> */
			if(childObj instanceof SchedulePpskDateRange){
				SchedulePpskDateRange dateRangeObj = (SchedulePpskDateRange)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent", 
						"date-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				dateRangeObj.setValue(scheduleProfile.getFromDateRecurrent());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to> */
				SchedulePpskDateRangeTo recurrentToDateObj = new SchedulePpskDateRangeTo();
				scheduleChildList_4.add(recurrentToDateObj);
				dateRangeObj.setTo(recurrentToDateObj);
			}
			
		}
		scheduleChildList_3.clear();
		generatePpskScheduleLevel_5();
	}
	
	private void generatePpskScheduleLevel_5(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>				SchedulePpskDateRangeTo
		 */
		for(Object childObj : scheduleChildList_4){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to> */
			if(childObj instanceof SchedulePpskDateRangeTo){
				SchedulePpskDateRangeTo recurrentToDateObj = (SchedulePpskDateRangeTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				recurrentToDateObj.setValue(scheduleProfile.getToDateRecurrent());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday> */
				SchedulePpskWeekday weekDayObj = new SchedulePpskWeekday();
				scheduleChildList_5.add(weekDayObj);
				recurrentToDateObj.setWeekday(weekDayObj);
			}
		}
		scheduleChildList_4.clear();
		generatePpskScheduleLevel_6();
	}
	
	private void generatePpskScheduleLevel_6(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>				SchedulePpskWeekday
		 */
		for(Object childObj : scheduleChildList_5){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday> */
			if(childObj instanceof SchedulePpskWeekday){
				SchedulePpskWeekday weekDayObj = (SchedulePpskWeekday)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range/to", 
						"weekday", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				weekDayObj.setValue(scheduleProfile.getPpskWeekDay());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range> */
				ScheduleTimeRange1 timeRange1Obj = new ScheduleTimeRange1();
				scheduleChildList_6.add(timeRange1Obj);
				weekDayObj.setTimeRange(timeRange1Obj);
			}
		}
		scheduleChildList_5.clear();
		generatePpskScheduleLevel_7();
	}
	
	private void generatePpskScheduleLevel_7(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>		ScheduleTimeRange1
		 */
		for(Object childObj : scheduleChildList_6){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range> */
			if(childObj instanceof ScheduleTimeRange1){
				ScheduleTimeRange1 timeRange1Obj = (ScheduleTimeRange1)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range/to/weekday", 
						"time-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRange1Obj.setValue(scheduleProfile.getFromTimeOneRecurrent());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to> */
				ScheduleTimeRange1To timeRangeToType1 = new ScheduleTimeRange1To();
				scheduleChildList_7.add(timeRangeToType1);
				timeRange1Obj.setTo(timeRangeToType1);
			}
		}
		scheduleChildList_6.clear();
		generatePpskScheduleLevel_8();
	}
	
	private void generatePpskScheduleLevel_8(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>		ScheduleTimeRange1To
		 */
		for(Object childObj : scheduleChildList_7){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to> */
			if(childObj instanceof ScheduleTimeRange1To){
				ScheduleTimeRange1To timeRangeToType1 = (ScheduleTimeRange1To)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range/to/weekday/time-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeToType1.setValue(scheduleProfile.getToTimeOneRecurrent());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range> */
				ScheduleTimeRange2 timeRangeType2 = new ScheduleTimeRange2();
				scheduleChildList_8.add(timeRangeType2);
				timeRangeToType1.setTimeRange(timeRangeType2);
			}
		}
		scheduleChildList_7.clear();
		generatePpskScheduleLevel_9();
	}
	
	private void generatePpskScheduleLevel_9(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range>		ScheduleTimeRange2
		 */
		for(Object childObj : scheduleChildList_8){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range> */
			if(childObj instanceof ScheduleTimeRange2){
				ScheduleTimeRange2 timeRangeType2 = (ScheduleTimeRange2)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range/to/weekday/time-range/to", 
						"time-range", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeType2.setValue(scheduleProfile.getFromTimeTwoRecurrent());
				
				/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range>.<to> */
				ScheduleTimeRange2To timeRangeToType2 = new ScheduleTimeRange2To();
				scheduleChildList_9.add(timeRangeToType2);
				timeRangeType2.setTo(timeRangeToType2);
			}
		}
		scheduleChildList_8.clear();
		generatePpskScheduleLevel_10();
	}
	
	private void generatePpskScheduleLevel_10(){
		/**
		 * <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range>.<to>		ScheduleTimeRange2To
		 */
		for(Object childObj : scheduleChildList_9){
			
			/** element: <schedule-ppsk>.<ppsk>.<recurrent>.<date-range>.<to>.<weekday>.<time-range>.<to>.<time-range>.<to> */
			if(childObj instanceof ScheduleTimeRange2To){
				ScheduleTimeRange2To timeRangeToType2 = (ScheduleTimeRange2To)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/schedule-ppsk/ppsk/recurrent/date-range/to/weekday/time-range/to/time-range", 
						"to", GenerateXMLDebug.SET_VALUE,
						scheduleProfile.getScheduleGuiName(), scheduleProfile.getScheduleName());
				timeRangeToType2.setValue(scheduleProfile.getToTimeTwoRecurrent());
			}
		}
		scheduleChildList_9.clear();
	}
}
