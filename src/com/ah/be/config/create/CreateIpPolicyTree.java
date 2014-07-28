package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.IpPolicyProfileInt;
import com.ah.be.config.create.source.IpPolicyProfileInt.IpPolicyActionValue;
import com.ah.xml.be.config.AhInt;
import com.ah.xml.be.config.IpPolicyAction;
import com.ah.xml.be.config.IpPolicyFrom;
import com.ah.xml.be.config.IpPolicyId;
import com.ah.xml.be.config.IpPolicyObj;
import com.ah.xml.be.config.IpPolicyTo;
import com.ah.xml.be.config.PolicyActionDeny;
import com.ah.xml.be.config.PolicyActionInterStationTrafficDrop;
import com.ah.xml.be.config.PolicyActionPermit;
import com.ah.xml.be.config.PolicyBefore;

/**
 * 
 * @author zhang
 *
 */
public class CreateIpPolicyTree {

	private IpPolicyProfileInt ipPolicyImpl;
	private IpPolicyObj ipPolicyObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> ipPolicyChildLevel_1 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_2 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_3 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_4 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_5 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_6 = new ArrayList<Object>();
	private List<Object> ipPolicyChildLevel_7 = new ArrayList<Object>();
	
	public CreateIpPolicyTree(IpPolicyProfileInt ipPolicyImpl, GenerateXMLDebug oDebug) {
		this.ipPolicyImpl = ipPolicyImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(ipPolicyImpl.isConfigIpPolicy()){
			ipPolicyObj = new IpPolicyObj();
			generateIpPolicyLevel_1();
		}
	}
	
	public IpPolicyObj getIpPolicyObj(){
		return this.ipPolicyObj;
	}
	
	private void generateIpPolicyLevel_1() throws Exception {
		/**
		 * <ip-policy>		IpPolicyObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"ip-policy", GenerateXMLDebug.SET_NAME,
				ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
		ipPolicyObj.setName(ipPolicyImpl.getIpPolicyName());
		
//		/** attribute: updateTime */
//		ipPolicyObj.setUpdateTime(ipPolicyImpl.getUpdateTime());
		
		/** attribute: operation */
		ipPolicyObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		ipPolicyObj.setCr("");
		
		/** element: <id> */
		for(int i=0; i<ipPolicyImpl.getIpPolicyIdSize(); i++){
			IpPolicyId policyIdObj = new IpPolicyId();
			ipPolicyChildLevel_1.add(policyIdObj);
			ipPolicyObj.getId().add(policyIdObj);
			
			generateIpPolicyLevel_2(i);
		}
	}
	
	private void generateIpPolicyLevel_2(int index) throws Exception {
		/***
		 * <ip-policy>.<id>		IpPolicyId
		 */
		for(Object childObj : ipPolicyChildLevel_1){
			
			/** element: <ip-policy>.<id> */
			if(childObj instanceof IpPolicyId){
				IpPolicyId policyIdObj = (IpPolicyId)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']", 
						"id", GenerateXMLDebug.SET_NAME,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				policyIdObj.setName(ipPolicyImpl.getIpPolicyIdName(index));
				
				/** attribute: operation */
				policyIdObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <ip-policy>.<id>.<from> */
				IpPolicyFrom fromObj = new IpPolicyFrom();
				ipPolicyChildLevel_2.add(fromObj);
				policyIdObj.setFrom(fromObj);
				
				/** element: <ip-policy>.<id>.<before> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']", 
						"before", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigBefore(index)){
					PolicyBefore beforeObj = new PolicyBefore();
					ipPolicyChildLevel_2.add(beforeObj);
					policyIdObj.setBefore(beforeObj);
				}
			}
		}
		ipPolicyChildLevel_1.clear();
		generateIpPolicyLevel_3(index);
	}
	
	private void generateIpPolicyLevel_3(int index) throws Exception {
		/***
		 * <ip-policy>.<id>.<from>			IpPolicyFrom
		 * <ip-policy>.<id>.<before>			PolicyBefore
		 */
		for(Object childObj : ipPolicyChildLevel_2){
			
			/** element: <ip-policy>.<id>.<from> */
			if(childObj instanceof IpPolicyFrom){
				IpPolicyFrom fromObj = (IpPolicyFrom)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']", 
						"from", GenerateXMLDebug.SET_VALUE,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				fromObj.setValue(ipPolicyImpl.getPolicyFromValue(index));
				
				/** attribute: quoteProhibited */
				fromObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ip-policy>.<id>.<from>.<to> */
				IpPolicyTo toObj = new IpPolicyTo();
				ipPolicyChildLevel_3.add(toObj);
				fromObj.setTo(toObj);
			}
			
			/** element: <ip-policy>.<id>.<before> */
			if(childObj instanceof PolicyBefore){
				PolicyBefore beforeObj = (PolicyBefore)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']", 
						"before", GenerateXMLDebug.SET_VALUE,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				beforeObj.setValue(ipPolicyImpl.getBeforeValue(index));
				
				/** attribute: operation */
				beforeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: quoteProhibited */
				beforeObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ip-policy>.<id>.<before>.<id> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/before", 
						"id", GenerateXMLDebug.SET_VALUE,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				Object[][] idParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ipPolicyImpl.getPolicyBeforeIdValue(index) }
				};
				beforeObj.setId(
						(AhInt)CLICommonFunc.createObjectWithName(AhInt.class, idParm)
				);
			}
		}
		ipPolicyChildLevel_2.clear();
		generateIpPolicyLevel_4(index);
	}
	
