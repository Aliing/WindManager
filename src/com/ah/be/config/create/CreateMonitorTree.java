package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.MonitorProfileInt;
import com.ah.xml.be.config.MonitorObj;
import com.ah.xml.be.config.MonitorSession;
import com.ah.xml.be.config.MonitorSessionDestination;
import com.ah.xml.be.config.MonitorSessionEnable;
import com.ah.xml.be.config.MonitorSessionSource;
import com.ah.xml.be.config.MonitorSessionSourceInterface;
import com.ah.xml.be.config.MonitorSessionSourceVlan;

public class CreateMonitorTree {
	
	private MonitorProfileInt monitorImpl;
	
	private MonitorObj monitorObj;
	
	private List<Object> monitorList_1 = new ArrayList<Object>();

	public CreateMonitorTree(MonitorProfileInt monitorImpl){
		this.monitorImpl = monitorImpl;
	}
	
	public void generate() {
		monitorObj = new MonitorObj();
		
		/** element: <monitor>.<session> */
		for(int index=0; index<monitorImpl.getSessionSize(); index++){
			monitorObj.getSession().add(createMonitorSession(index));
		}
	}
	
	public MonitorObj getMonitorObj(){
		return this.monitorObj;
	}
	
	private MonitorSession createMonitorSession(int index){
		MonitorSession sessionObj = new MonitorSession();
		
		/** attribute: name */
		sessionObj.setName(monitorImpl.getSessionName(index));
		
		/** attribute: operation */
		sessionObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <monitor>.<session>.<cr> */
		sessionObj.setCr("");
		
		/** element: <monitor>.<session>.<enable> */
		MonitorSessionEnable monitorObj = new MonitorSessionEnable();
		monitorList_1.add(monitorObj);
		sessionObj.setEnable(monitorObj);
		
		/** element: <monitor>.<session>.<source> */
		MonitorSessionSource sourceObj = new MonitorSessionSource();
		monitorList_1.add(sourceObj);
		sessionObj.setSource(sourceObj);
		
		/** element: <monitor>.<session>.<destination> */
		MonitorSessionDestination destinationObj = new MonitorSessionDestination();
		monitorList_1.add(destinationObj);
		sessionObj.setDestination(destinationObj);
		
		generateMonitorSessionLevel_1(index);
		
		return sessionObj;
	}
	
	private void generateMonitorSessionLevel_1(int index){
		/**
		 * <monitor>.<session>.<destination>				MonitorSessionDestination
		 * <monitor>.<session>.<source>						MonitorSessionSource
		 * <monitor>.<session>.<enable>						MonitorSessionEnable
		 */
		for(Object childObj : monitorList_1){
			
			/** element: <monitor>.<session>.<destination> */
			if(childObj instanceof MonitorSessionDestination){
				MonitorSessionDestination destinationObj = (MonitorSessionDestination)childObj;
				
				/** element: <monitor>.<session>.<destination>.<interface> */
				destinationObj.setInterface(CLICommonFunc.createAhStringActObj(
						monitorImpl.getInfDestinationName(index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <monitor>.<session>.<source> */
			if(childObj instanceof MonitorSessionSource){
				MonitorSessionSource sourceObj = (MonitorSessionSource)childObj;
				
				/** element: <monitor>.<session>.<source>.<vlan> */
				for(int j=0; j<monitorImpl.getSourceVlanSize(index); j++){
					sourceObj.getVlan().add(this.createMonitorSessionSourceVlan(index, j));
				}
				
				/** element: <monitor>.<session>.<source>.<interface> */
				for(int j=0; j<monitorImpl.getSourceInterfaceSize(index); j++){
					sourceObj.getInterface().add(this.createMonitorSessionSourceInterface(index, j));
				}
			}
			
			/** element: <monitor>.<session>.<enable> */
			if(childObj instanceof MonitorSessionEnable){
				MonitorSessionEnable enableObj = (MonitorSessionEnable)childObj;
				
				/** attribute: operation */
				enableObj.setOperation(CLICommonFunc.getAhEnumAct(monitorImpl.isSessionEnable(index)));
			}
		}
		monitorList_1.clear();
	}
	
	private MonitorSessionSourceVlan createMonitorSessionSourceVlan(int i, int j){
		MonitorSessionSourceVlan vlan = new MonitorSessionSourceVlan();
		
		/** attribute: operation */
		vlan.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		vlan.setName(monitorImpl.getSourceVlanName(i, j));
		
		/** attribute: quoteProhibited */
		vlan.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <monitor>.<session>.<source>.<vlan>.<ingress> */
		if(monitorImpl.isSourceVlanIngress(i, j)){
			vlan.setIngress("");
		}
		
		return vlan;
	}
	
	private MonitorSessionSourceInterface createMonitorSessionSourceInterface(int i, int j){
		MonitorSessionSourceInterface inf = new MonitorSessionSourceInterface();
		
		/** attribute: operation */
		inf.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		inf.setName(monitorImpl.getSourceInterfaceName(i, j));
		
		/** attribute: quoteProhibited */
		inf.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <monitor>.<session>.<source>.<interface>.<ingress> */
		if(monitorImpl.isConfigSourceInterfaceIngress(i, j)){
			inf.setIngress("");
		}
		
		/** element: <monitor>.<session>.<source>.<interface>.<both> */
		if(monitorImpl.isConfigSourceInterfaceBoth(i, j)){
			inf.setBoth("");
		}
		
		/** element: <monitor>.<session>.<source>.<interface>.<egress> */
		if(monitorImpl.isConfigSourceInterfaceEgress(i, j)){
			inf.setEgress("");
		}
		
		return inf;
	}
}
