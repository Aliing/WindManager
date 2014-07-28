package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.KddrInt;
import com.ah.xml.be.config.KddrObj;

public class CreateKddrTree {
	
	private KddrInt kddrImpl;
	
	private KddrObj kddrObj;
	
//	private List<Object> childList_1 = new ArrayList<Object>();
//	private List<Object> childList_2 = new ArrayList<Object>();

	public CreateKddrTree(KddrInt kddrImpl){
		this.kddrImpl = kddrImpl;
	}
	
	public void generate(){
		kddrObj = new KddrObj();
		
		generateChildLevel_1();
	}
	
	public KddrObj getKddrObj(){
		return this.kddrObj;
	}
	
	private void generateChildLevel_1(){
		
		/** element: <kddr>.<enable> */
		kddrObj.setEnable(CLICommonFunc.getAhOnlyAct(kddrImpl.isKddrEnable()));
		
		//childList_1.add(rateObj);
		//generateChildLevel_2();
	}
	
//	private void generateChildLevel_2(){
//		/**
//		 * <storm-control>.<rate-limit>			StormControlRateLimit
//		 */
//		for(Object childObj : childList_1){
//			StormControlRateLimit rateObj = (StormControlRateLimit)childObj;
//			
//			/** element: <storm-control>.<rate-limit>.<mode> */
//			StormControlRateLimitMode modeObj = new StormControlRateLimitMode();
//			rateObj.setMode(modeObj);
//			childList_2.add(modeObj);
//		}
//		childList_1.clear();
//	}
	
	
}
