package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.RouteProfileInt;
import com.ah.xml.be.config.RouteObj;

/**
 * 
 * @author zhang
 *
 */
public class CreateRouteTree {
	
	private RouteProfileInt routeImpl;
	private RouteObj routeObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> routeChildList_1 = new ArrayList<Object>();

	public CreateRouteTree(RouteProfileInt routeImpl, GenerateXMLDebug oDebug) {
		this.routeImpl = routeImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(routeImpl.isConfigureRoute() ){
			routeObj = new RouteObj();
			generateRouteLevel_1();
		}
	}
	
	public RouteObj getRouteObj(){
		return this.routeObj;
	}
	
	private void generateRouteLevel_1() throws Exception {
		/**
		 * <route>				RouteObj
		 */
		
		/** attribute: updateTime */
		routeObj.setUpdateTime(routeImpl.getUpdateTime());
		
		/** attribute: operation */
		routeObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"route", GenerateXMLDebug.SET_NAME,
				routeImpl.getHiveApGuiName(), routeImpl.getHiveApName());
		routeObj.setName(routeImpl.getRouteDestinationMAC());
		
		/** element: <route><outgoing-interface> */
		RouteObj.OutgoingInterface outgoingInterfaceObj = new RouteObj.OutgoingInterface();
		routeChildList_1.add(outgoingInterfaceObj);
		routeObj.setOutgoingInterface(outgoingInterfaceObj);
		
		generateRouteLevel_2();
	}
	
	private void generateRouteLevel_2() throws Exception {
		/**
		 * <route><outgoing-interface>			RouteObj.OutgoingInterface
		 */
		for(Object childObj : routeChildList_1){
			
			/** element: <route><outgoing-interface> */
			if(childObj instanceof RouteObj.OutgoingInterface){
				RouteObj.OutgoingInterface outgoingInterfaceObj = (RouteObj.OutgoingInterface)childObj;
				
				/** attribute value */
				outgoingInterfaceObj.setValue(routeImpl.getRouteInterfaceName());
				
				/** element: <route><outgoing-interface>.<next-hop> */
				oDebug.debug("/configuration/route/outgoing-interface", 
						"next-hop", GenerateXMLDebug.SET_VALUE,
						routeImpl.getHiveApGuiName(), routeImpl.getHiveApName());
				Object[][] nextHopParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, routeImpl.getRouteNextHopMac()}
				};
				outgoingInterfaceObj.setNextHop(
						(RouteObj.OutgoingInterface.NextHop)CLICommonFunc.createObjectWithName(RouteObj.OutgoingInterface.NextHop.class, nextHopParm)
				);
			}
		}
	}
}
