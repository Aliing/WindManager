package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ForwardingEngineInt;
import com.ah.xml.be.config.AhEnable;
import com.ah.xml.be.config.FeL2DefaultRoute;
import com.ah.xml.be.config.FeL2DefaultRouteEthx;
import com.ah.xml.be.config.FeL2DefaultRouteInterface;
import com.ah.xml.be.config.FeMacSessions;
import com.ah.xml.be.config.FeTunnelMulticastForwardAllow;
import com.ah.xml.be.config.FeTunnelMulticastForwardBlock;
import com.ah.xml.be.config.FeTunnelSelectiveMulticastForward;
import com.ah.xml.be.config.ForwardingAction;
import com.ah.xml.be.config.ForwardingEngineObj;
import com.ah.xml.be.config.ForwardingEngineStaticRule;
import com.ah.xml.be.config.ForwardingEngineTunnel;
import com.ah.xml.be.config.ForwardingInIf;
import com.ah.xml.be.config.ForwardingInIfDst;
import com.ah.xml.be.config.ForwardingInIfSrc;
import com.ah.xml.be.config.ForwardingInIfTxMac;
import com.ah.xml.be.config.ForwardingOutIf;
import com.ah.xml.be.config.TcpMssThresholdSize;

/**
 * @author zhang
 * @version 2007-12-20 10:34:51
 */

public class CreateForwardingEngineTree {

	private ForwardingEngineInt forwardingEngineImpl;
	private ForwardingEngineObj forwardObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> forwardingChildLevel_1 = new ArrayList<Object>();
	private List<Object> forwardingChildLevel_2 = new ArrayList<Object>();
	private List<Object> forwardingChildLevel_3 = new ArrayList<Object>();
	
	private List<Object> staticRuleChildLevel_1 = new ArrayList<Object>();
	private List<Object> staticRuleChildLevel_2 = new ArrayList<Object>();
	private List<Object> staticRuleChildLevel_3 = new ArrayList<Object>();
	private List<Object> staticRuleChildLevel_4 = new ArrayList<Object>();
	private List<Object> staticRuleChildLevel_5 = new ArrayList<Object>();
	private List<Object> staticRuleChildLevel_6 = new ArrayList<Object>();
	
