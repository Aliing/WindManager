package com.ah.be.config.cli.generate.impl;

import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.wlan.RadioProfile;

public class RadioProfileImpl extends AbstractAutoAdaptiveCLIGenerate {
	
	private RadioProfile radioProfile;
	private String radioName;
	
	public RadioProfileImpl(RadioProfile radioProfile){
		this.radioProfile = radioProfile;
	}

	@Override
	public void init() throws CLIGenerateException {
		radioName = radioProfile.getRadioName();
	}
	
	@Override
	public boolean isValid() {
		return hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) > 0 && 
				!radioProfile.isCliDefaultFlag();
	}
	
	@CLIConfig
	public CLIGenResult getChannelWidthAndOffset(){
		if(!isConfigChannelWidth()){
			return null;
		}
		CLIGenResult resObj = new CLIGenResult();
		
		//old format 20/40-above/40-below
		resObj.add(RADIO_PROFILE_CHANNEL_WIDTH_OLD, new Object[]{radioName, radioProfile.getChannelWidth()});
		
		//new format 20/40/80
		if(radioProfile.getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A){
			resObj.add(RADIO_PROFILE_CHANNEL_WIDTH_NEW, new Object[]{radioName, RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40});
			resObj.add(RADIO_PROFILE_PRIMARY_CHANNEL_OFFSET, new Object[]{radioName, 0});
		}else if(radioProfile.getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B){
			resObj.add(RADIO_PROFILE_CHANNEL_WIDTH_NEW, new Object[]{radioName, RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40});
			resObj.add(RADIO_PROFILE_PRIMARY_CHANNEL_OFFSET, new Object[]{radioName, 1});
		}else{
			resObj.add(RADIO_PROFILE_CHANNEL_WIDTH_NEW, new Object[]{radioName, radioProfile.getChannelWidth()});
		}
		
		return resObj;
	}
	
	private boolean isConfigChannelWidth(){
		short radioMode = radioProfile.getRadioMode();
		
		//	bg/a model default all channel-width is 20, no need config
		if (radioMode == RadioProfile.RADIO_PROFILE_MODE_BG || radioMode == RadioProfile.RADIO_PROFILE_MODE_A) {
			return false;
		}else if(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG){
			return hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_40M_FOR_24G);
		}else{
			return true;
		}
	}
}
