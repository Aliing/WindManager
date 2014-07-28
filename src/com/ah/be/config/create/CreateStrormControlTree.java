package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.StromControlInt;
import com.ah.xml.be.config.StormControlObj;
import com.ah.xml.be.config.StormControlRateLimit;
import com.ah.xml.be.config.StormControlRateLimitMode;

public class CreateStrormControlTree {
	
	private StromControlInt stormImpl;
	
	private StormControlObj stormObj;
	
	private List<Object> childList_1 = new ArrayList<Object>();
	private List<Object> childList_2 = new ArrayList<Object>();

	public CreateStrormControlTree(StromControlInt stormImpl){
		this.stormImpl = stormImpl;
	}
	
	public void generate(){
		stormObj = new StormControlObj();
		
		generateChildLevel_1();
	}
	
	public StormControlObj getStormControlObj(){
		return this.stormObj;
	}
	
	private void generateChildLevel_1(){
		
		/** element: <storm-control>.<rate-limit> */
		StormControlRateLimit rateObj = new StormControlRateLimit();
		stormObj.setRateLimit(rateObj);
		childList_1.add(rateObj);
		
		generateChildLevel_2();
	}
	
	private void generateChildLevel_2(){
		/**
		 * <storm-control>.<rate-limit>			StormControlRateLimit
		 */
		for(Object childObj : childList_1){
			StormControlRateLimit rateObj = (StormControlRateLimit)childObj;
			
			/** element: <storm-control>.<rate-limit>.<mode> */
			StormControlRateLimitMode modeObj = new StormControlRateLimitMode();
			rateObj.setMode(modeObj);
			childList_2.add(modeObj);
		}
		childList_1.clear();
		
		generateChildLevel_3();
	}
	
	private void generateChildLevel_3(){
		/**
		 * <storm-control>.<rate-limit>.<mode>			StormControlRateLimitMode
		 */
		for(Object childObj : childList_2){
			
			/** element: <storm-control>.<rate-limit>.<mode> */
			if(childObj instanceof StormControlRateLimitMode){
				StormControlRateLimitMode modeObj = (StormControlRateLimitMode)childObj;
				
				/** attribute: operation */
				modeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				modeObj.setValue(stormImpl.getStormControlMode());
			}
		}
		childList_2.clear();
	}
}
