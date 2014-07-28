package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MobilityPolicyProfileInt;
import com.ah.xml.be.config.MobilityPolicyFrom;
import com.ah.xml.be.config.MobilityPolicyObj;
import com.ah.xml.be.config.MobilityPolicyTo;

/**
 * 
 * @author zhang
 *
 */
public class CreateMobilityPolicyTree {
	
	private MobilityPolicyProfileInt mobPolicyImpl;
	private MobilityPolicyObj mobPolicyObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> mobilityPolicyChildList_1 = new ArrayList<Object>();
	private List<Object> mobilityPolicyChildList_2 = new ArrayList<Object>();
	private List<Object> mobilityPolicyChildList_3 = new ArrayList<Object>();
	
	public CreateMobilityPolicyTree(MobilityPolicyProfileInt mobPolicyImpl, GenerateXMLDebug oDebug) {
		this.mobPolicyImpl = mobPolicyImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(mobPolicyImpl.isConfigMobilityPolicy()){
			mobPolicyObj = new MobilityPolicyObj();
			generateMobilityPolicyLevel_1();
		}
	}
	
	public MobilityPolicyObj getMobilityPolicyObj(){
		return this.mobPolicyObj;
	}
	
	private void generateMobilityPolicyLevel_1() throws Exception {
		/**
		 * <mobility-policy>		MobilityPolicyObj
		 */
		
		/** attribute: updateTime */
		mobPolicyObj.setUpdateTime(mobPolicyImpl.getUpdateTime());
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"mobility-policy", GenerateXMLDebug.SET_NAME,
				mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
		mobPolicyObj.setName(mobPolicyImpl.getMobilityPolicyName());
		
		/** attribute: operation */
		mobPolicyObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <mobility-policy>.<dnxp> */
		oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']",
				"dnxp", GenerateXMLDebug.CONFIG_ELEMENT,
				mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
		if(mobPolicyImpl.isConfigureDnxp() ){
			MobilityPolicyObj.Dnxp mobDnxpObj = new MobilityPolicyObj.Dnxp();
			mobilityPolicyChildList_1.add(mobDnxpObj);
			mobPolicyObj.setDnxp(mobDnxpObj);
		}
		
		/** element: <mobility-policy>.<inxp> */
		oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']",
				"inxp", GenerateXMLDebug.CONFIG_ELEMENT,
				mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
		if(mobPolicyImpl.isConfigureInxp() ){
			MobilityPolicyObj.Inxp mobInxpObj = new MobilityPolicyObj.Inxp();
			mobilityPolicyChildList_1.add(mobInxpObj);
			mobPolicyObj.setInxp(mobInxpObj);
		}
		
		generateMobilityPolicyLevel_2();
	}
	
