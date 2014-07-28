package com.ah.be.config.cli.generate.impl;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.AhInterface.DeviceInfUnionType;
import com.ah.bo.hiveap.InterfaceMstpSettings;
import com.ah.bo.network.StpSettings;
import com.ah.bo.port.PortBasicProfile;

@CLIConfig
public class InterfaceImpl extends AbstractAutoAdaptiveCLIGenerate {

	@Override
	public void init() throws CLIGenerateException {
		
	}
	
	@CLIConfig
	public CLIGenResult getSpanningTreeMSTInstanceCLIs(){
		if(!isEnableSpanningTree() || hiveAp.getDeviceStpSettings() == null){
			return null;
		}
		if(!hiveAp.getDeviceStpSettings().isOverrideStp()){
			return null;
		}
		if(hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings().getStp_mode() != StpSettings.STP_MODE_MSTP){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		for(InterfaceMstpSettings mstpInf : hiveAp.getDeviceStpSettings().getInterfaceMstpSettings()){
			DeviceInfUnionType type = DeviceInfType.getInstance(mstpInf.getInterfaceNum(), hiveAp.getHiveApModel());
			String protName = type.getCLIName(hiveAp.getHiveApModel());
			if(StringUtils.isEmpty(protName)){
				continue;
			}
			if(type.getDeviceInfType() == DeviceInfType.PortChannel && 
					!isPortChannelExists(mstpInf.getInterfaceNum())){
				continue;
			}
			
			if(mstpInf.getDevicePathCost() > 0){
				cliRes.add(INTERFACE_SPANNING_TREE_MST_INSTANCE_PATH_COST, 
						new Object[]{protName, mstpInf.getInstance(), mstpInf.getDevicePathCost()});
			}
			cliRes.add(INTERFACE_SPANNING_TREE_MST_INSTANCE_PRIORITY, 
					new Object[]{protName, mstpInf.getInstance(), mstpInf.getDevicePriority()});
		}
		return cliRes;
	}
	
	private boolean isEnableSpanningTree() {
		boolean stpEnable = false;
		if(hiveAp.getConfigTemplate().getSwitchSettings() != null &&
				hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings() != null){
			stpEnable = hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp();
		}
		if(!stpEnable){
			return stpEnable;
		}
		
		boolean isOverrideStp = hiveAp.getDeviceStpSettings() != null? hiveAp.getDeviceStpSettings().isOverrideStp() : false;
		if(isOverrideStp){
			return hiveAp.getDeviceStpSettings().isEnableStp();
		}else{
			return stpEnable;
		}
	}
	
	private boolean isPortChannelExists(short finalValue){
		if (this.hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null) {
			for (PortBasicProfile port : hiveAp.getPortGroup().getBasicProfiles()) {
				if(port.isEnabledlinkAggregation()){
					short portIndex = port.getPortChannel();
					if(finalValue == DeviceInfType.PortChannel.getFinalValue(portIndex, hiveAp.getHiveApModel())){
						return true;
					}
				}
			}
		}
		return false;
	}

}
