package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.SpanningTreeInt;
import com.ah.bo.hiveap.DeviceMstpInstancePriority;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.MstpRegionPriority;
import com.ah.bo.network.StpSettings;
import com.ah.xml.be.config.SpanningTreeModeValue;

public class SpanningTreeImpl implements SpanningTreeInt {
	
	private HiveAp hiveAp;
	
	private StpSettings stpSettings;
	private DeviceStpSettings deviceStpSettings;
	
	private List<MstpRegionPriority> mstpRegionList;
	
	public SpanningTreeImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		init();
	}
	
	private void init(){
		if(hiveAp.getDeviceStpSettings() != null && 
				hiveAp.getDeviceStpSettings().isOverrideStp()){
			deviceStpSettings = hiveAp.getDeviceStpSettings();
		}
		
		if(hiveAp.getConfigTemplate().getSwitchSettings() != null){
			stpSettings = hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings();
		}
		
		mstpRegionList = new ArrayList<MstpRegionPriority>();
		
		if(stpSettings != null && stpSettings.getMstpRegion() != null 
				&& stpSettings.getMstpRegion().getMstpRegionPriorityList() != null){
			for(MstpRegionPriority priorityObj : stpSettings.getMstpRegion().getMstpRegionPriorityList()){
				if(priorityObj.getVlan() != null){
					priorityObj.setVlanList(CLICommonFunc.mergeRangeList(priorityObj.getVlan()));
				}
				mstpRegionList.add(priorityObj);
			}
		}
		//override MSTP instance and priority from device page to network policy
		if(deviceStpSettings != null && deviceStpSettings.getInstancePriority() != null){
			for(DeviceMstpInstancePriority ovMstpPriority : deviceStpSettings.getInstancePriority()){
				short ovInstance = ovMstpPriority.getInstance();
				int ovPriority = ovMstpPriority.getPriority();
				for(MstpRegionPriority mstpRegion : mstpRegionList){
					if(ovInstance == mstpRegion.getInstance()){
						mstpRegion.setPriority(ovPriority);
					}
				}
			}
			
		}
	}
	

	public boolean isConfigSpanningTree() {
		return stpSettings != null || deviceStpSettings != null;
	}
	
	public boolean isEnableDeviceSpanningTree(){
		return hiveAp.getDeviceStpSettings() != null && 
				hiveAp.getDeviceStpSettings().isOverrideStp() && 
				hiveAp.getDeviceStpSettings().isEnableStp();
	}

	public boolean isEnableSpanningTree() {
		if (deviceStpSettings != null && deviceStpSettings.isOverrideStp()
				&& stpSettings != null && stpSettings.isEnableStp()) {
			return deviceStpSettings.isEnableStp();
		}else{
			if(stpSettings != null){
				return stpSettings.isEnableStp();
			}
		}
		return false;
	}

	public SpanningTreeModeValue getSpanningMode() {
		int modeType = stpSettings.getStp_mode();
		switch(modeType){
		case StpSettings.STP_MODE_STP:
			return SpanningTreeModeValue.STP;
		case StpSettings.STP_MODE_RSTP: 
			return SpanningTreeModeValue.RSTP;
		case StpSettings.STP_MODE_MSTP: 
			return SpanningTreeModeValue.MSTP;
		default:
			return null;
		}
	}
	
	public boolean isModeMstp(){
		return getSpanningMode() == SpanningTreeModeValue.MSTP;
	}

	public int getForwardTime() {
		return deviceStpSettings.getForwardTime();
	}
	
	public boolean isConfigHelloTime(){
		return stpSettings.getStp_mode() == StpSettings.STP_MODE_STP;
	}

	public int getHelloTime() {
		return deviceStpSettings.getHelloTime();
	}

	public int getMaxAge() {
		return deviceStpSettings.getMaxAge();
	}
	
	public boolean isConfigMaxHops(){
		return stpSettings.getMstpRegion() != null;
	}

	public int getMaxHops() {
		return stpSettings.getMstpRegion().getHops();
	}

	public int getPriority() {
		return deviceStpSettings.getPriority();
	}

	public boolean isConfigRegion(){
		return stpSettings.getMstpRegion() != null;
	}
	
	public String getRegionValue() {
		return stpSettings.getMstpRegion().getRegionName();
	}
	
	public boolean isConfigRevision(){
		return stpSettings.getMstpRegion() != null;
	}

	public int getRevision() {
		return stpSettings.getMstpRegion().getRevision();
	}
	
	public boolean isConfigForceVersion(){
		return deviceStpSettings.getForceVersion() != DeviceStpSettings.DEFAULT_FORCE_VERSION;
	}

	public int getForceVersion() {
		return deviceStpSettings.getForceVersion();
	}

	public int getMstInstanceSize() {
		if(stpSettings.getMstpRegion() == null || stpSettings.getMstpRegion().getMstpRegionPriorityList() == null){
			return 0;
		}else{
			return stpSettings.getMstpRegion().getMstpRegionPriorityList().size();
		}
	}

	public String getMstInstanceName(int index) {
		int insName = mstpRegionList.get(index).getInstance();
		return String.valueOf(insName);
	}

	public int getInstancePriority(int index) {
		return mstpRegionList.get(index).getPriority();
	}

	public int getInstanceVlanSize(int index) {
		if(mstpRegionList.get(index).getVlanList() == null){
			return 0;
		}else{
			return mstpRegionList.get(index).getVlanList().size();
		}
	}

	public String getInstanceVlanName(int index, int i) {
		String nameStr = mstpRegionList.get(index).getVlanList().get(i);
		nameStr = nameStr.replace(" ", "").replace("-", " - ");
		return nameStr;
	}

}
