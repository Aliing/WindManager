package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.IpPolicyProfileInt;
import com.ah.be.config.create.source.MacPolicyProfileInt;
import com.ah.xml.be.config.AhInt;
import com.ah.xml.be.config.MacPolicyAction;
import com.ah.xml.be.config.MacPolicyFrom;
import com.ah.xml.be.config.MacPolicyId;
import com.ah.xml.be.config.MacPolicyObj;
import com.ah.xml.be.config.MacPolicyTo;
import com.ah.xml.be.config.PolicyActionDeny;
import com.ah.xml.be.config.PolicyActionPermit;
import com.ah.xml.be.config.PolicyBefore;

/**
 * 
 * @author zhang
 *
 */
public class CreateMacPolicyTree {
	
	private MacPolicyProfileInt macPolicyImpl;
	private MacPolicyObj macPolicyObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> macPolicyChildLevel_1 = new ArrayList<Object>();
	private List<Object> macPolicyChildLevel_2 = new ArrayList<Object>();
	private List<Object> macPolicyChildLevel_3 = new ArrayList<Object>();
	private List<Object> macPolicyChildLevel_4 = new ArrayList<Object>();
	private List<Object> macPolicyChildLevel_5 = new ArrayList<Object>();
	private List<Object> macPolicyChildLevel_6 = new ArrayList<Object>();

	public CreateMacPolicyTree(MacPolicyProfileInt macPolicyImpl, GenerateXMLDebug oDebug) {
		this.macPolicyImpl = macPolicyImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		macPolicyObj = new MacPolicyObj();
		generateMacPolicyLevel_1();
	}
	
	public MacPolicyObj getMacPolicyObj(){
		return macPolicyObj;
	}
	
	private void generateMacPolicyLevel_1() throws Exception {
		/**
		 * <mac-policy>		MacPolicyObj
		 */
		
		/** attribute: updateTime */
		macPolicyObj.setUpdateTime(macPolicyImpl.getUpdateTime());
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"mac-policy", GenerateXMLDebug.SET_NAME,
				macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
		macPolicyObj.setName(macPolicyImpl.getMacPolicyName());
		
		/** attribute: operation */
		macPolicyObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		macPolicyObj.setCr("");
		
		/** element: <mac-policy>.<id> */
		for(int i=0; i<macPolicyImpl.getMacPolicyIdSize(); i++ ){
			MacPolicyId idObj = new MacPolicyId();
			macPolicyChildLevel_1.add(idObj);
			macPolicyObj.getId().add(idObj);
			
			generateMacPolicyLevel_2(i);
		}
	}
	
	private void generateMacPolicyLevel_2(int index) throws Exception{
		/**
		 * <mac-policy>.<id>		MacPolicyId
		 */
		for(Object childObj : macPolicyChildLevel_1){
			
			/** element: <mac-policy>.<id> */
			if(childObj instanceof MacPolicyId){
				MacPolicyId idObj = (MacPolicyId)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']",
						"id", GenerateXMLDebug.SET_NAME,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				idObj.setName(macPolicyImpl.getMacPolicyIdName(index));
				
				/** attribute: operation */
				idObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <mac-policy>.<id>.<from> */
				MacPolicyFrom fromObj = new MacPolicyFrom();
				macPolicyChildLevel_2.add(fromObj);
				idObj.setFrom(fromObj);
				
				/** element: <mac-policy>.<id>.<before> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']",
						"before", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigBefore(index)){
					PolicyBefore beforeObj = new PolicyBefore();
					macPolicyChildLevel_2.add(beforeObj);
					idObj.setBefore(beforeObj);
				}
				
			}
			
		}
		macPolicyChildLevel_1.clear();
		generateMacPolicyLevel_3(index);
	}
	
	private void generateMacPolicyLevel_3(int index) throws Exception {
		/**
		 * <mac-policy>.<id>.<from>			MacPolicyFrom
		 * <mac-policy>.<id>.<before>		PolicyBefore
		 */
		for(Object childObj : macPolicyChildLevel_2){
			
			/** element: <mac-policy>.<id>.<from> */
			if(childObj instanceof MacPolicyFrom){
				MacPolicyFrom fromObj = (MacPolicyFrom)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']",
						"from", GenerateXMLDebug.SET_VALUE,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				fromObj.setValue(macPolicyImpl.getPolicyFromValue(index));
				
				/** attribute: quoteProhibited */
				fromObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mac-policy>.<id>.<from>.<to> */
				MacPolicyTo toObj = new MacPolicyTo();
				macPolicyChildLevel_3.add(toObj);
				fromObj.setTo(toObj);
			}
			
			/** element: <mac-policy>.<id>.<before> */
			if(childObj instanceof PolicyBefore){
				PolicyBefore beforeObj = (PolicyBefore)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']",
						"before", GenerateXMLDebug.SET_VALUE,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				beforeObj.setValue(macPolicyImpl.getBeforeValue(index));
				
				/** attribute: operation */
				beforeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: quoteProhibited */
				beforeObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mac-policy>.<id>.<before>.<id> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/before",
						"id", GenerateXMLDebug.SET_VALUE,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				Object[][] idParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, macPolicyImpl.getPolicyBeforeIdValue(index)}
				};
				beforeObj.setId(
						(AhInt)CLICommonFunc.createObjectWithName(AhInt.class, idParm)
				);
			}
		}
		macPolicyChildLevel_2.clear();
		generateMacPolicyLevel_4(index);
	}
	
