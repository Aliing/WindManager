package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.PortChannelInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.xml.be.config.PortChannelLoadBalanceModeValue;
 

public class PortChannelImpl implements PortChannelInt {
	
//	private HiveAp hiveAp;
	private PortGroupProfile portGroup;
	
	private List<Short> channelList = new ArrayList<Short>();
	
	public PortChannelImpl(HiveAp hiveAp){
		this.portGroup = hiveAp.getPortGroup();
		init();
	}
	
	private void init(){
		if(portGroup == null || portGroup.getBasicProfiles() == null){
			return;
		}
		
		for(PortBasicProfile basePort : portGroup.getBasicProfiles()){
			if(basePort.isEnabledlinkAggregation()){
				channelList.add(basePort.getPortChannel());
			}
		}
	}

	public boolean isConfigPortChannel() {
		return !channelList.isEmpty();
	}

	public int getPortChannelSize() {
		return channelList.size();
	}

	public String getPortChannelName(int index) {
		return channelList.get(index).toString();
	}

	public PortChannelLoadBalanceModeValue getChannelModeValue() {
		short modeShort = portGroup.getLoadBalanceMode();
		switch(modeShort){
		//see from switch in device auto has been remove.
//		case PortGroupProfile.LOADBLANCE_MODE_AUTO:
//			return PortChannelLoadBalanceModeValue.AUTO;
		case PortGroupProfile.LOADBLANCE_MODE_SRC_DST_MAC:
			return PortChannelLoadBalanceModeValue.SRC_DST_MAC;
		case PortGroupProfile.LOADBLANCE_MODE_SRC_DET_IP: 
			return PortChannelLoadBalanceModeValue.SRC_DST_IP;
		case PortGroupProfile.LOADBLANCE_MODE_SRC_DET_IP_PORT: 
			return PortChannelLoadBalanceModeValue.SRC_DST_IP_PORT;
		case PortGroupProfile.LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT: 
			return PortChannelLoadBalanceModeValue.SRC_DST_MAC_IP_PORT;
		default:
			return PortChannelLoadBalanceModeValue.SRC_DST_MAC_IP_PORT;
		}
	}
	
}
