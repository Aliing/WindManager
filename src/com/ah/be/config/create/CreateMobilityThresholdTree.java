package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MobilityThresholdProfileInt;
import com.ah.xml.be.config.MobilityThresholdObj;

/**
 * 
 * @author zhang
 *
 */
public class CreateMobilityThresholdTree {
	
	private MobilityThresholdProfileInt mobilityThresholdImpl;
	private MobilityThresholdObj mobilityThresholdObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> mobilityThresholdChildList_1 = new ArrayList<Object>();
	private List<Object> mobilityThresholdChildList_2 = new ArrayList<Object>();

	public CreateMobilityThresholdTree(MobilityThresholdProfileInt mobilityThresholdImpl, GenerateXMLDebug oDebug){
		this.mobilityThresholdImpl = mobilityThresholdImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		mobilityThresholdObj = new MobilityThresholdObj();
		generateMobilityThresholdChildLevel_1();
	}
	
	public MobilityThresholdObj getMobilityThresholdObj(){
		return this.mobilityThresholdObj;
	}
	
	private void generateMobilityThresholdChildLevel_1() {
		/**
		 * <mobility-threshold>		MobilityThresholdObj
		 */
		
		/** attribute: updateTime */
		mobilityThresholdObj.setUpdateTime(mobilityThresholdImpl.getUpdateTime());
		
		/** element: <mobility-threshold>.<gre-tunnel> */
		MobilityThresholdObj.GreTunnel greTunnelObj = new MobilityThresholdObj.GreTunnel();
		mobilityThresholdChildList_1.add(greTunnelObj);
		mobilityThresholdObj.setGreTunnel(greTunnelObj);
		
		generateMobilityThresholdChildLevel_2();
	}
	
	private void generateMobilityThresholdChildLevel_2() {
		/**
		 * <mobility-threshold>.<gre-tunnel>			MobilityThresholdObj.GreTunnel
		 */
		for(Object childObj : mobilityThresholdChildList_1){
			
			/** element: <mobility-threshold>.<gre-tunnel> */
			if(childObj instanceof MobilityThresholdObj.GreTunnel){
				MobilityThresholdObj.GreTunnel greTunnelObj = (MobilityThresholdObj.GreTunnel)childObj;
				
				/** element: <mobility-threshold>.<gre-tunnel>.<permitted-load> */
				MobilityThresholdObj.GreTunnel.PermittedLoad permittedLoadObj = new MobilityThresholdObj.GreTunnel.PermittedLoad();
				mobilityThresholdChildList_2.add(permittedLoadObj);
				greTunnelObj.setPermittedLoad(permittedLoadObj);
				
			}
		}
		
		generateMobilityThresholdChildLevel_3();
	}
	
	private void generateMobilityThresholdChildLevel_3() {
		/**
		 * <mobility-threshold>.<gre-tunnel>.<permitted-load>			MobilityThresholdObj.GreTunnel.PermittedLoad
		 */
		for(Object childObj : mobilityThresholdChildList_2){
			
			/** element: <mobility-threshold>.<gre-tunnel>.<permitted-load> */
			if(childObj instanceof MobilityThresholdObj.GreTunnel.PermittedLoad){
				MobilityThresholdObj.GreTunnel.PermittedLoad permittedLoadObj = (MobilityThresholdObj.GreTunnel.PermittedLoad)childObj;
				
				/** attribute: operation */
				permittedLoadObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mobility-threshold>.<gre-tunnel>.<permitted-load>.<high> */
				oDebug.debug("/configuration/mobility-threshold/gre-tunnel/permitted-load", 
						"high", GenerateXMLDebug.CONFIG_ELEMENT,
						mobilityThresholdImpl.getHiveApGuiName(), mobilityThresholdImpl.getHiveApName());
				if(mobilityThresholdImpl.isConfigureThresholdType(MobilityThresholdProfileInt.ROAMING_THRESHOLD_HIGH)){
					permittedLoadObj.setHigh(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <mobility-threshold>.<gre-tunnel>.<permitted-load>.<medium> */
				oDebug.debug("/configuration/mobility-threshold/gre-tunnel/permitted-load", 
						"medium", GenerateXMLDebug.CONFIG_ELEMENT,
						mobilityThresholdImpl.getHiveApGuiName(), mobilityThresholdImpl.getHiveApName());
				if(mobilityThresholdImpl.isConfigureThresholdType(MobilityThresholdProfileInt.ROAMING_THRESHOLD_MEDIUM)){
					permittedLoadObj.setMedium(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <mobility-threshold>.<gre-tunnel>.<permitted-load>.<low> */
				oDebug.debug("/configuration/mobility-threshold/gre-tunnel/permitted-load", 
						"low", GenerateXMLDebug.CONFIG_ELEMENT,
						mobilityThresholdImpl.getHiveApGuiName(), mobilityThresholdImpl.getHiveApName());
				if(mobilityThresholdImpl.isConfigureThresholdType(MobilityThresholdProfileInt.ROAMING_THRESHOLD_LOW)){
					permittedLoadObj.setLow(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
			}
		}
	}
}
