package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.TrackProfileInt;
import com.ah.xml.be.config.TrackAction;
import com.ah.xml.be.config.TrackInterval;
import com.ah.xml.be.config.TrackObj;
import com.ah.xml.be.config.TrackRetry;
import com.ah.xml.be.config.TrackTimeout;

/**
 * @author zhang
 * @version 2008-8-5 15:38:05
 */

public class CreateTrackProfileTree {
	
	private TrackProfileInt trackImpl;
	private TrackObj trackObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> trackChildList_1 = new ArrayList<Object>();

	public CreateTrackProfileTree(TrackProfileInt trackProfileImp, GenerateXMLDebug oDebug) {
		this.trackImpl = trackProfileImp;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(trackImpl.isConfigTrack()){
			trackObj = new TrackObj();
			generateTrackLevel_1();
		}
	}
	
	public TrackObj getTrackObj(){
		return this.trackObj;
	}
	
	private void generateTrackLevel_1() throws Exception{
		/**
		 * <track>		TrackObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"track", GenerateXMLDebug.SET_NAME,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		trackObj.setName(trackImpl.getTrackName());
		
		/** attribute: operation */
		trackObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <track>.<cr> */
		trackObj.setCr("");
		
		/** element: <track>.<enable> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"enable", GenerateXMLDebug.SET_OPERATION,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		trackObj.setEnable(CLICommonFunc.getAhOnlyAct(trackImpl.isEnableTrack()));
		
		/** element: <track>.<default-gateway> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"default-gateway", GenerateXMLDebug.CONFIG_ELEMENT,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		if(trackImpl.isEnableDefaultGateway()){
			
			oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
					"default-gateway", GenerateXMLDebug.SET_OPERATION,
					trackImpl.getTrackGuiName(), trackImpl.getTrackName());
			trackObj.setDefaultGateway(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <track>.<ip> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"ip", GenerateXMLDebug.CONFIG_ELEMENT,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		for(int i=0; i<trackImpl.getTrackIpSize(); i++){
			
			oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
					"ip", GenerateXMLDebug.SET_NAME,
					trackImpl.getTrackGuiName(), trackImpl.getTrackName());
			trackObj.getIp().add(CLICommonFunc.createAhNameActValue(trackImpl.getTrackIpAddress(i), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <track>.<multi-dst-logic> */
		TrackObj.MultiDstLogic logicObj = new TrackObj.MultiDstLogic();
		trackChildList_1.add(logicObj);
		trackObj.setMultiDstLogic(logicObj);
		
//		if(!trackImpl.isUseDefaultValue()){
			
		/** element: <track>.<action> */
		TrackAction actionObj = new TrackAction();
		trackChildList_1.add(actionObj);
		trackObj.setAction(actionObj);
		
		/** element: <track>.<retry> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"retry", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		Object[][] retryParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, trackImpl.getTrackRetry()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		trackObj.setRetry(
				(TrackRetry)CLICommonFunc.createObjectWithName(TrackRetry.class, retryParm)
		);
		
		//change from dakar_r6 for support OS version before dakar_r5, old-interval = interval * retryTime + 1
		/** element: <track>.<old-interval> */
		Object[][] oldIntervalParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, trackImpl.getTrackOldInterval()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		trackObj.setOldInterval((TrackInterval)CLICommonFunc.createObjectWithName(
				TrackInterval.class, oldIntervalParm));
		
		/** element: <track>.<interval> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"interval", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		Object[][] intervalParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, trackImpl.getTrackInterval()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		trackObj.setInterval(
				(TrackInterval)CLICommonFunc.createObjectWithName(TrackInterval.class, intervalParm)
		);
		
		/** element: <track>.<timeout> */
		oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
				"timeout", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		Object[][] timeoutParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, trackImpl.getTrackTimeOut()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		trackObj.setTimeout(
				(TrackTimeout)CLICommonFunc.createObjectWithName(TrackTimeout.class, timeoutParm)
		);

		/** element: <track>.<use-for-wan-testing> */
		if (trackImpl.isUseForWanTesting()) {
			oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
					"use-for-wan-testing", GenerateXMLDebug.SET_OPERATION,
					trackImpl.getTrackGuiName(), trackImpl.getTrackName());
			trackObj.setUseForWanTesting(CLICommonFunc.getAhOnlyAct(trackImpl.isUseForWanTesting()));
		}
		
		generateTrackLevel_2();
	}
	
	private void generateTrackLevel_2(){
		/**
		 * <track>.<multi-dst-logic>			TrackObj.MultiDstLogic
		 * <track>.<action>						TrackAction
		 */
		
		for(Object childObj : trackChildList_1){
			
			/** element: <track>.<multi-dst-logic> */
			if(childObj instanceof TrackObj.MultiDstLogic){
				TrackObj.MultiDstLogic logicObj = (TrackObj.MultiDstLogic)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
						"multi-dst-logic", GenerateXMLDebug.SET_VALUE,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				logicObj.setValue(trackImpl.getTrackLogic());
				
				/** attribute: operation */
				logicObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <track>.<action> */
			if(childObj instanceof TrackAction){
				TrackAction actionObj = (TrackAction)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']",
						"action", GenerateXMLDebug.SET_OPERATION,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				actionObj.setOperation(CLICommonFunc.getAhEnumAct(trackImpl.isDisableTrackAction()));
				
				/** element: <track>.<action>.<disable-access-radio> */
				oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']/action",
						"disable-access-radio", GenerateXMLDebug.CONFIG_ELEMENT,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				if(trackImpl.isConfigTrackAction(TrackProfileInt.TrackAction.disableAccessRadio)){
					actionObj.setDisableAccessRadio(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <track>.<action>.<enable-access-console> */
				oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']/action",
						"enable-access-console", GenerateXMLDebug.CONFIG_ELEMENT,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				if(trackImpl.isConfigTrackAction(TrackProfileInt.TrackAction.enableAccessConsole)){
					actionObj.setEnableAccessConsole(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <track>.<action>.<start-mesh-failover> */
				oDebug.debug("/configuration/track[@name='"+trackImpl.getTrackName()+"']/action",
						"start-mesh-failover", GenerateXMLDebug.CONFIG_ELEMENT,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				if(trackImpl.isConfigTrackAction(TrackProfileInt.TrackAction.startMeshFailover)){
					actionObj.setStartMeshFailover(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
		}
		trackChildList_1.clear();
	}
}
