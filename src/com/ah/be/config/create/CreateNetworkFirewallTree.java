package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.NetworkFirewallInt;
import com.ah.xml.be.config.NetworkFirewallAction;
import com.ah.xml.be.config.NetworkFirewallFrom;
import com.ah.xml.be.config.NetworkFirewallObj;
import com.ah.xml.be.config.NetworkFirewallRule;
import com.ah.xml.be.config.NetworkFirewallRuleOrder;
import com.ah.xml.be.config.NetworkFirewallService;
import com.ah.xml.be.config.NetworkFirewallTo;

public class CreateNetworkFirewallTree {
	
	private NetworkFirewallInt netFirewallImpl;
	private GenerateXMLDebug oDebug;
	
	private NetworkFirewallObj netFirewallObj;
	
	private List<Object> netFirewallChildLevel_1 = new ArrayList<Object>();
	private List<Object> netFirewallChildLevel_2 = new ArrayList<Object>();
	private List<Object> netFirewallChildLevel_3 = new ArrayList<Object>();
	private List<Object> netFirewallChildLevel_4 = new ArrayList<Object>();

	public CreateNetworkFirewallTree(NetworkFirewallInt netFirewallImpl, GenerateXMLDebug oDebug){
		this.netFirewallImpl = netFirewallImpl;
		this.oDebug = oDebug;
	}
	
	public NetworkFirewallObj getNetworkFirewallObj(){
		return this.netFirewallObj;
	}
	
	public void generate() throws Exception{
		if(netFirewallImpl.isConfigNetFirewall()){
			netFirewallObj = new NetworkFirewallObj();
			generateLevel_1();
		}
	}
	
	private void generateLevel_1(){
		
		/** attribute: operation */
		netFirewallObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: name */
		for(int index=0; index < netFirewallImpl.getNetFirewallRullSize(); index++){
			netFirewallObj.getName().add(this.createNetworkFirewallRule(index));
		}
	}
	
	private NetworkFirewallRule createNetworkFirewallRule(int index){
		NetworkFirewallRule rule = new NetworkFirewallRule();
		
		/** attribute: name */
		rule.setName(netFirewallImpl.getNetFirewallRullName(index));
		
		/** element: <network-firewall>.<name>.<cr> */
		NetworkFirewallRuleOrder crObj = new NetworkFirewallRuleOrder();
		netFirewallChildLevel_1.add(crObj);
		rule.setCr(crObj);
		
		/** element: <network-firewall>.<name>.<from> */
		NetworkFirewallFrom fromObj = new NetworkFirewallFrom();
		netFirewallChildLevel_1.add(fromObj);
		rule.setFrom(fromObj);
		
		generateNetworkFirewallRuleLevel_1(index);
		
		return rule;
	}
	
	private void generateNetworkFirewallRuleLevel_1(int index){
		/**
		 * <network-firewall>.<name>.<cr>						NetworkFirewallRuleOrder
		 * <network-firewall>.<name>.<from>						NetworkFirewallFrom
		 */
		for(Object childObj : netFirewallChildLevel_1){
			
			/** element: <network-firewall>.<name>.<cr> */
			if(childObj instanceof NetworkFirewallRuleOrder){
				NetworkFirewallRuleOrder crObj = (NetworkFirewallRuleOrder)childObj;
				
				/** attribute: name */
				crObj.setName(netFirewallImpl.getNetFirewallRullPosition(index));
			}
			
			/** element: <network-firewall>.<name>.<from> */
			if(childObj instanceof NetworkFirewallFrom){
				NetworkFirewallFrom fromObj = (NetworkFirewallFrom)childObj;
				
				/** attribute: name */
				fromObj.setName(netFirewallImpl.getNetFirewallRullFrom(index));
				
				fromObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <network-firewall>.<name>.<from>.<to> */
				NetworkFirewallTo toObj = new NetworkFirewallTo();
				netFirewallChildLevel_2.add(toObj);
				fromObj.setTo(toObj);
			}
		}
		netFirewallChildLevel_1.clear();
		generateNetworkFirewallRuleLevel_2(index);
		
	}
	
	private void generateNetworkFirewallRuleLevel_2(int index){
		/**
		 * <network-firewall>.<name>.<from>.<to>					NetworkFirewallTo
		 */
		for(Object childObj : netFirewallChildLevel_2){
			
			/** element: <network-firewall>.<name>.<from>.<to> */
			if(childObj instanceof NetworkFirewallTo){
				NetworkFirewallTo toObj = (NetworkFirewallTo)childObj;
				
				/** attribute: name */
				toObj.setName(netFirewallImpl.getNetFirewallRullTo(index));
				
				toObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <network-firewall>.<name>.<from>.<to>.<service> */
				NetworkFirewallService serviceObj = new NetworkFirewallService();
				netFirewallChildLevel_3.add(serviceObj);
				toObj.setService(serviceObj);
			}
		}
		netFirewallChildLevel_2.clear();
		generateNetworkFirewallRuleLevel_3(index);
	}
	
	private void generateNetworkFirewallRuleLevel_3(int index){
		/**
		 * <network-firewall>.<name>.<from>.<to>.<service>						NetworkFirewallService
		 */
		for(Object childObj : netFirewallChildLevel_3){
			
			/** element: <network-firewall>.<name>.<from>.<to>.<service> */
			if(childObj instanceof NetworkFirewallService){
				NetworkFirewallService serviceObj = (NetworkFirewallService)childObj;
				
				/** attribute: name */
				serviceObj.setName(netFirewallImpl.getNetFirewallRullService(index));
				
				/** element: <network-firewall>.<name>.<from>.<to>.<service>.<action> */
				NetworkFirewallAction actionObj = new NetworkFirewallAction();
				netFirewallChildLevel_4.add(actionObj);
				serviceObj.setAction(actionObj);
			}
		}
		netFirewallChildLevel_3.clear();
		generateNetworkFirewallRuleLevel_4(index);
	}
	
	private void generateNetworkFirewallRuleLevel_4(int index){
		/**
		 * <network-firewall>.<name>.<from>.<to>.<service>.<action>						NetworkFirewallAction
		 */
		for(Object childObj : netFirewallChildLevel_4){
			
			/** element: <network-firewall>.<name>.<from>.<to>.<service>.<action> */
			if(childObj instanceof NetworkFirewallAction){
				NetworkFirewallAction actionObj = (NetworkFirewallAction)childObj;
				
				/** attribute: name */
				actionObj.setName(netFirewallImpl.getNetFirewallRullAction(index));
				
				/** element: <network-firewall>.<name>.<from>.<to>.<service>.<action>.<logging> */
				actionObj.setLogging(CLICommonFunc.createAhName(netFirewallImpl.getNetFirewallRullLogging(index)));
			}
		}
		netFirewallChildLevel_4.clear();
	}
}
