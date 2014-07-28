package com.ah.be.config.create.source.impl.baseImpl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.IpProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApIpRoute;

public class IpProfileBaseImpl implements IpProfileInt {
	
	protected List<String> ipNetList = new ArrayList<String>();
	protected List<String> ipHostList = new ArrayList<String>();
	
	public IpProfileBaseImpl(HiveAp hiveAp){
		if(hiveAp.getIpRoutes() != null){
			for(HiveApIpRoute route : hiveAp.getIpRoutes()){
				if("255.255.255.255".equals(route.getNetmask())){
					ipHostList.add(route.getSourceIp() + " gateway " + route.getGateway());
				} else {
					ipNetList.add(CLICommonFunc.countIpAndMask(route.getSourceIp(), route.getNetmask()) + " " + route.getNetmask() + " gateway " + route.getGateway());
				}
			}
		}
	}
	
	public String getIpNetName(int index){
		return ipNetList.get(index);
	}
	
	public int getIpNetSize(){
		return ipNetList.size();
	}
	
	public String getIpHostName(int index){
		return ipHostList.get(index);
	}
	
	public int getIpHostSize(){
		return ipHostList.size();
	}
	
	public boolean isConfigIGMP() {
		return false;
	}

	public String getHiveApGuiName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHiveApName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConfigPathAndTcpMss() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isIpPathMtuDiscoveryEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isIpTcpMssThresholdEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isConfigThresholdSize() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getThresholdSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isConfigL3VpnThresholdSize() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getL3VpnThresholdSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnableIgmpSnooping() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnableImmediateLeave() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnableReportSuppression() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getGlobalDelayLeaveQueryInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getGlobalDelayLeaveQueryCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getGlobalRouterPortAginTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getGlobalRobustnessCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getIgmpPolicySize() {
		return 0;
	}
	
	public boolean getIgmpPolicyEnableSnooping(int index) {
    	return false;
    }
    
    public boolean getIgmpPolicyEnableImmediateLeave(int index) {
    	return false;
    }
    
    public int getIgmpPolicyDelayLeaveQueryCount(int index) {
    	return 0;
    }
    
    public int getIgmpPolicyDelayLeaveQueryInterval(int index) {
    	return 0;
    }
    
    public int getIgmpPolicyRobustnessCount(int index) {
    	return 0;
    }
    
    public int getIgmpPolicyRouterPortAginTime(int index) {
    	return 0;
    }
    
    public int getIgmpPolicyVlanId(int index) {
    	return 0;
    }

    public int getIgmpMulticastGroupSize() {
    	return 0;
    }
    
    public String getMulticastGroupValue(int index) {
    	return null;
    }

	@Override
	public boolean isConfigNatPolicy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNatPolicySize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNatPolicyName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNatPolicyConfigMatch(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNatPolicyConfigVirtualHost(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNatPolicyVhostInsideHostValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyMatchInsideValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyMatchOutsideValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostInsidePortValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostOutsidePortValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostProtocolValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
