package com.ah.be.config.create;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.PortChannelInt;
import com.ah.xml.be.config.PortChannelLoadBalance;
import com.ah.xml.be.config.PortChannelLoadBalanceMode;
import com.ah.xml.be.config.PortChannelObj;

public class CreatePortChannelTree {
	
	private PortChannelInt portChannelImpl;
	
	private PortChannelObj portChannelObj;
	
	private List<Object> channelChildList_1 = new ArrayList<Object>();
	private List<Object> channelChildList_2 = new ArrayList<Object>();
	
	public CreatePortChannelTree(PortChannelInt portChannelImpl) throws Exception {
		this.portChannelImpl = portChannelImpl;
	}
		
	public void generate() throws Exception{
		if(portChannelImpl.isConfigPortChannel()){
			
			/** element: <port-channel> */
			portChannelObj = new PortChannelObj();
			
			generatePortChannelLevel_1();
		}
	}	
	
	public PortChannelObj getPortChannelObj() {
		return portChannelObj;
	}
	
	private void generatePortChannelLevel_1(){
		
		/** element: <port-channel>.<cr> */
		for(int index=0; index<portChannelImpl.getPortChannelSize(); index++){
			portChannelObj.getCr().add(CLICommonFunc.createAhNameActValue(
					portChannelImpl.getPortChannelName(index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <port-channel>.<loadBalance> */
		PortChannelLoadBalance loadBalance = new PortChannelLoadBalance();
		channelChildList_1.add(loadBalance);
		portChannelObj.setLoadBalance(loadBalance);

		generatePortChannelLevel_2();
	}
	
	private void generatePortChannelLevel_2(){
		/**
		 * <port-channel>.<loadBalance>					PortChannelLoadBalance
		 */
		
		for(Object childObj : channelChildList_1){
			
			/** element: <port-channel>.<loadBalance> */
			if(childObj instanceof PortChannelLoadBalance){
				PortChannelLoadBalance loadBalance = (PortChannelLoadBalance)childObj;
				
				/** element: <port-channel>.<loadBalance>.<mode> */
				PortChannelLoadBalanceMode modeObj = new PortChannelLoadBalanceMode();
				channelChildList_2.add(modeObj);
				loadBalance.setMode(modeObj);
			}
		}
		channelChildList_1.clear();
		generatePortChannelLevel_3();
	}
	
	private void generatePortChannelLevel_3(){
		/**
		 * <port-channel>.<loadBalance>.<mode>					PortChannelLoadBalanceMode
		 */
		for(Object childObj : channelChildList_2){
			
			/** element: <port-channel>.<loadBalance>.<mode> */
			if(childObj instanceof PortChannelLoadBalanceMode){
				PortChannelLoadBalanceMode modeObj = (PortChannelLoadBalanceMode)childObj;
				
				/** attribute: operation */
				modeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				modeObj.setValue(portChannelImpl.getChannelModeValue());
				
			}
		}
		channelChildList_2.clear();
	}
	
}
