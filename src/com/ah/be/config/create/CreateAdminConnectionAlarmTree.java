package com.ah.be.config.create;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AdminConnectionAlarmInt;
import com.ah.xml.be.config.AdminConnectionAlarmingObj;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.ConnectionAlarmingIngressMulticast;
import com.ah.xml.be.config.ConnectionAlarmingIngressMulticastThreshold;
import com.ah.xml.be.config.ConnectionAlarmingProbRequest;
import com.ah.xml.be.config.ConnectionAlarmingProbRequestThreshold;
import com.ah.xml.be.config.ConnectionAlarmingTxFrameError;
import com.ah.xml.be.config.ConnectionAlarmingTxFrameErrorThreshold;
import com.ah.xml.be.config.ConnectionAlarmingTxRetry;
import com.ah.xml.be.config.ConnectionAlarmingTxRetryThreshold;

public class CreateAdminConnectionAlarmTree {
	
	private AdminConnectionAlarmInt connectionAlarm;
	private AdminConnectionAlarmingObj adminConnectionAlarm;
	private GenerateXMLDebug oDebug;
	private List<Object> ssidChildList_1 = new ArrayList<Object>();
	private List<Object> ssidChildList_2 = new ArrayList<Object>();
	//private List<Object> ssidChildList_3 = new ArrayList<Object>();
	//private List<Object> ssidChildList_4 = new ArrayList<Object>();

	public CreateAdminConnectionAlarmTree(AdminConnectionAlarmInt connectionAlarm, GenerateXMLDebug oDebug) throws Exception {
		this.connectionAlarm = connectionAlarm;
		this.oDebug = oDebug;
	}
		
	public void generate() throws Exception{
		if(this.connectionAlarm != null){
			adminConnectionAlarm = new AdminConnectionAlarmingObj();
			generateConnectionAlarmLevel_1();
		}
	}	
	
	public AdminConnectionAlarmingObj getAdminConnectionAlarmObj() {
		return adminConnectionAlarm;
	}
	
	private void generateConnectionAlarmLevel_1(){
		if (connectionAlarm.isEnableConnectionAlarm()) {
			adminConnectionAlarm.setEnable(CLICommonFunc.getAhOnlyAct(true));
			ConnectionAlarmingTxRetry txRetry = new ConnectionAlarmingTxRetry();
		    ConnectionAlarmingTxFrameError txFrameError = new ConnectionAlarmingTxFrameError();  
		    ConnectionAlarmingProbRequest probRequest = new ConnectionAlarmingProbRequest();
		    ConnectionAlarmingIngressMulticast ingressMulticast = new ConnectionAlarmingIngressMulticast();
		    adminConnectionAlarm.setTxRetry(txRetry);
		    adminConnectionAlarm.setTxFrameError(txFrameError);
		    adminConnectionAlarm.setProbRequest(probRequest);
		    adminConnectionAlarm.setIngressMulticast(ingressMulticast);
		    adminConnectionAlarm.setEnable(CLICommonFunc.getAhOnlyAct(true));
			ssidChildList_1.add(txRetry);
			ssidChildList_1.add(txFrameError);
			ssidChildList_1.add(probRequest);
			ssidChildList_1.add(ingressMulticast);
			
		} else {
			adminConnectionAlarm.setEnable(CLICommonFunc.getAhOnlyAct(false));
		}
		generateConnectionAlarmLevel_2();
	}
	
	private void generateConnectionAlarmLevel_2(){
		if(ssidChildList_1.isEmpty()){
			return;
		}
		for(Object childObj : ssidChildList_1){
			if(childObj instanceof ConnectionAlarmingTxRetry){
				ConnectionAlarmingTxRetry txRetry = (ConnectionAlarmingTxRetry)childObj;
				ConnectionAlarmingTxRetryThreshold txRetryThreshold = new ConnectionAlarmingTxRetryThreshold();
				txRetry.setThreshold(txRetryThreshold);
				ssidChildList_2.add(txRetryThreshold);
			}
			if(childObj instanceof ConnectionAlarmingTxFrameError){
				ConnectionAlarmingTxFrameError txFrameError = (ConnectionAlarmingTxFrameError)childObj;
				ConnectionAlarmingTxFrameErrorThreshold txFrameErrorThreshold = new ConnectionAlarmingTxFrameErrorThreshold();
				txFrameError.setThreshold(txFrameErrorThreshold);
				ssidChildList_2.add(txFrameErrorThreshold);
			}
			if(childObj instanceof ConnectionAlarmingProbRequest){
				ConnectionAlarmingProbRequest probRequest = (ConnectionAlarmingProbRequest)childObj;
				ConnectionAlarmingProbRequestThreshold probRequestThreshold = new ConnectionAlarmingProbRequestThreshold();
				probRequest.setThreshold(probRequestThreshold);
				ssidChildList_2.add(probRequestThreshold);
			}
			if(childObj instanceof ConnectionAlarmingIngressMulticast){
				ConnectionAlarmingIngressMulticast ingressMulticast = (ConnectionAlarmingIngressMulticast)childObj;
				ConnectionAlarmingIngressMulticastThreshold ingressMulticastThreshold = new ConnectionAlarmingIngressMulticastThreshold();
				ingressMulticast.setThreshold(ingressMulticastThreshold);
				ssidChildList_2.add(ingressMulticastThreshold);
			}
		}
		ssidChildList_1.clear();
		generateConnectionAlarmLevel_3();
	}
	
	private void generateConnectionAlarmLevel_3(){
		if(ssidChildList_2.isEmpty()){
			return;
		}
		for(Object childObj : ssidChildList_2){
			if(childObj instanceof ConnectionAlarmingTxRetryThreshold){
				ConnectionAlarmingTxRetryThreshold txRetry = (ConnectionAlarmingTxRetryThreshold)childObj;
				txRetry.setOperation(AhEnumAct.YES);
				txRetry.setValue(connectionAlarm.getTxRetryThreshold());
				//txRetry.setTimeInterval(connectionAlarm.getTxRetryInterval());
			}
			if(childObj instanceof ConnectionAlarmingTxFrameErrorThreshold){
				ConnectionAlarmingTxFrameErrorThreshold txFrameError = (ConnectionAlarmingTxFrameErrorThreshold)childObj;
				txFrameError.setOperation(AhEnumAct.YES);
				txFrameError.setValue(connectionAlarm.getTxFrameErrorThreshold());
				//txFrameError.setTimeInterval(connectionAlarm.getTxFrameErrorInterval());
			}
			if(childObj instanceof ConnectionAlarmingProbRequestThreshold){
				ConnectionAlarmingProbRequestThreshold probRequest = (ConnectionAlarmingProbRequestThreshold)childObj;
				probRequest.setOperation(AhEnumAct.YES);
				probRequest.setValue(connectionAlarm.getProbRequestThreshold());
				//probRequest.setTimeInterval(connectionAlarm.getProbRequestInterval());
			}
			if(childObj instanceof ConnectionAlarmingIngressMulticastThreshold){
				ConnectionAlarmingIngressMulticastThreshold ingressMulticast = (ConnectionAlarmingIngressMulticastThreshold)childObj;
				ingressMulticast.setOperation(AhEnumAct.YES);
				ingressMulticast.setValue(connectionAlarm.getIngressMulticastThreshold());
				//ingressMulticast.setTimeInterval(connectionAlarm.getIngressMulticastInterval());
			}
		}
		ssidChildList_2.clear();
		//generateConnectionAlarmLevel_4();
	}
	 
		 
}