	private void generateIpPolicyLevel_4(int index) throws Exception {
		/**
		 * <ip-policy>.<id>.<from>.<to>		IpPolicyTo
		 */
		for(Object childObj : ipPolicyChildLevel_3){
			
			/** element: <ip-policy>.<id>.<from>.<to> */
			if(childObj instanceof IpPolicyTo){
				IpPolicyTo toObj = (IpPolicyTo)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from", 
						"to", GenerateXMLDebug.SET_VALUE,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				toObj.setValue(ipPolicyImpl.getPolicyToValue(index));
				
				/** attribute: quoteProhibited */
				toObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service> */
				IpPolicyTo.Service serviceObj = new IpPolicyTo.Service();
				ipPolicyChildLevel_4.add(serviceObj);
				toObj.setService(serviceObj);
			}
		}
		ipPolicyChildLevel_3.clear();
		generateIpPolicyLevel_5(index);
	}
	
	private void generateIpPolicyLevel_5(int index) throws CreateXMLException{
		/**
		 * <ip-policy>.<id>.<from>.<to>.<service>			IpPolicyTo.Service
		 */
		for(Object childObj : ipPolicyChildLevel_4){
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service> */
			if(childObj instanceof IpPolicyTo.Service){
				IpPolicyTo.Service serviceObj = (IpPolicyTo.Service)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to", 
						"service", GenerateXMLDebug.SET_NAME,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				serviceObj.setName(ipPolicyImpl.getPolicyServiceName(index));
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action> */
				IpPolicyAction actionObj = new IpPolicyAction();
				ipPolicyChildLevel_5.add(actionObj);
				serviceObj.setAction(actionObj);
			}
		}
		ipPolicyChildLevel_4.clear();
		generateIpPolicyLevel_6(index);
	}
	
