package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.IpProfileInt;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.AhNameActValueQuoteProhibited;
import com.ah.xml.be.config.IpIgmp;
import com.ah.xml.be.config.IpIgmpSnooping;
import com.ah.xml.be.config.IpIgmpSnoopingVlan;
import com.ah.xml.be.config.IpObj;
import com.ah.xml.be.config.IpPathMtuDiscovery;
import com.ah.xml.be.config.IpTcpMssThreshold;
import com.ah.xml.be.config.TcpMssThresholdSize;

/**
 * @author zhang
 * @version 2007-12-14 10:51:18 AM
 */

public class CreateIpTree {

	private IpProfileInt ipProfileImpl;
	private IpObj ipObj;
	
	private GenerateXMLDebug oDebug;

	private List<Object> ipChildList_1 = new ArrayList<Object>();
	private List<Object> ipChildList_2 = new ArrayList<Object>();

	public CreateIpTree(IpProfileInt ipProfileImpl, GenerateXMLDebug oDebug) {
		this.ipProfileImpl = ipProfileImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		ipObj = new IpObj();
		generateChildLevel_1();
	}

	public IpObj getIpObj() {
		return this.ipObj;
	}

	private void generateChildLevel_1() throws Exception {
		/**
		 * <ip> IpObj
		 */

		/** element: <ip>.<route> */
		IpObj.Route routeObj = new IpObj.Route();
		ipChildList_1.add(routeObj);
		ipObj.setRoute(routeObj);
		
		
		if(ipProfileImpl.isConfigPathAndTcpMss()){
			/** element: <ip>.<path-mtu-discovery> */
			IpPathMtuDiscovery pMtuObj=new IpPathMtuDiscovery();
			ipChildList_1.add(pMtuObj);
			ipObj.setPathMtuDiscovery(pMtuObj);
			
			/** element: <ip>.<tcp-mss-threshold> */
			IpTcpMssThreshold tcpMssObj=new IpTcpMssThreshold();
			ipChildList_1.add(tcpMssObj);
			ipObj.setTcpMssThreshold(tcpMssObj);
		}
		
		IpIgmp igmp = new IpIgmp();
		ipObj.setIgmp(igmp);
		ipChildList_1.add(igmp);
		
		generateChildLevel_2();
	}

