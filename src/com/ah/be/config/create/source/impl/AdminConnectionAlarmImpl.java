package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.AdminConnectionAlarmInt;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;

public class AdminConnectionAlarmImpl implements AdminConnectionAlarmInt {
	
	//private HiveAp hiveAp;
	
	private ConfigTemplate configTemplate;
	
	public AdminConnectionAlarmImpl(HiveAp hiveAp) {
		//this.hiveAp = hiveAp;
		this.configTemplate = hiveAp.getConfigTemplate();
	}

	public boolean isEnableConnectionAlarm() {
		return configTemplate.isEnableConnectionAlarm();
	}

	public int getTxRetryThreshold() {
		return configTemplate.getTxRetryThreshold();
	}

	public int getTxRetryInterval() {
		return configTemplate.getTxRetryInterval();
	}

	public int getTxFrameErrorThreshold() {
		return configTemplate.getTxFrameErrorThreshold();
	}

	public int getTxFrameErrorInterval() {
		return configTemplate.getTxFrameErrorInterval();
	}

	public int getProbRequestThreshold() {
		return configTemplate.getProbRequestThreshold();
	}

	public int getProbRequestInterval() {
		return configTemplate.getProbRequestInterval();
	}

	public int getEgressMulticastThreshold() {
		return configTemplate.getEgressMulticastThreshold();
	}

	public int getEgressMulticastInterval() {
		return configTemplate.getEgressMulticastInterval();
	}

	public int getIngressMulticastThreshold() {
		return configTemplate.getIngressMulticastThreshold();
	}

	public int getIngressMulticastInterval() {
		return configTemplate.getIngressMulticastInterval();
	}

	public int getChannelUtilizationThreshold() {
		return configTemplate.getChannelUtilizationThreshold();
	}
	
	public int getChannelUtilizationInterval() {
		return configTemplate.getChannelUtilizationInterval();
	}

}
