package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.PerformanceSentinelInt;
import com.ah.xml.be.config.NotificationInterval;
import com.ah.xml.be.config.PerformanceSentinelObj;

/**
 * @author zhang
 * @version 2009-9-4 10:57:02
 */

public class CreatePerformanceSentinelTree {
	
	private GenerateXMLDebug oDebug;
	private PerformanceSentinelInt performanceImpl;
	
	private PerformanceSentinelObj performanceObj;

	public CreatePerformanceSentinelTree(PerformanceSentinelInt performanceImpl, GenerateXMLDebug oDebug){
		this.performanceImpl = performanceImpl;
		this.oDebug = oDebug;
	}
	
	public PerformanceSentinelObj getPerformanceSentinelObj(){
		return this.performanceObj;
	}
	
	public void generate() throws Exception{
		performanceObj = new PerformanceSentinelObj();
		generatePerformanceLevel_1();
	}
	
	private void generatePerformanceLevel_1() throws Exception{
		/**
		 * <performance-sentinel>					PerformanceSentinelObj
		 */
		
		/** element: <performance-sentinel>.<notification-interval> */
		oDebug.debug("/configuration/performance-sentinel", 
				"notification-interval", GenerateXMLDebug.SET_VALUE,
				null, null);
		Object[][] perParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, performanceImpl.getSlaInterval()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		performanceObj.setNotificationInterval(
				(NotificationInterval)CLICommonFunc.createObjectWithName(NotificationInterval.class, perParm)
		);
	}
}