	private void generateChildLevel_2()throws Exception {
		/**
		 * <ip>.<route> IpObj.Route
		 * <ip>.<path-mtu-discovery>
		 * <ip>.<tcp-mss-threshold>
		 */

		for (Object childObj : ipChildList_1) {

			/** element: <ip>.<route> */
			if (childObj instanceof IpObj.Route) {
				IpObj.Route routeObj = (IpObj.Route) childObj;

				/** element: <ip>.<route>.<net> */
				for(int index=0; index<ipProfileImpl.getIpNetSize(); index++){
					oDebug.debug("/configuration/ip/route", 
							"net", GenerateXMLDebug.SET_NAME,
							ipProfileImpl.getHiveApGuiName(), ipProfileImpl.getHiveApName());
					routeObj.getNet().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(
									ipProfileImpl.getIpNetName(index), 
									CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <ip>.<route>.<host> */
				for(int index=0; index<ipProfileImpl.getIpHostSize(); index++){
					oDebug.debug("/configuration/ip/route", 
							"host", GenerateXMLDebug.SET_NAME,
							ipProfileImpl.getHiveApGuiName(), ipProfileImpl.getHiveApName());
					routeObj.getHost().add(CLICommonFunc.createAhNameActValueQuoteProhibited(
							ipProfileImpl.getIpHostName(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <ip>.<path-mtu-discovery> */
			if(childObj instanceof IpPathMtuDiscovery){
				IpPathMtuDiscovery pMtuObj=(IpPathMtuDiscovery)childObj;
				/** element: <ip>.<path-mtu-discovery><enable> */
				pMtuObj.setEnable(CLICommonFunc.getAhOnlyAct(ipProfileImpl.isIpPathMtuDiscoveryEnable()));
			}
			
			/** element: <ip>.<tcp-mss-threshold> */
			if(childObj instanceof IpTcpMssThreshold){
				IpTcpMssThreshold tcpMssObj=(IpTcpMssThreshold)childObj;
				/** element: <ip>.<tcp-mss-threshold><enable> */
				tcpMssObj.setEnable(CLICommonFunc.getAhOnlyAct(ipProfileImpl.isIpTcpMssThresholdEnable()));
				
				if(ipProfileImpl.isIpTcpMssThresholdEnable()){
					
					if(ipProfileImpl.isConfigThresholdSize()){
						/** element: <ip>.<tcp-mss-threshold><threshold-size> */
						oDebug.debug("/configuration/ip/tcp-mss-threshold", 
								"threshold-size", GenerateXMLDebug.SET_NAME,
								ipProfileImpl.getHiveApGuiName(), ipProfileImpl.getHiveApName());
						Object[][] thresholdParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, ipProfileImpl.getThresholdSize()},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
							};
						tcpMssObj.setThresholdSize(
								(TcpMssThresholdSize)CLICommonFunc.createObjectWithName(TcpMssThresholdSize.class, thresholdParm)
							);
					}
					
					if(ipProfileImpl.isConfigL3VpnThresholdSize()){
						/** element: <ip>.<tcp-mss-threshold><l3-vpn-threshold-size> */
						oDebug.debug("/configuration/ip/tcp-mss-threshold", 
								"l3-vpn-threshold-size", GenerateXMLDebug.SET_NAME,
								ipProfileImpl.getHiveApGuiName(), ipProfileImpl.getHiveApName());
						Object[][] l3VpnParm = {
									{CLICommonFunc.ATTRIBUTE_VALUE, ipProfileImpl.getL3VpnThresholdSize()},
									{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
							};
						tcpMssObj.setL3VpnThresholdSize(
								(TcpMssThresholdSize)CLICommonFunc.createObjectWithName(TcpMssThresholdSize.class, l3VpnParm)
							);
					}
				}
				
			}
			
			/** element: <ip>.<igmp> */
			if(childObj instanceof IpIgmp){
				/** element: <ip>.<igmp><snooping> */
				IpIgmp igmp = (IpIgmp)childObj;
				IpIgmpSnooping snooping = new IpIgmpSnooping();
				ipChildList_2.add(snooping);
				igmp.setSnooping(snooping);
				
			}
		}
		ipChildList_1.clear();
		generateChildLevel_3();
	}
	
	private void generateChildLevel_3()throws Exception {
		for (Object childObj : ipChildList_2) {
			if (childObj instanceof IpIgmpSnooping) {
				IpIgmpSnooping snooping = (IpIgmpSnooping) childObj;
				if (ipProfileImpl.isConfigIGMP()) {
					snooping.setCr(CLICommonFunc.getAhOnlyAct(true));
					snooping.setReportSuppression(CLICommonFunc.getAhOnlyAct(ipProfileImpl.isEnableReportSuppression()));
					snooping.setImmediateLeave(CLICommonFunc.getAhOnlyAct(ipProfileImpl.isEnableImmediateLeave()));
					snooping.setLastMemberQueryInterval(CLICommonFunc.createAhIntActObj(ipProfileImpl.getGlobalDelayLeaveQueryInterval(), true));
					snooping.setLastMemberQueryCount(CLICommonFunc.createAhIntActObj(ipProfileImpl.getGlobalDelayLeaveQueryCount(), true));
					snooping.setRobustnessVariable(CLICommonFunc.createAhIntActObj(ipProfileImpl.getGlobalRobustnessCount(), true));
					snooping.setRouterAgingTime(CLICommonFunc.createAhIntActObj(ipProfileImpl.getGlobalRouterPortAginTime(), true));
					for (int i = 0; i < ipProfileImpl.getIgmpPolicySize(); i++) {
						//snooping.getVlan().add(createIpIgmpSnoopingVlan(i));
						IpIgmpSnoopingVlan vlan = createIpIgmpSnoopingVlan(i);
						if (vlan != null) {
							snooping.getVlan().add(vlan);
						}
					}
				    for (int i = 0; i < ipProfileImpl.getIgmpMulticastGroupSize(); i++) {
				    	snooping.getStatic().add(createIpIgmpMulticastGroup(i));
				    }
				} else {
					snooping.setCr(CLICommonFunc.getAhOnlyAct(false));
				}
			}
		}
		ipChildList_2.clear();
	}
	
	private AhNameActValueQuoteProhibited createIpIgmpMulticastGroup(int index) {
		return CLICommonFunc.createAhNameActValueQuoteProhibited(ipProfileImpl.getMulticastGroupValue(index), true, true);
	}
	
	private IpIgmpSnoopingVlan createIpIgmpSnoopingVlan(int index) {
		IpIgmpSnoopingVlan vlan = new IpIgmpSnoopingVlan();
		String vlanId = String.valueOf(ipProfileImpl.getIgmpPolicyVlanId(index));
		if (ipProfileImpl.getIgmpPolicyEnableSnooping(index)) {
			/** element: <ip>.<igmp>.<snooping>.<vlan>.<cr> */
			vlan.setCr(vlanId);
			vlan.setName(vlanId);
        	vlan.setOperation(AhEnumActValue.YES_WITH_VALUE);
        	vlan.setImmediateLeave(CLICommonFunc.getAhOnlyAct(ipProfileImpl.getIgmpPolicyEnableImmediateLeave(index)));
    		vlan.setLastMemberQueryCount(CLICommonFunc.createAhIntActObj(ipProfileImpl.getIgmpPolicyDelayLeaveQueryCount(index), true));
    		vlan.setLastMemberQueryInterval(CLICommonFunc.createAhIntActObj(ipProfileImpl.getIgmpPolicyDelayLeaveQueryInterval(index), true));
    		vlan.setRobustnessVariable(CLICommonFunc.createAhIntActObj(ipProfileImpl.getIgmpPolicyRobustnessCount(index), true));
    		vlan.setRouterAgingTime(CLICommonFunc.createAhIntActObj(ipProfileImpl.getIgmpPolicyRouterPortAginTime(index), true));
		} else {
			vlan.setName(vlanId);
        	vlan.setOperation(AhEnumActValue.NO_WITH_VALUE);
        }
		return vlan;
	}
	
}