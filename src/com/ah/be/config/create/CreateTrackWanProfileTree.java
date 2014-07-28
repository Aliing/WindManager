package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.TrackProfileInt;
import com.ah.be.config.create.source.impl.TrackWanProfileImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.TrackMultiDstLogic;
import com.ah.xml.be.config.TrackWanEnable;
import com.ah.xml.be.config.TrackWanEnableInterface;
import com.ah.xml.be.config.TrackWanObj;

/**
 * @author 
 * @version 2008-8-5 15:38:05
 */

public class CreateTrackWanProfileTree {
	
	private TrackProfileInt trackImpl;
	private TrackWanObj trackWanObj;
	private HiveAp hiveAp;

	private GenerateXMLDebug oDebug;
	
	private List<Object> trackChildList_1 = new ArrayList<Object>();
	private List<Object> trackChildList_2 = new ArrayList<Object>();

	public CreateTrackWanProfileTree(TrackProfileInt trackProfileImp, GenerateXMLDebug oDebug) {
		this.trackImpl = trackProfileImp;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(trackImpl.isConfigTrack()){
			trackWanObj = new TrackWanObj();
			generateTrackLevel_1();
		}
	}
	
	public TrackWanObj getTrackWanObj(){
		return this.trackWanObj;
	}

    public void setHiveAp(HiveAp hiveAp) {
        this.hiveAp = hiveAp;
    }

    private void generateTrackLevel_1() throws Exception{
		/**
		 * <track-wan>		TrackObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration",
				"track", GenerateXMLDebug.SET_NAME,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		trackWanObj.setName(trackImpl.getTrackName());
		
		/** attribute: operation */
		trackWanObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <track-wan>.<cr> */
		trackWanObj.setCr("");
		
		/** element: <track-wan>.<default-gateway> */
		oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
				"default-gateway", GenerateXMLDebug.CONFIG_ELEMENT,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		if(trackImpl.isEnableDefaultGateway()){
			oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
					"default-gateway", GenerateXMLDebug.SET_OPERATION,
					trackImpl.getTrackGuiName(), trackImpl.getTrackName());
			trackWanObj.setDefaultGateway(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <track-wan>.<ip> */
		oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
				"ip", GenerateXMLDebug.CONFIG_ELEMENT,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		for(int i=0; i<trackImpl.getTrackIpSize(); i++){
			oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
					"ip", GenerateXMLDebug.SET_NAME,
					trackImpl.getTrackGuiName(), trackImpl.getTrackName());
			trackWanObj.getIp().add(CLICommonFunc.createAhNameActValue(trackImpl.getTrackIpAddress(i), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <track-wan>.<multi-dst-logic> */
		TrackMultiDstLogic logicObj = new TrackMultiDstLogic();
		trackChildList_1.add(logicObj);
		trackWanObj.setMultiDstLogic(logicObj);

		/** element: <track-wan>.<retry> */
		oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
				"retry", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		trackWanObj.setRetry(CLICommonFunc.createAhIntActObj(trackImpl.getTrackRetry(), true));
		
		/** element: <track-wan>.<interval> */
		oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
				"interval", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		int interval = 0;
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.6.0") == 0){
			interval = trackImpl.getTrackInterval();
		}else if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.1.0") < 0){
			int tmp = trackImpl.getTrackInterval();
			int retry = trackImpl.getTrackRetry();
			interval = tmp*(retry + 1) + 1;
		}else{
			interval = trackImpl.getTrackInterval();
		}
		trackWanObj.setInterval(CLICommonFunc.createAhIntActObj(interval, true));
		
		/** element: <track-wan>.<timeout> */
		oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
				"timeout", GenerateXMLDebug.SET_VALUE,
				trackImpl.getTrackGuiName(), trackImpl.getTrackName());
		Object[][] timeoutParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, trackImpl.getTrackTimeOut()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		trackWanObj.setTimeout(CLICommonFunc.createAhIntActObj(trackImpl.getTrackTimeOut(), true));

		TrackWanProfileImpl trackWanImpl = (TrackWanProfileImpl)trackImpl;
		List<Short> interfaceTypes = trackWanImpl.getInterfaceType();

		for(int i = 0;i < interfaceTypes.size();i++){
			short intfType = interfaceTypes.get(i);
			if (intfType == AhInterface.DEVICE_IF_TYPE_USB) {
	        	trackWanObj.getInterface().add(CLICommonFunc.createAhNameActValue("usbnet0", true));
	        }else if (intfType == AhInterface.DEVICE_IF_TYPE_WIFI0 || intfType == AhInterface.DEVICE_IF_TYPE_WIFI1) {
				trackWanObj.getInterface().add(CLICommonFunc.createAhNameActValue("wifi" + (intfType-AhInterface.DEVICE_IF_TYPE_WIFI0), true));
			}else if(!hiveAp.isSwitchProduct()){
				trackWanObj.getInterface().add(CLICommonFunc.createAhNameActValue("eth" + (intfType-AhInterface.DEVICE_IF_TYPE_ETH0), true));
			}else if(hiveAp.isSwitchProduct()){
				String protName = DeviceInfType.getInstance(intfType, hiveAp.getHiveApModel()).getCLIName(hiveAp.getHiveApModel());
				trackWanObj.getInterface().add(CLICommonFunc.createAhNameActValue(protName, true));
			}
		}
       

        /** element: <track-wan>.<enable> */
        oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
                "enable", GenerateXMLDebug.SET_OPERATION,
                trackImpl.getTrackGuiName(), trackImpl.getTrackName());
        TrackWanEnable trackWanEnable = new TrackWanEnable();
        trackChildList_1.add(trackWanEnable);
        trackWanObj.setEnable(trackWanEnable);

        generateTrackLevel_2();
	}
	
	private void generateTrackLevel_2(){
		/**
		 * <track-wan>.<multi-dst-logic>			TrackObj.MultiDstLogic
		 */
		
		for(Object childObj : trackChildList_1){
			
			/** element: <track-wan>.<multi-dst-logic> */
			if(childObj instanceof TrackMultiDstLogic){
				TrackMultiDstLogic logicObj = (TrackMultiDstLogic)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/track-wan[@name='"+trackImpl.getTrackName()+"']",
						"multi-dst-logic", GenerateXMLDebug.SET_VALUE,
						trackImpl.getTrackGuiName(), trackImpl.getTrackName());
				logicObj.setValue(trackImpl.getTrackLogic());
				
				/** attribute: operation */
				logicObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			if (childObj instanceof TrackWanEnable) {
				TrackWanEnable trackWanEnable = (TrackWanEnable) childObj;
				trackWanEnable.setOperation(AhEnumAct.YES);
				trackWanEnable.setCr(CLICommonFunc.getAhOnlyAct(trackImpl.isEnableTrack()));
				
				TrackWanEnableInterface twei = new TrackWanEnableInterface();
				trackChildList_2.add(twei);
				trackWanEnable.setInterface(twei);
			}
			
		}
		
		trackChildList_1.clear();
		generateTrackLevel_3();
	}
	
	private void generateTrackLevel_3() {
		for (Object childObj : trackChildList_2) {
			if (childObj instanceof TrackWanEnableInterface) {
				TrackWanEnableInterface twei = (TrackWanEnableInterface) childObj;
				twei.setName(trackWanObj.getInterface().get(0).getName());
				twei.setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
			}
		}
		
		trackChildList_2.clear();
	}
}
