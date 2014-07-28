package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApDynamicRoute;
import com.ah.bo.mobility.HiveProfile;
import com.ah.be.config.create.source.AmrpProfileInt;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.AmrpMetricTypeValue;

/**
 * 
 * @author zhang
 *
 */
public class AmrpProfileImpl implements AmrpProfileInt {
	
	private HiveAp hiveAp;
	private List<HiveApDynamicRoute> dynamicRoutes;
	
	public AmrpProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		dynamicRoutes = hiveAp.getDynamicRoutes();
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}

	public String getUpdateTime(){
		List<Object> amrpObj = new ArrayList<Object>();
		amrpObj.add(hiveAp);
		if(dynamicRoutes != null){
			amrpObj.addAll(dynamicRoutes);
		}
		return CLICommonFunc.getLastUpdateTime(amrpObj);
	}
	
	public int getAmrpNeighborSize(){
		if(dynamicRoutes == null ){
			return 0;
		}else{
			return dynamicRoutes.size();
		}
	}
	
	public AmrpMetricTypeValue getAmrpMetricType(){
		String amrpType = CLICommonFunc.getEnumItemValue(HiveAp.METRIC_TYPE, hiveAp.getMetric());
		if(AmrpMetricTypeValue.AGGRESSIVE.value().equalsIgnoreCase(amrpType)){
			return AmrpMetricTypeValue.AGGRESSIVE;
		}else if(AmrpMetricTypeValue.CONSERVATIVE.value().equalsIgnoreCase(amrpType)){
			return AmrpMetricTypeValue.CONSERVATIVE;
		}else if(AmrpMetricTypeValue.NORMAL.value().equalsIgnoreCase(amrpType)){
			return AmrpMetricTypeValue.NORMAL;
		}else{
			return AmrpMetricTypeValue.NORMAL;
		}
	}
	
	public int getAmrpPollInterval(){
		return hiveAp.getMetricInteval();
	}
	
	public String getAmrpNeighborMac(int index){
		return CLICommonFunc.transFormMacAddrOrOui(dynamicRoutes.get(index).getNeighborMac());
	}
	
	public int getAmrpNeighborMin(int index){
		return dynamicRoutes.get(index).getRouteMinimun();
	}
	
	public int getAmrpNeighborMax(int index){
		return dynamicRoutes.get(index).getRouteMaximun();
	}
	
	public boolean isVpnClient(){
		return this.hiveAp.isVpnClient();
	}
	
	public int getHeartbeatInterval(){
		return this.hiveAp.getConfigTemplate().getVpnService().getAmrpInterval();
	}
	
	public int getHeartbeatRetry(){
		return this.hiveAp.getConfigTemplate().getVpnService().getAmrpRetry();
	}
	
	public int getPriority(InterfaceProfileInt.InterType interType){
		HiveProfile hiveProfile = hiveAp.getConfigTemplate().getHiveProfile();
		if(interType == InterfaceProfileInt.InterType.eth0){
			return hiveProfile.getEth0Priority();
		}else if(interType == InterfaceProfileInt.InterType.eth1){
			return hiveProfile.getEth1Priority();
		}else if(interType == InterfaceProfileInt.InterType.red0){
			return hiveProfile.getRed0Priority();
		}else{
			return hiveProfile.getAgg0Priority();
		}
	}
}