	private void generateMobilityPolicyLevel_2() throws Exception {
		/**
		 * <mobility-policy>.<dnxp>			MobilityPolicyObj.Dnxp
		 * <mobility-policy>.<inxp>			MobilityPolicyObj.Inxp
		 */
		for(Object childObj : mobilityPolicyChildList_1 ){
			
			/** element: <mobility-policy>.<dnxp> */
			if(childObj instanceof MobilityPolicyObj.Dnxp){
				MobilityPolicyObj.Dnxp dnxpObj = (MobilityPolicyObj.Dnxp)childObj;
				
//				/** element: <mobility-policy>.<dnxp>.<cr> */
//				dnxpObj.setCr("");
				
				/** element: <mobility-policy>.<dnxp>.<unroam-threshold> */
				oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/dnxp",
						"unroam-threshold", GenerateXMLDebug.CONFIG_ELEMENT,
						mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
				if(mobPolicyImpl.isConfigUnroamThreshold()){
					
					oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/dnxp",
							"unroam-threshold", GenerateXMLDebug.SET_VALUE,
							mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
					dnxpObj.setUnroamThreshold(
							CLICommonFunc.createAhStringActQuoteProhibited(mobPolicyImpl.getUnroamThresholdValue(), 
									CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}

				oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/dnxp",
						"nomadic-roaming", GenerateXMLDebug.CONFIG_ELEMENT,
						mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
				if(mobPolicyImpl.isConfigNomadicRoaming()){
					/** element: <mobility-policy>.<dnxp>.<nomadic-roaming> */
					dnxpObj.setNomadicRoaming(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}else{
					/** element: <mobility-policy>.<dnxp>.<cr> */
					dnxpObj.setCr("");
				}
			}
			
			/** element: <mobility-policy>.<inxp> */
			if(childObj instanceof MobilityPolicyObj.Inxp){
				MobilityPolicyObj.Inxp inxpObj = (MobilityPolicyObj.Inxp)childObj;
				
				/** element: <mobility-policy>.<inxp>.<gre-tunnel> */
				MobilityPolicyObj.Inxp.GreTunnel greTunnelObj = new MobilityPolicyObj.Inxp.GreTunnel();
				mobilityPolicyChildList_2.add(greTunnelObj);
				inxpObj.setGreTunnel(greTunnelObj);
			}
		}
		generateMobilityPolicyLevel_3();
	}
	
	private void generateMobilityPolicyLevel_3() throws Exception {
		/**
		 * <mobility-policy>.<inxp>.<gre-tunnel>		MobilityPolicyObj.Inxp.GreTunnel
		 */
		for(Object childObj : mobilityPolicyChildList_2){
			
			/** elemen: <mobility-policy>.<inxp>.<gre-tunnel> */
			if(childObj instanceof MobilityPolicyObj.Inxp.GreTunnel){
				MobilityPolicyObj.Inxp.GreTunnel greTunnelObj = (MobilityPolicyObj.Inxp.GreTunnel)childObj;
				
				/** elemen: <mobility-policy>.<inxp>.<gre-tunnel>.<from> */
//				if(!mobPolicyImpl.isMgtIpInFrom() ){
				if(mobPolicyImpl.isMgtIpInTo() ){
					for(int i=0; i<mobPolicyImpl.getMobilityPolicyFromSize(); i++ ){
						MobilityPolicyFrom fromObj = new MobilityPolicyFrom();
						setMobilityPolicyFrom(fromObj, i );
						greTunnelObj.getFrom().add(fromObj);
					}
				}
				
				/** elemen: <mobility-policy>.<inxp>.<gre-tunnel>.<to> */
				oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/inxp/gre-tunnel",
						"to", GenerateXMLDebug.CONFIG_ELEMENT,
						mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
				if(!mobPolicyImpl.isMgtIpInTo() ){
					MobilityPolicyTo inxpToObj = new MobilityPolicyTo();
					mobilityPolicyChildList_3.add(inxpToObj);
					greTunnelObj.setTo(inxpToObj);
				}
			}
		}
		generateMobilityPolicyLevel_4();
	}
	
	private void generateMobilityPolicyLevel_4() throws Exception {
		/**
		 * <mobility-policy>.<inxp>.<gre-tunnel>.<to>		MobilityPolicyTo
		 */
		for(Object childObj : mobilityPolicyChildList_3 ){
			
			/** element: <mobility-policy>.<inxp>.<gre-tunnel>.<to> */
			if(childObj instanceof MobilityPolicyTo ){
				MobilityPolicyTo inxpToObj = (MobilityPolicyTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/inxp/gre-tunnel",
						"to", GenerateXMLDebug.SET_VALUE,
						mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
				inxpToObj.setValue(mobPolicyImpl.getMobInxpToAddress());
				
				/** attribute: quoteProhibited */
				inxpToObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mobility-policy>.<inxp>.<gre-tunnel>.<to>.<password> */
				oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/inxp/gre-tunnel/to",
						"password", GenerateXMLDebug.SET_VALUE,
						mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
				inxpToObj.setPassword(
						CLICommonFunc.createAhEncryptedStringActValue(mobPolicyImpl.getMobInxpToPassword(), CLICommonFunc.getYesDefault())
				);
			}
		}
	}
	
	private void setMobilityPolicyFrom(MobilityPolicyFrom fromObj, int index ) throws Exception {
		/**
		 * <mobility-policy>.<inxp>.<gre-tunnel>.<from>			MobilityPolicyFrom
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/inxp/gre-tunnel",
				"from", GenerateXMLDebug.SET_NAME,
				mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
		fromObj.setName(mobPolicyImpl.getMobInxpFromAddress(index));
		
		/** attribute: operation */
		fromObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <mobility-policy>.<inxp>.<gre-tunnel>.<from>.<password> */
		oDebug.debug("/configuration/mobility-policy[@name='"+mobPolicyImpl.getMobilityPolicyName()+"']/inxp/gre-tunnel/from[@name='"+fromObj.getName()+"']",
				"password", GenerateXMLDebug.SET_NAME,
				mobPolicyImpl.getMobilityPolicyGuiName(), mobPolicyImpl.getMobilityPolicyName());
		fromObj.setPassword(CLICommonFunc.createAhEncryptedString(mobPolicyImpl.getMobInxpFromPassword(index)));
	}
}