	public CreateForwardingEngineTree(ForwardingEngineInt forwardingEngineImpl, GenerateXMLDebug oDebug) {
		this.forwardingEngineImpl = forwardingEngineImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws CreateXMLException, Exception{
		forwardObj = new ForwardingEngineObj();
		generateForwardLevel_1();
	}
	
	public ForwardingEngineObj getForwardingEngineObj(){
		return this.forwardObj;
	}
	
	private void generateForwardLevel_1() throws Exception{
		/**
		 * <forwarding-engine>			ForwardingEngineObj
		 */
		
		if(forwardingEngineImpl.isConfigForwardingEngine()){
			/** attribute: updateTime */
			forwardObj.setUpdateTime(forwardingEngineImpl.getUpdateTime());
			
			/** element: <forwarding-engine>.<inter-ssid-flood> */
			oDebug.debug("/configuration/forwarding-engine", 
					"inter-ssid-flood", GenerateXMLDebug.SET_OPERATION,
					forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
			forwardObj.setInterSsidFlood(this.createAhEnable(forwardingEngineImpl.isEnableInterSsidFlood()));
			
			/** element: <forwarding-engine>.<proxy-arp> */
			oDebug.debug("/configuration/forwarding-engine", 
					"proxy-arp", GenerateXMLDebug.SET_OPERATION,
					forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
			forwardObj.setProxyArp(this.createAhEnable(forwardingEngineImpl.isEnableProxyArp()));
			
			/** element: <forwarding-engine>.<max-ip-sessions-per-station> */
			oDebug.debug("/configuration/forwarding-engine", 
					"max-ip-sessions-per-station", GenerateXMLDebug.CONFIG_ELEMENT,
					forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
			if(forwardingEngineImpl.isConfigIpSession()){
				
				oDebug.debug("/configuration/forwarding-engine", 
						"max-ip-sessions-per-station", GenerateXMLDebug.SET_VALUE,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				Object[][] ipSessionParm ={
						{CLICommonFunc.ATTRIBUTE_VALUE, forwardingEngineImpl.getIpSession()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				forwardObj.setMaxIpSessionsPerStation(
						(ForwardingEngineObj.MaxIpSessionsPerStation)CLICommonFunc.createObjectWithName(ForwardingEngineObj.MaxIpSessionsPerStation.class, ipSessionParm)
				);
			}
			
			
			/** element: <forwarding-engine>.<max-mac-sessions-per-station> */
			oDebug.debug("/configuration/forwarding-engine", 
					"max-mac-sessions-per-station", GenerateXMLDebug.CONFIG_ELEMENT,
					forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
			if(forwardingEngineImpl.isConfigMacSession()){
				
				oDebug.debug("/configuration/forwarding-engine", 
						"max-mac-sessions-per-station", GenerateXMLDebug.SET_VALUE,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				Object[][] macSessionParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, forwardingEngineImpl.getMacSession()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				forwardObj.setMaxMacSessionsPerStation(
						(ForwardingEngineObj.MaxMacSessionsPerStation)CLICommonFunc.createObjectWithName(ForwardingEngineObj.MaxMacSessionsPerStation.class, macSessionParm)
				);
			}
			
			/** element: <forwarding-engine>.<log> */
			ForwardingEngineObj.Log logObj = new ForwardingEngineObj.Log();
			forwardingChildLevel_1.add(logObj);
			forwardObj.setLog(logObj);
			
			/** element: <forwarding-engine>.<drop> */
			ForwardingEngineObj.Drop dropObj = new ForwardingEngineObj.Drop();
			forwardingChildLevel_1.add(dropObj);
			forwardObj.setDrop(dropObj);
			
			/** element: <forwarding-engine>.<tunnel> */
			oDebug.debug("/configuration/forwarding-engine", 
					"tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
					forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
			if(forwardingEngineImpl.isConfigTunnel()){
				ForwardingEngineTunnel tunnelObj = new ForwardingEngineTunnel();
				forwardingChildLevel_1.add(tunnelObj);
				forwardObj.setTunnel(tunnelObj);
			}
			
			/** element: <forwarding-engine>.<mac-sessions> */
			FeMacSessions macSessions = new FeMacSessions();
			forwardingChildLevel_1.add(macSessions);
			forwardObj.setMacSessions(macSessions);
		}
		
		/** element: <forwarding-engine>.<static-rule> */
		if(forwardingEngineImpl.isConfigStaticRule()){
			for(int index=0; index<forwardingEngineImpl.getStaticRuleSize(); index++){
				forwardObj.getStaticRule().add(createForwardingEngineStaticRule(index));
			}
		}
		
		/** element: <forwarding-engine>.<l2-default-route> */
		FeL2DefaultRoute l2Route = new FeL2DefaultRoute();
		forwardingChildLevel_1.add(l2Route);
		forwardObj.setL2DefaultRoute(l2Route);
		
		generateForwardLevel_2();
	}
	
	private void generateForwardLevel_2() throws Exception{
		/**
		 * <forwarding-engine>.<log>				ForwardingEngineObj.Log
		 * <forwarding-engine>.<drop>				ForwardingEngineObj.Drop
		 * <forwarding-engine>.<tunnel>				ForwardingEngineTunnel
		 * <forwarding-engine>.<l2-default-route>	FeL2DefaultRoute
		 */
		for(Object childObj : forwardingChildLevel_1){
			
			/** element: <forwarding-engine>.<log> */
			if(childObj instanceof ForwardingEngineObj.Log){
				ForwardingEngineObj.Log logObj = (ForwardingEngineObj.Log)childObj;
				
				/** element: <forwarding-engine>.<log>.<firewall-dropped-packets> */
				oDebug.debug("/configuration/forwarding-engine/log", 
						"firewall-dropped-packets", GenerateXMLDebug.SET_OPERATION,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				logObj.setFirewallDroppedPackets(CLICommonFunc.getAhOnlyAct(forwardingEngineImpl.isEnableLogDroppedPackets()));
				
				/** element: <forwarding-engine>.<log>.<to-self-sessionsEnable> */
				oDebug.debug("/configuration/forwarding-engine/log", 
						"to-self-sessionsEnable", GenerateXMLDebug.SET_OPERATION,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				logObj.setToSelfSessions(CLICommonFunc.getAhOnlyAct(forwardingEngineImpl.isEnableLogSessionsEnable()));
				
			}
			
			/** element: <forwarding-engine>.<drop> */
			if(childObj instanceof ForwardingEngineObj.Drop){
				ForwardingEngineObj.Drop dropObj = (ForwardingEngineObj.Drop)childObj;
				
				/** element: <forwarding-engine>.<drop>.<ip-fragmented-packets> */
				oDebug.debug("/configuration/forwarding-engine/drop", 
						"ip-fragmented-packets", GenerateXMLDebug.SET_OPERATION,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				dropObj.setIpFragmentedPackets(CLICommonFunc.getAhOnlyAct(forwardingEngineImpl.isEnableDropFragmentedPackets()));
				
				/** element: <forwarding-engine>.<drop>.<to-self-non-management-traffic> */
				oDebug.debug("/configuration/forwarding-engine/drop", 
						"to-self-non-management-traffic", GenerateXMLDebug.SET_OPERATION,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				dropObj.setToSelfNonManagementTraffic(CLICommonFunc.getAhOnlyAct(forwardingEngineImpl.isEnableDropManagementTraffic()));
				
			}
			
			/** element: <forwarding-engine>.<tunnel> */
			if(childObj instanceof ForwardingEngineTunnel){
				ForwardingEngineTunnel tunnelObj = (ForwardingEngineTunnel)childObj;
				
				/** element: <forwarding-engine>.<tunnel>.<tcp-mss-threshold> */
				ForwardingEngineTunnel.TcpMssThreshold tcpMssThresholdObj = new ForwardingEngineTunnel.TcpMssThreshold();
				forwardingChildLevel_2.add(tcpMssThresholdObj);
				tunnelObj.setTcpMssThreshold(tcpMssThresholdObj);
				
				/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward> */
				FeTunnelSelectiveMulticastForward forwardObj = new FeTunnelSelectiveMulticastForward();
				forwardingChildLevel_2.add(forwardObj);
				tunnelObj.setSelectiveMulticastForward(forwardObj);
			}
			
			/** element: <forwarding-engine>.<l2-default-route> */
			if(childObj instanceof FeL2DefaultRoute){
				FeL2DefaultRoute l2Route = (FeL2DefaultRoute)childObj;
				
				/** element: <forwarding-engine>.<l2-default-route>.<interface> */
				FeL2DefaultRouteInterface interfaceObj = new FeL2DefaultRouteInterface();
				forwardingChildLevel_2.add(interfaceObj);
				l2Route.setInterface(interfaceObj);
			}
			/** element: <forwarding-engine>.<mac-sessions> */
			if(childObj instanceof FeMacSessions){
				FeMacSessions macSessions = (FeMacSessions)childObj;
				
				/** element: <forwarding-engine>.<mac-sessions>.<sync-vlan> */
				if(forwardingEngineImpl.isConfigForwardsyncVlan()){
					oDebug.debug("/configuration/forwarding-engine/mac-sessions", 
							"sync-vlan", GenerateXMLDebug.CONFIG_ELEMENT,
							forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
					macSessions.setSyncVlan(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
			
		}
		forwardingChildLevel_1.clear();
		generateForwardLevel_3();
	}
	
	private void generateForwardLevel_3() throws Exception{
		/**
		 * <forwarding-engine>.<tunnel>.<tcp-mss-threshold>					ForwardingEngineTunnel.TcpMssThreshold
		 * <forwarding-engine>.<l2-default-route>.<interface>				FeL2DefaultRouteInterface
		 * <forwarding-engine>.<tunnel>.<selective-multicast-forward>		FeTunnelSelectiveMulticastForward
		 */
		for(Object childObj : forwardingChildLevel_2){
			
			/** element: <forwarding-engine>.<tunnel>.<tcp-mss-threshold> */
			if(childObj instanceof ForwardingEngineTunnel.TcpMssThreshold){
				ForwardingEngineTunnel.TcpMssThreshold tcpMssThresholdObj = (ForwardingEngineTunnel.TcpMssThreshold)childObj;
				
				/** element: <forwarding-engine>.<tunnel>.<tcp-mss-threshold>.<enable> */
				oDebug.debug("/configuration/forwarding-engine/tunnel/tcp-mss-threshold", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				tcpMssThresholdObj.setEnable(CLICommonFunc.getAhOnlyAct(forwardingEngineImpl.isEnableTcpMss()));
				
				/** element: <forwarding-engine>.<tunnel>.<tcp-mss-threshold>.<threshold-size> */
				if(forwardingEngineImpl.isConfigTcpMssThresholdSize()){
					
					oDebug.debug("/configuration/forwarding-engine/tunnel/tcp-mss-threshold", 
							"enable", GenerateXMLDebug.SET_VALUE,
							forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
					Object[][] tcpMssParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, forwardingEngineImpl.getTcpMssThresholdSize()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					tcpMssThresholdObj.setThresholdSize((TcpMssThresholdSize)CLICommonFunc.createObjectWithName(TcpMssThresholdSize.class, tcpMssParm));
				}
			}
			
			/** element: <forwarding-engine>.<l2-default-route>.<interface>	*/
			if(childObj instanceof FeL2DefaultRouteInterface){
				FeL2DefaultRouteInterface interfaceObj = (FeL2DefaultRouteInterface)childObj;
				
				/** element: <forwarding-engine>.<l2-default-route>.<interface>.<eth1> */
				FeL2DefaultRouteEthx eth1Route = new FeL2DefaultRouteEthx();
				forwardingChildLevel_3.add(eth1Route);
				interfaceObj.setEth1(eth1Route);
			}
			
			/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>	*/
			if(childObj instanceof FeTunnelSelectiveMulticastForward){
				FeTunnelSelectiveMulticastForward forwardObj = (FeTunnelSelectiveMulticastForward)childObj;
				
				/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<allow-all>	*/
				oDebug.debug("/configuration/forwarding-engine/tunnel/selective-multicast-forward", 
						"allow-all", GenerateXMLDebug.CONFIG_ELEMENT,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				if(forwardingEngineImpl.isConfigForwardAllowAll()){
					FeTunnelMulticastForwardAllow allowAllObj = new FeTunnelMulticastForwardAllow();
					forwardingChildLevel_3.add(allowAllObj);
					forwardObj.setAllowAll(allowAllObj);
				}
				
				/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all>	*/
				oDebug.debug("/configuration/forwarding-engine/tunnel/selective-multicast-forward", 
						"block-all", GenerateXMLDebug.CONFIG_ELEMENT,
						forwardingEngineImpl.getMgmtServiceGuiName(), forwardingEngineImpl.getMgmtServiceName());
				if(forwardingEngineImpl.isConfigForwardBlockAll()){
					FeTunnelMulticastForwardBlock blockAllObj = new FeTunnelMulticastForwardBlock();
					forwardingChildLevel_3.add(blockAllObj);
					forwardObj.setBlockAll(blockAllObj);
				}
			}
		}
		forwardingChildLevel_2.clear();
		generateForwardLevel_4();
	}
	
	private void generateForwardLevel_4() throws Exception{
		/**
		 * <forwarding-engine>.<l2-default-route>.<interface>.<eth1>				FeL2DefaultRouteEthx
		 * <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<allow-all>	FeTunnelMulticastForwardAllow
		 * <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all>	FeTunnelMulticastForwardBlock
		 */
		for(Object childObj : forwardingChildLevel_3){
			
			if(childObj instanceof FeL2DefaultRouteEthx){
				FeL2DefaultRouteEthx eth1Route = (FeL2DefaultRouteEthx)childObj;
				
				/** element: <forwarding-engine>.<l2-default-route>.<interface>.<eth1>.<vlan> */
				for(int index=0; index<forwardingEngineImpl.getMultiNativeVlanSize(); index++){
					eth1Route.getVlan().add(CLICommonFunc.createAhNameActValueQuoteProhibited(
							forwardingEngineImpl.getMultiNativeVlanName(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<allow-all> */
			if(childObj instanceof FeTunnelMulticastForwardAllow){
				FeTunnelMulticastForwardAllow allowAllObj = (FeTunnelMulticastForwardAllow)childObj;
				
				for(int index=0; index<forwardingEngineImpl.getForwardExceptSize(); index++){
					allowAllObj.getExcept().add(CLICommonFunc.createAhNameActValue(
							forwardingEngineImpl.getForwardExceptValue(index), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all> */
			if(childObj instanceof FeTunnelMulticastForwardBlock){
				FeTunnelMulticastForwardBlock blockAllObj = (FeTunnelMulticastForwardBlock)childObj;
				
				/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all>.<cr> */
				blockAllObj.setCr(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				
				/** element: <forwarding-engine>.<tunnel>.<selective-multicast-forward>.<block-all>.<except> */
				for(int index=0; index<forwardingEngineImpl.getForwardExceptSize(); index++){
					blockAllObj.getExcept().add(CLICommonFunc.createAhNameActValue(
							forwardingEngineImpl.getForwardExceptValue(index), CLICommonFunc.getYesDefault()));
				}
			}
		}
		forwardingChildLevel_3.clear();
	}
	
	private AhEnable createAhEnable(boolean isYes){
		AhEnable enableTypeObj = new AhEnable();
		enableTypeObj.setEnable(CLICommonFunc.getAhOnlyAct(isYes));
		return enableTypeObj;
	}
	
	private ForwardingEngineStaticRule createForwardingEngineStaticRule(int index){
		ForwardingEngineStaticRule rule = new ForwardingEngineStaticRule();
		
		/** attribute: name */
		rule.setName(forwardingEngineImpl.getStaticRuleName(index));
		
		/** attribute: operation */
		rule.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <action> */
		ForwardingAction actionObj = new ForwardingAction();
		staticRuleChildLevel_1.add(actionObj);
		rule.setAction(actionObj);
		
		generateStaticRuleLevel_1(index);
		
		return rule;
	}
	
	private void generateStaticRuleLevel_1(int index){
		/**
		 * <action>				ForwardingAction
		 */
		for(Object childObj : staticRuleChildLevel_1){
			
			if(childObj instanceof ForwardingAction){
				ForwardingAction action = (ForwardingAction)childObj;
				
				/** attribute: value */
				action.setName(forwardingEngineImpl.getRuleActionValue(index));
				
				/** element: <action>.<in-if> */
				ForwardingInIf inIfObj = new ForwardingInIf();
				staticRuleChildLevel_2.add(inIfObj);
				action.setInIf(inIfObj);
			}
		}
		staticRuleChildLevel_1.clear();
		generateStaticRuleLevel_2(index);
	}
	
	private void generateStaticRuleLevel_2(int index){
		/**
		 * <action>.<in-if>				ForwardingInIf
		 */
		for(Object childObj : staticRuleChildLevel_2){
			
			if(childObj instanceof ForwardingInIf){
				ForwardingInIf inifObj = (ForwardingInIf)childObj;
				
				/** attribute: value */
				inifObj.setName(forwardingEngineImpl.getRuleInifValue(index));
				
				if(forwardingEngineImpl.isConfigRuleSrcOui(index)){
					ForwardingInIfSrc srcOuiObj = new ForwardingInIfSrc();
					staticRuleChildLevel_3.add(srcOuiObj);
					inifObj.setSrcOui(srcOuiObj);
				}else{
					ForwardingInIfSrc srcMacObj = new ForwardingInIfSrc();
					staticRuleChildLevel_3.add(srcMacObj);
					inifObj.setSrcMac(srcMacObj);
				}
			}
		}
		staticRuleChildLevel_2.clear();
		generateStaticRuleLevel_3(index);
	}
	
	private void generateStaticRuleLevel_3(int index){
		/**
		 * <action>.<in-if>.<src-mac>				ForwardingInIfSrc
		 * <action>.<in-if>.<src-oui>				ForwardingInIfSrc
		 */
		for(Object childObj : staticRuleChildLevel_3){
			
			if(childObj instanceof ForwardingInIfSrc){
				ForwardingInIfSrc srcObj = (ForwardingInIfSrc)childObj;
				
				/** attribute: value */
				srcObj.setName(forwardingEngineImpl.getRuleSrcValue(index));
				
				/** element: <action>.<in-if>.<src-mac>.<dst-mac> */
				ForwardingInIfDst dstMacObj = new ForwardingInIfDst();
				staticRuleChildLevel_4.add(dstMacObj);
				srcObj.setDstMac(dstMacObj);
			}
		}
		staticRuleChildLevel_3.clear();
		generateStaticRuleLevel_4(index);
	}
	
	private void generateStaticRuleLevel_4(int index){
		/**
		 * <action>.<in-if>.<src-mac>.<dst-mac>			ForwardingInIfDst
		 */
		for(Object childObj : staticRuleChildLevel_4){
			if(childObj instanceof ForwardingInIfDst){
				ForwardingInIfDst dstMacObj = (ForwardingInIfDst)childObj;
				
				/** attribute: value */
				dstMacObj.setName(forwardingEngineImpl.getRuleDstMacValue(index));
				
				/** element: <action>.<in-if>.<src-mac>.<dst-mac>.<tx-mac>*/
				ForwardingInIfTxMac txMacObj = new ForwardingInIfTxMac();
				staticRuleChildLevel_5.add(txMacObj);
				dstMacObj.setTxMac(txMacObj);
			}
		}
		staticRuleChildLevel_4.clear();
		generateStaticRuleLevel_5(index);
	}
	
	private void generateStaticRuleLevel_5(int index){
		/**
		 * <action>.<in-if>.<src-mac>.<dst-mac>.<tx-mac>			ForwardingInIfTxMac
		 */
		for(Object childObj : staticRuleChildLevel_5){
			if(childObj instanceof ForwardingInIfTxMac){
				ForwardingInIfTxMac txMacObj = (ForwardingInIfTxMac)childObj;
				
				/** attribute: value */
				txMacObj.setName(forwardingEngineImpl.getRuleTxMacValue(index));
				
				/** element: <action>.<in-if>.<src-mac>.<dst-mac>.<tx-mac>.<out-if> */
				ForwardingOutIf outIfObj = new ForwardingOutIf();
				staticRuleChildLevel_6.add(outIfObj);
				txMacObj.setOutIf(outIfObj);
			}
		}
		staticRuleChildLevel_5.clear();
		generateStaticRuleLevel_6(index);
	}
	
	private void generateStaticRuleLevel_6(int index){
		/**
		 * <action>.<in-if>.<src-mac>.<dst-mac>.<tx-mac>.<out-if>			ForwardingOutIf
		 */
		for(Object childObj : staticRuleChildLevel_6){
			if(childObj instanceof ForwardingOutIf){
				ForwardingOutIf outifObj = (ForwardingOutIf)childObj;
				
				/** attribute: value */
				outifObj.setName(forwardingEngineImpl.getRuleOutIfValue(index));
				
				/** element: <action>.<in-if>.<src-mac>.<dst-mac>.<tx-mac>.<out-if>.<rx-mac> */
				outifObj.setRxMac(CLICommonFunc.createAhName(forwardingEngineImpl.getRuleRxMacValue(index)));
			}
		}
		staticRuleChildLevel_6.clear();
	}
}
