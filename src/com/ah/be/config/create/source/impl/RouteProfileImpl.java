package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApStaticRoute;

import com.ah.be.config.create.source.RouteProfileInt;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class RouteProfileImpl implements RouteProfileInt {
	
	private HiveApStaticRoute staticRoutes ;
	private HiveAp hiveAp;
	
	public RouteProfileImpl(HiveAp hiveAp, HiveApStaticRoute staticRoutes){
		this.staticRoutes = staticRoutes;
		this.hiveAp = hiveAp;
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
	
	public boolean isConfigureRoute(){
		return staticRoutes != null;
	}
	
	public String getUpdateTime(){
		List<Object> routeList = new ArrayList<Object>();
		routeList.add(staticRoutes);
		return CLICommonFunc.getLastUpdateTime(routeList);
	}
	
	public String getRouteDestinationMAC(){
		return CLICommonFunc.transFormMacAddrOrOui(staticRoutes.getDestinationMac());
	}
	
	public String getRouteInterfaceName(){
		String rest=null;
		switch(staticRoutes.getInterfaceType()){
			case HiveApStaticRoute.STATIC_ROUTE_IF_ETH:
				rest = "eth0";
				break;
			case HiveApStaticRoute.STATIC_ROUTE_IF_ETH1:
				rest = "eth1";
				break;
			case HiveApStaticRoute.STATIC_ROUTE_IF_WIFI0:
				rest = "wifi0.1";
				break;
			case HiveApStaticRoute.STATIC_ROUTE_IF_WIFI1:
				rest = "wifi1.1";
				break;
			case HiveApStaticRoute.STATIC_ROUTE_IF_RED0:
				rest = "red0";
				break;
			case HiveApStaticRoute.STATIC_ROUTE_IF_AGG0:
				rest = "agg0";
				break;
		}
		return rest;
//		return CLICommonFunc.getEnumItemValue(HiveApStaticRoute.STATIC_ROUTE_IF_TYPE, staticRoutes.getInterfaceType()).toLowerCase();
	}
	
	public String getRouteNextHopMac(){
		return CLICommonFunc.transFormMacAddrOrOui(staticRoutes.getNextHopMac());
	}
}