	private void generateIpPolicyLevel_6(int index) throws CreateXMLException{
		/**
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>			IpPolicyAction
		 */
		for(Object childObj : ipPolicyChildLevel_5){
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action> */
			if(childObj instanceof IpPolicyAction){
				IpPolicyAction actionObj = (IpPolicyAction)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action", 
						"permit", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyAction(index, IpPolicyActionValue.permit)){
					PolicyActionPermit permitObj = new PolicyActionPermit();
					ipPolicyChildLevel_6.add(permitObj);
					actionObj.setPermit(permitObj);
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action", 
						"deny", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyAction(index, IpPolicyActionValue.deny)){
					PolicyActionDeny denyObj = new PolicyActionDeny();
					ipPolicyChildLevel_6.add(denyObj);
					actionObj.setDeny(denyObj);
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action", 
						"inter-station-traffic-drop", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyAction(index, IpPolicyActionValue.inter_station_traffic_drop)){
					PolicyActionInterStationTrafficDrop interStationTrafficDropObj = new PolicyActionInterStationTrafficDrop();
					ipPolicyChildLevel_6.add(interStationTrafficDropObj);
					actionObj.setInterStationTrafficDrop(interStationTrafficDropObj);
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<nat> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action", 
						"nat", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyAction(index, IpPolicyActionValue.nat)){
					actionObj.setNat("");
				}
			}
		}
		ipPolicyChildLevel_5.clear();
		generateIpPolicyLevel_7(index);
	}
	
	private void generateIpPolicyLevel_7(int index) throws CreateXMLException{
		/**
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>				PolicyActionPermit
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny>				PolicyActionDeny
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>		PolicyActionInterStationTrafficDrop
		 */
		for(Object childObj : ipPolicyChildLevel_6){
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit> */
			if(childObj instanceof PolicyActionPermit){
				PolicyActionPermit permitObj = (PolicyActionPermit)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/permit", 
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLog(index)){
					PolicyActionPermit.Log logObj = new PolicyActionPermit.Log();
					ipPolicyChildLevel_7.add(logObj);
					permitObj.setLog(logObj);
				}
			}
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny> */
			if(childObj instanceof PolicyActionDeny){
				PolicyActionDeny denyObj = (PolicyActionDeny)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny>.<log> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/deny", 
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLog(index)){
					PolicyActionDeny.Log logObj = new PolicyActionDeny.Log();
					ipPolicyChildLevel_7.add(logObj);
					denyObj.setLog(logObj);
				}
			}
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop> */
			if(childObj instanceof PolicyActionInterStationTrafficDrop){
				PolicyActionInterStationTrafficDrop interTrafficObj = (PolicyActionInterStationTrafficDrop)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/inter-station-traffic-drop", 
						"log", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLog(index)){
					PolicyActionInterStationTrafficDrop.Log logObj = new PolicyActionInterStationTrafficDrop.Log();
					ipPolicyChildLevel_7.add(logObj);
					interTrafficObj.setLog(logObj);
				}
			}
		}
		ipPolicyChildLevel_6.clear();
		generateIpPolicyLevel_8(index);
	}
	
	private void generateIpPolicyLevel_8(int index){
		/**
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log>			PolicyActionPermit.Log
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny>.<log>				PolicyActionDeny.Log
		 * <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log>	PolicyActionInterStationTrafficDrop.Log
		 */
		for(Object childObj : ipPolicyChildLevel_7){
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log> */
			if(childObj instanceof PolicyActionPermit.Log){
				PolicyActionPermit.Log logObj = (PolicyActionPermit.Log)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log>.<cr> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/permit/log", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.cr)){
					logObj.setCr("");
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log>.<initiate-session> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/permit/log", 
						"initiate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.initiate_session)){
					logObj.setInitiateSession("");
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<permit>.<log>.<terminate-session> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/permit/log", 
						"terminate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.terminate_session)){
					logObj.setTerminateSession("");
				}
			}
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny>.<log> */
			if(childObj instanceof PolicyActionDeny.Log){
				PolicyActionDeny.Log logObj = (PolicyActionDeny.Log)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<deny>.<log>.<packet-drop> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/deny/log", 
						"packet-drop", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.packet_drop)){
					logObj.setPacketDrop("");
				}
			}
			
			/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log> */
			if(childObj instanceof PolicyActionInterStationTrafficDrop.Log){
				PolicyActionInterStationTrafficDrop.Log logObj = (PolicyActionInterStationTrafficDrop.Log)childObj;
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log>.<initiate-session> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/inter-station-traffic-drop/log", 
						"initiate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.initiate_session)){
					logObj.setInitiateSession("");
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log>.<terminate-session> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/inter-station-traffic-drop/log", 
						"terminate-session", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.terminate_session)){
					logObj.setTerminateSession("");
				}
				
				/** element: <ip-policy>.<id>.<from>.<to>.<service>.<action>.<inter-station-traffic-drop>.<log>.<packet-drop> */
				oDebug.debug("/configuration/ip-policy[@name='"+ipPolicyImpl.getIpPolicyName()+"']/id[@name='"+ipPolicyImpl.getIpPolicyIdName(index)+"']/from/to/service/action/inter-station-traffic-drop/log", 
						"packet-drop", GenerateXMLDebug.CONFIG_ELEMENT,
						ipPolicyImpl.getIpPolicyGuiName(), ipPolicyImpl.getIpPolicyName());
				if(ipPolicyImpl.isConfigPolicyLogValue(index, IpPolicyProfileInt.IpPolicyLog.packet_drop)){
					logObj.setPacketDrop("");
				}
			}
		}
		ipPolicyChildLevel_7.clear();
	}
	
}