	private void generateMacPolicyLevel_4(int index) throws Exception{
		/**
		 * <mac-policy>.<id>.<from>.<to>				MacPolicyTo
		 */
		for(Object childObj : macPolicyChildLevel_3){
			
			/** element: <mac-policy>.<id>.<from>.<to> */
			if(childObj instanceof MacPolicyTo){
				MacPolicyTo toObj = (MacPolicyTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from",
						"to", GenerateXMLDebug.SET_VALUE,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				toObj.setValue(macPolicyImpl.getPolicyToValue(index));
				
				/** attribute: quoteProhibited */
				toObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action> */
				MacPolicyAction actionObj = new MacPolicyAction();
				macPolicyChildLevel_4.add(actionObj);
				toObj.setAction(actionObj);
			}
		}
		macPolicyChildLevel_3.clear();
		generateMacPolicyLevel_5(index);
	}
	
	private void generateMacPolicyLevel_5(int index) throws Exception{
		/**
		 * <mac-policy>.<id>.<from>.<to>.<action>				MacPolicyAction
		 */
		for(Object childObj : macPolicyChildLevel_4){
			
			/** element: <mac-policy>.<id>.<from>.<to>.<action> */
			if(childObj instanceof MacPolicyAction){
				MacPolicyAction actionObj = (MacPolicyAction)childObj;
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action",
						"permit", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyAction(index, IpPolicyProfileInt.IpPolicyActionValue.permit)){
					PolicyActionPermit permitObj = new PolicyActionPermit();
					macPolicyChildLevel_5.add(permitObj);
					actionObj.setPermit(permitObj);
				}
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<deny> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action",
						"deny", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyAction(index, IpPolicyProfileInt.IpPolicyActionValue.deny)){
					PolicyActionDeny denyObj = new PolicyActionDeny();
					macPolicyChildLevel_5.add(denyObj);
					actionObj.setDeny(denyObj);
				}
			}
		}
		macPolicyChildLevel_4.clear();
		generateMacPolicyLevel_6(index);
	}
	
	private void generateMacPolicyLevel_6(int index) throws Exception{
		/**
		 * <mac-policy>.<id>.<from>.<to>.<action>.<permit>			PolicyActionPermit
		 * <mac-policy>.<id>.<from>.<to>.<action>.<deny>			PolicyActionDeny
		 */
		for(Object childObj : macPolicyChildLevel_5){
			
			/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit> */
			if(childObj instanceof PolicyActionPermit){
				PolicyActionPermit permitObj = (PolicyActionPermit)childObj;
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/permit",
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLog(index)){
					PolicyActionPermit.Log logObj = new PolicyActionPermit.Log();
					macPolicyChildLevel_6.add(logObj);
					permitObj.setLog(logObj);
				}
			}
			
			/** element: <mac-policy>.<id>.<from>.<to>.<action>.<deny> */
			if(childObj instanceof PolicyActionDeny){
				PolicyActionDeny denyObj = (PolicyActionDeny)childObj;
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<deny>.<log> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/deny",
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLog(index)){
					PolicyActionDeny.Log logObj = new PolicyActionDeny.Log();
					macPolicyChildLevel_6.add(logObj);
					denyObj.setLog(logObj);
				}
			}
		}
		macPolicyChildLevel_5.clear();
		generateMacPolicyLevel_7(index);
	}
	
	private void generateMacPolicyLevel_7(int index) throws Exception{
		/**
		 * <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log>		PolicyActionPermit.Log
		 * <mac-policy>.<id>.<from>.<to>.<action>.<deny>.<log>			PolicyActionDeny.Log
		 */
		for(Object childObj : macPolicyChildLevel_6){
			
			/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log> */
			if(childObj instanceof PolicyActionPermit.Log){
				PolicyActionPermit.Log logObj = (PolicyActionPermit.Log)childObj;
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log>.<cr> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/permit/log",
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLogType(index, IpPolicyProfileInt.IpPolicyLog.cr)){
					logObj.setCr("");
				}
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log>.<initiate-session> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/permit/log",
						"initiate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLogType(index, IpPolicyProfileInt.IpPolicyLog.initiate_session)){
					logObj.setInitiateSession("");
				}
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<permit>.<log>.<terminate-session> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/permit/log",
						"terminate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLogType(index, IpPolicyProfileInt.IpPolicyLog.terminate_session)){
					logObj.setTerminateSession("");
				}
			}
			
			/** element: <mac-policy>.<id>.<from>.<to>.<action>.<deny>.<log> */
			if(childObj instanceof PolicyActionDeny.Log){
				PolicyActionDeny.Log logObj = (PolicyActionDeny.Log)childObj;
				
				/** element: <mac-policy>.<id>.<from>.<to>.<action>.<deny>.<log>.<packet-drop> */
				oDebug.debug("/configuration/mac-policy[@name='"+macPolicyImpl.getMacPolicyName()+"']/id[@name='"+macPolicyImpl.getMacPolicyIdName(index)+"']/from/to/action/deny/log",
						"packet-drop", GenerateXMLDebug.CONFIG_ELEMENT,
						macPolicyImpl.getMacPolicyGuiName(), macPolicyImpl.getMacPolicyName());
				if(macPolicyImpl.isConfigPolicyLogType(index, IpPolicyProfileInt.IpPolicyLog.packet_drop)){
					logObj.setPacketDrop("");
				}
			}
		}
		macPolicyChildLevel_6.clear();
	}
	
}
