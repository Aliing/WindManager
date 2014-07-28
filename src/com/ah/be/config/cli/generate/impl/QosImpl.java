package com.ah.be.config.cli.generate.impl;

import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.network.NetworkService;

public class QosImpl extends AbstractAutoAdaptiveCLIGenerate {
	
	private QosClassification qosClassMap;

	@Override
	public void init() throws CLIGenerateException {
		qosClassMap = hiveAp.getConfigTemplate().getClassifierMap();
	}
	
	@CLIConfig
	public CLIGenResult getClassifierMapService(){
		if(!isQosEnable() ||
				qosClassMap == null || !qosClassMap.getNetworkServicesEnabled() || 
				qosClassMap.getNetworkServices() == null || 
				qosClassMap.getNetworkServices().isEmpty() ){
			return null;
		}
		
		CLIGenResult resultObj = new CLIGenResult();
		String serviceName=null;
		for(QosNetworkService qosService : qosClassMap.getNetworkServices().values()){
			NetworkService serviceObj = qosService.getNetworkService();
			if(serviceObj == null){
				continue;
			}
			
			//service name
			if(serviceObj.isCliDefaultFlag()){
				serviceName = serviceObj.getServiceName().toLowerCase();
			}else{
				serviceName = serviceObj.getServiceName();
			}
			
			resultObj.add(QOS_CLASSIFIER_MAP_SERVICE, 
					new Object[]{serviceName, qosService.getQosClass(), qosService.getFilterAction()});
			
			if(qosService.isEnableLogging()){
				resultObj.add(QOS_CLASSIFIER_MAP_SERVICE_LOG, 
						new Object[]{serviceName, qosService.getQosClass()});
			}
		}
		return resultObj;
	}
	
	private boolean isQosEnable(){
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			return hiveAp.isEnableSwitchQosSettings();
		}else{
			return true;
		}
	}

}
